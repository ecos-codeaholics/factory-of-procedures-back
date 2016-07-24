package edu.uniandes.ecos.codeaholics.main;

import static spark.Spark.before;
import static spark.Spark.post;
import static spark.Spark.get;
import static spark.Spark.staticFiles;

import edu.uniandes.ecos.codeaholics.business.CitizenServices;
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
		
		//Pruebas
		get("/login", CitizenServices::login);
		post("/login", CitizenServices::doLogin);
		get("/signup", CitizenServices::signup);
		post("/signup", CitizenServices::createUser);

		//Rutas Ciudadano
		post("/citizen/create", CitizenServices::insertCitizen, GeneralUtil.json());
		post("/citizen/login", CitizenServices::doLogin, GeneralUtil.json());
		
		//Rutas Alcaldia
		
		//Admin Alcaldia
		
		//Rutas Mintic
		
		
		before("/api/doc/*", Authorization::doDocAuthorization);

	}
}
