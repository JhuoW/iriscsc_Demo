package com.xzmc.airuishi.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.UserDetailActivity;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.utils.PhotoUtils;

public class SearchResultListAdapter extends BaseAdapter {

	private List<QXUser> data;
	private Context context;
	public static ImageLoader imageLoader = ImageLoader.getInstance();

	public SearchResultListAdapter(List<QXUser> data, Context context) {
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
		ViewHolder holder;
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.item_searchfriendlist, null);
			holder = new ViewHolder();
			holder.friend_imageview = (ImageView) view
					.findViewById(R.id.iv_friend_image);
			holder.friend_sex = (ImageView) view.findViewById(R.id.iv_sex);
			holder.friend_name = (TextView) view
					.findViewById(R.id.tv_friend_title);
			holder.friend_address = (TextView) view
					.findViewById(R.id.tv_friend_address);
			holder.friend_item_layout = (RelativeLayout) view
					.findViewById(R.id.friend_item_layout);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final QXUser searchUser = data.get(position);
		holder.friend_name.setText(searchUser.getName());
		if (searchUser.getAddress().isEmpty()) {
			holder.friend_address.setText("");
		} else {
			holder.friend_address.setText(searchUser.getAddress());
		}
		imageLoader.displayImage(searchUser.getImage(),
				holder.friend_imageview,
				PhotoUtils.getImageOptions(R.drawable.icon_default_avatar));
		if (searchUser.getSex().equals("男")) {
			holder.friend_sex.setImageResource(R.drawable.icon_male);
		} else {
			holder.friend_sex.setImageResource(R.drawable.icon_female);
		}
		holder.friend_item_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserDetailActivity.class);
				intent.putExtra("userId", searchUser.getID());
				context.startActivity(intent);
				((Activity) context).finish();
			}
		});

		return view;
	}

	class ViewHolder {
		ImageView friend_imageview, friend_sex;
		TextView friend_name;
		TextView friend_address;
		RelativeLayout friend_item_layout;
	}

}
