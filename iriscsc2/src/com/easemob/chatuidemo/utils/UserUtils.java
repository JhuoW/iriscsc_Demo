package com.easemob.chatuidemo.utils;

import android.content.Context;
import android.widget.ImageView;

import com.easemob.chatuidemo.domain.User;
import com.squareup.picasso.Picasso;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;

public class UserUtils {
	/**
	 * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
	 * 
	 * @param username
	 * @return
	 */
	public static User getUserInfo(String username) {
		User user = App.getInstance().getContactList().get(username);
		if (user == null) {
			user = new User(username);
		}
		return user;
	}

	/**
	 * 设置用户头像
	 * 
	 * @param username
	 */
	public static void setUserAvatar(Context context, String username,
			ImageView imageView) {
		User user = getUserInfo(username);
		if(user==null||user.getAvatar()==null){
			Picasso.with(context).load(R.drawable.icon_default_avatar)
			.into(imageView);
		}
		else if (user != null && user.getAvatar().length() > 0) {
			Picasso.with(context).load(user.getAvatar())
					.placeholder(R.drawable.icon_default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.icon_default_avatar)
					.into(imageView);
		}
	}

}
