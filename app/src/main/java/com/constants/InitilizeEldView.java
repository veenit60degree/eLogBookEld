package com.constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.messaging.logistic.fragment.CertifyViewLogFragment;
import com.messaging.logistic.fragment.EldFragment;

import java.util.ArrayList;

/**
 * Created by kumar on 1/23/2018.
 */

public class InitilizeEldView {

    public InitilizeEldView() {
        super();
    }



    public void ShowActiveJobView(int job, String isPersonal, TextView jobTypeTxtVw, TextView perDayTxtVw, LinearLayout remainingLay,
                                  LinearLayout usedHourLay, TextView jobTimeTxtVw, TextView jobTimeRemngTxtVw){
        switch (job) {
            case EldFragment.OFF_DUTY:  //1
                if(isPersonal.equals("true"))
                    jobTypeTxtVw.setText("Personal Use");
                else
                    jobTypeTxtVw.setText("Off Duty");

               // remainingLay.setVisibility(View.GONE);
                usedHourLay.setVisibility(View.VISIBLE);
                jobTimeTxtVw.setText("");
                jobTimeRemngTxtVw.setText("");
               // perDayTxtVw.setText("(AS PER CURRENT STATUS)");
                break;

            case EldFragment.SLEEPER:   //2
                jobTypeTxtVw.setText("Sleeper");
              //  remainingLay.setVisibility(View.GONE);
                usedHourLay.setVisibility(View.VISIBLE);
                jobTimeTxtVw.setText("");
                jobTimeRemngTxtVw.setText("");
               // perDayTxtVw.setText("(AS PER CURRENT STATUS)");
                break;

            case EldFragment.DRIVING:   //3
                jobTypeTxtVw.setText("Driving");
              //  remainingLay.setVisibility(View.VISIBLE);
                usedHourLay.setVisibility(View.VISIBLE);
                jobTimeTxtVw.setText("");
                jobTimeRemngTxtVw.setText("");
               // perDayTxtVw.setText("(AS PER SHIFT)");
                break;

            case EldFragment.ON_DUTY:   //4
                jobTypeTxtVw.setText("On Duty");
                //remainingLay.setVisibility(View.VISIBLE);
                usedHourLay.setVisibility(View.VISIBLE);
                jobTimeTxtVw.setText("");
                jobTimeRemngTxtVw.setText("");
               // perDayTxtVw.setText("(AS PER SHIFT)");
                break;

            default:
                jobTypeTxtVw.setText("Off Duty");
                //remainingLay.setVisibility(View.GONE);
                usedHourLay.setVisibility(View.GONE);
                jobTimeTxtVw.setText("");
                jobTimeRemngTxtVw.setText("");
              //  perDayTxtVw.setText("(AS PER CURRENT STATUS)");
                break;
        }
    }


    public String getCurrentStatus(int job, String isPersonal){
        String status = "";
        switch (job) {
            case EldFragment.OFF_DUTY:  //1
                if(isPersonal.equals("true"))
                    status = "Personal Use";
                else
                    status = "Off Duty";

                break;

            case EldFragment.SLEEPER:   //2
                status = "Sleeper";
                break;

            case EldFragment.DRIVING:   //3
                status = "Driving";
                break;

            case EldFragment.ON_DUTY:   //4
                status = "On Duty";
                break;

            default:
                status = "Off Duty";

                break;
        }
        return status;
    }



    public void MoveFragment(String date, String dayName, String dayFullName, String dayShortName, int dayOfMonth,
                              boolean isCertifyLog, String VIN_NUMBER, int offsetFromUTC,
                             String LeftWeekOnDutyHoursInt, String LeftDayOnDutyHoursInt,
                             String LeftDayDrivingHoursInt, String cycle, String VehicleId,
                             boolean isSignPending, boolean isFragmentAdd, FragmentManager fragManager, String driverLogArray){

        Constants.IS_ACTIVE_ELD = false;

        try {
            CertifyViewLogFragment detailFragment = new CertifyViewLogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("date", date);
            bundle.putString("day_name", dayName);
            bundle.putString("month_full_name", dayFullName);
            bundle.putString("month_short_name", dayShortName);
            bundle.putInt("day_of_month", dayOfMonth);
            bundle.putBoolean("is_certify", isCertifyLog);
            bundle.putString("vin", VIN_NUMBER);
            bundle.putString("driverLogArray", driverLogArray);

            bundle.putInt("offset", offsetFromUTC);
            bundle.putString("LeftWeekOnDuty", LeftWeekOnDutyHoursInt);
            bundle.putString("LeftDayOnDuty", LeftDayOnDutyHoursInt);
            bundle.putString("LeftDayDriving", LeftDayDrivingHoursInt);
            bundle.putString("cycle", cycle);
            bundle.putString("VehicleId", VehicleId);
            bundle.putBoolean("signStatus", isSignPending);

            detailFragment.setArguments(bundle);

            FragmentTransaction fragmentTran = fragManager.beginTransaction();
            fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.fade_in, android.R.anim.fade_out);
            if(isFragmentAdd){
                fragmentTran.add(R.id.job_fragment, detailFragment);
            }else {
                fragmentTran.replace(R.id.job_fragment, detailFragment);
            }
            fragmentTran.addToBackStack("eld_certify_log");
            fragmentTran.commit();

        }catch (Exception e){
            e.printStackTrace();
        }

    }


/*
    public void ActiveView(RelativeLayout layout, TextView violationView, TextView timeView, TextView jobTypeView, boolean isViolation){
        if(isViolation) {
            layout.setBackgroundResource(R.drawable.red_eld_selector);
            violationView.setVisibility(View.VISIBLE);

        }else {
            layout.setBackgroundResource(R.drawable.eld_blue_new_selector);
            violationView.setVisibility(View.GONE);
        }

        violationView.setTextColor(Color.WHITE);
        timeView.setTextColor(Color.WHITE);
        jobTypeView.setTextColor(Color.WHITE);
    }*/

/*
    @SuppressLint("Range")
    public void InActiveView(RelativeLayout layout, TextView violationView, TextView timeView, TextView jobTypeView, Button personalUseBtn){
        String EldThemeColor = "", SkyBlueColor = "";    //4A88CC

        //if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled())  {
            EldThemeColor = "#F0F0F0";
            SkyBlueColor = "#ffffff";
        }else{
            EldThemeColor = "#1A3561";
            SkyBlueColor = "#1A3561";
        }
        layout.setBackgroundResource(R.drawable.gray_eld_selector);
        violationView.setTextColor(Color.parseColor(SkyBlueColor));
        timeView.setTextColor(Color.parseColor(SkyBlueColor));

        personalUseBtn.setTextColor(Color.parseColor(EldThemeColor));
        jobTypeView.setTextColor(Color.parseColor(EldThemeColor));

        violationView.setVisibility(View.GONE);
        personalUseBtn.setBackgroundResource(R.drawable.gray_eld_selector);


    }*/

    public void ActiveView(RelativeLayout layout, TextView violationView, TextView timeView, TextView jobTypeView,
                           TextView asPerShiftOrDayView, boolean isViolation){
        if(isViolation) {
            layout.setBackgroundResource(R.drawable.red_eld_selector);
            violationView.setVisibility(View.VISIBLE);

        }else {
            layout.setBackgroundResource(R.drawable.eld_blue_new_selector);
            violationView.setVisibility(View.GONE);
        }

        violationView.setTextColor(Color.WHITE);
        timeView.setTextColor(Color.WHITE);
        jobTypeView.setTextColor(Color.WHITE);

        asPerShiftOrDayView.setTextColor(Color.WHITE);

    }


    @SuppressLint("Range")
    public void InActiveView(RelativeLayout layout, TextView violationView, TextView timeView, TextView jobTypeView,
                             TextView asPerShiftOrDayView, Button personalUseBtn, boolean IsPersonalAllowed, Context context){
        String EldThemeColor = "", SkyBlueColor = "";    //4A88CC

        //if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled())  {
            EldThemeColor = "#F0F0F0";
            SkyBlueColor = "#ffffff";
        }else{
            EldThemeColor = "#1A3561";
            SkyBlueColor = "#1A3561";
        }
        layout.setBackgroundResource(R.drawable.gray_eld_selector);
        violationView.setTextColor(Color.parseColor(SkyBlueColor));
        timeView.setTextColor(Color.parseColor(SkyBlueColor));

        jobTypeView.setTextColor(Color.parseColor(EldThemeColor));

        asPerShiftOrDayView.setTextColor(Color.parseColor(EldThemeColor));

        if(IsPersonalAllowed && SharedPref.isPersonalUse75KmCrossed(context) == false) {
            personalUseBtn.setTextColor(Color.parseColor(EldThemeColor));
        }else{
            personalUseBtn.setTextColor(Color.parseColor("#ABAAAB"));
        }

        violationView.setVisibility(View.GONE);
        personalUseBtn.setBackgroundResource(R.drawable.gray_eld_selector);


    }

    public void AddTempRemark() {
        Globally.onDutyRemarks = new ArrayList<String>();
        Globally.onDutyRemarks.add("Brake Checks");
        Globally.onDutyRemarks.add("Border Crossing");
        Globally.onDutyRemarks.add("Equipment Wash");
        Globally.onDutyRemarks.add("Fueling");
        Globally.onDutyRemarks.add("Load Check");
        Globally.onDutyRemarks.add("Loading");
        Globally.onDutyRemarks.add("Pre-Trip + fueling");
        Globally.onDutyRemarks.add("Pre-Trip + loading");
        Globally.onDutyRemarks.add("Pre-Trip + unloading");
        Globally.onDutyRemarks.add("Pre-Trip/Post Trip");
        Globally.onDutyRemarks.add("RoadSide Inspection");
        Globally.onDutyRemarks.add("Scale");
        Globally.onDutyRemarks.add("Scale Inspection");
        Globally.onDutyRemarks.add("Trailer Drop");
        Globally.onDutyRemarks.add("Trailer Pickup");
        Globally.onDutyRemarks.add("Trailer Reject");
        Globally.onDutyRemarks.add("Trailer Repair");
        Globally.onDutyRemarks.add("Trailer Switch");
        Globally.onDutyRemarks.add("Trailer Wash");
        Globally.onDutyRemarks.add("Truck Wash");
        Globally.onDutyRemarks.add("Truck/Trailer Wash");
        Globally.onDutyRemarks.add("Unloading");
        Globally.onDutyRemarks.add("Yard Move");
        Globally.onDutyRemarks.add("Others");



    }

    
}
