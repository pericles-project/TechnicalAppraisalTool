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

package org.pericles.appraisaltool.mediademo.dao;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.UUID;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pericles.appraisaltool.mediademo.util.DateTool;
import org.pericles.appraisaltool.mediademo.util.PropertiesFileReader;

public class FusekiCommandsExecutor implements CommandsExecutor  {

	private final FusekiConnector fusekiConnector;
	private final PropertiesFileReader propertiesFileReader;
	private final String fusekiQueryUrl;
	private final String fusekiUpdateUrl;
	private static volatile String allComponentsInfoArray = null;
	
	public FusekiCommandsExecutor(FusekiConnector fusekiConnector,
								  PropertiesFileReader propertiesFileReader) {
		this.fusekiConnector = fusekiConnector;
		this.propertiesFileReader = propertiesFileReader;
		this.fusekiQueryUrl = propertiesFileReader.getPropertyValue("fusekiQueryUrl");
		this.fusekiUpdateUrl = propertiesFileReader.getPropertyValue("fusekiUpdateUrl");
	}
	
	private final String prefix = "PREFIX dva: <https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#>" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
			"PREFIX LRM: <http://xrce.xerox.com/LRM#>" +
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";

	private HashMap<String, Double> totalNumberOfComponentsMap = new HashMap<String, Double>();

	public void applyRecoveryOption(String artworkName, 
			String newVideoCodec, 
			String newContainerFormat,
			String newVideoPlayer,
			String newOperatingSystem,
			String newComputer) {
		String updateContent = 
				"DELETE {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/videoCodec> ?o}"
						+ "INSERT {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/videoCodec> \""+newVideoCodec+"\" }"
						+ "WHERE {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/videoCodec> ?o};"
						+ "DELETE {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/containerFormat> ?o}"
						+ "INSERT {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/containerFormat> \""+newContainerFormat+"\" }"
						+ "WHERE {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/containerFormat> ?o};"
						+ "DELETE {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/videoPlayer> ?o}"
						+ "INSERT {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/videoPlayer> \""+newVideoPlayer+"\" }"
						+ "WHERE {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/videoPlayer> ?o};"
						+ "DELETE {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/operatingSystem> ?o}"
						+ "INSERT {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/operatingSystem> \""+newOperatingSystem+"\" }"
						+ "WHERE {<http://pericles.org/artwork/"+artworkName+"> <http://pericles.org/artwork/operatingSystem> ?o}";

		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}

	public void calculateAllComponentUsedTimes() {

		ArrayList<String> componentNamesList = getAllComponentNames();

		String componentName = "";
		double totalNumberOfRows = 0;
		String queryContent = "";
		QueryExecution qe = null;
		ResultSet results = null;
		QuerySolution soln = null;
		String result = "";

		for (int i = 0; i < componentNamesList.size(); i++) {

			componentName = componentNamesList.get(i);

			String tempQuery = "";
			switch(getComponentType("<"+getComponentID(componentName)+">")) {
			case "videoCodec" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?s dva:hasVideoCodec ?componentID";
			break;
			case "VideoCodec" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?s dva:hasVideoCodec ?componentID";
			break;
			case "containerFormat" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?s dva:hasContainer ?componentID";
			break;
			case "Container" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?s dva:hasContainer ?componentID";
			break;
			case "videoPlayer" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:MediaPlayer . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "MediaPlayer" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:MediaPlayer . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "operatingSystem" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:OperatingSystem . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "OperatingSystem" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:OperatingSystem . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "computer" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:Computer . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "Computer" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:Computer . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			}

			queryContent = 
					prefix +
					"SELECT (count(?componentID) AS ?count) {" +
					tempQuery +
					"}";

			qe = QueryExecutionFactory.sparqlService(fusekiQueryUrl, queryContent);
			results = qe.execSelect();

			if (results.hasNext()) {
				soln = results.nextSolution();
				result = soln.get("count").toString();
				totalNumberOfRows = Double.valueOf(result.substring(0, result.indexOf("^")));
			}

			totalNumberOfComponentsMap.put(componentName, totalNumberOfRows);
		}
		qe.close();
	}

	@Override
	public void createNewArtwork(HashMap<String, String> artworkParameters) {
		
		String collectionID = "<" + getCollectionID(artworkParameters.get("collectionName")) + ">";

		String digitalVideoID = "dva:digitalvideo_" + UUID.randomUUID();

		String updateContent = 
				prefix +
				"INSERT DATA {" + 
				"	dva:artwork_" + UUID.randomUUID() + " rdf:type owl:NamedIndividual ," +
				"						   							   		  LRM:AggregatedResource ," +
				"						   							   		  dva:DigitalVideoArtwork ;" + 
				"				  								 LRM:hasPart " + digitalVideoID + " ," +
				"							  						  	     dva:" + artworkParameters.get("videoPlayer").replace(" ", "_") + ", "  +
				"							  								 dva:" + artworkParameters.get("operatingSystem").replace(" ", "_") + ", " +
				"							  								 dva:" + artworkParameters.get("computer").replace(" ", "_") + "; " +
				"				  								 dva:isMemberOf " +collectionID+ " ;" +
				"				  								 rdfs:label \"" +artworkParameters.get("artworkName")+ "\" ;" +
				//"				  								 dva:hasDateForAction \"" +artworkParameters.get("dateForAction")+ "\"^^xsd:date ;" +
				//"				  								 dva:hasImpact \"" +artworkParameters.get("impact")+ "\" ;" +
				"			      								 dva:hasAccessionDate \"" +DateTool.getXMLFormatDate(artworkParameters.get("accessionDate"))+ "\"^^xsd:date ." +
				//"	              								 dva:hasRiskLevel \"" +artworkParameters.get("riskLevel")+ "\" ." +
				//"	              								 dva:hasConfidence \"P" +artworkParameters.get("confidence")+ "Y\"^^xsd:duration ;" +
				//"	              								 dva:hasPrimaryRiskType \"" + artworkParameters.get("primaryRiskType") + "\" ." +

		    digitalVideoID + " rdf:type owl:NamedIndividual , " +
		    "                               dva:DigitalVideo ;" +	
		    "                      dva:hasContainer dva:" + artworkParameters.get("containerFormat").replace(" ", "_") + " ;" +
		    "                      dva:hasVideoCodec dva:" + artworkParameters.get("videoCodec").replace(" ", "_") + " ." +
		    "}";

		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}

	/**
	 * create a new artwork collection.
	 * @param newCollectionName the name of the new artwork collection
	 */
	@Override
	public void createNewCollection(String newCollectionName) {

		String updateContent = 
				prefix +
				"INSERT DATA { " +
				"	dva:collection_" + UUID.randomUUID() + " a dva:DigitalVideoArtworkCollection ;" +
				"	rdfs:label \"" + newCollectionName + "\" . " +
				"}";

		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}
	
	private void deleteIncompleteArtworkTriples(String artworkName) {
		String queryContent = 
				prefix + 
				"SELECT ?artworkID ?digitalVideoID ?recoveryOptionID ?changeID WHERE {" +
				"	?artworkID rdfs:label \"" + artworkName + "\" ." +
				"   ?artworkID LRM:hasPart ?digitalVideoID ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		String artworkID = "";
		String digitalVideoID = "";

		while (results.hasNext()) {

			QuerySolution soln = results.nextSolution() ;

			if (soln.get("digitalVideoID") != null) {
				digitalVideoID = soln.get("digitalVideoID").toString();
				if (digitalVideoID.contains("digitalvideo")) {
					deleteDigitalVideoTriples(digitalVideoID);
				}
			}

			if (soln.get("artworkID") != null) {
				artworkID = soln.get("artworkID").toString();
				deleteArtworkTriples(artworkID);
			}
		}

	}
	
	public void deleteAllArtworkRelatedTriples(String artworkName) {
		String queryContent = 
				prefix + 
				"SELECT ?artworkID ?digitalVideoID ?recoveryOptionID ?changeID WHERE {" +
				"	?artworkID rdfs:label \"" + artworkName + "\" ." +
				"   ?artworkID LRM:hasPart ?digitalVideoID ." +
				"   ?artworkID dva:hasRecoveryOption ?recoveryOptionID ." +
				"   ?recoveryOptionID dva:hasChange ?changeID ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		String artworkID = "";
		String digitalVideoID = "";
		String recoveryOptionID = "";
		String changeID = "";
		
		//If no result returns, it means the artwork does not all the triples.
		if (!results.hasNext()) {
			deleteIncompleteArtworkTriples(artworkName);
		}
		else {
			while (results.hasNext()) {
	
				QuerySolution soln = results.nextSolution() ;
	
				if (soln.get("changeID") != null) {
					changeID = soln.get("changeID").toString();
					deleteChangeTriples(changeID);
				}
	
				if (soln.get("recoveryOptionID") != null) {
					recoveryOptionID = soln.get("recoveryOptionID").toString();
					
					deleteRecoveryOptionTriples(recoveryOptionID);
				}
	
				if (soln.get("digitalVideoID") != null) {
					digitalVideoID = soln.get("digitalVideoID").toString();
					if (digitalVideoID.contains("digitalvideo")) {
						deleteDigitalVideoTriples(digitalVideoID);
					}
				}
	
				if (soln.get("artworkID") != null) {
					artworkID = soln.get("artworkID").toString();
					deleteArtworkTriples(artworkID);
				}
			}
		}
	}
	
	public void deleteAllAnalysisRelatedTriples(String artworkName) {
		String queryContent = 
				prefix + 
				"SELECT ?artworkID ?recoveryOptionID ?changeID WHERE {" +
				"	?artworkID rdfs:label \"" + artworkName + "\" ." +
				"   ?artworkID dva:hasRecoveryOption ?recoveryOptionID ." +
				"   ?recoveryOptionID dva:hasChange ?changeID ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		String artworkID = "";
		String digitalVideoID = "";
		String recoveryOptionID = "";
		String changeID = "";
		
		//If no result returns, it means the artwork does not all the triples.
		if (!results.hasNext()) {
		}
		else {
			while (results.hasNext()) {
	
				QuerySolution soln = results.nextSolution() ;
	
				if (soln.get("changeID") != null) {
					changeID = soln.get("changeID").toString();
					deleteChangeTriples(changeID);
				}
	
				if (soln.get("recoveryOptionID") != null) {
					recoveryOptionID = soln.get("recoveryOptionID").toString();
					deleteRecoveryOptionTriples(recoveryOptionID);
				}
	
				if (soln.get("artworkID") != null) {
					artworkID = soln.get("artworkID").toString();
					deletePredictionTriples(artworkID);
				}
			}
		}
	}

	/* 
	 * Delete specified artwork.
	 * @parameter artworkName the name of the artwork to be deleted
	 */
	@Override
	public void deleteArtwork(String artworkName) {
		deleteAllArtworkRelatedTriples(artworkName);
	}

	@Override
	public void deleteArtworks(LinkedList<String> artworkNames) {
		ListIterator<String> iterator = artworkNames.listIterator();
		while (iterator.hasNext()) {
			deleteArtwork(iterator.next().toString());
		}
	}

	public void deleteArtworkTriples(String artworkID) {
		String updateContent =
				prefix +
				"DELETE WHERE {" +
				"<" + artworkID + "> ?p ?o . " +
				"}";
		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}
	
	public void deletePredictionTriples(String artworkID) {
		String updateContent =
				prefix +
				"DELETE WHERE {<" + artworkID + "> dva:hasImpact ?impact . };" +
				"DELETE WHERE {<" + artworkID + "> dva:hasRiskLevel ?riskLevel . };" +
				"DELETE WHERE {<" + artworkID + "> dva:hasDateForAction ?dateForAction . };" +
				"DELETE WHERE {<" + artworkID + "> dva:hasConfidence ?confidence . };" +
				"DELETE WHERE {<" + artworkID + "> dva:hasPrimaryRiskType ?primaryRiskType . }";

		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}

	public void deleteChangeTriples(String changeID) {
		String updateContent =
				prefix +
				"DELETE WHERE {" +
				"<" + changeID + "> ?p ?o . " +
				"}";
		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}

	/**
	 * delete a artwork collection.
	 * @param collectionName the name of the collection to be deleted
	 */
	@Override
	public void deleteCollection(String collectionName) {

		LinkedList<String> artworkNameList = getArtworkNamesInCollection(collectionName);
		if (artworkNameList.size() != 0) {
			deleteArtworks(artworkNameList);
		}

		String updateContent = 
				prefix +
				"DELETE WHERE {" +
				"   ?c rdf:type dva:DigitalVideoArtworkCollection . " +
				"	?c rdfs:label \"" + collectionName + "\" . " +
				"	?c ?p ?o" +
				"}";

		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}

	public void deleteDigitalVideo(String digitalVideoID) {
		String updateContent = 
				prefix +
				"DELETE WHERE {" +
				digitalVideoID + "rdf:type dva:DigitalVideo . " +
				digitalVideoID + " ?p ?o" +
				"}";
		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}

	public void deleteDigitalVideoTriples(String digitalVideoID) {
		String updateContent =
				prefix +
				"DELETE WHERE {" +
				"<" + digitalVideoID + "> ?p ?o . " +
				"}";
		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}

	public void deleteRecoveryOptionTriples(String recoveryOptionID) {
		String updateContent =
				prefix +
				"DELETE WHERE {" +
				"<" + recoveryOptionID + "> ?p ?o . " +
				//"   ?s ?p ?o" +
				"}";
		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}

	/**
	 * get all artworks information.
	 * @return all artworks information. For example, 
	 * [{"riskLevel":"3","impact":"Low","confidence":"±1 Year","operatingSystem":"Windows 98","videoPlayer":"Xine",
	 * "collectionName":"Buildings","artworkName":"Skyscrapers","computer":"P6","primaryRisk":"Obsolescence",
	 * "containerFormat":"Flash Video","accessionDate":"01\/06\/2008","dateForAction":"23\/02\/2022","videoCodec":"MPEG-1"},
	 * {"riskLevel":"6","impact":"Medium","confidence":"±2 Years","operatingSystem":"Windows 95","videoPlayer":"RealPlayer",
	 * "collectionName":"Nature","artworkName":"Horses","computer":"P5","primaryRisk":"Obsolescence",
	 * "containerFormat":"QuickTime","accessionDate":"01\/04\/2012","dateForAction":"09\/01\/2020","videoCodec":"RealVideo"}] 
	 */
	public String getAllArtworksInfo() {
		JSONArray artworksInfoArray = new JSONArray();
		
		ResultSet allArtworkNamesResults = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?s WHERE {" +
				"	?s rdf:type dva:DigitalVideoArtwork" +
				"}");

		while (allArtworkNamesResults.hasNext()) {
			QuerySolution allArtworkNamesResultsSolution = allArtworkNamesResults.nextSolution() ;

			ResultSet artworkInfoResult = fusekiConnector.executeQuery(fusekiQueryUrl,
					prefix +
					"SELECT ?p ?o WHERE {" +
					"	<"+allArtworkNamesResultsSolution.get("s").toString()+"> ?p ?o" +
					"}");

			JSONObject jsonObject = new JSONObject();

			while (artworkInfoResult.hasNext()) {
				QuerySolution artworkInfoResultSolution = artworkInfoResult.nextSolution();
				String predicate = artworkInfoResultSolution.get("p").toString();
				String object = artworkInfoResultSolution.get("o").toString();

				switch (predicate) {
				case "http://www.w3.org/2000/01/rdf-schema#label" : jsonObject.put("artworkName", object);
				break;
				case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#isMemberOf"	: jsonObject.put("collectionName", getCollectionName("<" + object + ">"));		
				break;
				case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasPrimaryRiskType"	: jsonObject.put("primaryRisk", object);		
				break;
				case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasDateForAction"	: jsonObject.put("dateForAction", DateTool.getDisplayFormatDate(object));	
				break;
				case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasConfidence"	: jsonObject.put("confidence", DateTool.getDisplayFormatConfidence(object));		
				break;
				case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasAccessionDate"	: jsonObject.put("accessionDate", DateTool.getDisplayFormatDate(object));		
				break;
				case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasRiskLevel"	: jsonObject.put("riskLevel", object);		
				break;
				case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasRiskValue"	: jsonObject.put("riskLevel", object);		
				break;
				case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasImpact"	: jsonObject.put("impact", object);
				break;
				case "http://xrce.xerox.com/LRM#hasPart": {
					String componentType = getComponentType("<"+object+">");
					if (componentType != "") {
						if (componentType.equals("DigitalVideo")) {
							jsonObject.put("videoCodec", getVideoCodecName("<"+object+">"));
							jsonObject.put("containerFormat", getContainerFormatName("<"+object+">"));
						}
						else if (componentType.equals("MediaPlayer")) {
							jsonObject.put("videoPlayer", getComponentName("<"+object+">"));
						}
						else if (componentType.equals("OperatingSystem")) {
							jsonObject.put("operatingSystem", getComponentName("<"+object+">"));
						}
						else if (componentType.equals("Computer")) {
							jsonObject.put("computer", getComponentName("<"+object+">"));
						}
					}
					break;
				}
				}
			}
			artworksInfoArray.add(jsonObject);
		}
		return artworksInfoArray.toJSONString();
	}

	/**
	 * get all artwork collection names.
	 * @return all artwork collection names. For example, ["Buildings","Nature"]
	 */
	public String getAllCollectionNames() {

		JSONArray collectionNamesArray = new JSONArray();

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?s ?o WHERE {?s a dva:DigitalVideoArtworkCollection ." +
				"?s rdfs:label ?o .}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			collectionNamesArray.add(soln.get("o").toString());
		}

		return collectionNamesArray.toJSONString();
	}

	public ArrayList<String> getAllComponentNames() {
		ArrayList<String> componentNames = new ArrayList<String>();

		componentNames.addAll(getAllNamesOfAComponentType("videoCodec"));
		componentNames.addAll(getAllNamesOfAComponentType("containerFormat"));
		componentNames.addAll(getAllNamesOfAComponentType("videoPlayer"));
		componentNames.addAll(getAllNamesOfAComponentType("operatingSystem"));
		componentNames.addAll(getAllNamesOfAComponentType("computer"));
		return componentNames;
	}

	public String getAllComponentsInfo() {
		updateAllComponentsInfo();
		return allComponentsInfoArray;
	}
	
	public void updateAllComponentsInfo() {

		JSONArray componentsInfoArray = new JSONArray();

		ArrayList<String> componentNamesList = getAllComponentNames();
		
		calculateAllComponentUsedTimes();
		
		int count = 0;

		ResultSet results = null;
		QuerySolution soln = null;
		JSONObject componentInfoObject = null;
		String componentName = "";
		String queryContent = "";

		for (int i = 0; i < componentNamesList.size(); i++) {

			componentName = componentNamesList.get(i);

			long usedPercentage = getComponentUsedPercentage(componentName);
			
			if (usedPercentage > 0) {

				queryContent = 
						prefix +
						"SELECT * WHERE {" +
						"	?componentID rdfs:label \"" + componentName +"\" ." +
						"   ?componentID dva:hasRiskType ?riskType ." +
						"	?componentID dva:hasConfidence ?confidence ." +
						"   ?componentID dva:hasDateForAction ?dateForAction ." +
						"}";

				results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

				componentInfoObject = new JSONObject();

				componentInfoObject.put("Type", getDisplayComponentType(getComponentType("<"+getComponentID(componentName)+">")));

				componentInfoObject.put("riskType", "");
				componentInfoObject.put("confidence", "");
				componentInfoObject.put("dateForAction", "");
				componentInfoObject.put("usedPercentage", "0");

				if (results.hasNext()) {
					soln = results.nextSolution();

					componentInfoObject.put("riskType", soln.get("riskType").toString());

					componentInfoObject.put("confidence", DateTool.getDisplayFormatConfidence(soln.get("confidence").toString()));

					componentInfoObject.put("dateForAction", DateTool.getDisplayFormatDate(soln.get("dateForAction").toString()));

				}

				componentInfoObject.put("Name", componentName);
				componentInfoObject.put("usedPercentage", usedPercentage);
				componentsInfoArray.add(componentInfoObject);
			}
		}
		
		allComponentsInfoArray = componentsInfoArray.toJSONString();
	}
	
	public String getAllComponentsInfoInJson2() {
		String componentsInfo = "[";

		ArrayList<String> componentNamesList = getAllComponentNames();

		ResultSet results = null;
		QuerySolution soln = null;
		String componentName = "";
		String queryContent = "";
		String tempComponentInfo = "";

		for (int i = 0; i < componentNamesList.size(); i++) {

			componentName = componentNamesList.get(i);

			queryContent = 
					prefix +
					"SELECT * WHERE {" +
					"	?componentID rdfs:label \"" + componentName +"\" ." +
					"   ?componentID dva:hasRiskType ?riskType ." +
					"	?componentID dva:hasConfidence ?confidence ." +
					"   ?componentID dva:hasDateForAction ?dateForAction ." +
					"}";

			results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

			if (i == 0) {
				tempComponentInfo = "{\"Type\":\"\",\"usedPercentage\":\"0\",\"riskType\":\"\",\"confidence\":\"\",\"dateForAction\":\"\",\"Name\":\"\"}";
			}
			else {
				tempComponentInfo = ",{\"Type\":\"\",\"usedPercentage\":\"0\",\"riskType\":\"\",\"confidence\":\"\",\"dateForAction\":\"\",\"Name\":\"\"}";
			}

			if (results.hasNext()) {
				soln = results.nextSolution();

				tempComponentInfo = tempComponentInfo.replace("\"Type\":\"\"", "\"Type\":\""+soln.get("riskType").toString()+"\"");
				tempComponentInfo = tempComponentInfo.replace("\"confidence\":\"\"", "\"confidence\":\""+DateTool.getDisplayFormatConfidence(soln.get("confidence").toString())+"\"");
				tempComponentInfo = tempComponentInfo.replace("\"dateForAction\":\"\"", "\"dateForAction\":\""+DateTool.getDisplayFormatDate(soln.get("dateForAction").toString())+"\"");

			}

			tempComponentInfo = tempComponentInfo.replace("\"Name\":\"\"", "\"Name\":\""+componentName+"\"");
			tempComponentInfo = tempComponentInfo.replace("\"usedPercentage\":\"\"", "\"usedPercentage\":\""+String.valueOf(getComponentUsedPercentage(componentName))+"\"");

			componentsInfo = componentsInfo + tempComponentInfo;
		}

		componentsInfo = componentsInfo + "]";

		return componentsInfo;
	}

	/**
	 * get all computer names.
	 * @return all computer names. For example, ["P1","P4","P2"] 
	 */
	public String getAllComputerNames() {
		JSONArray computerNamesArray = new JSONArray();

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?computerName WHERE {" +
				"	?computerID rdf:type dva:Computer ." +
				"	?computerID rdfs:label ?computerName ." +
				"}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			computerNamesArray.add(soln.get("computerName").toString());
		}

		return computerNamesArray.toJSONString();
	}

	/**
	 * get all container format names.
	 * @return all container format names. For example, ["AVI","Flash Video","MPEG-4 Part 14","MXF"] 
	 */
	public String getAllContainerFormatNames() {
		JSONArray containerFormatNamesArray = new JSONArray();

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?containerFormatName WHERE {" +
				"	?containerFormatID rdf:type dva:Container ." +
				"	?containerFormatID rdfs:label ?containerFormatName ." +
				"}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			containerFormatNamesArray.add(soln.get("containerFormatName").toString());
		}

		return containerFormatNamesArray.toJSONString();
	}

	public ArrayList<String> getAllNamesOfAComponentType(String componentTypeName) {
		ArrayList<String> componentNames = new ArrayList<String>();

		String tempQuery = "";
		switch(componentTypeName) {
		case "videoCodec" : tempQuery = "?componentID rdf:type dva:VideoCodec .";
		break;
		case "containerFormat" : tempQuery = "?componentID rdf:type dva:Container .";
		break;
		case "videoPlayer" : tempQuery = "?componentID rdf:type dva:MediaPlayer .";
		break;
		case "operatingSystem" : tempQuery = "	?componentID rdf:type dva:OperatingSystem .";
		break;
		case "computer" : tempQuery = "	?componentID rdf:type dva:Computer .";
		break;
		}

		String queryContent = 
				prefix +
				"SELECT ?componentName WHERE {" +
				"	?componentID rdf:type owl:NamedIndividual ." +
				tempQuery +
				"	?componentID rdfs:label ?componentName ." +
				"}";


		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			componentNames.add(soln.get("componentName").toString());
		}

		return componentNames;
	}

	/**
	 * get all operating system names.
	 * @return all operating system names. For example, ["RHEL 4","Windows 8","RHEL 3","Windows me"] 
	 */
	public String getAllOperatingSystemNames() {
		JSONArray operatingSystemNamesArray = new JSONArray();

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?operatingSystemName WHERE {" +
				"	?operatingSystemID rdf:type dva:OperatingSystem ." +
				"	?operatingSystemID rdfs:label ?operatingSystemName ." +
				"}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			operatingSystemNamesArray.add(soln.get("operatingSystemName").toString());
		}

		return operatingSystemNamesArray.toJSONString();
	}

	/**
	 * get all video codec names.
	 * @return all video codec names. For example, ["MPEG-1","H.262","H.263","RealVideo"] 
	 */
	public String getAllVideoCodecNames() {
		JSONArray videoCodecNamesArray = new JSONArray();

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?videoCodecName WHERE {" +
				"	?videoCodecID rdf:type dva:VideoCodec ." +
				"	?videoCodecID rdfs:label ?videoCodecName ." +
				"}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			videoCodecNamesArray.add(soln.get("videoCodecName").toString());
		}

		return videoCodecNamesArray.toJSONString();
	}

	/**
	 * get all video player names.
	 * @return all video player names. For example, ["Media Player Classic","Winamp","Kodi","ALLPlayer"] 
	 */
	public String getAllVideoPlayerNames() {
		JSONArray videoPlayerNamesArray = new JSONArray();

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?videoPlayerName WHERE {" +
				"	?videoPlayerID rdf:type dva:MediaPlayer ." +
				"	?videoPlayerID rdfs:label ?videoPlayerName ." +
				"}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			videoPlayerNamesArray.add(soln.get("videoPlayerName").toString());
		}

		return videoPlayerNamesArray.toJSONString();
	}

	public String getArtworkID(String artworkName) {
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?artworkID WHERE {" +
				"?artworkID rdfs:label \"" + artworkName +"\" " +
				"}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			//Literal o = soln.getLiteral("o") ;   // Get a result variable - must be a literal
			return soln.get("?artworkID").toString();
		}
		return "Artwork ID not found";
	}
	
	public LinkedList<String> getAllArtworkIDs() {
		LinkedList<String> artworkNameList = new LinkedList<String>();
		String queryContent = 
				prefix +
				"SELECT ?artworkID WHERE {" +
				"   ?artworkID rdf:type dva:DigitalVideoArtwork ." +
				"}";
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			String fullArtworkID = soln.get("artworkID").toString();
			artworkNameList.add(fullArtworkID);
		}
		return artworkNameList;
	}

	public String getArtworkInfo(String artworkName) {
		String queryContent = 
				prefix +
				"SELECT ?p ?o WHERE {" +
				"   ?artworkID rdfs:label \"" + artworkName + "\" ." +
				"   ?artworkID ?p ?o ." +
				"}";

		ResultSet artworkInfoResult = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);


		JSONObject jsonObject = new JSONObject();
		while (artworkInfoResult.hasNext()) {
			QuerySolution artworkInfoResultSolution = artworkInfoResult.nextSolution();
			String predicate = artworkInfoResultSolution.get("p").toString();
			String object = artworkInfoResultSolution.get("o").toString();

			switch (predicate) {
			case "http://www.w3.org/2000/01/rdf-schema#label" : jsonObject.put("artworkName", object);
			break;
			case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#isMemberOf"	: jsonObject.put("collectionName", getCollectionName("<" + object + ">"));		
			break;
			case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasPrimaryRiskType"	: jsonObject.put("primaryRisk", object);		
			break;
			case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasDateForAction"	: jsonObject.put("dateForAction", DateTool.getDisplayFormatDate(object));	
			break;
			case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasConfidence"	: jsonObject.put("confidence", DateTool.getDisplayFormatConfidence(object));		
			break;
			case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasAccessionDate"	: jsonObject.put("accessionDate", DateTool.getDisplayFormatDate(object));		
			break;
			case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasRiskLevel"	: jsonObject.put("riskLevel", object);		
			break;
			case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasRiskValue"	: jsonObject.put("riskLevel", object);		
			break;
			case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasImpact"	: jsonObject.put("impact", object);
			break;
			case "http://xrce.xerox.com/LRM#hasPart": {
				String componentType = getComponentType("<"+object+">");
				if (componentType != "") {
					if (componentType.equals("DigitalVideo")) {
						jsonObject.put("videoCodec", getVideoCodecName("<"+object+">"));
						jsonObject.put("containerFormat", getContainerFormatName("<"+object+">"));
					}
					else if (componentType.equals("MediaPlayer")) {
						jsonObject.put("videoPlayer", getComponentName("<"+object+">"));
					}
					else if (componentType.equals("OperatingSystem")) {
						jsonObject.put("operatingSystem", getComponentName("<"+object+">"));
					}
					else if (componentType.equals("Computer")) {
						jsonObject.put("computer", getComponentName("<"+object+">"));
					}
				}
				break;
			}
			}
		}
		return jsonObject.toJSONString();    
	}

	public String getArtworkInfoFromCollection(String collectionName, String artworkName) {
		JSONArray artworksInfoArray = new JSONArray();

		ResultSet allArtworkNamesResults = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?a WHERE {" +
				"	?c rdfs:label \"" + collectionName +"\" ." +
				"	?a dva:isMemberOf ?c " +
				"}");

		while (allArtworkNamesResults.hasNext()) {
			QuerySolution allArtworkNamesResultsSolution = allArtworkNamesResults.nextSolution() ;

			if (getArtworkName("<"+allArtworkNamesResultsSolution.get("a").toString()+">").equals(artworkName)) {

				ResultSet artworkInfoResult = fusekiConnector.executeQuery(fusekiQueryUrl,
						prefix +
						"SELECT ?p ?o WHERE {" +
						"	<"+allArtworkNamesResultsSolution.get("a").toString()+"> ?p ?o" +
						"}");

				JSONObject jsonObject = new JSONObject();
				while (artworkInfoResult.hasNext()) {
					QuerySolution artworkInfoResultSolution = artworkInfoResult.nextSolution();
					String predicate = artworkInfoResultSolution.get("p").toString();
					String object = artworkInfoResultSolution.get("o").toString();

					switch (predicate) {
						case "http://www.w3.org/2000/01/rdf-schema#label" : jsonObject.put("artworkName", object);
						break;
						case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#isMemberOf"	: jsonObject.put("collectionName", getCollectionName("<" + object + ">"));		
						break;
						case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasPrimaryRiskType"	: jsonObject.put("primaryRisk", object);		
						break;
						case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasDateForAction"	: jsonObject.put("dateForAction", DateTool.getDisplayFormatDate(object));	
						break;
						case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasConfidence"	: jsonObject.put("confidence", DateTool.getDisplayFormatConfidence(object));		
						break;
						case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasAccessionDate"	: jsonObject.put("accessionDate", DateTool.getDisplayFormatDate(object));		
						break;
						case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasRiskLevel"	: jsonObject.put("riskLevel", object);		
						break;
						case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasRiskValue"	: jsonObject.put("riskLevel", object);		
						break;
						case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasImpact"	: jsonObject.put("impact", object);
						break;
						case "http://xrce.xerox.com/LRM#hasPart": {
							String componentType = getComponentType("<"+object+">");
							if (componentType != "") {
								if (componentType.equals("DigitalVideo")) {
									jsonObject.put("videoCodec", getVideoCodecName("<"+object+">"));
									jsonObject.put("containerFormat", getContainerFormatName("<"+object+">"));
								}
								else if (componentType.equals("MediaPlayer")) {
									jsonObject.put("videoPlayer", getComponentName("<"+object+">"));
								}
								else if (componentType.equals("OperatingSystem")) {
									jsonObject.put("operatingSystem", getComponentName("<"+object+">"));
								}
								else if (componentType.equals("Computer")) {
									jsonObject.put("computer", getComponentName("<"+object+">"));
								}
							}
							break;
						}
					}
				}
				artworksInfoArray.add(jsonObject);
				return artworksInfoArray.toJSONString();
			}
		}
		return artworksInfoArray.toJSONString();
	}

	public String getArtworkModel(String artworkName) {

		String queryContent = 
				prefix +
				"SELECT ?componentID WHERE {" +
				"	?artworkID rdfs:label \"" + artworkName + "\" ." +
				"	?artworkID LRM:hasPart ?componentID ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		JSONArray artworkModelArray = new JSONArray();
		JSONArray videoCodecArray = new JSONArray();
		JSONArray containerFormatArray = new JSONArray();
		JSONArray videoPlayerArray = new JSONArray();
		JSONArray operatingSystemArray = new JSONArray();
		JSONArray computerArray = new JSONArray();
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			String object = soln.get("componentID").toString();

			String componentType = getComponentType("<"+object+">");
			if (componentType != "") {
				if (componentType.equals("DigitalVideo")) {
					videoCodecArray.add("Video Codec");
					videoCodecArray.add(getVideoCodecName("<"+object+">"));
					videoCodecArray.add(getVideoCodecRiskType("<"+object+">"));
					videoCodecArray.add(DateTool.getDisplayFormatDate(getVideoCodecPredictedEndDate("<"+object+">")));

					containerFormatArray.add("Container Format");
					containerFormatArray.add(getContainerFormatName("<"+object+">"));
					containerFormatArray.add(getContainerFormatRiskType("<"+object+">"));
					containerFormatArray.add(DateTool.getDisplayFormatDate(getContainerFormatPredictedEndDate("<"+object+">")));
				}
				if (componentType.equals("MediaPlayer")) {
					videoPlayerArray.add("Video Player");
					videoPlayerArray.add(getComponentName("<"+object+">"));
					videoPlayerArray.add(getComponentRiskType("<"+object+">"));
					videoPlayerArray.add(DateTool.getDisplayFormatDate(getComponentPredictedEndDate("<"+object+">")));
				}
				if (componentType.equals("OperatingSystem")) {
					operatingSystemArray.add("Operating System");
					operatingSystemArray.add(getComponentName("<"+object+">"));
					operatingSystemArray.add(getComponentRiskType("<"+object+">"));
					operatingSystemArray.add(DateTool.getDisplayFormatDate(getComponentPredictedEndDate("<"+object+">")));
				}
				if (componentType.equals("Computer")) {
					computerArray.add("Computer");
					computerArray.add(getComponentName("<"+object+">"));
					computerArray.add(getComponentRiskType("<"+object+">"));
					computerArray.add(DateTool.getDisplayFormatDate(getComponentPredictedEndDate("<"+object+">")));
				}
			}
		}

		artworkModelArray.add(videoCodecArray);
		artworkModelArray.add(containerFormatArray);
		artworkModelArray.add(videoPlayerArray);
		artworkModelArray.add(operatingSystemArray);
		artworkModelArray.add(computerArray);
		return artworkModelArray.toJSONString();
	}

	public String getArtworkModel(String collectionName, String artworkName) {
		return getArtworkModel(artworkName);
	}

	public String getArtworkName(String artworkID) {
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?o WHERE {" +
				artworkID + " rdfs:label ?o" +
				"}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			return soln.get("o").toString();
		}
		return "Artwork Name not found";
	}

	public LinkedList<String> getArtworkNamesInCollection(String collectionName) {
		LinkedList<String> artworkNameList = new LinkedList<String>();
		String queryContent = 
				prefix +
				"SELECT ?o WHERE {" +
				"	?c rdfs:label \"" + collectionName + "\" ." +
				"	?a dva:isMemberOf ?c ." +
				"	?a rdfs:label ?o " +
				"}";
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			artworkNameList.add(soln.get("o").toString());
		}

		return artworkNameList;
	}

	public String getArtworkRecoveryInfo(String artworkName) {
		JSONArray recoveryOptionsArray = new JSONArray();
		int index = 1;

		String queryContent = 
				prefix +
				"SELECT ?recoveryOptionID WHERE {" +
				"	<" + getArtworkID(artworkName) + ">" + "dva:hasRecoveryOption ?recoveryOptionID ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		//Iterate each recovery option.
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;

			JSONObject recoveryOptionObject = new JSONObject();
			recoveryOptionObject.put("index", index);
			queryContent = 
					prefix +
					"SELECT ?changeTypeLabel ?changeValueLabel ?predictedEndDate ?impact WHERE {" +
					"	<" + soln.get("recoveryOptionID").toString() + ">" + "dva:hasChange ?changeID ." +
					"   <" + soln.get("recoveryOptionID").toString() + ">" + "dva:hasDateForAction ?predictedEndDate ." +
					"   <" + soln.get("recoveryOptionID").toString() + ">" + "dva:hasImpact ?impact ." +
					"	?changeID dva:hasChangeType ?changeType ." +
					"   ?changeType rdfs:label ?changeTypeLabel ." +
					"	?changeID dva:hasChangeValue ?changeValue ." +
					"   ?changeValue rdfs:label ?changeValueLabel ." +
					"}";

			ResultSet results2 = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);
			
			//Iterate each change in a given recovery option.
			while (results2.hasNext()) {
				QuerySolution soln2 = results2.nextSolution();

				String changeType = soln2.get("changeTypeLabel").toString();

				String changeTypeLabel = "";
				if (changeType.equals("Container")) {
					changeTypeLabel = "Container Format";
				}
				else if (changeType.equals("Media Player")) {
					changeTypeLabel = "Video Player";
				}
				else {
					changeTypeLabel = changeType;
				}

				String changeValue = soln2.get("changeValueLabel").toString();
				String predictedEndDate = soln2.get("predictedEndDate").toString();
				String impact = soln2.get("impact").toString();
				
				if (recoveryOptionObject.get("change") == null) {
					recoveryOptionObject.put("change", changeTypeLabel);
					switch (changeTypeLabel) {
						case "Video Codec" : recoveryOptionObject.put("from", getExistingVideoCodecName(artworkName));
						break;
						case "Container Format" : recoveryOptionObject.put("from", getExistingContainerFormatName(artworkName));
						break;
						case "Video Player" : recoveryOptionObject.put("from", getExistingComponentName(artworkName, "Media Player"));
						break;
						case "Operating System" : recoveryOptionObject.put("from", getExistingComponentName(artworkName, "Operating System"));
						break;
						case "Computer" : recoveryOptionObject.put("from", getExistingComponentName(artworkName, "Computer"));
						break;
					}
				}
				else {
					recoveryOptionObject.put("change", recoveryOptionObject.get("change") + "," + changeTypeLabel);
					switch (changeTypeLabel) {
						case "Video Codec" : recoveryOptionObject.put("from", recoveryOptionObject.get("from") + "," + getExistingVideoCodecName(artworkName));
						break;
						case "Container Format" : recoveryOptionObject.put("from", recoveryOptionObject.get("from") + "," + getExistingContainerFormatName(artworkName));
						break;
						case "Video Player" : recoveryOptionObject.put("from", recoveryOptionObject.get("from") + "," + getExistingComponentName(artworkName, "Media Player"));
						break;
						case "Operating System" : recoveryOptionObject.put("from", recoveryOptionObject.get("from") + "," + getExistingComponentName(artworkName, "Operating System"));
						break;
						case "Computer" : recoveryOptionObject.put("from", recoveryOptionObject.get("from") + "," + getExistingComponentName(artworkName, "Computer"));
						break;
					}
				}
				if (recoveryOptionObject.get("to") == null) {
					recoveryOptionObject.put("to", changeValue);
				}
				else {
					recoveryOptionObject.put("to", recoveryOptionObject.get("to") + "," + changeValue);
				}

				recoveryOptionObject.put("predictedEndDate", DateTool.getDisplayFormatDate(predictedEndDate));

				recoveryOptionObject.put("impact", impact);
			}
			if (validateRecoveryOptionObject(recoveryOptionObject)) {
				recoveryOptionsArray.add(recoveryOptionObject);
				index++;
			}
		}


		return recoveryOptionsArray.toJSONString();

	}


	public String getArtworksInfoFromCollection(String collectionName) {
		JSONArray artworksInfoArray = new JSONArray();

		ResultSet allArtworkNamesResults = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?a WHERE {" +
				"	?c rdfs:label \"" + collectionName +"\" ." +
				"	?a dva:isMemberOf ?c " +
				"}");

		while (allArtworkNamesResults.hasNext()) {
			QuerySolution allArtworkNamesResultsSolution = allArtworkNamesResults.nextSolution() ;

			ResultSet artworkInfoResult = fusekiConnector.executeQuery(fusekiQueryUrl,
					prefix +
					"SELECT ?p ?o WHERE {" +
					"	<"+allArtworkNamesResultsSolution.get("a").toString()+"> ?p ?o" +
					"}");

			JSONObject jsonObject = new JSONObject();
			while (artworkInfoResult.hasNext()) {
				QuerySolution artworkInfoResultSolution = artworkInfoResult.nextSolution();
				String predicate = artworkInfoResultSolution.get("p").toString();
				String object = artworkInfoResultSolution.get("o").toString();

				switch (predicate) {
					case "http://www.w3.org/2000/01/rdf-schema#label" : jsonObject.put("artworkName", object);
					break;
					case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#isMemberOf"	: jsonObject.put("collectionName", getCollectionName("<" + object + ">"));		
					break;
					case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasPrimaryRiskType"	: jsonObject.put("primaryRisk", object);		
					break;
					case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasDateForAction"	: jsonObject.put("dateForAction", DateTool.getDisplayFormatDate(object));	
					break;
					case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasConfidence"	: jsonObject.put("confidence", DateTool.getDisplayFormatConfidence(object));		
					break;
					case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasAccessionDate"	: jsonObject.put("accessionDate", DateTool.getDisplayFormatDate(object));		
					break;
					case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasRiskLevel"	: jsonObject.put("riskLevel", object);		
					break;
					case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasRiskValue"	: jsonObject.put("riskLevel", object);		
					break;
					case "https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#hasImpact"	: jsonObject.put("impact", object);
					break;
					case "http://xrce.xerox.com/LRM#hasPart": {
						String componentType = getComponentType("<"+object+">");
						if (componentType != "") {
							if (componentType.equals("DigitalVideo")) {
								jsonObject.put("videoCodec", getVideoCodecName("<"+object+">"));
								jsonObject.put("containerFormat", getContainerFormatName("<"+object+">"));
							}
							else if (componentType.equals("MediaPlayer")) {
								jsonObject.put("videoPlayer", getComponentName("<"+object+">"));
							}
							else if (componentType.equals("OperatingSystem")) {
								jsonObject.put("operatingSystem", getComponentName("<"+object+">"));
							}
							else if (componentType.equals("Computer")) {
								jsonObject.put("computer", getComponentName("<"+object+">"));
							}
						}
						break;
					}
				}
			}
			artworksInfoArray.add(jsonObject);
		}
		return artworksInfoArray.toJSONString();
	}

	public String getCollectionID(String collectionName) {

		String queryContent = 
				prefix +
				"SELECT ?s WHERE {" +
				"	?s rdfs:label \"" + collectionName + "\"" +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			return soln.get("s").toString();
		}
		return "Collection ID not found";
	}

	public String getCollectionName(String collectionID) {
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				prefix +
				"SELECT ?o WHERE {" +
				collectionID + " rdfs:label ?o" +
				"}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			return soln.get("o").toString();
		}
		return "Collection Name not found";
	}

	public String getComponentID(String componentName) {
		String queryContent =
				prefix +
				"SELECT ?componentID {" +
				"    ?componentID rdfs:label \"" + componentName + "\"" +
				"}";
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			return soln.get("?componentID").toString();
		}
		return "ComponentID not found";
	}

	public String getComponentName(String subject) {
		String queryContent = 
				prefix +
				"SELECT ?o WHERE {" +
				subject + "rdfs:label ?o " +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		if (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			return soln.get("o").toString();
		}

		return "";
	}

	public String getComponentPredictedEndDate(String componentID) {
		String queryContent = 
				prefix +
				"SELECT ?predictedEndDate WHERE {" +
				componentID + "dva:hasDateForAction ?predictedEndDate ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			return soln.get("predictedEndDate").toString();
		}
		return "";
	}

	public String getComponentRiskType(String componentID) {
		String queryContent = 
				prefix +
				"SELECT ?riskType WHERE {" +
				componentID + "dva:hasRiskType ?riskType ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			return soln.get("riskType").toString();
		}
		return "";
	}

	public String getComponentType(String subject) {
		String queryContent = 
				prefix +
				"SELECT ?o WHERE {" +
				subject + "rdf:type ?o " +
				"}";
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		String componentType = "";

		while (results.hasNext()) {

			QuerySolution soln = results.nextSolution() ;

			componentType = soln.get("o").toString();

			if (componentType.contains("dva")) {
				return componentType.substring(componentType.lastIndexOf("#")+1);

			}
		}

		return "";
	}

	public String getComponentTypeInPredicate(String componentName) {
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, 
				"SELECT ?o WHERE {?s ?p \""+componentName+"\" . ?s <http://pericles.org/component/Type> ?o}");

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			String componentType = soln.get("o").toString();
			switch(componentType) {
			case "Video Codec" : return "videoCodec";
			case "Container Format" : return "containerFormat";
			case "Video Player" : return "videoPlayer";
			case "Operating System" : return "operatingSystem";
			case "Computer" : return "computer";
			}
		}

		return "";
	}

	//Sparql query example from http://stackoverflow.com/questions/1223472/sparql-query-and-distinct-count
	public String getComponentTypeStats(String componentTypeName) {

		JSONArray statsArray = new JSONArray();

		ResultSet results = null;

		double totalNumberOfRows = getTotalNumberOfRows(componentTypeName);
		
		if (totalNumberOfRows > 0) {

			String tempQuery = "";
			switch(componentTypeName) {
			case "videoCodec" : tempQuery = "?s dva:hasVideoCodec ?componentID .";
			break;
			case "VideoCodec" : tempQuery = "?s dva:hasVideoCodec ?componentID .";
			break;
			case "containerFormat" : tempQuery = "?s dva:hasContainer ?componentID .";
			break;
			case "Container" : tempQuery = "?s dva:hasContainer ?componentID .";
			break;
			case "videoPlayer" : tempQuery = "?s LRM:hasPart ?componentID . ?componentID rdf:type dva:MediaPlayer .";
			break;
			case "MediaPlayer" : tempQuery = "?s LRM:hasPart ?componentID . ?componentID rdf:type dva:MediaPlayer .";
			break;
			case "operatingSystem" : tempQuery = "?s LRM:hasPart ?componentID . ?componentID rdf:type dva:OperatingSystem .";
			break;
			case "OperatingSystem" : tempQuery = "?s LRM:hasPart ?componentID . ?componentID rdf:type dva:OperatingSystem .";
			break;
			case "computer" : tempQuery = "?s LRM:hasPart ?componentID . ?componentID rdf:type dva:Computer .";
			break;
			case "Computer" : tempQuery = "?s LRM:hasPart ?componentID . ?componentID rdf:type dva:Computer .";
			break;
			}

			String queryContent = 
					prefix +
					"SELECT ?componentID ?componentName (COUNT(?componentID) as ?componentCount) WHERE {" +
					tempQuery + 
					"	?componentID rdfs:label ?componentName" +
					"}" +
					"GROUP BY ?componentID ?componentName";

			results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();

				JSONObject jsonObject = new JSONObject();

				String result = soln.get("componentCount").toString();
				double countOfComponent = Integer.valueOf(result.substring(0, result.indexOf("^")));

				jsonObject.put("label", soln.get("componentName").toString());
				jsonObject.put("data", countOfComponent/totalNumberOfRows*100);

				statsArray.add(jsonObject);
			}
		}

		return statsArray.toString();
	}

	public long getComponentUsedPercentage(String componentName) {

		String componentType = getComponentType("<"+getComponentID(componentName)+">");

		double componentUsedTimes = 0;

		double totalNumberOfRows = 0;
		double tempTotalNumber = 0;

		if (totalNumberOfComponentsMap.get(componentName) != null) {
			componentUsedTimes = totalNumberOfComponentsMap.get(componentName);
		}
		else {
			calculateAllComponentUsedTimes();
			componentUsedTimes = totalNumberOfComponentsMap.get(componentName);
		}

		tempTotalNumber = getTotalNumberOfRows(componentType);
		totalNumberOfRows = tempTotalNumber;
		totalNumberOfComponentsMap.put(componentType, tempTotalNumber);
		
		if (totalNumberOfRows == 0) {
			return 0;
		}

		return Math.round(componentUsedTimes/totalNumberOfRows*100);
	}

	public double getComponentUsedTimes(String componentName) {
		double totalNumberOfRows = 0;
		String tempQuery = "";
		switch(getComponentType("<"+getComponentID(componentName)+">")) {
			case "videoCodec" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?s dva:hasVideoCodec ?componentID";
			break;
			case "VideoCodec" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?s dva:hasVideoCodec ?componentID";
			break;
			case "containerFormat" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?s dva:hasContainer ?componentID";
			break;
			case "Container" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?s dva:hasContainer ?componentID";
			break;
			case "videoPlayer" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:MediaPlayer . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "MediaPlayer" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:MediaPlayer . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "operatingSystem" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:OperatingSystem . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "OperatingSystem" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:OperatingSystem . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "computer" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:Computer . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			case "Computer" : tempQuery = "?componentID rdfs:label \""+componentName+"\" . ?componentID rdf:type dva:Computer . ?digitalVideoID LRM:hasPart ?componentID .";
			break;
			default : return 0;
		}

		String queryContent = 
				prefix +
				"SELECT (count(?componentID) AS ?count) {" +
				tempQuery +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);
		QuerySolution soln = null;
		if (results.hasNext()) {
			soln = results.nextSolution();
			String result = soln.get("count").toString();
			totalNumberOfRows = Double.valueOf(result.substring(0, result.indexOf("^")));
		}
		return totalNumberOfRows;
	}

	public String getContainerFormatName(String subject) {
		String queryContent = 
				prefix +
				"SELECT ?containerFormatName WHERE {" +
				subject + "dva:hasContainer ?containerFormatID . " +
				"	?containerFormatID rdfs:label ?containerFormatName" +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		if (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			return soln.get("containerFormatName").toString();
		}

		return "";
	}

	public String getContainerFormatPredictedEndDate(String digitalVideoID) {
		String queryContent = 
				prefix +
				"SELECT ?predictedEndDate WHERE {" +
				digitalVideoID + "dva:hasContainer ?containerFormatID ." +
				"	?containerFormatID dva:hasDateForAction ?predictedEndDate ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			return soln.get("predictedEndDate").toString();
		}
		return "";
	}

	public String getContainerFormatRiskType(String digitalVideoID) {
		String queryContent = 
				prefix +
				"SELECT ?riskType WHERE {" +
				digitalVideoID + "dva:hasContainer ?containerFormatID ." +
				"	?containerFormatID dva:hasRiskType ?riskType ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			return soln.get("riskType").toString();
		}
		return "";
	}

	public String getDigitalVideoID(String artworkName) {
		String queryContent = 
				prefix +
				"SELECT ?o WHERE {" +
				"	?s rdfs:label \"" + artworkName + "\" . " +
				"	?s LRM:hasPart ?o ." +
				"}";
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {

			QuerySolution soln = results.nextSolution() ;

			if (soln.get("o").toString().contains("digitalvideo")) {
				return "<" + soln.get("o").toString() + ">";
			}
		}

		return "";
	}

	public String getDisplayComponentType(String componentType) {
		String displayComponentType = "";
		switch(componentType) {
		case "VideoCodec" : displayComponentType = "Video Codec";
		break;
		case "Container" : displayComponentType = "Container Format";
		break;
		case "MediaPlayer" : displayComponentType = "Video Player";
		break;
		case "OperatingSystem" : displayComponentType = "Operating System";
		break;
		case "Computer" : displayComponentType = "Computer";
		break;
		}
		return displayComponentType;
	}

	public String getExistingComponentName(String artworkName, String componentType) {
		String queryContent = 
				prefix +
				"SELECT ?componentName WHERE {" +
				"   ?artworkID rdfs:label \"" + artworkName + "\" ." +
				"	?artworkID LRM:hasPart ?componentID ." +
				"   ?componentID rdf:type ?componentType ." +
				"   ?componentType rdfs:label \"" + componentType + "\" ." +
				"   ?componentID rdfs:label ?componentName ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			return soln.get("componentName").toString();
		}
		return "";
	}

	public String getExistingContainerFormatName(String artworkName) {
		String queryContent = 
				prefix +
				"SELECT ?containerFormatName WHERE {" +
				"   ?artworkID rdfs:label \"" + artworkName + "\" ." +
				"	?artworkID LRM:hasPart ?componentID ." +
				"   ?componentID dva:hasContainer ?containerFormatID ." +
				"   ?containerFormatID rdfs:label ?containerFormatName ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			return soln.get("containerFormatName").toString();
		}
		return "";
	}

	public String getExistingVideoCodecName(String artworkName) {
		String queryContent = 
				prefix +
				"SELECT ?videoCodecName WHERE {" +
				"   ?artworkID rdfs:label \"" + artworkName + "\" ." +
				"	?artworkID LRM:hasPart ?componentID ." +
				"   ?componentID dva:hasVideoCodec ?videoCodecID ." +
				"   ?videoCodecID rdfs:label ?videoCodecName ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			return soln.get("videoCodecName").toString();
		}
		return "";
	}

	public int getTotalNumberOfRows(String componentTypeName) {
		String tempQuery = "";
		switch(componentTypeName) {
			case "videoCodec" : tempQuery = "?s dva:hasVideoCodec ?o";
			break;
			case "VideoCodec" : tempQuery = "?s dva:hasVideoCodec ?o";
			break;
			case "containerFormat" : tempQuery = "?s dva:hasContainer ?o";
			break;
			case "Container" : tempQuery = "?s dva:hasContainer ?o";
			break;
			case "videoPlayer" : tempQuery = "?s LRM:hasPart ?o . ?o rdf:type dva:MediaPlayer .";
			break;
			case "MediaPlayer" : tempQuery = "?s LRM:hasPart ?o . ?o rdf:type dva:MediaPlayer .";
			break;
			case "operatingSystem" : tempQuery = "?s LRM:hasPart ?o . ?o rdf:type dva:OperatingSystem .";
			break;
			case "OperatingSystem" : tempQuery = "?s LRM:hasPart ?o . ?o rdf:type dva:OperatingSystem .";
			break;
			case "computer" : tempQuery = "?s LRM:hasPart ?o . ?o rdf:type dva:Computer .";
			break;
			case "Computer" : tempQuery = "?s LRM:hasPart ?o . ?o rdf:type dva:Computer .";
			break;
			default : return 0;
		}

		String queryContent = 
				prefix +
				"SELECT (count(*) AS ?count) {" +
				tempQuery +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		int totalNumberOfRows = 0;
		if (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			String result = soln.get("count").toString();
			totalNumberOfRows = Integer.valueOf(result.substring(0, result.indexOf("^")));
		}

		return totalNumberOfRows;
	}

	public String getVideoCodecName(String subject) {
		String queryContent = 
				prefix +
				"SELECT ?videoCodecName WHERE {" +
				subject + "dva:hasVideoCodec ?videoCodecID . " +
				"	?videoCodecID rdfs:label ?videoCodecName" +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		if (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			return soln.get("videoCodecName").toString();
		}

		return "";
	}

	public String getVideoCodecPredictedEndDate(String digitalVideoID) {
		String queryContent = 
				prefix +
				"SELECT ?predictedEndDate WHERE {" +
				digitalVideoID + "dva:hasVideoCodec ?videoCodecID ." +
				"	?videoCodecID dva:hasDateForAction ?predictedEndDate ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			return soln.get("predictedEndDate").toString();
		}
		return "";
	}

	public String getVideoCodecRiskType(String digitalVideoID) {
		String queryContent = 
				prefix +
				"SELECT ?riskType WHERE {" +
				digitalVideoID + "dva:hasVideoCodec ?videoCodecID ." +
				"	?videoCodecID dva:hasRiskType ?riskType ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();

			return soln.get("riskType").toString();
		}
		return "";
	}

	public boolean isArtworkNameExisted(String artworkNameToCheck) {

		String queryContent = 
				prefix +
				"SELECT ?o WHERE {" +
				"	?s rdf:type dva:DigitalVideoArtwork ." +
				"	?s rdfs:label ?o ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			
			if (soln.get("o").toString().equals(artworkNameToCheck)) {
				return true;
			}
		}

		return false;
	}

	public boolean isCollectionNameExisted(String collectionNameToCheck) {

		String queryContent = 
				prefix +
				"SELECT ?o WHERE {" +
				"	?s a dva:DigitalVideoArtworkCollection ." +
				"	?s rdfs:label ?o ." +
				"}";

		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			if (soln.get("o").toString().equals(collectionNameToCheck)) {
				return true;
			}
		}

		return false;
	}

	public boolean validateRecoveryOptionObject(JSONObject recoveryOptionObject) {
		if (recoveryOptionObject.get("index") != null && recoveryOptionObject.get("change") != null && recoveryOptionObject.get("to") != null && recoveryOptionObject.get("predictedEndDate") != null) {
			return true;
		}
		return false;
	}

	public void writeTriplesToServer(String triples) {
		String updateContent = 
				prefix +
				"INSERT DATA { " +
				triples +
				"}";

		fusekiConnector.executeUpdate(fusekiUpdateUrl, updateContent);
	}

	@Override
	public boolean isComponentExisted(String componentName) {
		
		String queryContent = 
				prefix +
				"SELECT ?componentID WHERE {" +
				"?componentID rdfs:label \"" + componentName +"\". " +
				"}";
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);
		
		while (results.hasNext()) {
			return true;
		}
		
		return false;
	}
	
	public boolean isComponentsCompatible(String firstComponentName, String secondComponentName) {
		
		String queryContent = 
				prefix +
				"SELECT ?secondComponentName WHERE {" +
				"?firstComponentID rdfs:label \"" + firstComponentName +"\". " +
				"?softwareDependencyID rdf:type dva:SoftwareDependency ;" +
				"dva:hasWeight ?weightValue ;" +
				"LRM:from ?firstComponentID ;" +
				"LRM:to ?secondComponentID ." +
				"?secondComponentID rdfs:label ?secondComponentName ." +
				"FILTER (?weightValue >= 0.5)" +
				"}";
		ResultSet results = fusekiConnector.executeQuery(fusekiQueryUrl, queryContent);
		
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			if (soln.get("secondComponentName").toString().equals(secondComponentName)) {
				return true;
			}
		}
		
		return false;
	}
}
