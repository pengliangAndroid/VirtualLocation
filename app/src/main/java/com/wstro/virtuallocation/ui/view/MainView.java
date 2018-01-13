package com.wstro.virtuallocation.ui.view;

import com.wstro.app.common.mvp.MvpView;
import com.wstro.virtuallocation.data.model.AppInfo;

import java.util.List;

/**
 * @author pengl
 */

public interface MainView extends MvpView {

    void onInstallSuccess(List<AppInfo> infoList);

    void onInstallFail(String error);

    void onInstallRefresh();

    void onInstallStart();
}
