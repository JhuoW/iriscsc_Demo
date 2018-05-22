package com.xzmc.airuishi.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.adapter.BaseListAdapter;
import com.xzmc.airuishi.adapter.ViewHolder;
import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.bean.Answer;
import com.xzmc.airuishi.bean.Question;
import com.xzmc.airuishi.bean.Result;
import com.xzmc.airuishi.https.GetObjectFromService;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.SimpleNetTask;
import com.xzmc.airuishi.utils.Utils;
import com.xzmc.airuishi.view.HeaderLayout;


public class QuestionActivity extends BaseActivity {

	private List<Question> datas = new ArrayList<Question>();
	private int questionLen;
	private String curQuestionId;
	private ListView question_listview;
	private QuestionAdapter adapter;
	
	private ProgressDialog dialog;
	
	JSONArray jsonArray;
	
	private Button btn_return;
	private Button btn_submit;

	String title ;
	
	public class QuestionAdapter extends BaseListAdapter<Question> {

		public QuestionAdapter(Context ctx, List<Question> datas, int layoutId) {
			super(ctx, datas, layoutId);
		}

		@Override
		public void conver(ViewHolder holder, int position, final Question t) {
			TextView question_number = holder.getView(R.id.question_number);
			TextView question_content = holder.getView(R.id.question_content);
			RadioGroup single_selection = holder.getView(R.id.single_selection);
			LinearLayout mutiple_selection = holder
					.getView(R.id.mutiple_selection);
			EditText input_selection = holder.getView(R.id.input_selection);

			question_number.setText("(" + (position + 1) + "/" + questionLen
					+ ")");
			question_content.setText(t.getQuestion());
			// 单选
			if (t.getType() == 1) {
				input_selection.setVisibility(View.GONE);
				single_selection.setVisibility(View.VISIBLE);
				mutiple_selection.setVisibility(View.GONE);
				single_selection.removeAllViews();

				for (int i = 0; i < t.getAnswers().size(); i++) {
					RadioButton selection = new RadioButton(ctx);
					selection.setId(i);
					final int pos = i;
					if (t.getAnswers().get(i).isSelected()) {
						selection.setChecked(true);
					}
					selection
							.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									t.getAnswers().get(pos)
											.setSelected(isChecked);
								}
							});
					
					Log.d("t.getAnswers()", t.getAnswers().get(i).getSelection()); //打印出A，B
					selection.setTextColor(Color.BLACK);
					Log.d("text", selection.getText().toString());
					selection.setText(t.getAnswers().get(i).getSelection()
							+ "   "
							+ t.getAnswers().get(i).getSelectionContent());
					single_selection.addView(selection);
				}
			}
			// 多选
			else if (t.getType() == 2) {
				input_selection.setVisibility(View.GONE);
				single_selection.setVisibility(View.GONE);
				mutiple_selection.setVisibility(View.VISIBLE);
				mutiple_selection.removeAllViews();
				for (int i = 0; i < t.getAnswers().size(); i++) {
					CheckBox selection = new CheckBox(ctx);
					final int pos = i;
					selection.setId(i);
					selection.setChecked(t.getAnswers().get(i).isSelected());
					selection
							.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									t.getAnswers().get(pos)
											.setSelected(isChecked);
								}
							});
					selection.setTextColor(Color.BLACK);
					selection.setText(t.getAnswers().get(i).getSelection()
							+ "   "
							+ t.getAnswers().get(i).getSelectionContent());
					mutiple_selection.addView(selection);
				}
			}
			// 问答
			else if (t.getType() == 0) {
				input_selection.setVisibility(View.VISIBLE);
				single_selection.setVisibility(View.GONE);
				mutiple_selection.setVisibility(View.GONE);
			}
		}

	}
	
	public void findView(){
		headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
		headerLayout.showLeftBackButton(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				QuestionActivity.this.finish();
			}
		});
		question_listview = (ListView) findViewById(R.id.question_listview);
		adapter = new QuestionAdapter(getApplicationContext(), datas, R.layout.item_questionaire_layout);
		question_listview.setAdapter(adapter);
		
		headerLayout.showLeftBackButton(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				quitDialog();
			}
		});
		
		headerLayout.showTitle(title);
		headerLayout.showRightTextButton("提交", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submitResult();

			}
		});

	}
	
	private void quitDialog(){
		
		new SweetAlertDialog(ctx).setTitleText("确定退出本次问卷调查？").setCancelText("继续回答")
				.setCancelClickListener(new OnSweetClickListener() {
					
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						// TODO Auto-generated method stub
						sweetAlertDialog.dismiss();
					}
				}).setConfirmText("确定退出").setConfirmClickListener(new OnSweetClickListener() {
					
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						// TODO Auto-generated method stub
						sweetAlertDialog.dismiss();
						
						finish();
					}
				}).showCancelButton(true).show();
	
	}
	
	
	private void submitResult(){
		
		final List<Result> results = new ArrayList<Result>();
		for (int i = 0; i < datas.size(); i++) {
			Question question = datas.get(i);
			Result result = new Result();
			result.setNumber((i + 1) + "");
			// 问答
			if (question.getType() == 0) {
				String str = ((EditText) question_listview.getChildAt(i)
						.findViewById(R.id.input_selection)).getText()
						.toString().trim();
				result.setContent(str);
			}
			// 单选
			else if (question.getType() == 1) {
				String str = "";
				for (Answer answer : question.getAnswers()) {
					if (answer.isSelected()) {
						str = answer.getSelection();
					}
				}
				result.setContent(str);
			}
			// 多选
			else if (question.getType() == 2) {
				StringBuffer sb = new StringBuffer();
				for (Answer answer : question.getAnswers()) {
					if (answer.isSelected()) {
						sb.append(answer.getSelection());
					}
				}
				result.setContent(sb.toString());
			}
			results.add(result);
		}
		for (Result result : results) {
			if (result.getContent().isEmpty()) {
				Utils.toast("题目尚未完成");
				return;
			}
		}
		new SweetAlertDialog(ctx).setTitleText("提交问卷答案")
				.setContentText("确定提交问卷答案？").setCancelText("再看一会")
				.setCancelClickListener(new OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						sweetAlertDialog.dismiss();
					}
				}).setConfirmText("确定提交")
				.setConfirmClickListener(new OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						sweetAlertDialog.dismiss();
						String jsonstr = Utils.transObject2Json(results,
								"result");
						param.clear();
						param.put("questionId", curQuestionId);
						param.put("answers", jsonstr);
						Log.i("QuestionActivity", jsonstr);
						param.put("userID", Utils.getID());		
						
						new SimpleNetTask(ctx, true) {
							boolean flag;
							@Override
							protected void onSucceed() {
								if(flag){
									Utils.toast("感谢您的支持");
									finish();
								}else{
									Utils.toast("提交答案失败");
								}
							}
							@Override
							protected void doInBack() throws Exception {
								String jsonstr = new WebService(C.SUBMITANSWER,
										param).getReturnInfo();
								flag = GetObjectFromService
										.getSimplyResult(jsonstr);
//								
//								Log.d("QuestionaireActivity..jsonstr", jsonstr);
//								
							}
						}.execute();
					}
				}).showCancelButton(true).show();
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question);
		curQuestionId = getIntent().getExtras().getString("questionId");
		title = getIntent().getExtras().getString("questionName");
		Log.d("curQuestionId", curQuestionId);
		findView();
		initData();
	}
	
	
	private void initData() {
		dialog = new ProgressDialog(ctx);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("加载中......");
		dialog.show();
		param.clear();
		param.put("questionId", curQuestionId);
		new SimpleNetTask(ctx, true) {
			List<Question> temp;

			@Override
			protected void onSucceed() {
				if (temp == null) {
					dialog.dismiss();
					Utils.toast("获取数据失败");
					return;
				} else {
					dialog.dismiss();
					if (temp.size() == 0) {
						Utils.toast("本问卷没有题目");
						return;
					}
					questionLen = temp.size();
					datas.clear();
					datas.addAll(temp);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			protected void doInBack() throws Exception {
				String jsonstr = new WebService(C.GETQUESTIONCONTENT, param)
						.getReturnInfo();
				temp = GetObjectFromService.getQuestion(jsonstr);
			}
		}.execute();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quitDialog();
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
}
