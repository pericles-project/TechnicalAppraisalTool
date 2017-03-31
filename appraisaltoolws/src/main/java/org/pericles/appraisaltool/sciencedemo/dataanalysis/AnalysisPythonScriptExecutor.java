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

package org.pericles.appraisaltool.sciencedemo.dataanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.pericles.appraisaltool.sciencedemo.util.OperatingSystemChecker;
import org.pericles.appraisaltool.sciencedemo.util.PropertiesFileReader;

public class AnalysisPythonScriptExecutor {
	
	private final PropertiesFileReader propertiesFileReader;
	
	public AnalysisPythonScriptExecutor(PropertiesFileReader propertiesFileReader) {
		this.propertiesFileReader = propertiesFileReader;
	}
	
	public LinkedList<String> executeScript(String pythonCommand, String analysisScriptPath, String analysisScriptName, String parameters) {
		try{
			LinkedList<String> printouts = new LinkedList<String>();
			
			String line;
			
			System.out.println("Start to execute the Analysis Python script.");
			
			Process p = null;
			
			synchronized(this) {
				if (OperatingSystemChecker.INSTANCE.isWindows()) {
					System.out.println("cmd /c cd " + analysisScriptPath +
						" && " + pythonCommand + " " + analysisScriptName + " " + parameters +
						" && exit"
					);
					
					p = Runtime.getRuntime().exec(
						"cmd /c cd " + analysisScriptPath +
						" && " + pythonCommand + " " + analysisScriptName + " " + parameters +
						" && exit"
					);
				}
				else if (OperatingSystemChecker.INSTANCE.isLinux()) {
					System.out.println(
						pythonCommand + " " + analysisScriptName + " " + parameters
					);
					
					p = Runtime.getRuntime().exec(
						pythonCommand + " " + analysisScriptName + " " + parameters, null, new File(analysisScriptPath)
					);
					
				}
			}
			
			BufferedReader bri = new BufferedReader
					(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader
					(new InputStreamReader(p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				System.out.println(line);
				printouts.add(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				System.out.println(line);
				printouts.add(line);
			}
			bre.close();
			p.waitFor();
			System.out.println("Finished executing the Analysis Python script.");
			return printouts;
		} catch(Exception e){
			
		}
		return null;
	}
}