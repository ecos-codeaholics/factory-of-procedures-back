/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import java.util.Date;
import java.text.SimpleDateFormat;
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
import edu.uniandes.ecos.codeaholics.persistence.Procedure;
import edu.uniandes.ecos.codeaholics.persistence.ProcedureRequest;
import edu.uniandes.ecos.codeaholics.persistence.ProcedureStatus;
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
		  
		Document procedureFilter = new Document();
		try {
			procedureFilter.append("fileNumber", pRequest.params(":procedureId"));
			
			ProcedureStatus status = GSON.fromJson(pRequest.body(), ProcedureStatus.class);
			ArrayList<Document> procedureRequest = DataBaseUtil.find(procedureFilter, PROCEDURESREQUEST);
			
			Document procedureDoc = procedureRequest.get(0);
			String id = procedureDoc.get("_id").toString();
			Date startDate= (Date) procedureDoc.get("startDate");
			procedureDoc.remove("_id");
			procedureDoc.remove("startDate");
			ProcedureRequest procedure = GSON.fromJson(procedureDoc.toJson(), ProcedureRequest.class);
			procedure.setId(id);
			procedure.setStartDate(startDate);
			procedure.addHistory(
					new History(
					Integer.parseInt(pRequest.params(":stepId")),
					sqlDate.toString(), 
					pRequest.queryParams("email"),
					status.getStatusHistory(), pRequest.queryParams("comment"))
					);
			int maxStep = -1;
			int firstStep = -1;
			int i;
			for (i = 0; i < procedure.getActivities().size(); i++) {
				if (i ==0)
					firstStep= procedure.getActivities().get(i).getStep();
				if (procedure.getActivities().get(i).getStep() > maxStep)
					maxStep = procedure.getActivities().get(i).getStep();
				if (procedure.getActivities().get(i).getStep() < firstStep)
					firstStep= procedure.getActivities().get(i).getStep();
				if( procedure.getActivities().get(i).getStep() == Integer.parseInt(pRequest.params(":stepId") )){
					procedure.getActivities().get(i).setStatus(status.getStatusActivity());
				}
			}
			
			if (maxStep ==Integer.parseInt(pRequest.params(":stepId")) ||  status.getStatusProcedure() == "Finalizado"){
				procedure.setStatus("Finalizado");
				procedure.setFinishDate(new Date());
				procedure.getActivities().get(i-1).setStatus("Finalizado");
			}
			if(status.getStatusHistory() == "Rechazado"){
				if (1 == firstStep){
					procedure.setStatus("Finalizado");
					procedure.setFinishDate(new Date());
				}else{
					procedure.getActivities().get(i-1).setStatus("Pendiente");
					procedure.getActivities().get(i-2).setStatus("En curso");
				}
			}
			
			
			DataBaseUtil.update(procedureFilter, procedure.toDocument(), PROCEDURESREQUEST);
			
			response = messager.getOkMessage(status.getStatusProcedure());
		} catch (Exception e) {
			log.info("Problem writting : " + e.getMessage());
		}

		

		return response;

	}
}