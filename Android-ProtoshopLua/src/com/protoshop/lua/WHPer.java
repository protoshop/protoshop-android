package com.protoshop.lua;

import android.content.Context;
import android.util.DisplayMetrics;

public class WHPer {
    public float wPer;
    public float hPer;

    public WHPer(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        wPer = dm.widthPixels / 400f;
        hPer = dm.heightPixels / 640f;
    }
}
