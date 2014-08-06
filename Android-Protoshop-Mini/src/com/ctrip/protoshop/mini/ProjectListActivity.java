package com.ctrip.protoshop.mini;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ctrip.protoshop.mini.adapter.ProjectAdapter;
import com.ctrip.protoshop.mini.constants.Constans;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.ctrip.protoshop.mini.util.Util;

public class ProjectListActivity extends BaseActivity {

	private static int EDITE_CODE = 111;

	private static int STATE_ADD_ID = 112;
	private static int STATE_ICON_ID = 113;

	private ListView mListView;
	private BaseAdapter mAdapter;
	private List<ProjectModel> mProjectModels;

	private SatelliteMenu mSatelliteMenu;
	private View mAddProjectView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);

		initUI();
		loadLocalProject();
	}

	/**
	 * 
	 * 初始化控件.
	 */
	private void initUI() {

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("创意列表");

		mAddProjectView = findViewById(R.id.create_new_project_view);
		mListView = (ListView) findViewById(R.id.project_list_listView);

		mSatelliteMenu = (SatelliteMenu) findViewById(R.id.project_setting_view);
		ArrayList<SatelliteMenuItem> menuItems = new ArrayList<SatelliteMenuItem>();
		menuItems.add(new SatelliteMenuItem(STATE_ICON_ID, R.drawable.ic_launcher));
		menuItems.add(new SatelliteMenuItem(STATE_ADD_ID, R.drawable.add));
		mSatelliteMenu.addItems(menuItems);

		addOnListener();
		LocalBroadcastManager.getInstance(this).registerReceiver(deleteItemReceiver, new IntentFilter("delete item"));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	BroadcastReceiver deleteItemReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int position = intent.getIntExtra("position", -1);
			dealDeleteProgject(mProjectModels.get(position));
		}
	};

	/**
	 * 
	 * 为控件添加点击等事件.
	 */
	private void addOnListener() {
		// 添加新的工程
		mAddProjectView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addProject();
			}
		});

		// 进入编辑页面
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mProjectModels.get(position).isDelete) {
					mProjectModels.get(position).isDelete = false;
					mAdapter.notifyDataSetChanged();
					return;
				}
				MiniApplication.getInstance().currentProjectModel = mProjectModels.get(position);
				startActivityForResult(new Intent(getApplicationContext(), EditProjectActivity.class), EDITE_CODE);
			}
		});

		// 长按删除工程
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// showDeleteDialog(mProjectModels.get(position));
				if (!mProjectModels.get(position).isDelete) {
					mProjectModels.get(position).isDelete = true;
					mAdapter.notifyDataSetChanged();
					return true;
				}
				return false;
			}
		});

		// 菜单点击
		mSatelliteMenu.setOnItemClickedListener(new SateliteClickedListener() {

			@Override
			public void eventOccured(int id) {
				responseOnStateItemListener(id);
			}
		});
	}

	/**
	 * 
	 * 菜单点击事件.
	 * 
	 * @param id
	 *            菜单项ID
	 */
	protected void responseOnStateItemListener(int id) {
		if (id == STATE_ADD_ID) {
			addProject();
		} else if (id == STATE_ICON_ID) {
			Toast.makeText(this, "Hello,This is a Icon!", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 
	 * 加载已经创建的项目.
	 */
	private void loadLocalProject() {

		mProjectModels = new ArrayList<ProjectModel>();
		mAdapter = new ProjectAdapter(this, mProjectModels);
		mListView.setAdapter(mAdapter);

		String projectInfosStr = Util.readFileContent(Util.getLocalRootFile(this), Constans.PROJECT_LIST);
		if (!TextUtils.isEmpty(projectInfosStr)) {
			mProjectModels.addAll(JSON.parseArray(projectInfosStr, ProjectModel.class));
			mAdapter.notifyDataSetChanged();
		}

		if (mProjectModels.size() == 0) {
			mSatelliteMenu.setVisibility(View.GONE);
			mAddProjectView.setVisibility(View.VISIBLE);
		} else {
			mAddProjectView.setVisibility(View.GONE);
			mSatelliteMenu.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 * 展示删除项目对话框.
	 * 
	 * @param projectModel
	 */
	private void showDeleteDialog(final ProjectModel projectModel) {
		new AlertDialog.Builder(this).setTitle(R.string.tip_text).setIcon(android.R.drawable.ic_dialog_alert).setMessage(getString(R.string.delete_tip_text) + projectModel.appName + "?")
				.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dealDeleteProgject(projectModel);
					}

				}).setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 
	 * 删除项目.
	 * 
	 * @param projectModel
	 */
	private void dealDeleteProgject(ProjectModel projectModel) {
		mProjectModels.remove(projectModel);
		mAdapter.notifyDataSetChanged();
		saveProjectInfo();
		Util.deleteFile(Util.getLocalProFile(this, projectModel.appID));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDITE_CODE && resultCode == RESULT_OK) {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 删除项目
	 * 
	 */
	private void addProject() {
		View view = View.inflate(getApplicationContext(), R.layout.edite_layout, null);
		final EditText editText = (EditText) view.findViewById(R.id.editText1);

		new AlertDialog.Builder(this).setTitle(R.string.create_project_title).setMessage(R.string.create_project_msg).setView(view)
				.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Toast.makeText(getApplicationContext(), editText.getText().toString(), Toast.LENGTH_LONG).show();
						ProjectModel model = new ProjectModel();

						model.appID = Util.getUUID();
						model.appName = editText.getText().toString();

						mProjectModels.add(model);
						mAddProjectView.setVisibility(View.GONE);
						mAdapter.notifyDataSetChanged();
						mSatelliteMenu.setVisibility(View.VISIBLE);
					}
				}).setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 
	 * 保存工程信息.
	 */
	private void saveProjectInfo() {
		if (mProjectModels != null) {
			String jsonStr = JSON.toJSONString(mProjectModels);
			Util.saveFile(Util.getLocalRootFile(this).getAbsolutePath(), Constans.PROJECT_LIST, jsonStr);
		}
	}

	boolean isFirst = true;

	@Override
	protected void onDestroy() {
		saveProjectInfo();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(deleteItemReceiver);
		super.onDestroy();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// if (isFirst) {
	// Toast.makeText(getApplicationContext(), R.string.exit_text, Toast.LENGTH_SHORT).show();
	// saveProjectInfo();
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// Thread.sleep(1000);
	// isFirst = true;
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }).start();
	// isFirst = false;
	// return true;
	// }
	// }
	// return super.onKeyDown(keyCode, event);
	// }

}
