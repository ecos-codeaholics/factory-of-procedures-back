/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class Functionary extends Citizen {

	public static final String POSITION = "position";
	public static final String DEPENDENCY = "dependency";
	public static final String MAYORALTY = "mayoralty";

	@SerializedName("_id")
	private String _id;
	private String mayoralty;
	private String dependency;

	public Functionary() {
		
	}
	
	public Functionary(Citizen asCitizen) {
		super(asCitizen);
	}

	
	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getMayoralty() {
		return mayoralty;
	}

	public void setMayoralty(String mayoralty) {
		this.mayoralty = mayoralty;
	}
	
	public String getDependency() {
		return dependency;
	}

	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
	
	public Document toDocument() {
		Document functionary = new Document();
		functionary.append(IDENTIFICATION, this.getIdentification()).append(NAME, this.getName())
				.append(LASTNAME1, this.getLastName1()).append(LASTNAME2, this.getLastName2())
				.append(BIRTHDATE, this.getBirthDate()).append(EMAIL, this.getEmail()).append(SALT, this.getSalt())
				.append(PASSWORD, this.getPassword()).append(PROFILE, this.getUserProfile())
				.append(MAYORALTY, this.getMayoralty())
				.append(DEPENDENCY, this.getDependency());
							
		return functionary;
	}


}
