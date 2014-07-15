package com.ctrip.protoshop.interfaces.imp;

import android.content.Context;
import android.text.TextUtils;

import com.ctrip.protoshop.interfaces.IHomeScence;
import com.ctrip.protoshop.util.Util;

public class NetHomeScenceImp implements IHomeScence {

	@Override
	public String getHomeSccence(Context context, String appID) {
		String home = Util.readFileContent(Util.getAppFloder(appID), "patch.lua");
		if (!TextUtils.isEmpty(home)) {
			home = home.replace("\n", "");
		}
		return home;
	}

}
