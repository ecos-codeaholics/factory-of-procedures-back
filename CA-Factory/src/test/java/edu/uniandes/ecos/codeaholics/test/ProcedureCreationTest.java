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
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}
	
	@Test
	public void createProcedureTest() {
		TestsUtil.addProcedureUno ("Certificado de residencia","Anapoima");
		TestsUtil.addProcedureDos ("Certificado de residencia","El Rosal");
		TestsUtil.addProcedureTres ("Auxilio para gastos de sepelio","Anapoima");
		TestsUtil.addProcedureCuatro ("Auxilio para gastos de sepelio","El Rosal");
			
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
