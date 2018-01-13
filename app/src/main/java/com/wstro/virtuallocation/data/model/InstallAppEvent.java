package com.wstro.virtuallocation.data.model;

import java.util.List;

/**
 * @author pengl
 */

public class InstallAppEvent {
    private List<AppInfo> list;

    public InstallAppEvent(List<AppInfo> list){
        this.list = list;
    }

    public List<AppInfo> getList() {
        return list;
    }
}
