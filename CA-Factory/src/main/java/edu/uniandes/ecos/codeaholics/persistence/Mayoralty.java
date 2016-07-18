package edu.uniandes.ecos.codeaholics.persistence;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class Mayoralty {

	public static final String NAME = "name";
	public static final String ADDRESS = "address";

	@SerializedName("_id")
	private String _id;
	private String name;
	private String address;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
