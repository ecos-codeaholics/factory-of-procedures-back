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

public class FunctionaryServices {
	
	private static String PROCEDURESREQUEST = "proceduresRequest";

	/***
	 * Consulta tramites asignados a un funcionario.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object consultProcedures(Request pRequest, Response pResponse) {
				
		System.out.println("param of the request is: "+ pRequest.queryParams("email"));

		Document procedureFilter = new Document();
		procedureFilter.append("steps.functionary", pRequest.queryParams("email"));
				
		List<Document> dataset = new ArrayList<>();
		
		ArrayList<Document> documents = DataBaseUtil.find(procedureFilter, PROCEDURESREQUEST);
		//ArrayList<Document> documents = DataBaseUtil.getAll(PROCEDURESREQUEST);
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
		
		
		//ayuda para probar el servicio
		if(documents.isEmpty()){
			Document procedure = new Document();
			procedure.put("date", "Girardot");
			procedure.put("time", "Girardot");
			procedure.put("fileNumber", "Girardot");
			procedure.put("id", "Girardot");
			procedure.put("name", "Girardot");
			procedure.put("state", "Girardot");
			procedure.put("attached", "Girardot");
			
			dataset.add(procedure);
	
		}

		//pResponse.type("application/json");
		return dataset;

	}

	/***
	 * Consulta estado y detalles de un tramite por id
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object consultProceduresById(Request pRequest, Response pResponse) {

		System.out.println(pRequest.params(":id"));
		System.out.println("param of the query request is: "+ pRequest.queryParams("email"));
		System.out.println(pRequest.uri());
		
		
		Document procedureFilter = new Document();
		procedureFilter.append("steps.functionary", pRequest.queryParams("email"));
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
		
		//pResponse.type("application/json");
		return dataset;
	}

	/***
	 * Actualiza el estado de una tramite en una secretaria por aprobado
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static String approveProcedureStep(Request req, Response res) {

		return "success";
	}

}