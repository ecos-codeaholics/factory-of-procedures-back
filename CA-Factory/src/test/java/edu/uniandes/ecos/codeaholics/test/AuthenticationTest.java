/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.Constants;
import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: AuthenticationTest AuthenticationTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: This is a test for the normal authentication procedure
 * 
 * Implementation: you send email&password returns email
 *
 * Created: Aug 12, 2016 11:48:02 PM
 * 
 */
public class AuthenticationTest {

	Logger logger = LogManager.getLogger(AuthenticationTest.class);
	
	private final static String USER_EMAIL = "dbernal@uniandes.edu.co";
	
	@Test
	public void simpleAuthenticationTest() {
		
		TestsUtil.addCitizen("Andres", "Osorio", "Vargas", USER_EMAIL, "12345678");
		
		Authentication auth = new Authentication();
				
		try {
			assertTrue(auth.doAuthentication(USER_EMAIL, "12345678", Constants.CITIZEN_COLLECTION));
		} catch (WrongUserOrPasswordException e) {		
			e.printStackTrace();
		}
		
		Authentication.closeSession(USER_EMAIL);
		
		TestsUtil.removeCitizen(USER_EMAIL);
		
	}

}
