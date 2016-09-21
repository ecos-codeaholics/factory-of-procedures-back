/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class ProcedureRequest {

	public static final String CLASS = "class";
	public static final String FILENUMBER = "fileNumber";
	public static final String CITIZEN = "citizen";
	public static final String MAYORALTY = "mayoralty";

	@SerializedName("_id")
	private String _id;
	private String procedureClass;
	private Long fileNumber;
	private Citizen citizen;
	private Mayoralty mayoralty;

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}
	
	public String getProcedureClass() {
		return procedureClass;
	}

	public void setProcedureClass(String procedureClass) {
		this.procedureClass = procedureClass;
	}

	public Long getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(Long fileNumber) {
		this.fileNumber = fileNumber;
	}

	public Citizen getCitizen() {
		return citizen;
	}

	public void setCitizen(Citizen citizen) {
		this.citizen = citizen;
	}

	public Mayoralty getMayoralty() {
		return mayoralty;
	}

	public void setMayoralty(Mayoralty mayoralty) {
		this.mayoralty = mayoralty;
	}

	public Document toDocument() {
		Document procedureRequest = new Document();
		procedureRequest.append(CLASS, this.getProcedureClass());
		procedureRequest.append(FILENUMBER, this.getFileNumber());
		procedureRequest.append(CITIZEN, this.getCitizen().toDocument());
		procedureRequest.append(MAYORALTY, this.getMayoralty().toDocument());

		return procedureRequest;
	}

}
