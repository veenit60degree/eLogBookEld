package com.custom.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.RecapViewMethod;
import com.messaging.logistic.SuggestedFragmentActivity;
import com.messaging.logistic.fragment.SuggestedLogFragment;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.fragment.EldFragment;
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
    String DriverId, DeviceId, CurrentCycleId;
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
            JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(DriverId), dbHelper);
            List<RecapSignModel> signList   = constants.GetCertifySignList(recapViewMethod, DriverId, hMethods, dbHelper,
                    Global.GetCurrentDeviceDate(), CurrentCycleId, logPermissionObj, Global);
            boolean isMissingLoc = false;
            for(int i = 0 ; i < signList.size() ; i++){
                if(signList.get(i).isMissingLocation()){
                    isMissingLoc = true;
                    break;
                }
            }
            boolean isUncertifyLog = constants.GetCertifyLogSignStatus(recapViewMethod, DriverId, dbHelper, Global.GetCurrentDeviceDate(), CurrentCycleId, logPermissionObj);

            otherOptionList = constants.getOtherOptionsList(getContext(), isAllowMalfunction, isAllowUnIdentified, isMissingLoc, isUncertifyLog);
            OtherOptionsAdapter adapter = new OtherOptionsAdapter(getContext(), isPendingNotification, pendingNotificationCount, isGps, otherOptionList);
            otherFeatureListView.setAdapter(adapter);
        }catch (Exception e){}
        
        otherFeatureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("itemClick", "itemClick: " + position);

                switch (otherOptionList.get(position).getStatus()) {
                    case 0:
                        TabAct.host.setCurrentTab(3);
                        dismiss();
                        break;

                    case 1:
                        if (isGps) {
                            Globally.EldScreenToast(EldFragment.refreshLogBtn, getContext().getResources().getString(R.string.gps_already_enabled),
                                    getContext().getResources().getColor(R.color.color_eld_theme));
                        } else {
                            getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                        dismiss();

                        break;

                    case 2:
                        TabAct.host.setCurrentTab(12);
                        dismiss();
                        break;

                    case 3:
                        TabAct.host.setCurrentTab(11);
                        dismiss();
                        break;


                    case 4:
                        if (Globally.isConnected(getContext())) {
                            GetSuggestedRecords();
                        } else {
                            Globally.EldScreenToast(EldFragment.refreshLogBtn, Globally.INTERNET_MSG,
                                    getContext().getResources().getColor(R.color.colorVoilation));
                        }


                        break;

                    case 5:

                        if(SharedPref.getObdStatus(getContext()) == Constants.WIFI_DISCONNECTED || SharedPref.getObdStatus(getContext()) == Constants.WIFI_CONNECTED){
                            getContext().startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }

                        dismiss();
                        break;

                    case 6:
                    case 7:
                        EldFragment.moveToCertifyPopUpBtn.performClick();
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

            Log.d("response", "edit response: " + response);
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
