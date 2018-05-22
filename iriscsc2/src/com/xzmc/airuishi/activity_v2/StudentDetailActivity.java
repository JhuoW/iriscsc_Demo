package com.xzmc.airuishi.activity_v2;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.activity.ChatActivity;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.bean.Student;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.view.HeaderLayout;

public class StudentDetailActivity extends BaseActivity {

	Student student;
	ImageView avatar;
	TextView nickname;
	TextView sex;
	TextView phone;
	Button btn_chat;
	Button btn_seeDoc;
	static ImageLoader imageLoader = ImageLoader.getInstance();

	QXUser user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_studentdetail);
		student = (Student) getIntent().getSerializableExtra("post");
		user = new PreferenceMap(ctx).getUser();
		initView();
		initAction();
	}
	
	private void initView(){
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("学员详情");
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StudentDetailActivity.this.finish();
			}
		});
		avatar = (ImageView) findViewById(R.id.avatar);
		nickname = (TextView) findViewById(R.id.nickname);
		sex = (TextView) findViewById(R.id.sex);
		phone = (TextView) findViewById(R.id.phone);
		btn_chat = (Button) findViewById(R.id.btn_chat);
		btn_seeDoc = (Button) findViewById(R.id.btn_seeDoc);
		if(user.isIsCustomer()||user.isIsBusiness()||user.isIsCustomer()){
			btn_seeDoc.setVisibility(View.GONE);
		}else if(user.isIsSuperOpto()||user.isIsOpto()){
			btn_seeDoc.setVisibility(View.VISIBLE);
		}
		
	}
	private void initAction(){
		imageLoader
		.displayImage(
				student.getImgUrl(),
				avatar,
				PhotoUtils
						.getImageOptions(R.drawable.icon_default_avatar));
		nickname.setText(student.getNickName());
		sex.setText(student.getSex());
		phone.setText(student.getPhone());
		btn_chat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ctx, ChatActivity.class).putExtra(
						"userId",student.getUserId()));
			}
		});
		btn_seeDoc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(StudentDetailActivity.this, StudentDocActivity.class);
				intent.putExtra("userId", student.getUserId());
				startActivity(intent);
			}
		});
	}
}
