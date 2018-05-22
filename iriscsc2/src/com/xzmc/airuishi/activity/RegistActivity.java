package com.xzmc.airuishi.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.MD5;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.airuishi.view.MyProgressDialog;

public class RegistActivity extends BaseActivity {
	private View address_layout;
	private EditText et_phonenumber, et_inputusername, et_inputpassword;
	private Button btn_register;
	private TextView tv_address;
	private String phonenumber, username, password;

	static final int GETADDRESSREQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.getInstance().addActivity(this);
		setContentView(R.layout.activity_register);
		initView();
		initData();
		initAction();
	}

	private void initData() {
		headerLayout.showTitle("帐号注册");
	}

	public void initView() {
		address_layout = findViewById(R.id.address_layout);
		tv_address = (TextView) findViewById(R.id.tv_address);
		et_phonenumber = (EditText) findViewById(R.id.et_inputphone);
		et_inputusername = (EditText) findViewById(R.id.et_inputusername);
		et_inputpassword = (EditText) findViewById(R.id.et_inputpassword);
		btn_register = (Button) findViewById(R.id.btn_register);
		headerLayout = (HeaderLayout) this.findViewById(R.id.headerLayout);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case GETADDRESSREQUEST:
			String addressstr = mIntent.getStringExtra("address");
			tv_address.setText(addressstr);
			break;
		}
	}

	public void initAction() {
		headerLayout.showLeftBackButton("", new OnClickListener() {
			@Override
			public void onClick(View v) {
				RegistActivity.this.finish();
			}
		});

		address_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				it.setClass(RegistActivity.this, SelectAddressActivity.class);
				RegistActivity.this.startActivityForResult(it,
						GETADDRESSREQUEST);
			}
		});
		btn_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username = et_inputusername.getEditableText().toString();
				password = et_inputpassword.getEditableText().toString();
				phonenumber = et_phonenumber.getEditableText().toString();
				if (TextUtils.isEmpty(username)) {
					Utils.toast(R.string.User_name_cannot_be_empty);
					return;
				}
				if (TextUtils.isEmpty(password)) {
					Utils.toast(R.string.Password_cannot_be_empty);
					return;
				}
				if (TextUtils.isEmpty(phonenumber)) {
					Utils.toast(R.string.Phone_number_cannot_be_empty);
					return;
				}
				if (!Utils.isMobileNum(phonenumber)) {
					Utils.toast(R.string.Phone_number_is_wrong);
					return;
				}
				param.clear();
				param.put("phone", phonenumber);
				param.put("nickname", username);
				param.put("password", MD5.getMD5(password.getBytes()));
				param.put("address", tv_address.getText().toString());
				new RegistAsyncTask().execute();
			}
		});
	}

	class RegistAsyncTask extends AsyncTask<String, Void, Object> {
		Boolean flag = true;
		MyProgressDialog dialog = new MyProgressDialog(ctx);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		@Override
		protected Object doInBackground(String... params) {
			String jsonstr = new WebService(C.REGIST, param).getReturnInfo();
			Boolean result = GetObjectFromService.getSimplyResult(jsonstr);
			return result;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			
			Boolean _result = (Boolean) result;
			if(_result){
				Utils.toast(R.string.Registered_successfully);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}	
				RegistActivity.this.finish();
			}
			
//			if (_result) {
//				new Thread(new Runnable() {
//					public void run() {
//						try {
//							// 调用sdk注册方法
//							EMChatManager.getInstance().createAccountOnServer(phonenumber, password);
//							runOnUiThread(new Runnable() {
//								public void run() {
//									if (!RegistActivity.this.isFinishing())
//										if (dialog != null && dialog.isShowing()) {
//											dialog.dismiss();
//										}
//									Utils.toast(R.string.Registered_successfully);
//									finish();
//								}
//							});
//						} catch (final EaseMobException e) {
//							runOnUiThread(new Runnable() {
//								public void run() {
//									if (!RegistActivity.this.isFinishing())
//										if (dialog != null && dialog.isShowing()) {
//											dialog.dismiss();
//										}
//									int errorCode=e.getErrorCode();
//									if(errorCode==EMError.NONETWORK_ERROR){
//										Utils.toast(R.string.network_anomalies);
//									}else if(errorCode==EMError.USER_ALREADY_EXISTS){
//										Utils.toast(R.string.User_already_exists);
//									}else if(errorCode==EMError.UNAUTHORIZED){
//										Utils.toast(R.string.registration_failed_without_permission);
//									}else{
//										Utils.toast(R.string.Registration_failed);
//									}
//								}
//							});
//						}
//					}
//				}).start();	
//				
			else {
				Utils.toast(R.string.Registration_failed);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}

	}

}
