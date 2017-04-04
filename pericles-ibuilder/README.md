The programs requires Python 2.7 to be installed.

The programs build either the DVA or the EXP ontology and instances required for the appraisal tool.

There are two separate subfolders, MediaBuilder/ to create the DVA ontology and ScienceBuilder/ to build the EXP ontology.
Here the individual CSV files containing entity names, dependencies and transformation costs can be edited.

To run the program for media:

> cd MediaBuilder

> python2.7 main.py 

or

> cd ScienceBuilder

> python2.7 main.py 

for science.

The programs enables the following to be entered on the command line.

1. The base DVA/EXP ontology and definitions - a default turtle file baseOnt-v1.ttl is provided.
2. The entity, dependency and transformation costs - these are built from the CSV files in the subdirectoryies 
   MediaModel/ and ScienceModel/ respectively. Enter y here.
3. Sample instances of object (i.e. artworks or experiments) are given in the files named instances.csv. 

The output is a single Turtle file. Samples are provided - either test-dva.ttl or test_exp.ttl. Thus the pericles-ibuilder tool is only required if the ontologies are changed.

For the appraisal tool, steps 1-2 are required as a minimum, but for testing purposes, it is possible to build the elements separately.

Note - the csv files are sensitive to spaces, so leading and training spaces should be removed if the files are edited.
 
