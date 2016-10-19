package edu.uniandes.ecos.codeaholics.business;

//import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.google.gson.JsonSyntaxException;

import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DocumentSvc;
import edu.uniandes.ecos.codeaholics.config.IDocumentSvc;
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
//import edu.uniandes.ecos.codeaholics.config.Notification;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;
import spark.Request;
import spark.Response;

public class CitizenServices {

	private static IMessageSvc messager = new ResponseMessage();

	private static IDocumentSvc fileManager = new DocumentSvc();

	private static String USER_PROFILE = "citizen";

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
		procedureFilter.append("slug", pRequest.queryParams("procedure"));
		ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, PROCEDURES);
		for (Document item : documents) {
			dataset.add(item);
		}
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
		ArrayList<Document> documents = DataBaseUtil.getAll(USER_PROFILE);
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

		// Este no hace falta por por que el body del request viene null
		// Citizen citizen = GSON.fromJson(pRequest, Citizen.class);

		Document filter = new Document();
		filter.append("identification", Integer.parseInt(pRequest.params("identification")));
		filter.append("name", Integer.parseInt(pRequest.params("identification")));

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(filter, USER_PROFILE);
		for (Document item : documents) {
			item.remove("password");
			item.remove("salt");
			dataset.add(item);
		}

		// Type type = new TypeToken<List<Document>>() {
		// }.getType();
		// String json = GSON.toJson(dataset, type);

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
			String email = pRequest.queryParams("email");
			if (email != null) {
				Authentication.closeSession(email);
				response = messager.getOkMessage("Proceso Exitoso");
			} else {
				pResponse.status(417);
				response = messager.getNotOkMessage("El correo es necesario");
			}
		} catch (JsonSyntaxException e) {
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
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object startProcedure(Request pRequest, Response pResponse) {

		// HEAD
		// return "Proceso Exitoso method startProcedure";

		Object response;
		response = messager.getOkMessage("Proceso Exitoso");
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

		// Citizen citizen = GSON.fromJson(pRequest, Citizen.class);

		System.out.println("param of the request is: " + pRequest.queryParams("email"));

		Document procedureFilter = new Document();
		procedureFilter.append("citizen.email", pRequest.queryParams("email"));

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, PROCEDURESREQUEST);
		// ArrayList<Document> documents =
		// DataBaseUtil.getAll(PROCEDURESREQUEST);
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
			System.out.println(dataset);
			Document procedure2 = new Document();
			procedure2.put("id", 2);
			procedure2.put("name", "Jeison");
			procedure2.put("department", "Cundinamarca");
			procedure2.put("city", "Bogota");
			procedure2.put("status", "En proceso");
			dataset.add(procedure2);

			System.out.println(dataset);
		}

		// pResponse.type("application/json");
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

		System.out.println(pRequest.params(":id"));
		System.out.println("param of the query request is: " + pRequest.queryParams("email"));
		System.out.println(pRequest.uri());

		Document procedureFilter = new Document();
		procedureFilter.append("citizen.email", pRequest.queryParams("email"));
		procedureFilter.append("fileNumber", Long.parseLong(pRequest.params(":id")));

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

		// pResponse.type("application/json");
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

		try {

			fileManager.uploadDocument(pRequest);
			response = messager.getOkMessage("Proceso Exitoso");

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
