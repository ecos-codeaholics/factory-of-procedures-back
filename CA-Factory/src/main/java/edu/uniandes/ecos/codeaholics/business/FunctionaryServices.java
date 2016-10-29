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
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;
import edu.uniandes.ecos.codeaholics.persistence.History;
import edu.uniandes.ecos.codeaholics.persistence.ProcedureStatus;
import spark.Request;
import spark.Response;

public class FunctionaryServices {

	private final static Logger log = LogManager.getLogger(FunctionaryServices.class);

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
		//procedureFilter.append("fileNumber", Long.parseLong(pRequest.params(":id")));
		procedureFilter.append("fileNumber", pRequest.params(":id"));
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
		
		log.info("Cambiando estado a tramite: " + pRequest.body());
		Object response = null;
		java.util.Date utilDate = new java.util.Date(); //fecha actual
		  long lnMilisegundos = utilDate.getTime();
		  java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
		  
		List<Document> procedureFilter = new ArrayList<Document>();
		try {
			ProcedureStatus status = GSON.fromJson(pRequest.body(), ProcedureStatus.class);
			ArrayList<Document> procedureRequest = DataBaseUtil.find(
					new Document().append("fileNumber", pRequest.params(":procedureId")),
					PROCEDURESREQUEST);
			Document procedure = (Document) procedureRequest.get(0);
			@SuppressWarnings("unchecked")
			ArrayList<Document> histories = (ArrayList<Document>) procedure.get("histories");

			histories.add(new History(Integer.parseInt(pRequest.params(":stepId")),
					sqlDate.toString(), 
					pRequest.queryParams("email"),
					status.getStatusHistory(), pRequest.queryParams("comment")).toDocument());
			
			procedureFilter.add(
					new Document("fileNumber", new Document("$eq", pRequest.params(":procedureId"))));
			procedureFilter.add(new Document("activities",
					new Document("$elemMatch", new Document("step", Integer.parseInt(pRequest.params(":stepId"))))));
		
			Document replaceValue = new Document("activities.$.status", status.getStatusActivity());

			DataBaseUtil.compositeUpdate(procedureFilter, replaceValue, PROCEDURESREQUEST);
			DataBaseUtil.compositeUpdate(procedureFilter, new Document("status", status.getStatusProcedure()), PROCEDURESREQUEST);
			DataBaseUtil.compositeUpdate(procedureFilter, new Document("histories", histories), PROCEDURESREQUEST);

			response = messager.getOkMessage(status.getStatusProcedure());
		} catch (Exception e) {
			log.info("Problem writting : " + e.getMessage());
		}

		

		return response;

	}
}