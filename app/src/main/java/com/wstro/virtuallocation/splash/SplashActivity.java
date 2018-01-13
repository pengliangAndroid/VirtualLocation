package com.wstro.virtuallocation.splash;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.wstro.app.common.base.BaseActivity;
import com.wstro.virtuallocation.R;

import butterknife.BindView;


public class SplashActivity extends BaseActivity implements SplashView {

    @BindView(R.id.rl_splash)
    RelativeLayout rlSplash;

    SplashPresenter presenter;

    private Handler handler = new Handler();

    @Override
    protected int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_splash;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {

    }


    @Override
    protected void initData() {
        presenter = new SplashPresenter();
        presenter.attachView(this);


        rlSplash.setVisibility(View.VISIBLE);

        if (presenter.isLogin()) {


            startMainActivity(false);

        } else {
            //startMainActivity();
            //LoginActivity.start(context,true);
            //finish();
        }

        //startMainActivity(false);
        presenter.saveCurVersionName(this);
    }


    private void startMainActivity(final boolean isLogin) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.saveIsFirstRun();

                //MainActivity.start(context,isLogin);
                finish();
            }
        }, 800);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.detachView();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }


}
