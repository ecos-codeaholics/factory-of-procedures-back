/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.util.Date;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * Created by davidMtz on 27/6/16.
 * Modified AOsorio 29/10/16
 */

public final class ValidationUtil {
	
	public static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static final String PATTERN_NAME = "^[\\p{L} .'-]+$";
	
	public static final String PATTERN_NUMBER = "^[0-9]*$";
	
	// Metodos
	
	/**
	 * validate a given string against a defined regex pattern
	 * returns true if there is a match, false otherwise
	 * 
	 */
	public static BiFunction<String, String, Boolean> validate = (from,pattern) -> {
		return Pattern.compile(pattern).matcher(from).matches();
	};
	
	@SuppressWarnings("deprecation")
	public static Boolean validateBithDate (Date pBirthDate){
		Boolean result = true;
		Date today = new Date();
		Date minDate = new Date(today.getYear() - 18, today.getMonth(), today.getDay());
		if(pBirthDate.getTime() > minDate.getTime()){
			result = false;
		}
		
		return result;
	}
}
