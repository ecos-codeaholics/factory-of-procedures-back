/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import static spark.Spark.halt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;

import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

/**
 * 
 */
/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: Authorization Authorization.java
 * 
 * Original Author: @author davidMtz and @author AOSORIO
 * 
 * Description: Autorization works as a Stateless Session Manager (does not keep
 * track of the state of sessions.
 * 
 * Implementation: Autorization method JWT
 *
 * Created: Oct 28, 2016 9:15:25 AM
 * 
 */
public final class Authorization {

	// Atributos
	private final static Logger log = LogManager.getLogger(Authorization.class);

	private static String authenticationMethod = "JWT"; // ... JWT, Simple

	public static final String TOKEN_EMAIL_KEY = "jti";

	// Metodos

	/**
	 * Generic procedure to autorize any User with a provided Profile (citizen,
	 * functionary, admin)
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param pUserProfile
	 * @return Message (String) depending on the authorization result
	 */
	public static String authorizeUser(Request pRequest, Response pResponse, String pUserProfile) {

		if (pRequest.headers("Authorization") == null) {
			return "No Authorization in header. Ignore.";
		}

		boolean isAutorized = false;
		String token = pRequest.headers("Authorization").split(" ")[1];

		log.info("Token: " + token);

		if (!token.isEmpty()) {

			try {

				Document session = new Document();
				session.append("token", token);
				session.append("user-profile", pUserProfile);

				isAutorized = findSession(session);

			} catch (ExpiredTokenException authEx) {

				log.info(authEx.getMessage());
				isAutorized = false;

			} catch (FunctionalityAuthorizationException authEx) {

				log.info(authEx.getMessage());
				isAutorized = false;
			} catch (Exception ex ) {
				
				log.info("Something went wrong!");
				ex.getMessage();
			}

			if (!isAutorized) {
				halt(401, "No eres bienvenido aqui");
				return "No hay autorizacion";
			}

		}

		log.info("Termina proceso de autorizacion con exito");
		
		return "Proceso de autorizacion realizado";

	}

	/**
	 * Valida si lo datos corresponden a ciudadano con una sesion creada en el
	 * sistema.
	 *
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return resultado de la verificacion
	 */
	public static String authorizeCitizen(Request pRequest, Response pResponse) {

		return authorizeUser(pRequest, pResponse, Constants.CITIZEN_USER_PROFILE);

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

		return authorizeUser(pRequest, pResponse, Constants.FUNCTIONARY_USER_PROFILE);

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

		return authorizeUser(pRequest, pResponse, Constants.ADMIN_USER_PROFILE);

	}

	/**
	 * looks for a a session in DB. Checks arriving token validity
	 * 
	 * @param pSession
	 * @return
	 * @throws FunctionalityAuthorizationException
	 * @throws ExpiredTokenException
	 */
	public static boolean findSession(Document pSession)
			throws FunctionalityAuthorizationException, ExpiredTokenException {

		boolean isAuthorized = false;

		ArrayList<Document> documents = DataBaseUtil.find(pSession, Constants.SESSION_COLLECTION);

		if (documents.size() > 0) {

			log.info("User has a open session");

			if (authenticationMethod.equals("JWT")) {

				try {

					Document session = documents.get(0);
					String storedSalt = session.getString("salt");

					checkTokenValidity(pSession.getString("token"), storedSalt);
					isAuthorized = true;

				} catch (ExpiredTokenException ex) {
					log.info(ex.getMessage());
					throw ex;
				} catch ( Exception ex ) {
					log.info("Something went wrong!");
					ex.getMessage();
				}
				
				log.info("Usuario tiene sesion valida");
				
			}

		} else {
			log.info("Usuario no tiene sesion");
			isAuthorized = false;
			throw new FunctionalityAuthorizationException("No se encuentra sesion del usuario", "402");
		}

		return isAuthorized;

	}

	/**
	 * Checks the validity of the token - if token has expired throws expection
	 * 
	 * @param pToken
	 * @param pSalt
	 * @throws ExpiredTokenException
	 *             : custom exception (wraps the jwt library exception).
	 */
	private static void checkTokenValidity(String pToken, String pSalt) throws ExpiredTokenException {

		// 1. Extract expiration date
		try {

			String email = (String) getTokenClaim(pToken, pSalt, TOKEN_EMAIL_KEY);

			log.info("token has email: " + email);

			Claims claims = Jwts.parser().setSigningKey(pSalt).parseClaimsJws(pToken).getBody();

			log.info("Token is currently valid");

			Date expDate = claims.getExpiration();
			log.info("Expiration: " + expDate);

			long nowMillis = System.currentTimeMillis();
			Date now = new Date(nowMillis);

			long diff = now.getTime() - expDate.getTime();

			if (diff < (60000)) { // this token will expired soon

			}

		} catch (ExpiredJwtException jwtEx) {
			log.info(jwtEx.getLocalizedMessage());
			throw new ExpiredTokenException("Token ha expirado", "505");
		}

	}

	/**
	 * Get from token the correspoding Claim with key pKey - this method uses
	 * the signature (+secret)
	 * 
	 * @param pToken
	 * @param pSalt
	 *            : the secret used to verify the token
	 * @param pKey
	 * @return
	 * @throws ExpiredJwtException
	 *             : in case the token has expired
	 */
	public static Object getTokenClaim(String pToken, String pSalt, String pKey) throws ExpiredJwtException {

		Object obj = null;
		try {
			obj = Jwts.parser().setSigningKey(pSalt).parseClaimsJws(pToken).getBody().get(pKey);
		} catch (ExpiredJwtException jwtEx) {
			throw jwtEx;
		}
		return obj;

	}

	/**
	 * Get from token body the corresponding Claim with key pKey
	 * 
	 * @param pJwt
	 * @param pKey
	 * @return
	 * @throws InvalidTokenException
	 *             : in case the token has not three parts or if claim is not
	 *             found
	 */
	public static String getTokenClaim(String pJwt, String pKey) throws InvalidTokenException {

		String[] jwtElements = pJwt.split("\\.");

		if (jwtElements.length < 3) {
			throw new InvalidTokenException("Token is invalid", "505");
		}

		String value = null;

		ByteArrayInputStream inStream = new ByteArrayInputStream(Base64.getDecoder().decode(jwtElements[1]));

		BufferedReader streamReader = new BufferedReader(new InputStreamReader(inStream));
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;

		try {
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);

			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(responseStrBuilder.toString()).getAsJsonObject();

			if (json.getAsJsonObject().has(pKey)) {
				value = json.getAsJsonObject().get(pKey).getAsString();
				log.info("Found key with value: " + value);
			} else {
				throw new InvalidTokenException("Token is invalid", "505");
			}

		} catch (IOException e) {
			e.printStackTrace();

		}

		return value;
	}

	/** Get from the Request, the token and then from it extract the needed Claim
	 * @param pRequest
	 * @param pClaim
	 * @return
	 * @throws InvalidTokenException
	 */
	public static String getFromToken( Request pRequest, String pClaim ) throws  InvalidTokenException {
		
		String token = pRequest.headers("Authorization").split(" ")[1];
		try {
			return Authorization.getTokenClaim(token, pClaim);	
		} catch ( InvalidTokenException ex ) {
			throw ex;
		}
	
	}
		
}
