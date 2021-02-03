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

import androidx.annotation.Nullable;

import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.TcpClient;
import com.messaging.logistic.Globally;
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
    private static final long TIME_UPDATES_INTERVAL = 30 * 1000;   // 60 sec
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
        mTimer.schedule(timerTask, TIME_UPDATES_INTERVAL, TIME_UPDATES_INTERVAL);

        //  ------------- Wired OBD ----------
        this.connection = new RemoteServiceConnection();
        this.replyTo = new Messenger(new IncomingHandler());
        BindConnection();

    }




    //  ------------- Wired OBD data response handler ----------
    private class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {

            Bundle bundle = msg.getData();

            int speed = 0;

            try {
                /*  if(bundle.getString(constants.OBD_HighPrecisionOdometer) != null) {
                    currentHighPrecisionOdometer = bundle.getString(constants.OBD_HighPrecisionOdometer);
                }
                timeStamp = bundle.getString(constants.OBD_TimeStamp);
                  vin = bundle.getString(constants.OBD_VINNumber);  */

                ignitionStatus = bundle.getString(constants.OBD_IgnitionStatus);
                truckRPM = bundle.getString(constants.OBD_RPM);
                speed = bundle.getInt(constants.OBD_Vss);

            }catch (Exception e){
                e.printStackTrace();
            }

            // ---------------- temp data ---------------------
             // ignitionStatus = "ON"; truckRPM = "35436"; speed = 30;


            // ELD calling rule for Wired OBD
            /* Timer is calling after 10 sec. SpeedCounter value is 0,10,20,30,40,50,60. It is called 6 times in a minute.
                    In Driving time it is calling only once in a minute. */

            try {
                if (ignitionStatus.equals("ON") || !truckRPM.equals("0")) {
                    if(speed > 10 && lastVehSpeed > 10){
                        Globally.PlaySound(getApplicationContext());
                        Globally.ShowLocalNotification(getApplicationContext(), "ELD", AlertMsg, 2003);
                        SpeakOutMsg(AlertMsgSpeech);
                        // Globally.ShowLogoutNotificationWithSound(getApplicationContext(), "ELD", AlertMsg, mNotificationManager);

                    }
                    lastVehSpeed = speed;
                }else{
                    lastVehSpeed = -1;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    //  ------------- WiFI OBD data response handler ----------
    TcpClient.OnMessageReceived obdResponseHandler = new TcpClient.OnMessageReceived() {
        @Override
        public void messageReceived(String message) {
            Log.d("response", "OBD Response: " +message);

            try {
                String correctData = "";

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
                                    if(WheelBasedVehicleSpeed > 10 && lastVehSpeed > 10){
                                        Globally.PlaySound(getApplicationContext());
                                        Globally.ShowLocalNotification(getApplicationContext(), "ELD", AlertMsg, 2003);
                                        SpeakOutMsg(AlertMsgSpeech);
                                        //  Globally.ShowLogoutNotificationWithSound(getApplicationContext(), "ELD", AlertMsg, mNotificationManager);
                                    }
                                    lastVehSpeed = WheelBasedVehicleSpeed;
                                } else {
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



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("service", "---------onStartCommand Service");

        checkOBDConnection();

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }




    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e(TAG, "-----Running timerTask");

            if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                    !SharedPref.getPassword(getApplicationContext()).equals("")) {
                Log.e("Log", "--stop");
                StopService();

            }else{
                checkOBDConnection();
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




    private void checkOBDConnection(){

        // communicate with wired OBD server app with Message
        StartStopServer(constants.WiredOBD);

        boolean isAlsNetworkConnected   = wifiConfig.IsAlsNetworkConnected(getApplicationContext());  // get ALS Wifi ssid availability
        boolean isWiredObdConnected     = false;
        if(isBound && ( ignitionStatus.equals("ON") && !truckRPM.equals("0") ) ) {
            isWiredObdConnected = true;
        }

        // check WIFI connection
        if( !isWiredObdConnected && isAlsNetworkConnected ){    // check ALS SSID connection
            tcpClient.sendMessage("123456,can");
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
