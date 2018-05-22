package com.xzmc.airuishi.activity_v2;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chatuidemo.adapter.MyOptDocAdapter.ViewHolder;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Doc;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

public class MyAllOptDocActivity extends BaseActivity {
	ListView listview;
	List<Doc> docList = new ArrayList<Doc>();
	DocAdapter adapter;
	String userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myalloptdoc);
		userId = getIntent().getExtras().getString("userId",Utils.getID());
		initView();
		getOptDoc();
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("全部档案");
		headerLayout.showLeftBackButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyAllOptDocActivity.this.finish();
			}
		});
		listview = (ListView) findViewById(R.id.listview);
		adapter = new DocAdapter();
		listview.setAdapter(adapter);;
	}

	
	private void getOptDoc(){
		new SimpleNetTask(ctx,false) {
			List<Doc> temp = new ArrayList<Doc>();
			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				if(temp==null){
					Utils.toast("net error");
					return;
				}
				docList.clear();
				docList.addAll(temp);
				adapter.notifyDataSetChanged();
			}
			
			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				param.clear();
				param.put("userId", userId);
				String jsonstr = new WebService(C.GETMYOPTDOC, param).getReturnInfo();
				try {
					JSONObject json = new JSONObject(jsonstr);
					if(json.get("ret").equals("success")){
						JSONArray jsonarray = json.getJSONArray("optometrys");
						temp = GetObjectFromService.getMyOptDoc(jsonarray);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}.execute();
	}
	
	
	
	public class DocAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return docList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return docList.get(position);
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
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.item_allopt, null);
				holder.shade_left = (ImageView) convertView.findViewById(R.id.shade_left);
				holder.shade_right = (ImageView) convertView.findViewById(R.id.shade_right);
				holder.tv_time = (TextView) convertView.findViewById(R.id.time);
				holder.tv_SL_L = (TextView) convertView.findViewById(R.id.SL_L);
				holder.tv_SL_R = (TextView) convertView.findViewById(R.id.SL_R);
				holder.tv_CL_L = (TextView) convertView.findViewById(R.id.CL_L);
				holder.tv_CL_R = (TextView) convertView.findViewById(R.id.CL_R);
				holder.tv_Axial_L = (TextView) convertView.findViewById(R.id.Axial_L);
				holder.tv_Axial_R = (TextView) convertView.findViewById(R.id.Axial_R);
				holder.tv_Nv_L = (TextView) convertView.findViewById(R.id.Nv_L);
				holder.tv_Nv_R = (TextView) convertView.findViewById(R.id.Nv_R);
				holder.tv_Cv_L = (TextView) convertView.findViewById(R.id.Cv_L);
				holder.tv_Cv_R = (TextView) convertView.findViewById(R.id.Cv_R);
				holder.tv_AD = (TextView) convertView.findViewById(R.id.AD);
				holder.tv_PD = (TextView) convertView.findViewById(R.id.PD);
				holder.mColumnHorizontalScrollView = (HorizontalScrollView)convertView.findViewById(R.id.mColumnHorizontalScrollView);
				convertView.setTag(holder);
			} else {
	            holder = (ViewHolder) convertView.getTag();
			}
			
			Doc d = docList.get(position);
			holder.tv_time.setText(d.getTime().substring(3,8));
			holder.tv_SL_L.setText(d.getSL_L());
			holder.tv_SL_R.setText(d.getSL_R());
			holder.tv_CL_L.setText(d.getCv_L());
			holder.tv_CL_R.setText(d.getCL_R());
			holder.tv_Axial_L.setText(d.getAxial_L());
			holder.tv_Axial_R.setText(d.getAxial_R());
			holder.tv_Nv_L.setText(d.getNv_L());
			holder.tv_Nv_R.setText(d.getNv_R());
			holder.tv_Cv_L.setText(d.getCv_L());
			holder.tv_Cv_R.setText(d.getCv_R());
			holder.tv_AD.setText(d.getAD());
			holder.tv_PD.setText(d.getPD());
			return convertView;
		}

		public class ViewHolder {
			TextView tv_time;
			TextView tv_SL_L;
			TextView tv_SL_R;
			TextView tv_CL_L;
			TextView tv_CL_R;
			TextView tv_Axial_L;
			TextView tv_Axial_R;
			TextView tv_Nv_L;
			TextView tv_Nv_R;
			TextView tv_Cv_L;
			TextView tv_Cv_R;
			TextView tv_AD;
			TextView tv_PD;
			HorizontalScrollView mColumnHorizontalScrollView;

			/** 左阴影部分 */
			public ImageView shade_left;
			/** 右阴影部分 */
			public ImageView shade_right;
		}
	}
}
