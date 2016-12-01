/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc.EmailType;

/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: INotifierSvc INotifierSvc.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Servicio de notificacion (email, sms, etc)
 * 
 * Implementation: []
 *
 * Created: Sep 2, 2016 3:38:02 PM
 * 
 */
public interface INotifierSvc {
	
	public void send( EmailType pContext, String pToEmail, ArrayList<String> pParams) throws AddressException, MessagingException;
	
	public void send( EmailType pContext, String pToEmail) throws AddressException, MessagingException;
	
	public void send( EmailType pContext, String pToEmail, ArrayList<String> pParams, String pImage) throws AddressException, MessagingException;

}

