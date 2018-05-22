package com.xzmc.airuishi.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * @author xiaobian
 * @version ÂàõÂª∫Êó∂Èó¥Ôº?2015Âπ?4Êú?4Êó? ‰∏ãÂçà5:45:59
 * 
 */
public class ContactUser implements Parcelable {
	private String id;
	private String name;
	private String phone;
	private String image;
	private String statue;
	private String sortLetters;
	public ContactUser() {
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactUser other = (ContactUser) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ContactUser [id=" + id + ", name=" + name + ", phone=" + phone
				+ ", image=" + image + ", statue=" + statue + ", sortLetters="
				+ sortLetters + "]";
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getStatue() {
		return statue;
	}

	public void setStatue(String statue) {
		this.statue = statue;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String photo) {
		this.image = photo;
	}

	public static final Parcelable.Creator<ContactUser> CREATOR = new Creator<ContactUser>() {
		@Override
		public ContactUser[] newArray(int size) {
			return new ContactUser[size];
		}

		@Override
		public ContactUser createFromParcel(Parcel in) {
			return new ContactUser(in);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(image);
		dest.writeString(phone);
		dest.writeString(statue);
		dest.writeString(sortLetters);
	}

	private ContactUser(Parcel in) {
		id = in.readString();
		name = in.readString();
		image = in.readString();
		phone = in.readString();
		statue = in.readString();
		sortLetters = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
