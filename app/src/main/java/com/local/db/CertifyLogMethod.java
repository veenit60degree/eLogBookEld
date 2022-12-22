package com.local.db;

import android.database.Cursor;

import com.als.logistic.Globally;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CertifyLogMethod {

    /*-------------------- GET CERTIFY LOG Array -------------------- */
    public JSONArray getSavedCertifyLogArray(int DriverId, DBHelper dbHelper) {

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getShippingLog(DriverId);

        try {
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return logArray;

    }

    /* --------------- Insert/ Update Operations --------------- */
    public void CertifyLogHelper(int driverId, DBHelper dbHelper, JSONArray array) {

        Cursor rs = dbHelper.getShippingLog(driverId);
        try {
            if (rs != null & rs.getCount() > 0) {
                rs.moveToFirst();
                dbHelper.UpdateShippingLog(driverId, array);     // UPDATE Shipping Log
            } else {
                dbHelper.InsertShippingLog(driverId, array);       // INSERT Shipping Log
            }
            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //  Add new log as Json in Array
    public JSONObject AddCertifyLogArray(String DriverId, String DeviceId,
                                         String ProjectId, String LogDate,
                                         String SignImage, boolean IsContinueWithSign,
                                         boolean isReCertifyRequired, String CompanyId,
                                         String LocationType, String SignedCopyDate ) {

        JSONObject jsonObj = new JSONObject();

        try {

            jsonObj.put(ConstantsKeys.DriverId,     DriverId);
            jsonObj.put(ConstantsKeys.DeviceId,     DeviceId);
            jsonObj.put(ConstantsKeys.ProjectId,    ProjectId);
            jsonObj.put(ConstantsKeys.LogDate,      LogDate);

            if(!IsContinueWithSign) {
                jsonObj.put(ConstantsKeys.StringImage, SignImage);
            }

            jsonObj.put(ConstantsKeys.IsSignCopy,   IsContinueWithSign);
            jsonObj.put(ConstantsKeys.SignedCopyDate,   SignedCopyDate);

            jsonObj.put(ConstantsKeys.IsRecertifyRequied , isReCertifyRequired);
            jsonObj.put(ConstantsKeys.CompanyId , CompanyId);
            jsonObj.put(ConstantsKeys.LocationType, LocationType);
            jsonObj.put(ConstantsKeys.Latitude , Globally.LATITUDE);
            jsonObj.put(ConstantsKeys.Longitude, Globally.LONGITUDE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;

    }


}
