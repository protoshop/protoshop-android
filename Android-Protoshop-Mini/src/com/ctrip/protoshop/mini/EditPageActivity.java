package com.ctrip.protoshop.mini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.json.JSONException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;
import android.widget.AbsoluteLayout;
import com.alibaba.fastjson.JSON;
import com.ctrip.protoshop.mini.constants.Constans;
import com.ctrip.protoshop.mini.constants.LinkViewState;
import com.ctrip.protoshop.mini.model.ActionModel;
import com.ctrip.protoshop.mini.model.LinkModel;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.ctrip.protoshop.mini.util.ParseJsonUtil;
import com.ctrip.protoshop.mini.util.Util;
import com.ctrip.protoshop.mini.wiget.LinkView;
import com.ctrip.protoshop.mini.wiget.LinkView.OnLinkListener;
import com.ctrip.protoshop.mini.wiget.MiniTitleView;
import com.ctrip.protoshop.mini.wiget.MiniTitleView.OnTitleClikListener;
import com.protoshop.lua.LuaActivity;

@SuppressWarnings("deprecation")
public class EditPageActivity extends Activity {

    private static int LINK_REQUEST_CODE = 311;
    private final static int LINK_CODE = 312;
    private final static int DISPLAY_CODE = 313;

    private AbsoluteLayout mPanelView;
    private ProjectModel mProjectModel;
    private SatelliteMenu mSatelliteMenu;
    private MiniTitleView mTitleView;

    private HashMap<String, LinkView> mLinkViewMap;

    private int mPageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        initData();
        initUI();
        addOnListener();

    }

    private void initData() {
        mPageNum = getIntent().getIntExtra(Constans.PAGE_NUM, 0);
        mProjectModel = MiniApplication.getInstance().currentProjectModel;

        mLinkViewMap = new HashMap<String, LinkView>();
    }

    private void initUI() {

        mPanelView = (AbsoluteLayout) findViewById(R.id.edit_panel_view);
        mPanelView.setBackgroundDrawable(new BitmapDrawable(getResources(), Util.getBitmap(Util
            .getLocalProFile(mProjectModel.appID) + File.separator + mProjectModel.scenes.get(mPageNum).background)));

        mTitleView = (MiniTitleView) findViewById(R.id.edit_page_title);
        mTitleView.updateCenterText(mProjectModel.scenes.get(mPageNum).name);

        mSatelliteMenu = (SatelliteMenu) findViewById(R.id.page_setting_view);
        ArrayList<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
        items.add(new SatelliteMenuItem(DISPLAY_CODE, R.drawable.display));
        items.add(new SatelliteMenuItem(LINK_CODE, R.drawable.link));
        mSatelliteMenu.addItems(items);

        initLinkView();

    }

    private void initLinkView() {

        for (LinkModel linkModel : mProjectModel.scenes.get(mPageNum).elements) {
            LinkView linkView = new LinkView(this, linkModel);

            if (linkModel.actions.size() != 0) {
                linkView.updateLinkViewState(LinkViewState.LINK_NORMAL);
            } else {
                linkView.updateLinkViewState(LinkViewState.UNLINK_NORMAL);
            }

            AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(Integer.parseInt(linkModel.width),
                Integer.parseInt(linkModel.height), Integer.parseInt(linkModel.posX), Integer.parseInt(linkModel.posY));

            linkView.setOnLinkListener(new OnLinkListener() {

                @Override
                public void onLink(LinkModel model) {
                    Intent intent = new Intent(getApplicationContext(), LinkPageActivity.class);

                    if (model.actions.size() > 0) {
                        intent.putExtra(Constans.LINK_PAGE_ID, model.actions.get(0).target);
                    }

                    intent.putExtra(Constans.LINK_CUR_PAGE, mPageNum);
                    intent.putExtra(Constans.LINK_VIEW_ID, model.id);
                    startActivityForResult(intent, LINK_REQUEST_CODE);
                }

                @Override
                public void onCancel(LinkModel linkModel) {
                    mLinkViewMap.remove(linkModel.id);
                    mProjectModel.scenes.get(mPageNum).elements.remove(linkModel);
                }
            });

            mLinkViewMap.put(linkModel.id, linkView);
            mPanelView.addView(linkView, params);
        }
    }

    private void addOnListener() {
        mTitleView.setOnTitleClikListener(new OnTitleClikListener() {

            @Override
            public void onRightClik(View view) {

            }

            @Override
            public void onLeftClik(View view) {
                saveEditResult();
            }

            @Override
            public void onCenterClik(View view) {

            }
        });

        mSatelliteMenu.setOnItemClickedListener(new SateliteClickedListener() {

            @Override
            public void eventOccured(int id) {
                if (id == LINK_CODE) {
                    addLinkView();
                } else if (id == DISPLAY_CODE) {
                    displayResult();
                }
            }
        });
    }

    private void displayResult() {

        try {

            ParseJsonUtil.ParseJson(getApplicationContext(), mProjectModel, JSON.toJSONString(mProjectModel));
            mProjectModel.homeScene = mProjectModel.scenes.get(mPageNum).id;

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

    private void saveEditResult() {
        finish();
    }

    private void addLinkView() {
        Log.e("", "into--[addLinkView]");

        LinkModel model = new LinkModel();
        model.id = Util.getUUID();
        if (mProjectModel.scenes.get(mPageNum).elements == null) {
            mProjectModel.scenes.get(mPageNum).elements = new ArrayList<LinkModel>();
        }
        mProjectModel.scenes.get(mPageNum).elements.add(model);

        LinkView linkView = new LinkView(this, model);

        linkView.setOnLinkListener(new OnLinkListener() {

            @Override
            public void onLink(LinkModel model) {
                Intent intent = new Intent(getApplicationContext(), LinkPageActivity.class);
                intent.putExtra(Constans.LINK_VIEW_ID, model.id);
                intent.putExtra(Constans.LINK_CUR_PAGE, mPageNum);
                startActivityForResult(intent, LINK_REQUEST_CODE);
            }

            @Override
            public void onCancel(LinkModel linkModel) {
                mLinkViewMap.remove(linkModel.id);
                mProjectModel.scenes.get(mPageNum).elements.remove(linkModel);
            }
        });

        mLinkViewMap.put(model.id, linkView);

        mPanelView.addView(linkView);
        Log.e("", "out--[addLinkView]");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LINK_REQUEST_CODE && resultCode == RESULT_OK) {
            String id = data.getStringExtra(Constans.LINK_VIEW_ID);
            mLinkViewMap.get(id).updateLinkViewState(LinkViewState.LINK_NORMAL);
            ActionModel actionModel = new ActionModel();
            actionModel.target = data.getStringExtra(Constans.PAGE_ID);
            if (mLinkViewMap.get(id).getLinkModel().actions == null) {
                mLinkViewMap.get(id).getLinkModel().actions = new ArrayList<ActionModel>();
            }
            mLinkViewMap.get(id).getLinkModel().actions.clear();
            mLinkViewMap.get(id).getLinkModel().actions.add(actionModel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Set<String> keySet = mLinkViewMap.keySet();
        for (String string : keySet) {
            mLinkViewMap.get(string).updateLinkViewState(LinkViewState.UNLINK_NORMAL);
        }
    }

}
