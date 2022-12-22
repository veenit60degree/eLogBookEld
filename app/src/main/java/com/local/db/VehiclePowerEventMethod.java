package com.local.db;

import android.content.Context;
import android.database.Cursor;

import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.als.logistic.Globally;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VehiclePowerEventMethod {

    public static final int PowerEventOff    = 0;
    public static final int PowerEventOn     = 1;

    public VehiclePowerEventMethod() {
        super();
    }


    /*-------------------- GET Vehicle Power Event Array -------------------- */
    public JSONArray getVehPowerEventArray(DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getVehiclePowerEventLog(Integer.valueOf(Globally.PROJECT_ID) );

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.VEH_POWER_EVENT_LOG_LIST));
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

    /*-------------------- Insert/Update Vehicle Power Event in DB Helper -------------------- */
    public void VehPowerEventHelper(DBHelper dbHelper, JSONArray eventArray){

        Cursor rs = dbHelper.getVehiclePowerEventLog(Integer.valueOf(Globally.PROJECT_ID) );

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateVehiclePowerEventLog(Integer.valueOf(Globally.PROJECT_ID) , eventArray );        // UPDATE Vehicle Power Event ARRAY
        }else{
            dbHelper.InsertVehiclePowerEventLog( Integer.valueOf(Globally.PROJECT_ID) , eventArray );      // INSERT Vehicle Power Event ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


    /*-------------------- Create Vehicle Power Event object as JSON -------------------- */
    public void SaveVehPowerEvent(int PowerEvent, int OffsetUTC, DBHelper dbHelper, Context context){

        String DriverId         = SharedPref.getDriverId(context);
        String VehicleId        = SharedPref.getVehicleId(context);
        String VIN              = SharedPref.getVINNumber(context);
        String TruckNumber      = SharedPref.getTruckNumber(context);
        String CompanyId        = DriverConst.GetDriverDetails(DriverConst.CompanyId, context);
        String OdometerInKm     = SharedPref.getObdOdometer(context);
        String HighPrecisionOdometer = SharedPref.getHighPrecisionOdometer(context);
        String OdometerInMiles  = Constants.meterToMilesWith2DecPlaces(HighPrecisionOdometer);
        String EngineHour       = SharedPref.getObdEngineHours(context);
        String TimeStampUTC     = Globally.GetCurrentUTCTime();

        JSONObject pwrEventObj = new JSONObject();

        try {
            pwrEventObj.put(ConstantsKeys.DriverId,          DriverId);
            pwrEventObj.put(ConstantsKeys.TruckNumber,       TruckNumber);
            pwrEventObj.put(ConstantsKeys.VIN,               VIN);
            pwrEventObj.put(ConstantsKeys.VehicleId,         VehicleId);
            pwrEventObj.put(ConstantsKeys.CompanyId,         CompanyId);
            pwrEventObj.put(ConstantsKeys.OdometerInKm,      OdometerInKm);
            pwrEventObj.put(ConstantsKeys.OdometerInMiles,   OdometerInMiles);
            pwrEventObj.put(ConstantsKeys.EngineHours,       EngineHour);
            pwrEventObj.put(ConstantsKeys.PowerEvent,        PowerEvent);
            pwrEventObj.put(ConstantsKeys.TimeStampUTC,      TimeStampUTC);
            pwrEventObj.put(ConstantsKeys.OffsetUTC,         OffsetUTC);


        }catch (Exception e) {
            e.printStackTrace();
        }

            // get last saved events before update table
        JSONArray eventArray = getVehPowerEventArray(dbHelper);
        boolean isSameEvent = isSameEvent(PowerEvent, eventArray);

        // ignore to save same event
        if(!isSameEvent){
            eventArray.put(pwrEventObj);                // add new events in json array
            VehPowerEventHelper(dbHelper, eventArray);  // save updated events array in DB helper
        }

    }


    private boolean isSameEvent(int PowerEvent, JSONArray eventArray){
        boolean isSameEvent = false;
        try {
            if (eventArray.length() > 0) {
                JSONObject obj = (JSONObject) eventArray.get(eventArray.length() - 1);
                int event = obj.getInt(ConstantsKeys.PowerEvent);
                if(event == PowerEvent){
                    isSameEvent = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isSameEvent;
    }


    public void savePowerEventOnChange(String currentIgnitionStatus, int OffsetUTC, DBHelper dbHelper, Context context){
        //SharedPref.GetTruckInfoOnIgnitionChange(Constants.TruckIgnitionStatusMalDia, context);
        String lastIgnitionStatus = SharedPref.GetTruckIgnitionStatusForContinue(Constants.TruckIgnitionStatus, context);
        if(lastIgnitionStatus.equals("ON") && currentIgnitionStatus.equals("OFF")){
            SaveVehPowerEvent(PowerEventOff, OffsetUTC, dbHelper, context);
        }else if(lastIgnitionStatus.equals("OFF") && currentIgnitionStatus.equals("ON")){
            SaveVehPowerEvent(PowerEventOn, OffsetUTC, dbHelper, context);
        }
    }


}
