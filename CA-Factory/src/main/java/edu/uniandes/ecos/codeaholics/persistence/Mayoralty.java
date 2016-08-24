/** Copyright or License
 *
 */

package edu.uniandes.ecos.codeaholics.persistence;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class Mayoralty {

	public static final String NAME = "name";
	public static final String ADDRESS = "address";
	public static final String URL = "url";
	public static final String PHONE = "phone";

	@SerializedName("_id")
	private String _id;
	private String name;
	private String address;
	private String url;
	private String phone;

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
	
	public Document toDocument() {
		Document mayoralty = new Document();
		mayoralty.append(NAME, this.getName())
				.append(ADDRESS, this.getAddress())
				.append(URL, this.getUrl())
				.append(PHONE, this.getPhone());

		return mayoralty;
	}

}
