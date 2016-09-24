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
		TestsUtil.addProcedure();
	}

}
