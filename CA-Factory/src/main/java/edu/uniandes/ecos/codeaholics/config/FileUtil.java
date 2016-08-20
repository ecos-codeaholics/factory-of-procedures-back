
/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.Part;

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
 * Example: Given a directory PATH, return all files that are in this
 * directory according to some criteria
 * 
 * Implementation: Returns an ArrayList of string, containing file paths
 * 
 * Created: Feb 15, 2016 3:47:08 PM
 * 
 */
public class FileUtil {

	public static String LOCAL_TMP_PATH = "D:/Temp/MyDocuments/";
	public static final String LOCAL_TMP_PATH_ENV = "LOCAL_TMP_PATH_ENV";

	private static String root;
	private static ArrayList<String> allFiles = new ArrayList<String>();
	private static File[] files;

	public static ArrayList<String> getAllFiles() {
		return allFiles;
	}

	/**
	 * 
	 */
	public static void processRoot() {

		//TODO Need here an exception
		allFiles.clear();
		files = new File(root).listFiles();
		getSourceFiles(files, allFiles, "");

		for (int i = 0; i < allFiles.size(); ++i) {
			System.out.println(allFiles.get(i));
		}

	}

	/**
	 * @param current_files
	 * @param output
	 * @param fileExt
	 */
	public static void getSourceFiles(File[] current_files, ArrayList<String> output, String fileExt) {

		for (File file : current_files) {
			if (file.isDirectory()) {
				// System.out.println("Directory: " + file.getName());
				getSourceFiles(file.listFiles(), output, fileExt); // Calls same
																	// method
																	// again.
			} else {
				// if (file.getName()) {
				// System.out.println("File: " + file.getName());
				output.add(file.getAbsolutePath());
				// }
			}
		}
	}

	/**
	 * @return
	 */
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
	 * @param part
	 * @return
	 */
	private static String getFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	/**
	 * @param pPath
	 * @return
	 */
	public static ArrayList<String> listDownloadedFiles(String pPath) {

		ArrayList<String> currentFiles = new ArrayList<String>();
		processRoot();
		currentFiles = getAllFiles();
		return currentFiles;
	}

	/**
	 * @param str
	 * @return
	 */
	private static String removeLastChar(String str) {
		return str.substring(0, str.length() - 1);
	}

}
