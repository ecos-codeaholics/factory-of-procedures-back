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

	ArrayList<EmailType> emailTypes = new ArrayList<EmailType>();

	@Test
	public void emailSenderTest() {

		boolean success = false;

		emailTypes.add(EmailType.REGISTRATION);
		//emailTypes.add(EmailType.RECOVERY);
		//emailTypes.add(EmailType.UPDATE);
		
		emailer.add();
		emailer.build();
		ArrayList<String> pToEmail = new ArrayList<String>();
		pToEmail.add("osorio.af@gmail.com");

		try {
			TestsUtil.isConnected();
			for(Iterator<EmailType> itr = emailTypes.iterator(); itr.hasNext(); ) {
			    EmailType type = itr.next();
			    emailer.send(type, pToEmail);
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

}
