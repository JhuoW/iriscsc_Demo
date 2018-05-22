package com.xzmc.airuishi.bean;

import java.io.Serializable;

public class Comment implements Serializable{
	private String userId;
	private String nickname;
	private String picture;
	private String authority;
	private String content;
	private String time;
	private String id;
	private String status;
	private String acceptId;
	private String tonickname;
	private String topicture;
	private String toauthority;
	private String contentId;
	public String getToauthority() {
		return toauthority;
	}
	public void setToauthority(String toauthority) {
		this.toauthority = toauthority;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAcceptId() {
		return acceptId;
	}
	public void setAcceptId(String acceptId) {
		this.acceptId = acceptId;
	}
	public String getTonickname() {
		return tonickname;
	}
	public void setTonickname(String tonickname) {
		this.tonickname = tonickname;
	}
	public String getTopicture() {
		return topicture;
	}
	public void setTopicture(String topicture) {
		this.topicture = topicture;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	
	

}
