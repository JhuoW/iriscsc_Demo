package com.xzmc.airuishi.adapter;

import java.util.List;

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
import com.xzmc.airuishi.activity_v2.StudentDetailActivity;
import com.xzmc.airuishi.bean.Student;
import com.xzmc.airuishi.utils.PhotoUtils;

public class StudentAdapter extends BaseAdapter{
	Context context;
	List<Student> list;
	public static ImageLoader imageLoader = ImageLoader.getInstance();

	public StudentAdapter(Context context,List<Student> list){
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
		ViewHolder holder;
		if(convertView ==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_student, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.rl_student = (RelativeLayout) convertView.findViewById(R.id.rl_student);
			convertView.setTag(holder);;
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final Student s = list.get(position);
		holder.name.setText(s.getNickName());
		imageLoader.displayImage(s.getImgUrl(),
				holder.avatar,
				PhotoUtils.getImageOptions(R.drawable.icon_default_avatar));
		holder.rl_student.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, StudentDetailActivity.class);
				intent.putExtra("post", s);
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	public class ViewHolder{
		TextView name;
		ImageView avatar;
		RelativeLayout rl_student;
	}
}
