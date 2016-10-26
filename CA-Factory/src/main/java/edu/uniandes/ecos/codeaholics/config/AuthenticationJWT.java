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

	private Object token;

	public static final long TOKEN_LIFETIME = 1000 * 600; // 10 min
	public static final String TOKEN_ISSUER = "codeaholics";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uniandes.ecos.codeaholics.config.IAuthenticationSvc#doAuthentication(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean doAuthentication(String pEmail, String pPwd, String pProfile) throws WrongUserOrPasswordException {

		boolean authenticated = false;
		log.info("Verifying user data...");
		Document user = new Document();
		user.append("email", pEmail);

		ArrayList<Document> documents = DataBaseUtil.find(user, pProfile);

		if (documents.isEmpty()) {
			log.info("Usuario no existe");
			throw new WrongUserOrPasswordException("El usuario que ingresaste no existe", "101");
		} else {
			user.append("userProfile", pProfile);
			ArrayList<Document> documents2 = DataBaseUtil.find(user, pProfile);
			if (documents2.isEmpty()) {
				log.info("Informacion de usuario equivocada");
				throw new WrongUserOrPasswordException("Informacion de usuario equivocada", "104");
			} else {

				String salt = documents.get(0).get("salt").toString();
				String[] hash = GeneralUtil.getHash(pPwd, salt);

				user.append("password", hash[1]);

				ArrayList<Document> results = DataBaseUtil.find(user, pProfile);

				if (results.size() > 0) {
					log.info(pEmail + " authenticated! create token.");
					token = createJWT(pEmail, TOKEN_ISSUER, pProfile, salt, "Something");
					authenticated = true;
					createSession(pEmail, pProfile, token.toString(), salt);

				} else {
					token = "{}";
					authenticated = false;
					log.info("Clave equivocada");
					throw new WrongUserOrPasswordException("La clave que ingresaste es incorrecta", "103");
				}
			}
		}

		return authenticated;

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
	private String createJWT(String pId, String pIssuer, String pProfile, String pSalt, String pSubject) {

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
		builder.setSubject(pSubject);
		builder.setIssuer(pIssuer);
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
	private static void createSession(String pEmail, String pUserProfile, String pJwtToken, String pSalt) {

		Document session = new Document();
		session.append("email", pEmail);
		session.append("user-profile", pUserProfile);
		session.append("token", pJwtToken);
		session.append("salt", pSalt);
		log.info("Creating Session...");
		
		try {
			
			Document prevSession = new Document();
			prevSession.append("email", pEmail);
			ArrayList<Document> documents = DataBaseUtil.find(prevSession, "session");

			if (documents.isEmpty()) {
				DataBaseUtil.save(session, "session");
			} else {
				log.info("session alreadery exists for: " + pEmail);
			}
			
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
