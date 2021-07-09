package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

public class DeferralDialog extends Dialog {

    public interface DeferralListener {
        public void JobBtnReady();
        public void CancelBtnReady();
    }


    Constants constants;
    private int time;
    private DeferralListener readyListener;

    public DeferralDialog(Context context, int time, DeferralListener readyListener) {
        super(context);
        this.time = time;
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.dialog_deferral);
        setCancelable(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

        constants = new Constants();

        TextView defTimeTxtView           = (TextView) findViewById(R.id.defTimeTxtView);
        TextView cancelDefTxtView      = (TextView)findViewById(R.id.cancelDefTxtView);
        LinearLayout defConfirmBtn         = (LinearLayout) findViewById(R.id.defConfirmBtn);

        int hours = Globally.HourFromMin(time);
        int min = Globally.MinFromHourOnly(time);
        defTimeTxtView.setText(""+hours + " hrs " + min + " mins");

        defConfirmBtn.setOnClickListener(new LoadingJobListener());
        cancelDefTxtView.setOnClickListener(new CancelJobListener());
    }



    private class LoadingJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.JobBtnReady();
            dismiss();
        }
    }

    private class CancelJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.CancelBtnReady();
            dismiss();
        }
    }



}
