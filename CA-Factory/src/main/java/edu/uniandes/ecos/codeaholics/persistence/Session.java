/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Package: edu.uniandes.ecos.codeaholics.persistence
 *
 * Class: Session Session.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Session object class - type of session is with JWT (token)
 * 
 * Implementation: Currently only used as an auxiliary class, in principle one could also implement
 *   Session <- SessionJWT
 *
 * Created: Oct 25, 2016 11:31:55 AM
 * 
 */
public class Session {
	
	public static final String EMAIL = "email";
	public static final String PROFILE = "user-profile";
	public static final String TOKEN = "token";
	public static final String SALT = "salt";
	
	@SerializedName("_id")
	private String _id;
	String email;
	String token;
	String salt;
	String userProfile;
	
	/**
	 * 
	 */
	public Session() {
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
	}
	
	/**
	 * @param salt the salt to set
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	/**
	 * @return the userProfile
	 */
	public String getUserProfile() {
		return userProfile;
	}
	
	/**
	 * @param userProfile the userProfile to set
	 */
	public void setUserProfile(String userProfile) {
		this.userProfile = userProfile;
	}
	
	public Document toDocument() {
		
		Document session = new Document();
		session.append(EMAIL, this.getEmail());
		session.append(PROFILE, this.getUserProfile());
		session.append(TOKEN, this.getToken());
		session.append(SALT, this.getSalt());
		
		return session;
	}
	
	

}
