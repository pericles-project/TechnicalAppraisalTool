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

class Emodel:
    """ Create the initial parameters for the ecosystem model
    """
    # Name of the folder where the data files are stored
    path = 'MediaModel/'
 
    # The entity types - no duplicate names allowed!
    entityList = ['Video Codec', 'Container', 'Video Player', 'Operating System', 'PC'] 
                        
    # Labels for the entity instances
    entityIds = ['VCodec1', 'Cont1','VPlay1', 'OS1', 'PC1'] 

    # Describe type of each entity
    entityIdtoType = {'VCodec1':'Video Codec', 'Cont1':'Container','VPlay1':'Video Player', 'OS1':'Operating System', 'PC1':'PC'} 
 
    # Mapping from entityType to DVA ontology type
    entityTypetoDVA = {'Video Codec':'dva:VideoCodec', 'Container':'dva:Container', 'Video Player':'dva:MediaPlayer', 'Operating System':'dva:OperatingSystem', 'PC':'dva:Computer'} 
                     
    # Dependency model - indices refer to entityIds
    dependencyModel = [[0,0,1,0,0], [0,0,1,0,0], [1,1,0,1,0], [0,0,1,0,1], [0,0,0,1,0]] 

    # Relative weights for cost function
    relWeighting = {'Video Codec': 2.0, 'Container':1.0, 'Video Player':1.0, 'Operating System':1.0, 'PC':1.0} 
  
    # The initial configuration of the model 
    initialState = {'VCodec1':'H.262 (MPEG-2)', 'Cont1':'AVI', 'VPlay1':'DSPlayer', 'OS1':'Windows XP', 'PC1':'P1'}


#
# Initialise class 
#
    def __init__(self):
        a=1
