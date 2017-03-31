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
    path = 'ScienceModel/'
 
    # The entity types - no duplicate names allowed!
    entityList = ['Document Viewer', 'Document Format', 'Image Viewer', 'Image Format', 'Scripting Language', 'Database'] 
                        
    # Labels for the entity instances
    entityIds = ['DV1', 'DF1','IV1', 'IF1', 'SL1', 'DB1'] 

    # Describe type of each entity
    entityIdtoType = {'DB1':'Database', 'SL1':'Scripting Language','IF1':'Image Format', 'IV1':'Image Viewer', 'DF1':'Document Format', 'DV1':'Document Viewer'} 
 
    # Mapping from entityType to DVA ontology type
    entityTypetoDVA = {'Database':'exp:Database', 'Scripting Language':'exp:ScriptingLanguage', 'Image Format':'exp:ImageFormat', 'Image Viewer':'exp:ImageViewer', 'Document Format':'exp:DocumentFormat', 'Document Viewer':'exp:DocumentViewer'} 
                     
    # Dependency model - indices refer to entityIds
    dependencyModel = [[0,1,0,0,0,0], [1,0,0,0,1,0], [0,0,0,1,0,0], [0,0,1,0,1,0], [0,1,0,1,0,1], [0,0,0,0,1,0]] 

    # Relative weights for cost function
    relWeighting = {'Database': 1.0, 'Scripting Language':1.0, 'Image Format':1.0, 'Image Viewer':1.0, 'Document Format':1.0, 'Document Viewer':1.0 } 
  
    # The initial configuration of the model 
    initialState = {'DB1':'', 'SL1':'', 'IF1':'', 'IV1':'', 'DF1':'', 'DV1':''}


#
# Initialise class 
#
    def __init__(self):
        a=1
