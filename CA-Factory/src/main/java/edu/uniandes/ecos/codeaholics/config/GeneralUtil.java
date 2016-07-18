package edu.uniandes.ecos.codeaholics.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import spark.ModelAndView;
import spark.ResponseTransformer;
import spark.template.freemarker.FreeMarkerEngine;

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
	private static String toHexadecimal(byte[] digest) {
		
		String hash = "";
		for (byte aux : digest) {
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

	public static String toJson(Object object) {

		return new Gson().toJson(object);
	}

	public static ResponseTransformer json() {

		return GeneralUtil::toJson;
	}

	public static String render(String templatePath, HashMap<String, Object> model) {

		return new FreeMarkerEngine().render(new ModelAndView(model, templatePath));
	}

}
