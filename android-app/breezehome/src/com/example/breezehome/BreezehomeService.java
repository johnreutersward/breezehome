package com.example.breezehome;

public class BreezehomeService {
	
	private String name;
	private String description;
	private String url;
	
	public BreezehomeService(String name, String description, String url) {
		this.name = name;
		this.description = description;
		this.url = url;
	}
	
	public String toString() {
		return name + " - " + description;
	}
	
	public String getUrl() {
		return url;
	}

}
