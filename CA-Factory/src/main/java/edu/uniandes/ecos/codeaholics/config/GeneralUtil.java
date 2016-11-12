/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.ModelAndView;
import spark.ResponseTransformer;
import spark.template.freemarker.FreeMarkerEngine;
import spark.Request;

/**
 * Created by davidMtz on 27/6/16.
 */
public final class GeneralUtil {
	
	// Atributos
	private final static Logger log = LogManager.getLogger(GeneralUtil.class);
	
	// Metodos
	/***
	 * Convierte un arreglo de bytes a String usando valores hexadecimales
	 * 
	 * @param digest arreglo de bytes a convertir
	 * @return String creado a partir de digest
	 */
	private static String toHexadecimal(byte[] pDigest) {
		
		String hash = "";
		for (byte aux : pDigest) {
			int b = aux & 0xff;
			if (Integer.toHexString(b).length() == 1)
				hash += "0";
			hash += Integer.toHexString(b);
		}
		return hash;
	}

	/***
	 * Encripta una cadena mediante algoritmo de resumen de mensaje.
	 * 
	 * @param pPwd texto a encriptar
	 * @return mensaje encriptado
	 */
	public static String[] getHash(String pPwd, String pSalt) {

		if (pSalt.equals("")) {
			pSalt = getSalt();
		}
		String saltedPwd = pSalt + pPwd;
		String[] result = new String[2];
		result[0] = pSalt;
		byte[] digest = null;
		byte[] buffer = saltedPwd.getBytes();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.reset();
			messageDigest.update(buffer);
			digest = messageDigest.digest();
		} catch (NoSuchAlgorithmException ex) {
			log.error("Error creating hash");
		}
		result[1] = toHexadecimal(digest);
		return result;
	}
	
	/***
	 * Genera el salt para poder encriptar.
	 * 
	 * @return salt de encriptacion
	 */
	private static String getSalt() {

		String salt = String.valueOf(UUID.randomUUID());
		return salt;
	}

	/**
	 * @param object
	 * @return
	 */
	public static String toJson(Object pObject) {

		return new Gson().toJson(pObject);
	}

	/**
	 * @return
	 */
	public static ResponseTransformer json() {

		return GeneralUtil::toJson;
	}

	/**
	 * @param templatePath
	 * @param model
	 * @return
	 */
	public static String render(String pTemplatePath, HashMap<String, Object> pModel) {

		return new FreeMarkerEngine().render(new ModelAndView(pModel, pTemplatePath));
	}
	
	/**
	 * @return
	 */
	public static String randomPassword(){
		String result= null;
		String string = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		SecureRandom rnd = new SecureRandom();
		int length = 10;
		
		StringBuilder sb = new StringBuilder( length );
		   for( int i = 0; i < length; i++ ) 
		      sb.append(string.charAt(rnd.nextInt(string.length()) ) );
		result = sb.toString();
		
		return result;	
	}
	
	/**
	 * @param pRequest
	 * @param pKey
	 * @return
	 */
	public static String extractFromBody(Request pRequest, String pKey) {
		
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(pRequest.body()).getAsJsonObject();
		String value = "";
		if (json.getAsJsonObject().has(pKey)) {
			value = json.getAsJsonObject().get(pKey).getAsString();
			log.info("Found key with value: " + value);
		}
		return value;
		
	}
	
}
