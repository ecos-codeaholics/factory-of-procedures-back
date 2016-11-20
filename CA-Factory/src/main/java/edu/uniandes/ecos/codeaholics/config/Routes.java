/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

/**
 * Rutas de todos lo webservices
 * 
 * @author Codeaholics
 *
 */
public class Routes {

	// Rutas de sesiones
	public static final String SESSIONS = "/sessions/";

	// Rutas del ciudadano
	public static final String CITIZENS = "/citizens/";

	// Rutas del funcionario
	public static final String FUNTIONARIES = "/functionaries/";

	// Rutas del administrador
	public static final String ADMIN = "/admin/";

	// Rutas de autenticacion
	public static final String AUTH = "/auth/";
	
	// Ruta para estadisticas y reportes
	public static final String STATS = "/stats/";
	
	//Rutas de servicios externos
	
	//1. Servicio de generacion de numero de radicado y codigo de barras
	public static String BARCODER_EXTSVC_ROUTE;
	
	//2. Servicio Mock de verificacion de cedula
	public static String IDCERTIFIER_EXTSVC_ROUTE;
	
	//3. Servicio Mock de pago PSE
	public static String PSE_EXTSVC_ROUTE;
	
	
}
