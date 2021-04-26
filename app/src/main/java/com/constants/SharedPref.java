package com.constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
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



    public SharedPref() {
        super();
    }




    // Save Current Status
    public static void setCurrentStatus(int status, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("current_status", status);
        editor.commit();

    }


    // Get Current Status -------------------
    public static int getCurrentStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("current_status", -1);
    }



    public static void setVehilceMovingStatus(boolean status, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("VehilceMovingStatus", status);
        editor.commit();

    }


    // Get Current Status -------------------
    public static boolean isVehicleMoving( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("VehilceMovingStatus", false);
    }

    // Save VIN Number
    public static void setVINNumber(String VIN, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("VIN", VIN);
        editor.commit();

    }


    // Get VIN Number -------------------
    public static String getVINNumber( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("VIN", "false");
    }



    // Set UTC Time Zone -------------------
    public static void setUTCTimeZone(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
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
        editor.commit();
    }

    // Get Trailor Number -------------------
    public static String getTrailorNumber(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("TrailorNumber", "");
    }

    // Set Current Saved Time -------------------
    public static void setCurrentDate( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_DATE, value);
        editor.commit();
    }
    // Get Current Saved Time -------------------
    public static String getSystemSavedDate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CURRENT_DATE, "");
    }


    // Set Current Saved Time -------------------
    public static void setUTCCurrentDate( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_UTC_DATE, value);
        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }


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
        editor.commit();
    }


    // Get auto drive status -------------------
    public static String getOBDRestartTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("obd_restart_date", "");
    }


    // Get Obd Status -------------------
    public static int getObdStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("ObdStatus", Constants.NO_CONNECTION);
    }


    // Get Obd Status -------------------
    public static String getObdLastStatusTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ObdStatusTime", "");
    }


    // Set Obd Status -------------------
    public static void SaveObdStatus( int value, String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("ObdStatus", value);
        editor.putString("ObdStatusTime", time);
        editor.commit();
    }



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
        editor.commit();
    }



    // Set Diagnostic and Malfunction Records action status for main driver-------------------
    public static void SetDiagnosticAndMalfunctionSettingsMain( boolean IsAllowMalfunction, boolean IsAllowDiagnostic,
                                                    boolean IsClearMalfunction, boolean IsClearDiagnostic, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsAllowMalfunction, IsAllowMalfunction);
        editor.putBoolean(ConstantsKeys.IsAllowDiagnostic, IsAllowDiagnostic);
        editor.putBoolean(ConstantsKeys.IsClearMalfunction, IsClearMalfunction);
        editor.putBoolean(ConstantsKeys.IsClearDiagnostic, IsClearDiagnostic);

        editor.commit();
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

        editor.commit();
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




    // Set Re-Certification and Unidentified Records allow status -------------------
    public static void SetCertifcnUnIdenfdSettings( boolean isReCertification, boolean IsUnidentifiedRecords,
                                                    boolean IsPersonal, boolean IsYardMove, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsAllowLogReCertification, isReCertification);
        editor.putBoolean(ConstantsKeys.IsShowUnidentifiedRecords, IsUnidentifiedRecords);
        editor.putBoolean(ConstantsKeys.IsPersonal, IsPersonal);
        editor.putBoolean(ConstantsKeys.IsYardMove, IsYardMove);
        editor.commit();
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
            editor.commit();

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
        editor.commit();
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
    public static void SetNorthCanadaStatusMain( boolean IsNorthCanada, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsNorthCanada, IsNorthCanada);
        editor.commit();
    }



    // Get North Canada Status for main driver -------------------
    public static boolean IsNorthCanadaMain(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsNorthCanada, false);
        }
        return isRecord;
    }



    // Set North Canada Status for co driver -------------------
    public static void SetNorthCanadaStatusCo( boolean IsNorthCanada, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsNorthCanadaCo, IsNorthCanada);
        editor.commit();
    }



    // Get North Canada Status for co driver -------------------
    public static boolean IsNorthCanadaCo(Context context) {

        boolean isRecord = false;
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isRecord = preferences.getBoolean(ConstantsKeys.IsNorthCanadaCo, false);
        }
        return isRecord;
    }




    // Set Exempt Driver Status for main driver -------------------
    public static void SetExemptDriverStatusMain( boolean ExemptDriver, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsExemptDriver, ExemptDriver);
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
            editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }


    // Get DOT Status -------------------
    public static boolean IsDOT( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("dot_status", false);
    }




    // Set Inspection Issues -------------------
    public static void setInspectionIssues( String truckValue, String traileValue, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ConstantsKeys.TruckIssues, truckValue);
        editor.putString(ConstantsKeys.TrailerIssues, traileValue);
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }


    // Get  Password -------------------
    public static String getPassword(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("password", "");
    }


    // Set  Driver Type -------------------
    public static void setDriverType( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DRIVER_TYPE, value);
        editor.commit();
    }

    // Get  Driver Type -------------------
    public static String getDriverType( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(DRIVER_TYPE, "");
    }


    // Set Current Driver Type -------------------
    public static void setCurrentDriverType( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_DRIVER_TYPE, value);
        editor.commit();
    }


    // Get Current Driver Type -------------------
    public static String getCurrentDriverType( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CURRENT_DRIVER_TYPE, "");
    }


    // Set Re-Certify Data -------------------
    public static void setReCertifyData( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ReCertifyData", value);
        editor.commit();
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
        editor.commit();
    }



    // Set Is Read Violation Status -------------------
    public static void SetIsReadViolation( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_read_vln", value);
        editor.commit();
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
            editor.commit();
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
            editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }



    // Set System Token -------------------
    public static void SetSystemToken( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("systemToken", value);
        editor.commit();
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
        editor.commit();
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
            editor.commit();
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
            editor.commit();
        }catch (Exception e){}
    }


    // Get Certify Alert View Time -------------------
    public static String getCertifyAlertViewTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("CertifyAlertViewTime", "");
    }


    // Set  Load Id -------------------
    public static void setLoadId( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LoadId", value);
        editor.commit();
    }
    // Get  Load Id -------------------
    public static String getLoadsId( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("LoadId", "");
    }





    // Set  Job Type -------------------
    public static void setDriverStatusId(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }


    // Get Job Type -------------------
    public static String getDriverStatusId(String key, Context context) {
        String DriverStatusId = "";
        try {
            if (context != null) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                DriverStatusId = preferences.getString(key, "");
            }
        }catch (Exception e){}
        return DriverStatusId;
    }


    // Set  Country Cycle -------------------
    public static void setCountryCycle(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }


    // Get Login Status -------------------
    public static boolean GetNewLoginStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("user_type", true);
    }


    // Set time when popup window is opened -------------------
    public static void SetUpdateAppDialogTime( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("update_popup_time", value);
        editor.commit();
    }


    // Get popup window is opened time -------------------
    public static String GetUpdateAppDialogTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("update_popup_time", "");
    }



    // Set Truck Start or Login Status for Yard move / Personal use -------------------
    public static void SetTruckStartLoginStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("new_truck_start_login", value);
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }


    // Get Odometer Saving Status -------------------
    public static boolean GetOdoSavingStatus( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("odo_status", false);
    }


    // Set Truck Ignition Status -------------------
    public static void SetTruckIgnitionStatusForContinue( String ignitionStatus, String ignitionSource, String lastIgnitionTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.TruckIgnitionStatus, ignitionStatus);
        editor.putString(Constants.IgnitionSource, ignitionSource);
        editor.putString(Constants.LastIgnitionTime, lastIgnitionTime);

        editor.commit();
    }


    // Get Truck Ignition Status -------------------
    public static String GetTruckIgnitionStatusForContinue( String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key , "ON");
    }



    // Set Edited Log Status -------------------
    public static void SetEditedLogStatus( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_edited", value);
        editor.commit();
    }


    // Get Edited Log Status -------------------
    public static boolean IsEditedData( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("is_edited", false);
    }



    // Set Driver Id -------------------
    public static void setDriverId( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("DRIVER_ID", value);
        editor.commit();
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
        editor.commit();
    }
    // Get Time Zone -------------------
    public static String getTimeZone( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("time_zone", "");
    }



    // Set 16 hour haul exception -------------------
    public static void set16hrHaulExcptn( boolean value, Context context) {
        if(context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("haul_exception", value);
            editor.commit();
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
            editor.commit();
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
            editor.commit();
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
            editor.commit();
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
        editor.commit();
    }
    // Get Current UTC Time -------------------
    public static String getCurrentUTCTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("utc_current_time", "");
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
        editor.commit();
    }


    // Get personal use status for 75 km crossing -------------------
    public static boolean isPersonalUse75KmCrossed( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ConstantsKeys.PersonalUse75Km, true);
    }

    // Save personal use status, selected or not  -------------------
    public static void setPersonalUse75Km( boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.PersonalUse75Km, value);
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
            return preferences.getBoolean(ConstantsKeys.SuggestedEdit, false);
        }else{
            return false;
        }
    }


    // Save Engine sync diagnostic status  -------------------
    public static void saveEngSyncDiagnstcStatus( boolean IsEngSyncDia, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.EngSyncDiagnstc, IsEngSyncDia);

        editor.commit();
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


    // Save Engine sync Malfunction status  ----------------
    public static void saveEngSyncMalfunctionStatus( boolean IsEngSyncDia, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.EngSyncMalfunction, IsEngSyncDia);

        editor.commit();
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



    // Save lacation malfunction status  -------------------
    public static void saveLocMalfunctionOccurStatus( boolean IsLocMalfunction, String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsLocMalfunction, IsLocMalfunction);
        editor.putString(ConstantsKeys.LocMalfunctionOccurTime, time);

        editor.commit();
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

    // Save alert status settings -------------------
    public static void setEldOccurences( boolean IsUnidentified, boolean IsMalfunction, boolean IsDiagnostic,
                                            boolean SuggestedEdit, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsKeys.IsUnidentified, IsUnidentified);
        editor.putBoolean(ConstantsKeys.IsMalfunction, IsMalfunction);
        editor.putBoolean(ConstantsKeys.IsDiagnostic, IsDiagnostic);
        editor.putBoolean(ConstantsKeys.SuggestedEdit, SuggestedEdit);

        editor.commit();
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
        editor.commit();
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

        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }

    public List<String> getStatesInList(Context context) {

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

        return StateArrayList;
    }





    // Set Obd Speed -------------------
    public static void setObdSpeed( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("obd_speed", value);
        editor.commit();
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
        editor.commit();
    }

    // Get Refresh Data Time -------------------
    public static String getRefreshDataTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("data_refresh_time", "");
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
        editor.commit();

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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }

    // Get VSS value -------------------
    public static String getVehicleVin(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("VehicleVin", "");
    }



    // Set obd data Time Stamp -------------------
    public static void setTimeStamp( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TimeStamp", value);
        editor.commit();
    }

    // Set obd data Time Stamp -------------------
    public static String getTimeStamp(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("TimeStamp", "--");
    }


    // Set wired Obd Odometer -------------------
    public static void SetWiredObdOdometer( String value, Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("wired_obd_odometer", value);
        editor.commit();
    }

    // Get wired Obd Odometer -------------------
    public static String getWiredObdOdometer(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("wired_obd_odometer", "0");

    }





    // Set obd_engine_hours  -------------------
    public static void SetObdEngineHours( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("obd_engine_hours", value);
        editor.commit();
    }

    // Get obd_engine_hours -------------------
    public static String getObdEngineHours(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("obd_engine_hours", "0");
    }




    // Set Day start Odometer value  -------------------
    public static void setDayStartOdometer( String odometer, String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("dayStartOdometer", odometer);
        editor.putString("dayStartSavedTime", savedTime);
        editor.commit();
    }


    // Get Day start Odometer value -------------------
    public static String getDayStartOdometer(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("dayStartOdometer", "0");
    }

    // Get Day start Odometer Saved time -------------------
    public static String getDayStartSavedTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("dayStartSavedTime", "");
    }


    // Set High Precision Odometer value  -------------------
    public static void setHighPrecisionOdometer( String odometer, String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("highPrecisionOdometer", odometer);
        editor.putString("HighPrecesionSavedTime", savedTime);
        editor.commit();
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



    // Set Malfunction Called Time  -------------------
    public static void setMalfCallTime(String savedTime, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("MalfunctionCalledTime", savedTime);
        editor.commit();
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
        editor.commit();
    }

    // Get Malfunction last Called Time for Engine Sync -------------------
    public static String getEngSyncLastCallTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(ConstantsKeys.MalCalledLastTime, "");
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

        editor.commit();
    }

    // Get Malfunction last Called Time for Engine Sync -------------------
    public static String getEngSyncLastCallDetails(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "0");
    }



    // Save Last Called Wired Call Back count
    public static void setLastCalledWiredCallBack(long count, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("WiredCallBack", count);
        editor.commit();

    }


    // Get Last Called Wired Call Backs count -------------------
    public static long getLastCalledWiredCallBack( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong("WiredCallBack", 0);
    }



    // Set Ecm Obd location with time  -------------------
    public static void setEcmObdLocationWithTime( String lat, String lon, String odometer, String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ecmObdLat", lat);
        editor.putString("ecmObdLon", lon);
        editor.putString("ecmOdometer", odometer);
        editor.putString("ecmObdTime", time);

        editor.commit();
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


    // save Location Malfunction Type (x,m,e) -------------------
    public static void setLocMalfunctionType( String type, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ecmMalfunctionType", type);

        editor.commit();
    }

    // Get Location Malfunction Type -------------------
    public static String getLocMalfunctionType(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("ecmMalfunctionType", "");
    }


    // save Malfunction status for Manual Location input -------------------
    public static void saveManualLocStatus( boolean type, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("ecmManualLocStatus", type);

        editor.commit();
    }

    // Get Malfunction status for Manual Location input -------------------
    public static boolean isManualLocAccepted(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("ecmManualLocStatus", false);
    }



    // ===========================Save Current Cycle with details ===========================
    public void SetCycleOfflineDetails(String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("cycleDetails", value);
        editor.commit();
    }

    // =========================== Get Offline Data Status ===========================
    public String GetCycleDetails(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("cycleDetails", "[]");
    }



    // =========================== Save Last OBD Type with Time ===========================
    public void SaveConnectionInfo(String type, String time, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.CONNECTION_TYPE, type);
        editor.putString(Constants.LAST_SAVED_TIME, time);
        editor.commit();
    }

    // =========================== Get OBD saved time with Type===========================
    public String GetConnectionInfo(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    // Get Last Usage Data Saved Time -------------------
    public static void setLastUsageDataSavedTime( String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.DATA_USAGE_TIME, value);
        editor.commit();
    }

    // Get Last Usage Data Saved Time -------------------
    public static String getLastUsageDataSavedTime( Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.DATA_USAGE_TIME, "");
    }



}
