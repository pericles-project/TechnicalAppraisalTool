# Technical Appraisal Tool
The PERICLES Technical Appraisal Tool is aimed at conservators of time-based media collections and science data managers who are responsible for maintaining the long term reusability of complex digital objects. The appraisal tool was conceived as a web-based application. 

The appraisal tool:
1. Captures and analyses time-based activity data extracted from external data sources (e.g. search engines, software repositorues, Wkipedia) to predict component obsolscence or failure.
2. Applies a dependency model to analyse the potential impact of such changes on the object as a whole.
3. Determines recovery options of minimal cost which can prolong the reuse of the object.

The tool is based on a Java 8 framework around Apache Tomcat. Analytical components are written in Python 2 and R. The models are represented as OWL ontologies and stored either in Fuseki triple store or the PERICLES Entity Registry Model Repository (ERMR).
