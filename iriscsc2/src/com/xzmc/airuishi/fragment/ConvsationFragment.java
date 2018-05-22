package com.xzmc.airuishi.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.adapter.ChatAllHistoryAdapter;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.activity.ChatActivity;
import com.xzmc.airuishi.activity.MainActivity;
import com.xzmc.airuishi.activity.QuestionActivity;
import com.xzmc.airuishi.activity.QusetionaireActivity;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.PhotoUtils;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;

/**
 * @author xiaobian
 */
public class ConvsationFragment extends Fragment {
	private ListView listView;
	private ChatAllHistoryAdapter adapter;
	public RelativeLayout errorItem;
	public TextView errorText;
	private boolean hidden;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	
	private RelativeLayout rl_professor;
	private RelativeLayout rl_questionaire;
	private ImageView professorHeader;
	private TextView professorName;
	private TextView professorInfo;
	private TextView professorOnlineTime;
	public static ImageLoader imageLoader = ImageLoader.getInstance();

	private String professorId;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_conversation, container,
				false);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)){
			return;}
		initView();
		initData();
		initAction();
		onRefresh();
		registerForContextMenu(listView);
	}
	private void initView() {
		listView = (ListView) getView().findViewById(R.id.convList);
		errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		LayoutInflater mInflater = LayoutInflater.from(getActivity());
		LinearLayout headView = (LinearLayout) mInflater.inflate(
				R.layout.header_view, null);
		rl_professor = (RelativeLayout) headView.findViewById(R.id.rl_professor);
		rl_questionaire = (RelativeLayout) headView.findViewById(R.id.rl_questionaire);
		professorHeader = (ImageView) headView.findViewById(R.id.img_contact_avatar);
		professorName = (TextView) headView.findViewById(R.id.tv_contact_name);
		professorInfo = (TextView) headView.findViewById(R.id.tv_contact_phone);
		professorOnlineTime = (TextView) headView.findViewById(R.id.tv_onlinetime);
		listView.addHeaderView(headView);
	}
	
	private void getRecommendProfessor(){
		new SimpleNetTask(getActivity(),false) {
			String jsonstr;
			@Override
			protected void onSucceed() {
				// TODO Auto-generated method stub
				rl_professor.setVisibility(View.VISIBLE);
				try {
					JSONObject json = new JSONObject(jsonstr);
					if(json.get("ret").equals("success")){
						rl_professor.setVisibility(View.VISIBLE);
						String professorName = json.getString("nickName");
						String professorInfo = json.getString("professorInfo");
						String professorOnlineTime = json.getString("onlineTime");
						String imgUrl = json.getString("imgUrl");
						professorId = json.getString("ID");
						ConvsationFragment.this.professorName.setText(professorName);
						if(TextUtils.isEmpty(professorInfo)){
							ConvsationFragment.this.professorInfo.setText("暂无");
						}else{
							ConvsationFragment.this.professorInfo.setText(professorInfo);
						}
						
						if(TextUtils.isEmpty(professorOnlineTime)){
							ConvsationFragment.this.professorOnlineTime.setText("暂无");
						}else{
							ConvsationFragment.this.professorOnlineTime.setText(professorOnlineTime);
						}
						
						imageLoader.displayImage(imgUrl, professorHeader,
								PhotoUtils.getImageOptions(R.drawable.icon_default_avatar));
						
					}else{
						rl_professor.setVisibility(View.GONE);
						//Utils.toast("获取推荐专家失败");
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			
			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				Map<String, String> param = new HashMap<String, String>();
				param.clear();
				param.put("userId", Utils.getID());
				jsonstr = new WebService(C.GETEXPERTID, param).getReturnInfo();
			}
		}.execute();
	}
	
	
	private void initAction() {
		
		rl_questionaire.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), QusetionaireActivity.class);
				startActivity(intent);
			}
		});
		
		rl_professor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(getActivity(), ChatActivity.class);
				 intent.putExtra("userId", professorId);
				 startActivity(intent);
			}
		});
		final String st2 = getResources().getString(R.string.Cant_chat_with_yourself);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = adapter.getItem(position-1);
				String username = conversation.getUserName();
				if (username.equals(App.getInstance().getUserName()))
					Toast.makeText(getActivity(), st2, 0).show();
				else {
				    // 进入聊天页面
				    Intent intent = new Intent(getActivity(), ChatActivity.class);
				    if(conversation.isGroup()){
				        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", username);
				    }else{
				        // it is single chat
                        intent.putExtra("userId", username);
				    }
				    startActivity(intent);
				}
			}
		});

	}

	private void initData() {
		conversationList.addAll(loadConversationsWithRecentChat());
		
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
		// 设置adapter
		listView.setAdapter(adapter);
	}

	private void onRefresh() {
	}
	/**
	 * 获取所有会话
	 * @param context
	 * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
		 * 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 
		 * 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}
	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

				if (con1.first == con2.first) {
					return 0;
				} else if (con2.first > con1.first) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}
	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if(adapter != null)
		    adapter.notifyDataSetChanged();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity) getActivity()).updateUnreadLabel();
		if (!hidden && ! ((MainActivity)getActivity()).isConflict) {
			refresh();
		}
		getRecommendProfessor();
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.delete_message, menu); 
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean handled = false;
		boolean deleteMessage = false;
		if (item.getItemId() == R.id.delete_message) {
			deleteMessage = true;
			handled = true;
		} else if (item.getItemId() == R.id.delete_conversation) {
			deleteMessage = false;
			handled = true;
		}
		EMConversation tobeDeleteCons = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position-1);
		// 删除此会话
		EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), deleteMessage);
		InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
		inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
		adapter.remove(tobeDeleteCons);
		adapter.notifyDataSetChanged();
		((MainActivity) getActivity()).updateUnreadLabel();
		return handled ? true : super.onContextItemSelected(item);
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        if(((MainActivity)getActivity()).isConflict){
        	outState.putBoolean("isConflict", true);
        }
        else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
        	outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }
}
