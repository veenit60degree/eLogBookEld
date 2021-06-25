package com.messaging.logistic.fragment;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.adapter.logistic.MalfunctionAdapter;
import com.adapter.logistic.TabLayoutAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.SaveLogJsonObj;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.custom.dialogs.MalfunctionDialog;
import com.driver.details.DriverConst;
import com.google.android.material.tabs.TabLayout;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.MalfunctionDiagnosticMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.models.MalfunctionHeaderModel;
import com.models.MalfunctionModel;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MalfncnDiagnstcViewPager extends Fragment implements View.OnClickListener {

    View rootView;
    CardView cancelCertifyBtn, confirmCertifyBtn;
    TextView confirmCertifyTV, EldTitleTV, dateActionBarTV;
    TabLayout tabLayout;
    TabLayoutAdapter tabAdapter;
    ViewPager MalDiaViewPager;
    RelativeLayout rightMenuBtn, eldMenuLay;

    DBHelper dbHelper;
    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
    MalfunctionEventFragment malfunctionEventFragment;
    DiagnosticEventFragment diagnosticEventFragment;
    static TextView noRecordMalTV, noRecordDiaTV;
    static ExpandableListView malfunctionExpandList, diagnosticExpandList;

    String DriverId = "", DeviceId = "", VIN = "", FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId;
    Map<String, String> params;
    VolleyRequest GetMalfunctionEvents;
    Globally globally;

    List<MalfunctionModel> malfunctionChildList = new ArrayList<>();
    List<MalfunctionHeaderModel> malfunctionHeaderList = new ArrayList<>();
    private HashMap<String, List<MalfunctionModel>> malfunctionChildHashMap = new HashMap<>();

    List<MalfunctionHeaderModel> diagnosticHeaderList = new ArrayList<>();
    private HashMap<String, List<MalfunctionModel>> diagnosticChildHashMap = new HashMap<>();


    public static TextView invisibleMalfnBtn;
    MalfunctionDialog malfunctionDialog;
    SaveLogJsonObj clearRecordPost;
    ProgressDialog progressDialog;
    Constants constants;

    private int[] tabIcons = {
            R.drawable.original_log_icon,
            R.drawable.original_log_icon
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.activity_edit_log_compare, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }

        initView(rootView);

        return rootView;
    }

    void initView(View rootView) {

        GetMalfunctionEvents  = new VolleyRequest(getActivity());
        clearRecordPost   = new SaveLogJsonObj(getActivity(), apiResponse );
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");

        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();
        dbHelper            = new DBHelper(getActivity());
        constants           = new Constants();
        globally            = new Globally();

        cancelCertifyBtn    = (CardView)rootView.findViewById(R.id.cancelCertifyBtn);
        confirmCertifyBtn   = (CardView)rootView.findViewById(R.id.confirmCertifyBtn);

        confirmCertifyTV    = (TextView)rootView.findViewById(R.id.confirmCertifyTV);
        EldTitleTV          = (TextView) rootView.findViewById(R.id.EldTitleTV);
        invisibleMalfnBtn   = (TextView)rootView.findViewById(R.id.invisibleMalfnDiaBtn);
        dateActionBarTV     = (TextView)rootView.findViewById(R.id.dateActionBarTV);

        rightMenuBtn = (RelativeLayout) rootView.findViewById(R.id.rightMenuBtn);
        eldMenuLay   = (RelativeLayout) rootView.findViewById(R.id.eldMenuLay);

        malfunctionEventFragment = new MalfunctionEventFragment();
        diagnosticEventFragment = new DiagnosticEventFragment();
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        MalDiaViewPager  = (ViewPager) rootView.findViewById(R.id.editedLogPager);

        rightMenuBtn.setVisibility(View.GONE);

        DriverId = SharedPref.getDriverId( getActivity());
        DeviceId = SharedPref.GetSavedSystemToken(getActivity());

        dateActionBarTV.setVisibility(View.VISIBLE);
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.whiteee));
        EldTitleTV.setText(getResources().getString(R.string.malfunction_and_dia));
        confirmCertifyTV.setText(getString(R.string.ClearAll));
        setPagerAdapter(0, false);

        cancelCertifyBtn.setVisibility(View.GONE);
        confirmCertifyBtn.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        invisibleMalfnBtn.setOnClickListener(this);
        dateActionBarTV.setOnClickListener(this);

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

        DriverId                = SharedPref.getDriverId( getActivity());
        VIN                     = SharedPref.getVINNumber(getActivity());
        Country                 = constants.getCountryName(getActivity());
        OffsetFromUTC           = DriverConst.GetDriverSettings(DriverConst.OffsetHours, getActivity());
        CompanyId               = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
        DateTime currentDate    = globally.getDateTimeObj(globally.GetCurrentDateTime(), false);
        String CurrentCycleId   = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());

        try {
            if (currentDate.toString().length() > 10) {
                ToDateTime              = currentDate.toString().substring(0,10);
                if (CurrentCycleId.equals(globally.CANADA_CYCLE_1) || CurrentCycleId.equals(globally.CANADA_CYCLE_2)) {
                    FromDateTime = String.valueOf(currentDate.minusDays(14)).substring(0, 10);  // // in CAN 14+1 days
                } else {
                    FromDateTime = String.valueOf(currentDate.minusDays(7)).substring(0, 10);   // // in US 7+1 days
                }
            }

            if (SharedPref.IsAOBRD(getActivity())) {
                dateActionBarTV.setText(Html.fromHtml("<b><u>AOBRD</u></b>"));
            } else {
                dateActionBarTV.setText(Html.fromHtml("<b><u>ELD</u></b>"));
            }


            if (globally.isConnected(getContext())) {
                GetMalfunctionEvents(DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);
            } else {

                boolean isDiagnostic;
                boolean isMalfunction;
                if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
                    isDiagnostic = SharedPref.isDiagnosticOccur(getActivity());
                    isMalfunction = SharedPref.isMalfunctionOccur(getActivity());

                } else {
                    isDiagnostic = SharedPref.isDiagnosticOccurCo(getActivity());
                    isMalfunction = SharedPref.isMalfunctionOccurCo(getActivity());
                }

                if (isDiagnostic && !isMalfunction) {
                    setPagerAdapter(1, false);
                } else {
                    setPagerAdapter(0, false);
                }


                globally.EldScreenToast(confirmCertifyTV, globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // Add Fragments to Tabs ViewPager for each Tabs
    private void setPagerAdapter(int position, final boolean isUpdateFromApi){

        try {
            tabAdapter = new TabLayoutAdapter(getChildFragmentManager(), getActivity());
            tabAdapter.addFragment(malfunctionEventFragment, getString(R.string.malfunction_events), tabIcons[0]);
            tabAdapter.addFragment(diagnosticEventFragment, getString(R.string.dia_events), tabIcons[1]);
            MalDiaViewPager.setAdapter(tabAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            tabLayout.setupWithViewPager(MalDiaViewPager);
            MalDiaViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    highLightCurrentTab(position);
                    refreshAdapterOnPageChange(position, isUpdateFromApi);
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            if(isUpdateFromApi) {
                if (malfunctionHeaderList.size() == 0 && diagnosticHeaderList.size() > 0) {
                    highLightCurrentTab(1);
                } else if (malfunctionHeaderList.size() > 0 && diagnosticHeaderList.size() == 0) {
                    highLightCurrentTab(0);
                } else {
                    highLightCurrentTab(position);
                }
            }else{
                highLightCurrentTab(position);
            }
            refreshAdapterOnPageChange(position, isUpdateFromApi);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(tabAdapter.getTabView(i));
        }
        TabLayout.Tab currentTab = tabLayout.getTabAt(position);
        assert currentTab != null;
        currentTab.setCustomView(null);
        currentTab.setCustomView(tabAdapter.getSelectedTabView(position));
        MalDiaViewPager.setCurrentItem(position);
    }


    private void refreshAdapterOnPageChange(int position, boolean isUpdateFromApi){
        if(position == 0){
            // notify malfunction adapter
            if(noRecordMalTV != null) {
                notifyMalfunctionAdapter(noRecordMalTV, malfunctionExpandList,
                        malfunctionHeaderList, malfunctionChildHashMap, isUpdateFromApi, true);
            }
        }else{
            // notify diagnostic adapter
            if(noRecordDiaTV != null) {
                notifyMalfunctionAdapter(noRecordDiaTV, diagnosticExpandList,
                        diagnosticHeaderList, diagnosticChildHashMap, isUpdateFromApi, false);
            }
        }

        updateMalDiagnostic(malfunctionHeaderList, true, isUpdateFromApi);
        updateMalDiagnostic(diagnosticHeaderList, false, isUpdateFromApi);
    }


    void updateMalDiagnostic(List<MalfunctionHeaderModel> headerList, boolean isMalfunctionUpdate, boolean isUpdateFromApi){
        boolean isMalfunction = false;
        boolean isDiagnostic = false;

        if(headerList.size() > 0){
            if(isMalfunctionUpdate){
                if(headerList.size() > 0){
                    isMalfunction = true;
                }
            }else{
                if(headerList.size() > 0){
                    isDiagnostic = true;
                }
            }

        }else{
            if(isUpdateFromApi) {
                if (isMalfunctionUpdate) {
                    SharedPref.saveEngSyncMalfunctionStatus(false, getActivity());
                } else {
                    SharedPref.saveEngSyncDiagnstcStatus(false, getActivity());
                }
            }
        }


        if(isUpdateFromApi) {

            if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
                if(isMalfunctionUpdate){
                    isDiagnostic = SharedPref.isDiagnosticOccur(getActivity());
                }else{
                    isMalfunction = SharedPref.isMalfunctionOccur(getActivity());
                }
                SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getActivity()),
                        isMalfunction,
                        isDiagnostic,
                        SharedPref.isSuggestedEditOccur(getActivity()), getActivity());
            } else {
                if(isMalfunctionUpdate){
                    isDiagnostic = SharedPref.isDiagnosticOccurCo(getActivity());
                }else{
                    isMalfunction = SharedPref.isMalfunctionOccurCo(getActivity());
                }
                SharedPref.setEldOccurencesCo(SharedPref.isUnidentifiedOccurCo(getActivity()),
                        isMalfunction,
                        isDiagnostic,
                        SharedPref.isSuggestedEditOccurCo(getActivity()), getActivity());
            }
        }

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.confirmCertifyBtn:
                if (malfunctionDialog != null && malfunctionDialog.isShowing())
                    malfunctionDialog.dismiss();

                if(globally.isConnected(getContext())) {
                    if(malfunctionHeaderList.size() > 0 || diagnosticHeaderList.size() > 0) {
                        if(constants.isActionAllowed(getContext())) {
                            malfunctionDialog = new MalfunctionDialog(getActivity(), new ArrayList<MalfunctionModel>(),
                                    new MalfunctionDiagnosticListener());
                            malfunctionDialog.show();
                        }else{
                            globally.EldScreenToast(confirmCertifyBtn, getString(R.string.stop_vehicle_alert),
                                    getResources().getColor(R.color.colorVoilation));
                        }
                    }else{
                        globally.EldScreenToast(confirmCertifyTV, getString(R.string.no_malfunction_diagnostc_records), getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    globally.EldScreenToast(confirmCertifyTV, globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                }

                break;

            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;

            case R.id.invisibleMalfnDiaBtn:
                GetMalfunctionEvents( DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);
                break;

            case R.id.dateActionBarTV:
                TabAct.host.setCurrentTab(0);
                break;


        }
    }


    private class MalfunctionDiagnosticListener implements MalfunctionDialog.RecordsListener{

        @Override
        public void RecordsOkBtn(String reason, List<MalfunctionModel> listData) {

            if (malfunctionDialog != null && malfunctionDialog.isShowing())
                malfunctionDialog.dismiss();

            try {
                // get events data for clear
                JSONObject clearEventObj = constants.getMalfunctionDiagnosticArray(DriverId, reason,
                        malfunctionHeaderList, malfunctionChildHashMap, diagnosticHeaderList,
                        diagnosticChildHashMap, getActivity());

                JSONArray eventId = new JSONArray(clearEventObj.getString(ConstantsKeys.EventList));
                if (eventId.length() > 0) {
                    progressDialog.show();
                    clearRecordPost.SaveLogJsonObj(clearEventObj, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT, Constants.SocketTimeout30Sec, true, false, 0, 101);
                }else{
                    if(SharedPref.IsClearDiagnostic(getActivity()) && SharedPref.IsClearMalfunction(getActivity())){
                        globally.EldScreenToast(confirmCertifyTV, getString(R.string.no_malfunction_diagnostc_valid), getResources().getColor(R.color.colorVoilation));
                    }else{
                        if(SharedPref.IsClearDiagnostic(getActivity())){
                            globally.EldScreenToast(confirmCertifyTV, getString(R.string.no_diagnostic_records), getResources().getColor(R.color.colorVoilation));
                        }else{
                            globally.EldScreenToast(confirmCertifyTV, getString(R.string.no_malfunction_records), getResources().getColor(R.color.colorVoilation));
                        }
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }




    public static class MalfunctionEventFragment extends Fragment{

        View rootViewMal;
        RelativeLayout malfunctionActionBar;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            if (rootViewMal != null) {
                ViewGroup parent = (ViewGroup) rootViewMal.getParent();
                if (parent != null)
                    parent.removeView(rootViewMal);
            }
            try {
                rootViewMal = inflater.inflate(R.layout.fragment_malfunction, container, false);
                rootViewMal.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } catch (InflateException e) {
                e.printStackTrace();
            }


            initView(rootViewMal);

            return rootViewMal;
        }


        void initView(View view) {

            malfunctionActionBar    = (RelativeLayout)view.findViewById(R.id.malfunctionActionBar);
            malfunctionExpandList   = (ExpandableListView)view.findViewById(R.id.malfunctionExpandList);
            noRecordMalTV           = (TextView)view.findViewById(R.id.noRecordTV);

            malfunctionActionBar.setVisibility(View.GONE);
        }

    }

    public static class DiagnosticEventFragment extends Fragment{

        View rootViewDia;
        RelativeLayout malfunctionActionBar;


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

            malfunctionActionBar    = (RelativeLayout)view.findViewById(R.id.malfunctionActionBar);
            diagnosticExpandList    = (ExpandableListView)view.findViewById(R.id.malfunctionExpandList);
            noRecordDiaTV              = (TextView)view.findViewById(R.id.noRecordTV);

            malfunctionActionBar.setVisibility(View.GONE);
        }

    }



    void checkLocMalfunction(){
        boolean isLocMalfunctionOccur = SharedPref.isLocMalfunctionOccur(getActivity());
        String malfunctionType = SharedPref.getLocMalfunctionType(getActivity());
        if(isLocMalfunctionOccur && (malfunctionType.equals("m") || malfunctionType.equals("x"))){
            malfunctionHeaderList.add(new MalfunctionHeaderModel(
                    "Invalid Location Occur", "1", getString(R.string.loc_mal)));
            malfunctionChildList.add(new MalfunctionModel(
                    SharedPref.getCountryCycle("CountryCycle", getActivity()),
                    SharedPref.getVINNumber( getActivity()),
                    DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity()),
                    SharedPref.getLocMalfunctionOccuredTime(getActivity()),
                    "", "", "1", "", "1",
                    getString(R.string.loc_mal_occur), "", "", "",
                    SharedPref.getLocMalfunctionOccuredTime(getActivity()), "101", "101",""

            ));

            // Add both list (Header/CHild) in hash map type list
            malfunctionChildHashMap.put("1", malfunctionChildList);

        }
    }

    /*================== Get Unidentified Records ===================*/
    void GetMalfunctionEvents(String DriverId, String VIN, final String FromDateTime, final String ToDateTime,
                              final String Country, String OffsetFromUTC, String CompanyId){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.VIN, VIN); //4V4NC9EH7GN929538
        params.put(ConstantsKeys.FromDateTime, FromDateTime);
        params.put(ConstantsKeys.ToDateTime, ToDateTime );
        params.put(ConstantsKeys.Country, Country);
        params.put(ConstantsKeys.OffsetFromUTC, OffsetFromUTC);
        params.put(ConstantsKeys.CompanyId, CompanyId);

        GetMalfunctionEvents.executeRequest(Request.Method.POST, APIs.GET_MALFUNCTION_EVENTS , params, 0,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }





    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {
        @Override
        public void getResponse(String response, int flag) {

            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            JSONObject obj = null;
            String status = "";

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");
                if (status.equalsIgnoreCase("true")) {

                    JSONArray malfunctionArray = new JSONArray(obj.getString(ConstantsKeys.Data));
                    malfunctionHeaderList = new ArrayList<>();
                    malfunctionChildHashMap = new HashMap<>();
                    diagnosticHeaderList = new ArrayList<>();
                    diagnosticChildHashMap = new HashMap<>();

                    checkLocMalfunction();

                    for(int  i = 0 ; i < malfunctionArray.length() ; i++){
                        malfunctionChildList = new ArrayList<>();

                        JSONObject mainObj = (JSONObject)malfunctionArray.get(i);
                        MalfunctionHeaderModel headerModel = new MalfunctionHeaderModel(
                                mainObj.getString(ConstantsKeys.EventName),
                                mainObj.getString(ConstantsKeys.EventCode),
                                mainObj.getString(ConstantsKeys.Definition)

                        );


                        // Child array loop event
                        JSONArray malfunctionChildArray = new JSONArray(mainObj.getString(ConstantsKeys.list));
                        for(int  j = 0 ; j < malfunctionChildArray.length() ; j++) {
                            JSONObject objItem = (JSONObject) malfunctionChildArray.get(j);
                            String HexaSequenceNumber = "";
                            if(objItem.has(ConstantsKeys.HEXA_SEQUENCE_NUMBER)){
                                HexaSequenceNumber = objItem.getString(ConstantsKeys.HEXA_SEQUENCE_NUMBER);
                            }
                            MalfunctionModel malfunctionModel = new MalfunctionModel(
                                    objItem.getString(ConstantsKeys.Country),
                                    objItem.getString(ConstantsKeys.VIN),
                                    objItem.getString(ConstantsKeys.CompanyId),
                                    objItem.getString(ConstantsKeys.EventDateTime),
                                    objItem.getString(ConstantsKeys.EngineHours),
                                    objItem.getString(ConstantsKeys.Miles),
                                    objItem.getString(ConstantsKeys.DetectionDataEventCode),
                                    objItem.getString(ConstantsKeys.MasterDetectionDataEventId),
                                    objItem.getString(ConstantsKeys.EventCode),
                                    objItem.getString(ConstantsKeys.Reason),
                                    objItem.getString(ConstantsKeys.MalfunctionDefinition),
                                    objItem.getString(ConstantsKeys.FromDateTime),
                                    objItem.getString(ConstantsKeys.ToDateTime),
                                    objItem.getString(ConstantsKeys.DriverZoneEventDate),
                                    objItem.getString(ConstantsKeys.SEQUENCE_NO),
                                    HexaSequenceNumber,
                                    objItem.getString(ConstantsKeys.Id)

                            );
                            // add data in child list
                            malfunctionChildList.add(malfunctionModel);
                        }

                        boolean isDiagnostic = constants.isValidInteger( mainObj.getString(ConstantsKeys.EventCode));

                        // add data in header list
                        if(isDiagnostic ){   // Valid integer is Diagnostic
                            diagnosticHeaderList.add(headerModel);
                            diagnosticChildHashMap.put(mainObj.getString(ConstantsKeys.EventCode), malfunctionChildList);

                        }else{ // InValid integer is Malfunction
                            malfunctionHeaderList.add(headerModel);
                            malfunctionChildHashMap.put(mainObj.getString(ConstantsKeys.EventCode), malfunctionChildList);

                        }



                    }

                    try {
                        if (malfunctionHeaderList.size() == 0) {
                            malfunctionDiagnosticMethod.MalfnDiagnstcLogHelperEvents(Integer.valueOf(DriverId), dbHelper, new JSONArray());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    malfunctionHeaderList = new ArrayList<>();
                    malfunctionChildList = new ArrayList<>();
                    malfunctionChildHashMap = new HashMap<>();
                    diagnosticHeaderList = new ArrayList<>();
                    diagnosticChildHashMap = new HashMap<>();

                    checkLocMalfunction();
                }

                setPagerAdapter(MalDiaViewPager.getCurrentItem(), true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {

            Log.d("error", ">>error: " + error);
            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();


        }
    };



    /* ---------------------- clear Log Request Response ---------------- */
    DriverLogResponse apiResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag) {

            try {

                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();


                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ConstantsKeys.Status);
                String message  = obj.getString(ConstantsKeys.Message);
                if (status.equalsIgnoreCase("true")) {
                    // {"Status":true,"Message":"Required Parameter is empty","Data":null}

                    if(message.equals("Record Clear Successfully")) {
                        Toast.makeText(getActivity(), getString(R.string.RecordClearedSuccessfully), Toast.LENGTH_LONG).show();
                        progressDialog.show();

                        SharedPref.saveEngSyncDiagnstcStatus(false, getActivity());
                        SharedPref.saveEngSyncMalfunctionStatus(false, getActivity());
                        constants.saveDiagnstcStatus(getActivity(), false);
                        constants.saveMalfncnStatus(getActivity(), false);


                        // call get events api to refresh data
                        invisibleMalfnBtn.performClick();
                    }else{
                        globally.EldScreenToast(invisibleMalfnBtn, message,
                                getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    // {"Status":false,"Message":"Failed..","Data":null}
                    globally.EldScreenToast(invisibleMalfnBtn, message,
                            getResources().getColor(R.color.colorVoilation));

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("errorrr ", ">>>error dialog: ");

            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();


            globally.EldScreenToast(invisibleMalfnBtn, error,
                    getResources().getColor(R.color.colorVoilation));
        }

    };



    private void notifyMalfunctionAdapter(TextView noDataEldTV, ExpandableListView listView,
                                          List<MalfunctionHeaderModel> headerList,
                                          HashMap<String, List<MalfunctionModel>> childHashMap,
                                          boolean isUpdateFromApi, boolean isMalfunctionUpdate){

        if(childHashMap.size() > 0){
            noDataEldTV.setVisibility(View.GONE);
        }else{
            noDataEldTV.setVisibility(View.VISIBLE);
        }

        try {
            MalfunctionAdapter adapter = new MalfunctionAdapter(getActivity(), DriverId, headerList, childHashMap);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }


    }



}
