package com.ctrip.protoshop.http;

import android.content.Context;
import com.ctrip.protoshop.R;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;

public class HttpUtil {
    public static String getUrlByFunction(Context context, Function function) {

        String host = Constans.ENVIRONMENT.getHost();

        switch (function) {
        case SIGN_Up:
            return host + context.getString(R.string.sign_up_url);
        case LOGIN:
            return host + context.getString(R.string.login_url);
        case PROGRAM_LIST:
            return host + context.getString(R.string.program_list_url);
        case ZIP:
            return host + context.getString(R.string.zip_url);
        case LUA:
            return host + context.getString(R.string.lua_url);
        case CHANGE_PSW:
            return host + context.getString(R.string.changepsw_url);
        case FEEDBACK:
            return host + context.getString(R.string.feedback_url);
        case USERINFO:
            return host + context.getString(R.string.userinfo_url);
        case UPDATE_USERINFO:
            return host + context.getString(R.string.update_user_info_url);
        case DOMAIN_CALLBACK:
            return context.getString(R.string.domain_callback_url);
        case LOGINOUT:
            return host + context.getString(R.string.logout_url);
        default:
            return null;
        }
    }
}
