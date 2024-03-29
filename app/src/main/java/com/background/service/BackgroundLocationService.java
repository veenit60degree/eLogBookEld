


package com.background.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
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
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ble.comm.Observable;
import com.ble.comm.ObserverManager;
import com.ble.util.BleUtil;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.BleLog;
import com.clj.fastble.utils.HexUtil;
import com.constants.APIs;
import com.constants.AsyncResponse;
import com.constants.CheckConnectivity;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.DownloadPdf;
import com.constants.DriverLogResponse;
import com.constants.RequestResponse;
import com.constants.SaveDriverLogPost;
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

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import dal.tables.OBDDeviceData;
import models.RulesResponseObject;
import obdDecoder.Decoder;
import com.ble.comm.Observer;

// 27 jan 2021   --
public class BackgroundLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, TextToSpeech.OnInitListener, LocationListener, Observer {

    String TAG = "Service";
    String TAG_OBD = "OBD Service";
    String noObd = "obd not connected";

    String obdEngineHours = "0", currentHighPrecisionOdometer = "0", obdOdometer = "0", obdTripDistance = "0", ignitionStatus = "OFF", HighResolutionDistance = "0", truckRPM = "0", apiReturnedSpeed = "";

    int GPSSpeed = 0;
    int timeInSec = -1;
    int timeDuration = 2000;

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
    private static final long MIN_TIME_LOCATION_UPDATES = 8 * 1000;   // 8 sec
    private static final long OBD_TIME_LOCATION_UPDATES = 10 * 1000;   // 10 sec
    private static final long IDLE_TIME_LOCATION_UPDATES = 3600 * 1000;   // 1 hour

    VolleyRequest GetOdometerRequest, ctPatInsp18DaysRequest, saveDriverDeviceUsageLog;
    VolleyRequest UpdateLocReqVolley, UpdateUserStatusVolley, GetRecapView18DaysData, SaveMalDiaEventRequest;
    Map<String, String> params;
    String DriverId = "", CoDriverId = "", DeviceId = "", VIN_NUMBER = "", VehicleId = "", CompareLocVal = "";
    String ObdRestarted = "OBD Restarted";

    int DriverType = 0;
    boolean isStopService = false,  RestartObdFlag = false;
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
    boolean isBleObdRespond = false;
    boolean isEldBleFound = false;
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
    long processStartTime = -1;
    double tempOdo = 1.090132595E9;
    int tempSpeed = 0;
    int ignitionCount = 0;


    private static final String SERVICE_UUID = "00001000-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTER_WRITE_UUID = "00001001-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTER_NOTIFY_UUID = "00001002-0000-1000-8000-00805f9b34fb";

    // Bluetooth obd adapter decleration
    private static final String TAG_BLE = "BleService";
    private static final String TAG_BLE_CONNECT = "BleConnect";
    private static final String TAG_BLE_OPERATION = "BleOperation";
    boolean mIsScanning = false;
    boolean isManualDisconnected = false;

    int writeFailureCount = 0;
    int isScanningCount = 0;
    int writeFailureCountToStop = 0;
    int bleScanCount = 0;

    private BluetoothAdapter mBTAdapter;

    BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;
    List<BluetoothGattService> bluetoothGattServices = new ArrayList<>();
    List<BluetoothGattCharacteristic> characteristicList = new ArrayList<>();

    String name = "";
    String mac = "";





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
        SharedPref.SaveObdStatus(SharedPref.getObdStatus(getApplicationContext()), SharedPref.getObdLastStatusTime(getApplicationContext()), getApplicationContext());

        BindConnection();

        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
            bleInit();

            if(!BleManager.getInstance().isConnected(bleDevice) && SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED){
                SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(), getApplicationContext());
            }
        }else{
            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                checkWiredObdConnection(wifiConfig.IsAlsNetworkConnected(getApplicationContext()));
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
            final Bundle bundle = msg.getData();

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

                sendBroadCast(getWiredData(vin, speed, -1));

                // save wired obd call response time to recheck later
                 SharedPref.SetWiredObdCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
            }catch (Exception e){
                e.printStackTrace();
            }
            bundle.clear();

            obdCallBackObservable(speed, vin, timeStamp, null);

        }
    }


    private void obdCallBackObservable(int speed, String vin, String timeStamp, String[] obdDataArray){

        int OBD_LAST_STATUS = SharedPref.getObdStatus(getApplicationContext());
        String last_obs_source_name = SharedPref.getObdSourceName(getApplicationContext());

        try {
            if(obdDataArray != null) {
                obdEngineHours = "0"; currentHighPrecisionOdometer = "0"; obdOdometer = "0"; obdTripDistance = "0";
                ignitionStatus = "OFF"; truckRPM = "0"; ;

                if(obdDataArray.length > 14) {
                    obdOdometer = obdDataArray[9];
                    obdTripDistance = obdDataArray[14];
                    truckRPM = obdDataArray[8];
                    obdEngineHours = obdDataArray[10];
                    currentHighPrecisionOdometer = obdDataArray[9];
                    Globally.LATITUDE = obdDataArray[12] ;
                    Globally.LONGITUDE = obdDataArray[13] ;

                    if (Integer.valueOf(truckRPM) > 0) {
                        ignitionStatus = "ON";
                        if (constants.isValidFloat(currentHighPrecisionOdometer)) {
                            // convert km to meter
                            float floatOdoInMeter = Float.parseFloat(currentHighPrecisionOdometer) * 1000;
                            currentHighPrecisionOdometer = "" + Math.round(floatOdoInMeter);
                        }
                    } else {
                        ignitionStatus = "OFF";
                    }
                }
            }else{
                if(OBD_LAST_STATUS == constants.BLE_CONNECTED) {
                   // constants.saveAppUsageLog("BleCallback: Array null" ,  false, false, obdUtil);
                    truckRPM = "";  obdOdometer = "0";  obdEngineHours = "0";
                    speed = 0;  vin = "";  ignitionStatus = "OFF";
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        SharedPref.setVehicleVin(vin, getApplicationContext());
        SharedPref.setVss(speed, getApplicationContext());
        SharedPref.setRPM(truckRPM, getApplicationContext());
        SharedPref.SetWiredObdOdometer(obdOdometer, getApplicationContext());
        SharedPref.SetObdEngineHours(obdEngineHours, getApplicationContext());


        // ---------------- temp data ---------------------
         /*    ignitionStatus = "ON"; truckRPM = "700"; speed = 8; obdEngineHours = "123959";
              ignitionCount++;
              obdOdometer = Double.toString(tempOdo);
                tempOdo = tempOdo + 200;
              currentHighPrecisionOdometer = obdOdometer;
              SharedPref.SetWiredObdOdometer(obdOdometer, getApplicationContext());
            SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, global.getCurrentDate(), getApplicationContext());
           // Log.d("tempOdo", "tempOdo: " +tempOdo);
*/

        if(OBD_LAST_STATUS == constants.WIRED_CONNECTED || OBD_LAST_STATUS == constants.BLE_CONNECTED) {
            isWiredObdRespond = true;
            if (SharedPref.getVINNumber(getApplicationContext()).length() > 5) {

                // ELD rule calling for Wired OBD
                try {
                    if (ignitionStatus.equals("ON") && !truckRPM.equals("0")) {

                        Globally.IS_OBD_IGNITION = true;
                        continueStatusPromotForPcYm("ON", last_obs_source_name, global.getCurrentDate(), OBD_LAST_STATUS);

                        // check Power Malfunction/Diagnostic event
                        String PowerEventStatus = constants.isPowerDiaMalOccurred(currentHighPrecisionOdometer, ignitionStatus,
                                obdEngineHours, DriverId, global, malfunctionDiagnosticMethod, getApplicationContext(), dbHelper);

                        if(PowerEventStatus.length() > 0){
                            if(PowerEventStatus.equals(constants.MalfunctionEvent)){
                                SharedPref.savePowerMalfunctionStatus(true, global.getCurrentDate(), getApplicationContext());
                                saveMalfunctionInTable( constants.PowerComplianceMalfunction,
                                        getApplicationContext().getResources().getString(R.string.power_comp_mal_occured));

                                constants.saveMalfncnStatus(getApplicationContext(), true);
                                constants.saveAppUsageLog("OBD:"+ OBD_LAST_STATUS + " - saveMalfunctionInTable", false, false, obdUtil);

                            }else{
                                constants.saveDiagnstcStatus(getApplicationContext(), true);
                                saveMalfunctionInTable( constants.PowerDataDiagnostic,
                                        getApplicationContext().getResources().getString(R.string.power_dia_occured));

                                constants.saveAppUsageLog("OBD:"+ OBD_LAST_STATUS + " - saveDiagnstcInTable", false, false, obdUtil);

                            }

                        }


                        // check if Malfunction/Diagnostic event occurred in ECM disconnection
                        if(malfunctionDiagnosticMethod.isDisconnected(DriverId, dbHelper)) {

                            // clear Diagnostic event when wired connection is connected.
                            SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
                            constants.saveDiagnstcStatus(getApplicationContext(), false);

                            DateTime currentTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);
                            malfunctionDiagnosticMethod.updateOccEventTimeLog(currentTime, DriverId,
                                    SharedPref.getVINNumber(getApplicationContext()), currentTime , currentTime,
                                    getApplicationContext().getResources().getString(R.string.Connected), ConstantsKeys.DiagnosticEngSync, dbHelper, getApplicationContext());

                            constants.saveAppUsageLog("OBD:"+ OBD_LAST_STATUS + " - Check Malfunction/Diagnostic  ECM disconnection status", false, false, obdUtil);

                        }


                        double obdOdometerDouble = Double.parseDouble(currentHighPrecisionOdometer);
                        String previousHighPrecisionOdometer = SharedPref.getHighPrecisionOdometer(getApplicationContext());

                        // save current odometer for HOS calculation
                        saveDayStartOdometer(currentHighPrecisionOdometer);

                        String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());
                        String currentLogDate = global.GetCurrentDateTime();

                        if (savedDate.length() == 0 && obdOdometerDouble > 0) {
                            // save current HighPrecisionOdometer locally
                            savedDate = currentLogDate;
                            SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                        }

                        boolean isDrivingAllowed = true;
                        if (SharedPref.isDrivingAllowed(getApplicationContext()) == false && speed >= 8) {
                            final DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);    // Current Date Time
                            final DateTime savedDateTime = global.getDateTimeObj(SharedPref.getDrivingAllowedTime(getApplicationContext()), false);

                            if (savedDateTime.toString().length() > 10) {
                                int timeInSec = Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();
                                if (timeInSec > 20) {
                                    isDrivingAllowed = true;
                                    SharedPref.setDrivingAllowedStatus(true, "", getApplicationContext());
                                } else {
                                    isDrivingAllowed = false;
                                }
                            }
                        }

                        double savedOdometer = 0;
                        try{
                            savedOdometer = Double.parseDouble(previousHighPrecisionOdometer);
                        }catch (Exception e){
                            savedOdometer = obdOdometerDouble;
                           // e.printStackTrace();
                        }

                        if (obdOdometerDouble >= savedOdometer) {    // needs for this check is to avoid the wrong auto change status because some times odometers are not coming

                            double calculatedSpeedFromOdo = speed;

                            try {
                                if (OBD_LAST_STATUS == constants.WIRED_CONNECTED) {
                                    // calculating speed to comparing last saved odometer and current odometer (in meter) with time difference in seconds
                                    if(SharedPref.isOdoCalculationAllowed(getApplicationContext())) {
                                        calculatedSpeedFromOdo = constants.calculateSpeedFromWiredTabOdometer(savedDate, currentLogDate,
                                                previousHighPrecisionOdometer, currentHighPrecisionOdometer, getApplicationContext());
                                    }

                                    sendBroadCast(getWiredData(vin, speed, calculatedSpeedFromOdo));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                calculatedSpeedFromOdo = speed;
                              //  constants.saveAppUsageLog("Wired: Speed - "+speed + ", Calculated - "  +e.toString(), false, false, obdUtil);
                            }

                            if (speed >= 8 || calculatedSpeedFromOdo >= 8) {
                                SharedPref.setVehilceMovingStatus(true, getApplicationContext());
                            } else {
                                SharedPref.setVehilceMovingStatus(false, getApplicationContext());
                            }

                            if (isDrivingAllowed) {
                                timeDuration = Constants.SocketTimeout3Sec;
                                callRuleWithStatusWise(currentHighPrecisionOdometer, savedDate, vin, timeStamp, speed, calculatedSpeedFromOdo);
                            }/*else{
                                constants.saveAppUsageLog("Wired: DrivingAllowed - " + isDrivingAllowed, false, false, obdUtil);
                            }*/

                            // check malfunction if valid position not coming..
                            if(SharedPref.IsAllowMalfunction(getApplicationContext())  ) {  // SharedPref.IsAllowDiagnostic(getApplicationContext())
                                checkPositionMalfunction(currentHighPrecisionOdometer, currentLogDate);
                            }

                            Globally.VEHICLE_SPEED = speed;



                        }else{
                            constants.saveAppUsageLog("Odometer issue - Current: " + obdOdometerDouble + ". Last: "+ savedOdometer, false, false, obdUtil);
                        }

                        callWiredDataService(timeDuration);

                    } else {
                        speed = 0;
                        Globally.IS_OBD_IGNITION = false;
                        continueStatusPromotForPcYm("OFF", last_obs_source_name, "", OBD_LAST_STATUS);
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
                   // constants.saveAppUsageLog("Wired: Catch block - " + e.toString(), false, false, obdUtil);

                    callWiredDataService(Constants.SocketTimeout3Sec);

                }

            } else {
                callWiredDataService(Constants.SocketTimeout3Sec);
               // constants.saveAppUsageLog("Wired: VIN issue - Value: " + SharedPref.getVINNumber(getApplicationContext()), false, false, obdUtil);
            }

        }else{
            isWiredObdRespond = true;
            // check in wire disconnect case but device is also not connected with ALS/OBD wifi ssid
            if((OBD_LAST_STATUS == constants.WIRED_DISCONNECTED || OBD_LAST_STATUS == constants.BLE_DISCONNECTED) &&
                    wifiConfig.IsAlsNetworkConnected(getApplicationContext()) == false) {

                try {
                    if(SharedPref.IsAllowMalfunction(getApplicationContext()) ||
                            SharedPref.IsAllowDiagnostic(getApplicationContext()) ){
                        DateTime disconnectTime = global.getDateTimeObj(SharedPref.getObdLastStatusTime(getApplicationContext()), false);
                        DateTime currentTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);

                        if (disconnectTime != null && currentTime != null) {
                            int timeInSec = Seconds.secondsBetween(disconnectTime, currentTime).getSeconds();
                            if (timeInSec >= 70 ) {

                                boolean isEngSyncDiaOccurred = SharedPref.isEngSyncDiagnstc(getApplicationContext());
                                if (isEngSyncDiaOccurred == false &&
                                        SharedPref.IsAllowDiagnostic(getApplicationContext())) {

                                    SharedPref.saveEngSyncDiagnstcStatus(true, getApplicationContext());
                                    constants.saveDiagnstcStatus(getApplicationContext(), true);

                                    saveMalfunctionInTable( constants.ConstEngineSyncDiaEvent,
                                            getApplicationContext().getResources().getString(R.string.eng_sync_dia_occured));


                                    malfunctionDiagnosticMethod.updateOccEventTimeLog(currentTime, DriverId,
                                            SharedPref.getVINNumber(getApplicationContext()), disconnectTime, currentTime,
                                            getApplicationContext().getResources().getString(R.string.DisConnected), ConstantsKeys.DiagnosticEngSync, dbHelper, getApplicationContext());

                                    if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE){
                                        global.ShowLocalNotification(getApplicationContext(),
                                                getApplicationContext().getResources().getString(R.string.dia_event),
                                                getApplicationContext().getResources().getString(R.string.eng_sync_dia_occured_ble_desc), 2090);
                                    }else{
                                        global.ShowLocalNotification(getApplicationContext(),
                                                getApplicationContext().getResources().getString(R.string.dia_event),
                                                getApplicationContext().getResources().getString(R.string.eng_sync_dia_occured_desc), 2090);
                                    }

                                    Globally.PlaySound(getApplicationContext());

                                } else {

                                    if(SharedPref.IsAllowMalfunction(getApplicationContext())) {
                                        boolean isEngSyncMalOccurred = SharedPref.isEngSyncMalfunction(getApplicationContext());
                                        if (isEngSyncMalOccurred == false) {

                                            int totalEngSyncMissingMin = malfunctionDiagnosticMethod.getTotalEngSyncMissingMin(DriverId, dbHelper);
                                            if (totalEngSyncMissingMin >= 20) {  // After 30 min it will become engine Sync Mal
                                                SharedPref.saveEngSyncMalfunctionStatus(true, getApplicationContext());
                                                constants.saveMalfncnStatus(getApplicationContext(), true);

                                                saveMalfunctionInTable(constants.ConstEngineSyncMalEvent,
                                                        getApplicationContext().getResources().getString(R.string.eng_sync_mal_occured));


                                                malfunctionDiagnosticMethod.updateOccEventTimeLog(currentTime, DriverId,
                                                        SharedPref.getVINNumber(getApplicationContext()), disconnectTime, currentTime,
                                                        getApplicationContext().getResources().getString(R.string.DisConnected), ConstantsKeys.MalfunctionEngSync, dbHelper, getApplicationContext());


                                                global.ShowLocalNotification(getApplicationContext(),
                                                        getApplicationContext().getResources().getString(R.string.malfunction_event),
                                                        getApplicationContext().getResources().getString(R.string.eng_sync_mal_occured), 2091);

                                                Globally.PlaySound(getApplicationContext());

                                            } else {
                                                SharedPref.saveEngSyncMalfunctionStatus(false, getApplicationContext());
                                                constants.saveMalfncnStatus(getApplicationContext(), false);
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
                callWiredDataService(Constants.SocketTimeout4Sec);
            }else {
                callWiredDataService(Constants.SocketTimeout8Sec);
            }
        }


        // Sync app usage log to server (SAVE sync data service)
        obdUtil.syncAppUsageLog(getApplicationContext(), DriverId);

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

            } else if (jobType.equals(global.ON_DUTY)) {

                // if speed is coming >8 then ELD rule is called after 8 sec to change the status to Driving as soon as.
                if (speed >= 8 && calculatedSpeedFromOdo >= 8) {

                    try {
                        if (EldFragment.driverLogArray == null || EldFragment.driverLogArray.length() == 0) {
                            EldFragment.driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
                        }
                        boolean isYardMove = hMethods.isPCYM(EldFragment.driverLogArray);
                        if(isYardMove){
                            String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getApplicationContext());
                            if(speed >= 32 && (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2))) {   // In Yard move
                                timeDuration = Constants.SocketTimeout15Sec;
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
                    if(speed == 0){
                        timeDuration = Constants.SocketTimeout4Sec;
                    }
                    // call ELD rule after 1 minute to improve performance
                    if (constants.minDiff(savedDate, global, getApplicationContext()) > 0) {
                        saveLogWithRuleCall(currentHighPrecisionOdometer, currentLogDate, speed, vin, intHighPrecisionOdometerInKm,
                                timeStamp, savedDate, "OnDuty Speed: "+speed, calculatedSpeedFromOdo);
                    }
                }

            } else {

                // =================== For OFF Duty & Sleeper case =====================

                if (speed <= 0 && calculatedSpeedFromOdo <= 0 ) {  //
                    //   Log.d("ELD Rule", "data is correct for this status. No need to call ELD rule.");
                    if(speed == 0)
                        timeDuration = Constants.SocketTimeout4Sec;
                    SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                } else {
                    if (speed >= 8 && calculatedSpeedFromOdo >= 8 ) {    // if speed is coming >8 then ELD rule is called after 8 sec to change the status to Driving as soon as.

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
                                if(SharedPref.isPersonalUse75KmCrossed(getApplicationContext()) ){
                                   // constants.saveAppUsageLog("PU exceeded 75km ELD rule called", false, false, obdUtil);
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

    String getWiredData(String vin, int speed, double calculatedSpeed){
        return    "<b>ignitionStatus:</b> " + ignitionStatus + "<br/>" +
                "<b>Truck RPM:</b> " + truckRPM + "<br/>" +
                "<b>Odometer:</b> " + obdOdometer + "<br/>" +
                "<b>currentHighPrecisionOdometer:</b> " + currentHighPrecisionOdometer + "<br/>" +
                "<b>Speed:</b> " + speed + "<br/>" +
                "<b>Calculated Speed:</b> " + calculatedSpeed + "<br/>" +
                "<b>VIN:</b> " + vin + "<br/>" +
                "<b>Trip Distance:</b> " + obdTripDistance + "<br/>" +
                "<b>EngineHours:</b> " + obdEngineHours + "<br/>" ;
    }


    private void checkWiredObdConnection(boolean isAlsNetworkConnected){
        int lastObdStatus = SharedPref.getObdStatus(getApplicationContext());
        obdShell = ShellUtils.execCommand("cat /sys/class/power_supply/usb/type", false);

       if (obdShell.result == 0) {
            //System.out.println("obd --> cat type --> " + obdShell.successMsg);
            if (obdShell.successMsg.contains("USB_DCP")) {  // Connected State
                if (lastObdStatus != Constants.WIRED_CONNECTED) {
                    StartStopServer(constants.WiredOBD);
                }

                SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, global.getCurrentDate(), getApplicationContext());

            } else {
                // Disconnected State. Save only when last status was not already disconnected
                if (isAlsNetworkConnected == false && lastObdStatus != constants.WIRED_DISCONNECTED) {

                    SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, global.getCurrentDate(), getApplicationContext());
                    if (ignitionStatus.equals("OFF")) {
                        SharedPref.SetTruckIgnitionStatus(ignitionStatus, constants.WiredOBD, global.getCurrentDate(), obdEngineHours, currentHighPrecisionOdometer, getApplicationContext());
                    }

                    if(UILApplication.isActivityVisible()){
                        showEldEcmAlert();
                    }
                }

            }
        } else {
            if (isAlsNetworkConnected == false && lastObdStatus != Constants.WIFI_CONNECTED) {
                SharedPref.SaveObdStatus(Constants.WIRED_ERROR, global.getCurrentDate(), getApplicationContext());
            }
        }

    }


    void showEldEcmAlert(){
        try{
            Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
            intent.putExtra(ConstantsKeys.PersonalUse75Km, false);
            intent.putExtra(ConstantsKeys.IsEldEcmALert, true);
            LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void bleInit() {
        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            sendBroadCast(getResources().getString(R.string.ble_not_supported));
            Log.d(TAG_BLE, getResources().getString(R.string.ble_not_supported));
            return;
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();

        }
        if (mBTAdapter == null) {
            sendBroadCast(getResources().getString(R.string.bt_unavailable));
            Log.d(TAG_BLE, getResources().getString(R.string.bt_unavailable));
            return;
        }

        if (!mBTAdapter.isEnabled()) {
            mBTAdapter.enable();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                 initilizeBle();
                }
            }, 4000);


        }else{
            initilizeBle();
        }

    }


    private void initilizeBle(){
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(3, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(10000);
    }


    private void checkPermissionsBeforeScanBle() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isConnected = BleManager.getInstance().isConnected(bleDevice);

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            sendBroadCast("BlueTooth was disabled. Turning on..");
            try {
                Thread.sleep(2000);
                BleManager.getInstance().disconnectAllDevice();
                ObserverManager.getInstance().notifyObserver(bleDevice);
                constants.saveAppUsageLog("BlueTooth was disabled. Turning on..", false, false, obdUtil);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            if (constants.CheckGpsStatusToCheckMalfunction(getApplicationContext())) {

                if (!mIsScanning && !isConnected) {

                    // ignore scan after 5 attempts if device not found
                    if(SharedPref.getBleScanCount(getApplicationContext()) < 5) {
                        startScan();
                    }else{
                        if(bleScanCount == 5) {
                            bleScanCount++;
                            Globally.PlayNotificationSound(getApplicationContext());
                            global.ShowLocalNotification(getApplicationContext(),
                                    getApplicationContext().getResources().getString(R.string.BluetoothOBD),
                                    getApplicationContext().getResources().getString(R.string.BleObdNotFound), 2096);
                        }
                    }


                }

                // some times scan not finished and continuesly scaning then we need this check
                if(isScanningCount > 6 && mIsScanning){
                    if(BleManager.getInstance() != null) {
                        BleManager.getInstance().cancelScan();
                        mIsScanning = false;
                        isScanningCount = 0;
                    }
                }
                isScanningCount++;

            }
        }

    }


    private void startScan() {
        isScanningCount = 0;
        isEldBleFound = false;

        BleManager.getInstance().scan(new BleScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onScanStarted(boolean success) {
                if (success) {
                    mIsScanning = true;
                    isBleObdRespond = false;
                    sendBroadCast("Scan Started");
                   // constants.saveAppUsageLog("BleCallback: ScanStarted", false, false, obdUtil);
                } else {
                   // constants.saveAppUsageLog("Start Bluetooth: Scan" ,  false, false, obdUtil);
                    sendBroadCast("Scanning");
                    mIsScanning = false;
                    bleInit();
                }
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {

                try {
                    if (bleDevice.getName() != null) {
                        if (bleDevice.getName().contains("ALSELD") || bleDevice.getName().contains("SMBLE")) {
                            Log.d("getName", "getName: " + bleDevice.getName());
                            bleScanCount = 0;
                            SharedPref.saveBleScanCount(0, getApplicationContext());

                            isEldBleFound = true;
                            sendBroadCast("Connecting");
                            connect(bleDevice);
                            BleManager.getInstance().cancelScan();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                mIsScanning = false;
                sendBroadCast("Scan Finished");
                if (!isEldBleFound) {
                    bleScanCount++;
                    SharedPref.saveBleScanCount(bleScanCount, getApplicationContext());
                    sendBroadCast("Bluetooth OBD not found.");
                    obdCallBackObservable(-1, SharedPref.getVehicleVin(getApplicationContext()), global.GetCurrentDateTime(), null);
                }
            }
        });

    }



    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.d(TAG_BLE_CONNECT, "onStartConnect");
                sendBroadCast("Connecting");
                isBleObdRespond = false;
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.d(TAG_BLE_CONNECT, "onConnectFail");
                constants.saveAppUsageLog("BleCallback: ConnectFail" ,  false, false, obdUtil);
                sendBroadCast("Connecting failed");

                ObserverManager.getInstance().notifyObserver(bleDevice);

                if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {
                    SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(), getApplicationContext());
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onConnectSuccess(BleDevice blueToothDevice, BluetoothGatt gatt, int status) {
                Log.d(TAG_BLE_CONNECT, "Connected Successfully");

                if (BleManager.getInstance().isConnected(bleDevice)) {
                    isManualDisconnected = false;
                    setDisconnectType(isManualDisconnected);

                    setBleDevice(blueToothDevice);
                    addObserver(blueToothDevice);
                    name = bleDevice.getName();
                    mac  = bleDevice.getMac();

                  /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        gatt.requestMtu(512);
                    }*/

                    bluetoothGattServices = new ArrayList<>();
                    for (BluetoothGattService service : gatt.getServices()) {
                        bluetoothGattServices.add(service);
                    }

                    if (bluetoothGattServices.size() > 2) {
                        BluetoothGattService service = bluetoothGattServices.get(2);
                        setBluetoothGattService(service);
                        writeData();
                    }else{
                        constants.saveAppUsageLog("BleCallback: ConnectSuccess but gatt size is 0" ,  false, false, obdUtil);
                    }

                }

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {

                Log.d(TAG_BLE_CONNECT, "onDisConnected");
                constants.saveAppUsageLog("BleCallback: DisConnected" ,  false, false, obdUtil);
                isBleObdRespond = false;
                ObserverManager.getInstance().notifyObserver(bleDevice);
                stopService(bleDevice,getCharacteristic());

                sendBroadCast("BLE DisConnected - isManualDisconnected: " +isManualDisconnected);

                if (!isManualDisconnected()) {
                    bleInit();
                   // setScanRule();
                    startScan();
                }

                // Reset data on disConnected
                if(SharedPref.getObdStatus(getApplicationContext()) == constants.BLE_CONNECTED) {
                    truckRPM = "0";
                    obdOdometer = "0";
                    obdEngineHours = "0";
                    ignitionStatus = "OFF";
                    SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(), getApplicationContext());
                    obdCallBackObservable(-1, SharedPref.getVehicleVin(getApplicationContext()), global.GetCurrentDateTime(), null);

                    if(UILApplication.isActivityVisible()){
                        showEldEcmAlert();
                    }
                }



            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void getCharacteristicListData() {
        BluetoothGattService service = getBluetoothGattService();
        characteristicList = new ArrayList<>();
        if (service != null) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                characteristicList.add(characteristic);
            }
        }
    }

    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(int maxlength, int PROPERTY) {
        BluetoothGattCharacteristic characteristic = null;
        if (characteristicList.size() > maxlength) {
            characteristic = characteristicList.get(maxlength);
            if (PROPERTY > 0) {
                setCharacteristic(characteristic);
            }
        }
        return characteristic;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void writeData(){

        getCharacteristicListData();

        BluetoothGattCharacteristic characteristic  = getBluetoothGattCharacteristic(0, BluetoothGattCharacteristic.PROPERTY_WRITE);

        if(characteristic != null) {
            String writeValue = "";
            String uuidName = bleDevice.getName();
            //final String requestData  = "request:{source_id:" + uuidName + ",events:[{5B6A,0,0,000000,000000,000000000000,1111,0,0,,0,0,0,0,0,0,0,57,79}]}";
            final String requestData = "request:{source_id:" + uuidName + ",events:[{5B6A,0,0,000000,000000,000000000000,1111,0,0,0,0,,0,0,0,0,0,0,0,57,79}]}";    //{5B6A,0,0,000000,000000,000000000000,1111,0,0,,0,0,0,0,0,0,0,57,79}]

            writeValue = BleUtil.convertStringToHex(requestData);
            writeValue = writeValue.replaceAll(" ", "");
            byte[] bytes = BleUtil.invertStringToBytes(writeValue);

            BleManager.getInstance().write(
                    bleDevice,
                    SERVICE_UUID    /*characteristic.getService().getUuid().toString()*/,
                    CHARACTER_WRITE_UUID    /*characteristic.getUuid().toString()*/,
                    bytes, // by,
                    new BleWriteCallback() {

                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                            Log.d(TAG_BLE_OPERATION, "onWriteSuccess");
                            sendBroadCast("Write data Success");

                            if(current == total) {
                                // read ble data after write success
                                readData();
                            }
                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {
                            Log.d(TAG_BLE_OPERATION, "onWriteFailure" + exception.toString());
                            constants.saveAppUsageLog("onWriteFailure: " +exception.toString() ,  false, false, obdUtil);
                            sendBroadCast("Write data Failure: " +exception.toString());
                            mIsScanning = false;
                            isBleObdRespond = false;

                            ObserverManager.getInstance().notifyObserver(bleDevice);
                            stopService(bleDevice,getCharacteristic());
                            writeFailureCount++;
                            if(writeFailureCount > 4){

                                writeFailureCount = 0;

                                if (mBTAdapter != null && mBTAdapter.isEnabled()) {
                                    mBTAdapter.disable();
                                }

                            }

                            if(writeFailureCountToStop == 4){
                                bleScanCount = 6;
                                SharedPref.saveBleScanCount(bleScanCount, getApplicationContext());
                                Globally.PlayNotificationSound(getApplicationContext());
                                global.ShowLocalNotification(getApplicationContext(),
                                        getApplicationContext().getResources().getString(R.string.BluetoothOBD),
                                        getApplicationContext().getResources().getString(R.string.BleObdNotConnected), 2097);
                                writeFailureCountToStop = 0;
                            }
                            writeFailureCountToStop++;

                            if (isManualDisconnected()) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        bleInit();
                                    }
                                }, 1500);
                            }
                        }
                    });
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void readData(){

        BluetoothGattCharacteristic characteristic = getBluetoothGattCharacteristic(1, BluetoothGattCharacteristic.PROPERTY_READ);

        if (characteristic != null) {
            BleManager.getInstance().read(
                    bleDevice,
                    SERVICE_UUID    /*characteristic.getService().getUuid().toString()*/,
                    CHARACTER_NOTIFY_UUID   /*characteristic.getUuid().toString()*/,
                    new BleReadCallback() {

                        @Override
                        public void onReadSuccess(final byte[] data) {
                            Log.d(TAG_BLE_OPERATION, "onReadSuccess");
                            sendBroadCast("onReadSuccess");
                            notifyData();

                        }

                        @Override
                        public void onReadFailure(final BleException exception) {
                            Log.d(TAG_BLE_OPERATION, "onReadFailure");
                            mIsScanning = false;
                            isBleObdRespond = false;

                            constants.saveAppUsageLog("ReadFailure: " +exception.toString() ,  false, false, obdUtil);
                            sendBroadCast("onReadFailure: " +exception.toString());

                        }
                    });
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void notifyData(){

        final BluetoothGattCharacteristic characteristic = getBluetoothGattCharacteristic(1, BluetoothGattCharacteristic.PROPERTY_NOTIFY);

        if (characteristic != null) {
            BleManager.getInstance().notify(
                    bleDevice,
                    SERVICE_UUID    /*characteristic.getService().getUuid().toString()*/,
                    CHARACTER_NOTIFY_UUID   /*characteristic.getUuid().toString()*/,
                    false,
                    new BleNotifyCallback() {

                        @Override
                        public void onNotifySuccess() {
                            Log.d(TAG_BLE_OPERATION, "onNotifySuccess");
                            isBleObdRespond = false;
                            writeFailureCount = 2;
                            writeFailureCountToStop = 0;
                          //  String data = HexUtil.formatHexString(characteristic.getValue(), true);
                          //  Log.d("Notify Success Data", "data: " + data);
                           // constants.saveAppUsageLog("BleCallback: NotifySuccess" ,  false, false, obdUtil);
                            sendBroadCast("onNotifySuccess");
                        }

                        @Override
                        public void onNotifyFailure(final BleException exception) {

                            Log.d(TAG_BLE_OPERATION, "onReadFailure: " + exception.toString());
                            mIsScanning = false;
                            constants.saveAppUsageLog("NotifyFailure: " +exception.toString() ,  false, false, obdUtil);
                            sendBroadCast("onNotifyFailure: " + exception.toString());

                            if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {
                                SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(), getApplicationContext());
                            }



                        }

                        @Override
                        public void onCharacteristicChanged(byte[] data) {
                            Log.d("onCharacteristicChanged", "onCharacteristicChanged" );

                            try {
                                if (characteristic.getValue() != null && characteristic.getValue().length > 50) {
                                    // temp data
                              /*  if(isBleObdRespond == false){
                                    SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, global.getCurrentDate(), getApplicationContext());
                                    constants.saveAppUsageLog("BleCallback: Notify Data Changed" ,  false, false, obdUtil);
                                }
                                sendBroadCast( "Name: " + bleDevice.getName()+ ", Mac: "+ bleDevice.getMac());
*/
                                    String[] decodedArray = BleUtil.decodeDataChange(characteristic);
                                    if (decodedArray != null && decodedArray.length >= 20) {  //11
                                        if (isBleObdRespond == false) {
                                            SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, global.getCurrentDate(), getApplicationContext());
                                            constants.saveAppUsageLog("BleCallback: Notify Data Changed", false, false, obdUtil);
                                        }

                                        isBleObdRespond = true;
                                        int VehicleSpeed = Integer.valueOf(decodedArray[7]);
                                        String VehicleVIN = decodedArray[10];

                                 /*   // temp value assigned. updated firmware position in upper comment section
                                    int VehicleSpeed = 0;
                                    String VehicleVIN = decodedArray[8];
*/

                                        sendBroadCast(BleUtil.decodeDataChange(characteristic, bleDevice.getName(), bleDevice.getMac()));
                                        obdCallBackObservable(VehicleSpeed, VehicleVIN, global.GetCurrentDateTime(), decodedArray);
                                    } else {
                                        if (isBleObdRespond == false) {
                                            SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, global.getCurrentDate(), getApplicationContext());
                                            constants.saveAppUsageLog("BleCallback: Notify Data Changed inComplete", false, false, obdUtil);
                                        }
                                        isBleObdRespond = true;
                                        sendBroadCast("Ble OBD connected but returns inComplete data. <br/> Contact with your company.");
                                    }

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }


    private void addObserver(BleDevice blueToothDevice){
        bleDevice = blueToothDevice;
        if (bleDevice == null) {
            return;
        }
        ObserverManager.getInstance().addObserver(this);
    }


    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(new UUID[0])      // Only scan the equipment of the specified service, optional
                .setDeviceName(true, "")   // Only scan devices with specified broadcast name, optional
                .setDeviceMac("")                  // Only scan devices of specified mac, optional
                .setAutoConnect(false)          // isAutoConnect - AutoConnect parameter when connecting, optional, default false
                .setScanTimeOut(10000)              // Scan timeout time, optional, default 10 seconds
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    @Override
    public void disConnected(BleDevice bleDevice) {
        if (bleDevice != null && (bleDevice != null && bleDevice.getKey().equals(bleDevice.getKey()))) {
            mIsScanning = false;
            isBleObdRespond = false;
            Log.d(TAG_BLE_CONNECT, "Observer disConnected");

            sendBroadCast("disConnected");
        }
    }

    public void setDisconnectType(boolean isManual) {
        this.isManualDisconnected = isManual;
    }

    public boolean isManualDisconnected() {
        return isManualDisconnected;
    }



    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }
    public BluetoothGattService getBluetoothGattService() {
        return bluetoothGattService;
    }

    public void setBluetoothGattService(BluetoothGattService bluetoothGattService) {
        this.bluetoothGattService = bluetoothGattService;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopService(BleDevice bleDevice, BluetoothGattCharacteristic characteristic){
        try{
            if(bleDevice != null) {
                if (BleManager.getInstance().isConnected(bleDevice)) {

                    String character = "";
                    if(writeFailureCount > 1){
                        character = CHARACTER_NOTIFY_UUID;
                    }else{
                        character = CHARACTER_WRITE_UUID;
                    }
                    Log.e("Character:",""+getCharacteristic().getUuid().toString());

                    BleManager.getInstance().stopNotify(
                            bleDevice,
                            characteristic.getService().getUuid().toString(),
                            character);

                    isManualDisconnected = true;
                    setDisconnectType(isManualDisconnected);
                    BleManager.getInstance().clearCharacterCallback(bleDevice);
                    ObserverManager.getInstance().deleteObserver(this);
                    BleManager.getInstance().disconnect(bleDevice);
                   // BleManager.getInstance().disconnectAllDevice();
                    BleManager.getInstance().destroy();
                    isBleObdRespond = false;
                    mIsScanning = false;

                    if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {
                        SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(), getApplicationContext());
                    }
                    // stopSelf();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void sendBroadCast(String data){
        try {
         //   if(SharedPref.isOBDScreen(getApplicationContext())) {
                Intent intent = new Intent("ble_changed_data");
                intent.putExtra("decoded_data", data);
                LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
          //  }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void checkPositionMalfunction(String currentHighPrecisionOdometer, String currentLogDate){

        try {

            String lastCalledTime = SharedPref.getLastMalfCallTime(getApplicationContext());
            if (lastCalledTime.length() == 0 ) {
                SharedPref.setMalfCallTime(currentLogDate, getApplicationContext());
            }

            if (constants.minDiffMalfunction(lastCalledTime, global, getApplicationContext()) > 0) {

                SharedPref.setMalfCallTime(currentLogDate, getApplicationContext());

                if (constants.CheckGpsStatusToCheckMalfunction(getApplicationContext()) == false) {
                    Globally.LATITUDE = "0.0";
                    Globally.LONGITUDE = "0.0";
                    if (SharedPref.getEcmObdLatitude(getApplicationContext()).length() > 4) {
                        SharedPref.setEcmObdLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE,
                                currentHighPrecisionOdometer, global.GetCurrentDateTime(), getApplicationContext());
                    }
                }

                // check malfunction
                boolean isMalfunction = constants.isLocationMalfunctionOccured(getApplicationContext());
                //Log.d("isMalfunction", "isMalfunction: " + isMalfunction);

                if (isMalfunction && SharedPref.isLocMalfunctionOccur(getApplicationContext()) == false) {
                    SharedPref.saveLocMalfunctionOccurStatus(isMalfunction, currentLogDate, getApplicationContext());

                    saveMalfunctionInTable(constants.PositionComplianceMalfunction,
                            getApplicationContext().getResources().getString(R.string.pos_mal_occured));

                    global.ShowLocalNotification(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.pos_mal_occured),
                            getApplicationContext().getResources().getString(R.string.pos_mal_occured_desc), 2091);

                    Globally.PlaySound(getApplicationContext());
                }

              //  constants.saveAppUsageLog("OBD:" + " - CheckPositionMalfunction", false, false, obdUtil);

            }

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

                // write wired OBD details in a text file and save into the SD card.
                String obdSource;
                if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                    obdSource = constants.BleObd;
                }else{
                    obdSource = constants.WiredOBD;
                }
                if(SharedPref.isOdoCalculationAllowed(getApplicationContext())) {
                    saveObdData(obdSource, vin, obdOdometer, String.valueOf(intHighPrecisionOdometerInKm),
                            currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                            String.valueOf(calculatedSpeedFromOdo), obdTripDistance, timeStamp, savedDate);
                }else{
                    saveObdData(obdSource, vin, obdOdometer, String.valueOf(intHighPrecisionOdometerInKm),
                            currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                            String.valueOf(-1), obdTripDistance, timeStamp, savedDate);
                }



                if (speed > 200) {
                    speed = -1;
                }
            }

            callEldRuleForWired(speed, calculatedSpeedFromOdo);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void saveMalfunctionInTable(String malDiaType, String MalfunctionDefinition){

        // save malfunction occur event to server with few inputs
        JSONObject newOccuredEventObj = malfunctionDiagnosticMethod.GetJsonFromList(
                DriverId, DeviceId, SharedPref.getVINNumber(getApplicationContext()),
                DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
                DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                SharedPref.getObdEngineHours(getApplicationContext()),
                SharedPref.getHighPrecisionOdometer(getApplicationContext()),
                SharedPref.getHighPrecisionOdometer(getApplicationContext()),
                global.GetCurrentUTCTimeFormat(), malDiaType, MalfunctionDefinition
        );


        // save Occurred Mal/Dia events locally to get details later for clear them
        JSONArray malArrayEvent = malfunctionDiagnosticMethod.getSavedMalDiagstcArrayEvents(Integer.parseInt(DriverId), dbHelper);
        malArrayEvent.put(newOccuredEventObj);
        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelperEvents(Integer.parseInt(DriverId), dbHelper, malArrayEvent);


        // save Occurred event locally until not posted to server
        JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(Integer.parseInt(DriverId), dbHelper);
        malArray.put(newOccuredEventObj);
        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(Integer.parseInt(DriverId), dbHelper, malArray);

        // call api
        SaveMalfnDiagnstcLogToServer(malArray);

    }


    void saveDayStartOdometer(String currentHighPrecisionOdometer){
        String savedDate = SharedPref.getDayStartSavedTime(getApplicationContext());
        String currentLogDate = global.GetCurrentDateTime();
        try {
            currentHighPrecisionOdometer = currentHighPrecisionOdometer.split("\\.")[0];
            int odometerInMiles = constants.meterToMiles(Integer.parseInt(currentHighPrecisionOdometer));

            if(odometerInMiles > 0) {
                if (savedDate.length() > 0) {
                    int dayDiff = constants.getDayDiff(savedDate, currentLogDate);
                    String savedOdo = SharedPref.getDayStartOdometer(getApplicationContext());
                    if (dayDiff != 0 || savedOdo.equals("0")) {
                        SharedPref.setDayStartOdometer("" + odometerInMiles, currentLogDate, getApplicationContext());
                    }
                } else {
                    SharedPref.setDayStartOdometer("" + odometerInMiles, currentLogDate, getApplicationContext());
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

                timeInSec = Seconds.secondsBetween(savedDateTime, currentDateTime).getSeconds();    //Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();
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




    private void saveObdData(String source, String vin, String odometer, String HighPrecisionOdometer,
                             String obdOdometerInMeter, String correctedData, String ignition, String rpm,
                             String speed, String speedCalculated, String tripDistance, String timeStamp,
                             String previousDate){

        boolean isDeviceLogEnabled = driverPermissionMethod.isDeviceLogEnabled(DriverId, dbHelper);

        if(isDeviceLogEnabled) {

            JSONObject obj = new JSONObject();
            try {

                obj.put(constants.obdSource, source);
                obj.put(constants.obdOdometer, odometer);
                obj.put(constants.obdHighPrecisionOdo, HighPrecisionOdometer);

                if (source.equals(Constants.WifiOBD)) {

                    if (correctedData.trim().length() > 0) {
                        obj.put(constants.CorrectedData, correctedData);
                    }

                    try {
                        String[] array = obdOdometerInMeter.split(",  ");
                        if (array.length > 0) {
                            obj.put(constants.DecodedData, array[0]);

                            obj.put(constants.PreviousLogDate, previousDate);
                            obj.put(constants.CurrentLogDate, global.GetCurrentDateTime());
                            obj.put(constants.obdCalculatedSpeed, array[1]);
                        } else {
                            obj.put(constants.CurrentLogDate, global.GetCurrentDateTime());
                            obj.put(constants.DecodedData, obdOdometerInMeter);
                        }

                        obj.put(constants.WheelBasedVehicleSpeed, speed);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    if (source.equals(constants.ApiData) || source.equals(constants.OfflineData)) {
                        obj.put(constants.obdDetail, obdOdometerInMeter);
                        obj.put(constants.LastRecordTime, timeStamp);
                    } else {
                        obj.put(constants.obdOdometerInMeter, obdOdometerInMeter);
                        obj.put(constants.ObdRecordTime, timeStamp);
                        obj.put(constants.PreviousLogDate, previousDate);
                        obj.put(constants.CurrentLogDate, global.GetCurrentDateTime());
                    }

                    obj.put(constants.calculatedSpeed, speedCalculated);
                    obj.put(constants.obdSpeed, speed);
                    obj.put(constants.obdVINNumber, vin);
                }


                obj.put(constants.obdIgnitionStatus, ignition);
                obj.put(constants.obdRPM, rpm);
                obj.put(constants.apiReturnedSpeed, apiReturnedSpeed);
                obj.put(constants.obdTripDistance, tripDistance);
                obj.put(ConstantsKeys.Latitude, Globally.LATITUDE);
                obj.put(ConstantsKeys.Longitude, Globally.LONGITUDE);

                global.OBD_DataArray.put(obj);
                obdUtil.writeToLogFile(obj.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }








    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
           // Log.d("onLocationChanged", "---Latitude: " + location.getLatitude() + " -- Longitude: " + location.getLongitude());
            Globally.LATITUDE = "" +location.getLatitude();
            Globally.LONGITUDE = "" +location.getLongitude();
            Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(Globally.LONGITUDE);
          //  GpsVehicleSpeed = (int) location.getSpeed() * 18 / 5;
            isGpsUpdate = true;
            // GpsVehicleSpeed = 21;
            // saving location with time info to calculate location mafunction event
            int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
            if(ObdStatus == Constants.WIRED_CONNECTED ||
                    ObdStatus == Constants.WIFI_CONNECTED ||
                    ObdStatus == Constants.BLE_CONNECTED){
                saveEcmLocationWithTime(Globally.LATITUDE, SharedPref.getHighPrecisionOdometer(getApplicationContext()));
            }else{
                saveEcmLocationWithTime(Globally.LATITUDE, SharedPref.getHighPrecisionOdometer(getApplicationContext()));
            }

            // getLocDegree(location);
            //   global.ShowLocalNotification(getApplicationContext(), "onLocationChanged", "Speed is: " + GpsVehicleSpeed, 2003);
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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "---------onStartCommand Service");

        /*double speed = constants.calculateSpeedFromWiredTabOdometer("2021-06-28T00:12:26.08", "2021-06-28T00:12:30.08",
                "1090031400", "1090031465", getApplicationContext());
        Log.d("speed", "speed: " +speed);
        */
        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {

            boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
            String bleStatus = SharedPref.isBlePing(getApplicationContext());

            if(EldFragment.IsTruckChange){
                if(!mIsScanning && !isConnected) {
                    bleInit();
                }
            }else {
                if (bleStatus.equals("start")) {
                    if (!mIsScanning && !isConnected) {
                        writeFailureCountToStop = 0;
                        bleInit();
                    }
                } else if(bleStatus.equals("stop")) {
                    // stop ble communication
                    stopService(bleDevice, getCharacteristic());
                    isBleObdRespond = false;
                    SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(), getApplicationContext());
                    constants.saveAppUsageLog("BleCallback: "+getResources().getString(R.string.ble_disconnected_by_user) ,  false, false, obdUtil);
                   // sendBroadCast(getResources().getString(R.string.ble_disconnected_by_user));
                }
            }
            SharedPref.SetBlePingStatus("", getApplicationContext());

        }else if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED){
            StartStopServer(constants.WiredOBD);
        }else{
            if(wifiConfig.IsAlsNetworkConnected(getApplicationContext()) && IsOBDPingAllowed ){    // check ALS SSID connection with IsOBDPingAllowed permission
                tcpClient.sendMessage("123456,can");
                SharedPref.SaveConnectionInfo(constants.WifiOBD, Globally.GetCurrentDeviceDate(), getApplicationContext());
            }
        }

        EldFragment.IsTruckChange = false;
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
                Globally.LATITUDE = "0.0";
                Globally.LONGITUDE = "0.0";
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

            if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                    !SharedPref.getPassword(getApplicationContext()).equals("")) {

                processStartTime = -1;
                if (SpeedCounter == HalfSpeedCounter || SpeedCounter >= MaxSpeedCounter) {
                    processStartTime = System.currentTimeMillis();
                }

                // get ALS Wifi ssid availability
                boolean isAlsNetworkConnected   = wifiConfig.IsAlsNetworkConnected(getApplicationContext());
                int getObdLastStatus = SharedPref.getObdStatus(getApplicationContext());

                // communicate with wired OBD server if not connected
                if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                    checkWiredObdConnection(isAlsNetworkConnected);
                    if(!isWiredObdRespond) {
                       // constants.saveAppUsageLog("StartStopServer in Timer" ,  false, false, obdUtil);
                        StartStopServer(constants.WiredOBD);

                        if(BleManager.getInstance() != null && BleManager.getInstance().isConnected(bleDevice)) {
                            stopService(bleDevice, characteristic);
                        }

                    }else{
                        String obdLastCallDate = SharedPref.getWiredObdCallTime(getApplicationContext());
                        if(obdLastCallDate.length() > 10){
                            int lastCalledMinDiff = constants.getMinDifference(obdLastCallDate, Globally.GetCurrentDateTime());
                            if(lastCalledMinDiff > 0){
                                StartStopServer(constants.WiredOBD);
                            }
                        }
                    }
                }else if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE ){
                    checkPermissionsBeforeScanBle();
                }

                try {
                    // request for location if lat long is null
                    if( Globally.LATITUDE.equals("0.0") ||  Globally.LATITUDE.equals("") ||  Globally.LATITUDE.equals("null")){

                        // check availability of play services
                        if (global.checkPlayServices(getApplicationContext())) {
                            createLocationRequest(MIN_TIME_LOCATION_UPDATES);
                        } else {
                            requestLocationWithoutPlayServices();
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                // networkUsage();

                final boolean isGpsEnabled = constants.CheckGpsStatusToCheckMalfunction(getApplicationContext());
                if(!isGpsEnabled){
                    // GpsVehicleSpeed = -1;
                    Globally.LATITUDE = "0.0";
                    Globally.LONGITUDE = "0.0";
                }


                //  if(isBound && ( ignitionStatus.equals("ON") && !truckRPM.equals("0") ) )


                if (SpeedCounter == HalfSpeedCounter || SpeedCounter >= MaxSpeedCounter) {

                    if (!global.checkPlayServices(getApplicationContext()) && !isGpsUpdate) {
                        requestLocationWithoutPlayServices();
                    }

                    isStopService = false;
                    UpdateDriverInfo();

                    if(SpeedCounter >= MaxSpeedCounter) {
                        SpeedCounter = 0;
                        // Update UTC date time after 60 seconds
                        global.updateCurrentUtcTime(getApplicationContext());
                    }else{
                        SpeedCounter = SpeedCounter + LocRefreshTime;
                    }

                    try {

                        if (global.isWifiOrMobileDataEnabled(getApplicationContext()) ) {

                            if(constants.IsAlsServerResponding) {
                                if(SpeedCounter == 0) {
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

                                        if(UILApplication.isActivityVisible() && Constants.IS_ACTIVE_ELD){
                                             /*updateOfflineApiRejectionCount++;
                                           if (updateOfflineApiRejectionCount > 1) {
                                                updateOfflineApiRejectionCount = 0;
                                                constants.IsAlsServerResponding = false;
                                            }*/
                                        }else {

                                            saveActiveDriverData();
                                        }

                                    }
                                }
                            }else{
                                Globally.VEHICLE_SPEED = -1;
                               // VehicleSpeed = GpsVehicleSpeed;
                                updateOfflineApiRejectionCount++;
                                if(updateOfflineApiRejectionCount > 2){
                                    updateOfflineApiRejectionCount = 0;
                                    constants.IsAlsServerResponding = true;
                                }else{
                                    if(updateOfflineApiRejectionCount > 1){
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
                        }else {

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

                    // call Eld cycle rules with priority wise. ( 1: Wired Obd,    2: Wifi OBD,    3: API call OBD data )
                    callCycleRulesWithPriority(getObdLastStatus,
                            isAlsNetworkConnected); //(getObdLastStatus == constants.WIRED_CONNECTED ||  getObdLastStatus == constants.BLE_CONNECTED)

                    Recap18DaysLog();

                }else{
                    SpeedCounter = SpeedCounter + LocRefreshTime;
                }


                // check WIFI connection
                if( getObdLastStatus != constants.WIRED_CONNECTED  &&
                        getObdLastStatus != Constants.BLE_CONNECTED &&
                        (isAlsNetworkConnected && IsOBDPingAllowed )){    // check ALS SSID connection with IsOBDPingAllowed permission

                    tcpClient.sendMessage("123456,can");
                    SharedPref.SaveConnectionInfo(constants.WifiOBD, Globally.GetCurrentDeviceDate(), getApplicationContext());


                }else{
                    SharedPref.SaveConnectionInfo(constants.DataMalfunction, "", getApplicationContext());
                }



                /*    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(SpeedCounter == HalfSpeedCounter || SpeedCounter == MaxSpeedCounter) {
                                if (isGpsEnabled)
                                    getLocation(true);
                            }
                        }
                    });
*/


                // saveExecutionTime("Loop");
                //  Debug.stopMethodTracing();

            } else {
                Log.e("Log", "--stop");
                StopService();

            }

        }
    };


    void saveActiveDriverData(){
        if(SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
            DriverType = Constants.MAIN_DRIVER_TYPE;
        }else{
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

        if(logArrayCount > 0) {

            if(logArrayCount < 3 ){
                socketTimeout = constants.SocketTimeout10Sec;  //10 seconds
            }else if(logArrayCount < 10){
                socketTimeout = constants.SocketTimeout20Sec;  //20 seconds
            }else{
                socketTimeout = constants.SocketTimeout40Sec;  //40 seconds
            }

            saveDriverLogPost.PostDriverLogData(driverLogArray, SavedLogApi, socketTimeout, false, false, DriverType, SaveMainDriverLogData);
        }

    }


    void callCycleRulesWithPriority(int ConnectionType, boolean isAlsNetworkConnected){

        try {
            String currentDateStr = Globally.GetCurrentDeviceDate();
            // 1st priority is Wired obd connection, after that wifi OBD and then API response OBD data
            if (ConnectionType == constants.WIRED_CONNECTED ) {    // check wired obd
                SharedPref.SaveConnectionInfo(constants.WiredOBD, currentDateStr, getApplicationContext());
            }else if(ConnectionType == constants.BLE_CONNECTED){
                SharedPref.SaveConnectionInfo(constants.BleObd, currentDateStr, getApplicationContext());
            } else if (isAlsNetworkConnected == false || IsOBDPingAllowed == false) {    // check ALS SSID connection with IsOBDPingAllowed permission

                // SharedPref.SetConnectionType(constants.ConnectionApi, getApplicationContext());

                String lastConnectionInfo = SharedPref.GetConnectionInfo(Constants.CONNECTION_TYPE, getApplicationContext());
                if (!lastConnectionInfo.equals(constants.WiredOBD) && !lastConnectionInfo.equals(constants.BleObd)) {
                    try {

                        if (global.isWifiOrMobileDataEnabled(getApplicationContext()) && constants.IsAlsServerResponding) {
// -------------------------- need to verify this check -------------------------
                            if (SpeedCounter != 40) {
                                Thread.sleep(2000);

                                if (obdVehicleSpeed != -1)
                                    VehicleSpeed = obdVehicleSpeed;

                                SharedPref.SaveConnectionInfo(constants.ApiData, currentDateStr, getApplicationContext());

                                String jobType = SharedPref.getDriverStatusId(getApplicationContext());
                                if ((jobType.equals(global.SLEEPER) || jobType.equals(global.OFF_DUTY)) && VehicleSpeed == 0) {
                                    // No need to call ELD rule
                                } else {
                                    serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                                            hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                                            odometerhMethod, isAlsNetworkConnected, constants.API, obdVehicleSpeed, GpsVehicleSpeed, obdUtil);

                                    saveDummyData("Status-Online, Wifi status-" + isAlsNetworkConnected, constants.ApiData);

                                }
                                //   global.ShowLocalNotification(getApplicationContext(), "API data", "API obd Speed: " + obdVehicleSpeed + ", Rule Vehicle Speed " + VehicleSpeed, 2009);
                            }

                        }
                        resetDataAfterCycleCall(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    if (SpeedCounter != 40 && !lastConnectionInfo.equals(constants.DataMalfunction)) {
                        saveObdData(constants.DataMalfunction, "", "", "",
                                "", "", "", "", "", "",
                                "", "", "");

                        SharedPref.SaveConnectionInfo(constants.DataMalfunction, currentDateStr, getApplicationContext());

                    }


                }
            } else {
                SharedPref.SaveConnectionInfo(constants.DataMalfunction, currentDateStr, getApplicationContext());
            }

       /* File wiredObdLog = Globally.GetWiredLogFile(ConstantsKeys.WIRED_OBD_LOG, "txt");
        if(wiredObdLog != null && wiredObdLog.exists())
            wiredObdLog.delete();*/

            // Sync wired OBD saved log to server (SAVE sync data service)
            obdUtil.syncObdLogData(getApplicationContext(), DriverId, getDriverName());


            // usage log record
        /*    String lastUsageTime = SharedPref.getLastUsageDataSavedTime(getApplicationContext());
            if (lastUsageTime.equals("")) {
                SaveDriverDeviceUsageLog(global.getCurrentDate());
            } else {
                DateTime lastUsageDateTime = global.getDateTimeObj(lastUsageTime, false);
                DateTime currentDateTime = global.getDateTimeObj(global.getCurrentDate(), false);

                int minDiff = Minutes.minutesBetween(lastUsageDateTime, currentDateTime).getMinutes();
                if (minDiff >= 30) {
                    SaveDriverDeviceUsageLog(global.getCurrentDate());
                }
            }*/
        }catch (Exception e){
            e.printStackTrace();
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
                stopService(bleDevice, characteristic);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                //  ------------- BLE OBD ----------
                  stopService(bleDevice, characteristic);
            }else {
                //  ------------- Wired OBD ----------
                if(isBound){
                    StartStopServer("stop");
                    this.unbindService(connection);
                    isBound = false;
                }
            }




        }

        SharedPref.setServiceOnDestoryStatus(true, getApplicationContext());
        Log.i(TAG, "---------onDestroy Service method");


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
            if(Globally.LATITUDE.equals("0.0") || Globally.LONGITUDE.equals("0.0")){
                createLocationRequest(MIN_TIME_LOCATION_UPDATES);
            }else {
                SaveLocation(Globally.LATITUDE, Globally.LONGITUDE);
            }
        }else{
            if(Globally.LATITUDE.equals("0.0") || Globally.LONGITUDE.equals("0.0")){
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
            int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
            if(ObdStatus == Constants.WIRED_CONNECTED ||
                    ObdStatus == Constants.WIFI_CONNECTED ||
                    ObdStatus == Constants.BLE_CONNECTED){
                odometer = SharedPref.getHighPrecisionOdometer(getApplicationContext());
            }
        }

        if(latitude.length() > 4 ){
            SharedPref.setEcmObdLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE, odometer, global.GetCurrentDateTime(), getApplicationContext());
        }else{
            if(SharedPref.getEcmObdLatitude(getApplicationContext()).length() > 4) {
                SharedPref.setEcmObdLocationWithTime("0", "0", odometer, global.GetCurrentDateTime(), getApplicationContext());
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // Log.d("onLocationChanged", "---Latitude: " + location.getLatitude() + " -- Longitude: " + location.getLongitude());
        Globally.LATITUDE = "" +location.getLatitude();
        Globally.LONGITUDE = "" +location.getLongitude();
        Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(Globally.LONGITUDE);

       // GpsVehicleSpeed = (int) location.getSpeed() * 18 / 5;
       // GpsVehicleSpeed = 21;

        int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
        if(ObdStatus == Constants.WIRED_CONNECTED || ObdStatus == Constants.WIFI_CONNECTED || ObdStatus == Constants.BLE_CONNECTED){
            saveEcmLocationWithTime(Globally.LATITUDE, SharedPref.getHighPrecisionOdometer(getApplicationContext()));
        }else{
            saveEcmLocationWithTime(Globally.LATITUDE, SharedPref.getHighPrecisionOdometer(getApplicationContext()));
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

    /*        if(tempVal == 0) {
                message = "*TS01,861107033607475,165700070720,CAN:UNCONNECTED#";
                tempVal++;
            }else {
                tempVal = 0;
                message = "*TS01,861107033607475,164710070720,CAN:UNCONNECTED#";
            }*/

            try {
                String rawResponse = message;
                String correctData = "";

                if (!message.equals(noObd) && message.length() > 10) {

                    SharedPref.setVehicleVin("", getApplicationContext());

                    if (message.contains("CAN")) {

                        if (message.contains("CAN:UNCONNECTED")) {
                            SharedPref.SetTruckIgnitionStatusForContinue("OFF", constants.WifiOBD, "", getApplicationContext());
                            SharedPref.setVehilceMovingStatus(false, getApplicationContext());

                            ignitionStatus = "false";
                            saveDummyData(rawResponse, constants.WifiOBD);

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

                                if (WheelBasedVehicleSpeed == 0) {
                                    WheelBasedVehicleSpeed = GPSSpeed;
                                }


                                truckRPM = wifiConfig.checkJsonParameter(canObj, "RPMEngineSpeed", "0");
                                ignitionStatus = wifiConfig.checkJsonParameter(canObj, "EngineRunning", "false");
                                obdTripDistance = wifiConfig.checkJsonParameter(canObj, "TripDistanceInKM", "0");
                                String latitude = wifiConfig.checkJsonParameter(canObj, "GPSLatitude", "0.0");
                                String longitude = wifiConfig.checkJsonParameter(canObj, "GPSLongitude", "0.0");
                                HighResolutionDistance = wifiConfig.checkJsonParameter(canObj, "HighResolutionTotalVehicleDistanceInKM", "-1");
                                obdOdometer = HighResolutionDistance;
                                obdEngineHours = wifiConfig.checkJsonParameter(canObj, "EngineHours", "0");

                                String vin = wifiConfig.checkJsonParameter(canObj, ConstantsKeys.VIN, "");
                                SharedPref.setVehicleVin(vin, getApplicationContext());
                                SharedPref.SetObdEngineHours(obdEngineHours, getApplicationContext());

                                // saving location with time info to calculate location mafunction event
                                saveEcmLocationWithTime(Globally.LATITUDE, HighResolutionDistance);

                                if (!latitude.equals("0")) {
                                    Globally.LATITUDE = latitude;
                                    Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(longitude);
                                }


                                if (ignitionStatus.equals("true")) {    // truckRpmInt > 0
                                    SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, global.getCurrentDate(), getApplicationContext());
                                    Globally.IS_OBD_IGNITION = true;
                                    continueStatusPromotForPcYm("ON", constants.WifiOBD, global.getCurrentDate(), Constants.WIFI_CONNECTED);

                                    if (WheelBasedVehicleSpeed > 200) {
                                        WheelBasedVehicleSpeed = 0;
                                    }

                                    obdVehicleSpeed = (int) WheelBasedVehicleSpeed;
                                    VehicleSpeed = obdVehicleSpeed;
                                    Globally.VEHICLE_SPEED = obdVehicleSpeed;

                                    saveWifiObdData("--", HighResolutionDistance, ignitionStatus, VehicleSpeed, obdTripDistance, rawResponse, correctData, true);


                                } else {
                                    SharedPref.setVehilceMovingStatus(false, getApplicationContext());
                                    SharedPref.SetTruckIgnitionStatusForContinue("OFF", constants.WifiOBD, "", getApplicationContext());
                                    Globally.IS_OBD_IGNITION = false;
                                    continueStatusPromotForPcYm(ignitionStatus, constants.WifiOBD, global.getCurrentDate(), Constants.WIFI_DISCONNECTED);

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
                            Log.d("OBD", ObdRestarted);
                            saveDummyData(ObdRestarted, constants.WifiOBD);
                        } else {
                            ignitionStatus = "false";
                            saveDummyData(rawResponse, constants.WifiOBD);
                        }
                    }

                } else {

                    if (RestartObdFlag || message.contains("RST")) {
                        Log.d("OBD", ObdRestarted);
                        saveDummyData(ObdRestarted, constants.WifiOBD);
                    } else {

                        if (!message.equals(noObd)) {
                            SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED, global.getCurrentDate(), getApplicationContext());
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

                // save WIfi obd info to sd card
                saveObdData(type, "--", obdOdometer, obdOdometer, rawResponse.trim() + ",  " + "--",
                        "", "--", ignitionStatus, "--", "--",
                        obdTripDistance, global.GetCurrentDateTime(), "--");

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


    private void saveWifiObdData(String vin, String HighPrecisionOdometer, String ignition, int speed, String tripDistance,
                                 String rawResponse, String correctedData,  boolean isSave){

        int DrivingSpeedLimit   = DriverConst.getDriverConfiguredTime(DriverConst.DrivingSpeed, getApplicationContext());
        double speedCalculated = -1;
        String savedTime = SharedPref.GetWifiObdSavedTime(getApplicationContext());
        String currentLogDate = global.GetCurrentDateTime();

        if(rawResponse.contains("CAN")) {
            if(SharedPref.isOdoCalculationAllowed(getApplicationContext())) {
                speedCalculated = calculateSpeedFromWifiObdOdometer(
                        savedTime,
                        SharedPref.GetWifiObdOdometer(getApplicationContext()),
                        HighPrecisionOdometer);
            }else{
                speedCalculated = speed;
            }
        }

        if(speedCalculated >= 8 || speed >= 8){
            SharedPref.setVehilceMovingStatus(true, getApplicationContext());
        }else{
            SharedPref.setVehilceMovingStatus(false, getApplicationContext());
        }


        SharedPref.SetWifiObdOdometer(HighResolutionDistance, currentLogDate, rawResponse, getApplicationContext());

        if(isSave) {
            if(SharedPref.isOdoCalculationAllowed(getApplicationContext())) {
                // save WIfi obd info to sd card
                saveObdData(constants.WifiOBD, vin, obdOdometer, HighPrecisionOdometer, rawResponse.trim() + ",  "+speedCalculated,
                        correctedData, ignition, truckRPM, String.valueOf(speed), String.valueOf((int)speedCalculated),
                        tripDistance, currentLogDate, savedTime);
            }else{
                // save WIfi obd info to sd card
                saveObdData(constants.WifiOBD, vin, obdOdometer, HighPrecisionOdometer, rawResponse.trim() + ", -1",
                        correctedData, ignition, truckRPM, String.valueOf(speed), String.valueOf(-1),
                        tripDistance, currentLogDate, savedTime);
            }


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
      // Log.d("timeInSec", "timeInSec: "+timeInSec);

        if( ( truckRpm > 600 && timeInSec >= 5 ) ) {    //|| rawResponse.contains("GPS")

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


                if (speed < DrivingSpeedLimit || (speedCalculated < DrivingSpeedLimit )) {
                    Log.d("ELD Rule", "Rule is correct.");
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

        //    global.ShowLocalNotification(getApplicationContext(), "wifi OBD speed: ", "Wifi obd Speed: " + speed, 2003);

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


    void SaveMalfnDiagnstcLogToServer(JSONArray malArray1){
        try{
            if(DriverId.length() > 0) {
                if(malArray1 == null) {
                    malArray1 = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(Integer.valueOf(DriverId), dbHelper);
                }
                if (global.isConnected(getApplicationContext()) && malArray1.length() > 0 && isMalfncDataAlreadyPosting == false) {
                    isMalfncDataAlreadyPosting = true;
                    saveDriverLogPost.PostDriverLogData(malArray1, APIs.MALFUNCTION_DIAGNOSTIC_EVENT, Constants.SocketTimeout30Sec, false, false, 1, SaveMalDiagnstcEvent);
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
                    if (UILApplication.isActivityVisible()) {
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
                                if (lastIgnitionStatus.equals("OFF") && obdCurrentIgnition.equals("ON")) {
                                    SharedPref.SetTruckStartLoginStatus(true, getApplicationContext());

                                    Intent intent = new Intent(ConstantsKeys.IsIgnitionOn);
                                    intent.putExtra(ConstantsKeys.IsIgnitionOn, true);
                                    LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);

                                }

                                SharedPref.SetTruckIgnitionStatusForContinue(obdCurrentIgnition, type, time, getApplicationContext());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //
            }

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

                    case SaveMalDiagnstcEvent:
                        Log.d("SaveMalDiagnstcEvent", "SaveMalDiagnstcEvent saved successfully");
                        isMalfncDataAlreadyPosting = false;
                        // clear malfunction array
                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(Integer.parseInt(DriverId), dbHelper, new JSONArray());
                        break;


                    case UpdateOffLineStatus:


                        //   Log.d("response", "UpdateOffLine: " + flag + ": " + response);
                        try {

                            boolean IsAutoSync = false;
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

                                    if (dataObj.has("IsOBDPingAllowed")) {
                                        IsOBDPingAllowed = dataObj.getBoolean("IsOBDPingAllowed");
                                        SharedPref.SetOBDPingAllowedStatus(IsOBDPingAllowed, getApplicationContext());
                                    }

                                    if (dataObj.has("IsAutoSync")) {
                                        IsAutoSync = dataObj.getBoolean("IsAutoSync");
                                    }

                                    if (dataObj.has("IsAutoOnDutyDriveEnabled")) {
                                        boolean IsAutoDrive = dataObj.getBoolean("IsAutoOnDutyDriveEnabled");
                                        SharedPref.SetAutoDriveStatus(IsAutoDrive, getApplicationContext());
                                    }

                                    try {
                                        // Save Truck information for manual/auto mode
                                        SharedPref.SetIsAOBRD(IsAOBRD, getApplicationContext());
                                        SharedPref.SetAOBRDAutomatic(dataObj.getBoolean(ConstantsKeys.IsAOBRDAutomatic), getApplicationContext());
                                        SharedPref.SetAOBRDAutoDrive(dataObj.getBoolean(ConstantsKeys.IsAutoDriving), getApplicationContext());
                                        SharedPref.SetDrivingShippingAllowed(dataObj.getBoolean(ConstantsKeys.IsDrivingShippingAllowed), getApplicationContext());
                                        SharedPref.saveTimeStampView(dataObj.getBoolean(ConstantsKeys.IsTimestampEnabled), getApplicationContext());
                                        SharedPref.setCurrentUTCTime(UtcCurrentDate, getApplicationContext());

                                        boolean isPU75Crossed = dataObj.getBoolean(ConstantsKeys.PersonalUse75Km);
                                        boolean wasPU75Crossed = SharedPref.isPersonalUse75KmCrossed(getApplicationContext());
                                       // isPU75Crossed = true; //------------------

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
                                                    dataObj.getBoolean(ConstantsKeys.IsMalfunction),
                                                    dataObj.getBoolean(ConstantsKeys.IsDiagnostic),
                                                    isSuggestedEdit, getApplicationContext());

                                            isSuggestedRecall = SharedPref.isSuggestedRecall(getApplicationContext());

                                            SharedPref.SetExemptDriverStatusMain(dataObj.getBoolean(ConstantsKeys.IsExemptDriver), getApplicationContext());
                                            SharedPref.SetCycleRequestStatusMain(IsCycleRequest, getApplicationContext());


                                        }else{
                                            SharedPref.setEldOccurencesCo(dataObj.getBoolean(ConstantsKeys.IsUnidentified),
                                                    dataObj.getBoolean(ConstantsKeys.IsMalfunction),
                                                    dataObj.getBoolean(ConstantsKeys.IsDiagnostic),
                                                    isSuggestedEdit, getApplicationContext());

                                            isSuggestedRecall = SharedPref.isSuggestedRecallCo(getApplicationContext());

                                            SharedPref.SetExemptDriverStatusCo(dataObj.getBoolean(ConstantsKeys.IsExemptDriver), getApplicationContext());
                                            SharedPref.SetCycleRequestStatusCo(IsCycleRequest, getApplicationContext());

                                        }


                                        if ( (isSuggestedEdit && isSuggestedRecall) || IsCycleRequest || IsELDNotification) {
                                            try {
                                                if(UILApplication.isActivityVisible()) {
                                                    Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                                                    intent.putExtra(ConstantsKeys.SuggestedEdit, isSuggestedEdit);
                                                    intent.putExtra(ConstantsKeys.IsCycleRequest, IsCycleRequest);
                                                    intent.putExtra(ConstantsKeys.IsELDNotification, IsELDNotification);
                                                    intent.putExtra(ConstantsKeys.DriverELDNotificationList, ELDNotification);
                                                    LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
                                                }
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

                                    obdVehicleSpeed = dataObj.getInt("VehicleSpeed");
                                    apiReturnedSpeed = dataObj.getString("VehicleSpeed");

                                    Globally.VEHICLE_SPEED = obdVehicleSpeed;

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

                                // -------- upload offline locally saved data ---------
                                UploadSavedShipmentData();
                                SaveCertifyLog();
                                SaveMalfnDiagnstcLogToServer(null);

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
                if(!status.equals(getApplicationContext().getResources().getString(R.string.screen_reset))) {
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
                }else if(msg.contains(getApplicationContext().getResources().getString(R.string.als_alert))){
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
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int driver_id, int flag) {
            // SaveDriverLog
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

                        if (driverLogArray.length() == 1) {
                            ClearLogAfterSuccess(driver_id);

                            // save Co Driver Data is login with co driver
                            SaveCoDriverData();
                        }else{

                            if(RePostDataCountMain > 1){
                                ClearLogAfterSuccess(driver_id);
                                RePostDataCountMain = 0;
                            }else {
                                saveActiveDriverData();
                                RePostDataCountMain ++;
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

                        //    constants.ClearNotifications(getApplicationContext());
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
                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(Integer.parseInt(DriverId), dbHelper, new JSONArray());
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
        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
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





    void DeviceLogout(String message){
        EldActivity activity = EldActivity.instance;

        global.ClearAllFields(getApplicationContext());
        StopService();

        if (activity != null) {

            global.ShowLocalNotification(getApplicationContext(), "ELD eBook", message, 2003);

            Intent i = new Intent(activity, LoginActivity.class);
            activity.startActivity(i);
            activity.finish();
        }
    }





}