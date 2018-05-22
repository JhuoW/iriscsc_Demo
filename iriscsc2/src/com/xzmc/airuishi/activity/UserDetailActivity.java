package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.db.FriendsTable;
import com.xzmc.airuishi.db.ProfessDBHelper;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.NetAsyncTask;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.view.HeaderLayout;

public class UserDetailActivity extends BaseActivity implements OnClickListener {
	private ImageView avatar;
	private TextView username, usernumber, useraddress;
	private Button addRequest;
	private String userId;
	boolean isFriend = false;
	static ImageLoader imageLoader = ImageLoader.getInstance();
	private QXUser user;
	
	private LinearLayout ll_professor;
	private TextView tv_infor_professor;

	private LinearLayout ll_onlineTime;
	private TextView tv_onlineTime;
	
	ProfessDBHelper pdbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userdetail_layout);
		userId = (String) getIntent().getExtras().get("userId");
		pdbHelper = new ProfessDBHelper(ctx);
		isFriend = checkUserFrmDb(userId);
		initData();
	}

	private void initData() {
		param.clear();
		param.put("userID", userId);
		new NetAsyncTask(ctx, true) {
			@Override
			protected void onPost(Exception e) {
				if (user != null) {
					initView();
					username.setText(user.getName());
					usernumber.setText("NO." + user.getID());
					useraddress.setText(user.getAddress());
					if(user.getIsProfessor().equals("false")){
						ll_professor.setVisibility(View.GONE);
						ll_onlineTime.setVisibility(View.GONE);
					}else {
						ll_professor.setVisibility(View.VISIBLE);
						ll_onlineTime.setVisibility(View.VISIBLE);
						tv_infor_professor.setText(user.getProfessorInfo());
						tv_onlineTime.setText(user.getOnlineTime());
					}
					imageLoader
							.displayImage(
									user.getImage(),
									avatar,
									PhotoUtils
											.getImageOptions(R.drawable.icon_default_avatar));
				} else {
					return;
				}

			}

			@Override
			protected void doInBack() throws Exception {
				String jsonstr = new WebService(C.GETFRIENDINFOR, param)
						.getReturnInfo();
				user = GetObjectFromService.getQXUser(jsonstr);
			}
		}.execute();
	}

	private boolean checkUser(String id) {
		List<QXUser> friends = FriendsTable.getInstance().selectFriends();
		for (QXUser user : friends) {
			if (user.getID().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkUserFrmDb(String id){
		List<QXUser> commonfriends = pdbHelper.getCommonUser();
		List<QXUser> professfriends = pdbHelper.getProfessUser();
		List<QXUser> friends = new ArrayList<QXUser>();
		friends.addAll(commonfriends);
		friends.addAll(professfriends);
		for (QXUser user : friends) {
			if (user.getID().equals(id)) {
				return true;
			}
		}
		return false;
	}

	private void initView() {
		ll_professor = (LinearLayout) findViewById(R.id.ll_professor);
		tv_infor_professor = (TextView) findViewById(R.id.tv_infor_professor);
		ll_onlineTime = (LinearLayout) findViewById(R.id.ll_onlineTime);
		tv_onlineTime = (TextView) findViewById(R.id.tv_onlineTime);
		avatar = (ImageView) findViewById(R.id.iv_infor_avatar);
		username = (TextView) findViewById(R.id.tv_infor_name);
		usernumber = (TextView) findViewById(R.id.tv_infor_number);
		useraddress = (TextView) findViewById(R.id.tv_infor_address);
		addRequest = (Button) findViewById(R.id.btn_infor_addfriend);
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("个人信息");
		headerLayout.showLeftBackButton("", new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserDetailActivity.this.finish();
			}
		});
		if (user == null) {
			return;
		}
		if (isFriend) {
			addRequest.setText("发起聊天");
		} else {
			addRequest.setText("加为好友");
		}
		addRequest.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		if (isFriend) {
			startActivity(new Intent(ctx, ChatActivity.class).putExtra(
					"userId",userId));
		} else {
			Intent intent = new Intent(ctx, SubmitRequestActivity.class);
			intent.putExtra("toUserId", user.getID());
			UserDetailActivity.this.startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			UserDetailActivity.this.finish();
		}
	}
}
