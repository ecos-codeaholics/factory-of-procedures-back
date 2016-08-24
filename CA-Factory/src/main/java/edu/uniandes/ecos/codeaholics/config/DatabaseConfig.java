/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import com.mongodb.ServerAddress;

import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by snaphuman on 6/6/16.
 */

public class DatabaseConfig {

	// Atributos
	// Database Environment:
	// Use "local" to localhost mongodb instance
	// Use "replica" to remote replica set mongodb instances

	private static String dbServerUrl;
	private static String dbPort;
	private static String dbName;
	private static String dbEnv;
	private static String[] dbReplicaSetIPs;
	private static List<ServerAddress> dbServerAdresses;

	public DatabaseConfig(String pConfig) {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(pConfig);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			dbEnv = prop.getProperty("mongo.env");
			dbServerUrl = prop.getProperty("mongo.url");
			dbPort = prop.getProperty("mongo.port");
			dbName = prop.getProperty("mongo.db.name");			
			dbReplicaSetIPs = prop.getProperty("mongo.db.replicasetips").split(",");
					
			dbServerAdresses = new ArrayList<ServerAddress>();
			
			for( String ips : dbReplicaSetIPs) {
				dbServerAdresses.add( new ServerAddress(ips, 27017) );
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
			
			dbEnv = "local";
			dbServerUrl = "localhost";
			dbPort = "27017";
			dbName = "factory";

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return the dbServerUrl
	 */
	public String getDbServerUrl() {
		return dbServerUrl;
	}

	/**
	 * @return the dbPort
	 */
	public String getDbPort() {
		return dbPort;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @return the dbEnv
	 */
	public String getDbEnv() {
		return dbEnv;
	}

	/**
	 * @return the dbReplicaSetIPs
	 */
	public String[] getDbReplicaSetIPs() {
		return dbReplicaSetIPs;
	}

	/**
	 * @return the dbServerAdresses
	 */
	public List<ServerAddress> getDbServerAdresses() {
		return dbServerAdresses;
	}

}
