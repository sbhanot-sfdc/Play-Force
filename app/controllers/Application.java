package controllers;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.WS.*;
import play.cache.*;

import java.util.*;

import models.*;
import com.google.gson.*;

public class Application extends Controller {

    private static boolean retry = false;

    public static void index() {
		getAccounts();
		Logger.info("Between");
		render();
    }

    public static void sforceLogin() {
		if (!ForceDotComOAuth2.isLoggedIn())
		{
			/*NOTE: As a security best practice, it is advisable to pass in the
			 * Salesforce OAuth client key and secret as environment variables to your
			 * Play application (e.g. 'export clientKey=<your key>' on a Linux machine).
			 * However if you'd prefer to pass them in as configuration params, you set the
			 * appropriate param values in the application.conf file and then uncomment
			 * the 2 lines of code below.
			 */
			boolean result = ForceDotComOAuth2.login( System.getenv("clientKey"),
													  System.getenv("clientSecret"),	
													  /*Play.configuration.getProperty("sfdc.clientKey"),
													  Play.configuration.getProperty("sfdc.clientSecret"),*/
													  new ForceDotComOAuth2.OAuthListner() {
															@Override
															public void onSuccess(OAuthSession session){							
																		Logger.info("Session in callback is:"+session);
																		index();
															}
															
															@Override
															public void onFailure(String error, String errorDesc){
																renderText("Auth failed"+error);
															}
													  });
									
		}
		index();
    }
	
	public static void sforceLogout() {
		if (ForceDotComOAuth2.isLoggedIn())
		{
			boolean result = ForceDotComOAuth2.logout();
		}
		index();
	}
	
	private static void getAccounts()
	{
		Logger.info("getAccounts called");
		OAuthSession oauth = ForceDotComOAuth2.getOAuthSession();
		if (oauth == null)
			return;
		
		String query = "select name, id, AccountNumber, AnnualRevenue, NumberOfEmployees from Account limit 10";
		WSRequest req = WS.url(oauth.instance_url + "/services/data/v22.0/query/?q=%s", query);
		req.headers.put("Authorization", "OAuth "+oauth.access_token);
		HttpResponse response = req.get();
		
		Logger.info("response code is:"+response.getStatus());
		int res = response.getStatus();
		if (response.getStatus() == 200)
		{
			JsonArray accounts = response.getJson().getAsJsonObject().getAsJsonArray("records");
			
			if (accounts != null && accounts.size() > 0)
			{	
				ArrayList<Account> accts = new ArrayList<Account>();
				for (int i = 0; i < accounts.size(); i++)
				{
					JsonObject a = (JsonObject)accounts.get(i);
					Account acct = new Account();
					acct.parseFromJson(a);
					accts.add(acct);
				}
				renderArgs.put("accounts", accts);
			}	
		}
		else if (response.getStatus() == 401 && retry == false)
		{
			Logger.info("Calling refresh");
			retry = true;
			/*NOTE: As a security best practice, it is advisable to pass in the
			 * Salesforce OAuth client key and secret as environment variables to your
			 * Play application (e.g. 'export clientKey=<your key>' on a Linux machine).
			 * However if you'd prefer to pass them in as configuration params, you set the
			 * appropriate param values in the application.conf file and then uncomment
			 * the 2 lines of code below.
			 */
			ForceDotComOAuth2.refreshToken( "https://login.salesforce.com/services/oauth2/token",
											System.getenv("clientKey"),
											System.getenv("clientSecret"));	
											/*Play.configuration.getProperty("sfdc.TokenURL"),
											Play.configuration.getProperty("sfdc.clientKey"),
											Play.configuration.getProperty("sfdc.clientSecret"));*/
			Logger.info("Refresh done");
			getAccounts();
			Logger.info("getAccounts call done");
		}
	}
}