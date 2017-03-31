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

package org.pericles.appraisaltool.sciencedemo.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pericles.appraisaltool.sciencedemo.dataanalysis.HarvesterPythonScriptExecutor;
import org.pericles.appraisaltool.sciencedemo.dataanalysis.RScriptExecutor;
import org.pericles.appraisaltool.sciencedemo.dao.CommandsExecutor;
import org.pericles.appraisaltool.sciencedemo.dataanalysis.AnalysisPythonScriptExecutor;
import org.pericles.appraisaltool.sciencedemo.util.KeyValue;
import org.pericles.appraisaltool.sciencedemo.util.PropertiesFileReader;
import org.pericles.appraisaltool.sciencedemo.util.VideoFileMetadataExtractor;

public class WSRequestsHandler {
	
	private final CommandsExecutor commandsExecutor;
	private final VideoFileMetadataExtractor videoFileMetadataExtractor;
	private final HarvesterPythonScriptExecutor pythonScriptExecutor;
	private final RScriptExecutor rScriptExecutor;
	private final AnalysisPythonScriptExecutor analysisPythonScriptExecutor;
	private final PropertiesFileReader propertiesFileReader;
	private final String videoFileMetadataExtractorPath;
	private final String videoFileUploadPath;
	private String allArtworksUpdateStatus;
	
	
	public WSRequestsHandler(CommandsExecutor commandsExecutor, 
							 VideoFileMetadataExtractor videoFileMetadataExtractor,
							 HarvesterPythonScriptExecutor pythonScriptExecutor,
							 RScriptExecutor rScriptExecutor,
							 AnalysisPythonScriptExecutor analysisPythonScriptExecutor,
							 PropertiesFileReader propertiesFileReader) {
		this.commandsExecutor = commandsExecutor;
		this.videoFileMetadataExtractor = videoFileMetadataExtractor;
		this.pythonScriptExecutor = pythonScriptExecutor;
		this.rScriptExecutor = rScriptExecutor;
		this.analysisPythonScriptExecutor = analysisPythonScriptExecutor;
		this.propertiesFileReader = propertiesFileReader;
		this.videoFileMetadataExtractorPath = propertiesFileReader.getPropertyValue("videoFileMetadataExtractorPath");
		this.videoFileUploadPath= propertiesFileReader.getPropertyValue("videoFileUploadPath");
	}
	
	public synchronized KeyValue<Integer, String> applyRecoveryOption(String artworkName,
														 String newDatabase,
														 String newScriptingLanguage,
														 String newImageFormat,
														 String newImageViewer,
														 String newDocumentFormat,
														 String newDocumentViewer) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject artworkInfoObject = (JSONObject) parser.parse(commandsExecutor.getArtworkInfo(artworkName));
			
			HashMap<String, String> artworkParameters = new HashMap<String, String>();
			artworkParameters.put("database", newDatabase);
			artworkParameters.put("scriptingLanguage", newScriptingLanguage);
			artworkParameters.put("imageFormat", newImageFormat);
			artworkParameters.put("imageViewer", newImageViewer);
			artworkParameters.put("documentFormat", newDocumentFormat);
			artworkParameters.put("documentViewer", newDocumentViewer);
			
			artworkParameters.put("artworkName", artworkName);
			artworkParameters.put("collectionName", artworkInfoObject.get("collectionName").toString());
			artworkParameters.put("riskLevel", artworkInfoObject.get("riskLevel").toString());
			artworkParameters.put("accessionDate", artworkInfoObject.get("accessionDate").toString());
			
			deleteArtwork(artworkName);
			
			createNewArtwork(artworkParameters);
			
			commandsExecutor.updateAllComponentsInfo();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return new KeyValue<Integer, String>(200, "Recovery option has been applied.");
	}
	
	public KeyValue<Integer, String> createNewArtwork(HashMap<String, String>artworkParameters) {
		if (commandsExecutor.isArtworkNameExisted(artworkParameters.get("artworkName"))) {
			return new KeyValue<Integer, String>(409, "Object name is already existed.");
		}
		
		if (!commandsExecutor.isCollectionNameExisted(artworkParameters.get("collectionName"))) {
			return new KeyValue<Integer, String>(403, "Collection name not found.");
		}
		
		if (!commandsExecutor.isComponentsCompatible(artworkParameters.get("imageViewer"), artworkParameters.get("imageFormat"))) {
			return new KeyValue<Integer, String>(403, "Based on the system ontology model, Image Format <b>" + artworkParameters.get("imageFormat") +
					"</b> is not supported by Image Viewer <b>"+artworkParameters.get("imageViewer")+"</b>.");
		}
		
		if (!commandsExecutor.isComponentsCompatible(artworkParameters.get("documentViewer"), artworkParameters.get("documentFormat"))) {
			return new KeyValue<Integer, String>(403, "Based on the system ontology model, Document Format <b>" + artworkParameters.get("documentFormat") +
					"</b> is not supported by Document Viewer <b>"+artworkParameters.get("documentViewer")+"</b>.");
		}
		
		commandsExecutor.createNewArtwork(artworkParameters);
		
		String artworkID = commandsExecutor.getArtworkID(artworkParameters.get("artworkName"));
		
		LinkedList<String> printouts = analysisPythonScriptExecutor.executeScript(
			propertiesFileReader.getPropertyValue("pythonCommand"),
			propertiesFileReader.getPropertyValue("analysisScriptPath"), 
			propertiesFileReader.getPropertyValue("analysisScriptName"), 
			"exp " + propertiesFileReader.getPropertyValue("fusekiBase") + " " + propertiesFileReader.getPropertyValue("fusekiDataset") + " " + artworkID.substring(artworkID.lastIndexOf("#")+1) + " " + propertiesFileReader.getPropertyValue("predictionOutputFile")
		);
		
		commandsExecutor.writeTriplesToServer(getTriples(printouts));
		
		commandsExecutor.updateAllComponentsInfo();
		
		return new KeyValue<Integer, String>(201, "New experiment " + artworkParameters.get("artworkName") + " created.");
	}
	
	public KeyValue<Integer, String> createNewCollection(String newCollectionName) {
		if (commandsExecutor.isCollectionNameExisted(newCollectionName)) {
			return new KeyValue<Integer, String>(409, "The specified collection name is already existed. Please choose another one.");
		}
		else {
			commandsExecutor.createNewCollection(newCollectionName);
		}
		return new KeyValue<Integer, String>(201, "New object collection " + newCollectionName + " created.");
	}
	
	public KeyValue<Integer, String> deleteArtwork(String artworkName) {
		
		commandsExecutor.deleteArtwork(artworkName);
		
		commandsExecutor.updateAllComponentsInfo();

		return new KeyValue<Integer, String>(200, "Object <b>" + artworkName + "</b> has been deleted.");
	}
	
	public KeyValue<Integer, String> deleteArtworks(String artworkNames) {
		if (artworkNames != null && artworkNames != "") {
			String[] artworkNamesArray = artworkNames.split(",");
			for (String artworkName : artworkNamesArray) {
				KeyValue<Integer, String> result = deleteArtwork(artworkName);
				if (!result.getKey().equals(200)) {
					return new KeyValue<Integer, String>(500, "Deletion of object "+artworkName+" failed.");
				}
			}
			commandsExecutor.updateAllComponentsInfo();
		}
		else {
			return new KeyValue<Integer, String>(500, "No object name specified.");
		}
		return new KeyValue<Integer, String>(200, "Selected object(s) has been deleted.");
	}
	
	public KeyValue<Integer, String> deleteCollection(String collectionName) {
		
		commandsExecutor.deleteCollection(collectionName);
		
		commandsExecutor.updateAllComponentsInfo();
		
		return new KeyValue<Integer, String>(200, "Collection <b>" + collectionName + "</b> has been deleted.");
	}
	
	public String getAllArtworksInfo() {
		return commandsExecutor.getAllArtworksInfo();
	}
	
	public String getAllCollectionNames() {
		return commandsExecutor.getAllCollectionNames();
	}
	
	public String getAllDatabaseNames() {
		return commandsExecutor.getAllDatabaseNames();
	}
	
	public String getAllScriptingLanguageNames() {
		return commandsExecutor.getAllScriptingLanguageNames();
	}
	
	public String getAllImageFormatNames() {
		return commandsExecutor.getAllImageFormatNames();
	}
	
	public String getAllImageViewerNames() {
		return commandsExecutor.getAllImageViewerNames();
	}

	public String getAllDocumentFormatNames() {
		return commandsExecutor.getAllDocumentFormatNames();
	}

	public String getAllDocumentViewerNames() {
		return commandsExecutor.getAllDocumentViewerNames();
	}
	
	public String getArtworkModel(String artworkName) {
		return commandsExecutor.getArtworkModel(artworkName);
	}

	public String getArtworkRecoveryInfo(String artworkName) {
		return commandsExecutor.getArtworkRecoveryInfo(artworkName);
	}

	public String getArtworksInfo(String collectionName) {
		return commandsExecutor.getArtworksInfoFromCollection(collectionName);
	}
	
	private String getTriples(LinkedList<String> printouts) {
		String triples = "";
		ListIterator iterator = printouts.listIterator();
		String line = "";
		while (iterator.hasNext()) {
			line = iterator.next().toString();
			if (!line.contains("##") && line.contains("exp")) {
				triples = triples + line;
			}
		}
		return triples;
	}
	
	public String getVideoFileMetadata(String videoFilePath) {
		List<String> videoMetadata = videoFileMetadataExtractor.getVideoFileMetadata(videoFileMetadataExtractorPath, videoFilePath);
		JSONObject completeJsonObject = new JSONObject();
		JSONObject jsonObject = null;
		String section = "";
		if (videoMetadata != null) {
			for (String line : videoMetadata) {
				if (line.startsWith("General") || line.startsWith("Video") || line.startsWith("Audio")) {
					section = line;
					jsonObject = new JSONObject();
				}
				else if (line.contains(":")) {
					jsonObject.put(line.substring(0, line.indexOf(":")).replace(" ", ""), line.substring(line.indexOf(":")+2));
				}
				else {
					if (jsonObject != null) {
						completeJsonObject.put(section, jsonObject);
					}
				}
			}
		}
		
		return completeJsonObject.toString();
	}
	
	public String getVideoMetadata(InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		
		String uploadedFileLocation = videoFileUploadPath + fileDetail.getFileName();

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);
		
		//analyse it with mediainfo
		String output = getVideoFileMetadata(uploadedFileLocation);
		
		//The response has to send a valid JSON response. 
		//More details at http://webtips.krajee.com/question/json-syntax-error-bootstrap-fileinput/
		return output;
	}
	
	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	public String getAllComponentsInfo() {
		return commandsExecutor.getAllComponentsInfo();
	}
	
	public String getComponentTypeStats(String componentTypeName) {
		return commandsExecutor.getComponentTypeStats(componentTypeName);
	}
	
	public synchronized String updateAllArtworks() {
		update();
		return allArtworksUpdateStatus;
	}
	
	private synchronized void update() {
		new Thread() {
		    public void run() {
		    	allArtworksUpdateStatus = "Stage 1 of 3: Executing Python code to harvest all components...";
		    	System.out.println(allArtworksUpdateStatus);
		    	pythonScriptExecutor.executeScript(
		    			propertiesFileReader.getPropertyValue("pythonCommand"), 
		        		propertiesFileReader.getPropertyValue("harvestScriptPath"),
		    			propertiesFileReader.getPropertyValue("harvestScriptName"), 
		    			propertiesFileReader.getPropertyValue("fusekiBase") + " " + propertiesFileReader.getPropertyValue("fusekiDataset") + " exp");
		    	
				allArtworksUpdateStatus = "Stage 2 of 3: Executing R code to analyse predicted end dates for all components...";
				System.out.println(allArtworksUpdateStatus);
				rScriptExecutor.executeRscript(
					propertiesFileReader.getPropertyValue("rCommand"),
					propertiesFileReader.getPropertyValue("predictionScriptPath"),
					propertiesFileReader.getPropertyValue("predictionScriptName"),
					propertiesFileReader.getPropertyValue("harvestOutputFile") + " " + propertiesFileReader.getPropertyValue("predictionOutputFile")
				);
		    	
				allArtworksUpdateStatus = "Stage 3 of 3: Executing Python code to update all component's risk analysis information...";
				System.out.println(allArtworksUpdateStatus);
				LinkedList<String> artworkIDList = commandsExecutor.getAllArtworkIDs();
				for (String artworkID : artworkIDList) {
					
					LinkedList<String> printouts = analysisPythonScriptExecutor.executeScript(
						propertiesFileReader.getPropertyValue("pythonCommand"),
						propertiesFileReader.getPropertyValue("analysisScriptPath"), 
						propertiesFileReader.getPropertyValue("analysisScriptName"), 
						"exp " + propertiesFileReader.getPropertyValue("fusekiBase") + " " + propertiesFileReader.getPropertyValue("fusekiDataset") + " " + artworkID.substring(artworkID.lastIndexOf("#")+1) + " " + propertiesFileReader.getPropertyValue("predictionOutputFile")
					);	
					
					commandsExecutor.deleteAllAnalysisRelatedTriples(commandsExecutor.getArtworkName("<"+artworkID+">"));
					
					commandsExecutor.writeTriplesToServer(getTriples(printouts));
				}
				
				allArtworksUpdateStatus = "Update finished";
				System.out.println(allArtworksUpdateStatus);
		    }
		}.start();
	}
	
	public synchronized String getAllArtworksUpdateStatus() {
		return allArtworksUpdateStatus;
	}

	
}
