package com.background.service;

import static com.messaging.logistic.Globally.DateFormat;
import static com.messaging.logistic.Globally.GetCurrentUTCTimeFormat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.htstart.htsdk.HTBleSdk;
import com.htstart.htsdk.bluetooth.HTBleData;
import com.htstart.htsdk.bluetooth.HTBleDevice;
import com.htstart.htsdk.bluetooth.HTModeSP;
import com.htstart.htsdk.minterface.HTBleScanListener;
import com.htstart.htsdk.minterface.IReceiveListener;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.MalfunctionDiagnosticMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.notifications.NotificationManagerSmart;
import com.wifi.settings.WiFiConfig;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import dal.tables.OBDDeviceData;
import obdDecoder.Decoder;


public class AfterLogoutService extends Service implements TextToSpeech.OnInitListener {

    String ServerPackage = "com.als.obd";
    String ServerService = "com.als.obd.services.MainService";
    String ignitionStatus = "", truckRPM = "", currentHighPrecisionOdometer = "";
    String noObd = "obd not connected";
    String AlertMsg = "Your vehicle is moving and there is no driver login in eLog book";
    String AlertMsgSpeech = "Your vehicle is moving and there is no driver login in e log book";

    String TAG_OBD = "OBD Service";
    private static final long TIME_INTERVAL_WIFI  = 10 * 1000;   // 10 sec
    private static final long TIME_INTERVAL_WIRED = 3 * 1000;   // 3 sec

    String TAG = "Service";
    boolean isStopService = false;
    double lastVehSpeed = -1;

    int ThreshHoldSpeed = 8;
    int intermediateRecordTime = 60; // intermediate record time is 60 min

    boolean isTempTimeValidate = true;
    String TempTimeAtStart = "", CompanyId = "", TruckID = "", TempStatus = "";
    String LastDutyStatus = "", StatusStartTime = "";   //, CurrentDutyStatus = "";
    String VinNumber = "", UnAssignedVehicleMilesId = "", IntermediateLogId = "", EngineSeconds = "";
    VolleyRequest truckListApi,companyListApi;
    SaveUnidentifiedRecord SaveUpdateUnidentifiedApi;
    boolean Intermediate = false;
    boolean IntermediateUpdate = false;
    CheckConnectivity checkConnectivity;
    HelperMethods hMethods;
    DBHelper dbHelper;
    Globally global;
    JSONArray unPostedLogArray = new JSONArray();
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

    private static final String SERVICE_UUID = "00001000-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTER_WRITE_UUID = "00001001-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTER_NOTIFY_UUID = "00001002-0000-1000-8000-00805f9b34fb";

    private static final String TAG_BLE = "BleService";
    private static final String TAG_BLE_CONNECT = "BleConnect";
    private static final String TAG_BLE_OPERATION = "BleOperation";
    boolean mIsScanning = false;
    boolean isBleObdRespond = false;

    int bleScanCount = 0;
    int ObdPreference;
    int timerCount = 0;

    private BluetoothAdapter mBTAdapter;
    private ArrayList<HTBleDevice> mHTBleDevices = new ArrayList<>();
    private LinkedList<HTBleData> mHtblData = new LinkedList<>();
    Intent locServiceIntent;

    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;

    double tempOdo = 1179876199;  //1.090133595E9
    double tempEngHour = 22999.95;



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

        constants               = new Constants();
        mTimer                  = new Timer();

        saveDriverLogPost       = new SaveDriverLogPost(getApplicationContext(), saveLogRequestResponse);


        //  ------------- Wired OBD ----------
        this.connection = new RemoteServiceConnection();
        this.replyTo = new Messenger(new IncomingHandler());
        BindConnection();

        ObdPreference = SharedPref.getObdPreference(getApplicationContext());
        SharedPref.setNotiShowTime("", getApplicationContext());

        initUnidentifiedObj();

        if(ObdPreference == Constants.OBD_PREF_BLE) {
            initBleListener();
            checkPermissionsBeforeScanBle();
        }else{
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
            int speed = 0;

            try {
                ignitionStatus = bundle.getString(constants.OBD_IgnitionStatus);
                truckRPM = bundle.getString(constants.OBD_RPM);
                speed = bundle.getInt(constants.OBD_Vss);
                EngineSeconds = bundle.getString(constants.OBD_EngineHours);
                VinNumber = bundle.getString(constants.OBD_VINNumber);

                if(bundle.getString(constants.OBD_HighPrecisionOdometer) != null) {
                    currentHighPrecisionOdometer = bundle.getString(constants.OBD_HighPrecisionOdometer);
                }

            }catch (Exception e){
                e.printStackTrace();
            }


            // ---------------- temp data ---------------------
         /*   ignitionStatus = "ON"; truckRPM = "35436";
            VinNumber = SharedPref.getLastSavedVINNumber(getApplicationContext());
            if(LoginActivity.isDriving) {
                speed = 10;
                tempOdo = tempOdo + 50;
                tempEngHour = tempEngHour + .20;
            }else{
                speed = 0;
            }
            currentHighPrecisionOdometer = "" + BigDecimal.valueOf(tempOdo).toPlainString();
            EngineSeconds = ""+tempEngHour;

*/



            //========================================================
            isWiredCallBackCalled = true;
            checkObdDataWithRule(speed);
        }
    }


    void startLocationService(){
        try {
            // call location service
            locServiceIntent = new Intent(getApplicationContext(), LocationService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(locServiceIntent);
            }
            startService(locServiceIntent);
        } catch (Exception e) {
            e.printStackTrace();
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
                        LastDutyStatus = obj.getString(ConstantsKeys.DutyStatus);

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



    private void checkObdDataWithRule(int speed){
        try {

            if (SharedPref.getUserName(getApplicationContext()).equals("") &&
                    SharedPref.getPassword(getApplicationContext()).equals("")) {

                if(SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED ||
                        SharedPref.getObdStatus(getApplicationContext()) == Constants.BLE_CONNECTED) {

                    if (ignitionStatus.equals("ON") && !truckRPM.equals("0")) {
                        SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, "", "", getApplicationContext());

                        String savedDate = SharedPref.getHighPrecesionSavedTime(getApplicationContext());
                        if(savedDate.length() == 0){
                            savedDate = Globally.GetCurrentUTCTimeFormat();
                            SharedPref.saveHighPrecisionOdometer(currentHighPrecisionOdometer, savedDate, getApplicationContext());
                        }

                        if (speed >= 8 && lastVehSpeed >= 8) {

                            showNotificationAlert(speed);

                            if(SharedPref.IsAppRestricted(getApplicationContext())) {
                                SharedPref.setLoginAllowedStatus(false, getApplicationContext());
                            }


                            if(TruckID.length() > 0 && CompanyId.length() > 0) {
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

                        } else {
                            if(speed == 0) {
                                SharedPref.setLoginAllowedStatus(true, getApplicationContext());

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


                        lastVehSpeed = speed;

                        // ping wired server to get data
                        if( ObdPreference == Constants.OBD_PREF_WIRED &&
                                SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED) {
                            CallWired(TIME_INTERVAL_WIRED);
                        }




                    } else {
                        lastVehSpeed = -1;
                        SharedPref.setLoginAllowedStatus(true, getApplicationContext());


                        if( ObdPreference == Constants.OBD_PREF_WIRED ) {
                            if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_CONNECTED ||
                                    SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED) {
                                SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, "",  "", getApplicationContext());
                                CallWired(TIME_INTERVAL_WIFI);
                            }
                        }

                        // check Unidentified event occurrence
                        checkUnIdentifiedDiagnosticEvent(-1, false);

                    }
                }else{
                    lastVehSpeed = -1;
                    SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                    if( ObdPreference == Constants.OBD_PREF_WIRED ) {
                        SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, "",  "", getApplicationContext());
                        CallWired(Constants.SocketTimeout5Sec);
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

    // check Unidentified event occurrence
    private void checkUnIdentifiedDiagnosticEvent(int speed, boolean isVehicleInMotion){
        try{
            if (SharedPref.isUnidentifiedDiaEvent(getApplicationContext()) == false) {
                boolean isVehicleMotionChanged = malfunctionDiagnosticMethod.isVehicleMotionChanged(isVehicleInMotion,
                        Integer.parseInt(CompanyId), dbHelper);

                if (isVehicleMotionChanged) {
                    malfunctionDiagnosticMethod.saveVehicleMotionChangeTime(speed, Integer.parseInt(CompanyId), dbHelper);
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
                Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD", "Vehicle Speed: " + speed + " "
                        + AlertMsg, 2003);
                SpeakOutMsg(AlertMsgSpeech);
            }else{
                if (notificationShowTime.length() > 10) {
                    DateTime currentTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
                    DateTime lastCallDateTime = Globally.getDateTimeObj(notificationShowTime, false);
                    long secDiff = Constants.getDateTimeDuration(lastCallDateTime, currentTime).getStandardSeconds();
                    if (secDiff >= 120) {    // Showing notification After 2 min interval
                        SharedPref.setNotiShowTime(Globally.GetCurrentDateTime(), getApplicationContext());

                        Globally.PlaySound(getApplicationContext());
                        Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD",
                                "Vehicle Speed: " + speed + " " + AlertMsg, 2003);
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

    private void checkWiredObdConnection(){

        ShellUtils.CommandResult obdShell = ShellUtils.execCommand("cat /sys/class/power_supply/usb/type", false);

        if (obdShell.result == 0) {
            if (obdShell.successMsg.contains("USB_DCP")) {

                if(SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED){
                    StartStopServer(constants.WiredOBD);
                }

                // Connected State
                SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, "", "", getApplicationContext());

            } else {
                // Disconnected State. Save only when last status was not already disconnected
                SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, "", "", getApplicationContext());
                isWiredCallBackCalled = false;

                // check Unidentified event occurrence
                checkUnIdentifiedDiagnosticEvent(-1, false);

            }
        } else {
            // Error

            isWiredCallBackCalled = false;
            SharedPref.SaveObdStatus(Constants.WIRED_ERROR, "", "", getApplicationContext());


            //  temp values for testing
      /*    if(SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED){
                StartStopServer(constants.WiredOBD);
            }
            SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, "", "", getApplicationContext());
*/
        }

    }


    private void checkWifiOBDConnection(){

        // communicate with wired OBD server app with Message
       // StartStopServer(constants.WiredOBD);

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
            EndOnDutyOrDrivingRecords();
          //  postEventsToServer();
        }else {
            if (ObdPreference == Constants.OBD_PREF_BLE) {
                int ObdStatus = SharedPref.getObdStatus(getApplicationContext());
                boolean isConnected = HTBleSdk.Companion.getInstance().isConnected(HTModeSP.INSTANCE.getDeviceMac());
                if(pingStatus.equals("ble_start")){
                    if (!isConnected || ObdStatus != Constants.BLE_CONNECTED ) { //!mIsScanning &&

                        if(mIsScanning){
                            HTBleSdk.Companion.getInstance().stopHTBleScan();
                            mIsScanning = false;
                        }

                        //initBleListener();
                        initHtBle();
                    }else{
                        if (isConnected && ObdStatus != Constants.BLE_CONNECTED ) {

                            HTBleSdk.Companion.getInstance().disAllConnect();
                            mIsScanning = false;

                            initBleListener();
                            initHtBle();

                        }
                    }



                }else{
                    initHtBle();
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
                    checkPermissionsBeforeScanBle();
                }else if(ObdPreference == Constants.OBD_PREF_WIRED) {

                    checkWiredObdConnection();

                    if(isWiredCallBackCalled == false && SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED){
                        StartStopServer(constants.WiredOBD);
                    }else{
                        String obdLastCallDate = SharedPref.getWiredObdCallTime(getApplicationContext());
                        if (obdLastCallDate.length() > 10) {
                            int lastCalledMinDiff = constants.getMinDifference(obdLastCallDate, Globally.GetCurrentDateTime());
                            if (lastCalledMinDiff > 3) {
                                SharedPref.SetWiredObdCallTime(Globally.GetCurrentDateTime(), getApplicationContext());
                                StartStopServer(constants.WiredOBD);
                            }
                        }
                    }

                }else{
                    checkWifiOBDConnection();
                }

                if(CompanyId.length() > 0 && SharedPref.isUnidentifiedDiaEvent(getApplicationContext()) == false) {
                    timerCount++;
                    if (timerCount > 3) {
                        timerCount = 0;
                        String odometer = Constants.meterToKmWithObd(currentHighPrecisionOdometer);

                        // save unidentified events if vehicle is in motion for 30 min in last 24 hour
                        malfunctionDiagnosticMethod.saveVehicleMotionStatus(odometer, EngineSeconds, Integer.parseInt(CompanyId), TruckID, VinNumber, dbHelper, getApplicationContext());
                    }

                    postEventsToServer();
                }


                }


        }
    };

    // post occurred unidentified events to server
    private void postEventsToServer(){
        JSONArray array = malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper);
        if (global.isConnected(getApplicationContext()) && array.length() > 0 && isDataAlreadyPosting == false) {
            isDataAlreadyPosting = true;
            saveDriverLogPost.PostDriverLogData(array, APIs.MALFUNCTION_DIAGNOSTIC_EVENT, Constants.SocketTimeout30Sec,
                    false, false, 1, 101);
        }
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

                                if (ignitionStatus.equals("true")) {
                                    SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, "",  "", getApplicationContext());


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


                                } else {
                                    if(SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED) {
                                        SharedPref.setLoginAllowedStatus(true, getApplicationContext());
                                        SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED,  "",  "", getApplicationContext());
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
        boolean isConnected = HTBleSdk.Companion.getInstance().isConnected(HTModeSP.INSTANCE.getDeviceMac());

        try {
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            } else {
                if (constants.CheckGpsStatusToCheckMalfunction(getApplicationContext())) {

                    if (!mIsScanning && !isConnected) {

                        // ignore scan after 5 attempts if device not found
                      //  if (bleScanCount < 5) {
                            StartScanHtBle();
                       /* } else {
                            if (bleScanCount == 5) {
                                bleScanCount++;
                                HTBleSdk.Companion.getInstance().stopHTBleScan();
                            }
                        }*/


                    }

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
        HTBleSdk.Companion.getInstance().initHTBleSDK(getApplicationContext(), new IReceiveListener() {
            @Override
            public void onConnected(@org.jetbrains.annotations.Nullable String s) {
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_CONNECTED, s));
                Log.d("BleObd","onConnected");
                sendBroadcast(true);
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

                // check Unidentified event occurrence
                checkUnIdentifiedDiagnosticEvent(-1, false);

            }

            @Override
            public void onReceive(@NotNull String address, @NotNull String uuid, @NotNull HTBleData htBleData) {
                mHtblData.add(htBleData);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));

                sendBroadcast(true);
                int VehicleSpeed = Integer.valueOf(htBleData.getVehicleSpeed());
                truckRPM = htBleData.getEngineSpeed();
                VinNumber = htBleData.getVIN_Number();
                EngineSeconds = htBleData.getEngineHours();
                currentHighPrecisionOdometer = htBleData.getOdoMeter();
                Globally.LATITUDE = htBleData.getLatitude();
                Globally.LONGITUDE = htBleData.getLongitude() ;

                Log.d("BleObd","onReceive Data: "+ truckRPM + ", " + currentHighPrecisionOdometer +", " + Globally.LATITUDE);

                if(truckRPM.length() > 0) {
                    if (Integer.valueOf(truckRPM) > 0) {
                        ignitionStatus = "ON";
                    } else {
                        ignitionStatus = "OFF";
                    }

                    checkObdDataWithRule(VehicleSpeed);

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

        if(HTBleSdk.Companion.getInstance() == null) {
            initBleListener();
        }
        HTBleSdk.Companion.getInstance().startHTBleScan(new HTBleScanListener() {
            @Override
            public void onScanStart() {
                mHTBleDevices.clear();

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
                if (HTBleSdk.Companion.getInstance().isConnected(HTModeSP.INSTANCE.getDeviceMac()));
            }
        });

    }


    private void connectHtBle(final HTBleDevice htBleDevice) {
        HTBleSdk.Companion.getInstance().stopHTBleScan();
        if (HTBleSdk.Companion.getInstance().isAllConnected()) {
            // ToastUtil.show(getApplicationContext(), getString(R.string.ht_connect_error_other));
        } else {
            HTBleSdk.Companion.getInstance().connect(htBleDevice);
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
               /* if(HTBleSdk.Companion.getInstance().isConnected(HTModeSP.INSTANCE.getDeviceMac())) {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, HTModeSP.INSTANCE.getDeviceMac()));
                    HTBleSdk.Companion.getInstance().disAllConnect();
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
            UnAssignedVehicleMilesId = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());
            IntermediateLogId = SharedPref.getIntermediateLogId(getApplicationContext());

            String odometer = Constants.meterToKmWithObd(currentHighPrecisionOdometer);
            JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                    LastDutyStatus, StatusStartTime, "", Globally.LATITUDE, Globally.LONGITUDE, "", "",
                    EngineSeconds, "", odometer, "", Intermediate,
                    IntermediateUpdate, "0", false);

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

            Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD",
                    "UnIdentified driving record occurred" , 2005);


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
            boolean isOnDuty =  (LastDutyStatus.equals("DR") && obdSpeed == 0 && continueSpeedZeroDiff > 5);    //5  CurrentDutyStatus.equals("OD") &&

            if (isDriving || isOnDuty) {
                // clear last saved records
                SharedPref.SaveUnidentifiedIntermediateRecord("", "", "", "", "",
                        getApplicationContext());

                UnAssignedVehicleMilesId = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());
                IntermediateLogId = SharedPref.getIntermediateLogId(getApplicationContext());


                JSONArray savedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
                JSONArray unPostedArray = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);

                String odometer = Constants.meterToKmWithObd(currentHighPrecisionOdometer);
                // get last update object from unPosted Unidentified Log array
                JSONObject unPostedLogLastObj = hMethods.updateLastRecordOfUnIdentifiedLog(unPostedArray, Globally.GetCurrentUTCTimeFormat(),
                        odometer, Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds);

                if (unPostedLogLastObj != null && unPostedArray.length() > 0) {
                    unPostedArray.put(unPostedArray.length() - 1, unPostedLogLastObj);
                }


                // get last update object from posted/unposted Unidentified Log array
                JSONObject savedUnidentifiedLogLastObj = hMethods.updateLastRecordOfUnIdentifiedLog(savedUnidentifiedLogArray, Globally.GetCurrentUTCTimeFormat(),
                        odometer, Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds);

                if (savedUnidentifiedLogLastObj != null && savedUnidentifiedLogArray.length() > 0) {
                    savedUnidentifiedLogArray.put(savedUnidentifiedLogArray.length() - 1, savedUnidentifiedLogLastObj);

                    if(unPostedArray.length() == 0){
                        unPostedArray.put(savedUnidentifiedLogLastObj);
                    }
                }

                Intermediate = false;
                IntermediateUpdate = false;
                StatusStartTime = Globally.GetCurrentUTCTimeFormat();

                if (isDriving) {
                    LastDutyStatus = "DR";
                } else {
                    LastDutyStatus = "OD";
                }

                JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                        LastDutyStatus, StatusStartTime, "", Globally.LATITUDE, Globally.LONGITUDE, "", "",
                        EngineSeconds, "", odometer, "", Intermediate,
                        IntermediateUpdate, IntermediateLogId, false);


                SharedPref.setUnIdenLastDutyStatus(LastDutyStatus, getApplicationContext());
                savedUnidentifiedLogArray.put(jsonObject);
                unPostedArray.put(jsonObject);
                hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, savedUnidentifiedLogArray);

                if (Globally.isConnected(getApplicationContext())) {
                    SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
                }

                Globally.PlaySound(getApplicationContext());

                if(LastDutyStatus.equals("DR")){
                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD",
                            "Unidentified Driving Record occurred as vehicle is moving but no Driver is logged in" , 2005);
                }else {
                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD",
                            "Unidentified OnDuty record occurred as vehicle is not moving but engine is idling", 2005);
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

                    String startOdometer = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartOdometer, getApplicationContext());
                    String startLatitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLatitude, getApplicationContext());
                    String startLongitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLongitude, getApplicationContext());
                    String startEngineSeconds = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartEngineSeconds, getApplicationContext());

                    Intermediate = true;
                    IntermediateUpdate = true;  //hMethods.getIntermediateUpdateStatus(SavedUnidentifiedLogArray);
                    UnAssignedVehicleMilesId = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());
                    IntermediateLogId = SharedPref.getIntermediateLogId(getApplicationContext());

                    String odometer = Constants.meterToKmWithObd(currentHighPrecisionOdometer);
                    JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                            LastDutyStatus, startTime, Globally.GetCurrentUTCTimeFormat(), startLatitude, startLongitude,
                            Globally.LATITUDE, Globally.LONGITUDE, startEngineSeconds, EngineSeconds,
                            startOdometer, odometer, Intermediate,
                            IntermediateUpdate, IntermediateLogId, false);

                    SharedPref.setUnIdenLastDutyStatus(LastDutyStatus, getApplicationContext());
                    SavedUnidentifiedLogArray.put(jsonObject);
                    unPostedArray.put(jsonObject);
                    hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, SavedUnidentifiedLogArray);

                    SharedPref.SaveUnidentifiedIntermediateRecord(odometer, Globally.GetCurrentUTCTimeFormat(),
                            Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds, getApplicationContext());

                    if (Globally.isConnected(getApplicationContext())) {
                        SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
                    }


                    Globally.PlaySound(getApplicationContext());

                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD",
                            "Intermediate record occurred" , 2005);


                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void EndOnDutyOrDrivingRecords() {

        try {

            String startTime = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartTime, getApplicationContext());

            JSONArray SavedUnidentifiedLogArray = hMethods.getSavedUnidentifiedLogArray(Integer.parseInt(CompanyId), dbHelper);
            JSONArray unPostedArray             = hMethods.getUnpostedLogArray(Integer.parseInt(CompanyId), dbHelper);

            String LastDutyStatus = SharedPref.getUnIdenLastDutyStatus(getApplicationContext());

            String startOdometer = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartOdometer, getApplicationContext());
            String startLatitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLatitude, getApplicationContext());
            String startLongitude = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartLongitude, getApplicationContext());
            String startEngineSeconds = SharedPref.getUnidentifiedIntermediateRecord(ConstantsKeys.UnidenStartEngineSeconds, getApplicationContext());

            UnAssignedVehicleMilesId = SharedPref.getUnAssignedVehicleMilesId(getApplicationContext());
            IntermediateLogId = SharedPref.getIntermediateLogId(getApplicationContext());
            Intermediate = false;
            IntermediateUpdate = false;

            String odometer = Constants.meterToKmWithObd(currentHighPrecisionOdometer);
            JSONObject jsonObject = constants.getUnIdentifiedLogJSONObj(UnAssignedVehicleMilesId, CompanyId, VinNumber, TruckID,
                    LastDutyStatus, startTime, Globally.GetCurrentUTCTimeFormat(), startLatitude, startLongitude,
                    Globally.LATITUDE, Globally.LONGITUDE, startEngineSeconds, EngineSeconds,
                    startOdometer, odometer, Intermediate,
                    IntermediateUpdate, IntermediateLogId, false);

            SharedPref.setUnIdenLastDutyStatus(LastDutyStatus, getApplicationContext());
            SavedUnidentifiedLogArray.put(jsonObject);
            unPostedArray.put(jsonObject);
            hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, SavedUnidentifiedLogArray);

            SharedPref.SaveUnidentifiedIntermediateRecord(odometer, Globally.GetCurrentUTCTimeFormat(),
                    Globally.LATITUDE, Globally.LONGITUDE, EngineSeconds, getApplicationContext());

            if (Globally.isConnected(getApplicationContext())) {
                SaveAndUpdateUnidentifiedRecordsApi(unPostedArray);
            }

            if(LastDutyStatus.equals("DR")) {
                Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD",
                        "Unidentified Driving Record ended as Driver logged in.", 2005);
            }else{
                Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD",
                        "Unidentified On Duty Record ended as Driver logged in.", 2005);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void SaveAndUpdateUnidentifiedRecordsApi(JSONArray unPostedLogArray) {
        SaveUpdateUnidentifiedApi.PostDriverLogData(unPostedLogArray, APIs.ADD_UNIDENTIFIED_RECORD, Constants.SocketTimeout20Sec,103);
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
                        JSONObject resultJson = obj.getJSONObject(ConstantsKeys.Data);
                        UnAssignedVehicleMilesId = resultJson.getString(ConstantsKeys.UnAssignedVehicleMilesId);
                        IntermediateLogId = resultJson.getString(ConstantsKeys.IntermediateLogId);
                        SharedPref.setUnAssignedVehicleMilesId(UnAssignedVehicleMilesId, getApplicationContext());
                        SharedPref.setIntermediateLogId(IntermediateLogId, getApplicationContext());

                        // update array in table
                        hMethods.updateUploadedStatusInArray(CompanyId, dbHelper);
                        // hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId),dbHelper, new JSONArray());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
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

                if (status.equals("true")) {
                    isDataAlreadyPosting = false;
                    // clear malfunction array
                    malfunctionDiagnosticMethod.MalDiaDurationHelper(dbHelper, new JSONArray());

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }


        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("errorrr ", ">>>error dialog: " );

        }


    };


}
