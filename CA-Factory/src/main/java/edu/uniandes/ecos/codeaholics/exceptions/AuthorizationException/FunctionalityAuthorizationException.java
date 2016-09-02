/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException;

/**
 * Package: edu.uniandes.ecos.codeaholics.exceptions.AuthorizationException
 *
 * Class: FunctionalityAuthorizationException
 * FunctionalityAuthorizationException.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 8, 2016 3:21:14 PM
 * 
 */
public class FunctionalityAuthorizationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3658325432136145937L;

	private String errorCode = "Authorization error";

	/**
	 * 
	 * 
	 * @param message
	 * @param errorCode
	 */
	public FunctionalityAuthorizationException(String message, String errorCode){
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
