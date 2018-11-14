package com.hex.hackathon.TwitterReader.TwitterService;


public class Tweet {
 
	private String userName;
	private String message;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Tweet(String userName, String message) {
		super();
		this.userName = userName;
		this.message = message;
	}
	public Tweet() {
		super();
	}
	@Override
	public String toString() {
		return "Tweet [userName=" + userName + ", message=" + message + "]";
	}
	
	
}
