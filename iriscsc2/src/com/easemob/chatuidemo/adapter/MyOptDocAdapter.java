package com.easemob.chatuidemo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.bean.Doc;

public class MyOptDocAdapter extends BaseAdapter {

	
	public List<Doc> list;
	public Context ctx;
    private LayoutInflater inflater;
	
	public MyOptDocAdapter(Context ctx,List<Doc> list,LayoutInflater inflater){
		this.ctx = ctx;
		this.list = list;
		this.inflater = inflater;
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
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_myoptdoc, null);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
		}else{
            holder = (ViewHolder) convertView.getTag();
		}
			Doc d = list.get(position);
			holder.tv_time.setText(d.getTime());
		return convertView;
	}
	
	public class ViewHolder{
		TextView tv_time;
	}

}
