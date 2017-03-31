Pericles Technical Appraisal Tool - A web based application.

## Getting started

#### Prerequisite
* Tomcat 8 http://tomcat.apache.org/ (Note: The web pages might not displayed correctly in Tomcat 8.5.xx)
* Java 8 http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
* Fuseki 1 or 2 https://jena.apache.org/download/#jena-fuseki
* Python 2.7 https://www.python.org/downloads/
* R 3 https://www.r-project.org/
* MediaInfo https://mediaarea.net/en/MediaInfo
* Maven 3 https://maven.apache.org/

#### Upload appraisaltool (web application) to the server
1. Upload all files in “appraisaltool” to the server.
1. Make sure the “appraisaltool” is placed in Tomcat’s “webapps” folder.

#### Build appraisaltoolws.war from source
1. Use maven to produce .war file from source.

#### Upload appraisaltoolws.war (web service) to server
1. Upload the file “appraisaltoolws.war” to the server.
1. Make sure the “appraisaltoolws.war” is placed in Tomcat’s “webapps” folder.
 
#### Upload pericles-harvester to server
1. Upload all files in “pericles-harvester” to the server.
1. Make sure everybody has the right to execute the Python code and has the right to write the output file(s) if there is any.

#### Upload pericles-predictor to server
1. Upload all files in “pericles-predictor” to the server.
1. Make sure everybody has the right to execute the R code and has the right to write the output file(s) if there is any.

#### Upload pericles-analysis to server
1. Upload all files in “pericles-analysis” to the server.

#### Start/Restart Tomcat server
1. Start/Restart tomcat server.

#### Configuration of the web service
1. Change to the folder which contains the configuration file “config.properties” of the web service – “appraisaltoolws/WEB-INF/classes”, e.g. cd /opt/tomcat8/webapps/appraisaltoolws/WEB-INF/classes.
1. Open the “config.properties” file and edit where necessary.
1. Save and exit.
1. Restart Tomcat server if any configuration is changed.

#### Start/Restart Fuseki server
1. Change to the Fuseki’s root folder, e.g. cd /srv/fuseki/apache-jena-fuseki-2.4.0
1. Kill Fuseki’s process if it is already running, e.g. kill -9 3172
1. Start Fuseki server, e.g. sudo ./fuseki-server --update --mem /ds

#### Upload TTL files
1. Open a web browser and access Fuseki’s UI, e.g. http://localhost:3030
1. Select and upload ttl files (e.g. “test_dva.ttl” and “test_exp.ttl”) from “upload files” tab.

#### Test if everything is working
1. Open a web browser and access Appraisal tool’s UI, e.g. http://localhost:8080/appraisaltool/pages/index.html
1. Browse pages and try links and buttons to see if there is any problem.
1. Tomcat log file: {Tomcat root folder}/logs/catalina.out, e.g. /opt/tomcat8/logs/catalina.out

