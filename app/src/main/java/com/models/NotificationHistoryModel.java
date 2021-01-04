package com.models;

import org.joda.time.DateTime;

public class NotificationHistoryModel  implements Comparable {

    int Id;
    String NotificationTitle;
    String NotificationDetails;
    String notificationDateTime;
    boolean isRead;


    public NotificationHistoryModel(int id, String notificationTitle, String notificationDetails, String notificationDateTime, boolean isRead) {
        Id = id;
        NotificationTitle = notificationTitle;
        NotificationDetails = notificationDetails;
        this.notificationDateTime = notificationDateTime;
        this.isRead = isRead;
    }

    public int getId() {
        return Id;
    }

    public String getNotificationTitle() {
        return NotificationTitle;
    }

    public String getNotificationDetails() {
        return NotificationDetails;
    }

    public String getNotificationDateTime() {
        return notificationDateTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setReadStatus(boolean read) {
        isRead = read;
    }

    @Override
    public int compareTo(Object o) {
        int compareId=((NotificationHistoryModel)o).getId();

        /* For Ascending order*/
       // return this.Id-compareId;


        /* For Descending order  */
        return compareId-this.Id;

    }
}
