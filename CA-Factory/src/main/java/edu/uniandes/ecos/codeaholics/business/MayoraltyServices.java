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

}
