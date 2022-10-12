package com.messaging.logistic.fragment;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.logistic.CanDotCycleOpZoneAdapter;
import com.adapter.logistic.CanDotDutyStatusAdapter;
import com.adapter.logistic.CanDotEnginePowerAdapter;
import com.adapter.logistic.CanDotLogInOutAdapter;
import com.adapter.logistic.CanDotRemarksAdapter;
import com.adapter.logistic.UnidentifiedDutyStatusAdapter;
import com.adapter.logistic.UnidentifiedEnginePowerAdapter;
import com.adapter.logistic.UnidentifiedLogInOutAdapter;
import com.adapter.logistic.UnidentifiedRemarksAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.ConstantHtml;
import com.constants.Constants;
import com.constants.DoubleClickListener;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.SwitchTrackTextDrawable;
import com.constants.Utils;
import com.constants.VolleyRequest;
import com.constants.VolleyRequestWithoutRetry;
import com.constants.WebAppInterface;
import com.custom.dialogs.DatePickerDialog;
import com.custom.dialogs.DotOtherOptionDialog;
import com.custom.dialogs.DriverLocationDialog;
import com.custom.dialogs.GenerateRodsDialog;
import com.custom.dialogs.ShareDriverLogDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.messaging.logistic.EldActivity;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.models.CanadaDutyStatusModel;
import com.models.DriverLocationModel;
import com.models.UnAssignedVehicleModel;
import com.models.UnidentifiedEventModel;
import com.shared.pref.StatePrefManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import lib.kingja.switchbutton.SwitchMultiButton;

public class DotCanadaFragment extends Fragment implements View.OnClickListener{


    View rootView;

    HelperMethods hMethods;
    TextView dateRodsTV, dayStartTimeTV, timeZoneCTV, currLocCTV, commentCTV, dateTimeCTV;
    TextView driverNameCTV, driverIdCTV, exemptDriverCTV, driLicNoCTV, coDriverNameCTV, coDriverIdCTV;
    TextView viewMoreTV, EldTitleTV;
    TextView truckTractorIdTV, truckTractorVinTV, totalDisCTV, distanceTodayCTV, currTotalDisTV, currTotalEngTV;
    TextView trailerIdCTV, carrierNameCTV, carrierHomeTerCTV, carrierPrinPlaceCTV, currOperZoneCTV, curreCycleCTV;
    TextView totalHrsCTV, totalhrsCycleCTV, remainingHourCTV, offDutyDeffCTV, datDiagCTV, unIdenDriRecCTV;
    TextView malfStatusCTV, eldIdCTV, eldProviderCTV, eldCerCTV, eldAuthCTV, eventDotETV;   //canDotModeTxtVw

    TextView unIdnfddateRodsTV, unIdnfddayStartTimeTV, unIdnfdtimeZoneCTV, unIdnfdcurrLocCTV, unIdnfdcommentCTV, unIdnfddateTimeCTV;
    TextView unIdnfddriverNameCTV, unIdnfddriverIdCTV, unIdnfdexemptDriverCTV, unIdnfddriLicNoCTV, unIdnfdcoDriverNameCTV, unIdnfdcoDriverIdCTV;
    TextView unIdnfdviewMoreTV;
    TextView unIdnfdtruckTractorIdTV, unIdnfdtruckTractorVinTV, unIdnfdtotalDisCTV, unIdnfddistanceTodayCTV, unIdnfdcurrTotalDisTV, unIdnfdcurrTotalEngTV;
    TextView unIdnfdtrailerIdCTV, unIdnfdcarrierNameCTV, unIdnfdcarrierHomeTerCTV, unIdnfdcarrierPrinPlaceCTV, unIdnfdcurrOperZoneCTV, unIdnfdcurreCycleCTV;
    TextView unIdnfdtotalHrsCTV, unIdnfdtotalhrsCycleCTV, unIdnfdremainingHourCTV, unIdnfdoffDutyDeffCTV, unIdnfddatDiagCTV, UnunIdenDriRecCTV;
    TextView unIdnfdmalfStatusCTV, unIdnfdeldIdCTV, unIdnfdeldProviderCTV, unIdnfdeldCerCTV, unIdnfdeldAuthCTV;

    RelativeLayout eldMenuLay, rightMenuBtn, scrollUpBtn, scrollDownBtn, otherOptionBtn;
    LinearLayout canDotViewMorelay , unIdnfdcanDotViewMorelay;
    ImageView eldMenuBtn, nextDateBtn, previousDateBtn; // canDotModeImgVw;

    WebView canDotGraphWebView,unIdnfdcanDotGraphWebView;
    ProgressBar canDotProgressBar;
    NestedScrollView canDotScrollView;
    SwitchMultiButton canDotSwitchBtn;

    RecyclerView dutyChangeDotListView,loginLogDotListView,cycleOpZoneDotListView,remAnotnDotListView,enginePwrDotListView;
    RecyclerView unIdnfddutyChangeDotListView,unIdnfdloginLogDotListView,unIdnfdremAnotnDotListView,unIdnfdenginePwrDotListView;

    Constants constants;
    Globally global;
    VolleyRequest GetDotLogRequest;
    VolleyRequestWithoutRetry GetAddFromLatLngRequest;
    Map<String, String> params;


    CanDotDutyStatusAdapter canDotDutyStatusAdapter;
    CanDotLogInOutAdapter canDotLogInOutAdapter;
    CanDotCycleOpZoneAdapter canDotCycleOpZoneAdapter;
    CanDotRemarksAdapter canDotRemarksAdapter;
    CanDotEnginePowerAdapter canDotEnginePowerAdapter;
    DotOtherOptionDialog dotOtherOptionDialog;

    UnidentifiedDutyStatusAdapter unidentifiedDutyStatusAdapter;
    UnidentifiedLogInOutAdapter unidentifiedLogInOutAdapter;
    UnidentifiedRemarksAdapter unidentifiedRemarksAdapter;
    UnidentifiedEnginePowerAdapter unidentifiedEnginePowerAdapter;

    List<CanadaDutyStatusModel> VerifyDutyStatusList = new ArrayList();
    List<CanadaDutyStatusModel> VerifyEnginePowerList = new ArrayList();
    List<CanadaDutyStatusModel> VerifyLoginLogoutList = new ArrayList();
    List<CanadaDutyStatusModel> VerifyCommentsRemarksList = new ArrayList();

    List<CanadaDutyStatusModel> DutyStatusList = new ArrayList();
    List<CanadaDutyStatusModel> LoginLogoutList = new ArrayList();
    List<CanadaDutyStatusModel> CycleOpZoneList = new ArrayList();
    List<CanadaDutyStatusModel> CommentsRemarksList = new ArrayList();
    List<CanadaDutyStatusModel> EnginePowerList = new ArrayList();

    List<CanadaDutyStatusModel> UnIdnfdDutyStatusList = new ArrayList();
    List<CanadaDutyStatusModel> UnIdnfdLoginLogoutList = new ArrayList();
    List<CanadaDutyStatusModel> UnIdnfdCommentsRemarksList = new ArrayList();
    List<CanadaDutyStatusModel> UnIdnfdEnginePowerList = new ArrayList();

    GenerateRodsDialog generateRodsDialog;
    DatePickerDialog dateDialog;
    ShareDriverLogDialog shareDialog;
    List<String> StateArrayList = new ArrayList<>();
    List<DriverLocationModel> StateList = new ArrayList<>();
    List<String> countryList = new ArrayList<>();

    int DaysDiff = 0;
    int SelectedDayOfMonth  = 0;
    int UsaMaxDays          = 7;
    int CanMaxDays          = 14;
    int MaxDays;
    final int GetAddFromLatLng      = 160;
    boolean isUnidentified = false;
    int CURRENT_JOB_STATUS  = 0;

   /* public static int DutyStatusHeaderCount  = 1;
    public static int LoginLogoutHeaderCount = 1;
    public static int CycleOpZoneHeaderCount = 1;
    public static int EnginePowerHeaderCount = 1;
*/

    String DayName, MonthFullName , MonthShortName , CurrentCycleId;  // CountryCycle;
    String DefaultLine      = " <g class=\"event \">\n";

    String DriverId = "", DeviceId = "", AddressLine = "", AddressLat = "", AddressLon = "";
    String htmlAppendedText = "", LogDate = "", CurrentDate = "", LogSignImage = "", selectedCountryRods = "";
    String htmlUnidentifiedAppendedText = "";
    String TotalOnDutyHours         = "00:00";
    String TotalDrivingHours        = "00:00";
    String TotalOffDutyHours        = "00:00";
    String TotalSleeperBerthHours   = "00:00";
    String TotalUnidenitifiedOffDutyHours = "00:00";
    String TotalUnidenitifiedDrivingHours = "00:00";
    String TotalUnidenitifiedSleeperHours = "00:00";
    String TotalUnidenitifiedOnDutyHours = "00:00";


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

        Thread.setDefaultUncaughtExceptionHandler(onRuntimeError);

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }

        if(UILApplication.getInstance().isNightModeEnabled()){
            getActivity().setTheme(R.style.DarkTheme);
        } else {
            getActivity().setTheme(R.style.LightTheme);
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
        GetDotLogRequest    = new VolleyRequest(getActivity());
        GetAddFromLatLngRequest = new VolleyRequestWithoutRetry(getActivity());

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
       // canDotModeTxtVw     = (TextView)view.findViewById(R.id.canDotModeTxtVw);

        viewMoreTV          = (TextView)view.findViewById(R.id.viewMoreTV);
        EldTitleTV          = (TextView)view.findViewById(R.id.EldTitleTV);
        eventDotETV         = (TextView)view.findViewById(R.id.eventDotETV);

        dutyChangeDotListView= (RecyclerView) view.findViewById(R.id.dutyChangeDotListView);
        remAnotnDotListView  = (RecyclerView) view.findViewById(R.id.remAnotnDotListView);
        cycleOpZoneDotListView    = (RecyclerView)view.findViewById(R.id.addHrsDotListView);
        loginLogDotListView  = (RecyclerView) view.findViewById(R.id.loginLogDotListView);
        enginePwrDotListView = (RecyclerView) view.findViewById(R.id.enginePwrDotListView);

        canDotViewMorelay   = (LinearLayout)view.findViewById(R.id.canDotViewMorelay);

        unIdnfddateRodsTV              = (TextView)view.findViewById(R.id.unIdnfddateRodsTV);
        unIdnfddayStartTimeTV          = (TextView)view.findViewById(R.id.unIdnfddayStartTimeTV);
        unIdnfdtimeZoneCTV             = (TextView)view.findViewById(R.id.unIdnfdtimeZoneCTV);
        unIdnfdcurrLocCTV              = (TextView)view.findViewById(R.id.unIdnfdcurrLocCTV);
        unIdnfdcommentCTV              = (TextView)view.findViewById(R.id.unIdnfdcommentCTV);
        unIdnfddateTimeCTV             = (TextView)view.findViewById(R.id.unIdnfddateTimeCTV);

        unIdnfddriverNameCTV           = (TextView)view.findViewById(R.id.unIdnfddriverNameCTV);
        unIdnfddriverIdCTV             = (TextView)view.findViewById(R.id.unIdnfddriverIdCTV);
        unIdnfdexemptDriverCTV         = (TextView)view.findViewById(R.id.unIdnfdexemptDriverCTV);
        unIdnfddriLicNoCTV             = (TextView)view.findViewById(R.id.unIdnfddriLicNoCTV);
        unIdnfdcoDriverNameCTV         = (TextView)view.findViewById(R.id.unIdnfdcoDriverNameCTV);
        unIdnfdcoDriverIdCTV           = (TextView)view.findViewById(R.id.unIdnfdcoDriverIdCTV);

        unIdnfdtruckTractorIdTV        = (TextView)view.findViewById(R.id.unIdnfdtruckTractorIdTV);
        unIdnfdtruckTractorVinTV       = (TextView)view.findViewById(R.id.unIdnfdtruckTractorVinTV);
        unIdnfdtotalDisCTV             = (TextView)view.findViewById(R.id.unIdnfdtotalDisCTV);
        unIdnfddistanceTodayCTV        = (TextView)view.findViewById(R.id.unIdnfddistanceTodayCTV);
        unIdnfdcurrTotalDisTV          = (TextView)view.findViewById(R.id.unIdnfdcurrTotalDisTV);
        unIdnfdcurrTotalEngTV          = (TextView)view.findViewById(R.id.unIdnfdcurrTotalEngTV);

        unIdnfdtrailerIdCTV            = (TextView)view.findViewById(R.id.unIdnfdtrailerIdCTV);
        unIdnfdcarrierNameCTV          = (TextView)view.findViewById(R.id.unIdnfdcarrierNameCTV);
        unIdnfdcarrierHomeTerCTV       = (TextView)view.findViewById(R.id.unIdnfdcarrierHomeTerCTV);
        unIdnfdcarrierPrinPlaceCTV     = (TextView)view.findViewById(R.id.unIdnfdcarrierPrinPlaceCTV);
        unIdnfdcurrOperZoneCTV         = (TextView)view.findViewById(R.id.unIdnfdcurrOperZoneCTV);
        unIdnfdcurreCycleCTV           = (TextView)view.findViewById(R.id.unIdnfdcurreCycleCTV);

        unIdnfdtotalHrsCTV             = (TextView)view.findViewById(R.id.unIdnfdtotalHrsCTV);
        unIdnfdtotalhrsCycleCTV        = (TextView)view.findViewById(R.id.unIdnfdtotalhrsCycleCTV);
        unIdnfdremainingHourCTV        = (TextView)view.findViewById(R.id.unIdnfdremainingHourCTV);
        unIdnfdoffDutyDeffCTV          = (TextView)view.findViewById(R.id.unIdnfdoffDutyDeffCTV);
        unIdnfddatDiagCTV              = (TextView)view.findViewById(R.id.unIdnfddatDiagCTV);
        UnunIdenDriRecCTV              = (TextView)view.findViewById(R.id.UnunIdenDriRecCTV);

        unIdnfdmalfStatusCTV           = (TextView)view.findViewById(R.id.unIdnfdmalfStatusCTV);
        unIdnfdeldIdCTV                = (TextView)view.findViewById(R.id.unIdnfdeldIdCTV);
        unIdnfdeldProviderCTV          = (TextView)view.findViewById(R.id.unIdnfdeldProviderCTV);
        unIdnfdeldCerCTV               = (TextView)view.findViewById(R.id.unIdnfdeldCerCTV);
        unIdnfdeldAuthCTV              = (TextView)view.findViewById(R.id.unIdnfdeldAuthCTV);

        unIdnfddutyChangeDotListView   = (RecyclerView) view.findViewById(R.id.unIdnfddutyChangeDotListView);
        unIdnfdremAnotnDotListView     = (RecyclerView) view.findViewById(R.id.unIdnfdremAnotnDotListView);
        unIdnfdloginLogDotListView     = (RecyclerView) view.findViewById(R.id.unIdnfdloginLogDotListView);
        unIdnfdenginePwrDotListView    = (RecyclerView) view.findViewById(R.id.unIdnfdenginePwrDotListView);

        unIdnfdviewMoreTV              = (TextView)view.findViewById(R.id.unIdnfdviewMoreTV);
        unIdnfdcanDotViewMorelay       = (LinearLayout)view.findViewById(R.id.unIdnfdcanDotViewMorelay);

        eldMenuLay          = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        rightMenuBtn        = (RelativeLayout)view.findViewById(R.id.rightMenuBtn);
        scrollUpBtn         = (RelativeLayout)view.findViewById(R.id.scrollUpBtn);
        scrollDownBtn       = (RelativeLayout)view.findViewById(R.id.scrollDownBtn);
        otherOptionBtn      = (RelativeLayout)view.findViewById(R.id.otherOptionBtn);

        eldMenuBtn          = (ImageView)view.findViewById(R.id.eldMenuBtn);
        nextDateBtn         = (ImageView)view.findViewById(R.id.nextDateBtn);
        previousDateBtn     = (ImageView)view.findViewById(R.id.previousDate);
      //  canDotModeImgVw     = (ImageView)view.findViewById(R.id.canDotModeImgVw);

        canDotGraphWebView  = (WebView)view.findViewById(R.id.canDotGraphWebView);
        unIdnfdcanDotGraphWebView = (WebView)view.findViewById(R.id.unIdnfdcanDotGraphWebView);

        canDotProgressBar   = (ProgressBar)view.findViewById(R.id.canDotProgressBar);
        canDotScrollView    = (NestedScrollView) view.findViewById(R.id.canDotScrollView);

        canDotSwitchBtn     = (SwitchMultiButton) view.findViewById(R.id.canDotSwitchBtn);
        canDotSwitchBtn.setOnSwitchListener (onSwitchListener);


        eldMenuBtn.setImageResource(R.drawable.back_btn);
        viewMoreTV.setText(Html.fromHtml("<u>" + getResources().getString(R.string.view_more) + "</u>"));
        unIdnfdviewMoreTV.setText(Html.fromHtml("<u>" + getResources().getString(R.string.view_more) + "</u>"));
        rightMenuBtn.setVisibility(View.GONE);
        otherOptionBtn.setVisibility(View.VISIBLE);

        getBundleData();
        initilizeWebView();
        ReloadWebView(constants.HtmlCloseTag("00:00", "00:00", "00:00", "00:00"));

        initilizeUnidentiWebView();
        ReloadUnidentiWebView(constants.HtmlCloseTag("00:00", "00:00", "00:00", "00:00"));

        countryList.add("Select");
        countryList.add("CANADA");
        countryList.add("USA");

        if (global.isConnected(getActivity())) {

            String selectedDateStr = global.ConvertDateFormat(LogDate);
            String currentDateStr = global.ConvertDateFormat(CurrentDate);
            DateTime selectedDateTime = global.getDateTimeObj(selectedDateStr, false) ;
            DateTime currentDateTime = global.getDateTimeObj(currentDateStr, false) ;
            int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
            Logger.LogDebug("DaysDiff", "DaysDiff: " + DaysDiff);

            DOTBtnVisibility(DaysDiff, MaxDays);

            GetDriverDotDetails(DriverId, LogDate);

        }else{
            Globally.EldScreenToast(getActivity().findViewById(android.R.id.content), global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
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

       /* Switch dotSwitchBtn = (Switch)view.findViewById(R.id.dotSwitchBtn);
        dotSwitchBtn.setTrackDrawable(new SwitchTrackTextDrawable(getActivity(),"CAN", "US"));
*/

        eldMenuLay.setOnClickListener(this);
        viewMoreTV.setOnClickListener(this);
        unIdnfdviewMoreTV.setOnClickListener(this);
        nextDateBtn.setOnClickListener(this);
        previousDateBtn.setOnClickListener(this);
       // canDotModeImgVw.setOnClickListener(this);
        EldTitleTV.setOnClickListener(this);
        otherOptionBtn.setOnClickListener(this);
        scrollUpBtn.setOnClickListener(this);
        scrollDownBtn.setOnClickListener(this);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }


    SwitchMultiButton.OnSwitchListener onSwitchListener = new SwitchMultiButton.OnSwitchListener() {
        @Override
        public void onSwitch(int position, String tabText) {

            if ( position == 0) {
                if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS)) {
                    CurrentCycleId = DriverConst.GetDriverSettings(DriverConst.CANCycleId, getActivity());
                } else {
                    CurrentCycleId = DriverConst.GetDriverSettings(DriverConst.USACycleId, getActivity());
                }

                String obdCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());
                if (obdCycleId.equals(global.USA_WORKING_6_DAYS) || obdCycleId.equals(global.USA_WORKING_7_DAYS)) {
                    if (getParentFragmentManager().getBackStackEntryCount() > 1) {
                        getParentFragmentManager().popBackStack();
                    } else {
                        moveToDotMode(LogDate, DayName, MonthFullName, MonthShortName);
                    }
                } else {
                    moveToDotMode(LogDate, DayName, MonthFullName, MonthShortName);
                }
                canDotSwitchBtn.setSelectedTab(1);
            }
        }
    };

        private void getBundleData() {

            CurrentDate             = global.GetCurrentDeviceDate();
            DeviceId                = SharedPref.GetSavedSystemToken(getActivity());
            DriverId               = SharedPref.getDriverId( getActivity());

            try {
                Bundle getBundle = this.getArguments();
                if (getBundle != null) {
                    LogDate = getBundle.getString("date");
                    DayName = getBundle.getString("day_name");
                    MonthFullName = getBundle.getString("month_full_name");
                    MonthShortName = getBundle.getString("month_short_name");
                    CurrentCycleId = getBundle.getString("cycle");
                    SelectedDayOfMonth = getBundle.getInt("day_of_month");
                    CURRENT_JOB_STATUS = getBundle.getInt("current_status");
                    //CountryCycle =DriverConst.GetCurrentCycleName(DriverConst.GetCurrentDriverType(getActivity()), getActivity());
                    //  getBundle.clear();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            CurrentDate = global.GetCurrentDeviceDate();

            try {
                DBHelper dbHelper = new DBHelper(getActivity());
                DriverPermissionMethod driverPermissionMethod = new DriverPermissionMethod();
                JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DriverId), dbHelper);
                CanMaxDays = constants.GetDriverPermitDaysCount(logPermissionObj, CurrentCycleId, true);

                if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS)) {
                    MaxDays = UsaMaxDays;
                } else {
                    MaxDays = CanMaxDays;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            if(LogDate.length() > 1) {
                EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " ( " + DayName + " )");
           }

            try {
                StatePrefManager statePrefManager  = new StatePrefManager();
                StateList = statePrefManager.GetState(getActivity());
                StateList.add(0, new DriverLocationModel("", "Select", ""));
            } catch (Exception e) { }

            StateArrayList =  SharedPref.getStatesInList(getActivity());
            StateArrayList.add(0, "Select");


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

                dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, LogDate, new DateListener(), false);
                dateDialog.show();

                break;


            case R.id.otherOptionBtn:
                try{
                    dotOtherOptionDialog = new DotOtherOptionDialog(getActivity(), new OtherOptionDotListener());
                    dotOtherOptionDialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
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

            case R.id.unIdnfdviewMoreTV:
                if(unIdnfdcanDotViewMorelay.getVisibility() == View.VISIBLE){
                    unIdnfdcanDotViewMorelay.setVisibility(View.GONE);
                    unIdnfdviewMoreTV.setText(Html.fromHtml("<u>" + getResources().getString(R.string.view_more) + "</u>"));
                    int scrollPos = canDotScrollView.getScrollY();
                    ObjectAnimator.ofInt(canDotScrollView, "scrollY",  scrollPos - 1500).setDuration(600).start();
//                    canDotScrollView.fullScroll(View.FOCUS_UP);
                }else{
                    unIdnfdcanDotViewMorelay.setVisibility(View.VISIBLE);
                    unIdnfdviewMoreTV.setText(Html.fromHtml("<u>" + getResources().getString(R.string.view_less) + "</u>"));
                }

                break;


           /* case R.id.canDotModeImgVw:
                canDotModeTxtVw.performClick();
                break;*/

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


        }
    }


    void moveToDotMode(String date, String dayName, String dayFullName, String dayShortName){

        //getParentFragmentManager().popBackStackImmediate();

        FragmentManager fragManager = getActivity().getSupportFragmentManager();

        Fragment dotFragment = new DotUsaFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("day_name", dayName);
        bundle.putString("month_full_name", dayFullName);
        bundle.putString("month_short_name", dayShortName);
        bundle.putString("cycle", CurrentCycleId);
        dotFragment.setArguments(bundle);

        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, dotFragment);
        fragmentTran.addToBackStack("dot_can");  //"dot_log"
        fragmentTran.commitAllowingStateLoss();

    }


    private void ChangeViewWithDate(boolean isNext){

        String selectedDate = LogDate;
        String selectedDateStr = global.ConvertDateFormat(LogDate);
        String currentDateStr = global.ConvertDateFormat(CurrentDate);
        DateTime selectedDateTime = global.getDateTimeObj(selectedDateStr, false);
        DateTime currentDateTime = global.getDateTimeObj(currentDateStr, false);


        if (global.isConnected(getActivity())) {
            if(isNext){
                selectedDateTime = selectedDateTime.plusDays(1);
            }else{
                selectedDateTime = selectedDateTime.minusDays(1);
            }

            DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
            Logger.LogDebug("DaysDiff", "DaysDiff: " + DaysDiff);

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

                if(LogDate.length() > 1){
                    EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " (" + dayOfTheWeek + " )");
                    GetDriverDotDetails(DriverId, LogDate);
                }
            }

        }else{
            LogDate = selectedDate;
            global.EldScreenToast(scrollUpBtn, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            //webViewErrorDisplay();
        }

    }


    private void MoveFragment(String date ){
        InspectionsHistoryFragment savedInspectionFragment = new InspectionsHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("inspection_type", "pti_dot");
        savedInspectionFragment.setArguments(bundle);
        Constants.SelectedDatePti = date;

        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, savedInspectionFragment);
        fragmentTran.addToBackStack("inspection");
        fragmentTran.commitAllowingStateLoss();


    }

    private class OtherOptionDotListener implements DotOtherOptionDialog.OtherOptionDotListener{

        @Override
        public void ItemClickReady(int position) {

            if(position == 0){
                MoveFragment(LogDate);
            }else if(position == 1){
                if(CURRENT_JOB_STATUS == Constants.DRIVING){
                    Globally.EldScreenToast(rootView, getString(R.string.chnge_dr_to_othr),
                            getResources().getColor(R.color.colorVoilation));
                }else {
                    shareDriverLogDialog();
                }

            }else if(position == 2){
                generateRodsDialog = new GenerateRodsDialog(getActivity(), countryList, new GenerateRodsListner(),
                        DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity()));
                generateRodsDialog.show();
            }else {
                MoveDownloadLogFragment();
            }

          /*  if(position == 0){
                MoveFragment(LogDate);
            }else{
                shareDriverLogDialog();

            }*/
        }
    }


    private class GenerateRodsListner implements GenerateRodsDialog.RodsListner {
        @Override
        public void ChangeRodsReady(String Title, int position,String checkedMode,Date fromDate,Date toDate) {

            try {
//
                selectedCountryRods = countryList.get(position);
                SimpleDateFormat parseDate = new SimpleDateFormat(Globally.DateFormatHalf);
                String currentParseDatetime = parseDate.format(toDate);
                String lastParseDatetime =  parseDate.format(fromDate);
                if(selectedCountryRods == Globally.USA_CYCLE) {
                    DownloadPdfLog(Globally.USA_CYCLE,Globally.LOG_TYPE_ELD,lastParseDatetime,currentParseDatetime);
                    Globally.EldScreenToast(rootView, "Please wait,file download in progress.", getContext().getResources().getColor(R.color.color_eld_theme));
                }else{
                    if(!checkedMode.equals("")){
                        DownloadPdfLog(Globally.CANADA_CYCLE,checkedMode,lastParseDatetime,currentParseDatetime);
                        Globally.EldScreenToast(rootView, "Please wait,file download in progress.", getContext().getResources().getColor(R.color.color_eld_theme));
                    }
                }
                generateRodsDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private class DateListener implements DatePickerDialog.DatePickerListener{
        @Override
        public void JobBtnReady(String SelectedDate, String dayOfTheWeek, String monthFullName, String monthShortName, int dayOfMonth) {

            dateDialog.dismiss();

            LogDate = SelectedDate;
            DayName = dayOfTheWeek;
            MonthFullName = monthFullName;
            MonthShortName = monthShortName;
            if(LogDate.length() > 1) {
                EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " ( " + DayName + " )");
            }
            if (global.isConnected(getActivity())) {

                String selectedDateStr = global.ConvertDateFormat(LogDate);
                String currentDateStr = global.ConvertDateFormat(CurrentDate);
                DateTime selectedDateTime = global.getDateTimeObj(selectedDateStr, false);
                DateTime currentDateTime = global.getDateTimeObj(currentDateStr, false);
                DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
                Logger.LogDebug("DaysDiff", "DaysDiff: " + DaysDiff);

                DOTBtnVisibility(DaysDiff, MaxDays);
                GetDriverDotDetails(DriverId, LogDate);

            }else{
                global.EldScreenToast(scrollUpBtn, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            }

        }
    }



    void DownloadPdfLog(String country,String logtype,String fromDate,String toDate) {

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()));
        params.put(ConstantsKeys.FromDate, fromDate);
        params.put(ConstantsKeys.ToDate, toDate);
        params.put(ConstantsKeys.Country, country);
        params.put(ConstantsKeys.LogType, logtype);

        GetDotLogRequest.executeRequest(Request.Method.POST, APIs.DownloadPdfCanadaLog, params, 101,
                Constants.SocketTimeout50Sec, ResponseCallBack, ErrorCallBack);
    }

    private void MoveDownloadLogFragment(){
        DownloadRodsFragment logFragment = new DownloadRodsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("cycle", Globally.CANADA_CYCLE);
        logFragment.setArguments(bundle);


        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, logFragment);
        fragmentTran.addToBackStack("inspection");
        fragmentTran.commitAllowingStateLoss();


    }


    void shareDriverLogDialog() {

       // boolean IsAOBRDAutomatic        = SharedPref.IsAOBRDAutomatic(getActivity());
        boolean IsAOBRD                 = SharedPref.IsAOBRD(getActivity());

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

        try {
            if (Build.VERSION.SDK_INT >= 19) {
                // chromium, enable hardware acceleration
                canDotGraphWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                // older android version, disable hardware acceleration
                canDotGraphWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void initilizeUnidentiWebView(){
        WebSettings webSettings = unIdnfdcanDotGraphWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        unIdnfdcanDotGraphWebView.setWebViewClient(new WebViewClient());
        unIdnfdcanDotGraphWebView.setWebChromeClient(new WebChromeClient());
        unIdnfdcanDotGraphWebView.addJavascriptInterface( new WebAppInterface(), "Android");

        try {
            if (Build.VERSION.SDK_INT >= 19) {
                // chromium, enable hardware acceleration
                unIdnfdcanDotGraphWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                // older android version, disable hardware acceleration
                unIdnfdcanDotGraphWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void setdataOnAdapter(){

        try {
            canDotDutyStatusAdapter = new CanDotDutyStatusAdapter(getActivity(), DutyStatusList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            dutyChangeDotListView.setLayoutManager(manager);
            dutyChangeDotListView.setAdapter(canDotDutyStatusAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            canDotLogInOutAdapter = new CanDotLogInOutAdapter(getActivity(), LoginLogoutList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            loginLogDotListView.setLayoutManager(manager);
            loginLogDotListView.setAdapter(canDotLogInOutAdapter);
        }catch (Exception e){}

        try {
            canDotRemarksAdapter = new CanDotRemarksAdapter(getActivity(), CommentsRemarksList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            remAnotnDotListView.setLayoutManager(manager);
            remAnotnDotListView.setAdapter(canDotRemarksAdapter);
        }catch (Exception e){}

        try {
            canDotCycleOpZoneAdapter = new CanDotCycleOpZoneAdapter(getActivity(), CycleOpZoneList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            cycleOpZoneDotListView.setLayoutManager(manager);
            cycleOpZoneDotListView.setAdapter(canDotCycleOpZoneAdapter);
        }catch (Exception e){}

        try {
            canDotEnginePowerAdapter = new CanDotEnginePowerAdapter(getActivity(), EnginePowerList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            enginePwrDotListView.setLayoutManager(manager);
            enginePwrDotListView.setAdapter(canDotEnginePowerAdapter);
        }catch (Exception e){}

    }

    void setdataOnUnIdnfdAdapter(){

        try {
            unidentifiedDutyStatusAdapter = new UnidentifiedDutyStatusAdapter(getActivity(), UnIdnfdDutyStatusList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            unIdnfddutyChangeDotListView.setLayoutManager(manager);
            unIdnfddutyChangeDotListView.setAdapter(unidentifiedDutyStatusAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            unidentifiedLogInOutAdapter = new UnidentifiedLogInOutAdapter(getActivity(), UnIdnfdLoginLogoutList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            unIdnfdloginLogDotListView.setLayoutManager(manager);
            unIdnfdloginLogDotListView.setAdapter(unidentifiedLogInOutAdapter);
        }catch (Exception e){}

        try {
            unidentifiedRemarksAdapter = new UnidentifiedRemarksAdapter(getActivity(), UnIdnfdCommentsRemarksList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            unIdnfdremAnotnDotListView.setLayoutManager(manager);
            unIdnfdremAnotnDotListView.setAdapter(unidentifiedRemarksAdapter);
        }catch (Exception e){}

        try {
            unidentifiedEnginePowerAdapter = new UnidentifiedEnginePowerAdapter(getActivity(), UnIdnfdEnginePowerList);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            unIdnfdenginePwrDotListView.setLayoutManager(manager);
            unIdnfdenginePwrDotListView.setAdapter(unidentifiedEnginePowerAdapter);
        }catch (Exception e){}



    }


    int getHeight(int inspectionLayHeight, List<CanadaDutyStatusModel> list, int headerViewHeight, int dividerHeight){

       /* if (global.isTablet(getActivity())) {
            inspectionLayHeight = inspectionLayHeight;
        }else{
            inspectionLayHeight = inspectionLayHeight;
        }*/

        dividerHeight = dividerHeight * list.size();
        return (inspectionLayHeight * list.size()) + (constants.getDateTitleCount(list) * inspectionLayHeight) +
                (headerViewHeight * constants.getListNewDateCount(list)) + dividerHeight;

    }

    void ReloadWebView(final String closeTag){
        canDotGraphWebView.clearCache(true);
        canDotGraphWebView.loadData("", "text/html; charset=UTF-8", null );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String data = ConstantHtml.GraphHtml + htmlAppendedText + closeTag;
                canDotGraphWebView.loadDataWithBaseURL("" , data, "text/html", "UTF-8", "");
                if(global.isTablet(getActivity())){
                    canDotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            constants.dpToPx(getActivity(), 250)) );
                }else{
                    canDotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            constants.dpToPx(getActivity(), 160)) );
                }


            }
        }, 500);


    }

    void ReloadUnidentiWebView(final String closeTag){
        unIdnfdcanDotGraphWebView.clearCache(true);
        unIdnfdcanDotGraphWebView.loadData("", "text/html; charset=UTF-8", null );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String data = ConstantHtml.GraphHtml + htmlUnidentifiedAppendedText + closeTag;
                unIdnfdcanDotGraphWebView.loadDataWithBaseURL("" , data, "text/html", "UTF-8", "");
                if(global.isTablet(getActivity())){
                    unIdnfdcanDotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            constants.dpToPx(getActivity(), 250)) );
                }else{
                    unIdnfdcanDotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            constants.dpToPx(getActivity(), 160)) );
                }


            }
        }, 500);


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
                    endDateTime = global.GetCurrentDateTime();
                }else{
                    if(driverLogJsonArray.length() == 1 && LogDate.equals(CurrentDate)){
                        endDateTime = global.GetCurrentDateTime();
                    }
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
                        endDateTime = global.GetCurrentDateTime();
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

    void ParseUnidentifiedGraphData(ArrayList<UnidentifiedEventModel> driverLogList) {

        try {
            htmlUnidentifiedAppendedText    = "";

            for (int logCount = 0; logCount < driverLogList.size(); logCount++) {
                //JSONObject logObj = (JSONObject) driverLogJsonArray.get(logCount);
                UnidentifiedEventModel eventModel = driverLogList.get(logCount);
                int DRIVER_JOB_STATUS = eventModel.getEventCode();
                int EventType = eventModel.getEventType();

                boolean isYardMoveOrPersonal = false;

                if (EventType == 9 ) {

                    if(DRIVER_JOB_STATUS == 1){
                        DRIVER_JOB_STATUS = Constants.OFF_DUTY;
                    }else if(DRIVER_JOB_STATUS == 2 || DRIVER_JOB_STATUS == 3) {
                        DRIVER_JOB_STATUS = Constants.DRIVING;
                    }else if(DRIVER_JOB_STATUS == 4){
                        DRIVER_JOB_STATUS = Constants.ON_DUTY;
                    }


                }else{
                    if(logCount == 0) {
                        DRIVER_JOB_STATUS = Constants.OFF_DUTY;
                    }


                }



                String startDateTime = eventModel.getStartTime();   //logObj.getString("DateTimeWithMins");
                String endDateTime = eventModel.getEndDateTime();   //logObj.getString("EndDateTime");

                if (endDateTime.equals("null")) {
                    endDateTime = global.GetCurrentDateTime();
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(global.DateFormat);  //:SSSZ
                Date startDate = new Date();
                Date endDate = new Date();
                startHour = 0;
                startMin = 0;
                endHour = 0;
                endMin = 0;

             /*   if (logCount > 0 && logCount == driverLogJsonArray.length() - 1) {
                    if (LogDate.equals(CurrentDate)) {
                        endDateTime = global.GetCurrentDateTime();
                    } else {
                        if (endDateTime.length() > 16 && endDateTime.substring(11, 16).equals("00:00")) {
                            endDateTime = endDateTime.substring(0, 11) + "23:59" + endDateTime.substring(16, endDateTime.length());
                        }
                    }
                }*/

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
                        DrawUnidentifiedGraph(hLineX1, hLineX2, VerticalLineX, VerticalLineY, isYardMoveOrPersonal);
                    } else {
                        DrawUnidentifiedGraph(hLineX1, hLineX2, VerticalLineY, VerticalLineY, isYardMoveOrPersonal);
                    }
                }

                if (EventType == 9 ) {
                    OldStatus = DRIVER_JOB_STATUS;
                }

            }


            String CloseTag = constants.HtmlCloseTag(TotalUnidenitifiedOffDutyHours, TotalUnidenitifiedSleeperHours, TotalUnidenitifiedDrivingHours, TotalUnidenitifiedOnDutyHours);
            ReloadUnidentiWebView(CloseTag);


        } catch (Exception e) {
            e.printStackTrace();
            ReloadUnidentiWebView(constants.HtmlCloseTag("00:00", "00:00", "00:00", "00:00"));
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

    void DrawUnidentifiedGraph(int hLineX1, int hLineX2, int vLineY1, int vLineY2, boolean isDottedLine){

        if (isDottedLine) {
            htmlUnidentifiedAppendedText = htmlUnidentifiedAppendedText + DefaultLine +
                    constants.AddHorizontalDottedLine(hLineX1, hLineX2, vLineY2) +
                    constants.AddVerticalLine(hLineX1, vLineY1, vLineY2) +
                    "                  </g>\n";
        } else {
            htmlUnidentifiedAppendedText = htmlUnidentifiedAppendedText + DefaultLine +
                    constants.AddHorizontalLine(hLineX1, hLineX2, vLineY2) +
                    constants.AddVerticalLine(hLineX1, vLineY1, vLineY2) +
                    "                  </g>\n";
        }

    }


    //*================== Get Address From Lat Lng ===================*//*
    void GetAddFromLatLng() {

        AddressLine = "";
        AddressLat = Globally.LATITUDE;
        AddressLon = Globally.LONGITUDE;

        if(Globally.LATITUDE.length() > 4) {
            params = new HashMap<String, String>();
            params.put(ConstantsKeys.Latitude, AddressLat);
            params.put(ConstantsKeys.Longitude, AddressLon);
            params.put(ConstantsKeys.IsAOBRDAutomatic, String.valueOf(SharedPref.IsAOBRDAutomatic(getActivity())));

            GetAddFromLatLngRequest.executeRequest(Request.Method.POST, APIs.GET_Add_FROM_LAT_LNG, params, GetAddFromLatLng,
                    Constants.SocketTimeout3Sec, ResponseCallBack, ErrorCallBack);
        }
    }




    /* ================== Get Driver CANADA DOT Details =================== */
    void GetDriverDotDetails( final String DriverId, final String date) {

     //   GetAddFromLatLng();

        canDotProgressBar.setVisibility(View.VISIBLE);

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.Date, date);

        GetDotLogRequest.executeRequest(Request.Method.POST, APIs.MOBILE_CANADA_ELD_VIEW, params, 1,
                Constants.SocketTimeout50Sec, ResponseCallBack, ErrorCallBack);
    }


    void setDataOnTextView(JSONObject dataObj){
        try{
            dateRodsTV.setText(global.ConvertDateFormatddMMMyyyy(dataObj.getString("RecordDate"), Globally.DateFormat_dd_MMM_yyyy));
            dayStartTimeTV.setText(dataObj.getString("PeriodStartingTime"));
            timeZoneCTV.setText(dataObj.getString("TimeZone"));

            commentCTV.setText(constants.checkNullString(dataObj.getString("OutputFileComment")) );
            dateTimeCTV.setText(global.ConvertDateFormatMMddyyyyHHmm(dataObj.getString("CurrentDate")));

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


//3.3 km N Mountain View CA <br/> (37.42, -122.08)
            try {
                String CurrentOBDLocation = dataObj.getString("CurrentOBDLocation");
                String CurrentOBDLatLong = dataObj.getString("CurrentOBDLatLong");

                if (CurrentOBDLocation.equals("null")) {
                    currLocCTV.setText(Html.fromHtml(dataObj.getString("CurrentLocation")));
                } else {
                   // AddressLat = Constants.Convert2DecimalPlacesDouble(Double.parseDouble(AddressLat));
                   // AddressLon = Constants.Convert2DecimalPlacesDouble(Double.parseDouble(AddressLon));
                    currLocCTV.setText(Html.fromHtml(CurrentOBDLocation + "<br/>(" + CurrentOBDLatLong + ")"));
                }

            }catch (Exception e){
                //currLocCTV.setText(Html.fromHtml(AddressLine + "<br/>(" + AddressLat + ", " + AddressLon + ")"));
                currLocCTV.setText(Html.fromHtml(dataObj.getString("CurrentLocation")));
                e.printStackTrace();
            }

            String truckTractorId = "", truckTractorVin = "", totalVehDistance = "", distanceToday = "",  currTotalDis = "", currTotalEng = "";

            JSONArray EngineHourArray = constants.checkNullArray(dataObj,ConstantsKeys.EngineHourMilesReportList);
            if(EngineHourArray.length() > 0) {

                for (int i = 0; i < EngineHourArray.length(); i++) {
                    JSONObject engineObj = (JSONObject) EngineHourArray.get(i);
                    if(i == 0) {
                        truckTractorId  = "" + (i+1) + ") "+ engineObj.getString("TruckEquipmentNo");
                        truckTractorVin = "" + (i+1) + ") "+ engineObj.getString("CMVVIN");
                        totalVehDistance       = "" + (i+1) + ") "+ engineObj.getString("StartOdometr") + " - " + engineObj.getString("EndOdometer");
                        distanceToday   = "" + (i+1) + ") "+ engineObj.getString("DifferenceKM") ;
                    }else{
                        truckTractorId  = truckTractorId     + "\n" + (i+1) + ") "+ engineObj.getString("TruckEquipmentNo");
                        truckTractorVin = truckTractorVin   + "\n" + (i+1) + ") "+ engineObj.getString("CMVVIN");
                        totalVehDistance= totalVehDistance  + "\n" + (i+1) + ") "+ engineObj.getString("StartOdometr") + " - " + engineObj.getString("EndOdometer");
                        distanceToday   = distanceToday     + "\n" + (i+1) + ") "+ engineObj.getString("DifferenceKM") ;
                    }

                    currTotalDis = engineObj.getString("EndOdometer");
                    currTotalEng = engineObj.getString("EndEngineHours");
                   
                    try {
                        if(currTotalEng.length() > 0 && !currTotalEng.equals("--")) {
                            currTotalEng = Constants.Convert2DecimalPlacesString(currTotalEng);
                        }
                    }catch (Exception e){
                        currTotalEng = engineObj.getString("EndEngineHours");
                    }
                }

            }else{
                truckTractorId  = dataObj.getString("TruckTractorID");
                truckTractorVin = dataObj.getString("TruckTractorVIN");
            }

            truckTractorIdTV.setText(truckTractorId);
            truckTractorVinTV.setText(truckTractorVin);
            totalDisCTV.setText(totalVehDistance);
            distanceTodayCTV.setText(distanceToday);
            currTotalDisTV.setText(currTotalDis);
            currTotalEngTV.setText(currTotalEng);


        /*    String currentCycle = "";
            if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {

                if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS)) {
                    currentCycle = "USA Cycle \n" + DriverConst.GetDriverSettings(DriverConst.USACycleName, getActivity());
                }else{
                    currentCycle = "CAN Cycle \n" + DriverConst.GetDriverSettings(DriverConst.CANCycleName, getActivity());
                }
            }else{
                if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS)) {
                    currentCycle = "USA Cycle \n" + DriverConst.GetCoDriverSettings(DriverConst.CoUSACycleName, getActivity());
                }else{
                    currentCycle = "CAN Cycle \n" + DriverConst.GetCoDriverSettings(DriverConst.CoCANCycleName, getActivity());
                }
            }
            curreCycleCTV.setText(currentCycle);*/

            JSONObject DriverLogDetailModelObj = new JSONObject(dataObj.getString("DriverLogDetailModel"));
            JSONObject RulesApiModelNewCanadaObj = new JSONObject(DriverLogDetailModelObj.getString("RulesApiModelNewCanada"));

            int TotalHrsInShift = (int)RulesApiModelNewCanadaObj.getDouble("shiftUsedMinutes");
            int TotalHrsInCycle = (int)RulesApiModelNewCanadaObj.getDouble("cycleUsedMinutes");
            int RemainingHrsCycle = (int)RulesApiModelNewCanadaObj.getDouble("cycleRemainingMinutes");
           // int OffDutyTimeDefferal = (int)RulesApiModelNewCanadaObj.getDouble("");



            totalHrsCTV.setText(global.FinalValue(TotalHrsInShift));
            totalhrsCycleCTV.setText(global.FinalValue(TotalHrsInCycle));
            remainingHourCTV.setText(global.FinalValue(RemainingHrsCycle));
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

    void setDataOnUnIdnfdTextView(JSONObject dataObj){
        try{
            unIdnfddateRodsTV.setText(global.ConvertDateFormatddMMMyyyy(dataObj.getString("RecordDate"), Globally.DateFormat_dd_MMM_yyyy));
            unIdnfddayStartTimeTV.setText(dataObj.getString("PeriodStartingTime"));
            unIdnfdtimeZoneCTV.setText(dataObj.getString("TimeZone"));

            unIdnfdcommentCTV.setText("--");
            unIdnfddateTimeCTV.setText(global.ConvertDateFormatMMddyyyyHHmm(dataObj.getString("CurrentDate")));

            unIdnfddriverNameCTV.setText("Unidentified Driver");
            unIdnfddriverIdCTV.setText("Unidentified");
            unIdnfdexemptDriverCTV.setText(dataObj.getString("ExemptDriverStatus"));
            unIdnfddriLicNoCTV.setText("");
            unIdnfdcoDriverNameCTV.setText("");
            unIdnfdcoDriverIdCTV.setText("");

            unIdnfdtrailerIdCTV.setText(dataObj.getString("TrailerId"));
            unIdnfdcarrierNameCTV.setText(dataObj.getString("Carrier"));
            unIdnfdcarrierHomeTerCTV.setText(dataObj.getString("Hometerminal"));
            unIdnfdcarrierPrinPlaceCTV.setText(dataObj.getString("OfficeAddress"));
            unIdnfdcurrOperZoneCTV.setText("");
            unIdnfdcurreCycleCTV.setText("");    // + "\n" + dataObj.getString("cycledetail"));


//3.3 km N Mountain View CA <br/> (37.42, -122.08)
            try {
                String CurrentOBDLocation = dataObj.getString("CurrentOBDLocation");
                String CurrentOBDLatLong = dataObj.getString("CurrentOBDLatLong");

                if (CurrentOBDLocation.equals("null")) {
                    unIdnfdcurrLocCTV.setText(Html.fromHtml(dataObj.getString("CurrentLocation")));
                } else {
                    // AddressLat = Constants.Convert2DecimalPlacesDouble(Double.parseDouble(AddressLat));
                    // AddressLon = Constants.Convert2DecimalPlacesDouble(Double.parseDouble(AddressLon));
                    unIdnfdcurrLocCTV.setText(Html.fromHtml(CurrentOBDLocation + "<br/>(" + CurrentOBDLatLong + ")"));
                }

            }catch (Exception e){
                //currLocCTV.setText(Html.fromHtml(AddressLine + "<br/>(" + AddressLat + ", " + AddressLon + ")"));
                unIdnfdcurrLocCTV.setText(Html.fromHtml(dataObj.getString("CurrentLocation")));
                e.printStackTrace();
            }

            String truckTractorId = "", truckTractorVin = "", totalVehDistance = "", distanceToday = "",  currTotalDis = "", currTotalEng = "";

            JSONArray EngineHourArray = constants.checkNullArray(dataObj,ConstantsKeys.EngineHourMilesReportListUnidentified);
            if(EngineHourArray.length() > 0) {

                for (int i = 0; i < EngineHourArray.length(); i++) {
                    JSONObject engineObj = (JSONObject) EngineHourArray.get(i);
                    if(i == 0) {
                        truckTractorId  = "" + (i+1) + ") "+ engineObj.getString("TruckEquipmentNo");
                        truckTractorVin = "" + (i+1) + ") "+ engineObj.getString("CMVVIN");
                        totalVehDistance       = "" + (i+1) + ") "+ engineObj.getString("StartOdometr") + " - " + engineObj.getString("EndOdometer");
                        distanceToday   = "" + (i+1) + ") "+ engineObj.getString("DifferenceKM") ;
                    }else{
                        truckTractorId  = truckTractorId     + "\n" + (i+1) + ") "+ engineObj.getString("TruckEquipmentNo");
                        truckTractorVin = truckTractorVin   + "\n" + (i+1) + ") "+ engineObj.getString("CMVVIN");
                        totalVehDistance= totalVehDistance  + "\n" + (i+1) + ") "+ engineObj.getString("StartOdometr") + " - " + engineObj.getString("EndOdometer");
                        distanceToday   = distanceToday     + "\n" + (i+1) + ") "+ engineObj.getString("DifferenceKM") ;
                    }

                    currTotalDis = engineObj.getString("EndOdometer");
                    currTotalEng = engineObj.getString("EndEngineHours");

                    try {
                        if(currTotalEng.length() > 0 && !currTotalEng.equals("--")) {
                            currTotalEng = Constants.Convert2DecimalPlacesString(currTotalEng);
                        }
                    }catch (Exception e){
                        currTotalEng = engineObj.getString("EndEngineHours");
                    }
                }

            }else{
                truckTractorId  = dataObj.getString("TruckTractorID");
                truckTractorVin = dataObj.getString("TruckTractorVIN");
            }

            unIdnfdtruckTractorIdTV.setText(truckTractorId);
            unIdnfdtruckTractorVinTV.setText(truckTractorVin);
            unIdnfdtotalDisCTV.setText(totalVehDistance);
            unIdnfddistanceTodayCTV.setText(distanceToday);
            unIdnfdcurrTotalDisTV.setText(currTotalDis);
            unIdnfdcurrTotalEngTV.setText(currTotalEng);


        /*    String currentCycle = "";
            if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {

                if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS)) {
                    currentCycle = "USA Cycle \n" + DriverConst.GetDriverSettings(DriverConst.USACycleName, getActivity());
                }else{
                    currentCycle = "CAN Cycle \n" + DriverConst.GetDriverSettings(DriverConst.CANCycleName, getActivity());
                }
            }else{
                if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS)) {
                    currentCycle = "USA Cycle \n" + DriverConst.GetCoDriverSettings(DriverConst.CoUSACycleName, getActivity());
                }else{
                    currentCycle = "CAN Cycle \n" + DriverConst.GetCoDriverSettings(DriverConst.CoCANCycleName, getActivity());
                }
            }
            curreCycleCTV.setText(currentCycle);*/

            JSONObject DriverLogDetailModelObj = new JSONObject(dataObj.getString("DriverLogDetailModel"));
            JSONObject RulesApiModelNewCanadaObj = new JSONObject(DriverLogDetailModelObj.getString("RulesApiModelNewCanada"));

            int TotalHrsInShift = (int)RulesApiModelNewCanadaObj.getDouble("shiftUsedMinutes");
            int TotalHrsInCycle = (int)RulesApiModelNewCanadaObj.getDouble("cycleUsedMinutes");
            int RemainingHrsCycle = (int)RulesApiModelNewCanadaObj.getDouble("cycleRemainingMinutes");
            // int OffDutyTimeDefferal = (int)RulesApiModelNewCanadaObj.getDouble("");



            unIdnfdtotalHrsCTV.setText("");
            unIdnfdtotalhrsCycleCTV.setText("");
            unIdnfdremainingHourCTV.setText("");
            unIdnfdoffDutyDeffCTV.setText("");


            unIdnfddatDiagCTV.setText(constants.checkNullString(dataObj.getString("DataDiagnosticIndicatorsUn")));
            UnunIdenDriRecCTV.setText(dataObj.getString("UnIdentifiedDriverRecords"));

            unIdnfdmalfStatusCTV.setText(constants.checkNullString(dataObj.getString("ELDMalfunctionIndicatorsUn")));
            unIdnfdeldIdCTV.setText(dataObj.getString("ELDID"));
            unIdnfdeldProviderCTV.setText(dataObj.getString("ELDManufacturer"));
            unIdnfdeldCerCTV.setText(constants.checkNullString(dataObj.getString("ELDCertificationID")) );
            unIdnfdeldAuthCTV.setText(constants.checkNullString(dataObj.getString("ELDAuthenticationValue")) );

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

            String status = "", Message = "";
            JSONObject dataObj = null, obj = null;

            if(response != null && response.length() > 0) {
                try {

                    obj = new JSONObject(response);

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

                        if (flag == GetAddFromLatLng) {

                            if (!obj.isNull("Data")) {
                                try {
                                    JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                                    AddressLine = dataJObject.getString(ConstantsKeys.Location);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }


                        } else {

                            LogSignImage = "";
                            TotalOnDutyHours = "00:00";
                            TotalDrivingHours = "00:00";
                            TotalOffDutyHours = "00:00";
                            TotalSleeperBerthHours = "00:00";

                            TotalUnidenitifiedOffDutyHours = "00:00";
                            TotalUnidenitifiedDrivingHours = "00:00";
                            TotalUnidenitifiedSleeperHours = "00:00";
                            TotalUnidenitifiedOnDutyHours = "00:00";

                            VerifyDutyStatusList = new ArrayList();
                            VerifyEnginePowerList = new ArrayList();
                            VerifyLoginLogoutList  = new ArrayList();
                            VerifyCommentsRemarksList = new ArrayList<>();

                            DutyStatusList = new ArrayList();
                            LoginLogoutList = new ArrayList();
                            CommentsRemarksList = new ArrayList();
                            CycleOpZoneList = new ArrayList();
                            EnginePowerList = new ArrayList();


                            UnIdnfdDutyStatusList = new ArrayList();
                            UnIdnfdLoginLogoutList = new ArrayList();
                            UnIdnfdCommentsRemarksList = new ArrayList();
                            UnIdnfdEnginePowerList = new ArrayList();
                            JSONArray dotUnidentifdLogArray = new JSONArray();
                            JSONArray dotUnidentifdLogArray2 = new JSONArray();
                            JSONArray UnIdnfdEnginePowerArray = new JSONArray();


                            try {
                                TotalOffDutyHours = dataObj.getString("TotalOffDutyHours");
                                TotalSleeperBerthHours = dataObj.getString("TotalSleeperHours");
                                TotalDrivingHours = dataObj.getString("TotalDrivingHours");
                                TotalOnDutyHours = dataObj.getString("TotalOnDutyHours");

                                TotalUnidenitifiedOffDutyHours = dataObj.getString("TotalUnidenitifiedOffDutyHours");
                                TotalUnidenitifiedDrivingHours = dataObj.getString("TotalUnidenitifiedDrivingHours");
                                TotalUnidenitifiedSleeperHours = dataObj.getString("TotalUnidenitifiedSleeperHours");
                                TotalUnidenitifiedOnDutyHours = dataObj.getString("TotalUnidenitifiedOnDutyHours");

                                TotalOffDutyHours = TotalOffDutyHours.replaceAll("-", "");
                                TotalOnDutyHours = TotalOnDutyHours.replaceAll("-", "");
                                TotalDrivingHours = TotalDrivingHours.replaceAll("-", "");
                                TotalSleeperBerthHours = TotalSleeperBerthHours.replaceAll("-", "");

                                TotalUnidenitifiedOffDutyHours =  TotalUnidenitifiedOffDutyHours.replaceAll("-", "");
                                TotalUnidenitifiedDrivingHours =  TotalUnidenitifiedDrivingHours.replaceAll("-", "");
                                TotalUnidenitifiedSleeperHours =  TotalUnidenitifiedSleeperHours.replaceAll("-", "");
                                TotalUnidenitifiedOnDutyHours =  TotalUnidenitifiedOnDutyHours.replaceAll("-", "");


                                JSONArray dotLogArray = constants.checkNullArray(dataObj, ConstantsKeys.graphRecordList);

                                JSONArray unidentifiedgraphArray = constants.checkNullArray(dataObj, ConstantsKeys.oReportList);

                                for(int i  = 0 ;i< unidentifiedgraphArray.length(); i++){
                                    JSONObject object = (JSONObject) unidentifiedgraphArray.get(i);
                                    int EventType = object.getInt(ConstantsKeys.EventType);
                                    if(EventType == 9){
                                        dotUnidentifdLogArray.put(object);
                                        dotUnidentifdLogArray2.put(object);
                                    }

                                    if(EventType == 6 && object.getBoolean(ConstantsKeys.IsUnidentified)){
                                        UnIdnfdEnginePowerArray.put(object);
                                    }

                                    if(i == 0 && EventType != 9){
                                        object.put(ConstantsKeys.DateTimeWithMins, global.ConvertDateFormat(LogDate));
                                        object.put(ConstantsKeys.EndDateTime, global.ConvertDateFormat(LogDate));
                                        dotUnidentifdLogArray.put(object);
                                    }
                                }


                                ParseGraphData(dotLogArray);
                                String selectedDateStr = global.ConvertDateFormat(LogDate);
                               // dotUnidentifdLogArray = constants.setUnidentifiedGraphData(selectedDateStr, DaysDiff, dotUnidentifdLogArray);

                                ArrayList<UnidentifiedEventModel> UnidentifiedEventList = constants.getUnidentifiedEventList(selectedDateStr, DaysDiff, dotUnidentifdLogArray);
                                ParseUnidentifiedGraphData(UnidentifiedEventList);

                                JSONArray dutyStatusArray = constants.checkNullArray(dataObj, ConstantsKeys.dutyStatusChangesList);
                                JSONArray loginLogoutArray = constants.checkNullArray(dataObj, ConstantsKeys.loginAndLogoutList);
                                JSONArray ChangeInDriversCycleList = constants.checkNullArray(dataObj, ConstantsKeys.ChangeInDriversCycleList);
                                JSONArray commentsRemarksArray = constants.checkNullArray(dataObj, ConstantsKeys.commentsRemarksList);
                                JSONArray enginePowerArray = constants.checkNullArray(dataObj, ConstantsKeys.enginePowerUpAndShutDownList);


                                DutyStatusList = constants.parseCanadaDotInList(dutyStatusArray, true);
                                EnginePowerList = constants.parseCanadaDotInList(enginePowerArray, true);
                                VerifyLoginLogoutList = constants.parseCanadaDotInList(loginLogoutArray, true);
                                VerifyCommentsRemarksList = constants.parseCanadaDotInList(commentsRemarksArray, false);

                                UnIdnfdDutyStatusList = constants.parseCanadaDotInList(dotUnidentifdLogArray2, true);
                                UnIdnfdEnginePowerList = constants.parseCanadaDotInList(UnIdnfdEnginePowerArray, true);




                                for(int i = 0 ; i < VerifyLoginLogoutList.size() ; i++){
                                    if(VerifyLoginLogoutList.get(i).isUnidentified()){
                                        UnIdnfdLoginLogoutList.addAll(Collections.singletonList(VerifyLoginLogoutList.get(i)));
                                    }else{
                                        if (dataObj.has(ConstantsKeys.loginAndLogoutDates)) {
                                            JSONArray loginSortingArray = constants.checkNullArray(dataObj, ConstantsKeys.loginAndLogoutDates);
                                            LoginLogoutList = constants.parseCanadaLogoutLoginList(loginSortingArray);
                                        } else {
                                            LoginLogoutList.addAll(Collections.singletonList(VerifyLoginLogoutList.get(i)));
                                        }
                                    }
                                }

                                for(int i = 0 ; i < VerifyCommentsRemarksList.size() ; i++){
                                    if(VerifyCommentsRemarksList.get(i).isUnidentified()){
                                        UnIdnfdCommentsRemarksList.addAll(Collections.singleton((VerifyCommentsRemarksList.get(i))));
                                    }else{
                                        CommentsRemarksList.addAll(Collections.singleton((VerifyCommentsRemarksList.get(i))));
                                    }
                                }

                                CycleOpZoneList = constants.parseCanadaDotInList(ChangeInDriversCycleList, true);

                                setdataOnAdapter();
                                setDataOnTextView(dataObj);

                                setdataOnUnIdnfdAdapter();
                                setDataOnUnIdnfdTextView(dataObj);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        htmlAppendedText = "";
                        htmlUnidentifiedAppendedText = "";

                        String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
                        String CloseTagUnidetified = constants.HtmlCloseTag(TotalUnidenitifiedOffDutyHours, TotalSleeperBerthHours, TotalUnidenitifiedDrivingHours, TotalUnidenitifiedOnDutyHours);
                        ReloadWebView(CloseTag);
                        ReloadUnidentiWebView(CloseTagUnidetified);

                        global.EldScreenToast(scrollUpBtn, Message, getResources().getColor(R.color.colorVoilation));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            if(getActivity() != null && !getActivity().isFinishing()) {
                try {
                    Logger.LogDebug("error", ">>error: " + error);
                    canDotProgressBar.setVisibility(View.GONE);
                    global.EldScreenToast(scrollUpBtn, Globally.DisplayErrorMessage(error.toString()), getResources().getColor(R.color.colorVoilation));
                    htmlAppendedText = "";
                    htmlUnidentifiedAppendedText = "";
                    TotalOnDutyHours = "00:00";
                    TotalDrivingHours = "00:00";
                    TotalOffDutyHours = "00:00";
                    TotalSleeperBerthHours = "00:00";

                    TotalUnidenitifiedOffDutyHours = "00:00";
                    TotalUnidenitifiedDrivingHours = "00:00";
                    TotalUnidenitifiedSleeperHours = "00:00";
                    TotalUnidenitifiedOnDutyHours = "00:00";


                    String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
                    String CloseTagUnidetified = constants.HtmlCloseTag(TotalUnidenitifiedOffDutyHours, TotalSleeperBerthHours, TotalUnidenitifiedDrivingHours, TotalUnidenitifiedOnDutyHours);
                    ReloadWebView(CloseTag);
                    ReloadUnidentiWebView(CloseTagUnidetified);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };


    private Thread.UncaughtExceptionHandler onRuntimeError= new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            //Try starting the Activity again
            Logger.LogDebug("uncaughtException", "uncaughtException: " +ex.toString());
            SharedPref.SetDOTStatus( false, getActivity());
            getActivity().finish();
            System.exit(2);
        }
    };


}
