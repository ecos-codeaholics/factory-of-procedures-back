/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import com.google.gson.Gson;

import edu.uniandes.ecos.codeaholics.persistence.RequiredDocument;
import spark.Request;

/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: DocumentSvc DocumentSvc.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Class implementing the document/file service
 * 
 * Implementation: Implements IDocumentSvc
 *
 * Created: Aug 20, 2016 10:27:12 AM
 * 
 */
public class DocumentSvc implements IDocumentSvc {

	private String answerStr = "";
		
	/* (non-Javadoc)
	 * @see edu.uniandes.ecos.codeaholics.config.IDocumentSvc#uploadDocument(Request pRequest)
	 */
	@Override
	public void uploadDocument(Request pRequest) {
		
		File uploadDir = new File(FileUtil.LOCAL_TMP_PATH);
		uploadDir.mkdir();
			
		pRequest.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
		
		try {
			
			Collection<Part> parts;
			parts = pRequest.raw().getParts();			
			Part fPart = parts.iterator().next();
			
			try (InputStream input = fPart.getInputStream()) {
				
				Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
				
				Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
				
				RequiredDocument rDoc;

				rDoc = new RequiredDocument();
				rDoc.setFilePath(FileUtil.LOCAL_TMP_PATH);
				rDoc.setTmpName(tempFile.getFileName().toString());
				rDoc.setOriginalName(fPart.getSubmittedFileName());
				rDoc.setFileSize(fPart.getSize());
				rDoc.setRadicado(123456);
				rDoc.setTimestamp(System.currentTimeMillis());

				Gson gson = new Gson();
				answerStr = gson.toJson(rDoc);
				System.out.println(answerStr);
				
			}
			
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/* (non-Javadoc)
	 * @see edu.uniandes.ecos.codeaholics.config.IDocumentSvc#downloadDocument()
	 */
	@Override
	public void downloadDocument() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.uniandes.ecos.codeaholics.config.IDocumentSvc#listDocuments()
	 */
	@Override
	public void listDocuments() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the answerStr
	 */
	public String getAnswerStr() {
		return answerStr;
	}

	
}
