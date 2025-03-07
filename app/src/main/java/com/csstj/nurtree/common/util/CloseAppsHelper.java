package com.csstj.nurtree.common.util;

import android.content.Context;
import android.content.Intent;

public class CloseAppsHelper {
    private Context context;

    public CloseAppsHelper(Context context){
        this.context = context;
    }

    /**
     * HOME键
     */
    public void goToHomeScreen() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
