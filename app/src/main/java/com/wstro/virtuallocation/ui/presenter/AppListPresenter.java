package com.wstro.virtuallocation.ui.presenter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.wstro.app.common.mvp.BaseFrgPresenter;
import com.wstro.app.common.utils.CommonUtils;
import com.wstro.app.common.utils.rx.RxUtils;
import com.wstro.virtuallocation.data.model.AppInfo;
import com.wstro.virtuallocation.ui.adapter.PinnedHeaderEntity;
import com.wstro.virtuallocation.ui.view.AppListView;
import com.wstro.virtuallocation.utils.Cn2Spell;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.wstro.virtuallocation.ui.adapter.BaseHeaderAdapter.TYPE_DATA;

public class AppListPresenter extends BaseFrgPresenter<AppListView> {
    private static final String FORMAT = "^[a-z,A-Z].*$";
    private Map<String, Integer> indexMap;


    public AppListPresenter() {
        indexMap = new HashMap<>();
    }

    public void getInstallAppList(final Context context,final List<AppInfo> filterList){
        Observable.just("")
                .map(new Func1<String, List<PinnedHeaderEntity<AppInfo>>>() {
                    @Override
                    public List<PinnedHeaderEntity<AppInfo>> call(String s) {
                        AppInfo info = new AppInfo();
                        info.setPackageName(context.getPackageName());
                        filterList.add(info);
                        List<AppInfo> list = queryInstallAppList(context, filterList);
                        return sortListAppName(list);
                    }
                })
                .compose(RxUtils.<List<PinnedHeaderEntity<AppInfo>>>applyIOToMainThreadSchedulers())
                .compose(getBaseActivity().<List<PinnedHeaderEntity<AppInfo>>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<List<PinnedHeaderEntity<AppInfo>>>() {
                    @Override
                    public void call(List<PinnedHeaderEntity<AppInfo>> data) {
                        getMvpView().onGetInstallAppListSuccess(data);
                    }
                });

    }

    public void getLocalAppList(final Context context,String rootDir){
        if(rootDir == null)
            return;

        final File rootFile = new File(rootDir);
        if(!rootFile.exists())
            return;

        Observable.from(rootFile.listFiles())
                .map(new Func1<File, List<PinnedHeaderEntity<AppInfo>>>() {
                    @Override
                    public List<PinnedHeaderEntity<AppInfo>> call(File file) {
                        List<AppInfo> list = findAndParseAPKs(context, rootFile);
                        return sortListAppName(list);
                    }
                })
                .compose(RxUtils.<List<PinnedHeaderEntity<AppInfo>>>applyIOToMainThreadSchedulers())
                .compose(getBaseActivity().<List<PinnedHeaderEntity<AppInfo>>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<List<PinnedHeaderEntity<AppInfo>>>() {
                    @Override
                    public void call(List<PinnedHeaderEntity<AppInfo>> data) {
                        getMvpView().onGetInstallAppListSuccess(data);
                    }
                });
    }

    private List<PinnedHeaderEntity<AppInfo>> sortListAppName(List<AppInfo> appInfoList){
        List<PinnedHeaderEntity<AppInfo>> list = new ArrayList<>();

        if(!CommonUtils.isEmptyArray(appInfoList)){
            Map<String,List<AppInfo>> tempMap = new HashMap<>();
            List<String> sectionList = new ArrayList<>();

            for (int i = 0; i < appInfoList.size(); i++) {
                AppInfo obj = appInfoList.get(i);
                String name = Cn2Spell.getPinYinFirstLetter(obj.getAppName());
                if(TextUtils.isEmpty(name))
                    name = "#";
                addToDataMap(name.toUpperCase(), obj, sectionList, tempMap);
            }

            if(sectionList.size() != 0)
                sortDataByName(list,sectionList,tempMap);
        }

        return list;
    }

    private void addToDataMap(String firstName, AppInfo obj,List<String> sectionList,Map<String,List<AppInfo>> tempMap) {
        if (firstName.matches(FORMAT)) {
            if (sectionList.contains(firstName)) {
                tempMap.get(firstName).add(obj);
            } else {
                sectionList.add(firstName);
                List<AppInfo> list = new ArrayList<>();
                list.add(obj);
                tempMap.put(firstName, list);
            }
        } else {
            if (sectionList.contains("#")) {
                tempMap.get("#").add(obj);
            } else {
                sectionList.add("#");
                List<AppInfo> list = new ArrayList<>();
                list.add(obj);
                tempMap.put("#", list);
            }

        }
    }

    /**
     * 对数据进行排序
     */
    private void sortDataByName(List<PinnedHeaderEntity<AppInfo>> list,
                          List<String> sectionList,Map<String,List<AppInfo>> map) {
        Collections.sort(sectionList);//按照字母重新排序

        int position = 0;
        for (int i = 0; i < sectionList.size(); i++) {
            String headerName = sectionList.get(i);
            indexMap.put(headerName, position);// 存入map中，key为首字母字符串，value为首字母在listview中位置

            List<AppInfo> infos = map.get(sectionList.get(i));

            if(infos != null && infos.size() != 0){
                //list.add(new PinnedHeaderEntity<>(new AppInfo(), TYPE_HEADER,headerName));

                for (int j = 0; j < infos.size(); j++) {
                    list.add(new PinnedHeaderEntity<>(infos.get(j), TYPE_DATA,headerName));
                }
            }

            position = list.size() + 1;// 计算下一个首字母在listview的位置
        }

    }

    /**
     * 只遍历第二层
     * @param context
     * @param rootFile
     * @return
     */
    private List<AppInfo> findAndParseAPKs(Context context, File rootFile) {
        List<AppInfo> infoList = new ArrayList<>();

        if(rootFile.isDirectory()){
            File[] dirFiles = rootFile.listFiles();
            if (dirFiles == null)
                return infoList;

            for (File f : dirFiles) {
                addAppInfo(context,f,infoList);
            }
        }else{
            addAppInfo(context,rootFile,infoList);
        }

        return infoList;
    }

    private void addAppInfo(Context context,File f,List<AppInfo> list){
        if (!f.getName().toLowerCase().endsWith(".apk"))
            return;
        AppInfo appInfo = new AppInfo();
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageArchiveInfo(f.getAbsolutePath(), 0);
            //pkgInfo.applicationInfo.sourceDir = f.getAbsolutePath();
            //pkgInfo.applicationInfo.publicSourceDir = f.getAbsolutePath();
            appInfo.setAppName(f.getName());

            appInfo.setPackageName(pkgInfo.applicationInfo.packageName);
            Drawable icon = pkgInfo.applicationInfo.loadIcon(context.getPackageManager());
            appInfo.setIcon(icon);
            appInfo.setApkFilePath(f.getAbsolutePath());

            list.add(appInfo);
        } catch (Exception e) {
            // Ignore
        }
    }

    private List<AppInfo> queryInstallAppList(Context context,List<AppInfo> filterList){
        List<ApplicationInfo> infoList = context.getPackageManager().getInstalledApplications(0);
        List<AppInfo> appInfoList = new ArrayList<>();

        for (ApplicationInfo applicationInfo : infoList){
            int systemAppFlag = applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM;
            //非系统应用
            if(systemAppFlag == 0){


                AppInfo appInfo = new AppInfo();

                String name = applicationInfo.loadLabel(context.getPackageManager()).toString();
                appInfo.setAppName(name);
                appInfo.setPackageName(applicationInfo.packageName);

                if(filterList.contains(appInfo))
                    continue;

                Drawable icon = applicationInfo.loadIcon(context.getPackageManager());
                appInfo.setIcon(icon);
                appInfo.setApkFilePath(applicationInfo.sourceDir);

                appInfoList.add(appInfo);
            }
        }

        return appInfoList;
    }


    public Map<String, Integer> getIndexMap() {
        return indexMap;
    }
}