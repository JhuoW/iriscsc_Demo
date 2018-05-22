package com.xzmc.airuishi.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.PostDetailWebViewActivity;
import com.xzmc.airuishi.activity.PostDetaileActivity;
import com.xzmc.airuishi.adapter.CenterListAdp;
import com.xzmc.airuishi.adapter.HorizontalListViewAdapter;
import com.xzmc.airuishi.adapter.TopVpAdp;
import com.xzmc.airuishi.bean.CategoryModel;
import com.xzmc.airuishi.bean.PostModel;
import com.xzmc.airuishi.bean.TopVpItem;
import com.xzmc.airuishi.db.DBHelper;
import com.xzmc.airuishi.utils.BaseTools;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.view.ColumnHorizontalScrollView;
import com.xzmc.airuishi.view.HorizontalListView;
import com.xzmc.airuishi.view.MyProgressDialog;
import com.zxmc.airuishi.http.APIHelper;

public class NewsFragment extends Fragment {
	private List<CategoryModel> userChannelList = new ArrayList<CategoryModel>();
	private List<PostModel> topPostModels = new ArrayList<PostModel>();
	private List<PostModel> newsList = new ArrayList<PostModel>();
	private List<TopVpItem> topVpItems = new ArrayList<TopVpItem>();
	private ListView news_listview;
	private RelativeLayout relaCenterVpParent;
	private ViewPager centerViewPager;
	private RadioGroup centerRadioGroup;
	private TopVpAdp topVpAdp;

	public static String channel_id;
	public static String channel_text;

	private boolean showTopNews = true;

	private DBHelper dbHelper;
	private CenterListAdp mAdapter;
	private MyProgressDialog dialog;

	private int w, h;
	private RadioGroup.LayoutParams layoutParams;

	LinearLayout mRadioGroup_content;
	RelativeLayout rl_column;
	private ViewPager mViewPager;
	/** 用户选择的新闻分类列表 */
	/** 当前选中的栏目 */
	private int columnSelectIndex = 0;
	/** 左阴影部分 */
	public ImageView shade_left;
	/** 右阴影部分 */
	public ImageView shade_right;
	/** 屏幕宽度 */
	private int mScreenWidth = 0;
	/** Item宽度 */
	private int mItemWidth = 0;

	private ColumnHorizontalScrollView mColumnHorizontalScrollView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_news_layout, container,
				false);
		news_listview = (ListView) view.findViewById(R.id.news_listview);
		shade_left = (ImageView) view.findViewById(R.id.shade_left);
		shade_right = (ImageView) view.findViewById(R.id.shade_right);
		relaCenterVpParent = (RelativeLayout) view
				.findViewById(R.id.relaCenterVpParent);
		centerViewPager = (ViewPager) view.findViewById(R.id.centerViewPager);
		centerRadioGroup = (RadioGroup) view
				.findViewById(R.id.centerRadioGroup);

		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) view
				.findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) view
				.findViewById(R.id.mRadioGroup_content);
		rl_column = (RelativeLayout) view.findViewById(R.id.rl_column);

		w = getResources().getDrawable(R.drawable.radiobutton_normal)
				.getIntrinsicWidth();
		h = getResources().getDrawable(R.drawable.radiobutton_normal)
				.getIntrinsicHeight();
		layoutParams = new RadioGroup.LayoutParams(w, h);
		layoutParams.setMargins(0, 0, 30, 0);

		mScreenWidth = BaseTools.getWindowsWidth(getActivity());
		mItemWidth = mScreenWidth / 4;
		dialog = new MyProgressDialog(getActivity());
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		initAction();
	}

	private void initAction() {
		news_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						PostDetailWebViewActivity.class);
				intent.putExtra("post", newsList.get(position));
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		centerViewPager.setOnPageChangeListener(new OnPageChangeListener() {
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
						centerViewPager.setCurrentItem(checkedId);
					}
				});
	}

	private void initData() {
		dbHelper = new DBHelper(getActivity());
		dbHelper.openSqLiteDatabase();
		userChannelList = (ArrayList<CategoryModel>) dbHelper
				.queryAllCategory();
		if (userChannelList.size() != 0) {
			channel_text = userChannelList.get(0).getTitle();
			channel_id = userChannelList.get(0).getId();
			initTabColumn();
		}

	}

	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
		channel_text = userChannelList.get(tab_postion).getTitle();
		channel_id = userChannelList.get(tab_postion).getId();
		if (tab_postion != 0) {
			showTopNews = false;
		} else {
			showTopNews = true;
		}
		getNewsData(channel_id);
	}

	/**
	 * 初始化Column栏目项
	 * */
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		int count = userChannelList.size();
		mColumnHorizontalScrollView.setParam(getActivity(), mScreenWidth,
				mRadioGroup_content, shade_left, shade_right, rl_column);
		for (int i = 0; i < count; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					mItemWidth, LayoutParams.WRAP_CONTENT);
			params.leftMargin = 5;
			params.rightMargin = 5;
			TextView columnTextView = new TextView(getActivity());
			columnTextView.setTextAppearance(getActivity(),
					R.style.top_category_scroll_view_item_text);
			columnTextView.setBackgroundResource(R.drawable.radio_button_bg);
			columnTextView.setGravity(Gravity.CENTER);
			columnTextView.setPadding(5, 5, 5, 5);
			columnTextView.setId(i);
			columnTextView.setText(userChannelList.get(i).getTitle());
			columnTextView.setTextColor(getResources().getColorStateList(
					R.color.top_category_scroll_text_color_day));
			if (columnSelectIndex == i) {
				columnTextView.setSelected(true);
			}
			columnTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
						View localView = mRadioGroup_content.getChildAt(i);
						if (localView != v)
							localView.setSelected(false);
						else {
							selectTab(i);
						}

					}
				}
			});
			mRadioGroup_content.addView(columnTextView, i, params);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getNewsData(channel_id);
	}

	private int currentPage = 1;
	private int currentPageSize = 40;

	private void getNewsData(final String channel_id) {
		new SimpleNetTask(getActivity(), false) {
			@Override
			protected void onSucceed() {
				if (showTopNews) {
					relaCenterVpParent.setVisibility(View.VISIBLE);
					topPostModels.clear();
					topVpItems.clear();
					List<PostModel> models = dbHelper.queryPostsByCategoryId(-1
							+ "", currentPage * currentPageSize + "");
					topPostModels.addAll(models);

					for (int i = 0; i < topPostModels.size(); i++) {
						TopVpItem topVpItem = new TopVpItem();
						topVpItem.relativeLayout = (RelativeLayout) getActivity()
								.getLayoutInflater().inflate(
										R.layout.lay_center_vp_item, null);
						topVpItem.relativeLayout.setTag(i);
						topVpItem.imageView = (ImageView) topVpItem.relativeLayout
								.findViewById(R.id.ivViewPageItem);
						topVpItem.textView = (TextView) topVpItem.relativeLayout
								.findViewById(R.id.tvViewPageItem);
						topVpItem.relativeLayout
								.setOnClickListener(topImgsClickListenner);
						topVpItems.add(topVpItem);
					}
					topVpAdp = new TopVpAdp(topVpItems, topPostModels);
					centerViewPager.setAdapter(topVpAdp);
					centerRadioGroup.removeAllViews();
					for (int i = 0; i < topVpItems.size(); i++) {
						RadioButton radioButton = new RadioButton(getActivity());
						radioButton.setLayoutParams(layoutParams);
						radioButton
								.setButtonDrawable(R.drawable.radiobutton_sel);
						radioButton
								.setBackgroundResource(R.drawable.radiobutton_sel);
						if (i == 0) {
							radioButton.setSelected(true);
						} else {
							radioButton.setClickable(false);
						}
						centerRadioGroup.addView(radioButton, i);
					}

				} else {
					relaCenterVpParent.setVisibility(View.GONE);
				}

				List<PostModel> newdata;
				newdata = dbHelper.queryPostsByCategoryId(channel_id + "",
						currentPage * currentPageSize + "");
				newsList.clear();
				newsList.addAll(newdata);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				mAdapter = new CenterListAdp(newsList, getActivity()
						.getLayoutInflater());
				news_listview.setAdapter(mAdapter);
			}

			@Override
			protected void doInBack() throws Exception {
				if (showTopNews) {
					String jsonstr = new APIHelper().getTopPosts();
					try {
						JSONObject json = new JSONObject(jsonstr);
						if (json.get("ret").equals("success")) {
							JSONArray jsonarray = json
									.getJSONArray("advertList");
							dbHelper.insertPosts(jsonarray, -1 + "");
						}
					} catch (Exception e) {
					}
				}
				String jsonstr = new APIHelper().getPostsByCategory(channel_id
						+ "", currentPage, currentPageSize);
				try {
					JSONObject json = new JSONObject(jsonstr);
					if (json.get("ret").equals("success")) {
						JSONArray jsonarray = json.getJSONArray("newsList");
						dbHelper.insertPosts(jsonarray, channel_id);
					}
				} catch (Exception e) {
				}
			}
		}.execute();
	}

	private View.OnClickListener topImgsClickListenner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int index = (Integer) v.getTag();// index
			Intent intent = new Intent(getActivity(), PostDetailWebViewActivity.class);
			intent.putExtra("post", topPostModels.get(index));
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.anim_r_to_0,
					R.anim.anim_0_to_l);
		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mAdapter = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		onDestroyView();
	}
}
