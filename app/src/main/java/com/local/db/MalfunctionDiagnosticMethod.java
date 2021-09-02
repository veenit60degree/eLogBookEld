package com.local.db;

import android.content.Context;
import android.database.Cursor;

import com.constants.Constants;
import com.constants.SharedPref;
import com.messaging.logistic.Globally;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MalfunctionDiagnosticMethod {


    public MalfunctionDiagnosticMethod() {
        super();
    }


    /*-------------------- GET MALFUNCTION & DIAGNOSTIC LOG SAVED Array -------------------- */
    public JSONArray getSavedMalDiagstcArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getMalfunctionDiagnosticLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.MALFUNCTION_DIANOSTIC_LIST));
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

    /*-------------------- MALFUNCTION & DIAGNOSTIC DB Helper -------------------- */
    public void MalfnDiagnstcLogHelper( int driverId, DBHelper dbHelper, JSONArray eventArray){

        Cursor rs = dbHelper.getMalfunctionDiagnosticLog(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateMalfunctionDiagnosticLog(driverId, eventArray );        // UPDATE MALFUNCTION & DIAGNOSTIC LOG ARRAY
        }else{
            dbHelper.InsertMalfncnDiagnosticLog( driverId, eventArray  );      // INSERT MALFUNCTION & DIAGNOSTIC LOG ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }



    // Same data in bottom methods. but difference is we are not clearing records in this table after posted to server.
    /*-------------------- GET MALFUNCTION & DIAGNOSTIC LOG SAVED Array -------------------- */
    public JSONArray getSavedMalDiagstcArrayEvents(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getMalfunctionDiagnosticLog1(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.MALFUNCTION_DIANOSTIC_LIST1));
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


    /*-------------------- MALFUNCTION & DIAGNOSTIC DB Helper -------------------- */
    public void MalfnDiagnstcLogHelperEvents( int driverId, DBHelper dbHelper, JSONArray eventArray){

        Cursor rs = dbHelper.getMalfunctionDiagnosticLog1(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateMalfunctionDiagnosticLog1(driverId, eventArray );        // UPDATE MALFUNCTION & DIAGNOSTIC LOG ARRAY
        }else{
            dbHelper.InsertMalfncnDiagnosticLog1( driverId, eventArray  );      // INSERT MALFUNCTION & DIAGNOSTIC LOG ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }





    public JSONObject GetMalDiaEventJson(String DriverId, String DeviceId, String VIN, String UnitNo, String CompanyId,
                                      String EngineHours, String StartOdometer, String EndOdometer, String EventDateTime,
                                      String DiagnosticType, String MalfunctionDefinition, boolean IsCleared, String ClearedTime,
                                         String ClearedTimeOdometer, String ClearedTimeEngineHours )  {

        JSONObject malfnDiagnstcObj = new JSONObject();

        try {
            malfnDiagnstcObj.put(ConstantsKeys.DriverId, DriverId);
            malfnDiagnstcObj.put(ConstantsKeys.DeviceNumber, DeviceId);

            malfnDiagnstcObj.put(ConstantsKeys.VIN, VIN);
            malfnDiagnstcObj.put(ConstantsKeys.UnitNo, UnitNo);
            malfnDiagnstcObj.put(ConstantsKeys.CompanyId, CompanyId);

            malfnDiagnstcObj.put(ConstantsKeys.EngineHours, EngineHours);
            malfnDiagnstcObj.put(ConstantsKeys.StartOdometer, StartOdometer);
            malfnDiagnstcObj.put(ConstantsKeys.EndOdometer, EndOdometer );
            malfnDiagnstcObj.put(ConstantsKeys.EventDateTime, EventDateTime);
            malfnDiagnstcObj.put(ConstantsKeys.DiagnosticType, DiagnosticType );
            malfnDiagnstcObj.put(ConstantsKeys.MalfunctionDefinition, MalfunctionDefinition);

            malfnDiagnstcObj.put(ConstantsKeys.IsCleared, IsCleared);
            malfnDiagnstcObj.put(ConstantsKeys.ClearedTime, ClearedTime);
            malfnDiagnstcObj.put(ConstantsKeys.ClearedTimeOdometer, ClearedTimeOdometer );
            malfnDiagnstcObj.put(ConstantsKeys.ClearedTimeEngineHours, ClearedTimeEngineHours);

        }catch (Exception e){
            e.printStackTrace();
        }

        return malfnDiagnstcObj;
    }



    public JSONObject GetJsonForClearDiagnostic(String DriverId, String DeviceId, String eventsList, String Remarks)  {

        JSONObject malfnDiagnstcObj = new JSONObject();

        try {
            malfnDiagnstcObj.put(ConstantsKeys.DriverId, DriverId);
            malfnDiagnstcObj.put(ConstantsKeys.DeviceNumber, DeviceId);
            malfnDiagnstcObj.put(ConstantsKeys.EventList, eventsList);
            malfnDiagnstcObj.put(ConstantsKeys.Remarks, Remarks);


        }catch (Exception e){
            e.printStackTrace();
        }

        return malfnDiagnstcObj;
    }





    /*-------------------- MALFUNCTION & DIAGNOSTIC OCCURED TIME LOG DB Helper -------------------- */
    public void MalDiaOccTimeLogHelper( int driverId, DBHelper dbHelper, JSONArray eventArray){

        Cursor rs = dbHelper.getMalDiaTimeLog(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateMalDiaOccTimeLog(driverId, eventArray );        // UPDATE MALFUNCTION & DIAGNOSTIC OCC TIME LOG ARRAY
        }else{
            dbHelper.InsertMalDiaOccTimeLog( driverId, eventArray  );      // INSERT MALFUNCTION & DIAGNOSTIC OCC TIME LOG ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


    /*-------------------- GET MALFUNCTION & DIAGNOSTIC OCCURED TIME LOG -------------------- */
    public JSONArray getSavedMalDiaTimeLog(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getMalDiaTimeLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.MAL_DIA_OCCURRED_TIME_LIST));
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


    public JSONObject GetJsonForMalDiaOccTime(String DriverId, String VIN, String DisConnectStartTime, String DisConnectEndTime,
                                              int TotalMin, String status, String EventType)  {

        JSONObject malfnDiagnstcObj = new JSONObject();

        try {
            malfnDiagnstcObj.put(ConstantsKeys.DriverId, DriverId);
            malfnDiagnstcObj.put(ConstantsKeys.VIN, VIN);
            malfnDiagnstcObj.put(ConstantsKeys.DisConnectStartTime, DisConnectStartTime);
            malfnDiagnstcObj.put(ConstantsKeys.DisConnectEndTime, DisConnectEndTime);
            malfnDiagnstcObj.put(ConstantsKeys.TotalMin, TotalMin);
            malfnDiagnstcObj.put(ConstantsKeys.Status, status);
            malfnDiagnstcObj.put(ConstantsKeys.EventType, EventType);


        }catch (Exception e){
            e.printStackTrace();
        }

        return malfnDiagnstcObj;
    }



    // update occurred event time log array for 1 day only
    public void updateOccEventTimeLog(DateTime currentTime, String DriverId, String VIN,
                                      DateTime disConnectStartTime, DateTime disConnectEndTime, String status,
                                      String EventType, DBHelper dbHelper,  Context context){
        try {

            boolean isMalEvent = false;
            DateTime oneDayDiffDate = currentTime.minusDays(1);
            JSONArray updatedTimeArray = new JSONArray();
            JSONArray lastOccEventTimeArray = getSavedMalDiaTimeLog(Integer.valueOf(DriverId), dbHelper);

            // add only last 24 hour events in array
            for (int i = 0; i < lastOccEventTimeArray.length() ; i++) {
                JSONObject eventObj = (JSONObject) lastOccEventTimeArray.get(i);
                DateTime selectedDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.DisConnectStartTime), false);
                if(selectedDateTime.isAfter(oneDayDiffDate)){
                    updatedTimeArray.put(eventObj);
                    if(eventObj.getString(ConstantsKeys.EventType).equals(ConstantsKeys.MalfunctionEngSync)){
                        isMalEvent = true;
                    }
                }
            }

            // clear engine sync malfunction event if not occured in list
            if(isMalEvent == false && !EventType.equals(ConstantsKeys.MalfunctionEngSync)){
                SharedPref.saveEngSyncMalfunctionStatus(false, context);
               // constants.saveMalfncnStatus(context, false);
            }

            JSONObject obj;
            if(status.equals("DisConnected")){

                int timeInMin = Minutes.minutesBetween(disConnectStartTime, currentTime).getMinutes();
                obj = GetJsonForMalDiaOccTime(DriverId, VIN, disConnectStartTime.toString(), disConnectEndTime.toString(), timeInMin, status, EventType);

                updatedTimeArray.put(obj);

            }else{
                if(updatedTimeArray.length() > 0){
                    JSONObject lastObj = (JSONObject)updatedTimeArray.get(updatedTimeArray.length()-1);
                    DateTime disconStartTime = Globally.getDateTimeObj(lastObj.getString(ConstantsKeys.DisConnectStartTime), false);
                    int timeInMin = Minutes.minutesBetween(disconStartTime, currentTime).getMinutes();
                    lastObj.put(ConstantsKeys.DisConnectEndTime, currentTime);
                    lastObj.put(ConstantsKeys.TotalMin, timeInMin);
                    lastObj.put(ConstantsKeys.Status, "Connected");

                    // update time in last item
                    updatedTimeArray.put(updatedTimeArray.length()-1, lastObj);

                }
            }

            MalDiaOccTimeLogHelper(Integer.valueOf(DriverId), dbHelper, updatedTimeArray);


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public int getTotalEngSyncMissingMin(String DriverId, DBHelper dbHelper){
        int totalMin = 0;

        try{
            JSONArray eventArray = getSavedMalDiaTimeLog(Integer.valueOf(DriverId), dbHelper);

            for(int i = 0 ; i < eventArray.length() ; i++){
                JSONObject eventObj = (JSONObject) eventArray.get(i);
                if(i == eventArray.length()-1){
                    DateTime disconStartTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.DisConnectStartTime), false);
                    DateTime disconEndTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
                    int timeInMin = Minutes.minutesBetween(disconStartTime, disconEndTime).getMinutes();
                    totalMin = totalMin + timeInMin;
                }else{
                    totalMin = totalMin + eventObj.getInt(ConstantsKeys.TotalMin);


                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return totalMin;
    }



    public boolean isDisconnected(String DriverId, DBHelper dbHelper){
        boolean isDisconnected = false;
        try{
            JSONArray eventArray = getSavedMalDiaTimeLog(Integer.valueOf(DriverId), dbHelper);
            if(eventArray.length() > 0){
                JSONObject eventObj = (JSONObject) eventArray.get(eventArray.length()-1);
                String status = eventObj.getString(ConstantsKeys.Status);
                if(status.equals("DisConnected")){
                    isDisconnected = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isDisconnected;
    }




    /*-------------------- GET POWER COMPLIANCE MALFUNCTION & DIAGNOSTIC LOG SAVED Array -------------------- */
    public JSONArray getSavedPowerCompArray(int ProjectId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getPowerComplianceLog(ProjectId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.POWER_COMP_MAL_DIA_LIST));
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


    /*-------------------- MALFUNCTION & DIAGNOSTIC DB Helper -------------------- */
    public void PowerComplianceLogHelper( int driverId, DBHelper dbHelper, JSONArray eventArray){

        Cursor rs = dbHelper.getMalfunctionDiagnosticLog(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateMalfunctionDiagnosticLog(driverId, eventArray );        // UPDATE MALFUNCTION & DIAGNOSTIC LOG ARRAY
        }else{
            dbHelper.InsertMalfncnDiagnosticLog( driverId, eventArray  );      // INSERT MALFUNCTION & DIAGNOSTIC LOG ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


    // update poser compliance occurred event time log array for 1 day only
    public void updatePowerOccEventLog(DateTime currentTime, DBHelper dbHelper){
        try {

            DateTime oneDayDiffDate = currentTime.minusDays(1);
            JSONArray updatedTimeArray = new JSONArray();
            JSONArray lastOccEventTimeArray = getSavedPowerCompArray(Integer.valueOf(Globally.PROJECT_ID), dbHelper);

            // add only last 24 hour events in array
            for (int i = 0; i < lastOccEventTimeArray.length() ; i++) {
                JSONObject eventObj = (JSONObject) lastOccEventTimeArray.get(i);
                DateTime selectedDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.DisConnectStartTime), false);
                if(selectedDateTime.isAfter(oneDayDiffDate)){
                    updatedTimeArray.put(eventObj);
                }
            }

            if(updatedTimeArray.length() > 0){
                JSONObject lastObj = (JSONObject)updatedTimeArray.get(updatedTimeArray.length()-1);
                DateTime disconStartTime = Globally.getDateTimeObj(lastObj.getString(ConstantsKeys.DisConnectStartTime), false);
                int timeInMin = Minutes.minutesBetween(disconStartTime, currentTime).getMinutes();
                lastObj.put(ConstantsKeys.DisConnectEndTime, currentTime);
                lastObj.put(ConstantsKeys.TotalMin, timeInMin);
                lastObj.put(ConstantsKeys.Status, "Connected");

                // update time in last item
                updatedTimeArray.put(updatedTimeArray.length()-1, lastObj);

            }

            PowerComplianceLogHelper(Integer.valueOf(Globally.PROJECT_ID), dbHelper, updatedTimeArray);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getTotalPowerComplianceMin(DBHelper dbHelper){
        int totalMin = 0;

        try{
            JSONArray eventArray = getSavedPowerCompArray(Integer.valueOf(Globally.PROJECT_ID), dbHelper);

            for(int i = 0 ; i < eventArray.length() ; i++){
                JSONObject eventObj = (JSONObject) eventArray.get(i);
              //  if(eventObj.getString(ConstantsKeys.Status).equals("PwrComplianceEvent")){
                    if(i == eventArray.length()-1){
                        DateTime disconStartTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.DisConnectStartTime), false);
                        DateTime disconEndTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
                        int timeInMin = Minutes.minutesBetween(disconStartTime, disconEndTime).getMinutes();
                        totalMin = totalMin + timeInMin;
                    }else{
                        totalMin = totalMin + eventObj.getInt(ConstantsKeys.TotalMin);
                    }
               // }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return totalMin;
    }




}
