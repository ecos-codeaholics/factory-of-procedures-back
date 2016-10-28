package edu.uniandes.ecos.codeaholics.main;

import edu.uniandes.ecos.codeaholics.business.AuthServices;
import edu.uniandes.ecos.codeaholics.business.CitizenServices;
import edu.uniandes.ecos.codeaholics.business.FunctionaryServices;
import edu.uniandes.ecos.codeaholics.business.MayoraltyServices;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.config.Routes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static spark.Spark.*;

/**
 * 
 * @author Codeaholics
 *
 */
public class App {

	public static final String CONFIG_FILE = "src/main/resources/config.properties";
	public static int JETTY_SERVER_PORT = 4567;
	public static int JETTY_SERVER_MAXTHREADS = 1000;
	public static int JETTY_SERVER_MINTHREADS = 50;
	public static int JETTY_SERVER_TIMEOUTMILLIS = 30000;
	public static boolean USE_SPARK_HTTPS = false;

	/***
	 * Metodo principal del sistema.
	 *
	 * @param args
	 *            argumentos
	 * 
	 */
	public static void main(String[] args) {

		getConfig(CONFIG_FILE);
		port(JETTY_SERVER_PORT);
		threadPool(JETTY_SERVER_MAXTHREADS, JETTY_SERVER_MINTHREADS, JETTY_SERVER_TIMEOUTMILLIS);

		/**
		 * HTTPS option : JLRM
		 */
		if (USE_SPARK_HTTPS) {
			secure("deploy/keystore.jks", "codeaholics", null, null);
		}

		/**
		 * Initialize Database Connection
		 */
		DatabaseSingleton.getInstance();

		staticFiles.location("/public");

		/**
		 * Enable CORS
		 */
		CorsFilter.apply();

		/**
		 * Auth Routes
		 */
		// crear ciudadano /auth/ metodo POST {citizen info json}
		post(Routes.AUTH, AuthServices::insertCitizen, GeneralUtil.json());

		// reset de clave /auth/ metodo PUT {info password recovery json,
		// {email, id}}
		put(Routes.AUTH, AuthServices::resetPassword, GeneralUtil.json());

		// Must be in the authorize routes
		// {old password, new pass}}
		put(Routes.AUTH + ":identification", AuthServices::changePassword, GeneralUtil.json());

		// iniciar sesion /auth/login metodo POST
		post(Routes.AUTH + "login/", AuthServices::doLogin, GeneralUtil.json());

		post(Routes.AUTH + "upload/", CitizenServices::uploadDocuments, GeneralUtil.json());

		/**
		 * Citizen Routes
		 */
		// obtener lista de ciudadanos /CITIZENS/ metodo GET
		get(Routes.CITIZENS, CitizenServices::getCitizenList, GeneralUtil.json());

		// obtener detalles de unƒ ciudadano /CITIZENS/{id} --> template metodo
		// GET
		get(Routes.CITIZENS + ":identification", CitizenServices::getCitizenDetail, GeneralUtil.json());

		// cerrar sesion /SESSIONS/ metodo DELETE {session info json}
		delete(Routes.CITIZENS, CitizenServices::closeSession, GeneralUtil.json());

		// obtener lista de alcaldias del sistema /CITIZENS/ metodo GET
		get(Routes.CITIZENS + "mayoralties/", MayoraltyServices::getMayoraltyList, GeneralUtil.json());

		// obtener lista de tramites por alcaldia /CITIZENS/ metodo GET
		get(Routes.CITIZENS + "procedures/:mayoraltyName/", MayoraltyServices::proceduresByMayoralty,
				GeneralUtil.json());

		// obtener lista de tramites por ciudadano /CITIZENS/ metodo GET
		get(Routes.CITIZENS + "procedures/", CitizenServices::consultProcedures, GeneralUtil.json());

		// obtener detalle de un tramite por id /CITIZENS/ metodo GET
		get(Routes.CITIZENS + "procedures/edit/:id/", CitizenServices::consultProceduresById, GeneralUtil.json());

		// obtener detalle de un tramite para iniciar /CITIZENS/ metodo GET
		get(Routes.CITIZENS + "procedure/", CitizenServices::getProcedure, GeneralUtil.json());
		
		// iniciar tramite /CITIZENS/ metodo POST {procedureData info json}
		post(Routes.CITIZENS + "procedure/", CitizenServices::startProcedure, GeneralUtil.json());

		/**
		 * Routes Mayoralty
		 */
		// obtener lista de tramites asignados al funcionario /FUNCTIONARIES/
		// metodo GET
		get(Routes.FUNTIONARIES + "procedures/", FunctionaryServices::consultProcedures, GeneralUtil.json());

		// obtener detalle de un tramite por id /CITIZENS/ metodo GET
		get(Routes.FUNTIONARIES + "procedures/edit/" + ":id" + "/", FunctionaryServices::consultProceduresById,
				GeneralUtil.json());

		// aprobar un paso del flujo de un trámite /FUNCTIONARIES/ metodo POST.
		put(Routes.FUNTIONARIES + "procedures/:procedureId/steps/edit/:stepId/",
				FunctionaryServices::approveProcedureStep, GeneralUtil.json());

		/**
		 * Routes Administrator Mayoralty
		 */
		// TODO

		/**
		 * Routes MINTIC
		 */
		// TODO

		// Control de acceso para Ciudadnos
		before(Routes.CITIZENS + "*", Authorization::authorizeCitizen);

		// Control de acceso para Funcionionarios
		before(Routes.FUNTIONARIES + "*", Authorization::authorizeFuntionary);

		// Control de acceso para Admin Alcaldia
		before(Routes.ADMIN + "*", Authorization::authorizeAdmin);

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

	}

	/**
	 * Get JETTY configuration from properties file
	 * 
	 * @param pConfig
	 *            this is the path and name of the configuration file
	 */
	private static void getConfig(String pConfig) {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(pConfig);
			prop.load(input);

			JETTY_SERVER_PORT = Integer.parseInt(prop.getProperty("jetty.server.port"));
			JETTY_SERVER_MAXTHREADS = Integer.parseInt(prop.getProperty("jetty.server.minthreads"));
			JETTY_SERVER_MINTHREADS = Integer.parseInt(prop.getProperty("jetty.server.maxthreads"));
			JETTY_SERVER_TIMEOUTMILLIS = Integer.parseInt(prop.getProperty("jetty.server.timeoutMillis"));
			USE_SPARK_HTTPS = Boolean.parseBoolean(prop.getProperty("spark.https"));

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
