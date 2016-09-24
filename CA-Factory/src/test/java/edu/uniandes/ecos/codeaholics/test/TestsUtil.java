/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.main.App;
import edu.uniandes.ecos.codeaholics.persistence.Activity;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import edu.uniandes.ecos.codeaholics.persistence.Dependency;
import edu.uniandes.ecos.codeaholics.persistence.FieldAttribute;
import edu.uniandes.ecos.codeaholics.persistence.FieldOptions;
import edu.uniandes.ecos.codeaholics.persistence.FieldValidation;
import edu.uniandes.ecos.codeaholics.persistence.FileDocument;
import edu.uniandes.ecos.codeaholics.persistence.FormField;
import edu.uniandes.ecos.codeaholics.persistence.FormField.Type;
import edu.uniandes.ecos.codeaholics.persistence.Functionary;
import edu.uniandes.ecos.codeaholics.persistence.Mayoralty;
import edu.uniandes.ecos.codeaholics.persistence.Procedure;
import edu.uniandes.ecos.codeaholics.persistence.ProcedureRequest;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: TestsUtil TestsUtil.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Utilities for running tests
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 14, 2016 5:39:16 PM
 * 
 */
public class TestsUtil {

	static Logger logger = LogManager.getRootLogger();

	private String citizenSalt;

	/**
	 * @return the citizenSalt
	 */
	public String getCitizenSalt() {
		return citizenSalt;
	}

	/**
	 * add citizen to db for testing purposes. if it already exists, just update
	 * 
	 * @param pName
	 * @param pLastName1
	 * @param pEmail
	 * @param pPwd
	 */
	public void addCitizen(String pName, String pLastName1, String pEmail, String pPwd) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("citizen");

		Citizen citizen = new Citizen();
		citizen.setName(pName);
		citizen.setLastName1(pLastName1);
		citizen.setIdentification(1234567890);
		citizen.setEmail(pEmail);
		citizen.setPassword(pPwd);
		citizen.setUserProfile("citizen");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);

		Document user = new Document();
		user.append("email", pEmail);
		ArrayList<Document> documents = DataBaseUtil.find(user, "citizen");

		if (documents.isEmpty()) {
			collection.insertOne(citizen.toDocument());
		} else {
			logger.info("user alreadery exists");
			collection.findOneAndDelete(user);
			collection.insertOne(citizen.toDocument());
		}

		citizenSalt = hash[0];

	}

	/**
	 * @param pName
	 * @param pLastName1
	 * @param pEmail
	 * @param pPwd
	 */
	public void addFunctionary(String pName, String pLastName1, String pEmail, String pPwd) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("functionary");

		Mayoralty mayorality = new Mayoralty();
		mayorality.setName("Lenguazaque");
		mayorality.setAddress("CRA 123 45 1");
		mayorality.setUrl("https://lenguazaque.gov.co");
		mayorality.setPhone("333555888");

		Functionary citizen = new Functionary();
		citizen.setName(pName);
		citizen.setLastName1(pLastName1);
		citizen.setIdentification(1234567890);
		citizen.setEmail(pEmail);
		citizen.setPassword(pPwd);
		citizen.setUserProfile("functionary");

		citizen.setMayoralty("Lenguazaque");
		citizen.setPosition("Secretario Tesoreria");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);

		Document user = new Document();
		user.append("email", pEmail);
		ArrayList<Document> documents = DataBaseUtil.find(user, "citizen");

		if (documents.isEmpty()) {
			collection.insertOne(citizen.toDocument());
		} else {
			logger.info("user alreadery exists");
			collection.findOneAndDelete(user);
			collection.insertOne(citizen.toDocument());
		}

		citizenSalt = hash[0];
	}

	public static void addProcedure() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("procedures");

		/*
		 * private String name; private ArrayList<Activity> activities; private
		 * ArrayList<FileDocument> required; private ArrayList<FormField>
		 * fields;
		 */

		Procedure procedure = new Procedure();

		procedure.setName("Certificado de residencia");

		Activity activity = new Activity();
		activity.setName("Aprobacion");
		activity.setDescription("Revisar documentacion y aprobar");
		
		ArrayList<FileDocument> fileDocs = new ArrayList<FileDocument>();
		activity.setGenerated(fileDocs);
		//activity.setStartDate(LocalDate.of(2016, Month.SEPTEMBER, 24));

		ArrayList<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		
		procedure.setActivities(activities);
		
		Dependency dependency = new Dependency();
		dependency.setName("Secretaria de Gobierno");
		dependency.setExtension("EXT12345");

		Mayoralty mayorality = new Mayoralty();
		mayorality.setName("Anapoima");
		mayorality.setAddress("CRA 123 45 1");
		mayorality.setUrl("https://anapoima.gov.co");
		mayorality.setPhone("333555888");

		Functionary functionary = new Functionary();
		functionary.setName("Juan");
		functionary.setLastName1("Valdes");
		functionary.setIdentification(1234567890);
		functionary.setEmail("jvaldes@anapoima.gov");
		functionary.setUserProfile("functionary");
		functionary.setMayoralty("Lenguazaque");
		functionary.setPosition("Secretario de Gobierno");

		ArrayList<Functionary> listOfFunctionaries = new ArrayList<Functionary>();
		listOfFunctionaries.add(functionary);
		dependency.setFunctionaries(listOfFunctionaries);

		activity.setDependency(dependency);

		ArrayList<FileDocument> requiredDocs = new ArrayList<FileDocument>();
		procedure.setRequired(requiredDocs);
		
		ArrayList<FormField> formFields = new ArrayList<FormField>();
		
		FormField field1 = new FormField();
		field1.setLabel("Cedula");
		field1.setType(Type.number);
		field1.setHelpText("Ingrese aqui su cedula");
		field1.setRequired(true);

		field1.setFieldAttribute(new FieldAttribute());
		field1.setFieldOptions(new FieldOptions());
		field1.setFieldValidation(new FieldValidation());
		
		FormField field2 = new FormField();
		field2.setLabel("Direccion");
		field2.setType(Type.text);
		field2.setHelpText("Ingrese aqui su direccion");
		field2.setRequired(true);
		field2.setFieldAttribute(new FieldAttribute());
		field2.setFieldOptions(new FieldOptions());
		field2.setFieldValidation(new FieldValidation());
		
		formFields.add(field1);
		formFields.add(field2);

		// FieldAttribute fattribute = new FieldAttribute();
		// FieldValidation
		// FieldOptions

		logger.info("inserting new procedure instance");
		collection.insertOne(procedure.toDocument());

	}

	public void addProcedureRequest() {

		//MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		//MongoCollection<Document> collection = dbOne.getCollection("procedure_request");

		/*
		 * private String _id; private String procedureClass; private Long
		 * fileNumber; private Citizen citizen; ok private Mayoralty mayoralty;
		 * ok private Document procedureData; private ArrayList<String>
		 * deliveryDocs;
		 */
		ProcedureRequest procedure = new ProcedureRequest();

		procedure.setProcedureClass("Certificado de residencia");
		
		Mayoralty mayorality = new Mayoralty();
		mayorality.setName("Anapoima");
		mayorality.setAddress("CRA 123 45 6");
		mayorality.setUrl("https://anapoima.gov.co");
		mayorality.setPhone("333555888");

		Citizen citizen = new Citizen();
		citizen.setName("Juan");
		citizen.setLastName1("Valdes");
		citizen.setIdentification(1234567890);
		citizen.setEmail("jvaldes@uniandes");
		citizen.setPassword("Qwerty");
		citizen.setUserProfile("citizen");

	}

	/**
	 * get jetty server full URL
	 * 
	 * @return
	 */
	public static String getServerPath() {

		int port = App.JETTY_SERVER_PORT;
		String server = "http://localhost";

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(server);
		strBuilder.append(":");
		strBuilder.append(port);
		String serverPath = strBuilder.toString();

		return serverPath;
	}

	/**
	 * Remove a created file from local storage
	 * 
	 * @param pFileName
	 */
	public static void removeTestFile(String pFileName) {

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

	/**
	 * create a temporary file for this test
	 * 
	 * @param pFileName
	 */
	public static void createTestFile(String pFileName) {

		BufferedWriter writer = null;
		try {

			File logFile = new File(pFileName);
			String filePath = logFile.getCanonicalPath();
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
	 * @throws Exception
	 */
	public static String getTmpDir() throws Exception {

		String tmpPath = null;
		ArrayList<String> localStorage = new ArrayList<String>();

		localStorage.add("LOCAL_TMP_PATH_ENV"); // This is the preferred
												// environment variable
		localStorage.add("TMP"); // second best - linux, windows
		localStorage.add("HOME"); // if previous fail, last chance

		Iterator<String> itrPath = localStorage.iterator();

		boolean found = false;

		// Get the TMP_PATH from an environment variable
		while (itrPath.hasNext()) {
			String testPath = itrPath.next();
			String value = System.getenv(testPath);
			if (value != null) {
				tmpPath = value;
				found = true;
				break;
			}
		}
		if (!found)
			throw new Exception("TMP not defined!");

		return tmpPath;

	}

	/**
	 * Check if the input directory exists, if not then create it
	 * 
	 * @param inputDir
	 */
	public static void checkDir(String inputDir) {

		File theDir = new File(inputDir);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			logger.info("creating directory: " + inputDir);
			boolean result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				se.printStackTrace();
			}
			if (result) {
				logger.info("DIR created");
			}
		}
	}

	public static void isConnected() throws IOException {

		String strUrl = "http://stackoverflow.com/about";

		try {
			URL url = new URL(strUrl);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.connect();

		} catch (IOException e) {
			logger.error("Error creating HTTP connection");
			e.printStackTrace();
			throw e;
		}

	}

}
