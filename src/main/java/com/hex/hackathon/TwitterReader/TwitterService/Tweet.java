package com.hex.hackathon.TwitterReader.TwitterService;

import java.util.HashMap;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hex.hackathon.TwitterReader.Beans.CategoriesBean;
import com.hex.hackathon.TwitterReader.Beans.CategoryBean;

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
