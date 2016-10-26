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
	public static final String PLACEHOLDER = "placeholder";
	public static final String REQUIRED = "required";
	public static final String SUBTYPE = "subtype";
	public static final String DESCRIPTION = "description";
	public static final String CLASSNAME = "className";
	public static final String NAME = "name";
	public static final String MAXLENGHT = "maxlenght";
	
	
	@SerializedName("_id")
	private String _id;
	private String type;
	private String label;
	private String placeHolder;
	private boolean required;
	private String subtype;
	private String description;
	private String classname;
	private String name;
	private int maxlenght;
	
	

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPlaceHolder() {
		return placeHolder;
	}

	public void setPlaceHolder(String helpText) {
		this.placeHolder = helpText;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxlenght() {
		return maxlenght;
	}

	public void setMaxlenght(int maxlenght) {
		this.maxlenght = maxlenght;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
	
	public Document toDocument() {
		Document procedure = new Document();
		procedure.append(TYPE, this.getType()); //Need to fix this - gson cannot convert this |JLRM by the moment i let it as String
		procedure.append(LABEL, this.getLabel());
		procedure.append(PLACEHOLDER, this.getPlaceHolder());
		procedure.append(REQUIRED, this.isRequired());
		procedure.append(SUBTYPE, this.getSubtype());
		procedure.append(DESCRIPTION, this.getDescription());
		procedure.append(CLASSNAME, this.getClassname());
		procedure.append(NAME, this.getName());
		procedure.append(MAXLENGHT, this.getMaxlenght());
		
		return procedure;
	}
}
