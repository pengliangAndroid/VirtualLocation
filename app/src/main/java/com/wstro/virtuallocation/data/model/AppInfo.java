package com.wstro.virtuallocation.data.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AppInfo implements Parcelable {
    @Id
    private Long id;

    @Property
    private String appName;

    @Property
    private String packageName;

    @Transient
    private Drawable icon;

    @Property
    private String apkFilePath;

    @Property
    private int versionCode;

    @Property
    private String versionName;

    @Transient
    private boolean isFirstOpen;

    @Property
    private int userId;

    @Generated(hash = 681781123)
    public AppInfo(Long id, String appName, String packageName, String apkFilePath,
            int versionCode, String versionName, int userId) {
        this.id = id;
        this.appName = appName;
        this.packageName = packageName;
        this.apkFilePath = apkFilePath;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.userId = userId;
    }

    @Generated(hash = 1656151854)
    public AppInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }


    public boolean isFirstOpen() {
        return isFirstOpen;
    }

    public void setFirstOpen(boolean firstOpen) {
        isFirstOpen = firstOpen;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    @Override
    public int hashCode() {
        return packageName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AppInfo))
            return false;

        AppInfo info = (AppInfo) obj;
        boolean equals = this.packageName.equals(info.getPackageName());
        return equals;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.appName);
        dest.writeString(this.packageName);
        dest.writeString(this.apkFilePath);
        dest.writeInt(this.versionCode);
        dest.writeString(this.versionName);
        dest.writeByte(this.isFirstOpen ? (byte) 1 : (byte) 0);
        dest.writeInt(this.userId);
    }

    protected AppInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.appName = in.readString();
        this.packageName = in.readString();
        this.apkFilePath = in.readString();
        this.versionCode = in.readInt();
        this.versionName = in.readString();
        this.isFirstOpen = in.readByte() != 0;
        this.userId = in.readInt();
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel source) {
            return new AppInfo(source);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };
}
