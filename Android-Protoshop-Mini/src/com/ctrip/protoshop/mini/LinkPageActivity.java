package com.ctrip.protoshop.mini;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import com.ctrip.protoshop.mini.adapter.PageAdapter;
import com.ctrip.protoshop.mini.constants.Constans;
import com.ctrip.protoshop.mini.model.PageModel;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.ctrip.protoshop.mini.util.ProtoshopLog;
import com.ctrip.protoshop.mini.wiget.MiniTitleView;
import com.ctrip.protoshop.mini.wiget.MiniTitleView.OnTitleClikListener;

public class LinkPageActivity extends Activity implements OnItemClickListener {

    private ProjectModel mProjectModel;
    private GridView mGridView;
    private PageAdapter mAdapter;
    private MiniTitleView mTitleView;

    private Intent intent;

    private int mCurPage = 0;
    private String mLinkPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_link);

        intent = getIntent();

        mCurPage = intent.getIntExtra(Constans.LINK_CUR_PAGE, 0);
        mLinkPage = intent.getStringExtra(Constans.LINK_PAGE_ID);
        ProtoshopLog.e("LINK:" + mLinkPage);

        mProjectModel = MiniApplication.getInstance().currentProjectModel;
        mGridView = (GridView) findViewById(R.id.link_gridview);

        for (PageModel model : mProjectModel.scenes) {
            ProtoshopLog.e(model.id);
            if (model.id.equals(mLinkPage)) {
                model.isLinkPage = true;
            } else {
                model.isLinkPage = false;
            }
        }

        mAdapter = new PageAdapter(this, mProjectModel);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        
        mTitleView=(MiniTitleView) findViewById(R.id.page_link_title);
        mTitleView.setOnTitleClikListener(new OnTitleClikListener() {
            
            @Override
            public void onRightClik(View view) {
            }
            
            @Override
            public void onLeftClik(View view) {
                finish();
            }
            
            @Override
            public void onCenterClik(View view) {
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mCurPage) {
            Toast.makeText(getApplicationContext(), "当前页不可选!", Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra(Constans.PAGE_ID, ((PageModel) mAdapter.getItem(position)).id);
            ProtoshopLog.e("SelectID:" + ((PageModel) mAdapter.getItem(position)).id);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (PageModel model : mProjectModel.scenes) {
            model.isLinkPage = false;
        }
        mAdapter.notifyDataSetChanged();
    }
}