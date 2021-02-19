package com.shared.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.models.CycleModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class USCyclePrefManager {


    public static final String PREFS_NAME = "us_cycle";
    public static final String UPDATE_LOCATION = "us";



    public USCyclePrefManager() {
        super();
    }


    /*Save values in pref list*/
    public void SaveCycles(Context context, List<CycleModel> favorites) {
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
    public ArrayList<CycleModel> GetCycles(Context context) {
        SharedPreferences settings;
        List<CycleModel> favorites;
        ArrayList<CycleModel> emptyList = new ArrayList<CycleModel>();

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            CycleModel[] favoriteItems = gson.fromJson(jsonFavorites,CycleModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<CycleModel>(favorites);
        } else
            return emptyList;

        return (ArrayList<CycleModel>) favorites;
    }



    /*Add values in pref list*/
    public void AddCycle(Context context, CycleModel LocationModelList) {
        List<CycleModel> favorites = GetCycles(context);
        if (favorites == null)
            favorites = new ArrayList<CycleModel>();
        favorites.add(LocationModelList);
        SaveCycles(context, favorites);
    }



    /*   Clear saved data from list  */
    public ArrayList<CycleModel> RemoveCycleFromList(Context context) {
        SharedPreferences settings;
        List<CycleModel> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            CycleModel[] favoriteItems = gson.fromJson(jsonFavorites,CycleModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<CycleModel>(favorites);
        } else
            return null;

        favorites.clear();
        SaveCycles(context, favorites);
        return (ArrayList<CycleModel>) favorites;
    }




}
