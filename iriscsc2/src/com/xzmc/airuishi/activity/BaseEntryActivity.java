package com.xzmc.airuishi.activity;

import android.os.Bundle;

import com.xzmc.airuishi.service.LoginFinishReceiver;

public class BaseEntryActivity extends BaseActivity {
	LoginFinishReceiver loginFinishReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loginFinishReceiver = LoginFinishReceiver.register(this);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(loginFinishReceiver);
	}

}
