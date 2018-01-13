package com.wstro.virtuallocation.data.model;

/**
 * @author pengl
 */

public class InstallAppStatusEvent {
    private InstallAppStatus status;

    private String packageName;


    public InstallAppStatusEvent(String packageName,InstallAppStatus status) {
        this.status = status;
        this.packageName = packageName;
    }


    public InstallAppStatus getStatus() {
        return status;
    }

    public String getPackageName() {
        return packageName;
    }
}
