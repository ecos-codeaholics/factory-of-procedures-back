package edu.uniandes.ecos.codeaholics.main;

import java.util.HashMap;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

	/**
	 * 
	 * @author Codeaholics
	 *
	 */
	public class CorsFilter {
	
		
	private static final HashMap<String, String> corsHeaders = new HashMap<String, String>();
    
	static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

	/**
	 * 
	 */
    public final static void apply() {
        Filter filter = new Filter() {
            @Override
            public void handle(Request request, Response response) throws Exception {
                corsHeaders.forEach((key, value) -> {
                    response.header(key, value);
                });
            }
        };
        Spark.after(filter);
    }

}
