/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.bson.Document;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import edu.uniandes.ecos.codeaholics.config.IAuthenticationSvc;
import edu.uniandes.ecos.codeaholics.config.IDocumentSvc;
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DocumentSvc;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.config.Notification;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;
import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import spark.Request;
import spark.Response;

public class CitizenServices {

	private static Gson GSON = new GsonBuilder().serializeNulls().create();

	private static IMessageSvc messager = new ResponseMessage();

	/***
	 * Verifica las credenciales del ususario y crea la sesion.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return sesion creada en el sistema
	 */
	public static Object doLogin(Request pRequest, Response pResponse) {

		Object response = null;

		try {

			Citizen data = GSON.fromJson(pRequest.body(), Citizen.class);
			IAuthenticationSvc authenticate = new Authentication();

			boolean authenticated = authenticate.doAuthentication(data.getEmail(), data.getPassword(), "citizen");
			if (authenticated) {
				response = authenticate.getAnswer();
			}

		} catch (WrongUserOrPasswordException e) {
			pResponse.status(401);
			response = messager.getNotOkMessage(e.getMessage());

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

		return response;

	}

	/***
	 * Agrega un ciudadno a la base de datos.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object insertCitizen(Request pRequest, Response pResponse) {

		Object response = null;

		try {

			Citizen citizen = GSON.fromJson(pRequest.body(), Citizen.class);
			String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
			citizen.setPassword(hash[1]);
			citizen.setSalt(hash[0]);
			DataBaseUtil.save(citizen.toDocument(), "citizen");
			Notification.sendEmail(citizen.getEmail());

			response = messager.getOkMessage("Success");

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		} catch (AddressException e) {
			response = messager.getNotOkMessage(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			response = messager.getNotOkMessage(e.getMessage());
			e.printStackTrace();
		}

		// HEAD
		// res.status(200);
		// res.type("application/json");
		// return "success";

		pRequest.body();
		return response;

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
	public static String getCitizenList(Request pRequest, Response pResponse) {

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.getAll("citizen");
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

		Type type = new TypeToken<List<Document>>() {
		}.getType();
		String json = GSON.toJson(dataset, type);

		pResponse.type("application/json");

		return json;
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
public static String getCitizenDetail(Request pRequest, Response pResponse) {
		
		System.out.println(pRequest.params("identification"));
				
		//Citizen citizen = GSON.fromJson(req.params().toString(), Citizen.class);
		//Citizen citizen = GSON.fromJson(req.body(), Citizen.class);


		Citizen citizen = GSON.fromJson(pRequest.body(), Citizen.class);

		Document filter = new Document();
			
		//filter = filter.append("identification", citizen.getIdentification());
		filter = filter.append("identification", pRequest.params("identification"));
		
		System.out.println(filter.toString());

				
		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(filter, "citizen");
		for (Document item : documents) {
			item.remove("password");
			item.remove("salt");
			dataset.add(item);
		}

		System.out.println(dataset.get(0));
		
		Type type = new TypeToken<List<Document>>() {
		}.getType();

		String json = GSON.toJson(dataset, type);

		pResponse.type("application/json");
		return json;

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
	public static Object closeSession(Request pRequest, Response pResponse) {

		Object response = null;

		try {

			String email = pRequest.queryParams("email");
			Authentication.closeSession(email);

			response = messager.getOkMessage("Success");

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

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
		// return "success method startProcedure";

		Object response;
		response = messager.getOkMessage("Success");
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

		Object response;
		response = messager.getOkMessage("Success");
		return response;
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

		Object response;
		response = messager.getOkMessage("Success");
		return response;
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
		response = messager.getOkMessage("Success");
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

			IDocumentSvc fileUploader = new DocumentSvc();
			fileUploader.uploadDocument(pRequest);
			response = messager.getOkMessage("Success");

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

		return response;

	}

}
