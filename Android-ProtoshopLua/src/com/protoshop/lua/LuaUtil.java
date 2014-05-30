package com.protoshop.lua;

import android.content.Context;
import android.content.Intent;

/**
 * 针对Mini的工具.
 */
public class LuaUtil {
    public static Intent createIntent(Context context, String activityName) {
        Intent intent = null;
        try {
            Class<?> class1 = Class.forName(activityName);
            intent = new Intent(context, class1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return intent;
    }
}
