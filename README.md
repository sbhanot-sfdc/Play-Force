Sample Play application that integrates with Force.com
=================================

This is a simple demo Play application that integrates with Force.com using OAuth 2.0 and the REST API. Additional details about the application can be found here- .

PRE-REQUISITES
--------------

1) Play framework (http://www.playframework.org)

INSTALLATION
--------------
1) After downloading and installing the Play Framework, Https has to be enabled for the Play web server. This is required because Force.com requires the endpoint URL for an OAuth callback to be hosted on https. In order to enable https support in Play, follow the instruction on this page - http://www.playframework.org/documentation/1.1.1/releasenotes-1.1#https. If you plan on generating your own test certificates, you can follow the steps detailed in this blog post to generate your test X509 certificate - http://blogs.developerforce.com/developer-relations/2011/05/generating-valid-self-signed-certificates.html.

2) Next, create a new Remote Access Application in your Force.com Org. For the Callback URL, you'll have to enter https://<<your Play URL>>:9443/forcedotcomoauth2/callback. For e.g. if you're running the Play application in test mode on your local machine, the callback URL would be https://localhost:9443/forcedotcomoauth2/callback. Note the values of the 'Consumer Key' (aka 'Client Key') and 'Consumer Secret' (aka 'Client Secret') that are assigned to the Remote Access Application

3) Set 2 local environment variables - 'clientKey' and 'clientSecret' - to their respective values in the Play runtime environment. For example, on a Linux/Mac machine, you can say 'export clientKey=<value of the Consumer/Client Key>' and 'export clientSecret=<value of the Consumer/Client Secret>'. The Play application looks for these 2 environment variables when the user initiates the OAuth authentication process.

4) The sample Play application supports a 'Remember Me' mode whereby the user's Force.com OAuth session information is persisted across multiple browser sessions so that the user only has to log in to the application once. To enable this option, set the value of the 'sfdc.persistentSession' property in the conf/application.conf file to true.

5) Start the Play application by navigating to the root of the application and running 'play run'. On your browser, nagigate to https://<your Play URL>:9443 (for e.g. https://localhost:9443). Click on the 'Login' link and this should start the OAuth authentication. After logging into your Force.com Org, you should see a list of 10 Account records displayed on the page.

