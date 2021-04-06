package com.models;

public class Notification18DaysModel {


    int NotificationLogId;
    String DriverId;
    String DriverName;
    String NotificationTypeId;
    String NotificationTypeName;
    String Title;
    String Message;
    String ImagePath;
    String SendDate;
    String CompanyId;
    String Type;

    public Notification18DaysModel(int notificationLogId, String driverId, String driverName,
                              String notificationTypeId, String notificationTypeName, String title,
                              String message, String imagePath,  String sendDate,
                              String companyId, String type) {
        NotificationLogId = notificationLogId;
        DriverId = driverId;
        DriverName = driverName;
        NotificationTypeId = notificationTypeId;
        NotificationTypeName = notificationTypeName;
        Title = title;
        Message = message;
        ImagePath = imagePath;
        SendDate = sendDate;
        CompanyId = companyId;
        Type      = type;
    }

    public int getNotificationLogId() {
        return NotificationLogId;
    }

    public String getDriverId() {
        return DriverId;
    }

    public String getDriverName() {
        return DriverName;
    }


    public String getNotificationTypeId() {
        return NotificationTypeId;
    }

    public String getNotificationTypeName() {
        return NotificationTypeName;
    }

    public String getTitle() {
        return Title;
    }

    public String getMessage() {
        return Message;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public String getSendDate() {
        return SendDate;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public String getType() {
        return Type;
    }
}
