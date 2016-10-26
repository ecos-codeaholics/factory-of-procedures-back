package edu.uniandes.ecos.codeaholics.config;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;

import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;

/**
 * Created by davidMtz on 26/6/16.
 */
public final class Authentication implements IAuthenticationSvc {

	// Atributos
	private final static Logger log = LogManager.getLogger(Authentication.class);

	private Object responseObj;

	// Metodos
	@Override
	public boolean doAuthentication(String pEmail, String pPwd, String pProfile) throws WrongUserOrPasswordException {

		boolean authenticated = false;
		log.info("Verifying user data...");
		Document user = new Document();
		user.append("email", pEmail);

		ArrayList<Document> documents = DataBaseUtil.find(user, pProfile);

		if (documents.isEmpty()) {
			log.info("Usuario no existe");
			throw new WrongUserOrPasswordException("Usuario no existe", "101");
		} else {
			user.append("userProfile", pProfile);
			ArrayList<Document> documents2 = DataBaseUtil.find(user, pProfile);
			if (documents2.isEmpty()) {
				log.info("Perfil erroneo o no encontrado");
				throw new WrongUserOrPasswordException("Perfil erroneo o no encontrado", "102");
			} else {
				String salt = documents.get(0).get("salt").toString();
				String[] hash = GeneralUtil.getHash(pPwd, salt);

				user.append("password", hash[1]);

				ArrayList<Document> results = DataBaseUtil.find(user, pProfile);

				if (results.size() > 0) {
					log.info(pEmail + " authenticated!");
					createSession(pEmail, pProfile);
					authenticated = true;
					user.remove("password");
					user.remove("userProfile");
					responseObj = user;

				} else {
					responseObj = "{}";
					authenticated = false;
					log.info("Clave equivocada");
					throw new WrongUserOrPasswordException("Clave equivocada", "103");
				}
			}
		}

		return authenticated;

	}

	@Override
	public Object getAnswer() {
		return responseObj;
	}

	/**
	 * * Crea una sesion para un usuario dado su email.
	 * 
	 * @param pEmail
	 *            correo del usuario al que se le crea la sesion
	 * @param pUserProfile
	 *            perfil del usuario citizen, functionary, etc
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
	 * @param pEmail
	 *            correo del ususario al que se le crea la sesion
	 */
	public static void closeSession(String pEmail) {
		Document session = new Document();
		session.append("email", pEmail);
		log.info("Closing Session...");
		DataBaseUtil.delete(session, "session");
	}

}
