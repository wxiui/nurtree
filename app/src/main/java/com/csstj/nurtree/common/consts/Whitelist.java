package com.csstj.nurtree.common.consts;

import java.util.ArrayList;
import java.util.List;

public class Whitelist {

    private List<String> whitelist;

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    public Whitelist(){
        this.whitelist = new ArrayList<>();
        whitelist.add("com.android.settings");
        whitelist.add("com.google.android.apps.nexuslauncher");
        whitelist.add("com.sec.android.app.launcher");
        whitelist.add("com.hihonor.android.launcher");
    }

    public void add(String packageName){
        whitelist.add(packageName);
    }
}
