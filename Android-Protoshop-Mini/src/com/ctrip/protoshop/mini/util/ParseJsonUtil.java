package com.ctrip.protoshop.mini.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import com.ctrip.protoshop.mini.R;
import com.ctrip.protoshop.mini.model.ProjectModel;

public class ParseJsonUtil {
    public static void ParseJson(Context context, ProjectModel model, String jsonStr) throws JSONException, IOException {

        String appID = model.appID;
        Pattern pattern;
        Matcher matcher;

        JSONObject jsonObject = new JSONObject(jsonStr);
        appID = jsonObject.getString("appID");

        String scenceTemplate = Util.readStream(context.getAssets().open(("template/android_scenes_template.lua")));
        pattern = Pattern.compile("#appID");
        matcher = pattern.matcher(scenceTemplate);
        scenceTemplate = matcher.replaceAll(appID);
        
        float top=context.getResources().getDimension(R.dimen._30);
        pattern = Pattern.compile("#top");
        matcher = pattern.matcher(scenceTemplate);
        scenceTemplate = matcher.replaceAll(String.valueOf(top));

        String elementTemplate = Util.readStream(context.getAssets().open(("template/android_addView_template.lua")));
        pattern = Pattern.compile("#appID");
        matcher = pattern.matcher(elementTemplate);
        elementTemplate = matcher.replaceAll(appID);

        String actionTemplate = Util.readStream(context.getAssets().open(("template/android_setAction_template.lua")));
        pattern = Pattern.compile("#appID");
        matcher = pattern.matcher(actionTemplate);
        actionTemplate = matcher.replaceAll(appID);

        JSONArray scenceArray = jsonObject.getJSONArray("scenes");
        for (int i = 0; i < scenceArray.length(); i++) {
            String scenceStr = new String(scenceTemplate);
            JSONObject scenceObject = scenceArray.getJSONObject(i);
            String id = scenceObject.getString("id");
            String order = scenceObject.getString("order");
            if (order.equals("0")) {
                model.homeScene = id;
            }
            String backgroud = scenceObject.getString("background");
            backgroud = backgroud.substring(backgroud.lastIndexOf("/") + 1);

            pattern = Pattern.compile("#backgroud");
            matcher = pattern.matcher(scenceStr);
            scenceStr = matcher.replaceAll(backgroud);

            JSONArray elementArray = scenceObject.getJSONArray("elements");
            StringBuffer elementBuffer = new StringBuffer();
            for (int j = 0; j < elementArray.length(); j++) {
                String elementStr = new String(elementTemplate);
                JSONObject elementObject = elementArray.getJSONObject(j);

                String posY = elementObject.getString("posY");
                String posX = elementObject.getString("posX");
                String width = elementObject.getString("width");
                String height = elementObject.getString("height");

                pattern = Pattern.compile("#width");
                matcher = pattern.matcher(elementStr);
                elementStr = matcher.replaceAll(width);

                pattern = Pattern.compile("#height");
                matcher = pattern.matcher(elementStr);
                elementStr = matcher.replaceAll(height);

                pattern = Pattern.compile("#x");
                matcher = pattern.matcher(elementStr);
                elementStr = matcher.replaceAll(posX);

                pattern = Pattern.compile("#y");
                matcher = pattern.matcher(elementStr);
                elementStr = matcher.replaceAll(posY);

                StringBuffer actionBuffer = new StringBuffer();
                JSONArray actionArray = elementObject.getJSONArray("actions");
                for (int k = 0; k < actionArray.length(); k++) {
                    String actionStr = new String(actionTemplate);
                    JSONObject actionObject = actionArray.getJSONObject(k);
                    String target = actionObject.getString("target");
                    pattern = Pattern.compile("#target");
                    matcher = pattern.matcher(actionStr);
                    actionStr = matcher.replaceAll(target);

                    pattern = Pattern.compile("#setAction");
                    matcher = pattern.matcher(elementStr);
                    elementStr = matcher.replaceAll(actionStr.toString());
                    actionBuffer.append(elementStr);
                }

                //如果action为空，去除#setAction占位符
                pattern = Pattern.compile("#setAction");
                matcher = pattern.matcher(elementStr);
                elementStr = matcher.replaceAll("");
                actionBuffer.append(elementStr);

                elementBuffer.append(elementStr);
            }

            pattern = Pattern.compile("#addView");
            matcher = pattern.matcher(scenceStr);
            scenceStr = matcher.replaceAll(elementBuffer.toString());

            pattern = Pattern.compile("#");
            matcher = pattern.matcher(scenceStr);
            scenceStr = matcher.replaceAll("\\$");
            Util.saveScence(appID, id + ".lua", scenceStr);
        }

    }
}
