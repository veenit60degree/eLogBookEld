package com.models;

public class PermissionInfoModel {

    String Title;
    String Desc;
    int drawable;

    public PermissionInfoModel(String Title, String Desc, int drawable) {
        this.Title = Title;
        this.Desc = Desc;
        this.drawable = drawable;
    }


    public String getTitle() {
        return Title;
    }

    public String getDesc() {
        return Desc;
    }

    public int getDrawable() {
        return drawable;
    }
}
