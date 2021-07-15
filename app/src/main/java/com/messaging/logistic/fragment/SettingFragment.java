package com.messaging.logistic.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.AsyncResponse;
import com.constants.CheckConnectivity;
import com.constants.CircularProgressBar;
import com.constants.Constants;
import com.constants.DownloadAppService;
import com.constants.LoadingSpinImgView;
import com.constants.SharedPref;
import com.constants.SyncDataUpload;
import com.constants.VolleyRequest;
import com.custom.dialogs.AdverseRemarksDialog;
import com.custom.dialogs.ChangeCycleDialog;
import com.custom.dialogs.ConfirmationDialog;
import com.custom.dialogs.DeferralDialog;
import com.custom.dialogs.ObdDataInfoDialog;
import com.local.db.DeferralMethod;
import com.models.CycleModel;
import com.driver.details.DriverConst;
import com.driver.details.ParseLoginDetails;
import com.models.TimeZoneModel;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.SyncingMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.shared.pref.CaCyclePrefManager;
import com.shared.pref.CoCAPref;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.CoTimeZonePref;
import com.shared.pref.CoUSPref;
import com.shared.pref.EldCoDriverLogPref;
import com.shared.pref.EldSingleDriverLogPref;
import com.shared.pref.MainDriverEldPref;
import com.shared.pref.TimeZonePrefManager;
import com.shared.pref.USCyclePrefManager;
import com.wifi.settings.WiFiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;
import webapi.LocalCalls;


public class SettingFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{


    View rootView;
    TextView actionBarTitle, caCycleTV, usCycleTV, timeZoneTV, dateActionBarTV, checkAppUpdateTV, haulExpTxtView, haulExcptnTxtVw;
    TextView caCurrentCycleTV, usCurrentCycleTV, operatingZoneTV;
    Spinner caCycleSpinner, usCycleSpinner, timeZoneSpinner;
    Button SettingSaveBtn;
    ImageView updateAppDownloadIV, downloadHintImgView, opZoneTmgView, canEditImgView, usEditImgView;
    LoadingSpinImgView settingSpinImgVw;
    RelativeLayout rightMenuBtn, eldMenuLay, checkAppUpdateBtn, haulExceptionLay, SyncDataBtn, checkInternetBtn,
            obdDiagnoseBtn, docBtn, deferralRuleLay;
    LinearLayout settingsMainLay;
    SwitchCompat deferralSwitchButton, haulExceptnSwitchButton, adverseSwitchButton;
    List<CycleModel> CanCycleList;
    List<CycleModel> UsaCycleList;
    List<TimeZoneModel> TimeZoneList;
    ScrollView settingsScrollView;

    CheckConnectivity checkConnectivity;
    ParseLoginDetails SaveSettingDetails;
    CaCyclePrefManager caPrefManager;
    USCyclePrefManager usPrefmanager;
    TimeZonePrefManager timeZonePrefManager;
    SyncingMethod syncingMethod;
    DBHelper dbHelper;
    HelperMethods hMethods;
    DeferralMethod deferralMethod;

    Constants constant;
    Map<String, String> params;

    CoCAPref coCAPrefManager;
    CoUSPref coUSPrefmanager;
    CoTimeZonePref coTimePrefManager;
    ArrayAdapter<String> CanCycleAdapter;

    int SyncData = 1, CheckInternetConnection = 2, CheckUpdate = 3;
    int ExistingVersionCodeInt  = 0,  VersionCodeInt = 0, AppInstallAttemp = 0;
    int CanListSize = 0, UsaListSize = 0, TimeZoneListSize = 0, SavedPosition = 0;
    String SavedCanCycle = "", SavedUsaCycle = "", CurrentCycleId = "", SavedTimeZone = "", DeviceId = "", DriverId = "", DriverName = "", CompanyId = "";
    String SelectedCanCycle = "", SelectedUsaCycle = "", SelectedTimeZone = "", exceptionDesc = "", TruckNumber, DriverTimeZone,
            IsSouthCanada, SavedCycleType, changedCycleId, changedCycleName, LocationType = "";
    String Approved = "2";
    String Rejected = "3";

    ProgressDialog progressDialog;
    ConfirmationDialog confirmationDialog;
    AdverseRemarksDialog adverseRemarksDialog;
    DeferralDialog deferralDialog;

    CheckConnectivity connectivityTask;
    File syncingFile = new File("");
    File coDriverSyncFile = new File("");
    File DriverLogFile = new File("");
    File cycleUpdationRecordFile = new File("");

    private String url = "", ApkFilePath = "", existingApkFilePath = "";
    String VersionCode = "", VersionName = "", ExistingApkVersionCode = "", ExistingApkVersionName = "";
    CircularProgressBar downloadProgressBar;
    VolleyRequest GetAppUpdateRequest, GetDriverLogPostPermission, getCycleChangeApproval, ChangeCycleRequest, OperatingZoneRequest ;
    final int GetAppUpdate  = 1, DriverLogPermission = 2, CycleChangeApproval = 3, ChangeCycle = 4, OperatingZone = 5;
    int DriverType = 0;
    int leftOffOrSleeperMin = 0;
    long progressPercentage = 0;
    boolean isNorthCanada = false;
    boolean IsLogPermission = false, IsDownloading = false, IsManualAppDownload = false;
    DriverPermissionMethod driverPermissionMethod;
    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;
    EldSingleDriverLogPref eldSharedPref;
    EldCoDriverLogPref coEldSharedPref;
    // SyncDataUpload asyncTaskUpload;
    DownloadAppService downloadAppService = new DownloadAppService();
    Globally global;
    Constants constants;
    WiFiConfig wifiConfig;
    Animation fadeViewAnim;

    ChangeCycleDialog changeCycleDialog;
    AlertDialog enableExceptionAlert;
    private Vector<AlertDialog> vectorDialogs = new Vector<AlertDialog>();

    LocalCalls localCalls;

    boolean isHaulExcptn = false;
    boolean isAdverseExcptn = false;
    boolean isDeferral = false;
    boolean isCoDriverSync = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.settings_fragment, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }

        // FirebaseApp.initializeApp(getActivity());
        initView(rootView);

        return rootView;
    }


    void initView(View v) {

        localCalls                  = new LocalCalls();
        constants                   = new Constants();
        global                      = new Globally();
        GetAppUpdateRequest         = new VolleyRequest(getActivity());
        GetDriverLogPostPermission  = new VolleyRequest(getActivity());
        getCycleChangeApproval      = new VolleyRequest(getActivity());
        ChangeCycleRequest          = new VolleyRequest(getActivity());
        OperatingZoneRequest        = new VolleyRequest(getActivity());
        connectivityTask            = new CheckConnectivity(getActivity());
        driverPermissionMethod      = new DriverPermissionMethod();
        SaveSettingDetails          = new ParseLoginDetails();
        caPrefManager               = new CaCyclePrefManager();
        usPrefmanager               = new USCyclePrefManager();
        timeZonePrefManager         = new TimeZonePrefManager();
        dbHelper                    = new DBHelper(getActivity());
        hMethods                    = new HelperMethods();
        syncingMethod               = new SyncingMethod();
        deferralMethod              = new DeferralMethod();
        constant                    = new Constants();

        checkConnectivity           = new CheckConnectivity(getActivity());
        coCAPrefManager             = new CoCAPref();
        coUSPrefmanager             = new CoUSPref();
        coTimePrefManager           = new CoTimeZonePref();
        wifiConfig                  = new WiFiConfig();
        MainDriverPref              = new MainDriverEldPref();
        CoDriverPref                = new CoDriverEldPref();
        eldSharedPref               = new EldSingleDriverLogPref();
        coEldSharedPref             = new EldCoDriverLogPref();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");

        settingsScrollView   = (ScrollView)v.findViewById(R.id.settingsScrollView);
        actionBarTitle       = (TextView)v.findViewById(R.id.EldTitleTV);
        caCycleTV            = (TextView)v.findViewById(R.id.caCycleTV);
        usCycleTV            = (TextView)v.findViewById(R.id.usCycleTV);
        timeZoneTV           = (TextView)v.findViewById(R.id.timeZoneTV);
        dateActionBarTV      = (TextView)v.findViewById(R.id.dateActionBarTV);
        checkAppUpdateTV     = (TextView)v.findViewById(R.id.checkAppUpdateTV);
        haulExcptnTxtVw      = (TextView)v.findViewById(R.id.haulExcptnTxtVw);
        haulExpTxtView       = (TextView)v.findViewById(R.id.haulExpTxtView);
        operatingZoneTV      = (TextView)v.findViewById(R.id.operatingZoneTV);
        caCurrentCycleTV     = (TextView)v.findViewById(R.id.caCurrentCycleTV);
        usCurrentCycleTV     = (TextView)v.findViewById(R.id.usCurrentCycleTV);

        caCycleSpinner       = (Spinner)v.findViewById(R.id.caCycleSpinner);
        usCycleSpinner       = (Spinner)v.findViewById(R.id.usCycleSpinner);
        timeZoneSpinner      = (Spinner)v.findViewById(R.id.timeZoneSpinner);

        SettingSaveBtn       = (Button) v.findViewById(R.id.settingSaveBtn);
        updateAppDownloadIV  = (ImageView)v.findViewById(R.id.updateAppDownloadIV);
        downloadHintImgView  = (ImageView)v.findViewById(R.id.downloadHintImgView);
        opZoneTmgView        = (ImageView)v.findViewById(R.id.opZoneTmgView);
        canEditImgView       = (ImageView)v.findViewById(R.id.canEditImgView);
        usEditImgView        = (ImageView)v.findViewById(R.id.usEditImgView);
        settingSpinImgVw     = (LoadingSpinImgView)v.findViewById(R.id.settingSpinImgVw);

        haulExceptionLay     = (RelativeLayout) v.findViewById(R.id.haulExceptionLay);
        checkAppUpdateBtn    = (RelativeLayout) v.findViewById(R.id.checkAppUpdateBtn);
        eldMenuLay           = (RelativeLayout) v.findViewById(R.id.eldMenuLay);
        SyncDataBtn          = (RelativeLayout) v.findViewById(R.id.SyncDataBtn);
        checkInternetBtn     = (RelativeLayout) v.findViewById(R.id.checkInternetBtn);
        obdDiagnoseBtn       = (RelativeLayout) v.findViewById(R.id.obdDiagnoseBtn);
        docBtn               = (RelativeLayout) v.findViewById(R.id.docBtn);
        deferralRuleLay      = (RelativeLayout) v.findViewById(R.id.deferralRuleLay);

        rightMenuBtn         = (RelativeLayout) v.findViewById(R.id.rightMenuBtn);
        settingsMainLay      = (LinearLayout)v.findViewById(R.id.settingsMainLay);

        downloadProgressBar  = (CircularProgressBar) v.findViewById(R.id.downloadProgressBar);
        deferralSwitchButton   = (SwitchCompat)v.findViewById(R.id.deferralSwitchButton);
        haulExceptnSwitchButton = (SwitchCompat)v.findViewById(R.id.haulExceptnSwitchButton);
        adverseSwitchButton = (SwitchCompat)v.findViewById(R.id.adverseSwitchButton);

        rightMenuBtn.setVisibility(View.GONE);
        settingSpinImgVw.setImageResource(R.drawable.sync_settings);
        dateActionBarTV.setVisibility(View.VISIBLE);
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.whiteee));

        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            settingsMainLay.setBackgroundColor(getResources().getColor(R.color.gray_background) );
        }

        fadeViewAnim    = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        fadeViewAnim.setDuration(1500);

        DeviceId        = SharedPref.GetSavedSystemToken(getActivity());
        DriverId        = SharedPref.getDriverId( getActivity());
        CompanyId       = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());



        if (global.isConnected(getActivity())) {
            getCycleChangeApproval(DriverId, DeviceId, CompanyId);
        }


        deferralSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {
                    if (isChecked) {
                        List<DriverLog> oDriverLogDetail = hMethods.getSavedLogList(Integer.valueOf(DriverId),
                                global.GetCurrentJodaDateTime(),
                                global.GetCurrentUTCDateTime(), dbHelper);

                        DriverDetail oDriverDetail = hMethods.getDriverList(global.GetCurrentJodaDateTime(),
                                global.GetCurrentUTCDateTime(), Integer.valueOf(DriverId),
                                (int) global.GetTimeZoneOffSet(), Integer.valueOf(CurrentCycleId),
                                global.isSingleDriver(getActivity()),
                                Integer.valueOf(SharedPref.getDriverStatusId(getActivity())), false,
                                isHaulExcptn, isAdverseExcptn, isNorthCanada,
                                SharedPref.GetRulesVersion(getActivity()), oDriverLogDetail);

                        RulesResponseObject deferralObj = localCalls.IsEligibleDeferralRule(oDriverDetail);

                        if (deferralObj.isDeferralEligible()) {

                            leftOffOrSleeperMin = (int) deferralObj.getLeftOffOrSleeperMinutes();

                            try {
                                if (deferralDialog != null && deferralDialog.isShowing())
                                    deferralDialog.dismiss();

                                deferralDialog = new DeferralDialog(getActivity(), leftOffOrSleeperMin, new DeferralListener());
                                deferralDialog.show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            buttonView.setChecked(false);
                            global.EldScreenToast(SyncDataBtn, "You are not eligible for Deferral rule.", getResources().getColor(R.color.colorVoilation));
                        }
                    }else{
                        buttonView.setChecked(true);
                        global.EldScreenToast(SyncDataBtn, "Need Deferral rule disabled desc.", getResources().getColor(R.color.colorPrimary));

                       /* if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                            SharedPref.setDeferralForMain(false, getActivity());
                        }else{
                            SharedPref.setDeferralForCo(false, getActivity());
                        }


                        hMethods.SaveDriversJob(DriverId, DeviceId, "", getString(R.string.enable_deferral_rule),
                                LocationType, "", false, isNorthCanada, DriverType, constants,
                                MainDriverPref, CoDriverPref, eldSharedPref, coEldSharedPref,
                                syncingMethod, global, hMethods, dbHelper, getActivity() ) ;

*/
                    }
                }
            }
        });


        haulExceptnSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {

                    if (isChecked) {
                        getExceptionStatus();

                        if(!isAdverseExcptn) {
                            if(isAllowToEnableException(DriverId)) {
                                haulExceptionAlert();
                            }else{
                                buttonView.setChecked(false);
                                global.EldScreenToast(SyncDataBtn, exceptionDesc, getResources().getColor(R.color.colorSleeper));
                            }

                        }else{
                            buttonView.setChecked(false);
                            global.EldScreenToast(SyncDataBtn, getString(R.string.already_enable_excp), getResources().getColor(R.color.colorSleeper));
                        }
                    } else {
                        global.EldScreenToast(SyncDataBtn, getString(R.string.haul_excp_reset_auto), getResources().getColor(R.color.colorSleeper));
                        buttonView.setChecked(true);
                    }
                }
            }
        });

        adverseSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {
                    if (isChecked) {

                        getExceptionStatus();
                        if(!isHaulExcptn) {

                            if(isAllowToEnableException(DriverId)) {
                                try {
                                    if (adverseRemarksDialog != null && adverseRemarksDialog.isShowing())
                                        adverseRemarksDialog.dismiss();

                                    adverseRemarksDialog = new AdverseRemarksDialog(getActivity(), true,
                                            false, false, new RemarksListener());
                                    adverseRemarksDialog.show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                buttonView.setChecked(false);
                                global.EldScreenToast(SyncDataBtn, exceptionDesc, getResources().getColor(R.color.colorSleeper));
                            }

                        }else{
                            buttonView.setChecked(false);
                            global.EldScreenToast(SyncDataBtn, getString(R.string.already_enable_excp), getResources().getColor(R.color.colorSleeper));
                        }
                    } else {
                        global.EldScreenToast(SyncDataBtn, getString(R.string.excp_reset_auto), getResources().getColor(R.color.colorSleeper));
                        buttonView.setChecked(true);
                    }
                }
            }
        });

        /*


         */
/*

        // temperory hide for testing
        haulExceptnSwitchButton.setVisibility(View.GONE);
        haulExpTxtView.setText("Wired OBD");
        haulExceptionLay.setVisibility(View.VISIBLE);
        haulExcptnTxtVw.setVisibility(View.VISIBLE);

*/

        fadeViewAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(IsDownloading) {
                    checkAppUpdateTV.startAnimation(fadeViewAnim);
                    downloadHintImgView.startAnimation(fadeViewAnim);
                }else {
                    fadeViewAnim.cancel();
                    downloadHintImgView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });



        eldMenuLay.setOnClickListener(this);
        SettingSaveBtn.setOnClickListener(this);
        SyncDataBtn.setOnClickListener(this);
        checkInternetBtn.setOnClickListener(this);
        checkAppUpdateBtn.setOnClickListener(this);
        dateActionBarTV.setOnClickListener(this);
        obdDiagnoseBtn.setOnClickListener(this);
        docBtn.setOnClickListener(this);
        haulExceptionLay.setOnClickListener(this);
        operatingZoneTV.setOnClickListener(this);
        caCycleTV.setOnClickListener(this);
        usCycleTV.setOnClickListener(this);

        caCycleSpinner.setOnItemSelectedListener(this);
        usCycleSpinner.setOnItemSelectedListener(this);
        timeZoneSpinner.setOnItemSelectedListener(this);

        SettingSaveBtn.setVisibility(View.GONE);
        caCycleSpinner.setVisibility(View.GONE);
        usCycleSpinner.setVisibility(View.GONE);
        timeZoneSpinner.setVisibility(View.GONE);

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }

    @Override
    public void onResume() {
        super.onResume();


        if(SharedPref.IsAOBRD(getActivity())){
            dateActionBarTV.setText(Html.fromHtml("<b><u>AOBRD</u></b>"));
        }else{
            dateActionBarTV.setText(Html.fromHtml("<b><u>ELD</u></b>"));
        }

        dateActionBarTV.setText("Obd Info");

        global.hideSoftKeyboard(getActivity());
        getInstalledAppDetail();
        existingApkFilePath = getExistingApkPath();
        DriverId            = SharedPref.getDriverId( getActivity());
        DriverName          =  DriverConst.GetDriverDetails( DriverConst.DriverName, getActivity());

        actionBarTitle.setText(getResources().getString(R.string.action_settings));
        getSavedCycleData();

        getExceptionStatus();
        haulExceptnSwitchButton.setChecked(isHaulExcptn);
        adverseSwitchButton.setChecked(isAdverseExcptn);
        deferralSwitchButton.setChecked(isDeferral);

        if(constants.isLocMalfunctionEvent(getActivity(), DriverType) && SharedPref.getLocMalfunctionType( getContext()).equals("x")){
            // SharedPref.setLocMalfunctionType("m", getContext());
            LocationType = "m";
        }

        try{
            JSONObject logPermissionObj    = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DriverId), dbHelper);


            if(logPermissionObj != null) {
                try {
                    IsManualAppDownload = logPermissionObj.getBoolean(ConstantsKeys.IsManualAppDownload);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        DisplayCanCycles();
        DisplayUsaCycles();
        DisplayTimeZones();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver( progressReceiver, new IntentFilter("download_progress"));

        if (global.isConnected(getActivity())) {
            GetDriverLogPermission(DriverId);

            if(TabAct.isUpdateDirectly){
                TabAct.isUpdateDirectly = false;

                settingsScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        settingsScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });

                checkAppUpdateBtn.performClick();

            }
        }


    }


    void getExceptionStatus(){
        if(DriverType == Constants.MAIN_DRIVER_TYPE) {
            isHaulExcptn    = SharedPref.get16hrHaulExcptn(getActivity());
            isAdverseExcptn = SharedPref.getAdverseExcptn(getActivity());
            isDeferral      = SharedPref.getDeferralForMain(getActivity());
        }else{
            isHaulExcptn    = SharedPref.get16hrHaulExcptnCo(getActivity());
            isAdverseExcptn = SharedPref.getAdverseExcptnCo(getActivity());
            isDeferral      = SharedPref.getDeferralForCo(getActivity());
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        TabAct.isUpdateDirectly = false;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(progressReceiver);
    }



    private void changeCycleZoneDialog(String type, String currentCycle, String currentOpZone){
        try {
            if (changeCycleDialog != null && changeCycleDialog.isShowing())
                changeCycleDialog.dismiss();

            changeCycleDialog = new ChangeCycleDialog(getActivity(), type, currentCycle, currentOpZone, new ChangeCycleListener());
            changeCycleDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ChangeCycleListener implements ChangeCycleDialog.ChangeCycleListener{

        @Override
        public void ChangeCycleBtn(String type) {
            // operating_zone  us_cycle   can_cycle
            SavedCycleType = type;
            if(type.equals("operating_zone")){
                if(SharedPref.IsNorthCanada(getActivity())) {
                    IsSouthCanada = "true";
                   // SharedPref.SaveOperatingZone(getString(R.string.OperatingZoneNorth), getActivity());
                }else{
                    IsSouthCanada = "false";
                  //  SharedPref.SaveOperatingZone(getString(R.string.OperatingZoneSouth), getActivity());
                }

                if (global.isConnected(getActivity())) {
                    ChangeOperatingZone(DriverId, DeviceId, CompanyId, IsSouthCanada,
                            CurrentCycleId, DriverTimeZone, Globally.LATITUDE, Globally.LONGITUDE, TruckNumber);
                }else{
                    global.EldScreenToast(SyncDataBtn, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation) );
                }
                // temp data
              //  getSavedCycleData();
            }else{

                if(type.equals("can_cycle")){
                    if(SavedCanCycle.equals(global.CANADA_CYCLE_1)){
                        changedCycleId      = global.CANADA_CYCLE_2;
                        changedCycleName    = global.CANADA_CYCLE_2_NAME;
                    }else{
                        changedCycleId      = global.CANADA_CYCLE_1;
                        changedCycleName    = global.CANADA_CYCLE_1_NAME;
                    }
                }else{
                    if(SavedUsaCycle.equals(global.USA_WORKING_6_DAYS)){
                        changedCycleId      = global.USA_WORKING_7_DAYS;
                        changedCycleName    = global.USA_WORKING_7_DAYS_NAME;
                    }else{
                        changedCycleId      = global.USA_WORKING_6_DAYS;
                        changedCycleName    = global.USA_WORKING_6_DAYS_NAME;
                    }
                }

                if (global.isConnected(getActivity())) {
                    changeCycleRequest(DriverId, DeviceId, CompanyId, "",
                            Approved, changedCycleId, CurrentCycleId, Globally.LATITUDE,
                            Globally.LONGITUDE, DriverTimeZone, TruckNumber, global.GetCurrentDeviceDateDefault());
                }else{
                    global.EldScreenToast(SyncDataBtn, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation) );
                }

                // temp data
             /*   saveUpdatedCycleData();
                getSavedCycleData();

                DisplayCanCycles();
                DisplayUsaCycles();
                DisplayTimeZones();
*/
            }

        }
    }


    /* ======= Display Canada Cycle ======== */
    private void DisplayCanCycles(){
        CanCycleList = new ArrayList<CycleModel>();
        ArrayList<String> CycleArray = new ArrayList<String>();
        CanListSize = 0;
        int SavedCyclePos = 0;

        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ){

            try {
                CanListSize = caPrefManager.GetCycles(getActivity()).size();
            } catch (Exception e) {
                CanListSize = 0;
            }
            try {
                if (CanListSize > 0) {
                    CanCycleList = caPrefManager.GetCycles(getActivity());
                    for (int i = 0; i < CanCycleList.size(); i++) {
                        CycleArray.add(CanCycleList.get(i).getCycleName());

                        if(CanCycleList.get(i).getCycleId().equals(SavedCanCycle))
                            SavedCyclePos = i;
                    }

                    SetSpinnerAdapter(caCycleSpinner, SavedCyclePos, CycleArray, caCycleTV);

                }
            } catch (Exception e) {
                e.printStackTrace();
                CanListSize = 0;
            }
        }else{
            try {
                CanListSize = coCAPrefManager.GetCycles(getActivity()).size();
            } catch (Exception e) {
                CanListSize = 0;
            }
            try {
                if (CanListSize > 0) {
                    CanCycleList = coCAPrefManager.GetCycles(getActivity());
                    for (int i = 0; i < CanCycleList.size(); i++) {
                        CycleArray.add(CanCycleList.get(i).getCycleName());

                        if(CanCycleList.get(i).getCycleId().equals(SavedCanCycle))
                            SavedCyclePos = i;
                    }

                    SetSpinnerAdapter(caCycleSpinner, SavedCyclePos, CycleArray, caCycleTV);

                }
            } catch (Exception e) {
                e.printStackTrace();
                CanListSize = 0;
            }
        }

    }

    /* ======= Display USA Cycle ======== */
    private void DisplayUsaCycles(){
        UsaCycleList = new ArrayList<CycleModel>();
        ArrayList<String> CycleArray = new ArrayList<String>();
        UsaListSize = 0;
        int SavedCyclePos = 0;

        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ){
            try {
                UsaListSize = usPrefmanager.GetCycles(getActivity()).size();
            } catch (Exception e) {
                UsaListSize = 0;
            }
            try {
                if (UsaListSize > 0) {
                    UsaCycleList = usPrefmanager.GetCycles(getActivity());
                    for (int i = 0; i < UsaCycleList.size(); i++) {
                        CycleArray.add(UsaCycleList.get(i).getCycleName());

                        if(UsaCycleList.get(i).getCycleId().equals(SavedUsaCycle))
                            SavedCyclePos = i;

                    }
                    SetSpinnerAdapter(usCycleSpinner, SavedCyclePos, CycleArray, usCycleTV);
                }
            } catch (Exception e) {
                e.printStackTrace();
                UsaListSize = 0;
            }
        }else{
            try {
                UsaListSize = coUSPrefmanager.GetCycles(getActivity()).size();
            } catch (Exception e) {
                UsaListSize = 0;
            }
            try {
                if (UsaListSize > 0) {
                    UsaCycleList = coUSPrefmanager.GetCycles(getActivity());
                    for (int i = 0; i < UsaCycleList.size(); i++) {
                        CycleArray.add(UsaCycleList.get(i).getCycleName());

                        if(UsaCycleList.get(i).getCycleId().equals(SavedUsaCycle))
                            SavedCyclePos = i;
                    }
                    SetSpinnerAdapter(usCycleSpinner, SavedCyclePos, CycleArray, usCycleTV);
                }
            } catch (Exception e) {
                e.printStackTrace();
                UsaListSize = 0;
            }
        }
    }

    /* ======= Display Time Zones ======== */
    private void DisplayTimeZones(){
        TimeZoneList = new ArrayList<TimeZoneModel>();
        ArrayList<String> CycleArray = new ArrayList<String>();
        TimeZoneListSize = 0;
        int SavedTimeZonePos = 0;

        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ){
            try {
                TimeZoneListSize = timeZonePrefManager.GetTimeZone(getActivity()).size();
            } catch (Exception e) {
                TimeZoneListSize = 0;
            }
            try {
                if (TimeZoneListSize > 0) {
                    TimeZoneList = timeZonePrefManager.GetTimeZone(getActivity());
                    for (int i = 0; i < TimeZoneList.size(); i++) {
                        CycleArray.add(TimeZoneList.get(i).getTimeZoneName());

                        if(TimeZoneList.get(i).getTimeZoneID().equals(SavedTimeZone))
                            SavedTimeZonePos = i;

                    }
                    SetSpinnerAdapter(timeZoneSpinner, SavedTimeZonePos, CycleArray, timeZoneTV);
                }
            } catch (Exception e) {
                e.printStackTrace();
                TimeZoneListSize = 0;
            }
        }else{
            try {
                TimeZoneListSize = coTimePrefManager.GetTimeZone(getActivity()).size();
            } catch (Exception e) {
                TimeZoneListSize = 0;
            }
            try {
                if (TimeZoneListSize > 0) {
                    TimeZoneList = coTimePrefManager.GetTimeZone(getActivity());
                    for (int i = 0; i < TimeZoneList.size(); i++) {
                        CycleArray.add(TimeZoneList.get(i).getTimeZoneName());

                        if(TimeZoneList.get(i).getTimeZoneID().equals(SavedTimeZone))
                            SavedTimeZonePos = i;
                    }
                    SetSpinnerAdapter(timeZoneSpinner, SavedTimeZonePos, CycleArray, timeZoneTV);
                }
            } catch (Exception e) {
                e.printStackTrace();
                TimeZoneListSize = 0;
            }
        }
    }



    private void SetSpinnerAdapter(Spinner spinner, int indexPosition, ArrayList<String> spinnerArray, TextView view){
        CanCycleAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        CanCycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(CanCycleAdapter);
        spinner.setSelection(indexPosition);
        if (spinnerArray.get(indexPosition).equals(Globally.CANADA_CYCLE_1_NAME) && SharedPref.IsNorthCanada(getActivity())) {
            view.setText( "Cycle 1 (80/7) (N)");
        }else{
            if(spinnerArray.get(indexPosition).contains("C")) {
                if (spinnerArray.get(indexPosition).equals(Globally.CANADA_CYCLE_1_NAME)){
                    view.setText( "Cycle 1 (70/7)");
                }else{
                    view.setText( "Cycle 2 (120/14)");
                }
               // view.setText(Globally.CANADA_SOUTH_OPERATION_NAME + spinnerArray.get(indexPosition) + ")");
            }else {
                view.setText(spinnerArray.get(indexPosition));
            }
        }
    }




    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {


            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                SyncData();

                return true;
            } else {
                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(((Activity)getContext()), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            SyncData();
            return true;
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {

            case 1:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

                    SyncData();
                }
                break;

        }

    }


    private void SyncData(){

        JSONArray savedSyncedArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DriverId), dbHelper);

        if(savedSyncedArray.length() > 0) {
            syncingFile = global.SaveFileInSDCard("Sync_", savedSyncedArray.toString(), false, getActivity());
        }

        if(savedSyncedArray.length() > 0 || IsLogPermission) {
            // progressDialog.show();
            settingSpinImgVw.startAnimation();
            connectivityTask.ConnectivityRequest(SyncData, ConnectivityInterface);
        }else{
            ClearDriverUnSavedlog();
            global.EldScreenToast(SyncDataBtn, "No data available for syncing.", getResources().getColor(R.color.colorSleeper));
        }

    }


    private void SyncCoDriverData(String CoDriverId){

        JSONArray syncArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(CoDriverId), dbHelper);
        if(syncArray.length() > 0) {
            coDriverSyncFile = global.SaveFileInSDCard("Sync_", syncArray.toString(), false, getActivity());

            // Sync driver log API data to server with SAVE_LOG_TEXT_FILE (SAVE sync data service)
            SyncDataUpload syncDataUpload = new SyncDataUpload(getActivity(), CoDriverId, coDriverSyncFile,
                    null, null, false, asyncResponse );
            syncDataUpload.execute();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;

            case R.id.dateActionBarTV:
               // TabAct.host.setCurrentTab(0);

                ObdDataInfoDialog dialog = new ObdDataInfoDialog(getActivity());
                dialog.show();

                break;



            case R.id.SyncDataBtn:

                // isStoragePermissionGranted();
                SyncData();

                break;

            case R.id.checkInternetBtn:
                progressDialog.show();
                //CheckConnectivity CheckConnectivity = new CheckConnectivity(getActivity());
                checkConnectivity.ConnectivityRequest(CheckInternetConnection, ConnectivityInterface);

                break;


            case R.id.checkAppUpdateBtn:

                if(IsDownloading){

                    if (confirmationDialog != null && confirmationDialog.isShowing())
                        confirmationDialog.dismiss();
                    confirmationDialog = new ConfirmationDialog(getActivity(), Constants.AlertSettings, new ConfirmListener());
                    confirmationDialog.show();

                }else {

                    getInstalledAppDetail();
                    File existingFile = new File(global.getAlsApkPath(getActivity()) + "/" + getExistingApkPath());
                    if (!existingFile.isFile()) {
                        checkAppUpdateTV.setText(getResources().getString(R.string.Update_Status));
                    }

                    if (ExistingApkVersionCode.equals(VersionCode) && ExistingApkVersionName.equals(VersionName)) {
                        global.EldScreenToast(SyncDataBtn, "Your application is up to date", getResources().getColor(R.color.colorPrimary));
                    } else {
                        String updateTvText = checkAppUpdateTV.getText().toString();
                        if (updateTvText.equals(getResources().getString(R.string.install_updates))) {
                            if (ApkFilePath.length() > 0) {
                                InstallApp(ApkFilePath);
                            } else {
                                checkAppUpdateTV.setText(getResources().getString(R.string.Update_Status));
                                global.EldScreenToast(SyncDataBtn, "File not found", getResources().getColor(R.color.colorVoilation));
                            }
                        } else {
                            connectivityTask.ConnectivityRequest(CheckUpdate, ConnectivityInterface);
                        }
                    }
                }



                break;



            case R.id.obdDiagnoseBtn:

                ObdDiagnoseFragment obdDiagnoseFragment = new ObdDiagnoseFragment();
                MoveFragment(obdDiagnoseFragment);

             /*   if(wifiConfig.IsAlsNetworkConnected(getActivity()) ) {
                    ObdDiagnoseFragment obdDiagnoseFragment = new ObdDiagnoseFragment();
                    MoveFragment(obdDiagnoseFragment);
                }else{
                    global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.obd_connection_desc), getResources().getColor(R.color.colorVoilation));
                }*/

                break;

            case R.id.haulExceptionLay:
                //  ObdConfigFragment wiredObdFragment = new ObdConfigFragment();
                //  MoveFragment(wiredObdFragment);
                break;

            case R.id.docBtn:
                DocumentFragment helpFragment = new DocumentFragment();
                MoveFragment(helpFragment);
                break;


            case R.id.caCycleTV:

                if (CurrentCycleId.equals(global.CANADA_CYCLE_1) || CurrentCycleId.equals(global.CANADA_CYCLE_2)) {

                    if(constants.isActionAllowed(getActivity())) {
                        if(isSleepOffDuty()) {
                            changeCycleZoneDialog("can_cycle", SavedCanCycle, "");
                        }else{
                            global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.cycle_change_check), getResources().getColor(R.color.colorPrimary));
                        }
                    }else{
                        Globally.EldScreenToast(caCycleTV, getString(R.string.stop_vehicle_alert),
                                getResources().getColor(R.color.colorVoilation));
                    }

                }



                break;


            case R.id.usCycleTV:

                    if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS)) {

                        if(constants.isActionAllowed(getActivity())) {
                            if(isSleepOffDuty()) {
                                changeCycleZoneDialog("us_cycle", SavedUsaCycle, "");
                            }else{
                                global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.cycle_change_check), getResources().getColor(R.color.colorPrimary));
                            }
                        }else{
                            Globally.EldScreenToast(caCycleTV, getString(R.string.stop_vehicle_alert),
                                    getResources().getColor(R.color.colorVoilation));
                        }

                    }


                break;


            case R.id.operatingZoneTV:

                    if (CurrentCycleId.equals(global.CANADA_CYCLE_1) || CurrentCycleId.equals(global.CANADA_CYCLE_2)) {
                        if(constants.isActionAllowed(getActivity())) {
                            if(isSleepOffDuty() == false) {
                                changeCycleZoneDialog("operating_zone", CurrentCycleId, operatingZoneTV.getText().toString());
                            }else{
                                global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.op_zone_change_check), getResources().getColor(R.color.colorPrimary));
                            }
                        }else{
                            Globally.EldScreenToast(caCycleTV, getString(R.string.stop_vehicle_alert),
                                    getResources().getColor(R.color.colorVoilation));
                        }
                    }

                break;


        }
    }


    private boolean isSleepOffDuty(){
        String currentJob     = SharedPref.getDriverStatusId(getActivity());
        if(currentJob.equals(global.SLEEPER) || currentJob.equals(global.OFF_DUTY)){
            return true;
        }else{
            return false;
        }
    }


    /**
     * Creating new user node under 'users'
     */
   /* private void addUsageData() {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth

        // Check for already existed userId
        if (TextUtils.isEmpty(userId)) {
            // create User data
        } else {
            // add User data

        }


       *//* if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }*//*

        DataUsageModel user = new DataUsageModel(
                DriverId, DriverName,
                AlsSendingData, AlsReceivedData,
                MobileUsage, TotalUsage,
                global.getCurrentDate());

        Map<String, DataUsageModel> users = new HashMap<>();
        users.put(DriverName, user);

        mFirebaseDatabase.child(DriverId).push().setValue(user);

      //  mFirebaseDatabase.setValue(users);


    }

*/

    private class DeferralListener implements DeferralDialog.DeferralListener{

        @Override
        public void JobBtnReady() {

            if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                SharedPref.setDeferralForMain(true, getActivity());
            }else{
                SharedPref.setDeferralForCo(true, getActivity());
            }

            global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.Deferralenabled), getResources().getColor(R.color.colorPrimary));

            JSONArray savedDeferralArray = deferralMethod.getSavedDeferralArray(Integer.valueOf(DriverId), dbHelper);
            JSONObject deferralObj = deferralMethod.GetDeferralJson(DriverId, DeviceId, TruckNumber, CompanyId,
                    Globally.LATITUDE, Globally.LONGITUDE, SharedPref.getObdEngineHours(getActivity()),
                    SharedPref.getHighPrecisionOdometer(getActivity()),
                    ""+leftOffOrSleeperMin, "1");
            savedDeferralArray.put(deferralObj);

            // save deferral event inn local db and push automatically later. Reason behind this to save in offline also when internet is not working.
            deferralMethod.DeferralLogHelper(Integer.valueOf(DriverId), dbHelper, savedDeferralArray);

           /* hMethods.SaveDriversJob(DriverId, DeviceId, "", getString(R.string.enable_deferral_rule),
                    LocationType, "", false, isNorthCanada, DriverType, constants,
                    MainDriverPref, CoDriverPref, eldSharedPref, coEldSharedPref,
                    syncingMethod, global, hMethods, dbHelper, getActivity() ) ;

*/
        }

        @Override
        public void CancelBtnReady() {
            deferralSwitchButton.setChecked(false);
        }
    }

    private class RemarksListener implements AdverseRemarksDialog.RemarksListener{

        @Override
        public void CancelReady() {
            adverseSwitchButton.setChecked(false);
            try {
                if (adverseRemarksDialog != null && adverseRemarksDialog.isShowing())
                    adverseRemarksDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void JobBtnReady(String AdverseExceptionRemarks, boolean IsClaim, boolean IsCompanyAssign) {
            if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                SharedPref.setAdverseExcptn(true, getActivity());
            }else{
                SharedPref.setAdverseExcptnCo(true, getActivity());
            }

            getExceptionStatus();

            global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.advrs_excptn_enabled), getResources().getColor(R.color.colorPrimary));

            try {
                if (adverseRemarksDialog != null && adverseRemarksDialog.isShowing())
                    adverseRemarksDialog.dismiss();

                hMethods.SaveDriversJob(DriverId, DeviceId, AdverseExceptionRemarks, getString(R.string.enable_adverse_exception),
                        LocationType, "", false, isNorthCanada, DriverType, constants,
                        MainDriverPref, CoDriverPref, eldSharedPref, coEldSharedPref,
                        syncingMethod, global, hMethods, dbHelper, getActivity() ) ;

            }catch (Exception e){
                e.printStackTrace();
            }

          /*  try {
                // creating log when haul exception is not enabled to check log array
                int currentStatus = Integer.valueOf(SharedPref.getDriverStatusId(getActivity()));
                constants.writeViolationFile(global.getDateTimeObj(global.getCurrentDate(), false),
                        global.GetCurrentUTCDateTime(),
                        Integer.valueOf(DriverId), CurrentCycleId,
                        (int) global.GetTimeZoneOffSet(),
                        global.isSingleDriver(getActivity()),
                        currentStatus, false, isHaulExcptn,
                        "Not able to allow Adverse Exception.",
                        hMethods, dbHelper, getActivity());
            }catch (Exception e){
                e.printStackTrace();
            }*/

        }
    }


    /*================== Confirmation Listener ====================*/
    private class ConfirmListener implements ConfirmationDialog.ConfirmationListener {

        @Override
        public void OkBtnReady() {

            SharedPref.setAsyncCancelStatus(true, getActivity());
            IsDownloading = false;
            fadeViewAnim.cancel();

            downloadProgressBar.setVisibility(View.GONE);
            downloadHintImgView.setVisibility(View.GONE);
            updateAppDownloadIV.setVisibility(View.VISIBLE);

            checkAppUpdateTV.setText(getResources().getString(R.string.Update_Status));

            confirmationDialog.dismiss();
        }
    }

    CheckConnectivity.ConnectivityInterface ConnectivityInterface = new CheckConnectivity.ConnectivityInterface() {
        @Override
        public void IsConnected(boolean result, int flag) {
            Log.d("networkUtil", "result: " +result );

            if (result) {
                if(flag == CheckInternetConnection) {
                    if(progressDialog != null)
                        progressDialog.dismiss();
                    Constants.IsAlsServerResponding = true;
                    global.EldScreenToast(SyncDataBtn, "Internet Connected.", getResources().getColor(R.color.colorPrimary));
                }else if(flag == CheckUpdate){

                    if(IsManualAppDownload){
                        GetAppDetails(APIs.GET_MANNUAL_APK_DETAIL);
                    }else{
                        if(ApkFilePath.length() == 0) {
                            GetAppDetails(APIs.GET_ANDROID_APP_DETAIL);
                        }else{
                            CheckAppStatus(); //downloadButtonClicked(ApkFilePath, VersionCode, VersionName, IsDownloading);
                        }
                    }

                }else{

                    if(IsLogPermission) {
                        DriverLogFile = global.GetSavedFile(getActivity(), ConstantsKeys.ViolationTest, "txt");

                        // Save Cycle record in file on local storage
                        try {
                            JSONArray cycleArray = new JSONArray(SharedPref.GetCycleDetails(getActivity()));
                            if(cycleArray.length() > 0) {
                                cycleUpdationRecordFile = global.SaveFileInSDCard("Cycle_", cycleArray.toString(), false, getActivity());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    // Sync driver log API data to server with SAVE_LOG_TEXT_FILE (SAVE sync data service)
                    SyncDataUpload syncDataUpload = new SyncDataUpload(getActivity(), DriverId, syncingFile, DriverLogFile, cycleUpdationRecordFile, IsLogPermission, asyncResponse );
                    syncDataUpload.execute();

                }
            } else {
                settingSpinImgVw.stopAnimation();
                if(progressDialog != null){
                    progressDialog.dismiss();
                }
                global.EldScreenToast(SyncDataBtn, "Internet not working.", getResources().getColor(R.color.colorVoilation) );
            }
        }
    };



    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long percentage     = intent.getIntExtra("percentage", 0);
            ApkFilePath         = intent.getStringExtra("path");
            boolean isCompleted = intent.getBooleanExtra("isCompleted", false);

            if(percentage >= progressPercentage) {
                //  IsDownloading = true;
                downloadProgressBar.setProgress(percentage);
                progressPercentage = percentage;
            }
            if(isCompleted){

                fadeViewAnim.cancel();

                IsDownloading = false;
                downloadProgressBar.setVisibility(View.GONE);
                downloadHintImgView.setVisibility(View.GONE);
                updateAppDownloadIV.setVisibility(View.VISIBLE);

                if(ApkFilePath.equals("Downloading failed.")){
                    global.EldScreenToast(SyncDataBtn, ApkFilePath, getResources().getColor(R.color.colorSleeper));
                    ApkFilePath = "";
                    ExistingApkVersionCode = "";
                    ExistingApkVersionName = "";
                    checkAppUpdateTV.setText(getResources().getString(R.string.Update_Status));
                }else{
                    global.EldScreenToast(SyncDataBtn, "Downloading completed.", getResources().getColor(R.color.colorPrimary));
                    checkAppUpdateTV.setText(getResources().getString(R.string.install_updates));

                    if (ApkFilePath.length() > 0) {
                        InstallApp(ApkFilePath);
                    }
                }
            }

        }
    };


    String getExistingApkPath(){
        File apkFile = global.getAlsApkPath(getActivity());
        String path = "";
        try{
            if(apkFile != null) {
                for (File f : apkFile.listFiles()) {
                    if (f.isFile()) {
                        path = f.getName();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return path;
    }


    void getInstalledAppDetail(){
        ExistingApkVersionCode = global.GetAppVersion(getActivity(), "VersionCode");
        ExistingApkVersionName = global.GetAppVersion(getActivity(), "VersionName");

    }


    void InstallApp(String appPath){
        progressPercentage = 0;

        File toInstall = new File(appPath);

        if(toInstall.isFile()) {
            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){ //Build.VERSION.SDK_INT < Build.VERSION_CODES.P &&
                global.EldScreenToast(SyncDataBtn, "Not able to install app directly. You can install it manually in (Logistic/AlsApp/) folder.", Color.parseColor("#358A0D"));
            }else */

            if(AppInstallAttemp < 2) { // It means apk file has some problem and need to delete it to download again.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                  //  Uri apkUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", toInstall);
                    Uri apkUri = FileProvider.getUriForFile(getActivity(), constants.packageName + ".provider", toInstall);
                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,
                            getActivity().getApplicationInfo().packageName);
                    //  startActivityForResult(intent, REQUEST_INSTALL);


                    startActivity(intent);
                } else {
                    Uri apkUri = Uri.fromFile(toInstall);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                AppInstallAttemp++;
            }else{
                // Deleter apk file is exist
                String folder      = global.getAlsApkPath(getActivity()).toString();
                global.DeleteDirectory(folder);
                AppInstallAttemp = 0;

                checkAppUpdateBtn.performClick();

            }
        }

    }



    void downloadButtonClicked(String url, String VersionCode, String VersionName, boolean downloadStatus) {

        IsDownloading = true;
        downloadProgressBar.setProgress(0);
        downloadProgressBar.setVisibility(View.VISIBLE);
        downloadHintImgView.setVisibility(View.VISIBLE);
        updateAppDownloadIV.setVisibility(View.GONE);

        checkAppUpdateTV.setText(getResources().getString(R.string.Downloading));
        ApkFilePath = "";
        progressPercentage = 0;
        checkAppUpdateTV.startAnimation(fadeViewAnim);
        downloadHintImgView.startAnimation(fadeViewAnim);


        Intent serviceIntent = new Intent(getActivity(), downloadAppService.getClass());
        serviceIntent.putExtra("url", url);
        serviceIntent.putExtra("VersionCode", VersionCode);
        serviceIntent.putExtra("VersionName", VersionName);
        serviceIntent.putExtra("isDownloading", downloadStatus);
        getActivity().startService(serviceIntent);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){

            case R.id.caCycleSpinner:
                SelectedCanCycle = CanCycleList.get(position).getCycleId();
                break;

            case R.id.usCycleSpinner:
                SelectedUsaCycle = UsaCycleList.get(position).getCycleId();
                break;

            case R.id.timeZoneSpinner:
                SelectedTimeZone = TimeZoneList.get(position).getTimeZoneID();
                break;

        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    void ClearDriverUnSavedlog(){
        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ) { // Single Driver Type and Position is 0
            DriverType = Constants.MAIN_DRIVER_TYPE;
            MainDriverPref.ClearLocFromList(getActivity());
        }else{
            DriverType = Constants.CO_DRIVER_TYPE;
            CoDriverPref.ClearLocFromList(getActivity());
        }
    }


    /*================== Get app details ===================*/
    void GetAppDetails(String api){  /*, final String SearchDate*/

        progressDialog.show();

        params = new HashMap<String, String>();
        GetAppUpdateRequest.executeRequest(com.android.volley.Request.Method.GET, api , params, GetAppUpdate,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    /*================== Get Driver Trip Details ===================*/
    void GetDriverLogPermission(final String DriverId){  /*, final String SearchDate*/

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        GetDriverLogPostPermission.executeRequest(com.android.volley.Request.Method.POST, APIs.DRIVER_VIOLATION_PERMISSION , params, DriverLogPermission,
                Constants.SocketTimeout10Sec,  ResponseCallBack, ErrorCallBack);

    }


    /*================== Get Cycle Change Approval request ===================*/
    void getCycleChangeApproval(final String DriverId, final String DeviceId, final String CompanyId){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId);
         params.put(ConstantsKeys.CompanyId, CompanyId);
        getCycleChangeApproval.executeRequest(com.android.volley.Request.Method.POST, APIs.GET_CYCLE_CHANGE_REQUESTS , params, CycleChangeApproval,
                 Constants.SocketTimeout10Sec,  ResponseCallBack, ErrorCallBack);

    }

    /*================== change driver Cycle request ===================*/
    void changeCycleRequest(final String DriverId, final String DeviceId, final String CompanyId, String Id,
                            String Status, String ChangedCycleId, String CurrentCycleId, String Latitude ,
                            String Longitude, String DriverTimeZone, String PowerUnitNumber, String LogDate){

        String CoDriverId = "";
        if(DriverId.equals(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()))){
            CoDriverId = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
        }else{
            CoDriverId = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
        }

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.CoDriverId, CoDriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.Id, Id);
        params.put(ConstantsKeys.Status, Status);
        params.put(ConstantsKeys.CycleId, ChangedCycleId);
        params.put(ConstantsKeys.CurrentCycleId, CurrentCycleId);
        params.put(ConstantsKeys.Latitude, Latitude);
        params.put(ConstantsKeys.Longitude, Longitude);
        params.put(ConstantsKeys.DriverTimeZone, DriverTimeZone);
        params.put(ConstantsKeys.PowerUnitNumber, PowerUnitNumber);
        params.put(ConstantsKeys.LogDate, LogDate);
        params.put(ConstantsKeys.LocationType, SharedPref.getLocMalfunctionType(getActivity()));

        ChangeCycleRequest.executeRequest(com.android.volley.Request.Method.POST, APIs.CHANGE_DRIVER_CYCLE , params, ChangeCycle,
                Constants.SocketTimeout10Sec,  ResponseCallBack, ErrorCallBack);

    }


    /*================== Save Operating zone request ===================*/
    void ChangeOperatingZone(final String DriverId, final String DeviceId, final String CompanyId, final String IsSouthCanada,
                             final String CycleId, final String DriverTimeZone, final String Latitude, final String Longitude, final String PowerUnitNumber){

        String CoDriverId = "";
        if(DriverId.equals(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()))){
            CoDriverId = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
        }else{
            CoDriverId = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
        }

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.CoDriverId, CoDriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.IsSouthCanada, IsSouthCanada);
        params.put(ConstantsKeys.CycleId, CycleId);
        params.put(ConstantsKeys.DriverTimeZone, DriverTimeZone);
        params.put(ConstantsKeys.Latitude, Latitude);
        params.put(ConstantsKeys.Longitude, Longitude);
        params.put(ConstantsKeys.PowerUnitNumber, PowerUnitNumber);
        params.put(ConstantsKeys.LocationType, SharedPref.getLocMalfunctionType(getActivity()));

        OperatingZoneRequest.executeRequest(com.android.volley.Request.Method.POST, APIs.CHANGE_OPERATING_ZONE , params, OperatingZone,
                Constants.SocketTimeout10Sec,  ResponseCallBack, ErrorCallBack);

    }


    void CheckAppStatus(){
        if (ExistingVersionCodeInt >= VersionCodeInt) {

            global.EldScreenToast(SyncDataBtn, "Your application is up to date.", getResources().getColor(R.color.colorPrimary));

        }else{
            // Check app is already saved in sd card
            existingApkFilePath = getExistingApkPath();
            if (existingApkFilePath.length() > 0) {
                String[] apkPathArray = existingApkFilePath.split("_");
                if (apkPathArray.length > 2) {
                    ExistingApkVersionCode = apkPathArray[1];
                    ExistingApkVersionName = apkPathArray[2];
                    ExistingApkVersionName = ExistingApkVersionName.replaceAll(".apk", "");

                    if (ExistingApkVersionCode.equals(VersionCode) && ExistingApkVersionName.equals(VersionName)) {
                        checkAppUpdateTV.setText("Install Updates");
                        global.EldScreenToast(SyncDataBtn, "This application is already in (Logistic/AlsApp/) folder.", getResources().getColor(R.color.colorPrimary));
                        ApkFilePath = global.getAlsApkPath(getActivity()) + "/" + existingApkFilePath;
                        InstallApp(ApkFilePath);
                    } else {
                        downloadButtonClicked(ApkFilePath, VersionCode, VersionName, IsDownloading);
                    }
                } else {
                    downloadButtonClicked(ApkFilePath, VersionCode, VersionName, IsDownloading);
                }
            } else {
                downloadButtonClicked(ApkFilePath, VersionCode, VersionName, IsDownloading);
            }

        }
    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null;  //, dataObj = null;
            String status = "", Message = "";
            JSONObject dataObj = null;

            try {
                settingSpinImgVw.stopAnimation();
                obj = new JSONObject(response);
                status = obj.getString("Status");
                Message = obj.getString("Message");

            } catch (JSONException e) {
            }

            if (status.equalsIgnoreCase("true")) {
                switch (flag) {
                    case GetAppUpdate:
                        try {
                            Log.d("response", "response: " + response);


                            if(progressDialog != null)
                                progressDialog.dismiss();

                            dataObj = new JSONObject(obj.getString("Data"));
                            VersionCode = dataObj.getString("VersionCode");
                            VersionName = dataObj.getString("VersionName");
                            ApkFilePath = dataObj.getString("ApkFilePath");

                            try {
                                ExistingVersionCodeInt  = Integer.valueOf(ExistingApkVersionCode);
                                VersionCodeInt          = Integer.valueOf(VersionCode);
                            }catch (Exception e){
                                ExistingVersionCodeInt = 0;
                                VersionCodeInt = 0;
                                e.printStackTrace();
                            }

                            if(IsManualAppDownload == false && ExistingApkVersionCode.equals(VersionCode) && ExistingApkVersionName.equals(VersionName)){
                                global.EldScreenToast(SyncDataBtn, "Your application is up to date.", getResources().getColor(R.color.colorPrimary));
                            }else {
                                CheckAppStatus();

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;

                    case DriverLogPermission:
                        try {
                            IsLogPermission = obj.getBoolean("Data"); // "Data" parameter is used as Permission parameter to upload or not Driver's 18 Days Log to server with sync file

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;

                    case CycleChangeApproval:

                        break;

                    case ChangeCycle:
                        global.EldScreenToast(SyncDataBtn, Message, getResources().getColor(R.color.colorPrimary));


                        saveUpdatedCycleData();

                        break;

                    case OperatingZone:
                        global.EldScreenToast(SyncDataBtn, Message, getResources().getColor(R.color.colorPrimary));

                        if(IsSouthCanada.equals("true")) {
                            SharedPref.SetNorthCanadaStatus(false, getActivity());
                        }else{
                            SharedPref.SetNorthCanadaStatus(true, getActivity());
                        }

                        getSavedCycleData();
                        break;


                }
            }else{
                try {
                    if(progressDialog != null)
                        progressDialog.dismiss();
                    settingSpinImgVw.stopAnimation();

                    if(Message.equals("Device Logout") ){
                        global.ClearAllFields(getActivity());
                        global.StopService(getActivity());
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(i);
                        getActivity().finish();
                    }else{
                        if(flag == ChangeCycle){
                            Globally.DriverSwitchAlert(getActivity(), "Cycle Change Request !!", Message, "Ok");
                        }else if(flag == GetAppUpdate || flag == OperatingZone){
                            global.EldScreenToast(SyncDataBtn, Message, getResources().getColor(R.color.colorVoilation));
                        }



                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {
            switch (flag){

                default:
                    if(progressDialog != null)
                        progressDialog.dismiss();
                    settingSpinImgVw.stopAnimation();

                    Log.d("Driver", "error" + error.toString());
                    break;
            }
        }
    };


    AsyncResponse asyncResponse = new AsyncResponse() {
        @Override
        public void onAsyncResponse(String response, String DriverId) {
            if(progressDialog != null){
                progressDialog.dismiss();
            }

            Log.e("String Response", ">>>Sync Response:  " + response);
            settingSpinImgVw.stopAnimation();

            try {

                JSONObject obj = new JSONObject(response);
                String status = obj.getString("Status");
                if (status.equalsIgnoreCase("true")) {
                    String msgTxt = "Data syncing is completed" ;

                    /* ------------ Delete posted files from local after successfully posted to server --------------- */
                    if(isCoDriverSync == false) {
                        if (syncingFile != null && syncingFile.exists()) {
                            syncingFile.delete();
                            syncingFile = null;
                        }

                        if (IsLogPermission) {
                            if (DriverLogFile != null && DriverLogFile.exists()) {
                                DriverLogFile.delete();
                                DriverLogFile = null;
                                msgTxt = "Data syncing is completed with violation log file";
                            }

                            if (cycleUpdationRecordFile != null && cycleUpdationRecordFile.exists()) {
                                cycleUpdationRecordFile.delete();
                                SharedPref.SetCycleOfflineDetails("[]", getActivity());
                                cycleUpdationRecordFile = null;
                            }

                        }
                        /* -------------------------------------------------------------------------------------------------- */

                        IsLogPermission = false;
                        ClearDriverUnSavedlog();

                        syncingMethod.SyncingLogHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());
                        global.EldScreenToast(SettingSaveBtn, msgTxt, getResources().getColor(R.color.colorPrimary));
                    }else{
                        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ) { // Single Driver Type and Position is 0
                            // when main driver is selected, clear co driver data
                            CoDriverPref.ClearLocFromList(getActivity());
                        }else{
                            // clear main driver data
                            MainDriverPref.ClearLocFromList(getActivity());
                        }

                        if (coDriverSyncFile != null && coDriverSyncFile.exists()) {
                            coDriverSyncFile.delete();
                            coDriverSyncFile = null;
                        }

                    }

                    if(global.isSingleDriver(getActivity()) == false && isCoDriverSync == false){
                        String CoDriverId = "";
                        if(DriverId.equals(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()))){
                            CoDriverId = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
                        }else{
                            CoDriverId = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
                        }

                        isCoDriverSync = true;
                        SyncCoDriverData(CoDriverId);

                    }

                }else {
                    if(syncingFile != null && syncingFile.exists())
                        syncingFile.delete();

                    if(DriverLogFile != null && DriverLogFile.exists())
                        DriverLogFile.delete();

                    String message = obj.getString("Message");
                    if(message.contains("ServerError")){
                        message = "ALS server not responding";
                    }else if(message.contains("Network")){
                        message = "Internet connection problem";
                    }else if(message.contains("NoConnectionError")){
                        message = "Connection not working.";
                    }

                    global.EldScreenToast(SettingSaveBtn, message, getResources().getColor(R.color.colorVoilation));

                    if(message.equalsIgnoreCase("Device Logout") && constant.GetDriverSavedArray(getActivity()).length() == 0 ){
                        global.ClearAllFields(getActivity());
                        global.StopService(getActivity());
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(i);
                        getActivity().finish();
                    }
                }

            } catch (Exception e) {
                if(getActivity() != null) {
                    global.EldScreenToast(SettingSaveBtn, "Error occurred", getResources().getColor(R.color.colorVoilation));
                }
                e.printStackTrace();
            }
        }
    };


    void getSavedCycleData(){

        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ) {
            DriverType = Constants.MAIN_DRIVER_TYPE;
            SavedPosition  = 0;
            SavedCanCycle  = DriverConst.GetDriverSettings(DriverConst.CANCycleId, getActivity());
            SavedUsaCycle  = DriverConst.GetDriverSettings(DriverConst.USACycleId, getActivity());
            SavedTimeZone  = DriverConst.GetDriverSettings(DriverConst.TimeZoneID, getActivity());
            CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());
            TruckNumber    = DriverConst.GetDriverTripDetails(DriverConst.Truck, getActivity());
            DriverTimeZone = DriverConst.GetDriverSettings(DriverConst.DriverTimeZone, getActivity());
        }else{
            DriverType = Constants.CO_DRIVER_TYPE;
            SavedPosition  = 1;
            SavedCanCycle  = DriverConst.GetCoDriverSettings(DriverConst.CoCANCycleId, getActivity());
            SavedUsaCycle  = DriverConst.GetCoDriverSettings(DriverConst.CoUSACycleId, getActivity());
            SavedTimeZone  = DriverConst.GetCoDriverSettings(DriverConst.CoTimeZoneID, getActivity());
            CurrentCycleId = DriverConst.GetCoDriverCurrentCycle(DriverConst.CoCurrentCycleId, getActivity());
            TruckNumber    = DriverConst.GetCoDriverTripDetails(DriverConst.CoTruck, getActivity());
            DriverTimeZone = DriverConst.GetCoDriverSettings(DriverConst.CoDriverTimeZone, getActivity());
        }

        isNorthCanada  =  SharedPref.IsNorthCanada(getActivity());
        if(CurrentCycleId.equals(global.CANADA_CYCLE_1) || CurrentCycleId.equals(global.CANADA_CYCLE_2) ){
            caCurrentCycleTV.setVisibility(View.VISIBLE);
            usCurrentCycleTV.setVisibility(View.GONE);
            operatingZoneTV.setText("");
            opZoneTmgView.setVisibility(View.VISIBLE);
            canEditImgView.setVisibility(View.VISIBLE);
            usEditImgView.setVisibility(View.GONE);
            deferralRuleLay.setVisibility(View.VISIBLE);

            if(isNorthCanada) {
                operatingZoneTV.setText(getString(R.string.OperatingZoneNorth));
            }else{
                operatingZoneTV.setText(getString(R.string.OperatingZoneSouth));
            }
        }else{
            caCurrentCycleTV.setVisibility(View.GONE);
            usCurrentCycleTV.setVisibility(View.VISIBLE);
            opZoneTmgView.setVisibility(View.INVISIBLE);
            canEditImgView.setVisibility(View.GONE);
            usEditImgView.setVisibility(View.VISIBLE);
            operatingZoneTV.setText(getString(R.string.OperatingZoneUS));
            deferralRuleLay.setVisibility(View.GONE);
        }
    }

    void saveUpdatedCycleData(){

        CurrentCycleId = changedCycleId;
        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ) {
            if(SavedCycleType.equals("can_cycle")){
                SavedCanCycle = changedCycleId;

                DriverConst.SetDriverSettings(changedCycleName, CurrentCycleId, CurrentCycleId, SavedUsaCycle,  changedCycleName,
                        DriverConst.GetDriverSettings(DriverConst.USACycleName, getActivity()),
                        DriverTimeZone, DriverConst.GetDriverSettings(DriverConst.OffsetHours, getActivity()),
                        DriverConst.GetDriverSettings(DriverConst.TimeZoneID, getActivity()), getActivity());

            }else{
                SavedUsaCycle = changedCycleId;
                DriverConst.SetDriverSettings(changedCycleName, CurrentCycleId, SavedCanCycle,
                        changedCycleId,  DriverConst.GetDriverSettings(DriverConst.CANCycleName, getActivity()),
                        changedCycleName,
                        DriverTimeZone, DriverConst.GetDriverSettings(DriverConst.OffsetHours, getActivity()),
                        DriverConst.GetDriverSettings(DriverConst.TimeZoneID, getActivity()), getActivity());
            }

            DriverConst.SetDriverCurrentCycle(changedCycleName, CurrentCycleId, getActivity());

        }else{
            if(SavedCycleType.equals("can_cycle")){
                SavedCanCycle = changedCycleId;
                DriverConst.SetCoDriverSettings(changedCycleName, CurrentCycleId, CurrentCycleId, SavedUsaCycle,  changedCycleName,
                        DriverConst.GetCoDriverSettings(DriverConst.USACycleName, getActivity()),
                        DriverTimeZone, DriverConst.GetCoDriverSettings(DriverConst.OffsetHours, getActivity()),
                        DriverConst.GetCoDriverSettings(DriverConst.TimeZoneID, getActivity()), getActivity());


            }else{
                SavedUsaCycle = changedCycleId;
                DriverConst.SetCoDriverSettings(changedCycleName, CurrentCycleId, SavedCanCycle,
                        changedCycleId,  DriverConst.GetCoDriverSettings(DriverConst.CANCycleName, getActivity()),
                        changedCycleName,
                        DriverTimeZone, DriverConst.GetCoDriverSettings(DriverConst.OffsetHours, getActivity()),
                        DriverConst.GetCoDriverSettings(DriverConst.TimeZoneID, getActivity()), getActivity());
            }

            DriverConst.SetCoDriverCurrentCycle(changedCycleName, CurrentCycleId, getActivity());
        }


        getSavedCycleData();

        DisplayCanCycles();
        DisplayUsaCycles();
        DisplayTimeZones();


    }


    private void MoveFragment(Fragment fragment){
        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, fragment);
        fragmentTran.addToBackStack("obd_diagnose");
        fragmentTran.commit();


    }






    public void haulExceptionAlert() {
        if(enableExceptionAlert != null && enableExceptionAlert.isShowing()){
            Log.d("dialog", "dialog is showing" );
        }else {
            closeDialogs();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(getString(R.string.enable_excp));
            String message = "<font color='#555555'><b>Note: </b></font>" + getString(R.string.haul_excp_reset_auto) + "<br/> <br/>" + getString(R.string.continue_haul_exception) ;
            alertDialogBuilder.setMessage(Html.fromHtml(message));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();

                            boolean isHaulException = constant.getShortHaulExceptionDetail(getActivity(), DriverId, global,
                                                        isHaulExcptn, isAdverseExcptn, isNorthCanada, hMethods, dbHelper);

                            if (isHaulException) {
                                if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                                    SharedPref.set16hrHaulExcptn(true, getActivity());
                                }else{
                                    SharedPref.set16hrHaulExcptnCo(true, getActivity());
                                }

                                global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.haul_excptn_enabled), getResources().getColor(R.color.colorPrimary));

                                hMethods.SaveDriversJob(DriverId, DeviceId, "", getString(R.string.enable_ShortHaul_exception),
                                        LocationType, "", true, isNorthCanada, DriverType, constants,
                                        MainDriverPref, CoDriverPref, eldSharedPref, coEldSharedPref,
                                        syncingMethod, global, hMethods, dbHelper, getActivity());

                            } else {
                                global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.halu_excp_not_eligible), getResources().getColor(R.color.colorVoilation));
                                haulExceptnSwitchButton.setChecked(false);

                                if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                                    SharedPref.set16hrHaulExcptn(false, getActivity());
                                }else{
                                    SharedPref.set16hrHaulExcptnCo(false, getActivity());
                                }

                                try {
                                    // creating log when haul exception is not enabled to check log array
                                    int currentStatus = Integer.valueOf(SharedPref.getDriverStatusId(getActivity()));
                                    constants.writeViolationFile(global.getDateTimeObj(global.getCurrentDate(), false),
                                            global.GetCurrentUTCDateTime(),
                                            Integer.valueOf(DriverId), CurrentCycleId,
                                            (int) global.GetTimeZoneOffSet(),
                                            global.isSingleDriver(getActivity()),
                                            currentStatus, false, isHaulExcptn,
                                            "Not able to allow 16 hr Haul Exception.",
                                            hMethods, dbHelper, getActivity());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }

                            getExceptionStatus();
                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    haulExceptnSwitchButton.setChecked(false);
                    dialog.dismiss();

                }
            });


            enableExceptionAlert = alertDialogBuilder.create();
            vectorDialogs.add(enableExceptionAlert);
            enableExceptionAlert.show();
        }
    }


    public void closeDialogs() {
        for (AlertDialog dialog : vectorDialogs)
            if (dialog.isShowing()) dialog.dismiss();
    }


    public boolean isAllowToEnableException(String DriverId){
        boolean isAllow = false;
        exceptionDesc = "";
        String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());

        try {

            if(CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
                JSONArray logArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
                JSONObject lastObj = hMethods.GetLastJsonFromArray(logArray);

                int status = lastObj.getInt(ConstantsKeys.DriverStatusId);
                boolean yardMove = lastObj.getBoolean(ConstantsKeys.YardMove);

                if(status == Constants.DRIVING || (status == Constants.ON_DUTY && yardMove == false) ){
                    isAllow = true;
                }else{
                    if(status == Constants.ON_DUTY && yardMove){
                        exceptionDesc = "Exception not allowed in Yard Move.";
                    }else if(status == Constants.OFF_DUTY){
                        boolean Personal = lastObj.getBoolean(ConstantsKeys.Personal);
                        if(Personal){
                            exceptionDesc = "Exception not allowed in Personal Use.";
                        }else{
                            exceptionDesc = "Exception not allowed in Off Duty.";
                        }
                    }else{
                        exceptionDesc = "Exception not allowed in Sleeper.";
                    }
                }
            }else{
                exceptionDesc = "Exception not allowed in Canada Cycle.";
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isAllow;

    }




}
