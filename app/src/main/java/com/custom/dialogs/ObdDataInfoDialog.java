package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.constants.Constants;
import com.constants.SharedPref;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.HosSummaryFragment;

import java.util.Timer;
import java.util.TimerTask;

public class ObdDataInfoDialog extends Dialog {

    public ObdDataInfoDialog(Context cxt) {
        super(cxt);
        context = cxt;
    }

    long MIN_TIME_BW_UPDATES = 3000;  // 3 Sec
    ObdTimerTask timerTask;
    private Timer mTimer;
    TextView obdInfo;
    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_malfunctions);

        obdInfo = (TextView) findViewById(R.id.malfDefTxtView);

        TextView clearMalBtn = (TextView) findViewById(R.id.clearMalBtn);
        clearMalBtn.setText("Refresh");

        mTimer = new Timer();
        timerTask = new ObdTimerTask();

        clearMalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globally.showToast(obdInfo, "Refreshed");
                showObdData(obdInfo);
            }
        });


        showObdData(obdInfo);


     /*   mTimer.schedule(new TimerTask() {
            public void run() {
                ((Activity)context).runOnUiThread(new Runnable(){
                    public void run() {
                        showObdData(obdInfo);
                    }
                });
            }
        }, MIN_TIME_BW_UPDATES);
*/

        startTimer();

    }


    private void showObdData(final TextView view){
           String info = "<br/><br/> OBD not connected";

            int OBD_LAST_STATUS = SharedPref.getObdStatus(getContext());
            if(OBD_LAST_STATUS == Constants.WIRED_CONNECTED || OBD_LAST_STATUS == Constants.BLE_CONNECTED) {
                String obdType = "";
                if(OBD_LAST_STATUS == Constants.WIRED_CONNECTED) {
                    obdType = "Wired Tablet";
                }else{
                    obdType = "Bluetooth OBD";
                }

            info = "<br/><br/><b>OBD Type:</b> " + obdType + "<br/><br/>" +
                    "<b>VIN:</b> " + SharedPref.getVehicleVin(getContext()) + "<br/>" +
                    "<b>Speed:</b> " + SharedPref.getVss(getContext()) + "<br/>" +
                    "<b>RPM:</b> " + SharedPref.getRPM(getContext()) + "<br/>" +
                    "<b>EngineHours:</b> " + SharedPref.getObdEngineHours(getContext()) + "<br/>" +
                    "<b>OdometerInKm:</b> " + SharedPref.getWiredObdOdometer(getContext()) + "<br/>" +
                    "<b>OdometerInMeter:</b> " + SharedPref.getHighPrecisionOdometer(getContext()) + "<br/>" ;


            }
            view.setText(Html.fromHtml(info));
        }


    private class ObdTimerTask extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run() {

            ((Activity)context).runOnUiThread(new Runnable(){
                public void run() {
                    showObdData(obdInfo);
                }
            });

        }
    }


    private void startTimer() {
        try {
            mTimer.schedule(timerTask, MIN_TIME_BW_UPDATES, MIN_TIME_BW_UPDATES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void clearTimer() {
        try {
            if (mTimer != null) {
                mTimer.cancel();
                timerTask.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        clearTimer();
        super.onStop();
    }


}