package com.messaging.logistic.fragment;

import android.animation.ObjectAnimator;
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

import com.adapter.logistic.CanDotCycleOpZoneAdapter;
import com.adapter.logistic.CanDotDutyStatusAdapter;
import com.adapter.logistic.CanDotEnginePowerAdapter;
import com.adapter.logistic.CanDotLogInOutAdapter;
import com.adapter.logistic.CanDotRemarksAdapter;
import com.adapter.logistic.CanDotUnAssignedVehAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.ConstantHtml;
import com.constants.Constants;
import com.constants.DoubleClickListener;
import com.constants.SharedPref;
import com.constants.Utils;
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
import com.models.UnAssignedVehicleModel;
import com.shared.pref.StatePrefManager;

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
    TextView malfStatusCTV, eldIdCTV, eldProviderCTV, eldCerCTV, eldAuthCTV, canDotModeTxtVw, eventDotETV;
    RelativeLayout eldMenuLay, rightMenuBtn, scrollUpBtn, scrollDownBtn;
    LinearLayout canDotViewMorelay;
    ImageView eldMenuBtn, nextDateBtn, previousDateBtn, canDotModeImgVw;

    WebView canDotGraphWebView;
    ProgressBar canDotProgressBar;
    ScrollView canDotScrollView;
    ListView dutyChangeDotListView, remAnotnDotListView, cycleOpZoneDotListView, loginLogDotListView, enginePwrDotListView, unIdnfdVehDotListView;

    Constants constants;
    Globally global;
    SharedPref sharedPref;
    VolleyRequest GetDotLogRequest;
    Map<String, String> params;


    CanDotDutyStatusAdapter canDotDutyStatusAdapter;
    CanDotLogInOutAdapter canDotLogInOutAdapter;
    CanDotCycleOpZoneAdapter canDotCycleOpZoneAdapter;
    CanDotRemarksAdapter canDotRemarksAdapter;
    CanDotEnginePowerAdapter canDotEnginePowerAdapter;
    CanDotUnAssignedVehAdapter canDotUnAssignedVehAdapter;

    List<CanadaDutyStatusModel> DutyStatusList = new ArrayList();
    List<CanadaDutyStatusModel> LoginLogoutList = new ArrayList();
    List<CanadaDutyStatusModel> CycleOpZoneList = new ArrayList();
    List<CanadaDutyStatusModel> CommentsRemarksList = new ArrayList();
    List<CanadaDutyStatusModel> EnginePowerList = new ArrayList();
    List<UnAssignedVehicleModel> UnAssignedVehicleList = new ArrayList<>();

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

    String DriverId = "", DeviceId = "";
    String htmlAppendedText = "", LogDate = "", CurrentDate = "", LogSignImage = "";

    String TotalOnDutyHours         = "00:00";
    String TotalDrivingHours        = "00:00";
    String TotalOffDutyHours        = "00:00";
    String TotalSleeperBerthHours   = "00:00";

    int inspectionLayHeight = 0;
    int hLineX1         = 0;
    int hLineX2         = 0;

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
        canDotModeTxtVw     = (TextView)view.findViewById(R.id.canDotModeTxtVw);

        viewMoreTV          = (TextView)view.findViewById(R.id.viewMoreTV);
        EldTitleTV          = (TextView)view.findViewById(R.id.EldTitleTV);
        canSendLogBtn       = (TextView)view.findViewById(R.id.canSendLogBtn);
        viewInspectionBtn   = (TextView)view.findViewById(R.id.dateActionBarTV);
        eventDotETV         = (TextView)view.findViewById(R.id.eventDotETV);

        dutyChangeDotListView= (ListView)view.findViewById(R.id.dutyChangeDotListView);
        remAnotnDotListView  = (ListView)view.findViewById(R.id.remAnotnDotListView);
        cycleOpZoneDotListView    = (ListView)view.findViewById(R.id.addHrsDotListView);
        loginLogDotListView  = (ListView)view.findViewById(R.id.loginLogDotListView);
        enginePwrDotListView = (ListView)view.findViewById(R.id.enginePwrDotListView);
        unIdnfdVehDotListView= (ListView)view.findViewById(R.id.unIdnfdVehDotListView);

        canDotViewMorelay   = (LinearLayout)view.findViewById(R.id.canDotViewMorelay);

        eldMenuLay          = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        rightMenuBtn        = (RelativeLayout)view.findViewById(R.id.rightMenuBtn);
        scrollUpBtn         = (RelativeLayout)view.findViewById(R.id.scrollUpBtn);
        scrollDownBtn       = (RelativeLayout)view.findViewById(R.id.scrollDownBtn);

        eldMenuBtn          = (ImageView)view.findViewById(R.id.eldMenuBtn);
        nextDateBtn         = (ImageView)view.findViewById(R.id.nextDateBtn);
        previousDateBtn     = (ImageView)view.findViewById(R.id.previousDate);
        canDotModeImgVw     = (ImageView)view.findViewById(R.id.canDotModeImgVw);

        canDotGraphWebView  = (WebView)view.findViewById(R.id.canDotGraphWebView);
        canDotProgressBar   = (ProgressBar)view.findViewById(R.id.canDotProgressBar);
        canDotScrollView    = (ScrollView)view.findViewById(R.id.canDotScrollView);


        eldMenuBtn.setImageResource(R.drawable.back_btn);
        viewMoreTV.setText(Html.fromHtml("<u>" + getResources().getString(R.string.view_more) + "</u>"));
        rightMenuBtn.setVisibility(View.GONE);
        viewInspectionBtn.setText(getResources().getString(R.string.view_inspections));
        viewInspectionBtn.setVisibility(View.VISIBLE);

        RelativeLayout canGraphLayout = (RelativeLayout) view.findViewById(R.id.canGraphLayout);
        ViewGroup.LayoutParams params = canGraphLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if(global.isTablet(getActivity())){
            params.height = constants.intToPixel(getActivity(), 170);
        }else{
            params.height = constants.intToPixel(getActivity(), 140);
        }
        canGraphLayout.setLayoutParams(params);

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

            GetDriverDotDetails(DriverId, LogDate);

        }else{
            Globally.EldScreenToast(dateRodsTV, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            //webViewErrorDisplay();
        }


        // Scroll up LongClick listener
        scrollUpBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                canDotScrollView.smoothScrollTo(0, 0);
                ObjectAnimator.ofInt(canDotScrollView, "scrollY",  0).setDuration(1000).start();

                return false;
            }
        });

        // Scroll down LongClick listener
        scrollDownBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                int totalHeight = canDotScrollView.getChildAt(0).getHeight();
                ObjectAnimator.ofInt(canDotScrollView, "scrollY",  totalHeight).setDuration(1000).start();

                return false;
            }
        });



        eldMenuLay.setOnClickListener(this);
        viewMoreTV.setOnClickListener(this);
        nextDateBtn.setOnClickListener(this);
        previousDateBtn.setOnClickListener(this);
        canDotModeImgVw.setOnClickListener(this);
        EldTitleTV.setOnClickListener(this);
        canSendLogBtn.setOnClickListener(this);
        viewInspectionBtn.setOnClickListener(this);
        canDotModeTxtVw.setOnClickListener(this);
        scrollUpBtn.setOnClickListener(this);
        scrollDownBtn.setOnClickListener(this);

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

            case R.id.canDotModeTxtVw:
                moveToDotMode(LogDate, DayName, MonthFullName, MonthShortName);
                break;

            case R.id.scrollUpBtn:

                int scrollPos = canDotScrollView.getScrollY();

                if(scrollPos < 400){
                    canDotScrollView.smoothScrollTo(0, 0);
                    ObjectAnimator.ofInt(canDotScrollView, "scrollY",  0).setDuration(600).start();
                }else{
                    ObjectAnimator.ofInt(canDotScrollView, "scrollY",  scrollPos - 450).setDuration(600).start();
                }

            break;

            case R.id.scrollDownBtn:
                int totalHeight = canDotScrollView.getChildAt(0).getHeight();
                int scrollPoss = canDotScrollView.getScrollY();
                if(scrollPoss > totalHeight - 400){
                    ObjectAnimator.ofInt(canDotScrollView, "scrollY",  scrollPoss).setDuration(600).start();
                }else{
                    ObjectAnimator.ofInt(canDotScrollView, "scrollY",  scrollPoss + 450).setDuration(600).start();
                }


            break;

            case R.id.canDotModeImgVw:
                moveToDotMode(LogDate, DayName, MonthFullName, MonthShortName);
                break;
        }
    }


    void moveToDotMode(String date, String dayName, String dayFullName, String dayShortName){

        if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
            CurrentCycleId = DriverConst.GetDriverSettings(DriverConst.CANCycleId, getActivity());
        }else{
            CurrentCycleId = DriverConst.GetDriverSettings(DriverConst.USACycleId, getActivity());
        }

        getParentFragmentManager().popBackStackImmediate();

        FragmentManager fragManager = getActivity().getSupportFragmentManager();

        Fragment dotFragment = new DotUsaFragment();
        Globally.bundle.putString("date", date);
        Globally.bundle.putString("day_name", dayName);
        Globally.bundle.putString("month_full_name", dayFullName);
        Globally.bundle.putString("month_short_name", dayShortName);
        Globally.bundle.putString("cycle", CurrentCycleId);

        dotFragment.setArguments(Globally.bundle);

        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, dotFragment);
        fragmentTran.addToBackStack(null);  //"dot_log"
        fragmentTran.commit();

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
            Globally.EldScreenToast(dateRodsTV, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
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
                Globally.EldScreenToast(dateRodsTV, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            }

        }
    }


    void shareDriverLogDialog() {

        boolean IsAOBRDAutomatic        = sharedPref.IsAOBRDAutomatic(getActivity());
        boolean IsAOBRD                 = sharedPref.IsAOBRD(getActivity());


        if (!IsAOBRD || IsAOBRDAutomatic) {
            Constants.isEldHome = false;
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

        try {
            canDotCycleOpZoneAdapter = new CanDotCycleOpZoneAdapter(getActivity(), CycleOpZoneList);
            cycleOpZoneDotListView.setAdapter(canDotCycleOpZoneAdapter);
        }catch (Exception e){}

        try {
            canDotEnginePowerAdapter = new CanDotEnginePowerAdapter(getActivity(), EnginePowerList);
            enginePwrDotListView.setAdapter(canDotEnginePowerAdapter);
        }catch (Exception e){}

        try {
            canDotUnAssignedVehAdapter = new CanDotUnAssignedVehAdapter(getActivity(), UnAssignedVehicleList);
            unIdnfdVehDotListView.setAdapter(canDotUnAssignedVehAdapter);
        }catch (Exception e){}



        try {
            int headerViewHeight = 38;
            if(global.isTablet(getActivity())){
                inspectionLayHeight  = eventDotETV.getHeight() + 20;
                headerViewHeight = eventDotETV.getHeight() + 40;
            }else{
                inspectionLayHeight  = eventDotETV.getHeight() + 40;
                headerViewHeight = eventDotETV.getHeight() + 40;
            }


            if (inspectionLayHeight == 0) {
                if (global.isTablet(getActivity())) {
                    inspectionLayHeight = constants.intToPixel(getActivity(), 60);
                    headerViewHeight    = constants.intToPixel(getActivity(), 75);
                } else {
                    inspectionLayHeight = constants.intToPixel(getActivity(), 45);
                    headerViewHeight    = constants.intToPixel(getActivity(), 65);
                }
            }


            final int DutyStatusListHeight = (inspectionLayHeight * DutyStatusList.size()) + (constants.getDateTitleCount(DutyStatusList) * inspectionLayHeight) + (headerViewHeight * constants.getListNewDateCount(DutyStatusList));
            final int LoginLogoutListHeight = (inspectionLayHeight * LoginLogoutList.size()) + (constants.getDateTitleCount(LoginLogoutList) * inspectionLayHeight)+ (headerViewHeight * constants.getListNewDateCount(LoginLogoutList));
            final int CommentsRemarksListHeight = inspectionLayHeight * (CommentsRemarksList.size() +1) ;  //+ (constants.getDateTitleCount(CommentsRemarksList) * inspectionLayHeight)
            final int CycleOpZoneListHeight = (inspectionLayHeight * CycleOpZoneList.size()) + (constants.getDateTitleCount(CycleOpZoneList) * inspectionLayHeight) + (headerViewHeight * constants.getListNewDateCount(CycleOpZoneList));
            final int EnginePowerListHeight = (inspectionLayHeight * EnginePowerList.size()) + (constants.getDateTitleCount(EnginePowerList) * inspectionLayHeight) + (headerViewHeight * constants.getListNewDateCount(EnginePowerList));
            final int UnIdenfdVehListHeight = (inspectionLayHeight * UnAssignedVehicleList.size()+1) + headerViewHeight;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dutyChangeDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DutyStatusListHeight));
                    loginLogDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LoginLogoutListHeight));
                    remAnotnDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, CommentsRemarksListHeight));
                    cycleOpZoneDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, CycleOpZoneListHeight));
                    enginePwrDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, EnginePowerListHeight));
                    unIdnfdVehDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UnIdenfdVehListHeight));

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
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.Date, date);

        GetDotLogRequest.executeRequest(Request.Method.POST, APIs.MOBILE_CANADA_ELD_VIEW, params, 1,
                Constants.SocketTimeout50Sec, ResponseCallBack, ErrorCallBack);
    }


    void setDataOnTextView(JSONObject dataObj){
        try{
            dateRodsTV.setText(global.ConvertDateFormatddMMMyyyy(dataObj.getString("RecordDate")));
            dayStartTimeTV.setText(dataObj.getString("PeriodStartingTime"));
            timeZoneCTV.setText(dataObj.getString("TimeZone"));
            currLocCTV.setText(Html.fromHtml(dataObj.getString("CurrentLocation")) );
            commentCTV.setText(constants.checkNullString(dataObj.getString("OutputFileComment")) );
            dateTimeCTV.setText(Globally.ConvertDateFormatMMddyyyyHHmm(dataObj.getString("CurrentDate")));

            driverNameCTV.setText(dataObj.getString("DriverLastAndFirstName"));
            driverIdCTV.setText(dataObj.getString("DriverLoginId"));
            exemptDriverCTV.setText(dataObj.getString("ExemptDriverStatus"));
            driLicNoCTV.setText(dataObj.getString("DriverLicenseNumber") + " (" + dataObj.getString("DriverLicenseState") + ")");
            coDriverNameCTV.setText(dataObj.getString("CoDriverLastAndFirstName"));
            coDriverIdCTV.setText(dataObj.getString("CoDriverLoginId"));

            trailerIdCTV.setText(dataObj.getString("TrailerId"));
            carrierNameCTV.setText(dataObj.getString("Carrier"));
            carrierHomeTerCTV.setText(dataObj.getString("Hometerminal"));
            carrierPrinPlaceCTV.setText(dataObj.getString("OfficeAddress"));
            currOperZoneCTV.setText(dataObj.getString("CurrentOperatingZone"));
            curreCycleCTV.setText(dataObj.getString("CurrentCycle"));    // + "\n" + dataObj.getString("cycledetail"));



            String truckTractorId = "", truckTractorVin = "", totalDisC = "", distanceToday = "",  currTotalDis = "", currTotalEng = "";

            JSONArray EngineHourArray = new JSONArray(dataObj.getString("EngineHourMilesReportList"));
            if(EngineHourArray.length() > 0) {

                for (int i = 0; i < EngineHourArray.length(); i++) {
                    JSONObject engineObj = (JSONObject) EngineHourArray.get(i);
                    if(i == 0) {
                        truckTractorId  = "" + (i+1) + ") "+ engineObj.getString("TruckEquipmentNo");
                        truckTractorVin = "" + (i+1) + ") "+ engineObj.getString("CMVVIN");
                        totalDisC       = "" + (i+1) + ") "+ engineObj.getString("StartOdometr") + " - " + engineObj.getString("EndOdometer");
                        distanceToday   = "" + (i+1) + ") "+ engineObj.getString("DifferenceKM") ;
                    }else{
                        truckTractorId = truckTractorId     + "\n" + (i+1) + ") "+ engineObj.getString("TruckEquipmentNo");
                        truckTractorVin = truckTractorVin   + "\n" + (i+1) + ") "+ engineObj.getString("CMVVIN");
                        totalDisC       = totalDisC         + "\n" + (i+1) + ") "+ engineObj.getString("StartOdometr") + " - " + engineObj.getString("EndOdometer");
                        distanceToday   = distanceToday     + "\n" + (i+1) + ") "+ engineObj.getString("DifferenceKM") ;
                    }

                    currTotalDis = engineObj.getString("EndOdometer");
                    currTotalEng = engineObj.getString("EndEngineHours");

                }

            }else{
                truckTractorId  = dataObj.getString("TruckTractorID");
                truckTractorVin = dataObj.getString("TruckTractorVIN");
            }

            truckTractorIdTV.setText(truckTractorId);
            truckTractorVinTV.setText(truckTractorVin);
            totalDisCTV.setText(totalDisC);
            distanceTodayCTV.setText(distanceToday);
            currTotalDisTV.setText(currTotalDis);
            currTotalEngTV.setText(currTotalEng);


            String currentCycle = "";
            if (sharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {

                if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
                    currentCycle = "USA Cycle \n" + DriverConst.GetDriverSettings(DriverConst.USACycleName, getActivity());
                }else{
                    currentCycle = "CAN Cycle \n" + DriverConst.GetDriverSettings(DriverConst.CANCycleName, getActivity());
                }
            }else{
                if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
                    currentCycle = "USA Cycle \n" + DriverConst.GetCoDriverSettings(DriverConst.CoUSACycleName, getActivity());
                }else{
                    currentCycle = "CAN Cycle \n" + DriverConst.GetCoDriverSettings(DriverConst.CoCANCycleName, getActivity());
                }
            }
            curreCycleCTV.setText(currentCycle);

            JSONObject DriverLogDetailModelObj = new JSONObject(dataObj.getString("DriverLogDetailModel"));
            JSONObject RulesApiModelNewCanadaObj = new JSONObject(DriverLogDetailModelObj.getString("RulesApiModelNewCanada"));

            int TotalHrsInShift = (int)RulesApiModelNewCanadaObj.getDouble("shiftUsedMinutes");
            int TotalHrsInCycle = (int)RulesApiModelNewCanadaObj.getDouble("cycleUsedMinutes");
            int RemainingHrsCycle = (int)RulesApiModelNewCanadaObj.getDouble("cycleRemainingMinutes");
           // int OffDutyTimeDefferal = (int)RulesApiModelNewCanadaObj.getDouble("");



            totalHrsCTV.setText(Globally.FinalValue(TotalHrsInShift));
            totalhrsCycleCTV.setText(Globally.FinalValue(TotalHrsInCycle));
            remainingHourCTV.setText(Globally.FinalValue(RemainingHrsCycle));
            offDutyDeffCTV.setText(constants.checkNullString(dataObj.getString("OffDutyTimeDefferal")) );


            datDiagCTV.setText(dataObj.getString("DataDiagnosticIndicators"));
            unIdenDriRecCTV.setText(dataObj.getString("UnIdentifiedDriverRecords"));

            malfStatusCTV.setText(dataObj.getString("ELDMalfunctionIndicators"));
            eldIdCTV.setText(dataObj.getString("ELDID"));
            eldProviderCTV.setText(dataObj.getString("ELDManufacturer"));
            eldCerCTV.setText(constants.checkNullString(dataObj.getString("ELDCertificationID")) );
            eldAuthCTV.setText(constants.checkNullString(dataObj.getString("ELDAuthenticationValue")) );

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

            String status = "", Message = "";
            JSONObject dataObj = null;
            try {

                JSONObject obj = new JSONObject(response);

                status = obj.getString("Status");
                Message = obj.getString("Message");
                if (!obj.isNull("Data")) {
                    dataObj = new JSONObject(obj.getString("Data"));
                }
                canDotProgressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (status.equalsIgnoreCase("true")) {

                    LogSignImage = "";
                    TotalOnDutyHours         = "00:00";
                    TotalDrivingHours        = "00:00";
                    TotalOffDutyHours        = "00:00";
                    TotalSleeperBerthHours   = "00:00";

                    DutyStatusList = new ArrayList();
                    LoginLogoutList = new ArrayList();
                    CommentsRemarksList = new ArrayList();
                    CycleOpZoneList = new ArrayList();
                    EnginePowerList = new ArrayList();


                    try {
                        TotalOffDutyHours       = dataObj.getString("TotalOffDutyHours");
                        TotalSleeperBerthHours  = dataObj.getString("TotalSleeperHours");
                        TotalDrivingHours       = dataObj.getString("TotalDrivingHours");
                        TotalOnDutyHours        = dataObj.getString("TotalOnDutyHours");

                        TotalOffDutyHours      = TotalOffDutyHours.replaceAll("-", "");
                        TotalOnDutyHours       = TotalOnDutyHours.replaceAll("-", "");
                        TotalDrivingHours      = TotalDrivingHours.replaceAll("-", "");
                        TotalSleeperBerthHours = TotalSleeperBerthHours.replaceAll("-", "");

                        JSONArray dotLogArray = new JSONArray(dataObj.getString("graphRecordList"));
                        ParseGraphData(dotLogArray);

                        JSONArray dutyStatusArray = new JSONArray(dataObj.getString(ConstantsKeys.dutyStatusChangesList));
                        JSONArray loginLogoutArray = new JSONArray(dataObj.getString(ConstantsKeys.loginAndLogoutList));
                        JSONArray ChangeInDriversCycleList = new JSONArray(dataObj.getString(ConstantsKeys.ChangeInDriversCycleList));
                        JSONArray commentsRemarksArray = new JSONArray(dataObj.getString(ConstantsKeys.commentsRemarksList));
                        JSONArray enginePowerArray = new JSONArray(dataObj.getString(ConstantsKeys.enginePowerUpAndShutDownList));
                        JSONArray unIdentifiedVehArray = new JSONArray(dataObj.getString(ConstantsKeys.UnAssignedVehicleMilesList));

                        DutyStatusList =   constants.parseCanadaDotInList(dutyStatusArray, true);
                        LoginLogoutList = constants.parseCanadaDotInList(loginLogoutArray, true);
                        CommentsRemarksList = constants.parseCanadaDotInList(commentsRemarksArray, false);
                        CycleOpZoneList = constants.parseCanadaDotInList(ChangeInDriversCycleList, true);
                        EnginePowerList = constants.parseCanadaDotInList(enginePowerArray, true);

                        UnAssignedVehicleList = constants.parseCanadaDotUnIdenfdVehList(unIdentifiedVehArray);

                        setdataOnAdapter();
                        setDataOnTextView(dataObj);

                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }else{
                    htmlAppendedText    = "";

                    String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
                    ReloadWebView(CloseTag);

                    Globally.EldScreenToast(dateRodsTV, Message, getResources().getColor(R.color.colorVoilation));

                }
             } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            if(getActivity() != null) {
                try {
                    Log.d("error", ">>error: " + error);
                    canDotProgressBar.setVisibility(View.GONE);
                    Globally.EldScreenToast(dateRodsTV, "Error", getResources().getColor(R.color.colorVoilation));
                    htmlAppendedText = "";
                    TotalOnDutyHours = "00:00";
                    TotalDrivingHours = "00:00";
                    TotalOffDutyHours = "00:00";
                    TotalSleeperBerthHours = "00:00";


                    String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
                    ReloadWebView(CloseTag);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };





}
