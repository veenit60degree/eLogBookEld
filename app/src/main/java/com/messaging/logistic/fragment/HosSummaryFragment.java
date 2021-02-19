package com.messaging.logistic.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.InitilizeEldView;
import com.constants.LoadingSpinImgView;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.constants.VolleyRequestWithoutRetry;
import com.custom.dialogs.HosInfoDialog;
import com.custom.dialogs.ShareDriverLogDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.SuggestedFragmentActivity;
import com.messaging.logistic.UILApplication;
import com.models.DriverLocationModel;
import com.shared.pref.StatePrefManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import at.grabner.circleprogress.CircleProgressView;
import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;
import webapi.LocalCalls;

public class HosSummaryFragment extends Fragment implements View.OnClickListener {


    View rootView;
    Button availableHourBtnTV;
    TextView EldTitleTV, hosDistanceTV, hosLocationTV, nextBrkTitleTV;
    TextView breakUsedTimeTV, shiftUsedTimeTV, statusUsedTimeTV, cycleUsedTimeTV, hosCurrentCycleTV, driverMilesTitle;
    TextView statusHosTV, breakInfoTV, shiftInfoTV, statusInfoTV, cycleInfoTV, hosStatusCircle, hosStatusTV, malfunctionTV ;
    ImageView eldMenuBtn, hosStatusImgVw, malfunctionImgView;
    LoadingSpinImgView loadingSpinEldIV;
    RelativeLayout rightMenuBtn, eldMenuLay, hosMainLay, malfunctionLay;
    LinearLayout nextBreakLay;
    CircleProgressView breakCircularView, shiftCircularView, currentStatusCircularView, cycleCircularView;
    CardView sendLogHosBtn, nextBreakCardView, disabledBreakCardView;
    CardView hosCycleCardView, hosShiftCardView, hosStatusCardView, hosDistanceCardView, hosLocationCardView;

    String HOS_REMAIN_COLOR = "#6F6F6F";
    String HOS_SLEEP_OFF_COLOR = "#90A4AE";
    String isPersonal = "false", CurrentCycle = "";
    String violationReason = "30 mins break violation";
    String cycleViolationReason = "", shiftViolationReson = "";
    String DriverId;
    String DeviceId;
    String CycleId;
    String vin = "";

    long MIN_TIME_BW_UPDATES = 60000;  // 60 Sec
    final int OFF_DUTY = 1;
    final int SLEEPER = 2;
    final int DRIVING = 3;
    final int ON_DUTY = 4;
    final int GetAddFromLatLng      = 101;
    final int GetMiles              = 102;


    int DRIVER_JOB_STATUS;
    int CanadaBreakHour   = 120;      //  8 * 60 = 480 min (8 hours)
    int ShiftHour   = 480;      //  8 * 60 = 480 min (8 hours)
    int TotalCycleHour = 4200;  // 70*60 = 4200 mins (70 hours) Default
    int LeftCycleHoursInt;
    int LeftOnDutyHoursInt;
    int UsedOnDutyHoursInt;
    int LeftDrivingHoursInt;
    int UsedDrivingHoursInt;
    int UsedOffDutyHoursInt;
    int UsedSleeperHoursInt;
    int UsedShiftHoursInt;
    int LeftShiftHoursInt;
    int LeftNextBreak;
    int offsetFromUTC;
    int OffDutyMax = 30;    // in min
    int minOffDutyUsedHours = 0;
    int rulesVersion;

    boolean IsAOBRDAutomatic = true, isSingleDriver;
    boolean isRefreshBtnClicked = false, isMinOffDutyHoursSatisfied = false;

    DateTime currentDateTime , currentUTCTime;
    List<String> StateArrayList;
    List<DriverLocationModel> StateList;
    JSONArray driverLogArray, currentDayArray;
    ShareDriverLogDialog shareDialog;
    InitilizeEldView initilizeEldView;
    Constants constants;
    SharedPref sharedPref;
    Globally global;
    HelperMethods hMethods;
    DBHelper dbHelper;
    HosTimerTask timerTask;
    private Timer mTimer;
    VolleyRequestWithoutRetry GetAddFromLatLngRequest, GetMilesRequest;
    LocalCalls localCalls = new LocalCalls();
    RulesResponseObject usedAndRemainingTimeSB;
    RulesResponseObject RulesObj = new RulesResponseObject();
    DriverPermissionMethod driverPermissionMethod;
    Animation editLogAnimation;
    boolean isHaulExcptn;
    boolean isAdverseExcptn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_hos_summary, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }

// can 16, us 14
        initView(rootView);

        return rootView;
    }


    void initView(View v) {

        initilizeEldView        = new InitilizeEldView();
        constants               = new Constants();
        global                  = new Globally();
        sharedPref              = new SharedPref();
        hMethods                = new HelperMethods();
        dbHelper                = new DBHelper(getActivity());
        localCalls              = new LocalCalls();
        driverPermissionMethod = new DriverPermissionMethod();

        GetAddFromLatLngRequest = new VolleyRequestWithoutRetry(getActivity());
        GetMilesRequest         = new VolleyRequestWithoutRetry(getActivity());

        EldTitleTV              = (TextView)v.findViewById(R.id.EldTitleTV);
        cycleUsedTimeTV         = (TextView)v.findViewById(R.id.cycleUsedTimeTV);
        breakUsedTimeTV         = (TextView)v.findViewById(R.id.breakUsedTimeTV);
        shiftUsedTimeTV         = (TextView)v.findViewById(R.id.shiftUsedTimeTV);
        statusUsedTimeTV        = (TextView)v.findViewById(R.id.statusUsedTimeTV);
        hosCurrentCycleTV       = (TextView)v.findViewById(R.id.hosCurrentCycleTV);
        breakInfoTV             = (TextView)v.findViewById(R.id.breakInfoTV);
        shiftInfoTV             = (TextView)v.findViewById(R.id.shiftInfoTV);
        cycleInfoTV             = (TextView)v.findViewById(R.id.cycleInfoTV);
        statusInfoTV            = (TextView)v.findViewById(R.id.statusInfoTV);

        statusHosTV             = (TextView)v.findViewById(R.id.statusHosTV);
        hosDistanceTV           = (TextView)v.findViewById(R.id.hosDistanceTV);
        hosLocationTV           = (TextView)v.findViewById(R.id.hosLocationTV);
        hosStatusCircle         = (TextView)v.findViewById(R.id.hosStatusCircle);
        hosStatusTV             = (TextView)v.findViewById(R.id.hosStatusTV);
        nextBrkTitleTV          = (TextView)v.findViewById(R.id.nextBrkTitleTV);
        malfunctionTV           = (TextView)v.findViewById(R.id.malfunctionTV);
        driverMilesTitle        = (TextView)v.findViewById(R.id.driverMilesTitle);

        availableHourBtnTV      = (Button)v.findViewById(R.id.availableHourBtnTV);

        sendLogHosBtn           = (CardView)v.findViewById(R.id.sendLogHosBtn);
        nextBreakCardView       = (CardView)v.findViewById(R.id.nextBreakCardView);
        disabledBreakCardView   = (CardView)v.findViewById(R.id.disabledBreakCardView);

        hosCycleCardView        = (CardView)v.findViewById(R.id.hosCycleCardView);
        hosShiftCardView        = (CardView)v.findViewById(R.id.hosShiftCardView);
        hosStatusCardView       = (CardView)v.findViewById(R.id.hosStatusCardView);
        hosDistanceCardView     = (CardView)v.findViewById(R.id.hosDistanceCardView);
        hosLocationCardView     = (CardView)v.findViewById(R.id.hosLocationCardView);

        malfunctionLay          = (RelativeLayout) v.findViewById(R.id.malfunctionLay);
        rightMenuBtn            = (RelativeLayout) v.findViewById(R.id.rightMenuBtn);
        nextBreakLay            = (LinearLayout)v.findViewById(R.id.nextBreakLay);

        eldMenuLay              = (RelativeLayout)v.findViewById(R.id.eldMenuLay);
        hosMainLay              = (RelativeLayout)v.findViewById(R.id.hosMainLay);

        malfunctionImgView      = (ImageView)v.findViewById(R.id.malfunctionImgView);
        eldMenuBtn              = (ImageView)v.findViewById(R.id.eldMenuBtn);
        hosStatusImgVw          = (ImageView)v.findViewById(R.id.hosStatusImgVw);
        loadingSpinEldIV        = (LoadingSpinImgView)v.findViewById(R.id.loadingSpinEldIV);

        cycleCircularView       = (CircleProgressView)v.findViewById(R.id.cycleCircularView);
        breakCircularView       = (CircleProgressView)v.findViewById(R.id.breakCircularView);
        shiftCircularView       = (CircleProgressView)v.findViewById(R.id.shiftCircularView);
        currentStatusCircularView   = (CircleProgressView)v.findViewById(R.id.statusCircularView);

        eldMenuBtn.setImageResource(R.drawable.back_btn);
        EldTitleTV.setText(getResources().getString(R.string.summary));
        // rightMenuBtn.setVisibility(View.INVISIBLE);

        setMarqueText(hosLocationTV);
        setMarqueText(hosDistanceTV);
        setMarqueText(driverMilesTitle);

        if (sharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            isHaulExcptn    = sharedPref.get16hrHaulExcptn(getActivity());
            isAdverseExcptn = sharedPref.getAdverseExcptn(getActivity());
        }else{
            isHaulExcptn    = sharedPref.get16hrHaulExcptnCo(getActivity());
            isAdverseExcptn = sharedPref.getAdverseExcptnCo(getActivity());
        }

        editLogAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        editLogAnimation.setDuration(1500);

        Bundle bundle          = this.getArguments();
        DRIVER_JOB_STATUS      = bundle.getInt("current_status");
        LeftCycleHoursInt      = bundle.getInt("left_cycle");
        LeftOnDutyHoursInt     = bundle.getInt("left_onduty");
        LeftDrivingHoursInt    = bundle.getInt("left_driving");
        UsedOnDutyHoursInt     = bundle.getInt("total_onduty");
        UsedDrivingHoursInt    = bundle.getInt("total_driving");
        UsedOffDutyHoursInt    = bundle.getInt("total_offduty");
        UsedSleeperHoursInt    = bundle.getInt("total_sleeper");
        UsedShiftHoursInt      = bundle.getInt("shift_used");
        LeftShiftHoursInt      = bundle.getInt("shift_remain");
        offsetFromUTC          = bundle.getInt("offsetFromUTC");

        minOffDutyUsedHours        = bundle.getInt("offDuty_used_hours");
        isMinOffDutyHoursSatisfied = bundle.getBoolean("is_offDuty_hr_satisfied");


        DriverId               = bundle.getString("DriverId");
        DeviceId               = bundle.getString("DeviceId");
        CycleId                = bundle.getString("CycleId");
        isPersonal             = bundle.getString("isPersonal");
        CurrentCycle           = bundle.getString("cycle");

        IsAOBRDAutomatic       = bundle.getBoolean("IsAOBRDAutomatic");
        isSingleDriver         = bundle.getBoolean("isSingleDriver");


        try {
            StateArrayList     =  sharedPref.getStatesInList(getActivity());
            StatePrefManager statePrefManager  = new StatePrefManager();
            StateList          = statePrefManager.GetState(getActivity());
            driverLogArray     = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);

        } catch (Exception e) {
            e.printStackTrace();
            driverLogArray = new JSONArray();
        }


        if(sharedPref.getDriverStatusId("jobType", getActivity()).trim().equals(Globally.OFF_DUTY) ||
                sharedPref.getDriverStatusId("jobType", getActivity()).trim().equals(Globally.SLEEPER)){
            availableHourBtnTV.setVisibility(View.VISIBLE);
        }

        malfunctionImgView.setVisibility(View.VISIBLE);
        String status = initilizeEldView.getCurrentStatus(DRIVER_JOB_STATUS, isPersonal);
        statusHosTV.setText(status);

        breakCircularView.setSeekModeEnabled(false);
        shiftCircularView.setSeekModeEnabled(false);
        currentStatusCircularView.setSeekModeEnabled(false);
        cycleCircularView.setSeekModeEnabled(false);

        if(sharedPref.getVehicleVin(getActivity()).length() > 3){
            vin = "(<b>VIN</b>-" + sharedPref.getVehicleVin(getActivity())+ ")";   // getting from OBD directly (Cuurent VIN)
        }else{
            vin = "(<b>VIN</b>-" + sharedPref.getVINNumber(getActivity()) + ")";   // getting from truck selection when user select Truck and getting its VIN
        }

   /*     String s= getString(R.string.hos_driver_miles);
        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(2f), 0,5, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);// set color
        TextView tv= (TextView) findViewById(R.id.textview);
        tv.setText(ss1);
*/
        driverMilesTitle.setText(Html.fromHtml("<b>" + getString(R.string.hos_driver_miles) + "</b> " + vin) );

        CycleTimeCalculation(true);

        setDataOnStatusView(DRIVER_JOB_STATUS);
        getCycleHours();
        setBreakProgress();
        // Shift value settings
        setProgressbarValues(shiftCircularView, shiftUsedTimeTV, UsedShiftHoursInt, LeftShiftHoursInt);
        setProgressViolationColor(shiftCircularView, LeftShiftHoursInt);

//UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES
        if (UILApplication.getInstance().isNightModeEnabled()) {
            nextBreakCardView.setCardBackgroundColor(getResources().getColor(R.color.gray_hover));
            hosCycleCardView.setCardBackgroundColor(getResources().getColor(R.color.gray_hover));
            hosShiftCardView.setCardBackgroundColor(getResources().getColor(R.color.gray_hover));
            hosStatusCardView.setCardBackgroundColor(getResources().getColor(R.color.gray_hover));
            hosDistanceCardView.setCardBackgroundColor(getResources().getColor(R.color.gray_hover));
            hosLocationCardView.setCardBackgroundColor(getResources().getColor(R.color.gray_hover));
            hosMainLay.setBackgroundColor(getResources().getColor(R.color.gray_background));


        }


        if (constants.IsSendLog(DriverId, driverPermissionMethod, dbHelper) == false) {
             sendLogHosBtn.setCardBackgroundColor(getResources().getColor(R.color.silver));

        }


        editLogAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(sharedPref.isSuggestedEditOccur(getActivity()))
                    malfunctionLay.startAnimation(editLogAnimation);
                else {
                    editLogAnimation.cancel();
                    malfunctionLay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });



        eldMenuLay.setOnClickListener(this);
        sendLogHosBtn.setOnClickListener(this);
        rightMenuBtn.setOnClickListener(this);
        availableHourBtnTV.setOnClickListener(this);
        malfunctionLay.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        RestartTimer();

        if(sharedPref.isSuggestedEditOccur(getActivity())){
            malfunctionTV.setText(getString(R.string.review_carrier_edits));
          //  malfunctionTV.setBackgroundColor(getResources().getColor(R.color.colorSleeper));
            malfunctionLay.setVisibility(View.VISIBLE);
            malfunctionLay.startAnimation(editLogAnimation);
        }else{
            editLogAnimation.cancel();
            malfunctionLay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        clearTimer();

    }


    void setMarqueText(TextView view){
        view.setHorizontallyScrolling(true);
        view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        view.setSingleLine(true);
        view.setMarqueeRepeatLimit(-1);
        view.setSelected(true);

    }


    void setProgressViolationColor(CircleProgressView circularView, int leftTime){
        if(leftTime <= 0){
            circularView.setBarColor(getResources().getColor(R.color.colorVoilation), getResources().getColor(R.color.colorVoilation));
        }
    }

    void setProgressbarValues(CircleProgressView progressView, TextView reportView, int usedHour, int leftHour){
        //     value settings

        progressView.setMaxValue(usedHour + leftHour);
        progressView.setValue(usedHour);
        reportView.setText(Html.fromHtml(getHtmlText("U " + global.FinalValue(usedHour), "R " + global.FinalValue(leftHour))));

        if(leftHour <= 0) {
            reportView.setTextColor(getResources().getColor(R.color.colorVoilation));
        }

    }


    private void setBreakProgress(){
        if( (CycleId.equals(global.USA_WORKING_6_DAYS) || CycleId.equals(global.USA_WORKING_7_DAYS ) ) &&
                (DRIVER_JOB_STATUS == DRIVING || DRIVER_JOB_STATUS == ON_DUTY) ){

            String timeType = "Hours";
            if(LeftNextBreak < 60){
                timeType = "Mins";
            }

            breakUsedTimeTV.setText(Html.fromHtml(getHtmlText("in " + global.FinalValue(LeftNextBreak), timeType)) );
            breakCircularView.setMaxValue(ShiftHour); // 8 * 60 = 480 (8 hours)
            breakCircularView.setValue(ShiftHour - LeftNextBreak);

            if(LeftNextBreak > 0 && LeftNextBreak <= 120){


                breakCircularView.setBarColor(getResources().getColor(R.color.colorSleeper), getResources().getColor(R.color.colorSleeper));

                if(LeftNextBreak < 60){
                    breakInfoTV.setVisibility(View.VISIBLE);
                    breakInfoTV.setText(getResources().getString(R.string.hos_violation_apprch));
                    breakUsedTimeTV.setTextColor(getResources().getColor(R.color.colorSleeper));
                }else{
                    breakInfoTV.setVisibility(View.GONE);
                }

            }else if(LeftNextBreak <= 0){
                breakInfoTV.setVisibility(View.VISIBLE);
                breakInfoTV.setText(violationReason);
                breakCircularView.setBarColor(getResources().getColor(R.color.colorVoilation), getResources().getColor(R.color.colorVoilation));
                breakUsedTimeTV.setTextColor(getResources().getColor(R.color.colorVoilation));
            }else{
                breakInfoTV.setVisibility(View.GONE);
            }

       /* }else  if( (CycleId.equals(global.CANADA_CYCLE_1) || CycleId.equals(global.CANADA_CYCLE_2 ) ) &&
                (DRIVER_JOB_STATUS == DRIVING || DRIVER_JOB_STATUS == ON_DUTY) ){

            if(isMinOffDutyHoursSatisfied){
                disabledBreakCardView.setVisibility(View.VISIBLE);
            }else {
                breakCircularView.setBarColor(getResources().getColor(R.color.colorSleeper), getResources().getColor(R.color.colorDriving));

                int remainingTime = CanadaBreakHour - minOffDutyUsedHours;
                if(remainingTime < 0){
                    remainingTime = 0;
                }else if(remainingTime > 120){
                    remainingTime = CanadaBreakHour;
                }
                breakUsedTimeTV.setText(Html.fromHtml(getHtmlText("U " + global.FinalValue(minOffDutyUsedHours), "R " + global.FinalValue(remainingTime) )));
                breakCircularView.setMaxValue(CanadaBreakHour); // 8 * 60 = 480 (8 hours)
                breakCircularView.setValue(minOffDutyUsedHours);
                breakInfoTV.setText("Need 2 hours mandatory OffDuty");
                breakInfoTV.setVisibility(View.VISIBLE);
                nextBrkTitleTV.setText("Mandatory Break");

            }

       */ }else{
            disabledBreakCardView.setVisibility(View.VISIBLE);

        }
    }

    private void getCycleHours(){
        if(CurrentCycle.length() > 0 && !CurrentCycle.equals("null")){
            String[] arr = CurrentCycle.split(" ");
            if(arr.length > 0){
                String[] cycleHourArr = arr[1].split("/");
                if(cycleHourArr.length > 0){
                    TotalCycleHour = Integer.valueOf(cycleHourArr[0]) * 60 ;
                    int leftCycle = TotalCycleHour - LeftCycleHoursInt;

                    cycleUsedTimeTV.setText(Html.fromHtml(getHtmlText("U " + global.FinalValue(leftCycle), "R " + global.FinalValue(LeftCycleHoursInt))));

                    cycleCircularView.setMaxValue(TotalCycleHour);
                    cycleCircularView.setValue(leftCycle);

                    setProgressViolationColor(cycleCircularView, LeftCycleHoursInt);

                    if(LeftCycleHoursInt <= 0 ){
                        cycleInfoTV.setVisibility(View.VISIBLE);
                        cycleInfoTV.setText(cycleViolationReason.toLowerCase());
                        cycleUsedTimeTV.setTextColor(getResources().getColor(R.color.colorVoilation));
                    }
                }
            }
            hosCurrentCycleTV.setText(CurrentCycle);
        }


    }


    private String getHtmlText(String usedTime, String remainedTime){
        return "<html> " + usedTime + "<br/> <small> <font  color='"+ HOS_REMAIN_COLOR +"'>"+ remainedTime +"</font> </html> ";
    }

    private String getHtmlTextForOffSleeper(String usedTime, String remainedTime){
        return "<html> <font  color='"+ HOS_SLEEP_OFF_COLOR +"'>" + usedTime + "</font> <br/> <small> <font  color='"+ HOS_REMAIN_COLOR +"'>"+ remainedTime +"</font> </html> ";
    }

    //<font size=6 color='"+ usedTextColor +"'><b>"+ usedTime +"</b></font>

    private void setSBOffView(int maxTime, int usedTime){
        // set view theme according to Off duty status


        currentStatusCircularView.setMaxValue(maxTime);
        currentStatusCircularView.setValue(usedTime);
        String hrMinFormat = "Hours";
        if(usedTime < 60 ){
            hrMinFormat = "Mins";
        }
        String text =  getHtmlTextForOffSleeper( "U " + global.FinalValue(usedTime), hrMinFormat);
        statusUsedTimeTV.setText(Html.fromHtml( text ));


        if(DRIVER_JOB_STATUS == OFF_DUTY && isPersonal.equals("true")){
            hosStatusCircle.setBackgroundResource(R.drawable.circular_view_status);
            hosStatusTV.setTextColor(getResources().getColor(R.color.hos_current_status));
            hosStatusImgVw.setImageResource(R.drawable.hos_driving);
            currentStatusCircularView.setBarColor(getResources().getColor(R.color.hos_current_status), getResources().getColor(R.color.hos_current_status));
        }else{
            hosStatusCircle.setBackgroundResource(R.drawable.circular_view_sleeper);
            hosStatusTV.setTextColor(getResources().getColor(R.color.hos_sleeper));
            hosStatusImgVw.setImageResource(R.drawable.hos_sleeper);
            currentStatusCircularView.setBarColor(getResources().getColor(R.color.hos_sleeper), getResources().getColor(R.color.hos_sleeper));

            String statusTxt = "<b> Shift: </b> Sleeper + Off Duty<br/> " +
                    "&nbsp; &nbsp; &nbsp; <b>U " + global.FinalValue((int)usedAndRemainingTimeSB.getSleeperUsedMinutes()) + " || " +
                    "R " + global.FinalValue((int)usedAndRemainingTimeSB.getSleeperRemainingMinutes())+" </b>";
            statusInfoTV.setText(Html.fromHtml( statusTxt ));
            statusInfoTV.setVisibility(View.VISIBLE);

        }


    }


    private void setDataOnStatusView(int status){
        String text = "";

        switch (status){

            case OFF_DUTY:
            case SLEEPER:

                setSBOffView((int)usedAndRemainingTimeSB.getSleeperBirthMinutes(), (int)usedAndRemainingTimeSB.getSleeperUsedMinutes());

                break;


            case DRIVING:

                setShiftEndTime();

                hosStatusImgVw.setImageResource(R.drawable.hos_driving);

                //set gradient color to circular progress view
                if(LeftDrivingHoursInt <= 0) {
                    currentStatusCircularView.setBarColor(getResources().getColor(R.color.colorVoilation), getResources().getColor(R.color.colorVoilation));
                    statusInfoTV.setTextColor(getResources().getColor(R.color.colorVoilation));
                }else{
                    currentStatusCircularView.setBarColor(getResources().getColor(R.color.hos_current_status), getResources().getColor(R.color.colorVoilation));
                    statusInfoTV.setTextColor(getResources().getColor(R.color.hos_current_status));
                }

                // Driving value settings
                setProgressbarValues(currentStatusCircularView, statusUsedTimeTV, UsedDrivingHoursInt, LeftDrivingHoursInt);

                break;


            case ON_DUTY:

                setShiftEndTime();

                hosStatusImgVw.setImageResource(R.drawable.hos_status);

                //set gradient color to circular progress view
                if(LeftOnDutyHoursInt <= 0) {
                    currentStatusCircularView.setBarColor(getResources().getColor(R.color.colorVoilation), getResources().getColor(R.color.colorVoilation));
                    statusInfoTV.setTextColor(getResources().getColor(R.color.colorVoilation));
                }else{
                    currentStatusCircularView.setBarColor(getResources().getColor(R.color.hos_current_status), getResources().getColor(R.color.colorVoilation));
                }

                // OnDuty value settings
                setProgressbarValues(currentStatusCircularView, statusUsedTimeTV, UsedOnDutyHoursInt, LeftOnDutyHoursInt);

                break;

        }



    }


    void setShiftEndTime(){
        if(LeftShiftHoursInt > 0 ) {
            shiftInfoTV.setVisibility(View.VISIBLE);
            shiftInfoTV.setTextColor(getResources().getColor(R.color.black_semi));
            DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);
            DateTime endShiftDateTime = currentDateTime.plusMinutes(LeftShiftHoursInt);
            String endDate = global.convertUSTtoMM_dd_yyyy_hh_mm(endShiftDateTime.toString());
            Log.d("date", "converted Date: " + endDate);
            shiftInfoTV.setText(Html.fromHtml("&nbsp; &nbsp; <b>Shift Ends At </b> <br/>" + endDate) );
        }else{
            shiftInfoTV.setText(shiftViolationReson.toLowerCase());
            shiftInfoTV.setVisibility(View.VISIBLE);
            shiftInfoTV.setTextColor(getResources().getColor(R.color.colorVoilation));
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
            shareDialog = new ShareDriverLogDialog(getActivity(), getActivity(), DriverId, DeviceId, CycleId,
                    IsAOBRD, StateArrayList, StateList);
            shareDialog.show();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    void calculateLocalOdometersDistance(){
        try{

            String dayStartSavedDate    = sharedPref.getDayStartSavedTime(getActivity());
            String dayStartOdometerStr  = sharedPref.getDayStartOdometer(getActivity());
            String currentOdometerStr   = sharedPref.getHighPrecisionOdometer(getActivity());

            if(dayStartSavedDate.length() > 0) {
                int dayDiff = constants.getDayDiff(dayStartSavedDate, currentOdometerStr);
                if (dayDiff == 0) {
                    if (sharedPref.getObdStatus(getActivity()) == Constants.WIRED_ACTIVE || sharedPref.getObdStatus(getActivity()) == Constants.WIFI_ACTIVE) {
                        if (currentOdometerStr.contains(".")) {
                            currentOdometerStr = "" + Double.parseDouble(currentOdometerStr) * 1000;
                        }
                    }

                    int currentOdometerInMiles = constants.meterToMiles(Integer.valueOf(currentOdometerStr));
                    int distanceInMiles = currentOdometerInMiles - Integer.valueOf(dayStartOdometerStr);

                    String distanceAndVin = "(" + dayStartOdometerStr + " - " + currentOdometerInMiles + ") = <b>" + distanceInMiles + " Miles </b>";
                    hosDistanceTV.setText(Html.fromHtml(distanceAndVin));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void CycleTimeCalculation(boolean isUpdateUI ) {

        try {

            calculateLocalOdometersDistance();

            if (isUpdateUI && global.isConnected(getActivity())) {
                GetAddFromLatLng();
                GetEngineMiles();
            }

            currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);    // Current Date Time
            currentUTCTime = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), true);

            currentDayArray = hMethods.GetSingleDateArray(driverLogArray, currentDateTime, currentDateTime, currentUTCTime, true, offsetFromUTC);
            rulesVersion = sharedPref.GetRulesVersion(getActivity());

            List<DriverLog> oDriverLogDetail = hMethods.getSavedLogList(Integer.valueOf(DriverId), currentDateTime, currentUTCTime, dbHelper);
            DriverDetail oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DriverId),
                    offsetFromUTC, Integer.valueOf(CycleId), isSingleDriver, DRIVER_JOB_STATUS, false,
                    isHaulExcptn, isAdverseExcptn,
                    rulesVersion, oDriverLogDetail);

            RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CycleId), Integer.valueOf(DRIVER_JOB_STATUS), oDriverDetail);


                if (isUpdateUI) {
                    // Calculate 2 days data to get remaining Driving/Onduty hours
                    RulesResponseObject remainingTimeObj = hMethods.getRemainingTime(currentDateTime, currentUTCTime, offsetFromUTC,
                            Integer.valueOf(CycleId), isSingleDriver, Integer.valueOf(DriverId), DRIVER_JOB_STATUS,
                            false, isHaulExcptn,
                            isAdverseExcptn, rulesVersion, dbHelper);

                    LeftNextBreak = (int) localCalls.usaBreakRemainingHours(oDriverDetail).getBreakRemainingMinutes();

                    LeftCycleHoursInt = (int) RulesObj.getCycleRemainingMinutes();

                    UsedOnDutyHoursInt = (int) remainingTimeObj.getOnDutyUsedMinutes();
                    LeftOnDutyHoursInt = (int) remainingTimeObj.getOnDutyRemainingMinutes();
                    UsedDrivingHoursInt = (int) remainingTimeObj.getDrivingUsedMinutes();
                    LeftDrivingHoursInt = (int) remainingTimeObj.getDrivingRemainingMinutes();


                    LeftShiftHoursInt = (int) remainingTimeObj.getShiftRemainingMinutes();
                    UsedShiftHoursInt = (int) remainingTimeObj.getShiftUsedMinutes();

                    minOffDutyUsedHours = (int) remainingTimeObj.getMinimumOffDutyUsedHours();
                    isMinOffDutyHoursSatisfied = remainingTimeObj.isMinimumOffDutyHoursSatisfied();


                    UsedOffDutyHoursInt = hMethods.GetOffDutyTime(currentDayArray);
                    UsedSleeperHoursInt = hMethods.GetSleeperTime(currentDayArray);
                    usedAndRemainingTimeSB = localCalls.getUsedAndRemainingTimeSB(oDriverDetail);


                    if (LeftShiftHoursInt < 0) {
                        LeftShiftHoursInt = 0;
                    }
                    if (LeftOnDutyHoursInt < 0) {
                        LeftOnDutyHoursInt = 0;
                    }
                    if (LeftDrivingHoursInt < 0) {
                        LeftDrivingHoursInt = 0;
                    }

                    if (LeftCycleHoursInt < 0) {
                        LeftDrivingHoursInt = 0;
                        LeftCycleHoursInt = 0;
                        LeftOnDutyHoursInt = 0;
                        LeftShiftHoursInt = 0;
                    } else if (LeftCycleHoursInt < LeftDrivingHoursInt || LeftCycleHoursInt < LeftOnDutyHoursInt) {
                        if (LeftCycleHoursInt < LeftDrivingHoursInt) {
                            LeftDrivingHoursInt = LeftCycleHoursInt;
                        }
                        LeftOnDutyHoursInt = LeftCycleHoursInt;
                    }

                    if (RulesObj.isViolation()) {
                        cycleViolationReason = RulesObj.getViolationReason();
                        shiftViolationReson = RulesObj.getViolationReason();
                        LeftNextBreak = 0;
                        LeftDrivingHoursInt = 0;
                        LeftOnDutyHoursInt = 0;
                    } else {
                        shiftViolationReson = "";
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        if (isUpdateUI) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // value settings
                    setDataOnStatusView(DRIVER_JOB_STATUS);
                    getCycleHours();
                    setBreakProgress();

                    setProgressbarValues(shiftCircularView, shiftUsedTimeTV, UsedShiftHoursInt, LeftShiftHoursInt);
                    setProgressViolationColor(shiftCircularView, LeftShiftHoursInt);

                }
            });
        }
    }



    private class HosTimerTask extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run() {
            CycleTimeCalculation(true);
        }
    }

    private void RestartTimer() {
        try {
            clearTimer();
            mTimer = new Timer();
            timerTask = new HosTimerTask();
            mTimer.schedule(timerTask, MIN_TIME_BW_UPDATES, MIN_TIME_BW_UPDATES);
        } catch (Exception e) {
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
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.eldMenuLay:
                getFragmentManager().popBackStack();

                break;

            case R.id.sendLogHosBtn:

                if (constants.IsSendLog(DriverId, driverPermissionMethod, dbHelper)) {
                    shareDriverLogDialog();
                }else{
                    global.EldToastWithDuration(sendLogHosBtn, getResources().getString(R.string.share_not_allowed), getResources().getColor(R.color.colorVoilation) );
                }

                break;

            case R.id.rightMenuBtn:

                isRefreshBtnClicked = true;
                loadingSpinEldIV.startAnimation();
                CycleTimeCalculation(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity() != null){
                            if(!global.isConnected(getActivity())) {
                                loadingSpinEldIV.stopAnimation();
                                global.EldScreenToast(loadingSpinEldIV, ConstantsEnum.HOS_NOT_REFRESHED,
                                        getResources().getColor(R.color.colorSleeper));
                            }
                        }

                    }
                },Constants.SocketTimeout1Sec);
                break;


            case R.id.availableHourBtnTV:

                availableHourBtnTV.setEnabled(false);
                CycleTimeCalculation(false);

               RulesResponseObject RemainingTimeObj = hMethods.getRemainingTime(currentDateTime, currentUTCTime, offsetFromUTC,
                        Integer.valueOf(CycleId), isSingleDriver, Integer.valueOf(DriverId), Constants.DRIVING, false,
                       isHaulExcptn, isAdverseExcptn,
                        rulesVersion, dbHelper);

                HosInfoDialog dialog = new HosInfoDialog(getActivity(), RemainingTimeObj, RulesObj);
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        availableHourBtnTV.setEnabled(true);
                    }
                }, Constants.SocketTimeout1Sec);
                Log.d("DrivingRemainingMinutes", "DrivingRemainingMinutes: " + RemainingTimeObj.getDrivingRemainingMinutes());
                break;


            case R.id.malfunctionLay:
                Intent editIntent = new Intent(getActivity(), SuggestedFragmentActivity.class);
                editIntent.putExtra(ConstantsKeys.suggested_data, "");
                editIntent.putExtra(ConstantsKeys.Date, "");
                startActivity(editIntent);
                break;

        }
    }



    //*================== Get Address From Lat Lng ===================*//*
    void GetAddFromLatLng() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("Latitude", global.LATITUDE);
        params.put("Longitude", global.LONGITUDE);
        params.put("IsAOBRDAutomatic", String.valueOf(IsAOBRDAutomatic));

        GetAddFromLatLngRequest.executeRequest(Request.Method.POST, APIs.GET_Add_FROM_LAT_LNG, params, GetAddFromLatLng,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Engine Miles===================*//*
    void GetEngineMiles() {
        String CompanyId    = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
        String VIN          = DriverConst.GetDriverTripDetails(DriverConst.VIN, getActivity());

        // get device current date and day start UTC date
        String currentDateStr = global.GetCurrentDateTime();
        currentDateTime     = global.getDateTimeObj(currentDateStr, false);    // Current Date Time
        String cDate = currentDateStr;
        cDate = cDate.split("T")[0]+"T00:00:00";
        DateTime currentStartUtcDate = new DateTime(cDate);
        String utcDateStr = String.valueOf(new DateTime(Globally.getDateTimeObj(currentStartUtcDate.toString(), false)) );
        Log.d("cDate","cDate11: " +  utcDateStr);



        int isOdometer = sharedPref.IsOdometerFromOBD(getActivity()) ? 1 : 0;

        Map<String, String> params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("DeviceId", DeviceId );
        params.put("IsOdometerFromOBD", String.valueOf(isOdometer) );
        params.put("VIN", VIN);
        params.put("CompanyId", CompanyId);
        params.put("LogDate", String.valueOf(currentDateTime) );
        params.put("UTCStartDateTime", utcDateStr );

        GetMilesRequest.executeRequest(Request.Method.POST, APIs.GET_DRIVER_MILES, params, GetMiles,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);


    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void getResponse(String response, int flag) {


            JSONObject obj = null;
            String status = "";

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");
            } catch (JSONException e) {
            }
            loadingSpinEldIV.stopAnimation();

            if (status.equalsIgnoreCase("true")) {

                switch (flag) {

                    case GetAddFromLatLng:
                        if (!obj.isNull("Data")) {
                            try {

                                JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                                //String Country     = dataJObject.getString(ConstantsKeys.Country);
                                String AddressLine = dataJObject.getString(ConstantsKeys.Location) ;   //+ ", " + Country

                                hosLocationTV.setText(AddressLine);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        break;

                    case GetMiles:
                        Log.d("getMiles", "response: " + response);


                        try {

                            if(isRefreshBtnClicked){
                                global.EldScreenToast(loadingSpinEldIV, ConstantsEnum.UPDATED, getResources().getColor(R.color.colorPrimary));
                            }
                            if (!obj.isNull("Data")) {
                                JSONObject dataObj = new JSONObject(obj.getString("Data"));
                                String Miles = dataObj.getString("Miles");
                                String StartOdometer = dataObj.getString("StartOdometer");
                                String EndOdometer = dataObj.getString("EndOdometer");


                                String distance = "(" + StartOdometer + " - " + EndOdometer + ") = <b>" + Miles + " Miles </b>" ;
                                hosDistanceTV.setText(Html.fromHtml(distance));

                                // saved day start odometer locally to calculate distance
                                sharedPref.setDayStartOdometer(StartOdometer, global.GetCurrentDateTime(), getActivity());
                            }
                        }catch (Exception e){
                            hosDistanceTV.setText(Html.fromHtml(" <b>" + "-- </b>" ));
                            e.printStackTrace();
                        }
                        break;
                }

            }else{
                if(flag == GetMiles){
                    if(isRefreshBtnClicked){
                        global.EldToastWithDuration(loadingSpinEldIV, ConstantsEnum.HOS_NOT_REFRESHED,
                                getResources().getColor(R.color.colorSleeper));
                    }
                }
            }

            isRefreshBtnClicked = false;
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

        @Override
        public void getError(VolleyError error, int flag) {
            Log.d("getMiles", "error: " + error);
            try {

                if (flag == GetMiles) {
                    if (isRefreshBtnClicked) {
                        if (getActivity() != null) {
                            global.EldToastWithDuration(loadingSpinEldIV, ConstantsEnum.HOS_NOT_REFRESHED,
                                    getResources().getColor(R.color.colorSleeper));
                        }
                    }
                }
                loadingSpinEldIV.stopAnimation();

            }catch (Exception e){
                e.printStackTrace();
            }
            isRefreshBtnClicked = false;
        }
    };

}
