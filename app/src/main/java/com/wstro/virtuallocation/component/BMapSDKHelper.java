package com.wstro.virtuallocation.component;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 百度定位SDK帮助类
 * Created by pengl on 2017/8/2 0002.
 */

public class BMapSDKHelper {
    /**
     * SDK定位客户端，用来发起定位等
     */
    private LocationClient locationClient;

    /**
     * SDK定位客户端可选项
     */
    private LocationClientOption locationOption;

    private BDLocationListener locationListener;

    private Context context;

    public BMapSDKHelper(Context context, BDLocationListener listener){
        this.context = context.getApplicationContext();
        this.locationListener = listener;

        init();
    }

    /**
     * 初始化客户端
     */
    private void init(){
        locationClient = new LocationClient(context);

        locationOption = getDefaultOption();
        //设置可选参数
        locationClient.setLocOption(locationOption);
        //设置回调监听
        locationClient.registerLocationListener(locationListener);
    }


    /**
     * 得到默认的定位参数
     * @return
     */
    private LocationClientOption getDefaultOption(){
        LocationClientOption mOption = new LocationClientOption();
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setAddrType("all");
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        return mOption;
    }

    /**
     * 开始定位
     *
     */
    public void startLocation(){
        // 启动定位
        locationClient.start();
    }

    /**
     * 停止定位
     *
     */
    public void stopLocation(){
        locationClient.stop();
    }

    /**
     * 销毁定位
     *
     */
    public void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.stop();
            locationClient = null;
            locationOption = null;
            context = null;

        }
    }

}
