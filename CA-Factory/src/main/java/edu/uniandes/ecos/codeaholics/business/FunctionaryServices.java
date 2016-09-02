/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.Request;
import spark.Response;

public class FunctionaryServices {
	
	private static Gson GSON = new GsonBuilder().serializeNulls().create();
	
	/***
	 * Consulta tramites asignados a un funcionario.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static String consultProcedures(Request req, Response res) {

		return "success";
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