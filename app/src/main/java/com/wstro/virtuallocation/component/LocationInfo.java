package com.wstro.virtuallocation.component;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ClassName: LocationInfo
 * Function:
 * Date:     2017/10/9 0009 14:28
 *
 * @author Administrator
 * @see
 */
public class LocationInfo implements Parcelable {
    private double latitude;
    private double longitude;

    private String addrStr;
    private String cityName;


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddrStr() {
        return addrStr;
    }

    public void setAddrStr(String addrStr) {
        this.addrStr = addrStr;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        return "LocationInfo{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", addrStr='" + addrStr + '\'' +
                ", cityName='" + cityName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.addrStr);
        dest.writeString(this.cityName);
    }

    public LocationInfo() {
    }

    protected LocationInfo(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.addrStr = in.readString();
        this.cityName = in.readString();
    }

    public static final Creator<LocationInfo> CREATOR = new Creator<LocationInfo>() {
        @Override
        public LocationInfo createFromParcel(Parcel source) {
            return new LocationInfo(source);
        }

        @Override
        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };
}
