package com.shared.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.models.NotificationHistoryModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoNotificationPref {

    public static final String NOTI_CO_PREFS_NAME      = "co_notification_name";
    public static final String UPDATE_CO_NOTIFICATION  = "co_notification_loc";



    public CoNotificationPref() {
        super();
    }


    /*Save notifications in pref list*/
    public void SaveNotifications(Context context, List<NotificationHistoryModel> notificationList) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(NOTI_CO_PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(notificationList);

        editor.putString(UPDATE_CO_NOTIFICATION, jsonFavorites);

        editor.commit();
    }


    /*Get notification in pref list*/
    public ArrayList<NotificationHistoryModel> GetNotificationsList(Context context) {
        SharedPreferences settings;
        List<NotificationHistoryModel> notificationList;
        ArrayList<NotificationHistoryModel> emptyList = new ArrayList<NotificationHistoryModel>();

        settings = context.getSharedPreferences(NOTI_CO_PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_CO_NOTIFICATION)) {
            String jsonFavorites = settings.getString(UPDATE_CO_NOTIFICATION, null);
            Gson gson = new Gson();
            NotificationHistoryModel[] favoriteItems = gson.fromJson(jsonFavorites,NotificationHistoryModel[].class);
            notificationList = Arrays.asList(favoriteItems);
            notificationList = new ArrayList<NotificationHistoryModel>(notificationList);
        } else
            return emptyList;

        return (ArrayList<NotificationHistoryModel>) notificationList;
    }



    /*Add notification in pref list*/
    public void AddNotification(Context context, NotificationHistoryModel LocationModelList) {
        List<NotificationHistoryModel> favorites = GetNotificationsList(context);
        if (favorites == null)
            favorites = new ArrayList<NotificationHistoryModel>();
        favorites.add(LocationModelList);
        SaveNotifications(context, favorites);
    }



    /*   Clear notification data from list  */
    public ArrayList<NotificationHistoryModel> RemoveNotification(Context context) {
        SharedPreferences settings;
        List<NotificationHistoryModel> notificationList;

        settings = context.getSharedPreferences(NOTI_CO_PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_CO_NOTIFICATION)) {
            String jsonFavorites = settings.getString(UPDATE_CO_NOTIFICATION, null);
            Gson gson = new Gson();
            NotificationHistoryModel[] favoriteItems = gson.fromJson(jsonFavorites,NotificationHistoryModel[].class);
            notificationList = Arrays.asList(favoriteItems);
            notificationList = new ArrayList<NotificationHistoryModel>(notificationList);
        } else
            return null;

        notificationList.clear();
        SaveNotifications(context, notificationList);
        return (ArrayList<NotificationHistoryModel>) notificationList;
    }



}
