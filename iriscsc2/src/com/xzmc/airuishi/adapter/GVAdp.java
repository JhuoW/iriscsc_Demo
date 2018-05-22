package com.xzmc.airuishi.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xzmc.airuishi.R;

public class GVAdp extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private List<String> titles;

	private List<Integer> icons;

	public GVAdp(LayoutInflater layoutInflater, List<String> titles,
			List<Integer> icons) {
		this.layoutInflater = layoutInflater;
		this.icons = icons;
		this.titles = titles;
	}

	@Override
	public int getCount() {
		return icons.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Containner containner;
		if (convertView == null) {
			containner = new Containner();
			convertView = layoutInflater.inflate(R.layout.lay_gridview_item,
					null);
			containner.ivIcon = (ImageView) convertView
					.findViewById(R.id.ivIcon);
			containner.tvTitle = (TextView) convertView
					.findViewById(R.id.tvTitle);
			convertView.setTag(containner);
		} else {
			containner = (Containner) convertView.getTag();
		}
		containner.ivIcon.setBackgroundResource(icons.get(position));
		containner.tvTitle.setText(titles.get(position));
		return convertView;
	}

	public class Containner {
		private ImageView ivIcon;

		private TextView tvTitle;
	}

}
