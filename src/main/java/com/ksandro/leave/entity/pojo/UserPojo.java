package com.ksandro.leave.entity.pojo;

public class UserPojo {

	private String name;
	private String lastName;
	private String mail;
	private String password;
	
	public UserPojo() {
		
	}
	
	
	public UserPojo(String name, String lastName, String mail, String password) {
		super();
		this.name = name;
		this.lastName = lastName;
		this.mail = mail;
		this.password = password;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
