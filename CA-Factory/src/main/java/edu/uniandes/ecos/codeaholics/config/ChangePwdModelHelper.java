package edu.uniandes.ecos.codeaholics.config;


import org.bson.Document;

import com.google.gson.annotations.SerializedName;

public class ChangePwdModelHelper {
	
	//private final static String IDENTIFICATION = "identification";
	private final static String PASSWORD = "password";
	private final static String NEWPASSWORD = "newpassword";
	
	@SerializedName("_id")
	private String _id;
	//private Integer identification;
	private String password;
	private String newpassword;			
	

//	public Integer getIdentification () {
//		return identification;
//	}
	
	public String getPassword () {
		return password;
	}
	
	public String getNewpassword () {
		return newpassword;
	}
	
//	public void setIdentification (Integer identification) {
//		this.identification = identification;				
//	}
	
	public void setPassword (String password){
		this.password = password;				
	}
	
	public void setNewpassword (String newpasssword) {
		this.newpassword = newpasssword;				
	}
	
	public Document toDocument() {
		Document changePwd = new Document();
		changePwd.append(PASSWORD, this.getPassword())
				 .append(NEWPASSWORD, this.getNewpassword());
		return changePwd;
	}
	

}
