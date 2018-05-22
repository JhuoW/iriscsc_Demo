package com.xzmc.airuishi.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.bean.Province;

public class mExpandableListAdapter extends BaseExpandableListAdapter {

	private List<Province> province;
	private LayoutInflater inflater, childinflater;
	ImageDownloader mDownloader;
	Context context;

	public mExpandableListAdapter(Context context, List<Province> province) {
		this.province = province;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		childinflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getGroupCount() {
		return province.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return province.get(groupPosition).getCity().size();
	}

	@Override
	public Province getGroup(int provincePosition) {
		return province.get(provincePosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return province.get(groupPosition).getCity().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ImageView arrow;
		TextView name;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.category_item, parent,false);
		}

		name = (TextView) convertView.findViewById(R.id.category_text_large);
		arrow = (ImageView) convertView.findViewById(R.id.category_arow);

		name.setText(province.get(groupPosition).getName());

		if (isExpanded) {
			arrow.setImageResource(R.drawable.arrow_collapse);
		} else {
			arrow.setImageResource(R.drawable.arrow_expand);
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView childText = null;
		if (convertView == null) {
			convertView = childinflater.inflate(R.layout.category_child_item,
					parent, false);
		}
		childText = (TextView) convertView
				.findViewById(R.id.category_child_text);
		childText.setText(province.get(groupPosition).getCity()
				.get(childPosition).getName());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
