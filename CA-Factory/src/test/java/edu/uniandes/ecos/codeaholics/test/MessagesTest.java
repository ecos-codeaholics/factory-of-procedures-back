/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;

public class MessagesTest {
	
	Logger logger = LogManager.getRootLogger();
	
	private IMessageSvc messageSvc = new ResponseMessage();
	
	@Test
	public void successMessageTest() {
		
		Object response = messageSvc.getOkMessage("No errors");
		Gson gson = new Gson();
		String jsonInString = gson.toJson(response);
		
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonInString).getAsJsonObject();
		
		assertTrue(json.getAsJsonObject().has("successInd"));
		assertTrue(json.getAsJsonObject().has("errorMsg"));
		
	}

	@Test
	public void failedMessageTest() {
		Object response = messageSvc.getNotOkMessage("Errors found");
		Gson gson = new Gson();
		String jsonInString = gson.toJson(response);
		
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonInString).getAsJsonObject();
		
		assertTrue(json.getAsJsonObject().has("successInd"));
		assertTrue(json.getAsJsonObject().has("errorMsg"));
		
		logger.info(jsonInString);

	}


}
