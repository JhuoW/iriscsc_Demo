package com.xzmc.airuishi.activity_v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

public class PushActivity extends BaseActivity{

	Lesson lesson;
	  // 可滑动的显示选中用户的View
    private LinearLayout menuLinerLayout;
    private ListView listView;
    List<Student> list = new ArrayList<Student>();
    private int total = 0;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    private List<Student> addList = new ArrayList<Student>();
    ListAdapter adapter;
    boolean isAll = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push);
		lesson = (Lesson) getIntent().getSerializableExtra("post");
		initView();
		initAction();
		getMyStudent();
	}
	
	private void initView(){
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showLeftBackButton(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PushActivity.this.finish();
			}
		});
		headerLayout.showTitle("推送");
		headerLayout.showRightTextButton("全选", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isAll){
					deleteAll();
				}else{
					chooseAll();
				}
			}
		});
		listView = (ListView) findViewById(R.id.listview);
		
		menuLinerLayout = (LinearLayout) findViewById(R.id.linearLayoutMenu);
		
	}
	
	private void chooseAll(){
		adapter.addAll();
		adapter.notifyDataSetChanged();
		isAll = true;
	}
	
	private void deleteAll(){
		adapter.deleteAll();
		adapter.notifyDataSetChanged();
		isAll = false;
	}
	
	private void initAction(){
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();
            }
        });
	}
	
	private void getMyStudent(){
		new SimpleNetTask(ctx,true) {
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
						adapter = new ListAdapter(ctx, list);
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
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
	
	 //显示选择的头像
    private void showCheckImage(Student glufineid) {
        total++;
        // 包含TextView的LinearLayout
        // 参数设置
        android.widget.LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                60, 60, 1);
        View view = LayoutInflater.from(this).inflate(
                R.layout.header_item, null);
        ImageView images = (ImageView) view.findViewById(R.id.iv_avatar);
        TextView tv_name = (TextView) view.findViewById(R.id.name);
        menuLinerLayoutParames.setMargins(2, 2, 2, 2);
        // 设置id，方便后面删除
        view.setTag(glufineid);
        imageLoader
		.displayImage(
				glufineid.getImgUrl(),
				images,
				PhotoUtils
						.getImageOptions(R.drawable.default_face));

        menuLinerLayout.addView(view, menuLinerLayoutParames);
        tv_name.setText(glufineid.getNickName());
        addList.add(glufineid);
    }

	
	//删除选择的头像
    private void deleteImage(Student glufineid) {
        View view = (View) menuLinerLayout.findViewWithTag(glufineid);

        menuLinerLayout.removeView(view);
        total--;
        addList.remove(glufineid);
    }

	
	 private class ListAdapter extends BaseAdapter {

	        private LayoutInflater layoutInflater;
	        private Context context;
	        private List<Student> list = new ArrayList<Student>();
	        /**
	    	 * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	    	 */
	    	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();
	    	
	    	
	        public ListAdapter(Context context,List<Student> list){
	            this.context = context;
	            this.list = list;
	         // 初始化,默认都没有选中
	    		configCheckMap(false);
	        }

	        /**
	    	 * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
	    	 */
	    	public void configCheckMap(boolean bool) {

	    		for (int i = 0; i < list.size(); i++) {
	    			isCheckMap.put(i, bool);
	    		}

	    	}
	        
	        @Override
	        public int getCount() {
	            return this.list.size();
	        }

	        @Override
	        public Student getItem(int position) {
	            return list.get(position);
	        }

	        @Override
	        public long getItemId(int position) {
	            return position;
	        }

	    	public void addAll() {
	    		menuLinerLayout.removeAllViews();
	    		addList.clear();
    			adapter.configCheckMap(true);
	    		for (int i = 0; i < list.size(); i++) {
	    			showCheckImage(list.get(i));
	    		}
    			notifyDataSetChanged();

	    	}
	    	
	    	public void deleteAll() {
	    		menuLinerLayout.removeAllViews();
	    		addList.clear();
	    		adapter.configCheckMap(false);
	    		for (int i = 0; i < list.size(); i++) {
                    deleteImage(list.get(i));
	    		}
    			notifyDataSetChanged();
	    		addList.clear();

	    	}
	    	
	    	public Map<Integer, Boolean> getCheckMap() {
	    		return this.isCheckMap;
	    	}
	    	
	        @Override
	        public View getView(final int position, View convertView, ViewGroup parent) {
	            Student student = list.get(position);
	    		ViewGroup layout = null;
	            if (convertView == null) {
	                layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.listitem,parent,false);
	               
	            }else {
	    			layout = (ViewGroup) convertView;
	            }
	            
	            ImageView avatar = (ImageView) layout.findViewById(R.id.avatar);
	            TextView name = (TextView) layout.findViewById(R.id.name);
	            CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkbox);
                if (addList != null && addList.contains(student)) {
                	checkBox.setChecked(true);
                }
                if (checkBox != null){
                	checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        	/*
            				 * 将选择项加载到map里面寄存
            				 */
                        	isCheckMap.put(position, isChecked);
                        	if (isChecked) {
                                showCheckImage(list.get(position));
                            } else {
                                // 用户显示在滑动栏删除
                                deleteImage(list.get(position));
                            }
                        }
                    });
                }
	            
	            imageLoader
	    		.displayImage(
	    				student.getImgUrl(),
	    				avatar,
	    				PhotoUtils
	    						.getImageOptions(R.drawable.icon_default_avatar_selector));	 
	            name.setText(student.getNickName());
	            checkBox.setChecked(isCheckMap.get(position));
	        	ViewHolder holder = new ViewHolder();
	        	holder.avatar = avatar;
	        	holder.name = name;
	        	holder.checkBox = checkBox;
				layout.setTag(holder);

	            return layout;
	        }

	    }
	 public static class ViewHolder {
         ImageView avatar = null;
         TextView name = null;
         CheckBox checkBox = null;
      }
}
