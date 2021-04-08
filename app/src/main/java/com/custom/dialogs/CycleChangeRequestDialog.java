package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.EldFragment;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.List;

import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;

public class CycleChangeRequestDialog extends Dialog {

    public interface ConfirmationListener {
        public void OkBtnReady(String savedCycleType, String changedCycleName);
        public void CancelBtnReady(String savedCycleType, String changedCycleName);
    }

    private  String DriverId, currentCycleId, changedCycleId, changedCycleName, savedCycleType;
    private ConfirmationListener readyListener;
    Globally Global;
    Constants constants;
    HelperMethods hMethods;
    DBHelper dbHelper;
    SharedPref sharedPref;


    public CycleChangeRequestDialog(Context context, String driverId, String currentCycle, String changedCycle, ConfirmationListener readyListener) {
        super(context);
        this.DriverId = driverId;
        this.currentCycleId = currentCycle;
        this.changedCycleId = changedCycle;
        this.readyListener = readyListener;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_edit_delete_lay);
        //setCancelable(false);

        Global      = new Globally();
        constants = new Constants();
        hMethods = new HelperMethods();
        dbHelper = new DBHelper(getContext());
        sharedPref = new SharedPref();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
       // lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        if(Globally.isTablet(getContext())) {
            lp.width = constants.intToPixel(getContext(), 600);
        }else{
            lp.width = constants.intToPixel(getContext(), 500);
        }

        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);


        TextView changeTitleView, titleDescView, changedCycleRuleTxtVw;
        changeTitleView = (TextView)findViewById(R.id.changeTitleView);
        titleDescView=(TextView)findViewById(R.id.titleDescView);
        changedCycleRuleTxtVw=(TextView)findViewById(R.id.changedCycleRuleTxtVw);

        final Button confirmPopupButton = (Button)findViewById(R.id.confirmPopupButton);
        Button cancelPopupButton = (Button)findViewById(R.id.cancelPopupButton);

       // changeTitleView.setPadding(150, 0, 150, 0);

        changeTitleView.setText(getContext().getResources().getString(R.string.cycle_change_request));
        confirmPopupButton.setText(getContext().getResources().getString(R.string.approve));
        cancelPopupButton.setText(getContext().getResources().getString(R.string.reject));
        cancelPopupButton.setTypeface(null, Typeface.NORMAL);

        changedCycleRuleTxtVw.setVisibility(View.VISIBLE);
        CalculateCycleTime(changedCycleRuleTxtVw);

        String currentCycle = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycle, getContext());

        if(changedCycleId.equals(Globally.CANADA_CYCLE_1)){
            changedCycleName = Globally.CANADA_CYCLE_1_NAME;
            savedCycleType = "can_cycle";
        }else if(changedCycleId.equals(Globally.CANADA_CYCLE_2)){
            changedCycleName = Globally.CANADA_CYCLE_2_NAME;
            savedCycleType = "can_cycle";
        }else if(changedCycleId.equals(Globally.USA_WORKING_6_DAYS)){
            changedCycleName = Globally.USA_WORKING_6_DAYS_NAME;
            savedCycleType = "us_cycle";
        }else{
            changedCycleName = Globally.USA_WORKING_7_DAYS_NAME;
            savedCycleType = "us_cycle";
        }

        titleDescView.setText(Html.fromHtml(getContext().getString(R.string.change_cycle_request) + "<font color='#1A3561'> <b>"+ currentCycle
                                            +"</b></font> to<font color='#1A3561'> <b>"+ changedCycleName +"</b></font>.") );
        cancelPopupButton.setOnClickListener(new CancelJobListener());
        confirmPopupButton.setOnClickListener(new OkJobListener());

    }



    private class OkJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.OkBtnReady(savedCycleType, changedCycleName);
        }
    }

    private class CancelJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.CancelBtnReady(savedCycleType, changedCycleName);
        }
    }


    void CalculateCycleTime(TextView txtView){

        int offsetFromUTC = (int) Global.GetTimeZoneOffSet();
        List<DriverLog> oDriverLogDetail ;

        String currentJobStatus     = sharedPref.getDriverStatusId("jobType", getContext());

        DateTime currentDateTime    = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);    // Current Date Time
        DateTime currentUTCTime     = Globally.getDateTimeObj(Globally.GetCurrentUTCTimeFormat(), true);
         oDriverLogDetail           = hMethods.getSavedLogList(Integer.valueOf(DriverId), currentDateTime, currentUTCTime, dbHelper);

        int rulesVersion = sharedPref.GetRulesVersion(getContext());
        boolean isHaulExcptn, isAdverseExcptn;
        if (sharedPref.getCurrentDriverType(getContext()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            isHaulExcptn    = sharedPref.get16hrHaulExcptn(getContext());
            isAdverseExcptn = sharedPref.getAdverseExcptn(getContext());
        }else{
            isHaulExcptn    = sharedPref.get16hrHaulExcptnCo(getContext());
            isAdverseExcptn = sharedPref.getAdverseExcptnCo(getContext());
        }

        boolean isSingleDriver = false;
        if (sharedPref.getCurrentDriverType(getContext()).equals(DriverConst.StatusSingleDriver)) {
            isSingleDriver = true;
        }

        DriverDetail oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DriverId),
                offsetFromUTC, Integer.valueOf(changedCycleId), isSingleDriver, Integer.valueOf(currentJobStatus), false,
                isHaulExcptn, isAdverseExcptn,
                rulesVersion, oDriverLogDetail);

        // EldFragment.SLEEPER is used because we are just checking cycle time
        RulesResponseObject RulesObj = hMethods.CheckDriverRule(Integer.valueOf(changedCycleId), EldFragment.SLEEPER, oDriverDetail);

        // Calculate 2 days data to get remaining Driving/Onduty hours
        RulesResponseObject RemainingTimeObj = hMethods.getRemainingTime(currentDateTime, currentUTCTime, offsetFromUTC,
                Integer.valueOf(changedCycleId), isSingleDriver, Integer.valueOf(DriverId) , Integer.valueOf(currentJobStatus), false,
                isHaulExcptn, isAdverseExcptn,
                rulesVersion, dbHelper);

        try {
            int CycleRemainingMinutes   = constants.checkIntValue((int) RulesObj.getCycleRemainingMinutes());
            int OnDutyRemainingMinutes  = constants.checkIntValue((int) RemainingTimeObj.getOnDutyRemainingMinutes());
            int DriveRemainingMin       = constants.checkIntValue((int) RemainingTimeObj.getDrivingRemainingMinutes());

          //  int CycleUsedMinutes        = constants.checkIntValue((int) RulesObj.getCycleUsedMinutes());
           // int TotalOnDutyHoursInt     = constants.checkIntValue((int) RemainingTimeObj.getOnDutyUsedMinutes());
            //int ShiftUsedMinutes        = constants.checkIntValue((int) RemainingTimeObj.getShiftUsedMinutes());
          //  int DriveUsedMin            = constants.checkIntValue((int) RemainingTimeObj.getDrivingUsedMinutes());


            if(CycleRemainingMinutes < OnDutyRemainingMinutes){
                OnDutyRemainingMinutes = CycleRemainingMinutes;
            }

            String CycleRemaining          = "" +Global.HourFromMin(CycleRemainingMinutes) + " Hrs " ;
            String OnDutyRemaining         = "" +Global.HourFromMin(OnDutyRemainingMinutes) + " Hrs " ;
            String DriveRemaining          = "" +Global.HourFromMin(DriveRemainingMin) + " Hrs " ;

            //  String TotalCycleUsed          = Global.FinalValue(CycleUsedMinutes);
           // String OnDutyUsed              = Global.FinalValue(TotalOnDutyHoursInt);
           // String DrivingUsed             = Global.FinalValue(DriveUsedMin);


            String finalCycleData = "<font color='#3F88C5'><b>"+getContext().getResources().getString(R.string.hos_limitation) + "</b><br/>" +
                    "<b>Cycle &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b> :&nbsp;&nbsp; " + CycleRemaining + "<br/>" +
                    "<b>Driving  &nbsp;&nbsp;</b> :&nbsp;&nbsp; " + DriveRemaining + "<br/>" +
                    "<b>OnDuty   &nbsp;&nbsp;</b> :&nbsp;&nbsp; " + OnDutyRemaining + " </font>" ;

            txtView.setText(Html.fromHtml(finalCycleData) );

        } catch (Exception e) {
            e.printStackTrace();
        }

      /*  totalCycleHrsTV     .setText( TotalCycleUsedHour);
        leftCycleTV         .setText( LeftWeekOnDutyHoursInt );
        HrsAvailTV          .setText( HoursAvailableToday );
        HrsWorkedTV         .setText( HoursWorkedToday);
        hourAvailableTomoTV .setText( "14:00" );*/

    }



}

