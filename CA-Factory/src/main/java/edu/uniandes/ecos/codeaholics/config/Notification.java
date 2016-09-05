/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

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

public class Notification {
	
	// Atributos

	private final static Logger log = LogManager.getLogger(DataBaseUtil.class);

	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;
	


	public static void sendEmail (String recipient) throws AddressException, MessagingException {

		log.debug("setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		log.debug("Mail Server Properties have been setup successfully..");

		log.debug("get Mail Session.. ");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		// generateMailMessage.addRecipient(Message.RecipientType.CC, new
		// InternetAddress("david.fms22@gmail.com"));
		generateMailMessage.setSubject("Bienvenido a Fabrica de Tramites");
		String emailBody = "Su registro se ha realizado de forma existosa en nuestro sistema. "
				+ "<br><br> Cordial saludo, <br>Grupo Codeaholics";
		generateMailMessage.setContent(emailBody, "text/html");
		log.debug("Mail Session has been created successfully..");

		log.info("-----------------------------------");
		log.info("Get Session and Send mail");
		log.info("-----------------------------------");
		Transport transport = getMailSession.getTransport("smtp");
		transport.connect("smtp.gmail.com", "codeaholicsfactory@gmail.com", "codeaholics1");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}

}
