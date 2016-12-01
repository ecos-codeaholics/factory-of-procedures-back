package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

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
 * Class: ProcedureCreationTest ProcedureCreationTest.java
 * 
 * Original Author: @author JHEISON, SCC, AOSORIO
 * 
 * Description: This test class builds procedures and writes them in the DB
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Nov 19, 2016 9:34:40 PM
 * 
 */
public class ProcedureCreationTest {

	Logger logger = LogManager.getRootLogger();
	
	private static String PROCEDURE_1 = "Certificado de Residencia";
	private static String PROCEDURE_2 = "Certificado de Estratificaci\u00F3n";
	private static String PROCEDURE_3 = "Auxilio para Gastos Sepelio";
	private static String PROCEDURE_4 = "Solicitud De Contrataci\u00F3n Monitor Deportes";

	private static ArrayList<String> PROCDEURES_LIST_1 = new ArrayList<String>();
	private static ArrayList<String> PROCDEURES_LIST_2 = new ArrayList<String>();

	
	@BeforeClass
	public static void beforeClass() {
		App.main(null);
		TestsUtil.clearAllCollections();
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}
		
	@Test
	public void createCitizenTest(){
		
		TestsUtil.addCitizenUno();
		TestsUtil.addCitizenDos();
		TestsUtil.addCitizenTres();
		TestsUtil.addCitizenCuatro();
		TestsUtil.addCitizenCinco();
		
		assertEquals(true, true);		
	}
	
	@Test
	public void createFunctionariesTest() {
		
		//Functionary "uno" es admin
		TestsUtil.addFunctionaryUno("Juan", "Valdez", "Velez", "jvaldez@anapoima.gov.co", "12345678");
		TestsUtil.addFunctionaryDos("Arturo", "Calle", "Nieto", "acalle@anapoima.gov.co", "12345678");
		//Functionary "tres" es admin
		TestsUtil.addFunctionaryTres("Juan", "Valdez", "Velez", "jvaldez@elrosal.gov.co", "12345678");
		TestsUtil.addFunctionaryCuatro("Arturo", "Calle", "Nieto", "acalle@elrosal.gov.co", "12345678");
		
		assertEquals(true, true);		
	}
	
	@Test
	public void createProcedureTest() {

		TestsUtil.addProcedureUno( "T11335", PROCEDURE_1,"Anapoima","acalle@anapoima.gov.co");
		TestsUtil.addProcedureUno( "T11336", PROCEDURE_1,"El Rosal","acalle@elrosal.gov.co");
		
		TestsUtil.addProcedureDos( "T22335", PROCEDURE_2,"Anapoima","acalle@anapoima.gov.co");
		TestsUtil.addProcedureDos( "T22336", PROCEDURE_2,"El Rosal","acalle@elrosal.gov.co");
		
		TestsUtil.addProcedureTres( "T33445", PROCEDURE_3,"Anapoima","acalle@anapoima.gov.co");
		TestsUtil.addProcedureTres( "T33446", PROCEDURE_3,"El Rosal","acalle@elrosal.gov.co");
		
		//TestsUtil.addProcedureCuatro( "T99115", PROCEDURE_4,"Anapoima");
				
		assertEquals(true, true);
	}
	
	@Test
	public void createMayoraltiesTest(){
		
		//Test procedures associated to Anapoima
		PROCDEURES_LIST_1.add(PROCEDURE_1);
		PROCDEURES_LIST_1.add(PROCEDURE_2);
		PROCDEURES_LIST_1.add(PROCEDURE_3);
		PROCDEURES_LIST_1.add(PROCEDURE_4);
		
		//Test procedures associated to El Rosal
		PROCDEURES_LIST_2.add(PROCEDURE_1);
		PROCDEURES_LIST_2.add(PROCEDURE_2);
		PROCDEURES_LIST_2.add(PROCEDURE_3);
		
		TestsUtil.addMayoraltyUno(PROCDEURES_LIST_1);
		TestsUtil.addMayoraltyDos(PROCDEURES_LIST_2);
		
		assertEquals(true, true);		
	}
	
	@Test
	public void createProcedureRequestTest() {
		
		TestsUtil.addProcedureRequestUno();
		TestsUtil.addProcedureRequestDos();
		TestsUtil.addProcedureRequestTres();
		TestsUtil.addProcedureRequestCuatro();
		TestsUtil.addProcedureRequestCinco();
		TestsUtil.addProcedureRequestSeis();
		TestsUtil.addProcedureRequestSiete();
		TestsUtil.addProcedureRequestOcho();

		assertEquals(true, true);
	}

}
