package com.csstj.nurtree.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.csstj.nurtree.data.model.MinCircleInfo;


public class TimeManager {

    private static final String PREFS_NAME = "TimePrefs";
    private static final String KEY_BUILT_MINS = "built_mins"; //内置分钟，不答题就能用的时间
    private static final String KEY_LIMIT_BONUS_MINS = "limit_bonus_mins"; //奖励限制时间
    private static final String KEY_BONUS_MINS = "bonus_mins"; //奖励时间
    private static final String KEY_APP_BANNED = "app_banned"; //app禁用

    private static final String KEY_MONITOR_NUMS = "monitor_nums";
    private static final String KEY_BANNED_NUMS = "banned_nums"; //app禁用
    private static final String KEY_MONITOR = "monitor";

    private int builtDefValue = 300;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public TimeManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    // 保存唯一码到 SharedPreferences
    public void saveSysMins(int builtMins, int limitBonusMins) {
        editor.putInt(KEY_BUILT_MINS,builtMins);
        editor.putInt(KEY_LIMIT_BONUS_MINS,limitBonusMins);
        editor.apply();
    }

    // 保存唯一码到 SharedPreferences
    public void saveBonusMins(int bonusMins) {
        bonusMins = sharedPreferences.getInt(KEY_BONUS_MINS,0) + bonusMins;
        //奖励限制时间
        int limitBonusMins = sharedPreferences.getInt(KEY_LIMIT_BONUS_MINS,builtDefValue);
        if(bonusMins>limitBonusMins){
            bonusMins = limitBonusMins;
        }
        editor.putInt(KEY_BONUS_MINS, bonusMins);
        editor.apply();
    }

    // 从 SharedPreferences 获取唯一码
    public int getMinleft(int usageMins) {
        //总分钟数
        int totalmins = sharedPreferences.getInt(KEY_BUILT_MINS,builtDefValue)+sharedPreferences.getInt(KEY_BONUS_MINS,0);
        //剩余时间
        int minleft = totalmins-usageMins;

        if(minleft <= 0){
            minleft = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_APP_BANNED,true);
            editor.apply();
        }
        return minleft;
    }

    public MinCircleInfo getUsageInfo(int usageMins){
        //总分钟数
        int totalmins = sharedPreferences.getInt(KEY_BUILT_MINS,builtDefValue)+sharedPreferences.getInt(KEY_BONUS_MINS,0);
        if(totalmins == 0){
            return new MinCircleInfo(0,0,100);
        }
        //剩余时间
        int minleft = totalmins-usageMins;
        if(minleft <= 0){
            minleft = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_APP_BANNED,true);
            editor.apply();
        }
        //使用完成进度
        int progress = (totalmins-minleft)*100/totalmins;
        return new MinCircleInfo(totalmins,minleft,progress);
    }

    public void unblockBanApp(){
        editor.putBoolean(KEY_APP_BANNED,false);
        editor.apply();
    }

    public boolean isBanApp(int usageMins){
        boolean isBanApp = sharedPreferences.getBoolean(KEY_APP_BANNED,false);
        if (!isBanApp){
            //剩余时间
            int minleft;
            minleft = getMinleft(usageMins);
            if(minleft == 0){
                isBanApp = true;
            }
        }
        return isBanApp;
    }

    public void saveBannedNums(){
        int bannedNums = sharedPreferences.getInt(KEY_BANNED_NUMS,0);
        bannedNums++;
        editor.putInt(KEY_BANNED_NUMS,bannedNums);
        editor.apply();
    }
    public int getBannedNums() {
        //总分钟数
        return sharedPreferences.getInt(KEY_BANNED_NUMS,0);
    }

    public void saveMonitorNums(){
        int bannedNums = sharedPreferences.getInt(KEY_MONITOR_NUMS,0);
        bannedNums++;
        editor.putInt(KEY_MONITOR_NUMS,bannedNums);
        editor.apply();
    }
    public int getMonitorNums() {
        //总分钟数
        return sharedPreferences.getInt(KEY_MONITOR_NUMS,0);
    }
    public void saveMonitor(String monitor){
        editor.putString(KEY_MONITOR,monitor);
        editor.apply();
    }
    public String getMonitor() {
        return sharedPreferences.getString(KEY_MONITOR,null);
    }
}
