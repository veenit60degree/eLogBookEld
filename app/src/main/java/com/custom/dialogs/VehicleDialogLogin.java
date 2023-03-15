package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.als.logistic.TabAct;
import com.als.logistic.UILApplication;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.androidtrip.plugins.searchablespinner.SearchableSpinner;
import com.androidtrip.plugins.searchablespinner.interfaces.IStatusListener;
import com.androidtrip.plugins.searchablespinner.interfaces.OnItemSelectedListener;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.models.VehicleModel;
import com.searchable.spinner.SearchArrayListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VehicleDialogLogin extends Dialog implements View.OnClickListener{

    public interface VehicleLoginListener {
        public void ChangeVehicleReady(String Title, int position, View ButtonView, boolean isOldDialog);

        public void ContinueBtnReady(boolean isUpdate, String CoDriverId, String CompanyId, int position, boolean isOldDialog);
    }

    List<VehicleModel> truckList;
    private VehicleLoginListener readyListener;
    Button saveBtnJob;
    SearchableSpinner searchableSpinner;
    private SearchArrayListAdapter mSimpleArrayListAdapter;

    String NoTruckDesc = "You don't have any truck. Please contact to your company.";
    String NoTruckAvailable = "No truck available. Please contact with your company to continue.";
    String Title = "";
    String Truck, CoDriverId = "", CompanyId = "", DeviceId = "", DriverId = "", VIN = "";
    TextView TitleTV, refreshVehTV, logoutTruckPopupTV;
    int SelectedPosition = -1;  //, SetSpinnerPosition = 0;
    boolean isContinue = false;
    boolean isOldDialog;
    Activity activity;
    Constants constant;
    VolleyRequest LogoutRequest, GetOBDVehRequest;
    Map<String, String> params;
    ProgressDialog progressD ;
    Context mContext;
    Globally global;
    Constants constants;
    ProgressBar pBarTruckSlctDialog;


    public VehicleDialogLogin(Context context, Activity act, String truck, boolean isOldDialog,
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

        setContentView(R.layout.popup_vehicle_list);
        setCancelable(false);
        global = new Globally();
        constants = new Constants();

        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            if (global.isTablet(getContext())) {
                lp.width = constants.intToPixel(getContext(), 650);
            } else {
                lp.width = constants.intToPixel(getContext(), 550);
            }
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            getWindow().setAttributes(lp);

        }catch (Exception e){
            e.printStackTrace();
        }


        LogoutRequest       = new VolleyRequest(getContext());
        GetOBDVehRequest    = new VolleyRequest(getContext());

        constant            = new Constants();

        saveBtnJob = (Button) findViewById(R.id.btnSaveVehList);
        TitleTV = (TextView) findViewById(R.id.TitleVehTV);
        logoutTruckPopupTV = (TextView) findViewById(R.id.logoutVehTV);
        refreshVehTV        = (TextView) findViewById(R.id.refreshVehTV);
        searchableSpinner = (SearchableSpinner) findViewById(R.id.searchableSpinner);
        pBarTruckSlctDialog = (ProgressBar) findViewById(R.id.pBarTruckSlctDialog);

        DeviceId        = SharedPref.GetSavedSystemToken(getContext());
        DriverId        = SharedPref.getDriverId( getContext());
        CompanyId       = DriverConst.GetDriverDetails(DriverConst.CompanyId, getContext());
        VIN             = SharedPref.getVINNumber(getContext());

        refreshVehTV.setVisibility(View.VISIBLE);

        if(UILApplication.getInstance().isNightModeEnabled()){
            logoutTruckPopupTV.setText(Html.fromHtml("<font color='white'><u>Logout</u></font>"));
            refreshVehTV.setText(Html.fromHtml("<font color='white'><u>Refresh</u></font>"));

        } else {
            logoutTruckPopupTV.setText(Html.fromHtml("<font color='blue'><u>Logout</u></font>"));
            refreshVehTV.setText(Html.fromHtml("<font color='blue'><u>Refresh</u></font>"));
        }

        progressD = new ProgressDialog(getContext());
        progressD.setMessage("Loading ...");
        progressD.setCancelable(false);
        saveBtnJob.setBackgroundResource(R.drawable.gray_selector);

        getTruckList(truckList);


        LinearLayout loginTruckLay = (LinearLayout)findViewById(R.id.loginTruckLay);

        loginTruckLay.setOnClickListener(this);
        logoutTruckPopupTV.setOnClickListener(this);
        refreshVehTV.setOnClickListener(this);

        saveBtnJob.setOnClickListener(new ContinueBtnListener());

        HideKeyboard();

    }



    private void getTruckList(List<VehicleModel> truckList){
        if (truckList.size() < 1) {
            saveBtnJob.setText("Ok");
        } else {
            searchableSpinner.setVisibility(View.VISIBLE);
        }

        Title = "Please select the truck from list.";
        TitleTV.setText(Title);
        if (truckList.size() > 0) {
            ArrayList<String> EquipmentList = new ArrayList<String>();
            for (int i = 0; i < truckList.size(); i++) {
                EquipmentList.add(truckList.get(i).getEquipmentNumber());
            }

            // Creating adapter for spinner
            if (EquipmentList.size() > 0) {
                mSimpleArrayListAdapter = new SearchArrayListAdapter(getContext(), EquipmentList);
                searchableSpinner.setAdapter(mSimpleArrayListAdapter);

                searchableSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
                searchableSpinner.setStatusListener(new IStatusListener() {
                    @Override
                    public void spinnerIsOpening() {
                        // Logger.LogDebug("spinnerIsOpening", "spinnerIsOpening" );

                    }

                    @Override
                    public void spinnerIsClosing() {
                        //Logger.LogDebug("spinnerIsClosing", "spinnerIsClosing" );
                    }
                });

            } else {
                if (Truck.trim().length() == 0) {
                    Title = NoTruckAvailable;
                    TitleTV.setText(NoTruckAvailable);
                }
                searchableSpinner.setVisibility(View.GONE);
                saveBtnJob.setText("Ok");
            }
        }else{
            Title = NoTruckDesc;
            TitleTV.setText(NoTruckDesc);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.refreshVehTV:
                if (global.isConnected(getContext())) {
                    GetOBDAssignedVehicles(DriverId, DeviceId, CompanyId, VIN);
                }else{
                    global.EldScreenToast(logoutTruckPopupTV, global.CHECK_INTERNET_MSG, getContext().getResources().getColor(R.color.colorSleeper));
                }
                break;

            case R.id.logoutVehTV:
                if(SharedPref.getDriverId(getContext()).length() > 0) {
                    if (global.isConnected(getContext())) {
                        LogoutUser(SharedPref.getDriverId(getContext()));
                    } else {
                        global.EldScreenToast(logoutTruckPopupTV, global.CHECK_INTERNET_MSG, getContext().getResources().getColor(R.color.colorSleeper));
                    }
                }else{
                    LogoutUser();
                    activity.finish();
                }
                break;

            case R.id.loginTruckLay:
                HideKeyboard();
                searchableSpinner.hideEdit();
                break;

        }
    }


    private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
            // Logger.LogDebug("onItemSelected", "onItemSelected: " + position);
            SelectedPosition = position;

            Object object = searchableSpinner.getSelectedItem();
            if(object != null) {
                for (int i = 0; i < truckList.size(); i++) {
                    if (object.toString().equals(truckList.get(i).getEquipmentNumber())) {
                        SelectedPosition = i;
                        break;
                    }
                }
                saveBtnJob.setBackgroundResource(R.drawable.green_selector);
            }else{
                SelectedPosition = -1;
                saveBtnJob.setBackgroundResource(R.drawable.gray_selector);
            }

        }

        @Override
        public void onNothingSelected() {
            // Logger.LogDebug("onNothingSelected", "onNothingSelected" );
            saveBtnJob.setBackgroundResource(R.drawable.gray_selector);
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        searchableSpinner.hideEdit();

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
                global.ClearAllFields(getContext());
                global.StopService(getContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ContinueBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (saveBtnJob.getText().toString().equals("Ok")) {
                dismiss();
                LogoutUser();
                activity.finish();
            } else {

                if (SelectedPosition >= 0) {
                    readyListener.ChangeVehicleReady(Title, SelectedPosition, saveBtnJob, isOldDialog);
                } else {
                    global.EldScreenToast(saveBtnJob, "Please select truck to save.", getContext().getResources().getColor(R.color.colorVoilation));
                }
            }
        }
    }


    private class ChangeFieldListener implements View.OnClickListener {
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


    /*================== Get OBD Assigned Vehicles ===================*/
    void GetOBDAssignedVehicles(final String DriverId, final String DeviceId, final String CompanyId, final String VIN) {


        pBarTruckSlctDialog.setVisibility(View.VISIBLE);

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.VIN, VIN);

        GetOBDVehRequest.executeRequest(Request.Method.POST, APIs.GET_OBD_ASSIGNED_VEHICLES, params, ConstantsEnum.GetObdAssignedVeh,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Logout User request ===================*//*
    void LogoutUser(final String DriverId){
        progressD.show();

        String date = global.GetDriverCurrentDateTime(global, getContext());
        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.MobileDeviceCurrentDateTime, date);
        params.put(ConstantsKeys.CompanyId, DriverConst.GetDriverDetails(DriverConst.CompanyId, getContext()));
        params.put(ConstantsKeys.LocationType, SharedPref.getLocationEventType(getContext()));

        params.put(ConstantsKeys.Latitude,  Globally.LATITUDE);
        params.put(ConstantsKeys.Longitude, Globally.LONGITUDE);


        Logger.LogDebug("DateLogout", "MobileDeviceCurrentDateTime: " +date);

        LogoutRequest.executeRequest(Request.Method.POST, APIs.DRIVER_LOGOUT , params, 1,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }

    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            progressD.dismiss();
            Logger.LogDebug("response", "VehDiaLogin logout response: " + response);
            String status = "", Message = "";

            try {
                JSONObject obj = new JSONObject(response);
                status = obj.getString("Status");
                Message = obj.getString("Message");

                if(status.equalsIgnoreCase("true")){

                    if(flag == ConstantsEnum.GetObdAssignedVeh) {

                        try {
                            if (pBarTruckSlctDialog != null) {
                                pBarTruckSlctDialog.setVisibility(View.GONE);
                            }

                            TabAct.vehicleList = new ArrayList<VehicleModel>();
                            truckList = new ArrayList<VehicleModel>();

                            JSONArray vehicleJsonArray = new JSONArray(obj.getString(ConstantsKeys.Data));

                            for (int i = 0; i < vehicleJsonArray.length(); i++) {
                                JSONObject resultJson = (JSONObject) vehicleJsonArray.get(i);
                                VehicleModel vehicleModel = new VehicleModel(
                                        resultJson.getString("VehicleId"),
                                        resultJson.getString("EquipmentNumber"),
                                        resultJson.getString("PlateNumber"),
                                        resultJson.getString("VIN"),
                                        resultJson.getString("PreviousDeviceMappingId"),
                                        resultJson.getString("DeviceMappingId"),
                                        resultJson.getString("CompanyId")
                                );
                                TabAct.vehicleList.add(vehicleModel);
                                truckList.add(vehicleModel);
                            }

                            getTruckList(truckList);

                            if(vehicleJsonArray.length() == 0){
                                global.EldScreenToast(logoutTruckPopupTV, NoTruckDesc, getContext().getResources().getColor(R.color.colorSleeper));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        constant.ClearLogoutData(mContext);
                        dismiss();
                    }


                }else{
                    if(Message.equals("Device Logout")) {
                        constant.ClearLogoutData(mContext);
                        dismiss();
                    }else{
                        if(flag == ConstantsEnum.GetObdAssignedVeh) {
                            global.EldScreenToast(logoutTruckPopupTV, Message, getContext().getResources().getColor(R.color.colorSleeper));
                        }
                    }
                }



            }catch(Exception e){  }

        }
    };

    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

        @Override
        public void getError(VolleyError error, int flag) {
            Logger.LogDebug("onDuty error", "onDuty error: " + error.toString());
            try {
                global.EldScreenToast(logoutTruckPopupTV, Globally.DisplayErrorMessage(error.toString()), getContext().getResources().getColor(R.color.red_eld));

                if (progressD != null)
                    progressD.dismiss();

                if (pBarTruckSlctDialog != null)
                    pBarTruckSlctDialog.setVisibility(View.GONE);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };



}