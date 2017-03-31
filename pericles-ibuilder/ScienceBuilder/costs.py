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

import sys
import csv
from emodel import Emodel
from fileconfig import FileConfig
from utilities import Utilities

class Costs(FileConfig, Emodel, Utilities):
    """ Describes transformation costs in model
    """
#
# Initialise class 
#
    def __init__(self):
        
        self.weightTable = {} # Table of entity transformation costs

        weightFile = {}
        weightReader = {}

        U = Utilities()
        self.E = Emodel()
        Fc = FileConfig()

        for n, name in enumerate(Fc.weightNames):
            result1 = {}
            weightRow1 = {}

            # Open the input CSV files and the output JSON file
            weightFile[n] = open(self.E.path+name +'.csv', 'r')

            # Get the first line of files to get the headings
            readerFirstLine = csv.reader(weightFile[n])
            weightRow1 = readerFirstLine.next()

            # Read the remainder of the file using the file line headers
            weightReader[n] = csv.DictReader(weightFile[n], weightRow1)

            # Extract row headings and remove first column
            for row in weightReader[n]:
                header = row[weightRow1[0]]
                del row[weightRow1[0]]
                result1[header] = row 

            self.weightTable[n] = result1 
 

    #### Get weight table of costs. eType is the entity type e.g. Video Player
    # eState is the current state of that entity e.g. VLC
    #
    def getWeightTableAll(self, eType):
        elistIndex = self.E.entityList.index(eType)
        return self.weightTable[elistIndex] 

    #### Get weight table of costs. eType is the entity type e.g. Video Player
    # eState is the current state of that entity e.g. VLC
    #
    def getWeightTable(self, eType, eState):
        elistIndex = self.E.entityList.index(eType)
        return self.weightTable[elistIndex][eState] 

    #### Get weight table value. eType is the entity type e.g. Video Player
    # eState is the current state of that entity e.g. VLC 
    # fState is the tranisition state
    #
    def getWeightTableValue(self, eType, eState, fState):
        elistIndex = self.E.entityList.index(eType)
        return self.weightTable[elistIndex][eState][fState] 
      

    #### Get weight table keys. eType is the entity type e.g. Video Player
    # eState is the current state of that entity e.g. VLC
    #
    def getWeightTableKeys(self, eType, eState):
        elistIndex = self.E.entityList.index(eType)
        return self.weightTable[elistIndex][eState].keys()


