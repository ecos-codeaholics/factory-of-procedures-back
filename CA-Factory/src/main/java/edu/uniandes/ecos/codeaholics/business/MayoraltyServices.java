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
		filter.append("identification", Integer.parseInt(pRequest.params("identification")));

		Document dataset = new Document();
		ArrayList<Document> documents = DataBaseUtil.find(filter, MAYORALTY);
		if(documents.size() > 0){
			Document mayoralty = documents.get(0);
			dataset = (Document) mayoralty.get("procedures");
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
			alcaldia1.put("name", "Tocancipa");
			Document alcaldia3 = new Document();
			alcaldia1.put("name", "Paz de Ariporo");
			
			documents.add(alcaldia1);
			documents.add(alcaldia2);
			documents.add(alcaldia3);
		}

		pResponse.type("application/json");
		return dataset;
	}

}
