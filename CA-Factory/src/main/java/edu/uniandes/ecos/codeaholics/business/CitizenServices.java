package edu.uniandes.ecos.codeaholics.business;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DocumentSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc.EmailType;
import edu.uniandes.ecos.codeaholics.config.IDocumentSvc;
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;

import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.InvalidTokenException;

import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import edu.uniandes.ecos.codeaholics.persistence.Procedure;
import edu.uniandes.ecos.codeaholics.persistence.ProcedureRequest;

import spark.Request;
import spark.Response;

public class CitizenServices {

	private static Gson GSON = new GsonBuilder().serializeNulls().create();

	private static IMessageSvc messager = new ResponseMessage();

	private final static Logger log = LogManager.getLogger(CitizenServices.class);

	private static IDocumentSvc fileManager = new DocumentSvc();

	private static String CITIZEN = "citizen";

	private static String PROCEDURESREQUEST = "proceduresRequest";

	private static String PROCEDURES = "procedures";

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

		List<Document> dataset = new ArrayList<>();

		Document procedureFilter = new Document();
		procedureFilter.append("slug", pRequest.params(":procedureName"));

		ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, PROCEDURES);

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
	 * Obtiene la lista de todos los ciudadanos registrados en el sistema.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return lista con json por cada ciudadano
	 */
	public static Object getCitizenList(Request pRequest, Response pResponse) {

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.getAll(CITIZEN);
		String fullName = "";
		for (Document item : documents) {
			fullName = item.get("name").toString() + " " + item.get("lastName1").toString() + " "
					+ item.get("email").toString();
			item.remove("name");
			item.remove("lastName1");
			item.remove("lastName2");
			item.remove("password");
			item.remove("salt");
			item.remove("birthDate");
			item.remove("email");
			item.put("fullName", fullName);
			dataset.add(item);

		}

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
		ArrayList<Document> documents = DataBaseUtil.find(filter, CITIZEN);
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

			// .
			String email = Authorization.getFromToken(pRequest, Authorization.TOKEN_EMAIL_KEY);
			// ..

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

		ProcedureRequest procedureRequest = new ProcedureRequest();
		procedureRequest.setFileNumber(UUID.randomUUID().toString());

		String procedureName = pRequest.params(":procedureName");
		Document procedureFilter = new Document();
		procedureFilter.append("slug", procedureName);

		try {

			ArrayList<Document> procedures = DataBaseUtil.find(procedureFilter, PROCEDURES);

			Document procedureDoc = procedures.get(0);
			procedureDoc.remove("_id");
			Procedure procedure = GSON.fromJson(procedureDoc.toJson(), Procedure.class);
			procedureRequest.setProcedureClassName(procedure.getName());
			procedureRequest.setActivities(procedure.getActivities());

			Document citizenFilter = new Document();

			// .
			String email;

			try {
				email = Authorization.getFromToken(pRequest, Authorization.TOKEN_EMAIL_KEY);
			} catch (InvalidTokenException jwtEx) {
				log.info(jwtEx.getMessage());
				return "failed";
			}

			citizenFilter.append("email", email);
			// ..
			// citizenFilter.append("email", pRequest.queryParams("email"));

			ArrayList<Document> citizens = DataBaseUtil.find(citizenFilter, CITIZEN);

			Document citezenDoc = citizens.get(0);
			citezenDoc.remove("_id");
			Citizen citizen = GSON.fromJson(citezenDoc.toJson(), Citizen.class);
			procedureRequest.setCitizen(citizen);

			String mayoraltyName = pRequest.params(":mayoraltyName");
			procedureRequest.setMayoralty(mayoraltyName);

			Document procedureData = GSON.fromJson(pRequest.body(), Document.class);
			procedureRequest.setProcedureData(procedureData);

			Document deliveryDocs = new Document();
			procedureRequest.setDeliveryDocs(deliveryDocs);
			System.out.println(procedureRequest.toDocument());
			DataBaseUtil.save(procedureRequest.toDocument(), "proceduresRequest");

			ArrayList<String> parameters = new ArrayList<>();
			parameters.add(procedureRequest.getProcedureClassName());
			parameters.add(procedureRequest.getFileNumber());

			EmailNotifierSvc sendEmail = new EmailNotifierSvc();
			sendEmail.send(EmailType.INITPROCEDURE, citizen.getEmail(), parameters);

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		response = messager.getOkMessage("Registro Exitoso");
		pResponse.type("application/json");
		// return "Proceso Exitoso";

		pRequest.body();

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
	public static Object consultProcedures(Request pRequest, Response pResponse) {

		// .
		String email;

		try {
			email = Authorization.getFromToken(pRequest, Authorization.TOKEN_EMAIL_KEY);
		} catch (InvalidTokenException jwtEx) {
			log.info(jwtEx.getMessage());
			return "failed";
		}
		// ..

		Document procedureFilter = new Document();
		procedureFilter.append("citizen.email", email);

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, PROCEDURESREQUEST);

		for (Document item : documents) {
			item.remove("dependencies");
			item.remove("procedures");
			item.remove("address");
			item.remove("url");
			item.remove("phone");
			item.remove("state");
			item.remove("schedule");
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

		// .
		String email;

		try {
			email = Authorization.getFromToken(pRequest, Authorization.TOKEN_EMAIL_KEY);
		} catch (InvalidTokenException jwtEx) {
			log.info(jwtEx.getMessage());
			return "failed";
		}
		// ..

		procedureFilter.append("citizen.email", email);

		procedureFilter.append("fileNumber", pRequest.params(":id"));

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, PROCEDURESREQUEST);
		for (Document item : documents) {
			item.remove("dependencies");
			item.remove("procedures");
			item.remove("address");
			item.remove("url");
			item.remove("phone");
			item.remove("state");
			item.remove("schedule");
			dataset.add(item);

		}

		log.info("Consult procedures by id done");

		return dataset;
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
			
			String nameFile=fileRequest+citizen;
			
			fileManager.uploadDocument(pRequest);
			response = messager.getOkMessage(((DocumentSvc)fileManager).getAnswerStr());

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
