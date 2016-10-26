/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sebastian Cardona C on 26/10/2016.
 */
public class History {

	public static final String STEP = "step";
	public static final String DATE = "date";
	public static final String USER = "user";
	public static final String STATUS = "status";
	public static final String DESCRIPTION = "description";

	@SerializedName("_id")
	private String _id;
	private int step;
	private String date;
	private String user;
	private String status;
	private String description;
	
	
	
	public History(int step, String date, String user, String status, String description) {
		this.step = step;
		this.date = date;
		this.user = user;
		this.status = status;
		this.description = description;
	}
	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() { return status; }

	public void setStatus(String status) {
		this.status = status;
	}

	public Document toDocument() {
		Document history = new Document();
		history.append(STEP, this.getStep());
		history.append(DATE, this.getDate());
		history.append(USER, this.getUser());
		history.append(STATUS, this.getStatus());
		history.append(DESCRIPTION, this.getDescription());

		return history;
	}
}
