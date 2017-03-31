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
            line = 'dva:' + str(entityid) + ' rdf:type owl:NamedIndividual , \n'
            line = line + '\t'+ E.entityTypetoDVA[entityType] + ' ; \n'
            line = line + '  dva:hasGoogleName "' + refs['Google Trends'] + '" ; \n' 
            line = line + '  dva:hasWikipediaName "' + refs['Wikipedia'] + '" ; \n' 

            if 'Github' in refs.keys():
                line = line + '  dva:hasGithubName "' + refs['Github'] + '" ; \n'
            else:
                line = line + '  dva:hasGithubName ' + '"" ; \n'

            if 'SourceForge' in refs.keys():
                line = line + '  dva:hasSourceForgeName "' + refs['SourceForge'] + '" ; \n'
            else:
                line = line + '  dva:hasSourceForgeName ' + '"" ; \n'

            if 'StackExchange' in refs.keys():
                line = line + '  dva:hasStackExchangeName "' + refs['StackExchange'] + '" ; \n'
            else:
                line = line + '  dva:hasStackExchangeName ' + '"" ; \n'

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
    OmitZeroDeps = False # Determines whether or not to omit dependency triples with zero weight
                         # True = omit zero dependencies
                         

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
     
                    line = 'dva:' + str(depId) + ' rdf:type owl:NamedIndividual , \n'
                    line = line + '\t\t LRM:DisjunctiveDependency , \n'
                
                    if depType == 'software_dependency':
                        line = line + '\t\t dva:SoftwareDependency ; \n'
                    elif depType == 'hardware_dependency':
                        line = line + '\t\t dva:HardwareDependency ; \n'
                
                    line = line + '\t dva:hasWeight "' + str(weight) + '"^^xsd:decimal ; \n' 
                    line = line + '\t LRM:from dva:' + entity0 + ' ; \n' 

                    if depType == 'software_dependency':    
                        line = line + '\t LRM:intention dva:intention_1 ; \n'
                    elif depType == 'hardware_dependency':
                        line = line + '\t LRM:intention dva:intention_2 ; \n'

                    line = line + '\t LRM:specification dva:specification_' + str(idCount) + ' ; \n'
                    line = line + '\t LRM:to dva:' + entity1 + ' . \n\n' 

                    f.write(line)
                    idCount = idCount + 1 
                     
        ## Specification instances
        header = '#### Instances for specification of dependency of ' + entityType0 + ' on ' + entityType1 + ' \n\n'
        f.write(header)       
        line = 'dva:specification_' + str(n) + ' rdf:type owl:NamedIndividual , \n'
        line = line + '\t\t LRM:Specification ; \n '
        line = line + '\t dva:value "Describes the dependency ' + entityType0 + ' on ' + entityType1 + '". \n\n'
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
                 # Omit transformations from entities to themselves
#                 if r != c:
                 if True:
                    entity0 = r.replace(' ', '_')
                    entity1 = c.replace(' ', '_')
                    weight = col
                
                    line = 'dva:transformation' + '_' + str(cidCount) + ' rdf:type owl:NamedIndividual , \n'
                    line = line + '\t\t dva:Transformation ; \n'
                    line = line + '\t LRM:from dva:' + entity0 + ' ; \n'
                    line = line + '\t LRM:to dva:' + entity1 + ' ; \n'
                    line = line + '\t dva:hasCost "' + str(weight) + '"^^xsd:decimal ; \n' 
                    line = line + '\t LRM:intention dva:intention_3 ; \n'
                    line = line + '\t LRM:specification dva:specification_trans_' + str(e) + ' . \n\n'   
         
                    f.write(line) 
                    cidCount = cidCount + 1

        #### Specification instances
        header = '#### Instance for specification of transformation between instances of type ' + eType + ' \n\n'
        f.write(header)       
        line = 'dva:specification_trans_' + str(e) + ' rdf:type owl:NamedIndividual , \n'
        line = line + '\t\t LRM:Specification ; \n '
        line = line + '\t dva:value "Describes the transformation for entity type ' + eType + '". \n\n'
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
#            print iTable[col]

        #### Write out instances

        # Artwork collection
        line = 'dva:' + str(iTable['CollectionId']) + ' rdf:type dva:DigitalVideoArtworkCollection ; \n'
        line = line + '   rdfs:label "' + str(iTable['CollectionLabel']) + '" . \n\n'
        f.write(line)

        # New artwork
        line = 'dva:' + str(iTable['ArtworkId']) + ' rdf:type owl:NamedIndividual , \n'
        line = line + '        LRM:AggregatedResource , \n'
        line = line + '        dva:DigitalVideoArtwork ; \n'
        line = line + '   LRM:hasPart dva:' + str(iTable['DigitalVideoId']) + ', \n'
        line = line + '            dva:' + str(iTable['MediaPlayerId']) + ', \n' 
        line = line + '            dva:' + str(iTable['OSId']) + ', \n' 
        line = line + '            dva:' + str(iTable['PCId']) + '; \n'
        line = line + '   dva:isMemberOf dva:' + str(iTable['CollectionId']) + '; \n'
        line = line + '   rdfs:label "' + str(iTable['ArtworkLabel']) + '"; \n'
        line = line + '   dva:hasDateForAction "' + str(iTable['ArtworkDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   dva:hasImpact "' + str(iTable['ArtworkImpact']) + '" ; \n'
        line = line + '   dva:hasAccessionDate "' + str(iTable['AccessionDate']) + '"^^xsd:date ; \n'
        line = line + '   dva:hasRiskLevel "' + str(iTable['ArtworkRiskLevel']) + '" ; \n'
        line = line + '   dva:hasConfidence "' + str(iTable['ArtworkConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   dva:hasPrimaryRiskType "' + str(iTable['ArtworkPrimaryRiskType']) + '" . \n\n'
        f.write(line)

        # Digital video 
        line =  'dva:' + str(iTable['DigitalVideoId']) + ' rdf:type owl:NamedIndividual , \n'
        line = line + '        dva:DigitalVideo ; \n'
        line = line + '    dva:hasAspectRatio "' + str(iTable['AspectRatio']) + '" ; \n'
        line = line + '    dva:hasContainer dva:' + str(iTable['ContainerId']) + ' ; \n' 
        line = line + '    dva:hasVideoCodec dva:' + str(iTable['VideoCodecId']) + ' . \n\n' 
        f.write(line)

        # Video codec
        line = 'dva:' + str(iTable['VideoCodecId']) + ' dva:hasDateForAction "' 
        line = line + str(iTable['VCDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   dva:hasConfidence "' + str(iTable['VCConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   dva:hasRiskType "' + str(iTable['VCRiskType']) + '" . \n\n'
        f.write(line)

        # Container
        line = 'dva:' + str(iTable['ContainerId']) + ' dva:hasDateForAction "' 
        line = line + str(iTable['VCDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   dva:hasConfidence "' + str(iTable['VCConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   dva:hasRiskType "' + str(iTable['ContRiskType']) + '" . \n\n'
        f.write(line)

        # Media player
        line = 'dva:' + str(iTable['MediaPlayerId']) + ' dva:hasDateForAction "' 
        line = line + str(iTable['MPDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   dva:hasConfidence "' + str(iTable['MPConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   dva:hasRiskType "' + str(iTable['MPRiskType']) + '" . \n\n'
        f.write(line)

        # Operating system
        line = 'dva:' + str(iTable['OSId']) + ' dva:hasDateForAction "' 
        line = line + str(iTable['OSDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   dva:hasConfidence "' + str(iTable['OSConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   dva:hasRiskType "' + str(iTable['OSRiskType']) + '" . \n\n'
        f.write(line)

        # PC
        line = 'dva:' + str(iTable['PCId']) + ' dva:hasDateForAction "' 
        line = line + str(iTable['PCDateForAction']) + '"^^xsd:date ; \n'
        line = line + '   dva:hasConfidence "' + str(iTable['PCConfidence']) + '"^^xsd:duration ; \n'
        line = line + '   dva:hasRiskType "' + str(iTable['PCRiskType']) + '" . \n\n'
        f.write(line)

        # Artwork recovery options
        line =  'dva:' + str(iTable['ArtworkId']) + ' dva:hasRecoveryOption dva:' + str(iTable['RecoveryOptionId1']) + ' , \n'
        line = line + '        dva:' + str(iTable['RecoveryOptionId2']) + ' . \n\n'
        f.write(line)

        # Recovery Option 1
        line = 'dva:' + str(iTable['RecoveryOptionId1']) + ' dva:hasChange dva:' + str(iTable['RO1_Change1']) + ' , \n'
        line = line + '            dva:' + str(iTable['RO1_Change2']) + ' ; \n'
        line = line + '    dva:hasDateForAction "' + str(iTable['RO1_DateForAction']) + '"^^xsd:date ; \n'
        line = line + '    dva:hasImpact "' + str(iTable['RO1_Impact']) + '" . \n\n'
        f.write(line)

        # Recovery Option 2
        line = 'dva:' + str(iTable['RecoveryOptionId2']) + ' dva:hasChange dva:' + str(iTable['RO2_Change1']) + ' , \n'
        line = line + '            dva:' + str(iTable['RO2_Change2']) + ' ; \n'
        line = line + '    dva:hasDateForAction "' + str(iTable['RO2_DateForAction']) + '"^^xsd:date ; \n'
        line = line + '    dva:hasImpact "' + str(iTable['RO2_Impact']) + '" . \n\n'
        f.write(line)

        # Recovery Option 1: Changes 1 and 2
        line = 'dva:' + str(iTable['RO1_Change1']) + ' dva:hasChangeType dva:' + str(iTable['RO1_Change1_Type']) + ' ; \n'
        line = line +  '     dva:hasChangeValue dva:' + str(iTable['RO1_Change1_Value']) + ' . \n\n'
        line = line + 'dva:' + str(iTable['RO1_Change2']) + ' dva:hasChangeType dva:' + str(iTable['RO1_Change2_Type']) + ' ; \n'
        line = line +  '     dva:hasChangeValue dva:' + str(iTable['RO1_Change2_Value']) + ' . \n\n'
        f.write(line)

        # Recovery Option 2: Changes 1 and 2
        line = 'dva:' + str(iTable['RO2_Change1']) + ' dva:hasChangeType dva:' + str(iTable['RO2_Change1_Type']) + ' ; \n'
        line = line +  '     dva:hasChangeValue dva:' + str(iTable['RO1_Change1_Value']) + ' . \n\n'
        line = line + 'dva:' + str(iTable['RO2_Change2']) + ' dva:hasChangeType dva:' + str(iTable['RO2_Change2_Type']) + ' ; \n'
        line = line +  '     dva:hasChangeValue dva:' + str(iTable['RO2_Change2_Value']) + ' . \n\n'
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

