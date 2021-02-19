package com.shared.pref;


import android.content.Context;
import android.content.SharedPreferences;

import com.models.EldDriverLogModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EldSingleDriverLogPref {


    public String PREFS_NAME_Eld = "eld_driver_log";
    public String UPDATE_Driver_ELD = "driver_driver_log";



    public EldSingleDriverLogPref() {
        super();
    }

    public void SaveDriverLog(Context context, List<EldDriverLogModel> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME_Eld, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(UPDATE_Driver_ELD, jsonFavorites);

        editor.commit();
    }




    public List<EldDriverLogModel> LoadSavedLoc(Context context) {
        SharedPreferences settings;
        List<EldDriverLogModel> favorites;
        List<EldDriverLogModel> emptyList = new ArrayList<EldDriverLogModel>();

        settings = context.getSharedPreferences(PREFS_NAME_Eld,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_Driver_ELD)) {
            String jsonFavorites = settings.getString(UPDATE_Driver_ELD, null);
            Gson gson = new Gson();
            EldDriverLogModel[] favoriteItems = gson.fromJson(jsonFavorites,EldDriverLogModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<EldDriverLogModel>(favorites);
        } else
            return emptyList;

        return (List<EldDriverLogModel>) favorites;
    }



    /*Add values in pref list*/
    public void AddDriverLoc(Context context, EldDriverLogModel LocationModelList) {
        List<EldDriverLogModel> favorites = LoadSavedLoc(context);
        if (favorites == null)
            favorites = new ArrayList<EldDriverLogModel>();
        favorites.add(LocationModelList);
        SaveDriverLog(context, favorites);
    }


    /*   Clear saved data from list  */
    public List<EldDriverLogModel> ClearLogFromList(Context context) {
        SharedPreferences settings;
        List<EldDriverLogModel> favorites;

        settings = context.getSharedPreferences(PREFS_NAME_Eld,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_Driver_ELD)) {
            String jsonFavorites = settings.getString(UPDATE_Driver_ELD, null);
            Gson gson = new Gson();
            EldDriverLogModel[] favoriteItems = gson.fromJson(jsonFavorites,EldDriverLogModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<EldDriverLogModel>(favorites);
        } else
            return null;

        favorites.clear();
        SaveDriverLog(context, favorites);
        return (List<EldDriverLogModel>) favorites;
    }



}
