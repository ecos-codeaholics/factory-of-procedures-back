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
	public static boolean doAuthenticationCitizen(String pEmail, String pPwd) {
		
		boolean authenticated = false;
		log.info("Verifying user data...");
		Document user = new Document();
		user.append("email", pEmail);

		ArrayList<Document> documents = DataBaseUtil.find(user, "citizen");

		if (documents.isEmpty()) {
			log.info("User Doesn't Exist");
		} else {
			String salt = documents.get(0).get("salt").toString();
			String[] hash = GeneralUtil.getHash(pPwd, salt);

			user.append("password", hash[1]);

			ArrayList<Document> results = DataBaseUtil.find(user, "citizen");
			if (results.size() > 0) {
				log.info( pEmail+ " authenticated!");
				createSession(pEmail, "citizen");
				authenticated = true;
			} else {
				log.info("Wrong password");
			}
		}

		return authenticated;
	}
	
	/**
	 * Valida los datos contra la bd.
	 *
	 * @param pEmail correo del ususario
	 * @param pPwd contraeeña del ususario
	 * @return resultado de la autenticacion
	 */
	public static boolean doAuthenticationFuntionary(String pEmail, String pPwd, String pProfile) {
		
		boolean authenticated = false;
		log.info("Verifying user data...");
		Document user = new Document();
		user.append("email", pEmail);

		ArrayList<Document> documents = DataBaseUtil.find(user, "citizen");

		if (documents.isEmpty()) {
			log.info("User Doesn't Exist");
		} else {
			user.append("userProfile", "citizen");
			ArrayList<Document> documents2 = DataBaseUtil.find(user, "citizen");
			if(documents2.isEmpty()){
				log.info("User Profile Wrong");
			}else{
				String salt = documents.get(0).get("salt").toString();
				String[] hash = GeneralUtil.getHash(pPwd, salt);

				user.append("password", hash[1]);

				ArrayList<Document> results = DataBaseUtil.find(user, "citizen");
				if (results.size() > 0) {
					log.info( pEmail+ " authenticated!");
					createSession(pEmail, pProfile);
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
	private static void createSession(String pEmail, String pUserProfile) {

		Document session = new Document();
		session.append("email", pEmail);
		session.append("user-profile", pUserProfile);
		log.info("Creating Session...");
		try {
			DataBaseUtil.save(session, "session");

		} catch (MongoWriteException e) {

			if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
				log.info("Already exist session for user: " + pEmail);
			}
			throw e;
		}
	}
	
	/**
	 * * Cierra la sesion de un usuario dado su email.
	 *
	 * @param pEmail correo del ususario al que se le crea la sesion
	 */
	public static void closedSession(String pEmail) {
		Document session = new Document();
		session.append("email", pEmail);
		log.info("Closing Session...");
		DataBaseUtil.delete(session, "session");
	}
}
