/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uniandes.ecos.codeaholics.config.DatabaseConfig;

/**
 * Package: edu.uniandes.ecos.codeaholics.test
 *
 * Class: DBConfigTest DBConfigTest.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 12, 2016 11:14:10 PM
 * 
 */
public class DBConfigTest {

	@Test
	public void dbConfTest() {
		
		DatabaseConfig dbConf = new DatabaseConfig("src/main/resources/config_test.properties");
		
		assertEquals("local", dbConf.getDbEnv());
		assertEquals("factory_test", dbConf.getDbName());
		assertEquals("27017", dbConf.getDbPort());
		assertEquals("localhost", dbConf.getDbServerUrl());
		assertEquals("127.1.1.1", dbConf.getDbReplicaSetIPs()[0]);	
		assertEquals("127.1.1.2", dbConf.getDbReplicaSetIPs()[1]);	
		assertEquals("127.1.1.3", dbConf.getDbReplicaSetIPs()[2]);	
		
	}

}
