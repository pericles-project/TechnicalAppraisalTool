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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.simple.JSONObject;

/**
 * extracts video file metadata with the tool "MediaInfo". 
 * More information about MediaInfo available at https://mediaarea.net/en/MediaInfo
 */
public class VideoFileMetadataExtractor {
	
	/**
	 * Gets metadata of the specified video file.
	 * @param mediaInfoFolderPath the folder path of the MediaInfo tool.
	 * @param videoFilePath the file path of the video file. 
	 * @return a list which listing all the extracted metadata.
	 */
	public List<String> getVideoFileMetadata(String mediaInfoFolderPath, String videoFilePath) {
		List<String> systemOutputs = new LinkedList<String>();
		try{
			String line;
			Process p = null;
			if (System.getProperty("os.name").contains("Windows")) {
				p = Runtime.getRuntime().exec("cmd /c cd " + mediaInfoFolderPath + " && MediaInfo.exe \"" + videoFilePath + "\"");
			}
			else if (System.getProperty("os.name").contains("Linux")) {
				p = Runtime.getRuntime().exec("mediainfo " + videoFilePath);
			}
			
			BufferedReader bri = new BufferedReader
					(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader
					(new InputStreamReader(p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				System.out.println(line);
				systemOutputs.add(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				System.out.println(line);
			}
			bre.close();
			p.waitFor();
		} catch(Exception e){
		}
		return systemOutputs;
	}
}
