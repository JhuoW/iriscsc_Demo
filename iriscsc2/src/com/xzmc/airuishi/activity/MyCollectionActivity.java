package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.adapter.MyCollectionAdapter;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.PostModel;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;


public class MyCollectionActivity extends BaseActivity {
	private List<PostModel> datas=new ArrayList<PostModel>();
	private MyCollectionAdapter adapter;
	private ListView collection_list;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycollection_layout);
		initView();
		initAction();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}
	private void initAction() {
		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyCollectionActivity.this.finish();
			}
		});
		
		collection_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MyCollectionActivity.this,
						PostDetaileActivity.class);
				intent.putExtra("post", datas.get(position));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
	}

	private void initData() {
		headerLayout.showTitle(getResources().getString(R.string.My_news_collection));
		param.clear();
		param.put("userID", Utils.getID());
		
		new SimpleNetTask(ctx,true) {
			List<PostModel> temp = new ArrayList<PostModel>();
			@Override
			protected void onSucceed() {
				datas.clear();
				datas.addAll(temp);
				adapter.notifyDataSetChanged();
			}
			
			@Override
			protected void doInBack() throws Exception {
				String jsonstr=new WebService(C.GETCOLLECTION, param).getReturnInfo();
				temp=GetObjectFromService.getCollectionNews(jsonstr);
			}
		}.execute();
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		collection_list=(ListView) findViewById(R.id.collection_list);
		adapter = new MyCollectionAdapter(ctx, datas, R.layout.item_mycollection);
		collection_list.setAdapter(adapter);

	}

}
