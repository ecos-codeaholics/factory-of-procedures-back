package edu.uniandes.ecos.codeaholics.test;
/** Copyright or License
 *
 */


import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import com.google.gson.JsonObject;

import edu.uniandes.ecos.codeaholics.config.ExternalSvcInvoker;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: ExternalSvcInvokerTest ExternalSvcInvokerTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Nov 12, 2016 7:52:02 AM
 * 
 */
public class ExternalSvcInvokerTest {

	@Test
	public void barcoderTest() {

		String route = "https://warm-beach-98503.herokuapp.com/serialnumbers";
		
		try {
			ExternalSvcInvoker.invoke(route);
			JsonObject json = (JsonObject)ExternalSvcInvoker.getResponse();
			System.out.println(json.get("code"));
			assertTrue(json.has("code"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
