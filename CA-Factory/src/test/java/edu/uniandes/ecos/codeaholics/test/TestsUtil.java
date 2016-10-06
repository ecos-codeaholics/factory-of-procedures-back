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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
//import edu.uniandes.ecos.codeaholics.persistence.Dependency;
//import edu.uniandes.ecos.codeaholics.persistence.FieldAttribute;
//import edu.uniandes.ecos.codeaholics.persistence.FieldOptions;
//import edu.uniandes.ecos.codeaholics.persistence.FieldValidation;
import edu.uniandes.ecos.codeaholics.persistence.FormField;
//import edu.uniandes.ecos.codeaholics.persistence.FormField.Type;
import edu.uniandes.ecos.codeaholics.persistence.Functionary;
import edu.uniandes.ecos.codeaholics.persistence.Mayoralty;
import edu.uniandes.ecos.codeaholics.persistence.Procedure;
import edu.uniandes.ecos.codeaholics.persistence.ProcedureRequest;
import edu.uniandes.ecos.codeaholics.persistence.RequiredUpload;

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
	
	

//Procedure1
	public static void addProcedureUno(String pName, String pMayorName) {

		MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
		MongoCollection<Document> collection = dbOne.getCollection("procedures");

//		ArrayList<Functionary> listOfFunctionaries = new ArrayList<Functionary>();
		ArrayList<FormField> formFields = new ArrayList<FormField>();
		ArrayList<RequiredUpload> reqDocs = new ArrayList<RequiredUpload>();
		ArrayList<Activity> activities = new ArrayList<Activity>();

		Procedure procedure = new Procedure();
		procedure.setName(pName);
		procedure.setMayoralty(pMayorName);
		

		//Activities
		Activity activity1 = new Activity();
		activity1.setStep(1);
		activity1.setName("Aprobacion");
		activity1.setDescription("Revisar documentacion y aprobar");
		activity1.setDependency("Hacienda");
		
		activities.add(activity1);
		
		
		procedure.setActivities(activities);

		//Required
		RequiredUpload reqDoc1 = new RequiredUpload();
		
		reqDoc1.setType("file");
		reqDoc1.setRequired(true);
		reqDoc1.setClassName("form-control");
		
		reqDoc1.setLabel("Cedula de Ciudadania");
		reqDoc1.setDescription("Adjunte su cedula en formato (png, jpeg)");
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
		
		

//		
//		Dependency dependency = new Dependency();
//		dependency.setName("Secretaria de Hacienda");
//		dependency.setExtension("EXT12345");
//
//		Mayoralty mayorality = new Mayoralty();
//		mayorality.setName(pMayorName);
//		mayorality.setAddress("CRA 123 45 1");
//		mayorality.setUrl("https://anapoima.gov.co");
//		mayorality.setPhone("333555888");
//
//		Functionary functionary = new Functionary();
//		functionary.setName("Juan");
//		functionary.setLastName1("Valdes");
//		functionary.setIdentification(1234567890);
//		functionary.setEmail("jvaldes@anapoima.gov");
//		functionary.setUserProfile("functionary");
//		functionary.setMayoralty("Lenguazaque");
//		functionary.setPosition("Secretario de Gobierno");
//
//		listOfFunctionaries.add(functionary);
//		dependency.setFunctionaries(listOfFunctionaries);
//		activity.setDependency(dependency);

		//Form
		
		FormField field1 = new FormField();
		
		field1.setType("text");
		field1.setSubtype("tel");
		field1.setRequired(true);
		field1.setLabel("identificacion");
		field1.setDescription("numero de documento de identidad");
		field1.setPlaceHolder("123456789");
		field1.setClassname("form-control");
		field1.setName("identification");
		field1.setMaxlenght(11);
		
		formFields.add(field1);
		
		FormField field2 = new FormField();
		
		field2.setType("text");
		field2.setSubtype("text");
		field2.setRequired(true);
		field2.setLabel("direccion");
		field2.setDescription("direccion de residencia");
		field2.setPlaceHolder("CAlle -- # -- --");
		field2.setClassname("form-control");
		field2.setName("direccion");
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
		field4.setDescription("numero telefonico de contacto");
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
		field5.setPlaceHolder("Por favor diligencia su peticion detalladamente");
		field5.setClassname("form-control");
		field5.setName("carta");
		field5.setMaxlenght(5000);
		
		formFields.add(field5);
		
		procedure.setFields(formFields);
		
		logger.info("inserting new procedure instance");
		
		System.out.println(procedure.getFields());
		collection.insertOne(procedure.toDocument());

	}
	
	
	//Procedure2
		public static void addProcedureDos(String pName, String pMayorName) {

			MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
			MongoCollection<Document> collection = dbOne.getCollection("procedures");

//			ArrayList<Functionary> listOfFunctionaries = new ArrayList<Functionary>();
			ArrayList<FormField> formFields = new ArrayList<FormField>();
			ArrayList<RequiredUpload> reqDocs = new ArrayList<RequiredUpload>();
			ArrayList<Activity> activities = new ArrayList<Activity>();

			Procedure procedure = new Procedure();
			procedure.setName(pName);
			procedure.setMayoralty(pMayorName);

			//Activities
			Activity activity1 = new Activity();
			activity1.setStep(1);
			activity1.setName("Aprobacion");
			activity1.setDescription("Revisar documentacion y aprobar");
			activity1.setDependency("Atencion al ciudadano");
			activity1.setFunctionary("juanvaldez@elrosal");
			
			activities.add(activity1);
			
			
			procedure.setActivities(activities);

			//Required
			RequiredUpload reqDoc1 = new RequiredUpload();
			
			reqDoc1.setType("file");
			reqDoc1.setRequired(true);
			reqDoc1.setClassName("form-control");
			
			reqDoc1.setLabel("Cedula de Ciudadania");
			reqDoc1.setDescription("Adjunte su cedula en formato (png, jpeg)");
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
			

			//Form
			
			FormField field1 = new FormField();
			
			field1.setType("text");
			field1.setSubtype("tel");
			field1.setRequired(true);
			field1.setLabel("identificacion");
			field1.setDescription("numero de documento de identidad");
			field1.setPlaceHolder("123456789");
			field1.setClassname("form-control");
			field1.setName("identification");
			field1.setMaxlenght(11);
			
			formFields.add(field1);
			
			FormField field2 = new FormField();
			
			field2.setType("text");
			field2.setSubtype("text");
			field2.setRequired(true);
			field2.setLabel("direccion");
			field2.setDescription("direccion de residencia");
			field2.setPlaceHolder("CAlle -- # -- --");
			field2.setClassname("form-control");
			field2.setName("direccion");
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
			field4.setDescription("numero telefonico de contacto");
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
			field5.setPlaceHolder("Por favor diligencia su peticion detalladamente");
			field5.setClassname("form-control");
			field5.setName("carta");
			field5.setMaxlenght(5000);
			
			formFields.add(field5);
			
			procedure.setFields(formFields);
			
			logger.info("inserting new procedure instance");
			
			System.out.println(procedure.getFields());
			collection.insertOne(procedure.toDocument());

		}


		//Procedure3
		public static void addProcedureTres(String pName, String pMayorName) {

			MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
			MongoCollection<Document> collection = dbOne.getCollection("procedures");

//			ArrayList<Functionary> listOfFunctionaries = new ArrayList<Functionary>();
			ArrayList<FormField> formFields = new ArrayList<FormField>();
			ArrayList<RequiredUpload> reqDocs = new ArrayList<RequiredUpload>();
			ArrayList<Activity> activities = new ArrayList<Activity>();

			Procedure procedure = new Procedure();
			procedure.setName(pName);
			procedure.setMayoralty(pMayorName);

			//Activities
			Activity activity1 = new Activity();
			activity1.setStep(1);
			activity1.setName("Aprobacion");
			activity1.setDescription("Secretaria");
			activity1.setDependency("Atencion al ciudadano");
			activity1.setFunctionary("valentinaperez@anapoima");
			
			activities.add(activity1);
			
			
			procedure.setActivities(activities);

			//Required
			RequiredUpload reqDoc1 = new RequiredUpload();
			
			reqDoc1.setType("file");
			reqDoc1.setRequired(true);
			reqDoc1.setClassName("form-control");
			
			reqDoc1.setLabel("Cedula de Ciudadania del solicitante");
			reqDoc1.setDescription("Adjunte su cedula en formato (png, jpeg)");
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
			
			reqDoc3.setLabel("Cedula del fallecido");
			reqDoc3.setDescription("Adjunte la cedula del fallecido (png, jpeg)");
			reqDoc3.setName("cedulaFallecidoAtt");
			
			reqDocs.add(reqDoc3);
			
			RequiredUpload reqDoc4 = new RequiredUpload();
			
			reqDoc4.setType("file");
			reqDoc4.setRequired(true);
			reqDoc4.setClassName("form-control");
			
			reqDoc4.setLabel("Certificado de defuncion");
			reqDoc4.setDescription("Adjunte el certificado de defuncion (png, jpeg)");
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
			

			//Form
			
			FormField field1 = new FormField();
			
			field1.setType("text");
			field1.setSubtype("tel");
			field1.setRequired(true);
			field1.setLabel("identificacion");
			field1.setDescription("numero de documento de identidad");
			field1.setPlaceHolder("123456789");
			field1.setClassname("form-control");
			field1.setName("identification");
			field1.setMaxlenght(11);
			
			formFields.add(field1);
			
			FormField field2 = new FormField();
			
			field2.setType("text");
			field2.setSubtype("text");
			field2.setRequired(true);
			field2.setLabel("direccion");
			field2.setDescription("direccion de residencia");
			field2.setPlaceHolder("CAlle -- # -- --");
			field2.setClassname("form-control");
			field2.setName("direccion");
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
			field4.setDescription("numero telefonico de contacto");
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
			field5.setPlaceHolder("Por favor diligencia su peticion detalladamente");
			field5.setClassname("form-control");
			field5.setName("carta");
			field5.setMaxlenght(5000);
			
			formFields.add(field5);
			
			procedure.setFields(formFields);
			
			logger.info("inserting new procedure instance");
			
			System.out.println(procedure.getFields());
			collection.insertOne(procedure.toDocument());

		}

		//Procedure4
		public static void addProcedureCuatro(String pName, String pMayorName) {

			MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
			MongoCollection<Document> collection = dbOne.getCollection("procedures");

//			ArrayList<Functionary> listOfFunctionaries = new ArrayList<Functionary>();
			ArrayList<FormField> formFields = new ArrayList<FormField>();
			ArrayList<RequiredUpload> reqDocs = new ArrayList<RequiredUpload>();
			ArrayList<Activity> activities = new ArrayList<Activity>();

			Procedure procedure = new Procedure();
			procedure.setName(pName);
			procedure.setMayoralty(pMayorName);

			//Activities
			Activity activity1 = new Activity();
			activity1.setStep(1);
			activity1.setName("Aprobacion");
			activity1.setDescription("Revisar documentacion y aprobar");
			activity1.setDependency("Secretaria");
			activity1.setFunctionary("valentinaperez@elrosal");
			
			activities.add(activity1);
			
			
			procedure.setActivities(activities);

			//Required
			RequiredUpload reqDoc1 = new RequiredUpload();
			
			reqDoc1.setType("file");
			reqDoc1.setRequired(true);
			reqDoc1.setClassName("form-control");
			
			reqDoc1.setLabel("Comprobante de pago");
			reqDoc1.setDescription("Adjunte su comprobante de pago (png, jpeg)");
			reqDoc1.setName("cedulaAtt");
			
		

			reqDocs.add(reqDoc1);
			
			RequiredUpload reqDoc2 = new RequiredUpload();
			
			reqDoc2.setType("file");
			reqDoc2.setRequired(true);
			reqDoc2.setClassName("form-control");
			
			reqDoc2.setLabel("CErtificado de Defuncion");
			reqDoc2.setDescription("Adjunte su certificado de defuncion en formato (png, jpeg)");
			reqDoc2.setName("certificadoDefuncionAtt");
			
			reqDocs.add(reqDoc2);
			
			procedure.setRequired(reqDocs);
			

			//Form
			
			FormField field1 = new FormField();
			
			field1.setType("text");
			field1.setSubtype("tel");
			field1.setRequired(true);
			field1.setLabel("identificacion");
			field1.setDescription("numero de documento de identidad");
			field1.setPlaceHolder("123456789");
			field1.setClassname("form-control");
			field1.setName("identification");
			field1.setMaxlenght(11);
			
			formFields.add(field1);
			
			FormField field2 = new FormField();
			
			field2.setType("text");
			field2.setSubtype("text");
			field2.setRequired(true);
			field2.setLabel("direccion");
			field2.setDescription("direccion de residencia");
			field2.setPlaceHolder("CAlle -- # -- --");
			field2.setClassname("form-control");
			field2.setName("direccion");
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
			field4.setDescription("numero telefonico de contacto");
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
			field5.setPlaceHolder("Por favor diligencia su peticion detalladamente");
			field5.setClassname("form-control");
			field5.setName("carta");
			field5.setMaxlenght(5000);
			
			formFields.add(field5);
			
			procedure.setFields(formFields);
			
			logger.info("inserting new procedure instance");
			
			System.out.println(procedure.getFields());
			collection.insertOne(procedure.toDocument());

		}

		
		//ProcedureRequest1
		@SuppressWarnings("deprecation")
		public static <V> void addProcedureRequestUno() {

			MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
			MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");
			
			ProcedureRequest procedureRequest = new ProcedureRequest();
			
			procedureRequest.setProcedureClass("Certificado de Residencia");
			procedureRequest.setFileNumber(00001L);
			
			Citizen citizen = new Citizen();
			citizen.setEmail("campeche1@uniandes");
			citizen.setIdentification(123456);
			citizen.setName("campeche");
			citizen.setLastName1("suche");
						
			procedureRequest.setCitizen(citizen);
			procedureRequest.setMayoralty("anapoima");			
			
			Map<String, Object> procedureData = new HashMap<>();
			procedureData.put("identificacion", 123456);
			procedureData.put("direccion", "calle 1 # 12 -12");
			procedureData.put("barrio", "barrio Tal");
			procedureData.put("telefono", 55667733);
			procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");
			
			procedureRequest.setProcedureData(new Document(procedureData));
			
			ArrayList<String> deliveryDocs = new ArrayList<>();
			deliveryDocs.add("estaEsLARutaAlDoc1");
			deliveryDocs.add("estaEsLARutaAlDoc2");
			
			Map<String, Object> steps = new HashMap<>();
			steps.put("aprobacion", "En proceso");
			procedureRequest.setSteps(new Document(steps));
			
			procedureRequest.setDeliveryDocs(deliveryDocs);
			
			procedureRequest.setStartDate(new Date("2016/07/14"));
			procedureRequest.setFinishDate(null);
			procedureRequest.setStatus("En proceso");
			
			logger.info("inserting new procedure request instance");
						
			collection.insertOne(procedureRequest.toDocument());

		}

		//ProcedureRequest2
		@SuppressWarnings("deprecation")
		public static <V> void addProcedureRequestDos() {

			MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
			MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");
			
			ProcedureRequest procedureRequest = new ProcedureRequest();
			
			procedureRequest.setProcedureClass("Certificado de Residencia");
			procedureRequest.setFileNumber(00002L);
			
			Citizen citizen = new Citizen();
			citizen.setEmail("zotero1@uniandes");
			citizen.setIdentification(123456);
			citizen.setName("jorge");
			citizen.setLastName1("zotero");
						
			procedureRequest.setCitizen(citizen);
			procedureRequest.setMayoralty("anapoima");			
			
			Map<String, Object> procedureData = new HashMap<>();
			procedureData.put("identificacion", 123456);
			procedureData.put("direccion", "calle 2 # 23 -23");
			procedureData.put("barrio", "barrio lat");
			procedureData.put("telefono", 55667733);
			procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");
			
			procedureRequest.setProcedureData(new Document(procedureData));
			
			ArrayList<String> deliveryDocs = new ArrayList<>();
			deliveryDocs.add("estaEsLARutaAlDoc1");
			deliveryDocs.add("estaEsLARutaAlDoc2");
			
			Map<String, Object> steps = new HashMap<>();
			steps.put("aprobacion", "Finalizado");
			procedureRequest.setSteps(new Document(steps));
			
			procedureRequest.setDeliveryDocs(deliveryDocs);
			
			procedureRequest.setStartDate(new Date("2016/07/14"));
			procedureRequest.setFinishDate(new Date("2016/08/14"));
			procedureRequest.setStatus("Finalizado");
			
			logger.info("inserting new procedure request instance");
						
			collection.insertOne(procedureRequest.toDocument());

		}
		
		//ProcedureRequest3
		@SuppressWarnings("deprecation")
		public static <V> void addProcedureRequestTres() {

			MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
			MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");
			
			ProcedureRequest procedureRequest = new ProcedureRequest();
			
			procedureRequest.setProcedureClass("Auxilio para gastos de sepelio");
			procedureRequest.setFileNumber(00003L);
			
			Citizen citizen = new Citizen();
			citizen.setEmail("calarga1@uniandes");
			citizen.setIdentification(123456);
			citizen.setName("pedro");
			citizen.setLastName1("calarga");
						
			procedureRequest.setCitizen(citizen);
			procedureRequest.setMayoralty("El Rosal");			
			
			Map<String, Object> procedureData = new HashMap<>();
			procedureData.put("identificacion", 123456);
			procedureData.put("direccion", "calle 2 # 23 -23");
			procedureData.put("barrio", "barrio lat");
			procedureData.put("telefono", 55667733);
			procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");
			
			procedureRequest.setProcedureData(new Document(procedureData));
			
			ArrayList<String> deliveryDocs = new ArrayList<>();
			deliveryDocs.add("estaEsLARutaAlDoc1");
			deliveryDocs.add("estaEsLARutaAlDoc2");
			deliveryDocs.add("estaEsLARutaAlDoc2");
			
			Map<String, Object> steps = new HashMap<>();
			steps.put("aprobacion", "Finalizado");
			procedureRequest.setSteps(new Document(steps));
			
			procedureRequest.setDeliveryDocs(deliveryDocs);
			
			procedureRequest.setStartDate(new Date("2016/07/21"));
			procedureRequest.setFinishDate(new Date("2016/09/21"));
			procedureRequest.setStatus("Finalizado");
			
			logger.info("inserting new procedure request instance");
						
			collection.insertOne(procedureRequest.toDocument());

		}

		
		//ProcedureRequest2
		@SuppressWarnings("deprecation")
		public static <V> void addProcedureRequestCuatro() {

			MongoDatabase dbOne = DatabaseSingleton.getInstance().getDatabase();
			MongoCollection<Document> collection = dbOne.getCollection("proceduresRequest");
			
			ProcedureRequest procedureRequest = new ProcedureRequest();
			
			procedureRequest.setProcedureClass("Auxilio para gastos de sepelio");
			procedureRequest.setFileNumber(00004L);
			
			Citizen citizen = new Citizen();
			citizen.setEmail("talero@uniandes");
			citizen.setIdentification(123456);
			citizen.setName("david");
			citizen.setLastName1("talero");
						
			procedureRequest.setCitizen(citizen);
			procedureRequest.setMayoralty("El Rosal");			
			
			Map<String, Object> procedureData = new HashMap<>();
			procedureData.put("identificacion", 123456);
			procedureData.put("direccion", "calle 10 # 10 - 10");
			procedureData.put("barrio", "barrio lat");
			procedureData.put("telefono", 55667733);
			procedureData.put("carta de Solicitud", "Solicito amablemente un certificado de residencia");
			
			procedureRequest.setProcedureData(new Document(procedureData));
			
			ArrayList<String> deliveryDocs = new ArrayList<>();
			deliveryDocs.add("estaEsLARutaAlDoc1");
			deliveryDocs.add("estaEsLARutaAlDoc2");
			deliveryDocs.add("estaEsLARutaAlDoc2");
			
			Map<String, Object> steps = new HashMap<>();
			steps.put("aprobacion", "En Proceso");
			procedureRequest.setSteps(new Document(steps));
			
			procedureRequest.setDeliveryDocs(deliveryDocs);
			
			procedureRequest.setStartDate(new Date("2016/08/06"));
			procedureRequest.setFinishDate(null);
			procedureRequest.setStatus("En Proceso");
			
			logger.info("inserting new procedure request instance");
						
			collection.insertOne(procedureRequest.toDocument());

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
