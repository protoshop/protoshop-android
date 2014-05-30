package com.protoshop.lua;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.content.Context;

public class LuaFileUtil {

    public static File getProgramFloder(Context context) {
        File floder = new File(((LuaConfig)context.getApplicationContext()).getFloderPath());
        if (!floder.exists()) {
            floder.mkdirs();
        }
        return floder;
    }

    public static File getResources(Context context,String appName, String resource) {
        File floderFile = getProgramFloder(context);
        String path = floderFile.getAbsolutePath() + File.separator + appName + File.separator + resource;
        File file = new File(path);
        return file;
    }

    public static String getSeparator() {
        return File.separator;
    }

    public static String readFile(File file) {

        String strContent = "", strLine = "";
        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader read = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(read);
            while ((strLine = reader.readLine()) != null) {
                strContent += strLine;
            }
            reader.close();
            read.close();
        } catch (IOException e) {
            String strErrMsg = e.getMessage();
            System.out.println("Read file " + " failed! Error message:" + strErrMsg);
        }
        return strContent;
    }

}
