package com.background.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.TcpClient;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.notifications.NotificationManagerSmart;
import com.wifi.settings.WiFiConfig;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import dal.tables.OBDDeviceData;
import obdDecoder.Decoder;


public class AfterLogoutService extends Service implements TextToSpeech.OnInitListener{

    String ServerPackage = "com.als.obd";
    String ServerService = "com.als.obd.services.MainService";
    String ignitionStatus = "", truckRPM = "";
    String noObd = "obd not connected";
    String AlertMsg = "Your vehicle is moving and there is no driver login in eLog book";
    String AlertMsgSpeech = "Your vehicle is moving and there is no driver login in e log book";

    String TAG_OBD = "OBD Service";
    private static final long TIME_INTERVAL_WIFI  = 10 * 1000;   // 10 sec
    private static final long TIME_INTERVAL_WIRED = 2 * 1000;   // 3 sec
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
    SharedPref sharedPref;
    private Messenger messenger = null;     //used to make an RPC invocation
    private boolean isBound = false;
    private ServiceConnection connection;   //receives callbacks from bind and unbind invocations
    private Messenger replyTo = null;       //invocation replies are processed by this Messenger
    private Handler mHandler = new Handler();
    NotificationManagerSmart mNotificationManager;
    boolean isWiredCallBackCalled = false;


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

        sharedPref              = new SharedPref();
        constants               = new Constants();
        mTimer                  = new Timer();

        //  ------------- Wired OBD ----------
        this.connection = new RemoteServiceConnection();
        this.replyTo = new Messenger(new IncomingHandler());
        BindConnection();

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

            try {
                if (ignitionStatus.equals("ON") || !truckRPM.equals("0")) {
                    sharedPref.SaveObdStatus(Constants.WIRED_ACTIVE, getApplicationContext());
                    if(speed > 8 && lastVehSpeed > 8){
                        sharedPref.setLoginAllowedStatus(false, getApplicationContext());

                        long count = sharedPref.getLastCalledWiredCallBack(getApplicationContext());
                        if(count == 0) {
                            sharedPref.setLastCalledWiredCallBack(count, getApplicationContext());
                            Globally.PlaySound(getApplicationContext());
                            Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD", AlertMsg, 2003);
                            SpeakOutMsg(AlertMsgSpeech);
                        }

                        if(count >= TIME_INTERVAL_LIMIT){
                            // reset call back count
                            count = 0;
                        }

                        // save count --------------
                        sharedPref.setLastCalledWiredCallBack(count + TIME_INTERVAL_WIRED, getApplicationContext());
                       // Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD", "OBD Speed: " + speed, 203040);
                    }else{
                        sharedPref.setLoginAllowedStatus(true, getApplicationContext());
                    }
                    lastVehSpeed = speed;

                    CallWired(TIME_INTERVAL_WIRED);

                }else{
                    lastVehSpeed = -1;

                    if(sharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_ACTIVE ||
                            sharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_ACTIVE) {
                        sharedPref.SaveObdStatus(Constants.WIRED_INACTIVE, getApplicationContext());
                        CallWired(TIME_INTERVAL_WIFI);
                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void CallWired(long time){
        if (sharedPref.getUserName(getApplicationContext()).equals("") && sharedPref.getPassword(getApplicationContext()).equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isWiredCallBackCalled = true;
                    StartStopServer(constants.WiredOBD);
                }
            }, time);
        }
    }

    private void checkWifiOBDConnection(){

        // communicate with wired OBD server app with Message
       // StartStopServer(constants.WiredOBD);

        if (sharedPref.getUserName(getApplicationContext()).equals("") &&  sharedPref.getPassword(getApplicationContext()).equals("")) {

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



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("service", "---------onStartCommand Service");

        StartStopServer(constants.WiredOBD);
        checkWifiOBDConnection();

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }




    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e(TAG, "-----Running Logout timerTask");

            if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                    !SharedPref.getPassword(getApplicationContext()).equals("")) {
                Log.e("Log", "--stop");
                StopService();

            }else{

                // communicate with wired OBD server app with Message
                if(SharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_ACTIVE &&
                        SharedPref.getObdStatus(getApplicationContext()) != Constants.WIFI_ACTIVE){
                    StartStopServer(constants.WiredOBD);
                }else{
                    if(isWiredCallBackCalled == false){
                        StartStopServer(constants.WiredOBD);
                    }
                }

                checkWifiOBDConnection();
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

                sharedPref.setLoginAllowedStatus(true, getApplicationContext());

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
                                    sharedPref.SaveObdStatus(Constants.WIFI_ACTIVE, getApplicationContext());
                                    if(WheelBasedVehicleSpeed > 8 && lastVehSpeed > 8){
                                        sharedPref.setLoginAllowedStatus(false, getApplicationContext());
                                        Globally.PlaySound(getApplicationContext());
                                        Globally.ShowLogoutSpeedNotification(getApplicationContext(), "ALS ELD", AlertMsg, 2003);
                                        SpeakOutMsg(AlertMsgSpeech);
                                        //  Globally.ShowLogoutNotificationWithSound(getApplicationContext(), "ELD", AlertMsg, mNotificationManager);
                                    }else{
                                        sharedPref.setLoginAllowedStatus(true, getApplicationContext());
                                    }
                                    lastVehSpeed = WheelBasedVehicleSpeed;
                                } else {
                                    if(sharedPref.getObdStatus(getApplicationContext()) != Constants.WIRED_ACTIVE) {
                                        sharedPref.SaveObdStatus(Constants.WIFI_INACTIVE, getApplicationContext());
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



    public void onDestroy() {

        if(!isStopService) {

            Intent intent = new Intent(Constants.packageName);
            intent.putExtra("location", "torestore");
            sendBroadcast(intent);
        }else{
            Log.d(TAG, "Service stopped");

            //  ------------- Wired OBD ----------
            if(isBound){
                StartStopServer("stop");
                this.unbindService(connection);
                isBound = false;
            }

        }

    }




}
