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


package org.pericles.appraisaltool.mediademo.logic;

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
import org.pericles.appraisaltool.mediademo.dao.CommandsExecutor;
import org.pericles.appraisaltool.mediademo.dataanalysis.AnalysisPythonScriptExecutor;
import org.pericles.appraisaltool.mediademo.dataanalysis.HarvesterPythonScriptExecutor;
import org.pericles.appraisaltool.mediademo.dataanalysis.RScriptExecutor;
import org.pericles.appraisaltool.mediademo.util.KeyValue;
import org.pericles.appraisaltool.mediademo.util.PropertiesFileReader;
import org.pericles.appraisaltool.mediademo.util.VideoFileMetadataExtractor;
import org.pericles.appraisaltool.mediademo.util.VideoFileMetadataNameConvertor;

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
	
	private synchronized void updateAllComponentsInfo() {
		commandsExecutor.updateAllComponentsInfo();
	}
	
	public synchronized KeyValue<Integer, String> applyRecoveryOption(String artworkName, 
			  											 String newVideoCodec, 
			  											 String newContainerFormat,
			  											 String newVideoPlayer,
			  											 String newOperatingSystem,
			  											 String newComputer) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject artworkInfoObject = (JSONObject) parser.parse(commandsExecutor.getArtworkInfo(artworkName));
			
			HashMap<String, String> artworkParameters = new HashMap<String, String>();
			artworkParameters.put("videoCodec", newVideoCodec);
			artworkParameters.put("containerFormat", newContainerFormat);
			artworkParameters.put("videoPlayer", newVideoPlayer);
			artworkParameters.put("operatingSystem", newOperatingSystem);
			artworkParameters.put("computer", newComputer);
			
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
	
	public synchronized KeyValue<Integer, String> createNewArtwork(HashMap<String, String>artworkParameters) {

		if (artworkParameters.get("videoCodec") == null || artworkParameters.get("containerFormat") == null) {
			return new KeyValue<Integer, String>(400, "Please upload a video file to analyse metadata or enter video metadata manually.");
		}
		else {
			if (artworkParameters.get("videoCodec").equals("") || artworkParameters.get("containerFormat").equals("")) {
				return new KeyValue<Integer, String>(400, "Please upload a video file to analyse metadata or enter video metadata manually.");
			}
		
			if (commandsExecutor.isArtworkNameExisted(artworkParameters.get("artworkName"))) {
				return new KeyValue<Integer, String>(409, "Object name is already existed.");
			}
			
			if (!commandsExecutor.isCollectionNameExisted(artworkParameters.get("collectionName"))) {
				return new KeyValue<Integer, String>(403, "Collection name not found.");
			}
			
			if (!commandsExecutor.isComponentExisted(artworkParameters.get("videoCodec"))) {
				return new KeyValue<Integer, String>(403, "Video Codec <b>"+artworkParameters.get("videoCodec")+"</b> does not exist in the ontology model. Object creation failed.");
			}
			
			if (!commandsExecutor.isComponentExisted(artworkParameters.get("containerFormat"))) {
				return new KeyValue<Integer, String>(403, "Container Format <b>"+artworkParameters.get("containerFormat")+"</b> does not exist in the ontology model. Object creation failed.");
			}
			
			if (!commandsExecutor.isComponentsCompatible(artworkParameters.get("videoCodec"), artworkParameters.get("videoPlayer"))) {
				return new KeyValue<Integer, String>(403, "Based on the system ontology model, Video Codec <b>" + artworkParameters.get("videoCodec") +
						"</b> is not supported by Video Player <b>"+artworkParameters.get("videoPlayer")+"</b>.");
			}
			
			if (!commandsExecutor.isComponentsCompatible(artworkParameters.get("containerFormat"), artworkParameters.get("videoPlayer"))) {
				return new KeyValue<Integer, String>(403, "Based on the system ontology model, Container Format <b>" + artworkParameters.get("containerFormat") +
						"</b> is not supported by Video Player <b>"+artworkParameters.get("videoPlayer")+"</b>.");
			}
			
			if (!commandsExecutor.isComponentsCompatible(artworkParameters.get("videoPlayer"), artworkParameters.get("operatingSystem"))) {
				return new KeyValue<Integer, String>(403, "Based on the system ontology model, Video Player <b>" + artworkParameters.get("videoPlayer") +
						"</b> is not supported by Operating System <b>"+artworkParameters.get("operatingSystem")+"</b>.");
			}
			
			commandsExecutor.createNewArtwork(artworkParameters);
			
			String artworkID = commandsExecutor.getArtworkID(artworkParameters.get("artworkName"));
			
			LinkedList<String> printouts = analysisPythonScriptExecutor.executeScript(
				propertiesFileReader.getPropertyValue("pythonCommand"),
				propertiesFileReader.getPropertyValue("analysisScriptPath"), 
				propertiesFileReader.getPropertyValue("analysisScriptName"), 
				"dva " + propertiesFileReader.getPropertyValue("fusekiBase") + " " + propertiesFileReader.getPropertyValue("fusekiDataset") + " " + artworkID.substring(artworkID.lastIndexOf("#")+1) + " " + propertiesFileReader.getPropertyValue("predictionOutputFile")
			);
			
			commandsExecutor.writeTriplesToServer(getTriples(printouts));
			
		}	
		
		commandsExecutor.updateAllComponentsInfo();
		
		return new KeyValue<Integer, String>(201, "New object " + artworkParameters.get("artworkName") + " created.");
	}
	
	public synchronized KeyValue<Integer, String> createNewCollection(String newCollectionName) {
		if (commandsExecutor.isCollectionNameExisted(newCollectionName)) {
			return new KeyValue<Integer, String>(409, "The specified collection name is already existed. Please choose another one.");
		}
		else {
			commandsExecutor.createNewCollection(newCollectionName);
		}
		return new KeyValue<Integer, String>(201, "New object collection " + newCollectionName + " created.");
	}
	
	public synchronized KeyValue<Integer, String> deleteArtwork(String artworkName) {
		
		commandsExecutor.deleteArtwork(artworkName);
		
		commandsExecutor.updateAllComponentsInfo();

		return new KeyValue<Integer, String>(200, "Object <b>" + artworkName + "</b> has been deleted.");
	}
	
	public synchronized KeyValue<Integer, String> deleteArtworks(String artworkNames) {
		if (artworkNames != null && artworkNames != "") {
			String[] artworkNamesArray = artworkNames.split(",");
			for (String artworkName : artworkNamesArray) {
				KeyValue<Integer, String> result = deleteArtwork(artworkName);
				if (!result.getKey().equals(200)) {
					return new KeyValue<Integer, String>(500, "Deletion of object "+artworkName+" failed.");
				}
			}
		}
		else {
			return new KeyValue<Integer, String>(500, "No object name specified.");
		}
		
		commandsExecutor.updateAllComponentsInfo();
		
		return new KeyValue<Integer, String>(200, "Selected object(s) has been deleted.");
	}
	
	public synchronized KeyValue<Integer, String> deleteCollection(String collectionName) {
		
		commandsExecutor.deleteCollection(collectionName);
		
		commandsExecutor.updateAllComponentsInfo();
		
		return new KeyValue<Integer, String>(200, "Collection <b>" + collectionName + "</b> has been deleted.");
	}
	
	public synchronized String getAllArtworksInfo() {
		return commandsExecutor.getAllArtworksInfo();
	}
	
	public synchronized String getAllCollectionNames() {
		return commandsExecutor.getAllCollectionNames();
	}
	
	public synchronized String getAllComputerNames() {
		return commandsExecutor.getAllComputerNames();
	}
	
	public synchronized String getAllContainerFormatNames() {
		return commandsExecutor.getAllContainerFormatNames();
	}
	
	public synchronized String getAllOperatingSystemNames() {
		return commandsExecutor.getAllOperatingSystemNames();
	}

	public synchronized String getAllVideoCodecNames() {
		return commandsExecutor.getAllVideoCodecNames();
	}

	public synchronized String getAllVideoPlayerNames() {
		return commandsExecutor.getAllVideoPlayerNames();
	}
	
	public synchronized String getArtworkModel(String artworkName) {
		return commandsExecutor.getArtworkModel(artworkName);
	}

	public synchronized String getArtworkRecoveryInfo(String artworkName) {
		return commandsExecutor.getArtworkRecoveryInfo(artworkName);
	}

	public synchronized String getArtworksInfo(String collectionName) {
		return commandsExecutor.getArtworksInfoFromCollection(collectionName);
	}
	
	private synchronized String getTriples(LinkedList<String> printouts) {
		String triples = "";
		if (printouts != null) {
			ListIterator iterator = printouts.listIterator();
			String line = "";
			while (iterator.hasNext()) {
				line = iterator.next().toString();
				if (!line.contains("##") && line.contains("dva")) {
					triples = triples + line;
				}
			}
		}
		return triples;
	}
	
	public synchronized String getVideoFileMetadata(String videoFilePath) {
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
					String key = line.substring(0, line.indexOf(":")).replace(" ", "");
					String value = line.substring(line.indexOf(":")+2);
					if (key.equals("Format")) {
						value = VideoFileMetadataNameConvertor.getConvertedName(value);
					}
					jsonObject.put(key, value);
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
	
	public synchronized String getVideoMetadata(InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		
		String uploadedFileLocation = videoFileUploadPath + fileDetail.getFileName();

		writeToFile(uploadedInputStream, uploadedFileLocation);
		
		String output = getVideoFileMetadata(uploadedFileLocation);
		
		return output;
	}
	
	private synchronized void writeToFile(InputStream uploadedInputStream,
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
	
	public synchronized String getAllComponentsInfo() {
		return commandsExecutor.getAllComponentsInfo();
	}
	
	public synchronized String getComponentTypeStats(String componentTypeName) {
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
		    			propertiesFileReader.getPropertyValue("fusekiBase") + " " + propertiesFileReader.getPropertyValue("fusekiDataset") + " dva");
		    	
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
						"dva " + propertiesFileReader.getPropertyValue("fusekiBase") + " " + propertiesFileReader.getPropertyValue("fusekiDataset") + " " + artworkID.substring(artworkID.lastIndexOf("#")+1) + " " + propertiesFileReader.getPropertyValue("predictionOutputFile")
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
