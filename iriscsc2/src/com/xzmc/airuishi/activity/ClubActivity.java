package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.adapter.BaseListAdapter;
import com.xzmc.airuishi.adapter.ViewHolder;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

/**
 * @author xiaobian
 * @version 创建时间：2015年10月1日 下午7:01:24
 */
public class ClubActivity extends BaseActivity {
	private ListView contactlist;
	private List<QXUser> datas = new ArrayList<QXUser>();
	private ClubAdapter adapter;
	private static final int Number = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_club_layout);
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("会员中心");
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClubActivity.this.finish();
			}
		});
		headerLayout.showRightTextButton("换一批", new OnClickListener() {

			@Override
			public void onClick(View v) {
				initData();
			}
		});
		contactlist = (ListView) findViewById(R.id.contactlist);
		adapter = new ClubAdapter(ctx, datas, R.layout.item_club_user);
		contactlist.setAdapter(adapter);
		initData();
	}

	private void initData() {
		param.clear();
		param.put("userID", Utils.getID());
		param.put("count", String.valueOf(Number));
		new SimpleNetTask(ctx) {
			List<QXUser> temp;

			@Override
			protected void onSucceed() {
				if (temp == null) {
					Utils.toast("获取数据失败");
					return;
				}
				datas.clear();
				datas.addAll(temp);
				adapter.notifyDataSetChanged();
			}

			@Override
			protected void doInBack() throws Exception {
				String jsonstr = new WebService(C.GETRANDAMFRIENDS, param)
						.getReturnInfo();
				temp = GetObjectFromService.getClubData(jsonstr);
			}
		}.execute();
	}

	class ClubAdapter extends BaseListAdapter<QXUser> {

		public ClubAdapter(Context ctx, List<QXUser> datas, int layoutId) {
			super(ctx, datas, layoutId);
		}

		@Override
		public void conver(ViewHolder holder, int position, final QXUser t) {
			holder.setImage(R.id.img_contact_avatar, t.getImage(),
					R.drawable.default_avatar);
			holder.setText(R.id.tv_contact_name, t.getName());
			holder.setText(R.id.tv_contact_address, t.getAddress());
			Button add = holder.getView(R.id.btn_request_invite);
			add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ctx, SubmitRequestActivity.class);
					intent.putExtra("toUserId", t.getID());
					ClubActivity.this.startActivity(intent);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				}
			});
		}

	}
}
