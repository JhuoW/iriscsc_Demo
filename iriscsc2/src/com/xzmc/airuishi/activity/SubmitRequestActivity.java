package com.xzmc.airuishi.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

public class SubmitRequestActivity extends BaseActivity {
	private EditText requestcontent;
	private ImageView clearbutton;
	private String remark;
	private String fromUserId;
	private String toUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submitrequest_layout);
		toUserId=getIntent().getExtras().getString("toUserId");
		fromUserId=Utils.getID();
		initView();
	}

	private void initView() {
		requestcontent = (EditText) findViewById(R.id.et_sendrequest);
		clearbutton = (ImageView) findViewById(R.id.iv_clear);
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("请求验证");
		headerLayout.showLeftBackButton("", new OnClickListener() {
			@Override
			public void onClick(View v) {
				SubmitRequestActivity.this.finish();
			}
		});
		headerLayout.showRightTextButton("发送", new OnClickListener() {
			@Override
			public void onClick(View v) {
				param.clear();
				remark=requestcontent.getEditableText().toString();
				param.put("Remarks",remark );
				param.put("fromUserID", fromUserId);
				param.put("toUserID", toUserId);
				new SendRequestAsynTask().execute();
			}
		});
		clearbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestcontent.setText("");
			}
		});
	}

	class SendRequestAsynTask extends AsyncTask<Void, Void, Object> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Void... params) {
			String jsonstr = new WebService(C.REQUESTFRIEND, param).getReturnInfo();
			Boolean flag = GetObjectFromService.getSimplyResult(jsonstr);
			return flag;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if((Boolean)result){
				Utils.toast("发送好友请求成功");
				SubmitRequestActivity.this.finish();
			}else{
				Utils.toast("发送好友请求失败");
			}
		}
	}
}
