package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import java.util.Map;

public class UnidentifiedDialog extends Dialog {



    public interface RecordsListener {
        public void RecordsOkBtn(String reason, String status, String type);
    }


    private RecordsListener recordsListener;
    Button btnLoadingJob, btnCancelLoadingJob;
    TextView TitleTV, recordTitleTV;
    String DriverId, recordType;
    LinearLayout driverStatusLay;
    EditText remarksEditText;
    RadioGroup unIdentifyRadGroup;
    RadioButton drivingRadBtn, onDutyRadBtn, personalRadBtn;
    TextInputLayout reasonInputLayout;

    Map<String, String> params;
    VolleyRequest claimRequest, rejectRequest;


    public UnidentifiedDialog(Context context, String type, RecordsListener recordsListener) {
        super(context);
        recordType = type;
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

        claimRequest        = new VolleyRequest(getContext());
        rejectRequest       = new VolleyRequest(getContext());

        btnLoadingJob       = (Button)findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button)findViewById(R.id.btnCancelLoadingJob);

        TitleTV             = (TextView)findViewById(R.id.TitleTV);
        recordTitleTV       = (TextView)findViewById(R.id.recordTitleTV);
        remarksEditText     = (EditText)findViewById(R.id.TrailorNoEditText);

        driverStatusLay     = (LinearLayout)findViewById(R.id.driverStatusLay);
        unIdentifyRadGroup  = (RadioGroup)findViewById(R.id.unIdentifyRadGroupp);
        drivingRadBtn       = (RadioButton) findViewById(R.id.drivingRadBtnn);
        onDutyRadBtn        = (RadioButton)findViewById(R.id.onDutyRadBtnn);
        personalRadBtn      = (RadioButton)findViewById(R.id.personalRadBtnn);
        reasonInputLayout   = (TextInputLayout)findViewById(R.id.trailorNoInputType);


        DriverId                = SharedPref.getDriverId( getContext());


        recordTitleTV.setVisibility(View.VISIBLE);
        TitleTV.setText(getContext().getResources().getString(R.string.unIdentified_records));
        remarksEditText.setHint(getContext().getString(R.string.reason));
        reasonInputLayout.setHint(getContext().getResources().getString(R.string.reason));


        if(recordType.equals(getContext().getResources().getString(R.string.claim))){
            recordTitleTV.setText(getContext().getResources().getString(R.string.claim_reason));
            btnLoadingJob.setText(getContext().getResources().getString(R.string.claim));
            driverStatusLay.setVisibility(View.VISIBLE);
        }else{
            recordTitleTV.setText(getContext().getResources().getString(R.string.reject_reason));
            btnLoadingJob.setText(getContext().getResources().getString(R.string.reject));
        }



        unIdentifyRadGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.drivingRadBtnn) {
                    drivingRadBtn.setTextColor(getContext().getResources().getColor(R.color.hos_location));
                    onDutyRadBtn.setTextColor(getContext().getResources().getColor(R.color.black_semi));
                    personalRadBtn.setTextColor(getContext().getResources().getColor(R.color.black_semi));
                }else if(checkedId == R.id.onDutyRadBtnn){
                    drivingRadBtn.setTextColor(getContext().getResources().getColor(R.color.black_semi));
                    onDutyRadBtn.setTextColor(getContext().getResources().getColor(R.color.hos_location));
                    personalRadBtn.setTextColor(getContext().getResources().getColor(R.color.black_semi));
                }else{
                    drivingRadBtn.setTextColor(getContext().getResources().getColor(R.color.black_semi));
                    onDutyRadBtn.setTextColor(getContext().getResources().getColor(R.color.black_semi));
                    personalRadBtn.setTextColor(getContext().getResources().getColor(R.color.hos_location));
                }
            }
        });


        
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
                    if (recordType.equals(getContext().getResources().getString(R.string.claim))) {

                        int radioButtonID = unIdentifyRadGroup.getCheckedRadioButtonId();
                        View radioButton = unIdentifyRadGroup.findViewById(radioButtonID);
                        int idx = unIdentifyRadGroup.indexOfChild(radioButton);

                        String StatusId = Constants.getDriverStatus(idx);
                        if (StatusId.length() > 0) {
                            recordsListener.RecordsOkBtn(remarks, StatusId, recordType);
                        } else {
                            Globally.EldScreenToast(remarksEditText, "Select status to claim the records.", getContext().getResources().getColor(R.color.colorVoilation));
                        }

                    } else {
                        recordsListener.RecordsOkBtn(remarks, "", recordType);
                    }
                }else{
                    Globally.EldScreenToast(remarksEditText, "Enter reason first", getContext().getResources().getColor(R.color.colorVoilation));
                }
            }else{
                Globally.EldScreenToast(btnLoadingJob, Globally.CHECK_INTERNET_MSG, getContext().getResources().getColor(R.color.colorVoilation));
            }



        }
    }






}
