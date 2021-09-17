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
import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.MalfunctionDiagnosticMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.MalDiaEventModel;
import com.models.MalfunctionHeaderModel;
import com.models.MalfunctionModel;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MalfunctionDiagnosticHistoryFragment extends Fragment implements View.OnClickListener {


    View rootViewDia;
    RelativeLayout malfunctionActionBar, eldMenuLay, rightMenuBtn;
    ExpandableListView diagnosticExpandList;
    TextView noRecordTV, EldTitleTV;
    ImageView eldMenuBtn;
    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
    DBHelper dbHelper;
    Constants constants;

    List<MalfunctionModel> malfunctionChildList = new ArrayList<>();

    List<MalfunctionHeaderModel> diagnosticHeaderList = new ArrayList<>();
    private HashMap<String, List<MalfunctionModel>> diagnosticChildHashMap = new HashMap<>();

    String DriverId = "", VIN = "", Country, OffsetFromUTC, CompanyId;


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

        constants = new Constants();
        dbHelper = new DBHelper(getActivity());
        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();

        malfunctionActionBar    = (RelativeLayout)view.findViewById(R.id.malfunctionActionBar);
        eldMenuLay      = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        rightMenuBtn   = (RelativeLayout)view.findViewById(R.id.rightMenuBtn);

        diagnosticExpandList    = (ExpandableListView)view.findViewById(R.id.malfunctionExpandList);
        noRecordTV           = (TextView)view.findViewById(R.id.noRecordTV);
        EldTitleTV      = (TextView) view.findViewById(R.id.EldTitleTV);

        eldMenuBtn      = (ImageView)view.findViewById(R.id.eldMenuBtn);
        eldMenuBtn.setImageResource(R.drawable.back_white);

        EldTitleTV.setText(getResources().getString(R.string.mal_dia_history) );
        rightMenuBtn.setVisibility(View.GONE);

        DriverId                = SharedPref.getDriverId( getActivity());
        VIN                     = SharedPref.getVINNumber(getActivity());
        Country                 = constants.getCountryName(getActivity());
        OffsetFromUTC           = DriverConst.GetDriverSettings(DriverConst.OffsetHours, getActivity());
        CompanyId               = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());


        viewOfflineData();


        eldMenuLay.setOnClickListener(this);
    }


    void viewOfflineData(){
        try{
            JSONArray malDiaArray = malfunctionDiagnosticMethod.getMalDiaDurationArray(dbHelper);

            Log.d("malDiaArray", "malDiaArray: " + malDiaArray);

            try{
                diagnosticHeaderList        = new ArrayList<>();
                diagnosticChildHashMap      = new HashMap<>();
                malfunctionChildList        = new ArrayList<>();


                for(int  i = 0 ; i < malDiaArray.length() ; i++){
                    malfunctionChildList        = new ArrayList<>();

                    JSONObject mainObj = (JSONObject)malDiaArray.get(i);
                    String EventType = mainObj.getString(ConstantsKeys.DetectionDataEventCode);
                    boolean IsClearEvent = mainObj.getBoolean(ConstantsKeys.IsClearEvent);

                    if(IsClearEvent == true) {
                        MalDiaEventModel eventModel = constants.getMalDiaEventDetails(getActivity(), EventType);

                        MalfunctionHeaderModel headerModel = new MalfunctionHeaderModel(
                                eventModel.getEventTitle(), EventType, eventModel.getEventDesc(),
                                IsClearEvent, true);

                        DateTime EventDateTime = Globally.getDateTimeObj(mainObj.getString(ConstantsKeys.EventDateTime), false);
                        String driverTimeZone = String.valueOf(EventDateTime.plusHours(Integer.parseInt(OffsetFromUTC)));

                        String EngHrs = "";
                        if (mainObj.has(ConstantsKeys.ClearEngineHours)) {
                            EngHrs = mainObj.getString(ConstantsKeys.ClearEngineHours);
                        }
                        // Child array event
                        MalfunctionModel malfunctionModel = new MalfunctionModel(
                                Country,
                                VIN,
                                CompanyId,
                                mainObj.getString(ConstantsKeys.EventDateTime),
                                EngHrs,
                                "--", "", "", "",
                                "", "", "", "",
                                driverTimeZone, "--", "--", ""
                        );

                        // add data in child list
                        malfunctionChildList.add(malfunctionModel);


                        // add data in header list
                            diagnosticHeaderList.add(headerModel);
                            diagnosticChildHashMap.put(EventType, malfunctionChildList);

                    }
                }

                notifyMalfunctionAdapter(noRecordTV, diagnosticExpandList, diagnosticHeaderList, diagnosticChildHashMap);

            }catch (Exception e){
                e.printStackTrace();
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
            MalfunctionAdapter adapter = new MalfunctionAdapter(getActivity(), DriverId, headerList, childHashMap);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.eldMenuLay:
                getParentFragmentManager().popBackStack();
                break;
        }
    }
}
