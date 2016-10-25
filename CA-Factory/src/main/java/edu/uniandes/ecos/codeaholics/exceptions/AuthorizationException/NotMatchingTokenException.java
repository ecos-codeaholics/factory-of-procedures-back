/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException;
/**
 * Package: edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException
 *
 * Class: NotMatchingTokenException NotMatchingTokenException.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Oct 25, 2016 9:52:24 AM
 * 
 */
public class NotMatchingTokenException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3658325432136145938L;

	private String errorCode = "Token enviado no concuerda";

	/**
	 * 
	 * 
	 * @param message
	 * @param errorCode
	 */
	public NotMatchingTokenException(String message, String errorCode){
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
