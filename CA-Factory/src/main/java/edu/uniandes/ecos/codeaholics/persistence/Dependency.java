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
	public static final String FUNCTIONARIES = "functionaries";

	@SerializedName("_id")
	private String _id;
	private String name;
	private String extension;
	private ArrayList<Functionary> functionaries;

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

	public ArrayList<Functionary> getFunctionaries() {
		return functionaries;
	}

	public void setFunctionaries(ArrayList<Functionary> functionaries) {
		this.functionaries = functionaries;
	}
	
	public ArrayList<Document> functionariesDocuments() {
		ArrayList<Document> functionariesDocs = new ArrayList<Document>();
		if (!this.getFunctionaries().isEmpty()) {
			for (int i = 0; i < this.getFunctionaries().size(); i++) {
				functionariesDocs.add(this.getFunctionaries().get(i).toDocument());
			}
		}
		return functionariesDocs;
	}

	public Document toDocument() {
		Document dependency = new Document();
		dependency.append(NAME, this.getName())
				  .append(EXTENSION, this.getExtension())
				  .append(FUNCTIONARIES, functionariesDocuments());

		return dependency;
	}

}
