# Technical Appraisal Tool

## Description 

The PERICLES Technical Appraisal Tool is aimed at conservators of time-based media collections and science data managers who are responsible for maintaining the long term reusability of complex digital objects. The appraisal tool was conceived as a web-based application. It was developed by King's College London as part of the EU FP7  [PERICLES project](http://www.pericles-project.eu/) 

The appraisal tool:
1. Captures and analyses time-based activity data extracted from external data sources (e.g. search engines, software repositorues, Wkipedia) to predict component obsolscence or failure.
2. Applies a dependency model to analyse the potential impact of such changes on the object as a whole.
3. Determines recovery options of minimal cost which can prolong the reuse of the object.

The tool is based on a Java 8 framework around Apache Tomcat. Analytical components are written in Python 2 and R. The models are represented as OWL ontologies and stored either in Fuseki triple store or the PERICLES Entity Registry Model Repository (ERMR).

The tool can be accessed using any up-to-date web browser. It has been tested and developed using Chrome.

## Demonstration video

This video provides an introduction to the functionality and usage of the technical appraisal tool. 

[![Youtube video](https://raw.githubusercontent.com/pericles-project/TechnicalAppraisalTool/master/youtubevideo.png)](https://www.youtube.com/watch?v=pd7zWr7KYJw) 

## Screenshot

The tool has two demonstrators for media and science. The screenshot shows the Collection View for media.

[![Screenshot](https://raw.githubusercontent.com/pericles-project/TechnicalAppraisalTool/master/screenshot.png)](https://raw.githubusercontent.com/pericles-project/TechnicalAppraisalTool/master/screenshot.png)

## Installation

The main tool comprises five projects, which are stored in separate folders.
* appraisaltool/
* appraisaltoolws/
* pericles-harvester/
* pericles-analysis/
* pericles-predictor/

There is a separate README.md file describing the installation of each component. They should be installed in the sequence listed above.

An additional tool in pericles-ibuilder/ populates the media and science domain models and returns a corresponding .ttl file required for the appraisal tool. Sample ontologies are provided, so this tool is only required if changes are made to the domain ontologies.

## Acknowledgement

This work was supported by the European Commission Seventh Framework Programme under Grant Agreement Number FP7-601138 PERICLES. 
