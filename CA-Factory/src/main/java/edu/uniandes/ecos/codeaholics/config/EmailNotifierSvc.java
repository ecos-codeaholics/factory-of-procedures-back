/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.util.ArrayList;
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
 * Implementation: This is the implementation of interface INotifierSvc (replaces old Notification)
 *
 * Created: Aug 24, 2016 3:50:47 PM
 * 
 */

	
public class EmailNotifierSvc {

	private final static Logger log = LogManager.getLogger(EmailNotifierSvc.class);
	
	public enum EmailType {
		
		REGISTRATION, RECOVERY, UPDATE;
		
	}
	
	public class Email {
		
		String body;
		String subject;
		String signature;
		
	}
		
	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;
	
	private static String EMAIL_REGISTRATION_CONF = "src/main/resources/email/registration.properties";
	private static String EMAIL_RECOVERY_CONF = "src/main/resources/email/recovery.properties";
	private static String EMAIL_UPDATE_CONF = "src/main/resources/email/update.properties";
	
	public EmailNotifierSvc() {
		
		log.debug("setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		log.debug("Mail Server Properties have been setup successfully..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		
	}
	
	public void add() {
			
	}
	
	public void build() {
		
	}
	
	/**
	 * 
	 * @param pContext
	 * @param pToEmail ArrayList, the first parameter is the email which are going to recive the msj, In the case of recovery the second parameter is the new password
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void send( EmailType pContext, ArrayList<String> pToEmail ) throws AddressException, MessagingException  {
		if (pContext == EmailType.RECOVERY){
				generateMailMessage = new MimeMessage(getMailSession);
				generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(pToEmail.get(0)));
				generateMailMessage.setSubject("Bienvenido a Fabrica de Tramites");
				String emailBody = "Su password a sido restaurado de forma existosa en nuestro sistema. "
						+ "<br>Su nuevo password es: "+pToEmail.get(1)
						+ "<br><br> Cordial saludo, <br>Grupo Codeaholics";
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
	

	
}
