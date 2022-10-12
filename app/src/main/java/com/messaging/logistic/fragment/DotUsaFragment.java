package com.messaging.logistic.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adapter.logistic.DotLogAdapter;
import com.adapter.logistic.ShippingViewDetailAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.ConstantHtml;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.ScrollViewExt;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.constants.WebAppInterface;
import com.custom.dialogs.DatePickerDialog;
import com.custom.dialogs.DotOtherOptionDialog;
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
import com.models.DotDataModel;
import com.models.DriverLocationModel;
import com.models.ShipmentModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shared.pref.StatePrefManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import lib.kingja.switchbutton.SwitchMultiButton;

public class DotUsaFragment extends Fragment implements View.OnClickListener {

    View rootView;
    Globally global;
    HelperMethods hMethods;
    Constants constants;
    WebView dotGraphWebView;
    TextView errorConnectionView,  EldTitleTV, dotMalfunctionTV;
    ImageView nextDateBtn, previousDateBtn, eldMenuBtn, signImageView;

    LinearLayout itemOdometerLay, itemShippingLay;
    RelativeLayout rightMenuBtn, eldMenuLay, SignatureMainLay,otherOptionBtn;
    ProgressBar dotProgressBar;

    TextView recordDateTV, usDotTV, LicenseNoTV, LicensePlateTV;
    TextView eldIdTV, trailerIdTV, timeZoneTV, driverNameTV;
    TextView coDriverNameTV, eldManfctrTV, shippingIdTV, dataDiagnticTV;
    TextView period24startTV, driverIdTV, coDriverIdTV, truckTractorTV;
    TextView unidentifiedDriverTV, eldMalfTV, carrierTV, startEndOdoTV;
    TextView OdometerDiffTV, truckTractorVinTV, exemptDriverStatusTV, startEndEngineHrTV;
    TextView currentLocTV, fileCommentTV, PrintDisplayDateTV, placeOfBusinessTV;

    ListView dotDataListView, shippingDotListView;
    ScrollViewExt dotScrollView;
    SwitchMultiButton usDotSwitchBtn;

    GenerateRodsDialog generateRodsDialog;
    DatePickerDialog dateDialog;
    ShareDriverLogDialog shareDialog;
    List<String> StateArrayList = new ArrayList<>();
    List<DriverLocationModel> StateList = new ArrayList<>();
    List<DotDataModel> dotLogList;
    List<ShipmentModel> shipmentLogList;
    List<String> countryList = new ArrayList<>();

    VolleyRequest GetDotLogRequest;
    Map<String, String> params;
    DotLogAdapter dotLogAdapter;
    ShippingViewDetailAdapter shippingAdapter;
    DotOtherOptionDialog dotOtherOptionDialog;

    String INDIAN_URL       = "http://182.73.78.171:8286/";
   // String PRODUCTION_URL   = "https://alsrealtime.com/";
   // String logUrl = PRODUCTION_URL + "DriverLog/MobileELDView?driverId=";
    String LogDate, DayName, MonthFullName , MonthShortName , CurrentCycleId, selectedCountryRods = "";
    String CurrentDate, DRIVER_ID, DeviceId ;   //CountryCycle
    String DefaultLine      = " <g class=\"event \">\n";

    String htmlAppendedText = "";

    int shippingLayHeight = 0;
    int inspectionLayHeight = 0;
    int hLineX1         = 0;
    int hLineX2         = 0;
    int OldStatus       = -1;
    int startHour       = 0;
    int startMin        = 0;
    int endHour         = 0;
    int endMin          = 0;

    String TotalOnDutyHours         = "00:00";
    String TotalDrivingHours        = "00:00";
    String TotalOffDutyHours        = "00:00";
    String TotalSleeperBerthHours   = "00:00";
    boolean IsMalfunction = false;

    String LogSignImage = "", DriverId = "";    //LogSignImageInByte = ""
    int SelectedDayOfMonth  = 0;
    int UsaMaxDays          = 7;
    int CanMaxDays          = 14;
    int MaxDays;
    final int GetDriverDotLog     = 101;
    private DisplayImageOptions options;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Thread.setDefaultUncaughtExceptionHandler(onRuntimeError);

        if(UILApplication.getInstance().isNightModeEnabled()){
            getActivity().setTheme(R.style.DarkTheme);
        } else {
            getActivity().setTheme(R.style.LightTheme);
        }

        rootView = inflater.inflate(R.layout.fragment_dot_us, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }



    void initView(View view) {

        hMethods            = new HelperMethods();
        global              = new Globally();
        constants           = new Constants();
        GetDotLogRequest    = new VolleyRequest(getActivity());

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.no_sign_img)
                .showImageForEmptyUri(R.drawable.no_sign_img)
                .showImageOnFail(R.drawable.no_sign_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        dotDataListView     = (ListView)view.findViewById(R.id.dotDataListView);
        shippingDotListView = (ListView)view.findViewById(R.id.shippingDotListView);

        nextDateBtn         = (ImageView)view.findViewById(R.id.nextDateBtn);
        previousDateBtn     = (ImageView)view.findViewById(R.id.previousDate);
        eldMenuBtn          = (ImageView)view.findViewById(R.id.eldMenuBtn);
        signImageView       = (ImageView)view.findViewById(R.id.signImageView);

        otherOptionBtn      = (RelativeLayout) view.findViewById(R.id.otherOptionBtn);
        rightMenuBtn        = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        itemOdometerLay     = (LinearLayout)view.findViewById(R.id.itemOdometerLay);
        itemShippingLay     = (LinearLayout)view.findViewById(R.id.itemShippingLay);

        eldMenuLay          = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        SignatureMainLay    = (RelativeLayout)view.findViewById(R.id.SignatureMainLay);

        dotProgressBar      = (ProgressBar)view.findViewById(R.id.dotProgressBar);

        dotGraphWebView     = (WebView) view.findViewById(R.id.dotGraphWebView);
        dotScrollView       = (ScrollViewExt)view.findViewById(R.id.dotScrollView);
        usDotSwitchBtn      = (SwitchMultiButton)view.findViewById(R.id.usDotSwitchBtn);

        usDotSwitchBtn.setOnSwitchListener (onSwitchListener);

        initilizeTextView(view);
        getBundleData();

        countryList.add("Select");
        countryList.add("CANADA");
        countryList.add("USA");

        WebSettings webSettings = dotGraphWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        dotGraphWebView.setWebViewClient(new WebViewClient());
        dotGraphWebView.setWebChromeClient(new WebChromeClient());
        dotGraphWebView.addJavascriptInterface( new WebAppInterface(), "Android");


        nextDateBtn.setOnClickListener(this);
        previousDateBtn.setOnClickListener(this);
        otherOptionBtn.setOnClickListener(this);
        EldTitleTV.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);

    }

    private void initilizeTextView(View view){

        errorConnectionView = (TextView)view.findViewById(R.id.errorConnectionView);
        EldTitleTV          = (TextView)view.findViewById(R.id.EldTitleTV);
        dotMalfunctionTV    = (TextView)view.findViewById(R.id.dotMalfunctionTV);

        recordDateTV        = (TextView)view.findViewById(R.id.recordDateTV);
        usDotTV             = (TextView)view.findViewById(R.id.usDotTV);
        LicenseNoTV         = (TextView)view.findViewById(R.id.LicenseNoTV);
        LicensePlateTV      = (TextView)view.findViewById(R.id.LicensePlateTV);

        eldIdTV             = (TextView)view.findViewById(R.id.eldIdTV);
        trailerIdTV         = (TextView)view.findViewById(R.id.trailerIdTV);
        timeZoneTV          = (TextView)view.findViewById(R.id.timeZoneTV);
        driverNameTV        = (TextView)view.findViewById(R.id.driverNameTV);

        coDriverNameTV      = (TextView)view.findViewById(R.id.coDriverNameTV);
        eldManfctrTV        = (TextView)view.findViewById(R.id.eldManfctrTV);
        shippingIdTV        = (TextView)view.findViewById(R.id.shippingIdTV);
        dataDiagnticTV      = (TextView)view.findViewById(R.id.dataDiagnticTV);

        period24startTV     = (TextView)view.findViewById(R.id.period24startTV);
        driverIdTV          = (TextView)view.findViewById(R.id.driverIdTV);
        coDriverIdTV        = (TextView)view.findViewById(R.id.coDriverIdTV);
        truckTractorTV      = (TextView)view.findViewById(R.id.truckTractorTV);

        unidentifiedDriverTV= (TextView)view.findViewById(R.id.unidentifiedDriverTV);
        eldMalfTV           = (TextView)view.findViewById(R.id.eldMalfTV);
        carrierTV           = (TextView)view.findViewById(R.id.carrierTV);
        startEndOdoTV       = (TextView)view.findViewById(R.id.startEndOdoTV);

        OdometerDiffTV      = (TextView)view.findViewById(R.id.OdometerDiffTV);
        truckTractorVinTV   = (TextView)view.findViewById(R.id.truckTractorVinTV);
        exemptDriverStatusTV= (TextView)view.findViewById(R.id.exemptDriverStatusTV);
        startEndEngineHrTV  = (TextView)view.findViewById(R.id.startEndEngineHrTV);

        currentLocTV        = (TextView)view.findViewById(R.id.currentLocTV);
        fileCommentTV       = (TextView)view.findViewById(R.id.fileCommentTV);
        PrintDisplayDateTV  = (TextView)view.findViewById(R.id.PrintDisplayDateTV);
        placeOfBusinessTV   = (TextView)view.findViewById(R.id.placeOfBusinessTV);



    }


    SwitchMultiButton.OnSwitchListener onSwitchListener = new SwitchMultiButton.OnSwitchListener() {
        @Override
        public void onSwitch(int position, String tabText) {

            if ( position == 1) {
                if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS)) {
                    CurrentCycleId = DriverConst.GetDriverSettings(DriverConst.CANCycleId, getActivity());
                } else {
                    CurrentCycleId = DriverConst.GetDriverSettings(DriverConst.USACycleId, getActivity());
                }

                String obdCycleId = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());
                if (obdCycleId.equals(global.USA_WORKING_6_DAYS) || obdCycleId.equals(global.USA_WORKING_7_DAYS)) {
                    moveToDotMode(LogDate, DayName, MonthFullName, MonthShortName, CurrentCycleId);
                } else {
                    if (getParentFragmentManager().getBackStackEntryCount() > 1) {
                        getParentFragmentManager().popBackStack();
                    } else {
                        moveToDotMode(LogDate, DayName, MonthFullName, MonthShortName, CurrentCycleId);
                    }
                }
                usDotSwitchBtn.setSelectedTab(0);
            }
        }
    };


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }

    private void getBundleData(){

        Constants.IS_ACTIVE_ELD = false;
        Bundle getBundle        = this.getArguments();
        if(getBundle != null) {
            LogDate = getBundle.getString("date");
            DayName = getBundle.getString("day_name");
            MonthFullName = getBundle.getString("month_full_name");
            MonthShortName = getBundle.getString("month_short_name");
            CurrentCycleId = getBundle.getString("cycle");
            SelectedDayOfMonth = getBundle.getInt("day_of_month");
           // CountryCycle = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycle, getActivity());
           // getBundle.clear();
        }

        CurrentDate             = global.GetCurrentDeviceDate();
        DeviceId                = SharedPref.GetSavedSystemToken(getActivity());
        DRIVER_ID               = SharedPref.getDriverId( getActivity());

        try {
            DBHelper dbHelper = new DBHelper(getActivity());
            DriverPermissionMethod driverPermissionMethod = new DriverPermissionMethod();
            JSONObject logPermissionObj    = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DRIVER_ID), dbHelper);
            CanMaxDays = constants.GetDriverPermitDaysCount(logPermissionObj, CurrentCycleId, true);

            if (CurrentCycleId.equals(global.USA_WORKING_6_DAYS) || CurrentCycleId.equals(global.USA_WORKING_7_DAYS) ) {
                MaxDays = UsaMaxDays;
            }else{
                MaxDays = CanMaxDays;
            }


        }catch (Exception e){
            e.printStackTrace();
        }

     //   logUrl = PRODUCTION_URL + "DriverLog/MobileELDView?driverId=" + DRIVER_ID + "&date=";

        //dotMalfunctionTV.setText(CountryCycle);
        EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " ( " + DayName + " )");
        eldMenuBtn.setImageResource(R.drawable.back_btn);
        otherOptionBtn.setVisibility(View.VISIBLE);
        rightMenuBtn.setVisibility(View.GONE);
        previousDateBtn.setVisibility(View.VISIBLE);
        if(UILApplication.getInstance().isNightModeEnabled()){
            itemShippingLay.setBackgroundColor(getResources().getColor(R.color.field_bg_color));
        }else{
            itemShippingLay.setBackgroundColor(getResources().getColor(R.color.dot_titles_bg));
        }
        shippingLayHeight   = itemShippingLay.getMeasuredHeight();
        /*inspectionLayHeight = itemOdometerLay.getHeight();

        ViewTreeObserver vto = itemOdometerLay.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    itemOdometerLay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    itemOdometerLay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                inspectionLayHeight = itemOdometerLay.getMeasuredHeight();

            }
        });*/



        ReloadWebView(constants.HtmlCloseTag("00:00", "00:00", "00:00", "00:00"));
        Constants.IsAlsServerResponding = true;
        try {
            StatePrefManager statePrefManager  = new StatePrefManager();
            StateList = statePrefManager.GetState(getActivity());
            StateList.add(0, new DriverLocationModel("", "Select", ""));
        } catch (Exception e) { }

        StateArrayList =  SharedPref.getStatesInList(getActivity());
        StateArrayList.add(0, "Select");

        if (global.isConnected(getActivity())) {
            GetDriverDotDetails(DRIVER_ID, LogDate);
        }else{
            Globally.EldScreenToast(getActivity().findViewById(android.R.id.content), global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.nextDateBtn:
                ChangeViewWithDate(true);
                break;

            case R.id.previousDate:
                ChangeViewWithDate(false);
                break;

            case R.id.otherOptionBtn:
                try{
                    dotOtherOptionDialog = new DotOtherOptionDialog(getActivity(), new OtherOptionDotListener());
                    dotOtherOptionDialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;

            case R.id.EldTitleTV:
                if(dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();

                dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, LogDate, new DateListener(), false);
                dateDialog.show();

                break;


            case R.id.eldMenuLay:

                EldActivity.DOTButton.performClick();

                break;




        }
    }



    void moveToDotMode(String date, String dayName, String dayFullName, String dayShortName, String cycle){

       // getParentFragmentManager().popBackStackImmediate();

        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        Fragment dotFragment = new DotCanadaFragment();
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
        fragmentTran.addToBackStack("dot_usa");  //"dot_log"
        fragmentTran.commitAllowingStateLoss();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.IS_ACTIVE_ELD = true;

    }



    void shareDriverLogDialog() {

      //  boolean IsAOBRDAutomatic        = SharedPref.IsAOBRDAutomatic(getActivity());
        boolean IsAOBRD                 = SharedPref.IsAOBRD(getActivity());

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

            int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
            Logger.LogDebug("DaysDiff", "DaysDiff: " + DaysDiff);

            if ( DaysDiff >= 0 && DaysDiff <= MaxDays) {

                DOTBtnVisibility(DaysDiff, MaxDays);
                LogDate = global.ConvertDateFormatMMddyyyy(selectedDateTime.toString());

                //  String dayOfTheWeek = global.GetDayOfWeek(LogDate);
                int mnth = Integer.valueOf(LogDate.substring(0, 2));
                // String MonthFullName = global.MONTHS_FULL[mnth - 1];
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

                GetDriverDotDetails(DRIVER_ID, LogDate);

            }

        }else{
            LogDate = selectedDate;
            global.EldScreenToast(eldMenuLay, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            //webViewErrorDisplay();
        }

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


    void CheckSignatureVisibilityStatus(){

        if (!LogDate.equals(CurrentDate) ) {
            SignatureMainLay.setVisibility(View.VISIBLE);
            if(LogSignImage.length() > 0 ){ //|| LogSignImageInByte.length() > 0

                ImageLoader.getInstance().displayImage( LogSignImage, signImageView , options);
               /* if(LogSignImageInByte.length() > 0){
                    constants.loadByteImage(LogSignImageInByte, signImageView);
                }else{

                }*/

            }else {
                signImageView.setBackgroundDrawable(null);
                signImageView.setImageResource(R.drawable.no_sign_img);
            }
        } else {
            SignatureMainLay.setVisibility(View.GONE);

        }

    }



    private class OtherOptionDotListener implements DotOtherOptionDialog.OtherOptionDotListener{

        @Override
        public void ItemClickReady(int position) {
            if(position == 0){
                MoveFragment(LogDate);
            }else if(position == 1){
                shareDriverLogDialog();
            }else if(position == 2){
                generateRodsDialog = new GenerateRodsDialog(getActivity(), countryList, new GenerateRodsListner(),
                        DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity()));
                generateRodsDialog.show();
            }else {
                MoveDownloadLogFragment();
            }
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
                    Globally.EldScreenToast(rootView, "Please wait file download in progress.", getContext().getResources().getColor(R.color.color_eld_theme));
                }else{
                    if(!checkedMode.equals("")){
                        DownloadPdfLog(Globally.CANADA_CYCLE,checkedMode,lastParseDatetime,currentParseDatetime);
                        Globally.EldScreenToast(rootView, "Please wait file download in progress.", getContext().getResources().getColor(R.color.color_eld_theme));
                    }
                }
                generateRodsDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
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
        bundle.putString("cycle", Globally.USA_CYCLE);
        logFragment.setArguments(bundle);

        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, logFragment);
        fragmentTran.addToBackStack("inspection");
        fragmentTran.commitAllowingStateLoss();


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

            // reload WebView for selected date
            if (global.isConnected(getActivity())) {

                String selectedDateStr = global.ConvertDateFormat(LogDate);
                String currentDateStr = global.ConvertDateFormat(CurrentDate);
                DateTime selectedDateTime = global.getDateTimeObj(selectedDateStr, false);
                DateTime currentDateTime = global.getDateTimeObj(currentDateStr, false);
                int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
                Logger.LogDebug("DaysDiff", "DaysDiff: " + DaysDiff);

                DOTBtnVisibility(DaysDiff, MaxDays);
                GetDriverDotDetails(DRIVER_ID, LogDate);

            }else{
                global.EldScreenToast(eldMenuLay, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            }

        }
    }


    /* ================== Get Driver Details =================== */
    void GetDriverDotDetails( final String DriverId, final String date) {

        dotProgressBar.setVisibility(View.VISIBLE);

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.Date, date);

        GetDotLogRequest.executeRequest(Request.Method.POST, APIs.MOBILE_ELD_VIEW_NEW, params, GetDriverDotLog,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }


    void ReloadWebView(final String closeTag){
        dotGraphWebView.clearCache(true);
        dotGraphWebView.loadData("", "text/html; charset=UTF-8", null );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String data = ConstantHtml.GraphHtml + htmlAppendedText + closeTag;
                dotGraphWebView.loadDataWithBaseURL("" , data, "text/html", "UTF-8", "");

                if(global.isTablet(getActivity())){
                    dotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            constants.dpToPx(getActivity(), 250)) );
                }else{
                    dotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
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

                if (EventType == 1 ) {
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





    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

            dotProgressBar.setVisibility(View.GONE);

            String status = "", Message = "";
            JSONObject dataObj = null;

            if(response != null && response.length() > 0) {
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
                    TotalOnDutyHours = "00:00";
                    TotalDrivingHours = "00:00";
                    TotalOffDutyHours = "00:00";
                    TotalSleeperBerthHours = "00:00";

                    try {
                        TotalOffDutyHours = dataObj.getString(ConstantsKeys.TotalOffDutyHours);
                        TotalSleeperBerthHours = dataObj.getString(ConstantsKeys.TotalSleeperHours);
                        TotalDrivingHours = dataObj.getString(ConstantsKeys.TotalDrivingHours);
                        TotalOnDutyHours = dataObj.getString(ConstantsKeys.TotalOnDutyHours);
                        IsMalfunction = dataObj.getBoolean(ConstantsKeys.IsMalfunction);
                        LogSignImage = dataObj.getString(ConstantsKeys.LogSignImage);
                        if (LogSignImage.equals("null")) {
                            LogSignImage = "";
                        }

                        TotalOffDutyHours = TotalOffDutyHours.replaceAll("-", "");
                        TotalOnDutyHours = TotalOnDutyHours.replaceAll("-", "");
                        TotalDrivingHours = TotalDrivingHours.replaceAll("-", "");
                        TotalSleeperBerthHours = TotalSleeperBerthHours.replaceAll("-", "");

                        setDataOnView(dataObj);
                        CheckSignatureVisibilityStatus();
                        JSONArray dotLogArray = new JSONArray(dataObj.getString(ConstantsKeys.oReportList));
                        setDataOnList(dotLogArray);

                        JSONArray shippingLogArray = new JSONArray(dataObj.getString(ConstantsKeys.ShippingInformationModel));
                        setShippingDataOnList(shippingLogArray);

                        ParseGraphData(dotLogArray);

               /*     if(IsMalfunction){
                        dotMalfunctionTV.setVisibility(View.VISIBLE);
                    }else{
                        dotMalfunctionTV.setVisibility(View.GONE);
                    }*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    htmlAppendedText = "";

                    String CloseTag = constants.HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);
                    ReloadWebView(CloseTag);

                    global.EldScreenToast(eldMenuLay, Message, getResources().getColor(R.color.colorVoilation));

                }
            }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            Logger.LogDebug("error", ">>error: " + error);

            if(getActivity() != null && !getActivity().isFinishing()) {
                try {
                    dotProgressBar.setVisibility(View.GONE);
                    global.EldScreenToast(eldMenuLay, Globally.DisplayErrorMessage(error.toString()), getResources().getColor(R.color.colorVoilation));
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


    void setDataOnList(JSONArray array){
        try{
            dotLogList = new ArrayList<>();
            for(int i = 0; i < array.length() ; i++){
                JSONObject obj = (JSONObject)array.get(i);
                String time = obj.getString(ConstantsKeys.DateTimeWithMins);
                if(time.length() > 18) {
                    DateFormat format = new SimpleDateFormat(Globally.DateFormat, Locale.ENGLISH);
                    Date date = format.parse(time);

                    String remarks = constants.CheckNullString(obj.getString(ConstantsKeys.Remarks));
                    String strEventType = constants.checkNullString(obj.getString(ConstantsKeys.strEventType));

                    try {
                        if (obj.getString(ConstantsKeys.EventType).equals("7")) {
                            strEventType = obj.getString(ConstantsKeys.AdditionalInfo);
                            if (remarks.length() == 0) {
                                remarks = constants.getLoginLogoutEventName(7, obj.getInt(ConstantsKeys.EventCode));
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    DotDataModel dotLogItem = new DotDataModel(
                            constants.checkNullString(obj.getString(ConstantsKeys.strEventType)),
                            time,
                            time,
                            obj.getBoolean(ConstantsKeys.IsMalfunction),
                            time.substring(11, 19),
                            constants.checkNullString(obj.getString(ConstantsKeys.Annotation)),
                            constants.checkNullString(obj.getString(ConstantsKeys.OdometerInKm)),
                            constants.checkNullString(obj.getString(ConstantsKeys.TotalVehicleMiles)),
                            constants.checkNullString(obj.getString(ConstantsKeys.OdometerInKm)),
                            constants.checkNullString(obj.getString(ConstantsKeys.TotalVehicleMiles)),
                            constants.checkNullString(obj.getString(ConstantsKeys.TotalEngineHours)),
                            strEventType,
                            remarks,
                            constants.checkNullString(obj.getString(ConstantsKeys.Origin)),
                            constants.checkNullString(obj.getString(ConstantsKeys.SequenceNumber)),
                            date

                            );
                    dotLogList.add(dotLogItem);
                }
            }

            Collections.sort(dotLogList);

            dotLogAdapter = new DotLogAdapter(getActivity(), constants, dotLogList);
            dotDataListView.setAdapter(dotLogAdapter);

         //   if(inspectionLayHeight == 0) {
                if(global.isTablet(getActivity())){
                    inspectionLayHeight = constants.intToPixel(getActivity(), 68);
                }else{
                    inspectionLayHeight = constants.intToPixel(getActivity(), 62);
                }

          //  }
            Logger.LogDebug("inspectionLayHeight","inspectionLayHeight: "+inspectionLayHeight);
            final int Height      = (inspectionLayHeight + 1 ) * dotLogList.size();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dotDataListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Height  ));
                }
            },500);
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    void setShippingDataOnList(JSONArray array){
        try{
            shipmentLogList = new ArrayList<>();
            for(int i = 0; i <array.length() ; i++){
                JSONObject obj = (JSONObject)array.get(i);

                String blNumber = obj.getString(ConstantsKeys.ShippingDocumentNumber);
                String ShipperName =  obj.getString(ConstantsKeys.ShipperName);
                String ShipperState =  obj.getString(ConstantsKeys.ShipperState);
                String ShipperPostalCode =  obj.getString(ConstantsKeys.ShipperPostalCode);
                String ShippingDocDate =  obj.getString(ConstantsKeys.ShippingDocDate);
                String Commodity =  obj.getString(ConstantsKeys.Commodity);
                String savedDate = obj.getString(ConstantsKeys.ShipperDocDateStr);

                if(  !ShipperPostalCode.trim().equals("")) {
                    ShipmentModel shipModel = new ShipmentModel(
                            i,
                            DRIVER_ID,
                            "",
                            DeviceId,
                            ShippingDocDate,
                            blNumber,
                            Commodity,
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

            //   Collections.sort(shipmentLogList);
            shippingAdapter = new ShippingViewDetailAdapter(getActivity(), shipmentLogList);
            shippingDotListView.setAdapter(shippingAdapter);

            if(shippingLayHeight == 0) {
                itemShippingLay.measure(0, 0);
                shippingLayHeight = itemShippingLay.getMeasuredHeight();
            }
            final int Height      = (shippingLayHeight ) * shipmentLogList.size();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    shippingDotListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Height  ));
                }
            },800);


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void  setDataOnView(JSONObject obj){

        try {
            String recordDate  = global.ConvertDateFormatMMddyyyy(obj.getString(ConstantsKeys.RecordDate));
            String displayDate = global.ConvertDateFormatMMddyyyy(obj.getString(ConstantsKeys.PrintDisplayDate));

            recordDateTV.setText(recordDate);
            usDotTV.setText(CheckStringIsNull(obj, ConstantsKeys.USDOTNumber));
            LicenseNoTV .setText(CheckStringIsNull(obj, ConstantsKeys.DriverLicenseNumber));
            LicensePlateTV.setText(CheckStringIsNull(obj, ConstantsKeys.DriverLicenseState));

            eldIdTV.setText(CheckStringIsNull(obj, ConstantsKeys.ELDID));
            trailerIdTV.setText(CheckStringIsNull(obj, ConstantsKeys.TrailerId));
            timeZoneTV.setText(CheckStringIsNull(obj, ConstantsKeys.TimeZone));
            driverNameTV.setText(CheckStringIsNull(obj, ConstantsKeys.DriverName));

            coDriverNameTV.setText(CheckStringIsNull(obj, ConstantsKeys.CoDriverName));
            eldManfctrTV.setText(CheckStringIsNull(obj, ConstantsKeys.ELDManufacturer));
            shippingIdTV.setText(CheckStringIsNull(obj, ConstantsKeys.ShippingID));
            dataDiagnticTV.setText(CheckStringIsNull(obj, ConstantsKeys.DataDiagnosticIndicators));

            period24startTV.setText(CheckStringIsNull(obj, ConstantsKeys.PeriodStartingTime));
            driverIdTV.setText(CheckStringIsNull(obj, ConstantsKeys.DriverID));
            coDriverIdTV .setText(CheckStringIsNull(obj, ConstantsKeys.CoDriverID));
            truckTractorTV.setText(CheckStringIsNull(obj, ConstantsKeys.TruckTractorID));

            unidentifiedDriverTV.setText(CheckStringIsNull(obj, ConstantsKeys.UnIdentifiedDriverRecords));
            eldMalfTV.setText(CheckStringIsNull(obj, ConstantsKeys.ELDMalfunctionIndicators));
            carrierTV.setText(CheckStringIsNull(obj, ConstantsKeys.Carrier));

            truckTractorVinTV .setText(CheckStringIsNull(obj, ConstantsKeys.TruckTractorVIN));
            exemptDriverStatusTV.setText(CheckStringIsNull(obj, ConstantsKeys.ExemptDriverStatus));
            startEndEngineHrTV.setText(CheckStringIsNull(obj, ConstantsKeys.StartEndEngineHours));

            currentLocTV.setText(CheckStringIsNull(obj, ConstantsKeys.CurrentLocation));
            fileCommentTV.setText(CheckStringIsNull(obj, ConstantsKeys.FileComment));

            PrintDisplayDateTV.setText(displayDate);
            placeOfBusinessTV.setText(CheckStringIsNull(obj, ConstantsKeys.OfficeAddress));

            String odometer = obj.getString(ConstantsKeys.StartEndOdometer) + " (Miles) <br>" + obj.getString(ConstantsKeys.StartEndOdometerKM) + " (KM)";
            startEndOdoTV.setText(Html.fromHtml(odometer));

            String odometerDiff = "";
            if(IsMalfunction){
                odometerDiff = obj.getString(ConstantsKeys.OdometerDifference) + " (Miles) <font color='red'>(Malfunction)</font> <br>" +
                        obj.getString(ConstantsKeys.OdometerDifferenceKM) + " (KM) <font color='red'>(Malfunction)</font>";
            }else{
                odometerDiff = obj.getString(ConstantsKeys.OdometerDifference) + " (Miles) <br>" + obj.getString(ConstantsKeys.OdometerDifferenceKM) + " (KM)";
            }

            OdometerDiffTV.setText(Html.fromHtml(odometerDiff));



        }catch (Exception e){
            e.printStackTrace();
        }

    }


    String CheckStringIsNull(JSONObject obj, String keyValue){
        String value = "";
        try {
            value = obj.getString(keyValue);
            if(value.equals("") || value.equals("null")) {
                value = "--";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }



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
