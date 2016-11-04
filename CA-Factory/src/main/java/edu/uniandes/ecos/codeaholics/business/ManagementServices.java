/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spark.Request;
import spark.Response;

/**
 * Clase encargada de manegar todos los servicios de los administradores de la alcaldia en el sistema
 * 
 * @author Codeaholics
 *
 */
public class ManagementServices {

	private final static Logger log = LogManager.getLogger(ManagementServices.class);

	/**
	 * Se encarga de crear una alcaldia
	 * @param pRequest
	 * 				Request
	 * @param pResponse
	 * 				Response
	 * @return	json con mensaje de exito o fracaso
	 */
	public static String createMayoralty(Request pRequest, Response pResponse) {
		log.info("Mayoralty created");
		return "success";
	}

	/**
	 * Se encarga de crear un administrador en la alcaldia
	 * @param pRequest
	 * 				Request
	 * @param pResponse
	 * 				Response
	 * @return	json con mensaje de exito o fracaso
	 */
	public static String createAdminMayoralty(Request pRequest, Response pResponse) {
		return "success";
	}

	/**
	 * Se encarga de crear un procedimiento
	 * @param pRequest
	 * 				Request
	 * @param pResponse
	 * 				Response
	 * @return	json con mensaje de exito o fracaso
	 */
	public static String createProcedure(Request pRequest, Response pResponse) {
		return "success";
	}

	/**
	 * Se encarga de asignar un tramite a una alcaldia
	 * @param pRequest
	 * 				Request
	 * @param pResponse
	 * 				Response
	 * @return	json con mensaje de exito o fracaso
	 */
	public static String relateProcedureToMayoralty(Request pRequest, Response pResponse) {
		return "success";
	}

}
