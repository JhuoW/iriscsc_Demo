package com.xzmc.airuishi.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class QXUser implements Parcelable {
	private String ID;
	private String name;
	private String image;
	private String sex;
	private String address;
	private String authority;
	private String isProfessor;
	private String professorInfo;
	private String onlineTime;
	private boolean IsCustomer;
	private boolean IsOpto;
	private boolean IsBusiness;
	private boolean IsStudent;
	private boolean IsSuperOpto;

	
	
	public boolean isIsCustomer() {
		return IsCustomer;
	}
	public void setIsCustomer(boolean isCustomer) {
		IsCustomer = isCustomer;
	}
	public boolean isIsOpto() {
		return IsOpto;
	}
	public void setIsOpto(boolean isOpto) {
		IsOpto = isOpto;
	}
	public boolean isIsBusiness() {
		return IsBusiness;
	}
	public void setIsBusiness(boolean isBusiness) {
		IsBusiness = isBusiness;
	}
	public boolean isIsStudent() {
		return IsStudent;
	}
	public void setIsStudent(boolean isStudent) {
		IsStudent = isStudent;
	}
	public boolean isIsSuperOpto() {
		return IsSuperOpto;
	}
	public void setIsSuperOpto(boolean isSuperOpto) {
		IsSuperOpto = isSuperOpto;
	}
	public String getOnlineTime() {
		return onlineTime;
	}
	public void setOnlineTime(String onlineTime) {
		this.onlineTime = onlineTime;
	}
	public String getProfessorInfo() {
		return professorInfo;
	}
	public void setProfessorInfo(String professorInfo) {
		this.professorInfo = professorInfo;
	}
	public String getIsProfessor() {
		return isProfessor;
	}
	public void setIsProfessor(String isProfessor) {
		this.isProfessor = isProfessor;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public QXUser() {
	}
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<QXUser> CREATOR = new Creator<QXUser>() {
		@Override
		public QXUser[] newArray(int size) {
			return new QXUser[size];
		}

		@Override
		public QXUser createFromParcel(Parcel in) {
			return new QXUser(in);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ID);
		dest.writeString(name);
		dest.writeString(image);
		dest.writeString(sex);
		dest.writeString(address);
	}

	private QXUser(Parcel in) {
		ID = in.readString();
		name = in.readString();
		image = in.readString();
		sex = in.readString();
		address = in.readString();
	}
}
