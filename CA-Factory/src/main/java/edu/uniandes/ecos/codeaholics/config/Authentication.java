package edu.uniandes.ecos.codeaholics.config;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;

/**
 * Created by davidMtz on 26/6/16.
 */
public final class Authentication {

	// Atributos
	private final static Logger log = LogManager.getLogger(Authentication.class);

	// Metodos
	/**
	 * Valida los datos contra la bd.
	 *
	 * @param pEmail correo del ususario
	 * @param pPwd contraeeña del ususario
	 * @return resultado de la autenticacion
	 */
	public static boolean doAuthentication(String pEmail, String pPwd, String pProfile) {
		
		boolean authenticated = false;
		log.info("Verifying user data...");
		Document user = new Document();
		user.append("email", pEmail);

		ArrayList<Document> documents = DataBaseUtil.find(user, "user");

		if (documents.isEmpty()) {
			log.info("User Doesn't Exist");
		} else {
			user.append("user-profile", "user");
			ArrayList<Document> documents2 = DataBaseUtil.find(user, "user");
			if(documents2.isEmpty()){
				log.info("User Profile Wrong");
			}else{
				String salt = documents.get(0).get("salt").toString();
				String[] hash = GeneralUtil.getHash(pPwd, salt);

				user.append("password", hash[1]);

				ArrayList<Document> results = DataBaseUtil.find(user, "user");
				if (results.size() > 0) {
					log.info( pEmail+ " authenticated!");
					createSesion(pEmail, pProfile);
					authenticated = true;
				} else {
					log.info("Wrong password");
				}
			}
		}

		return authenticated;
	}

	/**
	 * * Crea una sesion para un usuario dado su email.
	 *
	 * @param pEmail correo del ususario al que se le crea la sesion
	 */
	private static void createSesion(String pEmail, String pUserProfile) {

		Document sesion = new Document();
		sesion.append("email", pEmail);
		sesion.append("user-profile", pUserProfile);
		log.info("Creating Session...");
		try {
			DataBaseUtil.save(sesion, "sesion");

		} catch (MongoWriteException e) {

			if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
				log.info("Already exist sesion for user: " + pEmail);
			}
			throw e;
		}
	}
	
	/**
	 * * Cierra la sesion de un usuario dado su email.
	 *
	 * @param pEmail correo del ususario al que se le crea la sesion
	 */
	private static void closedSesion(String pEmail) {
		Document sesion = new Document();
		sesion.append("email", pEmail);
		log.info("Closing Session...");
		DataBaseUtil.delete(sesion, "sesion");
	}
}
