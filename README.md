# Image Bank

* Browsing taxon images by categories, such as life stage, habitat.
* Crowdsourcing tools for taxon image curation: media selection and categorization/labeling.  
* Admin Management of taxon images for admins, taxon experts.

## Dev environment

1. Set up local tomcat
2. Clone git repo to <catalina.base>/webapps/lajitietokeskus
3. Add project to IDE
4. Set class output folder to /WEB-INF/classes
5. Include <catalina.base>/lib/servlet-api.jar to build path
6. Add jars in projects /lib folder to build path 
7. Follow deployment instructions 1-2

## Deployment

1. Place imagebank.properties config file to <catalina.base>/app-conf/
2. Place ojdbc8.jar to <catalina.base>/lib
3. Deploy imagebank.war using Tomcat manager

#### imagebank.properties example
~~~ 
SystemID = imagebank
SystemQname = KE.1621
LajiAuthURL = https://fmnh-ws-test-24.it.helsinki.fi/laji-auth

DevelopmentMode = YES
#StagingMode = NO
#ProductionMode = NO

BaseURL = //localhost:8081/imagebank
StaticURL = //localhost:8081/imagebank/static

#All other folders are relative to basefolder
BaseFolder = <tomcat base path>

LanguageFileFolder = /webapps/imagebank/locale
TemplateFolder     = /webapps/imagebank/template
LogFolder          = /application-logs/imagebank
StorageFolder      = /application-out/imagebank

LanguageFiles = locale
SupportedLanguages = fi,en,sv

ErrorReporting_SMTP_Host = localhost
ErrorReporting_SMTP_Username = 
ErrorReporting_SMTP_Password = 
ErrorReporting_SMTP_SendTo = 
ErrorReporting_SMTP_SendFrom = 
ErrorReporting_SMTP_Subject = 

#Imagebank internal database
DBdriver = oracle.jdbc.OracleDriver
DBurl = jdbc:oracle:thin:@//xxx:1521/xxx
DBusername = imagebank_tests|dev|production
DBpassword = 

#Taxonomy database (triplestore)
Taxonomy_DBdriver = oracle.jdbc.OracleDriver
Taxonomy_DBurl = jdbc:oracle:thin:@//xxx:1521/xxx
Taxonomy_DBusername = <taxon readonly user>
Taxonomy_DBpassword = 

#Production triplestore (for reading taxa schema related things)
TriplestoreURL = https://...
TriplestoreUsername = 
TriplestorePassword = 

#MediaAPI HTTP Client
MediaAPI_DBdriver = driver-not-used-but-some-value-required-here
MediaAPI_DBurl = https://...
MediaAPI_DBusername = 
MediaAPI_DBpassword = 

DwURL = https://dw.laji.fi/taxon-obs-count
~~~ 
