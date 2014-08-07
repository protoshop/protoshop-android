package com.ctrip.protoshop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.actionbarcompat.AbcPullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ctrip.protoshop.adapter.ProgramAdapter;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.OnHttpListener;
import com.ctrip.protoshop.interfaces.IHomeScence;
import com.ctrip.protoshop.interfaces.imp.LoadedState;
import com.ctrip.protoshop.interfaces.imp.NetHomeScenceImp;
import com.ctrip.protoshop.interfaces.imp.NormalState;
import com.ctrip.protoshop.mini.ProjectListActivity;
import com.ctrip.protoshop.model.ProgramModel;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.ctrip.protoshop.util.Util;

//import com.protoshop.lua.util.ParseJsonUtil;

public class MainActivity extends BaseActivity {
	private final static String TAG = MainActivity.class.getSimpleName();

	private ListView mListView;
	private View mEmptyView;
	private AbcPullToRefreshLayout mSwipeRefreshLayout;

	private List<ProgramModel> mModels;
	private List<ProgramModel> mLocalModels;
	private Map<String, ProgramModel> mProgramLoadedMap;
	private ProgramAdapter mAdapter;

	// 获取工程入口
	private IHomeScence mHomeScence;

	/**
	 * 清除缓存广播,清除缓存后，列表中展示该项目未下载。
	 */
	private BroadcastReceiver clearCacheReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			for (ProgramModel model : mModels) {
				if (!(model.state instanceof NormalState)) {
					model.state = new NormalState(MainActivity.this, mAdapter, mHomeScence, mProgramLoadedMap);
				}
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
		mProgramLoadedMap = new HashMap<String, ProgramModel>();

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);

		mSwipeRefreshLayout = (AbcPullToRefreshLayout) findViewById(R.id.swipe_refresh_layout);

		mListView = (ListView) findViewById(R.id.program_expandableListView);
		mModels = new ArrayList<ProgramModel>();
		mAdapter = new ProgramAdapter(this, mModels);
		mListView.setAdapter(mAdapter);

		mEmptyView = findViewById(R.id.empty_view);

		addOnListener();
	}

	private void addOnListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ProgramModel model = (ProgramModel) mAdapter.getItem(position);
				if (model instanceof ProgramModel) {
					model.state.onItemClick(model);
				}
			}
		});

		ActionBarPullToRefresh.from(this).allChildrenArePullable().listener(new OnRefreshListener() {

			@Override
			public void onRefreshStarted(View view) {

				getProgramsFromService();

			}
		}).setup(mSwipeRefreshLayout);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.ic_action_search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				mAdapter.getFilter().filter(arg0);
				return true;
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.ic_action_search:

			return true;
		case R.id.ic_action_local:
			startActivity(new Intent(getApplicationContext(), ProjectListActivity.class));
			return true;
		case R.id.ic_action_settings:
			startActivity(new Intent(getApplicationContext(), SettingActivity.class));
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 * 获取项目列表
	 */
	private void getPrograms() {
		getProgramsFromService();
		getProgramsFromLocal();
	}

	/**
	 * 
	 * 获取服务器最新项目列表.
	 */
	private void getProgramsFromService() {
		// 发送请求，获取项目列表

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", ProtoshopApplication.getInstance().token);
		sendGetParamRequest(Function.PROGRAM_LIST, params, new OnHttpListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				ProtoshopLog.e(error.toString());
				Toast.makeText(getApplicationContext(), "列表更新失败,请稍后再试!", Toast.LENGTH_SHORT).show();
				mSwipeRefreshLayout.setRefreshComplete();
			}

			@Override
			public void onResponse(String response) {
				ProtoshopLog.e("into--[onResponse]");
				ProtoshopLog.e(response);

				try {
					JSONObject resultObject = new JSONObject(response);
					String status;
					if (resultObject.has("status")) {
						status = resultObject.getString("status");
						if ("1".equals(status) && resultObject.has("code")) {
							relogin(resultObject);
						} else if ("0".equals(status) && resultObject.has("result")) {
							dealNormalResult(resultObject);
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

			/**
			 * @param resultObject
			 * @throws JSONException
			 */
			private void dealNormalResult(JSONObject resultObject) throws JSONException {
				List<ProgramModel> models = parseResult(resultObject);

				// 服务器获取的列表中是否有本地缓存，如果有本地缓存，并且editTime相同，不用刷新。否则刷新。服务器没有，本地有，默认本地删除。
				for (ProgramModel programModel : models) {
					compareResult(programModel);
				}

				mModels.clear();
				mModels.addAll(models);
				mAdapter.notifyDataSetChanged(mModels);
				mSwipeRefreshLayout.setRefreshComplete();

				if (mModels.size() == 0) {
					mEmptyView.setVisibility(View.VISIBLE);
					mEmptyView.bringToFront();
				} else {
					mEmptyView.setVisibility(View.GONE);
					mListView.bringToFront();
				}
				Toast.makeText(getApplicationContext(), "列表更新成功!", Toast.LENGTH_SHORT).show();
			}

			/**
			 * 比较返回结果
			 * 
			 * @param programModel
			 */
			private void compareResult(ProgramModel programModel) {
				String appID = programModel.appID;
				if (mProgramLoadedMap.containsKey(appID)) {
					if (programModel.editTime.equals(mProgramLoadedMap.get(appID).editTime)) {
						programModel.home_scence = mProgramLoadedMap.get(appID).home_scence;
						programModel.state = new LoadedState(MainActivity.this);
					} else {
						programModel.state = new NormalState(MainActivity.this, mAdapter, mHomeScence, mProgramLoadedMap);
					}
				} else {
					programModel.state = new NormalState(MainActivity.this, mAdapter, mHomeScence, mProgramLoadedMap);
				}
			}

			/**
			 * 重新登录
			 * 
			 * @param resultObject
			 * @throws JSONException
			 */
			private void relogin(JSONObject resultObject) throws JSONException {
				String code = resultObject.getString("code");
				if ("10002".equals(code) || "10003".equals(code)) {
					LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent(Constans.LOGOUT));
					startActivity(new Intent(getApplicationContext(), LoginActivity.class));
					finish();
					Toast.makeText(getApplicationContext(), "请重新登陆!", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onHttpStart() {
				mSwipeRefreshLayout.setRefreshing(true);
			}
		});

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
						model.state = new LoadedState(MainActivity.this);
						model.home_scence = mHomeScence.getHomeSccence(getApplicationContext(), model.appID);
						mProgramLoadedMap.put(model.appID, model);
					} else {
						model.state = new NormalState(MainActivity.this, mAdapter, mHomeScence, mProgramLoadedMap);
					}
				}

				mModels.clear();
				mModels.addAll(mLocalModels);
				mAdapter.notifyDataSetChanged(mModels);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

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
