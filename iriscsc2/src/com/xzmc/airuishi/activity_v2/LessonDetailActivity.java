package com.xzmc.airuishi.activity_v2;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.BaseActivity;
import com.xzmc.airuishi.bean.Lesson;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.fragment.LessonDetailFragment;
import com.xzmc.airuishi.fragment.StudentFragment;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.view.HeaderLayout;

public class LessonDetailActivity extends BaseActivity {

	private ArrayList<Fragment> fragments;

	private ViewPager viewPager;

	private TextView lessonDetail;

	private TextView student;

	private int line_width;

	private View line;

	private boolean isBack;

	public static Lesson lesson;
	QXUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lesson_detail);
		user = new PreferenceMap(ctx).getUser();
		Intent intent = getIntent();
		lesson = (Lesson) intent.getSerializableExtra("post");
		initView();
	}

	private void initView() {
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showTitle("详情");
		if (user.isIsOpto() || user.isIsSuperOpto()) {
			headerLayout.showRightTextButton("推送", new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(LessonDetailActivity.this,
							PushActivity2.class);
					intent.putExtra("post", lesson);
					startActivity(intent);
				}
			});
		}
		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LessonDetailActivity.this.finish();
			}
		});
		lessonDetail = (TextView) findViewById(R.id.lessondetail);
		student = (TextView) findViewById(R.id.student);
		line = findViewById(R.id.line);

		ViewPropertyAnimator.animate(lessonDetail).scaleX(1.2f).setDuration(0);
		ViewPropertyAnimator.animate(lessonDetail).scaleY(1.2f).setDuration(0);
		fragments = new ArrayList<Fragment>();
		fragments.add(new LessonDetailFragment());
		fragments.add(new StudentFragment());
		line_width = getWindowManager().getDefaultDisplay().getWidth()
				/ fragments.size();
		line.getLayoutParams().width = line_width;
		line.requestLayout();

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return fragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragments.get(arg0);
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				changeState(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				float tagerX = arg0 * line_width + arg2 / fragments.size();
				ViewPropertyAnimator.animate(line).translationX(tagerX)
						.setDuration(0);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		student.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(1);
			}
		});

		lessonDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(0);
			}
		});
	}

	private void changeState(int arg0) {
		if (arg0 == 0) {
			lessonDetail.setTextColor(getResources().getColor(R.color.theme));
			student.setTextColor(getResources().getColor(R.color.black));
			ViewPropertyAnimator.animate(lessonDetail).scaleX(1.2f)
					.setDuration(200);
			ViewPropertyAnimator.animate(lessonDetail).scaleY(1.2f)
					.setDuration(200);
			ViewPropertyAnimator.animate(student).scaleX(1.0f).setDuration(200);
			ViewPropertyAnimator.animate(student).scaleY(1.0f).setDuration(200);

		} else {
			student.setTextColor(getResources().getColor(R.color.theme));
			lessonDetail.setTextColor(getResources().getColor(R.color.black));
			ViewPropertyAnimator.animate(lessonDetail).scaleX(1.0f)
					.setDuration(200);
			ViewPropertyAnimator.animate(lessonDetail).scaleY(1.0f)
					.setDuration(200);
			ViewPropertyAnimator.animate(student).scaleX(1.2f).setDuration(200);
			ViewPropertyAnimator.animate(student).scaleY(1.2f).setDuration(200);
		}
	}

}
