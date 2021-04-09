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

    private  String DriverId, changedCycleId, changedCycleName, savedCycleType;
    private ConfirmationListener readyListener;
    Globally Global;
    Constants constants;
    HelperMethods hMethods;
    DBHelper dbHelper;
    SharedPref sharedPref;


    public CycleChangeRequestDialog(Context context, String driverId, String changedCycle, ConfirmationListener readyListener) {
        super(context);
        this.DriverId = driverId;
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

        String cycleCalculatedData = constants.CalculateCycleTimeData(getContext(), DriverId, false, false, changedCycleId, Global, sharedPref, hMethods, dbHelper);
        changedCycleRuleTxtVw.setText(Html.fromHtml(cycleCalculatedData) );

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



}

