package com.ctrip.protoshop;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.HttpCallback;
import com.ctrip.protoshop.util.MD5Util;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.ctrip.protoshop.util.Util;

public class LoginActivity extends BaseActivity implements OnClickListener, HttpCallback {
	private static final int REQUEST_DOMMAIN_CODE = 1111;

	private View mProgressView;

	private AutoCompleteTextView mNameView;
	private EditText mPswView;
	private TextView mLoginView;
	private TextView mDomainView;
	private TextView mSignUpView;

	private String mNameStr;
	private String mPswStr;

	private String[] mEmails = { "@ctrip.com", "@163.com", "@qq.com" };
	private ArrayAdapter<String> mEmailAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mProgressView = findViewById(R.id.progress_layout);

		mNameView = (AutoCompleteTextView) findViewById(R.id.login_name_view);
		mPswView = (EditText) findViewById(R.id.login_psw_view);

		mLoginView = (TextView) findViewById(R.id.login_btn_view);
		mSignUpView = (TextView) findViewById(R.id.sign_up_view);
		// 域账号登陆
		mDomainView = (TextView) findViewById(R.id.domain_btn_view);
		if (Constans.ENVIRONMENT.isNeedDomain()) {
			mDomainView.setVisibility(View.VISIBLE);
			mDomainView.setOnClickListener(this);
		} else {
			mDomainView.setVisibility(View.GONE);
			mDomainView.setOnClickListener(null);
		}

		mLoginView.setOnClickListener(this);
		mSignUpView.setOnClickListener(this);

		mEmailAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		mNameView.setAdapter(mEmailAdapter);
		mNameView.setThreshold(1);

		mNameView.addTextChangedListener(mNameWatcher);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.login_btn_view) {
			dealLogin();
		} else if (id == R.id.sign_up_view) {
			dealSiginUp();
		} else if (id == R.id.domain_btn_view) {
			dealDomainLogin();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_DOMMAIN_CODE && resultCode == RESULT_OK) {
			finish();
		}
	}

	//邮箱后缀提示
	private TextWatcher mNameWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String string = s.toString();
			if (string.contains("@")) {
				string = string.substring(0, string.indexOf("@"));
			}
			mEmailAdapter.clear();
			for (String str : mEmails) {
				mEmailAdapter.add(string + str);
			}
			mEmailAdapter.notifyDataSetChanged();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	/**
	 * 
	 * 处理域账号登陆.
	 */
	private void dealDomainLogin() {

		sendGetRequest(Function.DOMAIN_CALLBACK, new HttpCallback() {

			@Override
			public void onErrorResponse(VolleyError error) {
				ProtoshopLog.e("error", error.toString());
				if (error instanceof NoConnectionError) {
					Toast.makeText(getApplicationContext(), "请连接网络!", Toast.LENGTH_SHORT).show();
				} else if (error instanceof TimeoutError) {
					Toast.makeText(getApplicationContext(), "网络连接超时!", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onResponse(JSONObject response) {
				ProtoshopLog.e("Domain_First", response.toString());
				try {
					if (response.has("status")) {
						String status = response.getString("status");
						if ("1".equals(status) && response.has("code")) {
							String code = response.getString("code");
							if ("1002".equals(code)) {
								startActivityForResult(new Intent(getApplicationContext(), DomainActivity.class), REQUEST_DOMMAIN_CODE);
							}
						} else if ("0".equals(status)) {
							dealLoginResult(response);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onHttpStart() {

			}
		});

	}

	/**
	 * 
	 * 响应注册点击按钮
	 */
	private void dealSiginUp() {
		Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
		startActivity(intent);
	}

	/**
	 * 
	 * 处理登陆函数.
	 * 
	 */
	private void dealLogin() {
		Util.hideSoftkeyboard(getApplicationContext(), mLoginView);

		mNameStr = mNameView.getText().toString();
		mPswStr = mPswView.getText().toString();

		if (TextUtils.isEmpty(mNameStr)) {
			Toast.makeText(getApplicationContext(), "请输入邮件地址", Toast.LENGTH_SHORT).show();
			return;
		} else {
			Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
			Matcher matcher = pattern.matcher(mNameStr);
			if (!matcher.matches()) {
				Toast.makeText(getApplicationContext(), "请输入正确的邮件地址", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		if (TextUtils.isEmpty(mPswStr)) {
			Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
		mProgressView.setVisibility(View.VISIBLE);
		Util.hideSoftkeyboard(getApplicationContext(), mLoginView);

		HashMap<String, String> postParams = new HashMap<String, String>();
		postParams.put("email", mNameStr);
		postParams.put("passwd", MD5Util.getMd5(mPswStr));

		sendPostRequest(Function.LOGIN, postParams, this);

	}

	/**
	 * 登陆网络请求回调函数
	 */
	@Override
	public void onResponse(JSONObject response) {
		ProtoshopLog.e(response.toString());
		mProgressView.setVisibility(View.GONE);
		String resultStr = "登陆失败!";

		try {
			String status = "";

			if (response.has("status")) {
				status = response.getString("status");
				if ("0".equals(status) && response.has("result")) {
					resultStr = dealLoginResult(response);
				} else if ("1".equals(status) && response.has("message")) {
					resultStr = response.getString("message");
				} else {
					resultStr = "服务器错误，请联系开发人员!";
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
			resultStr = "服务器返回错误!";
		}

		Toast.makeText(getApplicationContext(), resultStr, Toast.LENGTH_SHORT).show();

	}

	/**
	 * 
	 * 处理登陆成功后的返回结果
	 * 
	 * @param response
	 * @return
	 * @throws JSONException
	 */

	private String dealLoginResult(JSONObject response) throws JSONException {

		String resultStr = null;
		JSONArray resultArray = response.getJSONArray("result");
		JSONObject tokenObject = resultArray.getJSONObject(0);
		if (tokenObject.has("token")) {

			resultStr = "登陆成功!";

			String token = tokenObject.getString("token");
			String name = tokenObject.getString("email");
			SharedPreferences preferences = getSharedPreferences(Constans.USER_INFO, 0);
			Editor editor = preferences.edit();
			editor.putString(Constans.TOKEN, token);
			editor.putString(Constans.USER_NAME, name);
			editor.commit();

			ProtoshopApplication.getInstance().token = token;
			ProtoshopApplication.getInstance().userName = name;

			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
			finish();

		}

		return resultStr;
	}

	/**
	 * 登陆网络失败回调函数
	 */
	@Override
	public void onErrorResponse(VolleyError error) {
		ProtoshopLog.e("error", error.toString());
		mProgressView.setVisibility(View.GONE);
		Toast.makeText(getApplicationContext(), "网络错误稍后再试!", Toast.LENGTH_SHORT).show();
		if (error instanceof NoConnectionError) {
			new AlertDialog.Builder(this).setTitle("网络错误").setMessage("请连接DEV!").setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		}
	}

	@Override
	public void onHttpStart() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
