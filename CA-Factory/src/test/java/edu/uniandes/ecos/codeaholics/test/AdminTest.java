/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import edu.uniandes.ecos.codeaholics.config.AuthenticationJWT;
import edu.uniandes.ecos.codeaholics.config.Constants;
import edu.uniandes.ecos.codeaholics.config.Routes;
import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;
import edu.uniandes.ecos.codeaholics.main.App;
import spark.Spark;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: AdminTest AdminTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Tests for administrator user profile
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Nov 23, 2016 8:57:21 AM
 * 
 */
public class AdminTest {

	Logger logger = LogManager.getLogger(AdminTest.class);

	private final String USER_NAME = "David";
	private final String USER_LASTNAME = "Bernal";
	private final String USER_LASTNAME2 = "Sanz";
	private final String USER_EMAIL = "xxxyyy@uniandes.edu.co";
	private final String USER_PWD = "12345678";

	private final String USER_TO_UPDATE_NAME = "Mauricio";
	private final String USER_TO_UPDATE_LASTNAME = "Benavides";
	private final String USER_TO_UPDATE_LASTNAME2 = "Cardenas";
	private final String USER_TO_UPDATE_EMAIL = "osorio.af@gmail.com";
	private final String USER_TO_UPDATE_PWD = "12345678";
	private final String USER_TO_UPDATE_DEPENDENCY = "Hacienda";

	@BeforeClass
	public static void beforeClass() {
		App.main(null);
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}

	private void initiateSession() throws WrongUserOrPasswordException {

		TestsUtil.addCitizen(USER_TO_UPDATE_NAME, USER_TO_UPDATE_LASTNAME, USER_TO_UPDATE_LASTNAME2,
				USER_TO_UPDATE_EMAIL, USER_TO_UPDATE_PWD);
		TestsUtil.addFunctionary(USER_NAME, USER_LASTNAME, USER_LASTNAME2, USER_EMAIL, USER_PWD,
				Constants.ADMIN_USER_PROFILE);

		AuthenticationJWT jwtToken = new AuthenticationJWT();

		try {
			jwtToken.doAuthentication(USER_EMAIL, USER_PWD, Constants.FUNCTIONARY_COLLECTION);
		} catch (WrongUserOrPasswordException e1) {
			e1.printStackTrace();
			throw e1;
		}

	}

	@Test
	public void functionaryCreationTest() {

		// Create test user & add session
		try {
			initiateSession();
		} catch (WrongUserOrPasswordException e1) {
			e1.printStackTrace();
		}

		int httpResult = 0;
		String httpMessage = "";
		String jsonResponse = "";
		StringBuilder result = new StringBuilder();

		String route = Routes.ADMIN;
		String serverPath = TestsUtil.getServerPath();

		try {
			URL appUrl = new URL(serverPath + route);
			
			// TODO ... study and understand why this fixes the Connection
			// refused error
			System.out.println("===== 0.");
			InputStream response = new URL("http://stackoverflow.com").openStream();
			response.close();
			System.out.println("===== 0. =====");

			HttpURLConnection urlConnection = (HttpURLConnection) appUrl.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Content-type", "application/json");
			urlConnection.setRequestMethod("POST");

			String loginData = "{email : \"" + USER_TO_UPDATE_EMAIL + "\"}";
			loginData += "{ dependency : \"" + USER_TO_UPDATE_DEPENDENCY + "\" }";

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

			logger.info(jsonResponse);

			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(result.toString()).getAsJsonObject();

			assertEquals(200, httpResult);
			assertEquals("OK", httpMessage);
			assertTrue(json.getAsJsonObject().has("responseMsg"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		TestsUtil.removeFunctionary(USER_EMAIL);
		TestsUtil.removeCitizen(USER_TO_UPDATE_EMAIL);
		TestsUtil.removeFunctionary(USER_TO_UPDATE_EMAIL);

		// Remove session
		AuthenticationJWT.closeSession(USER_EMAIL);

	}

}
