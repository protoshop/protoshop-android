package com.ctrip.protoshop.mini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.ctrip.protoshop.mini.adapter.PageAdapter;
import com.ctrip.protoshop.mini.constants.Constans;
import com.ctrip.protoshop.mini.model.PageModel;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.ctrip.protoshop.mini.util.ParseJsonUtil;
import com.ctrip.protoshop.mini.util.Util;
import com.ctrip.protoshop.mini.wiget.DragGridView;
import com.ctrip.protoshop.mini.wiget.DragGridView.OnChanageListener;
import com.ctrip.protoshop.mini.wiget.MiniTitleView;
import com.ctrip.protoshop.mini.wiget.MiniTitleView.OnTitleClikListener;
import com.protoshop.lua.LuaActivity;

public class EditProjectActivity extends Activity {
    private final static int FROM_GALLERY = 211;
    private final static int FROM_CAMARE = 212;
    private final static int CROP_PIC = 213;

    private final static int TAKE_PIC = 214;
    private final static int DISPLASY = 215;

    private MiniTitleView mTitleView;
    private SatelliteMenu mSettingView;
    private DragGridView mGridView;
    private PageAdapter mAdapter;

    private ProjectModel mProjectModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);
        mProjectModel = MiniApplication.getInstance().currentProjectModel;
        initUI();
    }

    private void initUI() {
        mTitleView = (MiniTitleView) findViewById(R.id.edit_project_title);
        mTitleView.updateCenterText(mProjectModel.appName);
        mSettingView = (SatelliteMenu) findViewById(R.id.project_setting_view);
        ArrayList<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
        items.add(new SatelliteMenuItem(DISPLASY, R.drawable.display));
        items.add(new SatelliteMenuItem(TAKE_PIC, R.drawable.camera));
        mSettingView.addItems(items);

        mGridView = (DragGridView) findViewById(R.id.project_page_gridView);
        if (mProjectModel.scenes == null) {
            mProjectModel.scenes = new ArrayList<PageModel>();
        }
        mAdapter = new PageAdapter(this, mProjectModel);
        mGridView.setAdapter(mAdapter);

        addOnListener();

    }

    /**
     * 注册监听器
     */
    private void addOnListener() {
        //返回
        mTitleView.setOnTitleClikListener(new OnTitleClikListener() {

            @Override
            public void onRightClik(View view) {

            }

            @Override
            public void onLeftClik(View view) {
                finishEditor();
            }

            @Override
            public void onCenterClik(View view) {
            }
        });

        //左下角菜单
        mSettingView.setOnItemClickedListener(new SateliteClickedListener() {

            @Override
            public void eventOccured(int id) {
                if (id == DISPLASY) {
                    displayProject();
                } else if (id == TAKE_PIC) {
                    selectPic();
                }
            }
        });

        //进入页面编辑
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MiniApplication.getInstance().currentProjectModel = mProjectModel;
                Intent intent = new Intent(getApplicationContext(), EditPageActivity.class);
                intent.putExtra(Constans.PAGE_NUM, position);
                startActivity(intent);

            }

        });
        //长按
        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                for (PageModel model : mProjectModel.scenes) {
                    model.isEditModel = true;
                }
                mGridView.setDragModel(true);
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
        //拖拽
        mGridView.setOnChangeListener(new OnChanageListener() {

            @Override
            public void onChange(int form, int to) {

                PageModel fromModel = mProjectModel.scenes.get(form);
                PageModel toModel = mProjectModel.scenes.get(to);

                mProjectModel.scenes.set(form, toModel);
                mProjectModel.scenes.set(to, fromModel);

                mAdapter.notifyDataSetChanged();
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
    private void selectPic() {
        String[] itemsStr = getResources().getStringArray(R.array.select_pic_items);

        new AlertDialog.Builder(this).setTitle(R.string.select_pic_text)
            .setItems(itemsStr, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        gotoGallery();
                        dialog.dismiss();
                        break;
                    case 1:
                        dialog.dismiss();
                        takePic();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;

                    default:
                        break;
                    }
                }

            }).show();
    }

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

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//设置输出格式
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
            model.background = Util.saveBitmap(Util.getLocalProFile(this,mProjectModel.appID), bitmap);
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
        mGridView.setDragModel(false);
    }

    private void finishEditor() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mGridView.isDragModel()) {
                editFinish();
            } else {
                finishEditor();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
