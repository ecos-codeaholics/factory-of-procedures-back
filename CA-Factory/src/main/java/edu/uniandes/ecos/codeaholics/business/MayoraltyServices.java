/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.Constants;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc.EmailType;
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;
import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.InvalidTokenException;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import edu.uniandes.ecos.codeaholics.persistence.Functionary;
import spark.Request;
import spark.Response;

/**
 * Clase encargada de manegar todos los servicios de los administradores de la alcaldia en el sistema
 * 
 * @author Codeaholics
 *
 */
public class MayoraltyServices {

	private final static Logger log = LogManager.getLogger(MayoraltyServices.class);
	
	private static IMessageSvc messager = new ResponseMessage();
	
	private static Gson GSON = new GsonBuilder().serializeNulls().create();
	
	/***
	 * Consulta lista de tramites por alcaldia.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object proceduresByMayoralty(Request pRequest, Response pResponse) {

		Document filter = new Document();
		filter.append("slug", pRequest.params(":mayoraltyName").toString());

		log.info(pRequest.params(":mayoraltyName").toString());
		
		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> mayoralties = DataBaseUtil.find(filter, Constants.MAYORALTY_COLLECTION);
		
		if(!mayoralties.isEmpty()){
			Document mayoralty = (Document) mayoralties.get(0);
			@SuppressWarnings("unchecked")
			ArrayList<String> procedures =  (ArrayList<String>) mayoralty.get("procedures");
			for (String item : procedures) {
				log.info(item);
				Document procedure = new Document();
				procedure.append("name", item);
				procedure.append("slug", item.replace(" ", "").toLowerCase());
				dataset.add(procedure);
			}
		}
		
		pResponse.type("application/json");
		return dataset;
	}
	
	/***
	 * Consulta lista de alcaldias creadas en el sistema
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object getMayoraltyList(Request pRequest, Response pResponse) {
		
		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.getAll(Constants.MAYORALTY_COLLECTION);
		
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
		try {
			ArrayList<Document> documents = DataBaseUtil.getAll(Constants.CITIZEN_COLLECTION);
			String fullName = "";
			for (Document item : documents) {
				fullName = item.get("name").toString() + " " + item.get("lastName1").toString();
				item.remove("password");
				item.remove("salt");
				item.put("fullName", fullName);
				dataset.add(item);

			}
		} catch (Exception e) {
			log.error("Problem listing citizen");
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
	public static Object getCitizenListForFunctionary(Request pRequest, Response pResponse) {

		List<Document> dataset = new ArrayList<>();
		try {
			ArrayList<Document> documents = DataBaseUtil.getAll(Constants.CITIZEN_COLLECTION);
			String fullName = "";
			for (Document item : documents) {
				item.remove("password");
				item.remove("_id");
				item.remove("salt");
				item.remove("userProfile");
				item.remove("birthDate");
				ArrayList<Document> functionaries = DataBaseUtil.find(item, Constants.FUNCTIONARY_COLLECTION);
				if(functionaries.isEmpty()){
					fullName = item.get("name").toString() + " " + item.get("lastName1").toString()
							+ " " + item.get("lastName2").toString();
					item.put("fullName", fullName);
					dataset.add(item);
				}
			}
		} catch (Exception e) {
			log.error("Problem listing citizen");
		}
		pResponse.type("application/json");
		return dataset;
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
	public static Object createFunctionary(Request pRequest, Response pResponse) {

		Object response = null;
		
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(pRequest.body()).getAsJsonObject();
		System.out.println(json.toString());
		JsonObject citizenJson = (JsonObject) json.get("citizen");
		JsonObject dependencyJson = (JsonObject) json.get("dependency");
		
		Document citizenDoc = GSON.fromJson(citizenJson, Document.class);
		Document dependencyDoc = GSON.fromJson(dependencyJson, Document.class);
		citizenDoc.remove("fullName");
	
		log.info(citizenDoc.toString());
		
		try {
			
			String subject;
			subject = Authorization.getFromToken(pRequest, Constants.TOKEN_SUBJECT_KEY);
			String mayoraltyStr = subject.split(",")[2];
			
			Document filter = new Document();
			filter.append("email", citizenDoc.get("email"));
			filter.append("name", citizenDoc.get("name"));
			
			ArrayList<Document> citizenList = DataBaseUtil.find(filter, Constants.CITIZEN_COLLECTION);
			Document asCitizenDoc = citizenList.get(0);
			
			//problem with Document -- toJson --> POJO (when attributes are != basic objects
			asCitizenDoc.remove("_id");
			asCitizenDoc.remove("birthDate");

			Citizen asCitizen = GSON.fromJson(asCitizenDoc.toJson(), Citizen.class);
			
			Functionary functionary  = new Functionary(asCitizen);
			
			functionary.setUserProfile(Constants.FUNCTIONARY_USER_PROFILE);
			functionary.setMayoralty(mayoraltyStr);
			functionary.setDependency(dependencyDoc.getString("name"));
			
			DataBaseUtil.save(functionary.toDocument(), Constants.FUNCTIONARY_COLLECTION);

			ArrayList<String> params = new ArrayList<String>();
			params.add(functionary.getName());
						
			//EmailNotifierSvc sendEmail = new EmailNotifierSvc();
			EmailNotifierSvc sendEmail = EmailNotifierSvc.getInstance();
			sendEmail.send(EmailType.MAKE_FUNCTIONARY, functionary.getEmail(), params);
			
			response = messager.getOkMessage("Registro exitoso de su solicitud");
			
		} catch (Exception e) {
			log.error(e.getMessage());
			response = messager.getNotOkMessage("Registro fallido.");
		}
		
		pResponse.type("application/json");
		
		return response;

	}
	
	/***
	 * Consulta lista de dependencias por alcaldia.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object dependenciesByMayoralty(Request pRequest, Response pResponse) {
		
		String mayoraltyStr;
		try {
			mayoraltyStr = Authorization.getFromToken(pRequest, Constants.TOKEN_SUBJECT_KEY);
		} catch (InvalidTokenException jwtEx) {
			log.error(jwtEx.getMessage());
			return "failed";
		}
		Document filter = new Document();
		filter.append("name", mayoraltyStr);
		log.info(mayoraltyStr);
		
		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> mayoralties = DataBaseUtil.find(filter, Constants.MAYORALTY_COLLECTION);
		
		if(!mayoralties.isEmpty()){
			Document mayoralty = (Document) mayoralties.get(0);
			@SuppressWarnings("unchecked")
			ArrayList<Document> dependencies =  (ArrayList<Document>) mayoralty.get("dependencies");
			for (Document item : dependencies) {
				dataset.add(item);
			}
		}
		
		pResponse.type("application/json");
		return dataset;
	}

}
