package com.ctrip.protoshop;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.view.Menu;
import android.view.ViewConfiguration;

public class TestActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		getOverflowMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	private void getOverflowMenu() {

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
