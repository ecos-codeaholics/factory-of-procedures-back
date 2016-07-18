package edu.uniandes.ecos.codeaholics.config;

import static spark.Spark.halt;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import edu.uniandes.ecos.codeaholics.persistence.Sesion;
import spark.Request;
import spark.Response;

/**
 * Created by davidMtz on 26/6/16.
 */
public final class Authorization {

	// Atributos
	private final static Logger log = LogManager.getLogger(Authorization.class);

	private static Gson GSON = new GsonBuilder()
			.serializeNulls()
			.create();

	// Metodos
	/**
	 * * Valida si lo datos corresponden a un paciente registrado.
	 *
	 * @param pEmail correo del ususario
	 * @param pPwd contraeeña del ususario
	 * @return resultado de la autenticacion
	 */
	public static String doDocAuthorization(Request req, Response res) {
		boolean authorized = false;
    	try {
    		Sesion data = GSON.fromJson(req.body(), Sesion.class);
			Document sesion = new Document();
			sesion.append("email", data.getEmail());
			sesion.append("user-profile", "doctor");
			ArrayList<Document> documents = DataBaseUtil.find(sesion, "sesion");
			if (documents.size() > 0) {
				log.info("authorized!");
				authorized = true;
			} else {
				log.info("not authorized!");
			}
    		
    		if (!authorized) {
				halt(401, "You are not welcome here");
			}
    		return "success";
			
		} catch (JsonSyntaxException e) {
			res.status(400);
			return "invalid json format";
		}

	}
}
