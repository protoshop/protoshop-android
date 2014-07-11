package com.ctrip.protoshop;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ctrip.protoshop.adapter.ProgramAdapter;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.FileReuest;
import com.ctrip.protoshop.http.HttpCallback;
import com.ctrip.protoshop.interfaces.IHomeScence;
import com.ctrip.protoshop.interfaces.imp.NetHomeScenceImp;
import com.ctrip.protoshop.model.ProgramModel;
import com.ctrip.protoshop.model.ZipModel;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.ctrip.protoshop.util.Util;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.protoshop.lua.LuaActivity;
import com.protoshop.lua.cache.ScenceCache;

//import com.protoshop.lua.util.ParseJsonUtil;

public class MainActivity extends BaseActivity implements OnItemClickListener, HttpCallback, OnRefreshListener<ListView>, OnClickListener {
	private final static String TAG = MainActivity.class.getSimpleName();

	private View mLoadingLayout;
	private TextView mLoadTipView;
	private ListView mListView;
	private PullToRefreshListView mPullToRefreshListView;
	private List<ProgramModel> mModels;
	private List<ProgramModel> mLocalModels;
	private Map<String, ProgramModel> mProgramMap;
	private ProgramAdapter mAdapter;

	private int mCurPosition = 0;
	private boolean isPullRefresh = false;

	// 获取工程入口
	private IHomeScence mHomeScence;

	/**
	 * 清除缓存广播,清除缓存后，列表中展示该项目未下载。
	 */
	private BroadcastReceiver clearCacheReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			for (ProgramModel model : mModels) {
				model.isLoadZip = false;
			}
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();

		ProtoshopApplication.getInstance().cachePath = Util.getUserRootFile().getAbsolutePath();

		mHomeScence = new NetHomeScenceImp();
		getPrograms();

		LocalBroadcastManager.getInstance(this).registerReceiver(clearCacheReceiver, new IntentFilter(Constans.CLEAR_CACHE));

	}

	private void initUI() {
		mLoadingLayout = findViewById(R.id.loading_layout);
		mLoadTipView = (TextView) mLoadingLayout.findViewById(R.id.program_comment_view);

		findViewById(R.id.title_reload_view).setOnClickListener(this);

		View settingView = findViewById(R.id.title_setting_view);
		settingView.setOnClickListener(this);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.program_expandableListView);
		mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullToRefreshListView.setOnRefreshListener(this);
		mPullToRefreshListView.getLoadingLayoutProxy().setPullLabel("下拉刷新");
		mPullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("加载中....");
		mPullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel("释放刷新");

		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setOnItemClickListener(this);
		mModels = new ArrayList<ProgramModel>();
		mAdapter = new ProgramAdapter(this, mModels);
		mListView.setAdapter(mAdapter);

		mProgramMap = new HashMap<String, ProgramModel>();
	}

	/**
	 * 
	 * 获取项目列表
	 */
	private void getPrograms() {
		getProgramsFromService(true);
		getProgramsFromLocal();
	}

	/**
	 * 
	 * 获取服务器最新项目列表.
	 */
	private void getProgramsFromService(boolean isNeedTip) {
		// 发送请求，获取项目列表
		if (isNeedTip) {
			mLoadingLayout.setVisibility(View.VISIBLE);
			mLoadTipView.setText("更新列表中...");
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", ProtoshopApplication.getInstance().token);
		sendGetParamRequest(Function.PROGRAM_LIST, params, this);

	}

	/**
	 * 
	 * 获取本地项目列表
	 */
	private void getProgramsFromLocal() {

		try {
			String localStr = Util.readFileContent(Util.getUserRootFile(), Constans.LOCAL_PROGRAM_LIST);
			if (!TextUtils.isEmpty(localStr)) {
				JSONObject jsonObject = new JSONObject(localStr);
				mLocalModels = parseResult(jsonObject);

				File floder = Util.getUserRootFile();
				List<String> nameList = new ArrayList<String>();
				for (String name : floder.list()) {
					nameList.add(name);
				}

				for (ProgramModel model : mLocalModels) {
					if (nameList.contains(model.appID)) {
						model.isLoadZip = true;
						model.home_scence = mHomeScence.getHomeSccence(getApplicationContext(), model.appID);
						mProgramMap.put(model.appID, model);
					}
				}

				mModels.clear();
				mModels.addAll(mLocalModels);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/*
	 * 获取服务器列表回调函数
	 * 
	 * @see com.android.volley.Response.Listener#onResponse(java.lang.Object)
	 */

	@Override
	public void onResponse(JSONObject response) {
		ProtoshopLog.e("into--[onResponse]");
		ProtoshopLog.e(response.toString());

		try {
			String status;
			if (response.has("status")) {
				status = response.getString("status");
				if ("1".equals(status) && response.has("code")) {
					String code = response.getString("code");
					if ("10002".equals(code) || "10003".equals(code)) {
						mLoadingLayout.setVisibility(View.GONE);
						LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constans.LOGOUT));
						startActivity(new Intent(getApplicationContext(), LoginActivity.class));
						finish();
						Toast.makeText(getApplicationContext(), "请重新登陆!", Toast.LENGTH_SHORT).show();
					}

				} else if ("0".equals(status) && response.has("result")) {
					List<ProgramModel> models = parseResult(response);

					// 服务器获取的列表中是否有本地缓存，如果有本地缓存，并且editTime相同，不用刷新。否则刷新。服务器没有，本地有，默认本地删除。
					for (ProgramModel programModel : models) {
						if (mProgramMap.containsKey(programModel.appID)) {
							if (programModel.editTime.equals(mProgramMap.get(programModel.appID).editTime)) {
								programModel.home_scence = mProgramMap.get(programModel.appID).home_scence;
								programModel.isLoadZip = true;
							}
						}
					}

					mModels.clear();
					mModels.addAll(models);
					mAdapter.notifyDataSetChanged();
					mLoadingLayout.setVisibility(View.GONE);
					mPullToRefreshListView.onRefreshComplete();

					mPullToRefreshListView.getLoadingLayoutProxy().setLastUpdatedLabel(Util.getTodayDate(new Date()));
				}
			} else {
				Toast.makeText(getApplicationContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		ProtoshopLog.e(error.toString());
		mLoadingLayout.setVisibility(View.GONE);
	}

	@Override
	public void onHttpStart() {
		if (!isPullRefresh) {
			mLoadingLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 列表点击回调
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mCurPosition = position - 1;
		ProgramModel model = mModels.get(mCurPosition);
		if (model.isLoadZip) {
			Intent intent = new Intent(this, LuaActivity.class);
			intent.putExtra(Constans.SCENE, model.home_scence);
			intent.putExtra(Constans.APPID, model.appID);

			ScenceCache.getInstance().scences.add(model.home_scence);
			startActivity(intent);

		} else if (!model.isLoading) {
			mLoadTipView.setText(R.string.loading_text);
			loadZipInfo(mCurPosition);
		} else if (model.isLoading) {
			Toast.makeText(this, "正在下载,请耐心等待!", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.title_setting_view) {
			startActivity(new Intent(getApplicationContext(), SettingActivity.class));
		} else if (v.getId() == R.id.title_reload_view) {
			getProgramsFromService(true);
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// 发送请求，获取项目列表
		isPullRefresh = true;
		getProgramsFromService(false);

	}

	/**
	 * 
	 * 获取压缩包信息
	 * 
	 * @param position
	 */
	private void loadZipInfo(int position) {
		mCurPosition = position;
		final ProgramModel programModel = mModels.get(position);
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", programModel.appID);
		params.put("token", ProtoshopApplication.getInstance().token);
		ProtoshopLog.e("appId", programModel.appID);
		ProtoshopLog.e("token", ProtoshopApplication.getInstance().token);
		sendPostRequest(Function.ZIP, params, new HttpCallback() {

			@Override
			public void onErrorResponse(VolleyError error) {
				programModel.isLoading = false;
				mAdapter.notifyDataSetChanged();
				mLoadingLayout.setVisibility(View.GONE);
				ProtoshopLog.e("error", error.toString());
				if (error instanceof NoConnectionError) {
					Toast.makeText(getApplicationContext(), "请连接网络!", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onResponse(JSONObject response) {
				ProtoshopLog.e(response.toString());
				programModel.isLoading = false;
				mAdapter.notifyDataSetChanged();
				try {
					if (response.has("status")) {
						String statusStr = response.getString("status");
						if ("0".equals(statusStr)) {
							ZipModel model = new ZipModel();
							JSONArray resultArray = response.getJSONArray("result");
							JSONObject jsonObject = resultArray.getJSONObject(0);
							try {
								model.url = jsonObject.getString("url");
								Toast.makeText(getApplicationContext(), "获取Zip信息成功,开始下载ZIP文件!", Toast.LENGTH_SHORT).show();
								loadZipFile(programModel, model);
							} catch (JSONException e) {
								e.printStackTrace();
								mLoadingLayout.setVisibility(View.GONE);
								Toast.makeText(getApplicationContext(), "解析JSON出错!", Toast.LENGTH_SHORT).show();
							}

						} else if ("1".equals(statusStr)) {
							String code = response.getString("code");
							if ("15005".equals(code)) {
								LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent(Constans.LOGOUT));
								startActivity(new Intent(getApplicationContext(), LoginActivity.class));
								finish();
								Toast.makeText(getApplicationContext(), "请重新登陆!", Toast.LENGTH_SHORT).show();
							} else if ("15003".equals(code) || "15002".equals(code)) {
								Toast.makeText(getApplicationContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onHttpStart() {
				programModel.isLoading = true;
				mAdapter.notifyDataSetChanged();
				mLoadingLayout.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 
	 * 下载压缩文件.
	 * 
	 * @param 压缩包MODEL
	 */
	private void loadZipFile(final ProgramModel programModel, ZipModel model) {
		programModel.isLoading = true;
		mAdapter.notifyDataSetChanged();
		FileReuest request = new FileReuest(model.url, new Listener<File>() {

			@Override
			public void onResponse(File response) {
				programModel.isLoading = false;
				mAdapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "Zip下载完成，开始解压!", Toast.LENGTH_SHORT).show();
				try {
					Util.unZipFiles(response, Util.getUserRootFile().getAbsolutePath());
					Util.deleteFile(response);
					mProgramMap.put(mModels.get(mCurPosition).appID, mModels.get(mCurPosition));
					mModels.get(mCurPosition).isLoadZip = true;
					mAdapter.notifyDataSetChanged();
					// Toast.makeText(getApplicationContext(), "ZIP解压完成,开始生成Lua!", Toast.LENGTH_SHORT).show();
					mModels.get(mCurPosition).home_scence = mHomeScence.getHomeSccence(getApplicationContext(), mModels.get(mCurPosition).appID);
					// mLoadingLayout.setVisibility(View.GONE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				programModel.isLoading = false;
				mAdapter.notifyDataSetChanged();
				mLoadingLayout.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "获取压缩包失败!", Toast.LENGTH_SHORT).show();
				ProtoshopLog.e("error", arg0.toString());
			}
		});
		request.setTag(TAG);
		ProtoshopApplication.getInstance().requestQueue.add(request);
	}

	/**
	 * 
	 * 解析服务器返回JSON数据
	 * 
	 * @param response
	 *            :服务器返回的数据
	 * @return 解析后的项目列表
	 * @throws JSONException
	 */
	private List<ProgramModel> parseResult(JSONObject response) throws JSONException {
		List<ProgramModel> list = new ArrayList<ProgramModel>();

		JSONArray array = response.getJSONArray("result");
		for (int i = 0; i < array.length(); i++) {
			ProgramModel model = new ProgramModel();
			JSONObject object = array.getJSONObject(i);
			ProtoshopLog.e(object.toString());
			if (object.has("appID")) {
				model.appID = object.getString("appID");
			}
			if (object.has("appName")) {
				model.appName = object.getString("appName");
			}
			if (object.has("appDesc")) {
				model.comment = object.getString("appDesc");
			}
			if (object.has("createTime")) {
				model.createTime = object.getString("createTime");
			}
			if (object.has("editTime")) {
				model.editTime = object.getString("editTime");
			}
			if (object.has("appIcon")) {
				// model.icon = object.getString("appIcon");
			}
			if (object.has("isPublic")) {
				model.isPublic = object.getString("isPublic");
			}
			list.add(model);
		}

		Util.saveFile(Constans.LOCAL_PROGRAM_LIST, response.toString());

		return list;
	}

	boolean isFirst = true;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isFirst) {
				Toast.makeText(getApplicationContext(), "再按一次就退出了!", Toast.LENGTH_SHORT).show();
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(1000);
							isFirst = true;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
				isFirst = false;
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		ProtoshopApplication.getInstance().requestQueue.cancelAll(TAG);
		for (ProgramModel model : mModels) {
			model.isLoading = false;
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(clearCacheReceiver);
	}

}
