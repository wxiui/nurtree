package com.csstj.nurtree.common.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.csstj.nurtree.common.consts.Whitelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    private static final String PREFS_NAME = "AppCodePrefs";
    private static final String KEY_WHITELIST = "whitelist";
    private static final String KEY_BLACKLIST = "blacklist";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Whitelist wlist;

    public ConfigManager(Context context) {
        wlist = new Whitelist();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    // 保存唯一码到 SharedPreferences
    public void saveWhitelist(String whitelist) {
        editor.putString(KEY_WHITELIST, whitelist);
        editor.apply();
    }
    public List<String> getWhitelist() {
        List<String> whitelist = new ArrayList<>();
        List<String> list0 = wlist.getWhitelist();
        whitelist.addAll(list0);
        String str = sharedPreferences.getString(KEY_WHITELIST, "");
        // 使用 split 方法将字符串按逗号分割成数组
        String[] array = str.split(",");
        // 使用 Arrays.asList 将数组转换为 List
        List<String> list = Arrays.asList(array);
        whitelist.addAll(list);
        return whitelist;
    }

    public void saveBlacklist(String whitelist) {
        editor.putString(KEY_BLACKLIST, whitelist);
        editor.apply();
    }

    public List<String> getBlacklist() {
        List<String> blacklist = new ArrayList<>();
        String str = sharedPreferences.getString(KEY_BLACKLIST, "");
        // 使用 split 方法将字符串按逗号分割成数组
        String[] array = str.split(",");
        // 使用 Arrays.asList 将数组转换为 List
        List<String> list = Arrays.asList(array);
        blacklist.addAll(list);
        return blacklist;
    }
}
