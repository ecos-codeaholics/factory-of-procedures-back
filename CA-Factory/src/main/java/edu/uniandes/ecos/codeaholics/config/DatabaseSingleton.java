/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;
/**
 * Package: config
 *
 * Class: DatabaseSingleton DatabaseSingleton.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Database singleton 
 * 
 * Implementation: Configuration taken from DatabaseConfig
 *
 * Created: Jun 8, 2016 8:15:14 AM
 * 
 */

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DatabaseSingleton {

	// Atributos
	private static DatabaseSingleton instance = null;
	
	private static MongoClient mongoClient = null;
	
	private static MongoDatabase mongoDatabase = null;

	// Constructores
	protected DatabaseSingleton() {
		String env = DatabaseConfig.DB_ENV;
		if (env == "replica") {
			mongoClient = new MongoClient(DatabaseConfig.DB_REPLICA_SET);
			mongoDatabase = mongoClient.getDatabase(DatabaseConfig.DB_NAME);
		} else if (env == "local") {
			mongoClient = new MongoClient(DatabaseConfig.DB_SERVER, DatabaseConfig.DB_SERVER_PORT);
			mongoDatabase = mongoClient.getDatabase(DatabaseConfig.DB_NAME);
		}
	}

	// Metodos
	public static DatabaseSingleton getInstance() {
		if (instance == null) {
			instance = new DatabaseSingleton();
		}
		return instance;
	}

	public MongoDatabase getDatabase() {
		return mongoDatabase;
	}

}
