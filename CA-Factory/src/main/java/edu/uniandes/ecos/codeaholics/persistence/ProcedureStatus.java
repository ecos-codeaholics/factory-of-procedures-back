/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

/*
 * Class: ProcedureStatus FunctionaryServices.java Original Author: @author
 * AOSORIO Description: Auxiliary class: catched the procedure status
 * returned in body Created: Oct 15, 2016 4:55:28 PM
 */
public class ProcedureStatus {

	String status;
	String comment;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	public String getStatusHistory() {
		String status = "";
		switch (this.status) {
		case "Anular":
			status = "Anulado";
			break;
		case "Aprobar":
			status = "Aprobado";
			break;
		case "Rechazar":
			status = "Rechazado";
			break;
		default:
			status= this.status;
			break;
		}
		return status;
	}
	public String getStatusProcedure(){
		String status = "";
		switch (this.status) {
		case "Anular":
			status = "Finalizado";
			break;
		case "Aprobar":
			status = "En proceso";
			break;
		case "Rechazar":
			status = "Finalizado";
			break;
		default:
			status= this.status;
			break;
		}
		return status;
	}

	public String getStatusActivity(){
		String status = "";
		switch (this.status) {
		case "Anular":
			status = "Finalizado";
			break;
		case "Aprobar":
			status = "En curso";
			break;
		case "Rechazar":
			status = "Finalizado";
			break;
		default:
			status= this.status;
			break;
		}
		return status;
	}
}
