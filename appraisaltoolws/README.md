Pericles Technical Appraisal Tool Web Services - RESTFul web services which utilised by Pericles Technical Appraisal Tool. These services can also be used for other purpose. 

## Getting started

#### Prerequisite
* Java 8 http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
* Maven 3 https://maven.apache.org/

#### Build appraisaltoolws.war from source
1. Use maven to produce .war file from source.

#### Upload appraisaltoolws.war (web service) to server
1. Upload the file “appraisaltoolws.war” to the server.
1. Make sure the “appraisaltoolws.war” is placed in Tomcat’s “webapps” folder.

#### Configuration of the web services
1. Change to the folder which contains the configuration file “config.properties” of the web service – “appraisaltoolws/WEB-INF/classes”, e.g. cd /opt/tomcat8/webapps/appraisaltoolws/WEB-INF/classes.
1. Open the “config.properties” file and edit where necessary.
1. Save and exit.
1. Restart Tomcat server if any configuration is changed.

#### To get it work with the appraisaltool, please refer to the readme file in appraisaltool folder.   

