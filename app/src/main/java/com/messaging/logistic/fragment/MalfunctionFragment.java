package com.messaging.logistic.fragment;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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

import com.adapter.logistic.MalfunctionAdapter;
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
import com.local.db.ConstantsKeys;
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

public class MalfunctionFragment extends Fragment implements View.OnClickListener {

    View rootView;
    ExpandableListView malfunctionExpListView;
    TextView EldTitleTV, noDataEldTV, dateActionBarTV;
    RelativeLayout rightMenuBtn, eldMenuLay;
    String DriverId = "", DeviceId = "", VIN = "", FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId;
    Map<String, String> params;
    VolleyRequest GetMalfunctionEvents;
    List<MalfunctionModel> malfunctionChildList = new ArrayList<>();
    List<MalfunctionHeaderModel> malfunctionHeaderList = new ArrayList<>();
    private HashMap<String, List<MalfunctionModel>> malfunctionChildHashMap = new HashMap<>();
    public static TextView invisibleMalfnBtn;
    MalfunctionAdapter malfunctionAdapter;
    MalfunctionDialog malfunctionDialog;
    SaveLogJsonObj clearRecordPost;
    ProgressDialog progressDialog;
    Constants constants;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_malfunction, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }


    void initView(View view) {

        GetMalfunctionEvents  = new VolleyRequest(getActivity());
        clearRecordPost   = new SaveLogJsonObj(getActivity(), apiResponse );
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");

        constants  = new Constants();
        malfunctionExpListView = (ExpandableListView) view.findViewById(R.id.malfunctionExpandList);
        noDataEldTV = (TextView) view.findViewById(R.id.noRecordTV);
        EldTitleTV = (TextView) view.findViewById(R.id.EldTitleTV);
        dateActionBarTV = (TextView) view.findViewById(R.id.dateActionBarTV);
        invisibleMalfnBtn = (TextView)view.findViewById(R.id.invisibleMalfnBtn);

        rightMenuBtn = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        eldMenuLay   = (RelativeLayout) view.findViewById(R.id.eldMenuLay);

        DriverId = SharedPref.getDriverId( getActivity());
        DeviceId = SharedPref.GetSavedSystemToken(getActivity());

        rightMenuBtn.setVisibility(View.GONE);
        dateActionBarTV.setVisibility(View.VISIBLE);
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.whiteee));

        //"<html>  <u>Logout</u> </html>"
        EldTitleTV.setText(getResources().getString(R.string.malfunction));
        dateActionBarTV.setText(Html.fromHtml("<b><u>" + getString(R.string.ClearAll) + "</u></b>"));

        malfunctionExpListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                switch (groupPosition) {
                    case 0:
                        Log.d(">>>", "" + groupPosition);
                        break;
                    case 1:
                        Log.d(">>>", "" + groupPosition);
                        break;
                    case 2:
                        Log.d(">>>", "" + groupPosition);
                        break;
                    case 3:
                        Log.d(">>>", "" + groupPosition);
                        break;
                }
                return false;
            }
        });

        checkLocMalfunction();

        dateActionBarTV.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        invisibleMalfnBtn.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        DriverId                = SharedPref.getDriverId( getActivity());
        VIN                     = SharedPref.getVINNumber(getActivity());
        Country                 = constants.getCountryName(getActivity());
        OffsetFromUTC           = DriverConst.GetDriverSettings(DriverConst.OffsetHours, getActivity());
        CompanyId               = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
        DateTime currentDate    = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
        ToDateTime              = currentDate.toString().substring(0,10);
        String CurrentCycleId     = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());

        if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
            FromDateTime = String.valueOf(currentDate.minusDays(14)).substring(0, 10);  // // in CAN 14+1 days
        }else{
            FromDateTime = String.valueOf(currentDate.minusDays(7)).substring(0, 10);   // // in US 7+1 days
        }

        notifyAdapter();

        if(Globally.isConnected(getContext())) {
            GetMalfunctionEvents( DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);
        }else{
            Globally.EldScreenToast(dateActionBarTV, Globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.dateActionBarTV:
                Log.d("ClearAllClickEvent", "ClearAllClickEvent");


                if (malfunctionDialog != null && malfunctionDialog.isShowing())
                    malfunctionDialog.dismiss();

                if(Globally.isConnected(getContext())) {
                    if(malfunctionHeaderList.size() > 0) {
                        malfunctionDialog = new MalfunctionDialog(getActivity(), new ArrayList<MalfunctionModel>(),
                                new MalfunctionDiagnosticListener());
                        malfunctionDialog.show();
                    }else{
                        Globally.EldScreenToast(dateActionBarTV, getString(R.string.no_malfunction_diagnostc_records), getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    Globally.EldScreenToast(dateActionBarTV, Globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                }


                break;

            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;

            case R.id.invisibleMalfnBtn:
                GetMalfunctionEvents( DriverId, VIN, FromDateTime, ToDateTime, Country, OffsetFromUTC, CompanyId);
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
                        malfunctionHeaderList, malfunctionChildHashMap, null, null, getActivity());

                JSONArray eventId = new JSONArray(clearEventObj.getString(ConstantsKeys.EventList));
                if (eventId.length() > 0) {
                    progressDialog.show();
                    clearRecordPost.SaveLogJsonObj(clearEventObj, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT, Constants.SocketTimeout30Sec, true, false, 0, 101);
                }else{
                    if(SharedPref.IsClearDiagnostic(getActivity()) && SharedPref.IsClearMalfunction(getActivity())){
                        Globally.EldScreenToast(dateActionBarTV, getString(R.string.no_malfunction_diagnostc_valid), getResources().getColor(R.color.colorVoilation));
                    }else{
                        if(SharedPref.IsClearDiagnostic(getActivity())){
                            Globally.EldScreenToast(dateActionBarTV, getString(R.string.no_diagnostic_records), getResources().getColor(R.color.colorVoilation));
                        }else{
                            Globally.EldScreenToast(dateActionBarTV, getString(R.string.no_malfunction_records), getResources().getColor(R.color.colorVoilation));
                        }
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
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

                    checkLocMalfunction();

                    for(int  i = 0 ; i < malfunctionArray.length() ; i++){
                        malfunctionChildList = new ArrayList<>();

                        JSONObject mainObj = (JSONObject)malfunctionArray.get(i);
                        MalfunctionHeaderModel headerModel = new MalfunctionHeaderModel(
                                mainObj.getString(ConstantsKeys.EventName),
                                mainObj.getString(ConstantsKeys.EventCode),
                                mainObj.getString(ConstantsKeys.Definition)

                        );
                        // add data in header list
                        malfunctionHeaderList.add(headerModel);


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

                        // Add both list (Header/CHild) in hash map type list
                        malfunctionChildHashMap.put(mainObj.getString(ConstantsKeys.EventCode), malfunctionChildList);

                    }



                    notifyAdapter();

                } else {
                    malfunctionHeaderList = new ArrayList<>();
                    malfunctionChildList = new ArrayList<>();
                    malfunctionChildHashMap = new HashMap<>();

                    checkLocMalfunction();
                }
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
                        Globally.EldScreenToast(invisibleMalfnBtn, message,
                                getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    // {"Status":false,"Message":"Failed..","Data":null}
                    Globally.EldScreenToast(invisibleMalfnBtn, message,
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


            Globally.EldScreenToast(invisibleMalfnBtn, error,
                    getResources().getColor(R.color.colorVoilation));
        }

    };



    private void notifyAdapter(){

        if(malfunctionChildHashMap.size() > 0){
            noDataEldTV.setVisibility(View.GONE);
        }else{
            noDataEldTV.setVisibility(View.VISIBLE);
            if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
                SharedPref.setEldOccurences(SharedPref.isUnidentifiedOccur(getActivity()),
                        false,
                        false,
                        SharedPref.isSuggestedEditOccur(getActivity()), getActivity());
            }else{
                SharedPref.setEldOccurencesCo(SharedPref.isUnidentifiedOccurCo(getActivity()),
                        false,
                        false,
                        SharedPref.isSuggestedEditOccurCo(getActivity()), getActivity());
            }

        }

        try {
            malfunctionAdapter = new MalfunctionAdapter(getActivity(), DriverId, malfunctionHeaderList, malfunctionChildHashMap);
            malfunctionExpListView.setAdapter(malfunctionAdapter);
           // malfunctionAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }


    }





}