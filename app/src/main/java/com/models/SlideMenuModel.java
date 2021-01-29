package com.models;

import android.graphics.drawable.Drawable;

public class SlideMenuModel {

    int status;
    int icon;
    String title;

    public SlideMenuModel(int status, int icon, String title) {
        this.status = status;
        this.icon = icon;
        this.title = title;
    }

    public int getStatus(){
        return status;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
