package com.xzmc.airuishi.fragment;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity_v2.LessonDetailActivity;
import com.xzmc.airuishi.bean.Lesson;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.MyProgressDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LessonDetailFragment extends Fragment {
	private WebView webView;
	Lesson lesson;
	MyProgressDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_lesson_detail,
				container, false);
		dialog = new MyProgressDialog(getActivity());
		webView = (WebView) view.findViewById(R.id.webview);
		lesson = LessonDetailActivity.lesson;
		setWebSeeting(webView);
		webView.loadUrl(lesson.getLink());
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	private void setWebSeeting(final WebView webView) {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		WebSettings webSettings = webView.getSettings();
		webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		webSettings.setLoadWithOverviewMode(true);
		webView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Utils.toast("加载失败，请重试");
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					dialog.dismiss();
				} else {
					dialog.show();
				}
			}
		});
	}
}
