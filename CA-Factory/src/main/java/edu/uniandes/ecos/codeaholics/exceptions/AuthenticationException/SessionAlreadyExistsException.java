/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException;
/**
 * Package: edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException
 *
 * Class: SessionAlreadyExistsException SessionAlreadyExistsException.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Oct 28, 2016 8:23:33 AM
 * 
 */
public class SessionAlreadyExistsException extends Exception {
	
	
	/**
	 * version number for serialization
	 */
	private static final long serialVersionUID = -5988451809797528194L;

	private String errorCode="Nombre de usuario o clave erronea";
	
	/**
	 * Assign message and error code to the exception 
	 * 
	 * @param message This message will be save in the exception
	 * @param errorCode This error cdoe will be save in the exception
	 */
	public SessionAlreadyExistsException(String message, String errorCode){
		super(message);
		this.errorCode=errorCode;
	}
	
	/**
	 * 
	 * @return The error code define
	 */
	public String getErrorCode(){
		return this.errorCode;
	}
	

}
