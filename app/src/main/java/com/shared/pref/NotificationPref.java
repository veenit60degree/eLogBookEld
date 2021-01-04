package com.shared.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.models.NotificationHistoryModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationPref {

    public static final String NOTI_PREFS_NAME      = "notification_name";
    public static final String UPDATE_NOTIFICATION  = "notification_loc";



    public NotificationPref() {
        super();
    }


    /*Save notifications in pref list*/
    public void SaveNotifications(Context context, List<NotificationHistoryModel> notificationList) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(NOTI_PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(notificationList);

        editor.putString(UPDATE_NOTIFICATION, jsonFavorites);

        editor.commit();
    }


    /*Get notification in pref list*/
    public List<NotificationHistoryModel> GetNotificationsList(Context context) {
        SharedPreferences settings;
        List<NotificationHistoryModel> notificationList;
        ArrayList<NotificationHistoryModel> emptyList = new ArrayList<NotificationHistoryModel>();

        settings = context.getSharedPreferences(NOTI_PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_NOTIFICATION)) {
            String jsonFavorites = settings.getString(UPDATE_NOTIFICATION, null);
            Gson gson = new Gson();
            try {
                NotificationHistoryModel[] favoriteItems= gson.fromJson(jsonFavorites,NotificationHistoryModel[].class);
                notificationList = Arrays.asList(favoriteItems);
                notificationList = new ArrayList<NotificationHistoryModel>(notificationList);
            }catch (Exception e){
                e.printStackTrace();
               return emptyList;
            }



        } else
            return emptyList;

        return  notificationList;
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

        settings = context.getSharedPreferences(NOTI_PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_NOTIFICATION)) {
            String jsonFavorites = settings.getString(UPDATE_NOTIFICATION, null);
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
