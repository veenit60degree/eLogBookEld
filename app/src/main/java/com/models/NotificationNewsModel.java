package com.models;

public class NotificationNewsModel {

    String notificationTitle;
    String notificationDesc;

    public NotificationNewsModel(String notificationTitle, String notificationDesc) {
        this.notificationTitle = notificationTitle;
        this.notificationDesc = notificationDesc;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationDesc() {
        return notificationDesc;
    }


}
