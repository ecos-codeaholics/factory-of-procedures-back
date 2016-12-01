/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;
/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: Constants Constants.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: The purpose of this class is to maintain all constants parameters in one place
 * 
 * Implementation: use final predicate
 *
 * Created: Oct 28, 2016 9:23:03 AM
 * 
 */
public class Constants {
	
	public static final String SESSION_COLLECTION = "session";
	
	public static final String CITIZEN_COLLECTION = "citizen";
	
	public static final String FUNCTIONARY_COLLECTION = "functionary";

	public static final String PROCEDUREREQUEST_COLLECTION = "procedureRequest";

	public static final String PROCEDURE_COLLECTION = "procedure";
	
	public static final String MAYORALTY_COLLECTION = "mayoralty";
	
	public static final String CITIZEN_USER_PROFILE = "citizen";
	
	public static final String FUNCTIONARY_USER_PROFILE = "functionary";
	
	public static final String ADMIN_USER_PROFILE = "admin";
	
	public static final String STATUS_PENDING = "Pendiente";
		
	public static final long TOKEN_LIFETIME = 1000 * 1800; // 30 min

	public static final String TOKEN_ISSUER = "http://codeaholics.dynns.com";

	public static final String TOKEN_SUBJECT = "A subject";
	
	public static final String TOKEN_EMAIL_KEY = "jti";

	public static final String TOKEN_SUBJECT_KEY = "sub";

}
