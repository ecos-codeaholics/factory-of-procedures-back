/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.uniandes.ecos.codeaholics.config.AuthenticationJWT;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.Constants;
import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;
import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.ExpiredTokenException;
import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.FunctionalityAuthorizationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationJWTTest {

	Logger logger = LogManager.getLogger(AuthenticationJWTTest.class);

	private static final long TOKEN_LIFETIME = 1000 * 600; // 10 min
	private static final String TOKEN_ISSUER = "http://codeaholics.dynns.com";

	private final static String USER_EMAIL = "dbernal@uniandes.edu.co";
	private final static String USER_PWD = "12345678";
	private final static String USER_NAME = "David";
	private final static String USER_LASTNAME = "Bernal";
	private final static String USER_LASTNAME2 = "Diaz";
	
	private String token;

	@Test
	public void tokenCreationTest() {

		TestsUtil utilities = new TestsUtil();
		utilities.addCitizen(USER_NAME, USER_LASTNAME, USER_LASTNAME2, USER_EMAIL, USER_PWD);
		String citizenSalt = utilities.getCitizenSalt();

		AuthenticationJWT jwtToken = new AuthenticationJWT();

		boolean authenticated = false;
		try {
			authenticated = jwtToken.doAuthentication(USER_EMAIL, USER_PWD, Constants.CITIZEN_COLLECTION);
		} catch (WrongUserOrPasswordException e1) {
			e1.printStackTrace();
		}

		if (authenticated) {
			token = (String) jwtToken.getAnswer();
			logger.info(token);

			// Verify and decode
			try {

				logger.info("JWT is signed? " + Jwts.parser().isSigned(token));

				Claims claims = Jwts.parser().setSigningKey(citizenSalt).parseClaimsJws(token).getBody();

				logger.info("ID: " + claims.getId());
				logger.info("Subject: " + claims.getSubject());
				logger.info("Issuer: " + claims.getIssuer());
				logger.info("Expiration: " + claims.getExpiration());

				decode(token);

				assertEquals(USER_EMAIL, claims.getId());

			} catch (Exception e) {
				e.printStackTrace();
				assertFalse(true);
				logger.info("Cannot get token claims");
			}
		} else {
			logger.info("User not authenticated");
			assertFalse(true);
		}

		AuthenticationJWT.closeSession(USER_EMAIL);
		
		utilities.removeCitizen(USER_EMAIL);

	}

	@Test
	public void sessionTest() {

		TestsUtil utilities = new TestsUtil();
		utilities.addCitizen(USER_NAME, USER_LASTNAME, USER_LASTNAME2, USER_EMAIL, USER_PWD);

		AuthenticationJWT jwtToken = new AuthenticationJWT();

		boolean authenticated = false;

		try {
			authenticated = jwtToken.doAuthentication(USER_EMAIL, USER_PWD, Constants.CITIZEN_COLLECTION);
		} catch (WrongUserOrPasswordException e1) {
			e1.printStackTrace();
		}

		boolean isAutorized = false;

		try {

			Document session = new Document();
			session.append("email", USER_EMAIL);
			session.append("user-profile", Constants.CITIZEN_USER_PROFILE);

			if (authenticated) {
				token = (String) jwtToken.getAnswer();
				logger.info(token);
			}

			// Check against token
			session.append("token", token);

			isAutorized = Authorization.findSession(session);

		} catch (ExpiredTokenException authEx) {

			logger.info(authEx.getMessage());
			isAutorized = false;

		} catch (FunctionalityAuthorizationException authEx) {

			logger.info(authEx.getMessage());
			isAutorized = false;
		}
		assertTrue(isAutorized);

		AuthenticationJWT.closeSession(USER_EMAIL);
		
		utilities.removeCitizen(USER_EMAIL);
		
	}

	@Test
	public void tokenExpirationTest() {

		TestsUtil utilities = new TestsUtil();

		utilities.addCitizen("Emily", "Nurse", "Cox", "emily@uniandes.edu.co", "12345678");
		String citizenSalt = utilities.getCitizenSalt();
		logger.info(citizenSalt);

		String expToken = createExpiredJWT("emily@uniandes.edu.co", citizenSalt);
		logger.info(expToken);

		utilities.addSession("emily@uniandes.edu.co", Constants.CITIZEN_USER_PROFILE, expToken, citizenSalt);

		boolean isAutorized = false;

		try {

			Document session = new Document();
			session.append("email", "emily@uniandes.edu.co");
			session.append("user-profile", Constants.CITIZEN_USER_PROFILE);

			// Check against token
			session.append("token", expToken);

			isAutorized = Authorization.findSession(session);

		} catch (ExpiredJwtException authEx) {
			logger.info(authEx.getMessage());
			isAutorized = false;

		} catch (ExpiredTokenException authEx) {

			logger.info(authEx.getMessage());
			isAutorized = false;

		} catch (Exception ex) {

			logger.info(ex.getMessage());
			isAutorized = false;
		}

		assertFalse(isAutorized);
		
		AuthenticationJWT.closeSession("emily@uniandes.edu.co");
		
		utilities.removeCitizen("emily@uniandes.edu.co");

	}

	public String createExpiredJWT(String pEmail, String pSalt) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		long expMillis = nowMillis - TOKEN_LIFETIME;
		Date exp = new Date(expMillis);

		logger.info(pSalt);

		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(pEmail);
		builder.setIssuedAt(now);
		builder.setSubject("Something");
		builder.setIssuer(TOKEN_ISSUER);
		builder.signWith(signatureAlgorithm, pSalt);
		builder.setExpiration(exp);
		builder.setAudience(Constants.CITIZEN_USER_PROFILE);

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	private void decode(String jwt) {

		String[] jwtElements = jwt.split("\\.");

		ByteArrayInputStream inStream = new ByteArrayInputStream(Base64.getDecoder().decode(jwtElements[0]));

		BufferedReader streamReader = new BufferedReader(new InputStreamReader(inStream));
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;

		try {
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);

			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(responseStrBuilder.toString()).getAsJsonObject();

			if (json.getAsJsonObject().has("alg")) {
				logger.info("token header algo: " + json.getAsJsonObject().get("alg"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
