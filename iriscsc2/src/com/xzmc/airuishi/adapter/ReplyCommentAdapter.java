package com.xzmc.airuishi.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.UserDetailActivity;
import com.xzmc.airuishi.bean.Comment;
import com.xzmc.airuishi.bean.Reply;

public class ReplyCommentAdapter extends BaseAdapter {

	private List<Comment> list;
	private Context ctx;
	
	public ReplyCommentAdapter(List<Comment> list,Context ctx){
		this.list = list;
		this.ctx = ctx;
	}
	
	
	public void addReply(List<Comment> reply){
		list.addAll(reply);
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
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(ctx).inflate(
					R.layout.item_reply_comment, null);
			holder = new ViewHolder();
			holder.content = (TextView) view.findViewById(R.id.content);
			holder.replyName = (TextView) view.findViewById(R.id.reply_name);
			holder.sendName = (TextView) view.findViewById(R.id.send_name);
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		
		final Comment reply = list.get(position);
		holder.content.setText(reply.getContent());
		String replyName = reply.getTonickname();
		String sendName = reply.getNickname();
//		holder.replyName.setText(reply.getTonickname());
//		holder.sendName.setText(reply.getNickname());
		
		if(replyName.length()>=4){
			replyName = replyName.substring(0, 3)+"...";
		}
		if(sendName.length()>=4){
			sendName = sendName.substring(0, 3)+"...";
		}
		
		holder.replyName.setText(replyName);
		holder.sendName.setText(sendName);
		
		holder.replyName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToUserDetail(reply.getAcceptId());
			}
		});
		holder.sendName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToUserDetail(reply.getUserId());
			}
		});
		return view;
	}
	public class ViewHolder{
		TextView sendName;
		TextView replyName;
		TextView content;
	}
	
	public void goToUserDetail(String id){
		Intent intent = new Intent(ctx,UserDetailActivity.class);
		intent.putExtra("userId", id);
		ctx.startActivity(intent);
	}

}
