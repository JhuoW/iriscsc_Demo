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
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class MyClientActivity extends BaseActivity implements
		XListView.IXListViewListener {
	XListView listview;
	List<Student> list = new ArrayList<Student>();
	StudentAdapter adapter;
	private final static int REFRESH = 1;
	private final static int LOADMORE = 2;
	private int mPageSize = 20;
	int refresh = 1;
	String id;

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
				MyClientActivity.this.finish();
			}
		});
		headerLayout.showTitle("我的客户");
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
				Intent intent = new Intent(MyClientActivity.this, StudentDetailActivity.class);
				intent.putExtra("post", s);
				startActivity(intent);
			}
		});
	}
	
	private void getClient(final int type, final String id) {
		new SimpleNetTask(ctx, refresh == 1) {
			List<Student> temp = new ArrayList<Student>();
			String jsonstr;

			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				listview.stopLoadMore();
				listview.stopRefresh();
				try {
					JSONObject json = new JSONObject(jsonstr);
					if(json.get("ret").equals("success")){
						JSONArray jsonarray = json.getJSONArray("users");
						temp = GetObjectFromService.getCustome(jsonarray);
						if (temp == null) {
		                    Utils.toast("net error");
		                    return;
		                }
		                if (temp.size() == mPageSize) {
		                    listview.setPullLoadEnable(true);
		                } else {
		                	listview.setPullLoadEnable(false);
		                }
		                if (type == REFRESH) {
		                    list.clear();
		                    list.addAll(temp);
		                    adapter.notifyDataSetChanged();
		                }else if (type == LOADMORE) {
		                    int position = list.size();
		                    list.addAll(temp);
		                    adapter.notifyDataSetChanged();
		                    listview.setSelection(position);
		                }
					}else{
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
				param.put("id", id);
				param.put("count", mPageSize + "");
				jsonstr = new WebService(C.getCustomerList, param)
						.getReturnInfo();
			}
		}.execute();
		refresh++;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getClient(REFRESH,0+"");
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		getClient(LOADMORE, list.get(list.size()-1).getId());
	}

}
