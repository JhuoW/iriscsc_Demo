package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.adapter.ContactUserListAdapter;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.ContactUser;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.db.FriendsTable;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.service.UserService;
import com.xzmc.airuishi.utils.CharacterParser;
import com.xzmc.airuishi.utils.ContactPinyinComparator;
import com.xzmc.airuishi.utils.NetAsyncTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.airuishi.view.MyProgressDialog;
import com.xzmc.qixinplus.listener.ICallBack;

public class ContactActivity extends BaseActivity {
	private List<ContactUser> list = new ArrayList<ContactUser>();
	private ContactUserListAdapter adapter;
	private ListView xlistview;
	private static CharacterParser characterParser;
	private static ContactPinyinComparator pinyinComparator;
	private MyProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.getInstance().addActivity(this);
		setContentView(R.layout.activity_contact_layout);
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new ContactPinyinComparator();
		initView();
		initData();
		initAction();
	}

	private void initAction() {
		xlistview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (ContactActivity.this.list.get(position).getStatue()
						.equals("Requested")) {
					new SweetAlertDialog(ctx)
							.setConfirmText("确定")
							.setTitleText("重新发送请求")
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												final SweetAlertDialog sweetAlertDialog) {
											new NetAsyncTask(ctx, false) {
												boolean issuccess;

												@Override
												protected void doInBack()
														throws Exception {
													UserService
															.addFriend(
																	ContactActivity.this.list
																			.get(position)
																			.getId(),
																	new ICallBack() {
																		@Override
																		public void onBackMessage(
																				Object content) {
																			issuccess = (Boolean) content;
																		}
																	});
												}

												@Override
												protected void onPost(
														Exception e) {
													if (issuccess) {
														Utils.toast("发送好友请求成功");

													} else {
														Utils.toast("发送好友请求失败");
													}
													sweetAlertDialog.dismiss();
												}

											}.execute();
										}
									}).show();
				}

				return true;
			}
		});
	}

	public static List<ContactUser> removeDuplicate(List<ContactUser> list)

	{
		Set set = new LinkedHashSet<String>();
		set.addAll(list);
		list.clear();
		list.addAll(set);
		return list;
	}

	private void initData() {
		dialog = new MyProgressDialog(ctx);
		dialog.show();
		String oldphone = "";
		String oldname = "";
		Uri uri = Phone.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri, null, null, null,
				"sort_key");

		if (cursor != null) {
			while (cursor.moveToNext()) {
				int nameIndex = cursor.getColumnIndex(Phone.DISPLAY_NAME);
				String name = cursor.getString(nameIndex);
				String phoneNumber = cursor.getString(
						cursor.getColumnIndex(Phone.NUMBER)).replace("+86", "");

				if (phoneNumber.length() >= 11) {
					ContactUser person = new ContactUser();
					person.setName(name);
					person.setPhone(phoneNumber);
					oldname = name;
					oldphone = phoneNumber;
					list.add(person);
				}
			}
			cursor.close();
		}
		list = removeDuplicate(list);
		list = filterData(list);
		String json = Utils.transObject2Json(list, "contact");
		param.clear();
		param.put("userID", Utils.getID());
		param.put("contactsOfPhone", json);
		new Thread(new getDataThread()).start();
	}

	private List<ContactUser> filterData(List<ContactUser> data) {
		String MyPhone = new PreferenceMap(ctx).getAccount();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getPhone().equals(MyPhone)) {
				data.remove(i);
			}
		}
		return list;
	}

	class getDataThread extends Thread {
		@Override
		public void run() {

			String jsonstr = new WebService(C.IMPORTPHONE, param)
					.getReturnInfo();
			List<ContactUser> data = GetObjectFromService.getContactUser(jsonstr);
			List<QXUser> friend = FriendsTable.getInstance().selectFriends();
			List<ContactUser> temp=new ArrayList<ContactUser>();
			for (int i = 0; i < data.size(); i++) {
				//for (int j = 0; j < friend.size(); j++) {
					//if (!data.get(i).getId().equals(friend.get(j).getID())) {
						temp.add(data.get(i));
					//}
				//}
			}
			temp = removeDuplicate(temp);
			Message msg = mHandler.obtainMessage();
			msg.obj = temp;
			mHandler.sendMessage(msg);
		}

	};

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			List<ContactUser> data = (List<ContactUser>) msg.obj;
			List<ContactUser> newlist = convertAVUser(data);
			ContactActivity.this.list = Collections.unmodifiableList(newlist);
			adapter = new ContactUserListAdapter(newlist, ctx);
			xlistview.setAdapter(adapter);
		}
	};

	private List<ContactUser> convertAVUser(List<ContactUser> datas) {
		List<ContactUser> contactusers = new ArrayList<ContactUser>();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			ContactUser avUser = datas.get(i);
			String username = avUser.getName();
			if (username != null) {
				String pinyin = characterParser.getSelling(username);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				if (sortString.matches("[A-Z]")) {
					avUser.setSortLetters(sortString.toUpperCase());
				} else {
					avUser.setSortLetters("#");
				}
			} else {
				avUser.setSortLetters("#");
			}
			contactusers.add(avUser);
		}
		Collections.sort(contactusers, pinyinComparator);
		return contactusers;
	}

	private void initView() {
		xlistview = (ListView) findViewById(R.id.contactlist);
		xlistview.setOnScrollListener(new PauseOnScrollListener(
				UserService.imageLoader, true, true));

		headerLayout = (HeaderLayout) this.findViewById(R.id.headerLayout);
		headerLayout.showTitle("通讯录");
		headerLayout.showLeftBackButton("", new OnClickListener() {
			@Override
			public void onClick(View v) {
				ContactActivity.this.finish();
			}
		});
	}

}
