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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ble.comm.Observer;
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
import com.clj.fastble.utils.HexUtil;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.ShellUtils;
import com.constants.TcpClient;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.SuggestedFragmentActivity;
import com.messaging.logistic.UILApplication;
import com.messaging.logistic.fragment.EldFragment;
import com.notifications.NotificationManagerSmart;
import com.wifi.settings.WiFiConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import dal.tables.OBDDeviceData;
import obdDecoder.Decoder;


public class AfterLogoutService extends Service implements TextToSpeech.OnInitListener, Observer {

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
    boolean isManualDisconnected = false;
    boolean isEldBleFound = false;

    int writeFailureCount = 0;
    int isScanningCount = 0;
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
            bleInit();
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
               // Log.d("time", "timeStamp: " + bundle.getString(constants.OBD_TimeStamp));
            }catch (Exception e){
                e.printStackTrace();
            }

            // ---------------- temp data ---------------------CalculateCycleTime
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
        }
    }



    private void checkObdDataWithRule(int speed){
        try {
            if(SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED ||
                    SharedPref.getObdStatus(getApplicationContext()) == Constants.BLE_CONNECTED) {

                if (ignitionStatus.equals("ON") && !truckRPM.equals("0")) {
                    SharedPref.SaveObdStatus(Constants.WIRED_CONNECTED, "", getApplicationContext());
                    if (speed > 8 && lastVehSpeed > 8) {
                        SharedPref.setLoginAllowedStatus(false, getApplicationContext());

                        long count = SharedPref.getLastCalledWiredCallBack(getApplicationContext());
                        if (count == 0) {
                            SharedPref.setLastCalledWiredCallBack(count, getApplicationContext());
                            Globally.PlaySound(getApplicationContext());

                            if(UILApplication.isActivityVisible()){
                                signInAlertDialog();
                            }else {
                                Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD", AlertMsg, 2003);
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("service", "---------onStartCommand Service");

        if(SharedPref.getObdPreference(getApplicationContext()) == Constants.OBD_PREF_BLE) {
            bleInit();
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


                                double GPSSpeed = Integer.valueOf(wifiConfig.checkJsonParameter(canObj, "GPSSpeed", "0"));
                                WheelBasedVehicleSpeed = Double.parseDouble(wifiConfig.checkJsonParameter(canObj, "WheelBasedVehicleSpeed", "0"));

                                if (WheelBasedVehicleSpeed == 0) {
                                    WheelBasedVehicleSpeed = GPSSpeed;
                                }

                                truckRPM = wifiConfig.checkJsonParameter(canObj, "RPMEngineSpeed", "0");
                                ignitionStatus = wifiConfig.checkJsonParameter(canObj, "EngineRunning", "false");

                                if (ignitionStatus.equals("true")) {
                                    SharedPref.SaveObdStatus(Constants.WIFI_CONNECTED, "", getApplicationContext());
                                    if(WheelBasedVehicleSpeed > 8 && lastVehSpeed > 8){
                                        SharedPref.setLoginAllowedStatus(false, getApplicationContext());
                                        Globally.PlaySound(getApplicationContext());

                                        if(UILApplication.isActivityVisible()){
                                            signInAlertDialog();
                                        }else {
                                            Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD", AlertMsg, 2003);
                                            SpeakOutMsg(AlertMsgSpeech);
                                        }
                                        //  Globally.ShowLogoutNotificationWithSound(getApplicationContext(), "ELD", AlertMsg, mNotificationManager);
                                    }else{
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




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void bleInit() {
        try {
            // BLE check
            if (!BleUtil.isBLESupported(this)) {
                return;
            }

            // BT check
            BluetoothManager manager = BleUtil.getManager(this);
            if (manager != null) {
                mBTAdapter = manager.getAdapter();

            }
            if (mBTAdapter == null) {
                return;
            } else {
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

        }catch (Exception e){
            e.printStackTrace();
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
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                try {
                    Thread.sleep(4000);
                    BleManager.getInstance().disconnectAllDevice();
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                if (constants.CheckGpsStatusToCheckMalfunction(getApplicationContext())) {

                    if (!mIsScanning && !isConnected) {
                        if(SharedPref.getBleScanCount(getApplicationContext()) < 5) {
                            startScan();
                        }/*else{
                            if(bleScanCount == 5) {
                                bleScanCount++;
                                Globally.PlayNotificationSound(getApplicationContext());
                                Globally.ShowLocalNotification(getApplicationContext(),
                                        getApplicationContext().getResources().getString(R.string.BluetoothOBD),
                                        getApplicationContext().getResources().getString(R.string.BleObdNotFound), 2096);
                            }
                        }*/
                    }

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

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void startScan() {
        isScanningCount = 0;
        isEldBleFound = false;

        BleManager.getInstance().scan(new BleScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onScanStarted(boolean success) {
                if(success) {
                    mIsScanning = true;
                    isBleObdRespond = false;
                }else{
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

                try{
                    if(bleDevice.getName() != null) {
                        if (bleDevice.getName().contains("ALSELD") || bleDevice.getName().contains("SMBLE")) {
                            Log.d("getName", "getName: " + bleDevice.getName());

                            isEldBleFound = true;
                            bleScanCount = 0;
                            SharedPref.saveBleScanCount(0, getApplicationContext());

                            connect(bleDevice);
                            BleManager.getInstance().cancelScan();
                        }
                    }else{
                        if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {
                            SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, Globally.GetCurrentDateTime(), getApplicationContext());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                mIsScanning = false;

                if (!isEldBleFound) {
                    bleScanCount++;
                    SharedPref.saveBleScanCount(bleScanCount, getApplicationContext());
                }

            }
        });
    }



    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.d(TAG_BLE_CONNECT, "onStartConnect");
                isBleObdRespond = false;
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.d(TAG_BLE_CONNECT, "onConnectFail");

                isBleObdRespond = false;
                ObserverManager.getInstance().notifyObserver(bleDevice);

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onConnectSuccess(BleDevice blueToothDevice, BluetoothGatt gatt, int status) {
                Log.d(TAG_BLE_CONNECT, "Connected Successfully");
                isBleObdRespond = false;
                if (BleManager.getInstance().isConnected(bleDevice)) {

                    isManualDisconnected = false;
                    setDisconnectType(isManualDisconnected);

                    setBleDevice(blueToothDevice);
                    addObserver(blueToothDevice);
                    name = bleDevice.getName();
                    mac  = bleDevice.getMac();

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        gatt.requestMtu(512);
//                    }

                    bluetoothGattServices = new ArrayList<>();
                    for (BluetoothGattService service : gatt.getServices()) {
                        bluetoothGattServices.add(service);
                    }

                    if(bluetoothGattServices.size() > 2) {
                        SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, Globally.GetCurrentDateTime(), getApplicationContext());

                        BluetoothGattService service = bluetoothGattServices.get(2);
                        setBluetoothGattService(service);
                        writeData();
                    }

                }

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {

                Log.d(TAG_BLE_CONNECT, "onDisConnected");
                isBleObdRespond = false;
                ObserverManager.getInstance().notifyObserver(bleDevice);
                stopService(bleDevice,getCharacteristic());

                if (!isManualDisconnected()) {
                    bleInit();
                   // setScanRule();
                    startScan();
                }

                if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {
                    SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, Globally.GetCurrentDateTime(), getApplicationContext());
                }


            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void getCharacteristicListData() {
        BluetoothGattService service = getBluetoothGattService();
        characteristicList = new ArrayList<>();
        if(service != null) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                characteristicList.add(characteristic);
            }
        }
    }

    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(int maxlength, int PROPERTY){
        BluetoothGattCharacteristic characteristic = null;
        if(characteristicList.size() > maxlength) {
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
            final String requestData = "request:{source_id:" + uuidName + ",events:[{5B6A,0,0,000000,000000,000000000000,1111,0,0,0,0,,0,0,0,0,0,0,0,57,79}]}";    //{5B6A,0,0,000000,000000,000000000000,1111,0,0,,0,0,0,0,0,0,0,57,79}]

            writeValue = BleUtil.convertStringToHex(requestData);
            writeValue = writeValue.replaceAll(" ", "");
            byte[] bytes = BleUtil.invertStringToBytes(writeValue);

            BleManager.getInstance().write(
                    bleDevice,
                    SERVICE_UUID/*characteristic.getService().getUuid().toString()*/,
                    CHARACTER_WRITE_UUID/*characteristic.getUuid().toString()*/,
                    bytes, // by,
                    new BleWriteCallback() {

                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                            Log.d(TAG_BLE_OPERATION, "onWriteSuccess");
                            // read ble data after write success
                            if (current == total) {
                                readData();
                            }

                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {
                            Log.d(TAG_BLE_OPERATION, "onWriteFailure");
                            mIsScanning = false;
                            isBleObdRespond = false;

                            ObserverManager.getInstance().notifyObserver(bleDevice);
                            stopService(bleDevice,getCharacteristic());
                            writeFailureCount++;




                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bleInit();
                                }
                            }, 1500);
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
                    SERVICE_UUID/*characteristic.getService().getUuid().toString()*/,
                    CHARACTER_NOTIFY_UUID/*characteristic.getUuid().toString()*/,
                    new BleReadCallback() {

                        @Override
                        public void onReadSuccess(final byte[] data) {
                            Log.d(TAG_BLE_OPERATION, "onReadSuccess");
                            isBleObdRespond = false;
                            notifyData();
                        }

                        @Override
                        public void onReadFailure(final BleException exception) {
                            Log.d(TAG_BLE_OPERATION, "onReadFailure");
                            mIsScanning = false;
                            isBleObdRespond = false;
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
                    SERVICE_UUID/*characteristic.getService().getUuid().toString()*/,
                    CHARACTER_NOTIFY_UUID/*characteristic.getUuid().toString()*/,
                    false,
                    new BleNotifyCallback() {

                        @Override
                        public void onNotifySuccess() {
                            Log.d(TAG_BLE_OPERATION, "onNotifySuccess");
                            isBleObdRespond = false;
                          //  String data = HexUtil.formatHexString(characteristic.getValue(), true);
                         //   Log.d("Notify Success Data", "data: " + data);
                            SharedPref.SaveObdStatus(Constants.BLE_CONNECTED, Globally.GetCurrentDateTime(), getApplicationContext());

                        }

                        @Override
                        public void onNotifyFailure(final BleException exception) {

                            Log.d(TAG_BLE_OPERATION, "onReadFailure: " + exception.toString());
                            mIsScanning = false;
                            isBleObdRespond = false;

                            if (SharedPref.getObdStatus(getApplicationContext()) != Constants.BLE_DISCONNECTED) {
                                SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, Globally.GetCurrentDateTime(), getApplicationContext());
                            }

                        }

                        @Override
                        public void onCharacteristicChanged(byte[] data) {
                            if(characteristic.getValue().length > 50) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                    try {
                                        String[] decodedArray = BleUtil.decodeDataChange(characteristic);
                                        if (decodedArray != null && decodedArray.length >= 10) {
                                            int VehicleSpeed = Integer.valueOf(decodedArray[7]);
                                            truckRPM = decodedArray[8];

                                            if (Integer.valueOf(truckRPM) > 0) {
                                                ignitionStatus = "ON";
                                            } else {
                                                ignitionStatus = "OFF";
                                            }

                                            checkObdDataWithRule(VehicleSpeed);
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
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
           /* String uuid = "";
        String displayName = "";
        String mac = "";
        boolean isAutoConnect = false;*/

        String[] uuids;
        String str_uuid = "";
        if (TextUtils.isEmpty(str_uuid)) {
            uuids = null;
        } else {
            uuids = str_uuid.split(",");
        }
        UUID[] serviceUuids = null;
        if (uuids != null && uuids.length > 0) {
            serviceUuids = new UUID[uuids.length];
            for (int i = 0; i < uuids.length; i++) {
                String name = uuids[i];
                String[] components = name.split("-");
                if (components.length != 5) {
                    serviceUuids[i] = null;
                } else {
                    serviceUuids[i] = UUID.fromString(uuids[i]);
                }
            }
        }

        String[] names;
        String str_name = "";
        if (TextUtils.isEmpty(str_name)) {
            names = null;
        } else {
            names = str_name.split(",");
        }

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // Only scan the equipment of the specified service, optional
                .setDeviceName(true, names)   // Only scan devices with specified broadcast name, optional
                .setDeviceMac(mac)                  // Only scan devices of specified mac, optional
                .setAutoConnect(false)      // AutoConnect parameter when connecting, optional, default false
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
        }
    }

    public boolean isBleConnected(){
        getBleDevice();
        return BleManager.getInstance().isConnected(bleDevice);
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
        if(bleDevice != null){
            if(BleManager.getInstance().isConnected(bleDevice)) {

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

                // stopSelf();
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

    }




}
