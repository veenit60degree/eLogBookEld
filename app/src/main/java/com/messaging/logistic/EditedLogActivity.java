package com.messaging.logistic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.adapter.logistic.TabLayoutAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.AlertDialogEld;
import com.constants.ConstantHtml;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.driver.details.EldDriverLogModel;
import com.google.android.material.tabs.TabLayout;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.messaging.logistic.fragment.EditedLogFragment;
import com.messaging.logistic.fragment.EldFragment;
import com.messaging.logistic.fragment.HosSummaryFragment;
import com.messaging.logistic.fragment.OriginalLogFragment;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditedLogActivity extends AppCompatActivity implements View.OnClickListener{

    public static List<EldDriverLogModel> editedLogList = new ArrayList<>();
    public static List<EldDriverLogModel> originalLogList = new ArrayList<>();
    public static JSONArray editedLogArray = new JSONArray();
    JSONArray editDataArray = new JSONArray();

    ImageView eldMenuBtn;
    TextView EldTitleTV;
    RelativeLayout rightMenuBtn;
    RelativeLayout eldMenuLay;
    ViewPager editedLogViewPager;
    TabLayout tabLayout;
    TabLayoutAdapter tabAdapter;

    String DefaultLine      = " <g class=\"event \">\n";
    String ViolationLine    = " <g class=\"event line-red\">\n";
    String htmlAppendedText = "";
    String TotalOnDutyHours         = "00:00";
    String TotalDrivingHours        = "00:00";
    String LeftCycleHours           = "00:00";
    String LeftDayDrivingHours      = "00:00";
    String TotalOffDutyHours        = "00:00";
    String TotalSleeperBerthHours   = "00:00";

    int hLineX1         = 0;
    int hLineX2         = 0;
    int hLineY          = 0;

    int vLineX          = 0;
    int vLineY1         = 0;
    int vLineY2         = 0;
    int offsetFromUTC   = 0;
    int startHour       = 0;
    int startMin        = 0;
    int endHour         = 0;
    int endMin          = 0;

    String DriverId, DeviceId;
    public HelperMethods hMethods;
    public DBHelper dbHelper;
    public SharedPref sharedPref;
    public Globally globally;
    public Constants constants;
    public static JSONArray driverLogArray;
    EditedLogFragment editedLogFragment;
    OriginalLogFragment originalLogFragment;
    CardView confirmCertifyBtn, cancelCertifyBtn;

    VolleyRequest GetEditedRecordRequest, claimLogRequest;
    Map<String, String> params;
    final int GetRecordFlag             = 101;
    final int CertifyRecordFlag         = 102;
    final int RejectRecordFlag          = 103;

    String AcceptedSuggestedRecord      = "1";
    String RejectSuggestedRecord        = "4";


    ProgressDialog progressDialog;

    public static String LogDate = "";
    public static DateTime currentDateTime, selectedUtcTime, selectedDateTime;

    AlertDialogEld confirmationDialog;

    private int[] tabIcons = {
            R.drawable.edit_log_icon,
            R.drawable.original_log_icon
    };


    public EditedLogActivity(){
        super();
        globally        = new Globally();
        hMethods        = new HelperMethods();
        dbHelper        = new DBHelper(this);
        sharedPref      = new SharedPref();
        constants       = new Constants();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_log_compare);

        globally        = new Globally();
        hMethods        = new HelperMethods();
        dbHelper        = new DBHelper(this);
        sharedPref      = new SharedPref();
        constants       = new Constants();

        GetEditedRecordRequest      = new VolleyRequest(this);
        claimLogRequest             = new VolleyRequest(this);
        progressDialog              = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");


        rightMenuBtn        = (RelativeLayout) findViewById(R.id.rightMenuBtn);
        eldMenuLay          = (RelativeLayout)findViewById(R.id.eldMenuLay);
        eldMenuBtn          = (ImageView)findViewById(R.id.eldMenuBtn);
        EldTitleTV          = (TextView) findViewById(R.id.EldTitleTV);

        editedLogViewPager  = (ViewPager) findViewById(R.id.editedLogPager);
        confirmCertifyBtn   = (CardView)findViewById(R.id.confirmCertifyBtn);
        cancelCertifyBtn    = (CardView)findViewById(R.id.cancelCertifyBtn);

        editedLogFragment   = new EditedLogFragment();
        originalLogFragment = new OriginalLogFragment();
        confirmationDialog  = new AlertDialogEld(this);

        offsetFromUTC = (int) globally.GetTimeZoneOffSet();
        currentDateTime = globally.getDateTimeObj(globally.GetCurrentDateTime(), false);
        LogDate = globally.GetCurrentDeviceDate();

        rightMenuBtn.setVisibility(View.INVISIBLE);
        eldMenuBtn.setImageResource(R.drawable.back_white);


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        DeviceId               = sharedPref.GetSavedSystemToken(this);
        DriverId               = sharedPref.getDriverId( this);


        try {
            driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
            driverLogArray = new JSONArray();
        }

        CheckSelectedDateTime(currentDateTime, LogDate);


        if(globally.isConnected(this)){
            GetSuggestedRecords(DriverId, DeviceId);
        }else{
            setPagetAdapter();
            globally.EldScreenToast(confirmCertifyBtn, globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
        }

        editedLogViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                highLightCurrentTab(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        eldMenuLay.setOnClickListener(this);
        confirmCertifyBtn.setOnClickListener(this);
        cancelCertifyBtn.setOnClickListener(this);

    }


    // Add Fragments to Tabs ViewPager for each Tabs
    private void setPagetAdapter(){
        tabAdapter = new TabLayoutAdapter(getSupportFragmentManager(), this);
        tabAdapter.addFragment(editedLogFragment, getString(R.string.edited_log), tabIcons[0]);
        tabAdapter.addFragment(originalLogFragment, getString(R.string.original_log), tabIcons[1]);
        editedLogViewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(editedLogViewPager);

        dateDescOnView(selectedDateTime.toString());

        highLightCurrentTab(0);
    }


    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(tabAdapter.getTabView(i));
        }
        TabLayout.Tab currentTab = tabLayout.getTabAt(position);
        assert currentTab != null;
        currentTab.setCustomView(null);
        currentTab.setCustomView(tabAdapter.getSelectedTabView(position));

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.eldMenuLay:
                finish();
                break;

            case R.id.confirmCertifyBtn:

                if(globally.isConnected(this)){
                    ClaimSuggestedRecords(DriverId, DeviceId, AcceptedSuggestedRecord, CertifyRecordFlag);
                }else{
                    globally.EldScreenToast(confirmCertifyBtn, globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                }
                break;

            case R.id.cancelCertifyBtn:
                if(globally.isConnected(this)){
                    confirmationDialog.ShowAlertDialog(getString(R.string.cancel_edit_record), getString(R.string.cancel_edit_record_desc),
                            getString(R.string.yes), getString(R.string.no),
                    101, positiveCallBack, negativeCallBack);
                }else{
                    globally.EldScreenToast(confirmCertifyBtn, globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                }
                break;

        }
    }



    String AddHorizontalLine(int hLineX1, int hLineX2, int hLineY){
        return "<line class=\"horizontal\" x1=\""+ hLineX1 +"\" x2=\""+ hLineX2 +"\" y1=\""+ hLineY +"\" y2=\""+ hLineY +"\"></line>\n" ;
    }


    String AddVerticalLine(int vLineX, int vLineY1, int vLineY2){
        return "<line class=\"vertical\" x1=\""+ vLineX +"\" x2=\""+ vLineX +"\" y1=\""+ vLineY1 +"\" y2=\""+ vLineY2 +"\"></line>\n" ;
    }


    String AddVerticalLineViolation(int vLineX, int vLineY1, int vLineY2){
        return "<line class=\"vertical no-color\" x1=\""+ vLineX +"\" x2=\""+ vLineX +"\" y1=\""+ vLineY1 +"\" y2=\""+ vLineY2 +"\"></line>\n" ;
    }


    void DrawGraph(int hLineX1, int hLineX2, int vLineY1, int vLineY2, boolean isViolation){
        if(isViolation){
            htmlAppendedText = htmlAppendedText + ViolationLine +
                    AddHorizontalLine(hLineX1, hLineX2, vLineY2 ) +
                    AddVerticalLineViolation(hLineX1, vLineY1, vLineY2 )+
                    "                  </g>\n";
        }else{
            htmlAppendedText = htmlAppendedText + DefaultLine +
                    AddHorizontalLine(hLineX1, hLineX2, vLineY2 ) +
                    AddVerticalLine(hLineX1, vLineY1, vLineY2 )+
                    "                  </g>\n";
        }
    }




    public void ReloadWebView(final WebView webView, final String closeTag){
        webView.clearCache(true);
        webView.loadData("", "text/html; charset=UTF-8", null );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String data = ConstantHtml.GraphHtml + htmlAppendedText + closeTag;
                webView.loadDataWithBaseURL("" , data, "text/html", "UTF-8", "");
            }
        }, 500);

    }


    void CheckSelectedDateTime(DateTime selectedDate, String LogDate){
        try {

            String cDate = String.valueOf(selectedDate);
            cDate = cDate.split("T")[0] + "T00:00:00";
            selectedDate = globally.getDateTimeObj(cDate, false);

            if (LogDate.equals(globally.GetCurrentDeviceDate())) {
                selectedDateTime = selectedDate;
                selectedUtcTime = globally.getDateTimeObj(globally.GetCurrentUTCTimeFormat(), true);
            } else {
                try {
                    selectedDateTime = globally.getDateTimeObj(globally.ConvertDateFormatyyyy_MM_dd(LogDate) + "T23:59:59", false); //global.ConvertDateFormat(LogDate)
                    selectedUtcTime = globally.getDateTimeObj(globally.GetUTCFromDate(globally.ConvertDateFormat(LogDate), offsetFromUTC), true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void LoadDataOnWebView(WebView webView, JSONArray driverLogJsonArray, String selectedLogDate, boolean isEdited){

        int DRIVER_JOB_STATUS = 1, OldStatus = -1;

        TotalDrivingHours        = "00:00";
        TotalOnDutyHours         = "00:00";
        LeftCycleHours           = "00:00";
        LeftDayDrivingHours      = "00:00";
        TotalOffDutyHours        = "00:00";
        TotalSleeperBerthHours   = "00:00";

        htmlAppendedText    = "";
        hLineX1 = 0;    hLineX2 = 0;    hLineY  = 0;
        vLineX  = 0;    vLineY1 = 0;    vLineY2 = 0;
        boolean isViolation ;

        if(isEdited) {
            editedLogList = new ArrayList<>();
        }else {
            originalLogList = new ArrayList<>();
        }

        try{
            for(int logCount = 0 ; logCount < driverLogJsonArray.length() ; logCount ++) {
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(logCount);
                DRIVER_JOB_STATUS = logObj.getInt(ConstantsKeys.DriverStatusId);
                String startDateTime = logObj.getString(ConstantsKeys.startDateTime);
                String endDateTime = logObj.getString(ConstantsKeys.endDateTime);
                String totalHours = logObj.getString(ConstantsKeys.TotalHours);

                if(DRIVER_JOB_STATUS == Constants.DRIVING || DRIVER_JOB_STATUS == Constants.ON_DUTY) {
                    if (!logObj.isNull(ConstantsKeys.IsViolation)) {
                        isViolation = logObj.getBoolean(ConstantsKeys.IsViolation);
                    } else {
                        isViolation = false;
                    }
                }else{
                    isViolation = false;
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(globally.DateFormat);  //:SSSZ
                Date startDate = new Date();
                Date endDate = new Date();
                startHour = 0; startMin = 0; endHour = 0; endMin = 0;

                if(logCount > 0 && logCount == driverLogJsonArray.length()-1 ) {
                    if(  selectedLogDate.equals(globally.GetCurrentDeviceDate()) ) {
                        endDateTime = Globally.GetCurrentDateTime();
                    }else{
                        if(endDateTime.length() > 16 && endDateTime.substring(11,16).equals("00:00")){
                            endDateTime = endDateTime.substring(0,11) + "23:59" +endDateTime.substring(16, endDateTime.length());
                        }
                    }

                }
                try {
                    startDate   = simpleDateFormat.parse(startDateTime);
                    endDate     = simpleDateFormat.parse(endDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String Duration = "";
                if (logObj.has(ConstantsKeys.Duration))
                    Duration = logObj.getString(ConstantsKeys.Duration);
                else {
                    Duration = globally.FinalValue(Integer.valueOf(totalHours));
                }

                EldDriverLogModel driverLogModel;
                if(DRIVER_JOB_STATUS == constants.ON_DUTY) {
                    driverLogModel = new EldDriverLogModel(DRIVER_JOB_STATUS, startDateTime, endDateTime, totalHours, "",
                            isViolation, "", "", Duration, "", "", logObj.getBoolean(ConstantsKeys.Personal),
                            false, false);
                }else{
                    boolean isEditedLog = false;
                    if(isEdited){
                        isEditedLog = logObj.getBoolean(ConstantsKeys.IsEdited);
                    }
                    driverLogModel = new EldDriverLogModel(DRIVER_JOB_STATUS, startDateTime, endDateTime, totalHours, "",
                            isViolation, "", "", Duration, "", "",
                            logObj.getBoolean(ConstantsKeys.Personal),
                            isEditedLog, false);
                }

                if(isEdited){
                    editedLogList.add(driverLogModel);
                }else{
                    originalLogList.add(driverLogModel);
                }


                startHour   = startDate.getHours() * 60;
                startMin    = startDate.getMinutes();
                endHour     = endDate.getHours() * 60;
                endMin      = endDate.getMinutes();

                if( driverLogJsonArray.length() == 1) {
                    long diff = globally.DateDifference(startDate, endDate);
                    if (diff > 0 && endDate.getHours() == 0) {
                        endHour = 24 * 60;
                    }
                }

                hLineX1 =   startHour + startMin;
                hLineX2 =   endHour + endMin;

                int VerticalLineX = constants.VerticalLine(OldStatus);
                int VerticalLineY = constants.VerticalLine(DRIVER_JOB_STATUS);

                if(hLineX2 > hLineX1) {
                    if (OldStatus != -1) {
                        DrawGraph(hLineX1, hLineX2, VerticalLineX, VerticalLineY, isViolation);
                    } else {
                        DrawGraph(hLineX1, hLineX2, VerticalLineY, VerticalLineY, isViolation);
                    }
                }
                OldStatus   =   DRIVER_JOB_STATUS;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        TotalOnDutyHours        = Globally.FinalValue(hMethods.GetOnDutyTime(driverLogJsonArray));
        TotalDrivingHours       = Globally.FinalValue(hMethods.GetDrivingTime(driverLogJsonArray));
        TotalOffDutyHours       = Globally.FinalValue(hMethods.GetOffDutyTime(driverLogJsonArray));
        TotalSleeperBerthHours  = Globally.FinalValue(hMethods.GetSleeperTime(driverLogJsonArray));

        String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
        ReloadWebView(webView, CloseTag);



    }



    /*================== Get suggested records edited from web ===================*/
    void GetSuggestedRecords(final String DriverId, final String DeviceId){

        if(progressDialog.isShowing() == false)
                progressDialog.show();

        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("DeviceId", DeviceId );

        GetEditedRecordRequest.executeRequest(Request.Method.POST, APIs.GET_SUGGESTED_RECORDS , params, GetRecordFlag,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    /*================== claim suggested records edited from web ===================*/
    void ClaimSuggestedRecords(final String DriverId, final String DeviceId, final String StatusId, int flag){

        if(progressDialog.isShowing() == false)
            progressDialog.show();

        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("DeviceId", DeviceId );
        params.put("CurrentDate", globally.GetCurrentDeviceDateDefault());
        params.put("StatusId", StatusId );

        claimLogRequest.executeRequest(Request.Method.POST, APIs.CHANGE_STATUS_SUGGESTED_EDIT , params, flag,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            Log.d("response", "edit response: " + response);
            JSONObject obj = null;
            String status = "", message = "";

            try {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }catch (Exception e){ e.printStackTrace();}

            try {
                obj = new JSONObject(response);
                status = obj.getString(ConstantsKeys.Status);
                message = obj.getString(ConstantsKeys.Message);
            } catch (JSONException e) {
            }

            if (status.equalsIgnoreCase("true")) {

                switch (flag) {
                    case GetRecordFlag:

                        try {
                            editedLogList = new ArrayList<>();
                            editDataArray = new JSONArray(obj.getString(ConstantsKeys.Data));
                            for(int dataCount = editDataArray.length()-1 ; dataCount >= 0 ; dataCount--){
                                JSONObject dataObj = (JSONObject)editDataArray.get(dataCount);

                                String selectedDate = dataObj.getString(ConstantsKeys.DriverLogDate);
                                editedLogArray = new JSONArray(dataObj.getString(ConstantsKeys.SuggestedEditModel));
                                LogDate = globally.ConvertDateFormatMMddyyyy(selectedDate);


                                for(int i = 0 ; i < editedLogArray.length() ; i++){
                                    JSONObject editedObj = (JSONObject)editedLogArray.get(i);
                                    EldDriverLogModel editModel = new EldDriverLogModel(

                                            editedObj.getInt(ConstantsKeys.DriverStatusId),

                                            editedObj.getString(ConstantsKeys.StartDateTime),
                                            editedObj.getString(ConstantsKeys.EndDateTime),
                                            editedObj.getString(ConstantsKeys.TotalHours),
                                            editedObj.getString(ConstantsKeys.CurrentCycleId),

                                            editedObj.getBoolean(ConstantsKeys.IsViolation),

                                            editedObj.getString(ConstantsKeys.UTCStartDateTime),
                                            editedObj.getString(ConstantsKeys.UTCEndDateTime),
                                            "",
                                            "",
                                            "",

                                            editedObj.getBoolean(ConstantsKeys.Personal),
                                            editedObj.getBoolean(ConstantsKeys.IsEdited),
                                            false

                                            );

                                    editedLogList.add(editModel);

                                }

                                CheckSelectedDateTime(globally.getDateTimeObj(selectedDate, false), LogDate);
                                setPagetAdapter();

                                break;
                            }


                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        setPagetAdapter();

                        break;

                    case CertifyRecordFlag:

                        globally.EldScreenToast(EldFragment.refreshLogBtn, message, getResources().getColor(R.color.color_eld_theme));
                        EldFragment.refreshLogBtn.performClick();

                        if(editDataArray.length() == 1){
                           sharedPref.setSuggestedEditStatus(false, EditedLogActivity.this);
                        }

                        try {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(getApplicationContext() != null)
                                        finish();

                                }
                            }, 1000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        break;

                    case RejectRecordFlag:
                        globally.EldScreenToast(confirmCertifyBtn, message, getResources().getColor(R.color.color_eld_theme));
                        break;

                }

            } else {
                setPagetAdapter();
                globally.EldScreenToast(confirmCertifyBtn, message, getResources().getColor(R.color.colorVoilation));
            }
        }

    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {

            try {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }catch (Exception e){e.printStackTrace();}


            switch (flag){

                case GetRecordFlag:
                    setPagetAdapter();
                    break;

                case CertifyRecordFlag:

                    globally.EldScreenToast(confirmCertifyBtn, error.toString(), getResources().getColor(R.color.colorVoilation));

                    break;

                case RejectRecordFlag:
                    globally.EldScreenToast(confirmCertifyBtn, error.toString(), getResources().getColor(R.color.colorVoilation));
                    break;


                default:
                    globally.EldScreenToast(confirmCertifyBtn, error.toString(), getResources().getColor(R.color.colorVoilation));

                    break;
            }
        }
    };


    AlertDialogEld.PositiveButtonCallback positiveCallBack = new AlertDialogEld.PositiveButtonCallback() {
        @Override
        public void getPositiveClick(int flag) {
            ClaimSuggestedRecords(DriverId, DeviceId, RejectSuggestedRecord, CertifyRecordFlag);
        }
    };

    AlertDialogEld.NegativeButtonCallBack negativeCallBack = new AlertDialogEld.NegativeButtonCallBack() {
        @Override
        public void getNegativeClick(int flag) {
            Log.d("negativeCallBack", "negativeCallBack: " + flag);
        }
    };


    void dateDescOnView(String date){
        String dateDesc = "";
        String[] dateMonth = Globally.dateConversionMMMM_ddd_dd(date.toString()).split(",");

        if(dateMonth.length > 1) {
            dateDesc = dateMonth[1] + " " + LogDate.split("/")[1] ;
        }

        EldTitleTV.setText(getResources().getString(R.string.review_carrier_edits) + " ( " + dateDesc + " )");

    }

}
