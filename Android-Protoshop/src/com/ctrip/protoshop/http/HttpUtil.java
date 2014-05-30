package com.ctrip.protoshop.http;

import android.content.Context;
import com.ctrip.protoshop.BuildConfig;
import com.ctrip.protoshop.R;
import com.ctrip.protoshop.constans.Function;

public class HttpUtil {
    public static String getUrlByFunction(Context context, Function function) {
        switch (function) {
        case SIGN_Up:
            if (BuildConfig.DEBUG) {
                return context.getString(R.string.test_sign_up_url);
            } else {
                return context.getString(R.string.sign_up_url);
            }
        case LOGIN:
            if (BuildConfig.DEBUG) {
                return context.getString(R.string.test_login_url);
            } else {
                return context.getString(R.string.login_url);
            }
        case PROGRAM_LIST:
            if (BuildConfig.DEBUG) {
                return context.getString(R.string.test_program_list_url);
            } else {
                return context.getString(R.string.program_list_url);
            }

        case ZIP:
            if (BuildConfig.DEBUG) {
                return context.getString(R.string.test_zip_url);
            } else {
                return context.getString(R.string.zip_url);
            }

        case LUA:
            if (BuildConfig.DEBUG) {
                return context.getString(R.string.test_lua_url);
            } else {
                return context.getString(R.string.lua_url);
            }
        case CHANGE_PSW:
            if (BuildConfig.DEBUG) {
                return context.getString(R.string.test_changepsw_url);
            } else {
                return context.getString(R.string.changepsw_url);
            }
        case FEEDBACK:
            if (BuildConfig.DEBUG) {
                return context.getString(R.string.test_feedback_url);
            } else {
                return context.getString(R.string.feedback_url);
            }
        case USERINFO:
            if (BuildConfig.DEBUG) {
                return context.getString(R.string.test_userinfo_url);
            } else {
                return context.getString(R.string.userinfo_url);
            }
        case UPDATE_USERINFO:
            if(BuildConfig.DEBUG){
                return context.getString(R.string.test_update_user_info_url);
            }else{
                return context.getString(R.string.update_user_info_url);
            }
        case DOMAIN_CALLBACK:
            if(BuildConfig.DEBUG){
                return context.getString(R.string.domain_callback_url);
            }else{
                return context.getString(R.string.test_domain_callback_url);
            }
        case LOGINOUT:
            if(BuildConfig.DEBUG){
                return context.getString(R.string.logout_url);
            }else{
                return context.getString(R.string.test_logout_url);
            }

        default:
            return null;
        }
    }
}
