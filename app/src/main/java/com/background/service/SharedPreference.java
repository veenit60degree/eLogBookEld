package com.background.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SharedPreference {

    public static final String PREFS_NAME = "logistic";
    public static final String UPDATE_LOCATION = "location";


    
    
    
    public SharedPreference() {
        super();
    }


    public static void storeDriverLocations(Context context, List<LocationModel> favorites) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(UPDATE_LOCATION, jsonFavorites);

        editor.commit();
    }



    
    
    public static ArrayList<LocationModel> loadSavedLocations(Context context) {
        SharedPreferences settings;
        List<LocationModel> favorites;
        ArrayList<LocationModel> emptyList = new ArrayList<LocationModel>();

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            LocationModel[] favoriteItems = gson.fromJson(jsonFavorites,LocationModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<LocationModel>(favorites);
        } else
            return emptyList;

        return (ArrayList<LocationModel>) favorites;
    }


    
    /*Add values in pref list*/
    public void addDriverLocation(Context context, LocationModel LocationModelList) {
        List<LocationModel> favorites = loadSavedLocations(context);
        if (favorites == null)
            favorites = new ArrayList<LocationModel>();
        favorites.add(LocationModelList);
        storeDriverLocations(context, favorites);
    }


    
    
    /*Remove values from pref list*/
    public void removePhoneNumber(Context context, LocationModel beanSampleList) {
        ArrayList<LocationModel> favorites = loadSavedLocations(context);
        if (favorites != null) {
            favorites.remove(beanSampleList);
            storeDriverLocations(context, favorites);
        }
    }

    

  
    

    
/*   Clear saved data from list  */
    public static ArrayList<LocationModel> clearLocationFromList(Context context) {
        SharedPreferences settings;
        List<LocationModel> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(UPDATE_LOCATION)) {
            String jsonFavorites = settings.getString(UPDATE_LOCATION, null);
            Gson gson = new Gson();
            LocationModel[] favoriteItems = gson.fromJson(jsonFavorites,LocationModel[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<LocationModel>(favorites);
        } else
            return null;

        favorites.clear();
        storeDriverLocations(context, favorites);
        return (ArrayList<LocationModel>) favorites;
    }
    

    

    
    
    
   
}
