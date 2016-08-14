/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: AuthenticationJWT AuthenticationJWT.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 12, 2016 6:36:05 PM
 * 
 */
public class AuthenticationJWT implements IAuthenticationSvc {

	// Atributos
	private final static Logger log = LogManager.getLogger(AuthenticationJWT.class);

	private String token;

	public static final long TOKEN_LIFETIME = 1000 * 600; // 10 min
	public static final String TOKEN_ISSUER = "codeaholics";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uniandes.ecos.codeaholics.config.IAuthenticationSvc#doAuthentication(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean doAuthentication(String pEmail, String pPwd, String pProfile) {

		boolean authenticated = false;
		log.info("Verifying user data...");
		Document user = new Document();
		user.append("email", pEmail);

		ArrayList<Document> documents = DataBaseUtil.find(user, pProfile);

		if (documents.isEmpty()) {
			log.info("User Doesn't Exist");
		} else {
			user.append("userProfile", pProfile);
			ArrayList<Document> documents2 = DataBaseUtil.find(user, pProfile);
			if (documents2.isEmpty()) {
				log.info("User Profile Wrong");
			} else {

				String salt = documents.get(0).get("salt").toString();
				String[] hash = GeneralUtil.getHash(pPwd, salt);

				user.append("password", hash[1]);

				ArrayList<Document> results = DataBaseUtil.find(user, pProfile);

				if (results.size() > 0) {

					log.info(pEmail + " authenticated! create token.");
					token = createJWT(pEmail, TOKEN_ISSUER, pProfile, salt, "Something");
					authenticated = true;

				} else {
					log.info("Wrong password");
					token = "{}";
					authenticated = false;
				}
			}
		}

		return authenticated;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uniandes.ecos.codeaholics.config.IAuthenticationSvc#getAnswer()
	 */
	@Override
	public String getAnswer() {
		return token;
	}

	/**
	 * @param id
	 * @param issuer
	 * @param subject
	 * @param ttlMillis
	 * @return
	 */
	private String createJWT(String pId, String pIssuer, String pProfile, String pSalt, String pSubject) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		long expMillis = nowMillis + TOKEN_LIFETIME;
		Date exp = new Date(expMillis);
		
		log.debug(pSalt);
		
		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(pId);
		builder.setIssuedAt(now);
		builder.setSubject(pSubject);
		builder.setIssuer(pIssuer);
		builder.signWith(signatureAlgorithm, pSalt);
		builder.setExpiration(exp);
		builder.setAudience(pProfile);

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();

	}

}
