/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException;
/**
 * Package: edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException
 *
 * Class: ExpiredTokenException ExpiredTokenException.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Oct 25, 2016 10:01:50 AM
 * 
 */
public class ExpiredTokenException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658325432136145939L;

	private String errorCode = "Token ha expirado. Necesita renovar";

	/**
	 * 
	 * 
	 * @param message
	 * @param errorCode
	 */
	public ExpiredTokenException(String message, String errorCode){
		super(message);
		this.errorCode=errorCode;
	}

	/**
	 * 
	 * @return
	 */
	public String getErrorCode() {
		return this.errorCode;
	}


}
