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
    public JSONArray getSavedMalDiagstcArray( DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getMalfunctionDiagnosticLog(Globally.PROJECT_ID_INT);

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



    public boolean isUnPostedOfflineEvent(String EventType, DBHelper dbHelper){

        boolean isUnpostedEvent = false;
        try{
            JSONArray malArray = getSavedMalDiagstcArray( dbHelper);
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
    public boolean isUnPostAnyEvent( DBHelper dbHelper){
        boolean isUnpostedEvent = false;
        try{
            JSONArray malArray = getSavedMalDiagstcArray( dbHelper);
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
        JSONArray malArray = getSavedMalDiagstcArray(dbHelper);
        try {
            for(int i = malArray.length()-1 ; i >= 0; i-- ){
                JSONObject malfnDiagnstcObj = (JSONObject) malArray.get(i);
                if(malfnDiagnstcObj.getString(ConstantsKeys.DiagnosticType).equals(EventType) &&
                          malfnDiagnstcObj.getBoolean(ConstantsKeys.IsCleared) == false){

                    malfnDiagnstcObj.put(ConstantsKeys.IsCleared, true);
                    malfnDiagnstcObj.put(ConstantsKeys.ClearedTime, Globally.GetCurrentUTCTimeFormat());
                    malfnDiagnstcObj.put(ConstantsKeys.Remarks, Remarks);
                    malfnDiagnstcObj.put(ConstantsKeys.ClearedTimeEngineHours, SharedPref.getObdEngineHours(context));
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


    public JSONArray getEventsDateWise(String selectedDate, DBHelper dbHelper){
        JSONArray array = new JSONArray();
        try {
            JSONArray eventArray = getMalDiaDurationArray(dbHelper);
            for(int i = 0 ; i < eventArray.length() ; i++){
                JSONObject obj = (JSONObject) eventArray.get(i);
                String EventDateTime = obj.getString(ConstantsKeys.EventDateTime);
                if(EventDateTime.length() > 10){
                    EventDateTime = EventDateTime.substring(0, 10);
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
                                              String StartOdometer, String startEngineHours){
        JSONObject obj = new JSONObject();
        try{
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.EventDateTime, EventDateTime);
            obj.put(ConstantsKeys.EventEndDateTime, EventEndDateTime);
            obj.put(ConstantsKeys.DetectionDataEventCode, DetectionDataEventCode);
            obj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
            obj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
            obj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
            obj.put(ConstantsKeys.ClearOdometer, ClearOdometer);
            obj.put(ConstantsKeys.StartOdometer, StartOdometer);
            obj.put(ConstantsKeys.EngineHours, startEngineHours);

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


    public void addNewMalDiaEventInDurationArray(DBHelper dbHelper, String DriverId, String EventDateTime, String EventEndDateTime,
                                                 String DetectionDataEventCode, Constants constants, Context context){
        try{
            String ClearEngineHours = SharedPref.getObdEngineHours(context);
            String ClearOdometer = SharedPref.getObdOdometer(context);
            JSONArray array = getMalDiaDurationArray(dbHelper);
            JSONObject newItemObj;
            String lastSavedOdometer = SharedPref.GetTruckInfoOnIgnitionChange(Constants.OdometerMalDia, context);
            String lastSavedEngHr =  SharedPref.GetTruckInfoOnIgnitionChange(Constants.EngineHourMalDia, context);
            String currentOdometer = SharedPref.getObdOdometer(context);
            String currentEngHr = SharedPref.getObdEngineHours(context);
            double engineHrDiffInMin = constants.getEngineHourDiff(lastSavedEngHr, currentEngHr);

            if(DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic) || DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction) ) {
                newItemObj = getNewMalDiaDurationObj(DriverId, EventDateTime, EventEndDateTime, DetectionDataEventCode, (int)engineHrDiffInMin,
                        false, ClearEngineHours, ClearOdometer, lastSavedOdometer, lastSavedEngHr);
            }else{
                newItemObj = getNewMalDiaDurationObj(DriverId, EventDateTime, EventEndDateTime, DetectionDataEventCode, 0,
                        false, ClearEngineHours, ClearOdometer, currentOdometer, currentEngHr);
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


    public double getLast24HourEventsDurInMin(String EventType, DBHelper dbHelper){
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
                    if(eventObj.has(ConstantsKeys.TotalMinutes)){
                        lastEventMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
                    }


                    if(DetectionDataEventCode.equals(EventType)){
                        totalMin = totalMin + lastEventMinutes;
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




// update Malfunction/Diagnostic enable/disable Status
    public void updateMalfDiaStatusForEnable(String DriverId, Globally global, Constants constants, DBHelper dbHelper, Context context){

        try {
            JSONArray array = getMalDiaDurationArray(dbHelper);
            int offset = Math.abs((int) global.GetTimeZoneOffSet());

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
                            DateTime eventEndUtcDate = Globally.getDateTimeObj(EventEndDateTime, false);
                            EventEndDateTime = eventEndUtcDate.minusHours(offset).toString();
                        } else {
                            int TotalMinutes = 0;
                            if(obj.has(ConstantsKeys.TotalMinutes)){
                                TotalMinutes = obj.getInt(ConstantsKeys.TotalMinutes);
                            }
                            EventEndDateTime = driverZoneDate.plusMinutes(TotalMinutes).toString();
                        }
                    }
                }
                // get status by driver wise
                if (global.isSingleDriver(context)) {
                    UpdateStatus(IsClearEvent, DetectionDataEventCode, EventDateTime, EventEndDateTime, driverZoneDate, constants, context);
                }else{

                    String EventDriverId = obj.getString(ConstantsKeys.DriverId);

                    if(DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction) ||
                            DetectionDataEventCode.equals(Constants.EngineSyncMalfunctionEvent) ||
                            DetectionDataEventCode.equals(Constants.PositionComplianceMalfunction)){
                        UpdateStatus(IsClearEvent, DetectionDataEventCode, EventDateTime, EventEndDateTime, driverZoneDate, constants, context);
                    }else {
                        if (DriverId.equals(EventDriverId)) {
                            UpdateStatus(IsClearEvent, DetectionDataEventCode, EventDateTime, EventEndDateTime, driverZoneDate, constants, context);
                        }
                    }

                }


            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void UpdateStatus(boolean IsClearEvent, String DetectionDataEventCode, String EventDateTime,
                        String EventEndDateTime, DateTime driverZoneDate, Constants constants, Context context){
        if(!IsClearEvent) {
            if (DetectionDataEventCode.equals(Constants.MissingDataDiagnostic)) {
                SharedPref.saveLocDiagnosticStatus(true, driverZoneDate.toString(), EventDateTime, context);
                 constants.saveDiagnstcStatus(context, true);
            } else if (DetectionDataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                SharedPref.savePowerMalfunctionOccurStatus(
                        SharedPref.isPowerMalfunctionOccurred(context),
                        true, EventEndDateTime, context);
                constants.saveDiagnstcStatus(context, true);

            } else if (DetectionDataEventCode.equals(Constants.EngineSyncDiagnosticEvent) ) {
                SharedPref.saveEngSyncDiagnstcStatus(true, context);
                constants.saveDiagnstcStatus(context, true);

            } else if (DetectionDataEventCode.equals(Constants.PositionComplianceMalfunction)) {
                SharedPref.saveLocMalfunctionOccurStatus(true, driverZoneDate.toString(), EventDateTime, context);
                constants.saveMalfncnStatus(context, true);
            } else if (DetectionDataEventCode.equals(Constants.PowerComplianceMalfunction)) {
                SharedPref.savePowerMalfunctionOccurStatus(true,
                        SharedPref.isPowerDiagnosticOccurred(context),
                        EventEndDateTime, context);
                constants.saveMalfncnStatus(context, true);
            } else if (DetectionDataEventCode.equals(Constants.EngineSyncMalfunctionEvent)) {
                SharedPref.saveEngSyncMalfunctionStatus(true, context);
                constants.saveMalfncnStatus(context, true);
            }else if(DetectionDataEventCode.equals(Constants.UnIdentifiedDrivingDiagnostic)){
                constants.saveDiagnstcStatus(context, true);
            }
        }

    }
    private JSONObject getJsonObjForClearEvent(String DriverId, String EventDateTime, String DataEventCode, boolean IsClearEvent,
                                               JSONObject eventObj, boolean isAlreadyCleared, Context context){
        JSONObject clearObj = new JSONObject();

        try{

            clearObj.put(ConstantsKeys.DriverId, DriverId);
            clearObj.put(ConstantsKeys.CompanyId, DriverConst.GetDriverDetails(DriverConst.CompanyId, context));
            clearObj.put(ConstantsKeys.UnitNo, DriverConst.GetDriverTripDetails(DriverConst.Truck, context));
            clearObj.put(ConstantsKeys.EventDateTime, EventDateTime);
            clearObj.put(ConstantsKeys.DetectionDataEventCode, DataEventCode);

            int TotalMinutes = -1;
            if(eventObj.has(ConstantsKeys.TotalMinutes)){
                TotalMinutes = eventObj.getInt(ConstantsKeys.TotalMinutes);
            }

            String EventEndDateTime = "", ClearEngineHours = "", ClearOdometer = "";

            if(isAlreadyCleared){   // some times multiple events occurred with same event thats why we are clearing wth same time other events
                clearObj.put(ConstantsKeys.TotalMinutes, 0);
                clearObj.put(ConstantsKeys.ClearEventDateTime, EventDateTime);
                clearObj.put(ConstantsKeys.EventEndDateTime, EventDateTime);

                clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                clearObj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
                clearObj.put(ConstantsKeys.ClearOdometer, ClearOdometer);

            }else{
                if(TotalMinutes > 0){

                    if(eventObj.has(ConstantsKeys.EventEndDateTime)){
                        EventEndDateTime = eventObj.getString(ConstantsKeys.EventEndDateTime);
                    }else if(eventObj.has(ConstantsKeys.ClearEventDateTime)){
                        EventEndDateTime = eventObj.getString(ConstantsKeys.ClearEventDateTime);
                    }

                    if (!DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                        if (EventEndDateTime.length() == 0) {
                            DateTime endTime = Globally.getDateTimeObj(EventDateTime, false);
                            EventEndDateTime = endTime.plusMinutes(TotalMinutes).toString();
                        }
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

                    if (DataEventCode.equals(Constants.PowerComplianceDiagnostic)) {
                        if(eventObj.has(ConstantsKeys.EngineHours)) {
                            clearObj.put(ConstantsKeys.EngineHours, eventObj.getString(ConstantsKeys.EngineHours));
                        }

                        clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                        clearObj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
                        clearObj.put(ConstantsKeys.ClearOdometer, ClearOdometer);
                    }else{
                        clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                        clearObj.put(ConstantsKeys.ClearEngineHours, SharedPref.getObdEngineHours(context));
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
                        //  DateTime EventDate = Globally.getDateTimeObj(EventDateTime, false);
                        //  DateTime EventEndDate = Globally.getDateTimeObj(EventEndDateTime, false);
                        // int minDiff = Constants.getMinDiff(EventDate, EventEndDate);

                        clearObj.put(ConstantsKeys.TotalMinutes, TotalMinutes);
                        clearObj.put(ConstantsKeys.ClearEventDateTime, EventEndDateTime);

                        if (eventObj.has(ConstantsKeys.ClearEngineHours)) {
                            ClearEngineHours = eventObj.getString(ConstantsKeys.ClearEngineHours);
                        }
                        if (eventObj.has(ConstantsKeys.ClearOdometer)) {
                            ClearOdometer = eventObj.getString(ConstantsKeys.ClearOdometer);
                        }

                        clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                        clearObj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
                        clearObj.put(ConstantsKeys.ClearOdometer, ClearOdometer);


                    } else {
                        DateTime EventDate = Globally.getDateTimeObj(EventDateTime, false);
                        DateTime currentTime = Globally.GetCurrentUTCDateTime();
                        int minDiff = Constants.getMinDiff(EventDate, currentTime);

                        clearObj.put(ConstantsKeys.EventEndDateTime, currentTime.toString());
                        clearObj.put(ConstantsKeys.TotalMinutes, minDiff);
                        clearObj.put(ConstantsKeys.ClearEventDateTime, currentTime.toString());

                        clearObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                        clearObj.put(ConstantsKeys.ClearEngineHours, SharedPref.getObdEngineHours(context));
                        clearObj.put(ConstantsKeys.ClearOdometer, SharedPref.getObdOdometer(context));

                    }

                }

            }
            clearObj.put(ConstantsKeys.StartOdometer, eventObj.getString(ConstantsKeys.StartOdometer));
        }catch (Exception e){
            e.printStackTrace();
        }
        return clearObj;
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
                String EventDriverId      = obj.getString(ConstantsKeys.DriverId);

                int dayDiff = Constants.getDayDiff(EventDateTime, Globally.GetCurrentDateTime());
                if(dayDiff != 0 || EventDriverId.equals(DriverId)) {
                    boolean IsClearEvent = false;
                    if (obj.has(ConstantsKeys.IsClearEvent)) {
                        IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                    }

                    if (EventCode.length() > 0) { // means clear selected Event only

                        if (!IsClearEvent) {
                            if (EventCode.equals(DataEventCode)) {
                                if (Constants.isDiagnosticEvent(EventCode)) {

                                    JSONObject clearObj = getJsonObjForClearEvent(DriverId, EventDateTime, DataEventCode, isClear,
                                                                obj, isAlreadyCleared, context);

                                    if (clearObj.getInt(ConstantsKeys.TotalMinutes) >= 0) {
                                        eventClearArray.put(clearObj);
                                    }
                                    eventArray.put(i, clearObj);

                                    isAlreadyCleared = true;
                                } else {
                                    DateTime eventTime = Globally.getDateTimeObj(EventDateTime, false);
                                    long hourDiff = Constants.getDateTimeDuration(eventTime, currentDateTime).getStandardHours();
                                    if (hourDiff >= 24) {
                                        JSONObject clearObj = getJsonObjForClearEvent(DriverId, EventDateTime, DataEventCode, isClear,
                                                                    obj, isAlreadyCleared, context);

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

    public JSONArray AddNewItemInPositionArray(String  DriverId, String EventCode, String engHour, String odometer, DBHelper dbHelper){

        JSONObject newObj = getNewMalDiaDurationObj( DriverId, Globally.GetCurrentDateTime(), "",
                                EventCode, 0, false, engHour, odometer, odometer, engHour);
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

                    String ClearEngineHours = "", ClearOdometer = "";
                    boolean IsClearEvent = false;
                    if(obj.has(ConstantsKeys.IsClearEvent)){
                        IsClearEvent = obj.getBoolean(ConstantsKeys.IsClearEvent);
                    }

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
                    updatedObj.put(ConstantsKeys.IsClearEvent, IsClearEvent);
                    updatedObj.put(ConstantsKeys.ClearEngineHours, ClearEngineHours);
                    updatedObj.put(ConstantsKeys.ClearOdometer, ClearOdometer);
                    updatedObj.put(ConstantsKeys.StartOdometer, obj.getString(ConstantsKeys.StartOdometer));

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



    public String clearMalAfter24Hours(DBHelper dbHelper, Context context){
        String clearedEventCode = "";
        try{
            DateTime currentTime = Globally.GetCurrentJodaDateTime();

            if(SharedPref.isMalfunctionOccur(context) ) {

                String clearEventLastCallTime = SharedPref.getClearMalCallTime(context);
                if (clearEventLastCallTime.length() == 0) {
                    SharedPref.setClearMalCallTime(Globally.GetCurrentDateTime(), context);
                }
                DateTime lastCallDateTime = Globally.getDateTimeObj(clearEventLastCallTime, false);

                // Checking after 1 min
                long minDiff = Constants.getDateTimeDuration(lastCallDateTime, currentTime).getStandardMinutes();
                if (minDiff > 0) {
                    // update call time
                    SharedPref.setClearMalCallTime(Globally.GetCurrentDateTime(), context);

                    JSONArray getDurationArray = getMalDiaDurationArray(dbHelper);
                    for (int i = 0; i < getDurationArray.length() ; i++) {
                        JSONObject eventObj = (JSONObject) getDurationArray.get(i);
                   //     DateTime selectedDateTime = Globally.getDateTimeObj(eventObj.getString(ConstantsKeys.EventDateTime), false);
                     //   DateTime currentUtcDate = Globally.GetCurrentUTCDateTime();
                      //  long hourDiff = Constants.getDateTimeDuration(selectedDateTime, currentUtcDate).getStandardHours();

                      //  if(hourDiff >= 24){
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
                                }
                            }

                      //  }
                    }
                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }

        return clearedEventCode;
    }


}
