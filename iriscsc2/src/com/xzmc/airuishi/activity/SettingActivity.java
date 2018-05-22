package com.xzmc.airuishi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.easemob.EMCallBack;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

public class SettingActivity extends BaseActivity {
	private Button exit_btn;
	private LinearLayout layout_clearcache, layout_changepassword;
	private TextView tv_push;
	private ImageView iv_push_pushswitch;
	public static final String SP_NAME = "SP_NAME";
	public static final String SP_KEY_PUSH = "SP_KEY_PUSH";
	private boolean ispush;
	private PreferenceMap accountPre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.getInstance().addActivity(this);
		setContentView(R.layout.activity_setting_layout);
		accountPre = new PreferenceMap(ctx, "accountInformation");
		initView();
		initData();
		initAction();
	}

	private void initData() {
		headerLayout.showTitle("设置");

		ispush = accountPre.isPush();
		if (ispush) {
			tv_push.setText("推送开启");
			iv_push_pushswitch.setImageResource(R.drawable.icon_subscribe_on);

		} else {
			tv_push.setText("推送关闭");
			iv_push_pushswitch.setImageResource(R.drawable.icon_subscribe_off);
		}
	}

	public void saveBooleanValue(boolean value) {
		accountPre.setIsPush(value);
	}

	private void initView() {
		exit_btn = (Button) findViewById(R.id.btn_exit);
		layout_clearcache = (LinearLayout) findViewById(R.id.layout_clearcache);
		layout_changepassword = (LinearLayout) findViewById(R.id.layout_changepassword);
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		tv_push = (TextView) findViewById(R.id.tv_push);
		iv_push_pushswitch = (ImageView) findViewById(R.id.iv_push_pushswitch);
	}

	private void initAction() {
		layout_changepassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.goActivity(SettingActivity.this, ChangePassword.class);
			}
		});
		layout_clearcache.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Utils.toast("成功清理缓存");
			}
		});
		iv_push_pushswitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ispush) {
					tv_push.setText("推送关闭");
					iv_push_pushswitch
							.setImageResource(R.drawable.icon_subscribe_off);
					ispush = false;

				} else {
					tv_push.setText("推送开启");
					iv_push_pushswitch
							.setImageResource(R.drawable.icon_subscribe_on);
					ispush = true;
				}
				new PreferenceMap(ctx).setIsPush(ispush);
				saveBooleanValue(ispush);
			}
		});
		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingActivity.this.finish();
			}
		});
		exit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SweetAlertDialog(ctx).setTitleText("确定退出程序？")
						.setCancelText("取消").setConfirmText("确定")
						.showCancelButton(true)
						.setConfirmClickListener(new OnSweetClickListener() {
							@Override
							public void onClick(

							SweetAlertDialog sweetAlertDialog) {
								sweetAlertDialog.dismiss();
								App.getInstance().logout(new EMCallBack() {
									@Override
									public void onSuccess() {
										JPushInterface.stopPush(ctx);
										QXUser user = new QXUser();
										new PreferenceMap(ctx).setUser(user);
										finish();			
										//App.getInstance().exit();
                                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));

									}

									@Override
									public void onProgress(int arg0, String arg1) {

									}

									@Override
									public void onError(int arg0, String arg1) {

									}
								});
							}
						}).setCancelClickListener(new OnSweetClickListener() {
							@Override
							public void onClick(
									SweetAlertDialog sweetAlertDialog) {
								sweetAlertDialog.dismiss();
							}
						}).show();

			}
		});
	}

}
