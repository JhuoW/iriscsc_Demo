package com.xzmc.airuishi.activity_v2;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Teacher;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class MyTeacherActivity extends BaseActivity implements
XListView.IXListViewListener {
	XListView listview;
	List<Teacher> list = new ArrayList<Teacher>();
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	TeacherAdapter adapter;
	int refresh = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myclient);
		initView();
		onRefresh();
		initAction();
	}

	private void initView(){
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyTeacherActivity.this.finish();
			}
		});
		headerLayout.showTitle("我的讲师");
		listview = (XListView) findViewById(R.id.listview);
		listview.setPullRefreshEnable(true);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
		adapter = new TeacherAdapter();
		listview.setAdapter(adapter);
	}
	
	private void initAction(){
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Teacher t = (Teacher) parent.getAdapter().getItem(position);
				Intent intent = new Intent(MyTeacherActivity.this, TeacherDetailActivity.class);
				intent.putExtra("post", t);
				startActivity(intent);
			}
		});
	}
	
	private void getTeacher(){
		new SimpleNetTask(ctx,refresh==1) {
			List<Teacher> temp = new ArrayList<Teacher>();
			String jsonstr;
			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				listview.stopRefresh();
				try {
					JSONObject json = new JSONObject(jsonstr);
					if (json.get("ret").equals("success")) {
						JSONArray jsonarray = json.getJSONArray("students");
						for (int i = 0; i < jsonarray.length(); i++) {
							Teacher s = new Teacher();
							JSONObject obj = jsonarray.getJSONObject(i);
							s.setUserId(obj.getString("userId"));
							s.setNickName(obj.getString("nickName"));
							s.setImgUrl(obj.getString("imgUrl"));
							s.setSex(obj.getString("sex"));
							s.setPhone(obj.getString("phone"));
							s.setStatus(obj.getString("status"));
							s.setPosition(obj.getString("position"));
							s.setIntroduction(obj.getString("introduction"));
							temp.add(s);
						}
						if (temp == null) {
							Utils.toast("net error");
							return;
						}
						list.clear();
						list.addAll(temp);
						adapter.notifyDataSetChanged();
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
				param.put("id", 0 + "");
				param.put("count", 200 + "");
				jsonstr = new WebService(C.getMyTeacherList, param).getReturnInfo();
			}
		}.execute();
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getTeacher();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	
	public class TeacherAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView ==null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(MyTeacherActivity.this).inflate(R.layout.item_student, null);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
				holder.rl_student = (RelativeLayout) convertView.findViewById(R.id.rl_student);
				convertView.setTag(holder);;
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			Teacher s = list.get(position);
			holder.name.setText(s.getNickName());
			imageLoader.displayImage(s.getImgUrl(),
					holder.avatar,
					PhotoUtils.getImageOptions(R.drawable.icon_default_avatar));
			return convertView;
		}
		public class ViewHolder{
			TextView name;
			ImageView avatar;
			RelativeLayout rl_student;
		}
	}
}
