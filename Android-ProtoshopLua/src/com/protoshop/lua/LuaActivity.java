package com.protoshop.lua;

import java.util.ArrayList;
import java.util.List;

import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.protoshop.lua.Constant.Constants;
import com.protoshop.lua.cache.ScenceCache;
import com.protoshop.lua.util.LuaLog;
import com.protoshop.lua.util.Util;

public class LuaActivity extends Activity {

	private LuaState mLuaState;

	// 要展示的Scene ID。
	private String mScene;
	// Scene 所在工程ID。
	private String mAppID;
	// 动画类型
	private String mAnimType;

	// Finish 广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LuaActivity.this.finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LuaLog.e("LuaActivity", "into--[onCreate]");
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}

		mScene = intent.getStringExtra(Constants.SCENE);
		mAppID = intent.getStringExtra(Constants.APPID);
		mAnimType = intent.getStringExtra(Constants.ANIM_TYPE);

		if (TextUtils.isEmpty(mScene)) {
			showError("数据解析错误!\n请清除缓存重新下载!");
			return;
		}

		if (TextUtils.isEmpty(mAppID)) {
			showError("数据解析错误!\n请清除缓存重新下载!");
			return;
		}

		getWindow().getDecorView().setTag(mScene);
		List<View> hotViews = new ArrayList<View>();
		ScenceCache.getInstance().hotMap.put(mScene, hotViews);

		String luaStr = Util.getLusStr(this, mAppID, mScene + ".lua");

		if (TextUtils.isEmpty(luaStr)) {
			TextView textView = new TextView(this);
			textView.setText("Lua解析错误!");
			setContentView(textView);
			return;
		}

		LuaLog.e(luaStr);

		// 执行Lua代码
		mLuaState = LuaStateFactory.newLuaState();
		mLuaState.LdoString(luaStr);
		mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "onCreate");
		mLuaState.pushJavaObject(this);
		mLuaState.call(1, 0);

		IntentFilter filter = new IntentFilter(mScene);
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Constants.BACK_HOME));

		LuaLog.e("LuaActivity", "out--[onCreate]");
	}

	private void showError(String msg) {
		TextView textView = new TextView(this);
		textView.setText(msg);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.RED);
		textView.setTextSize(20);
		textView.setBackgroundColor(Color.WHITE);
		setContentView(textView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mLuaState != null) {
			mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "onResume");
			mLuaState.pushJavaObject(this);
			mLuaState.call(1, 0);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		if (action == MotionEvent.ACTION_POINTER_2_DOWN) {
			dealTwoPointer();
			return true;
		}

		List<View> hotViews = ScenceCache.getInstance().hotMap.get(mScene);
		if (action == MotionEvent.ACTION_DOWN) {

			for (View view : hotViews) {
				view.setBackgroundColor(Color.CYAN);
			}
		} else if (action == MotionEvent.ACTION_UP) {
			for (View view : hotViews) {
				view.setBackgroundColor(0);
			}
		}
		return super.onTouchEvent(event);
	}

	private void dealTwoPointer() {
		final Dialog dialog = new Dialog(this, R.style.AppDialog);
		dialog.setContentView(R.layout.back_home_dialog);
		dialog.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Constants.BACK_HOME));
				dialog.dismiss();
			}
		});
		dialog.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	protected void onStop() {
		super.onStop();
		LuaLog.e("into---[onStop]");
		if (mLuaState != null) {
			mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "onStop");
			mLuaState.pushJavaObject(this);
			mLuaState.call(1, 0);
		}
	}

	// 查看要启动的Scene是否在缓存中，如果存在判断为返回,Scene上Activity被finish。
	public void startActivity(String animType, Intent intent) {
		String scenceId = intent.getStringExtra(Constants.SCENE);
		if (ScenceCache.getInstance().scences.contains(scenceId)) {
			List<String> scences = ScenceCache.getInstance().scences;
			List<String> subList = scences.subList(scences.indexOf(scenceId) + 1, scences.size());
			for (String string : subList) {
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(string));
			}
		} else {
			ScenceCache.getInstance().scences.add(scenceId);
			super.startActivity(intent);
			overridePendingTransition(getInAnim(animType), getOutAnim(animType));
		}

	}

	private int getInAnim(String animType) {
		if ("1".equals(animType)) {
			return R.anim.push_left_in;
		} else if ("2".equals(animType)) {
			return R.anim.push_right_in;
		} else if ("3".equals(animType)) {
			return R.anim.push_down_in;
		} else if ("4".equals(animType)) {
			return R.anim.push_up_in;
		}
		return 0;
	}

	private int getOutAnim(String animType) {
		if ("1".equals(animType)) {
			return R.anim.push_left_out;
		} else if ("2".equals(animType)) {
			return R.anim.push_right_out;
		} else if ("3".equals(animType)) {
			return R.anim.push_down_out;
		} else if ("4".equals(animType)) {
			return R.anim.push_up_out;
		}
		return 0;
	}

	private int getFinishInAnim(String animType) {
		if ("1".equals(animType)) {
			return R.anim.push_right_in;
		} else if ("2".equals(animType)) {
			return R.anim.push_left_in;
		} else if ("3".equals(animType)) {
			return R.anim.push_up_in;
		} else if ("4".equals(animType)) {
			return R.anim.push_down_in;
		}
		return 0;
	}

	private int getFinishOutAnim(String animType) {
		if ("1".equals(animType)) {
			return R.anim.push_right_out;
		} else if ("2".equals(animType)) {
			return R.anim.push_left_out;
		} else if ("3".equals(animType)) {
			return R.anim.push_up_out;
		} else if ("4".equals(animType)) {
			return R.anim.push_down_out;
		}
		return 0;
	}

	@Override
	public void finish() {
		super.finish();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		ScenceCache.getInstance().scences.remove(mScene);
		if (!TextUtils.isEmpty(mAnimType)) {
			overridePendingTransition(getFinishInAnim(mAnimType), getFinishOutAnim(mAnimType));
		}
	}
}
