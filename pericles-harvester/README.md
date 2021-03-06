# README #

The harvester pulls activity data from four different sources and stores them in databases. 
The entities are stored in separate tables, in the form (date, frequency), with date in the form YYYYMMDD.

It then applies a cleaning algorithm to filter the raw data and remove any data prior to significant local minima.

The output databases are:
* Google Trends - googletrends.db
* Wikipedia - wikitimeseries.db
* SourceForge - sourceforgedownloadstats.db
* Github - gitcommittimeseries.db

To use the Google harvester, a Google email and password are required to be entered in googletrends.py (lines 40 and 45 respectively).
These currently have default values.

There is a flag isPlotGraphs in main.py. This can be set to True to plot graphs of the raw and filtered data or False not to. 
If True is selected, a folder named Graphs needs to be created i.e. "mkdir Graphs" in the pericles-harvester/ folder. The default setting is False.

The program requires python2.7 to be installed. Then install the python modules:

> pip install bs4

> pip install mechanize 

> pip install rson

> pip install simplejson

> pip install matplotlib

> pip install scipy

> pip install numpy

To run from the command line:

> python2.7 main.py URL dataset dva/exp

#### Command line arguments
* argv[1] = base URL of Fuseki: e.g. http://localhost:3030
* argv[2] = Dataset name in Fuseki e.g. ds
* argv[3] = dva or exp (for media or science demos respectively)

Example: python2.7 main.py http://localhost:3030 ds dva

If using the harvester as part of the appraisal tool, the execution will be performed automatically. The comand line is only required if the component is used independently or integrated into another tool.





