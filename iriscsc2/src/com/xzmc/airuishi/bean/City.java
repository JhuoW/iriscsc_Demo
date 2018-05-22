package com.xzmc.airuishi.bean;

public class City {
	
	private String id;
	private String name;
	private String sortLetters;
	public City(String id,String name) {
		this.id=id;
		this.name=name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", sortLetters="
				+ sortLetters + "]";
	}

	
}