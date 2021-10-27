package com.messaging.logistic.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.adapter.logistic.MalfunctionAdapter;
import com.adapter.logistic.MalfunctionHistoryAdapter;
import com.constants.Constants;
import com.constants.SharedPref;
import com.custom.dialogs.DatePickerDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.MalfunctionDiagnosticMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.MalDiaEventModel;
import com.models.MalfunctionHeaderModel;
import com.models.MalfunctionModel;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MalfunctionDiagnosticHistoryFragment extends Fragment implements View.OnClickListener {


    View rootViewDia;
    RelativeLayout malfunctionActionBar, eldMenuLay, rightMenuBtn;
    ExpandableListView diagnosticExpandList;
    TextView noRecordTV, EldTitleTV, dateActionBarTV;
    ImageView eldMenuBtn, previousDateBtn, nextDateBtn;;
    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
    DBHelper dbHelper;
    HelperMethods hMethods;
    Constants constants;
    Globally globally;
    String EventDateTime = "", CurrentCycleId = "";
    int UsaMaxDays   = 7;
    int CanMaxDays   = 14;
    int MaxDays;

    List<MalfunctionModel> malfunctionChildList = new ArrayList<>();
    List<MalfunctionHeaderModel> diagnosticHeaderList = new ArrayList<>();
    private HashMap<String, List<MalfunctionModel>> diagnosticChildHashMap = new HashMap<>();

    String DriverId = "", VIN = "", Country, OffsetFromUTC, CompanyId;
    MalfunctionHeaderModel headerModel = null;
    DatePickerDialog dateDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootViewDia != null) {
            ViewGroup parent = (ViewGroup) rootViewDia.getParent();
            if (parent != null)
                parent.removeView(rootViewDia);
        }
        try {
            rootViewDia = inflater.inflate(R.layout.fragment_malfunction, container, false);
            rootViewDia.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootViewDia);

        return rootViewDia;
    }


    void initView(View view) {

        globally    = new Globally();
        constants   = new Constants();
        dbHelper    = new DBHelper(getActivity());
        hMethods    = new HelperMethods();

        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();

        malfunctionActionBar    = (RelativeLayout)view.findViewById(R.id.malfunctionActionBar);
        eldMenuLay              = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        rightMenuBtn            = (RelativeLayout)view.findViewById(R.id.rightMenuBtn);

        diagnosticExpandList    = (ExpandableListView)view.findViewById(R.id.malfunctionExpandList);
        noRecordTV              = (TextView)view.findViewById(R.id.noRecordTV);
        EldTitleTV              = (TextView) view.findViewById(R.id.EldTitleTV);
        dateActionBarTV         = (TextView) view.findViewById(R.id.dateActionBarTV);

        eldMenuBtn              = (ImageView)view.findViewById(R.id.eldMenuBtn);
        previousDateBtn         = (ImageView)view.findViewById(R.id.previousDate);
        nextDateBtn             = (ImageView)view.findViewById(R.id.nextDateBtn);

        eldMenuBtn.setImageResource(R.drawable.back_white);

        DriverId                = SharedPref.getDriverId( getActivity());
        VIN                     = SharedPref.getVINNumber(getActivity());
        Country                 = constants.getCountryName(getActivity());
        OffsetFromUTC           = DriverConst.GetDriverSettings(DriverConst.OffsetHours, getActivity());
        CompanyId               = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());


        EventDateTime = globally.GetCurrentDeviceDate();
        CurrentCycleId      = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());

        if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS) ) {
            MaxDays = UsaMaxDays;
        }else{
            MaxDays = CanMaxDays;
        }

        viewOfflineData(EventDateTime);
        previousDateBtn.setVisibility(View.VISIBLE);
        rightMenuBtn.setVisibility(View.GONE);
        dateActionBarTV.setVisibility(View.VISIBLE);

        dateActionBarTV.setText(EventDateTime);
        EldTitleTV.setText(getResources().getString(R.string.mal_dia_history) );

        previousDateBtn.setOnClickListener(this);
        nextDateBtn.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        dateActionBarTV.setOnClickListener(this);

    }


    void viewOfflineData(String selectedDate){
        try{
            selectedDate = globally.ConvertDateFormatyyyy_MM_dd(selectedDate);
            JSONArray malDiaArray = malfunctionDiagnosticMethod.getEventsDateWise(selectedDate, dbHelper);  //getMalDiaDurationArray(dbHelper);

            Log.d("malDiaArray", "malDiaArray: " + malDiaArray);

            try{
                diagnosticHeaderList        = new ArrayList<>();
                diagnosticChildHashMap      = new HashMap<>();
                malfunctionChildList        = new ArrayList<>();

                // Adding same type events in single group for Expandable ListView
                parseListInHashMap(malDiaArray, Constants.PowerComplianceDiagnostic);
                parseListInHashMap(malDiaArray, Constants.PowerComplianceMalfunction);

                parseListInHashMap(malDiaArray, Constants.EngineSyncDiagnosticEvent);
                parseListInHashMap(malDiaArray, Constants.EngineSyncMalfunctionEvent);

                parseListInHashMap(malDiaArray, Constants.MissingDataDiagnostic);
                parseListInHashMap(malDiaArray, Constants.PositionComplianceMalfunction);

                parseListInHashMap(malDiaArray, Constants.UnIdentifiedDrivingDiagnostic);



                notifyMalfunctionAdapter(noRecordTV, diagnosticExpandList, diagnosticHeaderList, diagnosticChildHashMap);

            }catch (Exception e){
                e.printStackTrace();
            }




        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void parseListInHashMap(JSONArray malDiaArray, String EventType){
        try{
            headerModel = null;
            malfunctionChildList        = new ArrayList<>();

            for(int  i = 0 ; i < malDiaArray.length() ; i++){

                JSONObject mainObj = (JSONObject)malDiaArray.get(i);
                String DetectionDataEventCode = mainObj.getString(ConstantsKeys.DetectionDataEventCode);

                if(EventType.equals(DetectionDataEventCode)) {
                    if (globally.isSingleDriver(getActivity())) {
                        parseData(mainObj, i);
                    } else {

                        String DrId = mainObj.getString(ConstantsKeys.DriverId);

                        if (DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction) ||
                                DetectionDataEventCode.equals(Constants.EngineSyncMalfunctionEvent) ||
                                DetectionDataEventCode.equals(Constants.PositionComplianceMalfunction)) {
                            parseData(mainObj, i);
                        } else {
                            if (DrId.equals(DriverId)) {
                                parseData(mainObj, i);
                            }
                        }

                    }
                }
            }

            // add data in header list
            if(headerModel != null) {
                diagnosticHeaderList.add(headerModel);
                diagnosticChildHashMap.put(EventType, malfunctionChildList);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseData(JSONObject mainObj, int position){
        try {

            String EventType = mainObj.getString(ConstantsKeys.DetectionDataEventCode);
            boolean IsClearEvent = mainObj.getBoolean(ConstantsKeys.IsClearEvent);

            if (IsClearEvent == true) {
                MalDiaEventModel eventModel = constants.getMalDiaEventDetails(getActivity(), EventType);

                headerModel = new MalfunctionHeaderModel(
                        eventModel.getEventTitle(), EventType, eventModel.getEventDesc(),
                        IsClearEvent, true, "" + malfunctionChildList.size());

                String driverEndTimeZone = "";
                DateTime EventDateTime = Globally.getDateTimeObj(mainObj.getString(ConstantsKeys.EventDateTime), false);
                String driverTimeZone = String.valueOf(EventDateTime.plusHours(Integer.parseInt(OffsetFromUTC)));


                if(mainObj.has(ConstantsKeys.EventEndDateTime) && mainObj.getString(ConstantsKeys.EventEndDateTime).length() > 15) {
                    DateTime EventEndDateTime = Globally.getDateTimeObj(mainObj.getString(ConstantsKeys.EventEndDateTime), false);
                    driverEndTimeZone = String.valueOf(EventEndDateTime.plusHours(Integer.parseInt(OffsetFromUTC)));
                }else{
                    if(EventType.equals(Constants.PowerComplianceDiagnostic) || EventType.equals(Constants.PowerComplianceMalfunction)){
                        driverEndTimeZone = driverTimeZone;
                    }
                }

                String EngHrs = "0", StartOdometer = "0", StartEngineHours = "0";
                if (mainObj.has(ConstantsKeys.ClearEngineHours)) {
                    EngHrs = mainObj.getString(ConstantsKeys.ClearEngineHours);
                }

                if (mainObj.has(ConstantsKeys.StartOdometer)) {
                    StartOdometer = mainObj.getString(ConstantsKeys.StartOdometer);
                }
                if (mainObj.has(ConstantsKeys.EngineHours)) {
                    StartEngineHours = mainObj.getString(ConstantsKeys.EngineHours);
                }

                try {
                    if(constants.isExponentialValue(StartOdometer)){
                        StartOdometer = BigDecimal.valueOf(Double.parseDouble(StartOdometer)).toPlainString();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String TotalMinutes = "--";
                if(mainObj.has(ConstantsKeys.TotalMinutes)){
                    TotalMinutes = mainObj.getString(ConstantsKeys.TotalMinutes);
                }

                // Child array event
                MalfunctionModel malfunctionModel = new MalfunctionModel(
                        Country,
                        VIN,
                        CompanyId,
                        mainObj.getString(ConstantsKeys.EventDateTime),
                        EngHrs,
                        StartOdometer,
                        mainObj.getString(ConstantsKeys.DetectionDataEventCode),
                        "", "",
                        "", "", "", driverEndTimeZone,  // passing event end date into to date
                        driverTimeZone, "--", StartEngineHours, TotalMinutes   //TotalMinutes value is passing in getId()
                );

                // add data in child list
                malfunctionChildList.add(malfunctionModel);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void notifyMalfunctionAdapter(TextView noDataEldTV, ExpandableListView listView,
                                          List<MalfunctionHeaderModel> headerList,
                                          HashMap<String, List<MalfunctionModel>> childHashMap){

        if(noDataEldTV != null) {
            if (childHashMap.size() > 0) {
                noDataEldTV.setVisibility(View.GONE);
            } else {
                noDataEldTV.setVisibility(View.VISIBLE);
            }
        }
        try {
            MalfunctionHistoryAdapter adapter = new MalfunctionHistoryAdapter(getActivity(), DriverId, headerList, childHashMap);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.dateActionBarTV:
                ShowDateDialog();
                break;


            case R.id.eldMenuLay:
                getParentFragmentManager().popBackStack();
                break;

            case R.id.previousDate:
                VisibleHideNextPrevView("prev");
                OnDateSelectionView(EventDateTime);
                break;

            case R.id.nextDateBtn:
                VisibleHideNextPrevView("next");
                OnDateSelectionView(EventDateTime);
                break;


        }
    }



    void ShowDateDialog(){
        try {
            if (dateDialog != null && dateDialog.isShowing())
                dateDialog.dismiss();

            dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, EventDateTime, new DateListener(), false);
            dateDialog.show();
        }catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
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
            EventDateTime = SelectedDate;
            VisibleHideNextPrevView("");
            OnDateSelectionView(SelectedDate);

        }
    }





    private void OnDateSelectionView(String SelectedDate){
        EventDateTime  = SelectedDate;
        dateActionBarTV.setText(SelectedDate);
        //10/03/2021

        // Get Local Saved Data
        viewOfflineData(EventDateTime);

    }


    void VisibleHideNextPrevView(String click){
        String selectedDateStr = globally.ConvertDateFormat(EventDateTime);
        String currentDateStr = globally.ConvertDateFormat(globally.GetCurrentDeviceDate());
        DateTime selectedDateTime = new DateTime(globally.getDateTimeObj(selectedDateStr, false) );
        DateTime currentDateTime = new DateTime(globally.getDateTimeObj(currentDateStr, false) );

        if(click.equals("next")){
            selectedDateTime = selectedDateTime.plusDays(1);
            EventDateTime = globally.ConvertDateFormatMMddyyyy(selectedDateTime.toString());
        }else if(click.equals("prev")){
            selectedDateTime = selectedDateTime.minusDays(1);
            EventDateTime = globally.ConvertDateFormatMMddyyyy(selectedDateTime.toString());
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

    }



}
