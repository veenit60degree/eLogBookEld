package com.local.db;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.constants.Constants;
import com.constants.SharedPref;
import com.als.logistic.Globally;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BleGpsAppLaunchMethod {

    public BleGpsAppLaunchMethod(){
        super();
    }


    String Tag = "Location Permission";

    /*-------------------- GET BLE GPS App LAUNCH LOG Array -------------------- */
    public JSONArray getBleGpsAppLaunchLogArray(DBHelper dbHelper) {

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getBleGpsAppLaunchLog(Globally.PROJECT_ID_INT);

        try {
            if (rs != null && rs.getCount() > 0) {
                rs.moveToFirst();
                String logList = rs.getString(rs.getColumnIndex(DBHelper.BLE_GPS_APPLAUNCH_LOG_LIST));
                try {
                    logArray = new JSONArray(logList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return logArray;

    }

    /* --------------- Insert/Update BLE, GPS, App LAUNCH LOGS --------------- */
    public void BleGpsAppLaunchLogHelper(DBHelper dbHelper, JSONArray array) {

        Cursor rs = dbHelper.getBleGpsAppLaunchLog(Globally.PROJECT_ID_INT);
        try {
            if (rs != null & rs.getCount() > 0) {
                rs.moveToFirst();
                dbHelper.UpdateBleGpsAppLaunchLog(Globally.PROJECT_ID_INT, array);     // UPDATE Shipping Log
            } else {
                dbHelper.InsertBleGpsAppLaunchLog(Globally.PROJECT_ID_INT, array);       // INSERT Shipping Log
            }
            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    //  Create new log as Json
    public JSONObject CreateBleGpsAppLogAsJson(int Type, int Mode, String LogDate ) {

        JSONObject jsonObj = new JSONObject();

        try {

            jsonObj.put(ConstantsKeys.Type,     Type);
            jsonObj.put(ConstantsKeys.Mode,     Mode);
            jsonObj.put(ConstantsKeys.TimeOfChange,    LogDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;

    }


    //  Create new log as Json
    public void SaveBleGpsAppLogInTable(int Type, int Mode, String LogDate, DBHelper dbHelper ) {

        try {
            // get saved logs in array
            JSONArray logArray = getBleGpsAppLaunchLogArray(dbHelper);

            // create new event log as json
            JSONObject GetNewLog = CreateBleGpsAppLogAsJson(Type, Mode, LogDate);

            // put new event log in array
            logArray.put(GetNewLog);

            // save updated log array in table
            BleGpsAppLaunchLogHelper(dbHelper, logArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    //  Get Selected Event Logs (BLE, GPS, APP LAUNCH) with EventType
    public JSONArray GetSelectedEventLog(int EventType, DBHelper dbHelper ) {

        JSONArray selectedLogArray = new JSONArray();

        try {
            JSONArray BleGpsAppLaunchLogArray = getBleGpsAppLaunchLogArray(dbHelper);

            for(int i = 0 ; i < BleGpsAppLaunchLogArray.length() ; i++){
                JSONObject obj = (JSONObject)BleGpsAppLaunchLogArray.get(i);
                int SelectedEventType = obj.getInt(ConstantsKeys.Type);

                if(SelectedEventType == EventType){
                    selectedLogArray.put(obj);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return selectedLogArray;

    }



    //  Clear Selected Event Log from array & Update it into table (BLE, GPS, APP LAUNCH)
    public JSONArray ClearAndUpdateSelectedEventLog(int EventType, DBHelper dbHelper ) {

        JSONArray selectedLogArray = new JSONArray();

        try {
            JSONArray BleGpsAppLaunchLogArray = getBleGpsAppLaunchLogArray(dbHelper);

            for(int i = 0 ; i < BleGpsAppLaunchLogArray.length() ; i++){
                JSONObject obj = (JSONObject)BleGpsAppLaunchLogArray.get(i);
                int SelectedEventType = obj.getInt(ConstantsKeys.Type);

                if(SelectedEventType != EventType){
                    selectedLogArray.put(obj);
                }

            }

            BleGpsAppLaunchLogHelper(dbHelper, selectedLogArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return selectedLogArray;

    }




    //  Get final log as Json for Ble, Gps, App Lunch logs to post on Server
    public JSONObject GetFinalBleGpsLogInJson(String DriverId, String DeviceId, JSONArray EventLogArray ) {

        JSONObject jsonObj = new JSONObject();

        try {

            jsonObj.put(ConstantsKeys.DriverId,     DriverId);
            jsonObj.put(ConstantsKeys.DeviceId,     DeviceId);
            jsonObj.put(ConstantsKeys.ModeTime,     EventLogArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;

    }


    public void checkLocationPermissionState(Context context, Globally global, DBHelper dbHelper, boolean isGpsEnabled) {

        int NotDetermined = 0;
        int Restricted = 1;
        int Denied = 2;
        int AuthorizedAlways = 3;
        int AuthorizedWhenInUse = 4;

        int CurrentLocationStatus = 0;
        int LastLocationStatus    = SharedPref.GetLocationStatus(context);

        if(!isGpsEnabled){
            CurrentLocationStatus = NotDetermined;
        }else {

            int fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int coarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (Build.VERSION.SDK_INT >= 31) {    //Build.VERSION_CODES.S   (Snow Cone)

                int bgLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

                boolean isAppLocationPermissionGranted = (bgLocation == PackageManager.PERMISSION_GRANTED) &&
                        (coarseLocation == PackageManager.PERMISSION_GRANTED);

                /*boolean preciseLocationAllowed = (fineLocation == PackageManager.PERMISSION_GRANTED)
                        && (coarseLocation == PackageManager.PERMISSION_GRANTED);

                if (preciseLocationAllowed) {
                    Logger.LogError(Tag, "Precise location is enabled in Android 12");
                } else {
                    Logger.LogError(Tag, "Precise location is disabled in Android 12");
                }*/

                if (isAppLocationPermissionGranted) {
                    //Logger.LogError(Tag, "Location is allowed all the time");
                    CurrentLocationStatus = AuthorizedAlways;
                } else if (coarseLocation == PackageManager.PERMISSION_GRANTED) {
                    //Logger.LogError(Tag, "Location is allowed while using the app");
                    CurrentLocationStatus = AuthorizedWhenInUse;
                } else {
                    //Logger.LogError(Tag, "Location is not allowed.");
                    CurrentLocationStatus = Denied;
                }

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                int bgLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

                boolean isAppLocationPermissionGranted = (bgLocation == PackageManager.PERMISSION_GRANTED) &&
                        (coarseLocation == PackageManager.PERMISSION_GRANTED);

                if (isAppLocationPermissionGranted) {
                    //Logger.LogError(Tag, "Location is allowed all the time");
                    CurrentLocationStatus = AuthorizedAlways;
                } else if (coarseLocation == PackageManager.PERMISSION_GRANTED) {
                    //Logger.LogError(Tag, "Location is allowed while using the app");
                    CurrentLocationStatus = AuthorizedWhenInUse;
                } else {
                   // Logger.LogError(Tag, "Location is not allowed.");
                    CurrentLocationStatus = Denied;
                }

            } else {

                boolean isAppLocationPermissionGranted = (fineLocation == PackageManager.PERMISSION_GRANTED) &&
                        (coarseLocation == PackageManager.PERMISSION_GRANTED);

                if (isAppLocationPermissionGranted) {
                   // Logger.LogError(Tag, "Location permission is granted");
                    CurrentLocationStatus = AuthorizedAlways;
                } else {
                    //Logger.LogError(Tag, "Location permission is not granted");
                    CurrentLocationStatus = Denied;
                }
            }
        }

        if(CurrentLocationStatus != LastLocationStatus){
            SharedPref.SetGpsBlePermission( SharedPref.WasBleEnabled(context), isGpsEnabled, CurrentLocationStatus, context);

            // write location status chlog
            SaveBleGpsAppLogInTable(Constants.LogEventTypeGps,CurrentLocationStatus, global.getCurrentDateLocalUtc(), dbHelper);

        }

    }




}
