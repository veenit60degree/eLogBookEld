package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.messaging.logistic.Globally;
import com.messaging.logistic.R;


public class RecapDialog extends Dialog {

    public interface RecapListener {
        public void CancelReady();
        public void JobBtnReady();

    }

    private int type;
    private String MainDriver, CoDriver, DriveHrsToday, OnDutyHrsToday, CycleRemingToday, Cycle;
    private RecapListener readyListener;

    public RecapDialog(Context context, int type, String MainDriver, String CoDriver, String DriveHrsToday,
                       String OnDutyHrsToday, String CycleremingToday, String cycle,
                       RecapListener readyListener) {
        super(context);
        this.type = type;
        this.MainDriver = MainDriver;
        this.CoDriver = CoDriver;
        this.DriveHrsToday = DriveHrsToday;
        this.OnDutyHrsToday = OnDutyHrsToday;
        this.CycleRemingToday = CycleremingToday;
        Cycle = cycle;
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.popup_recap_eld);
        setCancelable(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);

        Button btnLoadingJob,  btnCancelLoadingJob;
        final TextView recap_driver_tv, recap_co_driver_tv, recap_current_cycle_tv, recap_avail_hr_tv;
        final TextView recap_truck_tv, recap_trailer_tv, recap_onDuty_ava_tv, recap_cycle_Rem_Hrs_tv;

        btnLoadingJob           = (Button)findViewById(R.id.recapOkBtn);
        btnCancelLoadingJob     = (Button)findViewById(R.id.recapCancelBtn);

        recap_driver_tv         = (TextView) findViewById(R.id.recap_driver_tv);
        recap_co_driver_tv      = (TextView) findViewById(R.id.recap_co_driver_tv);
        recap_current_cycle_tv  = (TextView) findViewById(R.id.recap_current_cycle_tv);
        recap_avail_hr_tv       = (TextView) findViewById(R.id.recap_avail_hr_tv);

        recap_truck_tv          = (TextView) findViewById(R.id.recap_truck_tv);
        recap_trailer_tv        = (TextView) findViewById(R.id.recap_trailer_tv);
        recap_onDuty_ava_tv     = (TextView) findViewById(R.id.recap_onDuty_ava_tv);
        recap_cycle_Rem_Hrs_tv  = (TextView) findViewById(R.id.recap_cycle_Rem_Hrs_tv);

        if(type == 0)
            recap_driver_tv.setText(MainDriver);
        else
            recap_driver_tv.setText(CoDriver);

        if(DriveHrsToday.equalsIgnoreCase("null"))
            DriveHrsToday = "00:00";

        if(OnDutyHrsToday.equalsIgnoreCase("null"))
            OnDutyHrsToday = "00:00";

        if(CycleRemingToday.equalsIgnoreCase("null"))
            CycleRemingToday = "00:00";


        recap_current_cycle_tv.setText(Cycle);
        recap_avail_hr_tv.setText(DriveHrsToday);

        recap_trailer_tv.setText(Globally.TRAILOR_NUMBER);
        recap_truck_tv.setText(Globally.TRUCK_NUMBER);
        recap_onDuty_ava_tv.setText(OnDutyHrsToday);
        recap_cycle_Rem_Hrs_tv.setText(CycleRemingToday);
        btnLoadingJob.setBackgroundResource(R.drawable.green_selector);

        btnLoadingJob.setOnClickListener(new LoadingJobListener());
        btnCancelLoadingJob.setOnClickListener(new CancelBtnListener());

    }



    private class LoadingJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.JobBtnReady();
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.CancelReady();
        }
    }

}
