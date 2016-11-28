/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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

	private static EmailNotifierSvc instance = null;

	public enum EmailType {
		REGISTRATION, RESET, UPDATE, CHANGE, INITPROCEDURE, MAKE_FUNCTIONARY;
	}

	private static final String REGISTRATION_FILE = "src/main/resources/email/registration.properties";
	private static final String RESET_FILE = "src/main/resources/email/reset.properties";
	private static final String UPDATE_FILE = "src/main/resources/email/update.properties";
	private static final String INITIATE_FILE = "src/main/resources/email/initiate.properties";
	private static final String FUNCTIONARY_ROL_FILE = "src/main/resources/email/functionary.properties";

	private static Map<EmailType, String> context = new HashMap<EmailType, String>();

	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;

	private EmailNotifierSvc() {

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
		context.put(EmailType.MAKE_FUNCTIONARY, FUNCTIONARY_ROL_FILE);

	}

	/**
	 * EmailNotifierSvc follows a Singleton pattern
	 * 
	 * @return
	 */
	public static EmailNotifierSvc getInstance() {
		if (instance == null) {
			instance = new EmailNotifierSvc();
		}
		return instance;
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
	@Override
	public void send(EmailType pContext, String pToEmail) throws AddressException, MessagingException {

		// if (pContext.equals(EmailType.REGISTRATION)) {
		sendRegister(pToEmail);
		// } else if (pContext.equals(EmailType.MAKE_FUNCTIONARY)) {
		//
		// EmailBuilder email = new EmailBuilder(context.get(pContext));
		// sendEmail(pToEmail, email.getSubject(), email.build());
		// }
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
	@Override
	public void send(EmailType pContext, String pToEmail, ArrayList<String> pParams)
			throws AddressException, MessagingException {

		sendWithParams(pContext, pToEmail, pParams);

	}

	/**
	 * @param pContext
	 * @param pToEmail
	 * @param pParams
	 * @param pImage
	 * @throws AddressException
	 * @throws MessagingException
	 */
	@Override
	public void send(EmailType pContext, String pToEmail, ArrayList<String> pParams, String pImage)
			throws AddressException, MessagingException {

		EmailBuilder email = new EmailBuilder(context.get(pContext));
		sendEmailWithImage(pToEmail, email.getSubject(), email.build(pParams), pImage);

	}

	/**
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

	/**
	 * @param pContext
	 * @param pToEmail
	 * @param pParams
	 * @throws AddressException
	 * @throws MessagingException
	 */
	private void sendWithParams(EmailType pContext, String pToEmail, ArrayList<String> pParams)
			throws AddressException, MessagingException {

		EmailBuilder email = new EmailBuilder(context.get(pContext));
		sendEmail(pToEmail, email.getSubject(), email.build(pParams));

	}

	/**
	 * @param pToEmail
	 * @param pSubject
	 * @param pEmailBody
	 * @throws AddressException
	 * @throws MessagingException
	 */
	private void sendEmail(String pToEmail, String pSubject, String pEmailBody)
			throws AddressException, MessagingException {

		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(pToEmail));
		generateMailMessage.setSubject(pSubject);

		log.debug("Mail Session has been created successfully..");

		String emailBody = pEmailBody;

		generateMailMessage.setContent(emailBody, "text/html; charset=UTF-8");
		// generateMailMessage.setContent(emailBody, "text/html");

		log.info("Sending email message");

		Transport transport = getMailSession.getTransport("smtp");
		transport.connect("smtp.gmail.com", "codeaholicsfactory@gmail.com", "codeaholics1");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();

	}

	/**
	 * Send email with an attached image: logo, procedure bar, anything you need
	 * 
	 * @param pToEmail
	 * @param pSubject
	 * @param pEmailBody
	 * @param pImage
	 * @throws AddressException
	 * @throws MessagingException
	 */
	private void sendEmailWithImage(String pToEmail, String pSubject, String pEmailBody, String pImage)
			throws AddressException, MessagingException {

		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(pToEmail));
		generateMailMessage.setSubject(pSubject);

		log.debug("Mail Session has been created successfully..");

		String emailBody = pEmailBody;

		MimeMultipart multipart = new MimeMultipart("related");

		// .
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(emailBody, "text/html; charset=UTF-8");
		multipart.addBodyPart(messageBodyPart);

		// ..
		messageBodyPart = new MimeBodyPart();
		DataSource fds = new FileDataSource(pImage);
		messageBodyPart.setDataHandler(new DataHandler(fds));
		messageBodyPart.setHeader("Content-ID", "<image>");
		multipart.addBodyPart(messageBodyPart);

		// .. send as attachment
		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(pImage);
		messageBodyPart.setDataHandler(new DataHandler(source));
		String fileName = pImage.substring(pImage.lastIndexOf('/') + 1);
		messageBodyPart.setFileName(fileName);
		multipart.addBodyPart(messageBodyPart);

		// ...
		generateMailMessage.setContent(multipart);

		log.info("Sending email message");

		Transport transport = getMailSession.getTransport("smtp");
		transport.connect("smtp.gmail.com", "codeaholicsfactory@gmail.com", "codeaholics1");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();

	}

}
