package com.example.breezehome;

public class BreezehomeService {
	
	private String name;
	private String description;
	private String url;
	private boolean admin;
	private String original;
	
	public BreezehomeService(String name, String description, String url, boolean admin, String original) {
		this.name = name;
		this.description = description;
		this.url = url;
		this.admin = admin;
		this.original = original;
	}
	
	public String toString() {
		if (admin) {
			return name + " - " + description + " (admin)";
		}
		return name + " - " + description;
	}
	
	public String getUrl() {
		return url;
	}

}
