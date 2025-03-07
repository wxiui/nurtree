package com.csstj.nurtree.common;

import android.content.Context;
import android.content.SharedPreferences;

public class AppCodeManager {

    private static final String PREFS_NAME = "AppCodePrefs";
    private static final String KEY_APP_CODE = "app_code";
    private static final String KEY_APP_PHONE = "app_phone";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NICKNAME = "nickname";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AppCodeManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    // 保存唯一码到 SharedPreferences
    public void saveAppCode(String appCode) {
        editor.putString(KEY_APP_CODE, appCode);
        editor.apply();
    }

    // 保存家长手机号到 SharedPreferences
    public void savePhone(String phone) {
        editor.putString(KEY_APP_CODE, phone);
        editor.apply();
    }

    // 从 SharedPreferences 获取唯一码
    public String getAppCode() {
        return sharedPreferences.getString(KEY_APP_CODE, null);
    }

    // 从 SharedPreferences 获取家长手机号
    public String getPhone() {
        return sharedPreferences.getString(KEY_APP_PHONE, null);
    }

    // 检查是否已登录
    public boolean isLoggedIn() {
        String phone = sharedPreferences.getString(KEY_NICKNAME, null);
        boolean isLoggedIn = false;
        if(phone != null){
            isLoggedIn = true;
        }
        return isLoggedIn;
    }

    // 保存登录信息
    public void saveLoginInfo(String username, String password, String nickname) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_NICKNAME, nickname);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getNickname() {
        return sharedPreferences.getString(KEY_NICKNAME, null);
    }
}