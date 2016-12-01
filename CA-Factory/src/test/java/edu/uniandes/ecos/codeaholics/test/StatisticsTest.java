package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.google.gson.JsonObject;

import edu.uniandes.ecos.codeaholics.business.StatisticsServices;
import spark.Request;
import spark.Response;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: StatisticsTest StatisticsTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Test of the statistics service that displays some numbers on the main page
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Nov 13, 2016 7:16:11 PM
 * 
 */
public class StatisticsTest {

	Logger logger = LogManager.getRootLogger();
	
	@Test
	public void basicCounterTest() {

		Request pRequest = null;
		Response pResponse = null;
		
		JsonObject json = (JsonObject) StatisticsServices.getBasicStats(pRequest, pResponse);
		
		logger.info("Number of citizen: " + json.get("citizen"));
		logger.info("Number of mayoralties: " + json.get("mayoralties"));
		logger.info("Number of procedures: " + json.get("procedures"));
		
		assertTrue(json.has("citizen"));
	
	}

}
