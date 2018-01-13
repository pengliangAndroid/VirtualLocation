package com.wstro.virtuallocation.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pengl
 */

public class InstallAppAdapter extends FragmentPagerAdapter {
    private String[] names = new String[]{"本机应用", "安装包"};

    private List<Fragment> fragmentList;

    FragmentManager fm;

    public InstallAppAdapter(FragmentManager fm) {
        super(fm);

        this.fm = fm;
        fragmentList = new ArrayList<>();

        //for (int i = 0; i < names.length; i++) {
        fragmentList.add(AppListFragment.newInstance(true));
        fragmentList.add(AppListFragment.newInstance(false));
        //}
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    public String getPageTitle(int position){
        return names[position];
    }


}
