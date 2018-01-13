package com.wstro.virtuallocation.data.model;

import com.wstro.virtuallocation.component.LocationInfo;

import java.util.List;

/**
 * @author pengl
 */

public class RealData {

    private List<CellInfo> cellInfoList;

    private List<WifiInfo> wifiInfoList;

    private LocationInfo locationInfo;

    public RealData(List<CellInfo> cellInfoList, List<WifiInfo> wifiInfoList) {
        this.cellInfoList = cellInfoList;
        this.wifiInfoList = wifiInfoList;
    }

    public List<CellInfo> getCellInfoList() {
        return cellInfoList;
    }

    public void setCellInfoList(List<CellInfo> cellInfoList) {
        this.cellInfoList = cellInfoList;
    }

    public List<WifiInfo> getWifiInfoList() {
        return wifiInfoList;
    }

    public void setWifiInfoList(List<WifiInfo> wifiInfoList) {
        this.wifiInfoList = wifiInfoList;
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }
}
