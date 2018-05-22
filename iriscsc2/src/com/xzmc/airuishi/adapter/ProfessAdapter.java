package com.xzmc.airuishi.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.UserDetailActivity;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.service.UserService;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.view.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfessAdapter extends BaseAdapter {

	Context context;
	List<QXUser> list = new ArrayList<QXUser>();
	public static ImageLoader imageLoader = ImageLoader.getInstance();

	public ProfessAdapter(Context context, List<QXUser> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_common_friend, null);
		}
		TextView nameView = ViewHolder.findViewById(convertView,
				R.id.tv_friend_name);
		ImageView avatarView = ViewHolder.findViewById(convertView,
				R.id.img_friend_avatar);
		final QXUser user = list.get(position);
		final String name = user.getName();
		final String avatarUrl = user.getImage();
		UserService.displayAvatar(avatarUrl, avatarView);
		imageLoader.displayImage(avatarUrl, avatarView,
				PhotoUtils.getImageOptions(R.drawable.icon_default_avatar));
		nameView.setText(name);
		avatarView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserDetailActivity.class);
				intent.putExtra("userId", user.getID());
				context.startActivity(intent);
			}
		});
		return convertView;
	}

}
