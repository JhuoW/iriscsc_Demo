package com.xzmc.airuishi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.view.HeaderLayout;

public class SuggestionActivity extends BaseActivity implements OnClickListener{
	private ImageView net,account,message,product,more;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion_layout);
		initView();
		initData();
		initAction();
	}
	private void initAction() {
		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SuggestionActivity.this.finish();
			}
		});
	}
	private void initData() {
	}
	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		net=(ImageView) findViewById(R.id.iv_net);
		account=(ImageView) findViewById(R.id.iv_account);
		message=(ImageView) findViewById(R.id.iv_message);
		product=(ImageView) findViewById(R.id.iv_product);
		more=(ImageView) findViewById(R.id.iv_more);
		net.setOnClickListener(this);
		account.setOnClickListener(this);
		message.setOnClickListener(this);
		product.setOnClickListener(this);
		more.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent it=new Intent();
		it.setClass(SuggestionActivity.this, SubmitSuggestionActivity.class);
		switch(v.getId()){
		case R.id.iv_net:
			it.putExtra("suggestionType", "网络连接");
			break;
		case R.id.iv_account:
			it.putExtra("suggestionType", "帐号登录");
			break;
		case R.id.iv_message:
			it.putExtra("suggestionType", "即时通信");
			break;
		case R.id.iv_product:
			it.putExtra("suggestionType", "产品建议");
			break;
		case R.id.iv_more:
			it.putExtra("suggestionType", "更多问题");
			break;
		}
		SuggestionActivity.this.startActivity(it);
		SuggestionActivity.this.finish();
	}
}
