package com.messaging.logistic.fragment;
// Hello

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.adapter.logistic.UnIdentifiedListingAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.Slidingmenufunctions;
import com.constants.VolleyRequest;
import com.custom.dialogs.UnidentifiedDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UnidentifiedActivity;
import com.models.UnIdentifiedRecordModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnidentifiedFragment extends Fragment implements View.OnClickListener {

    View rootView;
    SharedPref sharedPref;
    ListView unIdentifiedListView;
    TextView EldTitleTV, noDataEldTV, dateActionBarTV;
    TextView claimRecordUnBtn, rejectRecordUnBtn, totalRecordsTV;
    public static CheckBox checkboxUnIdentifiedRecord;
    public static RelativeLayout rightMenuBtn;
    public  static boolean isItemViewClicked = false;
    RelativeLayout eldMenuLay, unIdentifiedTopLay;
    Map<String, String> params;
    VolleyRequest GetUnidentifiedRecords;
    String DriverId = "", DeviceId = "", DriverName = "", VIN = "", CurrentDate = "", Country = "";
    Globally global;
    Constants constants;
    final int UnidentifiedRecordFlag  = 101;
    UnIdentifiedListingAdapter listingAdapter;
    ArrayList<String> recordSelectedList = new ArrayList<>();
    List<UnIdentifiedRecordModel>  unIdentifiedRecordList = new ArrayList<>();

    SaveDriverLogPost claimRejectRecordPost;
    final int ClaimRecords          = 101;
    final int RejectRecords         = 102;
    final int RejectCompanyRecords  = 103;
    UnidentifiedDialog unidentifiedDialog;
    ProgressDialog progressDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.noti_history_fragment, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }


    void initView(View view) {

        sharedPref              = new SharedPref();
        global                  = new Globally();
        constants               = new Constants();
        GetUnidentifiedRecords  = new VolleyRequest(getActivity());
        claimRejectRecordPost   = new SaveDriverLogPost(getActivity(), apiResponse );

        unIdentifiedListView    = (ListView) view.findViewById(R.id.shippingListView);
        noDataEldTV             = (TextView)view.findViewById(R.id.noDataEldTV);
        EldTitleTV              = (TextView)view.findViewById(R.id.EldTitleTV);
        dateActionBarTV         = (TextView) view.findViewById(R.id.dateActionBarTV);
        claimRecordUnBtn        = (TextView) view.findViewById(R.id.claimRecordUnBtn);
        rejectRecordUnBtn       = (TextView) view.findViewById(R.id.rejectRecordUnBtn);
        totalRecordsTV          = (TextView)view.findViewById(R.id.totalRecordsTV);

        rightMenuBtn            = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        eldMenuLay              = (RelativeLayout) view.findViewById(R.id.eldMenuLay);
        unIdentifiedTopLay      = (RelativeLayout) view.findViewById(R.id.unIdentifiedTopLay);

        checkboxUnIdentifiedRecord = (CheckBox)view.findViewById(R.id.checkboxUnIdentifiedRecord);

        DeviceId                = sharedPref.GetSavedSystemToken(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");


        unIdentifiedTopLay.setVisibility(View.VISIBLE);
        unIdentifiedListView.setVisibility(View.VISIBLE);
        rightMenuBtn.setVisibility(View.GONE);
        dateActionBarTV.setVisibility(View.VISIBLE);
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.colorPrimary));


        EldTitleTV.setText(getResources().getString(R.string.unIdentified_records));
        if(sharedPref.IsAOBRD(getActivity())) {
            dateActionBarTV.setText(getString(R.string.aobrd));
        }else{
            dateActionBarTV.setText(getString(R.string.eld));
        }




        checkboxUnIdentifiedRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(!isItemViewClicked) {
                    boolean checkBoxSelection = compoundButton.isChecked();
                    setListSelectionRecord(checkBoxSelection);
                    Parcelable state = unIdentifiedListView.onSaveInstanceState();
                    unIdentifiedListView.onRestoreInstanceState(state);
                    notifyAdapter(checkBoxSelection, true);
                }
                isItemViewClicked = false;
            }
        });


        dateActionBarTV.setOnClickListener(this);
        rightMenuBtn.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        claimRecordUnBtn.setOnClickListener(this);
        rejectRecordUnBtn.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();

        CurrentDate             = global.getCurrentDateLocal();
        DriverId                = sharedPref.getDriverId( getActivity());
        VIN                     = sharedPref.getVINNumber(getActivity());
        DriverName              = Slidingmenufunctions.usernameTV.getText().toString();
        Country                 = constants.getCountryName(getActivity());

        notifyAdapter(false, false);

        if(Globally.isConnected(getContext())) {
            GetUnidentifiedRecords( DriverId, DeviceId, CurrentDate, VIN, UnidentifiedRecordFlag);
        }else{
            global.EldScreenToast(dateActionBarTV, global.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.dateActionBarTV:
                TabAct.host.setCurrentTab(0);
                break;

            case R.id.rightMenuBtn:

                GetUnidentifiedRecords( DriverId, DeviceId, CurrentDate, VIN, UnidentifiedRecordFlag);

                break;


            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;


            case  R.id.claimRecordUnBtn:

                if (unidentifiedDialog != null && unidentifiedDialog.isShowing())
                    unidentifiedDialog.dismiss();

                JSONArray claimArray = Constants.getClaimRecordsInArray("", "", "", "",
                        unIdentifiedRecordList,  recordSelectedList);

                if(getActivity() != null) {
                    if (claimArray.length() > 0) {
                        unidentifiedDialog = new UnidentifiedDialog(getActivity(), getResources().getString(R.string.claim), new UnIdentifiedListener());
                        unidentifiedDialog.show();

                    } else {
                        global.EldScreenToast(dateActionBarTV, "Select record first", getResources().getColor(R.color.colorVoilation));
                    }
                }

                break;

            case R.id.rejectRecordUnBtn:
                if (unidentifiedDialog != null && unidentifiedDialog.isShowing())
                    unidentifiedDialog.dismiss();

                JSONArray companyRejectedArray = Constants.getCompanyRejectRecordsInArray("", "", unIdentifiedRecordList,  recordSelectedList);
                JSONArray rejectedArray = Constants.getRejectRecordsInArray( "", "", unIdentifiedRecordList,  recordSelectedList);

                if(getActivity() != null) {
                    if (companyRejectedArray.length() > 0 || rejectedArray.length() > 0) {
                        unidentifiedDialog = new UnidentifiedDialog(getActivity(), getResources().getString(R.string.reject), new UnIdentifiedListener());
                        unidentifiedDialog.show();
                    } else {
                        global.EldScreenToast(dateActionBarTV, "Select record first", getResources().getColor(R.color.colorVoilation));
                    }
                }

                break;

        }
    }




    private class UnIdentifiedListener implements UnidentifiedDialog.RecordsListener{

        @Override
        public void RecordsOkBtn(String reason, String status, String recordType) {

            if (unidentifiedDialog != null && unidentifiedDialog.isShowing())
                unidentifiedDialog.dismiss();

            if(recordType.equals(getResources().getString(R.string.claim))) {
                JSONArray claimArray = Constants.getClaimRecordsInArray( DriverId, DriverName, reason, status, unIdentifiedRecordList, recordSelectedList);

                if (claimArray.length() > 0) {
                    progressDialog.show();
                    claimRejectRecordPost.PostDriverLogData(claimArray, APIs.CLAIM_UNIDENTIFIED_RECORD, Constants.SocketTimeout70Sec, true, false, 0, ClaimRecords);
                }

            }else{
                JSONArray companyRejectedArray = Constants.getCompanyRejectRecordsInArray(DriverId, reason,  unIdentifiedRecordList, recordSelectedList);
                JSONArray rejectedArray = Constants.getRejectRecordsInArray( DriverId, reason, unIdentifiedRecordList, recordSelectedList);

                if (companyRejectedArray.length() > 0 || rejectedArray.length() > 0) {

                    progressDialog.show();

                    if (companyRejectedArray.length() > 0 ) {
                        claimRejectRecordPost.PostDriverLogData(companyRejectedArray, APIs.REJECT_COMPANY_ASSIGNED_RECORD, Constants.SocketTimeout70Sec, true,
                                false, 0, RejectCompanyRecords);
                    }

                    if (rejectedArray.length() > 0) {
                        claimRejectRecordPost.PostDriverLogData(rejectedArray, APIs.REJECT_UNIDENTIFIED_RECORD, Constants.SocketTimeout70Sec, true,
                                false, 0, RejectRecords);
                    }
                }
            }
        }
    }


        /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse apiResponse = new DriverLogResponse() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag) {

                try {

                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();


                    JSONObject obj = new JSONObject(response);
                    String status = obj.getString("Status");
                    if (status.equalsIgnoreCase("true")) {

                        switch (flag) {

                            case ClaimRecords:
                                Globally.EldScreenToast(UnidentifiedFragment.rightMenuBtn,
                                        getResources().getString(R.string.claim_successfully),
                                        getResources().getColor(R.color.color_eld_theme));

                                break;

                            case RejectRecords:
                                Globally.EldScreenToast(UnidentifiedFragment.rightMenuBtn,
                                        getResources().getString(R.string.reject_successfully),
                                        getResources().getColor(R.color.color_eld_theme));
                                break;

                            case RejectCompanyRecords:
                                Globally.EldScreenToast(UnidentifiedFragment.rightMenuBtn,
                                        getResources().getString(R.string.reject_successfully),
                                        getResources().getColor(R.color.color_eld_theme));
                                break;

                        }

                        GetUnidentifiedRecords( DriverId, DeviceId, CurrentDate, VIN, UnidentifiedRecordFlag);

                    }else{
                        // {"Status":false,"Message":"Failed..","Data":null}
                        Globally.EldScreenToast(UnidentifiedFragment.rightMenuBtn, obj.getString("Message"),
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


                Globally.EldScreenToast(UnidentifiedFragment.rightMenuBtn, error,
                        getResources().getColor(R.color.colorVoilation));
            }

    };

    private void setListSelectionRecord(boolean isSelected){
        Log.d("recordSelectedList", "recordSelectedList: " + recordSelectedList);
        int count = 0;
        for(int i = 0 ; i < recordSelectedList.size() ; i++){
            if(isSelected){
                count++;
                recordSelectedList.set(i, "selected");
            }else{
                recordSelectedList.set(i, "");
            }
        }

        checkboxUnIdentifiedRecord.setText(getString(R.string.select_all) + " (" + count + ")");
    }




    /*================== Get Unidentified Records ===================*/
    void GetUnidentifiedRecords(final String DriverId, final String DeviceId, final String CurrentDate, String VIN, int flag){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.CurrentDate, CurrentDate);
         params.put(ConstantsKeys.VIN, VIN);
        params.put(ConstantsKeys.Country, Country);

        GetUnidentifiedRecords.executeRequest(Request.Method.POST, APIs.GET_UNIDENTIFIED_RECORDS , params, flag,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }





    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {
        @Override
        public void getResponse(String response, int flag) {


            try {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString("Status");
                if (status.equalsIgnoreCase("true")) {

                    switch (flag) {

                        case UnidentifiedRecordFlag:

                            UnidentifiedActivity.isUnIdentifiedRecordClaimed = true;
                            JSONObject dataObj = new JSONObject(obj.getString(ConstantsKeys.Data));
                            JSONArray unidentifiedArray = new JSONArray(dataObj.getString(ConstantsKeys.Unidentified));
                            JSONArray companyAssignedArray = new JSONArray(dataObj.getString(ConstantsKeys.CompanyAssigned));
                            unIdentifiedRecordList = new ArrayList<>();
                            recordSelectedList      = new ArrayList<String>();

                            for(int i = 0 ; i < unidentifiedArray.length() ; i++ ){
                                JSONObject objItem = (JSONObject)unidentifiedArray.get(i);

                                UnIdentifiedRecordModel model = new UnIdentifiedRecordModel(
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EquipmentNumber)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.VIN)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StartOdometer)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EndOdometer)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.TotalMiles)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.TotalKm)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.CompanyStartDateTime)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.CompanyEndDateTime)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StartLatitude)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StartLongitude)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EndLatitude)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EndLongitude)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StartLocation)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EndLocation)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.DriverStatusId)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.Remarks)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StatusId)),
                                        objItem.getString(ConstantsKeys.UnAssignedVehicleMilesId),
                                        objItem.getString(ConstantsKeys.AssignedUnidentifiedRecordsId),
                                        false,
                                        objItem.getString(ConstantsKeys.StartLocationKM),
                                        objItem.getString(ConstantsKeys.EndLocationKM)
                                        );

                                unIdentifiedRecordList.add(model);
                                recordSelectedList.add("");
                            }


                            for(int i = 0 ; i < companyAssignedArray.length() ; i++ ){
                                JSONObject objItem = (JSONObject)companyAssignedArray.get(i);

                                UnIdentifiedRecordModel model = new UnIdentifiedRecordModel(
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EquipmentNumber)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.VIN)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StartOdometer)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EndOdometer)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.TotalMiles)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.TotalKm)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.CompanyStartDateTime)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.CompanyEndDateTime)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StartLatitude)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StartLongitude)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EndLatitude)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EndLongitude)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StartLocation)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.EndLocation)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.DriverStatusId)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.Remarks)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.StatusId)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.UnAssignedVehicleMilesId)),
                                        Constants.CheckNullString(objItem.getString(ConstantsKeys.AssignedUnidentifiedRecordsId)),
                                        true,
                                         Constants.CheckNullString(objItem.getString(ConstantsKeys.StartLocationKM)),
                                         Constants.CheckNullString(objItem.getString(ConstantsKeys.EndLocationKM))
                                );

                                unIdentifiedRecordList.add(model);
                                recordSelectedList.add("");
                            }

                            checkboxUnIdentifiedRecord.setChecked(false);
                            notifyAdapter(false, false);

                            break;

                    }

                } else {

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
            switch (flag) {

                case UnidentifiedRecordFlag:

                    Globally.EldScreenToast(rightMenuBtn, "Error!! Check your internet connection", getResources().getColor(R.color.colorVoilation));

                    break;

            }


        }
    };


    void notifyAdapter(boolean isAllSelected, boolean isChecked){
        try{

            listingAdapter = new UnIdentifiedListingAdapter(getActivity(), DriverId, DriverName, isAllSelected, isChecked, recordSelectedList, unIdentifiedRecordList, this);
            unIdentifiedListView.setAdapter(listingAdapter);

        }catch (Exception e){ }

        try{

            if(unIdentifiedRecordList.size() > 0) {
                noDataEldTV.setVisibility(View.GONE);
                unIdentifiedTopLay.setVisibility(View.VISIBLE);

            }else {

                noDataEldTV.setVisibility(View.VISIBLE);
                unIdentifiedTopLay.setVisibility(View.GONE);
                if (sharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {    // Single Driver Type and Position is 0
                  //  sharedPref.setUnidentifiedAlertViewStatus(false, getContext());
                    sharedPref.setEldOccurences(false,
                            sharedPref.isMalfunctionOccur(getActivity()),
                            sharedPref.isDiagnosticOccur(getActivity()),
                            sharedPref.isSuggestedEditOccur(getActivity()), getActivity());
                }else{
                   // sharedPref.setUnidentifiedAlertViewStatusCo(false, getContext());
                    sharedPref.setEldOccurencesCo(false,
                            sharedPref.isMalfunctionOccurCo(getActivity()),
                            sharedPref.isDiagnosticOccurCo(getActivity()),
                            sharedPref.isSuggestedEditOccurCo(getActivity()), getActivity());
                }

            }
            EldTitleTV.setText(getResources().getString(R.string.unIdentified_records) + " (" + unIdentifiedRecordList.size() + ")");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}