/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import static spark.Spark.halt;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import spark.Request;
import spark.Response;

/**
 * Created by davidMtz on 26/6/16.
 */
public final class Authorization {

	// Atributos
	private final static Logger log = LogManager.getLogger(Authorization.class);

	// Metodos
	/**
	 * Valida si lo datos corresponden a ciudadano con una sesion creada en el
	 * sistema.
	 *
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return resultado de la verificación
	 */
	public static String authorizeCitizen(Request req, Response res) {
		boolean autorizado = false;

		String email = req.queryParams("email");
		if (email != null && !email.equals("")) {
			Document session = new Document();
			session.append("email", email);
			session.append("user-profile", "citizen");
			ArrayList<Document> documents = DataBaseUtil.find(session, "session");
			if (documents.size() > 0) {
				log.info("autorizado!");
				autorizado = true;
			} else {
				log.info("no autorizado!");
			}

			if (!autorizado) {
				halt(401, "No eres bienvenido aqui");
			}
			return "Proceso Exitoso";
		} else {
			halt(401, "No eres bienvenido aqui");
			return "incorrect";
		}
	}

	/**
	 * Valida si lo datos corresponden a funcionario con una sesion creada en el
	 * sistema.
	 *
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return resultado de la verificación
	 */
	public static String authorizeFuntionary(Request req, Response res) {
		boolean autorizado = false;

		String email = req.queryParams("email");
		log.info("Correo= "+email);
		if (email != null && !email.equals("")) {
			Document session = new Document();
			session.append("email", email);
			session.append("user-profile", "funtionary");
			ArrayList<Document> documents = DataBaseUtil.find(session, "session");
			if (documents.size() > 0) {
				log.info("autorizado!");
				autorizado = true;
			} else {
				log.info("no autorizado!");
			}

			if (!autorizado) {
				halt(401, "No eres bienvenido aqui");
			}
			return "Proceso Exitoso";
		} else {
			halt(401, "No eres bienvenido aqui");
			return "incorrect";
		}
	}

	/**
	 * Valida si lo datos corresponden a administrador de alcaldia con una
	 * sesion creada en el sistema.
	 *
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return resultado de la verificación
	 */
	public static String authorizeAdmin(Request req, Response res) {
		boolean autorizado = false;

		String email = req.queryParams("email");
		if (email != null && !email.equals("")) {
			Document session = new Document();
			session.append("email", email);
			session.append("user-profile", "admin");
			ArrayList<Document> documents = DataBaseUtil.find(session, "session");
			if (documents.size() > 0) {
				log.info("autorizado!");
				autorizado = true;
			} else {
				log.info("not autorizado!");
			}

			if (!autorizado) {
				halt(401, "No eres bienvenido aqui");
			}
			return "Proceso Exitoso";
		} else {
			halt(401, "No eres bienvenido aqui");
			return "incorrect";
		}
	}
}
