/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;
/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: IDocumentSvc IDocumentSvc.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: This is the service to handle documents
 *  - for uploading
 *  - for downloading
 *  - for listing
 * 
 * Implementation: Interface - exposes the 3 essential services
 *
 * Created: Aug 20, 2016 10:05:44 AM
 * 
 */
import spark.Request;

public interface IDocumentSvc {

	/**
	 * @param pRequest
	 */
	public void uploadDocument(Request pRequest);
	
	/**
	 * 
	 */
	public void downloadDocument();
	
	/**
	 * 
	 */
	public void listDocuments();
	
}
