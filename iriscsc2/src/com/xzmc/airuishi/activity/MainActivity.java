package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.baidu.location.LocationClient;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.easemob.util.NetUtils;
import com.umeng.analytics.MobclickAgent;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.db.FriendsTable;
import com.xzmc.airuishi.fragment.ContactFragment;
import com.xzmc.airuishi.fragment.ConvsationFragment;
import com.xzmc.airuishi.fragment.MeFragment;
import com.xzmc.airuishi.fragment.NewsFragment;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.service.LoginFinishReceiver;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

public class MainActivity extends BaseActivity implements OnClickListener,
		EMEventListener {
	private LinearLayout tab_conversation, tab_contact, tab_news, tab_me;
	private Fragment fragment_conversation, fragment_contact, fragment_news,
			fragment_me;
	private ImageView iv_conversation, iv_contact, iv_news, iv_me;
	private TextView tv_conversation, tv_contact, tv_news, tv_me,unreadLabel;
	public LocationClient locClient;
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
	// 当前fragment的index
	private int currentTabIndex;

	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED,
						false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			App.getInstance().logout(null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}

		String id = Utils.getID();
		JPushInterface.resumePush(ctx);
		JPushInterface.setAlias(ctx, id, new TagAliasCallback() {
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
			}
		});

		App.getInstance().addActivity(MainActivity.this);
		setContentView(R.layout.activity_main);

		checkUpgrade();
		initView();
		initData();
		initAction();
		setSelected(0);
		setSelected(2);
		MobclickAgent.updateOnlineConfig(this);
		LoginFinishReceiver.broadcast(this);

		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}

		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isConflict && !isCurrentAccountRemoved) {
			EMChatManager.getInstance().activityResumed();
		}

		// unregister this event listener when this activity enters the
		// background
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper
				.getInstance();
		sdkHelper.pushActivity(this);

		// register the event listener when enter the foreground
		EMChatManager
				.getInstance()
				.registerEventListener(
						this,
						new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
	}

	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// chatHistoryFragment.errorItem.setVisibility(View.GONE);
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			final String st1 = getResources().getString(
					R.string.Less_than_chat_server_connection);
			final String st2 = getResources().getString(
					R.string.the_current_network);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						showConflictDialog();
					} else {
						// chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
						if (NetUtils.hasNetwork(MainActivity.this)) {
						}
						// chatHistoryFragment.errorText.setText(st1);
						// else
						// chatHistoryFragment.errorText.setText(st2);
					}
				}
			});
		}
	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		App.getInstance().logout(null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								accountRemovedBuilder = null;
								finish();
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
							}
						});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		App.getInstance().logout(null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								conflictBuilder = null;
								finish();
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
							}
						});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
			}

		}

	}

	public String getVersion() {
		PackageManager manager;
		PackageInfo info = null;
		manager = this.getPackageManager();
		try {

			info = manager.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return info.versionName;
	}

	public void checkUpgrade() {
		param.clear();
		String version = getVersion();
		param.put("apkVersion", version);
		param.put("apkType", "upgrade_Android");
		new SimpleNetTask(ctx, false) {
			boolean flag;
			String jsonstr;

			@Override
			protected void onSucceed() {
				if (!flag) {
					return;
				} else {
					try {
						JSONObject json = new JSONObject(jsonstr);
						String des = json.getString("description");
						final String url = json.getString("fileUrl");
						String version = json.getString("version");

						new SweetAlertDialog(ctx)
								.setTitleText("有新版本" + version + "啦")
								.setContentText(des)
								.setCancelClickListener(
										new OnSweetClickListener() {
											@Override
											public void onClick(
													SweetAlertDialog sweetAlertDialog) {
												sweetAlertDialog.dismiss();
											}
										})
								.setConfirmText("立即更新")
								.setConfirmClickListener(
										new OnSweetClickListener() {
											@Override
											public void onClick(
													SweetAlertDialog sweetAlertDialog) {
												sweetAlertDialog.dismiss();
												startActivity(new Intent(
														Intent.ACTION_VIEW, Uri
																.parse(url)));
											}
										}).show();

					} catch (Exception e) {
					}
				}
			}

			@Override
			protected void doInBack() throws Exception {
				jsonstr = new WebService(C.GETUPGRADE, param).getReturnInfo();
				flag = GetObjectFromService.getSimplyResult(jsonstr);
			}
		}.execute();
	}

	private void initData() {
		FriendsTable table = FriendsTable.getInstance();
		List<QXUser> localfriend = table.selectFriends();
		Map<String, User> usermap = new HashMap<String, User>();

		for (int i = 0; i < localfriend.size(); i++) {
			QXUser qxUser = localfriend.get(i);
			User user = new User();
			user.setAvatar(qxUser.getImage());
			user.setNick(qxUser.getName());
			user.setUsername(qxUser.getID());
			usermap.put(qxUser.getID(), user);
		}
		QXUser localuser = new QXUser();
		localuser = new PreferenceMap(ctx).getUser();
		User curuser = new User();
		curuser.setAvatar(localuser.getImage());
		curuser.setNick(localuser.getName());
		curuser.setUsername(localuser.getID());
		usermap.put(localuser.getID(), curuser);
		App.getInstance().setContactList(usermap);
		UserDao dao = new UserDao(ctx);
		List<User> users = new ArrayList<User>(usermap.values());
		dao.saveContactList(users);
	}

	public static void goMainActivity(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
	}

	public void initView() {
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		tab_conversation = (LinearLayout) findViewById(R.id.tab_conversation);
		tab_contact = (LinearLayout) findViewById(R.id.tab_contact);
		tab_news = (LinearLayout) findViewById(R.id.tab_news);
		tab_me = (LinearLayout) findViewById(R.id.tab_me);
		iv_conversation = (ImageView) findViewById(R.id.iv_conversation);
		iv_contact = (ImageView) findViewById(R.id.iv_contact);
		iv_news = (ImageView) findViewById(R.id.iv_news);
		iv_me = (ImageView) findViewById(R.id.iv_me);

		tv_conversation = (TextView) findViewById(R.id.tv_conversation);
		tv_contact = (TextView) findViewById(R.id.tv_contact);
		tv_news = (TextView) findViewById(R.id.tv_news);
		tv_me = (TextView) findViewById(R.id.tv_me);

		tab_conversation.setOnClickListener(this);
		tab_contact.setOnClickListener(this);
		tab_news.setOnClickListener(this);
		tab_me.setOnClickListener(this);
	}

	private void setSelected(int i) {
		currentTabIndex = i;
		resetBottomImages();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		hideFragment(ft);
		switch (i) {
		case 0:
			if (fragment_conversation == null) {
				fragment_conversation = new ConvsationFragment();
				ft.add(R.id.id_content, fragment_conversation);
			} else {
				ft.show(fragment_conversation);
			}
			iv_conversation.setImageResource(R.drawable.icon_nav_one_act);
			tv_conversation.setTextColor(Utils.getColor(R.color.theme));
			headerLayout.removeAllRightViews();
			break;
		case 1:
			if (fragment_contact == null) {
				fragment_contact = new ContactFragment();
				ft.add(R.id.id_content, fragment_contact);
			} else {
				ft.show(fragment_contact);
			}
			iv_contact.setImageResource(R.drawable.icon_nav_two_act);
			tv_contact.setTextColor(Utils.getColor(R.color.theme));
			headerLayout.removeAllRightViews();
			break;
		case 2:
			if (fragment_news == null) {
				fragment_news = new NewsFragment();
				ft.add(R.id.id_content, fragment_news);
			} else {
				ft.show(fragment_news);
			}
			iv_news.setImageResource(R.drawable.icon_nav_three_act);
			tv_news.setTextColor(Utils.getColor(R.color.theme));
			headerLayout.removeAllRightViews();
			break;
		case 3:
			if (fragment_me == null) {
				fragment_me = new MeFragment();
				ft.add(R.id.id_content, fragment_me);
			} else {
				ft.show(fragment_me);
			}
			iv_me.setImageResource(R.drawable.icon_nav_four_act);
			tv_me.setTextColor(Utils.getColor(R.color.theme));
			headerLayout.removeAllRightViews();
			break;
		default:
			break;
		}
		ft.commit();
	}

	private void resetBottomImages() {
		iv_conversation.setImageResource(R.drawable.icon_nav_one);
		iv_contact.setImageResource(R.drawable.icon_nav_two);
		iv_news.setImageResource(R.drawable.icon_nav_three);
		iv_me.setImageResource(R.drawable.icon_nav_four);
		tv_conversation.setTextColor(Utils.getColor(R.color.RegistTextChoose));
		tv_contact.setTextColor(Utils.getColor(R.color.RegistTextChoose));
		tv_news.setTextColor(Utils.getColor(R.color.RegistTextChoose));
		tv_me.setTextColor(Utils.getColor(R.color.RegistTextChoose));
	}

	private void hideFragment(FragmentTransaction ft) {
		if (fragment_conversation != null) {
			ft.hide(fragment_conversation);
		}
		if (fragment_contact != null) {
			ft.hide(fragment_contact);
		}
		if (fragment_news != null) {
			ft.hide(fragment_news);
		}
		if (fragment_me != null) {
			ft.hide(fragment_me);
		}
	}

	public void initAction() {
		headerLayout.showLeftTextButton(
				getResources().getString(R.string.app_name), null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_conversation:
			setSelected(0);
			break;
		case R.id.tab_contact:
			setSelected(1);
			break;
		case R.id.tab_news:
			setSelected(2);
			break;
		case R.id.tab_me:
			setSelected(3);
			break;
		}
	}

	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Utils.toast(R.string.Double_quit_app);
				mExitTime = System.currentTimeMillis();
			} else {
				moveTaskToBack(false);
				// finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 消息监听可以注册多个，SDK支持事件链的传递，不过一旦消息链中的某个监听返回能够处理某一事件，消息将不会进一步传递。
	 * 后加入的事件监听会先收到事件的通知 如果收到的事件，能够被处理并且不需要其他的监听再处理，可以返回true，否则返回false
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();
			// 提示新消息
			HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			refreshUI();
			break;
		}

		case EventOfflineMessage: {
			refreshUI();
			break;
		}

		default:
			break;
		}
	}

	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				 updateUnreadLabel();
				if (currentTabIndex == 0) {
					// 当前页面如果为聊天历史页面，刷新此页面
					if (fragment_conversation != null) {
						((ConvsationFragment) fragment_conversation).refresh();
					}
				}
			}
		});
	}

	// /**
	// * 刷新未读消息数
	// */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper
				.getInstance();
		sdkHelper.popActivity(this);

		super.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}
}