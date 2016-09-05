/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.config;
/**
 * Package: edu.uniandes.ecos.codeaholics.config
 *
 * Class: IMessageSvc IMessageSvc.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Aug 23, 2016 4:01:41 PM
 * 
 */
public interface IMessageSvc {
	/**
	 * Envia el mensaje si se realizo la operacion correcta o incorrecta
	 * 
	 * @param message mensaje para enviar
	 * @return objeto con el mensaje
	 */
	public Object getOkMessage(String message);
	
	/**
	 * Envia el mensaje si se realizo la operacion correcta o incorrecta
	 * 
	 * @param message mensaje para enviar
	 * @return objeto con el mensaje
	 */
	public Object getNotOkMessage(String message);
}
