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

import java.util.Iterator;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pericles.appraisaltool.sciencedemo.util.PropertiesFileReader;

public class ERMRCommandsExecutor {
	
	private final String ermrQueryUrl;
	private final String ermrUpdateUrl;
	private final String queryPrefix = "PREFIX dva: <https://dl.dropboxusercontent.com/u/27469926/dva_t.owl#>" +
									   "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
									   "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
									   "PREFIX LRM: <http://xrce.xerox.com/LRM#>" +
									   "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
									   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";
	private final ERMRConnector ermrConnector;
	private final PropertiesFileReader propertiesFileReader;
	
	public ERMRCommandsExecutor(ERMRConnector ermrConnector,
			                    PropertiesFileReader propertiesFileReader) {
		this.ermrConnector = ermrConnector;
		this.propertiesFileReader = propertiesFileReader;
		this.ermrQueryUrl = propertiesFileReader.getPropertyValue("ermrQueryUrl");
		this.ermrUpdateUrl = propertiesFileReader.getPropertyValue("ermrUpdateUrl");
	}
	
	public JSONArray parseQueryResults(String queryResults) {
		
		JSONArray jsonArray = new JSONArray();
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(queryResults);
			jsonArray.add(jsonObject.get("names"));
			
			Iterator<String> iterator = ((JSONArray) jsonObject.get("values")).iterator();
	        while (iterator.hasNext()) {
	            jsonArray.add(iterator.next());
	        }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return jsonArray;
	}
	
	public String getCollectionID(String collectionName) {
		
		String results = ermrConnector.executeQuery(ermrQueryUrl, 
			queryPrefix +
			"SELECT ?s WHERE {" +
			"	?s rdfs:label \"" + collectionName + "\"" +
			"}");
		
		String collectionID = "";
		
		if (results != null && results != "") {
			collectionID = ((JSONArray) parseQueryResults(results).get(1)).get(0).toString();
		}
		
		return collectionID;
	}
}
