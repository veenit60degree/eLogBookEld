package com.messaging.logistic.fragment;

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
import android.util.Log;
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
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.CsvReader;
import com.constants.DriverLogResponse;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.Slidingmenufunctions;
import com.constants.VolleyRequest;
import com.custom.dialogs.DatePickerDialog;
import com.custom.dialogs.SignDialog;
import com.custom.dialogs.TrailorDialog;
import com.custom.dialogs.VehicleDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.InspectionMethod;
import com.local.db.ShipmentHelperMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.models.DriverLocationModel;
import com.models.PrePostModel;
import com.models.VehicleModel;
import com.shared.pref.StatePrefManager;
import com.simplify.ink.InkView;

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
    CheckBox checkboxTrailer, checkboxTruck;
    GridView truckGridView, trailerGridView;
    GridAdapter truckAdapter, trailerAdapter;
    List<PrePostModel> TruckInspList = new ArrayList<PrePostModel>();
    List<PrePostModel> TrailerInspList = new ArrayList<PrePostModel>();
    ArrayList<String> TruckList = new ArrayList<String>();
    ArrayList<String> TrailerList = new ArrayList<String>();
    ArrayList<Integer> TruckIdList = new ArrayList<Integer>();
    ArrayList<Integer> TrailerIdList = new ArrayList<Integer>();

    TextView dateActionBarTV, EldTitleTV, inspectionDateTv, powerInspectionTV, noDefectLabel, locInspTitleTV, trailerTextVw;
    public static TextView trailerInspectionTV;
    EditText remarksEditText, SupervisorNameTV, cityEditText;
    AutoCompleteTextView locInspectionTV;
    Spinner stateInspSpinner;
    String EldThemeColor = "#1A3561";
    String BlackColor    = "#7C7C7B";
    RadioGroup prePostRadioGroup, correctRadioGroup;
    RadioButton preTripButton, postTripButton, DefectsCorrectedBtn, DefectsNotCorrectedBtn;
    LinearLayout supervisorNameLay, AobrdLocLay;
    LinearLayout inspectionTrailerLay, inspectionTruckLay, insptnMainLay;
    RelativeLayout rightMenuBtn, eldMenuLay, superviserSignLay, DriverSignLay, truckTrailerTVLay, truckTrailerLayout;
    RelativeLayout inspectTrailerTitleLay, trailerGridLay;
    Button changeLocBtn, saveInspectionBtn;
    ImageView signSuprvsrIV, signDriverIV;
    ProgressBar inspectionProgressBar;
    String btnSelectedType = "", SignImageSelected = "", SupervisorSignImage = "", DriverSignImage = "";
    String  DRIVER_ID = "", VIN_NUMBER = "", DeviceId = "";
    String DriverName = "",CompanyId = "", InspectionDateTime = "", Location = "", PreTripInsp = "false", PostTripInsp = "false",
            AboveDefectsCorrected = "false", AboveDefectsNotCorrected = "false", Remarks = "",Latitude = "", Longitude = "",
            SupervisorMechanicsName = "", TruckIssueType = "", TraiorIssueType = "", DriverTimeZone = "", date = "", InspectionTypeId="";
    String City = "", State = "", Country = "", CurrentCycleId = "", CurrentJobStatus = "";
    String DriverId = "", CoDriverId = "", tempTruck = "", tempTrailer = "", CreatedDate = "";
    String ByteDriverSign = "", ByteSupervisorSign = "", SelectedDatee = "";
    SignDialog signDialog;
    ScrollView inspectionScrollView;
    TextView inspectionTypeTV;
    List<String> StateArrayList;
    List<DriverLocationModel> StateList;
    StatePrefManager statePrefManager;
    RequestQueue queue;
    DatePickerDialog dateDialog;
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
    Constants constants;
    CsvReader csvReader;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        dbHelper = new DBHelper(getActivity());
        SaveTrailerNumber = new VolleyRequest(getActivity());
        GetInspectionRequest = new VolleyRequest(getActivity());
        GetOBDVehRequest = new VolleyRequest(getActivity());
        SaveOBDVehRequest = new VolleyRequest(getActivity());
        GetAddFromLatLngRequest = new VolleyRequest(getActivity());
        saveInspectionPost = new SaveDriverLogPost(getActivity(), saveInspectionResponse);

        queue = Volley.newRequestQueue(getActivity());
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

        trailerTextVw = (TextView) view.findViewById(R.id.trailerTextVw);
        trailerInspectionTV = (TextView) view.findViewById(R.id.trailerInspectionTV);
        dateActionBarTV = (TextView) view.findViewById(R.id.dateActionBarTV);
        EldTitleTV = (TextView) view.findViewById(R.id.EldTitleTV);
        inspectionDateTv = (TextView) view.findViewById(R.id.inspectionDateTv);
        powerInspectionTV = (TextView) view.findViewById(R.id.powerInspectionTV);
        noDefectLabel = (TextView) view.findViewById(R.id.noDefectLabel);
        locInspTitleTV = (TextView) view.findViewById(R.id.locInspTitleTV);

        checkboxTrailer = (CheckBox) view.findViewById(R.id.checkboxTrailer);
        checkboxTruck = (CheckBox) view.findViewById(R.id.checkboxTruck);

        stateInspSpinner = (Spinner) view.findViewById(R.id.stateInspSpinner);
        inspectionScrollView = (ScrollView) view.findViewById(R.id.inspectionScrollView);

        dateActionBarTV.setVisibility(View.VISIBLE);
        rightMenuBtn.setVisibility(View.GONE);

        dateActionBarTV.setText(Html.fromHtml("<b><u>" + getResources().getString(R.string.view_inspections) + "</u></b>"));
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.whiteee));

        locInspectionTV.setThreshold(3);
        SelectedDatee = Globally.GetCurrentDeviceDate();
        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            EldThemeColor = "#ffffff";
            insptnMainLay.setBackgroundColor(getResources().getColor(R.color.gray_background) );
            // superviserSignLay.setBackgroundColor(Color.parseColor("#E2E2E2") );

        }

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

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    inspectionScrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            }, 500);

        }catch (Exception e){
            e.printStackTrace();
        }

        // Get Inspection Details
        if (Globally.isConnected(getActivity())) {
            DRIVER_ID           = SharedPref.getDriverId( getActivity());
            DeviceId            = SharedPref.GetSavedSystemToken(getActivity());
            VIN_NUMBER          = SharedPref.getVINNumber(getActivity());
            GetInspectionDetail(DRIVER_ID, DeviceId, Globally.PROJECT_ID, VIN_NUMBER);
        }

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
        CurrentCycleId      = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());

        DRIVER_ID           = SharedPref.getDriverId( getActivity());
        VIN_NUMBER          = SharedPref.getVINNumber(getActivity());
        IsAOBRDAutomatic    = SharedPref.IsAOBRDAutomatic(getActivity());
        DeviceId            = SharedPref.GetSavedSystemToken(getActivity());
        CreatedDate         = Globally.GetCurrentDeviceDateTime();
        IsAOBRD             = SharedPref.IsAOBRD(getActivity());

        DriverId            =  DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
        CoDriverId          =  DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());

        DriverName          = slideMenu.usernameTV.getText().toString();

        Globally.TRAILOR_NUMBER = SharedPref.getTrailorNumber(getActivity());
        powerInspectionTV.setText(Globally.TRUCK_NUMBER);
        trailerTextVw.setText(Globally.TRAILOR_NUMBER);
        saveInspectionBtn.setEnabled(true);

        CheckTrailerStatus();


        if(IsAOBRD && !IsAOBRDAutomatic){
           // Slidingmenufunctions.homeTxtView.setText("AOBRD - HOS");
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
                if(Globally.isConnected(getActivity()) == false)
                    locInspectionTV.setText(csvReader.getShortestAddress(getActivity()));

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

       /* if(IsAOBRD ) {
            Slidingmenufunctions.homeTxtView.setText("AOBRD - HOS");
        }else{
            Slidingmenufunctions.homeTxtView.setText("ELD - HOS");
        }*/


        if (!isOnCreate && Globally.isConnected(getActivity())) {
            if(TruckInspList.size() == 0 || TrailerInspList.size() == 0)
                GetInspectionDetail(DRIVER_ID, DeviceId, Globally.PROJECT_ID, VIN_NUMBER);
        }

        ShowHidePtiTrailerView();

        inspection18DaysArray  = inspectionMethod.getSavedInspectionArray(Integer.valueOf(DRIVER_ID), dbHelper);
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
                Location = csvReader.getShortestAddress(getActivity());
                changeLocBtn.setVisibility(View.VISIBLE);
            }
            if (Globally.isConnected(getActivity()) && Globally.LATITUDE.length() > 5) {
                GetAddFromLatLng(Globally.LATITUDE, Globally.LONGITUDE);
            }else{
                locInspectionTV.setText(csvReader.getShortestAddress(getActivity()));
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
        Log.d("data", "---data: " + data);
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

                    if(constants.isActionAllowed(getContext())) {
                        if (Globally.isConnected(getActivity())) {
                            dialog = new TrailorDialog(getActivity(), "trailor", false, Globally.TRAILOR_NUMBER, 0, false, Globally.onDutyRemarks, 0, dbHelper, new TrailorListener());
                            dialog.show();
                        } else {
                            Globally.EldScreenToast(saveInspectionBtn, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                        }
                    }else{
                        Globally.EldScreenToast(saveInspectionBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }

                }

                break;


            case R.id.inspectionTruckLay:

                CurrentJobStatus = SharedPref.getDriverStatusId(getActivity());

                if (CurrentJobStatus.equals(Globally.DRIVING )) {
                    Globally.EldScreenToast(saveInspectionBtn, ConstantsEnum.TRUCK_CHANGE, getResources().getColor(R.color.colorVoilation));
                }else {
                    if(constants.isActionAllowed(getContext())) {
                        if (Globally.isConnected(getActivity())) {
                            inspectionTruckLay.setEnabled(false);
                            inspectionProgressBar.setVisibility(View.VISIBLE);
                            GetOBDAssignedVehicles(DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()), DeviceId, CompanyId, VIN_NUMBER);
                        } else {
                            Globally.EldScreenToast(saveInspectionBtn, Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                        }
                    }else{
                        Globally.EldScreenToast(saveInspectionBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
                    }
                }

                break;


            case R.id.dateActionBarTV:
                ShowDateDialog();
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
                    trailerAdapter.notifyDataSetChanged();

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
                    truckAdapter.notifyDataSetChanged();

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
                // if(DriverSignImage.trim().length() == 0 ) {
                SignImageSelected = "driver";
                if (signDialog != null && signDialog.isShowing())
                    signDialog.dismiss();
                signDialog = new SignDialog(getActivity(), new SignListener());
                signDialog.show();
                // }
                break;

            case R.id.saveInspectionBtn:

                if(constants.isActionAllowed(getContext())) {
                    if(Globally.TRAILOR_NUMBER.length() > 0 || Globally.TRUCK_NUMBER.length() > 0) {
                        CheckInspectionValidation();
                    }else{
                        Globally.EldScreenToast(saveInspectionBtn, "Please update your Truck or Trailer number before save the inspections." , getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    Globally.EldScreenToast(saveInspectionBtn, getString(R.string.stop_vehicle_alert), getResources().getColor(R.color.colorVoilation));
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
        if(DriverSignImage.length() > 0 ) {
            getViewData();
            SaveInspectionOfflineWithAPI();
            //  new SaveDriverInspection().execute();
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
            if (City.length() > 0) {
                if(PreTripInsp.equals("true"))
                    InspectionTypeId = String.valueOf(Constants.PreInspection);
                else
                    InspectionTypeId = String.valueOf(Constants.PostInspection);

                CheckInspectValidationPart2();

            } else {
                inspectionScrollView.fullScroll(ScrollView.FOCUS_UP);
                cityEditText.requestFocus();
                Globally.EldScreenToast(saveInspectionBtn, "Enter city name.", getResources().getColor(R.color.colorVoilation));
            }
        } else {
            getLocation();
            if(PreTripInsp.equals("true"))
                InspectionTypeId = String.valueOf(Constants.PreInspection);
            else
                InspectionTypeId = String.valueOf(Constants.PostInspection);

            CheckInspectValidationPart2();

        }


    }

    void CheckInspectValidationPart2() {
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
            preTripButton.setTextColor(Color.parseColor(EldThemeColor));
            postTripButton.setTextColor(Color.parseColor(BlackColor));
        }else if(buttonType.equals("post")){
            PreTripInsp = "false"; PostTripInsp = "true"; //AboveDefectsCorrected = "false"; AboveDefectsNotCorrected = "false";
            postTripButton.setTextColor(Color.parseColor(EldThemeColor));
            preTripButton.setTextColor(Color.parseColor(BlackColor));
        }else if(buttonType.equals("corrected")){
            AboveDefectsCorrected = "true"; AboveDefectsNotCorrected = "false";  // PreTripInsp = "false"; PostTripInsp = "false";
            DefectsCorrectedBtn.setTextColor(Color.parseColor(EldThemeColor));
            DefectsNotCorrectedBtn.setTextColor(Color.parseColor(BlackColor));
        }else if(buttonType.equals("notCorrected")){
            AboveDefectsCorrected = "false"; AboveDefectsNotCorrected = "true";
            DefectsNotCorrectedBtn.setTextColor(Color.parseColor(EldThemeColor));
            DefectsCorrectedBtn.setTextColor(Color.parseColor(BlackColor));
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
        InspectionsHistoryFragment savedInspectionFragment = new InspectionsHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("inspection_type", "pti");
        savedInspectionFragment.setArguments(bundle);

        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, savedInspectionFragment);
        fragmentTran.addToBackStack("inspection");
        fragmentTran.commit();


    }

    void ShowDateDialog(){
        try {
            if (dateDialog != null && dateDialog.isShowing())
                dateDialog.dismiss();

            dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, SelectedDatee, new DateListener());
            dateDialog.show();
        }catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

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

        Constants.IS_TRAILER_INSPECT = false;
        noDefectLabel.setText("No defects");
        //noDefectLabel.setVisibility(View.VISIBLE);
        // prePostLay.setVisibility(View.VISIBLE);
        correctRadioGroup.setVisibility(View.GONE);
        supervisorNameLay.setVisibility(View.GONE);
        superviserSignLay.setVisibility(View.GONE);
        DriverSignImage     = "";
        SupervisorSignImage = "";
        ByteDriverSign = "";
        ByteSupervisorSign = "";

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


        ScrollUpView();


    }


    private class DateListener implements DatePickerDialog.DatePickerListener{
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

            tempTruck   = vehicleList.get(position).getEquipmentNumber();
            VIN_NUMBER = vehicleList.get(position).getVIN();
            SaveVehicleNumber(position, DriverId, CoDriverId, CompanyId, true);

        }

    }


    /*================== Signature Listener ====================*/
    private class SignListener implements SignDialog.SignListener{

        @Override
        public void SignOkBtn(InkView inkView, boolean IsSigned) {

            if(IsSigned) {
                if( SignImageSelected.equals("driver") ){
                    GetSignatureBitmap(inkView, signDriverIV);
                }else {
                    GetSignatureBitmap(inkView, signSuprvsrIV);
                }
            }else{
                if( SignImageSelected.equals("driver") ){
                    signDriverIV.setBackgroundDrawable(null);
                    DriverSignImage = "";
                }else {
                    signSuprvsrIV.setBackgroundDrawable(null);
                    SupervisorSignImage = "";
                }

            }


            signDialog.dismiss();
        }
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
        }, 350);
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

                inspectionScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 300);


    }


    //*================== Save Driver Inspection ===================*//*
    private void SaveInspectionOfflineWithAPI(){

        // disable temperory button click to avoid multiple clicks on button at the same time
        saveInspectionBtn.setEnabled(false);

        pDialog.show();

        // Convert image file into bytes
        ByteDriverSign = "";
        ByteSupervisorSign = "";

        File file = new File(DriverSignImage);
        if (file.exists()) {
            Log.i("", "---Add File: " + file.toString());
            ByteDriverSign = Globally.ConvertImageToByteAsString(DriverSignImage);
        }

        File f = new File(SupervisorSignImage);
        if (f.exists()) {
            Log.i("", "---Add File: " + f.toString());
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
        JSONObject inspectionData = inspectionMethod.AddNewInspectionObj(DRIVER_ID, DeviceId, Globally.PROJECT_ID, DriverName, CompanyId, EldFragment.VehicleId, "", VIN_NUMBER,
                Globally.TRUCK_NUMBER, Globally.TRAILOR_NUMBER, CreatedDate, Location, PreTripInsp, PostTripInsp, AboveDefectsCorrected, AboveDefectsNotCorrected,
                Remarks, Globally.LATITUDE, Globally.LONGITUDE, DriverTimeZone, SupervisorMechanicsName, TruckIssueType, TraiorIssueType, InspectionTypeId,
                ByteDriverSign, ByteSupervisorSign);

        // Add inspection JSON obj in 18 Days Array
        JSONArray reverseArray = shipmentHelperMethod.ReverseArray(inspection18DaysArray);
        JSONObject inspectionFor18DaysObj = inspectionMethod.Add18DaysObj(inspectionData, TruckList, TruckIdList, TrailerList, TrailerIdList);
        reverseArray.put(inspectionFor18DaysObj);

        // again reverse Array to show last item at top
        inspection18DaysArray = new JSONArray();
        inspection18DaysArray = shipmentHelperMethod.ReverseArray(reverseArray);
        inspectionMethod.DriverInspectionHelper(Integer.valueOf(DRIVER_ID), dbHelper, inspection18DaysArray);

        // Add inspection JSON obj in Offline Array
        JSONArray unPostedArray = inspectionMethod.getOfflineInspectionsArray(Integer.valueOf(DRIVER_ID), dbHelper);
        unPostedArray.put(inspectionData);
        inspectionMethod.DriverOfflineInspectionsHelper(Integer.valueOf(DRIVER_ID), dbHelper, unPostedArray);

        Constants.IS_TRAILER_INSPECT = false;
        if(Globally.isConnected(getActivity()) ){
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




    private void CheckTrailerStatus(){
        try{
            if(Globally.TRAILOR_NUMBER.equals(Constants.NoTrailer) || Globally.TRAILOR_NUMBER.trim().equals("")){
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

            Log.d("response", "Driver response: " + response);
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
                                Globally.TRAILOR_NUMBER = tempTrailer;
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

                                if(!InspectionObj.isNull("Latitude"))
                                    Latitude            = InspectionObj.getString("Latitude");

                                if(!InspectionObj.isNull("Longitude"))
                                    Longitude           = InspectionObj.getString("Longitude");

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

                                        vehicleDialog = new VehicleDialog(getActivity(), Globally.TRUCK_NUMBER, false, vehicleList, new VehicleListener());
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

                            Globally.TRUCK_NUMBER = tempTruck;
                            SharedPref.setVINNumber(VIN_NUMBER, getActivity());
                            EldFragment.VehicleId = vehicleList.get(VehListPosition).getVehicleId();
                            SharedPref.setVehicleId(EldFragment.VehicleId , getActivity());
                            powerInspectionTV.setText(tempTruck);

                            if(SharedPref.getDriverType(getContext()).equals(DriverConst.TeamDriver)) {
                                /*Save Trip Details */
                                Constants.SaveTripDetails(0, tempTruck , VIN_NUMBER, getActivity());
                                Constants.SaveTripDetails(1, tempTruck , VIN_NUMBER, getActivity());
                            }else{
                                Constants.SaveTripDetails(0, tempTruck , VIN_NUMBER, getActivity());
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
                                        Location = csvReader.getShortestAddress(getActivity());
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
                                Globally.hideSoftKeyboard(getActivity());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        TabAct.host.setCurrentTab(0);
                                    }
                                }, 200);

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
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, int inputDataLength) {
            Log.d("InspectionLog", "---Response Inspection: " + response);
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
            Log.d("errorrr ", ">>>error dialog: ");
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
