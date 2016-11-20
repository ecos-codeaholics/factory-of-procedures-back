/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;

/**
 * Package: codeAholics
 *
 * Class: IAuthenticationSvc IAuthenticationSvc.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Authentication interface
 * 
 * Implementation: Implemented using JWT and plain authentication (hand made)
 *
 * Created: Aug 2, 2016 1:40:28 PM
 * 
 */
public interface IAuthenticationSvc {
	/** doAuthentication: perform any type of authentication
	 * @param p1: parameter 1 (email)
	 * @param p2: parameter 2 (passwd)
	 * @param p3: parameter 3 (profile)
	 * @return true,false
	 * @throws WrongUserOrPasswordException 
	 */
	public boolean doAuthentication(String p1, String p2, String p3) throws WrongUserOrPasswordException;
	
	/** get an answer from authentication
	 * @return a String (could be a json token, an OK message, etc)
	 */
	public Object getAnswer();
}
