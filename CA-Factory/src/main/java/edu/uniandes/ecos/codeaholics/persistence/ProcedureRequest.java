/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import java.util.ArrayList;
import java.util.Date;

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
	public static final String PROCEDUREDATA = "procedureData";
	public static final String DELIVERYDOCS = "deliveryDocs";
	public static final String STEPS = "steps";
	public static final String STARTDATE = "startDate";
	public static final String FINISHDATE = "finishDate";
	public static final String STATUS = "status";

	@SerializedName("_id")
	private String _id;
	private String procedureClass;
	private Long fileNumber;
	private Citizen citizen;
	private String mayoralty;
	private Document procedureData;
	private Document deliveryDocs;
	private Document steps;
	private Date startDate;
	private Date finishDate;
	private String status;

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

	public String getMayoralty() {
		return mayoralty;
	}

	public void setMayoralty(String mayoralty) {
		this.mayoralty = mayoralty;
	}

	public Document getProcedureData() {
		return procedureData;
	}

	public void setProcedureData(Document procedureData) {
		this.procedureData = procedureData;
	}

	public Document getDeliveryDocs() {
		return deliveryDocs;
	}

	public void setDeliveryDocs(Document deliveryDocs) {
		this.deliveryDocs = deliveryDocs;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Document getSteps() {
		return steps;
	}

	public void setSteps(Document steps) {
		this.steps = steps;
	}

	public Document toDocument() {
		Document procedureRequest = new Document();
		procedureRequest.append(CLASS, this.getProcedureClass());
		procedureRequest.append(FILENUMBER, this.getFileNumber());
		procedureRequest.append(CITIZEN, this.getCitizen().toDocument());
		procedureRequest.append(MAYORALTY, this.getMayoralty());
		procedureRequest.append(PROCEDUREDATA, this.getProcedureData());
		procedureRequest.append(DELIVERYDOCS, this.getDeliveryDocs());
		procedureRequest.append(STEPS, this.getSteps());
		procedureRequest.append(STARTDATE, this.getStartDate());
		procedureRequest.append(FINISHDATE, this.getFinishDate());
		procedureRequest.append(STATUS, this.getStatus());

		return procedureRequest;
	}
	
	public Document deliveryDocuments() {
		Document deliveryDocs = new Document();
		for (int i = 0; i < this.getDeliveryDocs().size(); i++) {
			deliveryDocs.append("DELIVERYDOC" + (i + 1), this.getDeliveryDocs().get(i));
		}
		return deliveryDocs;

	}


}
