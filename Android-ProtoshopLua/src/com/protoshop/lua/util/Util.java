package com.protoshop.lua.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import android.content.Context;
import com.protoshop.lua.LuaConfig;

public class Util {

    public static String getLusStr(Context context, String appID, String scence) {
        FileInputStream inputStream = null;
        try {
            LuaLog.e("LUA:" + ((LuaConfig) context.getApplicationContext()).getFloderPath());
            File luaFloder = new File(((LuaConfig) context.getApplicationContext()).getFloderPath(), appID);
            File luaFile = new File(luaFloder, scence);
            inputStream = new FileInputStream(luaFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return readStream(inputStream);
    }

    public static void saveScence(Context context, String appID, String id, String content) {
        try {
            LuaLog.e("SaveLua:" + ((LuaConfig) context.getApplicationContext()).getFloderPath());
            File file = new File(((LuaConfig) context.getApplicationContext()).getFloderPath(), appID);
            if (!file.exists()) {
                file.mkdirs();
            }

            File contentFile = new File(file, id);
            if (!contentFile.exists()) {
                contentFile.createNewFile();
            }

            FileWriter writer = new FileWriter(contentFile);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized String readStream(InputStream inputStream) {

        StringBuffer buffer = new StringBuffer();
        try {
            String str;
            InputStreamReader streamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader reader = new BufferedReader(streamReader);
            try {
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                    buffer.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
