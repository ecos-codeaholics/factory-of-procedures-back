package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uniandes.ecos.codeaholics.main.App;
import spark.Spark;

public class ProcedureCreationTest {

	Logger logger = LogManager.getRootLogger();

	@BeforeClass
	public static void beforeClass() {
		App.main(null);
		TestsUtil.clearAllCollections();
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}
	
	//@Test 
	//public void clearDB () {
	//	TestsUtil.clearAllCollections();		
	//	assertEquals(true, true);
	//}
	
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
		
		TestsUtil.addFunctionaryUno("Juan", "Valdez", "jvaldez@anapoima", "12345678");
		TestsUtil.addFunctionaryDos("Arturo", "Calle", "acalle@anapoima", "12345678");
		TestsUtil.addFunctionaryTres("Juan", "Valdez", "jvaldez@elrosal", "12345678");
		TestsUtil.addFunctionaryCuatro("Arturo", "Calle", "acalle@elrosal", "12345678");
		
		assertEquals(true, true);		
	}
	
	@Test
	public void createProcedureTest() {
		TestsUtil.addProcedureUno ("Certificado de Residencia");
		TestsUtil.addProcedureDos ("Auxilio para Gastos Sepelio");
		TestsUtil.addProcedureTres ("Certificado de Estratificaci\u00f3n");
		//SCC
		TestsUtil.addProcedureCuatro ("Solicitud De Contratacion Monitor Deportes");	
		assertEquals(true, true);
	}
	
	@Test
	public void createMayoraltiesTest(){
		
		TestsUtil.addMayoraltyUno();
		TestsUtil.addMayoraltyDos();
		
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
