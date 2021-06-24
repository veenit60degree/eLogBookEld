package com.messaging.logistic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.RequestResponse;
import com.constants.SharedPref;
import com.constants.ShippingPost;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.OdometerHelperMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class OdometerFragment extends Fragment implements View.OnClickListener{

    View rootView;
    TextView EldTitleTV, odometerTruckTV, odometerReadingTitleTV, odometerStartTV, odometerVehicleTitleTV;
    TextView dateActionBarTV, recentStartTv, recentEndTv;
    LinearLayout odometerMainLay, odometerStartLay, endOdometerLay, vehNumberLay;
    RelativeLayout rightMenuBtn, eldMenuLay, recentReadingLay;
    Globally Global;
    RadioGroup odometerRadioGroup;
    RadioButton kmRadionBtn, milesRadionBtn;
    Button cancelReadingBtn, saveReadingBtn, editOdometerBtn, cancelEditViewBtn;
    EditText odometerEditTxt, endOdometerEditTxt;

    String DistanceType = "km",  DRIVER_ID = "", DeviceId = "", VIN_NUMBER = "", SelectedDate = "", CompanyId = "",
            StartOdometer = "", EndOdometer = "", ReadingType = "start";
    long StartOdoInt = 0, EndOdoInt = 0;
    String TruckOdometerId = "", IsEditOdometer = "false";
    final int GetOdometerss         = 1;
    final int SaveOdometers         = 2;
    final int GetOdometers18Days    = 3;
    final int SaveOdometerOffline   = 4;

    boolean IsViewCreated = true, IsOdometerSaved = false;
    JSONObject dataJson;
    ProgressBar odoProgressBar;
    DBHelper dbHelper;
    OdometerHelperMethod odometerhMethod;
    VolleyRequest GetOdometerRequest, SaveOdometerRequest;
    Map<String, String> params;
    JSONArray odometer18DaysArray, odometerArray;
    ShippingPost postRequest;
    Constants constant;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_odometer, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }



    void initView(View view) {

        dbHelper                = new DBHelper(getActivity());
        odometerhMethod         = new OdometerHelperMethod();
        postRequest             = new ShippingPost(getActivity(), requestResponse);
        Global                  = new Globally();
        constant                = new Constants();
        odoProgressBar          = (ProgressBar)view.findViewById(R.id.odoProgressBar);
        EldTitleTV              = (TextView)view.findViewById(R.id.EldTitleTV);
        odometerTruckTV         = (TextView)view.findViewById(R.id.odometerTruckTV);
        odometerReadingTitleTV  = (TextView)view.findViewById(R.id.odometerReadingTitleTV);
        odometerStartTV         = (TextView)view.findViewById(R.id.odometerStartTV);
        odometerVehicleTitleTV  = (TextView)view.findViewById(R.id.odometerVehicleTitleTV);
        dateActionBarTV         = (TextView)view.findViewById(R.id.dateActionBarTV);
        recentStartTv           = (TextView)view.findViewById(R.id.recentStartTv);
        recentEndTv             = (TextView)view.findViewById(R.id.recentEndTv);

        odometerEditTxt         = (EditText)view.findViewById(R.id.odometerEditTxt);
        endOdometerEditTxt      = (EditText)view.findViewById(R.id.endOdometerEditTxt);

        rightMenuBtn            = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        odometerMainLay         = (LinearLayout)view.findViewById(R.id.odometerMainLay);
        odometerStartLay        = (LinearLayout)view.findViewById(R.id.odometerStartLay);
        endOdometerLay          = (LinearLayout)view.findViewById(R.id.endOdometerLay);
        vehNumberLay            = (LinearLayout)view.findViewById(R.id.vehNumberLay);

        recentReadingLay        = (RelativeLayout)view.findViewById(R.id.recentReadingLay);
        eldMenuLay              = (RelativeLayout)view.findViewById(R.id.eldMenuLay);

        odometerRadioGroup      = (RadioGroup)view.findViewById(R.id.odometerRadioGroup);
        kmRadionBtn             = (RadioButton)view.findViewById(R.id.kmRadionBtn);
        milesRadionBtn          = (RadioButton)view.findViewById(R.id.milesRadionBtn);

        cancelReadingBtn        = (Button)view.findViewById(R.id.cancelReadingBtn);
        saveReadingBtn          = (Button)view.findViewById(R.id.saveReadingBtn);
        editOdometerBtn         = (Button)view.findViewById(R.id.editOdometerBtn);
        cancelEditViewBtn       = (Button)view.findViewById(R.id.cancelEditViewBtn);

        GetOdometerRequest      = new VolleyRequest(getActivity());
        SaveOdometerRequest     = new VolleyRequest(getActivity());

        EldTitleTV.setText("Odometer Reading");
        odometerTruckTV.setText(Global.TRUCK_NUMBER);
        rightMenuBtn.setVisibility(View.GONE);
        dateActionBarTV.setVisibility(View.VISIBLE);

        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.whiteee));


        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(7);
        odometerEditTxt.setFilters(FilterArray);
        endOdometerEditTxt.setFilters(FilterArray);



        odometerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.kmRadionBtn) {
                    DistanceType = "km";
                    kmRadionBtn.setTextColor(getResources().getColor(R.color.color_eld_theme));
                    milesRadionBtn.setTextColor(getResources().getColor(R.color.GrayColor));
                }else{
                    DistanceType = "miles";
                    kmRadionBtn.setTextColor(getResources().getColor(R.color.GrayColor));
                    milesRadionBtn.setTextColor(getResources().getColor(R.color.color_eld_theme));
                }
            }
        });

        DRIVER_ID       = SharedPref.getDriverId( getActivity());
        VIN_NUMBER      = SharedPref.getVINNumber( getActivity());

        DeviceId        = SharedPref.GetSavedSystemToken(getActivity());

        UserInfo();
        GetOdometer18Days(DRIVER_ID, DeviceId, CompanyId, SelectedDate);

        dateActionBarTV.setOnClickListener(this);
        odometerMainLay.setOnClickListener(this);
        cancelReadingBtn.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        saveReadingBtn.setOnClickListener(this);
        editOdometerBtn.setOnClickListener(this);
        cancelEditViewBtn.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();

        if(SharedPref.IsAOBRD(getActivity())){
            dateActionBarTV.setText(Html.fromHtml("<b><u>AOBRD</u></b>"));
        }else{
            dateActionBarTV.setText(Html.fromHtml("<b><u>ELD</u></b>"));
        }

        UserInfo();
        odometerTruckTV.setText(Global.TRUCK_NUMBER);
        saveReadingBtn.setEnabled(true);

        odometer18DaysArray = new JSONArray();
        odometer18DaysArray = odometerhMethod.getSavedOdometer18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);

        IsOdometerSaved = false;
        LoadDataOnViews();

        if (Global.isConnected(getActivity())) {
            GetOdometerReading(DRIVER_ID, DeviceId, VIN_NUMBER, SelectedDate);
            UploadSavedOdometerData();

            if(!IsViewCreated) {
                if (odometer18DaysArray == null || odometer18DaysArray.length() == 0)
                    GetOdometer18Days(DRIVER_ID, DeviceId, CompanyId, SelectedDate);
            }
        }else {
            LoadDataOnViews();
        }
        // GetOdometer18Days(DRIVER_ID, DeviceId, CompanyId, SelectedDate);
        IsViewCreated = false;
    }


    void UserInfo(){

        DRIVER_ID       = SharedPref.getDriverId( getActivity());
        VIN_NUMBER      = SharedPref.getVINNumber(getActivity());
        DeviceId        = SharedPref.GetSavedSystemToken(getActivity());

        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            CompanyId     = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
        } else {                                                                                 // If Current driver is Co Driver
            CompanyId     = DriverConst.GetCoDriverDetails(DriverConst.CoCompanyId, getActivity());
        }

        SelectedDate = Global.GetCurrentDeviceDateTime();
    }


    void LogoutUser(){
        if( constant.GetDriverSavedArray(getActivity()).length() == 0) {
            Global.ClearAllFields(getActivity());
            Global.StopService(getActivity());
            Intent i = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(i);
            getActivity().finish();
        }
    }

    void MoveToHomePage(){
        Global.hideSoftKeyboard(getActivity());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TabAct.host.setCurrentTab(0);
            }
        }, 200);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.dateActionBarTV:
                MoveToHomePage();

                break;

            case R.id.odometerMainLay:
                Global.hideSoftKeyboard(getActivity());
                break;

            case R.id.cancelReadingBtn:

                OdometerEditReset();
                MoveToHomePage();
                break;


            case R.id.cancelEditViewBtn:
                editOdometerBtn.performClick();
                break;

            case R.id.editOdometerBtn:

                if(dataJson!= null) {
                    ParseGetOdometerJson(dataJson);
                }

                if(editOdometerBtn.getText().toString().equals("Edit")){
                    OdometerRecentEdit();
                }else{
                    OdometerEditReset();

                }

                break;

            case R.id.saveReadingBtn:

                try {
                    if(constant.isActionAllowed(getContext())) {
                        if (IsEditOdometer.equals("false")) {
                            if (ReadingType.equals("end")) {
                                StartOdometer = "";
                                EndOdometer = odometerEditTxt.getText().toString().trim();
                            } else {

                                StartOdometer = odometerEditTxt.getText().toString().trim();
                                EndOdometer = "";
                            }
                        } else {
                            StartOdometer = odometerEditTxt.getText().toString().trim();
                            EndOdometer = endOdometerEditTxt.getText().toString().trim();
                        }


                        if (odometerEditTxt.getText().toString().trim().length() > 0) {
                            SelectedDate = Global.GetCurrentDeviceDateTime();
                            if (IsEditOdometer.equals("false")) {

                                String manualInputOdometer = odometerEditTxt.getText().toString().trim();
                                if (manualInputOdometer.contains(".")) {
                                    manualInputOdometer = manualInputOdometer.split(".")[0];
                                }

                                if (ReadingType.equals("end")) {

                                    EndOdoInt = Long.parseLong(manualInputOdometer);

                                    if (EndOdoInt > StartOdoInt) {
                                        saveReadingBtn.setEnabled(false);
                                        odoProgressBar.setVisibility(View.VISIBLE);

                                        // Call API with working internet
                                        SAVE_ODOMETER();
                                    } else {
                                        endOdometerEditTxt.requestFocus();
                                        Globally.EldScreenToast(saveReadingBtn, "End Odometer reading should be greater then Start Odometer reading", getResources().getColor(R.color.colorVoilation));
                                    }
                                } else {

                                    StartOdoInt = Long.parseLong(manualInputOdometer);
                                    if (StartOdoInt >= EndOdoInt) {
                                        saveReadingBtn.setEnabled(false);
                                        odoProgressBar.setVisibility(View.VISIBLE);

                                        // Call API with working internet
                                        SAVE_ODOMETER();
                                    } else {
                                        endOdometerEditTxt.requestFocus();
                                        Globally.EldScreenToast(saveReadingBtn, "Start Odometer reading should be greater then previous End Odometer reading", getResources().getColor(R.color.colorVoilation));
                                    }
                                }

                            } else {
                                if (Global.isConnected(getActivity())) {
                                    if (EndOdometer.length() > 0) {
                                        if (Integer.valueOf(EndOdometer) > Integer.valueOf(StartOdometer)) {
                                            saveReadingBtn.setEnabled(false);
                                            odoProgressBar.setVisibility(View.VISIBLE);

                                            // Call API with working internet
                                            SaveOdometerReading(DRIVER_ID, DeviceId, VIN_NUMBER, StartOdometer, EndOdometer, DistanceType,
                                                    SelectedDate, IsEditOdometer, TruckOdometerId);
                                            // SAVE_ODOMETER();
                                        } else {
                                            endOdometerEditTxt.requestFocus();
                                            Globally.EldScreenToast(saveReadingBtn, "End Odometer reading should be greater then Start Odometer reading", getResources().getColor(R.color.colorVoilation));
                                        }
                                    } else {
                                        saveReadingBtn.setEnabled(false);
                                        odoProgressBar.setVisibility(View.VISIBLE);

                                        // Call API with working internet
                                        SaveOdometerReading(DRIVER_ID, DeviceId, VIN_NUMBER, StartOdometer, EndOdometer, DistanceType,
                                                SelectedDate, IsEditOdometer, TruckOdometerId);
                                        // SAVE_ODOMETER();
                                    }
                                } else {
                                    Globally.EldScreenToast(saveReadingBtn, "Edit odometer is only available in online mode.", getResources().getColor(R.color.colorVoilation));
                                }

                            }
                        } else {
                            Globally.EldScreenToast(saveReadingBtn, "Please enter your odometer reading", getResources().getColor(R.color.colorVoilation));
                            odometerEditTxt.requestFocus();
                        }
                    }else{
                        Globally.EldScreenToast(saveReadingBtn, getString(R.string.stop_vehicle_alert),
                                getResources().getColor(R.color.colorVoilation));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;


            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;
        }
    }


    void OdometerEditReset(){

        IsEditOdometer = "false";
        editOdometerBtn.setText("Edit");
        vehNumberLay.setVisibility(View.VISIBLE);
        odometerStartLay.setVisibility(View.GONE);
        endOdometerLay.setVisibility(View.GONE);
        cancelEditViewBtn.setVisibility(View.GONE);
        recentReadingLay.setVisibility(View.VISIBLE);
        saveReadingBtn.setText(getResources().getString(R.string.save));


    }

    void OdometerRecentEdit(){
        IsEditOdometer = "true";
        editOdometerBtn.setText("Cancel");
        odometerEditTxt.setText(StartOdometer);
        endOdometerEditTxt.setText(EndOdometer);
        odometerEditTxt.setSelection(StartOdometer.length());
        endOdometerEditTxt.setSelection(EndOdometer.length());

        recentReadingLay.setVisibility(View.GONE);
        vehNumberLay.setVisibility(View.GONE);
        odometerStartLay.setVisibility(View.GONE);
        endOdometerLay.setVisibility(View.VISIBLE);
        cancelEditViewBtn.setVisibility(View.VISIBLE);
        odometerReadingTitleTV.setText(getResources().getString(R.string.start_ododmeter));
        odometerVehicleTitleTV.setText(getResources().getString(R.string.update_odometer));
        saveReadingBtn.setText(getResources().getString(R.string.update));
    }

    void LoadDataOnViews(){
        odometerArray = odometerhMethod.getSavedOdometerArray(Integer.valueOf(DRIVER_ID), dbHelper);
        JSONObject obj;
        if(odometerArray.length() > 0){
            obj = odometerhMethod.GetLastJsonObject(odometerArray, 1);
            ParseGetOdometerJson(obj);
        }else{
            if(odometer18DaysArray.length() > 0){
                obj = odometerhMethod.GetLastJsonObject(odometer18DaysArray, 1);
                ParseGetOdometerJson(obj);
            }
        }
    }


    void SAVE_ODOMETER() {
        odometerArray = new JSONArray();
        odometerArray = odometerhMethod.getSavedOdometerArray(Integer.valueOf(DRIVER_ID), dbHelper);
        IsOdometerSaved = true;

        JSONObject odoJson = odometerhMethod.AddOdometerInArray(DRIVER_ID, DeviceId, VIN_NUMBER, StartOdometer, EndOdometer, DistanceType,
                SelectedDate, IsEditOdometer, TruckOdometerId, Global.TRUCK_NUMBER, EldFragment.DriverStatusId);

        odometerArray.put(odoJson);
        odometerhMethod.OdometerHelper(Integer.valueOf(DRIVER_ID), dbHelper, odometerArray);
        odometer18DaysArray.put(odoJson);
        odometerhMethod.Odometer18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, odometer18DaysArray);


        if (Global.isConnected(getActivity())) {
            if(IsEditOdometer.equalsIgnoreCase("false")) {
                SharedPref.SetOdoSavingStatus(true, getActivity());
                postRequest.PostListingData(odometerArray, APIs.SAVE_ODOMETER_OFFLINE, SaveOdometerOffline);
            }else{
                SaveOdometerReading(DRIVER_ID, DeviceId, VIN_NUMBER, StartOdometer, EndOdometer, DistanceType,
                        SelectedDate, IsEditOdometer, TruckOdometerId);
            }
        } else {
            if(IsEditOdometer.equalsIgnoreCase("false"))
                PerformOperationsAfterSave(IsOdometerSaved, false);

           /* odoProgressBar.setVisibility(View.GONE);
            saveReadingBtn.setEnabled(true);
            Global.OdometerSaved(true, getActivity());
            Globally.EldScreenToast(saveReadingBtn, Globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
*/
        }
    }

    //*================== Get Odometer Reading ===================*//*
    void GetOdometerReading(final String DriverId, final String DeviceId, final String VIN, final String CreatedDate){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
         params.put(ConstantsKeys.VIN, VIN );
        // params.put("CreatedDate", CreatedDate);
        params.put(ConstantsKeys.IsCertifyLog, "false");

        GetOdometerRequest.executeRequest(Request.Method.POST, APIs.GET_ODOMETER , params, GetOdometerss,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    /*================== Save Odometer Reading ===================*/

    void SaveOdometerReading(final String DriverId, final String DeviceId, final String VIN,
                             final String StartOdometer, final String EndOdometer, final String DistanceType,
                             final String CreatedDate, final String IsEditOdometer, final String TruckOdometerId){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
         params.put(ConstantsKeys.VIN, VIN );
        params.put(ConstantsKeys.StartOdometer, StartOdometer );
        params.put(ConstantsKeys.EndOdometer, EndOdometer );
        params.put(ConstantsKeys.DistanceType, DistanceType );
        params.put(ConstantsKeys.IsEditOdometer, IsEditOdometer );

        if(IsEditOdometer.equals("true")){
            params.put(ConstantsKeys.TruckOdometerId, TruckOdometerId );
        }else{
            params.put(ConstantsKeys.CreatedDate, CreatedDate );
        }

        if(!EldFragment.DriverStatusId.equals("1")) {
            params.put(ConstantsKeys.DriverStatusID, EldFragment.DriverStatusId); // OnDuty, Driving, Sleeper
        }else{
            if(EldFragment.isPersonal.equals("true")){
                params.put(ConstantsKeys.DriverStatusID, "5"); // Personal
            }else{
                params.put(ConstantsKeys.DriverStatusID, EldFragment.DriverStatusId ); // Off Duty
            }
        }

        SaveOdometerRequest.executeRequest(Request.Method.POST, APIs.SAVE_ODOMETER , params, SaveOdometers,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }

    //*================== Get Odometer 18 Days data ===================*//*
    void GetOdometer18Days(final String DriverId, final String DeviceId, final String CompanyId , final String CreatedDate){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.CompanyId, CompanyId  );
        params.put(ConstantsKeys.CreatedDate, CreatedDate);

        GetOdometerRequest.executeRequest(Request.Method.POST, APIs.GET_ODOMETER_OFFLINE , params, GetOdometers18Days,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }

    void UploadSavedOdometerData(){
        try{
            odometerArray = new JSONArray();
            odometerArray = odometerhMethod.getSavedOdometerArray(Integer.valueOf(DRIVER_ID), dbHelper);

            if(odometerArray.length() > 0 ){
                SharedPref.SetOdoSavingStatus(true, getActivity());
                postRequest.PostListingData(odometerArray, APIs.SAVE_ODOMETER_OFFLINE, SaveOdometerOffline );
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    void ParseGetOdometerJson(JSONObject dataJson){
        StartOdometer   = "";
        EndOdometer     = "";
        // odometerEditTxt.setText("");

        try {

            if( dataJson.getString(ConstantsKeys.VIN).equalsIgnoreCase(VIN_NUMBER)) {
                if (!dataJson.getString(ConstantsKeys.StartOdometer).equalsIgnoreCase("null") &&
                        !dataJson.getString(ConstantsKeys.StartOdometer).equalsIgnoreCase("")) {
                    StartOdometer = dataJson.getString(ConstantsKeys.StartOdometer);
                }else{
                    if(odometer18DaysArray.length() > 1){
                        JSONObject obj = odometerhMethod.GetLastJsonObject(odometer18DaysArray, 2);
                        if (!obj.getString(ConstantsKeys.StartOdometer).equalsIgnoreCase("null") &&
                                !obj.getString(ConstantsKeys.StartOdometer).equalsIgnoreCase("")) {
                            if( obj.getString(ConstantsKeys.VIN).equalsIgnoreCase(VIN_NUMBER)) {
                                StartOdometer = obj.getString(ConstantsKeys.StartOdometer);
                            }
                        }
                    }
                }

                if (!dataJson.getString(ConstantsKeys.EndOdometer).equalsIgnoreCase("null")) {
                    EndOdometer = dataJson.getString(ConstantsKeys.EndOdometer);
                }

                if (dataJson.has("TruckOdometerId"))
                    TruckOdometerId = dataJson.getString("TruckOdometerId");

                if (StartOdometer.length() > 0 && EndOdometer.length() == 0) {
                    ReadingType = "end";
                    odometerReadingTitleTV.setText("End Reading : ");
                    odometerVehicleTitleTV.setText("Please Enter End Odometer");
                    odometerStartTV.setText(StartOdometer);
                    odometerStartLay.setVisibility(View.VISIBLE);
                    EldFragment.IsStartReading = true;
                } else {
                    ReadingType = "start";
                    odometerReadingTitleTV.setText("Start Odometer : ");
                    odometerVehicleTitleTV.setText("Please Enter Start Odometer");
                    odometerEditTxt.setText(EndOdometer);
                    odometerEditTxt.setSelection(EndOdometer.length());
                    odometerStartLay.setVisibility(View.GONE);
                    EldFragment.IsStartReading = false;
                }

                if (ReadingType.equals("start") && StartOdometer.length() == 0) {
                    odometerArray = odometerhMethod.getSavedOdometerArray(Integer.valueOf(DRIVER_ID), dbHelper);

                    if (odometerArray.length() > 1) {
                        JSONObject obj = odometerhMethod.GetLastJsonObject(odometerArray, 2);

                        if (obj.getString(ConstantsKeys.TruckEquipmentNumber).equals(Global.TRUCK_NUMBER)) {
                            if (odometerArray.length() > 1) {
                                StartOdometer = obj.getString(ConstantsKeys.StartOdometer);
                            }
                        } else {
                            if (odometer18DaysArray.length() > 1) {
                                JSONObject obj1 = odometerhMethod.GetLastJsonObject(odometer18DaysArray, 1);
                                if (obj1.getString(ConstantsKeys.TruckEquipmentNumber).equals(Global.TRUCK_NUMBER)) {
                                    StartOdometer = obj1.getString(ConstantsKeys.StartOdometer);
                                }
                            }

                        }
                    }
                }

                //  StartOdoInt = 0; EndOdoInt = 0;
                if (StartOdometer.trim().length() > 0) {
                    if (StartOdometer.contains(".")) {
                        StartOdometer = StartOdometer.split(".")[0];
                    }
                    StartOdoInt = Long.parseLong(StartOdometer);
                } else {
                    StartOdoInt = 0;
                    StartOdometer = "";
                }

                if (EndOdometer.trim().length() > 0) {
                    if (EndOdometer.contains(".")) {
                        EndOdometer = EndOdometer.split(".")[0];
                    }
                    EndOdoInt = Long.parseLong(EndOdometer);
                } else {
                    EndOdoInt = 0;
                    EndOdometer = "";
                }
            }else{
                StartOdometer   = "";   StartOdoInt = 0;
                EndOdometer     = "";    EndOdoInt = 0;
            }

            recentStartTv.setText("Start Odo: " + StartOdometer);
            recentEndTv.setText("End Odo: " + EndOdometer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void PerformOperationsAfterSave(boolean SwitchWindow, boolean internetStatus){
        try {
            EldFragment.IsOdometerReading = false;
            SharedPref.OdometerSaved(true, getActivity());
            odoProgressBar.setVisibility(View.GONE);

            if(SwitchWindow) {
                odometerEditTxt.setText("");
                if (internetStatus) {
                    Global.EldScreenToast(saveReadingBtn, "Odometer reading saved.", getResources().getColor(R.color.colorPrimary));
                }else{
                    Global.EldScreenToast(saveReadingBtn, "Odometer reading saved.", getResources().getColor(R.color.colorSleeper));
                }
                SharedPref.setVINNumber(VIN_NUMBER, getActivity());

                if (ReadingType.equals("end")) {
                    EldFragment.IsStartReading  = false;
                } else {
                    EldFragment.IsStartReading = true;
                }

                if(IsEditOdometer.equals("true")) {
                    editOdometerBtn.performClick();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveReadingBtn.setEnabled(true);
                        MoveToHomePage();
                    }
                }, 1100);
            }else{
                saveReadingBtn.setEnabled(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            saveReadingBtn.setEnabled(true);
        }

    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null;  //, dataObj = null;
            String status = "", message = "";

            odoProgressBar.setVisibility(View.GONE);

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");
                message = obj.getString("Message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status.equalsIgnoreCase("true")) {
                switch (flag) {
                    case SaveOdometers:
                        // CAll 18 Days API to update list properly
                        GetOdometer18Days(DRIVER_ID, DeviceId, CompanyId, SelectedDate);
                        IsOdometerSaved = true;
                        PerformOperationsAfterSave(IsOdometerSaved, true);
                        break;

                    case GetOdometerss:
                        try {
                            saveReadingBtn.setEnabled(true);
                            //  Log.d("response", "response Get: " + response);
                            dataJson = new JSONObject(obj.getString("Data"));
                            ParseGetOdometerJson(dataJson);

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        break;


                    case GetOdometers18Days:
                        try {
                            JSONArray resultArray = new JSONArray(obj.getString("Data"));
                            odometerhMethod.Odometer18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, resultArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }else{
                saveReadingBtn.setEnabled(true);
                Globally.EldScreenToast(saveReadingBtn, message , getResources().getColor(R.color.colorVoilation));
                if(!obj.isNull("Message")){
                    try {
                        if(obj.getString("Message").equals("Device Logout") && EldFragment.DriverJsonArray.length() == 0){
                            LogoutUser();
                        }else {
                            MoveToHomePage();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };




    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {

            if(flag == SaveOdometers){
                Globally.EldScreenToast(saveReadingBtn, "Failed !. Please try again." , getResources().getColor(R.color.colorVoilation));
            }else if(flag == GetOdometerss){
                LoadDataOnViews();
            }
            odoProgressBar.setVisibility(View.GONE);
            saveReadingBtn.setEnabled(true);
            Log.d("Driver", "error" + error.toString());
        }
    };




    RequestResponse requestResponse = new RequestResponse() {
        @Override
        public void onApiResponse(String response, int flag) {

            switch (flag) {

                case SaveOdometerOffline:
                    Log.d("response", "response Save: " + response);
                    SharedPref.SetOdoSavingStatus(false, getActivity());

                    // Remove saved data from array after post
                    odometerhMethod.OdometerHelper(Integer.valueOf(DRIVER_ID), dbHelper, new JSONArray());

                    // CAll 18 Days API to update list properly
                    GetOdometer18Days(DRIVER_ID, DeviceId, CompanyId, SelectedDate);
                    odoProgressBar.setVisibility(View.GONE);
                    PerformOperationsAfterSave(IsOdometerSaved, true);
                    break;

            }
        }

        @Override
        public void onResponseError(String error, int flag) {
            Log.d("shipmentJsonArray ", ">>>Error");
            SharedPref.SetOdoSavingStatus(false, getActivity());
            odoProgressBar.setVisibility(View.GONE);
            PerformOperationsAfterSave(IsOdometerSaved, false);
        }
    };




}
