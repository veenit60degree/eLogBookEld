package com.messaging.logistic.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
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
import com.constants.SaveDriverLogPost;
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

      /*  try {
            String data = "[{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-09-23T15:51:07\",\"EventEndDateTime\":\"2021-09-24T10:23:51.303\",\"DetectionDataEventCode\":\"5\",\"TotalMinutes\":1112,\"IsClearEvent\":false,\"ClearEngineHours\":\"0\",\"ClearOdometer\":\"\",\"StartOdometer\":\"0\",\"EngineHours\":\"0\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-09-24T17:58:49\",\"EventEndDateTime\":\"2021-09-24T23:54:50.103\",\"DetectionDataEventCode\":\"5\",\"TotalMinutes\":356,\"IsClearEvent\":false,\"ClearEngineHours\":\"0\",\"ClearOdometer\":\"\",\"StartOdometer\":\"0\",\"EngineHours\":\"0\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-09-27T17:11:56\",\"EventEndDateTime\":\"2021-09-28T14:16:12.393\",\"DetectionDataEventCode\":\"5\",\"TotalMinutes\":1265,\"IsClearEvent\":false,\"ClearEngineHours\":\"0\",\"ClearOdometer\":\"\",\"StartOdometer\":\"0\",\"EngineHours\":\"0\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-09-28T19:03:05\",\"EventEndDateTime\":\"2021-09-29T08:08:15.51\",\"DetectionDataEventCode\":\"5\",\"TotalMinutes\":785,\"IsClearEvent\":false,\"ClearEngineHours\":\"0\",\"ClearOdometer\":\"\",\"StartOdometer\":\"0\",\"EngineHours\":\"0\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-09-29T21:20:46\",\"EventEndDateTime\":\"2021-09-30T21:17:35.277\",\"DetectionDataEventCode\":\"5\",\"TotalMinutes\":1437,\"IsClearEvent\":false,\"ClearEngineHours\":\"0\",\"ClearOdometer\":\"\",\"StartOdometer\":\"0\",\"EngineHours\":\"0\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-09-30T20:54:31\",\"EventEndDateTime\":\"2021-10-01T09:47:35.307\",\"DetectionDataEventCode\":\"5\",\"TotalMinutes\":773,\"IsClearEvent\":false,\"ClearEngineHours\":\"0\",\"ClearOdometer\":\"\",\"StartOdometer\":\"0\",\"EngineHours\":\"0\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-04T19:06:27\",\"EventEndDateTime\":\"2021-10-04T19:08:29\",\"DetectionDataEventCode\":\"2\",\"TotalMinutes\":2,\"IsClearEvent\":true,\"ClearEngineHours\":\"23887.3\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1182596.465\",\"EngineHours\":\"23887.3\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-04T19:23:19\",\"EventEndDateTime\":\"2021-10-04T19:24:20\",\"DetectionDataEventCode\":\"2\",\"TotalMinutes\":1,\"IsClearEvent\":true,\"ClearEngineHours\":\"23887.6\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1182597.16\",\"EngineHours\":\"23887.6\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-04T20:04:24\",\"EventEndDateTime\":\"2021-10-05T17:07:16.237\",\"DetectionDataEventCode\":\"P\",\"TotalMinutes\":1263,\"IsClearEvent\":true,\"ClearEngineHours\":\"23887.7\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1.18259719E9\",\"EngineHours\":\"23887.7\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-04T23:35:55\",\"EventEndDateTime\":\"2021-10-05T21:18:10.06\",\"DetectionDataEventCode\":\"5\",\"TotalMinutes\":1303,\"IsClearEvent\":false,\"ClearEngineHours\":\"0\",\"ClearOdometer\":\"\",\"StartOdometer\":\"0\",\"EngineHours\":\"0\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-05T20:45:43\",\"EventEndDateTime\":\"2021-10-05T21:02:40\",\"DetectionDataEventCode\":\"2\",\"TotalMinutes\":17,\"IsClearEvent\":true,\"ClearEngineHours\":\"23896.25\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1182911.5250000001\",\"EngineHours\":\"23896.25\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-06T21:02:07\",\"EventEndDateTime\":\"2021-10-06T21:08:24\",\"DetectionDataEventCode\":\"2\",\"TotalMinutes\":6,\"IsClearEvent\":true,\"ClearEngineHours\":\"23900.35\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1183016.635\",\"EngineHours\":\"23900.35\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-06T21:21:06\",\"EventEndDateTime\":\"2021-10-06T22:31:04.467\",\"DetectionDataEventCode\":\"2\",\"TotalMinutes\":70,\"IsClearEvent\":true,\"ClearEngineHours\":\"23900.6\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1183025.84\",\"EngineHours\":\"23900.6\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-06T21:24:13\",\"EventEndDateTime\":\"2021-10-07T19:27:09.027\",\"DetectionDataEventCode\":\"E\",\"TotalMinutes\":1323,\"IsClearEvent\":true,\"ClearEngineHours\":\"23900.6\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1183025.84\",\"EngineHours\":\"23900.6\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-06T22:15:45\",\"EventEndDateTime\":\"2021-10-06T22:26:47\",\"DetectionDataEventCode\":\"1\",\"TotalMinutes\":11,\"IsClearEvent\":true,\"ClearEngineHours\":\"23900.6\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1.18302584E9\",\"EngineHours\":\"23900.6\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-06T22:31:22\",\"EventEndDateTime\":\"2021-10-07T19:27:09.027\",\"DetectionDataEventCode\":\"5\",\"TotalMinutes\":1256,\"IsClearEvent\":false,\"ClearEngineHours\":\"0\",\"ClearOdometer\":\"\",\"StartOdometer\":\"0\",\"EngineHours\":\"0\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-06T23:58:45\",\"EventEndDateTime\":\"2021-10-07T23:02:28.293\",\"DetectionDataEventCode\":\"P\",\"TotalMinutes\":1384,\"IsClearEvent\":true,\"ClearEngineHours\":\"23901.65\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1.183043785E9\",\"EngineHours\":\"23901.65\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-07T18:49:08\",\"EventEndDateTime\":\"2021-10-08T07:15:47.77\",\"DetectionDataEventCode\":\"5\",\"TotalMinutes\":746,\"IsClearEvent\":false,\"ClearEngineHours\":\"0\",\"ClearOdometer\":\"\",\"StartOdometer\":\"0\",\"EngineHours\":\"0\"},{\"DriverId\":\"127945\",\"EventDateTime\":\"2021-10-08T00:08:19\",\"EventEndDateTime\":\"2021-10-08T00:08:19\",\"DetectionDataEventCode\":\"2\",\"TotalMinutes\":0,\"IsClearEvent\":false,\"ClearEngineHours\":\"23908.5\",\"ClearOdometer\":\"\",\"StartOdometer\":\"1183258.57\",\"EngineHours\":\"23908.5\"}]";
            JSONArray durationArray = new JSONArray(data);
            //durationArray.remove(durationArray.length() - 1);
            malfunctionDiagnosticMethod.MalDiaDurationHelper(dbHelper, durationArray);
        }catch (Exception e){
            e.printStackTrace();
        }*/
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
        DateTime currentDate    = globally.getDateTimeObj(globally.GetCurrentDateTime(), false);
        String CurrentCycleId   = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());

        try {
            if (currentDate.toString().length() > 10) {
                ToDateTime              = currentDate.toString().substring(0,10);
                if ( CurrentCycleId.equals(globally.CANADA_CYCLE_1) || CurrentCycleId.equals(globally.CANADA_CYCLE_2) ) {
                    FromDateTime = String.valueOf(currentDate.minusDays(14)).substring(0, 10);  // // in CAN 14+1 days
                } else {
                    FromDateTime = String.valueOf(currentDate.minusDays(7)).substring(0, 10);   // // in US 7+1 days
                }
            }

            dateActionBarTV.setText(Html.fromHtml("<b><u>View Events</u></b>"));


            if (globally.isConnected(getContext())) {
                confirmCertifyLay.setVisibility(View.VISIBLE);
                JSONArray malArray1 = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);

                if (malArray1.length() > 0) {
                    saveDriverLogPost.PostDriverLogData(malArray1, APIs.MALFUNCTION_DIAGNOSTIC_EVENT, Constants.SocketTimeout30Sec, false, false, 1, 0);
                }else{
                    loadData();
                   // GetMalfunctionEvents(DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);
                }

            } else {
                confirmCertifyLay.setVisibility(View.GONE);

                loadData();

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

            Log.d("malDiaArray", "malDiaArray: " + malDiaArray);

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

                if(malDiaArray.length() == 0) {
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
                refreshButtonCLicked = true;
                GetMalfunctionEvents( DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);

                refreshEventDataFromService();

                break;

            case R.id.dateActionBarTV:
               // TabAct.host.setCurrentTab(0);

                MoveFragment(new MalfunctionDiagnosticHistoryFragment());

                break;


        }
    }


    protected void refreshEventDataFromService(){
        try {
            Constants.isCallMalDiaEvent = true;
            SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, getActivity());

            // call service onStart command to call event data to refresh
            Intent serviceIntent = new Intent(getActivity(), BackgroundLocationService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(serviceIntent);
            }
            getActivity().startService(serviceIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void MoveFragment(Fragment fragment){
        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.add(R.id.job_fragment, fragment);
        fragmentTran.addToBackStack("obd_diagnose");
        fragmentTran.commit();


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
            noRecordDiaTV           = (TextView)view.findViewById(R.id.noRecordTV);

            malfunctionActionBar.setVisibility(View.GONE);
        }

    }



    void checkLocMalfunction(){
        boolean isLocMalfunctionOccur = SharedPref.isLocMalfunctionOccur(getActivity());
        String malfunctionType = SharedPref.getLocationEventType(getActivity());
        if(isLocMalfunctionOccur && (malfunctionType.equals("m") || malfunctionType.equals("x"))){
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

            Log.d("error", ">>error: " + error);
            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();


        }
    };



    /* ---------------------- clear Log Request Response ---------------- */
    DriverLogResponse apiResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, int inputDataLength) {

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




    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int driver_id, int flag, int inputDataLength) {

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
            Log.d("errorrr ", ">>>error dialog: " );
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

            // Adding same type events in single group for Expandable ListView
            parseListInHashMap(malDiaArray, Constants.PowerComplianceDiagnostic);
            parseListInHashMap(malDiaArray, Constants.PowerComplianceMalfunction);

            parseListInHashMap(malDiaArray, Constants.EngineSyncDiagnosticEvent);
            parseListInHashMap(malDiaArray, Constants.EngineSyncMalfunctionEvent);

            parseListInHashMap(malDiaArray, Constants.MissingDataDiagnostic);
            parseListInHashMap(malDiaArray, Constants.PositionComplianceMalfunction);

            parseListInHashMap(malDiaArray, Constants.UnIdentifiedDrivingDiagnostic);

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
                    if (globally.isSingleDriver(getActivity())) {
                        parseData(mainObj);
                    } else {

                        String DrId = mainObj.getString(ConstantsKeys.DriverId);

                        if (DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction) ||
                                DetectionDataEventCode.equals(Constants.EngineSyncMalfunctionEvent) ||
                                DetectionDataEventCode.equals(Constants.PositionComplianceMalfunction)) {
                            parseData(mainObj);
                        } else {
                            if (DrId.equals(DriverId)) {
                                parseData(mainObj);
                            }
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

                String EngHrs = "", StartOdometer = "0";
                if (mainObj.has(ConstantsKeys.ClearEngineHours)) {
                    EngHrs = mainObj.getString(ConstantsKeys.ClearEngineHours);
                }

                if (mainObj.has(ConstantsKeys.StartOdometer)) {
                    StartOdometer = mainObj.getString(ConstantsKeys.StartOdometer); //constants.kmToMiles(

                }

                try {
                    if(constants.isExponentialValue(StartOdometer)){
                        StartOdometer = BigDecimal.valueOf(Double.parseDouble(StartOdometer)).toPlainString();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String TotalMinutes = "--";

                if(EventType.equals(Constants.PowerComplianceDiagnostic) || EventType.equals(Constants.PowerComplianceMalfunction)){
                    if(mainObj.has(ConstantsKeys.TotalMinutes)){
                        TotalMinutes = mainObj.getString(ConstantsKeys.TotalMinutes);
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
                        StartOdometer,
                        mainObj.getString(ConstantsKeys.DetectionDataEventCode),
                        "", "",
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
