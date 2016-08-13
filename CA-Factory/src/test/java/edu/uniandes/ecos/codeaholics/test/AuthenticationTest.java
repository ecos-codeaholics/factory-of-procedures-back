/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.bson.Document;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.config.Notification;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: AuthenticationTest AuthenticationTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 12, 2016 11:48:02 PM
 * 
 */
public class AuthenticationTest {

	public void addCitizen() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("citizen");
		
		Citizen citizen = new Citizen();
		citizen.setName("Andres");
		citizen.setLastName1("Osorio");
		citizen.setIdentification(1234567890);
		citizen.setEmail("aosorio@uniandes");
		citizen.setPassword("QWERTY");
		citizen.setUserProfile("citizen");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);
		collection.insertOne(citizen.toDocument());

	}

	@Test
	public void simpleAuthenticationTest() {
		
		addCitizen();
		
		Authentication auth = new Authentication();
		
		assertTrue(auth.doAuthentication("aosorio@uniandes", "QWERTY", "citizen"));
		
	}

}
