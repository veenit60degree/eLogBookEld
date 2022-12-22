package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.models.MalfunctionModel;

import java.util.List;

public class MalfunctionDialog  extends Dialog {



    public interface RecordsListener {
        public void RecordsOkBtn(String reason, List<MalfunctionModel> _listDataChild);
    }


    private RecordsListener recordsListener;
    Button btnLoadingJob, btnCancelLoadingJob;
    TextView TitleTV, recordTitleTV;
    EditText remarksEditText;
    TextInputLayout reasonInputLayout;
    List<MalfunctionModel> listData;

    public MalfunctionDialog(Context context, List<MalfunctionModel> _listData, RecordsListener recordsListener) {
        super(context);
        listData = _listData;
        this.recordsListener = recordsListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_trailor_fields);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        btnLoadingJob       = (Button)findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button)findViewById(R.id.btnCancelLoadingJob);

        TitleTV             = (TextView)findViewById(R.id.TitleTV);
        recordTitleTV       = (TextView)findViewById(R.id.recordTitleTV);
        remarksEditText     = (EditText)findViewById(R.id.TrailorNoEditText);

        reasonInputLayout   = (TextInputLayout)findViewById(R.id.trailorNoInputType);

        recordTitleTV.setVisibility(View.VISIBLE);
        TitleTV.setText(getContext().getResources().getString(R.string.malfunction_diagnostic_records));
        remarksEditText.setHint(getContext().getString(R.string.reason));
        reasonInputLayout.setHint(getContext().getResources().getString(R.string.reason));

        recordTitleTV.setText(getContext().getResources().getString(R.string.clear_reason));
        btnLoadingJob.setText(getContext().getResources().getString(R.string.clear));



        btnLoadingJob.setOnClickListener(new UnidentifiedFieldListener());

        btnCancelLoadingJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard();
                dismiss();
            }
        });


        HideKeyboard();
    }


    void HideKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)  getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {   }
    }



    private class UnidentifiedFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            HideKeyboard();

            if(Globally.isConnected(getContext())) {

                String remarks = remarksEditText.getText().toString().trim();

                if(remarks.length() > 0) {

                    recordsListener.RecordsOkBtn(remarks, listData);

                }else{
                    Globally.EldScreenToast(remarksEditText, "Enter reason first", getContext().getResources().getColor(R.color.colorVoilation));
                }
            }else{
                Globally.EldScreenToast(v, Globally.CHECK_INTERNET_MSG, getContext().getResources().getColor(R.color.colorVoilation));
            }



        }
    }






}
