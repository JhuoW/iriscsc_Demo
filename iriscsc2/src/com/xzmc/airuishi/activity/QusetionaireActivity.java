package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.ab.view.app.AbPopoverView;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.adapter.BaseListAdapter;
import com.xzmc.airuishi.adapter.ViewHolder;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.QuestionTitle;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class QusetionaireActivity extends BaseActivity implements
OnItemClickListener, OnItemLongClickListener, OnClickListener,
XListView.IXListViewListener {

	LinearLayout rootView = null;
	AbPopoverView popoverView = null ;

	private XListView question_list;
	private List<QuestionTitle> datas = new ArrayList<QuestionTitle>();

	private ImageView me;
	private QuestionAdapter adapter;
	private int curPosition;
	JSONArray jsonArray;
	

	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questionaire);
		findView();
		initData();
		initAction();
	}

	void findView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("问卷调查");
		headerLayout.showLeftBackButton(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				QusetionaireActivity.this.finish();
			}
		});
		rootView = (LinearLayout) findViewById(R.id.linearLayout);
		question_list = (XListView) findViewById(R.id.question_list);
		question_list.setPullRefreshEnable(true);
		question_list.setPullLoadEnable(false);
		question_list.setXListViewListener(this);
		adapter = new QuestionAdapter(ctx, datas, R.layout.item_question_list);
		question_list.setAdapter(adapter);
		question_list.setOnItemClickListener(this);
	}

	private void initData() {


		new SimpleNetTask(ctx) {
			List<QuestionTitle> temp = new ArrayList<QuestionTitle>();

			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				question_list.stopRefresh();
				if (temp == null) {
					Utils.toast("获取数据失败");
				} else {
					datas.clear();
					datas.addAll(temp);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				param.clear();
				param.put("userId", Utils.getID());
				String jsonStr = new WebService(C.GETQUESTIONARIE, param)
						.getReturnInfo();
				temp = GetObjectFromService.getQuestionList(jsonStr);
			}
		}.execute();
	}

	private class QuestionAdapter extends BaseListAdapter<QuestionTitle> {

		public QuestionAdapter(Context ctx, List<QuestionTitle> datas,
				int layoutId) {
			super(ctx, datas, layoutId);
		}

		@Override
		public void conver(ViewHolder holder, int position, QuestionTitle t) {
			holder.setText(R.id.question_title,
					position + 1 + " . " + t.getTitle());
			TextView statue = holder.getView(R.id.question_statue);
			if (t.isDone()) {
				statue.setText("已经完成");
				statue.setTextColor(getResources().getColor(R.color.grey_dark));
			} else {
				statue.setText("前往完成问卷");
				statue.setTextColor(getResources().getColor(R.color.next_color));
			}
		}

	}

	private void initAction() {
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		if (requestCode == 0 && resultCode == RESULT_OK) {
			datas.get(curPosition).setDone(true);
			adapter.notifyDataSetChanged();

			// 此局不确定
			// question_list.setAdapter(adapter);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initData();
	}

	public static void goMainActivity(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		initData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		curPosition = position;
		Intent intent = new Intent(getApplicationContext(),
				QuestionActivity.class);
		intent.putExtra("questionId", ((QuestionTitle) parent
				.getAdapter().getItem(position)).getId());
		intent.putExtra("questionName", ((QuestionTitle) parent
				.getAdapter().getItem(position)).getTitle());
		startActivity(intent);
	}


}
