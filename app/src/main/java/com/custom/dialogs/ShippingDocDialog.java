package com.custom.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;

import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.als.logistic.UILApplication;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.RequestResponse;
import com.constants.SharedPref;
import com.constants.ShippingPost;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.FailedApiTrackMethod;
import com.local.db.ShipmentHelperMethod;
import com.als.logistic.Globally;
import com.als.logistic.LoginActivity;
import com.als.logistic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ShippingDocDialog extends Dialog {


    String ShipperNumber = "", ShipperName = "" ,FromAddress = "", ToAddress = ""; // ShipperAddress;
    private Button shippingDocSaveBtn, shippingDocCancelBtn;
    private EditText shipperNoEditText, shipperNameEditText, shipperAddressEditText, shipperCityEditText, commodityEditText;
    private EditText FromEditText,ToEditText;
    String ToastColor = "#1A3561";
    String DriverId = "", DeviceId = "", SelectedDate = "";
    DBHelper dbHelper;
    ShipmentHelperMethod shipmentHelper;
    JSONArray shipmentJsonArray = new JSONArray();
    JSONArray shipment18DaysJsonArray = new JSONArray();
    JSONArray MainDriver18DaysJsonArray = new JSONArray();
    JSONArray CoDriver18DaysJsonArray = new JSONArray();

    String IsSingleDriver = "", MainDriverId = "", CoDriverId = "", Commodity = "", HomeTerminal = "", OfficeAddress = "";
    String Msg = "Shipping information updated.";
    String clearedShippingInfo = "Shipping information has been cleared";
    String MsgOffline = "Shipping information will be saved automatically with working internet connection";
    ShippingPost postRequest;
    ProgressDialog progressDialog;
    VolleyRequest GetShippingRequest, GetShippingDocNumber;
    FailedApiTrackMethod failedApiTrackMethod;
    Globally global;
    Constants constant;
    Map<String, String> params;
    final int GetShippingNo     = 1;
    final int GetShipping18Days = 2;
    final int Get18DaysListMain = 3;
    final int Get18DaysListCo   = 4;
    final int Save18DaysList    = 5;
    int DriverType;
    private boolean IsShippingCleared;
    JSONObject lastSavedJson;

    public ShippingDocDialog(Context context, String driverId, String deviceId, String selectedDate, DBHelper db_helper,
                             int driverType, boolean isShippingCleare) {
        super(context);
        DriverId            = driverId;
        DeviceId            = deviceId;
        SelectedDate        = selectedDate;
        dbHelper            = db_helper;
        IsShippingCleared   = isShippingCleare;
        DriverType          = driverType;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_shipping_doc);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setCancelable(false);

        global                  = new Globally();
        constant                = new Constants();
        GetShippingRequest      = new VolleyRequest(getContext());
        GetShippingDocNumber    = new VolleyRequest(getContext());
        failedApiTrackMethod    = new FailedApiTrackMethod();
        postRequest             = new ShippingPost(getContext(), requestResponse);
        shipmentHelper          = new ShipmentHelperMethod();
        shipperNoEditText       = (EditText)findViewById(R.id.shipperNoEditText);
        shipperNameEditText     = (EditText)findViewById(R.id.shipperNameEditText);
        shipperAddressEditText  = (EditText)findViewById(R.id.shipperAddressEditText);
        shipperCityEditText     = (EditText)findViewById(R.id.shipperCityEditText);
        commodityEditText       = (EditText)findViewById(R.id.commodityEditText);
        FromEditText            = (EditText)findViewById(R.id.shipperStateEditText);
        ToEditText              = (EditText)findViewById(R.id.shipperPostalEditText);

        shippingDocSaveBtn   = (Button)findViewById(R.id.shippingDocSaveBtn);
        shippingDocCancelBtn = (Button)findViewById(R.id.shippingDocCancelBtn);

        MainDriverId   = DriverConst.GetDriverDetails(DriverConst.DriverID, getContext());
        CoDriverId     = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getContext());

        if(DriverType ==  Constants.MAIN_DRIVER_TYPE) {
            OfficeAddress = DriverConst.GetDriverDetails(DriverConst.CarrierAddress, getContext());
            HomeTerminal = DriverConst.GetDriverDetails(DriverConst.HomeTerminal, getContext());
        }else{
            OfficeAddress = DriverConst.GetCoDriverDetails(DriverConst.CoCarrierAddress, getContext());
            HomeTerminal = DriverConst.GetCoDriverDetails(DriverConst.CoHomeTerminal, getContext());
        }

        IsSingleDriver =  SharedPref.getDriverType(getContext());//.equals(DriverConst.TeamDriver));
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving ...");


        try{
            shipmentJsonArray      = shipmentHelper.getSavedShipmentArray(Integer.valueOf(global.PROJECT_ID), dbHelper);
        }catch (Exception e){
            e.printStackTrace();
            shipmentJsonArray = new JSONArray();
        }


        try {
            if (IsSingleDriver.equals(DriverConst.SingleDriver)) {
                shipment18DaysJsonArray = shipmentHelper.getShipment18DaysArray(Integer.valueOf(DriverId), dbHelper);
                lastSavedJson = shipmentHelper.GetLastJsonObject(shipment18DaysJsonArray, 0);
            }else {
                MainDriver18DaysJsonArray = shipmentHelper.getShipment18DaysArray(Integer.valueOf(MainDriverId), dbHelper);
                CoDriver18DaysJsonArray = shipmentHelper.getShipment18DaysArray(Integer.valueOf(CoDriverId), dbHelper);

                if(DriverType == Constants.MAIN_DRIVER_TYPE){
                    shipment18DaysJsonArray = MainDriver18DaysJsonArray;
                }else{
                    shipment18DaysJsonArray = CoDriver18DaysJsonArray;
                }
            }
        }catch (Exception e){
            shipment18DaysJsonArray = new JSONArray();
            e.printStackTrace();
        }


        // If Driver save On duty job with Unloading then save data for unloading shipment....
        if(IsShippingCleared) {
            CallApiToSaveShipping(IsShippingCleared);
        }

        if(global.isConnected(getContext())) {
            GetShippingInfoWithDate(DriverId, DeviceId, SelectedDate);
        }

        // Check is array is empty then call API to get data from server
        if (IsSingleDriver.equals(DriverConst.SingleDriver)) {
            if (shipment18DaysJsonArray.length() == 0) {
                if(global.isConnected(getContext())) {
                    GetShippingInfoWithDate(DriverId, DeviceId, SelectedDate);
                    GetShipment18Days(DriverId, DeviceId, SelectedDate, GetShipping18Days);
                }
            }else {
                if(!IsShippingCleared) {
                    ParseShippingData(lastSavedJson);
                }
            }
        }else{
            if (MainDriver18DaysJsonArray.length() == 0) {
                if(global.isConnected(getContext())) {
                    GetShippingInfoWithDate(MainDriverId, DeviceId, SelectedDate);
                    GetShipment18Days(MainDriverId, DeviceId, SelectedDate, Get18DaysListMain);
                }
            }else{
                if(!IsShippingCleared && DriverType == Constants.MAIN_DRIVER_TYPE) {   // DriverType = 0 means Main driver
                    lastSavedJson = shipmentHelper.GetLastJsonObject(MainDriver18DaysJsonArray, 0);

                    ParseShippingData(lastSavedJson);
                }
            }

            if (CoDriver18DaysJsonArray.length() == 0) {
                if(global.isConnected(getContext())) {
                    GetShippingInfoWithDate(CoDriverId, DeviceId, SelectedDate);
                    GetShipment18Days(CoDriverId, DeviceId, SelectedDate, Get18DaysListCo);
                }
            }else{
                if(!IsShippingCleared && DriverType == Constants.CO_DRIVER_TYPE) {   // DriverType = 1 means Co driver
                    lastSavedJson = shipmentHelper.GetLastJsonObject(CoDriver18DaysJsonArray, 0);
                    ParseShippingData(lastSavedJson);
                }
            }

        }


        shippingDocSaveBtn.setOnClickListener(new ShipperFieldListener());

        shippingDocCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


    @Override
    protected void onStop() {
        HideKeyboard();
        super.onStop();
    }

    private class ShipperFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HideKeyboard();

            if(constant.isActionAllowed(getContext())) {

                String tempShipperNumber       = shipperNoEditText.getText().toString().trim();
                String tempShipperName         = shipperNameEditText.getText().toString().trim();
                String tempFromAddress         = FromEditText.getText().toString().trim();
                String tempToAddress           = ToEditText.getText().toString().trim();
                String tempCommodity           = commodityEditText.getText().toString().trim();

                if(IsShippingCleared) {
                    if (tempShipperNumber.equals(getContext().getResources().getString(R.string.Empty))) {
                        if (tempToAddress.length() > 0)
                            tempFromAddress = tempToAddress;
                    }
                }

                if ( tempShipperNumber.equals("") || tempFromAddress.equals("") ) {

                    if(tempShipperNumber.equals("")){
                        shipperNoEditText.requestFocus();
                        global.EldScreenToast(shippingDocSaveBtn, "Enter B/L or Trip number.", UILApplication.getInstance().getThemeColor());
                    }else if(tempFromAddress.equals("")){
                        FromEditText.requestFocus();
                        global.EldScreenToast(shippingDocSaveBtn, "Enter (From) address", UILApplication.getInstance().getThemeColor());
                    }

                }else{

                    if (tempShipperNumber.length() > 0 ) {      //|| (tempShipperName.length() > 0 && tempCommodity.length() > 0)
                        if (tempShipperNumber.equals(ShipperNumber) &&
                                tempCommodity.equals(Commodity) &&
                                tempShipperName.equals(ShipperName) &&
                                tempFromAddress.equals(FromAddress) &&
                                tempToAddress.equals(ToAddress)) {

                            dismiss();

                        } else {



                            ShipperNumber = shipperNoEditText.getText().toString().trim();
                            ShipperName = shipperNameEditText.getText().toString().trim();
                            FromAddress = FromEditText.getText().toString().trim();
                            ToAddress = ToEditText.getText().toString().trim();
                            Commodity = commodityEditText.getText().toString().trim();


                            boolean isContainSpclChar = false;
                            String spclCharAlert = "";
                            String alphabetNumAlertMsg = " Only accept alphabet and numeric char";

                            if(isContainSpecialChar(ShipperNumber)) {
                                isContainSpclChar = true;
                                spclCharAlert = "Shipper number field contain special char." + alphabetNumAlertMsg;
                            }else if(isContainSpecialChar(ShipperName)){
                                isContainSpclChar = true;
                                spclCharAlert = "Shipper name field contain special char." + alphabetNumAlertMsg;
                            }else if(isContainSpecialChar(FromAddress)){
                                isContainSpclChar = true;
                                spclCharAlert = "From address field contain special char." + alphabetNumAlertMsg;
                            }else if(isContainSpecialChar(ToAddress)){
                                isContainSpclChar = true;
                                spclCharAlert = "To address field contain special char." + alphabetNumAlertMsg;
                            }else if(isContainSpecialChar(Commodity)){
                                isContainSpclChar = true;
                                spclCharAlert = "Commodity field contain special char." + alphabetNumAlertMsg;
                            }


                            if(isContainSpclChar){
                                global.EldScreenToast(shippingDocSaveBtn, spclCharAlert, UILApplication.getInstance().getThemeColor());
                            }else {
                                if (SharedPref.IsDrivingShippingAllowed(getContext())) {
                                    if (ToAddress.length() > 0) {
                                        CallApiToSaveShipping(false);
                                    } else {
                                        ToEditText.requestFocus();
                                        global.EldScreenToast(shippingDocSaveBtn, "Enter (To) address", UILApplication.getInstance().getThemeColor());
                                    }
                                } else {
                                    CallApiToSaveShipping(false);
                                }
                            }


                        }
                    } else {

                       // if (tempShipperNumber.length() == 0 && tempShipperName.length() == 0 && tempCommodity.length() == 0) {
                            global.EldScreenToast(shippingDocSaveBtn, "Enter BL/Trip Number to save shipping information", UILApplication.getInstance().getThemeColor());   //or Shipper Name and Commodity
                       /* } else if (tempShipperName.length() == 0 || tempCommodity.length() == 0) {
                            global.EldScreenToast(shippingDocSaveBtn, "Enter Shipper Name and Commodity to save shipping information", UILApplication.getInstance().getThemeColor());
                        }*/

                    }
                }

            }else{
                global.EldScreenToast(shippingDocSaveBtn,  "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                getContext().getResources().getString(R.string.stop_vehicle_alert),
                        UILApplication.getInstance().getThemeColor());
            }
        }
    }


    private boolean isContainSpecialChar(String str){
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();

    }
    private void CallApiToSaveShipping(boolean IsUnloadingSaved) {

            if (IsSingleDriver.equals(DriverConst.TeamDriver)) {

                AddDataInDB(MainDriverId, CoDriverId, IsUnloadingSaved);

                if (global.isConnected(getContext())) {
                    // reset api call count
                    failedApiTrackMethod.isAllowToCallOrReset(dbHelper, APIs.SAVE_SHIPPING_DOC_NUMBER,
                            true, global, getContext());

                    progressDialog.show();
                    //POST data to server
                    postRequest.PostListingData(shipmentJsonArray, APIs.SAVE_SHIPPING_DOC_NUMBER, Save18DaysList);
                } else {
                    DismissDialog(Msg);
                }

            } else {
                AddDataInDB(DriverId, "", IsUnloadingSaved);
                if (global.isConnected(getContext())) {

                    // reset api call count
                    failedApiTrackMethod.isAllowToCallOrReset(dbHelper, APIs.SAVE_SHIPPING_DOC_NUMBER,
                            true, global, getContext());


                    progressDialog.show();
                    //POST data to server
                    postRequest.PostListingData(shipmentJsonArray, APIs.SAVE_SHIPPING_DOC_NUMBER, Save18DaysList);
                } else {
                    DismissDialog(Msg);
                }
            }

    }


    void DismissDialog(String msgg){
        try {
            if(getContext() != null ) {
                Toast.makeText(getContext(), msgg, Toast.LENGTH_LONG).show();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 500);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void LogoutUser(){
        if( constant.GetDriverSavedArray(getContext()).length() == 0) {
            try {
                global.ClearAllFields(getContext());
                global.StopService(getContext());
                Intent i = new Intent(getContext(), LoginActivity.class);
                getContext().startActivity(i);
                getOwnerActivity().finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void ParseShippingData(JSONObject dataObj){
        try {
            if(!dataObj.isNull(ConstantsKeys.ShippingDocumentNumber))
                ShipperNumber    = dataObj.getString(ConstantsKeys.ShippingDocumentNumber);
            if(!dataObj.isNull(ConstantsKeys.Commodity))
                Commodity       = dataObj.getString(ConstantsKeys.Commodity);
            if(!dataObj.isNull(ConstantsKeys.ShipperName))
                ShipperName     = dataObj.getString(ConstantsKeys.ShipperName);
            if(!dataObj.isNull(ConstantsKeys.FromAddress))         // ShipperState is used as From address
                FromAddress     = dataObj.getString(ConstantsKeys.FromAddress);
            if(!dataObj.isNull(ConstantsKeys.ToAddress))    // ShipperState is used as To address
                ToAddress       = dataObj.getString(ConstantsKeys.ToAddress);
         //   if(dataObj.has(ConstantsKeys.IsShippingCleared) && !dataObj.isNull(ConstantsKeys.IsShippingCleared))    // ShipperState is used as To address
              //   IsShippingCleared     = dataObj.getBoolean(ConstantsKeys.IsShippingCleared);

            if(ShipperNumber.equals(getContext().getResources().getString(R.string.Empty))){
                ShipperNumber = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            if(!IsShippingCleared) {
                SetTextOnView();
                if (ShipperNumber.length() > 0) {
                   // shippingDocSaveBtn.setText("Update");
                    shipperNoEditText.setSelection(ShipperNumber.length());
                } else {
                   // shippingDocSaveBtn.setText("Save");
                }
            }else{


                shipperNoEditText.setText(getContext().getResources().getString(R.string.Empty));
                shipperNoEditText.setSelection(getContext().getResources().getString(R.string.Empty).length());
                FromEditText.setText("");

                if(FromAddress.trim().length() > 0 && ToAddress.trim().length() > 0) {
                    FromEditText.setText(ToAddress);
                }else{
                    FromEditText.setText(FromAddress);
                    ToEditText.setText(ToAddress);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HideKeyboard();
    }


    void AddDataInDB(String MainDriverId, String CoDriverId, boolean IsShippingCleared){

        boolean isPostedData = false;   // this parameter was used earlier to check data is posted on not. if posted then it will be saved as true. but now some few changes in functionality its value is false statically..
        String currentDate      = global.GetDriverCurrentDateTime(global, getContext());
        String CurrentDateTime  = global.ConvertDateFormatMMddyyyyHHmm(currentDate);

        try {
            if(shipment18DaysJsonArray.length() > 0 && IsShippingCleared){
                JSONObject obj = shipmentHelper.GetLastJsonObject(shipment18DaysJsonArray, 0);
                ShipperNumber = getContext().getResources().getString(R.string.Empty);
                FromAddress = obj.getString(ConstantsKeys.ToAddress);
            }
           // FromAddress = lastSavedJson.getString(ConstantsKeys.FromAddress);
        }catch (Exception e){
            e.printStackTrace();
        }
        boolean IsEmptyLoad = false;

        if(shipperNoEditText.getText().toString().equals(getContext().getResources().getString(R.string.Empty))){
            if(FromAddress.length() > 0 && ToAddress.length() > 0){
                IsEmptyLoad = true;
            }
        }

        JSONObject shipmentObj = shipmentHelper.AddJobInArray(MainDriverId, CoDriverId,  DeviceId, CurrentDateTime,
                ShipperNumber, ShipperName, FromAddress, ToAddress, currentDate, Commodity, isPostedData, IsShippingCleared, IsEmptyLoad );

        shipmentJsonArray.put(shipmentObj);
        shipmentHelper.ShipmentHelper(Integer.valueOf(global.PROJECT_ID), dbHelper, shipmentJsonArray);


        // Save data in 18 days Shipping array list
            JSONObject shipment18DaysObj = shipmentHelper.AddJobIn18DaysArray(MainDriverId, CoDriverId,  DeviceId, CurrentDateTime,
                    ShipperNumber, ShipperName, FromAddress, ToAddress, currentDate, Commodity, IsShippingCleared);

            try {
                if (IsSingleDriver.equals(DriverConst.SingleDriver)) {
                    // reverse Array to add item at the end
                    JSONArray reverseArray = shipmentHelper.ReverseArray(shipment18DaysJsonArray);
                    reverseArray.put(shipment18DaysObj);

                    // again reverse Array to show last item at top
                    shipment18DaysJsonArray = new JSONArray();
                    shipment18DaysJsonArray = shipmentHelper.ReverseArray(reverseArray);

                    // Save Array in DB
                    shipmentHelper.Shipment18DaysHelper(Integer.valueOf(DriverId), dbHelper, shipment18DaysJsonArray);

                } else {

                        // reverse Array to add item at the end
                        JSONArray reverseMainArray = shipmentHelper.ReverseArray(MainDriver18DaysJsonArray);
                        JSONArray reverseCoArray = shipmentHelper.ReverseArray(CoDriver18DaysJsonArray);

                        reverseMainArray.put(shipment18DaysObj);
                        reverseCoArray.put(shipment18DaysObj);

                        // again reverse Array to show last item at top
                        MainDriver18DaysJsonArray = new JSONArray();
                        MainDriver18DaysJsonArray = shipmentHelper.ReverseArray(reverseMainArray);
                        CoDriver18DaysJsonArray = new JSONArray();
                        CoDriver18DaysJsonArray = shipmentHelper.ReverseArray(reverseCoArray);

                        // Save Array in DB for both driver
                        shipmentHelper.Shipment18DaysHelper(Integer.valueOf(MainDriverId), dbHelper, MainDriver18DaysJsonArray);
                        shipmentHelper.Shipment18DaysHelper(Integer.valueOf(CoDriverId), dbHelper, CoDriver18DaysJsonArray);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    void SetTextOnView(){
        shipperNoEditText.setText(ShipperNumber);
        shipperNameEditText.setText(ShipperName);
        shipperAddressEditText.setText(HomeTerminal);
        commodityEditText.setText(Commodity);
        shipperCityEditText.setText(OfficeAddress);
        FromEditText.setText(FromAddress);
        ToEditText.setText(ToAddress);


    }


    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    RequestResponse requestResponse = new RequestResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, int flag) {

            if(progressDialog != null){
                progressDialog.dismiss();
            }
            shippingDocSaveBtn.setEnabled(true);

            JSONObject obj = null;
            String status = "";

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");
                if(status.equalsIgnoreCase("true")) {
                   // JSONObject objItem = shipmentHelper.GetLastJsonObject(shipmentJsonArray, shipmentJsonArray.length()-1);
                 //   shipmentJsonArray = new JSONArray();
                 //   shipmentJsonArray.put(objItem);

                    // Save shipping inputs for offline
                  //  shipmentHelper.ShipmentHelper(Integer.valueOf(global.PROJECT_ID), dbHelper, shipmentJsonArray);

                    // Save data in 18 Days shipping list
                  //  Update18DaysDriverInfo(objItem);

                    // Clear shipping inputs after ssave
                      shipmentHelper.ShipmentHelper(Integer.valueOf(global.PROJECT_ID), dbHelper, new JSONArray());

                      if(IsShippingCleared){
                          DismissDialog(clearedShippingInfo);
                      }else{
                          DismissDialog(Msg);
                      }

                }else{
                    String Message = obj.getString("Message");
                    if(Message.equals("Device Logout")) {
                        LogoutUser();
                    }else {
                        // Save data in 18 Days shipping list
                        //  JSONObject objItem = shipmentHelper.GetLastJsonObject(shipmentJsonArray, shipmentJsonArray.length()-1);
                        //    Update18DaysDriverInfo(objItem);
                        if(getContext() != null  ) {
                            Toast.makeText(getContext(), MsgOffline, Toast.LENGTH_LONG).show();
                        }
                    }

                    dismiss();

                }
            } catch (Exception e) {  }

        }

        @Override
        public void onResponseError(String error, int flag) {
            Logger.LogDebug("errorrr ", ">>>error dialog: " );
            if(progressDialog != null){
                progressDialog.dismiss();
            }

            // Save data in 18 Days shipping list
          //  JSONObject objItem = shipmentHelper.GetLastJsonObject(shipmentJsonArray, shipmentJsonArray.length()-1);
         //   Update18DaysDriverInfo(objItem);

            shippingDocSaveBtn.setEnabled(true);
            DismissDialog(Msg);
        }
    };


    void Update18DaysDriverInfo(JSONObject objItem){
        try {

            if (IsSingleDriver.equals(DriverConst.SingleDriver)) {
                shipmentHelper.Update18DaysShippingList(shipment18DaysJsonArray, objItem, DriverId, SelectedDate, dbHelper);
            } else {
                JSONObject MainDriverJson = objItem;
                JSONObject CoDriverJson = objItem;

                MainDriverJson.put(ConstantsKeys.DriverId, objItem.getString(ConstantsKeys.DriverId));
                MainDriverJson.put(ConstantsKeys.CoDriverId, "");

                CoDriverJson.put(ConstantsKeys.DriverId, objItem.getString(ConstantsKeys.CoDriverId));
                CoDriverJson.put(ConstantsKeys.CoDriverId, "");

                shipmentHelper.Update18DaysShippingList(MainDriver18DaysJsonArray, MainDriverJson, MainDriverId, SelectedDate, dbHelper);
                shipmentHelper.Update18DaysShippingList(CoDriver18DaysJsonArray, CoDriverJson, CoDriverId, SelectedDate, dbHelper);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //*================== Get Driver Lad last 18 days ===================*//*
    void GetShippingInfoWithDate(final String DriverId, final String DeviceId, final String ShippingDocDate){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.ShippingDocDate, ShippingDocDate);

        GetShippingRequest.executeRequest(Request.Method.POST, APIs.GET_SHIPPING_DOC_NUMBER , params, GetShippingNo,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);
    }

    /*================== Get Shipping Document Number ===================*/
    void GetShipment18Days(final String DriverId, final String DeviceId, final String ShippingDocDate, int flag){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.ShippingDocDate, ShippingDocDate);

        GetShippingRequest.executeRequest(Request.Method.POST, APIs.GET_SHIPPING_INFO_OFFLINE , params, flag,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {
        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null, dataObj = null;
            String status = "";

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");
                if(status.equalsIgnoreCase("true")) {

                    switch (flag) {
                        case GetShippingNo:
                        if (!obj.isNull("Data")) {
                            dataObj = new JSONObject(obj.getString("Data"));
                            if (dataObj != null) { // && shipmentJsonArray.length() == 0
                                ParseShippingData(dataObj);
                            }
                        }
                        break;

                        case GetShipping18Days:
                            try {
                                JSONArray resultArray = new JSONArray(obj.getString("Data"));
                                shipmentHelper.Shipment18DaysHelper(Integer.valueOf(DriverId), dbHelper, resultArray);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        case Get18DaysListMain:
                            try {
                                JSONArray resultArray = new JSONArray(obj.getString("Data"));
                                shipmentHelper.Shipment18DaysHelper(Integer.valueOf(MainDriverId), dbHelper, resultArray);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;


                        case Get18DaysListCo:
                            try {
                                JSONArray resultArray = new JSONArray(obj.getString("Data"));
                                shipmentHelper.Shipment18DaysHelper(Integer.valueOf(CoDriverId), dbHelper, resultArray);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;


                    }
                }else{
                    if(!obj.isNull("Message")){
                        try {
                            dismiss();
                            String Message = obj.getString("Message");
                            if(Message.equals("Device Logout")) {
                                LogoutUser();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {  }

        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {

            Logger.LogDebug("error", ">>error: " + error);

            switch (flag) {
                case GetShippingNo:
                    if( error.toString().contains("NoConnection")){
                        global.EldScreenToast(shippingDocSaveBtn, getContext().getResources().getString(R.string.connection_error), UILApplication.getInstance().getThemeColor());
                    }else {
                        global.EldScreenToast(shippingDocSaveBtn, Globally.DisplayErrorMessage(error.toString()), UILApplication.getInstance().getThemeColor());
                    }
                    break;

            }


        }
    };


}
