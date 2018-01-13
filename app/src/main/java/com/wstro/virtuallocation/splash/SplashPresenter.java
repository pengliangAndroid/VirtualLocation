package com.wstro.virtuallocation.splash;

import android.content.Context;

import com.wstro.app.common.data.db.LoginUser;
import com.wstro.app.common.mvp.BaseActPresenter;
import com.wstro.app.common.utils.DeviceUtils;
import com.wstro.app.common.utils.LogUtil;
import com.wstro.virtuallocation.data.DataManager;

public class SplashPresenter extends BaseActPresenter<SplashView> {
    DataManager dataManager;


    public SplashPresenter() {
        dataManager = DataManager.get();
    }

    /**
     * 如果当前版本有启动引导页且是当前版本第一次登录则进入引导页，否则不进入
     *
     * @param context
     * @return
     */
    public boolean enterGuidePage(Context context) {
        //得到当前包版本
        String curVersionName = DeviceUtils.getVersionName(context);

        //得到上次启动的包版本
        String lastLoginVersionName = DataManager.get().getLoginVersionName();

        boolean isSameVersion = curVersionName.equals(lastLoginVersionName);

        boolean isFirstRun = DataManager.get().isFirstRun();

        if (false && (!isSameVersion || isFirstRun)) {
            return true;
        } else {
            return false;
        }
    }

    public void saveCurVersionName(Context context) {
        String curVersionName = DeviceUtils.getVersionName(context);

        DataManager.get().setLoginVersionName(curVersionName);
    }

    public void saveIsFirstRun() {
        DataManager.get().setIsFirstRun(false);
    }


    public boolean isLogin() {
        return DataManager.get().getLastLoginUser() != null;
    }

    public boolean loginIsOvertime(){
        LoginUser user = DataManager.get().getLastLoginUser();
        if(user == null)
            return true;

       /* OauthToken token = DataManager.get().getOauthToken();
        AppData.get().setOauthToken(token);
        DataConstants.accessToken = token.getAccessToken();*/

        long millis = System.currentTimeMillis();
        long interval = millis - user.getLastLoginTime();

        LogUtil.d("间隔时间(s)："+interval/1000);
       /* if((interval/1000) >= token.getExpiresIn()) {
            return true;
        }*/
        return false;
    }

    public LoginUser getLastLoginInfo(){
        return dataManager.getLastLoginUser();
    }

}
