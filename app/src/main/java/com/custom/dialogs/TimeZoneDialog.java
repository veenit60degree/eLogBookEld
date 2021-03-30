package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.constants.VolleyRequestWithoutRetry;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class TimeZoneDialog extends Dialog {

    Button btnUpdateApp;
    TextView TitleTV, recordTitleTV, logoutTV;
    ProgressDialog progressD ;
    Constants constant;
    Context mContext;
    Globally global;
    SharedPref sharedPref;
    VolleyRequest LogoutRequest;
    Map<String, String> params;
    final int LogoutUser = 1;
    final int CheckConnection = 2;
    boolean isTimeZoneValid,  isCurrentTimeBigger, isTimeValid;


    public TimeZoneDialog(Context context, boolean isvalidTimeZone, boolean isTimeValid) {
        super(context);
        this.mContext = context;
        isTimeZoneValid = isvalidTimeZone;
        this.isTimeValid = isTimeValid;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_update_app);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressD = new ProgressDialog(mContext);
        progressD.setMessage("Loading...");
        LogoutRequest   = new VolleyRequest(getContext());
        constant        = new Constants();
        global          = new Globally();
        sharedPref      = new SharedPref();

        recordTitleTV   = (TextView)findViewById(R.id.recordTitleTV);
        TitleTV         = (TextView)findViewById(R.id.TitleTV);
        logoutTV        = (TextView)findViewById(R.id.logoutTV);

        btnUpdateApp = (Button) findViewById(R.id.btnUpdateApp);

        isCurrentTimeBigger = global.isCurrentTimeBigger(getContext());
        logoutTV.setText(Html.fromHtml("<font color='blue'><u>Logout</u></font>"));
        btnUpdateApp.setText(mContext.getResources().getString(R.string.AdjustTime));

        if(isTimeZoneValid){
            // if time zone is valid, means time is invalid
            TitleTV.setText(mContext.getResources().getString(R.string.incorrect_time));
            recordTitleTV.setText(mContext.getResources().getString(R.string.incorrect_time_desc));

        }else {
            TitleTV.setText(mContext.getResources().getString(R.string.incorrect_timezone));
            recordTitleTV.setText(mContext.getResources().getString(R.string.incorrect_timezone_desc));
        }

        btnUpdateApp.setOnClickListener(new ChangeTimeZoneListener());

        if (global.isWifiOrMobileDataEnabled(getContext())) {
            GetServerCurrentUtcTime();
        }else{
            if(isCurrentTimeBigger) {
                sharedPref.setCurrentUTCTime(global.GetCurrentUTCTimeFormat(), mContext);
            }

            if(isTimeZoneValid) {
                dismiss();
            }
        }

        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPref.getDriverId(getContext()).length() > 0){
                    if (global.isWifiOrMobileDataEnabled(getContext())) {
                        LogoutUser(sharedPref.getDriverId(getContext()));
                    } else {
                        global.EldScreenToast(logoutTV, global.CHECK_INTERNET_MSG, getContext().getResources().getColor(R.color.colorSleeper));
                    }
                }else {
                    constant.ClearLogoutData(mContext);
                    dismiss();
                }
            }
        });


        HideKeyboard();
    }


    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }


    private class ChangeTimeZoneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            dismiss();
            getContext().startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
        }
    }



    //*================== Logout User request ===================*//*
    void LogoutUser(final String DriverId){
        progressD.show();
        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.MobileDeviceCurrentDateTime, global.getCurrentDate());

        LogoutRequest.executeRequest(Request.Method.POST, APIs.DRIVER_LOGOUT , params, LogoutUser,
                Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);

    }

    //*================== Get Server Current Utc Time request ===================*//*
    void GetServerCurrentUtcTime(){
        progressD.show();
        VolleyRequestWithoutRetry GetServerTimeRequest = new VolleyRequestWithoutRetry(mContext);
        Map<String, String> params = new HashMap<String, String>();
        GetServerTimeRequest.executeRequest(Request.Method.GET, APIs.CONNECTION_UTC_DATE, params, CheckConnection,
                Constants.SocketTimeout4Sec, ResponseCallBack, ErrorCallBack);
    }



    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

            progressD.dismiss();
            Log.d("response", " logout response: " + response);
            String status = "";

            try {
                JSONObject obj = new JSONObject(response);
                status = obj.getString("Status");

                if(status.equalsIgnoreCase("true")){

                    switch (flag){
                        case LogoutUser:
                            constant.ClearLogoutData(mContext);
                            dismiss();
                            break;

                        case CheckConnection:

                            // Save current UTC date time
                            sharedPref.setCurrentUTCTime( obj.getString("Data") , mContext );
                            boolean isCorrectTime = global.isCorrectTime(mContext);

                            if( isCorrectTime && isTimeZoneValid){
                                dismiss();
                            }

                            break;

                    }


                }else{
                    if(obj.getString("Message").equals("Device Logout")) {

                        constant.ClearLogoutData(mContext);
                        dismiss();

                    }else{
                        if(isCurrentTimeBigger)
                            sharedPref.setCurrentUTCTime( global.GetCurrentUTCTimeFormat() , mContext );

                      //  Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }
    };

    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

        @Override
        public void getError(VolleyError error, int flag) {
            Log.d("onDuty error", "onDuty error: " + error.toString());
            progressD.dismiss();
            global.EldScreenToast(recordTitleTV, error.toString(), getContext().getResources().getColor(R.color.red_eld));

            if(isCurrentTimeBigger)
                sharedPref.setCurrentUTCTime( global.GetCurrentUTCTimeFormat() , mContext );

            try {
                if(mContext != null)
                    dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };




}