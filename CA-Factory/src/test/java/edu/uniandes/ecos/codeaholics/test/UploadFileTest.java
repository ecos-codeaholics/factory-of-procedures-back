/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uniandes.ecos.codeaholics.main.App;
import spark.Spark;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: UploadFileTest UploadFileTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 20, 2016 9:19:21 AM
 * 
 */
public class UploadFileTest {

	Logger logger = LogManager.getRootLogger();
	String filePath = "";

	@BeforeClass
	public static void beforeClass() {
		App.main(null);
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}

	@Test
	public void uploadTest() {

		logger.info("Running upload test");

		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		createTestFile(timeLog);

		int httpResult = 0;
		String httpMessage = "";
		String jsonResponse = "";
		StringBuilder result = new StringBuilder();

		String route = "/citezen/documents/upload";
		String serverPath = TestsUtil.getServerPath();
		
		String attachmentName = "testFile";
		String attachmentFileName = timeLog;

		String crlf = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

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
			urlConnection.setRequestMethod("POST");
			
			// 1...
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Cache-Control", "no-cache");
			urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			// 2...
			DataOutputStream request = new DataOutputStream(urlConnection.getOutputStream());

			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\""
					+ attachmentFileName + "\"" + crlf);
			request.writeBytes(crlf);
			
			// 2.1...
			// TODO use the actual file / Challenge: send a png file
			String s = "Hello World!";
			byte data[] = s.getBytes();
			request.write(data);

			// 2.2...
			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
			request.flush();
			request.close();

			// 3...
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
			//urlConnection.disconnect();
			
			System.out.println(jsonResponse);
			System.out.println(result.toString());
		
			//TODO: move this outside of the try catch once the Connection error is understood
			assertEquals(200, httpResult);
			assertEquals("OK", httpMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}

		TestsUtil.removeTestFile(timeLog);
		
	}

	/**
	 * create a temporary file for this test
	 * 
	 * @param pFileName
	 */
	public void createTestFile(String pFileName) {

		BufferedWriter writer = null;
		try {

			File logFile = new File(pFileName);
			filePath = logFile.getCanonicalPath();
			logger.info(filePath);

			writer = new BufferedWriter(new FileWriter(logFile));
			writer.write("Hello world!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
	}


	
}
