package com.xzmc.airuishi.base;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import cn.jpush.android.api.JPushInterface;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.domain.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xzmc.airuishi.utils.PhotoUtils;

public class App extends Application {
	public static boolean debug = true;
	public static App ctx;
	private List<Activity> list = new ArrayList<Activity>();

	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	@Override
	public void onCreate() {
		super.onCreate();
		ctx = this;
		initImageLoader(ctx);
		JPushInterface.setDebugMode(false);
		JPushInterface.init(this);
		
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		// 如果app启用了远程的service，此application:onCreate会被调用2次
		// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
		// 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

		if (processAppName == null ||!processAppName.equalsIgnoreCase("com.xzmc.airuishi")) {
		    //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
		    // 则此application::onCreate 是被service 调用的，直接返回
		    return;
		}else{
			EMChat.getInstance().init(ctx);
			hxSDKHelper.onInit(ctx);
			EMChat.getInstance().setDebugMode(true);
		}
	}
	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
			}
		}
		return processName;
	}
	@SuppressLint("NewApi")
	public void openStrictMode() {
		if (App.debug) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().build());
		}
	}

	/**
	 * 获取内存中好友user list
	 * @return
	 */
	public Map<String, User> getContactList() {
		return hxSDKHelper.getContactList();
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(emCallBack);
	}
	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}
	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSDKHelper.setContactList(contactList);
	}

	public static App getInstance() {
		if (null == ctx) {
			ctx = new App();
		}
		return ctx;
	}

	public void addActivity(Activity activity) {
		list.add(activity);
	}
	public void removeActivity(Activity activity) {
		list.remove(activity);
	}

	public void exit() {
		for (Activity activity : list) {
			activity.finish();
		}
		System.exit(0);
	}

	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"airuishi/Cache");
		ImageLoaderConfiguration config = PhotoUtils.getImageLoaderConfig(
				context, cacheDir);
		ImageLoader.getInstance().init(config);
	}

}
