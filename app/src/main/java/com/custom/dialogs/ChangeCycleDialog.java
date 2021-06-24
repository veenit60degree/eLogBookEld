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
import android.widget.LinearLayout;
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

    private TextView changeCycleTitleTV, changeCycleDescTV, changedCycleRuleDescVw, oldCycleRuleDescVw;
    LinearLayout cycleRuleLay;
    private Button btnChangeCycle, cancelPopupButton;
    private String type, currentCycle, changedCycleId, currentOpZone;
    private ChangeCycleListener changeCycleListener;
    Constants constants;
    Globally Global;
    HelperMethods hMethods;
    DBHelper dbHelper;


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
            lp.width = constants.intToPixel(getContext(), 650);
        }else{
            lp.width = constants.intToPixel(getContext(), 550);
        }
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);



        changeCycleTitleTV      = (TextView) findViewById(R.id.changeTitleView);
        changeCycleDescTV       = (TextView)findViewById(R.id.titleDescView);
        changedCycleRuleDescVw  = (TextView)findViewById(R.id.changedCycleRuleDescVw);
        oldCycleRuleDescVw      = (TextView)findViewById(R.id.oldCycleRuleDescVw);

        btnChangeCycle          = (Button)findViewById(R.id.confirmPopupButton);
        cancelPopupButton       = (Button)findViewById(R.id.cancelPopupButton);
        cycleRuleLay            = (LinearLayout) findViewById(R.id.cycleRuleLay);

        cycleRuleLay.setVisibility(View.VISIBLE);

        btnChangeCycle.setText(getContext().getString(R.string.change));
       // cancelPopupButton.setText(getContext().getString(R.string.no));

        changeCycleDescTV.setTextColor(getContext().getResources().getColor(R.color.black_hover));
        cancelPopupButton.setTextColor(getContext().getResources().getColor(R.color.black_hover));

        boolean OperatingZoneChange = false;
        boolean OperatingZoneChangeOld = true;
        boolean isNorth = false;
        boolean isNorthOldValue = false;

        if(type.equals("us_cycle")){
            changeCycleTitleTV.setText(getContext().getString(R.string.us_cycle_change));
            if(currentCycle.equals(Globally.USA_WORKING_6_DAYS)){
                changedCycleId = Globally.USA_WORKING_7_DAYS;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>" +
                        Globally.USA_WORKING_6_DAYS_NAME + "</b> to <b>" + Globally.USA_WORKING_7_DAYS_NAME  + "</b> ?")  );
            }else {
                changedCycleId = Globally.USA_WORKING_6_DAYS;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>" +
                        Globally.USA_WORKING_7_DAYS_NAME + "</b> to <b>" + Globally.USA_WORKING_6_DAYS_NAME  + "</b> ?") );
            }
        }else if(type.equals("can_cycle")){
            changeCycleTitleTV.setText(getContext().getString(R.string.canada_cycle_change));
            String cycleName = "";
            if(SharedPref.IsNorthCanada(getContext())){
                cycleName = "Cycle 1 (80/7) (N)";
            } else{
                cycleName = "Cycle 1 (70/7)";
            }
            if(currentCycle.equals(Globally.CANADA_CYCLE_1)){
                changedCycleId = Globally.CANADA_CYCLE_2;

                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>" +
                        cycleName + "</b> to <b>" + "Cycle 2 (120/14)</b> ?") );
            }else {
                changedCycleId = Globally.CANADA_CYCLE_1;

                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>" +
                          "Cycle 2 (120/14)</b> to <b>" + cycleName  + "</b> ?") );
            }
        }else if(type.equals("operating_zone")){
            OperatingZoneChange = true;
            OperatingZoneChangeOld = false;
            changedCycleId = currentCycle;
            changeCycleTitleTV.setText(getContext().getString(R.string.operating_zone_change));

            if(currentOpZone.equals(getContext().getResources().getString(R.string.OperatingZoneSouth))){
                isNorth = true;
                isNorthOldValue = false;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_op_zone) + " <b>" + getContext().getString(R.string.OperatingZoneSouth) + "</b> to <b>" + getContext().getString(R.string.OperatingZoneNorth) + "</b>"));
            }else{
                isNorth = false;
                isNorthOldValue = true;
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_op_zone) + " <b>" + getContext().getString(R.string.OperatingZoneNorth) + "</b> to <b>" + getContext().getString(R.string.OperatingZoneSouth) + "</b>") );
            }

        }else{
            changeCycleTitleTV.setText(getContext().getString(R.string.cycle_change));
            if(currentCycle.equals(Globally.CANADA_CYCLE_1) || currentCycle.equals(Globally.CANADA_CYCLE_2) ){
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>Canada</b> to <b>USA</b> ?") );
            }else{
                changeCycleDescTV.setText(Html.fromHtml(getContext().getString(R.string.want_change_cycle) + " <b>USA</b> to <b>Canada</b> ?") );
            }
        }


        String changedCycleCalculatedData = constants.CalculateCycleTimeData(getContext(), SharedPref.getDriverId( getContext()), OperatingZoneChange, isNorth,
                                        changedCycleId, Global, hMethods, dbHelper);
        changedCycleRuleDescVw.setText(Html.fromHtml(changedCycleCalculatedData) );

        String oldCycleCalculatedData = constants.CalculateCycleTimeData(getContext(), SharedPref.getDriverId( getContext()), OperatingZoneChangeOld, isNorthOldValue,
                currentCycle, Global, hMethods, dbHelper);
        oldCycleRuleDescVw.setText(Html.fromHtml(oldCycleCalculatedData));


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
