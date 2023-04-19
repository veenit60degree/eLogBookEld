package com.custom.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.adapter.logistic.OtherOptionsAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.RecapViewMethod;
import com.als.logistic.SuggestedFragmentActivity;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.TabAct;
import com.als.logistic.fragment.EldFragment;
import com.models.OtherOptionsModel;
import com.models.RecapSignModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtherOptionsDialog extends Dialog {

    List<OtherOptionsModel> otherOptionList;
    ListView otherFeatureListView;
    RelativeLayout otherOptionMainLay;
    Constants constants;
    boolean isPendingNotification;
    boolean isGps;

    VolleyRequest GetEditedRecordRequest;
    Map<String, String> params;
    ProgressDialog progressDialog;
    String DriverId, CoDriverId, DeviceId, CurrentCycleId;
    int pendingNotificationCount, DriverType = 0;
    DriverPermissionMethod driverPermissionMethod;
    RecapViewMethod recapViewMethod;
    HelperMethods hMethods;
    DBHelper dbHelper;

    public OtherOptionsDialog(@NonNull Context context, boolean isPendingNotification, int pendingNotificationCount,
                              boolean isGps, int DriverType, String CurrentCycleId, DriverPermissionMethod driverPermissionMethod,
                              RecapViewMethod recapViewMethod, HelperMethods hMethods, DBHelper dbHelper) {
        super(context);
        this.isPendingNotification = isPendingNotification;
        this.pendingNotificationCount = pendingNotificationCount;
        this.isGps = isGps;
        this.DriverType = DriverType;

        this.CurrentCycleId = CurrentCycleId;
        this.driverPermissionMethod = driverPermissionMethod;
        this.recapViewMethod = recapViewMethod;
        this.hMethods = hMethods;
        this.dbHelper = dbHelper;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_other_options);
        setCancelable(true);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        GetEditedRecordRequest = new VolleyRequest(getContext());
        constants = new Constants();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading ...");

        DriverId        = SharedPref.getDriverId( getContext());
        DeviceId        = SharedPref.GetSavedSystemToken(getContext());

        if (!SharedPref.getDriverType(getContext()).equals(DriverConst.SingleDriver)) {
            if(DriverId.equals(DriverConst.GetDriverDetails(DriverConst.DriverID, getContext()))){
                CoDriverId   = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getContext());
            }else{
                CoDriverId   = DriverConst.GetDriverDetails(DriverConst.DriverID, getContext());
            }
        }

        otherFeatureListView = (ListView) findViewById(R.id.otherFeatureListView);
        otherOptionMainLay = (RelativeLayout) findViewById(R.id.otherOptionMainLay);

        boolean isAllowMalfunction = false;
        boolean isAllowUnIdentified = false;

        if(DriverType == Constants.MAIN_DRIVER_TYPE) {

            if(SharedPref.IsAllowMalfunction(getContext()) || SharedPref.IsAllowDiagnostic(getContext())){
                isAllowMalfunction = true;
            }
            if(SharedPref.IsShowUnidentifiedRecords(getContext()) ){
                isAllowUnIdentified = true;
            }

        }else{

            if(SharedPref.IsAllowMalfunctionCo(getContext()) || SharedPref.IsAllowDiagnosticCo(getContext())){
                isAllowMalfunction = true;
            }
            if(SharedPref.IsShowUnidentifiedRecordsCo(getContext()) ){
                isAllowUnIdentified = true;
            }
        }

        try {
            Globally Global = new Globally();
            JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DriverId), CoDriverId, dbHelper);
            List<RecapSignModel> signList   = constants.GetCertifySignList(recapViewMethod, DriverId, hMethods, dbHelper,
                    Global.GetCurrentDeviceDate(null, Global, getContext()), CurrentCycleId, logPermissionObj, Global, getContext());
            boolean isMissingLoc = false;
            for(int i = 0 ; i < signList.size() ; i++){
                if(signList.get(i).isMissingLocation()){
                    isMissingLoc = true;
                    break;
                }
            }
            boolean isUncertifyLog = constants.GetCertifyLogSignStatus(recapViewMethod, DriverId, dbHelper,
                    Global.GetCurrentDeviceDate(null, Global, getContext()), CurrentCycleId, logPermissionObj);

            otherOptionList = constants.getOtherOptionsList(getContext(), isAllowMalfunction, isAllowUnIdentified, isMissingLoc, isUncertifyLog);
            OtherOptionsAdapter adapter = new OtherOptionsAdapter(getContext(), isPendingNotification, pendingNotificationCount, isGps, otherOptionList);
            otherFeatureListView.setAdapter(adapter);
        }catch (Exception e){}
        
        otherFeatureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               // Logger.LogDebug("itemClick", "itemClick: " + position);

                switch (otherOptionList.get(position).getStatus()) {

                    case Constants.NOTIFICATION:
                        TabAct.host.setCurrentTab(3);
                        dismiss();
                        break;

                    case Constants.GPS:
                        if (isGps) {
                            Globally.EldScreenToast(EldFragment.refreshLogBtn, getContext().getResources().getString(R.string.gps_already_enabled),
                                    getContext().getResources().getColor(R.color.color_eld_theme));
                        } else {
                            getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                        dismiss();

                        break;

                    case Constants.MALFUNCTION:
                        TabAct.host.setCurrentTab(12);
                        dismiss();
                        break;

                    case Constants.UNIDENTIFIED:
                        TabAct.host.setCurrentTab(11);
                        dismiss();
                        break;


                    case Constants.SUGGESTED_LOGS:
                        if (Globally.isConnected(getContext())) {
                            GetSuggestedRecords();
                        } else {
                            Globally.EldScreenToast(EldFragment.refreshLogBtn, Globally.INTERNET_MSG,
                                    getContext().getResources().getColor(R.color.colorVoilation));
                        }


                        break;

                    case Constants.OBD:

                        if(SharedPref.getObdStatus(getContext()) == Constants.WIFI_DISCONNECTED || SharedPref.getObdStatus(getContext()) == Constants.WIFI_CONNECTED){
                            getContext().startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }

                        dismiss();
                        break;

                    case Constants.MISSING_LOCATION:
                    case Constants.UN_CERTIFY_LOG:
                        EldFragment.moveToCertifyPopUpBtn.performClick();
                        dismiss();
                        break;

                    case Constants.VIN:

                        if(Constants.isValidVinFromObd(SharedPref.getIgnitionStatus(getContext()), getContext()) == false){
                            Globally.EldToastWithDuration4Sec(EldFragment.refreshLogBtn, getContext().getResources().getString(R.string.VinMismatchedDesc),
                                    getContext().getResources().getColor(R.color.colorVoilation));
                        }else{
                            String vin = SharedPref.getVehicleVin(getContext());
                            if(vin.length() <= 5){
                                vin = SharedPref.getVINNumber(getContext());

                                int OBD_LAST_STATUS = SharedPref.getObdStatus(getContext());
                                if (OBD_LAST_STATUS == constants.WIRED_CONNECTED || OBD_LAST_STATUS == constants.BLE_CONNECTED ||
                                        OBD_LAST_STATUS == constants.WIFI_CONNECTED){
                                    Globally.EldToastWithDuration4Sec(EldFragment.refreshLogBtn, "Not able to receive VIN information from Truck",
                                            getContext().getResources().getColor(R.color.warning));
                                }else{
                                    Globally.EldToastWithDuration4Sec(EldFragment.refreshLogBtn, "Truck VIN Number is: " + vin,
                                            getContext().getResources().getColor(R.color.colorPrimary));
                                }

                            }else{
                                Globally.EldToastWithDuration4Sec(EldFragment.refreshLogBtn, "Truck VIN Number is: " + vin,
                                        getContext().getResources().getColor(R.color.colorPrimary));

                            }



                        }

                        dismiss();
                        break;

                }


            }
        });

        otherOptionMainLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }


    /*================== Get suggested records edited from web ===================*/
    void GetSuggestedRecords() {

        if (progressDialog.isShowing() == false)
            progressDialog.show();

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId);

        GetEditedRecordRequest.executeRequest(Request.Method.POST, APIs.GET_SUGGESTED_RECORDS, params, 101,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            Logger.LogDebug("response", "edit response: " + response);
            try {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ConstantsKeys.Status);

                if (status.equalsIgnoreCase("true")) {
                    JSONArray editDataArray = new JSONArray(obj.getString(ConstantsKeys.Data));
                    if(editDataArray.length() > 0) {
                        Intent i = new Intent(getContext(), SuggestedFragmentActivity.class);
                        i.putExtra(ConstantsKeys.suggested_data, editDataArray.toString());
                        i.putExtra(ConstantsKeys.Date, "");
                        getContext().startActivity(i);
                    }else{
                        Globally.EldScreenToast(EldFragment.refreshLogBtn, getContext().getResources().getString(R.string.no_suggested_logs),
                                getContext().getResources().getColor(R.color.colorVoilation));
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dismiss();

        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {
        @Override
        public void getError(VolleyError error, int flag) {

            try {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Globally.EldScreenToast(EldFragment.refreshLogBtn, getContext().getResources().getString(R.string.connection_error),
                    getContext().getResources().getColor(R.color.colorVoilation));

            dismiss();
        }
    };

}
