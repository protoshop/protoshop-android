package com.ctrip.protoshop.interfaces;

import com.ctrip.protoshop.adapter.ProgramAdapter.ViewHolder;
import com.ctrip.protoshop.model.ProgramModel;

public interface IProgramState {
    public void onItemClick(ProgramModel model);

    public void showView(ViewHolder holder);
}
