package com.xzmc.airuishi.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.json.JSONObject;
import org.kobjects.base64.Base64;

import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.UserUtils;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.service.PreferenceMap;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;
import com.xzmc.airuishi.view.MyProgressDialog;

public class ChangeInformationActivity extends BaseActivity implements
		OnClickListener {
	private static final int CAMERA_WITH_DATA = 3023;
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int CAMERA_CROP_DATA = 3022;
	private static final int SELECT_ADDRESS = 3024;
	private File PHOTO_DIR = null;
	private File mCurrentPhotoFile;
	private String mFileName;
	private View mAvatarView = null;
	public LayoutInflater mInflater;
	private Button albumButton, camButton, cancelButton;
	private ImageView avatar;
	private Button savechange;
	private EditText name;
	private TextView address;
	private CheckBox male, female;
	private View layout_name, layout_sex, layout_address;
	private String name_str;
	private String address_str;
	private String sex_str;
	private QXUser curuser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.getInstance().addActivity(this);
		setContentView(R.layout.activity_changeinformation_layout);
		mInflater = LayoutInflater.from(this);
		curuser = new PreferenceMap(ctx).getUser();
		initView();
		initData();
		initAction();
	}

	private void initData() {
		headerLayout.showTitle("个人信息修改");
		name.setText(curuser.getName());
		Utils.setEditTextLastPosition(name);
		if (curuser.getSex().equals("男")) {
			male.setChecked(true);
			female.setChecked(false);
		} else {
			female.setChecked(true);
			male.setChecked(false);
		}
		address.setText(curuser.getAddress());
		String photo_dir = AbFileUtil.getImageDownloadDir(this);
		if (AbStrUtil.isEmpty(photo_dir)) {
			AbToastUtil.showToast(ChangeInformationActivity.this, "存储卡不存在");
		} else {
			PHOTO_DIR = new File(photo_dir);
		}
	}

	private void initView() {
		headerLayout = (HeaderLayout) this.findViewById(R.id.headerLayout);
		name = (EditText) findViewById(R.id.tv_name);
		address = (TextView) findViewById(R.id.tv_address);
		male = (CheckBox) findViewById(R.id.cb_male);
		female = (CheckBox) findViewById(R.id.cb_female);
		layout_name = findViewById(R.id.layout_name);
		layout_sex = findViewById(R.id.layout_sex);
		layout_address = findViewById(R.id.layout_address);
		savechange = (Button) findViewById(R.id.btn_savechange);
		avatar = (ImageView) findViewById(R.id.iv_avatar);
		mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);
		albumButton = (Button) mAvatarView.findViewById(R.id.choose_album);
		camButton = (Button) mAvatarView.findViewById(R.id.choose_cam);
		cancelButton = (Button) mAvatarView.findViewById(R.id.choose_cancel);
		avatar.setOnClickListener(this);
		albumButton.setOnClickListener(this);
		camButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		layout_name.setOnClickListener(this);
		layout_sex.setOnClickListener(this);
		layout_address.setOnClickListener(this);
		savechange.setOnClickListener(this);
	}

	private void initAction() {
		headerLayout.showLeftBackButton("", new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangeInformationActivity.this.finish();
			}
		});

		male.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					female.setChecked(false);
				} else {
					female.setChecked(true);
				}
			}
		});
		female.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					male.setChecked(false);
				} else {
					male.setChecked(true);
				}
			}
		});
	}

	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			doTakePhoto();
		} else {
			AbToastUtil.showToast(ChangeInformationActivity.this, "没有可用的存储卡");
		}
	}

	protected void doTakePhoto() {
		try {
			mFileName = System.currentTimeMillis() + ".jpg";
			mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mCurrentPhotoFile));
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (Exception e) {
			AbToastUtil.showToast(ChangeInformationActivity.this,
					"未找到系统相机程序");
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA:
			Uri uri = mIntent.getData();
			String currentFilePath = getPath(uri);
			if (!AbStrUtil.isEmpty(currentFilePath)) {
				Intent intent1 = new Intent(this, CropImageActivity.class);
				intent1.putExtra("PATH", currentFilePath);
				startActivityForResult(intent1, CAMERA_CROP_DATA);
			} else {
				AbToastUtil.showToast(ChangeInformationActivity.this,
						"未在存储卡中找到这个文件");
			}
			break;
		case CAMERA_WITH_DATA:
			String currentFilePath2 = mCurrentPhotoFile.getPath();
			Intent intent2 = new Intent(this, CropImageActivity.class);
			intent2.putExtra("PATH", currentFilePath2);
			startActivityForResult(intent2, CAMERA_CROP_DATA);
			break;
		case SELECT_ADDRESS:
			String addressstr = mIntent.getStringExtra("address");
			address.setText(addressstr);
			break;
		case CAMERA_CROP_DATA:
			final String path = mIntent.getStringExtra("PATH");
			avatar.setImageBitmap(BitmapFactory.decodeFile(path));
			new SimpleNetTask(ctx, true) {
				boolean flag = true;
				MyProgressDialog dialog = new MyProgressDialog(ctx);

				@Override
				protected void onSucceed() {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();

					}
					if (flag) {
						Utils.toast("更换头像成功");
					} else {
						Utils.toast("更换头像失败");
					}
				}

				@Override
				protected void doInBack() throws Exception {
					FileInputStream fis = new FileInputStream(path);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int count = 0;
					while ((count = fis.read(buffer)) >= 0) {
						baos.write(buffer, 0, count);
					}
					String uploadBuffer = new String(Base64.encode(baos
							.toByteArray()));
					param.clear();
					param.put("userID", curuser.getID());
					param.put("Picture", uploadBuffer);
					String jsonstr = new WebService(C.MODIFYAVATAR, param)
							.getReturnInfo();
					try {
						JSONObject json = new JSONObject(jsonstr);
						if (json.get("ret").equals("success")) {
							String imageurl = json.getString("pictureUrl");
							curuser.setImage(imageurl);
							new PreferenceMap(ctx).setUser(curuser);
							User user=UserUtils.getUserInfo(Utils.getID());
							user.setAvatar(imageurl);
							App.getInstance().getContactList().put(Utils.getID(), user);
							flag = true;
							fis.close();
						} else {
							flag = false;
						}
					} catch (Exception e) {
						flag = false;
					}
				}

			}.execute();
			break;
		}
	}

	public String getPath(Uri uri) {
		if (AbStrUtil.isEmpty(uri.getAuthority())) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		CursorLoader cursorloder = new CursorLoader(ctx, uri, projection, null,
				null, null);
		Cursor cursor = cursorloder.loadInBackground();
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		return path;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_avatar:
			AbDialogUtil.showFragment(mAvatarView);
			break;
		case R.id.choose_album:
			AbDialogUtil.removeDialog(v.getContext());
			try {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
				intent.setType("image/*");
				startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
			} catch (ActivityNotFoundException e) {
				AbToastUtil.showToast(ChangeInformationActivity.this,
						"没有找到照片");
			}
			break;
		case R.id.choose_cam:
			AbDialogUtil.removeDialog(v.getContext());
			doPickPhotoAction();
			break;
		case R.id.choose_cancel:
			AbDialogUtil.removeDialog(v.getContext());
			break;
		case R.id.layout_address:
			Intent it = new Intent();
			it.setClass(ChangeInformationActivity.this,
					SelectAddressActivity.class);
			ChangeInformationActivity.this.startActivityForResult(it,
					SELECT_ADDRESS);
			break;
		case R.id.btn_savechange:
			name_str = name.getEditableText().toString();
			address_str = address.getText().toString();
			sex_str = "0";
			if (male.isChecked()) {
				sex_str = "1";
			}
			if (TextUtils.isEmpty(name_str)) {
				Utils.toast("用户名不能为空");
				return;
			}
			param.clear();
			param.put("userID", curuser.getID());
			param.put("nickname", name_str);
			param.put("sex", sex_str);
			param.put("address", address_str);
			new SimpleNetTask(ctx, true) {
				boolean flag;
				@Override
				protected void onSucceed() {
					if (flag) {
						curuser.setName(name_str);
						curuser.setSex(((male.isChecked() ? "男" : "女")));
						curuser.setAddress(address_str);
						new PreferenceMap(App.ctx).setUser(curuser);
						Utils.toast("修改信息成功");
						ChangeInformationActivity.this.finish();
					}
				}

				@Override
				protected void doInBack() throws Exception {
					String jsonstr = new WebService(C.MODIFYINFOR, param)
							.getReturnInfo();
					flag = GetObjectFromService.getSimplyResult(jsonstr);
				}
			}.execute();
			break;
		default:
			break;
		}

	}
}
