package com.wstro.virtuallocation.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wstro.app.common.base.BaseAppToolbarActivity;
import com.wstro.app.common.utils.CommonUtils;
import com.wstro.app.common.utils.DeviceUtils;
import com.wstro.app.common.utils.LogUtil;
import com.wstro.app.common.utils.PermissionUtils;
import com.wstro.virtuallocation.R;
import com.wstro.virtuallocation.data.model.AppInfo;
import com.wstro.virtuallocation.ui.presenter.InstallAppPresenter;
import com.wstro.virtuallocation.ui.view.InstallAppView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class InstallAppActivity extends BaseAppToolbarActivity implements InstallAppView {

    InstallAppPresenter presenter;

    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tv_add)
    TextView tvAdd;

    InstallAppAdapter pagerAdapter;

    private int titleTextColor,indicatorColor;

    private int curPage;

    ArrayList<AppInfo> installedList;

    public static void start(Activity context, ArrayList<AppInfo> list) {
        Intent starter = new Intent(context, InstallAppActivity.class);
        starter.putParcelableArrayListExtra("data",list);
        context.startActivityForResult(starter,100);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_install_app;
    }

    @Override
    protected void initViewsAndEvents(Bundle bundle) {
        titleText.setText("选择应用");

        installedList = getIntent().getParcelableArrayListExtra("data");

        titleTextColor = context.getResources().getColor(R.color.black_light);
        indicatorColor = context.getResources().getColor(R.color.colorPrimary);


        PermissionUtils.requestPermission(this, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, grantListener);


    }

    protected void initIndicator(){
        final PagerAdapter pagerAdapter = viewPager.getAdapter();

        if(pagerAdapter.getCount() != 0)
            viewPager.setOffscreenPageLimit(pagerAdapter.getCount() - 1);

        CommonNavigator commonNavigator = new CommonNavigator(this);
        /*commonNavigator.setSkimOver(true);
        int px = DeviceUtils.dp2px(context, 10);
        commonNavigator.setRightPadding(px);
        commonNavigator.setLeftPadding(px);*/
        commonNavigator.setAdjustMode(true);

        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return pagerAdapter.getCount();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView titleView = new SimplePagerTitleView(context);
                titleView.setText(pagerAdapter.getPageTitle(index));
                titleView.setTextSize(15);
                titleView.setNormalColor(titleTextColor);
                titleView.setSelectedColor(indicatorColor);

                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setLineHeight(5);
                //indicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);
                indicator.setColors(indicatorColor);
                return indicator;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return 1.0f;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return DeviceUtils.dp2px(context,10);
            }
        });
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    protected void initData() {
        presenter = new InstallAppPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this,requestCode,permissions,grantResults,grantListener);
    }


    private final PermissionUtils.PermissionGrantListener grantListener = new PermissionUtils.PermissionGrantListener() {
        @Override
        public void onPermissionGranted(int requestCode) {
            LogUtil.d("onPermissionGranted:"+requestCode);

            if(requestCode == PermissionUtils.CODE_READ_EXTERNAL_STORAGE){
                pagerAdapter = new InstallAppAdapter(getSupportFragmentManager());
                viewPager.setAdapter(pagerAdapter);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        curPage = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

                initIndicator();
            }
        }

        @Override
        public void onPermissionDenied(int requestCode) {
            LogUtil.d("onPermissionDenied:"+requestCode);
        }
    };

    @Override
    public void stopProgressDialog() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.detachView();
    }


    @OnClick(R.id.tv_add)
    public void onViewClicked() {
        AppListFragment fragment = (AppListFragment) pagerAdapter.getItem(curPage);
        List<AppInfo> list = fragment.getSelectedList();
        if(CommonUtils.isEmptyArray(list)){
            showCustomToast("请选择需要添加的应用");
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("data",new ArrayList<>(list));
        setResult(RESULT_OK,intent);
        finish();
        //RxBus.getDefault().post(new InstallAppEvent(list));

    }



    public ArrayList<AppInfo> getInstalledList() {
        return installedList;
    }
}
