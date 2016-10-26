/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by davidMtz on 27/6/16.
 */
public final class ValidationUtil {

	// Atributos
	private final static Logger log = LogManager.getLogger(ValidationUtil.class);
	private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	// Metodos
	/**
	 * Valida el email dado con una expresion regular.
	 * 
	 * @param email
	 *            email para validar
	 * @return true con email valido, de otro modo false
	 */
	public static boolean validateEmail(String email) {

		Pattern pattern = Pattern.compile(PATTERN_EMAIL);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();

	}

}
