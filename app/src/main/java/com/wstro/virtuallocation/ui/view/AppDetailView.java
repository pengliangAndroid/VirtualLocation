package com.wstro.virtuallocation.ui.view;

import com.wstro.app.common.mvp.MvpView;
import com.wstro.virtuallocation.data.model.RealData;

public interface AppDetailView extends MvpView {
    void onGetRealDataFail(String error);

    void onGetRealDataSuccess(RealData data);
}