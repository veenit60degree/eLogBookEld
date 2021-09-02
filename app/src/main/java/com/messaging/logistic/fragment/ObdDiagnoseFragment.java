package com.messaging.logistic.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.background.service.BackgroundLocationService;
import com.ble.utils.ToastUtil;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.TcpClient;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.wifi.settings.WiFiConfig;

import org.json.JSONObject;

import java.util.Vector;

import dal.tables.OBDDeviceData;
import obdDecoder.Decoder;

public class ObdDiagnoseFragment extends Fragment  implements View.OnClickListener{

    View rootView;
    TextView bleObdTxtView, odometerTxtView, gpsTxtView, simInfoTxtView, resetObdTxtView, obdDataTxtView, EldTitleTV, responseRawTxtView;
    RelativeLayout rightMenuBtn;
    RelativeLayout eldMenuLay;
    ImageView eldMenuBtn;
    ProgressBar obdProgressBar;
    AlertDialog saveJobAlertDialog;
    private Vector<AlertDialog> vectorDialogs = new Vector<AlertDialog>();

    WiFiConfig wifiConfig;
    TcpClient tcpClient;
    OBDDeviceData data;
    Decoder decoder;
    Constants constants;
    int clickBtnFlag = 0;
    int SIMFlag = 101;
    int CanFlag = 102;
    int RestartObdFlag = 103;
    int GpsFlag = 104;
    int newCmd1 = 1001;

    String simNumber = "";
    String responseTxt = "<b> ############## OBD Response ############## </b> <br><br><br> ";
    String htmlBlueFont = "<font color='blue'>";
    String htmlRedFont = "<font color='red'>";
    String closeFont = "</font>";
    Button button2;
    EditText field1;

   // BleDevice bleDevice = null;
    BackgroundLocationService bleService;
    //BluetoothGattCharacteristic characteristic;
    ProgressBar loaderProgress;
    ScrollView obdLayScrollView;
    Globally globally;
    LinearLayout wifiObdLay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_obd_diagnose, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }


    void initView(View v) {

        data            = new OBDDeviceData();
        decoder         = new Decoder();
        tcpClient       = new TcpClient(obdResponseHandler);
        wifiConfig      = new WiFiConfig();
        constants       = new Constants();
        globally        = new Globally();

        obdLayScrollView= (ScrollView)v.findViewById(R.id.obdLayScrollView);
        loaderProgress  = (ProgressBar)v.findViewById(R.id.loaderProgress);
        bleObdTxtView   = (TextView) v.findViewById(R.id.bleObdTxtView);
        odometerTxtView = (TextView) v.findViewById(R.id.odometerTxtView);
        gpsTxtView      = (TextView) v.findViewById(R.id.gpsTxtView);
        simInfoTxtView  = (TextView) v.findViewById(R.id.simInfoTxtView);
        resetObdTxtView = (TextView) v.findViewById(R.id.resetObdTxtView);
        obdDataTxtView  = (TextView) v.findViewById(R.id.obdDataTxtView);
        EldTitleTV      = (TextView) v.findViewById(R.id.EldTitleTV);
        responseRawTxtView = (TextView) v.findViewById(R.id.responseRawTxtView);

        obdProgressBar  = (ProgressBar)v.findViewById(R.id.obdProgressBar);
        rightMenuBtn    = (RelativeLayout) v.findViewById(R.id.rightMenuBtn);
        eldMenuLay      = (RelativeLayout)v.findViewById(R.id.eldMenuLay);
        wifiObdLay      = (LinearLayout)v.findViewById(R.id.wifiObdLay);
        eldMenuBtn      = (ImageView)v.findViewById(R.id.eldMenuBtn);

        button2         = (Button)v.findViewById(R.id.button2);
        field1          = (EditText)v.findViewById(R.id.field1);

        rightMenuBtn.setVisibility(View.INVISIBLE);
        eldMenuBtn.setImageResource(R.drawable.back_white);


        button2.setVisibility(View.GONE);
        field1.setVisibility(View.GONE);

        obdDataTxtView.setOnClickListener(this);
        bleObdTxtView.setOnClickListener(this);
        odometerTxtView.setOnClickListener(this);
        gpsTxtView.setOnClickListener(this);
        simInfoTxtView.setOnClickListener(this);
        resetObdTxtView.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        button2.setOnClickListener(this);

        if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIFI) {
            EldTitleTV.setText(getResources().getString(R.string.obd_diagnose) + " (WIFI)");
            bleObdTxtView.setVisibility(View.GONE);
        }else if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_BLE){
            EldTitleTV.setText(getResources().getString(R.string.obd_diagnose) + " (Bluetooth)");
            wifiObdLay.setVisibility(View.GONE);
        }else {
            EldTitleTV.setText(getResources().getString(R.string.obd_diagnose) + " (Wired)");
            bleObdTxtView.setVisibility(View.GONE);
            wifiObdLay.setVisibility(View.GONE);
            responseRawTxtView.setVisibility(View.INVISIBLE);
            obdDataTxtView.setText(getString(R.string.no_obd_settings));
        }

        final Button testBtn = (Button) rootView.findViewById(R.id.testBtn);
        testBtn.setVisibility(View.VISIBLE);
        if(BackgroundLocationService.OBD_DISCONNECTED){
            testBtn.setText("OBD Connected");
        }else{
            testBtn.setText("OBD Disconnect");
        }

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // String message = "";
                if(BackgroundLocationService.OBD_DISCONNECTED){
                    BackgroundLocationService.OBD_DISCONNECTED = false;
                    testBtn.setText("OBD Disconnect");

                   // message = "*TS01,861641041996397,015448020921,GPS:3;N49.112521;W122.520018;102;300;1.28,STT:C242;0,MGR:232512842,ADC:0;14.06;1;51.27;2;4.34,CAN:0B00F00414A2A2942800F4A20B00FEC1F2AE6702F2AE67020B00FEE099A0180099A018000B00FEEE8156602FFFFF44FF0B00F003CCFA25FFFF0C80FF0B00FEEFA2FFFF49FFFFFFFA0B00FEF6FF19584BFFFFFFFF0B00FEF7FFFFFFFF1A01FFFF0B00FEFCFFEFFFFFFFFFFFFF,EGT:11721003,EVT:1#";
                }else{
                    BackgroundLocationService.OBD_DISCONNECTED = true;
                    testBtn.setText("OBD Connected");

                  //  message = "*TS01,861641041996397,015555020921,CAN:0B00F004047D7DA62B00F47D0B00FEC180B0670280B067020B00FEF1FFF16FFCFFFF00FF0B00FEF6FF1A4E4DFFFFFFFF0B00FEEE80544B2FFFFF46FF0B00FEEFA9FFFF51FFFFFFFA0B00FEFCFFEFFFFFFFFFFFFF0B00FEF2000000FB0E06FFFF0B00FEE0A9A01800A9A018000B00FEBF8E717C7D757577760B00FD098014FC032933F803#";

                }

               /* try{
                    OBDDeviceData data = decoder.DecodeTextAndSave(message, new OBDDeviceData());
                    JSONObject simObj = new JSONObject(data.toString());
                    simNumber = simObj.getString("SIM");
                    obdDataTxtView.setText(Html.fromHtml(responseTxt + htmlBlueFont + "<b>Sim Number: </b>" +simNumber + closeFont));
                }catch (Exception e){
                    e.printStackTrace();
                }*/


            }
        });


    }


    void scanBtnClick(){
        int bleStatus = SharedPref.getObdStatus(getActivity());
        if(bleStatus == Constants.BLE_CONNECTED){
            //stopBleObdData();
            ToastUtil.show(getActivity(), getString(R.string.device_already_connected));
        }else {

            loaderProgress.setVisibility(View.VISIBLE);
            bleObdTxtView.setText(getString(R.string.start_scan));
            SharedPref.SetPingStatus("start", getActivity());
            SharedPref.saveBleScanCount(0, getActivity());

            Intent serviceIntent = new Intent(getActivity(), BackgroundLocationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(serviceIntent);
            }
            getActivity().startService(serviceIntent);
        }
    }


    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            loaderProgress.setVisibility(View.GONE);
            String data = intent.getStringExtra("decoded_data");

            try {
                int scrollX = obdLayScrollView.getScrollX();
                int scrollY = obdLayScrollView.getScrollY();
                obdDataTxtView.setText(Html.fromHtml(data));
                if (scrollY > 10) {
                    obdLayScrollView.scrollTo(scrollX, scrollY);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if(SharedPref.getObdStatus(getActivity()) == Constants.BLE_CONNECTED){
                bleObdTxtView.setText(getString(R.string.connected) + " (Ble OBD)");
            }else{
                //<b>Device Name:</b> SMBLE-000066<br/><b>MAC Address:</b> C4:64:E3:54:EF:03<br/><br/><b>Sequence Id:</b> 01B5<br/><b>Event Type:</b> 0<br/><b>Event Code:</b> 1<br/><b>Date:</b> 072821<br/><b>Time:</b> 112943<br/><b>Latest ACC ON time:</b> 072821112943<br/><b>Event Data:</b> OnTime<br/><b>Vehicle Speed:</b> 0<br/><b>Engine RPM:</b> 0<br/><b>Odometer:</b> 0<br/><b>Engine Hours:</b> 0<br/><b>VIN Number:</b> <br/><b>Latitude:</b> 30.70728<br/><b>Longitude:</b> 76.68493<br/><b>Distance since Last located:</b> 0<br/><b>Driver ID:</b> <br/><b>Version:</b>1<br/>
                if(!data.contains("MAC Address")) {
                    bleObdTxtView.setText(getString(R.string.connect_ble_obd));
                }else{
                    bleObdTxtView.setText(getString(R.string.connected) + " (Ble OBD)");
                }
            }

            if (SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIFI ) {
                String rawdata = intent.getStringExtra("raw_message");
                responseRawTxtView.setText(rawdata);
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
       // SharedPref.SetOBDScreenStatus(true, getActivity());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver( progressReceiver, new IntentFilter("ble_changed_data"));
    }

    @Override
    public void onPause() {
        super.onPause();
      //  SharedPref.SetOBDScreenStatus(false, getActivity());
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(progressReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.bleObdTxtView:
               /* String message = "*TS01,868323029228761,000115170721,CAN:1500FEEC33414B4A474C4452364A534A4E373536392A0B00F004018C8CEF1F00F48C0B00FEC100BCFE0C00BCFE0C0B00FEE50EEF0400FFFFFFFF0B00FEF1C3E00604000000300B00FEF6FF085E3BFFFFFFFF0B00FEEE7E53C62EFFFF43FF0B00FEEFA5FFFF39FFFFFFFA0B00FEFCFF86FFFFFFFFFFFF0B00FEF25D0016020B06FFFF0B00FEE0C3118500C31185000B00FEBFD70786747969FFFF0B00FEE852146107FFFFA04E0B00FEE99B010B009B010B00#";
               // decoder.DecodeTextAndSave(message, new OBDDeviceData());

                try{
                    data = decoder.DecodeTextAndSave(message, new OBDDeviceData());
                    JSONObject simObj = new JSONObject(data.toString());

                }catch (Exception e){
                    e.printStackTrace();
                }*/

                if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_BLE) {
                    if (constants.CheckGpsStatusToCheckMalfunction(getActivity())) {
                        scanBtnClick();
                    } else {
                        globally.EldScreenToast(rightMenuBtn, getResources().getString(R.string.gps_alert), getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    globally.EldScreenToast(rightMenuBtn, getResources().getString(R.string.chanage_obd_pref_sett_ble), getResources().getColor(R.color.colorVoilation));
                }
                break;

            case R.id.button2:

                if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIFI) {
                    responseRawTxtView.setText("");
                    //  if(wifiConfig.IsAlsNetworkConnected(getActivity()) ) {
                    clickBtnFlag = newCmd1;
                    obdProgressBar.setVisibility(View.VISIBLE);
                    tcpClient.sendMessage(field1.getText().toString().trim());
                    obdDataTxtView.setText("");
                }
               /* }else{
                    globally.EldScreenToast(odometerTxtView, getResources().getString(R.string.obd_connection_desc), getResources().getColor(R.color.colorVoilation));
                }*/

                break;



            case R.id.obdDataTxtView:
                if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIFI) {
                    if (wifiConfig.IsAlsNetworkConnected(getActivity())) {
                        if (clickBtnFlag == SIMFlag) {
                            if (simNumber.trim().length() > 0) {
                                constants.CopyString(getActivity(), simNumber);
                            }
                        }
                    } else {
                        globally.EldScreenToast(rightMenuBtn, getResources().getString(R.string.obd_connection_desc), getResources().getColor(R.color.colorVoilation));
                    }
                }else if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIRED) {
                    Intent serviceIntent = new Intent(getActivity(), BackgroundLocationService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(serviceIntent);
                    }
                    getActivity().startService(serviceIntent);
                }
                break;


            case R.id.responseRawTxtView:

                if(responseRawTxtView.getText().toString().trim().length() > 0) {
                    constants.CopyString(getActivity(), responseRawTxtView.getText().toString().trim());
                }
                break;



            case R.id.odometerTxtView:
                if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIFI) {
                    responseRawTxtView.setText("");
                    if (wifiConfig.IsAlsNetworkConnected(getActivity())) {
                        clickBtnFlag = CanFlag;
                        obdProgressBar.setVisibility(View.VISIBLE);
                        tcpClient.sendMessage("123456,can");
                        obdDataTxtView.setText("");
                    } else {
                        globally.EldScreenToast(rightMenuBtn, getResources().getString(R.string.obd_connection_desc), getResources().getColor(R.color.colorVoilation));
                    }
                }

          /*      String aaaa = "*TS01,868323029228761,024432250219,GPS:3;N49.177366;W122.723542;0;344;0.91,STT:C242;0,MGR:490784638,ADC:0;14.49;1;24.65;2;4.11,CAN:0B00F00471AAA9E31F00F4AA0B00FEC15ECE9C00829C9C000B00FEE0B2450600B24506000B00FEEE733E232BFFFF32FF0B00FEE583310000FFFFFFFF0B00FEE85B2D3E3EFFFFB54F0B00FEE9D28A00004B8A00000B00F003D07E1CFFFF0C82FF0B00FEEFADFFFF6FFFFFFFFA0B00FEBF933B7C7D868687860B00FEF6FF1A434DFFFFFFFF0B00FEF7FFFFFFFF1D01FFFF0B00FEFCFF93FFFFFFFFFFFF#";
               String curr = "*TS01,861107033666695,055408130620,CAN:0B00F004F1BCBD712A00FFBD0B00FEC1F6FEA812FFFFFFFF0B00FEE5DE26010009A019000B00FEBFD1557C7D8687FFFF#";
               String bbbb = "*TS01,861107033675233,044517100620,GPS:3;N40.019955;W120.105307;96;170;0.71,STT:C242;0,MGR:357438382,ADC:0;13.73;1;51.82;2;4.06,CAN:0B00F004F2A0A0CA2800F3FF0B00FEC1389C460FFFFFFFFF0B00FEEE7AFFEB30FFFFFFFF0B00FEE85005010000FF47770B00FEE90DE202000DE202000B00F003510026FF00FFFFFF0B00FEEFFFFFFF4B15A9FFFA0B00FEF6FF2A5A55FF4C49FF0B00FEFCFF7EFFFFFFFFFFFF,EGT:20787525,EVT:1#";
               String message = "*TS01,861641040534124,225428240120,CAN:0B00F00448E1E05A2F00F4E00B00FEC18EC65C0FFFFFFFFF0B00FEE0B60C9D00B60C9D000B00FEEE844D802FFFFF55FF0B00FEE5F7B205002E5614000B00FEE95524050074360E000B00F003DD0000FFFFFF00FF0B00FEEFC1FFFF54FFFFFFFA0B00FEF6FF305463FF8943FF0B00FEF7FFFFFFFF1D011D010B00FEFCFF77FFFFFFFFFFFF,EGT:25371098,EVT:1#";
                  String odbText = "*TS01,861107033601593,051913170919,GPS:3;N49.177049;W122.723174;0;0;1.19,STT:C242;0,MGR:709596933,ADC:0;13.98;1;55.64;2;4.10,CAN:0B00F004407D83C31200F4830B00FEC1CAC39404FFFFFFFF0B00FEE01DE92E001DE92E000B00FEEE6C3B962AFFFF3AFF0B00FEE56AA80100FFFFFFFF0B00FEE8402E0000FFFFA94F0B00FEE9BAED0300BAED03000B00F003D1000AFFFFFF5FFF0B00FEEFA7FFFF49FFFFFFFA0B00FEF6FF004D32FFFFFFFF0B00FEF7FFFFFFFF1901FFFF0B00FEFCFFA4FFFFFFFFFFFF,EGT:19350812,EVT:1#";
                  String dataaa = "*TS01,861107034211905,043806261119,CAN:0B00F004607D87DA15000F880B00FEC129EA9303FFFFFFFF0B00FEE097E80700F7A224000B00FEEE4EFF5027FFFFFFFF0B00FEE5693A010097C504000B00FEE8FFFFFFFFFFFF9E4D0B00FEE9CAE70000AE2604000B00F003D10015FFFF4F5E800B00FEEFFFFFFF450F7DFFFA0B00FEF6FF033AFFFFFFFFFF0B00FEF7FFFFFFFF1501FFFF0B00FEFCFFFFFFFFFFFFFFFF#";
                  String aa = "*TS01,868323029228761,024432250219,GPS:3;N49.177366;W122.723542;0;344;0.91,STT:C242;0,MGR:490784638,ADC:0;14.49;1;24.65;2;4.11,CAN:1600FEE10303602249804D1B282D24F0371DE047EF05190B00F004407D85121900F4860B00FEF1F7FFFFCFFFFFFFFF0B00FEC19206E005FFFFFFFF0B00FEEE5F309329FFFF3AFF0B00FEF23D0000000406FFFF0B00FEFCFF89FFFFFFFFFFFF0B00FEF5FFFFFFFFFFFFFFFF0B00FEF6FF014834FFFFFFFF0B00FEE0F1303C00F1303C00#";
                  parseObdCanData(aaaa);
*/

                break;

            case R.id.gpsTxtView:

                if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIFI) {
                    responseRawTxtView.setText("");
                    if (wifiConfig.IsAlsNetworkConnected(getActivity())) {
                        clickBtnFlag = GpsFlag;
                        obdProgressBar.setVisibility(View.VISIBLE);
                        tcpClient.sendMessage("123456,gps");
                        obdDataTxtView.setText("");
                    } else {
                        globally.EldScreenToast(rightMenuBtn, getResources().getString(R.string.obd_connection_desc), getResources().getColor(R.color.colorVoilation));
                    }
                }
                break;


            case R.id.simInfoTxtView:
                if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIFI) {
                    responseRawTxtView.setText("");
                    if (wifiConfig.IsAlsNetworkConnected(getActivity())) {
                        clickBtnFlag = SIMFlag;
                        obdProgressBar.setVisibility(View.VISIBLE);
                        tcpClient.sendMessage("123456,cid");
                        obdDataTxtView.setText("");
                    } else {
                        globally.EldScreenToast(rightMenuBtn, getResources().getString(R.string.obd_connection_desc), getResources().getColor(R.color.colorVoilation));
                    }
                }

                break;


            case R.id.resetObdTxtView:

                if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIFI) {
                    if (wifiConfig.IsAlsNetworkConnected(getActivity())) {
                        ObdDialog();
                    }else {
                        globally.EldScreenToast(rightMenuBtn, getResources().getString(R.string.obd_connection_desc), getResources().getColor(R.color.colorVoilation));
                    }
                }
                break;

            case R.id.eldMenuLay:
                getParentFragmentManager().popBackStack();
                break;
        }
    }



    public void ObdDialog(){

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Reset OBD !!");
            alertDialogBuilder.setMessage("Do you really want to reset the OBD device?");

            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            clickBtnFlag = RestartObdFlag;
                            obdProgressBar.setVisibility(View.VISIBLE);
                            tcpClient.sendMessage("123456,rst");
                            obdDataTxtView.setText("");
                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            saveJobAlertDialog = alertDialogBuilder.create();
            vectorDialogs.add(saveJobAlertDialog);
            saveJobAlertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void stopBleObdData(){

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Restart BLE OBD !!");
            alertDialogBuilder.setMessage("Do you really want to restart BLE OBD device?");

            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            bleObdTxtView.setText(getString(R.string.disconnected) + " (Ble OBD)");
                            obdDataTxtView.setText("");
                            SharedPref.SetPingStatus("stop", getActivity());

                            Intent serviceIntent = new Intent(getActivity(), BackgroundLocationService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                getActivity().startForegroundService(serviceIntent);
                            }
                            getActivity().startService(serviceIntent);

                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            saveJobAlertDialog = alertDialogBuilder.create();
            vectorDialogs.add(saveJobAlertDialog);
            saveJobAlertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    TcpClient.OnMessageReceived obdResponseHandler = new TcpClient.OnMessageReceived() {
        @Override
        public void messageReceived(String message) {
            Log.d("response", "OBD Respone: " +message);


            try{

                String noCanData = "OBD Data not available";
                String noObd = "obd not connected";
                simNumber = "";

                obdDataTxtView.setText(Html.fromHtml(responseTxt));
                obdProgressBar.setVisibility(View.GONE);

                if(!message.equals(noObd) && message.length() > 10){

                    if(clickBtnFlag == SIMFlag) {
                        responseRawTxtView.setText(message);
                        try{
                            data = decoder.DecodeTextAndSave(message, new OBDDeviceData());
                            JSONObject simObj = new JSONObject(data.toString());
                            simNumber = simObj.getString("SIM");
                            obdDataTxtView.setText(Html.fromHtml(responseTxt + htmlBlueFont + "<b>Sim Number: </b>" +simNumber + closeFont));
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }else if(clickBtnFlag == CanFlag){
                        responseRawTxtView.setText(message);
                        if(message.contains("CAN:UNCONNECTED")){
                            globally.EldScreenToast(rightMenuBtn, noCanData, getResources().getColor(R.color.colorVoilation));
                            obdDataTxtView.setText(Html.fromHtml(responseTxt + htmlRedFont + "Odometer data not available" + closeFont) );

                        }else {

                            parseObdCanData(message);

                        }


                    }else if(clickBtnFlag == GpsFlag){
                        responseRawTxtView.setText(message);
                        if (message.contains("GPS")) {
                            String[] responseArray = message.split("GPS");
                            if (responseArray.length > 1) {
                                String gpsData = responseArray[1];
                                String[] gpsArray = gpsData.split(";");
                                if (gpsArray.length > 3) {
                                    String latitude = gpsArray[1].substring(1, gpsArray[1].length());
                                    String longitude = gpsArray[2].substring(1, gpsArray[2].length());
                                    String speed = gpsArray[3];

                                    String obdGPS = "<b>Latitude:</b> " + latitude + "<br />" +
                                            "<b>Longitude:</b> " + longitude + "<br />" +
                                            "<b>Speed:</b> " + speed;
                                    Log.d("obdGPS", "obdGPS: " + obdGPS);
                                    obdDataTxtView.setText(Html.fromHtml(responseTxt + htmlBlueFont +obdGPS + closeFont));


                                }
                            }
                        }
                    }else if(clickBtnFlag == RestartObdFlag){
                        if(message.contains("RST")){
                            globally.EldScreenToast(rightMenuBtn, getResources().getString(R.string.obd_restarted), getResources().getColor(R.color.colorDriving));
                            obdDataTxtView.setText(Html.fromHtml(responseTxt + htmlBlueFont + getResources().getString(R.string.obd_restarted) + closeFont));
                        }
                    }else if(clickBtnFlag == newCmd1){
                        obdDataTxtView.setText(Html.fromHtml(responseTxt + htmlBlueFont + responseTxt + closeFont));
                    }
                }else{
                    //*TS01,866758047725979,070204100120,RST#
                    if(clickBtnFlag == RestartObdFlag){
                        if(message.equals(noObd)){
                            globally.EldScreenToast(rightMenuBtn, noObd, getResources().getColor(R.color.colorVoilation));
                        }else {
                            globally.EldScreenToast(rightMenuBtn, getResources().getString(R.string.obd_restarted), getResources().getColor(R.color.colorDriving));
                        }
                    }else {
                        globally.EldScreenToast(rightMenuBtn, noObd, getResources().getColor(R.color.colorVoilation));
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
                obdDataTxtView.setText("" + e.toString());
            }
        }
    };


    private void parseObdCanData(String message){
        try {

            String preFix = "*TS01,861107039609723,050743230120,";
            String postFix = "#";

            if(message.length() > 5 ){
                String first = message.substring(0, 5);
                String last = message.substring(message.length()-1, message.length());
                if(!first.equals("*TS01") && !last.equals("#")){
                    message = preFix + message + postFix;
                }
            }
            // have to comment "odbText" object ans pass "message" object to in decoder class

            data = decoder.DecodeTextAndSave(message, new OBDDeviceData());
            JSONObject canObj = new JSONObject(data.toString());
            String MileageInMeters          = wifiConfig.checkJsonParameter(canObj, "MileageInMeters", "0");
            String TripDistanceInKM         = wifiConfig.checkJsonParameter(canObj, "TripDistanceInKM", "0");
            String HighResolutionDistance   = wifiConfig.checkJsonParameter(canObj, "HighResolutionTotalVehicleDistanceInKM", "0");
            String WheelBasedVehicleSpeed   = wifiConfig.checkJsonParameter(canObj, "WheelBasedVehicleSpeed", "0");

            String EngineHours   = wifiConfig.checkJsonParameter(canObj, "EngineHours", "0");
            String GPSLatitude   = wifiConfig.checkJsonParameter(canObj, "GPSLatitude", "0");
            String GPSLongitude   = wifiConfig.checkJsonParameter(canObj, "GPSLongitude", "0");
            String RPMEngineSpeed   = wifiConfig.checkJsonParameter(canObj, "RPMEngineSpeed", "0");
            String TotalVehcileDistance   = wifiConfig.checkJsonParameter(canObj, "TotalVehcileDistance", "0");

            String canData =
                    "<b>Mileage               : </b> " + MileageInMeters + " m <br />" +
                            "<b>TripDistance          : </b> " + TripDistanceInKM + " km <br />" +
                            "<b>Total Vehicle Distance: </b> " + HighResolutionDistance + "<br />" +
                            "<b>WheelBasedVehicleSpeed: </b> " + WheelBasedVehicleSpeed + " km <br />" +

                            "<b>Engine Hours: </b> " + EngineHours + "<br />" +
                            "<b>GPS Latitude: </b> " + GPSLatitude + "<br />" +
                            "<b>GPS Longitude: </b> " + GPSLongitude + "<br />" +
                            "<b>RPM Engine Speed: </b> " + RPMEngineSpeed + "<br />" +
                            "<b>Total Vehcile Distance: </b> " + TotalVehcileDistance + "<br />"  ;



            obdDataTxtView.setText( Html.fromHtml(responseTxt + htmlBlueFont + canData + closeFont));

        }catch (Exception e){
            obdDataTxtView.setText(Html.fromHtml(responseTxt + htmlBlueFont + "------" + closeFont ) );
            e.printStackTrace();
        }

    }


}
