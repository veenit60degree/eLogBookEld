package com.constants;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.MalfunctionDiagnosticMethod;
import com.local.db.OdometerHelperMethod;
import com.local.db.RecapViewMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.EldFragment;
import com.models.CanadaDutyStatusModel;
import com.models.EldDataModelNew;
import com.models.MalfunctionHeaderModel;
import com.models.MalfunctionModel;
import com.models.NotificationHistoryModel;
import com.models.OtherOptionsModel;
import com.models.PrePostModel;
import com.models.RecapSignModel;
import com.models.SlideMenuModel;
import com.models.UnAssignedVehicleModel;
import com.models.UnIdentifiedRecordModel;
import com.opencsv.CSVReader;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.CoNotificationPref;
import com.shared.pref.MainDriverEldPref;
import com.shared.pref.NotificationPref;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;
import webapi.LocalCalls;

public class Constants {

    public static String LAT_KEY = "lat";
    public static String LON_KEY = "lon";
    public static String LOC_SAVED_TIME_KEY = "saved_time_key";

    public static int OBD_PREF_WIFI     = 1;
    public static int OBD_PREF_WIRED    = 2;
    public static int OBD_PREF_BLE      = 3;

    public static boolean IsAlreadyViolation = false;
    public static boolean IsHomePageOnCreate;

    public static String LogDate = "";
    public static String DayName = "";
    public static String MonthFullName = "";
    public static String MonthShortName = "";
    public static String Location = "Location";
    public static String Remarks = "Remarks";
    public static String Truck = "Truck";
    public static String Trailor = "Trailor";
    public static String AobrdWarning = "AOBRD Warning";
    public static String AobrdAutomatic = "AOBRD Auto";
    public static String NoTrailer = "No Trailer";
    public static String packageName = "com.messaging.logistic";
    public static String Personal = "Personal";

    // OBD parameters
    public static String OBD_Odometer = "obd_Odometer";
    public static String OBD_HighPrecisionOdometer = "obd_highPrecisionOdometer";
    public static String OBD_EngineHours = "obd_EngineHours";
    public static String OBD_IgnitionStatus = "obd_IgnitionStatus";
    public static String OBD_TripDistance = "obd_TripDistance";
    public static String OBD_VINNumber = "obd_VINNumber";
    public static String OBD_TimeStamp = "obd_TimeStamp";
    public static String OBD_RPM = "obd_RPM";
    public static String OBD_Vss = "obd_Vss";
    public static String OBD_Speed = "obd_Speed";
    public static String Action = "Action";

    public static String AlertSettings      = "AlertSettings";
    public static String AlertUnidentified  = "AlertUnidentified";

    public static String apiReturnedSpeed = "apiReturnedSpeed";
    public static String obdSource = "obdSource";
    public static String Source = "Source";
    public static String obdOdometer = "Odometer";
    public static String obdOdometerInMeter = "OdometerInMeter";
    public static String DecodedData = "DecodedData";
    public static String CorrectedData = "CorrectedData";
    public static String obdHighPrecisionOdo = "HighPrecisionOdometer";
    public static String obdEngineHours = "EngineHours";
    public static String obdIgnitionStatus = "IgnitionStatus";
    public static String obdTripDistance = "TripDistance";
    public static String obdVINNumber = "VINNumber";
    public static String obdCalculatedSpeed = "CalculatedSpeed";
    public static String obdTimeStamp = "TimeStamp";
    public static String obdRPM = "RPM";
    public static String obdVss = "Vss";
    public static String obdDetail = "detail";
    public static String obdSpeed = "Speed";
    public static String calculatedSpeed = "CalculatedSpeed";
    public static String WheelBasedVehicleSpeed = "WheelBasedVehicleSpeed";
    public static String LastRecordTime = "LastRecordFromObdTime";
    public static String ObdRecordTime = "ObdRecordTime";
    public static String FileWriteTime = "FileWriteDate";
    public static String PreviousLogDate = "PreviousLogDate";
    public static String CurrentLogDate = "CurrentLogDate";

    public static String WiredOBD = "wired_obd";
    public static String WifiOBD = "wifi_obd";
    public static String BleObd = "bluetooth_obd";
    public static String ApiData = "api_data";
    public static String OfflineData = "offline_data";
    public static String DataMalfunction = "Data_Malfunction";
    public static String DiagnosticEvent = "Diagnostic";
    public static String MalfunctionEvent = "Malfunction";

    public static String PositionComplianceMalfunction  = "L";
    public static String MissingDataElementDiagnostic   = "3";
    public static String ConstLocationMissing           = "LM";
    public static String PowerComplianceMalfunction     = "P";
    public static String PowerDataDiagnostic            = "1";

    public static String ConstEngineSyncDiaEvent        = "2";
    public static String ConstEngineSyncMalEvent        = "E";


    public static final int MAIN_DRIVER_TYPE    = 0;
    public static final int CO_DRIVER_TYPE      = 1;

    public static int ConnectionMalfunction = 0;
    public static int ConnectionWired = 1;
    public static int ConnectionWifi = 2;
    public static int ConnectionBluetooth = 3;
    public static int ConnectionApi = 4;
    public static int ConnectionOffline = 5;
    public static int CertifyLog = 101010;      // set this value to differentiate where we go on certify screen

    public static final int WIRED_CONNECTED     = 1001;
    public static final int WIRED_DISCONNECTED  = 1002;
    public static final int WIRED_ERROR         = 1006;
    public static final int WIRED_IGNITION_OFF  = 1007;

    public static final int WIFI_CONNECTED      = 1003;
    public static final int WIFI_DISCONNECTED   = 1004;
    public static final int NO_CONNECTION       = 1005;
    public static final int WIFI_IGNITION_OFF   = 1008;

    public static final int BLE_CONNECTED       = 1009;
    public static final int BLE_DISCONNECTED    = 1010;


    public static final int NOTIFICATION      = 0;
    public static final int GPS               = 1;
    public static final int MALFUNCTION       = 2;
    public static final int UNIDENTIFIED      = 3;
    public static final int SUGGESTED_LOGS    = 4;
    public static final int OBD               = 5;

    public static String TruckIgnitionStatus = "TruckIgnitionStatus";
    public static String IgnitionSource = "IgnitionSource";
    public static String LastIgnitionTime = "LastIgnitionTime";


    public static String TruckIgnitionStatusMalDia = "TruckIgnitionStatusMalDia";
    public static String IgnitionSourceMalDia = "IgnitionSourceMalDia";
    public static String IgnitionTimeMalDia = "LastIgnitionTimeMalDia";
    public static String EngineHourMalDia = "EngineHourMalDia";
    public static String OdometerMalDia = "OdometerMalDia";

    public static String CONNECTION_TYPE = "connection_type";
    public static String LAST_SAVED_TIME = "last_saved_time";
    public static String DATA_USAGE_TIME = "data_usage_time";


    public static String ViolationReason30Min = "30 MIN BREAK VIOLATION";

    String DeviceName = "Android";


    public static int ELDActivityLaunchCount = 0;
    public static boolean IS_ACTIVE_ELD = false;
    public static boolean IS_NOTIFICATION = false;
    public static boolean IS_SCREENSHOT = false;
    public static boolean IS_ELD_ON_CREATE = true;
    public static boolean IS_TRAILER_INSPECT = false;
    public static boolean IsEdiLogBackStack = false;
    public static boolean IsCtPatUploading = false;
    public static boolean IsAlsServerResponding = true;
    public static boolean isClaim   = false;
    public static boolean isEldHome   = false;

    public static String DriverLogId = "";
    public static String IsStartingLocation = "";

    public static int OFF_DUTY = 1;
    public static int SLEEPER  = 2;
    public static int DRIVING  = 3;
    public static int ON_DUTY  = 4;
    public static int PERSONAL = 1;

    final int EngineUp              = 1;
    final int EngineDown            = 3;
    final int Login                 = 1;
    final int Logout                = 2;
    final int MalfunctionActive     = 1;
    final int MalfunctionInactive   = 2;
    final int DiagnosticActive      = 3;
    final int DiagnosticInactive    = 4;
    
    public static int WIRED_OBD = 1001;
    public static int WIFI_OBD = 1002;
    public static int API = 1003;
    public static int OFFLINE = 1004;
    public static int OTHER_SOURCE = 1005;

     // EventCode Type Inside the list
    public static int Malfunction             = 1;    // EventCode value is an String in main array
    public static int Diagnostic              = 3;    // EventCode value is an Integer in main array
    public static int clearEvent              = 2;


    public static int EditRemarks        = 101;
    public static int EditLocation       = 102;
    public static int SocketTimeout1Sec  = 1000;   // 1 second
    public static int SocketTimeout2Sec  = 2000;   // 2 second
    public static int SocketTimeout3Sec  = 3000;   // 3 seconds
    public static int SocketTimeout4Sec  = 4000;   // 3 seconds
    public static int SocketTimeout5Sec  = 5000;   // 5 seconds
    public static int SocketTimeout8Sec  = 8000;   // 8 seconds
    public static int SocketTimeout10Sec = 10000;   // 10 seconds
    public static int SocketTimeout15Sec = 15000;   // 15 seconds
    public static int SocketTimeout20Sec = 20000;   // 20 seconds
    public static int SocketTimeout30Sec = 30000;   // 30 seconds
    public static int SocketTimeout40Sec = 40000;   // 30 seconds
    public static int SocketTimeout50Sec = 50000;   // 50 seconds
    public static int SocketTimeout60Sec = 60000;   // 60 seconds

    public static int SocketTimeout70Sec = 70000;   // 70 seconds
    public static int SocketTimeout80Sec = 80000;   // 80 seconds
    public static int SocketTimeout90Sec = 90000;   // 90 seconds
    public static int SocketTimeout100Sec= 100000;   // 100 seconds
    public static int SocketTimeout110Sec= 110000;   // 110 seconds
    public static int SocketTimeout120Sec= 120000;   // 120 seconds

    // ------ Notification Type --------
    //
    // ---- ID ----
    public static final int CycleChange = 1;
    public static final int NotDrivingButVehicleRunning = 2;
    public static final int DrivingButSpeedZero = 3;
    public static final int PersonalDrivingExceed = 4;
    public static final int AssignUnidentifiedRecords = 5;
    public static final int EldAlert = 6;
    public static final int EditDriverLog = 7;

    public static final int USADrivingNotification = 101;
    public static final int USAOnDutyNotification = 102;
    public static final int USAConsecutiveOnDutyNotification = 103;
    public static final int USA6DayRuleNotification = 104;
    public static final int USA7DayRuleNotification = 105;
    public static final int CANADADrivingNotification = 106;
    public static final int CANADAOnDutyNotification = 107;
    public static final int CANADAConsecutiveOnDutyNotification = 108;
    public static final int CANADACycle1Notification = 109;
    public static final int CANADACycle2Notification = 110;
    public static final int CANADAShiftNotification = 111;
    public static final int USAShiftNotification = 112;
    public static final int ELDLocalRule = 113;
    public static final int StaticLocalNotificationId = 1010;


    public static final int PreInspection = 1;
    public static final int PostInspection = 2;
    public static final int Trailer = 3;

    public static int inspectionLayHeight = 0;
    public static int inspectionViewHeight = 0;


    public Constants() {
        super();
    }

    public static final int ELD_HOS               = 0;
    public static final int PTI_INSPECTION        = 4;
    public static final int CT_PAT_INSPECTION     = 8;
    public static final int ODOMETER_READING      = 5;
    public static final int NOTIFICATION_HISTORY  = 3;
    public static final int SHIPPING_DOC          = 7;
    public static final int ELD_DOC               = 10;
    public static final int UNIDENTIFIED_RECORD   = 11;
    public static final int DATA_MALFUNCTION      = 12;
    public static final int SETTINGS              = 1;
    public static final int ALS_SUPPORT           = 6;
    public static final int ALS_TERMS_COND        = 14;
    public static final int LOGOUT                = 2;
    public static final int VERSION               = 13;


    public List<SlideMenuModel> getSlideMenuList(Context context, boolean isOdometerFromObd, boolean isunIdentified, boolean isMalfunction, String version){
        List<SlideMenuModel> list = new ArrayList<>();
        if(SharedPref.IsAOBRD(context)){
            list.add(new SlideMenuModel(ELD_HOS, R.drawable.eld_home, context.getResources().getString(R.string.aobrd_hos)));
        }else {
            list.add(new SlideMenuModel(ELD_HOS, R.drawable.eld_home, context.getResources().getString(R.string.eld_hos)));
        }
        list.add(new SlideMenuModel(PTI_INSPECTION, R.drawable.pre_post_inspection, context.getResources().getString(R.string.prePostTripIns)));
        list.add(new SlideMenuModel(CT_PAT_INSPECTION, R.drawable.ct_pat_inspection, context.getResources().getString(R.string.ctPat)));
        if(isOdometerFromObd == false) {
            list.add(new SlideMenuModel(ODOMETER_READING, R.drawable.odometer_menu, context.getResources().getString(R.string.odometerReading)));
        }
        list.add(new SlideMenuModel(NOTIFICATION_HISTORY, R.drawable.notification_history, context.getResources().getString(R.string.noti_history)));
        list.add(new SlideMenuModel(SHIPPING_DOC, R.drawable.shipping_docs, context.getResources().getString(R.string.ShippingDocNumber)));
        list.add(new SlideMenuModel(ELD_DOC, R.drawable.eld_docs, context.getResources().getString(R.string.eld_documents)));
        if(isunIdentified) {
            list.add(new SlideMenuModel(UNIDENTIFIED_RECORD, R.drawable.unidentified_menu, context.getResources().getString(R.string.unIdentified_records)));
        }
        if(isMalfunction) {
            list.add(new SlideMenuModel(DATA_MALFUNCTION, R.drawable.eld_malfunction, context.getResources().getString(R.string.malf_and_diagnostic)));
        }
        list.add(new SlideMenuModel(SETTINGS, R.drawable.settings, context.getResources().getString(R.string.action_settings)));
        list.add(new SlideMenuModel(ALS_SUPPORT, R.drawable.als_support, context.getResources().getString(R.string.action_Support)));
        list.add(new SlideMenuModel(ALS_TERMS_COND, R.drawable.terms_conditions, context.getResources().getString(R.string.terms_conditions)));
        list.add(new SlideMenuModel(LOGOUT, R.drawable.logout, context.getResources().getString(R.string.logout)));
        list.add(new SlideMenuModel(VERSION, R.drawable.transparent, version));

        return list;
    }



    public List<OtherOptionsModel> getOtherOptionsList(Context context, boolean isAllowMalfunction, boolean isAllowUnIdentified){
        List<OtherOptionsModel> optionsList = new ArrayList<>();
        optionsList.add(new OtherOptionsModel(R.drawable.notifications_other, NOTIFICATION, context.getResources().getString(R.string.notification)));
        optionsList.add(new OtherOptionsModel(R.drawable.gps_other, GPS, context.getResources().getString(R.string.gps)));

        if(isAllowMalfunction)
            optionsList.add(new OtherOptionsModel(R.drawable.malfunction_other, MALFUNCTION, context.getResources().getString(R.string.malf_and_dia)));
        //if(SharedPref.IsAllowMalfunction(context) || SharedPref.IsAllowDiagnostic(context))

        if(isAllowUnIdentified)
            optionsList.add(new OtherOptionsModel(R.drawable.unidentified_other, UNIDENTIFIED, context.getResources().getString(R.string.unIdentified_records)));
        //if(SharedPref.IsShowUnidentifiedRecords(context))

        if(SharedPref.IsCCMTACertified(context))
            optionsList.add(new OtherOptionsModel(R.drawable.edit_log_icon, SUGGESTED_LOGS, context.getResources().getString(R.string.suggested_logs)));

        if(SharedPref.getObdStatus(context) == Constants.WIFI_CONNECTED ){
            optionsList.add(new OtherOptionsModel(R.drawable.wifi_other, OBD, context.getResources().getString(R.string.obd_wifi)));
        }else if(SharedPref.getObdStatus(context) == Constants.WIRED_CONNECTED){
            optionsList.add(new OtherOptionsModel(R.drawable.wired_status_inactive, OBD, context.getResources().getString(R.string.wired_tablet)));
        }else if(SharedPref.getObdStatus(context) == Constants.BLE_CONNECTED){
            optionsList.add(new OtherOptionsModel(R.drawable.ble_ic, OBD, context.getResources().getString(R.string.obd_ble)));
        }else{
            optionsList.add(new OtherOptionsModel(R.drawable.eld_malfunction, OBD, context.getResources().getString(R.string.obd_not_connected)));
        }


        return optionsList;
    }



    public void SaveEldJsonToList(EldDataModelNew ListModel, JSONArray jsonArray) throws JSONException {

        JSONObject locationObj = new JSONObject();

        locationObj.put(ConstantsKeys.ProjectId, ListModel.getProjectId());
        locationObj.put(ConstantsKeys.DriverId, ListModel.getDriverId());
        locationObj.put(ConstantsKeys.DriverStatusId, ListModel.getDriverStatusId());

        locationObj.put(ConstantsKeys.IsYardMove, ListModel.getIsYard());
        locationObj.put(ConstantsKeys.IsPersonal, ListModel.getIsPersonal());
        locationObj.put(ConstantsKeys.DeviceID, ListModel.getDeviceID());

        locationObj.put(ConstantsKeys.Remarks, ListModel.getRemarks());
        locationObj.put(ConstantsKeys.UTCDateTime, ListModel.getUTCDateTime());
        locationObj.put(ConstantsKeys.TruckNumber, ListModel.getTruckNumber());
        locationObj.put(ConstantsKeys.TrailorNumber, ListModel.getTrailorNumber());
        locationObj.put(ConstantsKeys.CompanyId, ListModel.getCompanyId());
        locationObj.put(ConstantsKeys.DriverName, ListModel.getDriverName());
        locationObj.put(ConstantsKeys.City, ListModel.getCity());
        locationObj.put(ConstantsKeys.State, ListModel.getState());
        locationObj.put(ConstantsKeys.Country, ListModel.getCountry());
        locationObj.put(ConstantsKeys.IsViolation, ListModel.getIsViolation());
        locationObj.put(ConstantsKeys.ViolationReason, ListModel.getViolationReason());

        locationObj.put(ConstantsKeys.Latitude, ListModel.getLatitude());
        locationObj.put(ConstantsKeys.Longitude, ListModel.getLongitude());

        locationObj.put(ConstantsKeys.DeviceName, DeviceName);
        locationObj.put(ConstantsKeys.IsStatusAutomatic, ListModel.IsStatusAutomatic());

        locationObj.put(ConstantsKeys.OBDSpeed, ListModel.getOBDSpeed());
        locationObj.put(ConstantsKeys.GPSSpeed, ListModel.getGPSSpeed());
        locationObj.put(ConstantsKeys.PlateNumber, ListModel.getPlateNumber());
        locationObj.put(ConstantsKeys.IsShortHaulException, ListModel.getHaulHourException());
        locationObj.put(ConstantsKeys.IsShortHaulUpdate, ListModel.getShortHaulUpdate());

        locationObj.put(ConstantsKeys.DecesionSource, ListModel.getDecesionSource());
        locationObj.put(ConstantsKeys.IsAdverseException, ListModel.getIsAdverseException());
        locationObj.put(ConstantsKeys.AdverseExceptionRemarks, ListModel.getAdverseExceptionRemarks());
        locationObj.put(ConstantsKeys.EditedReason, ListModel.getEditedReason());
        locationObj.put(ConstantsKeys.LocationType, ListModel.getLocationType());
        locationObj.put(ConstantsKeys.IsNorthCanada, ListModel.getIsNorthCanada());

        locationObj.put(ConstantsKeys.DrivingStartTime, ListModel.getDrivingStartTime());
        locationObj.put(ConstantsKeys.IsAOBRD, ListModel.getIsAobrd());
        locationObj.put(ConstantsKeys.CurrentCycleId, ListModel.getCurrentCycleId());
        locationObj.put(ConstantsKeys.isDeferral, ListModel.getIsDeferral());
        locationObj.put(ConstantsKeys.UnassignedVehicleMilesId, ListModel.getUnassignedVehicleMilesId());
        locationObj.put(ConstantsKeys.isNewRecord, ListModel.getNewRecordStatus());

        jsonArray.put(locationObj);
    }


    public JSONObject GetJsonFromList(JSONArray jsonArray, int pos) throws JSONException {

        JSONObject obj = (JSONObject) jsonArray.get(pos);
        JSONObject locationObj = new JSONObject();

        String IsStatusAutomatic = "false", OBDSpeed = "0", GPSSpeed = "0", PlateNumber = "";
        String decesionSpurce = "", HaulHourException = "false", TruckNumber = "";
        String isAdverseException = "", adverseExceptionRemark = "", LocationType = "", IsNorthCanada = "false";

        String isViolation = obj.getString(ConstantsKeys.IsViolation).trim();

        if (!isViolation.equalsIgnoreCase("true") && !isViolation.equalsIgnoreCase("false")) {
            isViolation = "false";
        }

        if (obj.has(ConstantsKeys.IsStatusAutomatic)) {
            IsStatusAutomatic = obj.getString(ConstantsKeys.IsStatusAutomatic);
        }

        if (obj.has(ConstantsKeys.OBDSpeed)) {
            OBDSpeed = obj.getString(ConstantsKeys.OBDSpeed);
        }

        if (obj.has(ConstantsKeys.GPSSpeed)) {
            GPSSpeed = obj.getString(ConstantsKeys.GPSSpeed);
        }

        if (obj.has(ConstantsKeys.PlateNumber)) {
            PlateNumber = obj.getString(ConstantsKeys.PlateNumber);
        }

        if (obj.has(ConstantsKeys.IsShortHaulException)) {
            HaulHourException = obj.getString(ConstantsKeys.IsShortHaulException);
        }

        if (obj.has(ConstantsKeys.DecesionSource)) {
            decesionSpurce = obj.getString(ConstantsKeys.DecesionSource);
        }
        if (obj.has(ConstantsKeys.TruckNumber)) {
            TruckNumber = obj.getString(ConstantsKeys.TruckNumber);
        }

        if (obj.has(ConstantsKeys.IsAdverseException )) {
            isAdverseException = obj.getString(ConstantsKeys.IsAdverseException );
        }
        if (obj.has(ConstantsKeys.AdverseExceptionRemarks)) {
            adverseExceptionRemark = obj.getString(ConstantsKeys.AdverseExceptionRemarks);
        }
        if (obj.has(ConstantsKeys.LocationType)) {
            LocationType = obj.getString(ConstantsKeys.LocationType);
        }
        if (obj.has(ConstantsKeys.IsNorthCanada)) {
            IsNorthCanada = obj.getString(ConstantsKeys.IsNorthCanada);
        }

        String DrivingStartTime = "", IsAOBRD = "false", CurrentCycleId = "", isDeferral = "false";
        if (obj.has(ConstantsKeys.DrivingStartTime)) {
            DrivingStartTime = obj.getString(ConstantsKeys.DrivingStartTime);
        }

        if (obj.has(ConstantsKeys.IsAOBRD)) {
            IsAOBRD = obj.getString(ConstantsKeys.IsAOBRD);
        }
        if (obj.has(ConstantsKeys.CurrentCycleId)) {
            CurrentCycleId = obj.getString(ConstantsKeys.CurrentCycleId);
        }

        if (obj.has(ConstantsKeys.isDeferral)) {
            isDeferral = obj.getString(ConstantsKeys.isDeferral);
        }


        locationObj.put(ConstantsKeys.ProjectId, obj.getString(ConstantsKeys.ProjectId));
        locationObj.put(ConstantsKeys.DriverId, obj.getString(ConstantsKeys.DriverId));
        locationObj.put(ConstantsKeys.DriverStatusId, obj.getString(ConstantsKeys.DriverStatusId));

        locationObj.put(ConstantsKeys.IsYardMove, obj.getString(ConstantsKeys.IsYardMove));
        locationObj.put(ConstantsKeys.IsPersonal, obj.getString(ConstantsKeys.IsPersonal));
        locationObj.put(ConstantsKeys.DeviceID, obj.getString(ConstantsKeys.DeviceID));

        locationObj.put(ConstantsKeys.Remarks, obj.getString(ConstantsKeys.Remarks));
        locationObj.put(ConstantsKeys.UTCDateTime, obj.getString(ConstantsKeys.UTCDateTime));
        locationObj.put(ConstantsKeys.TrailorNumber, obj.getString(ConstantsKeys.TrailorNumber));
        locationObj.put(ConstantsKeys.TruckNumber, TruckNumber);

        locationObj.put(ConstantsKeys.CompanyId, obj.getString(ConstantsKeys.CompanyId));
        locationObj.put(ConstantsKeys.DriverName, obj.getString(ConstantsKeys.DriverName));
        locationObj.put(ConstantsKeys.City, obj.getString(ConstantsKeys.City));
        locationObj.put(ConstantsKeys.State, obj.getString(ConstantsKeys.State));
        locationObj.put(ConstantsKeys.Country, obj.getString(ConstantsKeys.Country));
        locationObj.put(ConstantsKeys.IsViolation, isViolation);
        locationObj.put(ConstantsKeys.ViolationReason, obj.getString(ConstantsKeys.ViolationReason));

        locationObj.put(ConstantsKeys.Latitude, obj.getString(ConstantsKeys.Latitude));
        locationObj.put(ConstantsKeys.Longitude, obj.getString(ConstantsKeys.Longitude));

        locationObj.put(ConstantsKeys.DeviceName, obj.getString(ConstantsKeys.DeviceName));
        locationObj.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);

        locationObj.put(ConstantsKeys.OBDSpeed, OBDSpeed);
        locationObj.put(ConstantsKeys.GPSSpeed, GPSSpeed);
        locationObj.put(ConstantsKeys.PlateNumber, PlateNumber);

        locationObj.put(ConstantsKeys.IsShortHaulException, HaulHourException);
        locationObj.put(ConstantsKeys.DecesionSource, decesionSpurce);

        locationObj.put(ConstantsKeys.IsAdverseException, isAdverseException);
        locationObj.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);
        locationObj.put(ConstantsKeys.LocationType, LocationType);
        locationObj.put(ConstantsKeys.IsNorthCanada, IsNorthCanada);

        locationObj.put(ConstantsKeys.DrivingStartTime, DrivingStartTime);
        locationObj.put(ConstantsKeys.IsAOBRD, IsAOBRD);
        locationObj.put(ConstantsKeys.CurrentCycleId, CurrentCycleId);
        locationObj.put(ConstantsKeys.isDeferral, isDeferral);
        locationObj.put(ConstantsKeys.UnassignedVehicleMilesId, "");


        return locationObj;
    }


    public static String CheckNullString(String inputValue) {

        if (inputValue == null || inputValue.equals("null")) {
            inputValue = "";
        }
        return inputValue;
    }

    public static boolean CheckNullBoolean(JSONObject json, String inputValue) {
        boolean val = false;
        try {
            if (!json.isNull(inputValue)) {
                val = json.getBoolean(inputValue);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return val;
    }

    public String CheckNullBString(String inputValue) {
        try {
            if (inputValue.trim().equals("null")) {
                inputValue = "--";
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return inputValue;
    }


    public static void ClearNotifications(Context context) {
        if (context != null) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancelAll();
        }
    }


    public static String DateDifference(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        // different = different % minutesInMilli;


        String time = "";
        if (elapsedDays > 0)
            time = elapsedDays + " days ago";
        else if (elapsedHours > 0)
            time = elapsedHours + " hours ago";
        else
            time = elapsedMinutes + " mins ago";

        return time;

    }


    public static void SaveTripDetails(int DriverType, String truck, String VIN_NUMBER, Context context) {

        switch (DriverType) {
            case 0:
                DriverConst.SetDriverTripDetails(
                        DriverConst.GetDriverTripDetails(DriverConst.TripId, context),
                        truck,
                        VIN_NUMBER,
                        DriverConst.GetDriverTripDetails(DriverConst.Trailor, context),
                        DriverConst.GetDriverTripDetails(DriverConst.TripNumber, context),
                        DriverConst.GetDriverTripDetails(DriverConst.ShipperName, context),
                        DriverConst.GetDriverTripDetails(DriverConst.ShipperCity, context),
                        DriverConst.GetDriverTripDetails(DriverConst.ShipperState, context),
                        DriverConst.GetDriverTripDetails(DriverConst.ConsigneeName, context),
                        DriverConst.GetDriverTripDetails(DriverConst.ConsigneeCity, context),
                        DriverConst.GetDriverTripDetails(DriverConst.ConsigneeState, context),
                        "", "", "", "",
                        context);
                break;

            case 1:
                DriverConst.SetCoDriverTripDetails(
                        DriverConst.GetCoDriverTripDetails(DriverConst.CoTripId, context),
                        truck,
                        VIN_NUMBER,
                        DriverConst.GetCoDriverTripDetails(DriverConst.CoTrailor, context),
                        DriverConst.GetCoDriverTripDetails(DriverConst.CoTripNumber, context),
                        DriverConst.GetCoDriverTripDetails(DriverConst.CoShipperName, context),
                        DriverConst.GetCoDriverTripDetails(DriverConst.CoShipperCity, context),
                        DriverConst.GetCoDriverTripDetails(DriverConst.CoShipperState, context),
                        DriverConst.GetCoDriverTripDetails(DriverConst.CoConsigneeName, context),
                        DriverConst.GetCoDriverTripDetails(DriverConst.CoConsigneeCity, context),
                        DriverConst.GetCoDriverTripDetails(DriverConst.CoConsigneeState, context),
                        "", "", "", "", context);
                break;
        }

    }


    public double CalculateDistance(double originLat, double originLon, double destLat, double destLon, String unit, int JobStatus) {
        double distance = 0.0;
        try {
            double theta = originLon - destLon;
            distance = (Math.sin(deg2rad(originLat)) * Math.sin(deg2rad(destLat)) + Math.cos(deg2rad(originLat)) * Math.cos(deg2rad(destLat)) * Math.cos(deg2rad(theta)));
            distance = Math.acos(distance);
            distance = rad2deg(distance);
            distance = distance * 60 * 1.1515;

            if (unit.equals("K")) {
                distance = distance * 1.609344;
            } else if (unit.equals("M")) {
                distance = distance * 0.8684;
            }
            //  distance = roundDoubleValue(distance, 2);
            if (JobStatus == Constants.OFF_DUTY || JobStatus == SLEEPER) {
                if (distance < 0.11) {   // ignore 100 meter distance
                    distance = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (distance);
    }



    public String correctOBDWrongData(String dataObd) {

        //    String fileee = "*TS01,868323029227748,034355220620,CAN:0B00FEF1FFCC5C0000001FFF0B00FEC13EF3121CFFFFFFFF0B00FEE0FFFFFFFF11CF1D010B00FEEE7A45B32DFFFFFFFF0B00FEE5EA180C007FE729000B00F004F1A2A9352800FFFF0B00FEEFFFFFFF68FFFFFFFA0B00FEF6FF1B46FFFFFFFFFF0B00FEF7FFFFFFFF0F01FFFF#";
        //    String logFile = "TS01,868323029227748,034245220620,CAN:0B00FEF1FF79590000001FFF0B00FEC1DFF1121CFFFFFFFF0B00FEE0FFFFFFFF03CF1D010B00FEEE77451E2CF*TS01,868323029227748,034245220620,CAN:0B00FEF1FF79590000001FFF0B00FEC1DFF1121CFFFFFFFF0B00FEE0FFFFFFFF03CF1D010B00FEEE77451E2CF*TS01,868323029227748,034245220620,CAN:0B00FEF1FF79590000001FFF0B00FEC1DFF1121CFFFFFFFF0B00FEE0FFFFFFFF03CF1D010B00FEEE77451E2CF*TS01,868323029227748,034245220620,CAN:0B00FEF1FF79590000001FFF0B00FEC1DFF1121CFFFFFFFF0B00FEE0FFFFFFFF03CF1D010B00FEEE77451E2CF*TS01,868323029227748,034245220620,CAN:0B00FEF1FF79590000001FFF0B00FEC1DFF1121CFFFFFFFF0B00FEE0FFFFFFFF03CF1D010B00FEEE77451E2CF*TS01,868323029227748,034245220620,CAN:0B00FEF1FF79590000001FFF0B00FEC1DFF1121CFFFFFFFF0B00FEE0FFFFFFFF03CF1D010B00FEEE77451E2CF*TS01,868323029227748,034245220620,CAN:0B00FEF1FF79590000001FFF0B00FEC1DFF1121CFFFFFFFF0B00FEE0FFFFFFFF03CF1D010B00FEEE77451E2CF*TS01,868323029227748,034245220620,CAN:0B00FEF1FF79590000001FFF0B00FEC1DFF1121CFFFFFFFF0B00FEE0FFFFFFFF03CF1D010B00FEEE77451E2CF*TS01,868323029227748,034245220620,CAN:0B00FEF1FF79590000001FFF0B00FEC1DFF1121CFFFFFFFF0B00FEE0FFFFFFFF03CF1D010B00FEEE77451E2CFFFFFFFF0B00FEE5EA180C007DE729000B00F004F1947D452600FFFF0B00FEEFFFFFFF67FFFFFFFA0B00FEF6FF0F44FFFFFFFFFF0B00FEF7FFFFFFFF1001FFFF#FFFFFFF0B00FEE5EA180C007DE729000B00F004F1947D452600FFFF0B00FEEFFFFFFF67FFFFFFFA0B00FEF6FF0F44FFFFFFFFFF0B00FEF7FFFFFFFF1001FFFF#FFFFFFF0B00FEE5EA180C007DE729000B00F004F1947D452600FFFF0B00FEEFFFFFFF67FFFFFFFA0B00FEF6FF0F44FFFFFFFFFF0B00FEF7FFFFFFFF1001FFFF#FFFFFFF0B00FEE5EA180C007DE729000B00F004F1947D452600FFFF0B00FEEFFFFFFF67FFFFFFFA0B00FEF6FF0F44FFFFFFFFFF0B00FEF7FFFFFFFF1001FFFF#FFFFFFF0B00FEE5EA180C007DE729000B00F004F1947D452600FFFF0B00FEEFFFFFFF67FFFFFFFA0B00FEF6FF0F44FFFFFFFFFF0B00FEF7FFFFFFFF1001FFFF#FFFFFFF0B00FEE5EA180C007DE729000B00F004F1947D452600FFFF0B00FEEFFFFFFF67FFFFFFFA0B00FEF6FF0F44FFFFFFFFFF0B00FEF7FFFFFFFF1001FFFF#FFFFFFF0B00FEE5EA180C007DE729000B00F004F1947D452600FFFF0B00FEEFFFFFFF67FFFFFFFA0B00FEF6FF0F44FFFFFFFFFF0B00FEF7FFFFFFFF1001FFFF#FFFFFFF0B00FEE5EA180C007DE729000B00F004F1947D452600FFFF0B00FEEFFFFFFF67FFFFFFFA0B00FEF6FF0F44FFFFFFFFFF0B00FEF7FFFFFFFF1001FFFF#FFFFFFF0B00FEE5";

        String correctData = "";

        if (dataObd.length() > 500) {

            String[] logArray = dataObd.split("TS01");
            int logLength = logArray.length;

            if (logLength > 1) {
                String data = "TS01" + logArray[logLength - 1];

                if (!data.substring(0, 1).equals("*")) {
                    data = "*" + data;
                }
                String[] hashDataArray = data.split("#");
                if (hashDataArray.length > 1) {
                    correctData = hashDataArray[0] + "#";
                } else {
                    correctData = data;
                }

                if (!correctData.substring(correctData.length() - 1, correctData.length()).equals("#")) {
                    correctData = correctData + "#";
                }

                Log.d("correctData", "correctData: " + correctData);

            }
        }

        return correctData;

    }


    public static float distFrom(float originLat, float originLon, float destLat, float destLon) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(destLat - originLat);
        double dLng = Math.toRadians(destLon - originLon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(originLat)) * Math.cos(Math.toRadians(destLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }


    public static double roundDoubleValue(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    public double CalculateRouteDistance(JSONArray locArray, String unit) {

        double originLat = 0.0;
        double originLon = 0.0;
        double destLat = 0.0;
        double destLon = 0.0;
        double distance = 0.0;

        for (int i = 0; i < locArray.length() - 1; i++) {
            try {
                JSONObject originJson = (JSONObject) locArray.get(i);
                originLat = originJson.getDouble(ConstantsKeys.Latitude);
                originLon = originJson.getDouble(ConstantsKeys.Longitude);

                JSONObject destJson = (JSONObject) locArray.get(i + 1);
                destLat = destJson.getDouble(ConstantsKeys.Latitude);
                destLon = destJson.getDouble(ConstantsKeys.Longitude);

                double theta = originLon - destLon;
                double dist = (Math.sin(deg2rad(originLat)) * Math.sin(deg2rad(destLat)) + Math.cos(deg2rad(originLat)) * Math.cos(deg2rad(destLat)) * Math.cos(deg2rad(theta)));
                dist = Math.acos(dist);
                dist = rad2deg(dist);
                dist = dist * 60 * 1.1515;

                if (unit.equals("K")) {
                    dist = dist * 1.609344;
                } else if (unit.equals("M")) {
                    dist = dist * 0.8684;
                }

                distance = distance + dist;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return (distance);
    }


    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public double GetVehicleSpeed(JSONArray savedLocArray, String unit, int JobStatus, Context context) {

        double time = 0;  // converted value of 30 Seconds in Hour. (0.00833333)
        double speed = 0;
        double distance = 0;

        double originLat = 0.0;
        double originLon = 0.0;
        double destLat = 0.0;
        double destLon = 0.0;

        String originDateStr = "";
        String destDateStr = "";

        String orgLatStr = "";
        String orgLonStr = "";

        String destLatStr = "";
        String destLonStr = "";

        try {
           /* orgLatStr       = Globally.getStartLocation(ConstantsKeys.StartLat, context);
            orgLonStr       = Globally.getStartLocation(ConstantsKeys.StartLon, context);
            originDateStr   = Globally.getStartLocation(ConstantsKeys.StartDate, context);

            destLatStr      = Globally.getEndLocation(ConstantsKeys.EndLat, context);
            destLonStr      = Globally.getEndLocation(ConstantsKeys.EndLon, context);
            destDateStr     = Globally.getEndLocation(ConstantsKeys.EndDate, context);

*/

            //  if(orgLatStr.equals("") || destLatStr.equals("")) {
            JSONObject originJson = (JSONObject) savedLocArray.get(savedLocArray.length() - 2);
            JSONObject destJson = (JSONObject) savedLocArray.get(savedLocArray.length() - 1);
            originDateStr = Globally.ConvertDeviceDateTimeFormat(originJson.getString("UTCDate"));
            destDateStr = Globally.ConvertDeviceDateTimeFormat(destJson.getString("UTCDate"));

            orgLatStr = setStringLength(originJson.getString("Latitude"));
            orgLonStr = setStringLength(originJson.getString("Longitude"));

            destLatStr = setStringLength(destJson.getString("Latitude"));
            destLonStr = setStringLength(destJson.getString("Longitude"));

            //  }

            originLat = Double.parseDouble(orgLatStr);
            originLon = Double.parseDouble(orgLonStr);

            destLat = Double.parseDouble(destLatStr);
            destLon = Double.parseDouble(destLonStr);

            //  Log.d("origin", "origin lat: " + originLat + ", " +originLon);
            //   Log.d("dest", "dest Lat: " + destLat + ", " + destLon);


            DateTime originDate, destDate;
            originDate = new DateTime(Globally.getDateTimeObj(originDateStr, false));
            destDate = new DateTime(Globally.getDateTimeObj(destDateStr, false));


               /*     try {
                        originDate = new DateTime(Globally.ConvertDeviceDateTimeFormat(originDateStr));
                        destDate = new DateTime(Globally.ConvertDeviceDateTimeFormat(destDateStr ));
                    }catch (Exception e){
                        e.printStackTrace();
                        originDate = new DateTime(Globally.getDateTimeObj(originDateStr, false));
                        destDate = new DateTime(Globally.getDateTimeObj(destDateStr, false));
                    }
*/

            long diffInMillis = destDate.getMillis() - originDate.getMillis();
            double secondDiff = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
            time = secondDiff / 3600;    // convert seconds to hours


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (destLat == 0.0 || destLon == 0.0) {
            speed = 0;
        } else {
            distance = CalculateDistance(originLat, originLon, destLat, destLon, unit, JobStatus);   // unit = k or m (kilometer/meter)
            speed = distance / time;
        }
        Log.d("speed", "speed: " + speed);

        return speed;
    }


    String setStringLength(String str) {

        if (str.length() >= 9) {
            //- str = str.substring(0, 8);
        }

        return str;
    }


    public JSONArray GetDriverSavedArray(Context context) {
        int listSize = 0;
        JSONArray DriverJsonArray = new JSONArray();
        List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            try {
                MainDriverEldPref MainDriverPref = new MainDriverEldPref();

                listSize = MainDriverPref.LoadSavedLoc(context).size();
                tempList = MainDriverPref.LoadSavedLoc(context);
            } catch (Exception e) {
                listSize = 0;
            }
        } else {
            try {
                CoDriverEldPref CoDriverPref = new CoDriverEldPref();
                listSize = CoDriverPref.LoadSavedLoc(context).size();
                tempList = CoDriverPref.LoadSavedLoc(context);
            } catch (Exception e) {
                listSize = 0;
            }
        }

        try {
            if (listSize > 0) {
                for (int i = 0; i < tempList.size(); i++) {
                    EldDataModelNew listModel = tempList.get(i);
                    if (listModel != null) {
                        SaveEldJsonToList(          /* Put data as JSON to List */
                                listModel,
                                DriverJsonArray
                        );
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DriverJsonArray;
    }


    public JSONArray GetDriverOffLineSavedLog(Context context, int DriverType,
                                              MainDriverEldPref MainDriverPref, CoDriverEldPref CoDriverPref) {
        int listSize = 0;
        JSONArray DriverJsonArray = new JSONArray();
        List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

        if (DriverType == Constants.MAIN_DRIVER_TYPE) {
            try {
                //  MainDriverEldPref MainDriverPref = new MainDriverEldPref();

                listSize = MainDriverPref.LoadSavedLoc(context).size();
                tempList = MainDriverPref.LoadSavedLoc(context);
            } catch (Exception e) {
                listSize = 0;
            }
        } else {
            try {
                // CoDriverEldPref CoDriverPref = new CoDriverEldPref();
                listSize = CoDriverPref.LoadSavedLoc(context).size();
                tempList = CoDriverPref.LoadSavedLoc(context);
            } catch (Exception e) {
                listSize = 0;
            }
        }

        try {
            if (listSize > 0) {
                for (int i = 0; i < tempList.size(); i++) {
                    EldDataModelNew listModel = tempList.get(i);
                    if (listModel != null) {
                        SaveEldJsonToList(          /* Put data as JSON to List */
                                listModel,
                                DriverJsonArray
                        );
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DriverJsonArray;
    }


    public void ClearLogoutData(Context context) {
        Globally.ClearAllFields(context);
        Globally.StopService(context);
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        ((Activity) context).finish();
    }


    public int dpToPx(Context cxt, int dp) {
        if (cxt != null) {
            DisplayMetrics displayMetrics = cxt.getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        } else {
            return 140;
        }
    }


    public static double milesToKm(double distanceInMiles) {
        return distanceInMiles * 1.609344f;    //1.60934
    }

    public static double kmToMiles(double distanceInKm) {
        // double miles=distanceInKm/1.609;
        return distanceInKm * 0.621371;
    }


    public static int meterToMiles(int meters){
        double meters2miles = 1609.344;
        int miles = (int) (meters / meters2miles);
       // Log.d("miles", "miles: " + miles);

        return miles;
    }

    public static String meterToKm(String odometer){
        try {
            double meter = Double.parseDouble(odometer);
            odometer = ""+(meter * 0.001);
        }catch (Exception e){
            e.printStackTrace();
        }
        return odometer;
    }


    public static int getDayDiff(String savedDate, String currentDate){
        int dayDiff = -1;
        try {
            DateTime savedDateTime = Globally.getDateTimeObj(savedDate, false);
            DateTime currentDateTime = Globally.getDateTimeObj(currentDate, false);
            dayDiff = Days.daysBetween(savedDateTime.toLocalDate(), currentDateTime.toLocalDate()).getDays();
        }catch (Exception e){
            e.printStackTrace();
        }

        return dayDiff;
    }


    public int getDateTitleCount(List<CanadaDutyStatusModel> logList){
        int count = 0;
        try {
            for(int i = 0; i < logList.size() ; i++) {
                if (i == 0) {
                    count++;
                } else {
                    int dayDiff = getDayDiff(logList.get(i - 1).getDateTimeWithMins(), logList.get(i).getDateTimeWithMins());
                    if (dayDiff != 0) {
                        count++;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return count;
    }

    public String DeviceSimInfo(String Phone, String SerialNo, String Brand, String Model,
                                String Version, String OperatorName) {

        String DeviceInfo = "";
        try {
            JSONObject deviceInfoObj = new JSONObject();
            deviceInfoObj.put("Phone", Phone);
            deviceInfoObj.put("SerialNo", SerialNo);
            deviceInfoObj.put("Brand", Brand);

            deviceInfoObj.put("Model", Model);
            deviceInfoObj.put("Version", Version);
            deviceInfoObj.put("OperatorName", OperatorName);

            DeviceInfo = deviceInfoObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DeviceInfo;


    }


    public String DeviceSimInfoStr(String Phone, String SerialNo, String Brand, String Model,
                                   String Version, String OperatorName) {

        String DeviceInfo = "Phone:" + Phone +
                "#SerialNo:" + SerialNo +
                "#Brand:" + Brand +
                "#Model:" + Model +
                "#Version:" + Version +
                "#OperatorName:" + OperatorName;

        return DeviceInfo;

    }


    public boolean CheckGpsStatus(Context context) {
        LocationManager locationManager;
        boolean gpsStatus = true;
        if(context != null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        // gpsStatus
        return true;
    }


    public boolean CheckGpsStatusToCheckMalfunction(Context context) {
        LocationManager locationManager;
        boolean gpsStatus = true;
        if(context != null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        return gpsStatus;
    }



    public void SaveNotification(int DriverType, String title, String violationReason, String currentDateTime, HelperMethods helperMethods,
                                 NotificationPref notificationPref, CoNotificationPref coNotificationPref, Context context) {
        NotificationHistoryModel notificationsModel;
        int id;
        if (DriverType == Constants.MAIN_DRIVER_TYPE) {

            List<NotificationHistoryModel> notificationsList = notificationPref.GetNotificationsList(context);

            id = notificationsList.size() + 1;
            notificationsModel = new NotificationHistoryModel(id, title, violationReason, currentDateTime, false);
            notificationPref.AddNotification(context, notificationsModel);
            notificationsList.add(notificationsModel);

            // Remove 7 Days Older notifications from List
            notificationsList = Remove7DaysOlderData(notificationsList, currentDateTime, helperMethods);

            // Update notification List
            notificationPref.SaveNotifications(context, notificationsList);

        } else {
            List<NotificationHistoryModel> coNotificationsList = coNotificationPref.GetNotificationsList(context);
            id = coNotificationPref.GetNotificationsList(context).size() + 1;
            notificationsModel = new NotificationHistoryModel(id, "title", violationReason, currentDateTime, false);

            coNotificationPref.AddNotification(context, notificationsModel);
            coNotificationsList.add(notificationsModel);

            // Remove 7 Days Older notifications from List
            coNotificationsList = Remove7DaysOlderData(coNotificationsList, currentDateTime, helperMethods);

            // Update notification List
            coNotificationPref.SaveNotifications(context, coNotificationsList);

        }

    }


    private List<NotificationHistoryModel> Remove7DaysOlderData(List<NotificationHistoryModel> notificationsList, String currentDateTimeStr, HelperMethods helperMethods) {
        for (int i = 0; i < notificationsList.size(); i++) {
            DateTime selectedDate = Globally.getDateTimeObj(notificationsList.get(i).getNotificationDateTime(), false);
            DateTime currentDateTime = Globally.getDateTimeObj(currentDateTimeStr, false);
            int dateDiff = helperMethods.DayDiffSplitMethod(currentDateTime, selectedDate);
            if (dateDiff > 7) { // max days for notifications are 7.
                notificationsList.subList(i, notificationsList.size()).clear();
                break;
            }
        }

        return notificationsList;
    }


    public int getPendingNotifications(int DriverType, NotificationPref notificationPref, CoNotificationPref coNotificationPref, Context context) {
        List<NotificationHistoryModel> notificationsList = new ArrayList<NotificationHistoryModel>();
        int listSize = 0;
        int pendingNotifications = 0;

        try {
            if (DriverType == MAIN_DRIVER_TYPE) {
                notificationsList = notificationPref.GetNotificationsList(context);
            } else {
                notificationsList = coNotificationPref.GetNotificationsList(context);
            }

            listSize = notificationsList.size();

        } catch (Exception e) {
            listSize = 0;
            e.printStackTrace();
        }

        for (int i = 0; i < listSize; i++) {
            if (notificationsList.get(i).isRead() == false) {
                pendingNotifications++;
            }
        }

        return pendingNotifications;

    }


    public void ClearUnreadNotifications(int DriverType, NotificationPref notificationPref, CoNotificationPref coNotificationPref, Context context) {

        List<NotificationHistoryModel> notificationsList = new ArrayList<NotificationHistoryModel>();
        int listSize = 0;

        try {
            if (DriverType == MAIN_DRIVER_TYPE) {
                notificationsList = notificationPref.GetNotificationsList(context);
            } else {
                notificationsList = coNotificationPref.GetNotificationsList(context);
            }

            listSize = notificationsList.size();

        } catch (Exception e) {
            listSize = 0;
            e.printStackTrace();
        }

        for (int i = listSize - 1; i >= 0; i--) {
            if (notificationsList.get(i).isRead() == false) {
                NotificationHistoryModel notificationHistoryModel = notificationsList.get(i);
                notificationHistoryModel.setReadStatus(true);
                notificationsList.set(i, notificationHistoryModel);
            } else {
                break;
            }
        }

        if (DriverType == MAIN_DRIVER_TYPE) { // Main Driver
            notificationPref.SaveNotifications(context, notificationsList);
        } else { // Co Driver
            coNotificationPref.SaveNotifications(context, notificationsList);
        }


    }


    /*  if (logObj.has(ConstantsKeys.IsAdverseException )) {
        isAdverseException = logObj.getString(ConstantsKeys.IsAdverseException );
    }
            if (logObj.has(ConstantsKeys.AdverseExceptionRemarks)) {
        adverseExceptionRemark = logObj.getString(ConstantsKeys.AdverseExceptionRemarks);
    }
            driverLogJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
            driverLogJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);

    String isAdverseException = "", adverseExceptionRemark = "";


    */

    public boolean isDriverAllowedToChange(Context context, String DriverId, int obdStatus, Utils obdUtils, Globally global,  DriverPermissionMethod driverPermissionMethod, DBHelper dbHelper){

            boolean isAllowedToChange = true;

            try {

                if(obdStatus == Constants.WIRED_CONNECTED ||
                        obdStatus == WIFI_CONNECTED ||
                        obdStatus == Constants.BLE_CONNECTED){

                    StringBuilder obdData = obdUtils.getObdLogData(context);
                    String[] fileArray = obdData.toString().split("\n\n");
                    Log.d("obdLog", "fileArray: " + fileArray);

                    if(fileArray.length > 0) {
                        JSONObject data = new JSONObject(fileArray[fileArray.length - 1]);

                        try {
                            String lastDate = data.getString(Constants.CurrentLogDate);
                            DateTime lastSavedTime = global.getDateTimeObj(lastDate, false);
                            DateTime currentDate = global.getDateTimeObj(global.GetCurrentDateTime(), false);
                            int secDiff = currentDate.getSecondOfDay() - lastSavedTime.getSecondOfDay();

                            if (secDiff <= 60) {   // 1 min diff
                                if (obdStatus == WIFI_CONNECTED) {
                                    int wheelSpeed = Integer.valueOf(data.getString(Constants.WheelBasedVehicleSpeed));
                                    int calculatedSpeed = Integer.valueOf(data.getString(Constants.obdCalculatedSpeed));

                                    if (wheelSpeed > 10 || calculatedSpeed > 10) {
                                        isAllowedToChange = false;
                                    }
                                } else {
                                    int wheelSpeed = Integer.valueOf(data.getString(Constants.obdSpeed));
                                    int calculatedSpeed = Integer.valueOf(data.getString(Constants.calculatedSpeed));
                                    if (wheelSpeed > 10 || calculatedSpeed > 10) {
                                        isAllowedToChange = false;
                                    }
                                }
                            }
                        }catch (Exception e){e.printStackTrace();}
                    }

                    // in case if Device debug Log is not enabled from web settings
                    boolean isVehicleMoving = SharedPref.isVehicleMoving(context);
                    boolean isDeviceLogEnabled = driverPermissionMethod.isDeviceLogEnabled(DriverId, dbHelper);
                    if( isVehicleMoving == true && isDeviceLogEnabled == false ){
                        isAllowedToChange = false;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return isAllowedToChange;
    }


    // calculate speed from wired truck odometers data (in meters) with time difference (in sec)
    public double calculateSpeedFromWiredTabOdometer(String savedTime, String currentDate,
                                                      String previousHighPrecisionOdometer, String currentHighPrecisionOdometer,
                                                      Globally global, SharedPref sharedPref, Context context){

        double speedInKm = -1;

        try {
            double odometerDistance = Double.parseDouble(currentHighPrecisionOdometer) - Double.parseDouble(previousHighPrecisionOdometer);
            if (savedTime.length() > 10) {
                try {
                    String timeStampStr = savedTime.replace(" ", "T");
                    DateTime savedDateTime = global.getDateTimeObj(timeStampStr, false);
                    DateTime currentDateTime = global.getDateTimeObj(currentDate, false);

                    int timeInSecnd = Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();    //Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();
                    speedInKm = (odometerDistance / 1000.0f) / (timeInSecnd / 3600.0f);
                    // speedInKm = odometerDistance / timeInSecnd;

                } catch (Exception e) {
                    e.printStackTrace();

                    // save current HighPrecisionOdometer locally
                    sharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, global.GetCurrentDateTime(), context);

                }

            } else {
                // save current HighPrecisionOdometer locally
                sharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, global.GetCurrentDateTime(), context);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return speedInKm;

    }


    public JSONArray AddNewStatusInList(String DriverName, String DriverStatusId, String violaotionReason, String address,
                                        String DRIVER_ID, String City, String State, String Country, String AddressLine,
                                        String CurrentCycleId, String Reason, String isPersonal, boolean isViolation,
                                        String IsStatusAutomatic, String OBDSpeed, String GPSSpeed, String PlateNumber,
                                        String decesionSource, boolean isYardMove,
                                        Globally Global, boolean isHaulException, boolean isHaulExceptionUpdate,
                                        String isAdverseException, String adverseExceptionRemark, String LocationType,
                                        String malAddInfo, boolean IsNorthCanada, HelperMethods hMethods, DBHelper dbHelper) {

        JSONArray driverArray = new JSONArray();
        long DriverLogId = 0;
        double LastJobTotalMin = 0;
        String lastDateTimeStr = "";
        String StartDeviceCurrentTime = Global.GetCurrentDateTime();
        String StartUTCCurrentTime = Global.GetCurrentUTCTimeFormat();

        try {

            driverArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject lastItemJson = hMethods.GetLastJsonFromArray(driverArray);

        if (lastItemJson != null) {
            try {
                DriverLogId = lastItemJson.getLong(ConstantsKeys.DriverLogId);
                lastDateTimeStr = lastItemJson.getString(ConstantsKeys.startDateTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            DateTime currentDateTime = Global.getDateTimeObj(StartDeviceCurrentTime, false);
            DateTime lastDateTime = Global.getDateTimeObj(lastDateTimeStr, false);
            LastJobTotalMin = currentDateTime.getMinuteOfDay() - lastDateTime.getMinuteOfDay();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject lastItemUpdatedJson = hMethods.UpdateLastJsonFromArray(driverArray, StartDeviceCurrentTime, StartUTCCurrentTime, LastJobTotalMin);
        if (address.equals("no_address")) {
            if (City.length() > 0) {
                address = City + ", " + State + ", " + Country;
            } else {
                address = AddressLine;
            }
        }


        DriverLogId = DriverLogId + 1;
        JSONObject newJsonData = hMethods.AddJobInArray(
                DriverLogId,
                Long.parseLong(DRIVER_ID),
                Integer.valueOf(DriverStatusId),

                StartDeviceCurrentTime,
                StartDeviceCurrentTime,
                StartUTCCurrentTime,
                StartUTCCurrentTime,

                0,  // because start and end date will be same for new status for that time
                Global.LATITUDE,
                Global.LONGITUDE,
                Global.LATITUDE,
                Global.LONGITUDE,
                isYardMove,
                Boolean.parseBoolean(isPersonal),
                Integer.valueOf(CurrentCycleId),
                isViolation,
                violaotionReason,
                DriverName,
                Reason,
                Global.TRAILOR_NUMBER,
                address, address,
                Global.TRUCK_NUMBER,
                IsStatusAutomatic,
                OBDSpeed,
                GPSSpeed,
                PlateNumber,

                isHaulException,
                isHaulExceptionUpdate,
                decesionSource,
                isAdverseException,
                adverseExceptionRemark,
                LocationType,
                malAddInfo,
                IsNorthCanada

        );

        try {
            // Update end date time with current date time of last saved item in Array

            if (driverArray != null && driverArray.length() > 0)
                driverArray.put(driverArray.length() - 1, lastItemUpdatedJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        driverArray.put(newJsonData);

        return driverArray;
    }


    public List<RecapSignModel> GetCertifySignList(RecapViewMethod recapViewMethod, String DRIVER_ID, DBHelper dbHelper,
                                                   String currentDate, String CurrentCycleId, JSONObject logPermissionObj) {
        JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
        int arraylength = recap18DaysArray.length();
        int initilizeValue = 0;
        int daysValidationValue = 0;
        int DriverPermittedDays = GetDriverPermitDaysCount(logPermissionObj, CurrentCycleId, false);
        DateTime lastDateTime = new DateTime();
        List<RecapSignModel> recapSignatureList = new ArrayList<>();

        if (DriverPermittedDays > arraylength) {
            DriverPermittedDays = arraylength;
        }

        if (arraylength > 0) {
            try {
                String currentDateHalf = Globally.ConvertDateFormatyyyy_MM_dd(currentDate);
                DateTime currentDateTime = Globally.getDateTimeObj(currentDateHalf + "T00:00:00", false );
                lastDateTime    = currentDateTime.minusDays(DriverPermittedDays);

                JSONObject objLast = (JSONObject) recap18DaysArray.get(arraylength - 1);
                String dateStr = objLast.getString("Date");
                if (dateStr.equals(currentDate)) {
                    initilizeValue = arraylength - 2;
                } else {
                    initilizeValue = arraylength - 1;
                    daysValidationValue = 0;
                }

                if (initilizeValue > DriverPermittedDays) {
                    daysValidationValue = initilizeValue - DriverPermittedDays;
                } else {
                    daysValidationValue = 0;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (arraylength == 2) {
                initilizeValue = 0;
                daysValidationValue = -1;
            }else{

            }

            for (int i = initilizeValue; i > daysValidationValue; i--) {
                try {
                    JSONObject obj = (JSONObject) recap18DaysArray.get(i);
                    String date = Globally.ConvertDateFormatyyyy_MM_dd(obj.getString(ConstantsKeys.Date));
                    DateTime selectedDateTime = Globally.getDateTimeObj( date + "T00:00:00", false);

                    if(selectedDateTime.isAfter(lastDateTime) || selectedDateTime.equals(lastDateTime)) {
                        String image = obj.getString(ConstantsKeys.LogSignImage);
                        if (image.length() == 0) {
                            recapSignatureList.add(new RecapSignModel(false, selectedDateTime));
                        }else{
                            recapSignatureList.add(new RecapSignModel(true, selectedDateTime));
                        }
                    }else{
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return recapSignatureList;
    }



    public boolean GetCertifyLogSignStatus(RecapViewMethod recapViewMethod, String DRIVER_ID, DBHelper dbHelper, String currentDate, String CurrentCycleId, JSONObject logPermissionObj) {
        JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
        boolean IsPendingSignature = false;
        int arraylength = recap18DaysArray.length();
        int initilizeValue = 0;
        int daysValidationValue = 0;
        int DriverPermittedDays = GetDriverPermitDaysCount(logPermissionObj, CurrentCycleId, false);
        DateTime lastDateTime = new DateTime();

        if (DriverPermittedDays > arraylength) {
            DriverPermittedDays = arraylength;
        }

        if (arraylength > 0) {
            try {
                String currentDateHalf = Globally.ConvertDateFormatyyyy_MM_dd(currentDate);
                DateTime currentDateTime = Globally.getDateTimeObj(currentDateHalf + "T00:00:00", false );
                lastDateTime    = currentDateTime.minusDays(DriverPermittedDays);

                JSONObject objLast = (JSONObject) recap18DaysArray.get(arraylength - 1);
                String dateStr = objLast.getString("Date");
                if (dateStr.equals(currentDate)) {
                    initilizeValue = arraylength - 2;
                } else {
                    initilizeValue = arraylength - 1;
                    daysValidationValue = 0;
                }

                if (initilizeValue >= DriverPermittedDays) {
                    daysValidationValue = initilizeValue - DriverPermittedDays;
                } else {
                    daysValidationValue = initilizeValue;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (arraylength == 2) {
                initilizeValue = 0;
                daysValidationValue = -1;
            }

// JSONArray currentDayArray = hMethods.GetSingleDateArray(driverLogArray, currentDateTime, currentDateTime, currentUTCTime, true, offsetFromUTC);
            for (int i = initilizeValue; i > daysValidationValue; i--) {
                try {
                    JSONObject obj = (JSONObject) recap18DaysArray.get(i);
                    String date = Globally.ConvertDateFormatyyyy_MM_dd(obj.getString(ConstantsKeys.Date));
                    DateTime selectedDateTime = Globally.getDateTimeObj( date + "T00:00:00", false);

                    if(selectedDateTime.isAfter(lastDateTime) || selectedDateTime.equals(lastDateTime)) {
                        String image = obj.getString(ConstantsKeys.LogSignImage);
                        if (image.length() == 0) {
                            IsPendingSignature = true;
                            break;
                        }
                    }else{
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return IsPendingSignature;
    }


    public void UpdateCertifyLogArray(RecapViewMethod recapViewMethod, String DRIVER_ID, int DriverPermitDays,
                                      DBHelper dbHelper, SharedPref sharedPref, Context context) {
        JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);

        if (recap18DaysArray.length() > 0) {
            try {

                boolean isChangeForUpdate = false;
                String currentDateHalf   = Globally.ConvertDateFormatyyyy_MM_dd(Globally.GetCurrentDeviceDate());
                DateTime currentDateTime = Globally.getDateTimeObj(currentDateHalf + "T00:00:00", false );
                DateTime fromDateTime    = currentDateTime.minusDays(DriverPermitDays);
                JSONArray reCertifyArray = new JSONArray(sharedPref.getReCertifyData(context));

                for (int i = reCertifyArray.length()-1; i >= 0; i--) {
                    JSONObject obj = (JSONObject) reCertifyArray.get(i);

                    if(obj.getBoolean(ConstantsKeys.IsRecertifyRequied)) {
                        //  String date = Globally.ConvertDateFormatyyyy_MM_dd(obj.getString(ConstantsKeys.Date));
                        DateTime selectedDateTime = Globally.getDateTimeObj(obj.getString(ConstantsKeys.LogDate), false);

                        if (selectedDateTime.isAfter(fromDateTime) || selectedDateTime.equals(fromDateTime)) {

                            for (int j = recap18DaysArray.length() - 1; j >= 0 ; j--) {
                                JSONObject objRecap = (JSONObject) recap18DaysArray.get(j);
                                String date = Globally.ConvertDateFormatyyyy_MM_dd(objRecap.getString(ConstantsKeys.Date));
                                DateTime recapSelectedDate = Globally.getDateTimeObj(date, false);
                                if (selectedDateTime.equals(recapSelectedDate)) {
                                    if(objRecap.getString(ConstantsKeys.LogSignImage).length() > 0 ||
                                            objRecap.getString(ConstantsKeys.LogSignImageInByte).length() > 0){
                                        objRecap.put(ConstantsKeys.LogSignImage, "");
                                        objRecap.put(ConstantsKeys.LogSignImageInByte, "");
                                        recap18DaysArray.put(j,objRecap);
                                        isChangeForUpdate = true;
                                    }

                                    //  break;
                                }
                            }

                        } else {
                            break;
                        }
                    }
                }

                if(isChangeForUpdate){
                    // update recap array for re-certify log
                    recapViewMethod.RecapView18DaysHelper( Integer.valueOf(DRIVER_ID), dbHelper, recap18DaysArray);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }


    public boolean isCertifySignExist(RecapViewMethod recapViewMethod, String DRIVER_ID, DBHelper dbHelper) {

        JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
        boolean isCertifySign = false;


        for (int i = 0; i < recap18DaysArray.length(); i++) {
            try {
                JSONObject obj = (JSONObject) recap18DaysArray.get(i);

                String image = obj.getString("LogSignImageInByte");
                if (image.length() > 20) {
                    isCertifySign = true;
                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return isCertifySign;
    }


    public String getLastSignature(RecapViewMethod recapViewMethod, String DRIVER_ID, DBHelper dbHelper) {

        JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
        String LogSignImage = "";

        for (int i = recap18DaysArray.length() - 1; i >= 0; i--) {
            try {
                JSONObject obj = (JSONObject) recap18DaysArray.get(i);

                String image = obj.getString("LogSignImageInByte");
                if (image.length() > 20) {
                    LogSignImage = image;
                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return LogSignImage;
    }


    public String getMemoryUsage(Context context) {

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        double availableMegs = mi.availMem / 0x100000L;

//Percentage can be calculated for API 16+
        double percentAvail = mi.availMem / (double) mi.totalMem * 100.0;

        return Convert2DecimalPlacesDouble(100 - percentAvail) + "%";

     /*       long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println(elapsedTime);*/
    }


    public String Convert2DecimalPlacesDouble(double value) {
        return String.format("%.2f", value);
    }

    public String getCpuUsage() {

        String output = "";
        try {

            String[] DATA = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder processBuilder = new ProcessBuilder(DATA);
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            byte[] byteArry = new byte[1024];
            while (inputStream.read(byteArry) != -1) {
                output = output + new String(byteArry);
            }
            inputStream.close();

            String[] outputArray = output.split("\n");
            if (outputArray.length > 2) {

            }

            Log.d("CPU_INFO", output);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return output;
    }


    static String getRandomNumber() {
        String resRandomStr = Globally.GetCurrentFullDateTime();
        int resRandom = 0;
        try {
            Random rand = new Random();
            for (int i = 1; i <= 10; i++) {
                resRandom = rand.nextInt((999999999 - 10000000) + 1) + 10;
            }
            resRandomStr = "" +resRandom;

        }catch (Exception e){
            e.printStackTrace();
        }

        return resRandomStr;
    }


    static String getSerialNumber(Context context){
        String SerialNumber = "";

        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.
                    TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            /*
             * getSubscriberId() returns the unique subscriber ID,
             */
            SerialNumber = telephonyManager.getSimSerialNumber();
            //Log.d("subscriberId", "subscriberId: " + telephonyManager.getSubscriberId());

            if(SerialNumber.equals("null") || SerialNumber.length() == 0) {
                SerialNumber = telephonyManager.getLine1Number();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return SerialNumber;
    }


    public static String getIMEIDeviceId(String userName, Context context) {

        String deviceId = "";

        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            } else {
                final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return "";
                    }
                }
                assert mTelephony != null;
                if (mTelephony.getDeviceId() != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        deviceId = mTelephony.getImei();
                    } else {
                        deviceId = mTelephony.getDeviceId();
                    }
                } else {
                    deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            }

            if (deviceId.trim().equalsIgnoreCase("null") || deviceId.trim().length() == 0) {
                deviceId = getSerialNumber(context);
            }

        }catch (Exception e){
            deviceId = "";
            e.printStackTrace();
        }

        try{
            if (deviceId.trim().equalsIgnoreCase("null") || deviceId.trim().length() < 5) {
                if(userName.length() > 2){
                    userName = userName.substring(0,2);
                }
                deviceId = userName + "00" + getRandomNumber();
            }
        }catch (Exception e){
            deviceId = "000" + getRandomNumber();
            e.printStackTrace();
        }

        return deviceId;
    }


    public void saveUpdateVehDetails(String DriverId, String PreviousDeviceMappingId, String DeviceMappingId,
                                     String VehicleId, String EquipmentNumber, String PlateNumber, String VIN,
                                     String CompanyId, String IMEINumber, Utils util){

        try {
            JSONObject obj = new JSONObject();
            obj.put("InputType", "Update Vehicle");
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put("PreviousDeviceMappingId", PreviousDeviceMappingId);
            obj.put("DeviceMappingId", DeviceMappingId);
            obj.put("VehicleId", VehicleId);
            obj.put("EquipmentNumber", EquipmentNumber);
            obj.put("PlateNumber", PlateNumber);
            obj.put("VIN", VIN);
            obj.put("CompanyId", CompanyId);
            obj.put("IMEINumber", IMEINumber);

            util.writeToLogFile(obj.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void saveLoginDetails(String username, String OSType, String DeviceSimInfo,
                                 String IMEINumber, Utils util){
        try {
            JSONObject obj = new JSONObject();
            obj.put("InputType", "Login");
            obj.put("username", username);
            obj.put("OSType", OSType);
            obj.put("IMEINumber", IMEINumber);
            obj.put("DeviceSimInfo", DeviceSimInfo);


            util.writeToLogFile(obj.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // ------------- Get Driver permitted Days --------------
    public int GetDriverPermitDaysCount( JSONObject logPermissionObj, String CurrentCycleId, boolean IsDot){

        int UsaMaxDays = 8;
        int CanMaxDays = 14;
        int DriverPermitMaxDays = 0;

        if(logPermissionObj != null) {
            try {
                DriverPermitMaxDays = logPermissionObj.getInt(ConstantsKeys.ViewCertifyDays);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS) ) {
            if(IsDot){
                DriverPermitMaxDays = UsaMaxDays;
            }else {
                if (DriverPermitMaxDays > UsaMaxDays) {
                    DriverPermitMaxDays = UsaMaxDays;
                }
            }
        }else{
            if(IsDot){
                DriverPermitMaxDays = CanMaxDays;
            }
        }

        return DriverPermitMaxDays;
    }

    // CT-PAT Truck inspection list
    public List<PrePostModel> CtPatTruckList(){
        List<PrePostModel> truckList = new ArrayList<>();
        PrePostModel model;

        model = new PrePostModel("1", "Bumper");
        truckList.add(model);

        model = new PrePostModel("2", "Engine");
        truckList.add(model);

        model = new PrePostModel("3", "Tires");
        truckList.add(model);

        model = new PrePostModel("4", "Floor (truck)");
        truckList.add(model);

        model = new PrePostModel("5", "Fuel tanks");
        truckList.add(model);

        model = new PrePostModel("6", "Cab");
        truckList.add(model);

        model = new PrePostModel("7", "Air tanks");
        truckList.add(model);

        model = new PrePostModel("8", "Drive shaft");
        truckList.add(model);

        model = new PrePostModel("9", "Exhaust");
        truckList.add(model);

        return truckList;

    }


    // CT-PAT Trailer inspection list
    public List<PrePostModel> CtPatTrailerList(){
        List<PrePostModel> trailerList = new ArrayList<>();
        PrePostModel model;

        model = new PrePostModel("10", "Tires");
        trailerList.add(model);

        model = new PrePostModel("11", "Fifth wheel");
        trailerList.add(model);

        model = new PrePostModel("12", "Outside/Undercarriage");
        trailerList.add(model);

        model = new PrePostModel("13", "Outside/Inside Doors");
        trailerList.add(model);

        model = new PrePostModel("14", "Floor (Trailer)");
        trailerList.add(model);

        model = new PrePostModel("15", "Side walls");
        trailerList.add(model);

        model = new PrePostModel("16", "Front wall");
        trailerList.add(model);

        model = new PrePostModel("17", " Ceiling/Roof");
        trailerList.add(model);

        model = new PrePostModel("18", "Refrigeration unit");
        trailerList.add(model);

        return trailerList;

    }



    public boolean getShortHaulExceptionDetail(Context context, String DriverId, Globally global, SharedPref sharedPref,
                                               boolean isShortHaul, boolean isAdverseExcptn, boolean IsNorthCanada,
                                               HelperMethods hMethods, DBHelper dbHelper){

        boolean isHaulException = false;
        try {
            JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
            JSONObject lastJsonItem         = (JSONObject) driverLogArray.get(driverLogArray.length() - 1);

            DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);    // Current Date Time
            DateTime currentUTCTime = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), true);
            int offsetFromUTC = (int) global.GetTimeZoneOffSet();
            String CurrentCycleId = GetDriverCycle(context);
            boolean isSingleDriver = global.isSingleDriver(context);
            int DRIVER_JOB_STATUS = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
            int rulesVersion = sharedPref.GetRulesVersion(context);

            List<DriverLog> oDriverLogDetail = hMethods.getSavedLogList(Integer.valueOf(DriverId), currentDateTime, currentUTCTime, dbHelper);
            DriverDetail oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DriverId),
                    offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver, DRIVER_JOB_STATUS, false,
                    isShortHaul,  isAdverseExcptn, IsNorthCanada,
                    rulesVersion, oDriverLogDetail);

            LocalCalls CallDriverRule = new LocalCalls();
            isHaulException = CallDriverRule.checkShortHaulExceptionEligibility(oDriverDetail).isEligibleShortHaulException();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return isHaulException;

    }


    public boolean IsWarningNotificationAllowed(String DriverId, DBHelper dbHelper){
        DriverPermissionMethod driverPermissionMethod = new DriverPermissionMethod();
        JSONObject logPermissionObj    = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DriverId), dbHelper);
        boolean IsAllowed              = false;

        if(logPermissionObj != null && logPermissionObj.has(ConstantsKeys.IsNotificationAllowed)) {
            try {
                IsAllowed = logPermissionObj.getBoolean(ConstantsKeys.IsNotificationAllowed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return IsAllowed;
    }



    String GetDriverCycle(Context context){
        String cycleId = Globally.CANADA_CYCLE_1; // default value
        try{
            if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
                cycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context);
            }else{
                cycleId = DriverConst.GetCoDriverCurrentCycle(DriverConst.CoCurrentCycleId, context);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return cycleId;

    }


    public void CopyString(Context context, String data){
        try {

            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(data);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("text label",data);
                clipboard.setPrimaryClip(clip);
            }
            if(context != null) {
                Toast.makeText(context, "Sim number copied", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void DeleteFile(String filePath){
        try {
            File file = new File(filePath);
            if (file.isFile()) {
                file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void loadByteImage(String LogSignImageInByte, ImageView signImageView){

        if(LogSignImageInByte.length() > 0) {
            Bitmap bitmap = Globally.ConvertStringBytesToBitmap(LogSignImageInByte);
            signImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, signImageView.getWidth(),
                    signImageView.getHeight(), false));
        }

    }



    public int intToPixel(Context context , int size){
        if(context != null) {
            Resources r = context.getResources();
            int px = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, size, r.getDisplayMetrics()));
            return px;
        }else{
            return 60;
        }
    }


    public void writeViolationFile(DateTime currentDateTime, DateTime currentUTCTime, int DriverId,
                                   String CurrentCycleId, int offsetFromUTC, boolean isSingleDriver,
                                   int DRIVER_JOB_STATUS, boolean isOldRecord, boolean is16hrHaulExcptn, String violationReason,
                                   HelperMethods hMethods, DBHelper dbHelper, Context context){

        try{
            JSONArray finalEldLogArray = new JSONArray();
            JSONArray DriverLog18Days  = hMethods.getSavedLogArray(DriverId, dbHelper);

            // Add inputs in json those are passing in ELD rule library
            JSONObject eldRuleInputsJson = new JSONObject();
            eldRuleInputsJson.put(ConstantsKeys.CurrentDateTime,    currentDateTime.toString());
            eldRuleInputsJson.put(ConstantsKeys.CurrentUTCTime,     currentUTCTime.toString());
            eldRuleInputsJson.put(ConstantsKeys.CurrentCycleId,     CurrentCycleId);
            eldRuleInputsJson.put(ConstantsKeys.OffsetFromUTC,      offsetFromUTC);
            eldRuleInputsJson.put(ConstantsKeys.IsSingleDriver,     isSingleDriver );
            eldRuleInputsJson.put(ConstantsKeys.DriverId,           DriverId);
            eldRuleInputsJson.put(ConstantsKeys.DriverJobStatus,    DRIVER_JOB_STATUS);
            eldRuleInputsJson.put(ConstantsKeys.IsOldRecord,        isOldRecord);
            eldRuleInputsJson.put(ConstantsKeys.Is16hrHaulException, is16hrHaulExcptn);
            eldRuleInputsJson.put(ConstantsKeys.ViolationReason,     violationReason);

            finalEldLogArray.put(eldRuleInputsJson);
            finalEldLogArray.put(DriverLog18Days);

            Globally.SaveFileInSDCard(ConstantsKeys.ViolationTest, finalEldLogArray.toString(), true, context);

            Constants.IsAlreadyViolation = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String AddHorizontalDottedLine(int hLineX1, int hLineX2, int hLineY){
        return "<line class=\"horizontal dotted-line\" x1=\""+ hLineX1 +"\" x2=\""+ hLineX2 +"\" y1=\""+ hLineY +"\" y2=\""+ hLineY +"\"></line>\n" ;
    }



    public String AddHorizontalLine(int hLineX1, int hLineX2, int hLineY){
        return "<line class=\"horizontal\" x1=\""+ hLineX1 +"\" x2=\""+ hLineX2 +"\" y1=\""+ hLineY +"\" y2=\""+ hLineY +"\"></line>\n" ;
    }


    public String AddVerticalLine(int vLineX, int vLineY1, int vLineY2){
        return "<line class=\"vertical\" x1=\""+ vLineX +"\" x2=\""+ vLineX +"\" y1=\""+ vLineY1 +"\" y2=\""+ vLineY2 +"\"></line>\n" ;
    }


    public String AddVerticalLineViolation(int vLineX, int vLineY1, int vLineY2){
        return "<line class=\"vertical no-color\" x1=\""+ vLineX +"\" x2=\""+ vLineX +"\" y1=\""+ vLineY1 +"\" y2=\""+ vLineY2 +"\"></line>\n" ;
    }

    public String HtmlCloseTag(String OffDutyHour, String SleeperHour, String DrivingHour, String OnDutyHour ){
        return " </g>\n" +
                "   <g class=\"durations\" transform=\"translate(1505, 40)\">\n" +
                "                  <text class=\"label\" transform=\"translate(0, 25)\" dy=\"0.35em\">"+OffDutyHour+"</text>\n" +
                "                  <text class=\"label\" transform=\"translate(0, 75)\" dy=\"0.35em\">"+SleeperHour+"</text>\n" +
                "                  <text class=\"label\" transform=\"translate(0, 125)\" dy=\"0.35em\">"+DrivingHour+"</text>\n" +
                "                  <text class=\"label\" transform=\"translate(0, 175)\" dy=\"0.35em\">"+OnDutyHour+"</text>\n" +
                "               </g>    \n" +
                "            </svg>\n" +
                "         </log-graph>\n" +
                "      </div>";
    }


    public int VerticalLine(int status){
        int job = 0;
        int OFF             = 25;
        int SB              = 75;
        int DR              = 125;
        int ON              = 175;

        switch (status){
            case 1:
                job = OFF;
                break;

            case 2:
                job = SB;
                break;

            case 3:
                job = DR;
                break;

            case 4:
                job = ON;
                break;
        }
        return job;
    }



    public int getMinDifference(String lastRestartTime, String currentDate){

        int minDiff = 0;

        try {
            DateTime savedDateTime = Globally.getDateTimeObj(lastRestartTime, false);
            DateTime currentDateTime = Globally.getDateTimeObj(currentDate, false);

            minDiff = Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();  // Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();
        }catch (Exception e){
            e.printStackTrace();
        }

        return minDiff;

    }

    public String convertLongDataToMb(long data){
        String hrSize = null;

        double mb = ((data/1024.0)/1024.0);
        DecimalFormat dec = new DecimalFormat("0.00");
        hrSize = dec.format(mb);    //.concat(" MB");


/*
        double by = data;
        double kb = data/1024.0;
        double mb = ((data/1024.0)/1024.0);
        double gb = (((data/1024.0)/1024.0)/1024.0);
        double tb = ((((data/1024.0)/1024.0)/1024.0)/1024.0);


        DecimalFormat dec = new DecimalFormat("0.00");
        hrSize = dec.format(mb);    //.concat(" MB");


        if ( tb>1 ) {
            hrSize = dec.format(tb).concat(" TB");
        } else if ( gb>1 ) {
            hrSize = dec.format(gb).concat(" GB");
        } else if ( mb>1 ) {
            hrSize = dec.format(mb).concat(" MB");
        } else if ( kb>1 ) {
            hrSize = dec.format(kb).concat(" KB");
        } else {
            hrSize = dec.format(by).concat(" Bytes");
        }
*/



        return hrSize;

    }



    public static String getDriverStatus(int id){
        String StatusId = "";
        switch (id){

            case 0:
                StatusId = Globally.DRIVING;
                break;

            case 1:
                StatusId = Globally.ON_DUTY;
                break;
            case 2:
                StatusId = Globally.PERSONAL;
                break;

            default:
                StatusId = "";
                break;

        }

        return StatusId;
    }


  public static JSONObject getClaimRecordInputsAsJson(String DriverId, String DriverStatusId,
                                                String UnAssignedVehicleMilesId, String AssignedRecordsId,
                                                      String Remarks, String UserName){

        JSONObject obj = new JSONObject();

        try{
            obj.put(ConstantsKeys.DriverId , DriverId);
            obj.put(ConstantsKeys.DriverStatusId , DriverStatusId);
            obj.put(ConstantsKeys.UnAssignedVehicleMilesId , UnAssignedVehicleMilesId);
            obj.put(ConstantsKeys.AssignedUnidentifiedRecordsId, AssignedRecordsId);
            obj.put(ConstantsKeys.Remarks , Remarks);
            obj.put(ConstantsKeys.UserName , UserName);
        }catch (Exception e){
            e.printStackTrace();
        }

        return obj;
  }



    public static JSONObject getRejectedRecordInputs(String DriverId, String UnAssignedVehicleMilesId, String Remarks){

        JSONObject obj = new JSONObject();

        try{
            obj.put(ConstantsKeys.DriverId , DriverId);
            obj.put(ConstantsKeys.UnAssignedVehicleMilesId , UnAssignedVehicleMilesId);
            obj.put(ConstantsKeys.Remarks , Remarks);
        }catch (Exception e){
            e.printStackTrace();
        }

        return obj;
    }


    public static JSONObject getCompanyRejectedRecordInputs(String DriverId, String UnAssignedVehicleMilesId,
                                                            String AssignedRecordsId, String Remarks){

        JSONObject obj = new JSONObject();

        try{
            obj.put(ConstantsKeys.DriverId , DriverId);
            obj.put(ConstantsKeys.UnAssignedVehicleMilesId , UnAssignedVehicleMilesId);
            obj.put(ConstantsKeys.AssignedUnidentifiedRecordsId, AssignedRecordsId);
            obj.put(ConstantsKeys.RejectionRemarks , Remarks);
        }catch (Exception e){
            e.printStackTrace();
        }

        return obj;
    }



    public static JSONObject getMalfunctionRecordInputs(String DriverId, String Remarks, String eventsCode){

        JSONObject obj = new JSONObject();
        try{
            obj.put(ConstantsKeys.DriverId , DriverId);
            obj.put(ConstantsKeys.Remarks , Remarks);
            obj.put(ConstantsKeys.EventList , eventsCode);
        }catch (Exception e){
            e.printStackTrace();
        }

        return obj;
    }


    public boolean IsSendLog(String DRIVER_ID, DriverPermissionMethod driverPermissionMethod, DBHelper dbHelper){
        JSONObject logPermissionObj    = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DRIVER_ID), dbHelper);
        boolean IsSendLog              = true;

        if(logPermissionObj != null) {
            try {
                IsSendLog = logPermissionObj.getBoolean(ConstantsKeys.SendLog);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return IsSendLog;
    }


    public int getPermitMaxDays(JSONArray recap18DaysArray, int DriverPermitMaxDays, HelperMethods hMethods, Globally global){
        int maxDays = DriverPermitMaxDays;
        int recapArrayLength = recap18DaysArray.length();
        if (DriverPermitMaxDays > recapArrayLength) {
            if(recapArrayLength > 0) {
                try {
                    JSONObject obj = (JSONObject) recap18DaysArray.get(0);
                    String date = obj.getString(ConstantsKeys.Date);
                    DateTime standardDateFormat = global.getDateTimeObj(global.ConvertDateFormat(date), false);
                    DateTime currentDateTime = global.getDateTimeObj(global.getCurrentDate(), false);

                    int diff = hMethods.DayDiff(currentDateTime, standardDateFormat);
                    if(diff <= DriverPermitMaxDays) {
                        maxDays = diff;
                    }else{
                        maxDays = recapArrayLength - 1;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                maxDays = 0;
            }
        }else{
            maxDays = DriverPermitMaxDays;
        }

        return maxDays;
    }


    public static JSONArray getClaimRecordsInArray(
                                            String DriverId, String DriverName,
                                            String reason, String DriverStatusId,
                                             List<UnIdentifiedRecordModel>  unIdentifiedRecordList,
                                             ArrayList<String> recordSelectedList){
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < unIdentifiedRecordList.size(); i++) {
                if (recordSelectedList.get(i).equals("selected")) {
                    JSONObject obj = Constants.getClaimRecordInputsAsJson(DriverId, DriverStatusId,
                            unIdentifiedRecordList.get(i).getUnAssignedVehicleMilesId(),
                            unIdentifiedRecordList.get(i).getAssignedUnidentifiedRecordsId(),
                            reason, DriverName);
                    array.put(obj);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return array;
    }



    public static JSONArray getRejectRecordsInArray(String DriverId, String reason,
                                              List<UnIdentifiedRecordModel>  unIdentifiedRecordList,
                                              ArrayList<String> recordSelectedList){
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < unIdentifiedRecordList.size(); i++) {
                if (recordSelectedList.get(i).equals("selected")) {
                    if(!unIdentifiedRecordList.get(i).isCompanyAssigned()) {
                        JSONObject obj = Constants.getRejectedRecordInputs(DriverId, unIdentifiedRecordList.get(i).getUnAssignedVehicleMilesId(), reason);
                        array.put(obj);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return array;
    }


    public static JSONArray getCompanyRejectRecordsInArray(String DriverId, String reason,
                                                     List<UnIdentifiedRecordModel>  unIdentifiedRecordList,
                                                     ArrayList<String> recordSelectedList){
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < unIdentifiedRecordList.size(); i++) {
                if (recordSelectedList.get(i).equals("selected")) {
                    if(unIdentifiedRecordList.get(i).isCompanyAssigned()) {
                        JSONObject obj = Constants.getCompanyRejectedRecordInputs(DriverId,
                                unIdentifiedRecordList.get(i).getUnAssignedVehicleMilesId(),
                                unIdentifiedRecordList.get(i).getAssignedUnidentifiedRecordsId(),
                                reason);
                        array.put(obj);
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return array;
    }


    public boolean isValidInteger(String text){
        boolean isValid = false;
        try {
            int num = Integer.parseInt(text);
            Log.i("",num+" is a number");
            isValid = true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            isValid = false;
        }catch (Exception e){
            e.printStackTrace();
            isValid = false;
        }
        return isValid;
    }


    public boolean isValidFloat(String text){
        boolean isValid = false;
        try {
            float num = Float.parseFloat(text);
            Log.i("",num+" is a number");
            isValid = true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            isValid = false;
        }catch (Exception e){
            e.printStackTrace();
            isValid = false;
        }
        return isValid;
    }

    public JSONObject getMalfunctionDiagnosticArray(String DriverId, String reason,
                                                    List<MalfunctionHeaderModel> MalHeaderList,
                                                    HashMap<String, List<MalfunctionModel>> MalfunctionChildMap,
                                                    List<MalfunctionHeaderModel> DiaHeaderList,
                                                    HashMap<String, List<MalfunctionModel>> DiafunctionChildMap,
            Context context){

        JSONObject obj = new JSONObject();
        boolean isClearDiagnostic  = SharedPref.IsClearDiagnostic(context);
        boolean isClearMalfunction = SharedPref.IsClearMalfunction(context);
        try {
            obj.put(ConstantsKeys.DriverId , DriverId);
            obj.put(ConstantsKeys.Remarks , reason);
            JSONArray EventsList = new JSONArray();

            for(int i = 0 ; i < MalHeaderList.size() ; i++){
                // Malfunction. (S type event code is not eligible for clear)
                if(isClearMalfunction && MalHeaderList.get(i).getEventCode().equalsIgnoreCase("S")){
                    List<MalfunctionModel> childList = MalfunctionChildMap.get(MalHeaderList.get(i).getEventCode());
                    for(int j = 0; j < childList.size() ; j++){
                        EventsList.put(childList.get(j).getId());
                    }
                }
            }

            if(DiaHeaderList != null) {
                for (int j = 0; j < DiaHeaderList.size(); j++) {
                    // Diagnostic (4 and 5 event code are not eligible for clear)
                    if (isClearDiagnostic && !DiaHeaderList.get(j).getEventCode().equals("4") &&
                            !DiaHeaderList.get(j).getEventCode().equals("5")) {
                        List<MalfunctionModel> childList = DiafunctionChildMap.get(DiaHeaderList.get(j).getEventCode());
                        for (int k = 0; k < childList.size(); k++) {
                            EventsList.put(childList.get(k).getId());
                        }
                    }
                }
            }

            // add all events id as list in json obj
            obj.put(ConstantsKeys.EventList , EventsList);

        }catch (Exception e){
            e.printStackTrace();
        }

        return obj;
    }


    public static JSONObject getMalfunctionRecordsInArray(
            String DriverId, String reason, List<MalfunctionModel> listData){

        JSONObject obj = new JSONObject();

        try {
            obj.put(ConstantsKeys.DriverId , DriverId);
            obj.put(ConstantsKeys.Remarks , reason);

            JSONArray EventsList = new JSONArray();
            for (int i = 0; i < listData.size(); i++) {
                EventsList.put(listData.get(i).getId());
            }
            obj.put(ConstantsKeys.EventList , EventsList);

        }catch (Exception e){
            e.printStackTrace();
        }

        return obj ;
    }



    public void saveAppUsageLog(String source, boolean isPU, boolean isPUOdoDialog, Utils util){
        JSONObject obj = new JSONObject();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        try {

            if(isPU){
                obj.put(ConstantsEnum.PU_Status, source);
                obj.put(ConstantsEnum.IsPUOdoDialog, isPUOdoDialog);
            }else{
                obj.put(Action, source);
            }
            obj.put(FileWriteTime, date);

            if(obj != null) {
                util.writeAppUsageLogFile(obj.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }




    public String getCountryName(Context context){

        String CurrentCycleId = "", countryName;

        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context);
        }else{
            CurrentCycleId = DriverConst.GetCoDriverCurrentCycle(DriverConst.CoCurrentCycleId, context);
        }

        if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2) ){
            countryName = "CANADA";
        }else{
            countryName = "USA";
        }

        return  countryName;
    }



    /*================== Get Signature Bitmap ====================*/
    public String GetSignatureBitmap(View targetView, ImageView canvasView, Context context){
        Bitmap signatureBitmap = Bitmap.createBitmap(targetView.getWidth(),
                targetView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(signatureBitmap);
        targetView.draw(c);
        BitmapDrawable d = new BitmapDrawable(context.getResources(), signatureBitmap);
        canvasView.setBackgroundDrawable(d);

        return Globally.SaveBitmapToFile(signatureBitmap, "sign", 100, context);
    }


    public boolean isReCertifyRequired(Context context, JSONObject dataObj, String CertifyDateTime){

        boolean IsRecertifyRequied = false;
        try {
            DateTime dateTime;
            if(CertifyDateTime.length() == 0) {
                dateTime = Globally.getDateTimeObj(dataObj.getString(ConstantsKeys.ChkDateTime), false);
            }else {
                dateTime = Globally.getDateTimeObj(CertifyDateTime, false);
            }
            JSONArray reCertifyArray = new JSONArray(SharedPref.getReCertifyData(context));

            for (int i = reCertifyArray.length() - 1; i >= 0; i--) {
                JSONObject obj = (JSONObject) reCertifyArray.get(i);

                DateTime selectedDateTime = Globally.getDateTimeObj(obj.getString(ConstantsKeys.LogDate), false);
                if (dateTime.equals(selectedDateTime)) {
                    IsRecertifyRequied = obj.getBoolean(ConstantsKeys.IsRecertifyRequied);
                    break;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return IsRecertifyRequied;
    }




    public static boolean isPlayStoreExist(Context context){
        boolean isPlayStoreExist = false;
        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setPackage("com.android.vending");
            if (i.resolveActivity(packageManager) != null) {
                isPlayStoreExist = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return isPlayStoreExist;


    }


    public boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    public boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }


    public static String parseDateWithName(String date){
        String dateDesc = "";
        String[] dateMonth = Globally.dateConversionMMMM_ddd_dd(date).split(",");

        if(dateMonth.length > 1) {
            dateDesc = dateMonth[1] + " " + date.substring(8, 10) + ", "+ date.substring(0, 4) ;
        }

        return dateDesc;


    }


    public void setTextStyleNormal(TextView textView){
        textView.setTypeface(null, Typeface.NORMAL);
        textView.setTextColor(Color.parseColor("#354365"));
    }
//textView.setTypeface(null, Typeface.NORMAL);



    public List<CanadaDutyStatusModel> parseCanadaDotInList(JSONArray logArray, boolean isSorting){

        List<CanadaDutyStatusModel> dotLogList = new ArrayList<>();
        List<CanadaDutyStatusModel> dotDateWiseList = new ArrayList<>();
        String lastDateTimeMin = "";

        try{
            for(int i = 0 ; i< logArray.length() ; i++) {
                JSONObject obj = (JSONObject)logArray.get(i);

                DateFormat format = new SimpleDateFormat(Globally.DateFormat, Locale.ENGLISH);
                Date date = format.parse(obj.getString(ConstantsKeys.DateTimeWithMins));

                int seqNumber = 0;
                if(!obj.isNull(ConstantsKeys.SequenceNumber)){
                    seqNumber = obj.getInt(ConstantsKeys.SequenceNumber);
                }

                CanadaDutyStatusModel dutyModel = new CanadaDutyStatusModel(
                        CheckNullBString(obj.getString(ConstantsKeys.DateTimeWithMins)),
                        CheckNullBString(obj.getString(ConstantsKeys.EventUTCTimeStamp)),
                        CheckNullBString(obj.getString(ConstantsKeys.DriverStatusID)),

                        obj.getInt(ConstantsKeys.EventType),
                        obj.getInt(ConstantsKeys.EventCode),
                        CheckNullBString(obj.getString(ConstantsKeys.DutyMinutes)),

                        CheckNullBString(obj.getString(ConstantsKeys.Annotation)),
                        CheckNullBString(obj.getString(ConstantsKeys.EventDate)),
                        CheckNullBString(obj.getString(ConstantsKeys.EventTime)),
                        CheckNullBString(obj.getString(ConstantsKeys.AccumulatedVehicleMiles)),
                        CheckNullBString(obj.getString(ConstantsKeys.AccumulatedEngineHours)),
                        CheckNullBString(obj.getString(ConstantsKeys.TotalVehicleMiles)),
                        CheckNullBString(obj.getString(ConstantsKeys.TotalEngineHours)),
                        CheckNullBString(obj.getString(ConstantsKeys.GPSLatitude)),
                        CheckNullBString(obj.getString(ConstantsKeys.GPSLongitude)),
                        CheckNullBString(obj.getString(ConstantsKeys.CMVVIN)),
                        CheckNullBString(obj.getString(ConstantsKeys.CarrierName)),

                        obj.getBoolean(ConstantsKeys.IsMalfunction),

                        CheckNullBString(obj.getString(ConstantsKeys.OdometerInKm)),
                        CheckNullBString(obj.getString(ConstantsKeys.strEventType)),
                        CheckNullBString(obj.getString(ConstantsKeys.Origin)),
                        CheckNullBString(obj.getString(ConstantsKeys.StartTime)),
                        CheckNullBString(obj.getString(ConstantsKeys.EndTime)),
                        CheckNullBString(obj.getString(ConstantsKeys.OBDDeviceDataId)),
                        CheckNullBString(obj.getString(ConstantsKeys.CurrentObdDeviceDataId)),
                        CheckNullBString(obj.getString(ConstantsKeys.DriverLogId)),
                        CheckNullBString(obj.getString(ConstantsKeys.Truck)),
                        CheckNullBString(obj.getString(ConstantsKeys.Trailor)),
                        CheckNullBString(obj.getString(ConstantsKeys.Remarks)),
                        CheckNullBString(obj.getString(ConstantsKeys.DriverId)),

                        obj.getBoolean(ConstantsKeys.IsPersonal),
                        obj.getBoolean(ConstantsKeys.IsYard),

                        CheckNullBString(obj.getString(ConstantsKeys.IsStatusAutomatic)),

                        CheckNullBString(obj.getString(ConstantsKeys.CurrentCycleId)),

                        seqNumber,

                        CheckNullBString(obj.getString(ConstantsKeys.TotalVehicleKM)),
                        CheckNullBString(obj.getString(ConstantsKeys.AdditionalInfo)),
                        CheckNullBString(obj.getString(ConstantsKeys.EditedById)),
                        CheckNullBString(obj.getString(ConstantsKeys.UserName)),

                        CheckNullBString(obj.getString(ConstantsKeys.RecordStatus)),

                        CheckNullBString(obj.getString(ConstantsKeys.DistanceSinceLastValidCord)),
                        CheckNullBString(obj.getString(ConstantsKeys.RecordOrigin)),
                        CheckNullBString(obj.getString(ConstantsKeys.DistanceInKM)),
                        CheckNullBString(obj.getString(ConstantsKeys.HexaSeqNumber)),
                        CheckNullBString(obj.getString(ConstantsKeys.OrderBy)),
                        CheckNullBString(obj.getString(ConstantsKeys.OnDutyHours)),
                        CheckNullBString(obj.getString(ConstantsKeys.OffDutyHours)),
                        CheckNullBString(obj.getString(ConstantsKeys.TruckEquipmentNo)),
                        CheckNullBString(obj.getString(ConstantsKeys.WorkShiftStart)),
                        CheckNullBString(obj.getString(ConstantsKeys.WorkShiftEnd)),
                        date

                );

                if(isSorting){
                 if(i > 0){
                     int dayDiff = getDayDiff(lastDateTimeMin, obj.getString(ConstantsKeys.DateTimeWithMins));
                     if (dayDiff == 0){
                         dotDateWiseList.add(dutyModel);
                     }else{
                         Collections.sort(dotDateWiseList);
                         for(int listPos = 0 ; listPos < dotDateWiseList.size() ; listPos++){
                             dotLogList.add(dotDateWiseList.get(listPos));
                         }
                         dotDateWiseList = new ArrayList<>();
                     }
                 }else{
                     dotDateWiseList.add(dutyModel);
                 }
                }else {
                    dotLogList.add(dutyModel);
                }
                lastDateTimeMin = obj.getString(ConstantsKeys.DateTimeWithMins);
            }

            if(dotLogList.size() == 0){
                Collections.sort(dotDateWiseList);
                for(int listPos = 0 ; listPos < dotDateWiseList.size() ; listPos++){
                    dotLogList.add(dotDateWiseList.get(listPos));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return dotLogList;
    }

    public void setMarqueonView(final TextView textView){

        textView.setHorizontallyScrolling(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine(true);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSelected(true);

    }


    public List<UnAssignedVehicleModel> parseCanadaDotUnIdenfdVehList(JSONArray logArray){

        List<UnAssignedVehicleModel> dotLogList = new ArrayList<>();

        try{
            for(int i = 0 ; i< logArray.length() ; i++) {
                JSONObject obj = (JSONObject)logArray.get(i);
                UnAssignedVehicleModel dutyModel = new UnAssignedVehicleModel(
                        CheckNullBString(obj.getString(ConstantsKeys.UnAssignedVehicleMilesId)),
                        CheckNullBString(obj.getString(ConstantsKeys.AssignedUnidentifiedRecordsId)),
                        CheckNullBString(obj.getString(ConstantsKeys.EquipmentNumber)),
                        CheckNullBString(obj.getString(ConstantsKeys.VIN)),

                        CheckNullBString(obj.getString(ConstantsKeys.StartOdometer)),
                        CheckNullBString(obj.getString(ConstantsKeys.EndOdometer)),
                        CheckNullBString(obj.getString(ConstantsKeys.TotalMiles)),
                        CheckNullBString(obj.getString(ConstantsKeys.TotalKm)),

                        CheckNullBString(obj.getString(ConstantsKeys.DriverZoneStartDateTime)),
                        CheckNullBString(obj.getString(ConstantsKeys.DriverZoneEndDateTime)),
                        CheckNullBString(obj.getString(ConstantsKeys.StatusId)),

                        obj.getBoolean(ConstantsKeys.IsIntermediateLog),
                        CheckNullBString(obj.getString(ConstantsKeys.HexaSeqNumber)),

                        CheckNullBString(obj.getString(ConstantsKeys.StartLocation)),
                        CheckNullBString(obj.getString(ConstantsKeys.EndLocation)),
                        CheckNullBString(obj.getString(ConstantsKeys.DutyStatus))

                        );

                dotLogList.add(dutyModel);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return dotLogList;
    }


    public String checkNullString(String data){
        if(data.equals("null") || data.length() == 0){
            data = "--";
        }
        return data;
    }



    // get Obd odometer Data
    public  void saveOdometer(String DriverStatusId, String DriverId, String DeviceId, JSONArray driver18DaysLogArray,
                              OdometerHelperMethod odometerhMethod, HelperMethods hMethods, DBHelper dbHelper, Context context) {

        try {
             int ObdStatus = SharedPref.getObdStatus(context);
            if (ObdStatus == Constants.WIRED_CONNECTED ||
                    ObdStatus == Constants.WIFI_CONNECTED ||
                    ObdStatus == Constants.BLE_CONNECTED) {
                int lastJobStatus = hMethods.getSecondLastJobStatus(driver18DaysLogArray);
                int currentJobStatus = Integer.valueOf(DriverStatusId);

                String odometerValue ;
                if (ObdStatus == Constants.WIRED_CONNECTED) {
                    odometerValue = SharedPref.getWiredObdOdometer(context);
                } else {
                    odometerValue = SharedPref.GetWifiObdOdometer(context);   // get odometer value from wifi obd
                }

                if (!odometerValue.equals("0")) {
                    if ((currentJobStatus == ON_DUTY || currentJobStatus == DRIVING) && (lastJobStatus == OFF_DUTY || lastJobStatus == SLEEPER) ||
                            (currentJobStatus == OFF_DUTY || currentJobStatus == SLEEPER) && (lastJobStatus == DRIVING || lastJobStatus == ON_DUTY)) {

                        odometerhMethod.AddOdometerAutomatically(DriverId, DeviceId, String.valueOf(odometerValue), DriverStatusId, dbHelper, context);

                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    // Save Driver Cycle From OBD data those are getting from als server.
    public void SaveCycleWithCurrentDate(int CycleId, String currentUtcDate, String changeType, SharedPref sharedPref, Globally global, Context context){


        try {
            /* ------------- Save Cycle details with time is different with earlier cycle --------------*/
            String CurrentCycle   = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context );
            if(CycleId != -1 && !CurrentCycle.equals(CycleId)) {
                JSONArray cycleDetailArray = global.getSaveCycleRecords(CycleId, changeType, context);
                sharedPref.SetCycleOfflineDetails(cycleDetailArray.toString(), context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        switch (CycleId){

            case 1:
                DriverConst.SetDriverCurrentCycle(Globally.CANADA_CYCLE_1_NAME, Globally.CANADA_CYCLE_1, context);
                DriverConst.SetCoDriverCurrentCycle(Globally.CANADA_CYCLE_1_NAME, Globally.CANADA_CYCLE_1, context);
                break;

            case 2:
                DriverConst.SetDriverCurrentCycle(Globally.CANADA_CYCLE_2_NAME, Globally.CANADA_CYCLE_2, context);
                DriverConst.SetCoDriverCurrentCycle(Globally.CANADA_CYCLE_2_NAME, Globally.CANADA_CYCLE_2, context);
                break;

            case 3:
                DriverConst.SetDriverCurrentCycle(Globally.USA_WORKING_6_DAYS_NAME, Globally.USA_WORKING_6_DAYS, context);
                DriverConst.SetCoDriverCurrentCycle(Globally.USA_WORKING_6_DAYS_NAME, Globally.USA_WORKING_6_DAYS, context);
                break;

            case 4:
                DriverConst.SetDriverCurrentCycle(Globally.USA_WORKING_7_DAYS_NAME, Globally.USA_WORKING_7_DAYS, context);
                DriverConst.SetCoDriverCurrentCycle(Globally.USA_WORKING_7_DAYS_NAME, Globally.USA_WORKING_7_DAYS, context);
                break;

        }

        // Save Current Date
        sharedPref.setCurrentDate(currentUtcDate, context);



    }



    public boolean isAllowLocMalfunctionEvent(Context context){
          return (SharedPref.IsAllowMalfunction(context) || SharedPref.IsAllowDiagnostic(context)) && SharedPref.isLocMalfunctionOccur(context);
    }

    public boolean isLocMalfunctionEvent(Context context, int DriverType){
        if(DriverType == Constants.MAIN_DRIVER_TYPE) {
            return (SharedPref.IsAllowMalfunction(context) || SharedPref.IsAllowDiagnostic(context)) && SharedPref.isLocMalfunctionOccur(context) ;
        }else{
            return (SharedPref.IsAllowMalfunctionCo(context) || SharedPref.IsAllowDiagnosticCo(context)) && SharedPref.isLocMalfunctionOccur(context) ;
        }
    }



    // ------------- Check Power Diagnostic/Malfunction status ---------------
    public String isPowerDiaMalOccurred(String currentHighPrecisionOdometer, String ignitionStatus,
                                         String obdEngineHours, String DriverId, Globally global,
                                         MalfunctionDiagnosticMethod malfunctionDiagnosticMethod,
                                         Context context, DBHelper dbHelper){

        String eventStatus = "";

        try {
            String lastIgnitionStatus = SharedPref.GetTruckIgnitionStatus(Constants.TruckIgnitionStatusMalDia, context);
            if (lastIgnitionStatus.equals("OFF")) {
                boolean isCurrentEngHourGreater = false;
                boolean isOdometerDiffValid = false;
                String lastSavedTime = SharedPref.GetTruckIgnitionStatus(Constants.IgnitionTimeMalDia, context);
                if (lastSavedTime.length() > 10) {
                    String lastOdometer = SharedPref.GetTruckIgnitionStatus(Constants.OdometerMalDia, context);
                    String lastEngineHour = SharedPref.GetTruckIgnitionStatus(Constants.EngineHourMalDia, context);

                    int minDiff = minDiff(lastSavedTime, global, context);
                    if (minDiff > 0) {
                        if (isValidFloat(lastEngineHour) && isValidFloat(obdEngineHours)) {

                            int lastEngineHourInt = global.MinFromHourOnly(Math.round(Float.parseFloat(lastEngineHour)));
                            int currEngineHourInt = global.MinFromHourOnly(Math.round(Float.parseFloat(obdEngineHours)));
                            if (currEngineHourInt > lastEngineHourInt) {
                                isCurrentEngHourGreater = true;
                            }
                        }

                        if (isValidFloat(currentHighPrecisionOdometer) && isValidFloat(lastOdometer)) {
                            float odoDiff = Float.parseFloat(meterToKm(currentHighPrecisionOdometer)) - Float.parseFloat(meterToKm(lastOdometer));
                            if (odoDiff >= 2) {
                                isOdometerDiffValid = true;
                            }
                        }

                        if (isCurrentEngHourGreater || isOdometerDiffValid) {

                            int earlierEventTime = malfunctionDiagnosticMethod.getTotalPowerComplianceMin(dbHelper);
                            int totalMinDia = minDiff + earlierEventTime;
                            DateTime currentTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);

                            // DateTime lastSavedDateTime = global.getDateTimeObj(lastSavedTime, false);
                            /*malfunctionDiagnosticMethod.updateOccEventTimeLog(currentTime, DriverId,
                                    SharedPref.getVINNumber(context), lastSavedDateTime, currentTime,
                                    context.getResources().getString(R.string.PwrComplianceEvent), ConstantsKeys.PowerDiagnstc,
                                    dbHelper, context);*/

                            malfunctionDiagnosticMethod.updatePowerOccEventLog(currentTime, dbHelper);

                            if (totalMinDia >= 20) {  // malfunction event time is 30 min

                                if (SharedPref.isPowerMalfunction(context)) {
                                    int dayDiff = getDayDiff(SharedPref.getPowerMalOccTime(context), global.GetCurrentDateTime());
                                    if (dayDiff > 0) {
                                        eventStatus = MalfunctionEvent;
                                        Globally.PlaySound(context);
                                        global.ShowLocalNotification(context,
                                                context.getResources().getString(R.string.malfunction_events),
                                                context.getResources().getString(R.string.power_comp_mal_occured), 2093);

                                        // Save power mal status with updated time
                                        SharedPref.savePowerMalfunctionStatus(true, global.GetCurrentDateTime(), context);

                                    }
                                } else {
                                    eventStatus = MalfunctionEvent;
                                    Globally.PlaySound(context);
                                    global.ShowLocalNotification(context,
                                            context.getResources().getString(R.string.malfunction_events),
                                            context.getResources().getString(R.string.power_comp_mal_occured), 2093);

                                    // Save power mal status with updated time
                                    SharedPref.savePowerMalfunctionStatus(true, global.GetCurrentDateTime(), context);

                                }


                            } else {
                                eventStatus = DiagnosticEvent;
                                Globally.PlaySound(context);
                                global.ShowLocalNotification(context,
                                        context.getResources().getString(R.string.dia_event),
                                        context.getResources().getString(R.string.power_dia_occured), 2092);

                                SharedPref.savePowerMalfunctionStatus(false, global.GetCurrentDateTime(), context);
                            }

                            // save updated values with truck ignition status
                            SharedPref.SetTruckIgnitionStatus(ignitionStatus, WiredOBD, global.getCurrentDate(), obdEngineHours, currentHighPrecisionOdometer, context);
                        }
                    }else{
                        SharedPref.savePowerMalfunctionStatus(false, global.GetCurrentDateTime(), context);
                    }

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return eventStatus;
    }



    // ------------- Malfunction status ---------------
    // x = when no loc or valid position for 8 km
    // m = If driver enter manual loc after position malfunction occur
    // e = if position malfunction occur more then 60 min

    public boolean isLocationMalfunctionOccured(Context context){
        boolean isMalfunction = false;
        int ObdStatus = SharedPref.getObdStatus(context);
        try {
            if (ObdStatus == Constants.WIRED_CONNECTED ||
                    ObdStatus == Constants.WIFI_CONNECTED ||
                    ObdStatus == Constants.BLE_CONNECTED) {

                if (SharedPref.getEcmObdLatitude(context).length() < 4) {

                    String currentOdometer = SharedPref.getHighPrecisionOdometer(context);
                    String lastOdometer = SharedPref.getEcmOdometer(context);

                    if (lastOdometer.length() > 1) {
                        double odometerDistance = Double.parseDouble(currentOdometer) - Double.parseDouble(lastOdometer);
                        odometerDistance = odometerDistance / 1000;

                        if (SharedPref.isLocMalfunctionOccur(context)) {
                            DateTime malfunctionOccurTime = Globally.getDateTimeObj(SharedPref.getLocMalfunctionOccuredTime(context), false);
                            DateTime currentTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
                            int minDiff = Minutes.minutesBetween(malfunctionOccurTime, currentTime).getMinutes();  // Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();

                            if (minDiff >= 30) {   //temp value 30 for testing. After 60 min it will become e type on loc mal
                                SharedPref.setLocMalfunctionType("e", context);
                            } else {
                                if (odometerDistance >= 8) {
                                    if(!SharedPref.getLocMalfunctionType(context).equals("m")) {
                                        SharedPref.setLocMalfunctionType("x", context);
                                    }
                                }
                            }

                            isMalfunction = true;

                        } else {
                            if (odometerDistance >= 8) {

                                if(!SharedPref.getLocMalfunctionType(context).equals("m")) {
                                    SharedPref.setLocMalfunctionType("x", context);
                                }
                                isMalfunction = true;
                            } else {
                                SharedPref.setLocMalfunctionType("", context);
                            }


                        }

                        isLocMalOccurDueToTime(context, true, isMalfunction);

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isMalfunction;
    }


    public boolean isLocMalOccurDueToTime(Context context, boolean isSaveMalfunctionStatus, boolean isDistanceMalfncn ){
        boolean isMalfunction = false;
        String lastSavedDate    = SharedPref.getEcmObdTime(context);
        if (lastSavedDate.length() > 10) {
            DateTime currentDate = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
            DateTime savedDateTime = Globally.getDateTimeObj(lastSavedDate, false);
            int minDiff = Minutes.minutesBetween(savedDateTime, currentDate).getMinutes();

            if(minDiff > 60){  // set val as "e" when time will be greater then 1 hour
                SharedPref.setLocMalfunctionType("e", context);
                isMalfunction = true;
            }else{
                boolean isLocMalfunctionOccur = SharedPref.isLocMalfunctionOccur(context);
                String malfunctionType = SharedPref.getLocMalfunctionType(context);
                if(isLocMalfunctionOccur && malfunctionType.equals("m")){
                    SharedPref.setLocMalfunctionType("m", context);
                    isMalfunction = true;
                }
            }

            if(isSaveMalfunctionStatus) {
                if(isMalfunction == false) {
                    SharedPref.setLocMalfunctionType("", context);
                    saveDiagnstcStatus(context, isDistanceMalfncn);
                }else{
                    saveMalfncnStatus(context, isMalfunction);
                }
            }

        }

        return isMalfunction;
    }


    public void saveDiagnstcStatus(Context context, boolean isDiagnosticOccur){
        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(context),
                    SharedPref.isMalfunctionOccur(context),
                    isDiagnosticOccur,
                    SharedPref.isSuggestedEditOccur(context), context);
        }else{
            SharedPref.setEldOccurencesCo(SharedPref.isUnidentifiedOccurCo(context),
                    SharedPref.isMalfunctionOccurCo(context),
                    isDiagnosticOccur,
                    SharedPref.isSuggestedEditOccurCo(context), context);
        }

    }

    public void saveMalfncnStatus(Context context, boolean isMalfncnOccur){
        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(context),
                    isMalfncnOccur,
                    SharedPref.isDiagnosticOccur(context),
                    SharedPref.isSuggestedEditOccur(context), context);
        }else{
            SharedPref.setEldOccurencesCo(SharedPref.isUnidentifiedOccurCo(context),
                    isMalfncnOccur,
                    SharedPref.isDiagnosticOccurCo(context),
                    SharedPref.isSuggestedEditOccurCo(context), context);
        }

    }

    public static boolean isTabletDevice(Context activityContext) {

        boolean device_large = ((activityContext.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);
        DisplayMetrics metrics = new DisplayMetrics();
        Activity activity = (Activity) activityContext;
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (device_large) {
            //Tablet
            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT){
                return true;
            }else if(metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM){
                return true;
            }else if(metrics.densityDpi == DisplayMetrics.DENSITY_TV){
                return true;
            }else if(metrics.densityDpi == DisplayMetrics.DENSITY_HIGH){
                return true;
            }else if(metrics.densityDpi == DisplayMetrics.DENSITY_280){
                return true;
            }else if(metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                return true;
            }else if(metrics.densityDpi == DisplayMetrics.DENSITY_400) {
                return true;
            }else if(metrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH) {
                return true;
            }else if(metrics.densityDpi == DisplayMetrics.DENSITY_560) {
                return true;
            }else if(metrics.densityDpi == DisplayMetrics.DENSITY_XXXHIGH) {
                return true;
            }
        }else{
            //Mobile
        }
        return false;
    }


    // Duty Status Changes, Intermediate Logs and Special Driving Condition(Personal Use and Yard Move)
    public String getDutyChangeEventName(int EventType, int EventCode, boolean IsPersonal, boolean IsYard ){
        String event = "";
        switch (EventType){
            case 1:
                if (EventCode == DRIVING)
                {
                    event = "DR";
                }
                else if (EventCode == ON_DUTY)
                {
                    event = "ON";
                }
                else if (EventCode == SLEEPER)
                {
                    event = "SB";
                }
                else
                {
                    event = "OFF";
                }

                break;

            case 2:
                event = "INT";
                break;

            case 3:
                if (EventCode == 1)
                {
                    event = "PC Start";
                }
                else if (EventCode == 2)
                {
                    event = "YM Start";
                }
                else if (IsPersonal == true)
                {
                    event = "PC End";
                }
                else if (IsYard == true)
                {
                    event = "YM End";
                }

                break;

            case 4:  // Certify Log
                event = "Certification of RODS";
                break;

            case 5: // Login Logout
                if (EventCode == Login)
                {
                    event = "Login";
                }
                else if (EventCode == Logout)
                {
                    event = "Logout";
                }
                break;

            case 6:  // Engine Up Down
                if (EventCode == EngineUp)
                {
                    event = "Power Up";
                }
                else if (EventCode == EngineDown)
                {
                    event = "Shut Down";
                }
                break;


            default:
                event = "" +EventType;
                break;

        }

        return event;
    }


    // Login and Logout, Certification of RODS, Data Diagnostic and Malfunction
    public String getLoginLogoutEventName(int EventType, int EventCode){
        String event = "";

        switch (EventType){

            case 4:     // Certify Log
                if(EventCode == 1){
                    event = "Certification of RODS (1)";
                }else{
                    event = "Re-Certification of RODS (" + EventCode + ")";
                }
                break;

            case 5: // Login Logout
                if (EventCode == Login)
                {
                    event = "Login";
                }
                else if (EventCode == Logout)
                {
                    event = "Logout";
                }
                break;

            case 7: // Diagnostic and Malfunction
                if (EventCode == MalfunctionActive)
                {
                    event = "Malfunction (detected)";
                }
                else if (EventCode == MalfunctionInactive)
                {
                    event = "Malfunction (cleared)";
                }
                else if (EventCode == DiagnosticActive)
                {
                    event = "Data Diagnostic (detected)";
                }
                else if (EventCode == DiagnosticInactive)
                {
                    event = "Data Diagnostic (cleared)";
                }
                break;


            default:
                event = "" +EventType;
                break;

        }

        return event;

    }



    // Change in Driver's Cycle. Change in Operating Zone, Off Dutty Time Deferral
    public String getCycleOpZoneEventName(int EventType, int EventCode){
        String event = "";

        switch (EventType){

            case 21:
                if (EventCode == 1)
                {
                event = "Cycle 1 (7 days)";
                }
                else if (EventCode == 2)
                {
                    event = "Cycle 2 (14 days)";
                } else if (EventCode == 3)
                {
                    event = "US (60/7)";
                } else if (EventCode == 4)
                {
                    event = "US (70/8)";
                }
                else
                {
                    event = "US";
                }
                break;

            default:
            event = "Remarks";
            break;

        }

        return event;
    }


    //  Engine Power Up and Shut Down
    public String getEnginePowerUpDownEventName(int EventType, int EventCode){
        String event = "";

        switch (EventType){

            case 6: // Engine Up Down
                if (EventCode == EngineUp)
                {
                    event = "Power Up";
                }
                else if (EventCode == EngineDown)
                {
                    event = "Shut Down";
                }
                break;

            default:
                event = ""+EventCode;
                break;

        }

        return event;
    }



    public int minDiff(String savedTime, Globally global, Context context){

        int timeInMin = 0;
        if(savedTime.length() > 10) {
            try{
                String timeStampStr = savedTime.replace(" ", "T");
                DateTime savedDateTime = global.getDateTimeObj(timeStampStr, false);
                DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);

                if(savedDateTime.isAfter(currentDateTime)){
                    SharedPref.saveHighPrecisionOdometer(SharedPref.getHighPrecisionOdometer(context), global.GetCurrentDateTime(), context);
                }
                timeInMin = Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return timeInMin;

    }


    public int minDiffMalfunction(String savedTime, Globally global, Context context){

        int timeInMin = 0;
        if(savedTime.length() > 10) {
            try{
                String timeStampStr = savedTime.replace(" ", "T");
                DateTime savedDateTime = global.getDateTimeObj(timeStampStr, false);
                DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);

                if(savedDateTime.isAfter(currentDateTime)){
                    SharedPref.setMalfCallTime(global.GetCurrentDateTime(), context);
                }
                timeInMin = Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return timeInMin;

    }


    public int checkIntValue(int value){
        if(value < 0)
            value = 0;

        return value;
    }

    public int getListNewDateCount(List<CanadaDutyStatusModel> DutyStatusList){
        int count = 0;
        for(int i = 0; i < DutyStatusList.size() ; i++){
            if (i == 0) {
                count++;
            } else {
                int dayDiff = getDayDiff(DutyStatusList.get(i-1).getDateTimeWithMins(), DutyStatusList.get(i).getDateTimeWithMins());
                if (dayDiff != 0){
                    count++;
                }
            }
        }

        return count;
    }


    public JSONArray getEldNotificationReadInput(JSONArray dataArray, String DriverId, String DeviceId){
        JSONArray inputArray = new JSONArray();
        try {
            for (int i = 0; i < dataArray.length(); i++) {

                JSONObject obj = (JSONObject) dataArray.get(i);
                JSONObject inputJson = new JSONObject();
                inputJson.put(ConstantsKeys.DriverId, DriverId);
                inputJson.put(ConstantsKeys.DeviceId, DeviceId);
                inputJson.put(ConstantsKeys.SettingName, obj.getString(ConstantsKeys.SettingName));
                inputJson.put(ConstantsKeys.ActionDateTime, obj.getString(ConstantsKeys.ActionDateTime));

                inputArray.put(inputJson);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return inputArray;
    }



    String addZeroForSingle(Globally Global, int min){
        String hours = "" +Global.HourFromMin(min);
        if(hours.length() == 1){
            return "0" + hours ;
        }else{
            return hours;
        }
    }

    String addZeroForMinSingle(Globally Global, int min){
        String hours = "" +Global.MinFromHourOnly(min);
        if(hours.length() == 1){
            return "0" + hours + " Hrs";
        }else{
            return hours + " Hrs";
        }
    }



    public String CalculateCycleTimeData(Context context, String DriverId, boolean OperatingZoneChange, boolean isNorth, String changedCycleId,
                                            Globally Global, SharedPref sharedPref, HelperMethods hMethods,DBHelper dbHelper ){

        int offsetFromUTC = (int) Global.GetTimeZoneOffSet();
        List<DriverLog> oDriverLogDetail ;

        String finalCycleData = "";
        String currentJobStatus     = sharedPref.getDriverStatusId("jobType", context);

        DateTime currentDateTime    = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);    // Current Date Time
        DateTime currentUTCTime     = Globally.getDateTimeObj(Globally.GetCurrentUTCTimeFormat(), true);
        oDriverLogDetail           = hMethods.getSavedLogList(Integer.valueOf(DriverId), currentDateTime, currentUTCTime, dbHelper);

        int rulesVersion = sharedPref.GetRulesVersion(context);
        boolean isHaulExcptn, isAdverseExcptn, IsNorthCanada;
        boolean isSingleDriver = false;

        if (sharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            isHaulExcptn = sharedPref.get16hrHaulExcptn(context);
            isAdverseExcptn = sharedPref.getAdverseExcptn(context);
            isSingleDriver = true;
        } else {
            isHaulExcptn = sharedPref.get16hrHaulExcptnCo(context);
            isAdverseExcptn = sharedPref.getAdverseExcptnCo(context);
        }

        if(OperatingZoneChange){
            IsNorthCanada = isNorth;
        }else {
            IsNorthCanada = sharedPref.IsNorthCanada(context);
        }


        DriverDetail oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DriverId),
                offsetFromUTC, Integer.valueOf(changedCycleId), isSingleDriver, Integer.valueOf(currentJobStatus), false,
                isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                rulesVersion, oDriverLogDetail);

        // EldFragment.SLEEPER is used because we are just checking cycle time
        RulesResponseObject RulesObj = hMethods.CheckDriverRule(Integer.valueOf(changedCycleId), EldFragment.SLEEPER, oDriverDetail);

        // Calculate 2 days data to get remaining Driving/Onduty hours
        RulesResponseObject RemainingTimeObj = hMethods.getRemainingTime(currentDateTime, currentUTCTime, offsetFromUTC,
                Integer.valueOf(changedCycleId), isSingleDriver, Integer.valueOf(DriverId) , Integer.valueOf(currentJobStatus), false,
                isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                rulesVersion, dbHelper);

        try {
            int CycleRemainingMinutes   = checkIntValue((int) RulesObj.getCycleRemainingMinutes());
            int OnDutyRemainingMinutes  = checkIntValue((int) RemainingTimeObj.getOnDutyRemainingMinutes());
            int DriveRemainingMin       = checkIntValue((int) RemainingTimeObj.getDrivingRemainingMinutes());
            int ShiftRemainingMin       = checkIntValue((int) RemainingTimeObj.getShiftRemainingMinutes());

            if(CycleRemainingMinutes < OnDutyRemainingMinutes){
                OnDutyRemainingMinutes = CycleRemainingMinutes;
            }

            if(ShiftRemainingMin < 0){
                ShiftRemainingMin = 0;
            }


            String CycleRemaining          = addZeroForSingle(Global, CycleRemainingMinutes) + ":" + addZeroForMinSingle(Global, CycleRemainingMinutes) ;
            String OnDutyRemaining         = addZeroForSingle(Global, OnDutyRemainingMinutes) + ":" + addZeroForMinSingle(Global, OnDutyRemainingMinutes) ;
            String DriveRemaining          = addZeroForSingle(Global, DriveRemainingMin) + ":" + addZeroForMinSingle(Global, DriveRemainingMin) ;
            String ShiftRemaining          = addZeroForSingle(Global, ShiftRemainingMin) + ":" + addZeroForMinSingle(Global, ShiftRemainingMin) ;

            finalCycleData = "<font color='#3F88C5'>" +
                    "<b>Cycle &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b> :&nbsp;&nbsp; " + CycleRemaining + "<br/>" +
                    "<b>Driving  &nbsp;&nbsp;</b>&nbsp;:&nbsp;&nbsp; " + DriveRemaining + "<br/>" +
                    "<b>OnDuty   &nbsp;&nbsp;</b>&nbsp:&nbsp        " + OnDutyRemaining   + "<br/>" +
                    "<b>Shift    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>&nbsp;:&nbsp  " + ShiftRemaining+ " </font>" ;

           // txtView.setText(Html.fromHtml(finalCycleData) );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalCycleData;

    }


    public boolean isActionAllowedWithCoDriver(Context context, DBHelper dbHelper, HelperMethods hMethods, Globally Global, String DRIVER_ID){
        boolean isAllowed = true;
        int ObdStatus = SharedPref.getObdStatus(context);
        if((ObdStatus == Constants.WIRED_CONNECTED ||
                ObdStatus == Constants.WIFI_CONNECTED ||
                ObdStatus == Constants.BLE_CONNECTED) &&
                SharedPref.isVehicleMoving(context) ){
            isAllowed = false;
        }
        boolean isCoDriverInDrYMPC = hMethods.isCoDriverInDrYMPC(context, Global, DRIVER_ID, dbHelper);

        if(isCoDriverInDrYMPC && isAllowed == false){
            isAllowed = true;
        }

        return isAllowed;
    }





    public boolean isActionAllowed(Context context){
        boolean isAllowed = true;
        int ObdStatus = SharedPref.getObdStatus(context);
        boolean isVehicleMoving = SharedPref.isVehicleMoving(context);
        if((ObdStatus == Constants.WIRED_CONNECTED || ObdStatus == Constants.WIFI_CONNECTED
                || ObdStatus == Constants.BLE_CONNECTED) && isVehicleMoving ){
            isAllowed = false;
        }
        return isAllowed;
    }

    public boolean isObdConnected(Context context){
        boolean isObdConnected = false;
        if (SharedPref.getObdStatus(context) == Constants.WIFI_CONNECTED || SharedPref.getObdStatus(context) == Constants.WIRED_CONNECTED
                || SharedPref.getObdStatus(context) == Constants.BLE_CONNECTED){
            isObdConnected = true;
        }

        return true;
    }


    public void checkBleConnection(){

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
