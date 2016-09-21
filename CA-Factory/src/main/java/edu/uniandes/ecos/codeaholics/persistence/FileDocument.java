/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 19/09/2016.
 */
public class FileDocument {

	public static final String NAME = "name";
	public static final String PATH = "path";

	@SerializedName("_id")
	private String _id;
	private String name;
	private String path;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Document toDocument() {
		Document fileDocument = new Document();
		fileDocument.append(NAME, this.getName());
		fileDocument.append(PATH, this.getPath());

		return fileDocument;
	}

}
