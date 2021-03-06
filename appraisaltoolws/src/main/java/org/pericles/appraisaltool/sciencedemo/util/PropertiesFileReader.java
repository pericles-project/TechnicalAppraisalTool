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

package org.pericles.appraisaltool.sciencedemo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Gets data from properties file. 
 * The default properties file is located in the folder "src/main/resources/".
 * The default properties file name is "config.properties".
 * 
 * @author Jun Zhang
 */
public class PropertiesFileReader {
	
	/**
	 * Gets the property value of the specified property.
	 * @param propertyName the name of the property
	 * @return the value of the property
	 */
	public String getPropertyValue(String propertyName) {
		InputStream inputStream = PropertiesFileReader.class.getResourceAsStream("/config.properties");
		Properties properties = new Properties();
		String propertyValue = "";
		try {
			properties.load(inputStream);
			propertyValue = properties.getProperty(propertyName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return propertyValue;  
	}
}
