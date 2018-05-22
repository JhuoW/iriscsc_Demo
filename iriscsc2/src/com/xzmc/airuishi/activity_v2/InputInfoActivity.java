package com.xzmc.airuishi.activity_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;

public class InputInfoActivity extends BaseActivity {

	EditText sl_l;
	EditText sl_r;
	EditText cl_l;
	EditText cl_r;
	EditText axial_l;
	EditText axial_r;
	EditText nv_l;
	EditText nv_r;
	EditText cv_l;
	EditText cv_r;
	EditText ad;
	EditText pd;
	Button send;
	String touserid;
	
	public static final int SENDREQUEST = 1000;
	public static final int SENDRESULT  = 2000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inputinfo);
		touserid = getIntent().getStringExtra("userId");
		initView();
		initAction();
	}
	private void initView(){
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showLeftBackButton(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputInfoActivity.this.finish();
			}
		});
		headerLayout.showTitle("录入");
		sl_l = (EditText) findViewById(R.id.sl_l);
		sl_r = (EditText) findViewById(R.id.sl_r);
		cl_l = (EditText) findViewById(R.id.cl_l);
		cl_r = (EditText) findViewById(R.id.cl_r);
		axial_l = (EditText) findViewById(R.id.axial_l);
		axial_r = (EditText) findViewById(R.id.axial_r);
		nv_l = (EditText) findViewById(R.id.nv_l);
		nv_r = (EditText) findViewById(R.id.nv_r);
		cv_l = (EditText) findViewById(R.id.cv_l);
		cv_r = (EditText) findViewById(R.id.cv_r);
		ad = (EditText) findViewById(R.id.ad);
		pd = (EditText) findViewById(R.id.pd);
		send = (Button) findViewById(R.id.send);
	}
	
	private void initAction(){
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String Sl_l = sl_l.getText().toString();
				final String Sl_r = sl_r.getText().toString();
				final String Cl_l = cl_l.getText().toString(); 
				final String Cl_r = cl_r.getText().toString(); 
				final String Axial_l = axial_l.getText().toString(); 
				final String Axial_r = axial_r.getText().toString(); 
				final String Nv_l = nv_l.getText().toString(); 
				final String Nv_r = nv_r.getText().toString(); 
				final String Cv_l = cv_l.getText().toString(); 
				final String Cv_r = cv_r.getText().toString(); 
				final String Ad = ad.getText().toString(); 
				final String Pd = pd.getText().toString(); 
				new SimpleNetTask(ctx,true) {
					boolean flag;
					@Override
					protected void onSucceed() {
						// TODO Auto-generated method stub
						if(flag){
							Utils.toast("提交成功");
							Intent intent = new Intent();
							   setResult(SENDRESULT,intent);
							InputInfoActivity.this.finish(); 
							
						}else{
							Utils.toast("提交失败,请重试");
						}
					}
					
					@Override
					protected void doInBack() throws Exception {
						// TODO Auto-generated method stub
						param.clear();
						param.put("fromUserId", Utils.getID());
						param.put("toUserId", touserid);
						param.put("SL_L", Sl_l);
						param.put("SL_R", Sl_r);
						param.put("CL_L", Cl_l);
						param.put("CL_R", Cl_r);
						param.put("Axial_L", Axial_l);
						param.put("Axial_R", Axial_r);
						param.put("Nv_L", Nv_l);
						param.put("Nv_R", Nv_r);
						param.put("Cv_L", Cv_l);
						param.put("Cv_R", Cv_r);
						param.put("AD", Ad);
						param.put("PD", Pd);
						String jsonstr = new WebService(C.addOptometryInfo, param).getReturnInfo();
						flag = GetObjectFromService.getSimplyResult(jsonstr);
					}
				}.execute();
			}
		});
	}
	
	private void methodToString(EditText et){
		String Sl_l = sl_l.getText().toString();
		String Sl_r = sl_r.getText().toString();
		String Cl_l = cl_l.getText().toString(); 
		String Cl_r = cl_r.getText().toString(); 
		String Axial_l = axial_l.getText().toString(); 
		String Axial_r = axial_r.getText().toString(); 
		String Nv_l = nv_l.getText().toString(); 
		String Nv_r = nv_r.getText().toString(); 
		String Cv_l = cv_l.getText().toString(); 
		String Cv_r = cv_r.getText().toString(); 
		String Ad = ad.getText().toString(); 
		String Pd = pd.getText().toString(); 
	}
	
}
