/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.bson.Document;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoWriteException;

import edu.uniandes.ecos.codeaholics.config.IAuthenticationSvc;
import edu.uniandes.ecos.codeaholics.config.IDocumentSvc;
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DocumentSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc.EmailType;
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

	private static IDocumentSvc fileManager = new DocumentSvc();
	
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

		pResponse.type("application/json");
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
			
			//TODO: replace with new service - EmailNotifier.send(EmailType.REGISTRATION,citizen.getEmail());
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
		pResponse.type("application/json");
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
	public static Object getCitizenList(Request pRequest, Response pResponse) {

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
		//String json = GSON.toJson(dataset, type);

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

		//Este no hace falta por por que el body del request viene null
		//Citizen citizen = GSON.fromJson(pRequest, Citizen.class);

		Document filter = new Document();
		filter.append("identification", Integer.parseInt(pRequest.params("identification")));
						
		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(filter, "citizen");
		for (Document item : documents) {
			item.remove("password");
			item.remove("salt");
			dataset.add(item);
		}
		
		Type type = new TypeToken<List<Document>>() {
		}.getType();

		//String json = GSON.toJson(dataset, type);
		
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
	public static Object closeSession(Request pRequest, Response pResponse) {
		
		
		Object response = null;

		try {

			String email = pRequest.params("email");
			System.out.println(email);
			Authentication.closeSession(email);

			response = messager.getOkMessage("Success");

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

			fileManager.uploadDocument(pRequest);
			response = messager.getOkMessage("Success");

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

		return response;

	}
	
	public static Object resetPassword (Request pRequest, Response pResponse) {
		Object response = null;
		
		try {
			Citizen data = GSON.fromJson(pRequest.body(), Citizen.class);
			System.out.println(data.getEmail()+" "+data.getIdentification());
			
			Document filter = new Document();
			filter.append("identification", data.getIdentification());
			filter.append("email", data.getEmail());
			
			
			ArrayList<Document> documents = DataBaseUtil.find(filter, "citizen");
			
			//TODO throw an exception about that email and identification doesn't correspond to a registered user			
			if (documents.isEmpty()){//throw exception
				}
			
			//Create randomize password
			String newPassword = GeneralUtil.randomPassword();
			
			System.out.println(newPassword);
			//create hash
			String newSalt = null;
			String[] hash = GeneralUtil.getHash(newPassword, "");
			String newPasswordHashed = hash[1];
			newSalt = hash[0];
			
			//send value to change
			Map<String, Object> valuesToReplace = new HashMap<String, Object>();
			valuesToReplace.put("password", newPassword);
			valuesToReplace.put("salt", newSalt);
			
			//send salt and password to the register in the DB
			Document register = new Document(valuesToReplace);
			DataBaseUtil.update(filter, register, "citizen");
			
			//create array list to ssend as a parameter to the EmailNotifierSvc
			ArrayList<String> parametersEmail = new ArrayList<>();
			parametersEmail.add(data.getEmail());
			parametersEmail.add(newPassword);
			
			//Send Email
			EmailNotifierSvc sendPassword = new EmailNotifierSvc();
			sendPassword.send(EmailType.RECOVERY, parametersEmail);
			
			
			//Notification.sendEmail("jasonlll88@hotmail.com");
		} 
		catch (MongoWriteException M) {
			// TODO: handle exception
			System.out.println("Mongo Exception");
			
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	} 

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
