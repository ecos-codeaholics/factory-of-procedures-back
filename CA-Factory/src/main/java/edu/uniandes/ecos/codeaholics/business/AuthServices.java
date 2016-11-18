package edu.uniandes.ecos.codeaholics.business;

//import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
//import javax.management.relation.RelationServiceNotRegisteredException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

//import com.google.gson.reflect.TypeToken;

import com.mongodb.MongoClientException;
import com.mongodb.MongoWriteException;

import edu.uniandes.ecos.codeaholics.config.Authentication;
import edu.uniandes.ecos.codeaholics.config.AuthenticationJWT;
import edu.uniandes.ecos.codeaholics.config.ChangePwdModelHelper;
import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc;
import edu.uniandes.ecos.codeaholics.config.EmailNotifierSvc.EmailType;
import edu.uniandes.ecos.codeaholics.config.GeneralUtil;
import edu.uniandes.ecos.codeaholics.config.IAuthenticationSvc;
import edu.uniandes.ecos.codeaholics.config.IMessageSvc;
//import edu.uniandes.ecos.codeaholics.config.Notification;
import edu.uniandes.ecos.codeaholics.config.ResponseMessage;
import edu.uniandes.ecos.codeaholics.config.ValidationUtil;
import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import spark.Request;
import spark.Response;

public class AuthServices {

	private static Gson GSON = new GsonBuilder().serializeNulls().create();

	private static IAuthenticationSvc authenticate = null;

	private static IMessageSvc messager = new ResponseMessage();

	private final static Logger log = LogManager.getLogger(AuthServices.class);

	private static String authenticationMethod = "JWT"; // ... JWT, Simple

	private static String CITIZEN_USER_PROFILE = "citizen";

	/***
	 * Verifica las credenciales del ususario y crea la sesion.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return sesion creada en el sistema
	 */
	public static Object doLogin(Request pRequest, Response pResponse) {

		Object response = null;

		try {
			log.info("login body: " + pRequest.body());
			Citizen data = GSON.fromJson(pRequest.body(), Citizen.class);

			if (authenticationMethod.equals("JWT")) {
				authenticate = new AuthenticationJWT();
			} else {
				authenticate = new Authentication();
			}

			boolean authenticated = authenticate.doAuthentication(data.getEmail(), data.getPassword(),
					data.getUserProfile());
			if (authenticated) {

				if (authenticationMethod.equals("JWT")) {
					// 1. process header Autorization : Bearer <token>
					StringBuilder bStr = new StringBuilder();
					// bStr.append("Bearer");
					// bStr.append(" ");
					bStr.append((String) authenticate.getAnswer());
					pResponse.header("access-control-expose-headers", "Authorization");
					pResponse.header("Authorization", bStr.toString());
					response = messager.getOkMessage("Inicio de sesi\u00F3n exitoso");
				} else {
					response = authenticate.getAnswer();
				}
			}

		} catch (WrongUserOrPasswordException e) {
			pResponse.status(401);
			response = messager.getNotOkMessage(e.getMessage());

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

		pResponse.type("application/json");
		return response;

	}

	/***
	 * Agrega un ciudadano a la base de datos.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object insertCitizen(Request pRequest, Response pResponse) {

		Object response = null;

		try {

			Citizen citizen = GSON.fromJson(pRequest.body(), Citizen.class);

			String[] hash = GeneralUtil.getHash(citizen.getPassword(), "");
			citizen.setPassword(hash[1]);
			citizen.setSalt(hash[0]);
			citizen.setUserProfile(CITIZEN_USER_PROFILE);
			Document citizenValidation = validateCitizen(citizen);

			if (citizenValidation.get("result").toString().equals("true")) {
				DataBaseUtil.save(citizen.toDocument(), CITIZEN_USER_PROFILE);
				EmailNotifierSvc sendEmail = new EmailNotifierSvc();
				sendEmail.send(EmailType.REGISTRATION, citizen.getEmail());

				response = messager.getOkMessage("Registro exitoso");
			} else {
				pResponse.status(401);
				response = messager.getNotOkMessage(citizenValidation.get("message").toString());
			}

		} catch (JsonSyntaxException e) {
			pResponse.status(401);
			response = messager.getNotOkMessage(e.getMessage());
		} catch (AddressException e) {
			pResponse.status(401);
			response = messager.getNotOkMessage(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			pResponse.status(401);
			response = messager.getNotOkMessage(e.getMessage());
			e.printStackTrace();
		}

		// HEAD
		// res.status(200);
		pResponse.type("application/json");
		// return "Proceso Exitoso";

		pRequest.body();
		return response;

	}

	private static Document validateCitizen(Citizen pCitizen) {

		Document response = new Document();
		Boolean validate = true;
		String message = "Ok";

		validate = ValidationUtil.validate.apply(pCitizen.getName(), ValidationUtil.PATTERN_NAME);

		if (!validate) {
			message = "No se permiten n\u00fameros en el nombre";
		} else {
			validate = ValidationUtil.validate.apply(pCitizen.getLastName1(), ValidationUtil.PATTERN_NAME);
			if (!validate) {
				message = "No se permiten n\u00fameros en el primer apellido";
			} else {
				if (pCitizen.getLastName2() != null) {
					validate = ValidationUtil.validate.apply(pCitizen.getLastName2(), ValidationUtil.PATTERN_NAME);
				}
				if (!validate) {
					message = "No se permiten n\u00fameros en el segundo apellido";
				} else {
					validate = ValidationUtil.validate.apply(pCitizen.getEmail(), ValidationUtil.PATTERN_EMAIL);
					if (!validate) {
						message = "Correo ingresado no cumple con formato requerido";
					} else {
						validate = ValidationUtil.validateBithDate(pCitizen.getBirthDate());
						if (!validate) {
							message = "Debe tener m\u00E1s de 18 a\u00F1s para registrarse";
						}
					}
				}
			}
		}
		
		response.put("result", validate);
		response.put("message", message);

		return response;
	}

	/***
	 * Agrega un ciudadano a la base de datos.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @return mensaje de proceso exitoso
	 */
	public static Object insertFunctionaryMock() {

		Object response = null;

		try {

			response = messager.getOkMessage("Registro exitoso");

		} catch (JsonSyntaxException e) {
			response = messager.getNotOkMessage(e.getMessage());
		}

		return response;

	}

	/**
	 * ENvia un nuevo password aleatorio al email del usuario
	 * 
	 * @param pRequest
	 *            Request
	 * @param pResponse
	 *            Response
	 * @return json object con la informacion de exito o falla del mensaje
	 * @throws WrongUserOrPasswordException
	 */

	public static Object resetPassword(Request pRequest, Response pResponse) throws WrongUserOrPasswordException {

		Object response = null;

		try {
			Citizen data = GSON.fromJson(pRequest.body(), Citizen.class);

			log.info(data.getEmail() + " " + data.getIdentification());

			Document filter = new Document();
			filter.append("identification", data.getIdentification());
			filter.append("email", data.getEmail());

			String userProfile = data.getUserProfile();
			ArrayList<Document> documents = DataBaseUtil.find(filter, userProfile);

			// TODO throw an exception about that email and identification
			// doesn't correspond to a registered user
			if (documents.isEmpty()) {// throw exception
				log.info("Id and email do not agree");
				throw new WrongUserOrPasswordException("Identificaci\u00F3n y correo no concuerdan", "400");
			}

			// Create randomize password
			String newPassword = GeneralUtil.randomPassword();

			log.info("new passwd" + newPassword);
			// create hash
			String newSalt = null;
			String[] hash = GeneralUtil.getHash(newPassword, "");
			String newPasswordHashed = hash[1];
			newSalt = hash[0];

			// send value to change
			Map<String, Object> valuesToReplace = new HashMap<String, Object>();
			valuesToReplace.put("password", newPasswordHashed);
			valuesToReplace.put("salt", newSalt);

			// send salt and password to the register in the DB
			Document register = new Document(valuesToReplace);

			DataBaseUtil.update(filter, register, userProfile);

			// create array list to ssend as a parameter to the EmailNotifierSvc

			ArrayList<String> parametersEmail = new ArrayList<>();
			parametersEmail.add(newPassword);

			// Send Email
			EmailNotifierSvc sendPassword = new EmailNotifierSvc();

			sendPassword.send(EmailType.CHANGE, data.getEmail(), parametersEmail);

			response = messager.getOkMessage("Proceso exitoso");

		}

		catch (WrongUserOrPasswordException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

		catch (MongoClientException mongoEx) {
			log.info(mongoEx.getMessage());

		} catch (MongoWriteException mongoEx) {
			log.info(mongoEx.getMessage());

		} catch (AddressException e) {
			e.printStackTrace();

		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return response;

	}

	public static Object changePassword(Request pRequest, Response pResponse)
			throws WrongUserOrPasswordException, AddressException, MessagingException {

		// Atributos

		Object response = null;

		try {
			ChangePwdModelHelper data = GSON.fromJson(pRequest.body(), ChangePwdModelHelper.class);

			Integer identification = Integer.parseInt(pRequest.params("identification"));
			String password = data.getPassword();
			String newPassword = data.getNewpassword();
			String newPasswordHashed = "";
			String savedPassword = "";
			String savedSalt = "";
			String passwordHashed = "";
			String newSalt = "";

			Document filter = new Document();
			filter.append("identification", identification);

			ArrayList<Document> documents = DataBaseUtil.find(filter, CITIZEN_USER_PROFILE);
			if (documents.isEmpty()) {
				log.info("User not found in DB (not registred)");
				throw new WrongUserOrPasswordException("Usuario no registrado", "400");
			}

			savedPassword = documents.get(0).getString("password");
			savedSalt = documents.get(0).getString("salt");

			// create hashed password to validate with the password saved in the
			// DB

			String[] hash = GeneralUtil.getHash(password, savedSalt);
			passwordHashed = hash[1];

			if (!passwordHashed.equals(savedPassword)) {
				log.info("Contrasena Incorrecta");
				throw new WrongUserOrPasswordException("Contrase\u00F1a incorrecta", "400");
			}

			// create hashed NEW password and salt
			hash = GeneralUtil.getHash(newPassword, "");
			newPasswordHashed = hash[1];
			newSalt = hash[0];

			// organize value to change
			Map<String, Object> valuesToReplace = new HashMap<String, Object>();
			valuesToReplace.put("password", newPasswordHashed);
			valuesToReplace.put("salt", newSalt);

			// Update the DB
			updatePwdInDB(filter, valuesToReplace, CITIZEN_USER_PROFILE);

			// create array list to send as a parameter to the EmailNotifierSvc
			ArrayList<String> parametersEmail = new ArrayList<>();
			parametersEmail.add(newPassword);

			// Send Email
			EmailNotifierSvc sendPassword = new EmailNotifierSvc();
			sendPassword.send(EmailType.CHANGE, documents.get(0).getString("email"), parametersEmail);

			response = messager.getOkMessage("Proceso exitoso");

		} catch (WrongUserOrPasswordException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}

		catch (MongoClientException mongoE) {
			log.info(mongoE.getMessage());

		} catch (MongoWriteException mongoE) {
			log.info(mongoE.getMessage());

		}

		pResponse.type("application/json");
		return response;

	}

	private static void updatePwdInDB(Document pFilter, Map<String, Object> pValuesToReplace, String pProfile)
			throws MongoWriteException {

		try {
			// send salt and password to the register in the DB
			Document register = new Document(pValuesToReplace);
			DataBaseUtil.update(pFilter, register, pProfile);

		} catch (MongoWriteException e) {
			throw e;
		}
	}

}
