/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import java.io.Console;
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
		filter.append("name", pRequest.params(":mayoraltyName").toString());

		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> mayoralties = DataBaseUtil.find(filter, MAYORALTY);
		if(!mayoralties.isEmpty()){
			Document mayoralty = (Document) mayoralties.get(0);
			@SuppressWarnings("unchecked")
			ArrayList<String> procedures =  (ArrayList<String>) mayoralty.get("procedures");
			for (String item : procedures) {
				System.out.println(item);
				Document procedure = new Document();
				procedure.append("name", item);
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
		
		pResponse.type("application/json");
		return dataset;
	}

}
