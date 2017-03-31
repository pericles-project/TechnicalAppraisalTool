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

class Entities(FileConfig, Emodel, Utilities):
    """ Describes entities in model
    """
#
# Initialise class 
#
    def __init__(self):
        self.entityTable = {} # Dict of entity names and alternatives
        self.numEntities = {} # Contains number of entities of each type
        entityFile = {}
        entityReader = {}

        U = Utilities()
        self.E = Emodel()
        Fc = FileConfig()

        for etype, fname in Fc.entityFilenames.iteritems():
            result = {}
            n = self.E.entityList.index(etype)
    
            # Open the input CSV files and the output JSON file
            entityFile[n] = open(self.E.path + fname +'.csv', 'r')

            # Get the first line of files to get the headings
            readerFirstLine = csv.reader(entityFile[n])
            entityRow1 = readerFirstLine.next()

            # Read the remainder of the file using the file line headers
            entityReader[n] = csv.DictReader(entityFile[n], entityRow1)

            # Extract row headings and remove first column
            for row in entityReader[n]:
                header = row[entityRow1[0]]
                del row[entityRow1[0]]
                result[header] = U.stripDict(row)

            self.entityTable[n] = result
            self.numEntities[etype] = len(self.entityTable[n].keys())
 
    #### Get number of entities of a given entity type (see E.EntityList for types)
    #
    def getNumEntities(self, eType):
        elistIndex = self.E.entityList.index(eType)    
        return len(self.entityTable[elistIndex].keys()) 

    #### Get dict of entities of a given entity type (see E.EntityList for types)
    #
    def getEntityTable(self, eType):
        n = self.E.entityList.index(eType)
        return self.entityTable[n]  

    #### Get first entity
    def getFirstEntity(self, eType):
        n = self.E.entityList.index(eType)
        return self.entityTable[n].keys()[0]              



