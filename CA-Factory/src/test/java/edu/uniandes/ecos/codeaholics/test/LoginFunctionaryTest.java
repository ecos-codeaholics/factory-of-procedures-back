package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.uniandes.ecos.codeaholics.config.Routes;
import edu.uniandes.ecos.codeaholics.main.App;
import spark.Spark;

public class LoginFunctionaryTest {

	Logger logger = LogManager.getRootLogger();

	@BeforeClass
	public static void beforeClass() {
		App.main(null);
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}
	
	@Test
	public void loginFunctionaryTest() {
		
		logger.info("Running loging test");

		
		TestsUtil.addFunctionaryUno("Juan", "Valdez", "jvaldez@anapoima", "12345678");
		TestsUtil.addFunctionaryDos("Arturo", "Calle", "acalle@anapoima", "12345678");
		TestsUtil.addFunctionaryTres("Juan", "Valdez", "jvaldez@elrosal", "12345678");
		TestsUtil.addFunctionaryCuatro("Arturo", "Calle", "acalle@elrosal", "12345678");

		int httpResult = 0;
		String httpMessage = "";
		String jsonResponse = "";
		StringBuilder result = new StringBuilder();

		String route = Routes.AUTH + "login/";
		String serverPath = TestsUtil.getServerPath();

		try {
			URL appUrl = new URL(serverPath + route);

			//TODO ... study and understand why this fixes the Connection refused error
			System.out.println("===== 0. ");
            InputStream response = new URL("http://stackoverflow.com").openStream();
            response.close();
            System.out.println("===== 0. =====");
			
			HttpURLConnection urlConnection = (HttpURLConnection) appUrl.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Content-type", "application/json");
			urlConnection.setRequestMethod("POST");

			String loginData = "{email : \"jvaldez@elrosal\", password : \"12345678\" , userProfile : \"functionary\"}";

			Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
			writer.write(loginData);
			writer.flush();
			httpResult = urlConnection.getResponseCode();
			httpMessage = urlConnection.getResponseMessage();

			InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
			BufferedReader reader = new BufferedReader(in);

			String text = "";
			while ((text = reader.readLine()) != null) {
				jsonResponse += text;
				result.append(text);
			}

			reader.close();
			in.close();
			//urlConnection.disconnect();
			
			logger.info(jsonResponse);
			logger.info(result.toString());

			//TODO: move this outside of the try catch once the Connection error is understood
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(result.toString()).getAsJsonObject();

			assertEquals(200, httpResult);
			assertEquals("OK", httpMessage);
			assertTrue(json.getAsJsonObject().has("responseMsg"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
