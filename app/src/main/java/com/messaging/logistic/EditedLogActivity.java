package com.messaging.logistic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.constants.DriverLogResponse;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.custom.dialogs.OtherReviewLogDialog;
import com.custom.dialogs.SignDialog;
import com.custom.dialogs.SignRecordDialog;
import com.driver.details.EldDriverLogModel;
import com.google.android.material.tabs.TabLayout;
import com.local.db.CertifyLogMethod;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.RecapViewMethod;
import com.messaging.logistic.fragment.DriverLogDetailFragment;
import com.messaging.logistic.fragment.EditedLogFragment;
import com.messaging.logistic.fragment.EldFragment;
import com.messaging.logistic.fragment.HosSummaryFragment;
import com.messaging.logistic.fragment.OriginalLogFragment;
import com.models.RecapModel;
import com.simplify.ink.InkView;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    List<RecapModel> otherLogList   = new ArrayList<>();
    public static JSONArray editedLogArray = new JSONArray();
    JSONArray dataArray = new JSONArray();
    JSONArray editDataArray = new JSONArray();

    ImageView eldMenuBtn, suggestInvisibleView;
    TextView EldTitleTV, confirmCertifyTV, otherSuggestedLogBtn;
    RelativeLayout rightMenuBtn;
    RelativeLayout eldMenuLay;
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

    String DriverId, DeviceId, imagePath = "";
    public HelperMethods hMethods;
    public DBHelper dbHelper;
    public SharedPref sharedPref;
    public Globally globally;
    public Constants constants;
    public static JSONArray driverLogArray;
    EditedLogFragment editedLogFragment;
    OriginalLogFragment originalLogFragment;
    CardView confirmCertifyBtn, cancelCertifyBtn;

    SaveDriverLogPost saveCertifyLogPost;
    VolleyRequest GetEditedRecordRequest, claimLogRequest;
    Map<String, String> params;
    final int GetRecordFlag             = 101;
    final int CertifyRecordFlag         = 102;
    final int RejectRecordFlag          = 103;

    String AcceptedSuggestedRecord      = "1";
    String RejectSuggestedRecord        = "4";
    String editedData                   = "";
    String Message                      = "";

    JSONArray recap18DaysArray  = new JSONArray();
    JSONArray CertifyLogArray   = new JSONArray();
    CertifyLogMethod certifyLogMethod;
    RecapViewMethod recapViewMethod;
    ProgressDialog progressDialog;
    OtherReviewLogDialog otherReviewLogDialog;
    SignDialog signDialog;
    AlertDialog alertDialog;

    boolean isCurrentDate = false;
    boolean isCertifySignExist = false;
    boolean IsContinueWithSign = false;
    String LogSignImageInByte = "";

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

        recapViewMethod = new RecapViewMethod();
        certifyLogMethod= new CertifyLogMethod();
        globally        = new Globally();
        hMethods        = new HelperMethods();
        dbHelper        = new DBHelper(this);
        sharedPref      = new SharedPref();
        constants       = new Constants();

        saveCertifyLogPost          = new SaveDriverLogPost(this, saveCertifyResponse);
        GetEditedRecordRequest      = new VolleyRequest(this);
        claimLogRequest             = new VolleyRequest(this);
        progressDialog              = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");

        Intent i = getIntent();
        editedData = i.getStringExtra(ConstantsKeys.suggested_data);

        rightMenuBtn        = (RelativeLayout) findViewById(R.id.rightMenuBtn);
        eldMenuLay          = (RelativeLayout)findViewById(R.id.eldMenuLay);
        eldMenuBtn          = (ImageView)findViewById(R.id.eldMenuBtn);
        suggestInvisibleView= (ImageView)findViewById(R.id.suggestInvisibleView);

        EldTitleTV          = (TextView) findViewById(R.id.EldTitleTV);
        confirmCertifyTV    = (TextView) findViewById(R.id.confirmCertifyTV);
        otherSuggestedLogBtn= (TextView) findViewById(R.id.dateActionBarTV);

        confirmCertifyBtn   = (CardView)findViewById(R.id.confirmCertifyBtn);
        cancelCertifyBtn    = (CardView)findViewById(R.id.cancelCertifyBtn);

        editedLogFragment   = new EditedLogFragment();
        originalLogFragment = new OriginalLogFragment();
        confirmationDialog  = new AlertDialogEld(this);

        offsetFromUTC = (int) globally.GetTimeZoneOffSet();
        currentDateTime = globally.getDateTimeObj(globally.GetCurrentDateTime(), false);
        LogDate = globally.GetCurrentDeviceDate();

        otherSuggestedLogBtn.setVisibility(View.VISIBLE);
        rightMenuBtn.setVisibility(View.INVISIBLE);
        eldMenuBtn.setImageResource(R.drawable.back_white);
        otherSuggestedLogBtn.setBackgroundResource(R.drawable.transparent);
        otherSuggestedLogBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        otherSuggestedLogBtn.setText(Html.fromHtml("<b><u>"+ getString(R.string.view_other_suggested_log) + "</u></b>"));

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        DeviceId               = sharedPref.GetSavedSystemToken(this);
        DriverId               = sharedPref.getDriverId( this);

        isCertifySignExist     = constants.isCertifySignExist(recapViewMethod, DriverId, dbHelper);
        recap18DaysArray       = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DriverId), dbHelper);
        CertifyLogArray        = certifyLogMethod.getSavedCertifyLogArray(Integer.valueOf(DriverId), dbHelper);


        try {
            driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
            driverLogArray = new JSONArray();
        }

        CheckSelectedDateTime(currentDateTime, LogDate);


        if(editedData.length() > 0){
            parseEditedData(editedData, false);
        }else {
            if (globally.isConnected(this)) {
                GetSuggestedRecords(DriverId, DeviceId);
            } else {
                setPagetAdapter();
                globally.EldScreenToast(confirmCertifyBtn, globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            }
        }


        eldMenuLay.setOnClickListener(this);
        confirmCertifyBtn.setOnClickListener(this);
        cancelCertifyBtn.setOnClickListener(this);
        otherSuggestedLogBtn.setOnClickListener(this);

    }



    // Add Fragments to Tabs ViewPager for each Tabs
    private void setPagetAdapter(){
        ViewPager editedLogViewPager  = (ViewPager) findViewById(R.id.editedLogPager);
        tabAdapter = new TabLayoutAdapter(getSupportFragmentManager(), this);
        tabAdapter.addFragment(editedLogFragment, getString(R.string.edited_log), tabIcons[0]);
        tabAdapter.addFragment(originalLogFragment, getString(R.string.original_log), tabIcons[1]);
        editedLogViewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(editedLogViewPager);

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

            case R.id.dateActionBarTV:
                try{
                    if (otherReviewLogDialog != null && otherReviewLogDialog.isShowing())
                        otherReviewLogDialog.dismiss();
                    otherReviewLogDialog = new OtherReviewLogDialog(EditedLogActivity.this, otherLogList, new ReviewLogListener());
                    otherReviewLogDialog.show();
                }catch (Exception e){e.printStackTrace();}

                break;

            case R.id.eldMenuLay:
                finish();
                break;

            case R.id.confirmCertifyBtn:

                if(globally.isConnected(this)){
                    if(isCurrentDate) {
                        ClaimSuggestedRecords(DriverId, DeviceId, AcceptedSuggestedRecord, CertifyRecordFlag);
                    }else{
                        if(isCertifySignExist){
                            ContinueWithoutSignDialog();
                        }else {
                            openSignDialog();
                        }
                    }
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






    public void ContinueWithoutSignDialog(){
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditedLogActivity.this);
            alertDialogBuilder.setTitle("Certify log alert !!");
            alertDialogBuilder.setMessage(getString(R.string.continue_sign_desc));
            alertDialogBuilder.setCancelable(false);


            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            IsContinueWithSign = true;
                            ClaimSuggestedRecords(DriverId, DeviceId, AcceptedSuggestedRecord, CertifyRecordFlag);
                            dialog.dismiss();
                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IsContinueWithSign = false;
                    openSignDialog();
                    dialog.dismiss();
                }
            });


            if (alertDialog != null && alertDialog.isShowing())
                alertDialog.dismiss();

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void openSignDialog(){

        try{
            if (signDialog != null && signDialog.isShowing())
                signDialog.dismiss();
            signDialog = new SignDialog(EditedLogActivity.this, new SignListener());
            signDialog.show();
        }catch (Exception e){e.printStackTrace();}


    }



    /*================== Signature Listener ====================*/
    private class SignListener implements SignDialog.SignListener{

        @Override
        public void SignOkBtn(InkView inkView, boolean IsSigned) {

            try {
                if (signDialog != null) {
                    if (IsSigned) {
                        imagePath = constants.GetSignatureBitmap(inkView, suggestInvisibleView, getApplicationContext());
                        signDialog.dismiss();

                        ClaimSuggestedRecords(DriverId, DeviceId, AcceptedSuggestedRecord, CertifyRecordFlag);

                    } else {
                        Globally.EldScreenToast(TabAct.sliderLay, "Error", getResources().getColor(R.color.colorVoilation) );
                        imagePath = "";
                    }


                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    /*================== Review Log Listener ====================*/
    private class ReviewLogListener implements OtherReviewLogDialog.ReviewLogListener {


        @Override
        public void JobBtnReady(String date, int position) {
            try {
                if (otherReviewLogDialog != null) {
                    otherReviewLogDialog.dismiss();

                    String selectedDate = otherLogList.get(position).getDay().split(",")[0];
                    if(EldTitleTV.getText().toString().contains(selectedDate)){
                        // ignore to refresh view
                    }else{

                        LogDate = Globally.ConvertDateFormatMMddyyyy(date);
                        CheckSelectedDateTime(Globally.getDateTimeObj(date, false), LogDate);

                        parseEditedData(dataArray.toString(), true);

                    }
                }
            } catch (Exception e) {


            }
        }
    }


    private void saveByteSignLocally(String SignImageInBytes, boolean IsContinueWithSign){

        // Add signed parameters with values into the json object and put into the json Array.
        JSONObject CertifyLogObj = certifyLogMethod.AddCertifyLogArray(DriverId, DeviceId, Globally.PROJECT_ID, LogDate,
                SignImageInBytes, IsContinueWithSign );
        CertifyLogArray.put(CertifyLogObj);

        // Insert/Update Certify Log table
        certifyLogMethod.CertifyLogHelper(Integer.valueOf(DriverId), dbHelper, CertifyLogArray );

        // Update recap array with byte image
        recap18DaysArray = recapViewMethod.UpdateSelectedDateRecapArray(recap18DaysArray, LogDate, SignImageInBytes);
        recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DriverId), dbHelper, recap18DaysArray);

    }


    void signPreviouslyWithAPi(boolean IsContinueWithSign){
        String lastSignature = constants.getLastSignature(recapViewMethod, DriverId, dbHelper);
        saveByteSignLocally(lastSignature, IsContinueWithSign);
        LogSignImageInByte = lastSignature;

        progressDialog.show();
        saveCertifyLogPost.PostDriverLogData(CertifyLogArray, APIs.CERTIFY_LOG_OFFLINE, constants.SocketTimeout10Sec,
                true, false, 0, 1);

    }


    private void SaveDriverSignArray(boolean IsContinueWithSign){

        if(IsContinueWithSign){
            signPreviouslyWithAPi(IsContinueWithSign);

        }else{
            File f = new File(imagePath);
            if (f.exists() ) {
                // Convert image file into bytes
                LogSignImageInByte = Globally.ConvertImageToByteAsString(imagePath);
                saveByteSignLocally(LogSignImageInByte, IsContinueWithSign);

                progressDialog.show();
                saveCertifyLogPost.PostDriverLogData(CertifyLogArray, APIs.CERTIFY_LOG_OFFLINE, constants.SocketTimeout10Sec, true, false, 0, 1);

                imagePath = "";

            }else{
                signPreviouslyWithAPi(true);

            }
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
                    selectedDateTime = globally.getDateTimeObj(globally.ConvertDateFormatyyyy_MM_dd(LogDate) + "T00:00:00", false); //global.ConvertDateFormat(LogDate)
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
                boolean isEditedLog = false;
                if(isEdited){
                    isEditedLog = logObj.getBoolean(ConstantsKeys.IsEdited);
                }


                if(DRIVER_JOB_STATUS == constants.ON_DUTY) {
                    driverLogModel = new EldDriverLogModel(DRIVER_JOB_STATUS, startDateTime, endDateTime, totalHours, "",
                            isViolation, "", "", Duration, "", "", logObj.getBoolean(ConstantsKeys.Personal),
                            isEditedLog,  logObj.getBoolean(ConstantsKeys.YardMove));
                }else{
                    driverLogModel = new EldDriverLogModel(DRIVER_JOB_STATUS, startDateTime, endDateTime, totalHours, "",
                            isViolation, "", "", Duration, "", "",
                            logObj.getBoolean(ConstantsKeys.Personal),
                            isEditedLog, logObj.getBoolean(ConstantsKeys.YardMove));
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


    void dismissDialog(){
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }catch (Exception e){ e.printStackTrace();}
    }

    /*================== claim suggested records edited from web ===================*/
    void ClaimSuggestedRecords(final String DriverId, final String DeviceId, final String StatusId, int flag){

        if(progressDialog.isShowing() == false)
            progressDialog.show();

        String selectedDate= selectedDateTime.toString().split("T")[0];
        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("DeviceId", DeviceId );
        params.put("CurrentDate", selectedDate);
        params.put("StatusId", StatusId );

        claimLogRequest.executeRequest(Request.Method.POST, APIs.CHANGE_STATUS_SUGGESTED_EDIT , params, flag,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            Log.d("response", "edit response: " + response);
            JSONObject obj = null;
            String status = "";
            Message = "";


            try {
                obj = new JSONObject(response);
                status = obj.getString(ConstantsKeys.Status);
                Message = obj.getString(ConstantsKeys.Message);
            } catch (JSONException e) {
            }

            if (status.equalsIgnoreCase("true")) {

                switch (flag) {
                    case GetRecordFlag:

                        dismissDialog();
                        try {
                            dataArray = new JSONArray(obj.getString(ConstantsKeys.Data));
                            parseEditedData(dataArray.toString(), false);
                        }catch (Exception e){e.printStackTrace();}

                        break;

                    case CertifyRecordFlag:

                        if(isCurrentDate){

                            dismissDialog();

                            globally.EldScreenToast(EldFragment.refreshLogBtn, Message, getResources().getColor(R.color.color_eld_theme));
                            finishActivityWithViewUpdate();
                            EldFragment.refreshLogBtn.performClick();


                        }else {
                            SaveDriverSignArray(IsContinueWithSign);
                        }

                        break;

                    case RejectRecordFlag:

                        dismissDialog();

                        if(Message.equals("Record updated successfully")){
                            Message = "Record rejected successfully";
                        }
                        globally.EldScreenToast(confirmCertifyBtn, Message, getResources().getColor(R.color.color_eld_theme));

                        finishActivityWithViewUpdate();

                        break;

                }

            } else {
                dismissDialog();

                setPagetAdapter();
                globally.EldScreenToast(confirmCertifyBtn, Message, getResources().getColor(R.color.colorVoilation));
            }
        }

    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {

            dismissDialog();

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



    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveCertifyResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("signatureLog", "---Response: " + response);
            dismissDialog();

            try {
                JSONObject obj = new JSONObject(response);

                if (obj.getString("Status").equals("true")) {
                    // Clear unsent Shipping Log from db
                    CertifyLogArray = new JSONArray();
                    certifyLogMethod.CertifyLogHelper(Integer.valueOf(DriverId), dbHelper, CertifyLogArray );
                    globally.EldScreenToast(EldFragment.refreshLogBtn, Message, getResources().getColor(R.color.color_eld_theme));
                }else{
                    globally.EldScreenToast(EldFragment.refreshLogBtn, Message, getResources().getColor(R.color.colorSleeper));
                }

                removeSelectedDateFromList();

               // finishActivityWithViewUpdate();
               // EldFragment.refreshLogBtn.performClick();

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("errorrr ", ">>>error dialog: ");

            try {
                dismissDialog();

                Globally.EldToastWithDuration(TabAct.sliderLay, Message, getResources().getColor(R.color.colorSleeper));
                finishActivityWithViewUpdate();
                EldFragment.refreshLogBtn.performClick();

              //  removeSelectedDateFromList();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };





    AlertDialogEld.PositiveButtonCallback positiveCallBack = new AlertDialogEld.PositiveButtonCallback() {
        @Override
        public void getPositiveClick(int flag) {
            ClaimSuggestedRecords(DriverId, DeviceId, RejectSuggestedRecord, RejectRecordFlag);
        }
    };

    AlertDialogEld.NegativeButtonCallBack negativeCallBack = new AlertDialogEld.NegativeButtonCallBack() {
        @Override
        public void getNegativeClick(int flag) {
            Log.d("negativeCallBack", "negativeCallBack: " + flag);
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void removeSelectedDateFromList(){

        try {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = (JSONObject) dataArray.get(i);
                DateTime DriverLogDate = Globally.getDateTimeObj(obj.getString(ConstantsKeys.DriverLogDate), false);
                int DaysDiff = hMethods.DayDiff(DriverLogDate, selectedDateTime);
                if(DaysDiff == 0){
                    dataArray.remove(i);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(dataArray.length() > 0){
            Toast.makeText(EditedLogActivity.this, getString(R.string.other_suggested_log), Toast.LENGTH_LONG).show();
            parseEditedData(dataArray.toString(), false);

        }else{
             finishActivityWithViewUpdate();
             EldFragment.refreshLogBtn.performClick();

        }
    }

    void parseEditedData(String editData, boolean isSelected){
        try {
            otherLogList = new ArrayList<>();
            editedLogList = new ArrayList<>();
            editDataArray = new JSONArray(editData);

            for(int dataCount = editDataArray.length()-1 ; dataCount >= 0 ; dataCount--){

                JSONObject dataObj = (JSONObject)editDataArray.get(dataCount);
                String selectedDate = dataObj.getString(ConstantsKeys.DriverLogDate);

                if(isSelected){
                    DateTime DriverLogDate = Globally.getDateTimeObj(selectedDate, false);
                    int DaysDiff = hMethods.DayDiff(DriverLogDate, selectedDateTime);
                    if(DaysDiff == 0){
                        refreshViewWithPager(dataObj, selectedDate);
                        break;
                    }

                }else{
                    refreshViewWithPager(dataObj, selectedDate);
                    break;

                }



            }


            for(int dataCount = editDataArray.length()-1 ; dataCount >= 0 ; dataCount--){
                JSONObject dataObj = (JSONObject)editDataArray.get(dataCount);
                String selectedDate = dataObj.getString(ConstantsKeys.DriverLogDate);
                otherLogList.add(new RecapModel(parseDateWithName(selectedDate), selectedDate,""));

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        setPagetAdapter();

    }


    void refreshViewWithPager(JSONObject dataObj, String selectedDate){
        try {
            editedLogArray = new JSONArray(dataObj.getString(ConstantsKeys.SuggestedEditModel));
            LogDate = globally.ConvertDateFormatMMddyyyy(selectedDate);

            editedLogList = parseJsonLogData(editedLogArray);

            CheckSelectedDateTime(globally.getDateTimeObj(selectedDate, false), LogDate);
            setPagetAdapter();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private List<EldDriverLogModel> parseJsonLogData(JSONArray editedLogArray){
        List<EldDriverLogModel> logList = new ArrayList<>();
        try {
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
                        editedObj.getBoolean(ConstantsKeys.YardMove)

                );

                logList.add(editModel);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return logList;
    }

    void finishActivityWithViewUpdate(){

        if(editDataArray.length() == 1){
           // make is suggested value false if edit logs for single day
            sharedPref.setAlertSettings(sharedPref.isUnidentified(getApplicationContext()),
                    sharedPref.isMalfunction(getApplicationContext()),
                    sharedPref.isDiagnostic(getApplicationContext()),
                    false, getApplicationContext());

            try {   // delay 1 sec to  update log
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
        }else{
            try {

                if(getApplicationContext() != null) {
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    void dateDescOnView(String date){
        String dateDesc = "";
        String[] dateMonth = Globally.dateConversionMMMM_ddd_dd(date.toString()).split(",");

        if(dateMonth.length > 1) {
            dateDesc = dateMonth[1] + " " + LogDate.split("/")[1] ;
        }

        EldTitleTV.setText(getResources().getString(R.string.review_carrier_edits) + " ( " + dateDesc + " )");

        if(LogDate.equals(Globally.GetCurrentDeviceDate())){
            isCurrentDate = true;
            confirmCertifyTV.setText(getString(R.string.Confirm));
        }

        if(dataArray.length() > 1){
            otherSuggestedLogBtn.setVisibility(View.VISIBLE);
        }else{
            otherSuggestedLogBtn.setVisibility(View.GONE);
        }

    }


    private String parseDateWithName(String date){
        String dateDesc = "";
        String[] dateMonth = Globally.dateConversionMMMM_ddd_dd(date.toString()).split(",");

        if(dateMonth.length > 1) {
            dateDesc = dateMonth[1] + " " + date.substring(8, 10) + ", "+ date.substring(0, 4) ;
        }

       return dateDesc;


    }



}
