package com.ctrip.protoshop.interfaces.imp;

import android.view.View;
import android.widget.Toast;
import com.ctrip.protoshop.ProtoshopApplication;
import com.ctrip.protoshop.adapter.ProgramAdapter.ViewHolder;
import com.ctrip.protoshop.interfaces.IProgramState;
import com.ctrip.protoshop.model.ProgramModel;

public class LoadingState implements IProgramState {

    public LoadingState() {
    }

    @Override
    public void onItemClick(ProgramModel model) {
        Toast.makeText(ProtoshopApplication.getInstance(), "正在下载,请稍等!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showView(ViewHolder holder) {
        holder.arrowView.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.VISIBLE);
    }

}
