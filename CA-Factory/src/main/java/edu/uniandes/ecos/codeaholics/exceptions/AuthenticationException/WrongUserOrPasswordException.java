/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException;
/**
 * Package: edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException
 *
 * Class: WrongUserOrPasswordException WrongUserOrPasswordException.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 8, 2016 3:14:19 PM
 * 
 */
public class WrongUserOrPasswordException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5988451809797528194L;
	
	private String errorCode="Wrong user name or Password";
	
	public WrongUserOrPasswordException(String message, String errorCode){
		super(message);
		this.errorCode=errorCode;
	}
	
	public String getErrorCode(){
		return this.errorCode;
	}
	
	
}
