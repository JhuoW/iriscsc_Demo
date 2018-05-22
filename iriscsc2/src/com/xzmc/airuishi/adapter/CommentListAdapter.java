package com.xzmc.airuishi.adapter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.PostDetaileActivity;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Comment;
import com.xzmc.airuishi.bean.CommentAdpBean;
import com.xzmc.airuishi.bean.Reply;
import com.xzmc.airuishi.db.CommentDBHelper;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.MyListView;
import com.zxmc.airuishi.http.APIHelper;

public class CommentListAdapter extends BaseAdapter {

	public List<Comment> data;
	public Context context;
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	// 回复评论
	private EditText replyEdit;
	private InputMethodManager manager;
	private PopupWindow editWindow;// 回复window
	private Button sendBtn;// 发送按钮
	private RelativeLayout root;
	private String newsId;
	
	private String type;
	
	CommentDBHelper dbHelper;

	public CommentListAdapter(List<Comment> data, Context context,String newsId,RelativeLayout root,String type) {
		this.data = data;
		this.context = context;
		dbHelper = new CommentDBHelper(context);
		this.newsId = newsId;
		this.root = root;
		this.type = type;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.item_newscommentlist, null);
			holder = new ViewHolder();
			holder.com_imageview = (ImageView) view
					.findViewById(R.id.iv_comment_image);
			holder.com_name = (TextView) view
					.findViewById(R.id.tv_comment_name);
			holder.com_time = (TextView) view
					.findViewById(R.id.tv_comment_time);
			holder.com_content = (TextView) view
					.findViewById(R.id.tv_comment_content);
			holder.comment_item_layout = (LinearLayout) view
					.findViewById(R.id.comment_item_layout);
			holder.reply_list = (MyListView) view.findViewById(R.id.reply_list);
			holder.reply_content = (LinearLayout) view.findViewById(R.id.reply_content);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final int outposition = position;
		final Comment comment = data.get(position);
		holder.com_name.setText(comment.getNickname());
		holder.com_time.setText(comment.getTime());
		holder.com_content.setText(comment.getContent());
		imageLoader.displayImage(comment.getPicture(), holder.com_imageview,
				PhotoUtils.getImageOptions(R.drawable.icon_default_avatar));
		
		final List<Comment> list ;
		list = dbHelper.getSonCommentList(comment.getId());
		//System.out.println("母评论id:"+comment.getId());
		//System.out.println("昵称："+list.get(0).getNickname()+"...昵称："+list.get(0).getTonickname());
		//System.out.println("List长度："+list.size());//2
		final CommentAdpBean cab = new CommentAdpBean();
		//母评论id:
		final String mumId = data.get(position).getId();
		cab.setList(list);
		final ReplyCommentAdapter adapter = new ReplyCommentAdapter(list, context);
		holder.reply_list.setAdapter(adapter);
		if(list.size()==0){
			holder.reply_content.setVisibility(View.GONE);
		}else {
			holder.reply_content.setVisibility(View.VISIBLE);
		}
		
		holder.reply_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				List<Comment> temp = new ArrayList<Comment>();
				temp.addAll(list);
				initReply(position,cab.getList());
				showDiscuss(outposition,temp,adapter,position,holder.reply_list,mumId);
			}
		});
		
		
		holder.comment_item_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initReply(outposition, data);
				showDiscuss(position,adapter,holder.reply_content,holder.reply_list,mumId);
			}
		});

		return view;
	}

	public class ViewHolder {
		ImageView com_imageview;
		TextView com_name;
		TextView com_time;
		TextView com_content;
		LinearLayout comment_item_layout;
		MyListView reply_list;
		LinearLayout reply_content;
	}

	private void initReply(int positon,List<Comment> list) {
		View editView = LayoutInflater.from(context).inflate(
				R.layout.friend__replay_input, null);
		editWindow = new PopupWindow(editView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		editWindow.setBackgroundDrawable(context.getResources().getDrawable(
				R.color.white));
		editWindow.setOutsideTouchable(true);
		replyEdit = (EditText) editView.findViewById(R.id.reply);
		replyEdit.setHint("回复"+list.get(positon).getNickname()+":");
		sendBtn = (Button) editView.findViewById(R.id.send_msg);
	}

	/**
	 * 显示回复评论框
	 * 
	 * @param reply
	 */
	private void showDiscuss(final int position ,final List<Comment> list,final ReplyCommentAdapter adapter,final int innerPosition,final ListView listView,final String mumId) {
		replyEdit.setFocusable(true);
		replyEdit.requestFocus();
		// 设置焦点，不然无法弹出输入法
		editWindow.setFocusable(true);

		// 以下两句不能颠倒
		editWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		editWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		editWindow.showAtLocation(root, Gravity.BOTTOM, 0, 0);

		// 显示键盘
		manager = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		editWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				manager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
				replyEdit.setText("");
				
			}
		});
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(replyEdit.getText().toString())) {
					Utils.toast("回复内容不能为空");
					return;
				}
				final String content = replyEdit.getText().toString();
				new SimpleNetTask(context) {
					boolean flag;
					@Override
					protected void onSucceed() {
						// TODO Auto-generated method stub
						if(flag){
							Comment c = new Comment();
							c.setUserId(Utils.getID());
							c.setNickname(Utils.getName());
							c.setContent(content);
							c.setTime(Utils.getDetailTime());
							c.setStatus("1");
							c.setAcceptId(list.get(innerPosition).getUserId());
							c.setTonickname(list.get(innerPosition).getNickname());
							c.setContentId(mumId);
							dbHelper.insertComment(c);
							List<Comment> reply = new ArrayList<Comment>();
							reply.add(c);
							list.add(c);
							adapter.addReply(reply);
							adapter.notifyDataSetChanged();
							editWindow.dismiss();
							//Utils.setListViewHeightBasedOnChildren(listView);
						}else{
							Utils.toast("评论失败，请重试");
						}
					}

					@Override
					protected void doInBack() throws Exception {
						Map<String, String> param = new HashMap<String, String>();
						param.clear();
						param.put("newsID", newsId);
						param.put("userID", Utils.getID());
						param.put("touserID",list.get(innerPosition).getUserId());
						System.out.println("toUserId = acceptId:"+list.get(innerPosition).getAcceptId());
						param.put("commentID", data.get(position).getId());
						param.put("content", content);
						param.put("type", type);
						String jsonStr = new WebService(C.REPLYCOMMENT,
								param).getReturnInfo();
						flag = GetObjectFromService.getSimplyResult(jsonStr);
					}
				}.execute();
			}
		});
	}
	
	//显示母评论回复评论框：
	/**
	 * 显示回复评论框
	 * 
	 * @param reply
	 */
	private void showDiscuss(final int position,final ReplyCommentAdapter adapter,final LinearLayout content_layout,final ListView listView,final String mumId) {
		replyEdit.setFocusable(true);
		replyEdit.requestFocus();
		// 设置焦点，不然无法弹出输入法
		editWindow.setFocusable(true);

		// 以下两句不能颠倒
		editWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		editWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		editWindow.showAtLocation(root, Gravity.BOTTOM, 0, 0);
		// 显示键盘
		manager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		editWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				manager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
				replyEdit.setText("");
			}
		});
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(replyEdit.getText().toString())) {
					Utils.toast("回复内容不能为空");
					return;
				}
				final String content = replyEdit.getText().toString();
				new SimpleNetTask(context) {
					boolean flag;

					@Override
					protected void onSucceed() {
						// TODO Auto-generated method stub
						if (flag) {
							content_layout.setVisibility(View.VISIBLE);
							Comment c = new Comment();
							c.setUserId(Utils.getID());
							c.setNickname(Utils.getName());
							c.setTonickname(data.get(position).getNickname());
							c.setContent(content);
							c.setTime(Utils.getDetailTime());
							c.setStatus("1");
							c.setContentId(mumId);
							dbHelper.insertComment(c);
							List<Comment> reply = new ArrayList<Comment>();
							reply.add(c);
							adapter.addReply(reply);
							adapter.notifyDataSetChanged();
							editWindow.dismiss();
							//Utils.setListViewHeightBasedOnChildren(listView);
						} else {
							Utils.toast("评论失败，请重试");
						}
					}

					@Override
					protected void doInBack() throws Exception {
						// TODO Auto-generated method stub
						Map<String, String> param = new HashMap<String, String>();
						param.clear();
						param.put("newsID", newsId);
						param.put("userID", Utils.getID());
						param.put("touserID", data.get(position).getUserId());
						System.out.println("touserID:"+data.get(position).getUserId());
						param.put("commentID", data.get(position).getId());
						param.put("content", content);
						param.put("type", type);
						String jsonStr = new WebService(C.REPLYCOMMENT,
								param).getReturnInfo();
						flag = GetObjectFromService
								.getSimplyResult(jsonStr);
					}
				}.execute();
			}
		});
	}
}
