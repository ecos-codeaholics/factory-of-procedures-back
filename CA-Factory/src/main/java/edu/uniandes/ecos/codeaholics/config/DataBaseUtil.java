/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import org.bson.Document;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.List;

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
	 * @param pRegister
	 *            registro que se desea adicionar
	 * @param pCollection
	 *            colection de destino
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
	 * @param pFilter
	 *            docuemento patron de l busqueda
	 * @param pCollection
	 *            colection donde se va a realizar la busqueda
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
	 * @param pRegister
	 *            registro que se desea adicionar
	 * @param pCollection
	 *            colection de destino
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

	public static ArrayList<Document> getAll(String pCollection) {

		FindIterable<Document> query = db.getCollection(pCollection).find();
		ArrayList<Document> results = new ArrayList<Document>();
		for (Document document : query) {
			results.add(document);
		}

		log.info("-----------------------------------");
		log.info("Getting Procedures...");
		log.info("-----------------------------------");

		return results;

	}

	/**
	 * Actualiza un registro en la base de datos
	 * 
	 * @param pFilter
	 *            documento filtro
	 * @param pRegister
	 *            registro para actualizar
	 * @param pCollection
	 *            coleccion en donde actulizarlo
	 * @throws MongoWriteException
	 *             exception de mongo
	 */
	public static void update(Document pFilter, Document pRegister, String pCollection) throws MongoWriteException {
		// create JSON with the $SET parameter (It's use to update a register in
		// the DB)
		Document registerOperator = new Document();
		registerOperator.append("$set", pRegister);

		// get the collection
		MongoCollection<Document> collection = db.getCollection(pCollection);

		// update the DB
		log.debug("Updating " + pRegister);
		log.debug("In Collection " + pCollection);
		try {
			collection.updateOne(pFilter, registerOperator);
			log.info("-----------------------------------");
			log.info("Successful Updated");
			log.info("-----------------------------------");
		} catch (MongoException e) {
			log.info(e.getMessage());
			throw e;
		}
	}

	/**
	 * Actualiza un campo a partir de una consulta inclusiva con el operador $and
	 *
	 */

	public static void compositeUpdate(List<Document> pFilter, Document pRegister, String pCollection) throws MongoWriteException {

		Document filterOperator = new Document();
		filterOperator.append("$and", pFilter);

		Document registerOperator = new Document();
		registerOperator.append("$set", pRegister);

		MongoCollection<Document> collection = db.getCollection(pCollection);

		try {
		collection.updateOne(filterOperator, registerOperator);
		} catch (MongoException e) {
			log.info(e.getMessage());
			throw e;
		}
		
	}

}
