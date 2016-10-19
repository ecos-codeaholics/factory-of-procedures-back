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
	public static final String MAYORALTY = "mayoralty";
	public static final String SLUG = "slug";

	@SerializedName("_id")
	private String _id;
	private String name;
	private ArrayList<Activity> activities;
	private ArrayList<RequiredUpload> requiredUpload;
	private ArrayList<FormField> fields;
	private String slugProcedure;

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

	public ArrayList<RequiredUpload> getRequired() {
		return requiredUpload;
	}

	public void setRequired(ArrayList<RequiredUpload> reqDocs) {
		this.requiredUpload = reqDocs;
	}

	public ArrayList<FormField> getFields() {
		return fields;
	}

	public void setFields(ArrayList<FormField> fields) {
		this.fields = fields;
	}
	
	public String getSlugProcedure() {
		return slugProcedure;
	}

	public void setSlugProcedure(String slugProcedure) {
		this.slugProcedure = slugProcedure;
	}

	public ArrayList<Document> activitiesDocuments() {
		ArrayList<Document> activitiesDocs = new ArrayList<Document>();
		if (!this.getActivities().isEmpty()) {
			for (int i = 0; i < this.getActivities().size(); i++) {
				activitiesDocs.add(this.getActivities().get(i).toDocument());
			}
		}
		return activitiesDocs;
	}

	public ArrayList<Document> requiredDocuments() {
		ArrayList<Document> requiredDocs = new ArrayList<Document>();
		if (!this.getRequired().isEmpty()) {
			for (int i = 0; i < this.getRequired().size(); i++) {
				requiredDocs.add(this.getRequired().get(i).toDocument());
			}
		}
		return requiredDocs;
	}

	public ArrayList<Document> fieldsDocuments() {
		ArrayList<Document> fieldsDocs = new ArrayList<Document>();
		if (!this.getFields().isEmpty()) {
			for (int i = 0; i < this.getFields().size(); i++) {
				fieldsDocs.add(this.getFields().get(i).toDocument());
			}
		}
		return fieldsDocs;
	}
	
	public Document toDocument() {
		Document procedure = new Document();
		procedure.append(NAME, getName());
		procedure.append(ACTIVITIES, activitiesDocuments());
		procedure.append(REQUIRED, requiredDocuments());
		procedure.append(SLUG, this.getName().replace(" ", ""));
		procedure.append(FIELDS, fieldsDocuments());

		return procedure;
	}

}
