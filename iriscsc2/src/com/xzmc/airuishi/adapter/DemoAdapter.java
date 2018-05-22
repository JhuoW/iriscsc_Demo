package com.xzmc.airuishi.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity_v2.PushActivity2;
import com.xzmc.airuishi.bean.Student;
import com.xzmc.airuishi.utils.PhotoUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DemoAdapter extends BaseAdapter {

	private Context context = null;

	private List<Student> datas = null;
	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();
    public static ImageLoader imageLoader = ImageLoader.getInstance();

	public DemoAdapter(Context context, List<Student> datas) {
		this.datas = datas;
		this.context = context;

		configCheckMap(false);
	}

	public void configCheckMap(boolean bool) {

		for (int i = 0; i < datas.size(); i++) {
			isCheckMap.put(i, bool);
		}

	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewGroup layout = null;

		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			layout = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.listitem, parent, false);
		} else {
			layout = (ViewGroup) convertView;
		}
		final Student bean = datas.get(position);
		ImageView avatar = (ImageView) layout.findViewById(R.id.avatar);
		 imageLoader
 		.displayImage(
 				bean.getImgUrl(),
 				avatar,
 				PhotoUtils
 						.getImageOptions(R.drawable.default_face));	 
		TextView name = (TextView) layout.findViewById(R.id.name);
		name.setText(bean.getNickName());
		CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkbox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isCheckMap.put(position, isChecked);
				if(isChecked){
					((PushActivity2)context).showCheckImage(bean);
				}else{
					((PushActivity2)context).deleteImage(bean);

				}
			}
		});
		if (isCheckMap.get(position) == null) {
			isCheckMap.put(position, false);
		}
		checkBox.setChecked(isCheckMap.get(position));

		ViewHolder holder = new ViewHolder();

		holder.checkBox = checkBox;

		holder.name = name;
		
		layout.setTag(holder);
		return layout;
	}

	public static class ViewHolder {
		public ImageView avatar = null;
		public TextView name = null;
		public CheckBox checkBox = null;
	}
	
	public void remove(int position) {
		this.datas.remove(position);
	}
	public Map<Integer, Boolean> getCheckMap() {
		return this.isCheckMap;
	}
	public List<Student> getDatas() {
		return datas;
	}
}
