/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class Mayoralty {

	public static final String NAME = "name";
	public static final String DEPENDENCIES = "dependencies";
	public static final String PROCEDURES = "procedures";
	public static final String ADDRESS = "address";
	public static final String URL = "url";
	public static final String PHONE = "phone";
	public static final String STATE = "state";
	public static final String SCHEDULE = "schedule";
	public static final String SLUG = "slug";

	@SerializedName("_id")
	private String _id;
	private String name;
	private List<Dependency> dependencies;
	private List<String> procedures;
	private String address;
	private String url;
	private String phone;
	private String state;
	private String schedule;
	private String slugMayoralty;
	

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
	
	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	public List<String> getProcedures() {
		return procedures;
	}

	public void setProcedures(List<String> procedures) {
		this.procedures = procedures;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	public String getSlugMayoralty() {
		return slugMayoralty;
	}

	public void setSlugMayoralty(String slugMayoralty) {
		this.slugMayoralty = slugMayoralty;
	}
	
	public ArrayList<Document> dependenciesDocuments() {
		ArrayList<Document> dependenciesDocs = new ArrayList<Document>();
		if (!this.getDependencies().isEmpty()) {
			for (int i = 0; i < this.getDependencies().size(); i++) {
				dependenciesDocs.add(this.getDependencies().get(i).toDocument());
			}
		}
		return dependenciesDocs;

	}
	
	public ArrayList<String> proceduresDocuments() {
		ArrayList<String> proceduresDocs = new ArrayList<String>();
		if (!this.getProcedures().isEmpty()) {
			for (int i = 0; i < this.getProcedures().size(); i++) {
				proceduresDocs.add(this.getProcedures().get(i));
			}
		}
		return proceduresDocs;

	}

	public Document toDocument() {
		Document mayoralty = new Document();
		mayoralty.append(NAME, this.getName())
				.append(DEPENDENCIES, dependenciesDocuments())
				.append(PROCEDURES, proceduresDocuments())
				.append(ADDRESS, this.getAddress())
				.append(URL, this.getUrl())
				.append(PHONE, this.getPhone())
				.append(STATE, this.getState())
				.append(SLUG, this.getName().replace(" ", "").toLowerCase())
				.append(SCHEDULE, this.getSchedule());

		return mayoralty;
	}

	

}
