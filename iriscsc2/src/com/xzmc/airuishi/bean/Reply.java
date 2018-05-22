package com.xzmc.airuishi.bean;

import java.io.Serializable;

public class Reply implements Serializable  {
	private String id;          //回复的评论id
	private String sendName;   //发送评论人
	private String replyName;  //被回复人
	private String senduserId;
	private String replyuserId;
	private String content;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSendName() {
		return sendName;
	}
	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
	public String getReplyName() {
		return replyName;
	}
	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}
	public String getSenduserId() {
		return senduserId;
	}
	public void setSenduserId(String senduserId) {
		this.senduserId = senduserId;
	}
	public String getReplyuserId() {
		return replyuserId;
	}
	public void setReplyuserId(String replyuserId) {
		this.replyuserId = replyuserId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	

}
