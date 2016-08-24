/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: EmailNotifierSvc EmailNotifierSvc.java
 * 
 * Original Author: @author Jheison Rodriguez
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
	
	public void send( EmailType pContext, String pToEmail ) {
		
		
	}
	
}
