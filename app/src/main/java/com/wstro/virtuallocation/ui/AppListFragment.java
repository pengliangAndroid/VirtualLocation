package com.wstro.virtuallocation.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseViewHolder;
import com.classic.common.MultipleStatusView;
import com.wstro.app.common.base.BaseFragment;
import com.wstro.app.common.base.CommonAdapter;
import com.wstro.app.common.utils.CommonUtils;
import com.wstro.app.common.utils.LogUtil;
import com.wstro.app.common.widget.DividerItemDecoration;
import com.wstro.virtuallocation.R;
import com.wstro.virtuallocation.data.model.AppInfo;
import com.wstro.virtuallocation.ui.adapter.PinnedHeaderEntity;
import com.wstro.virtuallocation.ui.presenter.AppListPresenter;
import com.wstro.virtuallocation.ui.view.AppListView;
import com.wstro.virtuallocation.utils.AppUtils;
import com.wstro.virtuallocation.widget.BladeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class AppListFragment extends BaseFragment implements AppListView {

    AppListPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.multiple_status_view)
    MultipleStatusView multipleStatusView;
    @BindView(R.id.blade_view)
    BladeView bladeView;


    CommonAdapter<PinnedHeaderEntity<AppInfo>> adapter;

    boolean isInstalled;

    Map<Integer, Boolean> selectedMap = new HashMap<>();

    public static AppListFragment newInstance(boolean isInstalled) {
        Bundle args = new Bundle();

        AppListFragment fragment = new AppListFragment();
        args.putBoolean("isInstalled", isInstalled);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_list;
    }

    @Override
    protected void initViewsAndEvents(View view, Bundle bundle) {
        isInstalled = getArguments().getBoolean("isInstalled");

        initRecyclerView();
        initAdapter();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));

        /*recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                LogUtil.d("onSimpleItemClick");
            }
        });*/

        bladeView.setOnItemClickListener(new BladeView.OnItemClickListener() {
            @Override
            public void onItemClick(String s) {
                Map<String, Integer> map = presenter.getIndexMap();
                if (map.get(s) != null) {
                    recyclerView.scrollToPosition(map.get(s));
                }
            }
        });
    }

    private void initAdapter() {
        List<PinnedHeaderEntity<AppInfo>> list = new ArrayList<>();

        adapter = new CommonAdapter<PinnedHeaderEntity<AppInfo>>(R.layout.list_select_app_item, list) {
            @Override
            public void convertViewItem(BaseViewHolder holder, PinnedHeaderEntity<AppInfo> info) {
                AppInfo item = info.getData();
                holder.setText(R.id.tv_name, item.getAppName());
                Drawable icon = item.getIcon();
                if (icon == null) {
                    icon = AppUtils.getApplicationIcon(context, item.getPackageName());
                }

                holder.setImageDrawable(R.id.iv_icon, icon);

                int position = holder.getLayoutPosition();
                CheckBox checkBox = holder.getView(R.id.chb_select);
                checkBox.setTag(position);

                Boolean isChecked = selectedMap.get(position);
                checkBox.setChecked(isChecked != null ? isChecked : false);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox chb = (CheckBox) v;
                        Integer tag = (Integer) v.getTag();

                        LogUtil.d(tag.intValue() + "," + chb.isChecked());
                        selectedMap.put(tag, chb.isChecked());
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        presenter = new AppListPresenter();
        presenter.attachView(this);

        multipleStatusView.showLoading();
        if (isInstalled) {
            InstallAppActivity activity = (InstallAppActivity) getActivity();
            presenter.getInstallAppList(context, activity.getInstalledList());
        } else {
            presenter.getLocalAppList(context, AppUtils.getSDPath().getAbsolutePath());


        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.detachView();
    }


    @Override
    public void onGetInstallAppListSuccess(List<PinnedHeaderEntity<AppInfo>> data) {
        if (CommonUtils.isEmptyArray(data)) {
            multipleStatusView.showEmpty();
        } else {
            multipleStatusView.showContent();
            /*Map<String, Integer> map = presenter.getIndexMap();
            Set<String> strings = map.keySet();
            String[] letters = new String[strings.size()];
            letters = strings.toArray(letters);

            bladeView.setLetters(letters);*/
            adapter.setNewData(data);
        }
    }

    @Override
    public void onGetInstallAppListFail(String error) {

    }

    public List<AppInfo> getSelectedList() {
        List<AppInfo> list = new ArrayList<>();
        Iterator<Integer> iterator = selectedMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            boolean flag = selectedMap.get(key) != null && selectedMap.get(key);
            if (flag) {
                list.add(adapter.getItem(key).getData());
            }
        }

        return list;
    }

}
