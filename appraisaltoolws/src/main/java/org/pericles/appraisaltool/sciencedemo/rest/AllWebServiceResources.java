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

package org.pericles.appraisaltool.sciencedemo.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.pericles.appraisaltool.sciencedemo.logic.WSRequestsHandler;
import org.pericles.appraisaltool.sciencedemo.util.KeyValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Path("/v1/sciencedemo")
@Singleton
public class AllWebServiceResources {
	private ApplicationContext context = null;
	private WSRequestsHandler wsRequestsHandler = null;
	
	public AllWebServiceResources() {
		context = new ClassPathXmlApplicationContext("SpringConfig.xml");
		wsRequestsHandler = (WSRequestsHandler) context.getBean("sciencedemo_wsRequestsHandler");
	}
	
	@Path("/version")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getVersion() {
		return "1.0";
	}
	
	@Path("/collectionview/allartworksinfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllArtworksInfo() {
		return wsRequestsHandler.getAllArtworksInfo();
	}
	
	@Path("/collectionview/allcollectionnames")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCollectionNames() {
		return wsRequestsHandler.getAllCollectionNames();
	}
	
	@Path("/collectionview/{collectionName}/artworksinfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getArtworksInfo(@PathParam("collectionName") String collectionName) {
		return wsRequestsHandler.getArtworksInfo(collectionName);
	}
	
	@Path("/collectionview/{artworkName}/artworkmodel")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getArtworkModel(@PathParam("artworkName") String artworkName) {
		return wsRequestsHandler.getArtworkModel(artworkName);
	}

	@Path("/collectionview/{artworkName}/recoveryinfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getArtworkRecoveryInfoInJson(@PathParam("artworkName") String artworkName) {
		return wsRequestsHandler.getArtworkRecoveryInfo(artworkName);
	}
	
	@Path("/componentview/stats/{componentTypeName}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getComponentStats(@PathParam("componentTypeName") String componentTypeName) {
		return wsRequestsHandler.getComponentTypeStats(componentTypeName);
	}
	
	@Path("/componentview/allcomponentsinfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllComponentsInfo() {
		return wsRequestsHandler.getAllComponentsInfo();
	}
	
	@Path("/creation/collectioncreation")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createNewCollection(@FormParam("newCollectionName") String newCollectionName) throws IOException {
		KeyValue<Integer, String> creationResult = wsRequestsHandler.createNewCollection(newCollectionName);
		
		return Response.status(creationResult.getKey().intValue()).entity(creationResult.getValue()).build();
	}
	
	@Path("/creation/artworkcreation")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createNewArtwork(@FormParam("newArtworkName") String newArtworkName,
									 @FormParam("collectionNamesSelection") String collectionNamesSelection,
									 @FormParam("accessionDateInput") String accessionDateInput,
									 @FormParam("databaseNamesSelection") String databaseNamesSelection,
									 @FormParam("scriptingLanguageNamesSelection") String scriptingLanguageNamesSelection,
									 @FormParam("imageFormatNamesSelection") String imageFormatNamesSelection,
									 @FormParam("imageViewerNamesSelection") String imageViewerNamesSelection,
									 @FormParam("documentFormatNamesSelection") String documentFormatNamesSelection,
									 @FormParam("documentViewerNamesSelection") String documentViewerNamesSelection) throws IOException {
		
		HashMap<String, String> artworkParameters = new HashMap<String, String>();
		artworkParameters.put("collectionName", collectionNamesSelection.replace("_", " "));
		artworkParameters.put("artworkName", newArtworkName);
		artworkParameters.put("accessionDate", accessionDateInput);
		
		artworkParameters.put("database", databaseNamesSelection.replace("_", " "));
		artworkParameters.put("scriptingLanguage", scriptingLanguageNamesSelection.replace("_", " "));
		artworkParameters.put("imageFormat", imageFormatNamesSelection.replace("_", " "));
		artworkParameters.put("imageViewer", imageViewerNamesSelection.replace("_", " "));
		artworkParameters.put("documentFormat", documentFormatNamesSelection.replace("_", " "));
		artworkParameters.put("documentViewer", documentViewerNamesSelection.replace("_", " "));
		
		KeyValue<Integer, String> result = wsRequestsHandler.createNewArtwork(artworkParameters);
		
		return Response.status(result.getKey().intValue()).entity(result.getValue()).build();
	}
	
	@Path("/update/artwork")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateArtwork(@FormParam("artworkName") String artworkName) throws IOException {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Response.status(200).entity("Collection <b>"+artworkName+"</b> updated.").build();
	}
	
	@Path("/update/allartworksupdate")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String updateAllArtworks() throws IOException {
		return wsRequestsHandler.updateAllArtworks();
	}
	
	@Path("/update/allartworksupdatestatus")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getAllArtworksUpdateStatus() throws IOException {
		return wsRequestsHandler.getAllArtworksUpdateStatus();
	}
	
	@Path("/update/artworkrecovery")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response applyArtowrkRecovery(@FormParam("artworkName") String artworkName,
			 							 @FormParam("newDatabase") String newDatabase,
			                             @FormParam("newScriptingLanguage") String newScriptingLanguage,
			                             @FormParam("newImageFormat") String newImageFormat,
			                             @FormParam("newImageViewer") String newImageViewer,
			                             @FormParam("newDocumentFormat") String newDocumentFormat,
			                             @FormParam("newDocumentViewer") String newDocumentViewer) throws IOException {
		
		KeyValue<Integer, String> result = wsRequestsHandler.applyRecoveryOption(artworkName, 
																					  newDatabase,
																					  newScriptingLanguage,
																					  newImageFormat, 
																					  newImageViewer, 
																					  newDocumentFormat,
																					  newDocumentViewer);
		
		return Response.status(200).entity("Collection <b>"+artworkName+"</b> updated.").build();
	}
	
	@Path("/deletion/collectiondeletion")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteCollection(@FormParam("collectionName") String collectionName) throws IOException {
		KeyValue<Integer, String> result = wsRequestsHandler.deleteCollection(collectionName);
		
		return Response.status(result.getKey().intValue()).entity(result.getValue()).build();
	}
	
	@Path("/deletion/artworkdeletion")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteArtwork(@FormParam("artworkName") String artworkName) throws IOException {
		
		KeyValue<Integer, String> result = wsRequestsHandler.deleteArtwork(artworkName);
		
		return Response.status(result.getKey().intValue()).entity(result.getValue()).build();
	}
	
	@Path("/deletion/artworksdeletion")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteArtworks(@FormParam("artworkNames") String artworkNames) throws IOException {
		
		KeyValue<Integer, String> result = wsRequestsHandler.deleteArtworks(artworkNames);
		
		return Response.status(result.getKey().intValue()).entity(result.getValue()).build();
	}
	
	@Path("/database/alldatabasenames")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllVideoPlayerNames() {
		return wsRequestsHandler.getAllDatabaseNames();
	}
	
	@Path("/scriptinglanguage/allscriptinglanguagenames")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllScriptingLanguageNames() {
		return wsRequestsHandler.getAllScriptingLanguageNames();
	}
	
	@Path("/imageformat/allimageformatnames")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllImageFormatNames() {
		return wsRequestsHandler.getAllImageFormatNames();
	}
	
	@Path("/imageviewer/allimageviewernames")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllimageviewerNames() {
		return wsRequestsHandler.getAllImageViewerNames();
	}
	
	@Path("/documentformat/alldocumentformatnames")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllDocumentFormatNames() {
		return wsRequestsHandler.getAllDocumentFormatNames();
	}
	
	@Path("/documentviewer/alldocumentviewernames")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllDocumentViewerNames() {
		return wsRequestsHandler.getAllDocumentViewerNames();
	}
}
