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

import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import spark.Request;
import spark.Response;

public class FunctionaryServices {

	private final static Logger log = LogManager.getLogger(AuthServices.class);
	
	private static Gson GSON = new GsonBuilder().serializeNulls().create();
	
	private static String PROCEDURES = "procedures";

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
		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.getAll(PROCEDURES);
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

		pResponse.type("application/json");
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
	public static String consultProceduresById(Request req, Response res) {

		return "success";
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
	public static String approveProcedure(Request req, Response res) {

		return "success";
	}

}