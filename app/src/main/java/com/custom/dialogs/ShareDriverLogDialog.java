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
import android.text.Html;
import android.util.Log;
import android.view.View;
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
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.DriverLocationModel;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    DatePicker datePicker;
    Date StartDate, EndDate;
    DateFormat format;
    Button selectButton , cancelDateBtn, changeLocLogBtn;
    LinearLayout cancelDriverLogBtn, shareDriverLogBtn;
    RelativeLayout fmcsaLogBtn, pdfLogBtn;
    EditText amountEditText, inspCmntEditTxt, cityShareEditText;
    CheckBox checkboxEmail, checkboxService;
    AutoCompleteTextView locLogAutoComplete;
    TextView startDateTv ,endDateTv, fmcsaDescTV, fmcsaLogTxtVw, pdfLogTxtVw;
    LinearLayout ShareLayout, emailLogLay, logBtnLay, shareLogMainLay, shareLogChildLay, shareServiceDialog;
    RelativeLayout sharedLocLay, AobrdSharedLocLay;
    String selectedDateView = "", email = "";
    FragmentActivity activity;
    ProgressBar sendLogProgressBar;
    int UsaMaxDays          = 7;    // 1 + 7  = 8 days
    int CanMaxDays          = 13;   // 1 = 13  = 14 days
    int MaxDays;
    boolean IsAOBRD;
    private String City = "", State = "", Country = "";
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
        setCancelable(false);

        datePicker=(DatePicker)findViewById(R.id.ShareDatePicker);
        Calendar calendar = Calendar.getInstance();
        //  calendar.add(Calendar.YEAR, -18);
        format = new SimpleDateFormat("MM/dd/yyyy");
        EndDate = new Date();
        StartDate = new Date();
        Date dateee = calendar.getTime();
        long mills = dateee.getTime();
        datePicker.setMaxDate(mills);
        GetAddFromLatLngRequest = new VolleyRequest(getContext());


        LATITUDE    = Globally.LATITUDE;
        LONGITUDE   = Globally.LONGITUDE;

        if(CurrentCycleId.equals("1") || CurrentCycleId.equals("2") || CurrentCycleId.equals("null")){
            calendar.add(Calendar.DAY_OF_MONTH, -13);   // Last 14 Days
        }else{
            calendar.add(Calendar.DAY_OF_MONTH, -7);   // Last 7 Days
        }
        Date mindate = calendar.getTime();
        datePicker.setMinDate(mindate.getTime());

        checkboxEmail       = (CheckBox)findViewById(R.id.checkboxEmail);
        checkboxService     = (CheckBox)findViewById(R.id.checkboxService);
        sendLogProgressBar  = (ProgressBar)findViewById(R.id.sendLogProgressBar);

        cancelDateBtn       = (Button)findViewById(R.id.cancelDateBtn);
        selectButton        = (Button)findViewById(R.id.ShareDateBtn);
        changeLocLogBtn     = (Button)findViewById(R.id.changeLocLogBtn);

        cancelDriverLogBtn  = (LinearLayout)findViewById(R.id.cancelDriverLogBtn);
        shareDriverLogBtn   = (LinearLayout) findViewById(R.id.shareDriverLogBtn);

        locLogAutoComplete  = (AutoCompleteTextView)findViewById(R.id.locLogAutoComplete);
        amountEditText      = (EditText)findViewById(R.id.amountEditText);
        inspCmntEditTxt     = (EditText)findViewById(R.id.inspCmntEditTxt);
        cityShareEditText   = (EditText)findViewById(R.id.cityShareEditText);

        startDateTv         = (TextView) findViewById(R.id.startDateTv);
        endDateTv           = (TextView) findViewById(R.id.endDateTv);
        fmcsaDescTV         = (TextView) findViewById(R.id.fmcsaDescTV);
        fmcsaLogTxtVw       = (TextView) findViewById(R.id.fmcsaLogTxtVw);
        pdfLogTxtVw         = (TextView) findViewById(R.id.pdfLogTxtVw);

        ShareLayout         = (LinearLayout)findViewById(R.id.ShareLayout);
        emailLogLay         = (LinearLayout)findViewById(R.id.emailLogLay);
        logBtnLay           = (LinearLayout)findViewById(R.id.logBtnLay);
        shareLogMainLay     = (LinearLayout)findViewById(R.id.shareLogMainLay);
        shareLogChildLay    = (LinearLayout)findViewById(R.id.shareLogChildLay);
        shareServiceDialog  = (LinearLayout)findViewById(R.id.shareServiceDialog);

        fmcsaLogBtn         = (RelativeLayout) findViewById(R.id.fmcsaLogBtn);
        pdfLogBtn           = (RelativeLayout)findViewById(R.id.pdfLogBtn);
        AobrdSharedLocLay   = (RelativeLayout)findViewById(R.id.AobrdSharedLocLay);
        sharedLocLay        = (RelativeLayout)findViewById(R.id.sharedLocLay);

        stateSharedSpinner  = (Spinner)findViewById(R.id.stateSharedSpinner);
        sendLogScrollView   = (ScrollView)findViewById(R.id.sendLogScrollView);

        locLogAutoComplete.setThreshold(3);



        if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS) ) {
            MaxDays = UsaMaxDays;
        }else{
            MaxDays = CanMaxDays;
        }

        String currentDate = Globally.GetCurrentDeviceDate();
        String currentDateStr = Globally.ConvertDateFormat(currentDate);
        DateTime selectedDateTime = new DateTime(Globally.getDateTimeObj(currentDateStr, false) );
        selectedDateTime = selectedDateTime.minusDays(MaxDays);
        String fromDate = Globally.ConvertDateFormatMMddyyyy(selectedDateTime.toString());

        startDateTv.setText(fromDate);
        endDateTv.setText(currentDate);

        try {
            StartDate = format.parse(fromDate);
            EndDate = format.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }



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


        selectButton.setOnClickListener(this);
        cancelDateBtn.setOnClickListener(this);
        ShareLayout.setOnClickListener(this);
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


    private class ShareBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            City  = cityShareEditText.getText().toString().trim();
            email = amountEditText.getText().toString().trim();
            email = email.replaceAll(" ", ",");
            email = email.replaceAll(",,", ",");
            String[] EmailArray = email.split(",");
            boolean IsValidEmail = false;
            String EmailData = "";
            String MailCheck    = String.valueOf(checkboxEmail.isChecked());
            String ServiceCheck = String.valueOf(checkboxService.isChecked());

            //
            for(int i = 0 ; i < EmailArray.length ; i++){
                if(EmailArray[i].matches(emailPattern) || EmailArray[i].matches(emailPattern2)){
                    IsValidEmail = true;
                    EmailData = EmailData + "<u><font color='blue'>" + EmailArray[i].trim() +"</font></u> " ;
                }else{
                    IsValidEmail = false;
                    EmailData = EmailData + "<font color='red'>" + EmailArray[i].trim() +"</font> " ;
                }
            }
            amountEditText.setText(Html.fromHtml(EmailData));
            //Log.d("Date", "StartDate: " +StartDate + "   ---EndDate: " +EndDate );
            amountEditText.setSelection(amountEditText.getText().toString().length());

            if(Globally.isConnected(getContext()) ) {
                if(emailLogLay.getVisibility() == View.VISIBLE){
                    if (IsValidEmail ) {
                        CheckValdation0(MailCheck, ServiceCheck);
                    }else{
                        sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                        amountEditText.requestFocus();
                        Globally.EldScreenToast(shareDriverLogBtn, "Please enter valid email address", getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    if(checkboxEmail.isChecked() || checkboxService.isChecked()){
                        CheckValdation0(MailCheck, ServiceCheck);
                    }else{
                        sendLogScrollView.fullScroll(ScrollView.FOCUS_UP);
                        Globally.EldScreenToast(shareDriverLogBtn, "Please select Web Service or Email", getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }

            }else{
                Globally.EldScreenToast(shareDriverLogBtn, Globally.CHECK_INTERNET_MSG, getContext().getResources().getColor(R.color.colorVoilation));
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
                logBtnLay.setVisibility(View.GONE);
                ShareLayout.setVisibility(View.VISIBLE);
                break;

            case R.id.endDateTv:
                HideKeyboard();
                selectedDateView = "end";
                logBtnLay.setVisibility(View.GONE);
                ShareLayout.setVisibility(View.VISIBLE);
                break;


            case R.id.cancelDateBtn:
                logBtnLay.setVisibility(View.VISIBLE);
                ShareLayout.setVisibility(View.GONE);
                break;

            case R.id.ShareLayout:
            case R.id.shareLogMainLay:
            case R.id.shareLogChildLay:

                HideKeyboard();

            break;



            case R.id.ShareDateBtn:
                int day=datePicker.getDayOfMonth();
                int year=datePicker.getYear();
                int mnth=datePicker.getMonth()+1;
                String month = "", dayyy = "";

                if(String.valueOf(mnth).length() == 1)
                    month = "0"+String.valueOf(mnth);
                else
                    month = String.valueOf(mnth);


                if(String.valueOf(day).length() == 1)
                    dayyy = "0"+String.valueOf(day);
                else
                    dayyy = String.valueOf(day);

                if(month.length() == 1)
                    month = "0"+month;

                String Date = month +"/"+ dayyy  +"/"+ year;
                //  Log.d("SelectedDate", "---SelectedDate" +Date);

                try {
                    //Log.d("SelectedDate", "---Format Date" +format.parse(Date));
                    if(selectedDateView.equals("start")) {
                        startDateTv.setText(Date);
                        StartDate = format.parse(Date);
                    }else{
                        endDateTv.setText(Date);
                        EndDate = format.parse(Date);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                logBtnLay.setVisibility(View.VISIBLE);
                ShareLayout.setVisibility(View.GONE);
                break;


            case R.id.fmcsaLogBtn:
                fmcsaLogBtn.setBackgroundResource(R.drawable.voilet_left);
                pdfLogBtn.setBackgroundResource(R.drawable.white_right);
                checkboxService.setChecked(true);
                emailLogLay.setVisibility(View.GONE);
                amountEditText.setText("");
                shareServiceDialog.setVisibility(View.VISIBLE);
                fmcsaDescTV.setVisibility(View.GONE);
                fmcsaLogTxtVw.setTextColor(getContext().getResources().getColor(R.color.whiteee));
                pdfLogTxtVw.setTextColor(getContext().getResources().getColor(R.color.hos_remaining));

                break;


            case R.id.pdfLogBtn:
                fmcsaLogBtn.setBackgroundResource(R.drawable.white_left);
                pdfLogBtn.setBackgroundResource(R.drawable.voilet_right);
                checkboxEmail.setChecked(false);
                checkboxService.setChecked(false);
                emailLogLay.setVisibility(View.VISIBLE);
                shareServiceDialog.setVisibility(View.GONE);
                fmcsaDescTV.setVisibility(View.VISIBLE);
                fmcsaLogTxtVw.setTextColor(getContext().getResources().getColor(R.color.hos_remaining));
                pdfLogTxtVw.setTextColor(getContext().getResources().getColor(R.color.whiteee));

                break;

        }
    }



    void DriverViewValidations(String MailCheck, String ServiceCheck){
        if (startDateTv.getText().toString().length() > 0) {
            if (endDateTv.getText().toString().length() > 0) {
                HideKeyboard();
                if (EndDate.after(StartDate) || EndDate.equals(StartDate)) {

                    SendDriverLog(DRIVER_ID, DeviceId, startDateTv.getText().toString(),
                            endDateTv.getText().toString(), email, inspCmntEditTxt.getText().toString(),
                            MailCheck, ServiceCheck, LATITUDE, LONGITUDE,
                            SharedPref.getTimeZone(getContext()));

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
        params.put("Latitude", lat);
        params.put("Longitude", lon );

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

                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
                params.put("DriverId", DriverId);
                params.put("DeviceId", DeviceId);
                params.put("fromDate", StartDate);
                params.put("toDate", EndDate);
                params.put("mailToIds", emails);
                params.put("InspectorComment", InspectorComment);
                params.put("IsMail", IsMail);
                params.put("IsService", IsService);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                params.put("DriverTimeZone", DriverTimeZone);
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
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.no_viewer_found), Toast.LENGTH_LONG).show();
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
