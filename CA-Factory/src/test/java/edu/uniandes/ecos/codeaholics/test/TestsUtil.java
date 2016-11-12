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
import java.util.Date;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uniandes.ecos.codeaholics.config.Constants;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.main.App;
import edu.uniandes.ecos.codeaholics.persistence.Activity;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import edu.uniandes.ecos.codeaholics.persistence.Dependency;
import edu.uniandes.ecos.codeaholics.persistence.FormField;
import edu.uniandes.ecos.codeaholics.persistence.Functionary;
import edu.uniandes.ecos.codeaholics.persistence.History;
import edu.uniandes.ecos.codeaholics.persistence.Mayoralty;
import edu.uniandes.ecos.codeaholics.persistence.Procedure;
import edu.uniandes.ecos.codeaholics.persistence.ProcedureRequest;
import edu.uniandes.ecos.codeaholics.persistence.RequiredUpload;
import edu.uniandes.ecos.codeaholics.persistence.Session;

//import edu.uniandes.ecos.codeaholics.persistence.Dependency;
//import edu.uniandes.ecos.codeaholics.persistence.FieldAttribute;
//import edu.uniandes.ecos.codeaholics.persistence.FieldOptions;
//import edu.uniandes.ecos.codeaholics.persistence.FieldValidation;
//import edu.uniandes.ecos.codeaholics.persistence.FormField.Type;

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

	private static String citizenSalt;

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
		MongoCollection<Document> collection = dbOne.getCollection(Constants.CITIZEN_COLLECTION);

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
		ArrayList<Document> documents = DataBaseUtil.find(user, Constants.CITIZEN_COLLECTION);

		if (documents.isEmpty()) {
			collection.insertOne(citizen.toDocument());
		} else {
			logger.info("user alreadery exists: " + pName);
			collection.findOneAndDelete(user);
			collection.insertOne(citizen.toDocument());
		}

		citizenSalt = hash[0];

	}

	/** Cleanup DB of Test users
	 * @param pEmail
	 */
	public void removeCitizen(String pEmail) {
		
		Document user = new Document();
		user.append("email", pEmail);
		logger.info("Removing user with email ... " + pEmail);
		DataBaseUtil.delete(user, Constants.CITIZEN_COLLECTION);
		
	}
	
	/** Create a mock session for a specific user
	 * @param pEmail
	 * @param pProfile
	 * @param pToken
	 * @param pSalt
	 */
	public void addSession(String pEmail, String pProfile, String pToken, String pSalt ) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection(Constants.SESSION_COLLECTION);

		Session session = new Session();
		session.setEmail(pEmail);
		session.setUserProfile(pProfile);
		session.setToken(pToken);
		session.setSalt(pSalt);
		
		Document prevSession = new Document();
		prevSession.append("email", pEmail);
		ArrayList<Document> documents = DataBaseUtil.find(prevSession, Constants.SESSION_COLLECTION);

		if (documents.isEmpty()) {
			collection.insertOne(session.toDocument());
		} else {
			logger.info("session alreadery exists for: " + pEmail);
			collection.findOneAndDelete(prevSession);
			collection.insertOne(session.toDocument());
		}

	}
	
	/**
	 * 
	 */
	public static void clearAllCollections() {

		logger.info("clearing all existing collections in the default DB");

		ArrayList<String> collections = new ArrayList<String>();
		collections.add("citizen");
		collections.add("functionary");
		collections.add("mayoralty");
		collections.add("procedures");
		collections.add("proceduresRequest");
		collections.add(Constants.SESSION_COLLECTION);

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection;

		Iterator<String> itrColl = collections.iterator();

		while (itrColl.hasNext()) {
			String collectionName = itrColl.next();
			collection = dbOne.getCollection(collectionName);
			collection.drop();
			logger.info("Collection " + collectionName + " dropped");
		}

	}

	// add citizen
	public static void addCitizenUno() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection(Constants.CITIZEN_COLLECTION);

		Citizen citizen = new Citizen();
		citizen.setName("Andr\u00E9s");
		citizen.setLastName1("Osorio");
		citizen.setIdentification(1234567890);
		citizen.setEmail("andres@uniandes");
		citizen.setPassword("12345678");
		citizen.setUserProfile("citizen");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);

		Document user = new Document();
		user.append("email", "andres@uniandes");
		ArrayList<Document> documents = DataBaseUtil.find(user, Constants.CITIZEN_COLLECTION);

		if (documents.isEmpty()) {
			collection.insertOne(citizen.toDocument());
		} else {
			logger.info("user alreadery exists");
			collection.findOneAndDelete(user);
			collection.insertOne(citizen.toDocument());
		}

		citizenSalt = hash[0];

	}

	// add citizen
	public static void addCitizenDos() {
		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection(Constants.CITIZEN_COLLECTION);

		Citizen citizen = new Citizen();
		citizen.setName("Fabian");
		citizen.setLastName1("Hernandez");
		citizen.setIdentification(1234567890);
		citizen.setEmail("fabian@uniandes");
		citizen.setPassword("12345678");
		citizen.setUserProfile("citizen");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);

		Document user = new Document();
		user.append("email", "fabian@uniandes");
		ArrayList<Document> documents = DataBaseUtil.find(user, Constants.CITIZEN_COLLECTION);

		if (documents.isEmpty()) {
			collection.insertOne(citizen.toDocument());
		} else {
			logger.info("user alreadery exists");
			collection.findOneAndDelete(user);
			collection.insertOne(citizen.toDocument());
		}

		citizenSalt = hash[0];

	}

	// add citizen
	public static void addCitizenTres() {
		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection(Constants.CITIZEN_COLLECTION);

		Citizen citizen = new Citizen();
		citizen.setName("Jheison");
		citizen.setLastName1("Rodriguez");
		citizen.setIdentification(1234567890);
		citizen.setEmail("jheison@uniandes");
		citizen.setPassword("12345678");
		citizen.setUserProfile("citizen");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);

		Document user = new Document();
		user.append("email", "jheison@uniandes");
		ArrayList<Document> documents = DataBaseUtil.find(user, Constants.CITIZEN_COLLECTION);

		if (documents.isEmpty()) {
			collection.insertOne(citizen.toDocument());
		} else {
			logger.info("user alreadery exists");
			collection.findOneAndDelete(user);
			collection.insertOne(citizen.toDocument());
		}

		citizenSalt = hash[0];
	}

	// add citizen
	public static void addCitizenCuatro() {
		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("citizen");

		Citizen citizen = new Citizen();
		citizen.setName("David");
		citizen.setLastName1("Martinez");
		citizen.setIdentification(1234567890);
		citizen.setEmail("david@uniandes");
		citizen.setPassword("12345678");
		citizen.setUserProfile("citizen");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);

		Document user = new Document();
		user.append("email", "david@uniandes");
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

	public static void addCitizenCinco() {
		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("citizen");

		Citizen citizen = new Citizen();
		citizen.setName("Sebastian");
		citizen.setLastName1("Cardona");
		citizen.setIdentification(1234567890);
		citizen.setEmail("sebastian@uniandes");
		citizen.setPassword("12345678");
		citizen.setUserProfile("citizen");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);

		Document user = new Document();
		user.append("email", "sebastian@uniandes");
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

	// add Alcaldia uno
	public static void addMayoraltyUno() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("mayoralty");

		Mayoralty mayoralty = new Mayoralty();
		mayoralty.setName("Anapoima");
		mayoralty.setAddress("CRA 123 45 1");
		mayoralty.setUrl("https://anapoima.gov.co");
		mayoralty.setPhone("333555888");

		Dependency dependencyUno = new Dependency();
		dependencyUno.setName("Hacienda");

		ArrayList<Functionary> funcionaryUno = new ArrayList<>();
		Functionary funcionarioUno = new Functionary();

		funcionarioUno.setEmail("jvaldez@anapoima");
		funcionaryUno.add(funcionarioUno);
		dependencyUno.setFunctionaries(funcionaryUno);

		Dependency dependencyDos = new Dependency();
		dependencyDos.setName("Atenci\u00F3n al Ciudadano");

		ArrayList<Functionary> funcionaryDos = new ArrayList<>();
		Functionary funcionarioDos = new Functionary();

		funcionarioDos.setEmail("acalle@anapoima");
		funcionaryDos.add(funcionarioDos);
		dependencyDos.setFunctionaries(funcionaryDos);

		ArrayList<Dependency> dependencies = new ArrayList<>();

		dependencies.add(dependencyUno);
		dependencies.add(dependencyDos);

		mayoralty.setDependencies(dependencies);

		ArrayList<String> procedures = new ArrayList<>();
		procedures.add("Certificado de residencia");
		procedures.add("Auxilio para Gastos Sepelio");
		procedures.add("Solicitud De Contrataci\u00F3on Monitor Deportes");

		mayoralty.setProcedures(procedures);

		collection.insertOne(mayoralty.toDocument());

	}

	// add Alcaldia dos
	public static void addMayoraltyDos() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("mayoralty");

		Mayoralty mayoralty = new Mayoralty();
		mayoralty.setName("El Rosal");
		mayoralty.setAddress("CRA 456 78 1");
		mayoralty.setUrl("https://elrosal.gov.co");
		mayoralty.setPhone("99977766");

		Dependency dependencyUno = new Dependency();
		dependencyUno.setName("Hacienda");

		ArrayList<Functionary> funcionaryUno = new ArrayList<>();
		Functionary funcionarioUno = new Functionary();

		funcionarioUno.setEmail("jvaldez@elrosal");
		funcionaryUno.add(funcionarioUno);
		dependencyUno.setFunctionaries(funcionaryUno);

		Dependency dependencyDos = new Dependency();
		dependencyDos.setName("Atenci\u00F3on al Ciudadano");

		ArrayList<Functionary> funcionaryDos = new ArrayList<>();
		Functionary funcionarioDos = new Functionary();

		funcionarioDos.setEmail("acalle@elrosal");
		funcionaryDos.add(funcionarioDos);
		dependencyDos.setFunctionaries(funcionaryDos);

		ArrayList<Dependency> dependencies = new ArrayList<>();

		dependencies.add(dependencyUno);
		dependencies.add(dependencyDos);

		mayoralty.setDependencies(dependencies);

		ArrayList<String> procedures = new ArrayList<>();
		procedures.add("Auxilio para Gastos Sepelio");
		procedures.add("Certificado de estratificaci\u00F3n");
		procedures.add("Solicitud De Contrataci\u00F3n Monitor Deportes");

		mayoralty.setProcedures(procedures);

		collection.insertOne(mayoralty.toDocument());

	}

	/**
	 * @param pName
	 * @param pLastName1
	 * @param pEmail
	 * @param pPwd
	 */

	// funcionario1
	public static void addFunctionaryUno(String pName, String pLastName1, String pEmail, String pPwd) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("functionary");
		MongoCollection<Document> collectionC = dbOne.getCollection("citizen");

		Mayoralty mayoralty = new Mayoralty();
		mayoralty.setName("Anapoima");
		mayoralty.setAddress("CRA 123 45 1");
		mayoralty.setUrl("https://anapoima.gov.co");
		mayoralty.setPhone("333555888");

		Functionary citizen = new Functionary();
		citizen.setName(pName);
		citizen.setLastName1(pLastName1);
		citizen.setIdentification(1234567890);
		citizen.setEmail(pEmail);
		citizen.setPassword(pPwd);
		citizen.setUserProfile("admin");

		citizen.setMayoralty("Anapoima");
		citizen.setDependency("Hacienda");

		String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
		citizen.setPassword(hash[1]);
		citizen.setSalt(hash[0]);

		Document user = new Document();
		user.append("email", pEmail);
		ArrayList<Document> documents = DataBaseUtil.find(user, "citizen");
		collectionC.insertOne(citizen.toDocument());

		if (documents.isEmpty()) {
			collection.insertOne(citizen.toDocument());
		} else {
			logger.info("user alreadery exists");
			collection.findOneAndDelete(user);
			collection.insertOne(citizen.toDocument());
		}

		citizenSalt = hash[0];
	}

	// funcionario2
	public static void addFunctionaryDos(String pName, String pLastName1, String pEmail, String pPwd) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("functionary");

		Mayoralty mayoralty = new Mayoralty();
		mayoralty.setName("Anapoima");
		mayoralty.setAddress("CRA 123 45 1");
		mayoralty.setUrl("https://anapoima.gov.co");
		mayoralty.setPhone("333555888");

		Functionary citizen = new Functionary();
		citizen.setName(pName);
		citizen.setLastName1(pLastName1);
		citizen.setIdentification(1234567890);
		citizen.setEmail(pEmail);
		citizen.setPassword(pPwd);
		citizen.setUserProfile("functionary");

		citizen.setMayoralty("Anapoima");
		citizen.setDependency("Atenci\u00F3n al Ciudadano");

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

	// funcionario2
	public static void addFunctionaryTres(String pName, String pLastName1, String pEmail, String pPwd) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("functionary");

		Mayoralty mayoralty = new Mayoralty();
		mayoralty.setName("El Rosal");
		mayoralty.setAddress("CRA 456 78 1");
		mayoralty.setUrl("https://elrosal.gov.co");
		mayoralty.setPhone("99977766");

		Functionary citizen = new Functionary();
		citizen.setName(pName);
		citizen.setLastName1(pLastName1);
		citizen.setIdentification(1234567890);
		citizen.setEmail(pEmail);
		citizen.setPassword(pPwd);
		citizen.setUserProfile("admin");

		citizen.setMayoralty("El Rosal");
		citizen.setDependency("Hacienda");

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

	// funcionario4
	public static void addFunctionaryCuatro(String pName, String pLastName1, String pEmail, String pPwd) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("functionary");

		Mayoralty mayoralty = new Mayoralty();
		mayoralty.setName("El Rosal");
		mayoralty.setAddress("CRA 456 78 1");
		mayoralty.setUrl("https://elrosal.gov.co");
		mayoralty.setPhone("99977766");

		Functionary citizen = new Functionary();
		citizen.setName(pName);
		citizen.setLastName1(pLastName1);
		citizen.setIdentification(1234567890);
		citizen.setEmail(pEmail);
		citizen.setPassword(pPwd);
		citizen.setUserProfile("functionary");

		citizen.setMayoralty("El Rosal");
		citizen.setDependency("Atenci\u00F3n al Ciudadano");
		;

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

	// Procedure1
	public static void addProcedureUno(String pName) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("procedures");

		// ArrayList<Functionary> listOfFunctionaries = new
		// ArrayList<Functionary>();
		ArrayList<FormField> formFields = new ArrayList<FormField>();
		ArrayList<RequiredUpload> reqDocs = new ArrayList<RequiredUpload>();
		ArrayList<Activity> activities = new ArrayList<Activity>();

		Procedure procedure = new Procedure();
		procedure.setName(pName);

		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Hacienda");
		activity1.setFunctionary("jvaldez@anapoima");
		activity1.setStatus("En curso");

		activities.add(activity1);

		procedure.setActivities(activities);

		// Required
		RequiredUpload reqDoc1 = new RequiredUpload();

		reqDoc1.setType("file");
		reqDoc1.setRequired(true);
		reqDoc1.setClassName("form-control");

		reqDoc1.setLabel("C\u00E9dula de Ciudadan\u00EDa");
		reqDoc1.setDescription("Adjunte su c\u00E9dula en formato (png, jpeg)");
		reqDoc1.setName("cedulaAtt");

		reqDocs.add(reqDoc1);

		RequiredUpload reqDoc2 = new RequiredUpload();

		reqDoc2.setType("file");
		reqDoc2.setRequired(true);
		reqDoc2.setClassName("form-control");

		reqDoc2.setLabel("Recibo");
		reqDoc2.setDescription("Adjunte su recibo en formato (png, jpeg)");
		reqDoc2.setName("reciboAtt");

		reqDocs.add(reqDoc2);

		procedure.setRequired(reqDocs);

		// Form

		FormField field1 = new FormField();

		field1.setType("text");
		field1.setSubtype("tel");
		field1.setRequired(true);
		field1.setLabel("identificaci\u00F3n");
		field1.setDescription("n\u00FAmero de documento de identidad");
		field1.setPlaceHolder("123456789");
		field1.setClassname("form-control");
		field1.setName("identification");
		field1.setMaxlenght(11);

		formFields.add(field1);

		FormField field2 = new FormField();

		field2.setType("text");
		field2.setSubtype("text");
		field2.setRequired(true);
		field2.setLabel("direcci\u00F3n");
		field2.setDescription("direcci\u00F3n de residencia");
		field2.setPlaceHolder("CAlle -- # -- --");
		field2.setClassname("form-control");
		field2.setName("direcci\u00F3n");
		field2.setMaxlenght(100);

		formFields.add(field2);

		FormField field3 = new FormField();

		field3.setType("text");
		field3.setSubtype("text");
		field3.setRequired(true);
		field3.setLabel("barrio");
		field3.setDescription("barrio");
		field3.setPlaceHolder("barrio");
		field3.setClassname("form-control");
		field3.setName("barrio");
		field3.setMaxlenght(50);

		formFields.add(field3);

		FormField field4 = new FormField();

		field4.setType("text");
		field4.setSubtype("tel");
		field4.setRequired(true);
		field4.setLabel("telefono");
		field4.setDescription("n\u00FAmero telef\u00F3nico de contacto");
		field4.setPlaceHolder("3-----");
		field4.setClassname("form-control");
		field4.setName("telefono");
		field4.setMaxlenght(10);

		formFields.add(field4);

		FormField field5 = new FormField();

		field5.setType("textarea");
		field5.setRequired(true);
		field5.setLabel("Carta de Solicitud");
		field5.setDescription("Carta de Solicitud");
		field5.setPlaceHolder("Por favor diligencie su petici\u00F3n detalladamente");
		field5.setClassname("form-control");
		field5.setName("carta");
		field5.setMaxlenght(5000);

		formFields.add(field5);

		procedure.setFields(formFields);

		logger.info("inserting new procedure instance");

		System.out.println(procedure.getFields());
		collection.insertOne(procedure.toDocument());

	}

	// Procedure2
	public static void addProcedureDos(String pName) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("procedures");

		// ArrayList<Functionary> listOfFunctionaries = new
		// ArrayList<Functionary>();
		ArrayList<FormField> formFields = new ArrayList<FormField>();
		ArrayList<RequiredUpload> reqDocs = new ArrayList<RequiredUpload>();

		Procedure procedure = new Procedure();
		procedure.setName(pName);

		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Atenci\u00F3n al ciudadano");
		activity1.setFunctionary("acalle@anapoima");
		activity1.setStatus("Finalizado");

		activities.add(activity1);

		procedure.setActivities(activities);

		// Required
		RequiredUpload reqDoc1 = new RequiredUpload();

		reqDoc1.setType("file");
		reqDoc1.setRequired(true);
		reqDoc1.setClassName("form-control");

		reqDoc1.setLabel("C\u00E9dula de Ciudadan\u00EDa");
		reqDoc1.setDescription("Adjunte su c\u00E9dula en formato (png, jpeg)");
		reqDoc1.setName("cedulaAtt");

		reqDocs.add(reqDoc1);

		RequiredUpload reqDoc2 = new RequiredUpload();

		reqDoc2.setType("file");
		reqDoc2.setRequired(true);
		reqDoc2.setClassName("form-control");

		reqDoc2.setLabel("Certificado Sisben");
		reqDoc2.setDescription("Adjunte su recibo en formato (png, jpeg)");
		reqDoc2.setName("sisbenAtt");

		reqDocs.add(reqDoc2);

		RequiredUpload reqDoc3 = new RequiredUpload();

		reqDoc3.setType("file");
		reqDoc3.setRequired(true);
		reqDoc3.setClassName("form-control");

		reqDoc3.setLabel("Certificado Presidente de la junta");
		reqDoc3.setDescription("Adjunte su recibo en formato (png, jpeg)");
		reqDoc3.setName("juntaAtt");

		reqDocs.add(reqDoc3);

		procedure.setRequired(reqDocs);

		// Form

		FormField field1 = new FormField();

		field1.setType("text");
		field1.setSubtype("tel");
		field1.setRequired(true);
		field1.setLabel("identificaci\u00F3n");
		field1.setDescription("n\u00FAmero de documento de identidad");
		field1.setPlaceHolder("123456789");
		field1.setClassname("form-control");
		field1.setName("identification");
		field1.setMaxlenght(11);

		formFields.add(field1);

		FormField field2 = new FormField();

		field2.setType("text");
		field2.setSubtype("text");
		field2.setRequired(true);
		field2.setLabel("direcci\u00F3n");
		field2.setDescription("direcci\u00F3n de residencia");
		field2.setPlaceHolder("CAlle -- # -- --");
		field2.setClassname("form-control");
		field2.setName("direcci\u00F3n");
		field2.setMaxlenght(100);

		formFields.add(field2);

		FormField field3 = new FormField();

		field3.setType("text");
		field3.setSubtype("text");
		field3.setRequired(true);
		field3.setLabel("barrio");
		field3.setDescription("barrio");
		field3.setPlaceHolder("barrio");
		field3.setClassname("form-control");
		field3.setName("barrio");
		field3.setMaxlenght(50);

		formFields.add(field3);

		FormField field4 = new FormField();

		field4.setType("text");
		field4.setSubtype("tel");
		field4.setRequired(true);
		field4.setLabel("telefono");
		field4.setDescription("n\u00FAmero telefonico de contacto");
		field4.setPlaceHolder("3-----");
		field4.setClassname("form-control");
		field4.setName("telefono");
		field4.setMaxlenght(10);

		formFields.add(field4);

		FormField field5 = new FormField();

		field5.setType("textarea");
		field5.setRequired(true);
		field5.setLabel("Carta de Solicitud");
		field5.setDescription("Carta de Solicitud");
		field5.setPlaceHolder("Por favor diligencie su petici\u00F3n detalladamente");
		field5.setClassname("form-control");
		field5.setName("carta");
		field5.setMaxlenght(5000);

		formFields.add(field5);

		procedure.setFields(formFields);

		logger.info("inserting new procedure instance");

		System.out.println(procedure.getFields());
		collection.insertOne(procedure.toDocument());

	}

	// Procedure3
	public static void addProcedureTres(String pName) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("procedures");

		// ArrayList<Functionary> listOfFunctionaries = new
		// ArrayList<Functionary>();
		ArrayList<FormField> formFields = new ArrayList<FormField>();
		ArrayList<RequiredUpload> reqDocs = new ArrayList<RequiredUpload>();
		ArrayList<Activity> activities = new ArrayList<Activity>();

		Procedure procedure = new Procedure();
		procedure.setName(pName);

		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Hacienda");
		activity1.setFunctionary("jvaldez@elrosal");
		activity1.setStatus("En curso");

		activities.add(activity1);

		procedure.setActivities(activities);

		// Required
		RequiredUpload reqDoc1 = new RequiredUpload();

		reqDoc1.setType("file");
		reqDoc1.setRequired(true);
		reqDoc1.setClassName("form-control");

		reqDoc1.setLabel("C\u00E9dula de ciudadan\u00EDa del solicitante");
		reqDoc1.setDescription("Adjunte su c\u00E9dula en formato (png, jpeg)");
		reqDoc1.setName("cedulaAtt");

		reqDocs.add(reqDoc1);

		RequiredUpload reqDoc2 = new RequiredUpload();

		reqDoc2.setType("file");
		reqDoc2.setRequired(true);
		reqDoc2.setClassName("form-control");

		reqDoc2.setLabel("Comprobante de Pago");
		reqDoc2.setDescription("Adjunte su comprobante (png, jpeg)");
		reqDoc2.setName("facturaAtt");

		reqDocs.add(reqDoc2);

		RequiredUpload reqDoc3 = new RequiredUpload();

		reqDoc3.setType("file");
		reqDoc3.setRequired(true);
		reqDoc3.setClassName("form-control");

		reqDoc3.setLabel("C\u00E9dula del fallecido");
		reqDoc3.setDescription("Adjunte la c\u00E9dula del fallecido (png, jpeg)");
		reqDoc3.setName("cedulaFallecidoAtt");

		reqDocs.add(reqDoc3);

		RequiredUpload reqDoc4 = new RequiredUpload();

		reqDoc4.setType("file");
		reqDoc4.setRequired(true);
		reqDoc4.setClassName("form-control");

		reqDoc4.setLabel("Certificado de defunci\u00F3n");
		reqDoc4.setDescription("Adjunte el certificado de defunci\u00F3n (png, jpeg)");
		reqDoc4.setName("defuncionAtt");

		reqDocs.add(reqDoc4);

		RequiredUpload reqDoc5 = new RequiredUpload();

		reqDoc5.setType("file");
		reqDoc5.setRequired(true);
		reqDoc5.setClassName("form-control");

		reqDoc5.setLabel("Certificado de cuenta bancaria");
		reqDoc5.setDescription("Adjunte el certificado de la cuenta bancaria (png, jpeg)");
		reqDoc5.setName("cuentaAtt");

		reqDocs.add(reqDoc5);

		procedure.setRequired(reqDocs);

		// Form

		FormField field1 = new FormField();

		field1.setType("text");
		field1.setSubtype("tel");
		field1.setRequired(true);
		field1.setLabel("identificaci\u00F3n");
		field1.setDescription("n\u00FAmero de documento de identidad");
		field1.setPlaceHolder("123456789");
		field1.setClassname("form-control");
		field1.setName("identification");
		field1.setMaxlenght(11);

		formFields.add(field1);

		FormField field2 = new FormField();

		field2.setType("text");
		field2.setSubtype("text");
		field2.setRequired(true);
		field2.setLabel("direcci\u00F3n");
		field2.setDescription("direcci\u00F3n de residencia");
		field2.setPlaceHolder("CAlle -- # -- --");
		field2.setClassname("form-control");
		field2.setName("direcci\u00F3n");
		field2.setMaxlenght(100);

		formFields.add(field2);

		FormField field3 = new FormField();

		field3.setType("text");
		field3.setSubtype("text");
		field3.setRequired(true);
		field3.setLabel("barrio");
		field3.setDescription("barrio");
		field3.setPlaceHolder("barrio");
		field3.setClassname("form-control");
		field3.setName("barrio");
		field3.setMaxlenght(50);

		formFields.add(field3);

		FormField field4 = new FormField();

		field4.setType("text");
		field4.setSubtype("tel");
		field4.setRequired(true);
		field4.setLabel("telefono");
		field4.setDescription("n\u00FAmero telefonico de contacto");
		field4.setPlaceHolder("3-----");
		field4.setClassname("form-control");
		field4.setName("telefono");
		field4.setMaxlenght(10);

		formFields.add(field4);

		FormField field5 = new FormField();

		field5.setType("textarea");
		field5.setRequired(true);
		field5.setLabel("Carta de Solicitud");
		field5.setDescription("Carta de Solicitud");
		field5.setPlaceHolder("Por favor diligencie su petici\u00F3n detalladamente");
		field5.setClassname("form-control");
		field5.setName("carta");
		field5.setMaxlenght(5000);

		formFields.add(field5);

		procedure.setFields(formFields);

		logger.info("inserting new procedure instance");

		System.out.println(procedure.getFields());
		collection.insertOne(procedure.toDocument());

	}

	// Procedure
	// SCC
		public static void addProcedureCuatro(String pName) {

			MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
			MongoCollection<Document> collection = dbOne.getCollection("procedures");

			// ArrayList<Functionary> listOfFunctionaries = new
			// ArrayList<Functionary>();
			ArrayList<FormField> formFields = new ArrayList<FormField>();
			ArrayList<RequiredUpload> reqDocs = new ArrayList<RequiredUpload>();
			ArrayList<Activity> activities = new ArrayList<Activity>();

			Procedure procedure = new Procedure();
			procedure.setName(pName);

			// Activities
			activities.add(new Activity("CDP","Certificado de disponibilidad Presupuestal","Jefe de presupuesto",1,"jvaldez@elrosal","Pendiente"));
			activities.add(new Activity("Elaboraci\u00F3n Contrato","Realizaci\u00F3n del contrato","Coordinador de deportes ",2,"jvaldez@elrosal","Pendiente"));
			activities.add(new Activity("Aprobaci\u00F3n juridica","Aprobaci\u00F3n de propuesta","Juridico",3,"jvaldez@elrosal","Pendiente"));
			activities.add(new Activity("Firma Alcalde","Firma del alcalde y Documento de supervisor del contrato","Alcalde",4,"jvaldez@elrosal","Pendiente"));
			activities.add(new Activity("Firma Ciudadano","Firma del contrato por el ciudadano","Ciudadan\u00EDa",5,"","Pendiente"));
			activities.add(new Activity("RP","Responsabilidad presupuestal","Jefe de presupuesto",6,"jvaldez@elrosal","Pendiente"));
			activities.add(new Activity("Informe de labor","Informe del objeto contractual","Ciudadan\u00EDa",7,"","Pendiente"));
			activities.add(new Activity("Informe supervisi\u00F3n","Informe de supervisi\u00F3n del contrato","Coordinador del contrato",8,"jvaldez@elrosal","Pendiente"));
			activities.add(new Activity("Orden de pago","Solicitud de orden de pago y comprobante de egreso","Tesoreria",9,"jvaldez@elrosal","Pendiente"));

			procedure.setActivities(activities);

			// Required
	
			reqDocs.add(new RequiredUpload("file", true, "C\u00E9dula de Ciudadan\u00EDa del solicitante", "Adjunte su c\u00E9dula en formato (png, jpeg)", "form-control", "cedulaAtt"));
			reqDocs.add(new RequiredUpload("file", true, "Documento de proyecto", "Adjunte el documento de proyecto (pdf)", "form-control", "cedulaAtt"));
			reqDocs.add(new RequiredUpload("file", true, "Recibo seguridad social", "Adjunte el recibo seguridad social (png, jpeg)", "form-control", "cedulaAtt"));
			reqDocs.add(new RequiredUpload("file", true, "Hoja de vida de funci\u00F3n publica", "Adjunte su hoja de vida de funci\u00F3n publica (pdf)", "form-control", "cedulaAtt"));
			reqDocs.add(new RequiredUpload("file", true, "Antecedentes Contraloria", "Adjunte sus antecedentes contraloria (pdf, png, jpeg)", "form-control", "cedulaAtt"));
			reqDocs.add(new RequiredUpload("file", true, "Antecedentes Fiscales", "Adjunte sus antecedentes fiscales(pdf, png, jpeg)", "form-control", "cedulaAtt"));
			reqDocs.add(new RequiredUpload("file", true, "RUT", "Adjunte su rut (pdf, png, jpeg)", "form-control", "cedulaAtt"));
			procedure.setRequired(reqDocs);

			// Form

			FormField field1 = new FormField();

			field1.setType("text");
			field1.setSubtype("tel");
			field1.setRequired(true);
			field1.setLabel("identificaci\u00F3n");
			field1.setDescription("n\u00FAmero de documento de identidad");
			field1.setPlaceHolder("123456789");
			field1.setClassname("form-control");
			field1.setName("identification");
			field1.setMaxlenght(11);

			formFields.add(field1);

			FormField field2 = new FormField();

			field2.setType("text");
			field2.setSubtype("text");
			field2.setRequired(true);
			field2.setLabel("direcci\u00F3n");
			field2.setDescription("direcci\u00F3n de residencia");
			field2.setPlaceHolder("CAlle -- # -- --");
			field2.setClassname("form-control");
			field2.setName("direcci\u00F3n");
			field2.setMaxlenght(100);

			formFields.add(field2);

			FormField field3 = new FormField();

			field3.setType("text");
			field3.setSubtype("text");
			field3.setRequired(true);
			field3.setLabel("barrio");
			field3.setDescription("barrio");
			field3.setPlaceHolder("barrio");
			field3.setClassname("form-control");
			field3.setName("barrio");
			field3.setMaxlenght(50);

			formFields.add(field3);

			FormField field4 = new FormField();

			field4.setType("text");
			field4.setSubtype("tel");
			field4.setRequired(true);
			field4.setLabel("telefono");
			field4.setDescription("n\u00FAmero telefonico de contacto");
			field4.setPlaceHolder("3-----");
			field4.setClassname("form-control");
			field4.setName("telefono");
			field4.setMaxlenght(10);

			formFields.add(field4);

			FormField field5 = new FormField();

			field5.setType("textarea");
			field5.setRequired(true);
			field5.setLabel("Carta de Solicitud");
			field5.setDescription("Carta de Solicitud");
			field5.setPlaceHolder("Por favor diligencie su petici\u00F3n detalladamente");
			field5.setClassname("form-control");
			field5.setName("carta");
			field5.setMaxlenght(5000);

			formFields.add(field5);

			procedure.setFields(formFields);

			logger.info("inserting new procedure instance");

			System.out.println(procedure.getFields());
			collection.insertOne(procedure.toDocument());

		}

	// ProcedureRequest1
	@SuppressWarnings("deprecation")
	public static <V> void addProcedureRequestUno() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");

		ProcedureRequest procedureRequest = new ProcedureRequest();

		procedureRequest.setProcedureClassName("Certificado de Residencia");
		procedureRequest.setFileNumber("1");

		Citizen citizen = new Citizen();
		citizen.setEmail("andres@uniandes");
		citizen.setIdentification(123456);
		citizen.setName("andres");
		citizen.setLastName1("osorio");

		procedureRequest.setCitizen(citizen);
		procedureRequest.setMayoralty("anapoima");

		Document procedureData = new Document();
		procedureData.put("identificaci\u00F3n", 123456);
		procedureData.put("direcci\u00F3n", "calle 1 # 12 -12");
		procedureData.put("barrio", "barrio Tal");
		procedureData.put("telefono", 55667733);
		procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");

		procedureRequest.setProcedureData(procedureData);

		Document deliveryDocs = new Document();
		deliveryDocs.put("Doc1", "estaEsLARutaAlDoc1");
		deliveryDocs.put("Doc2", "estaEsLARutaAlDoc2");

		procedureRequest.setDeliveryDocs(deliveryDocs);

		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Hacienda");
		activity1.setFunctionary("anapoima");
		activity1.setAprobacion("En proceso");
		activity1.setStatus("En curso");

		activities.add(activity1);
		procedureRequest.setActivities(activities);
		// History
		ArrayList<History> histories = new ArrayList<History>();
		histories.add(new History(0, "2016/10/26", citizen.getEmail(), "Iniciar", "Se inicia tramite"));
		procedureRequest.setHistories(histories);

		procedureRequest.setStartDate(new Date("2016/07/14"));
		procedureRequest.setFinishDate(null);
		procedureRequest.setStatus("En proceso");

		logger.info("inserting new procedure request instance");
		try {

			collection.insertOne(procedureRequest.toDocument());
		} catch (Exception e) {
			logger.info("addProcedureRequestUno " + e.getMessage());
		}

	}

	// ProcedureRequest2
	@SuppressWarnings("deprecation")
	public static <V> void addProcedureRequestDos() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");

		ProcedureRequest procedureRequest = new ProcedureRequest();

		procedureRequest.setProcedureClassName("Certificado de Residencia");
		procedureRequest.setFileNumber("2");

		Citizen citizen = new Citizen();
		citizen.setEmail("andres@uniandes");
		citizen.setIdentification(123456);
		citizen.setName("andres");
		citizen.setLastName1("osorio");

		procedureRequest.setCitizen(citizen);
		procedureRequest.setMayoralty("anapoima");

		Document procedureData = new Document();
		procedureData.put("identificaci\u00F3n", 123456);
		procedureData.put("direcci\u00F3n", "calle 2 # 23 -23");
		procedureData.put("barrio", "barrio lat");
		procedureData.put("telefono", 55667733);
		procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");

		procedureRequest.setProcedureData(procedureData);

		Document deliveryDocs = new Document();
		deliveryDocs.put("Doc1", "estaEsLARutaAlDoc1");
		deliveryDocs.put("Doc2", "estaEsLARutaAlDoc2");

		procedureRequest.setDeliveryDocs(deliveryDocs);

		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Hacienda");
		activity1.setFunctionary("jvaldez@anapoima");
		activity1.setAprobacion("Finalizado");
		activity1.setStatus("En curso");

		activities.add(activity1);
		procedureRequest.setActivities(activities);

		// History
				ArrayList<History> histories = new ArrayList<History>();
				histories.add(new History(0, "2016/10/26", citizen.getEmail(), "Iniciar", "Se inicia tramite"));
				procedureRequest.setHistories(histories);
				
		procedureRequest.setStartDate(new Date("2016/07/14"));
		procedureRequest.setFinishDate(new Date("2016/08/14"));
		procedureRequest.setStatus("Finalizado");

		logger.info("inserting new procedure request instance");

		collection.insertOne(procedureRequest.toDocument());

	}

	// ProcedureRequest3
	@SuppressWarnings("deprecation")
	public static <V> void addProcedureRequestTres() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");

		ProcedureRequest procedureRequest = new ProcedureRequest();

		procedureRequest.setProcedureClassName("Auxilio para Gastos Sepelio");
		procedureRequest.setFileNumber("3");

		Citizen citizen = new Citizen();
		citizen.setEmail("fabian@uniandes");
		citizen.setIdentification(123456);
		citizen.setName("fabian");
		citizen.setLastName1("hernandez");

		procedureRequest.setCitizen(citizen);
		procedureRequest.setMayoralty("El Rosal");

		Document procedureData = new Document();
		procedureData.put("identificaci\u00F3n", 123456);
		procedureData.put("direcci\u00F3n", "calle 2 # 23 -23");
		procedureData.put("barrio", "barrio lat");
		procedureData.put("telefono", 55667733);
		procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");

		procedureRequest.setProcedureData(procedureData);

		Document deliveryDocs = new Document();
		deliveryDocs.put("Doc1", "estaEsLARutaAlDoc1");
		deliveryDocs.put("Doc2", "estaEsLARutaAlDoc2");
		deliveryDocs.put("Doc3", "estaEsLARutaAlDoc3");

		procedureRequest.setDeliveryDocs(deliveryDocs);
		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Hacienda");
		activity1.setFunctionary("jvaldez@elrosal");
		activity1.setAprobacion("Finalizado");
		activity1.setStatus("En curso");

		activities.add(activity1);
		procedureRequest.setActivities(activities);

		// History
				ArrayList<History> histories = new ArrayList<History>();
				histories.add(new History(0, "2016/10/26", citizen.getEmail(), "Iniciar", "Se inicia tramite"));
				procedureRequest.setHistories(histories);
				
		procedureRequest.setStartDate(new Date("2016/07/21"));
		procedureRequest.setFinishDate(new Date("2016/09/21"));
		procedureRequest.setStatus("Finalizado");

		logger.info("inserting new procedure request instance");

		collection.insertOne(procedureRequest.toDocument());

	}

	// ProcedureRequest4
	@SuppressWarnings("deprecation")
	public static <V> void addProcedureRequestCuatro() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");

		ProcedureRequest procedureRequest = new ProcedureRequest();

		procedureRequest.setProcedureClassName("Auxilio para Gastos Sepelio");
		procedureRequest.setFileNumber("4");

		Citizen citizen = new Citizen();
		citizen.setEmail("fabian@uniandes");
		citizen.setIdentification(123456);
		citizen.setName("fabian");
		citizen.setLastName1("hernandez");

		procedureRequest.setCitizen(citizen);
		procedureRequest.setMayoralty("El Rosal");

		Document procedureData = new Document();
		procedureData.put("identificaci\u00F3n", 123456);
		procedureData.put("direcci\u00F3n", "calle 10 # 10 - 10");
		procedureData.put("barrio", "barrio lat");
		procedureData.put("telefono", 55667733);
		procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");

		procedureRequest.setProcedureData(procedureData);

		Document deliveryDocs = new Document();
		deliveryDocs.put("Doc1", "estaEsLARutaAlDoc1");
		deliveryDocs.put("Doc2", "estaEsLARutaAlDoc2");
		deliveryDocs.put("Doc3", "estaEsLARutaAlDoc3");

		procedureRequest.setDeliveryDocs(deliveryDocs);

		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Atenci\u00F3n al ciudadano");
		activity1.setFunctionary("jvaldez@elrosal");
		activity1.setAprobacion("En proceso");
		activity1.setStatus("En curso");

		activities.add(activity1);
		procedureRequest.setActivities(activities);

		// History
		ArrayList<History> histories = new ArrayList<History>();
		histories.add(new History(0, "2016/10/26", citizen.getEmail(), "Iniciar", "Se inicia tramite"));
		procedureRequest.setHistories(histories);

		procedureRequest.setStartDate(new Date("2016/08/06"));
		procedureRequest.setFinishDate(null);
		procedureRequest.setStatus("En proceso");

		logger.info("inserting new procedure request instance");

		collection.insertOne(procedureRequest.toDocument());

	}

	// ProcedureRequest5
	@SuppressWarnings("deprecation")
	public static <V> void addProcedureRequestCinco() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");

		ProcedureRequest procedureRequest = new ProcedureRequest();

		procedureRequest.setProcedureClassName("Certificado de Residencia");
		procedureRequest.setFileNumber("5");

		Citizen citizen = new Citizen();
		citizen.setEmail("jheison@uniandes");
		citizen.setIdentification(123456);
		citizen.setName("jheison");
		citizen.setLastName1("rodriguez");

		procedureRequest.setCitizen(citizen);
		procedureRequest.setMayoralty("anapoima");

		Document procedureData = new Document();
		procedureData.put("identificaci\u00F3n", 123456);
		procedureData.put("direcci\u00F3n", "calle 1 # 12 -12");
		procedureData.put("barrio", "barrio Tal");
		procedureData.put("telefono", 55667733);
		procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");

		procedureRequest.setProcedureData(procedureData);

		Document deliveryDocs = new Document();
		deliveryDocs.put("Doc1", "estaEsLARutaAlDoc1");
		deliveryDocs.put("Doc2", "estaEsLARutaAlDoc2");

		procedureRequest.setDeliveryDocs(deliveryDocs);

		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Hacienda");
		activity1.setFunctionary("jvaldez@anapoima");
		activity1.setAprobacion("En proceso");
		activity1.setStatus("En curso");

		activities.add(activity1);
		procedureRequest.setActivities(activities);
		// History
				ArrayList<History> histories = new ArrayList<History>();
				histories.add(new History(0, "2016/10/26", citizen.getEmail(), "Iniciar", "Se inicia tramite"));
				procedureRequest.setHistories(histories);
		procedureRequest.setStartDate(new Date("2016/07/14"));
		procedureRequest.setFinishDate(null);
		procedureRequest.setStatus("En proceso");

		logger.info("inserting new procedure request instance");

		collection.insertOne(procedureRequest.toDocument());

	}

	// ProcedureRequest6
	@SuppressWarnings("deprecation")
	public static <V> void addProcedureRequestSeis() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");

		ProcedureRequest procedureRequest = new ProcedureRequest();

		procedureRequest.setProcedureClassName("Certificado de Residencia");
		procedureRequest.setFileNumber("6");

		Citizen citizen = new Citizen();
		citizen.setEmail("jheison@uniandes");
		citizen.setIdentification(123456);
		citizen.setName("jheison");
		citizen.setLastName1("rodriguez");

		procedureRequest.setCitizen(citizen);
		procedureRequest.setMayoralty("anapoima");

		Document procedureData = new Document();
		procedureData.put("identificaci\u00F3n", 123456);
		procedureData.put("direcci\u00F3n", "calle 2 # 23 -23");
		procedureData.put("barrio", "barrio lat");
		procedureData.put("telefono", 55667733);
		procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");

		procedureRequest.setProcedureData(procedureData);

		Document deliveryDocs = new Document();
		deliveryDocs.put("Doc1", "estaEsLARutaAlDoc1");
		deliveryDocs.put("Doc2", "estaEsLARutaAlDoc2");

		procedureRequest.setDeliveryDocs(deliveryDocs);

		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Hacienda");
		activity1.setFunctionary("acalle@anapoima");
		activity1.setAprobacion("Finalizado");
		activity1.setStatus("En curso");

		activities.add(activity1);
		procedureRequest.setActivities(activities);
		// History
				ArrayList<History> histories = new ArrayList<History>();
				histories.add(new History(0, "2016/10/26", citizen.getEmail(), "Iniciar", "Se inicia tramite"));
				procedureRequest.setHistories(histories);
		procedureRequest.setStartDate(new Date("2016/07/14"));
		procedureRequest.setFinishDate(new Date("2016/08/14"));
		procedureRequest.setStatus("Finalizado");

		logger.info("inserting new procedure request instance");

		collection.insertOne(procedureRequest.toDocument());

	}

	// ProcedureRequest7
	@SuppressWarnings("deprecation")
	public static <V> void addProcedureRequestSiete() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");

		ProcedureRequest procedureRequest = new ProcedureRequest();

		procedureRequest.setProcedureClassName("Auxilio para Gastos Sepelio");
		procedureRequest.setFileNumber("7");

		Citizen citizen = new Citizen();
		citizen.setEmail("david@uniandes");
		citizen.setIdentification(123456);
		citizen.setName("david");
		citizen.setLastName1("martinez");

		procedureRequest.setCitizen(citizen);
		procedureRequest.setMayoralty("El Rosal");

		Document procedureData = new Document();
		procedureData.put("identificaci\u00F3n", 123456);
		procedureData.put("direcci\u00F3n", "calle 2 # 23 -23");
		procedureData.put("barrio", "barrio lat");
		procedureData.put("telefono", 55667733);
		procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");

		procedureRequest.setProcedureData(procedureData);

		Document deliveryDocs = new Document();
		deliveryDocs.put("Doc1", "estaEsLARutaAlDoc1");
		deliveryDocs.put("Doc2", "estaEsLARutaAlDoc2");
		deliveryDocs.put("Doc3", "estaEsLARutaAlDoc3");

		procedureRequest.setDeliveryDocs(deliveryDocs);

		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Hacienda");
		activity1.setFunctionary("jvaldez@elrosal");
		activity1.setAprobacion("Finalizado");
		activity1.setStatus("En curso");

		activities.add(activity1);
		procedureRequest.setActivities(activities);

		// History
				ArrayList<History> histories = new ArrayList<History>();
				histories.add(new History(0, "2016/10/26", citizen.getEmail(), "Iniciar", "Se inicia tramite"));
				procedureRequest.setHistories(histories);
		procedureRequest.setStartDate(new Date("2016/07/21"));
		procedureRequest.setFinishDate(new Date("2016/09/21"));
		procedureRequest.setStatus("Finalizado");

		logger.info("inserting new procedure request instance");

		collection.insertOne(procedureRequest.toDocument());

	}

	// ProcedureRequest8
	@SuppressWarnings("deprecation")
	public static <V> void addProcedureRequestOcho() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");

		ProcedureRequest procedureRequest = new ProcedureRequest();

		procedureRequest.setProcedureClassName("Auxilio para Gastos Sepelio");
		procedureRequest.setFileNumber("8");

		Citizen citizen = new Citizen();
		citizen.setEmail("david@uniandes");
		citizen.setIdentification(123456);
		citizen.setName("david");
		citizen.setLastName1("martinez");

		procedureRequest.setCitizen(citizen);
		procedureRequest.setMayoralty("El Rosal");

		Document procedureData = new Document();
		procedureData.put("identificaci\u00F3n", 123456);
		procedureData.put("direcci\u00F3n", "calle 10 # 10 - 10");
		procedureData.put("barrio", "barrio lat");
		procedureData.put("telefono", 55667733);
		procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");

		procedureRequest.setProcedureData(procedureData);

		Document deliveryDocs = new Document();
		deliveryDocs.put("Doc1", "estaEsLARutaAlDoc1");
		deliveryDocs.put("Doc2", "estaEsLARutaAlDoc2");
		deliveryDocs.put("Doc3", "estaEsLARutaAlDoc3");

		procedureRequest.setDeliveryDocs(deliveryDocs);

		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Atenci\u00F3n al ciudadano");
		activity1.setFunctionary("acalle@anapoima");
		activity1.setAprobacion("En proceso");
		activity1.setStatus("En curso");

		activities.add(activity1);
		// History
				ArrayList<History> histories = new ArrayList<History>();
				histories.add(new History(0, "2016/10/26", citizen.getEmail(), "Iniciar", "Se inicia tramite"));
				procedureRequest.setHistories(histories);
		procedureRequest.setActivities(activities);
		procedureRequest.setStartDate(new Date("2016/08/06"));
		procedureRequest.setFinishDate(null);
		procedureRequest.setStatus("En proceso");

		logger.info("inserting new procedure request instance");

		collection.insertOne(procedureRequest.toDocument());

	}


	// ProcedureRequest9
	//SCC
	@SuppressWarnings("deprecation")
	public static <V> void addProcedureRequestNueve() {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");

		ProcedureRequest procedureRequest = new ProcedureRequest();

		procedureRequest.setProcedureClassName("Solicitud De Contrataci\u00F3on Monitor Deportes");
		procedureRequest.setFileNumber("9");

		Citizen citizen = new Citizen();
		citizen.setEmail("sebastian@uniandes");
		citizen.setIdentification(123456);
		citizen.setName("Sebastian");
		citizen.setLastName1("Cardona");

		procedureRequest.setCitizen(citizen);
		procedureRequest.setMayoralty("El Rosal");

		Document procedureData = new Document();
		procedureData.put("identificaci\u00F3n", 123456);
		procedureData.put("direcci\u00F3n", "calle 10 # 10 - 10");
		procedureData.put("barrio", "barrio lat");
		procedureData.put("telefono", 55667733);
		procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");

		procedureRequest.setProcedureData(procedureData);

		Document deliveryDocs = new Document();
		deliveryDocs.put("Doc1", "estaEsLARutaAlDoc1");
		deliveryDocs.put("Doc2", "estaEsLARutaAlDoc2");
		deliveryDocs.put("Doc3", "estaEsLARutaAlDoc3");

		procedureRequest.setDeliveryDocs(deliveryDocs);

		ArrayList<Activity> activities = new ArrayList<Activity>();
		// Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobaci\u00F3n");
		activity1.setDescription("Revisar documentaci\u00F3n y aprobar");
		activity1.setDependency("Atenci\u00F3n al ciudadano");
		activity1.setFunctionary("acalle@anapoima");
		activity1.setAprobacion("En proceso");
		activity1.setStatus("En curso");

		activities.add(activity1);
		// History
				ArrayList<History> histories = new ArrayList<History>();
				histories.add(new History(0, "2016/10/26", citizen.getEmail(), "Iniciar", "Se inicia tramite"));
				procedureRequest.setHistories(histories);
		procedureRequest.setActivities(activities);
		procedureRequest.setStartDate(new Date("2016/08/06"));
		procedureRequest.setFinishDate(null);
		procedureRequest.setStatus("En proceso");

		logger.info("inserting new procedure request instance");

		collection.insertOne(procedureRequest.toDocument());

	}

	public static void addProcedureRequest() {
		ProcedureRequest procedure = new ProcedureRequest();

		procedure.setProcedureClassName("Certificado de residencia");

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
		logger.info("JETTY SERVER PORT: " + port);
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
			logger.info("removeTestFile> failure");
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
				/*
				 * File directoryName = new File(value + "/junittest"); if
				 * (!directoryName.exists()) {
				 * logger.info("creating directory: " + directoryName); boolean
				 * result = false; try { directoryName.mkdir(); result = true; }
				 * catch (SecurityException se) {
				 * System.out.println(se.getLocalizedMessage()); } if (result) {
				 * tmpPath = directoryName.toString();
				 * logger.info("LOCALTMP + /junittest created"); } } else {
				 * tmpPath = directoryName.toString(); }
				 */
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
