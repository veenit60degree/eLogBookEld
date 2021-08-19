package com.background.service;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

import com.ble.util.BleUtil;
import com.ble.util.ConstantEvent;
import com.ble.util.EventBusInfo;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.ShellUtils;
import com.constants.TcpClient;
import com.htstart.htsdk.HTBleSdk;
import com.htstart.htsdk.bluetooth.HTBleData;
import com.htstart.htsdk.bluetooth.HTBleDevice;
import com.htstart.htsdk.bluetooth.HTModeSP;
import com.htstart.htsdk.minterface.HTBleScanListener;
import com.htstart.htsdk.minterface.IReceiveListener;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.notifications.NotificationManagerSmart;
import com.wifi.settings.WiFiConfig;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
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
    String ignitionStatus = "", truckRPM = "";
    String noObd = "obd not connected";
    String AlertMsg = "Your vehicle is moving and there is no driver login in eLog book";
    String AlertMsgSpeech = "Your vehicle is moving and there is no driver login in e log book";

    String TAG_OBD = "OBD Service";
    private static final long TIME_INTERVAL_WIFI  = 10 * 1000;   // 10 sec
    private static final long TIME_INTERVAL_WIRED = 2 * 1000;   // 2 sec
    private static final long TIME_INTERVAL_LIMIT = 70000;

    String TAG = "Service";
    boolean isStopService = false;
    double lastVehSpeed = -1;

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

    private BluetoothAdapter mBTAdapter;
    private ArrayList<HTBleDevice> mHTBleDevices = new ArrayList<>();
    private LinkedList<HTBleData> mHtblData = new LinkedList<>();




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

        constants               = new Constants();
        mTimer                  = new Timer();

        //  ------------- Wired OBD ----------
        this.connection = new RemoteServiceConnection();
        this.replyTo = new Messenger(new IncomingHandler());
        BindConnection();

        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
           initBleListener();
            checkPermissionsBeforeScanBle();
        }else{
            checkWiredObdConnection();
        }


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
            }catch (Exception e){
                e.printStackTrace();
            }

            // ---------------- temp data ---------------------
            //  ignitionStatus = "ON"; truckRPM = "35436"; speed = 10;

            checkObdDataWithRule(speed);
        }
    }



    private void CallWired(long time){
        if (SharedPref.getUserName(getApplicationContext()).equals("") && SharedPref.getPassword(getApplicationContext()).equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isWiredCallBackCalled = true;
                    StartStopServer(constants.WiredOBD);
                }
            }, time);
        }
    }

    private void checkWiredObdConnection(){

        ShellUtils.CommandResult obdShell = ShellUtils.execCommand("cat /sys/class/power_supply/usb/type", false);

        if (obdShell.result == 0) {
            if (obdShell.successMsg.contains("USB_DCP")) {
                // Connected State
                SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, "", getApplicationContext());
            } else {
                // Disconnected State. Save only when last status was not already disconnected
                SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, "", getApplicationContext());
            }
        } else {
            // Error
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



    private void checkObdDataWithRule(int speed){
        try {

            if (SharedPref.getUserName(getApplicationContext()).equals("") &&
                    SharedPref.getPassword(getApplicationContext()).equals("")) {

                if(SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED ||
                        SharedPref.getObdStatus(getApplicationContext()) == Constants.BLE_CONNECTED) {

                    if (ignitionStatus.equals("ON") && !truckRPM.equals("0")) {
                        SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, "", getApplicationContext());
                        if (speed > 8 && lastVehSpeed > 8) {
                           // SharedPref.setLoginAllowedStatus(false, getApplicationContext());

                            long count = SharedPref.getLastCalledWiredCallBack(getApplicationContext());
                            if (count == 0) {
                                SharedPref.setLastCalledWiredCallBack(count, getApplicationContext());
                                Globally.PlaySound(getApplicationContext());

                                if(UILApplication.isActivityVisible()){
                                    signInAlertDialog();
                                }else {
                                   // Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD", AlertMsg, 2003);

                                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD", "Vehicle Speed: " +speed + " "
                                            + AlertMsg, 2003);

                                    SpeakOutMsg(AlertMsgSpeech);
                                }

                            }

                            if (count >= TIME_INTERVAL_LIMIT) {
                                // reset call back count
                                count = 0;
                            }

                            // save count --------------
                            SharedPref.setLastCalledWiredCallBack(count + TIME_INTERVAL_WIRED, getApplicationContext());

                        } else {
                            SharedPref.setLoginAllowedStatus(true, getApplicationContext());
                        }
                        lastVehSpeed = speed;

                        // ping wired server to get data
                        if( SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED &&
                                SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED) {
                            CallWired(TIME_INTERVAL_WIRED);
                        }

                    } else {
                        lastVehSpeed = -1;
                        SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                        if( SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED ) {
                            if (SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_CONNECTED ||
                                    SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED) {
                                SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, "", getApplicationContext());
                                CallWired(TIME_INTERVAL_WIFI);
                            }
                        }
                    }
                }else{
                    lastVehSpeed = -1;
                    SharedPref.setLoginAllowedStatus(true, getApplicationContext());

                    if( SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED ) {
                        SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, "", getApplicationContext());
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



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("service", "---------onStartCommand Service");

        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
            initHtBle();
        }else if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
            StartStopServer(constants.WiredOBD);
        }else{
            checkWifiOBDConnection();
        }



        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
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
                if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                    checkPermissionsBeforeScanBle();
                }else if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_WIRED) {
                    if(SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED ){
                        StartStopServer(constants.WiredOBD);
                    }else{
                        if(isWiredCallBackCalled == false){
                            StartStopServer(constants.WiredOBD);
                        }
                    }
                }else{
                    checkWifiOBDConnection();
                }

            }

        }
    };


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
                                    SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, "", getApplicationContext());


                                        if (WheelBasedVehicleSpeed > 8 && lastVehSpeed > 8) {
                                            long count = SharedPref.getLastCalledWiredCallBack(getApplicationContext());
                                            if (count == 0) {
                                                //  SharedPref.setLoginAllowedStatus(false, getApplicationContext());
                                                SharedPref.setLastCalledWiredCallBack(count, getApplicationContext());

                                                Globally.PlaySound(getApplicationContext());

                                                if (UILApplication.isActivityVisible()) {
                                                    signInAlertDialog();
                                                } else {
                                                    Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD",
                                                            "Vehicle Speed: " + (int) WheelBasedVehicleSpeed + " " + AlertMsg, 2003);
                                                    SpeakOutMsg(AlertMsgSpeech);
                                                }
                                            }

                                            if (count >= TIME_INTERVAL_LIMIT) {
                                                // reset call back count
                                                count = 0;
                                            }

                                            // save count --------------
                                            SharedPref.setLastCalledWiredCallBack(count + TIME_INTERVAL_WIRED, getApplicationContext());


                                            //  Globally.ShowLogoutNotificationWithSound(getApplicationContext(), "ELD", AlertMsg, mNotificationManager);
                                        } else {
                                            SharedPref.setLoginAllowedStatus(true, getApplicationContext());
                                        }


                                    lastVehSpeed = WheelBasedVehicleSpeed;


                                } else {
                                    if(SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_CONNECTED) {
                                        SharedPref.setLoginAllowedStatus(true, getApplicationContext());
                                        SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED,  "", getApplicationContext());
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





    private void signInAlertDialog(){

        final Dialog picker = new Dialog(getApplicationContext());
        picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.dialog_signin_vehicle);

        final Button signInOkBtn = (Button)picker.findViewById(R.id.signInOkBtn);
        signInOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.dismiss();
            }
        });

        if(getApplicationContext() != null) {
            picker.show();
        }

    }




    private void checkPermissionsBeforeScanBle() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isConnected = HTBleSdk.Companion.getInstance().isConnected(HTModeSP.INSTANCE.getDeviceMac());

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }else{
            if (constants.CheckGpsStatusToCheckMalfunction(getApplicationContext())) {

                if (!mIsScanning && !isConnected) {

                    // ignore scan after 3 attempts if device not found
                    if(bleScanCount < 3) {
                        StartScanHtBle();
                    }else{
                        if(bleScanCount == 3) {
                            bleScanCount++;
                            HTBleSdk.Companion.getInstance().stopHTBleScan();
                        }
                    }


                }

            }
        }

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

            }

            @Override
            public void onConnectTimeout(@org.jetbrains.annotations.Nullable String s) {
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_TIMEOUT, s));
            }

            @Override
            public void onConnectionError(@NotNull String s, int i, int i1) {
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_ERROR, s));
            }

            @Override
            public void onDisconnected(@org.jetbrains.annotations.Nullable String s) {
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, s));

            }

            @Override
            public void onReceive(@NotNull String address, @NotNull String uuid, @NotNull HTBleData htBleData) {
                mHtblData.add(htBleData);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));

                int VehicleSpeed = Integer.valueOf(htBleData.getVehicleSpeed());
                truckRPM = htBleData.getEngineSpeed();

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

            if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
                //  ------------- BLE OBD ----------
                if(HTBleSdk.Companion.getInstance().isConnected(HTModeSP.INSTANCE.getDeviceMac())) {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, HTModeSP.INSTANCE.getDeviceMac()));
                    HTBleSdk.Companion.getInstance().disAllConnect();
                }
            }else {
                //  ------------- Wired OBD ----------
                if(isBound){
                    StartStopServer("stop");
                    this.unbindService(connection);
                    isBound = false;
                }
            }

            super.onDestroy();

        }



    }




}
