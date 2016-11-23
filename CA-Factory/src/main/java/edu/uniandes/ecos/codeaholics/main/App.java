package edu.uniandes.ecos.codeaholics.main;

import edu.uniandes.ecos.codeaholics.business.AuthServices;
import edu.uniandes.ecos.codeaholics.business.CitizenServices;
import edu.uniandes.ecos.codeaholics.business.FunctionaryServices;
import edu.uniandes.ecos.codeaholics.business.MayoraltyServices;
import edu.uniandes.ecos.codeaholics.business.StatisticsServices;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.config.Routes;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
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

	private static Configuration cfg;
	private static Template pseTemplate;

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

		cfg = new Configuration(Configuration.VERSION_2_3_23);

		try {
			cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates/"));
			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			cfg.setLogTemplateExceptions(false);

			pseTemplate = cfg.getTemplate("about.ftlh");

		} catch (IOException e) {

			e.printStackTrace();
		}
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
		get(Routes.CITIZENS + "procedures/", CitizenServices::consultProcedureRequets, GeneralUtil.json());

		// obtener detalle de un tramite por id /CITIZENS/ metodo GET
		get(Routes.CITIZENS + "procedures/edit/:id/", CitizenServices::consultProceduresById, GeneralUtil.json());

		// obtener detalle de un tramite para iniciar /CITIZENS/ metodo GET
		get(Routes.CITIZENS + "procedures/detail/:mayoraltyName/:procedureName/", CitizenServices::getProcedure,
				GeneralUtil.json());

		// iniciar tramite /CITIZENS/ metodo POST {procedureData info json}
		post(Routes.CITIZENS + "procedures/", CitizenServices::startProcedure, GeneralUtil.json());

		// crear tramite iniciado por el ciudadano /CITIZENS/ metodo POST
		post(Routes.CITIZENS + "procedures/iniciar/:mayoraltyName/:procedureName/", CitizenServices::startProcedure,
				GeneralUtil.json());

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
		// obtener lista de ciudadanos /CITIZENS/ metodo GET
		get(Routes.ADMIN, MayoraltyServices::getCitizenListForFunctionary, GeneralUtil.json());

		// obtener lista de ciudadanos /CITIZENS/ metodo GET
		post(Routes.ADMIN, MayoraltyServices::createFunctionary, GeneralUtil.json());
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

		/*
		 * Get some basic statistics to display at the Home page
		 * 
		 */
		get(Routes.STATS + "basics/", StatisticsServices::getBasicStats, GeneralUtil.json());

		/*
		 * Get the backend info
		 * 
		 */
		get("/about", App::displayAbout);

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

			Routes.BARCODER_EXTSVC_ROUTE = prop.getProperty("extsvc.barcoder");
			Routes.IDCERTIFIER_EXTSVC_ROUTE = prop.getProperty("extsvc.idcert");
			Routes.PSE_EXTSVC_ROUTE = prop.getProperty("extsvc.pse");

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

	private static Object displayAbout(Request pRequest, Response pResponse) {

		Object response = null;
		try {

			Map<String, Object> root = new HashMap<>();
			InetAddress addr = InetAddress.getLocalHost();
	        String ipAddress = addr.getHostAddress();
			root.put("serverip", ipAddress);
			StringWriter out = new StringWriter();
			pseTemplate.process(root, out);

			pResponse.type("text/html");
			pResponse.status(200);

			return out;

		} catch (Exception e) {
			System.out.println("We got an exception");
			pResponse.status(400);
			pResponse.type("application/json");
			response = "{ errorCode : \"exception caught\"}";
		}

		return response;
	}
	
}
