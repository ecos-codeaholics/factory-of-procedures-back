/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoException;

import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;
import spark.Request;
import spark.Response;

public class FunctionaryServices {

	private final static Logger log = LogManager.getLogger(AuthServices.class);

	private static IMessageSvc messager = new ResponseMessage();

	private static Gson GSON = new GsonBuilder().serializeNulls().create();

	private static String PROCEDURESREQUEST = "proceduresRequest";

	/*
	 * Class: ProcedureStatus FunctionaryServices.java
	 * Original Author: @author AOSORIO
	 * Description: Auxiliary class: catched the procedure status returned in body
	 * Created: Oct 15, 2016 4:55:28 PM
	 */
	private class ProcedureStatus {
		String status;

		/**
		 * @return the status
		 */
		public String getStatus() {
			return status;
		}
	}

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

		log.info(pRequest.params(":procedureId"));
		log.info(pRequest.params(":stepId"));
		log.info("param of the request is: " + pRequest.queryParams("email"));

		List<Document> procedureFilter = new ArrayList<>();
		procedureFilter.add(new Document("fileNumber",
				new Document("$eq", Long.parseLong(pRequest.params(":procedureId")))));
		procedureFilter.add(new Document("activities",
				new Document("$elemMatch",
						new Document("step", Integer.parseInt(pRequest.params(":stepId"))))));

		ProcedureStatus status = GSON.fromJson(pRequest.body(), ProcedureStatus.class);
		String newStatus = status.getStatus();

		System.out.println("status: " + newStatus);

		Document replaceValue = new Document("activities.$.status", newStatus);

		try {
			DataBaseUtil.compositeUpdate(procedureFilter, replaceValue, PROCEDURESREQUEST);
		} catch (MongoException e) {
			System.out.println("Problem writting : " + newStatus);
		}

		response = messager.getOkMessage(newStatus);

		return response;

	}
}