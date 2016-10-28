/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uniandes.ecos.codeaholics.config.ValidationUtil;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: FieldValidationTest FieldValidationTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Oct 28, 2016 5:57:22 PM
 * 
 */
public class FieldValidationTest {

	private static String goodEmail = "andres@uniandes.edu.co";
	private static String badEmail = "andres@uniandes";
	private static String goodName = "Juan";
	private static String badName = "F3l1p*";
	
	@Test
	public void emailValidationTest() {
		
		assertTrue( ValidationUtil.validateEmail(goodEmail) );
		assertFalse( ValidationUtil.validateEmail(badEmail) );
	}

	@Test
	public void nameValidationTest() {
		
		assertTrue( ValidationUtil.validateLetters(goodName) );
		assertFalse( ValidationUtil.validateLetters(badName) );

		
	}
}
