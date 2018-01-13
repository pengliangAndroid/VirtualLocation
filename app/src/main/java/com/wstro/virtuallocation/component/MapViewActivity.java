package com.wstro.virtuallocation.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.wstro.app.common.base.BaseAppToolbarActivity;
import com.wstro.app.common.utils.LogUtil;
import com.wstro.app.common.utils.PermissionUtils;
import com.wstro.virtuallocation.R;

import butterknife.BindView;
import butterknife.OnClick;

public class MapViewActivity extends BaseAppToolbarActivity implements BDLocationListener {

    @BindView(R.id.map_view)
    TextureMapView mapView;

    @BindView(R.id.tv_address)
    TextView tvAddress;

    @BindView(R.id.edt_search)
    EditText edtSearch;

    private String cityName;

    //private BaiduMap baiduMap;

    LocationInfo myInfo;

    BMapSDKHelper sdkHelper;

    GeoCoder mapSearch;

    LatLng curLocation;

    public static void start(Activity activity, LocationInfo destInfo) {
        Intent starter = new Intent(activity, MapViewActivity.class);
        starter.putExtra("destInfo", destInfo);
        activity.startActivityForResult(starter,100);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_view;
    }

    @Override
    protected void initViewsAndEvents(Bundle bundle) {
        titleText.setText("位置信息");


        sdkHelper = new BMapSDKHelper(this,this);

        //普通地图
        mapSearch = GeoCoder.newInstance();
        mapSearch.setOnGetGeoCodeResultListener(listener);
        mapView.getMap().setMapType(BaiduMap.MAP_TYPE_NORMAL);

        mapView.showZoomControls(true);
        // 开启定位图层
        mapView.getMap().setMyLocationEnabled(true);
        //mapView.setPadding(20,0,0,20);
        //mapView.setVisibility(View.INVISIBLE);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    startSearch();
                    // 当按了搜索之后关闭软键盘
                    ((InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            MapViewActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

        PermissionUtils.requestPermission(this, PermissionUtils.CODE_ACCESS_COARSE_LOCATION, grantListener);

        mapView.getMap().setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus status) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus status, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus status) {
                setDestLocationIcon(status.target);
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus status) {
                LatLng target = status.target;

                mapSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(target));
            }
        });
    }

    private void startSearch() {
        String keyword = edtSearch.getText().toString().trim();
        if("".equals(keyword))
            return;

        if(TextUtils.isEmpty(cityName)){
            showToast("定位失败，请重新选择定位");
            return;
        }


        showProgressDialog("搜索中...");
        mapSearch.geocode(new GeoCodeOption()
                .city(cityName)
                .address(keyword));
        edtSearch.setText("");
    }


    @Override
    protected void initData() {

    }

    private void setDestLocationIcon(LatLng latLng){
        if(latLng != null) {
            mapView.getMap().clear();

            LatLng point = new LatLng(latLng.latitude, latLng.longitude);
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.ic_dest_loc);
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            mapView.getMap().addOverlay(option);

            //locationMapStatus(point.latitude,point.longitude,true);

            setMyLocationIcon(new LatLng(myInfo.getLatitude(),myInfo.getLongitude()));
        }
    }

    private void setMyLocationIcon(LatLng latLng){
        if(latLng != null) {
            LatLng point = new LatLng(latLng.latitude, latLng.longitude);
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.ic_my_loc);
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            mapView.getMap().addOverlay(option);

            //locationMapStatus(point.latitude,point.longitude,false);
        }
    }

    private void locationMapStatus(double latitude, double longitude,boolean isDefaultZoom){
        LatLng center = new LatLng(latitude, longitude);

        //定义地图状态
        MapStatus.Builder builder = new MapStatus.Builder()
                .target(center);

        if(!isDefaultZoom){
            builder.zoom(16);
        }else{
            builder.zoom(mapView.getMap().getMapStatus().zoom);
        }

        MapStatus mapStatus = builder.build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        //改变地图状态
        mapView.getMap().setMapStatus(mMapStatusUpdate);
    }

    @Override
    protected void onResume() {
        if (null != mapView) {
            mapView.setVisibility(View.VISIBLE);
            mapView.onResume();
        }
        super.onResume();

    }

    @Override
    protected void onPause() {
        if (null != mapView) {
            mapView.setVisibility(View.INVISIBLE);
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sdkHelper.destroyLocation();
        if (null != mapView) {
            mapView.onDestroy();
        }

        if(mapSearch != null)
            mapSearch.destroy();

    }

    @OnClick({R.id.iv_loc,R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_loc:
                if(myInfo != null) {
                    locationMapStatus(myInfo.getLatitude(), myInfo.getLongitude(),false);
                }else{
                    PermissionUtils.requestPermission(this, PermissionUtils.CODE_ACCESS_COARSE_LOCATION, grantListener);
                }
                break;
            case R.id.tv_confirm:
                if(curLocation == null)
                    return;
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setAddrStr(tvAddress.getText().toString());
                locationInfo.setCityName(cityName);
                locationInfo.setLatitude(curLocation.latitude);
                locationInfo.setLongitude(curLocation.longitude);

                Intent intent = new Intent();
                intent.putExtra("data",locationInfo);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }


    @Override
    public void onReceiveLocation(final BDLocation location) {
        if(location != null){
            myInfo = new LocationInfo();
            myInfo.setAddrStr(location.getAddrStr());
            myInfo.setCityName(location.getCity());
            myInfo.setLongitude(location.getLongitude());
            myInfo.setLatitude(location.getLatitude());

            cityName = location.getCity();
            setAddress(location.getAddrStr());

            //setMyLocationIcon(new LatLng(location.getLatitude(),location.getLongitude()));

            LogUtil.d(myInfo.toString());

            //setMyLocationIcon(new LatLng(myInfo.getLatitude(),myInfo.getLongitude()));
            locationMapStatus(myInfo.getLatitude(),myInfo.getLongitude(),false);
            setDestLocationIcon(new LatLng(location.getLatitude(),location.getLongitude()));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this,requestCode,permissions,grantResults,grantListener);
    }

    private final PermissionUtils.PermissionGrantListener grantListener = new PermissionUtils.PermissionGrantListener() {
        @Override
        public void onPermissionGranted(int requestCode) {
            LogUtil.d("onPermissionGranted:"+requestCode);
            if(requestCode == PermissionUtils.CODE_ACCESS_COARSE_LOCATION){
                // 开始定位SDK
                sdkHelper.startLocation();
            }
        }

        @Override
        public void onPermissionDenied(int requestCode) {
            LogUtil.d("onPermissionDenied:"+requestCode);
            showToast("访问定位权限失败，请在应用设置开启权限");
        }
    };

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {
            stopProgressDialog();
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
                LogUtil.d("没有检索到结果");
                return;
            }

            LogUtil.d("检索到结果:"+result.getAddress());
            setAddress(result.getAddress());
            //获取地理编码结果
            setDestLocationIcon(result.getLocation());
            locationMapStatus(result.getLocation().latitude,result.getLocation().longitude,false);
            curLocation = result.getLocation();
        }

        @Override

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
                LogUtil.d("没有检索到结果");
                return;
            }

            LogUtil.d("检索到结果:"+result.getAddress());
            //tvAddress.setText(result.getAddress());
            setAddress(result.getAddress());
            //获取反向地理编码结果
            cityName = result.getAddressDetail().city;
            curLocation = result.getLocation();

            setDestLocationIcon(result.getLocation());

        }
    };

    private void setAddress(final String address){
        tvAddress.post(new Runnable() {
            @Override
            public void run() {
                tvAddress.setText(address);
            }
        });
    }
}
