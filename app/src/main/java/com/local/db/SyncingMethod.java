package com.local.db;


import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;

public class SyncingMethod {


    public SyncingMethod() {
        super();
    }


    /*-------------------- GET SYNC LOG SAVED Array -------------------- */
    public JSONArray getSavedSyncingArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getSyncDataDetails(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.SYNC_DATA_LIST));
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
    public void SyncingLogHelper( int driverId, DBHelper dbHelper, JSONArray syncArray){

        Cursor rs = dbHelper.getSyncDataDetails(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateSyncDataDetails(driverId, syncArray );        // UPDATE SYNC LOG ARRAY
        }else{
            dbHelper.InsertSyncDataDetails( driverId, syncArray  );      // INSERT SYNC LOG ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


}
