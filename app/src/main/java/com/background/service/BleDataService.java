package com.background.service;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ble.util.BleUtil;
import com.ble.util.ConstantEvent;
import com.ble.util.EventBusInfo;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.Utils;
import com.htstart.htsdk.HTBleSdk;
import com.htstart.htsdk.bluetooth.HTBleData;
import com.htstart.htsdk.bluetooth.HTBleDevice;
import com.htstart.htsdk.minterface.HTBleScanListener;
import com.htstart.htsdk.minterface.IReceiveListener;
import com.local.db.BleGpsAppLaunchMethod;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.TabAct;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BleDataService extends Service {


    // Bluetooth obd adapter decleration
    boolean mIsScanning = false;
    boolean isDeviceFound = true;
    public static boolean IsScanClick = false;
    public static boolean isBleConnected = false;
    int bleScanCount = 0, sameDevicePairingAttempts = 0;
    private BluetoothAdapter mBTAdapter;
    private ArrayList<HTBleDevice> mHTBleDevices = new ArrayList<>();

    public static boolean isConnected = false;

    private Handler bleHandler = new Handler();
    Constants constants;
    String TAG_OBD = "BleService";
    Utils obdUtil;
    DBHelper dbHelper;
    DriverPermissionMethod driverPermissionMethod;
    String DriverId = "";
    Globally global;
    BleGpsAppLaunchMethod bleGpsAppLaunchMethod;

    /**
     * Please note that Bluetooth will also detect whether there is a connected device during initialization. If so, it will automatically connect to the Bluetooth device after the initial completion. If automatic connection after initialization is not required, please actively disconnect isAllConnected() or disconnect when the program exits ()
     * If you want to connect and switch to other devices, you need to disconnect isAllConnected() or disconnect() first, and then connect again. The current sdk does not support the function of connecting multiple devices at the same time.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();

        Logger.LogInfo("Service", "---------onCreate BleService");

        constants = new Constants();
        driverPermissionMethod = new DriverPermissionMethod();
        dbHelper = new DBHelper(getApplicationContext());
        DriverId        = SharedPref.getDriverId(getApplicationContext());
        global          = new Globally();
        bleGpsAppLaunchMethod   = new BleGpsAppLaunchMethod();

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
                ClearCacheBeforeConnect();
            }
        };


        registerReceiver(broadcastReceiver, makeFilter());

    }


    private void ClearCacheBeforeConnect(){
        try {
            String macAddress = HTBleSdk.Companion.getInstance().getAddress();
            if(macAddress != null && macAddress.length() > 7){

                if (mHTBleDevices.size() > 0 && sameDevicePairingAttempts < 4){
                    sameDevicePairingAttempts ++;
                    //Logger.LogError(TAG_OBD, "ble reBleConnect");
                    mIsScanning = false;
                    HTBleSdk.Companion.getInstance().reBleConnect();

                    constants.saveBleLog("reBleConnect",
                            Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                            driverPermissionMethod, obdUtil);

                }else{

                    if( mIsScanning){
                        mIsScanning = false;
                        HTBleSdk.Companion.getInstance().stopHTBleScan();
                    }

                    disconnectBeforeScan(macAddress);

                    sameDevicePairingAttempts ++;


                }

            }else {
                disconnectBeforeScan(macAddress);
                sameDevicePairingAttempts ++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    private void disconnectBeforeScan(String macAddress){

        try{
            if(macAddress != null && macAddress.length() > 5) {
                // clearing app cache
                if (sameDevicePairingAttempts > 3 && HTBleSdk.Companion.getInstance().isAllConnected()) {

                    sameDevicePairingAttempts = 0;
                    Constants.deleteCache(getApplicationContext());

                    constants.saveBleLog("disAllConnect before StartScanHtBle--",
                            Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                            driverPermissionMethod, obdUtil);

                }

                if (HTBleSdk.Companion.getInstance() != null) {
                    // Ble Clear Cache before scan
                    HTBleSdk.Companion.getInstance().disConnect(macAddress);
                }
            }
            mHTBleDevices.clear();
        }catch (Exception e){
            e.printStackTrace();
        }

        //Device connection after scanning is turned on
       StartScanHtBle();

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
                    boolean isDeviceFound = false;
                    for (int i = 0; i < mHTBleDevices.size(); i++) {
                        if (mHTBleDevices.get(i).getName().equals(TabAct.SelectDeviceName)) {
                            connectHtBle(mHTBleDevices.get(i));
                            sendBroadCast(getString(R.string.ht_connecting), "");
                            isDeviceFound = true;
                            break;
                        }
                    }

                    if(!isDeviceFound){
                        sendBroadCast(getString(R.string.connection_failed), "");
                        //disconnectBeforeScan("");
                    }
                }
            }else {
                boolean isHtBleConnected = isConnected && HTBleSdk.Companion.getInstance().isConnected();
                if (!isHtBleConnected) {
                   String macAddress = HTBleSdk.Companion.getInstance().getAddress();
                    if (IsScanClick && macAddress != null && macAddress.length() > 7) {
                        ClearCacheBeforeConnect();
                    }else {
                        checkPermissionsBeforeScanBle();
                    }
                    IsScanClick = false;
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
                    //Logger.LogError(TAG_OBD, "Ble The Bluetooth connection is successful, and the subsequent command operation will be performed after a delay of 3 seconds.");
                    //Logger.LogError(TAG_OBD, "Ble initBleListener 00" );
                    initBleListener("checkPermissionBeforeScan isConnected");//Register data listener callback
                    return;
                }
                if (!TextUtils.isEmpty(HTBleSdk.Companion.getInstance().getAddress())) {//Determine whether there is an existing connected bluetooth device in the cache, no need to scan the connection again, the sdk will automatically connect
                    //Logger.LogError(TAG_OBD, "Ble Bluetooth is in the process of connecting. After the connection is successful, it needs to delay for 3 seconds before performing subsequent command operations.");
                    //Logger.LogError(TAG_OBD, "Ble initBleListener 11" );
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

        HTBleSdk.Companion.getInstance().registerCallBack(new IReceiveListener() {
            @Override
            public void onConnected(@Nullable String s) {
                Logger.LogError("TAG", "----onConnected Ble==" + s);
                //  Logger.LogError("getAddress-onConnected", "getAddress: " +s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_CONNECTED, s));

                sameDevicePairingAttempts = 0;

                isConnected = true;
                if(IsScanClick) {
                    IsScanClick = false;
                    sendBroadCast(getString(R.string.ht_connecting), "");
                }
                sendEcmBroadcast(true, "Connected");




            }

            @Override
            public void onConnectTimeout(@Nullable String s) {
                Logger.LogError("TAG", "----onConnectTimeout==" + s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_TIMEOUT, s));
                //Logger.LogError("getAddress-Timeout", "getAddress: " +s);

                sendEcmBroadcast(false, "onConnectTimeout");
                isConnected = false;
                isBleConnected = false;

                constants.saveBleLog("onConnectTimeout", Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                        driverPermissionMethod, obdUtil);

            }

            @Override
            public void onConnectionError(@NonNull String s, int i, int i1) {
                Logger.LogError("TAG", "----onConnectionError==" + s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_ERROR, s));
                sendBroadCast(getString(R.string.ht_connect_error), "");
                sendEcmBroadcast(false, "onConnectionError");
                isConnected = false;
                isBleConnected = false;
                BackgroundLocationService.obdVehicleSpeed = 0;

              /*  constants.saveBleLog("onConnectionError", Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                        driverPermissionMethod, obdUtil);
*/

            }

            @Override
            public void onDisconnected(@Nullable String address) {

                Logger.LogError("TAG", "----onDisconnected==" + address);


                try {
                    if(isConnected){
                        isConnected = false;
                        isBleConnected = false;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Logger.LogError("TAG", "----onDisconnected handler");
                                constants.saveBleLog("onDisconnected", Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                                        driverPermissionMethod, obdUtil);

                                HTBleSdk.Companion.getInstance().unRegisterCallBack();
                                BackgroundLocationService.obdVehicleSpeed = 0;
                                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, address));

                                sendBroadCast(getString(R.string.ht_disconnected), "");
                                sendEcmBroadcast(false, "Disconnected");

                            }
                        }, 800);
                       // Thread.sleep(Constants.SocketTimeout800ms);

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }



            }

            @Override
            public void onReceive(@NotNull String address, @NotNull String uuid, @NotNull HTBleData htBleData) {
                  Logger.LogError("htBleData", "----htBleData: " + htBleData);

                int eventType = htBleData.getEventType();
                int eventCode = htBleData.getEventCode();
                if (eventType == 0 && eventCode == 1) {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
                } else if (eventType == 4 && (eventCode == 2 || eventCode == 1)) {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
                } else {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_QUERY_DATA_AVAILABLE, address, uuid, htBleData));
                }

                if (htBleData.getEventData().equals("OnTime")) {
                    isConnected = true;
                    isBleConnected = true;
                    sendEcmBroadcast(true, BleUtil.decodedData(htBleData, address));
                    sendBroadCastDecodedData(BleUtil.decodeDataChange(htBleData, address), "");
                }

            }


            @Override
            public void onResponse(@NotNull String address, @NotNull String uuid, @NotNull String sequenceID, @NotNull int status) {
//                Logger.LogError("TAG", "onResponse" + status + "==" + address);
                bleScanCount = 0;
                sameDevicePairingAttempts = 0;
                //Logger.LogError("getAddress-onResponse", "getAddress: " +address);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_RESPONSE, address, uuid, status));
                sendBroadCast(getString(R.string.ht_ble_response1), "");
            }
        });


        if(sameDevicePairingAttempts > 3){
            if( mIsScanning){
                mIsScanning = false;
                HTBleSdk.Companion.getInstance().stopHTBleScan();
            }

            disconnectBeforeScan("");
            sameDevicePairingAttempts = 0;
        }
        sameDevicePairingAttempts ++;
    }


    private void sendBroadCastDecodedData(String data, String rawMsg){
        try {
            if(SharedPref.IsDriverLogin(getApplicationContext())) {
                Intent intent = new Intent("ble_changed_data");
                intent.putExtra("decoded_data", data);
                intent.putExtra("raw_message", rawMsg);

                LocalBroadcastManager.getInstance(BleDataService.this).sendBroadcast(intent);
            }
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
                //Logger.LogError(TAG_OBD, "ble start scanning");
                HTBleSdk.Companion.getInstance().startHTBleScan(new HTBleScanListener() {
                    @Override
                    public void onScanStart() {
                        //Logger.LogError(TAG_OBD, "Ble onScanStart");
                        mHTBleDevices.clear();

                        bleScanCount++;
                        mIsScanning = true;
                        sendBroadCast(getString(R.string.Scanning), "");

                       /* constants.saveBleLog("HTBleScanListener - startHTBleScan",
                                Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                                driverPermissionMethod, obdUtil);
*/

                    }

                    @Override
                    public void onScanning(@org.jetbrains.annotations.Nullable HTBleDevice htBleDevice) {
                        //Logger.LogError(TAG_OBD, "Ble onScanning Name: " +htBleDevice.getName());

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
                        //Logger.LogError(TAG_OBD, "Ble onScanFailed");
                        mIsScanning = false;
                        sendBroadCast(getString(R.string.ht_scan_error), "");

                        constants.saveBleLog("HTBleScanListener - onScanFailed - ",
                                Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                                driverPermissionMethod, obdUtil);

                    }

                    @Override
                    public void onScanStop() {
                        //Logger.LogError(TAG_OBD, "Ble onScanStop");
                        mIsScanning = false;
                        SharedPref.saveBleScanCount(bleScanCount, getApplicationContext());
                        String deviceInfo = "";

                        if (HTBleSdk.Companion.getInstance().isConnected()) {   //HTModeSP.INSTANCE.getDeviceMac()
                            sendBroadCast(getString(R.string.ht_scan_completed), "");

                            isDeviceFound = true;

                        } else {

                            if(mHTBleDevices.size() == 1){
                                isDeviceFound = true;
                                deviceInfo = "Single device: " + mHTBleDevices.get(0).getName();
                                connectHtBle(mHTBleDevices.get(0));

                                if (!SharedPref.IsDriverLogin(getApplicationContext())) {
                                    sendDeviceCastAtLogin("");
                                }else {
                                    sendDeviceCast("");
                                }


                            }else if(mHTBleDevices.size() > 1){

                                isDeviceFound = true;
                                String availableDevices = "";
                                for(int i = 0 ; i < mHTBleDevices.size() ; i++){
                                    if(i == 0){
                                        availableDevices = mHTBleDevices.get(i).getName() ;
                                    }else{
                                        availableDevices += "@@@"+ mHTBleDevices.get(i).getName();
                                    }
                                }

                                if (!SharedPref.IsDriverLogin(getApplicationContext())) {
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

                                if(isDeviceFound){
                                    isDeviceFound = false;
                                    deviceInfo = "Device not found";
                                }

                            }

                        }

                        if(deviceInfo.length() > 0) {
                            constants.saveBleLog("HTBleScanListener - onScanStop - Scan Complete, " + deviceInfo,
                                    Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                                    driverPermissionMethod, obdUtil);
                        }


                    }
                });
            }else{
                //Logger.LogError(TAG_OBD, "ble StartScanHtBle else");
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
        HTBleSdk.Companion.getInstance().stopHTBleScan();
        if (HTBleSdk.Companion.getInstance().isAllConnected()) {
            sendBroadCast(getString(R.string.ht_connect_error_other), "");
            initBleListener("Device connection failed");//Register data listener callback
        } else {
            /*int bleStatus = SharedPref.getObdStatus(getApplicationContext());
            if(bleStatus != Constants.BLE_CONNECTED){
                sendBroadCast(getString(R.string.ht_checking), "");
                String macAddress = HTBleSdk.Companion.getInstance().getAddress();
                disconnectBeforeScan(macAddress);
            }else {*/
                sendBroadCast(getString(R.string.ht_connecting), "");
                HTBleSdk.Companion.getInstance().connect(htBleDevice);
                initBleListener("Device connected");//Register data listener callback
           // }
        }
    }


    private void sendBroadCast(String data, String rawMsg) {
        try {
            if (SharedPref.IsDriverLogin(getApplicationContext())) {
                Intent intent = new Intent("ble_changed_data");
                intent.putExtra("decoded_data", data);
                intent.putExtra("raw_message", rawMsg);
                LocalBroadcastManager.getInstance(BleDataService.this).sendBroadcast(intent);
            }
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
                                        Logger.LogError("TAG", "Ble switch on");

                                        constants.writeBleOnOffStatus(true, global, bleGpsAppLaunchMethod,
                                                dbHelper, getApplicationContext());
                                        break;

                                    case BluetoothAdapter.STATE_ON://bluetooth is on
                                        //It is detected that the bluetooth switch is turned on to reconnect
                                        if (!TextUtils.isEmpty(HTBleSdk.Companion.getInstance().getAddress())) {
                                            Logger.LogError("TAG", "Ble switch on");
                                            //Logger.LogError(TAG_OBD, "Ble initBleListener 22" );
                                            initBleListener("Ble STATE_ON");
                                        }
                                        break;
                                    case BluetoothAdapter.STATE_TURNING_OFF://bluetooth is turning off

                                       /* constants.saveBleLog("Ble STATE_TURNING_OFF",
                                                Globally.GetDriverCurrentDateTime(global, getApplicationContext()), getApplicationContext(), dbHelper,
                                                driverPermissionMethod, obdUtil);
*/
                                        sendDeviceCast(null);

                                        constants.writeBleOnOffStatus(false, global, bleGpsAppLaunchMethod,
                                                dbHelper, getApplicationContext());

                                        break;

                                    case BluetoothAdapter.STATE_OFF://bluetooth is off
                                        Logger.LogError("TAG", "Ble switch off");
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
            //Logger.LogError(TAG_OBD, "Ble Clear Cache onDestroy");
            HTBleSdk.Companion.getInstance().disAllConnect();
        }
        HTBleSdk.Companion.getInstance().unRegisterCallBack();//Remove data callback listener
        mHTBleDevices.clear();

        super.onDestroy();

    }




}
