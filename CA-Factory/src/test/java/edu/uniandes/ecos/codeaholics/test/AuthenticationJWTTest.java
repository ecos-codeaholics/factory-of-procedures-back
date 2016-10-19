/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.uniandes.ecos.codeaholics.config.AuthenticationJWT;
import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class AuthenticationJWTTest {

	Logger logger = LogManager.getLogger(AuthenticationJWTTest.class);
	
	public String token;
		
	@Test
	public void tokenCreationTest() {

		TestsUtil utilities = new TestsUtil();
		utilities.addCitizen("Andres", "Osorio", "aosorio@uniandes", "12345678");
		String citizenSalt = utilities.getCitizenSalt();
		
		AuthenticationJWT jwtToken = new AuthenticationJWT();
		
		
		boolean authenticated = false;
		try {
			authenticated = jwtToken.doAuthentication("aosorio@uniandes", "12345678", "citizen");
		} catch (WrongUserOrPasswordException e1) {
			e1.printStackTrace();
		}

		if (authenticated) {
			token = (String) jwtToken.getAnswer();
			logger.info(token);

			//Verify and decode
			try {
				
				Claims claims = Jwts.parser().setSigningKey(citizenSalt)
						.parseClaimsJws(token).getBody();

				logger.info("ID: " + claims.getId());
				logger.info("Subject: " + claims.getSubject());
				logger.info("Issuer: " + claims.getIssuer());
				logger.info("Expiration: " + claims.getExpiration());

				assertEquals("aosorio@uniandes", claims.getId());
				
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
