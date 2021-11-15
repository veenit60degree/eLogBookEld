package com.background.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ble.listener.MyReceiveListener;
import com.ble.util.ConstantEvent;
import com.ble.util.EventBusInfo;
import com.ble.util.BleUtil;
import com.ble.utils.ToastUtil;
import com.constants.APIs;
import com.constants.AsyncResponse;
import com.constants.CheckConnectivity;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.DriverLogResponse;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.htstart.htsdk.HTBleSdk;
import com.htstart.htsdk.bluetooth.HTBleData;
import com.htstart.htsdk.bluetooth.HTBleDevice;
import com.htstart.htsdk.bluetooth.HTModeSP;
import com.htstart.htsdk.minterface.HTBleScanListener;
import com.htstart.htsdk.minterface.IReceiveListener;
import com.local.db.CTPatInspectionMethod;
import com.local.db.CertifyLogMethod;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DeferralMethod;
import com.local.db.DriverPermissionMethod;
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
import com.messaging.logistic.EldActivity;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.messaging.logistic.fragment.EldFragment;
import com.notifications.NotificationManagerSmart;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.MainDriverEldPref;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.wifi.settings.WiFiConfig;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
public class BackgroundLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, TextToSpeech.OnInitListener, LocationListener {

    String TAG = "Service";
    String TAG_OBD = "OBD Service";
    String noObd = "obd not connected";
    String ClearEventType = "";

    String obdEngineHours = "0", currentHighPrecisionOdometer = "0", obdOdometer = "0", obdTripDistance = "0", ignitionStatus = "OFF", HighResolutionDistance = "0", truckRPM = "0", apiReturnedSpeed = "";

    int GPSSpeed = 0;
    int timeInSec = -1;
    int timeDuration = 4000;

    int SecondDriver                        = 0;
    final int UpdateOffLineStatus           = 2;
    final int SaveShippingOffline           = 3;
    final int SaveOdometerOffline           = 4;
    final int GetOdometers18Days            = 6;
    final int SaveCertifyLog                = 7;
    final int SaveDriverLog                 = 8;
    final int SaveInspectionMain            = 9;
    final int SaveInspectionCo              = 10;
    final int SaveCtPatInspMain             = 11;
    final int SaveCtPatInspCo               = 12;
    final int GetCtPat18DaysMainDriverLog   = 13;
    final int GetCtPat18DaysCoDriverLog     = 14;
    final int SaveDriverDeviceUsageLog      = 15;
    final int SaveMalDiagnstcEvent          = 16;
    final int SaveDeferralMain              = 17;
    final int SaveDeferralCo                = 18;
    final int GetMalDiaEventDuration        = 20;
    final int ClearMalDiaEvent              = 21;

    final int GetRecapViewFlagMain          = 111;
    final int GetRecapViewFlagCo            = 112;

    final int SaveMainDriverLogData         = 1002;
    final int SaveCoDriverLogData           = 1003;
    final int Save2ndDriverOdoData          = 101;
    int offSetFromServer                    = 0;
    int offsetFromUTC                       = 0;
    int VehicleSpeed                        = -1;
    int recapApiAttempts                    = 0;
    int updateOfflineApiRejectionCount      = 0;
    int updateOfflineNoResponseCount        = 0;
    int RePostDataCountMain                 = 0;
    int RePostDataCountCo                   = 0;

    int SpeedCounter                        = 60;      // initially it is 60 so that ELD rule is called instantly
    int MaxSpeedCounter                     = 60;
    int HalfSpeedCounter                    = 30;
    int sameLocationCount                   = 0;
    int ignitionOffCount                    = 0;

    public static int obdVehicleSpeed       = -1;
    public static int GpsVehicleSpeed       = -1;


    //private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    //private Location mLastLocation;
    public static LocationRequest locationRequest;
    public static GoogleApiClient mGoogleApiClient;

    protected LocationManager locationManager;

    int LocRefreshTime = 10;
    int CheckInternetConnection = 2;
    private static final long MIN_TIME_BW_UPDATES = 2 * 1000;   //30 sec. 30000 - 1/2 minute -- [960000 milli sec -- (16 minutes)]
    private static final long MIN_TIME_LOCATION_UPDATES = 1 * 1000;   // 1 sec
    private static final long OBD_TIME_LOCATION_UPDATES = 10 * 1000;   // 10 sec
    private static final long IDLE_TIME_LOCATION_UPDATES = 3600 * 1000;   // 1 hour

    VolleyRequest GetOdometerRequest, ctPatInsp18DaysRequest, saveDriverDeviceUsageLog;
    VolleyRequest UpdateLocReqVolley, UpdateUserStatusVolley, GetRecapView18DaysData, SaveMalDiaEventRequest, GetMalfunctionEvents;
    Map<String, String> params;
    String DriverId = "", CoDriverId = "", DeviceId = "", VIN_NUMBER = "", VehicleId = "", CompareLocVal = "";
    String ObdRestarted = "OBD Restarted";

    int DriverType = 0, LastObdSpeed = -1;
    boolean isStopService = false,  RestartObdFlag = false, IsAutoSync = false;;
    JSONArray shipmentArray, odometerArray, driverLogArray;
    DBHelper dbHelper;
    HelperMethods hMethods;
    ShipmentHelperMethod shipmentHelper;
    OdometerHelperMethod odometerHelper;
    RecapViewMethod recapViewMethod;
    OdometerHelperMethod odometerhMethod;
    InspectionMethod inspectionMethod;
    CTPatInspectionMethod ctPatInspectionMethod;
    LocationMethod LocMethod;
    LatLongHelper latLongHelper;
    NotificationMethod notificationMethod;
    DriverPermissionMethod driverPermissionMethod;
    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
    DeferralMethod deferralMethod;

    CheckConnectivity checkConnectivity;

    Globally global;

    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;
    NotificationManagerSmart mNotificationManager;
    Constants constants;
    ShippingPost postRequest;
    ServiceCycle serviceCycle;
    boolean IsLogApiACalled = false;
    public static boolean IsRecapApiACalled = false;
    // boolean Is30SecInterval = false;
    boolean IsAlertTimeValid = false;
    boolean isGpsUpdate      = false;
    boolean IsOBDPingAllowed = false;
    boolean isMalfncDataAlreadyPosting = false;
    boolean isWiredObdRespond = false;
    public static boolean IsAutoChange = false; //, IsAutoLogSaved = false;

    double latitude, longitude;
    private Handler mHandler = new Handler();
    File locDataFile;
    private TextToSpeech tts;
    String ViolationReason = "";
    SaveDriverLogPost saveDriverLogPost;
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
    long processStartTime = -1;
    double tempOdo = 1180203799;  //1.090133595E9
    double tempEngHour = 22999.95;


    // Bluetooth obd adapter decleration
    boolean mIsScanning = false;
    int bleScanCount = 0;
    private BluetoothAdapter mBTAdapter;
    private ArrayList<HTBleDevice> mHTBleDevices = new ArrayList<>();
    //private LinkedList<HTBleData> mHtblData = new LinkedList<>();

    public static boolean OBD_DISCONNECTED  = true;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "---------onCreate Service");

        global                  = new Globally();
        dbHelper                = new DBHelper(getApplicationContext());
        hMethods                = new HelperMethods();
        shipmentHelper          = new ShipmentHelperMethod();
        odometerHelper          = new OdometerHelperMethod();
        odometerhMethod         = new OdometerHelperMethod();
        recapViewMethod         = new RecapViewMethod();
        LocMethod               = new LocationMethod();
        latLongHelper           = new LatLongHelper();
        notificationMethod      = new NotificationMethod();
        syncingMethod           = new SyncingMethod();
        driverPermissionMethod  = new DriverPermissionMethod();
        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();
        deferralMethod          = new DeferralMethod();

        checkConnectivity       = new CheckConnectivity(getApplicationContext());
        MainDriverPref          = new MainDriverEldPref();
        CoDriverPref            = new CoDriverEldPref();
        constants               = new Constants();
        serviceCycle            = new ServiceCycle(getApplicationContext());
        postRequest             = new ShippingPost(getApplicationContext(), requestResponse);
        UpdateLocReqVolley      = new VolleyRequest(getApplicationContext());
        UpdateUserStatusVolley  = new VolleyRequest(getApplicationContext());
        GetRecapView18DaysData  = new VolleyRequest(getApplicationContext());
        SaveMalDiaEventRequest  = new VolleyRequest(getApplicationContext());
        GetMalfunctionEvents    = new VolleyRequest(getApplicationContext());
        mNotificationManager    = new NotificationManagerSmart(getApplicationContext());

        saveDriverDeviceUsageLog= new VolleyRequest(getApplicationContext());
        GetOdometerRequest      = new VolleyRequest(getApplicationContext());
        ctPatInsp18DaysRequest  = new VolleyRequest(getApplicationContext());
        inspectionMethod        = new InspectionMethod();
        ctPatInspectionMethod   = new CTPatInspectionMethod();
        logRecordMethod         = new UpdateLogRecordMethod();
        certifyLogMethod        = new CertifyLogMethod();
        saveDriverLogPost       = new SaveDriverLogPost(getApplicationContext(), saveLogRequestResponse);

        data                    = new OBDDeviceData();
        decoder                 = new Decoder();
        wifiConfig              = new WiFiConfig();
        tcpClient               = new TcpClient(obdResponseHandler);
        tts                     = new TextToSpeech(getApplicationContext(), this);

        mTimer                  = new Timer();
        mTimer.schedule(timerTask, OBD_TIME_LOCATION_UPDATES, OBD_TIME_LOCATION_UPDATES);

        DeviceId                = SharedPref.GetSavedSystemToken(getApplicationContext());
        IsOBDPingAllowed        =  SharedPref.isOBDPingAllowed(getApplicationContext());
        SharedPref.setServiceOnDestoryStatus(false, getApplicationContext());
        SharedPref.saveBleScanCount(0, getApplicationContext());

        createLocationRequest(MIN_TIME_LOCATION_UPDATES);

        // check availability of play services
        if (global.checkPlayServices(getApplicationContext())) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            requestLocationWithoutPlayServices();
        }



//  ------------- Wired OBD ----------
        this.connection = new RemoteServiceConnection();
        this.replyTo = new Messenger(new IncomingHandler());
        SharedPref.SaveObdStatus(SharedPref.getObdStatus(getApplicationContext()), SharedPref.getObdLastStatusTime(getApplicationContext()),
                                                    global.GetCurrentUTCTimeFormat(), getApplicationContext());

        BindConnection();
        initBleListener();

        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {

            checkPermissionsBeforeScanBle();

            if(!HTBleSdk.Companion.getInstance().isConnected() && SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED){
                SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(),
                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
            }
        }else{
            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                checkWiredObdConnection();
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

    }




    //  ------------- Wired OBD data response handler ----------
    private class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            String obdLastCallDate = SharedPref.getWiredObdCallTime(getApplicationContext());
            if(obdLastCallDate.length() > 10){
                final DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);    // Current Date Time
                final DateTime savedDateTime = global.getDateTimeObj(obdLastCallDate, false);

                int timeInSec = (int) Constants.getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
                if(timeInSec > 2){  // minimum call interval is 3 sec.
                    SharedPref.SetWiredObdCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
                  //  Log.d("GetCurrentDateTime", "GetCurrentDateTime: " + Globally.GetCurrentDateTime());

                    final Bundle bundle = msg.getData();
                    isWiredObdRespond = true;

                    String timeStamp = "--", vin = "--";
                    int speed = 0;

                    try {
                        timeStamp = bundle.getString(constants.OBD_TimeStamp);
                        obdOdometer = bundle.getString(constants.OBD_Odometer);
                        obdTripDistance = bundle.getString(constants.OBD_TripDistance);
                        ignitionStatus = bundle.getString(constants.OBD_IgnitionStatus);
                        truckRPM = bundle.getString(constants.OBD_RPM);
                        obdEngineHours = bundle.getString(constants.OBD_EngineHours);
                        vin = bundle.getString(constants.OBD_VINNumber);
                        speed = bundle.getInt(constants.OBD_Vss);

                        if(bundle.getString(constants.OBD_HighPrecisionOdometer) != null) {
                            currentHighPrecisionOdometer = bundle.getString(constants.OBD_HighPrecisionOdometer);
                        }

                        if(vin.length() <= 5){
                            vin = "";
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED ) {
                        // save wired obd call response time to recheck later
                        SharedPref.SetWiredObdCallTime(Globally.GetCurrentDateTime(), getApplicationContext());

                        sendBroadCast(parseObdDatainHtml(vin, speed, -1), "");

                        if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                                !SharedPref.getPassword(getApplicationContext()).equals("")) {
                            // this check is used to avoid fake PC/YM continue alert. because obd some times return ignition status value off false. So we are checking it continuesly 3 times
                            if (ignitionStatus.equals("OFF")) {
                                speed = 0;
                                if (ignitionOffCount > 2) {
                                    // call ELD rules with wired tablet data
                                    obdCallBackObservable(speed, vin, timeStamp, null);
                                    SharedPref.SetWrongVinAlertView(false, getApplicationContext());

                                } else {
                                    // ignore to call wrong rules. Verify 3 times false ignition status because some times OBD returns false value.
                                    ignitionOffCount++;
                                    if(ignitionOffCount == 1) {
                                        constants.saveObdData(getObdSource(), "VIN: " + vin + ", WIRED-IgnitionOffCount- " + ignitionOffCount, obdOdometer, currentHighPrecisionOdometer,
                                                currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                                                String.valueOf(-1), obdEngineHours, timeStamp, timeStamp,
                                                DriverId, dbHelper, driverPermissionMethod, obdUtil);
                                    }
                                }

                            } else {
                                ignitionOffCount = 0;

                                // call ELD rules with wired tablet data
                                obdCallBackObservable(speed, vin, timeStamp, null);
                            }
                        }

                    }

                    bundle.clear();


                }else if(timeInSec < 0){
                    SharedPref.SetWiredObdCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
                    Log.d("GetCurrentDateTime", "Negative Time: " + timeInSec);
                }

            }else{
                SharedPref.SetWiredObdCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
            }


        }
    }





    private void obdCallBackObservable(int speed, String vin, String timeStamp, HTBleData htBleData){

        int OBD_LAST_STATUS = SharedPref.getObdStatus(getApplicationContext());
        String last_obs_source_name = SharedPref.getObdSourceName(getApplicationContext());

        try {
            if(htBleData != null) {
                // bluetooth OBD data
                obdEngineHours = "0"; currentHighPrecisionOdometer = "0"; obdOdometer = "0"; obdTripDistance = "0";
                ignitionStatus = "OFF"; truckRPM = "0"; ;

                obdOdometer = htBleData.getOdoMeter();
                obdTripDistance = htBleData.getDistanceSinceLast();
                truckRPM = htBleData.getEngineSpeed();
                obdEngineHours = htBleData.getEngineHours();
                currentHighPrecisionOdometer = htBleData.getOdoMeter();
                Globally.LATITUDE = htBleData.getLatitude();
                Globally.LONGITUDE = htBleData.getLongitude() ;

                // this check is using to confirm loc update, because in loc disconnection ble OBD is sending last saved location.
                if(Globally.LATITUDE.equals(PreviousLatitude) && Globally.LONGITUDE.equals(PreviousLongitude) &&
                        !currentHighPrecisionOdometer.equals(PreviousOdometer)){
                    Globally.LATITUDE = "";
                    Globally.LONGITUDE = "";

                }

                PreviousLatitude = htBleData.getLatitude();
                PreviousLongitude = htBleData.getLongitude();
                PreviousOdometer = currentHighPrecisionOdometer;


                if(Globally.LATITUDE.length() < 5){
                    SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());
                }else{
                    SharedPref.SetLocReceivedFromObdStatus(true, getApplicationContext());
                }

                saveEcmLocationWithTime(Globally.LATITUDE, currentHighPrecisionOdometer);


                if (Integer.valueOf(truckRPM) > 0 || speed > 0) {
                    ignitionStatus = "ON";
                    ignitionOffCount = 0;
                }

                // this check is used to avoid fake PC/YM continue alert. because obd some times return ignition status value off false. So we are checking it continuesly 3 times
                if(ignitionStatus.equals("OFF")){
                    if(ignitionOffCount > 2){
                       // ignitionOffCount = 0;

                    }else{
                        ignitionOffCount++;
                        if(ignitionOffCount == 1) {
                            constants.saveObdData(getObdSource(), vin + " - BLE-IgnitionOffCount- " + ignitionOffCount, obdOdometer, currentHighPrecisionOdometer,
                                    currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                                    String.valueOf(-1), obdEngineHours, timeStamp, timeStamp,
                                    DriverId, dbHelper, driverPermissionMethod, obdUtil);
                        }
                        // temp ON ignitionStatus to check 3 times to confirm
                        ignitionStatus = "ON";
                    }

                }else{
                    ignitionOffCount = 0;
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }


        SharedPref.setVehicleVin(vin, getApplicationContext());
        SharedPref.setVss(speed, getApplicationContext());
        SharedPref.setRPM(truckRPM, getApplicationContext());







        // ---------------- temp data ---------------------




       /* if (OBD_DISCONNECTED) {
            ignitionStatus = "ON";
            truckRPM = "700";
            speed = 0;
            obdEngineHours = "23789.5";
            if (OBD_LAST_STATUS != Constants.WIRED_CONNECTED) {
                obdEngineHours = "23789.7";
                SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, global.getCurrentDate(),
                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
            }
            OBD_LAST_STATUS = constants.WIRED_CONNECTED;
        } else {
            ignitionStatus = "OFF";
            truckRPM = "0";
            speed = 0;
            obdEngineHours = "23789.5";
            if (OBD_LAST_STATUS != Constants.WIRED_DISCONNECTED) {
                SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, global.getCurrentDate(),
                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
            }

            OBD_LAST_STATUS = constants.WIRED_DISCONNECTED;

            String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());

            // save log when ignition status is changed.    //SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getApplicationContext())
            if (lastIgnitionStatus.equals("ON") ) {
                SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, "WiredOBD",
                        global.getCurrentDate(), global.GetCurrentUTCTimeFormat(),
                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()), getApplicationContext());

            }

        }

        tempEngHour = tempEngHour + .01;
        obdEngineHours = ""+tempEngHour;
        tempOdo = tempOdo + 400;
        obdOdometer = "" + BigDecimal.valueOf(tempOdo).toPlainString();
        currentHighPrecisionOdometer = obdOdometer;

        sendBroadCast(parseObdDatainHtml(vin, speed, -1), "");

        SharedPref.setVehicleVin(vin, getApplicationContext());
        SharedPref.setVss(speed, getApplicationContext());
        SharedPref.setRPM(truckRPM, getApplicationContext());

*/



// ====================================================================================================






// ------------------------------------------------------------------------


        String VINNumberFromApi = SharedPref.getVINNumber(getApplicationContext());

        if (VINNumberFromApi.length() > 5) {
            // this check is using because some times OBD returns 0 value
            checkEngHrOdo();

            writeWiredObdLogs(OBD_LAST_STATUS, speed, timeStamp, "Save Truck Info - OBD_LAST_STATUS: ");

            if (OBD_LAST_STATUS == constants.WIRED_CONNECTED || OBD_LAST_STATUS == constants.BLE_CONNECTED) {

                    // ELD rule calling for Wired OBD
                    try {
                        if (ignitionStatus.equals("ON")) {


                            String currentLogDate = global.GetCurrentDateTime();
                            Globally.IS_OBD_IGNITION = true;
                            continueStatusPromotForPcYm("ON", last_obs_source_name, global.getCurrentDate(), OBD_LAST_STATUS);


                            /* ======================== Malfunction & Diagnostic Events ========================= */
                            checkPowerMalDiaEvent();   // checking Power Data Compliance Mal/Dia event
                            checkEngSyncClearEvent();  // checking EngineSyncDataCompliance Mal/Dia clear event if already occurred


                            double obdOdometerDouble = Double.parseDouble(currentHighPrecisionOdometer);
                            String previousHighPrecisionOdometer = SharedPref.getHighPrecisionOdometer(getApplicationContext());

                            // save current odometer for HOS calculation
                            saveDayStartOdometer(currentHighPrecisionOdometer);

                            String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());
                            if (savedDate.length() == 0 && obdOdometerDouble > 0) {
                                // save current HighPrecisionOdometer locally
                                savedDate = currentLogDate;
                                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                            }

                            Globally.VEHICLE_SPEED = speed;
                            boolean isDrivingAllowed = true;
                            if (SharedPref.isDrivingAllowed(getApplicationContext()) == false && speed >= 8) {
                                final DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);    // Current Date Time
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

                            double savedOdometer;
                            try {
                                savedOdometer = Double.parseDouble(previousHighPrecisionOdometer);
                            } catch (Exception e) {
                                savedOdometer = obdOdometerDouble;
                            }

                            if (obdOdometerDouble >= savedOdometer) {    // needs for this check is to avoid the wrong auto change status because some times odometers are not coming

                                double calculatedSpeedFromOdo = speed;

                                try {
                                    if (OBD_LAST_STATUS == constants.WIRED_CONNECTED) {
                                        // calculating speed to comparing last saved odometer and current odometer (in meter) with time difference in seconds
                                        if (SharedPref.isOdoCalculationAllowed(getApplicationContext())) {
                                            calculatedSpeedFromOdo = constants.calculateSpeedFromWiredTabOdometer(savedDate, currentLogDate,
                                                    previousHighPrecisionOdometer, currentHighPrecisionOdometer, getApplicationContext());
                                        }

                                        sendBroadCast(parseObdDatainHtml(vin, speed, calculatedSpeedFromOdo), "");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    calculatedSpeedFromOdo = speed;
                                }

                                if (speed >= 8) {
                                    SharedPref.setVehilceMovingStatus(true, getApplicationContext());
                                } else {
                                    SharedPref.setVehilceMovingStatus(false, getApplicationContext());
                                }

                                if (isDrivingAllowed) {
                                    timeDuration = Constants.SocketTimeout3Sec;
                                    callRuleWithStatusWise(currentHighPrecisionOdometer, savedDate, vin, timeStamp, speed, calculatedSpeedFromOdo);
                                }

                            }

                            if (OBD_LAST_STATUS != constants.BLE_CONNECTED) {
                                callWiredDataService(timeDuration);
                            }

                        } else {
                            String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());

                            // save log when ignition status is changed.    //SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getApplicationContext())
                            if (lastIgnitionStatus.equals("ON")) {
                                constants.saveObdData(getObdSource(), "VIN: " + vin + ", Save Truck Info - Ignition Status: " + ignitionStatus, obdOdometer, currentHighPrecisionOdometer,
                                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                                        String.valueOf(-1), obdEngineHours, timeStamp, timeStamp,
                                        DriverId, dbHelper, driverPermissionMethod, obdUtil);

                                // save truck info to check power compliance mal/dia later.
                                SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, last_obs_source_name,
                                        global.getCurrentDate(), global.GetCurrentUTCTimeFormat(),
                                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()),
                                        getApplicationContext());

                            }

                            speed = 0;
                            Globally.IS_OBD_IGNITION = false;
                            SharedPref.SetTruckIgnitionStatusForContinue("OFF", last_obs_source_name, "", getApplicationContext());
                            SharedPref.setVss(speed, getApplicationContext());
                            SharedPref.setVehilceMovingStatus(false, getApplicationContext());

                            String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());
                            if (savedDate.length() == 0 && Double.parseDouble(currentHighPrecisionOdometer) > 0) {
                                savedDate = global.GetCurrentDateTime();
                                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, global.GetCurrentDateTime(), getApplicationContext());
                            }

                            callRuleWithStatusWise(currentHighPrecisionOdometer, savedDate, vin, timeStamp, speed, 0);

                            Globally.VEHICLE_SPEED = 0;

                            callWiredDataService(Constants.SocketTimeout8Sec);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Globally.IS_OBD_IGNITION = false;
                        Globally.VEHICLE_SPEED = -1;

                        callWiredDataService(Constants.SocketTimeout3Sec);

                    }

                } else {

                    // check in wire disconnect case but device is also not connected with ALS/OBD wifi ssid
                    if (SharedPref.getObdPreference(getApplicationContext()) != Constants.OBD_PREF_WIFI) {

                        // Check Engine Sync data Malfunction/Diagnostic event
                        checkEngineSyncMalDiaOccurredEvent(speed, false);


                        if (OBD_LAST_STATUS == constants.WIRED_DISCONNECTED) {
                            callWiredDataService(Constants.SocketTimeout3Sec);
                        } else {
                            callWiredHandlerWhenBle(Constants.SocketTimeout3Sec);
                        }

                    }

                    SharedPref.setVehilceMovingStatus(false, getApplicationContext());

                }

            if(constants.isValidVinFromObd(getApplicationContext()) == false &&
                    SharedPref.IsWrongVinAlertView(getApplicationContext()) == false) {

                SharedPref.SetWrongVinAlertView(true, getApplicationContext());

                Globally.PlayNotificationSound(getApplicationContext());
                global.ShowLocalNotification(getApplicationContext(),
                        getString(R.string.confirm_vin),
                        getString(R.string.confirm_vin_desc), 20811);

                if(UILApplication.isActivityVisible() && TabAct.speedAlertBtn != null) {
                    TabAct.speedAlertBtn.performClick();
                }
            }

        } else {
            callWiredDataService(Constants.SocketTimeout3Sec);
        }
        LastObdSpeed = speed;
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
                DateTime currentTime = Globally.GetCurrentJodaDateTime();
                DateTime lastCallDateTime = Globally.getDateTimeObj(lastCallTime, false);
                long secDiff = Constants.getDateTimeDuration(lastCallDateTime, currentTime).getStandardSeconds();
                long CheckIntervalInSec = 60;
                if(ignitionStatus.equals("OFF")){
                    CheckIntervalInSec = 180;   // 3 min
                }

                if (secDiff >= CheckIntervalInSec) {
                    SharedPref.setObdWriteCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
                    constants.saveObdData(getObdSource(),  "VIN: " + SharedPref.getVehicleVin(getApplicationContext()) + ", " + detail +OBD_LAST_STATUS, obdOdometer, currentHighPrecisionOdometer,
                            currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                            String.valueOf(-1), obdEngineHours, timeStamp, timeStamp,
                            DriverId, dbHelper, driverPermissionMethod, obdUtil);
                }
            } else {
                SharedPref.setObdWriteCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
            }
        }
    }


    void callRuleWithStatusWise(String currentHighPrecisionOdometer, String savedDate, String vin, String timeStamp, int speed, double calculatedSpeedFromOdo){
        try{
            String currentLogDate = global.GetCurrentDateTime();
            String jobType = SharedPref.getDriverStatusId(getApplicationContext());
            double intHighPrecisionOdometerInKm = (Double.parseDouble(currentHighPrecisionOdometer) * 0.001);
            if (jobType.equals(global.DRIVING)) {

                timeDuration = Constants.SocketTimeout10Sec;
                if (constants.minDiff(savedDate, global, getApplicationContext()) > 0) {
                    saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, vin, intHighPrecisionOdometerInKm,
                            timeStamp, savedDate, "DRIVING", calculatedSpeedFromOdo);

                }

                if(speed >= 8 && LastObdSpeed < 8){
                    sendBroadcastUpdateObd(true);
                }

            } else if (jobType.equals(global.ON_DUTY)) {

                // if speed is coming >8 then ELD rule is called after 8 sec to change the status to Driving as soon as.
                if (speed >= 8 && calculatedSpeedFromOdo >= 8 && !truckRPM.equals("0")) {

                    try {
                        if (EldFragment.driverLogArray == null || EldFragment.driverLogArray.length() == 0) {
                            EldFragment.driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
                        }
                        boolean isYardMove = hMethods.isPCYM(EldFragment.driverLogArray);
                        if(isYardMove){
                            String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getApplicationContext());
                            if(speed >= 8 && (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2))) {   // In Yard move
                                timeDuration = Constants.SocketTimeout6Sec;
                                saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, vin, intHighPrecisionOdometerInKm,
                                        timeStamp, savedDate, "OnDutyYM Speed: " + speed, calculatedSpeedFromOdo);
                            }else{
                                // call ELD rule after 1 minute to improve performance
                                if (constants.minDiff(savedDate, global, getApplicationContext()) > 0) {
                                    saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, vin, intHighPrecisionOdometerInKm,
                                            timeStamp, savedDate, "OnDutyYM Speed: " + speed, calculatedSpeedFromOdo);
                                }
                                timeDuration = Constants.SocketTimeout5Sec;
                            }
                        }else{
                            timeDuration = Constants.SocketTimeout15Sec;
                            saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, vin, intHighPrecisionOdometerInKm,
                                    timeStamp, savedDate, "OnDuty Speed: " + speed, calculatedSpeedFromOdo);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } else {

                    if(truckRPM.equals("0")){
                        speed = 0;
                        timeDuration = Constants.SocketTimeout10Sec;
                    }else{
                        if(speed == 0){
                            timeDuration = Constants.SocketTimeout3Sec;
                        }
                    }

                    // call ELD rule after 1 minute to improve performance
                    if (constants.minDiff(savedDate, global, getApplicationContext()) > 0) {
                        saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, vin, intHighPrecisionOdometerInKm,
                                timeStamp, savedDate, "OnDuty Speed: "+speed, calculatedSpeedFromOdo);
                    }
                }

            } else {

                // =================== For OFF Duty & Sleeper case =====================

                if (speed <= 0 && calculatedSpeedFromOdo <= 0 ) {
                    //   Log.d("ELD Rule", "data is correct for this status. No need to call ELD rule.");
                    if(speed == 0)
                        timeDuration = Constants.SocketTimeout3Sec;

                    // call ELD rule after 1 minute to improve performance
                    if (constants.minDiff(savedDate, global, getApplicationContext()) > 0) {
                        SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                    }
                } else {
                    if (speed >= 8 && calculatedSpeedFromOdo >= 8  && !truckRPM.equals("0")) {    // if speed is coming >8 then ELD rule is called after 8 sec to change the status to Driving as soon as.

                        if(jobType.equals(global.SLEEPER)){
                            timeDuration = Constants.SocketTimeout15Sec;
                            saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, vin, intHighPrecisionOdometerInKm,
                                    timeStamp, savedDate, "Sleeper-Speed: "+speed, calculatedSpeedFromOdo);
                        }else{
                            if (EldFragment.driverLogArray == null || EldFragment.driverLogArray.length() == 0) {
                                EldFragment.driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
                            }
                            boolean isPersonal = hMethods.isPCYM(EldFragment.driverLogArray);
                            if(isPersonal){
                                double AccumulativePersonalDistance = constants.getAccumulativePersonalDistance(DriverId, offsetFromUTC, Globally.GetCurrentJodaDateTime(),
                                        Globally.GetCurrentUTCDateTime(), hMethods, dbHelper, getApplicationContext());
                                //SharedPref.isPersonalUse75KmCrossed(getApplicationContext())
                                if(AccumulativePersonalDistance >= 75 || Constants.isPcYmAlertButtonClicked){

                                    // check PU status exceeding status if 75km exceeded showing popup
                                    checkPUExceedStatus(null);

                                    saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, vin, intHighPrecisionOdometerInKm,
                                            timeStamp, savedDate, "PersonalUse-Speed: "+speed, calculatedSpeedFromOdo);
                                    timeDuration = Constants.SocketTimeout15Sec;
                                }else{
                                    timeDuration = Constants.SocketTimeout5Sec;
                                    SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                                }

                            }else{
                                timeDuration = Constants.SocketTimeout15Sec;
                                saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, vin, intHighPrecisionOdometerInKm,
                                        timeStamp, savedDate, "OffDuty-Speed: "+speed, calculatedSpeedFromOdo);
                            }

                        }

                    }else{
                        SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            constants.saveObdData(getObdSource(), "callRuleWithStatusWise Exception: " +e.toString(), obdOdometer, currentHighPrecisionOdometer,
                    currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                    String.valueOf(-1), obdEngineHours, timeStamp, timeStamp,
                    DriverId, dbHelper, driverPermissionMethod, obdUtil);
        }
    }


    private String getObdSource(){
        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
            return constants.BleObd;
        }else if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED){
            return constants.WiredOBD;
        }else{
            return constants.WifiOBD;
        }
    }


    void callWiredDataService(int timeDuration){
        try {
            if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED ) {
                if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                        !SharedPref.getPassword(getApplicationContext()).equals("") &&
                            getApplicationContext() != null) {
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

    void callWiredHandlerWhenBle(int timeDuration){
        try {
            if(getApplicationContext() != null) {
                if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                        !SharedPref.getPassword(getApplicationContext()).equals("")) {
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

    String parseObdDatainHtml(String vin, int speed, double calculatedSpeed){

        if(calculatedSpeed < 0){
            return "<b>ignitionStatus:</b> " + ignitionStatus + "<br/>" +  "<b>Truck RPM:</b> " + truckRPM + "<br/>" +  "<b>Odometer:</b> " + obdOdometer + "<br/>" +
                    "<b>currentHighPrecisionOdometer:</b> " + currentHighPrecisionOdometer + "<br/>" +
                    "<b>Speed:</b> " + speed + "<br/>" +
                    "<b>VIN:</b> " + vin + "<br/>" +
                    "<b>Trip Distance:</b> " + obdTripDistance + "<br/>" +
                    "<b>EngineHours:</b> " + obdEngineHours + "<br/>" ;
        }else{
           return  "<b>ignitionStatus:</b> " + ignitionStatus + "<br/>" +  "<b>Truck RPM:</b> " + truckRPM + "<br/>" +   "<b>Odometer:</b> " + obdOdometer + "<br/>" +
                    "<b>currentHighPrecisionOdometer:</b> " + currentHighPrecisionOdometer + "<br/>" +   "<b>Speed:</b> " + speed + "<br/>" +
                    "<b>Calculated Speed:</b> " + calculatedSpeed + "<br/>" +  "<b>VIN:</b> " + vin + "<br/>" +
                    "<b>Trip Distance:</b> " + obdTripDistance + "<br/>" +   "<b>EngineHours:</b> " + obdEngineHours + "<br/>" ;
        }

    }


    private void checkWiredObdConnection(){
        int lastObdStatus = SharedPref.getObdStatus(getApplicationContext());
        obdShell = ShellUtils.execCommand("cat /sys/class/power_supply/usb/type", false);

       if (SharedPref.getVINNumber(getApplicationContext()).length() > 5) {
            if (obdShell.result == 0) {
                //System.out.println("obd --> cat type --> " + obdShell.successMsg);
                if (obdShell.successMsg.contains("USB_DCP")) {  // Connected State
                    if (lastObdStatus != Constants.WIRED_CONNECTED) {

                        constants.saveObdData(getObdSource(), "WIRED-CONNECTED", obdOdometer, "-1",
                                currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                                "-1", obdEngineHours, "", "",
                                    DriverId, dbHelper, driverPermissionMethod, obdUtil);

                        callWiredDataService(500);

                        sendBroadCast("<b>Wired tablet connected</b> <br/>", "");
                              sendBroadcastUpdateObd(false);

                        sendEcmBroadcast(false);
                        global.ShowLocalNotification(getApplicationContext(),
                                getString(R.string.wired_tablettt),
                                getString(R.string.wired_tablet_connected), 2081);

                    }

                    SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, global.getCurrentDate(),
                            global.GetCurrentUTCTimeFormat(), getApplicationContext());

                } else {
                    // Disconnected State. Save only when last status was not already disconnected
                    if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                        if (lastObdStatus != constants.WIRED_DISCONNECTED) {

                            constants.saveObdData(getObdSource(), "WIRED-DISCONNECTED", obdOdometer, "-1",
                                    currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                                    "-1", obdEngineHours, "", "",
                                    DriverId, dbHelper, driverPermissionMethod, obdUtil);


                            SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, global.getCurrentDate(),
                                    global.GetCurrentUTCTimeFormat(), getApplicationContext());

                            obdCallBackObservable(-1, "", "", null);
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

                        constants.saveObdData(getObdSource(), "WIRED-ERROR", obdOdometer, "-1",
                                currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                                "-1", obdEngineHours, "", "",
                                    DriverId, dbHelper, driverPermissionMethod, obdUtil);


                        sendEcmBroadcast(true);
                        Globally.PlayNotificationSound(getApplicationContext());
                        global.ShowLocalNotification(getApplicationContext(),
                                getString(R.string.wired_tablettt),
                                getString(R.string.wired_tablet_conn_error), 2081);

                        SharedPref.SaveObdStatus(Constants.WIRED_ERROR, global.getCurrentDate(),
                                global.GetCurrentUTCTimeFormat(), getApplicationContext());
                              sendBroadcastUpdateObd(false);
                    }
                }
            }
        }


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



    private void checkPermissionsBeforeScanBle() {

        initHtBle();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isConnected = HTBleSdk.Companion.getInstance().isConnected();   //HTModeSP.INSTANCE.getDeviceMac()
        HTBleSdk.Companion.getInstance().isConnected();
        try {
            if (bluetoothAdapter != null) {
                if (!bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.enable();
                    sendBroadCast("BlueTooth was disabled. Turning on..", "");
                    constants.saveAppUsageLog("BlueTooth was disabled. Turning on..", false, false, obdUtil);

                } else {
                    if (constants.CheckGpsStatusToCheckMalfunction(getApplicationContext())) {

                        if ( !isConnected) {    //!mIsScanning &&

                            StartScanHtBle();

                        }

                    }
                }
            }
        }catch (Exception e){}
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initHtBle(){

        if (!BleUtil.isBLESupported(this)) {
            sendBroadCast(getString(R.string.ble_not_supported), "");
            return;
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();

        }

        if (mBTAdapter == null) {
            sendBroadCast(getString(R.string.bt_unavailable), "");
            return;
        }

        if (!mBTAdapter.isEnabled()) {
            mBTAdapter.enable();
        }

    }

    private boolean isBleEnabled(){
        boolean isBleEnabled = false;
        try{
            BluetoothManager manager = BleUtil.getManager(getApplicationContext());
            if (manager != null) {
                mBTAdapter = manager.getAdapter();
                isBleEnabled = mBTAdapter.isEnabled();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isBleEnabled;
    }




    void initBleListener(){

        HTBleSdk.Companion.getInstance().registerCallBack(new IReceiveListener() {
            @Override
            public void onConnected(@Nullable String s) {
              //  Log.d("getAddress-onConnected", "getAddress: " +s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_CONNECTED, s));
                sendBroadCast(getString(R.string.ht_connected), "");
            }

            @Override
            public void onConnectTimeout(@Nullable String s) {
                try {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_TIMEOUT, s));
                    //Log.d("getAddress-Timeout", "getAddress: " +s);

                    constants.saveObdData(getObdSource(), "BLE - ConnectTimeout: " +s+ ", IsEnabled: " + isBleEnabled(),
                            obdOdometer, "-1",
                            currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                            "-1", obdEngineHours, "", "",
                            DriverId, dbHelper, driverPermissionMethod, obdUtil);


                    if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                        sendBroadCast(getString(R.string.ht_connect_timeout), "");

                        if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {

                            SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(),
                                    global.GetCurrentUTCTimeFormat(), getApplicationContext());

                            sendBroadcastUpdateObd(false);
                            sendEcmBroadcast(true);
                            if(SharedPref.getVINNumber(getApplicationContext()).length() > 5) {
                                Globally.PlayNotificationSound(getApplicationContext());
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectionError(@NonNull String s, int i, int i1) {
                try{
                   // Log.d("getAddress-error", "getAddress: " +s);

                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_ERROR, s));
                    sendBroadCast(getString(R.string.ht_connect_error), "");

                    constants.saveObdData(getObdSource(), "BLE - ConnectionError: " + s
                                    + ", ( " + i + ", " + i1 + ") , Ble Enabled: " + isBleEnabled(),
                            obdOdometer, "-1",
                            currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                            "-1", obdEngineHours, "", "",
                            DriverId, dbHelper, driverPermissionMethod, obdUtil);

                    if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                        if(SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {

                            SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(),
                                    global.GetCurrentUTCTimeFormat(), getApplicationContext());

                            if(SharedPref.getVINNumber(getApplicationContext()).length() > 5) {
                                Globally.PlayNotificationSound(getApplicationContext());
                                global.ShowLocalNotification(getApplicationContext(),
                                        getString(R.string.BluetoothOBD),
                                        getString(R.string.obd_ble_discon_conn_error), 2081);

                                sendBroadcastUpdateObd(false);
                                sendEcmBroadcast(true);
                                sendBroadCast(getString(R.string.ht_disconnected), "");
                            }
                        }

                        // call handler callback method to check malfunction/diagnostic when Connection Error occurred
                        StartStopServer(constants.WiredOBD);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnected(@Nullable String address) {
                try {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, address));
                   // Log.d("getAddress-onDisconnect", "getAddress: " +s);

                    constants.saveObdData(getObdSource(), "BLE-DISCONNECTED: " + address + ", Ble Enabled: " + isBleEnabled(),
                            obdOdometer, "-1",
                            currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                            "-1", obdEngineHours, "", "",
                            DriverId, dbHelper, driverPermissionMethod, obdUtil);


                    if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                            !SharedPref.getPassword(getApplicationContext()).equals("")) {

                        if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                            sendBroadCast(getString(R.string.ht_disconnected), "");

                            if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {

                                SharedPref.SetWrongVinAlertView(false, getApplicationContext());
                                SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(),
                                        global.GetCurrentUTCTimeFormat(), getApplicationContext());

                                if(SharedPref.getVINNumber(getApplicationContext()).length() > 5) {
                                    Globally.PlayNotificationSound(getApplicationContext());

                                    String savedMacAddress = SharedPref.GetBleOBDMacAddress(getApplicationContext());
                                    if(savedMacAddress.length() == 0 || savedMacAddress.equals(address)) {
                                        global.ShowLocalNotification(getApplicationContext(),
                                                getString(R.string.BluetoothOBD),
                                                getString(R.string.obd_ble_disconnected), 2081);
                                    }else{
                                        global.ShowLocalNotification(getApplicationContext(),
                                                getString(R.string.BleOBDConnErr),
                                                getString(R.string.connErrorDesc) , 2081);
                                    }

                                    sendBroadcastUpdateObd(false);
                                    sendEcmBroadcast(true);
                                    sendBroadCast(getString(R.string.ht_disconnected), "");
                                }
                            }

                            // call handler callback method to check malfunction/diagnostic when disconnected
                            StartStopServer(constants.WiredOBD);

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onReceive(@NotNull String address, @NotNull String uuid, @NotNull HTBleData htBleData) {
              //  EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
                if (htBleData.getEventType() == 0 && htBleData.getEventCode() == 1) {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
                } else {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_QUERY_DATA_AVAILABLE, address, uuid, htBleData));
                }
                Log.d("BleObd","onReceive RPM: "+ htBleData.getEngineSpeed() + ", VehicleSpeed: " + htBleData.getVehicleSpeed());

                try {
                    if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                            !SharedPref.getPassword(getApplicationContext()).equals("")) {

                        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                            //  mHtblData.add(htBleData);

                            String savedMacAddress = SharedPref.GetBleOBDMacAddress(getApplicationContext());
                            if(savedMacAddress.length() == 0 || savedMacAddress.equals(address)) {

                                SharedPref.SaveBleOBDMacAddress(address, getApplicationContext());

                                if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_CONNECTED) {
                                    SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, global.getCurrentDate(),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());

                                    constants.saveObdData(getObdSource(), "VIN: "+ htBleData.getVIN_Number() + " - BLE-CONNECTED: onReceive", obdOdometer, currentHighPrecisionOdometer,
                                            currentHighPrecisionOdometer, "", "", truckRPM, htBleData.getVehicleSpeed(),
                                            String.valueOf(-1), obdEngineHours, Globally.GetCurrentDateTime(), Globally.GetCurrentDateTime(),
                                            DriverId, dbHelper, driverPermissionMethod, obdUtil);

                                    sendBroadcastUpdateObd(false);

                                    sendEcmBroadcast(false);
                                    if(SharedPref.getVINNumber(getApplicationContext()).length() > 5) {
                                        global.ShowLocalNotification(getApplicationContext(),
                                                getString(R.string.BluetoothOBD),
                                                getString(R.string.obd_ble), 2081);
                                    }

                                }

                                if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                                    sendBroadCast(BleUtil.decodeDataChange(htBleData, HTBleSdk.Companion.getInstance().getAddress()), "");
                                    obdCallBackObservable(Integer.valueOf(htBleData.getVehicleSpeed()), htBleData.getVIN_Number(), global.GetCurrentDateTime(), htBleData);
                                }

                            }else{

                                disconnectBleObd();

                            }


                        }else{
                            disconnectBleObd();
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(@NotNull String address, @NotNull String uuid, @NotNull String sequenceID, @NotNull int status) {
                Log.e("status", "==" + status + "==" + address);
                bleScanCount = 0;
                //Log.d("getAddress-onResponse", "getAddress: " +address);

                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_RESPONSE, address, uuid, status));
                sendBroadCast(getString(R.string.ht_ble_response1), "");
            }
        });




    }


    public void StartScanHtBle(){

        try {
            HTBleSdk.Companion.getInstance().startHTBleScan(new HTBleScanListener() {
                @Override
                public void onScanStart() {
                    mHTBleDevices.clear();

                    bleScanCount++;
                    mIsScanning = true;
                    sendBroadCast(getString(R.string.Scanning), "");
                }

                @Override
                public void onScanning(@org.jetbrains.annotations.Nullable HTBleDevice htBleDevice) {
                    if (mHTBleDevices.contains(htBleDevice))
                        return;

                    mIsScanning = false;
                    mHTBleDevices.add(htBleDevice);

                    connectHtBle(htBleDevice);
                }

                @Override
                public void onScanFailed(int i) {
                    mIsScanning = false;
                    sendBroadCast(getString(R.string.ht_scan_error), "");
                }

                @Override
                public void onScanStop() {
                    mIsScanning = false;
                    SharedPref.saveBleScanCount(bleScanCount, getApplicationContext());

                    if (HTBleSdk.Companion.getInstance().isConnected()) {   //HTModeSP.INSTANCE.getDeviceMac()
                        sendBroadCast(getString(R.string.ht_scan_completed), "");
                    } else {
                        sendBroadCast(getString(R.string.ht_scan_completed_not_found), "");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void connectHtBle(final HTBleDevice htBleDevice) {
        HTBleSdk.Companion.getInstance().stopHTBleScan();
        if (HTBleSdk.Companion.getInstance().isAllConnected()) {
           // ToastUtil.show(getApplicationContext(), getString(R.string.ht_connect_error_other));
            sendBroadCast(getString(R.string.ht_connect_error_other), "");
        } else {

            String macAddress = htBleDevice.getAddress();
            String savedMacAddress = SharedPref.GetBleOBDMacAddress(getApplicationContext());
            if(savedMacAddress.length() == 0 || savedMacAddress.equals(macAddress)) {
                sendBroadCast(getString(R.string.ht_connected), "");
                HTBleSdk.Companion.getInstance().connect(htBleDevice);

                SharedPref.SaveBleOBDMacAddress(macAddress, getApplicationContext());
            }else{
                Globally.ShowLogoutSpeedNotification(getApplicationContext(),
                        getString(R.string.BleOBDConnErr),
                        getString(R.string.connErrorDesc), 2081);
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

    private boolean isMalDiaAllowed(){
        try {
            if (SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerComplianceMal, getApplicationContext()) ||
                    SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerDataDiag, getApplicationContext()) ||
                    SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PostioningComplMal, getApplicationContext()) ||
                    SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncMal, getApplicationContext()) ||
                    SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncDiag, getApplicationContext())) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }


    private void checkEngineSyncMalDiaOccurredEvent(int speed, boolean isDriverSwitched){

        try {

            if(SharedPref.isEngSyncMalfunction(getApplicationContext()) == false) {
                boolean isEngineSyncMalAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncMal, getApplicationContext());
                boolean isEngineSyncDiaAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncDiag, getApplicationContext());
                String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());

                if ((isEngineSyncMalAllowed || isEngineSyncDiaAllowed) && lastIgnitionStatus.equals("ON")) {

                    DateTime disconnectTime = global.getDateTimeObj(SharedPref.getObdLastStatusTime(getApplicationContext()), false);
                    DateTime currentTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);

                    if (disconnectTime != null && currentTime != null) {
                        int timeInSec = (int) Constants.getDateTimeDuration(disconnectTime, currentTime).getStandardSeconds();
                        //Seconds.secondsBetween(disconnectTime, currentTime).getSeconds();
                        if (timeInSec >= 70 || isDriverSwitched) {

                            boolean isEngSyncDiaOccurred = SharedPref.isEngSyncDiagnstc(getApplicationContext());
                            if (isEngSyncDiaOccurred == false && isEngineSyncDiaAllowed) {

                                constants.saveObdData(getObdSource(), "EngSyncDia - lastIgnitionStatus: " + lastIgnitionStatus, "", currentHighPrecisionOdometer,
                                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                                        "", obdEngineHours, global.GetCurrentDateTime(), "",
                                        DriverId, dbHelper, driverPermissionMethod, obdUtil);

                                obdUtil.syncObdSingleLog(getApplicationContext(), DriverId, getDriverName(), 10);


                                SharedPref.saveEngSyncDiagnstcStatus(true, getApplicationContext());
                                constants.saveDiagnstcStatus(getApplicationContext(), true);
                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());
                                SharedPref.setEngSyncClearEventCallTime(Globally.GetCurrentDateTime(), getApplicationContext());

                                // save occurred event in malfunction/diagnostic table
                                saveMalfunctionEventInTable(Constants.EngineSyncDiagnosticEvent,
                                        getString(R.string.eng_sync_dia_occured),
                                        global.GetCurrentUTCTimeFormat() );


                                // save malfunction entry in duration table
                                malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                                        global.GetCurrentUTCTimeFormat(),
                                        global.GetCurrentUTCTimeFormat(),
                                        Constants.EngineSyncDiagnosticEvent, constants, getApplicationContext());

                                // update mal/dia status for enable disable according to log
                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                                updateMalDiaInfoWindowAtHomePage();

                                // update malfunction & diagnostic Fragment screen
                                updateMalDiaFragment(true);

                                global.ShowLocalNotification(getApplicationContext(),
                                        getString(R.string.dia_event),
                                        getString(R.string.eng_sync_dia_occured_desc), 2090);

                                Globally.PlaySound(getApplicationContext());

                            } else {

                                if (isEngineSyncMalAllowed) {

                                    String clearEventLastCallTime = SharedPref.getEngSyncMalEventCallTime(getApplicationContext());
                                    if (clearEventLastCallTime.length() == 0) {
                                        SharedPref.setEngSyncMalEventCallTime(global.GetCurrentDateTime(), getApplicationContext());
                                    }

                                    // Checking after 1 min
                                    if (constants.minDiffMalfunction(clearEventLastCallTime, global, getApplicationContext()) > 0) {
                                        // update call time
                                        SharedPref.setEngSyncMalEventCallTime(global.GetCurrentDateTime(), getApplicationContext());

                                        boolean isEngSyncMalOccurred = SharedPref.isEngSyncMalfunction(getApplicationContext());
                                        if (isEngSyncMalOccurred == false) {
                                            double last24HrEngSyncEventTime = malfunctionDiagnosticMethod.getLast24HourEventsDurInMin(Constants.EngineSyncDiagnosticEvent, dbHelper);  //getTotalEngSyncMissingMin(DriverId, dbHelper);
                                            int minDiff = constants.getMinDifference(SharedPref.getObdLastStatusTime(getApplicationContext()),
                                                    global.GetCurrentDateTime());
                                            double totalEngSyncMissingMin = last24HrEngSyncEventTime + minDiff;
                                            if (totalEngSyncMissingMin >= Constants.PowerEngSyncMalOccTime) {

                                                constants.saveObdData(getObdSource(), "EngSyncMal - lastIgnitionStatus: " + lastIgnitionStatus, "", currentHighPrecisionOdometer,
                                                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                                                        "", obdEngineHours, global.GetCurrentDateTime(), "",
                                                        DriverId, dbHelper, driverPermissionMethod, obdUtil);

                                                obdUtil.syncObdSingleLog(getApplicationContext(), DriverId, getDriverName(), 10);



                                                SharedPref.saveEngSyncMalfunctionStatus(true, getApplicationContext());
                                                constants.saveMalfncnStatus(getApplicationContext(), true);
                                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());

                                                // save occurred event in malfunction/diagnostic table
                                                saveMalfunctionEventInTable(Constants.EngineSyncMalfunctionEvent,
                                                        getString(R.string.eng_sync_mal_occured),
                                                        global.GetCurrentUTCTimeFormat());  //SharedPref.getObdLastStatusUtcTime(getApplicationContext())


                                                // save malfunction entry in duration table
                                                malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                                                        global.GetCurrentUTCTimeFormat(),    //global.GetCurrentUTCTimeFormat(),
                                                        global.GetCurrentUTCTimeFormat(),
                                                        Constants.EngineSyncMalfunctionEvent, constants, getApplicationContext());

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

                                        }


                                    }




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

            if(SharedPref.isEngSyncDiagnstc(getApplicationContext())) {

                String EngineClearEventCallTime = SharedPref.getEngineClearEventCallTime(getApplicationContext());
                if(EngineClearEventCallTime.length() == 0){
                    EngineClearEventCallTime = global.getCurrentDate();
                    SharedPref.setEngineClearEventCallTime(EngineClearEventCallTime, getApplicationContext());
                }

                int callTimeMinDiff = constants.getMinDifference(EngineClearEventCallTime, global.getCurrentDate());

                // this check is used to avoid clear method calling after 3 sec each because checkEngSyncClearEvent is calling after 3 sec when data coming from obd
                if ( callTimeMinDiff >= 0) {
                    SharedPref.setEngineClearEventCallTime(global.getCurrentDate(), getApplicationContext());

                    boolean HasEngSyncEventForClear = malfunctionDiagnosticMethod.HasEngSyncEventForClear(Constants.EngineSyncDiagnosticEvent,
                            dbHelper, constants, global, getApplicationContext());
                    if (HasEngSyncEventForClear) {  //SharedPref.isEngSyncDiagnstc(getApplicationContext()) ||
                        ClearEventUpdate(DriverId, Constants.EngineSyncDiagnosticEvent,
                                "Auto clear engine sync diagnostic event", 0);


                        if (!SharedPref.isLocDiagnosticOccur(getApplicationContext()) && !SharedPref.isEngSyncDiagnstc(getApplicationContext())) {
                            SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getApplicationContext()),
                                    SharedPref.isMalfunctionOccur(getApplicationContext()),
                                    false,
                                    SharedPref.isSuggestedEditOccur(getApplicationContext()), getApplicationContext());
                        }


                        global.ShowLocalNotification(getApplicationContext(),
                                getString(R.string.event_cleared),
                                getString(R.string.eng_sync_dia_clear_desc), 2090);


                        malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());
                        updateMalDiaInfoWindowAtHomePage();

                        // update malfunction & diagnostic Fragment screen
                        updateMalDiaFragment(true);

                        SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
                        SharedPref.saveEngSyncMalfunctionStatus(SharedPref.isEngSyncMalfunction(getApplicationContext()), getApplicationContext());

                        //  constants.saveMalfncnStatus(getApplicationContext(), false);
                    }

                }




            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // check Power Data Compliance Malfunction/Diagnostic event
    private void checkPowerMalDiaEvent(){

        try{
            if(SharedPref.isPowerMalfunctionOccurred(getApplicationContext()) == false) {
                boolean isPowerCompMalAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerComplianceMal, getApplicationContext());
                boolean isPowerCompDiaAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerDataDiag, getApplicationContext());

                if (isPowerCompMalAllowed || isPowerCompDiaAllowed) {

                    String PowerEventStatus = constants.isPowerDiaMalOccurred(currentHighPrecisionOdometer, ignitionStatus,
                            obdEngineHours, DriverId, global, malfunctionDiagnosticMethod, isPowerCompMalAllowed, isPowerCompDiaAllowed,
                            getApplicationContext(), constants, dbHelper, driverPermissionMethod, obdUtil);

                    if (PowerEventStatus.length() > 0) {
                        if (PowerEventStatus.contains(constants.MalfunctionEvent)) {
                            if (isPowerCompMalAllowed) {

                            //    String occurredTime = constants.getPowerDiaMalOccurredTime(PowerEventStatus);


                                SharedPref.savePowerMalfunctionOccurStatus(true,
                                        SharedPref.isPowerDiagnosticOccurred(getApplicationContext()),
                                        global.getCurrentDate(), getApplicationContext()); //global.getCurrentDate()

                                // save occurred event in malfunction/diagnostic table
                                saveMalfunctionEventInTable(constants.PowerComplianceMalfunction,
                                        getString(R.string.power_comp_mal_occured),
                                        global.GetCurrentUTCTimeFormat() ); //occurredTime     SharedPref.GetTruckInfoOnIgnitionChange(Constants.IgnitionUtcTimeMalDia, getApplicationContext())

                                // save malfunction entry in duration table
                                malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                                        global.GetCurrentUTCTimeFormat() ,   //occurredTime     SharedPref.GetTruckInfoOnIgnitionChange(Constants.IgnitionUtcTimeMalDia, getApplicationContext()),
                                        global.GetCurrentUTCTimeFormat(),
                                        Constants.PowerComplianceMalfunction, constants, getApplicationContext());

                                // update mal/dia status for enable disable according to log
                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                                constants.saveMalfncnStatus(getApplicationContext(), true);
                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());
                            }
                        } else {
                            if (isPowerCompDiaAllowed) {
                             //  String occurredTime = constants.getPowerDiaMalOccurredTime(PowerEventStatus);

                                SharedPref.savePowerMalfunctionOccurStatus(
                                        SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                                        true, global.getCurrentDate(), getApplicationContext());

                                constants.saveDiagnstcStatus(getApplicationContext(), true);

                                // save occured event in malfunction/diagnostic table
                                saveMalfunctionEventInTable(constants.PowerComplianceDiagnostic, getString(R.string.power_dia_occured),
                                        Globally.GetCurrentUTCTimeFormat());  //SharedPref.GetTruckInfoOnIgnitionChange(Constants.IgnitionUtcTimeMalDia, getApplicationContext())


                                // save malfunction entry in duration table
                                malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                                        Globally.GetCurrentUTCTimeFormat(),   //SharedPref.GetTruckInfoOnIgnitionChange(Constants.IgnitionUtcTimeMalDia, getApplicationContext())
                                        global.GetCurrentUTCTimeFormat(),
                                        Constants.PowerComplianceDiagnostic, constants, getApplicationContext());

                                // update mal/dia status for enable disable according to log
                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());
                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());

                            }
                        }
                    }

                }
            }

            // check if Malfunction/Diagnostic event occurred in ECM disconnection
            if (SharedPref.isPowerDiagnosticOccurred(getApplicationContext())) {

                String PowerClearEventCallTime = SharedPref.getPowerClearEventCallTime(getApplicationContext());
                if(PowerClearEventCallTime.length() == 0){
                    PowerClearEventCallTime = global.getCurrentDate();
                    SharedPref.setPowerClearEventCallTime(PowerClearEventCallTime, getApplicationContext());
                }
                String dateee = SharedPref.getPowerMalOccTime(getApplicationContext());
                int minDiff = constants.getMinDifference(dateee, global.getCurrentDate());
                int callTimeMinDiff = constants.getMinDifference(PowerClearEventCallTime, global.getCurrentDate());

                // clear Power Diagnostic event after 5 min automatically when ECM is connected.
                if (minDiff > 5 && callTimeMinDiff >= 0) {  // callTimeMinDiff this check is used to avoid clear method calling after 3 sec each because checkPowerMalDiaEvent is calling after 3 sec when data coming from obd
                    SharedPref.setPowerClearEventCallTime(global.getCurrentDate(), getApplicationContext());
                    String eventOccuredDriverId = "";
                    if (!global.isSingleDriver(getApplicationContext())) {
                        eventOccuredDriverId = malfunctionDiagnosticMethod.getEventOccurredActualDriverid(dbHelper, Constants.PowerComplianceDiagnostic);
                        if(eventOccuredDriverId.length() == 0){
                            eventOccuredDriverId = DriverId;
                        }
                        ClearEventUpdate(eventOccuredDriverId, Constants.PowerComplianceDiagnostic,
                                "Auto clear Power data diagnostic event after 5 min of ECM data received", minDiff);
                    }else{
                        ClearEventUpdate(DriverId, Constants.PowerComplianceDiagnostic,
                                "Auto clear Power data diagnostic event after 5 min of ECM data received", minDiff);
                    }


                    global.ShowLocalNotification(getApplicationContext(),
                            getString(R.string.event_cleared),
                            getString(R.string.power_dia_clear_desc), 2093);


                    malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                    if (!SharedPref.isLocDiagnosticOccur(getApplicationContext()) && !SharedPref.isEngSyncDiagnstc(getApplicationContext())) {
                        SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getApplicationContext()),
                                SharedPref.isMalfunctionOccur(getApplicationContext()),
                                false,
                                SharedPref.isSuggestedEditOccur(getApplicationContext()), getApplicationContext());
                    }

                    SharedPref.savePowerMalfunctionOccurStatus(
                            SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                            false, global.getCurrentDate(), getApplicationContext());


                    updateMalDiaInfoWindowAtHomePage();
                    // update malfunction & diagnostic Fragment screen
                    updateMalDiaFragment(true);


                } else {
                    // update EndTime with TotalMinutes instantly but not cleared, because we are clearing it after 5 min
                  //  malfunctionDiagnosticMethod.updateTimeInPowerDiagnoseDia(dbHelper, getApplicationContext());
                }

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
                            if (Globally.LATITUDE.length() < 5 && SharedPref.getEcmObdLatitude(getApplicationContext()).length() > 4) {
                                SharedPref.setEcmObdLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE,
                                        currentHighPrecisionOdometer, global.GetCurrentDateTime(),
                                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
                            }


                            // check position malfunction event
                            String locMalDiaEvent = constants.isPositionMalfunctionEvent( DriverId, malfunctionDiagnosticMethod, dbHelper, getApplicationContext());
                            if (locMalDiaEvent.length() > 0) {
                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());
                                if (locMalDiaEvent.equals("M")) {   // && SharedPref.isLocMalfunctionOccur(getApplicationContext()) == false

                                    // save occurred event in malfunction/diagnostic table
                                    saveMalfunctionEventInTable(Constants.PositionComplianceMalfunction, getString(R.string.pos_mal_occured),
                                            global.GetCurrentUTCTimeFormat());  //SharedPref.getEcmObdUtcTime(getApplicationContext())

                                    // save malfunction entry in duration table
                                    malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                                            global.GetCurrentUTCTimeFormat(), global.GetCurrentUTCTimeFormat(),
                                            Constants.PositionComplianceMalfunction, constants, getApplicationContext());

                                    // update mal/dia status for enable disable according to log
                                    malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());

                                    SharedPref.saveLocMalfunctionOccurStatus(true, global.getCurrentDate(),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
                                    constants.saveMalfncnStatus(getApplicationContext(), true);

                                    Globally.PlayNotificationSound(getApplicationContext());
                                    global.ShowLocalNotification(getApplicationContext(),
                                            getString(R.string.malfunction_events),
                                            getString(R.string.pos_mal_event_desc), 2091);

                                    Globally.PlaySound(getApplicationContext());


                                    updateMalDiaInfoWindowAtHomePage();

                                    // update malfunction & diagnostic Fragment screen
                                    updateMalDiaFragment(true);

                                } else { //if(locMalDiaEvent.equals("D"))

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
                            malfunctionDiagnosticMethod.updateTimeOnLocationReceived(Constants.ConstLocationMissing, dbHelper);
                            SharedPref.saveLocDiagnosticStatus(false, "", "", getApplicationContext());
                        }

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void CheckEventsForClear(String EventCode, boolean isUpdate, int minDiff){
        try{
            int dayInMinDuration = 1440;
            if(SharedPref.isMalfunctionOccur(getApplicationContext()) || SharedPref.isDiagnosticOccur(getApplicationContext()) ){

                String clearEventLastCallTime = SharedPref.getClearEventCallTime(getApplicationContext());
                if (clearEventLastCallTime.length() == 0) {
                    SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());
                }

                // Checking after 1 min
                if (constants.minDiffMalfunction(clearEventLastCallTime, global, getApplicationContext()) > 0) {
                        // update call time
                    SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());

                    JSONArray clearEventArray = malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, DriverId, EventCode, true, isUpdate, getApplicationContext());
                    ClearEventType = EventCode;

                    /* We have 2 api for clear event. 1 for online events and 2nd is use here to clear in offline and input data as array.*/
                    if(clearEventArray.length() > 0){
                        // call clear event API.
                        if(global.isConnected(getApplicationContext() )) {
                            saveDriverLogPost.PostDriverLogData(clearEventArray, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT_BY_DATE,
                                    Constants.SocketTimeout30Sec, true, false, 0, ClearMalDiaEvent);
                        }else{
                            updateMalDiaInfoWindowAtHomePage();

                            // update malfunction & diagnostic Fragment screen
                            updateMalDiaFragment(true);
                        }
                    }else{
                        if(ClearEventType.equals(Constants.PowerComplianceDiagnostic)){

                            if(minDiff > dayInMinDuration) {
                                SharedPref.savePowerMalfunctionOccurStatus(
                                        false,
                                        false, global.getCurrentDate(), getApplicationContext());
                            }else{
                                SharedPref.savePowerMalfunctionOccurStatus(
                                        SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                                        false, global.getCurrentDate(), getApplicationContext());
                            }
                        }else if(ClearEventType.equals(Constants.EngineSyncDiagnosticEvent)){

                            SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
                            constants.saveDiagnstcStatus(getApplicationContext(), false);
                        }else {
                           // SharedPref.saveLocDiagnosticStatus(false, "", "", getApplicationContext());
                        }

                        updateMalDiaInfoWindowAtHomePage();

                        // update malfunction & diagnostic Fragment screen
                        updateMalDiaFragment(true);
                    }

                }



            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void EngSyncClearEventForSwitchedDriver(String DriverId, String EventCode){
        try{
            if(SharedPref.isDiagnosticOccur(getApplicationContext()) ){

                JSONArray clearEventArray = malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, DriverId, EventCode,
                                        true, true, getApplicationContext());
                ClearEventType = EventCode;

                /* We have 2 api for clear event. 1 for online events and 2nd is use here to clear in offline and input data as array.*/
                if(clearEventArray.length() > 0){
                    // call clear event API.
                    if(global.isConnected(getApplicationContext() )) {
                        saveDriverLogPost.PostDriverLogData(clearEventArray, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT_BY_DATE,
                                Constants.SocketTimeout30Sec, true, false, 0, ClearMalDiaEvent);
                    }
                }else{
                    /*if(ClearEventType.equals(Constants.PowerComplianceDiagnostic)){
                        SharedPref.savePowerMalfunctionOccurStatus(
                                SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                                false,  global.getCurrentDate(), getApplicationContext());
                    }else*/ if(ClearEventType.equals(Constants.EngineSyncDiagnosticEvent)){
                        SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
                        constants.saveDiagnstcStatus(getApplicationContext(), false);
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
            boolean isUnPostedEvent = malfunctionDiagnosticMethod.isUnPostedOfflineEvent( dataDiagnostic, dbHelper);

            if(isUnPostedEvent){
                // update clear event array in duration table and not posted to server with duration table input because occured event already exist TABLE_MALFUNCTION_DIANOSTIC
                malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, DriverId, dataDiagnostic, true,true, getApplicationContext());

                // update offline unposted event array
                JSONArray malArray = malfunctionDiagnosticMethod.updateOfflineUnPostedMalDiaEvent(DriverId, dataDiagnostic,
                                        clearDesc, dbHelper, getApplicationContext());

                // call api
                SaveMalfnDiagnstcLogToServer(malArray, DriverId);

            }else {
                CheckEventsForClear(dataDiagnostic, true, dayInMin);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // update clear event info in existing log if not uploaded on server yet
    private void ClearLastSwitchedEvent(String DriverId, String dataDiagnostic, String clearDesc){
        try{
            boolean isUnPostedEvent = malfunctionDiagnosticMethod.isUnPostedOfflineEvent(dataDiagnostic, dbHelper);

            if(isUnPostedEvent){
                // update clear event array in duration table and not posted to server with duration table input because occured event already exist TABLE_MALFUNCTION_DIANOSTIC
                malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, DriverId, dataDiagnostic, true,true, getApplicationContext());

                // update offline unposted event array
                JSONArray malArray = malfunctionDiagnosticMethod.updateOfflineUnPostedMalDiaEvent(DriverId, dataDiagnostic,
                        clearDesc, dbHelper, getApplicationContext());

                // call api
                SaveMalfnDiagnstcLogToServer(malArray, DriverId);

            }
              //  CheckEventsForClear(dataDiagnostic, true);

                EngSyncClearEventForSwitchedDriver(DriverId, dataDiagnostic);


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

    void saveLogWithRuleCall(String currentHighPrecisionOdometer, String currentLogDate, int speed, String vin, double intHighPrecisionOdometerInKm,
                             String timeStamp, String savedDate, String status, double calculatedSpeedFromOdo){

        try {

            if(!status.equals("not_saved")) {
                // save current HighPrecisionOdometer in DB
                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());

                if (speed > 200) {
                    speed = -1;
                }
            }

            callEldRuleForWired(speed, calculatedSpeedFromOdo);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    // Save Malfunction/Diagnostic Event Details
    void saveMalfunctionEventInTable(String malDiaType, String MalfunctionDefinition, String occurredTime){

        String clearedTime = "", clearTimeOdmeter = "", clearedTimeEngineHour = "";
        JSONObject newOccuredEventObj;
        String lastSavedOdometer = SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext());
        String lastSavedEngHr =  SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext());
        String currentOdometer = SharedPref.getObdOdometer(getApplicationContext());
        String currentEngHr = SharedPref.getObdEngineHours(getApplicationContext());

        // save malfunction/diagnostic occur event to server with few inputs
        if(malDiaType.equals(Constants.PowerComplianceDiagnostic) || malDiaType.equals(Constants.PowerComplianceMalfunction) ){
            newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                    DriverId, DeviceId, SharedPref.getVINNumber(getApplicationContext()),
                    DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
                    DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                    lastSavedEngHr,
                    lastSavedOdometer,
                    currentOdometer,
                    occurredTime, malDiaType, MalfunctionDefinition,
                    false, clearedTime,
                    currentOdometer,
                    currentEngHr
            );

        }else {

            newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                    DriverId, DeviceId, SharedPref.getVINNumber(getApplicationContext()),
                    DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
                    DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                    currentEngHr,
                    currentOdometer,
                    currentOdometer,
                    occurredTime, malDiaType, MalfunctionDefinition,
                    false, clearedTime, clearTimeOdmeter, clearedTimeEngineHour
            );

        }

        // save Occurred Mal/Dia events locally to get details later for clear them
     /*   JSONArray malArrayEvent = malfunctionDiagnosticMethod.getSavedMalDiagstcArrayEvents(Integer.parseInt(DriverId), dbHelper);
        malArrayEvent.put(newOccuredEventObj);
        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelperEvents(Integer.parseInt(DriverId), dbHelper, malArrayEvent);
*/

        // save Occurred event locally until not posted to server
        JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
        malArray.put(newOccuredEventObj);
        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper( dbHelper, malArray);

        // update malfunction & diagnostic Fragment screen
        updateMalDiaFragment(true);

        // call api
        SaveMalfnDiagnstcLogToServer(malArray, DriverId);


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
        String currentLogDate = global.GetCurrentDeviceDate();
        try {
            currentHighPrecisionOdometer = currentHighPrecisionOdometer.split("\\.")[0];

            if(!currentHighPrecisionOdometer.equals("--") && currentHighPrecisionOdometer.length() > 1) {
                String odometerInKm = constants.meterToKmWithObd(currentHighPrecisionOdometer);
                String odometerInMiles = constants.meterToMilesWith2DecPlaces(currentHighPrecisionOdometer);

                if (savedDate.length() > 0) {
                   // int dayDiff = constants.getDayDiff(savedDate, currentLogDate);
                    String savedOdo = SharedPref.getDayStartOdometerKm(getApplicationContext());
                    if (!global.GetCurrentDeviceDate().equals(savedDate) || savedOdo.equals("0")) {
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
        String currentDate = Globally.GetCurrentDateTime();

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



    void callEldRuleForWired( int speed, double calculatedSpeedFromOdo){
        // call cycle rule
        try {
            VehicleSpeed = speed;
            obdVehicleSpeed = (int) calculatedSpeedFromOdo;
            serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                    hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                    odometerhMethod, true, constants.WIRED_OBD, obdVehicleSpeed, GpsVehicleSpeed, obdUtil);
        }catch (Exception e){
            e.printStackTrace();
        }
        //   global.ShowLocalNotification(getApplicationContext(), "Wired OBD data", status + ", Speed " + VehicleSpeed, 2009);


    }









    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if(SharedPref.IsLocReceivedFromObd(getApplicationContext()) == false) {
                Globally.LATITUDE = "" + location.getLatitude();
                Globally.LONGITUDE = "" + location.getLongitude();
                Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(Globally.LONGITUDE);
                isGpsUpdate = true;

                //  GpsVehicleSpeed = (int) location.getSpeed() * 18 / 5;
                // GpsVehicleSpeed = 21;

                // saving location with time info to calculate location malfunction event
                //if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                    saveEcmLocationWithTime(Globally.LATITUDE, SharedPref.getHighPrecisionOdometer(getApplicationContext()));
                //}
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };


    private void disableBle(){
        try{
            BluetoothManager manager = BleUtil.getManager(this);
            if (manager != null) {
                mBTAdapter = manager.getAdapter();
            }

            if (mBTAdapter != null && mBTAdapter.isEnabled()) {
                mBTAdapter.disable();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "---------onStartCommand Service");


        String pingStatus = SharedPref.isPing(getApplicationContext());

        if(pingStatus.equals(ConstantsKeys.SaveOfflineData)){
            if (Constants.isDriverSwitchEvent) {
                Constants.isDriverSwitchEvent = false;
                if(SharedPref.isEngSyncDiagnstc(getApplicationContext())){

                    boolean IsLastSwitchedDriverEngSyncEvent = malfunctionDiagnosticMethod.IsLastSwitchedDriverEngSyncEvent(
                            Constants.lastDriverId, Constants.EngineSyncDiagnosticEvent, dbHelper);

                    if(IsLastSwitchedDriverEngSyncEvent) {

                        ClearLastSwitchedEvent(Constants.lastDriverId, Constants.EngineSyncDiagnosticEvent,
                                            "Auto clear engine sync diagnostic event on driver switching");

                       // malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(global, constants, dbHelper, getApplicationContext());

                        SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
                        SharedPref.saveEngSyncMalfunctionStatus(SharedPref.isEngSyncMalfunction(getApplicationContext()), getApplicationContext());
                    }


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Create Engine Sync data Malfunction/Diagnostic event for switched driver
                            checkEngineSyncMalDiaOccurredEvent(0, true);
                        }
                    }, 500);

                }
            }else if (Constants.isCallMalDiaEvent) {
                Constants.isCallMalDiaEvent = false;
                // update mal/dia status for enable disable according to log
                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, global, constants, dbHelper, getApplicationContext());


                if(isMalDiaAllowed() && Globally.isConnected(getApplicationContext())) {
                    GetMalDiaEventsDurationList();
                }
            }else if(Constants.isClearMissingCompEvent){
                Constants.isClearMissingCompEvent = false;
                ClearEventUpdate(DriverId, Constants.MissingDataDiagnostic,
                        "Clear positioning compliance diagnostic after entering manual location", 0);

            }else {
                driverLogArray = constants.GetDriversSavedArray(getApplicationContext(), MainDriverPref, CoDriverPref);
                if (driverLogArray.length() > 0) {
                    saveActiveDriverData();
                } else {
                    postAllOfflineSavedData(IsAutoSync);
                }
            }
        }else {
            if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
                if (ObdStatus != Constants.BLE_CONNECTED) {
                    SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(),
                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
                }

                boolean isConnected = HTBleSdk.Companion.getInstance().isConnected();   //HTModeSP.INSTANCE.getDeviceMac()

                if (EldFragment.IsTruckChange) {
                    EldFragment.IsTruckChange = false;

                    if (!isConnected) { //!mIsScanning &&

                        if(mIsScanning){
                            HTBleSdk.Companion.getInstance().stopHTBleScan();
                            mIsScanning = false;
                        }

                        checkPermissionsBeforeScanBle();

                    }
                } else {
                    if (pingStatus.equals("start")) {

                        if (!isConnected || ObdStatus != Constants.BLE_CONNECTED ) { //!mIsScanning &&

                            if(mIsScanning){
                                HTBleSdk.Companion.getInstance().stopHTBleScan();
                                mIsScanning = false;
                            }

                            HTBleSdk.Companion.getInstance().reBleConnect();

                        }else{
                            sendBroadCast("isConnected: " + isConnected + "\n ObdStatus: "+ObdStatus, "");

                            if (isConnected && ObdStatus != Constants.BLE_CONNECTED ) {

                                HTBleSdk.Companion.getInstance().disAllConnect();
                                mIsScanning = false;

                                HTBleSdk.Companion.getInstance().reBleConnect();

                                sendBroadCast("Scanning", "");

                            }
                        }


                    }
                }

            } else if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {

                disconnectBleObd();

                int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
                if (ObdStatus != Constants.WIRED_CONNECTED && ObdStatus != Constants.WIRED_ERROR) {
                    SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, global.getCurrentDate(),
                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
                }

                StartStopServer(constants.WiredOBD);
            } else {

                PingWithWifiObd(wifiConfig.IsAlsNetworkConnected(getApplicationContext()));
            }
        }

        UpdateDriverInfo();
        // getLocation(false);

        if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                !SharedPref.getPassword(getApplicationContext()).equals("")) {

            try {
                if(Constants.isEldHome) {
                    Constants.isEldHome = false;
                    JSONArray driverLogArray = constants.GetDriversSavedArray(getApplicationContext(),
                            MainDriverPref, CoDriverPref);
                    if (global.isWifiOrMobileDataEnabled(getApplicationContext()) && driverLogArray.length() == 0) {  // sharedPrefDriverLog.GetOfflineData(getApplicationContext()) == true) {  // This check is used to save offline saved data to server first then online status will be changed.
                        String VIN = SharedPref.getVINNumber(getApplicationContext());
                        UpdateOfflineDriverLog(DriverId, CoDriverId, DeviceId, VIN,
                                String.valueOf(GpsVehicleSpeed),
                                String.valueOf(obdVehicleSpeed),
                                constants.CheckGpsStatusToCheckMalfunction(getApplicationContext()));


                        if (SharedPref.GetNewLoginStatus(getApplicationContext())) {
                            JSONArray ctPatInsp18DaysArray = ctPatInspectionMethod.getCtPat18DaysInspectionArray(Integer.valueOf(DriverId), dbHelper);
                            if (ctPatInsp18DaysArray.length() == 0) {
                                String SelectedDate = global.GetCurrentDeviceDate();

                                if (SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)) {
                                    GetCtPatInspection18Days(DriverId, DeviceId, SelectedDate, GetCtPat18DaysMainDriverLog);
                                    GetCtPatInspection18Days(CoDriverId, DeviceId, SelectedDate, GetCtPat18DaysCoDriverLog);
                                } else {
                                    GetCtPatInspection18Days(DriverId, DeviceId, SelectedDate, GetCtPat18DaysMainDriverLog);
                                }

                            }
                        }

                    } else {
                        Globally.VEHICLE_SPEED = -1;
                        //VehicleSpeed = GpsVehicleSpeed;
                    }

                    Recap18DaysLog();
                }

            } catch (Exception e) {
            }


            SharedPref.SetPingStatus("", getApplicationContext());

        }


        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest(long time) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(time);
        locationRequest.setFastestInterval(time);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    private void requestLocationWithoutPlayServices(){
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_LOCATION_UPDATES,
                    10, locationListenerGPS);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                Globally.LATITUDE = "" + loc.getLatitude();
                Globally.LONGITUDE = "" + loc.getLongitude();
                Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(Globally.LONGITUDE);
            } else {
                Globally.LATITUDE = "";
                Globally.LONGITUDE = "";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            // Log.e(TAG, "-----Running timerTask");

            try {
                if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                        !SharedPref.getPassword(getApplicationContext()).equals("")) {

                    processStartTime = -1;
                    if (SpeedCounter == HalfSpeedCounter || SpeedCounter >= MaxSpeedCounter) {
                        processStartTime = System.currentTimeMillis();
                    }

                    // get ALS Wifi ssid availability
                    boolean isAlsNetworkConnected = wifiConfig.IsAlsNetworkConnected(getApplicationContext());
                    int ObdLastStatus = SharedPref.getObdStatus(getApplicationContext());

                    // communicate with wired OBD server if not connected
                    if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                        checkWiredObdConnection();
                        if (!isWiredObdRespond) {
                            StartStopServer(constants.WiredOBD);

                            if (HTBleSdk.Companion.getInstance().isConnected()) {   //HTModeSP.INSTANCE.getDeviceMac()
                              //  Log.d("getAddress", "getAddress: " +HTBleSdk.Companion.getInstance().getAddress());
                                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, HTBleSdk.Companion.getInstance().getAddress()));
                            }

                        } else {
                            String obdLastCallDate = SharedPref.getWiredObdCallTime(getApplicationContext());
                            if (obdLastCallDate.length() > 10) {
                                int lastCalledMinDiff = constants.getMinDifference(obdLastCallDate, Globally.GetCurrentDateTime());
                                if (lastCalledMinDiff > 1) {
                                    StartStopServer(constants.WiredOBD);
                                }
                            }
                        }
                    } else if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                        if (ObdLastStatus != Constants.BLE_CONNECTED) {
                            // call handler callback method to check malfunction/diagnostic when disconnected
                            StartStopServer(constants.WiredOBD);
                        }
                    }

                    try {
                        // request for location if lat long is null
                        if (Globally.LATITUDE.equals("0.0") || Globally.LATITUDE.equals("") || Globally.LATITUDE.equals("null")) {

                            // check availability of play services
                            if (global.checkPlayServices(getApplicationContext())) {
                                createLocationRequest(MIN_TIME_LOCATION_UPDATES);
                            } else {
                                requestLocationWithoutPlayServices();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // networkUsage();

                    final boolean isGpsEnabled = constants.CheckGpsStatusToCheckMalfunction(getApplicationContext());
                    if (!isGpsEnabled) {
                        if (Globally.LATITUDE.length() > 4) {
                            constants.saveObdData(getObdSource(), "Device GPS turned off. No location found. ", "", "",
                                    "", "", ignitionStatus, truckRPM, "" + VehicleSpeed,
                                    "", obdEngineHours, global.GetCurrentDateTime(), "",
                                    DriverId, dbHelper, driverPermissionMethod, obdUtil);
                        }

                        // GpsVehicleSpeed = -1;
                        Globally.LATITUDE = "";
                        Globally.LONGITUDE = "";
                    }


                    if (SpeedCounter == HalfSpeedCounter || SpeedCounter >= MaxSpeedCounter) {

                        if (!global.checkPlayServices(getApplicationContext()) && !isGpsUpdate) {
                            requestLocationWithoutPlayServices();
                        }

                        isStopService = false;
                        UpdateDriverInfo();

                        if (SpeedCounter >= MaxSpeedCounter) {
                            SpeedCounter = 0;
                            // Update UTC date time after 60 seconds
                            global.updateCurrentUtcTime(getApplicationContext());
                        } else {
                            SpeedCounter = SpeedCounter + LocRefreshTime;
                        }

                        try {

                            if (global.isWifiOrMobileDataEnabled(getApplicationContext())) {

                                if (constants.IsAlsServerResponding) {
                                    if (SpeedCounter == 0) {
                                        driverLogArray = constants.GetDriversSavedArray(getApplicationContext(),
                                                MainDriverPref, CoDriverPref);
                                        if (driverLogArray.length() == 0) {   // This check is used to save offline saved data to server first then online status will be changed.
                                            String VIN = SharedPref.getVINNumber(getApplicationContext());

                                            UpdateOfflineDriverLog(DriverId, CoDriverId, DeviceId, VIN,
                                                    String.valueOf(GpsVehicleSpeed),
                                                    String.valueOf(obdVehicleSpeed), isGpsEnabled);

                                        } else {
                                            // VehicleSpeed = GpsVehicleSpeed;
                                            Globally.VEHICLE_SPEED = -1;

                                            if (Constants.IS_ACTIVE_ELD == false) {
                                                saveActiveDriverData();
                                            }

                                        }
                                    }
                                } else {
                                    Globally.VEHICLE_SPEED = -1;
                                    // VehicleSpeed = GpsVehicleSpeed;
                                    updateOfflineApiRejectionCount++;
                                    if (updateOfflineApiRejectionCount > 2) {
                                        updateOfflineApiRejectionCount = 0;
                                        constants.IsAlsServerResponding = true;
                                    } else {
                                        if (updateOfflineApiRejectionCount > 1) {
                                            checkConnectivity.ConnectivityRequest(CheckInternetConnection, ConnectivityInterface);
                                        }
                                    }

                                    // if last status was online then save offline status
                               /* boolean connectionStatus = SharedPref.isOnline(getApplicationContext());
                                if(connectionStatus){
                                    constants.saveAppUsageLog(ConstantsEnum.StatusOffline,  false, false, obdUtil);
                                }*/
                                    SharedPref.setOnlineStatus(false, getApplicationContext());

                                }
                            } else {

                                constants.IsAlsServerResponding = true;
                                //  VehicleSpeed = GpsVehicleSpeed;

                                // if last status was online then save offline status
                           /* boolean connectionStatus = SharedPref.isOnline(getApplicationContext());
                            if(connectionStatus){
                                constants.saveAppUsageLog(ConstantsEnum.StatusOffline,  false, false, obdUtil);
                            }*/
                                SharedPref.setOnlineStatus(false, getApplicationContext());


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // call Eld cycle rules with priority wise. // depricated now
                        // callCycleRulesWithPriority(ObdLastStatus, isAlsNetworkConnected);

                        Recap18DaysLog();

                    } else {
                        SpeedCounter = SpeedCounter + LocRefreshTime;
                    }


                    // check WIFI connection
                    PingWithWifiObd(isAlsNetworkConnected);

                    // check positioning malfunction event ..
                    checkPositionMalfunction(currentHighPrecisionOdometer, global.GetCurrentDateTime());


                    if (ObdLastStatus == constants.WIRED_CONNECTED || ObdLastStatus == constants.BLE_CONNECTED ||
                            ObdLastStatus == constants.WIFI_CONNECTED) {

                        if (SpeedCounter == 0) {

                            if(getApplicationContext() != null) {
                                // Sync wired OBD saved log to server (SAVE sync data service)
                                obdUtil.syncObdLogData(getApplicationContext(), DriverId, getDriverName());

                                // Sync app usage log to server (SAVE sync data service)
                                obdUtil.syncAppUsageLog(getApplicationContext(), DriverId);
                            }

                            if (SharedPref.IsClearMalfunction(getApplicationContext())) {
                                String malEventCode = malfunctionDiagnosticMethod.clearMalAfter24Hours(dbHelper, getApplicationContext());
                                if (malEventCode.length() > 0) {
                                    String eventOccuredDriverId = "";
                                    if (!global.isSingleDriver(getApplicationContext())) {
                                        eventOccuredDriverId = malfunctionDiagnosticMethod.getEventOccurredActualDriverid(dbHelper, Constants.PowerComplianceDiagnostic);
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
                        }

                    }


                } else {
                    Log.e("Log", "--stop");
                    StopService();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    void PingWithWifiObd(boolean isAlsNetworkConnected){
        if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIFI) {

            //  ------------- BLE OBD ----------
            disconnectBleObd();

            if (isAlsNetworkConnected) {    // check ALS SSID connection with IsOBDPingAllowed permission
                if (IsOBDPingAllowed) {
                    tcpClient.sendMessage("123456,can");
                    SharedPref.SaveConnectionInfo(constants.WifiOBD, Globally.GetCurrentDeviceDate(), getApplicationContext());
                }
            } else {
                if (SharedPref.getObdStatus(getApplicationContext()) == Constants.WIFI_CONNECTED) {
                    sendEcmBroadcast(true);
                    Globally.PlayNotificationSound(getApplicationContext());
                    global.ShowLocalNotification(getApplicationContext(),
                            getString(R.string.wifi_obd),
                            getString(R.string.obd_device_disconnected), 2081);

                    SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED, global.getCurrentDate(),
                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
                    SharedPref.SaveConnectionInfo(constants.DataMalfunction, "", getApplicationContext());
                }
            }
        }
    }

    void saveActiveDriverData(){
        if(EldFragment.IsSaveOperationInProgress == false) {
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

                if (logArrayCount < 3) {
                    socketTimeout = constants.SocketTimeout10Sec;  //10 seconds
                } else if (logArrayCount < 10) {
                    socketTimeout = constants.SocketTimeout20Sec;  //20 seconds
                } else {
                    socketTimeout = constants.SocketTimeout40Sec;  //40 seconds
                }

                saveDriverLogPost.PostDriverLogData(driverLogArray, SavedLogApi, socketTimeout, false, false, DriverType, SaveMainDriverLogData);
            }
        }
    }


    // -------------- Reset OBD speed as default ----------------
    private void resetDataAfterCycleCall(boolean isCalled){

        CompareLocVal = "";
        VehicleSpeed = -1;
       // GpsVehicleSpeed = VehicleSpeed;
        obdVehicleSpeed = VehicleSpeed;
        SharedPref.setVss(VehicleSpeed, getApplicationContext());

        if(isCalled) {
            IsLogApiACalled = true;
            //  SetLocIntervalForBatterySaver();
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
            if(Slidingmenufunctions.usernameTV != null) {driverName = Slidingmenufunctions.usernameTV.getText().toString();}
        }catch (Exception e){
            e.printStackTrace();

            driverName = getDriverName();
            //  Log.d("driverName", "--driverName: " + driverName);
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
                }else{
                    // Exchange driver and co driver id when co driver is logged In.
                    CoDriverId      =  DriverConst.GetDriverDetails(DriverConst.DriverID, getApplicationContext());
                    DriverId        =  DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getApplicationContext());
                }

                SharedPref.setDriverId(DriverId, getApplicationContext());

            }

            if (SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
                DriverType      = Constants.MAIN_DRIVER_TYPE;
            }else{
                DriverType =Constants.CO_DRIVER_TYPE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        //   Log.d(ConstantsKeys.DriverId, "DriverId: " + DriverId);

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
        IsOBDPingAllowed    = SharedPref.isOBDPingAllowed(getApplicationContext());

        DateTime currentSavedDate;
        String currentSavedTime = SharedPref.getSystemSavedDate(getApplicationContext());
        if(currentSavedTime.length() > 10){
            currentSavedDate = new DateTime(global.getDateTimeObj(currentSavedTime, false));
            currentSavedDate = currentSavedDate.plusMinutes(1);
            SharedPref.setCurrentDate(currentSavedDate.toString(), getApplicationContext());
        }else{
            offsetFromUTC = (int) global.GetTimeZoneOffSet();
            if(offsetFromUTC == offSetFromServer) {
                currentSavedDate = new DateTime(global.getDateTimeObj(global.GetCurrentDateTime(), false)); //GetCurrentUTCTimeFormat
                SharedPref.setCurrentDate(currentSavedDate.toString(), getApplicationContext());
            }
        }

    }




    void Recap18DaysLog(){
        try {

            JSONArray recapArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DriverId), dbHelper);
            if(recapArray == null || recapArray.length() == 0) {
                if(!IsRecapApiACalled) {

                    if(SharedPref.getDriverType( getApplicationContext() ).equals(DriverConst.TeamDriver)){
                        String MainDriverId        =  DriverConst.GetDriverDetails(DriverConst.DriverID, getApplicationContext());
                        String CoDriverId      =  DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getApplicationContext());

                        GetRecapView18DaysData(MainDriverId, DeviceId, GetRecapViewFlagMain);
                        GetRecapView18DaysData(CoDriverId, DeviceId, GetRecapViewFlagCo);
                    }else{
                        GetRecapView18DaysData(DriverId, DeviceId, GetRecapViewFlagMain);
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"---onBind");
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onDestroy() {

        if(!isStopService) {

            Log.d("---onDestroy service_re", ConstantsEnum.StatusAppKilled );
            // save service status log

            Intent intent = new Intent(constants.packageName);
            intent.putExtra("location", "torestore");
            sendBroadcast(intent);
        }else{

            Log.d("---onDestroy service ", ConstantsEnum.StatusServiceStopped );

            try {
                StopLocationUpdates();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                //  ------------- BLE OBD ----------
                //disconnectBleObd();
               // HTBleSdk.Companion.getInstance().unRegisterCallBack();
            }else {
                //  ------------- Wired OBD ----------
                if(isBound){
                    StartStopServer("stop");
                    this.unbindService(connection);
                    isBound = false;
                }
                super.onDestroy();
            }

            super.onDestroy();



        }

        SharedPref.setServiceOnDestoryStatus(true, getApplicationContext());
        Log.i(TAG, "---------onDestroy Service method");


    }


    private void disconnectBleObd(){
        try {
            if (HTBleSdk.Companion.getInstance().isConnected()) {   //HTModeSP.INSTANCE.getDeviceMac()
             //   EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, HTBleSdk.Companion.getInstance().getAddress()));
                HTBleSdk.Companion.getInstance().disAllConnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    protected void StopLocationUpdates() {
        try {
            if (mGoogleApiClient.isConnected()) {
                stopForeground(true);
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
                mGoogleApiClient.disconnect();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }




    /*================== Get Unidentified Records ===================*/
    void GetMalDiaEventsDurationList(){

        String startEventDate = "";
    //    String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getApplicationContext());
      //  if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
            startEventDate = Globally.GetCurrentJodaDateTime().minusDays(15).toString();
        /*}else{
            startEventDate = Globally.GetCurrentJodaDateTime().minusDays(8).toString();
        }*/

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.UnitNo, Globally.TRUCK_NUMBER);
        params.put(ConstantsKeys.EventDateTime, startEventDate);

        GetMalfunctionEvents.executeRequest(Request.Method.POST, APIs.GET_MALFUNCTION_LIST_BY_TRUCK , params, GetMalDiaEventDuration,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }


    /* ================== Update Offline Driver Log =================== */
    void UpdateOfflineDriverLog(final String DriverId, final String CoDriverId, final String DeviceId, final String VIN,
                                String GpsSpeed, String obdSpeed, boolean isGpsEnabled ){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.CoDriverId, CoDriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.VIN, VIN );

        params.put(ConstantsKeys.GPSSpeed, GpsSpeed);
        params.put(ConstantsKeys.obdSpeed, obdSpeed);
        params.put(ConstantsKeys.isGpsEnabled, String.valueOf(isGpsEnabled) );

        UpdateUserStatusVolley.executeRequest(Request.Method.POST, APIs.UPDATE_OFF_LINE_DRIVER_LOG , params,
                UpdateOffLineStatus, Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);
    }




    //*================== Get Driver's 18 Days Recap View Log Data ===================*//*
    void GetRecapView18DaysData(final String DriverId, final String DeviceId , int GetRecapViewFlag){

        if(recapApiAttempts < 2) {
            IsRecapApiACalled = true;

            DateTime currentDateTime = new DateTime(global.GetCurrentDateTime());
            DateTime startDateTime = global.GetStartDate(currentDateTime, 15);
            String StartDate = global.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime));
            String EndDate = global.GetCurrentDeviceDate();  // current Date

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


    /*================== Upload Driver Updated Log Record Data ===================*/
    void SAVE_DRIVER_RECORD_LOG(final JSONArray geoData, final boolean isLoad, final boolean IsRecap, int socketTimeout){
        saveDriverLogPost.PostDriverLogData(geoData, APIs.UPDATE_DRIVER_LOG_RECORD, socketTimeout, isLoad, IsRecap, 1, SaveDriverLog);

    }




    void UploadSavedShipmentData(){

        try{
            shipmentArray = new JSONArray();
            shipmentArray = shipmentHelper.getSavedShipmentArray(Integer.valueOf(global.PROJECT_ID), dbHelper);
            if(shipmentArray.length() > 0 ){
                /*if (shipmentArray.length() == 1 ) {
                    if (!shipmentHelper.IsPosted(shipmentArray)){
                        postRequest.PostListingData(shipmentArray, APIs.SAVE_SHIPPING_DOC_NUMBER, SaveShippingOffline);
                    }
                }else{*/
                postRequest.PostListingData(shipmentArray, APIs.SAVE_SHIPPING_DOC_NUMBER, SaveShippingOffline);
                //  }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void UploadSavedOdometerData(){
        try{
            odometerArray = new JSONArray();
            odometerArray = odometerHelper.getSavedOdometerArray(Integer.valueOf(DriverId), dbHelper);

            if(odometerArray.length() > 0 ){
                SharedPref.SetOdoSavingStatus(true, getApplicationContext());
                postRequest.PostListingData(odometerArray, APIs.SAVE_ODOMETER_OFFLINE, SaveOdometerOffline );
            }

        }catch (Exception e){
            e.printStackTrace();
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



    /*public JSONArray GetDriversSavedArray(){
        int listSize = 0;
        JSONArray DriverJsonArray = new JSONArray();
        List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

        if(SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {   // Main Driver
            try {
                listSize = MainDriverPref.LoadSavedLoc(getApplicationContext()).size();
                tempList = MainDriverPref.LoadSavedLoc(getApplicationContext());
            } catch (Exception e) {
                listSize = 0;
            }
        }else{
            try {   // Co Driver
                listSize = CoDriverPref.LoadSavedLoc(getApplicationContext()).size();
                tempList = CoDriverPref.LoadSavedLoc(getApplicationContext());
            } catch (Exception e) {
                listSize = 0;
            }
        }

        try {
            if (listSize > 0) {
                for (int i = 0; i < tempList.size(); i++) {
                    EldDataModelNew listModel = tempList.get(i);
                    if(listModel != null) {
                        constants.SaveEldJsonToList(          *//* Put data as JSON to List *//*
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
        // Log.d("Arraay", "Arraay: " + DriverJsonArray.toString());
    }
*/


    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

    }



    void getLocation(boolean isSave){
        if(isSave) {
            if(Globally.LATITUDE.equals("0.0") || Globally.LATITUDE.equals("00.00")){
                createLocationRequest(MIN_TIME_LOCATION_UPDATES);
            }else {
                SaveLocation(Globally.LATITUDE, Globally.LONGITUDE);
            }
        }else{
            if(Globally.LATITUDE.equals("0.0") || Globally.LATITUDE.equals("00.00")){
                createLocationRequest(MIN_TIME_LOCATION_UPDATES);
            }
        }
    }



    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, (LocationListener) this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //  Log.d("onConnected", "onConnected");
        try {
            requestLocationUpdates();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("onConnectionFailed", "onConnectionFailed");
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
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

    // saving location with time info to calculate location malfunction event
    void saveEcmLocationWithTime(String latitude, String odo){

        String odometer = "0";
        if(odo.length() > 1){
            odometer = odo;
        }else{
            odometer = SharedPref.getHighPrecisionOdometer(getApplicationContext());
        }

        if(latitude.length() > 4 ){
            SharedPref.setEcmObdLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE, odometer,
                    global.GetCurrentDateTime(), global.GetCurrentUTCTimeFormat(), getApplicationContext());
        }else{
            if(SharedPref.getEcmObdLatitude(getApplicationContext()).length() > 4) {
                SharedPref.setEcmObdLocationWithTime("0", "0", odometer,
                        global.GetCurrentDateTime(), global.GetCurrentUTCTimeFormat(), getApplicationContext());
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        if(SharedPref.IsLocReceivedFromObd(getApplicationContext()) == false) {
            Globally.LATITUDE = "" + location.getLatitude();
            Globally.LONGITUDE = "" + location.getLongitude();
            Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(Globally.LONGITUDE);

            // GpsVehicleSpeed = (int) location.getSpeed() * 18 / 5;
            // GpsVehicleSpeed = 21;

           // if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                saveEcmLocationWithTime(Globally.LATITUDE, SharedPref.getHighPrecisionOdometer(getApplicationContext()));
          //  }
        }

    }


    CheckConnectivity.ConnectivityInterface ConnectivityInterface = new CheckConnectivity.ConnectivityInterface() {
        @Override
        public void IsConnected(boolean result, int flag) {
            Log.d("networkUtil", "result: " +result );

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
            Log.d("response", "OBD Response: " +message);
            obdVehicleSpeed = -1;

            // temp data
           //  message = "*TS01,861641040970625,002930140821,CAN:0B00F004517F7FB91D00F47F0B00FEC1B44D4103B44D41030B00FEF1FF9A3AFCFFFF00FF0B00FEF6FF095D3CFFFFFFFF0B00FEEE8160EB2EFFFF53FF0B00FEEF9DFFFF35FFFFFFFA0B00FEFCFF5EFFFFFFFFFFFF0B00FEF202019513D205FFFF0B00FEE007552100075521000B00FEBF1E3B7C7D8082FFFF0B00FD0900A9D904D20C85050B00FEE9FFFFFFFFBAD00200#";
         /*   if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_CONNECTED) {
                sendEcmBroadcast(false);
                global.ShowLocalNotification(getApplicationContext(),
                        getString(R.string.wifi_obd),
                        getString(R.string.obd_device_connected), 2081);
                SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, global.getCurrentDate(),
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


                                GPSSpeed = Integer.valueOf(wifiConfig.checkJsonParameter(canObj, "GPSSpeed", "0"));
                                WheelBasedVehicleSpeed = Double.parseDouble(wifiConfig.checkJsonParameter(canObj, "WheelBasedVehicleSpeed", "0"));

                                if (WheelBasedVehicleSpeed == -1) {
                                    WheelBasedVehicleSpeed = GPSSpeed;
                                }


                                truckRPM = wifiConfig.checkJsonParameter(canObj, "RPMEngineSpeed", "0");
                                ignitionStatus = wifiConfig.checkJsonParameter(canObj, "EngineRunning", "false");
                                obdTripDistance = wifiConfig.checkJsonParameter(canObj, "TripDistanceInKM", "0");
                                Globally.LATITUDE = wifiConfig.checkJsonParameter(canObj, "GPSLatitude", "");
                                Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(wifiConfig.checkJsonParameter(canObj, "GPSLongitude", ""));
                                HighResolutionDistance = wifiConfig.checkJsonParameter(canObj, "HighResolutionTotalVehicleDistanceInKM", "-1");
                                obdOdometer = HighResolutionDistance;
                                obdEngineHours = wifiConfig.checkJsonParameter(canObj, "EngineHours", "0");

                                String vin = wifiConfig.checkJsonParameter(canObj, ConstantsKeys.VIN, "");
                                if(vin.length() <= 5){
                                    vin = "";
                                }
                                SharedPref.setVehicleVin(vin, getApplicationContext());
                                SharedPref.SetObdEngineHours(obdEngineHours, getApplicationContext());
                                SharedPref.SetObdOdometer( Constants.meterToKm(HighResolutionDistance), getApplicationContext());
                                SharedPref.SetObdOdometerInMiles(Constants.meterToMilesWith2DecPlaces(currentHighPrecisionOdometer), getApplicationContext());

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

                                    // Check Engine Sync data Malfunction/Diagnostic event
                                 //   checkEngineSyncMalDiaOccurredEvent();

                                    // saving location with time info to calculate location malfunction event
                                    saveEcmLocationWithTime(Globally.LATITUDE, HighResolutionDistance);

                                    if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_CONNECTED) {
                                              sendBroadcastUpdateObd(false);
                                    }

                                    Globally.IS_OBD_IGNITION = true;
                                    continueStatusPromotForPcYm("ON", constants.WifiOBD, global.getCurrentDate(), Constants.WIFI_CONNECTED);
                                    SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, global.getCurrentDate(),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());


                                    // check Power Data Compliance Malfunction/Diagnostic event
                                    //checkPowerMalDiaEvent();


                                    if (WheelBasedVehicleSpeed > 200) {
                                        WheelBasedVehicleSpeed = 0;
                                    }

                                    obdVehicleSpeed = (int) WheelBasedVehicleSpeed;
                                    VehicleSpeed = obdVehicleSpeed;
                                    Globally.VEHICLE_SPEED = obdVehicleSpeed;

                             /*       // save updated values with truck ignition status
                                    SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, SharedPref.getObdSourceName(getApplicationContext()),
                                            global.getCurrentDate(), global.GetCurrentUTCTimeFormat(),
                                            obdEngineHours, HighResolutionDistance, getApplicationContext());

*/
                                    calculateWifiObdData("--", HighResolutionDistance, ignitionStatus, VehicleSpeed, obdTripDistance, rawResponse, correctData, true);

                                    sendBroadCast(parseObdDatainHtml(vin, (int)WheelBasedVehicleSpeed, -2), message);




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
                          //  Log.d("OBD", ObdRestarted);
                            saveDummyData(ObdRestarted, constants.WifiOBD);
                        } else {
                            ignitionStatus = "false";
                            saveDummyData(rawResponse, constants.WifiOBD);
                        }
                    }

                } else {

                    if (RestartObdFlag || message.contains("RST")) {
                        //Log.d("OBD", ObdRestarted);
                        saveDummyData(ObdRestarted, constants.WifiOBD);
                    } else {

                        if (!message.equals(noObd)) {
                            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIFI) {
                                if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_DISCONNECTED) {
                                    sendBroadcastUpdateObd(false);
                                 //   sendEcmBroadcast(true);
                                 //   Globally.PlayNotificationSound(getApplicationContext());
                                    SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED, global.getCurrentDate(),
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
        SharedPref.SetOBDRestartTime( global.getCurrentDate(), getApplicationContext());
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

        SharedPref.SetWifiObdOdometer(HighResolutionDistance, Globally.GetCurrentDateTime(), rawResponse.trim(), getApplicationContext());

    }


    private void calculateWifiObdData(String vin, String HighPrecisionOdometer, String ignition, int speed, String tripDistance,
                                 String rawResponse, String correctedData,  boolean isSave){

        int DrivingSpeedLimit   = DriverConst.getDriverConfiguredTime(DriverConst.DrivingSpeed, getApplicationContext());
        double speedCalculated = -1;
        String savedTime = SharedPref.GetWifiObdSavedTime(getApplicationContext());
        String currentLogDate = global.GetCurrentDateTime();

        if(rawResponse.contains("CAN")) {
            if(SharedPref.isOdoCalculationAllowed(getApplicationContext()) || speed > 220) {
                speedCalculated = calculateSpeedFromWifiObdOdometer(
                        savedTime,
                        SharedPref.GetWifiObdOdometer(getApplicationContext()),
                        HighPrecisionOdometer);
            }else{
                timeInSec = 6;
                speedCalculated = speed;
            }
        }

        if(speed >= 8){
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

                if (speed >= DrivingSpeedLimit || speedCalculated >= DrivingSpeedLimit ) {

                    if(speed == 0){
                        obdVehicleSpeed      = (int)speedCalculated;
                        VehicleSpeed         = obdVehicleSpeed;
                        Globally.VEHICLE_SPEED =  obdVehicleSpeed;
                    }

                    ServiceCycle.ContinueSpeedCounter = 0;
                    if (constants.minDiff(savedDate, global, getApplicationContext()) > 1) {  //&& !HighPrecisionOdometer.equals(SharedPref.getHighPrecisionOdometer(getApplicationContext()))
                        SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());
                        serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                                hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                                odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed, obdUtil);
                    }

                } else {

                    if (constants.minDiff(savedDate, global, getApplicationContext()) > 0) {
                        SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());
                        serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                                hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                                odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed, obdUtil);
                    }
                }

            } else if (jobType.equals(global.ON_DUTY)) {

                if (speed >= DrivingSpeedLimit || speedCalculated >= DrivingSpeedLimit ) {
                    if(speed == 0){
                        obdVehicleSpeed      = (int)speedCalculated;
                        VehicleSpeed         = obdVehicleSpeed;
                        Globally.VEHICLE_SPEED =  obdVehicleSpeed;
                    }

                    SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer,currentLogDate, getApplicationContext());
                    serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                            hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                            odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed, obdUtil);

                } else {

                    ServiceCycle.ContinueSpeedCounter = 0;
                    if (constants.minDiff(savedDate, global, getApplicationContext()) > 1) {
                        SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());
                        serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                                hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                                odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed, obdUtil);
                    }
                }

            } else {

                SharedPref.saveHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());

                if (speedCalculated >= DrivingSpeedLimit && speed == 0 ) {
                    obdVehicleSpeed = (int) speedCalculated;
                    VehicleSpeed = obdVehicleSpeed;
                    Globally.VEHICLE_SPEED = obdVehicleSpeed;
                    speed = obdVehicleSpeed;
                }


                if (speed <= DrivingSpeedLimit || speedCalculated <= DrivingSpeedLimit ) {
                   // Log.d("ELD Rule", "Rule is correct.");
                    ServiceCycle.ContinueSpeedCounter = 0;
                } else {
                    serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                            hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                            odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed, obdUtil);
                }

            }
        }else{
            if(timeInSec >= 5) {
                ServiceCycle.ContinueSpeedCounter = 0;
            }
        }

        SharedPref.setVss(VehicleSpeed, getApplicationContext());
        SharedPref.setRPM(truckRPM, getApplicationContext());

        resetDataAfterCycleCall(true);
    }


    private class UploadLocFile extends AsyncTask<String, String, String> {

        String strResponse = "";
        Response response;

        @Override
        protected String doInBackground(String... params) {

            try {

                com.squareup.okhttp.Request request = null;

                /* ===================  CROSS CHECK ONCE FOR LOAD_ID AND JOB_ID ================== */
                MultipartBuilder builderNew = new MultipartBuilder().type(MultipartBuilder.FORM);
                // .addFormDataPart(ConstantsKeys.DriverId, DriverId ) ;

                if (locDataFile != null && locDataFile.exists()) {
                    Log.i("", "---Add File: " + locDataFile.toString());
                    builderNew.addFormDataPart("myFile", "file",
                            RequestBody.create(MediaType.parse("application/txt"), new File(locDataFile.toString())));
                }

                RequestBody requestBody = builderNew.build();
                request = new com.squareup.okhttp.Request.Builder()
                        .url(APIs.SAVE_LAT_LONG)
                        .post(requestBody)
                        .build();


                OkHttpClient client = new OkHttpClient();
                client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
                client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
                client.setReadTimeout(30, TimeUnit.SECONDS);
                response = client.newCall(request).execute();
                strResponse = response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("String Response", ">>>>Location Response   " + result);

            try {

                if(locDataFile.exists())
                    locDataFile.delete();

                JSONObject obj = new JSONObject(result);
                String status = obj.getString("Status");

                if(status.equalsIgnoreCase("true")) {
                    LocMethod.DriverLocationHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



    void SaveLocation(String lat, String lon){

        try {
            if (lat.length() > 3 && lon.length() > 3) { // minimum format required 00.

                try {
                    String UTCDate = global.GetCurrentUTCTime();
                    // Saved json in location array
                    JSONObject newObj = LocMethod.LocationObject(DriverId, lat, lon, VIN_NUMBER, VehicleId , UTCDate, GpsVehicleSpeed );
                    JSONArray savedLocArray = LocMethod.getSavedLocationArray(Integer.valueOf(DriverId), dbHelper);

                    savedLocArray.put(newObj);

                    if(savedLocArray.length() > 1) {

                        try {
                            JSONObject originJson = (JSONObject) savedLocArray.get(savedLocArray.length() - 2);
                            String lastItemLat = originJson.getString("Latitude");
                            String lastItemLon = originJson.getString("Longitude");

                            if(!lat.equals(lastItemLat) || !lon.equals(lastItemLon)){   // speed > 1   because data accuracy difference is 10-20 meters. So speed value may be increased.
                                LocMethod.DriverLocationHelper(Integer.valueOf(DriverId), dbHelper, savedLocArray);
                            }else{
                                if(sameLocationCount > 1) {
                                    StopLocationUpdates();

                                    if (global.checkPlayServices(getApplicationContext())) {
                                        // Building the GoogleApi client
                                        buildGoogleApiClient();
                                    }
                                    createLocationRequest(MIN_TIME_LOCATION_UPDATES);
                                    sameLocationCount = 0;
                                }
                                sameLocationCount++;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }else{
                        LocMethod.DriverLocationHelper(Integer.valueOf(DriverId), dbHelper, savedLocArray);
                    }

                    if (global.isConnected(getApplicationContext()) && savedLocArray.length() >= 20) {
                        locDataFile = global.SaveFileInSDCard("Loc_", savedLocArray.toString(), false, getApplicationContext());
                        //  Log.d("savedLocArray", "--savedLocArray: " + savedLocArray);
                        new UploadLocFile().execute();

                    }

                    if(savedLocArray.length() > 5){
                        constants.IS_ELD_ON_CREATE = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONArray latLongArray  = latLongHelper.getSavedLatLonArray(dbHelper);
                JSONObject obj = latLongHelper.getLatLonJson(latitude, longitude);
                latLongArray.put(obj);
                latLongHelper.LatLongHelper(dbHelper, latLongArray);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void SaveDriverRecordLogUpdates(){
        try {
            if (DriverId.length() > 0) {
                JSONArray recordsLogArray = logRecordMethod.getSavedLogRecordArray(Integer.valueOf(DriverId), dbHelper);
                if (recordsLogArray.length() > 0) {
                    SAVE_DRIVER_RECORD_LOG(recordsLogArray, false, false, Constants.SocketTimeout30Sec);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void SaveCertifyLog(){
        try{
            if(DriverId.length() > 0) {
                JSONArray certifyLogArray = certifyLogMethod.getSavedCertifyLogArray(Integer.valueOf(DriverId), dbHelper);
                if (certifyLogArray.length() > 0) {
                    saveDriverLogPost.PostDriverLogData(certifyLogArray, APIs.CERTIFY_LOG_OFFLINE, Constants.SocketTimeout30Sec, false, false, 1, SaveCertifyLog);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void SaveMalfnDiagnstcLogToServer(JSONArray malArray1, String DriverId){
        try{
            if(DriverId.length() > 0) {
                if(malArray1 == null) {
                    malArray1 = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
                }
                if (global.isConnected(getApplicationContext()) && malArray1.length() > 0 && isMalfncDataAlreadyPosting == false) {
                    isMalfncDataAlreadyPosting = true;
                    saveDriverLogPost.PostDriverLogData(malArray1, APIs.MALFUNCTION_DIAGNOSTIC_EVENT, Constants.SocketTimeout30Sec,
                            false, false, 1, SaveMalDiagnstcEvent);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    //*================== Get Odometer 18 Days data ===================*//*
    void SaveDriverDeviceUsageLog(final String savedDate){
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


    }



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




    private void continueStatusPromotForPcYm(String obdCurrentIgnition, String type, String time, int obdStatus){

        try {

            //   if( obdStatus == Constants.WIFI_CONNECTED && obdStatus == Constants.WIRED_CONNECTED ) {
            if( obdStatus != Constants.NO_CONNECTION && SharedPref.GetNewLoginStatus(getApplicationContext()) == false) {
                try {
                  //  if (UILApplication.isActivityVisible()) {
                        if (EldFragment.driverLogArray == null || EldFragment.driverLogArray.length() == 0) {
                            EldFragment.driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
                        }
                        if (EldFragment.driverLogArray.length() > 0) {  //UILApplication.isActivityVisible() &&
                            JSONObject lastJsonItem =  hMethods.GetLastJsonFromArray(EldFragment.driverLogArray);
                            int currentJobStatus = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
                            boolean isYard = lastJsonItem.getBoolean(ConstantsKeys.YardMove);
                            boolean isPersonal = lastJsonItem.getBoolean(ConstantsKeys.Personal);

                            if ((currentJobStatus == constants.OFF_DUTY && isPersonal) || (currentJobStatus == constants.ON_DUTY && isYard)) {
                                String lastIgnitionStatus = SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getApplicationContext());
                                if (!lastIgnitionStatus.equals("ON") && obdCurrentIgnition.equals("ON")) {
                                    SharedPref.SetTruckStartLoginStatus(true, getApplicationContext());

                                    if(TabAct.host.getCurrentTab() == 0) {
                                        Intent intent = new Intent(ConstantsKeys.IsIgnitionOn);
                                        intent.putExtra(ConstantsKeys.IsIgnitionOn, true);
                                        LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
                                    }else{
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
                                }


                                SharedPref.SetTruckIgnitionStatusForContinue(obdCurrentIgnition, type, time, getApplicationContext());
                            }
                        }
                   // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //
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

                if (defMainDriverArray.length() > 0) {
                    saveDriverLogPost.PostDriverLogData(defMainDriverArray, APIs.SAVE_DEFFERAL_EVENT, Constants.SocketTimeout10Sec, true, false,
                            Integer.valueOf(DriverId), SaveDeferralMain);
                }


                if (defCoDriverArray.length() > 0) {
                    saveDriverLogPost.PostDriverLogData(defCoDriverArray, APIs.SAVE_DEFFERAL_EVENT, Constants.SocketTimeout10Sec, true, false,
                            Integer.valueOf(CoDriverId), SaveDeferralCo);
                }

            }else{
                defMainDriverArray = deferralMethod.getSavedDeferralArray(Integer.valueOf(DriverId), dbHelper);
                if(defMainDriverArray.length() > 0){
                    saveDriverLogPost.PostDriverLogData(defMainDriverArray, APIs.SAVE_DEFFERAL_EVENT, Constants.SocketTimeout10Sec, true, false,
                            Integer.valueOf(DriverId), SaveDeferralMain);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void SyncData(boolean IsAutoSync){

        try {

            savedSyncedArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DriverId), dbHelper);
           // Log.d("savedSyncedArray", "savedSyncedArray: " + savedSyncedArray);
            if(savedSyncedArray.length() > 0) {
                syncingFile = global.SaveFileInSDCard("Sync_", savedSyncedArray.toString(), false, getApplicationContext());
            }

            ViolationFile = global.GetSavedFile(getApplicationContext(),ConstantsKeys.ViolationTest, "txt");

            if(IsAutoSync){
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


    void updateClearEvent(boolean isRefreshFromServer){
        String notificationDesc = "";
        int notificationId = -1;
        malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, DriverId, ClearEventType, true, true, getApplicationContext());

        if(ClearEventType.equals(Constants.MissingDataDiagnostic)){
            // SharedPref.saveLocDiagnosticStatus(false, "", "",getApplicationContext());
            notificationDesc = getString(R.string.missing_data_dia_clear_desc);
            notificationId = 2091;
        }else if(ClearEventType.equals(Constants.PowerComplianceDiagnostic)){
            SharedPref.savePowerMalfunctionOccurStatus(
                    SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                    false,  global.getCurrentDate(), getApplicationContext());

            notificationDesc =  getString(R.string.power_dia_clear_desc);
            notificationId = 2093;

        }else if(ClearEventType.equals(Constants.EngineSyncDiagnosticEvent)){
            SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());

            notificationDesc =  getString(R.string.eng_sync_dia_clear_desc);
            notificationId = 2090;

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

        }

        global.ShowLocalNotification(getApplicationContext(), getString(R.string.event_cleared),  notificationDesc, notificationId);

        if(isRefreshFromServer) {
            GetMalDiaEventsDurationList();
        }

        ClearEventType = "";
        updateMalDiaInfoWindowAtHomePage();

        // update malfunction & diagnostic Fragment screen
        updateMalDiaFragment(false);

    }

    void postAllOfflineSavedData(boolean IsAutoSync){
        try{
            // -------- upload offline locally saved data ---------
            UploadSavedShipmentData();
            SaveCertifyLog();
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
            SyncData(IsAutoSync);

            checkDeferralData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }






    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @Override
        public void getResponse(String response, int flag) {
            //  Log.d("response", "Service response-" + flag + ": " + response);

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

                    case GetMalDiaEventDuration:
                        Log.d("response", "GetMalDiaEventDuration: " + flag + ": " + response);
                        Constants.isCallMalDiaEvent = false;
                        try {

                            String MainDriverId        =  DriverConst.GetDriverDetails(DriverConst.DriverID, getApplicationContext());
                            String CoDriverId      =  DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getApplicationContext());
                            JSONArray durationArray = new JSONArray();
                            JSONArray dataArray = new JSONArray(obj.getString("Data"));
                            for(int i = dataArray.length()-1 ; i >= 0 ; i--){
                                JSONObject objItem = (JSONObject) dataArray.get(i);

                                String EngineHours = "",StartOdometer = "";
                                if(!objItem.isNull(ConstantsKeys.EngineHours)){
                                    EngineHours = objItem.getString(ConstantsKeys.EngineHours);
                                }

                                if(!objItem.isNull(ConstantsKeys.StartOdometer)){
                                    StartOdometer = objItem.getString(ConstantsKeys.StartOdometer);
                                }

                                String DetectionDataEventCode = objItem.getString(ConstantsKeys.DetectionDataEventCode);
                                String DriverId = objItem.getString(ConstantsKeys.DriverId);
                                if(DriverId.equals(MainDriverId) || DriverId.equals(CoDriverId) ||
                                        DetectionDataEventCode.equals(Constants.UnIdentifiedDrivingDiagnostic)) {

                                    JSONObject item = malfunctionDiagnosticMethod.getNewMalDiaDurationObj(
                                            DriverId,
                                            objItem.getString(ConstantsKeys.EventDateTime),
                                            objItem.getString(ConstantsKeys.EventEndDateTime),
                                            DetectionDataEventCode,
                                            objItem.getInt(ConstantsKeys.TotalMinutes),
                                            objItem.getBoolean(ConstantsKeys.IsClearEvent),
                                            EngineHours,
                                            "",
                                            StartOdometer,
                                            EngineHours);

                                    durationArray.put(item);
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
                        Log.d("SaveMalDiagnstcEvent", "SaveMalDiagnstcEvent saved successfully");
                        isMalfncDataAlreadyPosting = false;
                        // clear malfunction array
                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());

                        // update malfunction & diagnostic Fragment screen
                        updateMalDiaFragment(false);

                        break;


                    case UpdateOffLineStatus:


                        //   Log.d("response", "UpdateOffLine: " + flag + ": " + response);
                        try {

                            updateOfflineNoResponseCount = 0;
                            updateOfflineApiRejectionCount = 0;
                            constants.IsAlsServerResponding = true;

                            apiReturnedSpeed = "--";

                            if (status.equalsIgnoreCase("false")) {

                                if (message.equalsIgnoreCase("Device Logout")) {
                                    DeviceLogout(message);
                                } else {

                                   // VehicleSpeed = GpsVehicleSpeed;

                                }
                            } else {

                                // -------- Save date time Locally -------------
                                try {
                                    JSONObject dataObj = new JSONObject(obj.getString("Data"));
                                    String UtcCurrentDate = dataObj.getString("UTCDateTime");
                                    String OBDUTCDate = dataObj.getString("OBDUTCDateTime");
                                    String CarrierName = dataObj.getString("CarrierName");
                                    DateTime utcCurrentDateTime = global.getDateTimeObj(UtcCurrentDate, false);
                                    DateTime ObdUtcDateTime = global.getDateTimeObj(OBDUTCDate, false);


                                    boolean IsAOBRD = dataObj.getBoolean("IsAOBRD");
                                    boolean IsAutoDrive = dataObj.getBoolean("IsAutoOnDutyDriveEnabled");
                                    IsOBDPingAllowed = dataObj.getBoolean("IsOBDPingAllowed");
                                    IsAutoSync = dataObj.getBoolean("IsAutoSync");



                                    try {
                                        // Save Truck information for manual/auto mode
                                        SharedPref.SetIsAOBRD(IsAOBRD, getApplicationContext());
                                        SharedPref.SetAOBRDAutomatic(dataObj.getBoolean(ConstantsKeys.IsAOBRDAutomatic), getApplicationContext());
                                        SharedPref.SetAOBRDAutoDrive(dataObj.getBoolean(ConstantsKeys.IsAutoDriving), getApplicationContext());
                                        SharedPref.SetDrivingShippingAllowed(dataObj.getBoolean(ConstantsKeys.IsDrivingShippingAllowed), getApplicationContext());
                                        SharedPref.saveTimeStampView(dataObj.getBoolean(ConstantsKeys.IsTimestampEnabled), getApplicationContext());
                                        SharedPref.setCurrentUTCTime(UtcCurrentDate, getApplicationContext());
                                        SharedPref.SetOBDPingAllowedStatus(IsOBDPingAllowed, getApplicationContext());
                                        SharedPref.SetAutoDriveStatus(IsAutoDrive, getApplicationContext());

                                        // check PU status exceeding status if 75km exceeded showing popup
                                        checkPUExceedStatus(dataObj);

                                        boolean isSuggestedEdit = dataObj.getBoolean(ConstantsKeys.SuggestedEdit);
                                        boolean isSuggestedRecall;
                                        boolean IsCycleRequest      =  dataObj.getBoolean(ConstantsKeys.IsCycleRequest);
                                        boolean IsELDNotification = false;
                                        String ELDNotification    = dataObj.getString("DriverELDNotificationList");

                                        try{
                                            JSONArray eldNotArray = new JSONArray(ELDNotification);
                                            if(eldNotArray.length() > 0){
                                                IsELDNotification = true;
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                        SharedPref.SetELDNotification(IsELDNotification, getApplicationContext());
                                        SharedPref.SetNorthCanadaStatus(dataObj.getBoolean(ConstantsKeys.IsNorthCanada), getApplicationContext());

                                        if(DriverType == Constants.MAIN_DRIVER_TYPE) {

                                            SharedPref.setEldOccurences(dataObj.getBoolean(ConstantsKeys.IsUnidentified),
                                                    SharedPref.isMalfunctionOccur(getApplicationContext()),
                                                    SharedPref.isDiagnosticOccur(getApplicationContext()),
                                                    isSuggestedEdit, getApplicationContext());


                                            isSuggestedRecall = SharedPref.isSuggestedRecall(getApplicationContext());

                                            SharedPref.SetExemptDriverStatusMain(dataObj.getBoolean(ConstantsKeys.IsExemptDriver), getApplicationContext());
                                            SharedPref.SetCycleRequestStatusMain(IsCycleRequest, getApplicationContext());


                                        }else{
                                            SharedPref.setEldOccurencesCo(dataObj.getBoolean(ConstantsKeys.IsUnidentified),
                                                    SharedPref.isMalfunctionOccurCo(getApplicationContext()) ,
                                                    SharedPref.isDiagnosticOccurCo(getApplicationContext()),
                                                    isSuggestedEdit, getApplicationContext());

                                            isSuggestedRecall = SharedPref.isSuggestedRecallCo(getApplicationContext());

                                            SharedPref.SetExemptDriverStatusCo(dataObj.getBoolean(ConstantsKeys.IsExemptDriver), getApplicationContext());
                                            SharedPref.SetCycleRequestStatusCo(IsCycleRequest, getApplicationContext());

                                        }


                                        if ( (isSuggestedEdit && isSuggestedRecall) || IsCycleRequest || IsELDNotification) {
                                            try {
                                                //if(UILApplication.isActivityVisible()) {
                                                    Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                                                    intent.putExtra(ConstantsKeys.SuggestedEdit, isSuggestedEdit);
                                                    intent.putExtra(ConstantsKeys.IsCycleRequest, IsCycleRequest);
                                                    intent.putExtra(ConstantsKeys.IsELDNotification, IsELDNotification);
                                                    intent.putExtra(ConstantsKeys.DriverELDNotificationList, ELDNotification);
                                                    LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
                                               // }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    SharedPref.setOnlineStatus(true, getApplicationContext());

                                    // Sync app usage log to server (SAVE sync data service)
                                    obdUtil.syncAppUsageLog(getApplicationContext(), DriverId);



                                    if (SharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)) {
                                        if (SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
                                            // Current active driver is Main Driver.
                                            DriverConst.UpdateDriverCarrierName(CarrierName, getApplicationContext());
                                        } else {
                                            // Current active driver is Co Driver.
                                            DriverConst.UpdateCoDriverCarrierName(CarrierName, getApplicationContext());
                                        }
                                    } else {
                                        DriverConst.UpdateDriverCarrierName(CarrierName, getApplicationContext());
                                    }

                                    if (dataObj.has("IsOdometerFromOBD"))
                                        SharedPref.SetOdometerFromOBD(dataObj.getBoolean("IsOdometerFromOBD"), getApplicationContext());

                                    int CycleId = dataObj.getInt("CycleId");
                                    int VehicleSpeedInterval = dataObj.getInt("VehicleSpeedInterval");
                                    int MinDiff = utcCurrentDateTime.getMinuteOfDay() - ObdUtcDateTime.getMinuteOfDay();

                                    if (MinDiff > VehicleSpeedInterval) { //&& MinDiff <= VehicleSpeedInterval
                                        IsAlertTimeValid = true;
                                    } else {
                                        IsAlertTimeValid = false;
                                    }

                                    //obdVehicleSpeed = dataObj.getInt("VehicleSpeed");
                                    apiReturnedSpeed = dataObj.getString("VehicleSpeed");

                                   // Globally.VEHICLE_SPEED = obdVehicleSpeed;

                                    if (obdVehicleSpeed != -1) {
                                        VehicleSpeed = obdVehicleSpeed;
                                    }

                                    //    global.ShowLocalNotification(getApplicationContext(), "API obd Speed", "API obd Speed: " + obdVehicleSpeed, 2009);

                                    // Save Driver Cycle With Current Date
                                    constants.SaveCycleWithCurrentDate(CycleId, utcCurrentDateTime.toString(), "UpdateOfflineDriverLog_api",
                                            global, getApplicationContext());
                                } catch (Exception e) {
                                   // VehicleSpeed = GpsVehicleSpeed;
                                    Globally.VEHICLE_SPEED = -1;
                                    e.printStackTrace();
                                }

                                postAllOfflineSavedData(IsAutoSync);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        SaveDriverRecordLogUpdates();

                        break;


                    case GetRecapViewFlagMain:
                    case GetRecapViewFlagCo:

                        IsRecapApiACalled = false;

                        try {
                            if (!obj.isNull("Data")) {
                                JSONArray recapArray = recapViewMethod.ParseServerResponseOfArray(obj.getJSONArray("Data"));

                                if (flag == GetRecapViewFlagMain) {
                                    recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DriverId), dbHelper, recapArray);
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


                        //  Log.d("response", "Service response GetRecapViewData-" + flag + ": " + response);
                        break;


                    case GetOdometers18Days:

                        try {
                            if (!obj.isNull("Data")) {
                                JSONArray resultArray = new JSONArray(obj.getString("Data"));
                                odometerhMethod.Odometer18DaysHelper(Integer.valueOf(DriverId), dbHelper, resultArray);
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

                    case SaveDriverDeviceUsageLog:
                        try {
                            SharedPref.setLastUsageDataSavedTime(global.getCurrentDate(), getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;


                }
            }else{

            }
        }
    };





    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            //  Log.d("error", "error-" +flag + " : " + error);
            switch (flag) {

                case GetRecapViewFlagMain:
                case GetRecapViewFlagCo:

                    IsRecapApiACalled = false;

                     /*   if (recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DriverId), dbHelper).length() == 0) {
                           Recap18DaysLog();
                        }
                    */


                    break;

                case UpdateOffLineStatus:

                    if(updateOfflineNoResponseCount > 0) {
                        constants.IsAlsServerResponding = false;
                    }

                    updateOfflineNoResponseCount++;
                  //  VehicleSpeed = GpsVehicleSpeed;
                    apiReturnedSpeed = "--";
                    Globally.VEHICLE_SPEED = -1;

                    break;


            }

        }
    };




    AsyncResponse asyncResponse = new AsyncResponse() {
        @Override
        public void onAsyncResponse(String response, String DriverId) {

            Log.e("String Response", ">>>Sync Response:  " + response);

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

                    Log.d("Sync", msgTxt );

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
                            //  JSONObject obj = shipmentHelper.GetLastJsonObject(shipmentArray, shipmentArray.length()-1);
                            //   JSONArray shipmentJsonArray = new JSONArray();
                            //   shipmentJsonArray.put(obj);
                            shipmentHelper.ShipmentHelper(Integer.valueOf(global.PROJECT_ID), dbHelper, new JSONArray());

                            break;


                        case SaveOdometerOffline:
                            SharedPref.SetOdoSavingStatus(false, getApplicationContext());
                            odometerHelper.OdometerHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                            String CompanyId = "", SelectedDate = "";
                            SelectedDate = Globally.GetCurrentDeviceDate();
                            DriverId   = SharedPref.getDriverId(getApplicationContext());
                            if(SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
                                CompanyId     = DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext());
                            } else {                                                                                 // If Current driver is Co Driver
                                CompanyId     = DriverConst.GetCoDriverDetails(DriverConst.CoCompanyId, getApplicationContext());
                            }

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
            Log.d("shipmentJsonArray ", ">>>Error");
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
                        e.printStackTrace();
                    }
                }

            }else {
                if(msg.equals(Constants.AobrdWarning)){
                    global.ShowNotificationWithSound(getApplicationContext(), "AOBRD", "Your current status is "+ status +" but your vehicle is running.", mNotificationManager);

                    // Play again
                    //playSound();

                }else if(msg.equals(Constants.AobrdAutomatic)) {
                    //  global.ShowNotificationWithSound(getApplicationContext(), "AOBRD", status, mNotificationManager);
                }else if(msg.contains(getString(R.string.als_alert))){
                    global.ShowNotificationWithSound(getApplicationContext(), "ALS Alert", status, mNotificationManager);
                }else {
                    if (SharedPref.IsReadViolation(getApplicationContext()) == false) {
                        if (RulesObj.isViolation() ) {
                            if(IsForground){
                                global.PlaySound(getApplicationContext());
                                ViolationReason = violatioReason;
                                // SpeakOutMsg();
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
            Log.d("error: ", "error: " + error);
        }
    };




    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int driver_id, int flag, int inputDataLength) {

            String status = "", Message = "";
            try {
                // String responseee = "{\"Status\":true,\"Message\":\"Record Successfully Saved\",\"Data\":null}";
                JSONObject obj = new JSONObject(response);
                status = obj.getString("Status");
                Message = obj.getString("Message");
            }catch (Exception e){
                e.printStackTrace();
            }

            if (status.equals("true")) {
                switch (flag) {

                    case ClearMalDiaEvent:

                        updateClearEvent(true);

                        break;

                    case SaveDeferralMain:
                        // clear main driver deferral events from local db
                        deferralMethod.DeferralLogHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                        break;

                    case SaveDeferralCo:
                        // clear main driver deferral events from local db
                        deferralMethod.DeferralLogHelper(Integer.valueOf(CoDriverId), dbHelper, new JSONArray());

                        break;


                    case SaveMainDriverLogData:
                        BackgroundLocationService.IsAutoChange = false;
                        driverLogArray = constants.GetDriversSavedArray(getApplicationContext(), MainDriverPref, CoDriverPref);

                        if (driverLogArray.length() == 1) {
                            ClearLogAfterSuccess(driver_id);

                            // save Co Driver Data is login with co driver
                            SaveCoDriverData();
                        }else{

                            /* Check Reason: some times data was uploading in background and user entered new status in between.
                                 In api response we are clearing the entries and in between entry was skipped before upload to server.
                                So to avoid this we are checking input length and current log length.*/
                            if(driverLogArray.length() == inputDataLength) {
                                if (RePostDataCountMain > 1) {
                                    ClearLogAfterSuccess(driver_id);
                                    RePostDataCountMain = 0;
                                } else {
                                    saveActiveDriverData();
                                    RePostDataCountMain++;
                                }
                            }else{
                                if (RePostDataCountMain > 2) {
                                    ClearLogAfterSuccess(driver_id);
                                    RePostDataCountMain = 0;
                                }else{
                                    saveActiveDriverData();
                                    RePostDataCountMain++;
                                }
                            }
                        }

                        break;


                    case SaveCoDriverLogData:

                        if(RePostDataCountCo > 1){
                            ClearLogAfterSuccess(driver_id);
                            RePostDataCountCo = 0;
                        }else {
                            SaveCoDriverData();
                            RePostDataCountCo ++;
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
                        // clear malfunction array
                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());
                        break;
                }
            }else {

                if(flag == SaveMainDriverLogData || flag == SaveCoDriverLogData) {
                    if (Message.equalsIgnoreCase("Duplicate Records")) {
                        ClearLogAfterSuccess(driver_id);
                        Log.d("flag", "flag: " + flag);

                        if(flag == SaveMainDriverLogData){
                            SaveCoDriverData();
                        }
                    }
                }else if(flag == SaveDriverLog){
                    if(Message.equals("Record Not Found")) {
                        logRecordMethod.UpdateLogRecordHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());
                        global.ShowLocalNotification(getApplicationContext(), "ELD", Message, 2015);
                    }
                }else if(flag == ClearMalDiaEvent){
                    if(Message.equals("Exception is occcured")) {

                        updateClearEvent(false);
                    }
                }

            }


        }
        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("errorrr ", ">>>error dialog: " );

        }
    };



    void ClearLogAfterSuccess(int driverType) {
        driverLogArray = new JSONArray();
        if (driverType == 0) // Single Driver Type and Position is 0
            MainDriverPref.ClearLocFromList(getApplicationContext());
        else
            CoDriverPref.ClearLocFromList(getApplicationContext());

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
                    saveDriverLogPost.PostDriverLogData(LogArray, SavedLogApi, socketTimeout, false, false, SecondDriverType, SaveCoDriverLogData);
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
                    saveDriverLogPost.PostDriverLogData(LogArray, SavedLogApi, socketTimeout, false, false, SecondDriverType, SaveCoDriverLogData);
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

            //  Log.e(TAG_DATA_STAT , "Data Mobile: " + MobileUsage);
            //   Log.e(TAG_DATA_STAT , "Data Total: " + TotalUsage);
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
                //Log.e(TAG_DATA_STAT , "Data ALS received: " + AlsReceivedData);
                // Log.e(TAG_DATA_STAT , "Data ALS Sent: " + AlsSendingData);
                // Log.d("data_nw_usage", String.format(Locale.getDefault(),"uid: %1d - name: %s: Sent = %1d, Rcvd = %1d", runningApp.uid, runningApp.processName, sent, received));
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
            Intent intent = new Intent();
            intent.setClassName(ServerPackage, ServerService);
            this.bindService(intent, this.connection, Context.BIND_AUTO_CREATE);

        }catch (Exception e){ }

    }


    void saveExecutionTime(String type){
        if(processStartTime != -1){
            try {
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - processStartTime;
                String usedMemory = constants.getMemoryUsage(getApplicationContext());
                obdUtil.writeExectnTimeLogFile(elapsedTime, usedMemory, type);

                obdUtil.executionLogCount();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    private void StartStopServer(final String value){
        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED   ||
                   SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
            if (isBound) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Setup the message for invocation
                        try {
                            Log.d(TAG_OBD, "Invocation Failed!!");

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
                            Log.d(TAG_OBD, "Invocation Failed!!");
                        }
                    }
                });

            } else {
                try {
                    Log.d(TAG_OBD, "Service is Not Bound!!");
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
           /* if(ServiceConnectedCount < 2) {
                StartStopServer(constants.WiredOBD);
            }
            ServiceConnectedCount++;*/

        }

        @Override
        public void onServiceDisconnected(ComponentName component)
        {
            messenger = null;
            isBound = false;
        }
    }




    private void checkPUExceedStatus(JSONObject dataObj){
        try{
            boolean isPU75Crossed = false;
            if(dataObj != null) {
                isPU75Crossed = dataObj.getBoolean(ConstantsKeys.PersonalUse75Km);
            }else{
                double AccumulativePersonalDistance = constants.getAccumulativePersonalDistance(DriverId, offsetFromUTC, Globally.GetCurrentJodaDateTime(),
                        Globally.GetCurrentUTCDateTime(), hMethods, dbHelper, getApplicationContext());
                if(AccumulativePersonalDistance >= 75){
                    isPU75Crossed = true;
                }

            }

            boolean wasPU75Crossed = SharedPref.isPersonalUse75KmCrossed(getApplicationContext());

            SharedPref.setPersonalUse75Km(isPU75Crossed, getApplicationContext());

            if(isPU75Crossed && wasPU75Crossed == false){
                try {
                    if (EldFragment.driverLogArray == null || EldFragment.driverLogArray.length() == 0) {
                        EldFragment.driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
                    }
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

                        saveLogWithRuleCall(blank, blank, obdVehicleSpeed, blank,
                                0, blank, blank, "not_saved", obdVehicleSpeed);

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