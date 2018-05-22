package com.xzmc.airuishi.bean; 

import java.util.List;

/** 
 * @author xiaobian 
 * @version 创建时间：2015年4月21日 下午7:42:09 
 * 
 */
public class Province {
	public String id;
	public String name;
	public List<City> city;
	public String sortLetters;

	@Override
	public String toString() {
		return "Province [id=" + id + ", name=" + name + ", city=" + city
				+ ", sortLetters=" + sortLetters + "]";
	}

	public List<City> getCity() {
		return city;
	}

	public void setCity(List<City> city) {
		this.city = city;
	}


	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getSortLetters() {
		return this.sortLetters;
	}

	public void setId(String string) {
		this.id = string;
	}

	public void setName(String paramString) {
		this.name = paramString;
	}

	public void setSortLetters(String paramString) {
		this.sortLetters = paramString;
	}
}