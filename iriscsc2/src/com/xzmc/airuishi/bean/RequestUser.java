package com.xzmc.airuishi.bean;

import java.io.Serializable;

/**
 * @author xiaobian
 * @version åˆ›å»ºæ—¶é—´ï¼?2015å¹?4æœ?2æ—? ä¸Šåˆ11:53:59
 */
public class RequestUser implements Serializable {
	private static final long serialVersionUID = 2L;
	private String ID;
	private String name;
	private String image;
	private String sex;
	private String Company;
	private String address;
	private String trade;
	private String status;
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTrade() {
		return trade;
	}

	public void setTrade(String trade) {
		this.trade = trade;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCompany() {
		return Company;
	}

	public void setCompany(String company) {
		Company = company;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
