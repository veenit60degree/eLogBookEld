package com.custom.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.AlertDialogEld;
import com.constants.Constants;
import com.constants.CsvReader;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.UILApplication;
import com.models.DriverLocationModel;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.MainDriverEldPref;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShareDriverLogDialog extends Dialog implements View.OnClickListener  {

    String emailPattern     = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String emailPattern2    = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
    String TAG              = "Google Places";
    String DRIVER_ID;
    String DeviceId;
    String CurrentCycleId   = "";
    Date StartDate, EndDate;
    DateFormat format;
    ImageView countryFlagImgView;
    Button changeLocLogBtn;
    LinearLayout cancelDriverLogBtn, shareDriverLogBtn;
    RelativeLayout fmcsaLogBtn, pdfLogBtn;
    EditText amountEditText, inspCmntEditTxt, cityShareEditText, canEmailEditText;
    CheckBox checkboxEmail, checkboxService;
    Spinner countrySpinner;
    AutoCompleteTextView locLogAutoComplete;
    TextView startDateTv ,endDateTv, fmcsaDescTV, fmcsaLogTxtVw, pdfLogTxtVw, dataTransTxtView;
    LinearLayout emailLogLay, logBtnLay, shareLogMainLay, shareLogChildLay, shareServiceDialog, countryLayout, inspectorCommentLay;
    RelativeLayout sharedLocLay, AobrdSharedLocLay;
    String selectedDateView = "", email = "";
    FragmentActivity activity;
    ProgressBar sendLogProgressBar;
    DatePickerDialog dateDialog;

    int CountryNoSelection  = 0;
    int CountryCan          = 1;
    int CountryUsa          = 2;
    int UsaMaxDays          = 7;    // 1 + 7  = 8 days
    int CanMaxDays          = 14;   // 1 = 13  = 14 days
    int MaxDays, SelectedCountry = 0;
    boolean IsAOBRD;
    private String City = "", Country = "", canSelectedEmail = "", selectedCountry = "Select", LocationFromApi = "" ;
    List<DriverLocationModel> StateList;
    List<String> StateArrayList;
    Spinner stateSharedSpinner;
    ScrollView sendLogScrollView;
    VolleyRequest GetAddFromLatLngRequest;
    Constants constant;
    Globally globally;
    HelperMethods hMethods;
    DBHelper dbHelper;
    AlertDialogEld statusEndConfDialog;
    CsvReader csvReader;
    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;


    public ShareDriverLogDialog(Context context, FragmentActivity activity, String dRIVER_ID,
                                String deviceId, String currentCycleId, boolean isAOBRD,
                                List<String> stateArrayList, List<DriverLocationModel> stateList) {
        super(context);
        this.activity   = activity;
        IsAOBRD         = isAOBRD;
        DRIVER_ID       = dRIVER_ID;
        DeviceId        = deviceId;
        CurrentCycleId  = currentCycleId;
        StateArrayList  = stateArrayList;
        StateList       = stateList;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_share_log);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setCancelable(false);

        format = new SimpleDateFormat("MM/dd/yyyy");
        EndDate = new Date();
        StartDate = new Date();
        constant  = new Constants();
        globally  = new Globally();
        csvReader = new CsvReader();

        GetAddFromLatLngRequest = new VolleyRequest(getContext());

        hMethods = new HelperMethods();
        dbHelper = new DBHelper(getContext());
        statusEndConfDialog  = new AlertDialogEld(getContext());
        MainDriverPref          = new MainDriverEldPref();
        CoDriverPref            = new CoDriverEldPref();

        checkboxEmail       = (CheckBox)findViewById(R.id.checkboxEmail);
        checkboxService     = (CheckBox)findViewById(R.id.checkboxService);
        sendLogProgressBar  = (ProgressBar)findViewById(R.id.sendLogProgressBar);

        changeLocLogBtn     = (Button)findViewById(R.id.changeLocLogBtn);

        countryFlagImgView  = (ImageView)findViewById(R.id.countryFlagImgView);
        countrySpinner      = (Spinner)findViewById(R.id.countrySpinner);

        cancelDriverLogBtn  = (LinearLayout)findViewById(R.id.cancelDriverLogBtn);
        shareDriverLogBtn   = (LinearLayout) findViewById(R.id.shareDriverLogBtn);

        locLogAutoComplete  = (AutoCompleteTextView)findViewById(R.id.locLogAutoComplete);
        amountEditText      = (EditText)findViewById(R.id.amountEditText);
        inspCmntEditTxt     = (EditText)findViewById(R.id.inspCmntEditTxt);
        cityShareEditText   = (EditText)findViewById(R.id.cityShareEditText);
        canEmailEditText    = (EditText)findViewById(R.id.canEmailEditText);

        startDateTv         = (TextView) findViewById(R.id.startDateTv);
        endDateTv           = (TextView) findViewById(R.id.endDateTv);
        fmcsaDescTV         = (TextView) findViewById(R.id.fmcsaDescTV);
        fmcsaLogTxtVw       = (TextView) findViewById(R.id.fmcsaLogTxtVw);
        pdfLogTxtVw         = (TextView) findViewById(R.id.pdfLogTxtVw);
        dataTransTxtView    = (TextView) findViewById(R.id.dataTransTxtView);

        emailLogLay         = (LinearLayout)findViewById(R.id.emailLogLay);
        logBtnLay           = (LinearLayout)findViewById(R.id.logBtnLay);
        shareLogMainLay     = (LinearLayout)findViewById(R.id.shareLogMainLay);
        shareLogChildLay    = (LinearLayout)findViewById(R.id.shareLogChildLay);
        shareServiceDialog  = (LinearLayout)findViewById(R.id.shareServiceDialog);
        countryLayout       = (LinearLayout)findViewById(R.id.countryLayout);
        inspectorCommentLay = (LinearLayout)findViewById(R.id.inspectorCommentLay);

        fmcsaLogBtn         = (RelativeLayout) findViewById(R.id.fmcsaLogBtn);
        pdfLogBtn           = (RelativeLayout)findViewById(R.id.pdfLogBtn);
        AobrdSharedLocLay   = (RelativeLayout)findViewById(R.id.AobrdSharedLocLay);
        sharedLocLay        = (RelativeLayout)findViewById(R.id.sharedLocLay);

        stateSharedSpinner  = (Spinner)findViewById(R.id.stateSharedSpinner);
        sendLogScrollView   = (ScrollView)findViewById(R.id.sendLogScrollView);

        locLogAutoComplete.setThreshold(3);
        inspectorCommentLay.setVisibility(View.VISIBLE);

        if (CurrentCycleId.equals(globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(globally.USA_WORKING_7_DAYS) ) {
            MaxDays = UsaMaxDays;
        }else{
            MaxDays = CanMaxDays;
        }

        ArrayAdapter countryAdapter = new ArrayAdapter(getContext(), R.layout.item_sharelog_spinner, R.id.shareLogSpinTV,
                getContext().getResources().getStringArray(R.array.country_array));
        countrySpinner.setAdapter(countryAdapter);


        getOfflineAddress();

        inspCmntEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length() > 60) {
                    inspCmntEditTxt.setError("Allows 60 characters only");
                    inspCmntEditTxt.setText(inspCmntEditTxt.getText().toString().substring(0, 59));
                }else{
                    inspCmntEditTxt.setError(null);
                }
            }
        });



        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                SelectedCountry = position;
                    switch (position){
                        case 0:
                            countryFlagImgView.setImageResource(R.drawable.no_flag);
                            canView();
                            selectedCountry = "Select";
                           // fmcsaLogTxtVw.setText(getContext().getResources().getString(R.string.eld_govt_logs));
                            break;

                        case 1:
                            countryFlagImgView.setImageResource(R.drawable.can_flag);
                            canView();
                            selectedCountry = "CAN";
                            break;

                        case 2:
                            countryFlagImgView.setImageResource(R.drawable.usa_flag);
                            usaView();
                            selectedCountry = "USA";
                            break;

                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setMinMaxDateOnView(MaxDays, true);



        if(IsAOBRD){
            shareServiceDialog.setVisibility(View.GONE);
            fmcsaDescTV.setVisibility(View.GONE);
            AobrdSharedLocLay.setVisibility(View.VISIBLE);
            sharedLocLay.setVisibility(View.GONE);

            if (StateArrayList.size() > 0) {
                // Creating adapter for spinner
                ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, StateArrayList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateSharedSpinner.setAdapter(dataAdapter);


                // Spinner click listener
                stateSharedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        Country = "";
                        if(position < StateList.size()) {
                            Country = StateList.get(position).getCountry();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }else{
            if(globally.isConnected(getContext())) {
                  GetAddFromLatLng(Globally.LATITUDE, Globally.LONGITUDE);
            }

            int obdStatus       = SharedPref.getObdStatus(getContext());
            if(obdStatus == Constants.BLE_CONNECTED || obdStatus == Constants.WIRED_CONNECTED || obdStatus == Constants.WIFI_CONNECTED){
                // OBD connected with ECM
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        globally.DriverSwitchAlert(getContext(), getContext().getString(R.string.Limited_ELD_),
                                getContext().getString(R.string.limited_ecm_desc), "Ok");
                    }
                }, 600);

            }
        }


        shareLogMainLay.setOnClickListener(this);
        shareLogChildLay.setOnClickListener(this);
        shareDriverLogBtn.setOnClickListener(new ShareBtnClick());
        startDateTv.setOnClickListener(this);
        endDateTv.setOnClickListener(this);
        changeLocLogBtn.setOnClickListener(this);
        cancelDriverLogBtn.setOnClickListener(new CancelDialog());
        amountEditText.setOnClickListener(new EditTextListener());
        fmcsaLogBtn.setOnClickListener(this);
        pdfLogBtn.setOnClickListener(this);


    }


    void setMinMaxDateOnView(int MaxDays, boolean isOnCreate){
        String currentDate = globally.GetCurrentDeviceDate(null, globally, getContext());
        String currentDateStr = globally.ConvertDateFormat(currentDate);
        DateTime selectedDateTime = globally.getDateTimeObj(currentDateStr, false);
        selectedDateTime = selectedDateTime.minusDays(MaxDays);
        String fromDate = globally.ConvertDateFormatMMddyyyy(selectedDateTime.toString());

        try {
            startDateTv.setText(fromDate);
            StartDate = format.parse(fromDate);

            if(isOnCreate) {
                endDateTv.setText(currentDate);
                EndDate = format.parse(currentDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void canView(){
       // fmcsaLogTxtVw.setText(getContext().getResources().getString(R.string.eld_govt_logs));
        dataTransTxtView.setText(getContext().getResources().getString(R.string.Data_transmission));
        canEmailEditText.setVisibility(View.VISIBLE);
        checkboxService.setVisibility(View.GONE);
        checkboxService.setChecked(false);
        checkboxEmail.setChecked(true);
        fmcsaDescTV.setVisibility(View.GONE);

        setMinMaxDateOnView(CanMaxDays, false);

       // getOfflineAddress();

    }

    void usaView(){
       // fmcsaLogTxtVw.setText(getContext().getResources().getString(R.string.fmcsaLogs));
        dataTransTxtView.setText(getContext().getResources().getString(R.string.DataTransmissionThrough));
        canEmailEditText.setVisibility(View.GONE);
        checkboxService.setVisibility(View.VISIBLE);
        checkboxService.setChecked(true);
        canEmailEditText.setText("");
        checkboxEmail.setChecked(false);
        if(emailLogLay.getVisibility() == View.VISIBLE) {
            fmcsaDescTV.setVisibility(View.GONE);
        }else{
            fmcsaDescTV.setVisibility(View.VISIBLE);
        }

        setMinMaxDateOnView(UsaMaxDays, false);
       // locLogAutoComplete.setText(Globally.LATITUDE + "," + Globally.LONGITUDE);

    }


    private class EditTextListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String data = amountEditText.getText().toString().trim();
            amountEditText.setText(data);
            amountEditText.setSelection(data.length());
        }
    }



    private class CancelDialog implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            dismiss();
        }
    }

    private boolean isValidEmailPattern(EditText emailText){
            String email = emailText.getText().toString().trim();
            email = email.replaceAll(" ", ",");
            email = email.replaceAll(",,", ",");
            String[] EmailArray = email.split(",");
            List<String> validEmailList = new ArrayList<>();
            boolean IsValidEmail = false;
            String EmailData = "";
            for(int i = 0 ; i < EmailArray.length ; i++){
                if(EmailArray[i].matches(emailPattern) || EmailArray[i].matches(emailPattern2)){
                    //IsValidEmail = true;
                    validEmailList.add("true");
                    EmailData = EmailData + "<u><font color='blue'>" + EmailArray[i].trim() +"</font></u> " ;
                }else{
                    //IsValidEmail = false;
                    validEmailList.add("false");
                    EmailData = EmailData + "<font color='red'>" + EmailArray[i].trim() +"</font> " ;
                }
            }

            if(validEmailList.toString().contains("false")){
                IsValidEmail = false;
            }else{
                IsValidEmail = true;
            }
            emailText.setText(Html.fromHtml(EmailData));
            emailText.setSelection(emailText.getText().toString().length());

            return IsValidEmail;
    }


    private class ShareBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            //hMethods.isActionAllowedWhileDriving(getContext(), globally, DRIVER_ID, true, dbHelper);
            boolean isActionAllowed = hMethods.isActionAllowedWhileMoving(getContext(), globally, DRIVER_ID, dbHelper);
            if(isActionAllowed) {
                if (globally.isConnected(getContext())) {

                    int offlineDataLength = constant.OfflineData(getContext(), MainDriverPref,
                            CoDriverPref, globally.isSingleDriver(getContext()));
                    if (offlineDataLength == 0 ) {
                        City = cityShareEditText.getText().toString().trim();
                        String MailCheck = String.valueOf(checkboxEmail.isChecked());
                        String ServiceCheck = String.valueOf(checkboxService.isChecked());

                        email = amountEditText.getText().toString().trim();
                        canSelectedEmail = canEmailEditText.getText().toString().trim();

                        if (selectedCountry.equals("Select")) {
                            sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                            globally.EldScreenToast(shareDriverLogBtn, getContext().getResources().getString(R.string.select_country), getContext().getResources().getColor(R.color.colorVoilation));
                        } else {

                            if (emailLogLay.getVisibility() == View.VISIBLE) {
                                if (isValidEmailPattern(amountEditText)) {
                                    CheckValdation0(MailCheck, ServiceCheck);
                                } else {
                                    sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                                    amountEditText.requestFocus();
                                    globally.EldScreenToast(shareDriverLogBtn, getContext().getResources().getString(R.string.enter_valid_email), getContext().getResources().getColor(R.color.colorVoilation));
                                }
                            } else {
                                if (selectedCountry.equals("CAN")) {

                                    if (isValidEmailPattern(canEmailEditText)) {
                                        if (checkboxEmail.isChecked()) {
                                            ServiceCheck = "false";
                                            CheckValdation0(MailCheck, ServiceCheck);
                                        } else {
                                            sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                                            globally.EldScreenToast(shareDriverLogBtn, "Please select Email for data transmission.", getContext().getResources().getColor(R.color.colorVoilation));
                                        }
                                    } else {
                                        sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                                        canEmailEditText.requestFocus();
                                        globally.EldScreenToast(shareDriverLogBtn, getContext().getResources().getString(R.string.enter_valid_email), getContext().getResources().getColor(R.color.colorVoilation));
                                    }
                                } else {
                                    if (checkboxEmail.isChecked() || checkboxService.isChecked()) {
                                        CheckValdation0(MailCheck, ServiceCheck);
                                    } else {
                                        sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                                        globally.EldScreenToast(shareDriverLogBtn, "Please select Web Service or Email", getContext().getResources().getColor(R.color.colorVoilation));
                                    }
                                }
                            }
                        }

                    }else{
                        globally.EldScreenToast(shareDriverLogBtn, getContext().getString(R.string.please_wait_data_syncing), getContext().getResources().getColor(R.color.colorVoilation));
                    }


                }else{
                    globally.EldScreenToast(shareDriverLogBtn, globally.CHECK_INTERNET_MSG, getContext().getResources().getColor(R.color.colorVoilation));
                }


            }else{
                globally.EldScreenToast(shareDriverLogBtn,  "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                getContext().getResources().getString(R.string.stop_vehicle_alert),
                       getContext().getResources().getColor(R.color.colorVoilation));
            }
        }
    }


    void CheckValdation0(String MailCheck, String ServiceCheck){
        if(IsAOBRD){
            if(City.length() > 0){
                DriverViewValidations(MailCheck, ServiceCheck);
            }else{
                sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                cityShareEditText.requestFocus();
                globally.EldScreenToast(shareDriverLogBtn, "Enter city name.", getContext().getResources().getColor(R.color.colorVoilation));
            }
        }else{
            DriverViewValidations(MailCheck, ServiceCheck);
        }
    }


    void HideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(amountEditText.getWindowToken(), 0);
        } catch (Exception e) {   }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.changeLocLogBtn:
                locLogAutoComplete.setEnabled(true);
                locLogAutoComplete.requestFocus();
                locLogAutoComplete.setFocusableInTouchMode(true);
                locLogAutoComplete.setSelection(locLogAutoComplete.getText().toString().length());
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(locLogAutoComplete, InputMethodManager.SHOW_FORCED);

                break;

            case R.id.startDateTv:
                HideKeyboard();
                selectedDateView = "start";
               // logBtnLay.setVisibility(View.GONE);
               // ShareLayout.setVisibility(View.VISIBLE);

               // setCalendarView();
                ShowDateDialog();

                break;

            case R.id.endDateTv:
                HideKeyboard();
                selectedDateView = "end";
              // logBtnLay.setVisibility(View.GONE);
                //ShareLayout.setVisibility(View.VISIBLE);

               // setCalendarView();
                ShowDateDialog();

                break;


            case R.id.shareLogChildLay:

                HideKeyboard();

            break;

            case R.id.shareLogMainLay:
                HideKeyboard();
                break;


            case R.id.fmcsaLogBtn:
                fmcsaLogBtn.setBackgroundResource(R.drawable.voilet_left);
                pdfLogBtn.setBackgroundResource(R.drawable.white_right);
                checkboxService.setChecked(true);
                emailLogLay.setVisibility(View.GONE);
                amountEditText.setText("");
                shareServiceDialog.setVisibility(View.VISIBLE);
                fmcsaLogTxtVw.setTextColor(getContext().getResources().getColor(R.color.whiteee));
                pdfLogTxtVw.setTextColor(getContext().getResources().getColor(R.color.hos_remaining));

                if(SelectedCountry == CountryCan){
                    fmcsaDescTV.setVisibility(View.GONE);
                    checkboxEmail.setChecked(true);
                }else{
                    fmcsaDescTV.setVisibility(View.VISIBLE);
                }
                inspectorCommentLay.setVisibility(View.VISIBLE);

                break;


            case R.id.pdfLogBtn:
                fmcsaLogBtn.setBackgroundResource(R.drawable.white_left);
                pdfLogBtn.setBackgroundResource(R.drawable.voilet_right);
                checkboxEmail.setChecked(false);
                checkboxService.setChecked(false);
                emailLogLay.setVisibility(View.VISIBLE);
                shareServiceDialog.setVisibility(View.GONE);
                fmcsaDescTV.setVisibility(View.GONE);
                fmcsaLogTxtVw.setTextColor(getContext().getResources().getColor(R.color.hos_remaining));
                pdfLogTxtVw.setTextColor(getContext().getResources().getColor(R.color.whiteee));

                inspectorCommentLay.setVisibility(View.GONE);
                inspCmntEditTxt.setText("");

                break;

        }
    }




    void ShowDateDialog() {
        try {
            if (dateDialog != null && dateDialog.isShowing())
                dateDialog.dismiss();

            String cycleId = "";
            if(SelectedCountry == CountryCan){
                cycleId = "1";
            }else{
                cycleId = "3";
            }

            dateDialog = new DatePickerDialog(getContext(), cycleId + ",sendLog",
                    globally.GetCurrentDeviceDate(null, globally, getContext()), new DateListener(), false);
            dateDialog.show();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    private class DateListener implements DatePickerDialog.DatePickerListener {
        @Override
        public void JobBtnReady(String SelectedDate, String dayOfTheWeek, String MonthFullName, String MonthShortName, int dayOfMonth) {

            try {   //01/27/2021
                if (dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }


            try {
                if(selectedDateView.equals("start")) {
                    startDateTv.setText(SelectedDate);
                    StartDate = format.parse(SelectedDate);
                }else{
                    endDateTv.setText(SelectedDate);
                    EndDate = format.parse(SelectedDate);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }




    AlertDialogEld.PositiveButtonCallback positiveCallBack = new AlertDialogEld.PositiveButtonCallback() {
        @Override
        public void getPositiveClick(int flag) {
            String MailCheck = String.valueOf(checkboxEmail.isChecked());
            String ServiceCheck = String.valueOf(checkboxService.isChecked());

            SendDriverLog(DRIVER_ID, DeviceId, startDateTv.getText().toString(),
                    endDateTv.getText().toString(), email, inspCmntEditTxt.getText().toString().trim(),
                    MailCheck, ServiceCheck, Globally.LATITUDE, Globally.LONGITUDE,
                    SharedPref.getTimeZone(getContext()));
        }
    };

    AlertDialogEld.NegativeButtonCallBack negativeCallBack = new AlertDialogEld.NegativeButtonCallBack() {
        @Override
        public void getNegativeClick(int flag) {
            Logger.LogDebug("negativeCallBack", "negativeCallBack: " + flag);
            inspCmntEditTxt.requestFocus();
        }
    };



    void DriverViewValidations(String MailCheck, String ServiceCheck){
        if (startDateTv.getText().toString().length() > 0) {
            if (endDateTv.getText().toString().length() > 0) {
                HideKeyboard();
                if (EndDate.after(StartDate) || EndDate.equals(StartDate)) {
                    String insComments = inspCmntEditTxt.getText().toString().trim();

                    if(emailLogLay.getVisibility() == View.VISIBLE && SelectedCountry == CountryCan) {
                        SendDriverLog(DRIVER_ID, DeviceId, startDateTv.getText().toString(),
                                endDateTv.getText().toString(), email, insComments,
                                MailCheck, ServiceCheck, Globally.LATITUDE, Globally.LONGITUDE,
                                SharedPref.getTimeZone(getContext()));
                    }else{
                        if(insComments.length() == 0) {

                             if(inspectorCommentLay.getVisibility() == View.VISIBLE) {
                                 statusEndConfDialog.ShowAlertDialog(getContext().getString(R.string.Confirmation_suggested),
                                         getContext().getString(R.string.confirm_witout_comment),
                                         getContext().getString(R.string.yes), getContext().getString(R.string.no),
                                         0, positiveCallBack, negativeCallBack);
                             }else{
                                 SendDriverLog(DRIVER_ID, DeviceId, startDateTv.getText().toString(),
                                         endDateTv.getText().toString(), email, insComments,
                                         MailCheck, ServiceCheck, Globally.LATITUDE, Globally.LONGITUDE,
                                         SharedPref.getTimeZone(getContext()));
                             }
                        }else if(insComments.length() < 4){
                            globally.EldScreenToast(shareDriverLogBtn, "Enter minimum 4 char", getContext().getResources().getColor(R.color.colorVoilation));
                            inspCmntEditTxt.setError("Enter minimum 4 char");
                        }else if(insComments.length() > 60) {
                            globally.EldScreenToast(shareDriverLogBtn, "Allows 60 characters only", getContext().getResources().getColor(R.color.colorVoilation));
                            inspCmntEditTxt.setError("Allows 60 characters only");
                        }else {
                            SendDriverLog(DRIVER_ID, DeviceId, startDateTv.getText().toString(),
                                    endDateTv.getText().toString(), email, insComments,
                                    MailCheck, ServiceCheck, Globally.LATITUDE, Globally.LONGITUDE,
                                    SharedPref.getTimeZone(getContext()));


                        }
                    }

                } else {
                    globally.EldScreenToast(shareDriverLogBtn, "(To Date) should be greater then (From Date).", getContext().getResources().getColor(R.color.colorVoilation));
                }
            } else {
                globally.EldScreenToast(shareDriverLogBtn, "Select (To Date) first.", getContext().getResources().getColor(R.color.colorVoilation));
            }
        } else {
            globally.EldScreenToast(shareDriverLogBtn, "Select (From Date) first.", getContext().getResources().getColor(R.color.colorVoilation));
        }
    }




    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            Logger.LogDebug("response", "Driver response: " + response);

            try {

                JSONObject obj = new JSONObject(response);
                String status = obj.getString("Status");

                if(status.equalsIgnoreCase("true")) {


                    if (!obj.isNull("Data")) {
                        JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                        City = dataJObject.getString(ConstantsKeys.City);
                        Country = dataJObject.getString(ConstantsKeys.Country);
                        LocationFromApi = dataJObject.getString(ConstantsKeys.Location);

                        if (Country.contains("China") || Country.contains("Russia") || Country.contains("null")) {
                            LocationFromApi = Globally.LATITUDE + "," + Globally.LONGITUDE;
                        } else {
                            if (Country.length() > 0 && !LocationFromApi.contains(Country)) {
                                LocationFromApi = dataJObject.getString(ConstantsKeys.Location) + ", " + Country;
                            }
                        }

                        locLogAutoComplete.setText(LocationFromApi);

                    }
                }else{
                    getOfflineAddress();
                }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
           Logger.LogDebug("error", "error");
            getOfflineAddress();
        }
    };



    private void getOfflineAddress(){
        String AddressLine = "";
        if(LocationFromApi.length() == 0) {
            if ((CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2))
                    && !SharedPref.IsAOBRD(getContext())) {
                AddressLine = csvReader.getShortestAddress(getContext());
            } else {
                if (Globally.LATITUDE.length() > 4) {
                    AddressLine = Globally.LATITUDE + "," + Globally.LONGITUDE;
                }
            }
            locLogAutoComplete.setText(AddressLine);
        }else{
            locLogAutoComplete.setText(LocationFromApi);
        }
    }



    //*================== Get Address From Lat Lng ===================*//*
    void GetAddFromLatLng(String lat, String lon) {

        Map<String, String> params = new HashMap<String, String>();
         params.put(ConstantsKeys.Latitude, lat);
         params.put(ConstantsKeys.Longitude, lon );

        GetAddFromLatLngRequest.executeRequest(Request.Method.POST, APIs.GET_Add_FROM_LAT_LNG, params, 1,
                Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);
    }



    private void SendDriverLog( final String DriverId, final String DeviceId, final String StartDate,
                                final String EndDate, final String emails, final String InspectorComment,
                                final String IsMail, final String IsService, final String latitude,
                                final String longitude, final String DriverTimeZone){

        shareDriverLogBtn.setEnabled(false);

        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Sending ...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.SEND_LOG,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Logger.LogDebug("Response ", ">>>Response: " + response);

                        pDialog.dismiss();
                        shareDriverLogBtn.setEnabled(true);

                        try {
                            JSONObject dataObj;
                            JSONObject obj = new JSONObject(response);
                            String message = "Sending failed.";
                            if(obj.has("Message")){
                                message = obj.getString("Message");
                            }

                            if ( obj.getString("Status").equals("true")) {

                                if(getContext() != null) {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                }

                                if (!obj.isNull("Data")) {
                                    dataObj = new JSONObject(obj.getString("Data"));
                                    String filePath = dataObj.getString("FilePath");
                                    DriverLogPDFViewConfirmation(getContext(), filePath, message );
                                }

                                dismiss();

                            }else{
                                if(message.equals("null")){
                                    message = "Error";
                                }
                                globally.EldToastWithDuration4Sec(shareDriverLogBtn, message, getContext().getResources().getColor(R.color.colorVoilation));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.LogDebug("error ", "---error: " + error);
                        shareDriverLogBtn.setEnabled(true);

                        globally.EldToastWithDuration4Sec(shareDriverLogBtn, "Error", getContext().getResources().getColor(R.color.colorVoilation));
                        pDialog.dismiss();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put(ConstantsKeys.DriverId, DriverId);
                params.put(ConstantsKeys.DeviceId, DeviceId);
                params.put(ConstantsKeys.fromDate, StartDate);
                params.put(ConstantsKeys.toDate, EndDate);
                params.put(ConstantsKeys.mailToIds, emails);
                params.put(ConstantsKeys.InspectorComment, InspectorComment);
                params.put(ConstantsKeys.IsMail, IsMail);
                params.put(ConstantsKeys.IsService, IsService);
                params.put(ConstantsKeys.latitude, latitude);
                params.put(ConstantsKeys.longitude, longitude);
                params.put(ConstantsKeys.DriverTimeZone, DriverTimeZone);
                params.put(ConstantsKeys.Country, selectedCountry );    // for canada view
                params.put(ConstantsKeys.EmailAddress, canSelectedEmail);   // for canada view

                Logger.LogDebug("API TAG", ">>>SendDriverLog");

                return params;
            }
        };

        int socketTimeout = 40000;   //40 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);

    }




    public void DriverLogPDFViewConfirmation(final Context context, final String link, String message){
        try {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            String dismissBtnName;
            if(!link.equals("Please check your email")) {
                dismissBtnName = "Dismiss";
                alertDialogBuilder.setTitle("You can check driver log in this link.");    //Driver Log !!
                alertDialogBuilder.setMessage(Html.fromHtml("<html> <font color='blue'><u>" + link + "</u></font> </html>") );    //"Do you want to see driver log ?"
                alertDialogBuilder.setCancelable(false);

                alertDialogBuilder.setPositiveButton("Open",
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {

                                try {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(link));
                                    context.startActivity(i);
                                } catch (Exception e) {
                                    if (getContext() != null) {
                                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.no_viewer_found), Toast.LENGTH_LONG).show();
                                    }
                                    e.printStackTrace();
                                }


                                dialog.dismiss();


                            }
                        });
            }else{
                alertDialogBuilder.setTitle("Success !!");    //Driver Log !!
                alertDialogBuilder.setMessage(message);
                alertDialogBuilder.setCancelable(false);

                dismissBtnName = "Ok";
            }

            alertDialogBuilder.setNegativeButton(dismissBtnName, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            if(UILApplication.getInstance().isNightModeEnabled()) {
                alertDialog.getWindow().setBackgroundDrawableResource(R.color.layout_color_dot);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.white));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.white));
            }

        }catch (Exception e){e.printStackTrace();}
    }



}
