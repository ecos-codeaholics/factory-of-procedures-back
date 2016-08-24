/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;

public class DBSingletonTest {

	@Test
	public void testUnique() {
		
		Logger logger = LogManager.getRootLogger();
		
		DatabaseSingleton dbOne = null;
		DatabaseSingleton dbTwo = null;

		logger.info("getting singleton...");
		dbOne = DatabaseSingleton.getInstance();
		logger.info("...got singleton: " + dbOne);
		logger.info("getting singleton...");
		dbTwo = DatabaseSingleton.getInstance();
		logger.info("...got singleton: " + dbTwo);
		
		logger.info("checking singletons for equality");
		Assert.assertEquals(true, dbOne == dbTwo);
		
	}

}
