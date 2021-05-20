package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.constants.Constants;
import com.constants.SharedPref;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

public class ObdDataInfoDialog extends Dialog {

    public ObdDataInfoDialog(Context context) {
        super(context);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_malfunctions);

        final TextView obdInfo = (TextView) findViewById(R.id.malfDefTxtView);

        TextView clearMalBtn = (TextView) findViewById(R.id.clearMalBtn);
        clearMalBtn.setText("Refresh");

        clearMalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globally.showToast(obdInfo, "Refreshed");
                showObdData(obdInfo);
            }
        });


        showObdData(obdInfo);

    }


        private void showObdData(TextView view){
            String info = "<br/><br/>Wired/Ble OBD not connected";

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

}