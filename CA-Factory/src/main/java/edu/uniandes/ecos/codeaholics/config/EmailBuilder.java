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
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Nov 10, 2016 10:17:13 AM
 * 
 */
public class EmailBuilder {

	public static final String EMAIL_BREAK = "<br>";

	private String body;
	private String salutation;
	private String subject;
	private String signature;
	private String ending;

	public EmailBuilder(String pConfig) {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(pConfig);
			prop.load(input);

			subject = prop.getProperty("email.subject");
			body = prop.getProperty("email.body");
			salutation = prop.getProperty("email.salutation");
			ending = prop.getProperty("email.ending");
			signature = prop.getProperty("email.signature");

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

		return emailBody.toString();

	}

	public String build(ArrayList<String> pParams) {

		StringBuilder emailBody = new StringBuilder();
		emailBody.append(this.getSalutation());
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);

		Iterator<String> itrParam = pParams.iterator();

		while (itrParam.hasNext()){
			String param = itrParam.next();
			body = this.getBody().replace("######", param);
		}
		
		emailBody.append(this.getBody());
		
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);
		emailBody.append(this.getEnding());
		emailBody.append(EMAIL_BREAK);
		emailBody.append(EMAIL_BREAK);
		emailBody.append(this.getSignature());

		return emailBody.toString();

	}
}
