package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;

import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uniandes.ecos.codeaholics.config.AuthenticationJWT;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class AuthenticationJWTTest {

	Logger logger = LogManager.getRootLogger();
	public String citizenSalt;
	public String token;
	
	public void addCitizen(String pName, String pLastName1, String pEmail, String pPwd) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("citizen");
				
		Citizen citizen = new Citizen();
		citizen.setName(pName);
		citizen.setLastName1(pLastName1);
		citizen.setIdentification(1234567890);
		citizen.setEmail(pEmail);
		citizen.setPassword(pPwd);
		citizen.setUserProfile("citizen");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);
		
		Document user = new Document();
		user.append("email", pEmail);
		ArrayList<Document> documents = DataBaseUtil.find(user, "citizen");
		
		if (documents.isEmpty()) {
			collection.insertOne(citizen.toDocument());
		} else {
			logger.info("user alreadery exists");
			collection.findOneAndDelete(user);
			collection.insertOne(citizen.toDocument());
		}
	
		citizenSalt = hash[0];
		
	}
	
	@Test
	public void tokenCreationTest() {

		addCitizen("Andres", "Osorio", "aosorio@uniandes.edu", "Qwerty");
		
		AuthenticationJWT jwtToken = new AuthenticationJWT();
		boolean authenticated = jwtToken.doAuthentication("aosorio@uniandes.edu", "Qwerty", "citizen");

		if (authenticated) {
			token = jwtToken.getAnswer();
			logger.info(token);

			//Verify and decode
			try {
				
				Claims claims = Jwts.parser().setSigningKey(citizenSalt)
						.parseClaimsJws(token).getBody();

				logger.info("ID: " + claims.getId());
				logger.info("Subject: " + claims.getSubject());
				logger.info("Issuer: " + claims.getIssuer());
				logger.info("Expiration: " + claims.getExpiration());

				assertEquals("aosorio@uniandes.edu", claims.getId());
				
			} catch (Exception e) {
				e.printStackTrace();
				assertFalse(true);
				logger.info("Cannot get token claims");
			}
		} else {
			logger.info("User not authenticated");
			assertFalse(true);
		}

	} 

}
