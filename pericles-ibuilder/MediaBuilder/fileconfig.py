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

class FileConfig(Emodel):
    """ Configure the names of the files containing information about entities and dependencies for the background model
    """
    # Names of the files containing the definitions for each entity type
    entityFilenames = {'Video Codec':'VCodecs', 'Container':'VContainers', 'Video Player':'VPlayers', 'Operating System':'OS', 'PC':'PC'}

    # Names of the files where the dependencies are stored
    depNames = ["VCodecToVPlayer", "VContainerToVPlayer", "VPlayerToOS", "OSToPC"]

    # Type of the dependencies
    depTypes = ['software_dependency', 'software_dependency', 'software_dependency', 'hardware_dependency']

    # Gives the indices of the entity pairs in depNames. Indices with reference to entityIds (changed from EntityList)
    depIndices = [(0,2), (1,2), (2,3), (3,4)] 

    # Names of the files containing the weights for each entity type (could be recast as a dict)
    weightNames = ["WeightVCodec", "WeightVContainer", "WeightVPlayer", "WeightOS","WeightPC"]

    # Analysis file name
    analysisFname = "Analysis"

#
# Initialise class 
#
    def __init__(self):
        a=1
