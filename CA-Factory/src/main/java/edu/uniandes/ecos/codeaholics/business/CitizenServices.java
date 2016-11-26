/** Copyright or License
 *
 */
package edu.uniandes.ecos.codeaholics.business;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.Constants;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DocumentSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc.EmailType;
import edu.uniandes.ecos.codeaholics.config.ExternalSvcInvoker;
import edu.uniandes.ecos.codeaholics.config.FileUtil;
import edu.uniandes.ecos.codeaholics.config.IDocumentSvc;
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;
import edu.uniandes.ecos.codeaholics.config.Routes;
import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.InvalidTokenException;
import edu.uniandes.ecos.codeaholics.persistence.Activity;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import edu.uniandes.ecos.codeaholics.persistence.Procedure;
//import edu.uniandes.ecos.codeaholics.persistence.ProcedureData;
import edu.uniandes.ecos.codeaholics.persistence.ProcedureRequest;
import edu.uniandes.ecos.codeaholics.persistence.RequiredDocument;
import spark.Request;
import spark.Response;

public class CitizenServices {

	private static Gson GSON = new GsonBuilder().serializeNulls().create();

	private static IMessageSvc messager = new ResponseMessage();

	private final static Logger log = LogManager.getLogger(CitizenServices.class);

	private static IDocumentSvc fileManager = new DocumentSvc();

	/***
	 * Obtiene el modelo del tramite para ser iniciado.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return lista con json del modelo del tramite
	 */
	public static Object getProcedure(Request pRequest, Response pResponse) {

		String mayoraltyName = pRequest.params(":mayoraltyName");
		String procedureName = pRequest.params(":procedureName");

		List<Document> dataset = new ArrayList<>();

		Document procedureFilter = new Document();
		procedureFilter.append("mayoraltyslug", mayoraltyName);
		procedureFilter.append("slug", procedureName);

		ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, Constants.PROCEDURE_COLLECTION);

		if (documents.isEmpty()) {
			log.info("No data found for " + pRequest.params(":procedureName"));
		}

		for (Document item : documents) {
			dataset.add(item);
		}

		log.info("getProcedure done");

		pResponse.type("application/json");
		return dataset;

	}

	/***
	 * Obtiene toda la informacion de un ciudadano dado su numero de
	 * identificacion.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return json informacion disponible del ciudadano
	 */
	public static Object getCitizenDetail(Request pRequest, Response pResponse) {

		Document filter = new Document();
		filter.append("identification", Integer.parseInt(pRequest.params("identification")));
		filter.append("name", Integer.parseInt(pRequest.params("identification")));

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(filter, Constants.CITIZEN_COLLECTION);
		for (Document item : documents) {
			item.remove("password");
			item.remove("salt");
			dataset.add(item);
		}

		pResponse.type("application/json");
		return dataset;
	}

	/***
	 * Cierra la sesion de un usuario
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return json con mensaje de exito o de falla
	 */
	public static Object closeSession(Request pRequest, Response pResponse) {

		Object response = null;

		try {
			String email = Authorization.getFromToken(pRequest, Constants.TOKEN_EMAIL_KEY);

			if (email != null) {
				Authentication.closeSession(email);
				response = messager.getOkMessage("Proceso Exitoso");
			} else {
				pResponse.status(417);
				response = messager.getNotOkMessage("El correo es necesario");
			}
		} catch (JsonSyntaxException | InvalidTokenException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

		pResponse.type("application/json");
		return response;
	}

	/***
	 * Registra la solicitud de un tramite y toda la infromacion asocida a esa
	 * solicitud.
	 * 
	 * @param pRequest
	 *            request (mayoralty name and procedure name are send inside the
	 *            route adn the citizen's email is send as a parameter)
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object startProcedure(Request pRequest, Response pResponse) {

		Object response = null;

		// .
		String email;

		try {
			email = Authorization.getFromToken(pRequest, Constants.TOKEN_EMAIL_KEY);
		} catch (InvalidTokenException jwtEx) {
			log.error(jwtEx.getMessage());
			return "failed";
		}
		// ..

		String barcodeImg = null;

		ProcedureRequest procedureRequest = new ProcedureRequest();

		try {

			ExternalSvcInvoker.invoke(Routes.BARCODER_EXTSVC_ROUTE);
			JsonObject json = (JsonObject) ExternalSvcInvoker.getResponse();
			procedureRequest.setFileNumber(json.get("code").getAsString());

			barcodeImg = FileUtil.getImageFromString(json.get("code").getAsString(),
					json.get("barcodeImg").getAsString());

		} catch (FileNotFoundException | UnknownHostException ex) {
			log.info("Problem reaching external service");
			procedureRequest.setFileNumber(UUID.randomUUID().toString());
		}

		String mayoraltyName = pRequest.params(":mayoraltyName");
		String procedureName = pRequest.params(":procedureName");

		Document procedureFilter = new Document();
		procedureFilter.append("mayoraltyslug", mayoraltyName);
		procedureFilter.append("slug", procedureName);

		// ...
		try {

			ArrayList<Document> procedures = DataBaseUtil.find(procedureFilter, Constants.PROCEDURE_COLLECTION);

			Document procedureDoc = procedures.get(0);
			procedureDoc.remove("_id");

			Procedure procedure = GSON.fromJson(procedureDoc.toJson(), Procedure.class);
			procedureRequest.setProcedureClassName(procedure.getName());
			procedureRequest.setActivities(procedure.getActivities());
			procedureRequest.getActivities().get(0);

			for (Activity activity : procedureRequest.getActivities()) {
				if (activity.getStep() == 1)
					activity.setStatus("En curso");
			}

			Document citizenFilter = new Document();

			citizenFilter.append("email", email);

			ArrayList<Document> citizens = DataBaseUtil.find(citizenFilter, Constants.CITIZEN_COLLECTION);

			Document citizenDoc = citizens.get(0);
			citizenDoc.remove("_id");
			citizenDoc.remove("birthDate"); // AO: Need to deal with
											// ISODate --> Java

			Citizen citizen = GSON.fromJson(citizenDoc.toJson(), Citizen.class);
			procedureRequest.setCitizen(citizen);

			log.debug("Found citizen " + citizen.toDocument());

			procedureRequest.setMayoralty(procedureDoc.get("mayoralty").toString());

			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(pRequest.body()).getAsJsonObject();
			JsonObject jsonData = (JsonObject) json.get("dataForm");
			JsonObject jsonDocs = (JsonObject) json.get("docs");

			Document procedureData = GSON.fromJson(jsonData, Document.class);
			procedureRequest.setProcedureData(procedureData);

			Document deliveryDocs = GSON.fromJson(jsonDocs, Document.class);
			procedureRequest.setDeliveryDocs(deliveryDocs);

			procedureRequest.setStatus("En proceso");
			procedureRequest.setStartDate(new Date());

			System.out.println(procedureRequest.toDocument());
			DataBaseUtil.save(procedureRequest.toDocument(), Constants.PROCEDUREREQUEST_COLLECTION);

			// .... Notify to citizen
			ArrayList<String> parameters = new ArrayList<>();
			parameters.add(procedureRequest.getProcedureClassName());
			parameters.add(procedureRequest.getFileNumber());

			EmailNotifierSvc sendEmail = new EmailNotifierSvc();
			sendEmail.send(EmailType.INITPROCEDURE, citizen.getEmail(), parameters, barcodeImg);

			response = messager
					.getOkMessage("Registro exitoso de su solicitud, su tr\u00E1mite fue creado con el n\u00FAmero: "
							+ procedureRequest.getFileNumber());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("There is a problem with starting a new procedure");
			log.info(e.getLocalizedMessage());
			response = messager.getNotOkMessage("Registro de solicitud fallido");
		}

		pResponse.type("application/json");

		return response;

	}

	/***
	 * Consulta tramites de un ciudadano.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object consultProcedureRequets(Request pRequest, Response pResponse) {

		// .
		String email;

		try {
			email = Authorization.getFromToken(pRequest, Constants.TOKEN_EMAIL_KEY);
		} catch (InvalidTokenException jwtEx) {
			log.info(jwtEx.getMessage());
			return "failed";
		}
		// ..

		Document procedureFilter = new Document();
		procedureFilter.append("citizen.email", email);

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, Constants.PROCEDUREREQUEST_COLLECTION);

		for (Document item : documents) {
			dataset.add(item);
		}

		// ayuda para probar el servicio
		if (documents.isEmpty()) {
			Document procedure1 = new Document();
			procedure1.put("id", 1);
			procedure1.put("name", "sebas");
			procedure1.put("department", "Caldas");
			procedure1.put("city", "Palestina");
			procedure1.put("status", "Finalizado");
			dataset.add(procedure1);

			log.info(dataset);

			Document procedure2 = new Document();
			procedure2.put("id", 2);
			procedure2.put("name", "Jeison");
			procedure2.put("department", "Cundinamarca");
			procedure2.put("city", "Bogota");
			procedure2.put("status", "En proceso");
			dataset.add(procedure2);

			log.info(dataset);
		}

		return dataset;
	}

	/***
	 * Consulta estado de un tramite.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object consultProceduresById(Request pRequest, Response pResponse) {

		log.info(pRequest.params(":id"));
		log.info(pRequest.uri());

		Document procedureFilter = new Document();

		String email;

		try {
			email = Authorization.getFromToken(pRequest, Constants.TOKEN_EMAIL_KEY);
		} catch (InvalidTokenException jwtEx) {
			log.info(jwtEx.getMessage());
			return "failed";
		}

		procedureFilter.append("citizen.email", email);

		procedureFilter.append("fileNumber", pRequest.params(":id"));

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, Constants.PROCEDUREREQUEST_COLLECTION);
		for (Document item : documents) {
			dataset.add(item);
		}

		log.info("Consult procedure by id done");

		return dataset;
	}

	/***
	 * Consulta estado de un tramite.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object consultProceduresDocuments(Request pRequest, Response pResponse) {
		try {
			log.info(pRequest.params(":id"));
			log.info(pRequest.uri());
			log.info("Changing procedure status: " + pRequest.body());
			Document procedureFilter = new Document();

			String email;
			email = Authorization.getFromToken(pRequest, Constants.TOKEN_EMAIL_KEY);

			procedureFilter.append("citizen.email", email);
			procedureFilter.append("fileNumber", pRequest.params(":procedureId"));

			List<Document> dataset = new ArrayList<>();
			ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, Constants.PROCEDUREREQUEST_COLLECTION);
			
			Document document = documents.get(0);
			document.remove("_id");
			document.remove("startDate");
			document.remove("finishDate");
			
			ProcedureRequest procedureR = GSON.fromJson(document.toJson(), ProcedureRequest.class);
			
			log.info("deliveryDocs: "+ procedureR.getDeliveryDocs().toJson());
			RequiredDocument obj= procedureR.getDeliveryDocs().get("Cédula de Ciudadanía",RequiredDocument.class);
			//procedureR.getDeliveryDocs().ge
			
			if (!obj.equals(null)){
				RequiredDocument req = (RequiredDocument)obj;
				
			}
			
			log.info("Consult procedure by id done");

			return dataset;
		} catch (InvalidTokenException jwtEx) {
			log.info(jwtEx.getMessage());
			return "failed";
		}catch (Exception e) {
			log.info(e.getMessage());
			return "failed";
		}
	}

	/***
	 * Consulta estado de tramites que se solicitaron en un rango de fechas.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object consultProceduresByDate(Request pRequest, Response pResponse) {
		Object response;
		response = messager.getOkMessage("Proceso Exitoso");
		return response;
	}

	/**
	 * Servicio para subir documentos
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	public static Object uploadDocuments(Request pRequest, Response pResponse) {

		Object response = null;

		log.info(pRequest.toString());
		try {
			log.info("headers: " + pRequest.headers());
			String citizen = pRequest.headers("citizen");
			String fileRequest = pRequest.headers("fileRequest");

			log.info("nombre del req: " + fileRequest);
			log.info("ciuda: " + citizen);

			// String nameFile = fileRequest + citizen;

			fileManager.uploadDocument(pRequest);
			response = messager.getOkMessage(((DocumentSvc) fileManager).getAnswerStr());

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

		return response;

	}

	/**
	 * Metodo que se encarga de descargar los documento.
	 * 
	 * @param pRequest
	 *            Request
	 * @param pResponse
	 *            Response
	 * @return json object message
	 */
	public static Object downloadDocuments(Request pRequest, Response pResponse) {

		Object response = null;

		try {

			response = fileManager.downloadDocument(pRequest, pResponse);

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

		return response;

	}

	/**
	 * Metodo que envia la lista de documentos almacenados
	 *
	 * @param pRequest
	 *            Request
	 * @param pResponse
	 *            Response
	 * @return json object message
	 */
	public static Object listDocuments(Request pRequest, Response pResponse) {

		Object response = null;

		try {

			response = fileManager.listDocuments();

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;

	}

}
