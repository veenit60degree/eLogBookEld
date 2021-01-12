package com.constants;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
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
import com.local.db.RecapViewMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.models.EldDataModelNew;
import com.models.MalfunctionHeaderModel;
import com.models.MalfunctionModel;
import com.models.NotificationHistoryModel;
import com.models.PrePostModel;
import com.models.RecapSignModel;
import com.models.UnIdentifiedRecordModel;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.CoNotificationPref;
import com.shared.pref.MainDriverEldPref;
import com.shared.pref.NotificationPref;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
    public static String Bluetooth = "bluetooth_obd";
    public static String ApiData = "api_data";
    public static String OfflineData = "offline_data";
    public static String DataMalfunction = "Data_Malfunction";

    public static int ConnectionMalfunction = 0;
    public static int ConnectionWired = 1;
    public static int ConnectionWifi = 2;
    public static int ConnectionBluetooth = 3;
    public static int ConnectionApi = 4;
    public static int ConnectionOffline = 5;
    public static int CertifyLog = 101010;      // set this value to differentiate where we go on certify screen

    public static final int WIRED_ACTIVE     = 1001;
    public static final int WIRED_INACTIVE   = 1002;
    public static final int WIFI_ACTIVE      = 1003;
    public static final int WIFI_INACTIVE    = 1004;
    public static final int NO_CONNECTION    = 1005;


    public static String TruckIgnitionStatus = "TruckIgnitionStatus";
    public static String IgnitionSource = "IgnitionSource";
    public static String LastIgnitionTime = "LastIgnitionTime";

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

    public static String DriverLogId = "";
    public static String IsStartingLocation = "";

    public static int OFF_DUTY = 1;
    public static int SLEEPER = 2;
    public static int DRIVING = 3;
    public static int ON_DUTY = 4;
    public static int PERSONAL = 1;

    public static int WIRED_OBD = 1001;
    public static int WIFI_OBD = 1002;
    public static int API = 1003;
    public static int OFFLINE = 1004;
    public static int OTHER_SOURCE = 1005;

     // EventCode Type Inside the list
    public static int Malfunction             = 1;    // EventCode value is an String in main array
    public static int Diagnostic              = 3;    // EventCode value is an Integer in main array
    public static int clearEvent              = 2;


    public static int EditRemarks = 101;
    public static int EditLocation = 102;
    public static int NOTIFICATION_ID = 0;
    public static int SocketTimeout1Sec = 1000;   // 1 second
    public static int SocketTimeout3Sec = 3000;   // 3 seconds
    public static int SocketTimeout4Sec = 4000;   // 3 seconds
    public static int SocketTimeout5Sec = 5000;   // 5 seconds
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

        jsonArray.put(locationObj);
    }


    public JSONObject GetJsonFromList(JSONArray jsonArray, int pos) throws JSONException {

        JSONObject obj = (JSONObject) jsonArray.get(pos);
        JSONObject locationObj = new JSONObject();

        String IsStatusAutomatic = "false", OBDSpeed = "0", GPSSpeed = "0", PlateNumber = "";
        String decesionSpurce = "", HaulHourException = "false", TruckNumber = "";
        String isAdverseException = "", adverseExceptionRemark = "";

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


        return locationObj;
    }


    public static String CheckNullString(String inputValue) {

        if (inputValue == null || inputValue.equals("null")) {
            inputValue = "";
        }
        return inputValue;
    }


    public void ClearNotifications(Context context) {
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

        if (DriverType == 0) {
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

        return gpsStatus;
    }


    public void SaveNotification(int DriverType, String title, String violationReason, String currentDateTime, HelperMethods helperMethods,
                                 NotificationPref notificationPref, CoNotificationPref coNotificationPref, Context context) {
        NotificationHistoryModel notificationsModel;
        int id;
        if (DriverType == 0) {

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
            if (DriverType == 0) {
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
            if (DriverType == 0) {
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

        if (DriverType == 0) { // Main Driver
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


    public JSONArray AddNewStatusInList(String DriverName, String DriverStatusId, String violaotionReason, String address,
                                        String DRIVER_ID, String City, String State, String Country, String AddressLine,
                                        String CurrentCycleId, String Reason, String isPersonal, boolean isViolation,
                                        String IsStatusAutomatic, String OBDSpeed, String GPSSpeed, String PlateNumber,
                                        String decesionSource, boolean isYardMove,
                                        Globally Global, boolean isHaulException, boolean isHaulExceptionUpdate,
                                        String isAdverseException, String adverseExceptionRemark,
                                        HelperMethods hMethods, DBHelper dbHelper) {

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
                adverseExceptionRemark

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



             /*   if (initilizeValue > 0) {

                    if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS) ) {
                        daysValidationValue = initilizeValue - DriverPermittedDays;
                    }

                } else {
                    daysValidationValue = initilizeValue;
                }*/

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

                if (initilizeValue > DriverPermittedDays) {
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
            obj.put("DriverId", DriverId);
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
                    SharedPref.get16hrHaulExcptn(context),  sharedPref.getAdverseExcptn(context),
                    rulesVersion, oDriverLogDetail);
            // RulesResponseObject RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), Integer.valueOf(DRIVER_JOB_STATUS), oDriverDetail);
            // isHaulException = RulesObj.isEligibleFor16HrsException();

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
            Toast.makeText(context, "Sim number copied", Toast.LENGTH_LONG).show();
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
        }
        return isValid;
    }



    public JSONObject getMalfunctionDiagnosticArray(
            String DriverId, String reason, List<MalfunctionHeaderModel> headerList,
             HashMap<String, List<MalfunctionModel>> malfunctionChildHashMap,
            Context context){

        JSONObject obj = new JSONObject();
        boolean isClearDiagnostic  = SharedPref.IsClearDiagnostic(context);
        boolean isClearMalfunction = SharedPref.IsClearMalfunction(context);
        try {
            obj.put(ConstantsKeys.DriverId , DriverId);
            obj.put(ConstantsKeys.Remarks , reason);
            JSONArray EventsList = new JSONArray();

            for(int i = 0 ; i < headerList.size() ; i++){
                // If EventCode is valid integer it is Diagnostic other wise Malfunction
                if(isValidInteger(headerList.get(i).getEventCode() ) ){
                    // Diagnostic (4 and 5 event code are not eligible for clear)
                    if(isClearDiagnostic && !headerList.get(i).getEventCode().equals("4") &&
                            !headerList.get(i).getEventCode().equals("5")){
                        List<MalfunctionModel> childList = malfunctionChildHashMap.get(headerList.get(i).getEventCode());
                        for(int j = 0; j < childList.size() ; j++){
                            EventsList.put(childList.get(j).getId());
                        }
                    }
                }else{
                    // Malfunction. (S type event code is not eligible for clear)
                    if(isClearMalfunction && headerList.get(i).getEventCode().equalsIgnoreCase("S")){
                        List<MalfunctionModel> childList = malfunctionChildHashMap.get(headerList.get(i).getEventCode());
                        for(int j = 0; j < childList.size() ; j++){
                            EventsList.put(childList.get(j).getId());
                        }
                    }
                }
            }
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



}
