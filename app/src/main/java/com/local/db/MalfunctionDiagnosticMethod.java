package com.local.db;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.messaging.logistic.Globally;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
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



    public boolean isUnPostedOfflineEvent(String DriverId, String EventType, DBHelper dbHelper){

        boolean isUnpostedEvent = false;
        try{
            JSONArray malArray = getSavedMalDiagstcArray(Integer.parseInt(DriverId), dbHelper);
            for(int i = malArray.length()-1 ; i >= 0; i-- ){
                JSONObject obj = (JSONObject) malArray.get(i);
                if(obj.getString(ConstantsKeys.DiagnosticType).equals(EventType) && obj.getBoolean(ConstantsKeys.IsCleared) == false){
                    isUnpostedEvent = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isUnpostedEvent;
    }


    // Check all events if not posted to server.
    public boolean isUnPostAnyEvent(String DriverId, DBHelper dbHelper){
        boolean isUnpostedEvent = false;
        try{
            JSONArray malArray = getSavedMalDiagstcArray(Integer.parseInt(DriverId), dbHelper);
            for(int i = malArray.length()-1 ; i >= 0; i-- ){
                JSONObject obj = (JSONObject) malArray.get(i);
                if(obj.getString(ConstantsKeys.DiagnosticType).equals(Constants.MissingDataDiagnostic) && obj.getBoolean(ConstantsKeys.IsCleared) == false){
                    isUnpostedEvent = true;
                    break;
                } else if(obj.getString(ConstantsKeys.DiagnosticType).equals(Constants.PowerComplianceDiagnostic) && obj.getBoolean(ConstantsKeys.IsCleared) == false){
                    isUnpostedEvent = true;
                    break;
                } else if(obj.getString(ConstantsKeys.DiagnosticType).equals(Constants.EngineSyncDiagnosticEvent) && obj.getBoolean(ConstantsKeys.IsCleared) == false){
                    isUnpostedEvent = true;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isUnpostedEvent;

    }


        // online clear event object
    public JSONArray updateOfflineUnPostedMalDiaEvent(String DriverId, String EventType, String Remarks,
                                                      DBHelper dbHelper, Context context)  {
        JSONArray malArray = getSavedMalDiagstcArray(Integer.parseInt(DriverId), dbHelper);
        try {
            for(int i = malArray.length()-1 ; i >= 0; i-- ){
                JSONObject malfnDiagnstcObj = (JSONObject) malArray.get(i);
                if(malfnDiagnstcObj.getString(ConstantsKeys.DiagnosticType).equals(EventType) &&
                          malfnDiagnstcObj.getBoolean(ConstantsKeys.IsCleared) == false){

                    malfnDiagnstcObj.put(ConstantsKeys.IsCleared, true);
                    malfnDiagnstcObj.put(ConstantsKeys.ClearedTime, Globally.GetCurrentUTCTimeFormat());
                    malfnDiagnstcObj.put(ConstantsKeys.Remarks, Remarks);
                    malfnDiagnstcObj.put(ConstantsKeys.ClearedTimeEngineHours, SharedPref.getObdEngineHours(context));
                    malfnDiagnstcObj.put(ConstantsKeys.ClearedTimeOdometer, Constants.meterToKm(SharedPref.getObdOdometer(context)) );

                    malArray.put(i, malfnDiagnstcObj);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        // update array in db
        MalfnDiagnstcLogHelper(Integer.parseInt(DriverId), dbHelper, malArray);

        return malArray;
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
    public void updatePowerOccEventLog(DateTime currentTime, float minDiff, DBHelper dbHelper){
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
              //  DateTime disconStartTime = Globally.getDateTimeObj(lastObj.getString(ConstantsKeys.DisConnectStartTime), false);
                lastObj.put(ConstantsKeys.DisConnectEndTime, currentTime);
                lastObj.put(ConstantsKeys.TotalMin, minDiff);
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
                totalMin = totalMin + eventObj.getInt(ConstantsKeys.TotalMin);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return totalMin;
    }





    /*-------------------- GET MALFUNCTION & DIAGNOSTIC Events duration Array -------------------- */
    public JSONArray getMalDiaDurationArray(DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getMalDiaDurationLog(Integer.valueOf(Globally.PROJECT_ID));

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.MAl_DIA_EVENT_DURATION_LIST));
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

    /*-------------------- MALFUNCTION & DIAGNOSTIC Events duration DB Helper -------------------- */
    public void MalDiaDurationHelper( DBHelper dbHelper, JSONArray eventArray){

        Cursor rs = dbHelper.getMalDiaDurationLog(Integer.valueOf(Globally.PROJECT_ID));

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateMalDiaDurationLog(Integer.valueOf(Globally.PROJECT_ID), eventArray );        // UPDATE MALFUNCTION & DIAGNOSTIC Events duration ARRAY
        }else{
            dbHelper.InsertMalDiaDurationLog( Integer.valueOf(Globally.PROJECT_ID), eventArray  );      // INSERT MALFUNCTION & DIAGNOSTIC Events duration ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }




    public JSONObject getMalDiaDurationObj(String EventDateTime, String EventEndDateTime, String DetectionDataEventCode,
                                           int TotalMinutes, boolean IsClearEvent ){
        JSONObject obj = new JSONObject();
        try{
            obj.put(ConstantsKeys.EventDateTime, EventDateTime);
            obj.put(ConstantsKeys.EventEndDateTime, EventEndDateTime);
            obj.put(ConstantsKeys.DetectionDataEventCode, DetectionDataEventCode);
            obj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
            obj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
        return obj;

    }

    // update end time instantly in power diagnostic event but not cleared, because we are clearing it after 5 min
    public void updateTimeInPowerDiagnoseDia(DBHelper dbHelper, Context context){
        JSONArray array = getMalDiaDurationArray(dbHelper);
        boolean isUpdatedEvent = false;
        try{
            for(int i = array.length()-1 ; i >=0 ; i--){
                JSONObject eventObj = (JSONObject) array.get(i);
                if(eventObj.getString(ConstantsKeys.DetectionDataEventCode).equals(Constants.PowerComplianceDiagnostic)){
                    int lastEventMinutes = 0;
                    if(eventObj.has(ConstantsKeys.TotalMinutes)){
                        lastEventMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
                    }

                    DateTime eventDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.EventDateTime), false);
                    DateTime currentDate = Globally.GetCurrentUTCDateTime();
                    long minDiff = Constants.getDateTimeDuration(eventDateTime, currentDate).getStandardMinutes();
                    if(minDiff > 0 && lastEventMinutes == 0){
                        String ObdOdometer = "";
                        try{
                            ObdOdometer = Constants.meterToKm(SharedPref.getObdOdometer(context));
                        }catch (Exception e){e.printStackTrace();}

                        eventObj.put(ConstantsKeys.TotalMinutes, minDiff);
                        eventObj.put(ConstantsKeys.EventEndDateTime, currentDate.toString());
                        eventObj.put(ConstantsKeys.ClearEngineHours, SharedPref.getObdEngineHours(context));
                        eventObj.put(ConstantsKeys.ClearOdometer, ObdOdometer);

                        array.put(i, eventObj);

                        isUpdatedEvent = true;

                        break;
                    }
                }
            }

            if(isUpdatedEvent) {
                MalDiaDurationHelper(dbHelper, array);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void addNewMalDiaEventInDurationArray(DBHelper dbHelper, String EventDateTime,
                                                 String DetectionDataEventCode, Context context){
        try{
            JSONArray array = getMalDiaDurationArray(dbHelper);
            JSONObject newItemObj = getMalDiaDurationObj(EventDateTime, EventDateTime, DetectionDataEventCode, 0,  false);
            if(DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic)){
                newItemObj.put(ConstantsKeys.ClearEngineHours, SharedPref.getObdEngineHours(context));
                newItemObj.put(ConstantsKeys.ClearOdometer, Constants.meterToKm(SharedPref.getObdOdometer(context)));
            }
            array.put(newItemObj);

            // save in db
            MalDiaDurationHelper(dbHelper, array);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public double getLast24HourEventsDurInMin(String EventType, DBHelper dbHelper){
        double totalMin = 0;
        try{
            JSONArray eventsArray = getMalDiaDurationArray(dbHelper);

            for (int i = 0; i < eventsArray.length() ; i++) {
                JSONObject eventObj = (JSONObject) eventsArray.get(i);
                String DetectionDataEventCode = eventObj.getString(ConstantsKeys.DetectionDataEventCode);
                int lastEventMinutes = 0;
                if(eventObj.has(ConstantsKeys.TotalMinutes)){
                    lastEventMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
                }


                if(DetectionDataEventCode.equals(EventType)){
                    totalMin = totalMin + lastEventMinutes;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return totalMin;
    }




// update Malfunction/Diagnostic enable/disable Status
    public void updateMalfDiaStatusForEnable(Globally global, Constants constants, DBHelper dbHelper, Context context){

        try {
            JSONArray array = getMalDiaDurationArray(dbHelper);
            int offset = Math.abs((int) global.GetTimeZoneOffSet());
            constants.resetMalDiaEvents(context);

            for(int i = 0 ; i< array.length(); i++){
                JSONObject obj = (JSONObject) array.get(i);
                String DetectionDataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);
                String EventDateTime = obj.getString(ConstantsKeys.EventDateTime);
                DateTime eventUtcDate = Globally.getDateTimeObj(EventDateTime, false) ;
                DateTime driverZoneDate = eventUtcDate.minusHours(offset);
                boolean IsClearEvent = false;
                if(obj.has(ConstantsKeys.IsClearEvent)) {
                    IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                }

                if(!IsClearEvent) {
                    if (DetectionDataEventCode.equals(Constants.MissingDataDiagnostic)) {
                        //SharedPref.saveLocDiagnosticStatus(true, driverZoneDate.toString(), EventDateTime, context);
                        // constants.saveDiagnstcStatus(context, true);
                    } else if (DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic) && !IsClearEvent) {
                        SharedPref.savePowerMalfunctionOccurStatus(
                                SharedPref.isPowerMalfunctionOccurred(context),
                                true, driverZoneDate.toString(), context);
                        constants.saveDiagnstcStatus(context, true);

                    } else if (DetectionDataEventCode.equals(Constants.EngineSyncDiagnosticEvent) && !IsClearEvent) {
                        SharedPref.saveEngSyncDiagnstcStatus(true, context);
                        constants.saveDiagnstcStatus(context, true);

                    } else if (DetectionDataEventCode.equals(Constants.PositionComplianceMalfunction)) {
                        SharedPref.saveLocMalfunctionOccurStatus(true, driverZoneDate.toString(), EventDateTime, context);
                        constants.saveMalfncnStatus(context, true);
                    } else if (DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction)) {
                        SharedPref.savePowerMalfunctionOccurStatus(true,
                                SharedPref.isPowerDiagnosticOccurred(context),
                                driverZoneDate.toString(), context);
                        constants.saveMalfncnStatus(context, true);
                    } else if (DetectionDataEventCode.equals(Constants.EngineSyncMalfunctionEvent)) {
                        SharedPref.saveEngSyncMalfunctionStatus(true, context);
                        constants.saveMalfncnStatus(context, true);
                    }
                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }



    private JSONObject getJsonObjForClearEvent(String EventDateTime, String DataEventCode, boolean IsClearEvent,
                                               JSONObject eventObj, Context context){
        JSONObject clearObj = new JSONObject();

        try{

            clearObj.put(ConstantsKeys.DriverId, SharedPref.getDriverId(context));
            clearObj.put(ConstantsKeys.CompanyId, DriverConst.GetDriverDetails(DriverConst.CompanyId, context));
            clearObj.put(ConstantsKeys.UnitNo, DriverConst.GetDriverTripDetails(DriverConst.Truck, context));
            clearObj.put(ConstantsKeys.EventDateTime, EventDateTime);
            clearObj.put(ConstantsKeys.DetectionDataEventCode, DataEventCode);

            int TotalMinutes = -1;
            if(eventObj.has(ConstantsKeys.TotalMinutes)){
                TotalMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
            }

            if(TotalMinutes > 0){
                String EventEndDateTime = "", ClearEngineHours = "", ClearOdometer = "";

                if(eventObj.has(ConstantsKeys.EventEndDateTime)){
                    EventEndDateTime = eventObj.getString(ConstantsKeys.EventEndDateTime);
                }else if(eventObj.has(ConstantsKeys.ClearEventDateTime)){
                    EventEndDateTime = eventObj.getString(ConstantsKeys.ClearEventDateTime);
                }
                if(EventEndDateTime.length() == 0){
                    DateTime endTime = Globally.getDateTimeObj(EventDateTime, false);
                    EventEndDateTime = endTime.plusMinutes(TotalMinutes).toString();
                }

                if(eventObj.has(ConstantsKeys.ClearEngineHours)){
                    ClearEngineHours = eventObj.getString(ConstantsKeys.ClearEngineHours);
                }
                if(eventObj.has(ConstantsKeys.ClearOdometer)){
                    ClearOdometer = eventObj.getString(ConstantsKeys.ClearOdometer);
                }

                clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                clearObj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
                clearObj.put(ConstantsKeys.ClearEventDateTime, EventEndDateTime);
                clearObj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
                clearObj.put(ConstantsKeys.ClearOdometer, ClearOdometer);

            }else {
                DateTime EventDate = Globally.getDateTimeObj(EventDateTime, false);
                DateTime currentTime = Globally.GetCurrentUTCDateTime();
                int minDiff = Constants.getMinDiff(EventDate, currentTime);

                clearObj.put(ConstantsKeys.EventEndDateTime, currentTime.toString());
                clearObj.put(ConstantsKeys.TotalMinutes, minDiff);

                clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                clearObj.put(ConstantsKeys.ClearEventDateTime, currentTime.toString());
                clearObj.put(ConstantsKeys.ClearEngineHours, SharedPref.getObdEngineHours(context));
                clearObj.put(ConstantsKeys.ClearOdometer, Constants.meterToKm(SharedPref.getObdOdometer(context)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return clearObj;
    }


    public JSONArray updateAutoClearEvent(DBHelper dbHelper, String EventCode, boolean isClear, boolean isUpdate, Context context){

        JSONArray eventClearArray = new JSONArray();
        try {
            DateTime currentDateTime = Globally.GetCurrentUTCDateTime();
            JSONArray eventArray = getMalDiaDurationArray(dbHelper);
            for(int i = 0 ; i < eventArray.length() ; i++) {
                JSONObject obj = (JSONObject) eventArray.get(i);
                String EventDateTime = obj.getString(ConstantsKeys.EventDateTime);
                String DataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);

                if(EventCode.length() > 0){ // means clear selected Event only
                    boolean IsClearEvent = false;
                    if(obj.has(ConstantsKeys.IsClearEvent)){
                        IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                    }

                    if(!IsClearEvent ){
                        if(EventCode.equals(DataEventCode) ) {
                            if(Constants.isDiagnosticEvent(EventCode)) {

                               JSONObject clearObj = getJsonObjForClearEvent(EventDateTime, DataEventCode, isClear, obj, context);

                               if(clearObj.getInt(ConstantsKeys.TotalMinutes) >= 0) {
                                   eventClearArray.put(clearObj);
                               }
                                eventArray.put(i, clearObj);

                            }else {
                                DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                                long dayDiff = Constants.getDateTimeDuration(eventTime, currentDateTime).getStandardDays();
                                if(dayDiff >= 24 ) {
                                    JSONObject clearObj = getJsonObjForClearEvent(EventDateTime, DataEventCode, isClear, obj, context);

                                    if(clearObj.getInt(ConstantsKeys.TotalMinutes) >= 0) {
                                        eventClearArray.put(clearObj);
                                    }
                                    eventArray.put(i, clearObj);

                                }
                            }
                        }

                    }
                }else{  // clear all events those are 24 hour old
                    DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                    long dayDiff = Constants.getDateTimeDuration(eventTime, currentDateTime).getStandardDays();
                    if(dayDiff >= 24 ) {
                        JSONObject clearObj = getJsonObjForClearEvent(EventDateTime, DataEventCode, isClear, obj, context);

                        if(clearObj.getInt(ConstantsKeys.TotalMinutes) >= 0) {
                            eventClearArray.put(clearObj);
                        }
                        eventArray.put(i, clearObj);


                    }

                }
            }


            // update duration array after clear events
            if(isUpdate) {
                MalDiaDurationHelper(dbHelper, eventArray);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return eventClearArray;
    }






    // update duration array after clear
    public boolean HasEngSyncEventForClear(String ClearEventCode, DBHelper dbHelper, Constants constants, Globally global, Context context){
        boolean hasEventForClear = false;
        try {
            String lastCalledTime = SharedPref.getEngSyncClearEventCallTime(context);
            if(lastCalledTime.length() > 10){
                int minDiff = constants.minDiffMalfunction(lastCalledTime, global, context);
                if (minDiff > 0) {
                    SharedPref.setEngSyncClearEventCallTime(Globally.GetCurrentDateTime(), context);

                    JSONArray eventArray = getMalDiaDurationArray(dbHelper);
                    for (int i = 0; i < eventArray.length(); i++) {
                        JSONObject obj = (JSONObject) eventArray.get(i);
                        String DataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);
                        boolean isCleared = false;
                        if(obj.has(ConstantsKeys.IsClearEvent)){
                            isCleared = obj.getBoolean(ConstantsKeys.IsClearEvent);
                        }
                        if (ClearEventCode.equals(DataEventCode) && isCleared == false) {
                            hasEventForClear = true;
                            break;
                        }

                    }
                }
            }else{
                SharedPref.setEngSyncClearEventCallTime(Globally.GetCurrentDateTime(), context);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return hasEventForClear;
    }







    // =======================================================================================================================

    /*-------------------- GET POSITIONING MALFUNCTION & DIAGNOSTIC Events duration Array -------------------- */
    public JSONArray getPositioningMalDiaArray(DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getPositioningMalDiaLog(Integer.valueOf(Globally.PROJECT_ID));

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.POSITION_MAl_DIA_EVENT_LIST));
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

    /*-------------------- POSITIONING MALFUNCTION & DIAGNOSTIC Events duration DB Helper -------------------- */
    public void PositioningMalDiaHelper( DBHelper dbHelper, JSONArray eventArray){

        Cursor rs = dbHelper.getPositioningMalDiaLog(Integer.valueOf(Globally.PROJECT_ID));

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdatePositioningMalDiaLog(Integer.valueOf(Globally.PROJECT_ID), eventArray );        // UPDATE MALFUNCTION & DIAGNOSTIC Events duration ARRAY
        }else{
            dbHelper.InsertPositioningMalDiaLog( Integer.valueOf(Globally.PROJECT_ID), eventArray  );      // INSERT MALFUNCTION & DIAGNOSTIC Events duration ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }

    public JSONArray AddNewItemInPositionArray(String EventCode, DBHelper dbHelper){

        JSONObject newObj = getMalDiaDurationObj(Globally.GetCurrentDateTime(), "",
                                EventCode, 0, false);
        JSONArray array = getPositioningMalDiaArray(dbHelper);
        array.put(newObj);

        return array;
    }


    public void clearEventsMoreThen1Day(DBHelper dbHelper){
        try{
            DateTime currentTime = Globally.GetCurrentJodaDateTime();
            JSONArray updatedTimeArray = new JSONArray();
            JSONArray lastOccEventTimeArray = getPositioningMalDiaArray(dbHelper);

            // add only last 24 hour events in array
            for (int i = 0; i < lastOccEventTimeArray.length() ; i++) {
                JSONObject eventObj = (JSONObject) lastOccEventTimeArray.get(i);
                DateTime selectedDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.EventDateTime), false);
                long hourDiff = Constants.getDateTimeDuration(selectedDateTime, currentTime).getStandardDays();
                if(hourDiff <= 24){
                    updatedTimeArray.put(eventObj);
                }else{
                    String EventEndDateTime = eventObj.getString(ConstantsKeys.EventEndDateTime);
                    if(EventEndDateTime.length() == 0){
                        updatedTimeArray.put(eventObj);
                    }
                }
            }

            PositioningMalDiaHelper(dbHelper, updatedTimeArray);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void updateTimeOnLocationReceived(String EventCode, DBHelper dbHelper){
        try{
            DateTime currentTime = Globally.GetCurrentJodaDateTime();
            JSONArray lastOccEventTimeArray = getPositioningMalDiaArray(dbHelper);

            for (int i = lastOccEventTimeArray.length()-1; i >= 0  ; i--) {
                JSONObject obj = (JSONObject) lastOccEventTimeArray.get(i);

                String EventEndDateTime = obj.getString(ConstantsKeys.EventEndDateTime);
                String DetectionDataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);

                if(EventEndDateTime.length() == 0 && EventCode.equals(DetectionDataEventCode)){
                    DateTime selectedDateTime = Globally.getDateTimeObj(obj.getString(ConstantsKeys.EventDateTime), false);
                    long minDiff = Constants.getDateTimeDuration(selectedDateTime, currentTime).getStandardMinutes();

                    boolean IsClearEvent = false;
                    if(obj.has(ConstantsKeys.IsClearEvent)){
                        IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                    }

                    JSONObject updatedObj = new JSONObject();
                    updatedObj.put(ConstantsKeys.EventDateTime, obj.getString(ConstantsKeys.EventDateTime));
                    updatedObj.put(ConstantsKeys.EventEndDateTime, currentTime.toString());
                    updatedObj.put(ConstantsKeys.DetectionDataEventCode, DetectionDataEventCode);
                    updatedObj.put(ConstantsKeys.TotalMinutes, minDiff);
                    updatedObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);

                    lastOccEventTimeArray.put(i, updatedObj);

                    break;
                }

            }

            PositioningMalDiaHelper(dbHelper, lastOccEventTimeArray);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public double getLast24HourLocDiaEventsInMin(String EventType, DBHelper dbHelper){
        double totalMin = 0;
        try{
            JSONArray eventsArray = getPositioningMalDiaArray(dbHelper);

            for (int i = 0; i < eventsArray.length() ; i++) {
                JSONObject eventObj = (JSONObject) eventsArray.get(i);
                String DetectionDataEventCode = eventObj.getString(ConstantsKeys.DetectionDataEventCode);

                double lastEventMinutes = 0;
                if(eventObj.has(ConstantsKeys.TotalMinutes)){
                    lastEventMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
                }

                if(DetectionDataEventCode.equals(EventType)){
                    totalMin = totalMin + lastEventMinutes;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return totalMin;
    }



}
