package com.messaging.logistic.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adapter.logistic.DriverLogInfoAdapter;
import com.adapter.logistic.OdometerAdapter;
import com.adapter.logistic.RecapAdapter;
import com.adapter.logistic.ShippingViewDetailAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.ConstantHtml;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.SaveDriverLogPost;
import com.constants.ScrollViewExt;
import com.constants.ScrollViewListener;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.constants.WebAppInterface;
import com.custom.dialogs.CertifyConfirmationDialog;
import com.custom.dialogs.DatePickerDialog;
import com.custom.dialogs.LoginDialog;
import com.custom.dialogs.MissingLocationDialog;
import com.custom.dialogs.SignDialog;
import com.custom.dialogs.SignRecordDialog;
import com.driver.details.DriverConst;
import com.models.EldDriverLogModel;
import com.local.db.CertifyLogMethod;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.LatLongHelper;
import com.local.db.OdometerHelperMethod;
import com.local.db.RecapViewMethod;
import com.local.db.ShipmentHelperMethod;
import com.messaging.logistic.EldActivity;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.models.DriverLocationModel;
import com.models.EldDataModelNew;
import com.models.OdometerModel;
import com.models.RecapModel;
import com.models.RecapSignModel;
import com.models.ShipmentModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.MainDriverEldPref;
import com.shared.pref.StatePrefManager;
import com.simplify.ink.InkView;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;

public class CertifyViewLogFragment extends Fragment implements View.OnClickListener, ScrollViewListener {


    View rootView;
    List<EldDriverLogModel> DriverLogList;
    DriverLogInfoAdapter LogInfoAdapter;

    ListView certifyLogListView, recapHistoryListView, odometerListView, shippingDetailListView;
    List<RecapModel> recapList;
    RecapAdapter recapAdapter;

    List<String> StateArrayList;
    List<DriverLocationModel> StateList;
    List<ShipmentModel> shipmentLogList = new ArrayList<ShipmentModel>();

    List<OdometerModel> odometerList;
    OdometerAdapter odometerAdapter;
    ShippingViewDetailAdapter shippingAdapter;

    public static Button saveSignatureBtn, editLogBtn, showHideRecapBtn;
    public static TextView invisibleRfreshBtn ;
    ImageView eldMenuBtn, signImageView, nextDateBtn, previousDateBtn, loadingSpinEldIV, certifyErrorImgView;
    TextView EldTitleTV, certifyDateTV, certifyCycleTV, EngineHourTitle, EngineHourTV;
    TextView certifyDriverNameTV, certifyCoDriverNameTV, certifyDriverIDTV, certifyCoDriverIDTV;
    TextView certifyDistanceTV, certifyCarrierTV, certifyVehicleTV, certifyTrailerTV, certifyMainOfficeTV,
            certifyHomeTV, certifyFromTV, certifyToTV, certifyRemarksTV, shipperNameTV, blNumberTV, commodityRecapTV;
    TextView totalCycleHrsTV, leftCycleTV, HrsAvailTV, HrsWorkedTV, hourAvailableTomoTV; // startReadingTV, endReadingTV, distanceReadingTV, vehicleReadingTV;
    TextView dayRecapTV, dateRecapTV, hourRecapTV, certifyLocView, certifyEldView, totalDisOdoTV, totalMilesOdoTV;
    TextView dateActionBarTV, plateNoTV, vinNumberTV, certifyExcptnTV, signLogTitle2;
    int RecapViewHeight = 0, odometerLayHeight = 0, shippingLayHeight = 0;

    LinearLayout certifyLogLay, logHistorylay, recapLayout, LogInfoLay, certifyLogItemLay,
            itemOdometerLay, itemShippingLay, certifyLocLay;
    RelativeLayout rightMenuBtn, signLay, SignatureMainLay, eldMenuLay, recapItemLay, viewDetailMaiLay, certifyRecordsListLay;

    String LogDate = "", CurrentDate = "", CurrentDateDefault = "", DayName = "", MonthFullName = "", MonthShortName = "", DRIVER_ID = "";
    String CountryCycle = "",  CompanyId = "";
    String MainDriverName = "",CoDriverName = "N/A", DeviceId = "", CurrentCycleId = "", VehicleId = "";  //   MainDriverId = "",CoDriverId = "N/A",
    String Distance, HomeTerminal, PlateNumber = "", TruckNo, TrailerNo, VIN_NUMBER = "", OfficeAddress = "", Carrier = "", Remarks = "";
    String TeamDriverType = "1", imagePath = "", LogSignImage = "", LogSignImageInByte = "", EngineMileage = "", OffLineLogSignImage = "", OfflineByteImg = "";
    String TotalOnDutyHours         = "00:00";
    String TotalDrivingHours        = "00:00";
    String LeftCycleHours           = "00:00";
    String LeftDayDrivingHours      = "00:00";
    String TotalOffDutyHours        = "00:00";
    String TotalSleeperBerthHours   = "00:00";

    int DRIVER_JOB_STATUS = 1, OldStatus = -1;
    final int GetDriverLog          = 1;
    final int GetOdometer           = 2;
    final int GET_SHIPMENT          = 3;
    final int GET_SHIPMENT_18Days   = 4;
    final int GetOdometers18Days    = 5;
    final int GetRecapViewData      = 6;
    final int GetDriverLog18Days    = 7;
    final int GetDriverPermission   = 8;
    final int SaveCertifyOnResume   = 9;
    final int SaveCertifyLog        = 10;
    final int GetReCertifyRecords   = 11;

    //int displayHeight   = 0;
    int displayWidth    = 0;

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

    int EditDaysCount       = 1;
    int DriverPermitMaxDays = 0;
    int SelectedDayOfMonth  = 0;
    int UsaMaxDays          = 8;
    int CanMaxDays          = 14;
    int MaxDays;

    String DefaultLine      = " <g class=\"event \">\n";
    String ViolationLine    = " <g class=\"event line-red\">\n";

    String htmlAppendedText = "";
    String colorVoilation = "#C92627";

    boolean isViolation     = false;
  //  boolean isCertifyLog    = false;
    boolean isSignPending   = false;
    boolean IsAOBRD         = false;
    boolean UpdateRecap     = false;
    boolean isOldRecord     = false;
   // boolean isDOT           = false;
    boolean isNorthCanada   = false;

    boolean nextPrevBtnClicked      = false;
    boolean isCertifyViewAgain      = false;
    boolean isTrueAnyPermission     = false;
    boolean isPermissionResponse    = false;
    boolean IsRecapApiCalled        = false;
    boolean IsCurrentDate           = false;
    boolean IsEditBtnVisible        = false;
    boolean IsEditLocation          = false;
    boolean IsAOBRDAutomatic        = false;
    boolean IsScrollEnd             = false;
    boolean IsContinueWithSign      = false;
    boolean isCertifySignExist      = false;
    boolean isExceptionEnabledForDay= false;
    boolean isLoadImageCalled       = false;
    boolean isReCertifyRequired     = false;
    boolean isSaveCertifyClicked    = false;

    int startHour = 0,startMin = 0, endHour = 0, endMin = 0;
    SignDialog signDialog;
    DatePickerDialog dateDialog;
    AlertDialog alertDialog;

    private DisplayImageOptions options;
    VolleyRequest GetLogRequest, GetOdometerRequest, GetShipmentRequest, GetShippingRequest,
            GetLog18DaysRequest, GetRecapView18DaysData, GetPermissions, GetReCertifyRequest;
    Map<String, String> params;
    WebView graphWebView;
    ProgressBar progressBarDriverLog;
    ProgressDialog progressDialog;

    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;
    DBHelper dbHelper;
    OdometerHelperMethod odometerhMethod;
    HelperMethods hMethods;
    ShipmentHelperMethod shipmentHelper;
    RecapViewMethod recapViewMethod;
    LatLongHelper latLongHelper;
    DriverPermissionMethod driverPermissionMethod;
    CertifyLogMethod certifyLogMethod;

    ScrollViewExt driverLogScrollView;
    JSONObject logPermissionObj = new JSONObject();
    JSONArray driverLogArray = new JSONArray();
    JSONArray selectedArray,  Shipping18DaysArray, odometer18DaysArray, recap18DaysArray, CertifyLogArray;
    SaveDriverLogPost saveCertifyLogPost;
    DateTime currentDateTime, selectedDateTime, selectedUtcTime, selectedDateRecap;
    Globally global;
    Constants constants;
    SharedPref sharedPref;
    String LeftWeekOnDutyHoursInt = "00:00", LeftDayOnDutyHoursInt = "00:00",
            LeftDayDrivingHoursInt = "00:00", TotalCycleUsedHour = "00:00";
    LoginDialog loginDialog;
    SignRecordDialog signRecordDialog;
    CertifyConfirmationDialog certifyConfirmationDialog;
    MissingLocationDialog missingLocationDialog;

    String MainDriverPass = "", CoDriverPass = "";
    int  eldWarningColor;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 101;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_certify_log, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }



    void initView(View view) {

        sharedPref                  = new SharedPref();
        constants                   = new Constants();
        global                      = new Globally();
        dbHelper                    = new DBHelper(getActivity());
        hMethods                    = new HelperMethods();

        saveCertifyLogPost          = new SaveDriverLogPost(getActivity(), saveCertifyResponse);
        odometerhMethod             = new OdometerHelperMethod();
        shipmentHelper              = new ShipmentHelperMethod();
        recapViewMethod             = new RecapViewMethod();
        latLongHelper               = new LatLongHelper();
        driverPermissionMethod      = new DriverPermissionMethod();
        certifyLogMethod            = new CertifyLogMethod();

        MainDriverPref              = new MainDriverEldPref();
        CoDriverPref                = new CoDriverEldPref();

        driverLogScrollView         = (ScrollViewExt)view.findViewById(R.id.driverLogScrollView);
        progressBarDriverLog        = (ProgressBar)view.findViewById(R.id.progressBarDriverLog);
        graphWebView                = (WebView)view.findViewById(R.id.graphWebView);
        certifyLogListView          = (ListView)view.findViewById(R.id.certifyLogListView);
        recapHistoryListView        = (ListView)view.findViewById(R.id.recapHistoryListView);
        odometerListView            = (ListView)view.findViewById(R.id.odometerListView);
        shippingDetailListView      = (ListView)view.findViewById(R.id.shippingDetailListView);

        editLogBtn                  = (Button)view.findViewById(R.id.editLogBtn);
        showHideRecapBtn            = (Button)view.findViewById(R.id.showHideRecapBtn);
        saveSignatureBtn            = (Button) view.findViewById(R.id.saveSignatureBtn);
        eldMenuBtn                  = (ImageView)view.findViewById(R.id.eldMenuBtn);
        signImageView               = (ImageView)view.findViewById(R.id.signImageView);
        previousDateBtn             = (ImageView)view.findViewById(R.id.previousDate);
        nextDateBtn                 = (ImageView)view.findViewById(R.id.nextDateBtn);
        loadingSpinEldIV            = (ImageView)view.findViewById(R.id.loadingSpinEldIV);
        certifyErrorImgView         = (ImageView)view.findViewById(R.id.certifyErrorImgView);

        certifyRecordsListLay       = (RelativeLayout)view.findViewById(R.id.certifyRecordsListLay);
        rightMenuBtn                = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        certifyLogLay               = (LinearLayout)view.findViewById(R.id.certifyLogLay);
        logHistorylay               = (LinearLayout)view.findViewById(R.id.logHistorylay);
        recapLayout                 = (LinearLayout)view.findViewById(R.id.recapLayout);
        LogInfoLay                  = (LinearLayout)view.findViewById(R.id.LogInfoLay);
        certifyLogItemLay           = (LinearLayout)view.findViewById(R.id.certifyLogItemLay);
        certifyLocLay               = (LinearLayout)view.findViewById(R.id.certifyLocLay);
        itemOdometerLay             = (LinearLayout)view.findViewById(R.id.itemOdometerLay);
        itemShippingLay             = (LinearLayout)view.findViewById(R.id.itemShippingLay);

        SignatureMainLay            = (RelativeLayout)view.findViewById(R.id.SignatureMainLay);
        signLay                     = (RelativeLayout)view.findViewById(R.id.signLay);
        eldMenuLay                  = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        recapItemLay                = (RelativeLayout)view.findViewById(R.id.recapItemLay);
        viewDetailMaiLay            = (RelativeLayout)view.findViewById(R.id.viewDetailMaiLay);

        totalDisOdoTV               = (TextView)view.findViewById(R.id.totalDisOdoTV);
        totalMilesOdoTV             = (TextView)view.findViewById(R.id.totalMilesOdoTV);
        certifyEldView              = (TextView)view.findViewById(R.id.certifyEldView);
        certifyLocView              = (TextView)view.findViewById(R.id.certifyLocView);
        EldTitleTV                  = (TextView)view.findViewById(R.id.EldTitleTV);
        certifyDateTV               = (TextView)view.findViewById(R.id.certifyDateTV);
        certifyCycleTV              = (TextView)view.findViewById(R.id.certifyCycleTV);
        EngineHourTV                = (TextView)view.findViewById(R.id.EngineHourTV);
        EngineHourTitle             = (TextView)view.findViewById(R.id.EngineHourTitle);
        certifyDriverNameTV         = (TextView)view.findViewById(R.id.certifyDriverNameTV);
        certifyDriverIDTV           = (TextView)view.findViewById(R.id.certifyDriverIDTV);
        certifyCoDriverNameTV       = (TextView)view.findViewById(R.id.certifyCoDriverNameTV);
        certifyCoDriverIDTV         = (TextView)view.findViewById(R.id.certifyCoDriverIDTV);
        invisibleRfreshBtn          = (TextView)view.findViewById(R.id.invisibleRfreshBtn);

        certifyDistanceTV           = (TextView)view.findViewById(R.id.certifyDistanceTV);
        certifyCarrierTV            = (TextView)view.findViewById(R.id.certifyCarrierTV);
        certifyVehicleTV            = (TextView)view.findViewById(R.id.certifyVehicleTV);
        certifyTrailerTV            = (TextView)view.findViewById(R.id.certifyTrailerTV);
        certifyMainOfficeTV         = (TextView)view.findViewById(R.id.certifyMainOfficeTV);
        certifyHomeTV               = (TextView)view.findViewById(R.id.certifyHomeTV);
        certifyFromTV               = (TextView)view.findViewById(R.id.certifyFromTV);
        certifyToTV                 = (TextView)view.findViewById(R.id.certifyToTV);
        certifyRemarksTV            = (TextView)view.findViewById(R.id.certifyRemarksTV);
        blNumberTV                  = (TextView)view.findViewById(R.id.blNumberTV);
        shipperNameTV               = (TextView)view.findViewById(R.id.shipperNameTV);
        commodityRecapTV            = (TextView)view.findViewById(R.id.commodityRecapTV);

        totalCycleHrsTV             = (TextView)view.findViewById(R.id.totalHrsSinceTV);
        HrsAvailTV                  = (TextView)view.findViewById(R.id.HrsAvailTV);
        HrsWorkedTV                 = (TextView)view.findViewById(R.id.HrsWorkedTV);
        leftCycleTV                 = (TextView)view.findViewById(R.id.leftCycleTV);
        hourAvailableTomoTV         = (TextView)view.findViewById(R.id.hourAvailableTomoTV);

        dayRecapTV                  = (TextView)view.findViewById(R.id.dayRecapTV);
        dateRecapTV                 = (TextView)view.findViewById(R.id.dateRecapTV);
        hourRecapTV                 = (TextView)view.findViewById(R.id.hourRecapTV);
        dateActionBarTV             = (TextView)view.findViewById(R.id.dateActionBarTV);
        plateNoTV                   = (TextView)view.findViewById(R.id.plateNoTV);
        vinNumberTV                 = (TextView)view.findViewById(R.id.vinNumberTV);
        certifyExcptnTV             = (TextView)view.findViewById(R.id.certifyExcptnTV);
        signLogTitle2               = (TextView)view.findViewById(R.id.signLogTitle2);

        itemOdometerLay.measure(0,0);
        itemShippingLay.measure(0,0);
        odometerLayHeight   = itemOdometerLay.getMeasuredHeight();
        shippingLayHeight   = itemShippingLay.getMeasuredHeight();

        GetLogRequest           = new VolleyRequest(getActivity());
        GetOdometerRequest      = new VolleyRequest(getActivity());
        GetShipmentRequest      = new VolleyRequest(getActivity());
        GetShippingRequest      = new VolleyRequest(getActivity());
        GetRecapView18DaysData  = new VolleyRequest(getActivity());
        GetLog18DaysRequest     = new VolleyRequest(getActivity());
        GetPermissions          = new VolleyRequest(getActivity());
        GetReCertifyRequest     = new VolleyRequest(getActivity());

        Constants.IS_ACTIVE_ELD = false;
        getBundleData();

        CurrentDateDefault      = Globally.GetCurrentDeviceDateDefault() + "T00:00:00";
        CurrentDate             = Globally.GetCurrentDeviceDate();
        IsAOBRDAutomatic        = sharedPref.IsAOBRDAutomatic(getActivity());

        eldWarningColor         = getActivity().getResources().getColor(R.color.colorVoilation);


        if(sharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
            DriverType = Constants.MAIN_DRIVER_TYPE;
        }else{
            DriverType = Constants.CO_DRIVER_TYPE;
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");

        int viewPadding = constants.intToPixel(getActivity(), 3);
        int viewRightPadding = constants.intToPixel(getActivity(), 5);

        loadingSpinEldIV.setPadding(viewPadding, viewPadding, viewRightPadding, viewPadding);
        loadingSpinEldIV.setImageResource(R.drawable.certify_calendar);
        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            viewDetailMaiLay.setBackgroundColor(getResources().getColor(R.color.gray_background) );
        }

        GetSavePreferences();
        crossVerifyRecapData();


        try {

            dayRecapTV.setTypeface(dayRecapTV.getTypeface(), Typeface.BOLD);
            dateRecapTV.setTypeface(dateRecapTV.getTypeface(), Typeface.BOLD);
            hourRecapTV.setTypeface(hourRecapTV.getTypeface(), Typeface.BOLD);
            RecapViewHeight = dayRecapTV.getLayoutParams().height;
            certifyExcptnTV.setVisibility(View.VISIBLE);

            if (LogDate.split("/").length > 1) {
                EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " ( " + DayName + " )");
            } else {
                EldTitleTV.setText(LogDate);
            }

            signImageView.setBackground(null);
            signImageView.setImageResource(R.drawable.transparent);
        }catch (Exception e){
            e.printStackTrace();
        }


        if(CurrentCycleId.equalsIgnoreCase("null"))
            CurrentCycleId = Globally.NO_CYCLE;



            if(isSignPending){
                certifyErrorImgView.setVisibility(View.VISIBLE);
            }

            certifyRecordsListLay.setVisibility(View.VISIBLE);
            certifyDateTV.setVisibility(View.GONE);

            if(LogDate.length() > 3)
                certifyDateTV.setText(MonthFullName + " " + LogDate.substring(3, LogDate.length() ));
            else
                certifyDateTV.setText(MonthFullName + " " + LogDate);

            if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS) ) {
                MaxDays = UsaMaxDays;
            }else{
                MaxDays = CanMaxDays;
            }
            JSONArray recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
            int recapArrayLength = recap18DaysArray.length();
            if (DriverPermitMaxDays > recapArrayLength) {
                if(recapArrayLength > 0) {
                    try {
                        JSONObject obj = (JSONObject) recap18DaysArray.get(0);
                        String date = obj.getString(ConstantsKeys.Date);
                        DateTime standardDateFormat = global.getDateTimeObj(Globally.ConvertDateFormat(date), false);

                        int diff = hMethods.DayDiff(currentDateTime, standardDateFormat);

                        if(diff <= DriverPermitMaxDays) {
                            DriverPermitMaxDays = diff;
                        }else{
                            DriverPermitMaxDays = recapArrayLength - 1;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }else{
                    DriverPermitMaxDays = 0;
                }
            }

            if(DriverPermitMaxDays < 0 ){
                DriverPermitMaxDays = 0;
            }

            if(DriverPermitMaxDays > MaxDays){
                DriverPermitMaxDays = MaxDays;
            }else{
                MaxDays = DriverPermitMaxDays;
            }

            if(SelectedDayOfMonth == constants.CertifyLog){
                openSignRecordDialog(false);
            }else{
                openMissingDialogAlert();
            }
       // }

        getPermissionWithView();


        WebSettings webSettings = graphWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        graphWebView.setWebViewClient(new WebViewClient());
        graphWebView.setWebChromeClient(new WebChromeClient());
        graphWebView.addJavascriptInterface( new WebAppInterface(), "Android");

        eldMenuBtn.setImageResource(R.drawable.back_btn);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.sign_here)
                .showImageForEmptyUri(R.drawable.sign_here)
                .showImageOnFail(R.drawable.sign_here)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        driverLogScrollView.setScrollViewListener(this);

        setMarqueonView(certifyVehicleTV);
        setMarqueonView(certifyTrailerTV);

        CheckSignatureVisibilityStatus(selectedArray);
        refreshPendingSignView();
        CallAPIs();

        recapHistoryListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });



        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {

              //  if(isCertifyLog) {
                    if(IsEditBtnVisible){
                        IsEditBtnVisible = false;
                        editLogBtn.setVisibility(View.GONE);
                    }else{
                        int diff = hMethods.DayDiff(currentDateTime, selectedDateRecap);
                        if( diff < 2 && isTrueAnyPermission ) {

                            if(diff == 0 && EditDaysCount != -1){
                                if(sharedPref.IsCCMTACertified(getActivity()) == false) {
                                    IsEditBtnVisible = true;
                                    editLogBtn.setVisibility(View.VISIBLE);
                                }
                            }else if(diff == 1 && EditDaysCount == 1){
                                IsEditBtnVisible = true;
                                editLogBtn.setVisibility(View.VISIBLE);
                            }
                        }else{
                            IsEditBtnVisible = false;
                            editLogBtn.setVisibility(View.GONE);
                        }
                    }
              //  }

                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });
        gestureDetector.setIsLongpressEnabled(true);

        graphWebView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }

        });



        if(LogDate.trim().length() == 0) {
            Toast.makeText(getActivity(), "Date format issue. Please select again.", Toast.LENGTH_LONG).show();
            getParentFragmentManager().popBackStack();
        }

        eldMenuLay.setOnClickListener(this);
        signLay.setOnClickListener(this);
        saveSignatureBtn.setOnClickListener(this);
        editLogBtn.setOnClickListener(this);
        showHideRecapBtn.setOnClickListener(this);
        EldTitleTV.setOnClickListener(this);
        certifyDateTV.setOnClickListener(this);
        dateActionBarTV.setOnClickListener(this);
        previousDateBtn.setOnClickListener(this);
        nextDateBtn.setOnClickListener(this);
        rightMenuBtn.setOnClickListener(this);
        certifyRecordsListLay.setOnClickListener(this);
        invisibleRfreshBtn.setOnClickListener(this);
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

       // isCertifySignExist  = constants.isCertifySignExist(recapViewMethod, DRIVER_ID, LogDate, dbHelper);
        CertifyLogArray     = certifyLogMethod.getSavedCertifyLogArray(Integer.valueOf(DRIVER_ID), dbHelper);

        if(Globally.isConnected(getActivity()) && CertifyLogArray.length() > 0){
            saveCertifyLogPost.PostDriverLogData(CertifyLogArray, APIs.CERTIFY_LOG_OFFLINE, constants.SocketTimeout20Sec, true, false, DriverType, SaveCertifyOnResume);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.IS_ACTIVE_ELD = true;
    }


    void getBundleData(){
        try{
            Bundle getBundle        = this.getArguments();
            if(getBundle != null) {
                LogDate = getBundle.getString("date");
                DayName = getBundle.getString("day_name");
                MonthFullName = getBundle.getString("month_full_name");
                MonthShortName = getBundle.getString("month_short_name");
                SelectedDayOfMonth = getBundle.getInt("day_of_month");
                //  isCertifyLog            = getBundle.getBoolean("is_certify");
                isSignPending = getBundle.getBoolean("signStatus");
                VIN_NUMBER = getBundle.getString("vin");
                offsetFromUTC = getBundle.getInt("offset");

                LeftWeekOnDutyHoursInt = getBundle.getString("LeftWeekOnDuty");
                LeftDayOnDutyHoursInt = getBundle.getString("LeftDayOnDuty");
                LeftDayDrivingHoursInt = getBundle.getString("LeftDayDriving");
                CurrentCycleId = getBundle.getString("cycle");
                VehicleId = getBundle.getString("VehicleId");
                driverLogArray = new JSONArray(getBundle.getString("driverLogArray"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void GetShipmentDetails(){

        JSONObject dataObj = null;
        try{
            if(LogDate.equals(CurrentDate)){

                    //  JSONArray shipmentArray = shipmentHelper.getSavedShipmentArray(Integer.valueOf(Globally.PROJECT_ID), dbHelper);
                    if(Shipping18DaysArray.length() > 0 ){
                        dataObj = shipmentHelper.GetLastJsonObject(Shipping18DaysArray, 0);
                    }else{
                        dataObj = shipmentHelper.getShipmentRecord(dataObj, Shipping18DaysArray, DRIVER_ID, LogDate, selectedDateRecap, dbHelper);
                    }

            }else{
                dataObj = shipmentHelper.getShipmentRecord(dataObj, Shipping18DaysArray, DRIVER_ID, LogDate, selectedDateRecap, dbHelper);
            }
        }catch (Exception e){
                e.printStackTrace();
        }

        setShipperInfo(dataObj, CurrentDate, LogDate);


        try {
            shipmentLogList = new ArrayList<ShipmentModel>();

            if(!LogDate.equals(CurrentDate)) {
                if (selectedArray.length() > 1) {
                    addDetailShippingList();
                }else{
                    if(selectedArray.length() == 1){
                        JSONObject shippingJson = (JSONObject) selectedArray.get(0);
                        int JobStatus = shippingJson.getInt(ConstantsKeys.DriverStatusId);

                        if(JobStatus == Constants.DRIVING || JobStatus == Constants.ON_DUTY){
                            addDetailShippingList();
                        }
                    }
                }
            }else{
                addDetailShippingList();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            //   Collections.sort(shipmentLogList);
            shippingAdapter = new ShippingViewDetailAdapter(getActivity(), shipmentLogList);
            shippingDetailListView.setAdapter(shippingAdapter);

        }catch (Exception e){}

        try{
            if(shippingLayHeight == 0) {
                itemShippingLay.measure(0, 0);
                shippingLayHeight = itemShippingLay.getMeasuredHeight();
            }
            final int Height      = (shippingLayHeight ) * shipmentLogList.size();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    shippingDetailListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Height  ));
                }
            },800);

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    private void addDetailShippingList(){
        try {
            for(int i = 0 ; i < Shipping18DaysArray.length() ; i++){
                JSONObject obj = (JSONObject)Shipping18DaysArray.get(i);
                String[] dateArray = obj.getString(ConstantsKeys.ShippingDocDate).split(" ");
                String date = "";
                if(dateArray.length > 0){
                    date = dateArray[0];
                }

                String savedDate = "", commodity = "";
                if(obj.has(ConstantsKeys.ShippingSavedDate)){
                    savedDate = obj.getString(ConstantsKeys.ShippingSavedDate);
                }else if(obj.has(ConstantsKeys.shippingdate)){
                    savedDate = obj.getString(ConstantsKeys.shippingdate);
                }

                if(obj.has(ConstantsKeys.Commodity)){
                    commodity = obj.getString(ConstantsKeys.Commodity);
                }

                if(date.equals(LogDate)) {

                    String blNumber = obj.getString(ConstantsKeys.ShippingDocumentNumber);
                    String ShipperName =  obj.getString(ConstantsKeys.ShipperName);
                    String ShipperState =  obj.getString(ConstantsKeys.ShipperState);
                    String ShipperPostalCode =  obj.getString(ConstantsKeys.ShipperPostalCode);

                    //blNumber.equals("") && blNumber.equals(getResources().getString(R.string.Empty))
                    if(  !ShipperPostalCode.trim().equals("")) {
                        ShipmentModel shipModel = new ShipmentModel(
                                i,
                                DRIVER_ID,
                                "",
                                DeviceId,
                                date,
                                blNumber,
                                commodity,
                                ShipperName,
                                ShipperState,
                                ShipperPostalCode,
                                savedDate,
                                false,
                                false
                        );

                        shipmentLogList.add(shipModel);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void CallAPIs(){

        try {
            Shipping18DaysArray = shipmentHelper.getShipment18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
            odometer18DaysArray = odometerhMethod.getSavedOdometer18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
            recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);

        }catch (Exception e){
            Shipping18DaysArray = new JSONArray();
            e.printStackTrace();
        }

        if(Globally.isConnected(getActivity()) ){

            DateTime currentDateTime    = new DateTime(Globally.GetCurrentDateTime());
            DateTime startDateTime      = Globally.GetStartDate(currentDateTime, 14);
            String StartDate            = Globally.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime));
            String EndDate              = Globally.GetCurrentDeviceDate(); // current Date



            DateTime fromDateTime       = currentDateTime.minusDays(DriverPermitMaxDays);
            String fromDateStr = Globally.ConvertDateFormatMMddyyyy(fromDateTime.toString());

            GetReCertifyRecords(DRIVER_ID, fromDateStr, LogDate);

            if(Shipping18DaysArray.length() == 0){
                GetShipment18Days( DRIVER_ID, DeviceId, LogDate, GET_SHIPMENT_18Days);
            }else{
                GetShipmentDetails();
            }

            if (odometer18DaysArray == null || odometer18DaysArray.length() == 0) {
                GetOdometer18Days(DRIVER_ID, DeviceId, CompanyId, LogDate);
            }


            if(recap18DaysArray == null || recap18DaysArray.length() == 0) {
                GetRecapView18DaysData(DRIVER_ID, DeviceId, StartDate, EndDate );
            }else{
                if(recapViewMethod.GetSelectedRecapData(recap18DaysArray, EndDate) == null && !BackgroundLocationService.IsRecapApiACalled){
                    StartDate = recapViewMethod.GetLastItemDate(recap18DaysArray);

                    startDateTime   = new DateTime(Globally.ConvertDateFormat(StartDate));
                    startDateTime   = Globally.GetStartDatePlus(startDateTime, 1);
                    StartDate       = Globally.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime) );

                    UpdateRecap = true;

                    GetRecapView18DaysData(DRIVER_ID, DeviceId, StartDate, EndDate );

                }
            }

        }else{
            GetShipmentDetails();
        }

        selectedDateRecap = Globally.getDateTimeObj(global.ConvertDateFormat(LogDate), false);
        GET_CERTIFY_LOG();


    }



    void ClearShipperFields(){
        blNumberTV.setText("N/A");
        shipperNameTV.setText("--");
        certifyFromTV.setText("--");
        certifyToTV.setText("--");
        commodityRecapTV.setText("--");
    }

    void setShipperInfo(JSONObject dataObj, String currentDate, String selectedDate){

        ClearShipperFields();

        if(dataObj != null) {

            try {
                //  if(dataObj.getBoolean(ConstantsKeys.IsShippingCleared) == false) {
                NullCheckJson(dataObj, blNumberTV, ConstantsKeys.ShippingDocumentNumber, "N/A");
                NullCheckJson(dataObj, shipperNameTV, ConstantsKeys.ShipperName, "--");
                NullCheckJson(dataObj, certifyFromTV, ConstantsKeys.FromAddress, "--");
                NullCheckJson(dataObj, certifyToTV, ConstantsKeys.ToAddress, "--");
                NullCheckJson(dataObj, commodityRecapTV, ConstantsKeys.Commodity, "--");
                if(blNumberTV.getText().toString().trim().length() == 0 ){
                    blNumberTV.setText("N/A");
                }
             /*   if(certifyFromTV.getText().toString().trim().length() > 0 && certifyToTV.getText().toString().trim().length() > 0) {
                    FromEditText.setText(ToAddress);
                    certifyToTV.setText("");
                }else{
                    FromEditText.setText(FromAddress);
                    ToEditText.setText(ToAddress);
                }

*/

                // }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // If Driving or onduty not existed in selected date array. Shipping info should be empty for current Date.
        if(!IsDataAvailable(selectedArray) && !IsCurrentDate){
            ClearShipperFields();
        }

    }


    private boolean IsDataAvailable(JSONArray selectedArray){
        boolean IsData = false;
        for(int i = 0 ; i < selectedArray.length() ; i++){
            try {
                JSONObject obj = (JSONObject)selectedArray.get(i);
                int DriverStatus = obj.getInt(ConstantsKeys.DriverStatusId);
                if(DriverStatus == EldFragment.DRIVING || DriverStatus == EldFragment.ON_DUTY ){
                    IsData = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return IsData;
    }


    private void GetSavePreferences(){

        global.hideSoftKeyboard(getActivity());

        if(EldFragment.isUpdateDriverLog){
            LogDate                 = Constants.LogDate;
            DayName                 = Constants.DayName;
            MonthFullName           = Constants.MonthFullName;
            MonthShortName          = Constants.MonthShortName;
            driverLogArray          = new JSONArray();
        }else{
            if(Constants.IsEdiLogBackStack) {
                Constants.IsEdiLogBackStack = false;
                LogDate = Constants.LogDate;
                DayName = Constants.DayName;
                MonthFullName = Constants.MonthFullName;
                MonthShortName = Constants.MonthShortName;
                driverLogArray = new JSONArray();
            }
        }

        String[] dateArray = LogDate.split("/");
        if(dateArray.length > 1) {
            EldTitleTV.setText(MonthShortName + " " + dateArray[1] + " ( " + DayName + " )");
        }

        if(LogDate.length() > 3) {
            certifyDateTV.setText(MonthFullName + " " + LogDate.substring(3, LogDate.length()));
        }else{
            certifyDateTV.setText(MonthFullName + " " + LogDate);
        }


        DeviceId                = sharedPref.GetSavedSystemToken(getActivity());
        DRIVER_ID               = sharedPref.getDriverId( getActivity());
        MainDriverName          = DriverConst.GetDriverDetails( DriverConst.DriverName, getActivity());

        CoDriverName            = DriverConst.GetCoDriverDetails( DriverConst.CoDriverName, getActivity());
        TeamDriverType          = sharedPref.getDriverType(getActivity());
        IsAOBRD                 = sharedPref.IsAOBRD(getActivity());

        TruckNo             = DriverConst.GetDriverTripDetails(DriverConst.Truck, getActivity());
        isNorthCanada       =  sharedPref.IsNorthCanada(getActivity());
        CountryCycle        = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycle, getActivity());
        CompanyId           = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());

        if(sharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
            certifyDriverNameTV.setText(MainDriverName);
            HomeTerminal        = DriverConst.GetDriverDetails(DriverConst.HomeTerminal, getActivity());
            OfficeAddress       = DriverConst.GetDriverDetails(DriverConst.CarrierAddress, getActivity());
            Carrier             = DriverConst.GetDriverDetails(DriverConst.Carrier, getActivity());

        } else {
            certifyDriverNameTV.setText(CoDriverName);

           // CompanyId           = DriverConst.GetCoDriverDetails(DriverConst.CoCompanyId, getActivity());
            HomeTerminal        = DriverConst.GetCoDriverDetails(DriverConst.CoHomeTerminal, getActivity());
            OfficeAddress       = DriverConst.GetCoDriverDetails(DriverConst.CoCarrierAddress, getActivity());
            Carrier             = DriverConst.GetCoDriverDetails(DriverConst.CoCarrier, getActivity());
        }

        if(IsAOBRD){
            certifyEldView.setText(getString(R.string.aobrd));
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
       // displayHeight = displayMetrics.heightPixels;
        displayWidth = displayMetrics.widthPixels;

        CheckSelectedDateTime();

        selectedArray       = new JSONArray();
        Shipping18DaysArray = new JSONArray();

        try {
            if (driverLogArray.length() == 0) {
                driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
            }
            selectedArray = hMethods.GetSingleDateArray(driverLogArray, selectedDateTime, currentDateTime, selectedUtcTime, IsCurrentDate, offsetFromUTC);
            recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
            TrailerNo = recapViewMethod.getTrailerNumberFromArray(recap18DaysArray, LogDate);
        }catch (Exception e){
            e.printStackTrace();
        }

        certifyCycleTV.setText(CountryCycle);
        certifyCarrierTV.setText(Carrier);
        certifyVehicleTV.setText(TruckNo);
        certifyTrailerTV.setText(TrailerNo);
        certifyMainOfficeTV.setText(OfficeAddress);
        certifyHomeTV.setText(HomeTerminal);


        String engineHours = sharedPref.getObdEngineHours(getActivity());
        int ObdStatus = sharedPref.getObdStatus(getActivity());
        if((ObdStatus == Constants.WIRED_CONNECTED || ObdStatus == Constants.WIFI_CONNECTED
                || ObdStatus == Constants.BLE_CONNECTED) && engineHours.length() > 1) {
             EngineHourTV.setText(constants.Convert1DecimalPlacesDouble(Double.parseDouble(engineHours)));
        }else{
            EngineHourTitle.setVisibility(View.GONE);
        }

        if(Globally.isConnected(getActivity())){
            GetDriverStatusPermission(DRIVER_ID, DeviceId, VehicleId);
        }

        try {
            selectedDateRecap = Globally.getDateTimeObj(global.ConvertDateFormat(LogDate), false);
            logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DRIVER_ID), dbHelper);
            DriverPermitMaxDays = logPermissionObj.getInt(ConstantsKeys.ViewCertifyDays);
            MaxDays = DriverPermitMaxDays;
            if (logPermissionObj.has(ConstantsKeys.EditDays)) {
                EditDaysCount = logPermissionObj.getInt(ConstantsKeys.EditDays);
            }

          //  if (isCertifyLog) {
                isTrueAnyPermission = driverPermissionMethod.isTrueAnyPermission(logPermissionObj);
                IsEditLocation = driverPermissionMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.LocationKey);
           // }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            StatePrefManager statePrefManager  = new StatePrefManager();
            StateList = statePrefManager.GetState(getActivity());
        } catch (Exception e) { }

        StateArrayList =  sharedPref.getStatesInList(getActivity());

    }


    void getPermissionWithView(){
        try {

            String selectedDateStr = global.ConvertDateFormat(LogDate);
            String currentDateStr = global.ConvertDateFormat(CurrentDate);
            DateTime selectedDateTime = new DateTime(global.getDateTimeObj(selectedDateStr, false) );
            DateTime currentDateTime = new DateTime(global.getDateTimeObj(currentDateStr, false) );

            int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
            DOTBtnVisibility(DaysDiff, MaxDays);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void CheckSelectedDateTime(){
        try {
            currentDateTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
            String cDate = String.valueOf(currentDateTime);
            cDate = cDate.split("T")[0] + "T00:00:00";
            currentDateTime = new DateTime(Globally.getDateTimeObj(cDate, false));

            if (LogDate.equals(CurrentDate)) {
                selectedDateTime = currentDateTime;
                selectedUtcTime = Globally.getDateTimeObj(Globally.GetCurrentUTCTimeFormat(), true);
            } else {
                try {
                    selectedDateTime = Globally.getDateTimeObj(global.ConvertDateFormatyyyy_MM_dd(LogDate) + "T23:59:59", false); //global.ConvertDateFormat(LogDate)
                    selectedUtcTime = Globally.getDateTimeObj(global.GetUTCFromDate(global.ConvertDateFormat(LogDate), offsetFromUTC), true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            selectedDateRecap = Globally.getDateTimeObj(global.ConvertDateFormat(LogDate), false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private class CertificationListener implements CertifyConfirmationDialog.CertifyConfirmationListener{

        @Override
        public void CertifyBtnReady() {

            certifyListenerCall();

        }

        @Override
        public void CancelBtnReady() {
            Log.d("cancel", "cancel listener called");
        }
    }



    void certifyListenerCall(){
        if(isSaveCertifyClicked) {
            File f = new File(imagePath);
            if (isCertifySignExist) {
                String signBtnTxt = saveSignatureBtn.getText().toString();
                if (f.exists() && signBtnTxt.equals(getString(R.string.save_and_certify))) {
                    SaveDriverSignArray();
                } else {
                    ContinueWithoutSignDialog();
                }

            } else {

                if (f.exists()) {
                    SaveDriverSignArray();
                } else {
                    openSignDialog();
                }
            }
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.EldTitleTV:
                if(dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();

                dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, LogDate, new DateListener(), false);
                dateDialog.show();
                break;

            case R.id.saveSignatureBtn:
                if(constants.isActionAllowed(getActivity())) {
                    isSaveCertifyClicked = true;
                    certifyConfirmationDialog = new CertifyConfirmationDialog(getContext(), new CertificationListener());
                    certifyConfirmationDialog.show();
                }else{
                     Globally.EldScreenToast(saveSignatureBtn, getString(R.string.stop_vehicle_alert),
                            getResources().getColor(R.color.colorVoilation));
                }
                break;


            case R.id.editLogBtn:

                if(constants.isActionAllowed(getActivity())) {
                    String driverType = "";
                    if(DriverType == Constants.MAIN_DRIVER_TYPE){
                        driverType = DriverConst.StatusSingleDriver;
                    }else {
                        driverType = DriverConst.StatusTeamDriver;
                    }
                    ConfirmLoginDialog(driverType);

                }else{
                    Globally.EldScreenToast(saveSignatureBtn, getString(R.string.stop_vehicle_alert),
                            getResources().getColor(R.color.colorVoilation));
                }

                break;


            case R.id.showHideRecapBtn:

                if(showHideRecapBtn.getText().toString().equals(getString(R.string.show_recap))){
                    showHideRecapBtn.setText(getString(R.string.hide_recap));
                    recapLayout.setVisibility(View.VISIBLE);
                    SetCertifyListViewHeight();

                }else{
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, 0);
                    recapLayout.setLayoutParams(param);
                    showHideRecapBtn.setText(getString(R.string.show_recap));
                    recapLayout.setVisibility(View.GONE);
                }

                break;


            case R.id.signLay:

                //CheckStoragePermissionGranted();
                if(LogSignImageInByte.length() == 0) {
                    openSignDialog();
                }

                break;

            case R.id.eldMenuLay:
                /*if(isDOT) {    // DOT mode Enabled
                    EldActivity.DOTButton.performClick();
                }else{
                    getParentFragmentManager().popBackStack();
                }*/
                getParentFragmentManager().popBackStack();
                break;

            case R.id.certifyDateTV:


                break;

            case R.id.dateActionBarTV:

                /*if(isDOT) {
                    MoveFragment(LogDate);
                }*/

                break;

            case R.id.certifyRecordsListLay:
                openSignRecordDialog(true);
                break;


            case R.id.previousDate:
                nextPrevBtnClicked = true;
                ChangeViewWithDate(false);
                break;


            case R.id.nextDateBtn:
                nextPrevBtnClicked = true;
                ChangeViewWithDate(true);
                break;


            case R.id.invisibleRfreshBtn:

                refreshPendingSignView();
                CheckSignatureVisibilityStatus(selectedArray);

                break;


            case R.id.rightMenuBtn:

                EldTitleTV.performClick();

                break;

        }
    }


    private void openSignRecordDialog(boolean isToastShowing){
        if (signRecordDialog != null && signRecordDialog.isShowing())
            signRecordDialog.dismiss();

        List<RecapSignModel> signList = constants.GetCertifySignList(recapViewMethod, DRIVER_ID, hMethods, dbHelper,
                global.GetCurrentDeviceDate(), CurrentCycleId, logPermissionObj, global, getActivity());

        if (signList.size() > 0) {
            signRecordDialog = new SignRecordDialog(getActivity(), DriverType, isCertifySignExist, recap18DaysArray, signList, new SignRecapListener(),
                                                        constants,  recapViewMethod,  certifyLogMethod, dbHelper );
            signRecordDialog.show();
        } else {
            if(isToastShowing)
                global.EldScreenToast(eldMenuBtn, getString(R.string.no_recap_data), Color.parseColor(colorVoilation));
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openSignDialog();

                return true;
            } else {
                ActivityCompat.requestPermissions(((Activity)getContext()), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            openSignDialog();
            return true;
        }

    }


    private void CheckStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasWriteContactsPermission = getActivity().checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE);
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(permissions, REQUEST_CODE_ASK_PERMISSIONS);
                //  return;
            }else{
                openSignDialog();
            }
        }else{
            openSignDialog();
        }
    }


  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)  .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/


    void openSignDialog(){
        if(LogSignImage.trim().length() == 0 && getActivity() != null  ) {
            if (signDialog != null && signDialog.isShowing())
                signDialog.dismiss();
            signDialog = new SignDialog(getActivity(), new SignListener());
            signDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {

            case REQUEST_CODE_ASK_PERMISSIONS:

                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    /*if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                        //resume tasks needing this permission
                    }*/

                }else{
                    openSignDialog();
                }
                break;

        }

    }




    private void ChangeViewWithDate(boolean isNext){

        editLogBtn.setVisibility(View.GONE);
        IsEditBtnVisible = false;

        String selectedDateStr = global.ConvertDateFormat(LogDate);
        String currentDateStr = global.ConvertDateFormat(CurrentDate);
        selectedDateTime = new DateTime(global.getDateTimeObj(selectedDateStr, false) );
        DateTime currentDateTime = new DateTime(global.getDateTimeObj(currentDateStr, false) );

        if(isNext){
            selectedDateTime = selectedDateTime.plusDays(1);
        }else{
            selectedDateTime = selectedDateTime.minusDays(1);
        }

        selectedDateRecap   = selectedDateTime;
        int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);

        if ( DaysDiff >= 0 && DaysDiff <= MaxDays){

            DOTBtnVisibility(DaysDiff, MaxDays);
            LogDate = global.ConvertDateFormatMMddyyyy(selectedDateTime.toString());

            if(LogDate.length() > 2) {
                String dayOfTheWeek = global.GetDayOfWeek(LogDate);
                int mnth = Integer.valueOf(LogDate.substring(0, 2));
                String MonthFullName = global.MONTHS_FULL[mnth - 1];
                String MonthShortName = global.MONTHS[mnth - 1];

                GetLogWithDate(LogDate, dayOfTheWeek, MonthFullName, MonthShortName);
            }
        }else {
            if (DaysDiff > MaxDays) {
                nextDateBtn.setVisibility(View.VISIBLE);
                previousDateBtn.setVisibility(View.GONE);
            }
        }

        openMissingDialogAlert();

    }



    private void DOTBtnVisibility(final int DaysDiff, final int MaxDays){

                if(DaysDiff == 0){
                    nextDateBtn.setVisibility(View.GONE);
                    if(DriverPermitMaxDays > 0) {
                        previousDateBtn.setVisibility(View.VISIBLE);
                    }
                }else if(DaysDiff == MaxDays){
                    previousDateBtn.setVisibility(View.GONE);
                    if(DriverPermitMaxDays > 0) {
                        nextDateBtn.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(DriverPermitMaxDays > 0) {
                        nextDateBtn.setVisibility(View.VISIBLE);
                        previousDateBtn.setVisibility(View.VISIBLE);
                    }else{
                        nextDateBtn.setVisibility(View.GONE);
                        previousDateBtn.setVisibility(View.GONE);
                    }
                }


    }



    String CheckStringIsNull(JSONObject InspectionObj, String keyValue){
        String value = "00:00";
        try {
            if(!InspectionObj.isNull(keyValue)) {
                value = InspectionObj.getString(keyValue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }


    void ParseRecapData(JSONObject dataObj) {
        SetCertifyListViewHeight();
        try {
            recapList = new ArrayList<RecapModel>();
            if (!dataObj.isNull(ConstantsKeys.CycleDaysDriverLogModel)) {
                JSONArray RecapJsonArray = new JSONArray(dataObj.getString(ConstantsKeys.CycleDaysDriverLogModel));
                try {
                    for (int logCount = 0; logCount < RecapJsonArray.length(); logCount++) {
                        JSONObject logObj   = (JSONObject)RecapJsonArray.get(logCount);
                        String day          = logObj.getString(ConstantsKeys.Day);
                        String date         = logObj.getString(ConstantsKeys.Date);
                        String hourWorked   = logObj.getString(ConstantsKeys.HoursWorked);

                        RecapModel recapModel = new RecapModel(day, date, hourWorked);
                        recapList.add(recapModel);
                    }

                    recapAdapter = new RecapAdapter(getActivity(), recapList);
                    recapHistoryListView.setAdapter(recapAdapter);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                recapAdapter = new RecapAdapter(getActivity(), recapList);
                recapHistoryListView.setAdapter(recapAdapter);
            }
        } catch (Exception e) {  }
    }



    void GetTruckTrailer11(JSONArray selectedArray){
        TruckNo = "";
        TrailerNo = "";


        for(int i = selectedArray.length()-1 ; i >= 0 ; i--){
            try {
                JSONObject itemObj = (JSONObject)selectedArray.get(i);
                String Truck = itemObj.getString(ConstantsKeys.Truck);
                String Trailor = itemObj.getString(ConstantsKeys.Trailor);
                String plate  = itemObj.getString("PlateNumber");

                if(Truck.length() > 0 && !TruckNo.contains(Truck)){
                    if(TruckNo.length() > 0) {
                        TruckNo = TruckNo + ", " + Truck + "/"+ plate;
                    }else{
                        TruckNo = Truck + "/" + plate;
                    }
                }

                if(!Trailor.equals(constants.NoTrailer) && !Trailor.equals("null") ) {
                    if (Trailor.length() > 0 && !TrailerNo.contains(Trailor)) {
                        if (TrailerNo.length() > 0) {
                            TrailerNo = TrailerNo + "," + Trailor;
                        } else {
                            TrailerNo = Trailor;
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        certifyVehicleTV.setText(TruckNo);


        if(LogDate.equals(CurrentDate)){
            if(Globally.TRUCK_NUMBER.length() > 0 && !TruckNo.contains(Globally.TRUCK_NUMBER)){
                if(TruckNo.length() > 0) {
                    TruckNo = TruckNo + "," + Globally.TRUCK_NUMBER;
                }else{
                    TruckNo = Globally.TRUCK_NUMBER;
                }
            }

            if(!Globally.TRAILOR_NUMBER.equals(constants.NoTrailer) && !Globally.TRAILOR_NUMBER.equals("null")) {
                if (Globally.TRAILOR_NUMBER.length() > 0 && !TrailerNo.contains(Globally.TRAILOR_NUMBER)) {
                    if (TrailerNo.length() > 0) {
                        TrailerNo = TrailerNo + "," + Globally.TRAILOR_NUMBER;
                    } else {
                        TrailerNo = Globally.TRAILOR_NUMBER;
                    }
                }
            }
        }



        TrailerNo = TrailerNo.replaceAll(constants.NoTrailer, "");
        certifyTrailerTV.setText(TrailerNo);
        // vinNumberTV.setText("");
        // plateNoTV.setText("");

    }



    void GetTruckTrailer(JSONArray selectedArray){
        TruckNo = "";
        TrailerNo = "";

        ArrayList<String> truckList = new ArrayList<>();
        ArrayList<String> plateList = new ArrayList<>();
        ArrayList<String> trailerList = new ArrayList<>();

        for(int i = selectedArray.length()-1 ; i >= 0 ; i--){
            try {
                JSONObject itemObj = (JSONObject)selectedArray.get(i);
                String Truck = itemObj.getString(ConstantsKeys.Truck);
                String plate  = itemObj.getString("PlateNumber");
                String Trailor = itemObj.getString(ConstantsKeys.Trailor);

                boolean isExistTruck = false;
                for(int tt = 0 ; tt < truckList.size() ; tt++){
                    if(truckList.get(tt).equals(Truck)){
                        isExistTruck = true;
                        break;
                    }
                }

                if(Truck.length() > 0 && !isExistTruck){
                    truckList.add(Truck);
                    plateList.add(plate);
                }

                boolean isExistTrailer = false;
                for(int tr = 0 ; tr < trailerList.size() ; tr++){
                    if(trailerList.get(tr).equals(Trailor)){
                        isExistTrailer = true;
                        break;
                    }
                }

                if(!Trailor.equals(constants.NoTrailer) && !Trailor.equals("null") &&
                        Trailor.length() > 0 && !isExistTrailer){
                    trailerList.add(Trailor);
                }



            /*    if(Truck.length() > 0 && !TruckNo.contains(Truck)){
                    if(TruckNo.length() > 0) {
                        TruckNo = TruckNo + ", " + Truck + "/"+ plate;
                    }else{
                        TruckNo = Truck + "/" + plate;
                    }
                }*/

              /*  if(!Trailor.equals(constants.NoTrailer) && !Trailor.equals("null") ) {
                    if (Trailor.length() > 0 && !TrailerNo.contains(Trailor)) {
                        if (TrailerNo.length() > 0) {
                            TrailerNo = TrailerNo + "," + Trailor;
                        } else {
                            TrailerNo = Trailor;
                        }
                    }
                }*/

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        TruckNo = "";
        for(int aa = 0; aa < truckList.size() ; aa++){
            if(aa == 0){
                TruckNo = truckList.get(aa) + "/" + plateList.get(aa);
            }else{
                TruckNo = TruckNo + ", " + truckList.get(aa) + "/" + plateList.get(aa);
            }
        }
        certifyVehicleTV.setText(TruckNo);


        TrailerNo = "";
        for(int bb = 0; bb < trailerList.size() ; bb++){
            if(bb == 0){
                TrailerNo = trailerList.get(bb);
            }else{
                TrailerNo = TrailerNo + ", " + trailerList.get(bb);
            }
        }


        if(LogDate.equals(CurrentDate)){
            if(Globally.TRUCK_NUMBER.length() > 0 && !TruckNo.contains(Globally.TRUCK_NUMBER)){
                if(TruckNo.length() > 0) {
                    TruckNo = TruckNo + "," + Globally.TRUCK_NUMBER;
                }else{
                    TruckNo = Globally.TRUCK_NUMBER;
                }
            }

            if(!Globally.TRAILOR_NUMBER.equals(constants.NoTrailer) && !Globally.TRAILOR_NUMBER.equals("null")) {
                if (Globally.TRAILOR_NUMBER.length() > 0 && !TrailerNo.contains(Globally.TRAILOR_NUMBER)) {
                    if (TrailerNo.length() > 0) {
                        TrailerNo = TrailerNo + "," + Globally.TRAILOR_NUMBER;
                    } else {
                        TrailerNo = Globally.TRAILOR_NUMBER;
                    }
                }
            }
        }



        TrailerNo = TrailerNo.replaceAll(constants.NoTrailer, "");
        certifyTrailerTV.setText(TrailerNo);

    }


    void SetCertifyListViewHeight(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int DividerHeigh = constants.intToPixel( getActivity(), certifyLogListView.getDividerHeight() );
                    int listSize     = DriverLogList.size() ;
                    if(listSize < 10){
                        listSize    = listSize + 3;
                    }else   if(listSize > 20){
                        listSize = listSize + 5;
                    }else{
                        listSize = listSize + 2;
                    }

                    int DriverLogListHeight      = (certifyLogItemLay.getHeight() + DividerHeigh ) * listSize; //getLayoutParams().height;
                    int RecapTitleHeight         = certifyLogItemLay.getHeight() + RecapViewHeight + recapHistoryListView.getHeight() + (recapItemLay.getHeight() * 5) + 22;


                    int layoutHeight = logHistorylay.getHeight();
                    if(DriverLogListHeight > RecapTitleHeight){
                        logHistorylay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DriverLogListHeight ));
                        setRecapView(layoutHeight, DriverLogListHeight);
                    }else{
                        logHistorylay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RecapTitleHeight ));
                        setRecapView(layoutHeight, RecapTitleHeight);
                    }



                }catch (Exception e){}

            }
        }, 500);

    }


    void setRecapView(int layoutHeight, int currentViewHeight){

        if(recapLayout.getVisibility() == View.VISIBLE) {

            /* ---------- Recap Layout height -------------- */
            int mobileRecapHeight = constants.dpToPx(getActivity(), 300);
            int tabRecapHeight7Inch = constants.dpToPx(getActivity(), 390);
            int tabRecapHeight10Inch = constants.dpToPx(getActivity(), 510);

            LinearLayout.LayoutParams param = null;
            if (global.isTablet(getActivity())) {

                if (displayWidth > 1920) {    // 8 or 10' tablet display
                    if (layoutHeight < tabRecapHeight10Inch) {
                        param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, currentViewHeight);
                    }else{
                        param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
                    }
                } else {  // 7' tablet display
                    if (layoutHeight < tabRecapHeight7Inch) {
                        param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, currentViewHeight);
                    }else{
                        param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
                    }
                }

            } else {
                if (layoutHeight < mobileRecapHeight) {   // Mobile display
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, currentViewHeight);
                }else{
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
                }
            }

            param.weight = 1.5f;
            param.setMargins(constants.dpToPx(getActivity(), 6), 0, 0, 0);
            recapLayout.setLayoutParams(param);

        }
    }


    private void GetCorrectTime(JSONArray driverLogJsonArray, String currentDate, String selectedDate ){

        try {

            if(!currentDate.equals(selectedDate)) {
                for (int i = 0; i < driverLogJsonArray.length(); i++) {
                    JSONObject logObj = (JSONObject) driverLogJsonArray.get(i);
                    int DriverStatus = logObj.getInt(ConstantsKeys.DriverStatusId);

                    switch (DriverStatus) {
                        case EldFragment.OFF_DUTY:
                            TotalOffDutyHours = "24:00";
                            break;
                        case EldFragment.SLEEPER:
                            TotalSleeperBerthHours = "24:00";
                            break;
                        case EldFragment.DRIVING:
                            TotalDrivingHours = "24:00";
                            break;

                        case EldFragment.ON_DUTY:
                            LeftCycleHours = "24:00";
                            break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    void CalculateCycleTime(DateTime currentDateTime, DateTime currentUTCTime, boolean isSingleDriver, boolean isCurrentDate){

        String HoursAvailableToday  = "00:00";
        String HoursWorkedToday     = "00:00";
        List<DriverLog> oDriverLogDetail ;


        if(isCurrentDate){

            currentDateTime     = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);    // Current Date Time
            currentUTCTime      = Globally.getDateTimeObj(Globally.GetCurrentUTCTimeFormat(), true);
            oDriverLogDetail    = hMethods.getSavedLogList(Integer.valueOf(DRIVER_ID), currentDateTime, currentUTCTime, dbHelper);
        }else{
            oDriverLogDetail    = hMethods.getSelectedLogList(Integer.valueOf(DRIVER_ID), currentDateTime, dbHelper);
        }
        int rulesVersion = sharedPref.GetRulesVersion(getActivity());
        boolean isHaulExcptn, isAdverseExcptn;
        if (sharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            isHaulExcptn    = sharedPref.get16hrHaulExcptn(getActivity());
            isAdverseExcptn = sharedPref.getAdverseExcptn(getActivity());
        }else{
            isHaulExcptn    = sharedPref.get16hrHaulExcptnCo(getActivity());
            isAdverseExcptn = sharedPref.getAdverseExcptnCo(getActivity());
        }

        DriverDetail oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DRIVER_ID),
                offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver, DRIVER_JOB_STATUS, isOldRecord,
                isHaulExcptn, isAdverseExcptn, isNorthCanada,
                rulesVersion, oDriverLogDetail);

        // EldFragment.SLEEPER is used because we are just checking cycle time
        RulesResponseObject RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), EldFragment.SLEEPER, oDriverDetail);

        // Calculate 2 days data to get remaining Driving/Onduty hours
        RulesResponseObject RemainingTimeObj = hMethods.getRemainingTime(currentDateTime, currentUTCTime, offsetFromUTC,
                Integer.valueOf(CurrentCycleId), isSingleDriver, Integer.valueOf(DRIVER_ID) , DRIVER_JOB_STATUS, isOldRecord,
                isHaulExcptn, isAdverseExcptn, isNorthCanada,
                rulesVersion, dbHelper);

        try {
            int CycleRemainingMinutes   = constants.checkIntValue((int) RulesObj.getCycleRemainingMinutes());
            int CycleUsedMinutes        = constants.checkIntValue((int) RulesObj.getCycleUsedMinutes());
            int OnDutyRemainingMinutes  = constants.checkIntValue((int) RemainingTimeObj.getOnDutyRemainingMinutes());
            int ShiftUsedMinutes        = constants.checkIntValue((int) RemainingTimeObj.getShiftUsedMinutes());


            if(CycleRemainingMinutes < OnDutyRemainingMinutes){
                OnDutyRemainingMinutes = CycleRemainingMinutes;
            }

            TotalCycleUsedHour      =  Globally.FinalValue(CycleUsedMinutes);
            LeftWeekOnDutyHoursInt  =  Globally.FinalValue(CycleRemainingMinutes);
            HoursAvailableToday     =  Globally.FinalValue(OnDutyRemainingMinutes);
            HoursWorkedToday        =  Globally.FinalValue(ShiftUsedMinutes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        totalCycleHrsTV     .setText( TotalCycleUsedHour);
        leftCycleTV         .setText( LeftWeekOnDutyHoursInt );
        HrsAvailTV          .setText( HoursAvailableToday );
        HrsWorkedTV         .setText( HoursWorkedToday);
        hourAvailableTomoTV .setText( "14:00" );

    }



    void ParseLogData(JSONObject dataObj, boolean isOffline){
        TotalDrivingHours        = "00:00";
        TotalOnDutyHours         = "00:00";
        LeftCycleHours           = "00:00";
        LeftDayDrivingHours      = "00:00";
        TotalOffDutyHours        = "00:00";
        TotalSleeperBerthHours   = "00:00";

        try {
            DriverLogList       = new ArrayList<EldDriverLogModel>();
            OldStatus           = -1;
            htmlAppendedText    = "";
            hLineX1 = 0;    hLineX2 = 0;    hLineY  = 0;
            vLineX  = 0;    vLineY1 = 0;    vLineY2 = 0;

            if(isOffline){

                ParseJsonArray(selectedArray);

                TotalOnDutyHours        = Globally.FinalValue(hMethods.GetOnDutyTime(selectedArray));
                TotalDrivingHours       = Globally.FinalValue(hMethods.GetDrivingTime(selectedArray));
                TotalOffDutyHours       = Globally.FinalValue(hMethods.GetOffDutyTime(selectedArray));
                TotalSleeperBerthHours  = Globally.FinalValue(hMethods.GetSleeperTime(selectedArray));

                if(selectedArray.length() == 1){
                    GetCorrectTime(selectedArray, CurrentDate, LogDate);
                }

                boolean isSingleDriver = false;
                if(sharedPref.getDriverType(getContext()).equals(DriverConst.SingleDriver)){
                    isSingleDriver = true;
                }


               // if(isCertifyLog) {
                    int diff = hMethods.DayDiff(currentDateTime, selectedDateRecap);
                    if(diff == 0){
                        IsCurrentDate = true;
                        nextDateBtn.setVisibility(View.GONE);
                    }else{
                        IsCurrentDate = false;
                    }
              //  }


                if(  LogDate.equals(CurrentDate) ) {    //DayStartLat != 0.0 && CurrentLat != 0.0 &&

                    CalculateCycleTime(selectedDateTime, selectedUtcTime, isSingleDriver, true);

                    EngineMileage =  constants.getOdometersDistance(getActivity());  // new DecimalFormat("##.##").format(distance);
                    certifyDistanceTV.setText(EngineMileage);
                }else {
                    CalculateCycleTime(selectedDateTime, selectedUtcTime, isSingleDriver, false);
                    certifyDistanceTV.setText("--");
                }

                if(isExceptionEnabledForDay){
                    certifyExcptnTV.setVisibility(View.VISIBLE);
                }else{
                    certifyExcptnTV.setVisibility(View.GONE);
                }

                try{
                   // int diff = hMethods.DayDiff(currentDateTime, selectedDateRecap);
                    LogInfoAdapter = new DriverLogInfoAdapter(getActivity(), DriverLogList, StateArrayList, StateList,
                            DriverType, IsEditLocation, diff, Integer.valueOf(DRIVER_ID), IsCurrentDate,
                            isExceptionEnabledForDay, driverLogArray, selectedDateTime , selectedDateTime,
                            offsetFromUTC, dbHelper, hMethods );
                    certifyLogListView.setAdapter(LogInfoAdapter);
                }catch (Exception e){  }


                String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
                ReloadWebView(CloseTag);


            }else {
                if (!dataObj.isNull("DriverLogModel")) {
                    selectedArray = new JSONArray(dataObj.getString("DriverLogModel"));
                    ParseJsonArray(selectedArray);

                    JSONObject SPJson = new JSONObject(dataObj.getString("oDriverTripTruckTrailorDetail_SP"));


                    if(!dataObj.isNull("LogSignImage")) {
                        if(dataObj.getString("LogSignImage").length() > 0) {

                            LogSignImage = dataObj.getString("LogSignImage");

                            if (OfflineByteImg.length() == 0 && dataObj.getString(ConstantsKeys.LogSignImageInByte).length() > 0) {
                                // Update recap array with byte image
                                isReCertifyRequired = constants.isReCertifyRequired(getActivity(), dataObj, "");
                                if (isReCertifyRequired == false) {
                                    recap18DaysArray = recapViewMethod.UpdateSelectedDateRecapArray(recap18DaysArray, LogDate, dataObj.getString(ConstantsKeys.LogSignImageInByte));
                                    recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, recap18DaysArray);

                                    ImageLoader.getInstance().displayImage(LogSignImage, signImageView, options);
                                    CheckSignatureVisibilityStatus(selectedArray);

                                } else {
                                    GetRecapViewOffLineData();
                                }
                            }



                        }
                    }

                    // certifyCarrierTV
                    NullCheckJson(dataObj, certifyCoDriverNameTV, ConstantsKeys.CoDriverName, "N/A");
                    NullCheckJson(dataObj, certifyCarrierTV, ConstantsKeys.CarrierName, Carrier);
                    NullCheckJson(dataObj, certifyDistanceTV, ConstantsKeys.EngineMileage, "0");
                    NullCheckJson(SPJson, certifyVehicleTV, ConstantsKeys.Truck, TruckNo);
                    PlateNumber  = SPJson.getString(ConstantsKeys.PlateNumber);



                    GetTruckTrailer(selectedArray);

                    if(!SPJson.isNull("Trailer")) {
                        TrailerNo = SPJson.getString("Trailer");
                        TrailerNo =  TrailerNo.replaceAll(constants.NoTrailer, "");
                    }

                    certifyTrailerTV.setText(TrailerNo);

                    EngineMileage = certifyDistanceTV.getText().toString().trim();
                    if(!dataObj.isNull("Remarks"))
                        Remarks             = dataObj.getString("Remarks");

                    Distance            = dataObj.getString("TotalDistance");
                    String[] DistanceArray = Distance.split("\\.");
                    if(DistanceArray.length > 1){
                        if(DistanceArray[1].length() > 2){
                            Distance = DistanceArray[0] + "." + DistanceArray[1].substring(0, 2) + " km";
                        }
                    }

                    if(EngineMileage.contains("Data Malfunction") && EngineMileage.equals("0") ){
                        certifyDistanceTV.setText(Distance);
                    }else{
                        // Update recap array with Engine Miles
                        recap18DaysArray = recapViewMethod.UpdateSelectedDateEngineMiles(recap18DaysArray, LogDate, EngineMileage);
                        recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, recap18DaysArray);
                    }


                    certifyRemarksTV.setText(Remarks);

                    // setTruckOnView();


                }

                if(isExceptionEnabledForDay){
                    certifyExcptnTV.setVisibility(View.VISIBLE);
                }else{
                    certifyExcptnTV.setVisibility(View.GONE);
                }

                try{
                    int diff = hMethods.DayDiff(currentDateTime, selectedDateRecap);
                    LogInfoAdapter = new DriverLogInfoAdapter(getActivity(), DriverLogList, StateArrayList, StateList, DriverType,
                            IsEditLocation, diff, Integer.valueOf(DRIVER_ID), IsCurrentDate, isExceptionEnabledForDay, driverLogArray,
                            selectedDateTime , selectedDateTime, offsetFromUTC, dbHelper, hMethods );
                    certifyLogListView.setAdapter(LogInfoAdapter);
                }catch (Exception e){  }


            }



        }catch (Exception e){
            e.printStackTrace();
            ReloadWebView(constants.HtmlCloseTag("00:00", "00:00", "00:00", "00:00"));
        }

    }



/*

    private void setTruckOnView(){
        String truckPlate = "";

        try{
            String[] truckArray = TruckNo.split(",");
            String[] plateArray = PlateNumber.split(",");

            for(int i = 0 ; i < truckArray.length ; i++){
                String plateNumber = "";
                if(plateArray.length > i){
                    plateNumber = plateArray[i];
                }
                if(i > 0){
                    truckPlate = truckPlate + ", " +truckArray[i] + "/" + plateNumber;
                }else{
                    truckPlate = truckArray[i] + "/" + plateNumber;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        certifyVehicleTV.setText(truckPlate);
    }

*/




    private void setMarqueonView(final TextView textView){

                textView.setHorizontallyScrolling(true);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                textView.setSingleLine(true);
                textView.setMarqueeRepeatLimit(-1);
                textView.setSelected(true);


    }

    void ParseJsonArray(JSONArray driverLogJsonArray){
        try{
            isExceptionEnabledForDay = false;

            for(int logCount = 0 ; logCount < driverLogJsonArray.length() ; logCount ++) {
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(logCount);
                DRIVER_JOB_STATUS = logObj.getInt(ConstantsKeys.DriverStatusId);
                String startDateTime = logObj.getString(ConstantsKeys.startDateTime);
                String endDateTime = logObj.getString(ConstantsKeys.endDateTime);
                String totalHours = logObj.getString(ConstantsKeys.TotalHours);
                String currentCycleId = logObj.getString(ConstantsKeys.CurrentCycleId);
                String UTCStartDateTime = logObj.getString(ConstantsKeys.startDateTime);  //UTCStartDateTime
                String UTCEndDateTime = logObj.getString(ConstantsKeys.endDateTime);  //UTCEndDateTime
                boolean isPersonal = false;
                boolean YardMove = false;
                boolean IsAdverseException = false;
                boolean IsShortHaulException = false;

                if (!logObj.isNull(ConstantsKeys.Personal))
                    isPersonal = logObj.getBoolean(ConstantsKeys.Personal);

                if(!logObj.isNull(ConstantsKeys.YardMove))
                    YardMove = logObj.getBoolean(ConstantsKeys.YardMove);


                if(!logObj.getString(ConstantsKeys.IsAdverseException).equals("null"))
                    IsAdverseException = logObj.getBoolean(ConstantsKeys.IsAdverseException);

                if(!logObj.getString(ConstantsKeys.IsShortHaulException).equals("null"))
                    IsShortHaulException = logObj.getBoolean(ConstantsKeys.IsShortHaulException);


                if(IsAdverseException || IsShortHaulException){
                    isExceptionEnabledForDay = true;
                }

                if(DRIVER_JOB_STATUS == 3 || DRIVER_JOB_STATUS == 4) {
                    if (!logObj.isNull(ConstantsKeys.IsViolation)) {
                        isViolation = logObj.getBoolean(ConstantsKeys.IsViolation);
                    } else {
                        isViolation = false;
                    }
                }else{
                    isViolation = false;
                }

                String Duration = "", Location = "", remarks = "", LocationKm = "";

                if (logObj.has(ConstantsKeys.Duration))
                    Duration = logObj.getString(ConstantsKeys.Duration);
                else {
                    Duration = logObj.getString(ConstantsKeys.TotalHours);
                    Duration = global.FinalValue(Integer.valueOf(Duration));
                }

                Location = logObj.getString(ConstantsKeys.StartLocation);
                Location = checkLocationData(Location);

                LocationKm = logObj.getString(ConstantsKeys.StartLocationKm);
                LocationKm = checkLocationData(LocationKm);

                remarks          = logObj.getString(ConstantsKeys.Remarks);


                if(logCount > 0 && logCount == driverLogJsonArray.length()-1 ) {
                    if(  LogDate.equals(CurrentDate) ) {
                        endDateTime = Globally.GetCurrentDateTime();
                    }else{
                        if(endDateTime.length() > 16 && endDateTime.substring(11,16).equals("00:00")){
                            endDateTime = endDateTime.substring(0,11) + "23:59" +endDateTime.substring(16,endDateTime.length());
                        }
                    }
                    DateTime startDateT = Globally.getDateTimeObj(startDateTime, false);
                    DateTime endDateT = Globally.getDateTimeObj(endDateTime, false);
                    int minDiff = endDateT.getMinuteOfDay() - startDateT.getMinuteOfDay();
                    Duration = global.FinalValue(minDiff);
                }

                EldDriverLogModel driverLogModel;
                if(DRIVER_JOB_STATUS == constants.ON_DUTY) {
                    driverLogModel = new EldDriverLogModel(DRIVER_JOB_STATUS, startDateTime, endDateTime, totalHours, currentCycleId,
                            isViolation, UTCStartDateTime, UTCEndDateTime, Duration, Location, LocationKm, remarks, YardMove,
                            IsAdverseException, IsShortHaulException, logObj.getString(ConstantsKeys.StartLatitude),
                            logObj.getString(ConstantsKeys.StartLongitude) );
                }else{
                    driverLogModel = new EldDriverLogModel(DRIVER_JOB_STATUS, startDateTime, endDateTime, totalHours, currentCycleId,
                            isViolation, UTCStartDateTime, UTCEndDateTime, Duration, Location, LocationKm, remarks, isPersonal,
                            IsAdverseException, IsShortHaulException, logObj.getString(ConstantsKeys.StartLatitude),
                            logObj.getString(ConstantsKeys.StartLongitude));
                }
                DriverLogList.add(driverLogModel);


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(global.DateFormat);  //:SSSZ
                Date startDate = new Date();
                Date endDate = new Date();
                startHour = 0; startMin = 0; endHour = 0; endMin = 0;

                try {
                    startDate   = simpleDateFormat.parse(startDateTime);
                    endDate     = simpleDateFormat.parse(endDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startHour   = startDate.getHours() * 60;
                startMin    = startDate.getMinutes();
                endHour     = endDate.getHours() * 60;
                endMin      = endDate.getMinutes();

                if( driverLogJsonArray.length() == 1) {
                    long diff = global.DateDifference(startDate, endDate);
                    if (diff > 0 && endDate.getHours() == 0) {
                        endHour = 24 * 60;
                    }
                }

                hLineX1 =   startHour + startMin;
                hLineX2 =   endHour + endMin;

                int VerticalLineX = constants.VerticalLine(OldStatus);
                int VerticalLineY = constants.VerticalLine(DRIVER_JOB_STATUS);

                if(hLineX2 > hLineX1) {
                    boolean isDottedLine = false;
                    if(YardMove || isPersonal){
                        isDottedLine = true;
                    }

                    if (OldStatus != -1) {
                        DrawGraph(hLineX1, hLineX2, VerticalLineX, VerticalLineY, isViolation, isDottedLine);
                    } else {
                        DrawGraph(hLineX1, hLineX2, VerticalLineY, VerticalLineY, isViolation, isDottedLine);
                    }
                }
                OldStatus   =   DRIVER_JOB_STATUS;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    String checkLocationData(String Location){
        if(Location.equals(", , ") || Location.equals(" , ")){
            Location = "No Location Found";
        }
        return Location;
    }

    void NullCheckJson(JSONObject json, TextView view, String key, String value){
        try {
            if(!json.isNull(key)) {
                if(key.equals(ConstantsKeys.Truck)){
                    TruckNo = json.getString(key);
                }
                view.setText(json.getString(key));
            }else{
                view.setText(value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void DrawGraph(int hLineX1, int hLineX2, int vLineY1, int vLineY2, boolean isViolation, boolean isDottedLine){
        if(isViolation ){   //&& !isDOT
            htmlAppendedText = htmlAppendedText + ViolationLine +
                    constants.AddHorizontalLine(hLineX1, hLineX2, vLineY2) +
                    constants.AddVerticalLineViolation(hLineX1, vLineY1, vLineY2) +
                    "                  </g>\n";
        }else {
            if(isDottedLine){
                htmlAppendedText = htmlAppendedText + DefaultLine +
                        constants.AddHorizontalDottedLine(hLineX1, hLineX2, vLineY2) +
                        constants.AddVerticalLine(hLineX1, vLineY1, vLineY2) +
                        "                  </g>\n";
            }else {
                htmlAppendedText = htmlAppendedText + DefaultLine +
                        constants.AddHorizontalLine(hLineX1, hLineX2, vLineY2) +
                        constants.AddVerticalLine(hLineX1, vLineY1, vLineY2) +
                        "                  </g>\n";
            }
        }
    }


    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        // We take the last son in the scrollview
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

        // if diff is zero, then the bottom has been reached
        if (diff < 100) {

            if(!IsScrollEnd) {
                IsScrollEnd = true;
                //   isCertifyViewAgain
                try {
                    if (LogSignImageInByte.length() > 0) {
                        saveSignatureBtn.setVisibility(View.GONE);


                        if(isLoadImageCalled){
                            loadByteImageWithHandler();
                        }else{
                            if(isCertifyViewAgain){
                                loadByteImageWithHandler();
                            }
                        }

                    } else {
                        ImageLoader.getInstance().displayImage(LogSignImage, signImageView, options);
                    }

                    CheckSignatureVisibilityStatus(selectedArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void loadByteImageWithHandler(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadByteImage(LogSignImageInByte);
            }
        }, 400);

    }


    private class SignRecapListener implements SignRecordDialog.DateSelectListener{


        @Override
        public void SignOkBtn(DateTime dateTime, boolean IsSigned) {

            if(signRecordDialog != null && signRecordDialog.isShowing())
                signRecordDialog.dismiss();

            selectedDateTime = dateTime;
            selectedDateRecap   = selectedDateTime;
            int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
            DOTBtnVisibility(DaysDiff, MaxDays);
            String[] dateMonth = global.dateConversionMMMM_ddd_dd(dateTime.toString()).split(",");
            String  SelectedDate = global.ConvertDateFormatMMddyyyy(dateTime.toString());

            if(dateMonth.length > 1) {
                String monthFullName = dateMonth[0];
                String monthShortName = dateMonth[1];
                String dayOfTheWeek = dateMonth[2];

                GetLogWithDate(SelectedDate, dayOfTheWeek, monthFullName, monthShortName);
                openMissingDialogAlert();
            }


        }
    }


    /*================== Signature Listener ====================*/
    private class SignListener implements SignDialog.SignListener{

        @Override
        public void SignOkBtn(InkView inkView, boolean IsSigned) {

            try {
                if (signDialog != null) {
                    if (IsSigned) {
                        SignatureMainLay.setVisibility(View.VISIBLE);
                        signLogTitle2.setVisibility(View.GONE);
                        signImageView.setBackground(null);
                        signImageView.setImageResource(R.drawable.transparent);
                        imagePath = constants.GetSignatureBitmap(inkView, signImageView, getActivity());
                        saveSignatureBtn.setText(getString(R.string.save_and_certify));

                        driverLogScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                driverLogScrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });

                        certifyListenerCall();

                    } else {
                        imagePath = "";
                        signImageView.setBackground(null);
                        signImageView.setImageResource(R.drawable.sign_here);
                        saveSignatureBtn.setText(getString(R.string.certify));
                        signLogTitle2.setVisibility(View.GONE);  //signLogTitle2.setVisibility(View.VISIBLE);
                        SignatureMainLay.setVisibility(View.GONE);
                    }

                    signDialog.dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    private class DateListener implements DatePickerDialog.DatePickerListener{
        @Override
        public void JobBtnReady(String SelectedDate, String dayOfTheWeek, String monthFullName, String monthShortName, int dayOfMonth) {

            IsScrollEnd = false;
            dateDialog.dismiss();
            editLogBtn.setVisibility(View.GONE);
            IsEditBtnVisible = false;


            String selectedDateTimeStr = global.ConvertDateFormat(SelectedDate);   //2020-06-09T00:00:00.000Z
            selectedDateTime = global.getDateTimeObj(selectedDateTimeStr, false);
            selectedDateRecap   = selectedDateTime;
            int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);

            DOTBtnVisibility(DaysDiff, MaxDays);
            GetLogWithDate(SelectedDate, dayOfTheWeek, monthFullName, monthShortName);
            openMissingDialogAlert();

        }
    }


    void GetLogWithDate(String SelectedDate, String dayOfTheWeek, String monthFullName, String monthShortName){

        nextDateBtn.setEnabled(false);
        previousDateBtn.setEnabled(false);

        LogDate = SelectedDate;
        DayName = dayOfTheWeek;
        MonthFullName = monthFullName;
        MonthShortName = monthShortName;
        if(LogDate.split("/").length > 1) {
            EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " ( " + DayName + " )");
        }

       // if(!isDOT) {
            if(LogDate.length() > 3)
                certifyDateTV.setText(MonthFullName + " " + LogDate.substring(3, LogDate.length()));
            else
                certifyDateTV.setText(MonthFullName + " " + LogDate);
       // }

        LogSignImage = ""; LogSignImageInByte = "";

        CheckSelectedDateTime();
        GET_CERTIFY_LOG();
        CheckSignatureVisibilityStatus(selectedArray);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextDateBtn.setEnabled(true);
                previousDateBtn.setEnabled(true);

            }
        }, 500);

    }

    void CheckSignatureVisibilityStatus(JSONArray LogJsonArray){
        try {
            if(getActivity() != null) {
                isCertifySignExist = constants.isCertifySignExist(recapViewMethod, DRIVER_ID, dbHelper);
                if (!LogDate.equals(CurrentDate) && LogJsonArray.length() > 0) {

                    if (isCertifySignExist) {
                        SignatureMainLay.setVisibility(View.GONE);
                        signLogTitle2.setVisibility(View.GONE);  //signLogTitle2.setVisibility(View.VISIBLE);
                        saveSignatureBtn.setText(getString(R.string.certify));
                    } else {
                        SignatureMainLay.setVisibility(View.VISIBLE);
                        signLogTitle2.setVisibility(View.GONE);
                        saveSignatureBtn.setText(getString(R.string.save_and_certify));
                    }

                    if (LogSignImage.length() > 0 || LogSignImageInByte.length() > 0) {
                        saveSignatureBtn.setVisibility(View.GONE);
                        signLogTitle2.setVisibility(View.GONE);
                        SignatureMainLay.setVisibility(View.VISIBLE);
                    } else {

                        signImageView.setBackground(null);
                        signImageView.setImageResource(R.drawable.sign_here);
                        saveSignatureBtn.setVisibility(View.VISIBLE);
                        if (isCertifySignExist) {
                            SignatureMainLay.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (DriverPermitMaxDays == 0) {

                        if (isCertifySignExist) {
                            SignatureMainLay.setVisibility(View.GONE);
                            signLogTitle2.setVisibility(View.GONE);  //signLogTitle2.setVisibility(View.VISIBLE);
                            saveSignatureBtn.setText(getString(R.string.certify));
                        } else {
                            SignatureMainLay.setVisibility(View.VISIBLE);
                            signLogTitle2.setVisibility(View.GONE);
                            saveSignatureBtn.setText(getString(R.string.save_and_certify));
                        }

                        saveSignatureBtn.setVisibility(View.VISIBLE);
                        if (LogSignImage.length() == 0 && LogSignImageInByte.length() == 0) {

                            signImageView.setBackground(null);
                            signImageView.setImageResource(R.drawable.sign_here);
                        }

                    } else {
                        SignatureMainLay.setVisibility(View.GONE);
                        saveSignatureBtn.setVisibility(View.GONE);
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void ConfirmLoginDialog(String userType){
        MainDriverName = DriverConst.GetDriverLoginDetails( DriverConst.UserName, getActivity());
        MainDriverPass = DriverConst.GetDriverLoginDetails( DriverConst.Passsword, getActivity());

        CoDriverName = DriverConst.GetCoDriverLoginDetails( DriverConst.CoUserName, getActivity());
        CoDriverPass = DriverConst.GetCoDriverLoginDetails( DriverConst.CoPasssword, getActivity());

        loginDialog = new LoginDialog(getActivity() ,
                userType,
                "certify",
                MainDriverName,
                CoDriverName, new LoginListener());
        loginDialog.show();
    }



    private void MoveFragment(String date ){
        InspectionsHistoryFragment savedInspectionFragment = new InspectionsHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("inspection_type", "pti");

        savedInspectionFragment.setArguments(bundle);

        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, savedInspectionFragment);
        fragmentTran.addToBackStack("inspection");
        fragmentTran.commit();


    }



    //*================== Get Driver Log last 18 days ===================*//*
    void GetDriverLog18Days(final String DriverId, final String DeviceId, final String UtcDate){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.ProjectId, "1");
        params.put(ConstantsKeys.UTCDateTime, UtcDate);

        GetLog18DaysRequest.executeRequest(Request.Method.POST, APIs.GET_DRIVER_LOG_18_DAYS, params, GetDriverLog18Days,
                Constants.SocketTimeout30Sec, ResponseCallBack, ErrorCallBack);

    }

    //*================== Get Shipmen Reading ===================*//*
    void GetShipmentDetails(final String DriverId, final String DeviceId, final String date ){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.ShippingDocDate, date);

        GetShipmentRequest.executeRequest(Request.Method.POST, APIs.GET_SHIPPING_DOC_NUMBER, params, GET_SHIPMENT,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }

    //*================== Get Odometer Reading ===================*//*
    void GetOdometerReading(final String DriverId, final String DeviceId, final String VIN,
                            final String CreatedDate, final String CompanyId ){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.VIN, VIN );
        params.put(ConstantsKeys.CreatedDate, CreatedDate);
        params.put(ConstantsKeys.IsCertifyLog, "true");
        params.put(ConstantsKeys.CompanyId, CompanyId );

        GetOdometerRequest.executeRequest(Request.Method.POST, APIs.GET_ODOMETER, params, GetOdometer,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

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
        params.put(ConstantsKeys.VIN, SharedPref.getVINNumber(getActivity()));
        params.put(ConstantsKeys.CompanyId, DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity()) );
        params.put(ConstantsKeys.Country, Country );

        GetPermissions.executeRequest(Request.Method.POST, APIs.GET_DRIVER_STATUS_PERMISSION, params, GetDriverPermission,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Re-Certify Records info ===================*//*
    void GetReCertifyRecords(final String DriverId, final String FromDate, final String ToDate ){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.FromDate, FromDate );
        params.put(ConstantsKeys.ToDate, ToDate );

        GetReCertifyRequest.executeRequest(Request.Method.POST, APIs.GET_RECERTIFY_PENDING_RECORDS, params, GetReCertifyRecords,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }



    /* ================== Get Driver Details =================== */
    void GET_DRIVER_DETAILS(final String ProjectId, final String DriverId, final String date) {

        if(nextPrevBtnClicked) {
            progressDialog.show();
        }
        nextPrevBtnClicked = false;

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.ProjectId, ProjectId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.ELDSearchDate, date);
        params.put(ConstantsKeys.TeamDriverType, TeamDriverType);

        GetLogRequest.executeRequest(Request.Method.POST, APIs.GET_DRIVER_STATUS, params, GetDriverLog,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }


    /*================== Get Shipping Document Number ===================*/
    void GetShipment18Days(final String DriverId, final String DeviceId, final String ShippingDocDate, int flag){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.ShippingDocDate, ShippingDocDate);

        GetShippingRequest.executeRequest(Request.Method.POST, APIs.GET_SHIPPING_INFO_OFFLINE , params, flag,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }

    //*================== Get Odometer 18 Days data ===================*//*
    void GetOdometer18Days(final String DriverId, final String DeviceId, final String CompanyId , final String CreatedDate){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
         params.put(ConstantsKeys.CompanyId, CompanyId  );
        params.put(ConstantsKeys.CreatedDate, CreatedDate);

        GetOdometerRequest.executeRequest(Request.Method.POST, APIs.GET_ODOMETER_OFFLINE , params, GetOdometers18Days,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Driver's 18 Days Recap View Log Data ===================*//*
    void GetRecapView18DaysData(final String DriverId, final String DeviceId,
                                String StartDate, String EndDate ){

        IsRecapApiCalled = true;

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
         params.put(ConstantsKeys.ProjectId, Globally.PROJECT_ID);
        params.put(ConstantsKeys.DrivingStartTime, StartDate);
        params.put(ConstantsKeys.DriverLogDate, EndDate );

        GetRecapView18DaysData.executeRequest(Request.Method.POST, APIs.GET_DRIVER_LOG_18_DAYS_DETAILS , params, GetRecapViewData,
                Constants.SocketTimeout50Sec, ResponseCallBack, ErrorCallBack);
    }



    private void saveByteSignLocally(String SignImageInBytes){
        // Add signed parameters with values into the json object and put into the json Array.
        JSONObject CertifyLogObj = certifyLogMethod.AddCertifyLogArray(DRIVER_ID, DeviceId, global.PROJECT_ID, LogDate,
                SignImageInBytes, IsContinueWithSign, isReCertifyRequired, CompanyId, sharedPref.getLocationEventType(getActivity())  );
        CertifyLogArray.put(CertifyLogObj);

        // Insert/Update Certify Log table
        certifyLogMethod.CertifyLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, CertifyLogArray );

        // Update recap array with byte image
        recap18DaysArray = recapViewMethod.UpdateSelectedDateRecapArray(recap18DaysArray, LogDate, SignImageInBytes);
        recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, recap18DaysArray);

    }



    private void SaveDriverSignArray(){
        isLoadImageCalled = false;

        try {
            if (IsContinueWithSign) {

                String lastSignature = constants.getLastSignature(recapViewMethod, DRIVER_ID, dbHelper);
                saveByteSignLocally(lastSignature);
                loadByteImage(lastSignature);
                LogSignImageInByte = lastSignature;
                isLoadImageCalled = true;

                if (Globally.isConnected(getActivity())) {
                    progressDialog.show();
                    saveSignatureBtn.setEnabled(false);
                    saveCertifyLogPost.PostDriverLogData(CertifyLogArray, APIs.CERTIFY_LOG_OFFLINE, constants.SocketTimeout10Sec,
                            true, false, DriverType, SaveCertifyLog);
                } else {
                    saveSignatureBtn.setVisibility(View.GONE);
                    SignatureMainLay.setVisibility(View.VISIBLE);
                    signLogTitle2.setVisibility(View.GONE);
                    global.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.certify_log_offline_saved), getResources().getColor(R.color.colorSleeper));

                    if (isCertifyViewAgain) {
                        isCertifyViewAgain = false;
                        GetRecapViewOffLineData();
                        loadByteImageWithHandler();
                    }
                }
                IsContinueWithSign = false;
                saveSignatureBtn.setText(getString(R.string.certify));

            } else {
                File f = new File(imagePath);
                if (f.exists()) {

                    // Convert image file into bytes
                    LogSignImageInByte = global.ConvertImageToByteAsString(imagePath);
                    saveByteSignLocally(LogSignImageInByte);

                    if (Globally.isConnected(getActivity())) {
                        progressDialog.show();
                        saveSignatureBtn.setEnabled(false);
                        saveCertifyLogPost.PostDriverLogData(CertifyLogArray, APIs.CERTIFY_LOG_OFFLINE, constants.SocketTimeout10Sec, true, false, DriverType, SaveCertifyLog);
                    } else {
                        saveSignatureBtn.setVisibility(View.GONE);
                        SignatureMainLay.setVisibility(View.VISIBLE);
                        signLogTitle2.setVisibility(View.GONE);
                        global.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.certify_log_offline_saved), getResources().getColor(R.color.colorSleeper));
                    }
                    imagePath = "";
                    saveSignatureBtn.setText(getString(R.string.certify));

                } else {
                    signImageView.setBackground(null);
                    signImageView.setImageResource(R.drawable.sign_here);
                    imagePath = "";
                    global.EldScreenToast(eldMenuBtn, getResources().getString(R.string.sign_not_valid), getResources().getColor(R.color.colorVoilation));
                }
            }

            refreshPendingSignView();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void refreshPendingSignView(){
        boolean isSignPending           = constants.GetCertifyLogSignStatus(recapViewMethod, DRIVER_ID, dbHelper, global.GetCurrentDeviceDate(), CurrentCycleId, logPermissionObj);
        List<RecapSignModel> signList   = constants.GetCertifySignList(recapViewMethod, DRIVER_ID, hMethods, dbHelper,
                                            global.GetCurrentDeviceDate(), CurrentCycleId, logPermissionObj, global, getActivity());
        boolean isMissingLoc = false;
        for(int i = 0 ; i < signList.size() ; i++){
            if(signList.get(i).isMissingLocation()){
                isMissingLoc = true;
                break;
            }
        }
        if(isSignPending || isMissingLoc) {
            certifyErrorImgView.setVisibility(View.VISIBLE);
        }else{
            certifyErrorImgView.setVisibility(View.GONE);
        }
    }


    void openMissingDialogAlert(){
        try {
            driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
            boolean isLocationMissing = constants.isLocationMissingSelectedDay (selectedDateTime, currentDateTime, driverLogArray, hMethods,
                    global, getActivity());

            if(isLocationMissing) {
                if (missingLocationDialog != null && missingLocationDialog.isShowing()) {
                    missingLocationDialog.dismiss();
                }

                missingLocationDialog = new MissingLocationDialog(getActivity(), new ChangeLocListener());
                missingLocationDialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private class ChangeLocListener implements MissingLocationDialog.ChangeLocationListener{

        @Override
        public void AddLocationListener() {

            try {
                driverLogScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        driverLogScrollView.smoothScrollTo(0, certifyLogItemLay.getBottom() * 12);
                    }
                });
            }catch (Exception e){e.printStackTrace();}

        }
    }


    private class LoginListener implements LoginDialog.LoginListener{


        @Override
        public void CancelReady() {
            if(loginDialog != null)
                loginDialog.dismiss();
        }

        @Override
        public void LoginBtnReady(String UserType, String userName, String Password, EditText UsernameEditText, EditText PasswordEditText) {

            if(Password.length() > 0) {
                if(UserType.equals("main_driver")){
                    if(userName.equals(MainDriverName) && Password.equals(MainDriverPass)){
                        Globally.hideKeyboardView(getActivity(), PasswordEditText);

                        if(loginDialog != null)
                            loginDialog.dismiss();

                        MoveToEditPage();

                    }else{
                        Globally.EldScreenToast(UsernameEditText, "Incorrect Password", eldWarningColor);
                    }
                }else{
                    if(userName.equals(CoDriverName) && Password.equals(CoDriverPass)){
                        Globally.hideKeyboardView(getActivity(), PasswordEditText);

                        if(loginDialog != null)
                            loginDialog.dismiss();

                        MoveToEditPage();

                    }else{
                        Globally.EldScreenToast(UsernameEditText, "Incorrect Password", eldWarningColor);
                    }
                }
            }else{
                Globally.EldScreenToast(UsernameEditText, "Please enter password", eldWarningColor);
            }

        }
    }


    void MoveToEditPage(){
        if (isTrueAnyPermission) {
            IsEditBtnVisible = false;
            Constants.LogDate = LogDate;
            Constants.DayName = DayName;
            Constants.MonthFullName = MonthFullName;
            Constants.MonthShortName = MonthShortName;

            FragmentManager fragManager = getActivity().getSupportFragmentManager();
            EditLogFragment editLogFragment = new EditLogFragment();   //EditLogFragment
            Bundle bundle = new Bundle();
            bundle.putString("screen_type", "edit_log");
            bundle.putString("link", "");
            bundle.putString("device_id", DeviceId);
            bundle.putString("vehicle_id", VehicleId);
            bundle.putString("cycleId", CurrentCycleId);
            bundle.putInt("driver_type", DriverType);

            bundle.putString("selectedDate", selectedDateRecap.toString());    //Globally.GetCurrentDateTime()
            bundle.putString("selectedUtcDate", Globally.getDateTimeObj(selectedDateRecap.minusHours(offsetFromUTC).toString(), false).toString());
            bundle.putInt("offsetFromUTC", offsetFromUTC);
            bundle.putString("permissions", logPermissionObj.toString());
            bundle.putBoolean("permission_response", isPermissionResponse);
            bundle.putBoolean("isCurrentDate", IsCurrentDate);
            editLogFragment.setArguments(bundle);

            FragmentTransaction fragmentTran = fragManager.beginTransaction();
            fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTran.replace(R.id.job_fragment, editLogFragment);
            fragmentTran.addToBackStack(null);
            fragmentTran.commit();
        } else {
            global.EldScreenToast(eldMenuBtn, "You don't have edit permission to edit your log. Please contact to your company.", Color.parseColor(colorVoilation));
        }

    }


    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveCertifyResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, int inputDataLength) {
            Log.d("signatureLog", "---Response: " + response);
            progressBarDriverLog.setVisibility(View.GONE);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            saveSignatureBtn.setEnabled(true);

            try {
                JSONObject obj = new JSONObject(response);

                if (obj.getString("Status").equals("true")) {
                    // Clear unsent Shipping Log from db
                    CertifyLogArray = new JSONArray();
                    certifyLogMethod.CertifyLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, CertifyLogArray );

                }

                if(flag == SaveCertifyLog){
                    Globally.EldScreenToast(previousDateBtn, getString(R.string.has_been_certified),
                            getResources().getColor(R.color.colorPrimary));
                    signLogTitle2.setVisibility(View.GONE);
                    saveSignatureBtn.setVisibility(View.GONE);
                    SignatureMainLay.setVisibility(View.VISIBLE);

                    if(isLoadImageCalled){
                        loadByteImageWithHandler();
                    }

                }

                if(isCertifyViewAgain){
                    isCertifyViewAgain = false;
                    GetRecapViewOffLineData();
                }

            }catch (Exception e){
                saveSignatureBtn.setEnabled(true);

                if(isCertifyViewAgain){
                    isCertifyViewAgain = false;
                    GetRecapViewOffLineData();
                }

                e.printStackTrace();
            }

        }

        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("errorrr ", ">>>error dialog: ");

            if(getActivity() != null) {
                try {
                    saveSignatureBtn.setEnabled(true);
                    progressBarDriverLog.setVisibility(View.GONE);
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (flag == SaveCertifyLog) {
                        global.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.certify_log_offline_saved), getResources().getColor(R.color.colorSleeper));
                        signLogTitle2.setVisibility(View.GONE);
                        saveSignatureBtn.setVisibility(View.GONE);
                        SignatureMainLay.setVisibility(View.VISIBLE);

                        if(isLoadImageCalled){
                            loadByteImageWithHandler();
                        }


                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };



    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null, dataObj = null;
            String status = "";

            if(getActivity() != null){

                try{


                    try {
                        obj = new JSONObject(response);
                        status = obj.getString("Status");
                        if (!obj.isNull("Data")) {
                            dataObj = new JSONObject(obj.getString("Data"));
                        }

                    } catch (JSONException e) {  }

                    if(status.equalsIgnoreCase("true")){

                        switch (flag){

                            case GetDriverLog:

                                scrollX = driverLogScrollView.getScrollX();
                                scrollY = driverLogScrollView.getScrollY();

                                GetShipmentDetails(DRIVER_ID, DeviceId, LogDate);
                                GetOdometerReading(DRIVER_ID, DeviceId, VIN_NUMBER, LogDate, CompanyId );



                                try {
                                    isViolation = false;
                                    imagePath = "";
                                    if(!obj.isNull("Data")) {
                                        dataObj = new JSONObject(obj.getString("Data"));

                                        JSONArray unPostedLogArray = constants.GetDriversSavedArray(getActivity(),
                                                    MainDriverPref, CoDriverPref);

                                        String logModel = dataObj.getString("DriverLogModel");

                                        if(unPostedLogArray.length() == 0){
                                            if(selectedArray.length() != new JSONArray(logModel).length()){
                                                GetDriverLog18Days(DRIVER_ID, DeviceId, Globally.GetCurrentUTCDate());
                                            }
                                        }
                                        selectedArray = new JSONArray(logModel);
                                        ParseLogData(dataObj, false);     // Parse Log Data

                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                progressBarDriverLog.setVisibility(View.GONE);
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }

                                break;


                            case GetOdometer:
                                try{
                                    //  Log.d("response", "response Get odometer: " + response);
                                    JSONArray odometerJsonArray = new JSONArray();

                                    if(dataObj.has("ListTruckOdometer")) {
                                        odometerJsonArray = new JSONArray(dataObj.getString("ListTruckOdometer"));
                                    }
                                    ShowOdometerValuesOnView(dataObj, odometerJsonArray, true);

                                    // Update updated odometer into local DB ......
                                    if (LogDate.equals(CurrentDate)) {
                                        odometer18DaysArray = odometerhMethod.getSavedOdometer18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
                                        if(odometer18DaysArray.length() > 0) {
                                            JSONArray selectedArray = odometerhMethod.GetSelectedDateArray(odometer18DaysArray, LogDate);
                                            JSONArray updated18DaysArray = odometerhMethod.UpdateCurrentDayOdometer(selectedArray, odometer18DaysArray, LogDate);
                                            odometerhMethod.Odometer18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, updated18DaysArray);
                                        }

                                    }



                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                break;


                            case GET_SHIPMENT:
                                try {
                                    if (!obj.isNull("Data")) {
                                        dataObj = new JSONObject(obj.getString("Data"));
                                        setShipperInfo(dataObj, CurrentDate, LogDate);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;


                            case GET_SHIPMENT_18Days:
                                try {
                                    JSONArray resultArray = new JSONArray(obj.getString("Data"));
                                    shipmentHelper.Shipment18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, resultArray);
                                    GetShipmentDetails();
                                } catch (JSONException e) {
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

                            case GetRecapViewData:
                                try{
                                    if (!obj.isNull("Data")) {
                                        JSONArray recapArray = recapViewMethod.ParseServerResponseOfArray(obj.getJSONArray("Data"));
                                        if(!UpdateRecap) {
                                            recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, recapArray);
                                        }else{
                                            recap18DaysArray = recapViewMethod.AddDataInArray(recap18DaysArray, recapArray);
                                            recap18DaysArray = recapViewMethod.RemoveMoreThen18DaysRecapArray(recap18DaysArray);
                                            recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, recap18DaysArray);

                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                IsRecapApiCalled = false;

                                break;


                            case GetDriverLog18Days:
                                /* ---------------- DB Helper operations (Insert/Update) --------------- */
                                try {
                                    if(getActivity() != null) {
                                        String savedDate = Globally.GetCurrentDateTime();
                                        if (savedDate != null)
                                            sharedPref.setSavedDateTime(savedDate, getActivity());

                                        JSONArray resultArray = new JSONArray(obj.getString("Data"));
                                        hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, resultArray);
                                        driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
                                        selectedArray = hMethods.GetSingleDateArray(driverLogArray, selectedDateTime, currentDateTime, selectedUtcTime, IsCurrentDate, offsetFromUTC);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case GetDriverPermission:
                                try {
                                    if (!obj.isNull("Data")) {
                                        JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                                        driverPermissionMethod.DriverPermissionHelper(Integer.valueOf(DRIVER_ID), dbHelper, dataJObject);
                                        logPermissionObj = dataJObject;
                                        isPermissionResponse = true;
                                        isTrueAnyPermission = driverPermissionMethod.isTrueAnyPermission(dataJObject);
                                        DriverPermitMaxDays = logPermissionObj.getInt(ConstantsKeys.ViewCertifyDays);
                                        IsEditLocation      = driverPermissionMethod.getPermissionStatus(dataJObject, ConstantsKeys.LocationKey);
                                        if(logPermissionObj.has(ConstantsKeys.EditDays)) {
                                            EditDaysCount = logPermissionObj.getInt(ConstantsKeys.EditDays);
                                        }

                                        CheckSignatureVisibilityStatus(selectedArray);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case GetReCertifyRecords:

                                try {
                                    if (!obj.isNull("Data")) {
                                        sharedPref.setReCertifyData(obj.getString("Data"), getActivity());

                                        // update recap array for reCertify the log if edited
                                        constants.UpdateCertifyLogArray(recapViewMethod, DRIVER_ID, 7,
                                                dbHelper, getActivity());

                                        refreshPendingSignView();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

                        }
                    }else{

                        try {
                            progressBarDriverLog.setVisibility(View.GONE);
                            if(progressDialog != null && progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }

                            if(obj.has("Message")){
                                String message = obj.getString("Message");

                                if(!message.equals("null"))
                                    global.EldScreenToast(eldMenuBtn, message, Color.parseColor(colorVoilation));
                                else if(message.contains("ServerError")){
                                    message = "ALS server not responding";
                                }else if(message.contains("Network")){
                                    message = "Internet connection problem";
                                }else if(message.contains("NoConnectionError")){
                                    message = "Connection not working.";
                                }


                                if(message.equals("Device Logout")){

                                    if(getActivity() != null) {
                                        JSONArray unPostedLogArray = constants.GetDriversSavedArray(getActivity(),
                                                MainDriverPref, CoDriverPref);
                                        if(unPostedLogArray.length() == 0) {
                                            global.ClearAllFields(getActivity());
                                            global.StopService(getActivity());
                                            Intent i = new Intent(getActivity(), LoginActivity.class);

                                            getActivity().startActivity(i);
                                            getActivity().finish();
                                        }
                                    }
                                }else{
                                    GetShipmentDetails();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };


    private List<OdometerModel> getOdometerList(JSONArray SelectedArray, boolean isOnline){

        List<OdometerModel> odometerList = new ArrayList<OdometerModel>();
        String VehicleNumber = "";

        try {

            for (int i = 0; i < SelectedArray.length(); i++) {
                JSONObject odometerListJson = (JSONObject) SelectedArray.get(i);
                String TruckEquipmentNumber = "--";
                boolean isPersonal = false;

                if (!isOnline)
                    VehicleNumber = odometerListJson.getString(ConstantsKeys.VehicleNumber);
                else
                    VehicleNumber = odometerListJson.getString(ConstantsKeys.TruckEquipmentNumber);

                if (!odometerListJson.isNull(ConstantsKeys.TruckEquipmentNumber))
                    TruckEquipmentNumber = odometerListJson.getString(ConstantsKeys.TruckEquipmentNumber);

                if(odometerListJson.has(ConstantsKeys.IsPersonal) && !odometerListJson.isNull(ConstantsKeys.IsPersonal)) {
                    isPersonal = odometerListJson.getBoolean(ConstantsKeys.IsPersonal);
                }

                OdometerModel odometerModel = new OdometerModel(
                        VehicleNumber,
                        odometerListJson.getString(ConstantsKeys.DriverId),
                        odometerListJson.getString(ConstantsKeys.VIN),
                        odometerListJson.getString(ConstantsKeys.StartOdometer),
                        odometerListJson.getString(ConstantsKeys.EndOdometer),
                        odometerListJson.getString(ConstantsKeys.TotalMiles),
                        odometerListJson.getString(ConstantsKeys.TotalKM),
                        odometerListJson.getString(ConstantsKeys.DistanceType),
                        odometerListJson.getString(ConstantsKeys.CreatedDate),
                        TruckEquipmentNumber,
                        odometerListJson.getString(ConstantsKeys.DriverStatusID),
                        isPersonal
                );
                odometerList.add(odometerModel);

            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return odometerList;
    }


    void ShowOdometerValuesOnView(JSONObject dataObj, JSONArray SelectedArray, boolean isOnline){
        try {
            odometerList = new ArrayList<>();

            odometerList = getOdometerList(SelectedArray, isOnline);
            try {
                odometerAdapter = new OdometerAdapter(getActivity(), odometerList);
                odometerListView.setAdapter(odometerAdapter);
            }catch (Exception e){}

            odometerLayHeight     = itemOdometerLay.getMeasuredHeight();
            final int Height      = (odometerLayHeight + 2) * odometerList.size();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    odometerListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Height  ));
                }
            },800);

            String totalDistanceKm = "--", totalDistanceMiles = "--";

            if(isOnline) {
                if (dataObj.has(ConstantsKeys.TotalKM) && !dataObj.isNull(ConstantsKeys.TotalKM))
                    totalDistanceKm = dataObj.getString(ConstantsKeys.TotalKM) + " km"; //+DistanceType;

                if (dataObj.has(ConstantsKeys.TotalMiles) && !dataObj.isNull(ConstantsKeys.TotalMiles))
                    totalDistanceMiles = dataObj.getString(ConstantsKeys.TotalMiles) + " miles"; //+DistanceType;
            }

            totalDisOdoTV.setText( totalDistanceKm );
            totalMilesOdoTV.setText(totalDistanceMiles);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            progressBarDriverLog.setVisibility(View.GONE);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            Log.d("error", ">>error: " +error);
        }
    };


    void GET_CERTIFY_LOG() {

        // Get Driver details in offline mode
        progressBarDriverLog.setVisibility(View.GONE);
        selectedArray = hMethods.GetSingleDateArray(driverLogArray,  selectedDateTime, currentDateTime,
                selectedUtcTime, IsCurrentDate, offsetFromUTC);


        JSONObject obj = new JSONObject();
        ParseLogData(obj, true);
        ParseRecapData(obj);
        GetShipmentDetails();
        GetOdometerOffLineData();
        GetRecapViewOffLineData();
        GetTruckTrailer(selectedArray);

        // Get Driver Log API if Driver is connected with internet
        if (Globally.isConnected(getActivity())) {
            GET_DRIVER_DETAILS(global.PROJECT_ID, DRIVER_ID, LogDate);
        }

    }

    void ReloadWebView(final String closeTag){
        graphWebView.loadData("", "text/html; charset=UTF-8", null );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String data = ConstantHtml.GraphHtml + htmlAppendedText + closeTag;
                graphWebView.loadDataWithBaseURL("" , data, "text/html", "UTF-8", "");
                if(global.isTablet(getActivity())){
                    graphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            constants.dpToPx(getActivity(), 200)) );
                }else{
                    graphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            constants.dpToPx(getActivity(), 160)) );
                }

            }
        }, 500);

    }


    void GetOdometerOffLineData(){
        try {
            JSONArray selectedArray = odometerhMethod.GetSelectedDateArray(odometer18DaysArray , LogDate);
            ShowOdometerValuesOnView(new JSONObject(), selectedArray, false);

            totalMilesOdoTV.setText(odometerhMethod.Convert2DecimalPlaces(odometerhMethod.GetTotalMiles(selectedArray)) + " miles");
            totalDisOdoTV.setText(odometerhMethod.Convert2DecimalPlaces(odometerhMethod.GetTotalKm(selectedArray)) + " km");

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    void crossVerifyRecapData(){
        JSONArray array = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
        JSONArray updatedArray = new JSONArray();
        DateTime lastItemDate = null;
        try{
            for(int i = 0 ; i < array.length() ; i++ ){
                JSONObject obj = (JSONObject)array.get(i);
                String date = global.ConvertDateFormatyyyy_MM_dd(obj.getString(ConstantsKeys.Date)) + "T00:00:00";
                DateTime selectedDateTime = global.getDateTimeObj(date, false);
                if(i == 0) {
                    updatedArray.put(obj);
                }else{
                    if(selectedDateTime.isAfter(lastItemDate)){
                        updatedArray.put(obj);
                    }else{
                        break;
                    }
                }

                lastItemDate = selectedDateTime;

            }

            // update Array in db if existing and updated array is not same
            if(array.length() != updatedArray.length()){
                recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, updatedArray);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    void GetRecapViewOffLineData(){
        try {
            recap18DaysArray = recapViewMethod.getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
            if(recap18DaysArray != null && recap18DaysArray.length() > 0){
                try {
                    JSONObject recapSelectedJson = recapViewMethod.GetSelectedRecapData(recap18DaysArray, LogDate);
                    if(recapSelectedJson != null) {
                        JSONArray selectedArray = new JSONArray();
                        selectedArray.put(recapSelectedJson);
                        LogSignImage = recapSelectedJson.getString(ConstantsKeys.LogSignImage);
                        LogSignImageInByte = recapSelectedJson.getString(ConstantsKeys.LogSignImageInByte);
                        OfflineByteImg = LogSignImageInByte;
                        OffLineLogSignImage = LogSignImage;

                            ParseRecapData(recapSelectedJson);


                        NullCheckJson(recapSelectedJson, certifyCoDriverNameTV, ConstantsKeys.CoDriverName, "N/A");
                        if( !LogDate.equals(CurrentDate) ) {
                            NullCheckJson(recapSelectedJson, certifyDistanceTV, ConstantsKeys.EngineMileage, "0");
                            EngineMileage = certifyDistanceTV.getText().toString();
                        }


                        loadByteImage(LogSignImageInByte);


                    }else{
                        CallRecapApi();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    CallRecapApi();
                }
            }else{
                CallRecapApi();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    void loadByteImage(String LogSignImageInByte){
        isCertifyViewAgain = false;

        try {
            signImageView.setBackground(null);
            signImageView.setImageResource(R.drawable.transparent);
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (LogSignImageInByte.length() > 0) {
                Bitmap bitmap = Globally.ConvertStringBytesToBitmap(LogSignImageInByte);
                int width = signImageView.getWidth();
                int height = signImageView.getHeight();
                if(width > 0 && height > 0) {
                    signImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
                }else{
                    isCertifyViewAgain = true;
                }
            }
        }catch (Exception e){
            isCertifyViewAgain = true;
            e.printStackTrace();
        }


    }


    void CallRecapApi(){
        if(Globally.isConnected(getActivity()) && IsRecapApiCalled == false) {
            DateTime currentDateTime = new DateTime(Globally.GetCurrentDateTime());
            DateTime startDateTime = Globally.GetStartDate(currentDateTime, 7);
            String StartDate = Globally.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime));
            String EndDate = Globally.GetCurrentDeviceDate(); // current Date

            GetRecapView18DaysData(DRIVER_ID, DeviceId, StartDate, EndDate);
        }
    }



    public void ContinueWithoutSignDialog(){
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Certify log alert !!");
            alertDialogBuilder.setMessage(getString(R.string.continue_sign_desc));
            alertDialogBuilder.setCancelable(false);


            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            IsContinueWithSign = true;
                            SaveDriverSignArray();
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



}
