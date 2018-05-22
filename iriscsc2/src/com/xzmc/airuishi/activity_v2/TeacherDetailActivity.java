package com.xzmc.airuishi.activity_v2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.activity.ChatActivity;
import com.xzmc.airuishi.bean.Teacher;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.view.RoundImageView;

public class TeacherDetailActivity extends BaseActivity {

	private RoundImageView avatar;
	private TextView tv_name;
	private TextView tv_sex;
	private TextView tv_position;
	private TextView tv_intro;
	
	private TextView tv_name2;
	private RelativeLayout rl_chat;
	private RelativeLayout rl_phone;
	Teacher t;
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	String userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacher_detail);
		t = (Teacher) getIntent().getSerializableExtra("post");
		userId = t.getUserId();
		initView();
		initAction();
	}
	
	private void initView(){
		avatar = (RoundImageView) findViewById(R.id.avatar);
		tv_name = (TextView) findViewById(R.id.name);
		tv_sex = (TextView) findViewById(R.id.sex);
		tv_position = (TextView) findViewById(R.id.position);
		tv_intro = (TextView) findViewById(R.id.introduce);
		tv_name2 = (TextView) findViewById(R.id.name2);
		rl_chat = (RelativeLayout) findViewById(R.id.rl_chat);
		rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);

	}
	
	private void initAction(){
		imageLoader
		.displayImage(
				t.getImgUrl(),
				avatar,
				PhotoUtils
						.getImageOptions(R.drawable.icon_default_avatar_selector));
		tv_name.setText(t.getNickName());
		tv_sex.setText(t.getSex());
		tv_position.setText(t.getPosition());
		tv_intro.setText(t.getIntroduction());
		tv_name2.setText(t.getNickName());
		rl_chat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ctx, ChatActivity.class);
				 intent.putExtra("userId", userId);
				 startActivity(intent);
			}
		});
		
		rl_phone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SweetAlertDialog(ctx, SweetAlertDialog.NORMAL_TYPE).showCancelButton(true)
				.setTitleText("是否拨打").setContentText(t.getPhone()).setConfirmText("确定")
				.setCancelText("取消").setConfirmClickListener(new OnSweetClickListener() {
					
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						// TODO Auto-generated method stub
						sweetAlertDialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:" + t.getPhone()));
                        startActivity(intent);
					}
				}).setCancelClickListener(new OnSweetClickListener() {
					
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						// TODO Auto-generated method stub
                        sweetAlertDialog.dismiss();
					}
				}).show();
			}
		});
	}
}
