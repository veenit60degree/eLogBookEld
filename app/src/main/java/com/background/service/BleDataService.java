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
import com.htstart.htsdk.HTBleSdk;
import com.htstart.htsdk.bluetooth.HTBleData;
import com.htstart.htsdk.bluetooth.HTBleDevice;
import com.htstart.htsdk.minterface.HTBleScanListener;
import com.htstart.htsdk.minterface.IReceiveListener;
import com.messaging.logistic.R;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BleDataService extends Service {


    // Bluetooth obd adapter decleration
    boolean mIsScanning = false;
    int bleScanCount = 0;
    private BluetoothAdapter mBTAdapter;
    private ArrayList<HTBleDevice> mHTBleDevices = new ArrayList<>();

    public static boolean isConnected = false;
    public ProgressDialog pdDialog;
    private static final long OBD_TIME_LOCATION_UPDATES = 10 * 1000;   // 10 sec

    private Handler bleHandler = new Handler();
    Constants constants;
    String TAG_OBD = "BleService";

    /**
     * Please note that Bluetooth will also detect whether there is a connected device during initialization. If so, it will automatically connect to the Bluetooth device after the initial completion. If automatic connection after initialization is not required, please actively disconnect isAllConnected() or disconnect when the program exits ()
     * If you want to connect and switch to other devices, you need to disconnect isAllConnected() or disconnect() first, and then connect again. The current sdk does not support the function of connecting multiple devices at the same time.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();


        constants = new Constants();

        bleHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                try {
                    StartScanHtBle();   //Device connection after scanning is turned on
                }catch (Exception e){ }
            }
        };


        /**
         * 在onStartCommand()
         * The checkPermissionsBeforeScanBle can be called in the method, and there is no need to call it again in the oncreate() method
         */
//        checkPermissionsBeforeScanBle();
        registerReceiver(broadcastReceiver, makeFilter());

    }

    /**
     * 启动服务不正确，先后启动了两次服务，onStartCommand被连续回调了两次
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        checkPermissionsBeforeScanBle();

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
                    Log.d(TAG_OBD, "The Bluetooth connection is successful, and the subsequent command operation will be performed after a delay of 3 seconds.");
                    initBleListener();//Register data listener callback
                    return;
                }
                if (!TextUtils.isEmpty(HTBleSdk.Companion.getInstance().getAddress())) {//Determine whether there is an existing connected bluetooth device in the cache, no need to scan the connection again, the sdk will automatically connect
                    Log.d(TAG_OBD, "Bluetooth is in the process of connecting. After the connection is successful, it needs to delay for 3 seconds before performing subsequent command operations.");
                    initBleListener();//Register data listener callback

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

    private boolean isBleEnabled() {
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
    void initBleListener() {
//        HTBleSdk.Companion.getInstance().unRegisterCallBack();
        HTBleSdk.Companion.getInstance().registerCallBack(new IReceiveListener() {
            @Override
            public void onConnected(@Nullable String s) {
                Log.e("TAG", "onConnected==" + s);
                //  Log.d("getAddress-onConnected", "getAddress: " +s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_CONNECTED, s));
                // set request interval 3 sec
                /**
                 * There is no need to delay each command for 3 seconds. It is only necessary to issue the command after the overall delay of 3 seconds after the Bluetooth connection is successful, because after the Bluetooth connection is successful, the SDK needs to establish a channel with the device and initialize related protocols. It takes a certain time.
                 */
                //  new Handler().postDelayed(() -> HTBleSdk.Companion.getInstance().setIntervalEvent(3),3000);

                isConnected = true;
                sendBroadCast(getString(R.string.ht_connected), "");
                pdDialog.cancel();

            }

            @Override
            public void onConnectTimeout(@Nullable String s) {
                Log.e("TAG", "onConnectTimeout==" + s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_TIMEOUT, s));
                //Log.d("getAddress-Timeout", "getAddress: " +s);
            }

            @Override
            public void onConnectionError(@NonNull String s, int i, int i1) {
                Log.e("TAG", "onConnectionError==" + s);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_ERROR, s));
                sendBroadCast(getString(R.string.ht_connect_error), "");

            }

            @Override
            public void onDisconnected(@Nullable String address) {
                Log.e("TAG", "onDisconnected==" + address);
                HTBleSdk.Companion.getInstance().unRegisterCallBack();
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, address));
                // Log.d("getAddress-onDisconnect", "getAddress: " +s);

                sendBroadCast(getString(R.string.ht_disconnected), "");
                isConnected = false;

            }

            @Override
            public void onReceive(@NotNull String address, @NotNull String uuid, @NotNull HTBleData htBleData) {
                Log.e("TAG", "onReceive==" + htBleData);
                //  Log.d("htBleData", "htBleData: " + htBleData);

                //  EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
                if (htBleData.getEventType() == 0 && htBleData.getEventCode() == 1) {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
                } else {
                    EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_QUERY_DATA_AVAILABLE, address, uuid, htBleData));
                }

                if (htBleData.getEventData() != "OnTime") {
                    sendBroadCast(BleUtil.decodeDataChange(htBleData, HTBleSdk.Companion.getInstance().getAddress()), "");
                }
            }


            @Override
            public void onResponse(@NotNull String address, @NotNull String uuid, @NotNull String sequenceID, @NotNull int status) {
//                Log.e("TAG", "onResponse" + status + "==" + address);
                bleScanCount = 0;
                //Log.d("getAddress-onResponse", "getAddress: " +address);
                EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_RESPONSE, address, uuid, status));
                sendBroadCast(getString(R.string.ht_ble_response1), "");
            }
        });
    }


    /**
     * Turn on Bluetooth scanning for devices
     */
    public void StartScanHtBle(){
        Log.e("TAG", "start scanning");

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

    /**
     * Bluetooth device connection
     *
     * @param htBleDevice
     */
    private void connectHtBle(final HTBleDevice htBleDevice) {
        Log.e("TAG", "connectHtBle=" + htBleDevice.getAddress());
        HTBleSdk.Companion.getInstance().stopHTBleScan();
        if (HTBleSdk.Companion.getInstance().isAllConnected()) {
            // ToastUtil.show(getApplicationContext(), getString(R.string.ht_connect_error_other));
            sendBroadCast(getString(R.string.ht_connect_error_other), "");
            initBleListener();//Register data listener callback
        } else {
            String macAddress = htBleDevice.getAddress();
            sendBroadCast(getString(R.string.ht_connected), "");
            HTBleSdk.Companion.getInstance().connect(htBleDevice);
            initBleListener();//Register data listener callback
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Register for Bluetooth switch status monitoring
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON://bluetooth is on

                            break;
                        case BluetoothAdapter.STATE_ON://bluetooth is on
                            //It is detected that the bluetooth switch is turned on to reconnect
                            if (!TextUtils.isEmpty(HTBleSdk.Companion.getInstance().getAddress())) {
                                Log.e("TAG", "Bluetooth switch on");
                                initBleListener();
                            }
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF://bluetooth is turning off

                            break;
                        case BluetoothAdapter.STATE_OFF://bluetooth is off
                            break;
                    }
            }
        }
    };

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /*unregisterReceiver(broadcastReceiver);
        if (mIsScanning)
            HTBleSdk.Companion.getInstance().stopHTBleScan();
        if (HTBleSdk.Companion.getInstance().isAllConnected())
            HTBleSdk.Companion.getInstance().disAllConnect();
        HTBleSdk.Companion.getInstance().unRegisterCallBack();//Remove data callback listener
    */

    }


    /**
     * Prevent continuous invocation of the startup service
     */
  /*  private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;
    private boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }*/

}
