package com.wstro.virtuallocation.data;

import android.content.Context;

import com.wstro.app.common.data.AbstractDataManager;
import com.wstro.app.common.data.HttpHelper;
import com.wstro.app.common.data.PreferencesHelper;
import com.wstro.app.common.data.db.DataBaseHelper;
import com.wstro.virtuallocation.data.db.CustomDataBaseHelper;
import com.wstro.virtuallocation.data.model.CellInfo;
import com.wstro.virtuallocation.data.model.WifiInfo;
import com.wstro.virtuallocation.data.net.RetrofitHelper;
import com.wstro.virtuallocation.data.net.ServiceRestApi;

import java.util.List;

import rx.Observable;

/**
 * ClassName: DataManager <br/>
 * Function: TODO ADD FUNCTION. <br/>
 *
 * @author pengl
 * @date 2017/10/7
 */

public class DataManager extends AbstractDataManager {
    private static final String DB_NAME = "data.db";

    private static final int DB_VERSION = 1;

    private static final String SP_NAME = "settings";

    private static DataManager instance = null;

    private RetrofitHelper retrofitHelper;

    private CustomDataBaseHelper dataHelper;

    private DataManager(){}

    @Override
    protected PreferencesHelper buildPreferencesHelper(Context context) {
        return new CustomPreferencesHelper(context,SP_NAME);
    }

    @Override
    protected DataBaseHelper buildDataBaseHelper(Context context) {
        dataHelper = new CustomDataBaseHelper(new CustomUpgradeHelper(context,DB_NAME,null,DB_VERSION));
        return null;
    }

    @Override
    protected HttpHelper buildHttpHelper(Context context) {
        retrofitHelper = new RetrofitHelper(context);
        return null;
    }

    public static DataManager get(){
        if(instance == null){
            synchronized (DataManager.class){
                if(instance == null){
                    instance = new DataManager();
                }
            }
        }

        return instance;
    }


    public Observable<List<WifiInfo>> getWifiInfo(double lat,double lon) {
        return retrofitHelper.getService(ServiceRestApi.class)
                .rewifi(lat,lon,10,"bd09","bd09");
    }

    public Observable<List<CellInfo>> getCellInfo(double lat, double lon) {
        return retrofitHelper.getService(ServiceRestApi.class)
                .recell(lat,lon,3,-1,1,"bd09","bd09");
    }


    public CustomDataBaseHelper getDataHelper() {
        return dataHelper;
    }

    public void destroy(){
        super.destroy();

        instance = null;
    }




}
