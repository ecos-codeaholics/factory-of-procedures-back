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
public class FieldValidation {

	public static final String MAXLENGHT = "maxLenght";
	public static final String SIZE = "size";
	public static final String DATEFORMAT = "dateFormat";
	public static final String FILETYPE = "fileType";

	@SerializedName("_id")
	private String _id;
	private Boolean maxLenght;
	private Integer size;
	private String dateFormat;
	private ArrayList<String> fileType;
	

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public Boolean getMaxLenght() {
		return maxLenght;
	}

	public void setMaxLenght(Boolean maxLenght) {
		this.maxLenght = maxLenght;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public ArrayList<String> getFileType() {
		return fileType;
	}

	public void setFileType(ArrayList<String> fileType) {
		this.fileType = fileType;
	}

	public Document toDocument() {
		Document fieldValidation = new Document();
		fieldValidation.append(MAXLENGHT, this.getMaxLenght());
		fieldValidation.append(SIZE, this.getSize());
		fieldValidation.append(DATEFORMAT, this.getDateFormat());
		fieldValidation.append(FILETYPE, this.getFileType());

		return fieldValidation;
	}

}
