/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: SessionManager SessionManager.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Oct 28, 2016 5:00:46 PM
 * 
 */
public class SessionManager {

	public static void test1() {
		
		Runnable runnable = () -> {
			String threadName = Thread.currentThread().getName();
			System.out.println("Hello " + threadName);
		};

		runnable.run();

		Thread thread = new Thread(runnable);
		thread.start();

		System.out.println("Done!");
	}
	
}
