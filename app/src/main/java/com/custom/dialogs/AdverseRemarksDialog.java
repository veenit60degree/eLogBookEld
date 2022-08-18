package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.google.android.material.textfield.TextInputLayout;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.models.DriverLocationModel;

import java.util.ArrayList;
import java.util.List;

public class AdverseRemarksDialog extends Dialog {


    public interface RemarksListener {
        public void CancelReady();
        public void JobBtnReady(String Remarks, boolean isClaim, boolean isCompanyAssigned,String StartOdometer,String EndOdometer,String StartLocation,String EndLocation,String StartCity,String StartState,String StartCountry,String EndCity,String EndState,String EndCountry,boolean startOdometer, boolean endOdometer, boolean startLocation, boolean endLocation);

    }

    private RemarksListener readyListener;
    EditText remarksEditText,startOdoEditText,endOdometerEditTxt,startLocationEditText,endLocationEditText,startAddressEditText,startPostolCodeEditText,endAddressEditText,endPostolCodeEditText;
    Button btnLoadingJob, btnCancelLoadingJob;
    TextView TitleTV, descTextView, desc2TxtView,startLocationTV,endLocationTV;
    TextInputLayout reasonInputLayout;
    List<DriverLocationModel> locationList;
    List<String> stateList;
    boolean isAdverse, IsClaim, IsCompanyAssign,startOdometer,endOdometer,startLocation,endLocation;
    Globally globally;
    LinearLayout startOdometerLay,endOdometerLay,startLocationLay,endLocationLay;
    Spinner startStateSelectSpinner,endStateSelectSpinner,selectUnitStartOdo,selectUnitEndOdo;
    int startStatePostion = 0,endStatePostion = 0;
    String StartSelectedState = "" , EndSelectedState = "",StartSelectedCountry = "",EndSelectedCountry ="";
    ArrayList<String> DistanceTypeList;
    String StartOdometerDistanceType = "",EndOdometerDistanceType = "";

    public AdverseRemarksDialog(Context context, boolean isAdverse, boolean isClaim, boolean isCompanyAssign, boolean startOdometer, boolean endOdometer, boolean startLocation, boolean endLocation, List<DriverLocationModel>  locList,List<String> stateList, RemarksListener readyListener) {
        super(context);
        this.isAdverse = isAdverse;
        this.IsClaim = isClaim;
        this.IsCompanyAssign = isCompanyAssign;
        this.readyListener = readyListener;
        this.startOdometer = startOdometer;
        this.startLocation = startLocation;
        this.endOdometer = endOdometer;
        this.endLocation = endLocation;
        this.locationList = locList;
        this.stateList  = stateList;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_trailor_fields);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        globally = new Globally();
        remarksEditText   = (EditText)findViewById(R.id.TrailorNoEditText);
        startOdoEditText   = (EditText)findViewById(R.id.startOdoEditText);
        endOdometerEditTxt   = (EditText)findViewById(R.id.endOdometerEditTxt);
        startLocationEditText   = (EditText)findViewById(R.id.startLocationEditText);
        endLocationEditText   = (EditText)findViewById(R.id.endLocationEditText);
        startAddressEditText   = (EditText)findViewById(R.id.startAddressEditText);
        endAddressEditText   = (EditText)findViewById(R.id.endAddressEditText);

        btnLoadingJob       = (Button)findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button)findViewById(R.id.btnCancelLoadingJob);

        descTextView        = (TextView)findViewById(R.id.recordTitleTV);
        desc2TxtView        = (TextView)findViewById(R.id.desc2TxtView);
        TitleTV             = (TextView)findViewById(R.id.TitleTV);
        startLocationTV     = (TextView)findViewById(R.id.startLocationTV);
        endLocationTV       = (TextView)findViewById(R.id.endLocationTV);
        reasonInputLayout   = (TextInputLayout)findViewById(R.id.trailorNoInputType);

        startOdometerLay   = (LinearLayout)findViewById(R.id.startOdometerLay);
        endOdometerLay     = (LinearLayout)findViewById(R.id.endOdometerLay);
        startLocationLay   = (LinearLayout)findViewById(R.id.startLocationLay);
        endLocationLay     = (LinearLayout)findViewById(R.id.endLocationLay);

        startStateSelectSpinner     = (Spinner) findViewById(R.id.startStateSelectSpinner);
        endStateSelectSpinner     = (Spinner) findViewById(R.id.endStateSelectSpinner);
        selectUnitStartOdo = (Spinner) findViewById(R.id.selectUnitStartOdo);
        selectUnitEndOdo = (Spinner) findViewById(R.id.selectUnitEndOdo);


        remarksEditText.setHint(getContext().getResources().getString(R.string.reason));
        reasonInputLayout.setHint(getContext().getResources().getString(R.string.reason));
        startOdoEditText.setHint("Start Odometer");
        endOdometerEditTxt.setHint("End Odometer");
        startLocationEditText.setHint("Enter City");
        endLocationEditText.setHint("Enter City");
        startAddressEditText.setHint("Enter Address");
//        startPostolCodeEditText.setHint("Enter Postol Code");
        endAddressEditText.setHint("Enter Address");
//        endPostolCodeEditText.setHint("Enter Postol Code");
        startLocationTV.setText("Start Location : ");
        endLocationTV.setText("End Location : ");

        if(startOdometer){
            startOdometerLay.setVisibility(View.VISIBLE);
        }
        if(endOdometer){
            endOdometerLay.setVisibility(View.VISIBLE);
        }
        if(startLocation){
            startLocationLay.setVisibility(View.VISIBLE);
        }
        if(endLocation){
            endLocationLay.setVisibility(View.VISIBLE);
        }

        if(isAdverse) {
            TitleTV.setText(getContext().getResources().getString(R.string.reason_for_adverse_excptn));

            String desc = "<font color='#555555'><b>Note: </b></font>" + getContext().getResources().getString(R.string.reason_for_adverse_excptn_desc) ;
            descTextView.setText(Html.fromHtml(desc));
            descTextView.setTextColor(getContext().getResources().getColor(R.color.gray_text));
            if(UILApplication.getInstance().isNightModeEnabled()){
                descTextView.setTextColor(getContext().getResources().getColor(R.color.white));
            }
            desc2TxtView.setText(". " + getContext().getResources().getString(R.string.excp_reset_auto));
            descTextView.setVisibility(View.VISIBLE);
            desc2TxtView.setVisibility(View.VISIBLE);


        }else{
            descTextView.setVisibility(View.GONE);

            if(IsClaim){
                btnLoadingJob.setText(getContext().getResources().getString(R.string.claim));
                TitleTV.setText(getContext().getResources().getString(R.string.unIdentified_records));
            }else{
                btnLoadingJob.setText(getContext().getResources().getString(R.string.reject));
                if(IsCompanyAssign) {
                    TitleTV.setText(getContext().getResources().getString(R.string.reason_for_reject_company));
                }else{
                    TitleTV.setText(getContext().getResources().getString(R.string.reason_for_reject));
                }
            }
        }


        if(startOdometer || endOdometer || startLocation || endLocation){
            descTextView.setVisibility(View.VISIBLE);
            descTextView.setText(getContext().getResources().getString(R.string.input_req_miss_elem));
            descTextView.setTextColor(getContext().getResources().getColor(R.color.colorVoilation));
        }


        remarksEditText.setSingleLine(false);
        remarksEditText.setMinLines(2);
        remarksEditText.setMaxLines(4);

        btnLoadingJob.setOnClickListener(new TrailorFieldListener());
        btnCancelLoadingJob.setOnClickListener(new CancelBtnListener());

        if(stateList != null) {
            ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, stateList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            startStateSelectSpinner.setAdapter(dataAdapter);
            endStateSelectSpinner.setAdapter(dataAdapter);
        }

        DistanceTypeList = new ArrayList<String>();
        DistanceTypeList.add("Select Unit");
        DistanceTypeList.add("KM");
        DistanceTypeList.add("Miles");

        ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, DistanceTypeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectUnitStartOdo.setAdapter(dataAdapter);
        selectUnitEndOdo.setAdapter(dataAdapter);

        selectUnitStartOdo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StartOdometerDistanceType = DistanceTypeList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectUnitEndOdo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                EndOdometerDistanceType = DistanceTypeList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Spinner click listener
        startStateSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                //String item = parent.getItemAtPosition(pos).toString();
                startStatePostion = pos;
                StartSelectedState = locationList.get(pos).getState();
                StartSelectedCountry = locationList.get(pos).getCountry();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Spinner click listener
        endStateSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                //String item = parent.getItemAtPosition(pos).toString();
                endStatePostion = pos;
                EndSelectedState = locationList.get(pos).getState();
                EndSelectedCountry = locationList.get(pos).getCountry();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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



    private class TrailorFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {



            String remarks = remarksEditText.getText().toString().trim();
            String startOdo = startOdoEditText.getText().toString().trim();
            String endOdo = endOdometerEditTxt.getText().toString().trim();
            String startLoc = startAddressEditText.getText().toString().trim();
            String endLoc = endAddressEditText.getText().toString().trim();



            boolean isValidate = validation(startOdometer,startOdoEditText,StartOdometerDistanceType,"Start");

            if(isValidate){
                boolean isValidateEndOdo = validation(endOdometer,endOdometerEditTxt,EndOdometerDistanceType,"End");
                if(isValidateEndOdo){

                    if(validationForLocation(startLocation,new String[]{startLocationEditText.getText().toString(), startAddressEditText.getText().toString(), StartSelectedState},"Start")){
                        if(validationForLocation(endLocation,new String[]{endLocationEditText.getText().toString(), endAddressEditText.getText().toString(), EndSelectedState},"End")){
                            if (remarks.trim().length() >= 4 ) {
                                HideKeyboard();
                                if(startOdometer && !startOdo.equals("")) {
                                    if (StartOdometerDistanceType.equals("KM")) {
                                        startOdo = Constants.kmToMeter1(startOdo);
                                    } else if (StartOdometerDistanceType.equals("Miles")) {
                                        startOdo = Constants.milesToMeter(startOdo);
                                    }
                                }

                                if(endOdometer && !endOdo.equals("")) {
                                    if (EndOdometerDistanceType.equals("KM")) {
                                        endOdo = Constants.kmToMeter1(endOdo);
                                    } else if (EndOdometerDistanceType.equals("Miles")) {
                                        endOdo = Constants.milesToMeter(endOdo);
                                    }
                                }
                                startOdo = Constants.getBeforeDecimalValues(startOdo);
                                endOdo = Constants.getBeforeDecimalValues(endOdo);
                                readyListener.JobBtnReady(remarks, IsClaim, IsCompanyAssign,startOdo,endOdo,startLoc,endLoc,startLocationEditText.getText().toString(),StartSelectedState,StartSelectedCountry,endLocationEditText.getText().toString(),EndSelectedState,EndSelectedCountry,startOdometer,endOdometer,startLocation,endLocation);
                            } else {
                                if(isAdverse) {
                                    globally.EldScreenToast(btnLoadingJob, ConstantsEnum.ADVERSE_REASON_ALERT, getContext().getResources().getColor(R.color.red_eld));
                                }else{
                                    if(IsClaim){
                                        globally.EldScreenToast(btnLoadingJob, ConstantsEnum.CLAIM_UNIDENTIFIED_REASON, getContext().getResources().getColor(R.color.red_eld));
                                    }else{
                                        if(IsCompanyAssign) {
                                            globally.EldScreenToast(btnLoadingJob, ConstantsEnum.COMPANY_REJECT_REASON, getContext().getResources().getColor(R.color.red_eld));
                                        }else{
                                            globally.EldScreenToast(btnLoadingJob, ConstantsEnum.REJECT_REASON_ALERT, getContext().getResources().getColor(R.color.red_eld));
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
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


    boolean validation(boolean odometerFiled, EditText textfiledType , String selectedUnit,String locationType){
        if(odometerFiled) {
            if (!textfiledType.getText().toString().equals("")) {
                if (selectedUnit.equals("Select Unit")) {
                    if(locationType.equals("Start")) {
                        globally.EldScreenToast(btnLoadingJob, "Select start odometer unit first.", getContext().getResources().getColor(R.color.red_eld));
                    }else{
                        globally.EldScreenToast(btnLoadingJob, "Select end odometer unit first.", getContext().getResources().getColor(R.color.red_eld));
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }else{
            return true;
        }
    }

    boolean validationForLocation(boolean odometer,String[] editTextArrayList,final String locationType){

        if(odometer) {
            ArrayList<String> locationList = new ArrayList<String>();

            if (!editTextArrayList[0].equals("")) {
                locationList.add(locationType + " City");
            }

            if (!editTextArrayList[1].equals("")) {
                locationList.add(locationType + " Address");
            }

            if (!editTextArrayList[2].equals("Select")) {
                locationList.add(locationType + " State");
            }

            if (locationList.size() == 3) {
                return true;
            } else {
                if (locationList.size() == 0) {
                    return true;
                } else {
                    if(locationType.equals("Start")){
                        globally.EldScreenToast(btnLoadingJob, "Please fill all start location fields", getContext().getResources().getColor(R.color.red_eld));
                    }else{
                        globally.EldScreenToast(btnLoadingJob, "Please fill all end location fields", getContext().getResources().getColor(R.color.red_eld));
                    }

                    return false;
                }
            }

        }else{
            return true;
        }

    }

}

