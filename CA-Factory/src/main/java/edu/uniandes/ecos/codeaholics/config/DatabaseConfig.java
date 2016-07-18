package edu.uniandes.ecos.codeaholics.config;

import com.mongodb.ServerAddress;

import java.util.Arrays;
import java.util.List;

/**
 * Created by snaphuman on 6/6/16.
 */

public class DatabaseConfig {

	// Atributos

	// Database Environment:
	// Use "local" to localhost mongodb instance
	// Use "replica" to remote replica set mongodb instances
	public static final String DB_ENV = "local";

	// Local env attributes
	public static final String DB_SERVER = "localhost";
	public static final int DB_SERVER_PORT = 27017;

	// Remote replica set attributes
	public static final List<ServerAddress> DB_REPLICA_SET = Arrays.asList(
			new ServerAddress("172.24.98.202", 27017),
			new ServerAddress("172.24.98.203", 27017)
	);
	
	public static final String DB_NAME = "factory";
}
