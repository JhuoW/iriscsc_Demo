package com.xzmc.airuishi.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.xzmc.airuishi.bean.Notify;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.view.MyNotification;

public class MyReceive extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			Notify notify = GetObjectFromService.getNotificationDetail(content);
			if (notify.getType().equals("RequestAddFriend")) {
				MyNotification notification = new MyNotification(context,
						notify);
				notification.sendNotification();
			} else if (notify.getType().equals("PushNews")&&new PreferenceMap(context).isPush()) {
				MyNotification notification = new MyNotification(context,
						notify);
				notification.sendNotification();
			}else if(notify.getType().equals("110")&&new PreferenceMap(context).isPush()){
				MyNotification notification = new MyNotification(context,
						notify);
				notification.sendNotification();
			}

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
		} else {
		}
	}

}
