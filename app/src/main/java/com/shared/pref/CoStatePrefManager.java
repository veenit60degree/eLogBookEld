package com.shared.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.models.DriverLocationModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CoStatePrefManager {

    public static final String PREFS_NAME = "co_state_name";
    public static final String UPDATE_LOCATION = "co_state_loc";



    public CoStatePrefManager() {
        super();
    }


    /*Save values in pref list*/
    public void SaveCoState(Context context, List<DriverLocationModel> favorites) {
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
    public ArrayList<DriverLocationModel> GetCoState(Context context) {
        SharedPreferences settings;
        List<DriverLocationModel> favorites;
        ArrayList<DriverLocationModel> emptyList = new ArrayList<DriverLocationModel>();

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            DriverLocationModel[] favoriteItems = gson.fromJson(jsonFavorites,DriverLocationModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<DriverLocationModel>(favorites);
        } else
            return emptyList;

        return (ArrayList<DriverLocationModel>) favorites;
    }



    /*Add values in pref list*/
    public void AddCoState(Context context, DriverLocationModel LocationModelList) {
        List<DriverLocationModel> favorites = GetCoState(context);
        if (favorites == null)
            favorites = new ArrayList<DriverLocationModel>();
        favorites.add(LocationModelList);
        SaveCoState(context, favorites);
    }



    /*   Clear saved data from list  */
    public ArrayList<DriverLocationModel> RemoveCoState(Context context) {
        SharedPreferences settings;
        List<DriverLocationModel> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            DriverLocationModel[] favoriteItems = gson.fromJson(jsonFavorites,DriverLocationModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<DriverLocationModel>(favorites);
        } else
            return null;

        favorites.clear();
        SaveCoState(context, favorites);
        return (ArrayList<DriverLocationModel>) favorites;
    }
}
