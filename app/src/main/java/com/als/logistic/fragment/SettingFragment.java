package com.als.logistic.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.AppDownloadService;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.AsyncResponse;
import com.constants.CheckConnectivity;
import com.constants.CircularProgressBar;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.DownloadAppService;
import com.constants.LoadingSpinImgView;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.SyncDataUpload;
import com.constants.VolleyRequest;
import com.custom.dialogs.AdverseRemarksDialog;
import com.custom.dialogs.AgricultureDialog;
import com.als.logistic.AppPermissionActivity;
import com.custom.dialogs.ChangeCycleDialog;
import com.custom.dialogs.ConfirmationDialog;
import com.custom.dialogs.DeferralDialog;
import com.custom.dialogs.DriverAddressDialog;
import com.custom.dialogs.ObdDataInfoDialog;
import com.local.db.DeferralMethod;
import com.local.db.MalfunctionDiagnosticMethod;
import com.models.CycleModel;
import com.driver.details.DriverConst;
import com.driver.details.ParseLoginDetails;
import com.models.TimeZoneModel;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.SyncingMethod;
import com.als.logistic.Globally;
import com.als.logistic.LoginActivity;
import com.als.logistic.R;
import com.als.logistic.TabAct;
import com.als.logistic.UILApplication;
import com.shared.pref.CaCyclePrefManager;
import com.shared.pref.CoCAPref;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.CoTimeZonePref;
import com.shared.pref.CoUSPref;
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

import kotlin.jvm.internal.Intrinsics;
import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;
import webapi.LocalCalls;


public class SettingFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{


    View rootView;
    TextView actionBarTitle, caCycleTV, usCycleTV, timeZoneTV, dateActionBarTV, checkAppUpdateTV, haulExpTxtView, haulExcptnTxtVw,adverseExpTxtView,adverseCanadaExpTxtView,deferralTxtView;
    TextView caCurrentCycleTV, usCurrentCycleTV, operatingZoneTV, agricultureExpTxtView, loggingTxtView, storageTextView;
    Spinner caCycleSpinner, usCycleSpinner, timeZoneSpinner;
    Button SettingSaveBtn;
    ImageView updateAppDownloadIV, downloadHintImgView, opZoneTmgView, canEditImgView, usEditImgView;
    LoadingSpinImgView settingSpinImgVw, downloadBlinkIV;
    RelativeLayout rightMenuBtn, eldMenuLay, checkAppUpdateBtn, haulExceptionLay, SyncDataBtn, checkInternetBtn,
            obdDiagnoseBtn, docBtn, deferralRuleLay, brightnessSoundEditBtn, settingsMainLay, actionbarMainLay,
            updateBlinkLayout, checkPermissionBtn;
    LinearLayout canCycleLayout, usaCycleLayout, timeZoneLayout;
    SwitchCompat deferralSwitchButton, haulExceptnSwitchButton, adverseSwitchButton,adverseCanadaSwitchButton,
            loggingSwitchButton, agricultureSwitchButton,dayNightSwitchButton;
    List<CycleModel> CanCycleList;
    List<CycleModel> UsaCycleList;
    List<TimeZoneModel> TimeZoneList;
    ScrollView settingsScrollView;
    ProgressBar storageProgress;

    CheckConnectivity checkConnectivity;
    ParseLoginDetails SaveSettingDetails;
    CaCyclePrefManager caPrefManager;
    USCyclePrefManager usPrefmanager;
    TimeZonePrefManager timeZonePrefManager;
    SyncingMethod syncingMethod;
    DBHelper dbHelper;
    HelperMethods hMethods;
    DeferralMethod deferralMethod;
    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;

    Constants constant;
    Map<String, String> params;

    CoCAPref coCAPrefManager;
    CoUSPref coUSPrefmanager;
    CoTimeZonePref coTimePrefManager;
    ArrayAdapter<String> CanCycleAdapter;


    int SyncData = 1, CheckInternetConnection = 2, CheckUpdate = 3;
    int ExistingVersionCodeInt  = 0,  VersionCodeInt = 0, AppInstallAttemp = 0;
    int CanListSize = 0, UsaListSize = 0, TimeZoneListSize = 0, SavedPosition = 0;
    String SavedCanCycle = "", SavedUsaCycle = "", CurrentCycleId = "", SavedTimeZone = "", DeviceId = "", DriverId = "",
            CoDriverId = "", DriverName = "", CoDriverName = "", CompanyId = "";
    String SelectedCanCycle = "", SelectedUsaCycle = "", SelectedTimeZone = "", exceptionDesc = "", TruckNumber,
            DriverTimeZone,  IsSouthCanada, SavedCycleType, changedCycleId, changedCycleName, LocationType = "",
            SourceAddress = "--", agricultureAddress = "";
    String SourceLatitude = "", SourceLongitude = "";
    String Approved = "2";
    String Rejected = "3";

    ProgressDialog progressDialog;
    ConfirmationDialog confirmationDialog;
    AdverseRemarksDialog adverseRemarksDialog;
    DeferralDialog deferralDialog;
    DriverAddressDialog driverAddressDialog;
    AgricultureDialog agricultureDialog;

    CheckConnectivity connectivityTask;
    File syncingFile = null;
    File coDriverSyncFile = null;
    File DriverLogFile = null;
    File cycleUpdationRecordFile = null;

    private String url = "", ApkFilePath = "", existingApkFilePath = "";
    String VersionCode = "", VersionName = "", ExistingApkVersionCode = "", ExistingApkVersionName = "";
    CircularProgressBar downloadProgressBar;
    VolleyRequest GetAppUpdateRequest, GetDriverLogPostPermission, getCycleChangeApproval, ChangeCycleRequest, OperatingZoneRequest,
            AddressLatLongRequest, GetAddFromLatLngRequest, SaveAgricultureRequest;
    final int GetAppUpdate  = 1, DriverLogPermission = 2, CycleChangeApproval = 3, ChangeCycle = 4, OperatingZone = 5,
                AddressLatLong = 6, SaveAgricultureException = 7;
    int DriverType = 0;
    int leftOffOrSleeperMin = 0;
    long progressPercentage = 0;
    boolean isNorthCanada = false;
    boolean IsLogPermission = false, IsDownloading = false, IsManualAppDownload = false, isAgricultureExcptn = false, IsAgriExceptionEnable = false,isDayNightMode = false;
    DriverPermissionMethod driverPermissionMethod;
    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;
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
    boolean isExemptDriver = false;


    private int brightness;
    //Content resolver used as a handle to the system's settings
    private ContentResolver cResolver;
    //Window object, that will store a reference to the current window
    private Window window;
    int maxVolume=1;
    AudioManager audioManager;
    int[] volume = new int[1];
    int brightnessMode;

    SeekBar showBrightnessSeekBar,showVolumeSeekBar;
    CardView brightnessCardView;
    LinearLayout settingsParentView;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
            rootView = inflater.inflate(R.layout.fragment_settings, container, false);
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
        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();
        GetAppUpdateRequest         = new VolleyRequest(getActivity());
        GetDriverLogPostPermission  = new VolleyRequest(getActivity());
        getCycleChangeApproval      = new VolleyRequest(getActivity());
        ChangeCycleRequest          = new VolleyRequest(getActivity());
        OperatingZoneRequest        = new VolleyRequest(getActivity());
        AddressLatLongRequest       = new VolleyRequest(getActivity());
        GetAddFromLatLngRequest     = new VolleyRequest(getActivity());
        SaveAgricultureRequest      = new VolleyRequest(getActivity());

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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");

        storageProgress      = (ProgressBar) v.findViewById(R.id.storageProgress);
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
        agricultureExpTxtView= (TextView)v.findViewById(R.id.agricultureExpTxtView);
        loggingTxtView       = (TextView)v.findViewById(R.id.loggingTxtView);
        storageTextView      = (TextView)v.findViewById(R.id.storageTextView);

        caCurrentCycleTV     = (TextView)v.findViewById(R.id.caCurrentCycleTV);
        usCurrentCycleTV     = (TextView)v.findViewById(R.id.usCurrentCycleTV);
        adverseExpTxtView     = (TextView)v.findViewById(R.id.adverseExpTxtView);
        adverseCanadaExpTxtView  = (TextView)v.findViewById(R.id.adverseCanadaExpTxtView);
        deferralTxtView          = (TextView)v.findViewById(R.id.deferralTxtView);

        caCycleSpinner       = (Spinner)v.findViewById(R.id.caCycleSpinner);
        usCycleSpinner       = (Spinner)v.findViewById(R.id.usCycleSpinner);
        timeZoneSpinner      = (Spinner)v.findViewById(R.id.timeZoneSpinner);

        SettingSaveBtn       = (Button) v.findViewById(R.id.settingSaveBtn);
        updateAppDownloadIV  = (ImageView)v.findViewById(R.id.updateAppDownloadIV);
        downloadHintImgView  = (ImageView)v.findViewById(R.id.downloadHintImgView);
        opZoneTmgView        = (ImageView)v.findViewById(R.id.opZoneTmgView);
        canEditImgView       = (ImageView)v.findViewById(R.id.canEditImgView);
        usEditImgView        = (ImageView)v.findViewById(R.id.usEditImgView);

        downloadBlinkIV      = (LoadingSpinImgView)v.findViewById(R.id.downloadBlinkIV);
        settingSpinImgVw     = (LoadingSpinImgView)v.findViewById(R.id.settingSpinImgVw);

        brightnessSoundEditBtn= (RelativeLayout)v.findViewById(R.id.soundbrightnessBtn);
        haulExceptionLay     = (RelativeLayout) v.findViewById(R.id.haulExceptionLay);
        checkAppUpdateBtn    = (RelativeLayout) v.findViewById(R.id.checkAppUpdateBtn);
        eldMenuLay           = (RelativeLayout) v.findViewById(R.id.eldMenuLay);
        SyncDataBtn          = (RelativeLayout) v.findViewById(R.id.SyncDataBtn);
        checkInternetBtn     = (RelativeLayout) v.findViewById(R.id.checkInternetBtn);
        obdDiagnoseBtn       = (RelativeLayout) v.findViewById(R.id.obdDiagnoseBtn);
        docBtn               = (RelativeLayout) v.findViewById(R.id.docBtn);
        deferralRuleLay      = (RelativeLayout) v.findViewById(R.id.deferralRuleLay);
        updateBlinkLayout    = (RelativeLayout) v.findViewById(R.id.updateBlinkLayout);
        checkPermissionBtn   = (RelativeLayout) v.findViewById(R.id.checkPermissionBtn);

        rightMenuBtn         = (RelativeLayout) v.findViewById(R.id.rightMenuBtn);
        settingsMainLay      = (RelativeLayout)v.findViewById(R.id.settingsMainLay);
        actionbarMainLay     = (RelativeLayout)v.findViewById(R.id.actionbarMainLay);

        settingsParentView   = (LinearLayout)v.findViewById(R.id.settingsParentView);
        canCycleLayout      = (LinearLayout)v.findViewById(R.id.canCycleLayout);
        usaCycleLayout      = (LinearLayout)v.findViewById(R.id.usaCycleLayout);
        timeZoneLayout      = (LinearLayout)v.findViewById(R.id.timeZoneLayout);


        downloadProgressBar  = (CircularProgressBar) v.findViewById(R.id.downloadProgressBar);

        deferralSwitchButton   = (SwitchCompat)v.findViewById(R.id.deferralSwitchButton);
        haulExceptnSwitchButton = (SwitchCompat)v.findViewById(R.id.haulExceptnSwitchButton);
        adverseSwitchButton = (SwitchCompat)v.findViewById(R.id.adverseSwitchButton);
        adverseCanadaSwitchButton  = (SwitchCompat) v.findViewById(R.id.adverseCanadaSwitchButton);
        agricultureSwitchButton  = (SwitchCompat) v.findViewById(R.id.agricultureSwitchButton);
        loggingSwitchButton     =  (SwitchCompat) v.findViewById(R.id.loggingSwitchButton);

        dayNightSwitchButton  = (SwitchCompat) v.findViewById(R.id.dayNightSwitchButton);

        showBrightnessSeekBar = (SeekBar)v.findViewById(R.id.sbBrightness);
        showVolumeSeekBar     = (SeekBar)v.findViewById(R.id.sbVolume);
        brightnessCardView    = (CardView)v.findViewById(R.id.brightnessCardView);

        rightMenuBtn.setVisibility(View.GONE);
        settingSpinImgVw.setImageResource(R.drawable.sync_data_spin);
        downloadBlinkIV.setImageResource(R.drawable.update_icon);

        dateActionBarTV.setVisibility(View.VISIBLE);
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.whiteee));

        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            settingsMainLay.setBackgroundColor(getResources().getColor(R.color.gray_background) );
           // dayNightSwitchButton.setStyle();
        }

        fadeViewAnim    = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        fadeViewAnim.setDuration(1500);

        DeviceId        = SharedPref.GetSavedSystemToken(getActivity());
        DriverId        = SharedPref.getDriverId( getActivity());
        CompanyId       = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());



        if (global.isConnected(getActivity())) {
            getCycleChangeApproval(DriverId, DeviceId, CompanyId);
            GetDriverLogPermission(DriverId);

        }


        deferralSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {
                    if(CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS) ){
                        global.EldScreenToast(SyncDataBtn, getString(R.string.excp_usa_cycle_check), getResources().getColor(R.color.colorSleeper));
                        buttonView.setChecked(false);
                    }else {
                        if (isChecked) {
                            List<DriverLog> oDriverLogDetail = hMethods.getSavedLogList(Integer.valueOf(DriverId),
                                    global.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getActivity()),
                                    global.GetCurrentUTCDateTime(), dbHelper);

                            DriverDetail oDriverDetail = hMethods.getDriverList(
                                    global.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getActivity()),
                                    global.GetCurrentUTCDateTime(), Integer.valueOf(DriverId),
                                    (int) global.GetDriverTimeZoneOffSet(getActivity()), Integer.valueOf(CurrentCycleId),
                                    global.isSingleDriver(getActivity()),
                                    Integer.valueOf(SharedPref.getDriverStatusId(getActivity())), false,
                                    isHaulExcptn, isAdverseExcptn, isNorthCanada,
                                    SharedPref.GetRulesVersion(getActivity()), oDriverLogDetail, getActivity());

                            if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2) ) {
                                oDriverDetail.setCanAdverseException(isAdverseExcptn);
                            }

                            RulesResponseObject deferralObj = localCalls.IsEligibleDeferralRule(oDriverDetail);

                            if (deferralObj.isDeferralEligible()) {

                                leftOffOrSleeperMin = (int) deferralObj.getLeftOffOrSleeperMinutes();

                                try {
                                    if (deferralDialog != null && deferralDialog.isShowing())
                                        deferralDialog.dismiss();

                                    deferralDialog = new DeferralDialog(getActivity(), leftOffOrSleeperMin, 1, new DeferralListener());
                                    deferralDialog.show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (getActivity() != null && !constants.isObdConnectedWithELD(getActivity())) {
                                    global.InternetErrorDialog(getActivity(), true, true);
                                }

                            } else {
                                buttonView.setChecked(false);
                                global.EldScreenToast(SyncDataBtn, getString(R.string.notEligForDef), getResources().getColor(R.color.colorVoilation));
                            }
                        } else {
                            buttonView.setChecked(true);
                            global.EldScreenToast(SyncDataBtn, getString(R.string.defWillAutoDis), getResources().getColor(R.color.warning));

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
                }else{
                    getExceptionStatus();
                    deferralSwitchButton.setChecked(isDeferral);
                }
            }
        });


        haulExceptnSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {

                    if(CurrentCycleId.equals(global.CANADA_CYCLE_1) || CurrentCycleId.equals(global.CANADA_CYCLE_2) ){
                        global.EldScreenToast(SyncDataBtn, getString(R.string.excp_canada_cycle_check), getResources().getColor(R.color.colorSleeper));
                        buttonView.setChecked(false);
                    }else {

                        if (isChecked) {
                            getExceptionStatus();

                            if (!isAdverseExcptn) {
                                if (isAllowToEnableException(DriverId)) {
                                    haulExceptionAlert();
                                } else {
                                    buttonView.setChecked(false);
                                    global.EldScreenToast(SyncDataBtn, exceptionDesc, getResources().getColor(R.color.colorSleeper));
                                }

                            } else {
                                buttonView.setChecked(false);
                                global.EldScreenToast(SyncDataBtn, getString(R.string.already_enable_excp), getResources().getColor(R.color.colorSleeper));
                            }
                        } else {
                            global.EldScreenToast(SyncDataBtn, getString(R.string.haul_excp_reset_auto), getResources().getColor(R.color.colorSleeper));
                            buttonView.setChecked(true);
                        }
                    }
                }
            }
        });

        adverseSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {
                    if(CurrentCycleId.equals(global.CANADA_CYCLE_1) || CurrentCycleId.equals(global.CANADA_CYCLE_2) ){
                        global.EldScreenToast(SyncDataBtn, getString(R.string.excp_canada_cycle_check), getResources().getColor(R.color.colorSleeper));
                        buttonView.setChecked(false);
                    }else {
                        if (isChecked) {

                            getExceptionStatus();
                            if (!isHaulExcptn) {

                                if (isAllowToEnableException(DriverId)) {
                                    try {
                                        if (adverseRemarksDialog != null && adverseRemarksDialog.isShowing())
                                            adverseRemarksDialog.dismiss();

                                        adverseRemarksDialog = new AdverseRemarksDialog(getActivity(), true,
                                                false, false,false,false,false,false,null,null, new RemarksListener());
                                        adverseRemarksDialog.show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    buttonView.setChecked(false);
                                    global.EldScreenToast(SyncDataBtn, exceptionDesc, getResources().getColor(R.color.colorSleeper));
                                }

                            } else {
                                buttonView.setChecked(false);
                                global.EldScreenToast(SyncDataBtn, getString(R.string.already_enable_excp), getResources().getColor(R.color.colorSleeper));
                            }
                        } else {
                            global.EldScreenToast(SyncDataBtn, getString(R.string.excp_reset_auto), getResources().getColor(R.color.colorSleeper));
                            buttonView.setChecked(true);
                        }
                    }
                }
            }
        });

        adverseCanadaSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {
                    if(CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS) ){
                        global.EldScreenToast(SyncDataBtn, getString(R.string.excp_usa_cycle_check), getResources().getColor(R.color.colorSleeper));
                        buttonView.setChecked(false);
                    }else {
                        if (isChecked) {

//                            getExceptionStatus();
                            if (!isHaulExcptn) {

                                if (isAllowToEnableExceptionn(DriverId, true)) {
                                    try {
                                        if (adverseRemarksDialog != null && adverseRemarksDialog.isShowing())
                                            adverseRemarksDialog.dismiss();

                                        adverseRemarksDialog = new AdverseRemarksDialog(getActivity(), true,
                                                false, false,false,false,false,false,null,null, new RemarksListener());
                                        adverseRemarksDialog.show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    buttonView.setChecked(false);
                                    global.EldScreenToast(SyncDataBtn, exceptionDesc, getResources().getColor(R.color.colorSleeper));
                                }

                            } else {
                                buttonView.setChecked(false);
                                global.EldScreenToast(SyncDataBtn, getString(R.string.already_enable_excp), getResources().getColor(R.color.colorSleeper));
                            }
                        } else {
                            global.EldScreenToast(SyncDataBtn, getString(R.string.excp_reset_auto), getResources().getColor(R.color.colorSleeper));
                            buttonView.setChecked(true);
                        }
                    }
                }
            }
        });




        agricultureSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {

                    if (isChecked) {

                      /*  if(CurrentCycleId.equals(global.CANADA_CYCLE_1) || CurrentCycleId.equals(global.CANADA_CYCLE_2) ){
                            global.EldScreenToast(SyncDataBtn, getString(R.string.excp_canada_cycle_check), getResources().getColor(R.color.colorSleeper));
                            buttonView.setChecked(false);
                        }else {*/
                            boolean isVehicleMoving = SharedPref.isVehicleMoving(getContext());
                            getExceptionStatus();

                            if (!isAgricultureExcptn) {
                                if (isAllowToEnableExceptionn(DriverId, false) && !isVehicleMoving) {
                                    // get current location address
                                    GetAddFromLatLng();

                                    OpenAddressDialog();
                                } else {
                                    buttonView.setChecked(false);
                                    global.EldScreenToast(SyncDataBtn, exceptionDesc, getResources().getColor(R.color.colorSleeper));
                                }
                            } else {
                                buttonView.setChecked(false);
                                global.EldScreenToast(SyncDataBtn, getString(R.string.already_enable_excp), getResources().getColor(R.color.colorSleeper));
                            }
                       // }

                    }else{
                        buttonView.setChecked(isChecked);
                        IsAgriExceptionEnable = false;

                        SaveAgricultureRecord( Globally.GetCurrentUTCTimeFormat(), SharedPref.getTruckNumber(getActivity()),
                                DriverId,CompanyId,
                                SharedPref.getAgricultureRecord("AgricultureAddress", getContext()),SharedPref.getAgricultureRecord("AgricultureLatitude", getContext()),SharedPref.getAgricultureRecord("AgricultureLongitude", getContext()),SharedPref.getObdEngineHours(getContext()),SharedPref.getObdOdometer(getContext()),"0");
                    }

                }
            }
        });


        loggingSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {

                    String CurrentCycleId      = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());
                    if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {

                        if (isChecked) {

                            boolean isVehicleMoving = SharedPref.isVehicleMoving(getContext());
                            getExceptionStatus();


                        }else{

                        }

                    }else{
                        buttonView.setChecked(false);
                        global.EldScreenToast(SyncDataBtn, getString(R.string.excp_usa_cycle_check), getResources().getColor(R.color.colorSleeper));
                    }




                }
            }
        });



        dayNightSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {

                    if (isChecked) {

                        if(!isDayNightMode){
                            UILApplication.getInstance().setIsNightModeEnabled(true);
                            getActivity().finish();
                            Intent intent = new Intent(getActivity(), TabAct.class);
                            startActivity(intent);
                            SharedPref.setDayNightMode(true, getActivity());
                            SharedPref.setDayNightActionClick(true, getActivity());

                        } else {
                            buttonView.setChecked(isChecked);
                            UILApplication.getInstance().setIsNightModeEnabled(false);
                            getActivity().finish();
                            Intent intent = new Intent(getActivity(), TabAct.class);
                            startActivity(intent);
                            SharedPref.setDayNightMode(false, getActivity());
                            SharedPref.setDayNightActionClick(true, getActivity());
                        }
                    }else{
                        buttonView.setChecked(isChecked);
                        UILApplication.getInstance().setIsNightModeEnabled(false);
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), TabAct.class);
                        startActivity(intent);
                        SharedPref.setDayNightMode(false, getActivity());
                        SharedPref.setDayNightActionClick(true, getActivity());
                    }

                }
            }
        });



            fadeViewAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(IsDownloading) {
                    checkAppUpdateTV.startAnimation(fadeViewAnim);
                    downloadHintImgView.startAnimation(fadeViewAnim);
                   // downloadBlinkIV.startAnimation();
                }else {
                    fadeViewAnim.cancel();
                    downloadBlinkIV.stopAnimation();
                    updateBlinkLayout.setVisibility(View.GONE);
                  //  downloadHintImgView.setVisibility(View.GONE);
                  //  updateAppDownloadIV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

       // adverseCanadaSwitchButton.setEnabled(false);
       // deferralSwitchButton.setEnabled(false);


        eldMenuLay.setOnClickListener(this);
        SettingSaveBtn.setOnClickListener(this);
        SyncDataBtn.setOnClickListener(this);
        checkInternetBtn.setOnClickListener(this);
        checkAppUpdateBtn.setOnClickListener(this);
        dateActionBarTV.setOnClickListener(this);
        obdDiagnoseBtn.setOnClickListener(this);
        docBtn.setOnClickListener(this);
        haulExceptionLay.setOnClickListener(this);
        brightnessSoundEditBtn.setOnClickListener(this);
        settingsParentView.setOnClickListener(this);
        actionbarMainLay.setOnClickListener(this);
        canCycleLayout.setOnClickListener(this);
        usaCycleLayout.setOnClickListener(this);
        timeZoneLayout.setOnClickListener(this);
        checkPermissionBtn.setOnClickListener(this);

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

        getStorageRecordOnView();
        getExceptionStatus();
        haulExceptnSwitchButton.setChecked(isHaulExcptn);
        deferralSwitchButton.setChecked(isDeferral);
        agricultureSwitchButton.setChecked(isAgricultureExcptn);
        dayNightSwitchButton.setChecked(isDayNightMode);

        int ColorGrayBackground = getResources().getColor(R.color.gray_background_one);
        int ColorGrayCategory = getResources().getColor(R.color.gray_category_color);

        if(UILApplication.getInstance().isNightModeEnabled()){
            ColorGrayCategory =  getResources().getColor(R.color.white);
            ColorGrayBackground = getResources().getColor(R.color.gray_category_color);
        }

        if(CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS) ){
            adverseCanadaExpTxtView.setTextColor(ColorGrayBackground);
            deferralTxtView.setTextColor(ColorGrayBackground);
            loggingTxtView.setTextColor(ColorGrayBackground);

            haulExpTxtView.setTextColor(ColorGrayCategory);
            adverseExpTxtView.setTextColor(ColorGrayCategory);
            agricultureExpTxtView.setTextColor(ColorGrayCategory);


            adverseCanadaSwitchButton.setChecked(false);

            adverseSwitchButton.setChecked(isAdverseExcptn);
        }else {

            haulExpTxtView.setTextColor(ColorGrayBackground);
            adverseExpTxtView.setTextColor(ColorGrayBackground);
            adverseCanadaExpTxtView.setTextColor(ColorGrayCategory);
            deferralTxtView.setTextColor(ColorGrayCategory);
            agricultureExpTxtView.setTextColor(ColorGrayBackground);
            loggingTxtView.setTextColor(ColorGrayCategory);

            adverseSwitchButton.setChecked(false);
            adverseCanadaSwitchButton.setChecked(isAdverseExcptn);

        }



        if(constants.isLocMalfunctionEvent(getActivity(), DriverType) && SharedPref.getLocationEventType( getContext()).equals("X")){
            LocationType = "M";
        }

        try{
            JSONObject logPermissionObj    = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DriverId), CoDriverId, dbHelper);


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


        if (!SharedPref.getDriverType(getActivity()).equals(DriverConst.SingleDriver)) {
            if(DriverId.equals(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()))){
                CoDriverId   = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
                CoDriverName = DriverConst.GetCoDriverDetails(DriverConst.CoDriverName, getActivity());
            }else{
                CoDriverId   = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
                CoDriverName = DriverConst.GetDriverDetails(DriverConst.DriverName, getActivity());
            }
        }


        DisplayCanCycles();
        DisplayUsaCycles();
        DisplayTimeZones();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver( progressReceiver, new IntentFilter(ConstantsKeys.DownloadProgress));

        if (global.isConnected(getActivity())) {
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
            isDeferral      = SharedPref.isDeferralMainDriver(getActivity());
            isExemptDriver  = SharedPref.IsExemptDriverMain(getActivity());
        }else{
            isHaulExcptn    = SharedPref.get16hrHaulExcptnCo(getActivity());
            isAdverseExcptn = SharedPref.getAdverseExcptnCo(getActivity());
            isDeferral      = SharedPref.isDeferralCoDriver(getActivity());
            isExemptDriver  = SharedPref.IsExemptDriverCo(getActivity());
        }

        isAgricultureExcptn =   SharedPref.getAgricultureExemption(getActivity());
        isDayNightMode =   SharedPref.getDayNightMode(getActivity());
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
                            Globally.LONGITUDE, DriverTimeZone, TruckNumber, global.GetCurrentDeviceDateDefault(global, getActivity()));
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




    private void saveMissingDiagnostic(String remarks, String type){
        try {
            boolean IsAllowMissingDataDiagnostic = SharedPref.GetOtherMalDiaStatus(ConstantsKeys.MissingDataDiag, getActivity());
            String RPM = SharedPref.getRPM(getActivity());

            if((RPM.equals("0") || !constants.isObdConnectedWithELD(getActivity()) ) &&
                    IsAllowMissingDataDiagnostic && !isExemptDriver) {

             //   boolean isMissingEventAlreadyWithStatus = malfunctionDiagnosticMethod.isMissingEventAlreadyWithOtherJobs(type, dbHelper);

                String desc = "";
                if(RPM.equals("0")){
                    remarks = "Vehicle ignition is off at ";
                    desc = " due to Vehicle ignition is off.";
                }else {
                    desc = " due to OBD not connected with E-Log Book";
                }

           //     if (!isMissingEventAlreadyWithStatus) {
                    // save malfunction occur event to server with few inputs
                    JSONObject newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                            DriverId, DeviceId, SharedPref.getVINNumber(getActivity()),
                            SharedPref.getTruckNumber(getActivity()),   //DriverConst.GetDriverTripDetails(DriverConst.Truck, getActivity()),
                            DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity()),
                            constants.get2DecimalEngHour(getActivity()), //SharedPref.getObdEngineHours(getActivity()),
                            SharedPref.getObdOdometer(getActivity()),
                            SharedPref.getObdOdometer(getActivity()),
                            Globally.GetCurrentUTCTimeFormat(), constants.MissingDataDiagnostic,
                            remarks + " " + type, false,
                            "", "", "",
                            "", type);  //Constants.getLocationType(getActivity())

                    // save Occurred event locally until not posted to server
                    JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
                    malArray.put(newOccuredEventObj);
                    malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, malArray);

                    // save malfunction entry in duration table
                    malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DriverId,
                            Globally.GetCurrentUTCTimeFormat(), Globally.GetCurrentUTCTimeFormat(),
                            Constants.MissingDataDiagnostic, type, "",  //Constants.getLocationType(getActivity())
                            type, constants, getActivity());

                    SharedPref.saveMissingDiaStatus(true, getActivity());

                    Globally.PlayNotificationSound(getActivity());
                    Globally.ShowLocalNotification(getActivity(),
                            getString(R.string.missing_dia_event),
                            getString(R.string.missing_event_occured_desc) + " in " +
                                    type + desc, 2091);


                    SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getActivity()),
                            SharedPref.isMalfunctionOccur(getActivity()), true,
                            SharedPref.isSuggestedEditOccur(getActivity()), getActivity());

             //   }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static String getSize(long size) {
        String s = "";
        double kb = size / 1024;
        double mb = kb / 1024;
        double gb = mb / 1024;
        double tb = gb / 1024;
        if(size < 1024L) {
            s = size + " Bytes";
        } else if(size >= 1024 && size < (1024L * 1024)) {
            s =  String.format("%.2f", kb) + " KB";
        } else if(size >= (1024L * 1024) && size < (1024L * 1024 * 1024)) {
            s = String.format("%.2f", mb) + " MB";
        } else if(size >= (1024L * 1024 * 1024) && size < (1024L * 1024 * 1024 * 1024)) {
            s = String.format("%.2f", gb) + " GB";
        } else if(size >= (1024L * 1024 * 1024 * 1024)) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getStorageRecordOnView(){
        File var10000 = Environment.getDataDirectory();
        Intrinsics.checkNotNullExpressionValue(var10000, "Environment.getDataDirectory()");
        File iPath = var10000;
        StatFs iStat = new StatFs(iPath.getPath());
        long iBlockSize = iStat.getBlockSizeLong();
        long iAvailableBlocks = iStat.getAvailableBlocksLong();
        long iTotalBlocks = iStat.getBlockCountLong();
        Integer iAvailableSpace = Integer.valueOf(constants.formatSize(iAvailableBlocks * iBlockSize).replaceAll("[^0-9]",""));
        Integer iTotalSpace = Integer.valueOf(constants.formatSize(iTotalBlocks * iBlockSize).replaceAll("[^0-9]",""));
        String AvailableSpace = this.getSize(iAvailableBlocks * iBlockSize);
        String TotalSpace = this.getSize(iTotalBlocks * iBlockSize);
        Intrinsics.checkNotNullExpressionValue(storageTextView, "mTextView");
        storageTextView.setText((CharSequence)("Storage (" + AvailableSpace + " free of " + TotalSpace + ")"));
        storageProgress.setMax(iTotalSpace);
        storageProgress.setProgress(iTotalSpace - iAvailableSpace);

        if(UILApplication.getInstance().isNightModeEnabled()){
            if(iAvailableSpace>=1500){
                storageProgress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.black_unidenfied)));
            }else {
                storageProgress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.slide_menu_default)));
            }
        }else{
            if(iAvailableSpace>=1500){
                storageProgress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_storage1500)));
            }else if(iAvailableSpace>=500 && iAvailableSpace < 1500){
                storageProgress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_storage500)));
            }else {
                storageProgress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_storage0)));
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
                Logger.LogVerbose("TAG","Permission is granted");
                SyncData();

                return true;
            } else {
                Logger.LogVerbose("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(((Activity)getContext()), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Logger.LogVerbose("TAG","Permission is granted");
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
                  //  Logger.LogVerbose("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
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
           // ClearDriverUnSavedlog();
            global.EldScreenToast(SyncDataBtn, "No data available for syncing.", getResources().getColor(R.color.colorVoilation));
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
                brightnessCardView.setVisibility(View.GONE);
                if(brightnessCardView.getVisibility() == View.VISIBLE) {
                    brightnessCardView.setVisibility(View.GONE);
                }

                TabAct.sliderLay.performClick();

                break;

            case R.id.dateActionBarTV:
               // TabAct.host.setCurrentTab(0);
                brightnessCardView.setVisibility(View.GONE);
                if(brightnessCardView.getVisibility() == View.VISIBLE) {
                    brightnessCardView.setVisibility(View.GONE);
                }

                ObdDataInfoDialog dialog = new ObdDataInfoDialog(getActivity(), DriverId );
                dialog.show();

                break;



            case R.id.SyncDataBtn:
                brightnessCardView.setVisibility(View.GONE);
                // isStoragePermissionGranted();
                SyncData();

                break;

            case R.id.checkInternetBtn:
                brightnessCardView.setVisibility(View.GONE);
                progressDialog.show();
                //CheckConnectivity CheckConnectivity = new CheckConnectivity(getActivity());
                checkConnectivity.ConnectivityRequest(CheckInternetConnection, ConnectivityInterface);

                break;


            case R.id.checkAppUpdateBtn:
                brightnessCardView.setVisibility(View.GONE);
                if(IsDownloading){

                    if (confirmationDialog != null && confirmationDialog.isShowing())
                        confirmationDialog.dismiss();
                    confirmationDialog = new ConfirmationDialog(getActivity(), Constants.AlertSettings, new ConfirmListener());
                    confirmationDialog.show();

                }else {

                    getInstalledAppDetail();
                    File existingFile = new File(global.getAlsApkPath() + "/" + getExistingApkPath());
                    if (!existingFile.isFile()) {
                        checkAppUpdateTV.setText(getResources().getString(R.string.Update_Status));
                    }

                    if (checkAppUpdateTV.getText().toString().equals("Install Updates") ) {
                        openFileLocation("");
                        //global.EldScreenToast(SyncDataBtn, "Update app from Downloads/EldApp folder.", getResources().getColor(R.color.colorPrimary));
                    } else {

                        if (ExistingApkVersionCode.equals(VersionCode) && ExistingApkVersionName.equals(VersionName)) {
                            global.EldScreenToast(SyncDataBtn, "Your application is up to date", UILApplication.getInstance().getThemeColor());
                        } else {
                            String updateTvText = checkAppUpdateTV.getText().toString();
                            if (updateTvText.equals(getResources().getString(R.string.install_updates))) {
                                if (ApkFilePath.length() > 0) {
                                    // InstallApp(ApkFilePath);
                                    openFileLocation(ApkFilePath);
                                } else {
                                    checkAppUpdateTV.setText(getResources().getString(R.string.Update_Status));
                                    global.EldScreenToast(SyncDataBtn, "File not found", getResources().getColor(R.color.colorVoilation));
                                }
                            } else {
                                connectivityTask.ConnectivityRequest(CheckUpdate, ConnectivityInterface);
                            }
                        }
                    }
                }


                break;



            case R.id.obdDiagnoseBtn:
                brightnessCardView.setVisibility(View.GONE);
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
                brightnessCardView.setVisibility(View.GONE);
                //  ObdConfigFragment wiredObdFragment = new ObdConfigFragment();
                //  MoveFragment(wiredObdFragment);
                break;

            case R.id.docBtn:
                brightnessCardView.setVisibility(View.GONE);
                DocumentFragment helpFragment = new DocumentFragment();
                MoveFragment(helpFragment);
                break;


            case R.id.canCycleLayout:
                brightnessCardView.setVisibility(View.GONE);
                if (CurrentCycleId.equals(global.CANADA_CYCLE_1) || CurrentCycleId.equals(global.CANADA_CYCLE_2)) {

                    //if(constants.isActionAllowed(getActivity())) {
                    if(hMethods.isActionAllowedWhileMoving(getActivity(), new Globally(), DriverId, dbHelper)){
                        if(isSleepOffDuty()) {
                            changeCycleZoneDialog("can_cycle", SavedCanCycle, "");
                        }else{
                            global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.cycle_change_check), UILApplication.getInstance().getThemeColor());
                        }
                    }else{
                        Globally.EldScreenToast(caCycleTV, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                        getString(R.string.stop_vehicle_alert),
                                getResources().getColor(R.color.colorVoilation));
                    }

                }



                break;


            case R.id.usaCycleLayout:
                brightnessCardView.setVisibility(View.GONE);
                    if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS)) {

                       // if(constants.isActionAllowed(getActivity())) {
                        if(hMethods.isActionAllowedWhileMoving(getActivity(), new Globally(), DriverId, dbHelper)){
                            if(isSleepOffDuty()) {
                                changeCycleZoneDialog("us_cycle", SavedUsaCycle, "");
                            }else{
                                global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.cycle_change_check), UILApplication.getInstance().getThemeColor());
                            }
                        }else{
                            Globally.EldScreenToast(caCycleTV, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                            getString(R.string.stop_vehicle_alert),
                                    getResources().getColor(R.color.colorVoilation));
                        }

                    }


                break;


            case R.id.soundbrightnessBtn:

                setSoundSettings();
                setBrightnessSettings();

                break;

            case R.id.settingsParentView:
                brightnessCardView.setVisibility(View.GONE);
                break;

            case R.id.actionbarMainLay:
                brightnessCardView.setVisibility(View.GONE);
                break;



            case R.id.timeZoneLayout:
                brightnessCardView.setVisibility(View.GONE);
                    if (CurrentCycleId.equals(global.CANADA_CYCLE_1) || CurrentCycleId.equals(global.CANADA_CYCLE_2)) {
                        //if(constants.isActionAllowed(getActivity())) {
                        if(hMethods.isActionAllowedWhileMoving(getActivity(), new Globally(), DriverId, dbHelper)){
                            if(isOnDuty()) {
                                changeCycleZoneDialog("operating_zone", CurrentCycleId, operatingZoneTV.getText().toString());
                            }else{
                                global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.op_zone_change_check), UILApplication.getInstance().getThemeColor());
                            }
                        }else{
                            Globally.EldScreenToast(caCycleTV, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                            getString(R.string.stop_vehicle_alert),
                                    getResources().getColor(R.color.colorVoilation));
                        }
                    }

                break;


            case R.id.checkPermissionBtn:

               Intent i = new Intent(getActivity(), AppPermissionActivity.class);
               startActivity(i);

                break;


        }
    }


    private void setBrightnessSettings(){
        if(brightnessCardView.getVisibility() == View.VISIBLE) {
            brightnessCardView.setVisibility(View.GONE);
        }else{
            brightnessCardView.setVisibility(View.VISIBLE);
        }
        cResolver = getActivity().getContentResolver();

        //Get the current window
        window =  getActivity().getWindow();

        //Set the seekbar range between 0 and 255
        showBrightnessSeekBar.setMax(255);
        //Set the seek bar progress to 1
        showBrightnessSeekBar.setKeyProgressIncrement(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.System.canWrite(getActivity())){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                startActivity(intent);
            }
        }

        try
        {
            //Get the current system brightness
            brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
            brightnessMode = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
//                    Settings.System.putInt(cResolver, "SCREEN_BRIGHTNESS_MODE", Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
        catch (Settings.SettingNotFoundException e)
        {
            //Throw an error case it couldn't be retrieved
            Logger.LogError("Error", "Cannot access system brightness");
            e.printStackTrace();
        }

        showBrightnessSeekBar.setProgress(brightness);

        showBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {

                boolean settingsCanWrite = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    settingsCanWrite = Settings.System.canWrite(getActivity());
                }

                if(settingsCanWrite) {
                    //Set the system brightness using the brightness variable value
                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
                    //Get the current window attributes
                    WindowManager.LayoutParams layoutpars = window.getAttributes();
                    //Set the brightness of this window
                    layoutpars.screenBrightness = brightness / (float) 255;
                    //Apply attribute changes to this window
                    window.setAttributes(layoutpars);

                    /////    1 Means Auto brightness on and 0 Means Auto brightness off . In Auto on seek bar setting not work //////////

                    if (brightnessMode == 1) {
                        Globally.showToast(rootView, "Please turn off auto brightness for this setting");
                    }
                }else{
                    global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.no_per_bright_settings), getResources().getColor(R.color.colorVoilation));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing handled here
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Set the minimal brightness level
                //if seek bar is 20 or any value below
                if (progress <= 20) {
                    //Set the brightness to 20
                    brightness = 20;
                } else //brightness is greater than 20
                {
                    //Set brightness variable based on the progress bar
                    brightness = progress;
                }
                //Calculate the brightness percentage
                float perc = (brightness / (float) 255) * 100;

            }

        });

    }


    private void setSoundSettings(){
        try {

            audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            //set max progress according to volume
            showVolumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            //get current volume
            showVolumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            //Set the seek bar progress to 1
            showVolumeSeekBar.setKeyProgressIncrement(1);
            //get max volume
            maxVolume = showVolumeSeekBar.getMax();
            showVolumeSeekBar.setMax(maxVolume);


            showVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Logger.LogError("Volume:", "ponStopTrackingTouch ");
                    // seekBar.setProgress(volume[0]);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    // volume[0] = progress;

//                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.ADJUST_SAME);
                    if(volume[0] < progress) {
                        Logger.LogError("Volume:", "progress raise: " + progress);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);

                    }else{
                        Logger.LogError("Volume:", "progress lower: " + progress);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                    }

                    if(progress == 0){
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND);
                    }else if(progress == maxVolume){
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                    }

                    volume[0] = progress;

                }
            });

            if(volume[0] != audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)){
                //volume changed put logic here
                Logger.LogError("Volume","dfsdf");
            }

        } catch (Exception e) {
            e.printStackTrace();
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



    private boolean isOnDuty(){
        String currentJob     = SharedPref.getDriverStatusId(getActivity());
        if(currentJob.equals(global.ON_DUTY)){
            return true;
        }else{
            return false;
        }
    }

    private class DeferralListener implements DeferralDialog.DeferralListener{

        @Override
        public void JobBtnReady(int time, int deferralDays) {

            if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                SharedPref.setDeferralForMain(true, Globally.GetDriverCurrentDateTime(global, getActivity()), "1", getActivity());
            }else{
                SharedPref.setDeferralForCo(true, Globally.GetDriverCurrentDateTime(global, getActivity()), "1", getActivity());
            }

            global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.Deferralenabled), UILApplication.getInstance().getThemeColor());

            JSONArray savedDeferralArray = deferralMethod.getSavedDeferralArray(Integer.valueOf(DriverId), dbHelper);
            JSONObject deferralObj = deferralMethod.GetDeferralJson(DriverId, DeviceId, TruckNumber, CompanyId,
                    Globally.LATITUDE, Globally.LONGITUDE, constants.get2DecimalEngHour(getActivity()),  //SharedPref.getObdEngineHours(getActivity()),
                    SharedPref.getObdOdometer(getActivity()),
                    ""+leftOffOrSleeperMin, "1");
            savedDeferralArray.put(deferralObj);

            // save deferral event inn local db and push automatically later. Reason behind this to save in offline also when internet is not working.
            deferralMethod.DeferralLogHelper(Integer.valueOf(DriverId), dbHelper, savedDeferralArray);

            saveMissingDiagnostic(getString(R.string.obd_data_is_missing), "OffDuty Deferral");

            SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());
            constant.startService(getActivity());

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
            adverseCanadaSwitchButton.setChecked(false);

            try {
                if (adverseRemarksDialog != null && adverseRemarksDialog.isShowing())
                    adverseRemarksDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void JobBtnReady(String AdverseExceptionRemarks, boolean IsClaim, boolean IsCompanyAssign,String startOdo,
                                String endOdo,String startLoc,String endLoc,String StartCity,String StartState,String StartCountry,
                                String EndCity,String EndState,String EndCountry,boolean startOdometer, boolean endOdometer,
                                boolean startLocation, boolean endLocation) {
            if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                SharedPref.setAdverseExcptn(true, getActivity());
            }else{
                SharedPref.setAdverseExcptnCo(true, getActivity());
            }

            getExceptionStatus();

            global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.advrs_excptn_enabled), UILApplication.getInstance().getThemeColor());

            try {
                if (adverseRemarksDialog != null && adverseRemarksDialog.isShowing())
                    adverseRemarksDialog.dismiss();

                hMethods.SaveDriversJob(DriverId, DeviceId, AdverseExceptionRemarks, getString(R.string.enable_adverse_exception),
                        LocationType, "", false, isNorthCanada, DriverType, constants,
                        MainDriverPref, CoDriverPref, syncingMethod, global, hMethods, dbHelper, getActivity(),
                        false, CoDriverId, CoDriverName, false, Constants.Auto) ;

            }catch (Exception e){
                e.printStackTrace();
            }

          /*  try {
                // creating log when haul exception is not enabled to check log array
                int currentStatus = Integer.valueOf(SharedPref.getDriverStatusId(getActivity()));
                constants.writeViolationFile(global.getDateTimeObj(global.GetDriverCurrentDateTime(Global, getActivity()), false),
                        global.GetCurrentUTCDateTime(),
                        Integer.valueOf(DriverId), CurrentCycleId,
                        (int) global.GetDriverTimeZoneOffSet(),
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

            updateBlinkLayout.setVisibility(View.GONE);
           // downloadHintImgView.setVisibility(View.GONE);
          //  updateAppDownloadIV.setVisibility(View.GONE);

            checkAppUpdateTV.setText(getResources().getString(R.string.Update_Status));

            confirmationDialog.dismiss();
        }
    }

    CheckConnectivity.ConnectivityInterface ConnectivityInterface = new CheckConnectivity.ConnectivityInterface() {
        @Override
        public void IsConnected(boolean result, int flag) {
            Logger.LogDebug("networkUtil", "result: " +result );

            if (result) {
                if(flag == CheckInternetConnection) {
                    if(progressDialog != null)
                        progressDialog.dismiss();
                    Constants.IsAlsServerResponding = true;
                    global.EldScreenToast(SyncDataBtn, "Internet Connected.", UILApplication.getInstance().getThemeColor());
                }else if(flag == CheckUpdate){

                    if(IsManualAppDownload){
                        GetAppDetails(APIs.GET_MANNUAL_APK_DETAIL);
                    }else{
                        if(ApkFilePath.length() == 0) {
                            GetAppDetails(APIs.GET_ANDROID_APP_DETAIL);
                        }else{
                            CheckAppStatus();
                        }
                    }

                }else{


                        DriverLogFile = global.GetViolationFile(getActivity(), ConstantsKeys.ViolationTest, "txt");

                        // use only testing time with DEV domain
                       // postDriverLogForTestingInDev();

                        // Save Cycle record in file on local storage
                    if(IsLogPermission) {
                        try {
                            JSONArray cycleArray = new JSONArray(SharedPref.GetCycleDetails(getActivity()));
                            if(cycleArray.length() > 0) {
                                cycleUpdationRecordFile = global.SaveFileInSDCard("Cycle_", cycleArray.toString(), false, getActivity());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    if ((syncingFile != null && syncingFile.exists()) || (DriverLogFile != null && DriverLogFile.exists())) {
                        // Sync driver log API data to server with SAVE_LOG_TEXT_FILE (SAVE sync data service)
                        // set this variable always true to get violation file to check wrong violtaion issue
                        IsLogPermission = true;
                        SyncDataUpload syncDataUpload = new SyncDataUpload(getActivity(), DriverId, syncingFile, DriverLogFile,
                                cycleUpdationRecordFile, IsLogPermission, asyncResponse );
                        syncDataUpload.execute();
                    }else{
                        settingSpinImgVw.stopAnimation();
                        global.EldScreenToast(SyncDataBtn, "No data available for syncing.", getResources().getColor(R.color.colorVoilation));
                    }


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

    // use only testing time with DEV domain
    void postDriverLogForTestingInDev(){
        try {
            if (APIs.DOMAIN_URL_ALS.contains("dev.alsrealtime.com")) {
                if (DriverLogFile == null || !DriverLogFile.exists()) {
                    IsLogPermission = true;
                    JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
                    Globally.SaveFileInSDCard(ConstantsKeys.ViolationTest, driverLogArray.toString(), true, getActivity());
                    DriverLogFile = global.GetViolationFile(getActivity(), ConstantsKeys.ViolationTest, "txt");

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean IsAgriExceptionChanged = intent.getBooleanExtra(ConstantsKeys.IsAgriException, false);
            if(IsAgriExceptionChanged){
                getExceptionStatus();
                agricultureSwitchButton.setChecked(isAgricultureExcptn);

            }else {
                long percentage = intent.getIntExtra(ConstantsKeys.Percentage, 0);
                ApkFilePath = intent.getStringExtra(ConstantsKeys.Path);
                boolean isCompleted = intent.getBooleanExtra(ConstantsKeys.IsCompleted, false);
                boolean IsInterrupted = intent.getBooleanExtra(ConstantsKeys.IsInterrupted, false);

                if (percentage >= progressPercentage) {
                    //  IsDownloading = true;
                    downloadProgressBar.setProgress(percentage);
                    progressPercentage = percentage;
                }

                if (isCompleted || IsInterrupted) {

                    fadeViewAnim.cancel();
                    downloadBlinkIV.stopAnimation();

                    IsDownloading = false;

                    if (ApkFilePath.equals("Downloading failed.") || IsInterrupted) {
                        if (IsInterrupted) {
                            global.EldScreenToast(SyncDataBtn, "Downloading cancelled", getResources().getColor(R.color.colorSleeper));
                        } else {
                            global.EldScreenToast(SyncDataBtn, ApkFilePath, getResources().getColor(R.color.colorSleeper));
                        }
                        ApkFilePath = "";
                        ExistingApkVersionCode = "";
                        ExistingApkVersionName = "";
                        checkAppUpdateTV.setText(getResources().getString(R.string.Update_Status));
                    } else {
                        global.EldScreenToast(SyncDataBtn, "Downloading completed", UILApplication.getInstance().getThemeColor());
                        checkAppUpdateTV.setText(getResources().getString(R.string.install_updates));

                        if (ApkFilePath.length() > 0) {
                            //InstallApp(ApkFilePath);
                            openFileLocation(ApkFilePath);
                        }



                    }


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateBlinkLayout.setVisibility(View.GONE);
                        }
                    }, 500);


                }
            }
        }
    };

    private void openFileLocation(String path){

        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(intent);

        Toast.makeText(getActivity(), "Update app from Downloads/EldApp folder.", Toast.LENGTH_LONG).show();
        //Globally.DriverSwitchAlert(getActivity(), "Eld App !!" , "Update app from Downloads/EldApp folder.", "Ok");
    }


    private void openFolder(String folderPath) {

           /*Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri folderUri = Uri.parse(path);
            intent.setDataAndType(folderUri, "resource/folder");
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "No app found to handle the folder.", Toast.LENGTH_SHORT).show();
            }*/


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(folderPath);
        intent.setDataAndType(uri, "*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivity(Intent.createChooser(intent, "Open folder"));
    }


    String getExistingApkPath(){
        File apkFile = global.getAlsApkPath();
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
                global.EldScreenToast(SyncDataBtn, "Not able to install app directly. You can install it manually in (Logistic/EldApp/) folder.", Color.parseColor("#358A0D"));
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
                String folder      = global.getAlsApkPath().toString();
                global.DeleteDirectory(folder);
                AppInstallAttemp = 0;

                checkAppUpdateBtn.performClick();

            }
        }

    }



    void downloadButtonClicked(String url, String VersionCode, String VersionName, boolean downloadStatus) {

        IsDownloading = true;
        downloadProgressBar.setProgress(0);
        updateBlinkLayout.setVisibility(View.VISIBLE);
        downloadProgressBar.setVisibility(View.VISIBLE);
        downloadHintImgView.setVisibility(View.VISIBLE);
        updateAppDownloadIV.setVisibility(View.GONE);

        checkAppUpdateTV.setText(getResources().getString(R.string.Downloading));
        ApkFilePath = "";
        progressPercentage = 0;
        checkAppUpdateTV.startAnimation(fadeViewAnim);
        downloadHintImgView.startAnimation(fadeViewAnim);
        downloadBlinkIV.startAnimation();

        /*Intent serviceIntent = new Intent(getActivity(), downloadAppService.getClass());
        serviceIntent.putExtra(ConstantsKeys.url, url);
        serviceIntent.putExtra(ConstantsKeys.VersionCode, VersionCode);
        serviceIntent.putExtra(ConstantsKeys.VersionName, VersionName);
        serviceIntent.putExtra(ConstantsKeys.IsDownloading, downloadStatus);
        getActivity().startService(serviceIntent);
*/

        Intent intent = new Intent(getActivity(), AppDownloadService.class);
        intent.setAction(Constants.ACTION_START_DOWNLOAD);
        intent.putExtra(Constants.EXTRA_APK_URL, url);

        intent.putExtra(ConstantsKeys.VersionCode, VersionCode);
        intent.putExtra(ConstantsKeys.VersionName, VersionName);
        intent.putExtra(ConstantsKeys.IsDownloading, downloadStatus);

        getActivity().startService(intent);


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
            MainDriverPref.ClearLogFromList(getActivity());
        }else{
            DriverType = Constants.CO_DRIVER_TYPE;
            CoDriverPref.ClearCoDrLogFromList(getActivity());
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
        GetDriverLogPostPermission.executeRequest(com.android.volley.Request.Method.POST, APIs.DRIVER_VIOLATION_PERMISSION ,
                params, DriverLogPermission,Constants.SocketTimeout10Sec,  ResponseCallBack, ErrorCallBack);

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
        //params.put(ConstantsKeys.Id, Id);
        params.put(ConstantsKeys.Status, Status);
        params.put(ConstantsKeys.CycleId, ChangedCycleId);
        params.put(ConstantsKeys.CurrentCycleId, CurrentCycleId);
        params.put(ConstantsKeys.Latitude, Latitude);
        params.put(ConstantsKeys.Longitude, Longitude);
        params.put(ConstantsKeys.DriverTimeZone, DriverTimeZone);
        params.put(ConstantsKeys.PowerUnitNumber, PowerUnitNumber);
        params.put(ConstantsKeys.LogDate, LogDate);
        params.put(ConstantsKeys.LocationType, SharedPref.getLocationEventType(getActivity()));

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
        params.put(ConstantsKeys.LocationType, SharedPref.getLocationEventType(getActivity()));

        OperatingZoneRequest.executeRequest(com.android.volley.Request.Method.POST, APIs.CHANGE_OPERATING_ZONE , params, OperatingZone,
                Constants.SocketTimeout10Sec,  ResponseCallBack, ErrorCallBack);

    }


    void GetUserLatLong(final String Address){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.SourceAddress, Address);

        AddressLatLongRequest.executeRequest(Request.Method.POST, APIs.GetLatLongFromAddress, params, AddressLatLong,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Address From Lat Lng ===================*//*
    void GetAddFromLatLng() {

        if (global.isConnected(getActivity()) && SourceAddress.length() < 3) {
            params = new HashMap<String, String>();
            params.put(ConstantsKeys.Latitude, Globally.LATITUDE);
            params.put(ConstantsKeys.Longitude, Globally.LONGITUDE);
           // params.put(ConstantsKeys.IsAOBRDAutomatic, "false");

            GetAddFromLatLngRequest.executeRequest(Request.Method.POST, APIs.GET_Add_FROM_LAT_LNG, params,
                    ConstantsEnum.GetAddFromLatLng, Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);
        }
    }



    void SaveAgricultureRecord(final String EventDateTimeInUtc,final String Truck,final String DriverId,final String CompanyId,
                              final String SourceAddress,final String SourceLatitude,final String SourceLongitude,final String Odometer,final String EngineHours,final String IsEnabled){

        String driverZoneCurrentTime = Globally.GetDriverCurrentDateTime(global, getActivity());
        driverZoneCurrentTime = driverZoneCurrentTime.replaceAll("T", " ");

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.EventDateTime, driverZoneCurrentTime);
        params.put(ConstantsKeys.EventDateTimeInUtc, EventDateTimeInUtc);
        params.put(ConstantsKeys.Truck, Truck);
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.SourceAddress, SourceAddress);
        params.put(ConstantsKeys.SourceLatitude, SourceLatitude);
        params.put(ConstantsKeys.SourceLongitude, SourceLongitude);
        params.put(ConstantsKeys.Odometer, Odometer);
        params.put(ConstantsKeys.EngineHours, EngineHours);
        params.put(ConstantsKeys.IsEnabled, IsEnabled);

        SaveAgricultureRequest.executeRequest(Request.Method.POST, APIs.AddAgricultureException, params, SaveAgricultureException,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }



    void CheckAppStatus(){
        if (ExistingVersionCodeInt >= VersionCodeInt) {

            global.EldScreenToast(SyncDataBtn, "Your application is up to date.", UILApplication.getInstance().getThemeColor());

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
                        global.EldScreenToast(SyncDataBtn, "This application is already in (Download/EldApp) folder.", UILApplication.getInstance().getThemeColor());
                        ApkFilePath = global.getAlsApkPath() + "/" + existingApkFilePath;
                       // InstallApp(ApkFilePath);
                        openFileLocation(ApkFilePath);
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


    private void updateIsCycleChangeIn18DaysLog(){
        try{
           JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
            JSONObject lastObj = hMethods.GetLastJsonFromArray(driverLogArray);
            lastObj.put(ConstantsKeys.IsCycleChanged, true);
            driverLogArray.put(driverLogArray.length()-1, lastObj);

            // update array in db helper
            hMethods.DriverLogHelper( Integer.valueOf(DriverId), dbHelper, driverLogArray);
        }catch (Exception e){
            e.printStackTrace();
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
                            Logger.LogDebug("response", "response: " + response);


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
                                global.EldScreenToast(SyncDataBtn, "Your application is up to date.", UILApplication.getInstance().getThemeColor());
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

                        if (getActivity() != null && !constants.isObdConnectedWithELD(getActivity())) {
                            global.InternetErrorDialog(getActivity(), true, true);
                            Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                        }else{
                            global.EldScreenToast(SyncDataBtn, Message, UILApplication.getInstance().getThemeColor());
                        }

                        saveUpdatedCycleData();
                        updateIsCycleChangeIn18DaysLog();

                        saveMissingDiagnostic(getString(R.string.obd_data_is_missing), "Cycle Change");

                        break;

                    case OperatingZone:
                        global.EldScreenToast(SyncDataBtn, Message, UILApplication.getInstance().getThemeColor());

                        if(IsSouthCanada.equals("true")) {
                            SharedPref.SetNorthCanadaStatus(false, getActivity());
                        }else{
                            SharedPref.SetNorthCanadaStatus(true, getActivity());
                        }

                        getSavedCycleData();

                        saveMissingDiagnostic(getString(R.string.obd_data_is_missing), "Operating Zone Change");

                        break;


                    case AddressLatLong:
                        try {
                            dataObj = new JSONObject(obj.getString("Data"));
                            SourceLatitude = dataObj.getString("SourceLatitude");
                            SourceLongitude = dataObj.getString("SourceLongitude");
                            double latitude = Double.parseDouble(SourceLatitude);
                            double longitude = Double.parseDouble(SourceLongitude);

                            if(Globally.LATITUDE.length() == 0){
                                Globally.LATITUDE = "0.0";
                                Globally.LONGITUDE = "0.0";
                            }

                            double distanceMiles = constants.CalculateDistance(Double.parseDouble(Globally.LATITUDE),Double.parseDouble(Globally.LONGITUDE),latitude,longitude,"M", 0);
                            double distanceMilesFormat = Double.parseDouble(Constants.Convert2DecimalPlacesDouble(distanceMiles));
                            double distanceKm =  Double.parseDouble(Constants.Convert2DecimalPlacesDouble(distanceMilesFormat * 1.60934));
                          //  double distance = Double.parseDouble(Constants.Convert2DecimalPlacesDouble(distanceMilesFormat - Constants.AgricultureDistanceInMiles));
                            if(distanceMilesFormat > Constants.AgricultureDistanceInMiles){
                               String color = "#354365";
                                if(UILApplication.getInstance().isNightModeEnabled()){
                                    color = "#FFFFFF";
                                }

                                String sourceDestAddress = "<br/><font color='"+color+"'><b>Current Address:</b> " + SourceAddress +
                                                            "<br/><b>Source Address:</b> " + agricultureAddress +
                                                            "<br/><b>Distance:</b> " + distanceMilesFormat + " miles ("+distanceKm+" KM)." ;

                                OpenAgricultureAlertDialog(sourceDestAddress + "<br/><br/><b>Please Note:</b> You are eligible for this exemption within 150 air-mile " +
                                        "(172.6 Miles or 277.80 KM) radius from the source of the commodities.</font>");
                            }else{
                                IsAgriExceptionEnable = true;
                                SaveAgricultureRecord(Globally.GetCurrentUTCTimeFormat(),
                                        SharedPref.getTruckNumber(getActivity()), DriverId,CompanyId,
                                        agricultureAddress,String.valueOf(latitude),String.valueOf(longitude),SharedPref.getObdEngineHours(getContext()),SharedPref.getObdOdometer(getContext()),"1");

                                if (driverAddressDialog != null && driverAddressDialog.isShowing())
                                    driverAddressDialog.dismiss();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;


                    case ConstantsEnum.GetAddFromLatLng:

                        if (!obj.isNull("Data")) {
                            try {
                                JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                                if(!dataJObject.getString(ConstantsKeys.City).equals("null")) {
                                    SourceAddress = dataJObject.getString(ConstantsKeys.City);
                                    SourceAddress = SourceAddress + ", " +dataJObject.getString(ConstantsKeys.State);
                                    SourceAddress = SourceAddress + ", " + dataJObject.getString(ConstantsKeys.Country);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        break;


                    case SaveAgricultureException:

                        if(IsAgriExceptionEnable) {

                            SourceAddress = "--";
                            SharedPref.setAgricultureExemption(true, getActivity());
                            SharedPref.SaveAgricultureRecord(SourceLatitude, SourceLongitude, agricultureAddress, getContext());

                            hMethods.SaveDriversJob(DriverId, DeviceId, getString(R.string.begin_ag_Exemption),
                                    getString(R.string.enable_agriculture_exception),
                                    LocationType, "", false, isNorthCanada, DriverType, constants,
                                    MainDriverPref, CoDriverPref,
                                    syncingMethod, global, hMethods, dbHelper, getActivity(), false,
                                    CoDriverId, CoDriverName, false, Constants.Auto);
                            global.EldScreenToast(SyncDataBtn, "Agriculture Exemption Enabled", UILApplication.getInstance().getThemeColor());


                        }else{

                            SharedPref.setAgricultureExemption(false, getActivity());
                            SharedPref.SaveAgricultureRecord("","","",getContext());
                            hMethods.SaveDriversJob(DriverId, DeviceId, getString(R.string.end_ag_Exemption), getString(R.string.disable_agriculture_exception),
                                    LocationType, "", false, isNorthCanada, DriverType, constants,
                                    MainDriverPref, CoDriverPref,
                                    syncingMethod, global, hMethods, dbHelper, getActivity(), false,
                                    CoDriverId, CoDriverName, false, Constants.Auto );

                            global.EldScreenToast(SyncDataBtn, "Agriculture Exemption Disabled", UILApplication.getInstance().getThemeColor());


                        }

                        getExceptionStatus();




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
                        }else if(flag == AddressLatLong){
                            Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                        }else if(flag == GetAppUpdate || flag == OperatingZone || flag == SaveAgricultureException){
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

                    if(flag == SaveAgricultureException){
                        global.EldScreenToast(SyncDataBtn, Globally.DisplayErrorMessage(error.toString()), getResources().getColor(R.color.colorVoilation));
                    }

                    Logger.LogDebug("Driver", "error" + error.toString());
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

            Logger.LogError("String Response", ">>>Sync Response:  " + response);
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
                        global.EldScreenToast(SettingSaveBtn, msgTxt, UILApplication.getInstance().getThemeColor());
                    }else{
                        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ) { // Single Driver Type and Position is 0
                            // when main driver is selected, clear co driver data
                            CoDriverPref.ClearCoDrLogFromList(getActivity());
                        }else{
                            // clear main driver data
                            MainDriverPref.ClearLogFromList(getActivity());
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


                    String message = constants.getErrorMsg(obj.getString("Message"));
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
                if(getActivity() != null && !getActivity().isFinishing()) {
                    global.EldScreenToast(SettingSaveBtn, "Error occurred", getResources().getColor(R.color.colorVoilation));
                }
                e.printStackTrace();
            }
        }
    };


    void getSavedCycleData(){

       // CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());
        CurrentCycleId = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());

        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ) {
            DriverType = Constants.MAIN_DRIVER_TYPE;
            SavedPosition  = 0;
            SavedCanCycle  = DriverConst.GetDriverSettings(DriverConst.CANCycleId, getActivity());
            SavedUsaCycle  = DriverConst.GetDriverSettings(DriverConst.USACycleId, getActivity());
            SavedTimeZone  = DriverConst.GetDriverSettings(DriverConst.TimeZoneID, getActivity());
        }else{
            DriverType = Constants.CO_DRIVER_TYPE;
            SavedPosition  = 1;
            SavedCanCycle  = DriverConst.GetCoDriverSettings(DriverConst.CoCANCycleId, getActivity());
            SavedUsaCycle  = DriverConst.GetCoDriverSettings(DriverConst.CoUSACycleId, getActivity());
            SavedTimeZone  = DriverConst.GetCoDriverSettings(DriverConst.CoTimeZoneID, getActivity());
        }

        TruckNumber    = SharedPref.getTruckNumber(getActivity());  //DriverConst.GetDriverTripDetails(DriverConst.Truck, getActivity());
        DriverTimeZone = DriverConst.GetDriverSettings(DriverConst.DriverTimeZone, getActivity());

        isNorthCanada  =  SharedPref.IsNorthCanada(getActivity());
        if(CurrentCycleId.equals(global.CANADA_CYCLE_1) || CurrentCycleId.equals(global.CANADA_CYCLE_2) ){
            caCurrentCycleTV.setVisibility(View.VISIBLE);
            usCurrentCycleTV.setVisibility(View.INVISIBLE);
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
            caCurrentCycleTV.setVisibility(View.INVISIBLE);
            usCurrentCycleTV.setVisibility(View.VISIBLE);
            opZoneTmgView.setVisibility(View.INVISIBLE);
            canEditImgView.setVisibility(View.GONE);
            usEditImgView.setVisibility(View.VISIBLE);
            operatingZoneTV.setText(getString(R.string.OperatingZoneUS));
            deferralRuleLay.setVisibility(View.VISIBLE);
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
        fragmentTran.commitAllowingStateLoss();


    }






    public void haulExceptionAlert() {
        if(enableExceptionAlert != null && enableExceptionAlert.isShowing()){
            Logger.LogDebug("dialog", "dialog is showing" );
        }else {
            closeDialogs();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogStyle);
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

                                global.EldScreenToast(SyncDataBtn, getResources().getString(R.string.haul_excptn_enabled), UILApplication.getInstance().getThemeColor());

                                hMethods.SaveDriversJob(DriverId, DeviceId, "", getString(R.string.enable_ShortHaul_exception),
                                        LocationType, "", true, isNorthCanada, DriverType, constants,
                                        MainDriverPref, CoDriverPref,
                                        syncingMethod, global, hMethods, dbHelper, getActivity(), false,
                                        CoDriverId, CoDriverName, false, Constants.Auto);

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
                                    constants.writeViolationFile(global.getDateTimeObj(global.GetDriverCurrentDateTime(global, getActivity()), false),
                                            global.GetCurrentUTCDateTime(),
                                            Integer.valueOf(DriverId), CurrentCycleId,
                                            (int) global.GetDriverTimeZoneOffSet(getActivity()),
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
        String CurrentCycleId      = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());

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

    public boolean isAllowToEnableExceptionn(String DriverId, boolean isAdverse){
        boolean isAllow = false;
        exceptionDesc = "";
        String CurrentCycleId      = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());
        try {
            String ValidateCycle1, ValidateCycle2;

            if(isAdverse){
                ValidateCycle1 = Globally.CANADA_CYCLE_1;
                ValidateCycle2 = Globally.CANADA_CYCLE_2;
            }else{
                ValidateCycle1 = Globally.USA_WORKING_6_DAYS;
                ValidateCycle2 = Globally.USA_WORKING_7_DAYS;

                /*ValidateCycle1 = Globally.CANADA_CYCLE_1;
                ValidateCycle2 = Globally.CANADA_CYCLE_2;*/
            }

            if(CurrentCycleId.equals(ValidateCycle1) || CurrentCycleId.equals(ValidateCycle2)) {
                JSONArray logArray = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
                JSONObject lastObj = hMethods.GetLastJsonFromArray(logArray);

                int status = lastObj.getInt(ConstantsKeys.DriverStatusId);
                boolean yardMove = lastObj.getBoolean(ConstantsKeys.YardMove);

                if(isAdverse){
                    if(status == Constants.DRIVING || (status == Constants.ON_DUTY && !yardMove) ){
                        isAllow = true;
                    }else{
                        exceptionDesc = "Exception only allowed in Driving or OnDuty.";
                    }
                }else{
                    boolean Personal = lastObj.getBoolean(ConstantsKeys.Personal);
                    if(status == Constants.OFF_DUTY && !Personal){
                        isAllow = true;
                    }else{
                        exceptionDesc = "Exception only allowed in OffDuty.";
                    }
                }
            }else{
                if(isAdverse) {
                    exceptionDesc = "Exception not allowed. Please contact to your company";
                }else{
                    exceptionDesc = getString(R.string.excp_canada_cycle_check);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isAllow;

    }


    void OpenAddressDialog() {


        try {
            if(getActivity() != null && !getActivity().isFinishing()) {

                driverAddressDialog = new DriverAddressDialog(getActivity(),rootView,
                        new DriverLocationListener());
                driverAddressDialog.show();
            }

        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private class DriverLocationListener implements DriverAddressDialog.LocationListener {

        @Override
        public void CancelLocReady() {
            if (driverAddressDialog != null && driverAddressDialog.isShowing())
                driverAddressDialog.dismiss();
            agricultureSwitchButton.setChecked(false);
        }

        @Override
        public void SaveLocReady(String Address) {

            Logger.LogDebug("Address: " , Address);
            agricultureAddress = Address;

            if(Address.length() < 5){
                Toast.makeText(getContext(), "Please enter proper address", Toast.LENGTH_LONG).show();
            }else {
                GetUserLatLong(Address);
            }

        }
    }


    void OpenAgricultureAlertDialog(String description) {


        try {
            if(getActivity() != null && !getActivity().isFinishing()) {

                agricultureDialog = new AgricultureDialog(getActivity(),description,
                        new AgricultureListner());
                agricultureDialog.show();
            }

        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private class AgricultureListner implements AgricultureDialog.ConfirmationListener {

        @Override
        public void OkBtnReady() {
            agricultureDialog.dismiss();
        }
    }

}
