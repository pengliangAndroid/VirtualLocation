package com.wstro.virtuallocation.ui.presenter;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.os.VUserInfo;
import com.lody.virtual.os.VUserManager;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.wstro.app.common.mvp.BaseActPresenter;
import com.wstro.app.common.utils.rx.RxUtils;
import com.wstro.virtuallocation.data.DataManager;
import com.wstro.virtuallocation.data.db.CustomDataBaseHelper;
import com.wstro.virtuallocation.data.model.AppInfo;
import com.wstro.virtuallocation.ui.view.MainView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author pengl
 */

public class MainPresenter extends BaseActPresenter<MainView> {

    DataManager dataManager;

    public MainPresenter() {
        dataManager = DataManager.get();
    }

    public List<AppInfo> getLocalAppInfoList(){
        CustomDataBaseHelper helper = dataManager.getDataHelper();
        return helper.queryAll(helper.getAppInfoDao());
    }

    public void insertAppInfo(AppInfo appInfo){
        CustomDataBaseHelper helper = dataManager.getDataHelper();
        helper.save(appInfo,helper.getAppInfoDao());
    }

    public void insertAppInfoList(List<AppInfo> list){
        CustomDataBaseHelper helper = dataManager.getDataHelper();
        helper.saveList(list,helper.getAppInfoDao());
    }


    public void deleteAllAppInfo(){
        CustomDataBaseHelper helper = dataManager.getDataHelper();
        helper.deleteAll(helper.getAppInfoDao());
    }

    public void deleteAppInfo(AppInfo info){
        CustomDataBaseHelper helper = dataManager.getDataHelper();
        helper.delete(info,helper.getAppInfoDao());
    }

    public void updateAppInfo(AppInfo info){
        CustomDataBaseHelper helper = dataManager.getDataHelper();
        helper.update(info,helper.getAppInfoDao());
    }


    class AddResult {
        private AppInfo appData;
        private int userId;
        private boolean justEnableHidden;
    }

    public void installAppList(final List<AppInfo> list){
        Observable.just("")
                //.observeOn(AndroidSchedulers.mainThread())
               /* .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        baseActivity.showProgressDialog("安装应用中...");
                    }
                })*/
                .map(new Func1<String, List<AppInfo>>() {
                    @Override
                    public List<AppInfo> call(String s) {
                        List<AppInfo> successList = new ArrayList<>();

                        getMvpView().onInstallStart();
                        for (int i = 0; i < list.size(); i++) {
                            //String path = list.get(i).getApkFilePath();

                            AddResult addResult = new AddResult();
                            String packageName = list.get(i).getPackageName();

                            InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(packageName, 0);
                            addResult.justEnableHidden = installedAppInfo != null;
                            if (addResult.justEnableHidden) {
                                int[] userIds = installedAppInfo.getInstalledUsers();
                                int nextUserId = userIds.length;
                            /*
                              Input : userIds = {0, 1, 3}
                              Output: nextUserId = 2
                             */
                                for (int j = 0; j < userIds.length; j++) {
                                    if (userIds[j] != j) {
                                        nextUserId = j;
                                        break;
                                    }
                                }
                                addResult.userId = nextUserId;

                                if (VUserManager.get().getUserInfo(nextUserId) == null) {
                                    // user not exist, create it automatically.
                                    String nextUserName = "Space " + (nextUserId + 1);
                                    VUserInfo newUserInfo = VUserManager.get().createUser(nextUserName, VUserInfo.FLAG_ADMIN);
                                    if (newUserInfo == null) {
                                        throw new IllegalStateException();
                                    }
                                }
                                boolean success = VirtualCore.get().installPackageAsUser(nextUserId, packageName);
                                if (!success) {
                                    throw new IllegalStateException();
                                }
                            }else{
                                InstallResult res = addVirtualApp(list.get(i));
                                if (!res.isSuccess) {
                                    throw new IllegalStateException();
                                }
                            }

                            boolean multipleVersion = addResult.justEnableHidden && addResult.userId != 0;
                            if (!multipleVersion) {
                                handleOptApp(packageName, true);
                                list.get(i).setFirstOpen(true);
                            } else {
                                list.get(i).setUserId(addResult.userId);
                                handleOptApp(packageName, false);
                                list.get(i).setFirstOpen(true);
                            }

                            insertAppInfo(list.get(i));
                            successList.add(list.get(i));
                        }

                        return successList;
                    }
                })
                .compose(RxUtils.<List<AppInfo>>applyIOToMainThreadSchedulers())
                .compose(baseActivity.<List<AppInfo>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Subscriber<List<AppInfo>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().onInstallFail(e.getMessage());
                    }

                    @Override
                    public void onNext(List<AppInfo> data) {
                        getMvpView().onInstallSuccess(data);
                    }
                });
    }

    public InstallResult addVirtualApp(AppInfo info) {
        int flags = InstallStrategy.COMPARE_VERSION | InstallStrategy.SKIP_DEX_OPT;
        /*if (info.fastOpen) {
            flags |= InstallStrategy.DEPEND_SYSTEM_IF_EXIST;
        }*/
        return VirtualCore.get().installPackage(info.getApkFilePath(), flags);
    }

    private void handleOptApp(final String packageName, final boolean needOpt) {
        long time = System.currentTimeMillis();
        if (needOpt) {
            try {
                VirtualCore.get().preOpt(packageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        time = System.currentTimeMillis() - time;
        if (time < 1500L) {
            try {
                Thread.sleep(1500L - time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
