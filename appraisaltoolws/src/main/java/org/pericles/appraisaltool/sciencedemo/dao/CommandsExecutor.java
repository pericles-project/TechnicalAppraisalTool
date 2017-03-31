/*
 * Copyright King's College London, 2017. 
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  King's College London licences this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

 *  http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.

 * Author: Jun Zhang
 */

package org.pericles.appraisaltool.sciencedemo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public interface CommandsExecutor {

	void createNewArtwork(HashMap<String, String> artworkParameters);

	void createNewCollection(String newCollectionName);

	void deleteArtwork(String artworkName);

	void deleteArtworks(ArrayList<String> artworkNames);

	void deleteCollection(String collectionName);
	
	void deleteAllAnalysisRelatedTriples(String artworkName);
	
	void updateAllComponentsInfo();
	
	String getAllArtworksInfo();
	
	String getAllCollectionNames();
	
	String getAllDatabaseNames();
	
	String getAllScriptingLanguageNames();
	
	String getAllImageFormatNames();
	
	String getAllImageViewerNames();
	
	String getAllDocumentFormatNames();

	String getAllDocumentViewerNames();

	String getArtworkID(String string);

	String getArtworkInfo(String artworkName);
	
	String getArtworkModel(String artworkName);
	
	String getArtworkRecoveryInfo(String artworkName);
	
	String getArtworksInfoFromCollection(String collectionName);
	
	boolean isArtworkNameExisted(String string);

	boolean isCollectionNameExisted(String newCollectionName);
	
	boolean isComponentsCompatible(String firstComponentName, String secondComponentName);

	void writeTriplesToServer(String triples);

	String getComponentTypeStats(String componentTypeName);

	String getAllComponentsInfo();

	String getArtworkName(String artworkID);
	
	LinkedList<String> getAllArtworkIDs();

}