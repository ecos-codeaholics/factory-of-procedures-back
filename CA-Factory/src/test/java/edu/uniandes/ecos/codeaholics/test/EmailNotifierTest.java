/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.junit.Test;

import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc.EmailType;
import edu.uniandes.ecos.codeaholics.config.INotifierSvc;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: EmailNotifierTest EmailNotifierTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Tests the email notifier service in all its functionalities
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Sep 2, 2016 5:14:04 PM
 * 
 */
public class EmailNotifierTest {

	private static INotifierSvc emailer = new EmailNotifierSvc();

	private static ArrayList<EmailType> emailTypes = new ArrayList<EmailType>();

	@Test
	public void emailRegistrationTest() {

		boolean success = false;

		try {
			TestsUtil.isConnected();
			EmailType type = EmailType.REGISTRATION;
			emailer.send(type, "osorio.af@gmail.com");
			success = true;

		} catch (AddressException e) {
			success = false;

		} catch (MessagingException e) {
			success = false;

		} catch (IOException e) {
			e.printStackTrace();
			success = true; // There is no connection
		}

		assertTrue(success);

	}

	@Test
	public void emailResetTest() {

		boolean success = false;

		emailTypes.add(EmailType.RESET);
		emailTypes.add(EmailType.UPDATE);

		ArrayList<String> params = new ArrayList<String>();
		params.add("NEWPASSWORD");

		try {
			TestsUtil.isConnected();
			for (Iterator<EmailType> itr = emailTypes.iterator(); itr.hasNext();) {
				EmailType type = itr.next();
				emailer.send(type, "osorio.af@gmail.com", params);
				success = true;
			}
		} catch (AddressException e) {
			success = false;

		} catch (MessagingException e) {
			success = false;

		} catch (IOException e) {
			e.printStackTrace();
			success = true; // There is no connection
		}

		assertTrue(success);

	}

	@Test
	public void emailInitiateProcedureTest() {

		boolean success = false;

		ArrayList<String> params = new ArrayList<String>();
		params.add("Prueba de creaci\u00F3n de tr\u00E1mite");
		params.add("12345678790");

		try {
			TestsUtil.isConnected();
			emailer.send(EmailType.INITPROCEDURE, "aosorio@uniandes.edu.co", params);
			success = true;
			
		} catch (

		AddressException e) {
			success = false;

		} catch (MessagingException e) {
			success = false;

		} catch (IOException e) {
			e.printStackTrace();
			success = true; // There is no connection
		}

		assertTrue(success);

	}

}
