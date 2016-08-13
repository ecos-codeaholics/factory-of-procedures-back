/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;
/**
 * Package: codeAholics
 *
 * Class: IAuthenticationSvc IAuthenticationSvc.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
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
	 */
	public boolean doAuthentication(String p1, String p2, String p3);
	
	/** get an answer from authentication
	 * @return a String (could be a json token, an OK message
	 */
	public String getAnswer();
}
