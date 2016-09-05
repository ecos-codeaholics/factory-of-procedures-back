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
 * Implementation: successInd : false (no problem) true (alarm)
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
		private boolean errorInd = false;
		@SuppressWarnings("unused")
		private String responseMsg;
		
		/**
		 * @param successInd the successInd to set
		 */
		public void setErrorInd(boolean errorInd) {
			this.errorInd = errorInd;
		}

		/**
		 * @param errorMsg the errorMsg to set
		 */
		public void setResponseMsg(String responseMsg) {
			this.responseMsg = responseMsg;
		}

	}
	
	@Override
	public Object getOkMessage(String message) {
		Response response = new Response();
		response.setErrorInd(false);
		response.setResponseMsg(message);
		return response;
	}


	@Override
	public Object getNotOkMessage(String message) {
		Response response = new Response();
		response.setErrorInd(true);
		response.setResponseMsg(message);
		return response;
	}

}
