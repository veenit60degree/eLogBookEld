package com.background.service;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ble.util.BleUtil;
import com.ble.util.ConstantEvent;
import com.ble.util.EventBusInfo;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.Utils;
import com.htstart.htsdk.HTBleSdk;
import com.htstart.htsdk.bluetooth.HTBleData;
import com.htstart.htsdk.bluetooth.HTBleDevice;
import com.htstart.htsdk.minterface.HTBleScanListener;
import com.htstart.htsdk.minterface.IReceiveListener;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BleDataService extends Service {


    // Bluetooth obd adapter decleration
    boolean mIsScanning = false;
    public static boolean IsScanClick = false;
    public static boolean isBleConnected = false;
    int bleScanCount = 0, sameDevicePairingAttempts = 0;
    private BluetoothAdapter mBTAdapter;
    private ArrayList<HTBleDevice> mHTBleDevices = new ArrayList<>();

    public static boolean isConnected = false;
    private static final long OBD_TIME_LOCATION_UPDATES = 10 * 1000;   // 10 sec

    private Handler bleHandler = new Handler();
    Constants constants;
    String TAG_OBD = "BleService";
    Utils obdUtil;
    DBHelper dbHelper;
    DriverPermissionMethod driverPermissionMethod;
    String DriverId = "";

    /**
     * Please note that Bluetooth will also detect whether there is a connected device during initialization. If so, it will automatically connect to the Bluetooth device after the initial completion. If automatic connection after initialization is not required, please actively disconnect isAllConnected() or disconnect when the program exits ()
     * If you want to connect and switch to other devices, you need to disconnect isAllConnected() or disconnect() first, and then connect again. The current sdk does not support the function of connecting multiple devices at the same time.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("Service", "---------onCreate BleService");

        constants = new Constants();
        driverPermissionMethod = new DriverPermissionMethod();
        dbHelper = new DBHelper(getApplicationContext());
        DriverId        = SharedPref.getDriverId(getApplicationContext());

        try{
            //  ------------- OBD Log write initilization----------
            obdUtil = new Utils(getApplicationContext());
            obdUtil.createLogFile();
        }catch (Exception e){
            e.printStackTrace();
        }

        bleHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                try {
                    String macAddress = HTBleSdk.Companion.getInstance().getAddress();
                    if(macAddress != null && macAddress.length() > 5){

                        if (mHTBleDevices.size() > 0 && sameDevicePairingAttempts < 4){
                            sameDevicePairingAttempts ++;
                             //Log.e(TAG_OBD, "ble reBleConnect");
                            mIsScanning = false;
                            HTBleSdk.Companion.getInstance().reBleConnect();

                            constants.saveBleLog("reBleConnect",
                                    Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                                    driverPermissionMethod, obdUtil);

                        }else{

                            if( mIsScanning){
                                mIsScanning = false;
                                HTBleSdk.Companion.getInstance().stopHTBleScan();
                            }

                            // clear cache before scan again
                            if (HTBleSdk.Companion.getInstance().isAllConnected()) {
                                //Log.e(TAG_OBD, "Ble Clear Cache before scannnn");
                                HTBleSdk.Companion.getInstance().disAllConnect();

                                constants.saveBleLog("disAllConnect before StartScanHtBle",
                                        Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                                        driverPermissionMethod, obdUtil);

                            }

                            StartScanHtBle();   //Device connection after scanning is turned on
                        }

                    }else {
                        if (HTBleSdk.Companion.getInstance().isAllConnected()) {
                            //Log.e(TAG_OBD, "Ble Clear Cache before scan");
                            HTBleSdk.Companion.getInstance().disAllConnect();

                            constants.saveBleLog("disAllConnect before StartScanHtBle--",
                                    Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                                    driverPermissionMethod, obdUtil);

                        }
                        StartScanHtBle();   //Device connection after scanning is turned on
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };


        registerReceiver(broadcastReceiver, makeFilter());

    }

    /**
     * 启动服务不正确，先后启动了两次服务，onStartCommand被连续回调了两次
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (TabAct.IsAppRestart) {
                Globally.BLE_NAME =  "";
                TabAct.IsAppRestart = false;
                try {
                    mHTBleDevices.clear();
                    HTBleSdk.Companion.getInstance().disAllConnect();
                }catch (Exception e){}

                stopSelf();

            } else if(TabAct.SelectDevice){

                TabAct.SelectDevice = false;
                boolean isHtBleConnected = isConnected && HTBleSdk.Companion.getInstance().isConnected();
                if(!isHtBleConnected) {
                    for (int i = 0; i < mHTBleDevices.size(); i++) {
                        if (mHTBleDevices.get(i).getName().equals(TabAct.SelectDeviceName)) {
                            connectHtBle(mHTBleDevices.get(i));
                            sendBroadCast(getString(R.string.ht_connecting), "");
                            break;
                        }
                    }
                }
            }else {
                boolean isHtBleConnected = isConnected && HTBleSdk.Companion.getInstance().isConnected();
                if (!isHtBleConnected) {
                    checkPermissionsBeforeScanBle();
                } else {
                    if (IsScanClick) {
                        IsScanClick = false;
                        if(isHtBleConnected){
                            sendBroadCast(getString(R.string.ht_connected), "");
                        }else {
                            sendBroadCast(getString(R.string.ht_connecting), "");
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }




        return START_STICKY;
    }

    /**
     * Start connecting to a bluetooth device
     */
    private void checkPermissionsBeforeScanBle() {

        if (!constants.isFastClick())//prevent consecutive calls
            return;

        initHtBle();

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                if (!bluetoothAdapter.isEnabled()) {
                    //  bluetoothAdapter.enable();//Implicitly try to force the bluetooth switch on
                    sendBroadCast("BlueTooth is disabled. Turn on it..", "");
                    return;
                }

                if (HTBleSdk.Companion.getInstance().isConnected()) { //device connected
                    //Log.e(TAG_OBD, "Ble The Bluetooth connection is successful, and the subsequent command operation will be performed after a delay of 3 seconds.");
                    //Log.e(TAG_OBD, "Ble initBleListener 00" );
                    initBleListener("checkPermissionBeforeScan isConnected");//Register data listener callback
                    return;
                }
                if (!TextUtils.isEmpty(HTBleSdk.Companion.getInstance().getAddress())) {//Determine whether there is an existing connected bluetooth device in the cache, no need to scan the connection again, the sdk will automatically connect
                    //Log.e(TAG_OBD, "Ble Bluetooth is in the process of connecting. After the connection is successful, it needs to delay for 3 seconds before performing subsequent command operations.");
                    //Log.e(TAG_OBD, "Ble initBleListener 11" );
                    initBleListener("checkPermissionBeforeScan getAddress");//Register data listener callback

                    return;
                }

                if (constants.CheckGpsStatusToCheckMalfunction(getApplicationContext())) {  //Check if positioning is available, mainly used for bluetooth scanning)
                    //Device connection after scanning is turned on
                    Message message = bleHandler.obtainMessage();
                    message.sendToTarget();
                }

            }
        }catch (Exception e){}
    }

    /**
     * Check if the current location is available
     *
     * @param context
     * @return
     */
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


    /**
     * start***************************************Repeat here to check if bluetooth is available
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initHtBle() {
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

       /* if (!mBTAdapter.isEnabled()) {
            mBTAdapter.enable();
        }*/

    }

    public boolean isBleEnabled() {
        boolean isBleEnabled = false;
        try {
            BluetoothManager manager = BleUtil.getManager(getApplicationContext());
            if (manager != null) {
                mBTAdapter = manager.getAdapter();
                isBleEnabled = mBTAdapter.isEnabled();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isBleEnabled;
    }
    /**end***************************************Repeat here to check if bluetooth is available*/

    /**
     * Register bluetooth device data callback
     */
    void initBleListener(String type) {

        constants.saveBleLog("initBleListener - " +type,
                Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                driverPermissionMethod, obdUtil);

        HTBleSdk.Companion.getInstance().registerCallBack(new IReceiveListener() {
            @Override
            public void onConnected(@Nullable String s) {
                Log.e("TAG", "Ble onConnected==" + s);
                //  Log.e("getAddress-onConnected", "getAddress: " +s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_CONNECTED, s));

                sameDevicePairingAttempts = 0;

                isConnected = true;
                if(IsScanClick) {
                    IsScanClick = false;
                    sendBroadCast(getString(R.string.ht_connecting), "");
                }
                sendEcmBroadcast(true, "Connected");

                constants.saveBleLog("onConnected", Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                        driverPermissionMethod, obdUtil);

            }

            @Override
            public void onConnectTimeout(@Nullable String s) {
                Log.e("TAG", "onConnectTimeout==" + s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_TIMEOUT, s));
                //Log.e("getAddress-Timeout", "getAddress: " +s);

                sendEcmBroadcast(false, "onConnectTimeout");
                isConnected = false;
                isBleConnected = false;

                constants.saveBleLog("onConnectTimeout", Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                        driverPermissionMethod, obdUtil);

            }

            @Override
            public void onConnectionError(@NonNull String s, int i, int i1) {
                Log.e("TAG", "onConnectionError==" + s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_ERROR, s));
                sendBroadCast(getString(R.string.ht_connect_error), "");
                sendEcmBroadcast(false, "onConnectionError");
                isConnected = false;
                isBleConnected = false;

                constants.saveBleLog("onConnectionError", Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                        driverPermissionMethod, obdUtil);


            }

            @Override
            public void onDisconnected(@Nullable String address) {
                Log.e("TAG", "onDisconnected==" + address);

                try {
                    isConnected = false;
                    isBleConnected = false;

                    HTBleSdk.Companion.getInstance().unRegisterCallBack();

                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, address));

                    sendBroadCast(getString(R.string.ht_disconnected), "");
                    sendEcmBroadcast(false, "Disconnected");

                    Thread.sleep(Constants.SocketTimeout800ms);
                }catch (Exception e){
                    e.printStackTrace();
                }

                constants.saveBleLog("onDisconnected", Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                        driverPermissionMethod, obdUtil);



            }

            @Override
            public void onReceive(@NotNull String address, @NotNull String uuid, @NotNull HTBleData htBleData) {
                Log.e("TAG", "onReceive==" + htBleData);
                //  Log.e("htBleData", "htBleData: " + htBleData);

                //  EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
                if (htBleData.getEventType() == 0 && htBleData.getEventCode() == 1) {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
                } else {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_QUERY_DATA_AVAILABLE, address, uuid, htBleData));
                }

                if (htBleData.getEventData() != "OnTime") {
                    isConnected = true;
                    isBleConnected = true;
                    sendEcmBroadcast(true, BleUtil.decodedData(htBleData, address));
                    sendBroadCastDecodedData(BleUtil.decodeDataChange(htBleData, address), "");
                }

            }


            @Override
            public void onResponse(@NotNull String address, @NotNull String uuid, @NotNull String sequenceID, @NotNull int status) {
//                Log.e("TAG", "onResponse" + status + "==" + address);
                bleScanCount = 0;
                //Log.e("getAddress-onResponse", "getAddress: " +address);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_RESPONSE, address, uuid, status));
                sendBroadCast(getString(R.string.ht_ble_response1), "");
            }
        });
    }


    private void sendBroadCastDecodedData(String data, String rawMsg){
        try {
            //   if(SharedPref.isOBDScreen(getApplicationContext())) {
            Intent intent = new Intent("ble_changed_data");
            intent.putExtra("decoded_data", data);
            intent.putExtra("raw_message", rawMsg);

            LocalBroadcastManager.getInstance(BleDataService.this).sendBroadcast(intent);
            //  }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Turn on Bluetooth scanning for devices
     */
    public void StartScanHtBle(){

        try {
            if(!mIsScanning) {
                //Log.e(TAG_OBD, "ble start scanning");
                HTBleSdk.Companion.getInstance().startHTBleScan(new HTBleScanListener() {
                    @Override
                    public void onScanStart() {
                        //Log.e(TAG_OBD, "Ble onScanStart");
                        mHTBleDevices.clear();

                        bleScanCount++;
                        mIsScanning = true;
                        sendBroadCast(getString(R.string.Scanning), "");

                        constants.saveBleLog("HTBleScanListener - startHTBleScan",
                                Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                                driverPermissionMethod, obdUtil);


                    }

                    @Override
                    public void onScanning(@org.jetbrains.annotations.Nullable HTBleDevice htBleDevice) {
                        //Log.e(TAG_OBD, "Ble onScanning Name: " +htBleDevice.getName());

                       /* if (mHTBleDevices.contains(htBleDevice))
                            return;
                        mIsScanning = false;
                        mHTBleDevices.add(htBleDevice);
                        connectHtBle(htBleDevice);
*/
                        boolean iAlreadyAvailable = false;
                        for(int i = 0; i < mHTBleDevices.size() ; i++){
                            if(mHTBleDevices.get(i).getName().trim().equals(htBleDevice.getName().trim())){
                                iAlreadyAvailable = true;
                                break;
                            }
                        }
                        if(!iAlreadyAvailable){
                            mHTBleDevices.add(htBleDevice);
                        }


                    }

                    @Override
                    public void onScanFailed(int i) {
                        //Log.e(TAG_OBD, "Ble onScanFailed");
                        mIsScanning = false;
                        sendBroadCast(getString(R.string.ht_scan_error), "");

                        constants.saveBleLog("HTBleScanListener - onScanFailed - ",
                                Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                                driverPermissionMethod, obdUtil);

                    }

                    @Override
                    public void onScanStop() {
                        //Log.e(TAG_OBD, "Ble onScanStop");
                        mIsScanning = false;
                        SharedPref.saveBleScanCount(bleScanCount, getApplicationContext());
                        String deviceInfo = "";

                        if (HTBleSdk.Companion.getInstance().isConnected()) {   //HTModeSP.INSTANCE.getDeviceMac()
                            sendBroadCast(getString(R.string.ht_scan_completed), "");

                            deviceInfo = "Connected";

                        } else {

                            boolean isLogout =  SharedPref.getUserName(getApplicationContext()).equals("") &&
                                    SharedPref.getPassword(getApplicationContext()).equals("");

                            if(mHTBleDevices.size() == 1){
                                connectHtBle(mHTBleDevices.get(0));

                                if(isLogout){
                                    sendDeviceCastAtLogin("");
                                }else {
                                    sendDeviceCast("");
                                }

                                deviceInfo = "Single device: " + mHTBleDevices.get(0).getName();
                            }else if(mHTBleDevices.size() > 1){

                                String availableDevices = "";
                                for(int i = 0 ; i < mHTBleDevices.size() ; i++){
                                    if(i == 0){
                                        availableDevices = mHTBleDevices.get(i).getName() ;
                                    }else{
                                        availableDevices += "@@@"+ mHTBleDevices.get(i).getName();
                                    }
                                }

                                if (isLogout) {
                                    // Logout service call
                                    sendDeviceCastAtLogin(availableDevices);

                                    deviceInfo = "Multiple devices at logout: " + availableDevices;
                                }else{
                                    // Login service (BackgroundLocationService)
                                    sendDeviceCast(availableDevices);
                                    deviceInfo = "Multiple devices at Login: " + availableDevices;

                                }
                            }else {
                                sendBroadCast(getString(R.string.ht_scan_completed_not_found), "");

                                deviceInfo = "Device not found";

                            }

                        }

                        constants.saveBleLog("HTBleScanListener - onScanStop - Scan Complete, " + deviceInfo ,
                                Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                                driverPermissionMethod, obdUtil);


                    }
                });
            }else{
                //Log.e(TAG_OBD, "ble StartScanHtBle else");
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * Bluetooth device connection
     *
     * @param htBleDevice
     */
    private void connectHtBle(final HTBleDevice htBleDevice) {
        //Log.e(TAG_OBD, "ble connectHtBle: " + htBleDevice.getAddress());
        HTBleSdk.Companion.getInstance().stopHTBleScan();
        if (HTBleSdk.Companion.getInstance().isAllConnected()) {
            //Log.e(TAG_OBD, "Ble initBleListener 33" );
            sendBroadCast(getString(R.string.ht_connect_error_other), "");
            initBleListener("Device connecttion failed");//Register data listener callback
        } else {
            //Log.e(TAG_OBD, "Ble initBleListener 44" );
            sendBroadCast(getString(R.string.ht_connected), "");
            HTBleSdk.Companion.getInstance().connect(htBleDevice);
            initBleListener("Device connected");//Register data listener callback
        }
    }


    private void sendBroadCast(String data, String rawMsg) {
        try {
            Intent intent = new Intent("ble_changed_data");
            intent.putExtra("decoded_data", data);
            intent.putExtra("raw_message", rawMsg);
            LocalBroadcastManager.getInstance(BleDataService.this).sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendDeviceCast(String BleDevices){
        try{
            Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
            intent.putExtra(ConstantsKeys.SuggestedEdit, false);
            intent.putExtra(ConstantsKeys.IsCycleRequest, false);
            intent.putExtra(ConstantsKeys.IsELDNotification, false);
            intent.putExtra(ConstantsKeys.DriverELDNotificationList, false);
            intent.putExtra(ConstantsKeys.BleDevices, BleDevices);



            LocalBroadcastManager.getInstance(BleDataService.this).sendBroadcast(intent);

        }catch (Exception e){}
    }


    private void sendDeviceCastAtLogin(String BleDevices){
        try{
            Intent intent = new Intent(ConstantsKeys.IsEventUpdate);
            intent.putExtra(ConstantsKeys.IsEventUpdate, false);
            intent.putExtra(ConstantsKeys.Status, false);
            intent.putExtra(ConstantsKeys.IsEldEcmALert, "false");
            intent.putExtra(ConstantsKeys.BleDevices, BleDevices);

            LocalBroadcastManager.getInstance(BleDataService.this).sendBroadcast(intent);

        }catch (Exception e){}
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }


    void sendEcmBroadcast(boolean IsConnected, String data){
        try{
            Intent intent = new Intent(ConstantsKeys.BleDataService);
            intent.putExtra(ConstantsKeys.IsConnected, IsConnected);
            intent.putExtra(ConstantsKeys.Data, data);
            LocalBroadcastManager.getInstance(BleDataService.this).sendBroadcast(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * Register for Bluetooth switch status monitoring
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                //String action = intent.getAction();
                // if(BluetoothAdapter.ACTION_STATE_CHANGED != null && BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                if(intent != null) {
                    if (intent.hasExtra(BluetoothAdapter.EXTRA_STATE)) {
                        switch (intent.getAction()) {
                            case BluetoothAdapter.ACTION_STATE_CHANGED:
                                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                                switch (blueState) {
                                    case BluetoothAdapter.STATE_TURNING_ON://bluetooth is on

                                        break;

                                    case BluetoothAdapter.STATE_ON://bluetooth is on
                                        //It is detected that the bluetooth switch is turned on to reconnect
                                        if (!TextUtils.isEmpty(HTBleSdk.Companion.getInstance().getAddress())) {
                                            Log.e("TAG", "Ble switch on");
                                            //Log.e(TAG_OBD, "Ble initBleListener 22" );
                                            initBleListener("Ble STATE_ON");
                                        }
                                        break;
                                    case BluetoothAdapter.STATE_TURNING_OFF://bluetooth is turning off

                                        constants.saveBleLog("Ble STATE_TURNING_OFF",
                                                Globally.GetCurrentDateTime(), getApplicationContext(), dbHelper,
                                                driverPermissionMethod, obdUtil);

                                        sendDeviceCast(null);

                                        break;

                                    case BluetoothAdapter.STATE_OFF://bluetooth is off
                                        break;
                                }

                        }
                    }

                }
            }catch (RuntimeException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };



    @Override
    public void onDestroy() {

        unregisterReceiver(broadcastReceiver);
        if (mIsScanning)
            HTBleSdk.Companion.getInstance().stopHTBleScan();
        if (HTBleSdk.Companion.getInstance().isAllConnected()) {
            //Log.e(TAG_OBD, "Ble Clear Cache onDestroy");
            HTBleSdk.Companion.getInstance().disAllConnect();
        }
        HTBleSdk.Companion.getInstance().unRegisterCallBack();//Remove data callback listener
        mHTBleDevices.clear();

        super.onDestroy();

    }




}
