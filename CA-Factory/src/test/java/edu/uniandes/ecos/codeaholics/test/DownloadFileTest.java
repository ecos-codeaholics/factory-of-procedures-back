package edu.uniandes.ecos.codeaholics.test;

import static spark.Spark.get;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import edu.uniandes.ecos.codeaholics.business.CitizenServices;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.main.App;
import spark.Spark;

public class DownloadFileTest {

	Logger logger = LogManager.getRootLogger();
	String filePath = "";

	@BeforeClass
	public static void beforeClass() {
		App.main(null);
		get("/procedures/documents/list",  CitizenServices::listDocuments, GeneralUtil.json());		
		get("/citizens/documents/download",  CitizenServices::downloadDocuments);
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}
	
	@Test
	public void downloadTest() {
		
	}
	

}
