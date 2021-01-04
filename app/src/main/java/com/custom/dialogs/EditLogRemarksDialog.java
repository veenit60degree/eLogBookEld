package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.constants.ConstantsEnum;
import com.google.android.material.textfield.TextInputLayout;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

public class EditLogRemarksDialog  extends Dialog {


    public interface RemarksListener {
        public void CancelReady();

        public void JobBtnReady(String Remarks);

    }

    private RemarksListener readyListener;
    EditText remarksEditText;
    Button btnLoadingJob, btnCancelLoadingJob;
    TextView TitleTV;
    TextInputLayout reasonInputLayout;


    public EditLogRemarksDialog(Context context, RemarksListener readyListener) {
        super(context);
        this.readyListener = readyListener;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_trailor_fields);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        remarksEditText = (EditText) findViewById(R.id.TrailorNoEditText);

        btnLoadingJob = (Button) findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button) findViewById(R.id.btnCancelLoadingJob);

        TitleTV = (TextView) findViewById(R.id.TitleTV);
        reasonInputLayout = (TextInputLayout) findViewById(R.id.trailorNoInputType);

        TitleTV.setText(getContext().getResources().getString(R.string.enter_reason));
        remarksEditText.setHint(getContext().getResources().getString(R.string.reason));
        reasonInputLayout.setHint(getContext().getResources().getString(R.string.reason));

        remarksEditText.setMinLines(2);
        remarksEditText.setMaxLines(4);

        btnLoadingJob.setOnClickListener(new TrailorFieldListener());
        btnCancelLoadingJob.setOnClickListener(new CancelBtnListener());

        HideKeyboard();
    }


    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }


    private class TrailorFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            String remarks = remarksEditText.getText().toString().trim();

            if (remarks.trim().length() >= 4) {
                HideKeyboard();

                readyListener.JobBtnReady(remarks);

            } else {
                Globally.EldScreenToast(remarksEditText, ConstantsEnum.EDIT_LOG_REASON_ALERT, getContext().getResources().getColor(R.color.red_eld));
            }

        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HideKeyboard();
            readyListener.CancelReady();
        }
    }
}