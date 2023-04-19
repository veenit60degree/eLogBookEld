package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.Logger;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.TabAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EldNotificationDialog extends Dialog  {

    private String DriverId, DeviceId, VehicleId, eldNotification, MainDriverId, message = "";
    ViewPager viewpager;
    TextView notificationOkBtn;
    SaveDriverLogPost saveDriverLogPost;
    Constants constants;
    JSONArray notiArray;
    VolleyRequest GetPermissions ;
    Map<String, String> params;
    boolean isNeedPermissionApi;


    public EldNotificationDialog(Context context, String eldNotification, boolean isRefreshPermission) {
        super(context);
        this.eldNotification = eldNotification;
        this.isNeedPermissionApi    = isRefreshPermission;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.popup_notification);
       // setCancelable(false);

        constants = new Constants();
        saveDriverLogPost = new SaveDriverLogPost(getContext(), saveLogRequestResponse);
        GetPermissions  = new VolleyRequest(getContext());

        DriverId = SharedPref.getDriverId(getContext());
        DeviceId = SharedPref.GetSavedSystemToken(getContext());
        VehicleId = SharedPref.getVehicleId(getContext());
        MainDriverId = DriverConst.GetDriverDetails(DriverConst.DriverID, getContext());

        final TextView notificationDescTV = (TextView) findViewById(R.id.notificationDescTV);

        notificationOkBtn       = (TextView)findViewById(R.id.notificationOkBtn);
        viewpager               = (ViewPager)findViewById(R.id.viewpager);

        viewpager.setVisibility(View.GONE);
        notificationDescTV.setVisibility(View.VISIBLE);

        try{
            notiArray = new JSONArray(eldNotification);
            String notiDesc = "";
            for(int i = 0 ; i < notiArray.length() ; i++){
                JSONObject eldObj = (JSONObject)notiArray.get(i);
                String SettingName = eldObj.getString(ConstantsKeys.SettingName);
                String isNotiRead = eldObj.getString(ConstantsKeys.SettingCurrentValue);
                if(SettingName.equals("IsPersonal")){
                    if(isNotiRead.equalsIgnoreCase("true")) {
                        notiDesc = notiDesc + ". Personal Use has been enabled.\n";
                    }else{
                        notiDesc = notiDesc + ". Personal Use has been disabled.\n";
                    }
                }else if(SettingName.equals("IsYardMove")){
                    if(isNotiRead.equalsIgnoreCase("true")) {
                        notiDesc = notiDesc + ". Yard Move has been enabled.\n";
                    }else{
                        notiDesc = notiDesc + ". Yard Move has been disabled.\n";
                    }
                }else if(SettingName.equals("IsExemptDriver")){
                    if(isNotiRead.equalsIgnoreCase("true")) {
                        notiDesc = notiDesc + ". ELD Exemption has been enabled.\n";
                    }else{
                        notiDesc = notiDesc + ". ELD Exemption has been disabled.\n";
                    }
                }

            }

            if(notiDesc.trim().length() > 0) {
                notificationDescTV.setText(notiDesc);
            }else{
                dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        notificationOkBtn.setOnClickListener(new LoadingJobListener());

    }



    private class LoadingJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            JSONArray notificationData = constants.getEldNotificationReadInput(notiArray, DriverId, DeviceId);
            saveDriverLogPost.PostDriverLogData(notificationData, APIs.UPDATE_ELD_SETTING_NOTIFICATIONS, constants.SocketTimeout10Sec, false, false, 1, 101);

        }
    }


     DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
         @RequiresApi(api = Build.VERSION_CODES.KITKAT)
         @Override
         public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, JSONArray inputData) {

             try {
                 JSONObject obj = new JSONObject(response);
                 String status = obj.getString("Status");
                 message = obj.getString("Message");

                 if(status.equalsIgnoreCase("true")){

                     SharedPref.SetELDNotification(false, getContext());
                     SharedPref.SetELDNotificationAlertViewStatus(true, getContext());

                     if(isNeedPermissionApi) {
                         GetDriverStatusPermission(DriverId, DeviceId, VehicleId);
                     }else{
                         Globally.EldScreenToast(TabAct.sliderLay, message, getContext().getResources().getColor(R.color.colorPrimary));
                         dismiss();
                     }

                 }else{
                     Globally.EldScreenToast(notificationOkBtn, message , getContext().getResources().getColor(R.color.red_eld));
                 }

             }catch(Exception e){
                 e.printStackTrace();
             }


         }

         @Override
         public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
             Logger.LogDebug("onDuty error", "onDuty error: " + error.toString());
             Globally.EldScreenToast(notificationOkBtn, error.toString(), getContext().getResources().getColor(R.color.red_eld));
         }
     };


    //*================== Get Driver Status Permissions ===================*//*
    void GetDriverStatusPermission(final String DriverId, final String DeviceId, final String VehicleId ){

        String Country = "";
        //String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getContext());
        String  CurrentCycleId      = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getContext()), getContext());

        if (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
            Country = "CANADA";
        } else {
            Country = "USA";
        }
        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.VehicleId, VehicleId );
        params.put(ConstantsKeys.VIN, SharedPref.getVINNumber(getContext()) );
        params.put(ConstantsKeys.CompanyId, DriverConst.GetDriverDetails(DriverConst.CompanyId, getContext()) );
        params.put(ConstantsKeys.Country, Country );

        GetPermissions.executeRequest(Request.Method.POST, APIs.GET_DRIVER_STATUS_PERMISSION, params, 100,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }



    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null;
            String status = "", Message = "";
            Logger.LogDebug("response", "response: " + response);

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");
                Message = obj.getString("Message");
            } catch (JSONException e) {
            }

            if (status.equalsIgnoreCase("true")) {
                try{
                      if (!obj.isNull("Data")) {
                          JSONObject dataJObject = new JSONObject(obj.getString("Data"));

                          boolean IsCCMTACertified = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsCCMTACertified);
                          SharedPref.SetCCMTACertifiedStatus(IsCCMTACertified, getContext());

                          boolean IsAllowLogReCertification = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsAllowLogReCertification);
                          boolean IsShowUnidentifiedRecords = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsShowUnidentifiedRecords);
                          boolean IsPersonal = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsPersonal);
                          boolean IsYardMove = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsYardMove);

                          boolean IsAllowMalfunction = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsAllowMalfunction);
                          boolean IsAllowDiagnostic = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsAllowDiagnostic);
                          boolean IsClearMalfunction = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsClearMalfunction);
                          boolean IsClearDiagnostic = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsClearDiagnostic);
                          boolean IsNorthCanada = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsNorthCanada);
                          boolean isExemptDriver = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsExemptDriver);
                          boolean IsCycleRequest = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsCycleRequest);
                          boolean IsUnidentified = constants.CheckNullBoolean(dataJObject, ConstantsKeys.IsUnidentified);

                          SharedPref.SetNorthCanadaStatus(IsNorthCanada, getContext());

                          if (DriverId.equals(MainDriverId)) {    // Update permissions for main driver
                              SharedPref.SetCertifcnUnIdenfdSettings(IsAllowLogReCertification, IsShowUnidentifiedRecords, IsPersonal, IsYardMove, getContext());
                              SharedPref.SetDiagnosticAndMalfunctionSettingsMain(IsAllowMalfunction, IsAllowDiagnostic, IsClearMalfunction, IsClearDiagnostic, getContext());
                              SharedPref.SetExemptDriverStatusMain(isExemptDriver, getContext());
                              SharedPref.SetCycleRequestStatusMain(IsCycleRequest, getContext());

                              SharedPref.setEldOccurences(IsUnidentified,
                                      SharedPref.isMalfunctionOccur(getContext()),
                                      SharedPref.isDiagnosticOccur(getContext()),
                                      SharedPref.isSuggestedEditOccur(getContext()), getContext());


                          } else {                                  // Update permissions for Co driver
                              SharedPref.SetCertifcnUnIdenfdSettingsCo(IsAllowLogReCertification, IsShowUnidentifiedRecords, IsPersonal, IsYardMove, getContext());
                              SharedPref.SetDiagnosticAndMalfunctionSettingsCo(IsAllowMalfunction, IsAllowDiagnostic, IsClearMalfunction, IsClearDiagnostic, getContext());
                              SharedPref.SetExemptDriverStatusCo(isExemptDriver, getContext());
                              SharedPref.SetCycleRequestStatusCo(IsCycleRequest, getContext());

                              SharedPref.setEldOccurencesCo(IsUnidentified,
                                      SharedPref.isMalfunctionOccurCo(getContext()),
                                      SharedPref.isDiagnosticOccurCo(getContext()),
                                      SharedPref.isSuggestedEditOccurCo(getContext()), getContext());

                          }
                      }

                    Globally.EldScreenToast(TabAct.sliderLay, message, getContext().getResources().getColor(R.color.colorPrimary));
                    dismiss();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                Globally.EldScreenToast(TabAct.sliderLay, Message, getContext().getResources().getColor(R.color.colorVoilation));
                dismiss();
            }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {
            dismiss();
        }
    };

}
