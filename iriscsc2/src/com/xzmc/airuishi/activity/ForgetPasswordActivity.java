package com.xzmc.airuishi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

/**
 * 忘记密码，获取验证码
 * @author xiaobian
 *
 */
public class ForgetPasswordActivity extends BaseActivity {
	private EditText et_inputcode, et_inputphone;
	private TextView tv_sendcode_again;
	private Button btn_checkcode, btn_getcode;
	private View step_one, step_two;
	private boolean canrequestcode = true;
	private String phonenumber;
	private String code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgetpassword);
		initView();
		initData();
		initAction();
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		et_inputcode = (EditText) findViewById(R.id.et_inputcode);
		et_inputphone = (EditText) findViewById(R.id.et_inputphone);
		tv_sendcode_again = (TextView) findViewById(R.id.tv_sendcode_again);
		btn_checkcode = (Button) findViewById(R.id.btn_checkcode);
		btn_getcode = (Button) findViewById(R.id.btn_getcode);
		step_one = findViewById(R.id.step_one);
		step_two = findViewById(R.id.step_two);
	}

	private void initAction() {
		tv_sendcode_again.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendCode();
			}
		});
		btn_checkcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkCode();
			}

		});
		btn_getcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				phonenumber = et_inputphone.getEditableText().toString();
				if (!Utils.isMobileNum(phonenumber)) {
					Utils.toast("输入正确的手机号");
					return;
				} else {
					sendCode();
					step_two.setVisibility(View.VISIBLE);
					step_one.setVisibility(View.GONE);
				}
			}
		});

		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ForgetPasswordActivity.this.finish();
			}
		});
	}

	private void checkCode() {
		code = et_inputcode.getEditableText().toString();
		if (code.isEmpty()) {
			Utils.toast("验证码不能为空");
			return;
		}
		param.clear();
		param.put("phone", phonenumber);
		param.put("code", code);
		new SimpleNetTask(ctx, false) {
			boolean flag;

			@Override
			protected void onSucceed() {
				if (flag) {
					Intent it = new Intent();
					it.setClass(ForgetPasswordActivity.this,
							ResetPasswordActivity.class);
					it.putExtra("phone", phonenumber);
					ForgetPasswordActivity.this.startActivity(it);
					ForgetPasswordActivity.this.finish();
				} else {
					Utils.toast("验证码错误/失效");
				}
			}

			@Override
			protected void doInBack() throws Exception {
				String jsonstr = new WebService(C.CONTRASTCODE, param)
						.getReturnInfo();
				flag = GetObjectFromService.getSimplyResult(jsonstr);
			}
		}.execute();

	}

	private void sendCode() {
		param.clear();
		param.put("phone", phonenumber);
		if (canrequestcode) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					new WebService(C.SENDSMS, param).getReturnInfo();
				}
			}).start();
			mc = new MyCountDownTimer(60000, 1000);
			mc.start();

		}
	}

	private void initData() {
		headerLayout.showTitle("获取密码");
	}

	private MyCountDownTimer mc;

	class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			tv_sendcode_again.setText("重新发送");
			canrequestcode = true;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tv_sendcode_again.setText("重新发送(" + millisUntilFinished / 1000
					+ ")");
			canrequestcode = false;
		}
	}
}
