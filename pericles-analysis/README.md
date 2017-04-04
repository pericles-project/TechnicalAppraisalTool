The program requires Python 2.7 to be installed.

Install the following Python modules:

> pip install requests

> pip install nose

> pip install numpy 

Start Fuseki and upload the Turtle file test_dva.ttl for media or test_exp.ttl for science.
This can be done through the Fuseki web interface. You will need to enter a name for the Fuseki data set e.g. ds.

To execute enter
> python2.7 main.py demo fuseki-URL fuseki-dataset objectId file.csv
where 
* demo = dva for media, exp for science
* fuseki-URL is the URL of the fuseki server
* objectId is the identifier of the artwork/experiment to be analysed
* file.csv is the output file from pericles-predictor/. 

For example

> python2.7 main.py dva http://localhost:3030 ds artwork_1 {path}/googletrends-output.csv 

or

> python2.7 main.py exp http://localhost:3030 ds experiment_1 {path}/googletrends-output.csv


NB. ERMR not currently implemented in command line arguments.

Alternatively, use the ERMR instead of Fuseki, upload the Turtle file test_dva.ttl or test_exp.ttl to the ERMR. Example here: dataset - Test1PericlesAnalysis test_exp.ttl is uploaded to the ERMR as test_exp_PericlesAnalysis

> python2.7 main.py dva https://141.5.100.67 Test1PericlesAnalysis artwork_1 ERMR

or

> python2.7 main.py exp https://141.5.100.67 test_exp_PericlesAnalysis experiment_1 ERMR

NB if you get this error "requests.exceptions.SSLError: [SSL: CERTIFICATE_VERIFY_FAILED] certificate verify failed (_ssl.c:590)"
check that you've remembered the 'ERMR' flag at the end


To run the tests

> cd pericles-analysis

> nosetests
