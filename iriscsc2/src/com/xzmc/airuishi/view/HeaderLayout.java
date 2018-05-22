package com.xzmc.airuishi.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xzmc.airuishi.R;

public class HeaderLayout extends LinearLayout {

	LayoutInflater mInflater;
	RelativeLayout header;
	TextView titleView;
	LinearLayout leftContainer, rightContainer;
	Button backBtn;

	public HeaderLayout(Context context) {
		super(context);
		init();
	}

	public HeaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mInflater = LayoutInflater.from(getContext());
		header = (RelativeLayout) mInflater.inflate(
				R.layout.base_common_header, null, false);
		titleView = (TextView) header.findViewById(R.id.titleView);
		leftContainer = (LinearLayout) header.findViewById(R.id.leftContainer);
		rightContainer = (LinearLayout) header
				.findViewById(R.id.rightContainer);
		backBtn = (Button) header.findViewById(R.id.backBtn);
		header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		addView(header);
	}

	public void showTitle(int titleId) {
		titleView.setVisibility(View.VISIBLE);
		titleView.setText(titleId + "");
	}

	public void showTitle(String s) {
		titleView.setVisibility(View.VISIBLE);
		titleView.setText(s);
	}

	public void showLeftBackButton(OnClickListener listener) {
		showLeftBackButton("", listener);
	}

	public void showLeftBackButton() {
		showLeftBackButton(null);
	}

	public void showLeftBackButton(String backText, OnClickListener listener) {
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setText(backText);
		if (listener == null) {
			listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					((Activity) getContext()).finish();
				}
			};
		}
		backBtn.setOnClickListener(listener);
	}
	
	public void showLeftTextButton(String rightResId, OnClickListener listener) {
		View imageViewLayout = mInflater.inflate(
				R.layout.base_common_header_right_btn, null, false);
		TextView leftButton = (TextView) imageViewLayout
				.findViewById(R.id.textBtn);
		leftButton.setText(rightResId);
		leftButton.setOnClickListener(listener);
		leftContainer.addView(imageViewLayout);
	}

	public void showRightImageButton(int rightResId, OnClickListener listener) {
		View imageViewLayout = mInflater.inflate(
				R.layout.base_common_header_right_image_btn, null, false);
		ImageButton rightButton = (ImageButton) imageViewLayout
				.findViewById(R.id.imageBtn);
		rightButton.setImageResource(rightResId);
		rightButton.setOnClickListener(listener);
		rightContainer.addView(imageViewLayout);
	}

	public void showLeftImageButton(int rightResId, OnClickListener listener) {
		View imageViewLayout = mInflater.inflate(
				R.layout.base_common_header_right_image_btn, null, false);
		ImageButton leftButton = (ImageButton) imageViewLayout
				.findViewById(R.id.imageBtn);
		leftButton.setImageResource(rightResId);
		leftButton.setOnClickListener(listener);
		leftContainer.removeAllViews();
		leftContainer.addView(imageViewLayout);
	}

	public void showRightTextButton(String rightResId, OnClickListener listener) {
		View imageViewLayout = mInflater.inflate(
				R.layout.base_common_header_right_btn, null, false);
		TextView rightButton = (TextView) imageViewLayout
				.findViewById(R.id.textBtn);
		rightButton.setText(rightResId);
		rightButton.setOnClickListener(listener);
		rightContainer.addView(imageViewLayout);
	}

	public void removeAllRightViews() {
		rightContainer.removeAllViews();
	}
}
