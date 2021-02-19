package com.shared.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.models.TimeZoneModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TimeZonePrefManager {


    public static final String PREFS_NAME = "time_zone";
    public static final String UPDATE_LOCATION = "zone";



    public TimeZonePrefManager() {
        super();
    }


    /*Save values in pref list*/
    public void SaveTimeZone(Context context, List<TimeZoneModel> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(UPDATE_LOCATION, jsonFavorites);

        editor.commit();
    }


    /*Get values in pref list*/
    public ArrayList<TimeZoneModel> GetTimeZone(Context context) {
        SharedPreferences settings;
        List<TimeZoneModel> favorites;
        ArrayList<TimeZoneModel> emptyList = new ArrayList<TimeZoneModel>();

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            TimeZoneModel[] favoriteItems = gson.fromJson(jsonFavorites,TimeZoneModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<TimeZoneModel>(favorites);
        } else
            return emptyList;

        return (ArrayList<TimeZoneModel>) favorites;
    }



    /*Add values in pref list*/
    public void AddTimeZone(Context context, TimeZoneModel LocationModelList) {
        List<TimeZoneModel> favorites = GetTimeZone(context);
        if (favorites == null)
            favorites = new ArrayList<TimeZoneModel>();
        favorites.add(LocationModelList);
        SaveTimeZone(context, favorites);
    }



    /*   Clear saved data from list  */
    public ArrayList<TimeZoneModel> RemoveTimeZoneFromList(Context context) {
        SharedPreferences settings;
        List<TimeZoneModel> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            TimeZoneModel[] favoriteItems = gson.fromJson(jsonFavorites,TimeZoneModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<TimeZoneModel>(favorites);
        } else
            return null;

        favorites.clear();
        SaveTimeZone(context, favorites);
        return (ArrayList<TimeZoneModel>) favorites;
    }


}
