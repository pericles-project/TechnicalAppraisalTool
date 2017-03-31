# Copyright King's College London, 2017. 
# See the NOTICE file distributed with this work for additional information
# regarding copyright ownership.  King's College London licences this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at

#  http://www.apache.org/licenses/LICENSE-2.0

# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# Author(s): Simon Waddington

import csv
from emodel import Emodel
from fileconfig import FileConfig
from utilities import Utilities

class Dependencies(FileConfig, Emodel, Utilities):
    """ Describes dependencies in model
    """
#
# Initialise class 
#
    def __init__(self):
        
        self.depTable = {} # Table of dependencies

        depFile = {}
        depRow1 = {}
        depReader = {}

        U = Utilities()
        E = Emodel()
        Fc = FileConfig()

        for n, name in enumerate(Fc.depNames):
            result = {}

            # Open the input CSV files and the output JSON file
            depFile[n] = open(E.path+name +'.csv', 'r')

            # Get the first line of files to get the headings
            readerFirstLine = csv.reader(depFile[n])
            depRow1[n] = readerFirstLine.next()

            # Read the remainder of the file using the file line headers
            depReader[n] = csv.DictReader(depFile[n], depRow1[n])

            # Extract row headings and remove first column    
            for row in depReader[n]:
                header = row[depRow1[n][0]]
                del row[depRow1[n][0]]
                result[header] = U.stripDict(row)

            self.depTable[n] = result
 
    #### Define functions
    #
    def getDepTable(self, index):
        return self.depTable[index]        



