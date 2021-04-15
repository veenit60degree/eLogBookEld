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
import com.constants.ScrollViewExt;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class DotUsaFragment extends Fragment implements View.OnClickListener {

    View rootView;
    Globally global;
    HelperMethods hMethods;
    SharedPref sharedPref;
    Constants constants;
    WebView dotGraphWebView;    //dotWebView;
    TextView errorConnectionView,  EldTitleTV, dotMalfunctionTV, viewInspectionBtn,sendLogBtn;
    ImageView nextDateBtn, previousDateBtn, eldMenuBtn, signImageView, dotModeImgVw;

    LinearLayout itemOdometerLay, itemShippingLay;
    RelativeLayout rightMenuBtn, eldMenuLay, SignatureMainLay;
    ProgressBar dotProgressBar;

    TextView recordDateTV, usDotTV, LicenseNoTV, LicensePlateTV;
    TextView eldIdTV, trailerIdTV, timeZoneTV, driverNameTV;
    TextView coDriverNameTV, eldManfctrTV, shippingIdTV, dataDiagnticTV;
    TextView period24startTV, driverIdTV, coDriverIdTV, truckTractorTV;
    TextView unidentifiedDriverTV, eldMalfTV, carrierTV, startEndOdoTV;
    TextView OdometerDiffTV, truckTractorVinTV, exemptDriverStatusTV, startEndEngineHrTV;
    TextView currentLocTV, fileCommentTV, PrintDisplayDateTV, dotModeTV;

    ListView dotDataListView, shippingDotListView;
    ScrollViewExt dotScrollView;

    DatePickerDialog dateDialog;
    ShareDriverLogDialog shareDialog;
    List<String> StateArrayList = new ArrayList<>();
    List<DriverLocationModel> StateList = new ArrayList<>();
    List<DotDataModel> dotLogList;
    List<ShipmentModel> shipmentLogList;

    VolleyRequest GetDotLogRequest;
    Map<String, String> params;
    DotLogAdapter dotLogAdapter;
    ShippingViewDetailAdapter shippingAdapter;

    String INDIAN_URL       = "http://182.73.78.171:8286/";
    String PRODUCTION_URL   = "https://alsrealtime.com/";
    String logUrl = PRODUCTION_URL + "DriverLog/MobileELDView?driverId=";
    String LogDate, DayName, MonthFullName , MonthShortName , CurrentCycleId;
    String CurrentDate, CountryCycle, DRIVER_ID, DeviceId ;
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

        rootView = inflater.inflate(R.layout.fragment_dot_us, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }



    void initView(View view) {

        hMethods            = new HelperMethods();
        sharedPref          = new SharedPref();
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
        dotModeImgVw        = (ImageView)view.findViewById(R.id.dotModeImgVw);

        rightMenuBtn        = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        itemOdometerLay     = (LinearLayout)view.findViewById(R.id.itemOdometerLay);
        itemShippingLay     = (LinearLayout)view.findViewById(R.id.itemShippingLay);

        eldMenuLay          = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        SignatureMainLay    = (RelativeLayout)view.findViewById(R.id.SignatureMainLay);

        dotProgressBar      = (ProgressBar)view.findViewById(R.id.dotProgressBar);

        dotGraphWebView     = (WebView) view.findViewById(R.id.dotGraphWebView);
        dotScrollView       = (ScrollViewExt)view.findViewById(R.id.dotScrollView);

        initilizeTextView(view);
        getBundleData();

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


     /*
      dotWebView.setWebViewClient(new WebViewClients());
        if (global.isConnected(getActivity())) {
            dotWebView.loadUrl(logUrl + LogDate);
        }else{
            webViewErrorDisplay();
        }*/

        nextDateBtn.setOnClickListener(this);
        previousDateBtn.setOnClickListener(this);
        viewInspectionBtn.setOnClickListener(this);
        EldTitleTV.setOnClickListener(this);
        sendLogBtn.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        dotModeTV.setOnClickListener(this);
        dotModeImgVw.setOnClickListener(this);

    }

    private void initilizeTextView(View view){

        errorConnectionView = (TextView)view.findViewById(R.id.errorConnectionView);
        EldTitleTV          = (TextView)view.findViewById(R.id.EldTitleTV);
        dotMalfunctionTV    = (TextView)view.findViewById(R.id.dotMalfunctionTV);
        viewInspectionBtn   = (TextView)view.findViewById(R.id.dateActionBarTV);
        sendLogBtn          = (TextView)view.findViewById(R.id.sendLogBtn);

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
        dotModeTV           = (TextView)view.findViewById(R.id.dotModeTV);



    }


    private void getBundleData(){

        Constants.IS_ACTIVE_ELD = false;
        Bundle getBundle        = this.getArguments();
        LogDate                 = getBundle.getString("date");
        DayName                 = getBundle.getString("day_name");
        MonthFullName           = getBundle.getString("month_full_name");
        MonthShortName          = getBundle.getString("month_short_name");
        CurrentCycleId          = getBundle.getString("cycle");
        SelectedDayOfMonth      = getBundle.getInt("day_of_month");
        CountryCycle            = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycle, getActivity());

        CurrentDate             = global.GetCurrentDeviceDate();
        DeviceId                = sharedPref.GetSavedSystemToken(getActivity());
        DRIVER_ID               = sharedPref.getDriverId( getActivity());

        try {
            DBHelper dbHelper = new DBHelper(getActivity());
            DriverPermissionMethod driverPermissionMethod = new DriverPermissionMethod();
            JSONObject logPermissionObj    = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DRIVER_ID), dbHelper);
            CanMaxDays = constants.GetDriverPermitDaysCount(logPermissionObj, CurrentCycleId, true);

            if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS) ) {
                MaxDays = UsaMaxDays;
            }else{
                MaxDays = CanMaxDays;
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        logUrl = PRODUCTION_URL + "DriverLog/MobileELDView?driverId=" + DRIVER_ID + "&date=";

        //dotMalfunctionTV.setText(CountryCycle);
        EldTitleTV.setText(MonthShortName + " " + LogDate.split("/")[1] + " ( " + DayName + " )");
        viewInspectionBtn.setText(getResources().getString(R.string.view_inspections));
        eldMenuBtn.setImageResource(R.drawable.back_btn);
        viewInspectionBtn.setVisibility(View.VISIBLE);
        rightMenuBtn.setVisibility(View.GONE);
        previousDateBtn.setVisibility(View.VISIBLE);
        itemShippingLay.setBackgroundColor(getResources().getColor(R.color.dot_titles_bg));
        shippingLayHeight   = itemShippingLay.getMeasuredHeight();
        inspectionLayHeight = itemOdometerLay.getHeight();

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
        });



        ReloadWebView(constants.HtmlCloseTag("00:00", "00:00", "00:00", "00:00"));
        Constants.IsAlsServerResponding = true;
        try {
            StatePrefManager statePrefManager  = new StatePrefManager();
            StateList = statePrefManager.GetState(getActivity());
        } catch (Exception e) { }

        StateArrayList =  sharedPref.getStatesInList(getActivity());

        if (global.isConnected(getActivity())) {
            GetDriverDotDetails(DRIVER_ID, LogDate);
        }else{
            Globally.EldScreenToast(eldMenuLay, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
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

            //viewInspectionBtn view click
            case R.id.dateActionBarTV:
                MoveFragment(LogDate);
                break;

            case R.id.EldTitleTV:
                if(dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();

                dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, LogDate, new DateListener());
                dateDialog.show();

                break;


            case R.id.sendLogBtn:
                shareDriverLogDialog();
                break;

            case R.id.eldMenuLay:

                EldActivity.DOTButton.performClick();

                break;

            case R.id.dotModeTV:
                if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
                    CurrentCycleId = DriverConst.GetDriverSettings(DriverConst.CANCycleId, getActivity());
                }else{
                    CurrentCycleId = DriverConst.GetDriverSettings(DriverConst.USACycleId, getActivity());
                }
                moveToDotMode(LogDate, DayName, MonthFullName, MonthShortName, CurrentCycleId);
                break;

            case R.id.dotModeImgVw:
                dotModeTV.performClick();
                break;
        }
    }



    void moveToDotMode(String date, String dayName, String dayFullName, String dayShortName, String cycle){

        getFragmentManager().popBackStackImmediate();

        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        Fragment dotFragment = new DotCanadaFragment();
        Globally.bundle.putString("date", date);
        Globally.bundle.putString("day_name", dayName);
        Globally.bundle.putString("month_full_name", dayFullName);
        Globally.bundle.putString("month_short_name", dayShortName);
        Globally.bundle.putString("cycle", cycle);

        dotFragment.setArguments(Globally.bundle);

        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, dotFragment);
        fragmentTran.addToBackStack(null);  //"dot_log"
        fragmentTran.commit();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.IS_ACTIVE_ELD = true;
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
            Globally.EldScreenToast(eldMenuLay, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
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
                DateTime selectedDateTime = new DateTime(global.getDateTimeObj(selectedDateStr, false) );
                DateTime currentDateTime = new DateTime(global.getDateTimeObj(currentDateStr, false) );
                int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
                Log.d("DaysDiff", "DaysDiff: " + DaysDiff);

                DOTBtnVisibility(DaysDiff, MaxDays);
                GetDriverDotDetails(DRIVER_ID, LogDate);

            }else{
                Globally.EldScreenToast(eldMenuLay, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                //webViewErrorDisplay();
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
            }
        }, 500);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final int width = dotGraphWebView.getWidth();
                final int height = dotGraphWebView.getHeight();

                if(width < 400){
                    dotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT + 100) );
                }else{
                    if(height == 0){
                        if(Globally.isTablet(getActivity())){
                            dotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(width, constants.dpToPx(getActivity(), 170) ) );
                        }else{
                            dotGraphWebView.setLayoutParams(new RelativeLayout.LayoutParams(width, constants.dpToPx(getActivity(), 140) ));
                        }
                    }
                }
                dotScrollView.fullScroll(ScrollView.FOCUS_UP);
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

               /*     if (!logObj.isNull(ConstantsKeys.Personal))
                        isPersonal = logObj.getBoolean(ConstantsKeys.Personal);

                    if(!logObj.isNull(ConstantsKeys.YardMove))
                        YardMove = logObj.getBoolean(ConstantsKeys.YardMove);
*/


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





    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

            dotProgressBar.setVisibility(View.GONE);

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

                try {
                    TotalOffDutyHours       = dataObj.getString("TotalOffDutyHours");
                    TotalSleeperBerthHours  = dataObj.getString("TotalSleeperHours");
                    TotalDrivingHours       = dataObj.getString("TotalDrivingHours");
                    TotalOnDutyHours        = dataObj.getString("TotalOnDutyHours");
                    IsMalfunction           = dataObj.getBoolean("IsMalfunction");
                    LogSignImage            = dataObj.getString("LogSignImage");
                    if(LogSignImage.equals("null")){
                        LogSignImage = "";
                    }

                    TotalOffDutyHours      = TotalOffDutyHours.replaceAll("-", "");
                    TotalOnDutyHours       = TotalOnDutyHours.replaceAll("-", "");
                    TotalDrivingHours      = TotalDrivingHours.replaceAll("-", "");
                    TotalSleeperBerthHours = TotalSleeperBerthHours.replaceAll("-", "");

                    setDataOnView(dataObj);
                    CheckSignatureVisibilityStatus();
                    JSONArray dotLogArray = new JSONArray(dataObj.getString("oReportList"));
                    setDataOnList(dotLogArray);

                    JSONArray shippingLogArray = new JSONArray(dataObj.getString("ShippingInformationModel"));
                    setShippingDataOnList(shippingLogArray);

                    ParseGraphData(dotLogArray);

                    if(IsMalfunction){
                        dotMalfunctionTV.setVisibility(View.VISIBLE);
                    }else{
                        dotMalfunctionTV.setVisibility(View.GONE);
                    }

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
            dotProgressBar.setVisibility(View.GONE);
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


    void setDataOnList(JSONArray array){
        try{
            dotLogList = new ArrayList<>();
            for(int i = 0; i < array.length() ; i++){
                JSONObject obj = (JSONObject)array.get(i);
                String time = obj.getString("DateTimeWithMins");
                DotDataModel dotLogItem = new DotDataModel(

                        obj.getString("strEventType"),
                        time,
                        time,
                        obj.getBoolean("IsMalfunction"),

                        time.substring(11, 16), obj.getString("Annotation"),
                        obj.getString("OdometerInKm"),
                        obj.getString("TotalVehicleMiles"),
                        obj.getString("OdometerInKm").trim(),
                        obj.getString("TotalVehicleMiles").trim(),
                        obj.getString("TotalEngineHours"),
                        obj.getString("strEventType"),
                        obj.getString("Origin" ) );
                dotLogList.add(dotLogItem);
            }


            dotLogAdapter = new DotLogAdapter(getActivity(), constants, dotLogList);
            dotDataListView.setAdapter(dotLogAdapter);

            if(inspectionLayHeight == 0) {
                // itemOdometerLay.measure(0, 0);
                //inspectionLayHeight =  itemOdometerLay.getMeasuredHeight() + 1;
                inspectionLayHeight = 160;
            }
            final int Height      = (inspectionLayHeight + 1 ) * dotLogList.size();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dotDataListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Height  ));
                }
            },800);
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
            String recordDate  = global.ConvertDateFormatMMddyyyy(obj.getString("RecordDate"));
            String displayDate = global.ConvertDateFormatMMddyyyy(obj.getString("PrintDisplayDate"));

            recordDateTV.setText(recordDate);
            usDotTV.setText(CheckStringIsNull(obj, "USDOTNumber"));
            LicenseNoTV .setText(CheckStringIsNull(obj, "DriverLicenseNumber"));
            LicensePlateTV.setText(CheckStringIsNull(obj, "DriverLicenseState"));

            eldIdTV.setText(CheckStringIsNull(obj, "ELDID"));
            trailerIdTV.setText(CheckStringIsNull(obj, "TrailerId"));
            timeZoneTV.setText(CheckStringIsNull(obj, "TimeZone"));
            driverNameTV.setText(CheckStringIsNull(obj, "DriverName"));

            coDriverNameTV.setText(CheckStringIsNull(obj, "CoDriverName"));
            eldManfctrTV.setText(CheckStringIsNull(obj, "ELDManufacturer"));
            shippingIdTV.setText(CheckStringIsNull(obj, "ShippingID"));
            dataDiagnticTV.setText(CheckStringIsNull(obj, "DataDiagnosticIndicators"));

            period24startTV.setText(CheckStringIsNull(obj, "PeriodStartingTime"));
            driverIdTV.setText(CheckStringIsNull(obj, "DriverID"));
            coDriverIdTV .setText(CheckStringIsNull(obj, "CoDriverID"));
            truckTractorTV.setText(CheckStringIsNull(obj, "TruckTractorID"));

            unidentifiedDriverTV.setText(CheckStringIsNull(obj, "UnIdentifiedDriverRecords"));
            eldMalfTV.setText(CheckStringIsNull(obj, "ELDMalfunctionIndicators"));
            carrierTV.setText(CheckStringIsNull(obj, "Carrier"));

            truckTractorVinTV .setText(CheckStringIsNull(obj, "TruckTractorVIN"));
            exemptDriverStatusTV.setText(CheckStringIsNull(obj, "ExemptDriverStatus"));
            startEndEngineHrTV.setText(CheckStringIsNull(obj, "StartEndEngineHours"));

            currentLocTV.setText(CheckStringIsNull(obj, "CurrentLocation"));
            fileCommentTV.setText(CheckStringIsNull(obj, "FileComment"));

            PrintDisplayDateTV.setText(displayDate);

            String odometer = obj.getString("StartEndOdometer") + " (Miles) <br>" + obj.getString("StartEndOdometerKM") + " (KM)";
            startEndOdoTV.setText(Html.fromHtml(odometer));

            String odometerDiff = "";
            if(IsMalfunction){
                odometerDiff = obj.getString("OdometerDifference") + " (Miles) <font color='red'>(Malfunction)</font> <br>" +
                        obj.getString("OdometerDifferenceKM") + " (KM) <font color='red'>(Malfunction)</font>";
            }else{
                odometerDiff = obj.getString("OdometerDifference") + " (Miles) <br>" + obj.getString("OdometerDifferenceKM") + " (KM)";
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







}
