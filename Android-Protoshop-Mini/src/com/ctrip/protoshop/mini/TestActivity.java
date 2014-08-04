package com.ctrip.protoshop.mini;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class TestActivity extends Activity {

    private AbsoluteLayout mPanelView;
    private ImageView mShowPageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mPanelView = (AbsoluteLayout) findViewById(R.id.edit_panel_view);
        mShowPageView = (ImageView) findViewById(R.id.show_page_backgroud_view);

    }

}
