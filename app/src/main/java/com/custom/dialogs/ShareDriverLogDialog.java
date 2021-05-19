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
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.DatePicker;
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
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.EldFragment;
import com.models.DriverLocationModel;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    String LATITUDE  = "", LONGITUDE = "";
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
    LinearLayout emailLogLay, logBtnLay, shareLogMainLay, shareLogChildLay, shareServiceDialog, countryLayout;
    RelativeLayout sharedLocLay, AobrdSharedLocLay;
    String selectedDateView = "", email = "";
    FragmentActivity activity;
    ProgressBar sendLogProgressBar;
    DatePickerDialog dateDialog;

    int CountryNoSelection  = 0;
    int CountryCan          = 1;
    int CountryUsa          = 2;
    int UsaMaxDays          = 7;    // 1 + 7  = 8 days
    int CanMaxDays          = 13;   // 1 = 13  = 14 days
    int MaxDays, SelectedCountry = 0;
    boolean IsAOBRD;
    private String City = "", State = "", Country = "", canSelectedEmail = "", selectedCountry = "Select";
    List<DriverLocationModel> StateList;
    List<String> StateArrayList;
    Spinner stateSharedSpinner;
    ScrollView sendLogScrollView;
    VolleyRequest GetAddFromLatLngRequest;


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

        GetAddFromLatLngRequest = new VolleyRequest(getContext());


        LATITUDE    = Globally.LATITUDE;
        LONGITUDE   = Globally.LONGITUDE;

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

        fmcsaLogBtn         = (RelativeLayout) findViewById(R.id.fmcsaLogBtn);
        pdfLogBtn           = (RelativeLayout)findViewById(R.id.pdfLogBtn);
        AobrdSharedLocLay   = (RelativeLayout)findViewById(R.id.AobrdSharedLocLay);
        sharedLocLay        = (RelativeLayout)findViewById(R.id.sharedLocLay);

        stateSharedSpinner  = (Spinner)findViewById(R.id.stateSharedSpinner);
        sendLogScrollView   = (ScrollView)findViewById(R.id.sendLogScrollView);

        locLogAutoComplete.setThreshold(3);
      //  countryLayout.setVisibility(View.GONE);

        if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS) ) {
            MaxDays = UsaMaxDays;
        }else{
            MaxDays = CanMaxDays;
        }

        ArrayAdapter countryAdapter = new ArrayAdapter(getContext(), R.layout.item_editlog_spinner, R.id.editlogSpinTV,
                getContext().getResources().getStringArray(R.array.country_array));
        countrySpinner.setAdapter(countryAdapter);



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
                            usaView();
                            selectedCountry = "Select";
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

                        if(position < StateList.size()) {
                            State = StateList.get(position).getState();
                            Country = StateList.get(position).getCountry();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }else{
            if(Globally.isConnected(getContext())) {
                  GetAddFromLatLng(LATITUDE, LONGITUDE);
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
        String currentDate = Globally.GetCurrentDeviceDate();
        String currentDateStr = Globally.ConvertDateFormat(currentDate);
        DateTime selectedDateTime = new DateTime(Globally.getDateTimeObj(currentDateStr, false) );
        selectedDateTime = selectedDateTime.minusDays(MaxDays);
        String fromDate = Globally.ConvertDateFormatMMddyyyy(selectedDateTime.toString());

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
        fmcsaLogTxtVw.setText(getContext().getResources().getString(R.string.eld_govt_logs));
        dataTransTxtView.setText(getContext().getResources().getString(R.string.Data_transmission));
        canEmailEditText.setVisibility(View.VISIBLE);
        checkboxService.setVisibility(View.GONE);
        checkboxService.setChecked(false);
        checkboxEmail.setChecked(true);
        fmcsaDescTV.setVisibility(View.GONE);

        setMinMaxDateOnView(CanMaxDays, false);


    }

    void usaView(){
        fmcsaLogTxtVw.setText(getContext().getResources().getString(R.string.fmcsaLogs));
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
            City  = cityShareEditText.getText().toString().trim();
            String MailCheck    = String.valueOf(checkboxEmail.isChecked());
            String ServiceCheck = String.valueOf(checkboxService.isChecked());

            email               = amountEditText.getText().toString().trim();
            canSelectedEmail    = canEmailEditText.getText().toString().trim();

            if(selectedCountry.equals("Select")){
                sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                Globally.EldScreenToast(shareDriverLogBtn, getContext().getResources().getString(R.string.select_country), getContext().getResources().getColor(R.color.colorVoilation));
            }else {
                if (Globally.isConnected(getContext())) {
                    if (emailLogLay.getVisibility() == View.VISIBLE) {
                        if (isValidEmailPattern(amountEditText)) {
                            CheckValdation0(MailCheck, ServiceCheck);
                        } else {
                            sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                            amountEditText.requestFocus();
                            Globally.EldScreenToast(shareDriverLogBtn, getContext().getResources().getString(R.string.enter_valid_email), getContext().getResources().getColor(R.color.colorVoilation));
                        }
                    } else {
                        if (selectedCountry.equals("CAN")){
                            if(isValidEmailPattern(canEmailEditText)){
                                ServiceCheck = "false";
                                CheckValdation0(MailCheck, ServiceCheck);
                            }else{
                                sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                                canEmailEditText.requestFocus();
                                Globally.EldScreenToast(shareDriverLogBtn, getContext().getResources().getString(R.string.enter_valid_email), getContext().getResources().getColor(R.color.colorVoilation));
                            }
                        }else {
                            if (checkboxEmail.isChecked() || checkboxService.isChecked()) {
                                CheckValdation0(MailCheck, ServiceCheck);
                            } else {
                                sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                                Globally.EldScreenToast(shareDriverLogBtn, "Please select Web Service or Email", getContext().getResources().getColor(R.color.colorVoilation));
                            }
                        }
                    }

                } else {
                    Globally.EldScreenToast(shareDriverLogBtn, Globally.CHECK_INTERNET_MSG, getContext().getResources().getColor(R.color.colorVoilation));
                }
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
                Globally.EldScreenToast(shareDriverLogBtn, "Enter city name.", getContext().getResources().getColor(R.color.colorVoilation));
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

            dateDialog = new DatePickerDialog(getContext(), cycleId + ",sendLog", Globally.GetCurrentDeviceDate(), new DateListener());
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



    void DriverViewValidations(String MailCheck, String ServiceCheck){
        if (startDateTv.getText().toString().length() > 0) {
            if (endDateTv.getText().toString().length() > 0) {
                HideKeyboard();
                if (EndDate.after(StartDate) || EndDate.equals(StartDate)) {
                    String insComments = inspCmntEditTxt.getText().toString().trim();
                    if(insComments.length() <= 60) {
                        SendDriverLog(DRIVER_ID, DeviceId, startDateTv.getText().toString(),
                                endDateTv.getText().toString(), email, insComments,
                                MailCheck, ServiceCheck, LATITUDE, LONGITUDE,
                                SharedPref.getTimeZone(getContext()));
                    }else {
                        Globally.EldScreenToast(shareDriverLogBtn, "Allows 60 characters only", getContext().getResources().getColor(R.color.colorVoilation));
                        inspCmntEditTxt.setError("Allows 60 characters only");
                    }
                } else {
                    Globally.EldScreenToast(shareDriverLogBtn, "(To Date) should be greater then (From Date).", getContext().getResources().getColor(R.color.colorVoilation));
                }
            } else {
                Globally.EldScreenToast(shareDriverLogBtn, "Select (To Date) first.", getContext().getResources().getColor(R.color.colorVoilation));
            }
        } else {
            Globally.EldScreenToast(shareDriverLogBtn, "Select (From Date) first.", getContext().getResources().getColor(R.color.colorVoilation));
        }
    }




    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            Log.d("response", "Driver response: " + response);

            try {

                JSONObject obj = new JSONObject(response);
                String status = obj.getString("Status");

                if(status.equalsIgnoreCase("true")) {


                    if (!obj.isNull("Data")) {
                        JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                        City = dataJObject.getString(ConstantsKeys.City);
                        State = dataJObject.getString(ConstantsKeys.State);
                        Country = dataJObject.getString(ConstantsKeys.Country);
                        String Location = dataJObject.getString(ConstantsKeys.Location);

                        if (Country.contains("China") || Country.contains("Russia") || Country.contains("null")) {
                            Location = LATITUDE + ","+LONGITUDE;
                        } else {
                            if (!Location.contains(Country)) {
                                Location = dataJObject.getString(ConstantsKeys.Location) + ", " + Country;
                            }
                        }

                        locLogAutoComplete.setText(Location);


                    }
                }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
           Log.d("error", "error");
        }
    };





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
                        Log.d("Response ", "---Response: " + response);

                        pDialog.dismiss();
                       // sendLogProgressBar.setVisibility(View.GONE);
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
                                    DriverLogPDFViewConfirmation(getContext(), filePath );
                                }

                                dismiss();

                            }else{
                                Globally.EldScreenToast(shareDriverLogBtn, message, getContext().getResources().getColor(R.color.colorVoilation));
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
                        Log.d("error ", "---error: " + error);
                       // sendLogProgressBar.setVisibility(View.GONE);
                        Globally.EldScreenToast(shareDriverLogBtn, "Error", getContext().getResources().getColor(R.color.colorVoilation));
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

                return params;
            }
        };

        int socketTimeout = 40000;   //40 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);

    }




    public void DriverLogPDFViewConfirmation(final Context context, final String link){
        try {
          //  AlertDialog alertDialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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
                            }catch (Exception e){
                                if(getContext() != null) {
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.no_viewer_found), Toast.LENGTH_LONG).show();
                                }
                                e.printStackTrace();
                            }



                            dialog.dismiss();


                        }
                    });

            alertDialogBuilder.setNegativeButton("Dismiss", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }



}
