package com.background.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;
import android.util.Log;

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
import com.constants.SaveDriverLogPost;
import com.constants.SaveUnidentifiedRecord;
import com.constants.SharedPref;
import com.constants.ShellUtils;
import com.constants.TcpClient;
import com.constants.Utils;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.htstart.htsdk.HTBleSdk;
import com.htstart.htsdk.bluetooth.HTBleData;
import com.htstart.htsdk.bluetooth.HTBleDevice;
import com.htstart.htsdk.minterface.HTBleScanListener;
import com.htstart.htsdk.minterface.IReceiveListener;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.MalfunctionDiagnosticMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.notifications.NotificationManagerSmart;
import com.wifi.settings.WiFiConfig;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
    String ClearEventType = "";

    String TAG = "Service";
    String TAG_OBD = "OBD Service";
    private static final long TIME_INTERVAL_WIFI  = 10 * 1000;   // 10 sec
    private static final long TIME_INTERVAL_WIRED = 3 * 1000;   // 3 sec

    boolean isStopService       = false;
    double lastVehSpeed         = -1;

    int ThreshHoldSpeed         = 8;
    int intermediateRecordTime  = 60;    // intermediate record time is 60 min
    int OnDutyRecordTime        = 5;    // OnDuty record time is 6 min when earlier event was DR and now vehicle has been stopped for 6 min

    boolean isTempTimeValidate = true;
    String TempTimeAtStart = "", CompanyId = "", TruckID = "", TempStatus = "";
    String LastDutyStatus = "", StatusStartTime = "";   //, CurrentDutyStatus = "";
    String VinNumber = "", UnAssignedVehicleMilesId = "", IntermediateLogId = "", EngineSeconds = "";
    String PreviousLatitude = "", PreviousLongitude = "", PreviousOdometer = "";

    boolean Intermediate = false;
    boolean IntermediateUpdate = false;
    boolean isMalfncDataAlreadyPosting = false;
    CheckConnectivity checkConnectivity;
    HelperMethods hMethods;
    DBHelper dbHelper;
    Globally global;
    JSONArray unPostedLogArray = new JSONArray();

    boolean UnidentifiedApiInProgress = false;
    final int GetMalDiaEventDuration        = 20;
    final int SaveUnidentifiedEvent         = 30;
    Map<String, String> params;
    VolleyRequest truckListApi, companyListApi, GetMalfunctionEvents;
    SaveUnidentifiedRecord SaveUpdateUnidentifiedApi;
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

    private static final String TAG_BLE = "BleService";
    boolean mIsScanning = false, ScanStart = false;
    boolean isBleObdRespond = false;

    int bleScanCount = 0;
    int ObdPreference;
    int timerCount = 0;
    int bleConnectionCount = 0;
    int VehicleSpeed = 0;

    final int SaveMalDiagnstcEvent  = 302;
    final int ClearMalDiaEvent       = 21;

    private BluetoothAdapter mBTAdapter;
    private ArrayList<HTBleDevice> mHTBleDevices = new ArrayList<>();
    private LinkedList<HTBleData> mHtblData = new LinkedList<>();
    Intent locServiceIntent;

    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
    DriverPermissionMethod driverPermissionMethod;

    double tempOdo = 1179884000;  //1.090133595E9
    double tempEngHour = 22999.95;

    Utils obdUtil;



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

        constants               = new Constants();
        mTimer                  = new Timer();

        saveDriverLogPost       = new SaveDriverLogPost(getApplicationContext(), saveLogRequestResponse);


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
        BindConnection();

        ObdPreference = SharedPref.getObdPreference(getApplicationContext());
        SharedPref.setNotiShowTime("", getApplicationContext());
        SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());

        initUnidentifiedObj();

        if(ObdPreference == Constants.OBD_PREF_BLE) {
            initBleListener();
            checkPermissionsBeforeScanBle();
        }else if(ObdPreference == Constants.OBD_PREF_WIRED) {
            checkWiredObdConnection();
        }


        startLocationService();

        SharedPref.setContinueSpeedZeroTime(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
        SharedPref.saveBleScanCount(0, getApplicationContext());
        mTimer.schedule(timerTask, TIME_INTERVAL_WIFI, TIME_INTERVAL_WIFI);

    }



    //  ------------- Wired OBD data response handler ----------
    private class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();

            try {
                ignitionStatus = bundle.getString(constants.OBD_IgnitionStatus);
                truckRPM = bundle.getString(constants.OBD_RPM);
                VehicleSpeed = bundle.getInt(constants.OBD_Vss);
                EngineSeconds = bundle.getString(constants.OBD_EngineHours);
                VinNumber = bundle.getString(constants.OBD_VINNumber);
                obdOdometer = bundle.getString(constants.OBD_Odometer);

                if(constants.isObdVinValid(VinNumber)){
                    SharedPref.setVehicleVin(VinNumber, getApplicationContext());
                }else{
                    VinNumber = SharedPref.getVINNumber(getApplicationContext());
                }



                if(bundle.getString(constants.OBD_HighPrecisionOdometer) != null) {
                    currentHighPrecisionOdometer = bundle.getString(constants.OBD_HighPrecisionOdometer);
                }

            }catch (Exception e){
                e.printStackTrace();
            }


            //========================================================
            isWiredCallBackCalled = true;


            String obdLastCallDate = SharedPref.getWiredObdCallTime(getApplicationContext());
            if(obdLastCallDate.length() > 10) {
                final DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);    // Current Date Time
                final DateTime savedDateTime = global.getDateTimeObj(obdLastCallDate, false);

                int timeInSec = (int) Constants.getDateTimeDuration(savedDateTime, currentDateTime).getStandardSeconds();
                if (timeInSec >= 3) {  // minimum call interval is 3 sec.
                    SharedPref.SetWiredObdCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
                    checkObdDataWithRule(VehicleSpeed);
                }else if(timeInSec < 0){
                    SharedPref.SetWiredObdCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
                }
            }else{
                SharedPref.SetWiredObdCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
            }

        }
    }




    private void checkObdDataWithRule(int speed){

        // ---------------- temp data ---------------------

        // temp odometer for simulator converting odometer from km to meter. because it is saving in km.

       /* if(ObdPreference == Constants.OBD_PREF_WIRED){
            currentHighPrecisionOdometer = Constants.kmToMeter(obdOdometer);
        }*/

/*
        int OBD_LAST_STATUSss = SharedPref.getObdStatus(getApplicationContext());
        if (LoginActivity.isDriving) {
            ignitionStatus = "ON";
            truckRPM = "700";
            speed = 10;
            // obdEngineHours = "23789.5";
            if (OBD_LAST_STATUSss != Constants.WIRED_CONNECTED) {
                // obdEngineHours = "23789.7";
                SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, global.getCurrentDate(),
                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
            }
            sendBroadcast(true);
        } else {
            ignitionStatus = "ON";
            truckRPM = "0";
            speed = 0;
            // obdEngineHours = "23789.5";
            if (OBD_LAST_STATUSss != Constants.WIRED_DISCONNECTED) {
                SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, global.getCurrentDate(),
                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
            }
            sendBroadcast(false);

            String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());

            // save log when ignition status is changed.    //SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getApplicationContext())
            if (lastIgnitionStatus.equals("ON") ) {
                // SharedPref.SetTruckIgnitionStatusForContinue(ignitionStatus, last_obs_source_name, "", getApplicationContext());
                SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, "WiredOBD",
                        global.getCurrentDate(), global.GetCurrentUTCTimeFormat(),
                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()), getApplicationContext());

            }

        }


        tempOdo = tempOdo + 500;
        currentHighPrecisionOdometer = "" + BigDecimal.valueOf(tempOdo).toPlainString();
        tempEngHour = tempEngHour + .01;
        EngineSeconds = ""+tempEngHour;
        VehicleSpeed = speed;

        SharedPref.setVss(speed, getApplicationContext());
        SharedPref.setRPM(truckRPM, getApplicationContext());

*/




        //========================================================




        try {

            if (SharedPref.getUserName(getApplicationContext()).equals("") &&
                    SharedPref.getPassword(getApplicationContext()).equals("")) {

                checkEngHrOdo();

                if(SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED ||
                        SharedPref.getObdStatus(getApplicationContext()) == Constants.BLE_CONNECTED) {

                    if (ignitionStatus.equals("ON") && !truckRPM.equals("0")) {

                        /* ======================== Malfunction & Diagnostic Events ========================= */
                        if(EngineSeconds.length() > 0 && !EngineSeconds.equals("0") && !EngineSeconds.equals("0.00")) {
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
                                if (isTempTimeValidate) {
                                    if (constants.minDiffFromUTC(savedDate, global, getApplicationContext()) > 0) {
                                        SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
                                        ChangeTempUnidentifiedStatus();

                                        isTempTimeValidate = false;
                                    }
                                } else {
                                    SharedPref.setContinueSpeedZeroTime(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());

                                    if (constants.minDiffFromUTC(savedDate, global, getApplicationContext()) > 0) {
                                        SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
                                        UnidentifiedStatusChange(speed, 0);
                                    }

                                }
                            }

                            // check Unidentified event occurrence
                            checkUnIdentifiedDiagnosticEvent(speed, true);
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
                                        checkUnIdentifiedDiagnosticEvent(speed, false);

                                    }

                                }
                            }

                        }


                        lastVehSpeed = speed;

                        // ping wired server to get data
                        if( ObdPreference == Constants.OBD_PREF_WIRED &&
                                SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED) {
                            CallWired(TIME_INTERVAL_WIRED);
                        }




                    } else {
                        lastVehSpeed = -1;
                        SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                        saveIgnitionStatus(ignitionStatus);


                        if( ObdPreference == Constants.OBD_PREF_WIRED ) {
                            if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_CONNECTED ||
                                    SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED) {
                                CallWired(Constants.SocketTimeout5Sec);
                            }
                        }

                        // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
                        if(!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {
                            // check Unidentified event occurrence
                            checkUnIdentifiedDiagnosticEvent(-1, false);

                            endDutyOnIgnitionOff();

                        }
                    }
                }else{
                    lastVehSpeed = -1;
                    SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                    if( ObdPreference != Constants.OBD_PREF_WIFI ) {
                        CallWired(Constants.SocketTimeout5Sec);

                     /*   constants.saveObdData(constants.getObdSource(getApplicationContext()), "WIRED-DISCONNECTED-1", "", "-1",
                                currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "-1",
                                "-1", EngineSeconds, "", "",
                                "0", dbHelper, driverPermissionMethod, obdUtil);
*/
                        // Check Engine Sync data Malfunction/Diagnostic event
                         checkEngineSyncMalDiaOccurredEvent();

                    }

                    // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
                    if(!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {
                        // check Unidentified event occurrence
                        checkUnIdentifiedDiagnosticEvent(-1, false);

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

        ShellUtils.CommandResult obdShell = ShellUtils.execCommand("cat /sys/class/power_supply/usb/type", false);


        if (obdShell.result == 0) {
            if (obdShell.successMsg.contains("USB_DCP")) {  //USB_DCP

                if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED) {

                    // Connected State
                    SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, global.getCurrentDate(),
                            global.GetCurrentUTCTimeFormat(), getApplicationContext());

                    StartStopServer(constants.WiredOBD);

                   /* constants.saveObdData(constants.getObdSource(getApplicationContext()), "WIRED-CONNECTED", "", "-1",
                            currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "-1",
                            "-1", EngineSeconds, "", "",
                            "0", dbHelper, driverPermissionMethod, obdUtil);
*/
                    global.ShowLocalNotification(getApplicationContext(),
                            getString(R.string.wired_tablettt),
                            getString(R.string.wired_tablet_connected), 2081);

                }

                sendBroadcast(true);


            } else {

                // Disconnected State. Save only when last status was not already disconnected
                  if (SharedPref.getObdStatus(getApplicationContext()) != constants.WIRED_DISCONNECTED) {

                      SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, global.getCurrentDate(),
                              global.GetCurrentUTCTimeFormat(), getApplicationContext());

                    /*  constants.saveObdData(constants.getObdSource(getApplicationContext()), "WIRED-DISCONNECTED", "", "-1",
                              currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "-1",
                              "-1", EngineSeconds, "", "",
                              "0", dbHelper, driverPermissionMethod, obdUtil);
*/
                      global.ShowLocalNotification(getApplicationContext(),
                              getString(R.string.wired_tablettt),
                              getString(R.string.wired_tablet_disconnected), 2081);

                  }

                sendBroadcast(false);

                isWiredCallBackCalled = false;

                // check Unidentified event occurrence
                checkUnIdentifiedDiagnosticEvent(-1, false);

            }
        } else {
            // Error

           /* if (SharedPref.getObdStatus(getApplicationContext()) != constants.WIRED_ERROR) {
                constants.saveObdData(constants.getObdSource(getApplicationContext()), "WIRED-ERROR", "", "-1",
                        "", "", "", "", "-1",
                        "-1", "", "", "",
                        "0", dbHelper, driverPermissionMethod, obdUtil);
            }*/

            isWiredCallBackCalled = false;
            SharedPref.SaveObdStatus(Constants.WIRED_ERROR, global.getCurrentDate(),
                    global.GetCurrentUTCTimeFormat(), getApplicationContext());

            sendBroadcast(false);
        }



    }



    private void initUnidentifiedObj(){
        try{

            hMethods            = new HelperMethods();
            dbHelper            = new DBHelper(getApplicationContext());
            global              = new Globally();
            TruckID             = DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext());
            CompanyId           = DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext());

            IntermediateLogId           = SharedPref.getIntermediateLogId(getApplicationContext());
            UnAssignedVehicleMilesId    = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());



            truckListApi        = new VolleyRequest(getApplicationContext());
            companyListApi      = new VolleyRequest(getApplicationContext());
            GetMalfunctionEvents= new VolleyRequest(getApplicationContext());

            checkConnectivity       = new CheckConnectivity(getApplicationContext());
            SaveUpdateUnidentifiedApi = new SaveUnidentifiedRecord(getApplicationContext(), ResponseCallBack);
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
                        }
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


    // check Unidentified event occurrence
    private void checkUnIdentifiedDiagnosticEvent(int speed, boolean isVehicleInMotion){
        try{
            boolean isUnidentifiedDiaEvent = SharedPref.isUnidentifiedDiaEvent(getApplicationContext());
            if (!isUnidentifiedDiaEvent) {
                if(CompanyId.length() > 0) {
                    boolean isVehicleMotionChanged = malfunctionDiagnosticMethod.isVehicleMotionChanged(isVehicleInMotion,
                            Integer.parseInt(CompanyId), dbHelper);

                    if (isVehicleMotionChanged) {
                        malfunctionDiagnosticMethod.saveVehicleMotionChangeTime(speed, Integer.parseInt(CompanyId), dbHelper);
                    }

                    /*constants.saveObdData(constants.getObdSource(getApplicationContext()), "UnIdentified Diagnostic event: " +
                                    malfunctionDiagnosticMethod.getUnidentifiedLogoutArray(Integer.parseInt(CompanyId), dbHelper).toString(),
                            "", "-1",  currentHighPrecisionOdometer, "", ignitionStatus, truckRPM,
                            "-1","-1", EngineSeconds, "", "",
                            "0", dbHelper, driverPermissionMethod, obdUtil);
                    */
                }

            } else {

                DateTime StartDateTime = Globally.getDateTimeObj(SharedPref.getUnidentifiedDiaOccTime(getApplicationContext()), false);
                DateTime EndDateTime = Globally.GetCurrentUTCDateTime();
                long eventDuration = Constants.getDateTimeDuration(StartDateTime, EndDateTime).getStandardMinutes();

                long dayInMin = 1440;
                if (eventDuration > dayInMin) {   // after 24 hour event will be occurred again
                    SharedPref.saveUnidentifiedEventStatus(false, "", getApplicationContext());
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
                SharedPref.setNotiShowTime(Globally.GetCurrentDateTime(), getApplicationContext());

                Globally.PlaySound(getApplicationContext());
                Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                        AlertMsg + "(" + speed + " km/h) " + AlertMsg1, 2003);
                SpeakOutMsg(AlertMsgSpeech);
            }else{
                if (notificationShowTime.length() > 10) {
                    DateTime currentTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
                    DateTime lastCallDateTime = Globally.getDateTimeObj(notificationShowTime, false);
                    long secDiff = Constants.getDateTimeDuration(lastCallDateTime, currentTime).getStandardSeconds();
                    if (secDiff >= 120) {    // Showing notification After 2 min interval
                        SharedPref.setNotiShowTime(Globally.GetCurrentDateTime(), getApplicationContext());

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
        if (SharedPref.getUserName(getApplicationContext()).equals("") && SharedPref.getPassword(getApplicationContext()).equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    StartStopServer(constants.WiredOBD);
                }
            }, time);
        }
    }



    private void checkWifiOBDConnection(){

        if (SharedPref.getUserName(getApplicationContext()).equals("") &&  SharedPref.getPassword(getApplicationContext()).equals("")) {

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

        Log.i("service", "---------onStartCommand Service");

        ObdPreference = SharedPref.getObdPreference(getApplicationContext());
        String pingStatus = SharedPref.isPing(getApplicationContext());
        if(pingStatus.equals(ConstantsKeys.ClearUnIdentifiedData)){

            // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
            if(!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {

                // check Unidentified event occurrence
                checkUnIdentifiedDiagnosticEvent(-1, false);

                EndOnDutyOrDrivingRecords("");

                  postEventsToServer();
            }

        }else {
            if (ObdPreference == Constants.OBD_PREF_BLE) {
                int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
                boolean isConnected = HTBleSdk.Companion.getInstance().isConnected();
                if(pingStatus.equals("ble_start")){
                    if (!isConnected || ObdStatus != Constants.BLE_CONNECTED ) { //!mIsScanning &&

                        if(mIsScanning){
                            HTBleSdk.Companion.getInstance().stopHTBleScan();
                            mIsScanning = false;
                        }

                  /*      initHtBle();*/

                        if(bleConnectionCount > 2) {
                            checkPermissionsBeforeScanBle();
                        }else{
                            HTBleSdk.Companion.getInstance().reBleConnect();
                        }
                        bleConnectionCount++;

                    }else{
                        if (isConnected && ObdStatus != Constants.BLE_CONNECTED ) {

                            HTBleSdk.Companion.getInstance().disAllConnect();
                            mIsScanning = false;

                            HTBleSdk.Companion.getInstance().reBleConnect();
                        }
                    }



                }

            } else if (ObdPreference == Constants.OBD_PREF_WIRED) {
                StartStopServer(constants.WiredOBD);
            } else {
                checkWifiOBDConnection();
            }

            if(Globally.LATITUDE.length() == 0){
                startLocationService();
            }

        }

        SharedPref.SetPingStatus("", getApplicationContext());


        if(TruckID.length() > 0 && constants.isMalDiaAllowed(getApplicationContext()) && Globally.isConnected(getApplicationContext())) {
            GetMalDiaEventsDurationList();
        }

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }



    void sendBroadcast(boolean isConnected){
        try{
            Intent intent = new Intent(ConstantsKeys.IsEventUpdate);
            intent.putExtra(ConstantsKeys.IsEventUpdate, true);
            intent.putExtra(ConstantsKeys.Status, isConnected);
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
            Log.e(TAG, "-----Running Logout timerTask");

            if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                    !SharedPref.getPassword(getApplicationContext()).equals("")) {
                Log.e("Log", "--stop");
                StopService();

            }else{

                // communicate with wired OBD server app with Message
                if(ObdPreference == Constants.OBD_PREF_BLE) {
                    //checkPermissionsBeforeScanBle();
                    if(!HTBleSdk.Companion.getInstance().isConnected()){
                        sendBroadcast(false);
                        EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, HTBleSdk.Companion.getInstance().getAddress()));
                        HTBleSdk.Companion.getInstance().reBleConnect();

                        // call handler callback method to check malfunction/diagnostic when disconnected
                        StartStopServer(constants.WiredOBD);


                    }
                }else if(ObdPreference == Constants.OBD_PREF_WIRED) {

                    checkWiredObdConnection();

                    if(isWiredCallBackCalled == false && SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED){
                        StartStopServer(constants.WiredOBD);
                    }else{

                        String obdLastCallDate = SharedPref.getWiredObdServerCallTime(getApplicationContext());
                        if (obdLastCallDate.length() > 10) {
                            int lastCalledDiffInSec = constants.getSecDifference(obdLastCallDate, Globally.GetCurrentDateTime());
                            if (lastCalledDiffInSec >= 20) {
                                StartStopServer(constants.WiredOBD);
                            }
                        }else{
                            StartStopServer(constants.WiredOBD);
                        }

                    }

                }else{
                    checkWifiOBDConnection();
                }

                // if unidentified events from OBD settings was false in web permissions then it will worked on app side other wise on backend side.
                if(!SharedPref.IsUnidentifiedFromOBD(getApplicationContext())) {
                    if (CompanyId.length() > 0 && SharedPref.isUnidentifiedDiaEvent(getApplicationContext()) == false) {
                        timerCount++;
                        if (timerCount > 3 && VehicleSpeed >= 8) {
                            timerCount = 0;
                            // String odometer = Constants.Convert2DecimalPlacesDouble(Double.parseDouble(Constants.meterToKmWithObd(currentHighPrecisionOdometer)));
                            String odometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));

                            // save unidentified events if vehicle is in motion for 30 min in last 24 hour
                            malfunctionDiagnosticMethod.saveVehicleMotionStatus(odometer, EngineSeconds, Integer.parseInt(CompanyId),
                                    TruckID, VinNumber, VehicleSpeed, dbHelper, constants,
                                    driverPermissionMethod, obdUtil, getApplicationContext());
                        }

                    }
                    postEventsToServer();
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
                checkPositionMalfunction(currentHighPrecisionOdometer, global.GetCurrentDateTime());


            }


        }
    };

    // post occurred unidentified events to server
    private void postEventsToServer(){
        JSONArray array = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);

        if (global.isConnected(getApplicationContext()) && array.length() > 0 && isDataAlreadyPosting == false) {
            isDataAlreadyPosting = true;
            saveDriverLogPost.PostDriverLogData(array, APIs.MALFUNCTION_DIAGNOSTIC_EVENT, Constants.SocketTimeout30Sec,
                    false, false, 1, 101);

          //  constants.saveTempUnidentifiedLog(array.toString(), obdUtil);
        }
    }


    /*================== Get Unidentified Records ===================*/
    void GetMalDiaEventsDurationList(){

        String startEventDate = Globally.GetCurrentJodaDateTime().toString();   //.minusDays(15)

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.UnitNo, TruckID);
        params.put(ConstantsKeys.EventDateTime, startEventDate);

        GetMalfunctionEvents.executeRequest(Request.Method.POST, APIs.GET_MALFUNCTION_LIST_BY_TRUCK , params, GetMalDiaEventDuration,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }



    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }




    //  ------------- Wired OBD ----------
    //Bind to the remote service
    private void BindConnection(){
        try{
            Intent intent = new Intent();
            intent.setClassName(ServerPackage, ServerService);
            this.bindService(intent, this.connection, Context.BIND_AUTO_CREATE);

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
        Log.d(TAG,"---onBind");
        return null;
    }




    // Speak Out Msg
    void SpeakOutMsg(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void StartStopServer(final String value){

        SharedPref.SetWiredObdServerCallTime(Globally.GetCurrentDateTime(), getApplicationContext());

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
            Log.d("response", "OBD Response: " +message);

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

                                if(Globally.LATITUDE.length() < 5){
                                    SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());
                                }else{
                                    SharedPref.SetLocReceivedFromObdStatus(true, getApplicationContext());
                                }

                                if (ignitionStatus.equals("true")) {
                                    SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, global.getCurrentDate(),
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
                                        SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED,  global.getCurrentDate(),
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






    private void checkPermissionsBeforeScanBle() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //boolean isConnected = HTBleSdk.Companion.getInstance().isConnected();

        try {
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            } else {
                if (constants.CheckGpsStatusToCheckMalfunction(getApplicationContext())) {
                        StartScanHtBle();
                }
            }
        }catch (Exception e){}
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initHtBle(){

        if (!BleUtil.isBLESupported(this)) {
            Log.d(TAG_BLE, getResources().getString(R.string.ble_not_supported));
            return;
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();

        }

        if (mBTAdapter == null) {
            Log.d(TAG_BLE, getResources().getString(R.string.bt_unavailable));
            return;
        }

        if (!mBTAdapter.isEnabled()) {
            mBTAdapter.enable();

            StartScanHtBle();

        }

    }

    void initBleListener(){
        HTBleSdk.Companion.getInstance().registerCallBack(new IReceiveListener() {
            @Override
            public void onConnected(@org.jetbrains.annotations.Nullable String s) {
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_CONNECTED, s));
                Log.d("BleObd","onConnected");
                sendBroadcast(true);
                bleConnectionCount = 0;
                    SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, global.getCurrentDate(),
                            global.GetCurrentUTCTimeFormat(), getApplicationContext());

            }

            @Override
            public void onConnectTimeout(@org.jetbrains.annotations.Nullable String s) {
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_TIMEOUT, s));
                Log.d("BleObd","onConnectTimeout");
                sendBroadcast(false);
            }

            @Override
            public void onConnectionError(@NotNull String s, int i, int i1) {
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_ERROR, s));
                Log.d("BleObd","onConnectionError");
                sendBroadcast(false);
            }

            @Override
            public void onDisconnected(@org.jetbrains.annotations.Nullable String s) {
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, s));
                Log.d("BleObd","onDisconnected");
                sendBroadcast(false);

                SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, global.getCurrentDate(),
                        global.GetCurrentUTCTimeFormat(), getApplicationContext());


                    global.ShowLocalNotification(getApplicationContext(),
                            getString(R.string.BleOBDConnErr),
                            getString(R.string.connErrorDesc) , 2081);


                // check Unidentified event occurrence
                checkUnIdentifiedDiagnosticEvent(-1, false);

                StartStopServer(constants.WiredOBD);

            }

            @Override
            public void onReceive(@NotNull String address, @NotNull String uuid, @NotNull HTBleData htBleData) {
                mHtblData.add(htBleData);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));

                String savedMacAddress = SharedPref.GetBleOBDMacAddress(getApplicationContext());
                if(savedMacAddress.length() == 0 || savedMacAddress.equals(address)) {

                    SharedPref.SaveBleOBDMacAddress(address, getApplicationContext());

                    if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_CONNECTED) {
                        bleConnectionCount  = 0;
                        SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, global.getCurrentDate(),
                                global.GetCurrentUTCTimeFormat(), getApplicationContext());

                        global.ShowLocalNotification(getApplicationContext(),
                                getString(R.string.BluetoothOBD),
                                getString(R.string.obd_ble), 2081);

                    }

                    sendBroadcast(true);
                    VehicleSpeed = Integer.valueOf(htBleData.getVehicleSpeed());
                    truckRPM = htBleData.getEngineSpeed();
                    VinNumber = htBleData.getVIN_Number();
                    EngineSeconds = htBleData.getEngineHours();
                    obdOdometer = htBleData.getOdoMeter();
                    currentHighPrecisionOdometer = htBleData.getOdoMeter();
                    String lat = htBleData.getLatitude();
                    String lon = htBleData.getLongitude();

                    if(lat.equalsIgnoreCase("X")){
                        lat = Constants.getLocationType(getApplicationContext());
                        lon = lat;
                    }

                    Globally.LATITUDE = lat;
                    Globally.LONGITUDE = lon ;

                    // this check is using to confirm loc update, because in loc disconnection ble OBD is sending last saved location.
                    if(Globally.LATITUDE.equals(PreviousLatitude) && Globally.LONGITUDE.equals(PreviousLongitude) &&
                            !currentHighPrecisionOdometer.equals(PreviousOdometer)){
                        Globally.LATITUDE = Globally.GPS_LATITUDE;
                        Globally.LONGITUDE = Globally.GPS_LONGITUDE;

                    }

                    PreviousLatitude = lat;
                    PreviousLongitude = lon;
                    PreviousOdometer = currentHighPrecisionOdometer;

                    if(Globally.LATITUDE.length() < 5){
                        SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());
                    }else{
                        SharedPref.SetLocReceivedFromObdStatus(true, getApplicationContext());
                    }

                    if(constants.isObdVinValid(VinNumber)){
                        SharedPref.setVehicleVin(VinNumber, getApplicationContext());
                    }else{
                        VinNumber = SharedPref.getVINNumber(getApplicationContext());
                    }

                    constants.saveEcmLocationWithTime(Globally.LATITUDE, Globally.LONGITUDE, currentHighPrecisionOdometer, getApplicationContext());

                    Log.d("BleObd","onReceive Data: "+ truckRPM + ", VehicleSpeed: " + VehicleSpeed);

                    String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());
                    Log.d("lastIgnitionStatus", "lastIgnitionStatus00: " +lastIgnitionStatus );
                    // this check is used when ble obd is disconnected
                    if(SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_CONNECTED) {
                        if (!SharedPref.getRPM(getApplicationContext()).equals("0") && lastIgnitionStatus.equals("true")) {
                            truckRPM = SharedPref.getRPM(getApplicationContext());
                            ignitionStatus = "ON";
                        }

                        checkObdDataWithRule(VehicleSpeed);

                    }else {

                        if (truckRPM.length() > 0) {
                            if (Integer.valueOf(truckRPM) > 0) {
                                ignitionStatus = "ON";
                            } else {
                                ignitionStatus = "OFF";
                            }

                            checkObdDataWithRule(VehicleSpeed);

                        }
                    }

                }else{
                    Globally.ShowLogoutSpeedNotification(getApplicationContext(),
                            getString(R.string.BleOBDConnErr),
                            getString(R.string.connErrorDesc) , 2005);
                    HTBleSdk.Companion.getInstance().disAllConnect();
                }



            }

            @Override
            public void onResponse(@NotNull String address, @NotNull String uuid, @NotNull String sequenceID, @NotNull int status) {
                Log.e("status", "==" + status + "==" + address);
                bleScanCount = 0;
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_RESPONSE, address, uuid, status));
            }
        });

    }


    public void StartScanHtBle(){

        HTBleSdk.Companion.getInstance().startHTBleScan(new HTBleScanListener() {
            @Override
            public void onScanStart() {
                mHTBleDevices.clear();

                ScanStart = true;
                bleScanCount++;
                mIsScanning = true;
                isBleObdRespond = false;
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
            }

            @Override
            public void onScanStop() {
                mIsScanning = false;
                if (HTBleSdk.Companion.getInstance().isConnected());
            }
        });

    }


    private void connectHtBle(final HTBleDevice htBleDevice) {
        HTBleSdk.Companion.getInstance().stopHTBleScan();
        if (HTBleSdk.Companion.getInstance().isAllConnected()) {
            // ToastUtil.show(getApplicationContext(), getString(R.string.ht_connect_error_other));
        } else {

            HTBleSdk.Companion.getInstance().connect(htBleDevice);

            String macAddress = htBleDevice.getAddress();
            String savedMacAddress = SharedPref.GetBleOBDMacAddress(getApplicationContext());
            if(savedMacAddress.length() == 0 || savedMacAddress.equals(macAddress)) {
                HTBleSdk.Companion.getInstance().connect(htBleDevice);
            }else{
                Globally.ShowLogoutSpeedNotification(getApplicationContext(),
                        getString(R.string.BleOBDConnErr),
                        getString(R.string.connErrorDesc) , 2005);
            }

        }
    }






    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onDestroy() {

        if(!isStopService) {

            Intent intent = new Intent(Constants.packageName);
            intent.putExtra("location", "torestore");
            sendBroadcast(intent);
        }else{
            Log.d(TAG, "Service stopped");

            if(ObdPreference == Constants.OBD_PREF_BLE) {
                //  ------------- BLE OBD ----------
               /* if(HTBleSdk.Companion.getInstance().isConnected()) {
                 //   EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, HTBleSdk.Companion.getInstance().getAddress()));
                    HTBleSdk.Companion.getInstance().disAllConnect();
                    HTBleSdk.Companion.getInstance().unRegisterCallBack();
                }*/
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




    private void ChangeTempUnidentifiedStatus(){

        try {

            JSONArray savedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
            JSONArray unPostedArray = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);
            LastDutyStatus = "DR";
            StatusStartTime = Globally.GetCurrentUTCTimeFormat();
            TempStatus = "";

            String odometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));

            JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj("", CompanyId, VinNumber, TruckID,
                    LastDutyStatus, StatusStartTime, "", Globally.LATITUDE, Globally.LONGITUDE, "", "",
                    EngineSeconds, "", odometer, "", false,
                    false, "0", false, Constants.getLocationType(getApplicationContext()) );

            SharedPref.SaveUnidentifiedIntermediateRecord(odometer, Globally.GetCurrentUTCTimeFormat(), Globally.LATITUDE, Globally.LONGITUDE,
                    EngineSeconds, getApplicationContext());

            SharedPref.setUnIdenLastDutyStatus(LastDutyStatus, getApplicationContext());
            savedUnidentifiedLogArray.put(jsonObject);
            unPostedArray.put(jsonObject);

            hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, savedUnidentifiedLogArray);

            if (Globally.isConnected(getApplicationContext())) {
                SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
            }

            Globally.PlaySound(getApplicationContext());

            Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                    getString(R.string.UnIdentifiedDrivingOcc) , 2005);


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

            if (isDriving || isOnDuty) {

                UnAssignedVehicleMilesId = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());
                IntermediateLogId = SharedPref.getIntermediateLogId(getApplicationContext());


                JSONArray savedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
                JSONArray unPostedArray = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);

             //   String odometer = Constants.Convert2DecimalPlacesDouble(Double.parseDouble(Constants.meterToKmWithObd(currentHighPrecisionOdometer)));
                String currentOdometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));
                String currentDate = Globally.GetCurrentUTCTimeFormat();

                if (savedUnidentifiedLogArray.length() > 0) {
                    JSONObject obj = (JSONObject) savedUnidentifiedLogArray.get(savedUnidentifiedLogArray.length() - 1);
                    String UTCEndDateTime = obj.getString(ConstantsKeys.UTCEndDateTime);
                    boolean Intermediate = obj.getBoolean(ConstantsKeys.Intermediate);

                    if (UTCEndDateTime.length() == 0 || Intermediate) {

                        // get last update object from unPosted Unidentified Log array
                        JSONObject unPostedLogLastObj = hMethods.updateLastRecordOfUnIdentifiedLog(unPostedArray, UnAssignedVehicleMilesId,
                                currentDate, currentOdometer, Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds, false);

                        if (unPostedLogLastObj != null && unPostedArray.length() > 0) {
                            unPostedArray.put(unPostedArray.length() - 1, unPostedLogLastObj);
                        }

                        // get last update object from posted/unposted Unidentified Log array
                        JSONObject savedUnidentifiedLogLastObj = hMethods.updateLastRecordOfUnIdentifiedLog(savedUnidentifiedLogArray,
                                UnAssignedVehicleMilesId, currentDate,
                                currentOdometer, Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds, false);

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
                            Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds, getApplicationContext());

                } else {
                    LastDutyStatus = "OD";
                    // clear last saved records
                    SharedPref.SaveUnidentifiedIntermediateRecord("", "", "", "", "",
                            getApplicationContext());

                }

                // clear vehicle miles ID and intermediate Log ID for new record
                SharedPref.setUnAssignedVehicleMilesId("", getApplicationContext());
                SharedPref.setIntermediateLogId("", getApplicationContext());

                JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj("", CompanyId, VinNumber, TruckID,
                        LastDutyStatus, StatusStartTime, "", Globally.LATITUDE, Globally.LONGITUDE, "", "",
                        EngineSeconds, "", currentOdometer, "", Intermediate,
                        IntermediateUpdate, "", false,
                        Constants.getLocationType(getApplicationContext()) );


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
                }else {
                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                            getString(R.string.onduty_rec_occurred), 2005);
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

            String startTime = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartTime, getApplicationContext());
            if (startTime.length() > 10) {

                int timeDiff = constants.minDiffFromUTC(startTime, global, getApplicationContext());
                if (timeDiff >= intermediateRecordTime && LastDutyStatus.equals("DR")) {
                    JSONArray SavedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
                    JSONArray unPostedArray             = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);

                    LastDutyStatus = "DR";

                    Intermediate = true;
                    IntermediateUpdate = true;  //hMethods.getIntermediateUpdateStatus(SavedUnidentifiedLogArray);
                    UnAssignedVehicleMilesId = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());
                    IntermediateLogId = SharedPref.getIntermediateLogId(getApplicationContext());

                    String currentTime = Globally.GetCurrentUTCTimeFormat();
                    String currentOdometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));

                    if(hMethods.IsAlreadyIntermediate(Integer.parseInt(CompanyId), dbHelper)){
                        String UnidenStartTime = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartTime, getApplicationContext());
                        String startOdometer = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartOdometer, getApplicationContext());
                        String startLatitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLatitude, getApplicationContext());
                        String startLongitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLongitude, getApplicationContext());
                        String startEngineSeconds = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartEngineSeconds, getApplicationContext());

                        JSONObject lastObj = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                                LastDutyStatus, UnidenStartTime, currentTime, startLatitude, startLongitude, Globally.LATITUDE, Globally.LONGITUDE,
                                startEngineSeconds, EngineSeconds,  startOdometer, currentOdometer,  Intermediate,
                                IntermediateUpdate, IntermediateLogId, false, Constants.getLocationType(getApplicationContext()));
                        SavedUnidentifiedLogArray.put(SavedUnidentifiedLogArray.length()-1, lastObj);
                        unPostedArray.put(lastObj);

                    }


                    JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                            LastDutyStatus, currentTime, currentTime, Globally.LATITUDE, Globally.LONGITUDE,
                            Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds, EngineSeconds,
                            currentOdometer, currentOdometer, Intermediate,false, "0",
                            false, Constants.getLocationType(getApplicationContext()));

                    SharedPref.setUnIdenLastDutyStatus(LastDutyStatus, getApplicationContext());
                    SavedUnidentifiedLogArray.put(jsonObject);
                    unPostedArray.put(jsonObject);
                    hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, SavedUnidentifiedLogArray);

                    SharedPref.SaveUnidentifiedIntermediateRecord(currentOdometer, currentTime,
                            Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds, getApplicationContext());

                    if (Globally.isConnected(getApplicationContext())) {
                        SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
                    }


                    Globally.PlaySound(getApplicationContext());

                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld),
                            getString(R.string.Inter_record_occ) , 2005);


                }
            }else{
                if(LastDutyStatus.equals("DR")){
                    String currentOdometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));
                    SharedPref.SaveUnidentifiedIntermediateRecord(currentOdometer, Globally.GetCurrentUTCTimeFormat(),
                            Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds, getApplicationContext());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void EndOnDutyOrDrivingRecords(String DutyStatus) {

        try {


            JSONArray SavedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
            JSONArray unPostedArray             = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);

            boolean IsIntermediateEvent = false;
            String lastDutyStatus = "";

            if(DutyStatus.length() == 0) {
                lastDutyStatus = SharedPref.getUnIdenLastDutyStatus(getApplicationContext());
            }else{
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

            if(IsIntermediateEvent) {
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


            //  String currentOdometer = Constants.Convert2DecimalPlacesDouble(Double.parseDouble(Constants.meterToKmWithObd(currentHighPrecisionOdometer));
            String currentOdometer = Constants.ConvertToBeforeDecimal(Constants.meterToKmWithObd(currentHighPrecisionOdometer));

            JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                    lastDutyStatus, startTime, Globally.GetCurrentUTCTimeFormat(), startLatitude, startLongitude,
                    Globally.LATITUDE, Globally.LONGITUDE, startEngineSeconds, EngineSeconds,
                    startOdometer, currentOdometer, Intermediate, IntermediateUpdate, IntermediateLogId,
                    false, Constants.getLocationType(getApplicationContext()));

            SavedUnidentifiedLogArray.put(jsonObject);
            unPostedArray.put(jsonObject);
            hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, SavedUnidentifiedLogArray);

            SharedPref.SaveUnidentifiedIntermediateRecord(currentOdometer, Globally.GetCurrentUTCTimeFormat(),
                    Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds, getApplicationContext());

            if (Globally.isConnected(getApplicationContext())) {
                SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
            }

            String NotificationMessage = "";
            if(DutyStatus.length() == 0) {
                if (lastDutyStatus.equals("DR")) {
                    NotificationMessage = "Unidentified Driving Record ended as Driver logged in.";
                } else {
                    NotificationMessage = "Unidentified On Duty Record ended as Driver logged in.";
                }
            }else{
                if (lastDutyStatus.equals("DR")) {
                    NotificationMessage = "Unidentified Driving Record ended as Vehicle info is not received.";
                } else {
                    NotificationMessage = "Unidentified On Duty Record ended as Vehicle info is not received.";
                }
            }

            Globally.ShowLogoutSpeedNotification(getApplicationContext(), getString(R.string.AlsEld), NotificationMessage, 2005);

            LastDutyStatus = "";
            SharedPref.setUnIdenLastDutyStatus("", getApplicationContext());
            SharedPref.setUnAssignedVehicleMilesId("", getApplicationContext());
            SharedPref.setIntermediateLogId("", getApplicationContext());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void SaveAndUpdateUnidentifiedRecordsApi(JSONArray unPostedLogArray) {
        if(unPostedLogArray.length() > 0 && !UnidentifiedApiInProgress) {
            UnidentifiedApiInProgress = true;
            SaveUpdateUnidentifiedApi.PostDriverLogData(unPostedLogArray, APIs.ADD_UNIDENTIFIED_RECORD, Constants.SocketTimeout20Sec,
                    SaveUnidentifiedEvent);

          /*  constants.saveObdData(constants.getObdSource(getApplicationContext()), "UnIdentified Event Occurred",
                    unPostedLogArray.toString(), currentHighPrecisionOdometer,
                    currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "-1",
                    "", EngineSeconds, global.GetCurrentDateTime(), "",
                    "0", dbHelper, driverPermissionMethod, obdUtil);
*/

        }
      //  constants.saveTempUnidentifiedLog(unPostedLogArray.toString(), obdUtil);
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


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void getResponse(String response, int flag) {

            try {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ConstantsKeys.Status);

                if (SharedPref.getUserName(getApplicationContext()).equals("") && SharedPref.getPassword(getApplicationContext()).equals("")) {
                    if (status.equalsIgnoreCase("true")) {

                        if(flag == GetMalDiaEventDuration){
                            try {

                                JSONArray durationArray = new JSONArray();
                                JSONArray dataArray = new JSONArray(obj.getString(ConstantsKeys.Data));

                                SharedPref.saveUnidentifiedEventStatus(false, "", getApplicationContext());

                                for(int i = dataArray.length()-1 ; i >= 0 ; i--){
                                    JSONObject objItem = (JSONObject) dataArray.get(i);

                                    String EngineHours = "",StartOdometer = "";
                                    if(!objItem.isNull(ConstantsKeys.EngineHours)){
                                        EngineHours = objItem.getString(ConstantsKeys.EngineHours);
                                    }

                                    if(!objItem.isNull(ConstantsKeys.StartOdometer)){
                                        StartOdometer = objItem.getString(ConstantsKeys.StartOdometer);
                                        if(StartOdometer.length() < 8) {
                                            StartOdometer = Constants.kmToMeter1(StartOdometer);
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

                                    JSONObject item = malfunctionDiagnosticMethod.getNewMalDiaDurationObj(
                                            "0",
                                            EventDateTime,
                                            objItem.getString(ConstantsKeys.EventEndDateTime),
                                            DetectionDataEventCode,
                                            objItem.getInt(ConstantsKeys.TotalMinutes),
                                            objItem.getBoolean(ConstantsKeys.IsClearEvent),
                                            EngineHours,
                                            "",
                                            StartOdometer,
                                            EngineHours,
                                            LocationType,
                                            CurrentStatus);

                                    durationArray.put(item);
                                    //  }

                                    DateTime selectedTime = global.getDateTimeObj(EventDateTime, false);
                                   // int minDiff = Constants.getMinDiff(selectedTime, global.GetCurrentJodaDateTime());
                                    long minDiff = Constants.getDateTimeDuration(selectedTime, Globally.GetCurrentUTCDateTime()).getStandardMinutes();
                                    if(DetectionDataEventCode == Constants.UnIdentifiedDrivingDiagnostic && minDiff < Constants.TotalMinInADay){
                                        SharedPref.saveUnidentifiedEventStatus(true, EventDateTime, getApplicationContext());
                                    }

                                }

                                malfunctionDiagnosticMethod.MalDiaDurationHelper(dbHelper, durationArray);

                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());

                            }catch (Exception e){
                                e.printStackTrace();
                            }



                        }else {
                            Log.d("----EventCreated", "Event Flag: "+ SaveUnidentifiedEvent);

                            UnidentifiedApiInProgress = false;
                            JSONObject resultJson = obj.getJSONObject(ConstantsKeys.Data);
                            UnAssignedVehicleMilesId = resultJson.getString(ConstantsKeys.UnAssignedVehicleMilesId);
                            IntermediateLogId = resultJson.getString(ConstantsKeys.IntermediateLogId);
                            SharedPref.setUnAssignedVehicleMilesId(UnAssignedVehicleMilesId, getApplicationContext());
                            SharedPref.setIntermediateLogId(IntermediateLogId, getApplicationContext());

                            // update array in table
                            hMethods.updateUploadedStatusInArray(CompanyId, dbHelper);
                            // hMethods.UnidentifiedRecordLogHelper(Integer.parseInt("0"),dbHelper, new JSONArray());
                        }

                    }else{
                        if(flag == SaveUnidentifiedEvent){
                            UnidentifiedApiInProgress = false;
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
                Log.d("error", "error-" + flag + " : " + error);

                if (flag == SaveUnidentifiedEvent) {
                    UnidentifiedApiInProgress = false;
                }
            }

        }
    };

    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int driver_id, int flag, int inputDataLength) {

            try {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString("Status");
               // String Message = obj.getString("Message");

                isDataAlreadyPosting = false;
                if (status.equals("true")) {

                   if(flag == SaveMalDiagnstcEvent) {
                       Log.d("SaveMalDiagnstcEvent", "SaveMalDiagnstcEvent saved successfully");
                       isMalfncDataAlreadyPosting = false;
                       // clear malfunction array
                       malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());


                   }else if(flag == ClearMalDiaEvent){

                       malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, "0", ClearEventType, true, true, getApplicationContext());
                       GetMalDiaEventsDurationList();

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
            Log.d("errorrr ", ">>>error dialog: " );
            isDataAlreadyPosting = false;
        }


    };



    void saveIgnitionStatus(String ignitionStatus){
        String lastIgnitionStatus = SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext());

        // save log when ignition status is changed.    //SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getApplicationContext())
        if (lastIgnitionStatus.equals("ON")) {
            // save truck info to check power compliance mal/dia later.
            SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, SharedPref.getObdSourceName(getApplicationContext()),
                    global.getCurrentDate(), global.GetCurrentUTCTimeFormat(),
                    SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                    SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()),
                    getApplicationContext());

        }
    }




    // check Power Data Compliance Malfunction/Diagnostic event
    private void checkPowerMalDiaEvent(){

        boolean isEventOccurred = false;
        try{
            if(SharedPref.isPowerMalfunctionOccurred(getApplicationContext()) == false) {
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
                                //  eventType = "PowerMalfunction";
                                SharedPref.savePowerMalfunctionOccurStatus(true,
                                        SharedPref.isPowerDiagnosticOccurred(getApplicationContext()),
                                        global.GetCurrentUTCTimeFormat(), getApplicationContext()); //global.getCurrentDate()

                                // save occurred event in malfunction/diagnostic table
                                saveMalfunctionEventInTable(constants.PowerComplianceMalfunction,
                                        getString(R.string.power_comp_mal_occured),
                                        global.GetCurrentUTCTimeFormat() );

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
                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());

                             /*   constants.saveObdData(constants.getObdSource(getApplicationContext()), "PowerMalEvent",
                                        malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper).toString(), currentHighPrecisionOdometer,
                                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "-1",
                                        "", EngineSeconds, global.GetCurrentDateTime(), "",
                                        "0", dbHelper, driverPermissionMethod, obdUtil);
*/


                            }
                        } else {
                            if (isPowerCompDiaAllowed) {

                                SharedPref.savePowerMalfunctionOccurStatus(
                                        SharedPref.isPowerMalfunctionOccurred(getApplicationContext()),
                                        true, global.GetCurrentUTCTimeFormat(), getApplicationContext());

                                constants.saveDiagnstcStatus(getApplicationContext(), true);

                                // save occurred event in malfunction/diagnostic table
                                saveMalfunctionEventInTable(constants.PowerComplianceDiagnostic, getString(R.string.power_dia_occured),
                                        Globally.GetCurrentUTCTimeFormat());


                                // save malfunction entry in duration table
                                malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, "0",
                                        Globally.GetCurrentUTCTimeFormat(),
                                        global.GetCurrentUTCTimeFormat(),
                                        Constants.PowerComplianceDiagnostic,  "-1",
                                        Constants.getLocationType(getApplicationContext()), "",
                                        constants, getApplicationContext());

                                // update mal/dia status for enable disable according to log
                                malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());
                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());


                               /* constants.saveObdData(constants.getObdSource(getApplicationContext()), "PowerDiaEvent",
                                        malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper).toString(), currentHighPrecisionOdometer,
                                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "-1",
                                        "", EngineSeconds, global.GetCurrentDateTime(), "",
                                        "0", dbHelper, driverPermissionMethod, obdUtil);

*/
                            }
                        }


                    }

                }
            }

            // check if Malfunction/Diagnostic event occurred in ECM disconnection
            if (SharedPref.isPowerDiagnosticOccurred(getApplicationContext())) {
                isEventOccurred = true;
                    ClearEventUpdate( Constants.PowerComplianceDiagnostic,
                                "Auto clear Power data diagnostic event after ECM data received");

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


               /* } else {
                    // update EndTime with TotalMinutes instantly but not cleared, because we are clearing it after 5 min
                    //  malfunctionDiagnosticMethod.updateTimeInPowerDiagnoseDia(dbHelper, getApplicationContext());
                }*/

            }

            // this case is for engine sync event because of truck ignition was not updated if power event was already occurred
            if(!isEventOccurred){   // && SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, getApplicationContext()).equals("")
                SharedPref.SaveTruckInfoOnIgnitionChange(ignitionStatus, SharedPref.getObdSourceName(getApplicationContext()),
                        global.getCurrentDate(), global.GetCurrentUTCTimeFormat(),
                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext()),
                        SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext()), getApplicationContext());
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }




    // Save Malfunction/Diagnostic Event Details
    void saveMalfunctionEventInTable(String malDiaType, String MalfunctionDefinition, String occurredTime){

        String clearedTime = "", clearTimeOdmeter = "", clearedTimeEngineHour = "";
        JSONObject newOccuredEventObj = new JSONObject();
        String lastSavedOdometer =   SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, getApplicationContext());
        String lastSavedEngHr =  SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, getApplicationContext());
        String currentOdometer = SharedPref.getObdOdometer(getApplicationContext());
        String currentEngHr = constants.get2DecimalEngHour(getApplicationContext());   //SharedPref.getObdEngineHours(getApplicationContext());


        // save malfunction/diagnostic occur event to server with few inputs
        if(malDiaType.equals(Constants.PowerComplianceDiagnostic) || malDiaType.equals(Constants.PowerComplianceMalfunction) ){
            newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                    "0", "", VinNumber,
                    DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
                    DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext()),
                    lastSavedEngHr,
                    lastSavedOdometer,
                    currentOdometer,
                    occurredTime, malDiaType, MalfunctionDefinition,
                    false, clearedTime,
                    currentOdometer,
                    currentEngHr, Constants.getLocationType(getApplicationContext()), ""
            );

        }else {

            newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                    "0", "", VinNumber,
                    DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext()),
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
        SaveMalfnDiagnstcLogToServer(malArray, "0");


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



    // update clear event info in existing log if not uploaded on server yet
    private void ClearEventUpdate(String dataDiagnostic, String clearDesc){
        try{
           // String dateee = SharedPref.getPowerMalOccTime(getApplicationContext());
           // int minDiff = constants.getMinDifference(dateee, global.getCurrentDate());

          //  boolean isUnPostedEvent = malfunctionDiagnosticMethod.isUnPostedOfflineEvent( dataDiagnostic, dbHelper);

          //  if(isUnPostedEvent){
                // update clear event array in duration table and not posted to server with duration table input because occured event already exist TABLE_MALFUNCTION_DIANOSTIC
                malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, "0", dataDiagnostic, true,true, getApplicationContext());

                // update offline unposted event array
                JSONArray malArray = malfunctionDiagnosticMethod.updateOfflineUnPostedMalDiaEvent("0", dataDiagnostic,
                        clearDesc, dbHelper, getApplicationContext());

                // call api
                SaveMalfnDiagnstcLogToServer(malArray, "0");



           /* }else {
                CheckEventsForClear(dataDiagnostic, true, minDiff);
            }*/

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

                    JSONArray clearEventArray = malfunctionDiagnosticMethod.updateAutoClearEvent(dbHelper, "0", EventCode, true, isUpdate, getApplicationContext());
                    ClearEventType = EventCode;

                    /* We have 2 api for clear event. 1 for online events and 2nd is use here to clear in offline and input data as array.*/
                    if(clearEventArray.length() > 0){
                        // call clear event API.
                        if(global.isConnected(getApplicationContext() )) {
                            saveDriverLogPost.PostDriverLogData(clearEventArray, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT_BY_DATE,
                                    Constants.SocketTimeout30Sec, true, false, 0, ClearMalDiaEvent);
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

                }



            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    private void checkEngineSyncMalDiaOccurredEvent(){

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
                        if (timeInSec >= 70 ) {

                            boolean isEngSyncDiaOccurred = SharedPref.isEngSyncDiagnstc(getApplicationContext());
                            if (isEngSyncDiaOccurred == false && isEngineSyncDiaAllowed) {

                                SharedPref.saveEngSyncDiagnstcStatus(true, getApplicationContext());
                                constants.saveDiagnstcStatus(getApplicationContext(), true);
                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());
                                SharedPref.setEngSyncClearEventCallTime(Globally.GetCurrentDateTime(), getApplicationContext());

                                // save occurred event in malfunction/diagnostic table
                                saveMalfunctionEventInTable(Constants.EngineSyncDiagnosticEvent,
                                        getString(R.string.eng_sync_dia_occured),
                                        global.GetCurrentUTCTimeFormat() );


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

                               /* constants.saveObdData(constants.getObdSource(getApplicationContext()), "EngSyncDia - lastIgnitionStatus: " + lastIgnitionStatus,
                                        malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper).toString(), currentHighPrecisionOdometer,
                                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "-1",
                                        "", EngineSeconds, global.GetCurrentDateTime(), "",
                                        "0", dbHelper, driverPermissionMethod, obdUtil);
*/


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
                                            double last24HrEngSyncEventTime = malfunctionDiagnosticMethod.getLast24HourEventsDurInMin(Constants.EngineSyncDiagnosticEvent,
                                                    "", 0.0, constants, driverPermissionMethod, obdUtil, dbHelper);  //getTotalEngSyncMissingMin(DriverId, dbHelper);
                                            int minDiff = constants.getMinDifference(SharedPref.getObdLastStatusTime(getApplicationContext()),
                                                    global.GetCurrentDateTime());
                                            double totalEngSyncMissingMin = last24HrEngSyncEventTime + minDiff;
                                            if (totalEngSyncMissingMin >= Constants.PowerEngSyncMalOccTime) {

                                                SharedPref.saveEngSyncMalfunctionStatus(true, getApplicationContext());
                                                constants.saveMalfncnStatus(getApplicationContext(), true);
                                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());

                                                // save occurred event in malfunction/diagnostic table
                                                saveMalfunctionEventInTable(Constants.EngineSyncMalfunctionEvent,
                                                        getString(R.string.eng_sync_mal_occured),
                                                        global.GetCurrentUTCTimeFormat());  //SharedPref.getObdLastStatusUtcTime(getApplicationContext())


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

/*
                                                constants.saveObdData(constants.getObdSource(getApplicationContext()), "EngSyncMal - lastIgnitionStatus: " + lastIgnitionStatus,
                                                        malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper).toString(), currentHighPrecisionOdometer,
                                                        currentHighPrecisionOdometer, "", ignitionStatus, truckRPM, "-1",
                                                        "", EngineSeconds, global.GetCurrentDateTime(), "",
                                                        "0", dbHelper, driverPermissionMethod, obdUtil);
*/


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
                                        currentHighPrecisionOdometer, global.GetCurrentDateTime(),
                                        global.GetCurrentUTCTimeFormat(), getApplicationContext());
                            }


                            // check position malfunction event
                            String locMalDiaEvent = constants.isPositionMalfunctionEvent( "0", malfunctionDiagnosticMethod,
                                    dbHelper, driverPermissionMethod, obdUtil, getApplicationContext());
                            if (locMalDiaEvent.length() > 0) {
                                SharedPref.setClearEventCallTime(global.GetCurrentDateTime(), getApplicationContext());
                                if (locMalDiaEvent.equals("M")) {   // && SharedPref.isLocMalfunctionOccur(getApplicationContext()) == false

                                    // save occurred event in malfunction/diagnostic table
                                    saveMalfunctionEventInTable(Constants.PositionComplianceMalfunction, getString(R.string.pos_mal_occured),
                                            global.GetCurrentUTCTimeFormat());  //SharedPref.getEcmObdUtcTime(getApplicationContext())

                                    // save malfunction entry in duration table
                                    malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, "0",
                                            global.GetCurrentUTCTimeFormat(), global.GetCurrentUTCTimeFormat(),
                                            Constants.PositionComplianceMalfunction,  "-1",
                                            "E", "", constants, getApplicationContext());

                                    // update mal/dia status for enable disable according to log
                                    malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable("0", global, constants, dbHelper, getApplicationContext());

                                    SharedPref.saveLocMalfunctionOccurStatus(true, global.getCurrentDate(),
                                            global.GetCurrentUTCTimeFormat(), getApplicationContext());
                                    constants.saveMalfncnStatus(getApplicationContext(), true);

                                    global.ShowLocalNotification(getApplicationContext(),
                                            getString(R.string.malfunction_events),
                                            getString(R.string.pos_mal_event_desc), 2091);
                                    Globally.PlaySound(getApplicationContext());


                                   /* constants.saveObdData(constants.getObdSource(getApplicationContext()), "Position Malfunction- Time: " +
                                                    SharedPref.getLocDiagnosticOccuredTime(getApplicationContext()),
                                            "",
                                            "-1", currentHighPrecisionOdometer, "", "", truckRPM, "-1",
                                            "-1", EngineSeconds, "", "",
                                            "0", dbHelper, driverPermissionMethod, obdUtil);
*/

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
                            malfunctionDiagnosticMethod.updateTimeOnLocationReceived(dbHelper); //Constants.ConstLocationMissing,
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
