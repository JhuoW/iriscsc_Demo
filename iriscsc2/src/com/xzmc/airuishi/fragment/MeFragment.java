package com.xzmc.airuishi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.AboutusActivity;
import com.xzmc.airuishi.activity.ChangeInformationActivity;
import com.xzmc.airuishi.activity.MyCollectionActivity;
import com.xzmc.airuishi.activity.SettingActivity;
import com.xzmc.airuishi.activity.SuggestionActivity;
import com.xzmc.airuishi.activity_v2.MyAllStudentActivity;
import com.xzmc.airuishi.activity_v2.MyArticleActivity;
import com.xzmc.airuishi.activity_v2.MyClientActivity;
import com.xzmc.airuishi.activity_v2.MyLessonActivity;
import com.xzmc.airuishi.activity_v2.MyOptDocActivity;
import com.xzmc.airuishi.activity_v2.MyTeacherActivity;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.RoundImageView;

public class MeFragment extends Fragment {
	private RoundImageView avatar;
	private TextView name, sex, id,tv_authority;
	private RelativeLayout layout_infor,
			layout_suggestion,
			layout_myarticle,   //我的文章
			layout_myclient,    //我的客户
			layout_mystud,    //我的学员
			layout_mylesson,  //我的课程
			layout_mycustomer,//我的顾客
			layout_myshop,     //我的店铺
			layout_myteacher,  //我的讲师
			layout_myopenclass
				;
	private LinearLayout layout_collection,layout_setting,layout_aboutus,layout_eyedoc ;    //视光档案
	
	private LinearLayout ll_opt,ll_client,ll_bussiness;
	
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	QXUser user;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_me_layout, container,
				false);
		user = new PreferenceMap(getActivity()).getUser();
		avatar = (RoundImageView) view.findViewById(R.id.iv_infor_avatar);
		name = (TextView) view.findViewById(R.id.tv_infor_name);
		sex = (TextView) view.findViewById(R.id.tv_infor_sex);
		id = (TextView) view.findViewById(R.id.tv_id);
		tv_authority = (TextView) view.findViewById(R.id.tv_authority);
		layout_infor =(RelativeLayout) view.findViewById(R.id.layout_infor);
		layout_suggestion =(RelativeLayout) view.findViewById(R.id.layout_suggestion);
		layout_collection =(LinearLayout) view.findViewById(R.id.layout_collection);
		layout_setting=(LinearLayout) view.findViewById(R.id.layout_setting);
		layout_aboutus=(LinearLayout) view.findViewById(R.id.layout_aboutus);
		
		
		layout_eyedoc = (LinearLayout) view.findViewById(R.id.layout_eyedoc);
		layout_myarticle = (RelativeLayout) view.findViewById(R.id.layout_myarticle);
		layout_myclient = (RelativeLayout) view.findViewById(R.id.layout_myclient);
		layout_mystud = (RelativeLayout) view.findViewById(R.id.layout_mystud);
		layout_mylesson= (RelativeLayout) view.findViewById(R.id.layout_mylesson);
		layout_mycustomer = (RelativeLayout) view.findViewById(R.id.layout_mycustomer);
		layout_myshop = (RelativeLayout) view.findViewById(R.id.layout_myshop);
		layout_myopenclass = (RelativeLayout) view.findViewById(R.id.layout_myopenclass);
		layout_myteacher = (RelativeLayout) view.findViewById(R.id.layout_myteacher);
		
		
		ll_opt = (LinearLayout) view.findViewById(R.id.ll_opt);
		ll_client = (LinearLayout) view.findViewById(R.id.ll_client);
		ll_bussiness = (LinearLayout) view.findViewById(R.id.ll_bussiness);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		QXUser user=new PreferenceMap(App.ctx).getUser();
		name.setText(user.getName());
		sex.setText(user.getSex());
		id.setText(user.getID());
		String url=user.getImage();
		imageLoader
				.displayImage(
						url,
						avatar,
						PhotoUtils
								.getImageOptions(R.drawable.icon_default_avatar_selector));
		initAction();
	}

	private void initAction() {
		if(user.isIsBusiness()){
			tv_authority.setText("商家");
		}else if(user.isIsCustomer()){
			tv_authority.setText("顾客");
		}else if(user.isIsOpto()){
			tv_authority.setText("视光师");
		}else if(user.isIsStudent()){
			tv_authority.setText("学员");
		}else if(user.isIsSuperOpto()){
			tv_authority.setText("大牌专家");
		}else{
			tv_authority.setText("无");
		}
		
		if(user.isIsBusiness()){
			ll_bussiness.setVisibility(View.VISIBLE);
			ll_opt.setVisibility(View.GONE);
			ll_client.setVisibility(View.VISIBLE);
		}
		if(user.isIsCustomer()||user.isIsStudent()){
			ll_client.setVisibility(View.VISIBLE);
			ll_bussiness.setVisibility(View.GONE);
			ll_opt.setVisibility(View.GONE);
		}
		if(user.isIsOpto()||user.isIsSuperOpto()){
			ll_client.setVisibility(View.GONE);
			ll_bussiness.setVisibility(View.GONE);
			ll_opt.setVisibility(View.VISIBLE);
		}
		
		layout_mycustomer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.toast("暂未开通");
			}
		});
		layout_myshop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.toast("暂未开通");
			}
		});
		layout_mylesson.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), MyLessonActivity.class);
				intent.putExtra("tag", "112");
				startActivity(intent);
			}
		});
		layout_myteacher.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.goActivity(getActivity(), MyTeacherActivity.class);
			}
		});
		
		layout_mystud.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.goActivity(getActivity(), MyAllStudentActivity.class);

			}
		});
		layout_myclient.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.goActivity(getActivity(), MyClientActivity.class);

			}
		});
		layout_myopenclass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), MyLessonActivity.class);
				intent.putExtra("tag", "111");
				startActivity(intent);

			}
		});
		layout_myarticle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.goActivity(getActivity(), MyArticleActivity.class);
			}
		});
		layout_eyedoc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.goActivity(getActivity(), MyOptDocActivity.class);
			}
		});
		layout_infor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.goActivity(getActivity(), ChangeInformationActivity.class);
			}
		});
		
		layout_collection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.goActivity(getActivity(), MyCollectionActivity.class);
			}
		});
		layout_setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.goActivity(getActivity(), SettingActivity.class);
			}
		});
		layout_aboutus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.goActivity(getActivity(), AboutusActivity.class);
			}
		});
		layout_suggestion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.goActivity(getActivity(), SuggestionActivity.class);
			}
		});
		
		
	}
}
