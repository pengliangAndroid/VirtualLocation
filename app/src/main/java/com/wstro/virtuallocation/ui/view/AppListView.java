package com.wstro.virtuallocation.ui.view;

import com.wstro.app.common.mvp.MvpView;
import com.wstro.virtuallocation.data.model.AppInfo;
import com.wstro.virtuallocation.ui.adapter.PinnedHeaderEntity;

import java.util.List;

public interface AppListView extends MvpView {

    void onGetInstallAppListSuccess(List<PinnedHeaderEntity<AppInfo>> data);
    void onGetInstallAppListFail(String error);
}