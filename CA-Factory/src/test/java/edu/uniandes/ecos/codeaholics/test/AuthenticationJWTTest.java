package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.uniandes.ecos.codeaholics.config.AuthenticationJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class AuthenticationJWTTest {

	Logger logger = LogManager.getRootLogger();
	
	public String token;
		
	@Test
	public void tokenCreationTest() {

		TestsUtil utilities = new TestsUtil();
		utilities.addCitizen("Andres", "Osorio", "aosorio@uniandes.edu", "Qwerty");
		String citizenSalt = utilities.getCitizenSalt();
		
		AuthenticationJWT jwtToken = new AuthenticationJWT();
		boolean authenticated = jwtToken.doAuthentication("aosorio@uniandes.edu", "Qwerty", "citizen");

		if (authenticated) {
			token = jwtToken.getAnswer();
			logger.info(token);

			//Verify and decode
			try {
				
				Claims claims = Jwts.parser().setSigningKey(citizenSalt)
						.parseClaimsJws(token).getBody();

				logger.info("ID: " + claims.getId());
				logger.info("Subject: " + claims.getSubject());
				logger.info("Issuer: " + claims.getIssuer());
				logger.info("Expiration: " + claims.getExpiration());

				assertEquals("aosorio@uniandes.edu", claims.getId());
				
			} catch (Exception e) {
				e.printStackTrace();
				assertFalse(true);
				logger.info("Cannot get token claims");
			}
		} else {
			logger.info("User not authenticated");
			assertFalse(true);
		}

	} 

}
