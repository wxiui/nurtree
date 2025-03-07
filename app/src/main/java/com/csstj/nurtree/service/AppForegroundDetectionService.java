package com.csstj.nurtree.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.csstj.nurtree.R;
import com.csstj.nurtree.common.consts.Whitelist;
import com.csstj.nurtree.common.helper.AppMonitorHelper;
import com.csstj.nurtree.common.util.CloseAppsHelper;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class AppForegroundDetectionService extends Service {
    private static final String TAG = "AppForegroundDetectionService";
    private Handler handler;
    private Runnable runnable;
    private static final long CHECK_INTERVAL = 2000; // 每2秒检查一次

    private static final int NOTIFICATION_ID = 1;

    private Whitelist whitelist = new Whitelist();

    private int count = 0;

    private static final String CHANNEL_ID = "my_channel_id";

    private CloseAppsHelper closeAppsHelper;

    private AppMonitorHelper appMonitorHelper;

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        closeAppsHelper = new CloseAppsHelper(getApplicationContext());
        appMonitorHelper = new AppMonitorHelper(getApplicationContext());
        // 创建通知渠道（Android 8.0 及以上版本需要）
        createNotificationChannel();
        // 创建通知
        Notification notification = buildNotification();
        try {
            // 启动前台服务
            startForeground(NOTIFICATION_ID, notification);
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "startForeground: " + e.getMessage());
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkForegroundApp();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 这里可以添加服务的具体逻辑，例如定时任务等
        // 服务被系统杀死后，会尝试自动重启
        handler.post(runnable);
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Foreground Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("服务运行中")
                .setContentText("前台服务正在运行")
                .setSmallIcon(R.drawable.ic_notification)
                .build();
    }

    @SuppressLint("ForegroundServiceType")
    private void checkForegroundApp() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                currentTime - CHECK_INTERVAL, currentTime);
        if (usageStatsList != null && !usageStatsList.isEmpty()) {
            SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();
            for (UsageStats usageStats : usageStatsList) {
                sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!sortedMap.isEmpty()) {
                String topPackageName = sortedMap.get(sortedMap.lastKey()).getPackageName();
                String currentPackageName = getPackageName();
                if (!topPackageName.equals(currentPackageName)) {
//                    if(!whitelist.getWhitelist().contains(topPackageName)){
//                        if(count<10){
//                            count++;
//                            closeAppsHelper.goToHomeScreen();
//                        }
//                    }
                    if(appMonitorHelper.isBlacklist(topPackageName)){
                        closeAppsHelper.goToHomeScreen();
                    }else if(appMonitorHelper.isBanApp(topPackageName)){
                        closeAppsHelper.goToHomeScreen();
                    }
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}