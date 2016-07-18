package edu.uniandes.ecos.codeaholics.business;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.oldmodels.EpisodeModel;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import edu.uniandes.ecos.codeaholics.persistence.Episode;
import spark.Request;
import spark.Response;

/**
 * Created by snaphuman on 7/11/16.
 */
public class IncidentServices {
	
	// Atributos
	private static EpisodeModel episodes = new EpisodeModel();

	private static Gson GSON = new GsonBuilder()
			.serializeNulls()
			.create();

    public static String formEpisodes (Request req, Response res) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("title", "Find migraine episodes");

        return GeneralUtil.render("episodes.ftl", params);
    }

    public static String findEpisodes (Request req, Response res) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date start = new Date();
        Date end = new Date();

        int cedula = Integer.parseInt(req.queryParams("cedula"));
        String startDate = req.queryParams("start-date");
        String endDate = req.queryParams("end-date");

        if (startDate.isEmpty() && endDate.isEmpty()) {
            episodes.getList(cedula);
        } else {
            System.out.println("Entra por aca");

            try {
                start = formatter.parse(startDate);
                end = formatter.parse(endDate);
            } catch (ParseException e ) {
                e.printStackTrace();
            }

            List<Episode> dataset = episodes.getListByDate (cedula, start, end);

            HashMap<String, Object> params = new HashMap<>();

            params.put("title", "Results");

            for (Episode item : dataset) {
                params.put(Episode.NAME_ACTIVIDAD, item.getActividad());
                params.put(Episode.NAME_CEDULA, item.getCedula());
                params.put(Episode.NAME_FECHA, item.getFecha());
                params.put(Episode.NAME_HORA, item.getHora());
                params.put(Episode.NAME_INTENSIDAD, item.getNivelDolor());
                params.put(Episode.NAME_MEDICAMENTO, item.getMedicamento());
            }

            return GeneralUtil.render("episodes-list.ftl", params);
        }


        return "success";
    }
    
    public static String create(Request req, Response res) {

		try {

			Episode data = GSON.fromJson(req.body(), Episode.class);
			episodes.addEpisode(data);
			
		} catch (JsonSyntaxException e) {
			res.status(400);
			return "invalid json format";
		}

		req.body();
		return "success";
	}

    // TODO: User must have to be authorized, Doctor Role.
	/***
	 * Obtain episodes from user id.
	 *
	 * @param req Request
	 * @param res Response
	 * @return Serialized Json object with list of episodes
	 */
    public static String getById (Request req, Response res) {

    	Episode data = GSON.fromJson(req.body(), Episode.class);

        List<Episode> dataset =  episodes.getList(data.getCedula());

        Type type = new TypeToken<List<Episode>>() {}.getType();

        String json = GSON.toJson(dataset, type);

        return json;
    }
    
    public static String doLogin(Request req, Response res) {
    	
    	try {

    		Citizen data = GSON.fromJson(req.body(), Citizen.class);
    		String result = null;
    		boolean authenticated = Authentication.doAuthentication(data.getEmail(), data.getPassword(), "user");
    		if (authenticated) {
    			result = "true";
    		} else {
    			result = "false";
    		}
    		return result;
			
		} catch (JsonSyntaxException e) {
			res.status(400);
			return "invalid json format";
		}

	}
}
