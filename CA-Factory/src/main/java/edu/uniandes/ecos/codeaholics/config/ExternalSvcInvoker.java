/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: ExternalSvcInvoker ExternalSvcInvoker.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Nov 12, 2016 7:37:41 AM
 * 
 */
public class ExternalSvcInvoker {

	private static Object response;

	public static void invoke(String pRoute) {
		
		int httpResult = 0;
		String httpMessage = "";
		String jsonResponse = "";
		StringBuilder result = new StringBuilder();

		try {

			URL appUrl = new URL(pRoute);

			HttpURLConnection urlConnection = (HttpURLConnection) appUrl.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Content-type", "application/json");
			urlConnection.setRequestMethod("GET");

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
			urlConnection.disconnect();

			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(result.toString()).getAsJsonObject();

			response = json;
			
			System.out.println(httpResult);
				
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Object getResponse() {
		return response;
	}

}
