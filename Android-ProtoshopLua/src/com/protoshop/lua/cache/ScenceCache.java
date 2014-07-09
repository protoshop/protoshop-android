package com.protoshop.lua.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.View;

/**
 * 运行过的Activity ID。
 */
public class ScenceCache {
	private static ScenceCache instance = new ScenceCache();

	public List<String> scences = new ArrayList<String>();
	public HashMap<String, List<View>> hotMap = new HashMap<String, List<View>>();

	public static ScenceCache getInstance() {
		return instance;
	}
}
