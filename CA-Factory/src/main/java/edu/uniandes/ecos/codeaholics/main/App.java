package edu.uniandes.ecos.codeaholics.main;

import static spark.Spark.before;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import edu.uniandes.ecos.codeaholics.business.IncidentServices;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;

/**
 * Created by snaphuman on 6/6/16.
 */
public class App {

	// Metodos
	/***
	 * Metodo principal del sistema.
	 * 
	 * @param args argunmentos
	 */
	public static void main(String[] args) {

		// Initialize Database Connection
		DatabaseSingleton.getInstance();
		staticFiles.location("/public");
        /*HTTPS line --- JLRM*/
        //secure("deploy/keystore.jks", "codeaholics", null, null);

		// Rutas App de android
		post("/api/login", IncidentServices::doLogin, GeneralUtil.json());
		post("/api/user/create", IncidentServices::create, GeneralUtil.json());
		post("/api/doc/get", IncidentServices::getById, GeneralUtil.json());

		// 
		before("/api/doc/*", Authorization::doDocAuthorization);

	}
}
