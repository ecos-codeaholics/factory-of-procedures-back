/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import java.util.ArrayList;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 19/09/2016.
 */
public class Procedure {

	public static final String NAME = "name";
	public static final String ACTIVITIES = "activities";
	public static final String REQUIRED = "required";
	public static final String FIELDS = "fields";

	@SerializedName("_id")
	private String _id;
	private String name;
	private ArrayList<Activity> activities;
	private ArrayList<String> required;
	private ArrayList<FormField> fields;

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

	public ArrayList<Activity> getActivities() {
		return activities;
	}

	public void setActivities(ArrayList<Activity> activities) {
		this.activities = activities;
	}

	public ArrayList<String> getRequired() {
		return required;
	}

	public void setRequired(ArrayList<String> requiredDocs) {
		this.required = requiredDocs;
	}

	public ArrayList<FormField> getFields() {
		return fields;
	}

	public void setFields(ArrayList<FormField> fields) {
		this.fields = fields;
	}

	public Document activitiesDocuments() {
		Document activitiesDocs = new Document();
		for (int i = 0; i < this.getActivities().size(); i++) {
			activitiesDocs.append("ACTIVITY" + (i + 1), this.getActivities().get(i).toDocument());
		}
		return activitiesDocs;

	}

	public Document requiredDocuments() {
		System.out.println(this.getRequired().size());
		Document requiredDocs = new Document();
		for (int i = 0; i < this.getRequired().size(); i++) {
			requiredDocs.append("REQUIRED" + (i + 1), this.getRequired().get(i));
		}
		return requiredDocs;

	}

	public Document fieldsDocuments() {
		Document fieldsDocs = new Document();
		if (!this.getFields().isEmpty()) {
			for (int i = 0; i < this.getFields().size(); i++) {
				fieldsDocs.append("FIELD" + (i + 1), this.getFields().get(i).toDocument());
			}
		}
		return fieldsDocs;

	}

	public Document toDocument() {
		Document procedure = new Document();
		procedure.append(NAME, getName());
		procedure.append(ACTIVITIES, activitiesDocuments());
		procedure.append(REQUIRED, requiredDocuments());
		procedure.append(FIELDS, fieldsDocuments());

		return procedure;
	}

}
