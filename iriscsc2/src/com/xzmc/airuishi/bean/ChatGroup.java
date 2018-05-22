package com.xzmc.airuishi.bean;

import java.util.List;

/**
 * @author xiaobian
 * @version 创建时间�?2015�?4�?10�? 上午11:32:19
 * 
 */
public class ChatGroup {
	private QXUser createuser;
	private String id;
	private String name;
	private List<QXUser> member;


	public ChatGroup() {
	}

	@Override
	public String toString() {
		return "ChatGroup [createuser=" + createuser + ", id=" + id + ", name="
				+ name + ", member=" + member + "]";
	}

	public QXUser getCreateuser() {
		return createuser;
	}

	public void setCreateuser(QXUser createuser) {
		this.createuser = createuser;
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

	public List<QXUser> getMember() {
		return member;
	}

	public void setMember(List<QXUser> member) {
		this.member = member;
	}

}
