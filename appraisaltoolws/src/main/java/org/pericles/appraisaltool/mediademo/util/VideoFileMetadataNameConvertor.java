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

package org.pericles.appraisaltool.mediademo.util;

public class VideoFileMetadataNameConvertor {
	public static String getConvertedName(String metadataName) {
		switch(metadataName.toLowerCase()) {
			case "avc": case "avc1": case "h264": case "h.264": return "H264";
			case "mpeg-4": case"mpeg visual": return "MPEG-4 Part 14";
			case "realvideo 4": return "RealVideo";
		}
		return metadataName;
	}
}
