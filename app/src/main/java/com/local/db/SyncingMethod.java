package com.local.db;


import android.annotation.SuppressLint;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.SYNC_DATA_LIST));
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




    /* ================================================================================================================*/


    /*-------------------- GET Version2 SYNC LOG SAVED Array -------------------- */
    public JSONArray getVersion2SyncingArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getSyncDataVersion2Details(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.SYNC_DATA_VERSION2_LIST));
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

    /*-------------------- Syncing log version2 Helper -------------------- */
    public void SyncingLogVersion2Helper( int driverId, DBHelper dbHelper, JSONArray syncArray){

        Cursor rs = dbHelper.getSyncDataVersion2Details(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateSyncDataVersion2(driverId, syncArray );        // UPDATE SYNC LOG in version 2 ARRAY
        }else{
            dbHelper.InsertSyncDataVersion2( driverId, syncArray  );      // INSERT SYNC LOG in version 2 ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }



    public JSONArray getUnPostedLogOnly(JSONArray DriverJsonArray, JSONArray savedV2SyncedArray){
          /*  JSONArray savedV2SyncedArray = getVersion2SyncingArray(Integer.valueOf(DRIVER_ID), dbHelper);
        SyncingLogVersion2Helper(Integer.valueOf(DRIVER_ID), dbHelper, savedV2SyncedArray);
*/
        // compare logs to avoid duplicate entries
        JSONArray finalArray = new JSONArray();
        try {
            for (int i = 0; i < savedV2SyncedArray.length(); i++) {
                JSONObject obj = (JSONObject) savedV2SyncedArray.get(i);
                String UTCDateTime = obj.getString(ConstantsKeys.UTCDateTime);
                String DriverStatusId = obj.getString(ConstantsKeys.DriverStatusId);
                boolean isExistingLog = isExistingLog(DriverJsonArray,UTCDateTime, DriverStatusId);
                if(!isExistingLog){
                    finalArray.put(obj);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return finalArray;
    }

    private boolean isExistingLog(JSONArray lastInputArray, String uTCDateTime, String driverStatusId){
        boolean isExistingLog = false;
        try {
            for (int i = 0; i < lastInputArray.length(); i++) {
                JSONObject obj = (JSONObject) lastInputArray.get(i);
                String UTCDateTime = obj.getString(ConstantsKeys.UTCDateTime);
                String DriverStatusId = obj.getString(ConstantsKeys.DriverStatusId);

                if(UTCDateTime.equals(uTCDateTime) && DriverStatusId.equals(driverStatusId)){
                    isExistingLog = true;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isExistingLog;
    }





    public int getLastStatusFromVersion2(int DriverId, DBHelper dbHelper){

        int lastStatus = -1;
        try {
            JSONArray selectedArray = getVersion2SyncingArray(DriverId, dbHelper);
            if(selectedArray.length() > 0){
                JSONObject lastJsonItem = (JSONObject) selectedArray.get(selectedArray.length() - 1);
                lastStatus = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
            }else{
                return lastStatus;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lastStatus;
    }




}
