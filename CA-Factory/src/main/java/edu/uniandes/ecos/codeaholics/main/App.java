package edu.uniandes.ecos.codeaholics.main;

import edu.uniandes.ecos.codeaholics.business.CitizenServices;
import edu.uniandes.ecos.codeaholics.config.Authorization;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;

import static spark.Spark.*;

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

        //Rutas Ciudadano
        post("/create", CitizenServices::insertCitizen, GeneralUtil.json());
        post("/citizenLogin", CitizenServices::doLogin, GeneralUtil.json());
        get("/citizen/citizenList", CitizenServices::getCitizenList, GeneralUtil.json());
        post("/citizen/getCitizen", CitizenServices::getCitizenDetail, GeneralUtil.json());
        get("/citizen/closeSession", CitizenServices::closeSession, GeneralUtil.json());
        
        //Rutas Alcaldia

        //Admin Alcaldia

        //Rutas Mintic


        before("/citizen/*", Authorization::authorizeCitizen);

        /**
         * Enable CORS in Spark Java to allow origins *
         * @see https://gist.github.com/saeidzebardast/e375b7d17be3e0f4dddf
         */
        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

    }
}
