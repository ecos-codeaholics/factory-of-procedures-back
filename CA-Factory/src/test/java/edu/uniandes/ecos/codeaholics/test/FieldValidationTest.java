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
	private static String goodNumber = "1234567890";
	private static String badNumber = "1234R6%";
	
	@Test
	public void emailValidationTest() {

		assertTrue(ValidationUtil.validate.apply(goodEmail, ValidationUtil.PATTERN_EMAIL));
		assertFalse(ValidationUtil.validate.apply(badEmail, ValidationUtil.PATTERN_EMAIL));
	}

	@Test
	public void nameValidationTest() {

		assertTrue(ValidationUtil.validate.apply(goodName, ValidationUtil.PATTERN_NAME));
		assertFalse(ValidationUtil.validate.apply(badName, ValidationUtil.PATTERN_NAME));

	}
	
	@Test
	public void numberValidationTest() {

		assertTrue(ValidationUtil.validate.apply(goodNumber, ValidationUtil.PATTERN_NUMBER));
		assertFalse(ValidationUtil.validate.apply(badNumber, ValidationUtil.PATTERN_NUMBER));

	}
}
