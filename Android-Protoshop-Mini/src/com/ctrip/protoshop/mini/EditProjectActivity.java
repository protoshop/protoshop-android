package com.ctrip.protoshop.mini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ctrip.protoshop.mini.adapter.PageAdapter;
import com.ctrip.protoshop.mini.constants.Constans;
import com.ctrip.protoshop.mini.model.PageModel;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.ctrip.protoshop.mini.util.ParseJsonUtil;
import com.ctrip.protoshop.mini.util.Util;
import com.protoshop.lua.LuaActivity;

public class EditProjectActivity extends BaseActivity {
	private final static int FROM_GALLERY = 211;
	private final static int FROM_CAMARE = 212;
	private final static int CROP_PIC = 213;

	private final static int DISPLASY = 214;
	private final static int PHOTO = 215;
	private final static int CAMARA = 216;
	private final static int NOTE = 217;

	private SatelliteMenu mSettingView;
	private GridView mGridView;
	private PageAdapter mAdapter;

	private ProjectModel mProjectModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_project);
		mProjectModel = MiniCache.getInstance().currentProjectModel;
		initUI();
	}

	private void initUI() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(mProjectModel.appName);

		mSettingView = (SatelliteMenu) findViewById(R.id.project_setting_view);
		mSettingView.setMainImage(R.drawable.icon_add);
		mSettingView.setCloseItemsOnClick(true);
		mSettingView.setExpandDuration(500);
		mSettingView.setSatelliteDistance(getResources().getDimensionPixelSize(R.dimen._100));
		mSettingView.setTotalSpacingDegree(90);

		ArrayList<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
		items.add(new SatelliteMenuItem(DISPLASY, R.drawable.icon_play));
		items.add(new SatelliteMenuItem(NOTE, R.drawable.icon_note));
		items.add(new SatelliteMenuItem(CAMARA, R.drawable.icon_camera));
		items.add(new SatelliteMenuItem(PHOTO, R.drawable.icon_photo));
		mSettingView.addItems(items);

		mGridView = (GridView) findViewById(R.id.project_page_gridView);
		if (mProjectModel.scenes == null) {
			mProjectModel.scenes = new ArrayList<PageModel>();
		}
		mAdapter = new PageAdapter(this, mProjectModel);
		mGridView.setAdapter(mAdapter);

		addOnListener();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			judgeBack();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 注册监听器
	 */
	private void addOnListener() {

		// 左下角菜单
		mSettingView.setOnItemClickedListener(new SateliteClickedListener() {

			@Override
			public void eventOccured(int id) {
				switch (id) {
				case DISPLASY:
					displayProject();
					break;
				case CAMARA:
					takePic();
					break;
				case PHOTO:
					gotoGallery();
					break;
				case NOTE:
					editPages();
					break;
				default:
					break;
				}

			}
		});

		// 进入页面编辑
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				MiniCache.getInstance().currentProjectModel = mProjectModel;
				Intent intent = new Intent(getApplicationContext(), EditPageActivity.class);
				intent.putExtra(Constans.PAGE_NUM, position);
				startActivity(intent);

			}

		});
		// 长按
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				editPages();
				return true;
			}
		});
	}

	/**
	 * 预览
	 */
	private void displayProject() {

		try {

			ParseJsonUtil.ParseJson(getApplicationContext(), mProjectModel, JSON.toJSONString(mProjectModel));

			Intent intent = new Intent(getApplicationContext(), LuaActivity.class);
			intent.putExtra(Constans.APPID, mProjectModel.appID);
			intent.putExtra(Constans.SCENE, mProjectModel.homeScene);
			startActivity(intent);

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 选择图片.
	 */
//	private void selectPic() {
//		String[] itemsStr = getResources().getStringArray(R.array.select_pic_items);
//
//		new AlertDialog.Builder(this).setTitle(R.string.select_pic_text).setItems(itemsStr, new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				switch (which) {
//				case 0:
//					gotoGallery();
//					dialog.dismiss();
//					break;
//				case 1:
//					dialog.dismiss();
//					takePic();
//					break;
//				case 2:
//					dialog.dismiss();
//					break;
//
//				default:
//					break;
//				}
//			}
//
//		}).show();
//	}

	/**
	 * 
	 * 图库获取图片.
	 */
	private void gotoGallery() {
		Toast.makeText(getApplicationContext(), "相册", Toast.LENGTH_SHORT).show();
		File tempFile = new File(Util.getRootFile(this), Constans.TEMP_PIC);
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		intent.putExtra("crop", "true");// 截取图片
		intent.putExtra("aspectX", 3);
		intent.putExtra("aspectY", 5);
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("output", Uri.fromFile(tempFile));
		startActivityForResult(intent, FROM_GALLERY);
	}

	/**
	 * 
	 * 调用照相机，拍照
	 */
	private void takePic() {
		Toast.makeText(getApplicationContext(), "照相", Toast.LENGTH_SHORT).show();
		File tempFile = new File(Util.getRootFile(this), Constans.TEMP_PIC);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));

		startActivityForResult(intent, FROM_CAMARE);
	}

	/**
	 * 裁剪图片
	 */
	private void cropPic() {
		File tempFile = new File(Util.getRootFile(this), Constans.TEMP_PIC);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(tempFile), "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 3);
		intent.putExtra("aspectY", 5);

		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 设置输出格式
		intent.putExtra("output", Uri.fromFile(tempFile));
		startActivityForResult(intent, CROP_PIC);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == FROM_GALLERY || requestCode == CROP_PIC) && resultCode == RESULT_OK) {
			PageModel model = new PageModel();
			model.id = Util.getUUID();
			model.name = "页面" + (mProjectModel.scenes.size() + 1);
			Bitmap bitmap = Util.getBitmap(Util.getRootFile(this).getAbsolutePath() + File.separator + Constans.TEMP_PIC);
			model.background = Util.saveBitmap(Util.getLocalProFile(this, mProjectModel.appID), bitmap);
			model.order = String.valueOf(mProjectModel.scenes.size());
			mProjectModel.scenes.add(model);
			mAdapter.notifyDataSetChanged();
		} else if (requestCode == FROM_CAMARE && resultCode == RESULT_OK) {
			cropPic();
		}
	}

	private void editFinish() {
		for (PageModel model : mProjectModel.scenes) {
			model.isEditModel = false;
		}
		mAdapter.notifyDataSetChanged();
	}

	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			judgeBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 
	 */
	private void judgeBack() {
		if (mProjectModel.scenes.get(0).isEditModel) {
			editFinish();
		} else {
			setResult(RESULT_OK);
			finish();
		}
	}

	/**
	 * 
	 */
	private void editPages() {
		for (PageModel model : mProjectModel.scenes) {
			model.isEditModel = true;
		}
		mAdapter.notifyDataSetChanged();
	}

}
