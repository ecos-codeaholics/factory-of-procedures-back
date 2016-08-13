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
	private static DatabaseSingleton instance      = null;
	private static MongoClient       mongoClient   = null;
	private static MongoDatabase     mongoDatabase = null;

	// Constructores
	protected DatabaseSingleton() {
		
		DatabaseConfig dbConf = new DatabaseConfig("src/main/resources/config.properties");
		
		String env = dbConf.getDbEnv();
		
		if (env.equals("replica")){
			mongoClient = new MongoClient(dbConf.getDbServerAdresses());
			mongoDatabase = mongoClient.getDatabase(dbConf.getDbName());
		} else if (env.equals("local")) {
			mongoClient = new MongoClient(dbConf.getDbServerUrl(), Integer.parseInt(dbConf.getDbPort()));
			mongoDatabase = mongoClient.getDatabase(dbConf.getDbName());
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
