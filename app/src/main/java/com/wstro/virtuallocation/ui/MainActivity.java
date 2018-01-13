package com.wstro.virtuallocation.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.lody.virtual.client.core.VirtualCore;
import com.wstro.app.common.base.BaseAppToolbarActivity;
import com.wstro.app.common.base.CommonAdapter;
import com.wstro.app.common.utils.CommonUtils;
import com.wstro.app.common.utils.DialogUtil;
import com.wstro.app.common.widget.GridSpacingDecoration;
import com.wstro.virtuallocation.Constants;
import com.wstro.virtuallocation.R;
import com.wstro.virtuallocation.data.model.AppInfo;
import com.wstro.virtuallocation.ui.presenter.MainPresenter;
import com.wstro.virtuallocation.ui.view.MainView;
import com.wstro.virtuallocation.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseAppToolbarActivity implements MainView{


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    CommonAdapter<AppInfo> adapter;

    MainPresenter presenter;

    //Subscription rxInstallApp,rxInstallAppStatus;

    List<AppInfo> installList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewsAndEvents(Bundle bundle) {
        titleText.setText(R.string.app_name);
        rightImage.setVisibility(View.GONE);

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.addItemDecoration(new GridSpacingDecoration(this,R.dimen.list_item_space_20));
        recyclerView.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter badapter, View view, int position) {
                if(position == adapter.getItemCount() - 1){
                    ArrayList<AppInfo> list = new ArrayList<>(adapter.getData());
                    InstallAppActivity.start(MainActivity.this,list);
                }else{
                    adapter.getItem(position).setFirstOpen(false);
                    AppDetailActivity.start(context,adapter.getItem(position));
                }
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                displayDeleteDialog(position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    private void initAdapter() {
        List<AppInfo> list = presenter.getLocalAppInfoList();

        list.add(new AppInfo());
        adapter = new CommonAdapter<AppInfo>(R.layout.list_install_app_item,list) {
            @Override
            public void convertViewItem(BaseViewHolder holder, AppInfo info) {
                if(info.getPackageName() != null) {
                    holder.setText(R.id.tv_name, info.getAppName());

                    Drawable icon = info.getIcon();
                    if(icon == null){
                        icon = AppUtils.getApplicationIcon(context,info.getPackageName());
                    }

                    holder.setImageDrawable(R.id.iv_icon, icon);
                }
            }
        };

        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void initData() {
        presenter = new MainPresenter();
        presenter.attachView(this);

        initAdapter();

        /*rxInstallApp = RxBus.getDefault().toObservable(InstallAppEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<InstallAppEvent>() {
                    @Override
                    public void call(InstallAppEvent event) {
                        List<AppInfo> list = event.getList();
                        if(CommonUtils.isEmptyArray(list))
                            return;

                        showProgressDialog("安装应用中...");
                        installList = list;
                        presenter.installAppList(installList);
                    }
                });*/

    }



    private void displayDeleteDialog(final int position){
        DialogUtil.dialogBuilder(context,"提示","确定移除该App吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppInfo item = adapter.getItem(position);
                        int userId = Constants.appUserId;
                        if(item.getUserId() != 0){
                            userId = item.getUserId();
                        }
                        boolean flag = VirtualCore.get().uninstallPackageAsUser(item.getPackageName(),userId);
                        if(flag) {
                            presenter.deleteAppInfo(item);
                            adapter.remove(position);
                        }

                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        ArrayList<AppInfo> list = data.getParcelableArrayListExtra("data");
        if(CommonUtils.isEmptyArray(list))
            return;


        installList = list;
        presenter.installAppList(installList);

        showProgressDialog("安装应用中...",false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(presenter != null)
            presenter.detachView();

        /*if(rxInstallApp != null)
            rxInstallApp.unsubscribe();*/
    }

   /* public AppInfo getInstallAppInfo(String packageName){
        if(CommonUtils.isEmptyArray(installList)){
            return null;
        }

        for (int i = 0; i < installList.size(); i++) {
            String name = installList.get(i).getPackageName();
            if (name.equals(packageName)) {
                return installList.get(i);
            }
        }

        return null;
    }
*/

    @Override
    public void onInstallSuccess(List<AppInfo> infoList) {
        stopProgressDialog();
        if(!CommonUtils.isEmptyArray(infoList)){
            adapter.addData(adapter.getItemCount() - 1,infoList);
        }

    }

    @Override
    public void onInstallFail(String error) {
        stopProgressDialog();
        showToast("安装失败");
    }

    @Override
    public void onInstallRefresh() {

    }

    @Override
    public void onInstallStart() {

    }
}
