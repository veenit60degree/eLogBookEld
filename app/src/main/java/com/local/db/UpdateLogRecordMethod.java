package com.local.db;

import android.database.Cursor;

import com.driver.details.EldDriverLogModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateLogRecordMethod {


    public UpdateLogRecordMethod() {
        super();
    }


    /*-------------------- GET UPDATED LOG RECORD Array -------------------- */
    public JSONArray getSavedLogRecordArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getDriverLogRecordDetails(DriverId);

        try {
            if (rs != null && rs.getCount() > 0) {
                rs.moveToFirst();
                String logList = rs.getString(rs.getColumnIndex(DBHelper.RECORD_LOG_LIST));
                try {
                    logArray = new JSONArray(logList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!rs.isClosed()) {
            rs.close();
        }

        return logArray;

    }

    /*-------------------- Recap/ViewLog 18 Days DB Helper -------------------- */
    public void UpdateLogRecordHelper( int DriverId, DBHelper dbHelper, JSONArray array){

        Cursor rs = dbHelper.getDriverLogRecordDetails(DriverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateDriverLogRecordDetails( DriverId, array );        // UPDATE Log Record ARRAY
        }else{
            dbHelper.InsertDriverLogRecordDetails( DriverId, array  );      // INSERT Log Record ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


    public JSONObject GetUpdateLogRecordJson(EldDriverLogModel model, String DriverId, String DeviceId, String RecordType) {


        JSONObject obj = new JSONObject();
        String RecordValue = "";
        if(RecordType.equals("Location")){
            RecordValue = model.getLocation();
        }else if(RecordType.equals("Remarks")){
            RecordValue = model.getRemarks();
        }

        String date = model.getStartDateTime();
        if(date.length() > 16){
            date = date.substring(0, 16);
        }

        try {
            obj.put(ConstantsKeys.DriverId , DriverId );
            obj.put(ConstantsKeys.DeviceId , DeviceId );
            obj.put(ConstantsKeys.DriverStatusId , model.getDriverStatusId());
            obj.put(ConstantsKeys.startDateTime , date);
            obj.put(ConstantsKeys.RecordType , RecordType);
            obj.put(ConstantsKeys.RecordValue , RecordValue);

        }catch (Exception e){
            e.printStackTrace();
        }

        return obj;
    }



}
