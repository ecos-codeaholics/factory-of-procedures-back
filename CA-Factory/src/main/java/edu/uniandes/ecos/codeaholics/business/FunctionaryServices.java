/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import edu.uniandes.ecos.codeaholics.persistence.History;
import edu.uniandes.ecos.codeaholics.persistence.ProcedureStatus;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;
import spark.Request;
import spark.Response;

public class FunctionaryServices {

	private final static Logger log = LogManager.getLogger(AuthServices.class);

	private static IMessageSvc messager = new ResponseMessage();

	private static Gson GSON = new GsonBuilder().serializeNulls().create();

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

		log.info("Param of the request is: " + pRequest.queryParams("email"));

		Document procedureFilter = new Document();
		procedureFilter.append("activities.functionary", pRequest.queryParams("email"));
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

		log.info(pRequest.params(":id"));
		log.info("param of the query request is: " + pRequest.queryParams("email"));
		log.info(pRequest.uri());

		Document procedureFilter = new Document();
		procedureFilter.append("activities.functionary", pRequest.queryParams("email"));
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
	public static Object approveProcedureStep(Request pRequest, Response pResponse) {

		Object response = null;
		log.info("Inicia Cambio de estado");
		log.info("Procedure id "+pRequest.params(":procedureId"));
		log.info("Step id "+pRequest.params(":stepId"));
		log.info("email of functionary is " + pRequest.queryParams("email"));
		log.info("comment of functionary is " + pRequest.queryParams("comment"));

		String newStatus = null;

		List<Document> procedureFilter = new ArrayList<Document>();
		try {
			
			ArrayList<Document> procedureRequest = DataBaseUtil.find(new Document().append("fileNumber", Long.parseLong(pRequest.params(":procedureId"))), PROCEDURESREQUEST);
			
				Document procedure =  (Document) procedureRequest.get(0);
				ArrayList<Document> histories =  (ArrayList<Document>) procedure.get("histories");
		
			
			
			procedureFilter.add(
					new Document("fileNumber", new Document("$eq", Long.parseLong(pRequest.params(":procedureId")))));
			procedureFilter.add(new Document("activities",
					new Document("$elemMatch", new Document("step", Integer.parseInt(pRequest.params(":stepId"))))));
			ProcedureStatus status = GSON.fromJson(pRequest.body(), ProcedureStatus.class);
			/*1 mayo 68*/
			newStatus = status.getStatusProcedure();
			log.info("status: " + newStatus);
			Document replaceValue = new Document("activities.$.status", newStatus);

			DataBaseUtil.compositeUpdate(procedureFilter, replaceValue, PROCEDURESREQUEST);
			DataBaseUtil.compositeUpdate(procedureFilter, new Document("status", newStatus), PROCEDURESREQUEST);
			
			histories.add(new History(
					Integer.parseInt(pRequest.params(":stepId")), "2016/10/27" ,
					pRequest.queryParams("email"), status.getStatusHistory(),pRequest.queryParams("comment")).toDocument());
			DataBaseUtil.compositeUpdate(procedureFilter,new Document( "histories",
					histories
					), PROCEDURESREQUEST);
			
			

		} catch (Exception e) {
			log.info("Problem writting : " + e.getMessage());
		}

		response = messager.getOkMessage(newStatus);

		return response;

	}
}