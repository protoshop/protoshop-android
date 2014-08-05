package com.ctrip.protoshop.mini;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends ActionBarActivity {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.one, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent();
			intent.setClassName(this, "com.ctrip.protoshop.SettingActivity");
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
