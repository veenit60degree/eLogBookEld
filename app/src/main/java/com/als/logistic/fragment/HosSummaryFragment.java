package com.als.logistic.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.CsvReader;
import com.constants.InitilizeEldView;
import com.constants.LoadingSpinImgView;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.constants.VolleyRequestWithoutRetry;
import com.custom.dialogs.HosInfoDialog;
import com.custom.dialogs.ObdDataInfoDialog;
import com.custom.dialogs.ShareDriverLogDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.SuggestedFragmentActivity;
import com.als.logistic.UILApplication;
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
    TextView EldTitleTV, hosDistanceTV, hosLocationTV, hosOperatingZoneTV, statusNewInfoTV, malfunctionTV;
    TextView perShiftDrivingTV,perShiftOnDutyTV,perShiftNextBreakTV,perShiftTV,perDayDrivingTV,perDayOnDutyTV,perDayOffDutyTV,cycleTV,currentCycleHosTV,hosPerDayTv,hosCycleTV;
    TextView perShiftUsedDrivingTV,perShiftUsedOnDutyTV,perShiftUsedOffDutyTV,perShiftUsedTimeTV;
    TextView perDayUsedDrivingTV,perDayUsedOnDutyTV,perDayUsedOffDutyTV,cycleUsedTimeTV,hosCycleTVUsa,offDutyRestTV, accPerDistanceTv;
    ImageView eldMenuBtn,hosFlagImgView;
    LoadingSpinImgView loadingSpinEldIV;
    RelativeLayout eldMenuLay, obdHosInfoImg, malfunctionLay;
    RelativeLayout shiftLeftLay, perDayLeftLay, jobMiddleLowerLay, jobRightUpperLay, jobRightLowerLay;
    LinearLayout jobMiddleUpperLay;
    CircleProgressView breakCircularView, shiftCircularView, perShiftCurrentDrivingCircularView,perShiftCurrentOnDutyCircularView,perShiftCurrentOffDutyCircularView,perDayCurrentDrivingCircularView,perDayCurrentOnDutyCircularView,perDayCurrentOffDutyCircularView,cycleCircularView,hosUsaCycleCircularView;
    CardView hosDistanceCardView, hosLocationCardView,sendLogHosBtn,hosPerDayDrivingCardView,hosPerDayOnDutyCardView,hosPerDayOffDutyCardView,hosCycleCardView,hosPerShiftDrivingCardView,hosPerShiftOnDutyCardView,hosPerShiftBreakCardView;
    RelativeLayout rightMenuBtn;
    LinearLayout hos_CycleViewChange;
    String HOS_REMAIN_COLOR = "#6F6F6F";
    String HOS_SLEEP_OFF_COLOR = "#90A4AE";
    String isPersonal = "false", CurrentCycle = "";
    String violationReason = "30 mins break violation";
    String cycleViolationReason = "", shiftViolationReson = "";
    String DriverId;
    String DeviceId;
    String CycleId;
    String LocationFromApi = "";
    SwitchCompat dotSwitchButton;
    CsvReader csvReader;

    long MIN_TIME_BW_UPDATES = 30000;  // 30 Sec
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
    int TotalOffDutyHour = 600;
    int LeftCycleHoursInt;
    int LeftOnDutyHoursInt;
    int UsedOnDutyHoursInt;
    int LeftDrivingHoursInt;
    int UsedDrivingHoursInt;
    int UsedOffDutyHoursInt;
    int UsedDayOffDutyHoursInt;
    int LeftOffDutyHoursInt;
    int UsedSleeperHoursInt;
    int UsedShiftHoursInt;
    int LeftShiftHoursInt;
    int LeftDayDrivingHoursInt;
    int UsedDayDrivingHoursInt;
    int LeftDayOnDutyDrivingHoursInt;
    int UsedDayOnDutyDrivingHoursInt;
    int LeftDayOffDutyDrivingHoursInt;
    int LeftNextBreak;
    int UsedNextBreak;
    int offsetFromUTC;
    int OffDutyMax = 30;    // in min
    int minOffDutyUsedHours = 0;
    int rulesVersion;

    boolean IsAOBRDAutomatic = true, isSingleDriver, isOnCreate = true;
    boolean isRefreshBtnClicked = false, isMinOffDutyHoursSatisfied = false;

    DateTime currentDateTime , currentUTCTime;
    List<String> StateArrayList;
    List<DriverLocationModel> StateList;
    JSONArray driverLogArray, currentDayArray;
    ShareDriverLogDialog shareDialog;
    InitilizeEldView initilizeEldView;
    Constants constants;
    Globally global;
    HelperMethods hMethods;
    DBHelper dbHelper;
    HosTimerTask timerTask;
    private Timer mTimer;
    VolleyRequestWithoutRetry GetAddFromLatLngRequest, GetMilesRequest;
    LocalCalls localCalls;
    RulesResponseObject usedAndRemainingTimeSB;
    RulesResponseObject RulesObj = new RulesResponseObject();
    DriverPermissionMethod driverPermissionMethod;
    Animation editLogAnimation;
    boolean isHaulExcptn;
    boolean isAdverseExcptn;
    boolean isNorthCanada;
    boolean isAutoCalled = false;
    double distanceInKm = 0;

    int deferralBlueLightDark;
    int resetWhiteColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Constants.IS_ACTIVE_HOS = true;
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }

        if(UILApplication.getInstance().isNightModeEnabled()){
            getActivity().setTheme(R.style.DarkTheme);
            deferralBlueLightDark = getResources().getColor(R.color.blue_hos);
            resetWhiteColor = getResources().getColor(R.color.hos_card_dark);
        } else {
            getActivity().setTheme(R.style.LightTheme);
            deferralBlueLightDark = getResources().getColor(R.color.deferral_light_blue);
            resetWhiteColor = getResources().getColor(R.color.white);
        }

        try {
            rootView = inflater.inflate(R.layout.hos_summary, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }

        Constants.IS_ACTIVE_ELD = false;

        initView(rootView);

        return rootView;
    }


    void initView(View v) {

        initilizeEldView        = new InitilizeEldView();
        constants               = new Constants();
        global                  = new Globally();
        hMethods                = new HelperMethods();
        csvReader               = new CsvReader();
        dbHelper                = new DBHelper(getActivity());
        localCalls              = new LocalCalls();
        driverPermissionMethod  = new DriverPermissionMethod();

        GetAddFromLatLngRequest = new VolleyRequestWithoutRetry(getActivity());
        GetMilesRequest         = new VolleyRequestWithoutRetry(getActivity());

        EldTitleTV              = (TextView)v.findViewById(R.id.EldTitleTV);
        hosDistanceTV           = (TextView)v.findViewById(R.id.hosDistanceTV);
        hosLocationTV           = (TextView)v.findViewById(R.id.hosLocationTV);
        cycleUsedTimeTV         = (TextView)v.findViewById(R.id.hosUsedCycleTV);
        perShiftUsedTimeTV      = (TextView)v.findViewById(R.id.hosShiftUsedTV);
        perShiftUsedOffDutyTV     = (TextView)v.findViewById(R.id.hosShiftNextBreakUsedTV);
        perShiftUsedDrivingTV   = (TextView)v.findViewById(R.id.drivingUsedTime);
        perShiftUsedOnDutyTV    = (TextView)v.findViewById(R.id.hosShiftUsedOnDutyTv);
        perDayUsedDrivingTV     = (TextView)v.findViewById(R.id.hosPerDayUsedDrivingTV);
        perDayUsedOnDutyTV      = (TextView)v.findViewById(R.id.hosPerDayUsedOnDutyTV);
        perDayUsedOffDutyTV     = (TextView)v.findViewById(R.id.hosPerDayUsedOffDutyTV);
        hosPerDayTv             = (TextView)v.findViewById(R.id.hosPerDayTv);
        hosCycleTV              = (TextView)v.findViewById(R.id.hosCycleTV);
        sendLogHosBtn           = (CardView)v.findViewById(R.id.sendLogHosBtn);
        hosPerDayOnDutyCardView      = (CardView)v.findViewById(R.id.hosPerDayOnDutyCardView);
        hosPerDayOffDutyCardView     = (CardView)v.findViewById(R.id.hosPerDayOffDutyCardView);
        hosPerDayDrivingCardView     = (CardView)v.findViewById(R.id.hosPerDayDrivingCardView);
        hosCycleCardView             = (CardView)v.findViewById(R.id.hosCycleCardView);
        hosPerShiftDrivingCardView   = (CardView)v.findViewById(R.id.hosPerShiftDrivingCardView);
        hosPerShiftOnDutyCardView    = (CardView)v.findViewById(R.id.hosPerShiftOnDutyCardView);
        hosPerShiftBreakCardView     = (CardView)v.findViewById(R.id.hosPerShiftBreakCardView);

        eldMenuLay              = (RelativeLayout)v.findViewById(R.id.eldMenuLay);
        obdHosInfoImg           = (RelativeLayout)v.findViewById(R.id.obdHosInfoImg);
        malfunctionLay          = (RelativeLayout)v.findViewById(R.id.malfunctionLay);

        shiftLeftLay            = (RelativeLayout)v.findViewById(R.id.shiftLeftLay);
        perDayLeftLay           = (RelativeLayout)v.findViewById(R.id.perDayLeftLay);
        jobMiddleLowerLay       = (RelativeLayout)v.findViewById(R.id.jobMiddleLowerLay);
        jobRightUpperLay        = (RelativeLayout)v.findViewById(R.id.jobRightUpperLay);
        jobRightLowerLay        = (RelativeLayout)v.findViewById(R.id.jobRightLowerLay);

        jobMiddleUpperLay       = (LinearLayout)v.findViewById(R.id.jobMiddleUpperLay);


        eldMenuBtn              = (ImageView)v.findViewById(R.id.eldMenuBtn);

        cycleCircularView       = (CircleProgressView)v.findViewById(R.id.cycleCircularView);
        breakCircularView       = (CircleProgressView)v.findViewById(R.id.shiftNextBreakCircularView);
        shiftCircularView       = (CircleProgressView)v.findViewById(R.id.shiftCircularView);
        perShiftCurrentDrivingCircularView   = (CircleProgressView)v.findViewById(R.id.shiftDrivingCircularView);
        perShiftCurrentOnDutyCircularView    = (CircleProgressView)v.findViewById(R.id.shiftOnDutyCircularView);
        perDayCurrentDrivingCircularView     = (CircleProgressView)v.findViewById(R.id.perDayDrivingCircularView);
        perDayCurrentOnDutyCircularView      = (CircleProgressView)v.findViewById(R.id.perDayOnDutyCircularView);
        perDayCurrentOffDutyCircularView     = (CircleProgressView)v.findViewById(R.id.perDayOffDutyCircularView);

        hosDistanceCardView     = (CardView)v.findViewById(R.id.hosDistanceCardView);
        hosLocationCardView     = (CardView)v.findViewById(R.id.hosLocationCardView);

        perShiftDrivingTV       = (TextView)v.findViewById(R.id.hosShiftDrivingTV);
        perShiftOnDutyTV        = (TextView)v.findViewById(R.id.hosShiftOnDutyTV);
        perShiftNextBreakTV     = (TextView)v.findViewById(R.id.hosShiftNextBreakTV);
        perShiftTV              = (TextView)v.findViewById(R.id.hosShiftTV);
        perDayDrivingTV         = (TextView)v.findViewById(R.id.hosPerDayDrivingTV);
        perDayOnDutyTV          = (TextView)v.findViewById(R.id.hosPerDayOnDutyTV);
        perDayOffDutyTV         = (TextView)v.findViewById(R.id.hosPerDayOffDutyTV);
        cycleTV                 = (TextView)v.findViewById(R.id.hosCycleTV);
        hosCycleTVUsa           = (TextView)v.findViewById(R.id.hosCycleTVUsa);
        offDutyRestTV           = (TextView)v.findViewById(R.id.hos_OffDutyRestTV);
        hosOperatingZoneTV      = (TextView)v.findViewById(R.id.hosOperatingZoneTV);
        statusNewInfoTV         = (TextView)v.findViewById(R.id.statusNewInfoTV);
        accPerDistanceTv        = (TextView)v.findViewById(R.id.accPerDistanceTv);
        malfunctionTV           = (TextView)v.findViewById(R.id.malfunctionTV);

        hosFlagImgView       = (ImageView)v.findViewById(R.id.hosFlagView);
        loadingSpinEldIV     = (LoadingSpinImgView)v.findViewById(R.id.loadingSpinEldIV);
        rightMenuBtn         = (RelativeLayout) v.findViewById(R.id.rightMenuBtn);
        dotSwitchButton      = (SwitchCompat) v.findViewById(R.id.dotSwitchButton);
        hos_CycleViewChange  = (LinearLayout) v.findViewById(R.id.hos_CycleViewChange);

        isNorthCanada  =  SharedPref.IsNorthCanada(getActivity());

        if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            isHaulExcptn    = SharedPref.get16hrHaulExcptn(getActivity());
            isAdverseExcptn = SharedPref.getAdverseExcptn(getActivity());
        }else{
            isHaulExcptn    = SharedPref.get16hrHaulExcptnCo(getActivity());
            isAdverseExcptn = SharedPref.getAdverseExcptnCo(getActivity());
        }

        editLogAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        editLogAnimation.setDuration(1500);


        getBundleData();
        dotSwitchButton.setChecked(false);
        dotSwitchButton.setVisibility(View.VISIBLE);

        if(SharedPref.IsNorthCanada(getActivity())){
            hosOperatingZoneTV.setText(getString(R.string.North));
        }else{
            hosOperatingZoneTV.setText(getString(R.string.South));
        }
        if(CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)){
            hosFlagImgView.setImageResource(R.drawable.can_flag);
            hosPerDayTv.setText("PER DAY");
            perDayDrivingTV.setText("Driving");
            perDayDrivingTV.setVisibility(View.VISIBLE);
            hos_CycleViewChange.setVisibility(View.GONE);
            hosCycleTV.setText(CurrentCycle);
            hosPerDayOnDutyCardView.setVisibility(View.VISIBLE);
            hosPerDayOffDutyCardView.setVisibility(View.VISIBLE);
            hosCycleCardView.setVisibility(View.VISIBLE);
            hosOperatingZoneTV.setVisibility(View.VISIBLE);
            dotSwitchButton.setText("CAN");

        }else{
            hosFlagImgView.setImageResource(R.drawable.usa_flag);
            hosPerDayTv.setText("CYCLE");
            perDayDrivingTV.setText("Cycle");
            perDayDrivingTV.setVisibility(View.GONE);
            hos_CycleViewChange.setVisibility(View.VISIBLE);
            hosOperatingZoneTV.setVisibility(View.GONE);
            hosCycleTVUsa.setText("(" + CurrentCycle + ")");
            hosCycleTV.setText(CurrentCycle);
            dotSwitchButton.setText("USA");
        }



        try {
            StateArrayList     =  SharedPref.getStatesInList(getActivity());
            StatePrefManager statePrefManager  = new StatePrefManager();
            StateList          = statePrefManager.GetState(getActivity());
            //driverLogArray     = hMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);

        } catch (Exception e) {
            e.printStackTrace();
            // driverLogArray = new JSONArray();
        }


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eldMenuBtn.setImageResource(R.drawable.back_btn);
                EldTitleTV.setText(getResources().getString(R.string.summary));

                setMarqueText(hosLocationTV);
                setMarqueText(hosDistanceTV);

                breakCircularView.setSeekModeEnabled(false);
                shiftCircularView.setSeekModeEnabled(false);
                perShiftCurrentDrivingCircularView.setSeekModeEnabled(false);
                perDayCurrentDrivingCircularView.setSeekModeEnabled(false);
                perDayCurrentOnDutyCircularView.setSeekModeEnabled(false);
                perDayCurrentOffDutyCircularView.setSeekModeEnabled(false);
                cycleCircularView.setSeekModeEnabled(false);
                perShiftCurrentOnDutyCircularView.setSeekModeEnabled(false);


                resetProgressBarUI();
                CycleTimeCalculation(true, false);
                setDataOnStatusView(DRIVER_JOB_STATUS);
                getCycleHours();
                setBreakProgress();

                // Shift value settings
                setProgressbarValues(shiftCircularView, perShiftUsedTimeTV, UsedShiftHoursInt, LeftShiftHoursInt);
                setProgressbarValues(perShiftCurrentDrivingCircularView, perShiftUsedDrivingTV, UsedDrivingHoursInt, LeftDrivingHoursInt);
                setProgressbarValues(perShiftCurrentOnDutyCircularView, perShiftUsedOnDutyTV, UsedOnDutyHoursInt, LeftOnDutyHoursInt);
                setProgressbarValuesDayWise(perDayCurrentOnDutyCircularView, perDayUsedOnDutyTV, UsedDayOnDutyDrivingHoursInt, LeftDayOnDutyDrivingHoursInt, false);
                setProgressbarValuesDayWise(perDayCurrentOffDutyCircularView, perDayUsedOffDutyTV, UsedDayOffDutyHoursInt,
                        LeftDayOffDutyDrivingHoursInt, true);
                if(CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)) {
                    setProgressbarValuesDayWise(perDayCurrentDrivingCircularView, perDayUsedDrivingTV, UsedDayDrivingHoursInt, LeftDayDrivingHoursInt, false);
                }

                if(LeftShiftHoursInt > 0 && LeftShiftHoursInt <= 30){
                    shiftCircularView.setBarColor(getResources().getColor(R.color.colorSleeper));
                }else if(LeftShiftHoursInt == 0){
                    shiftCircularView.setBarColor(getResources().getColor(R.color.colorVoilation));
                }else{
                    shiftCircularView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
                }

//UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES
               /* if (UILApplication.getInstance().isNightModeEnabled()) {
                    hosDistanceCardView.setCardBackgroundColor(getResources().getColor(R.color.gray_hover));
                    hosLocationCardView.setCardBackgroundColor(getResources().getColor(R.color.gray_hover));
                }*/

                if (constants.IsSendLog(DriverId, driverPermissionMethod, dbHelper) == false) {
                    sendLogHosBtn.setCardBackgroundColor(getResources().getColor(R.color.silver));
                }

                setAccPersonalDistanceOnView();

            }
        });



        editLogAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(SharedPref.isSuggestedEditOccur(getActivity())) {
//                    malfunctionLay.startAnimation(editLogAnimation);
                } else {
                    editLogAnimation.cancel();
//                    malfunctionLay.setVisibility(View.GONE);
                }
            }
//
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        dotSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isPressed()) {

                    if (global.isSingleDriver(getActivity()) && SharedPref.IsAppRestricted(getActivity()) && SharedPref.isVehicleMoving(getActivity())) {
                        Globally.EldScreenToast(eldMenuLay, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                        getString(R.string.stop_vehicle_alert),
                                getResources().getColor(R.color.colorVoilation));

                        if(buttonView.isChecked()){
                            buttonView.setChecked(false);
                        }else{
                            buttonView.setChecked(true);
                        }
                    }else{
                        setReverseCycleName();
                        if(CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)){
                            canadaDotView();
                        }else{
                            usaDotView();
                        }
                    }

                }

            }
        });



        eldMenuLay.setOnClickListener(this);
        sendLogHosBtn.setOnClickListener(this);
        obdHosInfoImg.setOnClickListener(this);
        hosDistanceCardView.setOnClickListener(this);
        hosLocationCardView.setOnClickListener(this);
        rightMenuBtn.setOnClickListener(this);
      //  EldFragment.summaryBtn.setEnabled(true);

        if(Constants.IS_HOS_AUTO_CALLED){
            Constants.IS_HOS_AUTO_CALLED = false;
            isAutoCalled = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(getActivity() != null) {

                        resetAllBar();
                        CycleTimeCalculation(true, true);

                    }
                }
            }, Constants.SocketTimeout5Sec);
        }

        checkScreenPixel();

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }

    boolean isWiredTablet(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        Logger.LogDebug("width","width: " +width);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int)(metrics.density * 160f);
        Logger.LogDebug("densityDpi","densityDpi: " +densityDpi);

        if(!Globally.isTablet(getActivity()) && width <= 1280 && densityDpi <= 320){
            return true;
        }else{
            return false;
        }

    }


    private void checkScreenPixel(){

        if(isWiredTablet()){
            int height = constants.intToPixel(getActivity(), 140);

            shiftLeftLay.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
            perDayLeftLay.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
            jobMiddleLowerLay.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
            jobRightUpperLay.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
            jobRightLowerLay.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
            jobMiddleUpperLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

            cycleCircularView.setBarWidth(9);
            cycleCircularView.setRimWidth(9);

            breakCircularView.setBarWidth(9);
            breakCircularView.setRimWidth(9);

            shiftCircularView.setBarWidth(9);
            shiftCircularView.setRimWidth(9);

            perShiftCurrentDrivingCircularView.setBarWidth(9);
            perShiftCurrentDrivingCircularView.setRimWidth(9);

            perShiftCurrentOnDutyCircularView.setBarWidth(9);
            perShiftCurrentOnDutyCircularView.setRimWidth(9);

            perDayCurrentDrivingCircularView.setBarWidth(9);
            perDayCurrentDrivingCircularView.setRimWidth(9);

            perDayCurrentOnDutyCircularView.setBarWidth(9);
            perDayCurrentOnDutyCircularView.setRimWidth(9);

            perDayCurrentOffDutyCircularView.setBarWidth(9);
            perDayCurrentOffDutyCircularView.setRimWidth(9);


            perShiftUsedDrivingTV.setTextSize(25);
            perShiftUsedOnDutyTV.setTextSize(25);
            perShiftUsedOffDutyTV.setTextSize(25);
            perShiftUsedTimeTV.setTextSize(25);

            perDayUsedDrivingTV.setTextSize(25);
            perDayUsedOnDutyTV.setTextSize(25);
            perDayUsedOffDutyTV.setTextSize(25);
            cycleUsedTimeTV.setTextSize(25);

          /*  perShiftDrivingTV.setPadding(0,0,0,30);
            perShiftOnDutyTV.setPadding(0,0,0,30);
            perShiftUsedOffDutyTV.setPadding(0,0,0,30);
            perShiftUsedTimeTV.setPadding(0,0,0,30);

            perDayUsedDrivingTV.setPadding(0,0,0,30);
            perDayUsedOnDutyTV.setPadding(0,0,0,30);
            perDayUsedOffDutyTV.setPadding(0,0,0,30);
            cycleUsedTimeTV.setPadding(0,0,0,30);
*/

          /*  if(perShiftNextBreakTV.getText().toString().equals("Off Duty")){
                perShiftNextBreakTV.setTextSize(17);
                perShiftNextBreakTV.setPadding(0,0,0,30);

            }*/

        }

    }


    void setAccPersonalDistanceOnView(){
        try {
            double AccumulativePersonalDistance = constants.getAccumulativePersonalDistance(DriverId, offsetFromUTC,
                    Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), global, getActivity()),
                    Globally.GetCurrentUTCDateTime(), hMethods, dbHelper, getActivity());
            accPerDistanceTv.setText(constants.Convert2DecimalPlacesDouble(AccumulativePersonalDistance) + " km");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void setReverseCycleName(){
        if(CycleId.equals(Globally.CANADA_CYCLE_1) || CycleId.equals(Globally.CANADA_CYCLE_2)){
            CurrentCycle = DriverConst.GetDriverSettings(DriverConst.USACycleName, getActivity());
            CycleId      = DriverConst.GetDriverSettings(DriverConst.USACycleId, getActivity());
        }else{
            CurrentCycle = DriverConst.GetDriverSettings(DriverConst.CANCycleName, getActivity());
            CycleId      = DriverConst.GetDriverSettings(DriverConst.CANCycleId, getActivity());
        }
    }


    void canadaDotView(){
        try{

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    hosFlagImgView.setImageResource(R.drawable.can_flag);
                    perDayDrivingTV.setVisibility(View.VISIBLE);
                    hos_CycleViewChange.setVisibility(View.GONE);
                    hosOperatingZoneTV.setVisibility(View.VISIBLE);
                    dotSwitchButton.setText("CAN");
                    hosPerDayTv.setText("PER DAY");
                    perDayDrivingTV.setText("Driving");
                    hosCycleTV.setText(CurrentCycle);
                    hosPerDayOnDutyCardView.setVisibility(View.VISIBLE);
                    hosPerDayOffDutyCardView.setVisibility(View.VISIBLE);
                    hosCycleCardView.setVisibility(View.VISIBLE);

                    resetProgressBarUI();
                    CycleTimeCalculation(true, false);
                    getCycleHours();
                    setBreakProgress();
                    setProgressbarValuesDayWise(perDayCurrentOnDutyCircularView, perDayUsedOnDutyTV, UsedDayOnDutyDrivingHoursInt, LeftDayOnDutyDrivingHoursInt, false);
                    setProgressbarValuesDayWise(perDayCurrentOffDutyCircularView, perDayUsedOffDutyTV, UsedDayOffDutyHoursInt,
                            LeftDayOffDutyDrivingHoursInt, true);
                    setProgressbarValuesDayWise(perDayCurrentDrivingCircularView, perDayUsedDrivingTV, UsedDayDrivingHoursInt, LeftDayDrivingHoursInt, false);
                    setProgressbarValues(perShiftCurrentDrivingCircularView, perShiftUsedDrivingTV, UsedDrivingHoursInt, LeftDrivingHoursInt);
                    setProgressbarValues(perShiftCurrentOnDutyCircularView, perShiftUsedOnDutyTV, UsedOnDutyHoursInt, LeftOnDutyHoursInt);


                    if(LeftDayDrivingHoursInt > 0 && LeftDayDrivingHoursInt <= 30){
                        perDayCurrentDrivingCircularView.setBarColor(getResources().getColor(R.color.colorSleeper));
                    }else if(LeftDayDrivingHoursInt == 0){
                        perDayCurrentDrivingCircularView.setBarColor(getResources().getColor(R.color.colorVoilation));
                    }else{
                        perDayCurrentDrivingCircularView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
                    }

                    if(LeftDayOnDutyDrivingHoursInt > 0 && LeftDayOnDutyDrivingHoursInt <= 30){
                        perDayCurrentOnDutyCircularView.setBarColor(getResources().getColor(R.color.colorSleeper));
                    }else if(LeftDayOnDutyDrivingHoursInt == 0){
                        perDayCurrentOnDutyCircularView.setBarColor(getResources().getColor(R.color.colorVoilation));
                    }else{
                        perDayCurrentOnDutyCircularView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
                    }

                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void usaDotView(){
        try{

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    hosFlagImgView.setImageResource(R.drawable.usa_flag);
                    perDayDrivingTV.setVisibility(View.GONE);
                    hosOperatingZoneTV.setVisibility(View.GONE);
                    hos_CycleViewChange.setVisibility(View.VISIBLE);
                    hosCycleTVUsa.setText("(" + CurrentCycle + ")");
                    dotSwitchButton.setText("USA");
                    hosPerDayTv.setText("CYCLE");
                    hosPerDayOnDutyCardView.setVisibility(View.INVISIBLE);
                    hosPerDayOffDutyCardView.setVisibility(View.INVISIBLE);
                    hosCycleCardView.setVisibility(View.INVISIBLE);
                    CycleTimeCalculation(true, false);
                    getCycleHours();
                    setBreakProgress();
                    setProgressbarValuesDayWise(perDayCurrentOnDutyCircularView, perDayUsedOnDutyTV, UsedDayOnDutyDrivingHoursInt, LeftDayOnDutyDrivingHoursInt, false);
                    setProgressbarValuesDayWise(perDayCurrentOffDutyCircularView, perDayUsedOffDutyTV, UsedDayOffDutyHoursInt,
                            LeftDayOffDutyDrivingHoursInt, true);
                    setProgressbarValues(perShiftCurrentDrivingCircularView, perShiftUsedDrivingTV, UsedDrivingHoursInt, LeftDrivingHoursInt);
                    setProgressbarValues(perShiftCurrentOnDutyCircularView, perShiftUsedOnDutyTV, UsedOnDutyHoursInt, LeftOnDutyHoursInt);

                }

            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        Constants.IS_ACTIVE_HOS = true;

        eldMenuLay.setEnabled(true);
        RestartTimer();

        if(!isOnCreate){
            resetAllBar();
            CycleTimeCalculation(true, true);
        }

        isOnCreate = false;

    }

    @Override
    public void onPause() {
        super.onPause();
        clearTimer();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.IS_ACTIVE_HOS = false;
        Constants.IS_ACTIVE_ELD = true;
    }



    void getExceptionStatus(){

        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isMal = SharedPref.isMalfunctionOccur(getActivity());
                        boolean isDia = SharedPref.isDiagnosticOccur(getActivity());
                        boolean isLocMal = constants.isAllowLocMalfunctionEvent(getActivity());

                        if (isLocMal || SharedPref.isEngSyncMalfunction(getActivity())) {
                            isMal = true;
                        }

                        if (isMal || isDia || SharedPref.isEngSyncDiagnstc(getActivity())) {
                            malfunctionLay.setVisibility(View.VISIBLE);
                            malfunctionLay.startAnimation(editLogAnimation);
                            if (isMal && isDia == false) {
                                malfunctionTV.setText(getString(R.string.malfunction_occur));
                                malfunctionLay.setBackgroundColor(getResources().getColor(R.color.colorVoilation));
                            } else if (isMal == false && isDia) {
                                malfunctionTV.setText(getString(R.string.diagnostic_occur));
                                malfunctionLay.setBackgroundColor(getResources().getColor(R.color.colorSleeper));
                            } else {
                                malfunctionLay.setBackgroundColor(getResources().getColor(R.color.colorVoilation));
                                malfunctionTV.setText(getString(R.string.malfunction_diag_occur));
                            }
                        } else {
                            editLogAnimation.cancel();
                            malfunctionLay.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    void getBundleData(){
        try {
            Bundle bundle = this.getArguments();
            if(bundle != null) {
                DRIVER_JOB_STATUS = bundle.getInt("current_status");
                LeftCycleHoursInt = bundle.getInt("left_cycle");
                LeftOnDutyHoursInt = bundle.getInt("left_onduty");
                LeftDrivingHoursInt = bundle.getInt("left_driving");
                UsedOnDutyHoursInt = bundle.getInt("total_onduty");
                UsedDrivingHoursInt = bundle.getInt("total_driving");
                UsedDayOffDutyHoursInt = bundle.getInt("total_offduty");
                UsedSleeperHoursInt = bundle.getInt("total_sleeper");
                UsedShiftHoursInt = bundle.getInt("shift_used");
                LeftShiftHoursInt = bundle.getInt("shift_remain");
                offsetFromUTC = bundle.getInt("offsetFromUTC");

                minOffDutyUsedHours = bundle.getInt("offDuty_used_hours");
                isMinOffDutyHoursSatisfied = bundle.getBoolean("is_offDuty_hr_satisfied");

                DriverId = bundle.getString("DriverId");
                DeviceId = bundle.getString("DeviceId");
                CycleId = bundle.getString("CycleId");
                isPersonal = bundle.getString("isPersonal");
                CurrentCycle = bundle.getString("cycle");
                driverLogArray = new JSONArray(bundle.getString("driverLogArray"));
                // currentDayArray= new JSONArray(bundle.getString("driverLogArray"));

                IsAOBRDAutomatic = bundle.getBoolean("IsAOBRDAutomatic");
                isSingleDriver = bundle.getBoolean("isSingleDriver");
                bundle.clear();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
        int totalValue = usedHour + leftHour;
        if(totalValue == 0){
            progressView.setMaxValue(100);
            progressView.setValue(0);
        }else{
            progressView.setMaxValue(totalValue);
            progressView.setValue(usedHour);
        }

        if(leftHour <= 0){
            progressView.setBarColor(getResources().getColor(R.color.colorVoilation));
        }else{
            progressView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
        }

        reportView.setText(Html.fromHtml(getHtmlText("U " + global.FinalValue(usedHour), "R " + global.FinalValue(leftHour))));
    }

    void setProgressbarValuesDayWise(CircleProgressView progressView, TextView reportView, int usedTimeInMin, int leftTimeInMin, boolean isOffDutyCircle){
        //     value settings

        if(CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)){
             int totalValue = usedTimeInMin + leftTimeInMin;
        if(totalValue == 0){
            progressView.setMaxValue(100);
            progressView.setValue(0);
        }else {
            progressView.setMaxValue(usedTimeInMin + leftTimeInMin);
            progressView.setValue(usedTimeInMin);
        }
            reportView.setText(Html.fromHtml(getHtmlText("U " + global.FinalValue(usedTimeInMin), "R " + global.FinalValue(leftTimeInMin))));

            if(reportView.getId() == R.id.hosPerDayUsedOffDutyTV){
                if(UILApplication.getInstance().isNightModeEnabled()){
                    hosPerDayOffDutyCardView.setCardBackgroundColor(getResources().getColor(R.color.field_bg_color));
                    progressView.setBarColor(getResources().getColor(R.color.colorSleeper));
                }else {
                    if (leftTimeInMin < 600) {
                        hosPerDayOffDutyCardView.setCardBackgroundColor(getResources().getColor(R.color.hos_offduty_background));
                        progressView.setBarColor(getResources().getColor(R.color.colorSleeper));
                        progressView.setRimColor(getResources().getColor(R.color.hos_progress_bg));

                    } else {
                        hosPerDayOffDutyCardView.setCardBackgroundColor(getResources().getColor(R.color.whiteee));
                        progressView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
                        progressView.setRimColor(getResources().getColor(R.color.hos_progress_bg));
                    }

                }
            }

        }else{
            int totalValue = usedTimeInMin + leftTimeInMin;
             if(totalValue == 0){
            progressView.setMaxValue(100);
            progressView.setValue(0);
        }else {
                 progressView.setMaxValue(usedTimeInMin + leftTimeInMin);
                 progressView.setValue(usedTimeInMin);
             }
            reportView.setText(Html.fromHtml(getHtmlTextDayWise("U " + global.FinalValue(usedTimeInMin), "R " + global.FinalValue(0))));
        }


        if(isOffDutyCircle){
            if(reportView.getId() != R.id.hosPerDayUsedOffDutyTV) {
                progressView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
            }
        }else {
            if (leftTimeInMin <= 0) {
                progressView.setBarColor(getResources().getColor(R.color.colorVoilation));
            } else {
                progressView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
            }
        }

    }


    private void resetProgressBarUI(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(DRIVER_JOB_STATUS == DRIVING){
                    hosPerShiftDrivingCardView.setCardBackgroundColor(deferralBlueLightDark);
                    hosPerDayDrivingCardView.setCardBackgroundColor(deferralBlueLightDark);
                    perShiftCurrentDrivingCircularView.setRimColor(getResources().getColor(R.color.white));
                    perDayCurrentDrivingCircularView.setRimColor(getResources().getColor(R.color.white));

                    if(isAutoCalled) {
                        perShiftCurrentOnDutyCircularView.setRimColor(deferralBlueLightDark);
                        perDayCurrentOnDutyCircularView.setRimColor(deferralBlueLightDark);
                    }

                }else if(DRIVER_JOB_STATUS == ON_DUTY){
                    hosPerShiftOnDutyCardView.setCardBackgroundColor(deferralBlueLightDark);
                    hosPerDayOnDutyCardView.setCardBackgroundColor(deferralBlueLightDark);
                    perShiftCurrentOnDutyCircularView.setRimColor(getResources().getColor(R.color.white));
                    perDayCurrentOnDutyCircularView.setRimColor(getResources().getColor(R.color.white));
                }else if(DRIVER_JOB_STATUS == OFF_DUTY){
                    hosPerShiftBreakCardView.setCardBackgroundColor(deferralBlueLightDark);
                    hosPerDayOffDutyCardView.setCardBackgroundColor(deferralBlueLightDark);
                    breakCircularView.setRimColor(getResources().getColor(R.color.white));
                    perDayCurrentOffDutyCircularView.setRimColor(getResources().getColor(R.color.white));
                    breakCircularView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));

                } else if(DRIVER_JOB_STATUS == SLEEPER){
                    hosPerShiftBreakCardView.setCardBackgroundColor(deferralBlueLightDark);
                    hosPerDayOffDutyCardView.setCardBackgroundColor(deferralBlueLightDark);
                    breakCircularView.setRimColor(getResources().getColor(R.color.white));
                    perDayCurrentOffDutyCircularView.setRimColor(getResources().getColor(R.color.white));
                    breakCircularView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
                }
            }
        });

    }


    private void resetAllBar(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    hosPerShiftDrivingCardView.setCardBackgroundColor(resetWhiteColor);
                    hosPerDayDrivingCardView.setCardBackgroundColor(resetWhiteColor);
                    hosPerShiftOnDutyCardView.setCardBackgroundColor(resetWhiteColor);
                    hosPerDayOnDutyCardView.setCardBackgroundColor(resetWhiteColor);
                    hosPerShiftBreakCardView.setCardBackgroundColor(resetWhiteColor);
                    hosPerDayOffDutyCardView.setCardBackgroundColor(resetWhiteColor);

                    perShiftCurrentOnDutyCircularView.setRimColor(getResources().getColor(R.color.hos_progress_bg));
                    perDayCurrentOnDutyCircularView.setRimColor(getResources().getColor(R.color.hos_progress_bg));
                    perShiftCurrentDrivingCircularView.setRimColor(getResources().getColor(R.color.hos_progress_bg));
                    perDayCurrentDrivingCircularView.setRimColor(getResources().getColor(R.color.hos_progress_bg));
                    perDayCurrentOffDutyCircularView.setRimColor(getResources().getColor(R.color.hos_progress_bg));
                    breakCircularView.setRimColor(getResources().getColor(R.color.hos_progress_bg));

                    breakCircularView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
    }

    private void resetCycle(){
        try{
            String CurrentCycleId = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());
            if(!CycleId.equals(CurrentCycleId)){
                setReverseCycleName();
                if(CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)){
                    canadaDotView();
                }else{
                    usaDotView();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dotSwitchButton.isChecked()){
                            dotSwitchButton.setChecked(false);
                        }else{
                            dotSwitchButton.setChecked(true);
                        }
                    }
                });

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setBreakProgress(){
        try {
            if ((CycleId.equals(global.USA_WORKING_6_DAYS) || CycleId.equals(global.USA_WORKING_7_DAYS))) {


                if((DRIVER_JOB_STATUS == DRIVING || DRIVER_JOB_STATUS == ON_DUTY)) {
                    //"U " + global.FinalValue(UsedNextBreak), "R " + global.FinalValue(LeftNextBreak)
                    perShiftUsedOffDutyTV.setText(Html.fromHtml(getHtmlText("in " + global.FinalValue(LeftNextBreak), "") ));
                    int TotalValue = LeftNextBreak+UsedNextBreak;
                    if(TotalValue == 0){
                        breakCircularView.setMaxValue(100);
                        breakCircularView.setValue(0);
                    }else{
                        breakCircularView.setMaxValue(LeftNextBreak+UsedNextBreak);
                        if(LeftNextBreak == 0){
                            breakCircularView.setValue(LeftNextBreak);
                        }else {
                            if (LeftNextBreak > UsedNextBreak) {
                                breakCircularView.setValue(LeftNextBreak - UsedNextBreak);
                            } else {
                                breakCircularView.setValue(UsedNextBreak - LeftNextBreak);
                            }
                        }
                    }


                    perShiftNextBreakTV.setText("Next Break");
                    perShiftNextBreakTV.setTextSize(14);
                    perShiftNextBreakTV.setPadding(0,0,0,0);

                }else if((DRIVER_JOB_STATUS == SLEEPER || DRIVER_JOB_STATUS == OFF_DUTY)){
                    int sbUsed = (int) usedAndRemainingTimeSB.getSleeperUsedMinutes();
                    int sbRemaining = (int) usedAndRemainingTimeSB.getSleeperRemainingMinutes();
                    int TotalValue = sbUsed + sbRemaining;
                    if(TotalValue == 0){
                        breakCircularView.setMaxValue(100);
                        breakCircularView.setValue(0);
                    }else {
                        breakCircularView.setMaxValue(sbUsed + sbRemaining); // 8 * 60 = 480 (8 hours)
                        breakCircularView.setValue(sbUsed);
                    }

                    perShiftNextBreakTV.setText("Break Time");
                    perShiftNextBreakTV.setTextSize( 14);
                    perShiftNextBreakTV.setPadding(0,0,0,0);

                    perShiftUsedOffDutyTV.setText(Html.fromHtml(getHtmlText("U " + global.FinalValue(sbUsed), "R " + global.FinalValue((int) usedAndRemainingTimeSB.getSleeperRemainingMinutes()))));


                    if(sbRemaining > 80 && sbRemaining <= 120){
                        breakCircularView.setBarColor(getResources().getColor(R.color.colorVoilation));
                    }else if(sbRemaining > 40 && sbRemaining <= 80){
                        breakCircularView.setBarColor(getResources().getColor(R.color.colorSleeper));
                    }else{
                        breakCircularView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
                    }


                }

        }else {

               // int remainingTime = TotalOffDutyHour - UsedOffDutyHoursInt;
//                if(remainingTime < 0){
//                    remainingTime = 0;
//                }else if(remainingTime > 120){
//                    remainingTime = CanadaBreakHour;
//                }
                perShiftNextBreakTV.setText("Off Duty");
                perShiftNextBreakTV.setTextSize(16);

                if(isWiredTablet()) {
                    perShiftNextBreakTV.setPadding(0, 28, 0, 0);
                }else{
                    perShiftNextBreakTV.setPadding(0, 15, 0, 0);
                }

                perShiftUsedOffDutyTV.setText(Html.fromHtml(getHtmlTextDayWise("U " + global.FinalValue(UsedOffDutyHoursInt), "R " + global.FinalValue(0) )));
                breakCircularView.setMaxValue(TotalOffDutyHour); // 8 * 60 = 480 (8 hours)
                breakCircularView.setValue(UsedOffDutyHoursInt);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCycleHours(){
        try {
            if (CurrentCycle.length() > 0 && !CurrentCycle.equals("null")) {
                String[] arr = CurrentCycle.split(" ");
                if (arr.length > 0) {
                    String[] cycleHourArr = arr[1].split("/");
                    if (cycleHourArr.length > 0) {
//
                        if (CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME)) {
                            hosCycleTV.setText(CurrentCycle);
                            if(SharedPref.IsNorthCanada(getActivity())) {
                                TotalCycleHour = 80 * 60;
                                hosCycleTV.setText(Globally.CANADA_CYCLE_1_NORTH_NAME);
                            }else{
                                TotalCycleHour = 70 * 60;
                                hosCycleTV.setText(CurrentCycle);
                            }
                        } else if(CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)){
                            TotalCycleHour = 120 * 60;
                        }else { // For USA Cycle
                            hosCycleTV.setText(CurrentCycle);
                            TotalCycleHour = Integer.valueOf(cycleHourArr[0]) * 60;
                        }

                        int leftCycle = TotalCycleHour - LeftCycleHoursInt;

                        if (CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)) {
                            cycleUsedTimeTV.setText(Html.fromHtml(getHtmlText("U " + global.FinalValue(leftCycle), " R " + global.FinalValue(LeftCycleHoursInt))));
                            cycleCircularView.setMaxValue(TotalCycleHour);
                            cycleCircularView.setValue(leftCycle);
                        }else{
                            perDayUsedDrivingTV.setText(Html.fromHtml(getHtmlText("U " + global.FinalValue(leftCycle), " R " + global.FinalValue(LeftCycleHoursInt))));
                            perDayCurrentDrivingCircularView.setMaxValue(TotalCycleHour);
                            perDayCurrentDrivingCircularView.setValue(leftCycle);

                        }


                        if(LeftCycleHoursInt > 0 && LeftCycleHoursInt <= 30){
                            cycleCircularView.setBarColor(getResources().getColor(R.color.colorSleeper));
                        }else if(LeftCycleHoursInt == 0){
                            cycleCircularView.setBarColor(getResources().getColor(R.color.colorVoilation));
                        }else{
                            cycleCircularView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
                        }
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private String getHtmlText(String usedTime, String remainedTime){
        String usedTimeStr = "<html> " + usedTime;
        String remainingTimeStr = "";
        if(remainedTime.length() > 0) {
            remainingTimeStr = "<br/>" + "<medium> <font  color='\" "+ HOS_REMAIN_COLOR +" \"'>" + remainedTime + "</font> </html>";
        }
        return usedTimeStr + remainingTimeStr;
    }

     private String getHtmlTextDayWise(String usedTime, String remainedTime){
        String usedTimeStr = "<html> " + usedTime;
        String remainingTimeStr = "";
        if(!remainedTime.equals("R 00:00")){
            remainingTimeStr = "<br/>"+"<medium> <font  color='\" "+ HOS_REMAIN_COLOR +" \"'>"+ remainedTime +"</font> </html>";
        }
        return usedTimeStr + remainingTimeStr;
    }

    private String getHtmlTextForOffSleeper(String usedTime, String remainedTime){
        return "<html> <font  color='"+ HOS_SLEEP_OFF_COLOR +"'>" + usedTime + "</font> <br/> <small> <font  color='"+ HOS_REMAIN_COLOR +"'>"+ remainedTime +"</font> </html> ";
    }

    //<font size=6 color='"+ usedTextColor +"'><b>"+ usedTime +"</b></font>

    private void setSBOffView(){
        // set view theme according to Off duty status


        String statusTxt = "<b> Shift: </b> Sleeper + Off Duty<br/> " +
                "&nbsp; &nbsp; &nbsp; <b>U " + global.FinalValue((int)usedAndRemainingTimeSB.getSleeperUsedMinutes()) + " || " +
                "R " + global.FinalValue((int)usedAndRemainingTimeSB.getSleeperRemainingMinutes())+" </b>";
        statusNewInfoTV.setText(Html.fromHtml( statusTxt ));

    }


    private void setDataOnStatusView(int status){

        try {
            if (getActivity() != null) {
                switch (status) {

                    case OFF_DUTY:

                        setProgressbarValuesDayWise(perDayCurrentOffDutyCircularView, perDayUsedOffDutyTV, UsedDayOffDutyHoursInt, LeftDayOffDutyDrivingHoursInt, true);

                    case SLEEPER:

                       /* if (usedAndRemainingTimeSB != null) {
                            setSBOffView(); //(int) usedAndRemainingTimeSB.getSleeperBirthMinutes(), (int) usedAndRemainingTimeSB.getSleeperUsedMinutes()
                        }*/
                        break;


                    case DRIVING:

                        setShiftEndTime();

                        // Driving value settings
                        setProgressbarValues(perShiftCurrentDrivingCircularView, perShiftUsedDrivingTV, UsedDrivingHoursInt, LeftDrivingHoursInt);

                        if(CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME) || CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)) {
                            setProgressbarValuesDayWise(perDayCurrentDrivingCircularView, perDayUsedDrivingTV, UsedDayDrivingHoursInt, LeftDayDrivingHoursInt, false);

                            if(LeftDayDrivingHoursInt > 0 && LeftDayDrivingHoursInt <= 30){
                                perShiftCurrentDrivingCircularView.setBarColor(getResources().getColor(R.color.colorSleeper));
                            }else if(LeftDayDrivingHoursInt == 0){
                                perShiftCurrentDrivingCircularView.setBarColor(getResources().getColor(R.color.colorVoilation));
                            }else{
                                perShiftCurrentDrivingCircularView.setBarColor(getResources().getColor(R.color.hos_progress_newbg));
                            }
                        }

                        break;


                    case ON_DUTY:

                        setShiftEndTime();

                        // OnDuty value settings
                        setProgressbarValues(perShiftCurrentOnDutyCircularView, perShiftUsedOnDutyTV, UsedOnDutyHoursInt, LeftOnDutyHoursInt);
                        setProgressbarValuesDayWise(perDayCurrentOnDutyCircularView, perDayUsedOnDutyTV, UsedDayOnDutyDrivingHoursInt, LeftDayOnDutyDrivingHoursInt, false);
                        break;

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void setShiftEndTime(){
        try {
            if (getActivity() != null) {
                if (LeftShiftHoursInt > 0) {
//                    shiftInfoTV.setVisibility(View.VISIBLE);
//                    shiftInfoTV.setTextColor(getResources().getColor(R.color.black_semi));
//                    DateTime currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);
//                    DateTime endShiftDateTime = currentDateTime.plusMinutes(LeftShiftHoursInt);
//                    String endDate = global.convertUSTtoMM_dd_yyyy_hh_mm(endShiftDateTime.toString());
//                    Logger.LogDebug("date", "converted Date: " + endDate);
//                    shiftInfoTV.setText(Html.fromHtml("&nbsp; &nbsp; <b>Shift Ends At </b> <br/>" + endDate));
                } else {
//                    shiftInfoTV.setText(shiftViolationReson.toLowerCase());
//                    shiftInfoTV.setVisibility(View.VISIBLE);
//                    shiftInfoTV.setTextColor(getResources().getColor(R.color.colorVoilation));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void shareDriverLogDialog() {

        boolean IsAOBRDAutomatic        = SharedPref.IsAOBRDAutomatic(getActivity());
        boolean IsAOBRD                 = SharedPref.IsAOBRD(getActivity());


        if (!IsAOBRD || IsAOBRDAutomatic) {
            Intent serviceIntent = new Intent(getActivity(), BackgroundLocationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(serviceIntent);
            }
            getActivity().startService(serviceIntent);
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

            String dayStartSavedDate    = SharedPref.getDayStartSavedTime(getActivity());
            final String dayStartOdometerStr  = SharedPref.getDayStartOdometerKm(getActivity());
            final String currentOdometerStr   = SharedPref.getObdOdometer(getContext());

            if(dayStartSavedDate.length() > 0) {
                if (dayStartSavedDate.equals(global.GetCurrentDeviceDate(null, global, getActivity()))) {

                    distanceInKm = Double.parseDouble(currentOdometerStr) - Double.parseDouble(dayStartOdometerStr);

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            if(distanceInKm < 0){
                                distanceInKm = 0;
                                hosDistanceTV.setText(Html.fromHtml("<b>0</b>"));

                            }else {
                                String distanceKmInStr = constants.getBeforeDecimalValue(String.valueOf(distanceInKm));

                                String distance = "(" + constants.getBeforeDecimalValue(dayStartOdometerStr) + " - " + constants.getBeforeDecimalValue(currentOdometerStr)
                                        + ") = <b>" + distanceKmInStr + " Km </b>";
                                hosDistanceTV.setText(Html.fromHtml(distance));
                            }
                        }
                    });


                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void CycleTimeCalculation(boolean isUpdateUI, boolean isDriving ) {

        try {

            if(isDriving){
                DRIVER_JOB_STATUS = Constants.DRIVING;
            }else {
                DRIVER_JOB_STATUS = Integer.parseInt(SharedPref.getDriverStatusId(getActivity()));
            }
            Logger.LogDebug("DriverJob","CurrentDriverJob: " +DRIVER_JOB_STATUS);
            calculateLocalOdometersDistance();

            if (isUpdateUI) {
                resetProgressBarUI();
                getExceptionStatus();

                if(global.isConnected(getActivity())) {
                    GetAddFromLatLng();
                    if (distanceInKm == 0) {
                        GetEngineMiles();  //=============================================================================================
                    }
                }else{
                    getOfflineAddress();
                }
            }

            currentUTCTime = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), true);
            currentDateTime = global.GetDriverCurrentTime(currentUTCTime.toString(), global, getActivity());

            currentDayArray = hMethods.GetSingleDateArray(driverLogArray, currentDateTime, currentDateTime, currentUTCTime,
                    true, offsetFromUTC, getActivity());
            rulesVersion = SharedPref.GetRulesVersion(getActivity());

            List<DriverLog> oDriverLogDetail = hMethods.getSavedLogList(Integer.valueOf(DriverId), currentDateTime, currentUTCTime, dbHelper);
            if(oDriverLogDetail.size() > 0) {
                oDriverLogDetail.get(oDriverLogDetail.size() - 1).setCurrentCyleId(Integer.valueOf(CycleId));
            }

            final DriverDetail oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DriverId),
                    offsetFromUTC, Integer.valueOf(CycleId), isSingleDriver, DRIVER_JOB_STATUS, false,
                    isHaulExcptn, isAdverseExcptn, isNorthCanada,
                    rulesVersion, oDriverLogDetail, getActivity());

            if(CycleId.equals(Globally.CANADA_CYCLE_1) || CycleId.equals(Globally.CANADA_CYCLE_2) ) {
                oDriverDetail.setCanAdverseException(isAdverseExcptn);
            }

            RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CycleId), Integer.valueOf(DRIVER_JOB_STATUS), oDriverDetail);


            if (isUpdateUI) {
                // Calculate 2 days data to get remaining Driving/Onduty hours
                RulesResponseObject remainingTimeObj = hMethods.getRemainingHosTime(currentDateTime, currentUTCTime, offsetFromUTC,
                        Integer.valueOf(CycleId), isSingleDriver, Integer.valueOf(DriverId), DRIVER_JOB_STATUS,
                        false, isHaulExcptn,
                        isAdverseExcptn,  isNorthCanada, rulesVersion, dbHelper, getActivity());

                LeftCycleHoursInt = (int) RulesObj.getCycleRemainingMinutes();
                TotalCycleHour = (int) remainingTimeObj.getCycleUsedMinutes();

                UsedNextBreak = (int) remainingTimeObj.getBreakUsedMinutes();
                LeftNextBreak = (int) remainingTimeObj.getBreakRemainingMinutes();

                UsedOnDutyHoursInt = (int) remainingTimeObj.getOnDutyUsedMinutes();
                LeftOnDutyHoursInt = (int) remainingTimeObj.getOnDutyRemainingMinutes();
                UsedDrivingHoursInt = (int) remainingTimeObj.getDrivingUsedMinutes();
                LeftDrivingHoursInt = (int) remainingTimeObj.getDrivingRemainingMinutes();

                UsedDayDrivingHoursInt = (int) remainingTimeObj.getDrivingUsedMinutesDayWise();
                LeftDayDrivingHoursInt = (int) remainingTimeObj.getDrivingRemainingMinutesDayWise();
                UsedDayOnDutyDrivingHoursInt = (int) remainingTimeObj.getOnDutyUsedMinutesDayWise();
                LeftDayOnDutyDrivingHoursInt = (int) remainingTimeObj.getOnDutyRemainingMinutesDayWise();
//                UsedDayOffDutyDrivingHoursInt = (int) remainingTimeObj.getOffdutyUsedMinutesDayWise();
                LeftDayOffDutyDrivingHoursInt = (int) remainingTimeObj.getOffDutyRemainingMinutesDayWise();
                UsedDayOffDutyHoursInt           = (int) remainingTimeObj.getOffdutyUsedMinutesDayWise();

                LeftShiftHoursInt = (int) remainingTimeObj.getShiftRemainingMinutes();
                UsedShiftHoursInt = (int) remainingTimeObj.getShiftUsedMinutes();
                UsedOffDutyHoursInt = (int) remainingTimeObj.getOffDutyUsedMinutes();

                minOffDutyUsedHours = (int) remainingTimeObj.getMinimumOffDutyUsedHours();
                isMinOffDutyHoursSatisfied = remainingTimeObj.isMinimumOffDutyHoursSatisfied();

                UsedSleeperHoursInt = hMethods.GetDutyStatusTimeInterval(currentDayArray, constants, EldFragment.SLEEPER);
                usedAndRemainingTimeSB = localCalls.getUsedAndRemainingTimeSB(oDriverDetail);



                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                      //  int leftHoursss = (int)(70 - 45.0);
                        String response = "";
                        if(oDriverDetail != null)
                        {
                            if(CurrentCycle.equals(Globally.CANADA_CYCLE_1_NAME)){

                                int offDutyDays = localCalls.IsTake24HoursOffDutyIn14Days(oDriverDetail);
                                int pendingDays= 14 - offDutyDays;
                                response = "24 hours OffDuty should be taken within " + pendingDays + " days";

                            }else if( CurrentCycle.equals(Globally.CANADA_CYCLE_2_NAME)){
                                double offDutyMin = localCalls.IsTake24HoursOffDutyWithIn70Hours(oDriverDetail);

                                if(offDutyMin < 0){
                                    response = "24 hours OffDuty completed within 70 hours.";
                                }else{
                                   int leftMin = (int)(70*60 - offDutyMin);
                                    String offDutyLeftHour = String.valueOf(leftMin/60);
                                    String offDutyLeftMin = String.valueOf(leftMin%60);
                                    if(offDutyLeftHour.length() == 1){
                                        offDutyLeftHour = "0" + offDutyLeftHour;
                                    }
                                    if(offDutyLeftMin.length() == 1){
                                        offDutyLeftMin = "0" + offDutyLeftMin;
                                    }

                                    response = "24 hours OffDuty should be taken within " + offDutyLeftHour +":"+offDutyLeftMin+ " hours.";


                                }

                            }

                        }

                        offDutyRestTV.setText(response);

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

                        // value settings
                        setDataOnStatusView(DRIVER_JOB_STATUS);
                        getCycleHours();
                        setBreakProgress();

                        setProgressbarValues(shiftCircularView, perShiftUsedTimeTV, UsedShiftHoursInt, LeftShiftHoursInt);

                        hosLocationTV.setText(Globally.LATITUDE + ", "+ Globally.LONGITUDE);

                        setAccPersonalDistanceOnView();
                    }
                });

            }

        }catch(Exception e){
            e.printStackTrace();
        }


    }


    private void getOfflineAddress(){

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                try {
                    String AddressLine = "";
                    if (LocationFromApi.length() == 0) {
                        if ((CycleId.equals(Globally.CANADA_CYCLE_1) || CycleId.equals(Globally.CANADA_CYCLE_2))
                                && !SharedPref.IsAOBRD(getContext())) {
                            AddressLine = csvReader.getShortestAddress(getContext());
                        } else {
                            if (Globally.LATITUDE.length() > 4) {
                                AddressLine = Globally.LATITUDE + "," + Globally.LONGITUDE;
                            }
                        }
                        hosLocationTV.setText(AddressLine);
                    } else {
                        hosLocationTV.setText(LocationFromApi);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }


    private class HosTimerTask extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run() {

            if(global.isSingleDriver(getActivity()) && SharedPref.isVehicleMoving(getActivity())) {
                resetAllBar();
                resetCycle();
            }

            CycleTimeCalculation(true, false);

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

        boolean isActionEventAllowedWithSpeed = hMethods.isActionEventAllowedWithSpeed(getActivity(), global, DriverId, dbHelper);

        switch (v.getId()){

            case R.id.obdHosInfoImg:

                if (isActionEventAllowedWithSpeed) {
                    ObdDataInfoDialog obdDialog = new ObdDataInfoDialog(getActivity(), DriverId);
                    obdDialog.show();
                }else {
                    Globally.EldScreenToast(eldMenuLay, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                    getString(R.string.stop_vehicle_alert),
                            getResources().getColor(R.color.colorVoilation));
                }

                break;

            case R.id.hosLocationCardView:


                break;


            case R.id.eldMenuLay:

                if (isActionEventAllowedWithSpeed) {
                    eldMenuLay.setEnabled(false);
                    getParentFragmentManager().popBackStack();
                }else{
                    Globally.EldScreenToast(eldMenuLay, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                    getString(R.string.stop_vehicle_alert),
                            getResources().getColor(R.color.colorVoilation));
                }

                break;

            case R.id.sendLogHosBtn:

                if (isActionEventAllowedWithSpeed) {
                    if (constants.IsSendLog(DriverId, driverPermissionMethod, dbHelper)) {

                        if (hMethods.isActionAllowedWhileMoving(getActivity(), global, DriverId, dbHelper)&&
                                DRIVER_JOB_STATUS != DRIVING) {
                            shareDriverLogDialog();
                        } else {
                            if(DRIVER_JOB_STATUS == DRIVING){
                                Globally.EldScreenToast(sendLogHosBtn, getString(R.string.chnge_dr_to_othr),
                                        getResources().getColor(R.color.colorVoilation));
                            }else {
                                global.EldScreenToast(sendLogHosBtn, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                                getString(R.string.stop_vehicle_alert),
                                        getResources().getColor(R.color.colorVoilation));
                            }
                        }

                    } else {
                        global.EldToastWithDuration(sendLogHosBtn, getResources().getString(R.string.share_not_allowed), getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    Globally.EldScreenToast(eldMenuLay, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                    getString(R.string.stop_vehicle_alert),
                            getResources().getColor(R.color.colorVoilation));
                }



                break;

            case R.id.rightMenuBtn:

                if (isActionEventAllowedWithSpeed) {
                    isRefreshBtnClicked = true;
                    loadingSpinEldIV.startAnimation();
                    resetAllBar();
                    CycleTimeCalculation(true, false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null && !getActivity().isFinishing()) {
                                if (!global.isConnected(getActivity())) {
                                    loadingSpinEldIV.stopAnimation();
                                    global.EldScreenToast(loadingSpinEldIV, ConstantsEnum.UPDATED, getResources().getColor(R.color.colorSleeper));
                                }
                            }

                        }
                    }, Constants.SocketTimeout1Sec);
                }else{
                    Globally.EldScreenToast(eldMenuLay, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                    getString(R.string.stop_vehicle_alert),
                            getResources().getColor(R.color.colorVoilation));
                }




                break;


            case R.id.availableHourBtnTV:

                availableHourBtnTV.setEnabled(false);
                CycleTimeCalculation(false, false);

                RulesResponseObject RemainingTimeObj = hMethods.getRemainingHosTime(currentDateTime, currentUTCTime, offsetFromUTC,
                        Integer.valueOf(CycleId), isSingleDriver, Integer.valueOf(DriverId), Constants.DRIVING, false,
                        isHaulExcptn, isAdverseExcptn, isNorthCanada,
                        rulesVersion, dbHelper, getActivity());

                HosInfoDialog dialog = new HosInfoDialog(getActivity(), RemainingTimeObj, RulesObj);
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        availableHourBtnTV.setEnabled(true);
                    }
                }, Constants.SocketTimeout1Sec);
                //Logger.LogDebug("DrivingRemainingMinutes", "DrivingRemainingMinutes: " + RemainingTimeObj.getDrivingRemainingMinutes());
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
        params.put(ConstantsKeys.Latitude, Globally.LATITUDE);
        params.put(ConstantsKeys.Longitude, Globally.LONGITUDE);
        params.put(ConstantsKeys.IsAOBRDAutomatic, String.valueOf(IsAOBRDAutomatic));

        GetAddFromLatLngRequest.executeRequest(Request.Method.POST, APIs.GET_Add_FROM_LAT_LNG, params, GetAddFromLatLng,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Engine Miles===================*//*
    void GetEngineMiles() {
        String CompanyId    = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
        String VIN          = SharedPref.getVINNumber(getActivity()); //DriverConst.GetDriverTripDetails(DriverConst.VIN, getActivity());

        // get device current date and day start UTC date
        String currentDateStr = global.GetDriverCurrentDateTime(global, getActivity());
        currentDateTime     = global.getDateTimeObj(currentDateStr, false);    // Current Date Time
        String cDate = currentDateStr;
        cDate = cDate.split("T")[0]+"T00:00:00";
        DateTime currentStartUtcDate = new DateTime(cDate);
        String utcDateStr = String.valueOf(new DateTime(Globally.getDateTimeObj(currentStartUtcDate.toString(), false)) );
       // Logger.LogDebug("cDate","cDate11: " +  utcDateStr);



        int isOdometer = SharedPref.IsOdometerFromOBD(getActivity()) ? 1 : 0;

        Map<String, String> params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.IsOdometerFromOBD, String.valueOf(isOdometer) );
        params.put(ConstantsKeys.VIN, VIN);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.LogDate, String.valueOf(currentDateTime) );
        params.put(ConstantsKeys.UTCStartDateTime, utcDateStr );

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
                e.printStackTrace();
            }

            try {
                if (getActivity() != null) {
                    loadingSpinEldIV.stopAnimation();
                    if (status.equalsIgnoreCase("true")) {

                        switch (flag) {

                            case GetAddFromLatLng:
                                if (!obj.isNull("Data")) {
                                    try {

                                        if (isRefreshBtnClicked) {
                                            if(UILApplication.getInstance().isNightModeEnabled()){
                                                global.EldScreenToast(loadingSpinEldIV, ConstantsEnum.UPDATED, UILApplication.getInstance().getThemeColor());
                                            }else {
                                                global.EldScreenToast(loadingSpinEldIV, ConstantsEnum.UPDATED, UILApplication.getInstance().getThemeColor());
                                            }
                                        }

                                        JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                                        //String Country     = dataJObject.getString(ConstantsKeys.Country);
                                        LocationFromApi = dataJObject.getString(ConstantsKeys.Location);   //+ ", " + Country

                                        if (LocationFromApi.length() > 0 && !LocationFromApi.equals("null")) {
                                            hosLocationTV.setText(LocationFromApi);
                                        } else {
                                            if(Globally.LATITUDE.length() > 4) {
                                                hosLocationTV.setText(Globally.LATITUDE + ", " + Globally.LONGITUDE);
                                            }
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    getOfflineAddress();
                                }


                                break;

                            case GetMiles:
                                Logger.LogDebug("getMiles", "response: " + response);


                                try {

                                    if (!obj.isNull("Data")) {
                                        JSONObject dataObj = new JSONObject(obj.getString("Data"));
                                        String Miles = constants.getBeforeDecimalValue(dataObj.getString("Miles"));
                                        String StartOdometer = constants.getBeforeDecimalValue(dataObj.getString("StartOdometer"));
                                        String EndOdometer = constants.getBeforeDecimalValue(dataObj.getString("EndOdometer"));


                                        if (!StartOdometer.equals("0")) {
                                            String distance = "(" + StartOdometer + " - " + EndOdometer + ") = <b>" + Miles + " Miles </b>";
                                            hosDistanceTV.setText(Html.fromHtml(distance) + "--");

                                            // saved day start odometer locally to calculate distance
                                           // SharedPref.setDayStartOdometer(StartOdometer, global.GetCurrentDeviceDate(), getActivity());

                                        }

                                    }
                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                                break;
                        }

                    } else {
                        if (flag == GetMiles) {
                            if (isRefreshBtnClicked) {
                                global.EldToastWithDuration(loadingSpinEldIV, ConstantsEnum.UPDATED, getActivity().getResources().getColor(R.color.colorSleeper));
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            isRefreshBtnClicked = false;
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

        @Override
        public void getError(VolleyError error, int flag) {
            Logger.LogDebug("getMiles", "error: " + error);
            try {
                if (getActivity() != null) {
                    if (flag == GetMiles) {
                        if (isRefreshBtnClicked) {
                            global.EldToastWithDuration(loadingSpinEldIV, ConstantsEnum.UPDATED, getResources().getColor(R.color.colorSleeper));
                        }
                        loadingSpinEldIV.stopAnimation();
                    }else if(flag == GetAddFromLatLng){
                        getOfflineAddress();
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            isRefreshBtnClicked = false;
        }
    };

}
