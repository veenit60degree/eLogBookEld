package com.background.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.als.logistic.EldActivity;
import com.als.logistic.Globally;
import com.als.logistic.LoginActivity;
import com.als.logistic.R;
import com.als.logistic.TabAct;
import com.als.logistic.UILApplication;
import com.als.logistic.fragment.EldFragment;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ble.util.BleUtil;
import com.ble.util.ConstantEvent;
import com.ble.util.EventBusInfo;
import com.constants.APIs;
import com.constants.AsyncResponse;
import com.constants.CheckConnectivity;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.DriverLogResponse;
import com.constants.Logger;
import com.constants.RequestResponse;
import com.constants.SaveDriverLogPost;
import com.constants.SaveLogJsonObj;
import com.constants.SharedPref;
import com.constants.ShellUtils;
import com.constants.ShippingPost;
import com.constants.Slidingmenufunctions;
import com.constants.SyncDataUpload;
import com.constants.TcpClient;
import com.constants.Utils;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.htstart.htsdk.HTBleSdk;
import com.htstart.htsdk.bluetooth.HTBleData;
import com.htstart.htsdk.bluetooth.HTBleDevice;
import com.local.db.BleGpsAppLaunchMethod;
import com.local.db.CTPatInspectionMethod;
import com.local.db.CertifyLogMethod;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DeferralMethod;
import com.local.db.DriverPermissionMethod;
import com.local.db.FailedApiTrackMethod;
import com.local.db.HelperMethods;
import com.local.db.InspectionMethod;
import com.local.db.LatLongHelper;
import com.local.db.LocationMethod;
import com.local.db.MalfunctionDiagnosticMethod;
import com.local.db.NotificationMethod;
import com.local.db.OdometerHelperMethod;
import com.local.db.RecapViewMethod;
import com.local.db.ShipmentHelperMethod;
import com.local.db.SyncingMethod;
import com.local.db.UpdateLogRecordMethod;
import com.local.db.VehiclePowerEventMethod;
import com.models.EldDataModelNew;
import com.notifications.NotificationManagerSmart;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.EldCoDriverLogPref;
import com.shared.pref.EldSingleDriverLogPref;
import com.shared.pref.MainDriverEldPref;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.wifi.settings.WiFiConfig;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import dal.tables.OBDDeviceData;
import models.RulesResponseObject;
import obdDecoder.Decoder;

// 27 jan 2021   --
public class BackgroundLocationService extends Service implements TextToSpeech.OnInitListener {

    String TAG = "Service";
    String TAG_OBD = "OBD Service";
    String noObd = "obd not connected";
    String ClearEventType = "";
    String SelectedDriverId = "";
    Intent locServiceIntent;

    String obdEngineHours = "0", currentHighPrecisionOdometer = "0", obdTripDistance = "0", ignitionStatus = "OFF",
            HighResolutionDistance = "0", truckRPM = "0";   // obdOdometer = "0",

    int GPSSpeed = 0;
    int timeInSec = -1;
    int timeDuration = 4000;
    int dayInMinDuration = 1440;

    int SecondDriver                        = 0;
    final int UpdateOffLineStatus           = 2;
    final int SaveShippingOffline           = 3;

    final int SaveOdometerOffline           = 4;
    final int GetOdometers18Days            = 6;
    final int SaveCertifyLog                = 7;
    final int SaveCertifyLogCo              = 70;
    final int SaveDriverLog                 = 8;
    final int SaveInspectionMain            = 9;
    final int SaveInspectionCo              = 10;
    final int SaveCtPatInspMain             = 11;
    final int SaveCtPatInspCo               = 12;
    final int GetCtPat18DaysMainDriverLog   = 13;
    final int GetCtPat18DaysCoDriverLog     = 14;
    final int SaveMalDiagnstcEvent          = 16;
    final int SaveDeferralMain              = 17;
    final int SaveDeferralCo                = 18;
    final int GetMalDiaEventDuration        = 20;
    final int ClearMalDiaEvent              = 21;
    final int SaveAgricultureAddress        = 22;
    final int SaveGpsEventLog               = 23;
    final int SaveBleEventLog               = 24;
    final int SaveVehPwrEventLog            = 25;
    final int GetDriverLog                  = 26;
    final int GetDriverLog18Days            = 27;
    final int GetCoDriverLog18Days          = 28;

    final int GetRecapViewFlagMain          = 111;
    final int GetRecapViewFlagCo            = 112;
    final int MainDriverNotification        = 113;
    final int CoDriverNotification          = 114;

    final int SaveMainDriverLogData         = 1002;
    final int SaveCoDriverLogData           = 1003;
    final int Save2ndDriverOdoData          = 101;
    int offSetFromServer                    = 0;
    int offsetFromUTC                       = 0;
    int recapApiAttempts                    = 0;
    int updateOfflineApiRejectionCount      = 0;
    int updateOfflineNoResponseCount        = 0;
    int RePostDataCountMain                 = 0;
    int RePostDataCountCo                   = 0;

    int SpeedCounter                        = 10;      // initially it is 10 so that ELD rule is called instantly
    int MaxSpeedCounter                     = 60;
    int HalfSpeedCounter                    = 30;
    int ignitionOffCount                    = 0;
    int onReceiveTotalCountOnReq            = 0;


    public static int obdVehicleSpeed       = -1;

    int LocRefreshTime = 10;
    int CheckInternetConnection = 2;
    private final long M_TIMER_UPDATES = 10 * 1000;   // 10 sec

    VolleyRequest GetOdometerRequest, ctPatInsp18DaysRequest;   //, saveDriverDeviceUsageLog;
    VolleyRequest UpdateLocReqVolley, UpdateUserStatusVolley, GetLog18DaysRequest, GetRecapView18DaysData, SaveMalDiaEventRequest;
    VolleyRequest GetMalfunctionEvents, SaveAgricultureRequest, GetLogRequest;
    Map<String, String> params;
    String DriverId = "", CoDriverId = "", CoDriverName = "", DeviceId = "", VIN_NUMBER = "", VehicleId = "";
    String ObdRestarted = "OBD Restarted";

    int DriverType = 0, LastObdSpeed = -1;
    boolean isStopService = false,  RestartObdFlag = false, isTeamDriverLogin = false, isDriverLogout = false;
    JSONArray shipmentArray, odometerArray, driverLogArray;
    DBHelper dbHelper;
    HelperMethods hMethods;
    ShipmentHelperMethod shipmentHelper;
    OdometerHelperMethod odometerHelper;
    RecapViewMethod recapViewMethod;
    InspectionMethod inspectionMethod;
    CTPatInspectionMethod ctPatInspectionMethod;
    LocationMethod LocMethod;
    LatLongHelper latLongHelper;
    DriverPermissionMethod driverPermissionMethod;
    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
    DeferralMethod deferralMethod;
    BleGpsAppLaunchMethod bleGpsAppLaunchMethod;
    VehiclePowerEventMethod vehiclePowerEventMethod;
    FailedApiTrackMethod failedApiTrackMethod;

    CheckConnectivity checkConnectivity;

    Globally global;

    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;
    NotificationManagerSmart mNotificationManager;
    NotificationMethod notificationMethod;
    Constants constants;
    ShippingPost postRequest;
    ServiceCycle serviceCycle;
    public static boolean IsRecapApiCalled = false;
    boolean IsCtPatApiCalled = false;
    boolean Is18DaysLogApiCalled = false;
    boolean isMalfncDataAlreadyPosting = false;
    boolean isDeferralAlreadyPosting = false;
    boolean isDeferralAlreadyPostingCo = false;
    boolean IsMissingDiaInProgress = false;
    boolean MalDiaEventsApiInProcess = false;
    public static boolean isReceiverInit = false;

    boolean isWiredObdRespond = false;
    public static boolean IsAutoChange = false; //, IsAutoLogSaved = false;

    private Handler mHandler = new Handler();
    private Handler speedAlertHandler = new Handler();
    private Handler dismissAlertHandler = new Handler();

    File locDataFile;
    private TextToSpeech tts;
    String ViolationReason = "";
    SaveDriverLogPost saveDriverLogPost, saveNotificationReq;
    SaveLogJsonObj saveEventLogPost;
    CertifyLogMethod certifyLogMethod;
    UpdateLogRecordMethod logRecordMethod;
    TcpClient tcpClient;
    OBDDeviceData data;
    Decoder decoder;
    WiFiConfig wifiConfig;
    SyncingMethod syncingMethod;
    JSONArray savedSyncedArray;
    JSONArray defMainDriverArray = new JSONArray();
    JSONArray defCoDriverArray = new JSONArray();
    JSONArray powerEventInputArray = new JSONArray();
    JSONArray twoDaysLogArray = new JSONArray();

    DateTime yesterdayDate = new DateTime();
    int IsLogIdMissing = -1;

    File syncingFile = new File("");
    File ViolationFile = new File("");


    // ---------- Wired OBD Client setup ----------

    String ServerPackage = "com.als.obd";
    String ServerService = "com.als.obd.services.MainService";
    private ShellUtils.CommandResult obdShell;


    Utils obdUtil;
    private Messenger messenger = null; //used to make an RPC invocation
    private boolean isBound = false;
    private ServiceConnection connection;//receives callbacks from bind and unbind invocations
    private Messenger replyTo = null; //invocation replies are processed by this Messenger
    String AlsSendingData = "";
    String AlsReceivedData = "";
    String MobileUsage = "";
    String TotalUsage = "";
    String PreviousLatitude = "", PreviousLongitude = "", PreviousOdometer = "";
    double tempOdo = 1180321279;  //1.090133595E9
    double tempEngHour = 22999.95;
    int tempSpeed = 0;
    int ignitionCount = 0;


   private BroadcastReceiver mMessageReceiver = null;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();

        Logger.LogInfo(TAG, "---------onCreate BackgroundService");

        global                  = new Globally();
        dbHelper                = new DBHelper(getApplicationContext());
        hMethods                = new HelperMethods();
        shipmentHelper          = new ShipmentHelperMethod();
        odometerHelper          = new OdometerHelperMethod();
        recapViewMethod         = new RecapViewMethod();
        LocMethod               = new LocationMethod();
        latLongHelper           = new LatLongHelper();
        syncingMethod           = new SyncingMethod();
        driverPermissionMethod  = new DriverPermissionMethod();
        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();
        deferralMethod          = new DeferralMethod();
        bleGpsAppLaunchMethod   = new BleGpsAppLaunchMethod();
        vehiclePowerEventMethod = new VehiclePowerEventMethod();
        failedApiTrackMethod    = new FailedApiTrackMethod();

        checkConnectivity       = new CheckConnectivity(getApplicationContext());
        MainDriverPref          = new MainDriverEldPref();
        CoDriverPref            = new CoDriverEldPref();
        constants               = new Constants();
        serviceCycle            = new ServiceCycle(getApplicationContext());
        postRequest             = new ShippingPost(getApplicationContext(), requestResponse);
        UpdateLocReqVolley      = new VolleyRequest(getApplicationContext());
        UpdateUserStatusVolley  = new VolleyRequest(getApplicationContext());
        GetRecapView18DaysData  = new VolleyRequest(getApplicationContext());
        GetLog18DaysRequest     = new VolleyRequest(getApplicationContext());
        SaveMalDiaEventRequest  = new VolleyRequest(getApplicationContext());
        GetMalfunctionEvents    = new VolleyRequest(getApplicationContext());
        SaveAgricultureRequest  = new VolleyRequest(getApplicationContext());
        GetLogRequest           = new VolleyRequest(getApplicationContext());
        mNotificationManager    = new NotificationManagerSmart(getApplicationContext());
        notificationMethod      = new NotificationMethod();

     //   saveDriverDeviceUsageLog= new VolleyRequest(getApplicationContext());
        GetOdometerRequest      = new VolleyRequest(getApplicationContext());
        ctPatInsp18DaysRequest  = new VolleyRequest(getApplicationContext());
        inspectionMethod        = new InspectionMethod();
        ctPatInspectionMethod   = new CTPatInspectionMethod();
        logRecordMethod         = new UpdateLogRecordMethod();
        certifyLogMethod        = new CertifyLogMethod();

        saveDriverLogPost       = new SaveDriverLogPost(getApplicationContext(), saveLogRequestResponse);
        saveNotificationReq     = new SaveDriverLogPost(getApplicationContext(), saveLogRequestResponse);

        saveEventLogPost        = new SaveLogJsonObj(getApplicationContext(), saveEventRequestResponse );


        data                    = new OBDDeviceData();
        decoder                 = new Decoder();
        wifiConfig              = new WiFiConfig();
        tcpClient               = new TcpClient(obdResponseHandler);
        tts                     = new TextToSpeech(getApplicationContext(), this);

        mTimer                  = new Timer();
        mTimer.schedule(timerTask, M_TIMER_UPDATES, M_TIMER_UPDATES);

        DeviceId                = SharedPref.GetSavedSystemToken(getApplicationContext());
        SharedPref.setServiceOnDestoryStatus(false, getApplicationContext());
        SharedPref.saveBleScanCount(0, getApplicationContext());
        isTeamDriverLogin = SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver);


        startLocationService();

        getDriverIDs();

        if (SharedPref.GetNewLoginStatus(getApplicationContext()) && SharedPref.IsDriverLogin(getApplicationContext())) {
            String VIN = SharedPref.getVINNumber(getApplicationContext());
            UpdateOfflineDriverLog(DriverId, CoDriverId, DeviceId, VIN,
                    String.valueOf(obdVehicleSpeed), true);

            constants.writeBleOnOffStatus(global.isBleEnabled(getApplicationContext()), global, bleGpsAppLaunchMethod,
                    dbHelper, getApplicationContext());

        }


//  ------------- Wired OBD ----------
        this.connection = new RemoteServiceConnection();
        this.replyTo = new Messenger(new IncomingHandler());
        SharedPref.SaveObdStatus(SharedPref.getObdStatus(getApplicationContext()), SharedPref.getObdLastStatusTime(getApplicationContext()),
                global.GetCurrentUTCTimeFormat(), getApplicationContext());

        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
            BindConnection();
            checkWiredObdConnection();
        }else if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE){

            if (SharedPref.IsDriverLogin(getApplicationContext())) {
                startBleService();
            }

            if(!HTBleSdk.Companion.getInstance().isConnected() && SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED){
                SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
            }

        }


        try{
            //  ------------- OBD Log write initilization----------
            obdUtil = new Utils(getApplicationContext());
            obdUtil.createLogFile();
            obdUtil.createAppUsageLogFile();
            obdUtil.createExecTimeLogFile();
        }catch (Exception e){
            e.printStackTrace();
        }


        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Logger.LogDebug("received", "received from service");

                boolean IsConnected = intent.getBooleanExtra(ConstantsKeys.IsConnected, false);
                String data = intent.getStringExtra(ConstantsKeys.Data);
                String Address = intent.getStringExtra(ConstantsKeys.Address);
               // Logger.LogDebug("Ble Data", "Data: " + data);
            // 0048 @@ 0 @@ 1 @@ 090622 @@ 060443 @@ 090622053820 @@ 090622060443 @@ OnTime @@ 0 @@ 0 @@ 642264000 @@ 0.00 @@ @@ X @@ X @@ 0

                checkBleRule(data, Address);
            }

        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(ConstantsKeys.BleDataService));


        try {
            speedAlertHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    try {
                        if (UILApplication.isActivityVisible() && TabAct.speedAlertBtn != null) {
                            // show vin mismatch alert
                            TabAct.speedAlertBtn.performClick();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            dismissAlertHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    try {
                        if (UILApplication.isActivityVisible() && TabAct.dismissAlertBtn != null) {
                            TabAct.dismissAlertBtn.performClick();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    //  ------------- Wired OBD data response handler ----------
    private class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {

            final Bundle bundle = msg.getData();
            isWiredObdRespond = true;

            String obdLastCallDate = SharedPref.getWiredObdCallTime(getApplicationContext());
            if(obdLastCallDate.length() > 10){
                final DateTime currentDateTime = global.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext());    // Current Date Time
                final DateTime savedDateTime = global.getDateTimeObj(obdLastCallDate, false);

                int timeInSec = (int) Constants.getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
                if(timeInSec >= 3){  // minimum call interval is 3 sec.
                    SharedPref.SetWiredObdCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    //  Logger.LogDebug("GetCurrentDateTime", "GetCurrentDateTime: " + Globally.GetDriverCurrentDateTime(global, getApplicationContext()));

                    if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED ) {

                        String timeStamp = "--", vin = "--";
                        int speed = 0;

                        try {
                            timeStamp = bundle.getString(constants.OBD_TimeStamp);
                           // obdOdometer = bundle.getString(constants.OBD_Odometer);
                            obdTripDistance = bundle.getString(constants.OBD_TripDistance);
                            ignitionStatus = bundle.getString(constants.OBD_IgnitionStatus);
                            truckRPM = bundle.getString(constants.OBD_RPM);
                            obdEngineHours = bundle.getString(constants.OBD_EngineHours);
                            vin = bundle.getString(constants.OBD_VINNumber);
                            speed = bundle.getInt(constants.OBD_Vss);
                            obdVehicleSpeed = speed;

                            currentHighPrecisionOdometer = bundle.getString(constants.OBD_HighPrecisionOdometer);
                            // using simple odometer value only when High Precision value was null or 0.
                            if(currentHighPrecisionOdometer.equals("null") || currentHighPrecisionOdometer.equals("0")) {
                                currentHighPrecisionOdometer = bundle.getString(constants.OBD_Odometer);
                                currentHighPrecisionOdometer = Constants.kmToMeter(currentHighPrecisionOdometer);
                            }else{
                                // if Truck's High Precision Odometer unit type is KM.
                                if(SharedPref.getHighPrecisionUnit(getApplicationContext()).equalsIgnoreCase("km")) {
                                    currentHighPrecisionOdometer = Constants.kmToMeter(currentHighPrecisionOdometer);
                                }
                            }

                            if(vin.length() <= 5){
                                vin = "";
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        // ---------------- temp data ---------------------

                        // temp odometer for simulator converting odometer from km to meter. because it is saving in km.
                        //  currentHighPrecisionOdometer = Constants.kmToMeter(obdOdometer);


                  /*      obdEngineHours = "123956";
                        ignitionCount++;
                        String obdOdometer = String.valueOf(tempOdo);
                        currentHighPrecisionOdometer = Constants.kmToMeter(obdOdometer);
                        SharedPref.SetObdOdometer(obdOdometer, getApplicationContext());
                        tempOdo = tempOdo + 500;
                        if(SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED) {

                            ignitionStatus = "ON"; truckRPM = "700";
                            if(tempSpeed > 10) {
                                speed = 15;
                                tempSpeed = 0;
                            }else{
                                speed = 0;
                            }
                            tempSpeed++;
                        }else {
                            ignitionStatus = "OFF"; truckRPM = "0"; speed = 0;
                        }*/


                        sendBroadCast(parseObdDataInHtml(vin, speed), "");

                        if (SharedPref.IsDriverLogin(getApplicationContext())) {

                            // this check is used to avoid fake PC/YM continue alert. because obd some times return ignition status value off false. So we are checking it continuesly 3 times
                            if (ignitionStatus.equals("OFF")) {
                                speed = 0;
                                if (ignitionOffCount > 2) {
                                    // call ELD rules with wired tablet data
                                    obdCallBackObservable(speed, vin, timeStamp);
                                    SharedPref.SetWrongVinAlertView(false, getApplicationContext());

                                } else {
                                    // ignore to call wrong rules. Verify 3 times false ignition status because some times OBD returns false value.
                                    ignitionOffCount++;
                                    callWiredDataService(Constants.SocketTimeout3Sec);
                                }

                            } else {
                                ignitionOffCount = 0;

                                String lastIgnitionStatus = SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getApplicationContext());
                                if (lastIgnitionStatus.equals("OFF") && obdEngineHours.equals(SharedPref.getObdEngineHours(getApplicationContext())) &&
                                        SharedPref.getObdStatus(getApplicationContext()) == constants.WIRED_CONNECTED ) {   //ignitionStatus.equals("ON") &&

                                    String IgnitionOffCallTime = SharedPref.getIgnitionOffCallTime(getApplicationContext());
                                    if(IgnitionOffCallTime.length() > 10){
                                        final DateTime lastDateTime = global.getDateTimeObj(IgnitionOffCallTime, false);
                                        int timeDiffInSec = (int) Constants.getDateTimeDuration(lastDateTime, currentDateTime).getStandardSeconds();
                                        if(timeDiffInSec >= 30){

                                            // call ELD rules with wired tablet data
                                            obdCallBackObservable(speed, vin, timeStamp);

                                        }else{
                                            if(timeDiffInSec < 0){
                                                SharedPref.SetIgnitionOffCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                            }
                                        }

                                    }else{
                                        // call ELD rules with wired tablet data
                                        obdCallBackObservable(speed, vin, timeStamp);

                                    }

                                }else{
                                    // call ELD rules with wired tablet data
                                    obdCallBackObservable(speed, vin, timeStamp);

                                }

                            }

                        }

                    }else{
                        // call ELD rules when ble disconnected

                        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE &&
                                SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_CONNECTED) {

                            obdCallBackObservable(-1, SharedPref.getVINNumber(getApplicationContext()),
                                                    global.GetDriverCurrentDateTime(global, getApplicationContext()));
                        }
                    }

                    bundle.clear();


                }else if(timeInSec < 0){
                    SharedPref.SetWiredObdCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    Logger.LogDebug("GetCurrentDateTime", "Negative Time: " + timeInSec);
                }

            }else{
                SharedPref.SetWiredObdCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
            }


        }
    }





    private void obdCallBackObservable(int speed, String vin, String timeStamp){

        SharedPref.SetIgnitionOffCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
        int OBD_LAST_STATUS = SharedPref.getObdStatus(getApplicationContext());
        String last_obs_source_name = SharedPref.getObdSourceName(getApplicationContext());

        SharedPref.setVehicleVin(vin, getApplicationContext());
        SharedPref.setVss(speed, getApplicationContext());
        SharedPref.setRPM(truckRPM, getApplicationContext());
        SharedPref.setIgnitionStatus(ignitionStatus, getApplicationContext());

        Globally.VEHICLE_SPEED = speed;

// ------------------------------------------------------------------------


        String VINNumberFromApi = SharedPref.getVINNumber(getApplicationContext());

        if (VINNumberFromApi.length() > 5) {
            // this check is using because some times OBD returns 0 value
            checkEngHrOdo();

            // check valid time before change status automatically
            boolean IsValidTime = global.isCorrectTime(getApplicationContext(), false);
            if (IsValidTime) {
                writeWiredObdLogs(OBD_LAST_STATUS, speed, timeStamp, "Save Truck Info - OBD_LAST_STATUS: ");


                if (OBD_LAST_STATUS == constants.WIRED_CONNECTED || OBD_LAST_STATUS == constants.BLE_CONNECTED) {

                    // ELD rule calling for Wired OBD
                    try {
                        if (ignitionStatus.equals("ON")) {

                            String currentLogDate = global.GetDriverCurrentDateTime(global, getApplicationContext());
                            Globally.IS_OBD_IGNITION = true;
                            continueStatusPromtForPcYm(ignitionStatus, last_obs_source_name, global.GetDriverCurrentDateTime(global, getApplicationContext()), OBD_LAST_STATUS);

                            /* ======================== Malfunction & Diagnostic Events ========================= */
                            if (obdEngineHours.length() > 0 && !obdEngineHours.equals("0") && !obdEngineHours.equals("0.00")) {

                                vehiclePowerEventMethod.savePowerEventOnChange(ignitionStatus, offsetFromUTC, dbHelper, getApplicationContext());

                                ClearMissingDiagnostics();  // checking  missing data for clear

                                checkPowerMalDiaEvent();   // checking Power Data Compliance Mal/Dia event

                                checkEngSyncClearEvent();  // checking EngineSyncDataCompliance Mal/Dia clear event if already occurred

                            }


                            double obdOdometerDouble = Double.parseDouble(currentHighPrecisionOdometer);
                            // save current odometer for HOS calculation
                            saveDayStartOdometer(currentHighPrecisionOdometer);

                            String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());
                            if (savedDate.length() == 0 && obdOdometerDouble > 0) {
                                // save current HighPrecisionOdometer locally
                                savedDate = currentLogDate;
                                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                            }

                            boolean isDrivingAllowed = true;
                            if (SharedPref.isDrivingAllowed(getApplicationContext()) == false && speed >= 8) {
                                final DateTime currentDateTime = global.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext());    // Current Date Time
                                final DateTime savedDateTime = global.getDateTimeObj(SharedPref.getDrivingAllowedTime(getApplicationContext()), false);

                                if (savedDateTime.toString().length() > 10) {
                                    int timeInSec = (int) Constants.getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
                                    if (timeInSec > 20) {
                                        isDrivingAllowed = true;
                                        SharedPref.setDrivingAllowedStatus(true, "", getApplicationContext());
                                    } else {
                                        isDrivingAllowed = false;
                                    }
                                }
                            }

                            if (speed > 1) {
                                SharedPref.setVehilceMovingStatus(true, getApplicationContext());
                                speedMovingBroadcast();
                            } else {
                                SharedPref.setVehilceMovingStatus(false, getApplicationContext());
                            }

                            if (isDrivingAllowed) {
                                timeDuration = Constants.SocketTimeout3Sec;
                                callRuleWithStatusWise(currentHighPrecisionOdometer, savedDate, vin, timeStamp, speed, -1);
                            }

                            callWiredDataService(timeDuration);

                        } else {

                            vehiclePowerEventMethod.savePowerEventOnChange(ignitionStatus, offsetFromUTC, dbHelper, getApplicationContext());

                            saveIgnitionStatus(ignitionStatus, last_obs_source_name);

                            speed = 0;
                            Globally.IS_OBD_IGNITION = false;
                            SharedPref.SetTruckIgnitionStatusForContinue("OFF", last_obs_source_name, "", getApplicationContext());
                            SharedPref.setVss(speed, getApplicationContext());
                            SharedPref.setVehilceMovingStatus(false, getApplicationContext());

                            String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());
                            if (savedDate.length() == 0 && Double.parseDouble(currentHighPrecisionOdometer) > 0) {
                                savedDate = global.GetDriverCurrentDateTime(global, getApplicationContext());
                                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, savedDate, getApplicationContext());
                            }

                            callRuleWithStatusWise(currentHighPrecisionOdometer, savedDate, vin, timeStamp, speed, 0);

                            callWiredDataService(Constants.SocketTimeout8Sec);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Globally.IS_OBD_IGNITION = false;
                        callWiredDataService(Constants.SocketTimeout3Sec);
                    }

                } else {

                    // check in wire disconnect case but device is also not connected with ALS/OBD wifi ssid
                    if (SharedPref.getObdPreference(getApplicationContext()) != Constants.OBD_PREF_WIFI) {

                        SharedPref.setVehilceMovingStatus(false, getApplicationContext());

                        // Check Engine Sync data Malfunction/Diagnostic event
                        checkEngineSyncMalDiaOccurredEvent(speed, false, false, 0);


                        if (OBD_LAST_STATUS == constants.WIRED_DISCONNECTED) {
                            callWiredDataService(Constants.SocketTimeout5Sec);
                        }

                    }

                    String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());
                    if (savedDate.length() == 0 && Double.parseDouble(currentHighPrecisionOdometer) > 0) {
                        savedDate = global.GetDriverCurrentDateTime(global, getApplicationContext());
                        SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, savedDate, getApplicationContext());
                    }
                    callRuleWithStatusWise(currentHighPrecisionOdometer, savedDate, vin, timeStamp, speed, -1);

                }

        }else{
             callWiredDataService(Constants.SocketTimeout8Sec);
        }


            if(!constants.isValidVinFromObd(ignitionStatus, getApplicationContext()) ) {

                if( !SharedPref.IsWrongVinAlertView(getApplicationContext())) {
                    constants.saveObdData(constants.getObdSource(getApplicationContext()),
                            "WIRED-InValidVinFromObd: " + vin + ", VIN Length- " + vin.length(), "",
                            currentHighPrecisionOdometer, "", "", ignitionStatus, truckRPM, String.valueOf(speed),
                            String.valueOf(-1), obdEngineHours, timeStamp, timeStamp,
                            DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());

                    SharedPref.SetWrongVinAlertView(true, getApplicationContext());

                    Globally.PlayNotificationSound(getApplicationContext());
                    global.ShowLocalNotification(getApplicationContext(),
                            getString(R.string.confirm_vin),
                            getString(R.string.confirm_vin_desc), 20811);

                    try {
                        Message message = speedAlertHandler.obtainMessage();
                        message.sendToTarget();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else{
                mNotificationManager.dismissNotification(getApplicationContext(), 20811);

                // dismiss alert dialog if already shown in tab Act
                try {
                    Message message = dismissAlertHandler.obtainMessage();
                    message.sendToTarget();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        } else {
            SharedPref.setVehilceMovingStatus(false, getApplicationContext());
            callWiredDataService(Constants.SocketTimeout3Sec);

        }
        LastObdSpeed = speed;
    }


    private void speedMovingBroadcast(){
        if(SharedPref.IsAppRestricted(getApplicationContext())) {
            if (global.isSingleDriver(getApplicationContext()) && !Constants.IS_ACTIVE_HOS) {
                Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                intent.putExtra(ConstantsKeys.SuggestedEdit, false);
                intent.putExtra(ConstantsKeys.IsCycleRequest, false);
                intent.putExtra(ConstantsKeys.IsELDNotification, false);
                intent.putExtra(ConstantsKeys.IsActiveHosScreen, true);

                LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);

            }else if(!global.isSingleDriver(getApplicationContext()) && !Constants.IS_ACTIVE_ELD){
                if (!hMethods.isActionEventAllowedWithSpeed(getApplicationContext(), global, DriverId, dbHelper)) {

                    Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                    intent.putExtra(ConstantsKeys.SuggestedEdit, false);
                    intent.putExtra(ConstantsKeys.IsCycleRequest, false);
                    intent.putExtra(ConstantsKeys.IsELDNotification, false);
                    intent.putExtra(ConstantsKeys.IsActiveHosScreen, false);
                    intent.putExtra(ConstantsKeys.IsActiveHomeScreen, true);

                    LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);

                }
            }

        }
    }

    private void checkWiredObdConnection(){
        if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
            int lastObdStatus = SharedPref.getObdStatus(getApplicationContext());
            obdShell = ShellUtils.execCommand("cat /sys/class/power_supply/usb/type", false);

            if (SharedPref.getVINNumber(getApplicationContext()).length() > 5) {
                if (obdShell.result == 0) {
                    // Logger.LogDebug("OBD", "obd --> cat type --> " + obdShell.successMsg);
                    if (obdShell.successMsg.contains("USB_DCP") ) {  // USB_DCP  Connected State    || obdShell.successMsg.contains("USB")

                        if (lastObdStatus != Constants.WIRED_CONNECTED) {

                            SharedPref.SetIgnitionOffCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                            SharedPref.SetIgnitionOffCalled(false, getApplicationContext());
                            SharedPref.setObdStatusAfterLogin(true, getApplicationContext());

                            constants.saveObdData(constants.getObdSource(getApplicationContext()),
                                    "WIRED-CONNECTED", "", "-1",
                                    currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                                    "-1", obdEngineHours, "", "",
                                    DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());

                            sendBroadCast("<b>Wired tablet connected</b> <br/>", "");
                            sendBroadcastUpdateObd(false);

                            sendEcmBroadcast(false);
                            global.ShowLocalNotification(getApplicationContext(),
                                    getString(R.string.wired_tablettt),
                                    getString(R.string.wired_tablet_connected), 2081);

                            callWiredDataService(5000);


                        }

                        SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                global.GetCurrentUTCTimeFormat(), getApplicationContext());

                    } else {
                        // Disconnected State. Save only when last status was not already disconnected
                        if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                            if (lastObdStatus != constants.WIRED_DISCONNECTED) {
                                obdVehicleSpeed = -1;

                                constants.saveObdData(constants.getObdSource(getApplicationContext()),
                                        "WIRED-DISCONNECTED", "", "-1",
                                        currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                                        "-1", obdEngineHours, "", "",
                                        DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());

                                SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
                                SharedPref.setVehilceMovingStatus(false, getApplicationContext());

                                obdCallBackObservable(-1, "", "");

                                sendBroadcastUpdateObd(false);
                                sendEcmBroadcast(true);

                                Globally.PlayNotificationSound(getApplicationContext());
                                global.ShowLocalNotification(getApplicationContext(),
                                        getString(R.string.wired_tablettt),
                                        getString(R.string.wired_tablet_disconnected), 2081);

                            }
                        }
                    }
                } else {
                    if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                        if (lastObdStatus != Constants.WIRED_ERROR) {
                            obdVehicleSpeed = -1;

                            constants.saveObdData(constants.getObdSource(getApplicationContext()),
                                    "WIRED-ERROR", "", "-1",
                                    currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                                    "-1", obdEngineHours, "", "",
                                    DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());


                            sendEcmBroadcast(true);
                            Globally.PlayNotificationSound(getApplicationContext());
                            global.ShowLocalNotification(getApplicationContext(),
                                    getString(R.string.wired_tablettt),
                                    getString(R.string.wired_tablet_conn_error), 2081);

                            SharedPref.SaveObdStatus(Constants.WIRED_ERROR, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                    global.GetCurrentUTCTimeFormat(), getApplicationContext());
                            sendBroadcastUpdateObd(false);
                        }
                    }
                }
            }


        }


    }



    void saveIgnitionStatus(String ignitionStatus, String last_obs_source_name){
        String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());

        // save log when ignition status is changed.    //SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getApplicationContext())
        if (lastIgnitionStatus.equals("ON")) {
            // save truck info to check power compliance mal/dia later.
            SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, last_obs_source_name,
                    global.GetDriverCurrentDateTime(global, getApplicationContext()), global.GetCurrentUTCTimeFormat(),
                    SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                    SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()),
                    getApplicationContext());

        }
    }


    // this check is using because some times OBD returns 0 value
    private void checkEngHrOdo(){
        if(constants.isValidData(obdEngineHours)) {
            SharedPref.SetObdEngineHours(obdEngineHours, getApplicationContext());
        }else{
            obdEngineHours = SharedPref.getObdEngineHours(getApplicationContext());
        }

        if(constants.isValidData(currentHighPrecisionOdometer)) {
            SharedPref.SetObdOdometer(Constants.meterToKmWithObd(currentHighPrecisionOdometer), getApplicationContext());
            SharedPref.SetObdOdometerInMiles(Constants.meterToMilesWith2DecPlaces(currentHighPrecisionOdometer), getApplicationContext());
        }else{
            // converting odometer from km to meter. because it is saving in km.
            currentHighPrecisionOdometer = Constants.kmToMeter(SharedPref.getObdOdometer(getApplicationContext()));

        }
    }


    private void writeWiredObdLogs(int OBD_LAST_STATUS, int speed, String timeStamp, String detail){

        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
            String lastCallTime = SharedPref.getObdWriteCallTime(getApplicationContext());
            if (lastCallTime.length() > 10) {
                DateTime currentTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext());
                DateTime lastCallDateTime = Globally.getDateTimeObj(lastCallTime, false);
                long secDiff = Constants.getDateTimeDuration(lastCallDateTime, currentTime).getStandardSeconds();
                long CheckIntervalInSec = 120;   // 2 min
                if(ignitionStatus.equals("OFF")){
                    CheckIntervalInSec = 180;   // 3 min
                }

                if (secDiff >= CheckIntervalInSec) {
                    SharedPref.setObdWriteCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    constants.saveObdData(constants.getObdSource(getApplicationContext()),
                            "VIN: " + SharedPref.getVehicleVin(getApplicationContext()) + ", " + detail +OBD_LAST_STATUS,
                            "", currentHighPrecisionOdometer,
                            currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                            String.valueOf(-1), obdEngineHours, timeStamp, timeStamp,
                            DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());

                }
            } else {
                SharedPref.setObdWriteCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
            }
        }
    }


    void callRuleWithStatusWise(String currentHighPrecisionOdometer, String savedDate, String vin, String timeStamp, int speed, double calculatedSpeedFromOdo){

        try{

            String currentLogDate = global.GetDriverCurrentDateTime(global, getApplicationContext());
            String jobType = SharedPref.getDriverStatusId(getApplicationContext());
            // double intHighPrecisionOdometerInKm = (Double.parseDouble(currentHighPrecisionOdometer) * 0.001);
            if (jobType.equals(global.DRIVING)) {

                timeDuration = Constants.SocketTimeout10Sec;
                if (constants.minDiff(savedDate, global, false, getApplicationContext()) > 0) {

                    saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, "DRIVING");

                }

                if (speed >= 8 && LastObdSpeed < 8) {
                    sendBroadcastUpdateObd(true);
                }

            } else if (jobType.equals(global.ON_DUTY)) {
                // if speed is coming >8 then ELD rule is called after 8 sec to change the status to Driving as soon as.
                if (speed >= 8 && !truckRPM.equals("0")) {

                    try {
                        get18DaysLogArrayLocally();

                        boolean isYardMove = hMethods.isPCYM(EldFragment.driverLogArray);
                        if (isYardMove) {
                            //String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getApplicationContext());
                            String CurrentCycleId = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getApplicationContext()), getApplicationContext());
                            if (speed >= 8 && (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2))) {   // In Yard move
                                timeDuration = Constants.SocketTimeout6Sec;
                                saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, "OnDutyYM Speed: " + speed);
                            } else {
                                // call ELD rule after 1 minute to improve performance
                                if (constants.minDiff(savedDate, global, false, getApplicationContext()) > 0) {
                                    saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, "OnDutyYM Speed: " + speed);
                                }
                                timeDuration = Constants.SocketTimeout5Sec;
                            }
                        } else {
                            timeDuration = Constants.SocketTimeout15Sec;
                            saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, "OnDuty Speed: " + speed);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    if (truckRPM.equals("0")) {
                        speed = 0;
                        timeDuration = Constants.SocketTimeout10Sec;
                    } else {
                        if (speed == 0) {
                            timeDuration = Constants.SocketTimeout3Sec;
                        }
                    }

                    //boolean isYmPcAlertShown = SharedPref.GetTruckStartLoginStatus(getApplicationContext());
                    // call ELD rule after 1 minute to improve performance
                    if (constants.minDiff(savedDate, global, false, getApplicationContext()) > 0) {    //||  (isYmPcAlertShown && isYardMove())
                        saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, "OnDuty Speed: " + speed);
                    }
                }

            } else {

                // =================== For OFF Duty & Sleeper case =====================
                // boolean isYmPcAlertShown = SharedPref.GetTruckStartLoginStatus(getApplicationContext());

                if (speed <= 0) { //&& calculatedSpeedFromOdo <= 0
                    if (speed == 0)
                        timeDuration = Constants.SocketTimeout3Sec;

                    // call ELD rule after 1 minute to improve performance
                    if (constants.minDiff(savedDate, global, false, getApplicationContext()) > 0) {
                        SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                    }

                } else {
                    if (speed >= 8 && !truckRPM.equals("0")) {    // && calculatedSpeedFromOdo >= 8 if speed is coming >8 then ELD rule is called after 8 sec to change the status to Driving as soon as.

                        if (jobType.equals(global.SLEEPER)) {
                            timeDuration = Constants.SocketTimeout15Sec;
                            saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, "Sleeper-Speed: " + speed);
                        } else {
                            get18DaysLogArrayLocally();

                            boolean isPersonal = hMethods.isPCYM(EldFragment.driverLogArray);
                            if (isPersonal) {
                                String puSavedTime = SharedPref.getPuExceedCheckDate(getApplicationContext());
                                if (puSavedTime.length() > 10) {
                                    int secDiff = constants.getSecDifference(puSavedTime, Globally.GetCurrentUTCTimeFormat());
                                    if (secDiff > 30) {
                                        SharedPref.setPuExceedCheckDate(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());

                                        checkPuExceedStatus(currentLogDate, speed);
                                    } else {
                                        if (secDiff < 0) {
                                            SharedPref.setPuExceedCheckDate(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
                                        }
                                    }
                                } else {
                                    SharedPref.setPuExceedCheckDate(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());

                                    checkPuExceedStatus(currentLogDate, speed);

                                }


                            } else {
                                timeDuration = Constants.SocketTimeout15Sec;
                                saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, "OffDuty-Speed: " + speed);
                            }

                        }

                    } else {
                        SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void checkPuExceedStatus(String currentLogDate, int speed){
        try{
            double AccumulativePersonalDistance = constants.getAccumulativePersonalDistance(DriverId, offsetFromUTC,
                    Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext()),
                    Globally.GetCurrentUTCDateTime(), hMethods, dbHelper, getApplicationContext());

            if(AccumulativePersonalDistance > 75 || Constants.isPcYmAlertButtonClicked){

                // check PU status exceeding status if 75km exceeded showing popup
                checkPUExceedStatus(null, AccumulativePersonalDistance);

                saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, "PersonalUse-Speed: "+speed);
                timeDuration = Constants.SocketTimeout15Sec;
            }else{
                timeDuration = Constants.SocketTimeout5Sec;
                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private JSONArray get18DaysLogArrayLocally(){
        if (EldFragment.driverLogArray == null || EldFragment.driverLogArray.length() == 0) {
            EldFragment.driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
        }
        return EldFragment.driverLogArray;
    }

    void callWiredDataService(int timeDuration){
        try {
            if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED ) {
                if (SharedPref.IsDriverLogin(getApplicationContext())) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            StartStopServer(constants.WiredOBD);
                        }
                    }, timeDuration);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    String parseObdDataInHtml(String vin, int speed){

        return "<b>IgnitionStatus:</b> " + ignitionStatus + "<br/>" +
                "<b>Truck RPM:</b> " + truckRPM + "<br/>" +
                "<b>HighPrecisionOdometer:</b> " + currentHighPrecisionOdometer + "<br/>" +
                "<b>Speed:</b> " + speed + "<br/>" +
                "<b>VIN:</b> " + vin + "<br/>" +
                "<b>Trip Distance:</b> " + obdTripDistance + "<br/>" +
                "<b>EngineHours:</b> " + obdEngineHours + "<br/>" ;
    }




    void sendEcmBroadcast(boolean IsEldEcmALert){
        try{
            Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
            intent.putExtra(ConstantsKeys.PersonalUse75Km, false);
            intent.putExtra(ConstantsKeys.IsEldEcmALert, IsEldEcmALert);
            LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void checkBleRule(String data, String Address){

        try {
            if (SharedPref.IsDriverLogin(getApplicationContext())) {

                if(BleDataService.isBleConnected){
                    // Logger.LogError("LoginService", "onReceive==" + htBleData);
                    onReceiveTotalCountOnReq = 0;
                    isReceiverInit = true;


                    String[] decodedDataArray = BleUtil.decodedDataArray(data);
                    if(decodedDataArray.length > 10){
                        if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {

                            String savedOnReceiveTime = SharedPref.getBleOnReceiveTime(getApplicationContext());
                            if (savedOnReceiveTime.length() > 10) {
                                int timeInSec = constants.getSecDifference(savedOnReceiveTime, Globally.GetDriverCurrentDateTime(global, getApplicationContext()));
                                if (timeInSec > 1) {
                                    SharedPref.setBleOnReceiveTime(getApplicationContext());

                                    // Logger.LogError("TAG", "onReceiveTime==" + htBleData);

                                    String savedMacAddress = SharedPref.GetBleOBDMacAddress(getApplicationContext());
                                    if (savedMacAddress.length() == 0 || savedMacAddress.equals(decodedDataArray[0])) {

                                        SharedPref.SaveBleOBDMacAddress(decodedDataArray[0], getApplicationContext());

                                        if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_CONNECTED) {

                                            // write BLE logs when connected
                                            writeBleConnectDisconnectLog(true);

                                            SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                                    global.GetCurrentUTCTimeFormat(), getApplicationContext());
                                            SharedPref.SetIgnitionOffCalled(false, getApplicationContext());
                                            SharedPref.setObdStatusAfterLogin(true, getApplicationContext());

                                            sendBroadcastUpdateObd(false);
                                            sendEcmBroadcast(false);
                                            if (SharedPref.getVINNumber(getApplicationContext()).length() > 5) {
                                                global.ShowLocalNotification(getApplicationContext(),
                                                        getString(R.string.BluetoothOBD),
                                                        getString(R.string.obd_ble), 2081);

                                                constants.saveBleLog("Data receive after connected",
                                                        Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                                                        driverPermissionMethod, obdUtil);


                                            }

                                            //  HTBleSdk.Companion.getInstance().setIntervalEvent(3);
                                        }


                                        getBleData(decodedDataArray);

                                        obdCallBackObservable(obdVehicleSpeed, decodedDataArray[13], global.GetDriverCurrentDateTime(global, getApplicationContext()));


                                    } /*else {
                                            disconnectBleObd();
                                        }*/

                                }else{
                                    if(timeInSec < 0){
                                        SharedPref.setBleOnReceiveTime(getApplicationContext());
                                    }
                                }
                            } else {
                                SharedPref.setBleOnReceiveTime(getApplicationContext());
                            }


                        } else {
                            disconnectBleObd();
                        }
                    }


                }else{

                    obdVehicleSpeed = -1;
                    SharedPref.setVehilceMovingStatus(false, getApplicationContext());
                    SharedPref.SetWrongVinAlertView(false, getApplicationContext());


                    if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {

                        sendBroadCast(data, "");


                        if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {


                            SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                    global.GetCurrentUTCTimeFormat(), getApplicationContext());


                            writeBleConnectDisconnectLog(false);

                            if (SharedPref.getVINNumber(getApplicationContext()).length() > 5) {
                                Globally.PlayNotificationSound(getApplicationContext());

                                String savedMacAddress = SharedPref.GetBleOBDMacAddress(getApplicationContext());
                                if (savedMacAddress.length() == 0 || savedMacAddress.equals(Address)) {
                                    global.ShowLocalNotification(getApplicationContext(),
                                            getString(R.string.BluetoothOBD),
                                            getString(R.string.obd_ble_disconnected), 2081);
                                } else {
                                    global.ShowLocalNotification(getApplicationContext(),
                                            getString(R.string.BleOBDConnErr),
                                            getString(R.string.obd_ble_disconnected), 2081);
                                }

                                sendBroadcastUpdateObd(false);
                                sendEcmBroadcast(true);
                            }
                        }

                    }


                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getBleData(String[] decodedDataArray){
        try {

            // bluetooth OBD data
            obdEngineHours = "0"; currentHighPrecisionOdometer = "0";  obdTripDistance = "0";   //obdOdometer = "0";
            ignitionStatus = "OFF"; truckRPM = "0";

            obdVehicleSpeed = Integer.valueOf(decodedDataArray[9]);
            //obdOdometer = decodedDataArray[11];
            obdTripDistance = decodedDataArray[16];
            truckRPM = decodedDataArray[10];
            obdEngineHours = decodedDataArray[12];
            currentHighPrecisionOdometer = decodedDataArray[11];

            Globally.LATITUDE = decodedDataArray[14];
            Globally.LONGITUDE = decodedDataArray[15];

            if(decodedDataArray[17].length() > 0) {
                Globally.BLE_NAME = decodedDataArray[17];
            }
            // this check is using to confirm loc update, because in loc disconnection ble OBD is sending last saved location.
            if(Globally.LATITUDE.equals(PreviousLatitude) && Globally.LONGITUDE.equals(PreviousLongitude) &&
                    !currentHighPrecisionOdometer.equals(PreviousOdometer)){
                Globally.LATITUDE = Globally.GPS_LATITUDE;
                Globally.LONGITUDE = Globally.GPS_LONGITUDE;

            }

            if(Globally.LATITUDE.length() < 4){
                SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());

                if(Globally.GPS_LATITUDE.length() > 3){
                    Globally.LATITUDE = Globally.GPS_LATITUDE;
                    Globally.LONGITUDE = Globally.GPS_LONGITUDE;
                }else {
                    Globally.LATITUDE = "";
                    Globally.LONGITUDE = "";
                }

            }else{

                SharedPref.SetLocReceivedFromObdStatus(true, getApplicationContext());
            }

            PreviousLatitude = decodedDataArray[14];
            PreviousLongitude = decodedDataArray[15];
            PreviousOdometer = currentHighPrecisionOdometer;

            constants.saveEcmLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE, currentHighPrecisionOdometer, getApplicationContext());


            if (Integer.valueOf(truckRPM) > 0 || obdVehicleSpeed > 0) {
                ignitionStatus = "ON";
                ignitionOffCount = 0;
            }


            String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());
            //  Logger.LogDebug("lastIgnitionStatus", "lastIgnitionStatus00: " +lastIgnitionStatus );
            // this check is used when ble obd is disconnected
            if(SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_CONNECTED) {
                if (!SharedPref.getRPM(getApplicationContext()).equals("0") && lastIgnitionStatus.equals("true")) {
                    truckRPM = SharedPref.getRPM(getApplicationContext());
                    ignitionStatus = "ON";
                }
            }

            // this check is used to avoid fake PC/YM continue alert. because obd some times return ignition status value off false. So we are checking it continuesly 3 times
            if(ignitionStatus.equals("OFF")){
                if(ignitionOffCount > 2){
                    // ignitionOffCount = 0;

                }else{
                    ignitionOffCount++;

                    // temp ON ignitionStatus to check 3 times to confirm
                    if(Integer.valueOf(truckRPM) > 400) {
                        ignitionStatus = "ON";
                    }
                }

            }else{
                ignitionOffCount = 0;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    // Bluetooth OBD connect/disconnect log with time
    private void writeBleConnectDisconnectLog(boolean isBleConnected){
        int LastLocationStatus = SharedPref.GetLocationStatus(getApplicationContext());

        if(isBleConnected){
            if (LastLocationStatus != Constants.OBD_CONNECTED) {
                Logger.LogError(TAG_OBD, "obd Connected");
                bleGpsAppLaunchMethod.SaveBleGpsAppLogInTable(Constants.LogEventTypeBle, Constants.OBD_CONNECTED,
                        global.getCurrentDateLocalUtc(), dbHelper);

                SharedPref.SetGpsBlePermission(global.isBleEnabled(getApplicationContext()), SharedPref.WasGpsEnabled(getApplicationContext()),
                        Constants.OBD_CONNECTED, getApplicationContext());
            }else{
                Logger.LogError(TAG_OBD, "Ble was connected");
            }

        }else {
            if (!global.isBleEnabled(getApplicationContext()) && LastLocationStatus != Constants.POWERED_OFF) {

                bleGpsAppLaunchMethod.SaveBleGpsAppLogInTable(Constants.LogEventTypeBle, Constants.POWERED_OFF,
                        global.getCurrentDateLocalUtc(), dbHelper);
                SharedPref.SetGpsBlePermission(global.isBleEnabled(getApplicationContext()), SharedPref.WasGpsEnabled(getApplicationContext()),
                        Constants.POWERED_OFF, getApplicationContext());
            } else {
                if (LastLocationStatus != Constants.OBD_DISCONNECT) {
                    Logger.LogError(TAG_OBD, "obd disConnected");
                    bleGpsAppLaunchMethod.SaveBleGpsAppLogInTable(Constants.LogEventTypeBle, Constants.OBD_DISCONNECT,
                            global.getCurrentDateLocalUtc(), dbHelper);
                    SharedPref.SetGpsBlePermission(global.isBleEnabled(getApplicationContext()), SharedPref.WasGpsEnabled(getApplicationContext()),
                            Constants.OBD_DISCONNECT, getApplicationContext());
                }else{
                    Logger.LogError(TAG_OBD, "Ble was disConnected");
                }
            }
        }

    }




    private void sendBroadCast(String data, String rawMsg){
        try {
            //   if(SharedPref.isOBDScreen(getApplicationContext())) {
            Intent intent = new Intent("ble_changed_data");
            intent.putExtra("decoded_data", data);
            intent.putExtra("raw_message", rawMsg);

            LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
            //  }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void saveEngSyncEventWithPowerEvent(double timeDiff){
        try{
            if(!SharedPref.isEngSyncMalfunction(getApplicationContext()) ) {
                boolean isEngineSyncMalAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncMal, getApplicationContext());
                boolean isEngineSyncDiaAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncDiag, getApplicationContext());

                if(isEngineSyncMalAllowed || isEngineSyncDiaAllowed) {
                    SharedPref.saveEngSyncDiagnstcStatus(true, getApplicationContext());
                    constants.saveDiagnstcStatus(getApplicationContext(), true);
                    SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    SharedPref.setEngSyncClearEventCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                    String EventType = "", MalfunctionDefinition = "";
                    if (timeDiff > Constants.PowerEngSyncMalOccTime) {
                        EventType = Constants.EngineSyncMalfunctionEvent;
                        MalfunctionDefinition = getString(R.string.eng_sync_mal_occured);
                    } else {
                        EventType = Constants.EngineSyncDiagnosticEvent;
                        MalfunctionDefinition = getString(R.string.eng_sync_dia_occured);
                    }

                    // save occurred event in malfunction/diagnostic table
                    DateTime currentDateTime = Globally.GetCurrentUTCDateTime();
                    DateTime eventStartTime = currentDateTime.minusMinutes((int) timeDiff);
                    String lastSavedOdometer = SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext());
                    String lastSavedEngHr = SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext());
                    String currentOdometer = SharedPref.getObdOdometer(getApplicationContext());
                    String currentEngHr = Constants.get2DecimalEngHour(getApplicationContext());

                    JSONObject newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                            DriverId, DeviceId, SharedPref.getVINNumber(getApplicationContext()),
                            SharedPref.getTruckNumber(getApplicationContext()), //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
                            DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                            lastSavedEngHr,
                            lastSavedOdometer,
                            currentOdometer,
                            eventStartTime.toString(), EventType, MalfunctionDefinition,
                            true, currentDateTime.toString(), currentOdometer, currentEngHr,
                            Constants.getLocationType(getApplicationContext()), ""
                    );

                    // save Occurred event locally until not posted to server
                    JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
                    malArray.put(newOccuredEventObj);
                    malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, malArray);


                    // save malfunction entry in duration table
                    JSONObject newItemObj = malfunctionDiagnosticMethod.getNewMalDiaDurationObj(DriverId, eventStartTime.toString(),
                            currentDateTime.toString(), EventType, (int) timeDiff,
                            true, currentEngHr, currentOdometer, lastSavedOdometer, lastSavedEngHr, EventType,
                            "", SharedPref.getVINNumber(getApplicationContext()));

                    JSONArray array = malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper);
                    array.put(newItemObj);

                    // save in db
                    malfunctionDiagnosticMethod.MalDiaDurationHelper(dbHelper, array);


                    // update mal/dia status for enable disable according to log
                    malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                    global.ShowLocalNotification(getApplicationContext(),
                            getString(R.string.dia_event),
                            getString(R.string.eng_sync_dia_occ_with_pwr), 2090);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void checkEngineSyncMalDiaOccurredEvent(int speed, boolean isDriverSwitched, boolean isOccurredWithPowerEvent, int time){

        try {

            if(!SharedPref.isEngSyncMalfunction(getApplicationContext()) ) {
                boolean isEngineSyncMalAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncMal, getApplicationContext());
                boolean isEngineSyncDiaAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncDiag, getApplicationContext());
                String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());
                // Logger.LogDebug("lastIgnitionStatus", "lastIgnitionStatus: " +lastIgnitionStatus );
                boolean isCoDriverEngSyncDia =  SharedPref.isCoDriverEngSyncDia(getApplicationContext());

                // check co driver eng sync dia status if already exist then ignore
                if(!isCoDriverEngSyncDia) {
                    if ((isEngineSyncMalAllowed || isEngineSyncDiaAllowed) && lastIgnitionStatus.equals("ON")) {

                        DateTime disconnectTime = global.getDateTimeObj(SharedPref.getObdLastStatusTime(getApplicationContext()), false);
                        DateTime currentTime = global.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext());

                        if (disconnectTime != null && currentTime != null) {
                            int timeInSec = (int) Constants.getDateTimeDuration(disconnectTime, currentTime).getStandardSeconds();

                            if (isOccurredWithPowerEvent) {
                                timeInSec = time;
                            }

                            if (timeInSec >= 70 || isDriverSwitched) {
                                String currentDate = global.GetCurrentUTCTimeFormat();

                                boolean isEngSyncDiaOccurred = SharedPref.isEngSyncDiagnstc(getApplicationContext());


                                if (!isEngSyncDiaOccurred && isEngineSyncDiaAllowed) {

                                    obdUtil.syncObdSingleLog(getApplicationContext(), DriverId, getDriverName(), 10);


                                    SharedPref.saveEngSyncDiagnstcStatus(true, getApplicationContext());
                                    constants.saveDiagnstcStatus(getApplicationContext(), true);
                                    SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                    SharedPref.setEngSyncClearEventCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                                    // save occurred event in malfunction/diagnostic table
                                    saveMalfunctionEventInTable(Constants.EngineSyncDiagnosticEvent,
                                            getString(R.string.eng_sync_dia_occured),
                                            currentDate, true);


                                    // save malfunction entry in duration table
                                    malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                                            currentDate,
                                            currentDate,
                                            Constants.EngineSyncDiagnosticEvent, "-1", Constants.EngineSyncDiagnosticEvent, "",
                                            constants, getApplicationContext());

                                    // update mal/dia status for enable disable according to log
                                    malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                                    updateMalDiaInfoWindowAtHomePage();

                                    // update malfunction & diagnostic Fragment screen
                                    updateMalDiaFragment(true);

                                    global.ShowLocalNotification(getApplicationContext(),
                                            getString(R.string.dia_event),
                                            getString(R.string.eng_sync_dia_occured_desc), 2090);

                                    Globally.PlaySound(getApplicationContext());

                                    constants.saveObdData(constants.getObdSource(getApplicationContext()),
                                            "EngSyncDia@@ - lastIgnitionStatus: " + lastIgnitionStatus,
                                            "disconnectTime: " +disconnectTime + ", Current: " +currentTime,
                                            currentHighPrecisionOdometer, currentHighPrecisionOdometer,
                                            "", ignitionStatus, truckRPM, String.valueOf(speed),
                                            "", obdEngineHours, global.GetDriverCurrentDateTime(global, getApplicationContext()), "",
                                            DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());

                                } else {

                                    if (isEngineSyncMalAllowed) {

                                        String clearEventLastCallTime = SharedPref.getEngSyncMalEventCallTime(getApplicationContext());
                                        if (clearEventLastCallTime.length() == 0) {
                                            SharedPref.setEngSyncMalEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                        }

                                        // Checking after 1 min
                                        int minDiffMalfunction = constants.minDiffMalfunction(clearEventLastCallTime, global, getApplicationContext());
                                        if (minDiffMalfunction > 0) {
                                            // update call time
                                            SharedPref.setEngSyncMalEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                                            double last24HrEngSyncEventTime = malfunctionDiagnosticMethod.getLast24HrEngSyncEvents(dbHelper);  //Constants.EngineSyncDiagnosticEvent,"", 0.0, constants, driverPermissionMethod, obdUtil,
//                                            int minDiff = constants.getMinDifference(SharedPref.getObdLastStatusTime(getApplicationContext()),
//                                                    global.GetDriverCurrentDateTime(global, getApplicationContext()));
//                                            double totalEngSyncMissingMin = last24HrEngSyncEventTime;
                                            Logger.LogDebug("last24HrEng", String.valueOf(last24HrEngSyncEventTime));
                                            if (last24HrEngSyncEventTime >= Constants.PowerEngSyncMalOccTime) {

                                                constants.saveObdData(constants.getObdSource(getApplicationContext()),
                                                        "EngSyncMal@@ - lastIgnitionStatus: " + lastIgnitionStatus,
                                                        "", currentHighPrecisionOdometer,
                                                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                                                        "", obdEngineHours, global.GetDriverCurrentDateTime(global, getApplicationContext()), "",
                                                        DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());

                                                obdUtil.syncObdSingleLog(getApplicationContext(), DriverId, getDriverName(), 10);


                                                SharedPref.saveEngSyncMalfunctionStatus(true, getApplicationContext());
                                                constants.saveMalfncnStatus(getApplicationContext(), true);
                                                SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                                                // save occurred event in malfunction/diagnostic table
                                                saveMalfunctionEventInTable(Constants.EngineSyncMalfunctionEvent,
                                                        getString(R.string.eng_sync_mal_occured),
                                                        currentDate, true);  //SharedPref.getObdLastStatusUtcTime(getApplicationContext())


                                                // save malfunction entry in duration table
                                                malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                                                        currentDate,    //currentDate,
                                                        currentDate,
                                                        Constants.EngineSyncMalfunctionEvent, "-1",
                                                        Constants.EngineSyncMalfunctionEvent, "", constants, getApplicationContext());

                                                // update mal/dia status for enable disable according to log
                                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                                                updateMalDiaInfoWindowAtHomePage();

                                                // update malfunction & diagnostic Fragment screen
                                                updateMalDiaFragment(true);

                                                global.ShowLocalNotification(getApplicationContext(),
                                                        getString(R.string.malfunction_event),
                                                        getString(R.string.eng_sync_mal_occured), 2090);

                                                Globally.PlaySound(getApplicationContext());


                                            }

                                        }else {
                                            if(minDiffMalfunction < 0){
                                                SharedPref.setEngSyncMalEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                            }
                                        }

                                    }



                                }


                            }else{
                                if(timeInSec < 0){
                                    SharedPref.SaveObdStatus(SharedPref.getObdStatus(getApplicationContext()), global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
                                }
                            }
                        }

                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void checkEngSyncClearEvent(){
        try{

            if(SharedPref.isEngSyncDiagnstc(getApplicationContext()) || isTeamDriverLogin) {

                boolean HasEngSyncEventForClear = malfunctionDiagnosticMethod.HasEngSyncEventForClear(Constants.EngineSyncDiagnosticEvent,
                        dbHelper, constants, global, getApplicationContext());
                if (HasEngSyncEventForClear) {
                    ClearEventUpdate(DriverId, Constants.EngineSyncDiagnosticEvent,
                            "Auto clear engine sync diagnostic event", 0);

                    // clear co driver engine sync event if occurred
                    if (isTeamDriverLogin) {
                        ClearEventUpdate(CoDriverId, Constants.EngineSyncDiagnosticEvent,
                                "Auto clear engine sync diagnostic event", 0);
                    }

                    if (!SharedPref.isLocDiagnosticOccur(getApplicationContext()) && !SharedPref.isEngSyncDiagnstc(getApplicationContext())) {
                        SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getApplicationContext()),
                                SharedPref.isMalfunctionOccur(getApplicationContext()),
                                false,
                                SharedPref.isSuggestedEditOccur(getApplicationContext()), getApplicationContext());
                    }


                    malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                    // malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(CoDriverId, global, constants, dbHelper, getApplicationContext());

                    SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
                    SharedPref.saveEngSyncMalfunctionStatus(SharedPref.isEngSyncMalfunction(getApplicationContext()), getApplicationContext());

                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }



    // check Power Data Compliance Malfunction/Diagnostic event
    private void checkPowerMalDiaEvent(){

        boolean isEventOccurred = false;
        String currentDate = Globally.GetCurrentUTCTimeFormat();
        try{
            if(SharedPref.isPowerMalfunctionOccurred(getApplicationContext()) == false) {
                isEventOccurred = true;
                boolean isPowerCompMalAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerComplianceMal, getApplicationContext());
                boolean isPowerCompDiaAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerDataDiag, getApplicationContext());

                if (isPowerCompMalAllowed || isPowerCompDiaAllowed) {

                    String PowerEventStatus = constants.isPowerDiaMalOccurred(currentHighPrecisionOdometer, ignitionStatus,
                            obdEngineHours, DriverId, global, malfunctionDiagnosticMethod, isPowerCompMalAllowed, isPowerCompDiaAllowed,
                            getApplicationContext(), constants, dbHelper, driverPermissionMethod, obdUtil);

                    if (PowerEventStatus.length() > 0) {

                        // Engine sync event occurred with idle truck
                        if(SharedPref.isAlsoSaveEngSyncDiaEvent(getApplicationContext())){
                            SharedPref.saveEngSyncEventAlso(false, Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                            String[] timeArray = PowerEventStatus.split(",");
                            if(timeArray.length > 0) {
                                double time = Double.parseDouble(timeArray[1].trim());
                                saveEngSyncEventWithPowerEvent(time);
                            }
                        }


                        if (PowerEventStatus.contains(constants.MalfunctionEvent)) {
                            if (isPowerCompMalAllowed) {

                                if (isPowerCompDiaAllowed) {
                                    currentDate = SharedPref.GetTruckInfoOnIgnitionChange(Constants.IgnitionUtcTimeMalDia, getApplicationContext());

                                    // save power diagnostic event also when malfunction occurred
                                    savePowerDiagnosticRecordInTable(currentDate, false);
                                    SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                }
                                currentDate = global.GetCurrentUTCTimeFormat();

                                // updated values after save diagnostic with truck ignition status because in this case we are saving last ignition time in diagnostic
                                SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, Constants.WiredOBD,
                                        global.GetDriverCurrentDateTime(global, getApplicationContext()), currentDate,
                                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()),
                                        getApplicationContext());

                                savePowerMalfunctionRecordInTable(currentDate);

                            }
                        } else {
                            if (isPowerCompDiaAllowed) {

                                currentDate = SharedPref.GetTruckInfoOnIgnitionChange(Constants.IgnitionUtcTimeMalDia, getApplicationContext());

                                savePowerDiagnosticRecordInTable(currentDate, true);

                                // update mal/dia status for enable disable according to log
                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());
                                SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                            }
                        }

                        // reset api call count
                        failedApiTrackMethod.isAllowToCallOrReset(dbHelper, APIs.MALFUNCTION_DIAGNOSTIC_EVENT,
                                true, global, getApplicationContext());


                    }

                    // save updated values with truck ignition status
                    SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, Constants.WiredOBD, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                            global.GetCurrentUTCTimeFormat(),
                            SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                            SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()),
                            getApplicationContext());


                }
            }

            // check if Malfunction/Diagnostic event occurred in ECM disconnection
            if (SharedPref.isPowerDiagnosticOccurred(getApplicationContext())) {
                isEventOccurred = true;
                String PowerClearEventCallTime = SharedPref.getPowerClearEventCallTime(getApplicationContext());
                if(PowerClearEventCallTime.length() == 0){
                    PowerClearEventCallTime = global.GetDriverCurrentDateTime(global, getApplicationContext());
                    SharedPref.setPowerClearEventCallTime(PowerClearEventCallTime, getApplicationContext());
                }
                String dateee = SharedPref.getPowerMalOccTime(getApplicationContext());
                int minDiff = constants.getMinDifference(dateee, currentDate);
                int callTimeSecDiff = constants.getSecDifference(PowerClearEventCallTime, global.getCurrentDate());

                // clear Power Diagnostic event after 2 min automatically when ECM is connected.
                if (minDiff > 1 && callTimeSecDiff > 18) {  // callTimeMinDiff this check is used to avoid clear method calling after 3 sec each because checkPowerMalDiaEvent is calling after 3 sec when data coming from obd
                    SharedPref.setPowerClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    String eventOccuredDriverId = "";
                    powerEventInputArray = new JSONArray();

                    if (!global.isSingleDriver(getApplicationContext())) {
                        eventOccuredDriverId = malfunctionDiagnosticMethod.getEventOccurredActualDriverid(dbHelper, Constants.PowerComplianceDiagnostic);
                        if(eventOccuredDriverId.length() == 0){
                            eventOccuredDriverId = DriverId;
                        }
                        ClearEventUpdate(eventOccuredDriverId, Constants.PowerComplianceDiagnostic,
                                "Auto clear Power data diagnostic event after 1 min of ECM data received", minDiff);
                    }else{
                        ClearEventUpdate(DriverId, Constants.PowerComplianceDiagnostic,
                                "Auto clear Power data diagnostic event after 1 min of ECM data received", minDiff);
                    }


                    if(powerEventInputArray.length() == 0){
                        DateTime clearEventCallTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext()).plusSeconds(40);
                        SharedPref.setPowerClearEventCallTime(clearEventCallTime.toString(), getApplicationContext());

                        if (global.isConnected(getApplicationContext())) {
                            JSONArray savedEvents = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
                            if(savedEvents.length() > 0){
                                SaveMalfnDiagnstcLogToServer(savedEvents, DriverId);

                                constants.saveObdData(constants.getObdSource(getApplicationContext()),
                                        "PowerClearEvent@@: " + savedEvents,
                                        "", "", currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "" + obdVehicleSpeed,
                                        "", obdEngineHours, Globally.GetDriverCurrentDateTime(global, getApplicationContext()), "",
                                        DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());


                            }else {
                                GetMalDiaEventsDurationList();

                                constants.saveObdData(constants.getObdSource(getApplicationContext()),
                                        "PowerClearSavedEvent@@: Event Api called",
                                        "", "", currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "" + obdVehicleSpeed,
                                        "", obdEngineHours, Globally.GetDriverCurrentDateTime(global, getApplicationContext()), "",
                                        DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());


                            }

                        }
                    }else {
                        if (!global.isConnected(getApplicationContext())) {
                            global.ShowLocalNotification(getApplicationContext(),
                                    getString(R.string.event_cleared),
                                    getString(R.string.power_dia_clear_desc), 2093);
                        }

                        constants.saveObdData(constants.getObdSource(getApplicationContext()),
                                "PowerClearEvent: " + powerEventInputArray,
                                "", "", currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "" + obdVehicleSpeed,
                                "", obdEngineHours, Globally.GetDriverCurrentDateTime(global, getApplicationContext()), "",
                                DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());


                        malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                        if (!SharedPref.isLocDiagnosticOccur(getApplicationContext()) && !SharedPref.isEngSyncDiagnstc(getApplicationContext())) {
                            SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getApplicationContext()),
                                    SharedPref.isMalfunctionOccur(getApplicationContext()),
                                    false,
                                    SharedPref.isSuggestedEditOccur(getApplicationContext()), getApplicationContext());
                        }

                        SharedPref.savePowerMalfunctionOccurStatus(
                                SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                                false, currentDate, getApplicationContext());


                        updateMalDiaInfoWindowAtHomePage();
                        // update malfunction & diagnostic Fragment screen
                        updateMalDiaFragment(true);
                    }

                } else {
                    // update EndTime with TotalMinutes instantly but not cleared, because we are clearing it after 5 min
                    //  malfunctionDiagnosticMethod.updateTimeInPowerDiagnoseDia(dbHelper, getApplicationContext());
                    if(callTimeSecDiff < 0){
                        SharedPref.setPowerClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    }
                }

            }

            // this case is for engine sync event because of truck ignition was not updated if power event was already occurred
            if(!isEventOccurred){
                SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, SharedPref.getObdSourceName(getApplicationContext()),
                        global.GetDriverCurrentDateTime(global, getApplicationContext()), currentDate,
                        obdEngineHours, HighResolutionDistance, getApplicationContext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void saveStorageMalfunction(){
        try{
            // save occurred event in malfunction/diagnostic table
            saveMalfunctionEventInTable(constants.DataRecordingComplianceMalfunction, getString(R.string.data_recording_mal_occured),
                    Globally.GetCurrentUTCTimeFormat(), true);

            // save malfunction entry in duration table
            malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                    Globally.GetCurrentUTCTimeFormat(),
                    global.GetCurrentUTCTimeFormat(),
                    Constants.DataRecordingComplianceMalfunction,  "-1",
                    Constants.DataRecordingComplianceMalfunction, "", constants, getApplicationContext());

            // update mal/dia status for enable disable according to log
            malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

            updateMalDiaInfoWindowAtHomePage();
            // update malfunction & diagnostic Fragment screen
            updateMalDiaFragment(true);

            Globally.PlayNotificationSound(getApplicationContext());
            global.ShowLocalNotification(getApplicationContext(),
                    getString(R.string.malfunction_events),
                    getString(R.string.data_rec_mal_def_noti), 2098);


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void clearStorageMalEvent(){
        ClearEventUpdate(DriverId, Constants.DataRecordingComplianceMalfunction, getString(R.string.data_rec_mal_cleared_def), 0);

        malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

        updateMalDiaInfoWindowAtHomePage();
        // update malfunction & diagnostic Fragment screen
        updateMalDiaFragment(true);

        Globally.PlayNotificationSound(getApplicationContext());
        global.ShowLocalNotification(getApplicationContext(),
                getString(R.string.event_cleared),
                getString(R.string.data_rec_mal_cleared_def), 2098);

    }


    void savePowerDiagnosticRecordInTable(String currentDate, boolean isUploadNow){
        try{

            SharedPref.savePowerMalfunctionOccurStatus(
                    SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                    true, currentDate, getApplicationContext());

            constants.saveDiagnstcStatus(getApplicationContext(), true);

            // save occurred event in diagnostic table
            saveMalfunctionEventInTable(constants.PowerComplianceDiagnostic, getString(R.string.power_dia_occured),
                    currentDate, isUploadNow);

            DateTime endDateTime = Globally.getDateTimeObj(Globally.GetCurrentUTCTimeFormat(), false).plusSeconds(90);
            // save diagnostic entry in duration table
            malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                    currentDate,
                    endDateTime.toString(),
                    Constants.PowerComplianceDiagnostic,  "-1",
                    constants.PowerComplianceDiagnostic, "", constants, getApplicationContext());



            writePowerEventLog();

        }catch (Exception e){e.printStackTrace();}
    }


    void savePowerMalfunctionRecordInTable(String currentDate){
        try{
            SharedPref.savePowerMalfunctionOccurStatus(true,
                    SharedPref.isPowerDiagnosticOccurred(getApplicationContext()),
                    currentDate, getApplicationContext()); //global.GetDriverCurrentDateTime(global, getApplicationContext())

            // save occurred event in malfunction table
            saveMalfunctionEventInTable(constants.PowerComplianceMalfunction,
                    getString(R.string.power_comp_mal_occured),
                    currentDate, true );

            // save malfunction entry in duration table
            malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                    currentDate ,
                    currentDate,
                    Constants.PowerComplianceMalfunction,  "-1",
                    constants.PowerComplianceMalfunction, "", constants, getApplicationContext());

            // update mal/dia status for enable disable according to log
            malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

            constants.saveMalfncnStatus(getApplicationContext(), true);
            SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

            writePowerEventLog();

        }catch (Exception e){e.printStackTrace();}

    }


    private void writePowerEventLog(){
        try{
            JSONArray array = malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper);
            if(array.length() > 0) {
                JSONObject eventObj = (JSONObject) array.get(array.length() - 1);

                constants.saveObdData(constants.getObdSource(getApplicationContext()), "Power Event: "+ eventObj,
                        "", "",
                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, ""+obdVehicleSpeed,
                        "", obdEngineHours, Globally.GetDriverCurrentDateTime(global, getApplicationContext()), Globally.GetDriverCurrentDateTime(global, getApplicationContext()),
                        DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // check Positioning compliance malfunction Event
    private void checkPositionMalfunction(String currentHighPrecisionOdometer, String currentLogDate){

        try {
            if(SharedPref.isLocMalfunctionOccur(getApplicationContext()) == false) {
                boolean isPositionMalAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PostioningComplMal, getApplicationContext());
                if (isPositionMalAllowed) {
                    String lastCalledTime = SharedPref.getLastMalfCallTime(getApplicationContext());
                    if (lastCalledTime.length() == 0) {
                        SharedPref.setMalfCallTime(currentLogDate, getApplicationContext());
                    }

                    // Checking after 1 min
                    int minDiff = constants.minDiffMalfunction(lastCalledTime, global, getApplicationContext());
                    if (minDiff > 0) {
                        SharedPref.setMalfCallTime(currentLogDate, getApplicationContext());

                    /* this check is added if driver use book after long time and diagnostic already occurred.
                     When book started OnLocationChanged not called instantly then due to blank latitude malfunction can occur.
                     So to avoid this we will check after 1 min */
                        if (minDiff < 5) {
                            String currentDate = global.GetCurrentUTCTimeFormat();
                            if (Globally.LATITUDE.length() < 5 && SharedPref.getEcmObdLatitude(getApplicationContext()).length() > 4) {
                                SharedPref.setEcmObdLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE,
                                        currentHighPrecisionOdometer, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                        currentDate, getApplicationContext());
                            }

                            // check position malfunction event
                            String locMalDiaEvent = constants.isPositionMalfunctionEvent( DriverId, malfunctionDiagnosticMethod,
                                    dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());
                            if (locMalDiaEvent.length() > 0) {
                                SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                if (locMalDiaEvent.equals("M")) {   // && SharedPref.isLocMalfunctionOccur(getApplicationContext()) == false

                                    // save occurred event in malfunction/diagnostic table
                                    saveMalfunctionEventInTable(Constants.PositionComplianceMalfunction, getString(R.string.pos_mal_occured),
                                            currentDate, true);  //SharedPref.getEcmObdUtcTime(getApplicationContext())

                                    // save malfunction entry in duration table
                                    malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                                            currentDate, currentDate,
                                            Constants.PositionComplianceMalfunction,  "-1",
                                            Constants.PositionComplianceMalfunction, "", constants, getApplicationContext());

                                    // update mal/dia status for enable disable according to log
                                    malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                                    SharedPref.saveLocMalfunctionOccurStatus(true, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                            currentDate, getApplicationContext());
                                    constants.saveMalfncnStatus(getApplicationContext(), true);

                                    Globally.PlayNotificationSound(getApplicationContext());
                                    global.ShowLocalNotification(getApplicationContext(),
                                            getString(R.string.malfunction_events),
                                            getString(R.string.pos_mal_event_desc), 2091);

                                    Globally.PlaySound(getApplicationContext());


                                    updateMalDiaInfoWindowAtHomePage();

                                    // update malfunction & diagnostic Fragment screen
                                    updateMalDiaFragment(true);

                                } else {
                                    //  Globally.PlayNotificationSound(getApplicationContext());
                                    global.ShowLocalNotification(getApplicationContext(),
                                            getString(R.string.missing_loc),
                                            getString(R.string.pos_dia_event_desc), 2091);

                                    Globally.PlaySound(getApplicationContext());


                                }
                            }
                        }

                        // check Positioning Diagnostic Clear Event
                        if (Globally.LATITUDE.length() > 4 && SharedPref.isLocDiagnosticOccur(getApplicationContext())) {
                            malfunctionDiagnosticMethod.updateTimeOnLocationReceived(dbHelper, getApplicationContext()); //Constants.ConstLocationMissing,
                            SharedPref.saveLocDiagnosticStatus(false, "", "", getApplicationContext());
                        }

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    private void clearPowerEventAtLogout(){
        try{
            JSONArray clearEventArray = malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, DriverId, Constants.PowerComplianceDiagnostic,
                    true, true, getApplicationContext());
            ClearEventType = Constants.PowerComplianceDiagnostic;

            if(clearEventArray.length() > 0 && global.isConnected(getApplicationContext())) {
                // call clear event API.
                saveDriverLogPost.PostDriverLogData(clearEventArray, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT_BY_DATE,
                        Constants.SocketTimeout30Sec, true, false, 0, ClearMalDiaEvent);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void ClearMissingDiagnostics(){
        try{
            int rpm = Integer.valueOf(truckRPM);
            if(rpm > 400){
                boolean isMissingDiaOccur = SharedPref.isMissingDiaOccur(getApplicationContext());
                if(isMissingDiaOccur && !IsMissingDiaInProgress ){

                    IsMissingDiaInProgress = true;  // this check is use to avoid call again and again. When timer interval will be 0 if will be false;
                    ClearEventUpdate(DriverId, Constants.MissingDataDiagnostic, getString(R.string.missing_event_cleared_def), 0);

                    malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                    updateMalDiaInfoWindowAtHomePage();
                    // update malfunction & diagnostic Fragment screen
                    updateMalDiaFragment(true);

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void saveMissingDiagnostic(String remarks, String type){
        try {
            boolean IsAllowMissingDataDiagnostic = SharedPref.GetOtherMalDiaStatus(ConstantsKeys.MissingDataDiag, getApplicationContext());
            String RPM = SharedPref.getRPM(getApplicationContext());

            if((RPM.equals("0") || !constants.isObdConnectedWithELD(getApplicationContext()) ) &&
                    IsAllowMissingDataDiagnostic && !constants.isExemptDriver(getApplicationContext())) {

                //   boolean isMissingEventAlreadyWithStatus = malfunctionDiagnosticMethod.isMissingEventAlreadyWithOtherJobs(type, dbHelper);

                String desc = "";
                if(RPM.equals("0")){
                    remarks = "Vehicle ignition is off at ";
                    desc = " due to Vehicle ignition is off.";
                }else {
                    desc = " due to OBD not connected with E-Log Book";
                }

                //     if (!isMissingEventAlreadyWithStatus) {
                // save malfunction occur event to server with few inputs
                JSONObject newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                        DriverId, DeviceId, SharedPref.getVINNumber(getApplicationContext()),
                        SharedPref.getTruckNumber(getApplicationContext()),
                        DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                        constants.get2DecimalEngHour(getApplicationContext()),
                        SharedPref.getObdOdometer(getApplicationContext()),
                        SharedPref.getObdOdometer(getApplicationContext()),
                        Globally.GetCurrentUTCTimeFormat(), constants.MissingDataDiagnostic,
                        remarks + " " + type, false,
                        "", "", "",
                        "", type);  //Constants.getLocationType(getApplicationContext())

                // save Occurred event locally until not posted to server
                JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
                malArray.put(newOccuredEventObj);
                malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, malArray);

                // save malfunction entry in duration table
                malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                        Globally.GetCurrentUTCTimeFormat(), Globally.GetCurrentUTCTimeFormat(),
                        Constants.MissingDataDiagnostic, type, "",
                        type, constants, getApplicationContext());

                SharedPref.saveMissingDiaStatus(true, getApplicationContext());

                Globally.PlayNotificationSound(getApplicationContext());
                Globally.ShowLocalNotification(getApplicationContext(),
                        getString(R.string.missing_dia_event),
                        getString(R.string.missing_event_occured_desc) + " in " +
                                type + desc, 2091);


                SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getApplicationContext()),
                        SharedPref.isMalfunctionOccur(getApplicationContext()), true,
                        SharedPref.isSuggestedEditOccur(getApplicationContext()), getApplicationContext());

                updateMalDiaInfoWindowAtHomePage();

                // update malfunction & diagnostic Fragment screen
                updateMalDiaFragment(true);


                //   }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void saveTimingMalfunctionEvent(String remarks, String currentDateTime){
        try {

            String eventType = "Timing compliance malfunction event";
            // save malfunction occur event to server with few inputs
            JSONObject newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                    DriverId, DeviceId, SharedPref.getVINNumber(getApplicationContext()),
                    SharedPref.getTruckNumber(getApplicationContext()),   //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
                    DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                    constants.get2DecimalEngHour(getApplicationContext()), //SharedPref.getObdEngineHours(getApplicationContext()),
                    SharedPref.getObdOdometer(getApplicationContext()),
                    SharedPref.getObdOdometer(getApplicationContext()),
                    currentDateTime, Constants.TimingComplianceMalfunction,
                    remarks + " " + eventType,
                    false, "", "",
                    "", "", eventType);

            // save Occurred event locally until not posted to server
            JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
            malArray.put(newOccuredEventObj);
            malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, malArray);


            // Clear warning time after event creation.
            SharedPref.saveTimingMalfunctionWarningTime("", getApplicationContext());

            SharedPref.saveTimingMalfunctionStatus(true, currentDateTime, getApplicationContext());

            global.ShowLocalNotification(getApplicationContext(), getString(R.string.timing_comp_mal_event),  getString(R.string.timing_comp_mal_desc), 20910);


            // save malfunction entry in duration table
            malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                    currentDateTime, currentDateTime,
                    Constants.TimingComplianceMalfunction, eventType, "", eventType,
                    constants, getApplicationContext());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void CheckEventsForClear(String DriverId, String EventCode, boolean isUpdate, int minDiff){
        try{

            if(SharedPref.isMalfunctionOccur(getApplicationContext()) || SharedPref.isDiagnosticOccur(getApplicationContext()) ){

                String clearEventLastCallTime = SharedPref.getClearEventCallTime(getApplicationContext());
                if (clearEventLastCallTime.length() == 0) {
                    SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                }

                // Checking after 60 sec
                int secDiff = constants.secDiffMalfunction(clearEventLastCallTime, global, getApplicationContext());
                if (secDiff >= 30) {
                    // update call time
                    SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                    JSONArray clearEventArray = malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, DriverId, EventCode, true, isUpdate, getApplicationContext());
                    ClearEventType = EventCode;

                    if(ClearEventType.equals(Constants.PowerComplianceDiagnostic)){
                        powerEventInputArray = clearEventArray;
                    }

                    // save data in unposted event array if api failed or internet issue. Clearing api response.
                    malfunctionDiagnosticMethod.addOccurEventUploadStatus(clearEventArray, dbHelper);

                    /* We have 2 api for clear event. 1 for online events and 2nd is use here to clear in offline and input data as array.*/
                    if(clearEventArray.length() > 0){

                        // call clear event API.
                        if(global.isConnected(getApplicationContext()) && !isMalfncDataAlreadyPosting) {
                            isMalfncDataAlreadyPosting = true;
                            saveDriverLogPost.PostDriverLogData(clearEventArray, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT_BY_DATE,
                                    Constants.SocketTimeout30Sec, true, false, 0, ClearMalDiaEvent);
                        }else{


                            updateMalDiaInfoWindowAtHomePage();

                            // update malfunction & diagnostic Fragment screen
                            updateMalDiaFragment(true);

                            String eventName = constants.getMalDiaEventDetails(getApplicationContext(), ClearEventType).getEventTitle() + " ";
                            global.ShowLocalNotification(getApplicationContext(), getString(R.string.event_cleared),
                                    eventName + getString(R.string.clear_event_desc), 2090);
                        }

                    }else{
                        if(ClearEventType.equals(Constants.PowerComplianceDiagnostic)){

                            if(minDiff > dayInMinDuration) {
                                SharedPref.savePowerMalfunctionOccurStatus(
                                        false,
                                        false, global.GetCurrentUTCTimeFormat(), getApplicationContext());
                            }else{
                                SharedPref.savePowerMalfunctionOccurStatus(
                                        SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                                        false, global.GetCurrentUTCTimeFormat(), getApplicationContext());
                            }
                        }else if(ClearEventType.equals(Constants.EngineSyncDiagnosticEvent)){

                            SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
                            constants.saveDiagnstcStatus(getApplicationContext(), false);
                        }

                        updateMalDiaInfoWindowAtHomePage();

                        // update malfunction & diagnostic Fragment screen
                        updateMalDiaFragment(true);
                    }

                }else{
                    if(secDiff < 0){
                        // update call time
                        SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    }

                    if(EventCode.equals(Constants.MissingDataDiagnostic)){
                        IsMissingDiaInProgress = false;
                    }
                }



            }else{
                if(isTeamDriverLogin && SharedPref.isCoDriverEngSyncDia(getApplicationContext())){
                    String clearEventLastCallTime = SharedPref.getClearEventCallTime(getApplicationContext());
                    if (clearEventLastCallTime.length() == 0) {
                        SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    }

                    // Checking after 1 min
                    if (constants.minDiffMalfunction(clearEventLastCallTime, global, getApplicationContext()) > 0) {
                        // update call time
                        SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                        JSONArray clearEventArray = malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, CoDriverId, EventCode,
                                true, isUpdate, getApplicationContext());
                        ClearEventType = EventCode;

                        // save data in unposted event array if api failed or internet issue. Clearing api response.
                        malfunctionDiagnosticMethod.addOccurEventUploadStatus(clearEventArray, dbHelper);

                        if(clearEventArray.length() > 0){
                            // call clear event API.
                            if(global.isConnected(getApplicationContext()) && !isMalfncDataAlreadyPosting ) {
                                isMalfncDataAlreadyPosting = true;
                                saveDriverLogPost.PostDriverLogData(clearEventArray, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT_BY_DATE,
                                        Constants.SocketTimeout30Sec, true, false, 0, ClearMalDiaEvent);
                            }else {
                                malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, CoDriverId, ClearEventType, true, true, getApplicationContext());
                                SharedPref.saveCoDriverEngSyncDiaStatus(false, getApplicationContext());

                                global.ShowLocalNotification(getApplicationContext(), getString(R.string.event_cleared),
                                        getString(R.string.codr_eng_sync_dia_clear_desc), 2090);

                            }

                        }

                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    // update clear event info in existing log if not uploaded on server yet
    private void ClearEventUpdate(String DriverId, String dataDiagnostic, String clearDesc, int dayInMin){
        try{

            SharedPref.saveEngSyncEventAlso(false, "", getApplicationContext());

            boolean isUnPostedEvent = malfunctionDiagnosticMethod.isUnPostedOfflineEvent(dataDiagnostic, dbHelper);
            if (isUnPostedEvent) {
                // update clear event array in duration table and not posted to server with duration table input because occurred event already exist TABLE_MALFUNCTION_DIANOSTIC
                malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, DriverId, dataDiagnostic, true, true, getApplicationContext());

                // update offline unposted event array
                JSONArray malArray = malfunctionDiagnosticMethod.updateOfflineUnPostedMalDiaEvent(DriverId, dataDiagnostic,
                        clearDesc, dbHelper, getApplicationContext());

                // call api
                SaveMalfnDiagnstcLogToServer(malArray, DriverId);

                constants.saveObdData(constants.getObdSource(getApplicationContext()), "Clear Event:- " +
                                dataDiagnostic, "Unposted Event Count: " + malArray.length(), "",
                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "" + obdVehicleSpeed,
                        "", obdEngineHours, global.GetDriverCurrentDateTime(global, getApplicationContext()), "",
                        DriverId, dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());

                if(dataDiagnostic.equals(Constants.PowerComplianceDiagnostic)){
                    powerEventInputArray = malArray;
                }

            } else {
                CheckEventsForClear(DriverId, dataDiagnostic, true, dayInMin);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    // update clear event info in existing log if not uploaded on server yet
    private void ClearLocationMissingEvent(String DriverId, String eventTime, String eventStatus){
        try{

            boolean isUnPostedEvent = malfunctionDiagnosticMethod.isUnPostedMissingEventToClear(eventTime, dbHelper);
            if (isUnPostedEvent) {
                // update clear event array in duration table and not posted to server with duration table input because occured event already exist TABLE_MALFUNCTION_DIANOSTIC
                malfunctionDiagnosticMethod.updateMissingDataToClear(eventTime, eventStatus, getApplicationContext(), dbHelper);

                // update offline unposted event array
                JSONArray missingEventArray = malfunctionDiagnosticMethod.updateOfflineUnPostedMissingEvent( eventTime,
                        "Missing diagnostic event has been cleared for this status", dbHelper, getApplicationContext());

                // call api
                SaveMalfnDiagnstcLogToServer(missingEventArray, DriverId);


            } else {

                // checking Missing Event with log time if match then update with clear data and Upload to server
                JSONArray missingEventArray = malfunctionDiagnosticMethod.updateMissingDataToClear(eventTime, eventStatus, getApplicationContext(), dbHelper);
                if(missingEventArray.length() > 0){

                    ClearEventType = Constants.MissingDataDiagnostic;
                    if(missingEventArray.length() > 0 && global.isConnected(getApplicationContext()) && !isMalfncDataAlreadyPosting) {
                        isMalfncDataAlreadyPosting = true;
                        // call clear event API.
                        saveDriverLogPost.PostDriverLogData(missingEventArray, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT_BY_DATE,
                                Constants.SocketTimeout30Sec, true, false, 0, ClearMalDiaEvent);
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    void updateMalDiaInfoWindowAtHomePage(){
        try{
            Intent intent = new Intent(ConstantsKeys.IsIgnitionOn);
            intent.putExtra(ConstantsKeys.IsIgnitionOn, false);
            intent.putExtra(ConstantsKeys.IsNeedToUpdate18DaysLog, false);
            intent.putExtra(ConstantsKeys.IsUpdateMalDiaInfoWindow, true);
            LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void saveLogWithRuleCall(String currentHighPrecisionOdometer, String currentLogDate, int speed, String status){

        try {

            if(!status.equals("not_saved")) {
                // save current HighPrecisionOdometer in DB
                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                if (speed > 200) {
                    speed = -1;
                }
            }

            callEldRuleForWired(speed);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    // Save Malfunction/Diagnostic Event Details
    void saveMalfunctionEventInTable(String malDiaType, String MalfunctionDefinition, String occurredTime, boolean isUploadNow){

        String clearedTime = "", clearTimeOdmeter = "", clearedTimeEngineHour = "";
        JSONObject newOccuredEventObj;
        String lastSavedOdometer =   SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext());
        String lastSavedEngHr =  SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext());
        //String lastSavedTime =  SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext());

        String currentOdometer = SharedPref.getObdOdometer(getApplicationContext());
        String currentEngHr = constants.get2DecimalEngHour(getApplicationContext());

        // save malfunction/diagnostic occur event to server with few inputs
        if(malDiaType.equals(Constants.PowerComplianceDiagnostic) ){
            newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                    DriverId, DeviceId, SharedPref.getVINNumber(getApplicationContext()),
                    SharedPref.getTruckNumber(getApplicationContext()), //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
                    DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                    lastSavedEngHr,
                    lastSavedOdometer,
                    currentOdometer,
                    occurredTime, malDiaType, MalfunctionDefinition,
                    false, clearedTime,
                    currentOdometer,
                    currentEngHr, Constants.getLocationType(getApplicationContext()), ""
            );

        }else if(malDiaType.equals(Constants.PowerComplianceMalfunction)){
            newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                    DriverId, DeviceId, SharedPref.getVINNumber(getApplicationContext()),
                    SharedPref.getTruckNumber(getApplicationContext()), //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
                    DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                    currentEngHr,
                    currentOdometer,
                    currentOdometer,
                    occurredTime, malDiaType, MalfunctionDefinition,
                    false, clearedTime,
                    currentOdometer,
                    currentEngHr, Constants.getLocationType(getApplicationContext()), ""
            );
        }else {

            newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                    DriverId, DeviceId, SharedPref.getVINNumber(getApplicationContext()),
                    SharedPref.getTruckNumber(getApplicationContext()), //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
                    DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                    currentEngHr,
                    currentOdometer,
                    currentOdometer,
                    occurredTime, malDiaType, MalfunctionDefinition,
                    false, clearedTime, clearTimeOdmeter, clearedTimeEngineHour,
                    Constants.getLocationType(getApplicationContext()), ""
            );

        }

        // save Occurred event locally until not posted to server
        JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
        malArray.put(newOccuredEventObj);
        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper( dbHelper, malArray);

        // update malfunction & diagnostic Fragment screen
        updateMalDiaFragment(true);

        // call api
        if(isUploadNow) {
            SaveMalfnDiagnstcLogToServer(malArray, DriverId);
        }

    }

    // update malfunction & diagnostic page
    void updateMalDiaFragment(boolean IsLocalEventUpdate){
        try {
            Intent intent = new Intent(ConstantsKeys.IsEventUpdate);
            intent.putExtra(ConstantsKeys.IsEventUpdate, true);
            intent.putExtra(ConstantsKeys.IsLocalEventUpdate, IsLocalEventUpdate);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    void saveDayStartOdometer(String currentHighPrecisionOdometer){
        String savedDate = SharedPref.getDayStartSavedTime(getApplicationContext());
        String currentLogDate = global.GetCurrentDeviceDate(null, global, getApplicationContext());
        try {
            currentHighPrecisionOdometer = currentHighPrecisionOdometer.split("\\.")[0];

            if(!currentHighPrecisionOdometer.equals("--") && currentHighPrecisionOdometer.length() > 1) {
                String odometerInKm = constants.meterToKmWithObd(currentHighPrecisionOdometer);
                String odometerInMiles = constants.meterToMilesWith2DecPlaces(currentHighPrecisionOdometer);

                if (savedDate.length() > 0) {
                    // int dayDiff = constants.getDayDiff(savedDate, currentLogDate);
                    String savedOdo = SharedPref.getDayStartOdometerKm(getApplicationContext());
                    if (!global.GetCurrentDeviceDate(null, global, getApplicationContext()).equals(savedDate) || savedOdo.equals("0")) {
                        SharedPref.setDayStartOdometer(odometerInKm, odometerInMiles, currentLogDate, getApplicationContext());

                    }
                } else {
                    SharedPref.setDayStartOdometer(odometerInKm, odometerInMiles, currentLogDate, getApplicationContext());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // calculate speed from wifi OBD odometers (in meters) with time difference (in sec)
    private double calculateSpeedFromWifiObdOdometer(String savedTime, String previousHighPrecisionOdometer,
                                                     String currentHighPrecisionOdometer){
        timeInSec = -1;
        double speedInKm = -1;
        double previousOdometer = Double.parseDouble(previousHighPrecisionOdometer);
        double curentOdometer = Double.parseDouble(currentHighPrecisionOdometer);

        previousOdometer = previousOdometer * 1000;
        curentOdometer = curentOdometer * 1000;

        double odometerDistance = curentOdometer - previousOdometer;
        String currentDate = Globally.GetDriverCurrentDateTime(global, getApplicationContext());

        if(savedTime.length() > 10 && previousOdometer > 0) {
            try{
                DateTime savedDateTime = global.getDateTimeObj(savedTime, false);
                DateTime currentDateTime = global.getDateTimeObj(currentDate, false);

                timeInSec = (int) Constants.getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
                //Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();    //Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();
                speedInKm = ( odometerDistance/1000.0f ) / ( timeInSec/3600.0f );

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return speedInKm;

    }



    void callEldRuleForWired( int speed){   //, double calculatedSpeedFromOdo
        // call cycle rule
        try {
            getDriverIDs();
            get18DaysLogArrayLocally();

            int obdType = constants.WIRED_OBD;
            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                obdType = constants.BLE_OBD;
            }

            serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), CoDriverId, EldFragment.driverLogArray, CoDriverName, speed,
                    hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, true, obdType, obdUtil);
        }catch (Exception e){
            e.printStackTrace();
        }

    }










    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.LogInfo(TAG, "---------onStartCommand Service");


        String pingStatus = SharedPref.isPing(getApplicationContext());
        if(pingStatus.equals(ConstantsKeys.SaveOfflineData)){
            if (Constants.isCallMalDiaEvent) {
                Constants.isCallMalDiaEvent = false;
                // update mal/dia status for enable disable according to log
                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());


                if(constants.isMalDiaAllowed(getApplicationContext()) && Globally.isConnected(getApplicationContext())) {
                    GetMalDiaEventsDurationList();
                }


            }else if(Constants.isClearMissingCompEvent){
                Constants.isClearMissingCompEvent = false;
                //  ClearEventUpdate(DriverId, Constants.MissingDataDiagnostic,
                //         "Clear positioning compliance diagnostic after entering manual location", 0);
                IsMissingDiaInProgress = false;

            }else if(Constants.isLogoutEvent){
                Constants.isLogoutEvent = false;
                //     clearPowerEventAtLogout();
                //  checkEngSyncClearEvent();

                /*SharedPref.SaveObdStatus(SharedPref.getObdStatus(getApplicationContext()),
                        global.GetDriverCurrentDateTime(global, getApplicationContext()),
                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
*/

            }else if(Constants.isStorageMalfunctionEvent){ // SAVE data storage compliance malfunction
                Constants.isStorageMalfunctionEvent = false;
                saveStorageMalfunction();
            }else if(Constants.isClearStorageMalEvent){ // Clear data storage compliance malfunction
                Constants.isClearStorageMalEvent = false;
                clearStorageMalEvent();
            }else if(Constants.isClearMissingEvent){
                Constants.isClearMissingEvent = false;

                if(Constants.ClearMissingEventTime.length() > 10) {
                    ClearLocationMissingEvent(DriverId, Constants.ClearMissingEventTime, Constants.ClearMissingEventStatus);
                }
                Constants.ClearMissingEventTime = "";
                Constants.ClearMissingEventStatus = "";
            }else {
                /*driverLogArray = constants.GetDriversSavedArray(getApplicationContext(), MainDriverPref, CoDriverPref);
                if (driverLogArray.length() > 0) {
                    saveActiveDriverData();
                } else {
                    postAllOfflineSavedData();
                } */
            }
        }else if(pingStatus.equals("device")){
             startBleService();
        }else {

            if (TabAct.IsAppRestart ) {
                if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                    startBleService();
                }
                // clearing app cache
                Constants.deleteCache(getApplicationContext());
                isStopService = true;
                stopSelf();

            }else {
                if (EldFragment.IsTruckChange) {
                    if (constants.isMalDiaAllowed(getApplicationContext()) && Globally.isConnected(getApplicationContext())) {
                        GetMalDiaEventsDurationList();
                    }
                }
                if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                    int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
                    if (ObdStatus != Constants.BLE_CONNECTED) {
                        SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                global.GetCurrentUTCTimeFormat(), getApplicationContext());
                    }

                    if (EldFragment.IsTruckChange) {

                        if (!global.isBleEnabled(getApplicationContext()) && EldFragment.IsTruckChangeCallService) {
                            EldFragment.IsTruckChangeCallService = false;
                            Globally.PlayNotificationSound(getApplicationContext());
                            Globally.ShowLocalNotification(getApplicationContext(),
                                    getString(R.string.ble_disabled),
                                    getString(R.string.ble_enabled_desc), 2097);

                            constants.writeBleOnOffStatus(false, global, bleGpsAppLaunchMethod,
                                    dbHelper, getApplicationContext());


                        }

                        startBleService();

                    } else {

                        if (pingStatus.equals("start")) {

                            if (!global.isBleEnabled(getApplicationContext())) {
                                // sendBroadCast("Scanning", "");
                                sendBroadCast(getString(R.string.ht_ble_disabled_error), "");
                                Globally.PlayNotificationSound(getApplicationContext());
                                Globally.ShowLocalNotification(getApplicationContext(),
                                        getString(R.string.ble_disabled),
                                        getString(R.string.ble_enabled_desc), 2097);
                            }else{
                                startBleService();
                            }

                        } else if (pingStatus.equals("stop")) {
                            isStopService = true;
                            stopSelf();
                        }
                    }

                } else if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {

                    BindConnection();

                    int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
                    if (ObdStatus != Constants.WIRED_CONNECTED && ObdStatus != Constants.WIRED_ERROR) {
                        SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                global.GetCurrentUTCTimeFormat(), getApplicationContext());
                    }

                    StartStopServer(constants.WiredOBD);

                    if (EldFragment.IsTruckChange) {
                        disconnectBleObd();

                       constants.TurnOffDeviceBluetooth(getApplicationContext());

                    }

                } else {

                    PingWithWifiObd(wifiConfig.IsAlsNetworkConnected(getApplicationContext()));
                    if (EldFragment.IsTruckChange) {
                        disconnectBleObd();
                        constants.TurnOffDeviceBluetooth(getApplicationContext());
                    }
                }
            }

            EldFragment.IsTruckChange = false;
        }

        UpdateDriverInfo();
        // getLocation(false);

        if (Constants.isDriverSwitchEvent) {
            getDriverIDs();
            boolean isCoDriverEngSyncDia = malfunctionDiagnosticMethod.getCoDriverEngSyncDiaStatus(dbHelper, CoDriverId);
            SharedPref.saveCoDriverEngSyncDiaStatus(isCoDriverEngSyncDia, getApplicationContext());

            // update mal/dia status for enable disable according to log
            malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());
        }

        Constants.isDriverSwitchEvent = false;
        SharedPref.SetPingStatus("", getApplicationContext());

        if(Globally.LATITUDE.length() < 4){
            startLocationService();
        }


        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }



    void startLocationService(){
        try {
            // call location service
            locServiceIntent = new Intent(getApplicationContext(), LocationListenerService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(locServiceIntent);
            }
            startService(locServiceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startBleService(){
        Intent serviceIntent = new Intent(getApplicationContext(), BleDataService.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
        startService(serviceIntent);
    }



    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            // Logger.LogError(TAG, "-----Running timerTask");
           // Logger.LogDebug("SpeedCounter", "SpeedCounter: " +SpeedCounter);

            try {
                if (SharedPref.IsDriverLogin(getApplicationContext())) {

                    // communicate with wired OBD server if not connected
                    if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                        checkWiredObdConnection();
                        if (!isWiredObdRespond) {
                            StartStopServer(constants.WiredOBD);

                            if (HTBleSdk.Companion.getInstance().isConnected()) {   //HTModeSP.INSTANCE.getDeviceMac()
                                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, HTBleSdk.Companion.getInstance().getAddress()));
                            }

                        } else {
                            String obdLastCallDate = SharedPref.getWiredObdServerCallTime(getApplicationContext());
                            if (obdLastCallDate.length() > 10) {
                                int lastCalledDiffInSec = constants.getSecDifference(obdLastCallDate, Globally.GetDriverCurrentDateTime(global, getApplicationContext()));
                                if (lastCalledDiffInSec >= 30) {
                                    StartStopServer(constants.WiredOBD);
                                }
                            }else{
                                StartStopServer(constants.WiredOBD);
                            }
                        }
                    }
                    else if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {

                        // if device not `connected
                        if (!BleDataService.isBleConnected || !HTBleSdk.Companion.getInstance().isConnected()) {
                            if (!SharedPref.GetNewLoginStatus(getApplicationContext())) {
                                if(BleDataService.IsScanClick) {
                                    BleDataService.IsScanClick = false;
                                    sendBroadCast(getString(R.string.ht_connecting), "");
                                }

                                if (SpeedCounter == 10 || SpeedCounter == HalfSpeedCounter) {
                                    startBleService();
                                }
                            }

                        }

                        if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_CONNECTED) {

                            truckRPM = SharedPref.getRPM(getApplicationContext());
                            ignitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());
                            obdEngineHours = "0";
                            currentHighPrecisionOdometer = "0";
                           // obdOdometer = "0";
                            obdTripDistance = "--";

                            sendBroadCast("", "");
                            obdCallBackObservable(-1, SharedPref.getVehicleVin(getApplicationContext()), global.GetDriverCurrentDateTime(global, getApplicationContext()));
                        }

                    }else{
                        // get ALS Wifi ssid availability
                        boolean isAlsNetworkConnected = wifiConfig.IsAlsNetworkConnected(getApplicationContext());
                        // check WIFI connection
                        PingWithWifiObd(isAlsNetworkConnected);

                    }


                    final boolean isGpsEnabled = constants.CheckGpsStatusToCheckMalfunction(getApplicationContext());
                    if (!isGpsEnabled) {
                        Globally.GPS_LATITUDE = "";
                        Globally.GPS_LONGITUDE = "";

                        if (SharedPref.getObdPreference(getApplicationContext()) != Constants.OBD_PREF_BLE) {
                            Globally.LATITUDE = "";
                            Globally.LONGITUDE = "";
                        }

                    }


                    if (SpeedCounter == HalfSpeedCounter || SpeedCounter >= MaxSpeedCounter) {

                        isStopService = false;
                        UpdateDriverInfo();

                        try {

                            if (global.isWifiOrMobileDataEnabled(getApplicationContext())) {


                                if (constants.IsAlsServerResponding) {
                                    if (SpeedCounter == HalfSpeedCounter) {

                                        int offlineDataLength = constants.OfflineData(getApplicationContext(), MainDriverPref,
                                                                    CoDriverPref, global.isSingleDriver(getApplicationContext()));
                                        if (offlineDataLength == 0 ) {   // This check is used to save offline saved data to server first then online status will be changed.
                                            String VIN = SharedPref.getVINNumber(getApplicationContext());
                                            UpdateOfflineDriverLog(DriverId, CoDriverId, DeviceId, VIN,
                                                    String.valueOf(obdVehicleSpeed), isGpsEnabled);
                                        } else {
                                            driverLogArray = constants.GetDriversSavedArray(getApplicationContext(),
                                                    MainDriverPref, CoDriverPref);

                                            if (!Constants.IS_ACTIVE_ELD ) {
                                                if(driverLogArray.length() > 0) {
                                                    saveActiveDriverData();
                                                }else{
                                                    SaveCoDriverData();
                                                }
                                            }else{
                                                // we are handling co driver data upload functionality in bg service only
                                                if(driverLogArray.length() == 0) {
                                                    SaveCoDriverData();
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    updateOfflineApiRejectionCount++;
                                    if (updateOfflineApiRejectionCount > 2) {
                                        updateOfflineApiRejectionCount = 0;
                                        constants.IsAlsServerResponding = true;
                                    } else {
                                        if (updateOfflineApiRejectionCount > 1) {
                                            checkConnectivity.ConnectivityRequest(CheckInternetConnection, ConnectivityInterface);
                                        }
                                    }
                                    SharedPref.setOnlineStatus(false, getApplicationContext());
                                }
                            } else {
                                constants.IsAlsServerResponding = true;
                                SharedPref.setOnlineStatus(false, getApplicationContext());

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    // check positioning malfunction event ..
                    checkPositionMalfunction(currentHighPrecisionOdometer, global.GetDriverCurrentDateTime(global, getApplicationContext()));

                    boolean isObdConnectedWithELD = constants.isObdConnectedWithELD(getApplicationContext());
                    if (isObdConnectedWithELD) {
                        if (SpeedCounter == 10) {
                            String malEventCode = malfunctionDiagnosticMethod.clearMalAfter24Hours(dbHelper, getApplicationContext());
                            if (malEventCode.length() > 0) {
                                String eventOccuredDriverId = "";
                                if (!global.isSingleDriver(getApplicationContext())) {
                                    eventOccuredDriverId = malfunctionDiagnosticMethod.getEventOccurredActualDriverid(dbHelper,
                                                                Constants.PowerComplianceDiagnostic);
                                    if (eventOccuredDriverId.length() == 0) {
                                        eventOccuredDriverId = DriverId;
                                    }
                                    ClearEventUpdate(eventOccuredDriverId, malEventCode,
                                            "Auto clear malfunction event after 24 hours of occurrence.", 0);
                                } else {
                                    ClearEventUpdate(DriverId, malEventCode,
                                            "Auto clear malfunction event after 24 hours of occurrence.", 0);
                                }

                            }
                        }

                    }else{

                        String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());
                        if (savedDate.length() == 0 && Double.parseDouble(currentHighPrecisionOdometer) > 0) {
                            savedDate = global.GetDriverCurrentDateTime(global, getApplicationContext());
                            SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer,
                                                savedDate, getApplicationContext());
                        }

                        callRuleWithStatusWise(currentHighPrecisionOdometer, savedDate,
                                SharedPref.getVINNumber(getApplicationContext()), global.GetDriverCurrentDateTime(global, getApplicationContext()), -1, -1);

                    }

                    checkMissingDiaEvent(isObdConnectedWithELD);


                    if(SpeedCounter == 10 || SpeedCounter == HalfSpeedCounter){

                        if (SpeedCounter == 10) {

                            if (SharedPref.getAgricultureExemption(getApplicationContext())) {
                                String address = SharedPref.getAgricultureRecord("AgricultureAddress",getApplicationContext());
                                if(!address.equals("")) {
                                    String AgricultureLatitude = SharedPref.getAgricultureRecord("AgricultureLatitude", getApplicationContext());
                                    String AgricultureLongitude = SharedPref.getAgricultureRecord("AgricultureLongitude", getApplicationContext());

                                    if(AgricultureLongitude.length() > 4 && Globally.LATITUDE.length() > 0) {
                                        double latitudeDouble = Double.parseDouble(AgricultureLatitude);
                                        double longitudeDouble = Double.parseDouble(AgricultureLongitude);
                                        double distanceMiles = constants.CalculateDistance(Double.parseDouble(Globally.LATITUDE),
                                                Double.parseDouble(Globally.LONGITUDE), latitudeDouble, longitudeDouble, "M", 0);
                                        Logger.LogDebug("", String.valueOf(distanceMiles));
                                        double distanceMilesFormat = Double.parseDouble(Constants.Convert2DecimalPlacesDouble(distanceMiles));
                                        // double distanceKm = Double.parseDouble(Constants.Convert2DecimalPlacesDouble(distanceMilesFormat * 1.60934));
                                        // double distance = Double.parseDouble(Constants.Convert2DecimalPlacesDouble(distanceMilesFormat - Constants.AgricultureDistanceInMiles));

                                        if (distanceMilesFormat > Constants.AgricultureDistanceInMiles) {

                                            // send broadcast to setting screen
                                            SaveAgricultureRecord( Globally.GetCurrentUTCTimeFormat(),
                                                    SharedPref.getTruckNumber(getApplicationContext()), DriverId,
                                                    DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                                                    SharedPref.getAgricultureRecord("AgricultureAddress", getApplicationContext()),
                                                    AgricultureLatitude, AgricultureLongitude,
                                                    SharedPref.getObdEngineHours(getApplicationContext()), SharedPref.getObdOdometer(getApplicationContext()), "0");
                                        }
                                    }
                                }

                            }

                            if (getApplicationContext() != null) {
                                // Sync wired OBD saved log to server (SAVE sync data service)
                                obdUtil.syncObdLogData(getApplicationContext(), DriverId, getDriverName());

                                // Sync app usage log to server (SAVE sync data service)
                                obdUtil.syncAppUsageLog(getApplicationContext(), DriverId);

                                // write logs for location status
                                bleGpsAppLaunchMethod.checkLocationPermissionState(getApplicationContext(), global,
                                        dbHelper, isGpsEnabled);

                                uploadBleGpsLogToServer();

                                // update mal/dia status `
                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                                uploadVehPowerEvents();

                            }

                        }else{
                            if(!SharedPref.GetNewLoginStatus(getApplicationContext()) && !TabAct.IsEcmAlertShown){
                                TabAct.IsEcmAlertShown = true;
                                if(!constants.isObdConnectedWithELD(getApplicationContext())) {
                                    sendEcmBroadcast(true);
                                }
                            }

                            // clear timing compliance malfunction
                            if( SharedPref.IsTimingMalfunction(getApplicationContext())) {
                                boolean IsValidTime = global.isCorrectTime(getApplicationContext(), false );
                                if (constants.isObdConnectedWithELD(getApplicationContext()) && IsValidTime) {
                                   // int minDiff = getTimeDiff(SharedPref.getTimingMalTime(getApplicationContext()));
                                    long minDiff = constants.getTimeDiffInMin(SharedPref.getTimingMalTime(getApplicationContext()),
                                                                                global.GetCurrentUTCDateTime());
                                    if (minDiff >= dayInMinDuration) { // 24 hour
                                        ClearEventUpdate(DriverId, Constants.TimingComplianceMalfunction,
                                                getResources().getString(R.string.timing_mal_clear_desc), 0);
                                    }
                                }
                            }else {
                                // Confirm valid time if time diff is 10 min then first show warning notification to user.
                                // After 5 min min create timing malfunction
                                boolean isTimingMalAllowed = SharedPref.GetOtherMalDiaStatus(ConstantsKeys.TimingCompMal, getApplicationContext());
                                boolean timingMalStatus = SharedPref.IsTimingMalfunction(getApplicationContext());
                                if (isTimingMalAllowed && !timingMalStatus) {
                                    boolean IsValidTime = global.isCorrectTime(getApplicationContext(), false);
                                    String warningTime = SharedPref.getTimingMalWarningTime(getApplicationContext());
                                    if (!IsValidTime) {
                                        if (warningTime.length() == 0) {
                                            // save warning time because we are creating timing mal after 5 min
                                            warningTime = global.GetCurrentUTCTimeFormat();
                                            SharedPref.saveTimingMalfunctionWarningTime(warningTime, getApplicationContext());
                                            Globally.PlayNotificationSound(getApplicationContext());
                                            global.ShowLocalNotification(getApplicationContext(), getString(R.string.timing_mal_alert), getString(R.string.timing_mal_alert_desc), 20910);

                                            Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                                            intent.putExtra(ConstantsKeys.PersonalUse75Km, false);
                                            intent.putExtra(ConstantsKeys.IsInvalidTime, true);
                                            LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);

                                        }

                                       // int minDiff = getTimeDiff(warningTime);
                                        int minDiff = constants.getTimeDiffInMin(warningTime, global.GetCurrentUTCDateTime());
                                        if (minDiff >= 5) {
                                            // save Timing Compliance Malfunction
                                            if(Globally.isConnected(getApplicationContext())) {
                                                String currentUtc = Globally.GetCurrentUTCTimeFormat();
                                                if(minDiff > 10){
                                                    // this check is used when driver was offline and we were not sure their time was correct.
                                                    // So when driver comes online we compare device time with server time. If we found incorrect time
                                                    // then we are creating timing mal event with that time when driver get time set alert.
                                                    currentUtc = String.valueOf(Globally.GetCurrentJodaDateTime().minusMinutes(minDiff-5));
                                                }

                                                saveTimingMalfunctionEvent(getString(R.string.timing_comp_mal_desc),
                                                        currentUtc);
                                            }
                                        }
                                    }else{
                                        // dismiss timing mal alert notification and clear warning time
                                        if(warningTime.length() > 0) {
                                            SharedPref.saveTimingMalfunctionWarningTime("", getApplicationContext());
                                            mNotificationManager.dismissNotification(getApplicationContext(), 20910);
                                        }
                                    }
                                }

                            }

                        }


                        SaveMalfnDiagnstcLogToServer(null, DriverId);

                    }


                    if (SpeedCounter >= MaxSpeedCounter) {
                        SpeedCounter = 10;
                        isMalfncDataAlreadyPosting = false;
                        // Update UTC date time after 60 seconds
                        global.updateCurrentUtcTime(getApplicationContext());

                    } else {
                        SpeedCounter += LocRefreshTime;
                    }


                } else {
                    Logger.LogError("Log", "--stop");
                    StopService();

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };



    void checkMissingDiaEvent(boolean isObdConnectedWithELD){
        try{
            // check login missing event occurrence
            if( !SharedPref.isLoginMissingEventOcc(getApplicationContext()) ){
                String TruckNo = SharedPref.getTruckNumber(getApplicationContext());
                String RPM = SharedPref.getRPM(getApplicationContext());

                if(TruckNo.length() > 0 && (RPM.equals("0") || !isObdConnectedWithELD)) {
                    String loginTimeUtcStr = SharedPref.getLoginTimeUTC(getApplicationContext());

                    if(loginTimeUtcStr.length() > 10) {
                        DateTime loginTimeUtc = Globally.getDateTimeObj(loginTimeUtcStr, false);
                        DateTime currentTimeUtc = Globally.GetCurrentUTCDateTime();

                        long secDiff = Constants.getDateTimeDuration(loginTimeUtc, currentTimeUtc).getStandardSeconds();
                        if (secDiff > 30 && secDiff <= 100) {

                            if(!SharedPref.isObdWasConnectedAfterLogin(getApplicationContext())) {
                                saveMissingDiagnostic(getString(R.string.obd_data_is_missing), "Login Event");
                            }
                            SharedPref.setLoginTimeUTC(loginTimeUtcStr, true, getApplicationContext());

                            sendEcmBroadcast(true);

                        } else if (secDiff >= 120) {
                            SharedPref.setLoginTimeUTC(loginTimeUtcStr, true, getApplicationContext());
                        }
                    }else{
                        SharedPref.setLoginTimeUTC(loginTimeUtcStr, true, getApplicationContext());
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void uploadBleGpsLogToServer(){
        try{
            JSONArray BleStatusLog = bleGpsAppLaunchMethod.GetSelectedEventLog(Constants.LogEventTypeBle, dbHelper);
            JSONArray GpsStatusLog = bleGpsAppLaunchMethod.GetSelectedEventLog(Constants.LogEventTypeGps, dbHelper);

            if(BleStatusLog.length() > 0 && Globally.isConnected(getApplicationContext())){
                JSONObject finalBleObj = bleGpsAppLaunchMethod.GetFinalBleGpsLogInJson(DriverId, DeviceId, BleStatusLog);
                saveEventLogPost.SaveLogJsonObj(finalBleObj, APIs.ADD_DEVICE_BLE_SETTINGS, Constants.SocketTimeout10Sec,
                        true, false, Integer.valueOf(DriverId), SaveBleEventLog);
            }

            if(GpsStatusLog.length() > 0 && Globally.isConnected(getApplicationContext())){
                JSONObject finalGpsObj = bleGpsAppLaunchMethod.GetFinalBleGpsLogInJson(DriverId, DeviceId, GpsStatusLog);
                saveEventLogPost.SaveLogJsonObj(finalGpsObj, APIs.ADD_DEVICE_GPS_SETTINGS, Constants.SocketTimeout10Sec,
                        true, false, Integer.valueOf(DriverId), SaveGpsEventLog);
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void uploadVehPowerEvents(){
        JSONArray eventArray = vehiclePowerEventMethod.getVehPowerEventArray(dbHelper);
        if(eventArray.length() > 0 && Globally.isConnected(getApplicationContext())) {
            saveDriverLogPost.PostDriverLogData(eventArray, APIs.SAVE_ENGINE_ON_OFF_EVENTS, Constants.SocketTimeout20Sec,
                    false, false, 0, SaveVehPwrEventLog);
        }
    }


    void PingWithWifiObd(boolean isAlsNetworkConnected){
        if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIFI) {

            if (isAlsNetworkConnected) {    // check ALS SSID connection with IsOBDPingAllowed permission
                if (SharedPref.isOBDPingAllowed(getApplicationContext())) {
                    tcpClient.sendMessage("123456,can");
                    SharedPref.SaveConnectionInfo(constants.WifiOBD,
                            Globally.GetCurrentDeviceDate(null, global, getApplicationContext()), getApplicationContext());
                }else{
                    SharedPref.setVehilceMovingStatus(false, getApplicationContext());
                }
            } else {
                if (SharedPref.getObdStatus(getApplicationContext()) == Constants.WIFI_CONNECTED) {
                    sendEcmBroadcast(true);
                    Globally.PlayNotificationSound(getApplicationContext());
                    global.ShowLocalNotification(getApplicationContext(),
                            getString(R.string.wifi_obd),
                            getString(R.string.obd_device_disconnected), 2081);

                    SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
                    SharedPref.SaveConnectionInfo(constants.DataMalfunction, "", getApplicationContext());
                    SharedPref.setVehilceMovingStatus(false, getApplicationContext());

                }else{
                    wifiConfig.testConnect(getApplicationContext());
                }
            }
        }
    }

    void saveActiveDriverData(){
        if(!EldFragment.IsSaveOperationInProgress) {

            if (SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
                DriverType = Constants.MAIN_DRIVER_TYPE;
            } else {
                DriverType = Constants.CO_DRIVER_TYPE;
            }

            String SavedLogApi = "";
            if (SharedPref.IsEditedData(getApplicationContext())) {
                SavedLogApi = APIs.SAVE_DRIVER_EDIT_LOG_NEW;
            } else {
                SavedLogApi = APIs.SAVE_DRIVER_STATUS;
            }

            int socketTimeout;
            int logArrayCount = driverLogArray.length();

            if (logArrayCount > 0) {
                EldFragment.IsSaveOperationInProgress = true;

                if (logArrayCount < 3) {
                    socketTimeout = constants.SocketTimeout10Sec;  //10 seconds
                } else if (logArrayCount < 10) {
                    socketTimeout = constants.SocketTimeout20Sec;  //20 seconds
                } else {
                    socketTimeout = constants.SocketTimeout40Sec;  //40 seconds
                }

                saveDriverLogPost.PostDriverLogData(driverLogArray, SavedLogApi, socketTimeout, false, false,
                        DriverType, SaveMainDriverLogData);
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    void StopService(){
        isStopService = true;
        try {
            mTimer.cancel();
            mTimer = null;
        } catch (Exception e) {
        }
        stopForeground(true);
        stopSelf();
    }


    String getDriverName(){
        String driverName = "";
        try {
            if(SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)){
                driverName = DriverConst.GetDriverDetails( DriverConst.DriverName, getApplicationContext());
            }else{
                driverName = DriverConst.GetCoDriverDetails( DriverConst.CoDriverName, getApplicationContext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return driverName;
    }

    private void getDriverIDs(){

        String driverName = "",  mainDriverName = "";
        try {
            mainDriverName = DriverConst.GetDriverDetails( DriverConst.DriverName, getApplicationContext());
            if(Slidingmenufunctions.usernameTV != null) {
                driverName = Slidingmenufunctions.usernameTV.getText().toString();
            }
        }catch (Exception e){
            e.printStackTrace();
            driverName = getDriverName();
        }


        try {
            if (SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.SingleDriver)) {
                DriverId        = SharedPref.getDriverId(getApplicationContext());
                CoDriverId      = "";
                DriverType      = Constants.MAIN_DRIVER_TYPE;
            } else {
                if(driverName.equalsIgnoreCase(mainDriverName)){
                    // pass driver and co driver id in the object (DriverId and CoDriverId).
                    DriverId        =  DriverConst.GetDriverDetails(DriverConst.DriverID, getApplicationContext());
                    CoDriverId      =  DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getApplicationContext());
                    CoDriverName    = DriverConst.GetCoDriverDetails(DriverConst.CoDriverName, getApplicationContext());
                }else{
                    // Exchange driver and co driver id when co driver is logged In.
                    CoDriverId      =  DriverConst.GetDriverDetails(DriverConst.DriverID, getApplicationContext());
                    DriverId        =  DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getApplicationContext());
                    CoDriverName    =   DriverConst.GetDriverDetails(DriverConst.DriverName, getApplicationContext());
                }

                SharedPref.setDriverId(DriverId, getApplicationContext());
                SelectedDriverId = DriverId;
            }

            if (SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
                DriverType      = Constants.MAIN_DRIVER_TYPE;
            }else{
                DriverType      = Constants.CO_DRIVER_TYPE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }


         //  Logger.LogDebug(ConstantsKeys.DriverId, "getDriverIDs-DriverId: " + DriverId);

    }


    void UpdateDriverInfo(){

        getDriverIDs();

        if (SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            String offset = DriverConst.GetDriverSettings(DriverConst.OffsetHours, getApplicationContext());
            if (offset.length() > 0) {
                offSetFromServer = Integer.valueOf(offset);
            }

        } else {     // If Current driver is Co Driver

            String offset = DriverConst.GetCoDriverSettings(DriverConst.CoOffsetHours, getApplicationContext());
            if (offset.length() > 0) {
                offSetFromServer = Integer.valueOf(offset);
            }

        }

        VIN_NUMBER          = SharedPref.getVINNumber(getApplicationContext());
        VehicleId           = SharedPref.getVehicleId(getApplicationContext());
        DeviceId            = SharedPref.GetSavedSystemToken(getApplicationContext());

        DateTime currentSavedDate;
        String currentSavedTime = SharedPref.getSystemSavedDate(getApplicationContext());
        if(currentSavedTime.length() > 10){
            currentSavedDate = global.getDateTimeObj(currentSavedTime, false);
            currentSavedDate = currentSavedDate.plusMinutes(1);
            SharedPref.setCurrentDate(currentSavedDate.toString(), getApplicationContext());
        }else{
            offsetFromUTC = (int) global.GetDriverTimeZoneOffSet(getApplicationContext());
            if(offsetFromUTC == offSetFromServer) {
                currentSavedDate = global.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext()); //GetCurrentUTCTimeFormat
                SharedPref.setCurrentDate(currentSavedDate.toString(), getApplicationContext());
            }
        }

    }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.LogDebug(TAG,"---onBind");
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onDestroy() {

        if(!isStopService) {

            Logger.LogDebug("---onDestroy service_re", ConstantsEnum.StatusAppKilled );
            // save service status log

            Intent intent = new Intent(constants.packageName);
            intent.putExtra("location", "torestore");
            sendBroadcast(intent);

          /*  if(!isAppRestarted && !SharedPref.WasBleEnabled(getApplicationContext())) {
                bleGpsAppLaunchMethod.SaveBleGpsAppLogInTable(Constants.LogEventTypeBle, Constants.UNKNOWN,
                        global.getCurrentDateLocalUtc(), dbHelper);

            }*/


        }else{

            Logger.LogDebug("---onDestroy service ", ConstantsEnum.StatusServiceStopped );

            //  ------------- Wired OBD ----------
            if(isBound){
                StartStopServer("stop");
                this.unbindService(connection);
                isBound = false;
            }

            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {

                LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

                isReceiverInit = false;
            }

            if(locServiceIntent != null){
                stopService(locServiceIntent);
            }


            super.onDestroy();



        }

        SharedPref.setServiceOnDestoryStatus(true, getApplicationContext());
        Logger.LogInfo(TAG, "---------onDestroy Service method");



    }


    private void disconnectBleObd(){
        try {

            HTBleSdk.Companion.getInstance().stopHTBleScan();
            HTBleSdk.Companion.getInstance().unRegisterCallBack();

            if (!HTBleSdk.Companion.getInstance().isAllConnected())
                HTBleSdk.Companion.getInstance().disAllConnect();

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /* ================== Get Driver Details =================== */
    void GetDriverLogs(final String DriverId, final String date) {

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.ProjectId, Globally.PROJECT_ID);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.ELDSearchDate, date);
        params.put(ConstantsKeys.TeamDriverType, SharedPref.getDriverType(getApplicationContext()));

        GetLogRequest.executeRequest(Request.Method.POST, APIs.GET_DRIVER_STATUS, params, GetDriverLog,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }


    /*================== Get Unidentified Records ===================*/
    void GetMalDiaEventsDurationList(){

        if(!MalDiaEventsApiInProcess) {
            if(VIN_NUMBER.length() == 0) {
                VIN_NUMBER = SharedPref.getVINNumber(getApplicationContext());
            }

            MalDiaEventsApiInProcess = true;
            String startEventDate = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext()).minusDays(14).toString();

            params = new HashMap<String, String>();
            params.put(ConstantsKeys.UnitNo, SharedPref.getTruckNumber(getApplicationContext()));
            params.put(ConstantsKeys.VIN, VIN_NUMBER);
            params.put(ConstantsKeys.EventDateTime, startEventDate);

            GetMalfunctionEvents.executeRequest(Request.Method.POST, APIs.GET_MALFUNCTION_LIST_BY_TRUCK, params, GetMalDiaEventDuration,
                    Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
        }
    }


    /* ================== Update Offline Driver Log =================== */
    void UpdateOfflineDriverLog(final String DriverId, final String CoDriverId, final String DeviceId, final String VIN,
                                String obdSpeed, boolean isGpsEnabled ){

        String isCCMTA = String.valueOf(SharedPref.IsCCMTACertified(getApplicationContext()));

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.CoDriverId, CoDriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.VIN, VIN );

        params.put(ConstantsKeys.GPSSpeed, "-1");
        params.put(ConstantsKeys.obdSpeed, obdSpeed);
        params.put(ConstantsKeys.isGpsEnabled, String.valueOf(isGpsEnabled) );
        params.put(ConstantsKeys.IsCheckSuggestedEdit, isCCMTA);

        UpdateUserStatusVolley.executeRequest(Request.Method.POST, APIs.UPDATE_OFF_LINE_DRIVER_LOG_NEW , params,
                UpdateOffLineStatus, Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }




    //*================== Get Driver's 18 Days Recap View Log Data ===================*//*
    void GetRecapView18DaysData(final String DriverId, final String DeviceId , int GetRecapViewFlag){

        if(recapApiAttempts < 2) {
            IsRecapApiCalled = true;

            DateTime currentDateTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext());
            DateTime startDateTime = global.GetStartDate(currentDateTime, 15);
            String StartDate = global.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime));
            String EndDate = global.GetCurrentDeviceDate(null, global, getApplicationContext());  // current Date

            params = new HashMap<String, String>();
            params.put(ConstantsKeys.DriverId, DriverId);
            params.put(ConstantsKeys.DeviceId, DeviceId);
            params.put(ConstantsKeys.ProjectId, global.PROJECT_ID);
            params.put(ConstantsKeys.DrivingStartTime, StartDate);
            params.put(ConstantsKeys.DriverLogDate, EndDate);

            GetRecapView18DaysData.executeRequest(Request.Method.POST, APIs.GET_DRIVER_LOG_18_DAYS_DETAILS, params, GetRecapViewFlag,
                    Constants.SocketTimeout50Sec, ResponseCallBack, ErrorCallBack);
        }
        recapApiAttempts++;
    }



    void DataRequest18Days(String DriverId, int flag){
        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.ProjectId, "1");
        params.put(ConstantsKeys.UTCDateTime,  Globally.GetCurrentUTCDate());

        GetLog18DaysRequest.executeRequest(Request.Method.POST, APIs.GET_DRIVER_LOG_18_DAYS, params, flag,
                Constants.SocketTimeout30Sec, ResponseCallBack, ErrorCallBack);

    }


    void SaveAgricultureRecord(final String EventDateTimeInUtc,final String Truck,final String DriverId,final String CompanyId,
                               final String SourceAddress,final String SourceLatitude,final String SourceLongitude,final String Odometer,final String EngineHours,final String IsEnabled){

        String driverZoneCurrentTime = Globally.GetDriverCurrentDateTime(global, getApplicationContext());
        driverZoneCurrentTime = driverZoneCurrentTime.replaceAll("T", " ");

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.EventDateTime, driverZoneCurrentTime);
        params.put(ConstantsKeys.EventDateTimeInUtc, EventDateTimeInUtc);
        params.put(ConstantsKeys.Truck, Truck);
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.SourceAddress, SourceAddress);
        params.put(ConstantsKeys.SourceLatitude, SourceLatitude);
        params.put(ConstantsKeys.SourceLongitude, SourceLongitude);
        params.put(ConstantsKeys.Odometer, Odometer);
        params.put(ConstantsKeys.EngineHours, EngineHours);
        params.put(ConstantsKeys.IsEnabled, IsEnabled);

        SaveAgricultureRequest.executeRequest(Request.Method.POST, APIs.AddAgricultureException, params, SaveAgricultureAddress,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }


    /*================== Upload Driver Updated Log Record Data ===================*/
    void SAVE_DRIVER_RECORD_LOG(final JSONArray geoData, final boolean isLoad, final boolean IsRecap, int socketTimeout){
        saveDriverLogPost.PostDriverLogData(geoData, APIs.UPDATE_DRIVER_LOG_RECORD, socketTimeout, isLoad, IsRecap, 1, SaveDriverLog);

    }




    void UploadSavedShipmentData(){

        try{
            if(global.isConnected(getApplicationContext())) {
                shipmentArray = shipmentHelper.getSavedShipmentArray(Integer.valueOf(global.PROJECT_ID), dbHelper);
                if(shipmentArray.length() > 0 ){
                    postRequest.PostListingData(shipmentArray, APIs.SAVE_SHIPPING_DOC_NUMBER, SaveShippingOffline);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void UploadSavedOdometerData(){
        try{
            odometerArray = new JSONArray();

            if(global.isConnected(getApplicationContext())) {

                odometerArray = odometerHelper.getSavedOdometerArray(Integer.valueOf(DriverId), dbHelper);
                if (odometerArray.length() > 0) {
                    SharedPref.SetOdoSavingStatus(true, getApplicationContext());
                    postRequest.PostListingData(odometerArray, APIs.SAVE_ODOMETER_OFFLINE, SaveOdometerOffline);
                }


                String id = "";
                if(SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)) {
                    if (SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
                        // Current active driver is Main Driver. So we need co driver details and we are getting co driver's details.
                        id = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getApplicationContext());
                    } else {
                        // Current active driver is Co Driver. So we need main driver details and we are getting main driver's details.
                        id = DriverConst.GetDriverDetails(DriverConst.DriverID, getApplicationContext());
                    }

                    if(id.length() > 0) {
                        SecondDriver = Integer.valueOf(id);
                        try {
                            JSONArray odometerArray = odometerHelper.getSavedOdometerArray(SecondDriver, dbHelper);
                            if (odometerArray.length() > 0) {
                                postRequest.PostListingData(odometerArray, APIs.SAVE_ODOMETER_OFFLINE, Save2ndDriverOdoData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }



    }




    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Logger.LogError("TTS", "This Language is not supported");
            }

        } else {
            Logger.LogError("TTS", "Initilization Failed!");
        }

    }


    // Speak Out Msg
    void SpeakOutMsg(){
        tts.speak(ViolationReason, TextToSpeech.QUEUE_FLUSH, null);
    }

    // Speak Out Msg
    void SpeakText(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }




    CheckConnectivity.ConnectivityInterface ConnectivityInterface = new CheckConnectivity.ConnectivityInterface() {
        @Override
        public void IsConnected(boolean result, int flag) {
            Logger.LogDebug("networkUtil", "result: " +result );

            if (result) {
                //if(flag == CheckInternetConnection) {
                constants.IsAlsServerResponding = true;

            }
        }
    };


    //  ------------- WiFI OBD data response handler ----------
    TcpClient.OnMessageReceived obdResponseHandler = new TcpClient.OnMessageReceived() {
        @Override
        public void messageReceived(String message) {
            Logger.LogDebug("response", "OBD Response: " +message);
            obdVehicleSpeed = -1;

            // temp data
            //  message = "*TS01,861641040970625,002930140821,CAN:0B00F004517F7FB91D00F47F0B00FEC1B44D4103B44D41030B00FEF1FF9A3AFCFFFF00FF0B00FEF6FF095D3CFFFFFFFF0B00FEEE8160EB2EFFFF53FF0B00FEEF9DFFFF35FFFFFFFA0B00FEFCFF5EFFFFFFFFFFFF0B00FEF202019513D205FFFF0B00FEE007552100075521000B00FEBF1E3B7C7D8082FFFF0B00FD0900A9D904D20C85050B00FEE9FFFFFFFFBAD00200#";
         /*   if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_CONNECTED) {
                sendEcmBroadcast(false);
                global.ShowLocalNotification(getApplicationContext(),
                        getString(R.string.wifi_obd),
                        getString(R.string.obd_device_connected), 2081);
                SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
            }*/


            try {
                String rawResponse = message;
                String correctData = "";

                if (!message.equals(noObd) && message.length() > 10) {

                    if (message.contains("CAN")) {

                        if (message.contains("CAN:UNCONNECTED")) {

                            ignitionStatus = "false";
                            saveDummyData(rawResponse, constants.WifiOBD);

                            Globally.IS_OBD_IGNITION = false;
                            // SharedPref.SetTruckIgnitionStatusForContinue("OFF", constants.WifiOBD, "", getApplicationContext());
                            SharedPref.setVss(0, getApplicationContext());
                            SharedPref.setVehilceMovingStatus(false, getApplicationContext());


                        } else {

                            double WheelBasedVehicleSpeed = -1;
                            try {
                                String preFix = "*TS01,861107039609723,050743230120,";
                                String postFix = "#";

                                if (message.length() > 5) {
                                    String first = message.substring(0, 5);
                                    String last = message.substring(message.length() - 1, message.length());
                                    if (!first.equals("*TS01") && !last.equals("#")) {
                                        message = preFix + message + postFix;
                                    }
                                }

                                if (message.length() > 500) {
                                    correctData = constants.correctOBDWrongData(message);
                                    message = correctData;
                                }

                                data = decoder.DecodeTextAndSave(message, new OBDDeviceData());
                                JSONObject canObj = new JSONObject(data.toString());

                                SharedPref.setObdStatusAfterLogin(true, getApplicationContext());
                                GPSSpeed = Integer.valueOf(wifiConfig.checkJsonParameter(canObj, "GPSSpeed", "0"));
                                WheelBasedVehicleSpeed = Double.parseDouble(wifiConfig.checkJsonParameter(canObj, "WheelBasedVehicleSpeed", "0"));

                                truckRPM = wifiConfig.checkJsonParameter(canObj, "RPMEngineSpeed", "0");
                                ignitionStatus = wifiConfig.checkJsonParameter(canObj, "EngineRunning", "false");
                                obdTripDistance = wifiConfig.checkJsonParameter(canObj, "TripDistanceInKM", "0");
                                Globally.LATITUDE = wifiConfig.checkJsonParameter(canObj, "GPSLatitude", "");
                                Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(wifiConfig.checkJsonParameter(canObj, "GPSLongitude", ""));
                                HighResolutionDistance = wifiConfig.checkJsonParameter(canObj, "HighResolutionTotalVehicleDistanceInKM", "-1");
                               // obdOdometer = HighResolutionDistance;
                                currentHighPrecisionOdometer = HighResolutionDistance;
                                obdEngineHours = wifiConfig.checkJsonParameter(canObj, "EngineHours", "0");

                                String vin = wifiConfig.checkJsonParameter(canObj, ConstantsKeys.VIN, "");
                                if(vin.length() <= 5){
                                    vin = "";
                                }
                                SharedPref.setVehicleVin(vin, getApplicationContext());
                                // SharedPref.SetObdEngineHours(obdEngineHours, getApplicationContext());
                                //  SharedPref.SetObdOdometer( Constants.meterToKmWithObd(HighResolutionDistance), getApplicationContext());
                                //  SharedPref.SetObdOdometerInMiles(Constants.meterToMilesWith2DecPlaces(currentHighPrecisionOdometer), getApplicationContext());

                                checkEngHrOdo();

                                if(Globally.LATITUDE.length() < 5){
                                    SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());
                                }else{
                                    SharedPref.SetLocReceivedFromObdStatus(true, getApplicationContext());
                                }


                                if (ignitionStatus.equals("true")) {    // truckRpmInt > 0

                                    if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_CONNECTED) {
                                        sendEcmBroadcast(false);
                                        global.ShowLocalNotification(getApplicationContext(),
                                                getString(R.string.wifi_obd),
                                                getString(R.string.obd_device_connected), 2081);
                                    }

                                    // saving location with time info to calculate location malfunction event
                                    constants.saveEcmLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE, HighResolutionDistance, getApplicationContext());

                                    if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_CONNECTED) {
                                        sendBroadcastUpdateObd(false);
                                    }

                                    Globally.IS_OBD_IGNITION = true;
                                    continueStatusPromtForPcYm("ON", constants.WifiOBD, global.GetDriverCurrentDateTime(global, getApplicationContext()), Constants.WIFI_CONNECTED);
                                    SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());

                                    obdVehicleSpeed = (int) WheelBasedVehicleSpeed;

                                    calculateWifiObdData(HighResolutionDistance, rawResponse, true);

                                    sendBroadCast(parseObdDataInHtml(vin, (int)WheelBasedVehicleSpeed), message);



                                    /* ======================== Malfunction & Diagnostic Events ========================= */
                                    if(obdEngineHours.length() > 0 && !obdEngineHours.equals("0") && !obdEngineHours.equals("0.00")) {
                                        ClearMissingDiagnostics();
                                        checkPowerMalDiaEvent();   // checking Power Data Compliance Mal/Dia event
                                        checkEngSyncClearEvent();  // checking EngineSyncDataCompliance Mal/Dia clear event if already occurred


                                    }


                                } else {
                                    Globally.IS_OBD_IGNITION = false;
                                    SharedPref.SetTruckIgnitionStatusForContinue("OFF", constants.WifiOBD, "", getApplicationContext());
                                    SharedPref.setVss(0, getApplicationContext());
                                    SharedPref.setVehilceMovingStatus(false, getApplicationContext());

                                    saveDummyData(rawResponse, constants.WifiOBD);

                                }


                            } catch (Exception e) {
                                e.printStackTrace();

                                HighResolutionDistance = "0";
                                ignitionStatus = "--";
                                obdTripDistance = "--";
                                //  tcpClient.sendMessage("123456,gps");

                            }

                        }
                        RestartObdFlag = false;
                    } else {
                        if (RestartObdFlag || message.contains("RST")) {
                            //  Logger.LogDebug("OBD", ObdRestarted);
                            saveDummyData(ObdRestarted, constants.WifiOBD);
                        } else {
                            ignitionStatus = "false";
                            saveDummyData(rawResponse, constants.WifiOBD);
                        }
                    }

                } else {

                    if (RestartObdFlag || message.contains("RST")) {
                        //Logger.LogDebug("OBD", ObdRestarted);
                        saveDummyData(ObdRestarted, constants.WifiOBD);
                    } else {

                        if (!message.equals(noObd)) {
                            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIFI) {
                                if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_DISCONNECTED) {
                                    sendBroadcastUpdateObd(false);
                                    //   sendEcmBroadcast(true);
                                    //   Globally.PlayNotificationSound(getApplicationContext());
                                    SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
                                }
                            }
                        }

                        ignitionStatus = "false";
                        saveDummyData(rawResponse, constants.WifiOBD);

                        String lastRestartTime = SharedPref.getOBDRestartTime(getApplicationContext());
                        if (lastRestartTime.length() > 10) {
                            int minDiff = constants.getMinDifference(lastRestartTime, global.getCurrentDate());
                            if (minDiff > 60) {
                                restartObd();
                            }
                        } else {
                            restartObd();
                        }
                    }
                }
            }catch (Exception e){
                ignitionStatus = "false";
                e.printStackTrace();
            }

        }
    };


    void restartObd(){
        RestartObdFlag = true;
        SharedPref.SetOBDRestartTime( global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
        tcpClient.sendMessage("123456,rst");
    }

    void saveDummyData(String rawResponse, String type){
        HighResolutionDistance = "0";  obdTripDistance = "--";    RestartObdFlag = false;
        String savedRawData = SharedPref.GetObdRawData(getApplicationContext());

        if(!savedRawData.equals(rawResponse.trim())) {
            if(rawResponse.contains("CAN:UNCONNECTED") && savedRawData.contains("CAN:UNCONNECTED")){
                // ignore same data to save again
            }else {

                // Sync wired OBD saved log to server (SAVE sync data service)
                if(rawResponse.contains("CAN:UNCONNECTED") || rawResponse.equals(noObd)){
                    obdUtil.syncObdSingleLog(getApplicationContext(), DriverId, getDriverName(), 10);
                }
            }
        }

        if(type.equals(constants.ApiData) || type.equals(constants.OfflineData)){
            HighResolutionDistance = SharedPref.GetWifiObdOdometer(getApplicationContext());

            // Sync wired OBD saved log to server (SAVE sync data service)
            obdUtil.syncObdSingleLog(getApplicationContext(), DriverId, getDriverName(), 10);

        }

        SharedPref.SetWifiObdOdometer(HighResolutionDistance, Globally.GetDriverCurrentDateTime(global, getApplicationContext()), rawResponse.trim(), getApplicationContext());

    }


    private void calculateWifiObdData(String HighPrecisionOdometer, String rawResponse,boolean isSave){

        int DrivingSpeedLimit   = DriverConst.getDriverConfiguredTime(DriverConst.DrivingSpeed, getApplicationContext());
        double speedCalculated = -1;
        String savedTime = SharedPref.GetWifiObdSavedTime(getApplicationContext());
        String currentLogDate = global.GetDriverCurrentDateTime(global, getApplicationContext());

        if(rawResponse.contains("CAN")) {
            if(SharedPref.isOdoCalculationAllowed(getApplicationContext()) || obdVehicleSpeed > 200) {
                speedCalculated = calculateSpeedFromWifiObdOdometer(
                        savedTime,
                        SharedPref.GetWifiObdOdometer(getApplicationContext()),
                        HighPrecisionOdometer);
            }else{
                timeInSec = 6;
                speedCalculated = obdVehicleSpeed;
            }
        }

        if(obdVehicleSpeed > 1){
            SharedPref.setVehilceMovingStatus(true, getApplicationContext());
        }else{
            SharedPref.setVehilceMovingStatus(false, getApplicationContext());
        }


        SharedPref.SetWifiObdOdometer(HighResolutionDistance, currentLogDate, rawResponse, getApplicationContext());

        if(isSave) {
            // save current odometer for HOS calculation
            double dayStartOdometer = Double.parseDouble(HighPrecisionOdometer) * 1000;
            saveDayStartOdometer(""+dayStartOdometer);
        }

        String jobType = SharedPref.getDriverStatusId(getApplicationContext());
        String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());

        if(savedDate.length() == 0) {
            // save current HighPrecisionOdometer locally
            savedDate = currentLogDate;
            SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());
        }



        double truckRpm = 0;

        try{
            truckRpm = Double.parseDouble(truckRPM);
        }catch (Exception e){}

        if(truckRpm > 500 && timeInSec >= 5) {

            if (jobType.equals(global.DRIVING)) {

                if (obdVehicleSpeed >= DrivingSpeedLimit || speedCalculated >= DrivingSpeedLimit ) {

                    if (obdVehicleSpeed > 200) {
                        obdVehicleSpeed = (int) speedCalculated;
                    }
                    ServiceCycle.ContinueSpeedCounter = 0;
                }

                if (constants.minDiff(savedDate, global, false, getApplicationContext()) > 0) {
                    SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());

                    get18DaysLogArrayLocally();
                    serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), CoDriverId, EldFragment.driverLogArray, CoDriverName,
                            obdVehicleSpeed,  hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError,
                            true, constants.WIFI_OBD,  obdUtil);
                }

            } else if (jobType.equals(global.ON_DUTY)) {

                if (obdVehicleSpeed >= DrivingSpeedLimit || speedCalculated >= DrivingSpeedLimit ) {
                    if(obdVehicleSpeed > 200){
                        obdVehicleSpeed      = (int)speedCalculated;
                    }

                    get18DaysLogArrayLocally();
                    SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer,currentLogDate, getApplicationContext());
                    serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), CoDriverId, EldFragment.driverLogArray, CoDriverName,
                            obdVehicleSpeed, hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError,
                            true, constants.WIFI_OBD, obdUtil);

                } else {

                    ServiceCycle.ContinueSpeedCounter = 0;
                    if (constants.minDiff(savedDate, global, false, getApplicationContext()) > 0 ) {    //|| (isYmPcAlertShown && isYardMove())
                        get18DaysLogArrayLocally();
                        SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());
                        serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), CoDriverId, EldFragment.driverLogArray, CoDriverName,
                        obdVehicleSpeed, hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError,
                                true, constants.WIFI_OBD, obdUtil);
                    }
                }

            } else {

                SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());

                if (speedCalculated >= DrivingSpeedLimit && obdVehicleSpeed > 200 ) {
                    obdVehicleSpeed = (int) speedCalculated;
                }


                if (obdVehicleSpeed <= DrivingSpeedLimit || speedCalculated <= DrivingSpeedLimit ) {
                    ServiceCycle.ContinueSpeedCounter = 0;
                } else {
                    get18DaysLogArrayLocally();
                    serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), CoDriverId, EldFragment.driverLogArray, CoDriverName,
                            obdVehicleSpeed, hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError,
                            true, constants.WIFI_OBD, obdUtil);
                }

            }
        }else{
            if(timeInSec >= 5) {
                ServiceCycle.ContinueSpeedCounter = 0;
            }
        }

        SharedPref.setVss(obdVehicleSpeed, getApplicationContext());
        SharedPref.setRPM(truckRPM, getApplicationContext());

    }



    private void get18DaysApiData(){
        boolean isTeamDriver = SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver);
        try {
            if (SharedPref.GetNewLoginStatus(getApplicationContext())) {

                // some times 18 days log were not updated when driver logged in after few days. So need to call updateOfflineStatus api first and then call 18 days log of 500 ms delay.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
                        if(driverLogArray.length() == 0 && !Is18DaysLogApiCalled) {
                            DataRequest18Days(DriverId, GetDriverLog18Days);
                            if (isTeamDriver) {
                                DataRequest18Days(CoDriverId, GetCoDriverLog18Days);
                            }
                            Is18DaysLogApiCalled = true;
                        }

                        JSONArray ctPatInsp18DaysArray = ctPatInspectionMethod.getCtPat18DaysInspectionArray(Integer.valueOf(DriverId), dbHelper);
                        if (ctPatInsp18DaysArray.length() == 0 && !Is18DaysLogApiCalled) {
                            String SelectedDate = global.GetCurrentDeviceDate(null, global, getApplicationContext());
                            if(!IsCtPatApiCalled) {
                                IsCtPatApiCalled = true;
                                if (isTeamDriver) {
                                    GetCtPatInspection18Days(DriverId, DeviceId, SelectedDate, GetCtPat18DaysMainDriverLog);
                                    GetCtPatInspection18Days(CoDriverId, DeviceId, SelectedDate, GetCtPat18DaysCoDriverLog);
                                } else {
                                    GetCtPatInspection18Days(DriverId, DeviceId, SelectedDate, GetCtPat18DaysMainDriverLog);
                                }
                            }
                        }

                    }
                }, 500);

            }


            JSONArray recapArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DriverId), dbHelper);
            JSONObject hasCurrentDatObj = recapViewMethod.GetSelectedRecapData(recapArray, Globally.GetCurrentDeviceDate(null, global, getApplicationContext())) ;
            if(recapArray == null || recapArray.length() == 0 || hasCurrentDatObj == null) {
                if(!IsRecapApiCalled) {
                    if(isTeamDriver){
                        GetRecapView18DaysData(DriverId, DeviceId, GetRecapViewFlagMain);
                        GetRecapView18DaysData(CoDriverId, DeviceId, GetRecapViewFlagCo);
                    }else{
                        GetRecapView18DaysData(DriverId, DeviceId, GetRecapViewFlagMain);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void SaveDriverRecordLogUpdates(){
        try {
            if(global.isConnected(getApplicationContext())) {
                if (DriverId.length() > 0) {
                    JSONArray recordsLogArray = logRecordMethod.getSavedLogRecordArray(Integer.valueOf(DriverId), dbHelper);
                    if (recordsLogArray.length() > 0) {
                        SAVE_DRIVER_RECORD_LOG(recordsLogArray, false, false, Constants.SocketTimeout30Sec);
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void SaveCertifyLog(){
        try{
            if(global.isConnected(getApplicationContext())) {
                if (DriverId.length() > 0) {
                    JSONArray certifyLogArray = certifyLogMethod.getSavedCertifyLogArray(Integer.valueOf(DriverId), dbHelper);
                    if (certifyLogArray.length() > 0) {
                        saveDriverLogPost.PostDriverLogData(certifyLogArray, APIs.CERTIFY_LOG_OFFLINE, Constants.SocketTimeout30Sec,
                                false, false, 1, SaveCertifyLog);

                    }
                }

                if (SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)) {
                    if (CoDriverId.length() > 0) {
                        JSONArray certifyLogArray = certifyLogMethod.getSavedCertifyLogArray(Integer.valueOf(CoDriverId), dbHelper);
                        if (certifyLogArray.length() > 0) {
                            saveDriverLogPost.PostDriverLogData(certifyLogArray, APIs.CERTIFY_LOG_OFFLINE, Constants.SocketTimeout30Sec,
                                    false, false, 1, SaveCertifyLogCo);

                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void saveNotifications(){
        JSONArray notificationArray = notificationMethod.getSaveToNotificationArray(Integer.valueOf(DriverId), dbHelper);

        if(notificationArray.length() > 0){
            saveNotificationReq.PostDriverLogData(notificationArray, APIs.SAVE_NOTIFICATION,
                    constants.SocketTimeout20Sec, false, false, DriverType, MainDriverNotification);
        }

        if(SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)){

            JSONArray saveToCoDriverSaveToArray = notificationMethod.getSaveToNotificationArray(Integer.valueOf(CoDriverId), dbHelper);
            if(saveToCoDriverSaveToArray.length() > 0){
                saveNotificationReq.PostDriverLogData(saveToCoDriverSaveToArray, APIs.SAVE_NOTIFICATION, constants.SocketTimeout20Sec,
                        false, false, DriverType, CoDriverNotification);
            }
        }

    }


    void SaveMalfnDiagnstcLogToServer(JSONArray malArray1, String DriverId){
        try{
            if(DriverId.length() > 0) {
                if(malArray1 == null) {
                    malArray1 = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
                }
                String api = APIs.MALFUNCTION_DIAGNOSTIC_EVENT;

                if(malfunctionDiagnosticMethod.IsOccurEventAlreadyUploaded(malArray1)){
                    api = APIs.CLEAR_MALFNCN_DIAGSTC_EVENT_BY_DATE;
                }


                if (global.isConnected(getApplicationContext()) && malArray1.length() > 0 && !isMalfncDataAlreadyPosting) {
                    isMalfncDataAlreadyPosting = true;
                    saveDriverLogPost.PostDriverLogData(malArray1, api, Constants.SocketTimeout20Sec,
                            false, false, 1, SaveMalDiagnstcEvent);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    //*================== Get Odometer 18 Days data ===================*//*
/*    void SaveDriverDeviceUsageLog(final String savedDate){
        networkUsage();

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.AlsSendingData, AlsSendingData );
        params.put(ConstantsKeys.AlsReceivedData, AlsReceivedData   );
        params.put(ConstantsKeys.MobileUsage, MobileUsage );
        params.put(ConstantsKeys.TitalUsage, TotalUsage );
        params.put(ConstantsKeys.EntryDate, savedDate );

        saveDriverDeviceUsageLog.executeRequest(Request.Method.POST, APIs.SAVE_DRIVER_DEVICE_USAGE_LOG , params, SaveDriverDeviceUsageLog,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);


    }*/



    //*================== Get Odometer 18 Days data ===================*//*
    void GetOdometer18Days(final String DriverId, final String DeviceId, final String CompanyId , final String CreatedDate){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.CompanyId, CompanyId  );
        params.put(ConstantsKeys.CreatedDate, CreatedDate);

        GetOdometerRequest.executeRequest(Request.Method.POST, APIs.GET_ODOMETER_OFFLINE , params, GetOdometers18Days,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);


    }


    //*================== Get Driver Status Permissions ===================*//*
    void GetCtPatInspection18Days(final String DriverId, final String DeviceId, final String SearchedDate, final int GetInspectionFlag ){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.SearchedDate, SearchedDate );

        ctPatInsp18DaysRequest.executeRequest(Request.Method.POST, APIs.GET_OFFLINE_17_INSPECTION_LIST, params, GetInspectionFlag,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }




    private void continueStatusPromtForPcYm(String obdCurrentIgnition, String type, String time, int obdStatus){

        try {

            if( obdStatus != Constants.NO_CONNECTION && !SharedPref.GetNewLoginStatus(getApplicationContext())) {
                try {
                    get18DaysLogArrayLocally();

                    if (EldFragment.driverLogArray.length() > 0) {  //UILApplication.isActivityVisible() &&
                        JSONObject lastJsonItem =  hMethods.GetLastJsonFromArray(EldFragment.driverLogArray);
                        int currentJobStatus = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
                        boolean isYard = lastJsonItem.getBoolean(ConstantsKeys.YardMove);
                        boolean isPersonal = lastJsonItem.getBoolean(ConstantsKeys.Personal);

                        if ((currentJobStatus == constants.OFF_DUTY && isPersonal) || (currentJobStatus == constants.ON_DUTY && isYard)) {
                            String lastIgnitionStatus = SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getApplicationContext());
                            if (lastIgnitionStatus.equals("OFF") && obdCurrentIgnition.equals("ON")) {

                               // if(Constants.IS_ACTIVE_ELD)
                                SharedPref.SetTruckStartLoginStatus(true, getApplicationContext());

                                global.ShowLocalNotification(getApplicationContext(),
                                        getString(R.string.ALS_ELD),
                                        getString(R.string.engine_restarted), 2081);

                                Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                                intent.putExtra(ConstantsKeys.SuggestedEdit, false);
                                intent.putExtra(ConstantsKeys.IsCycleRequest, false);
                                intent.putExtra(ConstantsKeys.IsELDNotification, false);
                                intent.putExtra(ConstantsKeys.DriverELDNotificationList, false);
                                intent.putExtra(ConstantsKeys.IsEngineRestarted, true);
                                intent.putExtra(ConstantsKeys.IsYard, isYard);
                                intent.putExtra(ConstantsKeys.IsPersonal, isPersonal);

                                LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
                            }


                            SharedPref.SetTruckIgnitionStatusForContinue(obdCurrentIgnition, type, time, getApplicationContext());
                        }else{
                            SharedPref.SetTruckIgnitionStatusForContinue(obdCurrentIgnition, type, time, getApplicationContext());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    void sendBroadcastUpdateObd(boolean ChangedToOthers){
        try {
            Intent intent = new Intent(ConstantsKeys.IsIgnitionOn);
            intent.putExtra(ConstantsKeys.IsIgnitionOn, false);
            intent.putExtra(ConstantsKeys.IsAutoStatusSaved, false);
            intent.putExtra(ConstantsKeys.ChangedToOthers, ChangedToOthers);
            if(ChangedToOthers){
                intent.putExtra(ConstantsKeys.IsDismissDialog, true);
            }
            intent.putExtra(ConstantsKeys.IsOBDStatusUpdate, true);
            intent.putExtra(ConstantsKeys.IsPcYmAlertChangeStatus, false);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void checkDeferralData(){
        try{
            if (SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)) {

                // -----------------------------UnPosted inspection -----------------------------------
                defMainDriverArray = deferralMethod.getSavedDeferralArray(Integer.valueOf(DriverId), dbHelper);
                defCoDriverArray = deferralMethod.getSavedDeferralArray(Integer.valueOf(CoDriverId), dbHelper);

                if (defMainDriverArray.length() > 0 && !isDeferralAlreadyPosting) {
                    isDeferralAlreadyPosting = true;
                    saveDriverLogPost.PostDriverLogData(defMainDriverArray, APIs.SAVE_DEFFERAL_EVENT, Constants.SocketTimeout10Sec, true, false,
                            Integer.valueOf(DriverId), SaveDeferralMain);
                }


                if (defCoDriverArray.length() > 0 && !isDeferralAlreadyPostingCo) {
                    isDeferralAlreadyPostingCo = true;
                    saveDriverLogPost.PostDriverLogData(defCoDriverArray, APIs.SAVE_DEFFERAL_EVENT, Constants.SocketTimeout10Sec, true, false,
                            Integer.valueOf(CoDriverId), SaveDeferralCo);
                }

            }else{
                defMainDriverArray = deferralMethod.getSavedDeferralArray(Integer.valueOf(DriverId), dbHelper);
                if(defMainDriverArray.length() > 0 && !isDeferralAlreadyPosting){
                    isDeferralAlreadyPosting = true;
                    saveDriverLogPost.PostDriverLogData(defMainDriverArray, APIs.SAVE_DEFFERAL_EVENT, Constants.SocketTimeout10Sec, true, false,
                            Integer.valueOf(DriverId), SaveDeferralMain);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void SyncData(){

        try {
            savedSyncedArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DriverId), dbHelper);
            // Logger.LogDebug("savedSyncedArray", "savedSyncedArray: " + savedSyncedArray);
            if(savedSyncedArray.length() > 0) {
                syncingFile = global.SaveFileInSDCard("Sync_", savedSyncedArray.toString(), false, getApplicationContext());
            }

            ViolationFile = global.GetSavedFile(getApplicationContext(),ConstantsKeys.ViolationTest, "txt");

            if(SharedPref.IsAutoSync(getApplicationContext())){
                if(ViolationFile.exists() && syncingFile.exists() ) {
                    // Sync driver log API data to server with SAVE_LOG_TEXT_FILE (SAVE sync data service)
                    SyncDataUpload syncDataUpload = new SyncDataUpload(getApplicationContext(), DriverId, syncingFile, ViolationFile, new File(""), true, asyncResponse);
                    syncDataUpload.execute();
                }
            }else{
                // when AutoSync disabled only check violation file and post it on server
                if(ViolationFile.exists() ) {
                    syncingFile = null;
                    SyncDataUpload syncDataUpload = new SyncDataUpload(getApplicationContext(), DriverId, syncingFile, ViolationFile, new File(""), true, asyncResponse);
                    syncDataUpload.execute();
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void checkLogMissingIdStatus(){

        driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
        yesterdayDate = Globally.getDateTimeObj(global.ConvertDateFormat(Globally.GetCurrentDeviceDate(null, global, getApplicationContext())), false).minusDays(1);
        twoDaysLogArray = hMethods.twoDaysLogArray( driverLogArray, yesterdayDate);

        IsLogIdMissing = hMethods.IsLogIdMissing(twoDaysLogArray, getApplicationContext());
        if(IsLogIdMissing != -1){
            if(IsLogIdMissing == 0){
                GetDriverLogs(DriverId, Globally.GetCurrentDeviceDate(null, global, getApplicationContext()));
            }else{
                GetRecapView18DaysData(DriverId, DeviceId, GetRecapViewFlagMain);
            }
        }

    }


    void updateClearEvent(boolean isRefreshFromServer){
        String notificationDesc = "";
        int notificationId = -1;

        if(ClearEventType.equals(Constants.MissingDataDiagnostic)){
            SharedPref.saveMissingDiaStatus(false, getApplicationContext());

            notificationDesc = getString(R.string.missing_data_dia_clear_desc);
            notificationId = 2091;
        }else if(ClearEventType.equals(Constants.PowerComplianceDiagnostic)){
            SharedPref.savePowerMalfunctionOccurStatus(
                    SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                    false,  global.GetCurrentUTCTimeFormat(), getApplicationContext());

            notificationDesc =  getString(R.string.power_dia_clear_desc);
            notificationId = 2093;

        }else if(ClearEventType.equals(Constants.EngineSyncDiagnosticEvent)){
            SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
            notificationId = 2090;

            if(SharedPref.isCoDriverEngSyncDia(getApplicationContext())){
                notificationDesc =  getString(R.string.codr_eng_sync_dia_clear_desc);
                malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, CoDriverId, ClearEventType, true, true, getApplicationContext());
                SharedPref.saveCoDriverEngSyncDiaStatus(false, getApplicationContext());
            }else{
                notificationDesc =  getString(R.string.eng_sync_dia_clear_desc);
            }
        }else if(ClearEventType.equals(Constants.PowerComplianceMalfunction)){
            SharedPref.savePowerMalfunctionOccurStatus(false,
                    SharedPref.isPowerDiagnosticOccurred(getApplicationContext()), "", getApplicationContext());

            notificationDesc =  getString(R.string.power_mal_clear_desc);
            notificationId = 2093;

        }else if(ClearEventType.equals(Constants.EngineSyncMalfunctionEvent)){
            SharedPref.saveEngSyncMalfunctionStatus(false, getApplicationContext());

            notificationDesc =  getString(R.string.eng_sync_mal_clear_desc);
            notificationId = 2090;

        }else if(ClearEventType.equals(Constants.PositionComplianceMalfunction)){
            SharedPref.saveLocMalfunctionOccurStatus(false, "", "",getApplicationContext());

            notificationDesc =  getString(R.string.pos_mal_clear_desc);
            notificationId = 2091;

        }else if(ClearEventType.equals(Constants.PositionComplianceMalfunction)){
            notificationDesc =  getString(R.string.data_rec_mal_cleared_def);
            notificationId = 2098;
        }else if(ClearEventType.equals(Constants.TimingComplianceMalfunction)){
            notificationDesc =  getString(R.string.timing_mal_clear_desc);
            notificationId = 20910;

        }

        malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, DriverId, ClearEventType, true, true, getApplicationContext());
        global.ShowLocalNotification(getApplicationContext(), getString(R.string.event_cleared),  notificationDesc, notificationId);

        if(isRefreshFromServer) {
            GetMalDiaEventsDurationList();
        }

        ClearEventType = "";
        updateMalDiaInfoWindowAtHomePage();

        // update malfunction & diagnostic Fragment screen
        updateMalDiaFragment(false);

    }

    void postAllOfflineSavedData(){
        try{
            // -------- upload offline locally saved data ---------
            UploadSavedShipmentData();
            SaveCertifyLog();
            saveNotifications();
            SaveMalfnDiagnstcLogToServer(null, DriverId);

            if (!SharedPref.GetOdoSavingStatus(getApplicationContext()))
                UploadSavedOdometerData();


            if (SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)) {

                // -----------------------------UnPosted inspection -----------------------------------
                JSONArray inspectionMainDriverArray = inspectionMethod.getOfflineInspectionsArray(Integer.valueOf(DriverId), dbHelper);
                JSONArray inspectionCoDriverArray = inspectionMethod.getOfflineInspectionsArray(Integer.valueOf(CoDriverId), dbHelper);

                if (inspectionMainDriverArray.length() > 0) {
                    saveDriverLogPost.PostDriverLogData(inspectionMainDriverArray, APIs.SAVE_INSPECTION_OFFLINE, Constants.SocketTimeout20Sec, true, false,
                            Integer.valueOf(DriverId), SaveInspectionMain);
                }


                if (inspectionCoDriverArray.length() > 0) {
                    saveDriverLogPost.PostDriverLogData(inspectionCoDriverArray, APIs.SAVE_INSPECTION_OFFLINE, Constants.SocketTimeout20Sec, true, false,
                            Integer.valueOf(CoDriverId), SaveInspectionCo);
                }


                // -----------------------------UnPosted CT-PAT Inspection -----------------------------------
                if (!Constants.IsCtPatUploading) {
                    JSONArray ctPatInspMainDriverArray = ctPatInspectionMethod.getCtPatUnPostedInspArray(Integer.valueOf(DriverId), dbHelper);
                    JSONArray ctPatInspCoDriverArray = ctPatInspectionMethod.getCtPatUnPostedInspArray(Integer.valueOf(CoDriverId), dbHelper);

                    if (ctPatInspMainDriverArray.length() > 0) {
                        saveDriverLogPost.PostDriverLogData(ctPatInspMainDriverArray, APIs.SAVE_17_INSPECTION_OFFLINE, Constants.SocketTimeout20Sec, true, false,
                                Integer.valueOf(DriverId), SaveCtPatInspMain);
                    }


                    if (ctPatInspCoDriverArray.length() > 0) {
                        saveDriverLogPost.PostDriverLogData(ctPatInspCoDriverArray, APIs.SAVE_17_INSPECTION_OFFLINE, Constants.SocketTimeout20Sec, true, false,
                                Integer.valueOf(CoDriverId), SaveCtPatInspCo);
                    }
                }

            } else {
                // -----------------------------UnPosted inspection -----------------------------------
                JSONArray inspectionMainDriverArray = inspectionMethod.getOfflineInspectionsArray(Integer.valueOf(DriverId), dbHelper);
                if (inspectionMainDriverArray.length() > 0) {
                    saveDriverLogPost.PostDriverLogData(inspectionMainDriverArray, APIs.SAVE_INSPECTION_OFFLINE, Constants.SocketTimeout20Sec, true, false,
                            Integer.valueOf(DriverId), SaveInspectionMain);
                }


                // -----------------------------UnPosted CT-PAT Inspection -----------------------------------
                if (!Constants.IsCtPatUploading) {
                    JSONArray ctPatInspMainDriverArray = ctPatInspectionMethod.getCtPatUnPostedInspArray(Integer.valueOf(DriverId), dbHelper);
                    if (ctPatInspMainDriverArray.length() > 0) {
                        saveDriverLogPost.PostDriverLogData(ctPatInspMainDriverArray, APIs.SAVE_17_INSPECTION_OFFLINE, Constants.SocketTimeout20Sec, true, false,
                                Integer.valueOf(DriverId), SaveCtPatInspMain);
                    }

                }
            }


            // Sync data automatically if violation file occured
            SyncData();

            checkDeferralData();

            checkLogMissingIdStatus();

        }catch (Exception e){
            e.printStackTrace();
        }
    }





    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @Override
        public void getResponse(String response, int flag) {
            //  Logger.LogDebug("response", "Service response-" + flag + ": " + response);

            JSONObject obj = null;
            String status = "", message = "";

            try{
                obj = new JSONObject(response);
                status = obj.getString("Status");
                message = obj.getString("Message");
            }catch (Exception e){
                e.printStackTrace();
            }

            if(status.equalsIgnoreCase("true")) {

                switch (flag) {

                    case GetDriverLog:

                        //
                        try {

                            if (!obj.isNull("Data")) {
                                JSONObject dataObj = new JSONObject(obj.getString("Data"));
                                String logModel = dataObj.getString("DriverLogModel");
                                if(!logModel.equals("null")) {
                                    JSONArray selectedArray = new JSONArray(logModel);
                                    DateTime selectedDateTime = Globally.getDateTimeObj(global.ConvertDateFormat(Globally.GetCurrentDeviceDate(null, global, getApplicationContext())), false);
                                    JSONArray logArrayBeforeSelectedDate = hMethods.GetArrayBeforeSelectedDate(driverLogArray, selectedDateTime);

                                    for(int i = 0 ; i < selectedArray.length() ; i++){
                                        JSONObject itemObj = (JSONObject) selectedArray.get(i);
                                        logArrayBeforeSelectedDate.put(itemObj);
                                    }

                                    // ------------ Update log array in local DB ---------
                                    hMethods.DriverLogHelper(Integer.valueOf(DriverId), dbHelper, logArrayBeforeSelectedDate);


                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;


                    case GetDriverLog18Days:
                    case GetCoDriverLog18Days:
                        Is18DaysLogApiCalled = false;

                       // Logger.LogDebug("18DaysLog", ">>>18DaysLog Service: " +flag);

                        try {
                            if (!obj.isNull("Data")) {
                                JSONArray resultArray = new JSONArray(obj.getString("Data"));

                                if (resultArray.length() > 0) {
                                    if(flag == GetDriverLog18Days){
                                        hMethods.DriverLogHelper(Integer.valueOf(SelectedDriverId), dbHelper, resultArray);

                                        if(resultArray.length() > 0) {
                                            JSONObject lastItemJson = hMethods.GetLastJsonFromArray(resultArray);
                                            String DRIVER_JOB_STATUS = lastItemJson.getString(ConstantsKeys.DriverStatusId);
                                            SharedPref.setDriverStatusId(DRIVER_JOB_STATUS, getApplicationContext());

                                            Intent intent = new Intent(ConstantsKeys.IsIgnitionOn);
                                            intent.putExtra(ConstantsKeys.Is18DaysLogUpdate, true);
                                            LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
                                        }

                                    }else{
                                        hMethods.DriverLogHelper(Integer.valueOf(CoDriverId), dbHelper, resultArray);
                                    }

                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;


                    case GetMalDiaEventDuration:
                        Logger.LogDebug("response", "GetMalDiaEventDuration: " + flag + ": " + response);
                        Constants.isCallMalDiaEvent = false;
                        MalDiaEventsApiInProcess = false;
                        IsMissingDiaInProgress = false;
                        SharedPref.saveMissingDiaStatus(false, getApplicationContext());

                        try {

                            JSONArray durationArray = new JSONArray();
                            JSONArray dataArray = new JSONArray(obj.getString("Data"));
                            for(int i = dataArray.length()-1 ; i >= 0 ; i--){
                                JSONObject objItem = (JSONObject) dataArray.get(i);

                                String EngineHours = "0", ClearEngineHour = "", StartOdometer = "", ClearOdometer = "";
                                int TotalMinutes = 0;

                                if(!objItem.getString(ConstantsKeys.EngineHours).equals("null")){
                                    EngineHours = objItem.getString(ConstantsKeys.EngineHours);
                                }

                                if(!objItem.getString(ConstantsKeys.ClearEngineHours).equals("null")){
                                    ClearEngineHour = objItem.getString(ConstantsKeys.ClearEngineHours);
                                }

                                if(!objItem.getString(ConstantsKeys.StartOdometer).equals("null")){
                                    StartOdometer = objItem.getString(ConstantsKeys.StartOdometer);
                                    if(StartOdometer.length() < 8 || StartOdometer.contains(".")) {
                                        StartOdometer = Constants.kmToMeter1(StartOdometer);
                                    }
                                }

                                if(!objItem.getString(ConstantsKeys.ClearOdometer).equals("null")){
                                    ClearOdometer = objItem.getString(ConstantsKeys.ClearOdometer);
                                    if(ClearOdometer.length() < 8 || ClearOdometer.contains(".")) {
                                        ClearOdometer = Constants.kmToMeter1(ClearOdometer);
                                    }
                                }

                                String VIN = "";
                                if(!objItem.isNull(ConstantsKeys.VIN)){
                                    VIN = objItem.getString(ConstantsKeys.VIN);
                                }

                                String DetectionDataEventCode = objItem.getString(ConstantsKeys.DetectionDataEventCode);

                                if(DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic)){
                                    TotalMinutes = (int)constants.getEngineHourDiff(EngineHours, ClearEngineHour);
                                }else{
                                    TotalMinutes = objItem.getInt(ConstantsKeys.TotalMinutes);
                                }

                                String DriverId = objItem.getString(ConstantsKeys.DriverId);
                                boolean isClearEvent;
                                if(objItem.getInt(ConstantsKeys.ClearEventId) > 0){
                                    isClearEvent = true;
                                }else {
                                    isClearEvent = objItem.getBoolean(ConstantsKeys.IsClearEvent);
                                }

                                String LocationType = constants.checkStringInJsonObj(objItem, ConstantsKeys.LocationType);
                                String CurrentStatus = constants.checkStringInJsonObj(objItem, ConstantsKeys.CurrentStatus);

                                JSONObject item = malfunctionDiagnosticMethod.getNewMalDiaDurationObj(
                                        DriverId,
                                        objItem.getString(ConstantsKeys.EventDateTime), // utc date time
                                        objItem.getString(ConstantsKeys.EventEndDateTime),
                                        DetectionDataEventCode,
                                        TotalMinutes,
                                        isClearEvent,
                                        ClearEngineHour,
                                        ClearOdometer,
                                        StartOdometer,
                                        EngineHours,
                                        LocationType,
                                        CurrentStatus,
                                        VIN
                                );

                                durationArray.put(item);

                                if(DetectionDataEventCode.equals(Constants.MissingDataDiagnostic) && !isClearEvent){
                                    //LocationType.equals("X") || LocationType.equals("E")
                                    // if(LocationType.length() == 0){
                                    SharedPref.saveMissingDiaStatus(true, getApplicationContext());
                                    // }

                                }

                            }

                            malfunctionDiagnosticMethod.MalDiaDurationHelper(dbHelper, durationArray);

                            malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());
                            updateMalDiaInfoWindowAtHomePage();

                            // update malfunction & diagnostic Fragment screen
                            updateMalDiaFragment(true);

                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        break;



                    case SaveMalDiagnstcEvent:
                        Logger.LogDebug("SaveMalDiagnstcEvent", "SaveMalDiagnstcEvent saved successfully");
                        isMalfncDataAlreadyPosting = false;
                        IsMissingDiaInProgress = false;
                        powerEventInputArray = new JSONArray();

                        // clear malfunction array
                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());

                        // update malfunction & diagnostic Fragment screen
                        updateMalDiaFragment(false);

                        break;


                    case UpdateOffLineStatus:

                       //    Logger.LogDebug("response", ">>>UpdateOffLine: " + flag  ); //  + ": " + response
                        try {

                            updateOfflineNoResponseCount = 0;
                            updateOfflineApiRejectionCount = 0;
                            constants.IsAlsServerResponding = true;

                            // -------- Save date time Locally -------------
                            try {
                                JSONObject dataObj = new JSONObject(obj.getString(ConstantsKeys.Data));
                                String UtcCurrentDate = dataObj.getString(ConstantsKeys.UTCDateTime);
                                DateTime utcCurrentDateTime = global.getDateTimeObj(UtcCurrentDate, false);

                                try {
                                    SharedPref.setCurrentUTCTime(UtcCurrentDate, getApplicationContext());
                                    boolean isSuggestedEdit = dataObj.getBoolean(ConstantsKeys.SuggestedEdit);
                                    boolean isSuggestedRecall;

                                    if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                                        isSuggestedRecall = SharedPref.isSuggestedRecall(getApplicationContext());
                                    } else {
                                        isSuggestedRecall = SharedPref.isSuggestedRecallCo(getApplicationContext());
                                    }

                                    constants.saveSuggestedStatus(getApplicationContext(), isSuggestedEdit);

                                    if ((isSuggestedEdit && isSuggestedRecall)) {   // || IsCycleRequestIsELDNotification
                                        try {
                                            Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                                            intent.putExtra(ConstantsKeys.SuggestedEdit, isSuggestedEdit);
                                            LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                SharedPref.setOnlineStatus(true, getApplicationContext());

                                // Sync app usage log to server (SAVE sync data service)
                                obdUtil.syncAppUsageLog(getApplicationContext(), DriverId);

                                // need to move in permission api
                                // if (dataObj.has(ConstantsKeys.IsOdometerFromOBD))
                                //    SharedPref.SetOdometerFromOBD(dataObj.getBoolean(ConstantsKeys.IsOdometerFromOBD), getApplicationContext());

                                if (dataObj.has(ConstantsKeys.CycleId) && !dataObj.getString(ConstantsKeys.CycleId).equals("null")){
                                    int CycleId = dataObj.getInt(ConstantsKeys.CycleId);

                                    // Save Driver Cycle With Current Date
                                    constants.SaveCycleWithCurrentDate(CycleId, utcCurrentDateTime.toString(), "UpdateOfflineDriverLog_api",
                                            global, getApplicationContext());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            postAllOfflineSavedData();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        SaveDriverRecordLogUpdates();

                        get18DaysApiData();

                        break;


                    case GetRecapViewFlagMain:
                    case GetRecapViewFlagCo:

                        IsRecapApiCalled = false;

                        try {
                            if (!obj.isNull("Data")) {
                                JSONArray recapArray = recapViewMethod.ParseServerResponseOfArray(obj.getJSONArray("Data"));

                                if (flag == GetRecapViewFlagMain) {
                                    recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DriverId), dbHelper, recapArray);

                                    if (!SharedPref.GetNewLoginStatus(getApplicationContext())) {
                                        Intent intent = new Intent(ConstantsKeys.IsIgnitionOn);
                                        intent.putExtra(ConstantsKeys.IsCertifyReminder, true);
                                        LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
                                    }
                                } else {
                                    recapViewMethod.RecapView18DaysHelper(Integer.valueOf(CoDriverId), dbHelper, recapArray);
                                }

                                // update recap array for reCertify the log if edited
                                constants.UpdateCertifyLogArray(recapViewMethod, DriverId, 7,
                                        dbHelper, getApplicationContext());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;


                    case GetOdometers18Days:

                        try {
                            if (!obj.isNull("Data")) {
                                JSONArray resultArray = new JSONArray(obj.getString("Data"));
                                odometerHelper.Odometer18DaysHelper(Integer.valueOf(DriverId), dbHelper, resultArray);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        break;

                    case GetCtPat18DaysMainDriverLog:

                        try {
                            if (!obj.isNull("Data")) {
                                try {
                                    JSONArray inspectionData = new JSONArray(obj.getString("Data"));

                                    ctPatInspectionMethod.DriverCtPatInsp18DaysHelper(Integer.valueOf(DriverId), dbHelper, inspectionData);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    case GetCtPat18DaysCoDriverLog:

                        try {
                            if (!obj.isNull("Data")) {
                                try {
                                    JSONArray inspectionData = new JSONArray(obj.getString("Data"));

                                    ctPatInspectionMethod.DriverCtPatInsp18DaysHelper(Integer.valueOf(CoDriverId), dbHelper, inspectionData);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                   /* case SaveDriverDeviceUsageLog:
                        try {
                            SharedPref.setLastUsageDataSavedTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
*/

                    case SaveAgricultureAddress:
                        try {
                            Globally.PlayNotificationSound(getApplicationContext());
                            global.ShowLocalNotification(getApplicationContext(),
                                    getString(R.string.exception_disabled),
                                    getString(R.string.ag_exmp_disabled), 2086);

                            SharedPref.setAgricultureExemption(false, getApplicationContext());
                            SharedPref.SaveAgricultureRecord("","","", getApplicationContext());

                            hMethods.SaveDriversJob(DriverId, DeviceId, getString(R.string.end_ag_Exemption),
                                    getString(R.string.disable_agriculture_exception),
                                    SharedPref.getLocationEventType(getApplicationContext()), "", false,
                                    SharedPref.IsNorthCanada(getApplicationContext()), DriverType, constants,
                                    MainDriverPref, CoDriverPref, new EldSingleDriverLogPref(), new EldCoDriverLogPref(),
                                    syncingMethod, global, hMethods, dbHelper, getApplicationContext(), false,
                                    CoDriverId, CoDriverName, false, Constants.Auto);

                            Intent intent = new Intent(ConstantsKeys.DownloadProgress);
                            intent.putExtra(ConstantsKeys.IsAgriException, true);
                            LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        break;

                }
            }else{

                if (message.equalsIgnoreCase("Device Logout")) {
                    driverLogArray = constants.GetDriversSavedArray(getApplicationContext(), MainDriverPref, CoDriverPref);
                    if (driverLogArray.length() == 0) {
                        postAllOfflineSavedData();

                        DeviceLogout(message);

                    }else{
                        SyncUserData();
                    }
                }else {
                    if (flag == GetMalDiaEventDuration) {
                        Constants.isCallMalDiaEvent = false;
                        MalDiaEventsApiInProcess = false;
                    } else if (flag == GetDriverLog18Days || flag == GetCoDriverLog18Days) {
                        Is18DaysLogApiCalled = false;
                    }
                }
            }
        }
    };





    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            //  Logger.LogDebug("error", "error-" +flag + " : " + error);
            switch (flag) {

                case GetRecapViewFlagMain:
                case GetRecapViewFlagCo:

                    IsRecapApiCalled = false;

                    break;

                case UpdateOffLineStatus:

                    if(updateOfflineNoResponseCount > 0) {
                        constants.IsAlsServerResponding = false;
                    }

                    updateOfflineNoResponseCount++;
                    break;

                case GetMalDiaEventDuration:
                    Constants.isCallMalDiaEvent = false;
                    MalDiaEventsApiInProcess = false;
                    break;

                case GetDriverLog18Days:
                case GetCoDriverLog18Days:
                   Is18DaysLogApiCalled = false;
                   break;
            }

        }
    };




    AsyncResponse asyncResponse = new AsyncResponse() {
        @Override
        public void onAsyncResponse(String response, String DriverId) {

            Logger.LogError("String Response", ">>>Sync Response:  " + response);

            try {

                JSONObject obj = new JSONObject(response);
                String status = obj.getString("Status");
                if (status.equalsIgnoreCase("true")) {

                    String msgTxt = "Data syncing is completed" ;
                    /* ------------ Delete posted files from local after successfully posted to server --------------- */
                    if(syncingFile != null && syncingFile.exists()) {
                        syncingFile.delete();
                        syncingFile = null;
                    }


                    if (ViolationFile != null && ViolationFile.exists()) {
                        msgTxt = "Data syncing is completed with violation log file";
                        ViolationFile.delete();
                        ViolationFile = null;
                    }


                    syncingMethod.SyncingLogHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                    Logger.LogDebug("Sync", msgTxt );

                    if(isDriverLogout){
                       isDriverLogout = false;
                        DeviceLogout("Device Logout");
                    }
                }else {
                    if(syncingFile != null && syncingFile.exists())
                        syncingFile.delete();

                    if(ViolationFile != null && ViolationFile.exists())
                        ViolationFile.delete();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };





    RequestResponse requestResponse = new RequestResponse() {
        @Override
        public void onApiResponse(String response, int flag) {

            String status = "", message = "";
            try {
                JSONObject obj = new JSONObject(response);
                status = obj.getString("Status");
                message = obj.getString("Message");

                if (status.equalsIgnoreCase("true")) {
                    switch (flag) {


                        case SaveShippingOffline:
                            shipmentHelper.ShipmentHelper(Integer.valueOf(global.PROJECT_ID), dbHelper, new JSONArray());
                            break;


                        case SaveOdometerOffline:
                            SharedPref.SetOdoSavingStatus(false, getApplicationContext());
                            odometerHelper.OdometerHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                            String SelectedDate = Globally.GetCurrentDeviceDate(null, global, getApplicationContext());
                            DriverId   = SharedPref.getDriverId(getApplicationContext());
                            String CompanyId     = DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext());

                            GetOdometer18Days(DriverId, DeviceId, CompanyId, SelectedDate);

                            break;

                        case Save2ndDriverOdoData:
                            odometerHelper.OdometerHelper(SecondDriver, dbHelper, new JSONArray());
                            break;


                    }
                }else{
                    if (status.equalsIgnoreCase("false")) {
                        if (message.equalsIgnoreCase("Device Logout")) {
                            DeviceLogout(message);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }

        @Override
        public void onResponseError(String error, int flag) {
            Logger.LogDebug("shipmentJsonArray ", ">>>Error");
            if(SaveOdometerOffline == flag){
                SharedPref.SetOdoSavingStatus(false, getApplicationContext());
            }
        }
    };




    ServiceCycle.ServiceCallback serviceCallBack = new ServiceCycle.ServiceCallback() {
        @Override
        public void onServiceResponse(RulesResponseObject RulesObj, RulesResponseObject RemainingTimeObj, boolean IsForground,
                                      boolean isEldToast, String msg, String status) {

            String violatioReason = RulesObj.getViolationReason().trim();

            if(isEldToast){
                if(!status.equals(getString(R.string.screen_reset))) {
                    global.ShowNotificationWithSound(getApplicationContext(), "ELD", msg, mNotificationManager);
                }

                if(IsForground){
                    try {
                        // Update Eld home screen UI
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.putExtra("isUpdate", "true");
                                intent.setAction(constants.packageName);
                                sendBroadcast(intent);
                            }
                        }, 4000);

                    }catch (Exception e){

                    }
                }

            }else {
                if(msg.equals(Constants.AobrdWarning)){
                    global.ShowNotificationWithSound(getApplicationContext(), "AOBRD", "Your current status is "+ status +" but your vehicle is running.", mNotificationManager);
                }else if(msg.contains(getString(R.string.als_alert))){
                    global.ShowNotificationWithSound(getApplicationContext(), "Eld Alert", status, mNotificationManager);
                }else {
                    if (SharedPref.IsReadViolation(getApplicationContext()) == false) {
                        if (RulesObj.isViolation() ) {
                            if(IsForground){
                                global.PlaySound(getApplicationContext());
                                ViolationReason = violatioReason;
                            } else {
                                if(!violatioReason.equalsIgnoreCase("Alert") && violatioReason.equals("")) {
                                    RulesObj.setNotificationType(101);
                                    global.ShowNotificationWithSound(getApplicationContext(), RulesObj, mNotificationManager);
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    ServiceCycle.ServiceError serviceError = new ServiceCycle.ServiceError() {
        @Override
        public void onServiceError(String error, boolean IsForground) {
            Logger.LogDebug("error: ", "error: " + error);
        }
    };



    void ClearLogBeforeResend() {

        // Get posted data as list model
        List<EldDataModelNew> driverLogList = constants.getLogInList(driverLogArray);

        if (DriverType == Constants.MAIN_DRIVER_TYPE) { // Single Driver Type and Position is 0
            MainDriverPref.ClearLocFromList(getApplicationContext());

            //  Save data for Main Driver
            MainDriverPref.SaveDriverLoc(getApplicationContext(), driverLogList);
        }else {
            CoDriverPref.ClearLocFromList(getApplicationContext());

            //  Save data for Co Driver
            CoDriverPref.SaveDriverLoc(getApplicationContext(), driverLogList);
        }


        driverLogArray = new JSONArray();
    }


    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int driver_id, int flag, JSONArray inputData) {

            String status = "", Message = "";
            JSONObject obj = null;
            try {
                // String responseee = "{\"Status\":true,\"Message\":\"Record Successfully Saved\",\"Data\":null}";
                obj = new JSONObject(response);
                status = obj.getString("Status");
                Message = obj.getString("Message");
            }catch (Exception e){
                e.printStackTrace();
            }

            if (status.equals("true")) {
                switch (flag) {

                    case ClearMalDiaEvent:
                        IsMissingDiaInProgress = false;
                        isMalfncDataAlreadyPosting = false;
                        powerEventInputArray = new JSONArray();

                        updateClearEvent(true);

                        // Clearing unposted event array.
                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());

                        break;

                    case SaveDeferralMain:
                        // clear main driver deferral events from local db
                        deferralMethod.DeferralLogHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());
                        isDeferralAlreadyPosting = false;
                        break;

                    case SaveDeferralCo:
                        // clear main driver deferral events from local db
                        deferralMethod.DeferralLogHelper(Integer.valueOf(CoDriverId), dbHelper, new JSONArray());
                        isDeferralAlreadyPostingCo = false;
                        break;


                    case SaveMainDriverLogData:

                        EldFragment.IsSaveOperationInProgress = false;
                         BackgroundLocationService.IsAutoChange = false;
                        driverLogArray = constants.GetDriversSavedArray(getApplicationContext(), MainDriverPref, CoDriverPref);
                        //boolean IsDuplicateStatusAllowed = SharedPref.GetOtherMalDiaStatus(ConstantsKeys.IsDuplicateStatusAllowed, getApplicationContext());
                        boolean IsEditedData = SharedPref.IsEditedData(getApplicationContext());
                        SharedPref.SetEditedLogStatus(false, getApplicationContext());

                        try {
                            if (driverLogArray.length() == 1 || IsEditedData) {
                                ClearLogAfterSuccess(driver_id);

                                // save Co Driver Data if login with co driver
                                SaveCoDriverData();

                                // update DriverLogId of current saved status
                                if (!IsEditedData) {
                                    String DriverLogId = obj.getString(ConstantsKeys.DriverLogId);
                                    hMethods.UpdateLastLogOfDriver(ConstantsKeys.DriverLogId, DriverLogId,
                                            DriverId, dbHelper);

                                    if(EldFragment.IsSaveJobException){
                                        EldFragment.IsSaveJobException = false;
                                       // SelectedDriverId = getSelectedDriverId(driver_id);
                                        DataRequest18Days(SelectedDriverId, GetDriverLog18Days);
                                    }

                                }else{

                                   // SelectedDriverId = getSelectedDriverId(driver_id);
                                    DataRequest18Days(SelectedDriverId, GetDriverLog18Days);
                                }

                            } else {

                            /* Check Reason: some times data was uploading in background and user entered new status in between.
                                 In api response we are clearing the entries and in between entry was skipped before upload to server.
                                So to avoid this we are checking input length and current log length.*/
                                if (driverLogArray.length() == inputData.length()) {
                                    if (RePostDataCountMain > 1 ) { //|| !IsDuplicateStatusAllowed
                                        ClearLogAfterSuccess(driver_id);
                                        RePostDataCountMain = 0;

                                        // save Co Driver Data if login with co driver
                                        SaveCoDriverData();

                                        // update DriverLogId of current saved status
                                        GetRecapView18DaysData(DriverId, DeviceId, GetRecapViewFlagMain);


                                    } else {
                                        // compare logs to avoid duplicate entries
                                       // driverLogArray = syncingMethod.getUnpostedLogOnly(DriverId, driverLogArray, dbHelper);
                                        driverLogArray = syncingMethod.getUnPostedLogOnly(inputData, driverLogArray);

                                        if (driverLogArray.length() > 0) {    //SaveRequestCount < 2 || IsDuplicateStatusAllowed
                                            saveActiveDriverData();
                                            RePostDataCountMain++;
                                            ClearLogBeforeResend();
                                        } else {
                                            ClearLogAfterSuccess(driver_id);

                                            // save Co Driver Data if login with co driver
                                            SaveCoDriverData();

                                        }



                                    }

                                    if(EldFragment.IsSaveJobException){
                                        EldFragment.IsSaveJobException = false;
                                       // SelectedDriverId = getSelectedDriverId(driver_id);
                                        DataRequest18Days(SelectedDriverId, GetDriverLog18Days);
                                    }

                                } else {
                                    if (RePostDataCountMain > 2) {
                                        ClearLogAfterSuccess(driver_id);
                                        RePostDataCountMain = 0;
                                        ClearLogBeforeResend();

                                        // save Co Driver Data if login with co driver
                                        SaveCoDriverData();

                                    } else {

                                        // compare logs to avoid duplicate entries
                                       // driverLogArray = syncingMethod.getUnpostedLogOnly(DriverId, driverLogArray, dbHelper);
                                        driverLogArray = syncingMethod.getUnPostedLogOnly(inputData, driverLogArray);

                                        if (driverLogArray.length() > 0) {    //SaveRequestCount < 2 || IsDuplicateStatusAllowed
                                            saveActiveDriverData();
                                            RePostDataCountMain++;

                                            ClearLogBeforeResend();
                                        } else {
                                            ClearLogAfterSuccess(driver_id);
                                        }

                                    }
                                }
                            }
                        }catch ( Exception e){
                            e.printStackTrace();
                        }

                        break;


                    case SaveCoDriverLogData:
                        EldFragment.IsSaveOperationInProgress = false;
                        ClearLogAfterSuccess(driver_id);
                        RePostDataCountCo = 0;
                       // SelectedDriverId = getSelectedDriverId(driver_id);
                        DataRequest18Days(CoDriverId, GetCoDriverLog18Days);

                        // call update offline api if data posted to server
                        int offlineDataLength = constants.OfflineData(getApplicationContext(), MainDriverPref,
                                CoDriverPref, global.isSingleDriver(getApplicationContext()));
                        if (offlineDataLength == 0 ) {   // This check is used to save offline saved data to server first then online status will be changed.
                            String VIN = SharedPref.getVINNumber(getApplicationContext());
                            UpdateOfflineDriverLog(DriverId, CoDriverId, DeviceId, VIN,
                                    String.valueOf(obdVehicleSpeed), true);
                        }

                        break;


                    case SaveDriverLog:

                        // ------------ Clear Driver Log Record File locally ------------
                        logRecordMethod.UpdateLogRecordHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                        break;


                    case SaveCertifyLog:
                        // ------------ Clear Certify Log Record File locally ------------
                        certifyLogMethod.CertifyLogHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                        break;

                    case SaveCertifyLogCo:
                        // ------------ Clear Certify Log Record File locally ------------
                        certifyLogMethod.CertifyLogHelper(Integer.valueOf(CoDriverId), dbHelper, new JSONArray());
                        break;

                    case SaveInspectionMain:
                    case SaveInspectionCo:
                        // Clear all unposted Array from list......
                        inspectionMethod.DriverOfflineInspectionsHelper(driver_id, dbHelper, new JSONArray());

                        break;


                    case SaveCtPatInspMain:
                    case SaveCtPatInspCo:
                        // Clear all unposted CT-PAT Array from list......
                        ctPatInspectionMethod.DriverCtPatUnPostedInspHelper(driver_id, dbHelper, new JSONArray());

                        break;


                    case SaveMalDiagnstcEvent:
                        isMalfncDataAlreadyPosting = false;
                        IsMissingDiaInProgress = false;

                        // clear malfunction array
                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());

                        IsMissingDiaInProgress = false;
                        break;

                    case SaveVehPwrEventLog:
                        vehiclePowerEventMethod.VehPowerEventHelper(dbHelper, new JSONArray());
                        break;


                    case MainDriverNotification:
                    // Clear un posted logs from array
                    notificationMethod.SaveToNotificationHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                    break;


                    case CoDriverNotification:
                        // Clear un posted logs from array
                        notificationMethod.SaveToNotificationHelper(Integer.valueOf(CoDriverId), dbHelper, new JSONArray());

                        break;

                }
            }else {

                if(flag == SaveMainDriverLogData || flag == SaveCoDriverLogData) {
                    EldFragment.IsSaveOperationInProgress = false;

                    if (Message.equalsIgnoreCase("Duplicate Records")) {
                        ClearLogAfterSuccess(driver_id);
                        Logger.LogDebug("flag", "flag: " + flag);

                        if(flag == SaveMainDriverLogData){
                            SaveCoDriverData();
                        }
                    }else if(Message.equals("Device Logout")){
                        driverLogArray = constants.GetDriversSavedArray(getApplicationContext(), MainDriverPref, CoDriverPref);
                        if (driverLogArray.length() == 0) {
                            DeviceLogout(Message);
                        }else{
                            SyncUserData();
                        }

                    }
                }else if(flag == SaveDriverLog){
                    if(Message.equals("Record Not Found")) {
                        logRecordMethod.UpdateLogRecordHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());
                        global.ShowLocalNotification(getApplicationContext(), "ELD", Message, 2015);
                    }
                }else if(flag == ClearMalDiaEvent){
                    isMalfncDataAlreadyPosting = false;
                    IsMissingDiaInProgress = false;
                    if(Message.equals("Exception is occcured")) {
                        updateClearEvent(false);
                    }else if(Message.equals("Record Clearance is failed")){
                        GetMalDiaEventsDurationList();
                        global.ShowLocalNotification(getApplicationContext(), "Timing Malfunction", Message, 20910);
                    }
                }else if(flag == SaveDeferralMain){
                    isDeferralAlreadyPosting = false;
                }else if(flag == SaveDeferralCo){
                    isDeferralAlreadyPostingCo = false;
                }else if(flag == SaveMalDiagnstcEvent){
                    IsMissingDiaInProgress = false;
                    isMalfncDataAlreadyPosting = false;
                    if(Message.equals("Record Clearance is failed")){
                        GetMalDiaEventsDurationList();
                        global.ShowLocalNotification(getApplicationContext(), "Timing Malfunction", Message, 20910);
                    }
                }

            }


        }
        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Logger.LogDebug("errorrr ", ">>>error dialog: " );
            if(flag == SaveDeferralMain){
                isDeferralAlreadyPosting = false;
            }else if(flag == SaveDeferralCo){
                isDeferralAlreadyPostingCo = false;
            }else if(flag == ClearMalDiaEvent){
                isMalfncDataAlreadyPosting = false;
                IsMissingDiaInProgress = false;
            }else if(flag == SaveMainDriverLogData || flag == SaveCoDriverLogData){
                EldFragment.IsSaveOperationInProgress = false;
            }

        }
    };


    void SyncUserData(){


        if(global.isSingleDriver(getApplicationContext())) {
            JSONArray syncArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DriverId), dbHelper);
            if(syncArray.length() > 0) {
                SyncDriverData(DriverId, syncArray, false);
            }else{
                DeviceLogout("Device Logout");
            }
        }else{
            JSONArray MainDriverSyncArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DriverId), dbHelper);
            JSONArray CoDriverSyncArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(CoDriverId), dbHelper);

            if(MainDriverSyncArray.length() == 0 && CoDriverSyncArray.length() == 0 ) {
                DeviceLogout("Device Logout");
            }else{

                if(MainDriverSyncArray.length() > 0) {
                    SyncDriverData(DriverId, MainDriverSyncArray, false);
                }

                if(CoDriverSyncArray.length() > 0) {
                    SyncDriverData(CoDriverId, CoDriverSyncArray, true);
                }

            }
        }


    }


    private void SyncDriverData(String DriverId, JSONArray syncArray, boolean isCoDriver){

        if(syncArray.length() > 0) {
            String filePrefix = "Sync_";
            if(isCoDriver){
                filePrefix = "SyncCo_";
            }
            File driverSyncFile = Globally.SaveFileInSDCard(filePrefix, syncArray.toString(), false, getApplicationContext());

            isDriverLogout = true;
            // Sync driver log API data to server with SAVE_LOG_TEXT_FILE (SAVE sync data service)
            SyncDataUpload syncDataUpload = new SyncDataUpload(getApplicationContext(), DriverId, driverSyncFile,
                    null, null, false, asyncResponse );
            syncDataUpload.execute();
        }
    }






    /* ---------------------- Save Event Log Request Response ---------------- */
    DriverLogResponse saveEventRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, JSONArray inputData) {

            try {

                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ConstantsKeys.Status);
                // String message  = obj.getString(ConstantsKeys.Message);
                if (status.equalsIgnoreCase("true")) {
                    if(flag == SaveBleEventLog) {
                        bleGpsAppLaunchMethod.ClearAndUpdateSelectedEventLog(Constants.LogEventTypeBle, dbHelper);
                    }else {
                        bleGpsAppLaunchMethod.ClearAndUpdateSelectedEventLog(Constants.LogEventTypeGps, dbHelper);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Logger.LogDebug("errorrr ", ">>>Event Log error");
        }

    };




    void ClearLogAfterSuccess(int driverType) {
        driverLogArray = new JSONArray();
        //syncingMethod.SyncingLogVersion2Helper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

        if (driverType == 0) { // Single Driver Type and Position is 0
            MainDriverPref.ClearLocFromList(getApplicationContext());
        }else{
            CoDriverPref.ClearLocFromList(getApplicationContext());
        }
    }


    private String getSelectedDriverId(int driverType){
        if (driverType == 0) { // Single Driver Type and Position is 0
            return DriverConst.GetDriverDetails(DriverConst.DriverID, getApplicationContext());
        }else{
            return DriverConst.GetDriverDetails(DriverConst.DriverID, getApplicationContext());
        }
    }
    /* ------------ Save Co-Driver Data those data was saved in offline mode -------------- */
    void SaveCoDriverData() {
        JSONArray LogArray = new JSONArray();
        int SecondDriverType = 0;
        int logArrayCount = 0, socketTimeout = 10000;

        if (!global.isSingleDriver(getApplicationContext())) {

            String SavedLogApi = "";
            if (SharedPref.IsEditedData(getApplicationContext())) {
                SavedLogApi = APIs.SAVE_DRIVER_EDIT_LOG_NEW;
            } else {
                SavedLogApi = APIs.SAVE_DRIVER_STATUS;
            }



            if(DriverType == Constants.MAIN_DRIVER_TYPE) {  // Current active driver is Main Driver. So we need co driver details and we are getting co driver's details.

                try {
                    SecondDriverType = 1;   // Co Driver

                    LogArray = constants.GetDriverOffLineSavedLog(getApplicationContext(), SecondDriverType, MainDriverPref, CoDriverPref);
                    logArrayCount = LogArray.length();
                    if(logArrayCount < 3 ){
                        socketTimeout = constants.SocketTimeout10Sec;  //10 seconds
                    }else if(logArrayCount < 10){
                        socketTimeout = constants.SocketTimeout20Sec;  //20 seconds
                    }else{
                        socketTimeout = constants.SocketTimeout40Sec;  //40 seconds
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (LogArray.length() > 0) {
                    EldFragment.IsSaveOperationInProgress = true;
                    saveDriverLogPost.PostDriverLogData(LogArray, SavedLogApi, socketTimeout, false,
                            false, SecondDriverType, SaveCoDriverLogData);
                }

            } else {
                // Current active driver is Co Driver. So we need main driver details and we are getting main driver's details.
                try {

                    SecondDriverType = 0;
                    LogArray = constants.GetDriverOffLineSavedLog(getApplicationContext(), SecondDriverType, MainDriverPref, CoDriverPref);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (LogArray.length() > 0) {
                    EldFragment.IsSaveOperationInProgress = true;
                    saveDriverLogPost.PostDriverLogData(LogArray, SavedLogApi, socketTimeout, false,
                            false, SecondDriverType, SaveCoDriverLogData);
                }

            }
        }


    }



    void playSound(){
        try{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    global.PlaySound(getApplicationContext());
                }
            }, 3000);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    void networkUsage() {

        try {
            long mobileBytes = TrafficStats.getMobileRxBytes();
            long totalBytes = TrafficStats.getTotalRxBytes();
            MobileUsage = constants.convertLongDataToMb(mobileBytes);
            TotalUsage = constants.convertLongDataToMb(totalBytes);

        }catch (Exception e){
            MobileUsage = "-1";
            TotalUsage = "-1";
            e.printStackTrace();
        }

        // Get running processes
        try {
            ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(getApplicationContext().ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = manager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningApp : runningApps) {
                long received = TrafficStats.getUidRxBytes(runningApp.uid);
                long sent = TrafficStats.getUidTxBytes(runningApp.uid);

                AlsReceivedData = constants.convertLongDataToMb(received);
                AlsSendingData = constants.convertLongDataToMb(sent);
                //Logger.LogError(TAG_DATA_STAT , "Data ALS received: " + AlsReceivedData);
                // Logger.LogError(TAG_DATA_STAT , "Data ALS Sent: " + AlsSendingData);
                // Logger.LogDebug("data_nw_usage", String.format(Locale.getDefault(),"uid: %1d - name: %s: Sent = %1d, Rcvd = %1d", runningApp.uid, runningApp.processName, sent, received));
            }
        }catch (Exception e){
            AlsReceivedData = "-1";
            AlsSendingData = "-1";
            e.printStackTrace();
        }
    }



    //  ------------- Wired OBD ----------
    //Bind to the remote service
    private void BindConnection(){
        try{
            if(!isBound){
                Intent intent = new Intent();
                intent.setClassName(ServerPackage, ServerService);
                this.bindService(intent, this.connection, Context.BIND_AUTO_CREATE);
            }
        }catch (Exception e){ }

    }




    private void StartStopServer(final String value){
        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED ) { //  || SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE

            SharedPref.SetWiredObdServerCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

            if (isBound) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Setup the message for invocation
                        try {
                            // Logger.LogDebug(TAG_OBD, "Wired Server Call");
                            //Set the ReplyTo Messenger for processing the invocation response
                            Message msg1 = new Handler().obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("key", value);
                            msg1.setData(bundle);
                            msg1.replyTo = replyTo;

                            //Make the invocation
                            messenger.send(msg1);
                            // bundle.clear();


                        } catch (Exception rme) {
                            Logger.LogDebug(TAG_OBD, "Invocation Failed!!");
                        }
                    }
                });

            } else {
                try {
                    Logger.LogDebug(TAG_OBD, "Service is Not Bound!!");
                    this.connection = new RemoteServiceConnection();
                    BindConnection();
                } catch (Exception e) {
                }
            }
        }
    }


    private class RemoteServiceConnection implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName component, IBinder binder)
        {
            messenger = new Messenger(binder);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName component)
        {
            messenger = null;
            isBound = false;
        }
    }




    private void checkPUExceedStatus(JSONObject dataObj, double AccumulativePersonalDistance){
        try{
            boolean isPU75Crossed = false;
            if(dataObj != null) {
                isPU75Crossed = dataObj.getBoolean(ConstantsKeys.PersonalUse75Km);
            }else{

                if(AccumulativePersonalDistance == -1){
                    AccumulativePersonalDistance = constants.getAccumulativePersonalDistance(DriverId, offsetFromUTC,
                            Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext()),
                            Globally.GetCurrentUTCDateTime(), hMethods, dbHelper, getApplicationContext());
                }


                if(AccumulativePersonalDistance > 75){
                    isPU75Crossed = true;
                }

            }

            boolean wasPU75Crossed = SharedPref.isPersonalUse75KmCrossed(getApplicationContext());

            SharedPref.setPersonalUse75Km(isPU75Crossed, getApplicationContext());

            if(isPU75Crossed && wasPU75Crossed == false){
                try {
                    get18DaysLogArrayLocally();

                    boolean isPersonal = true;
                    if (EldFragment.driverLogArray.length() > 0) {
                        JSONObject lastJsonItem = hMethods.GetLastJsonFromArray(EldFragment.driverLogArray);
                        //  int currentJobStatus = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
                        isPersonal = lastJsonItem.getBoolean(ConstantsKeys.Personal);
                    }
                    String driverStatusId = SharedPref.getDriverStatusId(getApplicationContext());

                    if (driverStatusId.equals(Globally.OFF_DUTY) && isPersonal) {
                        String certifyTitle = "Personal Use Alert";
                        String titleDesc = "Personal Use limit has been exceeded above 75 km for the day. Please change your status.";
                        global.PlayNotificationSound(getApplicationContext());

                        if (UILApplication.isActivityVisible()) {
                            Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                            intent.putExtra(ConstantsKeys.PersonalUse75Km, true);
                            intent.putExtra(ConstantsKeys.Title, certifyTitle + " !!");
                            intent.putExtra(ConstantsKeys.Desc, titleDesc);
                            intent.putExtra(ConstantsKeys.OBDSpeed, obdVehicleSpeed);
                            LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);

                        } else {
                            global.ShowLocalNotification(getApplicationContext(), certifyTitle,
                                    titleDesc, 2008);
                        }
                        String blank = "";

                        saveLogWithRuleCall(blank, blank, obdVehicleSpeed, "not_saved");

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    void DeviceLogout(String message){
        EldActivity activity = EldActivity.instance;

        global.ClearAllFields(getApplicationContext());
        StopService();

        if (activity != null) {
            Globally.PlayNotificationSound(getApplicationContext());
            global.ShowLocalNotification(getApplicationContext(), "ELD eBook", message, 2003);

            Intent i = new Intent(activity, LoginActivity.class);
            activity.startActivity(i);
            activity.finish();
        }
    }



}