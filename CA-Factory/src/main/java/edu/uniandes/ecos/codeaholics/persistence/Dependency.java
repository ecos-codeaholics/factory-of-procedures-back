/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import java.util.ArrayList;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class Dependency {

	public static final String NAME = "name";
	public static final String EXTENSION = "extension";
	public static final String FUNTIONARIES = "funtionaries";

	@SerializedName("_id")
	private String _id;
	private String name;
	private String extension;
	private ArrayList<Funtionary> funtionaries;

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

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public ArrayList<Funtionary> getFuntionaries() {
		return funtionaries;
	}

	public void setFuntionaries(ArrayList<Funtionary> funtionaries) {
		this.funtionaries = funtionaries;
	}

	public Document toDocument() {
		Document dependency = new Document();
		dependency.append(NAME, this.getName())
				  .append(EXTENSION, this.getExtension())
				  .append(FUNTIONARIES, funtionariesDocuments());

		return dependency;
	}

	public Document funtionariesDocuments() {
		Document funtionariesDocs = new Document();
		for (int i = 0; i < this.getFuntionaries().size(); i++) {
			funtionariesDocs.append("FUNTIONARY" + (i + 1), this.getFuntionaries().get(i).toDocument());
		}
		return funtionariesDocs;

	}

}
