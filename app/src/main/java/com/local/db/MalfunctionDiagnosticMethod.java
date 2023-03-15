package com.local.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.constants.Constants;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.Utils;
import com.driver.details.DriverConst;
import com.als.logistic.Globally;
import com.als.logistic.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MalfunctionDiagnosticMethod {


    public MalfunctionDiagnosticMethod() {
        super();
    }


    /*-------------------- GET MALFUNCTION & DIAGNOSTIC LOG SAVED Array -------------------- */
    public JSONArray getSavedMalDiagstcArray( DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getMalfunctionDiagnosticLog(Globally.PROJECT_ID_INT);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.MALFUNCTION_DIANOSTIC_LIST));
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
    public void MalfnDiagnstcLogHelper(DBHelper dbHelper, JSONArray eventArray){

        Cursor rs = dbHelper.getMalfunctionDiagnosticLog(Globally.PROJECT_ID_INT);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateMalfunctionDiagnosticLog(Globally.PROJECT_ID_INT, eventArray );        // UPDATE MALFUNCTION & DIAGNOSTIC LOG ARRAY
        }else{
            dbHelper.InsertMalfncnDiagnosticLog( Globally.PROJECT_ID_INT, eventArray  );      // INSERT MALFUNCTION & DIAGNOSTIC LOG ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }




    public JSONObject GetMalDiaEventJson(String DriverId, String DeviceId, String VIN, String UnitNo, String CompanyId,
                                         String EngineHours, String StartOdometer, String EndOdometer, String EventDateTime,
                                         String DiagnosticType, String MalfunctionDefinition, boolean IsCleared, String ClearedTime,
                                         String ClearedTimeOdometer, String ClearedTimeEngineHours,
                                         String LocationType, String CurrentStatus )  {

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
            malfnDiagnstcObj.put(ConstantsKeys.LocationType, LocationType);
            malfnDiagnstcObj.put(ConstantsKeys.CurrentStatus, CurrentStatus);

        }catch (Exception e){
            e.printStackTrace();
        }

        return malfnDiagnstcObj;
    }



    public boolean isUnPostedOfflineEvent(String EventType, DBHelper dbHelper){

        boolean isUnpostedEvent = false;
        try{
            JSONArray malArray = getSavedMalDiagstcArray( dbHelper);
            for(int i = malArray.length()-1 ; i >= 0; i-- ){
                JSONObject obj = (JSONObject) malArray.get(i);
                String DiagnosticType = obj.getString(ConstantsKeys.DiagnosticType);
                if(DiagnosticType.equals(EventType) && obj.getBoolean(ConstantsKeys.IsCleared) == false){
                    if(DiagnosticType.equals(Constants.MissingDataDiagnostic) ){
                        // String MalfunctionDefinition = obj.getString(ConstantsKeys.MalfunctionDefinition);
                        //!MalfunctionDefinition.contains("Driver ignore to save location")
                        String LocationType = obj.getString(ConstantsKeys.LocationType);
                        //LocationType.equals("X") || LocationType.equals("E")
                        if(LocationType.length() == 0){
                            isUnpostedEvent = true;
                        }
                    }else {
                        isUnpostedEvent = true;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isUnpostedEvent;
    }


    // Check all events if not posted to server.
    public boolean isUnPostAnyEvent( DBHelper dbHelper){
        boolean isUnpostedEvent = false;
        try{
            JSONArray malArray = getSavedMalDiagstcArray( dbHelper);
            for(int i = malArray.length()-1 ; i >= 0; i-- ){
                JSONObject obj = (JSONObject) malArray.get(i);
                if(obj.getString(ConstantsKeys.DiagnosticType).equals(Constants.MissingDataDiagnostic) && obj.getBoolean(ConstantsKeys.IsCleared) == false){
                    String LocationType = obj.getString(ConstantsKeys.LocationType);
                    if(LocationType.length() > 0){
                        isUnpostedEvent = true;
                        break;
                    }
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
        JSONArray malArray = getSavedMalDiagstcArray(dbHelper);
        try {
            for(int i = malArray.length()-1 ; i >= 0; i-- ){
                JSONObject malfnDiagnstcObj = (JSONObject) malArray.get(i);
                if(malfnDiagnstcObj.getString(ConstantsKeys.DiagnosticType).equals(EventType) &&
                        malfnDiagnstcObj.getBoolean(ConstantsKeys.IsCleared) == false){

                    malfnDiagnstcObj.put(ConstantsKeys.IsCleared, true);
                    malfnDiagnstcObj.put(ConstantsKeys.ClearedTime, Globally.GetCurrentUTCTimeFormat());
                    malfnDiagnstcObj.put(ConstantsKeys.Remarks, Remarks);
                    malfnDiagnstcObj.put(ConstantsKeys.ClearedTimeEngineHours, Constants.get2DecimalEngHour(context));  //SharedPref.getObdEngineHours(context));
                    malfnDiagnstcObj.put(ConstantsKeys.ClearedTimeOdometer, SharedPref.getObdOdometer(context) );

                    malArray.put(i, malfnDiagnstcObj);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        // update array in db
        MalfnDiagnstcLogHelper(dbHelper, malArray);

        return malArray;
    }








    /*-------------------- GET MALFUNCTION & DIAGNOSTIC OCCURED TIME LOG -------------------- */
    public JSONArray getSavedMalDiaTimeLog(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getMalDiaTimeLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.MAL_DIA_OCCURRED_TIME_LIST));
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
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.POWER_COMP_MAL_DIA_LIST));
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
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.MAl_DIA_EVENT_DURATION_LIST));
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


    public void checkMissingEventStatus(DBHelper dbHelper, Context context){

        try{
            /* Some times missing events clear status is wrongly updated.
             Due to that events are not clearing until refresh the app.
             Here we are are clarifying missing events are really cleared on not   */

            if(!SharedPref.isMissingDiaOccur(context)) {
                JSONArray malDiaArray = getMalDiaDurationArray(dbHelper);
                for (int i = malDiaArray.length() - 1; i >= 0; i--) {
                    JSONObject objItem = (JSONObject) malDiaArray.get(i);
                    String DetectionDataEventCode = objItem.getString(ConstantsKeys.DetectionDataEventCode);

                    boolean isClearEvent = true;
                    try {
                        if (objItem.has(ConstantsKeys.ClearEventId) && objItem.getString(ConstantsKeys.ClearEventId).trim().length() > 0) {
                            if (objItem.getInt(ConstantsKeys.ClearEventId) > 0) {
                                isClearEvent = true;
                            } else {
                                isClearEvent = objItem.getBoolean(ConstantsKeys.IsClearEvent);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (DetectionDataEventCode.equals(Constants.MissingDataDiagnostic) && !isClearEvent) {
                        SharedPref.saveMissingDiaStatus(true, context);
                        break;
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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


    /*-------------------- check power uncleared event status -------------------- */
    public boolean isPowerUnclearedEvent(DBHelper dbHelper){

        boolean isPowerUnclearedEvent = false;
        JSONArray array = getMalDiaDurationArray(dbHelper);

        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                if(obj.getString(ConstantsKeys.DetectionDataEventCode).equals(Constants.PowerComplianceDiagnostic)){
                    boolean IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                    if(!IsClearEvent){
                        isPowerUnclearedEvent = true;
                        break;
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isPowerUnclearedEvent;

    }

    public JSONArray getEventsDateWise(String selectedDate, Globally global, DBHelper dbHelper, Context context){
        JSONArray array = new JSONArray();
        try {
            JSONArray eventArray = getMalDiaDurationArray(dbHelper);
            for(int i = 0 ; i < eventArray.length() ; i++){
                JSONObject obj = (JSONObject) eventArray.get(i);
                String EventDateTime = obj.getString(ConstantsKeys.EventDateTime);
                if(EventDateTime.length() > 10){
                    int offset = Math.abs((int) global.GetDriverTimeZoneOffSet(context));
                    String eventDate = Globally.getDateTimeObj(EventDateTime, false).minusHours(offset).toString();

                    EventDateTime = eventDate.substring(0, 10);
                    if(selectedDate.equals(EventDateTime)){
                        array.put(obj);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return array;
    }

    public JSONObject getNewMalDiaDurationObj(String DriverId, String EventDateTime, String EventEndDateTime, String DetectionDataEventCode,
                                              int TotalMinutes, boolean IsClearEvent , String ClearEngineHours, String ClearOdometer,
                                              String StartOdometer, String startEngineHours, String LocationType, String CurrentStatus,
                                              String VinNumber){
        JSONObject obj = new JSONObject();
        try{
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.VIN, VinNumber);
            obj.put(ConstantsKeys.EventDateTime, EventDateTime);
            obj.put(ConstantsKeys.EventEndDateTime, EventEndDateTime);
            obj.put(ConstantsKeys.DetectionDataEventCode, DetectionDataEventCode);
            obj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
            obj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
            obj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
            obj.put(ConstantsKeys.ClearOdometer, ClearOdometer);
            obj.put(ConstantsKeys.StartOdometer, StartOdometer);
            obj.put(ConstantsKeys.EngineHours, startEngineHours);
            obj.put(ConstantsKeys.LocationType, LocationType);
            obj.put(ConstantsKeys.CurrentStatus, CurrentStatus);

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
                            ObdOdometer = SharedPref.getObdOdometer(context);
                        }catch (Exception e){e.printStackTrace();}

                        eventObj.put(ConstantsKeys.TotalMinutes, minDiff);
                        eventObj.put(ConstantsKeys.EventEndDateTime, currentDate.toString());
                        eventObj.put(ConstantsKeys.ClearEngineHours, Constants.get2DecimalEngHour(context));    //SharedPref.getObdEngineHours(context));
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


    public void addNewMalDiaEventInDurationArray(DBHelper dbHelper, String DriverId, String EventDateTime, String EventEndDateTime,
                                                 String DetectionDataEventCode, String EventStatus, String LocationType, String CurrentStatus,
                                                 Constants constants, Context context){
        try{
            String ClearEngineHours = Constants.get2DecimalEngHour(context);    //SharedPref.getObdEngineHours(context);
            String ClearOdometer = SharedPref.getObdOdometer(context);
            JSONArray array = getMalDiaDurationArray(dbHelper);
            JSONObject newItemObj;
            String lastSavedOdometer = SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, context);
            String lastSavedEngHr =  SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, context);
            String currentOdometer = SharedPref.getObdOdometer(context);
            String currentEngHr = Constants.get2DecimalEngHour(context);
            String VIN = SharedPref.getVINNumber(context);
            double engineHrDiffInMin = constants.getEngineHourDiff(lastSavedEngHr, currentEngHr);

            try {
                if (currentOdometer.length() > 8 && !currentOdometer.contains(".")) {
                    currentOdometer = Constants.meterToKmWithObd(currentOdometer);
                }

                if (lastSavedOdometer.length() > 8 && !lastSavedOdometer.contains(".")) {
                    lastSavedOdometer = Constants.meterToKmWithObd(lastSavedOdometer);
                }
            }catch (Exception e){}

            if(DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                newItemObj = getNewMalDiaDurationObj(DriverId, EventDateTime, EventEndDateTime, DetectionDataEventCode, (int)engineHrDiffInMin,
                        false, ClearEngineHours, ClearOdometer, lastSavedOdometer, lastSavedEngHr, LocationType, CurrentStatus, VIN);    //CurrentStatus
            }else if (DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction)){
                newItemObj = getNewMalDiaDurationObj(DriverId, EventDateTime, EventEndDateTime, DetectionDataEventCode, (int)engineHrDiffInMin,
                        false, ClearEngineHours, ClearOdometer, ClearOdometer, ClearEngineHours, LocationType, CurrentStatus, VIN);    //Cur
            }else{
                newItemObj = getNewMalDiaDurationObj(DriverId, EventDateTime, EventEndDateTime, DetectionDataEventCode, 0,
                        false, ClearEngineHours, ClearOdometer, currentOdometer, currentEngHr, LocationType, CurrentStatus, VIN);   //CurrentStatus
            }

            if(!EventStatus.equals("-1")){
                newItemObj.put(ConstantsKeys.Status, EventStatus);
            }

            if(DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic)){
                newItemObj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
                newItemObj.put(ConstantsKeys.ClearOdometer, ClearOdometer);
            }

            array.put(newItemObj);

            // save in db
            MalDiaDurationHelper(dbHelper, array);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isMissingEventAlreadyWithStatus(int status, String isPersonal, String type, DBHelper dbHelper){

        String jobType = Globally.JobStatus(status, Boolean.parseBoolean(isPersonal), type);

        boolean isMissingEventExist = isMissingEventAlreadyWithOtherJobs(jobType, dbHelper);

        return isMissingEventExist;
    }


    public boolean isMissingEventAlreadyWithOtherJobs(String jobType, DBHelper dbHelper){

        boolean isMissingEventExist = false;

        try {
            JSONArray eventsArray = getMalDiaDurationArray(dbHelper);

            for (int i = eventsArray.length()-1; i >= 0 ; i--) {
                JSONObject eventObj = (JSONObject) eventsArray.get(i);
                String EventDateTime = eventObj.getString(ConstantsKeys.EventDateTime);

                DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                long hourDiff = Constants.getDateTimeDuration(eventTime, Globally.GetCurrentUTCDateTime()).getStandardHours();
                if (hourDiff <= 24) {
                    if(eventObj.has(ConstantsKeys.CurrentStatus)){
                        String CurrentStatus = eventObj.getString(ConstantsKeys.CurrentStatus);
                        if(CurrentStatus.length() > 0 && CurrentStatus.equals(jobType)){  //if(!LocationType.equals("Certify Log")) {
                            isMissingEventExist = true;
                            break;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isMissingEventExist;
    }


    public boolean isMissingEventToClear(String eventLogDate, DBHelper dbHelper){

        boolean isClearEvent = false;

        try {
            JSONArray eventsArray = getMalDiaDurationArray(dbHelper);

            for (int i = eventsArray.length()-1; i >= 0 ; i--) {
                JSONObject eventObj = (JSONObject) eventsArray.get(i);
                String EventDateTime = eventObj.getString(ConstantsKeys.EventDateTime);
                DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                DateTime eventLogTime = Globally.getDateTimeObj(eventLogDate, false);

                long minDiff = Constants.getDateTimeDuration(eventTime, eventLogTime).getStandardMinutes();  //Globally.GetCurrentUTCDateTime()
                if (minDiff == 0) {
                    if(eventObj.has(ConstantsKeys.LocationType)){
                        String LocationType = eventObj.getString(ConstantsKeys.LocationType);
                        boolean IsClearEvent = eventObj.getBoolean(ConstantsKeys.IsClearEvent);

                        if((LocationType.equals("X") || LocationType.equals("E")) && !IsClearEvent){
                            isClearEvent = true;
                            break;
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return isClearEvent;
    }




    public JSONArray updateMissingDataToClear(String eventLogDate, String eventCurrentStatus, Context context, DBHelper dbHelper){

        JSONArray clearedEventArray = new JSONArray();

        try {
            JSONArray eventsArray = getMalDiaDurationArray(dbHelper);

            for (int i = eventsArray.length()-1; i >= 0 ; i--) {
                JSONObject eventObj = (JSONObject) eventsArray.get(i);
                String EventDateTime = eventObj.getString(ConstantsKeys.EventDateTime);
                DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                DateTime eventLogTime = Globally.getDateTimeObj(eventLogDate, false);

                long minDiff = Constants.getDateTimeDuration(eventTime, eventLogTime).getStandardMinutes();  //Globally.GetCurrentUTCDateTime()
                if (minDiff == 0) {
                    if(eventObj.has(ConstantsKeys.LocationType)){
                        String LocationType = eventObj.getString(ConstantsKeys.LocationType);
                        String CurrentStatus = eventObj.getString(ConstantsKeys.CurrentStatus);
                        boolean IsClearEvent = eventObj.getBoolean(ConstantsKeys.IsClearEvent);

                        // (LocationType.equals("X") || LocationType.equals("E"))
                        if( (LocationType.length() == 0 || CurrentStatus.equals(eventCurrentStatus) ||
                                CurrentStatus.equals("On Duty")) && !IsClearEvent){

                            String clearEventDate = Globally.GetCurrentUTCTimeFormat();
                            eventObj.put(ConstantsKeys.IsClearEvent, true);
                            eventObj.put(ConstantsKeys.ClearEventDateTime, clearEventDate);
                            eventObj.put(ConstantsKeys.EventEndDateTime, clearEventDate);

                            eventObj.put(ConstantsKeys.IsClearEvent, true);

                            eventObj.put(ConstantsKeys.ClearEngineHours, Constants.get2DecimalEngHour(context));
                            eventObj.put(ConstantsKeys.ClearOdometer, SharedPref.getObdOdometer(context));

                            eventObj.put(ConstantsKeys.CompanyId, DriverConst.GetDriverDetails(DriverConst.CompanyId, context));
                            eventObj.put(ConstantsKeys.UnitNo, SharedPref.getTruckNumber(context));

                            clearedEventArray.put(eventObj);

                            // update object in array
                            eventsArray.put(i, eventObj);   //eventArray.put(i, clearObj);

                            break;
                        }
                    }
                }
            }

            // update duration array after clear events
            MalDiaDurationHelper(dbHelper, eventsArray);

        }catch (Exception e){
            e.printStackTrace();
        }


        return clearedEventArray;
    }


    public boolean isUnPostedMissingEventToClear(String EventTime, DBHelper dbHelper){

        boolean isUnpostedEvent = false;
        try{
            JSONArray malArray = getSavedMalDiagstcArray( dbHelper);
            for(int i = malArray.length()-1 ; i >= 0; i-- ){
                JSONObject eventObj = (JSONObject) malArray.get(i);

                String EventDateTime = eventObj.getString(ConstantsKeys.EventDateTime);
                DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                DateTime eventLogTime = Globally.getDateTimeObj(EventTime, false);

                long minDiff = Constants.getDateTimeDuration(eventTime, eventLogTime).getStandardMinutes();  //Globally.GetCurrentUTCDateTime()
                if(minDiff == 0 && eventObj.has(ConstantsKeys.LocationType)){
                    String LocationType = eventObj.getString(ConstantsKeys.LocationType);
                    boolean IsClearEvent = eventObj.getBoolean(ConstantsKeys.IsClearEvent);
                    //(LocationType.equals("X") || LocationType.equals("E"))
                    if (LocationType.length() == 0 && !IsClearEvent) {
                        isUnpostedEvent = true;
                        break;
                    }


                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isUnpostedEvent;
    }



    // online clear event object
    public JSONArray updateOfflineUnPostedMissingEvent(String eventLogDate, String Remarks, DBHelper dbHelper, Context context)  {
        JSONArray malArray = getSavedMalDiagstcArray(dbHelper);
        try {
            for(int i = malArray.length()-1 ; i >= 0; i-- ){
                JSONObject eventObj = (JSONObject) malArray.get(i);
                String EventDateTime = eventObj.getString(ConstantsKeys.EventDateTime);
                DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                DateTime eventLogTime = Globally.getDateTimeObj(eventLogDate, false);

                long minDiff = Constants.getDateTimeDuration(eventTime, eventLogTime).getStandardMinutes();  //Globally.GetCurrentUTCDateTime()
                if (minDiff == 0) {
                    String LocationType = eventObj.getString(ConstantsKeys.LocationType);
                    boolean IsClearEvent = eventObj.getBoolean(ConstantsKeys.IsClearEvent);

                    //(LocationType.equals("X") || LocationType.equals("E"))
                    if(LocationType.length() == 0 && !IsClearEvent){

                        eventObj.put(ConstantsKeys.IsCleared, true);
                        eventObj.put(ConstantsKeys.ClearedTime, Globally.GetCurrentUTCTimeFormat());
                        eventObj.put(ConstantsKeys.Remarks, Remarks);
                        eventObj.put(ConstantsKeys.ClearedTimeEngineHours, Constants.get2DecimalEngHour(context));  //SharedPref.getObdEngineHours(context));
                        eventObj.put(ConstantsKeys.ClearedTimeOdometer, SharedPref.getObdOdometer(context) );
                        malArray.put(i, eventObj);
                    }

                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        // update array in db
        MalfnDiagnstcLogHelper(dbHelper, malArray);

        return malArray;
    }





    public double getLast24HrEngSyncEvents(DBHelper dbHelper){
        double totalMin = 0;
        try{
            JSONArray eventsArray = getMalDiaDurationArray(dbHelper);
            DateTime currentDate = Globally.GetCurrentUTCDateTime();
            boolean isAlreadyUncleared = false;
            for (int i = eventsArray.length()-1; i >=0 ; i--) {
                JSONObject eventObj = (JSONObject) eventsArray.get(i);
                String EventDateTime = eventObj.getString(ConstantsKeys.EventDateTime);
                //  DateTime eventDateTime = Globally.getDateTimeObj(EventDateTime, false);

                DateTime eventDateTime = Globally.getDateTimeObj(EventDateTime, false);
                long hourDiff = Constants.getDateTimeDuration(eventDateTime, currentDate).getStandardHours();
                if (hourDiff <= 24) {
                    String DetectionDataEventCode = eventObj.getString(ConstantsKeys.DetectionDataEventCode);
                    if (DetectionDataEventCode.equals(Constants.EngineSyncDiagnosticEvent)) {
                        int lastEventMinutes = 0;
                        boolean IsClearEvent = eventObj.getBoolean(ConstantsKeys.IsClearEvent);
                        if(!IsClearEvent) {

                            //if (eventObj.getString(ConstantsKeys.EventEndDateTime).equals("null")) {
                            if(isAlreadyUncleared){
                                if (eventObj.has(ConstantsKeys.TotalMinutes)) {
                                    lastEventMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
                                }else{
                                    lastEventMinutes = (int) Constants.getDateTimeDuration(eventDateTime, currentDate).getStandardMinutes();
                                }
                            }else{
                                lastEventMinutes = (int) Constants.getDateTimeDuration(eventDateTime, currentDate).getStandardMinutes();
                            }

                          /*  } else {

                            }*/

                            totalMin = totalMin + lastEventMinutes;

                            isAlreadyUncleared = true;
                        }else{
                            if (eventObj.has(ConstantsKeys.TotalMinutes)) {
                                lastEventMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
                            }
                            totalMin = totalMin + lastEventMinutes;
                        }
                    }
                }else{
                    break;
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return totalMin;
    }



    public double getLast24HrPwrEvents(String EngineHour, Double engineHrDiffInMin, DBHelper dbHelper){
        double totalMin = 0;
        try{
            JSONArray eventsArray = getMalDiaDurationArray(dbHelper);

            for (int i = eventsArray.length()-1; i >=0 ; i--) {
                JSONObject eventObj = (JSONObject) eventsArray.get(i);
                String EventDateTime = eventObj.getString(ConstantsKeys.EventDateTime);

                DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                long hourDiff = Constants.getDateTimeDuration(eventTime, Globally.GetCurrentUTCDateTime()).getStandardHours();
                if (hourDiff <= 24) {

                    String DetectionDataEventCode = eventObj.getString(ConstantsKeys.DetectionDataEventCode);
                    int lastEventMinutes = 0;

                    if (DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic)) {

                        if (eventObj.has(ConstantsKeys.TotalMinutes)) {
                            lastEventMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
                        }

                        String lastEngineHours = eventObj.getString(ConstantsKeys.EngineHours);
                        if (lastEngineHours.equals(EngineHour) && lastEventMinutes == engineHrDiffInMin) {
                            // ignore in this case to add same dia event time again

                        } else {

                            String StartOdometer = eventObj.getString(ConstantsKeys.StartOdometer);
                            String ClearOdometer = eventObj.getString(ConstantsKeys.ClearOdometer);
                            if (!StartOdometer.equals("null") && StartOdometer.length() > 1 &&
                                    !ClearOdometer.equals("null") && ClearOdometer.length() > 1) {
                                try {
                                    double OdometerDiff = Double.parseDouble(ClearOdometer) - Double.parseDouble(StartOdometer);
                                    if (OdometerDiff > 1) {
                                        totalMin = totalMin + lastEventMinutes;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                }else{
                    break;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return totalMin;
    }



    // get last 24 hor occurred missing dia event Status
    public boolean is24HrOldMissingEvent(DBHelper dbHelper) {

        boolean is24HrOldMissingEvent = false;
        try {
            JSONArray array = getMalDiaDurationArray(dbHelper);
            DateTime currentDateTime = Globally.GetCurrentUTCDateTime();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                String DetectionDataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);
                String LocationType = obj.getString(ConstantsKeys.LocationType);
                //  String CurrentStatus = eventObj.getString(ConstantsKeys.CurrentStatus);

                boolean IsClearEvent = true;
                if(obj.has(ConstantsKeys.IsClearEvent)) {
                    IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                }

                if(DetectionDataEventCode.equals(Constants.MissingDataDiagnostic) &&
                        LocationType.length() == 0 && !IsClearEvent){

                    DateTime EventDateTime = Globally.getDateTimeObj(obj.getString(ConstantsKeys.EventDateTime), false);

                    long hourDiff = Constants.getDateTimeDuration(EventDateTime, currentDateTime).getStandardHours();

                    if(hourDiff >= 24) {
                        is24HrOldMissingEvent = true;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return is24HrOldMissingEvent;

    }


    // update Malfunction/Diagnostic enable/disable Status
    public void updateMalfDiaStatusForEnable(String DriverId, Globally global, Constants constants, DBHelper dbHelper, Context context){

        try {
            JSONArray array = getMalDiaDurationArray(dbHelper);
            int offset = Math.abs((int) global.GetDriverTimeZoneOffSet(context));

            // reset status before checking updated status in loop
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

                String EventEndDateTime = "";

                if(DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction) ||
                        DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic)) {

                    if (obj.has(ConstantsKeys.EventEndDateTime)) {
                        EventEndDateTime = obj.getString(ConstantsKeys.EventEndDateTime);
                        if (EventEndDateTime.length() > 10) {
                            //  DateTime eventEndUtcDate = Globally.getDateTimeObj(EventEndDateTime, false);
                            //EventEndDateTime = eventEndUtcDate.minusHours(offset).toString();
                        } else {
                            int TotalMinutes = 0;
                            if(obj.has(ConstantsKeys.TotalMinutes)){
                                TotalMinutes = obj.getInt(ConstantsKeys.TotalMinutes);
                            }
                            EventEndDateTime = eventUtcDate.plusMinutes(TotalMinutes).toString();
                        }
                    }
                }


                String EventDriverId = obj.getString(ConstantsKeys.DriverId);

                if(DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction) ||
                        DetectionDataEventCode.equals(Constants.EngineSyncMalfunctionEvent) ||
                        DetectionDataEventCode.equals(Constants.PositionComplianceMalfunction) ||
                        DetectionDataEventCode.equals(Constants.DataRecordingComplianceMalfunction) ||
                        DetectionDataEventCode.equals(Constants.DataTransferMalfunction) ||
                        DetectionDataEventCode.equals(Constants.TimingComplianceMalfunction) ){
                    UpdateStatus(IsClearEvent, DetectionDataEventCode, EventDateTime, EventEndDateTime, driverZoneDate, constants, context);
                }else {
                    if(DriverId.equals("0")){
                        UpdateStatus(IsClearEvent, DetectionDataEventCode, EventDateTime, EventEndDateTime, driverZoneDate, constants, context);
                    }else {
                        if (DriverId.equals(EventDriverId) || EventDriverId.equals("0")) {
                            UpdateStatus(IsClearEvent, DetectionDataEventCode, EventDateTime, EventEndDateTime, driverZoneDate, constants, context);
                        }
                    }
                }



            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public boolean getCoDriverEngSyncDiaStatus(DBHelper dbHelper, String CoDriverId ){
        boolean isEngSyncDia = false;
        try{
            JSONArray array = getMalDiaDurationArray(dbHelper);
            for(int i = 0 ; i< array.length(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                String DetectionDataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);
               // String EventDateTime = obj.getString(ConstantsKeys.EventDateTime);
                String EventDriverId = obj.getString(ConstantsKeys.DriverId);

                if(EventDriverId.equals(CoDriverId)) {
                    boolean IsClearEvent = false;
                    if(obj.has(ConstantsKeys.IsClearEvent)) {
                        IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                    }

                   /* DateTime currentDateTime = Globally.GetCurrentUTCDateTime();
                    DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                    long minDiff = Constants.getDateTimeDuration(eventTime, currentDateTime).getStandardMinutes();
                    if (minDiff < Constants.TotalMinInADay) {*/

                        if (DetectionDataEventCode.equals(Constants.EngineSyncDiagnosticEvent) && !IsClearEvent) {
                            isEngSyncDia = true;
                            break;
                        }
                  //  }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isEngSyncDia;

    }


    private void UpdateStatus(boolean IsClearEvent, String DetectionDataEventCode, String EventDateTime,
                              String EventEndDateTime, DateTime driverZoneDate, Constants constants, Context context){
        if(!IsClearEvent) {
            if (DetectionDataEventCode.equals(Constants.MissingDataDiagnostic)) {
                if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.MissingDataDiag, context)) {   // check missing data dia permission
                    constants.saveDiagnstcStatus(context, true);
                    SharedPref.saveMissingDiaStatus(true, context);
                }
            } else if (DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerDataDiag, context)) {   // check power dia permission
                    SharedPref.savePowerMalfunctionOccurStatus(
                            SharedPref.isPowerMalfunctionOccurred(context),
                            true, EventEndDateTime, context);
                    constants.saveDiagnstcStatus(context, true);
                }

            } else if (DetectionDataEventCode.equals(Constants.EngineSyncDiagnosticEvent) ) {
                if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncDiag, context)) {    // check eng sync dia permission
                    SharedPref.saveEngSyncDiagnstcStatus(true, context);
                    constants.saveDiagnstcStatus(context, true);
                }

            } else if (DetectionDataEventCode.equals(Constants.PositionComplianceMalfunction)) {
                if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PostioningComplMal, context)) {   // check position mal permission
                    SharedPref.saveLocMalfunctionOccurStatus(true, driverZoneDate.toString(), EventDateTime, context);
                    constants.saveMalfncnStatus(context, true);
                }

            } else if (DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction)) {
                if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.PowerComplianceMal, context)) {   // check power mal permission
                    SharedPref.savePowerMalfunctionOccurStatus(true,
                            SharedPref.isPowerDiagnosticOccurred(context),
                            EventEndDateTime, context);
                    constants.saveMalfncnStatus(context, true);
                }

            } else if (DetectionDataEventCode.equals(Constants.EngineSyncMalfunctionEvent)) {
                if(SharedPref.GetParticularMalDiaStatus(ConstantsKeys.EnginSyncMal, context)) {     // check eng sync mal permission
                    SharedPref.saveEngSyncMalfunctionStatus(true, context);
                    constants.saveMalfncnStatus(context, true);
                }
            }else if(DetectionDataEventCode.equals(Constants.UnIdentifiedDrivingDiagnostic)){
                if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.UnidentifiedDiag, context)) {      // check unidentified dia permission
                    constants.saveDiagnstcStatus(context, true);
                }
            } else if (DetectionDataEventCode.equals(Constants.DataRecordingComplianceMalfunction)) {
                if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.DataRecComMal, context)) {     // check data rec mal permission
                    constants.saveMalfncnStatus(context, true);
                }
            } else if (DetectionDataEventCode.equals(Constants.DataTransferDiagnostic)) {
                if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.DataTransferDiag, context)) {      // check Data Transfer dia permission
                    constants.saveDiagnstcStatus(context, true);
                }
            } else if (DetectionDataEventCode.equals(Constants.DataTransferMalfunction)) {
                if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.DataTransferComplMal, context)) {      // check Data Transfer Mal permission
                    constants.saveMalfncnStatus(context, true);
                }
            }else if (DetectionDataEventCode.equals(Constants.TimingComplianceMalfunction)) {
                if(SharedPref.GetOtherMalDiaStatus(ConstantsKeys.TimingCompMal, context)) {      // check Time compliance Mal permission
                    SharedPref.saveTimingMalfunctionStatus(true, EventDateTime, context);
                    constants.saveMalfncnStatus(context, true);
                }
            }
        }

    }


    private JSONObject getJsonObjForClearEvent(String DriverId, String EventDateTime, String DataEventCode, boolean IsClearEvent,
                                               JSONObject eventObj, boolean isAlreadyCleared, Context context){
        JSONObject clearObj = new JSONObject();

        try{
            DateTime EventDate = Globally.getDateTimeObj(EventDateTime, false);
            DateTime currentTime = Globally.GetCurrentUTCDateTime();
            int minDiff = Constants.getMinDiff(EventDate, currentTime);

            clearObj.put(ConstantsKeys.DriverId, DriverId);
            clearObj.put(ConstantsKeys.CompanyId, DriverConst.GetDriverDetails(DriverConst.CompanyId, context));
            clearObj.put(ConstantsKeys.UnitNo, SharedPref.getTruckNumber(context));
            clearObj.put(ConstantsKeys.EventDateTime, EventDateTime);
            clearObj.put(ConstantsKeys.DetectionDataEventCode, DataEventCode);
            clearObj.put(ConstantsKeys.DiagnosticType, DataEventCode);
            clearObj.put(ConstantsKeys.VIN, SharedPref.getVINNumber(context));

            int TotalMinutes = -1;
            if(eventObj.has(ConstantsKeys.TotalMinutes)){
                TotalMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
            }

            String EventEndDateTime = "", ClearEventDateTime = "", ClearEngineHours = "", ClearOdometer = "";

            if(isAlreadyCleared){   // some times multiple events occurred with same event thats why we are clearing wth same time other events
                if(eventObj.has(ConstantsKeys.ClearEventDateTime)){
                    ClearEventDateTime = eventObj.getString(ConstantsKeys.ClearEventDateTime);
                }

                if(eventObj.has(ConstantsKeys.EventEndDateTime)){
                    EventEndDateTime = eventObj.getString(ConstantsKeys.EventEndDateTime);
                }

                if(ClearEventDateTime.length() > 10 && EventEndDateTime.length() > 10) {
                    clearObj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
                    clearObj.put(ConstantsKeys.ClearEventDateTime, Globally.formatDatePatternMilli(ClearEventDateTime) ); //ClearEventDateTime
                    clearObj.put(ConstantsKeys.EventEndDateTime, Globally.formatDatePatternMilli(EventEndDateTime));    //EventEndDateTime
                }else{
                    clearObj.put(ConstantsKeys.ClearEventDateTime, Globally.formatDatePatternMilli(Globally.GetCurrentUTCTimeFormat()));
                    clearObj.put(ConstantsKeys.EventEndDateTime, Globally.formatDatePatternMilli(Globally.GetCurrentUTCTimeFormat()));

                    if(!DataEventCode.equals(Constants.PowerComplianceDiagnostic) &&
                            !DataEventCode.equals(Constants.PowerComplianceMalfunction)){
                        clearObj.put(ConstantsKeys.TotalMinutes, minDiff);
                    }else{
                        clearObj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
                    }


                }
                clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                clearObj.put(ConstantsKeys.ClearEngineHours, eventObj.getString(ConstantsKeys.ClearEngineHours));
                clearObj.put(ConstantsKeys.ClearOdometer, eventObj.getString(ConstantsKeys.ClearOdometer));

            }else{
                if(TotalMinutes > 0 && (!DataEventCode.equals(Constants.PowerComplianceMalfunction) &&
                        !DataEventCode.equals(Constants.EngineSyncMalfunctionEvent) &&
                        !DataEventCode.equals(Constants.PositionComplianceMalfunction) &&
                        !DataEventCode.equals(Constants.DataRecordingComplianceMalfunction) &&
                        !DataEventCode.equals(Constants.DataTransferMalfunction) )){

                    /*if(eventObj.has(ConstantsKeys.EventEndDateTime)){
                        EventEndDateTime = eventObj.getString(ConstantsKeys.EventEndDateTime);
                    }else if(eventObj.has(ConstantsKeys.ClearEventDateTime)){
                        EventEndDateTime = eventObj.getString(ConstantsKeys.ClearEventDateTime);
                    }*/

                    //if (!DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                    //if (EventEndDateTime.length() == 0) {
                    // DateTime endTime = Globally.getDateTimeObj(EventDateTime, false);
                    // EventEndDateTime = endTime.plusMinutes(TotalMinutes).toString();
                    //}
                    // }else{
                    EventEndDateTime = Globally.GetCurrentUTCTimeFormat();
                    TotalMinutes = minDiff;
                    clearObj.put(ConstantsKeys.EventEndDateTime, Globally.formatDatePatternMilli(EventEndDateTime));    //EventEndDateTime
                    //  }

                    if(eventObj.has(ConstantsKeys.ClearEngineHours) && eventObj.getString(ConstantsKeys.ClearEngineHours).length() > 1){
                        ClearEngineHours = eventObj.getString(ConstantsKeys.ClearEngineHours);
                    }else{
                        ClearEngineHours = Constants.get2DecimalEngHour(context);
                    }

                    if(eventObj.has(ConstantsKeys.ClearOdometer) && eventObj.getString(ConstantsKeys.ClearOdometer).length() > 0){
                        ClearOdometer = eventObj.getString(ConstantsKeys.ClearOdometer);
                    }else{
                        ClearOdometer = SharedPref.getObdOdometer(context);
                    }

                    clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                    clearObj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
                    clearObj.put(ConstantsKeys.ClearEventDateTime, Globally.formatDatePatternMilli(EventEndDateTime));
                    clearObj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
                    clearObj.put(ConstantsKeys.ClearOdometer, ClearOdometer);

                    if (DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                        if(eventObj.has(ConstantsKeys.EngineHours)) {
                            clearObj.put(ConstantsKeys.EngineHours, eventObj.getString(ConstantsKeys.EngineHours));
                        }

                    }else{
                        clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                        clearObj.put(ConstantsKeys.ClearEngineHours, Constants.get2DecimalEngHour(context));
                        clearObj.put(ConstantsKeys.ClearOdometer, SharedPref.getObdOdometer(context));
                    }
                }else {
                    if (DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                        if (eventObj.has(ConstantsKeys.EventEndDateTime)) {
                            EventEndDateTime = eventObj.getString(ConstantsKeys.EventEndDateTime);
                        }
                        if (EventEndDateTime.length() < 10) {
                            EventEndDateTime = Globally.GetCurrentUTCTimeFormat();
                        }

                        clearObj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
                        clearObj.put(ConstantsKeys.ClearEventDateTime, Globally.formatDatePatternMilli(EventEndDateTime));

                        if (eventObj.has(ConstantsKeys.ClearEngineHours) && eventObj.getString(ConstantsKeys.ClearEngineHours).length() > 1) {
                            ClearEngineHours = eventObj.getString(ConstantsKeys.ClearEngineHours);
                        }else{
                            ClearEngineHours = Constants.get2DecimalEngHour(context);
                        }

                        if (eventObj.has(ConstantsKeys.ClearOdometer) && eventObj.getString(ConstantsKeys.ClearOdometer).length() > 0) {
                            ClearOdometer = eventObj.getString(ConstantsKeys.ClearOdometer);
                        }else{
                            ClearOdometer = SharedPref.getObdOdometer(context);
                        }

                        clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                        clearObj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
                        clearObj.put(ConstantsKeys.ClearOdometer, ClearOdometer);


                    } else {

                        clearObj.put(ConstantsKeys.EventEndDateTime, Globally.formatDatePatternMilli(currentTime.toString()) );
                        clearObj.put(ConstantsKeys.TotalMinutes, minDiff);
                        clearObj.put(ConstantsKeys.ClearEventDateTime, currentTime.toString());

                        clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                        clearObj.put(ConstantsKeys.ClearEngineHours, Constants.get2DecimalEngHour(context));    //SharedPref.getObdEngineHours(context)
                        clearObj.put(ConstantsKeys.ClearOdometer, SharedPref.getObdOdometer(context));

                        if(DataEventCode.equals(Constants.PositionComplianceMalfunction) ) {
                            SharedPref.saveLocDiagnosticStatus(false, "", "", context);
                        }

                    }

                }

            }
            clearObj.put(ConstantsKeys.StartOdometer, eventObj.getString(ConstantsKeys.StartOdometer));

            String LocationType = "";
            if(eventObj.has(ConstantsKeys.LocationType)){
                LocationType = eventObj.getString(ConstantsKeys.LocationType);
            }
            clearObj.put(ConstantsKeys.LocationType, LocationType);

            String CurrentStatus = "";
            if(eventObj.has(ConstantsKeys.CurrentStatus)){
                CurrentStatus = eventObj.getString(ConstantsKeys.CurrentStatus);
            }
            clearObj.put(ConstantsKeys.CurrentStatus, CurrentStatus);


            /*if(!DataEventCode.equals(Constants.PowerComplianceDiagnostic) && !DataEventCode.equals(Constants.PowerComplianceMalfunction) ) {
                if (clearObj.has(ConstantsKeys.ClearEventDateTime)) {
                    String clearDateStr = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.ClearEventDateTime));
                    clearObj.put(ConstantsKeys.ClearEventDateTime, clearDateStr);
                }
            }*/

            if(DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                if (clearObj.has(ConstantsKeys.EventEndDateTime)) {
                    String endDateTime = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.EventEndDateTime));
                    clearObj.put(ConstantsKeys.EventEndDateTime, endDateTime);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return clearObj;
    }


    public boolean isMalfunctionEvent(String EventCode) {
        if (EventCode.equals(Constants.PowerComplianceMalfunction) ||
                EventCode.equals(Constants.EngineSyncMalfunctionEvent) ||
                EventCode.equals(Constants.DataTransferMalfunction) ||
                EventCode.equals(Constants.PositionComplianceMalfunction) ||
                EventCode.equals(Constants.DataRecordingComplianceMalfunction) ||
                EventCode.equals(Constants.TimingComplianceMalfunction)) {
            return true;
        } else {
            return false;
        }
    }


    public JSONArray updateAutoClearEvent(DBHelper dbHelper, String DriverId, String EventCode, boolean isClear,
                                          boolean isUpdate, Context context){

        boolean isAlreadyCleared = false;

        JSONArray eventClearArray = new JSONArray();
        try {
            DateTime currentDateTime = Globally.GetCurrentUTCDateTime();
            JSONArray eventArray = getMalDiaDurationArray(dbHelper);
            for(int i = eventArray.length()-1 ; i >= 0  ; i--) {
                JSONObject obj = (JSONObject) eventArray.get(i);
                String EventDateTime = obj.getString(ConstantsKeys.EventDateTime);
                String DataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);
                String EventDriverId = obj.getString(ConstantsKeys.DriverId);

                int dayDiff = Constants.getDayDiff(EventDateTime, Globally.GetCurrentDateTime(new Globally(), context));
                boolean isMalfunctionForClear = isMalfunctionEvent(EventCode) && dayDiff != 0;

                if(isMalfunctionForClear || EventDriverId.equals(DriverId) || EventDriverId.equals("0")) {
                    boolean IsClearEvent = false;
                    if (obj.has(ConstantsKeys.IsClearEvent)) {
                        IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                    }

                    if (EventCode.length() > 0) { // means clear selected Event only

                        if (!IsClearEvent) {

                            if (EventCode.equals(DataEventCode)) {
                                if (Constants.isDiagnosticEvent(EventCode)) {

                                    if(EventCode.equals(Constants.MissingDataDiagnostic) ){
                                        String LocationType = obj.getString(ConstantsKeys.LocationType);
                                        String CurrentStatus = obj.getString(ConstantsKeys.CurrentStatus);
                                        //  CurrentStatus.equals("Off Duty") || CurrentStatus.equals("Sleeper") || CurrentStatus.equals("On Duty") || CurrentStatus.equals("Driving") || CurrentStatus.equals("Personal")

                                        if(LocationType.length() == 0 || CurrentStatus.equals("Logout Event") || CurrentStatus.equals("Login Event") ||
                                                CurrentStatus.equals("Logout Event") || CurrentStatus.equals("Operating Zone Change") ||
                                                CurrentStatus.equals("Cycle Change") || CurrentStatus.equals("OffDuty Deferral")){

                                            //  DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                                            // long hourDiff = Constants.getDateTimeDuration(eventTime, currentDateTime).getStandardHours();
                                            // if (hourDiff >= 24) {
                                            JSONObject clearObj = getJsonObjForClearEvent(DriverId, EventDateTime, DataEventCode, isClear,
                                                    obj, isAlreadyCleared, context);

                                            if(clearObj.has(ConstantsKeys.ClearEventDateTime)){
                                                String clearDateStr = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.ClearEventDateTime) );
                                                clearObj.put(ConstantsKeys.ClearEventDateTime, clearDateStr);
                                            }

                                            if(DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                                                if (clearObj.has(ConstantsKeys.EventEndDateTime)) {
                                                    String endDateTime = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.EventEndDateTime));
                                                    clearObj.put(ConstantsKeys.EventEndDateTime, endDateTime);
                                                }
                                            }

                                            if (clearObj.getInt(ConstantsKeys.TotalMinutes) >= 0) {
                                                eventClearArray.put(clearObj);
                                            }
                                            eventArray.put(i, clearObj);
                                            // isAlreadyCleared = true;
                                            // }
                                        }
                                    }else{
                                        JSONObject clearObj = getJsonObjForClearEvent(DriverId, EventDateTime, DataEventCode, isClear,
                                                obj, isAlreadyCleared, context);

                                        if(clearObj.has(ConstantsKeys.ClearEventDateTime)){
                                            String clearDateStr = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.ClearEventDateTime) );
                                            clearObj.put(ConstantsKeys.ClearEventDateTime, clearDateStr);
                                        }

                                        if(DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                                            if (clearObj.has(ConstantsKeys.EventEndDateTime)) {
                                                String endDateTime = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.EventEndDateTime));
                                                clearObj.put(ConstantsKeys.EventEndDateTime, endDateTime);
                                            }
                                        }

                                        if (clearObj.getInt(ConstantsKeys.TotalMinutes) >= 0) {
                                            eventClearArray.put(clearObj);
                                        }



                                        eventArray.put(i, clearObj);
                                        isAlreadyCleared = true;

                                    }

                                } else {
                                    DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                                    long hourDiff = Constants.getDateTimeDuration(eventTime, currentDateTime).getStandardHours();
                                    if (hourDiff >= 24) {   // || EventCode.equals(Constants.DataRecordingComplianceMalfunction)
                                        JSONObject clearObj = getJsonObjForClearEvent(DriverId, EventDateTime, DataEventCode, isClear,
                                                obj, isAlreadyCleared, context);

                                        if(clearObj.has(ConstantsKeys.ClearEventDateTime)){
                                            String clearDateStr = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.ClearEventDateTime) );
                                            clearObj.put(ConstantsKeys.ClearEventDateTime, clearDateStr);
                                        }

                                        if(DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                                            if (clearObj.has(ConstantsKeys.EventEndDateTime)) {
                                                String endDateTime = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.EventEndDateTime));
                                                clearObj.put(ConstantsKeys.EventEndDateTime, endDateTime);
                                            }
                                        }


                                        if (clearObj.getInt(ConstantsKeys.TotalMinutes) >= 0) {
                                            eventClearArray.put(clearObj);
                                        }
                                        eventArray.put(i, clearObj);

                                        isAlreadyCleared = true;
                                    }
                                }
                            }

                        }
                    } else {  // clear all events those are 24 hour old
                        if (IsClearEvent) {
                            eventArray.remove(i);
                        } else {
                            DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                            long hourDiff = Constants.getDateTimeDuration(eventTime, currentDateTime).getStandardHours();
                            if (hourDiff >= 24) {
                                JSONObject clearObj = getJsonObjForClearEvent(DriverId, EventDateTime, DataEventCode,
                                        isClear, obj, isAlreadyCleared, context);

                                if(clearObj.has(ConstantsKeys.ClearEventDateTime)){
                                    String clearDateStr = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.ClearEventDateTime) );
                                    clearObj.put(ConstantsKeys.ClearEventDateTime, clearDateStr);
                                }

                                if(DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                                    if (clearObj.has(ConstantsKeys.EventEndDateTime)) {
                                        String endDateTime = Globally.formatDatePatternMilli(clearObj.getString(ConstantsKeys.EventEndDateTime));
                                        clearObj.put(ConstantsKeys.EventEndDateTime, endDateTime);
                                    }
                                }


                                if (clearObj.getInt(ConstantsKeys.TotalMinutes) >= 0) {
                                    eventClearArray.put(clearObj);
                                }
                                eventArray.put(i, clearObj);
                            }
                        }
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


    public JSONArray addOccurEventUploadStatus(JSONArray eventArray, DBHelper dbHelper){

        JSONArray eventClearArray = new JSONArray();
        try {
            for(int i = 0 ; i < eventArray.length() ; i++){
                JSONObject obj = (JSONObject) eventArray.get(i);
                obj.put(ConstantsKeys.IsOccurEventAlreadyUploaded, true);
                eventClearArray.put(obj);
            }

            // save data in unposted event array if api failed or internet issue. Clearing api response.
            if(eventClearArray.length() > 0) {
                MalfnDiagnstcLogHelper(dbHelper, eventClearArray);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return eventClearArray;
    }



    public boolean IsOccurEventAlreadyUploaded(JSONArray eventArray){

        boolean IsOccurEventAlreadyUploaded = false;
        try {
            for(int i = 0 ; i < eventArray.length() ; i++){
                JSONObject obj = (JSONObject) eventArray.get(i);

                if(obj.has(ConstantsKeys.IsOccurEventAlreadyUploaded) && obj.getBoolean(ConstantsKeys.IsOccurEventAlreadyUploaded)){
                    IsOccurEventAlreadyUploaded = true;
                    break;

                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return IsOccurEventAlreadyUploaded;
    }


    public boolean isStorageMalfunction(DBHelper dbHelper, Context context) {

        boolean isStorageMalfunction = false;

        try {
            JSONArray array = getMalDiaDurationArray(dbHelper);

            for(int i = array.length()-1 ; i >=0 ; i--) {
                JSONObject obj = (JSONObject) array.get(i);
                String DetectionDataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);

                if (DetectionDataEventCode.equals(Constants.DataRecordingComplianceMalfunction)) {

                    if (obj.has(ConstantsKeys.IsClearEvent) && !obj.getBoolean(ConstantsKeys.IsClearEvent)) {
                        isStorageMalfunction = true;
                        break;
                    }
                }
            }

            SharedPref.SetStorageMalfunctionStatus(isStorageMalfunction, context);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return isStorageMalfunction;

    }


    public boolean isStorageMal24HrOldToClear(DBHelper dbHelper) {

        boolean isStorageMal24HrOldToClear = false;

        try {
            JSONArray array = getMalDiaDurationArray(dbHelper);

            for(int i = array.length()-1 ; i >=0 ; i--) {
                JSONObject obj = (JSONObject) array.get(i);
                String DetectionDataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);

                if (DetectionDataEventCode.equals(Constants.DataRecordingComplianceMalfunction)) {
                    boolean isCleared = false;
                    if(obj.has(ConstantsKeys.IsClearEvent)){
                        isCleared = obj.getBoolean(ConstantsKeys.IsClearEvent);
                    }

                    DateTime selectedDateTime = Globally.getDateTimeObj(obj.getString(ConstantsKeys.EventDateTime), false);
                    long hourDiff = Constants.getDateTimeDuration(selectedDateTime, Globally.GetCurrentUTCDateTime()).getStandardHours();
                    if(hourDiff >= 24 && !isCleared ){   //
                        isStorageMal24HrOldToClear = true;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isStorageMal24HrOldToClear;
    }



    public String getEventOccurredActualDriverid(DBHelper dbHelper, String EventCode){

        String DriverId = "";
        try {
            JSONArray eventArray = getMalDiaDurationArray(dbHelper);
            for (int i = eventArray.length()-1; i >= 0; i--) {
                JSONObject obj = (JSONObject) eventArray.get(i);

                String DataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);
                boolean isCleared = false;
                if(obj.has(ConstantsKeys.IsClearEvent)){
                    isCleared = obj.getBoolean(ConstantsKeys.IsClearEvent);
                }
                if (EventCode.equals(DataEventCode) && isCleared == false) {
                    DriverId = obj.getString(ConstantsKeys.DriverId);
                    break;
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return DriverId;
    }



    // update duration array after clear
    public boolean HasEngSyncEventForClear(String ClearEventCode, DBHelper dbHelper, Constants constants,
                                           Globally global, Context context){
        boolean hasEventForClear = false;
        try {
            String lastCalledTime = SharedPref.getEngSyncClearEventCallTime(context);
            if(lastCalledTime.length() > 10){
                int secDiff = constants.secDiffMalfunction(lastCalledTime, global, context);
                if (secDiff > 30) {
                    SharedPref.setEngSyncClearEventCallTime(Globally.GetDriverCurrentDateTime(new Globally(), context), context);

                    JSONArray eventArray = getMalDiaDurationArray(dbHelper);
                    for (int i = 0; i < eventArray.length(); i++) {
                        JSONObject obj = (JSONObject) eventArray.get(i);
                        String DataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);
                        boolean isCleared = false;
                        if(obj.has(ConstantsKeys.IsClearEvent) && !obj.getString(ConstantsKeys.IsClearEvent).equals("null")){
                            isCleared = obj.getBoolean(ConstantsKeys.IsClearEvent);
                        }
                        if (ClearEventCode.equals(DataEventCode) && isCleared == false) {
                            hasEventForClear = true;
                            break;
                        }

                    }
                }
            }else{
                SharedPref.setEngSyncClearEventCallTime(Globally.GetDriverCurrentDateTime(new Globally(), context), context);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return hasEventForClear;
    }




    // get last switched driver Id status for clear
    public boolean IsLastSwitchedDriverEngSyncEvent(String lastSwitchedDriverId, String ClearEventCode, DBHelper dbHelper){
        boolean hasEventForClear = false;
        try {

            JSONArray eventArray = getMalDiaDurationArray(dbHelper);
            for (int i = eventArray.length()-1; i >= 0; i--) {
                JSONObject obj = (JSONObject) eventArray.get(i);
                String DriverId = obj.getString(ConstantsKeys.DriverId);
                String DataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);
                boolean isCleared = false;
                if(obj.has(ConstantsKeys.IsClearEvent)){
                    isCleared = obj.getBoolean(ConstantsKeys.IsClearEvent);
                }
                if (ClearEventCode.equals(DataEventCode) && DriverId.equals(lastSwitchedDriverId) && isCleared == false) {
                    hasEventForClear = true;
                    break;
                }

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
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.POSITION_MAl_DIA_EVENT_LIST));
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

    public JSONArray AddNewItemInPositionArray(String  DriverId, String EventCode, String engHour, String odometer,
                                               String LocationType, String CurrentStatus, String VIN,
                                               DBHelper dbHelper, Context context){    //, String CurrentStatus

        JSONObject newObj = getNewMalDiaDurationObj( DriverId, Globally.GetDriverCurrentDateTime(new Globally(), context), "",
                EventCode, 0, false, engHour, odometer, odometer, engHour,
                LocationType, CurrentStatus, VIN); // CurrentStatus
        JSONArray array = getPositioningMalDiaArray(dbHelper);
        array.put(newObj);

        return array;
    }


    public void clearEventsMoreThen1Day(DBHelper dbHelper, Context context){
        try{
            DateTime currentTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), new Globally(), context);
            JSONArray updatedTimeArray = new JSONArray();
            JSONArray lastOccEventTimeArray = getPositioningMalDiaArray(dbHelper);

            // add only last 24 hour events in array
            for (int i = 0; i < lastOccEventTimeArray.length() ; i++) {
                JSONObject eventObj = (JSONObject) lastOccEventTimeArray.get(i);
                DateTime selectedDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.EventDateTime), false);
                long hourDiff = Constants.getDateTimeDuration(selectedDateTime, currentTime).getStandardHours();
                if(hourDiff <= 24){
                    updatedTimeArray.put(eventObj);
                }else{
                    boolean IsClearEvent = false;
                    if(eventObj.has(ConstantsKeys.IsClearEvent)){
                        IsClearEvent = eventObj.getBoolean(ConstantsKeys.IsClearEvent);
                    }
                    if(IsClearEvent == false) {
                        updatedTimeArray.put(eventObj);
                      /*  String EventEndDateTime = eventObj.getString(ConstantsKeys.EventEndDateTime);
                        if (EventEndDateTime.length() == 0) {

                        }*/
                    }
                }
            }

            PositioningMalDiaHelper(dbHelper, updatedTimeArray);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void updateTimeOnLocationReceived(DBHelper dbHelper, Context context){    //String EventCode,
        try{
            DateTime currentTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), new Globally(), context);
            JSONArray lastOccEventTimeArray = getPositioningMalDiaArray(dbHelper);

            for (int i = lastOccEventTimeArray.length()-1; i >= 0  ; i--) {
                JSONObject obj = (JSONObject) lastOccEventTimeArray.get(i);

                String EventEndDateTime = obj.getString(ConstantsKeys.EventEndDateTime);
                String DetectionDataEventCode = obj.getString(ConstantsKeys.DetectionDataEventCode);

                if(EventEndDateTime.length() == 0 ){    //&& EventCode.equals(DetectionDataEventCode)
                    DateTime selectedDateTime = Globally.getDateTimeObj(obj.getString(ConstantsKeys.EventDateTime), false);
                    long minDiff = Constants.getDateTimeDuration(selectedDateTime, currentTime).getStandardMinutes();

                    String ClearEngineHours = "", ClearOdometer = "";

                    if(obj.has(ConstantsKeys.ClearEngineHours)){
                        ClearEngineHours = obj.getString(ConstantsKeys.ClearEngineHours);
                    }
                    if(obj.has(ConstantsKeys.ClearOdometer)){
                        ClearOdometer = obj.getString(ConstantsKeys.ClearOdometer);
                    }

                    JSONObject updatedObj = new JSONObject();
                    updatedObj.put(ConstantsKeys.EventDateTime, obj.getString(ConstantsKeys.EventDateTime));
                    updatedObj.put(ConstantsKeys.EventEndDateTime, currentTime.toString());
                    updatedObj.put(ConstantsKeys.DetectionDataEventCode, DetectionDataEventCode);
                    updatedObj.put(ConstantsKeys.TotalMinutes, minDiff);
                    updatedObj.put(ConstantsKeys.IsClearEvent, true);
                    updatedObj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
                    updatedObj.put(ConstantsKeys.ClearOdometer, ClearOdometer);
                    updatedObj.put(ConstantsKeys.StartOdometer, obj.getString(ConstantsKeys.StartOdometer));

                    String LocationType = "";
                    if(obj.has(ConstantsKeys.LocationType)){
                        LocationType = obj.getString(ConstantsKeys.LocationType);
                    }
                    updatedObj.put(ConstantsKeys.LocationType, LocationType);


                    String CurrentStatus = "";
                    if(obj.has(ConstantsKeys.CurrentStatus)){
                        CurrentStatus = obj.getString(ConstantsKeys.CurrentStatus);
                    }
                    updatedObj.put(ConstantsKeys.CurrentStatus, CurrentStatus);


                    lastOccEventTimeArray.put(i, updatedObj);

                    break;
                }

            }

            PositioningMalDiaHelper(dbHelper, lastOccEventTimeArray);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public boolean isLastRecordCleared(DBHelper dbHelper){
        boolean IsClearEvent = true;

        try{
            JSONArray lastOccEventTimeArray = getPositioningMalDiaArray(dbHelper);

            for (int i = lastOccEventTimeArray.length()-1; i >= 0  ; i--) {
                JSONObject obj = (JSONObject) lastOccEventTimeArray.get(i);
                if(obj.has(ConstantsKeys.IsClearEvent)){
                    IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                }

                break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return IsClearEvent;
    }


    public double getLast24HourLocDiaEventsInMin(DBHelper dbHelper, Context context){
        double totalMin = 0;
        try{
            JSONArray eventsArray = getPositioningMalDiaArray(dbHelper);
            DateTime CurrentDateTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), new Globally(), context);

            for (int i = eventsArray.length()-1 ; i >= 0 ; i--) {
                JSONObject eventObj = (JSONObject) eventsArray.get(i);
                // String DetectionDataEventCode = eventObj.getString(ConstantsKeys.DetectionDataEventCode);

                DateTime EventDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.EventDateTime), false);

                long hourDiff = Constants.getDateTimeDuration(EventDateTime, CurrentDateTime).getStandardHours();

                double lastEventMinutes = 0;
                if(hourDiff < 24) {
                    if (eventObj.has(ConstantsKeys.TotalMinutes)) {
                        lastEventMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
                    }
                }else{
                    break;
                }

                totalMin = totalMin + lastEventMinutes;

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return totalMin;
    }



    public String clearMalAfter24Hours(DBHelper dbHelper, Context context){
        String clearedEventCode = "";
        try{
            DateTime currentTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), new Globally(), context);

            if(SharedPref.isMalfunctionOccur(context) ) {

                String clearEventLastCallTime = SharedPref.getClearMalCallTime(context);
                if (clearEventLastCallTime.length() == 0) {
                    SharedPref.setClearMalCallTime(Globally.GetDriverCurrentDateTime(new Globally(), context), context);
                }
                DateTime lastCallDateTime = Globally.getDateTimeObj(clearEventLastCallTime, false);

                // Checking after 1 min
                long minDiff = Constants.getDateTimeDuration(lastCallDateTime, currentTime).getStandardMinutes();
                if (minDiff > 0) {
                    // update call time
                    SharedPref.setClearMalCallTime(Globally.GetDriverCurrentDateTime(new Globally(), context), context);

                    JSONArray getDurationArray = getMalDiaDurationArray(dbHelper);
                    for (int i = 0; i < getDurationArray.length() ; i++) {
                        JSONObject eventObj = (JSONObject) getDurationArray.get(i);
                        DateTime selectedDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.EventDateTime), false);
                        DateTime currentUtcDate = Globally.GetCurrentUTCDateTime();
                        long hourDiff = Constants.getDateTimeDuration(selectedDateTime, currentUtcDate).getStandardHours();
                        // int dayDiff = Constants.getDayDiff(currentUtcDate.toString(), selectedDateTime.toString());

                        if( hourDiff >= 24){
                            String DetectionDataEventCode = eventObj.getString(ConstantsKeys.DetectionDataEventCode);
                            boolean isCleared = false;
                            if(eventObj.has(ConstantsKeys.IsClearEvent)){
                                isCleared = eventObj.getBoolean(ConstantsKeys.IsClearEvent);
                            }

                            if(!isCleared){
                                if(DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction)){
                                    clearedEventCode = DetectionDataEventCode;
                                    break;
                                }else if(DetectionDataEventCode.equals(Constants.EngineSyncMalfunctionEvent) ){
                                    clearedEventCode = DetectionDataEventCode;
                                    break;
                                }else if(DetectionDataEventCode.equals(Constants.PositionComplianceMalfunction)){
                                    clearedEventCode = DetectionDataEventCode;
                                    break;
                                }else if(DetectionDataEventCode.equals(Constants.DataRecordingComplianceMalfunction)){
                                    clearedEventCode = DetectionDataEventCode;
                                    break;
                                }
                            }

                        }
                    }
                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }

        return clearedEventCode;
    }




    // ===================================== UnIdentified Logout records events to save=====================================

    /*-------------------- GET LOGOUT UNIDENTIFIED SAVED Array -------------------- */
    public JSONArray getUnidentifiedLogoutArray(int companyId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();

        try {
            Cursor rs = dbHelper.getUnidentifiedLogoutRecordLog(companyId);

            if(rs != null && rs.getCount() > 0) {
                rs.moveToFirst();
                @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.UNIDENTIFIED_LOGOUT_EVENT_LIST));
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


    public void UnidentifiedLogoutRecordHelper( int companyId, DBHelper dbHelper, JSONArray driverLogArray){

        Cursor rs = dbHelper.getUnidentifiedLogoutRecordLog(companyId);

        try {
            if (rs != null & rs.getCount() > 0) {
                rs.moveToFirst();
                dbHelper.UpdateUnidentifiedLogoutRecordLog(companyId, driverLogArray);        // UPDATE DRIVER LOG
            } else {
                dbHelper.InsertUnidentifiedLogoutRecordLog(companyId, driverLogArray);      // INSERT DRIVER LOG
            }
            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public boolean isVehicleMotionChanged(boolean IsVehicleInMotion, int CompanyId, DBHelper dbHelper){

        boolean isVehicleMotionChanged = IsVehicleInMotion;
        try {
            JSONArray eventArray = getUnidentifiedLogoutArray(CompanyId, dbHelper);
            if(eventArray.length() > 0){
                JSONObject lastObj = (JSONObject) eventArray.get(eventArray.length() - 1);
                if(IsVehicleInMotion == lastObj.getBoolean(ConstantsKeys.IsVehicleInMotion)){
                    isVehicleMotionChanged = false;
                }else{
                    isVehicleMotionChanged = true;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return isVehicleMotionChanged;
    }


    public void saveVehicleMotionStatus(String StartOdometer, String startEngineHours, int CompanyId, String TruckID,
                                        String VIN, int vehicleSpeed, DBHelper dbHelper, Constants constants,
                                        DriverPermissionMethod driverPermissionMethod, Utils obdUtil, Context context){

        try{
            double TotalMinutes = 0;
            JSONArray eventsArray = getUnidentifiedLogoutArray(CompanyId, dbHelper);

            for (int i = 0; i < eventsArray.length() ; i++) {
                JSONObject eventObj = (JSONObject) eventsArray.get(i);

                double eventDuration = 0;

                String Truck = Constants.checkStringInJsonObj(eventObj, ConstantsKeys.TruckNumber);

                if(TruckID.equals(Truck)) {
                    DateTime StartDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.StartDateTime), false);
                    long minDiff = Constants.getDateTimeDuration(StartDateTime, Globally.GetCurrentUTCDateTime()).getStandardMinutes();   //Constants.getMinDiff(StartDateTime, Globally.GetCurrentJodaDateTime());
                    if (minDiff < Constants.TotalMinInADay) {
                        int itemTotalMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
                        if (i == eventsArray.length() - 1 && vehicleSpeed >= 8 && itemTotalMinutes == 0) {
                            DateTime EndDateTime = Globally.GetCurrentUTCDateTime();
                            eventDuration = Constants.getDateTimeDuration(StartDateTime, EndDateTime).getStandardMinutes();
                        } else {
                            eventDuration = itemTotalMinutes;
                        }

                        TotalMinutes = TotalMinutes + eventDuration;

                    }
                }

            }

            if(TotalMinutes >= Constants.UnidentifiedDiagnosticTime){
                JSONObject eventJsonObj = new JSONObject();
                eventJsonObj.put(ConstantsKeys.EventDateTime, Globally.GetCurrentUTCTimeFormat());
                eventJsonObj.put(ConstantsKeys.EventEndDateTime, "");
                eventJsonObj.put(ConstantsKeys.DiagnosticType, Constants.UnIdentifiedDrivingDiagnostic);
                eventJsonObj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
                eventJsonObj.put(ConstantsKeys.IsClearEvent, false);
                eventJsonObj.put(ConstantsKeys.StartOdometer, StartOdometer);
                eventJsonObj.put(ConstantsKeys.EngineHours, startEngineHours);
                eventJsonObj.put(ConstantsKeys.VIN, VIN);
                eventJsonObj.put(ConstantsKeys.CompanyId, CompanyId);
                eventJsonObj.put(ConstantsKeys.UnitNo, TruckID);

                // save in db
                // save Occurred event locally until not posted to server
                JSONArray malArray = getSavedMalDiagstcArray(dbHelper);
                malArray.put(eventJsonObj);
                MalfnDiagnstcLogHelper( dbHelper, malArray);

               /* JSONArray array = getMalDiaDurationArray(dbHelper);
                array.put(eventJsonObj);
                MalDiaDurationHelper(dbHelper, array);*/

                SharedPref.saveUnidentifiedEventStatus(true, Globally.GetCurrentUTCTimeFormat(), context);

                Globally.ShowLocalNotification(context,
                        context.getResources().getString(R.string.dia_event),
                        context.getResources().getString(R.string.unidentified_dia_occured_desc), 2095);


                constants.saveObdData("", context.getResources().getString(R.string.dia_event), eventsArray.toString(),
                        "-1","", "", "", "", "-1",
                        "-1", "", "", "",
                        "0", dbHelper, driverPermissionMethod, obdUtil, context);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void saveVehicleMotionChangeTime(String currentUtcDate, int speed, String TruckNumber,
                                            int CompanyId, DBHelper dbHelper, Context context){

        try{
            boolean isUpdate = false;
            JSONArray eventArray = getUnidentifiedLogoutArray(CompanyId, dbHelper);
            JSONObject eventObj = null;
            if(eventArray.length() > 0) {
                eventObj = (JSONObject) eventArray.get(eventArray.length() - 1);

                if (speed <= 0) {

                    DateTime EndDateTime = Globally.GetCurrentUTCDateTime();
                    DateTime StartDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.StartDateTime), false);
                    long minDiff = Constants.getDateTimeDuration(StartDateTime, EndDateTime).getStandardMinutes();

                    eventObj.put(ConstantsKeys.EndDateTime, EndDateTime.toString());
                    eventObj.put(ConstantsKeys.TotalMinutes, minDiff);
                    eventObj.put(ConstantsKeys.IsVehicleInMotion, false);

                    isUpdate = true;

                }else{
                    eventObj = new JSONObject();
                    eventObj.put(ConstantsKeys.StartDateTime, currentUtcDate);
                    eventObj.put(ConstantsKeys.EndDateTime, currentUtcDate);
                    eventObj.put(ConstantsKeys.TotalMinutes, 0);
                    eventObj.put(ConstantsKeys.IsVehicleInMotion, true);
                }
            }else{
                if(speed >= 8) {
                    eventObj = new JSONObject();
                    eventObj.put(ConstantsKeys.StartDateTime, currentUtcDate);
                    eventObj.put(ConstantsKeys.EndDateTime, currentUtcDate);
                    eventObj.put(ConstantsKeys.TotalMinutes, 0);
                    eventObj.put(ConstantsKeys.IsVehicleInMotion, true);
                }
            }

            eventObj.put(ConstantsKeys.TruckNumber, TruckNumber);

            if(eventObj != null){
                if(isUpdate){
                    eventArray.put(eventArray.length()-1, eventObj);
                }else{
                    eventArray.put(eventObj);
                }

                // save last 24 hours events only
                eventArray = getLast24HrRecordsOnly(eventArray, context);

                UnidentifiedLogoutRecordHelper(CompanyId, dbHelper, eventArray);

                Logger.LogDebug("MotionEventArray", ">>>MotionEventArray: " +eventArray);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public void removeEventAfterClaim(DateTime date, int CompanyId, String DutyStatus, DBHelper dbHelper){
        try {
            JSONArray eventArray = getUnidentifiedLogoutArray(CompanyId, dbHelper);
            String dateStr = date.toString();
            if(dateStr.length() >= 19) {
                dateStr = dateStr.substring(11, 19);
            }

            for(int i = 0; i < eventArray.length() ; i++){
                JSONObject obj = (JSONObject) eventArray.get(i);
                String StartDateTime = obj.getString(ConstantsKeys.StartDateTime);
                DateTime eventDate = Globally.getDateTimeObj(StartDateTime, false);

                if(StartDateTime.length() >= 19) {
                    StartDateTime = StartDateTime.substring(11, 19);
                }


                if(eventDate.equals(date) || (dateStr.equals(StartDateTime)&& DutyStatus.equals("DR")) ){
                    Logger.LogDebug("RemoveMotionEvent", ">>>RemoveMotionEventArray: " +eventArray);
                    eventArray.remove(i);
                    UnidentifiedLogoutRecordHelper(CompanyId, dbHelper, eventArray);
                    break;
                }

            }




        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public boolean isUnidenDriving(int CompanyId, DBHelper dbHelper){
        try{
            JSONArray array = getUnidentifiedLogoutArray(CompanyId, dbHelper);
            if(array.length() > 0){
                JSONObject obj = (JSONObject) array.get(array.length()-1);
                return obj.getBoolean(ConstantsKeys.IsVehicleInMotion);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private JSONArray getLast24HrRecordsOnly(JSONArray eventArray, Context context){

        JSONArray updatedTimeArray = new JSONArray();
        try{
            DateTime currentTime = Globally.GetDriverCurrentTime(Globally.GetCurrentUTCTimeFormat(), new Globally(), context);
            for (int i = 0; i < eventArray.length() ; i++) {

                JSONObject eventObj = (JSONObject) eventArray.get(i);
                DateTime selectedDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.StartDateTime), false);
                long hourDiff = Constants.getDateTimeDuration(selectedDateTime, currentTime).getStandardHours();
                if (hourDiff <= 24) {
                    updatedTimeArray.put(eventObj);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return updatedTimeArray;
    }



}
