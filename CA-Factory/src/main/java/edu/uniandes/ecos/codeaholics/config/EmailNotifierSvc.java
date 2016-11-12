/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: EmailNotifierSvc EmailNotifierSvc.java
 * 
 * Original Author: @author Andres Osorio
 * 
 * Description: Notify by email
 * 
 * Implementation: This is the implementation of interface INotifierSvc
 * (replaces old Notification)
 *
 * Created: Aug 24, 2016 3:50:47 PM
 * 
 */
public class EmailNotifierSvc implements INotifierSvc {

	private final static Logger log = LogManager.getLogger(EmailNotifierSvc.class);

	public enum EmailType {
		REGISTRATION, RESET, UPDATE, CHANGE, INITPROCEDURE;
	}
	
	private static final String REGISTRATION_FILE = "src/main/resources/email/registration.properties";
	private static final String RESET_FILE = "src/main/resources/email/reset.properties";
	private static final String UPDATE_FILE = "src/main/resources/email/update.properties";
	private static final String INITIATE_FILE = "src/main/resources/email/initiate.properties";

	private static Map<EmailType,String> context = new HashMap<EmailType,String>();
	
	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;

	public EmailNotifierSvc() {

		log.debug("setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		log.debug("Mail Server Properties have been setup successfully..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
	
		context.put(EmailType.REGISTRATION, REGISTRATION_FILE);
		context.put(EmailType.RESET, RESET_FILE);
		context.put(EmailType.UPDATE, UPDATE_FILE);
		context.put(EmailType.INITPROCEDURE, INITIATE_FILE);
		
	}

	/**
	 * 
	 * 
	 * @param pContext
	 * @param pToEmail
	 *            ArrayList, the first parameter is the email which are going to
	 *            recive the msj, In the case of recovery the second parameter
	 *            is the new password
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void send(EmailType pContext, String pToEmail) throws AddressException, MessagingException {

		if (pContext.equals(EmailType.REGISTRATION)) {
			sendRegister(pToEmail);
		}
	}

	/**
	 * 
	 * 
	 * @param pContext
	 * @param pToEmail
	 *            ArrayList, the first parameter is the email which are going to
	 *            recive the msj, In the case of recovery the second parameter
	 *            is the new password
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void send(EmailType pContext, String pToEmail, ArrayList<String> pParams)
			throws AddressException, MessagingException {
	
		sendWithParams(pContext, pToEmail, pParams);		
	}

	/**
	 * Envia el cor
	 * 
	 * @param pToEmail
	 *            is destinatary's mail address
	 * @throws AddressException
	 * @throws MessagingException
	 */
	private void sendRegister(String pToEmail) throws AddressException, MessagingException {
		
		EmailBuilder email = new EmailBuilder(REGISTRATION_FILE);			
		sendEmail(pToEmail, email.getSubject(), email.build());
	}

	private void sendWithParams(EmailType pContext, String pToEmail, ArrayList<String> pParams) throws AddressException, MessagingException {
		
		EmailBuilder email = new EmailBuilder(context.get(pContext));			
		sendEmail(pToEmail, email.getSubject(), email.build(pParams));
	}

	private void sendEmail(String pToEmail, String pSubject, String pEmailBody)
			throws AddressException, MessagingException {
		
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(pToEmail));
		generateMailMessage.setSubject(pSubject);
		
		String emailBody = pEmailBody;
		generateMailMessage.setContent(emailBody, "text/html");
		
		log.debug("Mail Session has been created successfully..");
		log.info("-----------------------------------");
		log.info("Get Session and Send mail to recover the password");
		log.info("-----------------------------------");
		
		Transport transport = getMailSession.getTransport("smtp");
		transport.connect("smtp.gmail.com", "codeaholicsfactory@gmail.com", "codeaholics1");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();	
	}

}

