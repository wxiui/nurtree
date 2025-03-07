package com.csstj.nurtree.data.model;

public class ProfileInfo {

    private int profileIcon;
    private String profileText;
    private int profileArrow;

    public ProfileInfo(int profileIcon,String profileText,int profileArrow){
        this.profileIcon = profileIcon;
        this.profileText = profileText;
        this.profileArrow = profileArrow;
    }
    public int getProfileIcon() {
        return profileIcon;
    }

    public void setProfileIcon(int profileIcon) {
        this.profileIcon = profileIcon;
    }

    public String getProfileText() {
        return profileText;
    }

    public void setProfileText(String profileText) {
        this.profileText = profileText;
    }

    public int getProfileArrow() {
        return profileArrow;
    }

    public void setProfileArrow(int profileArrow) {
        this.profileArrow = profileArrow;
    }
}
