/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 19/09/2016.
 */
public class FormField {

	public static final String TYPE = "type";
	public static final String LABEL = "label";
	public static final String HELPTEXT = "helpText";
	public static final String REQUIRED = "required";
	public static final String FIELDATTRIBUTE = "fieldAttribute";
	public static final String FILEOPTIONS = "fieldOptions";
	public static final String FILEVALIDATION = "fieldValidation";

	public enum Type {
		number, date, text;
	}
	
	public enum SubType {
		text, email, password;
	}
	
	@SerializedName("_id")
	private String _id;
	private Type type;
	private String label;
	private String helpText;
	private FieldAttribute fieldAttribute;
	private FieldOptions fieldOptions;
	private FieldValidation fieldValidation;
	

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public FieldAttribute getFieldAttribute() {
		return fieldAttribute;
	}

	public void setFieldAttribute(FieldAttribute fieldAttribute) {
		this.fieldAttribute = fieldAttribute;
	}

	public FieldOptions getFieldOptions() {
		return fieldOptions;
	}

	public void setFieldOptions(FieldOptions fieldOptions) {
		this.fieldOptions = fieldOptions;
	}

	public FieldValidation getFieldValidation() {
		return fieldValidation;
	}

	public void setFieldValidation(FieldValidation fieldValidation) {
		this.fieldValidation = fieldValidation;
	}

	public Document toDocument() {
		Document procedure = new Document();
		procedure.append(TYPE, this.getType());
		procedure.append(LABEL, this.getLabel());
		procedure.append(HELPTEXT, this.getHelpText());
		procedure.append(FIELDATTRIBUTE, this.getFieldAttribute().toDocument());
		procedure.append(FILEOPTIONS, this.getFieldOptions().toDocument());
		procedure.append(FILEVALIDATION, this.getFieldValidation().toDocument());

		return procedure;
	}

}
