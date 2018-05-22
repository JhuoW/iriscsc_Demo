package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.adapter.CommentListAdapter;
import com.xzmc.airuishi.adapter.CommentListAdapter.ViewHolder;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Comment;
import com.xzmc.airuishi.bean.PostModel;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.db.CommentDBHelper;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.airuishi.view.MyProgressDialog;

public class PostDetaileActivity extends BaseActivity implements
		OnItemClickListener {
	public static String newsbaseurl = "http://app.iriscsc.com:8080/web-news-visit-clean.aspx?nid=";
	public static String advbaseurl = "http://app.iriscsc.com:8080/web-advert-visit-clean.aspx?nid=";

	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	private View contentView;
	private WebView webView;
	private ImageView ivComment, blog_iscollection;
	private PostModel postModel;
	private TextView tvDetailAuthor;
	private ScrollView scrollView;
	private HeaderLayout headerLayout;
	private ListView comment_list;
	private CommentListAdapter adapter;
	private List<Comment> commentdata = new ArrayList<Comment>();
	private List<Comment> mumcommentdata = new ArrayList<Comment>();
	private Map<String, String> param = new HashMap<String, String>();
	private EditText blog_comment;
	String postId;
	MyProgressDialog dialog;
	PostModel curPost;
	boolean iscollection = false;
	int curCommentPage = 1;
	int curCommentCount = 40;

	QXUser user;

	CommentDBHelper dbHelper;
	
	// 回复评论
	private EditText replyEdit;
	private InputMethodManager manager;
	private PopupWindow editWindow;// 回复window
	private Button sendBtn;// 发送按钮
	private RelativeLayout root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lay_post_detail_act);
		dbHelper = new CommentDBHelper(ctx);
		// String appID = "wx967daebe835fbeac";
		// String appSecret = "5fa9e68ca3970e87a1f83e563c8dcbce";
		// // 添加微信平台
		// UMWXHandler wxHandler = new UMWXHandler(this,appID,appSecret);
		// wxHandler.addToSocialSDK();
		// // 添加微信朋友圈
		// UMWXHandler wxCircleHandler = new UMWXHandler(this,appID,appSecret);
		// wxCircleHandler.setToCircle(true);
		// wxCircleHandler.addToSocialSDK();
		user = new PreferenceMap(ctx).getUser();
		initView();
		initReply();
		Intent intent = getIntent();
		if (setData(intent)) {
			param.clear();
			param.put("newsID", curPost.getId());
			param.put("page", curCommentPage + "");
			param.put("count", curCommentCount + "");
			initData();
			configPlatforms();
			setShareContent();

		} else {
			startActivity(new Intent(this, MainActivity.class));
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			finish();
		}
	}

	/**
	 * 配置分享平台参数</br>
	 */
	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		// 添加QQ、QZone平台
		addQQQZonePlatform();
		// 添加微信、微信朋友圈平台
		addWXPlatform();
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wxf1970aaa5c3aafe9";
		String appSecret = "b6449a8860f27d38f5f69f8c73270455";

		String id = curPost.getId();
		String murl;
		if (curPost.getChannel_id() != null
				&& curPost.getChannel_id().equals("-1")) {
			murl = advbaseurl + id;
		} else {
			murl = newsbaseurl + id;
		}

		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(PostDetaileActivity.this,
				appId, appSecret);
		wxHandler.setTitle(curPost.getTitle());
		wxHandler.setTargetUrl(murl);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(PostDetaileActivity.this,
				appId, appSecret);
		wxHandler.setTitle(curPost.getTitle());
		wxHandler.setTargetUrl(murl);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	private void addQQQZonePlatform() {
		String appId = "1104814455";
		String appKey = "p4s4qBscWBDOfiDX";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
				PostDetaileActivity.this, appId, appKey);

		String id = curPost.getId();
		String murl;
		if (curPost.getChannel_id() != null
				&& curPost.getChannel_id().equals("-1")) {
			murl = advbaseurl + id;
		} else {
			murl = newsbaseurl + id;
		}
		qqSsoHandler.setTargetUrl(murl);
		qqSsoHandler.addToSocialSDK();
		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				PostDetaileActivity.this, appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	private void setShareContent() {
		// 配置SSO
		String id = curPost.getId();
		String murl;
		if (curPost.getChannel_id() != null
				&& curPost.getChannel_id().equals("-1")) {
			murl = advbaseurl + id;
		} else {
			murl = newsbaseurl + id;
		}
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				PostDetaileActivity.this, "1104814455", "p4s4qBscWBDOfiDX");
		qZoneSsoHandler.addToSocialSDK();
		mController.setShareContent(curPost.getTitle() + "：" + "\n" + murl);
		UMImage urlImage = new UMImage(PostDetaileActivity.this,
				curPost.getImageurl());
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(curPost.getTitle() + "：" + "\n" + murl);
		weixinContent.setTitle(curPost.getTitle());
		weixinContent.setTargetUrl(murl);
		weixinContent.setShareImage(urlImage);
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(curPost.getTitle() + "：" + "\n" + murl);
		circleMedia.setTitle(curPost.getTitle());
		circleMedia.setShareMedia(urlImage);
		// circleMedia.setShareMedia(uMusic);
		// circleMedia.setShareMedia(video);
		circleMedia.setTargetUrl(murl);
		mController.setShareMedia(circleMedia);

		// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(curPost.getTitle() + "：" + "\n" + murl);
		qzone.setTargetUrl(murl);
		qzone.setTitle(curPost.getTitle());
		qzone.setShareMedia(urlImage);
		// qzone.setShareMedia(uMusic);
		mController.setShareMedia(qzone);

		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(curPost.getTitle() + "：" + "\n" + murl);
		qqShareContent.setTitle(curPost.getTitle());
		qqShareContent.setShareMedia(urlImage);
		qqShareContent.setTargetUrl(murl);
		mController.setShareMedia(qqShareContent);

		SinaShareContent sinaContent = new SinaShareContent();
		sinaContent.setShareContent(curPost.getTitle() + "：" + "\n" + murl);
		sinaContent.setShareImage(urlImage);
		mController.setShareMedia(sinaContent);

	}

	private void initData() {
		new SimpleNetTask(ctx, false) {
			String type;
			@Override
			protected void onSucceed() {
				if (curPost.getChannel_id() != null
						&& curPost.getChannel_id().equals("-1")) {
					type = "adv";
					adapter = new CommentListAdapter(mumcommentdata, ctx,curPost.getId(),root,type);
				}else {
					type = "news";
					adapter = new CommentListAdapter(mumcommentdata, ctx,curPost.getId(),root,type);
				}
				comment_list.setAdapter(adapter);
				//setListViewHeightBasedOnChildren(comment_list);
				Utils.setListViewHeightBasedOnChildren(comment_list);
			}

			@Override
			protected void doInBack() throws Exception {
				String jsonstr = "";
				if (curPost.getChannel_id() != null
						&& curPost.getChannel_id().equals("-1")) {
					jsonstr = new WebService(C.GETADVECOMMENT, param)
							.getReturnInfo();
				} else {
					jsonstr = new WebService(C.GETNEWSCOMMENT, param)
							.getReturnInfo();
				}
				dbHelper.deleteAllDataFromTable(com.xzmc.airuishi.db.Comment.TABLENAME);
				commentdata = GetObjectFromService.getCommentList(jsonstr);
				System.out.println("commentdata长度："+commentdata.size());
				for(int i = 0;i<commentdata.size();i++){
					Comment c = commentdata.get(i);
					dbHelper.insertComment(c);
				}
				mumcommentdata = dbHelper.getMumCommentList();
//				String acceptid = mumcommentdata.get(0).getAcceptId();
//				System.out.println("acceptid:"+acceptid);
			}
		}.execute();
	}

	@SuppressLint("InflateParams")
	public void initView() {
		dialog = new MyProgressDialog(ctx);
		dialog.show();
		root = (RelativeLayout) findViewById(R.id.rl_root);
		blog_iscollection = (ImageView) findViewById(R.id.blog_iscollection);
		blog_comment = (EditText) findViewById(R.id.blog_comment);
		comment_list = (ListView) findViewById(R.id.comment_list);
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		webView = (WebView) findViewById(R.id.webView);
		setWebSeeting(webView);
		tvDetailAuthor = (TextView) findViewById(R.id.tvDetailAuthor);
		blog_comment.requestFocus();
		headerLayout.showLeftBackButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PostDetaileActivity.this.finish();
			}
		});
		headerLayout.showRightTextButton(getString(R.string.share),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mController.getConfig().setPlatforms(
								SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
								SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
								SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);
						mController.openShare(PostDetaileActivity.this, false);
					}
				});

		blog_comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PostDetaileActivity.this,
						AddCommmentActivity.class);
				intent.putExtra("postId", curPost.getId());
				if (curPost.getChannel_id() != null
						&& curPost.getChannel_id().equals("-1")) {
					intent.putExtra("cater", AddCommmentActivity.ADVER);
				} else {
					intent.putExtra("cater", AddCommmentActivity.NEWS);
				}
				PostDetaileActivity.this.startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		blog_iscollection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				param.clear();
				String id = Utils.getID();
				param.put("userID", id);
				param.put("newsID", curPost.getId());
				new SimpleNetTask(ctx, true) {
					boolean flag;
					String method = "";

					@Override
					protected void onSucceed() {
						if (flag) {
							if (iscollection) {
								blog_iscollection
										.setImageResource(R.drawable.icon_collection);
								iscollection = false;
							} else {
								final Dialog dialog = new Dialog(ctx,
										R.style.like_toast_dialog_style);
								View view = LayoutInflater.from(ctx).inflate(
										R.layout.record_layout, null);
								dialog.setContentView(view, new LayoutParams(
										ViewGroup.LayoutParams.WRAP_CONTENT,
										ViewGroup.LayoutParams.WRAP_CONTENT));
								WindowManager.LayoutParams lp = dialog
										.getWindow().getAttributes();
								lp.alpha = 0.9f;
								lp.gravity = Gravity.CENTER;
								dialog.show();

								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										Message msg = mHandler.obtainMessage();
										msg.obj = dialog;
										mHandler.sendMessage(msg);
									}
								}).start();

								blog_iscollection
										.setImageResource(R.drawable.icon_collection_like);
								iscollection = true;
							}

						}
					}

					@Override
					protected void doInBack() throws Exception {
						if (iscollection) {
							method = C.DELETECOLLECTION;
						} else {
							method = C.ADDCOLLECTION;
						}
						String jsonstr = new WebService(method, param)
								.getReturnInfo();
						flag = GetObjectFromService.getSimplyResult(jsonstr);
					}
				}.execute();
			}
		});

		WebSettings settings = webView.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setLoadWithOverviewMode(true);
		comment_list.setOnItemClickListener(this);
	}

	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		String id = curPost.getId();
		String murl;
		if (curPost.getChannel_id() != null
				&& curPost.getChannel_id().equals("-1")) {
			murl = advbaseurl + id;
		} else {
			murl = newsbaseurl + id;
		}
		intent.putExtra(Intent.EXTRA_TEXT, curPost.getTitle() + "：" + "\n"
				+ murl);
		startActivity(Intent.createChooser(intent, "爱睿视新闻分享"));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
//		if (resultCode != RESULT_OK) {
//			return;
//		}
//		Comment comment = new Comment();
//		QXUser user = new PreferenceMap(ctx).getUser();
//		comment.setContent(intent.getExtras().getString("commentcontent"));
//		comment.setPicture(user.getImage());
//		comment.setNickname(user.getName());
//		comment.setTime("刚刚");
//		mumcommentdata.add(0, comment);
//		adapter.notifyDataSetChanged();

		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, intent);
		}
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			((Dialog) msg.obj).cancel();
			;
		}
	};

	// ScrollView 嵌套ListView 需要先计算ListView的高
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int singleHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
			singleHeight = listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount()));
		params.height += singleHeight;
		listView.setLayoutParams(params);
	}

	public boolean setData(Intent intent) {
		curPost = (PostModel) intent.getSerializableExtra("post");
		String id = curPost.getId();
		if (curPost.getChannel_id() != null
				&& curPost.getChannel_id().equals("-1")) {
			webView.loadUrl(advbaseurl + id);
		} else {
			webView.loadUrl(newsbaseurl + id);
		}

		headerLayout.showTitle(curPost.getTitle());
		tvDetailAuthor.setText("本文由 " + curPost.getSource() + " 发布");
		if (curPost.isCollection().equals("true")) {
			blog_iscollection.setImageResource(R.drawable.icon_collection_like);
			iscollection = true;
		} else {
			blog_iscollection.setImageResource(R.drawable.icon_collection);
			iscollection = false;
		}
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setData(intent);
	}

	public void setWebSeeting(final WebView webView) {
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				Utils.toast("错误，请重试");
				comment_list.setAdapter(null);
			}
		});
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			scrollView.fullScroll(ScrollView.FOCUS_DOWN);
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearWebView(webView);
		mController.getConfig().cleanListeners();
	}

	public void clearWebView(WebView webView) {
		if (webView != null) {
			webView.stopLoading();
			webView.removeAllViews();
			webView.clearView();
			webView.destroy();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		System.out.println("点击");
		//showDiscuss(position);
		//replyEdit.setHint("回复  " + mumcommentdata.get(position).getNickname() + ":");
	}

	/**
	 * 显示回复评论框
	 * 
	 * @param reply
	 */
	private void showDiscuss(final int position) {
		replyEdit.setFocusable(true);
		replyEdit.requestFocus();
		// 设置焦点，不然无法弹出输入法
		editWindow.setFocusable(true);

		// 以下两句不能颠倒
		editWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		editWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		editWindow.showAtLocation(root, Gravity.BOTTOM, 0, 0);

		// 显示键盘
		manager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		editWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				manager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
				replyEdit.setText("");
			}
		});
		
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(replyEdit.getText().toString())) {
					Utils.toast("回复内容不能为空");
					return;
				}
				final String content = replyEdit.getText().toString();
				new SimpleNetTask(ctx) {
					boolean flag;

					@Override
					protected void onSucceed() {
						// TODO Auto-generated method stub
						if (flag) {
							Comment c = new Comment();
							c.setUserId(Utils.getID());
							c.setNickname(Utils.getName());
							c.setTonickname(mumcommentdata.get(position).getNickname());
							c.setContent(content);
							editWindow.dismiss();
						} else {
							Utils.toast("评论失败，请重试");
						}
					}

					@Override
					protected void doInBack() throws Exception {
						// TODO Auto-generated method stub
						param.clear();
						param.put("newsID", curPost.getId());
						param.put("userID", Utils.getID());
						param.put("touserID", mumcommentdata.get(position).getUserId());
						System.out.println("touserID:"+mumcommentdata.get(position).getUserId());
						param.put("commentID", mumcommentdata.get(position)
								.getId());
						param.put("content", content);
						String jsonStr = new WebService(C.REPLYCOMMENT,
								param).getReturnInfo();
						flag = GetObjectFromService
								.getSimplyResult(jsonStr);
					}
				}.execute();
			}
		});
	}

	private void initReply() {
		View editView = getLayoutInflater().inflate(
				R.layout.friend__replay_input, null);
		editWindow = new PopupWindow(editView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		editWindow.setBackgroundDrawable(getResources().getDrawable(
				R.color.white));
		editWindow.setOutsideTouchable(true);
		replyEdit = (EditText) editView.findViewById(R.id.reply);
		sendBtn = (Button) editView.findViewById(R.id.send_msg);
	}

	// 更新可见的Listview Item
	public void updateView(final int itemIndex, final String commentid) {

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dbHelper.deleteAllDataFromTable(com.xzmc.airuishi.db.Comment.TABLENAME);
		initData();
	}
}
