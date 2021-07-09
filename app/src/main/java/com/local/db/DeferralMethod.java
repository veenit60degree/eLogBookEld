package com.local.db;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeferralMethod {


    public DeferralMethod() {
        super();
    }


    /*-------------------- GET DEFERRAL LOG SAVED Array -------------------- */
    public JSONArray getSavedDeferralArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getDeferralLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.DEFERRAL_RULE_LIST));
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

    /*-------------------- Deferral log event DB Helper -------------------- */
    public void DeferralLogHelper( int driverId, DBHelper dbHelper, JSONArray eventArray){

        Cursor rs = dbHelper.getDeferralLog(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateDeferralLog(driverId, eventArray );        // UPDATE Deferral LOG ARRAY
        }else{
            dbHelper.InsertDeferralLog( driverId, eventArray  );      // INSERT Deferral LOG ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


    public JSONObject GetDeferralJson(String DriverId, String DeviceId, String Truck, String CompanyId,
                                      String Latitude,String Longitude,  String EngineHours, String Odometer,
                                      String DeferralOffTime, String DayCount )  {

        JSONObject malfnDiagnstcObj = new JSONObject();

        try {
            malfnDiagnstcObj.put(ConstantsKeys.DriverId, DriverId);
            malfnDiagnstcObj.put(ConstantsKeys.DeviceId, DeviceId);

            malfnDiagnstcObj.put(ConstantsKeys.Truck, Truck);
            malfnDiagnstcObj.put(ConstantsKeys.CompanyId, CompanyId);

            malfnDiagnstcObj.put(ConstantsKeys.EngineHours, EngineHours);
            malfnDiagnstcObj.put(ConstantsKeys.StartOdometer, Odometer);
            malfnDiagnstcObj.put(ConstantsKeys.Latitude, Latitude );
            malfnDiagnstcObj.put(ConstantsKeys.Longitude, Longitude);

            malfnDiagnstcObj.put(ConstantsKeys.DeferralOffTime, DeferralOffTime );
            malfnDiagnstcObj.put(ConstantsKeys.DayCount, DayCount);


        }catch (Exception e){
            e.printStackTrace();
        }

        return malfnDiagnstcObj;
    }


}
