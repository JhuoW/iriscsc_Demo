package com.xzmc.airuishi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.MD5;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

public class ResetPasswordActivity extends BaseActivity {
	private EditText et_inputpassword, et_inputpassword_2;
	private Button btn_resetpassword;
	private String newpassword, newpassword_2;
	private String phonenumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword);
		initView();
		initData();
		initAction();
	}

	private void initData() {
		headerLayout.showTitle("重置密码");
		phonenumber = getIntent().getStringExtra("phone");
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		et_inputpassword = (EditText) findViewById(R.id.et_inputpassword);
		et_inputpassword_2 = (EditText) findViewById(R.id.et_inputpassword_2);
		btn_resetpassword = (Button) findViewById(R.id.btn_resetpassword);
	}

	private void initAction() {
		headerLayout.showLeftBackButton("", new OnClickListener() {
			@Override
			public void onClick(View v) {
				ResetPasswordActivity.this.finish();
			}
		});
		btn_resetpassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newpassword = et_inputpassword.getEditableText().toString();
				newpassword_2 = et_inputpassword_2.getEditableText().toString();
				if (newpassword.isEmpty() && newpassword_2.isEmpty()) {
					Utils.toast("密码不能为空");
					return;
				} else if (newpassword.length() < 6) {
					Utils.toast("密码长度不能小于六位数");
					return;
				} else if (!newpassword.equals(newpassword_2)) {
					Utils.toast("两次密码输入不相同");
					return;
				} else {
					param.clear();
					param.put("phone", phonenumber);
					param.put("newPassword", MD5.getMD5(newpassword.getBytes()));
					new SimpleNetTask(ctx, false) {
						Boolean flag;
						@Override
						protected void onSucceed() {
							if (flag) {
								Utils.toast("重置密码成功");
								ResetPasswordActivity.this.finish();
							} else {
								Utils.toast("重置密码失败");
							}
						}
						@Override
						protected void doInBack() throws Exception {
							String jsonstr = new WebService(C.RESETPASSWORD,
									param).getReturnInfo();
							flag = GetObjectFromService
									.getSimplyResult(jsonstr);
						}
					}.execute();

				}

			}
		});
	}

}
