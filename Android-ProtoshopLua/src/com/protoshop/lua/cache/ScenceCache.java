package com.protoshop.lua.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行过的Activity ID。
 */
public class ScenceCache {
    private static ScenceCache instance = new ScenceCache();

    public List<String> scences = new ArrayList<String>();

    public static ScenceCache getInstance() {
        return instance;
    }
}
