package com.local.db;

import android.database.Cursor;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.als.logistic.Globally;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ShipmentHelperMethod {

    public ShipmentHelperMethod() {
        super();
    }


    /*-------------------- GET SHIPMENT SAVED Array -------------------- */
    public JSONArray getSavedShipmentArray(int ProjectId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getShipmentDetails(ProjectId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.SHIPMENT_LIST));
            try {
                logArray = new JSONArray(logList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!rs.isClosed()) {
            rs.close();
        }

        return logArray;

    }

    /*---------------- Insert/Update Shipment Table ---------------- */
    public void ShipmentHelper( int ProjectId, DBHelper dbHelper, JSONArray shipmentArray){

        Cursor rs = dbHelper.getShipmentDetails(ProjectId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateShipmentDetails(ProjectId, shipmentArray );     // UPDATE SHIPMENT DETAILS
        }else{
            dbHelper.InsertShipmentDetails( ProjectId, shipmentArray  );       // INSERT SHIPMENT DETAILS
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }



    //  Add new log as Json in Array
    public JSONObject AddJobInArray(String DriverId, String CoDriverId,
                                    String DeviceId, String Date,
                                    String BlNoTripNo, String ShipperName,
                                    String FromAddress, String ToAddress,
                                    String savedDate, String commodity,
                                    boolean isPosted, boolean IsShippingCleared, boolean IsEmptyLoad   ){

        JSONObject shipmentJson = new JSONObject();

        try {

            shipmentJson.put(ConstantsKeys.DriverId,                DriverId );
            shipmentJson.put(ConstantsKeys.CoDriverId,              CoDriverId);

            shipmentJson.put(ConstantsKeys.DeviceId,                DeviceId);
            shipmentJson.put(ConstantsKeys.ShippingDocDate,         Date);

            shipmentJson.put(ConstantsKeys.ShippingDocumentNumber,  BlNoTripNo );
            shipmentJson.put(ConstantsKeys.ShipperName,             ShipperName);
            shipmentJson.put(ConstantsKeys.Commodity,               commodity);
            shipmentJson.put(ConstantsKeys.FromAddress,             FromAddress);  //--
            shipmentJson.put(ConstantsKeys.ToAddress,               ToAddress);      // --
            shipmentJson.put(ConstantsKeys.ShippingSavedDate,       savedDate);

            shipmentJson.put(ConstantsKeys.IsPosted,                isPosted);
            shipmentJson.put(ConstantsKeys.IsShippingCleared,       IsShippingCleared);
            shipmentJson.put(ConstantsKeys.IsEmptyLoad,             IsEmptyLoad );
        }catch (Exception e){
            e.printStackTrace();
        }

        return shipmentJson;

    }




    //  Add new log as Json in 18 days shipping Array
    public JSONObject AddJobIn18DaysArray(String DriverId, String CoDriverId,
                                    String DeviceId, String Date,
                                    String BlNoTripNo, String ShipperName,
                                    String FromAddress, String ToAddress,
                                    String savedTime, String Commodity , boolean IsShippingCleared  ){

        JSONObject shipmentJson = new JSONObject();

        try {

            shipmentJson.put(ConstantsKeys.DriverId,                DriverId );
            shipmentJson.put(ConstantsKeys.CoDriverId,              CoDriverId);

            shipmentJson.put(ConstantsKeys.DeviceId,                DeviceId);
            shipmentJson.put(ConstantsKeys.ShippingDocDate,         Date);

            shipmentJson.put(ConstantsKeys.ShippingDocumentNumber,  BlNoTripNo );
            shipmentJson.put(ConstantsKeys.Commodity,               Commodity);
            shipmentJson.put(ConstantsKeys.ShipperName,             ShipperName);
            shipmentJson.put(ConstantsKeys.FromAddress,             FromAddress);
            shipmentJson.put(ConstantsKeys.ToAddress,               ToAddress);

            shipmentJson.put(ConstantsKeys.ShippingSavedDate,       savedTime);
            shipmentJson.put(ConstantsKeys.IsShippingCleared,       IsShippingCleared);


        }catch (Exception e){
            e.printStackTrace();
        }

        return shipmentJson;

    }




    //  create updated shipping info json Object
    public JSONObject createUpdatedInfoObject(String DriverId, String CoDriverId,
                                              String DeviceId, String Date,
                                              String BlNoTripNo, String ShipperName,
                                              String FromAddress, String ToAddress,
                                              String savedTime, String Commodity  ){

        JSONObject shipmentJson = new JSONObject();

        try {

            shipmentJson.put(ConstantsKeys.DriverId,                DriverId );
            shipmentJson.put(ConstantsKeys.CoDriverId,              CoDriverId);

            shipmentJson.put(ConstantsKeys.DeviceId,                DeviceId);
            shipmentJson.put(ConstantsKeys.ShippingDocDate,         Date);

            shipmentJson.put(ConstantsKeys.ShippingDocumentNumber,  BlNoTripNo );
            shipmentJson.put(ConstantsKeys.Commodity,               Commodity);
            shipmentJson.put(ConstantsKeys.ShipperName,             ShipperName);
            shipmentJson.put(ConstantsKeys.FromAddress,             FromAddress);
            shipmentJson.put(ConstantsKeys.ToAddress,               ToAddress);

            shipmentJson.put(ConstantsKeys.ShippingSavedDate,       savedTime);
            shipmentJson.put(ConstantsKeys.IsUpdateRecord,       true);


        }catch (Exception e){
            e.printStackTrace();
        }

        return shipmentJson;

    }



    //  Get Last Json Object
    public JSONObject GetLastJsonObject(JSONArray array, int position   ){

        JSONObject shipmentJson = new JSONObject();
        boolean IsShippingCleared = false;

        try {
            if(array.length() > position) {
                JSONObject obj = (JSONObject) array.get(position);    //array.length()-1

                shipmentJson.put(ConstantsKeys.DriverId, obj.getString(ConstantsKeys.DriverId));

                if (obj.has(ConstantsKeys.CoDriverId)) {
                    shipmentJson.put(ConstantsKeys.CoDriverId, obj.getString(ConstantsKeys.CoDriverId));
                } else {
                    shipmentJson.put(ConstantsKeys.CoDriverId, "");
                }

                if (obj.has(ConstantsKeys.DeviceId)) {
                    shipmentJson.put(ConstantsKeys.DeviceId, obj.getString(ConstantsKeys.DeviceId));
                } else {
                    shipmentJson.put(ConstantsKeys.DeviceId, "");
                }

                shipmentJson.put(ConstantsKeys.ShippingDocDate, obj.getString(ConstantsKeys.ShippingDocDate));

                shipmentJson.put(ConstantsKeys.ShippingDocumentNumber, obj.getString(ConstantsKeys.ShippingDocumentNumber));
                shipmentJson.put(ConstantsKeys.ShipperName, obj.getString(ConstantsKeys.ShipperName));
                shipmentJson.put(ConstantsKeys.FromAddress, obj.getString(ConstantsKeys.FromAddress));    //--
                shipmentJson.put(ConstantsKeys.ToAddress, obj.getString(ConstantsKeys.ToAddress));      // --

                shipmentJson.put(ConstantsKeys.IsPosted, true);


                if (obj.has(ConstantsKeys.Commodity)) {
                    shipmentJson.put(ConstantsKeys.Commodity, obj.getString(ConstantsKeys.Commodity));
                } else {
                    shipmentJson.put(ConstantsKeys.Commodity, "");
                }


                if (obj.has(ConstantsKeys.ShippingSavedDate)) {
                    shipmentJson.put(ConstantsKeys.ShippingSavedDate, obj.getString(ConstantsKeys.ShippingSavedDate));
                } else if (obj.has(ConstantsKeys.shippingdate)) {
                    shipmentJson.put(ConstantsKeys.ShippingSavedDate, obj.getString(ConstantsKeys.shippingdate));
                }

                if (obj.has(ConstantsKeys.IsShippingCleared)) {
                    IsShippingCleared = obj.getBoolean(ConstantsKeys.IsShippingCleared);
                }
                shipmentJson.put(ConstantsKeys.IsShippingCleared, IsShippingCleared);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return shipmentJson;

    }


    public boolean isLoadingRequired(String DriverId, DBHelper dbHelper){
        boolean isRequired = true;
        JSONArray shippingDetailArray = getShipment18DaysArray(Integer.valueOf(DriverId), dbHelper);

        if(shippingDetailArray.length() > 0){
            try {
                JSONObject obj = (JSONObject)shippingDetailArray.get(0);
                String ShippingDocumentNumber = obj.getString(ConstantsKeys.ShippingDocumentNumber);
                String ShipperName = obj.getString(ConstantsKeys.ShipperName);

                if(ShippingDocumentNumber.trim().length() > 0 || ShipperName.trim().length() > 0){
                    isRequired = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return isRequired;
    }

    //  Get Last Json Object
    public boolean IsPosted(JSONArray array   ){

        try {
            JSONObject shipmentJson = (JSONObject)array.get(array.length()-1);
            return  shipmentJson.getBoolean(ConstantsKeys.IsPosted);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }



    public JSONArray updateShipping18DaysArray(String DriverId, String date, JSONObject updatedObj,
                                               JSONArray shipmentArray, DBHelper dbHelper){

        for(int i = 0 ; i<shipmentArray.length() ; i++){
            try {
                JSONObject obj = (JSONObject) shipmentArray.get(i);
                String savedDate = "";
                if(obj.has(ConstantsKeys.ShippingSavedDate)){
                    savedDate = obj.getString(ConstantsKeys.ShippingSavedDate);
                }else{
                    savedDate = obj.getString(ConstantsKeys.shippingdate);
                }

                if(date.equals(savedDate)){
                    shipmentArray.put(i, updatedObj);

                    // save array in table after updation
                    Shipment18DaysHelper(Integer.valueOf(DriverId), dbHelper, shipmentArray);

                    break;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return shipmentArray;
    }

    /*-------------------- GET SHIPMENT 18 Days Array -------------------- */
    public JSONArray getShipment18DaysArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getShipment18DaysDetails(DriverId);
        try {
            if(rs != null && rs.getCount() > 0) {
                rs.moveToFirst();
                String logList = rs.getString(rs.getColumnIndex(DBHelper.SHIPMENT_18DAYS_LIST));
                logArray = new JSONArray(logList);

            }

            if (!rs.isClosed()) {
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return logArray;

    }


    public boolean isShippingCleared(String DRIVER_ID, DBHelper dbHelper){
        boolean isShippingCleared = false;
       JSONArray shippingArray = getShipment18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
        if(shippingArray.length() > 0 ) {
            JSONObject dataObj = GetLastJsonObject(shippingArray, 0);


            if (dataObj != null) {

                try {
                    if ( (dataObj.getString(ConstantsKeys.ShippingDocumentNumber).equals("") ||
                            dataObj.getString(ConstantsKeys.ShippingDocumentNumber).equalsIgnoreCase("Empty") )
                            &&
                            dataObj.getString(ConstantsKeys.ShipperName).equals("") &&
                            dataObj.getString(ConstantsKeys.ToAddress).equals("") ) {

                        isShippingCleared = true;

                    }else if( (!dataObj.getString(ConstantsKeys.ShippingDocumentNumber).equals("") &&
                            !dataObj.getString(ConstantsKeys.ShippingDocumentNumber).equalsIgnoreCase("Empty") ) &&

                            dataObj.getString(ConstantsKeys.FromAddress).equals("") ||
                            dataObj.getString(ConstantsKeys.ToAddress).equals("")    ){

                        isShippingCleared = true;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            isShippingCleared = true;
        }
            return isShippingCleared;
    }


    /*---------------- Insert/Update Shipment 18 Days Table ---------------- */
    public void Shipment18DaysHelper( int DriverId, DBHelper dbHelper, JSONArray shipmentArray){

        Cursor rs = dbHelper.getShipment18DaysDetails(DriverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateShipment18DaysDetails(DriverId, shipmentArray );     // UPDATE SHIPMENT DETAILS
        }else{
            dbHelper.InsertShipment18DaysDetails(DriverId, shipmentArray  );       // INSERT SHIPMENT DETAILS
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }



    public JSONArray ReverseArray(JSONArray array){
        JSONArray reversedArray = new JSONArray();

        for(int i = array.length()-1 ; i >= 0  ; i--){
            try {
                JSONObject obj = (JSONObject)array.get(i);
                reversedArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return reversedArray;
    }


    public JSONObject updateSplitItemInShipmentArray(JSONObject jsonObj, String ShippingDocDate, String ServerDate, String shippingdate){

        JSONObject lastObj = new JSONObject();
            try{
                lastObj.put(ConstantsKeys.ShippingDocNumberId, jsonObj.getString(ConstantsKeys.ShippingDocNumberId));
                lastObj.put(ConstantsKeys.ShippingDocumentNumber, jsonObj.getString(ConstantsKeys.ShippingDocumentNumber));
                lastObj.put(ConstantsKeys.ShipperName, jsonObj.getString(ConstantsKeys.ShipperName));
                lastObj.put(ConstantsKeys.ShipperAddress, jsonObj.getString(ConstantsKeys.ShipperAddress));
                lastObj.put(ConstantsKeys.ShipperCity, jsonObj.getString(ConstantsKeys.ShipperCity));
                lastObj.put(ConstantsKeys.ShipperState, jsonObj.getString(ConstantsKeys.ShipperState));
                lastObj.put(ConstantsKeys.ShipperPostalCode, jsonObj.getString(ConstantsKeys.ShipperPostalCode));
                lastObj.put(ConstantsKeys.DriverId, jsonObj.getString(ConstantsKeys.DriverId));

                lastObj.put(ConstantsKeys.ShippingDocDate, ShippingDocDate);
                lastObj.put(ConstantsKeys.ServerDate, ServerDate);
                lastObj.put(ConstantsKeys.shippingdate, shippingdate );

                if(jsonObj.has(ConstantsKeys.IsUnloading)) {
                    lastObj.put(ConstantsKeys.IsUnloading, jsonObj.getString(ConstantsKeys.IsUnloading));
                }else{
                    lastObj.put(ConstantsKeys.IsShippingCleared, jsonObj.getString(ConstantsKeys.IsShippingCleared));
                }

                lastObj.put(ConstantsKeys.Commodity, jsonObj.getString(ConstantsKeys.Commodity));


            } catch (JSONException e) {
                e.printStackTrace();
            }


        return lastObj;
    }






    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void Update18DaysShippingList(JSONArray shipmentArray, JSONObject objItem, String DriverId, String SelectedDate,
                                 DBHelper dbHelper){

        try {
          //  shipmentArray.remove(shipmentArray.length()-1);
            if(shipmentArray.length() > 0){
                JSONObject item = GetLastJsonObject(shipmentArray, 0);

                String[] dateArray = item.getString(ConstantsKeys.ShippingDocDate).split(" ");
                String date = "";
                if(dateArray.length > 0){
                    date = dateArray[0];
                }

                if(SelectedDate.equals(date) ){
                    shipmentArray.remove(shipmentArray.length()-1);
                    shipmentArray.put(objItem);
                }else{
                    shipmentArray.put(objItem);
                }
            }

            Shipment18DaysHelper(Integer.valueOf(DriverId), dbHelper, shipmentArray);

        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public JSONObject getShipmentRecord(JSONObject dataObj, JSONArray EighteenDaysArray, String DRIVER_ID,
                                        String LogDate, DateTime selectedDateTime, DBHelper dbHelper){
        boolean isDataAvailable = false;
        try {
            EighteenDaysArray = getShipment18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
            for(int i = EighteenDaysArray.length()-1 ; i >= 0  ; i--){
                dataObj = (JSONObject)EighteenDaysArray.get(i);
                if(LogDate.equals(dataObj.getString(ConstantsKeys.ShippingDocDate))){
                    isDataAvailable = true;
                    break;
                }
            }

            if(!isDataAvailable){
                dataObj = null;
                for(int i = 0 ; i < EighteenDaysArray.length()  ; i++){
                    dataObj = (JSONObject)EighteenDaysArray.get(i);

                    DateTime currentDateTime     = Globally.getDateTimeObj(Globally.ConvertDateFormat(dataObj.getString(ConstantsKeys.ShippingDocDate)), false);
                    if(currentDateTime.isBefore(selectedDateTime)){
                        break;
                    }else{
                        dataObj = null;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return dataObj ;
    }


}
