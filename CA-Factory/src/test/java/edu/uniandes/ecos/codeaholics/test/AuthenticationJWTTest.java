/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import edu.uniandes.ecos.codeaholics.config.AuthenticationJWT;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;
import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.ExpiredTokenException;
import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.FunctionalityAuthorizationException;
import edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException.NotMatchingTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationJWTTest {

	Logger logger = LogManager.getLogger(AuthenticationJWTTest.class);

	public static final long TOKEN_LIFETIME = 1000 * 600; // 10 min
	public static final String TOKEN_ISSUER = "codeaholics";

	private final static String USER_EMAIL = "dbernal@uniandes";
	private final static String USER_PWD = "12345678";
	private final static String USER_NAME = "David";
	private final static String USER_LASTNAME = "Bernal";

	private String token;

	@Test
	public void tokenCreationTest() {

		TestsUtil utilities = new TestsUtil();
		utilities.addCitizen(USER_NAME, USER_LASTNAME, USER_EMAIL, USER_PWD);
		String citizenSalt = utilities.getCitizenSalt();

		AuthenticationJWT jwtToken = new AuthenticationJWT();

		boolean authenticated = false;
		try {
			authenticated = jwtToken.doAuthentication(USER_EMAIL, USER_PWD, "citizen");
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
		
	}

	@Test
	public void sessionTest() {

		TestsUtil utilities = new TestsUtil();
		utilities.addCitizen(USER_NAME, USER_LASTNAME, USER_EMAIL, USER_PWD);

		AuthenticationJWT jwtToken = new AuthenticationJWT();

		boolean authenticated = false;

		try {
			authenticated = jwtToken.doAuthentication(USER_EMAIL, USER_PWD, "citizen");
		} catch (WrongUserOrPasswordException e1) {
			e1.printStackTrace();
		}

		boolean isAutorized = false;

		try {

			Document session = new Document();
			session.append("email", USER_EMAIL);
			session.append("user-profile", "citizen");

			if (authenticated) {
				token = (String) jwtToken.getAnswer();
				logger.info(token);
			}

			// Check against token
			session.append("token", token);

			isAutorized = Authorization.findSession(session);

		} catch (NotMatchingTokenException authEx) {
			logger.info(authEx.getMessage());
			isAutorized = false;

		} catch (ExpiredTokenException authEx) {

			logger.info(authEx.getMessage());
			isAutorized = false;

		} catch (FunctionalityAuthorizationException authEx) {

			logger.info(authEx.getMessage());
			isAutorized = false;
		}
		assertTrue(isAutorized);
		
		AuthenticationJWT.closeSession(USER_EMAIL);
		
	}

	@Test
	public void tokenExpirationTest() {

		TestsUtil utilities = new TestsUtil();

		utilities.addCitizen("Emily", "Nurse", "emily@uniandes", "12345678");
		String citizenSalt = utilities.getCitizenSalt();
		logger.info(citizenSalt);
		
		String expToken = createExpiredJWT("emily@uniandes", citizenSalt);
		logger.info(expToken);

		utilities.addSession("emily@uniandes", "citizen", expToken, citizenSalt);

		boolean isAutorized = false;

		try {

			Document session = new Document();
			session.append("email", "emily@uniandes");
			session.append("user-profile", "citizen");

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
		AuthenticationJWT.closeSession("emily@uniandes");

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
		builder.setAudience("citizen");

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

}
