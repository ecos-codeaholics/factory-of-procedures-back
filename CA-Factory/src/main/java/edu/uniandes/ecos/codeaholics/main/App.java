package edu.uniandes.ecos.codeaholics.main;

import static spark.Spark.before;

import static spark.Spark.get;
import static spark.Spark.delete;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.threadPool;
import static spark.Spark.post;
import static spark.Spark.secure;
import static spark.Spark.after;
import static spark.Spark.put;
import static spark.Spark.staticFiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import edu.uniandes.ecos.codeaholics.business.CitizenServices;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.config.Routes;

/**
 * Created by snaphuman on 6/6/16.
 */
public class App {

	public static final String CONFIG_FILE       = "src/main/resources/config.properties";
	public static int JETTY_SERVER_PORT          = 4567;
	public static int JETTY_SERVER_MAXTHREADS    = 50;
	public static int JETTY_SERVER_MINTHREADS    = 1000;
	public static int JETTY_SERVER_TIMEOUTMILLIS = 30000;
	public static boolean USE_SPARK_HTTPS        = false;

	/***
	 * Metodo principal del sistema.
	 *
	 * @param args
	 *            argumentos
	 */
	public static void main(String[] args) {

		getConfig(CONFIG_FILE);
		port(JETTY_SERVER_PORT);
		threadPool(JETTY_SERVER_MAXTHREADS, JETTY_SERVER_MINTHREADS, JETTY_SERVER_TIMEOUTMILLIS);

		/* HTTPS option : JLRM */
		if (USE_SPARK_HTTPS) {
			secure("deploy/keystore.jks", "codeaholics", null, null);
		}

		// Initialize Database Connection
		DatabaseSingleton.getInstance();

		staticFiles.location("/public");

		//... Rutas Ciudadano

		// crear ciudadano /CITIZENS/ metodo POST {citizen info json}
		post(Routes.CITIZENS, CitizenServices::insertCitizen, GeneralUtil.json());

		// obtener lista de ciudadanos /CITIZENS/ metodo GET
		get(Routes.CITIZENS, CitizenServices::getCitizenList, GeneralUtil.json());

	    //obtener detalles de un ciudadano /CITIZENS/{id} --> template metodo GET
        get(Routes.CITIZENS+":identification", CitizenServices::getCitizenDetail, GeneralUtil.json());

		// iniciar sesion /SESSIONS/   metodo POST {info login json}
		post(Routes.SESSIONS, CitizenServices::doLogin, GeneralUtil.json());

		// cerrar sesion /SESSIONS/  metodo DELETE {session info json}
		delete(Routes.SESSIONS+":email", CitizenServices::closeSession, GeneralUtil.json());

		

		//... Rutas Alcaldia
		// TODO


		//... Admin Alcaldia
		// TODO
		
		//... Rutas Mintic
		// TODO

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

	/*
	 * Enables CORS on requests. This method is an initialization method and should be called once.
	 * 
	 * https://sparktutorials.github.io/2016/05/01/cors.html
	 *
	 * TODO: check if this is a more complete solution to adding CORS (AO)
	 * TODO: remove the SuppressWaring when done
	 * 
	 */
	
	@SuppressWarnings("unused")
	private static void enableCORS(final String origin, final String methods, final String headers) {

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

	    before((request, response) -> {
	        response.header("Access-Control-Allow-Origin", origin);
	        response.header("Access-Control-Request-Method", methods);
	        response.header("Access-Control-Allow-Headers", headers);
	        // Note: this may or may not be necessary in your particular application
	        response.type("application/json");
	    });
	}
	

	/** 
	 * Get JETTY configuration from properties file
	 * @param pConfig
	 *               this is the path and name of the configuration file
	 */
	private static void getConfig(String pConfig) {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(pConfig);
			prop.load(input);
			
			JETTY_SERVER_PORT          = Integer.parseInt(prop.getProperty("jetty.server.port"));
			JETTY_SERVER_MAXTHREADS    = Integer.parseInt(prop.getProperty("jetty.server.minthreads"));
			JETTY_SERVER_MINTHREADS    = Integer.parseInt(prop.getProperty("jetty.server.maxthreads"));
			JETTY_SERVER_TIMEOUTMILLIS = Integer.parseInt(prop.getProperty("jetty.server.timeoutMillis"));
			USE_SPARK_HTTPS            = Boolean.parseBoolean(prop.getProperty("spark.https"));
		
		} catch (Exception e) {
			e.printStackTrace();
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
