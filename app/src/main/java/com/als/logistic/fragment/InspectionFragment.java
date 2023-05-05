package com.als.logistic.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.logistic.GridAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.CsvReader;
import com.constants.DriverLogResponse;
import com.constants.Logger;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.Slidingmenufunctions;
import com.constants.Utils;
import com.constants.VolleyRequest;
import com.custom.dialogs.PtiSignDialog;
import com.custom.dialogs.SignDialog;
import com.custom.dialogs.TrailorDialog;
import com.custom.dialogs.VehicleDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.FailedApiTrackMethod;
import com.local.db.HelperMethods;
import com.local.db.InspectionMethod;
import com.local.db.ShipmentHelperMethod;
import com.als.logistic.Globally;
import com.als.logistic.LoginActivity;
import com.als.logistic.R;
import com.als.logistic.TabAct;
import com.als.logistic.UILApplication;
import com.models.DriverLocationModel;
import com.models.PrePostModel;
import com.models.VehicleModel;
import com.shared.pref.StatePrefManager;
import com.simplify.ink.InkView;
import com.wifi.settings.WiFiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InspectionFragment extends Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    View rootView;
    boolean isChecked, IsClicked, IsAOBRD = false, IsAOBRDAutomatic = false, isOnCreate = true, isLocationChange;
    boolean IsSignCopy = false;
    CheckBox checkboxTrailer, checkboxTruck;
    GridView truckGridView, trailerGridView;
    GridAdapter truckAdapter, trailerAdapter;
    List<PrePostModel> TruckInspList = new ArrayList<PrePostModel>();
    List<PrePostModel> TrailerInspList = new ArrayList<PrePostModel>();
    ArrayList<String> TruckList = new ArrayList<String>();
    ArrayList<String> TrailerList = new ArrayList<String>();
    ArrayList<Integer> TruckIdList = new ArrayList<Integer>();
    ArrayList<Integer> TrailerIdList = new ArrayList<Integer>();
    ArrayList<String> DistanceTypeList;

    TextView dateActionBarTV, EldTitleTV, inspectionDateTv, powerInspectionTV, noDefectLabel, locInspTitleTV, trailerTextVw, currentOdometerTV;
    public static TextView trailerInspectionTV;
    EditText remarksEditText, SupervisorNameTV, cityEditText, odometerEditTxt;
    AutoCompleteTextView locInspectionTV;
    Spinner stateInspSpinner, selectDistanceSpinner;
    String EldThemeColor = "#1A3561";
    String BlackColor    = "#7C7C7B";
    String WhiteColor    = "#ffffff";
    String RadioDarkColor   = "#444366";
    RadioGroup prePostRadioGroup, correctRadioGroup;
    RadioButton preTripButton, postTripButton, DefectsCorrectedBtn, DefectsNotCorrectedBtn;
    LinearLayout supervisorNameLay, AobrdLocLay;
    LinearLayout inspectionTrailerLay, inspectionTruckLay, insptnMainLay;
    RelativeLayout rightMenuBtn, eldMenuLay, superviserSignLay, DriverSignLay, truckTrailerTVLay, truckTrailerLayout;
    RelativeLayout inspectTrailerTitleLay, trailerGridLay;
    Button changeLocBtn, saveInspectionBtn;
    ImageView signSuprvsrIV, signDriverIV;
    ProgressBar inspectionProgressBar;
    String btnSelectedType = "", SignImageSelected = "", SupervisorSignImage = "", DriverSignImage = "", Odometer = "", OdometerDistanceType = "";
    String DRIVER_ID = "", VIN_NUMBER = "", VehicleId = "", DeviceId = "";
    String DriverName = "",CompanyId = "", InspectionDateTime = "", Location = "", PreTripInsp = "false", PostTripInsp = "false",
            AboveDefectsCorrected = "false", AboveDefectsNotCorrected = "false", Remarks = "",  //Latitude = "", Longitude = "",
            SupervisorMechanicsName = "", TruckIssueType = "", TraiorIssueType = "", DriverTimeZone = "", date = "", InspectionTypeId="";
    String City = "", State = "", Country = "", CurrentCycleId = "", CurrentJobStatus = "";
    String DriverId = "", CoDriverId = "", tempTrailer = "", CreatedDate = "";
    String ByteDriverSign = "", ByteSupervisorSign = "", SelectedDatee = "";
    String TruckNumber = "", TrailerNumber= "", SignCopyDate = "";
    SignDialog signDialog;
    ScrollView inspectionScrollView;
    TextView inspectionTypeTV;
    List<String> StateArrayList;
    List<DriverLocationModel> StateList;
    StatePrefManager statePrefManager;
    TrailorDialog dialog;
    VehicleDialog vehicleDialog;
    List<VehicleModel>  vehicleList = new ArrayList<VehicleModel>();
    int VehListPosition = 0;
    final int SaveTrailer = 1, GetInspection = 2, GetObdAssignedVeh = 3, UpdateObdVeh = 4, GetAddFromLatLng = 5;
    VolleyRequest SaveTrailerNumber, GetInspectionRequest, GetOBDVehRequest, SaveOBDVehRequest, GetAddFromLatLngRequest;
    Map<String, String> params;
    ProgressDialog pDialog;
    Slidingmenufunctions slideMenu;
    DBHelper dbHelper;
    HelperMethods hMethods;
    InspectionMethod inspectionMethod;
    ShipmentHelperMethod shipmentHelperMethod;
    JSONArray driverLogArray, inspection18DaysArray;
    JSONArray inspectionUnPostedArray = new JSONArray();
    SaveDriverLogPost saveInspectionPost;
    FailedApiTrackMethod failedApiTrackMethod;
    Constants constants;
    CsvReader csvReader;
    Utils obdUtil;
    DriverPermissionMethod driverPermissionMethod;
    PtiSignDialog ptiSignDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(UILApplication.getInstance().isNightModeEnabled()){
            getActivity().setTheme(R.style.DarkTheme);
        } else {
            getActivity().setTheme(R.style.LightTheme);
        }

        rootView = inflater.inflate(R.layout.inspection_fragment, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }



    void initView(View view) {

        csvReader = new CsvReader();
        constants = new Constants();
        hMethods = new HelperMethods();
        inspectionMethod = new InspectionMethod();
        shipmentHelperMethod = new ShipmentHelperMethod();
        driverPermissionMethod = new DriverPermissionMethod();
        dbHelper = new DBHelper(getActivity());
        SaveTrailerNumber = new VolleyRequest(getActivity());
        GetInspectionRequest = new VolleyRequest(getActivity());
        GetOBDVehRequest = new VolleyRequest(getActivity());
        SaveOBDVehRequest = new VolleyRequest(getActivity());
        GetAddFromLatLngRequest = new VolleyRequest(getActivity());
        saveInspectionPost = new SaveDriverLogPost(getActivity(), saveInspectionResponse);

        try{
            obdUtil         = new Utils(getActivity());
            obdUtil.createLogFile();
        }catch (Exception e){
            e.printStackTrace();
        }

        failedApiTrackMethod = new FailedApiTrackMethod();
        pDialog = new ProgressDialog(getActivity());

        pDialog.setMessage("Saving ...");

        slideMenu = new Slidingmenufunctions();
        statePrefManager = new StatePrefManager();

        inspectionProgressBar = (ProgressBar) view.findViewById(R.id.inspectionProgressBar);
        truckGridView = (GridView) view.findViewById(R.id.truckGridView);
        trailerGridView = (GridView) view.findViewById(R.id.trailerGridView);

        inspectionTypeTV = (TextView) view.findViewById(R.id.inspectionTypeTV);
        eldMenuLay = (RelativeLayout) view.findViewById(R.id.eldMenuLay);
        superviserSignLay = (RelativeLayout) view.findViewById(R.id.superviserSignLay);
        DriverSignLay = (RelativeLayout) view.findViewById(R.id.DriverSignLay);
        truckTrailerTVLay = (RelativeLayout) view.findViewById(R.id.truckTrailerTVLay);
        truckTrailerLayout = (RelativeLayout) view.findViewById(R.id.truckTrailerLayout);
        inspectTrailerTitleLay = (RelativeLayout) view.findViewById(R.id.inspectTrailerTitleLay);
        trailerGridLay = (RelativeLayout) view.findViewById(R.id.trailerGridLay);

        rightMenuBtn = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        supervisorNameLay = (LinearLayout) view.findViewById(R.id.supervisorNameLay);
        AobrdLocLay = (LinearLayout) view.findViewById(R.id.AobrdLocLay);
        inspectionTrailerLay = (LinearLayout) view.findViewById(R.id.inspectionTrailerLay);
        inspectionTruckLay = (LinearLayout) view.findViewById(R.id.inspectionTruckLay);
        insptnMainLay       = (LinearLayout)view.findViewById(R.id.insptnMainLay);

        prePostRadioGroup = (RadioGroup) view.findViewById(R.id.prePostRadioGroup);
        correctRadioGroup = (RadioGroup) view.findViewById(R.id.correctRadioGroup);
        preTripButton = (RadioButton) view.findViewById(R.id.preTripButton);
        postTripButton = (RadioButton) view.findViewById(R.id.postTripButton);
        DefectsCorrectedBtn = (RadioButton) view.findViewById(R.id.DefectsCorrectedBtn);
        DefectsNotCorrectedBtn = (RadioButton) view.findViewById(R.id.DefectsNotCorrectedBtn);


        changeLocBtn = (Button) view.findViewById(R.id.changeLocBtn);
        saveInspectionBtn = (Button) view.findViewById(R.id.saveInspectionBtn);

        signSuprvsrIV = (ImageView) view.findViewById(R.id.signSuprvsrIV);
        signDriverIV = (ImageView) view.findViewById(R.id.signDriverIV);

        locInspectionTV = (AutoCompleteTextView) view.findViewById(R.id.locInspectionTV);
        remarksEditText = (EditText) view.findViewById(R.id.remarksEditText);
        SupervisorNameTV = (EditText) view.findViewById(R.id.SupervisorNameTV);
        cityEditText = (EditText) view.findViewById(R.id.cityEditText);
        odometerEditTxt= (EditText) view.findViewById(R.id.odometerEditTxt);

        trailerTextVw = (TextView) view.findViewById(R.id.trailerTextVw);
        trailerInspectionTV = (TextView) view.findViewById(R.id.trailerInspectionTV);
        dateActionBarTV = (TextView) view.findViewById(R.id.dateActionBarTV);
        EldTitleTV = (TextView) view.findViewById(R.id.EldTitleTV);
        inspectionDateTv = (TextView) view.findViewById(R.id.inspectionDateTv);
        powerInspectionTV = (TextView) view.findViewById(R.id.powerInspectionTV);
        noDefectLabel = (TextView) view.findViewById(R.id.noDefectLabel);
        locInspTitleTV = (TextView) view.findViewById(R.id.locInspTitleTV);
        currentOdometerTV = (TextView) view.findViewById(R.id.currentOdometerTV);

        checkboxTrailer = (CheckBox) view.findViewById(R.id.checkboxTrailer);
        checkboxTruck = (CheckBox) view.findViewById(R.id.checkboxTruck);

        stateInspSpinner = (Spinner) view.findViewById(R.id.stateInspSpinner);
        selectDistanceSpinner = (Spinner) view.findViewById(R.id.selectDistanceSpinner);

        inspectionScrollView = (ScrollView) view.findViewById(R.id.inspectionScrollView);

        dateActionBarTV.setVisibility(View.VISIBLE);
        rightMenuBtn.setVisibility(View.GONE);

        dateActionBarTV.setText(Html.fromHtml("<b><u>" + getResources().getString(R.string.view_inspections) + "</u></b>"));
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.whiteee));

        locInspectionTV.setThreshold(3);
        SelectedDatee = Globally.GetCurrentDeviceDate(null, new Globally(), getActivity());

        AddStatesInList();
        // Spinner click listener
        stateInspSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position < StateList.size()) {
                    State = StateList.get(position).getStateCode();
                    Country = StateList.get(position).getCountry();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        try{
            JSONArray TruckArray        = new JSONArray(SharedPref.getInspectionIssues(ConstantsKeys.TruckIssues, getActivity()));
            JSONArray TrailerArray      = new JSONArray(SharedPref.getInspectionIssues(ConstantsKeys.TrailerIssues, getActivity()));

            ParseInspectionIssues(TruckArray, TrailerArray);

        }catch (Exception e){
            e.printStackTrace();
        }

        DRIVER_ID           = SharedPref.getDriverId( getActivity());
        DeviceId            = SharedPref.GetSavedSystemToken(getActivity());
        VIN_NUMBER          = SharedPref.getVINNumber(getActivity());
        VehicleId           = SharedPref.getVehicleId(getActivity());

        // Get Inspection Details
        if (Globally.isConnected(getActivity())) {
            GetInspectionDetail(DRIVER_ID, DeviceId, Globally.PROJECT_ID, VIN_NUMBER);
        }

        setOdometerSpinnerData();

        eldMenuLay.setOnClickListener(this);
        saveInspectionBtn.setOnClickListener(this);
        changeLocBtn.setOnClickListener(this);
        preTripButton.setOnClickListener(this);
        postTripButton.setOnClickListener(this);
        DefectsCorrectedBtn.setOnClickListener(this);
        DefectsNotCorrectedBtn.setOnClickListener(this);
        superviserSignLay.setOnClickListener(this);
        DriverSignLay.setOnClickListener(this);
        checkboxTrailer.setOnClickListener(this);
        checkboxTruck.setOnClickListener(this);
        dateActionBarTV.setOnClickListener(this);
        inspectionTrailerLay.setOnClickListener(this);
        trailerInspectionTV.setOnClickListener(this);
        inspectionTruckLay.setOnClickListener(this);
        inspectionScrollView.setOnClickListener(this);

        prePostRadioGroup.setOnCheckedChangeListener(this);
        correctRadioGroup.setOnCheckedChangeListener(this);

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }


    @Override
    public void onResume() {
        super.onResume();

        CompanyId           = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
        DriverId            = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
        CoDriverId          = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
        CurrentCycleId      = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());

        DRIVER_ID           = SharedPref.getDriverId( getActivity());
        VIN_NUMBER          = SharedPref.getVINNumber(getActivity());
        IsAOBRDAutomatic    = SharedPref.IsAOBRDAutomatic(getActivity());
        DeviceId            = SharedPref.GetSavedSystemToken(getActivity());
        CreatedDate         = Globally.ConvertDateFormatMMddyyyyHHmm(Globally.GetDriverCurrentDateTime(new Globally(), getActivity()));
        IsAOBRD             = SharedPref.IsAOBRD(getActivity());
        VehicleId           = SharedPref.getVehicleId(getActivity());

        DriverName          = slideMenu.usernameTV.getText().toString();
        DriverTimeZone      = SharedPref.getTimeZone(getActivity());
        InspectionDateTime  = CreatedDate;
        date                    = Globally.ConvertDateTimeFormat(Globally.GetDriverCurrentDateTime(new Globally(), getActivity()));

        TruckNumber         = SharedPref.getTruckNumber(getActivity());
        TrailerNumber       = SharedPref.getTrailorNumber(getActivity());

        powerInspectionTV.setText(TruckNumber);
        trailerTextVw.setText(TrailerNumber);
        saveInspectionBtn.setEnabled(true);

        CheckTrailerStatus();
        setOdometerWithView();

        if(IsAOBRD && !IsAOBRDAutomatic){
            locInspTitleTV.setText("Enter City");
            AobrdLocLay.setVisibility(View.VISIBLE);
            locInspectionTV.setVisibility(View.GONE);

            cityEditText.setText(EldFragment.City);
            cityEditText.setSelection(EldFragment.City.length());
            if(EldFragment.OldSelectedStatePos < StateList.size()) {
                stateInspSpinner.setSelection(EldFragment.OldSelectedStatePos);
                State = StateList.get(EldFragment.OldSelectedStatePos).getStateCode();
                Country = StateList.get(EldFragment.OldSelectedStatePos).getCountry();
            }

            if(Globally.LATITUDE.length() < 5){
                changeLocBtn.setVisibility(View.VISIBLE);
            }else{
                if(Globally.isConnected(getActivity()) == false) {
                  if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
                      locInspectionTV.setText(csvReader.getShortestAddress(getActivity()));
                }else{
                      locInspectionTV.setText(Globally.LATITUDE + "," + Globally.LONGITUDE);
                }


                }
                changeLocBtn.setVisibility(View.GONE);
            }
        }else {
            locInspTitleTV.setText(getResources().getString(R.string.loc_of_inspection));
            AobrdLocLay.setVisibility(View.GONE);
            locInspectionTV.setVisibility(View.VISIBLE);

            getLocation();
            locInspectionTV.setText(Location);

            if(Location.length() == 0 || Location.contains("0.0")){
                changeLocBtn.setVisibility(View.VISIBLE);
                locInspectionTV.setText("");
            }
            if(IsAOBRD == false && Globally.LATITUDE.length() > 4) {
                changeLocBtn.setVisibility(View.GONE);
            }
        }

        inspectionDateTv.setText(CreatedDate.substring(0, 11));

        if (!isOnCreate && Globally.isConnected(getActivity())) {
            if(TruckInspList.size() == 0 || TrailerInspList.size() == 0)
                GetInspectionDetail(DRIVER_ID, DeviceId, Globally.PROJECT_ID, VIN_NUMBER);
        }

        ShowHidePtiTrailerView();

        inspection18DaysArray   = inspectionMethod.getSavedInspectionArray(Integer.valueOf(DRIVER_ID), dbHelper);
        inspectionUnPostedArray = inspectionMethod.getOfflineInspectionsArray(Integer.valueOf(DRIVER_ID), dbHelper);

        if(Globally.isConnected(getActivity()) && inspectionUnPostedArray.length() > 0 ){
            ClearFields();
            saveInspectionPost.PostDriverLogData(inspectionUnPostedArray, APIs.SAVE_INSPECTION_OFFLINE, Constants.SocketTimeout20Sec, true, false, 1, 102);
        }

        EldFragment.IsPrePost = false;
        isOnCreate = false;


    }


    void getLocation(){
        try {

            driverLogArray = new JSONArray();
            driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);

            JSONObject lastItemJson = hMethods.GetLastJsonFromArray(driverLogArray);

            Location = lastItemJson.getString(ConstantsKeys.StartLocation);
            if(Location.contains("null") || Location.equals(getString(R.string.no_location_found))) {
                if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
                    Location = csvReader.getShortestAddress(getActivity());
                }else{
                    Location = Globally.LATITUDE + "," + Globally.LONGITUDE;
                }
                changeLocBtn.setVisibility(View.VISIBLE);
            }
            if (Globally.isConnected(getActivity()) && Globally.LATITUDE.length() > 5) {
                GetAddFromLatLng(Globally.LATITUDE, Globally.LONGITUDE);
            }else{
                if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
                    locInspectionTV.setText(csvReader.getShortestAddress(getActivity()));
                }else{
                    locInspectionTV.setText(Globally.LATITUDE + "," + Globally.LONGITUDE);
                }

                changeLocBtn.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            driverLogArray = new JSONArray();
        }
    }

    void ShowHidePtiTrailerView(){
        if(Constants.IS_TRAILER_INSPECT){
            inspectionTypeTV.setVisibility(View.GONE);
            prePostRadioGroup.setVisibility(View.GONE);
            truckTrailerTVLay.setVisibility(View.GONE);
            truckTrailerLayout.setVisibility(View.GONE);
            EldTitleTV.setText(getResources().getString(R.string.TrailerInspection));
        }else {
            inspectionTypeTV.setVisibility(View.VISIBLE);
            prePostRadioGroup.setVisibility(View.VISIBLE);
            truckTrailerTVLay.setVisibility(View.VISIBLE);
            truckTrailerLayout.setVisibility(View.VISIBLE);
            EldTitleTV.setText(getResources().getString(R.string.prePostTripIns));

        }
    }


    private void AddStatesInList(){
        int stateListSize = 0;
        StateArrayList = new ArrayList<String>();
        StateList      = new ArrayList<DriverLocationModel>();

        try {
            StateList       = statePrefManager.GetState(getActivity());
            StateList.add(0,  new DriverLocationModel("", "Select", ""));
            stateListSize   = StateList.size();
        }catch (Exception e){
            stateListSize = 0;
        }


        for(int i = 0 ; i < stateListSize ; i++){
            StateArrayList.add(StateList.get(i).getState());
        }


        if (StateArrayList.size() > 0) {
            // Creating adapter for spinner
            ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, StateArrayList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stateInspSpinner.setAdapter(dataAdapter);

        }

    }



    private void SetDataInList(List<PrePostModel> array, ArrayList<String> list, ArrayList<Integer> idList){
        for(int i = 0 ; i < array.size() ; i++){
            list.add("");
            idList.add(-1);
        }
    }

    private String GetItemsId(ArrayList<String> array, ArrayList<Integer> idArray) {
        String data = "";
        try{
            for (int i = 0; i < array.size(); i++) {
                if (!array.get(i).equals("")) {
                    data = data + "," + idArray.get(i);
                }
            }

            if (data.length() > 0 && data.substring(0, 1).equals(",")) {
                data = data.substring(1, data.length());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       // Logger.LogDebug("data", "---data: " + data);
        return data;

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.inspectionScrollView:
                Globally.hideSoftKeyboard(getActivity());
                break;

            case R.id.inspectionTrailerLay:

                CurrentJobStatus = SharedPref.getDriverStatusId(getActivity());

                if (CurrentJobStatus.equals(Globally.DRIVING )) {
                    Globally.EldScreenToast(saveInspectionBtn, ConstantsEnum.TRAILER_CHANGE , getResources().getColor(R.color.colorVoilation));
                }else {

                   // if(constants.isActionAllowed(getContext())) {

                 //   HelperMethods helperMethods = new HelperMethods();
                   // DBHelper dbHelper = new DBHelper(getActivity());
                    if(hMethods.isActionAllowedWhileMoving(getActivity(), new Globally(), DriverId, dbHelper)){
                        if (Globally.isConnected(getActivity())) {
                            List<String> onDutyRemarkList = SharedPref.getOnDutyRemarks(new JSONArray(), getActivity());
                            dialog = new TrailorDialog(getActivity(), "trailor", false, TrailerNumber,
                                    0, false, onDutyRemarkList, 0, dbHelper, new TrailorListener());
                            dialog.show();
                        } else {
                            Globally.EldScreenToast(saveInspectionBtn, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                        }
                    }else{
                        Globally.EldScreenToast(saveInspectionBtn, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }

                }

                break;


            case R.id.inspectionTruckLay:

                CurrentJobStatus = SharedPref.getDriverStatusId(getActivity());

                if (CurrentJobStatus.equals(Globally.DRIVING )) {
                    Globally.EldScreenToast(saveInspectionBtn, ConstantsEnum.TRUCK_CHANGE, getResources().getColor(R.color.colorVoilation));
                }else {
                   // if(constants.isActionAllowed(getContext())) {
                    if(hMethods.isActionAllowedWhileMoving(getActivity(), new Globally(), DriverId, dbHelper)){
                        if (Globally.isConnected(getActivity())) {
                            inspectionTruckLay.setEnabled(false);
                            inspectionProgressBar.setVisibility(View.VISIBLE);
                            GetOBDAssignedVehicles(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()), DeviceId, CompanyId, VIN_NUMBER);
                        } else {
                            Globally.EldScreenToast(saveInspectionBtn, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                        }
                    }else{
                        Globally.EldScreenToast(saveInspectionBtn, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }
                }

                break;


            case R.id.dateActionBarTV:
              //  ShowDateDialog();

                MoveFragment(SelectedDatee);

                break;


            case R.id.checkboxTrailer:

                IsClicked = true;

                if(TrailerInspList.size() > 0) {
                    if (checkboxTrailer.isChecked()) {
                        isChecked = true;
                    } else {
                        isChecked = false;
                        TraiorIssueType = "";
                        SetDataInList(TrailerInspList, TrailerList, TrailerIdList);
                    }

                    trailerAdapter = new GridAdapter(getActivity(), isChecked, IsClicked, TrailerInspList, TrailerList, TrailerIdList);
                    trailerGridView.setAdapter(trailerAdapter);
                   // trailerAdapter.notifyDataSetChanged();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            trailerInspectionTV.performClick();
                        }
                    }, 300);
                }

                break;

            case R.id.checkboxTruck:

                IsClicked = true;

                if(TruckInspList.size() > 0) {
                    if (checkboxTruck.isChecked()) {
                        isChecked = true;
                    } else {
                        isChecked = false;
                        TruckIssueType = "";
                        SetDataInList(TruckInspList, TruckList, TruckIdList);
                    }

                    truckAdapter = new GridAdapter(getActivity(), isChecked, IsClicked, TruckInspList, TruckList, TruckIdList);
                    truckGridView.setAdapter(truckAdapter);
                    //truckAdapter.notifyDataSetChanged();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            trailerInspectionTV.performClick();
                        }
                    }, 300);
                }

                break;


            case R.id.trailerInspectionTV:
                TruckIssueType = "";
                TruckIssueType = GetItemsId(TruckList, TruckIdList);

                //  if(TruckIssueType.length() == 0) {
                TraiorIssueType = "";
                TraiorIssueType = GetItemsId(TrailerList, TrailerIdList);
                // }

                if(TruckIssueType.length() > 0 || TraiorIssueType.length() > 0){
                    noDefectLabel.setText("Please select defects status");
                    correctRadioGroup.setVisibility(View.VISIBLE);
                    superviserSignLay.setVisibility(View.VISIBLE);
                    supervisorNameLay.setVisibility(View.VISIBLE);
                }else{
                    noDefectLabel.setText("No defects");
                    correctRadioGroup.setVisibility(View.GONE);
                    superviserSignLay.setVisibility(View.GONE);
                    supervisorNameLay.setVisibility(View.GONE);
                    SetButtonView("defects");
                }


                break;



            case  R.id.superviserSignLay:
                // if(SupervisorSignImage.trim().length() == 0 ) {
                SignImageSelected = "supervisor";
                if (signDialog != null && signDialog.isShowing())
                    signDialog.dismiss();
                signDialog = new SignDialog(getActivity(), new SignListener());
                signDialog.show();
                // }
                break;

            case R.id.DriverSignLay:

                /*SignImageSelected = "driver";
                if (signDialog != null && signDialog.isShowing())
                    signDialog.dismiss();
                signDialog = new SignDialog(getActivity(), new SignListener());
                signDialog.show();
                */

                inspection18DaysArray = inspectionMethod.getSavedInspectionArray(Integer.valueOf(DRIVER_ID), dbHelper);
                boolean isPtiSignExist = constants.isPTI_SignExist(inspection18DaysArray);

                if(isPtiSignExist) {
                    if (ptiSignDialog != null && ptiSignDialog.isShowing()) {
                        ptiSignDialog.dismiss();
                    }
                    ptiSignDialog = new PtiSignDialog(getActivity(), "pti", "", inspection18DaysArray, new PtiConfirmationListener());
                    ptiSignDialog.show();
                }else{
                    openSignDialog();
                }

                break;

            case R.id.saveInspectionBtn:

                boolean isCurrentStatusMismatch = false;
                 CurrentJobStatus = SharedPref.getDriverStatusId(getActivity());
                Logger.LogDebug("CurrentJobStatus","CurrentJobStatus: "+CurrentJobStatus);

                try {
                    JSONArray driverLogArray = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
                    JSONObject lastJsonItem = hMethods.GetLastJsonFromArray(driverLogArray);
                    String CurrentJobStatusByLog = lastJsonItem.getString(ConstantsKeys.DriverStatusId);
                    if(!CurrentJobStatusByLog.equals(CurrentJobStatus)){
                        isCurrentStatusMismatch = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                if (CurrentJobStatus.equals(Globally.ON_DUTY ) || isCurrentStatusMismatch) {
                    if(hMethods.isActionAllowedWhileMoving(getActivity(), new Globally(), DriverId, dbHelper)){
                        if(TrailerNumber.length() > 0 || TruckNumber.length() > 0) {
                            CheckInspectionValidation();
                        }else{
                            Globally.EldScreenToast(saveInspectionBtn, "Please update your Truck or Trailer number before save the inspections." , getResources().getColor(R.color.colorVoilation));
                        }
                    }else{
                        Globally.EldScreenToast(saveInspectionBtn, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }
                }else {
                    Globally.EldScreenToast(saveInspectionBtn, ConstantsEnum.PTI_SAVE_ONDUTY_ONLY, getResources().getColor(R.color.colorVoilation));
                }

                break;


            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;

            case R.id.changeLocBtn:
                isLocationChange = true;
                locInspectionTV.setEnabled(true);
                locInspectionTV.requestFocus();
                locInspectionTV.setFocusableInTouchMode(true);
                locInspectionTV.setSelection(locInspectionTV.getText().toString().length());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(locInspectionTV, InputMethodManager.SHOW_FORCED);

                break;

        }
    }


    void CallSaveInspectionAPI(){

        SignCopyDate = "";
        if(IsSignCopy) {
            ArrayList<String> SignWithDate = constants.getPtiLastSignature(inspection18DaysArray);
            if(SignWithDate.size() > 1) {
                ByteDriverSign  = SignWithDate.get(0);
                SignCopyDate    = SignWithDate.get(1);
            }
        }

        if(DriverSignImage.length() > 0 || ByteDriverSign.length() > 0) {
            getViewData();
            SaveInspectionOfflineWithAPI(IsSignCopy);
        }else{
            inspectionScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            Globally.EldScreenToast(saveInspectionBtn, "Please add driver signature.", getResources().getColor(R.color.colorVoilation));
        }





    }

    void CheckInspectionValidation(){

        if(Constants.IS_TRAILER_INSPECT){
            InspectionTypeId = String.valueOf(Constants.Trailer);
            CheckInspectValidationPart0();
        }else {
            if (PreTripInsp.equals("true") || PostTripInsp.equals("true")) {
                CheckInspectValidationPart0();
            } else {
                inspectionScrollView.fullScroll(ScrollView.FOCUS_UP);
                Globally.EldScreenToast(saveInspectionBtn, "Please select trip inspection type.", getResources().getColor(R.color.colorVoilation));
            }
        }


    }


    void CheckInspectValidationPart0(){

        if (IsAOBRD && !IsAOBRDAutomatic) {
            City = cityEditText.getText().toString().trim();
            Location = City + ", " + State + ", " + Country;
            if (City.length() > 0 && State.length() > 0) {
                if(!InspectionTypeId.equals(""+Constants.Trailer)) {
                    if (PreTripInsp.equals("true"))
                        InspectionTypeId = String.valueOf(Constants.PreInspection);
                    else
                        InspectionTypeId = String.valueOf(Constants.PostInspection);
                }
                CheckInspectValidationPart2();

            } else {
                inspectionScrollView.fullScroll(ScrollView.FOCUS_UP);
                cityEditText.requestFocus();
                if (City.length() == 0) {
                    Globally.EldScreenToast(saveInspectionBtn, "Enter city name.", getResources().getColor(R.color.colorVoilation));
                }else{
                    Globally.EldScreenToast(saveInspectionBtn, "Select State first.", getResources().getColor(R.color.colorVoilation));
                }
            }
        } else {
            getLocation();
            if(!InspectionTypeId.equals(""+Constants.Trailer)) {
                if (PreTripInsp.equals("true"))
                    InspectionTypeId = String.valueOf(Constants.PreInspection);
                else
                    InspectionTypeId = String.valueOf(Constants.PostInspection);
            }

            CheckInspectValidationPart2();

        }


    }

    void CheckInspectValidationPart2() {

        boolean isObdConnected = constants.isObdConnectedWithELD(getActivity());

         if(!isObdConnected){

             Odometer = odometerEditTxt.getText().toString().trim();

             if(Odometer.length() > 0){
                 if(OdometerDistanceType.equals("Miles")){
                     Odometer = Constants.milesToMeter(Odometer);
                 }else{ // if(OdometerDistanceType.equals("KM"))
                     Odometer = Constants.kmToMeter1(Odometer);
                 }
             }

             if(odometerEditTxt.getVisibility() != View.VISIBLE) {
                 currentOdometerTV.setVisibility(View.GONE);
                 selectDistanceSpinner.setVisibility(View.VISIBLE);
                 odometerEditTxt.setVisibility(View.VISIBLE);
             }

        }else{
             Odometer = Constants.kmToMeter1(SharedPref.getObdOdometer(getContext()));
             OdometerDistanceType = "KM";   // temp to pass check

             if(odometerEditTxt.getVisibility() == View.VISIBLE) {
                 currentOdometerTV.setVisibility(View.VISIBLE);
                 selectDistanceSpinner.setVisibility(View.GONE);
                 odometerEditTxt.setVisibility(View.GONE);

             }

        }

       /* if( (isObdConnected || Odometer.length() == 0 || !Odometer.equals("0") ) &&
                (OdometerDistanceType.equals("KM") || OdometerDistanceType.equals("Miles")) ) {
          */


            if (correctRadioGroup.getVisibility() == View.VISIBLE && (TruckIssueType.length() > 0 || TraiorIssueType.length() > 0)) {
                if (AboveDefectsCorrected.equals("true") || AboveDefectsNotCorrected.equals("true")) {
                    CallSaveInspectionAPI();
                } else {
                    Globally.EldScreenToast(inspectionDateTv, "Please select defect status", getResources().getColor(R.color.colorVoilation));
                }
            } else {
                TruckIssueType = "";
                TraiorIssueType = "";
                CallSaveInspectionAPI();
            }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int CheckBoxId) {

        boolean isChecked = radioGroup.isPressed();
       // if (isChecked) {
            switch (CheckBoxId) {

                case R.id.preTripButton:
                    btnSelectedType = "pre";
                    SetButtonView(btnSelectedType);

                    break;

                case R.id.postTripButton:
                    btnSelectedType = "post";
                    SetButtonView(btnSelectedType);

                    break;

                case R.id.DefectsCorrectedBtn:
                    btnSelectedType = "corrected";
                    SetButtonView(btnSelectedType);
                    break;

                case R.id.DefectsNotCorrectedBtn:
                    btnSelectedType = "notCorrected";
                    SetButtonView(btnSelectedType);

                    break;
            }
      //  }
    }


    private class TrailorListener implements TrailorDialog.TrailorListener{

        @Override
        public void CancelReady() {

            try {
                if(dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void JobBtnReady(String TrailorNo, String reason, String type, boolean isTrailerUpdate, int ItemPosition, EditText TrailorNoEditText, EditText ReasonEditText) {

            Globally.hideKeyboardView(getActivity(), TrailorNoEditText);
            tempTrailer = TrailorNo;

            try {
                if(dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            SaveTrailerNumber(DRIVER_ID, DeviceId, TrailorNo);

        }
    }



    void setOdometerWithView(){

        String odometerInKm = SharedPref.getObdOdometer(getContext());

        if(odometerInKm.equals("--") || odometerInKm.equals("null")){
            odometerInKm = "0";
        }

        if(constants.isObdConnectedWithELD(getActivity())){
            Odometer = Constants.kmToMeter1(odometerInKm);

            if(Odometer.length() == 0 || !Odometer.equals("0")){

                String odometerInMiles = Constants.kmToMiles(odometerInKm);

                currentOdometerTV.setText(Constants.getUpTo2DecimalString(odometerInKm) + " km (" + Constants.getUpTo2DecimalString(odometerInMiles) + " miles)" );

                currentOdometerTV.setVisibility(View.VISIBLE);
                selectDistanceSpinner.setVisibility(View.GONE);
                odometerEditTxt.setVisibility(View.GONE);

            }else{
                currentOdometerTV.setVisibility(View.GONE);
                selectDistanceSpinner.setVisibility(View.VISIBLE);
                odometerEditTxt.setVisibility(View.VISIBLE);
                Odometer = "0";
            }


        }else{
            currentOdometerTV.setVisibility(View.GONE);
            selectDistanceSpinner.setVisibility(View.VISIBLE);
            odometerEditTxt.setVisibility(View.VISIBLE);
            Odometer = "0";
        }

    }


    void setOdometerSpinnerData(){
        try{
            DistanceTypeList = new ArrayList<String>();
            DistanceTypeList.add("Select Unit");
            DistanceTypeList.add("KM");
            DistanceTypeList.add("Miles");

            ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, DistanceTypeList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectDistanceSpinner.setAdapter(dataAdapter);

            // Spinner click listener
            selectDistanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    OdometerDistanceType = DistanceTypeList.get(position);
                    Logger.LogDebug("OdometerDistanceType ",OdometerDistanceType);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void getViewData(){
        Remarks = remarksEditText.getText().toString();
        SupervisorMechanicsName = SupervisorNameTV.getText().toString();
        TruckIssueType = ""; TraiorIssueType = "";
        TruckIssueType = GetItemsId(TruckList, TruckIdList);
        TraiorIssueType = GetItemsId(TrailerList, TrailerIdList);

    }


    void SetButtonView(String buttonType){
        if(buttonType.equals("pre")){
            PreTripInsp = "true"; PostTripInsp = "false";
            if(UILApplication.getInstance().isNightModeEnabled()){
                postTripButton.setTextColor(Color.parseColor(WhiteColor));
                preTripButton.setTextColor(Color.parseColor(RadioDarkColor));
            }else{
                postTripButton.setTextColor(Color.parseColor(BlackColor));
                preTripButton.setTextColor(Color.parseColor(EldThemeColor));
            }
        }else if(buttonType.equals("post")){
            PreTripInsp = "false"; PostTripInsp = "true"; //AboveDefectsCorrected = "false"; AboveDefectsNotCorrected = "false";
            if(UILApplication.getInstance().isNightModeEnabled()){
                preTripButton.setTextColor(Color.parseColor(WhiteColor));
                postTripButton.setTextColor(Color.parseColor(RadioDarkColor));
            }else{
                preTripButton.setTextColor(Color.parseColor(BlackColor));
                postTripButton.setTextColor(Color.parseColor(EldThemeColor));
            }
        }else if(buttonType.equals("corrected")){
            AboveDefectsCorrected = "true"; AboveDefectsNotCorrected = "false";  // PreTripInsp = "false"; PostTripInsp = "false";
            if(UILApplication.getInstance().isNightModeEnabled()){
                DefectsNotCorrectedBtn.setTextColor(Color.parseColor(WhiteColor));
                DefectsCorrectedBtn.setTextColor(Color.parseColor(RadioDarkColor));
            }else{
                DefectsNotCorrectedBtn.setTextColor(Color.parseColor(BlackColor));
                DefectsCorrectedBtn.setTextColor(Color.parseColor(EldThemeColor));
            }
        }else if(buttonType.equals("notCorrected")){
            AboveDefectsCorrected = "false"; AboveDefectsNotCorrected = "true";
            if(UILApplication.getInstance().isNightModeEnabled()){
                DefectsCorrectedBtn.setTextColor(Color.parseColor(WhiteColor));
                DefectsNotCorrectedBtn.setTextColor(Color.parseColor(RadioDarkColor));
            }else{
                DefectsCorrectedBtn.setTextColor(Color.parseColor(BlackColor));
                DefectsNotCorrectedBtn.setTextColor(Color.parseColor(EldThemeColor));
            }
        }else if(buttonType.equals("defects")){
            AboveDefectsCorrected = "false"; AboveDefectsNotCorrected = "false";
            DefectsNotCorrectedBtn.setTextColor(Color.parseColor(BlackColor));
            DefectsCorrectedBtn.setTextColor(Color.parseColor(BlackColor));
        }else{
            AboveDefectsCorrected = "false"; AboveDefectsNotCorrected = "false";  PreTripInsp = "false"; PostTripInsp = "false";
            postTripButton.setTextColor(Color.parseColor(BlackColor));
            preTripButton.setTextColor(Color.parseColor(BlackColor));
            DefectsNotCorrectedBtn.setTextColor(Color.parseColor(BlackColor));
            DefectsCorrectedBtn.setTextColor(Color.parseColor(BlackColor));
        }
    }



    /*================== Get Signature Bitmap ====================*/
    void GetSignatureBitmap(View targetView, ImageView canvasView){
        canvasView.setImageResource(R.drawable.transparent);
        Bitmap b = Bitmap.createBitmap(targetView.getWidth(),
                targetView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        targetView.draw(c);
        BitmapDrawable d = new BitmapDrawable(getResources(), b);
        canvasView.setBackgroundDrawable(d);

        if( SignImageSelected.equals("driver") )
            DriverSignImage     = Globally.SaveBitmapToFile(b, "sign", 100, getActivity());
        else
            SupervisorSignImage = Globally.SaveBitmapToFile(b, "mech_sign", 100, getActivity());

    }


    void ParseListData(JSONArray array, List<PrePostModel> list){
        for(int i = 0 ; i < array.length() ; i++){
            try {
                JSONObject truckObj = (JSONObject)array.get(i);
                PrePostModel model = new PrePostModel(truckObj.getString("InspectionIssueTypeId"), truckObj.getString("IssueName"));
                list.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }



    private void MoveFragment(String date ){
        Constants.IsInspectionDetailViewBack = false;
        Constants.SelectedDatePti = date;
        InspectionsHistoryFragment savedInspectionFragment = new InspectionsHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("inspection_type", "pti");
        savedInspectionFragment.setArguments(bundle);
        Constants.SelectedDatePti = date;

        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, savedInspectionFragment);
        fragmentTran.addToBackStack("inspection");
        fragmentTran.commitAllowingStateLoss();


    }

/*    void ShowDateDialog(){
        try {
            if (dateDialog != null && dateDialog.isShowing())
                dateDialog.dismiss();

            dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, SelectedDatee, new DateListener(), false);
            dateDialog.show();
        }catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }*/

    void ClearFields(){

        postTripButton.setChecked(false);
        preTripButton.setChecked(false);
        DefectsCorrectedBtn.setChecked(false);
        DefectsNotCorrectedBtn.setChecked(false);

        prePostRadioGroup.clearCheck();
        correctRadioGroup.clearCheck();

        postTripButton.setTextColor(Color.parseColor(BlackColor));
        preTripButton.setTextColor(Color.parseColor(BlackColor));
        DefectsCorrectedBtn.setTextColor(Color.parseColor(BlackColor));
        DefectsNotCorrectedBtn.setTextColor(Color.parseColor(BlackColor));


        SetButtonView("clear");

        TruckList = new ArrayList<>();
        TruckIdList = new ArrayList<>();
        TrailerList = new ArrayList<>();
        TrailerIdList = new ArrayList<>();

        SetDataInList(TruckInspList, TruckList, TruckIdList);
        SetDataInList(TrailerInspList, TrailerList, TrailerIdList);

        truckAdapter = new GridAdapter(getActivity(), false, false, TruckInspList, TruckList, TruckIdList);
        trailerAdapter = new GridAdapter(getActivity(), false, false, TrailerInspList, TrailerList, TrailerIdList);
        truckGridView.setAdapter(truckAdapter);
        trailerGridView.setAdapter(trailerAdapter);

        remarksEditText.setText("");
        SupervisorNameTV.setText("");
        cityEditText.setText("");
        InspectionTypeId = "";
        locInspectionTV.setText("");
        odometerEditTxt.setText("");

        setOdometerSpinnerData();

        Constants.IS_TRAILER_INSPECT = false;
        noDefectLabel.setText("No defects");
        //noDefectLabel.setVisibility(View.VISIBLE);
        // prePostLay.setVisibility(View.VISIBLE);
        correctRadioGroup.setVisibility(View.GONE);
        supervisorNameLay.setVisibility(View.GONE);
        superviserSignLay.setVisibility(View.GONE);
        DriverSignImage     = "";
        SupervisorSignImage = "";
        ByteDriverSign = ""; SignCopyDate = "";
        ByteSupervisorSign = "";

        IsSignCopy = false;
        PreTripInsp = "false";
        PostTripInsp = "false";
        AboveDefectsCorrected = "false";
        AboveDefectsNotCorrected = "false";
        Remarks = "";
        SupervisorMechanicsName = "";
        TruckIssueType = "";
        TraiorIssueType = "";
        InspectionTypeId = "";
        Location = "";

        checkboxTruck.setChecked(false);
        checkboxTrailer.setChecked(false);
        EldFragment.City = "";
        EldFragment.OldSelectedStatePos = 0;

        signDriverIV.setBackgroundDrawable(null);
        signSuprvsrIV.setBackgroundDrawable(null);
        signDriverIV.setImageResource(R.drawable.transparent);
        signSuprvsrIV.setImageResource(R.drawable.transparent);

        ScrollUpView();


    }


  /*  private class DateListener implements DatePickerDialog.DatePickerListener{
        @Override
        public void JobBtnReady(String SelectedDate, String dayOfTheWeek, String MonthFullName, String MonthShortName, int dayOfMonth) {

            try {
                if (dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();
            }catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            SelectedDatee = SelectedDate;
            MoveFragment(SelectedDate);
        }
    }
*/

    private class VehicleListener implements VehicleDialog.VehicleListener {
        @Override
        public void ChangeVehicleReady(String Title, int position, boolean isOldDialog) {


            try {
                if(vehicleDialog != null && vehicleDialog.isShowing())
                    vehicleDialog.dismiss();
            }catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            SaveVehicleNumber(position, DriverId, CoDriverId, CompanyId, true);

        }

    }


    /*================== Signature Listener ====================*/
    private class SignListener implements SignDialog.SignListener{

        @Override
        public void SignOkBtn(InkView inkView, boolean IsSigned) {

            if(IsSigned) {
                SignCopyDate = Globally.GetDriverCurrentDateTime(new Globally(), getActivity());
                if( SignImageSelected.equals("driver") ){
                    signDriverIV.setImageResource(R.drawable.transparent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GetSignatureBitmap(inkView, signDriverIV);
                        }
                    }, 100);

                }else {
                    GetSignatureBitmap(inkView, signSuprvsrIV);
                }
            }else{
                if( SignImageSelected.equals("driver") ){
                    signDriverIV.setImageResource(R.drawable.transparent);
                    signDriverIV.setBackgroundDrawable(null);
                    DriverSignImage = "";
                    ByteDriverSign = ""; SignCopyDate = "";
                }else {
                    signSuprvsrIV.setBackgroundDrawable(null);
                    SupervisorSignImage = "";
                }

            }


            signDialog.dismiss();
        }
    }



    private class PtiConfirmationListener implements PtiSignDialog.PtiConfirmationListener{

        @Override
        public void PtiBtnReady(String ByteSign, String SignDate) {
            IsSignCopy = true;
            ByteDriverSign = ByteSign;
            SignCopyDate   = SignDate;
            if(SignCopyDate.contains("T")){
                SignCopyDate = Globally.ConvertDateFormatMMddyyyyHHmm(SignCopyDate);
            }
            signDriverIV.setImageResource(R.drawable.transparent);
            signDriverIV.setBackgroundDrawable(null);
            constants.LoadByteImage(signDriverIV, ByteDriverSign);
        }

        @Override
        public void CancelBtnReady() {
            IsSignCopy = false;
            openSignDialog();
        }
    }



    private void openSignDialog(){
        SignImageSelected = "driver";
        if (signDialog != null && signDialog.isShowing())
            signDialog.dismiss();
        signDialog = new SignDialog(getActivity(), new SignListener());
        signDialog.show();
    }


    void SaveVehicleNumber(int position, String DRIVER_Id, String CoDriverId, String CompanyId, boolean isShown){
        VehListPosition = position;
        UpdateOBDAssignedVehicle(
                DRIVER_Id,
                CoDriverId,
                DeviceId,
                vehicleList.get(position).getPreviousDeviceMappingId(),
                vehicleList.get(position).getDeviceMappingId(),
                vehicleList.get(position).getVehicleId(),
                vehicleList.get(position).getEquipmentNumber(),
                vehicleList.get(position).getPlateNumber(),
                vehicleList.get(position).getVIN(),
                CompanyId,
                SharedPref.getImEiNumber(getActivity()) );

    }

    /*================== Update OBD Assigned Vehicles ===================*/
    void UpdateOBDAssignedVehicle(final String DriverId, final String CoDriverId, final String DeviceId,  final String PreviousDeviceMappingId,
                                  final String DeviceMappingId, final String VehicleId, final String EquipmentNumber,
                                  final String PlateNumber, final String VIN, final String CompanyId, final String IMEINumber){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.CoDriverId, CoDriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.PreviousDeviceMappingId, PreviousDeviceMappingId);
        params.put(ConstantsKeys.DeviceMappingId, DeviceMappingId);
        params.put(ConstantsKeys.VehicleId, VehicleId);
        params.put(ConstantsKeys.EquipmentNumber, EquipmentNumber);
        params.put(ConstantsKeys.PlateNumber, PlateNumber);
        params.put(ConstantsKeys.VIN, VIN);
        params.put(ConstantsKeys.CompanyId, CompanyId);
        params.put(ConstantsKeys.IMEINumber, IMEINumber);

        SaveOBDVehRequest.executeRequest(Request.Method.POST, APIs.UPDATE_OBD_ASSIGNED_VEHICLE , params, UpdateObdVeh,
                Constants.SocketTimeout30Sec, ResponseCallBack, ErrorCallBack);

    }



    /*================== Get OBD Assigned Vehicles ===================*/
    void GetOBDAssignedVehicles(final String DriverId, final String DeviceId, final String CompanyId,  final String VIN){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId);
         params.put(ConstantsKeys.CompanyId, CompanyId);
         params.put(ConstantsKeys.VIN, VIN);

        GetOBDVehRequest.executeRequest(Request.Method.POST, APIs.GET_OBD_ASSIGNED_VEHICLES , params, GetObdAssignedVeh,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Save Trailer Number ===================*//*
    void SaveTrailerNumber(final String DriverId, final String DeviceId, final String TrailerNumber){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId);
         params.put(ConstantsKeys.VIN, TrailerNumber);        // ( please note: here VIN is used as TrailorNumber in parameters. )

        SaveTrailerNumber.executeRequest(Request.Method.POST, APIs.UPDATE_TRAILER_NUMBER , params, SaveTrailer,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }



    //*================== Get Inspection Details ===================*//*
    void GetInspectionDetail(final String DriverId, final String DeviceId, final String ProjectId, final String VIN){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
         params.put(ConstantsKeys.ProjectId, ProjectId );
         params.put(ConstantsKeys.VIN, VIN );

        GetInspectionRequest.executeRequest(Request.Method.POST, APIs.GET_INSPECTION_DETAIL , params, GetInspection,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Get Address From Lat Lng ===================*//*
    void GetAddFromLatLng(String lat, String lon) {

        params = new HashMap<String, String>();
         params.put(ConstantsKeys.Latitude, lat);
         params.put(ConstantsKeys.Longitude, lon );

        GetAddFromLatLngRequest.executeRequest(Request.Method.POST, APIs.GET_Add_FROM_LAT_LNG, params, GetAddFromLatLng,
                Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);
    }

    void ScrollUpView(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                inspectionScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 100);
    }


    private void ParseInspectionIssues(JSONArray TruckArray, JSONArray TrailerArray){
        ParseListData(TruckArray, TruckInspList);
        ParseListData(TrailerArray, TrailerInspList);

        SetDataInList(TruckInspList, TruckList, TruckIdList);
        SetDataInList(TrailerInspList, TrailerList, TrailerIdList);

        truckAdapter = new GridAdapter(getActivity(), false, false, TruckInspList, TruckList, TruckIdList);
        trailerAdapter = new GridAdapter(getActivity(), false, false, TrailerInspList, TrailerList, TrailerIdList);
        truckGridView.setAdapter(truckAdapter);
        trailerGridView.setAdapter(trailerAdapter);

        final int truckViewCount      = TruckInspList.size() / 2 + TruckInspList.size() % 2;
        final int trailerViewCount    = TrailerInspList.size() / 2 + TrailerInspList.size() % 2;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Constants.inspectionLayHeight * truckViewCount) );
                    truckGridView.setLayoutParams(mParam);
                }catch (Exception e){}

                try {
                    RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Constants.inspectionLayHeight * trailerViewCount) );
                    trailerGridView.setLayoutParams(mParam);
                }catch (Exception e){}

                //inspectionScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 300);


    }


    //*================== Save Driver Inspection ===================*//*
    private void SaveInspectionOfflineWithAPI(boolean IsSignCopy){

        // disable temperory button click to avoid multiple clicks on button at the same time
        saveInspectionBtn.setEnabled(false);
       // CreatedDate         = Globally.ConvertDateFormatMMddyyyyHHmm(Globally.GetCurrentUTCTimeFormat());
        CreatedDate         = Globally.ConvertDateFormatMMddyyyyHHmm(Globally.GetDriverCurrentDateTime(new Globally(), getActivity()));
       // Globally.GetCurrentUTCTimeFormat()
        pDialog.show();

        // Convert image file into bytes

        if(!IsSignCopy){
            SignCopyDate = CreatedDate;
            ByteDriverSign = "";
            File file = new File(DriverSignImage);
            if (file.exists()) {
                Logger.LogInfo("", "---Add File: " + file);
                ByteDriverSign = Globally.ConvertImageToByteAsString(DriverSignImage);
            }
        }else{
            if(SignCopyDate.contains("T")){
                SignCopyDate = Globally.ConvertDateFormatMMddyyyyHHmm(SignCopyDate);
            }
        }

        ByteSupervisorSign = "";
        File f = new File(SupervisorSignImage);
        if (f.exists()) {
            Logger.LogInfo("", "---Add File: " + f.toString());
            ByteSupervisorSign = Globally.ConvertImageToByteAsString(SupervisorSignImage);
        }

        if(isLocationChange){
            Location = locInspectionTV.getText().toString().trim();
        }else{
            if(Location.length() == 0){
                Location = locInspectionTV.getText().toString().trim();
            }
        }


        isLocationChange = false;

        checkOdometer();

        // Add inspection JSON obj in 18 Days Array

        JSONObject inspection18DaysObj = inspectionMethod.AddNewInspectionObj(DRIVER_ID, DeviceId, Globally.PROJECT_ID, DriverName, CompanyId, VehicleId, "", VIN_NUMBER,
                TruckNumber, TrailerNumber, CreatedDate, Location, PreTripInsp, PostTripInsp, AboveDefectsCorrected, AboveDefectsNotCorrected,
                Remarks, Globally.LATITUDE, Globally.LONGITUDE, DriverTimeZone, SupervisorMechanicsName, TruckIssueType, TraiorIssueType, InspectionTypeId,
                ByteDriverSign, ByteSupervisorSign, Odometer, false, SignCopyDate);

        JSONArray reverseArray = shipmentHelperMethod.ReverseArray(inspection18DaysArray);
        JSONObject inspectionFor18DaysObj = inspectionMethod.Add18DaysObj(inspection18DaysObj, TruckList, TruckIdList, TrailerList, TrailerIdList);
        reverseArray.put(inspectionFor18DaysObj);

        // again reverse Array to show last item at top
        inspection18DaysArray = new JSONArray();
        inspection18DaysArray = shipmentHelperMethod.ReverseArray(reverseArray);
        inspectionMethod.DriverInspectionHelper(Integer.valueOf(DRIVER_ID), dbHelper, inspection18DaysArray);


        // Add inspection JSON obj in Offline Array

        JSONObject inspectionData = inspectionMethod.AddNewInspectionObj(DRIVER_ID, DeviceId, Globally.PROJECT_ID,
                DriverName, CompanyId, VehicleId, "", VIN_NUMBER, TruckNumber, TrailerNumber,
                CreatedDate, Location, PreTripInsp, PostTripInsp, AboveDefectsCorrected,  AboveDefectsNotCorrected,
                Remarks, Globally.LATITUDE, Globally.LONGITUDE, DriverTimeZone, SupervisorMechanicsName, TruckIssueType,
                TraiorIssueType, InspectionTypeId, ByteDriverSign, ByteSupervisorSign, Odometer, IsSignCopy, SignCopyDate);

        JSONArray unPostedArray = inspectionMethod.getOfflineInspectionsArray(Integer.valueOf(DRIVER_ID), dbHelper);
        unPostedArray.put(inspectionData);
        inspectionMethod.DriverOfflineInspectionsHelper(Integer.valueOf(DRIVER_ID), dbHelper, unPostedArray);

        Constants.IS_TRAILER_INSPECT = false;
        if(Globally.isConnected(getActivity()) ){
            // reset api call count
            failedApiTrackMethod.isAllowToCallOrReset(dbHelper, APIs.SAVE_INSPECTION_OFFLINE, true, new Globally(), getActivity());

            saveInspectionPost.PostDriverLogData(unPostedArray, APIs.SAVE_INSPECTION_OFFLINE, Constants.SocketTimeout20Sec, true, false, 1, 101);
        }else{
            pDialog.dismiss();
            Globally.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.inspection_willbe_saved ), getResources().getColor(R.color.colorSleeper) );
            ClearFields();
            locInspectionTV.setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TabAct.host.setCurrentTab(0);
                }
            },1200);

        }



    }



    private void checkOdometer(){
        try{
            if(Odometer.contains(".")){
                String[] odoArray = Odometer.split("\\.");
                if(odoArray.length > 1 && !odoArray[1].equals("0")) {
                    Odometer = Constants.kmToMeter1(Odometer);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void CheckTrailerStatus(){
        try{
            if(TrailerNumber.equals(Constants.NoTrailer) || TrailerNumber.trim().equals("")){
                inspectTrailerTitleLay.setVisibility(View.GONE);
                trailerGridLay.setVisibility(View.GONE);

                SetDataInList(TrailerInspList, TrailerList, TrailerIdList);
                trailerAdapter = new GridAdapter(getActivity(), false, false, TrailerInspList, TrailerList, TrailerIdList);
                truckGridView.setAdapter(truckAdapter);

                TraiorIssueType = "";

            }else{
                inspectTrailerTitleLay.setVisibility(View.VISIBLE);
                trailerGridLay.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @Override
        public void getResponse(String response, int flag) {

            Logger.LogDebug("response", "Driver response: " + response);
            String status = "";
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");

            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                if(status.equalsIgnoreCase("true")){

                    switch (flag){

                        case SaveTrailer:
                            try {
                                TrailerNumber = tempTrailer;
                                SharedPref.setTrailorNumber( tempTrailer, getActivity());
                                trailerTextVw.setText(tempTrailer);

                                CheckTrailerStatus();
                                Globally.EldScreenToast(saveInspectionBtn, obj.getString("Message"), getResources().getColor(R.color.colorPrimary));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            break;



                        case GetInspection:
                            try{
                                TruckInspList = new ArrayList<PrePostModel>();
                                TrailerInspList = new ArrayList<PrePostModel>();
                                TruckList   = new ArrayList<String>();
                                TrailerList = new ArrayList<String>();
                                TruckIdList = new ArrayList<Integer>();
                                TrailerIdList = new ArrayList<Integer>();


                                JSONObject dataObj = new JSONObject(obj.getString("Data"));
                                JSONObject InspectionObj = new JSONObject(dataObj.getString("Inspection"));

                                InspectionDateTime      = InspectionObj.getString("InspectionDateTime");
                                DriverTimeZone          = InspectionObj.getString("DriverTimeZone");

                               /* if(!InspectionObj.isNull("Latitude"))
                                    Latitude            = InspectionObj.getString("Latitude");

                                if(!InspectionObj.isNull("Longitude"))
                                    Longitude           = InspectionObj.getString("Longitude");
*/
                                date                    = Globally.ConvertDateTimeFormat(InspectionDateTime);


                                JSONArray TruckArray        = new JSONArray(dataObj.getString("TruckIssueList"));
                                JSONArray TrailerArray      = new JSONArray(dataObj.getString("TrailorIssueList"));

                                // Save Inspections issues list in local
                                SharedPref.setInspectionIssues(TruckArray.toString(), TrailerArray.toString(), getActivity());


                                // Save CT-PAT Inspections issues list in local
                                if(dataObj.has("SeventeenTruckList")){
                                    SharedPref.setCtPatInspectionIssues(dataObj.getString("SeventeenTruckList"), dataObj.getString("SeventeenTrailorList"), getActivity());
                                }


                                ParseInspectionIssues(TruckArray, TrailerArray);

                                inspectionDateTv.setText(date);


                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            break;

                        case GetObdAssignedVeh:
                            try {
                                inspectionProgressBar.setVisibility(View.GONE);
                                inspectionTruckLay.setEnabled(true);
                                vehicleList = new ArrayList<VehicleModel>();
                                JSONArray vehicleJsonArray = new JSONArray(obj.getString("Data"));

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

                                    vehicleList.add(vehicleModel);
                                }

                                if (vehicleList.size() > 0) {
                                    try {
                                        if (vehicleDialog != null && vehicleDialog.isShowing()) {
                                            vehicleDialog.dismiss();
                                        }

                                        vehicleDialog = new VehicleDialog(getActivity(), TruckNumber, false, vehicleList, new VehicleListener());
                                        vehicleDialog.show();

                                    } catch (final IllegalArgumentException e) {
                                        e.printStackTrace();
                                    } catch (final Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Globally.EldScreenToast(saveInspectionBtn, "No vehicles are available.", getResources().getColor(R.color.colorVoilation));
                                }
                            }catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;


                        case UpdateObdVeh:
                            Globally.EldScreenToast(saveInspectionBtn, "Updated successfully.", getResources().getColor(R.color.colorPrimary));

                            try {
                                String oldVinNumber = VIN_NUMBER;
                                JSONObject vehicleJsonObj = new JSONObject(obj.getString(ConstantsKeys.Data));
                                IsAOBRD             = vehicleJsonObj.getBoolean(ConstantsKeys.AOBRD);
                                IsAOBRDAutomatic    = vehicleJsonObj.getBoolean(ConstantsKeys.IsAOBRDAutomatic);
                                VIN_NUMBER          = vehicleJsonObj.getString(ConstantsKeys.VIN);

                                TruckNumber = vehicleJsonObj.getString(ConstantsKeys.EquipmentNumber);
                                VehicleId = vehicleJsonObj.getString(ConstantsKeys.VehicleId);

                                boolean isOdometerFromOBD = false;
                                String plateNo = "";

                                if(vehicleJsonObj.has(ConstantsKeys.OdometerFromOBD)){
                                    isOdometerFromOBD = vehicleJsonObj.getBoolean(ConstantsKeys.OdometerFromOBD);
                                }

                                if(vehicleJsonObj.has(ConstantsKeys.PlateNumber)){
                                    plateNo = vehicleJsonObj.getString(ConstantsKeys.PlateNumber);
                                }

                                SharedPref.SetIsAOBRD(IsAOBRD, getActivity());
                                SharedPref.SetAOBRDAutomatic(IsAOBRDAutomatic, getActivity());
                                SharedPref.SetAOBRDAutoDrive(vehicleJsonObj.getBoolean(ConstantsKeys.IsAutoDriving), getActivity());
                                SharedPref.SetOdometerFromOBD(isOdometerFromOBD, getActivity());
                                SharedPref.setCurrentTruckPlateNo(plateNo, getActivity());
                                SharedPref.setVehicleId(VehicleId, getActivity());
                                SharedPref.setVINNumber(VIN_NUMBER,  getActivity());
                                SharedPref.setLastSavedVINNumber(VIN_NUMBER, getActivity());
                                SharedPref.setTruckNumber(TruckNumber, getActivity());

                                powerInspectionTV.setText(TruckNumber);
                                if(!oldVinNumber.equals(VIN_NUMBER)){
                                    constants.resetObdValues(getActivity());
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }






                            break;


                        case GetAddFromLatLng:
                            if (!obj.isNull("Data")) {
                                try {
                                    JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                                    City        = dataJObject.getString(ConstantsKeys.City);
                                    State       = dataJObject.getString(ConstantsKeys.State);
                                    Country     = dataJObject.getString(ConstantsKeys.Country);
                                    Location    = dataJObject.getString(ConstantsKeys.Location);

                                    if (Country.contains("China") || Country.contains("Russia") || Country.contains("null")) {
                                        if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
                                            Location = csvReader.getShortestAddress(getActivity());
                                        }else{
                                            Location = Globally.LATITUDE + "," + Globally.LONGITUDE;
                                        }

                                        changeLocBtn.setVisibility(View.VISIBLE);
                                    }/*else {
                                        if (!Location.contains(Country)) {
                                            Location = dataJObject.getString(ConstantsKeys.Location) + ", " + Country;
                                        }
                                    }*/

                                    locInspectionTV.setText(Location);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                            break;

                    }


                }else{
                    inspectionProgressBar.setVisibility(View.GONE);
                    inspectionTruckLay.setEnabled(true);
                    if(!obj.isNull("Message")){
                        try {
                            if(obj.getString("Message").equals("Device Logout") && EldFragment.DriverJsonArray.length() == 0){
                                Globally.ClearAllFields(getActivity());
                                Globally.StopService(getActivity());
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                getActivity().startActivity(i);
                                getActivity().finish();
                            }else{
                                if(flag == UpdateObdVeh){
                                    Globally.EldScreenToast(saveInspectionBtn, obj.getString("Message"), getResources().getColor(R.color.colorSleeper));
                                }else {
                                    Globally.hideSoftKeyboard(getActivity());
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            TabAct.host.setCurrentTab(0);
                                        }
                                    }, 200);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }



        }
    };

    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            switch (flag){
                default:
                    try {
                        inspectionProgressBar.setVisibility(View.GONE);
                        inspectionTruckLay.setEnabled(true);
                        Globally.EldScreenToast(saveInspectionBtn, Globally.CONNECTION_ERROR , getResources().getColor(R.color.colorVoilation));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };





    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveInspectionResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, JSONArray inputData) {
            Logger.LogDebug("InspectionLog", "---Response Inspection: " + response);
            pDialog.dismiss();
            Globally.hideSoftKeyboard(getActivity());
            locInspectionTV.setEnabled(false);

            try {
                JSONObject obj = new JSONObject(response);
                String Message = obj.getString("Message");

                if (obj.getBoolean("Status")) {
                    if(Message.equals(getResources().getString(R.string.data_saved_successfully))){
                        // Clear all unposted Array from list......
                        inspectionMethod.DriverOfflineInspectionsHelper(Integer.valueOf(DRIVER_ID), dbHelper, new JSONArray());
                        ClearFields();

                        if(flag != 102) {
                            Globally.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.inspection_saved_successfully), getResources().getColor(R.color.colorPrimary));

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    TabAct.host.setCurrentTab(0);
                                }
                            }, 1300);
                        }
                    }else{
                        saveInspectionBtn.setEnabled(true);
                        Globally.EldToastWithDuration(TabAct.sliderLay, Message, getResources().getColor(R.color.colorSleeper));

                    }
                }else{
                    if(flag != 102) {
                        Globally.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.inspection_willbe_saved), getResources().getColor(R.color.colorSleeper));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TabAct.host.setCurrentTab(0);
                            }
                        }, 1200);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Logger.LogDebug("errorrr ", ">>>error dialog: ");
            Globally.hideSoftKeyboard(getActivity());
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            if(flag != 102) {
                ClearFields();
                locInspectionTV.setEnabled(false);
                Globally.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.inspection_willbe_saved), getResources().getColor(R.color.colorSleeper));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TabAct.host.setCurrentTab(0);
                    }
                }, 1200);
            }
        }
    };


}
