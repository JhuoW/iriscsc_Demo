package com.xzmc.airuishi.view;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.NewFriendActivity;
import com.xzmc.airuishi.activity.PostDetaileActivity;
import com.xzmc.airuishi.activity_v2.MyLessonActivity;
import com.xzmc.airuishi.bean.Notify;
import com.xzmc.airuishi.bean.PostModel;

public class MyNotification {
	Context context;
	String jsonstr;//
	Notify notify;//
	String title;// 
	String image;//
	int type;//

	NotificationManager manager;
	int notificationID;

	public MyNotification(Context context, Notify notify) {
		super();
		this.context = context;
		this.notify = notify;
		manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@SuppressLint("NewApi")
	public void sendNotification() {
		Intent intent = new Intent();
		if (notify.getType().equals("RequestAddFriend")) {
			intent.setClass(context, NewFriendActivity.class);
		}
		else if(notify.getType().equals("PushNews")){
			PostModel post=new PostModel();
			post.setId(notify.getContent());
			post.setTitle(notify.getTitle());
			post.setSource("爱睿视");
			post.setCollection("false");
			intent.putExtra("post", post);
			intent.setClass(context, PostDetaileActivity.class);
		}else if(notify.getType().equals("110")){
			intent.setClass(context, MyLessonActivity.class);
		}
		// else if (notify.getType() == 1) {
		// intent.setClass(context, NewsDetailActivity.class);
		// } else if (notify.getType() == 2) {
		// intent.setClass(context, NewsDetailActivity.class);
		// }
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);

		Builder builder = new Notification.Builder(context);

		builder.setSmallIcon(R.drawable.eay_icon);
		builder.setTicker(notify.getTitle());
		builder.setWhen(System.currentTimeMillis());
		builder.setContentTitle(notify.getTitle());
		builder.setContentText(notify.getTitle());
		builder.setContentIntent(pendingIntent);

		builder.setDefaults(Notification.DEFAULT_SOUND);
		Notification notification = builder.build();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		manager.notify(notificationID, notification);
	}

}
