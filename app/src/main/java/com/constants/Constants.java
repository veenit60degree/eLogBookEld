package com.constants;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.background.service.BackgroundLocationService;
import com.ble.util.BleUtil;
import com.driver.details.DriverConst;
import com.local.db.BleGpsAppLaunchMethod;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.MalfunctionDiagnosticMethod;
import com.local.db.OdometerHelperMethod;
import com.local.db.RecapViewMethod;
import com.als.logistic.Globally;
import com.als.logistic.LoginActivity;
import com.als.logistic.R;
import com.als.logistic.UILApplication;
import com.als.logistic.fragment.EldFragment;
import com.models.CanadaDutyStatusModel;
import com.models.EldDataModelNew;
import com.models.MalDiaEventModel;
import com.models.MalfunctionHeaderModel;
import com.models.MalfunctionModel;
import com.models.NotificationHistoryModel;
import com.models.OtherOptionsModel;
import com.models.PrePostModel;
import com.models.RecapSignModel;
import com.models.SlideMenuModel;
import com.models.UnAssignedVehicleModel;
import com.models.UnIdentifiedRecordModel;
import com.models.UnidentifiedEventModel;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.CoNotificationPref;
import com.shared.pref.MainDriverEldPref;
import com.shared.pref.NotificationPref;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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

import kotlin.jvm.internal.Intrinsics;
import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;
import webapi.LocalCalls;

public class Constants {

    public static final String ACTION_START_DOWNLOAD = "com.background.service.ACTION_START_DOWNLOAD";
    public static final String EXTRA_APK_URL = "com.background.service.EXTRA_APK_URL";

    public static final int OBD_PREF_WIFI = 1;
    public static final int OBD_PREF_WIRED = 2;
    public static final int OBD_PREF_BLE = 3;

    public static final int LocationSourceGps = 1;
    public static final int LocationSourceObd = 2;

    public static final int LogEventTypeBle = 1;
    public static final int LogEventTypeGps = 2;

    public static final int EmptyTrailerAnim = 1;
    public static final int OdometerAnim = 2;
    public static final int ExceptionAnim = 3;
    public static final int ConnectionAnim = 4;
    public static final int EditLogAnim = 5;

    // BluetoothConnectionType
    public static final int UNKNOWN = 0;
    public static final int RESETTING = 1;
    public static final int UNSUPPORTED = 2;
    public static final int UNAUTHORIZED = 3;
    public static final int POWERED_OFF = 4;
    public static final int POWERED_ON = 5;
    public static final int OBD_CONNECTED = 6;
    public static final int OBD_DISCONNECT = 7;

    public static int CurrentDriverJob = 1;

    public static final String Auto = "1";
    public static final String Driver = "2";

    // Mal/Dia defination
    public static String ConstLocationMissing = "LM";

    public static String PowerComplianceDiagnostic = "1";
    public static String EngineSyncDiagnosticEvent = "2";
    public static String MissingDataDiagnostic = "3";
    public static String DataTransferDiagnostic = "4";
    public static String UnIdentifiedDrivingDiagnostic = "5";

    public static String PowerComplianceMalfunction = "P";
    public static String EngineSyncMalfunctionEvent = "E";
    public static String DataTransferMalfunction = "S";
    public static String PositionComplianceMalfunction = "L";
    public static String DataRecordingComplianceMalfunction = "R";
    public static String TimingComplianceMalfunction = "T";

    public static int UnidentifiedDiagnosticTime = 30;   // 30 min
    public static int PowerEngSyncMalOccTime = 30;   // 30 min
    public static int PositioningMalOccTime = 60;   // 60 min

    public static int TotalMinInADay = 1440;

    public static boolean IsUnidentifiedLocMissing = false;
    public static boolean IsLogEdited = false;
    public static boolean IsAlreadyViolation = false;
    public static boolean IsHomePageOnCreate;
    public static boolean IsInspectionDetailViewBack = false;
    public static boolean isLocationUpdated = false;
    // public static boolean isLogEdited = false;

    public static String SelectedDatePti = "";
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
    public static String packageName = "com.als.logistic";
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

    public static String AlertSettings = "AlertSettings";
    public static String AlertUnidentified = "AlertUnidentified";

    public static String apiReturnedSpeed = "apiReturnedSpeed";
    public static String obdSource = "obdSource";
    public static String Source = "Source";
    public static String obdOdometer = "Odometer";
    public static String EventData = "EventData";
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


    public static int NoLocation            = 0;
    public static int LocationPrecise       = 1;
    public static int LocationApproximate   = 2;

    public static final int MAIN_DRIVER_TYPE = 0;
    public static final int CO_DRIVER_TYPE = 1;

    // login/Logout/Certification/Malfunction DOT events
    public static int LogoutLoginEventDot = 5;
    public static int MalfunctionEventDot = 7;
    public static int CertificationEventDot = 4;

    public static int ConnectionMalfunction = 0;
    public static int ConnectionWired = 1;
    public static int ConnectionWifi = 2;
    public static int ConnectionBluetooth = 3;
    public static int ConnectionApi = 4;
    public static int ConnectionOffline = 5;
    public static int CertifyLog = 101010;      // set this value to differentiate where we go on certify screen

    public static double AgricultureDistanceInMiles = 172.61;

    public static final int WIRED_CONNECTED = 1001;
    public static final int WIRED_DISCONNECTED = 1002;
    public static final int WIRED_ERROR = 1006;
    public static final int WIRED_IGNITION_OFF = 1007;

    public static final int WIFI_CONNECTED = 1003;
    public static final int WIFI_DISCONNECTED = 1004;
    public static final int NO_CONNECTION = 1005;
    public static final int WIFI_IGNITION_OFF = 1008;

    public static final int BLE_CONNECTED = 1009;
    public static final int BLE_DISCONNECTED = 1010;


    public static final int NOTIFICATION = 0;
    public static final int GPS = 1;
    public static final int MALFUNCTION = 2;
    public static final int UNIDENTIFIED = 3;
    public static final int SUGGESTED_LOGS = 4;
    public static final int OBD = 5;
    public static final int MISSING_LOCATION = 6;
    public static final int UN_CERTIFY_LOG = 7;
    public static final int VIN = 8;

    public static String TruckIgnitionStatus = "TruckIgnitionStatus";
    public static String IgnitionSource = "IgnitionSource";
    public static String LastIgnitionTime = "LastIgnitionTime";


    public static String TruckIgnitionStatusMalDia = "TruckIgnitionStatusMalDia";
    public static String IgnitionSourceMalDia = "IgnitionSourceMalDia";
    public static String IgnitionTimeMalDia = "LastIgnitionTimeMalDia";
    public static String IgnitionUtcTimeMalDia = "LastIgnitionUtcTimeMalDia";
    public static String EngineHourMalDia = "EngineHourMalDia";
    public static String OdometerMalDia = "OdometerMalDia";

    public static String CONNECTION_TYPE = "connection_type";
    public static String LAST_SAVED_TIME = "last_saved_time";
    public static String DATA_USAGE_TIME = "data_usage_time";
    public static String ServiceRunningTime = "ServiceRunningTime";


    public static String ViolationReason30Min = "30 MIN BREAK VIOLATION";

    String DeviceName = "Android";


    public static int ELDActivityLaunchCount = 0;
    public static boolean IS_ACTIVE_ELD = false;
    public static boolean IS_ACTIVE_HOS = false;
    public static boolean IS_HOS_AUTO_CALLED = false;
    public static boolean IS_NOTIFICATION = false;
    public static boolean IS_SCREENSHOT = false;
    public static boolean IS_ELD_ON_CREATE = true;
    public static boolean IS_TRAILER_INSPECT = false;
    public static boolean IsEdiLogBackStack = false;
    public static boolean IsCtPatUploading = false;
    public static boolean IsAlsServerResponding = true;
    public static boolean isClaim = false;
    public static boolean isCallMalDiaEvent = false;
    public static boolean isClearMissingCompEvent = false;
    public static boolean isPcYmAlertButtonClicked = false;


    public static boolean isLogoutEvent = false;
    public static boolean isStorageMalfunctionEvent = false;
    public static boolean isClearStorageMalEvent = false;
    public static boolean isDriverSwitchEvent = false;
    public static boolean isDriverSwitchEventForHome = false;
    public static boolean isClearMissingEvent = false;

    public static String lastDriverId = "0";
    public static String ClearMissingEventTime = "";
    public static String ClearMissingEventStatus = "";

    public static int OFF_DUTY = 1;
    public static int SLEEPER = 2;
    public static int DRIVING = 3;
    public static int ON_DUTY = 4;
    public static int PERSONAL = 1;
    public static int INTERMEDIATE = 2;

    final int EngineUp = 1;
    final int EngineDown = 3;
    final int Login = 1;
    final int Logout = 2;
    final int MalfunctionActive = 1;
    final int MalfunctionCleared = 2;
    final int DiagnosticActive = 3;
    final int DiagnisticCleared = 4;

    public static int WIRED_OBD = 1001;
    public static int WIFI_OBD = 1002;
    public static int BLE_OBD = 1006;
    public static int API = 1003;
    public static int OFFLINE = 1004;
    public static int OTHER_SOURCE = 1005;

    // EventCode Type Inside the list
    public static int Malfunction = 1;    // EventCode value is an String in main array
    public static int Diagnostic = 3;    // EventCode value is an Integer in main array
    public static int clearEvent = 2;


    public static int EditRemarks = 101;
    public static int EditLocation = 102;
    public static int SocketTimeout300ms = 300;   // 300 milli second
    public static int SocketTimeout500ms = 500;   // 500 milli second
    public static int SocketTimeout800ms = 800;   // 500 milli second
    public static int SocketTimeout1Sec = 1000;   // 1 second
    public static int SocketTimeout2Sec = 2000;   // 2 second
    public static int SocketTimeout3Sec = 3000;   // 3 seconds
    public static int SocketTimeout4Sec = 4000;   // 3 seconds
    public static int SocketTimeout5Sec = 5000;   // 5 seconds
    public static int SocketTimeout6Sec = 6000;   // 6 seconds
    public static int SocketTimeout8Sec = 8000;   // 8 seconds
    public static int SocketTimeout10Sec = 10000;   // 10 seconds
    public static int SocketTimeout15Sec = 15000;   // 15 seconds
    public static int SocketTimeout20Sec = 20000;   // 20 seconds
    public static int SocketTimeout25Sec = 25000;   // 25 seconds
    public static int SocketTimeout30Sec = 30000;   // 30 seconds
    public static int SocketTimeout40Sec = 40000;   // 30 seconds
    public static int SocketTimeout50Sec = 50000;   // 50 seconds
    public static int SocketTimeout60Sec = 60000;   // 60 seconds

    public static int SocketTimeout70Sec = 70000;   // 70 seconds
    public static int SocketTimeout80Sec = 80000;   // 80 seconds
    public static int SocketTimeout90Sec = 90000;   // 90 seconds
    public static int SocketTimeout100Sec = 100000;   // 100 seconds
    public static int SocketTimeout110Sec = 110000;   // 110 seconds
    public static int SocketTimeout120Sec = 120000;   // 120 seconds

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

    public static final int ELD_HOS = 0;
    public static final int PTI_INSPECTION = 4;
    public static final int CT_PAT_INSPECTION = 8;
    public static final int ODOMETER_READING = 5;
    public static final int NOTIFICATION_HISTORY = 3;
    public static final int SHIPPING_DOC = 7;
    public static final int ELD_DOC = 10;
    public static final int UNIDENTIFIED_RECORD = 11;
    public static final int DATA_MALFUNCTION = 12;
    public static final int SETTINGS = 1;
    public static final int ALS_SUPPORT = 6;
    public static final int ALS_TERMS_COND = 14;
    public static final int LOGOUT = 2;
    public static final int VERSION = 13;


    public MalDiaEventModel getMalDiaEventDetails(Context context, String EventCode) {
        MalDiaEventModel eventModel;
        if (EventCode.equals(PositionComplianceMalfunction)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.pos_mal_title), context.getString(R.string.pos_mal_def));
        } else if (EventCode.equals(MissingDataDiagnostic)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.missing_data_dia_title), context.getString(R.string.missing_data_dia_def));
        } else if (EventCode.equals(PowerComplianceMalfunction)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.power_mal_title), context.getString(R.string.power_mal_def));
        } else if (EventCode.equals(DataRecordingComplianceMalfunction)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.data_rec_mal_title), context.getString(R.string.data_rec_mal_def));
        } else if (EventCode.equals(PowerComplianceDiagnostic)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.power_dia_title), context.getString(R.string.power_dia_def));
        } else if (EventCode.equals(EngineSyncDiagnosticEvent)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.eng_sync_dia_title), context.getString(R.string.eng_sync_dia_def));
        } else if (EventCode.equals(EngineSyncMalfunctionEvent)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.eng_sync_mal_title), context.getString(R.string.eng_sync_mal_def));
        } else if (EventCode.equals(UnIdentifiedDrivingDiagnostic)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.unidentified_title), context.getString(R.string.unidentified_def));
        } else if (EventCode.equals(DataTransferDiagnostic)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.data_transfer_dia_title), context.getString(R.string.data_transfer_dia_def));
        } else if (EventCode.equals(DataTransferMalfunction)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.data_transfer_mal_title), context.getString(R.string.data_transfer_mal_def));
        } else if (EventCode.equals(TimingComplianceMalfunction)) {
            eventModel = new MalDiaEventModel(context.getString(R.string.timing_comp_mal_event), context.getString(R.string.timing_comp_mal_desc));
        } else {
            eventModel = new MalDiaEventModel(context.getString(R.string.other_dia_title), context.getString(R.string.other_dia_def));
        }
        return eventModel;
    }


    public List<SlideMenuModel> getSlideMenuList(Context context, boolean isOdometerFromObd, boolean isunIdentified, boolean isMalfunction, String version) {
        List<SlideMenuModel> list = new ArrayList<>();
        if (SharedPref.IsAOBRD(context)) {
            list.add(new SlideMenuModel(ELD_HOS, R.drawable.eld_home, context.getResources().getString(R.string.aobrd_hos)));
        } else {
            list.add(new SlideMenuModel(ELD_HOS, R.drawable.eld_home, context.getResources().getString(R.string.eld_hos)));
        }
        list.add(new SlideMenuModel(PTI_INSPECTION, R.drawable.pre_post_inspection, context.getResources().getString(R.string.prePostTripIns)));
        list.add(new SlideMenuModel(CT_PAT_INSPECTION, R.drawable.ct_pat_inspection, context.getResources().getString(R.string.ctPat)));
        if (isOdometerFromObd == false) {
            list.add(new SlideMenuModel(ODOMETER_READING, R.drawable.odometer_menu, context.getResources().getString(R.string.odometerReading)));
        }
        list.add(new SlideMenuModel(NOTIFICATION_HISTORY, R.drawable.notification_history, context.getResources().getString(R.string.noti_history)));
        list.add(new SlideMenuModel(SHIPPING_DOC, R.drawable.shipping_docs, context.getResources().getString(R.string.ShippingDocNumber)));
        list.add(new SlideMenuModel(ELD_DOC, R.drawable.eld_docs, context.getResources().getString(R.string.eld_documents)));
        if (isunIdentified) {
            list.add(new SlideMenuModel(UNIDENTIFIED_RECORD, R.drawable.unidentified_menu, context.getResources().getString(R.string.unIdentified_records)));
        }
        if (isMalfunction) {
            list.add(new SlideMenuModel(DATA_MALFUNCTION, R.drawable.eld_malfunction, context.getResources().getString(R.string.malf_and_diagnostic)));
        }
        list.add(new SlideMenuModel(SETTINGS, R.drawable.settings, context.getResources().getString(R.string.action_settings)));
        list.add(new SlideMenuModel(ALS_SUPPORT, R.drawable.als_support, context.getResources().getString(R.string.action_Support)));
        list.add(new SlideMenuModel(ALS_TERMS_COND, R.drawable.terms_conditions, context.getResources().getString(R.string.terms_conditions)));
        list.add(new SlideMenuModel(LOGOUT, R.drawable.logout, context.getResources().getString(R.string.logout)));
        list.add(new SlideMenuModel(VERSION, R.drawable.transparent, version));

        return list;
    }


    public List<OtherOptionsModel> getOtherOptionsList(Context context, boolean isAllowMalfunction, boolean isAllowUnIdentified,
                                                       boolean isMissingLoc, boolean isUnCertify) {
        List<OtherOptionsModel> optionsList = new ArrayList<>();
        optionsList.add(new OtherOptionsModel(R.drawable.notifications_other, NOTIFICATION, context.getResources().getString(R.string.notification)));
        optionsList.add(new OtherOptionsModel(R.drawable.gps_other, GPS, context.getResources().getString(R.string.gps)));

        if (isAllowMalfunction)
            optionsList.add(new OtherOptionsModel(R.drawable.malfunction_other, MALFUNCTION, context.getResources().getString(R.string.malf_and_dia)));

        if (isAllowUnIdentified)
            optionsList.add(new OtherOptionsModel(R.drawable.unidentified_other, UNIDENTIFIED, context.getResources().getString(R.string.unIdentified_records)));

        if (SharedPref.IsCCMTACertified(context))
            optionsList.add(new OtherOptionsModel(R.drawable.edit_log_icon, SUGGESTED_LOGS, context.getResources().getString(R.string.suggested_logs)));

        if (isMissingLoc)
            optionsList.add(new OtherOptionsModel(R.drawable.loc_missing, MISSING_LOCATION, context.getResources().getString(R.string.MissingLoc)));

        if (isUnCertify)
            optionsList.add(new OtherOptionsModel(R.drawable.uncertified, UN_CERTIFY_LOG, context.getResources().getString(R.string.uncertify_log)));


        if (SharedPref.getObdStatus(context) == Constants.WIFI_CONNECTED) {
            optionsList.add(new OtherOptionsModel(R.drawable.wifi_other, OBD, context.getResources().getString(R.string.obd_wifi)));
        } else if (SharedPref.getObdStatus(context) == Constants.WIRED_CONNECTED) {
            optionsList.add(new OtherOptionsModel(R.drawable.wired_status_inactive, OBD, context.getResources().getString(R.string.wired_tablet_connected)));
        } else if (SharedPref.getObdStatus(context) == Constants.BLE_CONNECTED) {
            optionsList.add(new OtherOptionsModel(R.drawable.ble_ic, OBD, context.getResources().getString(R.string.obd_ble)));
        } else {
            optionsList.add(new OtherOptionsModel(R.drawable.eld_malfunction, OBD, context.getResources().getString(R.string.obd_not_connected)));
        }

        if (isValidVinFromObd(SharedPref.getIgnitionStatus(context), context)) {
            optionsList.add(new OtherOptionsModel(R.drawable.vin_obd, VIN, context.getResources().getString(R.string.VinMatched)));
        } else {
            optionsList.add(new OtherOptionsModel(R.drawable.vin_obd, VIN, context.getResources().getString(R.string.VinMismatched)));
        }

        return optionsList;
    }


    public void SaveEldJsonToList(EldDataModelNew ListModel, JSONArray jsonArray, Context context) throws JSONException {

        JSONObject locationObj = new JSONObject();

        locationObj.put(ConstantsKeys.ProjectId, ListModel.getProjectId());
        locationObj.put(ConstantsKeys.DriverId, ListModel.getDriverId());
        locationObj.put(ConstantsKeys.DriverStatusId, ListModel.getDriverStatusId());
        locationObj.put(ConstantsKeys.DriverLogId, ListModel.getDriverLogId());

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
        //locationObj.put(ConstantsKeys.UnassignedVehicleMilesId, ListModel.getUnassignedVehicleMilesId());
        locationObj.put(ConstantsKeys.isNewRecord, ListModel.getNewRecordStatus());
        locationObj.put(ConstantsKeys.IsCycleChanged, ListModel.IsCycleChanged());
        locationObj.put(ConstantsKeys.UnAssignedVehicleMilesId, ListModel.getUnAssignedVehicleMilesId());

        locationObj.put(ConstantsKeys.CoDriverId, ListModel.getCoDriverId());
        locationObj.put(ConstantsKeys.CoDriverName, ListModel.getCoDriverName());
        locationObj.put(ConstantsKeys.IsSkipRecord, ListModel.getIsSkipRecord());
        locationObj.put(ConstantsKeys.LocationSource, ListModel.getLocationSource());

        locationObj.put(ConstantsKeys.EngineHours, ListModel.getEngineHour());
        locationObj.put(ConstantsKeys.Odometer, ListModel.getOdometer());
        locationObj.put(ConstantsKeys.DriverVehicleTypeId, ListModel.getDriverVehicleTypeId());

        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            if (SharedPref.IsMainDriverEdited(context)) {
                locationObj.put(ConstantsKeys.AppVersionCode, Globally.GetAppVersion(context, "VersionCode"));
            }
        } else {
            if (SharedPref.IsCoDriverEditedData(context)) {
                locationObj.put(ConstantsKeys.AppVersionCode, Globally.GetAppVersion(context, "VersionCode"));
            }
        }
        locationObj.put(ConstantsKeys.IsHosLoggingRule, ListModel.isLoggingRule());

        jsonArray.put(locationObj);
    }


    public JSONObject GetJsonFromList(JSONArray jsonArray, int pos) throws JSONException {

        JSONObject obj = (JSONObject) jsonArray.get(pos);
        JSONObject locationObj = new JSONObject();

        String isViolation = obj.getString(ConstantsKeys.IsViolation).trim();

        if (!isViolation.equalsIgnoreCase("true") && !isViolation.equalsIgnoreCase("false")) {
            isViolation = "false";
        }

        String IsStatusAutomatic = checkStringBoolInJsonObj(obj, ConstantsKeys.IsStatusAutomatic);
        String OBDSpeed = checkStringInJsonObj(obj, ConstantsKeys.OBDSpeed);
        String GPSSpeed = checkStringInJsonObj(obj, ConstantsKeys.GPSSpeed);
        String PlateNumber = checkStringInJsonObj(obj, ConstantsKeys.PlateNumber);
        String HaulHourException = checkStringBoolInJsonObj(obj, ConstantsKeys.IsShortHaulException);
        String decesionSource = checkStringInJsonObj(obj, ConstantsKeys.DecesionSource);
        String TruckNumber = checkStringInJsonObj(obj, ConstantsKeys.TruckNumber);
        String isAdverseException = checkStringBoolInJsonObj(obj, ConstantsKeys.IsAdverseException);
        String adverseExceptionRemark = checkStringInJsonObj(obj, ConstantsKeys.AdverseExceptionRemarks);
        String LocationType = checkStringInJsonObj(obj, ConstantsKeys.LocationType);
        String IsNorthCanada = checkStringBoolInJsonObj(obj, ConstantsKeys.IsNorthCanada);


        String DrivingStartTime = checkStringInJsonObj(obj, ConstantsKeys.DrivingStartTime);
        String IsAOBRD = checkStringBoolInJsonObj(obj, ConstantsKeys.IsAOBRD);
        String CurrentCycleId = checkStringInJsonObj(obj, ConstantsKeys.CurrentCycleId);
        String isDeferral = checkStringBoolInJsonObj(obj, ConstantsKeys.isDeferral);
        String IsCycleChanged = checkStringBoolInJsonObj(obj, ConstantsKeys.IsCycleChanged);

        String CoDriverId = checkStringInJsonObj(obj, ConstantsKeys.CoDriverId);
        String CoDriverName = checkStringInJsonObj(obj, ConstantsKeys.CoDriverName);
        String IsSkipRecord = checkStringBoolInJsonObj(obj, ConstantsKeys.IsSkipRecord);
        String UnAssignedVehicleMilesId = checkIntInJsonObj(obj, ConstantsKeys.UnAssignedVehicleMilesId);

        String EngHour = checkStringInJsonObj(obj, ConstantsKeys.EngineHours);
        String odometer = checkStringInJsonObj(obj, ConstantsKeys.Odometer);

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
        locationObj.put(ConstantsKeys.DecesionSource, decesionSource);

        locationObj.put(ConstantsKeys.IsAdverseException, isAdverseException);
        locationObj.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);
        locationObj.put(ConstantsKeys.LocationType, LocationType);
        locationObj.put(ConstantsKeys.IsNorthCanada, IsNorthCanada);

        locationObj.put(ConstantsKeys.DrivingStartTime, DrivingStartTime);
        locationObj.put(ConstantsKeys.IsAOBRD, IsAOBRD);
        locationObj.put(ConstantsKeys.CurrentCycleId, CurrentCycleId);
        locationObj.put(ConstantsKeys.isDeferral, isDeferral);
        locationObj.put(ConstantsKeys.IsCycleChanged, IsCycleChanged);
        locationObj.put(ConstantsKeys.UnAssignedVehicleMilesId, UnAssignedVehicleMilesId);

        locationObj.put(ConstantsKeys.CoDriverId, CoDriverId);
        locationObj.put(ConstantsKeys.CoDriverName, CoDriverName);
        locationObj.put(ConstantsKeys.IsSkipRecord, IsSkipRecord);

        locationObj.put(ConstantsKeys.EngineHours, EngHour);
        locationObj.put(ConstantsKeys.Odometer, odometer);

        return locationObj;
    }


    public List<EldDataModelNew> getLogInList(JSONArray logArray){

        List <EldDataModelNew> logList = new ArrayList<EldDataModelNew>();

        for(int i = 0; i < logArray.length() ; i++){
            try {
                String editLogReason = "", LocationType = "";
                JSONObject obj = (JSONObject)logArray.get(i);
                String IsStatusAutomatic = "false", OBDSpeed = "0", GPSSpeed = "0", TruckNumber = "",
                        DecesionSource = "", PlateNumber = "", isHaulException = "false", IsShortHaulUpdate = "false";
                String isAdverseException = "false", adverseExceptionRemark = "", IsNorthCanada = "false", IsCycleChanged = "false";
                int LocationSource = -1;

                if(obj.has(ConstantsKeys.IsStatusAutomatic)){
                    IsStatusAutomatic = obj.getString(ConstantsKeys.IsStatusAutomatic);
                }

                if(obj.has(ConstantsKeys.OBDSpeed)){
                    OBDSpeed = obj.getString(ConstantsKeys.OBDSpeed);
                }

                if(obj.has(ConstantsKeys.GPSSpeed)){
                    GPSSpeed = obj.getString(ConstantsKeys.GPSSpeed);
                }

                if(obj.has(ConstantsKeys.PlateNumber)){
                    PlateNumber = obj.getString(ConstantsKeys.PlateNumber);
                }

                if(obj.has(ConstantsKeys.IsShortHaulException)){
                    isHaulException = obj.getString(ConstantsKeys.IsShortHaulException);
                }

                if(obj.has(ConstantsKeys.IsShortHaulUpdate)){
                    IsShortHaulUpdate = obj.getString(ConstantsKeys.IsShortHaulUpdate);
                }


                if(obj.has(ConstantsKeys.DecesionSource)){
                    DecesionSource = obj.getString(ConstantsKeys.DecesionSource);
                }

                if(obj.has(ConstantsKeys.Truck)){
                    TruckNumber = obj.getString(ConstantsKeys.Truck);
                }

                if (obj.has(ConstantsKeys.IsAdverseException )) {
                    isAdverseException = obj.getString(ConstantsKeys.IsAdverseException );
                }
                if (obj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                    adverseExceptionRemark = obj.getString(ConstantsKeys.AdverseExceptionRemarks);
                }

                if (obj.has(ConstantsKeys.EditedReason)) {
                    editLogReason = obj.getString(ConstantsKeys.EditedReason);
                }

                if (obj.has(ConstantsKeys.LocationType)) {
                    LocationType = obj.getString(ConstantsKeys.LocationType);
                }

                if (obj.has(ConstantsKeys.IsNorthCanada)) {
                    IsNorthCanada = obj.getString(ConstantsKeys.IsNorthCanada);
                }

                if (obj.has(ConstantsKeys.IsCycleChanged)) {
                    IsCycleChanged = obj.getString(ConstantsKeys.IsCycleChanged);
                }

                if (obj.has(ConstantsKeys.LocationSource)) {
                    LocationSource = obj.getInt(ConstantsKeys.LocationSource);
                }



                String DrivingStartTime = "", IsAOBRD = "false", CurrentCycleId = "", isDeferral = "false";
                String isNewRecord = "false";
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

                if (obj.has(ConstantsKeys.isNewRecord)) {
                    isNewRecord = obj.getString(ConstantsKeys.isNewRecord);
                }

                String UnAssignedVehicleMilesId = "0";
                if (obj.has(ConstantsKeys.UnAssignedVehicleMilesId) && !obj.getString(ConstantsKeys.UnAssignedVehicleMilesId).equals("null")) {
                    UnAssignedVehicleMilesId = obj.getString(ConstantsKeys.UnAssignedVehicleMilesId);
                }

                String EngHour = "";
                if (obj.has(ConstantsKeys.EngineHours) && !obj.getString(ConstantsKeys.EngineHours).equals("null")) {
                    EngHour = obj.getString(ConstantsKeys.EngineHours);
                }

                String odometer = "";
                if (obj.has(ConstantsKeys.Odometer) && !obj.getString(ConstantsKeys.Odometer).equals("null")) {
                    odometer = obj.getString(ConstantsKeys.Odometer);
                }

                String DriverVehicleTypeId = Constants.Driver;
                if (obj.has(ConstantsKeys.DriverVehicleTypeId) && !obj.getString(ConstantsKeys.DriverVehicleTypeId).equals("null")) {
                    DriverVehicleTypeId = obj.getString(ConstantsKeys.DriverVehicleTypeId);
                }

                String isHosLogging = "false";
                if (obj.has(ConstantsKeys.IsHosLoggingRule) && !obj.getString(ConstantsKeys.IsHosLoggingRule).equals("null")) {
                    DriverVehicleTypeId = obj.getString(ConstantsKeys.IsHosLoggingRule);
                }

                EldDataModelNew logModel = new EldDataModelNew(
                        obj.getString(ConstantsKeys.ProjectId),
                        obj.getString(ConstantsKeys.DriverId),
                        obj.getString(ConstantsKeys.DriverStatusId),
                        obj.getString(ConstantsKeys.DriverLogId),

                        obj.getString(ConstantsKeys.IsYardMove),
                        obj.getString(ConstantsKeys.IsPersonal),
                        obj.getString(ConstantsKeys.DeviceID),

                        obj.getString(ConstantsKeys.Remarks),
                        obj.getString(ConstantsKeys.UTCDateTime),
                        TruckNumber,
                        obj.getString(ConstantsKeys.TrailorNumber),
                        obj.getString(ConstantsKeys.CompanyId),
                        obj.getString(ConstantsKeys.DriverName),

                        obj.getString(ConstantsKeys.City),
                        obj.getString(ConstantsKeys.State),
                        obj.getString(ConstantsKeys.Country),
                        obj.getString(ConstantsKeys.IsViolation),
                        obj.getString(ConstantsKeys.ViolationReason),
                        obj.getString(ConstantsKeys.Latitude),
                        obj.getString(ConstantsKeys.Longitude),
                        IsStatusAutomatic,
                        OBDSpeed,
                        GPSSpeed,
                        PlateNumber,

                        isHaulException,
                        IsShortHaulUpdate,

                        DecesionSource,
                        isAdverseException,
                        adverseExceptionRemark,
                        editLogReason,
                        LocationType,
                        IsNorthCanada,
                        DrivingStartTime,
                        IsAOBRD,
                        CurrentCycleId,
                        isDeferral,
                        "",
                        isNewRecord,
                        IsCycleChanged,
                        UnAssignedVehicleMilesId,
                        obj.getString(ConstantsKeys.CoDriverId),
                        obj.getString(ConstantsKeys.CoDriverName),
                        "false", LocationSource, EngHour,
                        odometer, DriverVehicleTypeId,
                        isHosLogging

                );

                logList.add(logModel);
            }catch (Exception e){
                e.printStackTrace();
            }
        }



        return logList;
    }




    public static String checkStringInJsonObj(JSONObject obj, String key) {
        String value = "";
        try {
            if (obj.has(key) && !obj.getString(key).equals("null")) {
                value = obj.getString(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String checkStringBoolInJsonObj(JSONObject obj, String key) {
        String value = "false";
        try {
            if (obj.has(key)) {
                value = obj.getString(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String checkIntInJsonObj(JSONObject obj, String key) {
        String value = "0";
        try {
            if (obj.has(key) && !obj.getString(key).equals("null")) {
                value = obj.getString(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    public static boolean isValidVinFromObd(String ignitionStatus, Context context) {
        String VINNumberFromApi = SharedPref.getVINNumber(context);
        String obdReceivedVin = SharedPref.getVehicleVin(context);

        try {
            if (obdReceivedVin.contains("u0000")) {
                byte[] bytes = obdReceivedVin.getBytes("UTF-8");
                String convertedVin = new String(bytes, "UTF-8");
                if (convertedVin.length() > 20) {
                    obdReceivedVin = "";
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        boolean isObdConnected = false;
        int ObdStatus = SharedPref.getObdStatus(context);
        if (ObdStatus == WIFI_CONNECTED ||
                ObdStatus == WIRED_CONNECTED ||
                ObdStatus == BLE_CONNECTED) {
            isObdConnected = true;
        }

        if (isObdConnected) {
            if (obdReceivedVin.length() < 8 || obdReceivedVin.equalsIgnoreCase(VINNumberFromApi) ||
                    !ignitionStatus.equalsIgnoreCase("ON")) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    public boolean isObdVinValid(String obdReceivedVin) {
        boolean isValidVin = false;
        try {
            if (!obdReceivedVin.contains("u0000") && obdReceivedVin.length() > 8) {
                isValidVin = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValidVin;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;
    }

    public String CheckNullBString(String inputValue) {
        try {
            if (inputValue.trim().equals("null")) {
                inputValue = "--";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputValue;
    }


    public String CheckDateFormat(String inputValue) {
        try {
            if (inputValue.trim().length() > 19) {
                inputValue = inputValue.substring(0, 19);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputValue;
    }

    public boolean CheckNullBoolean(String inputValue) {
        boolean output = false;
        try {
            if (!inputValue.trim().equals("null")) {
                output = Boolean.parseBoolean(inputValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
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


    public void checkWiredObdConnectionWithOdometer(String DriverId, String DeviceId, String DriverStatusId, DBHelper dbHelper,
                                                    OdometerHelperMethod odometerhMethod, Context context) {
        if (SharedPref.GetTruckIgnitionStatusForContinue(Constants.IgnitionSource, context).equals(Constants.WiredOBD)) {
            DateTime lastRecordSavedTime = Globally.getDateTimeObj(SharedPref.GetTruckIgnitionStatusForContinue(Constants.LastIgnitionTime,
                    context), false);
            DateTime currentDate = Globally.getDateTimeObj(Globally.GetDriverCurrentDateTime(new Globally(), context), false);

            int dayDiff = Days.daysBetween(lastRecordSavedTime.toLocalDate(), currentDate.toLocalDate()).getDays();

            if (dayDiff == 0) {
                long minDiff = getDateTimeDuration(lastRecordSavedTime, currentDate).getStandardMinutes();
                if (minDiff < 5) {
                    String ObdOdometer = SharedPref.getObdOdometer(context);

                    odometerhMethod.AddOdometerAutomatically(DriverId, DeviceId, ObdOdometer, DriverStatusId, dbHelper, context);

                }
            }

        }
    }


/*    public static void SaveTripDetails(int DriverType, String truck, String VIN_NUMBER, Context context) {

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

        }

    }*/


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

                Logger.LogDebug("correctData", "correctData: " + correctData);

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

            //  Logger.LogDebug("origin", "origin lat: " + originLat + ", " +originLon);
            //   Logger.LogDebug("dest", "dest Lat: " + destLat + ", " + destLon);


            DateTime originDate, destDate;
            originDate = Globally.getDateTimeObj(originDateStr, false);
            destDate = Globally.getDateTimeObj(destDateStr, false);

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
        Logger.LogDebug("speed", "speed: " + speed);

        return speed;
    }


    String setStringLength(String str) {

        if (str.length() >= 9) {
            //- str = str.substring(0, 8);
        }

        return str;
    }


    public String calculateLocalOdometersDistance(Context context) {

        String distance = "";
        try {

            String dayStartSavedDate = SharedPref.getDayStartSavedTime(context);
            String dayStartOdometerStr = SharedPref.getDayStartOdometerKm(context);
            String currentOdometerStr = SharedPref.getObdOdometer(context);

            if (dayStartSavedDate.length() > 0) {
                int dayDiff = getDayDiff(dayStartSavedDate, Globally.GetDriverCurrentDateTime(new Globally(), context));
                if (dayDiff == 0) {

                    double currentOdoInMiles = Double.parseDouble(meterToKmWithObd(currentOdometerStr)); //meterToMiles(Double.parseDouble(currentOdometerStr));
                    double dayStartOdoInMiles = Double.parseDouble(dayStartOdometerStr);  // ALREADY SAVED IN MILES
                    double distanceInMiles = currentOdoInMiles - dayStartOdoInMiles;

                    String kmDiff = getBeforeDecimalValue(Convert2DecimalPlacesDouble(distanceInMiles));
                    String currKM = getBeforeDecimalValue(meterToKmWithObd(currentOdometerStr));  //Convert2DecimalPlacesDouble(currentOdoInMiles)
                    String dayStartKm = getBeforeDecimalValue(dayStartOdometerStr);   //Convert2DecimalPlacesDouble(dayStartOdoInMiles)

                    distance = "(" + dayStartKm + " - " + currKM + ") = <b>" + kmDiff + " Km </b>";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return distance;
    }


    public String getBeforeDecimalValue(String value) {
        try {
            String[] distanceArray = value.split("\\.");
            if (distanceArray.length > 0) {
                value = distanceArray[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public String getOdometersDistance(Context context) {

        String distance = "0";
        try {

            String dayStartSavedDate = SharedPref.getDayStartSavedTime(context);
            String dayStartOdometerMilesStr = SharedPref.getDayStartOdometerMiles(context);
            String currentOdometerMilesStr = SharedPref.getObdOdometerInMiles(context);   //"1179791980";

            if (dayStartSavedDate.length() > 0) {
                if (dayStartOdometerMilesStr.length() > 0 && !dayStartOdometerMilesStr.equals("null") &&
                        currentOdometerMilesStr.length() > 0 && !currentOdometerMilesStr.equals("null")) {
                    double currentOdometer = Double.parseDouble(currentOdometerMilesStr);
                    double dayStartOdometer = Double.parseDouble(dayStartOdometerMilesStr);

                    double distanceInMiles ;
                    if(dayStartOdometer > currentOdometer){
                        if(currentOdometer == 0){
                            distanceInMiles = currentOdometer;
                        }else{
                            distanceInMiles = dayStartOdometer - currentOdometer;
                        }

                    }else{
                        distanceInMiles = currentOdometer - dayStartOdometer;
                    }

                    distance = getBeforeDecimalValue(String.valueOf(distanceInMiles)) + " Miles";


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return distance;
    }



    private boolean isTimeZoneAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), Settings.System.AUTO_TIME_ZONE, 0) == 1;
        }
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
                                DriverJsonArray,
                                context
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
                                DriverJsonArray,
                                context
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

    public static String milesToMeter(String odometer) {

        try {
            double odometerInMiles = Double.parseDouble(odometer);
            odometer = BigDecimal.valueOf(odometerInMiles * 1609.344).toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (odometer.contains(".")) {
            String[] array = odometer.split("\\.");
            odometer = array[0];
        }

        return odometer;
    }


    public static String kmToMiles(String odometer) {
        // double miles=distanceInKm/1.609;
        try {
            double km = Double.parseDouble(odometer);
            odometer = Convert2DecimalPlacesDouble(km * 0.621371);
            if (isExponentialString(odometer)) {
                odometer = BigDecimal.valueOf(Double.parseDouble(odometer)).toPlainString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return odometer;
    }


    public static String kmToMeter(String odometerInKm) {
        try {
            double km = Double.parseDouble(odometerInKm);
            odometerInKm = "" + (km * 1000);

            if (isExponentialString(odometerInKm)) {
                odometerInKm = BigDecimal.valueOf(Double.parseDouble(odometerInKm)).toPlainString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return odometerInKm;
    }

    public static String kmToMeter1(String odometerInKm) {
        try {
            if (odometerInKm.length() > 0) {
                double km = Double.parseDouble(odometerInKm) * 1000;
                //odometerInKm = "" + (km * 1000);
                odometerInKm = BigDecimal.valueOf(km).toPlainString();

                String[] array = odometerInKm.split("\\.");
                if (array.length > 1) {
                    if (array[1].equals("0")) {
                        odometerInKm = array[0];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return odometerInKm;
    }


    public static String getUpTo2DecimalString(String value) {

        try {
            if (value.contains(".")) {
                String[] array = value.split("\\.");
                value = array[0];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (value.equals("null")) {
            value = "0";
        }
        return value;
    }


    public static String meterToMilesWith2DecPlaces(String distance) {
        String miles = distance;
        try {
            if (distance.length() > 0 && !distance.equals("null")) {
                double meter = Double.parseDouble(distance);
                double meters2miles = 1609.344;
                meter = (meter / meters2miles);
                miles = Convert2DecimalPlacesDouble(meter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return miles;
    }



    public boolean isExemptDriver(Context context) {
        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            return SharedPref.IsExemptDriverMain(context);
        } else {
            return SharedPref.IsExemptDriverCo(context);
        }
    }


    public static String meterToKmWith0DecPlaces(String odometer) {
        try {
            if (odometer.length() > 0 && !odometer.equals("null")) {
                double meter = Double.parseDouble(odometer);
                meter = meter * 0.001;
                odometer = Convert0DecimalPlacesDouble(meter);
                // odometer = ""+ meter ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return odometer;
    }

    public static String meterToKmWithObd(String odometer) {
        try {
            if (odometer.length() > 0 && !odometer.equals("null")) {
                double meter = Double.parseDouble(odometer);
                odometer = String.valueOf(meter * 0.001);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return odometer;
    }


    public static int getLocationSource(Context context) {

        if (SharedPref.IsLocReceivedFromObd(context)) {
            return LocationSourceObd;
        } else {
            return LocationSourceGps;
        }
    }


    public static int getDayDiff(String savedDate, String currentDate) {
        int dayDiff = -1;
        try {
            DateTime savedDateTime = Globally.getDateTimeObj(savedDate, false);
            DateTime currentDateTime = Globally.getDateTimeObj(currentDate, false);
            dayDiff = Days.daysBetween(savedDateTime.toLocalDate(), currentDateTime.toLocalDate()).getDays();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dayDiff;
    }


    public static int getMinDiff(String savedDate, String currentDate) {
        int minDiff = -1;
        try {
            DateTime savedDateTime = Globally.getDateTimeObj(savedDate, false);
            DateTime currentDateTime = Globally.getDateTimeObj(currentDate, false);
            minDiff = (int) getDateTimeDuration(savedDateTime, currentDateTime).getStandardMinutes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return minDiff;
    }


    public int getDateTitleCount(List<CanadaDutyStatusModel> logList) {
        int count = 0;
        try {
            for (int i = 0; i < logList.size(); i++) {
                if (i == 0) {
                    count++;
                } else {
                    int dayDiff = getDayDiff(logList.get(i - 1).getDateTimeWithMins(), logList.get(i).getDateTimeWithMins());
                    if (dayDiff != 0) {
                        count++;
                    }
                }
            }
        } catch (Exception e) {
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




    public boolean CheckGpsStatus(Context context) {
        LocationManager locationManager;
        boolean gpsStatus = true;
        if (context != null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        return gpsStatus;
    }


    public boolean CheckGpsStatusToCheckMalfunction(Context context) {
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean gpsStatusNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            // turnGPSOn(context);
            return gpsStatus || gpsStatusNetwork;
        } else {
            return true;
        }

    }

    // 0 for OFF and 1 for ON
    public int GetGpsStatusIn0And1Form(Context context) {
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean gpsStatusNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            // turnGPSOn(context);
            if (gpsStatus || gpsStatusNetwork) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 1;
        }

    }

    private void turnGPSOn(Context context) {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
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

    public boolean isDriverAllowedToChange(Context context, String DriverId, int obdStatus, Utils obdUtils, Globally global, DriverPermissionMethod driverPermissionMethod, DBHelper dbHelper) {

        boolean isAllowedToChange = true;

        try {

            if (obdStatus == Constants.WIRED_CONNECTED ||
                    obdStatus == WIFI_CONNECTED ||
                    obdStatus == Constants.BLE_CONNECTED) {

                StringBuilder obdData = obdUtils.getObdLogData(context);
                String[] fileArray = obdData.toString().split("\n\n");
                Logger.LogDebug("obdLog", "fileArray: " + fileArray);

                if (fileArray.length > 0) {
                    JSONObject data = new JSONObject(fileArray[fileArray.length - 1]);

                    try {
                        String lastDate = data.getString(Constants.CurrentLogDate);
                        DateTime lastSavedTime = global.getDateTimeObj(lastDate, false);
                        DateTime currentDate = global.getDateTimeObj(global.GetDriverCurrentDateTime(global, context), false);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // in case if Device debug Log is not enabled from web settings
                boolean isVehicleMoving = SharedPref.isVehicleMoving(context);
                boolean isDeviceLogEnabled = driverPermissionMethod.isDeviceLogEnabled(DriverId, dbHelper);
                if (isVehicleMoving == true && isDeviceLogEnabled == false) {
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
                                                     Globally global, Context context) {

        double speedInKm = -1;

        try {
            double odometerDistance = Double.parseDouble(currentHighPrecisionOdometer) - Double.parseDouble(previousHighPrecisionOdometer);
            if (savedTime.length() > 10) {
                try {
                    String timeStampStr = savedTime.replace(" ", "T");
                    DateTime savedDateTime = global.getDateTimeObj(timeStampStr, false);
                    DateTime currentDateTime = global.getDateTimeObj(currentDate, false);

                    int timeInSecnd = (int) Constants.getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
                    //Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();
                    speedInKm = (odometerDistance / 1000.0f) / (timeInSecnd / 3600.0f);
                    // speedInKm = odometerDistance / timeInSecnd;

                } catch (Exception e) {
                    e.printStackTrace();

                    // save current HighPrecisionOdometer locally
                    SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, global.GetDriverCurrentDateTime(global, context), context);

                }

            } else {
                // save current HighPrecisionOdometer locally
                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, global.GetDriverCurrentDateTime(global, context), context);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return speedInKm;

    }


    public JSONArray AddNewStatusInList(String DriverName, String DriverStatusId, String violaotionReason, String address,
                                        String DRIVER_ID, String City, String State, String Country, String AddressLine,
                                        String AddressKm, String CurrentCycleId, String Reason, String isPersonal, boolean isViolation,
                                        String IsStatusAutomatic, String OBDSpeed, String GPSSpeed, String PlateNumber,
                                        String decesionSource, boolean isYardMove,
                                        Globally Global, boolean isHaulException, boolean isHaulExceptionUpdate,
                                        String isAdverseException, String adverseExceptionRemark, String LocationType,
                                        String malAddInfo, boolean IsNorthCanada, boolean IsCycleChanged, String Odometer,
                                        String CoDriverId, String CoDriverName, String Truck, String Trailer, String EngHour,
                                        String odometer, String DriverVehicleTypeId, boolean isHosLogging, HelperMethods hMethods,
                                        DBHelper dbHelper, Context context) {

        JSONArray driverArray = new JSONArray();
        long DriverLogId = 0;
        long LastJobTotalMin = 0;
        String lastDateTimeStr = "";
        //  String StartDeviceCurrentTime = Global.GetDriverCurrentDateTime(global, context);
        String StartUTCCurrentTime = Global.GetCurrentUTCTimeFormat();
        DateTime startDateTimeUtc = Global.GetDriverCurrentTime(StartUTCCurrentTime, Global, context);

        try {

            driverArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject lastItemJson = hMethods.GetLastJsonFromArray(driverArray);

        if (driverArray.length() > 0 && lastItemJson != null && lastItemJson.length() > 0) {
            try {
                // DriverLogId = lastItemJson.getLong(ConstantsKeys.DriverLogId);
                lastDateTimeStr = lastItemJson.getString(ConstantsKeys.startDateTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        try {
            // DateTime currentDateTime = Global.getDateTimeObj(StartDeviceCurrentTime, false);
            if (lastDateTimeStr.length() < 10) {
                lastDateTimeStr = startDateTimeUtc.toString();
            }
            DateTime lastDateTime = Global.getDateTimeObj(lastDateTimeStr, false);
            LastJobTotalMin = getDateTimeDuration(startDateTimeUtc, lastDateTime).getStandardMinutes();

            if (LastJobTotalMin < 0) {
                LastJobTotalMin = Constants.getMinDiff(lastDateTime, startDateTimeUtc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject lastItemUpdatedJson = hMethods.UpdateLastJsonFromArray(driverArray, startDateTimeUtc.toString(),
                StartUTCCurrentTime, LastJobTotalMin, Odometer);

        if (address.equals("no_address")) {
            if (City.length() > 0) {
                address = City + ", " + State + ", " + Country;
            } else {
                address = AddressLine;
            }
        }

        if (AddressKm.equals("no_address")) {
            if (City.length() > 0) {
                AddressKm = City + ", " + State;
            }
        }

        /*if(Reason.equals("Border Crossing")){
            IsCycleChanged = true;
        }*/

        // DriverLogId = DriverLogId + 1;
        JSONObject newJsonData = hMethods.AddJobInArray(
                DriverLogId,
                Long.parseLong(DRIVER_ID),
                Integer.valueOf(DriverStatusId),

                startDateTimeUtc.toString(),
                startDateTimeUtc.toString(),
                StartUTCCurrentTime,
                StartUTCCurrentTime,

                0,  // because start and end date will be same for new status for that time
                Globally.LATITUDE,
                Globally.LONGITUDE,
                Globally.LATITUDE,
                Globally.LONGITUDE,
                isYardMove,
                Boolean.parseBoolean(isPersonal),
                Integer.valueOf(CurrentCycleId),
                isViolation,
                violaotionReason,
                DriverName,
                Reason,
                Trailer,
                address, address,
                Truck,
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
                IsNorthCanada,
                AddressKm,
                IsCycleChanged,
                Odometer,
                Odometer,
                CoDriverId,
                CoDriverName,
                "0",
                EngHour,
                odometer,
                DriverVehicleTypeId,
                false,
                isHosLogging



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

    public double getPersonalUseOdometerFromArray(String DRIVER_ID, int offsetFromUTC, DateTime currentDateTime, DateTime currentUTCTime,
                                                  HelperMethods hMethods, DBHelper dbHelper, Context context) {
        double PersonalDistance = 0;
        try {
            JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
            JSONArray currentDayArray = hMethods.GetSingleDateArray(driverLogArray, currentDateTime, currentDateTime, currentUTCTime, true, offsetFromUTC, context);

            for (int i = 0; i < currentDayArray.length(); i++) {
                JSONObject obj = (JSONObject) currentDayArray.get(i);
                if (obj.getBoolean(ConstantsKeys.Personal)) {
                    Double startOdometer = obj.getDouble(ConstantsKeys.StartOdometerInKm);
                    if (startOdometer > 0) {
                        Double endOdometer = obj.getDouble(ConstantsKeys.EndOdometerInKm);

                        if (i == currentDayArray.length() - 1) {
                            endOdometer = Double.parseDouble(SharedPref.getObdOdometer(context));
                        }

                        double odometerDiff = endOdometer - startOdometer;
                        PersonalDistance = PersonalDistance + odometerDiff;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return PersonalDistance;
    }


    public double getAccumulativePersonalDistance(String DRIVER_ID, int offsetFromUTC, DateTime currentDateTime, DateTime currentUTCTime,
                                                  HelperMethods hMethods, DBHelper dbHelper, Context context) {

        double PersonalDistance = getPersonalUseOdometerFromArray(DRIVER_ID, offsetFromUTC, currentDateTime, currentUTCTime,
                hMethods, dbHelper, context);
        try {

            String puSelectedDate = SharedPref.getSelectedDayForPuOdometer(context);
            if (puSelectedDate.equals(Globally.GetCurrentDeviceDate(null, new Globally(), context))) {
                double TotalPUOdometerForDay = Double.parseDouble(SharedPref.getTotalPUOdometerForDay(context));
                PersonalDistance = PersonalDistance + TotalPUOdometerForDay;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return PersonalDistance;
    }


    public boolean isLocationMissing(String DRIVER_ID, String CurrentCycleId, DriverPermissionMethod driverPermissionMethod,
                                     RecapViewMethod recapViewMethod, Globally Global, HelperMethods hMethods, DBHelper dbHelper,
                                     JSONObject logPermissionObj, Context context) {
        boolean isLocMissing = false;
        try {
            if (logPermissionObj == null) {
                logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DRIVER_ID), "", dbHelper);
            }
            List<RecapSignModel> signList = GetCertifySignList(recapViewMethod, DRIVER_ID, hMethods, dbHelper,
                    Global.GetCurrentDeviceDate(null, Global, context), CurrentCycleId, logPermissionObj, Global, context);
            for (int i = 0; i < signList.size(); i++) {
                if (signList.get(i).isMissingLocation()) {
                    isLocMissing = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isLocMissing;
    }


    public boolean isLocationMissingSelectedDay(DateTime selectedDateTime, DateTime currentDateTime,
                                                JSONArray driverLogArray, boolean isAlreadySelectedDayArray, HelperMethods hMethods, Globally Global, Context context) {
        boolean isLocMissing = false;
        try {
            boolean IsCurrentDate;
            int dayDiff = Days.daysBetween(selectedDateTime.toLocalDate(), currentDateTime.toLocalDate()).getDays();

            if (dayDiff == 0) {
                IsCurrentDate = true;
            } else {
                IsCurrentDate = false;
            }
            JSONArray selectedArray;
            if (isAlreadySelectedDayArray) {
                selectedArray = driverLogArray;
            } else {
                selectedArray = hMethods.GetSingleDateArray(driverLogArray, selectedDateTime,
                        currentDateTime, Globally.GetCurrentUTCDateTime(), IsCurrentDate,
                        (int) Global.GetDriverTimeZoneOffSet(context), context);
            }

            for (int i = 0; i < selectedArray.length(); i++) {
                JSONObject obj = (JSONObject) selectedArray.get(i);
                String StartLocation = obj.getString(ConstantsKeys.StartLocation).trim();
                String StartLocationKm = obj.getString(ConstantsKeys.StartLocationKm).trim();
                //   if (locationKm.contains("null") || locationKm.equals(",") || locationKm.equals("") || locationKm.equals(context.getString(R.string.no_location_found))) {
                if (StartLocation.equals("null") || StartLocation.equals(",") || StartLocation.length() == 0 || StartLocation.equals("No Location Found")) {
                    if (StartLocationKm.equals("null") || StartLocationKm.equals(",") || StartLocationKm.length() == 0 || StartLocationKm.equals("No Location Found")) {
                        String StartLatitude = obj.getString(ConstantsKeys.StartLatitude);
                        if (StartLatitude.length() < 5 && SharedPref.IsAOBRD(context) == false) {
                            isLocMissing = true;
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isLocMissing;
    }


    public List<RecapSignModel> GetCertifySignList(RecapViewMethod recapViewMethod, String DRIVER_ID,
                                                   HelperMethods hMethods, DBHelper dbHelper,
                                                   String currentDate, String CurrentCycleId,
                                                   JSONObject logPermissionObj, Globally Global, Context context) {
        JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
        JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
        int arraylength = recap18DaysArray.length();
        int initilizeValue = 0;
        int daysValidationValue = 0;
        int DriverPermittedDays = GetDriverPermitDaysCount(logPermissionObj, CurrentCycleId, false);
        DateTime lastDateTime = new DateTime();
        DateTime currentDateTime = new DateTime();
        List<RecapSignModel> recapSignatureList = new ArrayList<>();

        if (DriverPermittedDays > arraylength) {
            DriverPermittedDays = arraylength;
        }

        if (arraylength > 0) {
            try {
                String currentDateHalf = Globally.ConvertDateFormatyyyy_MM_dd(currentDate);
                currentDateTime = Globally.getDateTimeObj(currentDateHalf + "T00:00:00", false);
                lastDateTime = currentDateTime.minusDays(DriverPermittedDays);

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
            } else {

            }

            for (int i = initilizeValue; i >= daysValidationValue; i--) {
                try {
                    JSONObject obj = (JSONObject) recap18DaysArray.get(i);
                    String date = Globally.ConvertDateFormatyyyy_MM_dd(obj.getString(ConstantsKeys.Date));
                    DateTime selectedDateTime = Globally.getDateTimeObj(date + "T00:00:00", false);

                    boolean isLocationMissing = isLocationMissingSelectedDay(selectedDateTime, currentDateTime, driverLogArray,
                            false, hMethods, Global, context);

                    if (selectedDateTime.isAfter(lastDateTime) || selectedDateTime.equals(lastDateTime)) {
                        String image = obj.getString(ConstantsKeys.LogSignImage);
                        boolean isReCertifyRequired = isReCertifyRequired(context, null, selectedDateTime.toString());
                        if (image.length() == 0) {
                            recapSignatureList.add(new RecapSignModel(false, isLocationMissing, isReCertifyRequired, selectedDateTime));
                        } else {
                            recapSignatureList.add(new RecapSignModel(true, isLocationMissing, isReCertifyRequired, selectedDateTime));
                        }
                    } else {
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return recapSignatureList;
    }


    public boolean GetCertifyLogSignStatus(RecapViewMethod recapViewMethod, String DRIVER_ID, DBHelper dbHelper, String currentDate,
                                           String CurrentCycleId, JSONObject logPermissionObj) {
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
                DateTime currentDateTime = Globally.getDateTimeObj(currentDateHalf + "T00:00:00", false);
                lastDateTime = currentDateTime.minusDays(DriverPermittedDays);

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
            if (initilizeValue == daysValidationValue) {
                for (int i = initilizeValue; i >= 0; i--) {
                    try {
                        if (initilizeValue < recap18DaysArray.length()) {
                            JSONObject obj = (JSONObject) recap18DaysArray.get(i);
                            String image = obj.getString(ConstantsKeys.LogSignImage);
                            String LogSignImageInByte = obj.getString(ConstantsKeys.LogSignImageInByte);

                            if (image.length() == 0 && LogSignImageInByte.length() == 0) {
                                IsPendingSignature = true;
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            } else {
                for (int i = initilizeValue; i > daysValidationValue; i--) {
                    try {
                        JSONObject obj = (JSONObject) recap18DaysArray.get(i);
                        String date = Globally.ConvertDateFormatyyyy_MM_dd(obj.getString(ConstantsKeys.Date));
                        DateTime selectedDateTime = Globally.getDateTimeObj(date + "T00:00:00", false);

                        if (selectedDateTime.isAfter(lastDateTime) || selectedDateTime.equals(lastDateTime)) {
                            String image = obj.getString(ConstantsKeys.LogSignImage);
                            if (image.length() == 0) {
                                IsPendingSignature = true;
                                break;
                            }

                        } else {
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return IsPendingSignature;
    }


    public void UpdateCertifyLogArray(RecapViewMethod recapViewMethod, String DRIVER_ID, int DriverPermitDays,
                                      DBHelper dbHelper, Context context) {
        JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);

        if (recap18DaysArray.length() > 0) {
            try {

                boolean isChangeForUpdate = false;
                String currentDateHalf = Globally.ConvertDateFormatyyyy_MM_dd(Globally.
                        GetCurrentDeviceDate(null, new Globally(), context));
                DateTime currentDateTime = Globally.getDateTimeObj(currentDateHalf + "T00:00:00", false);
                DateTime fromDateTime = currentDateTime.minusDays(DriverPermitDays);
                JSONArray reCertifyArray = new JSONArray(SharedPref.getReCertifyData(context));

                for (int i = reCertifyArray.length() - 1; i >= 0; i--) {
                    JSONObject obj = (JSONObject) reCertifyArray.get(i);

                    if (obj.getBoolean(ConstantsKeys.IsRecertifyRequied)) {
                        DateTime selectedDateTime = Globally.getDateTimeObj(obj.getString(ConstantsKeys.LogDate), false);

                        if (selectedDateTime.isAfter(fromDateTime) || selectedDateTime.equals(fromDateTime)) {

                            for (int j = recap18DaysArray.length() - 1; j >= 0; j--) {
                                JSONObject objRecap = (JSONObject) recap18DaysArray.get(j);
                                String date = Globally.ConvertDateFormatyyyy_MM_dd(objRecap.getString(ConstantsKeys.Date));
                                DateTime recapSelectedDate = Globally.getDateTimeObj(date, false);
                                if (selectedDateTime.equals(recapSelectedDate)) {
                                    String byteImage = objRecap.getString(ConstantsKeys.LogSignImageInByte);
                                    if (objRecap.getString(ConstantsKeys.LogSignImage).length() > 0 || byteImage.length() > 0) {
                                        objRecap.put(ConstantsKeys.LogSignImage, "");
                                        objRecap.put(ConstantsKeys.LogSignImageInByte, "");
                                        objRecap.put(ConstantsKeys.CertifyOldImage, byteImage);

                                        recap18DaysArray.put(j, objRecap);
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

                if (isChangeForUpdate) {
                    // update recap array for re-certify log
                    recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, recap18DaysArray);

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

                String image = obj.getString(ConstantsKeys.LogSignImageInByte);
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

                String image = obj.getString(ConstantsKeys.LogSignImageInByte);
                if (image.length() > 20) {
                    LogSignImage = image;
                    break;
                } else if (obj.has(ConstantsKeys.CertifyOldImage)) {
                    image = obj.getString(ConstantsKeys.CertifyOldImage);
                    if (image.length() > 20) {
                        LogSignImage = image;
                        break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return LogSignImage;
    }


    public String getLastSignDate(RecapViewMethod recapViewMethod, String DRIVER_ID, DBHelper dbHelper) {

        JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
        String LogSignDate = "";

        for (int i = recap18DaysArray.length() - 1; i >= 0; i--) {
            try {
                JSONObject obj = (JSONObject) recap18DaysArray.get(i);

                String image = obj.getString(ConstantsKeys.LogSignImageInByte);
                if (image.length() > 20) {
                    LogSignDate = obj.getString(ConstantsKeys.Date);
                    break;
                } else if (obj.has(ConstantsKeys.CertifyOldImage)) {
                    image = obj.getString(ConstantsKeys.CertifyOldImage);
                    if (image.length() > 20) {
                        LogSignDate = obj.getString(ConstantsKeys.Date);
                        break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return LogSignDate;
    }


    public boolean isPTI_SignExist(JSONArray inspection18DaysArray) {

        boolean isPtiSign = false;


        for (int i = 0; i < inspection18DaysArray.length(); i++) {
            try {
                JSONObject obj = (JSONObject) inspection18DaysArray.get(i);
                JSONObject ptiObj = new JSONObject(obj.getString(ConstantsKeys.Inspection));

                String image = "";
                if (ptiObj.has(ConstantsKeys.ByteDriverSign)) {
                    image = ptiObj.getString(ConstantsKeys.ByteDriverSign);
                } else if (ptiObj.has(ConstantsKeys.DriverSignature)) {
                    String[] ImageArray = ptiObj.getString(ConstantsKeys.DriverSignature).split("@@@");
                    if (ImageArray.length > 1) {
                        image = ImageArray[1];
                    }
                }

                if (image.length() > 20) {
                    isPtiSign = true;
                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return isPtiSign;
    }


    public ArrayList<String> getPtiLastSignature(JSONArray inspection18DaysArray) {

        ArrayList<String> LogSignImageWithDate = new ArrayList<>();

        for (int i = 0; i < inspection18DaysArray.length(); i++) {
            try {
                JSONObject obj = (JSONObject) inspection18DaysArray.get(i);
                JSONObject ptiObj = new JSONObject(obj.getString(ConstantsKeys.Inspection));

                String image = "";
                if (ptiObj.has(ConstantsKeys.ByteDriverSign)) {
                    image = ptiObj.getString(ConstantsKeys.ByteDriverSign);
                } else if (ptiObj.has(ConstantsKeys.DriverSignature)) {
                    String[] ImageArray = ptiObj.getString(ConstantsKeys.DriverSignature).split("@@@");
                    if (ImageArray.length > 1) {
                        image = ImageArray[1];
                    }
                }

                // String image = ptiObj.getString(ConstantsKeys.ByteDriverSign);
                if (image.length() > 20) {
                    LogSignImageWithDate.add(image);
                    String InspectionDateTime = ptiObj.getString(ConstantsKeys.InspectionDateTime); //Globally.ConvertDateFormatMMddyyyy(
                    LogSignImageWithDate.add(InspectionDateTime);
                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return LogSignImageWithDate;
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    public void LoadByteImage(ImageView imgView, String LogSignImageInByte) {
        final int width = imgView.getWidth();
        final int height = imgView.getHeight();

        try {
            if (LogSignImageInByte.length() > 0) {
                final Bitmap bitmap = Globally.ConvertStringBytesToBitmap(LogSignImageInByte);

                if (width > 0 && height > 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imgView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
                        }
                    }, 250);
                } else {
                    imgView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 400, 180, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public JSONObject getUnIdentifiedLogJSONObj(String UnAssignedVehicleMilesId, String CompanyId, String VinNumber, String TruckTV,
                                                String LastDutyStatus, String StatusStartTime, String StatusEndTime, String lat, String lon,
                                                String endLat, String endLon, String StartEngineSeconds, String EndEngineSeconds,
                                                String StartOdometer, String EndOdometer, boolean Intermediate,
                                                boolean IntermediateUpdate, String IntermediateLogId,
                                                boolean IsUploadedUnIdenRecord, String LocationType, String Origin) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (UnAssignedVehicleMilesId.length() == 0) {
                UnAssignedVehicleMilesId = "0";
            }
            if (IntermediateLogId.length() == 0) {
                IntermediateLogId = "0";
            }

            jsonObject.put(ConstantsKeys.UnAssignedVehicleMilesId, UnAssignedVehicleMilesId);
            jsonObject.put(ConstantsKeys.CompanyId, CompanyId);
            jsonObject.put(ConstantsKeys.VIN, VinNumber);
            jsonObject.put(ConstantsKeys.EquipmentNumber, TruckTV);
            jsonObject.put(ConstantsKeys.DutyStatus, LastDutyStatus);
            jsonObject.put(ConstantsKeys.UTCStartDateTime, StatusStartTime);
            jsonObject.put(ConstantsKeys.UTCEndDateTime, StatusEndTime);
            jsonObject.put(ConstantsKeys.StartLatitude, lat);
            jsonObject.put(ConstantsKeys.StartLongitude, lon);
            jsonObject.put(ConstantsKeys.EndLatitude, endLat);
            jsonObject.put(ConstantsKeys.EndLongitude, endLon);
            jsonObject.put(ConstantsKeys.StartEngineSeconds, StartEngineSeconds);
            jsonObject.put(ConstantsKeys.EndEngineSeconds, EndEngineSeconds);
            jsonObject.put(ConstantsKeys.StartOdometer, StartOdometer);
            jsonObject.put(ConstantsKeys.EndOdometer, EndOdometer);
            jsonObject.put(ConstantsKeys.Intermediate, Intermediate);
            jsonObject.put(ConstantsKeys.IntermediateUpdate, IntermediateUpdate);
            jsonObject.put(ConstantsKeys.IntermediateLogId, IntermediateLogId);
            jsonObject.put(ConstantsKeys.IsUploadedUnIdenRecord, IsUploadedUnIdenRecord);
            jsonObject.put(ConstantsKeys.LocationType, LocationType);
            jsonObject.put(ConstantsKeys.Origin, Origin);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
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


    public static String Convert2DecimalPlacesDouble(double value) {
        String strValue = "";
        try {
            strValue = String.format("%.2f", value);
            strValue = strValue.replace(".00", "");
        } catch (Exception e) {
            e.printStackTrace();
            strValue = "" + value;
        }

        return strValue;
    }


    public static String Convert0DecimalPlacesDouble(double value) {
        String strValue = "";
        try {
            strValue = String.format("%.0f", value);
            strValue = strValue.replace(".00", "");
        } catch (Exception e) {
            e.printStackTrace();
            strValue = "" + value;
        }

        return strValue;
    }


    public static String getBeforeDecimalValues(String value) {

        try {
            if (value.contains(".")) {
                String[] array = value.split("\\.");
                if (array.length > 0) {
                    value = array[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }


    public static String get2DecimalEngHour(Context context) {
        String engHour = SharedPref.getObdEngineHours(context);
        try {
            engHour = Convert2DecimalPlacesDouble(Double.parseDouble(engHour));
        } catch (Exception e) {
            engHour = SharedPref.getObdEngineHours(context);
            e.printStackTrace();
        }

        return engHour;
    }

    public static String Convert1DecimalPlacesDouble(double value) {
        try {
            return String.format("%.1f", value);
        } catch (Exception e) {
            e.printStackTrace();
            return "" + value;
        }

    }

    public static String Convert2DecimalPlacesString(String value) {
        try {
            String[] array = value.split("\\.");
            if (array.length > 1) {
                String val = array[1];
                if (val.length() > 2) {
                    val = val.substring(0, 2);
                }

                return array[0] + "." + val;
            } else {
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }

    }

    public boolean isExponentialValue(String str) {
        String CHAR1 = ".";             //   check for the presence of at least one letter
        String CHAR2 = "[0-9]+";       //   check for the presence of at least one number
        String CHAR3 = "[A-Za-z]";     //   check that only numbers and letters compose this string

        if (str.contains(CHAR1) || (str.matches(CHAR2) && str.matches(CHAR3))) {
            return true;
        } else {
            return false;
        }

    }


    static boolean isExponentialString(String str) {
        String CHAR1 = ".";             //   check for the presence of at least one letter
        String CHAR2 = "[0-9]+";       //   check for the presence of at least one number
        String CHAR3 = "[A-Za-z]";     //   check that only numbers and letters compose this string

        if (str.contains(CHAR1) || (str.matches(CHAR2) && str.matches(CHAR3))) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isValidData(String data) {
        boolean isValid = false;
        try {
            if (!data.equals("--") && !data.equals("0")) {
                if (Double.parseDouble(data) > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
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

            Logger.LogDebug("CPU_INFO", output);

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
            resRandomStr = "" + resRandom;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resRandomStr;
    }


    @SuppressLint("MissingPermission")
    public static String getSerialNumber(Context context) {
        String SerialNumber = "";

        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.
                    TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }

            SerialNumber = telephonyManager.getSimSerialNumber();

            if (SerialNumber.equals("null") || SerialNumber.length() == 0) {
                SerialNumber = telephonyManager.getLine1Number();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return SerialNumber;
    }


    @SuppressLint("MissingPermission")
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

        } catch (Exception e) {
            deviceId = "";
            e.printStackTrace();
        }

        try {
            if (deviceId.trim().equalsIgnoreCase("null") || deviceId.trim().length() < 5) {
                if (userName.length() > 2) {
                    userName = userName.substring(0, 2);
                }
                deviceId = userName + "00" + getRandomNumber();
            }
        } catch (Exception e) {
            deviceId = "000" + getRandomNumber();
            e.printStackTrace();
        }

        return deviceId;
    }


    public void saveLoginDetails(String username, String OSType, String DeviceSimInfo,
                                 String IMEINumber, Utils util) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("InputType", "Login");
            obj.put("username", username);
            obj.put("OSType", OSType);
            obj.put("IMEINumber", IMEINumber);
            obj.put("DeviceSimInfo", DeviceSimInfo);


            util.writeToLogFile(obj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ------------- Get Driver permitted Days --------------
    public int GetDriverPermitDaysCount(JSONObject logPermissionObj, String CurrentCycleId, boolean IsDot) {

        int UsaMaxDays = 8;
        int CanMaxDays = 14;
        int DriverPermitMaxDays = 0;
        boolean isLogNull = false;
        if (logPermissionObj != null && logPermissionObj.toString().length() > 2) {
            try {
                DriverPermitMaxDays = logPermissionObj.getInt(ConstantsKeys.ViewCertifyDays);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            isLogNull = true;
        }

        if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
            if (IsDot || isLogNull) {
                DriverPermitMaxDays = UsaMaxDays;
            } else {
                if (DriverPermitMaxDays > UsaMaxDays) {
                    DriverPermitMaxDays = UsaMaxDays;
                }
            }
        } else {
            if (IsDot || isLogNull) {
                DriverPermitMaxDays = CanMaxDays;
            }
        }

        return DriverPermitMaxDays;
    }

    // CT-PAT Truck inspection list
    public List<PrePostModel> CtPatTruckList() {
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
    public List<PrePostModel> CtPatTrailerList() {
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


    public boolean getShortHaulExceptionDetail(Context context, String DriverId, Globally global,
                                               boolean isShortHaul, boolean isAdverseExcptn, boolean IsNorthCanada,
                                               HelperMethods hMethods, DBHelper dbHelper) {

        boolean isHaulException = false;
        try {
            JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
            JSONObject lastJsonItem = (JSONObject) driverLogArray.get(driverLogArray.length() - 1);

            DateTime currentDateTime = global.getDateTimeObj(global.GetDriverCurrentDateTime(global, context), false);    // Current Date Time
            DateTime currentUTCTime = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), true);
            int offsetFromUTC = (int) global.GetDriverTimeZoneOffSet(context);
            //String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context);
            String CurrentCycleId = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(context), context);

            boolean isSingleDriver = global.isSingleDriver(context);
            int DRIVER_JOB_STATUS = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
            int rulesVersion = SharedPref.GetRulesVersion(context);

            List<DriverLog> oDriverLogDetail = hMethods.getSavedLogList(Integer.valueOf(DriverId), currentDateTime, currentUTCTime, dbHelper);
            DriverDetail oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DriverId),
                    offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver, DRIVER_JOB_STATUS, false,
                    isShortHaul, isAdverseExcptn, IsNorthCanada,
                    rulesVersion, oDriverLogDetail, context);

            if (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
                oDriverDetail.setCanAdverseException(isAdverseExcptn);
            }

            LocalCalls CallDriverRule = new LocalCalls();
            isHaulException = CallDriverRule.checkShortHaulExceptionEligibility(oDriverDetail).isEligibleShortHaulException();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return isHaulException;

    }


    public boolean IsWarningNotificationAllowed(String DriverId, DBHelper dbHelper) {
        DriverPermissionMethod driverPermissionMethod = new DriverPermissionMethod();
        JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DriverId), "", dbHelper);
        boolean IsAllowed = false;

        if (logPermissionObj != null && logPermissionObj.has(ConstantsKeys.IsAutoDutyNotificationAllowed)) {
            try {
                IsAllowed = logPermissionObj.getBoolean(ConstantsKeys.IsAutoDutyNotificationAllowed);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return IsAllowed;
    }


    public void CopyString(Context context, String data) {
        try {

            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(data);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("text label", data);
                clipboard.setPrimaryClip(clip);
            }
            if (context != null) {
                Toast.makeText(context, "Sim number copied", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void DeleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadByteImage(String LogSignImageInByte, ImageView signImageView) {

        if (LogSignImageInByte.length() > 0) {
            Bitmap bitmap = Globally.ConvertStringBytesToBitmap(LogSignImageInByte);
            signImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, signImageView.getWidth(),
                    signImageView.getHeight(), false));
        }

    }


    public int intToPixel(Context context, int size) {
        if (context != null) {
            Resources r = context.getResources();
            int px = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, size, r.getDisplayMetrics()));
            return px;
        } else {
            return 60;
        }
    }


    public void writeViolationFile(DateTime currentDateTime, DateTime currentUTCTime, int DriverId,
                                   String CurrentCycleId, int offsetFromUTC, boolean isSingleDriver,
                                   int DRIVER_JOB_STATUS, boolean isOldRecord, boolean is16hrHaulExcptn, String violationReason,
                                   HelperMethods hMethods, DBHelper dbHelper, Context context) {

        try {
            JSONArray finalEldLogArray = new JSONArray();
            JSONArray DriverLog18Days = hMethods.getSavedLogArray(DriverId, dbHelper);

            // Add inputs in json those are passing in ELD rule library
            JSONObject eldRuleInputsJson = new JSONObject();
            eldRuleInputsJson.put(ConstantsKeys.CurrentDateTime, currentDateTime.toString());
            eldRuleInputsJson.put(ConstantsKeys.CurrentUTCTime, currentUTCTime.toString());
            eldRuleInputsJson.put(ConstantsKeys.CurrentCycleId, CurrentCycleId);
            eldRuleInputsJson.put(ConstantsKeys.OffsetFromUTC, offsetFromUTC);
            eldRuleInputsJson.put(ConstantsKeys.IsSingleDriver, isSingleDriver);
            eldRuleInputsJson.put(ConstantsKeys.DriverId, DriverId);
            eldRuleInputsJson.put(ConstantsKeys.DriverJobStatus, DRIVER_JOB_STATUS);
            eldRuleInputsJson.put(ConstantsKeys.IsOldRecord, isOldRecord);
            eldRuleInputsJson.put(ConstantsKeys.Is16hrHaulException, is16hrHaulExcptn);
            eldRuleInputsJson.put(ConstantsKeys.ViolationReason, violationReason);

            finalEldLogArray.put(eldRuleInputsJson);
            finalEldLogArray.put(DriverLog18Days);

            Globally.SaveFileInSDCard(ConstantsKeys.ViolationTest, finalEldLogArray.toString(), true, context);

            Constants.IsAlreadyViolation = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String AddHorizontalDottedLine(int hLineX1, int hLineX2, int hLineY) {
        return "<line class=\"horizontal dotted-line\" x1=\"" + hLineX1 + "\" x2=\"" + hLineX2 + "\" y1=\"" + hLineY + "\" y2=\"" + hLineY + "\"></line>\n";
    }


    public String AddHorizontalLine(int hLineX1, int hLineX2, int hLineY) {
        return "<line class=\"horizontal\" x1=\"" + hLineX1 + "\" x2=\"" + hLineX2 + "\" y1=\"" + hLineY + "\" y2=\"" + hLineY + "\"></line>\n";
    }


    public String AddVerticalLine(int vLineX, int vLineY1, int vLineY2) {
        return "<line class=\"vertical\" x1=\"" + vLineX + "\" x2=\"" + vLineX + "\" y1=\"" + vLineY1 + "\" y2=\"" + vLineY2 + "\"></line>\n";
    }


    public String AddVerticalLineViolation(int vLineX, int vLineY1, int vLineY2) {
        return "<line class=\"vertical no-color\" x1=\"" + vLineX + "\" x2=\"" + vLineX + "\" y1=\"" + vLineY1 + "\" y2=\"" + vLineY2 + "\"></line>\n";
    }

    public String HtmlCloseTag(String OffDutyHour, String SleeperHour, String DrivingHour, String OnDutyHour) {
        return " </g>\n" +
                "   <g class=\"durations\" transform=\"translate(1505, 40)\">\n" +
                "                  <text class=\"label\" transform=\"translate(0, 25)\" dy=\"0.35em\">" + OffDutyHour + "</text>\n" +
                "                  <text class=\"label\" transform=\"translate(0, 75)\" dy=\"0.35em\">" + SleeperHour + "</text>\n" +
                "                  <text class=\"label\" transform=\"translate(0, 125)\" dy=\"0.35em\">" + DrivingHour + "</text>\n" +
                "                  <text class=\"label\" transform=\"translate(0, 175)\" dy=\"0.35em\">" + OnDutyHour + "</text>\n" +
                "               </g>    \n" +
                "            </svg>\n" +
                "         </log-graph>\n" +
                "      </div>";
    }


    public int VerticalLine(int status) {
        int job = 0;
        int OFF = 25;
        int SB = 75;
        int DR = 125;
        int ON = 175;

        switch (status) {
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


    private String getTimeFormat(String time) {
        // 2022-01-11T23:29:43.267
        if (time.length() > 21) {
            time = time.substring(0, 19);
        }
        return time;
    }


    public int getMinDifference(String lastRestartTime, String currentDate) {

        int minDiff = 0;

        try {
            lastRestartTime = getTimeFormat(lastRestartTime);
            currentDate = getTimeFormat(currentDate);
            DateTime savedDateTime = Globally.getDateTimeObj(lastRestartTime, false);
            DateTime currentDateTime = Globally.getDateTimeObj(currentDate, false);

            minDiff = (int) getDateTimeDuration(savedDateTime, currentDateTime).getStandardMinutes();
            //Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return minDiff;

    }

    public int getSecDifference(String lastRestartTime, String currentDate) {

        int secDiff = 0;

        try {
            DateTime savedDateTime = Globally.getDateTimeObj(lastRestartTime, false);
            DateTime currentDateTime = Globally.getDateTimeObj(currentDate, false);

            secDiff = (int) getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return secDiff;

    }


    public String convertLongDataToMb(long data) {
        String hrSize = null;

        double mb = ((data / 1024.0) / 1024.0);
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


    public static String getDriverStatus(int id) {
        String StatusId = "";
        switch (id) {

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


 /* public static JSONObject getClaimRecordInputsAsJson(String DriverId, String DriverStatusId,
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
  }*/


    public static JSONObject getClaimRecordInputsAsJson(String DriverId, String Vin, String DriverStatusId,
                                                        String UnAssignedVehicleMilesId, String AssignedRecordsId,
                                                        String Remarks, String UserName, String StartOdo, String EndOdo,
                                                        String StartLoc, String EndLoc, String StartCity, String StartState,
                                                        String StartCountry, String EndCity, String EndState, String EndCountry,
                                                        boolean startOdometer, boolean endOdometer, boolean startLocation, boolean endLocation) {

        JSONObject obj = new JSONObject();

        try {
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.VIN, Vin);
            obj.put(ConstantsKeys.DriverStatusId, DriverStatusId);
            obj.put(ConstantsKeys.UnAssignedVehicleMilesId, UnAssignedVehicleMilesId);
            obj.put(ConstantsKeys.AssignedUnidentifiedRecordsId, AssignedRecordsId);
            obj.put(ConstantsKeys.Remarks, Remarks);
            obj.put(ConstantsKeys.UserName, UserName);
            if (startOdometer) {
                obj.put(ConstantsKeys.StartOdometer, StartOdo);
            }

            if (endOdometer) {
                obj.put(ConstantsKeys.EndOdometer, EndOdo);
            }

            if (startLocation) {
                obj.put(ConstantsKeys.StartLocation, StartLoc);
                obj.put(ConstantsKeys.StartCity, StartCity);
                obj.put(ConstantsKeys.StartState, StartState);
                obj.put(ConstantsKeys.StartCountry, StartCountry);
            }

            if (endLocation) {
                obj.put(ConstantsKeys.EndLocation, EndLoc);
                obj.put(ConstantsKeys.EndCity, EndCity);
                obj.put(ConstantsKeys.EndState, EndState);
                obj.put(ConstantsKeys.EndCountry, EndCountry);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }


    public static JSONObject getRejectedRecordInputs(String DriverId, String UnAssignedVehicleMilesId, String Remarks) {

        JSONObject obj = new JSONObject();

        try {
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.UnAssignedVehicleMilesId, UnAssignedVehicleMilesId);
            obj.put(ConstantsKeys.Remarks, Remarks);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }


    public static JSONObject getCompanyRejectedRecordInputs(String DriverId, String UnAssignedVehicleMilesId,
                                                            String AssignedRecordsId, String Remarks) {

        JSONObject obj = new JSONObject();

        try {
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.UnAssignedVehicleMilesId, UnAssignedVehicleMilesId);
            obj.put(ConstantsKeys.AssignedUnidentifiedRecordsId, AssignedRecordsId);
            obj.put(ConstantsKeys.RejectionRemarks, Remarks);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }


    public static JSONObject getMalfunctionRecordInputs(String DriverId, String Remarks, String eventsCode) {

        JSONObject obj = new JSONObject();
        try {
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.Remarks, Remarks);
            obj.put(ConstantsKeys.EventList, eventsCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }


    public boolean IsSendLog(String DRIVER_ID, DriverPermissionMethod driverPermissionMethod, DBHelper dbHelper) {
        JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DRIVER_ID), "", dbHelper);
        boolean IsSendLog = true;

        try {
            if (logPermissionObj != null && logPermissionObj.has(ConstantsKeys.SendLog)) {
                IsSendLog = logPermissionObj.getBoolean(ConstantsKeys.SendLog);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return IsSendLog;
    }


    public int getAvailableSpace() {

        int iAvailableSpace = 0;

        try {
            File var10000 = Environment.getDataDirectory();
            Intrinsics.checkNotNullExpressionValue(var10000, "Environment.getDataDirectory()");
            File iPath = var10000;
            StatFs iStat = new StatFs(iPath.getPath());
            long iBlockSize = iStat.getBlockSizeLong();
            long iAvailableBlocks = iStat.getAvailableBlocksLong();
            iAvailableSpace = Integer.valueOf(formatSize(iAvailableBlocks * iBlockSize).replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return iAvailableSpace;
    }


    public final String formatSize(long sizeee) {
        long size = sizeee;
        String suffix = (String) null;
        if (size >= (long) 1024) {
            suffix = "KB";
            size = size / (long) 1024;
            if (size >= (long) 1024) {
                suffix = "MB";
                size /= (long) 1024;
            }


        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        for (int commaOffset = resultBuffer.length() - 3; commaOffset > 0; commaOffset -= 3) {
            resultBuffer.insert(commaOffset, ',');
        }

        if (suffix != null) {
            resultBuffer.append(suffix);
        }

        return resultBuffer.toString();
    }


    public static JSONArray getClaimRecordsInArray(
            String DriverId, String DriverName,
            String reason, String DriverStatusId,
            List<UnIdentifiedRecordModel> unIdentifiedRecordList,
            ArrayList<String> recordSelectedList,
            MalfunctionDiagnosticMethod malfunctionDiagnosticMethod,
            int offsetFromUTC, DBHelper dbHelper, Context context) {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < unIdentifiedRecordList.size(); i++) {
                if (recordSelectedList.get(i).equals("selected")) {
                    JSONObject obj = Constants.getClaimRecordInputsAsJson(DriverId, "", DriverStatusId,
                            unIdentifiedRecordList.get(i).getUnAssignedVehicleMilesId(),
                            unIdentifiedRecordList.get(i).getAssignedUnidentifiedRecordsId(),
                            reason, DriverName, "", "", "", "", "", "", "",
                            "", "", "", false, false, false, false);
                    array.put(obj);

                    if (unIdentifiedRecordList.get(i).getStartLocationKm().length() == 0) {
                        Constants.IsUnidentifiedLocMissing = true;
                    }

                    // remove that events from unidentified vehicle motion list, that was created in logout service.
                    if (DriverId.length() > 0) {
                        String DutyStatus = unIdentifiedRecordList.get(i).getDutyStatus();
                        DateTime dateTime = Globally.getDateTimeObj(unIdentifiedRecordList.get(i).getStartDateTime(), false);
                        dateTime = dateTime.minusHours(offsetFromUTC);
                        int CompanyId = Integer.valueOf(DriverConst.GetDriverDetails(DriverConst.CompanyId, context));
                        malfunctionDiagnosticMethod.removeEventAfterClaim(dateTime, CompanyId, DutyStatus, dbHelper);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return array;
    }


    public static JSONArray getRejectRecordsInArray(String DriverId, String reason,
                                                    List<UnIdentifiedRecordModel> unIdentifiedRecordList,
                                                    ArrayList<String> recordSelectedList,
                                                    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod,
                                                    int offsetFromUTC, DBHelper dbHelper, Context context) {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < unIdentifiedRecordList.size(); i++) {
                if (recordSelectedList.get(i).equals("selected")) {
                    if (!unIdentifiedRecordList.get(i).isCompanyAssigned()) {
                        JSONObject obj = Constants.getRejectedRecordInputs(DriverId, unIdentifiedRecordList.get(i).getUnAssignedVehicleMilesId(), reason);
                        array.put(obj);


                        // remove that events from unidentified vehicle motion list, that was created in logout service.
                        if (DriverId.length() > 0) {
                            String DutyStatus = unIdentifiedRecordList.get(i).getDutyStatus();
                            DateTime dateTime = Globally.getDateTimeObj(unIdentifiedRecordList.get(i).getStartDateTime(), false);
                            dateTime = dateTime.minusHours(offsetFromUTC);
                            int CompanyId = Integer.valueOf(DriverConst.GetDriverDetails(DriverConst.CompanyId, context));
                            malfunctionDiagnosticMethod.removeEventAfterClaim(dateTime, CompanyId, DutyStatus, dbHelper);
                        }


                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return array;
    }


    public static JSONArray getCompanyRejectRecordsInArray(String DriverId, String reason,
                                                           List<UnIdentifiedRecordModel> unIdentifiedRecordList,
                                                           ArrayList<String> recordSelectedList) {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < unIdentifiedRecordList.size(); i++) {
                if (recordSelectedList.get(i).equals("selected")) {
                    if (unIdentifiedRecordList.get(i).isCompanyAssigned()) {
                        JSONObject obj = Constants.getCompanyRejectedRecordInputs(DriverId,
                                unIdentifiedRecordList.get(i).getUnAssignedVehicleMilesId(),
                                unIdentifiedRecordList.get(i).getAssignedUnidentifiedRecordsId(),
                                reason);
                        array.put(obj);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return array;
    }


    public boolean isValidInteger(String text) {
        boolean isValid = false;
        try {
            int num = Integer.parseInt(text);
            // Logger.LogInfo("",num+" is a number");
            isValid = true;
        } catch (NumberFormatException e) {
            // e.printStackTrace();
            Logger.LogError("NumberFormatException", "NumberFormatException: " + text);
            isValid = false;
        } catch (Exception e) {
            e.printStackTrace();
            isValid = false;
        }
        return isValid;
    }


    public boolean isValidFloat(String text) {
        boolean isValid = false;
        if (text.length() > 0 && !text.equals("null")) {
            try {
                float num = Float.parseFloat(text);
                //Logger.LogInfo("",num+" is a number");
                isValid = true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                isValid = false;
            } catch (Exception e) {
                e.printStackTrace();
                isValid = false;
            }
        }
        return isValid;
    }

    public JSONObject getMalfunctionDiagnosticArray(String DriverId, String reason,
                                                    List<MalfunctionHeaderModel> MalHeaderList,
                                                    HashMap<String, List<MalfunctionModel>> MalfunctionChildMap,
                                                    List<MalfunctionHeaderModel> DiaHeaderList,
                                                    HashMap<String, List<MalfunctionModel>> DiafunctionChildMap,
                                                    Context context) {

        JSONObject obj = new JSONObject();
        //  boolean isClearDiagnostic  = SharedPref.IsClearDiagnostic(context);
        //  boolean isClearMalfunction = SharedPref.IsClearMalfunction(context);
        try {
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.Remarks, reason);
            JSONArray EventsList = new JSONArray();

            for (int i = 0; i < MalHeaderList.size(); i++) {
                // Malfunction. (S type event code is not eligible for clear)
                //isClearMalfunction &&
                if (MalHeaderList.get(i).getEventCode().equalsIgnoreCase("S")) {
                    List<MalfunctionModel> childList = MalfunctionChildMap.get(MalHeaderList.get(i).getEventCode());
                    for (int j = 0; j < childList.size(); j++) {
                        EventsList.put(childList.get(j).getId());
                    }
                }
            }

            if (DiaHeaderList != null) {
                for (int j = 0; j < DiaHeaderList.size(); j++) {
                    // Diagnostic (4 and 5 event code are not eligible for clear)
                    //isClearDiagnostic &&
                    if (!DiaHeaderList.get(j).getEventCode().equals("4") &&
                            !DiaHeaderList.get(j).getEventCode().equals("5")) {
                        List<MalfunctionModel> childList = DiafunctionChildMap.get(DiaHeaderList.get(j).getEventCode());
                        for (int k = 0; k < childList.size(); k++) {
                            EventsList.put(childList.get(k).getId());
                        }
                    }
                }
            }

            // add all events id as list in json obj
            obj.put(ConstantsKeys.EventList, EventsList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }


    public static JSONObject getMalfunctionRecordsInArray(
            String DriverId, String reason, List<MalfunctionModel> listData) {

        JSONObject obj = new JSONObject();

        try {
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.Remarks, reason);

            JSONArray EventsList = new JSONArray();
            for (int i = 0; i < listData.size(); i++) {
                EventsList.put(listData.get(i).getId());
            }
            obj.put(ConstantsKeys.EventList, EventsList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }


    public void saveAppUsageLog(String source, boolean isPU, boolean isPUOdoDialog, Utils util) {
        JSONObject obj = new JSONObject();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        try {

            if (isPU) {
                obj.put(ConstantsEnum.PU_Status, source);
                obj.put(ConstantsEnum.IsPUOdoDialog, isPUOdoDialog);
            } else {
                obj.put(Action, source);
            }
            obj.put(FileWriteTime, date);

            if (obj != null) {
                util.writeAppUsageLogFile(obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTempUnidentifiedLog(String data, Utils util) {
        try {
            util.writeAppUsageLogFile(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getMinDiff(String startDateTime, String endDateTime, boolean isPrevDayLastLog) {
        int minDiff = 0;
        try {
            DateTime startDateT = Globally.getDateTimeObj(startDateTime, false);
            DateTime endDateT = Globally.getDateTimeObj(endDateTime, false);
            minDiff = (int) Constants.getDateTimeDuration(startDateT, endDateT).getStandardMinutes();

            if (isPrevDayLastLog) {
                minDiff++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return minDiff;
    }


    public String getCountryName(Context context) {

        String CountryName = "";

        //String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context);
        String CurrentCycleId = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(context), context);

        if (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
            CountryName = "CANADA";
        } else if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
            CountryName = "USA";
        }

        return CountryName;
    }


    /*================== Get Signature Bitmap ====================*/
    public String GetSignatureBitmap(View targetView, ImageView canvasView, Context context) {
        Bitmap signatureBitmap = Bitmap.createBitmap(targetView.getWidth(),
                targetView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(signatureBitmap);
        targetView.draw(c);
        BitmapDrawable d = new BitmapDrawable(context.getResources(), signatureBitmap);
        canvasView.setBackgroundDrawable(d);

        return Globally.SaveBitmapToFile(signatureBitmap, "sign", 100, context);
    }


    public boolean isReCertifyRequired(Context context, JSONObject dataObj, String CertifyDateTime) {

        boolean IsRecertifyRequied = false;
        try {
            DateTime dateTime;
            if (CertifyDateTime.length() == 0) {
                dateTime = Globally.getDateTimeObj(dataObj.getString(ConstantsKeys.ChkDateTime), false);
            } else {
                dateTime = Globally.getDateTimeObj(CertifyDateTime, false);
            }
            JSONArray reCertifyArray = new JSONArray(SharedPref.getReCertifyData(context));

            for (int i = reCertifyArray.length() - 1; i >= 0; i--) {
                JSONObject obj = (JSONObject) reCertifyArray.get(i);

                DateTime selectedDateTime = Globally.getDateTimeObj(obj.getString(ConstantsKeys.LogDate), false);
                int DaysDiff = Constants.getDayDiff(dateTime.toString(), selectedDateTime.toString());
                if (DaysDiff == 0) {    //dateTime.equals(selectedDateTime)
                    IsRecertifyRequied = obj.getBoolean(ConstantsKeys.IsRecertifyRequied);
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return IsRecertifyRequied;
    }


    public static boolean isPlayStoreExist(Context context) {
        boolean isPlayStoreExist = false;
        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setPackage("com.android.vending");
            if (i.resolveActivity(packageManager) != null) {
                isPlayStoreExist = true;
            }
        } catch (Exception e) {
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


    public boolean isTimeAlertShownForDay(Context c) {
        String savedTime = SharedPref.getAutomaticAlertTime(c);
        if(savedTime.length() > 10){
            String currentDate = Globally.GetDriverCurrentDateTime(new Globally(), c);
            int dayDiff = getDayDiff(savedTime, currentDate);
            if(dayDiff != 0){
                return false;
            }else{
                return SharedPref.IsAutomaticTimeAlertShown(c);
            }
        }

        return false;
    }


    public void showAutomaticTimeAlert(Context c, Globally globally, AlertDialog timeSettingAlert){
        if(!isTimeAutomatic(c)) {
            boolean isTimeAlertShownForDay = isTimeAlertShownForDay(c);
            if(!isTimeAlertShownForDay){
                String savedTime = SharedPref.getAutomaticAlertTime(c);
                if(savedTime.length() > 10){
                    String currentDate = Globally.GetDriverCurrentDateTime(globally, c);
                    int minDiff = getMinDiff(savedTime, currentDate);
                    if(minDiff > 10){
                        String certifyTitle = "Automatic Time Zone Setting Required !!";
                        String titleDesc = "Kindly change your device's Date and Time to AUTOMATIC, so that your logbook could function properly.";
                        Globally.AutomaticTimeSettingAlert(c, certifyTitle, titleDesc, "Ok",
                                timeSettingAlert, false);

                        SharedPref.setAutomaticTimeAlertWithStatus(Globally.GetDriverCurrentDateTime(globally, c), true, c);

                    }
                }else{
                    SharedPref.setAutomaticTimeAlertWithStatus(Globally.GetDriverCurrentDateTime(globally, c), false, c);
                }
            }
        }
    }


    public static String parseDateWithName(String date) {
        String dateDesc = "";
        String[] dateMonth = Globally.dateConversionMMMM_ddd_dd(date).split(",");

        if (dateMonth.length > 1) {
            dateDesc = dateMonth[1] + " " + date.substring(8, 10) + ", " + date.substring(0, 4);
        }

        return dateDesc;


    }


    public void setTextStyleNormal(TextView textView) {
        textView.setTypeface(null, Typeface.NORMAL);
        if (UILApplication.getInstance().isNightModeEnabled()) {
            textView.setTextColor(Color.parseColor("#ffffff"));
        } else {
            textView.setTextColor(Color.parseColor("#354365"));
        }
    }
//textView.setTypeface(null, Typeface.NORMAL);


    /* public List<CanadaDutyStatusModel> parseCanadaLogoutLoginList(JSONArray logArray){

         List<CanadaDutyStatusModel> dotLogList = new ArrayList<>();

         try {
             for (int i = 0; i < logArray.length(); i++) {
                 JSONObject obj = (JSONObject) logArray.get(i);
                 JSONArray dateWiseArray = new JSONArray(obj.getString("loginAndLogoutDateObjectList"));
                 parseData(dateWiseArray, dotLogList, false);
             }


             if(dotLogList.size() > 0){
                 CanadaDutyStatusModel model = dotLogList.get(dotLogList.size()-1);
                 model.setHeaderViewCount(logArray.length());
                 dotLogList.set(dotLogList.size()-1, model);
             }

         }catch (Exception e){
             e.printStackTrace();
         }


         return dotLogList;
     }
 */
    public List<CanadaDutyStatusModel> parseCanadaLogoutLoginList(JSONArray logArray) {

        List<CanadaDutyStatusModel> dotLogList = new ArrayList<>();

        try {

            for (int i = 0; i < logArray.length(); i++) {
                JSONObject obj = (JSONObject) logArray.get(i);
                JSONArray dateWiseArray = new JSONArray(obj.getString("loginAndLogoutDateObjectList"));
                JSONArray afterSplitdateWiseArray = new JSONArray();
                for (int j = 0; j < dateWiseArray.length(); j++) {
                    JSONObject object = (JSONObject) dateWiseArray.get(j);
                    if (!object.getBoolean("IsUnidentified")) {
                        afterSplitdateWiseArray.put(object);
                    }
                }
                parseData(afterSplitdateWiseArray, dotLogList, false);
            }


            if (dotLogList.size() > 0) {
                CanadaDutyStatusModel model = dotLogList.get(dotLogList.size() - 1);
                model.setHeaderViewCount(logArray.length());
                dotLogList.set(dotLogList.size() - 1, model);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return dotLogList;
    }

    public List<CanadaDutyStatusModel> parseCanadaDotInList(JSONArray logArray, boolean isSorting) {

        List<CanadaDutyStatusModel> dotLogList = new ArrayList<>();
        parseData(logArray, dotLogList, isSorting);

        return dotLogList;
    }


    private void parseData(JSONArray logArray, List<CanadaDutyStatusModel> dotLogList, boolean isSorting) {

        try {
            int headerViewCount = 0;
            boolean isNewDate = true;
            String lastDateTimeMin = "";
            List<CanadaDutyStatusModel> dotDateWiseList = new ArrayList<>();
            for (int i = 0; i < logArray.length(); i++) {
                JSONObject obj = (JSONObject) logArray.get(i);

                String DateTimeWithMins = obj.getString(ConstantsKeys.DateTimeWithMins);
                if (DateTimeWithMins.length() > 19) {
                    DateTimeWithMins = DateTimeWithMins.substring(0, 19);
                }

                DateFormat format = new SimpleDateFormat(Globally.DateFormat, Locale.ENGLISH);
                Date date = format.parse(DateTimeWithMins);

                int seqNumber = 0;
                if (!obj.isNull(ConstantsKeys.SequenceNumber)) {
                    seqNumber = obj.getInt(ConstantsKeys.SequenceNumber);
                }

                if (i > 0) {
                    isNewDate = false;
                }

                CanadaDutyStatusModel dutyModel = getDutyModel(obj, seqNumber, date, isNewDate);
                if (isSorting) {
                    if (i > 0) {
                        int dayDiff = getDayDiff(lastDateTimeMin, DateTimeWithMins);
                        if (dayDiff == 0) {
                            dotDateWiseList.add(dutyModel);
                        } else {
                            headerViewCount++;
                            Collections.sort(dotDateWiseList);
                            for (int listPos = 0; listPos < dotDateWiseList.size(); listPos++) {
                                dotLogList.add(dotDateWiseList.get(listPos));
                            }
                            dotDateWiseList = new ArrayList<>();
                        }
                    } else {
                        headerViewCount++;
                        dotDateWiseList.add(dutyModel);
                    }
                } else {
                    dotLogList.add(dutyModel);
                }
                lastDateTimeMin = DateTimeWithMins;

            }

            if (dotLogList.size() == 0) {
                Collections.sort(dotDateWiseList);
                for (int listPos = 0; listPos < dotDateWiseList.size(); listPos++) {
                    dotLogList.add(dotDateWiseList.get(listPos));
                }
            }

            if (dotLogList.size() > 0) {
                dotLogList.get(dotLogList.size() - 1).setHeaderViewCount(headerViewCount);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private CanadaDutyStatusModel getDutyModel(JSONObject obj, int seqNumber, Date date, boolean IsNewDate) {
        try {
            //String DateTimeWithMins = ;
            /*if(DateTimeWithMins.length() > 19){
                DateTimeWithMins = DateTimeWithMins.substring(0, 19);
            }*/

            int EventType = obj.getInt(ConstantsKeys.EventType);
            int EventCode = obj.getInt(ConstantsKeys.EventCode);
            String remarks = obj.getString(ConstantsKeys.Remarks);

            if (EventType == 21) {
                if (EventCode == 1) {
                    remarks = "Cycle 1 (7 days)";
                } else if (EventCode == 2) {
                    remarks = "Cycle 2  (14 days)";
                } else if (EventCode == 3) {
                    remarks = "US (60/7)";
                } else if (EventCode == 4) {
                    remarks = "US (70/8)";
                } else {
                    remarks = "United State";
                }
            }


            CanadaDutyStatusModel dutyModel = new CanadaDutyStatusModel(
                    CheckDateFormat(obj.getString(ConstantsKeys.DateTimeWithMins)),
                    CheckDateFormat(obj.getString(ConstantsKeys.EventUTCTimeStamp)),
                    CheckNullBString(obj.getString(ConstantsKeys.DriverStatusID)),

                    EventType,
                    EventCode,
                    CheckNullBString(obj.getString(ConstantsKeys.DutyMinutes)),

                    CheckNullBString(obj.getString(ConstantsKeys.Annotation)),
                    CheckNullBString(obj.getString(ConstantsKeys.EventDate)),
                    CheckNullBString(obj.getString(ConstantsKeys.EventTime)),
                    CheckNullBString(obj.getString(ConstantsKeys.AccumulatedVehicleKm)),
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
                    CheckNullBString(remarks),
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
                    date,
                    CheckDateFormat(obj.getString(ConstantsKeys.EditDateTime)),
                    CheckDateFormat(obj.getString(ConstantsKeys.CertifyLogDate)),
                    IsNewDate,
                    0,
                    obj.getBoolean(ConstantsKeys.IsUnidentified)

            );

            return dutyModel;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setMarqueonView(final TextView textView) {

        textView.setHorizontallyScrolling(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine(true);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSelected(true);

    }


    public List<UnAssignedVehicleModel> parseCanadaDotUnIdenfdVehList(JSONArray logArray) {

        List<UnAssignedVehicleModel> dotLogList = new ArrayList<>();

        try {
            for (int i = 0; i < logArray.length(); i++) {
                JSONObject obj = (JSONObject) logArray.get(i);

                String StartDateTime = obj.getString(ConstantsKeys.DriverZoneStartDateTime);
                if (StartDateTime.length() > 19) {
                    StartDateTime = StartDateTime.substring(0, 19);
                }

                DateFormat format = new SimpleDateFormat(Globally.DateFormat, Locale.ENGLISH);
                Date date = format.parse(StartDateTime);


                UnAssignedVehicleModel dutyModel = new UnAssignedVehicleModel(
                        CheckNullBString(obj.getString(ConstantsKeys.UnAssignedVehicleMilesId)),
                        CheckNullBString(obj.getString(ConstantsKeys.AssignedUnidentifiedRecordsId)),
                        CheckNullBString(obj.getString(ConstantsKeys.EquipmentNumber)),
                        CheckNullBString(obj.getString(ConstantsKeys.VIN)),

                        CheckNullBString(obj.getString(ConstantsKeys.strOdometersKm)),
                        CheckNullBString(obj.getString(ConstantsKeys.EndOdometer)),
                        CheckNullBString(obj.getString(ConstantsKeys.TotalMiles)),
                        CheckNullBString(obj.getString(ConstantsKeys.TotalKm)),

                        CheckNullBString(obj.getString(ConstantsKeys.DriverZoneStartDateTime)),
                        CheckNullBString(obj.getString(ConstantsKeys.DriverZoneEndDateTime)),
                        CheckNullBString(obj.getString(ConstantsKeys.StatusId)),

                        CheckNullBoolean(obj.getString(ConstantsKeys.IsIntermediateLog)),

                        CheckNullBString(obj.getString(ConstantsKeys.HexaSeqNumber)),

                        CheckNullBString(obj.getString(ConstantsKeys.StartLocation)),
                        CheckNullBString(obj.getString(ConstantsKeys.EndLocation)),

                        CheckNullBString(obj.getString(ConstantsKeys.StartLatitude)),
                        CheckNullBString(obj.getString(ConstantsKeys.StartLongitude)),

                        CheckNullBString(obj.getString(ConstantsKeys.DutyStatus)),
                        date

                );

                dotLogList.add(dutyModel);
            }

            Collections.sort(dotLogList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dotLogList;
    }


    public String checkNullString(String data) {
        if (data.equals("null") || data.length() == 0) {
            data = "--";
        }
        return data;
    }


    // get Obd odometer Data
    public void saveOdometer(String DriverStatusId, String DriverId, String DeviceId, JSONArray driver18DaysLogArray,
                             OdometerHelperMethod odometerhMethod, HelperMethods hMethods, DBHelper dbHelper, Context context) {

        try {
            int ObdStatus = SharedPref.getObdStatus(context);
            if (ObdStatus == Constants.WIRED_CONNECTED ||
                    ObdStatus == Constants.WIFI_CONNECTED ||
                    ObdStatus == Constants.BLE_CONNECTED) {
                int lastJobStatus = hMethods.getSecondLastJobStatus(driver18DaysLogArray);
                int currentJobStatus = Integer.valueOf(DriverStatusId);

                String odometerValue = SharedPref.getObdOdometer(context);
                if (!odometerValue.equals("0")) {
                    if ((currentJobStatus == ON_DUTY || currentJobStatus == DRIVING) && (lastJobStatus == OFF_DUTY || lastJobStatus == SLEEPER) ||
                            (currentJobStatus == OFF_DUTY || currentJobStatus == SLEEPER) && (lastJobStatus == DRIVING || lastJobStatus == ON_DUTY)) {

                        odometerhMethod.AddOdometerAutomatically(DriverId, DeviceId, odometerValue, DriverStatusId, dbHelper, context);

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    // Save Driver Cycle From OBD data those are getting from als server.
    public void SaveCycleWithCurrentDate(int CycleId, String currentUtcDate, String changeType, Globally global, Context context) {


        try {
            /* ------------- Save Cycle details with time is different with earlier cycle --------------*/
            //String CurrentCycle   = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context );
            String CurrentCycle = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(context), context);

            if (CycleId != 0 && !CurrentCycle.equals("" + CycleId)) {
                JSONArray cycleDetailArray = global.getSaveCycleRecords(CycleId, changeType, context);
                SharedPref.SetCycleOfflineDetails(cycleDetailArray.toString(), context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If active driver is Main Driver
        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            switch (CycleId) {
                case 1:
                    DriverConst.SetDriverCurrentCycle(Globally.CANADA_CYCLE_1_NAME, Globally.CANADA_CYCLE_1, context);
                    break;

                case 2:
                    DriverConst.SetDriverCurrentCycle(Globally.CANADA_CYCLE_2_NAME, Globally.CANADA_CYCLE_2, context);
                    break;

                case 3:
                    DriverConst.SetDriverCurrentCycle(Globally.USA_WORKING_6_DAYS_NAME, Globally.USA_WORKING_6_DAYS, context);
                    break;

                case 4:
                    DriverConst.SetDriverCurrentCycle(Globally.USA_WORKING_7_DAYS_NAME, Globally.USA_WORKING_7_DAYS, context);
                    break;
            }
        } else {
            // If active driver is Co Driver
            switch (CycleId) {
                case 1:
                    DriverConst.SetCoDriverCurrentCycle(Globally.CANADA_CYCLE_1_NAME, Globally.CANADA_CYCLE_1, context);
                    break;

                case 2:
                    DriverConst.SetCoDriverCurrentCycle(Globally.CANADA_CYCLE_2_NAME, Globally.CANADA_CYCLE_2, context);
                    break;

                case 3:
                    DriverConst.SetCoDriverCurrentCycle(Globally.USA_WORKING_6_DAYS_NAME, Globally.USA_WORKING_6_DAYS, context);
                    break;

                case 4:
                    DriverConst.SetCoDriverCurrentCycle(Globally.USA_WORKING_7_DAYS_NAME, Globally.USA_WORKING_7_DAYS, context);
                    break;

            }
        }

        // Save Current Date
        SharedPref.setCurrentDate(currentUtcDate, context);

    }


    public boolean isAllowLocMalfunctionEvent(Context context) {
        return (SharedPref.IsAllowMalfunction(context) || SharedPref.IsAllowDiagnostic(context)) && SharedPref.isLocMalfunctionOccur(context);
    }

    public boolean isLocMalfunctionEvent(Context context, int DriverType) {
        if (DriverType == Constants.MAIN_DRIVER_TYPE) {
            return (SharedPref.IsAllowMalfunction(context) || SharedPref.IsAllowDiagnostic(context)) && SharedPref.isLocMalfunctionOccur(context);
        } else {
            return (SharedPref.IsAllowMalfunctionCo(context) || SharedPref.IsAllowDiagnosticCo(context)) && SharedPref.isLocMalfunctionOccur(context);
        }
    }


    public String getPowerDiaMalOccurredTime(String PowerEventStatus) {
        String occurredTime = "";
        try {
            int minDiff = 0;
            String[] dataArray = PowerEventStatus.split(",");
            if (dataArray.length > 1) {
                minDiff = (int) Double.parseDouble(dataArray[1]);
            }

            DateTime currentDate = Globally.GetCurrentUTCDateTime();
            occurredTime = currentDate.minusMinutes(minDiff).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return occurredTime;
    }


    // ------------- Check Power Diagnostic/Malfunction status ---------------
    public String isPowerDiaMalOccurred(String currentHighPrecisionOdometer, String ignitionStatus,
                                        String obdEngineHours, String DriverId, Globally global,
                                        MalfunctionDiagnosticMethod malfunctionDiagnosticMethod,
                                        boolean isPowerCompMalAllowed, boolean isPowerCompDiaAllowed,
                                        Context context, Constants constants, DBHelper dbHelper,
                                        DriverPermissionMethod driverPermissionMethod, Utils obdUtil) {

        String eventStatus = "";
        try {
            String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, context);
            String lastEngineHour = SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, context);
            String lastodometer = SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, context);


            if (!lastIgnitionStatus.equals("ON")) {

                double engineHrDiffInMin = 0; //, odoDiff = 0;

                String lastSavedTime = SharedPref.GetTruckInfoOnIgnitionChange(Constants.IgnitionTimeMalDia, context);
                if (lastSavedTime.length() > 10) {

                    int secDiff = getSecDifference(lastSavedTime, Globally.GetDriverCurrentDateTime(global, context));   //minDiff(lastSavedTime, global, context);
                    if (secDiff >= 20) {

                        engineHrDiffInMin = getEngineHourDiff(lastEngineHour, obdEngineHours);

                       /* if (isValidFloat(currentHighPrecisionOdometer) && isValidFloat(lastOdometer)) {
                            odoDiff = Float.parseFloat(meterToKm(currentHighPrecisionOdometer)) - Float.parseFloat(meterToKm(lastOdometer));
                        }*/

                        if (engineHrDiffInMin > 4) {

                            SharedPref.SetIgnitionOffCalled(true, context);
                            double totalDuration = 0;
                            double OdometerDiff = 0;

                            double previousDiaEventTime = malfunctionDiagnosticMethod.getLast24HrPwrEvents(lastEngineHour, engineHrDiffInMin, dbHelper);

                            try {
                                OdometerDiff = Double.parseDouble(SharedPref.getObdOdometer(context)) - Double.parseDouble(lastodometer);
                                if (OdometerDiff > 0) {
                                    totalDuration = engineHrDiffInMin + previousDiaEventTime;

                                } else {
                                    totalDuration = engineHrDiffInMin;

                                    // Engine Sync diagnostic event is also occurred because truck was not moving
                                    SharedPref.saveEngSyncEventAlso(true, Globally.GetDriverCurrentDateTime(global, context), context);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // add earlier diagnostic time within 24 hr with current time

                            if (totalDuration >= PowerEngSyncMalOccTime && OdometerDiff > 1) {

                                if (isPowerCompMalAllowed) {

                                    if (SharedPref.isPowerMalfunctionOccurred(context) == false) {
                                        eventStatus = MalfunctionEvent + "," + engineHrDiffInMin;
                                        Globally.PlayNotificationSound(context);
                                        global.ShowLocalNotification(context,
                                                context.getResources().getString(R.string.malfunction_events),
                                                context.getResources().getString(R.string.power_comp_mal_occured), 2093);

                                        // Save power mal status with updated time
                                        SharedPref.savePowerMalfunctionOccurStatus(true,
                                                SharedPref.isPowerDiagnosticOccurred(context), global.GetCurrentUTCTimeFormat(), context);

                                        constants.saveObdData("PowerMalEvent - Ignition- " + lastIgnitionStatus +
                                                        ", CurrEngineHours: " + obdEngineHours + ", LastEngineHour: " + lastEngineHour + ", LastOdometer: " + lastodometer +
                                                        ", Duration: " + totalDuration, "",
                                                "", currentHighPrecisionOdometer,
                                                currentHighPrecisionOdometer, "", ignitionStatus, "", "",
                                                String.valueOf(-1), obdEngineHours, "", "",
                                                DriverId, dbHelper, driverPermissionMethod, obdUtil, context);

                                    }

                                }

                            } else {
                                if (isPowerCompDiaAllowed) {
                                    eventStatus = DiagnosticEvent + "," + engineHrDiffInMin;
                                    Globally.PlayNotificationSound(context);
                                    global.ShowLocalNotification(context,
                                            context.getResources().getString(R.string.dia_event),
                                            context.getResources().getString(R.string.power_dia_occured), 2092);

                                    SharedPref.savePowerMalfunctionOccurStatus(
                                            SharedPref.isPowerMalfunctionOccurred(context),
                                            true, global.GetCurrentUTCTimeFormat(), context);

                                    constants.saveObdData("PowerDiaEvent - Ignition- " + lastIgnitionStatus +
                                                    ", CurrEngineHours: " + obdEngineHours + ", LastEngineHour: " + lastEngineHour + ", LastOdometer: " + lastodometer +
                                                    ", TotalDuration: " + totalDuration + ", PreviousLocDiaTime: " + previousDiaEventTime, "",
                                            "", currentHighPrecisionOdometer,
                                            currentHighPrecisionOdometer, "", ignitionStatus, "", "",
                                            String.valueOf(-1), obdEngineHours, "", "",
                                            DriverId, dbHelper, driverPermissionMethod, obdUtil, context);

                                }

                            }
                        }

                    } else {
                        // save updated values with truck ignition status
                        SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, WiredOBD, global.GetDriverCurrentDateTime(global, context),
                                global.GetCurrentUTCTimeFormat(),
                                SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, context),
                                SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, context),
                                context);

                    }

                } else {
                    // save 1st entry if no data in SharedPref
                    SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, WiredOBD, global.GetDriverCurrentDateTime(global, context),
                            global.GetCurrentUTCTimeFormat(), SharedPref.getObdEngineHours(context),
                            SharedPref.getObdOdometer(context), context);
                }

            } else {
                // save updated values with truck ignition status
                SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, WiredOBD, global.GetDriverCurrentDateTime(global, context),
                        global.GetCurrentUTCTimeFormat(), SharedPref.getObdEngineHours(context),
                        SharedPref.getObdOdometer(context), context);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventStatus;
    }


    public void refreshEventDataFromService(Context context) {
        try {
            Constants.isCallMalDiaEvent = true;
            SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, context);

            // call service onStart command to call event data to refresh
            Intent serviceIntent = new Intent(context, BackgroundLocationService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            }
            context.startService(serviceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // saving location with time info to calculate location malfunction event
    public void saveEcmLocationWithTime(String latitude, String longitude, String odo, Context context) {

        String odometer;
        if (odo.length() > 1) {
            odometer = odo;
        } else {
            odometer = SharedPref.getHighPrecisionOdometer(context);
        }

        if (latitude.length() > 4) {
            SharedPref.setEcmObdLocationWithTime(latitude, longitude, odometer,
                    Globally.GetDriverCurrentDateTime(new Globally(), context), Globally.GetCurrentUTCTimeFormat(), context);
        } else {
            if (SharedPref.getEcmObdLatitude(context).length() > 4) {

                SharedPref.setEcmObdLocationWithTime("0", "0", odometer,
                        Globally.GetDriverCurrentDateTime(new Globally(), context), Globally.GetCurrentUTCTimeFormat(), context);
            }
        }
    }


    public float getEngineHourDiff(String lastEngineHour, String currentEngineHour) {
        float engineHourDiff = 0;

        if (isValidFloat(lastEngineHour) && isValidFloat(currentEngineHour)) {
            float lastEngineHrFloat = Float.parseFloat(lastEngineHour) * 60;
            float currentEngineHrFloat = Float.parseFloat(currentEngineHour) * 60;

            if (lastEngineHrFloat != 0 && currentEngineHrFloat != 0) {
                engineHourDiff = currentEngineHrFloat - lastEngineHrFloat;
            }

        }

        return engineHourDiff;
    }


    public boolean isMalfunction(String EventCode) {
        if (EventCode.equals(Constants.PowerComplianceMalfunction) || EventCode.equals(Constants.EngineSyncMalfunctionEvent) ||
                EventCode.equals(Constants.PositionComplianceMalfunction) || EventCode.equals(Constants.DataRecordingComplianceMalfunction)) {
            return true;
        } else {
            return false;
        }
    }


    public void saveObdData(String source, String vin, String eventData, String HighPrecisionOdometer,
                            String obdOdometerInMeter, String correctedData, String ignition, String rpm,
                            String speed, String speedCalculated, String EngineHours, String timeStamp,
                            String previousDate, String DriverId, DBHelper dbHelper,
                            DriverPermissionMethod driverPermissionMethod, Utils obdUtil, Context context) {

        boolean isDeviceLogEnabled = driverPermissionMethod.isDeviceLogEnabled(DriverId, dbHelper);

        if (isDeviceLogEnabled || DriverId.equals("0")) {

            JSONObject obj = new JSONObject();
            try {

                if (source.contains("Ble-")) {
                    obj.put(EventData, source);

                } else {
                    obj.put(obdSource, source);
                    if (eventData.length() > 0) {
                        obj.put(obdOdometer, eventData);
                    }
                    obj.put(obdHighPrecisionOdo, HighPrecisionOdometer);

                    if (source.equals(Constants.WifiOBD)) {

                        obj.put(WheelBasedVehicleSpeed, speed);

                        if (correctedData.trim().length() > 0) {
                            obj.put(CorrectedData, correctedData);
                        }

                        try {
                            String[] array = obdOdometerInMeter.split(",  ");
                            if (array.length > 0) {
                                obj.put(PreviousLogDate, previousDate);
                                obj.put(CurrentLogDate, Globally.GetDriverCurrentDateTime(new Globally(), context));

                                obj.put(DecodedData, array[0]);

                                if (array.length > 1 && !array[1].equals("-1")) {
                                    obj.put(obdCalculatedSpeed, array[1]);
                                }
                            } else {
                                obj.put(CurrentLogDate, Globally.GetDriverCurrentDateTime(new Globally(), context));
                                obj.put(DecodedData, obdOdometerInMeter);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {

                        if (source.equals(ApiData) || source.equals(OfflineData)) {
                            obj.put(obdDetail, obdOdometerInMeter);
                            obj.put(LastRecordTime, timeStamp);
                        } else {
                            obj.put(CurrentLogDate, Globally.GetDriverCurrentDateTime(new Globally(), context));
                        }

                        if (!vin.contains("BLE - ConnectionError")) {
                            if (speedCalculated.length() > 0 && !speedCalculated.equals("-1")) {
                                obj.put(calculatedSpeed, speedCalculated);
                            }
                            obj.put(obdSpeed, speed);
                        }

                        obj.put(obdVINNumber, vin);

                    }

                    if (!vin.contains("BLE - ConnectionError")) {
                        obj.put(obdEngineHours, EngineHours);
                        obj.put(obdIgnitionStatus, ignition);
                        obj.put(obdRPM, rpm);
                        obj.put(ConstantsKeys.Latitude, Globally.LATITUDE);
                        obj.put(ConstantsKeys.Longitude, Globally.LONGITUDE);
                    }
                }

                Globally.OBD_DataArray.put(obj);
                obdUtil.writeToLogFile(obj.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public void saveAppLog(String performanceDesc, String DriverId, DBHelper dbHelper,
                            DriverPermissionMethod driverPermissionMethod, Utils obdUtil) {

        boolean isDeviceLogEnabled = driverPermissionMethod.isDeviceLogEnabled(DriverId, dbHelper);

        if (isDeviceLogEnabled || DriverId.equals("0")) {

            JSONObject obj = new JSONObject();
            try {
                obj.put(EventData, performanceDesc);

                obdUtil.writeToLogFile(obj.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }



    public void saveBleLog(String data, String timeStamp, Context context, DBHelper dbHelper,
                           DriverPermissionMethod driverPermissionMethod, Utils obdUtil) {

        String DriverId = SharedPref.getDriverId(context);
        boolean isDeviceLogEnabled = driverPermissionMethod.isDeviceLogEnabled(DriverId, dbHelper);

        if (isDeviceLogEnabled || DriverId.equals("0")) {

            JSONObject obj = new JSONObject();
            try {

                obj.put(EventData, data);
                obj.put(FileWriteTime, timeStamp);

                Globally.OBD_DataArray.put(obj);
                obdUtil.writeToLogFile(obj.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public Animation confirmAnimationInitilization(Animation anim, Context context) {
        //if(anim == null) {
        anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        anim.setDuration(1500);
        // }
        return anim;
    }

    public static Duration getDateTimeDuration(DateTime selectedDateTime, DateTime currentTime) {
        Duration duration = new Duration(selectedDateTime, currentTime);
        return duration;
    }



    public static int getTimeDiffInMin(String savedEventTime, DateTime currentDateTime){
        int minDiff = 0;

        try {
            DateTime savedEventDateTime = Globally.getDateTimeObj(savedEventTime, false);
            minDiff = (int) getDateTimeDuration(savedEventDateTime, currentDateTime).getStandardMinutes();

            /*
             int dayInMinDuration = 1440;
            int dayDiff = DayDiff(currentDateTime, savedEventDateTime);
            if(dayDiff > 0){
                minDiff = dayDiff * dayInMinDuration;
            }else {
                minDiff = (int) getDateTimeDuration(savedEventDateTime, currentDateTime).getStandardMinutes();
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return minDiff;
    }


    public static int DayDiff(DateTime currentDate, DateTime oldDate){
        long diffInMillis = currentDate.getMillis() - oldDate.getMillis();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = diffInMillis / daysInMilli;
        return (int) elapsedDays;
    }

    public JSONArray checkNullArray(JSONObject dataObj, String key) {
        JSONArray array = new JSONArray();
        try {
            if (!dataObj.getString(key).equals("null")) {
                array = new JSONArray(dataObj.getString(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return array;

    }


    // CT-PAT Agriculture inspection list
    public List<PrePostModel> CtPatAgricultureList() {
        List<PrePostModel> agricultureList = new ArrayList<>();
        PrePostModel model;

        model = new PrePostModel("101", "Area Of Inspection");
        agricultureList.add(model);

      /*  model = new PrePostModel("102", "Container Identification");
        agricultureList.add(model);
*/
        model = new PrePostModel("103", "Conveyance Clean Before Loading");
        agricultureList.add(model);

        model = new PrePostModel("104", "Check Insects And Rodents");
        agricultureList.add(model);

        model = new PrePostModel("105", "Vacuum Pressure Wash");
        agricultureList.add(model);

        model = new PrePostModel("106", "WPM Compliant IPPC Logos");
        agricultureList.add(model);

        return agricultureList;

    }



   /* public Duration getDateTimeDurationPublic(DateTime selectedDateTime, DateTime currentTime){
        Duration duration = new Duration(selectedDateTime, currentTime);
        return duration;
    }*/

    // ------------- Malfunction status ---------------
    // x = when no loc or valid position for 8 km
    // m = If driver enter manual loc after position malfunction occur
    // e = if position malfunction occur more then 60 min

    public String isPositionMalfunctionEvent(String DriverId, MalfunctionDiagnosticMethod malfunctionDiagnosticMethod, DBHelper dbHelper,
                                             DriverPermissionMethod driverPermissionMethod, Utils obdUtil, Context context) {
        String PositionComplianceStatus = "";
        int ObdStatus = SharedPref.getObdStatus(context);
        try {
            if (ObdStatus == Constants.WIRED_CONNECTED ||
                    ObdStatus == Constants.WIFI_CONNECTED ||
                    ObdStatus == Constants.BLE_CONNECTED) {

                if (SharedPref.getEcmObdLatitude(context).length() < 5) {

                    String currentOdometer = SharedPref.getHighPrecisionOdometer(context);
                    String lastOdometer = SharedPref.getEcmOdometer(context);
                    boolean isLocDiagnosticOccurred = SharedPref.isLocDiagnosticOccur(context);

                    if (lastOdometer.length() > 1) {
                        double odometerDistance = Double.parseDouble(currentOdometer) - Double.parseDouble(lastOdometer);
                        odometerDistance = odometerDistance / 1000;

                        if (odometerDistance >= 8 || isLocDiagnosticOccurred) {

                            boolean isLastRecordCleared = malfunctionDiagnosticMethod.isLastRecordCleared(dbHelper);
                            boolean isLocMissingCounterAlreadyActive = ((odometerDistance < 8 || isLocDiagnosticOccurred) && isLastRecordCleared);

                            if (!isLocDiagnosticOccurred || isLocMissingCounterAlreadyActive) { // Save Position malfunction event status if earlier status was false
                                SharedPref.saveLocDiagnosticStatus(true, Globally.GetDriverCurrentDateTime(new Globally(), context),
                                        Globally.GetCurrentUTCTimeFormat(), context);

                                saveObdData("Wired", "LocationEvent-OdometerDistance: " + odometerDistance, "LocationDiagnostic",
                                        lastOdometer, currentOdometer, malfunctionDiagnosticMethod.getPositioningMalDiaArray(dbHelper).toString(), "", "", "",
                                        "", obdEngineHours, Globally.GetDriverCurrentDateTime(new Globally(), context), "",
                                        DriverId, dbHelper, driverPermissionMethod, obdUtil, context);

                                PositionComplianceStatus = "D";

                                // update array for last 24 hours only before add new event
                                malfunctionDiagnosticMethod.clearEventsMoreThen1Day(dbHelper, context);

                                String ObdEngineHours = Constants.get2DecimalEngHour(context); //SharedPref.getObdEngineHours(context);
                                String ObdOdometer = SharedPref.getObdOdometer(context);

                                JSONArray array = malfunctionDiagnosticMethod.AddNewItemInPositionArray(DriverId, ConstLocationMissing,
                                        ObdEngineHours, ObdOdometer, "X", "",
                                        SharedPref.getVINNumber(context), dbHelper, context);
                                malfunctionDiagnosticMethod.PositioningMalDiaHelper(dbHelper, array);

                            }

                            if (SharedPref.isLocMalfunctionOccur(context) == false) {
                                DateTime malfunctionOccurTime = Globally.getDateTimeObj(SharedPref.getLocDiagnosticOccuredTime(context), false);
                                DateTime currentTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), new Globally(), context);
                                double minDiff = getDateTimeDuration(malfunctionOccurTime, currentTime).getStandardMinutes();


                                double previousOccEventTime = malfunctionDiagnosticMethod.getLast24HourLocDiaEventsInMin(dbHelper, context);
                                double TotalMinDiff = minDiff + previousOccEventTime;

                                if (TotalMinDiff >= PositioningMalOccTime) {

                                    saveObdData("Wired", "LocationEvent-OdometerDistance: " + odometerDistance + ", minDiff: " + minDiff +
                                                    ", malfunctionOccurTime: " + malfunctionOccurTime +
                                                    ", currentTime: " + currentTime, "PositioningMalDiaArray",
                                            lastOdometer, currentOdometer, malfunctionDiagnosticMethod.getPositioningMalDiaArray(dbHelper).toString(), "", "", "",
                                            "", obdEngineHours, Globally.GetDriverCurrentDateTime(new Globally(), context), "",
                                            DriverId, dbHelper, driverPermissionMethod, obdUtil, context);


                                    // Save Position malfunction event status if earlier status was false
                                    SharedPref.saveLocMalfunctionOccurStatus(true, Globally.GetDriverCurrentDateTime(new Globally(), context),
                                            Globally.GetCurrentUTCTimeFormat(), context);
                                    saveMalfncnStatus(context, true);
                                    PositionComplianceStatus = "M";

                                    // clearing pos array after pos mal because we are checking dia for mal only here
                                    malfunctionDiagnosticMethod.PositioningMalDiaHelper(dbHelper, new JSONArray());    //array

                                }
                            }

                        }

                    } else {
                        if (Globally.LATITUDE.length() < 5) {
                          /*  saveObdData("Wired", "SaveInvalidLocationTime-MethodConst", "", currentOdometer,
                                    currentOdometer, "", "", "", "" ,
                                    "", obdEngineHours, Globally.GetDriverCurrentDateTime(global, context), "",
                                    DriverId, dbHelper, driverPermissionMethod, obdUtil);

*/
                            SharedPref.setEcmObdLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE,
                                    currentOdometer, Globally.GetDriverCurrentDateTime(new Globally(), context), Globally.GetCurrentUTCTimeFormat(), context);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return PositionComplianceStatus;
    }


    public static String getLocationType(Context context) {

        String LocationType = "";
        if (SharedPref.isLocMalfunctionOccur(context)) {
            LocationType = "E";
        } else if (SharedPref.isLocDiagnosticOccur(context)) {
            LocationType = "X";
        }

        return LocationType;
    }


    public void resetMalDiaEvents(Context context) {
        saveMalfncnStatus(context, false);
        saveDiagnstcStatus(context, false);
        SharedPref.saveEngSyncDiagnstcStatus(false, context);
        SharedPref.saveEngSyncMalfunctionStatus(false, context);
        SharedPref.savePowerMalfunctionOccurStatus(false, false, "", context);
        SharedPref.saveLocMalfunctionOccurStatus(false, "", "", context);
        SharedPref.saveMissingDiaStatus(false, context);
        SharedPref.saveMissingDiaStatus(false, context);
        SharedPref.saveTimingMalfunctionStatus(false, "", context);
        SharedPref.SetStorageMalfunctionStatus(false, context);

    }


    public void saveDiagnstcStatus(Context context, boolean isDiagnosticOccur) {
        //  if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
        SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(context),
                SharedPref.isMalfunctionOccur(context),
                isDiagnosticOccur,
                SharedPref.isSuggestedEditOccur(context), context);
        //  }else{
        SharedPref.setEldOccurencesCo(SharedPref.isUnidentifiedOccurCo(context),
                SharedPref.isMalfunctionOccurCo(context),
                isDiagnosticOccur,
                SharedPref.isSuggestedEditOccurCo(context), context);
        // }

    }

    public void saveMalfncnStatus(Context context, boolean isMalfncnOccur) {
        // if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
        SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(context),
                isMalfncnOccur,
                SharedPref.isDiagnosticOccur(context),
                SharedPref.isSuggestedEditOccur(context), context);
     /*   }else{
            SharedPref.setEldOccurencesCo(SharedPref.isUnidentifiedOccurCo(context),
                    isMalfncnOccur,
                    SharedPref.isDiagnosticOccurCo(context),
                    SharedPref.isSuggestedEditOccurCo(context), context);
        }*/

    }

    public void saveSuggestedStatus(Context context, boolean isSuggested){
        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {    // Single Driver Type and Position is 0
            SharedPref.setEldOccurences(
                    SharedPref.isUnidentifiedOccur(context),
                    SharedPref.isMalfunctionOccur(context),
                    SharedPref.isDiagnosticOccur(context),
                    isSuggested, context);
        }else{
            SharedPref.setEldOccurencesCo(
                    SharedPref.isUnidentifiedOccurCo(context),
                    SharedPref.isMalfunctionOccurCo(context),
                    SharedPref.isDiagnosticOccurCo(context),
                    isSuggested, context);
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
            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_TV) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_HIGH) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_280) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_400) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_560) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XXXHIGH) {
                return true;
            }
        } else {
            //Mobile
        }
        return false;
    }


    public static String getSaveStatusOrEditedApi(Context context){
        String SavedLogApi = "";
        if(context != null) {
            if(isEditedLog(context)){
                SavedLogApi = APIs.SAVE_DRIVER_EDIT_LOG_NEW;
            }else{
                SavedLogApi = APIs.SAVE_DRIVER_STATUS;
            }
        }

        return SavedLogApi;
    }

    public static boolean isEditedLog(Context context){
        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            boolean IsMainDriverEdited = SharedPref.IsMainDriverEdited(context);
            return IsMainDriverEdited;
        } else {
            boolean IsCoDriverEditedData = SharedPref.IsCoDriverEditedData(context);
            return IsCoDriverEditedData;
        }
    }


    public static String getSaveApiInCoDriverCase(Context context){
        String SavedLogApi = "";
        if(context != null) {
            boolean isEditedLog;
            if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
                isEditedLog = SharedPref.IsCoDriverEditedData(context);
            } else {
                isEditedLog =  SharedPref.IsMainDriverEdited(context);
            }
            if(isEditedLog){
                SavedLogApi = APIs.SAVE_DRIVER_EDIT_LOG_NEW;
            }else{
                SavedLogApi = APIs.SAVE_DRIVER_STATUS;
            }

        }

        return SavedLogApi;
    }


    public String getErrorMsg(String errorStr){

        if (errorStr.contains("Object reference")) {
            errorStr = "Input data problem. Restart your app and try again.";
        }else if( errorStr.contains("timeout") || errorStr.contains("TimeoutError")){
            errorStr = "Connection timeout. Please try again.";
        }else if (errorStr.contains("NoConnectionError") || errorStr.contains("SSLHandshakeException") ) {
            errorStr = "Internet connection error";
        }else if(errorStr.contains("UnknownHostException")){
            String[] array = errorStr.split(": ");
            if(array.length > 2){
                errorStr = array[1] + ". Please check your internet connection and try again";
            }
        }else if(errorStr.contains("Network")){
            errorStr = "Network connection Error";
        } else if (errorStr.contains("ServerError")) {
            errorStr = "ALS server not responding";
        }

		/*if (errorStr.contains("Object reference")) {
			errorStr = "Invalid Username/Password.";
		}else if( errorStr.contains("timeout")){
			errorStr = "Connection timeout error";
		}else if (errorStr.contains("NoConnectionError") || errorStr.contains("SSLHandshakeException")) {
			errorStr = "Internet connection error";
		}else if(errorStr.contains("Network")){
			errorStr = "Network Error";
		} else if (errorStr.contains("ServerError")) {
			errorStr = "ALS server not responding";
		}*/

        // intilize request queue object again to notify network.
        return errorStr;
    }



    // Duty Status Changes, Intermediate Logs and Special Driving Condition(Personal Use and Yard Move)
    public String getDutyChangeEventName(int EventType, int EventCode, boolean IsPersonal, boolean IsYard) {
        String event = "";
        switch (EventType) {
            case 1:
                if (EventCode == DRIVING) {
                    event = "DR";
                } else if (EventCode == ON_DUTY) {
                    event = "ON";
                } else if (EventCode == SLEEPER) {
                    event = "SB";
                } else {
                    event = "OFF";
                }

                break;

            case 2:
                event = "INT";
                break;

            case 3:
                if (EventCode == 1) {
                    event = "PC Start";
                } else if (EventCode == 2) {
                    event = "YM Start";
                } else if (IsPersonal == true) {
                    event = "PC End";
                } else if (IsYard == true) {
                    event = "YM End";
                }

                break;

            case 4:  // Certify Log
                event = "Certification of RODS";
                break;

            case 5: // Login Logout
                if (EventCode == Login) {
                    event = "Login";
                } else if (EventCode == Logout) {
                    event = "Logout";
                }
                break;

            case 6:  // Engine Up Down
                if (EventCode == EngineUp) {
                    event = "Power Up";
                } else if (EventCode == EngineDown) {
                    event = "Shut Down";
                }
                break;

            case 9:
                if (EventCode == DRIVING || EventCode == INTERMEDIATE) {
                    event = "DR";
                } else if (EventCode == ON_DUTY) {
                    event = "ON";
                } else if (EventCode == OFF_DUTY) {
                    event = "OFF";
                }

                break;

            default:
                event = "" + EventType;
                break;

        }

        return event;
    }


    // Login and Logout, Certification of RODS, Data Diagnostic and Malfunction
    public String getLoginLogoutEventName(int EventType, int EventCode) {
        String event = "";

        switch (EventType) {

            case 4:     // Certify Log
                if (EventCode == 1) {
                    event = "Certification of RODS (1)";
                } else {
                    event = "Re-Certification of RODS (" + EventCode + ")";
                }
                break;

            case 5: // Login Logout
                if (EventCode == Login) {
                    event = "Login";
                } else if (EventCode == Logout) {
                    event = "Logout";
                }
                break;

            case 7: // Diagnostic and Malfunction
                if (EventCode == MalfunctionActive) {
                    event = "Detected";    //"Malfunction (detected)";
                } else if (EventCode == MalfunctionCleared) {
                    event = "Cleared"; //"Malfunction (cleared)";
                } else if (EventCode == DiagnosticActive) {
                    event = "Detected";    //"Data Diagnostic (detected)";
                } else if (EventCode == DiagnisticCleared) {
                    event = "Cleared"; //"Data Diagnostic (cleared)";
                }
                break;


            default:
                event = "" + EventType;
                break;

        }

        return event;

    }


    // Change in Driver's Cycle. Change in Operating Zone, Off Dutty Time Deferral
    public String getCycleOpZoneEventName(int EventType, int EventCode) {
        String event = "";

        switch (EventType) {

            case 21:
                if (EventCode == 1) {
                    event = "Cycle 1 (7 days)";
                } else if (EventCode == 2) {
                    event = "Cycle 2 (14 days)";
                } else if (EventCode == 3) {
                    event = "US (60/7)";
                } else if (EventCode == 4) {
                    event = "US (70/8)";
                } else {
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
    public String getEnginePowerUpDownEventName(int EventType, int EventCode) {
        String event = "";

        switch (EventType) {

            case 6: // Engine Up Down
                if (EventCode == EngineUp) {
                    event = "Power Up";
                } else if (EventCode == EngineDown) {
                    event = "Shut Down";
                }
                break;

            default:
                event = "" + EventCode;
                break;

        }

        return event;
    }


    public ArrayList<UnidentifiedEventModel> getUnidentifiedEventList(String date, int DaysDiff, JSONArray eventArray,
                                                                      Context context) {
        // JSONArray unidentifiedEventArray = new JSONArray();
        ArrayList<UnidentifiedEventModel> unidentifiedEventList = new ArrayList<>();

        boolean isCurrentDay = true;
        if (DaysDiff != 0) {
            isCurrentDay = false;
        }

        try {
            for (int i = 0; i < eventArray.length(); i++) {
                JSONObject obj = (JSONObject) eventArray.get(i);
                int EventType = obj.getInt("EventType");

                if (i == 0) {

                    if (EventType != 9) {

                        if (eventArray.length() > 1) {
                            JSONObject nextObj = (JSONObject) eventArray.get(i + 1);
                            unidentifiedEventList.add(getUnidentifiedModel(date, nextObj.getString(ConstantsKeys.DateTimeWithMins),
                                    9, 1));

                        } else {
                            String endDateTime = date;
                            if (!isCurrentDay) {
                                if (date.length() > 16 && date.substring(11, 16).equals("00:00")) {
                                    endDateTime = date.substring(0, 11) + "23:59:59";
                                }
                            } else {
                                endDateTime = Globally.GetDriverCurrentDateTime(new Globally(), context);
                            }
                            unidentifiedEventList.add(getUnidentifiedModel(date, endDateTime, 9, 1));
                        }

                    } else {
                        unidentifiedEventList.add(getUnidentifiedModel(
                                obj.getString(ConstantsKeys.DateTimeWithMins),
                                obj.getString(ConstantsKeys.EndDateTime),
                                obj.getInt(ConstantsKeys.EventType),
                                obj.getInt(ConstantsKeys.EventCode)));

                    }

                } else {

                    if (date.equals(obj.getString(ConstantsKeys.DateTimeWithMins))) {
                        int pos = unidentifiedEventList.size() - 1;
                        String endDateTime = Globally.GetDriverCurrentDateTime(new Globally(), context);

                        if (!isCurrentDay) {
                            if (date.length() > 16 && date.substring(11, 16).equals("00:00")) {
                                endDateTime = date.substring(0, 11) + "23:59:59";
                            }
                        }

                        UnidentifiedEventModel model = getUnidentifiedModel(
                                unidentifiedEventList.get(pos).getStartTime(),
                                endDateTime,
                                unidentifiedEventList.get(pos).getEventType(),
                                unidentifiedEventList.get(pos).getEventCode());

                        unidentifiedEventList.set(unidentifiedEventList.size() - 1, model);
                        break;
                    } else {
                        if (i == eventArray.length() - 1) {

                            if (isCurrentDay) {

                                unidentifiedEventList.add(getUnidentifiedModel(
                                        obj.getString(ConstantsKeys.DateTimeWithMins),
                                        obj.getString(ConstantsKeys.EndDateTime),
                                        obj.getInt(ConstantsKeys.EventType),
                                        obj.getInt(ConstantsKeys.EventCode)));


                                // add 1 more obj at last to cover up to current time
                                unidentifiedEventList.add(getUnidentifiedModel(
                                        obj.getString(ConstantsKeys.EndDateTime),
                                        Globally.GetDriverCurrentDateTime(new Globally(), context), 9, 1));


                            } else {
                                String endDateTime = date;
                                if (date.length() > 16 && date.substring(11, 16).equals("00:00")) {
                                    endDateTime = date.substring(0, 11) + "23:59:59";
                                }


                                if (obj.getString(ConstantsKeys.EndDateTime).equals(endDateTime)) {
                                    unidentifiedEventList.add(getUnidentifiedModel(
                                            obj.getString(ConstantsKeys.DateTimeWithMins),
                                            endDateTime,
                                            obj.getInt(ConstantsKeys.EventType),
                                            obj.getInt(ConstantsKeys.EventCode)));


                                } else {
                                    unidentifiedEventList.add(getUnidentifiedModel(
                                            obj.getString(ConstantsKeys.DateTimeWithMins),
                                            obj.getString(ConstantsKeys.EndDateTime),
                                            obj.getInt(ConstantsKeys.EventType),
                                            obj.getInt(ConstantsKeys.EventCode)));


                                    // add 1 more obj at last to fill graph upto 24 hour
                                    unidentifiedEventList.add(getUnidentifiedModel(
                                            obj.getString(ConstantsKeys.EndDateTime),
                                            endDateTime, 9, 1));


                                }

                            }
                        } else {
                            JSONObject nextObj = (JSONObject) eventArray.get(i + 1);

                            unidentifiedEventList.add(getUnidentifiedModel(
                                    obj.getString(ConstantsKeys.DateTimeWithMins),
                                    obj.getString(ConstantsKeys.EndDateTime),
                                    obj.getInt(ConstantsKeys.EventType),
                                    obj.getInt(ConstantsKeys.EventCode)));


                            String nextStartTime = nextObj.getString(ConstantsKeys.DateTimeWithMins);
                            String currObjEndTime = obj.getString(ConstantsKeys.EndDateTime);
                            if (!nextStartTime.equals(currObjEndTime)) {

                                // add 1 more obj to fill blank space in graph
                                unidentifiedEventList.add(getUnidentifiedModel(
                                        currObjEndTime,
                                        nextStartTime,
                                        9,
                                        1));


                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return unidentifiedEventList;
    }


    private UnidentifiedEventModel getUnidentifiedModel(String startTime, String endTime,
                                                        int EventType, int EventCode) {
        return new UnidentifiedEventModel(startTime, endTime, EventType, EventCode);
    }


    // calculate speed from wired truck odometers data (in meters) with time difference (in sec)
    public double calculateSpeedFromWiredTabOdometer(String savedTime, String currentDate, String previousHighPrecisionOdometer,
                                                     String currentHighPrecisionOdometer, Context context) {

        double speedInKm = -1;
        double odometerDistance = Double.parseDouble(currentHighPrecisionOdometer) - Double.parseDouble(previousHighPrecisionOdometer);

        if (savedTime.length() > 10) {
            try {
                String timeStampStr = savedTime.replace(" ", "T");
                DateTime savedDateTime = Globally.getDateTimeObj(timeStampStr, false);
                DateTime currentDateTime = Globally.getDateTimeObj(currentDate, false);

                int timeInSecnd = (int) Constants.getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
                //Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();
                speedInKm = (odometerDistance / 1000.0f) / (timeInSecnd / 3600.0f);
                // speedInKm = odometerDistance / timeInSecnd;

            } catch (Exception e) {
                e.printStackTrace();

                // save current HighPrecisionOdometer locally
                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, Globally.GetDriverCurrentDateTime(new Globally(), context), context);

            }

        } else {
            // save current HighPrecisionOdometer locally
            SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, Globally.GetDriverCurrentDateTime(new Globally(), context), context);

        }
        return speedInKm;

    }


    public int minDiff(String savedTime, Globally global, boolean isPcYm, Context context) {

        int timeInMin = 0;
        if (savedTime.length() > 10) {
            try {
                String timeStampStr = savedTime.replace(" ", "T");
                DateTime savedDateTime = global.getDateTimeObj(timeStampStr, false);
                DateTime currentDateTime = global.getDateTimeObj(global.GetDriverCurrentDateTime(global, context), false);

                if (savedDateTime.isAfter(currentDateTime)) {
                    if (isPcYm) {
                        SharedPref.savePcYmAlertCallTime(Globally.GetDriverCurrentDateTime(global, context), context);
                    } else {
                        SharedPref.saveHighPrecisionOdometer(SharedPref.getHighPrecisionOdometer(context), global.GetDriverCurrentDateTime(global, context), context);
                    }
                }
                timeInMin = (int) getDateTimeDuration(savedDateTime, currentDateTime).getStandardMinutes();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (isPcYm) {
                SharedPref.savePcYmAlertCallTime(Globally.GetDriverCurrentDateTime(global, context), context);
            } else {
                SharedPref.saveHighPrecisionOdometer(SharedPref.getHighPrecisionOdometer(context), global.GetDriverCurrentDateTime(global, context), context);
            }
        }
        return timeInMin;

    }


    public int minDiffFromUTC(String savedTime, Globally global, Context context) {

        int timeInMin = 0;
        if (savedTime.length() > 10) {
            try {
                String timeStampStr = savedTime.replace(" ", "T");
                DateTime savedDateTime = global.getDateTimeObj(timeStampStr, false);
                DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), false);

                if (savedDateTime.isAfter(currentDateTime)) {
                    SharedPref.saveHighPrecisionOdometer(SharedPref.getHighPrecisionOdometer(context), global.GetCurrentUTCTimeFormat(), context);
                }
                timeInMin = (int) getDateTimeDuration(savedDateTime, currentDateTime).getStandardMinutes();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return timeInMin;

    }


    public int minDiffMalfunction(String savedTime, Globally global, Context context) {

        int timeInMin = 0;
        if (savedTime.length() > 10) {
            try {
                String timeStampStr = savedTime.replace(" ", "T");
                DateTime savedDateTime = global.getDateTimeObj(timeStampStr, false);
                DateTime currentDateTime = global.getDateTimeObj(global.GetDriverCurrentDateTime(global, context), false);

                if (savedDateTime.isAfter(currentDateTime)) {
                    SharedPref.setMalfCallTime(global.GetDriverCurrentDateTime(global, context), context);
                }
                timeInMin = Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return timeInMin;

    }


    public int secDiffMalfunction(String savedTime, Globally global, Context context) {

        int timeInSec = 0;
        if (savedTime.length() > 10) {
            try {
                String timeStampStr = savedTime.replace(" ", "T");
                DateTime savedDateTime = global.getDateTimeObj(timeStampStr, false);
                DateTime currentDateTime = global.getDateTimeObj(global.GetDriverCurrentDateTime(global, context), false);

                if (savedDateTime.isAfter(currentDateTime)) {
                    SharedPref.setMalfCallTime(global.GetDriverCurrentDateTime(global, context), context);
                }
                // timeInSec = Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();
                timeInSec = Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return timeInSec;

    }


    public int checkIntValue(int value) {
        if (value < 0)
            value = 0;

        return value;
    }

    public int getListNewDateCount(List<CanadaDutyStatusModel> DutyStatusList) {
        int count = 0;
        for (int i = 0; i < DutyStatusList.size(); i++) {
            if (i == 0) {
                count++;
            } else {
                int dayDiff = getDayDiff(DutyStatusList.get(i - 1).getDateTimeWithMins(), DutyStatusList.get(i).getDateTimeWithMins());
                if (dayDiff != 0) {
                    count++;
                }
            }
        }

        return count;
    }


    public JSONArray getEldNotificationReadInput(JSONArray dataArray, String DriverId, String DeviceId) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputArray;
    }


    String addZeroForSingle(Globally Global, int min) {
        String hours = "" + Global.HourFromMin(min);
        if (hours.length() == 1) {
            return "0" + hours;
        } else {
            return hours;
        }
    }

    String addZeroForMinSingle(Globally Global, int min) {
        String hours = "" + Global.MinFromHourOnly(min);
        if (hours.length() == 1) {
            return "0" + hours + " Hrs";
        } else {
            return hours + " Hrs";
        }
    }


    public String CalculateCycleTimeData(Context context, String DriverId, boolean OperatingZoneChange,
                                         boolean isNorth, String changedCycleId,
                                         Globally Global, HelperMethods hMethods, DBHelper dbHelper) {

        int offsetFromUTC = (int) Global.GetDriverTimeZoneOffSet(context);
        List<DriverLog> oDriverLogDetail;

        String finalCycleData = "";
        String currentJobStatus = SharedPref.getDriverStatusId(context);

        DateTime currentUTCTime = Globally.getDateTimeObj(Globally.GetCurrentUTCTimeFormat(), true);
        DateTime currentDateTime = Globally.GetDriverCurrentTime(currentUTCTime.toString(), Global, context);    // Current Date Time
        oDriverLogDetail = hMethods.getSavedLogList(Integer.valueOf(DriverId), currentDateTime, currentUTCTime, dbHelper);

        int rulesVersion = SharedPref.GetRulesVersion(context);
        boolean isHaulExcptn, isAdverseExcptn, IsNorthCanada;
        boolean isSingleDriver = false;

        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            isHaulExcptn = SharedPref.get16hrHaulExcptn(context);
            isAdverseExcptn = SharedPref.getAdverseExcptn(context);
            isSingleDriver = true;
        } else {
            isHaulExcptn = SharedPref.get16hrHaulExcptnCo(context);
            isAdverseExcptn = SharedPref.getAdverseExcptnCo(context);
        }

        if (OperatingZoneChange) {
            IsNorthCanada = isNorth;
        } else {
            IsNorthCanada = SharedPref.IsNorthCanada(context);
        }


        DriverDetail oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DriverId),
                offsetFromUTC, Integer.valueOf(changedCycleId), isSingleDriver, Integer.valueOf(currentJobStatus), false,
                isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                rulesVersion, oDriverLogDetail, context);

        if (changedCycleId.equals(Global.CANADA_CYCLE_1) || changedCycleId.equals(Global.CANADA_CYCLE_2)) {
            oDriverDetail.setCanAdverseException(isAdverseExcptn);
        }
        // EldFragment.SLEEPER is used because we are just checking cycle time
        RulesResponseObject RulesObj = hMethods.CheckDriverRule(Integer.valueOf(changedCycleId), EldFragment.SLEEPER, oDriverDetail);

        // Calculate 2 days data to get remaining Driving/Onduty hours
        RulesResponseObject RemainingTimeObj = hMethods.getRemainingTime(currentDateTime, currentUTCTime, offsetFromUTC,
                Integer.valueOf(changedCycleId), isSingleDriver, Integer.valueOf(DriverId), Integer.valueOf(currentJobStatus), false,
                isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                rulesVersion, dbHelper, context);

        try {
            int CycleRemainingMinutes = checkIntValue((int) RulesObj.getCycleRemainingMinutes());
            int OnDutyRemainingMinutes = checkIntValue((int) RemainingTimeObj.getOnDutyRemainingMinutes());
            int DriveRemainingMin = checkIntValue((int) RemainingTimeObj.getDrivingRemainingMinutes());
            int ShiftRemainingMin = checkIntValue((int) RemainingTimeObj.getShiftRemainingMinutes());

            if (CycleRemainingMinutes < OnDutyRemainingMinutes) {
                OnDutyRemainingMinutes = CycleRemainingMinutes;
            }

            if (ShiftRemainingMin < 0) {
                ShiftRemainingMin = 0;
            }


            String CycleRemaining = addZeroForSingle(Global, CycleRemainingMinutes) + ":" + addZeroForMinSingle(Global, CycleRemainingMinutes);
            String OnDutyRemaining = addZeroForSingle(Global, OnDutyRemainingMinutes) + ":" + addZeroForMinSingle(Global, OnDutyRemainingMinutes);
            String DriveRemaining = addZeroForSingle(Global, DriveRemainingMin) + ":" + addZeroForMinSingle(Global, DriveRemainingMin);
            String ShiftRemaining = addZeroForSingle(Global, ShiftRemainingMin) + ":" + addZeroForMinSingle(Global, ShiftRemainingMin);

            if (UILApplication.getInstance().isNightModeEnabled()) {
                finalCycleData = "<font color='#E3E3E1'>" +
                        "<b>Cycle &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b> :&nbsp;&nbsp; " + CycleRemaining + "<br/>" +
                        "<b>Driving  &nbsp;&nbsp;</b>&nbsp;:&nbsp;&nbsp; " + DriveRemaining + "<br/>" +
                        "<b>OnDuty  &nbsp;</b>&nbsp:&nbsp &nbsp;        " + OnDutyRemaining + "<br/>" +
                        "<b>Shift    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>&nbsp;:&nbsp &nbsp;  " + ShiftRemaining + " </font>";
            } else {
                finalCycleData = "<font color='#3F88C5'>" +
                        "<b>Cycle &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b> :&nbsp;&nbsp; " + CycleRemaining + "<br/>" +
                        "<b>Driving  &nbsp;&nbsp;</b>&nbsp;:&nbsp;&nbsp; " + DriveRemaining + "<br/>" +
                        "<b>OnDuty  &nbsp;</b>&nbsp:&nbsp &nbsp;        " + OnDutyRemaining + "<br/>" +
                        "<b>Shift    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>&nbsp;:&nbsp &nbsp;  " + ShiftRemaining + " </font>";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalCycleData;

    }


    public static int getMinDiff(DateTime StartTime, DateTime EndTime) {
        int dayDiff = Days.daysBetween(StartTime.toLocalDate(), EndTime.toLocalDate()).getDays();

        if (dayDiff > 0) {
            return (int) getDateTimeDuration(StartTime, EndTime).getStandardMinutes();
        } else {
            return EndTime.getMinuteOfDay() - StartTime.getMinuteOfDay();
        }

    }


    /*===== Get Driver Jobs in Array List======= */
    public JSONArray GetDriversSavedArray(Context context, MainDriverEldPref MainDriverPref,
                                          CoDriverEldPref CoDriverPref) {
        int listSize = 0;
        JSONArray DriverJsonArray = new JSONArray();
        List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

        if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            try {
                listSize = MainDriverPref.LoadSavedLoc(context).size();
                tempList = MainDriverPref.LoadSavedLoc(context);
            } catch (Exception e) {
                listSize = 0;
            }
        } else {
            try {
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
                                DriverJsonArray,
                                context
                        );
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DriverJsonArray;
        // Logger.LogDebug("Arraay", "Arraay: " + DriverJsonArray.toString());
    }


    public int OfflineData(Context context, MainDriverEldPref MainDriverPref,
                                          CoDriverEldPref CoDriverPref, boolean isSingleDriver) {
        int offlineLogSize = 0;

            try {
                offlineLogSize = MainDriverPref.LoadSavedLoc(context).size();

                if(offlineLogSize == 0 && !isSingleDriver) {
                    offlineLogSize = CoDriverPref.LoadSavedLoc(context).size();
                }

            } catch (Exception e) {
                offlineLogSize = 0;
            }



        return offlineLogSize;
    }



    public static String getParsedOdometerWithUnit(String Odometer, String Unit) {

        try {
            if (Odometer.length() > 0) {
                if (Unit.equals("Miles")) {
                    Odometer = Constants.meterToMilesWith2DecPlaces(Odometer);
                } else {
                    Odometer = Constants.meterToKmWithObd(Odometer);
                }

                // String odometerInMeter = Constants.meterToKmWithObd(Odometer);
                if (Odometer.contains(".")) {
                    String[] array = Odometer.split("\\.");
                    Odometer = array[0];
                }
            }
        } catch (Exception e) {
        }

        return Odometer;
    }


    public boolean isActionAllowedWithCoDriver(Context context, DBHelper dbHelper, HelperMethods hMethods, Globally Global, String DRIVER_ID) {
        boolean isAllowed = true;
        int ObdStatus = SharedPref.getObdStatus(context);
        if (SharedPref.IsAppRestricted(context)) {
            if ((ObdStatus == Constants.WIRED_CONNECTED ||
                    ObdStatus == Constants.WIFI_CONNECTED ||
                    ObdStatus == Constants.BLE_CONNECTED) &&
                    SharedPref.isVehicleMoving(context)) {
                isAllowed = false;
            }
            boolean isCoDriverInDrYMPC = hMethods.isCoDriverInDrYMPC(context, Global, DRIVER_ID, dbHelper);

            if (isCoDriverInDrYMPC && isAllowed == false) {
                isAllowed = true;
            }
        }
        return isAllowed;
    }


    public boolean isActionAllowed(Context context) {
        boolean isAllowed = true;
        int ObdStatus = SharedPref.getObdStatus(context);
        boolean isVehicleMoving = SharedPref.isVehicleMoving(context);
        if (SharedPref.IsAppRestricted(context)) {
            if ((ObdStatus == Constants.WIRED_CONNECTED || ObdStatus == Constants.WIFI_CONNECTED
                    || ObdStatus == Constants.BLE_CONNECTED) && isVehicleMoving) {
                isAllowed = false;
            } else {
                isAllowed = true;
            }
        }
        return isAllowed;
    }

    public boolean isObdConnected(Context context) {
        boolean isObdConnected = true;
        if (SharedPref.IsAppRestricted(context)) {
            if (SharedPref.getObdStatus(context) == Constants.WIFI_CONNECTED || SharedPref.getObdStatus(context) == Constants.WIRED_CONNECTED
                    || SharedPref.getObdStatus(context) == Constants.BLE_CONNECTED) {
                isObdConnected = true;
            } else {
                isObdConnected = false;
            }
        }

        return isObdConnected;
    }


    public String getObdSource(Context context) {
        if (SharedPref.getObdPreference(context) == Constants.OBD_PREF_BLE) {
            return BleObd;
        } else if (SharedPref.getObdPreference(context) == Constants.OBD_PREF_WIRED) {
            return WiredOBD;
        } else {
            return WifiOBD;
        }
    }

    //  ----------- checking Obd Connection Without aby Restriction-------------------
    public boolean isObdConnectedWithELD(Context context) {
        boolean isObdConnected;
        int ObdStatus = SharedPref.getObdStatus(context);
        if (ObdStatus == WIFI_CONNECTED ||
                ObdStatus == WIRED_CONNECTED ||
                ObdStatus == BLE_CONNECTED) {
            isObdConnected = true;
        } else {
            isObdConnected = false;
        }

        return isObdConnected;
    }

    // this check is used for restrict mode if last status is PC/YM and OBD is not cobbected with ECM, then driver can't status his status
    public boolean isAllowedFromPCYM(int DRIVER_JOB_STATUS, String isPersonal, boolean isYardMove, Context context) {
        if (isObdConnected(context) == false && (
                (DRIVER_JOB_STATUS == OFF_DUTY && isPersonal.equals("true")) ||
                        (DRIVER_JOB_STATUS == ON_DUTY && isYardMove))) {
            return false;
        } else {
            return true;
        }
    }


    public String lastStatusDesc(int DRIVER_JOB_STATUS, boolean isYardMove, Context context) {
        if (DRIVER_JOB_STATUS == ON_DUTY && isYardMove) {
            return context.getResources().getString(R.string.not_change_status_from) + " Yard Move " + context.getResources().getString(R.string.until_not_conn_with_ecm);
        } else {
            return context.getResources().getString(R.string.not_change_status_from) + " Personal Use " + context.getResources().getString(R.string.until_not_conn_with_ecm);
        }
    }

    public boolean isActionAllowedRestricted(Context context) {
        boolean isAllowed = true;
        int ObdStatus = SharedPref.getObdStatus(context);
        boolean isVehicleMoving = SharedPref.isVehicleMoving(context);
        if ((ObdStatus == Constants.WIRED_CONNECTED || ObdStatus == Constants.WIFI_CONNECTED
                || ObdStatus == Constants.BLE_CONNECTED) && isVehicleMoving) {
            isAllowed = false;
        }
        return isAllowed;
    }

    public static boolean isDiagnosticEvent(String EventCode) {
        return (EventCode.equals(MissingDataDiagnostic) || EventCode.equals(PowerComplianceDiagnostic) || EventCode.equals(EngineSyncDiagnosticEvent));
    }


    public boolean isMalDiaAllowed(Context context) {
        try {
            if (SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerComplianceMal, context) ||
                    SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerDataDiag, context) ||
                    SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PostioningComplMal, context) ||
                    SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncMal, context) ||
                    SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncDiag, context)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    public void writeBleOnOffStatus(boolean isTurningOn, Globally global, BleGpsAppLaunchMethod bleGpsAppLaunchMethod,
                                    DBHelper dbHelper, Context context) {
        if (SharedPref.getObdPreference(context) == Constants.OBD_PREF_BLE) {
            // write logs for bluetooth on/off status
            if (isTurningOn) {
                bleGpsAppLaunchMethod.SaveBleGpsAppLogInTable(Constants.LogEventTypeBle, Constants.POWERED_ON,
                        global.getCurrentDateLocalUtc(), dbHelper);
            } else {
                bleGpsAppLaunchMethod.SaveBleGpsAppLogInTable(Constants.LogEventTypeBle, Constants.POWERED_OFF,
                        global.getCurrentDateLocalUtc(), dbHelper);
            }

            // global.isBleEnabled(context)
            SharedPref.SetGpsBlePermission(isTurningOn, SharedPref.WasGpsEnabled(context),
                    SharedPref.GetLocationStatus(context), context);
        }
    }


    @SuppressLint("MissingPermission")
    public void checkBleConnection() {

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startService(Context context) {
        Intent serviceIntent = new Intent(context, BackgroundLocationService.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        }
        context.startService(serviceIntent);
    }


    public static boolean isDeferralOccurred(String DRIVER_ID, String MainDriverId, Context context) {
        int deferralDay = getDeferralDay(DRIVER_ID, MainDriverId, context);

        if (deferralDay != 0) {
            return true;
        } else {
            return false;
        }
    }


    public static int getDeferralDay(String DRIVER_ID, String MainDriverId, Context context) {
        int deferralDay = -1;
        try {
            if (DRIVER_ID.equals(MainDriverId)) {
                String defDay = SharedPref.getDeferralDayMainDriver(context);
                if(defDay.length() > 0)
                    deferralDay = Integer.parseInt(defDay);
            } else {
                String defDay = SharedPref.getDeferralDayCoDriver(context);
                if(defDay.length() > 0)
                    deferralDay = Integer.parseInt(defDay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deferralDay;
    }


    public static int confirmDeferralRuleDays(String DRIVER_ID, String MainDriverId, Context context) {
        DateTime currentDate = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), new Globally(), context);

        int newDeferralDayValue = -1;
        int deferralDay;
        String deferralDate;

        try {
            if (DRIVER_ID.equals(MainDriverId)) {
                deferralDay = Integer.parseInt(SharedPref.getDeferralDayMainDriver(context));
                deferralDate = SharedPref.getDeferralDateMainDriver(context);
            } else {
                deferralDay = Integer.parseInt(SharedPref.getDeferralDayCoDriver(context));
                deferralDate = SharedPref.getDeferralDateCoDriver(context);
            }

            if (deferralDate.length() > 15) {
                DateTime deferralDateTime = Globally.getDateTimeObj(deferralDate, false);
                int daysDiff = Days.daysBetween(deferralDateTime.toLocalDate(), currentDate.toLocalDate()).getDays();

                if (daysDiff == 1) {
                    if (deferralDay == 1) {
                        newDeferralDayValue = 2;
                    } else if (deferralDay == 2) {
                        newDeferralDayValue = 0;
                    }
                } else if (daysDiff > 1) {
                    newDeferralDayValue = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDeferralDayValue;

    }


    public int getCurrentDeferralDayCount(String DRIVER_ID, String MainDriverId, Context context) {
        DateTime currentDate = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), new Globally(), context);

        int deferralDay = -1;
        String deferralDate;

        try {
            if (DRIVER_ID.equals(MainDriverId)) {
                deferralDay = Integer.parseInt(SharedPref.getDeferralDayMainDriver(context));
                deferralDate = SharedPref.getDeferralDateMainDriver(context);
            } else {
                deferralDay = Integer.parseInt(SharedPref.getDeferralDayCoDriver(context));
                deferralDate = SharedPref.getDeferralDateCoDriver(context);
            }

            if (deferralDate.length() > 15) {
                DateTime deferralDateTime = Globally.getDateTimeObj(deferralDate, false);
                int daysDiff = Days.daysBetween(deferralDateTime.toLocalDate(), currentDate.toLocalDate()).getDays();


                if (daysDiff > 0 && deferralDay == 2) {
                    deferralDay++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deferralDay;

    }


    public static void copyTextToClipboard(Context context, String url) {
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("URL", url);
            clipboard.setPrimaryClip(clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isInfoMissing(UnIdentifiedRecordModel recordModel) {
        boolean isInfoMissing = false;

        if (recordModel != null) {
            if (recordModel.getStartOdometer().equals("") ||
                    recordModel.getEndOdometer().equals("") ||
                    recordModel.getStartLocation().equals("") ||
                    recordModel.getEndLocation().equals("")) {
                isInfoMissing = true;
            }
        }

        return isInfoMissing;
    }

    // reset OBD values after truck change or OBD preference changes
    public void resetObdValues(Context context) {
        try {
            if (context != null) {
                SharedPref.saveHighPrecisionOdometer("0", "", context);
                SharedPref.SetObdOdometer("0", context);
                SharedPref.SetObdEngineHours("0", context);
                SharedPref.SaveTruckInfoOnIgnitionChange("", "", "", "", "0", "0", context);
                SharedPref.SaveBleOBDMacAddress("", context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void openAppOnPlayStore(Context context) {
        try {
            String appPackageName = context.getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            intent.setPackage("com.android.vending");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void TurnOffDeviceBluetooth(Context context) {

        try {
            BluetoothAdapter mBTAdapter = null;
            // BT check
            BluetoothManager manager = BleUtil.getManager(context);
            if (manager != null) {
                mBTAdapter = manager.getAdapter();
            }

            if (mBTAdapter != null && mBTAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mBTAdapter.disable();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
