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

public enum OperatingSystemChecker {
	
	INSTANCE;
	
	private final String os;
	
	OperatingSystemChecker() {
		os = System.getProperty("os.name");
	}
	
	public String getOsName() {
		return os;
	}
	
	public boolean isWindows() {
		return os.startsWith("Windows");
	}

	public boolean isUnix() {
		return os.startsWith("Unix");
	}
	
	public boolean isLinux() {
		return os.startsWith("Linux");
	}
}
