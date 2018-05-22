package com.xzmc.airuishi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity_v2.LessonDetailActivity;
import com.xzmc.airuishi.activity_v2.StudentDetailActivity;
import com.xzmc.airuishi.adapter.StudentAdapter;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Lesson;
import com.xzmc.airuishi.bean.Student;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class StudentFragment extends Fragment  implements
XListView.IXListViewListener  {
	XListView listview;
	StudentAdapter adapter;
	List<Student> list = new ArrayList<Student>();
	Lesson lesson;
	int refresh = 1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_student, null);
		lesson = LessonDetailActivity.lesson;
		listview = (XListView) view.findViewById(R.id.listview);
		listview.setPullRefreshEnable(true);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
		adapter = new StudentAdapter(getActivity(), list);
		listview.setAdapter(adapter);
		onRefresh();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initAction();
	}

	private void initAction(){
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Student s = (Student) parent.getAdapter().getItem(position);
				Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
				intent.putExtra("post", s);
				startActivity(intent);
			}
		});
	}
	
	
	private void getData(){
		new SimpleNetTask(getActivity(),refresh==1) {
			List<Student> temp = new ArrayList<Student>();
			String jsonstr;
			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				listview.stopRefresh();
				try {
					JSONObject json = new JSONObject(jsonstr);
					if(json.get("ret").equals("success")){
						JSONArray jsonarray = json.getJSONArray("students");
						for(int i = 0;i<jsonarray.length();i++){
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
						if(temp==null){
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
				Map<String, String> param = new HashMap<String, String>();
				param.clear();
				param.put("userId", Utils.getID());
				param.put("courseId",lesson.getCourseId());
				jsonstr = new WebService(C.GETCOURSESTUD, param).getReturnInfo();
			}
		}.execute();
		refresh++;
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	
	
}
