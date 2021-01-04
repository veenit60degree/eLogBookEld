package com.shared.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.models.EldDataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EldSharedPref {

    public static final String PREFS_NAME = "eld";
    public static final String UPDATE_LOCATION = "driver_location";



    public EldSharedPref() {
        super();
    }


    public static void SaveDriverLoc(Context context, List<EldDataModel> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(UPDATE_LOCATION, jsonFavorites);

        editor.commit();
    }




    public static ArrayList<EldDataModel> LoadSavedLoc(Context context) {
        SharedPreferences settings;
        List<EldDataModel> favorites;
        ArrayList<EldDataModel> emptyList = new ArrayList<EldDataModel>();

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            EldDataModel[] favoriteItems = gson.fromJson(jsonFavorites,EldDataModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<EldDataModel>(favorites);
        } else
            return emptyList;

        return (ArrayList<EldDataModel>) favorites;
    }



    /*Add values in pref list*/
    public void AddDriverLoc(Context context, EldDataModel LocationModelList) {
        List<EldDataModel> favorites = LoadSavedLoc(context);
        if (favorites == null)
            favorites = new ArrayList<EldDataModel>();
        favorites.add(LocationModelList);
        SaveDriverLoc(context, favorites);
    }


    /*   Clear saved data from list  */
    public ArrayList<EldDataModel> ClearLocFromList(Context context) {
        SharedPreferences settings;
        List<EldDataModel> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            EldDataModel[] favoriteItems = gson.fromJson(jsonFavorites,EldDataModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<EldDataModel>(favorites);
        } else
            return null;

        favorites.clear();
        SaveDriverLoc(context, favorites);
        return (ArrayList<EldDataModel>) favorites;
    }




}
