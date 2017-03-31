Performs Weibull curve fitting and Gaussian curve generation and analysis. 
The input is a database containing tables of (date, frequency) pairs for each entity.
The output is a CSV file, with one row per entity.

isPlotGraphs is a Boolean to determine if graphs are plotted or not - set to TRUE or FALSE in predictor.R. 
For operational use, it should be set to FALSE.

The tool requires R to be installed. We have developed and tested with R version 3.2.3.

The following libraries are required:

> install.packages('DBI')

> install.packages('RSQLite')

> install.packages('fitdistrplus')

> install.packages('MASS')

> install.packages('ggplot2')

> install.packages('stats4')

> install.packages('plyr')

> install.packages('minpack.lm')

To execute the program:

> Rscript predictor.R database.db output.csv

Where database.db contains the tables of (date, frequency) data.

The output file output.csv stores the calculations

Graphs (if enabled) are stored in the folder generatedgraphs/. This should be created and the program should have write access.

Two types of graphs are produced - one is the Weibull curve fitting the raw data and one for the Normal curves.