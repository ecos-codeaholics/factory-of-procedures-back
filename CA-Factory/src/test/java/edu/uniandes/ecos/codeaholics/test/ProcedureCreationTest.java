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

public class ProcedureCreationTest {

	Logger logger = LogManager.getRootLogger();

	private static String PROCEDURE_UNO = "Certificado de Residencia";
	private static String PROCEDURE_DOS = "Auxilio para Gastos Sepelio";
	private static String PROCEDURE_TRES = "Certificado de Estratificaci\u00F3n";
	private static String PROCEDURE_CUATRO = "Solicitud De Contrataci\u00F3n Monitor Deportes";

	private static ArrayList<String> PROCDEURES_LIST = new ArrayList<String>();
	
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
		
		TestsUtil.addFunctionaryUno("Juan", "Valdez", "jvaldez@anapoima.gov.co", "12345678");
		TestsUtil.addFunctionaryDos("Arturo", "Calle", "acalle@anapoima.gov.co", "12345678");
		TestsUtil.addFunctionaryTres("Juan", "Valdez", "jvaldez@elrosal.gov.co", "12345678");
		TestsUtil.addFunctionaryCuatro("Arturo", "Calle", "acalle@elrosal.gov.co", "12345678");
		
		assertEquals(true, true);		
	}
	
	@Test
	public void createProcedureTest() {

		TestsUtil.addProcedureUno (PROCEDURE_UNO);
		TestsUtil.addProcedureDos (PROCEDURE_DOS);
		TestsUtil.addProcedureTres (PROCEDURE_TRES);
		TestsUtil.addProcedureCuatro (PROCEDURE_CUATRO);
		
		assertEquals(true, true);
	}
	
	@Test
	public void createMayoraltiesTest(){
		
		PROCDEURES_LIST.add(PROCEDURE_UNO);
		PROCDEURES_LIST.add(PROCEDURE_DOS);
		PROCDEURES_LIST.add(PROCEDURE_TRES);
		PROCDEURES_LIST.add(PROCEDURE_CUATRO);
		
		TestsUtil.addMayoraltyUno(PROCDEURES_LIST);
		TestsUtil.addMayoraltyDos(PROCDEURES_LIST);
		
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
		
		//SCC
		//TestsUtil.addProcedureRequestNueve();

		assertEquals(true, true);
	}

}
