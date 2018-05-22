package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.db.Profess;
import com.xzmc.airuishi.db.ProfessDBHelper;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.service.UserService;
import com.xzmc.airuishi.utils.MD5;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.MyProgressDialog;

public class LoginActivity extends BaseEntryActivity implements OnClickListener {
	private EditText usernameEdit, passwordEdit;
	private Button loginBtn;
	private TextView registerBtn, login_forgetpassword;
	private ImageView image_password, login_remenber_password_image;
	private View login_remenber_password;
	private PreferenceMap accountPre;
	private String name, password;

	private boolean canseePassword = false;
	private boolean remenberAccount = false;
	ProfessDBHelper dbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		accountPre = new PreferenceMap(ctx);
		dbHelper = new ProfessDBHelper(ctx);
		initView();
		initData();
		initAction();
	}

	private void initAction() {
		// 如果用户名改变，清空密码
		usernameEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				passwordEdit.setText(null);
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

	private void initData() {
		if (accountPre.isRemenberAccount()) {
			usernameEdit.setText(accountPre.getAccount());
			passwordEdit.setText(accountPre.getPassword());
			login_remenber_password_image
					.setImageResource(R.drawable.ic_register_agree_true);
			remenberAccount = true;
			Utils.setEditTextLastPosition(usernameEdit);
		}

	}

	private void initView() {
		usernameEdit = (EditText) findViewById(R.id.et_username);
		passwordEdit = (EditText) findViewById(R.id.et_password);
		loginBtn = (Button) findViewById(R.id.btn_login);
		registerBtn = (TextView) findViewById(R.id.btn_register);
		login_forgetpassword = (TextView) findViewById(R.id.login_forgetpassword);
		image_password = (ImageView) findViewById(R.id.image_password);
		login_remenber_password_image = (ImageView) findViewById(R.id.login_remenber_password_image);
		login_remenber_password = findViewById(R.id.login_remenber_password);

		image_password.setOnClickListener(this);
		login_remenber_password.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		login_forgetpassword.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == registerBtn) {
			Utils.goActivity(ctx, RegistActivity.class);
		} else if (v == image_password) {
			if (canseePassword) {
				image_password
						.setImageResource(R.drawable.ic_login_input_password_false);
				passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				canseePassword = false;
			} else {
				image_password
						.setImageResource(R.drawable.ic_login_input_password_true);
				passwordEdit
						.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				canseePassword = true;
			}
			Utils.setEditTextLastPosition(passwordEdit);
		} else if (v == login_remenber_password) {
			if (remenberAccount) {
				login_remenber_password_image
						.setImageResource(R.drawable.ic_register_agree_false);
				remenberAccount = false;
			} else {
				login_remenber_password_image
						.setImageResource(R.drawable.ic_register_agree_true);
				remenberAccount = true;
			}
		} else if (v == login_forgetpassword) {
			Utils.goActivity(LoginActivity.this, ForgetPasswordActivity.class);
		} else {
			login();
		}
	}

	class LoginAsyncTask extends AsyncTask<String, Void, Object> {
		MyProgressDialog dialog = new MyProgressDialog(ctx);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		@Override
		protected Object doInBackground(String... params) {
			String jsonstr = new WebService(C.LOGIN, param).getReturnInfo();
			Map<String, Boolean> result = GetObjectFromService.getLoginResult(jsonstr);
			try {
				List<QXUser> friend = UserService.findFriends(ctx);

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
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Object data) {
			super.onPostExecute(data);
			Map<String, Boolean> result = (Map<String, Boolean>) data;
			if (result.get("result")) {
				// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
				String id=Utils.getID();
				String passwordstr=MD5.getMD5(password.getBytes());
				EMChatManager.getInstance().login(id,passwordstr ,new EMCallBack() {
							@Override
							public void onSuccess() {
								try {
									EMChatManager.getInstance().loadAllConversations();
								} catch (Exception e) {
									e.printStackTrace();
								}
								//更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
								EMChatManager.getInstance().updateCurrentUserNick(accountPre.getUser().getName());
								if (dialog != null && dialog.isShowing()) {
									dialog.dismiss();
								}
								MainActivity.goMainActivity(LoginActivity.this);
								finish();
							}

							@Override
							public void onProgress(int arg0, String arg1) {

							}

							@Override
							public void onError(int arg0, String arg1) {

							}
						});
			} else {
				Utils.toast("登录失败");
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}

	}
	 
	private void login() {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available,
					Toast.LENGTH_SHORT).show();
			return;
		}

		name = usernameEdit.getText().toString().trim();
		password = passwordEdit.getText().toString().trim();

		if (TextUtils.isEmpty(name)) {
			Utils.toast(R.string.username_cannot_null);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			Utils.toast(R.string.password_can_not_null);
			return;
		}
		accountPre.setIsRemenberAccount(remenberAccount);
		if (remenberAccount) {
			accountPre.setAccount(name);
			accountPre.setPassword(password);
		} else {
			accountPre.setAccount("");
			accountPre.setPassword("");
		}
		param.clear();
		param.put("phone", name);
		param.put("password", MD5.getMD5(password.getBytes()));
		new LoginAsyncTask().execute();
	}
}
