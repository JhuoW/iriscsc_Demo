package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.adapter.mExpandableListAdapter;
import com.xzmc.airuishi.bean.City;
import com.xzmc.airuishi.bean.Province;
import com.xzmc.airuishi.db.MetaDao;
import com.xzmc.airuishi.utils.CharacterParser;
import com.xzmc.airuishi.utils.CityPinyinComparator;
import com.xzmc.airuishi.utils.DBUtil;
import com.xzmc.airuishi.utils.ProvincePinyinComparator;
import com.xzmc.airuishi.view.HeaderLayout;

public class SelectAddressActivity extends BaseActivity {

	private SQLiteDatabase mMetaDb;

	private mExpandableListAdapter myExpandAdapter;
	private ExpandableListView addresslist;
	private static CharacterParser characterParser;
	private static ProvincePinyinComparator pinyinComparator;
	private static CityPinyinComparator citypinyinComparator;

	private List<Province> provinces = new ArrayList<Province>();

	private LinearLayout ll_mylocation;
	private LinearLayout ll_islocation;
	private TextView tv_mylocation;

	private LocationClient mClient;
	private LocationClientOption mOption;
	private String mLBSAddress;
	public String mLocation;
	public double mLongitude;
	public double mLatitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selecteaddress);

		characterParser = CharacterParser.getInstance();
		pinyinComparator = new ProvincePinyinComparator();
		citypinyinComparator = new CityPinyinComparator();
		
		
		initLBS();
		initView();
		new Thread(new Runnable() {
			@Override
			public void run() {
				mClient.start();
				mClient.requestLocation();
			}
		}).start();

	}

	@Override
	protected void onStart() {
		super.onStart();
		initData();
		initAction();
	}

	private void initLBS() {
		mOption = new LocationClientOption();
		mOption.setOpenGps(true);
		mOption.setCoorType("bd09ll");
		mOption.setScanSpan(1000);
		mOption.setAddrType("all");
		mOption.disableCache(false);
		mOption.setPriority(LocationClientOption.GpsFirst);
		mClient = new LocationClient(getApplicationContext(), mOption);
	}

	private void initView() {
		View view = LayoutInflater.from(ctx).inflate(
				R.layout.selflocation_item, null);
		ll_mylocation = (LinearLayout) view.findViewById(R.id.layout_location);
		ll_islocation = (LinearLayout) view.findViewById(R.id.ll_islocation);
		tv_mylocation = (TextView) view.findViewById(R.id.tv_self_location);

		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		addresslist = (ExpandableListView) findViewById(R.id.address_expandablelistview);
		headerLayout.showTitle("选择地址");
		addresslist.setGroupIndicator(null);

		addresslist.addHeaderView(view);

	}

	private void initAction() {
		ll_mylocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tv_mylocation.getVisibility() == View.GONE) {
					return;
				} else {
					Intent intent = new Intent();
					intent.putExtra("address", tv_mylocation.getText()
							.toString());
					setResult(RESULT_OK, intent);
					SelectAddressActivity.this.finish();
				}
			}
		});

		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectAddressActivity.this.finish();
			}
		});
		addresslist.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				for (int i = 0; i < provinces.size(); i++) {
					if (groupPosition != i) {
						addresslist.collapseGroup(i);
					}
				}
			}

		});

		addresslist.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent();
				String pro = provinces.get(groupPosition).getName();
				String city = provinces.get(groupPosition).getCity()
						.get(childPosition).getName();
				String addr;
				if (pro.equals(city)) {
					addr = pro;
				} else {
					addr = pro + city;
				}
				intent.putExtra("address", addr);
				setResult(RESULT_OK, intent);
				SelectAddressActivity.this.finish();
				return true;
			}
		});
		mClient.registerLocationListener(new BDLocationListener() {

			public void onReceiveLocation(BDLocation arg0) {
				mLBSAddress = arg0.getAddrStr();
				mLocation = arg0.getAddrStr();
				mLatitude = arg0.getLatitude();
				mLongitude = arg0.getLongitude();
				mHandler.sendEmptyMessage(2);
			}

			public void onReceivePoi(BDLocation arg0) {
				
			}
		});
	}

	private void initData() {
		provinces = getProvince();
		provinces = convertProvince(provinces);
		List<Province> datas = new ArrayList<Province>();
		for (int i = 0; i < provinces.size(); i++) {
			List<City> citys = new ArrayList<City>();
			Province province = provinces.get(i);
			citys = MetaDao.getCitysByProvinceId(this, province.getId());
			citys = convertCity(citys);
			province.setCity(citys);
			datas.add(province);
		}
		provinces = datas;

		myExpandAdapter = new mExpandableListAdapter(
				SelectAddressActivity.this, provinces);
		addresslist.setAdapter(myExpandAdapter);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mLBSAddress != null) {
				tv_mylocation.setVisibility(View.VISIBLE);
				ll_islocation.setVisibility(View.GONE);
				tv_mylocation.setText(mLBSAddress);
			}
		}

	};

	private List<Province> convertProvince(List<Province> datas) {
		List<Province> sorts = new ArrayList<Province>();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			Province province = datas.get(i);
			String username = province.getName();
			if (username != null) {
				String pinyin = characterParser.getSelling(username);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				if (username.startsWith("重")) {
					sortString = "C";
				}
				if (sortString.matches("[A-Z]")) {
					province.setSortLetters(sortString.toUpperCase());
				} else {
					province.setSortLetters("#");
				}
			} else {
				province.setSortLetters("#");
			}
			sorts.add(province);
		}
		Collections.sort(sorts, pinyinComparator);
		return sorts;
	}

	private List<City> convertCity(List<City> datas) {
		List<City> sorts = new ArrayList<City>();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			City city = datas.get(i);
			String username = city.getName();
			if (username != null) {
				String pinyin = characterParser.getSelling(username);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				if (sortString.matches("[A-Z]")) {
					city.setSortLetters(sortString.toUpperCase());
				} else {
					city.setSortLetters("#");
				}
			} else {
				city.setSortLetters("#");
			}
			sorts.add(city);
		}
		Collections.sort(sorts, citypinyinComparator);
		return sorts;
	}

	private List<Province> getProvince() {
		List<Province> datas = new ArrayList<Province>();
		int i = 0;
		int j = 0;
		this.mMetaDb = DBUtil.getAppMetaDB(this);
		Cursor localCursor1 = this.mMetaDb.rawQuery("SELECT * FROM province",
				null);
		if ((localCursor1 != null) && (localCursor1.getCount() != 0)) {
			localCursor1.moveToFirst();
			i = localCursor1.getColumnIndex("id");
			j = localCursor1.getColumnIndex("name");
		}
		do {
			Province province = new Province();
			province.setId(localCursor1.getString(i));
			province.setName(localCursor1.getString(j));
			datas.add(province);
		} while (localCursor1.moveToNext());
		localCursor1.close();
		return datas;
	}

}
