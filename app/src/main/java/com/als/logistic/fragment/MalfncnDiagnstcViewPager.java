package com.als.logistic.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.adapter.logistic.MalfunctionAdapter;
import com.adapter.logistic.TabLayoutAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.Logger;
import com.constants.SaveDriverLogPost;
import com.constants.SaveLogJsonObj;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.custom.dialogs.MalfunctionDialog;
import com.driver.details.DriverConst;
import com.google.android.material.tabs.TabLayout;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.MalfunctionDiagnosticMethod;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.TabAct;
import com.als.logistic.UILApplication;
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
import java.util.Map;

public class MalfncnDiagnstcViewPager extends Fragment implements View.OnClickListener {

    View rootView;
    CardView cancelCertifyBtn, confirmCertifyBtn;
    TextView confirmCertifyTV, EldTitleTV, dateActionBarTV;
    TabLayout tabLayout;
    TabLayoutAdapter tabAdapter;
    ViewPager MalDiaViewPager;
    RelativeLayout rightMenuBtn, eldMenuLay;
    LinearLayout confirmCertifyLay;

    DBHelper dbHelper;
    HelperMethods helperMethods;
    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
    MalfunctionEventFragment malfunctionEventFragment;
    DiagnosticEventFragment diagnosticEventFragment;
    static TextView noRecordMalTV, noRecordDiaTV;
    static ExpandableListView malfunctionExpandList, diagnosticExpandList;
    MalfunctionHeaderModel headerModel = null;

    boolean isOnCreate = true;
    String DriverId = "", DeviceId = "", VIN = "", FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId;
    Map<String, String> params;
    VolleyRequest GetMalfunctionEvents;
    SaveDriverLogPost saveDriverLogPost;
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
    boolean refreshButtonCLicked = false;



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

        if(UILApplication.getInstance().isNightModeEnabled()){
            getActivity().setTheme(R.style.DarkTheme);
        } else {
            getActivity().setTheme(R.style.LightTheme);
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
        saveDriverLogPost       = new SaveDriverLogPost(getActivity(), saveLogRequestResponse);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");

        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();
        dbHelper            = new DBHelper(getActivity());
        helperMethods       = new HelperMethods();
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
        confirmCertifyLay   = (LinearLayout)rootView.findViewById(R.id.confirmCertifyLay);

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
        confirmCertifyBtn.setVisibility(View.GONE);
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


        // update mal/dia status for enable disable according to log
        malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(DriverId, globally, constants, dbHelper, getActivity());

        getDriverInfo();
        DateTime currentDate    = globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), globally, getActivity());
        String CurrentCycleId      = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());

        try {
            if (currentDate.toString().length() > 10) {
                ToDateTime              = currentDate.toString().substring(0,10);
                if ( CurrentCycleId.equals(globally.CANADA_CYCLE_1) || CurrentCycleId.equals(globally.CANADA_CYCLE_2) ) {
                    FromDateTime = String.valueOf(currentDate.minusDays(14)).substring(0, 10);  // // in CAN 14+1 days
                } else {
                    FromDateTime = String.valueOf(currentDate.minusDays(7)).substring(0, 10);   // // in US 7+1 days
                }
            }

            dateActionBarTV.setText(Html.fromHtml("<b><u>Events History" +
                    "</u></b>"));

            loadData();

            if (globally.isConnected(getContext())) {
                confirmCertifyLay.setVisibility(View.VISIBLE);
                JSONArray malArray1 = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);

                if (malArray1.length() > 0) {
                    saveDriverLogPost.PostDriverLogData(malArray1, APIs.MALFUNCTION_DIAGNOSTIC_EVENT, Constants.SocketTimeout20Sec, false, false, 1, 0);
                }

            } else {
                confirmCertifyLay.setVisibility(View.GONE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        isOnCreate = false;

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver( progressReceiver, new IntentFilter(ConstantsKeys.IsEventUpdate));

    }


    void loadData(){
        final boolean isDiagnostic;
        final boolean isMalfunction;
        if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
            isDiagnostic = SharedPref.isDiagnosticOccur(getActivity());
            isMalfunction = SharedPref.isMalfunctionOccur(getActivity());

        } else {
            isDiagnostic = SharedPref.isDiagnosticOccurCo(getActivity());
            isMalfunction = SharedPref.isMalfunctionOccurCo(getActivity());
        }

        if(isOnCreate){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewOfflineData(isDiagnostic, isMalfunction, false);
                }
            }, 700);

        }else{
            viewOfflineData(isDiagnostic, isMalfunction, false);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(progressReceiver);
    }


    void getDriverInfo(){
        DriverId                = SharedPref.getDriverId( getActivity());
        VIN                     = SharedPref.getVINNumber(getActivity());
        Country                 = constants.getCountryName(getActivity());
        OffsetFromUTC           = DriverConst.GetDriverSettings(DriverConst.OffsetHours, getActivity());
        CompanyId               = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
    }


    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

             try {
                 getDriverInfo();
                 boolean IsEventUpdate = intent.getBooleanExtra(ConstantsKeys.IsEventUpdate, false);
                 if (IsEventUpdate) {
                     boolean  IsLocalEventUpdate =  intent.getBooleanExtra(ConstantsKeys.IsLocalEventUpdate, false);
                     if(IsLocalEventUpdate) {
                         final boolean isDiagnostic;
                         final boolean isMalfunction;
                         if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
                             isDiagnostic = SharedPref.isDiagnosticOccur(getActivity());
                             isMalfunction = SharedPref.isMalfunctionOccur(getActivity());
                         } else {
                             isDiagnostic = SharedPref.isDiagnosticOccurCo(getActivity());
                             isMalfunction = SharedPref.isMalfunctionOccurCo(getActivity());
                         }

                         viewOfflineData(isDiagnostic, isMalfunction, IsLocalEventUpdate);
                     }else{

                        // GetMalfunctionEvents(DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);
                     }

                 }

            }catch (Exception e){
                 e.printStackTrace();
             }
        }
    };




    void viewOfflineData(boolean isDiagnostic, boolean isMalfunction, boolean isReceiverUpdate){
        try{
            JSONArray malDiaArray = malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper);

            Logger.LogDebug("malDiaArray", "malDiaArray: " + malDiaArray);

                if (isDiagnostic && !isMalfunction) {
                    if(malDiaArray.length() > 0) {
                        showOfflineData(malDiaArray, 1);
                    }else {
                         setPagerAdapter(1, false);
                    }
                    if(isReceiverUpdate){
                        MalDiaViewPager.setCurrentItem(0);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MalDiaViewPager.setCurrentItem(1);
                            }
                        }, 100);
                    }
                } else {

                    if(malDiaArray.length() > 0) {
                        showOfflineData(malDiaArray, 0);
                    }else {
                        malfunctionChildList = new ArrayList<>();
                        malfunctionHeaderList = new ArrayList<>();
                        malfunctionChildHashMap = new HashMap<>();
                        diagnosticHeaderList = new ArrayList<>();
                        diagnosticChildHashMap = new HashMap<>();


                        setPagerAdapter(0, false);
                    }

                    if(isReceiverUpdate){
                        MalDiaViewPager.setCurrentItem(1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MalDiaViewPager.setCurrentItem(0);
                            }
                        }, 100);
                    }
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
            //if(noRecordMalTV != null) {
                notifyMalfunctionAdapter(noRecordMalTV, malfunctionExpandList,
                        malfunctionHeaderList, malfunctionChildHashMap);
           // }
        }else{
            // notify diagnostic adapter
           // if(noRecordDiaTV != null) {
                notifyMalfunctionAdapter(noRecordDiaTV, diagnosticExpandList,
                        diagnosticHeaderList, diagnosticChildHashMap);
           // }
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

                        if(helperMethods.isActionAllowedWhileMoving(getActivity(), new Globally(), DriverId, dbHelper)){
                            malfunctionDialog = new MalfunctionDialog(getActivity(), new ArrayList<MalfunctionModel>(),
                                    new MalfunctionDiagnosticListener());
                            malfunctionDialog.show();
                        }else{
                            globally.EldScreenToast(confirmCertifyBtn, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                            getString(R.string.stop_vehicle_alert),
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
                refreshButtonCLicked = true;
                GetMalfunctionEvents( DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);

                constants.refreshEventDataFromService(getActivity());

                break;

            case R.id.dateActionBarTV:
               // TabAct.host.setCurrentTab(0);

               MoveFragment(new MalfunctionDiagnosticHistoryFragment());

                //Globally.PlayNotificationSound(getActivity());
               // Globally.ShowLocalNotification(getActivity(), "ELD", "Test Notification", 2091);


                break;


        }
    }



    private void MoveFragment(Fragment fragment){
        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, fragment);
        fragmentTran.addToBackStack("obd_diagnose");
        fragmentTran.commitAllowingStateLoss();


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
                    globally.EldScreenToast(confirmCertifyTV, getString(R.string.no_malfunction_diagnostc_valid), getResources().getColor(R.color.colorVoilation));
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

            if(UILApplication.getInstance().isNightModeEnabled()){
                getActivity().setTheme(R.style.DarkTheme);
            } else {
                getActivity().setTheme(R.style.LightTheme);
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
            noRecordDiaTV           = (TextView)view.findViewById(R.id.noRecordTV);

            malfunctionActionBar.setVisibility(View.GONE);
        }

    }



    void checkLocMalfunction(){
        boolean isLocMalfunctionOccur = SharedPref.isLocMalfunctionOccur(getActivity());
        String malfunctionType = SharedPref.getLocationEventType(getActivity());
        if(isLocMalfunctionOccur && (malfunctionType.equals("M") || malfunctionType.equals("X"))){
            malfunctionHeaderList.add(new MalfunctionHeaderModel(
                    "Positioning Compliance Event", "1", getString(R.string.loc_mal), false, true, "-1"));
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

                    JSONArray malfunctionArray  = new JSONArray(obj.getString(ConstantsKeys.Data));
                    malfunctionHeaderList       = new ArrayList<>();
                    malfunctionChildHashMap     = new HashMap<>();
                    diagnosticHeaderList        = new ArrayList<>();
                    diagnosticChildHashMap      = new HashMap<>();
                    malfunctionChildList        = new ArrayList<>();

                  //  checkLocMalfunction();

                    for(int  i = 0 ; i < malfunctionArray.length() ; i++){
                        malfunctionChildList = new ArrayList<>();

                        JSONObject mainObj = (JSONObject)malfunctionArray.get(i);
                        headerModel = new MalfunctionHeaderModel(
                                mainObj.getString(ConstantsKeys.EventName),
                                mainObj.getString(ConstantsKeys.EventCode),
                                mainObj.getString(ConstantsKeys.Definition),
                               false, false, ""+i

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
                                    objItem.getString(ConstantsKeys.StartOdometer),
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
                                    "--"


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

                    if(refreshButtonCLicked) {
                        try {
                            if (malfunctionHeaderList.size() == 0) {
                                constants.resetMalDiaEvents(getActivity());
                                //malfunctionDiagnosticMethod.MalfnDiagnstcLogHelperEvents(Integer.valueOf(DriverId), dbHelper, new JSONArray());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    malfunctionHeaderList = new ArrayList<>();
                    malfunctionChildList = new ArrayList<>();
                    malfunctionChildHashMap = new HashMap<>();
                    diagnosticHeaderList = new ArrayList<>();
                    diagnosticChildHashMap = new HashMap<>();

                    if(refreshButtonCLicked) {
                        checkLocMalfunction();
                        constants.resetMalDiaEvents(getActivity());
                    }
                }

                setPagerAdapter(MalDiaViewPager.getCurrentItem(), true);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Check all events if not posted to server. Then avoid refresh data
            boolean isUnposted = malfunctionDiagnosticMethod.isUnPostAnyEvent(dbHelper);
            if(refreshButtonCLicked && isUnposted) {
                Constants.isCallMalDiaEvent = true;
                SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());

                Intent serviceIntent = new Intent(getActivity(), BackgroundLocationService.class);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getActivity().startForegroundService(serviceIntent);
                }
                getActivity().startService(serviceIntent);
            }

            refreshButtonCLicked = false;
        }
    };



    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {

            Logger.LogDebug("error", ">>error: " + error);
            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();


        }
    };



    /* ---------------------- clear Log Request Response ---------------- */
    DriverLogResponse apiResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, JSONArray inputData) {

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
            Logger.LogDebug("errorrr ", ">>>error dialog: ");

            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();


            globally.EldScreenToast(invisibleMalfnBtn, error,
                    getResources().getColor(R.color.colorVoilation));
        }

    };




    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int driver_id, int flag, JSONArray inputData) {

            String status = "";
            try {
                JSONObject obj = new JSONObject(response);
                status = obj.getString("Status");
            }catch (Exception e){
                e.printStackTrace();
            }

            if (status.equals("true")) {
                malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());

              //  GetMalfunctionEvents(DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);
            }else{
               // GetMalfunctionEvents(DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);
            }


        }
        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Logger.LogDebug("errorrr ", ">>>error dialog: " );
        }
    };



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
            MalfunctionAdapter adapter = new MalfunctionAdapter(getActivity(), DriverId, headerList, childHashMap);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
           // e.printStackTrace();
        }


    }



    void showOfflineData(JSONArray malDiaArray, int position){
        try{
            malfunctionHeaderList       = new ArrayList<>();
            malfunctionChildHashMap     = new HashMap<>();
            diagnosticHeaderList        = new ArrayList<>();
            diagnosticChildHashMap      = new HashMap<>();
            malfunctionChildList        = new ArrayList<>();

            // Adding same type events in single group for Expandable ListView with permission check
            if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerDataDiag, getActivity())) {   // check power dia permission
                parseListInHashMap(malDiaArray, Constants.PowerComplianceDiagnostic);
            }

            if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerComplianceMal, getActivity())) {   // check power mal permission
                parseListInHashMap(malDiaArray, Constants.PowerComplianceMalfunction);
            }


            if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncDiag, getActivity())) {    // check eng sync dia permission
                parseListInHashMap(malDiaArray, Constants.EngineSyncDiagnosticEvent);
            }
            if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncMal, getActivity())) {     // check eng sync mal permission
                parseListInHashMap(malDiaArray, Constants.EngineSyncMalfunctionEvent);
            }


            if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PostioningComplMal, getActivity())) {   // check position mal permission
                parseListInHashMap(malDiaArray, Constants.PositionComplianceMalfunction);
            }
            if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.MissingDataDiag, getActivity())) {   // check missing data dia permission
                parseListInHashMap(malDiaArray, Constants.MissingDataDiagnostic);
            }


            if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.DataTransferDiag, getActivity())) {      // check data transfer dia permission
                parseListInHashMap(malDiaArray, Constants.DataTransferDiagnostic);
            }
            if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.DataTransferComplMal, getActivity())) {      // check Data Transfer Mal permission
                parseListInHashMap(malDiaArray, Constants.DataTransferMalfunction);
            }


            if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.UnidentifiedDiag, getActivity())) {      // check unidentified dia permission
                parseListInHashMap(malDiaArray, Constants.UnIdentifiedDrivingDiagnostic);
            }

            if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.DataRecComMal, getActivity())) {     // check data rec mal permission
                parseListInHashMap(malDiaArray, Constants.DataRecordingComplianceMalfunction);
            }

            if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.TimingCompMal, getActivity())) {     // check Timing compliance mal permission
                parseListInHashMap(malDiaArray, Constants.TimingComplianceMalfunction);
            }

            setPagerAdapter(position, false);

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

                    String DrId = mainObj.getString(ConstantsKeys.DriverId);

                    if (DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction) ||
                            DetectionDataEventCode.equals(Constants.EngineSyncMalfunctionEvent) ||
                            DetectionDataEventCode.equals(Constants.PositionComplianceMalfunction) ||
                            DetectionDataEventCode.equals(Constants.DataTransferMalfunction) ||
                            DetectionDataEventCode.equals(Constants.UnIdentifiedDrivingDiagnostic) ||
                            DetectionDataEventCode.equals(Constants.DataRecordingComplianceMalfunction) ||
                            DetectionDataEventCode.equals(Constants.TimingComplianceMalfunction)) {
                        parseData(mainObj);
                    } else {
                        if (DrId.equals(DriverId) || DrId.equals("0")) {
                            parseData(mainObj);
                        }
                    }

                }
            }

            // add data in header list
            if(headerModel != null) {
                boolean isDiagnostic = constants.isValidInteger(EventType);
                // add data in header list
                if (isDiagnostic) {   // Valid integer is Diagnostic
                    diagnosticHeaderList.add(headerModel);
                    diagnosticChildHashMap.put(EventType, malfunctionChildList);

                } else { // InValid integer is Malfunction
                    malfunctionHeaderList.add(headerModel);
                    malfunctionChildHashMap.put(EventType, malfunctionChildList);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void parseData(JSONObject mainObj){
        try{
            String EventType = mainObj.getString(ConstantsKeys.DetectionDataEventCode);
            boolean IsClearEvent = mainObj.getBoolean(ConstantsKeys.IsClearEvent);

            if (IsClearEvent == false) {
                MalDiaEventModel eventModel = constants.getMalDiaEventDetails(getActivity(), EventType);

                headerModel = new MalfunctionHeaderModel(
                        eventModel.getEventTitle(), EventType, eventModel.getEventDesc(),
                        IsClearEvent, true, "" + malfunctionChildList.size());

                DateTime EventDateTime = globally.getDateTimeObj(mainObj.getString(ConstantsKeys.EventDateTime), false);
                String driverTimeZone = String.valueOf(EventDateTime.plusHours(Integer.parseInt(OffsetFromUTC)));

                String EngHrs = "", StartOdometer = "0", clearOdometer = "0", finalOdometer = "0";
                if (mainObj.has(ConstantsKeys.EngineHours)) {
                    EngHrs = mainObj.getString(ConstantsKeys.EngineHours);
                }

                if (mainObj.has(ConstantsKeys.StartOdometer)) {
                    StartOdometer = mainObj.getString(ConstantsKeys.StartOdometer); //constants.kmToMiles(
                }

                if (mainObj.has(ConstantsKeys.ClearOdometer)) {
                    clearOdometer = mainObj.getString(ConstantsKeys.ClearOdometer);
                }

                try {
                    if(EventType.equals(Constants.PowerComplianceMalfunction) && clearOdometer.length() > 0){
                        if (constants.isExponentialValue(clearOdometer)) {
                            finalOdometer = BigDecimal.valueOf(Double.parseDouble(clearOdometer)).toPlainString();
                        }else{
                            finalOdometer = clearOdometer;
                        }
                    }else {
                        if (constants.isExponentialValue(StartOdometer)) {
                            finalOdometer = BigDecimal.valueOf(Double.parseDouble(StartOdometer)).toPlainString();
                        }else{
                            finalOdometer = StartOdometer;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String TotalMinutes = "--";

                if(EventType.equals(Constants.PowerComplianceDiagnostic) || EventType.equals(Constants.PowerComplianceMalfunction) ||
                          EventType.equals(Constants.UnIdentifiedDrivingDiagnostic) || EventType.equals(Constants.MissingDataDiagnostic) ||
                        EventType.equals(Constants.DataRecordingComplianceMalfunction) || EventType.equals(Constants.TimingComplianceMalfunction)){
                    if(mainObj.has(ConstantsKeys.TotalMinutes)){
                        TotalMinutes = mainObj.getString(ConstantsKeys.TotalMinutes);
                    }

                    if(EventType.equals(Constants.MissingDataDiagnostic)){
                        TotalMinutes = "--";
                    }
                }else{
                    TotalMinutes = ""+constants.getMinDifference(mainObj.getString(ConstantsKeys.EventDateTime), globally.GetCurrentUTCTimeFormat());
                }

                // Child array event
                MalfunctionModel malfunctionModel = new MalfunctionModel(
                        Country,
                        VIN,
                        CompanyId,
                        mainObj.getString(ConstantsKeys.EventDateTime),
                        EngHrs,
                        finalOdometer,
                        mainObj.getString(ConstantsKeys.DetectionDataEventCode),
                        mainObj.getString(ConstantsKeys.DriverId),
                        mainObj.getString(ConstantsKeys.CurrentStatus),
                        "", "", "", "",
                        driverTimeZone, "--", "--", TotalMinutes   //TotalMinutes value is passing in getId()
                );

                // add data in child list
                malfunctionChildList.add(malfunctionModel);



            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
