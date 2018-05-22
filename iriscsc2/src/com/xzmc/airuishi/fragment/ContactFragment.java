package com.xzmc.airuishi.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.ChatActivity;
import com.xzmc.airuishi.activity.ClubActivity;
import com.xzmc.airuishi.activity.ContactActivity;
import com.xzmc.airuishi.activity.NewFriendActivity;
import com.xzmc.airuishi.activity.UserDetailActivity;
import com.xzmc.airuishi.adapter.ProfessAdapter;
import com.xzmc.airuishi.adapter.UserFriendAdapter;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.bean.SortUser;
import com.xzmc.airuishi.db.FriendsTable;
import com.xzmc.airuishi.db.Profess;
import com.xzmc.airuishi.db.ProfessDBHelper;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.service.UserService;
import com.xzmc.airuishi.utils.CharacterParser;
import com.xzmc.airuishi.utils.NetAsyncTask;
import com.xzmc.airuishi.utils.PinyinComparator;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.EnLetterView;
import com.xzmc.airuishi.view.MyListView;
import com.xzmc.qixinplus.ui.xlist.XListView;

public class ContactFragment extends BaseFragment implements
		OnItemClickListener, OnItemLongClickListener, OnClickListener,
		XListView.IXListViewListener {
	private TextView dialog;
	private XListView friendsList;
	private EnLetterView rightLetter;
	private UserFriendAdapter userAdapter;
	private List<SortUser> friends = new ArrayList<SortUser>();
	private List<SortUser> sourcedatas = new ArrayList<SortUser>();
	private List<QXUser> oldfriends;
	public static ImageView msgTipsView;
	private LinearLayout newFriendLayout, contactLayout, layout_club;
	private static CharacterParser characterParser;
	private static PinyinComparator pinyinComparator;
	private FriendsTable friendstable;
	private EditText searchNameEdit;
	MyListView list_friends;
	ProfessDBHelper professDBHelper;
	ProfessAdapter professAdapter;
	List<QXUser> professorList = new ArrayList<QXUser>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false))
			return;
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		professDBHelper = new ProfessDBHelper(getActivity());
		friendstable = FriendsTable.getInstance();
		initListView();
		initRightLetterView();
		initAction();
		onRefresh();
	}

	private void initAction() {
		
		list_friends.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(
//						"userId", professorList.get(position).getID()));
				Intent intent = new Intent(getActivity(), UserDetailActivity.class);
				intent.putExtra("userId", professorList.get(position).getID());
				startActivity(intent);
			}
		});
		
		searchNameEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void filterData(String filterStr) {
		List<SortUser> filterDateList = new ArrayList<SortUser>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = sourcedatas;
		} else {
			filterDateList.clear();
			for (SortUser sortModel : sourcedatas) {
				String name = sortModel.getInnerUser().getName();
				if (name.toUpperCase().indexOf(
						filterStr.toString().toUpperCase()) != -1
						|| characterParser.getSelling(name).toUpperCase()
								.startsWith(filterStr.toString().toUpperCase())) {
					filterDateList.add(sortModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		friends.clear();
		friends.addAll(filterDateList);
		userAdapter.notifyDataSetChanged();
	}

	private List<SortUser> convertAVUser(List<QXUser> datas) {
		List<SortUser> sortUsers = new ArrayList<SortUser>();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			QXUser avUser = datas.get(i);
			SortUser sortUser = new SortUser();
			sortUser.setInnerUser(avUser);
			String username = avUser.getName();
			if (username != null) {
				String pinyin = characterParser.getSelling(username);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				if (sortString.matches("[A-Z]")) {
					sortUser.setSortLetters(sortString.toUpperCase());
				} else {
					sortUser.setSortLetters("#");
				}
			} else {
				sortUser.setSortLetters("#");
			}
			sortUsers.add(sortUser);
		}
		Collections.sort(sortUsers, pinyinComparator);
		return sortUsers;
	}

	private void initListView() {
		friendsList = (XListView) getView().findViewById(R.id.list_friends);
		friendsList.setPullRefreshEnable(true);
		friendsList.setPullLoadEnable(false);
		friendsList.setXListViewListener(this);
		LayoutInflater mInflater = LayoutInflater.from(ctx);
		LinearLayout headView = (LinearLayout) mInflater.inflate(
				R.layout.contact_include_new_friend, null);
		msgTipsView = (ImageView) headView.findViewById(R.id.iv_msg_tips);
		newFriendLayout = (LinearLayout) headView.findViewById(R.id.layout_new);
		contactLayout = (LinearLayout) headView
				.findViewById(R.id.layout_contact);
		layout_club = (LinearLayout) headView.findViewById(R.id.layout_club);
		searchNameEdit = (EditText) headView.findViewById(R.id.searchNameEdit);
		list_friends = (MyListView) headView.findViewById(R.id.list_friends);
		professAdapter = new ProfessAdapter(getActivity(), professorList);
		list_friends.setAdapter(professAdapter);
		newFriendLayout.setOnClickListener(this);
		contactLayout.setOnClickListener(this);
		layout_club.setOnClickListener(this);
		friendsList.addHeaderView(headView);
		//oldfriends = friendstable.selectFriends();
		oldfriends = professDBHelper.getCommonUser();
		List<SortUser> temp=convertAVUser(oldfriends);
		friends.clear();
		friends.addAll(temp);
		sourcedatas.clear();
		sourcedatas.addAll(temp);
		userAdapter = new UserFriendAdapter(getActivity(), friends);
		friendsList.setAdapter(userAdapter);
		friendsList.setOnItemClickListener(this);
		friendsList.setOnItemLongClickListener(this);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void initRightLetterView() {
		rightLetter = (EnLetterView) getView().findViewById(R.id.right_letter);
		dialog = (TextView) getView().findViewById(R.id.dialog);
		rightLetter.setTextView(dialog);
		rightLetter
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_new:
			Utils.goActivity(getActivity(), NewFriendActivity.class);
			break;
		case R.id.layout_contact:
			Utils.goActivity(getActivity(), ContactActivity.class);
			break;
		case R.id.layout_club:
			Utils.goActivity(getActivity(), ClubActivity.class);
			break;
		}

	}

	@Override
	public void onRefresh() {
		professDBHelper.deleteAllDataFromTable(Profess.TABLENAME);
		new NetAsyncTask(ctx, false) {
			boolean haveAddRequest;
			List<QXUser> friend;
			List<QXUser> professor = new ArrayList<QXUser>();
			@Override
			protected void doInBack() throws Exception {
				friend = UserService.findFriends(getActivity());
				if (friend.size() != 0 && friend != null) {
//					friendstable.deleteAllFriend(oldfriends);
//					friendstable.insertFriends(friend);
					oldfriends = professDBHelper.getCommonUser();
					professor = professDBHelper.getProfessUser();
				}
				if (professDBHelper.getCommonUser().size() == 0) {
					oldfriends.clear();
				}

				Map<String, User> usermap = App.getInstance().getContactList();

				for (int i = 0; i < friend.size(); i++) {
					QXUser qxUser = friend.get(i);
					User user = new User();
					user.setAvatar(qxUser.getImage());
					user.setNick(qxUser.getName());
					user.setUsername(qxUser.getID());
					usermap.put(qxUser.getID(), user);
				}
				App.getInstance().setContactList(usermap);
				UserDao dao = new UserDao(ctx);
				List<User> users = new ArrayList<User>(usermap.values());
				dao.saveContactList(users);
			}

			@Override
			protected void onPost(Exception e) {
				friendsList.stopRefresh();
				if (e != null) {
					Utils.toast(ctx, e.getMessage());
				} else {
					List<SortUser> sortUsers = convertAVUser(oldfriends);
					friends.clear();
					friends.addAll(sortUsers);
					sourcedatas.clear();
					sourcedatas.addAll(sortUsers);
					userAdapter.notifyDataSetChanged();
					professorList.clear();
					professorList.addAll(professor);
					professAdapter.notifyDataSetChanged();
					
				}
			}
		}.execute();
	}

	public static void showNewFriendTips() {
		msgTipsView.setVisibility(View.VISIBLE);
	}

	private class LetterListViewListener implements
			EnLetterView.OnTouchingLetterChangedListener {
		@Override
		public void onTouchingLetterChanged(String s) {
			int position = userAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				friendsList.setSelection(position);
			}
		}
	}

	private boolean hidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			onRefresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			//onRefresh();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
	final SortUser user = (SortUser) arg0.getAdapter().getItem(position);
//		startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(
//				"userId", user.getInnerUser().getID()));
	Intent intent = new Intent(getActivity(), UserDetailActivity.class);
	intent.putExtra("userId", user.getInnerUser().getID());
	startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		final SortUser user = (SortUser) arg0.getAdapter().getItem(position);

		new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
				.setTitleText("解除与" + user.getInnerUser().getName() + "的好友关系？")
				.setConfirmText("确定解除")
				.setCancelText("取消解除")
				.showCancelButton(true)
				.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.dismiss();
							}
						})
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(final SweetAlertDialog sDialog) {
								new NetAsyncTask(getActivity(), false) {
									boolean result = false;

									@Override
									protected void onPost(Exception e) {
										if (result) {
											sDialog.setTitleText("完成")
													.setContentText("已解除好友关系")
													.setConfirmText("确定")
													.showCancelButton(false)
													.setConfirmClickListener(
															null)
													.changeAlertType(
															SweetAlertDialog.SUCCESS_TYPE);
											friendstable.deleteFriend(user
													.getInnerUser().getID());
											// 删除会话栏数据
											EMChatManager.getInstance()
													.deleteConversation(
															user.getInnerUser()
																	.getID(),
															false, true);
											onRefresh();

										} else {
											sDialog.setContentText("解除好友关系失败")
													.setConfirmText("确定")
													.showCancelButton(false)
													.changeAlertType(
															SweetAlertDialog.ERROR_TYPE);
										}
									}

									@Override
									protected void doInBack() throws Exception {
										Map<String, String> param = new HashMap<String, String>();
										param.put("fromUserID", Utils.getID());
										param.put("tofromUserID", user
												.getInnerUser().getID());
										String jsonstr = new WebService(
												C.DELETEFRIEND, param)
												.getReturnInfo();
										result = GetObjectFromService
												.getSimplyResult(jsonstr);
									}
								}.execute();

							}
						}).show();
		return true;
	}

	@Override
	public void onLoadMore() {
	}
}
