package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.constants.Constants;
import com.constants.SharedPref;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

public class ChangeCycleDialog extends Dialog {

    public interface ChangeCycleListener {
        public void ChangeCycleBtn(String type);

    }

    private TextView changeCycleTitleTV, changeCycleDescTV, changedCycleRuleTxtVw;
    private Button btnChangeCycle, cancelPopupButton;
    private String type, currentCycle, changedCycleId, currentOpZone;
    private ChangeCycleListener changeCycleListener;
    Constants constants;
    Globally Global;
    HelperMethods hMethods;
    DBHelper dbHelper;
    SharedPref sharedPref;


    public ChangeCycleDialog(Context context, String type, String currentCycle, String currentOpZone,  ChangeCycleListener readyListener) {
        super(context);
        this.type = type;
        this.currentCycle = currentCycle;
        this.currentOpZone = currentOpZone;
        this.changeCycleListener = readyListener;

        Global      = new Globally();
        constants = new Constants();
        hMethods = new HelperMethods();
        dbHelper = new DBHelper(getContext());
        sharedPref = new SharedPref();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.popup_edit_delete_lay);
       // setCancelable(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        if(Globally.isTablet(getContext())) {
            lp.width = constants.intToPixel(getContext(), 500);
        }else{
            lp.width = constants.intToPixel(getContext(), 400);
        }
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);



        changeCycleTitleTV      = (TextView) findViewById(R.id.changeTitleView);
        changeCycleDescTV       = (TextView)findViewById(R.id.titleDescView);
        changedCycleRuleTxtVw   = (TextView)findViewById(R.id.changedCycleRuleTxtVw);

        btnChangeCycle          = (Button)findViewById(R.id.confirmPopupButton);
        cancelPopupButton       = (Button)findViewById(R.id.cancelPopupButton);

        changedCycleRuleTxtVw.setVisibility(View.VISIBLE);

        btnChangeCycle.setText(getContext().getString(R.string.change));
       // cancelPopupButton.setText(getContext().getString(R.string.no));
        changeCycleTitleTV.setText(getContext().getString(R.string.Confirmation_suggested));

        changeCycleDescTV.setTextColor(getContext().getResources().getColor(R.color.black_hover));
        cancelPopupButton.setTextColor(getContext().getResources().getColor(R.color.black_hover));

        boolean OperatingZoneChange = false;
        boolean isNorth = false;

        if(type.equals("us_cycle")){
          //  changeCycleTitleTV.setText(getContext().getString(R.string.canada_cycle));
            if(currentCycle.equals(Globally.USA_WORKING_6_DAYS)){
                changedCycleId = Globally.USA_WORKING_7_DAYS;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>" +  Globally.USA_WORKING_6_DAYS_NAME + "</b> to <b>" + Globally.USA_WORKING_7_DAYS_NAME  + "</b> ?")  );
            }else {
                changedCycleId = Globally.USA_WORKING_6_DAYS;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>" + Globally.USA_WORKING_7_DAYS_NAME + "</b> to <b>" + Globally.USA_WORKING_6_DAYS_NAME  + "</b> ?") );
            }
        }else if(type.equals("can_cycle")){
          //  changeCycleTitleTV.setText(getContext().getString(R.string.usa_cycle));
            if(currentCycle.equals(Globally.CANADA_CYCLE_1)){
                changedCycleId = Globally.CANADA_CYCLE_2;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>" + Globally.CANADA_CYCLE_1_NAME + "</b> to <b>" + Globally.CANADA_CYCLE_2_NAME  + "</b> ?") );
            }else {
                changedCycleId = Globally.CANADA_CYCLE_1;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>" + Globally.CANADA_CYCLE_2_NAME + "</b> to <b>" + Globally.CANADA_CYCLE_1_NAME   + "</b> ?"));
            }
        }else if(type.equals("operating_zone")){
            OperatingZoneChange = true;
            if(currentCycle.equals(Globally.CANADA_CYCLE_1)){
                changedCycleId = Globally.CANADA_CYCLE_2;
            }else {
                changedCycleId = Globally.CANADA_CYCLE_1;
            }

            if(currentOpZone.equals(getContext().getResources().getString(R.string.OperatingZoneSouth))){
                isNorth = true;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_op_zone) + " <b>" + getContext().getString(R.string.OperatingZoneSouth) + "</b> to <b>" + getContext().getString(R.string.OperatingZoneNorth) + "</b>"));
            }else{
                isNorth = false;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_op_zone) + " <b>" + getContext().getString(R.string.OperatingZoneNorth) + "</b> to <b>" + getContext().getString(R.string.OperatingZoneSouth) + "</b>") );
            }

           // changeCycleTitleTV.setText(getContext().getString(R.string.Operating_zone));

        }else{
            if(currentCycle.equals(Globally.CANADA_CYCLE_1) || currentCycle.equals(Globally.CANADA_CYCLE_2) ){
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>Canada</b> to <b>USA</b> ?") );
            }else{
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>USA</b> to <b>Canada</b> ?") );
            }
        }

        String cycleCalculatedData = constants.CalculateCycleTimeData(getContext(), sharedPref.getDriverId( getContext()), OperatingZoneChange, isNorth,
                                        changedCycleId, Global, sharedPref, hMethods, dbHelper);
        changedCycleRuleTxtVw.setText(Html.fromHtml(cycleCalculatedData) );

        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnChangeCycle.setOnClickListener(new ChangeFieldListener());

    }


    private class ChangeFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            changeCycleListener.ChangeCycleBtn(type);
            dismiss();
        }
    }


}
