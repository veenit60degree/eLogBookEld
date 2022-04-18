package com.messaging.logistic.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.SaveDriverLogPost;
import com.custom.dialogs.AdverseRemarksDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.messaging.logistic.UnidentifiedActivity;
import com.models.DriverLocationModel;
import com.shared.pref.StatePrefManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnidentifiedRecordDetailFragment extends Fragment implements View.OnClickListener {

    View rootView;
    RelativeLayout rightMenuBtn, eldMenuLay;
    TextView EldTitleTV;
    TextView recordTimeTxtVw, endOdoTxtVw, startOdoTxtVw, endTimeTxtVw, startTimeTxtVw, endLocTxtVw, startLocTxtVw, totalKmTxtVw, totalKmLabelTV;
    ImageView eldMenuBtn;
    Button rejectRecordBtn, claimRecordBtn;
    RadioGroup unIdentifyRadGroup;
    RadioButton drivingRadBtn, onDutyRadBtn, personalRadBtn;
    AdverseRemarksDialog remarksDialog;

    SaveDriverLogPost claimRejectRecordPost;
    final int ClaimRecordFlag                   = 101;
    final int RejectRecordFlag                  = 102;
    final int RejectCompanyAssignedRecordFlag   = 103;

    String CurrentCycleId = "", StartLocationKM = "";
    String DriverId = "", DriverStatusId = "", AssignedRecordsId = "", unAssignedVehicleMilesId = "",  DriverName = "",VIN = "";
    boolean isCompanyAssigned = false;
    ProgressDialog progressDialog;
    Constants constant;
    String StartOdometer = "", EndOdometer = "", StartDateTime = "", EndDateTime = "",
            StartLocation = "", EndLocation = "", EndLocationKM = "",
            TotalMiles = "", TotalKm = "";
    StatePrefManager statePrefManager;

    List<String> StateArrayList;
    List<DriverLocationModel> StateList;


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
            rootView = inflater.inflate(R.layout.unidentified_details, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);


        return rootView;
    }


    void initView(View view) {

        constant = new Constants();
        claimRejectRecordPost   = new SaveDriverLogPost(getActivity(), apiResponse );
        rightMenuBtn = (RelativeLayout)view.findViewById(R.id.rightMenuBtn);
        eldMenuLay = (RelativeLayout)view.findViewById(R.id.eldMenuLay);

        recordTimeTxtVw = (TextView)view.findViewById(R.id.recordTimeTxtVw);
        startOdoTxtVw = (TextView)view.findViewById(R.id.startOdoTxtVw);
        endOdoTxtVw = (TextView)view.findViewById(R.id.endOdoTxtVw);

        startTimeTxtVw = (TextView)view.findViewById(R.id.startTimeTxtVw);
        endTimeTxtVw = (TextView)view.findViewById(R.id.endTimeTxtVw);
        startLocTxtVw = (TextView)view.findViewById(R.id.startLocTxtVw);
        endLocTxtVw = (TextView)view.findViewById(R.id.endLocTxtVw);
        totalKmTxtVw = (TextView)view.findViewById(R.id.totalKmTxtVw);
        totalKmLabelTV= (TextView)view.findViewById(R.id.totalKmLabelTV);

        EldTitleTV = (TextView)view.findViewById(R.id.EldTitleTV);
        eldMenuBtn = (ImageView) view.findViewById(R.id.eldMenuBtn);

        rejectRecordBtn = (Button)view.findViewById(R.id.rejectRecordBtn);
        claimRecordBtn = (Button)view.findViewById(R.id.claimRecordBtn);
        
        unIdentifyRadGroup = (RadioGroup)view.findViewById(R.id.unIdentifyRadGroup);
        drivingRadBtn = (RadioButton) view.findViewById(R.id.drivingRadBtn);
        onDutyRadBtn = (RadioButton)view.findViewById(R.id.onDutyRadBtn);
        personalRadBtn = (RadioButton)view.findViewById(R.id.personalRadBtn);


        eldMenuBtn.setImageResource(R.drawable.back_btn);
        EldTitleTV.setText(getString(R.string.unIdentified_record_details));
        rightMenuBtn.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");
        statePrefManager = new StatePrefManager();

        getData();
        AddStatesInList();

        unIdentifyRadGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.drivingRadBtn) {
                    //StatusId = Globally.DRIVING;
                    drivingRadBtn.setTextColor(getResources().getColor(R.color.hos_location));
                    onDutyRadBtn.setTextColor(getResources().getColor(R.color.black_semi));
                    personalRadBtn.setTextColor(getResources().getColor(R.color.black_semi));
                }else if(checkedId == R.id.onDutyRadBtn){
                    //StatusId = Globally.ON_DUTY;
                    drivingRadBtn.setTextColor(getResources().getColor(R.color.black_semi));
                    onDutyRadBtn.setTextColor(getResources().getColor(R.color.hos_location));
                    personalRadBtn.setTextColor(getResources().getColor(R.color.black_semi));
                }else{
                    // StatusId = Globally.OFF_DUTY;
                    drivingRadBtn.setTextColor(getResources().getColor(R.color.black_semi));
                    onDutyRadBtn.setTextColor(getResources().getColor(R.color.black_semi));
                    personalRadBtn.setTextColor(getResources().getColor(R.color.hos_location));
                }
            }
        });


        CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());

        eldMenuLay.setOnClickListener(this);
        rejectRecordBtn.setOnClickListener(this);
        claimRecordBtn.setOnClickListener(this);

    }


    private void getData(){



        Bundle getBundle        = this.getArguments();
        if(getBundle != null) {
            DriverId = getBundle.getString(ConstantsKeys.DriverId);
            VIN = getBundle.getString(ConstantsKeys.VIN);
            DriverName = getBundle.getString(ConstantsKeys.UserName);
            DriverStatusId = getBundle.getString(ConstantsKeys.DriverStatusId);
            unAssignedVehicleMilesId = getBundle.getString(ConstantsKeys.UnAssignedVehicleMilesId);
            AssignedRecordsId = getBundle.getString(ConstantsKeys.AssignedUnidentifiedRecordsId);

            isCompanyAssigned = getBundle.getBoolean(ConstantsKeys.CompanyAssigned);


            StartOdometer = getBundle.getString(ConstantsKeys.StartOdometer);
            EndOdometer = getBundle.getString(ConstantsKeys.EndOdometer);
            StartDateTime = getBundle.getString(ConstantsKeys.StartDateTime);
            EndDateTime = getBundle.getString(ConstantsKeys.EndDateTime);
            StartLocation = getBundle.getString(ConstantsKeys.StartLocation);
            EndLocation = getBundle.getString(ConstantsKeys.EndLocation);
            StartLocationKM = getBundle.getString(ConstantsKeys.StartLocationKM);
            EndLocationKM = getBundle.getString(ConstantsKeys.EndLocationKM);

            TotalMiles = getBundle.getString(ConstantsKeys.TotalMiles);
            TotalKm = getBundle.getString(ConstantsKeys.TotalKM);
            getBundle.clear();
        }
        String startTime = getTime(StartDateTime);
        String endTime = getTime(EndDateTime);

        if(TotalMiles.length() == 0){
            TotalMiles = "N/A";
        }

        if(TotalKm.length() == 0){
            TotalKm = "N/A";
        }

        startOdoTxtVw.setText(StartOdometer);
        endOdoTxtVw.setText(EndOdometer);

        startTimeTxtVw.setText(startTime);
        endTimeTxtVw.setText(endTime);

        if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
            totalKmTxtVw.setText(TotalMiles);

            startLocTxtVw.setText(StartLocation);
            endLocTxtVw.setText(EndLocation);

        }else{
            totalKmLabelTV.setText(getString(R.string.total_km));
            totalKmTxtVw.setText(TotalKm);

            startLocTxtVw.setText(StartLocationKM);
            endLocTxtVw.setText(EndLocationKM);


        }

    }


    String getTime(String date){
        DateTime dateTime = new DateTime(Globally.getDateTimeObj(date, false));
        int monthOfYear = dateTime.getMonthOfYear();
        return dateTime.getDayOfMonth() + " " + Globally.MONTHS[monthOfYear - 1] + " " + Globally.Convert12HourFormatTime(date);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.eldMenuLay:
                eldMenuLay.setEnabled(false);
                getParentFragmentManager().popBackStack();
                break;

            case R.id.rejectRecordBtn:

                boolean startOdometer = false;
                boolean endOdometer = false;
                boolean startLocation = false;
                boolean endLocation = false;


                try {

                    if (remarksDialog != null && remarksDialog.isShowing())
                        remarksDialog.dismiss();

                    if(constant.isActionAllowed(getContext())) {

                        remarksDialog = new AdverseRemarksDialog(getActivity(), false, false,false,false,false,false, isCompanyAssigned,null,null,new RemarksListener());
                        remarksDialog.show();
                    }else{
                        Globally.EldScreenToast(rejectRecordBtn, getString(R.string.stop_vehicle_alert),
                                getResources().getColor(R.color.colorVoilation));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }




                break;

            case R.id.claimRecordBtn:

                try {
                    if(constant.isActionAllowed(getContext())) {
                        int radioButtonID   = unIdentifyRadGroup.getCheckedRadioButtonId();
                        View radioButton    = unIdentifyRadGroup.findViewById(radioButtonID);
                        int idx             = unIdentifyRadGroup.indexOfChild(radioButton);

                        DriverStatusId = Constants.getDriverStatus(idx);
                        if (idx != -1){
                            startOdometer = StartOdometer.equals("");
                            endOdometer = EndOdometer.equals("");
                            startLocation = StartLocation.equals("");
                            endLocation = EndLocation.equals("");
                            remarksDialog = new AdverseRemarksDialog(getActivity(), false, true, isCompanyAssigned,startOdometer,endOdometer,startLocation,endLocation,StateList,StateArrayList, new RemarksListener());
                            remarksDialog.show();
                        }else{
                            Globally.EldScreenToast(TabAct.sliderLay, getResources().getString(R.string.select_status_first),
                                    getResources().getColor(R.color.colorVoilation));
                        }

                    }else{
                        Globally.EldScreenToast(rejectRecordBtn, getString(R.string.stop_vehicle_alert),
                                getResources().getColor(R.color.colorVoilation));
                    }

               } catch (Exception e) {
                        e.printStackTrace();
                    }

                break;

        }
    }



    private void AddStatesInList() {
        int stateListSize = 0;
        StateArrayList = new ArrayList<String>();
        StateList = new ArrayList<DriverLocationModel>();

        try {
            StateList = statePrefManager.GetState(getActivity());
            StateList.add(0, new DriverLocationModel("", "Select", ""));
            stateListSize = StateList.size();
        } catch (Exception e) {
            stateListSize = 0;
        }

        for (int i = 0; i < stateListSize; i++) {
            StateArrayList.add(StateList.get(i).getState());
        }


    }

    private class RemarksListener implements AdverseRemarksDialog.RemarksListener{

        @Override
        public void CancelReady() {

            try {
                if (remarksDialog != null && remarksDialog.isShowing())
                    remarksDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void JobBtnReady(String reason, boolean isClaim, boolean isCompanyAssigned,String startOdo,String endOdo,String startLoc,String endLoc,String StartCity,String StartState,String StartCountry,String EndCity,String EndState,String EndCountry,boolean startOdometer, boolean endOdometer, boolean startLocation, boolean endLocation) {

            try {
                if (remarksDialog != null && remarksDialog.isShowing())
                    remarksDialog.dismiss();


                if(Globally.isConnected(getActivity())) {
                    if(isClaim) {

                        if(StartLocationKM.length() == 0 && startLoc.length() == 0){
                            Constants.IsUnidentifiedLocMissing = true;
                        }

                        JSONArray claimArray = new JSONArray();
                        JSONObject claimData = Constants.getClaimRecordInputsAsJson(DriverId,VIN, DriverStatusId,
                                unAssignedVehicleMilesId, AssignedRecordsId, reason, DriverName,startOdo,endOdo,startLoc,endLoc,StartCity,StartState,StartCountry,EndCity,EndState,EndCountry,startOdometer,endOdometer,startLocation,endLocation);
                        claimArray.put(claimData);

                        progressDialog.show();
                        claimRejectRecordPost.PostDriverLogData(claimArray, APIs.CLAIM_UNIDENTIFIED_RECORD, Constants.SocketTimeout20Sec, true, false, 0, ClaimRecordFlag);

                    }else{

                        if(isCompanyAssigned){
                            JSONArray companyRejectedArray = new JSONArray();
                            JSONObject obj = Constants.getCompanyRejectedRecordInputs(DriverId, unAssignedVehicleMilesId, AssignedRecordsId,  reason);
                            companyRejectedArray.put(obj);

                            progressDialog.show();
                            claimRejectRecordPost.PostDriverLogData(companyRejectedArray, APIs.REJECT_COMPANY_ASSIGNED_RECORD, Constants.SocketTimeout20Sec, true,
                                    false, 0, RejectCompanyAssignedRecordFlag);

                        }else{
                            JSONArray rejectedArray = new JSONArray();
                            JSONObject obj = Constants.getRejectedRecordInputs(DriverId, unAssignedVehicleMilesId, reason);
                            rejectedArray.put(obj);

                            progressDialog.show();
                            claimRejectRecordPost.PostDriverLogData(rejectedArray, APIs.REJECT_UNIDENTIFIED_RECORD, Constants.SocketTimeout20Sec, true,
                                    false, 0, RejectRecordFlag);
                        }
                    }
                }else{
                    Globally.EldScreenToast(TabAct.sliderLay, Globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }




    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse apiResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, int inputDataLength) {

            try {

                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ConstantsKeys.Status);
                if (status.equalsIgnoreCase("true")) {

                    switch (flag) {

                        case ClaimRecordFlag:

                            Globally.EldScreenToast(TabAct.sliderLay,
                                    getResources().getString(R.string.claim_successfully),
                                    getResources().getColor(R.color.color_eld_theme));
                            UnidentifiedActivity.isUnIdentifiedRecordClaimed = true;

                            if(Constants.IsUnidentifiedLocMissing){
                                Constants.IsUnidentifiedLocMissing = false;

                                try {
                                    Intent intent = new Intent(ConstantsKeys.SuggestedEdit);
                                    intent.putExtra(ConstantsKeys.PersonalUse75Km, false);
                                    intent.putExtra(ConstantsKeys.IsUnIdenLocMissing, true);
                                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }

                            constant.refreshEventDataFromService(getActivity());

                            break;

                        case RejectRecordFlag:
                            Constants.IsUnidentifiedLocMissing = false;
                            Globally.EldScreenToast(TabAct.sliderLay,
                                    getResources().getString(R.string.reject_successfully),
                                    getResources().getColor(R.color.color_eld_theme));

                           // constants.refreshEventDataFromService(getActivity());

                            break;

                        case RejectCompanyAssignedRecordFlag:
                            Globally.EldScreenToast(TabAct.sliderLay,
                                    getResources().getString(R.string.reject_successfully),
                                    getResources().getColor(R.color.color_eld_theme));
                            break;

                    }

                    getParentFragmentManager().popBackStack();

                }else{
                    // {"Status":false,"Message":"Failed..","Data":null}
                    Globally.EldScreenToast(TabAct.sliderLay, obj.getString("Message"),
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


            Globally.EldScreenToast(TabAct.sliderLay, error,
                    getResources().getColor(R.color.colorVoilation));
        }

    };

}
