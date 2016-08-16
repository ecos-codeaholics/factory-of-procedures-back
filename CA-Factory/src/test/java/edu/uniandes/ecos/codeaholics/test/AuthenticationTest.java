/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uniandes.ecos.codeaholics.config.Authentication;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: AuthenticationTest AuthenticationTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 12, 2016 11:48:02 PM
 * 
 */
public class AuthenticationTest {

	@Test
	public void simpleAuthenticationTest() {
		
		TestsUtil utilities = new TestsUtil();
		utilities.addCitizen("Andres", "Osorio", "aosorio@uniandes", "QWERTY");
		
		Authentication auth = new Authentication();
		
		assertTrue(auth.doAuthentication("aosorio@uniandes", "QWERTY", "citizen"));
		
	}

}
