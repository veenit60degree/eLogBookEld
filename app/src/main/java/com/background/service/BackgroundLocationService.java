package com.background.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.AsyncResponse;
import com.constants.CheckConnectivity;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.DownloadAppService;
import com.constants.DriverLogResponse;
import com.constants.RequestResponse;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
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
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.InspectionMethod;
import com.local.db.LatLongHelper;
import com.local.db.LocationMethod;
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
import com.models.EldDataModelNew;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
public class BackgroundLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, TextToSpeech.OnInitListener, LocationListener {

    int count = 0;
    String TAG = "Service";
    String TAG_OBD = "OBD Service";
    String noObd = "obd not connected";

    String obdOdometer = "0", obdTripDistance = "0", ignitionStatus = "OFF", HighResolutionDistance = "0", truckRPM = "0", apiReturnedSpeed = "";

    int GPSSpeed = 0;
    int timeInSec = -1;

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


    VolleyRequest GetOdometerRequest, ctPatInsp18DaysRequest, saveDriverDeviceUsageLog;

    int LocRefreshTime = 10;
    int CheckInternetConnection = 2;
    private static final long MIN_TIME_BW_UPDATES = 2 * 1000;   //30 sec. 30000 - 1/2 minute -- [960000 milli sec -- (16 minutes)]
    private static final long MIN_TIME_LOCATION_UPDATES = 5 * 1000;   // 5 sec
    private static final long OBD_TIME_LOCATION_UPDATES = 10 * 1000;   // 10 sec
    private static final long IDLE_TIME_LOCATION_UPDATES = 3600 * 1000;   // 1 hour
    VolleyRequest UpdateLocReqVolley, UpdateUserStatusVolley, GetRecapView18DaysData;
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
    Globally global;
    SharedPref sharedPref;

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
    File syncingFile = new File("");
    File DriverLogFile = new File("");


    // ---------- Wired OBD Client setup ----------

    String ServerPackage = "com.als.obd";
    String ServerService = "com.als.obd.services.MainService";

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




    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "---------onCreate Service");


     /*   StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                .permitDiskWrites()
                .build());
        StrictMode.setThreadPolicy(old);*/

        global                  = new Globally();
        sharedPref              = new SharedPref();
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

        MainDriverPref          = new MainDriverEldPref();
        CoDriverPref            = new CoDriverEldPref();
        constants               = new Constants();
        serviceCycle            = new ServiceCycle(getApplicationContext());
        postRequest             = new ShippingPost(getApplicationContext(), requestResponse);
        UpdateLocReqVolley      = new VolleyRequest(getApplicationContext());
        UpdateUserStatusVolley  = new VolleyRequest(getApplicationContext());
        GetRecapView18DaysData  = new VolleyRequest(getApplicationContext());
        mNotificationManager    = new NotificationManagerSmart(getApplicationContext());
        DeviceId                = sharedPref.GetSavedSystemToken(getApplicationContext());

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

        sharedPref.setServiceOnDestoryStatus(false, getApplicationContext());

        IsOBDPingAllowed =  sharedPref.isOBDPingAllowed(getApplicationContext());

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

        BindConnection();

        try{
            //  ------------- OBD Log write initilization----------
            obdUtil = new Utils(getApplicationContext());
            obdUtil.createLogFile();
        }catch (Exception e){
            e.printStackTrace();
        }


        try {
            obdUtil.createAppUsageLogFile();
            obdUtil.createExecTimeLogFile();
        }catch (Exception e){
            e.printStackTrace();
        }


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

    //  ------------- Wired OBD data response handler ----------
    private class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {

            Bundle bundle = msg.getData();

            String currentHighPrecisionOdometer = "0", timeStamp = "--", vin = "--";
            int speed = 0;

            try {
                timeStamp = bundle.getString(constants.OBD_TimeStamp);
                obdOdometer = bundle.getString(constants.OBD_Odometer);
                obdTripDistance = bundle.getString(constants.OBD_TripDistance);
                ignitionStatus = bundle.getString(constants.OBD_IgnitionStatus);
                truckRPM = bundle.getString(constants.OBD_RPM);
                vin = bundle.getString(constants.OBD_VINNumber);
                speed = bundle.getInt(constants.OBD_Vss);

                if(bundle.getString(constants.OBD_HighPrecisionOdometer) != null) {
                    currentHighPrecisionOdometer = bundle.getString(constants.OBD_HighPrecisionOdometer);
                }


            }catch (Exception e){
                e.printStackTrace();
            }

            sharedPref.setVss(speed, getApplicationContext());
            sharedPref.setRPM(truckRPM, getApplicationContext());
            sharedPref.SetWiredObdOdometer(obdOdometer, getApplicationContext());


            // ---------------- temp data ---------------------
            //  ignitionStatus = "ON"; truckRPM = "35436"; speed = 30;


            if(ignitionStatus.equals("ON")){
                global.IS_OBD_IGNITION = true;
                saveObdStatus("ON", constants.WiredOBD, global.getCurrentDate());
            }else{
                global.IS_OBD_IGNITION = false;
                saveObdStatus("OFF", constants.WiredOBD, "");
                sharedPref.SaveObdIgnitionStatus(false, global.getCurrentDate(), -1, getApplicationContext());

                if(ignitionStatus.equals("--")){
                    sharedPref.SaveObdStatus(Constants.NO_CONNECTION, getApplicationContext());
                }else {
                    sharedPref.SaveObdStatus(Constants.WIRED_INACTIVE, getApplicationContext());
                }

            }

            // ELD calling rule for Wired OBD
            /* Timer is calling after 10 sec. SpeedCounter value is 0,10,20,30,40,50,60. It is called 6 times in a minute.
                    In Driving time it is calling only once in a minute. */

            try {
                if (ignitionStatus.equals("ON") || !truckRPM.equals("0")) {

                    //  sharedPref.SetConnectionType(constants.ConnectionWired, getApplicationContext());
                    sharedPref.SaveObdStatus(Constants.WIRED_ACTIVE, getApplicationContext());
                    saveExecutionTime("Wired");

                    String jobType = sharedPref.getDriverStatusId("jobType", getApplicationContext());
                    double intHighPrecisionOdometerInKm = (Double.parseDouble(currentHighPrecisionOdometer) * 0.001);
                    double obdOdometerDouble  = Double.parseDouble(currentHighPrecisionOdometer);
                    String previousHighPrecisionOdometer = sharedPref.getHighPrecisionOdometer(getApplicationContext());

                    String savedDate = sharedPref.getHighPrecesionSavedTime(getApplicationContext());
                    String currentLogDate = global.GetCurrentDateTime();

                    if (savedDate.length() == 0 && obdOdometerDouble > 0) {
                        // save current HighPrecisionOdometer locally
                        savedDate = currentLogDate;
                        sharedPref.setHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());
                    }

                    // calculating speed to comparing last saved odometer and current odometer (in meter) with time differencein seconds
                    double calculatedSpeedFromOdo = constants.calculateSpeedFromWiredTabOdometer(savedDate, currentLogDate,
                            previousHighPrecisionOdometer, currentHighPrecisionOdometer, global, sharedPref, getApplicationContext());

                    // write wired OBD details in a text file and save into the SD card.
                    saveObdData(constants.WiredOBD, vin, obdOdometer, String.valueOf(intHighPrecisionOdometerInKm),
                            currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, String.valueOf(speed),
                            String.valueOf((int)calculatedSpeedFromOdo), obdTripDistance, timeStamp, savedDate);

                    double savedOdometer = Double.parseDouble(previousHighPrecisionOdometer);
                    if(obdOdometerDouble >= savedOdometer) {    // needs for this check is to avoid the wrong auto change status because some times odometers are not coming
                        if (calculatedSpeedFromOdo < 5 && speed < 5) {
                            if (sharedPref.getLastIgnitionStatus(getApplicationContext()) == true && sharedPref.getLastObdSpeed(getApplicationContext()) < 5) {
                                // ignore it
                            } else {
                                sharedPref.SaveObdIgnitionStatus(true, global.getCurrentDate(), speed, getApplicationContext());
                            }
                        }

                        if (jobType.equals(global.DRIVING)) {

                            if (SpeedCounter == 10) {

                                // save current HighPrecisionOdometer in DB
                                sharedPref.setHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());

                                if (speed >= 10 && calculatedSpeedFromOdo >= 10) {

                                    callEldRuleForWired(speed, (int) calculatedSpeedFromOdo);

                                } else if (speed < 10 && calculatedSpeedFromOdo < 10) {

                                    callEldRuleForWired(speed, (int) calculatedSpeedFromOdo);

                                }
                            }


                        } else if (jobType.equals(global.ON_DUTY)) {


                            // if speed is coming >10 then ELD rule is called after 10 sec to change the status to Driving as soon as.
                            if (speed > 10 && calculatedSpeedFromOdo > 10) {

                                // save current HighPrecisionOdometer locally
                                sharedPref.setHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());

                                callEldRuleForWired(speed, (int) calculatedSpeedFromOdo);

                            } else {
                                // call ELD rule after 1 minute to improve performance
                                if (minDiff(savedDate) > 0) {

                                    // save current HighPrecisionOdometer locally
                                    sharedPref.setHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());

                                    callEldRuleForWired(speed, (int) calculatedSpeedFromOdo);
                                }
                            }

                        } else {

                            // =================== For OFF Duty & Sleeper case =====================


                            // save current HighPrecisionOdometer in DB
                            sharedPref.setHighPrecisionOdometer(currentHighPrecisionOdometer, currentLogDate, getApplicationContext());


                            if (speed <= 0 && calculatedSpeedFromOdo <= 0) {
                                Log.d("ELD Rule", "data is correct for this status. No need to call ELD rule.");
                            } else {
                                if (speed > 10 && calculatedSpeedFromOdo > 10) {    //if speed is coming >10 then ELD rule is called after 10 sec to change the status to Driving as soon as.
                                    callEldRuleForWired(speed, (int) calculatedSpeedFromOdo);
                                }
                            }
                        }

                        if (SpeedCounter == 0 || SpeedCounter == HalfSpeedCounter) {
                            resetDataAfterCycleCall(true);
                        } else {
                            resetDataAfterCycleCall(false);
                        }

                        global.VEHICLE_SPEED = speed;

                    }


                } else {
                    global.VEHICLE_SPEED = -1;
                }
            }catch (Exception e){
                e.printStackTrace();
                global.IS_OBD_IGNITION = false;
                global.VEHICLE_SPEED = -1;
            }
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


    private int minDiff(String savedTime){

        int timeInMin = 0;
        if(savedTime.length() > 10) {
            try{
                String timeStampStr = savedTime.replace(" ", "T");
                DateTime savedDateTime = global.getDateTimeObj(timeStampStr, false);
                DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);

                timeInMin = Minutes.minutesBetween(savedDateTime, currentDateTime).getMinutes();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return timeInMin;

    }





    void callEldRuleForWired( int speed, int calculatedSpeedFromOdo){
        // call cycle rule
        VehicleSpeed = speed;
        obdVehicleSpeed = calculatedSpeedFromOdo;
        serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                odometerhMethod, true, constants.WIRED_OBD, obdVehicleSpeed, GpsVehicleSpeed);

        //   global.ShowLocalNotification(getApplicationContext(), "Wired OBD data", status + ", Speed " + VehicleSpeed, 2009);


    }




    private void saveObdData(String source, String vin, String odometer, String HighPrecisionOdometer,
                             String obdOdometerInMeter, String correctedData, String ignition, String rpm,
                             String speed, String speedCalculated, String tripDistance, String timeStamp,
                             String previousDate){

        boolean isDeviceLogEnabled = driverPermissionMethod.isDeviceLogEnabled(DriverId, dbHelper);

        if(isDeviceLogEnabled) {
            JSONObject obj = new JSONObject();
            // String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
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
                obj.put(ConstantsKeys.Latitude, global.LATITUDE);
                obj.put(ConstantsKeys.Longitude, global.LONGITUDE);

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

            global.LATITUDE = "" +location.getLatitude();
            global.LONGITUDE = "" +location.getLongitude();
            global.LONGITUDE = Globally.CheckLongitudeWithCycle(global.LONGITUDE);
            GpsVehicleSpeed = (int) location.getSpeed() * 18 / 5;

            isGpsUpdate = true;


/*            final Date date = new Date(location.getTime());
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sDate = sdf.format(date);

            Log.d("Location", "---time: " + sDate );*/
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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "---------onStartCommand Service");

        UpdateDriverInfo();
        // getLocation(false);

        if (!sharedPref.getUserName(getApplicationContext()).equals("") &&
                !sharedPref.getPassword(getApplicationContext()).equals("")) {
            try {
                JSONArray driverLogArray = GetDriversSavedArray();
                if (global.isWifiOrMobileDataEnabled(getApplicationContext()) && driverLogArray.length() == 0) {  // sharedPrefDriverLog.GetOfflineData(getApplicationContext()) == true) {  // This check is used to save offline saved data to server first then online status will be changed.
                    String VIN = SharedPref.getVINNumber(getApplicationContext());
                    UpdateOfflineDriverLog(DriverId, CoDriverId, DeviceId, VIN,
                            String.valueOf(GpsVehicleSpeed),
                            String.valueOf(obdVehicleSpeed),
                            constants.CheckGpsStatus(getApplicationContext()));


                    JSONArray ctPatInsp18DaysArray = ctPatInspectionMethod.getCtPat18DaysInspectionArray(Integer.valueOf(DriverId), dbHelper);
                    if(ctPatInsp18DaysArray.length() == 0){
                        String  SelectedDate = Globally.GetCurrentDeviceDate();

                        if(sharedPref.getDriverType( getApplicationContext() ).equals(DriverConst.TeamDriver)) {
                            GetCtPatInspection18Days(DriverId, DeviceId, SelectedDate, GetCtPat18DaysMainDriverLog );
                            GetCtPatInspection18Days(CoDriverId, DeviceId, SelectedDate, GetCtPat18DaysCoDriverLog );
                        }else{
                            GetCtPatInspection18Days(DriverId, DeviceId, SelectedDate, GetCtPat18DaysMainDriverLog );
                        }

                    }


                } else {
                    global.VEHICLE_SPEED = -1;
                    VehicleSpeed = GpsVehicleSpeed;
                }
            } catch (Exception e) {
            }

            Recap18DaysLog();
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
                global.LATITUDE = "" + loc.getLatitude();
                global.LONGITUDE = "" + loc.getLongitude();
                global.LONGITUDE = Globally.CheckLongitudeWithCycle(global.LONGITUDE);
            } else {
                global.LATITUDE = "0.0";
                global.LONGITUDE = "0.0";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            // Log.e(TAG, "-----Running timerTask");

            if (!sharedPref.getUserName(getApplicationContext()).equals("") &&
                    !sharedPref.getPassword(getApplicationContext()).equals("")) {

                //  Debug.startMethodTracing("eld-" + Globally.GetCurrentDateTime());

                processStartTime = -1;
                if (SpeedCounter == HalfSpeedCounter || SpeedCounter >= MaxSpeedCounter) {
                    processStartTime = System.currentTimeMillis();
                }

                // communicate with wired OBD server app with Message
                StartStopServer(constants.WiredOBD);


                try {
                    if( global.LATITUDE.equals("0.0") ||  global.LATITUDE.equals("") ||  global.LATITUDE.equals("null")){

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

                final boolean isGpsEnabled = constants.CheckGpsStatus(getApplicationContext());
                if(!isGpsEnabled){
                    GpsVehicleSpeed = -1;
                    global.LATITUDE = "0.0";
                    global.LONGITUDE = "0.0";
                }

                boolean isAlsNetworkConnected   = wifiConfig.IsAlsNetworkConnected(getApplicationContext());  // get ALS Wifi ssid availability
                boolean isWiredObdConnected     = false;
                if(isBound && ( ignitionStatus.equals("ON") && !truckRPM.equals("0") ) ) {
                    isWiredObdConnected = true;
                }


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
                                    driverLogArray = GetDriversSavedArray();
                                    if (driverLogArray.length() == 0) {   // This check is used to save offline saved data to server first then online status will be changed.
                                        String VIN = sharedPref.getVINNumber(getApplicationContext());

                                        UpdateOfflineDriverLog(DriverId, CoDriverId, DeviceId, VIN,
                                                String.valueOf(GpsVehicleSpeed),
                                                String.valueOf(obdVehicleSpeed), isGpsEnabled);

                                    } else {
                                        VehicleSpeed = GpsVehicleSpeed;
                                        global.VEHICLE_SPEED = -1;

                                        if(UILApplication.isActivityVisible() && Constants.IS_ACTIVE_ELD){
                                            updateOfflineApiRejectionCount++;
                                            if (updateOfflineApiRejectionCount > 1) {
                                                updateOfflineApiRejectionCount = 0;
                                                constants.IsAlsServerResponding = false;
                                            }
                                        }else {

                                            saveActiveDriverData();
                                        }

                                    }
                                }
                            }else{
                                global.VEHICLE_SPEED = -1;
                                VehicleSpeed = GpsVehicleSpeed;
                                updateOfflineApiRejectionCount++;
                                if(updateOfflineApiRejectionCount > 5){
                                    updateOfflineApiRejectionCount = 0;
                                    constants.IsAlsServerResponding = true;
                                }else{
                                    if(updateOfflineApiRejectionCount == 2){
                                        CheckConnectivity CheckConnectivity = new CheckConnectivity(getApplicationContext());
                                        CheckConnectivity.ConnectivityRequest(CheckInternetConnection, ConnectivityInterface);
                                    }
                                }

                                // if last status was online then save offline status
                                boolean connectionStatus = sharedPref.isOnline(getApplicationContext());
                                if(connectionStatus){
                                    constants.saveAppUsageLog(ConstantsEnum.StatusOffline,  false, false, obdUtil);
                                }
                                sharedPref.setOnlineStatus(false, getApplicationContext());

                            }
                        }else {

                            constants.IsAlsServerResponding = true;
                            VehicleSpeed = GpsVehicleSpeed;

                            // if last status was online then save offline status
                            boolean connectionStatus = sharedPref.isOnline(getApplicationContext());
                            if(connectionStatus){
                                constants.saveAppUsageLog(ConstantsEnum.StatusOffline,  false, false, obdUtil);
                            }
                            sharedPref.setOnlineStatus(false, getApplicationContext());


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // call Eld cycle rules with priority wise. ( 1: Wired Obd,    2: Wifi OBD,    3: API call OBD data )
                    callCycleRulesWithPriority(isWiredObdConnected, isAlsNetworkConnected);

                    Recap18DaysLog();

                }else{
                    SpeedCounter = SpeedCounter + LocRefreshTime;
                }


                // check WIFI connection
                if( !isWiredObdConnected && (isAlsNetworkConnected && IsOBDPingAllowed )){    // check ALS SSID connection with IsOBDPingAllowed permission

                    tcpClient.sendMessage("123456,can");
                    sharedPref.SaveConnectionInfo(constants.WifiOBD, Globally.GetCurrentDeviceDate(), getApplicationContext());


                }else{
                    sharedPref.SaveConnectionInfo(constants.DataMalfunction, "", getApplicationContext());
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


                saveExecutionTime("Loop");
                //  Debug.stopMethodTracing();

            } else {
                Log.e("Log", "--stop");
                StopService();

            }

        }
    };


    void saveActiveDriverData(){
        if(sharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
            DriverType = 0;
        }else{
            DriverType = 1;
        }

        String SavedLogApi = "";
        if (sharedPref.IsEditedData(getApplicationContext())) {
            SavedLogApi = APIs.SAVE_DRIVER_EDIT_LOG;
        } else {
            SavedLogApi = APIs.SAVE_DRIVER_STATUS;
        }

        int socketTimeout;
        int logArrayCount = driverLogArray.length();
        if(logArrayCount < 3 ){
            socketTimeout = constants.SocketTimeout10Sec;  //10 seconds
        }else if(logArrayCount < 10){
            socketTimeout = constants.SocketTimeout20Sec;  //20 seconds
        }else{
            socketTimeout = constants.SocketTimeout40Sec;  //40 seconds
        }

        saveDriverLogPost.PostDriverLogData(driverLogArray, SavedLogApi, socketTimeout, false, false, DriverType, SaveMainDriverLogData);

    }


    void callCycleRulesWithPriority(boolean isWiredObdConnected, boolean isAlsNetworkConnected){

        String currentDateStr = Globally.GetCurrentDeviceDate();

        // 1st priority is Wired obd connection, after that wifi OBD and then API response OBD data
        if(isWiredObdConnected){    // check wired obd
            // already called above each 10 sec
            sharedPref.SaveConnectionInfo(constants.WiredOBD, currentDateStr , getApplicationContext());

        }else if(isAlsNetworkConnected == false || IsOBDPingAllowed == false){    // check ALS SSID connection with IsOBDPingAllowed permission

            // sharedPref.SetConnectionType(constants.ConnectionApi, getApplicationContext());

            String lastConnectionInfo = sharedPref.GetConnectionInfo(Constants.CONNECTION_TYPE, getApplicationContext());
            if(!lastConnectionInfo.equals(constants.WiredOBD) && !lastConnectionInfo.equals(constants.WifiOBD) ) {   // && !lastConnectionInfo.equals(constants.WifiOBD)
                try {

                    Thread.sleep(3000);

                    if (global.isWifiOrMobileDataEnabled(getApplicationContext()) && constants.IsAlsServerResponding) {

                        if (SpeedCounter != 40) {

                            if (obdVehicleSpeed != -1)
                                VehicleSpeed = obdVehicleSpeed;

                            sharedPref.SaveConnectionInfo(constants.ApiData, currentDateStr, getApplicationContext());

                            String jobType = sharedPref.getDriverStatusId("jobType", getApplicationContext());
                            if( (jobType.equals(global.SLEEPER) || jobType.equals(global.OFF_DUTY)) && VehicleSpeed == 0){
                                // No need to call ELD rule
                            }else {
                                serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                                        hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                                        odometerhMethod, isAlsNetworkConnected, constants.API, obdVehicleSpeed, GpsVehicleSpeed);

                                saveDummyData("Status-Online, Wifi status-"+isAlsNetworkConnected, constants.ApiData);

                            }
                            //   global.ShowLocalNotification(getApplicationContext(), "API data", "API obd Speed: " + obdVehicleSpeed + ", Rule Vehicle Speed " + VehicleSpeed, 2009);
                        }

                    } else {
                        VehicleSpeed = GpsVehicleSpeed;

                        sharedPref.SaveConnectionInfo(constants.OfflineData, currentDateStr, getApplicationContext());

                        String jobType = sharedPref.getDriverStatusId("jobType", getApplicationContext());
                        if( (jobType.equals(global.SLEEPER) || jobType.equals(global.OFF_DUTY)) && VehicleSpeed == 0){
                            // No need to call ELD rule
                        }else {
                            serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                                    hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                                    odometerhMethod, isAlsNetworkConnected, constants.OFFLINE, obdVehicleSpeed, GpsVehicleSpeed);

                            saveDummyData("Status-Offline, Wifi status-"+isAlsNetworkConnected, constants.OfflineData);
                        }
                        //   global.ShowLocalNotification(getApplicationContext(), "OFFLINE data", "Vehicle Speed " + VehicleSpeed, 2009);
                    }


                    resetDataAfterCycleCall(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{

                if (SpeedCounter != 40 && !lastConnectionInfo.equals(constants.DataMalfunction)) {
                    saveObdData(constants.DataMalfunction, "", "", "",
                            "", "", "", "", "", "",
                            "", "","");

                    sharedPref.SaveConnectionInfo(constants.DataMalfunction, currentDateStr, getApplicationContext());

                }


            }
        }else{
            sharedPref.SaveConnectionInfo(constants.DataMalfunction, currentDateStr, getApplicationContext());
        }

       /* File wiredObdLog = Globally.GetWiredLogFile(ConstantsKeys.WIRED_OBD_LOG, "txt");
        if(wiredObdLog != null && wiredObdLog.exists())
            wiredObdLog.delete();*/

        // Sync wired OBD saved log to server (SAVE sync data service)
        obdUtil.syncObdLogData(getApplicationContext(), DriverId, getDriverName());


        String lastUsageTime = sharedPref.getLastUsageDataSavedTime(getApplicationContext());
        if(lastUsageTime.equals("")){
            SaveDriverDeviceUsageLog(global.getCurrentDate());
        }else{
            DateTime lastUsageDateTime = global.getDateTimeObj(lastUsageTime, false);
            DateTime currentDateTime = global.getDateTimeObj(global.getCurrentDate(), false);

            int minDiff = Minutes.minutesBetween(lastUsageDateTime, currentDateTime).getMinutes();
            //  Log.d("dateTime", "joda Min diff: " + minDiff);

            if(minDiff >= 30){
                SaveDriverDeviceUsageLog(global.getCurrentDate());
            }

        }

    }




    // -------------- Reset OBD speed as default ----------------
    private void resetDataAfterCycleCall(boolean isCalled){

        CompareLocVal = "";
        VehicleSpeed = -1;
        GpsVehicleSpeed = VehicleSpeed;
        obdVehicleSpeed = VehicleSpeed;
        sharedPref.setVss(VehicleSpeed, getApplicationContext());

        if(isCalled) {
            IsLogApiACalled = true;
            //  SetLocIntervalForBatterySaver();
        }

    }



    void SetLocIntervalForBatterySaver(){
        try {

            JSONArray driver18DaysLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);

            int arrayLength = driver18DaysLogArray.length();

            if(arrayLength > 0){
                JSONObject lastJsonItem         = (JSONObject) driver18DaysLogArray.get(arrayLength - 1);
                DateTime previousDateTime       = global.getDateTimeObj(lastJsonItem.getString(ConstantsKeys.startDateTime), false);
                DateTime currentDateTime        = global.getDateTimeObj(global.getCurrentDate(), false);

                int DRIVER_JOB_STATUS = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
                long CURRENT_TIME_INTERVAL = locationRequest.getFastestInterval();

                if(DRIVER_JOB_STATUS == 1) {

                    if(CURRENT_TIME_INTERVAL != IDLE_TIME_LOCATION_UPDATES) {
                        int MinDiff = currentDateTime.getMinuteOfDay() - previousDateTime.getMinuteOfDay();

                        if(MinDiff > 30){   // 30 min. After 30 min idle time changed from 2 sec to 1 hour for battery saver.

                            if (global.checkPlayServices(getApplicationContext())) {
                                createLocationRequest(IDLE_TIME_LOCATION_UPDATES);
                            }else{
                                requestLocationWithoutPlayServices();
                            }

                        }
                    }

                    //  Log.d("FastestInterval", "FastestInterval: " + CURRENT_TIME_INTERVAL);
                }else{

                    if( CURRENT_TIME_INTERVAL != MIN_TIME_LOCATION_UPDATES) {
                        if (global.checkPlayServices(getApplicationContext())) {
                            createLocationRequest(MIN_TIME_LOCATION_UPDATES);
                        }else{
                            requestLocationWithoutPlayServices();
                        }
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
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
            if(sharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)){
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
            driverName = Slidingmenufunctions.usernameTV.getText().toString();
        }catch (Exception e){
            e.printStackTrace();

            driverName = getDriverName();
            //  Log.d("driverName", "--driverName: " + driverName);
        }


        try {
            if (sharedPref.getDriverType(getApplicationContext()).equals(DriverConst.SingleDriver)) {
                DriverId        = sharedPref.getDriverId(getApplicationContext());
                CoDriverId      = "";
                DriverType      = 0;
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
                DriverType = 1;
                sharedPref.setDriverId(DriverId, getApplicationContext());

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        //   Log.d("DriverId", "DriverId: " + DriverId);

    }


    void UpdateDriverInfo(){

        getDriverIDs();

        if (sharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
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

        VIN_NUMBER  = sharedPref.getVINNumber(getApplicationContext());
        VehicleId   = sharedPref.getVehicleId(getApplicationContext());

        DateTime currentSavedDate = new DateTime();
        String currentSavedTime = sharedPref.getSystemSavedDate(getApplicationContext());
        if(currentSavedTime.length() > 10){
            currentSavedDate = new DateTime(global.getDateTimeObj(currentSavedTime, false));
            currentSavedDate = currentSavedDate.plusMinutes(1);
            sharedPref.setCurrentDate(currentSavedDate.toString(), getApplicationContext());
        }else{
            offsetFromUTC = (int) global.GetTimeZoneOffSet();
            if(offsetFromUTC == offSetFromServer) {
                currentSavedDate = new DateTime(global.getDateTimeObj(global.GetCurrentDateTime(), false)); //GetCurrentUTCTimeFormat
                sharedPref.setCurrentDate(currentSavedDate.toString(), getApplicationContext());
            }
        }


        //  Log.d("currentSavedDate", "----currentSavedDate: " + currentSavedDate.toString());
    }


    void Recap18DaysLog(){
        try {

            JSONArray recapArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DriverId), dbHelper);
            if(recapArray == null || recapArray.length() == 0) {
                if(!IsRecapApiACalled) {

                    if(sharedPref.getDriverType( getApplicationContext() ).equals(DriverConst.TeamDriver)){
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


    public void onDestroy() {

        if(!isStopService) {

            Log.d("---onDestroy service_re", ConstantsEnum.StatusAppKilled );
            // save service status log
            constants.saveAppUsageLog(ConstantsEnum.StatusAppKilled, false, false, obdUtil);

            Intent intent = new Intent(constants.packageName);
            intent.putExtra("location", "torestore");
            sendBroadcast(intent);
        }else{

            Log.d("---onDestroy service ", ConstantsEnum.StatusServiceStopped );
            // save service status log
            constants.saveAppUsageLog(ConstantsEnum.StatusServiceStopped,  false, false, obdUtil);

            try {
                StopLocationUpdates();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //  ------------- Wired OBD ----------
            if(isBound){
                StartStopServer("stop");
                this.unbindService(connection);
                isBound = false;
            }

        }

        sharedPref.setServiceOnDestoryStatus(true, getApplicationContext());
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
        params.put("DriverId", DriverId);
        params.put("CoDriverId", CoDriverId);
        params.put("DeviceId", DeviceId);
        params.put("VIN", VIN );

        params.put("GPSSpeed", GpsSpeed);
        params.put("obdSpeed", obdSpeed);
        params.put("isGpsEnabled", String.valueOf(isGpsEnabled) );

        UpdateUserStatusVolley.executeRequest(Request.Method.POST, APIs.UPDATE_OFF_LINE_DRIVER_LOG , params, UpdateOffLineStatus,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);
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
            params.put("DriverId", DriverId);
            params.put("DeviceId", DeviceId);
            params.put("ProjectId", global.PROJECT_ID);
            params.put("DrivingStartTime", StartDate);
            params.put("DriverLogDate", EndDate);

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
                sharedPref.SetOdoSavingStatus(true, getApplicationContext());
                postRequest.PostListingData(odometerArray, APIs.SAVE_ODOMETER_OFFLINE, SaveOdometerOffline );
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        String id = "";
        if(sharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)) {
            if (sharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
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



    public JSONArray GetDriversSavedArray(){
        int listSize = 0;
        JSONArray DriverJsonArray = new JSONArray();
        List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

        if(sharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {   // Main Driver
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
                        constants.SaveEldJsonToList(          /* Put data as JSON to List */
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



    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

    }



    void getLocation(boolean isSave){
        if(isSave) {
            if(global.LATITUDE.equals("0.0") || global.LONGITUDE.equals("0.0")){
                createLocationRequest(MIN_TIME_LOCATION_UPDATES);
            }else {
                SaveLocation(global.LATITUDE, global.LONGITUDE);
            }
        }else{
            if(global.LATITUDE.equals("0.0") || global.LONGITUDE.equals("0.0")){
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

    @Override
    public void onLocationChanged(Location location) {
        global.LATITUDE = "" +location.getLatitude();
        global.LONGITUDE = "" +location.getLongitude();
        global.LONGITUDE = Globally.CheckLongitudeWithCycle(global.LONGITUDE);

        GpsVehicleSpeed = (int) location.getSpeed() * 18 / 5;




       /* final Date date = new Date(location.getTime());
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDate = sdf.format(date);

        Log.d("Location", "---time: " + sDate );*/
        //  Log.d("Location speed", "Current speed: " + GpsVehicleSpeed );
        //   global.ShowLocalNotification(getApplicationContext(), "onLocationChanged", "Speed is: " + GpsVehicleSpeed, 2003);


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

                    if (message.contains("CAN")) {

                        if (message.contains("CAN:UNCONNECTED")) {
                            sharedPref.SaveObdStatus(Constants.WIFI_INACTIVE, getApplicationContext());
                            sharedPref.SaveObdIgnitionStatus(false, global.getCurrentDate(), -1, getApplicationContext());

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
                                // int truckRpmInt = Integer.valueOf(truckRPM);

                                if (!latitude.equals("0")) {
                                    global.LATITUDE = latitude;
                                    global.LONGITUDE = Globally.CheckLongitudeWithCycle(longitude);
                                }


                                if (ignitionStatus.equals("true")) {    // truckRpmInt > 0
                                    sharedPref.SaveObdStatus(Constants.WIFI_ACTIVE, getApplicationContext());
                                    global.IS_OBD_IGNITION = true;
                                    saveObdStatus("ON", constants.WifiOBD, global.getCurrentDate());

                                    if (WheelBasedVehicleSpeed > 200) {
                                        WheelBasedVehicleSpeed = 0;
                                    }

                                    obdVehicleSpeed = (int) WheelBasedVehicleSpeed;
                                    VehicleSpeed = obdVehicleSpeed;
                                    global.VEHICLE_SPEED = obdVehicleSpeed;

                                    saveWifiObdData("--", HighResolutionDistance, ignitionStatus, VehicleSpeed, obdTripDistance, rawResponse, correctData, true);


                                } else {
                                    sharedPref.SaveObdStatus(Constants.WIFI_INACTIVE, getApplicationContext());
                                    global.IS_OBD_IGNITION = false;
                                    saveObdStatus(ignitionStatus, constants.WifiOBD, global.getCurrentDate());

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
                    } else if (message.contains("GPS")) {

                        try {
                            String[] responseArray = message.split("GPS");
                            if (responseArray.length > 1) {
                                String gpsData = responseArray[1];
                                String[] gpsArray = gpsData.split(";");
                                if (gpsArray.length > 3) {
                                    global.LATITUDE = gpsArray[1].substring(1, gpsArray[1].length());
                                    global.LONGITUDE = gpsArray[2].substring(1, gpsArray[2].length());
                                    global.LONGITUDE = Globally.CheckLongitudeWithCycle(global.LONGITUDE);

                                    GPSSpeed = Integer.valueOf(gpsArray[3]);

                                    obdVehicleSpeed = GPSSpeed;
                                    VehicleSpeed = GPSSpeed;
                                    global.VEHICLE_SPEED = GPSSpeed;

                                    obdOdometer = sharedPref.GetWifiObdOdometer(getApplicationContext());

                                    saveWifiObdData("--", HighResolutionDistance, ignitionStatus, VehicleSpeed, obdTripDistance, rawResponse, correctData, true);

                                }
                            } else {

                                if (obdVehicleSpeed == -1) {
                                    VehicleSpeed = GpsVehicleSpeed;
                                } else {
                                    VehicleSpeed = obdVehicleSpeed;
                                }

                                correctData = "Android device GPS decesion";
                                saveWifiObdData("--", HighResolutionDistance, ignitionStatus, VehicleSpeed, obdTripDistance, rawResponse, correctData, true);


                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            ignitionStatus = "false";
                            saveDummyData("GPS Exception: " + e.toString(), constants.WifiOBD);

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
                        ignitionStatus = "false";
                        saveDummyData(rawResponse, constants.WifiOBD);

                        String lastRestartTime = sharedPref.getOBDRestartTime(getApplicationContext());
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
        sharedPref.SetOBDRestartTime( global.getCurrentDate(), getApplicationContext());
        tcpClient.sendMessage("123456,rst");
    }

    void saveDummyData(String rawResponse, String type){
        HighResolutionDistance = "0";  obdTripDistance = "--";    RestartObdFlag = false;
        String savedRawData = sharedPref.GetObdRawData(getApplicationContext());

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
            HighResolutionDistance = sharedPref.GetWifiObdOdometer(getApplicationContext());

            // Sync wired OBD saved log to server (SAVE sync data service)
            obdUtil.syncObdSingleLog(getApplicationContext(), DriverId, getDriverName(), 10);

        }

        sharedPref.SetWifiObdOdometer(HighResolutionDistance, Globally.GetCurrentDateTime(), rawResponse.trim(), getApplicationContext());

    }


    private void saveWifiObdData(String vin, String HighPrecisionOdometer, String ignition, int speed, String tripDistance,
                                 String rawResponse, String correctedData,  boolean isSave){

        int DrivingSpeedLimit   = DriverConst.getDriverConfiguredTime(DriverConst.DrivingSpeed, getApplicationContext());
        double speedCalculated = -1;
        String savedTime = sharedPref.GetWifiObdSavedTime(getApplicationContext());
        String currentLogDate = global.GetCurrentDateTime();

        if(rawResponse.contains("CAN")) {
            speedCalculated = calculateSpeedFromWifiObdOdometer(
                    savedTime,
                    sharedPref.GetWifiObdOdometer(getApplicationContext()),
                    HighPrecisionOdometer);

            if(speedCalculated < 5 && speed < 5){
                if(sharedPref.getLastIgnitionStatus(getApplicationContext()) == true && sharedPref.getLastObdSpeed(getApplicationContext()) < 5)  {
                    // ignore it
                }else{
                    sharedPref.SaveObdIgnitionStatus(true, global.getCurrentDate(), speed, getApplicationContext());
                }
            }
        }

        sharedPref.SetWifiObdOdometer(HighResolutionDistance, currentLogDate, rawResponse, getApplicationContext());

        if(isSave) {
            // save WIfi obd info to sd card
            saveObdData(constants.WifiOBD, vin, obdOdometer, HighPrecisionOdometer, rawResponse.trim() + ",  "+speedCalculated,
                    correctedData, ignition, truckRPM, String.valueOf(speed), String.valueOf((int)speedCalculated),
                    tripDistance, currentLogDate, savedTime);
        }

        String jobType = sharedPref.getDriverStatusId("jobType", getApplicationContext());
        String savedDate = sharedPref.getHighPrecesionSavedTime(getApplicationContext());

        if(savedDate.length() == 0) {
            // save current HighPrecisionOdometer locally
            savedDate = currentLogDate;
            sharedPref.setHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());
        }



        double truckRpm = 0;

        try{
            truckRpm = Double.parseDouble(truckRPM);
        }catch (Exception e){}
        Log.d("timeInSec", "timeInSec: "+timeInSec);

        if( ( truckRpm > 600 && timeInSec >= 5 ) || rawResponse.contains("GPS")) {

            if (jobType.equals(global.DRIVING)) {

                if (speed >= DrivingSpeedLimit || speedCalculated >= DrivingSpeedLimit ) {

                    if(speed == 0){
                        obdVehicleSpeed      = (int)speedCalculated;
                        VehicleSpeed         = obdVehicleSpeed;
                        global.VEHICLE_SPEED =  obdVehicleSpeed;
                    }

                    ServiceCycle.ContinueSpeedCounter = 0;
                    if (minDiff(savedDate) > 1) {  //&& !HighPrecisionOdometer.equals(sharedPref.getHighPrecisionOdometer(getApplicationContext()))
                        sharedPref.setHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());
                        serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                                hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                                odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed);
                    }

                } else {

                    if (minDiff(savedDate) > 0) {
                        sharedPref.setHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());
                        serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                                hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                                odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed);
                    }
                }

            } else if (jobType.equals(global.ON_DUTY)) {

                if (speed >= DrivingSpeedLimit || speedCalculated >= DrivingSpeedLimit ) {
                    if(speed == 0){
                        obdVehicleSpeed      = (int)speedCalculated;
                        VehicleSpeed         = obdVehicleSpeed;
                        global.VEHICLE_SPEED =  obdVehicleSpeed;
                    }

                    sharedPref.setHighPrecisionOdometer(HighPrecisionOdometer,currentLogDate, getApplicationContext());
                    serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                            hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                            odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed);

                } else {

                    ServiceCycle.ContinueSpeedCounter = 0;
                    if (minDiff(savedDate) > 1) {
                        sharedPref.setHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());
                        serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                                hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                                odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed);
                    }
                }

            } else {

                sharedPref.setHighPrecisionOdometer(HighPrecisionOdometer, currentLogDate, getApplicationContext());

                if (speedCalculated >= DrivingSpeedLimit && speed == 0 ) {
                    obdVehicleSpeed = (int) speedCalculated;
                    VehicleSpeed = obdVehicleSpeed;
                    global.VEHICLE_SPEED = obdVehicleSpeed;
                    speed = obdVehicleSpeed;
                }


                if (speed < DrivingSpeedLimit || (speedCalculated < DrivingSpeedLimit )) {
                    Log.d("ELD Rule", "Rule is correct.");
                    ServiceCycle.ContinueSpeedCounter = 0;
                } else {
                    serviceCycle.CalculateCycleTime(Integer.valueOf(DriverId), IsLogApiACalled, IsAlertTimeValid, VehicleSpeed,
                            hMethods, dbHelper, latLongHelper, LocMethod, serviceCallBack, serviceError, notificationMethod, shipmentHelper,
                            odometerhMethod, true, constants.WIFI_OBD, obdVehicleSpeed, GpsVehicleSpeed);
                }

            }
        }else{
            if(timeInSec >= 5) {
                ServiceCycle.ContinueSpeedCounter = 0;
            }
        }

        sharedPref.setVss(VehicleSpeed, getApplicationContext());
        sharedPref.setRPM(truckRPM, getApplicationContext());

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
                // .addFormDataPart("DriverId", DriverId ) ;

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




    //*================== Get Odometer 18 Days data ===================*//*
    void SaveDriverDeviceUsageLog(final String savedDate){
        networkUsage();

        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("AlsSendingData", AlsSendingData );
        params.put("AlsReceivedData", AlsReceivedData   );
        params.put("MobileUsage", MobileUsage );
        params.put("TitalUsage", TotalUsage );
        params.put("EntryDate", savedDate );

        saveDriverDeviceUsageLog.executeRequest(Request.Method.POST, APIs.SAVE_DRIVER_DEVICE_USAGE_LOG , params, SaveDriverDeviceUsageLog,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);


    }



    //*================== Get Odometer 18 Days data ===================*//*
    void GetOdometer18Days(final String DriverId, final String DeviceId, final String CompanyId , final String CreatedDate){

        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("DeviceId", DeviceId );
        params.put("CompanyId", CompanyId  );
        params.put("CreatedDate", CreatedDate);

        GetOdometerRequest.executeRequest(Request.Method.POST, APIs.GET_ODOMETER_OFFLINE , params, GetOdometers18Days,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);


    }


    //*================== Get Driver Status Permissions ===================*//*
    void GetCtPatInspection18Days(final String DriverId, final String DeviceId, final String SearchedDate, final int GetInspectionFlag ){

        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("DeviceId", DeviceId );
        params.put("SearchedDate", SearchedDate );

        ctPatInsp18DaysRequest.executeRequest(Request.Method.POST, APIs.GET_OFFLINE_17_INSPECTION_LIST, params, GetInspectionFlag,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }




    // Save Driver Cycle From OBD data those are getting from als server.
    void SaveCycleWithCurrentDate(int CycleId, String currentUtcDate, String changeType){


        try {
            /* ------------- Save Cycle details with time is different with earlier cycle --------------*/
            String CurrentCycle   = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getApplicationContext() );
            if(CycleId != -1 && !CurrentCycle.equals(CycleId)) {
                JSONArray cycleDetailArray = global.getSaveCycleRecords(CycleId, changeType, getApplicationContext());
                sharedPref.SetCycleOfflineDetails(cycleDetailArray.toString(), getApplicationContext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        switch (CycleId){

            case 1:
                DriverConst.SetDriverCurrentCycle(Globally.CANADA_CYCLE_1_NAME, Globally.CANADA_CYCLE_1, getApplicationContext());
                DriverConst.SetCoDriverCurrentCycle(Globally.CANADA_CYCLE_1_NAME, Globally.CANADA_CYCLE_1, getApplicationContext());
                break;

            case 2:
                DriverConst.SetDriverCurrentCycle(Globally.CANADA_CYCLE_2_NAME, Globally.CANADA_CYCLE_2, getApplicationContext());
                DriverConst.SetCoDriverCurrentCycle(Globally.CANADA_CYCLE_2_NAME, Globally.CANADA_CYCLE_2, getApplicationContext());
                break;

            case 3:
                DriverConst.SetDriverCurrentCycle(Globally.USA_WORKING_6_DAYS_NAME, Globally.USA_WORKING_6_DAYS, getApplicationContext());
                DriverConst.SetCoDriverCurrentCycle(Globally.USA_WORKING_6_DAYS_NAME, Globally.USA_WORKING_6_DAYS, getApplicationContext());
                break;

            case 4:
                DriverConst.SetDriverCurrentCycle(Globally.USA_WORKING_7_DAYS_NAME, Globally.USA_WORKING_7_DAYS, getApplicationContext());
                DriverConst.SetCoDriverCurrentCycle(Globally.USA_WORKING_7_DAYS_NAME, Globally.USA_WORKING_7_DAYS, getApplicationContext());
                break;

        }

        // Save Current Date
        sharedPref.setCurrentDate(currentUtcDate, getApplicationContext());



    }



    private void saveObdStatus(String ignition, String type, String time){

        try {


          /*  if(UILApplication.isActivityVisible() && EldFragment.driverLogArray.length() > 0) {
                JSONObject lastJsonItem = (JSONObject) EldFragment.driverLogArray.get(EldFragment.driverLogArray.length() - 1);
                int currentJobStatus = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
                boolean isYard = lastJsonItem.getBoolean(ConstantsKeys.YardMove);
                boolean isPersonal = lastJsonItem.getBoolean(ConstantsKeys.Personal);

                if( (currentJobStatus == constants.OFF_DUTY && isPersonal) ||
                        (currentJobStatus == constants.ON_DUTY && isYard) ){
                    if(sharedPref.GetTruckIgnitionStatus(constants.TruckIgnitionStatus, getApplicationContext()).equals("OFF")
                            && ignition.equals("ON")) {
                        sharedPref.SetTruckStartLoginStatus(true, getApplicationContext());
                    }
                }
            }*/

            sharedPref.SetTruckIgnitionStatus(ignition, type, time, getApplicationContext());


        }catch (Exception e){
            e.printStackTrace();
        }

    }



    private void SyncData(){

        try {

            savedSyncedArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DriverId), dbHelper);

            if(savedSyncedArray.length() > 0) {
                syncingFile = global.SaveFileInSDCard("Sync_", savedSyncedArray.toString(), false, getApplicationContext());
            }

            DriverLogFile = global.GetSavedFile(getApplicationContext(),ConstantsKeys.ViolationTest, "txt");

            if(DriverLogFile.exists() || syncingFile.exists() ) {
                // Sync driver log API data to server with SAVE_LOG_TEXT_FILE (SAVE sync data service)
                SyncDataUpload syncDataUpload = new SyncDataUpload(getApplicationContext(), DriverId, syncingFile, DriverLogFile, new File(""), true, asyncResponse);
                syncDataUpload.execute();
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
                                    if (updateOfflineNoResponseCount > 0) {
                                        constants.IsAlsServerResponding = false;
                                    }
                                    updateOfflineNoResponseCount++;
                                    VehicleSpeed = GpsVehicleSpeed;

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
                                        sharedPref.SetOBDPingAllowedStatus(IsOBDPingAllowed, getApplicationContext());
                                    }

                                    if (dataObj.has("IsAutoSync")) {
                                        IsAutoSync = dataObj.getBoolean("IsAutoSync");
                                    }

                                    if (dataObj.has("IsAutoOnDutyDriveEnabled")) {
                                        boolean IsAutoDrive = dataObj.getBoolean("IsAutoOnDutyDriveEnabled");
                                        sharedPref.SetAutoDriveStatus(IsAutoDrive, getApplicationContext());
                                    }


                                    ignitionStatus = dataObj.getString("EngineStatus");

                                    if(ignitionStatus.equals("true")){
                                        global.IS_OBD_IGNITION = true;
                                        saveObdStatus("ON", constants.ApiData, global.getCurrentDate());
                                    }else{
                                        global.IS_OBD_IGNITION = false;
                                        saveObdStatus("OFF", constants.ApiData, "");
                                    }


                                    try {
                                        // Save Truck information for manual/auto mode
                                        sharedPref.SetIsAOBRD(IsAOBRD, getApplicationContext());
                                        sharedPref.SetAOBRDAutomatic(dataObj.getBoolean(ConstantsKeys.IsAOBRDAutomatic), getApplicationContext());
                                        sharedPref.SetAOBRDAutoDrive(dataObj.getBoolean(ConstantsKeys.IsAutoDriving), getApplicationContext());
                                        sharedPref.SetDrivingShippingAllowed(dataObj.getBoolean(ConstantsKeys.IsDrivingShippingAllowed), getApplicationContext());
                                        sharedPref.saveTimeStampView(dataObj.getBoolean(ConstantsKeys.IsTimestampEnabled), getApplicationContext());
                                        sharedPref.setCurrentUTCTime(UtcCurrentDate, getApplicationContext());
                                        sharedPref.setPersonalUse75Km(dataObj.getBoolean(ConstantsKeys.PersonalUse75Km), getApplicationContext());

                                        if(dataObj.has(ConstantsKeys.SuggestedEdit)) {
                                            boolean isSuggestedEdit = dataObj.getBoolean(ConstantsKeys.SuggestedEdit);

                                            sharedPref.setEldOccurences(dataObj.getBoolean(ConstantsKeys.IsUnidentified),
                                                    dataObj.getBoolean(ConstantsKeys.IsMalfunction),
                                                    dataObj.getBoolean(ConstantsKeys.IsDiagnostic),
                                                    isSuggestedEdit, getApplicationContext());

                                            if (isSuggestedEdit && sharedPref.isSuggestedRecall(getApplicationContext()) ) {
                                                try {
                                                    if(UILApplication.isActivityVisible()) {
                                                        Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                                                        intent.putExtra(ConstantsKeys.SuggestedEdit, isSuggestedEdit);
                                                        LocalBroadcastManager.getInstance(BackgroundLocationService.this).sendBroadcast(intent);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    boolean connectionStatus = sharedPref.isOnline(getApplicationContext());
                                    if(!connectionStatus){  // if last status was not online then save online status
                                        constants.saveAppUsageLog(ConstantsEnum.StatusOnline,  false, false, obdUtil);
                                    }
                                    sharedPref.setOnlineStatus(true, getApplicationContext());

                                    // Sync app usage log to server (SAVE sync data service)
                                    obdUtil.syncAppUsageLog(getApplicationContext(), DriverId);



                                    if (sharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)) {
                                        if (sharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
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
                                        sharedPref.SetOdometerFromOBD(dataObj.getBoolean("IsOdometerFromOBD"), getApplicationContext());

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

                                    global.VEHICLE_SPEED = obdVehicleSpeed;

                                    if (obdVehicleSpeed != -1) {
                                        VehicleSpeed = obdVehicleSpeed;
                                    }

                                    //    global.ShowLocalNotification(getApplicationContext(), "API obd Speed", "API obd Speed: " + obdVehicleSpeed, 2009);

                                    // Save Driver Cycle With Current Date
                                    SaveCycleWithCurrentDate(CycleId, utcCurrentDateTime.toString(), "UpdateOfflineDriverLog_api");

                                } catch (Exception e) {
                                    VehicleSpeed = GpsVehicleSpeed;
                                    global.VEHICLE_SPEED = -1;
                                    e.printStackTrace();
                                }

                                // -------- upload offline locally saved data ---------
                                UploadSavedShipmentData();
                                SaveCertifyLog();
                                if (!sharedPref.GetOdoSavingStatus(getApplicationContext()))
                                    UploadSavedOdometerData();


                                if (sharedPref.getDriverType(getApplicationContext()).equals(DriverConst.TeamDriver)) {

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

                                if (IsAutoSync) {
                                    SyncData();
                                }


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
                                        dbHelper, sharedPref, getApplicationContext());
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
                            sharedPref.setLastUsageDataSavedTime(global.getCurrentDate(), getApplicationContext());
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
                    VehicleSpeed = GpsVehicleSpeed;
                    apiReturnedSpeed = "--";
                    global.VEHICLE_SPEED = -1;

                    break;


            }

        }
    };




    AsyncResponse asyncResponse = new AsyncResponse() {
        @Override
        public void onAsyncResponse(String response) {

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


                    if (DriverLogFile != null && DriverLogFile.exists()) {
                        msgTxt = "Data syncing is completed with violation log file";
                        DriverLogFile.delete();
                        DriverLogFile = null;
                    }


                    syncingMethod.SyncingLogHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                    Log.d("Sync", msgTxt );

                }else {
                    if(syncingFile != null && syncingFile.exists())
                        syncingFile.delete();

                    if(DriverLogFile != null && DriverLogFile.exists())
                        DriverLogFile.delete();
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
                            sharedPref.SetOdoSavingStatus(false, getApplicationContext());
                            odometerHelper.OdometerHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                            String CompanyId = "", SelectedDate = "";
                            SelectedDate = Globally.GetCurrentDeviceDate();
                            DriverId   = sharedPref.getDriverId(getApplicationContext());
                            if(sharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
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
                sharedPref.SetOdoSavingStatus(false, getApplicationContext());
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
                        Thread.sleep(4000);
                        Intent intent = new Intent();
                        intent.putExtra("isUpdate", "true");
                        intent.setAction(constants.packageName);
                        sendBroadcast(intent);

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
                    global.ShowNotificationWithSound(getApplicationContext(), msg, status, mNotificationManager);
                }else {
                    if (sharedPref.IsReadViolation(getApplicationContext()) == false) {
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

                        constants.ClearNotifications(getApplicationContext());
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
            if (sharedPref.IsEditedData(getApplicationContext())) {
                SavedLogApi = APIs.SAVE_DRIVER_EDIT_LOG;
            } else {
                SavedLogApi = APIs.SAVE_DRIVER_STATUS;
            }



            if(DriverType == 0) {  // Current active driver is Main Driver. So we need co driver details and we are getting co driver's details.

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

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void StartStopServer(final String value){
        if(isBound){

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Setup the message for invocation
                    try{
                        Log.d(TAG_OBD, "Invocation Failed!!");

                        //Set the ReplyTo Messenger for processing the invocation response
                        Message msg1 = new Handler().obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("key",  value );
                        msg1.setData(bundle);
                        msg1.replyTo = replyTo;

                        //Make the invocation
                        messenger.send(msg1);

                    }catch(Exception rme){
                        //Show an Error Message
                        rme.printStackTrace();
                        Log.d(TAG_OBD, "Invocation Failed!!");
                    }
                }
            });

        }else{
            try{
                Log.d(TAG_OBD, "Service is Not Bound!!");
                this.connection = new RemoteServiceConnection();
                BindConnection();
            }catch (Exception e){
                e.printStackTrace();
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