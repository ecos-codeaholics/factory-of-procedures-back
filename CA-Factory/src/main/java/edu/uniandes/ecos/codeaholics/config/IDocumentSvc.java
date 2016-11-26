/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;
import javax.servlet.http.HttpServletResponse;

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
import spark.Response;

public interface IDocumentSvc {

	/** Upload file method
	 * @param pRequest
	 */
	public void uploadDocument(Request pRequest);
	

	/** Download file method
	 * @param pRequest
	 * @param pResponse
	 * @return A Http servlet responde ( downloaded file )
	 */         
	public HttpServletResponse downloadDocument(Request pRequest, Response pResponse);
	
	/**
	 * 
	 */
	public String listDocuments();


	HttpServletResponse downloadDocument(String locationDir, String name, HttpServletResponse pResponse);
	
}
