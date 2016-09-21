/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 19/09/2016.
 */
public class FieldAttribute {

	public static final String SUBTYPE = "subType";
	public static final String NAME = "name";
	public static final String PLACEHOLDER = "placeHolder";
	public static final String REQUIRED = "required";

	@SerializedName("_id")
	private String _id;
	private String subType;
	private String name;
	private Boolean required;
	private String placeHolder;
	

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public String getPlaceHolder() {
		return placeHolder;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

	public Document toDocument() {
		Document fieldAttribute = new Document();
		fieldAttribute.append(SUBTYPE, this.getSubType());
		fieldAttribute.append(NAME, this.getName());
		fieldAttribute.append(PLACEHOLDER, this.getPlaceHolder());
		fieldAttribute.append(REQUIRED, this.getRequired());

		return fieldAttribute;
	}

}
