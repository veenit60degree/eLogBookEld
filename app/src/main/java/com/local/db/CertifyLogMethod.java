package com.local.db;

import android.database.Cursor;

import com.messaging.logistic.fragment.EldFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CertifyLogMethod {

    /*-------------------- GET CERTIFY LOG Array -------------------- */
    public JSONArray getSavedCertifyLogArray(int DriverId, DBHelper dbHelper) {

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getShippingLog(DriverId);

        if (rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.SHIPPING_LOG_LIST));
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

    /* --------------- Insert/ Update Operations --------------- */
    public void CertifyLogHelper(int driverId, DBHelper dbHelper, JSONArray array) {

        Cursor rs = dbHelper.getShippingLog(driverId);

        if (rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateShippingLog(driverId, array);     // UPDATE Shipping Log
        } else {
            dbHelper.InsertShippingLog(driverId, array);       // INSERT Shipping Log
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }



    //  Add new log as Json in Array
    public JSONObject AddCertifyLogArray(String DriverId, String DeviceId,
                                         String ProjectId, String LogDate,
                                         String SignImage, boolean IsContinueWithSign ) {

        JSONObject jsonObj = new JSONObject();

        try {

            jsonObj.put(ConstantsKeys.DriverId,     DriverId);
            jsonObj.put(ConstantsKeys.DeviceId,     DeviceId);
            jsonObj.put(ConstantsKeys.ProjectId,    ProjectId);
            jsonObj.put(ConstantsKeys.LogDate,      LogDate);
            if(!IsContinueWithSign) {
                jsonObj.put(ConstantsKeys.StringImage, SignImage);
            }/*else{
                jsonObj.put(ConstantsKeys.StringImage, "");
            }*/
            jsonObj.put(ConstantsKeys.IsSignCopy,   IsContinueWithSign);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;

    }


}
