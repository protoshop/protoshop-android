package com.ctrip.protoshop.interfaces.imp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.ctrip.protoshop.adapter.ProgramAdapter.ViewHolder;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.interfaces.IProgramState;
import com.ctrip.protoshop.model.ProgramModel;
import com.protoshop.lua.LuaActivity;
import com.protoshop.lua.cache.ScenceCache;

public class LoadedState implements IProgramState {
    private Context mContext;

    public LoadedState(Context context) {
        mContext = context;
    }

    @Override
    public void onItemClick(ProgramModel model) {
        Intent intent = new Intent(mContext, LuaActivity.class);
        intent.putExtra(Constans.SCENE, model.home_scence);
        intent.putExtra(Constans.APPID, model.appID);

        ScenceCache.getInstance().scences.add(model.home_scence);
        mContext.startActivity(intent);

    }

    @Override
    public void showView(ViewHolder holder) {
        holder.arrowView.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.GONE);
    }

}
