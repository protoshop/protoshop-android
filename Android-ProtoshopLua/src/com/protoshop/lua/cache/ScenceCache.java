package com.protoshop.lua.cache;

import java.util.ArrayList;
import java.util.List;

public class ScenceCache {
    private static ScenceCache instance = new ScenceCache();

    public List<String> scences = new ArrayList<String>();

    public static ScenceCache getInstance() {
        return instance;
    }
}
