package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import edu.uniandes.ecos.codeaholics.main.App;
import spark.Spark;

public class LoginTest {

	// private class AuthAnswer {
	// String email;
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	// @Override
	// public String toString() {
	// return "AuthAnswer [email=" + email + "]";
	// }
	//
	// }

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
	public void loginTest() {
		logger.info("Running loging test");

		TestsUtil utilities = new TestsUtil();
		utilities.addCitizen("Jean", "Valjean", "jvaljean@uniandes", "Qwerty");

		int httpResult = 0;
		String httpMessage = "";
		String jsonResponse = "";
		StringBuilder result = new StringBuilder();

		String route = "/citizenLogin";
		String serverPath = TestsUtil.getServerPath();
		
		try {
			URL appUrl = new URL(serverPath + route);

			HttpURLConnection urlConnection = (HttpURLConnection) appUrl.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Content-type", "application/json");
			urlConnection.setRequestMethod("POST");

			String loginData = "{email : \"jvaljean@uniandes\", password : \"Qwerty\" }";

			Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
			writer.write(loginData);
			writer.flush();
			httpResult = urlConnection.getResponseCode();
			httpMessage = urlConnection.getResponseMessage();

			InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
			BufferedReader reader = new BufferedReader(in);

			int line = 0;
			String text = "";
			while ((text = reader.readLine()) != null) {
				jsonResponse += text;
				result.append(text);
				line += 1;
				System.out.println(line);
			}

			reader.close();
			in.close();

			System.out.println(jsonResponse);
			System.out.println(result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * JsonParser parser = new JsonParser(); JsonObject json =
		 * parser.parse(result.toString()).getAsJsonObject();
		 */

		assertEquals(200, httpResult);
		assertEquals("OK", httpMessage);

		// System.out.println(json.getAsJsonObject().has("email"));

	}

}
