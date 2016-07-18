package edu.uniandes.ecos.codeaholics.persistence;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by davidMtz22 on 18/07/2016.
 */
public class Citizen {

	public static final String IDENTIFICATION = "identification";
	public static final String NAME = "name";
	public static final String LASTNAME1 = "lastName1";
	public static final String LASTNAME2 = "lastName2";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String SALT = "salt";
	public static final String BIRTHDATE = "birthDate";

	@SerializedName("_id")
	private String _id;
	private Integer identificationNumber;
	private String name;
	private String lastName1;
	private String lastName2;
	private String email;
	private String password;
	private String salt;
	private Date birthDate;

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public Integer getIdentificationNumber() {
		return identificationNumber;
	}

	public void setIdentificationNumber(Integer identificationNumber) {
		this.identificationNumber = identificationNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName1() {
		return lastName1;
	}

	public void setLastName1(String lastName1) {
		this.lastName1 = lastName1;
	}

	public String getLastName2() {
		return lastName2;
	}

	public void setLastName2(String lastName2) {
		this.lastName2 = lastName2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getBirthDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		formatter.applyPattern(String.valueOf(birthDate));
		System.out.println(birthDate);
		try {
			this.birthDate = formatter.parse(String.valueOf(birthDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
}
