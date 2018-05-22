package com.xzmc.airuishi.activity_v2;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.activity.MainActivity;
import com.xzmc.airuishi.bean.Doc;
import com.xzmc.airuishi.bean.PostModel;
import com.xzmc.airuishi.view.ColumnHorizontalScrollView;
import com.xzmc.airuishi.view.HeaderLayout;

public class MyOptDocDetailActivity extends BaseActivity {

	Doc d;

	List<String> list = new ArrayList<String>();
	
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
	private HorizontalScrollView mColumnHorizontalScrollView;

	/** 左阴影部分 */
	public ImageView shade_left;
	/** 右阴影部分 */
	public ImageView shade_right;
	/** 屏幕宽度 */
	private int mScreenWidth = 0;
	/** Item宽度 */
	private int mItemWidth = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_optdetail);
		Intent intent = getIntent();
		initView();
		if(setData(intent)){
			
		}else{
			startActivity(new Intent(this, MainActivity.class));
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			finish();
		}
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("视光详情");
		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyOptDocDetailActivity.this.finish();
			}
		});
		shade_left = (ImageView) findViewById(R.id.shade_left);
		shade_right = (ImageView) findViewById(R.id.shade_right);
		tv_time = (TextView) findViewById(R.id.time);
		tv_SL_L = (TextView) findViewById(R.id.SL_L);
		tv_SL_R = (TextView) findViewById(R.id.SL_R);
		tv_CL_L = (TextView) findViewById(R.id.CL_L);
		tv_CL_R = (TextView) findViewById(R.id.CL_R);
		tv_Axial_L = (TextView) findViewById(R.id.Axial_L);
		tv_Axial_R = (TextView) findViewById(R.id.Axial_R);
		tv_Nv_L = (TextView) findViewById(R.id.Nv_L);
		tv_Nv_R = (TextView) findViewById(R.id.Nv_R);
		tv_Cv_L = (TextView) findViewById(R.id.Cv_L);
		tv_Cv_R = (TextView) findViewById(R.id.Cv_R);
		tv_AD = (TextView) findViewById(R.id.AD);
		tv_PD = (TextView) findViewById(R.id.PD);
		mColumnHorizontalScrollView = (HorizontalScrollView)findViewById(R.id.mColumnHorizontalScrollView);

	}
	
	private boolean setData(Intent intent){
		d = (Doc) intent.getSerializableExtra("post");
		tv_time.setText(d.getTime().substring(3,8));
		tv_SL_L.setText(d.getSL_L());
		tv_SL_R.setText(d.getSL_R());
		tv_CL_L.setText(d.getCv_L());
		tv_CL_R.setText(d.getCL_R());
		tv_Axial_L.setText(d.getAxial_L());
		tv_Axial_R.setText(d.getAxial_R());
		tv_Nv_L.setText(d.getNv_L());
		tv_Nv_R.setText(d.getNv_R());
		tv_Cv_L.setText(d.getCv_L());
		tv_Cv_R.setText(d.getCv_R());
		tv_AD.setText(d.getAD());
		tv_PD.setText(d.getPD());
		return true;
	}

}
