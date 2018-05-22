package com.xzmc.airuishi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.easemob.chatuidemo.domain.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.db.Profess;
import com.xzmc.airuishi.db.ProfessDBHelper;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.qixinplus.listener.ICallBack;


public class UserService {
	public static final int ORDER_UPDATED_AT = 1;
	public static final int ORDER_DISTANCE = 0;
	public static ImageLoader imageLoader = ImageLoader.getInstance();


	public static List<QXUser> findFriends(Context context) throws Exception {
		ProfessDBHelper dbHelper = new ProfessDBHelper(context);
		QXUser curUser = new PreferenceMap(App.ctx).getUser();
		Map<String, String> param = new HashMap<String, String>();
		param.put("usermd5", curUser.getID());
		String jsonstr = new WebService(C.GETFRIEND, param).getReturnInfo();
		JSONObject json = new JSONObject(jsonstr);
		if (json.get("ret").equals("success")) {
			JSONArray jsonarray = json.getJSONArray("userFriendList");
			dbHelper.deleteAllDataFromTable(Profess.TABLENAME);
			dbHelper.insertintoProfess(jsonarray);;
		}
		List<QXUser> friends = new ArrayList<QXUser>();
		friends = dbHelper.getUser();
		return friends;
	}
	
	/**
	 * 获取专家列表
	 * @param imageUrl
	 * @param avatarView
	 */
	public static List<QXUser> findProfess(){
		Map<String,String> param = new HashMap<String, String>();
		param.clear();
		param.put("count", 20+"");
		String jsonStr = new WebService(C.GETPROFESS, param).getReturnInfo();
		List<QXUser> profess = GetObjectFromService.getFriend(jsonStr);
		List<QXUser> professList = new ArrayList<QXUser>();
		professList.addAll(profess);
		return professList;
	}
	
	public static void displayAvatar(String imageUrl, ImageView avatarView) {
		imageLoader.displayImage(imageUrl, avatarView,
				PhotoUtils.getImageOptions(R.drawable.icon_default_avatar));
	}

	public static void addFriend(String friendId,
			final ICallBack saveCallback) {
		QXUser user = new PreferenceMap(App.ctx).getUser();
		Map<String,String> param = new HashMap<String, String>();
		param.put("Remarks","你好，我是"+user.getName());
		param.put("fromUserID", user.getID());
		param.put("toUserID", friendId);
		String jsonstr = new WebService(C.REQUESTFRIEND, param).getReturnInfo();
		Boolean flag = GetObjectFromService.getSimplyResult(jsonstr);
		saveCallback.onBackMessage(flag);
	}
}
