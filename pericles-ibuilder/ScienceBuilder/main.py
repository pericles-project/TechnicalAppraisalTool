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


import json
import sys
import csv
from uuid import UUID
from emodel import Emodel
from fileconfig import FileConfig
from entities import Entities
from dependencies import Dependencies
from costs import Costs
from shutil import copyfile


#
#### Check filenames and extensions are correct 
#
def checkFileExtn(fname, fextn):
    fsplit = fname.split(".")
    if len(fsplit) != 2:
        print "Incorrect filename: ", fname
        return False
    else:
        if fsplit[1] != fextn:
            print "Wrong file extension: ", fsplit[1], fextn
            return False
        else:
            return True


#
#### Create entity instances
#
# entList: File object where instance definitions are stored (.csv)
# f: file object where output is to be written (.ttl)
#
def createEntityInstances(E, Ent, f):
    for entityType in E.entityList:
        entityTable = Ent.getEntityTable(entityType)
        entityid = UUID('{12345678-1234-5678-1234-567812345678}')
        header = '#### Instances for ' + entityType + ' \n\n'
        f.write(header)
        for entity,refs in entityTable.items():
            entityid = entity.replace(' ', '_')
            line = 'exp:' + str(entityid) + ' rdf:type owl:NamedIndividual , \n'
            line = line + '\t'+ E.entityTypetoDVA[entityType] + ' ; \n'
            line = line + '  exp:hasGoogleName "' + refs['Google Trends'] + '" ; \n' 
            line = line + '  exp:hasWikipediaName "' + refs['Wikipedia'] + '" ; \n' 
            
            if 'Github' in refs.keys():
                line = line + '  exp:hasGithubName "' + refs['Github'] + '" ; \n'
            else:
                line = line + '  exp:hasGithubName ' + '"" ; \n'

            if 'SourceForge' in refs.keys():
                line = line + '  exp:hasSourceForgeName "' + refs['SourceForge'] + '" ; \n'
            else:
                line = line + '  exp:hasSourceForgeName ' + '"" ; \n'

            if 'StackExchange' in refs.keys():
                line = line + '  exp:hasStackExchangeName "' + refs['StackExchange'] + '" ; \n'
            else:
                line = line + '  exp:hasStackExchangeName ' + '"" ; \n'

            line = line + '  rdfs:label "' + entity + '" .\n\n'  
            f.write(line)

#
#### Create dependency instances
#
# : File object where instance definitions are stored (.csv)
# f: file object where output is to be written (.ttl)
#
def createDependencyInstances(E, Fc, Dep, f):
    idCount = 0
    OmitZeroDeps = True # Determines whether or not to omit dependency triples with zero weight

    for n, name in enumerate(Fc.depNames):
        depTable = {}
        depTable = Dep.getDepTable(n)
        iPair = Fc.depIndices[n]
        entityType0 = E.entityIdtoType[E.entityIds[iPair[0]]]
        entityType1 = E.entityIdtoType[E.entityIds[iPair[1]]]
        depType = Fc.depTypes[n]
        
        header = '#### Instances for dependency of ' + entityType0 + ' on ' + entityType1 + '\n\n'
        f.write(header)

        for r, row in depTable.iteritems():
            for c, col in row.iteritems():
                entity0 = r.replace(' ', '_')
                entity1 = c.replace(' ', '_')
                weight = col
                if not(OmitZeroDeps == True and weight == 0):
                    depId = depType + '_' + str(idCount)
     
                    line = 'exp:' + str(depId) + ' rdf:type owl:NamedIndividual , \n'
                    line = line + '\t\t LRM:DisjunctiveDependency , \n'
                
                    if depType == 'software_dependency':
                        line = line + '\t\t exp:SoftwareDependency ; \n'
                    elif depType == 'hardware_dependency':
                        line = line + '\t\t exp:HardwareDependency ; \n'
                
                    line = line + '\t exp:hasWeight "' + str(weight) + '"^^xsd:decimal ; \n' 
                    line = line + '\t LRM:from exp:' + entity0 + ' ; \n' 

                    if depType == 'software_dependency':    
                        line = line + '\t LRM:intention exp:intention_1 ; \n'
                    elif depType == 'hardware_dependency':
                        line = line + '\t LRM:intention exp:intention_2 ; \n'

                    line = line + '\t LRM:specification exp:specification_' + str(idCount) + ' ; \n'
                    line = line + '\t LRM:to exp:' + entity1 + ' . \n\n' 

                    f.write(line)
                    idCount = idCount + 1 
                     
        ## Specification instances
        header = '#### Instances for specification of dependency of ' + entityType0 + ' on ' + entityType1 + ' \n\n'
        f.write(header)       
        line = 'exp:specification_' + str(n) + ' rdf:type owl:NamedIndividual , \n'
        line = line + '\t\t LRM:Specification ; \n '
        line = line + '\t exp:value "Describes the dependency ' + entityType0 + ' on ' + entityType1 + '". \n\n'
        f.write(line) 


#
#### Create costs instances
#
# : File object where instance definitions are stored (.csv)
# f: file object where output is to be written (.ttl)
#
def createCostInstances(E, C, f):

    cidCount = 0
    
    for e, eType in enumerate(E.entityList):
        header = '#### Instances for transformation costs for instances of type ' + eType + ' \n\n'
        f.write(header) 
        weightTable = {}
        weightTable = C.getWeightTableAll(eType)
        
        for r, row in weightTable.iteritems():
            for c, col in row.iteritems():
                 # Omit transformations from entities to themselves - omit this for the moment
#                 if r != c:
                if True:    
                    entity0 = r.replace(' ', '_')
                    entity1 = c.replace(' ', '_')
                    weight = col
                
                    line = 'exp:transformation' + '_' + str(cidCount) + ' rdf:type owl:NamedIndividual , \n'
                    line = line + '\t\t exp:Transformation ; \n'
                    line = line + '\t LRM:from exp:' + entity0 + ' ; \n'
                    line = line + '\t LRM:to exp:' + entity1 + ' ; \n'
                    line = line + '\t exp:hasCost "' + str(weight) + '"^^xsd:decimal ; \n' 
                    line = line + '\t LRM:intention exp:intention_3 ; \n'
                    line = line + '\t LRM:specification exp:specification_trans_' + str(e) + ' . \n\n'   
         
                    f.write(line) 
                    cidCount = cidCount + 1

        #### Specification instances
        header = '#### Instance for specification of transformation between instances of type ' + eType + ' \n\n'
        f.write(header)       
        line = 'exp:specification_trans_' + str(e) + ' rdf:type owl:NamedIndividual , \n'
        line = line + '\t\t LRM:Specification ; \n '
        line = line + '\t exp:value "Describes the transformation for entity type ' + eType + '". \n\n'
        f.write(line)

#
#### Create object instances
#
# objectFile: File object where instance definitions are stored (.csv)
# f: file object where output is to be written (.ttl)
#
def createObjectInstances(objFile, f):
    row1 = {}
    iTable = {}

    # Get the first line of files to get the headings
    readerFirstLine = csv.reader(objFile)
    row1 = readerFirstLine.next()

    # Read the remainder of the file using the file line headers
    instanceReader = csv.DictReader(objFile, row1)

    # Extract row headings then data rows
    for row in instanceReader:
        for col in row: 
            iTable[col] = row[col]
#            print iTable['CollectionId']

        #### Write out instances

        # Artwork collection
        line = 'exp:' + str(iTable['CollectionId']) + ' rdf:type exp:ExperimentCollection ; \n'
        line = line + '   rdfs:label "' + str(iTable['CollectionLabel']) + '" . \n\n'
        f.write(line)

        # New artwork
        line = 'exp:' + str(iTable['ExperimentId']) + ' rdf:type owl:NamedIndividual , \n'
        line = line + '        LRM:AggregatedResource , \n'
        line = line + '        exp:Experiment ; \n'
        line = line + '   LRM:hasPart exp:' + str(iTable['DatabaseId']) + ', \n'
        line = line + '            exp:' + str(iTable['ScriptingLanguageId']) + ', \n' 
        line = line + '            exp:' + str(iTable['ImageFormatId']) + ', \n' 
        line = line + '            exp:' + str(iTable['ImageViewerId']) + ', \n'
        line = line + '            exp:' + str(iTable['DocumentFormatId']) + ', \n'
        line = line + '            exp:' + str(iTable['DocumentViewerId']) + '; \n'
        line = line + '   exp:isMemberOf exp:' + str(iTable['CollectionId']) + '; \n'
        line = line + '   rdfs:label "' + str(iTable['ExperimentLabel']) + '"; \n'
        line = line + '   exp:hasDateForAction "' + str(iTable['ExperimentDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   exp:hasImpact "' + str(iTable['ExperimentImpact']) + '" ; \n'
        line = line + '   exp:hasAccessionDate "' + str(iTable['AccessionDate']) + '"^^xsd:date ; \n'
        line = line + '   exp:hasRiskLevel "' + str(iTable['ExperimentRiskLevel']) + '" ; \n'
        line = line + '   exp:hasConfidence "' + str(iTable['ExperimentConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   exp:hasPrimaryRiskType "' + str(iTable['ExperimentPrimaryRiskType']) + '" . \n\n'
        f.write(line)

        # Database
        line = 'exp:' + str(iTable['DatabaseId']) + ' exp:hasDateForAction "' 
        line = line + str(iTable['DBDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   exp:hasConfidence "' + str(iTable['DBConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   exp:hasRiskType "' + str(iTable['DBRiskType']) + '" . \n\n'
        f.write(line)

        # Scripting Language
        line = 'exp:' + str(iTable['ScriptingLanguageId']) + ' exp:hasDateForAction "' 
        line = line + str(iTable['SLDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   exp:hasConfidence "' + str(iTable['SLConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   exp:hasRiskType "' + str(iTable['SLRiskType']) + '" . \n\n'
        f.write(line)

        # Image Format
        line = 'exp:' + str(iTable['ImageFormatId']) + ' exp:hasDateForAction "' 
        line = line + str(iTable['IFDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   exp:hasConfidence "' + str(iTable['IFConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   exp:hasRiskType "' + str(iTable['IFRiskType']) + '" . \n\n'
        f.write(line)

        # Image Viewer
        line = 'exp:' + str(iTable['ImageViewerId']) + ' exp:hasDateForAction "' 
        line = line + str(iTable['IVDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   exp:hasConfidence "' + str(iTable['IVConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   exp:hasRiskType "' + str(iTable['IVRiskType']) + '" . \n\n'
        f.write(line)

        # Document Format
        line = 'exp:' + str(iTable['DocumentFormatId']) + ' exp:hasDateForAction "' 
        line = line + str(iTable['DFDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   exp:hasConfidence "' + str(iTable['DFConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   exp:hasRiskType "' + str(iTable['DFRiskType']) + '" . \n\n'
        f.write(line)

        # Document Viewer
        line = 'exp:' + str(iTable['DocumentViewerId']) + ' exp:hasDateForAction "' 
        line = line + str(iTable['DVDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   exp:hasConfidence "' + str(iTable['DVConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   exp:hasRiskType "' + str(iTable['DVRiskType']) + '" . \n\n'
        f.write(line)

        # Experiment recovery options
        line =  'exp:' + str(iTable['ExperimentId']) + ' exp:hasRecoveryOption exp:' + str(iTable['RecoveryOptionId1']) + ' , \n'
        line = line + '        exp:' + str(iTable['RecoveryOptionId2']) + ' . \n\n'
        f.write(line)

        # Recovery Option 1
        line = 'exp:' + str(iTable['RecoveryOptionId1']) + ' exp:hasChange exp:' + str(iTable['RO1_Change1']) + ' , \n'
        line = line + '            exp:' + str(iTable['RO1_Change2']) + ' ; \n'
        line = line + '    exp:hasDateForAction "' + str(iTable['RO1_DateForAction']) + '"^^xsd:date ; \n'
        line = line + '    exp:hasImpact "' + str(iTable['RO1_Impact']) + '" . \n\n'
        f.write(line)

        # Recovery Option 2
        line = 'exp:' + str(iTable['RecoveryOptionId2']) + ' exp:hasChange exp:' + str(iTable['RO2_Change1']) + ' , \n'
        line = line + '            exp:' + str(iTable['RO2_Change2']) + ' ; \n'
        line = line + '    exp:hasDateForAction "' + str(iTable['RO2_DateForAction']) + '"^^xsd:date ; \n'
        line = line + '    exp:hasImpact "' + str(iTable['RO2_Impact']) + '" . \n\n'
        f.write(line)

        # Recovery Option 1: Changes 1 and 2
        line = 'exp:' + str(iTable['RO1_Change1']) + ' exp:hasChangeType exp:' + str(iTable['RO1_Change1_Type']) + ' ; \n'
        line = line +  '     exp:hasChangeValue exp:' + str(iTable['RO1_Change1_Value']) + ' . \n\n'
        line = line + 'exp:' + str(iTable['RO1_Change2']) + ' exp:hasChangeType exp:' + str(iTable['RO1_Change2_Type']) + ' ; \n'
        line = line +  '     exp:hasChangeValue exp:' + str(iTable['RO1_Change2_Value']) + ' . \n\n'
        f.write(line)

        # Recovery Option 2: Changes 1 and 2
        line = 'exp:' + str(iTable['RO2_Change1']) + ' exp:hasChangeType exp:' + str(iTable['RO2_Change1_Type']) + ' ; \n'
        line = line +  '     exp:hasChangeValue exp:' + str(iTable['RO1_Change1_Value']) + ' . \n\n'
        line = line + 'exp:' + str(iTable['RO2_Change2']) + ' exp:hasChangeType exp:' + str(iTable['RO2_Change2_Type']) + ' ; \n'
        line = line +  '     exp:hasChangeValue exp:' + str(iTable['RO2_Change2_Value']) + ' . \n\n'
        f.write(line)

#
#### Main program
#
def main():
    ######## Initialisation

    # Initialise the ecosystem model static parameters 
    E = Emodel()

    # Initialise the background data file parameters
    Fc = FileConfig()

    # Initialise the table of entity names indexed by entity type
    Ent = Entities()

    # Initialise the table of dependencies between pairs of entities
    Dep = Dependencies()

    # Initialise table of costs
    C = Costs()

    # Copy class and property definitions into new file
#    copyfile('dva_defs_v3.ttl', 'test2.ttl')    

    # Open file to append new data
#    f = open('test2.ttl', 'a')
#    f = open('test4.ttl', 'w')

    baseOntology =    raw_input("Enter base ontology .ttl filename (leave blank to omit):    ")
    includeModel =    raw_input("Include entities, dependencies and costs (y/n)?:            ")
    objectInstances = raw_input("Enter object instances .csv filename (leave blank to omit): ") 
    outFilename =     raw_input("Enter output .ttl filename:                                 ")
 
    # File extension checking
    if baseOntology != "" and checkFileExtn(baseOntology, "ttl") == False:
        sys.exit()
    if includeModel != "y" and includeModel != "n":
        print "Please enter y or n"
        sys.exit()
    if objectInstances != "" and checkFileExtn(objectInstances, "csv") == False:
        sys.exit()
    if outFilename != "" and checkFileExtn(outFilename, "ttl") == False:
        sys.exit()

    # Open files for read, write, append 
    if baseOntology != "":
        try: 
            copyfile(baseOntology, outFilename)
        except IOError:
            print('Cannot copy ', baseOntology)
            sys.exit()
        print "* Written base ontology"

        if objectInstances != "" or includeModel == "y":
            try: 
                outFile = open(outFilename, "a")
            except IOError:
                print('Cannot open ', outFilename)
                sys.exit()

        if objectInstances != "":
            try:
                objectFile = open(objectInstances, 'r')
            except IOError:
                print('Cannot open ', objectInstances)
                sys.exit()     
    else:
        if includeModel == "y" or objectInstances != "":
            try:
                outFile = open(outFilename, "w")
            except IOError:
                print('Cannot open ', outFilename)
                sys.exit()

        if objectInstances != "":
            try:
                objectFile = open(objectInstances, 'r')
            except IOError:
                print('Cannot open ', objectInstances)
                sys.exit()
          

    # Create model instances
    if includeModel == "y":
        createEntityInstances(E, Ent, outFile)
        createDependencyInstances(E, Fc, Dep, outFile)
        createCostInstances(E, C, outFile)
        print "* Written model instances"

    # Create object instances
    if objectInstances != "":
        createObjectInstances(objectFile, outFile)
        print "* Written object instances"
        
    # Close the output file 
    if includeModel == "y" or objectInstances != "":                
        outFile.close()


if __name__ == "__main__":
    main()

