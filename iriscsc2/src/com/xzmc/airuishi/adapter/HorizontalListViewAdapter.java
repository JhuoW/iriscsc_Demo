package com.xzmc.airuishi.adapter;

/** 
 * @author xiaobian 
 * @version 创建时间：2015年5月3日 下午9:30:07 
 * 
 */
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.bean.CategoryModel;
import com.xzmc.airuishi.utils.BaseTools;
import com.xzmc.airuishi.utils.Utils;

public class HorizontalListViewAdapter extends BaseAdapter {
	Context context;
	List<CategoryModel> datas;
	private int mScreenWidth = 0;
	private int mItemWidth = 0;

	public HorizontalListViewAdapter(Context con, List<CategoryModel> datas) {
		this.context = con;
		this.datas = datas;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	private LayoutInflater mInflater;

	@Override
	public Object getItem(int position) {
		return position;
	}

	private ViewHolder vh = new ViewHolder();

	private static class ViewHolder {
		private TextView title;
		private LinearLayout rl;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.horizontallistview_item,
					null);
			vh.title = (TextView) convertView.findViewById(R.id.tv_name);
			vh.rl = (LinearLayout) convertView.findViewById(R.id.rl);
			mScreenWidth = BaseTools.getWindowsWidth((Activity) context);
			mItemWidth = mScreenWidth / 4;
			LinearLayout.LayoutParams param = new LayoutParams(mItemWidth,
					Utils.dip2px(context, 45));
			vh.title.setLayoutParams(param);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		CategoryModel category = datas.get(position);
		vh.title.setText(category.getTitle());
		return convertView;
	}
}