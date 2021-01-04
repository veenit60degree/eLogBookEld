package com.local.db;


import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LatLongHelper {


    public LatLongHelper() {
        super();
    }


    /*-------------------- GET SYNC LOG SAVED Array -------------------- */
    public JSONArray getSavedLatLonArray( DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getLatLonDetails();

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.LAT_LON_LIST));
            try {
                logArray = new JSONArray(logList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!rs.isClosed()) {
            rs.close();
        }

        return logArray;

    }


    /*-------------------- Recap/ViewLog 18 Days DB Helper -------------------- */
    public void LatLongHelper( DBHelper dbHelper, JSONArray latLonArray){

        Cursor rs = dbHelper.getLatLonDetails();

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateLatLongDetails( latLonArray );        // UPDATE LAT LONG ARRAY
        }else{
            dbHelper.InsertLatLongDetails( latLonArray  );      // INSERT LAT LONG ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }



    public JSONObject getLatLonJson(double latitude, double longitude){

        JSONObject latLonJson = new JSONObject();

        try {
            latLonJson.put(ConstantsKeys.Latitude, latitude);
            latLonJson.put(ConstantsKeys.Longitude, longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return latLonJson;
    }


}
