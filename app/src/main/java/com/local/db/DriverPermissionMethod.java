package com.local.db;

import android.annotation.SuppressLint;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DriverPermissionMethod {

    public DriverPermissionMethod() {
        super();
    }


    /*-------------------- GET Driver Permissions Array -------------------- */
    public JSONObject getDriverPermissionObj(int DriverId, DBHelper dbHelper){

        JSONObject logJObject = new JSONObject();
        Cursor rs = dbHelper.getDriverPermissionDetails(DriverId);

        try {
            if (rs != null && rs.getCount() > 0) {
                rs.moveToFirst();
                @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.PERMISSION_LIST));
                try {
                    logJObject = new JSONObject(logList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
           // e.printStackTrace();
        }
        return logJObject;

    }


    /*-------------------- Driver Permissions DB Helper -------------------- */
    public void DriverPermissionHelper(  int driverId, DBHelper dbHelper, JSONObject logJObject){

        Cursor rs = dbHelper.getDriverPermissionDetails(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateDriverPermissionDetails(driverId, logJObject );     // UPDATE PERMISSION DETAILS
        }else{
            dbHelper.InsertDriverPermissionDetails( driverId, logJObject  );       // INSERT PERMISSION DETAILS
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }



    public boolean isDeviceLogEnabled(String DriverId, DBHelper dbHelper){

        boolean isDeviceLogEnabled = false;
        try{
            JSONObject logPermissionObj    = getDriverPermissionObj(Integer.valueOf(DriverId), dbHelper);
            if(logPermissionObj != null) {
                try {
                    isDeviceLogEnabled = logPermissionObj.getBoolean(ConstantsKeys.IsDeviceDebugLogEnable);
                } catch (JSONException e) {
                  //  e.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return isDeviceLogEnabled;
    }


    /*-------------------- Get Driver Permissions Status -------------------- */
        public boolean getPermissionStatus(JSONObject logJObject, String keyValue){
            boolean permission = false;
            try {
                permission  =   logJObject.getBoolean(keyValue);
            }catch (Exception e){
                e.printStackTrace();
            }

            return permission;
        }


    /*-------------------- Get Driver Permissions Status -------------------- */
    public boolean isTrueAnyPermission(JSONObject dataJObject){
        boolean permission = false;
        try {

            if(getPermissionStatus(dataJObject, ConstantsKeys.OffDutyKey) ||
                    getPermissionStatus(dataJObject, ConstantsKeys.SleeperKey) ||
                    getPermissionStatus(dataJObject, ConstantsKeys.DrivingKey) ||
                    getPermissionStatus(dataJObject, ConstantsKeys.OnDutyKey) )
            {
                permission = true;
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return permission;
    }




}
