package edu.uniandes.ecos.codeaholics.business;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.config.Notification;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import spark.Request;
import spark.Response;

public class CitizenServices {

	private static Gson GSON = new GsonBuilder().serializeNulls().create();

	/***
	 * Verifica las credenciales del ususario y crea la sesion.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return sesion creada en el sistema
	 */
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

	/***
	 * Agrega un ciudadno a la base de datos.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return mensaje de proceso exitoso
	 */
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
	 * Obtiene la lista de todos los ciudadanos registrados en el sistema.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return lista con json por cada ciudadano
	 */
	public static String getCitizenList(Request req, Response res) {
		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.getAll("citizen");
		String fullName = "";
		for (Document item : documents) {
			fullName = item.get("name").toString() +" "+ item.get("lastName1").toString() +" "+ item.get("lastName2").toString();
			item.remove("name");
			item.remove("lastName1");
			item.remove("lastName2");
			item.remove("password");
			item.remove("salt");
			item.remove("birthDate");
			item.remove("email");
			item.put("fullName", fullName);
			dataset.add(item);
		}
		Type type = new TypeToken<List<Document>>() {
		}.getType();

		String json = GSON.toJson(dataset, type);

		return json;
	}

	/***
	 * Obtiene toda la informacion de un ciudadano dado su numero de
	 * identificacion.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return json informacion disponible del ciudadano
	 */
	public static String getCitizenDetail(Request req, Response res) {
		Citizen citizen = GSON.fromJson(req.body(), Citizen.class);
		Document filter = new Document();
		filter.append("identification", citizen.getIdentification());
		List<Document> dataset = new ArrayList<>();
		ArrayList<Document> documents = DataBaseUtil.find(filter, "citizen");
		for (Document item : documents) {
			item.remove("password");
			item.remove("salt");
			dataset.add(item);
		}
		Type type = new TypeToken<List<Document>>() {
		}.getType();

		String json = GSON.toJson(dataset, type);

		return json;
	}
	
	/***
	 * Obtiene toda la informacion de un ciudadano dado su numero de
	 * identificacion.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return json informacion disponible del ciudadano
	 */
	public static String closeSession(Request req, Response res) {
		try {

			String email = req.queryParams("email");
			Authentication.closedSession(email);
			
			return "success";

		} catch (JsonSyntaxException e) {
			res.status(400);
			return "invalid json format";
		}
	}

	/***
	 * Registra la solicitud de un tramite y toda la infromacion asocida a esa
	 * solicitud.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static String startProcedure(Request req, Response res) {

		return "success";
	}

	/***
	 * Consulta historico de tramites.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static String consultProcedures(Request req, Response res) {

		return "success";
	}

	/***
	 * Consulta estado de un tramite.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static String consultProceduresById(Request req, Response res) {

		return "success";
	}

	/***
	 * Consulta estado de tramites que se solicitaron en un rango de fechas.
	 * 
	 * @param req
	 *            request
	 * @param res
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static String consultProceduresByDate(Request req, Response res) {

		return "success";
	}

}
