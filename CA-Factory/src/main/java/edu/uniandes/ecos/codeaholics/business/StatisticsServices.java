/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.business;

import org.bson.Document;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoDatabase;

import edu.uniandes.ecos.codeaholics.config.Constants;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import spark.Request;
import spark.Response;

/**
 * Package: edu.uniandes.ecos.codeaholics.business
 *
 * Class: StatisticsServices StatisticsServices.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Run some statatics on the database
 * 
 * Implementation: Do some counting on the database with queries
 *
 * Created: Nov 12, 2016 11:54:18 AM
 * 
 */
public class StatisticsServices {

	private static MongoDatabase db = DatabaseSingleton.getInstance().getDatabase();
	
	public static Object getBasicStats(Request pRequest, Response pResponse) {

		JsonObject json = new JsonObject();
		
		// 1. Number of citizen registered
		Document filter = new Document();
		filter.append("userProfile", "citizen");

		long ncitizen = db.getCollection(Constants.CITIZEN_COLLECTION).count(filter);
			
		// 2. Number of mayor
		long nmayoralties = db.getCollection(Constants.MAYORALTY_COLLECTION).count();

		// 3. Number of procedures

		long nprocedures = db.getCollection(Constants.PROCEDURE_COLLECTION).count();

		json.addProperty("citizen", ncitizen);
		json.addProperty("mayoralties",nmayoralties);
		json.addProperty("procedures", nprocedures);
		
		return json;
		
	}

}
