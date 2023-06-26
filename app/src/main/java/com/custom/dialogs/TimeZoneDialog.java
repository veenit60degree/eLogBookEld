package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.als.logistic.UILApplication;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.constants.VolleyRequestWithoutRetry;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.als.logistic.Globally;
import com.als.logistic.R;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class TimeZoneDialog extends Dialog {

    Button btnUpdateApp;
    TextView TitleTV, recordTitleTV, logoutTV, timezoneDetailIssueTV, autoTimeSettingDescTV;
    ProgressDialog progressD ;
    Constants constant;
    Context mContext;
    Globally global;
    VolleyRequest LogoutRequest;
    Map<String, String> params;
    final int LogoutUser = 1;
    final int CheckConnection = 2;
    boolean isTimeZoneValid,  isCurrentTimeBigger, isTimeValid, IsOnCreateView;
    Animation timeSettingsDescAnim;
    String savedDate;


    public TimeZoneDialog(Context context, boolean isvalidTimeZone, boolean isTimeValid,
                          boolean isOnCreateView, String savedDate) {
        super(context);
        this.mContext = context;
        isTimeZoneValid = isvalidTimeZone;
        this.isTimeValid = isTimeValid;
        IsOnCreateView = isOnCreateView;
        this.savedDate = savedDate;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_update_app);
        constant        = new Constants();

        setCancelable(false);

        //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        if(Globally.isTablet(getContext())) {
            lp.width = constant.intToPixel(mContext, 850);
        }else{
            lp.width = constant.intToPixel(mContext, 730);
        }
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);



        progressD = new ProgressDialog(mContext);
        progressD.setMessage("Loading...");
        LogoutRequest   = new VolleyRequest(mContext);

        global          = new Globally();

        recordTitleTV   = (TextView)findViewById(R.id.recordTitleTV);
        TitleTV         = (TextView)findViewById(R.id.TitleTV);
        logoutTV        = (TextView)findViewById(R.id.logoutTV);
        timezoneDetailIssueTV= (TextView)findViewById(R.id.timezoneDetailIssueTV);
        autoTimeSettingDescTV = (TextView)findViewById(R.id.autoTimeSettingDescTV);

        btnUpdateApp = (Button) findViewById(R.id.btnUpdateApp);

        isCurrentTimeBigger = global.isCurrentTimeBigger(mContext);

        timeSettingsDescAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        timeSettingsDescAnim.setDuration(1500);


        try {
            String fontColor = "<font color='#1A3561'>";
            if(UILApplication.getInstance().isNightModeEnabled()) {
                fontColor = "<font color='#ffffff'>";
                logoutTV.setText(Html.fromHtml("<font color='white'><u>Logout</u></font>"));
            }else{
                logoutTV.setText(Html.fromHtml("<font color='blue'><u>Logout</u></font>"));
            }
            logoutTV.setVisibility(View.GONE);

            String autoTimeSettingDesc = "<b>Note: </b>" + getContext().getString(R.string.auto_time_setting_desc);
            autoTimeSettingDescTV.setText(Html.fromHtml(autoTimeSettingDesc));
            autoTimeSettingDescTV.startAnimation(timeSettingsDescAnim);

            if (isTimeZoneValid) {
                // if time zone is valid, means time is invalid
                TitleTV.setText(mContext.getResources().getString(R.string.timing_mal_alert));  //incorrect_time
                recordTitleTV.setText(mContext.getResources().getString(R.string.timing_mal_alert_desc));   //incorrect_time_desc
                btnUpdateApp.setText(mContext.getResources().getString(R.string.AdjustTime));

                DateTime savedUtcDateTime = global.getDateTimeObj(savedDate, false);
                String offset = DriverConst.GetDriverSettings(DriverConst.OffsetHours, mContext);
                if (offset.length() > 0) {
                    int offSetFromServer = Integer.valueOf(offset);
                    if(offSetFromServer < 0){
                        offSetFromServer = Math.abs(Integer.valueOf(offset));
                    }
                    savedUtcDateTime = savedUtcDateTime.minusHours(offSetFromServer);
                    String serverSavedTime = global.ConvertDateFormatMMddyyyyHHmm(savedUtcDateTime.toString());
                    String currentDate = Globally.GetDriverCurrentDateTime(global, getContext());
                    String DeviceTime = global.ConvertDateFormatMMddyyyyHHmm(currentDate);
                    String[] timeZoneArray = DriverConst.GetDriverSettings(DriverConst.DriverTimeZone, mContext).split(" ");
                    String timeZoneShortName = "";

                    for(int i = 0 ; i < timeZoneArray.length ; i++){
                         if(timeZoneArray[i].length() > 0){
                             timeZoneShortName = timeZoneShortName + timeZoneArray[i].substring(0, 1);
                         }
                     }

                    String timeZoneStatus = "";
                    try {
                        int timeSettingStatus = Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME);
                        Logger.LogDebug("timeSettingStatus","timeSettingStatus: " +timeSettingStatus);
                        if(timeSettingStatus == 0){
                            timeZoneStatus = "<br/><b>Time Settings:</b> Manual";
                        }else{
                            timeZoneStatus = "<br/><b>Time Settings:</b> Automatic";
                        }

                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }

                    String timeAlert = fontColor + "<b>Current " + timeZoneShortName +" Time:</b>   </font> " + serverSavedTime + fontColor +
                            "<br/><b>Your Device Time in " + timeZoneShortName+" :</b> </font> " + DeviceTime + timeZoneStatus;



                    timezoneDetailIssueTV.setText(Html.fromHtml(timeAlert));
                }

            } else {
                TitleTV.setText(mContext.getResources().getString(R.string.incorrect_timezone));
                recordTitleTV.setText(mContext.getResources().getString(R.string.incorrect_timezone_desc));
                btnUpdateApp.setText(mContext.getResources().getString(R.string.AdjustTimeZone));

                String timezoneAlert = fontColor + "<b>Driver Timezone:</b></font> " +
                        DriverConst.GetDriverSettings(DriverConst.DriverTimeZone, mContext) +
                        fontColor + "<br/><b> Device Timezone:</b></font> " + TimeZone.getDefault().getDisplayName();
                timezoneDetailIssueTV.setText(Html.fromHtml(timezoneAlert));

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //btnUpdateApp.setText(mContext.getResources().getString(R.string.ok));
        btnUpdateApp.setOnClickListener(new ChangeTimeZoneListener());

        if (global.isWifiOrMobileDataEnabled(mContext)) {
            GetServerCurrentUtcTime();
        }else{
            if(isCurrentTimeBigger) {
                SharedPref.setCurrentUTCTime(global.GetCurrentUTCTimeFormat(), mContext);
            }

            if(isTimeZoneValid) {
                dismiss();
            }
        }

        timeSettingsDescAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    if (getContext() != null) {
                        autoTimeSettingDescTV.startAnimation(timeSettingsDescAnim);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        HideKeyboard();
    }


    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }


    private class ChangeTimeZoneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            dismiss();
            mContext.startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
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
        if(getContext() != null)
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

            Logger.LogDebug("response", "TimeZone logout response: " + response);

            try {
                if(getContext() != null) {
                    progressD.dismiss();
                }

                String status = "";

                JSONObject obj = new JSONObject(response);
                status = obj.getString("Status");

                if(status.equalsIgnoreCase("true")){

                    switch (flag){
                        case LogoutUser:
                            constant.ClearLogoutData(mContext);
                            if(mContext != null) {
                                dismiss();
                            }
                            break;

                        case CheckConnection:

                            // Save current UTC date time
                            String date = obj.getString("Data");
                            SharedPref.setCurrentUTCTime(date , mContext );
                            boolean isCorrectTime = global.isCorrectTime(mContext, IsOnCreateView, date);

                            if( isCorrectTime && mContext != null){ //&& isTimeZoneValid
                                dismiss();
                            }

                            break;

                    }


                }else{
                    if(obj.getString("Message").equals("Device Logout")) {

                        constant.ClearLogoutData(mContext);
                        if(mContext != null) {
                            dismiss();
                        }
                    }else{
                        if(isCurrentTimeBigger)
                            SharedPref.setCurrentUTCTime( global.GetCurrentUTCTimeFormat() , mContext );
                        if(mContext != null) {
                            dismiss();
                        }
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
           // Logger.LogDebug("onDuty error", "onDuty error: " + error.toString());

            try {
                if(isCurrentTimeBigger)
                    SharedPref.setCurrentUTCTime( global.GetCurrentUTCTimeFormat() , mContext );

                if(getContext() != null) {
                    if(progressD != null)
                        progressD.dismiss();

                    global.EldScreenToast(recordTitleTV, Globally.DisplayErrorMessage(error.toString()), mContext.getResources().getColor(R.color.red_eld));

                    dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };




}