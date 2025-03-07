package com.csstj.nurtree.common.helper;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.csstj.nurtree.common.TimeManager;
import com.csstj.nurtree.common.consts.Whitelist;
import com.csstj.nurtree.common.manager.ConfigManager;

import java.util.Calendar;
import java.util.List;

public class AppMonitorHelper {

    public static final String ACTION_NOTIFY_UI = "com.csstj.nurtree.client.ACTION_NOTIFY_UI";
    private static final String TAG = "AppMonitorHelper";

    private ConfigManager configManager;
    private Context context;
    private PackageManager packageManager;
    private TimeManager timeManager;

    public AppMonitorHelper(Context context){
        this.context = context;
        packageManager = context.getPackageManager();
        timeManager = new TimeManager(context);
        configManager = new ConfigManager(context);
    }

    public boolean isBlacklist(String packageName){
        if(packageName.equals(context.getPackageName())){
            return false;
        }else if(configManager.getBlacklist().contains(packageName)){
            return true;
        }
        return false;
    }

    private boolean isWhitelist(String packageName){
        if(configManager.getWhitelist().contains(packageName)){
            return true;
        }
        return false;
    }


    /**
     * 获取当天的使用时间（单位：毫秒）
     * 过滤系统应用，白名单应用，后台时间
     * @return
     */
    public long getTodayUsageTime() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        // 查询使用统计
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        long totalUsageTime = 0;
        PackageManager packageManager = context.getPackageManager();
        for (UsageStats usageStats : stats) {

            String packageName = usageStats.getPackageName();
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                // 过滤掉系统应用
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //过滤白名单应用
                    if (!isWhitelist(packageName)){
                        // 过滤掉后台时间,只统计前台时间
                        totalUsageTime += usageStats.getTotalTimeInForeground();
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "ApplicationInfo: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return totalUsageTime;
    }

    public long getTodayUsageMins(){
        long todayUsageTime = getTodayUsageTime();
        return todayUsageTime / ( 1000*60 );
    }

    /**
     * 是否系统应用
     * @param packageName
     * @return
     */
    private boolean isSysApp(String packageName){
        boolean isSysApp = true;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            // 过滤掉系统应用
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 只统计前台时间
                isSysApp = false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "ApplicationInfo: " + e.getMessage());
            e.printStackTrace();
        }
        return isSysApp;
    }

    public boolean isBanApp(String packageName){
        boolean isBanApp = false;
        if (!context.getPackageName().equals(packageName)) {
            //saveMonitorNums();
            //timeManager.saveMonitor(packageName);
//            try {
//                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
//                String appName = applicationInfo.loadLabel(packageManager).toString();
//                timeManager.saveMonitor(appName+":"+packageName);
//                // 过滤掉系统应用
//            } catch (PackageManager.NameNotFoundException e) {
//                Log.d(TAG, "ApplicationInfo: " + e.getMessage());
//                e.printStackTrace();
//            }
            // 如果当前应用不是目标应用，停止某些操作
            //if(!isSysApp(packageName)){

            if(!isWhitelist(packageName)){
                long todayUsageMins = getTodayUsageMins();
                if(timeManager.isBanApp((int) todayUsageMins)){
                    isBanApp = true;
                }
            }

                //timeManager.saveBannedNums();
            //}
        }
        return isBanApp;
    }

    public void saveMonitorNums(){
        timeManager.saveMonitorNums();
    }
}
