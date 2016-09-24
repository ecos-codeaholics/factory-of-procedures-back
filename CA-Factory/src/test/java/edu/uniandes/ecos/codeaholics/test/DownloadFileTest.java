package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spark.Spark.get;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import edu.uniandes.ecos.codeaholics.business.CitizenServices;
import edu.uniandes.ecos.codeaholics.config.Routes;
import edu.uniandes.ecos.codeaholics.main.App;
import spark.Spark;

public class DownloadFileTest {

	Logger logger = LogManager.getLogger(DownloadFileTest.class);
	String filePath = "";

	//This is only for testing purposes (so the route is unprotected)
	private static String DOC_LIST_ROUTE = Routes.AUTH + "/citizens/documents/list";
	private static String DOC_DOWNLOAD_ROUTE = Routes.AUTH + "/citizens/documents/download";
	
	
	private static final int BUFFER_SIZE = 4096;

	private class DocumentPath {

		private String file;

		public String getFile() {
			return file;
		}

		@SuppressWarnings("unused")
		public void setFile(String file) {
			this.file = file;
		}
	}

	@BeforeClass
	public static void beforeClass() {
		App.main(null);
		get(DOC_LIST_ROUTE, CitizenServices::listDocuments);
		get(DOC_DOWNLOAD_ROUTE, CitizenServices::downloadDocuments);
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}

	@Test
	public void listTest() {

		int httpResult = 0;
		String httpMessage = "";
		StringBuilder result = new StringBuilder();

		String serverPath = TestsUtil.getServerPath();
		String tmpFile = "HelloWorld.txt";
		String tmpFilePath = "";

		try {
			tmpFilePath = TestsUtil.getTmpDir() + "/" + tmpFile;
		} catch (Exception e1) {
			tmpFilePath = tmpFile;
			e1.printStackTrace();
		}

		TestsUtil.createTestFile(tmpFilePath);

		boolean found = false;

		try {

			URL appUrl = new URL(serverPath + DOC_LIST_ROUTE);

			// TODO ... study and understand why this fixes the Connection
			// refused error
			System.out.println("===== 0. ");
			InputStream response = new URL("http://stackoverflow.com").openStream();
			response.close();
			System.out.println("===== 0. =====");

			HttpURLConnection urlConnection = (HttpURLConnection) appUrl.openConnection();

			httpResult = urlConnection.getResponseCode();
			httpMessage = urlConnection.getResponseMessage();

			InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
			BufferedReader reader = new BufferedReader(in);

			String text = "";
			while ((text = reader.readLine()) != null) {
				result.append(text);
			}

			JsonParser parser = new JsonParser();
			JsonArray json = parser.parse(result.toString()).getAsJsonArray();

			reader.close();
			in.close();

			Iterator<JsonElement> itr = json.iterator();
			while (itr.hasNext()) {
				JsonElement jElement = itr.next();
				Gson gson = new GsonBuilder().create();
				DocumentPath p = gson.fromJson(jElement.getAsJsonObject(), DocumentPath.class);

				String items[] = p.getFile().split("/");
				if (items[items.length - 1].equals(tmpFile)) {
					logger.info(tmpFile);
					found = true;

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(200, httpResult);
		assertEquals("OK", httpMessage);
		assertTrue(found);

		TestsUtil.removeTestFile(tmpFilePath);

	}

	@Test
	public void downloadTest() {

		int httpResult = 0;
		String httpMessage = "";

		String serverPath = TestsUtil.getServerPath();
		String tmpFile = "DownHelloWorld.txt";
		String tmpFilePath = "";
		String saveDir = "";

		try {
			tmpFilePath = TestsUtil.getTmpDir() + File.separator + tmpFile;
			saveDir = TestsUtil.getTmpDir() + File.separator + "downloads";
			TestsUtil.checkDir(saveDir);

		} catch (Exception e1) {
			tmpFilePath = tmpFile;
			e1.printStackTrace();
		}

		TestsUtil.createTestFile(tmpFilePath);

		boolean success = false;

		URL appUrl;

		try {

			appUrl = new URL(serverPath + DOC_DOWNLOAD_ROUTE + "?filepath=" + tmpFile);

			// TODO ... study and understand why this fixes the Connection
			// refused error
			System.out.println("===== 0. ");
			InputStream response = new URL("http://stackoverflow.com").openStream();
			response.close();
			System.out.println("===== 0. =====");

			HttpURLConnection urlConnection = (HttpURLConnection) appUrl.openConnection();

			httpResult = urlConnection.getResponseCode();
			httpMessage = urlConnection.getResponseMessage();

			if (httpResult == HttpURLConnection.HTTP_OK) {

				String fileName = "";
				String disposition = urlConnection.getHeaderField("Content-Disposition");
				String contentType = urlConnection.getContentType();
				int contentLength = urlConnection.getContentLength();

				if (disposition != null) {
					// extracts file name from header field
					int index = disposition.indexOf("filename=");
					if (index > 0) {
						fileName = disposition.substring(index + 9, disposition.length());
					}
				} else {
					fileName = tmpFile;
				}

				logger.info("Content-Type = " + contentType);
				logger.info("Content-Disposition = " + disposition);
				logger.info("Content-Length = " + contentLength);
				logger.info("fileName = " + fileName);

				// opens input stream from the HTTP connection
				InputStream inputStream = urlConnection.getInputStream();

				String saveFilePath = saveDir + File.separator + fileName;
				// opens an output stream to save into file
				FileOutputStream outputStream = new FileOutputStream(saveFilePath);

				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				outputStream.close();
				inputStream.close();

				success = true;

				logger.info("File downloaded");
			} else {
				logger.info("No file to download. Server replied HTTP code: " + httpResult);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		TestsUtil.removeTestFile(tmpFilePath);
		assertEquals(200, httpResult);
		assertEquals("OK", httpMessage);
		assertTrue(success);

	}

}
