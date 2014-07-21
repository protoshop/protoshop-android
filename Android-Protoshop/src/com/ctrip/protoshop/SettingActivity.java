package com.ctrip.protoshop;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Environment;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.OnHttpListener;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.ctrip.protoshop.util.Util;
import com.ctrip.protoshop.widget.AnimationButton;
import com.ctrip.protoshop.widget.AnimationButton.OnConfirmLisntener;

public class SettingActivity extends BaseActivity implements OnClickListener, TextWatcher, OnConfirmLisntener {

	private EditText mFeedbackView;
	private TextView mSendView;
	private TextView mShowCountView;
	private View mFeedbackInputLayout;

	private EditText mNickNameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Setting");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		/* 用户信息相关 */
		((TextView) findViewById(R.id.show_email_view)).setText(ProtoshopApplication.getInstance().userName);
		mNickNameView = (EditText) findViewById(R.id.nick_name_view);
		mNickNameView.setText(ProtoshopApplication.getInstance().userInfo.nickname);
		/* 修改密码 */
		findViewById(R.id.psw_layout).setOnClickListener(this);
		/* 清除缓存 */
		((AnimationButton) findViewById(R.id.clear_cache_btn)).setOnConfirmLisntener(this);

		/* 反馈相关 */
		findViewById(R.id.feedback_layout).setOnClickListener(this);
		mFeedbackInputLayout = findViewById(R.id.feedback_input_layout);

		mFeedbackView = (EditText) findViewById(R.id.feedback_editText);
		mFeedbackView.addTextChangedListener(this);
		mSendView = (TextView) findViewById(R.id.feedback_send_view);
		mSendView.setOnClickListener(this);
		mShowCountView = (TextView) findViewById(R.id.show_text_count_view);

		/* 捐献按钮 */
		findViewById(R.id.contribute_layout).setOnClickListener(this);

		/* 登出按钮 */
		((AnimationButton) findViewById(R.id.logout_btn)).setOnConfirmLisntener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.ic_action_save) {
			saveNickName(findViewById(R.id.ic_action_save));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Intent getSupportParentActivityIntent() {
		return getIntent();
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.psw_layout) {
			startActivity(new Intent(getApplicationContext(), ChangePswActivity.class));
		} else if (v.getId() == R.id.feedback_send_view) {
			sendFeedback(v);
		} else if (v.getId() == R.id.feedback_layout) {
			dealFeedback(v);
		} else if (v.getId() == R.id.contribute_layout) {
			dealContribute();
		}
	}

	/**
	 * 
	 * 修改用户昵称
	 * 
	 * @param v
	 */
	private void saveNickName(View v) {
		Util.hideSoftkeyboard(getApplicationContext(), v);
		Toast.makeText(getApplicationContext(), "信息更新中...", Toast.LENGTH_LONG).show();
		String nickName = mNickNameView.getText().toString();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", ProtoshopApplication.getInstance().token);
		params.put("nickname", nickName);
		sendPostRequest(Function.UPDATE_USERINFO, params, new OnHttpListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "网络错误，请稍后再试!", Toast.LENGTH_SHORT).show();
				ProtoshopLog.e(error.toString());
			}

			@Override
			public void onResponse(String response) {
				try {
					JSONObject resultObject = new JSONObject(response);
					ProtoshopLog.e(resultObject.toString());
					if (resultObject.has("status")) {
						String status = resultObject.getString("status");
						if ("0".equals(status)) {
							Toast.makeText(getApplicationContext(), "修改成功!", Toast.LENGTH_SHORT).show();
						} else if ("1".equals(status)) {
							if (resultObject.has("code")) {
								String errorCode = resultObject.getString("code");
								if ("5002".equals(errorCode)) {
									dealLogout();
								} else if ("5003".equals(errorCode)) {
									Toast.makeText(getApplicationContext(), "服务器内部错误!", Toast.LENGTH_SHORT).show();
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "解析错误!", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onHttpStart() {

			}
		});
	}

	/**
	 * 
	 * 处理反馈显示
	 * 
	 * @param v
	 */
	private void dealFeedback(View v) {
		int visible = mFeedbackInputLayout.getVisibility();
		visible = visible == View.VISIBLE ? View.GONE : View.VISIBLE;
		mFeedbackInputLayout.setVisibility(visible);
	}

	/**
	 * 
	 * 发送反馈
	 * 
	 * @param view
	 */
	private void sendFeedback(View view) {
		String feedback = mFeedbackView.getText().toString();
		if (TextUtils.isEmpty(feedback)) {
			Toast.makeText(getApplicationContext(), "请填写反馈内容!", Toast.LENGTH_SHORT).show();
			return;
		}
		Util.hideSoftkeyboard(getApplicationContext(), view);

		HashMap<String, String> postParams = new HashMap<String, String>();
		postParams.put("token", ProtoshopApplication.getInstance().token);
		postParams.put("content", feedback);
		postParams.put("source", "2");
		mFeedbackInputLayout.setVisibility(View.GONE);
		sendPostRequest(Function.FEEDBACK, postParams, new OnHttpListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				ProtoshopLog.e("error", error.toString());
				Toast.makeText(getApplicationContext(), "网络错误,请稍后再试!", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onResponse(String response) {
				ProtoshopLog.e(response);
				try {
					JSONObject resultObject = new JSONObject(response);
					String resultStr = "反馈成功!";
					if (resultObject.has("status")) {
						String status = resultObject.getString("status");
						if ("1".equals(status) && resultObject.has("code")) {

							String errorCode = resultObject.getString("code");
							if ("13005".equals(errorCode)) {
								resultStr = "服务器错误!";
							} else if ("13004".equals(errorCode)) {
								resultStr = "反馈源错误!";
							}
						}
					}

					Toast.makeText(getApplicationContext(), resultStr, Toast.LENGTH_SHORT).show();

				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "解析错误!", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onHttpStart() {
			}
		});
	}

	/**
	 * 捐献操作.
	 */
	private void dealContribute() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse("https://qr.alipay.com/ap27zqsxo3v5q61z1f");
		intent.setData(content_url);
		startActivity(intent);
	}

	/**
	 * 
	 * 清除缓存并发送清除缓存广播
	 * 
	 */
	private void dealClearCahce() {
		Util.deleteFile(Util.getUserRootFile());
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Constans.CLEAR_CACHE));
	}

	/**
	 * 
	 * 用户登出
	 */
	private void dealLogout() {

		// 外网不需要域登陆
		if (!Constans.ENVIRONMENT.equals(Environment.OUTERNET)) {
			CookieSyncManager syncManager = CookieSyncManager.createInstance(this);
			syncManager.startSync();
			CookieManager.getInstance().removeAllCookie();
			sendGetRequest(Function.LOGINOUT, new OnHttpListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
				}

				@Override
				public void onResponse(String response) {
				}

				@Override
				public void onHttpStart() {
				}
			});
		}

		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Constans.LOGOUT));
		startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		SharedPreferences preferences = getSharedPreferences(Constans.USER_INFO, 0);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
		finish();

	}

	/* 反馈文字编辑 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		int curCount = 240 - (mFeedbackView.getText().toString().length() + s.length()) / 2;
		mShowCountView.setText("" + curCount);
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (mFeedbackView.getText().toString().length() > 0) {
			mSendView.setTextColor(getResources().getColor(R.color.orange_ff8a00));
			mSendView.setEnabled(true);
		} else {
			mSendView.setTextColor(getResources().getColor(R.color.orange_ffd5b8));
			mSendView.setEnabled(false);
		}
	}

	@Override
	public boolean onConfirm(View view) {
		if (view.getId() == R.id.clear_cache_btn) {
			dealClearCahce();
			return true;
		} else if (view.getId() == R.id.logout_btn) {
			dealLogout();
			return false;
		}
		return false;
	}

}
