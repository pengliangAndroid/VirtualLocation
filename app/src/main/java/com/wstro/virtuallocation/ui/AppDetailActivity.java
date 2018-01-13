package com.wstro.virtuallocation.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.ipc.VirtualLocationManager;
import com.suke.widget.SwitchButton;
import com.wstro.app.common.base.BaseAppToolbarActivity;
import com.wstro.app.common.utils.SPUtils;
import com.wstro.virtuallocation.Constants;
import com.wstro.virtuallocation.R;
import com.wstro.virtuallocation.component.LocationInfo;
import com.wstro.virtuallocation.component.MapViewActivity;
import com.wstro.virtuallocation.data.model.AppInfo;
import com.wstro.virtuallocation.data.model.CellInfo;
import com.wstro.virtuallocation.data.model.RealData;
import com.wstro.virtuallocation.data.model.WifiInfo;
import com.wstro.virtuallocation.ui.presenter.AppDetailPresenter;
import com.wstro.virtuallocation.ui.view.AppDetailView;
import com.wstro.virtuallocation.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AppDetailActivity extends BaseAppToolbarActivity implements AppDetailView {

    AppDetailPresenter presenter;

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.switch_button)
    SwitchButton switchButton;

    AppInfo appInfo;

    boolean hasLocation;

    LocationInfo locInfo;

    public static void start(Context context, AppInfo appInfo) {
        Intent starter = new Intent(context, AppDetailActivity.class);
        starter.putExtra("data",appInfo);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_app_detail;
    }

    @Override
    protected void initViewsAndEvents(Bundle bundle) {
        titleText.setText("虚拟操作");

        appInfo = getIntent().getParcelableExtra("data");

        if(appInfo == null)
            return;

        tvName.setText(appInfo.getAppName());
        Drawable icon = appInfo.getIcon();
        if(icon == null){
            icon = AppUtils.getApplicationIcon(context,appInfo.getPackageName());
        }

        ivIcon.setImageDrawable(icon);

       /* String address = (String) SPUtils.get(context,appInfo.getPackageName(),"");
        if(!TextUtils.isEmpty(address)){
            hasLocation = true;
            tvAddress.setText(address);
        }else{
            hasLocation = false;
            tvAddress.setText("未模拟位置");
        }*/

        int mode = VirtualLocationManager.get().getMode(Constants.appUserId, appInfo.getPackageName());
        hasLocation = mode != 0;
        switchButton.setChecked(hasLocation);

        if(hasLocation){
            String name = (String) SPUtils.get(this, appInfo.getPackageName(), "");
            if(!"".equals(name)){
                tvAddress.setText("位置："+name);
            }
        }

        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                int mode = isChecked ? VirtualLocationManager.MODE_USE_SELF : VirtualLocationManager.MODE_CLOSE;

                VirtualLocationManager.get().setMode(0,appInfo.getPackageName(),mode);
            }
        });


    }

    @Override
    protected void initData() {
        presenter = new AppDetailPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.detachView();
    }


    @OnClick({R.id.ll_item,R.id.rl_position})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_item:
                launch(appInfo.getPackageName());
                break;
            case R.id.rl_position:
                if(!hasLocation){
                    switchButton.setChecked(true);
                }
                MapViewActivity.start(this,null);
                break;
        }
    }

    public void launch(final String packageName) {
        final int userId = appInfo.getUserId() != 0 ? appInfo.getUserId() : Constants.appUserId;

        final Intent intent = VirtualCore.get().getLaunchIntent(packageName, userId);
        if(intent == null)
            return;

        VirtualCore.get().setUiCallback(intent, mUiCallback);

        showProgressDialog("启动中...");


        /*if (!appInfo.isFirstOpen()) {
            try {
                VirtualCore.get().preOpt(appInfo.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        VActivityManager.get().startActivity(intent, userId);
        /*rx.Observable.just("")
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        if (!appInfo.isFirstOpen()) {
                            try {
                                VirtualCore.get().preOpt(appInfo.getPackageName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                })
                .compose(RxUtils.<Object>applyIOToMainThreadSchedulers())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        //stopProgressDialog();
                    }

                    @Override
                    public void onNext(Object obj) {
                        //stopProgressDialog();
                        if(intent != null){
                            VActivityManager.get().startActivity(intent, userId);
                        }
                    }
                });*/
    }


    private final VirtualCore.UiCallback mUiCallback = new VirtualCore.UiCallback() {

        @Override
        public void onAppOpened(String packageName, int userId) throws RemoteException {
            stopProgressDialog();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) return;

        locInfo = data.getParcelableExtra("data");
        /*MapUtils.LatLngPoint wgsPoint = MapUtils.bd2wgs(new MapUtils.LatLngPoint(locInfo.getLatitude(),
                locInfo.getLongitude()));*/

        if(locInfo == null)
            return;

        showProgressDialog("获取虚拟数据中...");
        presenter.getNetRealInfo(locInfo,appInfo.getPackageName());


    }

    @Override
    public void onGetRealDataFail(String error) {
        stopProgressDialog();
        showToast("获取虚拟数据失败，"+error);
    }

    @Override
    public void onGetRealDataSuccess(RealData data) {
        stopProgressDialog();

        if(data == null)
            return;

        List<CellInfo> cellList = data.getCellInfoList();
        List<WifiInfo> wifiList = data.getWifiInfoList();

        int cellNumber = cellList != null ? cellList.size() : 0;
        int wifiNumber = wifiList != null ? wifiList.size() : 0;

        showToast(String.format("虚拟GPS、基站(%d)、WIFI(%d)成功",cellNumber,wifiNumber));
        tvAddress.setText("位置："+locInfo.getAddrStr());
    }


}
