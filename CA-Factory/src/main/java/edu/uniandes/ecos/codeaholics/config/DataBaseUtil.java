package edu.uniandes.ecos.codeaholics.config;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Created by davidMtz on 27/6/16.
 */
public final class DataBaseUtil {
	
	// Atributos
	private final static Logger log = LogManager.getLogger(DataBaseUtil.class);
	
	private static MongoDatabase db = DatabaseSingleton.getInstance().getDatabase();
	
	// Metodos
	/***
	 * Adiciona el Documentos en la coleccion especificada.
	 * 
	 * @param pRegister registro que se desea adicionar
	 * @param pCollection colection de destino
	 */
	public static void save(Document pRegister, String pCollection) throws MongoWriteException {
		
		log.debug("Saving " + pRegister);
		log.debug("In Collection " + pCollection);
		MongoCollection<Document> collection = db.getCollection(pCollection);
		collection.insertOne(pRegister);
		log.info("-----------------------------------");
		log.info("Successful Insert");
		log.info("-----------------------------------");
	}
	
	/***
	 * Busca todos los documentos de iguales atributos que el filtro.
	 * 
	 * @param pFilter docuemento patron de l busqueda
	 * @param pCollection colection donde se va a realizar la busqueda
	 */
	public static ArrayList<Document> find(Document pFilter, String pCollection) {

		FindIterable<Document> query = db.getCollection(pCollection).find(pFilter);
		ArrayList<Document> results = new ArrayList<Document>();
		for (Document document : query) {
			results.add(document);
		}
		return results;
	}
	
	/***
	 * Adiciona el Documentos en la coleccion especificada.
	 * 
	 * @param pRegister registro que se desea adicionar
	 * @param pCollection colection de destino
	 */
	public static void delete(Document pRegister, String pCollection) throws MongoWriteException {
		
		log.debug("Deleting " + pRegister);
		log.debug("In Collection " + pCollection);
		MongoCollection<Document> collection = db.getCollection(pCollection);
		collection.deleteOne(pRegister);
		log.info("-----------------------------------");
		log.info("Successful Delete");
		log.info("-----------------------------------");
	}

}
