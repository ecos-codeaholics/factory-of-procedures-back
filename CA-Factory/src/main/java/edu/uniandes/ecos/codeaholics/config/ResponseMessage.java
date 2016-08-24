/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;
/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: ResponseMessage ResponseMessage.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 23, 2016 4:02:21 PM
 * 
 */
public class ResponseMessage implements IMessageSvc {

	public enum Levels {
	    DEBUG, ERROR, OK, INFO, WARNING;
	}
	
	public class Response {
		
		@SuppressWarnings("unused")
		private boolean successInd = false;
		@SuppressWarnings("unused")
		private String errorMsg;
		
		/**
		 * @param successInd the successInd to set
		 */
		public void setSuccessInd(boolean successInd) {
			this.successInd = successInd;
		}

		/**
		 * @param errorMsg the errorMsg to set
		 */
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

	}
	
	@Override
	public Object getOkMessage(String message) {
		Response response = new Response();
		response.setSuccessInd(true);
		response.setErrorMsg(message);
		return response;
	}


	@Override
	public Object getNotOkMessage(String message) {
		Response response = new Response();
		response.setSuccessInd(false);
		response.setErrorMsg(message);
		return response;
	}

}
