package com.xzmc.airuishi.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.PostDetaileActivity;
import com.xzmc.airuishi.adapter.CenterListAdp;
import com.xzmc.airuishi.adapter.TopVpAdp;
import com.xzmc.airuishi.bean.PostModel;
import com.xzmc.airuishi.db.DBHelper;
import com.xzmc.airuishi.utils.NetAsyncTask;
import com.xzmc.airuishi.view.MyProgressDialog;
import com.zxmc.airuishi.http.APIHelper;

public class NewsDetailFragment extends Fragment {
//	private final static String TAG = "NewsFragment";
//	Activity activity;
//	HeadListView mListView;
//	CenterListAdp mAdapter;
//	String text;
//	int channel_id;
//	public final static int SET_NEWSLIST = 0;
//	//Toast��ʾ��
//	private RelativeLayout notify_view;
//	private TextView notify_view_text;
//	private ArrayList<PostModel> newsList = new ArrayList<PostModel>();
//	private List<PostModel> topPostModels = new ArrayList<PostModel>();
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		initData();
//		super.onCreate(savedInstanceState);
//	}
//
//	@Override
//	public void onAttach(Activity activity) {
//		this.activity = activity;
//		super.onAttach(activity);
//	}
//	
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
//		return view;
//	}
//
//	private void initData() {
//		System.out.println("id="+NewsFragment.channel_id+"text="+NewsFragment.channel_text);
//		handler.obtainMessage(SET_NEWSLIST).sendToTarget();
//	}
//	
//	Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case SET_NEWSLIST:
//				if(mAdapter == null){
//					}
//				}
//			super.handleMessage(msg);
//		}
//	};
//	
//	/* ��ʼ��ѡ����е�header*/
//	public void initCityChannel() {
//	}
//	
//	/* ��ʼ��֪ͨ��Ŀ*/
//	private void initNotify() {
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				notify_view_text.setText(String.format(getString(R.string.ss_pattern_update), 10));
//				notify_view.setVisibility(View.VISIBLE);
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						notify_view.setVisibility(View.GONE);
//					}
//				}, 2000);
//			}
//		}, 1000);
//	}
//	/* �ݻ���ͼ */
//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		mAdapter = null;
//	}
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//	}
//}

	Activity activity;
	private ArrayList<PostModel> newsList = new ArrayList<PostModel>();
	private List<PostModel> topPostModels = new ArrayList<PostModel>();

	private ViewPager topViewPager;
	private ListView mListView;
	private RadioGroup centerRadioGroup;

	
	private TopVpAdp topVpAdp;
	private String text;
	private String channel_id;
	private int currentPage = 1;
	private int currentPageSize = 20;
	private int w, h;
	public final static int SET_NEWSLIST = 0;
	private DBHelper dbHelper;
	MyProgressDialog dialog;
	private CenterListAdp mAdapter;
	private NetAsyncTask task;
	private RadioGroup.LayoutParams layoutParams;
	

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
		dialog = new MyProgressDialog(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("oncreatview");
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.news_fragment, null);
		mListView = (ListView) view.findViewById(R.id.mListView);
		topViewPager = (ViewPager) view.findViewById(R.id.centerViewPager);
		centerRadioGroup = (RadioGroup) view
				.findViewById(R.id.centerRadioGroup);
		w = getResources().getDrawable(R.drawable.radiobutton_normal)
				.getIntrinsicWidth();
		h = getResources().getDrawable(R.drawable.radiobutton_normal)
				.getIntrinsicHeight();
		layoutParams = new RadioGroup.LayoutParams(w, h);
		layoutParams.setMargins(0, 0, 30, 0);
		dbHelper = new DBHelper(getActivity());
		dbHelper.openSqLiteDatabase();
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
			//dialog.show();
	}
	@Override
	public void onResume() {
		super.onResume();
		text=NewsFragment.channel_text;
		channel_id=NewsFragment.channel_id;
		System.out.println("text="+text+"channel_id="+channel_id);
		initData();
		initAction();
	}

	private void initAction() {
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						PostDetaileActivity.class);
				intent.putExtra("post", newsList.get(position));
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		topViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < centerRadioGroup.getChildCount(); i++) {
					if (i == arg0) {
						centerRadioGroup.getChildAt(i).setSelected(true);
					} else {
						centerRadioGroup.getChildAt(i).setSelected(false);
					}
				}

			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		centerRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						for (int i = 0; i < centerRadioGroup.getChildCount(); i++) {
							if (i == checkedId) {
								centerRadioGroup.getChildAt(i)
										.setSelected(true);
							} else {
								centerRadioGroup.getChildAt(i).setSelected(
										false);
							}
						}
						topViewPager.setCurrentItem(checkedId);
					}
				});
	}

	private void initData() {
		
		// if (channel_id==1) {
		// topVpItems.clear();
		// topPostModels.clear();
		// List<PostModel> data = dbHelper.queryTopPosts("5");
		// topPostModels.addAll(data);
		// for (int i = 0; i < topPostModels.size(); i++) {
		// TopVpItem topVpItem = new TopVpItem();
		// topVpItem.relativeLayout = (RelativeLayout) getActivity()
		// .getLayoutInflater().inflate(
		// R.layout.lay_center_vp_item, null);
		// topVpItem.relativeLayout.setTag(i);
		// topVpItem.imageView = (ImageView) topVpItem.relativeLayout
		// .findViewById(R.id.ivViewPageItem);
		// topVpItem.textView = (TextView) topVpItem.relativeLayout
		// .findViewById(R.id.tvViewPageItem);
		// topVpItem.relativeLayout
		// .setOnClickListener(topImgsClickListenner);
		// topVpItems.add(topVpItem);
		// }
		// centerRadioGroup.removeAllViews();
		// for (int i = 0; i < topVpItems.size(); i++) {
		// RadioButton radioButton = new RadioButton(getActivity());
		// radioButton.setLayoutParams(layoutParams);
		// radioButton.setButtonDrawable(R.drawable.radiobutton_sel);
		// radioButton.setBackgroundResource(R.drawable.radiobutton_sel);
		// if (i == 0) {
		// radioButton.setSelected(true);
		// } else {
		// radioButton.setClickable(false);
		// }
		// centerRadioGroup.addView(radioButton, i);
		// }
		// topVpAdp = new TopVpAdp(topVpItems, topPostModels);
		// topViewPager.setAdapter(topVpAdp);
		// newsList.addAll(dbHelper.queryCenterPost(currentPage
		// * currentPageSize + ""));
		// } else {
		// topViewPager.setVisibility(View.GONE);
		// centerRadioGroup.setVisibility(View.GONE);
		// newsList.addAll(dbHelper.queryPostsByCategorySlug(slug, currentPage
		// * currentPageSize + ""));
		// }
		topViewPager.setVisibility(View.GONE);
		centerRadioGroup.setVisibility(View.GONE);
		newsList.addAll(dbHelper.queryPostsByCategoryId(channel_id + "",
				currentPage * currentPageSize + ""));
		mAdapter = new CenterListAdp(newsList, getActivity()
				.getLayoutInflater());
		mListView.setAdapter(mAdapter);
		task = (NetAsyncTask) new NetAsyncTask(getActivity(), false) {
			@Override
			protected void onPost(Exception e) {
				List<PostModel> newdata;
				// if (channel_id==1) {
				// newdata =
				// dbHelper.queryPostsByCategoryId(channel_id+"",currentPage
				// * currentPageSize + "");
				// topVpItems = new ArrayList<TopVpItem>();
				// topPostModels.clear();
				// //List<PostModel> models = dbHelper.queryTopPosts();
				// //topPostModels.addAll(models);
				//
				// for (int i = 0; i < topPostModels.size(); i++) {
				// TopVpItem topVpItem = new TopVpItem();
				// topVpItem.relativeLayout = (RelativeLayout) getActivity()
				// .getLayoutInflater().inflate(
				// R.layout.lay_center_vp_item, null);
				// topVpItem.relativeLayout.setTag(i);
				// topVpItem.imageView = (ImageView) topVpItem.relativeLayout
				// .findViewById(R.id.ivViewPageItem);
				// topVpItem.textView = (TextView) topVpItem.relativeLayout
				// .findViewById(R.id.tvViewPageItem);
				// topVpItem.relativeLayout
				// .setOnClickListener(topImgsClickListenner);
				// topVpItems.add(topVpItem);
				// }
				// topVpAdp = new TopVpAdp(topVpItems, topPostModels);
				// topViewPager.setAdapter(topVpAdp);
				// centerRadioGroup.removeAllViews();
				// for (int i = 0; i < topVpItems.size(); i++) {
				// RadioButton radioButton = new RadioButton(getActivity());
				// radioButton.setLayoutParams(layoutParams);
				// radioButton
				// .setButtonDrawable(R.drawable.radiobutton_sel);
				// radioButton
				// .setBackgroundResource(R.drawable.radiobutton_sel);
				// if (i == 0) {
				// radioButton.setSelected(true);
				// } else {
				// radioButton.setClickable(false);
				// }
				// centerRadioGroup.addView(radioButton, i);
				// }
				//
				// } else {
				newdata = dbHelper.queryPostsByCategoryId(channel_id + "",
						currentPage * currentPageSize + "");
				// }

				newsList.clear();
				newsList.addAll(newdata);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				mAdapter.notifyDataSetChanged();

			}

			@Override
			protected void doInBack() throws Exception {
				if ((String.valueOf(channel_id)).equals("1")) {
					String jsonstr = new APIHelper().getPostsByCategory(
							channel_id + "", currentPage, currentPageSize);
					// String jsonstoptr = new APIHelper().getTopPosts(
					// currentPage, currentPageSize);
					System.out.println(channel_id);
					System.out.println("##"+jsonstr);
					
					try {
						// JSONObject topjson = new JSONObject(jsonstoptr);
						JSONObject json = new JSONObject(jsonstr);
						if (json.get("ret").equals("success")) {
							JSONArray jsonarray = json.getJSONArray("newsList");
							dbHelper.insertPosts(jsonarray, channel_id);
						}
						// if (topjson.get("status").equals("ok")) {
						// JSONArray topjsonarray = topjson
						// .getJSONArray("posts");
						// dbHelper.insertPosts(topjsonarray,channel_id);
						// }
					} catch (Exception e) {
					}
				} else {
					String jsonstr = new APIHelper().getPostsByCategory(
							channel_id + "", currentPage, currentPageSize);
					try {
						JSONObject json = new JSONObject(jsonstr);
						if (json.get("status").equals("ok")) {
							JSONArray jsonarray = json.getJSONArray("posts");
							dbHelper.insertPosts(jsonarray, channel_id);
						}
					} catch (Exception e) {
					}
				}
			}
		}.executeOnExecutor(Executors.newCachedThreadPool());
	}

	/** ����õ�Ƭ */
	private View.OnClickListener topImgsClickListenner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int index = (Integer) v.getTag();// index
			Intent intent = new Intent(getActivity(), PostDetaileActivity.class);
			intent.putExtra("post",
					topPostModels.get(index).getId());
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.anim_r_to_0,
					R.anim.anim_0_to_l);
		}
	};

	/* �ݻ���ͼ */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
			task.cancel(true);
		}
		task = null;
		mAdapter = null;
	}

	/* �ݻٸ�Fragment��һ����FragmentActivity ���ݻٵ�ʱ������Ŵݻ� */
	@Override
	public void onDestroy() {
		super.onDestroy();
		onDestroyView();
	}
}
