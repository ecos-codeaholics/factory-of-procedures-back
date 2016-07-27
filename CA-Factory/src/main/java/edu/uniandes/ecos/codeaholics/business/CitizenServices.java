package edu.uniandes.ecos.codeaholics.business;

import java.util.ArrayList;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.config.Notification;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import spark.Request;
import spark.Response;

import static edu.uniandes.ecos.codeaholics.persistence.ViewsHelper.render;

public class CitizenServices {

	private static Gson GSON = new GsonBuilder().serializeNulls().create();
	private static Citizen users = new Citizen();

	public static String doLogin(Request req, Response res) {

		try {

			Citizen data = GSON.fromJson(req.body(), Citizen.class);
			String result = null;
			boolean authenticated = Authentication.doAuthenticationCitizen(data.getEmail(), data.getPassword());
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

	public static String insertCitizen(Request req, Response res) {

		try {

			Citizen citizen = GSON.fromJson(req.body(), Citizen.class);
			String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
			citizen.setPassword(hash[1]);
			citizen.setSalt(hash[0]);
			DataBaseUtil.save(citizen.toDocument(), "citizen");
			Notification.sendEmail(citizen.getEmail());

		} catch (JsonSyntaxException e) {
			res.status(400);
			return "invalid json format";
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		req.body();
		return "success";
	}

	/***
	 * Carga la pagina de registro de usuarios.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return estrututa de pagina
	 */
	public static String signup(Request req, Response res) {

		HashMap<String, Object> params = new HashMap<>();
		params.put("title", "Sign up");

		return render("signup.ftl", params);
	}

	public static String createUser(Request req, Response res) {

		String email = req.queryParams("email");
		String password = req.queryParams("password");
		String name = req.queryParams("name");
		String lastName = req.queryParams("last-name");
		Integer identification = Integer.parseInt(req.queryParams("identification"));
		String[] hash = GeneralUtil.getHash(password, "");
		users.setPassword(hash[1]);
		users.setSalt(hash[0]);
		users.setEmail(email);
		users.setName(name);
		users.setLastName1(lastName);
		users.setIdentification(identification);
		HashMap<String, Object> params = new HashMap<>();
		params.put("title", "Sign up");

		// users.addUser(name, lastName, password, email, identification, rol);

		DataBaseUtil.save(users.toDocument(), "citizen");
		try {
			Notification.sendEmail(users.getEmail());
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		params.put("msg", "Success");

		return render("signup.ftl", params);
	}

	/***
	 * Carga pagina de login.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return estrututa de pagina
	 */
	public static String login(Request req, Response res) {

		HashMap<String, Object> params = new HashMap<>();

		params.put("title", "Login");

		return render("login.ftl", params);
	}

	public static String startProcedure(Request req, Response res) {

		return "success";
	}

	public static String consultProcedures(Request req, Response res) {

		return "success";
	}

	public static String consultProceduresById(Request req, Response res) {

		return "success";
	}

	public static String consultProceduresByDate(Request req, Response res) {

		return "success";
	}
	
	public static String getCitizenList(Request req, Response res) {
		List<Citizen> dataset =  new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.getAll("citizen");
		for (Document item : documents) {
			dataset.add(GSON.fromJson(item.toJson(), Citizen.class));
		} 
        Type type = new TypeToken<List<Citizen>>() {}.getType();

        String json = GSON.toJson(dataset, type);

        return json;
	}
	
	public static String getCitizenDetail(Request req, Response res) {
		Citizen citizen = GSON.fromJson(req.body(), Citizen.class);
		Document filter = new Document();
		filter.append("identification", citizen.getIdentification());
		List<Citizen> dataset =  new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(filter, "citizen");
		for (Document item : documents) {
			dataset.add(GSON.fromJson(item.toJson(), Citizen.class));
		} 
        Type type = new TypeToken<List<Citizen>>() {}.getType();

        String json = GSON.toJson(dataset, type);

        return json;
	}

}
