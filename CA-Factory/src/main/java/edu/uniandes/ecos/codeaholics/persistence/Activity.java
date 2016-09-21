/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 19/09/2016.
 */
public class Activity {

	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String STARTDATE = "startDate";
	public static final String FINISHDATE = "finishDate";
	public static final String STATUS = "status";
	public static final String DEPENDENCY = "dependency";
	public static final String GENERATED = "generated";

	@SerializedName("_id")
	private String _id;
	private String name;
	private String description;
	private Date startDate;
	private Date finishDate;
	private String status;
	private Dependency dependency;
	private ArrayList<FileDocument> generated;

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Dependency getDependency() {
		return dependency;
	}

	public void setDependency(Dependency dependency) {
		this.dependency = dependency;
	}

	public ArrayList<FileDocument> getGenerated() {
		return generated;
	}

	public void setGenerated(ArrayList<FileDocument> generated) {
		this.generated = generated;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Document toDocument() {
		Document activity = new Document();
		activity.append(NAME, this.getName());
		activity.append(DESCRIPTION, this.getDescription());
		activity.append(STARTDATE, this.getStartDate());
		activity.append(FINISHDATE, this.getFinishDate());
		activity.append(STATUS, this.getStatus());
		activity.append(DEPENDENCY, this.getDependency().toDocument());
		activity.append(GENERATED, generatedDocuments());

		return activity;
	}
	
	public Document generatedDocuments() {
		Document generatedDocs = new Document();
		for (int i = 0; i < this.getGenerated().size(); i++) {
			generatedDocs.append("GENERATED" + (i + 1), this.getGenerated().get(i).toDocument());
		}
		return generatedDocs;

	}

}
