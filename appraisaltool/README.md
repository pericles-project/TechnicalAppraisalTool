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
2. Make sure the “appraisaltool” is placed in Tomcat’s “webapps” folder.

#### Build appraisaltoolws.war from source
1. Use maven to produce .war file from source. A prebuilt file appraisaltoolws.war is provided. If this is used, this step can be omitted

#### Upload appraisaltoolws.war (web service) to server
1. Upload the file “appraisaltoolws.war” to the server.
2. Make sure the “appraisaltoolws.war” is placed in Tomcat’s “webapps” folder.
 
#### Upload pericles-harvester to server
1. Create a folder called pericles-harvester/ and upload all files in “pericles-harvester” from Github.
2. Make sure all users have the right to execute processes and write to the folder pericles-harvester/.

#### Upload pericles-predictor to server
1. Create a folder called pericles-predictor and upload all files in “pericles-predictor” from Github.
2. Make sure all users have the right to execute processes and write to the folder pericles-predictor/.

#### Upload pericles-analysis to server
1. Create a folder called pericles-analysis/ and upload all files in “pericles-analysis” from Github.

#### Start/Restart Tomcat server
1. Start/Restart tomcat server.

#### Configuration of the web service
1. Change to the folder which contains the configuration file “config.properties” of the web service – “appraisaltoolws/WEB-INF/classes”, e.g. cd /opt/tomcat8/webapps/appraisaltoolws/WEB-INF/classes.
2. Open the “config.properties” file and edit where necessary.
3. Save and exit.
4. Restart Tomcat server if any configuration is changed.

#### Start/Restart Fuseki server
1. Change to the Fuseki’s root folder, e.g. cd /srv/fuseki/apache-jena-fuseki-2.4.0
2. Kill Fuseki’s process if it is already running, e.g. kill -9 3172
3. Start Fuseki server, e.g. sudo ./fuseki-server --update --mem /ds

#### Upload TTL files
1. Open a web browser and access Fuseki’s UI, e.g. http://localhost:3030
2. Select and upload ttl files (e.g. “test_dva.ttl” and “test_exp.ttl”) from “upload files” tab.

#### Test if everything is working
1. Open a web browser and access the Appraisal tool’s UI, e.g. http://localhost:8080/appraisaltool/pages/index.html
2. Browse pages and try links and buttons to see if there is any problem.
3. Tomcat log file: {Tomcat root folder}/logs/catalina.out, e.g. /opt/tomcat8/logs/catalina.out

