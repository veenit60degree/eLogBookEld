package com.messaging.logistic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adapter.logistic.SavedInspectionTitleAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.custom.dialogs.DatePickerDialog;
import com.driver.details.DriverConst;
import com.local.db.CTPatInspectionMethod;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.InspectionMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.models.CtPatInspectionModel;
import com.models.SavedInspectionModel;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InspectionsHistoryFragment extends Fragment implements View.OnClickListener{

    View rootView;
    ArrayList<String> TruckList, TrailerList;
    // public static int inspectionLayHeight = 0;
    TextView dateActionBarTV, EldTitleTV, inspectionDateTv, noDataInspectTV;
    TextView trailerTextVw;
    RelativeLayout eldMenuLay, rightMenuBtn;
    ImageView  eldMenuBtn, previousDateBtn, nextDateBtn;
    String  DRIVER_ID = "", DeviceId = "", CurrentCycleId = "", InspectionDateTime = "", CurrentDate = "", inspectionType = "";
    int UsaMaxDays   = 7;
    int CanMaxDays   = 14;
    int MaxDays;
    ScrollView inspectionScrollView;
    ListView inspectionListView;
    VolleyRequest GetInspectionsRequest;
    Map<String,String> params;
    Constants constant;
    DatePickerDialog dateDialog;
    ProgressBar inspectionProgressBar;
    public static List<SavedInspectionModel> savedInspectionList = new ArrayList<>();
    public static List<CtPatInspectionModel> savedCtPatInspectionList = new ArrayList<>();
    SavedInspectionTitleAdapter inspectionAdapter;
    boolean isTablet, isDOT ;
    DBHelper dbHelper;
    HelperMethods hMethods;
    InspectionMethod inspectionMethod;
    CTPatInspectionMethod ctPatInspMethod;
    JSONArray savedInspectionArray  = new JSONArray();
    JSONArray selectedDateArray     = new JSONArray();
    Globally global;
    SharedPref sharedPref;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.inspection_fragment, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }



    void initView(View view){

        sharedPref              = new SharedPref();
        global                  = new Globally();
        dbHelper                = new DBHelper(getActivity());
        hMethods                = new HelperMethods();
        inspectionMethod        = new InspectionMethod();
        ctPatInspMethod         = new CTPatInspectionMethod();

        constant                = new Constants();
        GetInspectionsRequest   = new VolleyRequest(getActivity());

        inspectionProgressBar   = (ProgressBar)view.findViewById(R.id.inspectionProgressBar);
        eldMenuLay              = (RelativeLayout)view.findViewById(R.id.eldMenuLay);

        rightMenuBtn            = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        eldMenuBtn              = (ImageView)view.findViewById(R.id.eldMenuBtn);
        previousDateBtn         = (ImageView)view.findViewById(R.id.previousDate);
        nextDateBtn             = (ImageView)view.findViewById(R.id.nextDateBtn);

        dateActionBarTV         = (TextView)view.findViewById(R.id.dateActionBarTV);
        EldTitleTV              = (TextView)view.findViewById(R.id.EldTitleTV);
        inspectionDateTv        = (TextView)view.findViewById(R.id.inspectionDateTv);
        trailerTextVw           = (TextView)view.findViewById(R.id.trailerTextVw);
        noDataInspectTV         = (TextView)view.findViewById(R.id.noDataInspectTV);


        inspectionScrollView    = (ScrollView)view.findViewById(R.id.inspectionScrollView);
        inspectionListView      = (ListView)  view.findViewById(R.id.inspectionListView);

        eldMenuBtn.setImageResource(R.drawable.back_btn);

        inspectionListView.setVisibility(View.VISIBLE);
        inspectionScrollView.setVisibility(View.GONE);
        dateActionBarTV.setVisibility(View.VISIBLE);
        rightMenuBtn.setVisibility(View.GONE);

        TruckList           = new ArrayList<String>();
        TrailerList         = new ArrayList<String>();
        DRIVER_ID           = sharedPref.getDriverId( getActivity());

        isTablet            = Globally.isTablet(getActivity());
        DeviceId            = sharedPref.GetSavedSystemToken(getActivity());
        isDOT               = sharedPref.IsDOT(getActivity());

        Globally.getBundle  = this.getArguments();
        InspectionDateTime  = Globally.getBundle.getString("date");
        inspectionType      = Globally.getBundle.getString("inspection_type");
        dateActionBarTV.setText(InspectionDateTime);

        CurrentCycleId      = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());
        CurrentDate         = Globally.GetCurrentDeviceDate();

        if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS) ) {
            MaxDays = UsaMaxDays;
        }else{
            MaxDays = CanMaxDays;
        }


        // Get Local Saved Data
        GetLocalInspectionArray();


        // Check from server
        if(Globally.isConnected(getActivity()) ) {
            if (inspectionType.equals("pti")){
                GetSavedInspection(DRIVER_ID, DeviceId, InspectionDateTime);
            }
        }

        //if(isDOT){
            previousDateBtn.setVisibility(View.VISIBLE);
            nextDateBtn.setVisibility(View.VISIBLE);

            VisibleHideNextPrevView("");
      //  }

        inspectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoveFragment(position);

            }
        });

        eldMenuLay.setOnClickListener(this);
        dateActionBarTV.setOnClickListener(this);
        previousDateBtn.setOnClickListener(this);
        nextDateBtn.setOnClickListener(this);
        EldTitleTV.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.dateActionBarTV:
                ShowDateDialog();
                break;

            case R.id.eldMenuLay:
                getFragmentManager().popBackStack();
                break;

            case R.id.previousDate:
                VisibleHideNextPrevView("prev");
                OnDateSelectionView(InspectionDateTime);
                break;

            case R.id.nextDateBtn:
                VisibleHideNextPrevView("next");
                OnDateSelectionView(InspectionDateTime);
                break;

            case R.id.EldTitleTV:

                break;
        }
    }


    void VisibleHideNextPrevView(String click){
        String selectedDateStr = global.ConvertDateFormat(InspectionDateTime);
        String currentDateStr = global.ConvertDateFormat(CurrentDate);
        DateTime selectedDateTime = new DateTime(global.getDateTimeObj(selectedDateStr, false) );
        DateTime currentDateTime = new DateTime(global.getDateTimeObj(currentDateStr, false) );

        if(click.equals("next")){
            selectedDateTime = selectedDateTime.plusDays(1);
            InspectionDateTime = global.ConvertDateFormatMMddyyyy(selectedDateTime.toString());
        }else if(click.equals("prev")){
            selectedDateTime = selectedDateTime.minusDays(1);
            InspectionDateTime = global.ConvertDateFormatMMddyyyy(selectedDateTime.toString());
        }

        int DaysDiff = hMethods.DayDiff(currentDateTime, selectedDateTime);
        DOTBtnVisibility(DaysDiff, MaxDays);
    }

    private void DOTBtnVisibility(int DaysDiff, int MaxDays){

        if(DaysDiff == 0){
            nextDateBtn.setVisibility(View.GONE);
            previousDateBtn.setVisibility(View.VISIBLE);
        }else if(DaysDiff == MaxDays){
            previousDateBtn.setVisibility(View.GONE);
            nextDateBtn.setVisibility(View.VISIBLE);
        }else{
            nextDateBtn.setVisibility(View.VISIBLE);
            previousDateBtn.setVisibility(View.VISIBLE);
        }

       /* if(!isDOT){
            previousDateBtn.setVisibility(View.GONE);
            nextDateBtn.setVisibility(View.GONE);
        }*/


    }


    void ShowDateDialog(){
        try {
            if (dateDialog != null && dateDialog.isShowing())
                dateDialog.dismiss();

            dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, InspectionDateTime, new DateListener());
            dateDialog.show();
        }catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    private void MoveFragment(int position){
        InspectionDetailView savedInspectionFragment = new InspectionDetailView();
        CtPatDetailInspection ctPatDetailInspectionFragment = new CtPatDetailInspection();

        try {
            Globally.bundle.putString("selectedObj", selectedDateArray.get(position).toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        Globally.bundle.putInt("position", position);

        savedInspectionFragment.setArguments(Globally.bundle);
        ctPatDetailInspectionFragment.setArguments(Globally.bundle);

        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out, android.R.anim.fade_in,android.R.anim.fade_out);

        if (inspectionType.equals("pti")) {
            fragmentTran.replace(R.id.job_fragment, savedInspectionFragment);
        }else{
            fragmentTran.replace(R.id.job_fragment, ctPatDetailInspectionFragment);
        }

        fragmentTran.addToBackStack(null);
        fragmentTran.commit();


    }






    private ArrayList<String> ParseListData(JSONArray array){
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0 ; i < array.length() ; i++){
            try {
                JSONObject truckObj = (JSONObject)array.get(i);
                if(truckObj.has("IssueName")){
                    list.add(truckObj.getString("IssueName"));
                }else {
                    if (truckObj.getBoolean("Selected"))
                        list.add(truckObj.getString("Text"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    private String CheckStringIsNull(JSONObject InspectionObj, String keyValue){
        String value = "";
        try {
            if(!InspectionObj.isNull(keyValue)) {
                value = InspectionObj.getString(keyValue);
            }else{
                value = "N/A";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
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
            InspectionDateTime = SelectedDate;
            VisibleHideNextPrevView("");
            OnDateSelectionView(SelectedDate);

        }
    }



    private void OnDateSelectionView(String SelectedDate){
        InspectionDateTime  = SelectedDate;
        dateActionBarTV.setText(SelectedDate);

        // Get Local Saved Data
        GetLocalInspectionArray();

        if (inspectionType.equals("pti")) {
            // Get data from Server
            if (Globally.isConnected(getActivity())) {
                GetSavedInspection(DRIVER_ID, DeviceId, SelectedDate);
            }
        }
    }


    void resetData(){
        trailerTextVw.setText("N/A");
        inspectionDateTv.setText(InspectionDateTime);
        dateActionBarTV.setText(InspectionDateTime);
        TruckList   = new ArrayList<String>();
        TrailerList = new ArrayList<String>();

    }


    //*================== Get Saved Inspection ===================*//*
    void GetSavedInspection(final String DriverId, final String DeviceId, final String DateTime){

        //   inspectionProgressBar.setVisibility(View.VISIBLE);

        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("DeviceId", DeviceId);
        params.put("Inspection.InspectionDateTime", DateTime );

        GetInspectionsRequest.executeRequest(Request.Method.POST, APIs.GET_SAVED_INSPECTION, params, 1,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);


    }


    String AddHeaderInList(int type){
        String header = "";
        switch (type){
            case 1:
                header = "Pre Trip Inspection";
                break;

            case 2:
                header =  "Post Trip Inspection";
                break;

            default:
                header = "Trailer Inspection";
                break;

        }
        return  header;
    }


    private void ParseInspectionArray(JSONObject dataObj, boolean isLocal){
        try {

            savedInspectionList = new ArrayList<>();
            savedCtPatInspectionList = new ArrayList<>();

            JSONArray InspectionArray;

            if(isLocal){
                InspectionArray = selectedDateArray;
            }else {
                InspectionArray = new JSONArray(dataObj.getString("InspectionList"));
            }

            for(int i = 0 ; i < InspectionArray.length() ; i++){
                JSONObject inspectItemObj = (JSONObject)InspectionArray.get(i);

                JSONArray TruckArray        = new JSONArray(inspectItemObj.getString(ConstantsKeys.InspectionTruckIssueType));
                JSONArray TrailerArray      = new JSONArray(inspectItemObj.getString(ConstantsKeys.InspectionTrailorIssueType));
                TruckList   = ParseListData(TruckArray);
                TrailerList = ParseListData(TrailerArray);
                String VehicleEquNumber = "", TrailorEquNumber = "", VIN = "";

                int InspectionTypeId = 1;
                JSONObject inspectionItemObj = new JSONObject(inspectItemObj.getString(ConstantsKeys.Inspection));
                if(!inspectionItemObj.isNull(ConstantsKeys.InspectionTypeId))
                    InspectionTypeId = inspectionItemObj.getInt(ConstantsKeys.InspectionTypeId);

                String driverSign = "", supervisorSign = "", CreatedDate = "", InspecDateTime = InspectionDateTime;
                if(inspectionItemObj.has(ConstantsKeys.DriverSignature)){
                    driverSign = inspectionItemObj.getString(ConstantsKeys.DriverSignature);
                }else if(inspectionItemObj.has(ConstantsKeys.ByteDriverSign)){
                    driverSign = inspectionItemObj.getString(ConstantsKeys.ByteDriverSign);
                }

                if(inspectionItemObj.has(ConstantsKeys.SupervisorMechanicsSignature)){
                    supervisorSign = inspectionItemObj.getString(ConstantsKeys.SupervisorMechanicsSignature);
                }else if(inspectionItemObj.has(ConstantsKeys.ByteSupervisorSign)){
                    supervisorSign = inspectionItemObj.getString(ConstantsKeys.ByteSupervisorSign);
                }

                if(inspectionItemObj.has(ConstantsKeys.CreatedDate)){
                    CreatedDate = inspectionItemObj.getString(ConstantsKeys.CreatedDate);
                }

                if(inspectionItemObj.has(ConstantsKeys.InspectionDateTime)){
                    CreatedDate = Globally.InspectionDateTimeFormat(inspectionItemObj.getString(ConstantsKeys.InspectionDateTime));
                    InspecDateTime = CreatedDate;
                }

                VehicleEquNumber = CheckStringIsNull(inspectionItemObj, ConstantsKeys.VehicleEquNumber);
                TrailorEquNumber = CheckStringIsNull(inspectionItemObj, ConstantsKeys.TrailorEquNumber);
                VIN              = CheckStringIsNull(inspectionItemObj, ConstantsKeys.VIN);

                boolean PreTripInspectionSatisfactory = false, PostTripInspectionSatisfactory = false, AboveDefectsCorrected = false, AboveDefectsNotCorrected = false;
                String SupervisorMechanicsName = "", Remarks = "";
                String arrivalSealNumber = "", departureSealNumber = "";
                String securityInspectionPersonName = "", followUpInspectionPersonName = "", affixedSealPersonName = "",  verificationPersonName = "";
                String byteInspectionConductorSign = "", byteFollowUpConductorSign = "", byteSealFixerSign = "", byteSealVerifierSign = "";

                if (inspectionType.equals("pti")) {
                    PreTripInspectionSatisfactory = inspectionItemObj.getBoolean(ConstantsKeys.PreTripInspectionSatisfactory);
                    PostTripInspectionSatisfactory = inspectionItemObj.getBoolean(ConstantsKeys.PostTripInspectionSatisfactory);
                    AboveDefectsCorrected = inspectionItemObj.getBoolean(ConstantsKeys.AboveDefectsCorrected);
                    AboveDefectsNotCorrected = inspectionItemObj.getBoolean(ConstantsKeys.AboveDefectsNotCorrected);

                    SupervisorMechanicsName = inspectionItemObj.getString(ConstantsKeys.SupervisorMechanicsName);
                    Remarks = inspectionItemObj.getString(ConstantsKeys.Remarks);
                }else{
                    arrivalSealNumber = inspectionItemObj.getString(ConstantsKeys.ArrivalSealNumber);
                    departureSealNumber = inspectionItemObj.getString(ConstantsKeys.DepartureSealNumber);
                    securityInspectionPersonName = inspectionItemObj.getString(ConstantsKeys.SecurityInspectionPersonName);
                    followUpInspectionPersonName = inspectionItemObj.getString(ConstantsKeys.FollowUpInspectionPersonName);
                    affixedSealPersonName = inspectionItemObj.getString(ConstantsKeys.AffixedSealPersonName);
                    verificationPersonName = inspectionItemObj.getString(ConstantsKeys.VerificationPersonName);

                    byteInspectionConductorSign = getByteImage(inspectionItemObj, ConstantsKeys.SecurityInspectionPersonSignature, ConstantsKeys.ByteInspectionConductorSign);
                    byteFollowUpConductorSign = getByteImage(inspectionItemObj, ConstantsKeys.FollowUpInspectionPersonSignature, ConstantsKeys.ByteFollowUpConductorSign);
                    byteSealFixerSign = getByteImage(inspectionItemObj, ConstantsKeys.AffixedSealPersonSignature, ConstantsKeys.ByteSealFixerSign);
                    byteSealVerifierSign = getByteImage(inspectionItemObj, ConstantsKeys.VerificationPersonSignature, ConstantsKeys.ByteSealVerifierSign);

                    CtPatInspectionModel ctPatModel = new CtPatInspectionModel("","","", "", "", "",
                            VIN, VehicleEquNumber, TrailorEquNumber, InspecDateTime, arrivalSealNumber, departureSealNumber,
                            securityInspectionPersonName, followUpInspectionPersonName, affixedSealPersonName,  verificationPersonName, "", "",
                            TruckArray.toString(), TrailerArray.toString(), byteInspectionConductorSign, byteFollowUpConductorSign,
                            byteSealFixerSign, byteSealVerifierSign);

                    savedCtPatInspectionList.add(ctPatModel);


                }


                SavedInspectionModel inspectModel = new SavedInspectionModel(
                        AddHeaderInList(InspectionTypeId),
                        VIN,
                        VehicleEquNumber,
                        TrailorEquNumber,
                        InspecDateTime,
                        CheckStringIsNull(inspectionItemObj, "Location"),

                        PreTripInspectionSatisfactory,
                        PostTripInspectionSatisfactory,
                        AboveDefectsCorrected,
                        AboveDefectsNotCorrected,

                        Remarks,
                        driverSign,
                        SupervisorMechanicsName,
                        supervisorSign,
                        CreatedDate,

                        InspectionTypeId,
                        "", "",

                        TruckList,
                        TrailerList

                );
                savedInspectionList.add(inspectModel);
            }

            inspectionAdapter = new SavedInspectionTitleAdapter(getActivity(), inspectionType, savedInspectionList );
            inspectionListView.setAdapter(inspectionAdapter);

            if(savedInspectionList.size() > 0){
                noDataInspectTV.setVisibility(View.GONE);
            }else{
                noDataInspectTV.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            noDataInspectTV.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }
    }



    private String getByteImage(JSONObject obj, String key, String localKey){
        String image = "";
        try {
            if(obj.has(key)){
                String[] array = obj.getString(key).split("@@@");
                if(array.length > 1){
                    image = array[1];
                }
            }else {
                image = obj.getString(localKey);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return image;
    }



    private void GetLocalInspectionArray(){

        if (inspectionType.equals("pti")) {
            savedInspectionArray = inspectionMethod.getSavedInspectionArray(Integer.valueOf(DRIVER_ID), dbHelper);
            EldTitleTV.setText(getResources().getString(R.string.SavedPrePostTripIns));
        }else{
            savedInspectionArray = ctPatInspMethod.getCtPat18DaysInspectionArray(Integer.valueOf(DRIVER_ID), dbHelper);
            EldTitleTV.setText(getResources().getString(R.string.SavedCtPatIns));
        }

        selectedDateArray = new JSONArray();
        JSONObject itemObj = new JSONObject();

        for(int i = 0 ; i < savedInspectionArray.length() ; i++){
            try {
                itemObj = (JSONObject)savedInspectionArray.get(i);
                JSONObject inspJson = new JSONObject(itemObj.getString("Inspection"));
                String selectedDate = Globally.ConvertInspectionsDateFormat(inspJson.getString("InspectionDateTime"));
                if(InspectionDateTime.equals(selectedDate)){
                    selectedDateArray.put(itemObj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        ParseInspectionArray(itemObj, true);
    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @Override
        public void getResponse(String response, int flag) {

            Log.d("response", "Driver response: " + response);
            String status = "";
            inspectionProgressBar.setVisibility(View.GONE);

            JSONObject objJson;
            try {
                objJson = new JSONObject(response);
                status = objJson.getString("Status");

                if(status.equalsIgnoreCase("true")){
                    TruckList   = new ArrayList<String>();
                    TrailerList = new ArrayList<String>();

                    JSONObject dataObj = new JSONObject(objJson.getString("Data"));

                    JSONObject InspectionObj = new JSONObject(dataObj.getString("Inspection"));

                    String  CreatedDate = CheckStringIsNull(InspectionObj, "CreatedDate");

                    try {
                        if(!CreatedDate.equals("N/A")) {
                            CreatedDate = Globally.ConvertDateTimeFormat(CreatedDate);
                            if(CreatedDate.equals("")){
                                CreatedDate = InspectionDateTime;
                            }
                        }else {
                            CreatedDate = InspectionDateTime;
                        }
                    }catch (Exception e){
                        CreatedDate = InspectionDateTime;
                    }


                    ParseInspectionArray(dataObj, false);

                    inspectionDateTv.setText(CreatedDate);
                    dateActionBarTV.setText(CreatedDate);


                }else{
                    inspectionProgressBar.setVisibility(View.GONE);
                    if(!objJson.isNull("Message")){
                        try {
                            Globally.EldScreenToast(eldMenuBtn, objJson.getString("Message"), getResources().getColor(R.color.colorVoilation));

                            if(objJson.getString("Message").equals("Device Logout")  && constant.GetDriverSavedArray(getActivity()).length() == 0){
                                Globally.ClearAllFields(getActivity());
                                Globally.StopService(getActivity());
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                getActivity().startActivity(i);
                                getActivity().finish();
                            }else{
                                resetData();

                                savedInspectionList = new ArrayList<>();
                                inspectionAdapter = new SavedInspectionTitleAdapter(getActivity(), inspectionType, savedInspectionList );
                                inspectionListView.setAdapter(inspectionAdapter);
                                noDataInspectTV.setVisibility(View.VISIBLE);

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


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

        @Override
        public void getError(VolleyError error, int flag) {

            if(getActivity() != null) {
                Log.d("Driver", "error" + error.toString());

                resetData();
                inspectionProgressBar.setVisibility(View.GONE);
                Globally.EldScreenToast(eldMenuBtn, Globally.CONNECTION_ERROR, getResources().getColor(R.color.colorVoilation));

            }
        }
    };

}
