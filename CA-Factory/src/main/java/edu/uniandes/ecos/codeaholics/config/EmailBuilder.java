/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: EmailBuilder EmailBuilder.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Class for building email notifications
 * 
 * Implementation: Email text is obtained from java properties files
 *
 * Created: Nov 10, 2016 10:17:13 AM
 * 
 */
public class EmailBuilder {

	public static final String EMAIL_BREAK = "<br>";

	private static String body;
	private static String salutation;
	private static String subject;
	private static String signature;
	private static String ending;
	private static String habeas;

	public EmailBuilder() {

		subject = "[F\u00E1brica de Tr\u00E1mites] Bienvenido(a) a la F\u00E1brica de Tr\u00E1mites";
		salutation = "Apreciado ciudadano:";
		body = "Su registro se ha realizado de forma exitosa en nuestro sistema. Recuerde que su nombre de usuario es el mismo correo electr\u00F3nico.";
		ending = "Gracias. Cordial saludo,";
		signature = "Servicio de notificaciones<br>F\u00E1brica de Tr\u00E1mites";
		habeas = "<p style=\"font-size:80%;\">Con la nueva ley de Habeas Data, estamos en la obligaci\u00F3n de informarte que est\u00E1s en nuestra base de datos y recibes esta informaci\u00F3n porque has tenido relaci\u00F3n con el Ministerio de las Tecnolog\u00EDas de Informaci\u00F3n y las comunicaciones.</p>";

	}

	public EmailBuilder(String pConfig) {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(pConfig);
			prop.load(input);

			subject = prop.getProperty("email.subject");
			salutation = prop.getProperty("email.salutation");
			body = prop.getProperty("email.body");			
			ending = prop.getProperty("email.ending");
			signature = prop.getProperty("email.signature");
			habeas = prop.getProperty("email.habeas");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getBody() {
		return body;
	}

	public String getSubject() {
		return subject;
	}

	public String getSignature() {
		return signature;
	}

	public String getEnding() {
		return ending;
	}

	public String getSalutation() {
		return salutation;
	}

	public String getHabeas() {
		return habeas;
	}

	public String build() {

		StringBuilder emailBody = new StringBuilder();
		emailBody.append(this.getSalutation());
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);
		emailBody.append(this.getBody());
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);
		emailBody.append(this.getEnding());
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);
		emailBody.append(this.getSignature());
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);
		emailBody.append(this.getHabeas());

		return emailBody.toString();

	}

	public String build(ArrayList<String> pParams) {

		StringBuilder emailBody = new StringBuilder();

		Iterator<String> itrParam = pParams.iterator();

		emailBody.append(this.getSalutation());
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);

		while (itrParam.hasNext()) {
			String param = itrParam.next();
			body = this.getBody().replaceFirst("######", param);
		}

		emailBody.append(this.getBody());

		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);
		emailBody.append(this.getEnding());
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);
		emailBody.append(this.getSignature());
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);
		emailBody.append(this.getHabeas());

		return emailBody.toString();

	}
}
