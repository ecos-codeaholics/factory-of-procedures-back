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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
		String server = "http://localhost:4567";

		String attachmentName = "testFile";
		String attachmentFileName = timeLog;

		String crlf = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		try {

			URL appUrl = new URL(server + route);

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

			System.out.println(jsonResponse);
			System.out.println(result.toString());

			urlConnection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * JsonParser parser = new JsonParser(); JsonObject json =
		 * parser.parse(result.toString()).getAsJsonObject();
		 */

		assertEquals(200, httpResult);
		assertEquals("OK", httpMessage);

		removeTestFile(timeLog);

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

	/**
	 * Remove the created file from local storage
	 * 
	 * @param pFileName
	 */
	private void removeTestFile(String pFileName) {
		
		File file = new File(pFileName);
		
		try {
			Files.delete(file.toPath());
			logger.info("Temporay file deleted: " + file.getAbsolutePath());
		} catch (NoSuchFileException x) {
			logger.error("%s: no such" + " file or directory%n", file);
		} catch (DirectoryNotEmptyException x) {
			logger.error("%s not empty%n", file);
		} catch (IOException x) {
			// File permission problems are caught here.
			logger.error(x.getMessage());
		}
	}
	
}
