package com.local.db;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LocationMethod {

    public LocationMethod() {
        super();
    }


    /*-------------------- GET SAVED LOCATION Array -------------------- */
    public JSONArray getSavedLocationArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getDriverLocDetails(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.DRIVER_LOCATION_LIST));
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

    /*-------------------- Insert/Update Driver location in DB Helper -------------------- */
    public void DriverLocationHelper( int driverId, DBHelper dbHelper, JSONArray LocArray){

        Cursor rs = dbHelper.getDriverLocDetails(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateDriverLocDetails(driverId, LocArray );        // UPDATE DRIVER LOC ARRAY
        }else{
            dbHelper.InsertDriverLocDetails( driverId, LocArray  );      // INSERT DRIVER LOC ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


    /*-------------------- Create Location object as JSON -------------------- */
    public JSONObject LocationObject(String DriverId, String Latitude, String Longitude,
                                     String VIN, String VehicleId, String UTCDate,
                                     int GPSSpeed){

        JSONObject locObj = new JSONObject();

        try {
            locObj.put(ConstantsKeys.DriverId,  DriverId);
            locObj.put(ConstantsKeys.Latitude,  Latitude);
            locObj.put(ConstantsKeys.Longitude, Longitude);
            locObj.put(ConstantsKeys.VIN,       VIN);
            locObj.put(ConstantsKeys.VehicleId, VehicleId);
            locObj.put(ConstantsKeys.UTCDate,   UTCDate);
            locObj.put(ConstantsKeys.GPSSpeed,  GPSSpeed);

        }catch (Exception e) {
            e.printStackTrace();
        }

        return locObj;
    }




}
