/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import spark.Request;
import spark.Response;

/**
 * Clase encargada de manegar todos los servicios de los administradores de la alcaldia en el sistema
 * 
 * @author Codeaholics
 *
 */
public class MayoraltyServices {
	
	private static String MAYORALTY = "mayoralty";

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
		filter.append("name", pRequest.queryParams("email").toString());

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(filter, MAYORALTY);
		if(documents.size() > 0){
			Document mayoralty = documents.get(0);
			//dataset = (Document) mayoralty.get("procedures");
		}
		//ayuda oara probar el servicio
		if(documents.isEmpty()){
			Document procedure1 = new Document();
			procedure1.put("name", "Pagar predial");
			Document procedure2 = new Document();
			procedure2.put("name", "Registrarse en el SISBEN");
			
			dataset.add(procedure1);
			dataset.add(procedure2);
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
		ArrayList<Document> documents = DataBaseUtil.getAll(MAYORALTY);
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
		//ayuda oara probar el servicio
		if(documents.isEmpty()){
			Document alcaldia1 = new Document();
			alcaldia1.put("name", "Girardot");
			Document alcaldia2 = new Document();
			alcaldia2.put("name", "Tocancipa");
			Document alcaldia3 = new Document();
			alcaldia3.put("name", "Paz de Ariporo");
			
			dataset.add(alcaldia1);
			dataset.add(alcaldia2);
			dataset.add(alcaldia3);
		}
		
		pResponse.type("application/json");
		return dataset;
	}

}
