/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: FileUtil
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: This class provides all sort of File related utilities
 * 
 * Example: Given a directory PATH, return all files that are in this directory
 * according to some criteria
 * 
 * Implementation: Returns an ArrayList of string, containing file paths
 * 
 * Created: Feb 15, 2016 3:47:08 PM
 * 
 */
public class FileUtil {

	public static String LOCAL_TMP_PATH;

	public static final String LOCAL_TMP_PATH_ENV = "LOCAL_TMP_PATH_ENV";

	private static final int MAX_DEPTH_LEVEL = 1;

	private static int currentLevel = 0;

	private static ArrayList<String> allFiles = new ArrayList<String>();

	private static File[] files;

	/**
	 * 
	 */
	public static void processRoot(String pRoot) {

		// TODO Need here an exception
		allFiles.clear();
		files = new File(pRoot).listFiles();
		currentLevel = 1;
		getSourceFiles(files, allFiles, "");

	}

	/**
	 * @param current_files
	 * @param output
	 * @param fileExt
	 */
	public static void getSourceFiles(File[] pCurrentFiles, ArrayList<String> pOutput, String pFileExt) {

		for (File file : pCurrentFiles) {
			if (file.isDirectory() && (currentLevel < MAX_DEPTH_LEVEL)) {
				getSourceFiles(file.listFiles(), pOutput, pFileExt);
				currentLevel += 1;
			} else {
				pOutput.add(file.getAbsolutePath());
			}
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String getList() {

		ArrayList<String> files = listDownloadedFiles(LOCAL_TMP_PATH);
		Iterator<String> itrFiles = files.iterator();
		String allFiles = "[";

		while (itrFiles.hasNext()) {
			String jsonStr = "{\"file" + "\": \"" + itrFiles.next().replace("\\", "/") + "\"}";
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(jsonStr).getAsJsonObject();
			allFiles += obj.toString() + ",";
		}
		allFiles = removeLastChar(allFiles) + "]";
		return allFiles;
	}

	/**
	 * @return
	 */
	public static ArrayList<String> getAllFiles() {
		return allFiles;
	}

	/**
	 * @param pPath
	 * @return
	 */
	public static ArrayList<String> listDownloadedFiles(String pRoot) {

		ArrayList<String> currentFiles = new ArrayList<String>();
		processRoot(pRoot);
		currentFiles = getAllFiles();
		return currentFiles;
	}

	/**
	 * @param str
	 * @return
	 */
	public static String removeLastChar(String pStr) {
		return pStr.substring(0, pStr.length() - 1);
	}

	/**
	 * @throws Exception
	 */
	public static void configTmpDir() throws Exception {

		ArrayList<String> localStorage = new ArrayList<String>();

		localStorage.add(LOCAL_TMP_PATH_ENV); // This is the preferred
												// environment variable
		localStorage.add("TMP"); // second best - linux, windows
		localStorage.add("HOME"); // if previous fail, last chance

		Iterator<String> itrPath = localStorage.iterator();

		boolean found = false;

		while (itrPath.hasNext()) {
			// Get the TMP_PATH from an environment variable
			String testPath = itrPath.next();
			String value = System.getenv(testPath);
			if (value != null) {
				LOCAL_TMP_PATH = value;
				// System.out.println("****" + LOCAL_TMP_PATH);
				found = true;
				break;
			}
		}

		if (!found)
			throw new Exception("LOCAL_TMP_PATH_ENV not defined!");

	}

	/**
	 * Save an image enconde in a Base64 string
	 * 
	 * @param pImageName image full path
	 * @param pImageStr base64 enconded image
	 * @return
	 */
	public static String getImageFromString(String pImageName, String pImageStr) {

		String testImg = "./src/main/resources/tests/barcode.png";
		
		try {
			configTmpDir();
		} catch (Exception e1) {
			System.out.println("Cannot find localdirectory");
			e1.printStackTrace();
			return testImg;
		}

		byte[] bytes = Base64.getDecoder().decode(pImageStr);
		FileOutputStream fos;
		
		try {

			StringBuilder filePath = new StringBuilder();
			filePath.append(LOCAL_TMP_PATH);
			filePath.append("/");
			filePath.append(pImageName);
			filePath.append(".png");

			fos = new FileOutputStream(filePath.toString());
			fos.write(bytes);
			fos.close();

			return filePath.toString();

		} catch (IOException e) {
			System.out.println("Cannot save file");
			e.printStackTrace();
		}

		return testImg;
		
	}

}
