package com.xzmc.airuishi.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.bean.QXUser;

public class PreferenceMap {
	public static final String NOTIFY_WHEN_NEWS = "notifyWhenNews";
	public static final String VOICE_NOTIFY = "voiceNotify";
	public static final String VIBRATE_NOTIFY = "vibrateNotify";
	public static final String ISREMENBER = "isremenber";
	public static final String ISPUSH = "ispush";
	public static final String ACCOUNT = "account";
	public static final String PASSWORD = "password";

	public static final String NAME = "curusername";
	public static final String SEX = "curusersex";
	public static final String ID = "curuserid";
	public static final String IMAGE = "curuserimage";
	public static final String ADDRESS = "curuseraddress";
	public static final String AUTHORITY = "curuserauthority";
	
	public static final String IsCustomer = "IsCustomer";
	public static final String IsOpto = "IsOpto";
	public static final String IsBusiness = "IsBusiness";
	public static final String IsStudent = "IsStudent";
	public static final String IsSuperOpto = "IsSuperOpto";
	
	Context cxt;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	public static PreferenceMap currentUserPreferenceMap;

	public PreferenceMap(Context cxt) {
		this.cxt = cxt;
		pref = PreferenceManager.getDefaultSharedPreferences(cxt);
		editor = pref.edit();
	}

	public PreferenceMap(Context cxt, String prefName) {
		this.cxt = cxt;
		pref = cxt.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		editor = pref.edit();
	}

	// public static PreferenceMap getCurUserPrefDao(Context ctx) {
	// if (currentUserPreferenceMap == null) {
	// currentUserPreferenceMap = new PreferenceMap(ctx,
	// User.getCurrentUserId());
	// }
	// return currentUserPreferenceMap;
	// }

	public boolean isRemenberAccount() {
		return pref.getBoolean(ISREMENBER, false);
	}

	public void setIsRemenberAccount(Boolean flag) {
		editor.putBoolean(ISREMENBER, flag).commit();
	}

	public boolean isPush() {
		return pref.getBoolean(ISPUSH, true);
	}

	public void setIsPush(Boolean flag) {
		editor.putBoolean(ISPUSH, flag).commit();
	}

	public String getAccount() {
		return pref.getString(ACCOUNT, "");
	}

	public void setAccount(String account) {
		editor.putString(ACCOUNT, account).commit();
	}

	public String getPassword() {
		return pref.getString(PASSWORD, "");
	}

	public void setPassword(String password) {
		editor.putString(PASSWORD, password).commit();
	}

	public boolean isNotifyWhenNews() {
		return pref.getBoolean(NOTIFY_WHEN_NEWS, App.ctx.getResources()
				.getBoolean(R.bool.defaultNotifyWhenNews));
	}

	public void setNotifyWhenNews(boolean notifyWhenNews) {
		editor.putBoolean(NOTIFY_WHEN_NEWS, notifyWhenNews).commit();
	}

	boolean getBooleanByResId(int resId) {
		return App.ctx.getResources().getBoolean(resId);
	}

	public boolean isVoiceNotify() {
		return pref.getBoolean(VOICE_NOTIFY,
				getBooleanByResId(R.bool.defaultVoiceNotify));
	}

	public void setVoiceNotify(boolean voiceNotify) {
		editor.putBoolean(VOICE_NOTIFY, voiceNotify).commit();
	}

	public boolean isVibrateNotify() {
		return pref.getBoolean(VIBRATE_NOTIFY,
				getBooleanByResId(R.bool.defaultVibrateNotify));
	}

	public void setVibrateNotify(boolean vibrateNotify) {
		editor.putBoolean(VIBRATE_NOTIFY, vibrateNotify);
	}

	
	/**
	 * 	IsCustomer = "IsCustomer";
	IsOpto = "IsOpto";
	IsBusiness = "IsBusiness";
	IsStudent = "IsStudent";
	IsSuperOpto = "IsSuperOpto";
	 * @param user
	 */
	public void setUser(QXUser user) {
		editor.putString(NAME, user.getName()).commit();
		editor.putString(ID, user.getID()).commit();
		editor.putString(SEX, user.getSex()).commit();
		editor.putString(IMAGE, user.getImage()).commit();
		editor.putString(ADDRESS, user.getAddress()).commit();
		editor.putString(AUTHORITY, user.getAuthority()).commit();
		
		editor.putBoolean(IsCustomer, user.isIsCustomer()).commit();
		editor.putBoolean(IsOpto, user.isIsOpto()).commit();
		editor.putBoolean(IsBusiness,user.isIsBusiness()).commit();
		editor.putBoolean(IsStudent, user.isIsStudent()).commit();
		editor.putBoolean(IsSuperOpto, user.isIsSuperOpto()).commit();
	}

	public QXUser getUser() {
		QXUser user = new QXUser();
		user.setID(pref.getString(ID, ""));
		user.setImage(pref.getString(IMAGE, ""));
		user.setName(pref.getString(NAME, ""));
		user.setSex(pref.getString(SEX, ""));
		user.setAuthority(pref.getString(AUTHORITY, ""));
		user.setAddress(pref.getString(ADDRESS, ""));
		
		user.setIsCustomer(pref.getBoolean(IsCustomer, false));
		user.setIsOpto(pref.getBoolean(IsOpto, false));
		user.setIsBusiness(pref.getBoolean(IsBusiness, false));
		user.setIsStudent(pref.getBoolean(IsStudent, false));
		user.setIsSuperOpto(pref.getBoolean(IsSuperOpto, false));
		return user;
	}
}
