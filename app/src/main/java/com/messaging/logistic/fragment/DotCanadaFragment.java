package com.messaging.logistic.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adapter.logistic.CanDotAddHrsAdapter;
import com.adapter.logistic.CanDotDutyStatusAdapter;
import com.adapter.logistic.CanDotEnginePowerAdapter;
import com.adapter.logistic.CanDotLogInOutAdapter;
import com.adapter.logistic.CanDotRemarksAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.ConstantHtml;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.constants.WebAppInterface;
import com.custom.dialogs.DatePickerDialog;
import com.custom.dialogs.ShareDriverLogDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.messaging.logistic.EldActivity;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.CanadaDutyStatusModel;
import com.models.DriverLocationModel;
import com.shared.pref.StatePrefManager;

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
import java.util.TimeZone;

public class DotCanadaFragment extends Fragment implements View.OnClickListener{


    View rootView;

    HelperMethods hMethods;
    TextView dateRodsTV, dayStartTimeTV, timeZoneCTV, currLocCTV, commentCTV, dateTimeCTV;
    TextView driverNameCTV, driverIdCTV, exemptDriverCTV, driLicNoCTV, coDriverNameCTV, coDriverIdCTV;
    TextView viewMoreTV, EldTitleTV, canSendLogBtn, viewInspectionBtn;
    TextView truckTractorIdTV, truckTractorVinTV, totalDisCTV, distanceTodayCTV, currTotalDisTV, currTotalEngTV;
    TextView trailerIdCTV, carrierNameCTV, carrierHomeTerCTV, carrierPrinPlaceCTV, currOperZoneCTV, curreCycleCTV;
    TextView totalHrsCTV, totalhrsCycleCTV, remainingHourCTV, offDutyDeffCTV, datDiagCTV, unIdenDriRecCTV;
    TextView malfStatusCTV, eldIdCTV, eldProviderCTV, eldCerCTV, eldAuthCTV;
    RelativeLayout eldMenuLay, rightMenuBtn;
    LinearLayout canDotViewMorelay, enginePwrDotLay;
    ImageView eldMenuBtn, nextDateBtn, previousDateBtn;

    WebView canDotGraphWebView;
    ProgressBar canDotProgressBar;
    ScrollView canDotScrollView;
    ListView dutyChangeDotListView, remAnotnDotListView, addHrsDotListView, loginLogDotListView, enginePwrDotListView;

    Constants constants;
    Globally global;
    SharedPref sharedPref;
    VolleyRequest GetDotLogRequest;
    Map<String, String> params;

    CanDotAddHrsAdapter canDotAddHrsAdapter;
    CanDotDutyStatusAdapter canDotDutyStatusAdapter;
    CanDotEnginePowerAdapter canDotEnginePowerAdapter;
    CanDotLogInOutAdapter canDotLogInOutAdapter;
    CanDotRemarksAdapter canDotRemarksAdapter;

    List<CanadaDutyStatusModel> DutyStatusList = new ArrayList();
    List<CanadaDutyStatusModel> LoginLogoutList = new ArrayList();
    List<CanadaDutyStatusModel> CommentsRemarksList = new ArrayList();
    List<CanadaDutyStatusModel> AdditionalHoursList = new ArrayList();
    List<CanadaDutyStatusModel> EnginePowerList = new ArrayList();

    DatePickerDialog dateDialog;
    ShareDriverLogDialog shareDialog;
    List<String> StateArrayList = new ArrayList<>();
    List<DriverLocationModel> StateList = new ArrayList<>();

    int SelectedDayOfMonth  = 0;
    int UsaMaxDays          = 7;
    int CanMaxDays          = 14;
    int MaxDays;

    String DayName, MonthFullName , MonthShortName , CurrentCycleId, CountryCycle;

    String DefaultLine      = " <g class=\"event \">\n";
    String ViolationLine    = " <g class=\"event line-red\">\n";

    String DriverId = "", DeviceId = "";
    String htmlAppendedText = "", LogDate = "", CurrentDate = "", LogSignImage = "";
    String colorVoilation = "#C92627";

    String TotalOnDutyHours         = "00:00";
    String TotalDrivingHours        = "00:00";
    String TotalOffDutyHours        = "00:00";
    String TotalSleeperBerthHours   = "00:00";

    int inspectionLayHeight = 0;
    int hLineX1         = 0;
    int hLineX2         = 0;
    int hLineY          = 0;

    int vLineX          = 0;
    int vLineY1         = 0;
    int vLineY2         = 0;
    int offsetFromUTC   = 0;
    int DriverType      = 0;
    int scrollX         = 0;
    int scrollY         = -1;
    int OldStatus       = -1;
    int startHour       = 0;
    int startMin        = 0;
    int endHour         = 0;
    int endMin          = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_dot_canada, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }


    void initView(View view) {

        hMethods            = new HelperMethods();
        constants           = new Constants();
        global              = new Globally();
        sharedPref          = new SharedPref();
        GetDotLogRequest    = new VolleyRequest(getActivity());

        dateRodsTV          = (TextView)view.findViewById(R.id.dateRodsTV);
        dayStartTimeTV      = (TextView)view.findViewById(R.id.dayStartTimeTV);
        timeZoneCTV         = (TextView)view.findViewById(R.id.timeZoneCTV);
        currLocCTV          = (TextView)view.findViewById(R.id.currLocCTV);
        commentCTV          = (TextView)view.findViewById(R.id.commentCTV);
        dateTimeCTV         = (TextView)view.findViewById(R.id.dateTimeCTV);

        driverNameCTV       = (TextView)view.findViewById(R.id.driverNameCTV);
        driverIdCTV         = (TextView)view.findViewById(R.id.driverIdCTV);
        exemptDriverCTV     = (TextView)view.findViewById(R.id.exemptDriverCTV);
        driLicNoCTV         = (TextView)view.findViewById(R.id.driLicNoCTV);
        coDriverNameCTV     = (TextView)view.findViewById(R.id.coDriverNameCTV);
        coDriverIdCTV       = (TextView)view.findViewById(R.id.coDriverIdCTV);

        truckTractorIdTV    = (TextView)view.findViewById(R.id.truckTractorIdTV);
        truckTractorVinTV   = (TextView)view.findViewById(R.id.truckTractorVinTV);
        totalDisCTV         = (TextView)view.findViewById(R.id.totalDisCTV);
        distanceTodayCTV    = (TextView)view.findViewById(R.id.distanceTodayCTV);
        currTotalDisTV      = (TextView)view.findViewById(R.id.currTotalDisTV);
        currTotalEngTV      = (TextView)view.findViewById(R.id.currTotalEngTV);

        trailerIdCTV        = (TextView)view.findViewById(R.id.trailerIdCTV);
        carrierNameCTV      = (TextView)view.findViewById(R.id.carrierNameCTV);
        carrierHomeTerCTV   = (TextView)view.findViewById(R.id.carrierHomeTerCTV);
        carrierPrinPlaceCTV = (TextView)view.findViewById(R.id.carrierPrinPlaceCTV);
        currOperZoneCTV     = (TextView)view.findViewById(R.id.currOperZoneCTV);
        curreCycleCTV       = (TextView)view.findViewById(R.id.curreCycleCTV);

        totalHrsCTV         = (TextView)view.findViewById(R.id.totalHrsCTV);
        totalhrsCycleCTV    = (TextView)view.findViewById(R.id.totalhrsCycleCTV);
        remainingHourCTV    = (TextView)view.findViewById(R.id.remainingHourCTV);
        offDutyDeffCTV      = (TextView)view.findViewById(R.id.offDutyDeffCTV);
        datDiagCTV          = (TextView)view.findViewById(R.id.datDiagCTV);
        unIdenDriRecCTV     = (TextView)view.findViewById(R.id.unIdenDriRecCTV);

        malfStatusCTV       = (TextView)view.findViewById(R.id.malfStatusCTV);
        eldIdCTV            = (TextView)view.findViewById(R.id.eldIdCTV);
        eldProviderCTV      = (TextView)view.findViewById(R.id.eldProviderCTV);
        eldCerCTV           = (TextView)view.findViewById(R.id.eldCerCTV);
        eldAuthCTV          = (TextView)view.findViewById(R.id.eldAuthCTV);

        viewMoreTV          = (TextView)view.findViewById(R.id.viewMoreTV);
        EldTitleTV          = (TextView)view.findViewById(R.id.EldTitleTV);
        canSendLogBtn       = (TextView)view.findViewById(R.id.canSendLogBtn);
        viewInspectionBtn   = (TextView)view.findViewById(R.id.dateActionBarTV);

        dutyChangeDotListView= (ListView)view.findViewById(R.id.dutyChangeDotListView);
        remAnotnDotListView  = (ListView)view.findViewById(R.id.remAnotnDotListView);
        addHrsDotListView    = (ListView)view.findViewById(R.id.addHrsDotListView);
        loginLogDotListView  = (ListView)view.findViewById(R.id.loginLogDotListView);
        enginePwrDotListView = (ListView)view.findViewById(R.id.enginePwrDotListView);

        canDotViewMorelay   = (LinearLayout)view.findViewById(R.id.canDotViewMorelay);
        enginePwrDotLay     = (LinearLayout)view.findViewById(R.id.enginePwrDotLay);

        eldMenuLay          = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        rightMenuBtn        = (RelativeLayout)view.findViewById(R.id.rightMenuBtn);

        eldMenuBtn          = (ImageView)view.findViewById(R.id.eldMenuBtn);
        nextDateBtn         = (ImageView)view.findViewById(R.id.nextDateBtn);
        previousDateBtn     = (ImageView)view.findViewById(R.id.previousDate);

        canDotGraphWebView  = (WebView)view.findViewById(R.id.canDotGraphWebView);
        canDotProgressBar   = (ProgressBar)view.findViewById(R.id.canDotProgressBar);
        canDotScrollView    = (ScrollView)view.findViewById(R.id.canDotScrollView);


        eldMenuBtn.setImageResource(R.drawable.back_btn);
        viewMoreTV.setText(Html.fromHtml("<u>" + getResources().getString(R.string.view_more) + "</u>"));
        rightMenuBtn.setVisibility(View.GONE);
        viewInspectionBtn.setText(getResources().getString(R.string.view_inspections));
        viewInspectionBtn.setVisibility(View.VISIBLE);

        getBundleData();
        initilizeWebView();
        ReloadWebView(constants.HtmlCloseTag("00:00", "00:00", "00:00", "00:00"));


        if (global.isConnected(getActivity())) {

            String selectedDateStr = global.ConvertDateFormat(LogDate);
            String currentDateStr = global.ConvertDateFormat(CurrentDate);
            DateTime selectedDateTime = new DateTime(global.getDateTimeObj(selectedDateStr, false) );
            DateTime currentDateTime = new DateTime(global.getDateTimeObj(currentDateStr, false) );
            int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
            Log.d("DaysDiff", "DaysDiff: " + DaysDiff);

            DOTBtnVisibility(DaysDiff, MaxDays);

            //LogDate = "02/02/2021";
            GetDriverDotDetails(DriverId, LogDate);

        }else{
            Globally.EldScreenToast(eldMenuLay, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            //webViewErrorDisplay();
        }

        eldMenuLay.setOnClickListener(this);
        viewMoreTV.setOnClickListener(this);
        nextDateBtn.setOnClickListener(this);
        previousDateBtn.setOnClickListener(this);
        EldTitleTV.setOnClickListener(this);
        canSendLogBtn.setOnClickListener(this);
        viewInspectionBtn.setOnClickListener(this);

    }


        private void getBundleData() {

            CurrentDate             = global.GetCurrentDeviceDate();
            DeviceId                = sharedPref.GetSavedSystemToken(getActivity());
            DriverId               = sharedPref.getDriverId( getActivity());

            Bundle getBundle = this.getArguments();
            LogDate = getBundle.getString("date");
            DayName = getBundle.getString("day_name");
            MonthFullName = getBundle.getString("month_full_name");
            MonthShortName = getBundle.getString("month_short_name");
            CurrentCycleId = getBundle.getString("cycle");
            SelectedDayOfMonth = getBundle.getInt("day_of_month");
            CountryCycle = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycle, getActivity());

            CurrentDate = global.GetCurrentDeviceDate();

            try {
                DBHelper dbHelper = new DBHelper(getActivity());
                DriverPermissionMethod driverPermissionMethod = new DriverPermissionMethod();
                JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DriverId), dbHelper);
                CanMaxDays = constants.GetDriverPermitDaysCount(logPermissionObj, CurrentCycleId, true);

                if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
                    MaxDays = UsaMaxDays;
                } else {
                    MaxDays = CanMaxDays;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " ( " + DayName + " )");

            try {
                StatePrefManager statePrefManager  = new StatePrefManager();
                StateList = statePrefManager.GetState(getActivity());
            } catch (Exception e) { }

            StateArrayList =  sharedPref.getStatesInList(getActivity());



        }

    private void DOTBtnVisibility(int DaysDiff, int MaxDays){
        if(DaysDiff == 0){
            nextDateBtn.setVisibility(View.GONE);
            previousDateBtn.setVisibility(View.VISIBLE);
        }else if(DaysDiff == MaxDays){
            previousDateBtn.setVisibility(View.GONE);
            nextDateBtn.setVisibility(View.VISIBLE);
        }else{
            nextDateBtn.setVisibility(View.VISIBLE);
            previousDateBtn.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.nextDateBtn:
                ChangeViewWithDate(true);
                break;


            case R.id.previousDate:
                ChangeViewWithDate(false);
                break;


            case R.id.EldTitleTV:
                if(dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();

                dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, LogDate, new DateListener());
                dateDialog.show();

                break;


            case R.id.canSendLogBtn:
                shareDriverLogDialog();

            break;

            //viewInspectionBtn view click
            case R.id.dateActionBarTV:
                MoveFragment(LogDate);
                break;

            case R.id.eldMenuLay:
                EldActivity.DOTButton.performClick();

                break;

            case R.id.viewMoreTV:
                if(canDotViewMorelay.getVisibility() == View.VISIBLE){
                    canDotViewMorelay.setVisibility(View.GONE);
                    viewMoreTV.setText(Html.fromHtml("<u>" + getResources().getString(R.string.view_more) + "</u>"));
                    canDotScrollView.fullScroll(View.FOCUS_UP);
                }else{
                    canDotViewMorelay.setVisibility(View.VISIBLE);
                    viewMoreTV.setText(Html.fromHtml("<u>" + getResources().getString(R.string.view_less) + "</u>"));
                }

                break;

        }
    }





    private void ChangeViewWithDate(boolean isNext){

        String selectedDate = LogDate;
        String selectedDateStr = global.ConvertDateFormat(LogDate);
        String currentDateStr = global.ConvertDateFormat(CurrentDate);
        DateTime selectedDateTime = new DateTime(global.getDateTimeObj(selectedDateStr, false) );
        DateTime currentDateTime = new DateTime(global.getDateTimeObj(currentDateStr, false) );


        if (global.isConnected(getActivity())) {
            if(isNext){
                selectedDateTime = selectedDateTime.plusDays(1);
            }else{
                selectedDateTime = selectedDateTime.minusDays(1);
            }

            int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
            Log.d("DaysDiff", "DaysDiff: " + DaysDiff);

            if ( DaysDiff >= 0 && DaysDiff <= MaxDays) {

                DOTBtnVisibility(DaysDiff, MaxDays);
                LogDate = global.ConvertDateFormatMMddyyyy(selectedDateTime.toString());
                int mnth = Integer.valueOf(LogDate.substring(0, 2));
                String MonthShortName   =   global.MONTHS[mnth - 1];

                Date date = null;
                try {
                    SimpleDateFormat inFormat = new SimpleDateFormat("MM/dd/yyyy");
                    inFormat.setTimeZone(TimeZone.getDefault());
                    date = inFormat.parse(LogDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                outFormat.setTimeZone(TimeZone.getDefault());

                String dayOfTheWeek     = outFormat.format(date);


                EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " (" + dayOfTheWeek + " )");

                GetDriverDotDetails(DriverId, LogDate);

            }

        }else{
            LogDate = selectedDate;
            Globally.EldScreenToast(eldMenuLay, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            //webViewErrorDisplay();
        }

    }


    private void MoveFragment(String date ){
        InspectionsHistoryFragment savedInspectionFragment = new InspectionsHistoryFragment();
        Globally.bundle.putString("date", date);
        Globally.bundle.putString("inspection_type", "pti");

        savedInspectionFragment.setArguments(Globally.bundle);

        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, savedInspectionFragment);
        fragmentTran.addToBackStack("inspection");
        fragmentTran.commit();


    }


    private class DateListener implements DatePickerDialog.DatePickerListener{
        @Override
        public void JobBtnReady(String SelectedDate, String dayOfTheWeek, String monthFullName, String monthShortName, int dayOfMonth) {

            dateDialog.dismiss();

            LogDate = SelectedDate;
            DayName = dayOfTheWeek;
            MonthFullName = monthFullName;
            MonthShortName = monthShortName;
            EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " ( " + DayName + " )");

            if (global.isConnected(getActivity())) {

                String selectedDateStr = global.ConvertDateFormat(LogDate);
                String currentDateStr = global.ConvertDateFormat(CurrentDate);
                DateTime selectedDateTime = new DateTime(global.getDateTimeObj(selectedDateStr, false) );
                DateTime currentDateTime = new DateTime(global.getDateTimeObj(currentDateStr, false) );
                int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
                Log.d("DaysDiff", "DaysDiff: " + DaysDiff);

                DOTBtnVisibility(DaysDiff, MaxDays);
                GetDriverDotDetails(DriverId, LogDate);

            }else{
                Globally.EldScreenToast(eldMenuLay, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            }

        }
    }


    void shareDriverLogDialog() {

        boolean IsAOBRDAutomatic        = sharedPref.IsAOBRDAutomatic(getActivity());
        boolean IsAOBRD                 = sharedPref.IsAOBRD(getActivity());


        if (!IsAOBRD || IsAOBRDAutomatic) {
            Globally.serviceIntent = new Intent(getActivity(), BackgroundLocationService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(Globally.serviceIntent);
            }
            getActivity().startService(Globally.serviceIntent);
        }

        try {
            if (shareDialog != null && shareDialog.isShowing()) {
                shareDialog.dismiss();
            }
            shareDialog = new ShareDriverLogDialog(getActivity(), getActivity(), DriverId, DeviceId, CurrentCycleId,
                    IsAOBRD, StateArrayList, StateList);
            shareDialog.show();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }



    void initilizeWebView(){
        WebSettings webSettings = canDotGraphWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        canDotGraphWebView.setWebViewClient(new WebViewClient());
        canDotGraphWebView.setWebChromeClient(new WebChromeClient());
        canDotGraphWebView.addJavascriptInterface( new WebAppInterface(), "Android");

    }


    void setdataOnAdapter(){

        try {
            canDotDutyStatusAdapter = new CanDotDutyStatusAdapter(getActivity(), DutyStatusList);
            dutyChangeDotListView.setAdapter(canDotDutyStatusAdapter);
        }catch (Exception e){}

        try{
            canDotLogInOutAdapter = new CanDotLogInOutAdapter(getActivity(), LoginLogoutList);
            loginLogDotListView.setAdapter(canDotLogInOutAdapter);
        }catch (Exception e){}

        try {
            canDotRemarksAdapter = new CanDotRemarksAdapter(getActivity(), CommentsRemarksList);
            remAnotnDotListView.setAdapter(canDotRemarksAdapter);
        }catch (Exception e){}

      /*  try {
            canDotAddHrsAdapter = new CanDotAddHrsAdapter(getActivity(), AdditionalHoursList);
            addHrsDotListView.setAdapter(canDotAddHrsAdapter);
        }catch (Exception e){}
*/
        try {
            canDotEnginePowerAdapter = new CanDotEnginePowerAdapter(getActivity(), EnginePowerList);
            enginePwrDotListView.setAdapter(canDotEnginePowerAdapter);
        }catch (Exception e){}


        try {
            inspectionLayHeight = enginePwrDotLay.getHeight();

            ViewTreeObserver vto = enginePwrDotLay.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        enginePwrDotLay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        enginePwrDotLay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    inspectionLayHeight = enginePwrDotLay.getMeasuredHeight();

                }
            });

            if (inspectionLayHeight == 0) {
                if (global.isTablet(getActivity())) {
                    inspectionLayHeight = constants.intToPixel(getActivity(), 55);
                } else {
                    inspectionLayHeight = constants.intToPixel(getActivity(), 50);
                }

            }

            final int DutyStatusListHeight = (inspectionLayHeight) * DutyStatusList.size();
            final int LoginLogoutListHeight = (inspectionLayHeight) * LoginLogoutList.size();
            final int CommentsRemarksListHeight = (inspectionLayHeight) * CommentsRemarksList.size();
           // final int AdditionalHoursListHeight = (inspectionLayHeight) * AdditionalHoursList.size();
            final int EnginePowerListHeight = (inspectionLayHeight) * EnginePowerList.size();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dutyChangeDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DutyStatusListHeight));
                    loginLogDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LoginLogoutListHeight));
                    remAnotnDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, CommentsRemarksListHeight));
                   // addHrsDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, AdditionalHoursListHeight));
                    enginePwrDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, EnginePowerListHeight));

                }
            }, 500);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    void ReloadWebView(final String closeTag){
        canDotGraphWebView.clearCache(true);
        canDotGraphWebView.loadData("", "text/html; charset=UTF-8", null );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String data = ConstantHtml.GraphHtml + htmlAppendedText + closeTag;
                canDotGraphWebView.loadDataWithBaseURL("" , data, "text/html", "UTF-8", "");
            }
        }, 500);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final int width = canDotGraphWebView.getWidth();
                final int height = canDotGraphWebView.getHeight();

                if(width < 400){
                    canDotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT) );
                }else{
                    if(height == 0){
                        if(Globally.isTablet(getActivity())){
                            canDotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(width, constants.dpToPx(getActivity(), 150) ) );
                        }else{
                            canDotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(width, constants.dpToPx(getActivity(), 118) ));
                        }

                    }
                }
            }
        }, 700);


    }




    void ParseGraphData(JSONArray driverLogJsonArray) {

        try {
            htmlAppendedText    = "";

            for (int logCount = 0; logCount < driverLogJsonArray.length(); logCount++) {
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(logCount);
                int DRIVER_JOB_STATUS = logObj.getInt("EventCode");
                int EventType = logObj.getInt("EventType");

                boolean isYardMoveOrPersonal = false;

                if (EventType != 1 && EventType != 3) {   // && EventType != 2
                    if(logCount > 0) {
                        DRIVER_JOB_STATUS = OldStatus;
                    }
                }else{

                    if(EventType == 3 && DRIVER_JOB_STATUS == 0) {
                        if(logCount > 0) {
                            DRIVER_JOB_STATUS = OldStatus;
                        }
                    }else if(EventType == 3 && DRIVER_JOB_STATUS == 2){
                        DRIVER_JOB_STATUS = Constants.ON_DUTY;
                        isYardMoveOrPersonal = true;
                    }else if(EventType == 3 && DRIVER_JOB_STATUS == 1){
                        isYardMoveOrPersonal = true;
                    }

                }

                String startDateTime = logObj.getString("StartTime");
                String endDateTime = logObj.getString("EndTime");

                if (endDateTime.equals("null")) {
                    endDateTime = Globally.GetCurrentDateTime();
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(global.DateFormat);  //:SSSZ
                Date startDate = new Date();
                Date endDate = new Date();
                startHour = 0;
                startMin = 0;
                endHour = 0;
                endMin = 0;

                if (logCount > 0 && logCount == driverLogJsonArray.length() - 1) {
                    if (LogDate.equals(CurrentDate)) {
                        endDateTime = Globally.GetCurrentDateTime();
                    } else {
                        if (endDateTime.length() > 16 && endDateTime.substring(11, 16).equals("00:00")) {
                            endDateTime = endDateTime.substring(0, 11) + "23:59" + endDateTime.substring(16, endDateTime.length());
                        }
                    }
                }

                try {
                    startDate = simpleDateFormat.parse(startDateTime);
                    endDate = simpleDateFormat.parse(endDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startHour = startDate.getHours() * 60;
                startMin = startDate.getMinutes();
                endHour = endDate.getHours() * 60;
                endMin = endDate.getMinutes();

                hLineX1 = startHour + startMin;
                hLineX2 = endHour + endMin;

                int VerticalLineX = constants.VerticalLine(OldStatus);
                int VerticalLineY = constants.VerticalLine(DRIVER_JOB_STATUS);

                if (hLineX2 > hLineX1) {
                    if (OldStatus != -1) {
                        DrawGraph(hLineX1, hLineX2, VerticalLineX, VerticalLineY, isYardMoveOrPersonal);
                    } else {
                        DrawGraph(hLineX1, hLineX2, VerticalLineY, VerticalLineY, isYardMoveOrPersonal);
                    }
                }

                if (EventType == 1 ) {  //|| EventType == 2
                    OldStatus = DRIVER_JOB_STATUS;
                }


            }

            String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
            ReloadWebView(CloseTag);


        } catch (Exception e) {
            e.printStackTrace();
            ReloadWebView(constants.HtmlCloseTag("00:00", "00:00", "00:00", "00:00"));
        }

    }



    void DrawGraph(int hLineX1, int hLineX2, int vLineY1, int vLineY2, boolean isDottedLine){

        if (isDottedLine) {
            htmlAppendedText = htmlAppendedText + DefaultLine +
                    constants.AddHorizontalDottedLine(hLineX1, hLineX2, vLineY2) +
                    constants.AddVerticalLine(hLineX1, vLineY1, vLineY2) +
                    "                  </g>\n";
        } else {
            htmlAppendedText = htmlAppendedText + DefaultLine +
                    constants.AddHorizontalLine(hLineX1, hLineX2, vLineY2) +
                    constants.AddVerticalLine(hLineX1, vLineY1, vLineY2) +
                    "                  </g>\n";
        }

    }



    /* ================== Get Driver CANADA DOT Details =================== */
    void GetDriverDotDetails( final String DriverId, final String date) {

        canDotProgressBar.setVisibility(View.VISIBLE);

        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("Date", date);

        GetDotLogRequest.executeRequest(Request.Method.POST, APIs.MOBILE_CANADA_ELD_VIEW, params, 1,
                Constants.SocketTimeout50Sec, ResponseCallBack, ErrorCallBack);
    }


    void setDataOnTextView(JSONObject dataObj){
        try{
            dateRodsTV.setText(global.ConvertDateFormatddMMMyyyy(dataObj.getString("RecordDate")));
            dayStartTimeTV.setText(dataObj.getString("PeriodStartingTime"));
            timeZoneCTV.setText(dataObj.getString("TimeZone"));
            currLocCTV.setText(dataObj.getString("CurrentLocation"));
            //commentCTV.setText(dataObj.getString(""));
            //dateTimeCTV.setText(dataObj.getString(""));

            driverNameCTV.setText(dataObj.getString("DriverLastAndFirstName"));
            driverIdCTV.setText(dataObj.getString("DriverLoginId"));
            exemptDriverCTV.setText(dataObj.getString("ExemptDriverStatus"));
            driLicNoCTV.setText(dataObj.getString("DriverLicenseNumber"));
            coDriverNameCTV.setText(dataObj.getString("CoDriverLastAndFirstName"));
            coDriverIdCTV.setText(dataObj.getString("CoDriverLoginId"));

       /*     truckTractorIdTV.setText(dataObj.getString(""));
            truckTractorVinTV.setText(dataObj.getString(""));
            totalDisCTV.setText(dataObj.getString(""));
            distanceTodayCTV.setText(dataObj.getString(""));
            currTotalDisTV.setText(dataObj.getString(""));
            currTotalEngTV.setText(dataObj.getString(""));

            trailerIdCTV.setText(dataObj.getString(""));
            carrierNameCTV.setText(dataObj.getString(""));
            carrierHomeTerCTV.setText(dataObj.getString(""));
            carrierPrinPlaceCTV.setText(dataObj.getString(""));
            currOperZoneCTV.setText(dataObj.getString(""));
            curreCycleCTV.setText(dataObj.getString(""));

            totalHrsCTV.setText(dataObj.getString(""));
            totalhrsCycleCTV.setText(dataObj.getString(""));
            remainingHourCTV.setText(dataObj.getString(""));
            offDutyDeffCTV.setText(dataObj.getString(""));
*/
            datDiagCTV.setText(dataObj.getString("DataDiagnosticIndicators"));
            unIdenDriRecCTV.setText(dataObj.getString("UnIdentifiedDriverRecords"));

            malfStatusCTV.setText(dataObj.getString("ELDMalfunctionIndicators"));
            eldIdCTV.setText(dataObj.getString("ELDID"));
            eldProviderCTV.setText(dataObj.getString("ELDManufacturer"));

            //eldCerCTV.setText(dataObj.getString(""));
           // eldAuthCTV.setText(dataObj.getString(""));

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

            canDotProgressBar.setVisibility(View.GONE);

            //  {"Status":false,"Message":"Device id is wrong","Data":false}

            String status = "", Message = "";
            JSONObject dataObj = null;
            try {
                JSONObject obj = new JSONObject(response);

                status = obj.getString("Status");
                Message = obj.getString("Message");
                if (!obj.isNull("Data")) {
                    dataObj = new JSONObject(obj.getString("Data"));
                }

            } catch (JSONException e) {
            }

            if (status.equalsIgnoreCase("true")) {

                LogSignImage = "";
                TotalOnDutyHours         = "00:00";
                TotalDrivingHours        = "00:00";
                TotalOffDutyHours        = "00:00";
                TotalSleeperBerthHours   = "00:00";

                DutyStatusList = new ArrayList();
                LoginLogoutList = new ArrayList();
                CommentsRemarksList = new ArrayList();
                AdditionalHoursList = new ArrayList();
                EnginePowerList = new ArrayList();


                try {
                    TotalOffDutyHours       = dataObj.getString("TotalOffDutyHours");
                    TotalSleeperBerthHours  = dataObj.getString("TotalSleeperHours");
                    TotalDrivingHours       = dataObj.getString("TotalDrivingHours");
                    TotalOnDutyHours        = dataObj.getString("TotalOnDutyHours");
                /*    IsMalfunction           = dataObj.getBoolean("IsMalfunction");
                    LogSignImage            = dataObj.getString("LogSignImage");
                    if(LogSignImage.equals("null")){
                        LogSignImage = "";
                    }*/

                    TotalOffDutyHours      = TotalOffDutyHours.replaceAll("-", "");
                    TotalOnDutyHours       = TotalOnDutyHours.replaceAll("-", "");
                    TotalDrivingHours      = TotalDrivingHours.replaceAll("-", "");
                    TotalSleeperBerthHours = TotalSleeperBerthHours.replaceAll("-", "");

                  //  setDataOnView(dataObj);
                   // CheckSignatureVisibilityStatus();
                    JSONArray dotLogArray = new JSONArray(dataObj.getString("graphRecordList"));
                  //  setDataOnList(dotLogArray);

                  //  JSONArray shippingLogArray = new JSONArray(dataObj.getString("ShippingInformationModel"));
                   // setShippingDataOnList(shippingLogArray);

                    ParseGraphData(dotLogArray);

                   /* if(IsMalfunction){
                        dotMalfunctionTV.setVisibility(View.VISIBLE);
                    }else{
                        dotMalfunctionTV.setVisibility(View.GONE);
                    }*/

                    JSONArray dutyStatusArray = new JSONArray(dataObj.getString(ConstantsKeys.dutyStatusChangesList));
                    JSONArray loginLogoutArray = new JSONArray(dataObj.getString(ConstantsKeys.loginAndLogoutList));
                   // JSONArray ChangeInDriversCycleList = new JSONArray(dataObj.getString(ConstantsKeys.ChangeInDriversCycleList));
                    JSONArray commentsRemarksArray = new JSONArray(dataObj.getString(ConstantsKeys.commentsRemarksList));
                    JSONArray additionalHoursArray = new JSONArray(dataObj.getString(ConstantsKeys.additionalHoursNotRecordedList));
                    JSONArray enginePowerArray = new JSONArray(dataObj.getString(ConstantsKeys.enginePowerUpAndShutDownList));

                    DutyStatusList =   constants.parseCanadaDotInList(dutyStatusArray);
                    LoginLogoutList = constants.parseCanadaDotInList(loginLogoutArray);
                    CommentsRemarksList = constants.parseCanadaDotInList(commentsRemarksArray);
                   // AdditionalHoursList = constants.parseCanadaDotInList(additionalHoursArray);
                    EnginePowerList = constants.parseCanadaDotInList(enginePowerArray);


                    setdataOnAdapter();
                    setDataOnTextView(dataObj);

                }catch (Exception e){
                    e.printStackTrace();
                }


            }else{
                htmlAppendedText    = "";

                String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
                ReloadWebView(CloseTag);

                Globally.EldScreenToast(eldMenuLay, Message, getResources().getColor(R.color.colorVoilation));

            }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            canDotProgressBar.setVisibility(View.GONE);
            Globally.EldScreenToast(eldMenuLay, "Error", getResources().getColor(R.color.colorVoilation));
            htmlAppendedText    = "";
            TotalOnDutyHours         = "00:00";
            TotalDrivingHours        = "00:00";
            TotalOffDutyHours        = "00:00";
            TotalSleeperBerthHours   = "00:00";


            String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
            ReloadWebView(CloseTag);
            Globally.EldScreenToast(eldMenuLay, "Error", getResources().getColor(R.color.colorVoilation));
            Log.d("error", ">>error: " +error);
        }
    };





}
