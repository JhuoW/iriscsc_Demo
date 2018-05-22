package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.adapter.NewFriendAdapter;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.RequestUser;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.BaseListView;
import com.xzmc.airuishi.view.ClearEditText;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.airuishi.view.MyProgressDialog;

public class NewFriendActivity extends BaseActivity {
	private BaseListView<RequestUser> listView;
	private List<RequestUser> data = new ArrayList<RequestUser>();
	private NewFriendAdapter adapter;
	private ClearEditText searchNameEdit;
	private RelativeLayout searchBtn;
	private TextView mynumber;
	private LinearLayout myinfo_layout;
	private TextView tv_search_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newfriend_layout);
		initView();
		initData();
		initAction();
	}
	
	private void initAction() {
		searchNameEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (searchNameEdit.getEditableText().toString().length() == 0) {
					searchBtn.setVisibility(View.GONE);
					myinfo_layout.setVisibility(View.VISIBLE);
				} else {
					searchBtn.setVisibility(View.VISIBLE);
					myinfo_layout.setVisibility(View.GONE);
					tv_search_text.setText(searchNameEdit.getEditableText()
							.toString());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewFriendActivity.this,
						SearchResultActivity.class);
				intent.putExtra("searchContent", tv_search_text.getText()
						.toString());
				ctx.startActivity(intent);
				NewFriendActivity.this.finish();
			}
		});

		listView.setPullLoadEnable(false);
		listView.setPullRefreshEnable(false);
	}

	private void initData() {
		param.clear();
		param.put("userID", Utils.getID());
		new MyAsynTask().executeOnExecutor(Executors.newCachedThreadPool());
		mynumber.setText(Utils.getID());
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount()));
		params.height += 5;// if without this statement,the listview will be a
							// little short
		listView.setLayoutParams(params);
	}

	class MyAsynTask extends AsyncTask<Void, Void, Object> {
		MyProgressDialog dialog=new MyProgressDialog(ctx);
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}
		
		@Override
		protected Object doInBackground(Void... params) {
			String jsonstr = new WebService(C.NEWFRIENDREQUEST, param)
					.getReturnInfo();
			List<RequestUser> data = GetObjectFromService
					.getRequestUser(jsonstr);
			return data;
		}

		@Override
		protected void onPostExecute(final Object result) {
			super.onPostExecute(result);
			if(dialog!=null&&dialog.isShowing()){
				dialog.dismiss();
			}
			data = (List<RequestUser>) result;
			adapter = new NewFriendAdapter(ctx, data);
			listView.setAdapter(adapter);
			setListViewHeightBasedOnChildren(listView);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		searchNameEdit.setText("");
		initData();
		initAction();
	}

	public void initView() {
		headerLayout = (HeaderLayout) this.findViewById(R.id.headerLayout);
		headerLayout.showTitle("新朋友");
		headerLayout.showLeftBackButton("", new OnClickListener() {
			@Override
			public void onClick(View v) {
				NewFriendActivity.this.finish();
			}
		});
		myinfo_layout = (LinearLayout) findViewById(R.id.myinfo_layout);
		mynumber = (TextView) findViewById(R.id.tv_mynumber);
		listView = (BaseListView<RequestUser>) findViewById(R.id.newfriendList);
		tv_search_text = (TextView) findViewById(R.id.tv_search_text);
		searchNameEdit = (ClearEditText) findViewById(R.id.searchNameEdit);
		searchBtn = (RelativeLayout) findViewById(R.id.search_layout);
		headerLayout = (HeaderLayout) this.findViewById(R.id.headerLayout);
		listView.requestFocus();
	}

	private void deleteAddRequest(final RequestUser addRequest) {
	}

}
