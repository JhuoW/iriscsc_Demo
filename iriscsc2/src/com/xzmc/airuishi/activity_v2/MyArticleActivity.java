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
import com.xzmc.airuishi.activity.PostDetailWebViewActivity;
import com.xzmc.airuishi.adapter.CenterListAdp;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.PostModel;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class MyArticleActivity extends BaseActivity implements
		XListView.IXListViewListener {
	XListView listView;
	CenterListAdp adapter;
	List<PostModel> list = new ArrayList<PostModel>();

	private final static int REFRESH = 1;
	private final static int LOADMORE = 2;
	int refresh = 1;

	String id = "0";
	
    private int mPageSize = 20;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myarticle);
		initView();
		onRefresh();
		initAction();
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("我的文章");
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyArticleActivity.this.finish();
			}
		});
		listView = (XListView) findViewById(R.id.listview);
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);
		adapter = new CenterListAdp(list, getLayoutInflater());
		listView.setAdapter(adapter);
	}

	private void initAction(){
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				PostModel pm = (PostModel) parent.getAdapter().getItem(position);
				Intent intent = new Intent(MyArticleActivity.this,
						PostDetailWebViewActivity.class);
				intent.putExtra("post", pm);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
	}
	
	private void getData(final int type,final String id){
		new SimpleNetTask(ctx,refresh==1) {
            List<PostModel> temp = new ArrayList<PostModel>();
            String jsonstr;
			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
                listView.stopLoadMore();
                listView.stopRefresh();
				try {
					JSONObject json = new JSONObject(jsonstr);
					if(json.get("ret").equals("success")){
						JSONArray jsonarray = json.getJSONArray("newsList");
						temp = GetObjectFromService.getPostModel(jsonarray);
						if (temp == null) {
		                    Utils.toast("net error");
		                    return;
		                }
		                if (temp.size() == mPageSize) {
		                    listView.setPullLoadEnable(true);
		                } else {
		                	listView.setPullLoadEnable(false);
		                }
		                if (type == REFRESH) {
		                    list.clear();
		                    list.addAll(temp);
		                    adapter.notifyDataSetChanged();
		                }else if (type == LOADMORE) {
		                    int position = list.size();
		                    list.addAll(temp);
		                    adapter.notifyDataSetChanged();
		                    listView.setSelection(position);
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
				param.put("count", mPageSize+"");
				jsonstr = new WebService(C.GETMYARTICLE, param).getReturnInfo();
			}
		}.execute();
		refresh++;
	}
	
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
        getData(REFRESH,0+"");

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		getData(LOADMORE, list.get(list.size()-1).getId());
	}

}
