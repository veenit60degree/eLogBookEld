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

import com.constants.SharedPref;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

/**
 * Created by kumar on 8/30/2017.
 */

public class LoadingFieldDialog extends Dialog {

    public interface LoadingListener {
        public void CancelReady();
        public void JobBtnReady();

    }

    private String type, title;
    private LoadingListener readyListener;

    public LoadingFieldDialog(Context context, String type, String title, LoadingListener readyListener) {
        super(context);
        this.type = type;
        this.title = title;
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.popup_loading_fields);
        setCancelable(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);

        Button btnLoadingJob,  btnCancelLoadingJob;
        final TextView tractorTextView, trailorTextView, ConfirmTitleTV;

        btnLoadingJob = (Button)findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button)findViewById(R.id.btnCancelLoadingJob);

        trailorTextView = (TextView) findViewById(R.id.trailorTextView);
        tractorTextView = (TextView) findViewById(R.id.tractorTextView);
        ConfirmTitleTV = (TextView) findViewById(R.id.ConfirmTitleTV);

        trailorTextView.setText(SharedPref.getTrailorNumber(getContext()));
        tractorTextView.setText(SharedPref.getTruckNumber(getContext()));

        ConfirmTitleTV.setText(title);

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
