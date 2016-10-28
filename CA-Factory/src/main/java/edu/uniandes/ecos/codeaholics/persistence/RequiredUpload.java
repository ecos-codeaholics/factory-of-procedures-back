/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Package: edu.uniandes.ecos.codeaholics.main
 *
 * Class: Required Required.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: [one line class summary]
 * 
 * Implementation: [Notes on implementation]
 *
 * Created: Sep 26, 2016 7:37:33 PM
 * 
 */
public class RequiredUpload {
	public static final String TYPE = "type";
	public static final String REQUIRED = "required";
	public static final String LABEL = "label";
	public static final String DESCRIPTION = "description";
	public static final String CLASSNAME = "className";
	public static final String NAME = "name";
	
	@SerializedName("_id")
	private String type;
	private boolean required;
	private String label;
	private String description;
	private String className;
	private String name;

//	//JLRM datos para el formato del campo subir archivo al formulario
//	public Required() {
//		// TODO Auto-generated constructor stub
//		type = "file";
//		required = true;
//		className = "form-control";		
//	}
//	
	
	
	public RequiredUpload(String type, boolean required, String label, String description, String className,
			String name) {
		this.type = type;
		this.required = required;
		this.label = label;
		this.description = description;
		this.className = className;
		this.name = name;
	}
	public RequiredUpload() {
		// TODO Auto-generated constructor stub
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setDescription(String description){
		this.description = description;
	}
	public String getDescription() {
		return description;		
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public Document toDocument() {
		Document procedure = new Document();
		procedure.append(TYPE, this.getType());
		procedure.append(REQUIRED, this.isRequired());
		procedure.append(LABEL, this.getLabel());
		procedure.append(DESCRIPTION, this.getDescription());
		procedure.append(CLASSNAME, this.getClassName());
		procedure.append(NAME, this.getName());			
		return procedure;
	}
}
