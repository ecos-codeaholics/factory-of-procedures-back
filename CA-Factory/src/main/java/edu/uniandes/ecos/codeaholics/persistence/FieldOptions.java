/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 19/09/2016.
 */
public class FieldOptions {

	public static final String KEY = "key";
	public static final String VALUE = "value";
	public static final String OTHER = "other";
	public static final String MULTIPLE = "multiple";
	public static final String TOOGLE = "toogle";

	@SerializedName("_id")
	private String _id;
	private String key;
	private String value;
	private String other;
	private Boolean multiple;
	private Boolean toogle;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public Boolean getMultiple() {
		return multiple;
	}
	
	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}

	public Boolean getToogle() {
		return toogle;
	}

	public void setToogle(Boolean toogle) {
		this.toogle = toogle;
	}

	public Document toDocument() {
		Document fieldOptions = new Document();
		fieldOptions.append(KEY, this.getKey());
		fieldOptions.append(VALUE, this.getValue());
		fieldOptions.append(OTHER, this.getOther());
		fieldOptions.append(MULTIPLE, this.getMultiple());
		fieldOptions.append(TOOGLE, this.getToogle());

		return fieldOptions;
	}

}
