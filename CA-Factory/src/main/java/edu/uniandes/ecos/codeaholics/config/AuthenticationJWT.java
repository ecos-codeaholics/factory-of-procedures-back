/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.util.ArrayList;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;

import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.SessionAlreadyExistsException;
import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: AuthenticationJWT AuthenticationJWT.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 12, 2016 6:36:05 PM
 * 
 */
public class AuthenticationJWT implements IAuthenticationSvc {

	// Atributos
	private final static Logger log = LogManager.getLogger(AuthenticationJWT.class);

	private static Object token;

	private static final long TOKEN_LIFETIME = 1000 * 600; // 10 min

	private static final String TOKEN_ISSUER = "http://codeaholics.dynns.com";

	private static final String TOKEN_SUBJECT = "A subject";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uniandes.ecos.codeaholics.config.IAuthenticationSvc#doAuthentication(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean doAuthentication(String pEmail, String pPwd, String pProfile) throws WrongUserOrPasswordException {

		boolean isAuthenticated = false;
		log.info("Verifying user data...");
		Document user = new Document();
		user.append("email", pEmail);

		ArrayList<Document> documents = DataBaseUtil.find(user, pProfile);

		// 1. check if user exists in the DB
		if (documents.isEmpty()) {

			log.info("Usuario no existe");
			throw new WrongUserOrPasswordException("El usuario que ingresaste no existe", "101");
		} else {

			// 2. Create new session
			try {

				String salt = documents.get(0).get("salt").toString();
				String[] hash = GeneralUtil.getHash(pPwd, salt);
				user.append("password", hash[1]);

				String functioryRol = ValidateUser(user, pProfile);

				log.info(pEmail + " has passwd checked. go ahead and create session with token.");

				createSession(pEmail, functioryRol, salt);

				log.info("New session created with token: " + token.toString());

				isAuthenticated = true;

			} catch (WrongUserOrPasswordException valEx) {

				token = "{}";
				log.info("Clave equivocada");
				isAuthenticated = false;
				throw valEx;
			}

		}

		return isAuthenticated;

	}

	private String ValidateUser(Document user, String pProfile) throws WrongUserOrPasswordException {
		String rol = "";
		ArrayList<Document> results = DataBaseUtil.find(user, pProfile);

		if (results.isEmpty()) {
			throw new WrongUserOrPasswordException("La clave que ingresaste es incorrecta", "103");
		}else{
			rol = results.get(0).get("userProfile").toString();
		}
		return rol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uniandes.ecos.codeaholics.config.IAuthenticationSvc#getAnswer()
	 */
	@Override
	public Object getAnswer() {
		return token;
	}

	/**
	 * @param id
	 * @param issuer
	 * @param subject
	 * @param ttlMillis
	 * @return
	 */
	private static String createJWT(String pId, String pProfile, String pSalt) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		long expMillis = nowMillis + TOKEN_LIFETIME;
		Date exp = new Date(expMillis);

		log.debug(pSalt);

		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(pId);
		builder.setIssuedAt(now);
		builder.setSubject(TOKEN_SUBJECT);
		builder.setIssuer(TOKEN_ISSUER);
		builder.signWith(signatureAlgorithm, pSalt);
		builder.setExpiration(exp);
		builder.setAudience(pProfile);

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();

	}

	/**
	 * * Crea una sesion para un usuario dado su email.
	 * 
	 * @param pEmail
	 *            correo del usuario al que se le crea la sesion
	 * @param pUserProfile
	 *            perfil del usuario citizen, functionary, etc
	 */
	private static void createSession(String pEmail, String pProfile, String pSalt) {

		token = createJWT(pEmail, pProfile, pSalt);

		Document session = new Document();
		session.append("email", pEmail);
		session.append("user-profile", pProfile);
		session.append("token", token);
		session.append("salt", pSalt);
		log.info("Creating Session...");

		try {

			hasSession(pEmail);

			DataBaseUtil.save(session, Constants.SESSION_COLLECTION);

		} catch (SessionAlreadyExistsException ssEx) {

			log.info(ssEx.getMessage() + " : " + pEmail);
			closeSession(pEmail);
			DataBaseUtil.save(session, Constants.SESSION_COLLECTION);

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
		DataBaseUtil.delete(session, Constants.SESSION_COLLECTION);
	}

	public static void hasSession(String pEmail) throws SessionAlreadyExistsException {

		Document prevSession = new Document();
		prevSession.append("email", pEmail);
		ArrayList<Document> documents = DataBaseUtil.find(prevSession, Constants.SESSION_COLLECTION);

		if (!documents.isEmpty()) {
			throw new SessionAlreadyExistsException("Session for this user already exists", "105");
		}

	}

}
