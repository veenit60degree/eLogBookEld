package com.constants;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.local.db.ConstantsKeys;
import com.als.logistic.Globally;
import com.models.DriverLocationModel;
import com.shared.pref.StatePrefManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SharedPref {


    static String CURRENT_DATE                        = "currentDate";
    static String CURRENT_UTC_DATE                    = "current_utc_date";
    static String DRIVER_TYPE                	  	  = "driver_type";
    static String CURRENT_DRIVER_TYPE				  = "current_driver_type";
    static String PU_EXCEED_CHECK_DATE				  = "PuExceedCheckDate";


    public SharedPref() {
        super();
    }




    // Save Current Status
    public static void setCurrentStatus(int status, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("current_status", status);
        editor.apply();

    }


    // Get Current Status -------------------
    public static int getCurrentStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("current_status", -1);
    }


    // Save Total Personal use Odometer For the Day -----------
    public static void setTotalPUOdometerForDay(String odometer, String time, Context context){

        if(odometer.equals("null")){
            odometer = "";
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TotalPUOdometerForDay", odometer);
        editor.putString("PUOdometerDate", time);
        editor.apply();

    }


    // Get Total Personal use Odometer For the Day -------------------
    public static String getTotalPUOdometerForDay( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String TotalPUOdometerForDay = preferences.getString("TotalPUOdometerForDay", "0");
        if(TotalPUOdometerForDay.trim().equals("null") || TotalPUOdometerForDay.length() == 0){
            TotalPUOdometerForDay = "0";
        }
        return TotalPUOdometerForDay;


    }

    // Get selected day for Personal use Odometer  -------------------
    public static String getSelectedDayForPuOdometer( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String PUOdometerDate = preferences.getString("PUOdometerDate", "0");
        if(PUOdometerDate.trim().equals("null") || PUOdometerDate.length() == 0){
            PUOdometerDate = "0";
        }
        return PUOdometerDate;


    }


    public static void setVehilceMovingStatus(boolean status, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("VehilceMovingStatus", status);
        editor.apply();

    }


    // Get Current Status -------------------
    public static boolean isVehicleMoving( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean VehilceMovingStatus = preferences.getBoolean("VehilceMovingStatus", false);
        return VehilceMovingStatus;
    }

    // Save VIN Number
    public static void setVINNumber(String VIN, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("VIN", VIN);
        editor.apply();

    }


    // Get VIN Number -------------------
    public static String getVINNumber( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("VIN", "");
    }


    // Save VIN Number to check at logout time
    public static void setLastSavedVINNumber(String VIN, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LastSavedVin", VIN);
        editor.apply();

    }


    // Get VIN Number to confirm with current VIN at logout time-------------------
    public static String getLastSavedVINNumber( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("LastSavedVin", "");
    }


    // Set UTC Time Zone -------------------
    public static void setUTCTimeZone(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Get UTC Time Zone -------------------
    public static String getUTCTimeZone(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    // SetTrailor Number -------------------
    public static void setTrailorNumber( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TrailorNumber", value);
        editor.apply();
    }

    // Get Trailor Number -------------------
    public static String getTrailorNumber(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("TrailorNumber", "");
    }

    // Save Truck Number
    public static void setTruckNumber( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TruckNumber", value);
        editor.apply();
    }

    // Get Truck Number -------------------
    public static String getTruckNumber(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("TruckNumber", "");
    }


    // Set Current Saved Time -------------------
    public static void setCurrentDate( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_DATE, value);
        editor.apply();
    }
    // Get Current Saved Time -------------------
    public static String getSystemSavedDate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CURRENT_DATE, "");
    }


    // Set Current Saved Time -------------------
    public static void setPuExceedCheckDate( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PU_EXCEED_CHECK_DATE, value);
        editor.apply();
    }
    // Get Current Saved Time -------------------
    public static String getPuExceedCheckDate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PU_EXCEED_CHECK_DATE, "");
    }

    // Set Current Saved Time -------------------
    public static void setUTCCurrentDate( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_UTC_DATE, value);
        editor.apply();
    }

    // Get Current Saved Time -------------------
    public static String getCurrentUTCDate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CURRENT_UTC_DATE, "");
    }


    // Set auto drive status  -------------------
    public static void SetAutoDriveStatus( boolean isAutoDrive, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("auto_drive", isAutoDrive);
        editor.apply();
    }


    // Get auto drive status -------------------
    public static boolean isAutoDrive(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("auto_drive", false);
    }


    // Set auto drive status  -------------------
    public static void SetOBDPingAllowedStatus( boolean isAutoDrive, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("IsOBDPingAllowed", isAutoDrive);
        editor.apply();
    }


    // Get Odometer Calculation Allow  status -------------------
    public static boolean isOdoCalculationAllowed(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("OdoCalculationAllowed", false);
    }


    // Set Odometer Calculation Allow  status  -------------------
    public static void SetOdoCalculationAllowed( boolean isAutoDrive, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("OdoCalculationAllowed", isAutoDrive);
        editor.apply();
    }




    // Get  ping status -------------------
    public static String isPing(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("IsBlePing", "");
    }


    // Set ping status  -------------------
    public static void SetPingStatus( String status, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("IsBlePing", status);
        editor.apply();
    }


    // Get Ble Scan Count  -------------------
    public static int getBleScanCount(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("bleScanCount", 0);
    }


    // set Ble Scan Count  -------------------
    public static void saveBleScanCount( int count, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bleScanCount", count);
        editor.apply();
    }


    // Save Ble OBD Mac Address  -------------------
    public static void SaveBleOBDMacAddress( String date, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("BleOBDMacAddress", date);
        editor.apply();
    }


    // Get Ble OBD Mac Address -------------------
    public static String GetBleOBDMacAddress(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("BleOBDMacAddress", "");
    }


  /*  // Get OBD Screen status -------------------
    public static boolean isOBDScreen(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("isOBDScreen", false);
    }


    // Set OBD Screen status  -------------------
    public static void SetOBDScreenStatus( boolean date, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isOBDScreen", date);
        editor.apply();
    }*/

    // Get auto drive status -------------------
    public static boolean isOBDPingAllowed(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("IsOBDPingAllowed", false);
    }


    // Set auto drive status  -------------------
    public static void SetOBDRestartTime( String date, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("obd_restart_date", date);
        editor.apply();
    }


    // Get auto drive status -------------------
    public static String getOBDRestartTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("obd_restart_date", "");
    }



    // Set Obd Status -------------------
    public static void SaveObdStatus( int value, String time, String utcTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("ObdStatus", value);
        editor.putString("ObdStatusTime", time);
        editor.putString("ObdStatusUtcTime", utcTime);
        editor.apply();
    }



    // Get Obd Status -------------------
    public static int getObdStatus( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getInt("ObdStatus", Constants.NO_CONNECTION);
        }else{
            return Constants.NO_CONNECTION;
        }
    }


    // Get last recorded time of Obd Status -------------------
    public static String getObdLastStatusTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ObdStatusTime", "");
    }

    // Get last recorded utc time of Obd Status -------------------
    public static String getObdLastStatusUtcTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ObdStatusUtcTime", "");
    }


    // Set Wired Obd Call Time  -------------------
    public static void SetCoDriverSwitchTime( String date, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CoDriverSwitchTime", date);
        editor.apply();
    }

    // Get last Wired Obd CallTime -------------------
    public static String getCoDriverSwitchTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("CoDriverSwitchTime", "");
    }




    // Set HOS logging status for both drivers  -------------------
    public static void SetHosLoggingStatus( boolean mainDriverStatus, boolean coDriverStatus, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("mainDriverLoggingStatus", mainDriverStatus);
        editor.putBoolean("coDriverLoggingStatus", coDriverStatus);
        editor.apply();
    }

    // Get HOS logging status of main driver -------------------
    public static boolean getMainDriverLoggingStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("mainDriverLoggingStatus", false);
    }

    // Get HOS logging status of co-driver -------------------
    public static boolean getCoDriverLoggingStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("coDriverLoggingStatus", false);
    }

    // Set Wired Obd Call Time  -------------------
    public static void SetWiredObdCallTime( String date, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("WiredObdCallTime", date);
        editor.apply();
    }

    // Get last Wired Obd CallTime -------------------
    public static String getWiredObdCallTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("WiredObdCallTime", "");
    }



    // Set Wired Obd Server Call Time  -------------------
    public static void SetWiredObdServerCallTime( String date, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("WiredObdServerCallTime", date);
        editor.apply();
    }

    // Get last Wired Obd Server CallTime -------------------
    public static String getWiredObdServerCallTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("WiredObdServerCallTime", "");
    }



    // Set After Ignition Off Call Time  -------------------
    public static void SetIgnitionOffCallTime( String date, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("IgnitionOffCallTime", date);
        editor.apply();
    }

    // Get  last Wired Obd Call Time after ignition off -------------------
    public static String getIgnitionOffCallTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("IgnitionOffCallTime", "");
    }



    // save Ble OnReceive callback Time -------------------
    public static void setBleOnReceiveTime( Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("BleOnReceiveTime", Globally.GetDriverCurrentDateTime(new Globally(), context));   //Globally.GetCurrentDateTime()
        editor.apply();
    }


    // get Ble OnReceive callback Time -------------------
    public static String getBleOnReceiveTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("BleOnReceiveTime", "");
    }


    // Set Wrong Vin Alert View status  -------------------
    public static void SetWrongVinAlertView( boolean status, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("WrongVinAlertStatus", status);
        editor.apply();
    }

    // Get Wrong Vin Alert View status -------------------
    public static boolean IsWrongVinAlertView( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("WrongVinAlertStatus", false);
    }

    // SAve Unidentified status From OBD to create -------------------
    public static void SetUnidentifiedFromOBDStatus( boolean status, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("UnidentifiedFromOBD", status);
        editor.apply();
    }

    // Get Unidentified status From OBD to create -------------------
    public static boolean IsUnidentifiedFromOBD( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("UnidentifiedFromOBD", false);
    }




    // Get Obd Status -------------------
    public static String getObdSourceName( Context context) {
        String source = "";
       switch (SharedPref.getObdPreference(context)){
           case Constants.OBD_PREF_WIRED:
               source = Constants.WiredOBD;

            break;

           case Constants.OBD_PREF_WIFI:
               source = Constants.WifiOBD;
            break;

           case Constants.OBD_PREF_BLE:
               source = Constants.BleObd;
            break;

       }
        return source;
    }





/*
    // Get Obd Status -------------------
    public static boolean getLastIgnitionStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("ignitionStatus", false);
    }


    public static String getLastIgnitionTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ignitionTime", "");
    }


    public static int getLastObdSpeed( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("obdCurrentSpeed_", -1);
    }

    // Set Obd Status -------------------
    public static void SaveObdIgnitionStatus( boolean ignitionStatus, String time, int speed, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("ignitionStatus", ignitionStatus);
        editor.putString("ignitionTime", time);
        editor.putInt("obdCurrentSpeed_", speed);
        editor.apply();
    }
*/



    // Set Diagnostic and Malfunction Records action status for main driver-------------------
    public static void SetDiagnosticAndMalfunctionSettingsMain( boolean IsAllowMalfunction, boolean IsAllowDiagnostic,
                                                    boolean IsClearMalfunction, boolean IsClearDiagnostic, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsAllowMalfunction, IsAllowMalfunction);
        editor.putBoolean(ConstantsKeys.IsAllowDiagnostic, IsAllowDiagnostic);
        editor.putBoolean(ConstantsKeys.IsClearMalfunction, IsClearMalfunction);
        editor.putBoolean(ConstantsKeys.IsClearDiagnostic, IsClearDiagnostic);

        editor.apply();
    }

    // Get Malfunction Status -------------------
    public static boolean IsAllowMalfunction(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsAllowMalfunction, false);
    }

    // Get Diagnostic Status -------------------
    public static boolean IsAllowDiagnostic(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsAllowDiagnostic, false);
    }

    // Get Malfunction clear Status -------------------
    public static boolean IsClearMalfunction(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsClearMalfunction, true);
    }

    // Get Diagnostic Clear Status -------------------
    public static boolean IsClearDiagnostic(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsClearDiagnostic, true);
    }




    // Set Diagnostic and Malfunction Records action status for co driver-------------------
    public static void SetDiagnosticAndMalfunctionSettingsCo( boolean IsAllowMalfunction, boolean IsAllowDiagnostic,
                                                                boolean IsClearMalfunction, boolean IsClearDiagnostic, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsAllowMalfunctionCo, IsAllowMalfunction);
        editor.putBoolean(ConstantsKeys.IsAllowDiagnosticCo, IsAllowDiagnostic);
        editor.putBoolean(ConstantsKeys.IsClearMalfunctionCo, IsClearMalfunction);
        editor.putBoolean(ConstantsKeys.IsClearDiagnosticCo, IsClearDiagnostic);

        editor.apply();
    }


    // Get Malfunction Status -------------------
    public static boolean IsAllowMalfunctionCo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsAllowMalfunctionCo, false);
    }

    // Get Diagnostic Status -------------------
    public static boolean IsAllowDiagnosticCo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsAllowDiagnosticCo, false);
    }

    // Get Malfunction clear Status -------------------
    public static boolean IsClearMalfunctionCo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsClearMalfunctionCo, true);
    }

    // Get Diagnostic Clear Status -------------------
    public static boolean IsClearDiagnosticCo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsClearDiagnosticCo, true);
    }




    // Set bluetooth and GPS status when action changed-------------------
    public static void SetGpsBlePermission(boolean IsBleEnabled, boolean IsGpsEnabled, int LocationStatus, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsBleEnabled, IsBleEnabled);
        editor.putBoolean(ConstantsKeys.IsGpsEnabled, IsGpsEnabled);
        editor.putInt(ConstantsKeys.LocationStatus, LocationStatus);

        editor.apply();
    }


    // Get last GPS Status -------------------
    public static boolean WasGpsEnabled(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsGpsEnabled, false);
    }


    // Get last location permission Status -------------------
    public static int GetLocationStatus(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(ConstantsKeys.LocationStatus, 0);
    }

      // Get last bluetooth Status -------------------
   public static boolean WasBleEnabled(Context context) {
       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
       return preferences.getBoolean(ConstantsKeys.IsBleEnabled, false);
   }



    // Set Re-Certification and Unidentified Records allow status -------------------
    public static void SetCertifcnUnIdenfdSettings( boolean isReCertification, boolean IsUnidentifiedRecords,
                                                    boolean IsPersonal, boolean IsYardMove, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsAllowLogReCertification, isReCertification);
        editor.putBoolean(ConstantsKeys.IsShowUnidentifiedRecords, IsUnidentifiedRecords);
        editor.putBoolean(ConstantsKeys.IsPersonal, IsPersonal);
        editor.putBoolean(ConstantsKeys.IsYardMove, IsYardMove);
        editor.apply();
    }


    // Get Re-Certification Status -------------------
    public static boolean IsReCertification(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsAllowLogReCertification, false);
    }


    // Get Unidentified Records view Status -------------------
    public static boolean IsShowUnidentifiedRecords(Context context) {
        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsShowUnidentifiedRecords, false);
        }
        return isRecord;
    }


    // Save Unidentified Records alert view Status -------------------
    public static void setUnidentifiedAlertViewStatus(boolean isAlert, Context context) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(ConstantsKeys.UnIdentifiedAlertStatus, isAlert);
            editor.apply();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // Get Unidentified Records alert view Status -------------------
    public static boolean getUnidentifiedAlertViewStatus( Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.UnIdentifiedAlertStatus, false);
        }
        return isRecord;


    }


    // Set Re-Certification and Unidentified Records allow status for co driver-------------------
    public static void SetCertifcnUnIdenfdSettingsCo( boolean isReCertification, boolean IsUnidentifiedRecords,
                                                    boolean IsPersonal, boolean IsYardMove, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsAllowLogReCertificationCo, isReCertification);
        editor.putBoolean(ConstantsKeys.IsShowUnidentifiedRecordsCo, IsUnidentifiedRecords);
        editor.putBoolean(ConstantsKeys.IsPersonalCo, IsPersonal);
        editor.putBoolean(ConstantsKeys.IsYardMoveCo, IsYardMove);
        editor.apply();
    }


    // Get Re-Certification Status for co driver -------------------
    public static boolean IsReCertificationCo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsAllowLogReCertificationCo, false);
    }


    // Get Unidentified Records view Status for co driver -------------------
    public static boolean IsShowUnidentifiedRecordsCo(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsShowUnidentifiedRecordsCo, false);
        }
        return isRecord;


    }


    // Set North Canada Status for main driver -------------------
    public static void SetNorthCanadaStatus( boolean IsNorthCanada, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsNorthCanada, IsNorthCanada);
        editor.apply();
    }



    // Get North Canada Status for main driver -------------------
    public static boolean IsNorthCanada(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsNorthCanada, false);
        }
        return isRecord;
    }


    // Set location received parameter from OBD -------------------
    public static void SetLocReceivedFromObdStatus( boolean IsNorthCanada, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.LocReceivedFromObd, IsNorthCanada);
        editor.apply();
    }



    // Get location received parameter from OBD -------------------
    public static boolean IsLocReceivedFromObd(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.LocReceivedFromObd, false);
        }
        return isRecord;
    }



    // Set Obd Preference for to use obd -------------------
    public static void SetObdPreference( int ObdPreference, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(ConstantsKeys.ObdPreference, ObdPreference);
        editor.apply();
    }



    // Get Obd Preference for to use obd -------------------
    public static int getObdPreference(Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getInt(ConstantsKeys.ObdPreference, Constants.OBD_PREF_BLE);
        }
        return Constants.OBD_PREF_BLE;
    }



    // Set High Precision odometer Unit for wired tablet -------------------
    public static void SetHighPrecisionUnit( String ObdPreference, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.HighPrecisionUnit, ObdPreference);
        editor.apply();
    }



    // Get High Precision odometer Unit for wired tablet -------------------
    public static String getHighPrecisionUnit(Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.HighPrecisionUnit, "m");
        }
        return "M";
    }

    // Set Exempt Driver Status for main driver -------------------
    public static void SetExemptDriverStatusMain( boolean ExemptDriver, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsExemptDriver, ExemptDriver);
        editor.apply();
    }



    // Get Exempt Driver Status for main driver -------------------
    public static boolean IsExemptDriverMain(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsExemptDriver, false);
        }
        return isRecord;
    }



    // Set Exempt Driver Status for co driver -------------------
    public static void SetExemptDriverStatusCo( boolean CCMTACertified, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsExemptDriverCo, CCMTACertified);
        editor.apply();
    }



    // Get ExemptDriver Status for co driver -------------------
    public static boolean IsExemptDriverCo(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsExemptDriverCo, false);
        }
        return isRecord;
    }




    // Set Cycle Request Status for main driver -------------------
    public static void SetCycleRequestAlertViewStatus( boolean isCycleRequest, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsCycleRequest, isCycleRequest);
        editor.apply();
    }



    // Get Cycle Request Status for main driver -------------------
    public static boolean IsCycleRequestAlertShownAlready(Context context) {
        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsCycleRequest, false);
        }
        return isRecord;
    }





    // Set Cycle Request Status for main driver -------------------
    public static void SetCycleRequestStatusMain( boolean isCycleRequest, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsCycleRequestMain, isCycleRequest);
        editor.apply();
    }



    // Get Cycle Request Status for main driver -------------------
    public static boolean IsCycleRequestMain(Context context) {
        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsCycleRequestMain, false);
        }
        return isRecord;
    }



    // Set Cycle Request Status for co driver -------------------
    public static void SetCycleRequestStatusCo( boolean isCycleRequest, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsCycleRequestCo, isCycleRequest);
        editor.apply();
    }



    // Get Cycle Request Status for co driver -------------------
    public static boolean IsCycleRequestCo(Context context) {
        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsCycleRequestCo, false);
        }
        return isRecord;
    }




    // Set ELD Notification Status for main driver -------------------
    public static void SetELDNotification( boolean isNotification, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsELDNotificationShown, isNotification);
        editor.apply();
    }



    // Get ELD Notification Status for main driver -------------------
    public static boolean IsELDNotification(Context context) {
        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsELDNotificationShown, false);
        }
        return isRecord;
    }




    // Set ELD Notification Alert View Status for main driver -------------------
    public static void SetELDNotificationAlertViewStatus( boolean IsELDNotificationAlert, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsELDNotificationAlert, IsELDNotificationAlert);
        editor.apply();
    }



    // Get ELD Notification Alert View Status Status for main driver -------------------
    public static boolean IsELDNotificationAlertShownAlready(Context context) {
        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsELDNotificationAlert, false);
        }
        return isRecord;
    }



    // Set CCMTACertified status -------------------
    public static void SetCCMTACertifiedStatus( boolean CCMTACertified, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsCCMTACertified, CCMTACertified);
        editor.apply();
    }



    // Get CCMTA Certified Status  -------------------
    public static boolean IsCCMTACertified(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsCCMTACertified, false);
        }
        return isRecord;


    }


    // Set App Restricted status -------------------
    public static void SetAppRestrictedStatus( boolean IsAppRestricted, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsAppRestricted, IsAppRestricted);
        editor.apply();
    }



    // Get App Restricted Status  -------------------
    public static boolean IsAppRestricted(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsAppRestricted, false);
        }
        return isRecord;


    }


    // Get Unidentified Records alert view Status for co driver-------------------
    public static boolean getUnidentifiedAlertViewStatusCo( Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.UnIdentifiedAlertStatusCo, true);
        }
        return isRecord;


    }

    public static void setUnidentifiedAlertViewStatusCo(boolean isAlert, Context context) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(ConstantsKeys.UnIdentifiedAlertStatusCo, isAlert);
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Get yard move allow Status -------------------
    public static boolean IsYardMoveAllowed(Context context) {
        boolean isYard = true;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isYard = preferences.getBoolean(ConstantsKeys.IsYardMove, true);
        }
        return isYard;
    }


    // Get is personal allow Status -------------------
    public static boolean IsPersonalAllowed(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.IsPersonal, true);
    }

    // Set Current WIFI Obd Odometer -------------------
    public static void SetWifiObdOdometer( String odometer, String savedTime, String rawData, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("current_obd", odometer);
        editor.putString("obd_saved_time", savedTime);
        editor.putString("raw_data", rawData);
        editor.apply();
    }

    // Get WIFI Current Obd Odometer -------------------
    public static String GetWifiObdOdometer(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("current_obd", "0");
    }

    // Get Current WIFI Obd Odometer -------------------
    public static String GetWifiObdSavedTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("obd_saved_time", "");
    }


    // Get OBD Raw data   -------------------
    public static String GetObdRawData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("raw_data", "");
    }


    // Set Time stamp Enabled -------------------
    public static void saveTimeStampView( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("IsTimestampEnabled", value);
        editor.apply();
    }


    // Get Time stamp Enabled -------------------
    public static boolean isTimestampEnabled( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("IsTimestampEnabled", false);
    }


    // Set DOT status -------------------
    public static void SetDOTStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dot_status", value);
        editor.apply();
    }


    // Get DOT Status -------------------
    public static boolean IsDOT( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("dot_status", false);
    }


    // Set First Time Login status main Driver -------------------
    public static void SetFirstTimeLoginStatusMain( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("FirstTimeLoginMain", value);
        editor.apply();
    }


    // Get First Time Login Status main driver -------------------
    public static boolean IsFirstTimeLoginMain( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("FirstTimeLoginMain", false);
    }


    // Set First Time Login status for co driver -------------------
    public static void SetFirstTimeLoginStatusCo( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("FirstTimeLoginCo", value);
        editor.apply();
    }


    // Get First Time Login Status for co Driver -------------------
    public static boolean IsFirstTimeLoginCo( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("FirstTimeLoginCo", false);
    }

    // Get driver First Login Time -------------------
    public static void setFirstLoginTime( String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("FirstLoginTime", time);
        editor.apply();
    }

    // Get driver First Login Time -------------------
    public static String getFirstLoginTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("FirstLoginTime", "");
    }



    // save Unassigned Required api call Time -------------------
    public static void setCheckUnassignedReqTime( String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CheckUnassignedReqTime", time);
        editor.apply();
    }

    // Get Unassigned Required api call Time -------------------
    public static String getCheckUnassignedReqTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("CheckUnassignedReqTime", "");
    }



    // Set Inspection Issues -------------------
    public static void setInspectionIssues( String truckValue, String traileValue, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.TruckIssues, truckValue);
        editor.putString(ConstantsKeys.TrailerIssues, traileValue);
        editor.apply();
    }
    // Get Inspection Issues -------------------
    public static String getInspectionIssues(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "[]");
    }


    // Set CT-PAT Inspection Issues -------------------
    public static void setCtPatInspectionIssues( String truckValue, String traileValue, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.TruckCtPatIssues, truckValue);
        editor.putString(ConstantsKeys.TrailerCtPatIssues, traileValue);
        editor.apply();
    }
    // Get CT-PAT Inspection Issues -------------------
    public static String getCtPatInspectionIssues(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "[]");
    }






    /* ####################################################################################################### */

    /*====================== Save User Data with Shared Preferences =====================*/
    // Set  UserName -------------------
    public static void setUserName(String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", value);
        editor.apply();
    }

    // Get  USerName -------------------
    public static String getUserName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("username", "");
    }


    // Set  Password -------------------
    public static void setPassword(String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("password", value);
        editor.apply();
    }


    // Get  Password -------------------
    public static String getPassword(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("password", "");
    }


    public static void setUserCred(String mainDriverLoginName, String coDriverLoginName, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mainDriverLoginName", mainDriverLoginName);
        editor.putString("coDriverLoginName", coDriverLoginName);

        editor.apply();
    }

    // Get last login main driver detail -------------------
    public static String getLastLoginMainDriver(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("mainDriverLoginName", "");
    }

    // Get last login co driver detail -------------------
    public static String getLastLoginCoDriver(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("coDriverLoginName", "");
    }

    // Set  Driver Type -------------------
    public static void setDriverType( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DRIVER_TYPE, value);
        editor.apply();
    }

    // Get  Driver Type -------------------
    public static String getDriverType( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(DRIVER_TYPE, "");
        }else{
            return "";
        }
    }


    // Set Current Driver Type -------------------
    public static void setCurrentDriverType( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_DRIVER_TYPE, value);
        editor.apply();
    }


    // Get Current Driver Type -------------------
    public static String getCurrentDriverType( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CURRENT_DRIVER_TYPE, "");
    }


    // Set Current Driver Type -------------------
    public static void setDriverTimeZoneOffSet( long value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("driver_time_ZONE", value);
        editor.apply();
    }


    // Get Current Driver Type -------------------
    public static long getDriverTimeZoneOffSet( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong("driver_time_ZONE", 0);
    }


    // Set Re-Certify Data -------------------
    public static void setReCertifyData( String value, Context context) {
        try {
            if (context != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("ReCertifyData", value);
                editor.apply();
            }
        }catch(Exception e){}
    }


    // Get Re-Certify Data -------------------
    public static String getReCertifyData( Context context) {
        String array = "[]";
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            array = preferences.getString("ReCertifyData", "[]");
        }
        return array;
    }



    // Get  Driver Type -------------------
    public static boolean IsOdometerSaved( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("odo", false);
    }


    // Set Current Driver Type -------------------
    public static void OdometerSaved( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("odo", value);
        editor.apply();
    }



    // Set Is Read Violation Status -------------------
    public static void SetIsReadViolation( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_read_vln", value);
        editor.apply();
    }

    // Get Is Read Violation Status -------------------
    public static boolean IsReadViolation( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("is_read_vln", false);
    }


    // Set Violation -------------------
    public static void SetViolation( boolean value, Context context) {
        if(context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("violation", value);
            editor.apply();
        }
    }


    // Get Violation -------------------
    public static boolean IsViolation( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("violation", false);
    }




    // Set Permission Info View Status -------------------
    public static void SetPermissionInfoViewStatus( boolean value, Context context) {
        if(context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("PermissionInfoView", value);
            editor.apply();
        }
    }


    // Get  Permission Info View Status -------------------
    public static boolean getPermissionInfoViewStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("PermissionInfoView", false);
    }




    // Set notification Deleted -------------------
    public static void notificationDeleted( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("notification_deleted", value);
        editor.apply();
    }


    // Get notification Deleted  -------------------
    public static boolean isNotificationDeleted( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("notification_deleted", false);
    }




    // Set AOBRD status -------------------
    public static void SetIsAOBRD( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_aobrd", value);
        editor.apply();
    }


    // Get AOBRD Status -------------------
    public static boolean IsAOBRD( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("is_aobrd", false);
    }


    // Set AOBRD Automatic Status -------------------
    public static void SetAOBRDAutomatic( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("IsAOBRDAutomatic", value);
        editor.apply();
    }


    // Get AOBRD Automatic Status -------------------
    public static boolean IsAOBRDAutomatic( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("IsAOBRDAutomatic", false);
    }


    // Set Driving Shipping Allowed Status -------------------
    public static void SetDrivingShippingAllowed( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("IsDrivingShippingAllowed", value);
        editor.apply();
    }


    // Get Driving Shipping Allowed  Status -------------------
    public static boolean IsDrivingShippingAllowed( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("IsDrivingShippingAllowed", false);
    }


    // Set AOBRD Automatic Status -------------------
    public static void SetAOBRDAutoDrive( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("IsAOBRDAutoDrive", value);
        editor.apply();
    }


    // Get AOBRD Automatic Status -------------------
    public static boolean IsAOBRDAutoDrive( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("IsAOBRDAutoDrive", false);
    }



    // Set AOBRD Automatic Status -------------------
    public static void SetCertifyMandatoryStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("CertifyMandatory", value);
        editor.apply();
    }


    // Get AOBRD Automatic Status -------------------
    public static boolean IsCertifyMandatory( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("CertifyMandatory", false);
    }



    // Set Odometer Reading From OBD Status -------------------
    public static void SetOdometerFromOBD( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("IsOdometerFromOBD", value);
        editor.apply();
    }


    // Get Odometer Reading From OBD Status -------------------
    public static boolean IsOdometerFromOBD( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("IsOdometerFromOBD", false);
    }



    // Get Violation Reason -------------------
    public static String GetViolationReason( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("violationReason", "");
    }


    // Set Violation Reason -------------------
    public static void SetViolationReason( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("violationReason", value);
        editor.apply();
    }


    // Get Violation Reason -------------------
    public static String GetCurrentTruckPlateNo( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("plateNo", "");
    }


    // Set Violation Reason -------------------
    public static void setCurrentTruckPlateNo( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("plateNo", value);
        editor.apply();
    }



    // Set System Token -------------------
    public static void SetSystemToken( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("systemToken", value);
        editor.apply();
    }

    // Get System Token -------------------
    public static String GetSavedSystemToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("systemToken", "");

    }


    // Save ImEi Number
    public static void setImEiNumber(String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("imei", value);
        editor.apply();
    }


    // Get  ImEi Number -------------------
    public static String getImEiNumber(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("imei", "");
    }

    // Save Date Time
    public static void setSavedDateTime(String value, Context context) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("dateTime", value);
            editor.apply();
        }catch (Exception e){}
    }


    // Get  Date Time -------------------
    public static String getSavedDateTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("dateTime", "");
    }



    // Save Certify Alert View Time
    public static void setCertifyAlertViewTime(String value, Context context) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("CertifyAlertViewTime", value);
            editor.apply();
        }catch (Exception e){}
    }


    // Get Certify Alert View Time -------------------
    public static String getCertifyAlertViewTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("CertifyAlertViewTime", "");
    }




    // Set  Job Type -------------------
    public static void setDriverStatusId( String value, Context context) {
        if(context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("jobType", value);
            editor.apply();
        }
    }


    // Get Job Type -------------------
    public static String getDriverStatusId( Context context) {
        String DriverStatusId = "1";
        try {
            if (context != null) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                DriverStatusId = preferences.getString("jobType", "1");

                if(DriverStatusId.length() == 0){
                    DriverStatusId = "1";
                }
            }
        }catch (Exception e){}

        return DriverStatusId;
    }


    // Set  Country Cycle -------------------
    public static void setCountryCycle(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }


    // Get  CountryCycle -------------------
    public static String getCountryCycle(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    // Set AsyncTask Status -------------------
    public static void setAsyncCancelStatus(boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("AsyncTaskStatus", value);
        editor.apply();
    }


    // Get AsyncTask Status -------------------
    public static boolean getAsyncCancelStatus(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("AsyncTaskStatus", false);
    }


    // Set Login Allowed Status -------------------
    public static void setLoginAllowedStatus(boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("IsLoginAllowed", value);
        editor.apply();
    }


    // Get Login Allowed Status -------------------
    public static boolean isLoginAllowed(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("IsLoginAllowed", true);
    }



    // Set Country Cycle -------------------
    public static void setUserCountryCycle(String keyCA, String valueCA,
                                           String keyUS, String valueUS,
                                           Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyCA, valueCA);
        editor.putString(keyUS, valueUS);
        editor.apply();
    }

    // Get Country Cycle -------------------
    public static String getUserCountryCycle(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }




    // Set Start Locations -------------------
    public static void setStartLocation(String lat, String lon, String date,
                                        Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.StartLat, lat);
        editor.putString(ConstantsKeys.StartLon, lon);
        editor.putString(ConstantsKeys.StartDate, date);
        editor.apply();
    }

    // Get Start Locations -------------------
    public static String getStartLocation(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    // Set End Locations -------------------
    public static void setEndLocation(String lat, String lon, String date,
                                      Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.EndLat, lat);
        editor.putString(ConstantsKeys.EndLon, lon);
        editor.putString(ConstantsKeys.EndDate, date);
        editor.apply();
    }

    // Get End Locations -------------------
    public static String getEndLocation(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    // Set Login Status -------------------
    public static void SetNewLoginStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("user_type", value);
        editor.apply();
    }


    // Get Login Status -------------------
    public static boolean GetNewLoginStatus( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean("user_type", true);
        }else{
            return true;
        }
    }


    // Set time when popup window is opened -------------------
    public static void SetUpdateAppDialogTime( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("update_popup_time", value);
        editor.apply();
    }


    // Get popup window is opened time -------------------
    public static String GetUpdateAppDialogTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("update_popup_time", "");
    }


    // Set Truck Start or Login Status for Yard move / Personal use -------------------
    public static void SetAfterLoginConfStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("after_login_conf", value);
        editor.apply();
    }


    // Get Truck Start or Login Status for Yard move / Personal use -------------------
    public static boolean GetAfterLoginConfStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("after_login_conf", false);
    }

    // Set Truck Start or Login Status for Yard move / Personal use -------------------
    public static void SetTruckStartLoginStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("new_truck_start_login", value);
        editor.apply();
    }


    // Get Truck Start or Login Status for Yard move / Personal use -------------------
    public static boolean GetTruckStartLoginStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("new_truck_start_login", true);
    }

    // Set Rule version -------------------
    public static void SetRulesVersion( int value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("rulesVersion", value);
        editor.apply();
    }


    // Get rule version  -------------------
    public static int GetRulesVersion( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("rulesVersion", 0);
    }


/*
    // Set Connection Type -------------------
    public static void SetConnectionType( int value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("ConnectionType", value);
        editor.apply();
    }


    // Get Connection Type  -------------------
    public static int GetConnectionType( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("ConnectionType", 0);
    }

*/



    // Set Vehicle Id -------------------
    public static void setVehicleId( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("VehicleId", value);
        editor.apply();
    }


    // Get Vehicle Id -------------------
    public static String getVehicleId( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("VehicleId", "");
    }


    // Set Odometer Saving Status -------------------
    public static void SetOdoSavingStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("odo_status", value);
        editor.apply();
    }


    // Get Odometer Saving Status -------------------
    public static boolean GetOdoSavingStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("odo_status", false);
    }


    public static void SaveTruckInfoOnIgnitionChange( String ignitionStatus, String ignitionSource, String lastIgnitionTime,
                                               String lastIgnitionUtcTime, String EngineHour, String Odometer, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.TruckIgnitionStatusMalDia, ignitionStatus);
        editor.putString(Constants.IgnitionSourceMalDia, ignitionSource);
        editor.putString(Constants.IgnitionTimeMalDia, lastIgnitionTime);
        editor.putString(Constants.IgnitionUtcTimeMalDia, lastIgnitionUtcTime);
        editor.putString(Constants.EngineHourMalDia, EngineHour);
        editor.putString(Constants.OdometerMalDia, Odometer);

        editor.apply();
    }


    // Get Truck ignition Status -------------------
    public static String GetTruckInfoOnIgnitionChange( String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }

    // Set Truck Ignition Status for continue status -------------------
    public static void SetTruckIgnitionStatusForContinue( String ignitionStatus, String ignitionSource, String lastIgnitionTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.TruckIgnitionStatus, ignitionStatus);
        editor.putString(Constants.IgnitionSource, ignitionSource);
        editor.putString(Constants.LastIgnitionTime, lastIgnitionTime);

        editor.apply();
    }


    // Get Truck Ignition Status -------------------
    public static String GetTruckIgnitionStatusForContinue( String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key , "ON");
    }



    // Set Edited Log Status -------------------
    public static void SetMainDriverEditedLogStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_edited", value);
        editor.apply();
    }


    // Get Edited Log Status -------------------
    public static boolean IsMainDriverEdited( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("is_edited", false);
    }


    // Set co driverEdited Log Status -------------------
    public static void SetCoDriverEditedLogStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_edited_co", value);
        editor.apply();
    }


    // Get co driver Edited Log Status -------------------
    public static boolean IsCoDriverEditedData( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("is_edited_co", false);
    }



    // Set Edited Log Status -------------------
    public static void SetIgnitionOffCalled( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isIgnitionOffCalled", value);
        editor.apply();
    }


    // Get Edited Log Status -------------------
    public static boolean IsIgnitionOffCalled( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("isIgnitionOffCalled", false);
    }


    // Set Driver Id -------------------
    public static void setDriverId( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("DRIVER_ID", value);
        editor.apply();
    }
    // Get Driver Id -------------------
    public static String getDriverId( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("DRIVER_ID", "");
    }


    // Set Time Zone -------------------
    public static void setTimeZone( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("time_zone", value);
        editor.apply();
    }
    // Get Time Zone -------------------
    public static String getTimeZone( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("time_zone", "");
    }


    // Set Deferral Rule settings For Main driver -------------------
    public static void setDeferralForMain( boolean value, String startDate, String eventDay, Context context) {
        if(context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("deferral_main", value);
            editor.putString("deferral_start_date", startDate);
            editor.putString("deferral_day", eventDay);

            editor.apply();
        }
    }

    // Get Main driver's Deferral Rule settings -------------------
    public static boolean isDeferralMainDriver( Context context) {
        boolean isDefferal = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isDefferal = preferences.getBoolean("deferral_main", false);
        }
        return isDefferal;
    }


    // Get Main driver's Deferral Start Date -------------------
    public static String getDeferralDateMainDriver( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString("deferral_start_date", "");
        }
        return "";
    }

    // Get Main driver's Deferral Day -------------------
    public static String getDeferralDayMainDriver( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString("deferral_day", "0");
        }
        return "";
    }



    // Set Deferral Rule settings For Co driver -------------------
    public static void setDeferralForCo( boolean value, String startDate, String eventDay, Context context) {
        if(context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("deferral_co", value);
            editor.putString("deferral_co_start_date", startDate);
            editor.putString("deferral_co_day", eventDay);
            editor.apply();
        }
    }

    // Get Co driver's Deferral Rule settings -------------------
    public static boolean isDeferralCoDriver( Context context) {
        boolean isDefferal = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isDefferal = preferences.getBoolean("deferral_co", false);
        }
        return isDefferal;
    }


    // Get Co driver's Deferral Start Date -------------------
    public static String getDeferralDateCoDriver( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString("deferral_co_start_date", "");
        }
        return "";
    }

    // Get Co driver's Deferral Event Code -------------------
    public static String getDeferralDayCoDriver( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString("deferral_co_day", "0");
        }
        return "";
    }





    // Set 16 hour haul exception -------------------
    public static void set16hrHaulExcptn( boolean value, Context context) {
        if(context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("haul_exception", value);
            editor.apply();
        }
    }

    // Get 16 hour haul exception -------------------
    public static boolean get16hrHaulExcptn( Context context) {
        boolean isHaul = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isHaul = preferences.getBoolean("haul_exception", false);
        }
        return isHaul;
    }



    // Set 16 hour haul exception for co driver  -------------------
    public static void set16hrHaulExcptnCo( boolean value, Context context) {
        if(context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("haul_exception_co", value);
            editor.apply();
        }
    }

    // Get 16 hour haul exception for co driver  -------------------
    public static boolean get16hrHaulExcptnCo( Context context) {
        boolean isHaul = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isHaul = preferences.getBoolean("haul_exception_co", false);
        }
        return isHaul;

    }


    // Set Adverse exception -------------------
    public static void setAdverseExcptn( boolean value, Context context) {
        if(context  != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("adverse_exception", value);
            editor.apply();
        }
    }

    // Get Adverse exception -------------------
    public static boolean getAdverseExcptn( Context context) {
        boolean isAdverse = false;
        if(context  != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isAdverse = preferences.getBoolean("adverse_exception", false);
        }

        return isAdverse;
    }



    // Set Adverse exception for co driver -------------------
    public static void setAdverseExcptnCo( boolean value, Context context) {
        if(context  != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("adverse_exception_co", value);
            editor.apply();
        }
    }

    // Get Adverse exception for co driver -------------------
    public static boolean getAdverseExcptnCo( Context context) {
        boolean isAdverse = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isAdverse = preferences.getBoolean("adverse_exception_co", false);
        }
        return isAdverse;

    }


    // Set Current UTC Time -------------------
    public static void setCurrentUTCTime( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("utc_current_time", value);
        editor.apply();
    }
    // Get Current UTC Time -------------------
    public static String getCurrentUTCTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("utc_current_time", "");
    }

    // Set Driver First Login Time -------------------
    public static void setDriverFirstLoginTime( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("DriverFirstLoginTime", value);
        editor.apply();
    }

    // Get Driver First Login Time -------------------
    public static String getDriverFirstLoginTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("DriverFirstLoginTime", "");
    }

    // Set CoDriver First Login Time -------------------
    public static void setCoDriverFirstLoginTime( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CoDriverFirstLoginTime", value);
        editor.apply();
    }

    // Get CoDriver First Login Time -------------------
    public static String getCoDriverFirstLoginTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("CoDriverFirstLoginTime", "");
    }


    // Set login time (UTC) and occurrence event status -------------------
    public static void setLoginTimeUTC( String value, boolean isLoginEvent, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("login_time_utc", value);
        editor.putBoolean("is_login_event_occ", isLoginEvent);
        editor.apply();
    }

    // Get login time (UTC)  -------------------
    public static String getLoginTimeUTC( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("login_time_utc", "");
    }




    // Set Automatic Time setting Alert status with date -------------------
    public static void setAutomaticTimeAlertWithStatus( String time, boolean isAlertShown, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("AutomaticTimeAlert", time);
        editor.putBoolean("IsAutomaticTimeAlert", isAlertShown);
        editor.apply();
    }

    // Get AutomaticTime setting Alert date -------------------
    public static String getAutomaticAlertTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("AutomaticTimeAlert", "");
    }

    // Get AutomaticTime setting Alert display status -------------------
    public static boolean IsAutomaticTimeAlertShown( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("IsAutomaticTimeAlert", false);
    }

    // get Login Missing Event Occurrence -------------------
    public static boolean isLoginMissingEventOcc( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("is_login_event_occ", false);
    }

    // Get background Service OnDestory method status -------------------
    public static boolean isServiceOnDestoryCalled( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("service_on_destroy", false);
    }

    // Save background Service OnDestory Called method status -------------------
    public static void setServiceOnDestoryStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("service_on_destroy", value);
        editor.apply();
    }


    // Get personal use status for 75 km crossing -------------------
    public static boolean isPersonalUse75KmCrossed( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.PersonalUse75Km, false);
    }

    // Save personal use status, selected or not  -------------------
    public static void setPersonalUse75Km( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.PersonalUse75Km, value);
        editor.apply();
    }


/*

    // Get personal use status, selected or not -------------------
    public static boolean isPersonalUseSelected( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.PersonalUseSelected, false);
    }

    // Save personal use status for 75 km crossing -------------------
    public static void setPersonalUseSelected( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.PersonalUseSelected, value);
        editor.apply();
    }
*/


    // Get pDriving Allowing Status -------------------
    public static boolean isDrivingAllowed( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.DrivingAllowedStatus, true);
    }

    public static String getDrivingAllowedTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(ConstantsKeys.DrivingAllowedStatusTime, "");
    }

    // Save Driving Allowing Status with time-------------------
    public static void setDrivingAllowedStatus( boolean value, String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.DrivingAllowedStatus, value);
        editor.putString(ConstantsKeys.DrivingAllowedStatusTime, time);
        editor.apply();
    }



    // Save Co-Driver Switching status  -------------------
    public static void saveCoDriverSwitchingStatus( boolean IsEngSyncDia, Context context) {
        if(context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(ConstantsKeys.CoDriverSwitching, IsEngSyncDia);
            editor.apply();
        }
    }

    // Get Co Driver Switching status -------------------
    public static boolean isCoDriverSwitching( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.CoDriverSwitching, false);
        }else {
            return false;
        }
    }


    // Save Engine sync diagnostic status  -------------------
    public static void saveEngSyncDiagnstcStatus( boolean IsEngSyncDia, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.EngSyncDiagnstc, IsEngSyncDia);

        editor.apply();
    }

    // Get Engine sync diagnostic status -------------------
    public static boolean isEngSyncDiagnstc( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.EngSyncDiagnstc, false);
        }else {
            return false;
        }
    }


    // Save co driver Engine sync diagnostic status  -------------------
    public static void saveCoDriverEngSyncDiaStatus( boolean IsEngSyncDia, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.EngSyncDiagnstcCo, IsEngSyncDia);

        editor.apply();
    }

    // Getco driver Engine sync diagnostic status -------------------
    public static boolean isCoDriverEngSyncDia( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.EngSyncDiagnstcCo, false);
        }else {
            return false;
        }
    }



    // Save others malfunction/Diagnostic status
    public static void saveOtherMalDiaStatus( boolean UnidentifiedDiag, boolean IsDuplicateStatusAllowed, boolean DataTransferDiag,
                                                   boolean MissingDataDiag, boolean DataTransferComplMal,  boolean DataRecComMal,
                                                   boolean TimingCompMal, Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(ConstantsKeys.UnidentifiedDiag, UnidentifiedDiag);
        editor.putBoolean(ConstantsKeys.IsDuplicateStatusAllowed, IsDuplicateStatusAllowed);
        editor.putBoolean(ConstantsKeys.DataTransferDiag, DataTransferDiag);

        editor.putBoolean(ConstantsKeys.MissingDataDiag, MissingDataDiag);
        editor.putBoolean(ConstantsKeys.DataTransferComplMal, DataTransferComplMal);

        editor.putBoolean(ConstantsKeys.DataRecComMal, DataRecComMal);
        editor.putBoolean(ConstantsKeys.TimingCompMal, TimingCompMal);


        editor.apply();
    }


    // Get others malfunction/Diagnostic status -------------------
    public static boolean GetOtherMalDiaStatus(String key,  Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(key, false);
        }else {
            return false;
        }
    }



    // Save particular malfunction/Diagnostic status
    public static void saveParticularMalDiaStatus( boolean PowerComplianceMal, boolean EnginSyncMal, boolean PostioningComplMal,
                                                   boolean PowerDataDiag, boolean EnginSyncDiag, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(ConstantsKeys.PowerComplianceMal, PowerComplianceMal);
        editor.putBoolean(ConstantsKeys.EnginSyncMal, EnginSyncMal);
        editor.putBoolean(ConstantsKeys.PostioningComplMal, PostioningComplMal);

        editor.putBoolean(ConstantsKeys.PowerDataDiag, PowerDataDiag);
        editor.putBoolean(ConstantsKeys.EnginSyncDiag, EnginSyncDiag);

        editor.apply();
    }


    // Get particular malfunction/Diagnostic status -------------------
    public static boolean GetParticularMalDiaStatus(String key,  Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(key, false);
        }else {
            return false;
        }
    }


    // Save particular malfunction/Diagnostic status
    public static void saveTimingMalfunctionStatus( boolean TimingComplianceMal, String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(ConstantsKeys.TimingComplianceMal, TimingComplianceMal);
        editor.putString(ConstantsKeys.TimingComplianceMalTime, time);

        editor.apply();
    }


    // Get Timing Compliance malfunction status -------------------
    public static boolean IsTimingMalfunction(Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.TimingComplianceMal, false);
        }else {
            return false;
        }
    }

    // Get Timing Compliance malfunction occurred time -------------------
    public static String getTimingMalTime(Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.TimingComplianceMalTime, "");
        }else {
            return "";
        }
    }


    // save Timing Compliance malfunction warning time -------------------
    public static void saveTimingMalfunctionWarningTime( String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(ConstantsKeys.TimingMalWarningTime, time);

        editor.apply();
    }


    // Get Timing Compliance malfunction warning time -------------------
    public static String getTimingMalWarningTime(Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.TimingMalWarningTime, "");
        }else {
            return "";
        }
    }


    // Save Unidentified event status  -------------------
    public static void saveUnidentifiedEventStatus( boolean isUnidentifiedDia, String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.UnidentifiedDataDiag, isUnidentifiedDia);
        editor.putString(ConstantsKeys.UnidentifiedOccTime, time);
        editor.apply();
    }

    // Get Unidentified event status -------------------
    public static boolean isUnidentifiedDiaEvent( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.UnidentifiedDataDiag, false);
        }else {
            return false;
        }
    }

    // Get Unidentified event occurred time  -------------------
    public static String getUnidentifiedDiaOccTime( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.UnidentifiedOccTime, "");
        }else {
            return "";
        }
    }



    // Save Power Malfunction status  -------------------
    public static void savePowerMalfunctionOccurStatus( boolean IsEngSyncMal, boolean isEngSynDia, String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.PowerMalfunction, IsEngSyncMal);
        editor.putBoolean(ConstantsKeys.PowerDiagnstc, isEngSynDia);
        editor.putString(ConstantsKeys.PowerMalfunctionTimeOcc, time);

        editor.apply();
    }

    // Get power Malfunction status -------------------
    public static boolean isPowerMalfunctionOccurred( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.PowerMalfunction, false);
        }else {
            return false;
        }
    }

    // Get power Diagnostic status -------------------
    public static boolean isPowerDiagnosticOccurred( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.PowerDiagnstc, false);
        }else {
            return false;
        }
    }


    // Get power Malfunction occurred time  -------------------
    public static String getPowerMalOccTime( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.PowerMalfunctionTimeOcc, "");
        }else {
            return "";
        }
    }


    // Save Engine sync diagnostic event also when power diagnostic occurred and odometer was not changed or less then equal to 1 km  ----------------
    public static void saveEngSyncEventAlso( boolean IsEngSyncDia, String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.EngSyncDiagnosticAlso, IsEngSyncDia);
        editor.putString(ConstantsKeys.EngSyncDiagnosticTimeAlso, savedTime);

        editor.apply();
    }


    // if true, save engine sync Diagnostic event also when power diagnostic occurred -------------------
    public static boolean isAlsoSaveEngSyncDiaEvent( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.EngSyncDiagnosticAlso, false);
        }else {
            return false;
        }
    }








    // Get also saved engine sync event time  -------------------
    public static String alsoSavedEngSyncDiaEventTime( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.EngSyncDiagnosticTimeAlso, "");
        }else {
            return "";
        }
    }



    // Save Engine sync Malfunction status  ----------------
    public static void saveEngSyncMalfunctionStatus( boolean IsEngSyncDia, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.EngSyncMalfunction, IsEngSyncDia);

        editor.apply();
    }

    // Get Engine sync Malfunction status -------------------
    public static boolean isEngSyncMalfunction( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.EngSyncMalfunction, false);
        }else {
            return false;
        }
    }




    // Save Missing Diagnostic status those occurred in obd disconnect time -------------------
    public static void saveMissingDiaStatus( boolean IsLocMalfunction, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.MissingDiagnostic, IsLocMalfunction);

        editor.apply();
    }

    // Get issing Diagnostic occured status -------------------
    public static boolean isMissingDiaOccur( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.MissingDiagnostic, false);
        }else {
            return false;
        }
    }




    // Save location malfunction status  -------------------
    public static void saveLocMalfunctionOccurStatus( boolean IsLocMalfunction, String time, String utcTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsLocMalfunction, IsLocMalfunction);
        editor.putString(ConstantsKeys.LocMalfunctionOccurTime, time);
        editor.putString(ConstantsKeys.LocMalfunctionOccurUtcTime, utcTime);

        editor.apply();
    }

    // Get location Malfunction occured status -------------------
    public static boolean isLocMalfunctionOccur( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.IsLocMalfunction, false);
        }else {
            return false;
        }
    }


    // Get location Malfunction occured time -------------------
    public static String getLocMalfunctionOccuredTime( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.LocMalfunctionOccurTime, "");
        }else {
            return "";
        }
    }

    // Get location Malfunction occured utc time -------------------
    public static String getLocMalfunctionOccuredUtcTime( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.LocMalfunctionOccurUtcTime, "");
        }else {
            return "";
        }
    }


    // Save location Diagnostic status  -------------------
    public static void saveLocDiagnosticStatus( boolean IsLocDiagnostic, String time, String utcTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsLocDiagnostic, IsLocDiagnostic);
        editor.putString(ConstantsKeys.LocDiaOccurTime, time);
        editor.putString(ConstantsKeys.LocDiaOccurUtcTime, utcTime);
        editor.apply();
    }

    // Get location Diagnostic occurred status -------------------
    public static boolean isLocDiagnosticOccur( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.IsLocDiagnostic, false);
        }else {
            return false;
        }
    }



    // Set Storage Malfunction Status -------------------
    public static void SetStorageMalfunctionStatus( boolean CCMTACertified, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsStorageMalfunction, CCMTACertified);
        editor.apply();
    }



    // Get Storage Malfunction Status  -------------------
    public static boolean IsStorageMalfunction(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsStorageMalfunction, false);
        }
        return isRecord;


    }


    // Get location Diagnostic occured time -------------------
    public static String getLocDiagnosticOccuredTime( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.LocDiaOccurTime, "");
        }else {
            return "";
        }
    }

    // Get location Diagnostic occured utc time -------------------
    public static String getLocDiagnosticOccuredUtcTime( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(ConstantsKeys.LocDiaOccurUtcTime, "");
        }else {
            return "";
        }
    }




    // Save alert status settings -------------------
    public static void setEldOccurences( boolean IsUnidentified, boolean IsMalfunction, boolean IsDiagnostic,
                                            boolean SuggestedEdit, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsUnidentified, IsUnidentified);
        editor.putBoolean(ConstantsKeys.IsMalfunction, IsMalfunction);
        editor.putBoolean(ConstantsKeys.IsDiagnostic, IsDiagnostic);
        editor.putBoolean(ConstantsKeys.SuggestedEdit, SuggestedEdit);

        editor.apply();
    }


    // Get Unidentified occure status  -------------------
    public static boolean isUnidentifiedOccur( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.IsUnidentified, false);
        }else{
            return false;
        }
    }

    // Get Malfunction occur status -------------------
    public static boolean isMalfunctionOccur( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.IsMalfunction, false);
        }else {
            return false;
        }
    }

    // Get Diagnostic occur status -------------------
    public static boolean isDiagnosticOccur( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.IsDiagnostic, false);
        }else {
            return false;
        }
    }

    // Get Suggested Edit status  -------------------
    public static boolean isSuggestedEditOccur( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isSuggestedEditOccur = preferences.getBoolean(ConstantsKeys.SuggestedEdit, false);
            return isSuggestedEditOccur;
        }else{
            return false;
        }
    }



    //  Get suggested recall status-------------------
    public static boolean isSuggestedRecall( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean("suggested_log_recall", true);
        }else{
            return true;
        }
    }

    // Save suggested recall status -------------------
    public static void setSuggestedRecallStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("suggested_log_recall", value);
        editor.apply();
    }


    // Save alert status settings for co driver -------------------
    public static void setEldOccurencesCo( boolean IsUnidentified, boolean IsMalfunction, boolean IsDiagnostic,
                                         boolean SuggestedEdit, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsUnidentifiedCo, IsUnidentified);
        editor.putBoolean(ConstantsKeys.IsMalfunctionCo, IsMalfunction);
        editor.putBoolean(ConstantsKeys.IsDiagnosticCo, IsDiagnostic);
        editor.putBoolean(ConstantsKeys.SuggestedEditCo, SuggestedEdit);

        editor.apply();
    }

    // Get Unidentified occure status for co driver -------------------
    public static boolean isUnidentifiedOccurCo( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.IsUnidentifiedCo, false);
        }else {
            return false;
        }
    }

    // Get Malfunction occur status for co driver -------------------
    public static boolean isMalfunctionOccurCo( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.IsMalfunctionCo, false);
        }else{
            return false;
        }
    }

    // Get Diagnostic occur statusfor co driver  -------------------
    public static boolean isDiagnosticOccurCo( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.IsDiagnosticCo, false);
        }else{
            return false;
        }
    }

    // Get Suggested Edit status for co driver -------------------
    public static boolean isSuggestedEditOccurCo( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.SuggestedEditCo, false);
        }else{
            return false;
        }
    }


    //  Get suggested recall status-------------------
    public static boolean isSuggestedRecallCo( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean("suggested_log_recallCo", true);
        }else{
            return true;
        }
    }

    // Save suggested recall status -------------------
    public static void setSuggestedRecallStatusCo( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("suggested_log_recallCo", value);
        editor.apply();
    }




    // Get driver status he is online or not-------------------
    public static boolean isOnline( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("status_online", true);
    }

    // Save driver online/offline status -------------------
    public static void setOnlineStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("status_online", value);
        editor.apply();
    }

    public static List<String> getStatesInList(Context context) {

        int stateListSize = 0;
        List<String> StateArrayList = new ArrayList<String>();
        List<DriverLocationModel> StateList = new ArrayList<DriverLocationModel>();

        StatePrefManager statePrefManager = new StatePrefManager();

        try {
            StateList = statePrefManager.GetState(context);
            stateListSize = StateList.size();
        } catch (Exception e) {
            stateListSize = 0;
        }

        for (int i = 0; i < stateListSize; i++) {
            StateArrayList.add(StateList.get(i).getState());
        }
      //  StateArrayList.add(0, "Select");
        return StateArrayList;
    }





    // Set Obd Speed -------------------
    public static void setObdSpeed( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("obd_speed", value);
        editor.apply();
    }
    // Get Obd Speed -------------------
    public static String getObdSpeed( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("obd_speed", "[]");
    }

    // Set Refresh Data Time -------------------
    public static void setRefreshDataTime(String dateTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("data_refresh_time", dateTime);
        editor.apply();
    }

    // Get Refresh Data Time -------------------
    public static String getRefreshDataTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("data_refresh_time", "");
    }


    // Set location change Time -------------------
    public static void setLocChangeTime(String dateTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("loc_change_time", dateTime);
        editor.apply();
    }

    // Get location change Time -------------------
    public static String getLocChangeTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("loc_change_time", "");
    }


    public JSONArray ReverseArray(JSONArray array){
        JSONArray reversedArray = new JSONArray();

        for(int i = array.length()-1 ; i >= 0  ; i--){
            try {
                JSONObject obj = (JSONObject)array.get(i);
                reversedArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return reversedArray;
    }


    // =========================================== OBD pref method ====================================================================

    // Save Engine Hours
    public static void setEngineHours(String VIN, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("EngineHours", VIN);
        editor.apply();

    }


    // Get Engine Hours -------------------
    public static String getEngineHours( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("EngineHours", "--");
    }



    // Set Ignition Status -------------------
    public static void setIgnitionStatus(String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ignition", value);
        editor.apply();
    }

    // Get Ignition Status -------------------
    public static String getIgnitionStatus(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ignition", "--");
    }




    // Set Trip Distance -------------------
    public static void setTripDistance( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TripDistance", value);
        editor.apply();
    }

    // Get Trip Distance -------------------
    public static String getTripDistance(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("TripDistance", "0");
    }



    // Set RPM value-------------------
    public static void setRPM( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("rpm", value);
        editor.apply();
    }

    // Get RPM value -------------------
    public static String getRPM(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("rpm", "--");
    }




    // Set VSS value -------------------
    public static void setVss( int value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("vss", value);
        editor.apply();
    }

    // Get VSS value -------------------
    public static int getVss(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("vss", -1);
    }




    // Set VSS value -------------------
    public static void setVehicleVin( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("VehicleVin", value);
        editor.apply();
    }

    // Get VSS value -------------------
    public static String getVehicleVin(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("VehicleVin", "");
    }



    // Set obd data Time Stamp -------------------
    public static void setContinueSpeedZeroTime( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TimeStamp", value);
        editor.apply();
    }

    // Set obd data Time Stamp -------------------
    public static String getContinueSpeedZeroTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("TimeStamp", "--");
    }


    // Set wired Obd Odometer -------------------
    public static void SetObdOdometer( String value, Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("wired_obd_odometer", value);
        editor.apply();
    }

    // Get wired Obd Odometer -------------------
    public static String getObdOdometer(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String wired_obd_odometer = preferences.getString("wired_obd_odometer", "0");
        if(wired_obd_odometer.trim().equals("null") || wired_obd_odometer.length() == 0){
            wired_obd_odometer = "0";
        }
        return wired_obd_odometer;

    }


    // Set wired Obd Odometer in miles -------------------
    public static void SetObdOdometerInMiles( String value, Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("wired_obd_odometer_miles", value);
        editor.apply();
    }

    // Get wired Obd Odometer in miles-------------------
    public static String getObdOdometerInMiles(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String wired_obd_odometer_miles = preferences.getString("wired_obd_odometer_miles", "0");
        if(wired_obd_odometer_miles.trim().equals("null") || wired_obd_odometer_miles.length() == 0){
            wired_obd_odometer_miles = "0";
        }
        return wired_obd_odometer_miles;



    }



    // Set obd_engine_hours  -------------------
    public static void SetObdEngineHours( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("obd_engine_hours", value);
        editor.apply();
    }

    // Get obd_engine_hours -------------------
    public static String getObdEngineHours(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("obd_engine_hours", "0");
    }




    // Set Day start Odometer value  -------------------
    public static void setDayStartOdometer( String odometerKM,  String odometerMiles, String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("dayStartOdometerKm", odometerKM);
        editor.putString("dayStartOdometerMiles", odometerMiles);
        editor.putString("dayStartSavedTime", savedTime);
        editor.apply();
    }


    // Get Day start Odometer value in km -------------------
    public static String getDayStartOdometerKm(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String dayStartOdometerKm = preferences.getString("dayStartOdometerKm", "0");
        if(dayStartOdometerKm.trim().equals("null") || dayStartOdometerKm.length() == 0){
            dayStartOdometerKm = "0";
        }
        return dayStartOdometerKm;

    }

    // Get Day start Odometer value in miles -------------------
    public static String getDayStartOdometerMiles(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("dayStartOdometerMiles", "0");
    }


    // Get Day start Odometer Saved time -------------------
    public static String getDayStartSavedTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("dayStartSavedTime", "");
    }


    // Set High Precision Odometer value  -------------------
    public static void saveHighPrecisionOdometer( String odometer, String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("highPrecisionOdometer", odometer);
        editor.putString("HighPrecesionSavedTime", savedTime);
        Logger.LogDebug("HighPrecesionSavedTime", "HighPrecesionSavedTime: " +savedTime);
        editor.apply();
    }

    // Get High Precision Odometer value -------------------
    public static String getHighPrecisionOdometer(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("highPrecisionOdometer", "0");
    }

    // Get High Precision Odometer Saved time -------------------
    public static String getHighPrecesionSavedTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("HighPrecesionSavedTime", "");
    }



    // Set Pc Ym Alert Call time   -------------------
    public static void savePcYmAlertCallTime( String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("PcYmAlertCallTime", savedTime);
        editor.apply();
    }

    // Get Pc Ym Alert Call time -------------------
    public static String getPcYmAlertCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("PcYmAlertCallTime", "");
    }


    // Set Clear Event Called Time  -------------------
    public static void setClearEventCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ClearEventCalledTime", savedTime);
        editor.apply();
    }

    // Get Clear Event last Called Time -------------------
    public static String getClearEventCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ClearEventCalledTime", "");
    }


    // Set power Clear Event Called Time  -------------------
    public static void setPowerClearEventCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("PowerClearEventCalledTime", savedTime);
        editor.apply();
    }

    // Get power Clear Event last Called Time -------------------
    public static String getPowerClearEventCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("PowerClearEventCalledTime", "");
    }


    // Set power Clear Event Called Time  -------------------
    public static void setEngineClearEventCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("EngClearEventCalledTime", savedTime);
        editor.apply();
    }

    // Get power Clear Event last Called Time -------------------
    public static String getEngineClearEventCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("EngClearEventCalledTime", "");
    }



    // Set Engine Sync malfunction Event Called Time  -------------------
    public static void setEngSyncMalEventCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("EngSyncMalEventCallTime", savedTime);
        editor.apply();
    }

    // Get Engine Sync malfunction Event last Called Time -------------------
    public static String getEngSyncMalEventCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("EngSyncMalEventCallTime", "");
    }

    // Set Clear malfunction Event Called Time  -------------------
    public static void setClearMalCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ClearMalCalledTime", savedTime);
        editor.apply();
    }

    // Get Clear malfunction event last Called Time -------------------
    public static String getClearMalCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ClearMalCalledTime", "");
    }


    // Set Eng Sync Clear Event Called Time  -------------------
    public static void setEngSyncClearEventCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("EngSyncClearEventCalledTime", savedTime);
        editor.apply();
    }

    // Get Eng Sync Clear Event last Called Time -------------------
    public static String getEngSyncClearEventCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("EngSyncClearEventCalledTime", "");
    }




    // Set Malfunction Called Time  -------------------
    public static void setMalfCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("MalfunctionCalledTime", savedTime);
        editor.apply();
    }

    // Get Malfunction last Called Time -------------------
    public static String getLastMalfCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("MalfunctionCalledTime", "");
    }


    // Set Malfunction Called Time for Engine Sync -------------------
    public static void setEngSyncMalCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.MalCalledLastTime, savedTime);
        editor.apply();
    }

    // Get Malfunction last Called Time for Engine Sync -------------------
    public static String getEngSyncLastCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(ConstantsKeys.MalCalledLastTime, "");
    }



    // Set Obd Write Called Time   -------------------
    public static void setObdWriteCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ObdWriteCallTime", savedTime);
        editor.apply();
    }

    // Get Obd Write Called Time  -------------------
    public static String getObdWriteCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ObdWriteCallTime", "");
    }


    // Set Malfunction Called Time for Engine details -------------------
    public static void saveMalCalledEngineDetails( String MalCalledLastOdo, String MalCalledLastEngHr,
                                                 String MalCalledLastRpm, String MalCalledLastSpeed,
                                                   String MalCalledLastEngIgntn, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.MalCalledLastOdo, MalCalledLastOdo);
        editor.putString(ConstantsKeys.MalCalledLastEngHr, MalCalledLastEngHr);
        editor.putString(ConstantsKeys.MalCalledLastRpm, MalCalledLastRpm);
        editor.putString(ConstantsKeys.MalCalledLastSpeed, MalCalledLastSpeed);
        editor.putString(ConstantsKeys.MalCalledLastEngIgntn, MalCalledLastEngIgntn);

        editor.apply();
    }

    // Get Malfunction last Called Time for Engine Sync -------------------
    public static String getEngSyncLastCallDetails(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "0");
    }



    // Save notification show time in logout service
    public static void setNotiShowTime(String count, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("NotiShowTime", count);
        editor.apply();

    }


    // Get notification show time in logout service -------------------
    public static String getNotiShowTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("NotiShowTime", "");
    }



    // Set Ecm Obd location with time  -------------------
    public static void setEcmObdLocationWithTime( String lat, String lon, String odometer, String time, String utcTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ecmObdLat", lat);
        editor.putString("ecmObdLon", lon);
        editor.putString("ecmOdometer", odometer);
        editor.putString("ecmObdTime", time);
        editor.putString("ecmObdUtcTime", utcTime);

        editor.apply();
    }

    // Get Ecm Obd Latitude of last saved location -------------------
    public static String getEcmObdLatitude(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ecmObdLat", "0");
    }

    // Get Ecm Obd Langitude of last saved location -------------------
    public static String getEcmObdLongitude(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ecmObdLon", "0");
    }

    // Get Ecm odometer of last saved from obd -------------------
    public static String getEcmOdometer(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ecmOdometer", "0");
    }


    // Get Ecm Obd Time of last saved location -------------------
    public static String getEcmObdTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ecmObdTime", "");
    }

    // Get Ecm Obd UTCTime of last saved location -------------------
    public static String getEcmObdUtcTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ecmObdUtcTime", "");
    }


    // save Location Malfunction Type (x,m,e) -------------------
    public static void setLocationEventType( String type, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ecmMalfunctionType", type);

        editor.apply();
    }

    // Get Location Malfunction Type -------------------
    public static String getLocationEventType(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ecmMalfunctionType", "");
    }


    // save Malfunction status for Manual Location input -------------------
    public static void saveManualLocStatus( boolean type, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("ecmManualLocStatus", type);

        editor.apply();
    }

    // Get Malfunction status for Manual Location input -------------------
    public static boolean isManualLocAccepted(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("ecmManualLocStatus", false);
    }



    // ===========================Save Current Cycle with details ===========================
    public static void SetCycleOfflineDetails(String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("cycleDetails", value);
        editor.apply();
    }

    // =========================== Get Offline Data Status ===========================
    public static String GetCycleDetails(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("cycleDetails", "[]");
    }



    // =========================== Save Last OBD Type with Time ===========================
    public static void SaveConnectionInfo(String type, String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.CONNECTION_TYPE, type);
        editor.putString(Constants.LAST_SAVED_TIME, time);
        editor.apply();
    }

    // =========================== Get OBD saved time with Type===========================
    public static String GetConnectionInfo(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    // Get Last Usage Data Saved Time -------------------
    public static void setLastUsageDataSavedTime( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.DATA_USAGE_TIME, value);
        editor.apply();
    }

    // Get Last Usage Data Saved Time -------------------
    public static String getLastUsageDataSavedTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.DATA_USAGE_TIME, "");
    }


    // Get Background Service Running Time -------------------
    public static void setBgServiceRunningTime( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.ServiceRunningTime, value);
        editor.apply();
    }

    // Get Background Service Running Time -------------------
    public static String getBgServiceRunningTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.ServiceRunningTime, "");
    }


    // Set UnAssigned Vehicle Miles Id -------------------
    public static void setUnAssignedVehicleMilesId( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("UnAssignedVehicleMilesId", value);
        editor.apply();
    }
    // GetUnAssigned Vehicle Miles Id  -------------------
    public static String getUnAssignedVehicleMilesId( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String UnAssignedVehicleMilesId = preferences.getString("UnAssignedVehicleMilesId", "0");
        if(UnAssignedVehicleMilesId.equals("null") || UnAssignedVehicleMilesId.length() == 0){
            UnAssignedVehicleMilesId = "0";
        }
        return UnAssignedVehicleMilesId;
    }

    // Set  IntermediateLog Id -------------------
    public static void setIntermediateLogId( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("IntermediateLogId", value);
        editor.apply();
    }
    // Get  IntermediateLog Id -------------------
    public static String getIntermediateLogId( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String IntermediateLogId = preferences.getString("IntermediateLogId", "0");

        if(IntermediateLogId.equals("null") || IntermediateLogId.length() == 0){
            IntermediateLogId = "0";
        }

        return IntermediateLogId;
    }


    // Set UnIdentified Last saved Duty Status -------------------
    public static void setUnIdenLastDutyStatus( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("UnIdentifiedLastDutyStatus", value);
        editor.apply();
    }


    // Get UnIdentified Last saved Duty Status -------------------
    public static String getUnIdenLastDutyStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("UnIdentifiedLastDutyStatus", "");
    }


    // Set UnidentifiedRecord Intermediate Record -------------------
    public static void SaveUnidentifiedIntermediateRecord( String startOdometer, String startTime,String startLatitude,String startLongitude,String startEngineSecond, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.UnidenStartOdometer, startOdometer);
        editor.putString(ConstantsKeys.UnidenStartTime, startTime);
        editor.putString(ConstantsKeys.UnidenStartLatitude, startLatitude);
        editor.putString(ConstantsKeys.UnidenStartLongitude, startLongitude);
        editor.putString(ConstantsKeys.UnidenStartEngineSeconds, startEngineSecond);
        editor.apply();
    }


    public static String getUnidentifiedIntermediateRecord( String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    // Set Agriculture Record -------------------
    public static void SaveAgricultureRecord( String agricultureLatitude,String agricultureLongitude,String agricultureAddress, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.AgricultureLatitude, agricultureLatitude);
        editor.putString(ConstantsKeys.AgricultureLongitude, agricultureLongitude);
        editor.putString(ConstantsKeys.AgricultureAddress, agricultureAddress);
        editor.apply();
    }


    public static String getAgricultureRecord( String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    // Set Adverse exception for co driver -------------------
    public static void setAgricultureExemption( boolean value, Context context) {
        if(context  != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("agriculture_Exception", value);
            editor.apply();
        }
    }

    // Get Adverse exception for co driver -------------------
    public static boolean getAgricultureExemption( Context context) {
        boolean isAdverse = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isAdverse = preferences.getBoolean("agriculture_Exception", false);
        }
        return isAdverse;

    }

    // Set Auto Sync log Status -------------------
    public static void setAutoSyncStatus( boolean value, Context context) {
        if(context  != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("AutoSyncStatus", value);
            editor.apply();
        }
    }

    // Get Auto Sync log Status -------------------
    public static boolean IsAutoSync( Context context) {
        boolean AutoSyncStatus = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            AutoSyncStatus = preferences.getBoolean("AutoSyncStatus", false);
        }
        return AutoSyncStatus;

    }

    // Set Day night mode -------------------
    public static void setDayNightMode( boolean value, Context context) {
        if(context  != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("day_night_mode", value);
            editor.apply();
        }
    }

    // Get Day night mode -------------------
    public static boolean getDayNightMode( Context context) {
        boolean isDayNightMode = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isDayNightMode = preferences.getBoolean("day_night_mode", false);
        }
        return isDayNightMode;

    }

    // Set Day night Action Click -------------------
    public static void setDayNightActionClick( boolean value, Context context) {
        if(context  != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("day_night_action", value);
            editor.apply();
        }
    }

    // Get Day night Action Click -------------------
    public static boolean getDayNightActionClick( Context context) {
        boolean isDayNightAction = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isDayNightAction = preferences.getBoolean("day_night_action", false);
        }
        return isDayNightAction;

    }

    // Save Obd Status After Login  -------------------
    public static void setObdStatusAfterLogin( boolean isUnidentifiedDia,Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.ConnectedAfterLogin, isUnidentifiedDia);
        editor.apply();
    }

    // Get Obd Status After Login -------------------
    public static boolean isObdWasConnectedAfterLogin( Context context) {
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(ConstantsKeys.ConnectedAfterLogin, false);
        }else {
            return false;
        }
    }


    public static boolean IsDriverLogin(Context context){

        if(context != null) {
            if (!SharedPref.getUserName(context).equals("") &&
                    !SharedPref.getPassword(context).equals("")) {
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }



    // Get api call status, is already called or not -------------------
    public static boolean isApiAlreadyCalled(int ApiFlag, Globally Global, Context context) {
        boolean isApiAlreadyCalled = false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String apiList = preferences.getString(ConstantsKeys.ApiCallStatus, "[]");
        try{
            JSONArray array = new JSONArray(apiList);
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject obj = (JSONObject) array.get(i);
                int savedApiFlag = obj.getInt(ConstantsKeys.ApiFlag);
                if(savedApiFlag == ApiFlag){
                    String apiCalledDate = obj.getString(ConstantsKeys.ApiCalledDate);
                    String currentDate = Globally.GetDriverCurrentDateTime(new Globally(), context);

                    switch (ApiFlag){
                        case ConstantsEnum.GetOdometer:
                            int DayDiff = Constants.getDayDiff(apiCalledDate, currentDate);
                            if(DayDiff == 0){
                                isApiAlreadyCalled = true;
                            }else{
                                updateApiCallStatus(ApiFlag, true, Global, context);
                            }
                            break;

                        case ConstantsEnum.GetNotifications:
                            int minDiff = Constants.getMinDiff(apiCalledDate, currentDate);
                            if(minDiff > 30){
                                updateApiCallStatus(ApiFlag, true, Global, context);
                            }else{
                                isApiAlreadyCalled = true;
                            }
                            break;

                        case ConstantsEnum.GetDriverPermission:
                            int minDiffP = Constants.getMinDiff(apiCalledDate, currentDate);
                            if(minDiffP > 240){
                                updateApiCallStatus(ApiFlag, true, Global, context);
                            }else{
                                isApiAlreadyCalled = true;
                            }
                            break;

                        default:
                            isApiAlreadyCalled = obj.getBoolean(ConstantsKeys.IsAlreadyCalled);
                            break;

                    }

                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isApiAlreadyCalled;


    }

// need to add GetOdometerReading api check and 1 more

    public static void updateApiCallStatus(int ApiFlag, boolean isUpdate, Globally global,  Context context){

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();

            if(ApiFlag != 0) {
                String apiList = prefs.getString(ConstantsKeys.ApiCallStatus, "[]");
                JSONArray listArray = new JSONArray(apiList);


                boolean isAlreadyExist = false;
                for (int i = 0; i < listArray.length(); i++) {
                    JSONObject obj = (JSONObject) listArray.get(i);
                    int savedApi = obj.getInt(ConstantsKeys.ApiFlag);
                    if (savedApi == ApiFlag) {
                        if(isUpdate){
                            obj.put(ConstantsKeys.ApiCalledDate, Globally.GetCurrentDateTime(global, context));
                            listArray.put(i, obj);

                            // update api call time -----------
                            editor.putString(ConstantsKeys.ApiCallStatus, listArray.toString());

                        }else {
                            isAlreadyExist = true;
                        }

                        break;
                    }
                }

                if (!isAlreadyExist) {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put(ConstantsKeys.ApiFlag, ApiFlag);
                    itemObj.put(ConstantsKeys.IsAlreadyCalled, true);
                    itemObj.put(ConstantsKeys.ApiCalledDate, Globally.GetCurrentDateTime(new Globally(), context));

                    listArray.put(itemObj);

                    // Save api call status and add api name in list with response status -----------
                    editor.putString(ConstantsKeys.ApiCallStatus, listArray.toString());

                }

                // Save api call status and add api name in list with response status -----------
                if(!isUpdate) {
                    editor.putString(ConstantsKeys.ApiCallStatus, listArray.toString());
                }
            }else{
                // clear list is api is empty, only called in login response -----------
                editor.putString(ConstantsKeys.ApiCallStatus, "[]");
            }
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // Save onduty remarks -----------
    public static void saveOnDutyRemarks(String remarks, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.OnDutyRemarks, remarks);
        editor.apply();

    }


    // Get onDuty api remarks -------------------
    public static List<String> getOnDutyRemarks(JSONArray remarkArray, Context context) {
        List<String> onDutyRemarksList = new ArrayList<>();

        try{
            if(remarkArray.length() == 0){
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String apiList = preferences.getString(ConstantsKeys.OnDutyRemarks, "[]");
                remarkArray = new JSONArray(apiList);
            }

            if(remarkArray.length() > 0) {
                onDutyRemarksList.add("Select");
                for (int i = 0; i < remarkArray.length(); i++) {
                    JSONObject resultJson = (JSONObject) remarkArray.get(i);
                    String remarks = resultJson.getString("OnDutyRemarks");
                    onDutyRemarksList.add(remarks);
                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }

        return onDutyRemarksList;


    }

}
