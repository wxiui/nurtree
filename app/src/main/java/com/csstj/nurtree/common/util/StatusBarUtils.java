package com.csstj.nurtree.common.util;

import android.content.Context;
import android.content.res.Resources;

public class StatusBarUtils {
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        // 获取系统资源类
        Resources resources = context.getResources();
        // 获取状态栏高度的资源 ID
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            // 根据资源 ID 获取状态栏高度
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}