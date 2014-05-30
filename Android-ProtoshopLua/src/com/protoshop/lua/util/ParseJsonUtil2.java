package com.protoshop.lua.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import com.protoshop.lua.LuaConfig;

public class ParseJsonUtil2 {
    public static String ParseJson(Context context, String appID) throws JSONException, IOException {

        Pattern pattern;
        Matcher matcher;

        String home_scence = null;

        String floder = ((LuaConfig) context.getApplicationContext()).getFloderPath();

        File jsonFile = new File(floder, appID + File.separator + "projectJson.json");
        JSONObject jsonObject = new JSONObject(Util.readStream(new FileInputStream(jsonFile)));
        appID = jsonObject.getString("appID");

        //加载模板
        String scenceTemplate = Util.readStream(context.getAssets().open(
            ("template/android_template/android_lua_template.lua")));
        String createViewTemplate = Util.readStream(context.getAssets().open(
            "template/android_template/android_createView_template.lua"));
        String setAttrTemplate = Util.readStream(context.getAssets().open(
            "template/android_template/android_setAttr_template.lua"));
        String setActionTemplate = Util.readStream(context.getAssets().open(
            "template/android_template/android_setAction_template.lua"));

        //替换模板中的APPID
        pattern = Pattern.compile("#appID");
        matcher = pattern.matcher(scenceTemplate);
        scenceTemplate = matcher.replaceAll(appID);

        //开始解析JSON

        //获取JSON中的Scene
        JSONArray scenceArray = jsonObject.getJSONArray("scences");
        for (int i = 0; i < scenceArray.length(); i++) {
            String scenceStr = new String(scenceTemplate);
            JSONObject scenceObject = scenceArray.getJSONObject(i);
            String scenceId = scenceObject.getString("id");
            String order = scenceObject.getString("order");
            //按照之前的约定，只要order为0的Scene就是默认的开始页面。
            //建议：Python解析的时候，需要把开始ID存储到一个文件中。Android从文件中读取。文件名称，需要我们约定一下。
            if (order.equals("0")) {
                home_scence = scenceId;
            }
            //获取Scene页面的背景图片
            String backgroud = scenceObject.getString("background");
            //截取背景图片名称.
            backgroud = backgroud.substring(backgroud.lastIndexOf("/") + 1);

            //替换模板中的background
            pattern = Pattern.compile("#backgroud");
            matcher = pattern.matcher(scenceStr);
            scenceStr = matcher.replaceAll(backgroud);

            //替换Scene名称
            pattern = Pattern.compile("#scence");
            matcher = pattern.matcher(scenceStr);
            scenceStr = matcher.replaceAll("id" + scenceId);

            //递归解析elements元素
            String luaElementStr = parseElements("id" + scenceId, scenceObject, createViewTemplate, setAttrTemplate,
                setActionTemplate);

            //替换模板中的#addView，到此整个解析完成。
            pattern = Pattern.compile("#addView");
            matcher = pattern.matcher(scenceStr);
            scenceStr = matcher.replaceAll(luaElementStr);

            pattern = Pattern.compile("#");
            matcher = pattern.matcher(scenceStr);
            scenceStr = matcher.replaceAll("\\$");

            //存储生成的Lua脚本
            Util.saveScence(context, appID, scenceId + ".lua", scenceStr);
        }
        return home_scence;

    }

    private static String parseElements(String parentID, JSONObject parentElementObject, String createViewTemplate,
                                        String setAttrTemplate, String setActionTemplate) throws JSONException {
        //解析JSON中的element
        StringBuffer elementBuffer = new StringBuffer();
        
        JSONArray elementArray = parentElementObject.getJSONArray("elements");
        for (int j = 0; j < elementArray.length(); j++) {
            JSONObject elementObject = elementArray.getJSONObject(j);
            //获取element中的type(例如：Button，ImageView等)
            String type = elementObject.getString("type");

            String viewId = "id" + String.valueOf(System.currentTimeMillis());

            //插入command中的createView方法。替换参数#type
            String createViewStr = new String(createViewTemplate);
            Pattern pattern = Pattern.compile("#type");
            Matcher matcher = pattern.matcher(createViewStr);
            createViewStr = matcher.replaceAll(type);

            pattern = Pattern.compile("#view");
            matcher = pattern.matcher(createViewStr);
            createViewStr = matcher.replaceAll(viewId);

            elementBuffer.append(createViewStr + "\n");

            //插入command中的setAtrr方法。替换参数#attrjson(控件属性的JSON字符串)
            String setAttrStr = new String(setAttrTemplate);
            pattern = Pattern.compile("#attrJson");
            matcher = pattern.matcher(setAttrStr);
            setAttrStr = matcher.replaceAll(elementObject.toString());

            pattern = Pattern.compile("#view");
            matcher = pattern.matcher(setAttrStr);
            setAttrStr = matcher.replaceAll(viewId);

            pattern = Pattern.compile("#parent");
            matcher = pattern.matcher(setAttrStr);
            setAttrStr = matcher.replaceAll(parentID);

            elementBuffer.append(setAttrStr + "\n");

            if (elementObject.has("elements")) {
//                if (type.equalsIgnoreCase("ScrollView")) {
//                    elementBuffer.append(parseElements(viewId, elementObject.getJSONArray("elements").getJSONObject(0),
//                        createViewTemplate, setAttrTemplate, setActionTemplate));
//                } else {
                    elementBuffer.append(parseElements(viewId, elementObject, createViewTemplate, setAttrTemplate,
                        setActionTemplate));
                //}
            }

            //插入command中的setAction方法。替换参数#acitonJson
            if (elementObject.has("actions")) {
                JSONArray actionArray = elementObject.getJSONArray("actions");
                for (int k = 0; k < actionArray.length(); k++) {
                    JSONObject actionObject = actionArray.getJSONObject(k);

                    String setActionStr = new String(setActionTemplate);

                    pattern = Pattern.compile("#actionJson");
                    matcher = pattern.matcher(setActionStr);
                    setActionStr = matcher.replaceAll(actionObject.toString());

                    pattern = Pattern.compile("#view");
                    matcher = pattern.matcher(setActionStr);
                    setActionStr = matcher.replaceAll(viewId);

                  elementBuffer.append(setActionStr + "\n");
                }
            }
        }
        return elementBuffer.toString();
    }
}
