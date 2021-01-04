package com.shared.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.driver.details.EldDriverLogModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EldCoDriverLogPref {

    public static final String PREFS_NAME = "eld_co_driver_log";
    public static final String UPDATE_LOCATION = "co_driver_driver_log";


    public EldCoDriverLogPref() {
        super();
    }


    public void SaveCoDriverLog(Context context, List<EldDriverLogModel> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(UPDATE_LOCATION, jsonFavorites);

        editor.commit();
    }




    public List<EldDriverLogModel> LoadSavedLoc(Context context) {
        SharedPreferences settings;
        List<EldDriverLogModel> favorites;
        List<EldDriverLogModel> emptyList = new ArrayList<EldDriverLogModel>();

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            EldDriverLogModel[] favoriteItems = gson.fromJson(jsonFavorites,EldDriverLogModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<EldDriverLogModel>(favorites);
        } else
            return emptyList;

        return (ArrayList<EldDriverLogModel>) favorites;
    }



    /*Add values in pref list*/
    public void AddDriverLoc(Context context, EldDriverLogModel LocationModelList) {
        List<EldDriverLogModel> favorites = LoadSavedLoc(context);
        if (favorites == null)
            favorites = new ArrayList<EldDriverLogModel>();
        favorites.add(LocationModelList);
        SaveCoDriverLog(context, favorites);
    }


    /*   Clear saved data from list  */
    public List<EldDriverLogModel> ClearCoLogFromList(Context context) {
        SharedPreferences settings;
        List<EldDriverLogModel> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            EldDriverLogModel[] favoriteItems = gson.fromJson(jsonFavorites,EldDriverLogModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<EldDriverLogModel>(favorites);
        } else
            return null;

        favorites.clear();
        SaveCoDriverLog(context, favorites);
        return (ArrayList<EldDriverLogModel>) favorites;
    }



}
