package edu.uniandes.ecos.codeaholics.main;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.threadPool;
import static spark.Spark.post;
import static spark.Spark.secure;
import static spark.Spark.staticFiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import edu.uniandes.ecos.codeaholics.business.CitizenServices;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;

/**
 * Created by snaphuman on 6/6/16.
 */
public class App {

	public static final String CONFIG_FILE = "src/main/resources/config.properties";
	public static int JETTY_SERVER_PORT = 4567;
	public static int JETTY_SERVER_MAXTHREADS = 50;
	public static int JETTY_SERVER_MINTHREADS = 1000;
	public static int JETTY_SERVER_TIMEOUTMILLIS = 30000;
	public static boolean USE_SPARK_HTTPS = false;

	/***
	 * Metodo principal del sistema.
	 *
	 * @param args
	 *            argunmentos
	 */
	public static void main(String[] args) {

		getConfig(CONFIG_FILE);
		port(JETTY_SERVER_PORT);
		threadPool(JETTY_SERVER_MAXTHREADS, JETTY_SERVER_MINTHREADS, JETTY_SERVER_TIMEOUTMILLIS);
			
		/* HTTPS option : JLRM */
		if( USE_SPARK_HTTPS ) {
			secure("deploy/keystore.jks", "codeaholics", null, null);
		}
		
		//
		
		// Initialize Database Connection
		DatabaseSingleton.getInstance();
		
		staticFiles.location("/public");
		
		 //Rutas Ciudadano
        // Deberiamos cambiar las rutas por citizens
        // Para el versionamiento de la API debe comenzar con /api/v1
        
        
        //cambiar esta por /citizens/{info citizens} metodo POST
        post("/create", 
        		CitizenServices::insertCitizen, GeneralUtil.json());
        
        //cambiar esta por /citizens/sessions/{login info}      metodo POST  
        post("/citizenLogin", CitizenServices::doLogin, GeneralUtil.json());
        
        //cambiar por /citizens metodo GET
        get("/citizen/citizenList", CitizenServices::getCitizenList, GeneralUtil.json());
        
        //cambiar /citizens/{id}        metodo GET
        post("/citizen/getCitizen", CitizenServices::getCitizenDetail, GeneralUtil.json());
        
        //cambiar /citizens/sessions/{session id} metodo DELETE
        get("/citizen/closeSession", CitizenServices::closeSession, GeneralUtil.json());
        
        //	/citizens/{id}/procedures/{procedure info} metodo POST, opciones de filtro
        post("/citizens/:id/procedures/", CitizenServices::startProcedure, GeneralUtil.json());
        
        
        //	/citizens/{id}/procedures/ metodo GET, opciones de filtro
        get("/citizens/:id/procedures/", CitizenServices::consultProcedures, GeneralUtil.json());
        
        //	/citizens/{id}/procedures/{id} metodo GET
        get("/citizens/:id/procedures/:idP", CitizenServices::consultProceduresById, GeneralUtil.json());
              

        //Rutas Alcaldia
        
        //test


		before("/citizen/*", Authorization::authorizeCitizen);

		/**
		 * Enable CORS in Spark Java to allow origins *
		 * 
		 * @see https://gist.github.com/saeidzebardast/e375b7d17be3e0f4dddf
		 */
		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

	}
	
	private static void getConfig( String pConfig ) {
		
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream( pConfig );
			prop.load(input);
			JETTY_SERVER_PORT = Integer.parseInt( prop.getProperty("jetty.server.port") );
			JETTY_SERVER_MAXTHREADS = Integer.parseInt( prop.getProperty("jetty.server.minthreads") );
			JETTY_SERVER_MINTHREADS = Integer.parseInt( prop.getProperty("jetty.server.maxthreads") );
			JETTY_SERVER_TIMEOUTMILLIS = Integer.parseInt( prop.getProperty("jetty.server.timeoutMillis") );
			USE_SPARK_HTTPS = Boolean.parseBoolean( prop.getProperty("spark.https") );
		} catch (Exception e) {

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
