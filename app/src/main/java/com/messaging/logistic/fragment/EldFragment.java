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
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
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
import com.background.service.LocationService;
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
import com.custom.dialogs.ChangeCycleDialog;
import com.custom.dialogs.ConfirmationDialog;
import com.custom.dialogs.ContinueStatusDialog;
import com.custom.dialogs.DatePickerDialog;
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
import com.driver.details.DriverConst;
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
import com.models.RecapSignModel;
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


public class EldFragment extends Fragment implements View.OnClickListener {

    View rootView, loginDialogView;

    RelativeLayout StatusMainView, refreshPageBtn;
    LinearLayout DriverLay, trailorLayout, truckLay, remainingLay, usedHourLay, shippingLay, odometerLay,  eldNewBottomLay, eldChildSubLay;
    ImageView editTrailorIV, editTruckIV, eldMenuBtn, eldMenuErrorImgVw, certifyLogErrorImgVw;
    LoadingSpinImgView loadingSpinEldIV;
    RelativeLayout OnDutyBtn, DrivingBtn, OffDutyBtn, SleeperBtn, eldMenuLay, dayNightLay, eldHomeDriverUiLay, settingsMenuBtn, otherOptionBtn, malfunctionLay;
    ImageView calendarBtn, coDriverImgView, connectionStatusImgView, cyleFlagImgView;
    Button sendReportBtn, yardMoveBtn, personalUseBtn;
    RelativeLayout certifyLogBtn;
    public static Button refreshLogBtn, moveToCertifyPopUpBtn;
    public static TextView summaryBtn;
    int DRIVER_JOB_STATUS = 1, SWITCH_VIEW = 1, oldStatusView = 0, DriverType = 0;
    TextView dateTv, nameTv, jobTypeTxtVw, perDayTxtVw, jobTimeTxtVw, jobTimeRemngTxtVw, EldTitleTV,
            timeRemainingTxtVw, invisibleTxtVw, viewHistoryBtn;
    TextView tractorTv, trailorTv, coDriverTv, otherOptionBadgeView;
    TextView onDutyViolationTV, drivingViolationTV, sleeperViolationTV, offDutyViolationTV;
    TextView onDutyTimeTxtVw, drivingTimeTxtVw, sleeperTimeTxtVw, offDutyTimeTxtVw;
    TextView onDutyTxtVw, drivingTxtVw, sleeperTxtVw, offDutyTxtVw, excpnEnabledTxtVw;
    TextView DriverComNameTV, coDriverComNameTV, remainingTimeTopTV, currentCycleTxtView, refreshTitleTV;
    TextView asPerShiftOnDutyTV, asPerShiftDrivingTV, asPerDateSleepTV, asPerDateOffDutyTV, malfunctionTV;

    boolean IsRefreshedClick = false, IsAOBRDAutomatic = false, IsAOBRD = false, isNewDateStart = true, isYardBtnClick = false, isFragmentAdd = false;
    boolean isFirst = true, isViolation = false, isCertifyLog = false, IsSaveOperationInProgress = false, isDrivingCalled = false, IsDOT = false;
    boolean IsAddressUpdate = false, is18DaysLogApiCalled = false, isTimeDefault = true;
    public static boolean IsOdometerReading = false, IsPopupDismissed = false;
    Animation emptyTrailerNoAnim, OdometerFaceView, exceptionFaceView, connectionStatusAnimation, editLogAnimation;
    String strCurrentDate = "", DRIVER_ID = "0", DeviceId = "", LoginTruckChange = "true";
    String SelectedDate = "", CompanytimeZone = "", UtcTimeZone = "", CountryCycle = "", VIN_NUMBER = "";
    String isPersonalOld = "false", Reason = "", TrailorNumber = "", MainDriverName = "", CoDriverName = "";
    String SavedCanCycle = "", SavedUsaCycle = "", CurrentCycle = "", CurrentCycleId = "", TeamDriverType = "",
            MainDriverId = "", CoDriverId = "", ViolationsReason = "", titleDesc, okText, ptiSelectedtxt = "";
    String certifyTitle = "<font color='#1A3561'><b>Alert !!</b></font>";
    String DriverCompanyId = "", DriverCarrierName = "", CoDriverCarrierName = "", State = "", Country = "", AddressLine = "", LocationType = "", MalfunctionDefinition = "";
    public static String City = "", AobrdState = "", isPersonal = "false", VehicleId = "", DriverStatusId = "1";
    String packageName              = "com.messaging.logistic";

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
    DatePickerDialog dateDialog;
    ShippingDocDialog shippingDocDialog;
    DriverLocationDialog driverLocationDialog;
    TimeZoneDialog timeZoneDialog;
    RemainingTimeDialog remainingDialog;
    OtherOptionsDialog otherOptionsDialog;
    EldNotificationDialog eldNotificationDialog;


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
   // final int PcEndCnfirmation      = 260;
   // final int YmEndCnfirmation      = 270;


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

    String DeviceTimeZone = "", DriverTimeZone = "", LocationJobTYpe = "";
    String WeeklyRemainingTime = "00:00", DrivingRemainingTime = "00:00", OnDutyRemainingTime = "00:00";
    MyTimerTask timerTask;
    Globally Global;
    CsvReader csvReader;

    SaveDriverLogPost saveDriverLogPost;
    VolleyRequest GetLogRequest, GetOdometerRequest, GetOdo18DaysRequest, GetLog18DaysRequest, GetOnDutyRequest, GetOBDVehRequest, GetShippingRequest, GetPermissions;
    VolleyRequest SaveOBDVehRequest, SaveTrailerNumber, Inspection18DaysRequest, GetInspectionRequest, GetNotificationRequest;
    VolleyRequest GetNewsNotificationReq, GetReCertifyRequest, GetRecapView18DaysData, notReadyRequest ;
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
    TcpClient tcpClient;
    OBDDeviceData data;
    Decoder decoder;
    Utils obdUtil;


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
            data            = new OBDDeviceData();
            decoder         = new Decoder();
            obdUtil         = new Utils(getActivity());
            tcpClient       = new TcpClient(obdResponseHandler);

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
        viewHistoryBtn.setOnClickListener(this);
        certifyLogBtn.setOnClickListener(this);
        OnDutyBtn.setOnClickListener(this);
        DrivingBtn.setOnClickListener(this);
        OffDutyBtn.setOnClickListener(this);
        SleeperBtn.setOnClickListener(this);
        personalUseBtn.setOnClickListener(this);
        moveToCertifyPopUpBtn.setOnClickListener(this);
        refreshLogBtn.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        shippingLay.setOnClickListener(this);
        odometerLay.setOnClickListener(this);
        summaryBtn.setOnClickListener(this);
        invisibleTxtVw.setOnClickListener(this);
        dayNightLay.setOnClickListener(this);
        settingsMenuBtn.setOnClickListener(this);
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
        Log.d("IsAOBRD", "IsAOBRD 1: " + IsAOBRD);

        isViolation = SharedPref.IsViolation(getActivity());
        progressD = new ProgressDialog(getActivity());
        progressD.setMessage("Saving ...");

        AddStatesInList();
        initilizeEldView.AddTempRemark();
        GetSavePreferences();
        GetDriversSavedData(false, DriverType);
        GetTripSavedData();
        SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);


        /*========= Start Service =============*/
        Constants.isEldHome = false;
        startService();


        IsDOT = SharedPref.IsDOT(getActivity());

        if(IsDOT){
            dotWithData();
        }else {
            // -------------------------- CALL API --------bluetooth------------------
             if(TabAct.isTabActOnCreate) {
                 constants.IS_ELD_ON_CREATE = true;
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

        if (!SharedPref.GetNewLoginStatus(getActivity()) && Constants.IsHomePageOnCreate){
            isCertifySignPending( Constants.IsHomePageOnCreate);
        }
        Constants.IsHomePageOnCreate = false;


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

                    if(getActivity() != null){
                            if (isHaulExcptn || isAdverseExcptn) {
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
                    if(getActivity() != null) {
                        int obdStatus = SharedPref.getObdStatus(getActivity());
                        if (obdStatus == Constants.WIRED_CONNECTED || obdStatus == Constants.BLE_CONNECTED || obdStatus == Constants.WIFI_CONNECTED) {

                            if(obdStatus == Constants.BLE_CONNECTED){
                                connectionStatusImgView.setImageResource(R.drawable.ble_ic);
                            }else if(obdStatus == Constants.WIRED_CONNECTED){
                                connectionStatusImgView.setImageResource(R.drawable.obd_active);
                            }else{
                                connectionStatusImgView.setImageResource(R.drawable.wifi_obd_active);
                            }

                            connectionStatusAnimation.cancel();
                            connectionStatusImgView.setAlpha(1f);
                            connectionStatusImgView.setColorFilter(getResources().getColor(R.color.colorPrimary));

                        } else {
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
        viewHistoryBtn          = (TextView) view.findViewById(R.id.viewHistoryBtn);
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

        IsValidTime = Global.isCorrectTime(getActivity() );

        boolean isSavedTimeZoneCorrect = false;
        if(DeviceTimeZone.equalsIgnoreCase(DriverTimeZone) || offsetFromUTC == offSetFromServer){
            isSavedTimeZoneCorrect = true;
          /*  if(!isConnected){
                boolean isCurrentTimeBigger = Global.isCurrentTimeBigger(getActivity());
                if(isCurrentTimeBigger) {
                    IsValidTime = true;
                }
            }*/
        }


        if ( !isSavedTimeZoneCorrect || !IsValidTime ) {
            showTimeZoneAlert(isConnected, isSavedTimeZoneCorrect, IsValidTime);
        } else {
            if (timeZoneDialog != null && timeZoneDialog.isShowing()) {
                timeZoneDialog.dismiss();
            }
        }



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
                         saveInfo("", false, false, false);
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

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver( progressReceiver, new IntentFilter(ConstantsKeys.IsIgnitionOn));


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
            if(getActivity() != null) {
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
                                    Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.wifi_inactive), getResources().getColor(R.color.colorSleeper));
                                }
                            }/*else{
                                connectionStatusImgView.setImageResource(R.drawable.obd_inactive);
                                connectionStatusImgView.startAnimation(connectionStatusAnimation);
                                if (isToastShowing) {
                                    Global.EldToastWithDuration4Sec(connectionStatusImgView, getResources().getString(R.string.obd_data_connection_desc), getResources().getColor(R.color.colorSleeper) );
                                }
                            }*/
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
    }


    void setExceptionView(){
        getExceptionStatus();

        if (isHaulExcptn || isAdverseExcptn) {
            excpnEnabledTxtVw.startAnimation(exceptionFaceView);
            excpnEnabledTxtVw.setVisibility(View.VISIBLE);

            if(isHaulExcptn){
                excpnEnabledTxtVw.setText(getString(R.string.short_haul_excp_enabled));
            }else{
                excpnEnabledTxtVw.setText(getString(R.string.adverse_excp_enabled));
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
                    if(getActivity() != null) {
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

                    if(getActivity() != null) {
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
        GetDriverCycle(DriverType);

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
            bundle.putInt("total_offduty", TotalOffDutyHoursInt);
            bundle.putInt("total_sleeper", TotalSleeperBerthHoursInt);
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
                            saveInfo("", false, false, false);
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
                GetOnDutyRemarks();
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
            Log.e("Log", "----Running");
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
                                    saveInfo("", false, false, false);
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
                        recapViewMethod, Global, hMethods, dbHelper, logPermissionObj);

                if (isPendingNotifications | isUnCertified || isMissingLoc) {
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
            // CurrentCycleId = Global.USA_WORKING_7_DAYS;
            IsLogApiACalled = false;
            Global.hideSoftKeyboard(getActivity());

            DeviceId = SharedPref.GetSavedSystemToken(getActivity());
            CountryCycle = SharedPref.getCountryCycle("CountryCycle", getActivity());
            CompanytimeZone = SharedPref.getTimeZone(getActivity());
            UtcTimeZone = SharedPref.getUTCTimeZone("utc_time_zone", getActivity());
            VIN_NUMBER = SharedPref.getVINNumber( getActivity());
            Global.TRAILOR_NUMBER = SharedPref.getTrailorNumber(getActivity());
            TrailorNumber = SharedPref.getTrailorNumber(getActivity());
            TeamDriverType = SharedPref.getDriverType(getActivity());

            if(TrailorNumber.equalsIgnoreCase("null")) {
                TrailorNumber = "";
                Global.TRAILOR_NUMBER = "";
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
            //dateTv.setText(SelectedDate);

            Calendar cal=Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(cal.getTime());
            dateTv.setText( month_name + " " + SelectedDate.substring(3, 5 )  );   //+ " " + SelectedDate.substring(6, SelectedDate.length())

            DeviceTimeZone = TimeZone.getDefault().getDisplayName();
            IsNorthCanada  =  SharedPref.IsNorthCanada(getActivity());


            try {
                if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
                    DriverType = Constants.MAIN_DRIVER_TYPE;     // Single Driver Type and Position is 0
                    SavedCanCycle = DriverConst.GetDriverSettings(DriverConst.CANCycleName, getActivity());
                    SavedUsaCycle = DriverConst.GetDriverSettings(DriverConst.USACycleName, getActivity());
                    DriverCompanyId = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
                    Global.TRUCK_NUMBER = DriverConst.GetDriverTripDetails(DriverConst.Truck, getActivity());
                    isDeferral      = SharedPref.getDeferralForMain(getActivity());

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
                    Global.TRUCK_NUMBER = DriverConst.GetCoDriverTripDetails(DriverConst.CoTruck, getActivity());
                    isDeferral      = SharedPref.getDeferralForCo(getActivity());

                    DRIVER_ID = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
                    SharedPref.setDriverId(DRIVER_ID, getActivity());
                    DriverTimeZone = DriverConst.GetCoDriverSettings(DriverConst.CoDriverTimeZone, getActivity());

                    String offset = DriverConst.GetCoDriverSettings(DriverConst.CoOffsetHours, getActivity());
                    if (offset.length() > 0) {
                        offSetFromServer = Integer.valueOf(offset);
                    }

                }
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

      /*  DateTime savedDateTime = new DateTime();
        String currentSavedTime = SharedPref.getSystemSavedDate(getActivity());
        if(currentSavedTime.length() > 10) {
            savedDateTime = Global.getDateTimeObj(currentSavedTime, false);
        }else{
            if(offsetFromUTC == offSetFromServer) {
                savedDateTime = new DateTime(Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false));
            }
        }*/

// ----------------- Get Date Difference ---------------------

     /*   DateTime systemDateTime = new DateTime(Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false));
        if(currentSavedTime.length() > 10) {
            long diffInMillis = systemDateTime.getMillis() - savedDateTime.getMillis();
            int minDiff = (int) TimeUnit.MILLISECONDS.toMinutes(diffInMillis);

            if (Math.max(-5, minDiff) == Math.min(minDiff, 5)) {
                IsValidTime = true;
            } else {
                IsValidTime = false;
            }
        }else {
            IsValidTime = true;
        }*/
    }


    void GetDriverCycle(int DriverType){
        try{
            if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                CurrentCycle   = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycle, getActivity());
                CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());
            }else{
                CurrentCycle   = DriverConst.GetCoDriverCurrentCycle(DriverConst.CoCurrentCycle, getActivity());
                CurrentCycleId = DriverConst.GetCoDriverCurrentCycle(DriverConst.CoCurrentCycleId, getActivity());
            }

            if (CurrentCycle.equalsIgnoreCase("null") || CurrentCycleId.equals("-1")  || CurrentCycleId.length() == 0) {
                CurrentCycle = Global.USA_WORKING_7_DAYS_NAME;
                CurrentCycleId = Global.USA_WORKING_7_DAYS;
            }

            remainingTimeTopTV.setText(Html.fromHtml("Cycle time left <b>" + Global.FinalValue(LeftWeekOnDutyHoursInt) + "</b>" ));
            if (CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) && SharedPref.IsNorthCanada(getActivity())) {
                currentCycleTxtView.setText(CurrentCycle + " (N)");
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
                tractorTv.setText(Global.TRUCK_NUMBER);
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

                GetDriverCycle(DriverType);

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
        if (DriverType == Constants.MAIN_DRIVER_TYPE) {
            Global.TRUCK_NUMBER = DriverConst.GetDriverTripDetails(DriverConst.Truck, getActivity());
            // Global.TRAILOR_NUMBER = DriverConst.GetDriverTripDetails( DriverConst.Trailor, getActivity());
            Global.CONSIGNEE_NAME = DriverConst.GetDriverTripDetails(DriverConst.ConsigneeName, getActivity());
            Global.SHIPPER_NAME = DriverConst.GetDriverTripDetails(DriverConst.ShipperName, getActivity());
            Global.TRIP_NUMBER = DriverConst.GetDriverTripDetails(DriverConst.TripNumber, getActivity());
            VIN_NUMBER = DriverConst.GetDriverTripDetails(DriverConst.VIN, getActivity());
        } else {
            Global.TRUCK_NUMBER = DriverConst.GetCoDriverTripDetails(DriverConst.CoTruck, getActivity());
            // Global.TRAILOR_NUMBER = DriverConst.GetCoDriverTripDetails( DriverConst.CoTrailor, getActivity());
            Global.CONSIGNEE_NAME = DriverConst.GetCoDriverTripDetails(DriverConst.CoConsigneeName, getActivity());
            Global.SHIPPER_NAME = DriverConst.GetCoDriverTripDetails(DriverConst.CoShipperName, getActivity());
            Global.TRIP_NUMBER = DriverConst.GetCoDriverTripDetails(DriverConst.CoTripNumber, getActivity());
            VIN_NUMBER = DriverConst.GetCoDriverTripDetails(DriverConst.CoVIN, getActivity());
        }
        SetDataInView();
    }


    // Call API to save driver job status ..................
    void saveInfo(String jobStatus, boolean isSaveJob, boolean isLoad, boolean IsRecap) {
        int socketTimeout ;
        int logArrayCount = DriverJsonArray.length();
        if(logArrayCount < 3 ){
            socketTimeout = constants.SocketTimeout10Sec;  //10 seconds
        }else if(logArrayCount < 10){
            socketTimeout = constants.SocketTimeout20Sec;  //20 seconds
        }else{
            socketTimeout = constants.SocketTimeout40Sec;  //40 seconds
        }
        isFirst = false;
        if (isSaveJob && !jobStatus.equals("")) {
            SaveDriverJob(jobStatus);
        }
        /* =========== Save Data as File in SD Card ==============  */
        IsPopupDismissed = false;
        SAVE_DRIVER_STATUS(DriverJsonArray, isLoad, IsRecap, socketTimeout);

    }


    void SetJobButtonView(int job, boolean isViolation, String IsPersonal) {

        switch (job) {
            case OFF_DUTY:  //1

                initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed);
                if (IsPersonal.equals("false")) {
                    personalUseBtn.setText(getString(R.string.pc_start));
                    initilizeEldView.ActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, false);
                } else {
                    initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed);
                    personalUseBtn.setBackgroundResource(R.drawable.eld_blue_new_selector);
                    personalUseBtn.setTextColor(Color.WHITE);
                    personalUseBtn.setText(getString(R.string.pc_end));
                }


                StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);

                break;

            case SLEEPER:  //2

                initilizeEldView.ActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, false);
                initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed);
                StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);

                break;

            case DRIVING:   //3

                initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.ActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, isViolation);
                initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed);

                if (isViolation) {
                    StatusMainView.setBackgroundResource(R.drawable.red_default);
                } else {
                    StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);
                }
                break;

            case ON_DUTY:   //4
                initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.ActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, isViolation);
                initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed);

                if (isViolation) {
                    StatusMainView.setBackgroundResource(R.drawable.red_default);
                } else {
                    StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);
                }

                break;


            default:
                initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed);
                initilizeEldView.ActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, false);
                StatusMainView.setBackgroundResource(R.drawable.eld_blue_new_default);


                break;
        }
    }


    void setJobButtonViewTrue(int job, String isPersonal) {
        switch (job) {
            case OFF_DUTY:  //1
                if (isPersonal.equals("true")) {
                    initilizeEldView.InActiveView(OffDutyBtn, offDutyViolationTV, offDutyTimeTxtVw, offDutyTxtVw, asPerDateOffDutyTV, personalUseBtn, IsPersonalUseAllowed);
                    initilizeEldView.InActiveView(SleeperBtn, sleeperViolationTV, sleeperTimeTxtVw, sleeperTxtVw, asPerDateSleepTV, personalUseBtn, IsPersonalUseAllowed);
                    initilizeEldView.InActiveView(DrivingBtn, drivingViolationTV, drivingTimeTxtVw, drivingTxtVw, asPerShiftDrivingTV, personalUseBtn, IsPersonalUseAllowed);
                    initilizeEldView.InActiveView(OnDutyBtn, onDutyViolationTV, onDutyTimeTxtVw, onDutyTxtVw, asPerShiftOnDutyTV, personalUseBtn, IsPersonalUseAllowed);
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
                if(getActivity() != null) {
                    OnDutyBtn.setEnabled(true);
                    DrivingBtn.setEnabled(true);
                    OffDutyBtn.setEnabled(true);
                    SleeperBtn.setEnabled(true);
                    personalUseBtn.setEnabled(true);
                    progressBar.setVisibility(View.GONE);


                    setYardMoveView();

                    if (IsPersonalUseAllowed) {
                        if (DRIVER_JOB_STATUS == constants.OFF_DUTY && isPersonal.equals("true")) {
                            personalUseBtn.setTextColor(getResources().getColor(R.color.whiteee));
                        } else {
                            personalUseBtn.setTextColor(getResources().getColor(R.color.color_eld_theme));
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
                            recapViewMethod, Global, hMethods, dbHelper, logPermissionObj);
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

        if(getActivity() != null) {
            if (!IsAOBRD || IsAOBRDAutomatic) {
                Constants.isEldHome = false;
                startService();
            }

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

                break;


            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;


            case R.id.shippingLay:
                //constants.isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID )
                if(constants.isActionAllowed(getActivity())){
                    showShippingDialog(false);
                } else {
                    Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                }

                break;

            case R.id.odometerLay:

                if(!SharedPref.IsOdometerFromOBD(getActivity())) {
                    //isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID )){   //constants.isActionAllowed(getActivity())){
                    if(constants.isActionAllowed(getActivity())){
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
                    if(getActivity() != null){
                      if(constants.isActionAllowedRestricted(getActivity()) ){    //constants.isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID )
                        if (Global.isConnected(getActivity())) {
                            if (DRIVER_JOB_STATUS == DRIVING ) {
                                Global.EldScreenToast(OnDutyBtn, ConstantsEnum.TRAILER_CHANGE, getResources().getColor(R.color.colorVoilation));
                            } else {
                                trailerDialog = new TrailorDialog(getActivity(), "trailor", false, TrailorNumber, 0, false,
                                        Global.onDutyRemarks, 0, dbHelper, new TrailorListener());
                                trailerDialog.show();
                            }
                        } else {
                            Global.EldScreenToast(truckLay, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                        }
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

                    //constants.isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID )
                    if(constants.isActionAllowed(getActivity())){
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
               // if(constants.isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID ) ) {  //constants.isActionAllowed(getActivity())
                    TabAct.host.setCurrentTab(1);
                /*}else{
                    Global.EldScreenToast(settingsMenuBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                }*/
                break;


            case R.id.otherOptionBtn:
                try {
                    if (otherOptionsDialog != null && otherOptionsDialog.isShowing()) {
                        otherOptionsDialog.dismiss();
                    }
                    boolean isPendingNotifications = isPendingNotifications(DRIVER_JOB_STATUS);
                    otherOptionsDialog = new OtherOptionsDialog(getActivity(), isPendingNotifications, pendingNotificationCount,
                            constants.CheckGpsStatusToCheckMalfunction(getActivity()), DriverType, CurrentCycleId, driverPermissionMethod,
                            recapViewMethod, hMethods, dbHelper);
                    otherOptionsDialog.show();
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
                            saveInfo("", false, false, false);
                        }
                    } else {
                        // Global.EldScreenToast(OnDutyBtn, getString(R.string.already_uploading), getResources().getColor(R.color.color_eld_theme));
                        refreshDataEvent(isConnected);
                    }
                }else{
                    loadingSpinEldIV.stopAnimation();
                    Global.EldScreenToast(OnDutyBtn, Global.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                }


                break;


            case R.id.yardMoveBtn:

                MalfunctionDefinition = "";
                if (SharedPref.IsYardMoveAllowed(getActivity())) {
                    if (constants.isObdConnected(getActivity())) {

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
                            String pcBtnLabel = personalUseBtn.getText().toString();
                            DateTime lastSaveUtcDate = Global.getDateTimeObj(SharedPref.getCurrentUTCTime(getActivity()), false);
                            DateTime currentUTCTime = Global.getDateTimeObj(Global.GetCurrentDateTime(), true);
                            int dayDiff = hMethods.DayDiff(currentUTCTime, lastSaveUtcDate);

                            if( (CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2)) &&
                                    pcBtnLabel.equals(getString(R.string.pc_start)) && dayDiff == 0 &&
                                    SharedPref.isPersonalUse75KmCrossed(getActivity())){
                                Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.personal_use_limit_75),
                                        getResources().getColor(R.color.colorVoilation));
                            }else {

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


                        }else{
                            Global.EldToastWithDuration4Sec(OnDutyBtn, getResources().getString(R.string.connect_with_obd_first), getResources().getColor(R.color.colorVoilation));
                        }

                    } else {
                        Global.EldToastWithDuration4Sec(personalUseBtn, getResources().getString(R.string.personal_not_allowed), getResources().getColor(R.color.colorVoilation));
                    }


                break;


            case R.id.onDutyLay:
                MalfunctionDefinition = "";
                //constants.isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID )
                if(constants.isActionAllowed(getActivity())){
                    isYardBtnClick = false;
                    OnDutyBtnClick();
                } else {
                    Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                }

                break;

            case R.id.drivingLay:
                MalfunctionDefinition = "";
                DrivingBtnClick();

                break;


            case R.id.sleeperDutyLay:

                MalfunctionDefinition = "";
                //constants.isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID )
                if(constants.isActionAllowed(getActivity())){
                    SleeperBtnClick();
                } else {
                    Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                }


                break;


            case R.id.offDutyLay:

                MalfunctionDefinition = "";
                //constants.isActionAllowedWithCoDriver(getActivity(), dbHelper, hMethods, Global, DRIVER_ID )
                if(constants.isActionAllowed(getActivity())){
                    OffDutyBtnClick();
                } else {
                    Global.EldScreenToast(OnDutyBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                }

                break;



            case R.id.refreshLogBtn:
                if(SharedPref.isSuggestedEditOccur(getActivity()) && Constants.isClaim == false){
                    Toast.makeText(getActivity(), getString(R.string.other_suggested_log), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getActivity(), SuggestedFragmentActivity.class);
                    i.putExtra(ConstantsKeys.suggested_data, "");
                    i.putExtra(ConstantsKeys.Date, "");
                    startActivity(i);
                }else {
                    IsRefreshedClick = false;
                    GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
                }

                break;

            case R.id.resetTimerBtn:
                isFragmentAdd = true;
                moveToCertifyWithPopup();
                isFragmentAdd = false;
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
                         saveInfo("", false, false, false);
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
                if (isCertifySignPending(false)) {
                    Global.DriverSwitchAlert(getActivity(), certifyTitle, titleDesc, okText);
                    resetCertifyLogDialogTitles();

                } else {
                    if (hMethods.CanChangeStatus(OFF_DUTY, driverLogArray, Global, false)) {
                        restartLocationService();

                        // if Odometers are saving through OBD automatically then makes all the condition false to ignore odometer save
                        if(SharedPref.IsOdometerFromOBD(getActivity())) {
                            SharedPref.OdometerSaved(true, getActivity());
                            EldFragment.IsStartReading = false;
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
                                    if (DRIVER_JOB_STATUS == OFF_DUTY && EldFragment.IsStartReading) {
                                        Global.OdometerDialog(getActivity(), ConstantsEnum.SAVE_END_READING, true, OFF_DUTY, OffDutyBtn, alertDialog);
                                    } else {
                                        JobStatusInt = OFF_DUTY;
                                        OpenLocationDialog(OFF_DUTY, OldSelectedStatePos, false);
                                    }
                                }
                            } else {
                                AFTER_PERSONAL_JOB = PERSONAL_OFF_DUTY;
                                if (isPersonal.equals("true")) {
                                    if (EldFragment.IsStartReading) {
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
                           /* } else {
                                OpenLocationDialog(OFF_DUTY, OldSelectedStatePos);
                            }*/
                        }  else {
                            if (constants.isLocMalfunctionEvent(getActivity(), DriverType)) {
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
                    EldFragment.IsStartReading = false;
                    IsPopupDismissed = true;
                }

                if (isCertifySignPending(false)) {
                    Global.DriverSwitchAlert(getActivity(), certifyTitle, titleDesc, okText);
                    resetCertifyLogDialogTitles();
                } else {
                    if (hMethods.CanChangeStatus(SLEEPER, driverLogArray, Global, false)) {

                        restartLocationService();

                        if (IsAOBRD && !IsAOBRDAutomatic) {
                            //  if (Global.isConnected(getActivity())) {
                            if (isPersonal.equals("true")) {
                                if (SharedPref.IsOdometerSaved(getActivity()) && !EldFragment.IsStartReading) {
                                    JobStatusInt = SLEEPER;
                                    OpenLocationDialog(SLEEPER, OldSelectedStatePos, false);
                                } else {
                                    AFTER_PERSONAL_JOB = PERSONAL_SLEEPER;
                                    if (EldFragment.IsStartReading) {
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
                            if(constants.isLocMalfunctionEvent(getActivity(), DriverType)){
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
                                            trailerDialog = new TrailorDialog(getActivity(), "trailor_driving", false, TrailorNumber, 0, false,
                                                    Global.onDutyRemarks, 0, dbHelper, new TrailorListener());
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
                    Global.EldScreenToast(OnDutyBtn, ConstantsEnum.CO_DRIVING_ALERT + coDriverStatus + ConstantsEnum.CO_DRIVING_ALERT1, getResources().getColor(R.color.colorVoilation));
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
            if (isCertifySignPending(false)) {
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
                        DisableJobViews();

                        // if Odometers are saving through OBD autmatically then makes all the condition false to ignore odometer save
                        if(SharedPref.IsOdometerFromOBD(getActivity())) {
                            EldFragment.IsStartReading = false;
                        }

                        // save status directly on button click. and uploaded automatically in background
                        if (!IsStartReading) {
                            SaveJobStatusAlert("Personal");
                        } else {
                            Global.EldScreenToast(OnDutyBtn, "Please enter End odometer Reading before Personal use.", getResources().getColor(R.color.colorVoilation));
                        }

                        EnableJobViews();



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
        if (isCertifySignPending(false)) {
            Global.DriverSwitchAlert(getActivity(), certifyTitle, titleDesc, okText);
            resetCertifyLogDialogTitles();
        } else {
            if (Global.TRAILOR_NUMBER.length() > 0) {

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
                trailerDialog = new TrailorDialog(getActivity(), "trailor_driving", false, TrailorNumber, 0, false,
                        Global.onDutyRemarks, 0, dbHelper, new TrailorListener());
                trailerDialog.show();

                // Global.EldScreenToast(OnDutyBtn, getResources().getString(R.string.update_trailer_no), getResources().getColor(R.color.colorVoilation));
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

            if(getActivity() != null) {
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
            if(getActivity() != null) {
                if (dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();

                dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, Global.GetCurrentDeviceDate(), new DateListener());
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
            if(getActivity() != null) {
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
                    EldFragment.IsStartReading = false;
                }

                if (SharedPref.IsOdometerSaved(getActivity()) && !EldFragment.IsStartReading) {
                    JobStatusInt = DRIVING;
                    OpenLocationDialog(DRIVING, OldSelectedStatePos, false);
                } else {
                    AFTER_PERSONAL_JOB = PERSONAL_DRIVING;

                    if (EldFragment.IsStartReading) {
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
            if(constants.isLocMalfunctionEvent(getActivity(), DriverType)){
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
        if (Global.TRUCK_NUMBER.trim().length() > 0) {
            IsPrePost = false;
            isPersonal = "false";
            SharedPref.OdometerSaved(false, getActivity());
            SharedPref.setVINNumber( VIN_NUMBER,  getActivity());
           // SetJobButtonView(DRIVING, isViolation, isPersonal);
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

            GetDriverLogData();
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
                EldFragment.IsStartReading = false;
                IsPopupDismissed = true;
            }

            if (isPersonal.equals("true")) {
                if (SharedPref.IsOdometerSaved(getActivity()) && !EldFragment.IsStartReading) {
                    JobStatusInt = ON_DUTY;
                    OpenLocationDialog(ON_DUTY, OldSelectedStatePos, false);
                } else {
                    AFTER_PERSONAL_JOB = PERSONAL_ON_DUTY;
                    if (EldFragment.IsStartReading) {
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
            if (constants.isLocMalfunctionEvent(getActivity(), DriverType)) {
                OpenLocationDialog(ON_DUTY, OldSelectedStatePos, true);
            } else {
                DisableJobViews();
                // save status directly on button click. and uploaded automatically in background
                SaveJobStatusAlert("On Duty");
            }
        }
    }



    void SaveOnDutyStatus() {

        if (Global.TRUCK_NUMBER.trim().length() > 0) {
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
                if(getActivity() != null) {

                        if (trailerDialog != null && trailerDialog.isShowing())
                            trailerDialog.dismiss();

                    trailerDialog = new TrailorDialog(getActivity(), "on_duty", isYardBtnClick, TrailorNumber,
                            0, false, Global.onDutyRemarks, 0, dbHelper, new TrailorListener());
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

        SharedPref.setVINNumber( VIN_NUMBER, getActivity());
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
        GetDriverLogData();
        CalculateTimeInOffLine(false, false);

        // After save locally push data to server
        pushStatusToServerAfterSave();


    }

    void SaveSleeperStatus() {
        isPersonal = "false";
        if (DRIVER_JOB_STATUS != SLEEPER) {
            if (Global.TRUCK_NUMBER.trim().length() > 0) {
                isPersonal = "false";
                IsPrePost = false;
                isViolation = false;
                SharedPref.SetIsReadViolation(false, getActivity());
                SharedPref.OdometerSaved(false, getActivity());
                SharedPref.setVINNumber(VIN_NUMBER,  getActivity());
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

                GetDriverLogData();
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
        SharedPref.setVINNumber(VIN_NUMBER,  getActivity());
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
        GetDriverLogData();
        CalculateTimeInOffLine(false, false);

        // After save locally push data to server
        pushStatusToServerAfterSave();

    }


    void OpenLocationDialog(int JobType, int OldSelectedStatePos, boolean isMalfunction) {

        isRemainingView = false;
        try {
            if(getActivity() != null) {
                if (StateArrayList.size() > 0) {
                    if (driverLocationDialog != null && driverLocationDialog.isShowing()) {
                        driverLocationDialog.dismiss();
                    }
                    driverLocationDialog = new DriverLocationDialog(getActivity(), City, State, OldSelectedStatePos, JobType, isMalfunction, OnDutyBtn,
                            StateArrayList, new DriverLocationListener());
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
                            YardMovePersonalStatusAlert(isYardMove, false);
                        }
                    }
                }else{
                    if(intent.hasExtra(ConstantsKeys.IsAutoStatusSaved)){
                        if(intent.getBooleanExtra(ConstantsKeys.IsAutoStatusSaved, false) == true){

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CalculateTimeInOffLine(false, false);
                                    oldStatusView = Integer.valueOf(DRIVER_JOB_STATUS);
                                }
                            }, 1000);

                            Global.DriverSwitchAlertWithDismiss(getActivity(), certifyTitle, titleDesc, "Ok",
                                    statusAlertDialog, true);

                        }else if(intent.getBooleanExtra(ConstantsKeys.ChangedToOthers, false) == true){
                            certifyTitle = "Duty Status Change Alert !!";
                            titleDesc = "Please change your status from Driving to other duty status due to vehicle is not moving.";
                            Global.DriverSwitchAlertWithDismiss(getActivity(), certifyTitle, titleDesc, "Ok",
                                    statusAlertDialog, false);
                        }
                    }
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


    private void GetDriverLogData() {

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
    }


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

        boolean isLogSavedInSyncTable = false;
        String statusStr = "";
        try {

            String address = "", wasViolation = "false", ViolationReason = "", DriverName = "";
            String currentUTCTime = Global.GetCurrentUTCTime();
            String CurrentDeviceDate = Global.GetCurrentDateTime();
            String currentUtcTimeDiffFormat = Global.GetCurrentUTCTimeFormat();
            DateTime currentDateTime = Global.getDateTimeObj(CurrentDeviceDate, false);    // Current Date Time
            DateTime currentUTCDateTime = Global.getDateTimeObj(Global.GetCurrentUTCTimeFormat(), true);

            getExceptionStatus();
            try {
                CurrentCycleId = hMethods.CheckStringNull(CurrentCycleId);
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
                    if (CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2)) {
                        AddressLine = csvReader.getShortestAddress(getActivity());
                    }else{
                        AddressLine = Globally.LATITUDE + "," + Globally.LONGITUDE;
                    }
                }
                // Check violation before save status
                if (driverStatus.equals(Global.DRIVING) || driverStatus.equals(Global.ON_DUTY)) {
                    JSONArray logArray = constants.AddNewStatusInList(
                            "", driverStatus, "", "no_address",
                            DRIVER_ID, City, State, Country, AddressLine, AddressLine,
                            CurrentCycleId, Reason, isPersonal, isViolation,
                            "false", String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                            String.valueOf(BackgroundLocationService.GpsVehicleSpeed), SharedPref.GetCurrentTruckPlateNo(getActivity()), "mannual_save", isYardBtnClick,
                            Global, SharedPref.get16hrHaulExcptn(getActivity()), false,
                            "" + isHaulExcptn,"",
                            LocationType, MalfunctionDefinition, IsNorthCanada,
                            hMethods, dbHelper);


                    String CurrentDate = Global.GetCurrentDateTime();
                    int rulesVersion = SharedPref.GetRulesVersion(getActivity());

                    List<DriverLog> oDriverLog = hMethods.GetLogAsList(logArray);
                    DriverDetail oDriverDetail1 = hMethods.getDriverList(new DateTime(CurrentDate), new DateTime(currentUtcTimeDiffFormat),
                            Integer.valueOf(DRIVER_ID), offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver,
                            DRIVER_JOB_STATUS, isOldRecord, isHaulExcptn,isAdverseExcptn, IsNorthCanada, rulesVersion, oDriverLog);
                    RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), Integer.valueOf(driverStatus),
                            oDriverDetail1);

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
                    address = City + ", " + State + ", " + Country;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isLocMalfunction = false;

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
                        Global.TRUCK_NUMBER,
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
                        "false", // IsStatusAutomatic is false when mannual job has been done
                        String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                        String.valueOf(BackgroundLocationService.GpsVehicleSpeed),
                        SharedPref.GetCurrentTruckPlateNo(getActivity()),
                        String.valueOf(isHaulExcptn),
                        "false", "mannual_save",
                        String.valueOf(isAdverseExcptn),
                        "", "", LocationType,
                        String.valueOf(IsNorthCanada),
                        CurrentDeviceDate,
                        String.valueOf(IsAOBRD),
                        CurrentCycleId,
                        String.valueOf(isDeferral), "", "false"

                );

                // Save Model in offline Array
                if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                    MainDriverPref.AddDriverLoc(getActivity(), locationModel);

                    /* ==== Add data in list to show in offline mode ============ */
                    EldDriverLogModel logModel = new EldDriverLogModel(Integer.valueOf(DriverStatusId), "startDateTime", "endDateTime", "totalHours",
                            "currentCycleId", false, currentUtcTimeDiffFormat, currentUtcTimeDiffFormat,
                            "", "", AddressLine, "", Boolean.parseBoolean(isPersonal),
                            isAdverseExcptn, isHaulExcptn);
                    eldSharedPref.AddDriverLoc(getActivity(), logModel);
                } else {
                    CoDriverPref.AddDriverLoc(getActivity(), locationModel);

                    /* ==== Add data in list to show in offline mode ============ */
                    EldDriverLogModel logModel = new EldDriverLogModel(Integer.valueOf(DriverStatusId), "startDateTime", "endDateTime", "totalHours",
                            "currentCycleId", false, currentUtcTimeDiffFormat, currentUtcTimeDiffFormat,
                            "", "", AddressLine, "", Boolean.parseBoolean(isPersonal),
                            isAdverseExcptn, isHaulExcptn);
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

                Log.d("savedSyncedArray", "savedSyncedArray: " +savedSyncedArray);

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
                        String.valueOf(BackgroundLocationService.GpsVehicleSpeed),
                        SharedPref.GetCurrentTruckPlateNo(getActivity()), "mannual_save", isYardBtnClick,
                        Global, isHaulExcptn, false, "" + isAdverseExcptn,"",
                        LocationType, MalfunctionDefinition, IsNorthCanada, hMethods, dbHelper);

                /* ---------------- DB Helper operations (Insert/Update) --------------- */
                hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, driverLogArray);
                Reason = "";
                BackgroundLocationService.IsAutoChange = false;
                SharedPref.SetTruckIgnitionStatusForContinue("ON", "home", Global.getCurrentDate(), getActivity());

                // get Obd odometer Data
                if (!IsAOBRD || IsAOBRDAutomatic) {
                    int lastJobStatus = hMethods.getSecondLastJobStatus(driverLogArray);
                    int currentJobStatus = Integer.valueOf(DriverStatusId);

                    if ((currentJobStatus == ON_DUTY || currentJobStatus == DRIVING) &&
                            (lastJobStatus == OFF_DUTY || lastJobStatus == SLEEPER)) {

                        if (wifiConfig.IsAlsNetworkConnected(getActivity())) {
                            tcpClient.sendMessage("123456,can");
                        } else {
                            checkWiredObdConnectionWithOdometer();
                        }

                    } else {

                        if (currentJobStatus == OFF_DUTY || currentJobStatus == SLEEPER) {
                            if (lastJobStatus == DRIVING || lastJobStatus == ON_DUTY) {
                                if (wifiConfig.IsAlsNetworkConnected(getActivity())) {
                                    tcpClient.sendMessage("123456,can");
                                } else {
                                    checkWiredObdConnectionWithOdometer();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

                // ============================ Save Job Status in SQLite ==============================
                driverLogArray = constants.AddNewStatusInList(DriverName, DriverStatusId, ViolationReason, address,
                        DRIVER_ID, City, State, Country, AddressLine, AddressLine,
                        CurrentCycleId, Reason, isPersonal, isViolation,
                        "false", String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                        String.valueOf(BackgroundLocationService.GpsVehicleSpeed),
                        SharedPref.GetCurrentTruckPlateNo(getActivity()), "mannual_save", isYardBtnClick,
                        Global, isHaulExcptn, false,"" + isAdverseExcptn,"",
                        LocationType, MalfunctionDefinition, IsNorthCanada, hMethods, dbHelper);

                /* ---------------- DB Helper operations (Insert/Update) --------------- */
                hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, driverLogArray);

                // save driver status manual input in log
                constants.saveAppUsageLog(statusStr + " status not saved in 18 days array"  + ". reason behind: " +e.toString(),
                        false, false, obdUtil);
            }

            // save driver status manual input in log
          /*  constants.saveAppUsageLog("Status changed to " + statusStr + ", SyncLogEntry=" +isLogSavedInSyncTable,
                    false, false, obdUtil);
*/
        }  catch (Exception e){
            e.printStackTrace();
            // save driver status manual input in log
            constants.saveAppUsageLog("Exception occured when changed to " + statusStr + ", SyncLogEntry=" +isLogSavedInSyncTable,
                    false, false, obdUtil);

        }


        // Save odometer
        // save app display status log
        if(SharedPref.IsOdometerFromOBD(getActivity())) {
            constants.saveOdometer(DriverStatusId, DRIVER_ID, DeviceId, driverLogArray,
                    odometerhMethod, hMethods, dbHelper, getActivity());
        }
        SharedPref.setDrivingAllowedStatus(true, "", getActivity());
        AddressLine = "";

        oldStatusView = Integer.valueOf(driverStatus);
        if (driverStatus.equals(Global.ON_DUTY)){
            setYardMoveView();
        }
        Log.d("Saved Status Service", "--- DriverStatusId: "+DriverStatusId);
    }


    private void checkWiredObdConnectionWithOdometer(){
        if(SharedPref.GetTruckIgnitionStatusForContinue(constants.IgnitionSource, getActivity()).equals(constants.WiredOBD)) {
            DateTime lastRecordSavedTime = Global.getDateTimeObj(SharedPref.GetTruckIgnitionStatusForContinue(constants.LastIgnitionTime,
                    getActivity()), false);
            DateTime currentDate = Global.getDateTimeObj(Global.getCurrentDate(), false);

            int dayDiff = Days.daysBetween(lastRecordSavedTime.toLocalDate(), currentDate.toLocalDate()).getDays();

            if(dayDiff == 0){
                int minDiff = currentDate.getMinuteOfDay() - lastRecordSavedTime.getMinuteOfDay();

                if(minDiff < 5 ){
                    String HighResolutionDistance = SharedPref.getHighPrecisionOdometer(getActivity());
                    odometerhMethod.AddOdometerAutomatically(DRIVER_ID, DeviceId, HighResolutionDistance, DriverStatusId, dbHelper, getActivity());

                }
            }

        }
    }

    // ------------- Check Certify signature status -------------
    public boolean isCertifySignPending(boolean isOnCreate ){
        DateTime currentDateTime          = Global.getDateTimeObj(Global.GetCurrentDateTime(), false);    // Current Date Time
        DateTime currentUTCTime           = Global.getDateTimeObj(Global.GetCurrentUTCTimeFormat(), true);
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


        //isSignPending = true; // for test. need to delete --------------------------------------


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

                }
            }else{
                if(isSignPending && isOnCreate){
                    certifyLogAlert(title, titleMsg);
                }
            }
        }

        return isCertifySignaturePending;
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
                        isCertifySignPending(false ), isFragmentAdd, fragManager, driverLogArray.toString());
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
        public void CancelBtnReady(String TruckIgnitionStatus) {
            SharedPref.SetTruckStartLoginStatus(false, getActivity());
            SharedPref.SetTruckIgnitionStatusForContinue(TruckIgnitionStatus, "home", Global.getCurrentDate(), getActivity());

            if (Globally.VEHICLE_SPEED < 10) {
                SaveOffDutyStatus();
            } else {
                SaveDrivingStatus();
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
                                SharedPref.setVINNumber(VIN_NUMBER, getActivity());
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

                                        GetDriverCycle(DriverType);
                                    }

                                }

                                Global.SaveCurrentCycle(DriverType, Country, "border_crossing", getActivity());
                                SetDataInView();


                          /*      if (Global.isConnected(getActivity())) {
                                    saveInfo(Global.ON_DUTY, true, true, false);
                                } else {*/

                                    SaveDriverJob(Global.ON_DUTY);
                                    EnableJobViews();

                                    Global.EldScreenToast(OnDutyBtn, "You are now ON DUTY but not driving.", getResources().getColor(R.color.colorPrimary));

                                    GetDriverLogData();
                                    initilizeEldView.ShowActiveJobView(DRIVER_JOB_STATUS, isPersonal, jobTypeTxtVw, perDayTxtVw, remainingLay,
                                            usedHourLay, jobTimeTxtVw, jobTimeRemngTxtVw);
                                    SetJobButtonView(DRIVER_JOB_STATUS, isViolation, isPersonal);
                                    CalculateTimeInOffLine(false, false);

                                // After save locally push data to server
                                pushStatusToServerAfterSave();

                                //  }

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

                                    // call lat long API
                                    IsAddressUpdate = true;
                                    GetAddFromLatLng();

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

                    if (Global.isConnected(getActivity())) {
                        TrailorNumber = TrailorNo.trim();

                        if (TrailorNumber.equalsIgnoreCase("null"))
                            TrailorNumber = "";

                        // SaveTrailerLocally(TrailorNo);

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

                        SaveTrailerNumber(DRIVER_ID, DeviceId, TrailorNumber);
                    } else {
                        Global.EldScreenToast(ReasonEditText, Globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorSleeper));
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
        public void ChangeVehicleReady(String Title, int position) {

            if (!Title.equals(ConstantsEnum.StrTruckAttachedTxt)) {
                if (Global.isConnected(getActivity())) {
                    LoginTruckChange = "false";
                    VehicleBtnClick(Title, position);

                    try {
                        if (vehicleDialog != null && vehicleDialog.isShowing())
                            vehicleDialog.dismiss();
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

    }

    private class VehicleLoginListener implements VehicleDialogLogin.VehicleLoginListener {
        @Override
        public void ChangeVehicleReady(String Title, int position, View view) {

            try{
                if(getActivity() != null){
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

        @Override
        public void ContinueBtnReady(boolean isContinue, final String CoDriverId, final String CompanyId, int position) {
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

                if(isExemptDriver == false) {
                    // save malfunction occur event to server with few inputs
                    JSONObject newOccuredEventObj = malfunctionDiagnosticMethod.GetJsonFromList(
                            DRIVER_ID, DeviceId, SharedPref.getVINNumber(getActivity()),
                            DriverConst.GetDriverTripDetails(DriverConst.Truck, getActivity()),
                            DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity()),
                            SharedPref.getObdEngineHours(getActivity()),
                            SharedPref.getHighPrecisionOdometer(getActivity()),
                            SharedPref.getHighPrecisionOdometer(getActivity()),
                            Global.GetCurrentDateTime(), constants.MissingDataElementDiagnostic,
                            getString(R.string.ignore_to_save_loc) + " " + Global.JobStatus(DRIVER_JOB_STATUS, Boolean.parseBoolean(isPersonal)));


                    // save Occurred Mal/Dia events locally to get details later for clear them
                    JSONArray malArrayEvent = malfunctionDiagnosticMethod.getSavedMalDiagstcArrayEvents(Integer.parseInt(DRIVER_ID), dbHelper);
                    malArrayEvent.put(newOccuredEventObj);
                    malfunctionDiagnosticMethod.MalfnDiagnstcLogHelperEvents(Integer.parseInt(DRIVER_ID), dbHelper, malArrayEvent);

                    // save Occurred event locally until not posted to server
                    JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(Integer.parseInt(DRIVER_ID), dbHelper);
                    malArray.put(newOccuredEventObj);
                    malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(Integer.parseInt(DRIVER_ID), dbHelper, malArray);

                    MalfunctionDefinition = constants.ConstLocationMissing;

                    Global.ShowLocalNotification(getActivity(),
                                                    getResources().getString(R.string.missing_data_element),
                                                    getResources().getString(R.string.missing_event_occured_desc), 2091);


                }

                isLocMalfunction = false;
                LocationType = SharedPref.getLocMalfunctionType(getActivity());
                saveInAobrdMalfnModeStatus(JobType);

            }
        }

        @Override
        public void SaveLocReady(int position, int spinnerItemPos, int JobType, String city, EditText CityNameEditText,
                                 View view, boolean isMalfunction) {

            City = city;
            OldSelectedStatePos = spinnerItemPos;
            LocationType = "";

            if (spinnerItemPos < StateList.size()) {
                State = StateList.get(spinnerItemPos).getState();
                Country = StateList.get(spinnerItemPos).getCountry();
            }

            if (City.length() > 0) {

                if(isMalfunction && SharedPref.getLocMalfunctionType( getContext()).equals("x")){
                    isLocMalfunction = constants.isLocMalfunctionEvent(getActivity(), DriverType);
                    LocationType = "m";
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


    void SaveTrailerLocally(String trailerNumber) {
        if(trailerNumber.equalsIgnoreCase("null"))
            trailerNumber = "";

        Log.d("trailerNumber", "trailerNumber: " + trailerNumber);

        Global.TRAILOR_NUMBER = trailerNumber;
        TrailorNumber = trailerNumber;
        SharedPref.setTrailorNumber(trailerNumber, getActivity());
    }


    void saveInAobrdMalfnModeStatus(int JobType){
        switch (JobType) {

            case OFF_DUTY:
                Global.SaveCurrentCycle(DriverType, Country, "", getActivity());
                SetDataInView();
                SaveOffDutyStatus();
                break;

            case SLEEPER:
                Global.SaveCurrentCycle(DriverType, Country, "", getActivity());
                SetDataInView();

                SaveSleeperStatus();
                break;

            case DRIVING:

                Global.SaveCurrentCycle(DriverType, Country, "", getActivity());
                SetDataInView();
                isPersonal = "false";
                SaveDrivingStatus();

                break;


            case ON_DUTY:

                SaveOnDutyStatus();
                break;

            case PERSONAL:
                SavePersonalStatus();
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
            driverLogArray = new JSONArray();
            driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
            driverLogArray = new JSONArray();
        }

        // get driver's current cycle id
        GetDriverCycle(DriverType);
        isSingleDriver = Global.isSingleDriver(getActivity());
        isYardMove = false;

        getExceptionStatus();

        if(IsCheckOffSleeperTime) {
            int rulesVersion = SharedPref.GetRulesVersion(getActivity());
            getDailyOffLeftMinObj = hMethods.getDailyOffLeftMinutes(currentDateTime, currentUTCTime, offsetFromUTC,
                    Integer.valueOf(CurrentCycleId), isSingleDriver, Integer.valueOf(DRIVER_ID), LATEST_JOB_STATUS, isOldRecord,
                    isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                    rulesVersion, dbHelper);

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

                //Log.d("DriverStatusId", "DriverStatusId: " +SharedPref.getDriverStatusId(getActivity()));
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
                        if( Global.isConnected(getActivity()) && DriverJsonArray.length() == 0 ) {
                            GetDriverLog18Days(DRIVER_ID, GetDriverLog18Days);
                        }else{
                            if(isEldRuleAlreadyCalled == false) {
                                CalculateTimeInOffLine(false, true);
                            }
                            isEldRuleAlreadyCalled = true;
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

                        if (SharedPref.GetNewLoginStatus(getActivity())) {
                            callEldRuleMethod(currentDayArray, currentDateTime, currentUTCTime, onResume);
                        }else{
                            boolean isPopupShown = SharedPref.GetTruckStartLoginStatus(getActivity());
                            isYardMove = lastJsonItem.getBoolean(ConstantsKeys.YardMove);

                            if (isPopupShown && ( isPersonal.equals("true") || (DRIVER_JOB_STATUS == ON_DUTY && isYardMove) ) ) {

                                YardMovePersonalStatusAlert(isYardMove, true);
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

        TotalOffDutyHoursInt        = hMethods.GetOffDutyTime(currentDayArray);
        TotalSleeperBerthHoursInt   = hMethods.GetSleeperTime(currentDayArray);

        CurrentCycleId              = hMethods.CheckStringNull(CurrentCycleId);
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
        if(!CurrentCycleId.equals("") && !CurrentCycleId.equals("-1")) {

            int rulesVersion = SharedPref.GetRulesVersion(getActivity());
            oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DRIVER_ID),
                    offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver, DRIVER_JOB_STATUS, isOldRecord,
                    isHaulExcptn, isAdverseExcptn, IsNorthCanada,
                    rulesVersion, oDriverLogDetail);

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
                        rulesVersion, dbHelper);

                LeftWeekOnDutyHoursInt = (int) RulesObj.getCycleRemainingMinutes();
                TotalOnDutyHoursInt = (int) RemainingTimeObj.getOnDutyUsedMinutes();   // hMethods.GetOnDutyTime(currentDayArray);
                TotalDrivingHoursInt = (int) RemainingTimeObj.getDrivingUsedMinutes();  // hMethods.GetDrivingTime(currentDayArray);
                LeftDayOnDutyHoursInt = (int) RemainingTimeObj.getOnDutyRemainingMinutes();
                LeftDayDrivingHoursInt = (int) RemainingTimeObj.getDrivingRemainingMinutes();

                shiftRemainingMinutes = (int) RemainingTimeObj.getShiftRemainingMinutes();
                shiftUsedMinutes = (int) RemainingTimeObj.getShiftUsedMinutes();

                minOffDutyUsedHours = (int) RemainingTimeObj.getMinimumOffDutyUsedHours();
                isMinOffDutyHoursSatisfied = RemainingTimeObj.isMinimumOffDutyHoursSatisfied();

                boolean isDisableAdverseException = RemainingTimeObj.isDisableAdverseException();
                boolean isDisableShortHaul = RemainingTimeObj.isDisableShortHaul();

                if (isDisableAdverseException) {
                    if (isAdverseExcptn) {
                        if (DriverType == Constants.MAIN_DRIVER_TYPE)
                            SharedPref.setAdverseExcptn(false, getActivity());
                        else
                            SharedPref.setAdverseExcptnCo(false, getActivity());

                        hMethods.SaveDriversJob(DRIVER_ID, DeviceId, "", getString(R.string.disable_adverse_exception),
                                LocationType, "", false, IsNorthCanada, DriverType, constants,
                                MainDriverPref, CoDriverPref, eldSharedPref, coEldSharedPref,
                                syncingMethod, Global, hMethods, dbHelper, getActivity());
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
                                syncingMethod, Global, hMethods, dbHelper, getActivity());
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

        GetDriverLogData();

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
        String TotalSleeper     = Global.FinalValue(TotalSleeperBerthHoursInt);
        String TotalOffDuty     = Global.FinalValue(TotalOffDutyHoursInt);
        String TotalOnDuty      = Global.FinalValue(TotalOnDutyHoursInt);
        String TotalDriving     = Global.FinalValue(TotalDrivingHoursInt);
        String LeftOnDuty       = Global.FinalValue(LeftDayOnDutyHoursInt);
        String LeftDriving      = Global.FinalValue(LeftDayDrivingHoursInt);
        String LeftCycleTime    = Global.FinalValue(LeftWeekOnDutyHoursInt);

        onDutyTimeTxtVw.setText("U " + TotalOnDuty + " R " + LeftOnDuty);
        drivingTimeTxtVw.setText("U " + TotalDriving + " R " + LeftDriving);
        sleeperTimeTxtVw.setText("U " + TotalSleeper);
        offDutyTimeTxtVw.setText("U " + TotalOffDuty);

        WeeklyRemainingTime     = LeftCycleTime;
        DrivingRemainingTime    = LeftDriving;
        OnDutyRemainingTime     = LeftOnDuty;


        if (DRIVER_JOB_STATUS == 3) {
            jobTimeTxtVw.setText(TotalDriving);
            jobTimeRemngTxtVw.setText(LeftDriving);

            if (isViolation && ViolatioMsg.length() > 0) {
                if (!ViolationsReason.equals(ViolatioMsg) || SharedPref.IsReadViolation(getActivity()) == false) {
                    SharedPref.SetIsReadViolation(false, getActivity());
                    Global.SnackBarViolation(StatusMainView, ViolatioMsg, getActivity());
                }
            }

        } else if (DRIVER_JOB_STATUS == 4) {
            jobTimeTxtVw.setText(TotalOnDuty);
            jobTimeRemngTxtVw.setText(LeftOnDuty);

            if (isViolation && ViolatioMsg.length() > 0) {
                if (!ViolationsReason.equals(ViolatioMsg) || SharedPref.IsReadViolation(getActivity()) == false) {
                    SharedPref.SetIsReadViolation(false, getActivity());
                    Global.SnackBarViolation(StatusMainView, ViolatioMsg, getActivity());
                }
            }

        }  else if (DRIVER_JOB_STATUS == 2) {
            jobTimeTxtVw.setText(TotalSleeper);
        }else {
            jobTimeTxtVw.setText(TotalOffDuty);
        }
        remainingTimeTopTV.setText(Html.fromHtml("Cycle time left <b>" + LeftCycleTime + "</b>"));
        if (CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) && SharedPref.IsNorthCanada(getActivity())) {
            currentCycleTxtView.setText(CurrentCycle + " (N)");
        }else{
            currentCycleTxtView.setText(CurrentCycle);
        }

        if(CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)){
            cyleFlagImgView.setImageResource(R.drawable.can_flag);
        }else{
            cyleFlagImgView.setImageResource(R.drawable.usa_flag);
        }
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
                            SharedPref.setVINNumber(VIN_NUMBER, getActivity());
                            Global.EldScreenToast(OnDutyBtn, msg, getResources().getColor(R.color.color_eld_theme));
                            Global.OdometerDialog(getActivity(), "Please Enter Odometer Reading.", true, PERSONAL, personalUseBtn, alertDialog);

                            // save app display status log
                            if (SharedPref.IsOdometerFromOBD(getActivity())) {
                                constants.saveAppUsageLog(ConstantsEnum.StatusPuAuto, true, false, obdUtil);
                            } else {
                                constants.saveAppUsageLog(ConstantsEnum.StatusPuMannual, true, true, obdUtil);
                            }
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


    /* ================== Save Driver Details =================== */
    void SAVE_DRIVER_STATUS(final JSONArray geoData, final boolean isLoad, final boolean IsRecap, int socketTimeout) {
        SaveRequestCount++;
        if (isLoad ) {
            progressD.show();
        }
        IsSaveOperationInProgress = true;
        String SavedLogApi = "";
        if(SharedPref.IsEditedData(getActivity())){
            SavedLogApi = APIs.SAVE_DRIVER_EDIT_LOG_NEW;
        }else{
            SavedLogApi = APIs.SAVE_DRIVER_STATUS;
        }

        saveDriverLogPost.PostDriverLogData(geoData, SavedLogApi, socketTimeout, isLoad, IsRecap, DriverType, MainDriverLog);

    }


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


    /*================== Update OBD Assigned Vehicles ===================*/
    void UpdateOBDAssignedVehicle(final String DriverId, final String CoDriverId, final String DeviceId, final String PreviousDeviceMappingId,
                                  final String DeviceMappingId, final String VehicleId, final String EquipmentNumber,
                                  final String PlateNumber, final String VIN, final String CompanyId, final String IMEINumber,
                                  final String LoginTruckChange) {

       /* try {
            constants.saveUpdateVehDetails(DriverId, PreviousDeviceMappingId, DeviceMappingId,  VehicleId,
                    EquipmentNumber, PlateNumber, VIN, CompanyId, IMEINumber, obdUtil);
             obdUtil.syncObdSingleLog(getActivity(), DriverId, MainDriverName, 1);

        }catch (Exception e){
            e.printStackTrace();
        }*/

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

        if(!IsAddressUpdate) {
            if (JobStatusInt != 101) {
                int OldSelectedStateListPos = -1;
                OpenLocationDialog(JobStatusInt, OldSelectedStateListPos, false);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }

            DisableJobViews();
        }

        Globally.LONGITUDE = Global.CheckLongitudeWithCycle(Globally.LONGITUDE);

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.Latitude, Globally.LATITUDE);
        params.put(ConstantsKeys.Longitude, Globally.LONGITUDE);
        params.put(ConstantsKeys.IsAOBRDAutomatic, String.valueOf(IsAOBRDAutomatic));

        GetAddFromLatLngRequest.executeRequest(Request.Method.POST, APIs.GET_Add_FROM_LAT_LNG, params, GetAddFromLatLng,
                Constants.SocketTimeout3Sec, ResponseCallBack, ErrorCallBack);

    }



    //*================== Get Odometer Reading ===================*//*
    void GetOdometerReading(final String DriverId, final String DeviceId, final String VIN, final String CreatedDate) {

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.VIN, VIN);
        params.put(ConstantsKeys.CreatedDate, CreatedDate);
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
                        saveInfo("", false, false, false);
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


    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag) {

            switch (flag) {

                case MainDriverLog:
                  //  constants.ClearNotifications(getActivity() );
                    Log.d("Response ", "---Response Save: " + response);
                    IsSaveOperationInProgress = false;
                    progressD.dismiss();
                    EnableJobViews();

                    JSONObject obj;

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
                                if (DriverJsonArray.length() == 1) {
                                    ClearLogAfterSuccess(isLoad, IsRecap);
                                } else {
                                    try {
                                        int LastStatus = 0, SecLastStatus = 0;
                                        JSONObject lastObj = constants.GetJsonFromList(DriverJsonArray, DriverJsonArray.length() - 1);
                                        JSONObject secLastObj = constants.GetJsonFromList(DriverJsonArray, DriverJsonArray.length() - 2);
                                        LastStatus = lastObj.getInt("DriverStatusId");
                                        SecLastStatus = secLastObj.getInt("DriverStatusId");

                                        if (LastStatus == SecLastStatus) {
                                            ClearLogAfterSuccess(isLoad, IsRecap);

                                        } else {
                                            if (SaveRequestCount < 3) {
                                                saveInfo("", false, false, false);
                                            } else {
                                                ClearLogAfterSuccess(isLoad, IsRecap);
                                            }
                                        }

                                        SaveCoDriverData(isLoad, IsRecap);
                                    } catch (Exception e) {
                                        ClearLogAfterSuccess(isLoad, IsRecap);
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
                status = obj.getString("Status");
                Message = obj.getString("Message");

                if (!obj.isNull("Data")) {
                    dataObj = new JSONObject(obj.getString("Data"));
                }

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
                            if(getActivity() != null) {
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

                            if(onDutyRemarkList.size() > 1){
                                if(onDutyRemarkList.get(0).equals("Border Crossing") && onDutyRemarkList.get(1).equals("Brake Check")){
                                    onDutyRemarkList.set(0, "Brake Check");
                                    onDutyRemarkList.set(1, "Border Crossing");
                                    Global.onDutyRemarks.set(0, "Brake Check");
                                    Global.onDutyRemarks.set(1, "Border Crossing");
                                }
                            }
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

                    case GetObdAssignedVeh:
                        try {
                            if(progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            TabAct.vehicleList = new ArrayList<VehicleModel>();
                            JSONArray vehicleJsonArray = new JSONArray(obj.getString("Data"));

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
                                Toast.makeText(getActivity(), "OBD Vehicles66: " +e.toString(), Toast.LENGTH_LONG).show();
                                constants.saveAppUsageLog("OBD Vehicles66: " +e.toString(), false, false, obdUtil);
                                e.printStackTrace();
                            }

                            if (SharedPref.GetNewLoginStatus(getActivity())) {
                                try {
                                    if (vehicleDialogLogin != null && vehicleDialogLogin.isShowing())
                                        vehicleDialogLogin.dismiss();

                                    vehicleDialogLogin = new VehicleDialogLogin(getActivity(), getActivity(), Global.TRUCK_NUMBER, TabAct.vehicleList, new VehicleLoginListener());
                                    vehicleDialogLogin.show();
                                } catch (final IllegalArgumentException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "OBD Vehicles55: " +e.toString(), Toast.LENGTH_LONG).show();
                                    constants.saveAppUsageLog("OBD Vehicles55: " +e.toString(), false, false, obdUtil);
                                } catch (final Exception e) {
                                    Toast.makeText(getActivity(), "OBD Vehicles44: " +e.toString(), Toast.LENGTH_LONG).show();
                                    constants.saveAppUsageLog("OBD Vehicles44: " +e.toString(), false, false, obdUtil);
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if (IsVehicleDialogShown) {
                                        if (TabAct.vehicleList.size() > 0) {
                                                if (vehicleDialog != null && vehicleDialog.isShowing()) {
                                                    vehicleDialog.dismiss();
                                                }

                                                vehicleDialog = new VehicleDialog(getActivity(), Global.TRUCK_NUMBER, TabAct.vehicleList, new VehicleListener());
                                                vehicleDialog.show();

                                        } else {
                                            Global.EldScreenToast(OnDutyBtn, "No vehicles are available.", getResources().getColor(R.color.colorVoilation));
                                        }
                                    }
                                } catch (final IllegalArgumentException e) {
                                     e.printStackTrace();
                                    Toast.makeText(getActivity(), "OBD Vehicles33: " +e.toString(), Toast.LENGTH_LONG).show();
                                    constants.saveAppUsageLog("OBD Vehicles33: " +e.toString(), false, false, obdUtil);
                                } catch (final Exception e) {
                                    Toast.makeText(getActivity(), "OBD Vehicles22: " +e.toString(), Toast.LENGTH_LONG).show();
                                    constants.saveAppUsageLog("OBD Vehicles22: " +e.toString(), false, false, obdUtil);
                                     e.printStackTrace();
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "OBD Vehicles11: " +e.toString(), Toast.LENGTH_LONG).show();
                            constants.saveAppUsageLog("OBD Vehicles11: " +e.toString(), false, false, obdUtil);
                            Global.EldScreenToast(OnDutyBtn, "vehicles list error", getResources().getColor(R.color.colorVoilation));
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

                            if(vehicleJsonObj.has("OdometerFromOBD")){
                                isOdometerFromOBD = vehicleJsonObj.getBoolean("OdometerFromOBD");
                            }

                            if(vehicleJsonObj.has("PlateNumber")){
                                plateNo = vehicleJsonObj.getString("PlateNumber");
                            }

                            SharedPref.SetIsAOBRD(IsAOBRD, getActivity());
                            SharedPref.SetAOBRDAutomatic(IsAOBRDAutomatic, getActivity());
                            SharedPref.SetAOBRDAutoDrive(vehicleJsonObj.getBoolean("IsAutoDriving"), getActivity());
                            SharedPref.SetOdometerFromOBD(isOdometerFromOBD, getActivity());
                            SharedPref.setCurrentTruckPlateNo(plateNo, getActivity());

                            setTitleView(isOdometerFromOBD, isExemptDriver);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        // if(IsObdVehShown)
                        if (TabAct.vehicleList.size() > 0) {
                            VIN_NUMBER = TabAct.vehicleList.get(TruckListPosition).getVIN();
                            Global.TRUCK_NUMBER = TabAct.vehicleList.get(TruckListPosition).getEquipmentNumber();
                            VehicleId = TabAct.vehicleList.get(TruckListPosition).getVehicleId();
                            tractorTv.setText(Global.TRUCK_NUMBER);
                            SharedPref.setVehicleId(VehicleId, getActivity());
                        }
                        SharedPref.setVINNumber(VIN_NUMBER,  getActivity());


                        if (SharedPref.GetNewLoginStatus(getActivity())) {
                            Constants.isEldHome = true;

                            SharedPref.SetNewLoginStatus(false, getActivity());

                            String updatePopupDate = SharedPref.GetUpdateAppDialogTime(getActivity());
                            if(!updatePopupDate.equals(SelectedDate)){
                                TabAct.openUpdateDialogBtn.performClick();
                            }

                            try {
                                if (progressDialog != null)
                                    progressDialog.dismiss();

                                if (vehicleDialogLogin != null && vehicleDialogLogin.isShowing())
                                    vehicleDialogLogin.dismiss();
                            } catch (final IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }

                            if(getActivity() != null) {
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
                        } else {

                            Global.EldScreenToast(OnDutyBtn, "Updated successfully.", getResources().getColor(R.color.colorPrimary));
                        }

                        IsTruckChange = true;
                        GetDriverStatusPermission(DRIVER_ID, DeviceId, VehicleId);

                        if (SharedPref.getDriverType(getContext()).equals(DriverConst.TeamDriver)) {
                            /*Save Trip Details */
                            Constants.SaveTripDetails(0, Global.TRUCK_NUMBER, VIN_NUMBER, getActivity());
                            Constants.SaveTripDetails(1, Global.TRUCK_NUMBER, VIN_NUMBER, getActivity());
                        } else {
                            Constants.SaveTripDetails(DriverType, Global.TRUCK_NUMBER, VIN_NUMBER, getActivity());
                        }


                        break;


                    case SendLog:
                        Global.EldScreenToast(OnDutyBtn, "Sent Successfully.", getResources().getColor(R.color.colorPrimary));
                        break;

                    case GetOdometer:
                        if (dataObj != null) {
                            try {
                                if (!dataObj.getString("StartOdometer").equals("null") &&
                                        dataObj.getString("EndOdometer").equals("null")) {
                                    EldFragment.IsStartReading = true;
                                } else {
                                    EldFragment.IsStartReading = false;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case SaveTrailer:

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

                                if(dataJObject.has(ConstantsKeys.IsOdoCalculationAllowed) && !dataJObject.isNull(ConstantsKeys.IsOdoCalculationAllowed)){
                                    SharedPref.SetOdoCalculationAllowed(dataJObject.getBoolean(ConstantsKeys.IsOdoCalculationAllowed), getActivity());
                                }

                                int ObdPreference = Constants.OBD_PREF_WIFI;
                                if(dataJObject.has(ConstantsKeys.ObdPreference) && !dataJObject.isNull(ConstantsKeys.ObdPreference) ) {
                                    ObdPreference = dataJObject.getInt(ConstantsKeys.ObdPreference);
                                    int savedObdPref = SharedPref.getObdPreference(getActivity());

                                    if(ObdPreference != savedObdPref){
                                        if(ObdPreference == Constants.OBD_PREF_BLE){
                                            SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, Global.getCurrentDate(), getActivity());
                                        }else if(ObdPreference == Constants.OBD_PREF_WIRED){
                                            SharedPref.SaveObdStatus(Constants.WIRED_DISCONNECTED, Global.getCurrentDate(), getActivity());
                                        }else{
                                            SharedPref.SaveObdStatus(Constants.WIFI_DISCONNECTED, Global.getCurrentDate(), getActivity());
                                        }
                                        constants.saveAppUsageLog("ObdPreference: From " + savedObdPref + " to " + ObdPreference, false, false, obdUtil);

                                    }

                                    SharedPref.SetObdPreference(ObdPreference, getActivity());

                                    if(ObdPreference != savedObdPref){
                                        startService();     // call onStartCommand in service to connect with current OBD pref
                                    }

                                }else{
                                    SharedPref.SetObdPreference(Constants.OBD_PREF_WIFI, getActivity());
                                }

                                setObdStatus(false);

                                if(IsTruckChange){
                                    if(ObdPreference == Constants.OBD_PREF_BLE && SharedPref.getObdStatus(getActivity()) == Constants.BLE_CONNECTED){
                                        // set obd disconnection time is current time for diagnostic/malfunction event.
                                        SharedPref.SaveObdStatus(Constants.BLE_DISCONNECTED, Global.getCurrentDate(), getActivity());
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


                                if(DRIVER_ID.equals(MainDriverId)) {    // Update permissions for main driver
                                    SharedPref.SetCertifcnUnIdenfdSettings(IsAllowLogReCertification, IsShowUnidentifiedRecords, IsPersonal, IsYardMove, context);
                                    SharedPref.SetDiagnosticAndMalfunctionSettingsMain(IsAllowMalfunction, IsAllowDiagnostic, IsClearMalfunction, IsClearDiagnostic, context);
                                    SharedPref.SetExemptDriverStatusMain(isExemptDriver, getActivity());
                                    SharedPref.SetCycleRequestStatusMain(IsCycleRequest, getActivity());

                                    boolean isMal = SharedPref.isMalfunctionOccur(getActivity());
                                    boolean isDia = SharedPref.isDiagnosticOccur(getActivity());
                                    SharedPref.setEldOccurences(IsUnidentified,
                                            isMal, isDia,
                                            SharedPref.isSuggestedEditOccur(getActivity()), getActivity());


                                }else{                                  // Update permissions for Co driver
                                    SharedPref.SetCertifcnUnIdenfdSettingsCo(IsAllowLogReCertification, IsShowUnidentifiedRecords, IsPersonal, IsYardMove, context);
                                    SharedPref.SetDiagnosticAndMalfunctionSettingsCo(IsAllowMalfunction, IsAllowDiagnostic, IsClearMalfunction, IsClearDiagnostic, context);
                                    SharedPref.SetExemptDriverStatusCo(isExemptDriver, getActivity());
                                    SharedPref.SetCycleRequestStatusCo(IsCycleRequest, getActivity());

                                    SharedPref.setEldOccurencesCo(IsUnidentified,
                                            SharedPref.isMalfunctionOccurCo(getActivity()) ,
                                            SharedPref.isDiagnosticOccurCo(getActivity()),
                                            SharedPref.isSuggestedEditOccurCo(getActivity()), getActivity());

                                }

                                setExceptionView();

                                if(IsELDNotification){

                                    try {
                                        if (eldNotificationDialog != null && eldNotificationDialog.isShowing()) {
                                            eldNotificationDialog.dismiss();
                                        }
                                        eldNotificationDialog = new EldNotificationDialog(getActivity(), ELDNotification, false );
                                        eldNotificationDialog.show();

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

                            if(newsNotificationList.size() > 0 && getActivity() != null) {
                                NotificationNewsDialog newsDialog = new NotificationNewsDialog(getActivity(), newsNotificationList);
                                newsDialog.show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;


                    case NotReady:
                        Log.d("NotReady","NotReady: " + response);
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

                                AddressLine = csvReader.getShortestAddress(getActivity());

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
                        if(getActivity() != null) {
                            Global.EldScreenToast(OnDutyBtn, "Failed to refresh", getResources().getColor(R.color.colorVoilation));
                        }
                    }

                    break;

                case UpdateObdVeh:


                    String Message = error.toString();
                    if(Message.contains("timeout")){
                        Message = "Connection time out.";
                    }
                    if (SharedPref.GetNewLoginStatus(getActivity())) {
                        Global.EldScreenToast(loginDialogView, Message, getResources().getColor(R.color.colorVoilation));
                    }else{
                        Global.EldScreenToast(OnDutyBtn, Message, getResources().getColor(R.color.colorVoilation));
                    }

                    break;

                case GetAddFromLatLng:

                    try {

                        if (!IsAddressUpdate) {
                            EnableJobViews();
                            Global.LONGITUDE = Global.CheckLongitudeWithCycle(Global.LONGITUDE);
                            AddressLine = csvReader.getShortestAddress(getActivity());
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


    TcpClient.OnMessageReceived obdResponseHandler = new TcpClient.OnMessageReceived() {
        @Override
        public void messageReceived(String message) {
            // Log.d("response", "OBD Respone: " +message);
            String noObd = "obd not connected";

            if(!message.equals(noObd) && message.length() > 10){

                if(message.contains("CAN:UNCONNECTED")){
                    // dataTxtView.setText(noCanData);
                }else {

                    try {
                        data = decoder.DecodeTextAndSave(message, new OBDDeviceData());
                        JSONObject canObj = new JSONObject(data.toString());

                        String HighResolutionDistance   = wifiConfig.checkJsonParameter(canObj, "HighResolutionTotalVehicleDistanceInKM", "0");

                        //String odometerValue = "1370";
                        odometerhMethod.AddOdometerAutomatically(DRIVER_ID, DeviceId, HighResolutionDistance, DriverStatusId, dbHelper, getActivity());


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }
        }
    };





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
                                    if(getActivity() != null) {
                                        trailerDialog = new TrailorDialog(getActivity(), constants.Personal, false,
                                                TrailorNumber, 0, false, Global.onDutyRemarks, 0, dbHelper, new TrailorListener());
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

                if(getActivity() != null) {
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
            if(getActivity() != null){
                if(certifyAlertTime.length() > 0){
                    DateTime savedDate = Global.getDateTimeObj(certifyAlertTime, false);
                    DateTime currentDate = Global.getDateTimeObj(Global.GetCurrentUTCTimeFormat(), false);
                    int dayDiff = Days.daysBetween(savedDate.toLocalDate(), currentDate.toLocalDate()).getDays();
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
                    isCertifySignPending(false ), isFragmentAdd, fragManager, driverLogArray.toString());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    public void YardMovePersonalStatusAlert(boolean isYardMove, boolean isLogin) {

        try {
            if(getActivity() != null) {
                if (continueStatusDialog != null && continueStatusDialog.isShowing()) {
                    Log.d("dialog", "dialog is showing");
                } else {

                    String TruckIgnitionStatus = SharedPref.GetTruckIgnitionStatusForContinue(constants.TruckIgnitionStatus, getActivity());
                    continueStatusDialog = new ContinueStatusDialog(getActivity(), isYardMove, isLogin, TruckIgnitionStatus, new ContinueListener());
                    continueStatusDialog.show();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void shipmentInfoAlert() {

        try{
            if(getActivity() != null){

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
