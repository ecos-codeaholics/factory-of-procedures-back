/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 19/09/2016.
 */
public class Activity {

	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String DEPENDENCY = "dependency";
	public static final String GENERATED = "generated";
	// JLRM
	public static final String STEP = "step";
	public static final String FUNCTIONARY = "functionary";
	public static final String STATUS = "status";

	@SerializedName("_id")
	private String _id;
	private String name;
	private String description;
	private String dependency;
	private int step;
	private String functionary;
	private String aprobacion;
	private String status;

	public Activity(String name, String description, String dependency, int step, String functionary, 
			String status) {
		this.name = name;
		this.description = description;
		this.dependency = dependency;
		this.step = step;
		this.functionary = functionary;
		this.status = status;
	}
	public Activity() {
		// TODO Auto-generated constructor stub
	}

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

	public String getDependency() {
		return dependency;
	}

	public void setDependency(String dependency) {
		this.dependency = dependency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getFunctionary() {
		return functionary;
	}

	public void setFunctionary(String functionary) {
		this.functionary = functionary;
	}

	public String getAprobacion() {
		return aprobacion;
	}

	public void setAprobacion(String aprobacion) {
		this.aprobacion = aprobacion;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Document toDocument() {
		Document activity = new Document();
		activity.append(STEP, this.getStep());
		activity.append(NAME, this.getName());
		activity.append(DESCRIPTION, this.getDescription());
		activity.append(DEPENDENCY, this.getDependency());
		activity.append(FUNCTIONARY, this.getFunctionary());
		activity.append(STATUS, this.getStatus());
		// activity.append(GENERATED, generatedDocuments());

		return activity;
	}
}
