package com.xzmc.airuishi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.bean.ContactUser;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.service.UserService;
import com.xzmc.airuishi.utils.NetAsyncTask;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.qixinplus.listener.ICallBack;

public class ContactUserListAdapter extends BaseAdapter {

	private List<ContactUser> data;
	private Context context;
	public static ImageLoader imageLoader = ImageLoader.getInstance();

	public ContactUserListAdapter(List<ContactUser> data, Context context) {
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.item_contact_user, null);
			holder = new ViewHolder();
			holder.alpha = (TextView) view.findViewById(R.id.alpha);
			holder.con_hasrequest = (TextView) view
					.findViewById(R.id.tv_request_hasrequest);
			holder.con_imageview = (ImageView) view
					.findViewById(R.id.img_contact_avatar);
			holder.layout = (LinearLayout) view.findViewById(R.id.layout);
			holder.phone_layout = (LinearLayout) view
					.findViewById(R.id.phone_layout);
			holder.count_layout = (LinearLayout) view
					.findViewById(R.id.count_layout);
			holder.layout = (LinearLayout) view.findViewById(R.id.layout);
			holder.con_name = (TextView) view
					.findViewById(R.id.tv_contact_name);
			holder.con_id = (TextView) view.findViewById(R.id.tv_contact_id);
			holder.con_phone = (TextView) view
					.findViewById(R.id.tv_contact_phone);
			holder.con_add = (Button) view.findViewById(R.id.btn_request_add);
			holder.con_invite = (Button) view
					.findViewById(R.id.btn_request_invite);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final ContactUser user = data.get(position);

		int section = getSectionForPosition(position);
		if (position == getPositionForSection(section)) {
			holder.layout.setVisibility(View.VISIBLE);
			holder.alpha.setText(user.getSortLetters());
		} else {
			holder.layout.setVisibility(View.GONE);
		}
		holder.con_name.setText(user.getName());
		imageLoader.displayImage(user.getImage(), holder.con_imageview,
				PhotoUtils.getImageOptions(R.drawable.icon_default_avatar));
		if (user.getStatue().equals("Unregistered")) {
			holder.phone_layout.setVisibility(View.VISIBLE);
			holder.count_layout.setVisibility(View.GONE);
			holder.con_hasrequest.setVisibility(View.GONE);
			holder.con_invite.setVisibility(View.VISIBLE);
			holder.con_add.setVisibility(View.GONE);
			holder.con_phone.setText(user.getPhone());
		} else if (user.getStatue().equals("Toadd")) {
			holder.phone_layout.setVisibility(View.GONE);
			holder.count_layout.setVisibility(View.VISIBLE);
			holder.con_invite.setVisibility(View.GONE);
			holder.con_add.setVisibility(View.VISIBLE);
			holder.con_hasrequest.setVisibility(View.GONE);
			holder.con_id.setText(user.getId());
		} else if (user.getStatue().equals("Requested")) {
			holder.phone_layout.setVisibility(View.GONE);
			holder.count_layout.setVisibility(View.VISIBLE);
			holder.con_invite.setVisibility(View.GONE);
			holder.con_add.setVisibility(View.GONE);
			holder.con_hasrequest.setVisibility(View.VISIBLE);
		}
		holder.con_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new NetAsyncTask(context, true) {
					boolean issuccess;

					@Override
					protected void doInBack() throws Exception {
						UserService.addFriend(user.getId(), new ICallBack() {
							@Override
							public void onBackMessage(Object content) {
								issuccess = (Boolean) content;
							}
						});
					}

					@Override
					protected void onPost(Exception e) {
						if (issuccess) {
							Utils.toast("发送好友请求成功");
							holder.con_add.setClickable(false);
						} else {
							Utils.toast("发送好友请求失败");
						}
					}

				}.execute();
			}
		});
		holder.con_invite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SweetAlertDialog(context).setTitleText("邀请好友")
						.setContentText("确定向" + user.getName() + "发送邀请短信?")
						.setConfirmText("发送")
						.setConfirmClickListener(new OnSweetClickListener() {
							@Override
							public void onClick(
									SweetAlertDialog sweetAlertDialog) {
								sweetAlertDialog.dismiss();

								String content = "你好，我是"
										+ new PreferenceMap(App.ctx).getUser()
												.getName()
										+ ",邀请你加入【爱睿视】—您的私人眼视光保健专家。下载地址:"
										+ "http://app.iriscsc.com:8080/appdownload.html";
								String phone = user.getPhone();
								SmsManager smsManager = SmsManager.getDefault();
								if (content.length() > 70) {
									ArrayList<String> texts = smsManager
											.divideMessage(content);
									smsManager.sendMultipartTextMessage(phone,
											null, texts, null, null);
								} else {
									smsManager.sendTextMessage(phone, null,
											content, null, null);
								}

							}
						}).show();

			}
		});
		return view;
	}

	public int getSectionForPosition(int position) {
		return data.get(position).getSortLetters().charAt(0);
	}

	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = data.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	public Object[] getSections() {
		return null;
	}

	class ViewHolder {
		ImageView con_imageview;
		TextView con_name;
		TextView con_phone;
		TextView con_id;
		TextView con_hasrequest;
		Button con_add;
		Button con_invite;
		LinearLayout layout;
		LinearLayout count_layout;
		LinearLayout phone_layout;
		TextView alpha;
	}

}
