package com.xzmc.airuishi.activity_v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Lesson;
import com.xzmc.airuishi.bean.Student;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

public class PushActivity2 extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	Lesson lesson;
	// 可滑动的显示选中用户的View
	public LinearLayout menuLinerLayout;
	public ListView listView;
	List<Student> list = new ArrayList<Student>();
	public int total = 0;
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	public List<Student> addList = new ArrayList<Student>();
	public DemoAdapter adpAdapter = null;
	boolean isAll = false;
	Button btn_confirm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push);
		lesson = (Lesson) getIntent().getSerializableExtra("post");
		initView();
		getMyStudent();
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PushActivity2.this.finish();
			}
		});
		headerLayout.showTitle("推送");
		btn_confirm = (Button) findViewById(R.id.confirm);
		headerLayout.showRightTextButton("全选", new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isAll) {
					addList.clear();
					chooseAll(false);
				} else {
					addList.clear();
					chooseAll(true);
				}

			}
		});
		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		menuLinerLayout = (LinearLayout) findViewById(R.id.linearLayoutMenu);
		btn_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				push();
			}
		});
	}

	public void push() {
		if (addList.size() == 0) {
			Utils.toast("未选择学员");
			return;
		}
		JSONArray json = new JSONArray();
		for(int i = 0;i<addList.size();i++){
			JSONObject jo = new JSONObject();
			try {
				jo.put("Id", addList.get(i).getUserId());
				json.put(jo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		final String sendUsers = "{\"sendUsers\":" + json + "}";
		System.out.println("sendUsers:"+sendUsers);
		new SimpleNetTask(ctx, true) {
			boolean flag;
			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				if(flag){
					Utils.toast("推送成功");
				}else{
					Utils.toast("推送失败");
				}
			}

			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				param.clear();
				param.put("userID", Utils.getID());
				param.put("courseId", lesson.getCourseId());
				param.put("sendUsers", sendUsers);
				String jsonstr = new WebService(C.push, param).getReturnInfo();
				flag = GetObjectFromService.getSimplyResult(jsonstr);
			}
		}.execute();
	}

	private void chooseAll(boolean t) {
		adpAdapter.configCheckMap(t);
		adpAdapter.notifyDataSetChanged();
		isAll = t;
	}

	private void getMyStudent() {
		new SimpleNetTask(ctx, true) {
			List<Student> temp = new ArrayList<Student>();
			String jsonstr;

			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				try {
					JSONObject json = new JSONObject(jsonstr);
					if (json.get("ret").equals("success")) {
						JSONArray jsonarray = json.getJSONArray("students");
						for (int i = 0; i < jsonarray.length(); i++) {
							Student s = new Student();
							JSONObject obj = jsonarray.getJSONObject(i);
							s.setUserId(obj.getString("userId"));
							s.setNickName(obj.getString("nickName"));
							s.setImgUrl(obj.getString("imgUrl"));
							s.setSex(obj.getString("sex"));
							s.setPhone(obj.getString("phone"));
							s.setStatue(obj.getString("status"));
							temp.add(s);
						}
						if (temp == null) {
							Utils.toast("net error");
							return;
						}

						list.clear();
						list.addAll(temp);
						adpAdapter = new DemoAdapter(ctx, list);
						listView.setAdapter(adpAdapter);
					} else {
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
				param.put("id", 0 + "");
				param.put("count", 200 + "");
				jsonstr = new WebService(C.getMyStudentList, param)
						.getReturnInfo();
			}
		}.execute();
	}

	public void deleteImage(Student glufineid) {
		View view = (View) menuLinerLayout.findViewWithTag(glufineid);

		menuLinerLayout.removeView(view);
		total--;
		addList.remove(glufineid);
	}

	public void showCheckImage(Student glufineid) {
		android.widget.LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
				60, 60, 1);
		View view = LayoutInflater.from(ctx)
				.inflate(R.layout.header_item, null);
		ImageView images = (ImageView) view.findViewById(R.id.iv_avatar);
		TextView tv_name = (TextView) view.findViewById(R.id.name);
		menuLinerLayoutParames.setMargins(2, 2, 2, 2);
		view.setTag(glufineid);
		imageLoader.displayImage(glufineid.getImgUrl(), images,
				PhotoUtils.getImageOptions(R.drawable.default_face));

		menuLinerLayout.addView(view, menuLinerLayoutParames);
		tv_name.setText(glufineid.getNickName());
		addList.add(glufineid);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View itemLayout,
			int position, long id) {
		// TODO Auto-generated method stub
		if (itemLayout.getTag() instanceof ViewHolder) {

			ViewHolder holder = (ViewHolder) itemLayout.getTag();

			holder.checkBox.toggle();

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public class DemoAdapter extends BaseAdapter {

		private Context context = null;

		private List<Student> datas = null;
		private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

		public DemoAdapter(Context context, List<Student> datas) {
			this.datas = datas;
			this.context = context;

			configCheckMap(false);
		}

		public void configCheckMap(boolean bool) {

			for (int i = 0; i < datas.size(); i++) {
				isCheckMap.put(i, bool);
			}

		}

		@Override
		public int getCount() {
			return datas == null ? 0 : datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewGroup layout = null;
			if (convertView == null) {
				layout = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.listitem, parent, false);
			} else {
				layout = (ViewGroup) convertView;
			}
			final Student bean = datas.get(position);
			ImageView avatar = (ImageView) layout.findViewById(R.id.avatar);
			imageLoader.displayImage(bean.getImgUrl(), avatar,
					PhotoUtils.getImageOptions(R.drawable.default_face));
			TextView name = (TextView) layout.findViewById(R.id.name);
			name.setText(bean.getNickName());
			CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkbox);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					isCheckMap.put(position, isChecked);
					if (addList.contains(bean)) {
						deleteImage(bean);		
						}
					if(addList.size()==0){
						menuLinerLayout.removeAllViews();
					}
					if (isChecked) {
						showCheckImage(bean);
					} else {
						deleteImage(bean);
					}

				}
			});
			if (isCheckMap.get(position) == null) {
				isCheckMap.put(position, false);
			}
			checkBox.setChecked(isCheckMap.get(position));

			ViewHolder holder = new ViewHolder();

			holder.checkBox = checkBox;

			holder.name = name;

			layout.setTag(holder);
			return layout;
		}

		public void remove(int position) {
			this.datas.remove(position);
		}

		public Map<Integer, Boolean> getCheckMap() {
			return this.isCheckMap;
		}

		public List<Student> getDatas() {
			return datas;
		}
	}

	public static class ViewHolder {
		public ImageView avatar = null;
		public TextView name = null;
		public CheckBox checkBox = null;
	}
}
