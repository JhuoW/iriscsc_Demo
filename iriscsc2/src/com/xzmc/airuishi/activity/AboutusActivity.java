package com.xzmc.airuishi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.view.HeaderLayout;

public class AboutusActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.getInstance().addActivity(this);
		setContentView(R.layout.activity_aboutus_layout);
		initView();
		initData();
		initAction();
	}
	private void initAction() {
		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutusActivity.this.finish();
			}
		});
	}
	private void initData() {
	}
	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
	}

}
