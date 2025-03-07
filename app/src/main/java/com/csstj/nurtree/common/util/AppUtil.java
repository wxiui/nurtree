package com.csstj.nurtree.common.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.csstj.nurtree.data.model.AppInfo;


public class AppUtil {

    private static final String TAG = "AppUtil";

    private PackageManager packageManager;

    public AppUtil(PackageManager packageManager){
        this.packageManager = packageManager;
    }

    public AppInfo getAppInfo(String packageName){
        try {
            AppInfo appInfo = null;
            ApplicationInfo applicationInfo = null;
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            // 加载应用名称
            String appName = applicationInfo.loadLabel(packageManager).toString();
            // 加载应用图标
            Drawable appIcon = applicationInfo.loadIcon(packageManager);
            // 这里可以将Drawable转换为Bitmap或其他格式进行使用或显示

            appInfo = new AppInfo(appName,appIcon);
            return appInfo;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "getAppInfo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public AppInfo getAppInfo(ApplicationInfo applicationInfo){
        AppInfo appInfo = null;
        // 加载应用名称
        String appName = applicationInfo.loadLabel(packageManager).toString();
        // 加载应用图标
        Drawable appIcon = applicationInfo.loadIcon(packageManager);
        // 这里可以将Drawable转换为Bitmap或其他格式进行使用或显示

        appInfo = new AppInfo(appName,appIcon);
        return appInfo;
    }
}
