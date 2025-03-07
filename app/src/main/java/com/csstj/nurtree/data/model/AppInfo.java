package com.csstj.nurtree.data.model;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppInfo {

    private String appName;
    private Drawable appIcon;
    private String appUsage;

    public AppInfo(String appName, Drawable appIcon, String appUsage){
        this.appName = appName;
        this.appIcon = appIcon;
        this.appUsage = appUsage;
    }

    public AppInfo(String appName, Drawable appIcon){
        this.appName = appName;
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppUsage() {
        return appUsage;
    }

    public void setAppUsage(String appUsage) {
        Log.d("AppInfo", "appUsage: " + appUsage);
        this.appUsage = appUsage;
    }
}
