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

import edu.uniandes.ecos.codeaholics.config.Constants;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc.EmailType;
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;
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
				item.remove("salt");
				item.remove("userProfile");
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
		
		Document citizenDoc = GSON.fromJson(pRequest.body(), Document.class);
		citizenDoc.remove("fullName");
		citizenDoc.remove("_id");
	
		log.info(citizenDoc.toString());
		
		try {

			ArrayList<Document> citizenList = DataBaseUtil.find(citizenDoc, Constants.CITIZEN_COLLECTION);
			Document functionaryDoc = citizenList.get(0);
			
			Functionary functionary = GSON.fromJson(functionaryDoc.toJson(), Functionary.class);
			functionary.setUserProfile(Constants.FUNCTIONARY_USER_PROFILE);
			functionary.setDependency("Atenci\u00F3n al Ciudadano");
			
			DataBaseUtil.save(functionary.toDocument(), Constants.FUNCTIONARY_COLLECTION);

			ArrayList<String> params = new ArrayList<String>();
			params.add(functionary.getName());
						
			EmailNotifierSvc sendEmail = new EmailNotifierSvc();
			sendEmail.send(EmailType.MAKE_FUNCTIONARY, functionary.getEmail(), params);

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		response = messager.getOkMessage("Registro exitoso de su solicitud");
		pResponse.type("application/json");
		
		// return "Proceso Exitoso";
		//pRequest.body();

		return response;

	}

}
