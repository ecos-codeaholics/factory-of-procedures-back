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
import edu.uniandes.ecos.codeaholics.exceptions.AuthenticationException.WrongUserOrPasswordException;
import edu.uniandes.ecos.codeaholics.persistence.Citizen;
import spark.Request;
import spark.Response;

public class AuthServices {

	private static Gson GSON = new GsonBuilder().serializeNulls().create();

	private static IAuthenticationSvc authenticate = null;

	private static IMessageSvc messager = new ResponseMessage();

	
	private final static Logger log = LogManager.getLogger(AuthServices.class);


	private static String authenticationMethod = "JWT"; //... JWT, Simple

	
	private static String USER_PROFILE = "citizen";

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

			Citizen data = GSON.fromJson(pRequest.body(), Citizen.class);

			if (authenticationMethod.equals("JWT")) {
				authenticate = new AuthenticationJWT();
			} else {
				authenticate = new Authentication();
			}

			boolean authenticated = authenticate.doAuthentication(data.getEmail(), data.getPassword(), "citizen");
			if (authenticated) {

				if (authenticationMethod.equals("JWT")) {
					// 1. process header Autorization : Bearer <token>
					StringBuilder bStr = new StringBuilder();
					//bStr.append("Bearer");
					//bStr.append(" ");
					bStr.append((String) authenticate.getAnswer());
					pResponse.header("access-control-expose-headers", "Authorization");
					pResponse.header("Authorization", bStr.toString());
					response = messager.getOkMessage("Successful login");
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
			citizen.setUserProfile(USER_PROFILE);
			DataBaseUtil.save(citizen.toDocument(), USER_PROFILE);

			// create array list to send as a parameter to the EmailNotifierSvc
			ArrayList<String> parametersEmail = new ArrayList<>();
			parametersEmail.add(citizen.getEmail());

			// TODO: replace with new service -
			// EmailNotifier.send(EmailType.REGISTRATION,citizen.getEmail());
			// Notification.sendEmail(citizen.getEmail());
			// Send Email
			EmailNotifierSvc sendEmail = new EmailNotifierSvc();
			sendEmail.send(EmailType.REGISTRATION, parametersEmail);

			response = messager.getOkMessage("Successful registration");

		} catch (JsonSyntaxException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		} catch (AddressException e) {
			response = messager.getNotOkMessage(e.getMessage()); // change
																	// message
			e.printStackTrace();
		} catch (MessagingException e) {
			response = messager.getNotOkMessage(e.getMessage()); // change
																	// message
			e.printStackTrace();
		}

		// HEAD
		// res.status(200);
		pResponse.type("application/json");
		// return "success";

		pRequest.body();
		return response;

	}

	/**
	 * ENvia un nuevo password aleatorio al email del usuario
	 * 
	 * @param pRequest
	 *            Request
	 * @param pResponse
<<<<<<< HEAD
	 * 				Response
	 * @return	json object con la informacion de exito o falla del mensaje
	 * @throws WrongUserOrPasswordException 
=======
	 *            Response
	 * @return json object con la informacion de exito o falla del mensaje
>>>>>>> branch 'development' of https://github.com/ecos-codeaholics/factory-of-procedures-back.git
	 */

	public static Object resetPassword (Request pRequest, Response pResponse) throws WrongUserOrPasswordException {

		Object response = null;

		try {
			Citizen data = GSON.fromJson(pRequest.body(), Citizen.class);
			System.out.println(data.getEmail() + " " + data.getIdentification());

			Document filter = new Document();
			filter.append("identification", data.getIdentification());
			filter.append("email", data.getEmail());
			
			ArrayList<Document> documents = DataBaseUtil.find(filter, USER_PROFILE);
			
			//TODO throw an exception about that email and identification doesn't correspond to a registered user			
			if (documents.isEmpty()){//throw exception
				log.info("Identification or Email wrong");
				throw new WrongUserOrPasswordException("Identification or Email wrong", "400");	
				}
			
			//Create randomize password
			String newPassword = GeneralUtil.randomPassword();

			System.out.println(newPassword);
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

			DataBaseUtil.update(filter, register, USER_PROFILE);

			// create array list to ssend as a parameter to the EmailNotifierSvc

			ArrayList<String> parametersEmail = new ArrayList<>();
			parametersEmail.add(data.getEmail());
			parametersEmail.add(newPassword);

			// Send Email
			EmailNotifierSvc sendPassword = new EmailNotifierSvc();

			sendPassword.send(EmailType.CHANGE, parametersEmail);
			
			response = messager.getOkMessage("Success");

						
		} 
		
		catch (WrongUserOrPasswordException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}
		
		catch (MongoClientException M) {
			// TODO: handle exception
			System.out.println("Success");
		} catch (MongoWriteException M) {
			// TODO: handle exception
			System.out.println("Mongo Exception");

		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;

	} 
	
	
	public static Object changePassword (Request pRequest, Response pResponse) throws WrongUserOrPasswordException, AddressException, MessagingException {
		
		// Atributos
		
		Object response = null;
		
		try {
			ChangePwdModelHelper data = GSON.fromJson(pRequest.body(), ChangePwdModelHelper.class);
			
			Integer identification = Integer.parseInt(pRequest.params("identification"));
			String password = data.getPassword();
			String newPassword = data.getNewpassword();
			String newPasswordHashed = "";
			String savedPassword ="";
			String savedSalt= "";
			String passwordHashed = "";
			String newSalt = "";
			
			Document filter =  new Document();
			filter.append("identification", identification);
						
			ArrayList<Document> documents = DataBaseUtil.find(filter, "citizen");
			if (documents.isEmpty()){
				log.info("User Doesn�t Exists");
				throw new WrongUserOrPasswordException("User Doesn�t Exists", "400");				
			}
			
			savedPassword = documents.get(0).getString("password");
			savedSalt = documents.get(0).getString("salt");
			
			//create hashed password to validate with the password saved in the DB
			
			String[] hash = GeneralUtil.getHash(password, savedSalt);
			passwordHashed = hash[1];

			if (!passwordHashed.equals(savedPassword)){
				log.info("Wrong Password");
				throw new WrongUserOrPasswordException("Wrong Password", "400");
			}
			
			//create hashed NEW password and salt
			hash = GeneralUtil.getHash(newPassword, "");
			newPasswordHashed = hash[1];
			newSalt = hash[0];
			
			//organize value to change
			Map<String, Object> valuesToReplace = new HashMap<String, Object>();
			valuesToReplace.put("password", newPasswordHashed);
			valuesToReplace.put("salt", newSalt);
			
			//send salt and password to the register in the DB
			Document register = new Document(valuesToReplace);
			DataBaseUtil.update(filter, register, "citizen");
			
			//create array list to send as a parameter to the EmailNotifierSvc
			ArrayList<String> parametersEmail = new ArrayList<>();
			parametersEmail.add(documents.get(0).getString("email"));
			parametersEmail.add(newPassword);
			
			//Send Email
			EmailNotifierSvc sendPassword = new EmailNotifierSvc();
			sendPassword.send(EmailType.CHANGE, parametersEmail);
			
			response = messager.getOkMessage("Success");
			
		} 
		catch (WrongUserOrPasswordException e) {
			pResponse.status(400);
			response = messager.getNotOkMessage(e.getMessage());
		}
		
		catch (MongoClientException M) {
			// TODO: handle exception
			System.out.println("Success");
		}
		catch (MongoWriteException M) {
			// TODO: handle exception
			System.out.println("Mongo Exception");
		}

		
		
		pResponse.type("application/json");
		return response;

	}

}
