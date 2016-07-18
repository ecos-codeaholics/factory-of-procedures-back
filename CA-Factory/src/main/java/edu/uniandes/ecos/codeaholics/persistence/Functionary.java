package edu.uniandes.ecos.codeaholics.persistence;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class Functionary extends Citizen {

	public static final String POSITION = "position";
	public static final String PROFILE = "profile";

	@SerializedName("_id")
	private String _id;
	private String position;
	private String profile;
	private Mayoralty mayoralty;

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Mayoralty getMayoralty() {
		return mayoralty;
	}

	public void setMayoralty(Mayoralty mayoralty) {
		this.mayoralty = mayoralty;
	}
}
