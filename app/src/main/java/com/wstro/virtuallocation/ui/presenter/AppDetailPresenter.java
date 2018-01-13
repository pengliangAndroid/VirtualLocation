package com.wstro.virtuallocation.ui.presenter;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.lody.virtual.client.ipc.VirtualLocationManager;
import com.lody.virtual.remote.vloc.VCell;
import com.lody.virtual.remote.vloc.VLocation;
import com.lody.virtual.remote.vloc.VWifi;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.wstro.app.common.mvp.BaseActPresenter;
import com.wstro.app.common.utils.SPUtils;
import com.wstro.app.common.utils.rx.RxUtils;
import com.wstro.virtuallocation.Constants;
import com.wstro.virtuallocation.component.LocationInfo;
import com.wstro.virtuallocation.data.DataManager;
import com.wstro.virtuallocation.data.model.CellInfo;
import com.wstro.virtuallocation.data.model.RealData;
import com.wstro.virtuallocation.data.model.WifiInfo;
import com.wstro.virtuallocation.ui.view.AppDetailView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

public class AppDetailPresenter extends BaseActPresenter<AppDetailView> {
    DataManager dataManager;

    public AppDetailPresenter() {
        dataManager = DataManager.get();
    }

    public void getNetRealInfo(final LocationInfo locInfo,final String packageName){
        double lat = locInfo.getLatitude();
        double lon = locInfo.getLongitude();

        Observable<List<WifiInfo>> wifiObservable = dataManager.getWifiInfo(lat, lon);
        Observable<List<CellInfo>> cellObservable = dataManager.getCellInfo(lat, lon);

        wifiObservable
                .zipWith(cellObservable, new Func2<List<WifiInfo>, List<CellInfo>, RealData>() {
                    @Override
                    public RealData call(List<WifiInfo> wifiInfoList, List<CellInfo> cellInfoList) {
                        return new RealData(cellInfoList,wifiInfoList);
                    }
                })
                .map(new Func1<RealData, RealData>() {
                    @Override
                    public RealData call(RealData data) {
                        List<CellInfo> cellList = data.getCellInfoList();
                        List<WifiInfo> wifiList = data.getWifiInfoList();

                        List<VCell> vCellList = new ArrayList<>();
                        List<VWifi> vWifiList = new ArrayList<>();

                        for (int i = 0; i < cellList.size(); i++) {
                            vCellList.add(transferCell(cellList.get(i)));
                        }

                        for (int i = 0; i < wifiList.size(); i++) {
                            vWifiList.add(transferWifi(wifiList.get(i)));
                        }

                        if(vCellList.size() > 0) {
                            VirtualLocationManager.get().setCell(Constants.appUserId,
                                    packageName, vCellList.get(0));
                            VirtualLocationManager.get().setAllCell(Constants.appUserId,
                                    packageName, vCellList);
                        }

                        VirtualLocationManager.get().setAllWifi(Constants.appUserId,
                                packageName, vWifiList);

                        VirtualLocationManager.get().setLocation(Constants.appUserId,
                                packageName,transferLocation(locInfo));
                        SPUtils.put(baseActivity,packageName,locInfo.getAddrStr());

                        return data;
                    }
                })
                .compose(RxUtils.<RealData>applyIOToMainThreadSchedulers())
                .compose(baseActivity.<RealData>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Subscriber<RealData>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().onGetRealDataFail(e.getMessage());
                    }

                    @Override
                    public void onNext(RealData data) {
                        getMvpView().onGetRealDataSuccess(data);
                    }
                });
    }

    private VLocation transferLocation(LocationInfo locInfo){
        if(locInfo == null)
            return null;

        VLocation vLocation = new VLocation();

        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.BD09LL);
        LatLng ll = new LatLng(locInfo.getLatitude(), locInfo.getLongitude());
        converter.coord(ll);
        ll = converter.convert();

        vLocation.latitude = ll.latitude;
        vLocation.longitude = ll.longitude;
        vLocation.address = locInfo.getAddrStr();
        vLocation.city = locInfo.getCityName();

        return vLocation;
    }

    private VCell transferCell(CellInfo cellInfo){
        if(cellInfo == null)
            return null;

        VCell obj = new VCell();
        obj.mcc = 460;
        obj.mnc = cellInfo.getMnc();
        obj.cid = cellInfo.getCi();
        obj.lac = cellInfo.getLac();
        obj.psc = -1;

        return obj;
    }

    private VWifi transferWifi(WifiInfo wifiInfo){
        if(wifiInfo == null)
            return null;

        VWifi obj = new VWifi();
        obj.bssid = wifiInfo.getMac();
        obj.level = wifiInfo.getAcc();

        return obj;
    }
}