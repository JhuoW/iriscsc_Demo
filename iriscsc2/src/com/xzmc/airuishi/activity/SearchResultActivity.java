package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.adapter.SearchResultListAdapter;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.service.UserService;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.airuishi.view.MyProgressDialog;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class SearchResultActivity extends BaseActivity implements
		XListView.IXListViewListener {
	private XListView xlistview;
	private List<QXUser> listdata = new ArrayList<QXUser>();
	private SearchResultListAdapter adapter;
	private Map<String, String> param = new HashMap<String, String>();
	private int PAGE = 1;
	private int COUNT = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchresult_layout);
		initView();
		initData();
		initAction();
	}

	private void initAction() {
		headerLayout.showLeftBackButton("", new OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchResultActivity.this.finish();
			}
		});
	}

	private void initData() {
		String searchcontent = SearchResultActivity.this.getIntent()
				.getExtras().getString("searchContent");
		param.clear();
		param.put("userCode", Utils.getID());
		param.put("searchContent", searchcontent);
		param.put("page", PAGE + "");
		param.put("count", COUNT + "");
		new GetResultAsynTask().execute();

	}

	class GetResultAsynTask extends AsyncTask<Void, Void, Object> {
		MyProgressDialog dialog=new MyProgressDialog(ctx);
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		@Override
		protected Object doInBackground(Void... params) {
			String jsonstr = new WebService(C.SEARCHRESULT, param)
					.getReturnInfo();
			List<QXUser> result = GetObjectFromService.getSearchResult(jsonstr);
			int j = result.size();
			List<QXUser> filterlist = new ArrayList<QXUser>();
			String id2 = Utils.getID();
			for (int i = 0; i < j; i++) {
				String id1 = result.get(i).getID();
				if (!id1.equals(id2)) {
					filterlist.add(result.get(i));
				}
			}
			return filterlist;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if(dialog!=null&&dialog.isShowing()){
				dialog.dismiss();
			}
			listdata = (List<QXUser>) result;
			adapter = new SearchResultListAdapter(listdata, ctx);
			xlistview.setAdapter(adapter);
		}

	}

	protected void initView() {
		headerLayout = (HeaderLayout) this.findViewById(R.id.headerLayout);
		headerLayout.showTitle("搜索结果");
		xlistview = (XListView) findViewById(R.id.searchList);
		xlistview.setPullRefreshEnable(false);
		xlistview.setPullLoadEnable(false);
		xlistview.setXListViewListener(this);
		xlistview.setOnScrollListener(new PauseOnScrollListener(
				UserService.imageLoader, true, true));

	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
