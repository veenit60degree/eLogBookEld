package com.shared.pref;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.models.EldDataModelNew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoDriverEldPref {

    public static final String PREFS_Name_Co_driver = "co_eld";
    public static final String UPDATE_Co_Driver = "co_driver_location";



    public CoDriverEldPref() {
        super();
    }


    public static void SaveDriverLoc(Context context, List<EldDataModelNew> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_Name_Co_driver, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(UPDATE_Co_Driver, jsonFavorites);

        editor.commit();
    }




    public static ArrayList<EldDataModelNew> LoadSavedLoc(Context context) {
        SharedPreferences settings;
        List<EldDataModelNew> favorites = new ArrayList<>();
        ArrayList<EldDataModelNew> emptyList = new ArrayList<EldDataModelNew>();

        settings = context.getSharedPreferences(PREFS_Name_Co_driver,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_Co_Driver)) {
            String jsonFavorites = settings.getString(UPDATE_Co_Driver, null);
            Gson gson = new Gson();
            EldDataModelNew[] favoriteItems = gson.fromJson(jsonFavorites,EldDataModelNew[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<EldDataModelNew>(favorites);
        } else
            return emptyList;

        return (ArrayList<EldDataModelNew>) favorites;
    }



    /*Add values in pref list*/
    public void AddDriverLoc(Context context, EldDataModelNew LocationModelList) {
        List<EldDataModelNew> favorites = LoadSavedLoc(context);
        if (favorites == null)
            favorites = new ArrayList<EldDataModelNew>();
        favorites.add(LocationModelList);
        SaveDriverLoc(context, favorites);
    }


    /*   Clear saved data from list  */
    public ArrayList<EldDataModelNew> ClearLocFromList(Context context) {
        SharedPreferences settings;
        List<EldDataModelNew> favorites;

        settings = context.getSharedPreferences(PREFS_Name_Co_driver,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_Co_Driver)) {
            String jsonFavorites = settings.getString(UPDATE_Co_Driver, null);
            Gson gson = new Gson();
            EldDataModelNew[] favoriteItems = gson.fromJson(jsonFavorites,EldDataModelNew[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<EldDataModelNew>(favorites);
        } else
            return null;

        favorites.clear();
        SaveDriverLoc(context, favorites);
        return (ArrayList<EldDataModelNew>) favorites;
    }




}
