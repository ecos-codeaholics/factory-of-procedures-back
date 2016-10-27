/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import static spark.Spark.halt;

import java.util.ArrayList;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import spark.Request;
import spark.Response;

import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

/**
 * Created by davidMtz on 26/6/16.
 */
public final class Authorization {

	// Atributos
	private final static Logger log = LogManager.getLogger(Authorization.class);

	private static String authenticationMethod = "JWT"; // ... JWT, Simple

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
	public static String authorizeCitizen(Request pRequest, Response pResponse) {

		boolean isAutorized = false;

		String auth = pRequest.headers("Authorization");
		if (auth != null ) {

				try {

					Document session = new Document();
					session.append("user-profile", "citizen");

					// Check against token if authentication method is JWT
					if (authenticationMethod.equals("JWT")) {

						String token = pRequest.headers("Authorization").split(" ")[1];
						session.append("token", token);
					}

					isAutorized = findSession(session);

				} catch (NotMatchingTokenException authEx) {
					log.info(authEx.getMessage());
					isAutorized = false;

				} catch( ExpiredTokenException authEx ) {

					log.info(authEx.getMessage());
					isAutorized = false;

				} catch (FunctionalityAuthorizationException authEx) {

					log.info(authEx.getMessage());
					isAutorized = false;
				}

			if (!isAutorized) {
				halt(401, "No eres bienvenido aqui");
				return "No hay autorizacion";
			}

			return "Proceso de autorizacion Exitoso";

		}

		return "";
	}

	private static void checkTokenValidity(String pToken, String pSalt) throws ExpiredJwtException, ExpiredTokenException {
		
		//1. Extract expiration date
		try {
			
			Claims claims = Jwts.parser().setSigningKey(pSalt)
					.parseClaimsJws(pToken).getBody();
			
			Date expDate = claims.getExpiration();
			log.info("Expiration: " + expDate);
			
			long nowMillis = System.currentTimeMillis();
			Date now = new Date(nowMillis);

			//2. Compare with current time
			if( now.after(expDate)){
				throw new ExpiredTokenException("Token ha expirado","505");
			} else {
				log.info("Token is currently valid");
			}
			
		} catch ( ExpiredJwtException jwtEx ) {
			
			throw jwtEx;
		}
			
	}

	public static boolean findSession(Document pSession) throws FunctionalityAuthorizationException, 
		NotMatchingTokenException, ExpiredTokenException {

		boolean isAuthorized = false;

		ArrayList<Document> documents = DataBaseUtil.find(pSession, "session");

		if (documents.size() > 0) {
			
			log.info("Usuario tiene sesion");

			if (authenticationMethod.equals("JWT")) {
				
				try {
					
					Document session = documents.get(0);
					
					String sentToken   = pSession.getString("token");
					String storedToken = session.getString("token");				
					String storedSalt  = session.getString("salt");
					
					compareTokens(sentToken,storedToken);
					checkTokenValidity(pSession.getString("token"),storedSalt);
					log.info("Usuario tiene sesion valida");
					isAuthorized = true;
					
				} catch (NotMatchingTokenException ex) {
					log.info(ex.getMessage());
					throw ex;
				}
 				
				catch (ExpiredTokenException ex) {
					log.info(ex.getMessage());
					throw ex;
				}
			}
			
		} else {
			log.info("Usuario no autorizado!");
			isAuthorized = false;
			throw new FunctionalityAuthorizationException("No se encuentra sesion del usuario", "402");
		}

		return isAuthorized;

	}

	private static void compareTokens(String sentToken, String storedToken) throws NotMatchingTokenException {

		if( !sentToken.equals(storedToken) ) {
			throw new NotMatchingTokenException("Token invalido, no concuerda", "505");
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
	public static String authorizeFuntionary(Request pRequest, Response pResponse) {
		boolean autorizado = false;

		String email = pRequest.queryParams("email");
		// log.info("Correo= "+email);
		if (email != null && !email.equals("")) {
			Document session = new Document();
			session.append("email", email);
			session.append("user-profile", "functionary");
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
	public static String authorizeAdmin(Request pRequest, Response pResponse) {
		boolean autorizado = false;

		String email = pRequest.queryParams("email");
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
