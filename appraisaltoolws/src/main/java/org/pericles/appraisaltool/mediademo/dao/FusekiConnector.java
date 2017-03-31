/**
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

import java.util.UUID;

import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;

public class FusekiConnector {
	
	public ResultSet executeQuery(String fusekiQueryUrl, String query) {
		ResultSet results = null;
		
		QueryExecution qe = QueryExecutionFactory.sparqlService(fusekiQueryUrl, query);
		results = qe.execSelect();

		return results;
	}
	
	public void executeUpdate(String fusekiUpdateUrl, String updateContent) {
		String id = UUID.randomUUID().toString();

		UpdateProcessor upp = UpdateExecutionFactory.createRemote(
			UpdateFactory.create(String.format(updateContent, id)), 
	        fusekiUpdateUrl);
	    
		upp.execute();
	}
}

