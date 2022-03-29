package com.messaging.logistic.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.AlertDialogEld;
import com.constants.AsyncResponse;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.CsvReader;
import com.constants.DriverLogResponse;
import com.constants.GPSRequest;
import com.constants.InitilizeEldView;
import com.constants.LoadingSpinImgView;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.Slidingmenufunctions;
import com.constants.SyncDataUpload;
import com.constants.TcpClient;
import com.constants.Utils;
import com.constants.VolleyRequest;
import com.constants.VolleyRequestWithoutRetry;
import com.custom.dialogs.ConfirmationDialog;
import com.custom.dialogs.ContinueStatusDialog;
import com.custom.dialogs.DatePickerDialog;
import com.custom.dialogs.DeferralDialog;
import com.custom.dialogs.DriverLocationDialog;
import com.custom.dialogs.EldNotificationDialog;
import com.custom.dialogs.NotificationNewsDialog;
import com.custom.dialogs.OtherOptionsDialog;
import com.custom.dialogs.RemainingTimeDialog;
import com.custom.dialogs.ShareDriverLogDialog;
import com.custom.dialogs.ShippingDocDialog;
import com.custom.dialogs.TimeZoneDialog;
import com.custom.dialogs.TrailorDialog;
import com.custom.dialogs.VehicleDialog;
import com.custom.dialogs.VehicleDialogLogin;
import com.custom.dialogs.VehicleDialogLoginOld;
import com.custom.dialogs.VehicleDialogOld;
import com.driver.details.DriverConst;
import com.local.db.DeferralMethod;
import com.local.db.MalfunctionDiagnosticMethod;
import com.messaging.logistic.UnidentifiedActivity;
import com.models.EldDriverLogModel;
import com.driver.details.ParseLoginDetails;
import com.local.db.CTPatInspectionMethod;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.InspectionMethod;
import com.local.db.NotificationMethod;
import com.local.db.OdometerHelperMethod;
import com.local.db.RecapViewMethod;
import com.local.db.ShipmentHelperMethod;
import com.local.db.SyncingMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.SuggestedFragmentActivity;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.models.DriverLocationModel;
import com.models.EldDataModelNew;
import com.models.NotificationNewsModel;
import com.models.VehicleModel;
import com.notifications.NotificationManagerSmart;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.CoNotificationPref;
import com.shared.pref.EldCoDriverLogPref;
import com.shared.pref.EldSingleDriverLogPref;
import com.shared.pref.MainDriverEldPref;
import com.shared.pref.NotificationPref;
import com.shared.pref.StatePrefManager;
import com.wifi.settings.WiFiConfig;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import dal.tables.OBDDeviceData;
import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;
import obdDecoder.Decoder;
import webapi.LocalCalls;


public class EldFragment extends Fragment implements View.OnClickListener {

    View rootView, loginDialogView;

    RelativeLayout StatusMainView, refreshPageBtn;
    LinearLayout DriverLay, trailorLayout, truckLay, remainingLay, usedHourLay, shippingLay, odometerLay,  eldNewBottomLay, eldChildSubLay;
    ImageView editTrailorIV, editTruckIV, eldMenuBtn, eldMenuErrorImgVw, certifyLogErrorImgVw;
    LoadingSpinImgView loadingSpinEldIV;
    RelativeLayout OnDutyBtn, DrivingBtn, OffDutyBtn, SleeperBtn, eldMenuLay, dayNightLay, eldHomeDriverUiLay, settingsMenuBtn,
            logoutMenuBtn, otherOptionBtn, malfunctionLay;
    ImageView calendarBtn, coDriverImgView, connectionStatusImgView, cyleFlagImgView;
    Button sendReportBtn, yardMoveBtn, personalUseBtn;
    RelativeLayout certifyLogBtn;
    public static Button refreshLogBtn, moveToCertifyPopUpBtn, autoOffDutyBtn, autoOnDutyBtn, autoDriveBtn;
    public static TextView summaryBtn;
    int DRIVER_JOB_STATUS = 1, SWITCH_VIEW = 1, oldStatusView = 0, DriverType = 0;
    TextView dateTv, nameTv, jobTypeTxtVw, perDayTxtVw, jobTimeTxtVw, jobTimeRemngTxtVw, EldTitleTV,
            timeRemainingTxtVw, invisibleTxtVw;
    TextView tractorTv, trailorTv, coDriverTv, otherOptionBadgeView;
    TextView onDutyViolationTV, drivingViolationTV, sleeperViolationTV, offDutyViolationTV;
    TextView onDutyTimeTxtVw, drivingTimeTxtVw, sleeperTimeTxtVw, offDutyTimeTxtVw;
    TextView onDutyTxtVw, drivingTxtVw, sleeperTxtVw, offDutyTxtVw, excpnEnabledTxtVw;
    TextView DriverComNameTV, coDriverComNameTV, remainingTimeTopTV, currentCycleTxtView, refreshTitleTV, malfunctionTV;
    TextView asPerShiftOnDutyTV, asPerShiftDrivingTV, asPerDateSleepTV, asPerDateOffDutyTV;

    boolean IsRefreshedClick = false, IsAOBRDAutomatic = false, IsAOBRD = false, isNewDateStart = true, isYardBtnClick = false, isFragmentAdd = false;
    boolean isFirst = true, isViolation = false, isCertifyLog = false, isDrivingCalled = false, IsDOT = false;
    boolean IsAddressUpdate = false, isTimeDefault = true, is18DaysLogApiCalled = false, isSaveTrailerPending = false, IsLogApiRefreshed = false;
    public static boolean IsOdometerReading = false, IsSaveOperationInProgress = false, IsPopupDismissed = false;
    Animation emptyTrailerNoAnim, OdometerFaceView, exceptionFaceView, connectionStatusAnimation, editLogAnimation;
    String strCurrentDate = "", DRIVER_ID = "0", DeviceId = "", LoginTruckChange = "true";
    String SelectedDate = "", CompanytimeZone = "", UtcTimeZone = "", CountryCycle = "", VIN_NUMBER = "", TruckNumber = "";
    String isPersonalOld = "false", Reason = "", TrailorNumber = "", MainDriverName = "", CoDriverName = "";
    String SavedCanCycle = "", SavedUsaCycle = "", CurrentCycle = "", CurrentCycleId = "0", TeamDriverType = "",
            MainDriverId = "", CoDriverId = "", CoDriverIdInSaveStatus = "", CoDriverNameInSaveStatus = "", ViolationsReason = "", titleDesc, okText, ptiSelectedtxt = "";
    String certifyTitle = "<font color='#1A3561'><b>Alert !!</b></font>";
    String DriverCompanyId = "", DriverCarrierName = "", CoDriverCarrierName = "", State = "", Country = "", AddressLine = "", LocationType = "", MalfunctionDefinition = "";
    public static String City = "", AobrdState = "", isPersonal = "false", VehicleId = "", DriverStatusId = "1";
    String packageName              = "com.messaging.logistic";
    String currentUtcTimeDiffFormat = Globally.GetCurrentUTCTimeFormat();

    public static int OldSelectedStatePos = 0;
    int TruckListPosition = 0;
    private Timer mTimer;
    long MIN_TIME_BW_UPDATES = 60000;  // 60 SecPostDriverLogData

    ShareDriverLogDialog shareDialog;
    Constants constants;
    ProgressBar progressBar;
    TrailorDialog trailerDialog;
    VehicleDialog vehicleDialog;
    VehicleDialogLogin vehicleDialogLogin;
    VehicleDialogOld vehicleDialogOld;
    VehicleDialogLoginOld vehicleDialogLoginOld;
    boolean isOldVehicleDialog = false;

    DatePickerDialog dateDialog;
    ShippingDocDialog shippingDocDialog;
    DriverLocationDialog driverLocationDialog;
    TimeZoneDialog timeZoneDialog;
    RemainingTimeDialog remainingDialog;
    OtherOptionsDialog otherOptionsDialog;
    EldNotificationDialog eldNotificationDialog;
    DeferralDialog deferralDialog;

    Slidingmenufunctions slideMenu;
    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;
    StatePrefManager statePrefManager;

    List<String> StateArrayList;
    List<DriverLocationModel> StateList;
    List<String> onDutyRemarkList = new ArrayList<String>();

    public static JSONArray DriverJsonArray = new JSONArray();
    JSONArray currentDayArray = new JSONArray();
    int listSize = 0;

    EldSingleDriverLogPref eldSharedPref;
    EldCoDriverLogPref coEldSharedPref;
    ParseLoginDetails parseLoginDetails;


    final int UpdateObdVeh          =  20;
    final int GetObdAssignedVeh     =  30;
    final int GetOndutyRemarks      =  40;
    final int SendLog               =  70;
    final int GetOdometer           =  80;
    final int SaveTrailer           =  90;
    final int GetDriverLog18Days    = 100;
    final int GetCoDriverLog18Days  = 110;
    final int GetShipment18Days     = 120;
    final int GetShipment18DaysCo   = 130;
    final int GetOdometers18Days    = 140;
    final int GetDriverPermission   = 150;
    final int GetAddFromLatLng      = 160;
    final int GetInspection18Days   = 170;
    final int GetInspection18DaysCo = 180;
    final int GetInspection         = 190;
    final int GetNotifications      = 200;
    final int GetNewsNotifications  = 210;
    final int GetReCertifyRecords   = 220;
    final int GetRecapViewFlagMain  = 230;
    final int GetRecapViewFlagCo    = 240;
    final int NotReady              = 250;
    final int OdometerDetailInPu    = 260;
    final int SaveAgricultureException = 270;


    /*-------- DRIVER STATUS ----------*/
    public static final int OFF_DUTY = 1;
    public static final int SLEEPER  = 2;
    public static final int DRIVING  = 3;
    public static final int ON_DUTY  = 4;
    final int PERSONAL = 5;

    int LATEST_JOB_STATUS            = 1;
    int Driver18DaysApiCount         = 0;
    int JobStatusInt                 = 1;
    int pendingNotificationCount     = 0;

    /*-------- After PERSONAL JOB ----------*/
    final int PERSONAL_OFF_DUTY = 11;
    final int PERSONAL_SLEEPER = 12;
    final int PERSONAL_DRIVING = 13;
    final int PERSONAL_ON_DUTY = 14;
    int AFTER_PERSONAL_JOB = 0;
    int PC_END = 1010;
    int YM_END = 1020;


    int SaveRequestCount = 0;
    int TotalDrivingHoursInt = 0;
    int TotalOnDutyHoursInt = 0;
    int TotalOffDutyHoursInt = 0;
    int TotalSleeperBerthHoursInt = 0;
    int OffDutyPerShift = 0;
    int SleeperPerShift = 0;
    int LeftDayDrivingHoursInt = 0;
    int LeftDayOnDutyHoursInt = 0;
    int LeftWeekOnDutyHoursInt = 0;
    int offsetFromUTC = 0;
    int offSetFromServer = 0;
    int shiftRemainingMinutes = 0;
    int shiftUsedMinutes = 0;
    int minOffDutyUsedHours = 0;
    final int MainDriverLog = 101;
    final int CoDriverLog = 102;

    boolean isYardMove = false;
    boolean IsLogShown = false;
    boolean IsTrailorUpdate = false;
    boolean IsRecapShown = true;
    boolean IsObdVehShown = true;
    boolean IsVehicleDialogShown = false;
    boolean IsOnCreateView = true;
    boolean IsLogApiACalled = false;
    boolean isSingleDriver = true;
    boolean isRemainingView = false;
    boolean IsValidTime = true;
    boolean isOldRecord = false;
    boolean isMinOffDutyHoursSatisfied = false;
    boolean IsPersonalUseAllowed = true;
    boolean isEldRuleAlreadyCalled = false;
    boolean isLocMalfunction;
    boolean isExemptDriver = false;
    boolean IsNorthCanada = false;

    public static boolean IsTruckChange = false;
    public static boolean IsPrePost = false;
    public static boolean IsStartReading = false;
    public static boolean IsMsgClick = false;
    public static boolean isUpdateDriverLog = false;

    boolean isHaulExcptn        = false;
    boolean isAdverseExcptn     = false;
    boolean isDeferral          = false;
    boolean isUnIdentifiedOccur = false;
    boolean isUnIdentifiedAlert = false;
    boolean isPending18DaysRequest = false;
    boolean isDeferralOccurred      = false;
    boolean isAgriException    = false;

    String DeviceTimeZone = "", DriverTimeZone = "", LocationJobTYpe = "";
    String WeeklyRemainingTime = "00:00", DrivingRemainingTime = "00:00", OnDutyRemainingTime = "00:00";
    MyTimerTask timerTask;
    Globally Global;
    CsvReader csvReader;

    SaveDriverLogPost saveDriverLogPost;
    VolleyRequest GetLogRequest, GetOdometerRequest, GetOdo18DaysRequest, GetLog18DaysRequest, GetOnDutyRequest, GetOBDVehRequest, GetShippingRequest, GetPermissions;
    VolleyRequest SaveOBDVehRequest, SaveTrailerNumber, Inspection18DaysRequest, GetInspectionRequest, GetNotificationRequest;
    VolleyRequest GetNewsNotificationReq, GetReCertifyRequest, GetRecapView18DaysData, notReadyRequest, SaveAgricultureRequest ;
    VolleyRequestWithoutRetry GetAddFromLatLngRequest;
    // RequestQueue    SaveLogRequest;
    Map<String, String> params;

    NotificationManagerSmart mNotificationManager;
    DBHelper dbHelper;
    HelperMethods hMethods;
    ShipmentHelperMethod shipmentMethod;
    OdometerHelperMethod odometerhMethod;
    SyncingMethod syncingMethod;
    RecapViewMethod recapViewMethod;
    InspectionMethod inspectionMethod;
    NotificationMethod notificationMethod;
    CTPatInspectionMethod ctPatInspectionMethod;
    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
    DeferralMethod deferralMethod;

    InitilizeEldView initilizeEldView;
    public static JSONArray driverLogArray = new JSONArray();

    List<DriverLog> oDriverLogDetail = new ArrayList<DriverLog>();

    DriverDetail oDriverDetail = new DriverDetail();

    RulesResponseObject RulesObj, getDailyOffLeftMinObj;

    RulesResponseObject RemainingTimeObj;


    NotificationPref notificationPref;
    CoNotificationPref coNotificationPref;

    AlertDialogEld statusEndConfDialog;
    ConfirmationDialog confirmationDialog;
    ContinueStatusDialog continueStatusDialog;
    AlertDialog alertDialog, inspectDialog;
    ProgressDialog progressDialog;
    ProgressDialog progressD;
    GPSRequest gpsRequest;
    DriverPermissionMethod driverPermissionMethod;
    AlertDialog statusAlertDialog;
    AlertDialog saveJobAlertDialog, certifyLogAlert;
    private Vector<AlertDialog> vectorDialogs = new Vector<AlertDialog>();
    SwitchCompat dotSwitchButton;
    ImageView dayNightButton;
    ServiceBroadcastReceiver updateReceiver;

    WiFiConfig wifiConfig;
    Decoder decoder;
    Utils obdUtil;
    LocalCalls localCalls;


    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_eld_new, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        try {
            initView(rootView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }


    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    void initView(View view) throws JSONException {

        try{
            slideMenu       = new Slidingmenufunctions();
            dbHelper        = new DBHelper(getActivity());
            hMethods        = new HelperMethods();
            wifiConfig      = new WiFiConfig();
            decoder         = new Decoder();
            obdUtil         = new Utils(getActivity());
            localCalls      = new LocalCalls();

            obdUtil.createLogFile();
            obdUtil.createAppUsageLogFile();
        }catch (Exception e){
            e.printStackTrace();
        }

        shipmentMethod = new ShipmentHelperMethod();
        odometerhMethod = new OdometerHelperMethod();
        initilizeEldView = new InitilizeEldView();
        syncingMethod = new SyncingMethod();
        recapViewMethod = new RecapViewMethod();
        inspectionMethod = new InspectionMethod();
        notificationMethod = new NotificationMethod();
        ctPatInspectionMethod = new CTPatInspectionMethod();
        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();
        deferralMethod              = new DeferralMethod();

        gpsRequest = new GPSRequest(getActivity());
        driverPermissionMethod = new DriverPermissionMethod();
        notificationPref = new NotificationPref();
        coNotificationPref = new CoNotificationPref();

        RulesObj = new RulesResponseObject();
        RemainingTimeObj = new RulesResponseObject();
        getDailyOffLeftMinObj = new RulesResponseObject();
        Global = new Globally();
        csvReader = new CsvReader();
        parseLoginDetails = new ParseLoginDetails();
        eldSharedPref = new EldSingleDriverLogPref();
        coEldSharedPref = new EldCoDriverLogPref();

        MainDriverPref = new MainDriverEldPref();
        CoDriverPref = new CoDriverEldPref();
        statePrefManager = new StatePrefManager();

        saveDriverLogPost = new SaveDriverLogPost(getActivity(), saveLogRequestResponse);
        GetLogRequest = new VolleyRequest(getActivity());
        GetOdometerRequest = new VolleyRequest(getActivity());
        GetOdo18DaysRequest = new VolleyRequest(getActivity());
        GetLog18DaysRequest = new VolleyRequest(getActivity());
        GetOnDutyRequest = new VolleyRequest(getActivity());
        GetOBDVehRequest = new VolleyRequest(getActivity());
        GetShippingRequest = new VolleyRequest(getActivity());
        GetPermissions = new VolleyRequest(getActivity());

        SaveOBDVehRequest = new VolleyRequest(getActivity());
        SaveTrailerNumber = new VolleyRequest(getActivity());
        Inspection18DaysRequest = new VolleyRequest(getActivity());
        GetInspectionRequest = new VolleyRequest(getActivity());
        GetNotificationRequest = new VolleyRequest(getActivity());
        GetNewsNotificationReq = new VolleyRequest(getActivity());
        GetReCertifyRequest = new VolleyRequest(getActivity());
        GetAddFromLatLngRequest = new VolleyRequestWithoutRetry(getActivity());
        GetRecapView18DaysData  = new VolleyRequest(getActivity());
        notReadyRequest         = new VolleyRequest(getActivity());
        SaveAgricultureRequest  = new VolleyRequest(getActivity());

        statusEndConfDialog  = new AlertDialogEld(getActivity());
        statusAlertDialog =  new AlertDialog.Builder(getActivity()).create();

        updateReceiver = new ServiceBroadcastReceiver();
        //  SaveLogRequest      = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        mNotificationManager = new NotificationManagerSmart(getActivity());
        progressDialog.setMessage("Saving ...");
        progressDialog.setCancelable(true);


        //-------------------  Check all table existing status --------------------
        dbHelper.CheckAllTableExistingStatus();

        constants = new Constants();

        StatusMainView = (RelativeLayout) view.findViewById(R.id.StatusMainView);

        OnDutyBtn = (RelativeLayout) view.findViewById(R.id.onDutyLay);
        DrivingBtn = (RelativeLayout) view.findViewById(R.id.drivingLay);
        OffDutyBtn = (RelativeLayout) view.findViewById(R.id.offDutyLay);
        SleeperBtn = (RelativeLayout) view.findViewById(R.id.sleeperDutyLay);

        eldMenuLay = (RelativeLayout) view.findViewById(R.id.eldMenuLay);
        eldHomeDriverUiLay = (RelativeLayout) view.findViewById(R.id.eldHomeDriverUiLay);
        dayNightLay = (RelativeLayout) view.findViewById(R.id.dayNightLay);
        settingsMenuBtn = (RelativeLayout)view.findViewById(R.id.settingsMenuBtn);
        logoutMenuBtn   = (RelativeLayout)view.findViewById(R.id.logoutMenuBtn);
        otherOptionBtn = (RelativeLayout)view.findViewById(R.id.otherOptionBtn);
        malfunctionLay = (RelativeLayout)view.findViewById(R.id.malfunctionLay);

        shippingLay = (LinearLayout) view.findViewById(R.id.shippingLay);
        odometerLay = (LinearLayout) view.findViewById(R.id.odometerLay);
        eldNewBottomLay = (LinearLayout) view.findViewById(R.id.eldNewBottomLay);
        eldChildSubLay = (LinearLayout) view.findViewById(R.id.eldChildSubLay);

        coDriverImgView = (ImageView) view.findViewById(R.id.codriverImgView);
        calendarBtn = (ImageView) view.findViewById(R.id.calendarBtn);
        editTrailorIV = (ImageView) view.findViewById(R.id.editTrailorIV);
        editTruckIV = (ImageView) view.findViewById(R.id.editTruckIV);
        eldMenuBtn = (ImageView) view.findViewById(R.id.eldMenuBtn);
        connectionStatusImgView = (ImageView)view.findViewById(R.id.connectionStatusImgView);
        eldMenuErrorImgVw = (ImageView) view.findViewById(R.id.eldMenuErrorImgVw);
        certifyLogErrorImgVw= (ImageView) view.findViewById(R.id.certifyLogErrorImgVw);
        cyleFlagImgView= (ImageView) view.findViewById(R.id.cyleFlagImgView);

        certifyLogBtn = (RelativeLayout) view.findViewById(R.id.certifyLogBtn);
        sendReportBtn = (Button) view.findViewById(R.id.sendReportBtn);
        yardMoveBtn   = (Button)view.findViewById(R.id.yardMoveBtn);

        personalUseBtn = (Button) view.findViewById(R.id.personalUseBtn);
        moveToCertifyPopUpBtn = (Button) view.findViewById(R.id.resetTimerBtn);
        refreshLogBtn = (Button) view.findViewById(R.id.refreshLogBtn);
        autoOffDutyBtn = (Button) view.findViewById(R.id.autoOffDutyBtn);
        autoOnDutyBtn  = (Button)view.findViewById(R.id.autoOnDutyBtn);
        autoDriveBtn = (Button) view.findViewById(R.id.autoDriveBtn);

        summaryBtn = (TextView) view.findViewById(R.id.summaryBtn);
        truckLay = (LinearLayout) view.findViewById(R.id.truckLay);
        trailorLayout = (LinearLayout) view.findViewById(R.id.trailorLayout);
        remainingLay = (LinearLayout) view.findViewById(R.id.remainingLay);
        usedHourLay = (LinearLayout) view.findViewById(R.id.usedHourLay);
        refreshPageBtn = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        DriverLay = (LinearLayout) view.findViewById(R.id.DriverLay);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarEld);
        loadingSpinEldIV = (LoadingSpinImgView) view.findViewById(R.id.loadingSpinEldIV);

        dotSwitchButton = (SwitchCompat) view.findViewById(R.id.dotSwitchButton);
        dayNightButton = (ImageView) view.findViewById(R.id.dayNightButton);

        emptyTrailerNoAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        OdometerFaceView = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        exceptionFaceView = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        connectionStatusAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        editLogAnimation          = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

        InitilizeTextView(view);
        emptyTrailerNoAnim.setDuration(1500);
        OdometerFaceView.setDuration(1500);
        exceptionFaceView.setDuration(1500);
        connectionStatusAnimation.setDuration(1500);
        editLogAnimation.setDuration(1500);

        truckLay.setOnClickListener(this);
        trailorLayout.setOnClickListener(this);
        refreshPageBtn.setOnClickListener(this);
        DriverLay.setOnClickListener(this);
        calendarBtn.setOnClickListener(this);
        sendReportBtn.setOnClickListener(this);
        yardMoveBtn.setOnClickListener(this);
        certifyLogBtn.setOnClickListener(this);
        OnDutyBtn.setOnClickListener(this);
        DrivingBtn.setOnClickListener(this);
        OffDutyBtn.setOnClickListener(this);
        SleeperBtn.setOnClickListener(this);
        personalUseBtn.setOnClickListener(this);
        moveToCertifyPopUpBtn.setOnClickListener(this);
        autoOffDutyBtn.setOnClickListener(this);
        autoOnDutyBtn.setOnClickListener(this);
        autoDriveBtn.setOnClickListener(this);


        refreshLogBtn.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        shippingLay.setOnClickListener(this);
        odometerLay.setOnClickListener(this);
        summaryBtn.setOnClickListener(this);
        invisibleTxtVw.setOnClickListener(this);
        dayNightLay.setOnClickListener(this);
        settingsMenuBtn.setOnClickListener(this);
        logoutMenuBtn.setOnClickListener(this);
        connectionStatusImgView.setOnClickListener(this);
        otherOptionBtn.setOnClickListener(this);
        malfunctionLay.setOnClickListener(this);

        dotSwitchButton.setChecked(false);
        dotSwitchButton.setVisibility(View.VISIBLE);
        dayNightLay.setVisibility(View.GONE);
        otherOptionBtn.setVisibility(View.VISIBLE);

        dotSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked && buttonView.isPressed()) {

                  //  if(constants.isActionAllowed(getActivity())) {
                        if (SharedPref.IsDOT(getActivity()) == false) {
                            dotWithData();
                        } else {
                            SharedPref.SetDOTStatus(false, getActivity());
                        }
                   /* }else{
                        dotSwitchButton.setChecked(false);
                        Global.EldScreenToast(settingsMenuBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }*/

                } else {
                    dotSwitchButton.setChecked(false);
                }
            }
        });


        if (UILApplication.getInstance().isNightModeEnabled())
            dayNightButton.setImageResource(R.drawable.day_mode);
        else
            dayNightButton.setImageResource(R.drawable.night_mode);


        titleDesc = "<font color='#2E2E2E'><html>" + getResources().getString(R.string.certify_previous_days_log) + " </html></font>";
        okText = "<font color='#1A3561'><b>" + getResources().getString(R.string.ok) + "</b></font>";

        offsetFromUTC = (int) Global.GetTimeZoneOffSet();
        IsAOBRD = SharedPref.IsAOBRD(getActivity());

        isViolation = SharedPref.IsViolation(getActivity());
        progressD = new ProgressDialog(getActivity());
        progressD.setMessage("Saving ...");

        AddStatesInList();
        initilizeEldView.AddTempRemark();
        GetSavePreferences();
        GetDriversSavedData(false, DriverType);
        GetTripSavedData();
        SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);

        if (SharedPref.GetNewLoginStatus(getActivity()) == false) {
            getDayStartOdometer();
            constants.IS_ELD_ON_CREATE = false;

            if(Constants.IsHomePageOnCreate){
                isCertifySignPending( Constants.IsHomePageOnCreate, false);
            }
        }

        Constants.IsHomePageOnCreate = false;

        /*========= Start Service =============*/
        Constants.isEldHome = false;
        startService();


        IsDOT = SharedPref.IsDOT(getActivity());

        if(IsDOT){
            dotWithData();
        }else {
            // -------------------------- CALL API --------bluetooth------------------
             if(TabAct.isTabActOnCreate) {
                 TabAct.vehicleList = new ArrayList<VehicleModel>();
                 GetDriverStatusPermission(DRIVER_ID, DeviceId, VehicleId);

                 JSONArray inspectionArray = inspectionMethod.getSavedInspectionArray(Integer.valueOf(DRIVER_ID), dbHelper);
                 if (inspectionArray.length() == 0 && Global.isConnected(getActivity())) {
                     GetInspection18Days(DRIVER_ID, DeviceId, SelectedDate, GetInspection18Days);
                 }

                 JSONArray TruckArray = new JSONArray(SharedPref.getInspectionIssues(ConstantsKeys.TruckIssues, getActivity()));
                 if (TruckArray.length() == 0 && Global.isConnected(getActivity())) {
                     GetInspectionDefectsList(DRIVER_ID, DeviceId, Global.PROJECT_ID, VIN_NUMBER);
                 }

                 boolean isDeleted = SharedPref.isNotificationDeleted(getActivity());
                 JSONArray notificationArray = notificationMethod.getSavedNotificationArray(Integer.valueOf(DRIVER_ID), dbHelper);

                 if (notificationArray.length() == 0 && !isDeleted) {
                     GetNotificationLog(DRIVER_ID, DeviceId);
                 }
             }
             TabAct.isTabActOnCreate = false;

        }


        SharedPref.setStartLocation("", "", "", getActivity());
        SharedPref.setEndLocation("", "", "", getActivity());


     emptyTrailerNoAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                try {
                    if (getActivity() != null) {
                        if (TrailorNumber.trim().length() == 0)
                            trailorLayout.startAnimation(emptyTrailerNoAnim);
                        else
                            emptyTrailerNoAnim.cancel();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        OdometerFaceView.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                try {
                    if (getActivity() != null) {
                        if (IsOdometerReading)
                            odometerLay.startAnimation(OdometerFaceView);
                        else
                            OdometerFaceView.cancel();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        exceptionFaceView.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    getExceptionStatus();

                    if(getActivity() != null && !getActivity().isFinishing()){
                            if (isHaulExcptn || isAdverseExcptn || isDeferralOccurred || isAgriException) {
                                excpnEnabledTxtVw.setAlpha(1f);
                                excpnEnabledTxtVw.startAnimation(exceptionFaceView);
                            } else {
                                exceptionFaceView.cancel();
                                excpnEnabledTxtVw.setVisibility(View.GONE);
                                excpnEnabledTxtVw.setAlpha(0f);
                            }
                     }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        connectionStatusAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                try {
                    if(getActivity() != null && !getActivity().isFinishing()) {
                        int obdStatus       = SharedPref.getObdStatus(getActivity());
                        int obdPreference   = SharedPref.getObdPreference(getActivity());

                        if(obdPreference == Constants.OBD_PREF_BLE && obdStatus == Constants.BLE_CONNECTED){
                            connectionStatusImgView.setImageResource(R.drawable.ble_ic);
                            cancelObdIconBlinking();
                        }else if(obdPreference == Constants.OBD_PREF_WIRED && obdStatus == Constants.WIRED_CONNECTED){
                            connectionStatusImgView.setImageResource(R.drawable.obd_active);
                            cancelObdIconBlinking();
                        }else if(obdPreference == Constants.OBD_PREF_WIFI && obdStatus == Constants.WIFI_CONNECTED){
                            connectionStatusImgView.setImageResource(R.drawable.wifi_obd_active);
                            cancelObdIconBlinking();
                        }else{
                            if(SharedPref.getObdPreference(getActivity()) == Constants.OBD_PREF_WIFI && wifiConfig.IsAlsNetworkConnected(getActivity())){
                                connectionStatusAnimation.cancel();
                                connectionStatusImgView.setAlpha(1f);
                                connectionStatusImgView.setColorFilter(getResources().getColor(R.color.colorPrimary));
                            }else {
                                connectionStatusImgView.startAnimation(connectionStatusAnimation);
                            }
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        editLogAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                setMalfnDiagnEventInfo();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }


    private void cancelObdIconBlinking(){
        connectionStatusAnimation.cancel();
        connectionStatusImgView.setAlpha(1f);
        connectionStatusImgView.setColorFilter(getResources().getColor(R.color.colorPrimary));

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }


    void InitilizeTextView(View view) {
        onDutyViolationTV       = (TextView) view.findViewById(R.id.onDutyViolationTV);
        drivingViolationTV      = (TextView) view.findViewById(R.id.drivingViolationTV);
        sleeperViolationTV      = (TextView) view.findViewById(R.id.sleeperViolationTV);
        offDutyViolationTV      = (TextView) view.findViewById(R.id.offDutyViolationTV);

        onDutyTimeTxtVw         = (TextView) view.findViewById(R.id.onDutyTimeTxtVw);
        drivingTimeTxtVw        = (TextView) view.findViewById(R.id.drivingTimeTxtVw);
        sleeperTimeTxtVw        = (TextView) view.findViewById(R.id.sleeperTimeTxtVw);
        offDutyTimeTxtVw        = (TextView) view.findViewById(R.id.offDutyTimeTxtVw);

        onDutyTxtVw             = (TextView) view.findViewById(R.id.onDutyTxtVw);
        drivingTxtVw            = (TextView) view.findViewById(R.id.drivingTxtVw);
        sleeperTxtVw            = (TextView) view.findViewById(R.id.sleeperTxtVw);
        offDutyTxtVw            = (TextView) view.findViewById(R.id.offDutyTxtVw);
        excpnEnabledTxtVw       = (TextView) view.findViewById(R.id.excpnEnabledTxtVw);

        jobTypeTxtVw            = (TextView) view.findViewById(R.id.jobTypeTxtVw);
        jobTimeTxtVw            = (TextView) view.findViewById(R.id.jobTimeTxtVw);
        jobTimeRemngTxtVw       = (TextView) view.findViewById(R.id.jobTimeRemngTxtVw);
        remainingTimeTopTV      = (TextView) view.findViewById(R.id.remainingTimeTopTV);
        currentCycleTxtView     = (TextView) view.findViewById(R.id.currentCycleTxtView);
        timeRemainingTxtVw      = (TextView) view.findViewById(R.id.timeRemainingTxtVw);
        perDayTxtVw             = (TextView) view.findViewById(R.id.perDayTxtVw);
        EldTitleTV              = (TextView) view.findViewById(R.id.EldTitleTV);
        refreshTitleTV          = (TextView) view.findViewById(R.id.refreshTitleTV);

        dateTv                  = (TextView) view.findViewById(R.id.dateTv);
        nameTv                  = (TextView) view.findViewById(R.id.nameTv);
        tractorTv               = (TextView) view.findViewById(R.id.tractorTv);
        trailorTv               = (TextView) view.findViewById(R.id.trailorTv);
        coDriverTv              = (TextView) view.findViewById(R.id.coDriverTv);
        otherOptionBadgeView    = (TextView) view.findViewById(R.id.otherOptionBadgeView);
        invisibleTxtVw          = (TextView) view.findViewById(R.id.invisibleTxtVw);

        DriverComNameTV         = (TextView) view.findViewById(R.id.DriverComNameTV);
        coDriverComNameTV       = (TextView) view.findViewById(R.id.coDriverComNameTV);

        asPerShiftOnDutyTV         = (TextView) view.findViewById(R.id.asPerShiftOnDutyTV);
        asPerShiftDrivingTV       = (TextView) view.findViewById(R.id.asPerShiftDrivingTV);
        asPerDateSleepTV         = (TextView) view.findViewById(R.id.asPerDateSleepTV);
        asPerDateOffDutyTV       = (TextView) view.findViewById(R.id.asPerDateOffDutyTV);
        malfunctionTV            = (TextView) view.findViewById(R.id.malfunctionTV);

        refreshTitleTV.setVisibility(View.VISIBLE);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();


        /*int minDiff = Constants.getMinDiff("2022-03-14T11:51:45", "2022-03-16T06:12:45");
        int MINNN =  Constants.getMinDiff(Globally.getDateTimeObj("2022-03-14T11:51:45", false),
                Globally.getDateTimeObj("2022-03-16T06:12:45", false));
*/
        if(constants == null) {
            try {
                initView(rootView);
            } catch (JSONException e) {
                e.printStackTrace();
                LogoutUser();
            }

        }

        try {
            if (!constants.CheckGpsStatus(getActivity()))
                gpsRequest.EnableGPSAutoMatically();

            if (getActivity() != null) {
                if (SharedPref.IsOdometerFromOBD(getActivity())) {
                    odometerLay.setVisibility(View.INVISIBLE);
                }
                settingsMenuBtn.setVisibility(View.VISIBLE);
                logoutMenuBtn.setVisibility(View.VISIBLE);
            }

            setExceptionView();
            setObdStatus(false);

            offsetFromUTC = (int) Global.GetTimeZoneOffSet();
            IsPersonalUseAllowed = SharedPref.IsPersonalAllowed(getActivity());

            EnableJobViews();
        }catch (Exception e){}

        // Update UI from service listener
        ServiceReceiverUpdate();

        Constants.IS_ACTIVE_ELD = true;
        SaveRequestCount    = 0;
        isViolation         = SharedPref.IsViolation(getActivity());
        SelectedDate        = Global.GetCurrentDeviceDate();
        boolean isConnected = Global.isConnected(getContext());
        IsAOBRDAutomatic    = SharedPref.IsAOBRDAutomatic(getActivity());
        IsAOBRD             = SharedPref.IsAOBRD(getActivity());

        RestartTimer();
        Globally.hideSoftKeyboard(getActivity());
        GetSavePreferences();


        if(UnidentifiedActivity.isUnIdentifiedRecordClaimed){
            GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
        }

       checkDriverTimeZone(isConnected);



        //---------------- temp delete last item code ---------------

      /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
              try {
                    driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
                    driverLogArray.remove(driverLogArray.length()-1);
                    driverLogArray.remove(driverLogArray.length()-1);
                    hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, driverLogArray); // saving in db after updating the array
                } catch (Exception e) {
                    e.printStackTrace();
                    driverLogArray = new JSONArray();
                }
            }*/


        SetDataInView();
        IsLogShown = false;
        IsVehicleDialogShown = false;

        loadOnCreateView(isConnected);

        if(isUpdateDriverLog){
            isUpdateDriverLog = false;
            CalculateTimeInOffLine(false, false);

            GetDriversSavedData(false, DriverType);
             if (Global.isConnected(getActivity())) {
                 if (DriverJsonArray.length() > 0 && !IsSaveOperationInProgress) {
                     IsPrePost = false;
                     if (SaveRequestCount < 2) {
                         SAVE_DRIVER_STATUS();
                     }
                 }
             }
        }else{
            if(drivingTimeTxtVw.getText().toString().equals(getString(R.string.time_default)) && isTimeDefault){    // call method again (only once) to refresh view with updaed time
                isTimeDefault = false;
                CalculateTimeInOffLine(false, false);
            }
        }
     //

        if(!isSingleDriver) {
            oldStatusView = DRIVER_JOB_STATUS;
            DriverStatusId = ""+DRIVER_JOB_STATUS;
        }

        IsLogApiRefreshed = false;
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver( progressReceiver, new IntentFilter(ConstantsKeys.IsIgnitionOn));


        SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);

        confirmDeferralRuleDays();

        if(VIN_NUMBER.length() > 0) {
            boolean isStorageMalfunction = malfunctionDiagnosticMethod.isStorageMalfunction(dbHelper, getActivity());
            int AvailableStorageSpace = constants.getAvailableSpace();
            if (AvailableStorageSpace <= 200 && !isStorageMalfunction) {    // 200 mb
                Constants.isStorageMalfunctionEvent = true;
                SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());
                startService();
            } else {
                if(isStorageMalfunction) {
                    boolean isStorageMal24HrOld = malfunctionDiagnosticMethod.isStorageMal24HrOldToClear(dbHelper);
                    if (AvailableStorageSpace > 200  && isStorageMal24HrOld) {
                        Constants.isClearStorageMalEvent = true;
                        SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());
                        startService();
                    }
                }
            }
        }

    }






    void getDayStartOdometer(){
        String odoSelectedDay = SharedPref.getSelectedDayForPuOdometer(getActivity());
        String dayStartOdometer = SharedPref.getTotalPUOdometerForDay(getActivity());
        if(dayStartOdometer.equals("0") ||
                !Global.GetCurrentDeviceDate().equals(odoSelectedDay) ){
            getPuOdometerUsedDetail();
        }

    }


    void checkDriverTimeZone(boolean isConnected){
        IsValidTime = Global.isCorrectTime(getActivity() );

        boolean isSavedTimeZoneCorrect = false;
        if(DeviceTimeZone.equalsIgnoreCase(DriverTimeZone) || offsetFromUTC == offSetFromServer){
            isSavedTimeZoneCorrect = true;
        }


        if ( !isSavedTimeZoneCorrect || !IsValidTime ) {
            constants.saveObdData("", "", "", "",
                    "", "Incorrect TimeZone: " + offsetFromUTC +
                            ", ServerOffset: " + offSetFromServer +
                            ", SavedUtcDate: " +  SharedPref.getCurrentUTCTime(getActivity()) +
                            ", DeviceTimeZone: " + DeviceTimeZone + ", DriverTimeZone: " + DriverTimeZone+
                            ", Current: " + Globally.GetCurrentUTCDateTime(), "","","",
                    String.valueOf(-1), "", "", "",
                    DRIVER_ID, dbHelper, driverPermissionMethod, obdUtil);

            showTimeZoneAlert(isConnected, isSavedTimeZoneCorrect, IsValidTime);
        } else {
            if (timeZoneDialog != null && timeZoneDialog.isShowing()) {
                timeZoneDialog.dismiss();
            }
        }
    }

    void setMalfnDiagnEventInfo(){
        try {
            if (getActivity() != null) {
                boolean isMal = SharedPref.isMalfunctionOccur(getActivity());
                boolean isDia = SharedPref.isDiagnosticOccur(getActivity());
                boolean isLocMal = constants.isAllowLocMalfunctionEvent(getActivity());

                if(isMal || isDia || isLocMal || SharedPref.isEngSyncMalfunction(getActivity()) ||
                        SharedPref.isEngSyncDiagnstc(getActivity()) ) {
                    malfunctionLay.setVisibility(View.VISIBLE);
                    malfunctionLay.startAnimation(editLogAnimation);
                    if(isMal && isDia == false) {
                        malfunctionTV.setText(getString(R.string.malfunction_occur));
                        malfunctionLay.setBackgroundColor(getResources().getColor(R.color.colorVoilation));
                    }else if(isMal == false && isDia){
                        malfunctionTV.setText(getString(R.string.diagnostic_occur));
                        malfunctionLay.setBackgroundColor(getResources().getColor(R.color.colorSleeper));
                    }else{
                        malfunctionLay.setBackgroundColor(getResources().getColor(R.color.colorVoilation));
                        malfunctionTV.setText(getString(R.string.malfunction_diag_occur));
                    }
                }else {
                    editLogAnimation.cancel();
                    malfunctionLay.setVisibility(View.GONE);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    void setTitleView(boolean isOdometerFromOBD, boolean isExemtDriver){
        try {
            if (IsAOBRD) {
                EldTitleTV.setText(getString(R.string.title_aobrd));
                dotSwitchButton.setVisibility(View.GONE);
            } else {
                dotSwitchButton.setVisibility(View.VISIBLE);
                if(isExemtDriver){
                    EldTitleTV.setText(getString(R.string.title_eld_exempt));
                }else{
                    EldTitleTV.setText(getString(R.string.title_eld));
                }

            }

            if(isOdometerFromOBD){
                odometerLay.setVisibility(View.INVISIBLE);
            }else{
                odometerLay.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }

    }


    void setObdStatus(final boolean isToastShowing){

        try {
            if(getActivity() != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        connectionStatusImgView.setColorFilter(null);
                        int obdStatus       = SharedPref.getObdStatus(getActivity());
                        int obdPreference   = SharedPref.getObdPreference(getActivity());

                        if(obdPreference == Constants.OBD_PREF_BLE){
                            connectionStatusImgView.setImageResource(R.drawable.ble_ic);

                            if(obdStatus == Constants.BLE_CONNECTED){
                                connectionStatusImgView.setColorFilter(getResources().getColor(R.color.colorPrimary));
                                if (isToastShowing) {
                                    Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.ble_active), getResources().getColor(R.color.color_eld_theme) );
                                }
                            }else{
                                connectionStatusImgView.setColorFilter(getResources().getColor(R.color.spinner_blue));
                                connectionStatusImgView.startAnimation(connectionStatusAnimation);
                                if (isToastShowing) {
                                    Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.ble_inactive), getResources().getColor(R.color.colorSleeper) );
                                }
                            }
                        }else if(obdPreference == Constants.OBD_PREF_WIRED){
                            if(obdStatus == Constants.WIRED_CONNECTED){
                                connectionStatusImgView.setImageResource(R.drawable.obd_active);
                                if (isToastShowing) {
                                    Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.wired_active), getResources().getColor(R.color.color_eld_theme) );
                                }
                            }else{
                                connectionStatusImgView.setImageResource(R.drawable.obd_inactive);
                                connectionStatusImgView.startAnimation(connectionStatusAnimation);

                                if (isToastShowing) {
                                    if(obdStatus == Constants.WIRED_DISCONNECTED)
                                        Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.wired_inactive), getResources().getColor(R.color.colorSleeper) );
                                    else if (obdStatus == Constants.WIRED_ERROR)
                                        Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.wired_conn_error), getResources().getColor(R.color.colorSleeper) );
                                    else
                                        Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.engine_ign_off_state), getResources().getColor(R.color.colorSleeper) );
                                }
                            }
                        }else{
                            connectionStatusImgView.setImageResource(R.drawable.wifi_obd_active);

                            if(wifiConfig.IsAlsNetworkConnected(getActivity())) {
                                connectionStatusImgView.setColorFilter(getResources().getColor(R.color.colorPrimary));
                                if (isToastShowing) {
                                    Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.wifi_active), getResources().getColor(R.color.color_eld_theme));
                                }
                            }else {
                                connectionStatusImgView.setColorFilter(getResources().getColor(R.color.spinner_blue));
                                connectionStatusImgView.startAnimation(connectionStatusAnimation);
                                if (isToastShowing) {
                                    if(wifiConfig.isWifiEnabled(getActivity())){
                                        Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.Limited_ELD_failed_to_rec), getResources().getColor(R.color.colorSleeper));
                                    }else{
                                        Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.wifi_status_disabled), getResources().getColor(R.color.colorSleeper));
                                    }


                                }
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void getExceptionStatus(){
        if(DriverType == Constants.MAIN_DRIVER_TYPE) {
            isHaulExcptn        = SharedPref.get16hrHaulExcptn(getActivity());
            isAdverseExcptn     = SharedPref.getAdverseExcptn(getActivity());
            isUnIdentifiedOccur = SharedPref.isUnidentifiedOccur(getActivity());
            isUnIdentifiedAlert = SharedPref.getUnidentifiedAlertViewStatus(getActivity());
        }else{
            isHaulExcptn        = SharedPref.get16hrHaulExcptnCo(getActivity());
            isAdverseExcptn     = SharedPref.getAdverseExcptnCo(getActivity());
            isUnIdentifiedOccur = SharedPref.isUnidentifiedOccurCo(getActivity());
            isUnIdentifiedAlert = SharedPref.getUnidentifiedAlertViewStatusCo(getActivity());
        }

        isDeferralOccurred = constants.isDeferralOccurred(DRIVER_ID, MainDriverId, getActivity());
        isAgriException = SharedPref.getAgricultureExemption(getActivity());
    }


    void setExceptionView(){
        getExceptionStatus();

        if (isHaulExcptn || isAdverseExcptn || isDeferralOccurred || isAgriException) {
            excpnEnabledTxtVw.startAnimation(exceptionFaceView);
            excpnEnabledTxtVw.setVisibility(View.VISIBLE);

            if(isHaulExcptn){
                excpnEnabledTxtVw.setText(getString(R.string.short_haul_excp_enabled));
            }else if(isAdverseExcptn){
                excpnEnabledTxtVw.setText(getString(R.string.adverse_excp_enabled));
            }else if(isDeferralOccurred){
                int deferralDays = constants.getCurrentDeferralDayCount(DRIVER_ID, MainDriverId, getActivity());
                if(deferralDays != 2){
                    deferralDays = 1;
                }
                excpnEnabledTxtVw.setText("OFF DUTY DEFERRAL (DAY " + deferralDays + ")");
            }else if(isAgriException){
                excpnEnabledTxtVw.setText(getString(R.string.agri_excp_enabled));
            }else{
                excpnEnabledTxtVw.setVisibility(View.GONE);
            }

        }else{
            excpnEnabledTxtVw.setVisibility(View.GONE);
        }

        boolean isMalfunction = SharedPref.isMalfunctionOccur(getActivity()) || SharedPref.isDiagnosticOccur(getActivity())||
                SharedPref.isUnidentifiedOccur(getActivity());
        if(isUnIdentifiedOccur || isHaulExcptn || isAdverseExcptn || isMalfunction){
            eldMenuErrorImgVw.setVisibility(View.VISIBLE);
        }else{
            eldMenuErrorImgVw.setVisibility(View.GONE);
        }


        if(isUnidentifiedAllowed() && isUnIdentifiedOccur && isUnIdentifiedAlert &&
                SharedPref.GetNewLoginStatus(getActivity()) == false && SharedPref.IsDOT(getActivity()) == false){
            try {
                if (confirmationDialog != null && confirmationDialog.isShowing()){
                    //  confirmationDialog.dismiss();
                }else{
                    if(getActivity() != null && !getActivity().isFinishing()) {
                        confirmationDialog = new ConfirmationDialog(getActivity(), Constants.AlertUnidentified, new ConfirmListener());
                        confirmationDialog.show();
                    }
                }


            }catch (Exception e){e.printStackTrace();}
        }

    }



    void showTimeZoneAlert(boolean isConnected, boolean isTimeZoneValid, boolean isTimeValid){
        try {

            if (DRIVER_ID.length() > 0) {

                    if (SharedPref.GetNewLoginStatus(getActivity())) {
                        loadOnCreateView(isConnected);
                    }

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        try {
                            if (timeZoneDialog != null && timeZoneDialog.isShowing()) {
                                timeZoneDialog.dismiss();
                            }

                            timeZoneDialog = new TimeZoneDialog(getActivity(), isTimeZoneValid, isTimeValid);
                            timeZoneDialog.show();

                        } catch (final IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    LogoutUser();
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    boolean isUnidentifiedAllowed(){
        boolean isUnidentifiedAllowed = false;
        if(DriverType == Constants.MAIN_DRIVER_TYPE) {
            isUnidentifiedAllowed = SharedPref.IsShowUnidentifiedRecords(getActivity());
        }else{
            isUnidentifiedAllowed = SharedPref.IsShowUnidentifiedRecordsCo(getActivity());
        }
        return isUnidentifiedAllowed;
    }


    void dotWithData(){
        SharedPref.SetDOTStatus(true, getActivity());

        String dayOfTheWeek = Global.GetDayOfWeek(SelectedDate);
        int mnth = Integer.valueOf(SelectedDate.substring(0, 2));
        String MonthFullName = Global.MONTHS_FULL[mnth - 1];
        String MonthShortName = Global.MONTHS[mnth - 1];
        isCertifyLog = false;
        moveToDotMode(SelectedDate, dayOfTheWeek, MonthFullName, MonthShortName, CurrentCycleId);


    }


    void moveToDotMode(String date, String dayName, String dayFullName, String dayShortName, String cycle){
        FragmentManager fragManager = getActivity().getSupportFragmentManager();

        Fragment dotFragment;
        if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
            dotFragment = new DotCanadaFragment();
        }else {
            dotFragment = new DotUsaFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("day_name", dayName);
        bundle.putString("month_full_name", dayFullName);
        bundle.putString("month_short_name", dayShortName);
        bundle.putString("cycle", cycle);

        dotFragment.setArguments(bundle);

        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, dotFragment);
        fragmentTran.addToBackStack(null);  //"dot_log"
        fragmentTran.commit();

    }


    void moveToHosSummary(){

        // get driver's current cycle id
        GetDriverCycle();

        try {
            FragmentManager fragManager = getActivity().getSupportFragmentManager();
            Bundle bundle = new Bundle();
            HosSummaryFragment hosFragment = new HosSummaryFragment();

            bundle.putString("DriverId", DRIVER_ID);
            bundle.putString("DeviceId", DeviceId);
            bundle.putString("CycleId", CurrentCycleId);
            bundle.putString("driverLogArray", driverLogArray.toString());
           // bundle.putString("currentDayArray", currentDayArray.toString());

            bundle.putString("cycle", CurrentCycle);
            bundle.putString("break_used", CurrentCycle);
            bundle.putString("break_total", CurrentCycle);
            bundle.putString("isPersonal", isPersonal);

            bundle.putBoolean("isSingleDriver", isSingleDriver);
            bundle.putBoolean("IsAOBRDAutomatic", IsAOBRDAutomatic);

            bundle.putInt("current_status", DRIVER_JOB_STATUS);
            bundle.putInt("left_cycle", LeftWeekOnDutyHoursInt);
            bundle.putInt("left_onduty", LeftDayOnDutyHoursInt);
            bundle.putInt("left_driving", LeftDayDrivingHoursInt);
            bundle.putInt("total_onduty", TotalOnDutyHoursInt);
            bundle.putInt("total_driving", TotalDrivingHoursInt);
            bundle.putInt("total_offduty", OffDutyPerShift);
            bundle.putInt("total_sleeper", SleeperPerShift);
            bundle.putInt("shift_used", shiftUsedMinutes);
            bundle.putInt("shift_remain", shiftRemainingMinutes);
            bundle.putInt("offsetFromUTC", offsetFromUTC);

            bundle.putInt("offDuty_used_hours", minOffDutyUsedHours);
            bundle.putBoolean("is_offDuty_hr_satisfied", isMinOffDutyHoursSatisfied);

            hosFragment.setArguments(bundle);


            FragmentTransaction fragmentTran = fragManager.beginTransaction();
            fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTran.add(R.id.job_fragment, hosFragment);
            fragmentTran.addToBackStack("dot_log");
            fragmentTran.commit();

        }catch (Exception e){
            e.printStackTrace();
        }

    }




    void loadOnCreateView(boolean isConnected){
        if (!IsOnCreateView) {

            if (!isUpdateDriverLog) {
                GetDriversSavedData(false, DriverType);
               /* try {
                    driverLogArray = new JSONArray();
                    driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
                } catch (Exception e) {
                    e.printStackTrace();
                    driverLogArray = new JSONArray();
                }*/

                CalculateTimeInOffLine(false, true);

                try {
                    if (Global.isConnected(getActivity())) {
                        if (SharedPref.GetNewLoginStatus(getActivity())) {
                            if (TabAct.vehicleList.size() == 0)
                                GetOBDAssignedVehicles(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()), DeviceId, DriverCompanyId, VIN_NUMBER);
                        }
                        if (DriverJsonArray.length() > 0 && !IsSaveOperationInProgress) {
                            IsPrePost = false;
                            SAVE_DRIVER_STATUS();
                        } else {
                            IsLogShown = false;
                            IsRecapShown = false;
                        }

                        if (shipmentMethod.getShipment18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper).length() == 0) {
                            GetShipment18Days(DRIVER_ID, DeviceId, SelectedDate, GetShipment18Days);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            onResumeDataSet(isConnected);

        }
    }


    void onResumeDataSet(boolean isConnected){
        try {
            // --------- Call API-------------
            if (isConnected) {
                if (SharedPref.GetNewLoginStatus(getActivity())) {
                    if (TabAct.vehicleList.size() == 0) {
                        GetOBDAssignedVehicles(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()), DeviceId, DriverCompanyId, VIN_NUMBER);
                    }
                }

            }

            DateTime lastDataSavedDate;
            if (SharedPref.getSavedDateTime(getActivity()).length() > 0) {
                lastDataSavedDate = Global.getDateTimeObj(SharedPref.getSavedDateTime(getActivity()), false);
            } else {
                lastDataSavedDate = Global.getDateTimeObj(Global.GetCurrentDateTime(), false);
            }
            // Current Date Time
            DateTime currentUTCTime = Global.getDateTimeObj(Global.GetCurrentDateTime(), true);
            if (hMethods.DayDiff(currentUTCTime, lastDataSavedDate) > 2 ||
                    (driverLogArray == null || driverLogArray.length() == 0)) {
                if (TeamDriverType.equals(DriverConst.TeamDriver)) {
                    if (isConnected) {
                        GetDriverLog18Days(MainDriverId, GetDriverLog18Days);
                        GetDriverLog18Days(CoDriverId, GetCoDriverLog18Days);
                    }
                } else {
                    if (isConnected)
                        GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
                }
            } else {
                CalculateTimeInOffLine(false, true);
            }


            if (isConnected) {
                if (TeamDriverType.equals(DriverConst.TeamDriver)) {
                    if (shipmentMethod.getShipment18DaysArray(Integer.valueOf(MainDriverId), dbHelper).length() == 0)
                        GetShipment18Days(MainDriverId, DeviceId, SelectedDate, GetShipment18Days);

                    if (shipmentMethod.getShipment18DaysArray(Integer.valueOf(CoDriverId), dbHelper).length() == 0)
                        GetShipment18Days(CoDriverId, DeviceId, SelectedDate, GetShipment18DaysCo);

                    if (odometerhMethod.getSavedOdometer18DaysArray(Integer.valueOf(MainDriverId), dbHelper).length() == 0)
                        GetOdometer18Days(MainDriverId, DeviceId, DriverCompanyId, SelectedDate);

                    if (odometerhMethod.getSavedOdometer18DaysArray(Integer.valueOf(CoDriverId), dbHelper).length() == 0)
                        GetOdometer18Days(CoDriverId, DeviceId, DriverCompanyId, SelectedDate);
                } else {
                    if (shipmentMethod.getShipment18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper).length() == 0)
                        GetShipment18Days(DRIVER_ID, DeviceId, SelectedDate, GetShipment18Days);

                    if (odometerhMethod.getSavedOdometer18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper).length() == 0)
                        GetOdometer18Days(DRIVER_ID, DeviceId, DriverCompanyId, SelectedDate);
                }
                 if(SharedPref.IsOdometerFromOBD(getActivity())) {
                     GetOdometerReading(DRIVER_ID, DeviceId, VIN_NUMBER, Global.GetCurrentDeviceDate());
                 }
            }

            if (isPersonal.equals("true")) {
                SharedPref.OdometerSaved(false, getActivity());
            }

            if (SharedPref.GetNewLoginStatus(getActivity()))
                IsRecapShown = false;
            else {
                IsRecapShown = true;
            }

            IsOnCreateView = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private class MyTimerTask extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run() {
           // Log.e("Log", "----TimerTask EldFragment");
            strCurrentDate = Global.getCurrentDate();
            SelectedDate = Global.GetCurrentDeviceDate();

            if(Global.isCorrectTime(getActivity())){
                if (timeZoneDialog != null && timeZoneDialog.isShowing()) {
                    timeZoneDialog.dismiss();
                }
            }

            setObdStatus(false);

            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(!constants.CheckGpsStatus(getActivity())) {
                            ClearGPSData();
                            gpsRequest.EnableGPSAutoMatically();
                        }

                        GetDriversSavedData(false, DriverType);
                        // if(Global.CompareSavedWithCurrentDate(getActivity())) {
                        if (Global.isConnected(getActivity())) {
                            if (DriverJsonArray.length() > 0 && !IsSaveOperationInProgress) {
                                IsPrePost = false;
                                if (SaveRequestCount < 2) {
                                    SAVE_DRIVER_STATUS();
                                }else{
                                    CalculateTimeInOffLine(false, false);
                                }
                            } else {
                                IsLogShown = false;
                                IsRecapShown = false;
                                if (driverLogArray.length() > 0) {
                                    CalculateTimeInOffLine(false, false);
                                } else {
                                    GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
                                }
                            }
                        } else {
                            SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);
                            if (driverLogArray.length() > 0) {
                                CalculateTimeInOffLine(false, false);
                            }
                        }

                        if(SharedPref.IsOdometerFromOBD(getActivity())){
                            odometerLay.setVisibility(View.INVISIBLE);
                        }else{
                            odometerLay.setVisibility(View.VISIBLE);
                        }

                        setDateOnView(SelectedDate);

                        confirmDeferralRuleDays();

                    }
                });
            } catch (Exception e) {
            }

        }
    }


    private void ServiceReceiverUpdate(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(packageName);
        getActivity(). registerReceiver(updateReceiver, intentFilter);
    }

    public class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                 Bundle extras = intent.getExtras();
                String state = extras.getString("isUpdate");

                if(state != null && state.equals("true")){
                    invisibleTxtVw.performClick();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }


    private void RestartTimer() {
        try {
            clearTimer();
            mTimer = new Timer();
            timerTask = new MyTimerTask();
            mTimer.schedule(timerTask, MIN_TIME_BW_UPDATES, MIN_TIME_BW_UPDATES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void AddStatesInList() {
        int stateListSize = 0;
        StateArrayList = new ArrayList<String>();
        StateList = new ArrayList<DriverLocationModel>();

        try {
            StateList = statePrefManager.GetState(getActivity());
            StateList.add(0, new DriverLocationModel("", "Select", ""));
            stateListSize = StateList.size();
        } catch (Exception e) {
            stateListSize = 0;
        }

        for (int i = 0; i < stateListSize; i++) {
            StateArrayList.add(StateList.get(i).getState());
        }

        if (stateListSize == 0) {
            LogoutUser();
        }
    }




    boolean isPendingNotifications(int JobStatus){
        pendingNotificationCount = constants.getPendingNotifications(DriverType, notificationPref, coNotificationPref, getActivity());
        boolean isPending = false;
        if(pendingNotificationCount > 0 && (JobStatus == DRIVING || JobStatus == ON_DUTY)){
            isPending = true;
        }
        return isPending;
    }


    void checkNotificationAlert(){
        try {
            if (getActivity() != null) {
                boolean isPendingNotifications = false;
                if(DriverType == Constants.MAIN_DRIVER_TYPE){
                    boolean isMal = SharedPref.isMalfunctionOccur(getActivity());
                    boolean isDia = SharedPref.isDiagnosticOccur(getActivity());
                    isPendingNotifications = isPendingNotifications(DRIVER_JOB_STATUS) ||
                            SharedPref.isSuggestedEditOccur(getActivity()) ||
                            SharedPref.isUnidentifiedOccur(getActivity()) ||
                            isMal ||
                            isDia ||
                             constants.isAllowLocMalfunctionEvent(getActivity()) ||
                    constants.CheckGpsStatusToCheckMalfunction(getActivity()) == false;

                         /*   || (SharedPref.getObdStatus(getActivity()) != Constants.WIFI_CONNECTED &&
                            SharedPref.getObdStatus(getActivity()) != Constants.WIRED_CONNECTED)
                           */

                }else{
                    isPendingNotifications = isPendingNotifications(DRIVER_JOB_STATUS) ||
                            SharedPref.isSuggestedEditOccurCo(getActivity()) ||
                            SharedPref.isUnidentifiedOccurCo(getActivity()) ||
                            SharedPref.isMalfunctionOccurCo(getActivity()) ||
                            SharedPref.isDiagnosticOccurCo(getActivity()) ||
                            constants.CheckGpsStatusToCheckMalfunction(getActivity()) == false ||
                            constants.isAllowLocMalfunctionEvent(getActivity()) ;

                  /*  || (SharedPref.getObdStatus(getActivity()) != Constants.WIFI_CONNECTED &&
                            SharedPref.getObdStatus(getActivity()) != Constants.WIRED_CONNECTED)
                         */
                }

                JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DRIVER_ID), dbHelper);
                boolean isUnCertified = constants.GetCertifyLogSignStatus(recapViewMethod, DRIVER_ID, dbHelper, SelectedDate, CurrentCycleId, logPermissionObj);
                boolean isMissingLoc = constants.isLocationMissing(DRIVER_ID, CurrentCycleId, driverPermissionMethod,
                        recapViewMethod, Global, hMethods, dbHelper, logPermissionObj, getActivity());

                if (isPendingNotifications | isUnCertified || isMissingLoc || !Constants.isValidVinFromObd(SharedPref.getIgnitionStatus(getActivity()), getActivity())) {
                    otherOptionBadgeView.setVisibility(View.VISIBLE);
                } else {
                    otherOptionBadgeView.setVisibility(View.GONE);
                }

                setMalfnDiagnEventInfo();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void GetSavePreferences() {

        try {
            IsLogApiACalled = false;
            Global.hideSoftKeyboard(getActivity());

            DeviceId = SharedPref.GetSavedSystemToken(getActivity());
            CountryCycle = SharedPref.getCountryCycle("CountryCycle", getActivity());
            CompanytimeZone = SharedPref.getTimeZone(getActivity());
            UtcTimeZone = SharedPref.getUTCTimeZone("utc_time_zone", getActivity());
            VIN_NUMBER = SharedPref.getVINNumber( getActivity());
            TrailorNumber = SharedPref.getTrailorNumber(getActivity());
            TeamDriverType = SharedPref.getDriverType(getActivity());

            if(TrailorNumber.equalsIgnoreCase("null")) {
                TrailorNumber = "";
            }

            isPersonalOld = isPersonal;

            MainDriverName = DriverConst.GetDriverDetails(DriverConst.DriverName, getActivity());
            CoDriverName = DriverConst.GetCoDriverDetails(DriverConst.CoDriverName, getActivity());
            VehicleId = SharedPref.getVehicleId(getActivity());

            MainDriverId = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
            CoDriverId = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());

            strCurrentDate = Global.getCurrentDate();
            CompanytimeZone = SharedPref.getTimeZone(getActivity());
            UtcTimeZone = SharedPref.getUTCTimeZone("utc_time_zone", getActivity());

            SelectedDate = Global.GetCurrentDeviceDate();
            setDateOnView(SelectedDate);

            DeviceTimeZone = TimeZone.getDefault().getDisplayName();
            IsNorthCanada  =  SharedPref.IsNorthCanada(getActivity());


            try {
                if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
                    DriverType = Constants.MAIN_DRIVER_TYPE;     // Single Driver Type and Position is 0
                    SavedCanCycle = DriverConst.GetDriverSettings(DriverConst.CANCycleName, getActivity());
                    SavedUsaCycle = DriverConst.GetDriverSettings(DriverConst.USACycleName, getActivity());
                    isDeferral      = SharedPref.isDeferralMainDriver(getActivity());

                    DRIVER_ID = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
                    SharedPref.setDriverId(DRIVER_ID, getActivity());
                    DriverTimeZone = DriverConst.GetDriverSettings(DriverConst.DriverTimeZone, getActivity());
                    String offset = DriverConst.GetDriverSettings(DriverConst.OffsetHours, getActivity());
                    if (offset.length() > 0) {
                        offSetFromServer = Integer.valueOf(offset);
                    }

                } else {     // If Current driver is Co Driver
                    DriverType = Constants.CO_DRIVER_TYPE;     // Co Driver Type and Position is 1
                    SavedCanCycle = DriverConst.GetCoDriverSettings(DriverConst.CoCANCycleName, getActivity());
                    SavedUsaCycle = DriverConst.GetCoDriverSettings(DriverConst.CoUSACycleName, getActivity());
                    DriverCompanyId = DriverConst.GetCoDriverDetails(DriverConst.CoCompanyId, getActivity());
                    isDeferral      = SharedPref.isDeferralCoDriver(getActivity());

                    DRIVER_ID = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
                    SharedPref.setDriverId(DRIVER_ID, getActivity());
                    DriverTimeZone = DriverConst.GetCoDriverSettings(DriverConst.CoDriverTimeZone, getActivity());

                    String offset = DriverConst.GetCoDriverSettings(DriverConst.CoOffsetHours, getActivity());
                    if (offset.length() > 0) {
                        offSetFromServer = Integer.valueOf(offset);
                    }

                }

                DriverCompanyId = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
                TruckNumber = SharedPref.getTruckNumber(getActivity());

            }catch (Exception e){
                e.printStackTrace();
            }

            DRIVER_ID = SharedPref.getDriverId(getActivity());
            DriverStatusId = SharedPref.getDriverStatusId(getActivity()).trim();

            try {
                if (DRIVER_ID.equalsIgnoreCase("null") || DRIVER_ID.equals(""))
                    DRIVER_ID = "0";

                driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
            } catch (Exception e) {
                e.printStackTrace();
                driverLogArray = new JSONArray();
            }
            if (DriverStatusId.equalsIgnoreCase("null") || DriverStatusId.equals(""))
                DriverStatusId = "1";
            oldStatusView = Integer.valueOf(DriverStatusId);


        } catch (Exception e) {
            e.printStackTrace();
        }


        if(Global.isSingleDriver(getActivity()) == false ){
            if(DRIVER_ID.equals(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()))){
                CoDriverIdInSaveStatus   = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
                CoDriverNameInSaveStatus = DriverConst.GetCoDriverDetails(DriverConst.CoDriverName, getActivity());
            }else{
                CoDriverIdInSaveStatus   = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
                CoDriverNameInSaveStatus = DriverConst.GetDriverDetails(DriverConst.DriverName, getActivity());
            }
        }

    }


    void setDateOnView(String SelectedDate){
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(cal.getTime());
            dateTv.setText(month_name + " " + SelectedDate.substring(3, 5));   //+ " " + SelectedDate.substring(6, SelectedDate.length())
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void GetDriverCycle(){
        try{
            CurrentCycle   = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycle, getActivity());
            CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());

            if (CurrentCycle.equalsIgnoreCase("null") || CurrentCycleId.equals("0")  || CurrentCycleId.length() == 0) {
                CurrentCycle = Global.NO_CYCLE_NAME;
                CurrentCycleId = Global.NO_CYCLE;
            }

            remainingTimeTopTV.setText(Html.fromHtml("Cycle time left <b>" + Global.FinalValue(LeftWeekOnDutyHoursInt) + "</b>" ));
            if (CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) && SharedPref.IsNorthCanada(getActivity())) {
                currentCycleTxtView.setText("Cycle 1 (80/7) (N)");
            }else{
                currentCycleTxtView.setText(CurrentCycle);
            }

            if(CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)){
                cyleFlagImgView.setImageResource(R.drawable.can_flag);
            }else{
                cyleFlagImgView.setImageResource(R.drawable.usa_flag);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    void SetDataInView() {
        DriverCarrierName = DriverConst.GetDriverDetails(DriverConst.Carrier, getActivity());
        CoDriverCarrierName = DriverConst.GetCoDriverDetails(DriverConst.CoCarrier, getActivity());

        getActivity().runOnUiThread(new Runnable(){
            public void run() {
                tractorTv.setText(TruckNumber);
                trailorTv.setText(TrailorNumber);


                if (TrailorNumber.trim().length() == 0)
                    trailorLayout.startAnimation(emptyTrailerNoAnim);
                else
                    emptyTrailerNoAnim.cancel();

                if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                    nameTv.setText(MainDriverName);
                    coDriverTv.setText(CoDriverName);
                    DriverComNameTV.setText(DriverCarrierName);
                    coDriverComNameTV.setText(CoDriverCarrierName);
                    isExemptDriver = SharedPref.IsExemptDriverMain(getActivity());
                } else {
                    nameTv.setText(CoDriverName);
                    coDriverTv.setText(MainDriverName);
                    DriverComNameTV.setText(CoDriverCarrierName);
                    coDriverComNameTV.setText(DriverCarrierName);
                    isExemptDriver = SharedPref.IsExemptDriverCo(getActivity());
                }

                GetDriverCycle();

                setTitleView(SharedPref.IsOdometerFromOBD(getActivity()), isExemptDriver);

                try {
                    if (Global.isSingleDriver(getActivity())) {
                        coDriverImgView.setVisibility(View.GONE);
                        isSingleDriver = true;
                    } else {
                        coDriverImgView.setVisibility(View.VISIBLE);
                        isSingleDriver = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    void GetTripSavedData() {

        TruckNumber = SharedPref.getTruckNumber(getActivity());
        VIN_NUMBER = SharedPref.getVINNumber(getActivity());   //DriverConst.GetDriverTripDetails(DriverConst.VIN, getActivity());

        SetDataInView();
    }


    // Call API to save driver job status ..................
    void SAVE_DRIVER_STATUS() {
        int socketTimeout ;
        int logArrayCount = DriverJsonArray.length();
        if(logArrayCount < 3 ){
            socketTimeout = constants.SocketTimeout10Sec;  //10 seconds
        }else if(logArrayCount < 10){
            socketTimeout = constants.SocketTimeout20Sec;  //20 seconds
        }else{
            socketTimeout = constants.SocketTimeout40Sec;  //40 seconds
        }

        SaveRequestCount++;
        isFirst = false;
        IsPopupDismissed = false;
        IsSaveOperationInProgress = true;

        String SavedLogApi = "";
        if(SharedPref.IsEditedData(getActivity())){
            SavedLogApi = APIs.SAVE_DRIVER_EDIT_LOG_NEW;
        }else{
            SavedLogApi = APIs.SAVE_DRIVER_STATUS;
        }

        saveDriverLogPost.PostDriverLogData(DriverJsonArray, SavedLogApi, socketTimeout, false, false, DriverType, MainDriverLog);

        constants.saveObdData("", "ELD Home SaveLogApi Status: " +DRIVER_JOB_STATUS, "",
                "-1", "", "", "", "", "-1",
                "-1", "", "", "",
                DRIVER_ID, dbHelper, driverPermissionMethod, obdUtil);


        //  SAVE_DRIVER_STATUS(DriverJsonArray, false, false, socketTimeout);

    }


    void SetJobButtonView(int job, boolean isViolation, String IsPersonal) {

        try {
            if(getActivity() != null){
                 switch (job) {
                case OFF_DUTY:  //1

                    initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    if (IsPersonal.equals("false")) {
                        personalUseBtn.setText(getString(R.string.pc_start));
                        initilizeEldView.ActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, false);
                    } else {
                        initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                        personalUseBtn.setBackgroundResource(R.drawable.eld_blue_new_selector);
                        personalUseBtn.setTextColor(Color.WHITE);
                        personalUseBtn.setText(getString(R.string.pc_end));
                    }

                    yardMoveBtn.setText(getString(R.string.ym_start));
                    StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);

                    break;

                case SLEEPER:  //2

                    initilizeEldView.ActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, false);
                    initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);

                    personalUseBtn.setText(getString(R.string.pc_start));
                    yardMoveBtn.setText(getString(R.string.ym_start));

                    break;

                case DRIVING:   //3

                    initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.ActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, isViolation);
                    initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());

                    personalUseBtn.setText(getString(R.string.pc_start));
                    yardMoveBtn.setText(getString(R.string.ym_start));

                    if (isViolation) {
                        StatusMainView.setBackgroundResource(R.drawable.red_default);
                    } else {
                        StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);
                    }
                    break;

                case ON_DUTY:   //4
                    initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.ActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, isViolation);
                    initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());

                    personalUseBtn.setText(getString(R.string.pc_start));

                    if (isViolation) {
                        StatusMainView.setBackgroundResource(R.drawable.red_default);
                    } else {
                        StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);
                    }

                    break;


                default:
                    initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.ActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, false);
                    StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);
                    personalUseBtn.setText(getString(R.string.pc_start));
                    yardMoveBtn.setText(getString(R.string.ym_start));

                    break;
            }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void setJobButtonViewTrue(int job, String isPersonal) {

        try{
            if(getActivity() != null){
                switch (job) {
            case OFF_DUTY:  //1
                if (isPersonal.equals("true")) {
                    initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed, getActivity());
                    personalUseBtn.setBackgroundResource(R.drawable.green_eld_selector);
                    personalUseBtn.setTextColor(Color.WHITE);
                } else {
                    initilizeEldView.ActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, isViolation);
                }

                break;

            case SLEEPER:   //2
                initilizeEldView.ActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, isViolation);
                // OffDutyBtn.setEnabled(true);
                break;

            case DRIVING:   //3
                initilizeEldView.ActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, isViolation);
                // OffDutyBtn.setEnabled(true);
                break;

            case ON_DUTY:   //4
                initilizeEldView.ActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, isViolation);
                //  OffDutyBtn.setEnabled(true);
                break;

            default:
                initilizeEldView.ActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, isViolation);
                // OffDutyBtn.setEnabled(false);
                break;
        }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    void SaveJobType(String currentJobType) {
        SharedPref.setDriverStatusId(currentJobType, getActivity());
        DriverStatusId = currentJobType;
        DRIVER_JOB_STATUS = Integer.valueOf(currentJobType);
        constants.IsAlreadyViolation = false;
        //  Log.d("---jobTypeSaved", "---jobTypeSaved: "+ currentJobType);
    }


    void DisableJobViews(){
        OnDutyBtn.setEnabled(false);
        DrivingBtn.setEnabled(false);
        OffDutyBtn.setEnabled(false);
        SleeperBtn.setEnabled(false);
        personalUseBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

    }

    void EnableJobViews(){

        try{
            if (otherOptionsDialog != null && otherOptionsDialog.isShowing())
                 otherOptionsDialog.dismiss();
        }catch (Exception e){}

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getActivity() != null && !getActivity().isFinishing()) {
                    OnDutyBtn.setEnabled(true);
                    DrivingBtn.setEnabled(true);
                    OffDutyBtn.setEnabled(true);
                    SleeperBtn.setEnabled(true);
                    personalUseBtn.setEnabled(true);
                    progressBar.setVisibility(View.GONE);


                    setYardMoveView();

                    if (IsPersonalUseAllowed) {
                        if(SharedPref.isPersonalUse75KmCrossed(getActivity())){
                            personalUseBtn.setTextColor(getResources().getColor(R.color.gray_hover));
                        }else {
                            if (DRIVER_JOB_STATUS == constants.OFF_DUTY && isPersonal.equals("true")) {
                                personalUseBtn.setTextColor(getResources().getColor(R.color.whiteee));
                            } else {
                                personalUseBtn.setTextColor(getResources().getColor(R.color.color_eld_theme));
                            }
                        }
                    } else {
                        personalUseBtn.setTextColor(getResources().getColor(R.color.gray_hover));
                    }

                    if (constants.IsSendLog(DRIVER_ID, driverPermissionMethod, dbHelper)) {
                        sendReportBtn.setTextColor(getResources().getColor(R.color.color_eld_theme));
                    } else {
                        sendReportBtn.setTextColor(getResources().getColor(R.color.gray_hover));
                    }

                    JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DRIVER_ID), dbHelper);
                    boolean isUnCertified = constants.GetCertifyLogSignStatus(recapViewMethod, DRIVER_ID, dbHelper, SelectedDate, CurrentCycleId, logPermissionObj);
                    boolean isMissingLoc = constants.isLocationMissing(DRIVER_ID, CurrentCycleId, driverPermissionMethod,
                            recapViewMethod, Global, hMethods, dbHelper, logPermissionObj, getActivity());
                    if (isMissingLoc || isUnCertified) {
                        certifyLogErrorImgVw.setVisibility(View.VISIBLE);
                    } else {
                        certifyLogErrorImgVw.setVisibility(View.GONE);
                    }
                }
            }
        }, 500);

    }

    void setYardMoveView(){
        if(isYardMove){
            yardMoveBtn.setText(getString(R.string.ym_end));
            if (isViolation) {
                yardMoveBtn.setBackgroundResource(R.drawable.red_default);
            }else{
                yardMoveBtn.setBackgroundResource(R.drawable.eld_blue_new_default);
            }
            yardMoveBtn.setTextColor(getResources().getColor(R.color.whiteee));
        }else{
            yardMoveBtn.setText(getString(R.string.ym_start));
            if (SharedPref.IsYardMoveAllowed(getActivity())) {
                yardMoveBtn.setTextColor(getResources().getColor(R.color.color_eld_theme));
            } else {
                yardMoveBtn.setTextColor(getResources().getColor(R.color.gray_hover));
            }
            yardMoveBtn.setBackgroundResource(R.drawable.gray_eld_selector);
        }
    }

    void shareDriverLogDialog() {

        if(getActivity() != null && !getActivity().isFinishing()) {
            try {
                if (shareDialog != null && shareDialog.isShowing()) {
                    shareDialog.dismiss();
                }
                shareDialog = new ShareDriverLogDialog(getActivity(), getActivity(), DRIVER_ID, DeviceId, CurrentCycleId,
                        IsAOBRD, StateArrayList, StateList);
                shareDialog.show();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }




    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.summaryBtn:
                summaryBtn.setEnabled(false);
                moveToHosSummary();

                  //  NotificationNewsDialog newsDialog = new NotificationNewsDialog(getActivity(), new ArrayList<NotificationNewsModel>(), true);
                   // newsDialog.show();

                break;


            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;


            case R.id.shippingLay:
                //if(constants.isActionAllowed(getActivity())){
                if(hMethods.isActionAllowedWhileDriving(getActivity(), Global, DRIVER_ID, dbHelper)){
                    showShippingDialog(false);
                } else {
                    Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                }

                break;

            case R.id.odometerLay:

                if(!SharedPref.IsOdometerFromOBD(getActivity())) {
                    //if(constants.isActionAllowed(getActivity())){
                    if(hMethods.isActionAllowedWhileDriving(getActivity(), Global, DRIVER_ID, dbHelper)){
                        TabAct.host.setCurrentTab(5);
                    } else {
                        Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }
                }
                break;


            case R.id.truckLay:

                 if(constants.isActionAllowedRestricted(getActivity())){
                     if (Global.isConnected(getActivity())) {
                         if (DRIVER_JOB_STATUS == DRIVING ) {
                             Global.EldScreenToast(OnDutyBtn, ConstantsEnum.TRUCK_CHANGE, getResources().getColor(R.color.colorVoilation));
                         } else {
                             IsVehicleDialogShown = true;
                             progressBar.setVisibility(View.VISIBLE);
                             GetOBDAssignedVehicles(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()), DeviceId, DriverCompanyId, VIN_NUMBER);
                         }
                     } else {
                         Global.EldScreenToast(truckLay, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                     }
                } else {
                    Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                }

                break;


            case R.id.trailorLayout:
                try{
                    if(getActivity() != null && !getActivity().isFinishing()){
                      if(constants.isActionAllowedRestricted(getActivity()) ){    //constants.isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID )
                       // if (Global.isConnected(getActivity())) {
                            if (DRIVER_JOB_STATUS == DRIVING ) {
                                Global.EldScreenToast(OnDutyBtn, ConstantsEnum.TRAILER_CHANGE, getResources().getColor(R.color.colorVoilation));
                            } else {
                                if (trailerDialog != null && trailerDialog.isShowing())
                                    trailerDialog.dismiss();

                                trailerDialog = new TrailorDialog(getActivity(), "trailor", false, TrailorNumber, 0, false,
                                        Global.onDutyRemarks, oldStatusView, dbHelper, new TrailorListener());
                                trailerDialog.show();
                            }
                        /*} else {
                            Global.EldScreenToast(truckLay, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                        }*/
                    } else {
                        Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }
                }
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;



            case R.id.sendReportBtn:

                if(constants.IsSendLog(DRIVER_ID, driverPermissionMethod, dbHelper)) {

                    //constants.isActionAllowed(getActivity())
                    if(hMethods.isActionAllowedWhileDriving(getActivity(), Global, DRIVER_ID, dbHelper)){
                        shareDriverLogDialog();
                    } else {
                        Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }
                 }else{
                        Global.EldToastWithDuration4Sec(sendReportBtn, getResources().getString(R.string.share_not_allowed), getResources().getColor(R.color.colorVoilation) );
                 }



                break;

            case R.id.certifyLogBtn:
                isCertifyLog = true;
                ShowDateDialog();
                break;

            case R.id.invisibleTxtVw:
                CalculateTimeInOffLine(false, false);
                break;


            case R.id.calendarBtn:
              //  ShowDateDialog();
                break;


            case R.id.dayNightLay:
                // TabAct.dayNightBtn.performClick();
                break;

            case R.id.settingsMenuBtn:
               // if(constants.isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID ) ) {
                    TabAct.host.setCurrentTab(1);
               /* }else{
                    Global.EldScreenToast(settingsMenuBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                }*/
                break;

            case R.id.logoutMenuBtn:

                Slidingmenufunctions.invisibleLogoutEvent.performClick();

                if (getActivity() != null && !constants.isObdConnectedWithELD(getActivity())) {
                    Global.InternetErrorDialog(getActivity(), true, true);
                }

                break;


            case R.id.otherOptionBtn:

                try {
                    if (getActivity() != null && !getActivity().isFinishing()){

                        if (otherOptionsDialog != null && otherOptionsDialog.isShowing()) {
                            otherOptionsDialog.dismiss();
                        }
                        boolean isPendingNotifications = isPendingNotifications(DRIVER_JOB_STATUS);
                        otherOptionsDialog = new OtherOptionsDialog(getActivity(), isPendingNotifications, pendingNotificationCount,
                                constants.CheckGpsStatusToCheckMalfunction(getActivity()), DriverType, CurrentCycleId, driverPermissionMethod,
                                recapViewMethod, hMethods, dbHelper);
                        otherOptionsDialog.show();
                    }

                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.malfunctionLay:
                TabAct.host.setCurrentTab(12);
                break;


            case R.id.connectionStatusImgView:
                setObdStatus(true);
                break;


            case R.id.rightMenuBtn:

                SharedPref.setObdSpeed("[]", getActivity());

                IsVehicleDialogShown = false;
                loadingSpinEldIV.startAnimation();
                GetSavePreferences();
                boolean isConnected = Global.isConnected(getActivity());
                if (isConnected) {
                    GetDriversSavedData(false, DriverType);
                    if (DriverJsonArray.length() > 0) {
                        if(IsSaveOperationInProgress){
                            Global.EldScreenToast(OnDutyBtn, getString(R.string.data_saving_inProgress), getResources().getColor(R.color.colorVoilation));
                            loadingSpinEldIV.stopAnimation();   // refreshDataEvent(isConnected);
                        }else{
                            IsRefreshedClick = true;
                            SAVE_DRIVER_STATUS();
                        }
                    } else {
                        // Global.EldScreenToast(OnDutyBtn, getString(R.string.already_uploading), getResources().getColor(R.color.color_eld_theme));
                        refreshDataEvent(isConnected);
                    }
                }else{
                    loadingSpinEldIV.stopAnimation();
                    Global.EldScreenToast(OnDutyBtn, Global.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));

                    Constants.isCallMalDiaEvent = true;
                    SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());
                    startService();
                }


                break;


            case R.id.yardMoveBtn:

                MalfunctionDefinition = "";
                if (SharedPref.IsYardMoveAllowed(getActivity())) {
                    if (constants.isObdConnected(getActivity())) {

                        if (DRIVER_JOB_STATUS != DRIVING) {
                            boolean isAllowed;
                            if (isSingleDriver) {
                                isAllowed = constants.isActionAllowed(getActivity());
                            } else {
                                isAllowed = hMethods.isDrivingAllowedWithCoDriver(getActivity(), Global, DRIVER_ID, true, dbHelper);
                            }

                            if (isAllowed) {
                                JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
                                JSONObject lastItemJson = hMethods.GetLastJsonFromArray(driverLogArray);

                                try {
                                    if (DRIVER_JOB_STATUS == ON_DUTY && lastItemJson.getString(ConstantsKeys.YardMove).equals("true")) {
                                        statusEndConfDialog.ShowAlertDialog(getString(R.string.Confirmation_suggested), getString(R.string.WantEndYM),
                                                getString(R.string.yes), getString(R.string.no),
                                                YM_END, positiveCallBack, negativeCallBack);
                                    } else {
                                        isYardBtnClick = true;
                                        OnDutyBtnClick();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                if (isSingleDriver) {
                                    Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                                } else {
                                    String coDriverStatus = hMethods.getCoDriverStatus(getActivity(), DRIVER_ID, Global, dbHelper);
                                    if (coDriverStatus.equals("Yard Move")) {
                                        Global.EldScreenToast(OnDutyBtn, ConstantsEnum.CO_DRIVING_ALSO + coDriverStatus + ConstantsEnum.CO_DRIVING_ALERT1, getResources().getColor(R.color.colorVoilation));
                                    } else {
                                        Global.EldScreenToast(OnDutyBtn, ConstantsEnum.CO_DRIVING_ALERT + coDriverStatus + ConstantsEnum.CO_DRIVING_ALERT1, getResources().getColor(R.color.colorVoilation));
                                    }
                                }
                            }

                        }else{
                            Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.pc_ym_alert_with_dr), getResources().getColor(R.color.colorVoilation));
                        }

                    }else{
                         Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.connect_with_obd_first), getResources().getColor(R.color.colorVoilation));
                    }
                } else {
                      Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.yard_move_not_allowed), getResources().getColor(R.color.colorVoilation));
                }

                break;


            case R.id.personalUseBtn:

                MalfunctionDefinition = "";
                    if (SharedPref.IsPersonalAllowed(getActivity())) {

                        // check device is connected with ECM or not
                        if (constants.isObdConnected(getActivity())) {

                            if (DRIVER_JOB_STATUS != DRIVING){
                                double AccumulativePersonalDistance = constants.getAccumulativePersonalDistance(DRIVER_ID, offsetFromUTC, Global.GetCurrentJodaDateTime(),
                                        Global.GetCurrentUTCDateTime(), hMethods, dbHelper, getActivity());
                            if (AccumulativePersonalDistance < 75) {
                                String pcBtnLabel = personalUseBtn.getText().toString();
                                DateTime lastSaveUtcDate = Global.getDateTimeObj(SharedPref.getCurrentUTCTime(getActivity()), false);
                                DateTime currentUTCTime = Global.getDateTimeObj(Global.GetCurrentDateTime(), true);
                                int dayDiff = hMethods.DayDiff(currentUTCTime, lastSaveUtcDate);
                                boolean is75KmExceeded = SharedPref.isPersonalUse75KmCrossed(getActivity());
                                if ((CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2)) &&
                                        pcBtnLabel.equals(getString(R.string.pc_start)) && dayDiff == 0 && is75KmExceeded) {
                                    Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.personal_use_limit_75),
                                            getResources().getColor(R.color.colorVoilation));
                                } else {

                                    boolean isAllowed;
                                    if (isSingleDriver) {
                                        isAllowed = constants.isActionAllowed(getActivity());
                                    } else {
                                        isAllowed = hMethods.isDrivingAllowedWithCoDriver(getActivity(), Global, DRIVER_ID, true, dbHelper);
                                    }

                                    if (isAllowed) {

                                        if ((CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2)) &&
                                                SharedPref.isPersonalUse75KmCrossed(getActivity()) && dayDiff == 0 &&
                                                !pcBtnLabel.equals(getString(R.string.pc_end))) {
                                            Global.EldToastWithDuration4Sec(personalUseBtn, getResources().getString(R.string.personal_use_limit_75), getResources().getColor(R.color.colorVoilation));
                                        } else {
                                            PersonalBtnClick();
                                        }
                                    } else {
                                        if (isSingleDriver) {
                                            Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                                        } else {
                                            String coDriverStatus = hMethods.getCoDriverStatus(getActivity(), DRIVER_ID, Global, dbHelper);
                                            if (coDriverStatus.equals("Personal Use")) {
                                                Global.EldScreenToast(OnDutyBtn, ConstantsEnum.CO_DRIVING_ALSO + coDriverStatus + ConstantsEnum.CO_DRIVING_ALERT1, getResources().getColor(R.color.colorVoilation));
                                            } else {
                                                Global.EldScreenToast(OnDutyBtn, ConstantsEnum.CO_DRIVING_ALERT + coDriverStatus + ConstantsEnum.CO_DRIVING_ALERT1, getResources().getColor(R.color.colorVoilation));
                                            }

                                        }
                                    }
                                }
                            } else {
                                Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.personal_use_limit_75), getResources().getColor(R.color.colorVoilation));
                            }
                        }else{
                             Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.pc_ym_alert_with_dr), getResources().getColor(R.color.colorVoilation));
                         }
                        }else{
                            Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.connect_with_obd_first), getResources().getColor(R.color.colorVoilation));
                        }

                    } else {
                        Global.EldToastWithDuration4Sec(personalUseBtn, getResources().getString(R.string.personal_not_allowed), getResources().getColor(R.color.colorVoilation));
                    }


                break;


            case R.id.onDutyLay:

                if (constants.isAllowedFromPCYM(DRIVER_JOB_STATUS, isPersonal, isYardMove, getActivity()) ) {
                    MalfunctionDefinition = "";
                   // if (constants.isActionAllowed(getActivity())) {
                    if(hMethods.isActionAllowedWhileDriving(getActivity(), Global, DRIVER_ID, dbHelper)){
                        isYardBtnClick = false;
                        OnDutyBtnClick();
                    } else {
                        Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    Global.EldScreenToast(OnDutyBtn, constants.lastStatusDesc(DRIVER_JOB_STATUS, isYardMove, getActivity()), getResources().getColor(R.color.colorVoilation));
                }
                break;

            case R.id.drivingLay:
                if (constants.isAllowedFromPCYM(DRIVER_JOB_STATUS, isPersonal, isYardMove, getActivity()) ) {
                    MalfunctionDefinition = "";
                    DrivingBtnClick();
                }else{
                    Global.EldScreenToast(OnDutyBtn, constants.lastStatusDesc(DRIVER_JOB_STATUS, isYardMove, getActivity()), getResources().getColor(R.color.colorVoilation));
                }

                break;


            case R.id.sleeperDutyLay:

                if (constants.isAllowedFromPCYM(DRIVER_JOB_STATUS, isPersonal, isYardMove, getActivity()) ) {
                    MalfunctionDefinition = "";
                    //if(constants.isActionAllowed(getActivity())){
                    if(hMethods.isActionAllowedWhileDriving(getActivity(), Global, DRIVER_ID, dbHelper)){
                        SleeperBtnClick();
                    } else {
                        Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    Global.EldScreenToast(OnDutyBtn, constants.lastStatusDesc(DRIVER_JOB_STATUS, isYardMove, getActivity()), getResources().getColor(R.color.colorVoilation));
                }

                break;


            case R.id.offDutyLay:

                if (constants.isAllowedFromPCYM(DRIVER_JOB_STATUS, isPersonal, isYardMove, getActivity()) ) {
                    MalfunctionDefinition = "";
                    //if(constants.isActionAllowed(getActivity())){
                    if(hMethods.isActionAllowedWhileDriving(getActivity(), Global, DRIVER_ID, dbHelper)){
                        OffDutyBtnClick();
                    } else {
                        Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    Global.EldScreenToast(OnDutyBtn, constants.lastStatusDesc(DRIVER_JOB_STATUS, isYardMove, getActivity()), getResources().getColor(R.color.colorVoilation));
                }

                break;



            case R.id.refreshLogBtn:
                try {
                    if (SharedPref.isSuggestedEditOccur(getActivity()) && Constants.isClaim == false) {
                        Toast.makeText(getActivity(), getString(R.string.other_suggested_log), Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getActivity(), SuggestedFragmentActivity.class);
                        i.putExtra(ConstantsKeys.suggested_data, "");
                        i.putExtra(ConstantsKeys.Date, "");
                        startActivity(i);
                    } else {
                        IsRefreshedClick = false;

                        if (TeamDriverType.equals(DriverConst.TeamDriver)) {
                           // GetDriverLog18Days(MainDriverId, GetDriverLog18Days);
                           // GetDriverLog18Days(CoDriverId, GetCoDriverLog18Days);

                            dataRequest18Days(MainDriverId, GetDriverLog18Days);
                            dataRequest18Days(CoDriverId, GetCoDriverLog18Days);
                        } else {
                            dataRequest18Days(DRIVER_ID, GetDriverLog18Days);
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.resetTimerBtn:
                isFragmentAdd = true;
                moveToCertifyWithPopup();
                isFragmentAdd = false;
                break;

            case R.id.autoOffDutyBtn:
                SaveOffDutyStatus();
                break;

            case R.id.autoDriveBtn:
                SaveDrivingStatus();
                break;

            case R.id.autoOnDutyBtn:
                isYardBtnClick = false;
                Reason = "Others";
                DriverStatusId = Global.ON_DUTY;
                SaveDriverJob(Global.ON_DUTY);
                break;

        }
    }


    void pushStatusToServerAfterSave() {
         if (Global.isConnected(getActivity())){
             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     GetDriversSavedData(false, DriverType);
                     if (DriverJsonArray.length() > 0 && !IsSaveOperationInProgress) {
                         SAVE_DRIVER_STATUS();
                     }
                 }
             }, 500);
        }
    }


    void refreshDataEvent(boolean isConnected){
        String lastRefreshTime = SharedPref.getRefreshDataTime(getActivity());

        if (lastRefreshTime.length() > 0) {
            DateTime lastSavedRefreshTime = Global.getDateTimeObj(lastRefreshTime, false);
            DateTime currentTime = Global.getDateTimeObj(Global.GetCurrentUTCTimeFormat(), false);

            int secDiff = currentTime.getSecondOfDay() - lastSavedRefreshTime.getSecondOfDay();
            if (secDiff > 10) {  // 10 sec diff
                onResumeDataSet(isConnected);
                SharedPref.setRefreshDataTime(Global.GetCurrentUTCTimeFormat(), getActivity());
                IsRefreshedClick = true;
                GetDriverStatusPermission(DRIVER_ID, DeviceId, VehicleId);
            } else {
               // SharedPref.setRefreshDataTime(Global.GetCurrentUTCTimeFormat(), getActivity());
                if(!IsRefreshedClick)
                    loadingSpinEldIV.stopAnimation();
                Global.EldScreenToast(OnDutyBtn, getString(R.string.already_refreshed), getResources().getColor(R.color.colorSleeper));
                SharedPref.setRefreshDataTime(Global.GetCurrentUTCTimeFormat(), getActivity());
            }

        } else {
            onResumeDataSet(isConnected);
            SharedPref.setRefreshDataTime(Global.GetCurrentUTCTimeFormat(), getActivity());
            IsRefreshedClick = true;
            GetDriverStatusPermission(DRIVER_ID, DeviceId, VehicleId);
        }
    }


    private void resetCertifyLogDialogTitles(){
        certifyTitle = "<font color='#1A3561'><b>Alert !!</b></font>";
        titleDesc = "<font color='#2E2E2E'><html>" + getResources().getString(R.string.certify_previous_days_log) + " </html> </font>";
        okText = "<font color='#1A3561'><b>" + getResources().getString(R.string.ok) + "</b></font>";
    }

    private void OffDutyBtnClick(){
        isDrivingCalled = false;
        isYardBtnClick = false;
        if(constants.CheckGpsStatus(getActivity())) {
            if (DRIVER_JOB_STATUS != OFF_DUTY || (DRIVER_JOB_STATUS == OFF_DUTY && isPersonal.equals("true"))) {
                if (isCertifySignPending(false, false)) {
                    Global.DriverSwitchAlert(getActivity(), certifyTitle, titleDesc, okText);
                    resetCertifyLogDialogTitles();

                } else {
                    if (hMethods.CanChangeStatus(OFF_DUTY, driverLogArray, Global, false)) {
                        restartLocationService();

                        // if Odometers are saving through OBD automatically then makes all the condition false to ignore odometer save
                        if(SharedPref.IsOdometerFromOBD(getActivity())) {
                            SharedPref.OdometerSaved(true, getActivity());
                            IsStartReading = false;
                            IsPopupDismissed = true;
                        }

                        if (IsAOBRD && !IsAOBRDAutomatic) {
                            // if (Global.isConnected(getActivity())) {
                            if (SharedPref.IsOdometerSaved(getActivity())) {
                                if (DRIVER_JOB_STATUS == DRIVING) {
                                    if (!IsPopupDismissed) {
                                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_READING_AFTER + " after Driving.", false, OFF_DUTY, OffDutyBtn, alertDialog);
                                    } else {
                                        JobStatusInt = OFF_DUTY;
                                        OpenLocationDialog(OFF_DUTY, OldSelectedStatePos, false);
                                    }
                                } else {
                                    if (DRIVER_JOB_STATUS == OFF_DUTY && IsStartReading) {
                                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_END_READING, true, OFF_DUTY, OffDutyBtn, alertDialog);
                                    } else {
                                        JobStatusInt = OFF_DUTY;
                                        OpenLocationDialog(OFF_DUTY, OldSelectedStatePos, false);
                                    }
                                }
                            } else {
                                AFTER_PERSONAL_JOB = PERSONAL_OFF_DUTY;
                                if (isPersonal.equals("true")) {
                                    if (IsStartReading) {
                                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_END_READING, true, OFF_DUTY, OffDutyBtn, alertDialog);
                                    } else {
                                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_READING, true, OFF_DUTY, OffDutyBtn, alertDialog);
                                    }

                                } else {
                                    if (DRIVER_JOB_STATUS == DRIVING) {
                                        if (!IsPopupDismissed) {
                                            Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_READING_AFTER, false, OFF_DUTY, OffDutyBtn, alertDialog);
                                        } else {
                                            JobStatusInt = OFF_DUTY;
                                            OpenLocationDialog(OFF_DUTY, OldSelectedStatePos, false);
                                        }
                                    } else {
                                        JobStatusInt = OFF_DUTY;
                                        OpenLocationDialog(OFF_DUTY, OldSelectedStatePos, false);
                                    }
                                }
                            }

                        }  else {
                            if (SharedPref.isLocDiagnosticOccur(getActivity()) && Globally.LATITUDE.length() < 5) { //constants.isLocMalfunctionEvent(getActivity(), DriverType)
                                // DriverLocationDialog.updateViewTV.performClick();
                                OpenLocationDialog(OFF_DUTY, OldSelectedStatePos, true);
                            } else {
                                DisableJobViews();
                                // save status directly on button click. and uploaded automatically in background
                                SaveJobStatusAlert("Off Duty");
                            }
                        }
                    } else {
                        Global.EldScreenToast(OnDutyBtn, ConstantsEnum.DUPLICATE_JOB_ALERT, getResources().getColor(R.color.colorVoilation));
                    }
                }
            }
        }else{
            ClearGPSData();
            Global.EldScreenToast(OnDutyBtn, getResources().getString(R.string.gps_alert), getResources().getColor(R.color.colorVoilation));
        }
    }


    private void SleeperBtnClick(){
        isDrivingCalled = false;
        isYardBtnClick = false;
        if(constants.CheckGpsStatus(getActivity())) {
            if (DRIVER_JOB_STATUS != SLEEPER) {

                // if Odometers are saving through OBD autmatically then makes all the condition false to ignore odometer save
                if(SharedPref.IsOdometerFromOBD(getActivity())) {
                    SharedPref.OdometerSaved(true, getActivity());
                    IsStartReading = false;
                    IsPopupDismissed = true;
                }

                if (isCertifySignPending(false, false)) {
                    Global.DriverSwitchAlert(getActivity(), certifyTitle, titleDesc, okText);
                    resetCertifyLogDialogTitles();
                } else {
                    if (hMethods.CanChangeStatus(SLEEPER, driverLogArray, Global, false)) {

                        restartLocationService();

                        if (IsAOBRD && !IsAOBRDAutomatic) {
                            //  if (Global.isConnected(getActivity())) {
                            if (isPersonal.equals("true")) {
                                if (SharedPref.IsOdometerSaved(getActivity()) && !IsStartReading) {
                                    JobStatusInt = SLEEPER;
                                    OpenLocationDialog(SLEEPER, OldSelectedStatePos, false);
                                } else {
                                    AFTER_PERSONAL_JOB = PERSONAL_SLEEPER;
                                    if (IsStartReading) {
                                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_END_READING, true, SLEEPER, SleeperBtn, alertDialog);
                                    } else {
                                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_READING, true, SLEEPER, SleeperBtn, alertDialog);
                                    }

                                }
                            } else {
                                if (DRIVER_JOB_STATUS == DRIVING) {
                                    if (!IsPopupDismissed) {
                                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_READING_AFTER + " after Driving.", false, SLEEPER, SleeperBtn, alertDialog);
                                    } else {
                                        JobStatusInt = SLEEPER;
                                        OpenLocationDialog(SLEEPER, OldSelectedStatePos, false);
                                    }
                                } else {
                                    JobStatusInt = SLEEPER;
                                    OpenLocationDialog(SLEEPER, OldSelectedStatePos, false);
                                }
                            }

                        } else{
                            if(SharedPref.isLocDiagnosticOccur(getActivity()) && Globally.LATITUDE.length() < 5){  //constants.isLocMalfunctionEvent(getActivity(), DriverType)
                                OpenLocationDialog(SLEEPER, OldSelectedStatePos, true);
                            }else {
                                DisableJobViews();
                                // save status directly on button click. and uploaded automatically in background
                                SaveJobStatusAlert("Sleeper Berth");

                            }
                        }

                    } else {
                        Global.EldScreenToast(OnDutyBtn, ConstantsEnum.DUPLICATE_JOB_ALERT, getResources().getColor(R.color.colorVoilation));
                    }
                }
            }
        }else{
            ClearGPSData();
            Global.EldScreenToast(OnDutyBtn, getResources().getString(R.string.gps_alert), getResources().getColor(R.color.colorVoilation));
        }
    }


    private void DrivingBtnClick(){
        isDrivingCalled = false;
        isYardBtnClick = false;
        if(constants.CheckGpsStatus(getActivity())) {
            if (DRIVER_JOB_STATUS != DRIVING) {

                String coDriverStatus = hMethods.getCoDriverStatus(getActivity(), DRIVER_ID, Global, dbHelper);
                boolean isDrivingAllowedWithCo = hMethods.isDrivingAllowedWithCoDriver(getActivity(), Global, DRIVER_ID, true, dbHelper);
                if(isDrivingAllowedWithCo) {

                    if(constants.isActionAllowed(getActivity()) == false &&
                            (!coDriverStatus.equals("Yard Move") || !coDriverStatus.equals("Personal Use")) ){
                        Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }else {
                        if (hMethods.CanChangeStatus(DRIVING, driverLogArray, Global, false)) {

                            if (SharedPref.IsDrivingShippingAllowed(getActivity())) {
                                if (shipmentMethod.isShippingCleared(DRIVER_ID, dbHelper)) {
                                    Global.EldScreenToast(OnDutyBtn, getResources().getString(R.string.shipping_info_alert), getResources().getColor(R.color.colorVoilation));
                                } else {
                                    DrivingWithCertifySign();
                                }
                            } else {

                                if (shipmentMethod.isShippingCleared(DRIVER_ID, dbHelper)) {
                                    if (TrailorNumber.equals(getResources().getString(R.string.no_trailer))) {
                                        progressBar.setVisibility(View.GONE);
                                        DrivingWithCertifySign();
                                    } else {
                                        if (TrailorNumber.trim().length() > 0) {
                                            shipmentInfoAlert();
                                        } else {
                                            if (trailerDialog != null && trailerDialog.isShowing())
                                                trailerDialog.dismiss();

                                            trailerDialog = new TrailorDialog(getActivity(), "trailor_driving", false, TrailorNumber, 0, false,
                                                    Global.onDutyRemarks, oldStatusView, dbHelper, new TrailorListener());
                                            trailerDialog.show();
                                            // Global.EldScreenToast(OnDutyBtn, getResources().getString(R.string.Add_trailer_no), getResources().getColor(R.color.colorVoilation));
                                        }
                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    DrivingWithCertifySign();
                                }

                            }
                        } else {
                            Global.EldScreenToast(OnDutyBtn, ConstantsEnum.DUPLICATE_JOB_ALERT, getResources().getColor(R.color.colorVoilation));
                        }
                    }
                }else{
                    if(hMethods.isSwitchedTimeGreater10Sec(true, getActivity())){
                        Global.EldScreenToast(OnDutyBtn, ConstantsEnum.CO_DRIVING_ALERT + coDriverStatus + ConstantsEnum.CO_DRIVING_ALERT1, getResources().getColor(R.color.colorVoilation));
                    }else{
                        Global.EldScreenToast(OnDutyBtn, ConstantsEnum.AFTER_SWITCH_ALERT, getResources().getColor(R.color.colorVoilation));
                    }

                }
            }
        }else{
            ClearGPSData();
            Global.EldScreenToast(OnDutyBtn, getResources().getString(R.string.gps_alert), getResources().getColor(R.color.colorVoilation));
        }


    }


    private void OnDutyBtnClick(){
        isDrivingCalled = false;

        boolean isGps = constants.CheckGpsStatus(getActivity());

        if(isGps) {
            if (isCertifySignPending(false, false)) {
                Global.DriverSwitchAlert(getActivity(), certifyTitle, titleDesc, okText);
                resetCertifyLogDialogTitles();
            } else {
                restartLocationService();
                if (((DRIVER_JOB_STATUS == OFF_DUTY && !isPersonal.equals("true")) || DRIVER_JOB_STATUS == SLEEPER) &&
                        !isRemainingView) {

                    LATEST_JOB_STATUS = ON_DUTY;
                    DisableJobViews();
                    CalculateTimeInOffLine(true, false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }, 500);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            double remainingTime = getDailyOffLeftMinObj.getLeftOffOrSleeperMinutes();
                            if (remainingTime <= 0) {
                                OnDuty(OnDutyBtn);
                            } else {
                                String currentStatus;
                                if (DRIVER_JOB_STATUS == OFF_DUTY)
                                    currentStatus = "Off Duty";
                                else
                                    currentStatus = "Sleeper";

                                RemainingTimeDialog(ON_DUTY, currentStatus, String.valueOf((int) remainingTime));
                            }
                        }
                    }, 200);
                } else {
                    OnDuty(OnDutyBtn);
                }
            }
        }else{
            ClearGPSData();
            Global.EldScreenToast(OnDutyBtn, getResources().getString(R.string.gps_alert), getResources().getColor(R.color.colorVoilation));
        }
    }


    private void PersonalBtnClick(){
        isDrivingCalled = false;
        isYardBtnClick = false;

        if(constants.CheckGpsStatus(getActivity())) {
            if (DRIVER_JOB_STATUS != OFF_DUTY ||
                    (DRIVER_JOB_STATUS == OFF_DUTY && isPersonal.equals("false"))) {

                    if (hMethods.CanChangeStatus(OFF_DUTY, driverLogArray, Global, true)) {

                        restartLocationService();

                        if (!IsAOBRD) {

                            DisableJobViews();

                            // if Odometers are saving through OBD autmatically then makes all the condition false to ignore odometer save
                            if (SharedPref.IsOdometerFromOBD(getActivity())) {
                                IsStartReading = false;
                            }

                            // save status directly on button click. and uploaded automatically in background
                            if (!IsStartReading) {
                                SaveJobStatusAlert("Personal");
                            } else {
                                Global.EldScreenToast(OnDutyBtn, "Please enter End odometer Reading before Personal use.", getResources().getColor(R.color.colorVoilation));
                            }

                            EnableJobViews();
                        }else{
                            //SaveJobStatusAlert("Personal");
                            OpenLocationDialog(PERSONAL, OldSelectedStatePos, false);
                        }


                    } else {
                        Global.EldScreenToast(OnDutyBtn, ConstantsEnum.DUPLICATE_JOB_ALERT, getResources().getColor(R.color.colorVoilation));
                    }
            }else{

                if (constants.isObdConnected(getActivity())) {
                    statusEndConfDialog.ShowAlertDialog(getString(R.string.Confirmation_suggested), getString(R.string.WantEndPU),
                            getString(R.string.yes),  getString(R.string.no),
                            PC_END, positiveCallBack, negativeCallBack);
                }else{
                    Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.connect_with_obd_first), getResources().getColor(R.color.colorVoilation));
                }

            }
        }else{
            ClearGPSData();
            Global.EldScreenToast(OnDutyBtn, getResources().getString(R.string.gps_alert), getResources().getColor(R.color.colorVoilation));
        }
    }



    AlertDialogEld.PositiveButtonCallback positiveCallBack = new AlertDialogEld.PositiveButtonCallback() {
        @Override
        public void getPositiveClick(int flag) {

            if(flag == PC_END){
                SaveOffDutyStatus();
            }else{
                Reason = "Other";   //Yard Move End
                isYardMove = false;
                isYardBtnClick = false;

                SaveDriverJob(Global.ON_DUTY);
                Global.EldScreenToast(OnDutyBtn, getResources().getString(R.string.yardmove_ended), getResources().getColor(R.color.colorPrimary));
            }


        }
    };

    AlertDialogEld.NegativeButtonCallBack negativeCallBack = new AlertDialogEld.NegativeButtonCallBack() {
        @Override
        public void getNegativeClick(int flag) {
            Log.d("negativeCallBack", "negativeCallBack: " + flag);
        }
    };



    private void DrivingWithCertifySign(){
        if (isCertifySignPending(false, false)) {
            Global.DriverSwitchAlert(getActivity(), certifyTitle, titleDesc, okText);
            resetCertifyLogDialogTitles();
        } else {
            if (TrailorNumber.length() > 0) {

                restartLocationService();

                if ((DRIVER_JOB_STATUS == OFF_DUTY && !isPersonal.equals("true")) || DRIVER_JOB_STATUS == SLEEPER) {

                    LATEST_JOB_STATUS = DRIVING;
                    DisableJobViews();
                    CalculateTimeInOffLine(true, false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }, 500);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            double remainingTime = getDailyOffLeftMinObj.getLeftOffOrSleeperMinutes();
                            if (remainingTime <= 0) {
                                Driving();
                            } else {
                                String currentStatus;
                                if (DRIVER_JOB_STATUS == OFF_DUTY)
                                    currentStatus = "Off Duty";
                                else
                                    currentStatus = "Sleeper";

                                RemainingTimeDialog(DRIVING, currentStatus, String.valueOf((int) remainingTime));
                            }
                        }
                    }, 200);
                } else {
                    Driving();
                }


            } else {
                if (trailerDialog != null && trailerDialog.isShowing())
                    trailerDialog.dismiss();

                if (getActivity() != null && !getActivity().isFinishing()){
                    trailerDialog = new TrailorDialog(getActivity(), "trailor_driving", false, TrailorNumber, 0, false,
                            Global.onDutyRemarks, oldStatusView, dbHelper, new TrailorListener());
                trailerDialog.show();
            }
            }
        }
    }


    private void ClearGPSData(){
        AddressLine = "";
        Globally.LATITUDE = "";
        Globally.LONGITUDE = "";
        City        = "";
        State       = "";
        Country     = "";
    }

    private void restartLocationService(){
        if (Globally.LATITUDE.equals("0.0") || Globally.LATITUDE.equals("") ){
            Constants.isEldHome = false;
            startService();
        }
    }

    private void startService(){
       Intent serviceIntent = new Intent(getActivity(), BackgroundLocationService.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(serviceIntent);
        }
        getActivity().startService(serviceIntent);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showShippingDialog(boolean IsShippingCleared){
        try {

            if(getActivity() != null && !getActivity().isFinishing()) {
                progressBar.setVisibility(View.GONE);

                if (shippingDocDialog != null && shippingDocDialog.isShowing()) {
                    shippingDocDialog.dismiss();
                }

                shippingDocDialog = new ShippingDocDialog(getActivity(), DRIVER_ID, DeviceId, SelectedDate, dbHelper, DriverType, IsShippingCleared);
                shippingDocDialog.show();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    void ShowDateDialog() {
        try {
            if(getActivity() != null && !getActivity().isFinishing()) {
                if (dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();

                dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, Global.GetCurrentDeviceDate(), new DateListener(), false);
                dateDialog.show();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    void RemainingTimeDialog(int status, String currentStatus, String time) {
        try {
            if(getActivity() != null && !getActivity().isFinishing()) {
                if (remainingDialog != null && remainingDialog.isShowing()) {
                    remainingDialog.dismiss();
                }
                remainingDialog = new RemainingTimeDialog(getActivity(), status, currentStatus, time, new RemainingTimeListener());
                remainingDialog.show();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    void Driving() {
        constants.IS_ELD_ON_CREATE = false;
        // DrivingBtn.setEnabled(false);
        if (IsAOBRD && !IsAOBRDAutomatic) {
            //  if (Global.isConnected(getActivity())) {
            if (isPersonal.equals("true")) {

                // if Odometers are saving through OBD autmatically then makes all the condition false to ignore odometer save
                if(SharedPref.IsOdometerFromOBD(getActivity())) {
                    SharedPref.OdometerSaved(true, getActivity());
                    IsStartReading = false;
                }

                if (SharedPref.IsOdometerSaved(getActivity()) && !IsStartReading) {
                    JobStatusInt = DRIVING;
                    OpenLocationDialog(DRIVING, OldSelectedStatePos, false);
                } else {
                    AFTER_PERSONAL_JOB = PERSONAL_DRIVING;

                    if (IsStartReading) {
                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_END_READING, true, DRIVING, DrivingBtn, alertDialog);
                    } else {
                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_READING, true, DRIVING, DrivingBtn, alertDialog);
                    }

                }
            } else {
                JobStatusInt = DRIVING;
                OpenLocationDialog(DRIVING, OldSelectedStatePos, false);
            }

        } else{
            if(SharedPref.isLocDiagnosticOccur(getActivity()) && Globally.LATITUDE.length() < 5){ //constants.isLocMalfunctionEvent(getActivity(), DriverType)
                //  DriverLocationDialog.updateViewTV.performClick();
                OpenLocationDialog(DRIVING, OldSelectedStatePos, true);
            }else {
                isPersonal = "false";
                // save status directly on button click. and uploaded automatically in background
                SaveJobStatusAlert("Driving");
            }
        }


    }

    void SaveDrivingStatus() {
        if (TruckNumber.trim().length() > 0) {
            IsPrePost = false;
            isPersonal = "false";
            isYardMove = false;
            SharedPref.OdometerSaved(false, getActivity());
            SWITCH_VIEW = DRIVING;
            SaveJobType(Global.DRIVING);
            SaveRequestCount = 0;
            //  BackgroundLocationService.IsAutoLogSaved = false;
            constants.IS_TRAILER_INSPECT = false;

            // save status directly on button click. and uploaded automatically in background
            DriverStatusId = String.valueOf(SWITCH_VIEW);
            DRIVER_JOB_STATUS = DRIVING;
            GetDriversSavedData(false, DriverType);
            EnableJobViews();

            SaveDriverJob(Global.DRIVING);
            Global.EldScreenToast(OnDutyBtn, "You are now driving.", getResources().getColor(R.color.colorPrimary));

            //GetDriverLogData();
            initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                    usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);
            SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);
            CalculateTimeInOffLine(false, false);

            // After save locally push data to server
            pushStatusToServerAfterSave();

        } else {
            Global.EldScreenToast(OnDutyBtn, "Please add Truck before start Driving.", getResources().getColor(R.color.colorPrimary));
        }
    }

    void OnDuty(View view) {
        constants.IS_ELD_ON_CREATE = false;
        if (IsAOBRD && !IsAOBRDAutomatic) {

            // if Odometers are saving through OBD autmatically then makes all the condition false to ignore odometer save
            if (SharedPref.IsOdometerFromOBD(getActivity())) {
                SharedPref.OdometerSaved(true, getActivity());
                IsStartReading = false;
                IsPopupDismissed = true;
            }

            if (isPersonal.equals("true")) {
                if (SharedPref.IsOdometerSaved(getActivity()) && !IsStartReading) {
                    JobStatusInt = ON_DUTY;
                    OpenLocationDialog(ON_DUTY, OldSelectedStatePos, false);
                } else {
                    AFTER_PERSONAL_JOB = PERSONAL_ON_DUTY;
                    if (IsStartReading) {
                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_END_READING, true, ON_DUTY, view, alertDialog);
                    } else {
                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_READING, true, ON_DUTY, view, alertDialog);
                    }
                }
            } else {
                if (DRIVER_JOB_STATUS == DRIVING) {
                    if (!IsPopupDismissed)
                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_READING_AFTER + " after Driving.", false, ON_DUTY, view, alertDialog);
                    else {
                        JobStatusInt = ON_DUTY;
                        OpenLocationDialog(ON_DUTY, OldSelectedStatePos, false);
                    }
                } else {
                    JobStatusInt = ON_DUTY;
                    OpenLocationDialog(ON_DUTY, OldSelectedStatePos, false);
                }
            }

        } else {
            if (SharedPref.isLocDiagnosticOccur(getActivity()) && Globally.LATITUDE.length() < 5) {   // constants.isLocMalfunctionEvent(getActivity(), DriverType)
                OpenLocationDialog(ON_DUTY, OldSelectedStatePos, true);
            } else {
                DisableJobViews();
                // save status directly on button click. and uploaded automatically in background
                SaveJobStatusAlert("On Duty");
            }
        }
    }



    void SaveOnDutyStatus() {

        if (TruckNumber.trim().length() > 0) {
            SetJobButtonView(ON_DUTY, isViolation, isPersonal);
            SWITCH_VIEW = ON_DUTY;
            DRIVER_JOB_STATUS = ON_DUTY;
            SaveJobType(Global.ON_DUTY);
            try {
                driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if(getActivity() != null && !getActivity().isFinishing()) {

                        if (trailerDialog != null && trailerDialog.isShowing())
                            trailerDialog.dismiss();

                    trailerDialog = new TrailorDialog(getActivity(), "on_duty", isYardBtnClick, TrailorNumber,
                            0, false, Global.onDutyRemarks, oldStatusView, dbHelper, new TrailorListener());
                    trailerDialog.show();
                }
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }


        } else {
            Global.EldScreenToast(OnDutyBtn, "Please add Truck before start On Duty.", getResources().getColor(R.color.colorPrimary));
        }

    }


    void SaveOffDutyStatus() {

        IsPrePost = false;
        isPersonal = "false";
        SharedPref.OdometerSaved(false, getActivity());
        isViolation = false;
        SharedPref.SetIsReadViolation(false, getActivity());
        SharedPref.SetViolation(isViolation, getActivity());
        SharedPref.SetViolationReason("", getActivity());
        SaveRequestCount = 0;
        //  BackgroundLocationService.IsAutoLogSaved = false;
        constants.IS_TRAILER_INSPECT = false;

        SWITCH_VIEW = OFF_DUTY;
        DriverStatusId = String.valueOf(SWITCH_VIEW);
        SaveJobType(Global.OFF_DUTY);

        // save status directly on button click. and uploaded automatically in background
        DRIVER_JOB_STATUS = OFF_DUTY;
        SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);
        initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);

        GetDriversSavedData(false, DriverType);
        SaveDriverJob(Global.OFF_DUTY);


        EnableJobViews();

        Global.EldScreenToast(OnDutyBtn, "You are now Off DUTY", getResources().getColor(R.color.colorPrimary));
        //GetDriverLogData();
        CalculateTimeInOffLine(false, false);

        // After save locally push data to server
        pushStatusToServerAfterSave();


    }

    void SaveSleeperStatus() {
        isPersonal = "false";
        if (DRIVER_JOB_STATUS != SLEEPER) {
            if (TruckNumber.trim().length() > 0) {
                isPersonal = "false";
                IsPrePost = false;
                isViolation = false;
                SharedPref.SetIsReadViolation(false, getActivity());
                SharedPref.OdometerSaved(false, getActivity());
               // SetJobButtonView(SLEEPER, isViolation, isPersonal);
                SWITCH_VIEW = SLEEPER;
                SaveJobType(Global.SLEEPER);
                SaveRequestCount = 0;
                // BackgroundLocationService.IsAutoLogSaved = false;
                constants.IS_TRAILER_INSPECT = false;

                SharedPref.SetViolation(isViolation, getActivity());
                SharedPref.SetViolationReason("", getActivity());

                // save status directly on button click. and uploaded automatically in background
                DriverStatusId = String.valueOf(SWITCH_VIEW);
                DRIVER_JOB_STATUS = SLEEPER;
                GetDriversSavedData(false, DriverType);
                SaveDriverJob(Global.SLEEPER);
                Global.EldScreenToast(OnDutyBtn, "Your job status is SLEEPER.", getResources().getColor(R.color.colorPrimary));
                EnableJobViews();

                //GetDriverLogData();
                initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                        usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);
                SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);
                CalculateTimeInOffLine(false, false);

                // After save locally push data to server
                pushStatusToServerAfterSave();

            } else {
                Global.EldScreenToast(OnDutyBtn, "Please add Truck before start Sleeper.", getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    void SavePersonalStatus() {

        isPersonal = "true";
        isPersonalOld = "true";
        SharedPref.OdometerSaved(false, getActivity());
        IsPrePost = false;
        SetJobButtonView(OFF_DUTY, false, isPersonal);
        initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);
        SWITCH_VIEW = OFF_DUTY;
        DriverStatusId = String.valueOf(SWITCH_VIEW);
        SaveJobType(Global.OFF_DUTY);
        isViolation = false;
        SharedPref.SetIsReadViolation(false, getActivity());
        SharedPref.SetViolation(isViolation, getActivity());
        SharedPref.SetViolationReason("", getActivity());
        SharedPref.SetTruckStartLoginStatus(false, getActivity());
        // BackgroundLocationService.IsAutoLogSaved = false;
        constants.IS_TRAILER_INSPECT = false;

        // save status directly on button click. and uploaded automatically in background
        GetDriversSavedData(false, DriverType);
        SaveDriverJob(Global.OFF_DUTY);
        Global.EldScreenToast(OnDutyBtn, "You have selected truck for Personal Use", getResources().getColor(R.color.colorPrimary));

        DRIVER_JOB_STATUS = 1;
       // GetDriverLogData();
        CalculateTimeInOffLine(false, false);

        // After save locally push data to server
        pushStatusToServerAfterSave();

    }


    void OpenLocationDialog(int JobType, int OldSelectedStatePos, boolean isMalfunction) {

        isRemainingView = false;
        EnableJobViews();
        try {
            if(getActivity() != null && !getActivity().isFinishing()) {
                if (StateArrayList.size() > 0) {
                    if (driverLocationDialog != null && driverLocationDialog.isShowing()) {
                        driverLocationDialog.dismiss();
                    }
                    driverLocationDialog = new DriverLocationDialog(getActivity(), City, State, OldSelectedStatePos, JobType,
                            isMalfunction, OnDutyBtn, StateArrayList, new DriverLocationListener());
                    driverLocationDialog.show();
                }
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }



    void clearTimer() {
        try {
            if (mTimer != null) {
                mTimer.cancel();
                timerTask.cancel();
                mTimer = null;
                timerTask = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Constants.IS_ACTIVE_ELD = false;
        exceptionFaceView.cancel();
        malfunctionTV.setText("");
        settingsMenuBtn.setVisibility(View.GONE);
        logoutMenuBtn.setVisibility(View.GONE);

        clearTimer();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(progressReceiver);

    }



    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try{

                boolean IsIgnitionOn = intent.getBooleanExtra(ConstantsKeys.IsIgnitionOn, false);
                if(IsIgnitionOn){
                    if (SharedPref.GetNewLoginStatus(getActivity()) == false) {
                        boolean isPopupShown = SharedPref.GetTruckStartLoginStatus(getActivity());
                        if (isPopupShown && ( isPersonal.equals("true") || (DRIVER_JOB_STATUS == ON_DUTY && isYardMove) ) ) {
                            boolean isPer = false;
                            if(isPersonal.equals("true")){
                                isPer = true;
                            }
                            YardMovePersonalStatusAlert(isYardMove, isPer,false);
                        }
                    }
                }else{

                    if(intent.hasExtra(ConstantsKeys.IsAutoStatusSaved)) {
                        if (intent.getBooleanExtra(ConstantsKeys.IsAutoStatusSaved, false) == true) {
                            final boolean isPersonalUse75Km = intent.getBooleanExtra(ConstantsKeys.PersonalUse75Km, false);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CalculateTimeInOffLine(false, false);
                                    oldStatusView = Integer.valueOf(DRIVER_JOB_STATUS);

                                    if (isPersonalUse75Km == true) {
                                        if(personalUseBtn.getText().toString().equals(getString(R.string.pc_end))){
                                            personalUseBtn.setText(getString(R.string.pc_start));
                                        }
                                    }

                                }
                            }, 1000);

                            Global.DriverSwitchAlertWithDismiss(getActivity(), certifyTitle, titleDesc, "Ok",
                                    statusAlertDialog, true);

                            if(intent.getBooleanExtra(ConstantsKeys.IsPcYmAlertChangeStatus, false) == true){
                                try {
                                    if(getActivity() != null && !getActivity().isFinishing()) {
                                        if (continueStatusDialog != null && continueStatusDialog.isShowing()) {
                                            continueStatusDialog.dismiss();
                                           // Log.d("dialog", "dialog dismissed");
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        } else if (intent.getBooleanExtra(ConstantsKeys.ChangedToOthers, false) == true) {

                            if(intent.hasExtra(ConstantsKeys.IsDismissDialog) && intent.getBooleanExtra(ConstantsKeys.IsDismissDialog, false) == true){
                                Global.DriverSwitchAlertWithDismiss(getActivity(), certifyTitle, titleDesc, "Ok",
                                        statusAlertDialog, true);
                            }else {
                                certifyTitle = "Duty Status Change Alert !!";
                                titleDesc = "Please change your status from Driving to other duty status due to vehicle is not moving.";
                                Global.DriverSwitchAlertWithDismiss(getActivity(), certifyTitle, titleDesc, "Ok",
                                        statusAlertDialog, false);
                            }
                        }else if(intent.getBooleanExtra(ConstantsKeys.IsOBDStatusUpdate, false) == true){
                            setObdStatus(false);
                        }

                    }else if(intent.hasExtra(ConstantsKeys.IsNeedToUpdate18DaysLog)) {
                        if (intent.getBooleanExtra(ConstantsKeys.IsNeedToUpdate18DaysLog, false) == true) {
                            if (Global.isConnected(getActivity())) {
                                GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
                            }
                        }else if (intent.getBooleanExtra(ConstantsKeys.IsUpdateMalDiaInfoWindow, false) == true) {
                            setMalfnDiagnEventInfo();
                        }
                    }else if(intent.hasExtra(ConstantsKeys.IsCertifyReminder)){
                        if (intent.getBooleanExtra(ConstantsKeys.IsCertifyReminder, false) == true) {
                            isCertifySignPending(false, true);
                        }
                    }
                    setMalfnDiagnEventInfo();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };


    /*===== Get Driver Jobs in Array List======= */
    private void GetDriversSavedData(boolean IsCheckPersonal, int DriverType) {
        listSize = 0;
        DriverJsonArray = new JSONArray();
        List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

        if (DriverType == Constants.MAIN_DRIVER_TYPE) {
            try {
                listSize = MainDriverPref.LoadSavedLoc(getActivity()).size();
                tempList = MainDriverPref.LoadSavedLoc(getActivity());
            } catch (Exception e) {
                listSize = 0;
            }
        } else {
            try {
                listSize = CoDriverPref.LoadSavedLoc(getActivity()).size();
                tempList = CoDriverPref.LoadSavedLoc(getActivity());
            } catch (Exception e) {
                listSize = 0;
            }
        }

        try {
            if (listSize > 0) {
                for (int i = 0; i < tempList.size(); i++) {
                    EldDataModelNew listModel = tempList.get(i);

                    if (listModel != null) {
                        constants.SaveEldJsonToList(listModel, DriverJsonArray);  /* Put data as JSON to List */

                        DRIVER_JOB_STATUS = Integer.valueOf(listModel.getDriverStatusId());
                        if (IsCheckPersonal) {
                            isPersonal = listModel.getIsPersonal();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listSize = 0;
        }

    }


 /*   private void GetDriverLogData() {

        switch (DRIVER_JOB_STATUS) {
            case OFF_DUTY:
                HighlightedViewHide();
                break;

            case SLEEPER:
                HighlightedViewHide();
                break;

            case DRIVING:
                HighlightedViewVisible();
                break;

            case ON_DUTY:
                HighlightedViewVisible();
                break;
        }

    }


    void HighlightedViewVisible() {
        remainingLay.setVisibility(View.VISIBLE);
    }


    void HighlightedViewHide() {
        remainingLay.setVisibility(View.GONE);
    }*/


    void VehicleBtnClick(String Title, final int position) {

        TruckListPosition = position ;  //- 1

        if (SharedPref.getDriverType(getContext()).equals(DriverConst.TeamDriver)) {
            SaveVehicleNumber(TruckListPosition, MainDriverId, CoDriverId, DriverCompanyId, LoginTruckChange, true);
        } else {
            SaveVehicleNumber(TruckListPosition, DRIVER_ID, "0", DriverCompanyId, LoginTruckChange, true);
        }

    }


    void clearOfflineData(){
        try {
            DriverJsonArray = new JSONArray();
            if (DriverType == Constants.MAIN_DRIVER_TYPE) // Single Driver Type and Position is 0
                MainDriverPref.ClearLocFromList(getActivity());
            else
                CoDriverPref.ClearLocFromList(getActivity());
        }catch (Exception e){
        }
    }


    /*===== Save Driver Jobs with Shared Preference to 18 days Array List and in unposted array those will be posted to server======= */
    private void SaveDriverJob(String driverStatus ) {

      /*  try {
            if(getActivity() != null && !getActivity().isFinishing()) {
                if (!constants.isObdConnectedWithELD(getActivity()) ) {
                    Toast.makeText(getActivity(), getString(R.string.info_missed_desc), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/


        boolean isLogSavedInSyncTable = false;
        String statusStr = "";
        String address = "", wasViolation = "false", ViolationReason = "", DriverName = "";
        String currentUTCTime = Global.GetCurrentUTCTime();
        String CurrentDeviceDate = Global.GetCurrentDateTime();
        currentUtcTimeDiffFormat = Global.GetCurrentUTCTimeFormat();
        DateTime currentDateTime = Global.getDateTimeObj(CurrentDeviceDate, false);    // Current Date Time
        DateTime currentUTCDateTime = Global.getDateTimeObj(Global.GetCurrentUTCTimeFormat(), true);


        try {

            if (SharedPref.isLocMalfunctionOccur(getActivity()) ) { //constants.isLocMalfunctionEvent(context, DriverType)
                LocationType = SharedPref.getLocationEventType(getActivity());
            }else if (SharedPref.isLocDiagnosticOccur(getActivity()) && Globally.LATITUDE.length() < 5){
                LocationType = SharedPref.getLocationEventType(getActivity());
            }else{
                LocationType = "";
            }

            getExceptionStatus();
            try {
                if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                    DriverName = MainDriverName;
                } else {
                    DriverName = CoDriverName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                if(AddressLine.length() == 0){
                    if(SharedPref.isLocDiagnosticOccur(getActivity()) && LocationType.length() > 0){
                        AddressLine = State + " " + City;
                    }else {
                        if ((CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2))
                                && SharedPref.IsAOBRD(getActivity()) == false) {
                            AddressLine = csvReader.getShortestAddress(getActivity());
                        } else {
                            if (Globally.LATITUDE.length() > 4) {
                                AddressLine = Globally.LATITUDE + "," + Globally.LONGITUDE;
                            }
                        }
                    }
                }

                if(!driverStatus.equals(Global.ON_DUTY)){
                    isYardBtnClick = false;
                }

                // Check violation before save status
                if (driverStatus.equals(Global.DRIVING) || driverStatus.equals(Global.ON_DUTY)) {
                    isPersonal = "false";

                    JSONArray logArray = constants.AddNewStatusInList(
                            "", driverStatus, "", "no_address",
                            DRIVER_ID, City, State, Country, AddressLine, AddressLine,
                            CurrentCycleId, Reason, isPersonal, isViolation,
                            "false", String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                            "" + constants.GetGpsStatusIn0And1Form(getActivity()),   // earlier value was GPSVehicleSpeed now it is deprecated. now GPS status is sending in this parameter
                            SharedPref.GetCurrentTruckPlateNo(getActivity()),
                            "mannual_save", isYardBtnClick,
                            Global, SharedPref.get16hrHaulExcptn(getActivity()), false,
                            "" + isHaulExcptn,"",
                            LocationType, MalfunctionDefinition, IsNorthCanada, false,
                            SharedPref.getObdOdometer(getActivity()), CoDriverIdInSaveStatus, CoDriverNameInSaveStatus,
                            TruckNumber, TrailorNumber, hMethods, dbHelper);


                    String CurrentDate = Global.GetCurrentDateTime();
                    int rulesVersion = SharedPref.GetRulesVersion(getActivity());

                    List<DriverLog> oDriverLog = hMethods.GetLogAsList(logArray);
                    DriverDetail oDriverDetail1 = hMethods.getDriverList(new DateTime(CurrentDate), new DateTime(currentUtcTimeDiffFormat),
                            Integer.valueOf(DRIVER_ID), offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver,
                            DRIVER_JOB_STATUS, isOldRecord, isHaulExcptn,isAdverseExcptn, IsNorthCanada, rulesVersion,
                            oDriverLog, getActivity());

                    if(CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2) ) {
                        oDriverDetail1.setCanAdverseException(isAdverseExcptn);
                    }

                    RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), Integer.valueOf(driverStatus), oDriverDetail1);

                    isViolation = RulesObj.isViolation();
                    if (isViolation) {
                        wasViolation = "true";
                        ViolationReason = RulesObj.getViolationReason();
                    } else {
                        wasViolation = "false";
                        ViolationReason = "";
                    }


                } else {
                    try {
                        JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
                        JSONObject lastItemJson = hMethods.GetLastJsonFromArray(driverLogArray);

                        if (driverStatus.equals(Global.OFF_DUTY) && isPersonal.equals("true")) {

                            String location = lastItemJson.getString(ConstantsKeys.StartLocation);
                            String[] loc = location.split(", ");
                            if (loc.length > 2) {
                                int locLength = loc.length - 1;
                                Country = loc[locLength];
                                State = loc[locLength - 1];

                                for (int i = 0; i < locLength - 1; i++) {
                                    City = City + " " + loc[i];
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                statusStr = initilizeEldView.getCurrentStatus(Integer.valueOf(driverStatus), isPersonal);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                IsAOBRD = SharedPref.IsAOBRD(getActivity());
                IsAOBRDAutomatic = SharedPref.IsAOBRDAutomatic(getActivity());

                if ((!IsAOBRD || IsAOBRDAutomatic) && !isLocMalfunction) {
                    City = "";
                    State = "";
                    Country = "";
                    address = AddressLine;
                } else {
                    City = constants.CheckNullString(City);
                    State = constants.CheckNullString(State);
                    Country = constants.CheckNullString(Country);

                   // address = City + ", " + State ;
                    address = State + " " + City;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            isLocMalfunction = false;

            boolean isCycleChange = false;
            /*if(DriverStatusId.equals(Globally.ON_DUTY) && Reason.equals("Border Crossing")){
                isCycleChange = true;
            }*/

            try {
                // Save driver job in array
                EldDataModelNew locationModel = new EldDataModelNew(
                        Global.PROJECT_ID,
                        DRIVER_ID,
                        DriverStatusId,

                        String.valueOf(isYardBtnClick),
                        isPersonal,
                        DeviceId,

                        Reason,
                        currentUTCTime,
                        TruckNumber,
                        TrailorNumber,
                        DriverCompanyId,
                        DriverName,

                        City,
                        State,
                        Country,
                        wasViolation,
                        ViolationReason,
                        Globally.LATITUDE,
                        Globally.LONGITUDE,
                        "false", // IsStatusAutomatic is false when manual job has been done
                        String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                        "" + constants.GetGpsStatusIn0And1Form(getActivity()),   // earlier value was GPSVehicleSpeed now it is deprecated. now GPS status is sending in this parameter. 1=On, 0=Off
                        SharedPref.GetCurrentTruckPlateNo(getActivity()),
                        String.valueOf(isHaulExcptn),
                        "false", "mannual_save",
                        String.valueOf(isAdverseExcptn),
                        "", "", LocationType,
                        String.valueOf(IsNorthCanada),
                        CurrentDeviceDate,
                        String.valueOf(IsAOBRD),
                        CurrentCycleId,
                        String.valueOf(isDeferral), "", "false",
                        String.valueOf(isCycleChange),
                        "0",
                        CoDriverIdInSaveStatus, CoDriverNameInSaveStatus,"false"

                );

                // Save Model in offline Array
                if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                    MainDriverPref.AddDriverLoc(getActivity(), locationModel);

                    /* ==== Add data in list to show in offline mode ============ */
                    EldDriverLogModel logModel = new EldDriverLogModel(Integer.valueOf(DriverStatusId), "0","startDateTime", "endDateTime", "totalHours",
                            "currentCycleId", false, currentUtcTimeDiffFormat, currentUtcTimeDiffFormat,
                            "", "", AddressLine, "", Boolean.parseBoolean(isPersonal),
                            isAdverseExcptn, isHaulExcptn, Globally.LATITUDE, Globally.LONGITUDE, CoDriverIdInSaveStatus, CoDriverNameInSaveStatus);
                    eldSharedPref.AddDriverLoc(getActivity(), logModel);
                } else {
                    CoDriverPref.AddDriverLoc(getActivity(), locationModel);

                    /* ==== Add data in list to show in offline mode ============ */
                    EldDriverLogModel logModel = new EldDriverLogModel(Integer.valueOf(DriverStatusId), "0", "startDateTime", "endDateTime", "totalHours",
                            "currentCycleId", false, currentUtcTimeDiffFormat, currentUtcTimeDiffFormat,
                            "", "", AddressLine, "", Boolean.parseBoolean(isPersonal),
                            isAdverseExcptn, isHaulExcptn, Globally.LATITUDE, Globally.LONGITUDE, CoDriverIdInSaveStatus, CoDriverNameInSaveStatus);
                    coEldSharedPref.AddDriverLoc(getActivity(), logModel);
                }


                constants.SaveEldJsonToList(locationModel, DriverJsonArray);   /* Put data as JSON to List */

                // Saved json in synced array which is using in setting page to sync data mannually.
                JSONObject newObj = constants.GetJsonFromList(DriverJsonArray, DriverJsonArray.length() - 1);
                JSONArray savedSyncedArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DRIVER_ID), dbHelper);

                int syncDataFileLimit = 160;
                if(savedSyncedArray.length() > syncDataFileLimit){

                    // Remove old records from table if file size is greater then 160 to save memory avoid large file to upload extra data.
                    JSONArray reverseArray = notificationMethod.ReverseArray(savedSyncedArray);
                    for(int i = reverseArray.length()-1 ; i >= 40  ; i--){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            reverseArray.remove(i);
                        }
                    }
                    savedSyncedArray = notificationMethod.ReverseArray(reverseArray);
                }

              //  Log.d("savedSyncedArray", "savedSyncedArray: " +savedSyncedArray);

                savedSyncedArray.put(newObj);
                syncingMethod.SyncingLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, savedSyncedArray);
                isLogSavedInSyncTable = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPref.SetViolation(isViolation, getActivity());
            SharedPref.SetViolationReason(ViolationReason, getActivity());

            try {
                boolean IsWarningNotificationAllowed = constants.IsWarningNotificationAllowed(DRIVER_ID, dbHelper);

                if (isViolation && IsWarningNotificationAllowed) {

                    constants.SaveNotification(DriverType, getResources().getString(R.string.violation), ViolationReason, currentDateTime.toString(), hMethods, notificationPref, coNotificationPref, getActivity());

                    // Save Notification in 18 days notification history array
                    notificationMethod.saveNotificationHistory(
                            Integer.valueOf(DRIVER_ID),
                            DeviceId,
                            DriverName,
                            getResources().getString(R.string.violation),
                            ViolationReason,
                            currentDateTime,
                            DriverCompanyId,
                            dbHelper);


                    // Write violation file in storage
                    constants.writeViolationFile(currentDateTime, currentUTCDateTime, Integer.valueOf(DRIVER_ID), CurrentCycleId, offsetFromUTC, isSingleDriver,
                            DRIVER_JOB_STATUS, isOldRecord, isHaulExcptn, ViolationReason, hMethods, dbHelper, getActivity());

                }

                // ============================ Save Job Status in SQLite ==============================
                driverLogArray = constants.AddNewStatusInList(DriverName, DriverStatusId, ViolationReason, address,
                        DRIVER_ID, City, State, Country, AddressLine, AddressLine,
                        CurrentCycleId, Reason, isPersonal, isViolation,
                        "false", String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                        "" + constants.GetGpsStatusIn0And1Form(getActivity()),   // earlier value was GPSVehicleSpeed now it is deprecated. now GPS status is sending in this parameter
                        SharedPref.GetCurrentTruckPlateNo(getActivity()), "mannual_save", isYardBtnClick,
                        Global, isHaulExcptn, false, "" + isAdverseExcptn,"",
                        LocationType, MalfunctionDefinition, IsNorthCanada, isCycleChange,
                        SharedPref.getObdOdometer(getActivity()), CoDriverIdInSaveStatus, CoDriverNameInSaveStatus,
                        TruckNumber, TrailorNumber, hMethods, dbHelper);

                /* ---------------- DB Helper operations (Insert/Update) --------------- */
                hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, driverLogArray);
                Reason = "";
                BackgroundLocationService.IsAutoChange = false;
                SharedPref.SetTruckIgnitionStatusForContinue("ON", "home", Global.getCurrentDate(), getActivity());

            } catch (Exception e) {
                e.printStackTrace();
                // ============================ Save Job Status in SQLite ==============================
                driverLogArray = constants.AddNewStatusInList(DriverName, DriverStatusId, ViolationReason, address,
                        DRIVER_ID, City, State, Country, AddressLine, AddressLine,
                        CurrentCycleId, Reason, isPersonal, isViolation,
                        "false", String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                        "" + constants.GetGpsStatusIn0And1Form(getActivity()),   // earlier value was GPSVehicleSpeed now it is deprecated. now GPS status is sending in this parameter
                        SharedPref.GetCurrentTruckPlateNo(getActivity()), "mannual_save", isYardBtnClick,
                        Global, isHaulExcptn, false,"" + isAdverseExcptn,"",
                        LocationType, MalfunctionDefinition, IsNorthCanada, isCycleChange, CoDriverIdInSaveStatus, CoDriverNameInSaveStatus,
                        SharedPref.getObdOdometer(getActivity()), TruckNumber, TrailorNumber, hMethods, dbHelper);

                /* ---------------- DB Helper operations (Insert/Update) --------------- */
                hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, driverLogArray);

                // save driver status manual input in log
                constants.saveAppUsageLog(statusStr + " status not saved in 18 days array"  + ". reason behind: " +e.toString(),
                        false, false, obdUtil);
            }

        }  catch (Exception e){
            e.printStackTrace();
            // save driver status manual input in log
            constants.saveAppUsageLog("EldFragment-Exception occured when changed to " + statusStr + ", SyncLogEntry=" +isLogSavedInSyncTable,
                    false, false, obdUtil);

        }


        // save missing diagnostic
        if(!constants.isObdConnectedWithELD(getActivity()) && !isExemptDriver ) {
            boolean isMissingEventAlreadyWithStatus = malfunctionDiagnosticMethod.isMissingEventAlreadyWithStatus(DRIVER_JOB_STATUS,
                    isPersonal, ""+DRIVER_JOB_STATUS, dbHelper);
            if (!isMissingEventAlreadyWithStatus) {
                LocationType = "";
                SharedPref.saveMissingDiaStatus(true, getActivity());

                saveMissingDiagnostic(getString(R.string.obd_data_is_missing), currentUtcTimeDiffFormat);

                Globally.PlayNotificationSound(getActivity());
                Global.ShowLocalNotification(getActivity(),
                        getString(R.string.missing_dia_event),
                        getString(R.string.missing_event_occured_desc) + " in " +
                                Global.JobStatus(DRIVER_JOB_STATUS, Boolean.parseBoolean(isPersonal), ""+DRIVER_JOB_STATUS) +
                                " due to OBD not connected with E-Log Book", 2091);

            }
        }

        // Save odometer
        // save app display status log
        if(SharedPref.IsOdometerFromOBD(getActivity())) {
            constants.saveOdometer(DriverStatusId, DRIVER_ID, DeviceId, driverLogArray,
                    odometerhMethod, hMethods, dbHelper, getActivity());
        }
        SharedPref.setDrivingAllowedStatus(true, "", getActivity());
        AddressLine = "";
        State = ""; City = ""; Country = "";

        oldStatusView = Integer.valueOf(driverStatus);
        if (driverStatus.equals(Global.ON_DUTY)){
            setYardMoveView();
        }

        isYardBtnClick = false;

        if (getActivity() != null && !constants.isObdConnectedWithELD(getActivity())) {
            Global.InternetErrorDialog(getActivity(), true, true);
        }

    }



    // ------------- Check Certify signature status -------------
    public boolean isCertifySignPending(boolean isOnCreate, boolean IsCertifyReminder ){
        JSONObject logPermissionObj       = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DRIVER_ID), dbHelper);
        boolean isCertifySignaturePending = false;
        boolean IsCertifyMandatory        = SharedPref.IsCertifyMandatory(getActivity());
        boolean isSignPending             = constants.GetCertifyLogSignStatus(recapViewMethod, DRIVER_ID, dbHelper, SelectedDate, CurrentCycleId, logPermissionObj);

        String colorCode                  = "#1A3561";
        if (UILApplication.getInstance().isNightModeEnabled()) { //UILApplication.getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES
            colorCode = "#808080";
        }
        String title                      = "<font color='" + colorCode + "'><b>Certify Reminder !!</b></font>";
        String titleMsg                   = "<font color='#2E2E2E'><html>" + getResources().getString(R.string.certify_previous_days_log_warning) + " </html></font>";
        String dismissText                = "<font color='" +colorCode+ "'><b>" + getResources().getString(R.string.dismiss) + "</b></font>";


        if( IsCertifyMandatory ){
            if(isSignPending) {
                isCertifySignaturePending = true;

                if (isOnCreate) {

                    certifyLogAlert(title, titleMsg);
                   // Global.DriverSwitchAlert(getActivity(), title, titleMsg, dismissText);
                }
            }
        }else{
            if(isSignPending && !isOnCreate){
                if (currentDayArray.length() <= 1 && isNewDateStart) {
                    titleDesc                 = "<font color='#2E2E2E'><html>" + getResources().getString(R.string.certify_previous_days_log_ask_popup) + " </html> </font>";
                    isNewDateStart            = false;
                    isCertifySignaturePending = true;

                    certifyTitle = title;
                    titleDesc    = titleMsg;
                    okText       = dismissText;

                    if(IsCertifyReminder) {
                        certifyLogAlert(title, titleMsg);
                    }

                }else{
                    if( isNewDateStart){
                        isNewDateStart = false;
                        certifyLogAlert(title, titleMsg);
                    }
                }
            }else{
                if(isSignPending && isOnCreate){
                    certifyLogAlert(title, titleMsg);
                }
            }
        }

        return isCertifySignaturePending;
    }

    private void confirmDeferralRuleDays(){
        boolean isDeferralOccurred = constants.isDeferralOccurred(DRIVER_ID, MainDriverId, getActivity());
        int deferralDays = constants.confirmDeferralRuleDays(DRIVER_ID, MainDriverId, getActivity());
       // String lastItemStartDate = "";
        //String currentLogDate = Global.GetCurrentDeviceDate();
        DateTime currentJodaDateTime = Global.GetCurrentJodaDateTime();
        DateTime currentJodaUtcDateTime = Global.GetCurrentUTCDateTime();

        if(isDeferralOccurred && deferralDays != -1 ){
            RulesResponseObject deferralObj;
            int rulesVersion = SharedPref.GetRulesVersion(getActivity());
            oDriverLogDetail = hMethods.getSavedLogList(Integer.valueOf(DRIVER_ID), currentJodaDateTime, currentJodaUtcDateTime, dbHelper);
            //lastItemStartDate = Global.ConvertDateFormatMMddyyyy(oDriverLogDetail.get(oDriverLogDetail.size()-1).getStartDateTime().toString());

         //   if(lastItemStartDate.equals(currentLogDate)){
                oDriverDetail = hMethods.getDriverList(currentJodaDateTime, currentJodaUtcDateTime, Integer.valueOf(DRIVER_ID),
                    offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver, DRIVER_JOB_STATUS, isOldRecord,
                    isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                    rulesVersion, oDriverLogDetail, getActivity());

                if(CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2) ) {
                    oDriverDetail.setCanAdverseException(isAdverseExcptn);
                }

                if(deferralDays == 2){
                    deferralObj = localCalls.SecondDayDeferralLeftHours(oDriverDetail);
                }else{
                    deferralObj = localCalls.IsEligibleDeferralRule(oDriverDetail);
                }

                int leftOffOrSleeperMin = (int) deferralObj.getLeftOffOrSleeperMinutes();

                try {
                    if (deferralDialog != null && deferralDialog.isShowing())
                        deferralDialog.dismiss();

                    if(deferralDays == 0) {
                        updateDeferralRule(leftOffOrSleeperMin, deferralDays);
                    }else{
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            deferralDialog = new DeferralDialog(getActivity(), leftOffOrSleeperMin, deferralDays, new DeferralListener());
                            deferralDialog.show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                setExceptionView();

            }

       // }

    }


    void updateDeferralRule(int leftOffOrSleeperMin, int deferralDays){
        try{
            boolean isDeferral = true;
            if(deferralDays == 0){
                isDeferral = false;
            }
            if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                SharedPref.setDeferralForMain(isDeferral, Globally.GetCurrentDateTime(), "" +deferralDays, getActivity());
            }else{
                SharedPref.setDeferralForCo(isDeferral, Globally.GetCurrentDateTime(), "" +deferralDays, getActivity());
            }

            JSONArray savedDeferralArray = deferralMethod.getSavedDeferralArray(Integer.valueOf(DRIVER_ID), dbHelper);
            JSONObject deferralObj = deferralMethod.GetDeferralJson(DRIVER_ID, DeviceId, TruckNumber, DriverCompanyId,
                    Globally.LATITUDE, Globally.LONGITUDE, constants.get2DecimalEngHour(getActivity()),   //SharedPref.getObdEngineHours(getActivity()),
                    SharedPref.getObdOdometer(getActivity()),""+leftOffOrSleeperMin, ""+deferralDays);
            savedDeferralArray.put(deferralObj);

            // save deferral event inn local db and push automatically later. Reason behind this to save in offline also when internet is not working.
            deferralMethod.DeferralLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, savedDeferralArray);


            SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());
            startService();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private class DeferralListener implements DeferralDialog.DeferralListener{

        @Override
        public void JobBtnReady(int leftOffOrSleeperMin, int deferralDays) {

            updateDeferralRule(leftOffOrSleeperMin, deferralDays);
        }

        @Override
        public void CancelBtnReady() {
        }
    }



    /*================== Confirmation Listener ====================*/
    private class ConfirmListener implements ConfirmationDialog.ConfirmationListener {

        @Override
        public void OkBtnReady() {

            if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                SharedPref.setUnidentifiedAlertViewStatus(false, getActivity());
            }else{
                SharedPref.setUnidentifiedAlertViewStatusCo(false, getActivity());
            }


            confirmationDialog.dismiss();
            TabAct.host.setCurrentTab(11);
        }
    }


    private class DateListener implements DatePickerDialog.DatePickerListener {
        @Override
        public void JobBtnReady(String SelectedDate, String dayOfTheWeek, String MonthFullName, String MonthShortName, int dayOfMonth) {

            try {
                if (dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            try {

                FragmentManager fragManager = getActivity().getSupportFragmentManager();
                initilizeEldView.MoveFragment(SelectedDate, dayOfTheWeek, MonthFullName, MonthShortName, dayOfMonth, isCertifyLog,
                        VIN_NUMBER, offsetFromUTC, Global.FinalValue(LeftWeekOnDutyHoursInt), Global.FinalValue(LeftDayOnDutyHoursInt),
                        Global.FinalValue(LeftDayDrivingHoursInt), CurrentCycleId, VehicleId,
                        isCertifySignPending(false , false), isFragmentAdd, fragManager, driverLogArray.toString());
            } catch (final Exception e) {
                e.printStackTrace();
            }

        }
    }


    private class ContinueListener implements ContinueStatusDialog.ContinueListener{

        @Override
        public void ContinueBtnReady(String TruckIgnitionStatus) {
            SharedPref.SetTruckStartLoginStatus(false, getActivity());
            SharedPref.SetTruckIgnitionStatusForContinue(TruckIgnitionStatus, "home", Global.getCurrentDate(), getActivity());
            CalculateTimeInOffLine(false, false);
        }

        @Override
        public void CancelBtnReady(String TruckIgnitionStatus, boolean isYardMovee) {
            SharedPref.SetTruckStartLoginStatus(false, getActivity());
            SharedPref.SetTruckIgnitionStatusForContinue(TruckIgnitionStatus, "home", Global.getCurrentDate(), getActivity());

            if (Globally.VEHICLE_SPEED < 10) {
                if(isYardMovee){
                    Reason = "Others";
                    DriverStatusId = Global.ON_DUTY;
                    isYardMove = false;
                    isYardBtnClick = false;
                    SaveDriverJob(Global.ON_DUTY);
                }else {
                    SaveOffDutyStatus();
                }
            } else {
                Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                //SaveDrivingStatus();
            }
        }
    }


    private class TrailorListener implements TrailorDialog.TrailorListener {

        @Override
        public void CancelReady() {

            try {
                if (trailerDialog != null && trailerDialog.isShowing())
                    trailerDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            // isOnDutyTouch = false;
            SaveJobType(String.valueOf(oldStatusView));
            SetJobButtonView(oldStatusView, isViolation, isPersonal);
            setJobButtonViewTrue(oldStatusView, isPersonal);

            EnableJobViews();
            progressBar.setVisibility(View.GONE);
            Globally.hideSoftKeyboard(getActivity());

        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void JobBtnReady(final String TrailorNo, String reason, String type, boolean isUpdatedTrailer, int ItemPosition, EditText TrailorNoEditText, EditText ReasonEditText) {

            try {
                if (type.equals("on_duty")) {

                    ptiSelectedtxt = "";
                    // if(TrailorNo.length() > 0) {
                    if (reason.length() > 0) {
                        HideKeyboard(ReasonEditText);
                        constants.IS_TRAILER_INSPECT = isUpdatedTrailer;

                        if (reason.equals("Trailer Switch") && TrailorNo.length() == 0) {
                            Global.EldScreenToast(ReasonEditText, ConstantsEnum.SELECT_TRAILER_ALERT, getContext().getResources().getColor(R.color.red_eld));

                        } else if (reason.equals("Trailer Switch") && TrailorNumber.equals(TrailorNo)) {
                            Global.EldScreenToast(ReasonEditText, ConstantsEnum.CHANGE_TRAILER_ALERT, getContext().getResources().getColor(R.color.red_eld));

                        } else {
                            String LastReason = "", lastItemIsYardMove = "false";
                            int LastStatus = 1;
                            try {
                                JSONObject lastItemJson = hMethods.GetLastJsonFromArray(driverLogArray);
                                LastStatus = lastItemJson.getInt(ConstantsKeys.DriverStatusId);
                                LastReason = lastItemJson.getString(ConstantsKeys.Remarks);
                                lastItemIsYardMove = lastItemJson.getString(ConstantsKeys.YardMove);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            boolean isAlreadyYardMove = false;
                            if (reason.equals(getResources().getString(R.string.yard_move))) {
                                isYardBtnClick = true;

                                if (lastItemIsYardMove.equals("true")) {
                                    isAlreadyYardMove = true;
                                }
                            }

                            if (LastStatus == ON_DUTY && (LastReason.equals(reason) || isAlreadyYardMove)) {
                                Global.EldScreenToast(TrailorNoEditText, "You already saved (" + reason + ") reason. Please change the Reason to save new status for OnDuty.", getResources().getColor(R.color.colorVoilation));
                            } else {

                                // ------------- Save Trailer Number in shared pref -------------
                                SaveTrailerLocally(TrailorNo);


                                getActivity().runOnUiThread(new Runnable(){
                                    public void run() {
                                        if (TrailorNo.trim().length() == 0) {
                                            Global.EldScreenToast(OnDutyBtn, "Trailer number not saved.", getResources().getColor(R.color.colorPrimary));
                                        }
                                        if (TrailorNumber.trim().length() == 0)
                                            trailorLayout.startAnimation(emptyTrailerNoAnim);
                                        else
                                            emptyTrailerNoAnim.cancel();
                                    }
                                });

                                trailorTv.setText(TrailorNumber);
                                Reason = reason;

                                IsPrePost = false;
                                constants.IS_TRAILER_INSPECT = false;

                                if (Reason.contains(getResources().getString(R.string.pre_trip)) ||
                                        Reason.contains(getResources().getString(R.string.post_trip))) {
                                    IsPrePost = true;
                                    constants.IS_TRAILER_INSPECT = false;
                                    ptiSelectedtxt = reason;
                                } else if (Reason.equals(getResources().getString(R.string.TrailerPickup)) ||
                                        Reason.equals(getResources().getString(R.string.TrailerSwitch))) {
                                    IsPrePost = true;
                                    constants.IS_TRAILER_INSPECT = true;
                                    if (Reason.equals(getResources().getString(R.string.TrailerSwitch))) {
                                        showShippingDialog(true);
                                    }
                                } else if (Reason.equals(getResources().getString(R.string.Unloading)) || Reason.equals(getResources().getString(R.string.loading))) {
                                    if (Reason.equals(getResources().getString(R.string.loading))) {
                                        if (SharedPref.IsDrivingShippingAllowed(getContext()) && shipmentMethod.isLoadingRequired(DRIVER_ID, dbHelper)) {
                                            showShippingDialog(false);
                                        }
                                    } else {
                                        showShippingDialog(true);
                                    }
                                } else if (Reason.equals(getResources().getString(R.string.TrailerDrop))) {
                                    SaveTrailerLocally("");
                                    trailorTv.setText(TrailorNumber);
                                    showShippingDialog(true);
                                } else if (Reason.equals(getResources().getString(R.string.yard_move))) {
                                    Reason = ReasonEditText.getText().toString().trim();
                                    SharedPref.SetTruckStartLoginStatus(false, getActivity());
                                } else if(Reason.equals("Border Crossing") && isAgriException){
                                    Reason = Reason +" (AG End)";

                                    String address = SharedPref.getAgricultureRecord("AgricultureAddress",getContext());
                                    if(!address.equals("")) {

                                        SaveAgricultureRecord(Global.getCurrentDateLocal(), Globally.GetCurrentUTCTimeFormat(),
                                                TruckNumber, DRIVER_ID, DriverCompanyId, address,
                                                SharedPref.getAgricultureRecord("AgricultureLatitude", getActivity()),
                                                SharedPref.getAgricultureRecord("AgricultureLongitude", getActivity()),
                                                SharedPref.getObdEngineHours(getContext()), SharedPref.getObdOdometer(getActivity()), "0");
                                    }

                                }

                                if (TrailorNumber.length() == 0 ||
                                        TrailorNumber.equals(getResources().getString(R.string.no_trailer))) {
                                    // IsPrePost = false;
                                    constants.IS_TRAILER_INSPECT = false;
                                }

                                // -------------- Update Trailer Number in Local Array -------------
                                recapViewMethod.UpdateTrailerInRecapArray(new JSONArray(), SelectedDate, TrailorNumber, DRIVER_ID, dbHelper);

                                isPersonal = "false";
                                SharedPref.OdometerSaved(false, getActivity());
                                GetDriversSavedData(false, DriverType);
                                SWITCH_VIEW = ON_DUTY;
                                DriverStatusId = String.valueOf(SWITCH_VIEW);
                                SaveRequestCount = 0;
                                // BackgroundLocationService.IsAutoLogSaved = false;

                                if (reason.equals(getResources().getString(R.string.BorderCrossing))) {
                                    if (!IsAOBRD || IsAOBRDAutomatic) {
                                        if (CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2)) {
                                            Country = "USA";
                                            // Make Operating Zone default value for south canada in US
                                            SharedPref.SetNorthCanadaStatus(false, getActivity());
                                        } else {
                                            Country = "CANADA";
                                        }

                                        GetDriverCycle();
                                    }

                                    Global.SaveCurrentCycle(Country, "border_crossing", getActivity());

                                    try{
                                        if(isAdverseExcptn) {
                                            isAdverseExcptn = false;

                                            if (DriverType == Constants.MAIN_DRIVER_TYPE)
                                                SharedPref.setAdverseExcptn(isAdverseExcptn, getActivity());
                                            else
                                                SharedPref.setAdverseExcptnCo(isAdverseExcptn, getActivity());

                                            if(getActivity() != null && !getActivity().isFinishing()) {
                                                Toast.makeText(getActivity(), "Adverse Exception has been ended due to cycle change.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }catch (Exception e){}


                                }


                                SetDataInView();


                          /*      if (Global.isConnected(getActivity())) {
                                    saveInfo(Global.ON_DUTY, true, true, false);
                                } else {*/

                                    SaveDriverJob(Global.ON_DUTY);
                                    EnableJobViews();

                                    Global.EldScreenToast(OnDutyBtn, "You are now ON DUTY but not driving.", getResources().getColor(R.color.colorPrimary));

                                   // GetDriverLogData();
                                    initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                                            usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);
                                    SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);



                                if (constants.IS_TRAILER_INSPECT) {
                                    if (TrailorNumber.length() > 0) {
                                        // Global.InspectTrailerDialog(getActivity(), "Trailer Inspection !!", "You need to inspect your updated trailer.", inspectDialog);
                                        IsPrePost = false;
                                        if (TrailorNumber.equals(getResources().getString(R.string.no_trailer))) {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    TabAct.host.setCurrentTab(4);
                                                }
                                            }, 1500);
                                        } else {
                                            Global.InspectTrailerDialog(getActivity(), "Trailer Inspection !!", "You need to inspect your updated trailer.", inspectDialog);
                                            CalculateTimeInOffLine(false, false);
                                        }
                                    }
                                } else {
                                    if (IsPrePost) {
                                        if (ptiSelectedtxt.contains(getResources().getString(R.string.pre_trip_unloading))) {
                                            showShippingDialog(true);
                                        }

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                TabAct.host.setCurrentTab(4);
                                            }
                                        }, 1500);
                                    }
                                }


                                try {
                                    if (trailerDialog != null && trailerDialog.isShowing())
                                        trailerDialog.dismiss();

                                    if(!IsPrePost){
                                        CalculateTimeInOffLine(false, false);

                                        // After save locally push data to server
                                        pushStatusToServerAfterSave();

                                        // call lat long API
                                        IsAddressUpdate = true;
                                        GetAddFromLatLng();

                                    }else{
                                        progressBar.setVisibility(View.GONE);
                                        EnableJobViews();
                                    }



                                } catch (final IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (final Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //  }

                        }
                    } else {
                        ReasonEditText.requestFocus();
                        Global.EldScreenToast(TrailorNoEditText, "Enter reason to save the status.", getResources().getColor(R.color.colorSleeper));
                    }

                } else if (type.equals(Constants.Personal)) {
                    Reason = reason;
                    SavePersonalStatus();
                    HideKeyboard(ReasonEditText);

                    TrailorNumber = TrailorNo.trim();
                    SaveTrailerLocally(TrailorNumber);
                    trailorTv.setText(TrailorNumber);


                    try {
                        if (trailerDialog != null && trailerDialog.isShowing())
                            trailerDialog.dismiss();
                    } catch (final IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }


                    // call lat long API
                    IsAddressUpdate = true;
                    GetAddFromLatLng();

                } else if (type.equals("trailor_driving")) {
                    HideKeyboard(ReasonEditText);
                    TrailorNumber = TrailorNo.trim();
                    SaveTrailerLocally(TrailorNumber);
                    trailorTv.setText(TrailorNumber);

                    DrivingBtnClick();

                    try {
                        if (trailerDialog != null && trailerDialog.isShowing())
                            trailerDialog.dismiss();
                    } catch (final IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    HideKeyboard(ReasonEditText);

                        TrailorNumber = TrailorNo.trim();

                        if (TrailorNumber.equalsIgnoreCase("null"))
                            TrailorNumber = "";

                        getActivity().runOnUiThread(new Runnable(){
                            public void run() {
                                if (TrailorNumber.trim().length() == 0)
                                    trailorLayout.startAnimation(emptyTrailerNoAnim);
                                else
                                    emptyTrailerNoAnim.cancel();
                            }
                        });


                        try {
                            if (trailerDialog != null && trailerDialog.isShowing())
                                trailerDialog.dismiss();
                        } catch (final IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }

                    if (Global.isConnected(getActivity())) {
                        if(DriverJsonArray.length() > 0) {
                            isSaveTrailerPending = true;
                            // trying to push driver job status unposted data to server with trailer save
                            SAVE_DRIVER_STATUS();
                        }else {
                            SaveTrailerNumber(DRIVER_ID, DeviceId, TrailorNumber);
                        }
                    }else{
                        SaveTrailerEvent("Saved Successfully");
                    }


                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private class RemainingTimeListener implements RemainingTimeDialog.RemainingTimeListener {

        @Override
        public void CancelReady() {

            progressBar.setVisibility(View.GONE);
            try {
                if (remainingDialog != null && remainingDialog.isShowing())
                    remainingDialog.dismiss();

                EnableJobViews();
            } catch (final Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void OkBtnReady(int JobStatus) {
            try {
                if (remainingDialog != null && remainingDialog.isShowing())
                    remainingDialog.dismiss();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            if (JobStatus == DRIVING) {
                Driving();
            } else {
                isRemainingView = true;
                OnDuty(OnDutyBtn);
            }
        }

    }


    private class VehicleListener implements VehicleDialog.VehicleListener {
        @Override
        public void ChangeVehicleReady(String Title, int position, boolean isOldDialog) {

            VehicleListenerEvent(Title, position, isOldDialog);
        }

    }

    private class OldVehicleListener implements VehicleDialogOld.VehicleListener {
        @Override
        public void ChangeVehicleReady(String Title, int position, boolean isOldDialog) {
            VehicleListenerEvent(Title, position, isOldDialog);

        }

    }


    private class VehicleLoginListener implements VehicleDialogLogin.VehicleLoginListener {
        @Override
        public void ChangeVehicleReady(String Title, int position, View view, boolean isOldDialog) {
            VehicleListenerLoginEvent(Title, position, view, isOldDialog);

        }

        @Override
        public void ContinueBtnReady(boolean isContinue, final String CoDriverId, final String CompanyId, int position, boolean isOldDialog) {
            try {
                if (vehicleDialogLogin != null && vehicleDialogLogin.isShowing())
                    vehicleDialogLogin.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            //  ShowRecapDialog();
        }
    }


    private class OldVehicleLoginListener implements VehicleDialogLoginOld.VehicleLoginListener {
        @Override
        public void ChangeVehicleReady(String Title, int position, View view, boolean isOldDialog) {
            VehicleListenerLoginEvent( Title, position, view, isOldDialog);

        }

        @Override
        public void ContinueBtnReady(boolean isContinue, final String CoDriverId, final String CompanyId, int position, boolean isOldDialog) {
            try {
                if (vehicleDialogLoginOld != null && vehicleDialogLoginOld.isShowing())
                    vehicleDialogLoginOld.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            //  ShowRecapDialog();
        }
    }


    void VehicleListenerLoginEvent(String Title, int position, View view, boolean isOldDialog){
        try{
            if(getActivity() != null && !getActivity().isFinishing()){
                if (!Title.equals(ConstantsEnum.StrTruckAttachedTxt)) {
                    if (Global.isConnected(getActivity())) {
                        LoginTruckChange = "true";
                        progressDialog.show();
                        loginDialogView = view;
                        VehicleBtnClick(Title, position);
                    } else {
                        Global.EldScreenToast(view, Global.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                    }

                } else {
                    Global.EldScreenToast(view, Title, getResources().getColor(R.color.colorVoilation));
                    getActivity().finish();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void VehicleListenerEvent(String Title, int position, boolean isOldDialog){
        if (!Title.equals(ConstantsEnum.StrTruckAttachedTxt)) {
            if (Global.isConnected(getActivity())) {
                LoginTruckChange = "false";
                VehicleBtnClick(Title, position);

                try {
                    if(isOldDialog){
                        if (vehicleDialogOld != null && vehicleDialogOld.isShowing())
                            vehicleDialogOld.dismiss();
                    }else {
                        if (vehicleDialog != null && vehicleDialog.isShowing())
                            vehicleDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                Global.EldScreenToast(OnDutyBtn, Global.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            }
        } else {
            Global.EldScreenToast(OnDutyBtn, Title, getResources().getColor(R.color.colorVoilation));
            getActivity().finish();
        }
    }


    void openOldVehicleDialog(boolean IsVehicleDialogShown){
        try {
            if(IsVehicleDialogShown && getActivity() != null && !getActivity().isFinishing()) {
                if (vehicleDialogOld != null && vehicleDialogOld.isShowing()) {
                    vehicleDialogOld.dismiss();
                }
                isOldVehicleDialog = true;
                vehicleDialogOld = new VehicleDialogOld(getActivity(), TruckNumber, true, TabAct.vehicleList, new OldVehicleListener());
                vehicleDialogOld.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void openOldVehicleDialogLogin(){
        try {
            if (getActivity() != null && !getActivity().isFinishing()) {
                if (vehicleDialogLoginOld != null && vehicleDialogLoginOld.isShowing())
                    vehicleDialogLoginOld.dismiss();

                isOldVehicleDialog = true;
                vehicleDialogLoginOld = new VehicleDialogLoginOld(getActivity(), getActivity(), TruckNumber, true,
                        TabAct.vehicleList, new OldVehicleLoginListener());
                vehicleDialogLoginOld.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void SaveTrailerEvent(String Message){
        try {
            Global.EldScreenToast(OnDutyBtn, Message, getResources().getColor(R.color.colorPrimary));

            trailorTv.setText(TrailorNumber);
            SaveTrailerLocally(TrailorNumber);

            // -------------- Update Trailer Number in Local Array -------------
            recapViewMethod.UpdateTrailerInRecapArray(new JSONArray(), SelectedDate, TrailorNumber, DRIVER_ID, dbHelper);

            if(!shipmentMethod.isShippingCleared(DRIVER_ID, dbHelper)) {
                if (TrailorNumber.equals(getResources().getString(R.string.no_trailer)) || TrailorNumber.trim().length() == 0) {
                    showShippingDialog(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void SaveVehicleNumber(int position, String DRIVER_Id, String CoDriverId, String DriverCompanyId, String LoginTruckChange, boolean isShown) {
        IsObdVehShown = isShown;

        UpdateOBDAssignedVehicle(
                DRIVER_Id,
                CoDriverId,
                DeviceId,
                TabAct.vehicleList.get(position).getPreviousDeviceMappingId(),
                TabAct.vehicleList.get(position).getDeviceMappingId(),
                TabAct.vehicleList.get(position).getVehicleId(),
                TabAct.vehicleList.get(position).getEquipmentNumber(),
                TabAct.vehicleList.get(position).getPlateNumber(),
                TabAct.vehicleList.get(position).getVIN(),
                DriverCompanyId,
                SharedPref.getImEiNumber(getActivity()),
                LoginTruckChange);

    }



    private class DriverLocationListener implements DriverLocationDialog.LocationListener {

        @Override
        public void CancelLocReady(boolean isMalfunction, int JobType) {
            IsPopupDismissed = false;
            EnableJobViews();
            LocationType = "";
            try {
                if (driverLocationDialog != null && driverLocationDialog.isShowing())
                    driverLocationDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            if(isMalfunction){

                if(!isExemptDriver) {

                    if (SharedPref.isLocMalfunctionOccur(getActivity()) ) {
                        LocationType = "E";
                        SharedPref.setLocationEventType(LocationType, getActivity());

                        // save driver job status on ignore location
                        isLocMalfunction = false;
                        saveInAobrdMalfnModeStatus(JobType);


                    }else {
                        LocationType = "X";
                        SharedPref.setLocationEventType(LocationType, getActivity());
                        currentUtcTimeDiffFormat = Global.GetCurrentUTCTimeFormat();

                        // save driver job status on ignore location with missing diagnostic
                        isLocMalfunction = false;
                        saveInAobrdMalfnModeStatus(JobType);

                        // save missing diagnostic
                        saveMissingDiagnostic(getString(R.string.ignore_to_save_loc), currentUtcTimeDiffFormat);

                        Globally.PlayNotificationSound(getActivity());
                        Global.ShowLocalNotification(getActivity(),
                                getResources().getString(R.string.missing_dia_event),
                                getResources().getString(R.string.missing_event_occured_desc), 2091);

                        SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getActivity()),
                                SharedPref.isMalfunctionOccur(getActivity()), true,
                                SharedPref.isSuggestedEditOccur(getActivity()), getActivity());

                        setMalfnDiagnEventInfo();
                    }
                }

            }


        }

        @Override
        public void SaveLocReady(int position, int spinnerItemPos, int JobType, String city, EditText CityNameEditText,
                                 View view, boolean isMalfunction) {

            City = city;
            OldSelectedStatePos = spinnerItemPos;
            LocationType = "";

            if (spinnerItemPos < StateList.size()) {
                State = StateList.get(spinnerItemPos).getStateCode();
                Country = StateList.get(spinnerItemPos).getCountry();
            }

            if (City.length() > 0) {

                if(isMalfunction  && Globally.LATITUDE.length() < 5) {
                    if (SharedPref.isLocMalfunctionOccur(getActivity())) {
                        LocationType = "E";
                    }else{
                        LocationType = "M";
                        if(SharedPref.isLocDiagnosticOccur(getActivity())){
                            Constants.isClearMissingCompEvent = true;
                            SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());
                            startService();

                        }

                    }
                    isLocMalfunction = true;
                    SharedPref.setLocationEventType(LocationType, getActivity());
                }

                try {
                    if (driverLocationDialog != null && driverLocationDialog.isShowing())
                        driverLocationDialog.dismiss();
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                HideKeyboard(CityNameEditText);

                saveInAobrdMalfnModeStatus(JobType);

            } else {
                Global.EldScreenToast(CityNameEditText, "Please enter city name", getResources().getColor(R.color.colorVoilation));
            }
        }
    }



    private void saveMissingDiagnostic(String remarks, String currentDateTime){
        try {
            String type = Global.JobStatus(DRIVER_JOB_STATUS, Boolean.parseBoolean(isPersonal), ""+DRIVER_JOB_STATUS);

            // save malfunction occur event to server with few inputs
            JSONObject newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
                                DRIVER_ID, DeviceId, SharedPref.getVINNumber(getActivity()),
                                SharedPref.getTruckNumber(getActivity()),   //DriverConst.GetDriverTripDetails(DriverConst.Truck, getActivity()),
                                DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity()),
                                constants.get2DecimalEngHour(getActivity()), //SharedPref.getObdEngineHours(getActivity()),
                                SharedPref.getObdOdometer(getActivity()),
                                SharedPref.getObdOdometer(getActivity()),
                                currentDateTime, constants.MissingDataDiagnostic,
                                 remarks + " " + type,
                                false, "", "",
                                "", LocationType, type);

            // save Occurred event locally until not posted to server
            JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
            malArray.put(newOccuredEventObj);
            malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, malArray);

            MalfunctionDefinition = Constants.ConstLocationMissing;

            // save malfunction entry in duration table
            malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, DRIVER_ID,
                    currentDateTime, currentDateTime,
                    Constants.MissingDataDiagnostic, ""+DRIVER_JOB_STATUS, LocationType, type,
                    constants, getActivity());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void SaveTrailerLocally(String trailerNumber) {
        if(trailerNumber.equalsIgnoreCase("null"))
            trailerNumber = "";

        TrailorNumber = trailerNumber;
        SharedPref.setTrailorNumber(trailerNumber, getActivity());
    }


    void saveInAobrdMalfnModeStatus(int JobType){
        switch (JobType) {

            case OFF_DUTY:
                Global.SaveCurrentCycle(Country, "", getActivity());
                SetDataInView();
                SaveOffDutyStatus();
                break;

            case SLEEPER:
                Global.SaveCurrentCycle(Country, "", getActivity());
                SetDataInView();

                SaveSleeperStatus();
                break;

            case DRIVING:

                Global.SaveCurrentCycle(Country, "", getActivity());
                SetDataInView();
                isPersonal = "false";
                SaveDrivingStatus();

                break;


            case ON_DUTY:

                SaveOnDutyStatus();
                break;

            case PERSONAL:

               // SavePersonalStatus();

                if(getActivity() != null && !getActivity().isFinishing()) {
                    if (trailerDialog != null && trailerDialog.isShowing())
                        trailerDialog.dismiss();

                    trailerDialog = new TrailorDialog(getActivity(), constants.Personal, false,
                            TrailorNumber, 0, false, Global.onDutyRemarks, oldStatusView, dbHelper, new TrailorListener());
                    trailerDialog.show();
                }

                break;
        }
    }

    void HideKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }


    void CalculateTimeInOffLine(boolean IsCheckOffSleeperTime, boolean onResume) {


        RulesObj = new RulesResponseObject();
        RemainingTimeObj = new RulesResponseObject();
        DateTime currentDateTime = Global.getDateTimeObj(Global.GetCurrentDateTime(), false);    // Current Date Time
        DateTime currentUTCTime = Global.getDateTimeObj(Global.GetCurrentUTCTimeFormat(), true);
        offsetFromUTC = (int) Global.GetTimeZoneOffSet();

        try {
            driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
            driverLogArray = new JSONArray();
        }

        // get driver's current cycle id
        GetDriverCycle();
        isSingleDriver = Global.isSingleDriver(getActivity());
        isYardMove = false;

        getExceptionStatus();

        if(IsCheckOffSleeperTime) {
            int rulesVersion = SharedPref.GetRulesVersion(getActivity());
            getDailyOffLeftMinObj = hMethods.getDailyOffLeftMinutes(currentDateTime, currentUTCTime, offsetFromUTC,
                    Integer.valueOf(CurrentCycleId), isSingleDriver, Integer.valueOf(DRIVER_ID), LATEST_JOB_STATUS, isOldRecord,
                    isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                    rulesVersion, dbHelper, getActivity());

            EnableJobViews();
        }else {

            if (driverLogArray.length() == 0) {
                DRIVER_JOB_STATUS = 1;
                isPersonal = "false";

                clearCalculationsView();

                initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                        usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);
                SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);
                GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
            } else {

                JSONObject lastJsonItem = new JSONObject();
                JSONArray selectedArray = new JSONArray();
                boolean isArrayNull = false;

                try {
                    currentDayArray = hMethods.GetSingleDateArray(driverLogArray, currentDateTime, currentDateTime, currentUTCTime, true, offsetFromUTC);


                    if ( currentDayArray.length() == 0 ) {

                        /* ========= carry forward same status up to current day instantly to update driver log rule. (Date diff) ============ */

                        String lastItemEndTime = hMethods.getLastStatusDateTime(driverLogArray);
                        DateTime lastObjDateTime = Globally.getDateTimeObj(lastItemEndTime, false ).minusHours(Math.abs(offsetFromUTC));

                        int dayDiff = Days.daysBetween(lastObjDateTime.toLocalDate(), currentDateTime.toLocalDate()).getDays();
                        //(int) Constants.getDateTimeDuration(lastObjDateTime, currentDateTime).getStandardDays();

                        // get last item of array
                        JSONObject lastObj = (JSONObject) driverLogArray.get(driverLogArray.length()-1);

                        // updated end dateTime in last item of array
                        JSONObject updatedLstObj = hMethods.updateLastItemFromArray(driverLogArray, lastObj, currentDateTime, offsetFromUTC);
                        driverLogArray.put(driverLogArray.length()-1, updatedLstObj);

                        // add Skipped Days in 18 days log Array
                        driverLogArray = hMethods.addSkipDaysItemsInArray(driverLogArray, lastObjDateTime, offsetFromUTC, dayDiff);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // save updated log array into the database
                        hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, driverLogArray); // saving in db after updating the array

                        currentDayArray = hMethods.GetSingleDateArray(driverLogArray, currentDateTime, currentDateTime, currentUTCTime, true, offsetFromUTC);
                        if ( currentDayArray.length() == 0 ) {
                            isArrayNull = true;
                        }else{
                            isArrayNull = false;
                        }

                        selectedArray = hMethods.GetSelectedDateArray(driverLogArray, DRIVER_ID, currentDateTime, currentDateTime,
                                currentUTCTime, offsetFromUTC, 2, dbHelper);

                        if(selectedArray.length() == 0){
                            isArrayNull = false;
                        }


                        // call 18 days API to get accurate 18 days updated log
                        if( Global.isConnected(getActivity()) && DriverJsonArray.length() == 0 && isEldRuleAlreadyCalled == false) {
                            isEldRuleAlreadyCalled = true;
                            GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
                        }else{
                            if(isEldRuleAlreadyCalled == false) {
                               // Log.d("isEldRuleAlreadyCalled", "------------CalculateTimeInOffLine called " );
                                isEldRuleAlreadyCalled = true;
                                CalculateTimeInOffLine(false, true);
                            }
                        }

                    } else {
                        selectedArray = hMethods.GetSelectedDateArray(driverLogArray, DRIVER_ID, currentDateTime, currentDateTime,
                                currentUTCTime, offsetFromUTC, 2, dbHelper);
                        if(selectedArray.length() == 0){
                            isArrayNull = false;
                        }

                        lastJsonItem = hMethods.GetLastJsonFromArray(driverLogArray);
                        if(!lastJsonItem.getString(ConstantsKeys.CurrentCycleId).equals(CurrentCycleId)) {
                            // update Cycle ID in 18 days log array last item because ELD Rules used log array's last item cycle ID.
                            driverLogArray = hMethods.updateLastJob(driverLogArray, CurrentCycleId);
                            // saving in db after updating the array
                            hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, driverLogArray);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // Calculate 18 days log data
                if (!isArrayNull && lastJsonItem.length() > 0) {
                    try {

                        DRIVER_JOB_STATUS = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
                        isPersonal = lastJsonItem.getString(ConstantsKeys.Personal);
                        String currentJob = SharedPref.getDriverStatusId(getActivity());

                        // if log array last status is not equal to current status. Then need to refresh 18 days log API
                        if(!currentJob.equals(""+DRIVER_JOB_STATUS) && !IsLogApiRefreshed){
                            IsLogApiRefreshed = true;
                            GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
                        }

                        if (SharedPref.GetNewLoginStatus(getActivity())) {
                            callEldRuleMethod(currentDayArray, currentDateTime, currentUTCTime, onResume);
                        }else{
                            boolean isPopupShown = SharedPref.GetTruckStartLoginStatus(getActivity());
                            isYardMove = lastJsonItem.getBoolean(ConstantsKeys.YardMove);

                            if (isPopupShown && ( isPersonal.equals("true") || (DRIVER_JOB_STATUS == ON_DUTY && isYardMove) ) ) {
                                boolean isPer = false;
                                if(isPersonal.equals("true")){
                                    isPer = true;
                                }
                                YardMovePersonalStatusAlert(isYardMove, isPer, true);
                            }else{
                                if(continueStatusDialog != null && continueStatusDialog.isShowing()){
                                    continueStatusDialog.dismiss();
                                }

                            }

                            callEldRuleMethod(currentDayArray, currentDateTime, currentUTCTime, onResume);

                        }

                        setYardMoveView();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!IsLogApiACalled)
                            GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
                    }
                } else {
                    isViolation = false;
                    SharedPref.SetViolation(isViolation, getActivity());
                    initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                            usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);

                    SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);

                }

                IsLogApiACalled = true;

            }
            // Show Pending Notifications badge count on view
            checkNotificationAlert();

        }
    }


    void callEldRuleMethod( JSONArray currentDayArray, DateTime currentDateTime, DateTime currentUTCTime, boolean onResume){

        SharedPref.setDriverStatusId("" + DRIVER_JOB_STATUS, getActivity());

        TotalOffDutyHoursInt        = hMethods.GetDutyStatusTimeInterval(currentDayArray, constants, EldFragment.OFF_DUTY);
        TotalSleeperBerthHoursInt   = hMethods.GetDutyStatusTimeInterval(currentDayArray, constants, EldFragment.SLEEPER);

        oDriverLogDetail            = hMethods.getSavedLogList(Integer.valueOf(DRIVER_ID), currentDateTime, currentUTCTime, dbHelper);

        if (oDriverLogDetail == null || oDriverLogDetail.size() == 0) {
            if (!IsLogApiACalled)
                GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
        } else {
            CalculateCycleTime(currentDateTime, currentUTCTime, isSingleDriver, onResume);
        }
    }


    void CalculateCycleTime(DateTime currentDateTime, DateTime currentUTCTime, boolean isSingleDriver, boolean onResume) {

        getExceptionStatus();
        if(CurrentCycleId.length() > 0 && !CurrentCycleId.equals("0")) {

            int rulesVersion = SharedPref.GetRulesVersion(getActivity());
            oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DRIVER_ID),
                    offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver, DRIVER_JOB_STATUS, isOldRecord,
                    isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                    rulesVersion, oDriverLogDetail, getActivity());

            if(CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2) ) {
                oDriverDetail.setCanAdverseException(isAdverseExcptn);
            }
            RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), Integer.valueOf(DRIVER_JOB_STATUS), oDriverDetail);

            if (DRIVER_JOB_STATUS == DRIVING || DRIVER_JOB_STATUS == ON_DUTY) {
                if (RulesObj != null) {
                    isViolation = RulesObj.isViolation();
                }
            } else {
                isViolation = false;
                SharedPref.SetIsReadViolation(false, getActivity());
                SharedPref.SetViolation(isViolation, getActivity());
            }

            try {
                // Calculate 2 days data to get remaining Driving/Onduty hours
                RemainingTimeObj = hMethods.getRemainingTime(currentDateTime, currentUTCTime, offsetFromUTC,
                        Integer.valueOf(CurrentCycleId), isSingleDriver, Integer.valueOf(DRIVER_ID), DRIVER_JOB_STATUS, isOldRecord,
                        isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                        rulesVersion, dbHelper, getActivity());

                LeftWeekOnDutyHoursInt      = (int) RulesObj.getCycleRemainingMinutes();
                TotalOnDutyHoursInt         = (int) RemainingTimeObj.getOnDutyUsedMinutes();   // hMethods.GetOnDutyTime(currentDayArray);
                TotalDrivingHoursInt        = (int) RemainingTimeObj.getDrivingUsedMinutes();  // hMethods.GetDrivingTime(currentDayArray);
                LeftDayOnDutyHoursInt       = (int) RemainingTimeObj.getOnDutyRemainingMinutes();
                LeftDayDrivingHoursInt      = (int) RemainingTimeObj.getDrivingRemainingMinutes();
              //  TotalSleeperBerthHoursInt   = (int) RemainingTimeObj.getSleeperUsedMinutes();
              //  TotalOffDutyHoursInt        = (int) RemainingTimeObj.getOffDutyUsedMinutes();
                OffDutyPerShift             = (int) RemainingTimeObj.getOffDutyUsedMinutes();
                SleeperPerShift             = (int) RemainingTimeObj.getSleeperUsedMinutes();

                shiftRemainingMinutes       = (int) RemainingTimeObj.getShiftRemainingMinutes();
                shiftUsedMinutes            = (int) RemainingTimeObj.getShiftUsedMinutes();

                minOffDutyUsedHours         = (int) RemainingTimeObj.getMinimumOffDutyUsedHours();
                isMinOffDutyHoursSatisfied  = RemainingTimeObj.isMinimumOffDutyHoursSatisfied();

              //  boolean isDisableAdverseException = RemainingTimeObj.isDisableAdverseException();
               // boolean isDisableShortHaul = RemainingTimeObj.isDisableShortHaul();

                boolean isDisableAdverseException = localCalls.IsAdverseExceptionDisabled(oDriverDetail);
                boolean isDisableShortHaul = localCalls.IsShortHaulExceptionDisabled(oDriverDetail);

                if (isDisableAdverseException) {
                    if (isAdverseExcptn) {
                       // isAdverseExcptn = false;
                        if (DriverType == Constants.MAIN_DRIVER_TYPE)
                            SharedPref.setAdverseExcptn(false, getActivity());
                        else
                            SharedPref.setAdverseExcptnCo(false, getActivity());

                        hMethods.SaveDriversJob(DRIVER_ID, DeviceId, "", getString(R.string.disable_adverse_exception),
                                LocationType, "", false, IsNorthCanada, DriverType, constants,
                                MainDriverPref, CoDriverPref, eldSharedPref, coEldSharedPref,
                                syncingMethod, Global, hMethods, dbHelper, getActivity(), false, CoDriverIdInSaveStatus,
                                CoDriverNameInSaveStatus, false);
                    }

                }

                if (isDisableShortHaul) {

                    if (isHaulExcptn) {
                        if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                            SharedPref.set16hrHaulExcptn(false, getActivity());
                        } else {
                            SharedPref.set16hrHaulExcptnCo(false, getActivity());
                        }
                        hMethods.SaveDriversJob(DRIVER_ID, DeviceId, "", getString(R.string.disable_ShortHaul_exception),
                                LocationType, "", true, IsNorthCanada, DriverType, constants,
                                MainDriverPref, CoDriverPref, eldSharedPref, coEldSharedPref,
                                syncingMethod, Global, hMethods, dbHelper, getActivity(), false, CoDriverIdInSaveStatus,
                                CoDriverNameInSaveStatus, false);
                    }

                }

                setExceptionView();

            } catch (Exception e) {
                LeftWeekOnDutyHoursInt = 0;
                e.printStackTrace();
            }

            initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                    usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);

            DrivingOnDutyCalculations();
            SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);


            if (SharedPref.IsReadViolation(getActivity()) == false && isViolation) {
                if (!onResume)
                    Global.ShowNotificationWithSound(getActivity(), RulesObj, mNotificationManager);
            }

            if(RulesObj.getNotificationType() > 0){
                Global.ShowShiftAlertNotification(getActivity(), RulesObj, mNotificationManager);
            }

            SetTextOnView(RulesObj.getViolationReason().trim());
            ViolationsReason = RulesObj.getViolationReason();


            if(drivingTimeTxtVw.getText().toString().equals(getString(R.string.time_default)) && isTimeDefault){    // call method again (only once) to refresh view with updaed time
                isTimeDefault = false;
                CalculateTimeInOffLine(false, false);
            }
        }

       // GetDriverLogData();

    }


    void DrivingOnDutyCalculations() {

        if (LeftWeekOnDutyHoursInt <= 0) {
            LeftWeekOnDutyHoursInt = 0;
            LeftDayDrivingHoursInt = 0;
            LeftDayOnDutyHoursInt = 0;
        } else if (LeftWeekOnDutyHoursInt < LeftDayDrivingHoursInt || LeftWeekOnDutyHoursInt < LeftDayOnDutyHoursInt) {
            if(LeftWeekOnDutyHoursInt < LeftDayDrivingHoursInt) {
                LeftDayDrivingHoursInt = LeftWeekOnDutyHoursInt;
            }
            LeftDayOnDutyHoursInt = LeftWeekOnDutyHoursInt;
        }
        if (LeftDayDrivingHoursInt < 0)
            LeftDayDrivingHoursInt = 0;

        if (LeftDayOnDutyHoursInt < 0)
            LeftDayOnDutyHoursInt = 0;
    }


    void SetTextOnView(String ViolatioMsg) {
        try {
            String TotalSleeper = Global.FinalValue(TotalSleeperBerthHoursInt);
            String TotalOffDuty = Global.FinalValue(TotalOffDutyHoursInt);
            String TotalOnDuty = Global.FinalValue(TotalOnDutyHoursInt);
            String TotalDriving = Global.FinalValue(TotalDrivingHoursInt);
            String LeftOnDuty = Global.FinalValue(LeftDayOnDutyHoursInt);
            String LeftDriving = Global.FinalValue(LeftDayDrivingHoursInt);
            String LeftCycleTime = Global.FinalValue(LeftWeekOnDutyHoursInt);

            onDutyTimeTxtVw.setText("U " + TotalOnDuty + " R " + LeftOnDuty);
            drivingTimeTxtVw.setText("U " + TotalDriving + " R " + LeftDriving);
            sleeperTimeTxtVw.setText("U " + TotalSleeper);
            offDutyTimeTxtVw.setText("U " + TotalOffDuty);

            WeeklyRemainingTime = LeftCycleTime;
            DrivingRemainingTime = LeftDriving;
            OnDutyRemainingTime = LeftOnDuty;


            if (DRIVER_JOB_STATUS == DRIVING) {

                jobTimeRemngTxtVw.setText(LeftDriving);

                if (isViolation && ViolatioMsg.length() > 0) {
                    if (!ViolationsReason.equals(ViolatioMsg) || SharedPref.IsReadViolation(getActivity()) == false) {
                        SharedPref.SetIsReadViolation(false, getActivity());
                        Global.SnackBarViolation(StatusMainView, ViolatioMsg, getActivity());
                    }
                }

            } else if (DRIVER_JOB_STATUS == ON_DUTY) {
                // jobTimeTxtVw.setText(TotalOnDuty);
                jobTimeRemngTxtVw.setText(LeftOnDuty);

                if (isViolation && ViolatioMsg.length() > 0) {
                    if (!ViolationsReason.equals(ViolatioMsg) || SharedPref.IsReadViolation(getActivity()) == false) {
                        SharedPref.SetIsReadViolation(false, getActivity());
                        Global.SnackBarViolation(StatusMainView, ViolatioMsg, getActivity());
                    }
                }

            }  /*else if (DRIVER_JOB_STATUS == SLEEPER) {
            jobTimeTxtVw.setText(currentStatusViewInput);
        }else {
            jobTimeTxtVw.setText(TotalOffDutyPerShift);
        }*/

            int asPerCurrentStatus = hMethods.getLastStatusDuration(driverLogArray, DRIVER_JOB_STATUS, Boolean.parseBoolean(isPersonal));
            String currentStatusViewInput = Global.FinalValue(asPerCurrentStatus);
            jobTimeTxtVw.setText(currentStatusViewInput);

            remainingTimeTopTV.setText(Html.fromHtml("Cycle time left <b>" + LeftCycleTime + "</b>"));
            if (CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) && SharedPref.IsNorthCanada(getActivity())) {
                currentCycleTxtView.setText("Cycle 1 (80/7) (N)");
            } else {
                currentCycleTxtView.setText(CurrentCycle);
            }

            if (CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)) {
                cyleFlagImgView.setImageResource(R.drawable.can_flag);
            } else {
                cyleFlagImgView.setImageResource(R.drawable.usa_flag);
            }

            if (asPerCurrentStatus < 2) {
                driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
            }

        }catch (Exception e){}
    }


    void LogoutUser() {
        try {
            Global.ClearAllFields(getActivity());
            Global.StopService(getActivity());

            Intent i = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(i);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ClearLog() {
        try {
            if (DriverJsonArray.length() == 1) {
                if (DriverType == Constants.MAIN_DRIVER_TYPE) // Single Driver Type and Position is 0
                    MainDriverPref.ClearLocFromList(getActivity());
                else
                    CoDriverPref.ClearLocFromList(getActivity());
            }

            if (oldStatusView != 0) {
                SaveJobType(String.valueOf(oldStatusView));
                SetJobButtonView(oldStatusView, isViolation, isPersonal);
            }
        } catch (Exception e) {
        }

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void SAVE_LOG_RESPONSE_PROCESS(boolean isLoad, final boolean IsRecap) {

        try {
            EnableJobViews();
            if (!IsTrailorUpdate) {
                if (isLoad) {
                    String msg;
                    String status = SharedPref.getDriverStatusId(getActivity());
                    if (status.equalsIgnoreCase("null") || status.equals(""))
                        status = "1";

                    if (status.equals(Global.OFF_DUTY)) {
                        City = "";
                        if (isPersonal.equals("true")) {
                            msg = "You have selected truck for Personal Use";
                            Global.EldScreenToast(OnDutyBtn, msg, getResources().getColor(R.color.color_eld_theme));
                            Global.OdometerDialog(getActivity(), "Please Enter Odometer Reading.", true, PERSONAL, personalUseBtn, alertDialog);

                            // save app display status log
                           /* if (SharedPref.IsOdometerFromOBD(getActivity())) {
                                constants.saveAppUsageLog(ConstantsEnum.StatusPuAuto, true, false, obdUtil);
                            } else {
                                constants.saveAppUsageLog(ConstantsEnum.StatusPuMannual, true, true, obdUtil);
                            }*/
                        } else {
                            msg = "You are now Off DUTY";
                            isPersonalOld = "false";

                            if (oldStatusView == DRIVING) {
                                if (!SharedPref.IsOdometerFromOBD(getActivity())) {
                                    IsOdometerReading = true;
                                    odometerLay.startAnimation(OdometerFaceView);
                                }
                            }
                            Global.EldScreenToast(OnDutyBtn, msg, getResources().getColor(R.color.color_eld_theme));


                        }
                    } else if (status.equals(Global.SLEEPER)) {
                        City = "";
                        msg = "Your job status is SLEEPER.";
                        Global.EldScreenToast(OnDutyBtn, msg, getResources().getColor(R.color.color_eld_theme));

                        if (oldStatusView == DRIVING) {
                            if (!SharedPref.IsOdometerFromOBD(getActivity())) {
                                IsOdometerReading = true;
                                odometerLay.startAnimation(OdometerFaceView);
                            }
                        }

                        isPersonalOld = "false";
                    } else if (status.equals(Global.DRIVING)) {
                        City = "";
                        msg = "You are now DRIVING.";
                        Global.EldScreenToast(OnDutyBtn, msg, getResources().getColor(R.color.color_eld_theme));

                        if (!SharedPref.IsOdometerFromOBD(getActivity())) {
                            IsOdometerReading = true;
                            odometerLay.startAnimation(OdometerFaceView);
                        }
                        isPersonalOld = "false";
                    } else {
                        msg = "You are now ON DUTY but not driving.";
                        Global.EldScreenToast(OnDutyBtn, msg, getResources().getColor(R.color.color_eld_theme));
                        isPersonalOld = "false";
                        if (!SharedPref.IsOdometerFromOBD(getActivity())) {
                            IsOdometerReading = true;
                            odometerLay.startAnimation(OdometerFaceView);
                        }
                    }

                }

                oldStatusView = SWITCH_VIEW;
                SetJobButtonView(SWITCH_VIEW, isViolation, isPersonal);
                strCurrentDate = Global.getCurrentDate();
                SelectedDate = Global.GetCurrentDeviceDate();

                if (SharedPref.getDriverStatusId(getActivity()).equals(Global.ON_DUTY) && IsPrePost) {
                    if (TrailorNumber.length() > 0) {
                        if (!constants.IS_TRAILER_INSPECT) {
                            if (ptiSelectedtxt.contains(getResources().getString(R.string.pre_trip_unloading))) {
                                showShippingDialog(true);
                            }
                        }
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (SharedPref.getDriverStatusId(getActivity()).equals(Global.ON_DUTY) && IsPrePost) {
                            if (TrailorNumber.length() > 0) {
                                if (constants.IS_TRAILER_INSPECT) {
                                    IsPrePost = false;
                                    Global.InspectTrailerDialog(getActivity(), "Trailer Inspection !!", "You need to inspect your updated trailer.", inspectDialog);
                                } else {
                                    TabAct.host.setCurrentTab(4);
                                }
                            }
                        } else {
                            IsLogShown = false;
                            IsRecapShown = IsRecap;
                            // GET_DRIVER_DETAILS(Global.PROJECT_ID, DRIVER_ID, SelectedDate);
                            CalculateTimeInOffLine(false, false);
                        }
                    }
                }, 500);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    /*===================================================== API Methods ========================================================*/


    /* ------------ Save Co-Driver Data those data was saved in offline mode -------------- */
    void SaveCoDriverData(final boolean isLoad, final boolean IsRecap) {
        JSONArray LogArray = new JSONArray();
        int SecondDriverType = 0;
        int logArrayCount = 0, socketTimeout = 10000;

        if (!isSingleDriver) {
            if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                // Current active driver is Main Driver. So we need co driver details and we are getting co driver's details.

                try {
                    SecondDriverType = 1;   // Co Driver
                    LogArray = constants.GetDriverOffLineSavedLog(getActivity(), SecondDriverType, MainDriverPref, CoDriverPref);
                    logArrayCount = LogArray.length();
                    if(logArrayCount < 3 ){
                        socketTimeout = constants.SocketTimeout10Sec;  //10 seconds
                    }else if(logArrayCount < 10){
                        socketTimeout = constants.SocketTimeout20Sec;  //20 seconds
                    }else{
                        socketTimeout = constants.SocketTimeout40Sec;  //40 seconds
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (LogArray.length() > 0) {
                    IsSaveOperationInProgress = true;
                    saveDriverLogPost.PostDriverLogData(LogArray, APIs.SAVE_DRIVER_STATUS, socketTimeout, isLoad, IsRecap, SecondDriverType, CoDriverLog);
                }

            } else {
                // Current active driver is Co Driver. So we need main driver details and we are getting main driver's details.
                try {
                    SecondDriverType = 0;   // Main Driver
                    LogArray = constants.GetDriverOffLineSavedLog(getActivity(), SecondDriverType, MainDriverPref, CoDriverPref);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (LogArray.length() > 0) {
                    IsSaveOperationInProgress = true;
                    saveDriverLogPost.PostDriverLogData(LogArray, APIs.SAVE_DRIVER_STATUS, socketTimeout, isLoad, IsRecap, SecondDriverType, CoDriverLog);
                }

            }
        }


    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void ClearLogAfterSuccess(boolean isLoad, boolean IsRecap) {
        DriverJsonArray = new JSONArray();
        if (DriverType == Constants.MAIN_DRIVER_TYPE) // Single Driver Type and Position is 0
            MainDriverPref.ClearLocFromList(getActivity());
        else
            CoDriverPref.ClearLocFromList(getActivity());

        SAVE_LOG_RESPONSE_PROCESS(isLoad, IsRecap);
        IsTrailorUpdate = false;
    }




    /*================== Get Driver Trip Details ===================*/
    void GetOnDutyRemarks() {

        if(onDutyRemarkList.size() == 0) {
            params = new HashMap<String, String>();
            GetOnDutyRequest.executeRequest(Request.Method.POST, APIs.GET_ONDUTY_REMARKS, params, GetOndutyRemarks,
                    Constants.SocketTimeout15Sec, ResponseCallBack, ErrorCallBack);
        }
    }


    //*================== Get Re-Certify Records info ===================*//*
    void GetReCertifyRecords(){

        if(SharedPref.IsReCertification(getActivity())) {
            DateTime currentDateTime = new DateTime(Globally.GetCurrentDateTime());
            DateTime fromDateTime = currentDateTime.minusDays(7);
            String fromDateStr = Globally.ConvertDateFormatMMddyyyy(fromDateTime.toString());
            String toDate = Globally.ConvertDateFormatMMddyyyy(currentDateTime.toString());

            params = new HashMap<String, String>();
            params.put(ConstantsKeys.DriverId, DRIVER_ID);
            params.put(ConstantsKeys.FromDate, fromDateStr);
            params.put(ConstantsKeys.ToDate, toDate);

            GetReCertifyRequest.executeRequest(Request.Method.POST, APIs.GET_RECERTIFY_PENDING_RECORDS, params, GetReCertifyRecords,
                    Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);
        }

    }



    //*================== Save Trailer Number ===================*//*
    void SaveTrailerNumber(final String DriverId, final String DeviceId, final String TrailerNumber) {

        isSaveTrailerPending = false;

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId);
         params.put(ConstantsKeys.VIN, TrailerNumber);        // ( please note: here VIN is used as TrailorNumber in parameters. )

        SaveTrailerNumber.executeRequest(Request.Method.POST, APIs.UPDATE_TRAILER_NUMBER, params, SaveTrailer,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    /*================== Get OBD Assigned Vehicles ===================*/
    void GetOBDAssignedVehicles(final String DriverId, final String DeviceId, final String CompanyId, final String VIN) {


        if (SharedPref.GetNewLoginStatus(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
        }

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.VIN, VIN);

        GetOBDVehRequest.executeRequest(Request.Method.POST, APIs.GET_OBD_ASSIGNED_VEHICLES, params, GetObdAssignedVeh,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    void getPuOdometerUsedDetail(){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DRIVER_ID);
        params.put(ConstantsKeys.Date, Global.GetCurrentDeviceDate());
        params.put(ConstantsKeys.VIN, VIN_NUMBER);
        params.put(ConstantsKeys.CompanyId, DriverCompanyId);

        GetOBDVehRequest.executeRequest(Request.Method.POST, APIs.GET_ODOMETER_DETAIL_IN_PU, params, OdometerDetailInPu,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);
    }

    /*================== Update OBD Assigned Vehicles ===================*/
    void UpdateOBDAssignedVehicle(final String DriverId, final String CoDriverId, final String DeviceId, final String PreviousDeviceMappingId,
                                  final String DeviceMappingId, final String VehicleId, final String EquipmentNumber,
                                  final String PlateNumber, final String VIN, final String CompanyId, final String IMEINumber,
                                  final String LoginTruckChange) {

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.CoDriverId, CoDriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.PreviousDeviceMappingId, PreviousDeviceMappingId);
        params.put(ConstantsKeys.DeviceMappingId, DeviceMappingId);
        params.put(ConstantsKeys.VehicleId, VehicleId);
        params.put(ConstantsKeys.EquipmentNumber, EquipmentNumber);
        params.put(ConstantsKeys.PlateNumber, PlateNumber);
        params.put(ConstantsKeys.VIN, VIN);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.IMEINumber, IMEINumber);
        params.put(ConstantsKeys.LoginTruckChange, LoginTruckChange);

        SaveOBDVehRequest.executeRequest(Request.Method.POST, APIs.UPDATE_OBD_ASSIGNED_VEHICLE, params, UpdateObdVeh,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Address From Lat Lng ===================*//*
    void GetAddFromLatLng() {

        if (!IsAddressUpdate) {
            if (JobStatusInt != 101) {
                int OldSelectedStateListPos = -1;
                OpenLocationDialog(JobStatusInt, OldSelectedStateListPos, false);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }

            DisableJobViews();
        }

        Globally.LONGITUDE = Global.CheckLongitudeWithCycle(Globally.LONGITUDE);

        if (!SharedPref.IsAOBRD(getActivity()) && !IsPrePost ){
            params = new HashMap<String, String>();
            params.put(ConstantsKeys.Latitude, Globally.LATITUDE);
            params.put(ConstantsKeys.Longitude, Globally.LONGITUDE);
            params.put(ConstantsKeys.IsAOBRDAutomatic, String.valueOf(IsAOBRDAutomatic));

            GetAddFromLatLngRequest.executeRequest(Request.Method.POST, APIs.GET_Add_FROM_LAT_LNG, params, GetAddFromLatLng,
                    Constants.SocketTimeout3Sec, ResponseCallBack, ErrorCallBack);
        }else{
            progressBar.setVisibility(View.GONE);
            EnableJobViews();
        }
    }



    //*================== Get Odometer Reading ===================*//*
    void GetOdometerReading(final String DriverId, final String DeviceId, final String VIN, final String CreatedDate) {

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.VIN, VIN);
        params.put(ConstantsKeys.CreatedDate, CreatedDate);
        params.put(ConstantsKeys.CompanyId, DriverCompanyId);
        params.put(ConstantsKeys.IsCertifyLog, "false");

        GetOdometerRequest.executeRequest(Request.Method.POST, APIs.GET_ODOMETER, params, GetOdometer,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);
    }

    //*================== Get Driver Log last 18 days ===================*//*
    void GetDriverLog18Days(final String DriverId, int GetDriverLog18Days) {

        if(IsSaveOperationInProgress == false) {
            if (!isSingleDriver) {
                is18DaysLogApiCalled = false;
            }

            GetDriversSavedData(false, DriverType);
            if (Global.isConnected(getActivity())) {
                if (DriverJsonArray.length() > 0 ) {
                    IsPrePost = false;
                    if (!IsSaveOperationInProgress && SaveRequestCount < 2) {
                        isPending18DaysRequest = true;
                        SAVE_DRIVER_STATUS();
                    }
                }else{
                     if (Driver18DaysApiCount < 2 && is18DaysLogApiCalled == false) {
                        is18DaysLogApiCalled = true;
                        dataRequest18Days(DriverId, GetDriverLog18Days);
                    }
                    Driver18DaysApiCount++;
                }
            }

        }
    }

    void dataRequest18Days(String DriverId, int GetDriverLog18Days){
        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.ProjectId, "1");
        params.put(ConstantsKeys.UTCDateTime,  Global.GetCurrentUTCDate());

        GetLog18DaysRequest.executeRequest(Request.Method.POST, APIs.GET_DRIVER_LOG_18_DAYS, params, GetDriverLog18Days,
                Constants.SocketTimeout30Sec, ResponseCallBack, ErrorCallBack);
    }


    //*================== Get Driver Lad last 18 days ===================*//*
    void GetShipment18Days(final String DriverId, final String DeviceId, final String ShippingDocDate, int flag) {

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.ShippingDocDate, ShippingDocDate);

        GetShippingRequest.executeRequest(Request.Method.POST, APIs.GET_SHIPPING_INFO_OFFLINE, params, flag,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }

    //*================== Get Odometer 18 Days data ===================*//*
    void GetOdometer18Days(final String DriverId, final String DeviceId, final String CompanyId, final String CreatedDate) {

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.CreatedDate, CreatedDate);

        GetOdo18DaysRequest.executeRequest(Request.Method.POST, APIs.GET_ODOMETER_OFFLINE, params, GetOdometers18Days,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Driver Status Permissions ===================*//*
    void GetDriverStatusPermission(final String DriverId, final String DeviceId, final String VehicleId ){

        String Country = "";
        String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());
        if (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
            Country = "CANADA";
        } else {
            Country = "USA";
        }
        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.VehicleId, VehicleId );
        params.put(ConstantsKeys.VIN, SharedPref.getVINNumber(getActivity()) );
        params.put(ConstantsKeys.CompanyId, DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity()) );
        params.put(ConstantsKeys.Country, Country );
        params.put(ConstantsKeys.TruckEquipment, TruckNumber);

        GetPermissions.executeRequest(Request.Method.POST, APIs.GET_DRIVER_STATUS_PERMISSION, params, GetDriverPermission,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Driver Status Permissions ===================*//*
    void GetInspection18Days(final String DriverId, final String DeviceId, final String SearchedDate, final int GetInspectionFlag ){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.SearchedDate, SearchedDate );

        Inspection18DaysRequest.executeRequest(Request.Method.POST, APIs.GET_OFFLINE_INSPECTION_LIST, params, GetInspectionFlag,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }



    //*================== Get Inspection Details ===================*//*
    void GetInspectionDefectsList(final String DriverId, final String DeviceId, final String ProjectId, final String VIN){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.ProjectId, ProjectId );
        params.put(ConstantsKeys.VIN, VIN );

        GetInspectionRequest.executeRequest(Request.Method.POST, APIs.GET_INSPECTION_DETAIL , params, GetInspection,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }



    /*================== Get Driver Trip Details ===================*/
    void GetNotificationLog(final String DriverId, final String DeviceId){  /*, final String SearchDate*/

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
         params.put(ConstantsKeys.ProjectId, Global.PROJECT_ID );
        GetNotificationRequest.executeRequest(Request.Method.POST, APIs.GET_NOTIFICATION_LOG , params, GetNotifications,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }

    /*================== Get latest news or alert ===================*/
    void GetNewsNotification(){

        params = new HashMap<String, String>();
        GetNewsNotificationReq.executeRequest(Request.Method.GET, APIs.GET_NOTIFICATIONS, params, GetNewsNotifications,
                Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Driver's 18 Days Recap View Log Data ===================*//*
    void GetRecapView18DaysData(final String DriverId, final String DeviceId , int GetRecapViewFlag){
        BackgroundLocationService.IsRecapApiACalled = true;

        DateTime currentDateTime      = new DateTime(Global.GetCurrentDateTime());
        DateTime startDateTime        = Global.GetStartDate(currentDateTime, 15);
        String   StartDate            = Global.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime));
        String   EndDate              = Global.GetCurrentDeviceDate();  // current Date

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.ProjectId, Global.PROJECT_ID);
        params.put(ConstantsKeys.DrivingStartTime, StartDate);
        params.put(ConstantsKeys.DriverLogDate, EndDate );

        GetRecapView18DaysData.executeRequest(Request.Method.POST, APIs.GET_DRIVER_LOG_18_DAYS_DETAILS , params, GetRecapViewFlag,
                Constants.SocketTimeout50Sec, ResponseCallBack, ErrorCallBack);
    }



    //*================== Save Agriculture Record ===================*//*

    void SaveAgricultureRecord(final String EventDateTime,final String EventDateTimeInUtc,final String Truck,final String DriverId,final String CompanyId,
                               final String SourceAddress,final String SourceLatitude,final String SourceLongitude,final String Odometer,final String EngineHours,final String IsEnabled){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.EventDateTime, EventDateTime);
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
                Constants.SocketTimeout30Sec, ResponseCallBack, ErrorCallBack);

    }


    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, int inputDataLength) {

            switch (flag) {

                case MainDriverLog:
                  //  constants.ClearNotifications(getActivity() );
                    Log.d("Response ", "---Response Save: " + response);
                    IsSaveOperationInProgress = false;
                    progressD.dismiss();
                    EnableJobViews();
                    JSONObject obj;

                    if(isSaveTrailerPending){
                        SaveTrailerNumber(DRIVER_ID, DeviceId, TrailorNumber);
                    }

                    try {
                       // String responseee = "{\"Status\":true,\"Message\":\"Record Successfully Saved\",\"Data\":null}";
                        obj = new JSONObject(response);
                        String Message = obj.getString("Message");

                        if (!isFirst && obj.getString("Status").equals("true")) {
                            BackgroundLocationService.IsAutoChange = false;

                            if(IsRefreshedClick){
                                clearOfflineData();
                                IsRefreshedClick = false;
                                refreshPageBtn.performClick();
                            }else {

                                GetDriversSavedData(false, DriverType);

                                if (DriverJsonArray.length() == 1) {
                                    ClearLogAfterSuccess(isLoad, IsRecap);
                                } else {
                                     /*Check Reason: some times data was uploading in background and user entered new status in between.
                                     In api response we are clearing the entries and in between entry was skipped before upload to server.
                                     So to avoid this we are checking input length and current log length.*/
                                    if(DriverJsonArray.length() == inputDataLength) {
                                        try {
                                            int LastStatus = 0, SecLastStatus = 0;
                                            JSONObject lastObj = constants.GetJsonFromList(DriverJsonArray, DriverJsonArray.length() - 1);
                                            JSONObject secLastObj = constants.GetJsonFromList(DriverJsonArray, DriverJsonArray.length() - 2);
                                            LastStatus = lastObj.getInt("DriverStatusId");
                                            SecLastStatus = secLastObj.getInt("DriverStatusId");

                                            if (LastStatus == SecLastStatus) {
                                                ClearLogAfterSuccess(isLoad, IsRecap);

                                            } else {
                                                if (SaveRequestCount < 2) {
                                                    SAVE_DRIVER_STATUS();
                                                } else {
                                                    ClearLogAfterSuccess(isLoad, IsRecap);
                                                }
                                            }

                                            SaveCoDriverData(isLoad, IsRecap);
                                        } catch (Exception e) {
                                            ClearLogAfterSuccess(isLoad, IsRecap);
                                        }
                                    }else{
                                         SAVE_DRIVER_STATUS();
                                    }
                                }
                            }

                            if(isPending18DaysRequest){
                                isPending18DaysRequest = false;
                                is18DaysLogApiCalled = true;
                                dataRequest18Days(DRIVER_ID, GetDriverLog18Days);
                            }
                        } else {
                             if(IsRefreshedClick){
                                 clearOfflineData();
                                IsRefreshedClick = false;
                                refreshPageBtn.performClick();
                            }else {
                                 if (Message.equalsIgnoreCase("Duplicate Records")) {
                                     clearOfflineData();
                                     SAVE_LOG_RESPONSE_PROCESS(isLoad, IsRecap);

                                     IsTrailorUpdate = false;


                                     SaveCoDriverData(isLoad, IsRecap);
                                 } else {
                                     if (isLoad && Message.length() > 0) {
                                         Global.EldScreenToast(OnDutyBtn, Message, getResources().getColor(R.color.colorVoilation));
                                     }

                                     try {
                                         if (Message.equals("Device Logout")) {
                                             if (DriverJsonArray.length() > 0) {
                                                 SyncUserData();
                                                 ClearLogAfterSuccess(isLoad, IsRecap);
                                             } else {
                                                 LogoutUser();
                                             }
                                         }
                                     } catch (Exception e) {
                                         e.printStackTrace();
                                     }


                                 }
                             }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ClearLog();
                    }
                    isFirst = false;


                    break;

                case CoDriverLog:
                    IsSaveOperationInProgress = false;

             //      constants.ClearNotifications(getActivity() );
                    try {
                        EnableJobViews();
                        obj = new JSONObject(response);
                        String Message = obj.getString("Message");

                        if (obj.getString("Status").equals("true")) {
                            BackgroundLocationService.IsAutoChange = false;

                            if (DriverType == Constants.MAIN_DRIVER_TYPE)
                                MainDriverPref.ClearLocFromList(getActivity()); // Clear Main Driver log
                            else
                                CoDriverPref.ClearLocFromList(getActivity());   // Clear Co Driver log

                        } else {
                            if (Message.equalsIgnoreCase("Duplicate Records")) {
                                if (DriverType == Constants.MAIN_DRIVER_TYPE)
                                    MainDriverPref.ClearLocFromList(getActivity()); // Clear Main Driver log
                                else
                                    CoDriverPref.ClearLocFromList(getActivity());   // Clear Co Driver log
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    break;

            }

        }

        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("errorrr ", ">>>error dialog: ");

            if(IsRefreshedClick){

                IsRefreshedClick = false;
                boolean isConnected = Global.isConnected(getActivity());
                if(isConnected){
                    refreshDataEvent(isConnected);
                 }else{
                    loadingSpinEldIV.stopAnimation();
                    Global.EldScreenToast(OnDutyBtn, Global.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                }

            }

            IsSaveOperationInProgress = false;
            progressD.dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SAVE_LOG_RESPONSE_PROCESS(isLoad, IsRecap);
            }

            if(flag == MainDriverLog && isSaveTrailerPending) {
                SaveTrailerNumber(DRIVER_ID, DeviceId, TrailorNumber);
            }
        }
    };


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null, dataObj = null;
            String status = "", Message = "";

            if(flag == GetDriverLog18Days)
                is18DaysLogApiCalled = false;

            try {
                obj = new JSONObject(response);
                status = obj.getString(ConstantsKeys.Status);
                Message = obj.getString(ConstantsKeys.Message);

                if (!obj.isNull(ConstantsKeys.Data)) {
                    dataObj = new JSONObject(obj.getString(ConstantsKeys.Data));
                }

           /*     if(Message.contains("Object reference not set to an instance")){
                    constants.saveObdData("Object reference not set", "API Flag: " + flag, "", "",
                            "", "", "", "", "0",
                            "-1", "", Global.GetCurrentDeviceDate(), Global.GetCurrentDeviceDate(),
                            DRIVER_ID, dbHelper, driverPermissionMethod, obdUtil);
                }*/

            } catch (JSONException e) { }

            if (status.equalsIgnoreCase("true")) {

                switch (flag) {

                    case GetShipment18Days:
                        try {
                            JSONArray resultArray = new JSONArray(obj.getString("Data"));
                            shipmentMethod.Shipment18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, resultArray);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case GetShipment18DaysCo:
                        try {
                            JSONArray resultArray = new JSONArray(obj.getString("Data"));
                            shipmentMethod.Shipment18DaysHelper(Integer.valueOf(CoDriverId), dbHelper, resultArray);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case GetDriverLog18Days:
                        /* ---------------- DB Helper operations (Insert/Update) --------------- */
                        try {
                            UnidentifiedActivity.isUnIdentifiedRecordClaimed = false;
                            constants.IS_ELD_ON_CREATE = false;
                            String savedDate = Global.GetCurrentDateTime();
                            if (savedDate != null)
                                SharedPref.setSavedDateTime(savedDate, getActivity());

                            if (!obj.isNull("Data")) {
                                JSONArray resultArray = new JSONArray(obj.getString("Data"));

                                if (resultArray.length() > 0) {

                                    if (isSingleDriver) {
                                        hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, resultArray);
                                    } else {
                                        hMethods.DriverLogHelper(Integer.valueOf(MainDriverId), dbHelper, resultArray);
                                    }

                                    JSONObject lastObj = hMethods.GetLastJsonFromArray(resultArray);

                                    if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                                        if (!lastObj.isNull(ConstantsKeys.IsAdverseException)) {
                                            SharedPref.setAdverseExcptn(lastObj.getBoolean(ConstantsKeys.IsAdverseException), getActivity());
                                        }

                                        if (!lastObj.isNull(ConstantsKeys.IsShortHaulException)) {
                                            SharedPref.set16hrHaulExcptn(lastObj.getBoolean(ConstantsKeys.IsShortHaulException), getActivity());
                                        }

                                    }else{
                                        if (!lastObj.isNull(ConstantsKeys.IsAdverseException)) {
                                            SharedPref.setAdverseExcptnCo(lastObj.getBoolean(ConstantsKeys.IsAdverseException), getActivity());
                                        }

                                        if (!lastObj.isNull(ConstantsKeys.IsShortHaulException)) {
                                            SharedPref.set16hrHaulExcptnCo(lastObj.getBoolean(ConstantsKeys.IsShortHaulException), getActivity());
                                        }

                                    }

                                    if (DRIVER_ID.equals(MainDriverId)) {
                                        CalculateTimeInOffLine(false, false);
                                    }


                                } else {

                                    DRIVER_JOB_STATUS = 1;
                                    isPersonal = "false";
                                    clearCalculationsView();

                                    initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                                            usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);
                                    SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadingSpinEldIV.stopAnimation();

                        if(IsRefreshedClick) {
                            if(getActivity() != null && !getActivity().isFinishing()) {
                                Global.EldScreenToast(OnDutyBtn, "Data refreshed successfully", getResources().getColor(R.color.colorPrimary));
                                IsRefreshedClick = false;

                             //   GetShipment18Days(DRIVER_ID, DeviceId, SelectedDate, GetShipment18Days);
                            //    GetOdometer18Days(DRIVER_ID, DeviceId, DriverCompanyId, SelectedDate);

                                IsAOBRD = SharedPref.IsAOBRD(getActivity());
                                setTitleView(SharedPref.IsOdometerFromOBD(getActivity()), isExemptDriver);
                            }
                        }



                        break;


                    case GetCoDriverLog18Days:
                        /* ---------------- DB Helper operations (Insert/Update) --------------- */
                        try {
                            JSONArray resultArray = new JSONArray(obj.getString("Data"));
                            if (resultArray.length() > 0) {
                                hMethods.DriverLogHelper(Integer.valueOf(CoDriverId), dbHelper, resultArray);

                                if(!DRIVER_ID.equals(MainDriverId)) {
                                    CalculateTimeInOffLine(false, false);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;


                    case GetOndutyRemarks:

                        try {
                            JSONArray remarkArray = new JSONArray(obj.getString("Data"));
                            Global.onDutyRemarks = new ArrayList<String>();
                            onDutyRemarkList    = new ArrayList<>();

                            for (int i = 0; i < remarkArray.length(); i++) {
                                JSONObject resultJson = (JSONObject) remarkArray.get(i);
                                String remarks = resultJson.getString("OnDutyRemarks");
                                Global.onDutyRemarks.add(remarks);
                                onDutyRemarkList.add(remarks);
                            }

                           /* if(onDutyRemarkList.size() > 1){
                                if(onDutyRemarkList.get(0).equals("Border Crossing") && onDutyRemarkList.get(1).equals("Brake Check")){
                                    onDutyRemarkList.set(0, "Brake Check");
                                    onDutyRemarkList.set(1, "Border Crossing");
                                    Global.onDutyRemarks.set(0, "Brake Check");
                                    Global.onDutyRemarks.set(1, "Border Crossing");
                                }
                            }*/
                            Global.onDutyRemarks.add(0, "Select");
                            onDutyRemarkList.add(0, "Select");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    case GetReCertifyRecords:
                        try {
                            if (!obj.isNull("Data")) {
                                SharedPref.setReCertifyData(obj.getString("Data"), getActivity());

                                // update recap array for reCertify the log if edited
                                constants.UpdateCertifyLogArray(recapViewMethod, DRIVER_ID, 7,
                                        dbHelper,  getActivity());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;


                    case OdometerDetailInPu:
                        try{
                            if(dataObj != null) {

                                SharedPref.setTotalPUOdometerForDay(dataObj.getString(ConstantsKeys.TotalKM),
                                        Global.GetCurrentDeviceDate(), getActivity());

                                SharedPref.setDayStartOdometer(dataObj.getString(ConstantsKeys.DayOdometerInKm),
                                        dataObj.getString(ConstantsKeys.DayOdometerInMiles),
                                            Global.GetCurrentDeviceDate(), getActivity());


                            /*    constants.saveObdData("OdometerDetail-EldHome", "PersonalUseTotalKm: " + dataObj.getString(ConstantsKeys.TotalKM) +
                                                ", DayOdometerInKm: " + dataObj.getString(ConstantsKeys.DayOdometerInKm) + ", DayOdometerInMiles: " +
                                                dataObj.getString(ConstantsKeys.DayOdometerInMiles), "", "",
                                        "", "", "", "", "0",
                                        "-1", "", Global.GetCurrentDeviceDate(), Global.GetCurrentDeviceDate(),
                                        DRIVER_ID, dbHelper, driverPermissionMethod, obdUtil);
*/
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;

                    case GetObdAssignedVeh:
                        try {
                            if(progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            TabAct.vehicleList = new ArrayList<VehicleModel>();
                            JSONArray vehicleJsonArray = new JSONArray(obj.getString(ConstantsKeys.Data));

                            try {
                                for (int i = 0; i < vehicleJsonArray.length(); i++) {
                                    JSONObject resultJson = (JSONObject) vehicleJsonArray.get(i);
                                    VehicleModel vehicleModel = new VehicleModel(
                                            resultJson.getString("VehicleId"),
                                            resultJson.getString("EquipmentNumber"),
                                            resultJson.getString("PlateNumber"),
                                            resultJson.getString("VIN"),
                                            resultJson.getString("PreviousDeviceMappingId"),
                                            resultJson.getString("DeviceMappingId"),
                                            resultJson.getString("CompanyId")
                                    );
                                    TabAct.vehicleList.add(vehicleModel);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            if (SharedPref.GetNewLoginStatus(getActivity()) && getActivity() != null && !getActivity().isFinishing() ) {
                                try {
                                    if (vehicleDialogLogin != null && vehicleDialogLogin.isShowing())
                                        vehicleDialogLogin.dismiss();

                                    isOldVehicleDialog = false;
                                    vehicleDialogLogin = new VehicleDialogLogin(getActivity(), getActivity(), TruckNumber, false,
                                            TabAct.vehicleList, new VehicleLoginListener());
                                    vehicleDialogLogin.show();
                                } catch (final IllegalArgumentException e) {
                                    e.printStackTrace();

                                    openOldVehicleDialogLogin();

                                } catch (final Exception e) {
                                    e.printStackTrace();
                                    openOldVehicleDialogLogin();

                                }
                            } else {
                                try {
                                    if (IsVehicleDialogShown && getActivity() != null && !getActivity().isFinishing() ) {
                                        if (TabAct.vehicleList.size() > 0) {
                                                if (vehicleDialog != null && vehicleDialog.isShowing()) {
                                                    vehicleDialog.dismiss();
                                                }

                                                isOldVehicleDialog = false;
                                                vehicleDialog = new VehicleDialog(getActivity(), TruckNumber, false, TabAct.vehicleList, new VehicleListener());
                                                vehicleDialog.show();

                                        } else {
                                            Global.EldScreenToast(OnDutyBtn, "No vehicles are available.", getResources().getColor(R.color.colorVoilation));
                                        }
                                    }
                                } catch (final IllegalArgumentException e) {
                                     e.printStackTrace();

                                    openOldVehicleDialog(IsVehicleDialogShown);

                                } catch (final Exception e) {
                                    e.printStackTrace();
                                    openOldVehicleDialog(IsVehicleDialogShown);

                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            openOldVehicleDialog(IsVehicleDialogShown);

                        }

                        break;

                    case GetRecapViewFlagMain:
                    case GetRecapViewFlagCo:

                        BackgroundLocationService.IsRecapApiACalled = false;

                        try {
                            if (!obj.isNull("Data")) {
                                JSONArray recapArray = recapViewMethod.ParseServerResponseOfArray(obj.getJSONArray("Data"));

                                if (flag == GetRecapViewFlagMain) {
                                    recapViewMethod.RecapView18DaysHelper(Integer.valueOf(MainDriverId), dbHelper, recapArray);
                                } else {
                                    recapViewMethod.RecapView18DaysHelper(Integer.valueOf(CoDriverId), dbHelper, recapArray);
                                }

                                // update recap array for reCertify the log if edited
                                constants.UpdateCertifyLogArray(recapViewMethod, DRIVER_ID, 7,
                                        dbHelper,  getActivity());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //  Log.d("response", "Service response GetRecapViewData-" + flag + ": " + response);
                        break;



                    case UpdateObdVeh:

                        IsVehicleDialogShown = false;

                        try {
                            JSONObject vehicleJsonObj = new JSONObject(obj.getString("Data"));
                            IsAOBRD             = vehicleJsonObj.getBoolean("AOBRD");
                            IsAOBRDAutomatic    = vehicleJsonObj.getBoolean("IsAOBRDAutomatic");
                            VIN_NUMBER          = vehicleJsonObj.getString("VIN");

                            boolean isOdometerFromOBD = false;
                            String plateNo = "";

                            if(vehicleJsonObj.has(ConstantsKeys.OdometerFromOBD)){
                                isOdometerFromOBD = vehicleJsonObj.getBoolean(ConstantsKeys.OdometerFromOBD);
                            }

                            if(vehicleJsonObj.has(ConstantsKeys.PlateNumber)){
                                plateNo = vehicleJsonObj.getString(ConstantsKeys.PlateNumber);
                            }

                            SharedPref.SetIsAOBRD(IsAOBRD, getActivity());
                            SharedPref.SetAOBRDAutomatic(IsAOBRDAutomatic, getActivity());
                            SharedPref.SetAOBRDAutoDrive(vehicleJsonObj.getBoolean(ConstantsKeys.IsAutoDriving), getActivity());
                            SharedPref.SetOdometerFromOBD(isOdometerFromOBD, getActivity());
                            SharedPref.setCurrentTruckPlateNo(plateNo, getActivity());

                            setTitleView(isOdometerFromOBD, isExemptDriver);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        // if(IsObdVehShown)
                        if (TabAct.vehicleList.size() > 0) {
                            VIN_NUMBER = TabAct.vehicleList.get(TruckListPosition).getVIN();
                            TruckNumber = TabAct.vehicleList.get(TruckListPosition).getEquipmentNumber();
                            VehicleId = TabAct.vehicleList.get(TruckListPosition).getVehicleId();
                            tractorTv.setText(TruckNumber);
                            SharedPref.setVehicleId(VehicleId, getActivity());
                        }
                        SharedPref.setVINNumber(VIN_NUMBER,  getActivity());
                        SharedPref.setLastSavedVINNumber(VIN_NUMBER, getActivity());

                        if (SharedPref.GetNewLoginStatus(getActivity())) {

                            Constants.isEldHome = true;
                            SharedPref.SetNewLoginStatus(false, getActivity());

                            Constants.isCallMalDiaEvent = true;
                            SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());
                            startService();


                            String updatePopupDate = SharedPref.GetUpdateAppDialogTime(getActivity());
                            if(!updatePopupDate.equals(SelectedDate)){
                                TabAct.openUpdateDialogBtn.performClick();
                            }

                            try {
                                if (progressDialog != null)
                                    progressDialog.dismiss();

                                if(isOldVehicleDialog){
                                    if (vehicleDialogLoginOld != null && vehicleDialogLoginOld.isShowing())
                                        vehicleDialogLoginOld.dismiss();
                                }else {
                                    if (vehicleDialogLogin != null && vehicleDialogLogin.isShowing())
                                        vehicleDialogLogin.dismiss();
                                }
                            } catch (final IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }

                            if(getActivity() != null && !getActivity().isFinishing()) {
                                Toast.makeText(getActivity(), "Updated successfully.", Toast.LENGTH_LONG).show();

                                getExceptionStatus();
                                if (isUnidentifiedAllowed() && isUnIdentifiedOccur && isUnIdentifiedAlert) {
                                    try {
                                        if (confirmationDialog != null && confirmationDialog.isShowing())
                                            confirmationDialog.dismiss();
                                        confirmationDialog = new ConfirmationDialog(getActivity(), Constants.AlertUnidentified, new ConfirmListener());
                                        confirmationDialog.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    setExceptionView();
                                    CalculateTimeInOffLine(false, false);
                                }
                            }

                            GetNewsNotification();
                            GetReCertifyRecords();

                            if(isExemptDriver){
                                Globally.DriverSwitchAlert(getActivity(), getString(R.string.exempt_reminder_desc), "", getString(R.string.ok));
                            }

                            isCertifySignPending(false, false);

                        } else {

                            Global.EldScreenToast(OnDutyBtn, "Updated successfully.", getResources().getColor(R.color.colorPrimary));
                        }

                        SharedPref.setTruckNumber(TruckNumber, getActivity());
                        SharedPref.setVINNumber(VIN_NUMBER, getActivity());

                        IsTruckChange = true;
                        GetDriverStatusPermission(DRIVER_ID, DeviceId, VehicleId);

                        getDayStartOdometer();

                        break;


                    case SendLog:
                        Global.EldScreenToast(OnDutyBtn, "Sent Successfully.", getResources().getColor(R.color.colorPrimary));
                        break;

                    case GetOdometer:
                        if (dataObj != null) {
                            try {
                                if (!dataObj.getString("StartOdometer").equals("null") &&
                                        dataObj.getString("EndOdometer").equals("null")) {
                                    IsStartReading = true;
                                } else {
                                    IsStartReading = false;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case SaveTrailer:

                        SaveTrailerEvent(Message);
                        break;

                    case GetOdometers18Days:
                        try {
                            JSONArray resultArray = new JSONArray(obj.getString("Data"));
                            odometerhMethod.Odometer18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, resultArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case GetDriverPermission:
                        try {

                            if(IsRefreshedClick){   // manual refreshed by driver

                                Driver18DaysApiCount = 0;

                                if (TeamDriverType.equals(DriverConst.TeamDriver)) {
                                    GetDriverLog18Days(MainDriverId, GetDriverLog18Days);
                                    GetDriverLog18Days(CoDriverId, GetCoDriverLog18Days);

                                   // GetInspection18Days( DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()), DeviceId,  SelectedDate,GetInspection18Days );
                                  //  GetInspection18Days( DriverConst.GetDriverDetails(DriverConst.CoDriverID, getActivity()), DeviceId,  SelectedDate,GetInspection18DaysCo );

                                    GetRecapView18DaysData(MainDriverId, DeviceId, GetRecapViewFlagMain);
                                    GetRecapView18DaysData(CoDriverId, DeviceId, GetRecapViewFlagCo);

                                } else {
                                    GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
                                  //  GetInspection18Days( DRIVER_ID, DeviceId,  SelectedDate, GetInspection18Days );
                                    GetRecapView18DaysData(DRIVER_ID, DeviceId, GetRecapViewFlagMain);
                                }


                                GetReCertifyRecords();

                                // clear CT PAT info to update it again in service class. because if array size is 0 API will be called
                               // ctPatInspectionMethod.DriverCtPatInsp18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, new JSONArray());
                               // recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, new JSONArray());

                                Constants.isCallMalDiaEvent = true;
                                SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());
                                startService();

                            }

                            if (!obj.isNull("Data")) {
                                JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                                driverPermissionMethod.DriverPermissionHelper(Integer.valueOf(DRIVER_ID), dbHelper, dataJObject);

                                boolean IsCertifyMandatory = false;
                                if(dataJObject.has(ConstantsKeys.IsCertifyMandatory)) {
                                    IsCertifyMandatory = dataJObject.getBoolean(ConstantsKeys.IsCertifyMandatory);
                                }
                                Context context = getActivity();

                                if(context != null) {
                                    SharedPref.SetCertifyMandatoryStatus(IsCertifyMandatory, getActivity());

                                    if(constants.IsSendLog(DRIVER_ID, driverPermissionMethod, dbHelper)) {
                                        sendReportBtn.setTextColor(context.getResources().getColor(R.color.color_eld_theme));
                                    } else {
                                        sendReportBtn.setTextColor(context.getResources().getColor(R.color.gray_hover));
                                    }
                                }

                                boolean IsCCMTACertified = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsCCMTACertified);
                                SharedPref.SetCCMTACertifiedStatus(IsCCMTACertified, getActivity());

                                boolean IsAppRestricted     = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsAppRestricted);
                                SharedPref.SetAppRestrictedStatus(IsAppRestricted, getActivity());

                                boolean IsAllowLogReCertification   = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsAllowLogReCertification);
                                boolean IsShowUnidentifiedRecords   = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsShowUnidentifiedRecords);
                                boolean IsPersonal                  = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsPersonal);
                                boolean IsYardMove                  = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsYardMove);

                                boolean IsAllowMalfunction  = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsAllowMalfunction);
                                boolean IsAllowDiagnostic   = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsAllowDiagnostic);
                                boolean IsClearMalfunction  = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsClearMalfunction);
                                boolean IsClearDiagnostic   = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsClearDiagnostic);
                                boolean IsNorthCanada       = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsNorthCanada);
                                isExemptDriver              = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsExemptDriver);
                                boolean IsCycleRequest      = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsCycleRequest);
                                boolean IsUnidentified      = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsUnidentified);
                                boolean UnidentifiedFromOBD = constants.CheckNullBoolean(dataJObject, ConstantsKeys.UnidentifiedFromOBD);
                                boolean IsAgriException = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsAgriException);

                                SharedPref.SetUnidentifiedFromOBDStatus(UnidentifiedFromOBD, getActivity());
                                SharedPref.setAgricultureExemption(IsAgriException, getActivity());

                                boolean IsDeferral          = false;
                                int DeferralDay             = dataJObject.getInt(ConstantsKeys.DeferralDay);
                                String DeferralDate         = dataJObject.getString(ConstantsKeys.DeferralDate);

                                if(DeferralDay > 0){
                                    IsDeferral = true;

                                   /* if(DeferralDay == 2){
                                        DeferralDate = Global.getDateTimeObj(DeferralDate, false).minusDays(1).toString();
                                    }*/

                                }

                                if(dataJObject.has(ConstantsKeys.IsOdoCalculationAllowed) && !dataJObject.isNull(ConstantsKeys.IsOdoCalculationAllowed)){
                                    SharedPref.SetOdoCalculationAllowed(dataJObject.getBoolean(ConstantsKeys.IsOdoCalculationAllowed), getActivity());
                                }else{
                                    SharedPref.SetOdoCalculationAllowed(false, getActivity());
                                }

                                if(dataJObject.has(ConstantsKeys.PowerComplianceMal)){
                                    SharedPref.saveParticularMalDiaStatus( dataJObject.getBoolean(ConstantsKeys.PowerComplianceMal) ,
                                            dataJObject.getBoolean(ConstantsKeys.EnginSyncMal),
                                            dataJObject.getBoolean(ConstantsKeys.PostioningComplMal) ,
                                            dataJObject.getBoolean(ConstantsKeys.PowerDataDiag) ,
                                            dataJObject.getBoolean(ConstantsKeys.EnginSyncDiag) ,
                                            getActivity());
                                }


                                int ObdPreference = Constants.OBD_PREF_WIFI;
                                if(dataJObject.has(ConstantsKeys.ObdPreference) && !dataJObject.isNull(ConstantsKeys.ObdPreference) ) {
                                    ObdPreference = dataJObject.getInt(ConstantsKeys.ObdPreference);
                                    int savedObdPref = SharedPref.getObdPreference(getActivity());

                                    if(ObdPreference != savedObdPref){
                                        if(ObdPreference == Constants.OBD_PREF_BLE){
                                            SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, Global.getCurrentDate(), Global.GetCurrentUTCTimeFormat(), getActivity());
                                        }else if(ObdPreference == Constants.OBD_PREF_WIRED){
                                            SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, Global.getCurrentDate(), Global.GetCurrentUTCTimeFormat(), getActivity());
                                        }else{
                                            SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED, Global.getCurrentDate(), Global.GetCurrentUTCTimeFormat(), getActivity());
                                        }

                                        constants.saveAppUsageLog("ObdPreference: From " + savedObdPref + " to " + ObdPreference, false, false, obdUtil);
                                        SharedPref.SetLocReceivedFromObdStatus(false, getActivity());
                                    }

                                    SharedPref.SetObdPreference(ObdPreference, getActivity());

                                    if(ObdPreference != savedObdPref){
                                        startService();     // call onStartCommand in service to connect with current OBD pref
                                    }

                                }else{
                                    SharedPref.SetObdPreference(SharedPref.getObdPreference(getActivity()), getActivity());
                                }

                                setObdStatus(false);

                                if(IsTruckChange){
                                    if(ObdPreference == Constants.OBD_PREF_BLE && SharedPref.getObdStatus(getActivity()) == Constants.BLE_CONNECTED){
                                        // set obd disconnection time is current time for diagnostic/malfunction event.
                                        SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, Global.getCurrentDate(),
                                                Global.GetCurrentUTCTimeFormat(), getActivity());
                                    }
                                    startService();
                                    setObdStatus(false);
                                }

                                boolean IsELDNotification = false;
                                String ELDNotification    = dataObj.getString("DriverELDNotificationList");

                                try{
                                    JSONArray eldNotArray = new JSONArray(ELDNotification);
                                    if(eldNotArray.length() > 0){
                                        IsELDNotification = true;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                SharedPref.SetELDNotification(IsELDNotification, getActivity());
                                SharedPref.SetNorthCanadaStatus(IsNorthCanada, getActivity());

                                SharedPref.SetDiagnosticAndMalfunctionSettingsMain(IsAllowMalfunction, IsAllowDiagnostic, IsClearMalfunction, IsClearDiagnostic, context);
                                SharedPref.SetDiagnosticAndMalfunctionSettingsCo(IsAllowMalfunction, IsAllowDiagnostic, IsClearMalfunction, IsClearDiagnostic, context);
                                SharedPref.SetCertifcnUnIdenfdSettings(IsAllowLogReCertification, IsShowUnidentifiedRecords, IsPersonal, IsYardMove, context);
                                SharedPref.SetCertifcnUnIdenfdSettingsCo(IsAllowLogReCertification, IsShowUnidentifiedRecords, IsPersonal, IsYardMove, context);
                                IsPersonalUseAllowed  = IsPersonal;

                                if(DRIVER_ID.equals(MainDriverId)) {    // Update permissions for main driver

                                    SharedPref.SetExemptDriverStatusMain(isExemptDriver, getActivity());
                                    SharedPref.SetCycleRequestStatusMain(IsCycleRequest, getActivity());

                                    if(DeferralDate.equals("null")){
                                        DeferralDate = "";
                                    }

                                    SharedPref.setDeferralForMain(IsDeferral, DeferralDate, ""+DeferralDay, getActivity());

                                    boolean isMal = SharedPref.isMalfunctionOccur(getActivity());
                                    boolean isDia = SharedPref.isDiagnosticOccur(getActivity());
                                    SharedPref.setEldOccurences(IsUnidentified,
                                            isMal, isDia,
                                            SharedPref.isSuggestedEditOccur(getActivity()), getActivity());


                                }else{                                  // Update permissions for Co driver

                                    SharedPref.SetExemptDriverStatusCo(isExemptDriver, getActivity());
                                    SharedPref.SetCycleRequestStatusCo(IsCycleRequest, getActivity());
                                    SharedPref.setDeferralForCo(IsDeferral, DeferralDate, ""+DeferralDay, getActivity());

                                    SharedPref.setEldOccurencesCo(IsUnidentified,
                                            SharedPref.isMalfunctionOccurCo(getActivity()) ,
                                            SharedPref.isDiagnosticOccurCo(getActivity()),
                                            SharedPref.isSuggestedEditOccurCo(getActivity()), getActivity());

                                }


                                setExceptionView();

                                if(IsELDNotification){

                                    try {
                                        if (getActivity() != null && !getActivity().isFinishing()) {
                                            if (eldNotificationDialog != null && eldNotificationDialog.isShowing()) {
                                                eldNotificationDialog.dismiss();
                                            }
                                            eldNotificationDialog = new EldNotificationDialog(getActivity(), ELDNotification, false);
                                            eldNotificationDialog.show();
                                        }
                                    } catch (final IllegalArgumentException e) {
                                        e.printStackTrace();
                                    } catch (final Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                if(IsCycleRequest){
                                    certifyTitle = "<font color='#1A3561'><b>Alert !!</b></font>";
                                    titleDesc = "<font color='#2E2E2E'><html>" + getResources().getString(R.string.cycle_change_req) + " </html> </font>";
                                    okText = "<font color='#1A3561'><b>" + getResources().getString(R.string.ok) + "</b></font>";
                                    Global.SwitchAlertWIthTabPosition(getActivity(), certifyTitle, titleDesc, okText, 3);
                                }

                                setTitleView(SharedPref.IsOdometerFromOBD(getActivity()), isExemptDriver);

                                confirmDeferralRuleDays();


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    case GetAddFromLatLng:

                        EnableJobViews();
                        progressBar.setVisibility(View.GONE);
                        if (!obj.isNull("Data")) {
                            try {
                                JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                                City        = dataJObject.getString(ConstantsKeys.City);
                                State       = dataJObject.getString(ConstantsKeys.State);
                                Country     = dataJObject.getString(ConstantsKeys.Country);
                                AddressLine = dataJObject.getString(ConstantsKeys.Location);    // + ", " + Country;
                                AobrdState  = State;

                                if (!IsAddressUpdate) {
                                    if (JobStatusInt != 101) {
                                        if (driverLocationDialog != null && DriverLocationDialog.updateViewTV != null) {
                                                DriverLocationDialog.updateViewTV.performClick();
                                        }
                                    } else {
                                        SaveJobStatusAlert(LocationJobTYpe);
                                    }
                                }else{
                                    driverLogArray = hMethods.updateLocationInLastItem(driverLogArray, AddressLine);
                                    // save updated log array into the database
                                        hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, driverLogArray); // saving in db after updating the location in array

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            IsAddressUpdate = false;

                        }


                        break;

                    case GetInspection18Days:
                        if (!obj.isNull("Data")) {
                            try {
                                JSONArray inspectionData = new JSONArray(obj.getString("Data"));

                                if (TeamDriverType.equals(DriverConst.TeamDriver)) {
                                    int DriverId = Integer.valueOf(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()));
                                    inspectionMethod.DriverInspectionHelper( DriverId, dbHelper, inspectionData);
                                }else{
                                    inspectionMethod.DriverInspectionHelper( Integer.valueOf(DRIVER_ID), dbHelper, inspectionData);
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case GetInspection18DaysCo:
                        if (!obj.isNull("Data")) {
                            try {
                                int CoDriverId = Integer.valueOf(DriverConst.GetDriverDetails(DriverConst.CoDriverID, getActivity()));
                                JSONArray inspectionData = new JSONArray(obj.getString("Data"));
                                inspectionMethod.DriverInspectionHelper( CoDriverId, dbHelper, inspectionData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case GetInspection:
                        try{

                            JSONArray TruckArray        = new JSONArray(dataObj.getString("TruckIssueList"));
                            JSONArray TrailerArray      = new JSONArray(dataObj.getString("TrailorIssueList"));

                            // Save Inspections issues list in local
                            SharedPref.setInspectionIssues(TruckArray.toString(), TrailerArray.toString(), getActivity());

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;

                    case GetNotifications:

                        try{

                            JSONArray notification18DaysArray = new JSONArray();
                            JSONArray jsonArray = new JSONArray(obj.getString(ConstantsKeys.Data));

                            for(int i = 0 ; i < jsonArray.length();i++){
                                JSONObject dataJson = (JSONObject)jsonArray.get(i);

                                if(dataJson.getString(ConstantsKeys.Title).length() > 0) {
                                    notification18DaysArray.put(dataJson);
                                }
                            }

                            // Save Notifications in 18 days Array
                            notificationMethod.NotificationHelper(Integer.valueOf(DRIVER_ID), dbHelper, notification18DaysArray);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                    case GetNewsNotifications:

                        try {
                            List<NotificationNewsModel> newsNotificationList = new ArrayList<NotificationNewsModel>();
                            JSONArray newsArray = new JSONArray(obj.getString("Data"));
                            for(int newsCount = 0; newsCount < newsArray.length() ; newsCount++){
                                JSONObject newsObj = (JSONObject)newsArray.get(newsCount);

                                NotificationNewsModel newsModel = new NotificationNewsModel(
                                        newsObj.getString(ConstantsKeys.NewsTitle),
                                        newsObj.getString(ConstantsKeys.NewsDescription));

                                newsNotificationList.add(newsModel);

                            }

                            if(newsNotificationList.size() > 0 && getActivity() != null && !getActivity().isFinishing() ) {
                                NotificationNewsDialog newsDialog = new NotificationNewsDialog(getActivity(), newsNotificationList, false);
                                newsDialog.show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;


                    case NotReady:
                        Log.d("NotReady","NotReady: " + response);
                        break;


                    case SaveAgricultureException:

                        SharedPref.setAgricultureExemption(false, getActivity());
                        SharedPref.SaveAgricultureRecord("","","",getContext());
                        Global.EldScreenToast(OnDutyBtn, "Agriculture Exemption Disabled", getResources().getColor(R.color.colorPrimary));

                        setExceptionView();

                        break;
                }
            } else {

                progressBar.setVisibility(View.GONE);
                EnableJobViews();
                IsRefreshedClick = false;
                loadingSpinEldIV.stopAnimation();
                try {
                    if (!obj.isNull("Message")) {

                        if (Message.equals("Device Logout") ) {
                            if(DriverJsonArray.length() == 0) {
                                LogoutUser();
                            }else{
                                SyncUserData();
                            }
                        } else {

                            if(Message.length() > 0) {
                                if (flag == UpdateObdVeh && SharedPref.GetNewLoginStatus(getActivity())) {
                                    Global.EldScreenToast(loginDialogView, Message, getResources().getColor(R.color.colorVoilation));
                                } else {
                                    if (!Message.contains("failure")) {
                                        Global.EldScreenToast(OnDutyBtn, Message, getResources().getColor(R.color.colorVoilation));
                                    }
                                }
                            }

                            if (flag == GetAddFromLatLng) {
                                EnableJobViews();

                                if ( (CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2))
                                        && SharedPref.IsAOBRD(getActivity()) == false ) {
                                    AddressLine = csvReader.getShortestAddress(getActivity());
                                }

                                try {
                                    if (JobStatusInt != 101) {
                                        if (driverLocationDialog != null && DriverLocationDialog.updateViewTV != null) {
                                            DriverLocationDialog.updateViewTV.performClick();
                                        }
                                    } else {
                                        SaveJobStatusAlert(LocationJobTYpe);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (flag == GetRecapViewFlagMain || flag == GetRecapViewFlagCo) {
                                BackgroundLocationService.IsRecapApiACalled = false;
                            }

                            if (flag == GetDriverLog18Days) {
                                Log.d("response","response: "+response);
                            }

                            try {
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                            } catch (Exception e) {
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

        @Override
        public void getError(VolleyError error, int flag) {

            try {
                if (progressDialog != null)
                    progressDialog.dismiss();
            } catch (Exception e) {
            }
            loadingSpinEldIV.stopAnimation();
            progressBar.setVisibility(View.GONE);

            switch (flag) {

                case GetDriverPermission:
                case GetDriverLog18Days:
                    is18DaysLogApiCalled = false;
                    if (IsRefreshedClick) {
                        loadingSpinEldIV.stopAnimation();
                        IsRefreshedClick = false;
                        if(getActivity() != null && !getActivity().isFinishing()) {
                            Global.EldScreenToast(OnDutyBtn, "Failed to refresh", getResources().getColor(R.color.colorVoilation));
                        }
                    }

                    break;

                case UpdateObdVeh:

                    if (SharedPref.GetNewLoginStatus(getActivity())) {
                        Global.EldScreenToast(loginDialogView, Globally.DisplayErrorMessage(error.toString()), getResources().getColor(R.color.colorVoilation));
                    }else{
                        Global.EldScreenToast(OnDutyBtn, Globally.DisplayErrorMessage(error.toString()), getResources().getColor(R.color.colorVoilation));
                    }

                    break;

                case GetAddFromLatLng:

                    try {

                        if (!IsAddressUpdate) {
                            EnableJobViews();
                            Global.LONGITUDE = Global.CheckLongitudeWithCycle(Global.LONGITUDE);
                            if ( (CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2))
                                    && SharedPref.IsAOBRD(getActivity()) == false ) {
                                AddressLine = csvReader.getShortestAddress(getActivity());
                            }
                            progressBar.setVisibility(View.GONE);
                            if (JobStatusInt != 101) {
                                if (driverLocationDialog != null && DriverLocationDialog.updateViewTV != null) {
                                        DriverLocationDialog.updateViewTV.performClick();
                                }
                            } else {
                                SaveJobStatusAlert(LocationJobTYpe);
                            }
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }

                    IsAddressUpdate = false;
                    break;

                case GetRecapViewFlagMain:
                case GetRecapViewFlagCo:

                    BackgroundLocationService.IsRecapApiACalled = false;

                    break;


                default:
                    try {
                        EnableJobViews();
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };


    AsyncResponse asyncResponse = new AsyncResponse() {
        @Override
        public void onAsyncResponse(String response, String DriverId) {
            Log.d("async response", "async response: " +response );

            try {

                JSONObject obj = new JSONObject(response);
                String status = obj.getString("Status");

                if (status.equalsIgnoreCase("true")) {
                    // Clear Sync data after upload to server
                    syncingMethod.SyncingLogHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());
                }

                DriverJsonArray = new JSONArray();
                MainDriverPref.ClearLocFromList(getActivity());
                CoDriverPref.ClearLocFromList(getActivity());



            }catch (Exception e){
                e.printStackTrace();
            }
        }

    };


    void ClearDriverUnSavedlog(){
        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver) ) { // Single Driver Type and Position is 0
            DriverType = Constants.MAIN_DRIVER_TYPE;
            MainDriverPref.ClearLocFromList(getActivity());
        }else{
            DriverType = Constants.CO_DRIVER_TYPE;
            CoDriverPref.ClearLocFromList(getActivity());
        }
    }



    void SyncUserData(){
        /*JSONArray savedSyncedArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DRIVER_ID), dbHelper);
        File syncingFile = new File("");
        if(savedSyncedArray.length() > 0) {
            syncingFile = Globally.SaveFileInSDCard("Sync_", savedSyncedArray.toString(), false, getActivity());
        }*/


        if(isSingleDriver) {
            JSONArray syncArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DRIVER_ID), dbHelper);
            if(syncArray.length() > 0) {
                SyncDriverData(DRIVER_ID, syncArray, false);
                /*File blankFile = new File("");
                SyncDataUpload asyncTaskUpload = new SyncDataUpload(getActivity(), DRIVER_ID, syncingFile, blankFile, blankFile, false, asyncResponse);
                asyncTaskUpload.execute();*/
            }else{
                LogoutUser();
            }
        }else{
            JSONArray MainDriverSyncArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(MainDriverId), dbHelper);
            JSONArray CoDriverSyncArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(CoDriverId), dbHelper);

            if(MainDriverSyncArray.length() == 0 && CoDriverSyncArray.length() == 0 ) {
                LogoutUser();
            }else{

                if(MainDriverSyncArray.length() > 0) {
                    SyncDriverData(MainDriverId, MainDriverSyncArray, false);
                }

                if(CoDriverSyncArray.length() > 0) {
                    SyncDriverData(CoDriverId, CoDriverSyncArray, true);
                }

            }
        }


    }


    private void SyncDriverData(String DriverId, JSONArray syncArray, boolean isCoDriver){

        if(syncArray.length() > 0) {
            String filePrefix = "Sync_";
            if(isCoDriver){
                filePrefix = "SyncCo_";
            }
           File driverSyncFile = Globally.SaveFileInSDCard(filePrefix, syncArray.toString(), false, getActivity());

            // Sync driver log API data to server with SAVE_LOG_TEXT_FILE (SAVE sync data service)
            SyncDataUpload syncDataUpload = new SyncDataUpload(getActivity(), DriverId, driverSyncFile,
                    null, null, false, asyncResponse );
            syncDataUpload.execute();
        }
    }



    void clearCalculationsView(){
        TotalOnDutyHoursInt         = 0;
        TotalDrivingHoursInt        = 0;
        LeftDayOnDutyHoursInt       = 0;
        LeftDayDrivingHoursInt      = 0;
        LeftWeekOnDutyHoursInt      = 0;
        TotalOffDutyHoursInt        = 0;
        TotalSleeperBerthHoursInt   = 0;
        OffDutyPerShift             = 0;
        SleeperPerShift             = 0;

        SetTextOnView("");
    }

    public void SaveJobStatusAlert(final String Status) {

        try {
            closeDialogs();

            if (isDrivingCalled) {
                DRIVER_JOB_STATUS = DRIVING;
                if (IsAOBRD && !IsAOBRDAutomatic) {
                    JobStatusInt = DRIVING;
                    GetAddFromLatLng();
                } else {
                    SaveDrivingStatus();
                }
            } else {

                String isPcYm = hMethods.isPersonalOrYM(driverLogArray);
                String title = "";

                if(isPcYm.equals(ConstantsKeys.Personal)) {
                    title = "Do you want to start <b>" + Status + "</b> & end <b>Personal Use</b> ?" ;
                }else if (isPcYm.equals(ConstantsKeys.YardMove) ){
                    title = "Do you want to start <b>" + Status + "</b> & end <b>Yard Move</b> ?" ;
                }else{
                    if (Status.equals("On Duty") && isYardBtnClick) {
                        title = "Do you want to change the status to On Duty (Yard Move) ?";
                    } else {
                        title = "Do you want to change the status to " + Status + "?" ;
                    }
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(Html.fromHtml(title));
                alertDialogBuilder.setMessage("");  //"You have " + time + " remaining in " + OldStatus+"."
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();

                                constants.IS_ELD_ON_CREATE = false;
                                if (Status.equals("Off Duty")) {
                                    SaveOffDutyStatus();

                                    // call lat long API
                                    IsAddressUpdate = true;
                                    GetAddFromLatLng();

                                } else if (Status.equals("Sleeper Berth")) {
                                    SaveSleeperStatus();

                                    // call lat long API
                                    IsAddressUpdate = true;
                                    GetAddFromLatLng();

                                } else if (Status.equals("Driving")) {
                                    DRIVER_JOB_STATUS = DRIVING;
                                    if (IsAOBRD && !IsAOBRDAutomatic) {
                                        JobStatusInt = DRIVING;
                                        GetAddFromLatLng();
                                    } else {
                                        SaveDrivingStatus();

                                        // call lat long API
                                        IsAddressUpdate = true;
                                        GetAddFromLatLng();

                                    }

                                } else if (Status.equals("On Duty")) {
                                    DRIVER_JOB_STATUS = ON_DUTY;
                                    if (IsAOBRD && !IsAOBRDAutomatic) {
                                        JobStatusInt = ON_DUTY;
                                        GetAddFromLatLng();
                                    } else {
                                        SaveOnDutyStatus();
                                    }
                                } else if (Status.equals(getResources().getString(R.string.yard_move))) {
                                    DRIVER_JOB_STATUS = ON_DUTY;
                                    if (IsAOBRD && !IsAOBRDAutomatic) {
                                        JobStatusInt = ON_DUTY;
                                        GetAddFromLatLng();
                                    } else {
                                        SaveOnDutyStatus();
                                    }
                                } else {
                                    if(getActivity() != null && !getActivity().isFinishing()) {
                                        if (trailerDialog != null && trailerDialog.isShowing())
                                            trailerDialog.dismiss();

                                        trailerDialog = new TrailorDialog(getActivity(), constants.Personal, false, TrailorNumber,
                                                0, false, Global.onDutyRemarks, oldStatusView, dbHelper, new TrailorListener());
                                        trailerDialog.show();
                                    }

                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.GONE);
                        EnableJobViews();

                        dialog.dismiss();
                    }
                });

                if(getActivity() != null && !getActivity().isFinishing()) {
                    saveJobAlertDialog = alertDialogBuilder.create();
                    vectorDialogs.add(saveJobAlertDialog);
                    saveJobAlertDialog.show();
                }
            }
        }catch (Exception e){}
        isDrivingCalled = false;
    }




    private void certifyLogAlert(String title, String message){

        String certifyAlertTime = SharedPref.getCertifyAlertViewTime(getActivity());
        boolean isAlertAllowed = true;

        try{
            if(getActivity() != null && !getActivity().isFinishing()){
                if(certifyAlertTime.length() > 0){
                    DateTime savedDate = Global.getDateTimeObj(certifyAlertTime, false);
                    DateTime currentDate = Global.getDateTimeObj(Global.GetCurrentUTCTimeFormat(), false);
                    int dayDiff = Days.daysBetween(savedDate.toLocalDate(), currentDate.toLocalDate()).getDays();
                    //(int) Constants.getDateTimeDuration(savedDate, currentDate).getStandardDays();
                    if(dayDiff == 0){
                        int hourDiff = currentDate.getHourOfDay() - savedDate.getHourOfDay();
                        if(hourDiff < 4){
                            isAlertAllowed = false;
                        }
                    }
                }

                if(isAlertAllowed && SharedPref.IsDOT(getActivity()) == false) {
                    if (certifyLogAlert != null && certifyLogAlert.isShowing()) {
                        Log.d("dialog", "dialog is showing");
                    } else {

                        closeDialogs();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle(Html.fromHtml(title));
                        alertDialogBuilder.setMessage(Html.fromHtml(message));
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Agree",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        dialog.dismiss();

                                        moveToCertifyWithPopup();

                                    }
                                });

                        alertDialogBuilder.setNegativeButton("Not Ready", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                // call NotReady API
                                if (Global.isConnected(getActivity())) {
                                    params = new HashMap<String, String>();
                                    params.put(ConstantsKeys.DriverId, DRIVER_ID);
                                     params.put(ConstantsKeys.DeviceId, DeviceId );
                                    params.put(ConstantsKeys.DriverName, nameTv.getText().toString() );
                                     params.put(ConstantsKeys.CompanyId, DriverCompanyId );
                                    params.put(ConstantsKeys.DriverTimeZoneName, DriverTimeZone );
                                    params.put(ConstantsKeys.LogDateTime, Global.getCurrentDate() );

                                    notReadyRequest.executeRequest(Request.Method.POST, APIs.SAVE_CERTIFY_SIGN_REJECTED_AUDIT , params, NotReady,
                                            Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);
                                }

                            }
                        });


                        certifyLogAlert = alertDialogBuilder.create();
                        vectorDialogs.add(certifyLogAlert);
                        certifyLogAlert.show();

                        SharedPref.setCertifyAlertViewTime(Global.GetCurrentUTCTimeFormat(), getActivity());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void moveToCertifyWithPopup(){
        try {
            FragmentManager fragManager = getActivity().getSupportFragmentManager();

            String dayOfTheWeek = "", MonthFullName = "", MonthShortName = "";

            DateTime currentDate = Global.getDateTimeObj(Global.getCurrentDate(), false);
            String[] dateMonth = Global.dateConversionMMMM_ddd_dd(currentDate.toString()).split(",");

            if (dateMonth.length > 1) {
                MonthFullName = dateMonth[0];
                MonthShortName = dateMonth[1];
                dayOfTheWeek = dateMonth[2];
            }

            initilizeEldView.MoveFragment(SelectedDate, dayOfTheWeek, MonthFullName, MonthShortName, constants.CertifyLog, isCertifyLog,
                    VIN_NUMBER, offsetFromUTC, Global.FinalValue(LeftWeekOnDutyHoursInt), Global.FinalValue(LeftDayOnDutyHoursInt),
                    Global.FinalValue(LeftDayDrivingHoursInt), CurrentCycleId, VehicleId,
                    isCertifySignPending(false, false ), isFragmentAdd, fragManager, driverLogArray.toString());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    public void YardMovePersonalStatusAlert(boolean isYardMove, boolean isPer, boolean isLogin) {

        try {
            if(getActivity() != null && !getActivity().isFinishing()) {
                if (continueStatusDialog != null && continueStatusDialog.isShowing()) {
                    Log.d("dialog", "dialog is showing");
                } else {

                    String TruckIgnitionStatus = SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getActivity());
                    continueStatusDialog = new ContinueStatusDialog(getActivity(), isYardMove, isPer, isLogin, TruckIgnitionStatus, new ContinueListener());
                    continueStatusDialog.show();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void shipmentInfoAlert() {

        try{
            if(getActivity() != null && !getActivity().isFinishing()){

                closeDialogs();

                String title = "";
              /*  if(isUnloading){
                    title = "Do you want to clear your shipping details ?";
                }else{*/
                title = "Do you want to add/update your shipping details ?";
                //}

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Shipping Details");
                alertDialogBuilder.setMessage(title);
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();

                                isDrivingCalled = false;
                                showShippingDialog(false);

                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        isDrivingCalled = true;
                        DrivingWithCertifySign();
                    }
                });


                saveJobAlertDialog = alertDialogBuilder.create();
                vectorDialogs.add(saveJobAlertDialog);
                saveJobAlertDialog.show();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void closeDialogs() {
        for (AlertDialog dialog : vectorDialogs)
            if (dialog.isShowing()) dialog.dismiss();
    }






}
