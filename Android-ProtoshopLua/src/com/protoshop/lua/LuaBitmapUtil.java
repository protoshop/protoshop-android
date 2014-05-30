package com.protoshop.lua;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class LuaBitmapUtil {
    public static Bitmap getBitmap(String path) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, opt);
        return bitmap;
    }

    public static String getResourcesPath(Context context,String appName, String resources) {
        File file = new File(LuaFileUtil.getProgramFloder(context).getAbsolutePath() + File.separator + appName, resources);
        return file.getAbsolutePath();
    }

    public static BitmapDrawable getBitmapDrawable(Context context,String appName, String resources) {
        @SuppressWarnings("deprecation")
        BitmapDrawable drawable = new BitmapDrawable(getBitmap(getResourcesPath(context,appName, resources)));
        return drawable;
    }

    public static void recycleBitmap(BitmapDrawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
            System.gc();
        }
    }
}
