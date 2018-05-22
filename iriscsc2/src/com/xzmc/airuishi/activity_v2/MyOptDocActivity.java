package com.xzmc.airuishi.activity_v2;

import java.io.ObjectOutputStream.PutField;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.chatuidemo.adapter.MyOptDocAdapter;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Doc;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class MyOptDocActivity extends BaseActivity implements
		XListView.IXListViewListener {

	private XListView listview;
	List<Doc> docList = new ArrayList<Doc>();
	MyOptDocAdapter adapter;
	
	RelativeLayout rl_doc;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myopt_doc);
		initView();
		onRefresh();
		initAction();
	}

	private void initView() {
		rl_doc = (RelativeLayout) findViewById(R.id.rl_doc);
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyOptDocActivity.this.finish();
			}
		});
		headerLayout.showTitle(getResources().getString(R.string.eye_doc));
		listview = (XListView) findViewById(R.id.listview);
		listview.setPullRefreshEnable(true);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
		adapter = new MyOptDocAdapter(ctx, docList, getLayoutInflater());
		listview.setAdapter(adapter);
	}

	private void initAction(){
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Doc d = (Doc) parent.getAdapter().getItem(position);
				Intent intent = new Intent(MyOptDocActivity.this, MyOptDocDetailActivity.class);
				intent.putExtra("post", d);
				startActivity(intent);
			    overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
			}
		});
		
		rl_doc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MyOptDocActivity.this, MyAllOptDocActivity.class);
				startActivity(intent);
			}
		});
	}
	
	
	private void getOptDoc(){
		new SimpleNetTask(ctx,false) {
			List<Doc> temp = new ArrayList<Doc>();
			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				listview.stopRefresh();
				if(temp==null){
					Utils.toast("net error");
					return;
				}
				docList.clear();
				docList.addAll(temp);
				adapter.notifyDataSetChanged();
			}
			
			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				param.clear();
				param.put("userId", Utils.getID());
				String jsonstr = new WebService(C.GETMYOPTDOC, param).getReturnInfo();
				try {
					JSONObject json = new JSONObject(jsonstr);
					if(json.get("ret").equals("success")){
						JSONArray jsonarray = json.getJSONArray("optometrys");
						temp = GetObjectFromService.getMyOptDoc(jsonarray);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}.execute();
	}
	
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getOptDoc();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

}
