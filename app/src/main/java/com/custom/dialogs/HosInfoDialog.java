package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.als.logistic.Globally;
import com.als.logistic.R;

import models.RulesResponseObject;


public class HosInfoDialog extends Dialog{

    RulesResponseObject RemainingTimeObj, RulesObj;
    TextView logoutTV, TitleTV, hosSummaryTxtVw;
    Button btnOk;
    boolean isTablet;
    int TotalOnDutyHoursInt;
    int TotalDrivingHoursInt;
    int LeftDayOnDutyHoursInt;
    int LeftDayDrivingHoursInt;
    int LeftWeekOnDutyHoursInt;


    public HosInfoDialog(Context context, RulesResponseObject RemainingTime, RulesResponseObject rulesObj) {
        super(context);
        this.RemainingTimeObj = RemainingTime;
        this.RulesObj = rulesObj;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_update_app);

        int dialogWidth = 0;
        isTablet = Globally.isTablet(getContext());

        if(isTablet){
            dialogWidth = Globally.dp2px(600, getContext());
        }else{
            dialogWidth = Globally.dp2px(470, getContext());
        }

        getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        logoutTV        = (TextView)findViewById(R.id.logoutTV);
        TitleTV         = (TextView)findViewById(R.id.TitleTV);
        hosSummaryTxtVw = (TextView)findViewById(R.id.recordTitleTV);

        btnOk           = (Button)findViewById(R.id.btnUpdateApp);

        logoutTV.setVisibility(View.GONE);
        hosSummaryTxtVw.setGravity(Gravity.LEFT);

        int hosSummaryPadding;
        if(isTablet){
            hosSummaryPadding = Globally.dp2px(35, getContext());
        }else{
            hosSummaryPadding = Globally.dp2px(20, getContext());
        }
        hosSummaryTxtVw.setPadding(hosSummaryPadding, 0 , 0 ,0);
        btnOk.setText(getContext().getResources().getString(R.string.ok));
        TitleTV.setText(getContext().getResources().getString(R.string.available_hour_summary));

        DrivingOnDutyCalculations();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    void DrivingOnDutyCalculations() {

        LeftWeekOnDutyHoursInt  = (int) RulesObj.getCycleRemainingMinutes();
        TotalOnDutyHoursInt     = (int) RemainingTimeObj.getOnDutyUsedMinutes();   // hMethods.GetOnDutyTime(currentDayArray);
        TotalDrivingHoursInt    = (int) RemainingTimeObj.getDrivingUsedMinutes();  // hMethods.GetDrivingTime(currentDayArray);
        LeftDayOnDutyHoursInt   = (int) RemainingTimeObj.getOnDutyRemainingMinutes();
        LeftDayDrivingHoursInt  = (int) RemainingTimeObj.getDrivingRemainingMinutes();

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

        String hosSummary = "<font color='" + getContext().getResources().getColor(R.color.color_eld_theme_one) + "'>Driving Used &nbsp;&nbsp;:       "+
                Globally.FinalValue(TotalDrivingHoursInt) +
                "<br/>Driving Left&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: " + Globally.FinalValue(LeftDayDrivingHoursInt) +"</font>" +

                "<font color='" +getContext().getResources().getColor(R.color.color_eld_theme_one) + "'><br/><br/>OnDuty Used &nbsp;&nbsp;&nbsp;:       "+
                Globally.FinalValue(TotalOnDutyHoursInt) + 
                "<br/>OnDuty Left&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: " + Globally.FinalValue(LeftDayOnDutyHoursInt) +

                "</font>";

        hosSummaryTxtVw.setText(Html.fromHtml(hosSummary));

    }




}
