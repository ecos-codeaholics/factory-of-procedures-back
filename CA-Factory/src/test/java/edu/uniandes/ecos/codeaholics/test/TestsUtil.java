/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.main.App;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: TestsUtil TestsUtil.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Utilities for running tests
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 14, 2016 5:39:16 PM
 * 
 */
public class TestsUtil {
	
	Logger logger = LogManager.getRootLogger();
	
	private String citizenSalt;
	
	/**
	 * @return the citizenSalt
	 */
	public String getCitizenSalt() {
		return citizenSalt;
	}

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
	
	public static String cleanJsonString( String pStr ) {
		String cleanStr;
		int pos1 = pStr.indexOf('"');
		int pos2 = pStr.lastIndexOf('"');
		System.out.println(pos1 + " " + pos2 + " " + pStr.length());
		if( pos1 == 0 && pos2 == (pStr.length()-1)) {
			cleanStr = pStr.substring(1, pos2);
		} else
			cleanStr = pStr;
		return cleanStr;
	}

	public static String getServerPath() {
		
		int port = App.JETTY_SERVER_PORT;
		String server = "http://localhost";

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(server);
		strBuilder.append(":");
		strBuilder.append(port);
		String serverPath = strBuilder.toString();
		
		return serverPath;
	}
}
