package com.background.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ble.util.BleUtil;
import com.ble.util.ConstantEvent;
import com.ble.util.EventBusInfo;
import com.constants.APIs;
import com.constants.CheckConnectivity;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.Logger;
import com.constants.SaveDriverLogPost;
import com.constants.SaveLogJsonObj;
import com.constants.SaveUnidentifiedRecord;
import com.constants.SharedPref;
import com.constants.ShellUtils;
import com.constants.TcpClient;
import com.constants.Utils;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.htstart.htsdk.HTBleSdk;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.MalfunctionDiagnosticMethod;
import com.local.db.VehiclePowerEventMethod;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.notifications.NotificationManagerSmart;
import com.wifi.settings.WiFiConfig;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import dal.tables.OBDDeviceData;
import obdDecoder.Decoder;


public class AfterLogoutService extends Service implements TextToSpeech.OnInitListener {

    String ServerPackage = "com.als.obd";
    String ServerService = "com.als.obd.services.MainService";
    String ignitionStatus = "", truckRPM = "", currentHighPrecisionOdometer = "", obdOdometer = "";
    String noObd = "obd not connected";
    String AlertMsg = "Your vehicle is moving ";
    String AlertMsg1 = "and there is no driver login in eLog book";
    String AlertMsgSpeech = "Your vehicle is moving and there is no driver login in e log book";
    String ClearEventType = "", Origin = "Android";

    String TAG = "LogoutService";
    String TAG_OBD = "OBD LogoutService";
    private static final long TIME_INTERVAL_WIFI  = 10 * 1000;   // 10 sec
    private static final long TIME_INTERVAL_WIRED = 3 * 1000;   // 3 sec

    boolean isStopService       = false;
    double lastVehSpeed         = -1;

    int SpeedCounter            = 60;      // initially it is 60 so that ELD rule is called instantly
    int ThreshHoldSpeed         = 8;
    int intermediateRecordTime  = 60;    // intermediate record time is 60 min
    int OnDutyRecordTime        = 5;    // OnDuty record time is 6 min when earlier event was DR and now vehicle has been stopped for 6 min
    int apiCallCount            = 0;
    int offsetFromUTC           = 0;

    boolean isTempTimeValidate = true;
    boolean IsUnassignedRequired = true;

    String TempTimeAtStart = "", CompanyId = "", TruckID = "", TempStatus = "";
    String LastDutyStatus = "", StatusStartTime = "";   //, CurrentDutyStatus = "";
    String VinNumber = "", UnAssignedVehicleMilesId = "0", IntermediateLogId = "0", EngineSeconds = "";
    String PreviousLatitude = "", PreviousLongitude = "", PreviousOdometer = "";

    boolean Intermediate = false;
    boolean IntermediateUpdate = false;
    boolean isMalfncDataAlreadyPosting = false;
    CheckConnectivity checkConnectivity;
    HelperMethods hMethods;
    DBHelper dbHelper;
    Globally global;
    JSONArray unPostedLogArray = new JSONArray();
    JSONArray powerEventInputArray = new JSONArray();

    boolean UnidentifiedApiInProgress = false;
    boolean isMalEventApiInProgress = false;
    final int GetMalDiaEventDuration        = 20;
    final int SaveUnidentifiedEvent         = 30;

    Map<String, String> params;
    VolleyRequest truckListApi, companyListApi, GetMalfunctionEvents;
    SaveUnidentifiedRecord SaveUpdateUnidentifiedApi;
    SaveLogJsonObj UnassignedRequiredRequest;
    SaveDriverLogPost saveDriverLogPost;

    boolean isDataAlreadyPosting = false;

    private TextToSpeech textToSpeech;
    TcpClient tcpClient;
    OBDDeviceData data;
    Decoder decoder;
    WiFiConfig wifiConfig;
    Constants constants;
    private Messenger messenger = null;     //used to make an RPC invocation
    private boolean isBound = false;
    private ServiceConnection connection;   //receives callbacks from bind and unbind invocations
    private Messenger replyTo = null;       //invocation replies are processed by this Messenger
    private Handler mHandler = new Handler();
    NotificationManagerSmart mNotificationManager;
    boolean isWiredCallBackCalled = false;

    Intent locServiceIntent;


    int ObdPreference;

    int bleConnectionCount = 0;
    int VehicleObdSpeed = 0;

    final int SaveMalDiagnstcEvent  = 302;
    final int ClearMalDiaEvent      = 21;
    final int SaveVehPwrEventLog    = 25;

    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
    DriverPermissionMethod driverPermissionMethod;
    VehiclePowerEventMethod vehiclePowerEventMethod;

    //double tempOdo = 1179884000;  //1.090133595E9
    // double tempEngHour = 22999.95;

    Utils obdUtil;
    private BroadcastReceiver mMessageReceiver = null;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();

        data                    = new OBDDeviceData();
        decoder                 = new Decoder();
        wifiConfig              = new WiFiConfig();
        tcpClient               = new TcpClient(obdResponseHandler);
        mNotificationManager    = new NotificationManagerSmart(getApplicationContext());
        textToSpeech            = new TextToSpeech(getApplicationContext(), this);
        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();
        driverPermissionMethod  = new DriverPermissionMethod();
        vehiclePowerEventMethod = new VehiclePowerEventMethod();

        constants               = new Constants();
        mTimer                  = new Timer();

        saveDriverLogPost       = new SaveDriverLogPost(getApplicationContext(), saveLogRequestResponse);
        VinNumber = getVin();

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive (Context context, Intent intent){
                 Logger.LogDebug("received", "received from service");
               // boolean BleDataService = intent.getBooleanExtra(ConstantsKeys.BleDataService, false);
                boolean IsConnected = intent.getBooleanExtra(ConstantsKeys.IsConnected, false);
                String data         = intent.getStringExtra(ConstantsKeys.Data);
               // Logger.LogDebug("Ble Data", "Data: " + data);
// 0048 @@ 0 @@ 1 @@ 090622 @@ 060443 @@ 090622053820 @@ 090622060443 @@ OnTime @@ 0 @@ 0 @@ 642264000 @@ 0.00 @@ @@ X @@ X @@ 0
               // if (BleDataService) {

                if (!SharedPref.IsDriverLogin(getApplicationContext())) {

                    try{

                        if(IsConnected){
                            String[] decodedDataArray = BleUtil.decodedDataArray(data);
                            if(decodedDataArray.length > 10){

                                String savedOnReceiveTime = SharedPref.getBleOnReceiveTime(getApplicationContext());
                                if (savedOnReceiveTime.length() > 10) {
                                    int timeInSec = constants.getSecDifference(savedOnReceiveTime, Globally.GetDriverCurrentDateTime(global, getApplicationContext()));
                                    if (timeInSec > 1) {
                                        SharedPref.setBleOnReceiveTime(getApplicationContext());

                                        String savedMacAddress = SharedPref.GetBleOBDMacAddress(getApplicationContext());
                                        if (savedMacAddress.length() == 0 || savedMacAddress.equals(decodedDataArray[0])) {
                                            //  Logger.LogError("TAG", "onReceiveTime==" + htBleData);
                                            SharedPref.SaveBleOBDMacAddress(decodedDataArray[0], getApplicationContext());

                                            if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_CONNECTED) {
                                                bleConnectionCount = 0;
                                                SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                                        global.GetCurrentUTCTimeFormat(), getApplicationContext());

                                                global.ShowLocalNotification(getApplicationContext(),
                                                        getString(R.string.BluetoothOBD),
                                                        getString(R.string.obd_ble), 2081);

                                                sendBroadcast(false, "No");

                                            }

                                            sendBroadcast(true, "");
                                            VehicleObdSpeed = Integer.valueOf(decodedDataArray[9]);
                                            truckRPM = decodedDataArray[10];
                                            VinNumber = decodedDataArray[13];
                                            EngineSeconds = decodedDataArray[12];
                                            obdOdometer = decodedDataArray[11];
                                            currentHighPrecisionOdometer = decodedDataArray[11];

                                               /* String lat = htBleData.getLatitude();
                                                String lon = htBleData.getLongitude();
                                                if(lat.equalsIgnoreCase("X")){
                                                    lat = Constants.getLocationType(getApplicationContext());
                                                    lon = lat;
                                                }
                                                 Globally.LATITUDE = lat;
                                                Globally.LONGITUDE = lon ;
                                                */

                                            Globally.LATITUDE = decodedDataArray[14];
                                            Globally.LONGITUDE = decodedDataArray[15];

                                            // this check is using to confirm loc update, because in loc disconnection ble OBD is sending last saved location.
                                            if (Globally.LATITUDE.equals(PreviousLatitude) && Globally.LONGITUDE.equals(PreviousLongitude) &&
                                                    !currentHighPrecisionOdometer.equals(PreviousOdometer)) {
                                                Globally.LATITUDE = Globally.GPS_LATITUDE;
                                                Globally.LONGITUDE = Globally.GPS_LONGITUDE;

                                            }

                                            if (Globally.LATITUDE.length() < 4) {
                                                SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());

                                                if (Globally.GPS_LATITUDE.length() > 3) {
                                                    Globally.LATITUDE = Globally.GPS_LATITUDE;
                                                    Globally.LONGITUDE = Globally.GPS_LONGITUDE;
                                                } else {
                                                    Globally.LATITUDE = "";
                                                    Globally.LONGITUDE = "";
                                                }

                                            } else {
                                                SharedPref.SetLocReceivedFromObdStatus(true, getApplicationContext());
                                            }

                                            PreviousLatitude = decodedDataArray[14];
                                            PreviousLongitude = decodedDataArray[15];
                                            PreviousOdometer = currentHighPrecisionOdometer;

                                            if (constants.isObdVinValid(VinNumber)) {
                                                SharedPref.setVehicleVin(VinNumber, getApplicationContext());
                                            } else {
                                                VinNumber = SharedPref.getVINNumber(getApplicationContext());
                                            }

                                            constants.saveEcmLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE, currentHighPrecisionOdometer, getApplicationContext());


                                            String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());
                                            //Logger.LogDebug("lastIgnitionStatus", "lastIgnitionStatus00: " +lastIgnitionStatus );
                                            // this check is used when ble obd is disconnected
                                            if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_CONNECTED) {
                                                if (!SharedPref.getRPM(getApplicationContext()).equals("0") && lastIgnitionStatus.equals("true")) {
                                                    truckRPM = SharedPref.getRPM(getApplicationContext());
                                                    ignitionStatus = "ON";
                                                }

                                                checkObdDataWithRule(VehicleObdSpeed);

                                            } else {

                                                if (truckRPM.length() > 0) {
                                                    if (Integer.valueOf(truckRPM) > 0) {
                                                        ignitionStatus = "ON";
                                                    } else {
                                                        ignitionStatus = "OFF";
                                                    }

                                                    checkObdDataWithRule(VehicleObdSpeed);

                                                }
                                            }

                                        }

                                    }else{
                                        if(timeInSec < 0){
                                            SharedPref.setBleOnReceiveTime(getApplicationContext());
                                        }
                                    }
                                } else {
                                    SharedPref.setBleOnReceiveTime(getApplicationContext());
                                }

                            }
                        }else{

                            sendBroadcast(false, "");
                            SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                            if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {

                                global.ShowLocalNotification(getApplicationContext(),
                                        getString(R.string.BluetoothOBD),
                                        getString(R.string.obd_ble_disconnected), 2081);

                                sendBroadcast(false, "Yes");
                                // check Unidentified event occurrence
                                // saveUnidentifiedEventStatusOld(-1, false);
                            }

                            SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                    global.GetCurrentUTCTimeFormat(), getApplicationContext());

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Logger.LogDebug("Driver LoggedIn", "Login");
                    if(!isDataAlreadyPosting){
                        StopService();
                    }
                }

            }
        };


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(ConstantsKeys.BleDataService));


        try{
            //  ------------- OBD Log write initilization----------
            obdUtil = new Utils(getApplicationContext());
            obdUtil.createAppUsageLogFile();
        }catch (Exception e){
            e.printStackTrace();
        }


        //  ------------- Wired OBD ----------
        this.connection = new RemoteServiceConnection();
        this.replyTo = new Messenger(new IncomingHandler());

        ObdPreference = SharedPref.getObdPreference(getApplicationContext());
        SharedPref.setNotiShowTime("", getApplicationContext());
        SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());

        initUnidentifiedObj();

        if(ObdPreference == Constants.OBD_PREF_BLE) {

            if (!SharedPref.IsDriverLogin(getApplicationContext())) {
                startBleService();
            }


        }else if(ObdPreference == Constants.OBD_PREF_WIRED) {
            BindConnection();
            checkWiredObdConnection();
        }


        startLocationService();

        SharedPref.setContinueSpeedZeroTime(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
        SharedPref.saveBleScanCount(0, getApplicationContext());
        mTimer.schedule(timerTask, TIME_INTERVAL_WIFI, TIME_INTERVAL_WIFI);

    }


    private void checkUnAssignedReqStatus(){

        try {
            if(constants.isObdConnectedWithELD(getApplicationContext())) {
                String apiCallTime = SharedPref.getCheckUnassignedReqTime(getApplicationContext());
                if (apiCallTime.length() == 0) {
                    CheckUnassignedRequired();
                } else {
                    DateTime apiCallDateTime = Globally.getDateTimeObj(apiCallTime, false);
                    DateTime currentTime = Globally.getDateTimeObj(Globally.GetCurrentUTCTimeFormat(), true);
                    int minDiff = (int) Constants.getDateTimeDuration(apiCallDateTime, currentTime).getStandardMinutes();
                    if (minDiff > 4) {
                        CheckUnassignedRequired();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //  ------------- Wired OBD data response handler ----------
    private class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();

            try {
                if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED ) {
                    ignitionStatus = bundle.getString(constants.OBD_IgnitionStatus);
                    truckRPM = bundle.getString(constants.OBD_RPM);
                    VehicleObdSpeed = bundle.getInt(constants.OBD_Vss);
                    EngineSeconds = bundle.getString(constants.OBD_EngineHours);
                    VinNumber = bundle.getString(constants.OBD_VINNumber);
                    obdOdometer = bundle.getString(constants.OBD_Odometer);

                    if (constants.isObdVinValid(VinNumber)) {
                        SharedPref.setVehicleVin(VinNumber, getApplicationContext());
                    } else {
                        VinNumber = SharedPref.getVINNumber(getApplicationContext());
                    }


                    if (bundle.getString(constants.OBD_HighPrecisionOdometer) != null) {
                        currentHighPrecisionOdometer = bundle.getString(constants.OBD_HighPrecisionOdometer);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            //========================================================
            isWiredCallBackCalled = true;


            String obdLastCallDate = SharedPref.getWiredObdCallTime(getApplicationContext());
            if(obdLastCallDate.length() > 10) {
                final DateTime currentDateTime = global.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext());    // Current Date Time
                final DateTime savedDateTime = global.getDateTimeObj(obdLastCallDate, false);

                int timeInSec = (int) Constants.getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
                if (timeInSec >= 3) {  // minimum call interval is 3 sec.
                    SharedPref.SetWiredObdCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    checkObdDataWithRule(VehicleObdSpeed);
                }else if(timeInSec < 0){
                    SharedPref.SetWiredObdCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                }
            }else{
                SharedPref.SetWiredObdCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
            }

        }
    }






    private void checkObdDataWithRule(int speed){

        // ---------------- temp data ---------------------

        // temp odometer for simulator converting odometer from km to meter. because it is saving in km.

        if(ObdPreference == Constants.OBD_PREF_WIRED){
            currentHighPrecisionOdometer = Constants.kmToMeter(obdOdometer);
        }


        try {

            if (!SharedPref.IsDriverLogin(getApplicationContext())) {

                checkEngHrOdo();

                int OBD_LAST_STATUS = SharedPref.getObdStatus(getApplicationContext());
                if(OBD_LAST_STATUS == Constants.WIRED_CONNECTED || OBD_LAST_STATUS == Constants.BLE_CONNECTED) {

                    if (ignitionStatus.equals("ON") && !truckRPM.equals("0")) {

                        /* ======================== Malfunction & Diagnostic Events ========================= */
                        if(EngineSeconds.length() > 0 && !EngineSeconds.equals("0") && !EngineSeconds.equals("0.00")) {
                            vehiclePowerEventMethod.savePowerEventOnChange(ignitionStatus, offsetFromUTC, dbHelper, getApplicationContext());
                            checkPowerMalDiaEvent();   // checking Power Data Compliance Mal/Dia event
                            checkEngSyncClearEvent();  // checking EngineSyncDataCompliance Mal/Dia clear event if already occurred
                        }

                        String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());
                        if(savedDate.length() == 0){
                            savedDate = Globally.GetCurrentUTCTimeFormat();
                            SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, savedDate, getApplicationContext());
                        }else{
                            SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer,
                                    SharedPref.getHighPrecesionSavedTime(getApplicationContext()), getApplicationContext());
                        }

                        if (speed >= 8 && lastVehSpeed >= 8) {

                            showNotificationAlert(speed);

                            if(SharedPref.IsAppRestricted(getApplicationContext())) {
                                SharedPref.setLoginAllowedStatus(false, getApplicationContext());
                            }

                            // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
                            if(!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {
                                if (TruckID.length() > 0 && CompanyId.length() > 0) {

                                    if(IsUnassignedRequired) {
                                        if (isTempTimeValidate) {
                                            if (constants.minDiffFromUTC(savedDate, global, getApplicationContext()) > 0) {
                                                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
                                                saveDrivingStatusForUnidentifiedDia(speed);

                                                isTempTimeValidate = false;
                                            }
                                        } else {
                                            SharedPref.setContinueSpeedZeroTime(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());

                                            if (constants.minDiffFromUTC(savedDate, global, getApplicationContext()) > 0) {
                                                SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
                                                UnidentifiedStatusChange(speed, 0);
                                            }

                                        }
                                    }else{
                                        // End Duty event if already active and
                                        endDutyOnIgnitionOff();
                                    }
                                }else{
                                    TruckID             = SharedPref.getTruckNumber(getApplicationContext());
                                    CompanyId           = DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext());

                                }

                                // check Unidentified event occurrence
                                // saveUnidentifiedEventStatusOld(speed, true);
                            }

                        } else {
                            if(speed == 0) {
                                SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                                // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
                                if (!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {
                                    if (TruckID.length() > 0 && CompanyId.length() > 0) {

                                        if (isTempTimeValidate == false) {
                                            String lastSavedTime = SharedPref.getContinueSpeedZeroTime(getApplicationContext());
                                            int continueSpeedZeroDiff = constants.minDiffFromUTC(lastSavedTime, global, getApplicationContext());
                                            UnidentifiedStatusChange(speed, continueSpeedZeroDiff);
                                        }

                                        if (isTempTimeValidate && constants.minDiffFromUTC(savedDate, global, getApplicationContext()) >= 1) {
                                            isTempTimeValidate = false;
                                        }

                                        // check Unidentified event occurrence
                                        // saveUnidentifiedEventStatusOld(speed, false);

                                    }

                                }
                            }

                        }


                        lastVehSpeed = speed;

                        // ping wired server to get data
                        if( ObdPreference == Constants.OBD_PREF_WIRED && OBD_LAST_STATUS == Constants.WIRED_CONNECTED) {
                            CallWired(TIME_INTERVAL_WIRED);
                        }

                    } else {
                        lastVehSpeed = -1;
                        SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                        vehiclePowerEventMethod.savePowerEventOnChange(ignitionStatus, offsetFromUTC, dbHelper, getApplicationContext());

                        saveIgnitionStatus(ignitionStatus);


                        if( ObdPreference == Constants.OBD_PREF_WIRED ) {
                            if (OBD_LAST_STATUS != Constants.WIRED_CONNECTED) {
                                CallWired(Constants.SocketTimeout5Sec);
                            }
                        }

                        // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
                        if(!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {
                            // check Unidentified event occurrence
                            // saveUnidentifiedEventStatusOld(-1, false);

                            endDutyOnIgnitionOff();

                        }
                    }
                }else{
                    lastVehSpeed = -1;
                    SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                    if( ObdPreference != Constants.OBD_PREF_WIFI ) {

                        if (ObdPreference == Constants.OBD_PREF_WIRED) {
                            CallWired(Constants.SocketTimeout5Sec);
                        }

                        // Check Engine Sync data Malfunction/Diagnostic event
                        checkEngineSyncMalDiaOccurredEvent();

                    }

                    // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
                    if(!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {
                        // check Unidentified event occurrence
                        //  saveUnidentifiedEventStatusOld(-1, false);

                        endDutyOnIgnitionOff();

                    }

                }

            }else{
                lastVehSpeed = -1;
                SharedPref.setLoginAllowedStatus(true, getApplicationContext());

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void checkWiredObdConnection() {

        if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
            ShellUtils.CommandResult obdShell = ShellUtils.execCommand("cat /sys/class/power_supply/usb/type", false);
            Logger.LogDebug("OBD", "obd --> cat type --> " + obdShell.successMsg);
            if (obdShell.result == 0) {
                if (obdShell.successMsg.contains("USB_DCP") ) {  //USB_DCP ) || obdShell.successMsg.contains("USB")

                    if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED) {

                        // Connected State
                        SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                global.GetCurrentUTCTimeFormat(), getApplicationContext());

                        StartStopServer(constants.WiredOBD);

                        global.ShowLocalNotification(getApplicationContext(),
                                getString(R.string.wired_tablettt),
                                getString(R.string.wired_tablet_connected), 2081);

                        sendBroadcast(false, "No");
                    }

                    sendBroadcast(true, "");


                } else {

                    // Disconnected State. Save only when last status was not already disconnected
                    if (SharedPref.getObdStatus(getApplicationContext()) != constants.WIRED_DISCONNECTED) {

                        SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                global.GetCurrentUTCTimeFormat(), getApplicationContext());


                        global.ShowLocalNotification(getApplicationContext(),
                                getString(R.string.wired_tablettt),
                                getString(R.string.wired_tablet_disconnected), 2081);

                        sendBroadcast(false, "Yes");
                    }

                    sendBroadcast(false, "");

                    isWiredCallBackCalled = false;

                    // check Unidentified event occurrence
                    // saveUnidentifiedEventStatusOld(-1, false);

                }
            } else {
                // Error


                isWiredCallBackCalled = false;
                SharedPref.SaveObdStatus(Constants.WIRED_ERROR, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                        global.GetCurrentUTCTimeFormat(), getApplicationContext());

                sendBroadcast(false, "");
            }

        }

    }



    private void initUnidentifiedObj(){
        try{

            hMethods            = new HelperMethods();
            dbHelper            = new DBHelper(getApplicationContext());
            global              = new Globally();
            TruckID             = SharedPref.getTruckNumber(getApplicationContext());   //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext());
            CompanyId           = DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext());

            IntermediateLogId           = SharedPref.getIntermediateLogId(getApplicationContext());
            UnAssignedVehicleMilesId    = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());



            truckListApi        = new VolleyRequest(getApplicationContext());
            companyListApi      = new VolleyRequest(getApplicationContext());
            GetMalfunctionEvents= new VolleyRequest(getApplicationContext());

            checkConnectivity       = new CheckConnectivity(getApplicationContext());
            SaveUpdateUnidentifiedApi = new SaveUnidentifiedRecord(getApplicationContext(), ResponseCallBack, ErrorCallBack);
            UnassignedRequiredRequest = new SaveLogJsonObj(getApplicationContext(), jsonApiResponse);

            if(CompanyId.length() > 0) {
                unPostedLogArray = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);
                TempTimeAtStart = Globally.GetCurrentUTCTimeFormat();

                if (unPostedLogArray.length() > 0) {
                    if (Globally.isConnected(getApplicationContext())) {
                        SaveAndUpdateUnidentifiedRecordsApi(unPostedLogArray);
                    }

                }

                try {

                    JSONArray UnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
                    if (UnidentifiedLogArray.length() > 0) {
                        isTempTimeValidate = false;
                        JSONObject obj = (JSONObject) UnidentifiedLogArray.get(UnidentifiedLogArray.length() - 1);
                        StatusStartTime = obj.getString(ConstantsKeys.UTCStartDateTime);
                        String UTCEndDateTime = obj.getString(ConstantsKeys.UTCEndDateTime);

                        if(UTCEndDateTime.length() > 0) {
                            LastDutyStatus = SharedPref.getUnIdenLastDutyStatus(getApplicationContext());
                        }else{
                            LastDutyStatus = obj.getString(ConstantsKeys.DutyStatus);
                        }


                    }else {
                        LastDutyStatus = SharedPref.getUnIdenLastDutyStatus(getApplicationContext());

                        isTempTimeValidate = true;
                        SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, "", getApplicationContext());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void endDutyOnIgnitionOff(){
        try{

            if (TruckID.length() > 0 && CompanyId.length() > 0) {

                JSONArray UnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
                if (UnidentifiedLogArray.length() > 0) {
                    JSONObject obj = (JSONObject) UnidentifiedLogArray.get(UnidentifiedLogArray.length() - 1);
                    String UTCEndDateTime = obj.getString(ConstantsKeys.UTCEndDateTime);
                    boolean Intermediate = obj.getBoolean(ConstantsKeys.Intermediate);
                    String LastDutyStatus = "";

                    if(UTCEndDateTime.length() == 0 || Intermediate) {
                        LastDutyStatus = obj.getString(ConstantsKeys.DutyStatus);
                    }

                    if(LastDutyStatus.equals("DR") || LastDutyStatus.equals("OD")){

                        String lastSavedTime = SharedPref.getContinueSpeedZeroTime(getApplicationContext());
                        int continueSpeedZeroDiff = constants.minDiffFromUTC(lastSavedTime, global, getApplicationContext());

                        if( continueSpeedZeroDiff > OnDutyRecordTime){
                            SharedPref.setContinueSpeedZeroTime(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
                            EndOnDutyOrDrivingRecords(LastDutyStatus);
                        } else if(continueSpeedZeroDiff < 0){SharedPref.setContinueSpeedZeroTime(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());}
                    }

                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void checkEngHrOdo(){
        if(constants.isValidData(EngineSeconds)) {
            SharedPref.SetObdEngineHours(EngineSeconds, getApplicationContext());
        }else{
            EngineSeconds = SharedPref.getObdEngineHours(getApplicationContext());
        }

        if(constants.isValidData(currentHighPrecisionOdometer)) {
            SharedPref.SetObdOdometer(Constants.meterToKmWithObd(currentHighPrecisionOdometer), getApplicationContext());
            SharedPref.SetObdOdometerInMiles(Constants.meterToMilesWith2DecPlaces(currentHighPrecisionOdometer), getApplicationContext());
        }else{
            // converting odometer from km to meter. because it is saving in km.
            currentHighPrecisionOdometer = Constants.kmToMeter(SharedPref.getObdOdometer(getApplicationContext()));
        }

    }


    // save vehicle status DR/ON when vehicle moves or stopped for Unidentified diagnostic event if DR more then 30 min for the day
    private void saveVehMotionStatusForUnidentified(String currentUtcDate, int speed, boolean isVehicleInMotion){
        try{
            // check unidentified dia permission status
            boolean isOtherMalDiaALlowed = SharedPref.GetOtherMalDiaStatus(ConstantsKeys.UnidentifiedDiag, getApplicationContext());
            if(isOtherMalDiaALlowed) {

                if (CompanyId.length() > 0) {
                    boolean isVehicleMotionChanged = malfunctionDiagnosticMethod.isVehicleMotionChanged(isVehicleInMotion,
                            Integer.parseInt(CompanyId), dbHelper);

                    if (isVehicleMotionChanged) {
                        malfunctionDiagnosticMethod.saveVehicleMotionChangeTime(currentUtcDate, speed, TruckID, Integer.parseInt(CompanyId),
                                dbHelper, getApplicationContext());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showNotificationAlert(int speed){
        try{
            String notificationShowTime = SharedPref.getNotiShowTime(getApplicationContext());
            if (notificationShowTime.length() == 0) {
                SharedPref.setNotiShowTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                Globally.PlaySound(getApplicationContext());
                Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                        AlertMsg + "(" + speed + " km/h) " + AlertMsg1, 2003);
                SpeakOutMsg(AlertMsgSpeech);
            }else{
                if (notificationShowTime.length() > 10) {
                    DateTime currentTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext());
                    DateTime lastCallDateTime = Globally.getDateTimeObj(notificationShowTime, false);
                    long secDiff = Constants.getDateTimeDuration(lastCallDateTime, currentTime).getStandardSeconds();
                    if (secDiff >= 120) {    // Showing notification After 2 min interval
                        SharedPref.setNotiShowTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                        Globally.PlaySound(getApplicationContext());
                        Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                                AlertMsg + "(" + speed + " km/h) " + AlertMsg1, 2003);
                        SpeakOutMsg(AlertMsgSpeech);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void CallWired(long time){
        if (!SharedPref.IsDriverLogin(getApplicationContext())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    StartStopServer(constants.WiredOBD);
                }
            }, time);
        }
    }



    private void checkWifiOBDConnection(){

        if (!SharedPref.IsDriverLogin(getApplicationContext())) {

            boolean isAlsNetworkConnected = wifiConfig.IsAlsNetworkConnected(getApplicationContext());  // get ALS Wifi ssid availability
            boolean isWiredObdConnected = false;
            if (isBound && (ignitionStatus.equals("ON") && !truckRPM.equals("0"))) {
                isWiredObdConnected = true;
            }

            // check WIFI connection
            if (!isWiredObdConnected && isAlsNetworkConnected) {    // check ALS SSID connection
                tcpClient.sendMessage("123456,can");
            }
        }else{
            lastVehSpeed = -1;
            SharedPref.setLoginAllowedStatus(true, getApplicationContext());
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.LogInfo("service", "---------onStartCommand Service");

        try {
            offsetFromUTC = (int) global.GetDriverTimeZoneOffSet(getApplicationContext());
            ObdPreference = SharedPref.getObdPreference(getApplicationContext());
            String pingStatus = SharedPref.isPing(getApplicationContext());

            if (pingStatus.equals(ConstantsKeys.ClearUnIdentifiedData)) {

                // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
                if (!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {

                    // check Unidentified event occurrence
                    //  saveUnidentifiedEventStatusOld(-1, false);

                    String LastDutyStatus = SharedPref.getUnIdenLastDutyStatus(getApplicationContext());
                    if (LastDutyStatus.equals("DR") || LastDutyStatus.equals("OD")) {
                        EndOnDutyOrDrivingRecords("");
                    } else {
                        // if event already ended in offline but not posted to server
                        if (CompanyId.length() > 0) {
                            JSONArray unPostedArray = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);
                            if (unPostedArray.length() > 0 && Globally.isConnected(getApplicationContext())) {
                                SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
                            }
                        }
                    }

                    postEventsToServer();
                }

            } else {
                if (ObdPreference == Constants.OBD_PREF_BLE) {
                    // int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
                    if (TruckID.length() > 0 && CompanyId.length() > 0) {
                        if (pingStatus.equals("ble_start")) {

                            if (!global.isBleEnabled(getApplicationContext())) {
                                startBleService();

                                Globally.PlayNotificationSound(getApplicationContext());
                                Globally.ShowLocalNotification(getApplicationContext(),
                                        getString(R.string.ble_disabled),
                                        getString(R.string.ble_enabled_desc), 2097);
                            } else {
                                boolean isConnected = HTBleSdk.Companion.getInstance().isConnected();
                                if (!BleDataService.isBleConnected || !isConnected) { // if device not `connected
                                    startBleService();
                                }
                            }
                            // }
                        }
                    }
                } else if (ObdPreference == Constants.OBD_PREF_WIRED) {
                    BindConnection();
                    StartStopServer(constants.WiredOBD);
                } else {
                    checkWifiOBDConnection();
                }

                if (Globally.LATITUDE.length() < 4) {
                    startLocationService();
                }

            }

            SharedPref.SetPingStatus("", getApplicationContext());


            if (TruckID.length() > 0 && constants.isMalDiaAllowed(getApplicationContext()) && Globally.isConnected(getApplicationContext())) {

                boolean isPowerUnclearedEvent = malfunctionDiagnosticMethod.isPowerUnclearedEvent(dbHelper);

                if (!isPowerUnclearedEvent) {
                    GetMalDiaEventsDurationList();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }



    private void startBleService(){
        if(TruckID.length() > 0 && CompanyId.length() > 0) {
            Intent serviceIntent = new Intent(getApplicationContext(), BleDataService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }
            startService(serviceIntent);
        }
    }


    void sendBroadcast(boolean isConnected, String IsEldEcmALert){
        try{
            Intent intent = new Intent(ConstantsKeys.IsEventUpdate);
            intent.putExtra(ConstantsKeys.IsEventUpdate, true);
            intent.putExtra(ConstantsKeys.Status, isConnected);
            intent.putExtra(ConstantsKeys.IsEldEcmALert, IsEldEcmALert);

            LocalBroadcastManager.getInstance(AfterLogoutService.this).sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            // Logger.LogError(TAG, "-----Running Logout timerTask");

            try {
                if (SharedPref.IsDriverLogin(getApplicationContext())) {
                    Logger.LogError("Log", "--stop, Driver logged in");
                    
                    StopService();

                } else {

                    if(TruckID.length() == 0){
                        TruckID             = SharedPref.getTruckNumber(getApplicationContext());   //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext());
                        CompanyId           = DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext());
                    }

                    // communicate with wired OBD server app with Message
                    if (ObdPreference == Constants.OBD_PREF_BLE) {

                        if (!SharedPref.IsDriverLogin(getApplicationContext())) {

                            if (!BleDataService.isBleConnected || !HTBleSdk.Companion.getInstance().isConnected()) { // if device not `connected

                                if (SpeedCounter == 0 || SpeedCounter == 30) {
                                    startBleService();
                                }

                                if (!BleDataService.isBleConnected) {
                                    sendBroadcast(false, "");

                                    truckRPM = SharedPref.getRPM(getApplicationContext());
                                    ignitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());
                                    EngineSeconds = "0";
                                    currentHighPrecisionOdometer = "0";

                                    // call rule method to check malfunction/diagnostic when disconnected
                                    checkObdDataWithRule(-1);

                                }
                            }

                        }

                        // Logger.LogError(TAG, "-----Running timer htble 1");
                    } else if (ObdPreference == Constants.OBD_PREF_WIRED) {

                        checkWiredObdConnection();

                        if (isWiredCallBackCalled == false && SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED) {
                            StartStopServer(constants.WiredOBD);
                        } else {

                            String obdLastCallDate = SharedPref.getWiredObdServerCallTime(getApplicationContext());
                            if (obdLastCallDate.length() > 10) {
                                int lastCalledDiffInSec = constants.getSecDifference(obdLastCallDate, Globally.GetDriverCurrentDateTime(global, getApplicationContext()));
                                if (lastCalledDiffInSec >= 20) {
                                    StartStopServer(constants.WiredOBD);
                                }
                            } else {
                                StartStopServer(constants.WiredOBD);
                            }

                        }

                    } else {
                        checkWifiOBDConnection();
                    }

                    // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
                    if (apiCallCount == 0) {

                        if (!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {
                            if (CompanyId.length() > 0) {

                                if(SharedPref.isUnidentifiedDiaEvent(getApplicationContext()) == false) {
                                    if (VehicleObdSpeed >= 8) {

                                        // String odometer = Constants.Convert2DecimalPlacesDouble(Double.parseDouble(Constants.meterToKmWithObd(currentHighPrecisionOdometer)));
                                        String odometer = Constants.getBeforeDecimalValues(Constants.meterToKmWithObd(currentHighPrecisionOdometer));
                                        VinNumber = getVin();

                                        // save unidentified events if vehicle is in motion for 30 min in last 24 hour
                                        malfunctionDiagnosticMethod.saveVehicleMotionStatus(odometer, EngineSeconds,
                                                Integer.parseInt(CompanyId), TruckID, VinNumber, VehicleObdSpeed, dbHelper,
                                                constants, driverPermissionMethod, obdUtil, getApplicationContext());
                                    }

                                }

                                checkUnAssignedReqStatus();
                            }

                            postEventsToServer();




                        }

                        uploadVehPowerEvents();
                        SaveAndUpdateUnidentifiedRecordsApi(null);

                    }

                    apiCallCount++;
                    if (apiCallCount > 2) {
                        apiCallCount = 0;
                    }


                    final boolean isGpsEnabled = constants.CheckGpsStatusToCheckMalfunction(getApplicationContext());
                    if (!isGpsEnabled) {
                        Globally.GPS_LATITUDE = "";
                        Globally.GPS_LONGITUDE = "";

                        if (SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                            Globally.LATITUDE = "";
                            Globally.LONGITUDE = "";
                        }

                    }

                    // check positioning malfunction event ..
                    checkPositionMalfunction(currentHighPrecisionOdometer, global.GetDriverCurrentDateTime(global, getApplicationContext()));



                }
            }catch (Exception e){
                e.printStackTrace();
            }

            if (SpeedCounter >= 60) {
                SpeedCounter = 0;
            }else{
                SpeedCounter += 10;
            }

        }
    };


    private void uploadVehPowerEvents(){
       JSONArray eventArray = vehiclePowerEventMethod.getVehPowerEventArray(dbHelper);
        if(eventArray.length() > 0 && Globally.isConnected(getApplicationContext())) {
            saveDriverLogPost.PostDriverLogData(eventArray, APIs.SAVE_ENGINE_ON_OFF_EVENTS, Constants.SocketTimeout20Sec,
                    false, false, 0, SaveVehPwrEventLog);
        }
    }


    // post occurred unidentified events to server
    private void postEventsToServer(){
        JSONArray array = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);

        if (global.isConnected(getApplicationContext()) && array.length() > 0 && isDataAlreadyPosting == false) {
            Logger.LogDebug("array","array: " +array);

            isDataAlreadyPosting = true;
            saveDriverLogPost.PostDriverLogData(array,APIs.MALFUNCTION_DIAGNOSTIC_EVENT, Constants.SocketTimeout20Sec,
                    false, false, 1, 101);

            //  constants.saveTempUnidentifiedLog(array.toString(), obdUtil);
        }
    }


    /*================== Get Unidentified Records ===================*/
    void GetMalDiaEventsDurationList(){

        if(!isMalEventApiInProgress){
            VinNumber = getVin();
            isMalEventApiInProgress = true;
            String startEventDate = Globally.GetCurrentJodaDateTime().minusDays(5).toString();

            params = new HashMap<String, String>();
            params.put(ConstantsKeys.UnitNo, TruckID);
            params.put(ConstantsKeys.VIN, VinNumber);
            params.put(ConstantsKeys.EventDateTime, startEventDate);

            GetMalfunctionEvents.executeRequest(Request.Method.POST, APIs.GET_MALFUNCTION_LIST_BY_TRUCK , params, GetMalDiaEventDuration,
                    Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
        }
    }



    void CheckUnassignedRequired(){

        JSONObject eventData = new JSONObject();
        try {
            eventData.put(ConstantsKeys.TruckNumber, TruckID);
            eventData.put(ConstantsKeys.CompanyId, CompanyId);
            eventData.put(ConstantsKeys.VIN, VinNumber);

            UnassignedRequiredRequest.SaveLogJsonObj(eventData, APIs.CHECK_IS_UNASSIGNED_REQUIRED,
                    Constants.SocketTimeout10Sec,false, false, 1, 1001);

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Logger.LogError("TTS", "This Language is not supported");
            }

        } else {
            Logger.LogError("TTS", "Initilization Failed!");
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
        }catch (Exception e){  }

    }

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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.LogDebug(TAG,"---onBind");
        return null;
    }




    // Speak Out Msg
    void SpeakOutMsg(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void StartStopServer(final String value){

        SharedPref.SetWiredObdServerCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

        if(isBound){

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Setup the message for invocation
                    try{
                        Logger.LogDebug(TAG_OBD, "Invocation Failed!!");

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
                        Logger.LogDebug(TAG_OBD, "Invocation Failed!!");
                    }
                }
            });

        }else{
            try{
                Logger.LogDebug(TAG_OBD, "Service is Not Bound!!");
                this.connection = new RemoteServiceConnection();
                BindConnection();
            }catch (Exception e){
                // e.printStackTrace();
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



    //  ------------- WiFI OBD data response handler ----------
    TcpClient.OnMessageReceived obdResponseHandler = new TcpClient.OnMessageReceived() {
        @Override
        public void messageReceived(String message) {
            Logger.LogDebug("response", "OBD Response: " +message);

            try {
                String correctData = "";

                SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                if (!message.equals(noObd) && message.length() > 10) {

                    if (message.contains("CAN")) {

                        if (!message.contains("CAN:UNCONNECTED")) {

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


                                int GPSSpeed = Integer.valueOf(wifiConfig.checkJsonParameter(canObj, "GPSSpeed", "0"));
                                WheelBasedVehicleSpeed = Integer.valueOf(wifiConfig.checkJsonParameter(canObj, "WheelBasedVehicleSpeed", "0"));

                                if (WheelBasedVehicleSpeed == 0) {
                                    WheelBasedVehicleSpeed = GPSSpeed;
                                }

                                truckRPM = wifiConfig.checkJsonParameter(canObj, "RPMEngineSpeed", "0");
                                ignitionStatus = wifiConfig.checkJsonParameter(canObj, "EngineRunning", "false");

                                Globally.LATITUDE = wifiConfig.checkJsonParameter(canObj, "GPSLatitude", "");
                                Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(wifiConfig.checkJsonParameter(canObj, "GPSLongitude", ""));
                                String HighResolutionDistance = wifiConfig.checkJsonParameter(canObj, "HighResolutionTotalVehicleDistanceInKM", "-1");
                                String obdEngineHours = wifiConfig.checkJsonParameter(canObj, "EngineHours", "0");

                                String vin = wifiConfig.checkJsonParameter(canObj, ConstantsKeys.VIN, "");
                                if(vin.length() <= 5){
                                    vin = "";
                                }
                                SharedPref.setVehicleVin(vin, getApplicationContext());
                                SharedPref.SetObdEngineHours(obdEngineHours, getApplicationContext());
                                SharedPref.SetObdOdometer( Constants.meterToKmWithObd(HighResolutionDistance), getApplicationContext());
                                SharedPref.SetObdOdometerInMiles(Constants.meterToMilesWith2DecPlaces(currentHighPrecisionOdometer), getApplicationContext());


                                if(Globally.LATITUDE.length() < 5){
                                    SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());
                                }else{
                                    SharedPref.SetLocReceivedFromObdStatus(true, getApplicationContext());
                                }

                                if (ignitionStatus.equals("true")) {
                                    SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());


                                    if (WheelBasedVehicleSpeed > 8 && lastVehSpeed > 8) {

                                        showNotificationAlert((int) WheelBasedVehicleSpeed);

                                        if(SharedPref.IsAppRestricted(getApplicationContext())) {
                                            SharedPref.setLoginAllowedStatus(false, getApplicationContext());
                                        }

                                        //  Globally.ShowLogoutNotificationWithSound(getApplicationContext(), "ELD", AlertMsg, mNotificationManager);
                                    } else {
                                        SharedPref.setLoginAllowedStatus(true, getApplicationContext());
                                    }


                                    lastVehSpeed = WheelBasedVehicleSpeed;

                                    constants.saveEcmLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE, HighResolutionDistance, getApplicationContext());

                                } else {
                                    if(SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED) {
                                        SharedPref.setLoginAllowedStatus(true, getApplicationContext());
                                        SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED,  global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                                global.GetCurrentUTCTimeFormat(), getApplicationContext());
                                    }
                                    lastVehSpeed = -1;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }else{
                        lastVehSpeed = -1;
                    }

                }
            }catch (Exception e){
                ignitionStatus = "false";
                e.printStackTrace();
            }

        }
    };








    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onDestroy() {

        if(!isStopService) {

            Intent intent = new Intent(Constants.packageName);
            intent.putExtra("location", "torestore");
            sendBroadcast(intent);
        }else{
            Logger.LogDebug(TAG, "Service stopped" );

            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                //  ------------- BLE OBD ----------
                if (TruckID.length() > 0 && CompanyId.length() > 0) {
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                }
            }else {
                //  ------------- Wired OBD ----------
                if(isBound){
                    StartStopServer("stop");
                    this.unbindService(connection);
                    isBound = false;
                }
            }

            if(locServiceIntent != null){
                stopService(locServiceIntent);
            }

            super.onDestroy();

        }



    }




    private void saveDrivingStatusForUnidentifiedDia(int speed){

        try {

            if(CompanyId.length() > 0) {
                JSONArray savedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
                JSONArray unPostedArray = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);
                LastDutyStatus = "DR";
                StatusStartTime = Globally.GetCurrentUTCTimeFormat();
                TempStatus = "";

                //   String odometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));
                String currentOdometer = Constants.getBeforeDecimalValues(SharedPref.getObdOdometer(getApplicationContext()));
                String currentEngHrs = Constants.get2DecimalEngHour(getApplicationContext());
                VinNumber = getVin();


                JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj("", CompanyId, VinNumber, TruckID,
                        LastDutyStatus, StatusStartTime, "", Globally.LATITUDE, Globally.LONGITUDE, "", "",
                        currentEngHrs, "", currentOdometer, "", false,
                        false, "0", false,
                        Constants.getLocationType(getApplicationContext()), Origin);

                SharedPref.SaveUnidentifiedIntermediateRecord(currentOdometer, Globally.GetCurrentUTCTimeFormat(), Globally.LATITUDE, Globally.LONGITUDE,
                        currentEngHrs, getApplicationContext());

                SharedPref.setUnIdenLastDutyStatus(LastDutyStatus, getApplicationContext());
                savedUnidentifiedLogArray.put(jsonObject);
                unPostedArray.put(jsonObject);

                hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, savedUnidentifiedLogArray);


                if (Globally.isConnected(getApplicationContext())) {
                    SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
                }

                Globally.PlaySound(getApplicationContext());

                Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                        getString(R.string.UnIdentifiedDrivingOcc), 2005);


                // save start driving event record time
                saveVehMotionStatusForUnidentified(StatusStartTime, speed, true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    private void UnidentifiedStatusChange(int obdSpeed, int continueSpeedZeroDiff) {

        try {
           /* if (LastDutyStatus.equals("DR")) {
                CurrentDutyStatus = ThreshHoldSpeed == 0 ? "OD" : "DR";
            } else {
                CurrentDutyStatus = obdSpeed >= ThreshHoldSpeed ? "DR" : "OD";
            }*/

            boolean isDriving = ( (LastDutyStatus.equals("") || LastDutyStatus.equals("OD")) && obdSpeed >= ThreshHoldSpeed);   //CurrentDutyStatus.equals("DR") &&
            boolean isOnDuty =  (LastDutyStatus.equals("DR") && obdSpeed == 0 && continueSpeedZeroDiff > OnDutyRecordTime);    //CurrentDutyStatus.equals("OD") &&
            VinNumber = getVin();

            if(obdSpeed == 0 && LastDutyStatus.equals("OD")){
                SharedPref.setContinueSpeedZeroTime(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
            }
            if (isDriving || isOnDuty) {

                UnAssignedVehicleMilesId = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());
                IntermediateLogId = SharedPref.getIntermediateLogId(getApplicationContext());


                JSONArray savedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
                JSONArray unPostedArray = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);

                String currentDate = Globally.GetCurrentUTCTimeFormat();
                //   String currentOdometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));
                String currentOdometer = Constants.getBeforeDecimalValues(SharedPref.getObdOdometer(getApplicationContext()));
                String currentEngHrs   = Constants.get2DecimalEngHour(getApplicationContext());

                if (savedUnidentifiedLogArray.length() > 0) {
                    JSONObject obj = (JSONObject) savedUnidentifiedLogArray.get(savedUnidentifiedLogArray.length() - 1);
                    String UTCEndDateTime = obj.getString(ConstantsKeys.UTCEndDateTime);
                    boolean Intermediate = obj.getBoolean(ConstantsKeys.Intermediate);

                    if (UTCEndDateTime.length() == 0 || Intermediate) {

                        // get last update object from unPosted Unidentified Log array
                        JSONObject unPostedLogLastObj = hMethods.updateLastRecordOfUnIdentifiedLog(unPostedArray, UnAssignedVehicleMilesId,
                                currentDate, currentOdometer, Globally.LATITUDE, Globally.LONGITUDE, currentEngHrs, false);

                        if (unPostedLogLastObj != null && unPostedArray.length() > 0) {
                            unPostedArray.put(unPostedArray.length() - 1, unPostedLogLastObj);
                        }

                        // get last update object from posted/unposted Unidentified Log array
                        JSONObject savedUnidentifiedLogLastObj = hMethods.updateLastRecordOfUnIdentifiedLog(savedUnidentifiedLogArray,
                                UnAssignedVehicleMilesId, currentDate,
                                currentOdometer, Globally.LATITUDE, Globally.LONGITUDE, currentEngHrs, false);

                        if (savedUnidentifiedLogLastObj != null && savedUnidentifiedLogArray.length() > 0) {
                            savedUnidentifiedLogArray.put(savedUnidentifiedLogArray.length() - 1, savedUnidentifiedLogLastObj);

                            if(unPostedArray.length() == 0){
                                unPostedArray.put(savedUnidentifiedLogLastObj);
                            }
                        }

                    }
                }




                Intermediate = false;
                IntermediateUpdate = false;
                StatusStartTime = currentDate;

                if (isDriving) {
                    LastDutyStatus = "DR";
                    // update last saved Intermediate records
                    SharedPref.SaveUnidentifiedIntermediateRecord(currentOdometer, currentDate,
                            Globally.LATITUDE, Globally.LONGITUDE, currentEngHrs, getApplicationContext());

                } else {
                    LastDutyStatus = "OD";
                    // clear last saved records
                    SharedPref.SaveUnidentifiedIntermediateRecord("", "", "", "", "",
                            getApplicationContext());

                }

                // clear vehicle miles ID and intermediate Log ID for new record
                SharedPref.setUnAssignedVehicleMilesId("0", getApplicationContext());
                SharedPref.setIntermediateLogId("0", getApplicationContext());

                JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj("", CompanyId, VinNumber, TruckID,
                        LastDutyStatus, StatusStartTime, "", Globally.LATITUDE, Globally.LONGITUDE, "", "",
                        currentEngHrs, "", currentOdometer, "", Intermediate,
                        IntermediateUpdate, "", false,
                        Constants.getLocationType(getApplicationContext()), Origin );


                SharedPref.setUnIdenLastDutyStatus(LastDutyStatus, getApplicationContext());
                savedUnidentifiedLogArray.put(jsonObject);
                unPostedArray.put(jsonObject);
                hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, savedUnidentifiedLogArray);

                if (Globally.isConnected(getApplicationContext())) {
                    SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
                }

                Globally.PlaySound(getApplicationContext());

                if(LastDutyStatus.equals("DR")){
                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                            getString(R.string.driving_rec_occurred) , 2005);

                    // save start driving event record time
                    saveVehMotionStatusForUnidentified(currentDate, obdSpeed, true);

                }else {
                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                            getString(R.string.onduty_rec_occurred), 2005);

                    // save end driving event record time
                    saveVehMotionStatusForUnidentified(currentDate, obdSpeed, false);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        // Check intermediate records while Driving.............
        IntermediateRecords();

    }




    private void IntermediateRecords() {

        try {
            VinNumber = getVin();
            String startTime = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartTime, getApplicationContext());
            if (startTime.length() > 10) {

                int timeDiff = constants.minDiffFromUTC(startTime, global, getApplicationContext());
                if (timeDiff >= intermediateRecordTime && LastDutyStatus.equals("DR")) {
                    JSONArray SavedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
                    JSONArray unPostedArray             = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);

                    LastDutyStatus = "DR";

                    Intermediate = true;
                    IntermediateUpdate = true;
                    UnAssignedVehicleMilesId = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());
                    IntermediateLogId = SharedPref.getIntermediateLogId(getApplicationContext());

                    String currentTime = Globally.GetCurrentUTCTimeFormat();

                    // String currentOdometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));
                    String currentOdometer = Constants.getBeforeDecimalValues(SharedPref.getObdOdometer(getApplicationContext()));
                    String currentEngHrs   = Constants.get2DecimalEngHour(getApplicationContext());


                    if(hMethods.IsAlreadyIntermediate(Integer.parseInt(CompanyId), dbHelper)){
                        String UnidenStartTime = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartTime, getApplicationContext());
                        String startOdometer = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartOdometer, getApplicationContext());
                        String startLatitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLatitude, getApplicationContext());
                        String startLongitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLongitude, getApplicationContext());
                        String startEngineSeconds = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartEngineSeconds, getApplicationContext());

                        JSONObject lastObj = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                                LastDutyStatus, UnidenStartTime, currentTime, startLatitude, startLongitude, Globally.LATITUDE, Globally.LONGITUDE,
                                startEngineSeconds, currentEngHrs,  startOdometer, currentOdometer,  Intermediate,
                                IntermediateUpdate, IntermediateLogId, false,
                                Constants.getLocationType(getApplicationContext()), Origin);
                        SavedUnidentifiedLogArray.put(SavedUnidentifiedLogArray.length()-1, lastObj);
                        unPostedArray.put(lastObj);

                    }


                    JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                            LastDutyStatus, currentTime, currentTime, Globally.LATITUDE, Globally.LONGITUDE,
                            Globally.LATITUDE, Globally.LONGITUDE, currentEngHrs, currentEngHrs,
                            currentOdometer, currentOdometer, Intermediate,false, "0",
                            false, Constants.getLocationType(getApplicationContext()), Origin);

                    SharedPref.setUnIdenLastDutyStatus(LastDutyStatus, getApplicationContext());
                    SavedUnidentifiedLogArray.put(jsonObject);
                    unPostedArray.put(jsonObject);
                    hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, SavedUnidentifiedLogArray);

                    SharedPref.SaveUnidentifiedIntermediateRecord(currentOdometer, currentTime,
                            Globally.LATITUDE, Globally.LONGITUDE, currentEngHrs, getApplicationContext());

                    if (Globally.isConnected(getApplicationContext())) {
                        SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
                    }


                    Globally.PlaySound(getApplicationContext());

                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                            getString(R.string.Inter_record_occ) , 2005);


                }
            }else{
                if(LastDutyStatus.equals("DR")){
                    String currentOdometer = Constants.getBeforeDecimalValues(SharedPref.getObdOdometer(getApplicationContext()));
                    SharedPref.SaveUnidentifiedIntermediateRecord(currentOdometer, Globally.GetCurrentUTCTimeFormat(),
                            Globally.LATITUDE, Globally.LONGITUDE, Constants.get2DecimalEngHour(getApplicationContext()),
                            getApplicationContext());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void EndOnDutyOrDrivingRecords(String DutyStatus) {

        try {

            if(CompanyId.length() > 0) {
                VinNumber = getVin();
                JSONArray SavedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
                JSONArray unPostedArray = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);

                boolean IsIntermediateEvent = false;
                String lastDutyStatus = "";

                if (DutyStatus.length() == 0) {
                    lastDutyStatus = SharedPref.getUnIdenLastDutyStatus(getApplicationContext());
                } else {
                    lastDutyStatus = DutyStatus;
                }

                String startTime = "", startOdometer = "", startLatitude = "", startLongitude = "", startEngineSeconds = "";

                if (SavedUnidentifiedLogArray.length() > 0) {
                    JSONObject obj = (JSONObject) SavedUnidentifiedLogArray.get(SavedUnidentifiedLogArray.length() - 1);
                    IsIntermediateEvent = obj.getBoolean(ConstantsKeys.Intermediate);

                    startTime = obj.getString(ConstantsKeys.UTCStartDateTime);
                    startOdometer = obj.getString(ConstantsKeys.StartOdometer);
                    startLatitude = obj.getString(ConstantsKeys.StartLatitude);
                    startLongitude = obj.getString(ConstantsKeys.StartLongitude);
                    startEngineSeconds = obj.getString(ConstantsKeys.StartEngineSeconds);
                }

                if (IsIntermediateEvent) {
                    startTime = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartTime, getApplicationContext());
                    startOdometer = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartOdometer, getApplicationContext());
                    startLatitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLatitude, getApplicationContext());
                    startLongitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLongitude, getApplicationContext());
                    startEngineSeconds = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartEngineSeconds, getApplicationContext());
                }

                UnAssignedVehicleMilesId = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());
                IntermediateLogId = SharedPref.getIntermediateLogId(getApplicationContext());
                Intermediate = false;   //hMethods.IsAlreadyIntermediate(Integer.parseInt(CompanyId), dbHelper);
                IntermediateUpdate = false;  //Intermediate;


                // String currentOdometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));
                String currentOdometer = Constants.getBeforeDecimalValues(SharedPref.getObdOdometer(getApplicationContext()));
                String currentEngHrs = Constants.get2DecimalEngHour(getApplicationContext());

                JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                        lastDutyStatus, startTime, Globally.GetCurrentUTCTimeFormat(), startLatitude, startLongitude,
                        Globally.LATITUDE, Globally.LONGITUDE, startEngineSeconds, currentEngHrs,
                        startOdometer, currentOdometer, Intermediate, IntermediateUpdate, IntermediateLogId,
                        false, Constants.getLocationType(getApplicationContext()), Origin);

                SavedUnidentifiedLogArray.put(jsonObject);
                unPostedArray.put(jsonObject);
                hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, SavedUnidentifiedLogArray);

                SharedPref.SaveUnidentifiedIntermediateRecord(currentOdometer, Globally.GetCurrentUTCTimeFormat(),
                        Globally.LATITUDE, Globally.LONGITUDE, currentEngHrs, getApplicationContext());

                if (Globally.isConnected(getApplicationContext())) {
                    SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
                }

                // save end driving event record time
                if (lastDutyStatus.equals("DR")) {
                    saveVehMotionStatusForUnidentified(Globally.GetCurrentUTCTimeFormat(), -1, false);
                }

                String NotificationMessage = "";
                if (DutyStatus.length() == 0) {
                    if (lastDutyStatus.equals("DR")) {
                        NotificationMessage = "Unidentified Driving Record ended as Driver logged in.";
                    } else {
                        NotificationMessage = "Unidentified On Duty Record ended as Driver logged in.";
                    }
                } else {
                    if (lastDutyStatus.equals("DR")) {
                        NotificationMessage = "Unidentified Driving Record ended as Vehicle info is not received.";
                    } else {
                        NotificationMessage = "Unidentified On Duty Record ended as Vehicle info is not received.";
                    }
                }

                Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld), NotificationMessage, 2005);

                LastDutyStatus = "";
                UnAssignedVehicleMilesId = "";
                IntermediateLogId = "";
                SharedPref.setUnIdenLastDutyStatus("", getApplicationContext());
                SharedPref.setUnAssignedVehicleMilesId("0", getApplicationContext());
                SharedPref.setIntermediateLogId("0", getApplicationContext());

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void SaveAndUpdateUnidentifiedRecordsApi(JSONArray unPostedLogArray) {
        // remove duplicate records if Exist

        if(CompanyId.length() > 0) {
            if (unPostedLogArray == null) {
                unPostedLogArray = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);
            }

            // remove duplicate entries if exist
            unPostedLogArray = removeDuplicateRecordIfCreated(unPostedLogArray);

            if (unPostedLogArray.length() > 0 && !UnidentifiedApiInProgress && global.isConnected(getApplicationContext() )) {
                UnidentifiedApiInProgress = true;
                SaveUpdateUnidentifiedApi.PostDriverLogData(unPostedLogArray, APIs.ADD_UNIDENTIFIED_RECORD, Constants.SocketTimeout20Sec,
                        SaveUnidentifiedEvent);
            }
        }
    }


    private JSONArray removeDuplicateRecordIfCreated(JSONArray array){
        try{
            String lastEventStartTime = "";
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject obj = (JSONObject) array.get(i);
                String startDateTime = obj.getString(ConstantsKeys.UTCStartDateTime);
                if(startDateTime.equals(lastEventStartTime)){
                    Logger.LogDebug("RemoveEvent", ">>> Remove duplicate event: " +obj);
                    array.remove(i-1);

                }
                lastEventStartTime = startDateTime;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return array;
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


    DriverLogResponse jsonApiResponse = new DriverLogResponse() {

        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, JSONArray inputArray) {

            try {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ConstantsKeys.Status);

                if (status.equalsIgnoreCase("true")) {
                    SharedPref.setCheckUnassignedReqTime(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());

                    if (obj.getString(ConstantsKeys.Message).equals("Need to Save")) {
                        IsUnassignedRequired = true;
                    } else {
                        IsUnassignedRequired = false;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }



        }

        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {

        }
    };

    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void getResponse(String response, int flag) {

            try {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ConstantsKeys.Status);

                if (!SharedPref.IsDriverLogin(getApplicationContext())) {
                    if (status.equalsIgnoreCase("true")) {

                        if(flag == GetMalDiaEventDuration){
                            isMalEventApiInProgress = false;
                            try {

                                JSONArray durationArray = new JSONArray();
                                JSONArray dataArray = new JSONArray(obj.getString(ConstantsKeys.Data));

                                SharedPref.saveUnidentifiedEventStatus(false, "", getApplicationContext());

                                for(int i = dataArray.length()-1 ; i >= 0 ; i--){
                                    JSONObject objItem = (JSONObject) dataArray.get(i);

                                    String EngineHours = "",StartOdometer = "", ClearEngineHour = "", ClearOdometer = "";
                                    int TotalMinutes = 0;

                                    if(!objItem.isNull(ConstantsKeys.EngineHours)){
                                        EngineHours = objItem.getString(ConstantsKeys.EngineHours);

                                    }

                                    if(!objItem.getString(ConstantsKeys.ClearEngineHours).equals("null")){
                                        ClearEngineHour = objItem.getString(ConstantsKeys.ClearEngineHours);
                                    }


                                    String VIN = "";
                                    if(!objItem.isNull(ConstantsKeys.VIN)){
                                        VIN = objItem.getString(ConstantsKeys.VIN);
                                    }

                                    // !objItem.isNull(ConstantsKeys.StartOdometer)
                                    if(!objItem.getString(ConstantsKeys.StartOdometer).equals("null")){
                                        StartOdometer = objItem.getString(ConstantsKeys.StartOdometer);
                                        if(StartOdometer.length() < 8) {
                                            StartOdometer = Constants.kmToMeter1(StartOdometer);
                                        }
                                    }

                                    if(!objItem.getString(ConstantsKeys.ClearOdometer).equals("null")){
                                        ClearOdometer = objItem.getString(ConstantsKeys.ClearOdometer);
                                        if(ClearOdometer.length() < 8 || ClearOdometer.contains(".")) {
                                            ClearOdometer = Constants.kmToMeter1(ClearOdometer);
                                        }
                                    }

                                    String DetectionDataEventCode = objItem.getString(ConstantsKeys.DetectionDataEventCode);
                                    String EventDateTime = objItem.getString(ConstantsKeys.EventDateTime);

                                    String LocationType = "";
                                    if(obj.has(ConstantsKeys.LocationType)){
                                        LocationType = obj.getString(ConstantsKeys.LocationType);
                                    }
                                    String CurrentStatus = "";
                                    if(obj.has(ConstantsKeys.CurrentStatus)){
                                        CurrentStatus = obj.getString(ConstantsKeys.CurrentStatus);
                                    }

                                    if(DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic)){
                                        TotalMinutes = (int)constants.getEngineHourDiff(EngineHours, ClearEngineHour);
                                    }else{
                                        TotalMinutes = objItem.getInt(ConstantsKeys.TotalMinutes);
                                    }

                                    boolean IsClearEvent = objItem.getBoolean(ConstantsKeys.IsClearEvent);
                                    JSONObject item = malfunctionDiagnosticMethod.getNewMalDiaDurationObj(
                                            "0",
                                            EventDateTime,
                                            objItem.getString(ConstantsKeys.EventEndDateTime),
                                            DetectionDataEventCode,
                                            TotalMinutes,
                                            IsClearEvent,
                                            ClearEngineHour,
                                            ClearOdometer,
                                            StartOdometer,
                                            EngineHours,
                                            LocationType,
                                            CurrentStatus,
                                            VIN);

                                    durationArray.put(item);
                                    //  }

                                    //  DateTime selectedTime = global.getDateTimeObj(EventDateTime, false);
                                    // int minDiff = Constants.getMinDiff(selectedTime, global.GetCurrentJodaDateTime());
                                    // long minDiff = Constants.getDateTimeDuration(selectedTime, Globally.GetCurrentUTCDateTime()).getStandardMinutes();
                                    if(DetectionDataEventCode.equals(Constants.UnIdentifiedDrivingDiagnostic) && !IsClearEvent){   //minDiff < Constants.TotalMinInADay
                                        SharedPref.saveUnidentifiedEventStatus(true, EventDateTime, getApplicationContext());
                                    }

                                }

                                malfunctionDiagnosticMethod.MalDiaDurationHelper(dbHelper, durationArray);

                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());

                                //   Logger.LogDebug("----durationArray", ">>>>durationArray: "+ durationArray);


                            }catch (Exception e){
                                e.printStackTrace();
                            }



                        }else {
                            Logger.LogDebug("----EventCreated", "Event Flag: "+ SaveUnidentifiedEvent);

                            JSONObject resultJson = obj.getJSONObject(ConstantsKeys.Data);
                            UnAssignedVehicleMilesId = resultJson.getString(ConstantsKeys.UnAssignedVehicleMilesId);
                            IntermediateLogId = resultJson.getString(ConstantsKeys.IntermediateLogId);
                            SharedPref.setUnAssignedVehicleMilesId(UnAssignedVehicleMilesId, getApplicationContext());
                            SharedPref.setIntermediateLogId(IntermediateLogId, getApplicationContext());

                            // update unposted event to posted in table
                            hMethods.updateUploadedStatusInArray(CompanyId, dbHelper);

                            //hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, new JSONArray());
                            UnidentifiedApiInProgress = false;

                        }

                    }else{
                        if(flag == SaveUnidentifiedEvent){
                            UnidentifiedApiInProgress = false;
                        }else if(flag == GetMalDiaEventDuration){
                            isMalEventApiInProgress = false;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };




    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {

            if(error != null) {
                Logger.LogDebug("error", "error-" + flag + " : " + error);
            }
                if (flag == SaveUnidentifiedEvent) {
                    UnidentifiedApiInProgress = false;
                }else if (flag == GetMalDiaEventDuration){
                    isMalEventApiInProgress = false;
                }


        }
    };

    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int driver_id, int flag, JSONArray inputData) {

            try {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString("Status");
                // String Message = obj.getString("Message");

                isDataAlreadyPosting = false;
                if (status.equals("true")) {

                    if(flag == SaveMalDiagnstcEvent) {
                        Logger.LogDebug("SaveMalDiagnstcEvent", "SaveMalDiagnstcEvent saved successfully");
                        isMalfncDataAlreadyPosting = false;
                        powerEventInputArray = new JSONArray();

                        // clear malfunction array
                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());

                        apiCallCount = 0;

                    }else if(flag == ClearMalDiaEvent){

                        malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, "0", ClearEventType, true, true, getApplicationContext());
                        boolean isPowerUnclearedEvent = malfunctionDiagnosticMethod.isPowerUnclearedEvent(dbHelper);

                        String desc = getString(R.string.event_clear_desc);
                        if(ClearEventType.equals(Constants.PowerComplianceDiagnostic)){
                            powerEventInputArray = new JSONArray();
                            desc = getString(R.string.power_dia_clear_desc);
                        }

//                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());

                        global.ShowLocalNotification(getApplicationContext(),
                                getString(R.string.dia_event), desc, 2090);

                        Globally.PlaySound(getApplicationContext());


                        if(!isPowerUnclearedEvent) {
                            GetMalDiaEventsDurationList();
                        }

                    }else if(flag == SaveVehPwrEventLog){
                        vehiclePowerEventMethod.VehPowerEventHelper(dbHelper, new JSONArray());
                    }else{
                        // clear malfunction array
                        malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());

                    }


                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }


        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Logger.LogDebug("errorrr ", ">>>error dialog: " );
            isDataAlreadyPosting = false;
        }


    };



    void saveIgnitionStatus(String ignitionStatus){
        String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());

        // save log when ignition status is changed.    //SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getApplicationContext())
        if (lastIgnitionStatus.equals("ON")) {
            // save truck info to check power compliance mal/dia later.
            SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, SharedPref.getObdSourceName(getApplicationContext()),
                    global.GetDriverCurrentDateTime(global, getApplicationContext()), global.GetCurrentUTCTimeFormat(),
                    SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                    SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()),
                    getApplicationContext());

        }
    }




    // check Power Data Compliance Malfunction/Diagnostic event
    private void checkPowerMalDiaEvent(){

        boolean isEventOccurred = false;
        try{

            String currentDate = Globally.GetCurrentUTCTimeFormat();

            if(SharedPref.isPowerMalfunctionOccurred(getApplicationContext()) == false ) {
                isEventOccurred = true;


                boolean isPowerCompMalAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerComplianceMal, getApplicationContext());
                boolean isPowerCompDiaAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerDataDiag, getApplicationContext());

                if ( (isPowerCompMalAllowed || isPowerCompDiaAllowed) && CompanyId.length() > 0) {

                    String PowerEventStatus = constants.isPowerDiaMalOccurred(currentHighPrecisionOdometer, ignitionStatus,
                            EngineSeconds, "0", global, malfunctionDiagnosticMethod, isPowerCompMalAllowed, isPowerCompDiaAllowed,
                            getApplicationContext(), constants, dbHelper, driverPermissionMethod, obdUtil);

                    if (PowerEventStatus.length() > 0) {
                        if (PowerEventStatus.contains(constants.MalfunctionEvent)) {
                            if (isPowerCompMalAllowed) {

                                if (isPowerCompDiaAllowed) {
                                    currentDate = SharedPref.GetTruckInfoOnIgnitionChange(Constants.IgnitionUtcTimeMalDia, getApplicationContext());

                                    // save power diagnostic event also when malfunction occurred
                                    savePowerDiagnosticRecordInTable(currentDate, false);
                                    // update mal/dia status for enable disable according to log
                                    // malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());
                                    SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                }

                                currentDate = global.GetCurrentUTCTimeFormat();

                                // updated values after save diagnostic with truck ignition status because in this case we are saving last ignition time in diagnostic
                                SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, Constants.WiredOBD,
                                        global.GetDriverCurrentDateTime(global, getApplicationContext()), currentDate,
                                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()),
                                        getApplicationContext());


                                savePowerMalfunctionRecordInTable(currentDate, true);
                            }
                        } else {
                            if (isPowerCompDiaAllowed) {

                                // SharedPref.setPowerClearEventCallTime(currentDate, getApplicationContext());
                                currentDate = SharedPref.GetTruckInfoOnIgnitionChange(Constants.IgnitionUtcTimeMalDia, getApplicationContext());

                                savePowerDiagnosticRecordInTable(currentDate, true);

                                Logger.LogDebug("Power event", ">>>>>Power event occurred " + malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper));


                                // update mal/dia status for enable disable according to log
                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());

                                SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                            }
                        }


                    }

                    // updated values after save diagnostic with truck ignition status because in this case we are saving last ignition time in diagnostic
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
                    PowerClearEventCallTime = currentDate;
                    SharedPref.setPowerClearEventCallTime(PowerClearEventCallTime, getApplicationContext());
                }
                String dateee = SharedPref.getPowerMalOccTime(getApplicationContext());
                int secDiff = constants.getSecDifference(dateee, currentDate);
                int callTimeSecDiff = constants.getSecDifference(PowerClearEventCallTime, currentDate);

                // clear Power Diagnostic event after 1 min automatically when ECM is connected.
                if (secDiff > 60 && callTimeSecDiff > 18) {
                    SharedPref.setPowerClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    Logger.LogDebug("Clear event", ">>>>>Clear event occurred " + malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper));

                    powerEventInputArray = new JSONArray();
                    ClearEventUpdate( Constants.PowerComplianceDiagnostic,
                            "Auto clear Power data diagnostic event after ECM data received");

                    if(powerEventInputArray.length() == 0){
                        DateTime clearEventCallTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getApplicationContext()).plusSeconds(40);
                        SharedPref.setPowerClearEventCallTime(clearEventCallTime.toString(), getApplicationContext());

                        if (global.isConnected(getApplicationContext())) {
                            JSONArray savedEvents = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
                            if(savedEvents.length() > 0){
                                SaveMalfnDiagnstcLogToServer(savedEvents, "0");
                            }else {
                                GetMalDiaEventsDurationList();
                            }

                        }
                    }else {
                        malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());

                        if (!SharedPref.isLocDiagnosticOccur(getApplicationContext()) && !SharedPref.isEngSyncDiagnstc(getApplicationContext())) {
                            SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getApplicationContext()),
                                    SharedPref.isMalfunctionOccur(getApplicationContext()),
                                    false,
                                    SharedPref.isSuggestedEditOccur(getApplicationContext()), getApplicationContext());
                        }

                        SharedPref.savePowerMalfunctionOccurStatus(
                                SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                                false, global.GetCurrentUTCTimeFormat(), getApplicationContext());
                    }
                }else{
                    if(callTimeSecDiff < 0){
                        SharedPref.setPowerClearEventCallTime(currentDate, getApplicationContext());
                    }
                }




            }

            // this case is for engine sync event because of truck ignition was not updated if power event was already occurred
            if(!isEventOccurred){   // && SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext()).equals("")
                SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, SharedPref.getObdSourceName(getApplicationContext()),
                        global.GetDriverCurrentDateTime(global, getApplicationContext()), global.GetCurrentUTCTimeFormat(),
                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()), getApplicationContext());
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }




    void savePowerDiagnosticRecordInTable(String currentDate, boolean isUpload){
        try{

            SharedPref.savePowerMalfunctionOccurStatus(
                    SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                    true, currentDate, getApplicationContext());

            constants.saveDiagnstcStatus(getApplicationContext(), true);

            // save occurred event in malfunction/diagnostic table
            saveMalfunctionEventInTable(constants.PowerComplianceDiagnostic, getString(R.string.power_dia_occured),
                    currentDate, isUpload);

            DateTime endDateTime = Globally.getDateTimeObj(Globally.GetCurrentUTCTimeFormat(), false).plusSeconds(90);
            String endDate = Globally.formatDatePatternMilli(endDateTime.toString());

            // save malfunction entry in duration table
            malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, "0",
                    currentDate,
                    endDate,
                    Constants.PowerComplianceDiagnostic,  "-1",
                    Constants.getLocationType(getApplicationContext()), "",
                    constants, getApplicationContext());

        }catch (Exception e){e.printStackTrace();}
    }



    void savePowerMalfunctionRecordInTable(String currentDate, boolean isUpload){
        try{
            SharedPref.savePowerMalfunctionOccurStatus(true,
                    SharedPref.isPowerDiagnosticOccurred(getApplicationContext()),
                    currentDate, getApplicationContext()); //global.GetDriverCurrentDateTime(global, getApplicationContext())

            // save occurred event in malfunction/diagnostic table
            saveMalfunctionEventInTable(constants.PowerComplianceMalfunction,
                    getString(R.string.power_comp_mal_occured),
                    currentDate, isUpload);


            // save malfunction entry in duration table
            malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, "0",
                    global.GetCurrentUTCTimeFormat(),
                    global.GetCurrentUTCTimeFormat(),
                    Constants.PowerComplianceMalfunction, "-1",
                    Constants.getLocationType(getApplicationContext()), "",
                    constants, getApplicationContext());

            // update mal/dia status for enable disable according to log
            malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());

            constants.saveMalfncnStatus(getApplicationContext(), true);
            SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());



        }catch (Exception e){e.printStackTrace();}

    }




    // Save Malfunction/Diagnostic Event Details
    void saveMalfunctionEventInTable(String malDiaType, String MalfunctionDefinition, String occurredTime, boolean isUpload){

        String clearedTime = "", clearTimeOdmeter = "", clearedTimeEngineHour = "";
        JSONObject newOccuredEventObj = new JSONObject();
        String lastSavedOdometer =   SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext());
        String lastSavedEngHr =  SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext());
        String currentOdometer = SharedPref.getObdOdometer(getApplicationContext());
        String currentEngHr = constants.get2DecimalEngHour(getApplicationContext());   //SharedPref.getObdEngineHours(getApplicationContext());
        VinNumber = getVin();

        // save malfunction/diagnostic occur event to server with few inputs
        if(malDiaType.equals(Constants.PowerComplianceDiagnostic) ){
            newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                    "0", "", VinNumber,
                    SharedPref.getTruckNumber(getApplicationContext()),    //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
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
                    "0", "", VinNumber,
                    SharedPref.getTruckNumber(getApplicationContext()),    //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
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
                    "0", "", VinNumber,
                    SharedPref.getTruckNumber(getApplicationContext()),
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


        // call api
        if(isUpload) {
            SaveMalfnDiagnstcLogToServer(malArray, "0");
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
                    saveDriverLogPost.PostDriverLogData(malArray1, APIs.MALFUNCTION_DIAGNOSTIC_EVENT, Constants.SocketTimeout20Sec,
                            false, false, 1, SaveMalDiagnstcEvent);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    // update clear event info in existing log if not uploaded on server yet
    private void ClearEventUpdate(String dataDiagnostic, String clearDesc){
        try{
            String dateee = SharedPref.getPowerMalOccTime(getApplicationContext());
            int minDiff = constants.getMinDifference(dateee, global.GetCurrentUTCTimeFormat());

            boolean isUnPostedEvent = malfunctionDiagnosticMethod.isUnPostedOfflineEvent( dataDiagnostic, dbHelper);

            if(isUnPostedEvent){
                // update clear event array in duration table and not posted to server with duration table input because occured event already exist TABLE_MALFUNCTION_DIANOSTIC
                JSONArray malArray = malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, "0", dataDiagnostic, true,true, getApplicationContext());

                // update offline unposted event array
                malfunctionDiagnosticMethod.updateOfflineUnPostedMalDiaEvent("0", dataDiagnostic,
                        clearDesc, dbHelper, getApplicationContext());
                if(malArray.length() == 0) {
                    malArray = malfunctionDiagnosticMethod.updateOfflineUnPostedMalDiaEvent("0", dataDiagnostic,
                            clearDesc, dbHelper, getApplicationContext());
                }

                // call api
                SaveMalfnDiagnstcLogToServer(malArray, "0");

                if(dataDiagnostic.equals(Constants.PowerComplianceDiagnostic)){
                    powerEventInputArray = malArray;
                }

            }else {
                CheckEventsForClear(dataDiagnostic, true, minDiff);
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
                    SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                }

                int minDiffMal = constants.minDiffMalfunction(clearEventLastCallTime, global, getApplicationContext());
                // Checking after 1 min
                if (minDiffMal > 0) {
                    // update call time
                    SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                    JSONArray clearEventArray = malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, "0", EventCode, true, isUpdate, getApplicationContext());
                    ClearEventType = EventCode;

                    if(ClearEventType.equals(Constants.PowerComplianceDiagnostic)){
                        powerEventInputArray = clearEventArray;
                    }

                    // save data in unposted event array if api failed or internet issue. Clearing api response.
//                    malfunctionDiagnosticMethod.addOccurEventUploadStatus(clearEventArray, dbHelper);

                    /* We have 2 api for clear event. 1 for online events and 2nd is use here to clear in offline and input data as array.*/
                    if(clearEventArray.length() > 0){
                        // call clear event API.
                        if(global.isConnected(getApplicationContext() )) {
                            saveDriverLogPost.PostDriverLogData(clearEventArray, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT_BY_DATE,
                                    Constants.SocketTimeout20Sec, true, false, 0, ClearMalDiaEvent);
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

                    }

                }else{
                    if(minDiffMal < 0){
                        SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                    }

                }



            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private String getVin(){
        if(VinNumber.length() > 0) {
            if (constants.isObdVinValid(VinNumber)) {
                return VinNumber;
            } else {
                return SharedPref.getVINNumber(getApplicationContext());
            }
        }else{
            return SharedPref.getVINNumber(getApplicationContext());
        }
    }

    private void checkEngineSyncMalDiaOccurredEvent(){

        try {

            if(!SharedPref.isEngSyncMalfunction(getApplicationContext())) {
                boolean isEngineSyncMalAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncMal, getApplicationContext());
                boolean isEngineSyncDiaAllowed = SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncDiag, getApplicationContext());
                String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());

                if ((isEngineSyncMalAllowed || isEngineSyncDiaAllowed) && lastIgnitionStatus.equals("ON")) {

                    DateTime disconnectTime = global.getDateTimeObj(SharedPref.getObdLastStatusTime(getApplicationContext()), false);
                    DateTime currentTime = global.getDateTimeObj(global.GetDriverCurrentDateTime(global, getApplicationContext()), false);

                    if (disconnectTime != null && currentTime != null) {
                        int timeInSec = (int) Constants.getDateTimeDuration(disconnectTime, currentTime).getStandardSeconds();
                        //Seconds.secondsBetween(disconnectTime, currentTime).getSeconds();
                        if (timeInSec >= 70 ) {

                            boolean isEngSyncDiaOccurred = SharedPref.isEngSyncDiagnstc(getApplicationContext());
                            if (!isEngSyncDiaOccurred && isEngineSyncDiaAllowed) {

                                SharedPref.saveEngSyncDiagnstcStatus(true, getApplicationContext());
                                constants.saveDiagnstcStatus(getApplicationContext(), true);
                                SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                SharedPref.setEngSyncClearEventCallTime(Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                                // save occurred event in malfunction/diagnostic table
                                saveMalfunctionEventInTable(Constants.EngineSyncDiagnosticEvent,
                                        getString(R.string.eng_sync_dia_occured),
                                        global.GetCurrentUTCTimeFormat(), true );


                                // save malfunction entry in duration table
                                malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, "0",
                                        global.GetCurrentUTCTimeFormat(),
                                        global.GetCurrentUTCTimeFormat(),
                                        Constants.EngineSyncDiagnosticEvent,  "-1",
                                        Constants.getLocationType(getApplicationContext()),
                                        "", constants, getApplicationContext());

                                // update mal/dia status for enable disable according to log
                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());


                                global.ShowLocalNotification(getApplicationContext(),
                                        getString(R.string.dia_event),
                                        getString(R.string.eng_sync_dia_occured_desc), 2090);

                                Globally.PlaySound(getApplicationContext());



                            } else {

                                if (isEngineSyncMalAllowed) {

                                    String clearEventLastCallTime = SharedPref.getEngSyncMalEventCallTime(getApplicationContext());
                                    if (clearEventLastCallTime.length() == 0) {
                                        SharedPref.setEngSyncMalEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                    }

                                    // Checking after 1 min
                                    if (constants.minDiffMalfunction(clearEventLastCallTime, global, getApplicationContext()) > 0) {
                                        // update call time
                                        SharedPref.setEngSyncMalEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                                        double last24HrEngSyncEventTime = malfunctionDiagnosticMethod.getLast24HrEngSyncEvents(dbHelper);  //Constants.EngineSyncDiagnosticEvent,"", 0.0, constants, driverPermissionMethod, obdUtil,
//                                        int minDiff = constants.getMinDifference(SharedPref.getObdLastStatusTime(getApplicationContext()),
//                                                global.GetDriverCurrentDateTime(global, getApplicationContext()));
//                                        double totalEngSyncMissingMin = last24HrEngSyncEventTime;
                                        Logger.LogDebug("last24HrEng", String.valueOf(last24HrEngSyncEventTime));
                                        if (last24HrEngSyncEventTime >= Constants.PowerEngSyncMalOccTime) {

                                            SharedPref.saveEngSyncMalfunctionStatus(true, getApplicationContext());
                                            constants.saveMalfncnStatus(getApplicationContext(), true);
                                            SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                                            // save occurred event in malfunction/diagnostic table
                                            saveMalfunctionEventInTable(Constants.EngineSyncMalfunctionEvent,
                                                    getString(R.string.eng_sync_mal_occured),
                                                    global.GetCurrentUTCTimeFormat(), true);  //SharedPref.getObdLastStatusUtcTime(getApplicationContext())


                                            // save malfunction entry in duration table
                                            malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, "0",
                                                    global.GetCurrentUTCTimeFormat(),    //global.GetCurrentUTCTimeFormat(),
                                                    global.GetCurrentUTCTimeFormat(),
                                                    Constants.EngineSyncMalfunctionEvent,  "-1",
                                                    Constants.getLocationType(getApplicationContext()), "",
                                                    constants, getApplicationContext());

                                            // update mal/dia status for enable disable according to log
                                            malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());

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
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void checkEngSyncClearEvent(){
        try{

            if(SharedPref.isEngSyncDiagnstc(getApplicationContext())) {

                String EngineClearEventCallTime = SharedPref.getEngineClearEventCallTime(getApplicationContext());
                if(EngineClearEventCallTime.length() == 0){
                    EngineClearEventCallTime = global.GetDriverCurrentDateTime(global, getApplicationContext());
                    SharedPref.setEngineClearEventCallTime(EngineClearEventCallTime, getApplicationContext());
                }

                int callTimeMinDiff = constants.getMinDifference(EngineClearEventCallTime, global.getCurrentDate());

                // this check is used to avoid clear method calling after 3 sec each because checkEngSyncClearEvent is calling after 3 sec when data coming from obd
                if ( callTimeMinDiff >= 0) {
                    SharedPref.setEngineClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());

                    boolean HasEngSyncEventForClear = malfunctionDiagnosticMethod.HasEngSyncEventForClear(Constants.EngineSyncDiagnosticEvent,
                            dbHelper, constants, global, getApplicationContext());
                    if (HasEngSyncEventForClear) {  //SharedPref.isEngSyncDiagnstc(getApplicationContext()) ||
                        ClearEventUpdate(Constants.EngineSyncDiagnosticEvent,
                                "Auto clear engine sync diagnostic event");


                        if (!SharedPref.isLocDiagnosticOccur(getApplicationContext()) && !SharedPref.isEngSyncDiagnstc(getApplicationContext())) {
                            SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getApplicationContext()),
                                    SharedPref.isMalfunctionOccur(getApplicationContext()),
                                    false,
                                    SharedPref.isSuggestedEditOccur(getApplicationContext()), getApplicationContext());
                        }


                        global.ShowLocalNotification(getApplicationContext(),
                                getString(R.string.event_cleared),
                                getString(R.string.eng_sync_dia_clear_desc), 2090);


                        malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());

                        SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
                        SharedPref.saveEngSyncMalfunctionStatus(SharedPref.isEngSyncMalfunction(getApplicationContext()), getApplicationContext());


                    }

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
                                        currentHighPrecisionOdometer, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
                            }


                            // check position malfunction event
                            String locMalDiaEvent = constants.isPositionMalfunctionEvent( "0", malfunctionDiagnosticMethod,
                                    dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());
                            if (locMalDiaEvent.length() > 0) {
                                SharedPref.setClearEventCallTime(global.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext());
                                if (locMalDiaEvent.equals("M")) {   // && SharedPref.isLocMalfunctionOccur(getApplicationContext()) == false

                                    // save occurred event in malfunction/diagnostic table
                                    saveMalfunctionEventInTable(Constants.PositionComplianceMalfunction, getString(R.string.pos_mal_occured),
                                            global.GetCurrentUTCTimeFormat(), true);  //SharedPref.getEcmObdUtcTime(getApplicationContext())

                                    // save malfunction entry in duration table
                                    malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, "0",
                                            global.GetCurrentUTCTimeFormat(), global.GetCurrentUTCTimeFormat(),
                                            Constants.PositionComplianceMalfunction,  "-1",
                                            "E", "", constants, getApplicationContext());

                                    // update mal/dia status for enable disable according to log
                                    malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());

                                    SharedPref.saveLocMalfunctionOccurStatus(true, global.GetDriverCurrentDateTime(global, getApplicationContext()),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
                                    constants.saveMalfncnStatus(getApplicationContext(), true);

                                    global.ShowLocalNotification(getApplicationContext(),
                                            getString(R.string.malfunction_events),
                                            getString(R.string.pos_mal_event_desc), 2091);
                                    Globally.PlaySound(getApplicationContext());



                                } else {

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

}
