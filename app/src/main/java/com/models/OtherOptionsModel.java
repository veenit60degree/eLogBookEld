package com.models;

public class OtherOptionsModel {

    int drawable;
    int status;
    String title;

    public OtherOptionsModel(int drawable, int status, String title) {
        this.drawable = drawable;
        this.status = status;
        this.title = title;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}
