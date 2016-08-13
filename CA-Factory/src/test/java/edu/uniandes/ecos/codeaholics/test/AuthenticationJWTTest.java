package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import org.bson.Document;
import org.junit.Test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uniandes.ecos.codeaholics.config.AuthenticationJWT;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class AuthenticationJWTTest {

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
		collection.insertOne(citizen.toDocument());
		
		citizenSalt = hash[0];
		
	}
	
	@Test
	public void tokenCreationTest() {

		addCitizen("Andres", "Osorio", "aosorio@uniandes", "Qwerty");
		
		AuthenticationJWT jwtToken = new AuthenticationJWT();
		boolean authenticated = jwtToken.doAuthentication("aosorio@uniandes", "Qwerty", "citizen");

		if (authenticated) {
			token = jwtToken.getAnswer();
			System.out.println(token);

			//Verify and decode
			try {
				
				Claims claims = Jwts.parser().setSigningKey(citizenSalt)
						.parseClaimsJws(token).getBody();

				System.out.println("ID: " + claims.getId());
				System.out.println("Subject: " + claims.getSubject());
				System.out.println("Issuer: " + claims.getIssuer());
				System.out.println("Expiration: " + claims.getExpiration());

				assertEquals("aosorio@uniandes", claims.getId());
				
			} catch (Exception e) {
				e.printStackTrace();
				assertFalse(true);
				System.out.println("Cannot get token claims");
			}
		} else {
			System.out.println("User not authenticated");
			assertFalse(true);
		}

	} 

}
