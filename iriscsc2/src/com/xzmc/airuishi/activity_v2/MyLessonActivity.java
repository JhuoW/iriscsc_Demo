package com.xzmc.airuishi.activity_v2;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Lesson;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class MyLessonActivity extends BaseActivity implements
		XListView.IXListViewListener {

	XListView listview;
	List<Lesson> list = new ArrayList<Lesson>();
	MyLessonAdapter adapter;
	int refresh = 1;
	QXUser user;
	String need;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mylesson);
		user = new PreferenceMap(ctx).getUser();
		need = getIntent().getExtras().getString("tag", "112");
		initView();
		onRefresh();
		initAction();
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("我的课程");
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyLessonActivity.this.finish();
			}
		});
		listview = (XListView) findViewById(R.id.listview);
		listview.setPullRefreshEnable(true);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
		adapter = new MyLessonAdapter();
		listview.setAdapter(adapter);
	}

	private void initAction(){
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Lesson l = (Lesson) parent.getAdapter().getItem(position);
				Intent intent = new Intent(MyLessonActivity.this, LessonDetailActivity.class);
				intent.putExtra("post", l);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
	}
	
	private void getData() {
		new SimpleNetTask(ctx, refresh == 1) {
			List<Lesson> temp = new ArrayList<Lesson>();
			String jsonstr;

			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				listview.stopRefresh();
				try {
					JSONObject json = new JSONObject(jsonstr);
					if (json.get("ret").equals("success")) {
						JSONArray jsonarray = json.getJSONArray("courses");
						if(need.equals("111")){
							temp = GetObjectFromService.getLessonList(jsonarray);
						}else{
							temp = GetObjectFromService.getLessonList_receive(jsonarray);
						}
						if (temp == null) {
							Utils.toast("net error");
							return;
						}
						list.clear();
						list.addAll(temp);
						adapter.notifyDataSetChanged();
					} else {
						Utils.toast("获取数据失败");
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				param.clear();
				param.put("userId", Utils.getID());
				jsonstr = new WebService(C.GETCOURSELIST, param)
						.getReturnInfo();
			}
		}.execute();
		refresh++;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	public class MyLessonAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.item_mylesson, null);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.description = (TextView) convertView
						.findViewById(R.id.description);
				holder.releaseTime = (TextView) convertView
						.findViewById(R.id.releaseTime);
				holder.lectureTime = (TextView) convertView
						.findViewById(R.id.lectureTime);
				holder.usercount = (TextView) convertView
						.findViewById(R.id.usercount);
				holder.isopen = (ImageView) convertView
						.findViewById(R.id.isopen);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Lesson l = list.get(position);
			holder.title.setText(l.getTitle());
			holder.description.setText(l.getDescription());
			holder.releaseTime.setText(l.getReleaseTime());
			holder.lectureTime.setText(l.getLectureTime());
			holder.usercount.setText(l.getUserCount());
			if (l.getStatus().equals("81")) {
				holder.isopen.setImageResource(R.drawable.weikaike);
			} else if (l.getStatus().equals("83")) {
				holder.isopen.setImageResource(R.drawable.jinxing);
			} else if (l.getStatus().equals("85")) {
				holder.isopen.setImageResource(R.drawable.weikaike);
			} else if (l.getStatus().equals("86")) {
				holder.isopen.setImageResource(R.drawable.jieshu);
			}
			return convertView;
		}

		public class ViewHolder {
			TextView title;
			TextView description;
			TextView releaseTime;
			TextView lectureTime;
			TextView usercount;
			ImageView isopen;
		}
	}
}
