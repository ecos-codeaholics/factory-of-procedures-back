package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

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
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Nov 13, 2016 7:16:11 PM
 * 
 */
public class StatisticsTest {

	@Test
	public void basicCounterTest() {

		Request pRequest = null;
		Response pResponse = null;
		
		JsonObject json = (JsonObject) StatisticsServices.getBasicStats(pRequest, pResponse);
		
		System.out.println(json.get("citizen"));
		System.out.println(json.get("mayoralties"));
		System.out.println(json.get("procedures"));
		
		assertTrue(json.has("citizen"));
	
	}

}
