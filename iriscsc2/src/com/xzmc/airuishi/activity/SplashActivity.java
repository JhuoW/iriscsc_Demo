package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.db.DBHelper;
import com.xzmc.airuishi.service.UserService;
import com.zxmc.airuishi.http.APIHelper;

/**
 * 展开页
 * 
 * @author xiaobian
 */
public class SplashActivity extends BaseActivity {
	private static final int sleepTime = 2000;
	private DBHelper dbHelper;
	private RelativeLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.lay_start_act);

		dbHelper = new DBHelper(this);
		dbHelper.openSqLiteDatabase();
		layout = (RelativeLayout) findViewById(R.id.layout);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
		alphaAnimation.setDuration(1000);
		layout.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		new Thread() {
			@Override
			public void run() {
				String ret = new APIHelper().getNewsCatergory();
				try {
					JSONObject json = new JSONObject(ret);
					if (json.get("ret").equals("success")) {
						JSONArray jsonarray = json
								.getJSONArray("newsCategoryList");
						dbHelper.insertCategories(jsonarray);
					}
					List<QXUser> friend = UserService.findFriends(ctx);
					Map<String, User> usermap = App.getInstance()
							.getContactList();
					for (int i = 0; i < friend.size(); i++) {
						QXUser qxUser = friend.get(i);
						User user = new User();
						user.setAvatar(qxUser.getImage());
						user.setNick(qxUser.getName());
						user.setUsername(qxUser.getID());
						usermap.put(qxUser.getID(), user);
					}
					App.getInstance().setContactList(usermap);
					UserDao dao = new UserDao(ctx);
					List<User> users = new ArrayList<User>(usermap.values());
					dao.saveContactList(users);
				} catch (Exception e) {
				}

				if (DemoHXSDKHelper.getInstance().isLogined()) {
					long start = System.currentTimeMillis();
					EMChatManager.getInstance().loadAllConversations();

					startActivity(new Intent(SplashActivity.this,
							MainActivity.class));
					finish();
				} else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this,
							LoginActivity.class));
					finish();
				}
			}
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 获取软件版本号
	 * 
	 * @return
	 */
	private String getVersion() {
		String st = getResources().getString(R.string.Version_number_is_wrong);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}
}
