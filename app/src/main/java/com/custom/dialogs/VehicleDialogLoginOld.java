package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.VehicleModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleDialogLoginOld extends Dialog {

    public interface VehicleLoginListener {
        public void ChangeVehicleReady(String Title, int position, View ButtonView, boolean isOldDialog);

        public void ContinueBtnReady(boolean isUpdate, String CoDriverId, String CompanyId, int position, boolean isOldDialog);
    }

    List<VehicleModel> truckList;
    private VehicleLoginListener readyListener;
    EditText TrailorNoEditText;
    Button ContinueBtnJob, ChangeBtnJob;
    Spinner remarkSpinner;
    String Truck, Title = "", CoDriverId = "", CompanyId = "";
    TextView TitleTV, SpinnerTitleTV, logoutTruckPopupTV;
    int SelectedPosition = 0;  //, SetSpinnerPosition = 0;
    boolean isContinue = false;
    boolean isOldDialog;
    Activity activity;
    Constants constant;
    VolleyRequest LogoutRequest;
    Map<String, String> params;
    ProgressDialog progressD ;
    Context mContext;

    public VehicleDialogLoginOld(Context context, Activity act, String truck, boolean isOldDialog,
                                 List<VehicleModel> remarkList, VehicleLoginListener readyListener) {
        super(context);
        activity = act;
        Truck = truck;
        this.isOldDialog = isOldDialog;
        this.truckList = remarkList;
        this.readyListener = readyListener;
        this.mContext= context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_trailor_fields);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        LogoutRequest   = new VolleyRequest(getContext());
        constant        = new Constants();
        TrailorNoEditText = (EditText) findViewById(R.id.TrailorNoEditText);
        TrailorNoEditText.setVisibility(View.GONE);

        ContinueBtnJob = (Button) findViewById(R.id.btnLoadingJob);
        ChangeBtnJob = (Button) findViewById(R.id.btnCancelLoadingJob);

        TitleTV = (TextView) findViewById(R.id.TitleTV);
        SpinnerTitleTV = (TextView) findViewById(R.id.SpinnerTitleTV);
        logoutTruckPopupTV = (TextView) findViewById(R.id.logoutTruckPopupTV);

        remarkSpinner = (Spinner) findViewById(R.id.remarkSpinner);

        logoutTruckPopupTV.setText(Html.fromHtml("<font color='blue'><u>Logout</u></font>"));
        ChangeBtnJob.setText("Save");

        logoutTruckPopupTV.setVisibility(View.VISIBLE);
        ContinueBtnJob.setVisibility(View.GONE); //BackgroundResource(R.drawable.green_selector);
        ChangeBtnJob.setBackgroundResource(R.drawable.gray_selector);

        progressD = new ProgressDialog(getContext());
        progressD.setMessage("Loading ...");
        progressD.setCancelable(false);

        if (truckList.size() <= 1) {
            ChangeBtnJob.setText("Ok");
        } else {
            remarkSpinner.setVisibility(View.VISIBLE);
            // SpinnerTitleTV.setVisibility(View.VISIBLE);
            //  SpinnerTitleTV.setText("Select truck from list to change.");
        }

        if (Truck.trim().length() > 0) {
            Title = "Please select truck from list.";
        } else {
            Title = "You haven't selected any truck for now. Please select a truck first.";
        }

        TitleTV.setText(Title);
        if (truckList.size() > 1) {
            ArrayList<String> EquipmentList = new ArrayList<String>();
            for (int i = 0; i < truckList.size(); i++) {
                EquipmentList.add(truckList.get(i).getEquipmentNumber());
            }

            // Creating adapter for spinner
            if (EquipmentList.size() > 1) {
                ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, EquipmentList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                remarkSpinner.setAdapter(dataAdapter);
            } else {
                if (Truck.trim().length() == 0) {
                    Title = "No truck available. Please contact with your company to continue.";
                }
                remarkSpinner.setVisibility(View.GONE);
                ChangeBtnJob.setText("Ok");
            }
        }else{
            Title = "You don't have any truck. Please contact to your company.";
            TitleTV.setText(Title);
        }




        // Spinner click listener
        remarkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedPosition = position;

                if(position == 0){
                    ChangeBtnJob.setBackgroundResource(R.drawable.gray_selector);
                }else{
                    ChangeBtnJob.setBackgroundResource(R.drawable.green_selector);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        logoutTruckPopupTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SharedPref.getDriverId(getContext()).length() > 0) {
                    if (Globally.isConnected(getContext())) {
                        LogoutUser(SharedPref.getDriverId(getContext()));
                    } else {
                        Globally.EldScreenToast(logoutTruckPopupTV, Globally.CHECK_INTERNET_MSG, getContext().getResources().getColor(R.color.colorSleeper));
                    }
                }else{
                    LogoutUser();
                    activity.finish();
                }
            }
        });

        ChangeBtnJob.setOnClickListener(new ChangeFieldListener());
        ContinueBtnJob.setOnClickListener(new ContinueBtnListener());

        HideKeyboard();
    }


    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    void LogoutUser() {
        try {
            if (constant.GetDriverSavedArray(getContext()).length() == 0) {
                Globally.ClearAllFields(getContext());
                Globally.StopService(getContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ChangeFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (ChangeBtnJob.getText().toString().equals("Ok")) {
                dismiss();
                LogoutUser();
                activity.finish();
            } else {
                //if (SelectedPosition > 0) {
                    readyListener.ChangeVehicleReady(Title, SelectedPosition, ChangeBtnJob, isOldDialog);
                /*} else {
                    Globally.EldScreenToast(ChangeBtnJob, "Please select truck to save.", getContext().getResources().getColor(R.color.colorVoilation));
                }*/
            }
        }
    }


    private class ContinueBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isContinue) {
                for (int i = 0; i < truckList.size(); i++) {
                    if (Truck.equals(truckList.get(i).getEquipmentNumber())) {
                        SelectedPosition = i;
                    }
                }
            }
            readyListener.ContinueBtnReady(isContinue, CoDriverId, CompanyId, SelectedPosition, isOldDialog);
        }
    }



    //*================== Logout User request ===================*//*
    void LogoutUser(final String DriverId){
        progressD.show();
        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);

        LogoutRequest.executeRequest(Request.Method.POST, APIs.DRIVER_LOGOUT , params, 1,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }

    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            progressD.dismiss();
            Logger.LogDebug("response", " logout response: " + response);
            String status = "";

            try {
                Globally.obj = new JSONObject(response);
                status = Globally.obj.getString("Status");

                if(status.equalsIgnoreCase("true")){

                    constant.ClearLogoutData(mContext);
                    dismiss();

                }else{
                    if(Globally.obj.getString("Message").equals("Device Logout")) {

                        constant.ClearLogoutData(mContext);
                        dismiss();

                    }
                }

            }catch(Exception e){  }

        }
    };

    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

        @Override
        public void getError(VolleyError error, int flag) {
            Logger.LogDebug("onDuty error", "onDuty error: " + error.toString());
            progressD.dismiss();
            Globally.EldScreenToast(logoutTruckPopupTV, Globally.DisplayErrorMessage(error.toString()), getContext().getResources().getColor(R.color.red_eld));

        }
    };

}