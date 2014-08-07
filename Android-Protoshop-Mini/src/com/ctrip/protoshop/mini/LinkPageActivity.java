package com.ctrip.protoshop.mini;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.ctrip.protoshop.mini.adapter.PageAdapter;
import com.ctrip.protoshop.mini.constants.Constans;
import com.ctrip.protoshop.mini.model.PageModel;
import com.ctrip.protoshop.mini.model.ProjectModel;

public class LinkPageActivity extends BaseActivity implements OnItemClickListener {

	private GridView mGridView;
	private PageAdapter mAdapter;
	private ImageView[] mAniTypeViews = new ImageView[5];

	private ProjectModel mProjectModel;
	private Intent intent;
	private int mCurPage = 0;
	private String mLinkPage;
	private int mSelectedPage = -1;
	private int mAniType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_link);

		intent = getIntent();
		mCurPage = intent.getIntExtra(Constans.LINK_CUR_PAGE, 0);
		mLinkPage = intent.getStringExtra(Constans.LINK_PAGE_ID);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("链接至");

		mProjectModel = MiniApplication.getInstance().currentProjectModel;
		mGridView = (GridView) findViewById(R.id.link_gridview);

		for (int i = 0; i < mProjectModel.scenes.size(); i++) {
			PageModel model = mProjectModel.scenes.get(i);
			if (model.id.equals(mLinkPage)) {
				model.isLinkPage = true;
				mSelectedPage = i;
			} else {
				model.isLinkPage = false;
			}

		}

		mAdapter = new PageAdapter(this, mProjectModel);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);

		int[] aniTypeIDs = { R.id.none_view, R.id.push_down_view, R.id.push_up_view, R.id.push_left_view, R.id.push_right_view };
		for (int i = 0; i < aniTypeIDs.length; i++) {
			mAniTypeViews[i] = (ImageView) findViewById(aniTypeIDs[i]);
			mAniTypeViews[i].setTag(i);
			mAniTypeViews[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int type = (Integer) v.getTag();
					if (type == mAniType) {
						return;
					}
					mAniTypeViews[mAniType].setSelected(false);
					v.setSelected(true);
					mAniType = type;
				}
			});
		}
		mAniTypeViews[mAniType].setSelected(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			if (mSelectedPage != -1) {
				intent.putExtra(Constans.PAGE_ID, ((PageModel) mAdapter.getItem(mSelectedPage)).id);
				setResult(RESULT_OK, intent);
			}
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == mCurPage) {
			Toast.makeText(getApplicationContext(), "当前页不可选!", Toast.LENGTH_SHORT).show();
		} else {

			if (mSelectedPage == position) {
				return;
			}

			if (mSelectedPage != -1) {
				PageModel model = (PageModel) mAdapter.getItem(mSelectedPage);
				model.isLinkPage = false;
			}
			PageModel model = (PageModel) mAdapter.getItem(position);
			model.isLinkPage = true;
			mSelectedPage = position;

			mAdapter.notifyDataSetChanged();
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
