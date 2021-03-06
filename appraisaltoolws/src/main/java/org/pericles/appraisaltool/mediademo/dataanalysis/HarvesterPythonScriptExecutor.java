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

package org.pericles.appraisaltool.mediademo.dataanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.pericles.appraisaltool.mediademo.util.OperatingSystemChecker;
import org.pericles.appraisaltool.mediademo.util.PropertiesFileReader;



public class HarvesterPythonScriptExecutor {
	
	private final PropertiesFileReader propertiesFileReader;
	
	public HarvesterPythonScriptExecutor(PropertiesFileReader propertiesFileReader) {
		this.propertiesFileReader = propertiesFileReader;
	}

	public void executeScript(String pythonCommand, String harvestScriptPath, String harvestScriptName, String parameters) {
		try{
			String line;
			Process p = null;
			
			System.out.println("Start to execute the harvest Python script.");
			
			synchronized(this) {
				if (OperatingSystemChecker.INSTANCE.isWindows()) {
					System.out.println(
						"cmd /c cd " + harvestScriptPath +
						" && "+ pythonCommand + " " + harvestScriptName + " " + parameters +
						" && exit"
					);
					
					p = Runtime.getRuntime().exec(
						"cmd /c cd " + harvestScriptPath +
						" && "+ pythonCommand + " " + harvestScriptName + " " + parameters +
						" && exit"
					);
				}
				else {
					System.out.println(
						pythonCommand + " " + harvestScriptName + " " + parameters
					);
					
					p = Runtime.getRuntime().exec(
						pythonCommand + " " + harvestScriptName + " " + parameters, null, new File(harvestScriptPath)
					);
				}
			}
			
			BufferedReader bri = new BufferedReader
					(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader
					(new InputStreamReader(p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				System.out.println(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				System.out.println(line);
			}
			bre.close();
			p.waitFor();
			System.out.println("Finished executing the Python script.");
		} catch(Exception e){
			
		}
	}
}
