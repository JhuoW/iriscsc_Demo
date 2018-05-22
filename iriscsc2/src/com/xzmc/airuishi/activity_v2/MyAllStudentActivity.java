package com.xzmc.airuishi.activity_v2;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.adapter.StudentAdapter;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Student;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class MyAllStudentActivity extends BaseActivity implements
		XListView.IXListViewListener {
	XListView listview;
	List<Student> list = new ArrayList<Student>();
	StudentAdapter adapter;
	int refresh = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myclient);
		initView();
		onRefresh();
		initAction();
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyAllStudentActivity.this.finish();
			}
		});
		headerLayout.showTitle("我的学员");
		listview = (XListView) findViewById(R.id.listview);
		listview.setPullRefreshEnable(true);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
		adapter = new StudentAdapter(ctx, list);
		listview.setAdapter(adapter);
	}

	private void initAction(){
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Student s = (Student) parent.getAdapter().getItem(position);
				Intent intent = new Intent(MyAllStudentActivity.this, StudentDetailActivity.class);
				intent.putExtra("post", s);
				startActivity(intent);
			}
		});
	}
	
	private void getMyStud() {
		new SimpleNetTask(ctx, refresh == 1) {
			List<Student> temp = new ArrayList<Student>();
			String jsonstr;

			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				listview.stopRefresh();
				try {
					JSONObject json = new JSONObject(jsonstr);
					if (json.get("ret").equals("success")) {
						JSONArray jsonarray = json.getJSONArray("students");
						for (int i = 0; i < jsonarray.length(); i++) {
							Student s = new Student();
							JSONObject obj = jsonarray.getJSONObject(i);
							s.setUserId(obj.getString("userId"));
							s.setNickName(obj.getString("nickName"));
							s.setImgUrl(obj.getString("imgUrl"));
							s.setSex(obj.getString("sex"));
							s.setPhone(obj.getString("phone"));
							s.setStatue(obj.getString("status"));
							temp.add(s);
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
				param.put("id", 0 + "");
				param.put("count", 200 + "");
				jsonstr = new WebService(C.getMyStudentList, param)
						.getReturnInfo();
			}
		}.execute();
		refresh++;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getMyStud();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

}
