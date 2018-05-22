package com.xzmc.airuishi.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

public class AddCommmentActivity extends BaseActivity {
	private EditText mContent;
	private TextView mCount;
	private String postId;
	private int cater;
	public static int NEWS=0;
	public static int ADVER=1;
	private String method;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blogaddcomment);
		postId = getIntent().getStringExtra("postId");
		cater=getIntent().getIntExtra("cater", 0);
		if(cater==0){
			method=C.ADDNEWSCOMMENT;
		}else if(cater==1){
			method=C.ADDADVERCOMMENT;
		}
		findViewById();
		setListener();
		initAction();
	}

	private void initAction() {
		headerLayout.showTitle("添加评论");
		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddCommmentActivity.this.finish();
			}
		});
		headerLayout.showRightImageButton(
				R.drawable.action_button_out_pressed_light,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String commentcontent = mContent.getEditableText()
								.toString();
						if (commentcontent.length() == 0) {
							Utils.toast("给点评论吧");
						} else {
							publishComment(commentcontent);
						}
					}

				});
	}

	private void publishComment(final String commentcontent) {
		 
		new SimpleNetTask(ctx, true) {
			boolean flag;
			String jsonstr;
			@Override
			protected void onSucceed() {
				if(flag){
					try {
						JSONObject json = new JSONObject(jsonstr);
						String id = json.getString("id");
						Utils.toast("评论成功");
						Intent intent = new Intent();
			    		intent.putExtra("commentcontent", commentcontent);
			    		intent.putExtra("id", id);
			    		setResult(RESULT_OK, intent);
						AddCommmentActivity.this.finish();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					Utils.toast("评论失败");
				}
			}
			@Override
			protected void doInBack() throws Exception {
				param.clear();
				param.put("userID", Utils.getID());
				param.put("newsID", postId);
				param.put("content", commentcontent);
				jsonstr=new WebService(method, param).getReturnInfo();
				flag=GetObjectFromService.getSimplyResult(jsonstr);
			}
		}.execute();
	}

	private void findViewById() {
		mContent = (EditText) findViewById(R.id.blogaddcomment_content);
		mCount = (TextView) findViewById(R.id.blogaddcomment_count);
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
	}

	private void setListener() {
		mContent.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				int number = s.length();
				mCount.setText(String.valueOf(number));
				selectionStart = mContent.getSelectionStart();
				selectionEnd = mCount.getSelectionEnd();
				if (temp.length() > 140) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					mContent.setText(s);
					mContent.setSelection(tempSelection);
				}
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
