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

class Utilities():
    """ Utility functions
    """
#
# Initialise class 
#
    def __init__(self):
        a=1
 
    #### Remove spaces from a dict 
    #
    def stripDict(self, d):
        newd = {}
        for key, value in d.iteritems():
            nkey = key.strip()
            nvalue = value.strip()
            newd[nkey] = nvalue
        return newd 

              



