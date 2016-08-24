/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;
import java.util.HashMap;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class ViewsHelper {

	public static String render(String templatePath, HashMap<String, Object> model) {

		return new FreeMarkerEngine().render(new ModelAndView(model, templatePath));
	}
	
}
