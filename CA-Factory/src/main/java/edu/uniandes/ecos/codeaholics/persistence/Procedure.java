/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class Procedure {

	public static final String NAME = "name";

	@SerializedName("_id")
	private String _id;
	private String name;

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

	public Document toDocument() {
		Document procedure = new Document();
		procedure.append(NAME, this.getName());

		return procedure;
	}

}
