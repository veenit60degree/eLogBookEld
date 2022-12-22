package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.als.logistic.R;


public class RemainingTimeDialog extends Dialog {


    public interface RemainingTimeListener {

        public void CancelReady();
        public void OkBtnReady(int JobStatus);

    }


    Button btnAccept, btnDecline;
    TextView recordTitleTV, TitleTV;
    TextInputLayout trailorNoInputType;
    int JobStatus;
    String CurrentStatus, RemainingTime;
    private RemainingTimeListener timeListener;


    public RemainingTimeDialog(Context context, int status, String currentStatus, String time, RemainingTimeListener readyListener) {
        super(context);
        JobStatus = status;
        this.CurrentStatus = currentStatus;
        this.RemainingTime = time;
        this.timeListener = readyListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_trailor_fields);

        recordTitleTV = (TextView) findViewById(R.id.recordTitleTV);
        TitleTV = (TextView)findViewById(R.id.TitleTV);
        trailorNoInputType = (TextInputLayout)findViewById(R.id.trailorNoInputType);
        trailorNoInputType.setVisibility(View.GONE);
        TitleTV.setVisibility(View.GONE);
        recordTitleTV.setVisibility(View.VISIBLE);


        btnAccept           = (Button) findViewById(R.id.btnLoadingJob);
        btnDecline          = (Button) findViewById(R.id.btnCancelLoadingJob);

        btnAccept.setText("Ok");
        btnDecline.setText("Cancel");
        String alertTitle = "Your <b>" + CurrentStatus + "</b> remaining time is " + RemainingTime + " minutes.<br />Do you want to continue ?";
        recordTitleTV.setText(Html.fromHtml(alertTitle));

        btnAccept.setOnClickListener(new OkBtnListener());
        btnDecline.setOnClickListener(new CancelBtnListener());

    }


    private class OkBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            timeListener.OkBtnReady(JobStatus);
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            timeListener.CancelReady();
        }
    }





}
