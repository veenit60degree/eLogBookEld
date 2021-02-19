package com.local.db;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.background.service.BackgroundLocationService;
import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.models.EldDriverLogModel;
import com.messaging.logistic.Globally;
import com.messaging.logistic.fragment.EldFragment;
import com.models.DriverLogModel;
import com.models.EldDataModelNew;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.EldCoDriverLogPref;
import com.shared.pref.EldSingleDriverLogPref;
import com.shared.pref.MainDriverEldPref;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;
import webapi.LocalCalls;


public class HelperMethods {

    final int OFF_DUTY = 1;
    final int SLEEPER  = 2;
    final int DRIVING  = 3;
    final int ON_DUTY  = 4;
    final int PERSONAL = 5;

    public HelperMethods() {
        super();
    }

    /*-------------------- GET DRIVER SAVED Array -------------------- */
    public JSONArray getSavedLogArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();

        try {
            Cursor rs = dbHelper.getDriverLog(DriverId);

            if(rs != null && rs.getCount() > 0) {
                rs.moveToFirst();
                String logList = rs.getString(rs.getColumnIndex(DBHelper.DRIVER_LOG_LIST));
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


    public void DriverLogHelper( int driverId, DBHelper dbHelper, JSONArray driverLogArray){

        Cursor rs = dbHelper.getDriverLog(driverId);

        try {
            if (rs != null & rs.getCount() > 0) {
                rs.moveToFirst();
                dbHelper.UpdateDriverLog(driverId, driverLogArray);        // UPDATE DRIVER LOG
            } else {
                dbHelper.InsertDriverLog(driverId, driverLogArray);      // INSERT DRIVER LOG
            }
            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String getLastStatusDateTime(JSONArray driverLogArray){
        String endDateTime = "";
        try {
            if(driverLogArray.length() > 0){
                JSONObject obj = (JSONObject)driverLogArray.get(driverLogArray.length()-1);
                endDateTime = obj.getString(ConstantsKeys.endDateTime);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return endDateTime;
    }


    public DriverDetail getDriverList(DateTime currentDate, DateTime currentUTCDate, int driverId,
                                      final int offsetFromUTC, final int eldCyclesId, final boolean isSingleDriver, int LastStatus,
                                      boolean isOldRecord, boolean isHaulException,  boolean isAdverseException, int ruleVersion, List<DriverLog> logList){

        LocalTime time = new LocalTime();
        DriverDetail model = new DriverDetail();
        model.setOnDutyDriving(EldFragment.DRIVING);
        model.setOffDutyNotInSleeper(EldFragment.OFF_DUTY);
        model.setOffDutyInSleeper(EldFragment.SLEEPER);
        model.setOnDutyNotDriving(EldFragment.ON_DUTY);
        model.setRuleVersion(ruleVersion);
        model.setCurrentDate(currentDate);
        model.setCurrentUTCDate(currentUTCDate);

        model.setEldCyclesId(eldCyclesId);
        model.setSingleDriver(isSingleDriver);

        model.setDeviceId("");
        model.setDriverId(driverId);
        model.setOffsetFromUTC(offsetFromUTC);
        model.setoDriverLogDetail(logList);

        model.setPeriodStartTime(time);
        model.setNotificationCategory("");

        model.setEnableHalfDrivingTimeLeft(false);
        model.setEnableHalfOnDutyTimeLeft(false);
        model.setEnableHalfConsecDutyTimeLeft(false);

        model.setSend2ndDrivingNotification(false);
        model.setSend3rdDrivingNotification(false);
        model.setSend4thDrivingNotification(false);
        model.setSend5thNotification(false);
        model.setLeftMinutesOfConcernedDuty(0);
        model.setNotificationType(0);
        model.setViolationNotificationType("");
        model.setLastSelectedStatus(LastStatus);
        model.setOldRecord(isOldRecord);
        model.setShortHaulExceptionEnabled(isHaulException);

        // is16HourExceptionEnabled is used for Adverse Exception
        model.setIs16HourExceptionEnabled(isAdverseException);

        return model;
    }


    //  Get new log as Json in Array
    public JSONObject AddJobInArray(long DriverLogId, long DriverId,
                                    int DriverStatusId,
                                    String StartDateTime, String EndDateTime,
                                    String UTCStartDateTime, String UTCEndDateTime,
                                    double totalMin,
                                    String StartLatitude, String StartLongitude,
                                    String EndLatitude, String EndLongitude,

                                    boolean YardMove, boolean Personal,
                                    int CurrentCycleId, boolean IsViolation,
                                    String ViolationReason,
                                    String DriverName, String Remarks,
                                    String Trailor, String StartLocation,
                                    String EndLocation, String Truck,
                                    String IsStatusAutomatic, String OBDSpeed,
                                    String GPSSpeed, String PlateNumber, boolean isHaulException, boolean isShortHaulUpdate,
                                    String decesionSource,   String isAdverseException,
                                    String adverseExceptionRemark){

        JSONObject driverLogJson = new JSONObject();

        try {

            driverLogJson.put(ConstantsKeys.DriverLogId,  DriverLogId );/**/
            driverLogJson.put(ConstantsKeys.DriverId, DriverId);

            driverLogJson.put(ConstantsKeys.ProjectId, 1);      // static
            driverLogJson.put(ConstantsKeys.DriverStatusId, DriverStatusId);

            driverLogJson.put(ConstantsKeys.startDateTime, StartDateTime );
            driverLogJson.put(ConstantsKeys.endDateTime, EndDateTime);
            driverLogJson.put(ConstantsKeys.utcStartDateTime, UTCStartDateTime);
            driverLogJson.put(ConstantsKeys.utcEndDateTime, UTCEndDateTime);

            driverLogJson.put(ConstantsKeys.totalMin, totalMin);

            driverLogJson.put(ConstantsKeys.StartLatitude,  StartLatitude);
            driverLogJson.put(ConstantsKeys.StartLongitude,  StartLongitude );
            driverLogJson.put(ConstantsKeys.EndLatitude,  EndLatitude );
            driverLogJson.put(ConstantsKeys.EndLongitude,  EndLongitude );

            driverLogJson.put(ConstantsKeys.YardMove,  YardMove );
            driverLogJson.put(ConstantsKeys.Personal,  Personal);

            driverLogJson.put(ConstantsKeys.CurrentCycleId, CurrentCycleId );
            driverLogJson.put(ConstantsKeys.IsViolation, IsViolation );

            driverLogJson.put(ConstantsKeys.ViolationReason,  ViolationReason);
            driverLogJson.put(ConstantsKeys.createdDate, EndDateTime);

            driverLogJson.put(ConstantsKeys.DriverName, DriverName );
            driverLogJson.put(ConstantsKeys.Remarks, Remarks );
            driverLogJson.put(ConstantsKeys.Trailor, Trailor);
            driverLogJson.put(ConstantsKeys.StartLocation, StartLocation);
            driverLogJson.put(ConstantsKeys.EndLocation, EndLocation );
            driverLogJson.put(ConstantsKeys.Truck, Truck );

            driverLogJson.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);
            driverLogJson.put(ConstantsKeys.OBDSpeed, OBDSpeed );
            driverLogJson.put(ConstantsKeys.GPSSpeed, GPSSpeed );
            driverLogJson.put(ConstantsKeys.PlateNumber, PlateNumber );

            driverLogJson.put(ConstantsKeys.IsShortHaulException, isHaulException );
            driverLogJson.put(ConstantsKeys.IsShortHaulUpdate, isShortHaulUpdate );

            driverLogJson.put(ConstantsKeys.DecesionSource, decesionSource);

            driverLogJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
            driverLogJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);


        }catch (Exception e){
            e.printStackTrace();
        }

        return driverLogJson;

    }



    public JSONArray updateLocationInLastItem(JSONArray array, String address){
        JSONArray updatedArray = new JSONArray();
        try {

            if(array != null && array.length() >= 0) {

                JSONObject lastJob = GetLastJsonFromArray(array);
                lastJob.put(ConstantsKeys.StartLocation, address);
                lastJob.put(ConstantsKeys.EndLocation, address);

                updatedArray = UpdateJobLastItemInArray(array, lastJob);


            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return updatedArray;
    }



/*

    public JSONObject AddNewDateLog (JSONArray driverLogArray, DateTime currentDateTime, DateTime currentUTCTime,
                                    int offsetFromUTC, DBHelper dbHelper){
        JSONObject newJsonData = new JSONObject();
        long DriverId = 0;
        try {
            JSONObject Lastobj = (JSONObject) driverLogArray.get(driverLogArray.length() - 1);
            String startDateTime = Globally.ConvertDeviceDateTimeFormat(Globally.GetCurrentDeviceDate() + " 00:00:00"); //MM/dd/yyyy HH:mm:ss
            String startUTCDateTime = Globally.GetUTCFromDate(startDateTime, offsetFromUTC);
            String endUTCDateTime = Globally.GetUTCFromDate(String.valueOf(currentUTCTime), offsetFromUTC);
            double totalMin = currentDateTime.getMinuteOfDay() - Globally.getDateTimeObj(startDateTime, false).getMinuteOfDay();

            DriverId = Lastobj.getLong(ConstantsKeys.DriverId);

            DateTime lastEndDate = Globally.getDateTimeObj(Lastobj.getString(ConstantsKeys.endDateTime), false);

            int diff = DayDiffSplitMethod(currentDateTime, lastEndDate);


            int CycleId = 1;
            if (!Lastobj.isNull("ConstantsKeys.CurrentCycleId")) {
                CycleId = Lastobj.getInt(ConstantsKeys.CurrentCycleId);
            }

                if (diff == 0) {
                    return null;
                } else {
                    newJsonData = addJSON(Lastobj, DriverId, startDateTime, startUTCDateTime,
                            currentDateTime, Globally.getDateTimeObj(endUTCDateTime, true), totalMin, CycleId);
                    driverLogArray.put(newJsonData);
                    DriverLogHelper((int) DriverId, dbHelper, driverLogArray);
                   */
/* selectedArray = GetSelectedDateArray(driverLogArray, String.valueOf(DriverId), currentDateTime,
                            currentDateTime, currentUTCTime, offsetFromUTC, dbHelper);*//*

                }
            }catch(JSONException e){
                e.printStackTrace();
        }

        return newJsonData;
    }



    private JSONObject addJSON(JSONObject Lastobj, long DriverId, String startDateTime, String startUTCDateTime, 
                               DateTime currentEndDateTime, DateTime endUTCTime, double totalMin, int CycleId, boolean HaulHourException){
        JSONObject newJsonData = new JSONObject();
        try {
            String IsStatusAutomatic = "false", OBDSpeed = "0", GPSSpeed = "0";
            if(newJsonData.has(ConstantsKeys.IsStatusAutomatic))
                IsStatusAutomatic = newJsonData.getString(ConstantsKeys.IsStatusAutomatic);

            if(newJsonData.has(ConstantsKeys.OBDSpeed))
                OBDSpeed = newJsonData.getString(ConstantsKeys.OBDSpeed);

            if(newJsonData.has(ConstantsKeys.GPSSpeed))
                GPSSpeed = newJsonData.getString(ConstantsKeys.GPSSpeed);


            newJsonData = AddJobInArray(
                    Lastobj.getLong(ConstantsKeys.DriverLogId) + 1,
                    DriverId,
                    Lastobj.getInt(ConstantsKeys.DriverStatusId),

                    startDateTime,
                    String.valueOf(currentEndDateTime),
                    startUTCDateTime,
                    String.valueOf(endUTCTime),

                    totalMin,
                    "", "", "", "",
                    false,
                    Lastobj.getBoolean(ConstantsKeys.Personal),
                    CycleId,
                    Lastobj.getBoolean(ConstantsKeys.IsViolation),
                    "",
                    Lastobj.getString(ConstantsKeys.DriverName),
                    Lastobj.getString(ConstantsKeys.Remarks),
                    Lastobj.getString(ConstantsKeys.Trailor),
                    "", "",
                    Lastobj.getString(ConstantsKeys.Truck),
                    IsStatusAutomatic,
                    OBDSpeed,
                    GPSSpeed,
                    HaulHourException

            );
        }catch (Exception e){
            e.printStackTrace();
        }
        return newJsonData;
        
    }

*/


    public JSONArray UpdateJobLastItemInArray(JSONArray array, JSONObject jsonObject){

        if(array.length() > 0){
            try {
                array.put(array.length()-1, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    //  Get Last log as Json From Array
    public JSONObject GetLastJsonFromArray(JSONArray logArray){
        JSONObject json = new JSONObject();
        try {
            if(logArray.length() > 0){
                json = (JSONObject)logArray.get(logArray.length()-1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }



    public JSONObject updateLastItemFromArray(JSONArray logArray, JSONObject lastObj, DateTime currentDateTime, int offsetFromUTC){

        try {
            String endDateStr = lastObj.getString(ConstantsKeys.endDateTime).substring(0, 10) + "T23:59:59";

            DateTime lastEndTime = new DateTime(endDateStr);
            DateTime lastStartTime = Globally.getDateTimeObj(lastObj.getString(ConstantsKeys.startDateTime), false);
            int LastJobTotalMin = lastEndTime.getMinuteOfDay() - lastStartTime.getMinuteOfDay();

            lastObj.put(ConstantsKeys.endDateTime,        endDateStr);
            lastObj.put(ConstantsKeys.utcEndDateTime,     Globally.GetUTCFromDate(endDateStr, offsetFromUTC));
            lastObj.put(ConstantsKeys.totalMin,           LastJobTotalMin);

        }catch (Exception e){
            e.printStackTrace();
        }

        return lastObj;
    }


    public JSONArray addSkipDaysItemsInArray(JSONArray logArray, DateTime lastObjDateTime,
                                             int offsetFromUTC, int dayDiff){

        try {

            for(int i = 0 ; i < dayDiff ; i++){
                try{

                    DateTime dayStartDateTime = lastObjDateTime.plusDays(i+1);

                    String startDateStr = dayStartDateTime.toString().substring(0, 10) + "T00:00:00";
                    String endDateStr;
                    if(i == dayDiff-1){
                        endDateStr   = Globally.GetCurrentDateTime();
                    }else{
                        endDateStr   = dayStartDateTime.toString().substring(0, 10) + "T23:59:59";
                    }

                    JSONObject newObj = AddSameStatusInNextDayArray(logArray,startDateStr, Globally.GetUTCFromDate(startDateStr, offsetFromUTC), endDateStr,
                            Globally.GetUTCFromDate(endDateStr, offsetFromUTC));

                    // JSONObject newObj = SplitJsonFromArrayHomeScreen(logArray, startDateStr, Globally.GetUTCFromDate(startDateStr, offsetFromUTC));

                    logArray.put(newObj);


                }catch (Exception e){
                    e.printStackTrace();
                }

            }



        }catch (Exception e){
            e.printStackTrace();
        }

        return logArray;
    }


    public JSONArray updateLastItem(JSONArray logArray, JSONObject lastObj, DateTime currentDateTime, int offsetFromUTC){
        int length = logArray.length()-1;
        try {
            String endDateStr = lastObj.getString(ConstantsKeys.endDateTime).substring(0, 10) + "T23:59:59";

            DateTime lastEndTime = new DateTime(endDateStr);
            DateTime lastStartTime = Globally.getDateTimeObj(lastObj.getString(ConstantsKeys.startDateTime), false);
            int LastJobTotalMin = lastEndTime.getMinuteOfDay() - lastStartTime.getMinuteOfDay();

            lastObj.put(ConstantsKeys.endDateTime,        endDateStr);
            lastObj.put(ConstantsKeys.utcEndDateTime,     Globally.GetUTCFromDate(endDateStr, offsetFromUTC));
            lastObj.put(ConstantsKeys.totalMin,           LastJobTotalMin);

            logArray.put(length, lastObj);


            String startDateStr = currentDateTime.toString().substring(0, 10) + "T00:00:00";
      /*      if(new DateTime(startDateStr).equals(new DateTime(endDateStr).plusSeconds(1))){
                Log.d("dateeee", "sameeeeeeeee: " );
            }*/

            JSONObject newObj = SplitJsonFromArrayHomeScreen(logArray, startDateStr, Globally.GetUTCFromDate(startDateStr, offsetFromUTC));

            logArray.put(newObj);

        }catch (Exception e){
            e.printStackTrace();
        }

        return logArray;
    }



    public JSONArray updateLastJob(JSONArray array, String CurrentCycleId){
        JSONArray updatedArray = new JSONArray();
        try {
            JSONObject lastJob = GetLastJsonFromArray(array);
            lastJob.put(ConstantsKeys.CurrentCycleId, CurrentCycleId);

            updatedArray = UpdateJobLastItemInArray(array, lastJob);
        }catch (Exception e){
            e.printStackTrace();
        }

        return updatedArray;
    }


    //  Update Last log date time in Array for end date time
    public JSONObject UpdateLastJsonFromArray(JSONArray logArray, String endDateTime, String utcEndDateTime, double totalMin){
        JSONObject driverLogJson = new JSONObject();
        try {
            if(logArray.length() > 0){
                JSONObject logObj = (JSONObject) logArray.get(logArray.length()-1);

                String DriverName = "", IsStatusAutomatic = "false", OBDSpeed = "0", GPSSpeed = "0";
                String DecesionSource = "", PlateNumber = "";
                String isAdverseException = "", adverseExceptionRemark = "";
                boolean HaulHourException = false, IsShortHaulUpdate = false;

                int CycleId = 1;
                if(!logObj.isNull("ConstantsKeys.CurrentCycleId"))
                    CycleId = logObj.getInt(ConstantsKeys.CurrentCycleId);

                driverLogJson.put(ConstantsKeys.DriverLogId,        logObj.getLong(ConstantsKeys.DriverLogId));
                driverLogJson.put(ConstantsKeys.DriverId ,          logObj.getLong(ConstantsKeys.DriverId));

                driverLogJson.put(ConstantsKeys.ProjectId,          logObj.getInt(ConstantsKeys.ProjectId));
                driverLogJson.put(ConstantsKeys.DriverStatusId,     logObj.getInt(ConstantsKeys.DriverStatusId));

                driverLogJson.put(ConstantsKeys.startDateTime,      logObj.getString(ConstantsKeys.startDateTime));
                driverLogJson.put(ConstantsKeys.endDateTime,        endDateTime);
                driverLogJson.put(ConstantsKeys.utcStartDateTime,   logObj.getString(ConstantsKeys.utcStartDateTime));
                driverLogJson.put(ConstantsKeys.utcEndDateTime,     utcEndDateTime);

                driverLogJson.put(ConstantsKeys.totalMin,           totalMin);

                driverLogJson.put(ConstantsKeys.StartLatitude,      logObj.getString(ConstantsKeys.StartLatitude));
                driverLogJson.put(ConstantsKeys.StartLongitude,     logObj.getString(ConstantsKeys.StartLongitude));
                driverLogJson.put(ConstantsKeys.EndLatitude,        logObj.getString(ConstantsKeys.EndLatitude));
                driverLogJson.put(ConstantsKeys.EndLongitude,       logObj.getString(ConstantsKeys.EndLongitude));

                driverLogJson.put(ConstantsKeys.YardMove,           logObj.getBoolean(ConstantsKeys.YardMove));
                driverLogJson.put(ConstantsKeys.Personal,           logObj.getBoolean(ConstantsKeys.Personal));

                driverLogJson.put(ConstantsKeys.CurrentCycleId,     CycleId);
                driverLogJson.put(ConstantsKeys.IsViolation,        logObj.getBoolean(ConstantsKeys.IsViolation));

                driverLogJson.put(ConstantsKeys.ViolationReason,    logObj.getString(ConstantsKeys.ViolationReason));
                driverLogJson.put(ConstantsKeys.createdDate,        logObj.getString(ConstantsKeys.createdDate));

                driverLogJson.put(ConstantsKeys.Remarks,            logObj.getString(ConstantsKeys.Remarks));
                driverLogJson.put(ConstantsKeys.Trailor,            logObj.getString(ConstantsKeys.Trailor));
                driverLogJson.put(ConstantsKeys.StartLocation,      logObj.getString(ConstantsKeys.StartLocation));
                driverLogJson.put(ConstantsKeys.EndLocation,        logObj.getString(ConstantsKeys.EndLocation));
                driverLogJson.put(ConstantsKeys.Truck,              logObj.getString(ConstantsKeys.Truck));

                if(logObj.has(ConstantsKeys.DriverName))
                    DriverName = logObj.getString(ConstantsKeys.DriverName);
                else

                if(logObj.has(ConstantsKeys.IsStatusAutomatic))
                    IsStatusAutomatic = logObj.getString(ConstantsKeys.IsStatusAutomatic);

                if(logObj.has(ConstantsKeys.OBDSpeed))
                    OBDSpeed = logObj.getString(ConstantsKeys.OBDSpeed);

                if(logObj.has(ConstantsKeys.GPSSpeed))
                    GPSSpeed = logObj.getString(ConstantsKeys.GPSSpeed);

                if(logObj.has(ConstantsKeys.PlateNumber))
                    PlateNumber = logObj.getString(ConstantsKeys.PlateNumber);


                if(logObj.has(ConstantsKeys.IsShortHaulException) && !logObj.getString(ConstantsKeys.IsShortHaulException).equals("null") )
                    HaulHourException = logObj.getBoolean(ConstantsKeys.IsShortHaulException);

                if(logObj.has(ConstantsKeys.IsShortHaulUpdate) && !logObj.getString(ConstantsKeys.IsShortHaulUpdate).equals("null")  ) {
                    IsShortHaulUpdate = logObj.getBoolean(ConstantsKeys.IsShortHaulUpdate);
                }

                if(logObj.has(ConstantsKeys.DecesionSource))
                    DecesionSource = logObj.getString(ConstantsKeys.DecesionSource);

                if (logObj.has(ConstantsKeys.IsAdverseException )) {
                    isAdverseException = logObj.getString(ConstantsKeys.IsAdverseException );
                }
                if (logObj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                    adverseExceptionRemark = logObj.getString(ConstantsKeys.AdverseExceptionRemarks);
                }

                driverLogJson.put(ConstantsKeys.DriverName,        DriverName );
                driverLogJson.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);
                driverLogJson.put(ConstantsKeys.OBDSpeed,          OBDSpeed);
                driverLogJson.put(ConstantsKeys.GPSSpeed,          GPSSpeed);
                driverLogJson.put(ConstantsKeys.PlateNumber,       PlateNumber);

                driverLogJson.put(ConstantsKeys.IsShortHaulException,  HaulHourException);
                driverLogJson.put(ConstantsKeys.IsShortHaulUpdate, IsShortHaulUpdate);

                driverLogJson.put(ConstantsKeys.DecesionSource,     DecesionSource);

                driverLogJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
                driverLogJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return driverLogJson;
    }


    //  Sppit log date time in Array
    public JSONObject SplitJsonFromArray(JSONArray logArray, String startDateTime, String startUtcDate, String endDateTime, String endUtcDate,
                                         double totalMin, boolean isSplitPart1,     RulesResponseObject RulesObj){
        JSONObject driverLogJson = new JSONObject();
        String IsStatusAutomatic = "false", HaulHourException = "false", IsShortHaulUpdate = "false", OBDSpeed = "0";
        String DecesionSource = "", GPSSpeed = "0", PlateNumber = "";
        String isAdverseException = "", adverseExceptionRemark = "";

        try {
            if(logArray.length() > 0){
                JSONObject logObj = (JSONObject) logArray.get(logArray.length()-1);

                driverLogJson.put(ConstantsKeys.DriverLogId,        logObj.getLong(ConstantsKeys.DriverLogId));
                driverLogJson.put(ConstantsKeys.DriverId ,          logObj.getLong(ConstantsKeys.DriverId));

                driverLogJson.put(ConstantsKeys.ProjectId,          logObj.getInt(ConstantsKeys.ProjectId));
                driverLogJson.put(ConstantsKeys.DriverStatusId,     logObj.getInt(ConstantsKeys.DriverStatusId));

                if(isSplitPart1) {

                    driverLogJson.put(ConstantsKeys.startDateTime, logObj.getString(ConstantsKeys.startDateTime));
                    driverLogJson.put(ConstantsKeys.utcStartDateTime, logObj.getString(ConstantsKeys.utcStartDateTime));
                    driverLogJson.put(ConstantsKeys.endDateTime, endDateTime);
                    driverLogJson.put(ConstantsKeys.utcEndDateTime, endUtcDate);

                    driverLogJson.put(ConstantsKeys.IsViolation,        false);

                    driverLogJson.put(ConstantsKeys.ViolationReason,    "");

                }else {
                    driverLogJson.put(ConstantsKeys.startDateTime, startDateTime);
                    driverLogJson.put(ConstantsKeys.utcStartDateTime, startUtcDate);

                    driverLogJson.put(ConstantsKeys.endDateTime, logObj.getString(ConstantsKeys.endDateTime));
                    driverLogJson.put(ConstantsKeys.utcEndDateTime, logObj.getString(ConstantsKeys.utcEndDateTime));

                    driverLogJson.put(ConstantsKeys.IsViolation,        true);

                    driverLogJson.put(ConstantsKeys.ViolationReason,    RulesObj.getViolationReason());

                }
                driverLogJson.put(ConstantsKeys.totalMin,           totalMin);

                driverLogJson.put(ConstantsKeys.StartLatitude,      logObj.getString(ConstantsKeys.StartLatitude));
                driverLogJson.put(ConstantsKeys.StartLongitude,     logObj.getString(ConstantsKeys.StartLongitude));
                driverLogJson.put(ConstantsKeys.EndLatitude,        logObj.getString(ConstantsKeys.EndLatitude));
                driverLogJson.put(ConstantsKeys.EndLongitude,       logObj.getString(ConstantsKeys.EndLongitude));

                driverLogJson.put(ConstantsKeys.YardMove,           logObj.getBoolean(ConstantsKeys.YardMove));
                driverLogJson.put(ConstantsKeys.Personal,           logObj.getBoolean(ConstantsKeys.Personal));

                driverLogJson.put(ConstantsKeys.CurrentCycleId,     logObj.getString(ConstantsKeys.CurrentCycleId));

                driverLogJson.put(ConstantsKeys.createdDate,        logObj.getString(ConstantsKeys.createdDate));

                driverLogJson.put(ConstantsKeys.DriverName,         logObj.getString(ConstantsKeys.DriverName));
                driverLogJson.put(ConstantsKeys.Remarks,            logObj.getString(ConstantsKeys.Remarks));
                driverLogJson.put(ConstantsKeys.Trailor,            logObj.getString(ConstantsKeys.Trailor));
                driverLogJson.put(ConstantsKeys.StartLocation,      logObj.getString(ConstantsKeys.StartLocation));
                driverLogJson.put(ConstantsKeys.EndLocation,        logObj.getString(ConstantsKeys.EndLocation));
                driverLogJson.put(ConstantsKeys.Truck,              logObj.getString(ConstantsKeys.Truck));

                if(logObj.has(ConstantsKeys.IsStatusAutomatic))
                    IsStatusAutomatic = logObj.getString(ConstantsKeys.IsStatusAutomatic);

                if(logObj.has(ConstantsKeys.OBDSpeed))
                    OBDSpeed = logObj.getString(ConstantsKeys.OBDSpeed);

                if(logObj.has(ConstantsKeys.GPSSpeed))
                    GPSSpeed = logObj.getString(ConstantsKeys.GPSSpeed);

                if(logObj.has(ConstantsKeys.PlateNumber))
                    PlateNumber = logObj.getString(ConstantsKeys.PlateNumber);

                if(logObj.has(ConstantsKeys.IsShortHaulException))
                    HaulHourException = logObj.getString(ConstantsKeys.IsShortHaulException);

                if(logObj.has(ConstantsKeys.IsShortHaulUpdate))
                    IsShortHaulUpdate = logObj.getString(ConstantsKeys.IsShortHaulUpdate);



                if(logObj.has(ConstantsKeys.DecesionSource))
                    DecesionSource = logObj.getString(ConstantsKeys.DecesionSource);

                if (logObj.has(ConstantsKeys.IsAdverseException )) {
                    isAdverseException = logObj.getString(ConstantsKeys.IsAdverseException );
                }
                if (logObj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                    adverseExceptionRemark = logObj.getString(ConstantsKeys.AdverseExceptionRemarks);
                }

                driverLogJson.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);
                driverLogJson.put(ConstantsKeys.OBDSpeed,          OBDSpeed);
                driverLogJson.put(ConstantsKeys.GPSSpeed,          GPSSpeed);
                driverLogJson.put(ConstantsKeys.PlateNumber,       PlateNumber);

                driverLogJson.put(ConstantsKeys.IsShortHaulException, HaulHourException);
                driverLogJson.put(ConstantsKeys.IsShortHaulUpdate, IsShortHaulUpdate );

                driverLogJson.put(ConstantsKeys.DecesionSource,    DecesionSource);

                driverLogJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
                driverLogJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return driverLogJson;
    }



    //  Slpit log date time in Array
    public JSONObject AddSameStatusInNextDayArray(JSONArray logArray, String startDateTime, String startUtcDate, String endDateTime,
                                                  String endUtcDate){
        JSONObject driverLogJson = new JSONObject();
        String IsStatusAutomatic = "false", HaulHourException = "false", IsShortHaulUpdate = "false", OBDSpeed = "0", GPSSpeed = "0";
        String DecesionSource = "", PlateNumber = "";
        String isAdverseException = "", adverseExceptionRemark = "";

        int totalMin = 0;

        try {
            DateTime startDate = Globally.getDateTimeObj(startDateTime, false);
            DateTime endDate = Globally.getDateTimeObj(endDateTime, false);
            totalMin = endDate.getMinuteOfDay() - startDate.getMinuteOfDay();

        }catch (Exception e){}

        try {
            if(logArray.length() > 0){
                JSONObject logObj = (JSONObject) logArray.get(logArray.length()-1);

                driverLogJson.put(ConstantsKeys.DriverLogId,        logObj.getLong(ConstantsKeys.DriverLogId));
                driverLogJson.put(ConstantsKeys.DriverId ,          logObj.getLong(ConstantsKeys.DriverId));

                driverLogJson.put(ConstantsKeys.ProjectId,          logObj.getInt(ConstantsKeys.ProjectId));
                driverLogJson.put(ConstantsKeys.DriverStatusId,     logObj.getInt(ConstantsKeys.DriverStatusId));

                driverLogJson.put(ConstantsKeys.startDateTime,      startDateTime);
                driverLogJson.put(ConstantsKeys.utcStartDateTime,   startUtcDate);

                driverLogJson.put(ConstantsKeys.endDateTime,        endDateTime);
                driverLogJson.put(ConstantsKeys.utcEndDateTime,     endUtcDate);

                driverLogJson.put(ConstantsKeys.IsViolation,        logObj.getString(ConstantsKeys.IsViolation));

                driverLogJson.put(ConstantsKeys.ViolationReason,   logObj.getString(ConstantsKeys.ViolationReason));


                driverLogJson.put(ConstantsKeys.totalMin,           totalMin);

                driverLogJson.put(ConstantsKeys.StartLatitude,      logObj.getString(ConstantsKeys.StartLatitude));
                driverLogJson.put(ConstantsKeys.StartLongitude,     logObj.getString(ConstantsKeys.StartLongitude));
                driverLogJson.put(ConstantsKeys.EndLatitude,        logObj.getString(ConstantsKeys.EndLatitude));
                driverLogJson.put(ConstantsKeys.EndLongitude,       logObj.getString(ConstantsKeys.EndLongitude));

                driverLogJson.put(ConstantsKeys.YardMove,           logObj.getBoolean(ConstantsKeys.YardMove));
                driverLogJson.put(ConstantsKeys.Personal,           logObj.getBoolean(ConstantsKeys.Personal));

                driverLogJson.put(ConstantsKeys.CurrentCycleId,     logObj.getString(ConstantsKeys.CurrentCycleId));

                driverLogJson.put(ConstantsKeys.createdDate,        logObj.getString(ConstantsKeys.createdDate));

                driverLogJson.put(ConstantsKeys.DriverName,         logObj.getString(ConstantsKeys.DriverName));
                driverLogJson.put(ConstantsKeys.Remarks,            logObj.getString(ConstantsKeys.Remarks));
                driverLogJson.put(ConstantsKeys.Trailor,            logObj.getString(ConstantsKeys.Trailor));
                driverLogJson.put(ConstantsKeys.StartLocation,      logObj.getString(ConstantsKeys.StartLocation));
                driverLogJson.put(ConstantsKeys.EndLocation,        logObj.getString(ConstantsKeys.EndLocation));
                driverLogJson.put(ConstantsKeys.Truck,              logObj.getString(ConstantsKeys.Truck));

                if(logObj.has(ConstantsKeys.IsStatusAutomatic))
                    IsStatusAutomatic = logObj.getString(ConstantsKeys.IsStatusAutomatic);

                if(logObj.has(ConstantsKeys.OBDSpeed))
                    OBDSpeed = logObj.getString(ConstantsKeys.OBDSpeed);

                if(logObj.has(ConstantsKeys.GPSSpeed))
                    GPSSpeed = logObj.getString(ConstantsKeys.GPSSpeed);

                if(logObj.has(ConstantsKeys.PlateNumber))
                    PlateNumber = logObj.getString(ConstantsKeys.PlateNumber);

                if(logObj.has(ConstantsKeys.IsShortHaulException))
                    HaulHourException = logObj.getString(ConstantsKeys.IsShortHaulException);

                if(logObj.has(ConstantsKeys.IsShortHaulUpdate))
                    IsShortHaulUpdate = logObj.getString(ConstantsKeys.IsShortHaulUpdate);

                if(logObj.has(ConstantsKeys.DecesionSource))
                    DecesionSource = logObj.getString(ConstantsKeys.DecesionSource);

                if (logObj.has(ConstantsKeys.IsAdverseException )) {
                    isAdverseException = logObj.getString(ConstantsKeys.IsAdverseException );
                }
                if (logObj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                    adverseExceptionRemark = logObj.getString(ConstantsKeys.AdverseExceptionRemarks);
                }

                driverLogJson.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);
                driverLogJson.put(ConstantsKeys.OBDSpeed,          OBDSpeed);
                driverLogJson.put(ConstantsKeys.GPSSpeed,          GPSSpeed);
                driverLogJson.put(ConstantsKeys.PlateNumber,       PlateNumber);
                driverLogJson.put(ConstantsKeys.IsShortHaulException, HaulHourException);
                driverLogJson.put(ConstantsKeys.IsShortHaulUpdate, IsShortHaulUpdate );

                driverLogJson.put(ConstantsKeys.DecesionSource,    DecesionSource);

                driverLogJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
                driverLogJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);




            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return driverLogJson;
    }



    //  Split log date time in Array
    public JSONObject SplitJsonFromArrayHomeScreen(JSONArray logArray, String dateTime, String utcDateTime){
        JSONObject driverLogJson = new JSONObject();
        String IsStatusAutomatic = "false", HaulHourException = "false", IsShortHaulUpdate = "", OBDSpeed = "0";
        String DecesionSource = "", GPSSpeed = "0", PlateNumber = "";
        String isAdverseException = "", adverseExceptionRemark = "";

        try {
            if(logArray.length() > 0){
                JSONObject logObj = (JSONObject) logArray.get(logArray.length()-1);

                driverLogJson.put(ConstantsKeys.DriverLogId,        logObj.getLong(ConstantsKeys.DriverLogId));
                driverLogJson.put(ConstantsKeys.DriverId ,          logObj.getLong(ConstantsKeys.DriverId));

                driverLogJson.put(ConstantsKeys.ProjectId,          logObj.getInt(ConstantsKeys.ProjectId));
                driverLogJson.put(ConstantsKeys.DriverStatusId,     logObj.getInt(ConstantsKeys.DriverStatusId));

                driverLogJson.put(ConstantsKeys.startDateTime, dateTime);
                driverLogJson.put(ConstantsKeys.utcStartDateTime, utcDateTime);

                driverLogJson.put(ConstantsKeys.endDateTime, dateTime);
                driverLogJson.put(ConstantsKeys.utcEndDateTime, utcDateTime);

                driverLogJson.put(ConstantsKeys.IsViolation,        false);
                driverLogJson.put(ConstantsKeys.ViolationReason,    "");
                driverLogJson.put(ConstantsKeys.totalMin,           0);

                driverLogJson.put(ConstantsKeys.StartLatitude,      logObj.getString(ConstantsKeys.StartLatitude));
                driverLogJson.put(ConstantsKeys.StartLongitude,     logObj.getString(ConstantsKeys.StartLongitude));
                driverLogJson.put(ConstantsKeys.EndLatitude,        logObj.getString(ConstantsKeys.EndLatitude));
                driverLogJson.put(ConstantsKeys.EndLongitude,       logObj.getString(ConstantsKeys.EndLongitude));

                driverLogJson.put(ConstantsKeys.YardMove,           logObj.getBoolean(ConstantsKeys.YardMove));
                driverLogJson.put(ConstantsKeys.Personal,           logObj.getBoolean(ConstantsKeys.Personal));

                driverLogJson.put(ConstantsKeys.CurrentCycleId,     logObj.getString(ConstantsKeys.CurrentCycleId));

                driverLogJson.put(ConstantsKeys.createdDate,        logObj.getString(ConstantsKeys.createdDate));

                driverLogJson.put(ConstantsKeys.DriverName,         logObj.getString(ConstantsKeys.DriverName));
                driverLogJson.put(ConstantsKeys.Remarks,            logObj.getString(ConstantsKeys.Remarks));
                driverLogJson.put(ConstantsKeys.Trailor,            logObj.getString(ConstantsKeys.Trailor));
                driverLogJson.put(ConstantsKeys.StartLocation,      logObj.getString(ConstantsKeys.StartLocation));
                driverLogJson.put(ConstantsKeys.EndLocation,        logObj.getString(ConstantsKeys.EndLocation));
                driverLogJson.put(ConstantsKeys.Truck,              logObj.getString(ConstantsKeys.Truck));

                if(logObj.has(ConstantsKeys.IsStatusAutomatic))
                    IsStatusAutomatic = logObj.getString(ConstantsKeys.IsStatusAutomatic);

                if(logObj.has(ConstantsKeys.OBDSpeed))
                    OBDSpeed = logObj.getString(ConstantsKeys.OBDSpeed);

                if(logObj.has(ConstantsKeys.GPSSpeed))
                    GPSSpeed = logObj.getString(ConstantsKeys.GPSSpeed);

                if(logObj.has(ConstantsKeys.PlateNumber))
                    PlateNumber = logObj.getString(ConstantsKeys.PlateNumber);

                if(logObj.has(ConstantsKeys.IsShortHaulException))
                    HaulHourException = logObj.getString(ConstantsKeys.IsShortHaulException);

                if(logObj.has(ConstantsKeys.IsShortHaulUpdate))
                    IsShortHaulUpdate = logObj.getString(ConstantsKeys.IsShortHaulUpdate);



                if(logObj.has(ConstantsKeys.DecesionSource))
                    DecesionSource = logObj.getString(ConstantsKeys.DecesionSource);

                if (logObj.has(ConstantsKeys.IsAdverseException )) {
                    isAdverseException = logObj.getString(ConstantsKeys.IsAdverseException );
                }
                if (logObj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                    adverseExceptionRemark = logObj.getString(ConstantsKeys.AdverseExceptionRemarks);
                }

                driverLogJson.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);
                driverLogJson.put(ConstantsKeys.OBDSpeed,          OBDSpeed);
                driverLogJson.put(ConstantsKeys.GPSSpeed,          GPSSpeed);
                driverLogJson.put(ConstantsKeys.PlateNumber,       PlateNumber);
                driverLogJson.put(ConstantsKeys.IsShortHaulException, HaulHourException);
                driverLogJson.put(ConstantsKeys.IsShortHaulUpdate, IsShortHaulUpdate );

                driverLogJson.put(ConstantsKeys.DecesionSource,    DecesionSource);

                driverLogJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
                driverLogJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);




            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return driverLogJson;
    }


    public JSONObject AddSameStatusJson(JSONObject lastItemJson, DateTime  currentDateTime ,
                                        DateTime currentUTCTime, RulesResponseObject RulesObj, boolean isStatusChange, int status){
        JSONObject sameStatusJson = new JSONObject();
        String IsStatusAutomatic = "false", HaulHourException = "false", IsShortHaulUpdate = "false", OBDSpeed = "0";
        String DecesionSource = "", GPSSpeed = "0", PlateNumber = "";
        String isAdverseException = "", adverseExceptionRemark = "";

        try {
            int DriverLogId = lastItemJson.getInt(ConstantsKeys.DriverLogId) + 1;

            sameStatusJson.put(ConstantsKeys.DriverLogId, DriverLogId);
            sameStatusJson.put(ConstantsKeys.DriverId, lastItemJson.getLong(ConstantsKeys.DriverId));

            sameStatusJson.put(ConstantsKeys.ProjectId, lastItemJson.getInt(ConstantsKeys.ProjectId));

            if(isStatusChange){
                sameStatusJson.put(ConstantsKeys.DriverStatusId, status);
            }else {
                sameStatusJson.put(ConstantsKeys.DriverStatusId, lastItemJson.getInt(ConstantsKeys.DriverStatusId));
            }
            sameStatusJson.put(ConstantsKeys.startDateTime, currentDateTime);
            sameStatusJson.put(ConstantsKeys.endDateTime, currentDateTime);
            sameStatusJson.put(ConstantsKeys.utcStartDateTime, currentUTCTime);
            sameStatusJson.put(ConstantsKeys.utcEndDateTime, currentUTCTime);

            sameStatusJson.put(ConstantsKeys.totalMin, 0);

            sameStatusJson.put(ConstantsKeys.StartLatitude, lastItemJson.getString(ConstantsKeys.StartLatitude));
            sameStatusJson.put(ConstantsKeys.StartLongitude, lastItemJson.getString(ConstantsKeys.StartLongitude));
            sameStatusJson.put(ConstantsKeys.EndLatitude, lastItemJson.getString(ConstantsKeys.EndLatitude));
            sameStatusJson.put(ConstantsKeys.EndLongitude, lastItemJson.getString(ConstantsKeys.EndLongitude));

            sameStatusJson.put(ConstantsKeys.YardMove, lastItemJson.getBoolean(ConstantsKeys.YardMove));
            sameStatusJson.put(ConstantsKeys.Personal, lastItemJson.getBoolean(ConstantsKeys.Personal));

            if(!lastItemJson.getString(ConstantsKeys.CurrentCycleId).equalsIgnoreCase("null")) {
                sameStatusJson.put(ConstantsKeys.CurrentCycleId, lastItemJson.getInt(ConstantsKeys.CurrentCycleId));
            }else{
                sameStatusJson.put(ConstantsKeys.CurrentCycleId, 1);
            }

            if(lastItemJson.has(ConstantsKeys.DriverName)){
                sameStatusJson.put(ConstantsKeys.DriverName, lastItemJson.getString(ConstantsKeys.DriverName));
            }else{
                sameStatusJson.put(ConstantsKeys.DriverName, "");
            }

            sameStatusJson.put(ConstantsKeys.createdDate, currentDateTime);
            sameStatusJson.put(ConstantsKeys.Remarks, lastItemJson.getString(ConstantsKeys.Remarks));
            sameStatusJson.put(ConstantsKeys.Trailor, lastItemJson.getString(ConstantsKeys.Trailor));
            sameStatusJson.put(ConstantsKeys.StartLocation, lastItemJson.getString(ConstantsKeys.StartLocation));
            sameStatusJson.put(ConstantsKeys.EndLocation, lastItemJson.getString(ConstantsKeys.EndLocation));
            sameStatusJson.put(ConstantsKeys.Truck, lastItemJson.getString(ConstantsKeys.Truck));

            if(RulesObj != null) {
                sameStatusJson.put(ConstantsKeys.IsViolation, RulesObj.isViolation());
                sameStatusJson.put(ConstantsKeys.ViolationReason, RulesObj.getViolationReason());
            }else{
                sameStatusJson.put(ConstantsKeys.IsViolation, lastItemJson.getBoolean(ConstantsKeys.IsViolation));
                sameStatusJson.put(ConstantsKeys.ViolationReason,  lastItemJson.getString(ConstantsKeys.ViolationReason));
            }


            if(lastItemJson.has(ConstantsKeys.IsStatusAutomatic))
                IsStatusAutomatic = lastItemJson.getString(ConstantsKeys.IsStatusAutomatic);

            if(lastItemJson.has(ConstantsKeys.OBDSpeed))
                OBDSpeed = lastItemJson.getString(ConstantsKeys.OBDSpeed);

            if(lastItemJson.has(ConstantsKeys.GPSSpeed))
                GPSSpeed = lastItemJson.getString(ConstantsKeys.GPSSpeed);

            if(lastItemJson.has(ConstantsKeys.PlateNumber))
                PlateNumber = lastItemJson.getString(ConstantsKeys.PlateNumber);

            if(lastItemJson.has(ConstantsKeys.IsShortHaulException))
                HaulHourException = lastItemJson.getString(ConstantsKeys.IsShortHaulException);

            if(lastItemJson.has(ConstantsKeys.IsShortHaulUpdate))
                IsShortHaulUpdate = lastItemJson.getString(ConstantsKeys.IsShortHaulUpdate);


            if(lastItemJson.has(ConstantsKeys.DecesionSource))
                DecesionSource = lastItemJson.getString(ConstantsKeys.DecesionSource);

            if (lastItemJson.has(ConstantsKeys.IsAdverseException )) {
                isAdverseException = lastItemJson.getString(ConstantsKeys.IsAdverseException );
            }
            if (lastItemJson.has(ConstantsKeys.AdverseExceptionRemarks)) {
                adverseExceptionRemark = lastItemJson.getString(ConstantsKeys.AdverseExceptionRemarks);
            }
            sameStatusJson.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);
            sameStatusJson.put(ConstantsKeys.OBDSpeed,          OBDSpeed);
            sameStatusJson.put(ConstantsKeys.GPSSpeed,          GPSSpeed);
            sameStatusJson.put(ConstantsKeys.PlateNumber,       PlateNumber);
            sameStatusJson.put(ConstantsKeys.IsShortHaulException, HaulHourException);
            sameStatusJson.put(ConstantsKeys.IsShortHaulUpdate, IsShortHaulUpdate );

            sameStatusJson.put(ConstantsKeys.DecesionSource,    DecesionSource);
            sameStatusJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
            sameStatusJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);


        }catch (Exception e){
            e.printStackTrace();
        }
        return sameStatusJson;
    }

    // Remove All data after 18 days from list
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public JSONArray RemoveOldDateLogFromArray(JSONArray jsonArray, String currentDate){
        JSONArray array = new JSONArray();
        try {
            for(int i = jsonArray.length()-1 ; i >=0 ; i--){
                JSONObject obj = (JSONObject)jsonArray.get(i);

                DateTime currentDateTime = Globally.getDateTimeObj(currentDate, false);
                DateTime oldDateTime = Globally.getDateTimeObj(obj.getString(ConstantsKeys.endDateTime), false);

                int DateDiff = DayDiffSplitMethod(currentDateTime, oldDateTime);

                if(DateDiff > 18){
                    jsonArray.remove(i);
                }else {
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }


    // Remove selected days log from the list
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public JSONArray RemoveSelectedDateLogFromArray(JSONArray jsonArray, String selectedDate){

        try {
            for(int i = jsonArray.length()-1 ; i >=0 ; i--){
                JSONObject obj = (JSONObject)jsonArray.get(i);

                String DateStr = obj.getString(ConstantsKeys.startDateTime);

                DateTime selectedDateTime = Globally.getDateTimeObj(selectedDate, false);
                DateTime oldDateTime = Globally.getDateTimeObj(DateStr, false);

                int DateDiff = DayDiffSplitMethod(selectedDateTime, oldDateTime);

                if(DateDiff == 0){
                    jsonArray.remove(i);
                }else {
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }




    public static int DayDiff(DateTime currentDate, DateTime oldDate){
        long diffInMillis = currentDate.getMillis() - oldDate.getMillis();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = diffInMillis / daysInMilli;
        return (int) elapsedDays;
    }


    public static int DayDiffSplitMethod(DateTime currentDate, DateTime oldDate){
        String cDate = String.valueOf(currentDate);
        String pDate = String.valueOf(oldDate);
        cDate = cDate.split("T")[0]+"T00:00:00";
        pDate = pDate.split("T")[0]+"T00:00:00";
        currentDate = Globally.getDateTimeObj(cDate, false);
        oldDate = Globally.getDateTimeObj(pDate, false);
        long diffInMillis = currentDate.getMillis() - oldDate.getMillis();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = diffInMillis / daysInMilli;
        return (int) elapsedDays;
    }


    private Date yesterday(int diff) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -diff);
        return cal.getTime();
    }


    private String getYesterdayDateString(int diff, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(yesterday(diff));
    }


    //MM/dd/yyyy


  /*  private String getYesterdayUtcDateString(int diff) {
        DateFormat dateFormat = new SimpleDateFormat(Globally.DateFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(yesterday(diff));
    }*/




    // Get Selected Date Array
    public JSONArray GetSelectedDateArray (JSONArray driverLogJsonArray, String DriverId, DateTime selectedDate,  DateTime currentDate,
                                           DateTime UtcCurrentDate, int offsetFromUTC, int daysDiff, DBHelper dbHelper){

        JSONArray parseArray = new JSONArray();
        JSONObject driverLogJson = new JSONObject();
        int CurrentCycleId, diff = 0, diffLast = 0;
        String IsStatusAutomatic = "false", HaulHourException = "false", IsShortHaulUpdate = "false", OBDSpeed = "0";
        String DecesionSource = "", GPSSpeed = "0", PlateNumber = "";
        String isAdverseException = "", adverseExceptionRemark = "";

        boolean IsViolation, isNewEntry = false;
        //  String startDateTimeStr = getYesterdayDateString(1, Globally.DateFormatMMddyyyy) + " 00:00:00"; //MM/dd/yyyy HH:mm:ss
        String lastDateTimeStr  = getYesterdayDateString(1, Globally.DateFormatHalf) + "T23:59:59";
        DateTime startDateTimeNow = Globally.getDateTimeObj(getYesterdayDateString(0, Globally.DateFormatHalf) + "T00:00:00", false);

        try {
            for(int i = 0 ; i < driverLogJsonArray.length() ; i++){
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(i);
                DateTime startDateTime      = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);

                CurrentCycleId = 1;
                IsViolation = false;

                //  diff = DayDiffSplitMethod(currentDate , startDateTime);
                diff = Days.daysBetween(startDateTime.toLocalDate(), currentDate.toLocalDate()).getDays();
                if(diff < daysDiff ) {
                    driverLogJson = new JSONObject();

                    driverLogJson.put(ConstantsKeys.DriverLogId, logObj.getLong(ConstantsKeys.DriverLogId));
                    driverLogJson.put(ConstantsKeys.DriverId, logObj.getLong(ConstantsKeys.DriverId));

                    driverLogJson.put(ConstantsKeys.ProjectId, logObj.getInt(ConstantsKeys.ProjectId));
                    driverLogJson.put(ConstantsKeys.DriverStatusId, logObj.getInt(ConstantsKeys.DriverStatusId));

                    driverLogJson.put(ConstantsKeys.startDateTime, logObj.getString(ConstantsKeys.startDateTime));
                    driverLogJson.put(ConstantsKeys.utcStartDateTime, logObj.getString(ConstantsKeys.utcStartDateTime));


                    if (diff == 0) {
                        if (i == driverLogJsonArray.length() - 1) {
                            DateTime date = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
                            int totalMin = currentDate.getMinuteOfDay() - date.getMinuteOfDay();

                            driverLogJson.put(ConstantsKeys.endDateTime, String.valueOf(currentDate));
                            driverLogJson.put(ConstantsKeys.utcEndDateTime, String.valueOf(UtcCurrentDate));
                            driverLogJson.put(ConstantsKeys.totalMin, totalMin);

                        } else {
                            driverLogJson.put(ConstantsKeys.endDateTime, logObj.getString(ConstantsKeys.endDateTime));
                            driverLogJson.put(ConstantsKeys.utcEndDateTime, logObj.getString(ConstantsKeys.utcEndDateTime));
                            driverLogJson.put(ConstantsKeys.totalMin, logObj.getInt(ConstantsKeys.totalMin));
                        }

                    } else {
                        diffLast = DayDiffSplitMethod(currentDate, Globally.getDateTimeObj(logObj.getString(ConstantsKeys.endDateTime), false));
                        DateTime startDate = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);

                        if (i == driverLogJsonArray.length() - 1) {
                            if (diffLast == 1) {
                                String LastDayUTCDate = Globally.GetUTCFromDate(String.valueOf(startDateTimeNow), offsetFromUTC);
                                double totalMin = Globally.getDateTimeObj(lastDateTimeStr, false).getMinuteOfDay() - startDate.getMinuteOfDay() ;

                                driverLogJson.put(ConstantsKeys.endDateTime, lastDateTimeStr);
                                driverLogJson.put(ConstantsKeys.utcEndDateTime, LastDayUTCDate);
                                driverLogJson.put(ConstantsKeys.totalMin, totalMin);

                                isNewEntry = true;
                            } else {
                                driverLogJson.put(ConstantsKeys.endDateTime, logObj.getString(ConstantsKeys.endDateTime));
                                driverLogJson.put(ConstantsKeys.utcEndDateTime, logObj.getString(ConstantsKeys.utcEndDateTime));
                                driverLogJson.put(ConstantsKeys.totalMin, logObj.getInt(ConstantsKeys.totalMin));
                            }

                        } else {
                            driverLogJson.put(ConstantsKeys.endDateTime, logObj.getString(ConstantsKeys.endDateTime));
                            driverLogJson.put(ConstantsKeys.utcEndDateTime, logObj.getString(ConstantsKeys.utcEndDateTime));
                            driverLogJson.put(ConstantsKeys.totalMin, logObj.getInt(ConstantsKeys.totalMin));
                        }
                    }

                    if(!logObj.isNull(ConstantsKeys.CurrentCycleId)){
                        CurrentCycleId = logObj.getInt(ConstantsKeys.CurrentCycleId);
                    }

                    if(!logObj.isNull(ConstantsKeys.IsViolation)){
                        IsViolation = logObj.getBoolean(ConstantsKeys.IsViolation);
                    }

                    driverLogJson.put(ConstantsKeys.StartLatitude, logObj.getString(ConstantsKeys.StartLatitude));
                    driverLogJson.put(ConstantsKeys.StartLongitude, logObj.getString(ConstantsKeys.StartLongitude));
                    driverLogJson.put(ConstantsKeys.EndLatitude, logObj.getString(ConstantsKeys.EndLatitude));
                    driverLogJson.put(ConstantsKeys.EndLongitude, logObj.getString(ConstantsKeys.EndLongitude));

                    driverLogJson.put(ConstantsKeys.YardMove, logObj.getBoolean(ConstantsKeys.YardMove));
                    driverLogJson.put(ConstantsKeys.Personal, logObj.getBoolean(ConstantsKeys.Personal));

                    driverLogJson.put(ConstantsKeys.CurrentCycleId, CurrentCycleId);
                    driverLogJson.put(ConstantsKeys.IsViolation, IsViolation );

                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.ViolationReason);

                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.createdDate);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.DriverName);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.Remarks);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.Trailor);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.StartLocation);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.EndLocation);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.Truck);


                    if(logObj.has(ConstantsKeys.IsStatusAutomatic))
                        IsStatusAutomatic = logObj.getString(ConstantsKeys.IsStatusAutomatic);

                    if(logObj.has(ConstantsKeys.OBDSpeed))
                        OBDSpeed = logObj.getString(ConstantsKeys.OBDSpeed);

                    if(logObj.has(ConstantsKeys.GPSSpeed))
                        GPSSpeed = logObj.getString(ConstantsKeys.GPSSpeed);

                    if(logObj.has(ConstantsKeys.PlateNumber))
                        PlateNumber = logObj.getString(ConstantsKeys.PlateNumber);

                    if(logObj.has(ConstantsKeys.IsShortHaulException))
                        HaulHourException = logObj.getString(ConstantsKeys.IsShortHaulException);

                    if(logObj.has(ConstantsKeys.IsShortHaulUpdate))
                        IsShortHaulUpdate = logObj.getString(ConstantsKeys.IsShortHaulUpdate);


                    if(logObj.has(ConstantsKeys.DecesionSource))
                        DecesionSource = logObj.getString(ConstantsKeys.DecesionSource);

                    if (logObj.has(ConstantsKeys.IsAdverseException )) {
                        isAdverseException = logObj.getString(ConstantsKeys.IsAdverseException );
                    }
                    if (logObj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                        adverseExceptionRemark = logObj.getString(ConstantsKeys.AdverseExceptionRemarks);
                    }

                    driverLogJson.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);
                    driverLogJson.put(ConstantsKeys.OBDSpeed,          OBDSpeed);
                    driverLogJson.put(ConstantsKeys.GPSSpeed,          GPSSpeed);
                    driverLogJson.put(ConstantsKeys.PlateNumber,       PlateNumber);
                    driverLogJson.put(ConstantsKeys.IsShortHaulException, HaulHourException);
                    driverLogJson.put(ConstantsKeys.IsShortHaulUpdate, IsShortHaulUpdate );

                    driverLogJson.put(ConstantsKeys.DecesionSource,    DecesionSource);

                    driverLogJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
                    driverLogJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);

                    parseArray.put(driverLogJson);

                }
            }

            if(isNewEntry){

                JSONArray array = UpdateJobLastItemInArray(driverLogJsonArray, driverLogJson);
                RulesResponseObject RulesObj = null;
                String LastDayUTCDate = Globally.GetUTCFromDate(String.valueOf(startDateTimeNow), offsetFromUTC);
                JSONObject sameStatusJson = AddSameStatusJson(driverLogJson, startDateTimeNow, Globally.getDateTimeObj(LastDayUTCDate, true),
                        RulesObj, false, 0);
                array.put(sameStatusJson);

                DriverLogHelper(Integer.valueOf(DriverId), dbHelper, array);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return parseArray;
    }


    // Get Selected Date Array
    public JSONArray UpdateLastWithAddNewObject (JSONArray driverLogJsonArray, String DriverId, int offsetFromUTC, DBHelper dbHelper){

        JSONArray parseArray = new JSONArray();
        String IsStatusAutomatic = "false", HaulHourException = "false", IsShortHaulUpdate = "false", OBDSpeed = "0";
        String DecesionSource = "", GPSSpeed = "0", PlateNumber = "";
        String isAdverseException = "", adverseExceptionRemark = "";

        String lastDateTimeStr  = getYesterdayDateString(1, Globally.DateFormatHalf) + "T23:59:59";
        DateTime startDateTimeNow = Globally.getDateTimeObj(getYesterdayDateString(0, Globally.DateFormatHalf) + "T00:00:00", false);
        int CurrentCycleId = 1;
        boolean IsViolation = false;

        try {

            JSONObject logObj = (JSONObject) driverLogJsonArray.get(driverLogJsonArray.length() - 1 );
            // DateTime startDateTime      = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);

            JSONObject driverLogJson = new JSONObject();

            driverLogJson.put(ConstantsKeys.DriverLogId, logObj.getLong(ConstantsKeys.DriverLogId));
            driverLogJson.put(ConstantsKeys.DriverId, logObj.getLong(ConstantsKeys.DriverId));

            driverLogJson.put(ConstantsKeys.ProjectId, logObj.getInt(ConstantsKeys.ProjectId));
            driverLogJson.put(ConstantsKeys.DriverStatusId, logObj.getInt(ConstantsKeys.DriverStatusId));

            driverLogJson.put(ConstantsKeys.startDateTime, logObj.getString(ConstantsKeys.startDateTime));
            driverLogJson.put(ConstantsKeys.utcStartDateTime, logObj.getString(ConstantsKeys.utcStartDateTime));


            DateTime startDate = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
            String LastDayUTCDate = Globally.GetUTCFromDate(String.valueOf(startDateTimeNow), offsetFromUTC);

            double totalMin = Globally.getDateTimeObj(lastDateTimeStr, false).getMinuteOfDay() - startDate.getMinuteOfDay() ;
            driverLogJson.put(ConstantsKeys.endDateTime, lastDateTimeStr);
            driverLogJson.put(ConstantsKeys.utcEndDateTime, LastDayUTCDate);
            driverLogJson.put(ConstantsKeys.totalMin, totalMin);

            if(!logObj.isNull(ConstantsKeys.CurrentCycleId)){
                CurrentCycleId = logObj.getInt(ConstantsKeys.CurrentCycleId);
            }

            if(!logObj.isNull(ConstantsKeys.IsViolation)){
                IsViolation = logObj.getBoolean(ConstantsKeys.IsViolation);
            }

            driverLogJson.put(ConstantsKeys.StartLatitude, logObj.getString(ConstantsKeys.StartLatitude));
            driverLogJson.put(ConstantsKeys.StartLongitude, logObj.getString(ConstantsKeys.StartLongitude));
            driverLogJson.put(ConstantsKeys.EndLatitude, logObj.getString(ConstantsKeys.EndLatitude));
            driverLogJson.put(ConstantsKeys.EndLongitude, logObj.getString(ConstantsKeys.EndLongitude));

            driverLogJson.put(ConstantsKeys.YardMove, logObj.getBoolean(ConstantsKeys.YardMove));
            driverLogJson.put(ConstantsKeys.Personal, logObj.getBoolean(ConstantsKeys.Personal));

            driverLogJson.put(ConstantsKeys.CurrentCycleId, CurrentCycleId);
            driverLogJson.put(ConstantsKeys.IsViolation, IsViolation );

            CheckNullStatus(logObj, driverLogJson, ConstantsKeys.ViolationReason);
            CheckNullStatus(logObj, driverLogJson, ConstantsKeys.createdDate);
            CheckNullStatus(logObj, driverLogJson, ConstantsKeys.DriverName);
            CheckNullStatus(logObj, driverLogJson, ConstantsKeys.Remarks);
            CheckNullStatus(logObj, driverLogJson, ConstantsKeys.Trailor);
            CheckNullStatus(logObj, driverLogJson, ConstantsKeys.StartLocation);
            CheckNullStatus(logObj, driverLogJson, ConstantsKeys.EndLocation);
            CheckNullStatus(logObj, driverLogJson, ConstantsKeys.Truck);

            if(logObj.has(ConstantsKeys.IsStatusAutomatic))
                IsStatusAutomatic = logObj.getString(ConstantsKeys.IsStatusAutomatic);

            if(logObj.has(ConstantsKeys.OBDSpeed))
                OBDSpeed = logObj.getString(ConstantsKeys.OBDSpeed);

            if(logObj.has(ConstantsKeys.GPSSpeed))
                GPSSpeed = logObj.getString(ConstantsKeys.GPSSpeed);

            if(logObj.has(ConstantsKeys.PlateNumber))
                PlateNumber = logObj.getString(ConstantsKeys.PlateNumber);

            if(logObj.has(ConstantsKeys.IsShortHaulException))
                HaulHourException = logObj.getString(ConstantsKeys.IsShortHaulException);

            if(logObj.has(ConstantsKeys.IsShortHaulUpdate))
                IsShortHaulUpdate = logObj.getString(ConstantsKeys.IsShortHaulUpdate);



            if(logObj.has(ConstantsKeys.DecesionSource))
                DecesionSource = logObj.getString(ConstantsKeys.DecesionSource);

            if (logObj.has(ConstantsKeys.IsAdverseException )) {
                isAdverseException = logObj.getString(ConstantsKeys.IsAdverseException );
            }
            if (logObj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                adverseExceptionRemark = logObj.getString(ConstantsKeys.AdverseExceptionRemarks);
            }
            driverLogJson.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);
            driverLogJson.put(ConstantsKeys.OBDSpeed,          OBDSpeed);
            driverLogJson.put(ConstantsKeys.GPSSpeed,          GPSSpeed);
            driverLogJson.put(ConstantsKeys.PlateNumber,       PlateNumber);
            driverLogJson.put(ConstantsKeys.IsShortHaulException, HaulHourException);
            driverLogJson.put(ConstantsKeys.IsShortHaulUpdate, IsShortHaulUpdate );

            driverLogJson.put(ConstantsKeys.DecesionSource,    DecesionSource );

            driverLogJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
            driverLogJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);


            parseArray.put(driverLogJson);


            // Update Last json
            JSONArray array = UpdateJobLastItemInArray(driverLogJsonArray, driverLogJson);


            RulesResponseObject RulesObj = null;
            JSONObject sameStatusJson = AddSameStatusJson(driverLogJson, startDateTimeNow, Globally.getDateTimeObj(LastDayUTCDate, true),
                    RulesObj, false, 0);

            sameStatusJson.put(ConstantsKeys.StartLocation, "Mid Night Event");
            sameStatusJson.put(ConstantsKeys.EndLocation, "Mid Night Event");

            array.put(sameStatusJson);

            DriverLogHelper(Integer.valueOf(DriverId), dbHelper, array);


        }catch (Exception e){
            e.printStackTrace();
        }

        return parseArray;
    }



    // Get Selected Date Array
    public JSONArray GetSingleDateArray (JSONArray driverLogJsonArray, DateTime selectedDate,  DateTime currentDate, DateTime UtcCurrentDate,
                                         boolean IsCurrentDay, int offsetFromUTC){

        JSONArray parseArray = new JSONArray();
        int CurrentCycleId;
        boolean IsViolation;

        try {
            for(int i = 0 ; i < driverLogJsonArray.length() ; i++){
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(i);
                DateTime startDateTime      = Globally.getDateTimeObj(logObj.getString("StartDateTime"), false);
                CurrentCycleId = 1;
                IsViolation = false;
                String IsStatusAutomatic = "false", DecesionSource = "", HaulHourException = "false", IsShortHaulUpdate = "false",
                        OBDSpeed = "0", GPSSpeed = "0", PlateNumber = "";
                String isAdverseException = "", adverseExceptionRemark = "";

                int startDayOfMonth = startDateTime.getDayOfMonth();
                int selectedDayOfMonth = selectedDate.getDayOfMonth();
                int startMonthOfYear = startDateTime.getMonthOfYear();
                int selectedMonthOfYear = selectedDate.getMonthOfYear();

                if(startMonthOfYear == selectedMonthOfYear && startDayOfMonth == selectedDayOfMonth) {
                    JSONObject driverLogJson = new JSONObject();

                    driverLogJson.put(ConstantsKeys.DriverLogId, logObj.getLong(ConstantsKeys.DriverLogId));
                    driverLogJson.put(ConstantsKeys.DriverId, logObj.getLong(ConstantsKeys.DriverId));

                    driverLogJson.put(ConstantsKeys.ProjectId, logObj.getInt(ConstantsKeys.ProjectId));
                    driverLogJson.put(ConstantsKeys.DriverStatusId, logObj.getInt(ConstantsKeys.DriverStatusId));

                    driverLogJson.put(ConstantsKeys.startDateTime, logObj.getString(ConstantsKeys.startDateTime));
                    driverLogJson.put(ConstantsKeys.utcStartDateTime, logObj.getString(ConstantsKeys.utcStartDateTime));



                    //   if( currentDate.getDayOfMonth() == selectedDate.getDayOfMonth()){


                    if(i < driverLogJsonArray.length()-1){

                        String SplitStartDate = logObj.getString(ConstantsKeys.startDateTime).trim().substring(0, 10);
                        String SplitEndDate   = logObj.getString(ConstantsKeys.endDateTime).trim().substring(0, 10);

                        if(!IsCurrentDay && !SplitStartDate.equals(SplitEndDate) ){
                            String lastItemEndDateStr = SplitStartDate + "T23:59:59";
                            DateTime lastItemStartDate = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
                            String UTCEndDate = Globally.GetUTCFromDate(lastItemEndDateStr, offsetFromUTC);
                            DateTime lastItemEndDate = Globally.getDateTimeObj(lastItemEndDateStr, false); //Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);

                            int totalMin = new DateTime(lastItemEndDate).getMinuteOfDay() - lastItemStartDate.getMinuteOfDay();
                            driverLogJson.put(ConstantsKeys.endDateTime, lastItemEndDateStr );
                            driverLogJson.put(ConstantsKeys.utcEndDateTime, UTCEndDate);
                            driverLogJson.put(ConstantsKeys.totalMin, totalMin);

                        }else{
                            driverLogJson.put(ConstantsKeys.endDateTime, logObj.getString(ConstantsKeys.endDateTime));
                            driverLogJson.put(ConstantsKeys.utcEndDateTime, logObj.getString(ConstantsKeys.utcEndDateTime));
                            driverLogJson.put(ConstantsKeys.totalMin, logObj.getInt(ConstantsKeys.totalMin));
                        }

                    }else{

                        String cDate = String.valueOf(currentDate);
                        cDate = cDate.split("T")[0]+"T00:00:00";
                        String cSelecteddate = String.valueOf(selectedDate);

                        DateTime lastCurrentDate = new DateTime(Globally.getDateTimeObj(cDate, false));
                        DateTime selectedDateTime = new DateTime(Globally.getDateTimeObj(cSelecteddate, false));

                        if(DayDiff(lastCurrentDate, selectedDateTime) == 0){
                            DateTime endDateTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
                            DateTime startDate = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
                            int totalMin = endDateTime.getMinuteOfDay() - startDate.getMinuteOfDay();

                            driverLogJson.put(ConstantsKeys.endDateTime, String.valueOf(endDateTime));
                            driverLogJson.put(ConstantsKeys.utcEndDateTime, String.valueOf(UtcCurrentDate));
                            driverLogJson.put(ConstantsKeys.totalMin, totalMin);

                        }else{
                            DateTime date = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
                            int totalMin = currentDate.getMinuteOfDay() - date.getMinuteOfDay();

                            driverLogJson.put(ConstantsKeys.endDateTime, String.valueOf(currentDate));
                            driverLogJson.put(ConstantsKeys.utcEndDateTime, String.valueOf(UtcCurrentDate));
                            driverLogJson.put(ConstantsKeys.totalMin, totalMin);
                        }


                    }


                    if(!logObj.isNull(ConstantsKeys.CurrentCycleId)){
                        CurrentCycleId = logObj.getInt(ConstantsKeys.CurrentCycleId);
                    }

                    if(!logObj.isNull(ConstantsKeys.IsViolation)){
                        IsViolation = logObj.getBoolean(ConstantsKeys.IsViolation);
                    }

                    try {
                        if(logObj.getString(ConstantsKeys.StartLatitude).equalsIgnoreCase("null")){
                            driverLogJson.put(ConstantsKeys.StartLatitude, "");
                            driverLogJson.put(ConstantsKeys.StartLongitude, "");
                        }else{
                            driverLogJson.put(ConstantsKeys.StartLatitude, logObj.getString(ConstantsKeys.StartLatitude));
                            driverLogJson.put(ConstantsKeys.StartLongitude, logObj.getString(ConstantsKeys.StartLongitude));
                        }

                        if(logObj.getString(ConstantsKeys.EndLatitude).equalsIgnoreCase("null")){
                            driverLogJson.put(ConstantsKeys.EndLatitude, "");
                            driverLogJson.put(ConstantsKeys.EndLongitude, "");
                        }else{
                            driverLogJson.put(ConstantsKeys.EndLatitude, logObj.getString(ConstantsKeys.EndLatitude));
                            driverLogJson.put(ConstantsKeys.EndLongitude, logObj.getString(ConstantsKeys.EndLongitude));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    driverLogJson.put(ConstantsKeys.YardMove, logObj.getBoolean(ConstantsKeys.YardMove));
                    driverLogJson.put(ConstantsKeys.Personal, logObj.getBoolean(ConstantsKeys.Personal));

                    driverLogJson.put(ConstantsKeys.CurrentCycleId, CurrentCycleId);
                    driverLogJson.put(ConstantsKeys.IsViolation, IsViolation );


                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.ViolationReason);

                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.createdDate);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.DriverName);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.Remarks);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.Trailor);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.StartLocation);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.EndLocation);
                    CheckNullStatus(logObj, driverLogJson, ConstantsKeys.Truck);


                    if(logObj.has(ConstantsKeys.IsStatusAutomatic))
                        IsStatusAutomatic = logObj.getString(ConstantsKeys.IsStatusAutomatic);

                    if(logObj.has(ConstantsKeys.OBDSpeed))
                        OBDSpeed = logObj.getString(ConstantsKeys.OBDSpeed);

                    if(logObj.has(ConstantsKeys.GPSSpeed))
                        GPSSpeed = logObj.getString(ConstantsKeys.GPSSpeed);

                    if(logObj.has(ConstantsKeys.PlateNumber))
                        PlateNumber = logObj.getString(ConstantsKeys.PlateNumber);

                    if(logObj.has(ConstantsKeys.IsShortHaulException))
                        HaulHourException = logObj.getString(ConstantsKeys.IsShortHaulException);

                    if(logObj.has(ConstantsKeys.IsShortHaulUpdate))
                        IsShortHaulUpdate = logObj.getString(ConstantsKeys.IsShortHaulUpdate);



                    if(logObj.has(ConstantsKeys.DecesionSource))
                        DecesionSource = logObj.getString(ConstantsKeys.DecesionSource);

                    if (logObj.has(ConstantsKeys.IsAdverseException )) {
                        isAdverseException = logObj.getString(ConstantsKeys.IsAdverseException );
                    }
                    if (logObj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                        adverseExceptionRemark = logObj.getString(ConstantsKeys.AdverseExceptionRemarks);
                    }
                    driverLogJson.put(ConstantsKeys.IsStatusAutomatic, IsStatusAutomatic);
                    driverLogJson.put(ConstantsKeys.OBDSpeed,          OBDSpeed);
                    driverLogJson.put(ConstantsKeys.GPSSpeed,          GPSSpeed);
                    driverLogJson.put(ConstantsKeys.PlateNumber,       PlateNumber);

                    driverLogJson.put(ConstantsKeys.IsShortHaulException, HaulHourException);
                    driverLogJson.put(ConstantsKeys.IsShortHaulUpdate, IsShortHaulUpdate );

                    driverLogJson.put(ConstantsKeys.DecesionSource,    DecesionSource );

                    driverLogJson.put(ConstantsKeys.IsAdverseException, isAdverseException);
                    driverLogJson.put(ConstantsKeys.AdverseExceptionRemarks, adverseExceptionRemark);

                    parseArray.put(driverLogJson);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return parseArray;
    }




    // -------- Get Array Before Selected Date --------------
    public JSONArray GetArrayBeforeSelectedDate(JSONArray driverLogJsonArray, DateTime selectedDate){

        JSONArray array = new JSONArray();

        // String cDate = String.valueOf(selectedDate);
        // cDate = cDate.split("T")[0]+"T00:00:00";
        // selectedDate = new DateTime(Globally.getDateTimeObj(cDate, false));

        for(int i = 0 ; i < driverLogJsonArray.length() ; i++){
            JSONObject logObj = null;
            try {
                logObj = (JSONObject) driverLogJsonArray.get(i);
                DateTime startDateTime      = Globally.getDateTimeObj(logObj.getString("StartDateTime"), false);

                if(startDateTime.isBefore(selectedDate)){
                    array.put(driverLogJsonArray.get(i));
                }else{
                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;

    }




    // -------- Get Array Before Selected Date --------------
    public void UpdateAndMaintain18DaysArray(JSONArray driverLog18daysArray, DateTime selectedDate, JSONObject selectedObj, String DriverId, DBHelper dbHelper){


        boolean isEditable = false;
        for(int i = driverLog18daysArray.length()-1 ; i >= 0  ; i--){
            try {
                JSONObject jsonObj18days = (JSONObject) driverLog18daysArray.get(i);
                String startDateStr     = jsonObj18days.getString(ConstantsKeys.startDateTime);
                DateTime startDateTime  = Globally.getDateTimeObj(startDateStr.substring(0, 17) + "00", false);
                Log.d("startDateStr", "startDateStr: " + startDateStr.substring(0, 17) + "00");

                if(selectedDate.isBefore(startDateTime) || startDateTime.equals(selectedDate)) {
                    if(startDateTime.equals(selectedDate)) {
                        isEditable = true;
                        driverLog18daysArray.put(i, selectedObj);
                    }
                }else{
                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(isEditable) {
            dbHelper.UpdateDriverLog(Integer.valueOf(DriverId), driverLog18daysArray);        // UPDATE DRIVER LOG
        }

    }


    // -------- Get Array Before Selected Date --------------
    public JSONArray GetSplitLog(JSONArray driverLogJsonArray, DateTime splitDateTime){

        JSONArray array = new JSONArray();

        // String cDate = String.valueOf(selectedDate);
        // cDate = cDate.split("T")[0]+"T00:00:00";
        // selectedDate = new DateTime(Globally.getDateTimeObj(cDate, false));

        for(int i = 0 ; i < driverLogJsonArray.length() ; i++){
            JSONObject logObj = null;
            try {
                logObj = (JSONObject) driverLogJsonArray.get(i);
                DateTime startDateTime      = Globally.getDateTimeObj(logObj.getString("StartDateTime"), false);

                array.put(driverLogJsonArray.get(i));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;

    }



    void CheckNullStatus(JSONObject json, JSONObject returnJSON, String key){
        try {
            if(!json.isNull(key))
                returnJSON.put(key, json.getString(key));
            else
                returnJSON.put(key, "");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // Get Selected On Duty Time
    public int GetOnDutyTime(JSONArray driverLogJsonArray ){
        int TotalTime = 0;
        int SelectedTime = 0;

        try {
            for(int i = 0 ; i < driverLogJsonArray.length() ; i++){
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(i);
                DateTime startDateTime      = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
                DateTime endDateTime      = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.endDateTime), false);

                String startDateStr     = Globally.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime));
                String endDateStr       = Globally.ConvertDateFormatMMddyyyy(String.valueOf(endDateTime));

                if(i == driverLogJsonArray.length()-1 && !startDateStr.equals(endDateStr)){
                    endDateTime         = Globally.getDateTimeObj(Globally.ConvertDateFormatyyyy_MM_dd(startDateStr) + "T23:59:59", false);
                }

                if(logObj.getInt(ConstantsKeys.DriverStatusId) == EldFragment.ON_DUTY) {
                    SelectedTime = endDateTime.getMinuteOfDay() - startDateTime.getMinuteOfDay();
                    TotalTime = TotalTime + SelectedTime;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return TotalTime;
    }



    // Get Selected Driving Time
    public int GetDrivingTime(JSONArray driverLogJsonArray){
        int TotalTime = 0;
        int SelectedTime = 0;

        try {
            for(int i = 0 ; i < driverLogJsonArray.length() ; i++){
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(i);
                DateTime startDateTime      = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
                DateTime endDateTime      = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.endDateTime), false);

                String startDateStr     = Globally.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime));
                String endDateStr       = Globally.ConvertDateFormatMMddyyyy(String.valueOf(endDateTime));

                if(i == driverLogJsonArray.length()-1 && !startDateStr.equals(endDateStr)){
                    endDateTime         = Globally.getDateTimeObj(Globally.ConvertDateFormatyyyy_MM_dd(startDateStr) + "T23:59:59", false);
                }

                if(logObj.getInt(ConstantsKeys.DriverStatusId) == EldFragment.DRIVING) {
                    SelectedTime = endDateTime.getMinuteOfDay() - startDateTime.getMinuteOfDay();
                    TotalTime = TotalTime + SelectedTime;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return TotalTime;
    }


    // Get Selected Off Duty Time
    public int GetOffDutyTime(JSONArray driverLogJsonArray){
        int TotalTime = 0;
        int SelectedTime = 0;

        try {
            for(int i = 0 ; i < driverLogJsonArray.length() ; i++){
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(i);
                DateTime startDateTime  = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
                DateTime endDateTime    = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.endDateTime), false);

                String startDateStr     = Globally.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime));
                String endDateStr       = Globally.ConvertDateFormatMMddyyyy(String.valueOf(endDateTime));

                if(i == driverLogJsonArray.length()-1 && !startDateStr.equals(endDateStr)){
                    endDateTime         = Globally.getDateTimeObj(Globally.ConvertDateFormatyyyy_MM_dd(startDateStr) + "T23:59:59", false);
                }

                if(logObj.getInt(ConstantsKeys.DriverStatusId) == EldFragment.OFF_DUTY) {
                    SelectedTime = endDateTime.getMinuteOfDay() - startDateTime.getMinuteOfDay();
                    TotalTime = TotalTime + SelectedTime;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return TotalTime;
    }


    // Get Selected Sleeper Time
    public int GetSleeperTime(JSONArray driverLogJsonArray){
        int TotalTime = 0;
        int SelectedTime = 0;

        try {
            for(int i = 0 ; i < driverLogJsonArray.length() ; i++){
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(i);
                DateTime startDateTime      = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
                DateTime endDateTime      = Globally.getDateTimeObj(logObj.getString(ConstantsKeys.endDateTime), false);

                String startDateStr     = Globally.ConvertDateFormatMMddyyyy(String.valueOf(startDateTime));
                String endDateStr       = Globally.ConvertDateFormatMMddyyyy(String.valueOf(endDateTime));

                if(i == driverLogJsonArray.length()-1 && !startDateStr.equals(endDateStr)){
                    endDateTime         = Globally.getDateTimeObj(Globally.ConvertDateFormatyyyy_MM_dd(startDateStr) + "T23:59:59", false);
                }

                if(logObj.getInt(ConstantsKeys.DriverStatusId) == EldFragment.SLEEPER) {
                    SelectedTime = endDateTime.getMinuteOfDay() - startDateTime.getMinuteOfDay();
                    TotalTime = TotalTime + SelectedTime;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return TotalTime;
    }



    /*-------------------- GET DRIVER SAVED LOG -------------------- */
    public List<DriverLog> getSavedLogList(int DriverId, DateTime end, DateTime endUtc, DBHelper dbHelper){
        List<DriverLog> driverLogList = new ArrayList<DriverLog>();
        Cursor rs = dbHelper.getDriverLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.DRIVER_LOG_LIST));

            try {
                JSONArray logArray = new JSONArray(logList);

                driverLogList = GetDriverLogModelList(logArray, end, endUtc);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (!rs.isClosed()) {
            rs.close();
        }

        return driverLogList;

    }

    /*-------------------- GET DRIVER ALL LOGS IN LIST -------------------- */
    public List<DriverLog> GetDriverLogModelList(JSONArray logArray, DateTime end, DateTime endUtc){
        List<DriverLog> driverLogList = new ArrayList<DriverLog>();
        boolean isLastElement;
        try {
            for(int i = 0 ; i < logArray.length() ; i++){
                JSONObject json = (JSONObject)logArray.get(i);

                if(i == logArray.length() - 1)
                    isLastElement = true;
                else
                    isLastElement = false;

                DriverLog driverLogModel = GetSelectedLogModel(json, end, endUtc, isLastElement);
                driverLogList.add(driverLogModel);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return driverLogList;
    }


    /*-------------------- GET DRIVER SELECTED LOG -------------------- */
    public List<DriverLog> getSelectedLogList(int DriverId, DateTime SelectedDate, DBHelper dbHelper){

        List<DriverLog> driverLogList = new ArrayList<DriverLog>();
        Cursor rs = dbHelper.getDriverLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.DRIVER_LOG_LIST));

            try {
                JSONArray logArray = new JSONArray(logList);
                for(int i = 0 ; i < logArray.length() ; i++){
                    JSONObject json = (JSONObject)logArray.get(i);
                    DateTime startDateTime       = Globally.getDateTimeObj(json.getString(ConstantsKeys.startDateTime),  false);
                    DateTime endDateTime         = Globally.getDateTimeObj(json.getString(ConstantsKeys.endDateTime), false);
                    DateTime utcStartDateTime    = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcStartDateTime), true);
                    DateTime utcEndDateTime      = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcEndDateTime), true);

                    if(startDateTime.isBefore(SelectedDate)) {
                        int CurrentCycleId = 1;
                        if (!json.isNull(ConstantsKeys.CurrentCycleId)) {
                            CurrentCycleId = json.getInt(ConstantsKeys.CurrentCycleId);
                        }

                        DriverLog driverLogModel = new DriverLog();
                        driverLogModel.setDriverLogId(json.getLong(ConstantsKeys.DriverLogId));
                        driverLogModel.setDriverId(json.getLong(ConstantsKeys.DriverId));
                        driverLogModel.setProjectId(json.getInt(ConstantsKeys.ProjectId));
                        driverLogModel.setDriverStatusId(json.getInt(ConstantsKeys.DriverStatusId));

                        driverLogModel.setStartDateTime(startDateTime);
                        driverLogModel.setUtcStartDateTime(utcStartDateTime);
                        driverLogModel.setEndDateTime(endDateTime);
                        driverLogModel.setUtcEndDateTime(utcEndDateTime);
                        driverLogModel.setTotalMinutes(json.getDouble(ConstantsKeys.totalMin));

                        driverLogModel.setStartLatitude(json.getString(ConstantsKeys.StartLatitude));
                        driverLogModel.setStartLongitude(json.getString(ConstantsKeys.StartLongitude));
                        driverLogModel.setEndLatitude(json.getString(ConstantsKeys.EndLatitude));
                        driverLogModel.setEndLongitude(json.getString(ConstantsKeys.EndLongitude));

                        driverLogModel.setYardMove(json.getBoolean(ConstantsKeys.YardMove));
                        driverLogModel.setPersonal(json.getBoolean(ConstantsKeys.Personal));

                        driverLogModel.setCurrentCyleId(CurrentCycleId);
                        driverLogModel.setViolation(json.getBoolean(ConstantsKeys.IsViolation));

                        if (!json.isNull(ConstantsKeys.ViolationReason))
                            driverLogModel.setViolationReason(json.getString(ConstantsKeys.ViolationReason));
                        else
                            driverLogModel.setViolationReason("");


                        driverLogModel.setCreatedDate(endDateTime);

                        if(json.has(ConstantsKeys.IsShortHaulException) && !json.isNull(ConstantsKeys.IsShortHaulException))
                            driverLogModel.setIsShortHaulException(json.getBoolean(ConstantsKeys.IsShortHaulException));
                        else
                            driverLogModel.setIsShortHaulException(false);



                        if(json.has(ConstantsKeys.IsAdverseException) && !json.isNull(ConstantsKeys.IsAdverseException))
                            driverLogModel.setIsAdverseException(json.getBoolean(ConstantsKeys.IsAdverseException));
                        else
                            driverLogModel.setIsAdverseException(false);


                        driverLogList.add(driverLogModel);

                    }else{
                        break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (!rs.isClosed()) {
            rs.close();
        }

        return driverLogList;

    }

    /*-------------------- GET DRIVER SELECTED MODEL -------------------- */
    public  DriverLog GetSelectedLogModel( JSONObject json, DateTime end, DateTime endUtc, boolean isLastElement){

        DriverLog driverLogModel = new DriverLog();
        try {
            DateTime startDateTime       = Globally.getDateTimeObj(json.getString(ConstantsKeys.startDateTime),  false);
            DateTime endDateTime         = Globally.getDateTimeObj(json.getString(ConstantsKeys.endDateTime), false);
            DateTime utcStartDateTime    = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcStartDateTime), true);
            DateTime utcEndDateTime      = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcEndDateTime), true);
            int CurrentCycleId = 1;
            if(!json.isNull(ConstantsKeys.CurrentCycleId)){
                CurrentCycleId = json.getInt(ConstantsKeys.CurrentCycleId);
            }


            driverLogModel.setDriverLogId(json.getLong(ConstantsKeys.DriverLogId));
            driverLogModel.setDriverId(json.getLong(ConstantsKeys.DriverId));
            driverLogModel.setProjectId(json.getInt(ConstantsKeys.ProjectId));
            driverLogModel.setDriverStatusId(json.getInt(ConstantsKeys.DriverStatusId));

            driverLogModel.setStartDateTime(startDateTime);
            driverLogModel.setUtcStartDateTime(utcStartDateTime);

            if(isLastElement) {
                driverLogModel.setEndDateTime(end);
                driverLogModel.setUtcEndDateTime(endUtc);
                driverLogModel.setTotalMinutes(end.getMinuteOfDay() - startDateTime.getMinuteOfDay());
            }else{
                driverLogModel.setEndDateTime(endDateTime);
                driverLogModel.setUtcEndDateTime(utcEndDateTime);
                if(json.isNull(ConstantsKeys.totalMin)){
                    driverLogModel.setTotalMinutes(0);
                }else{
                    driverLogModel.setTotalMinutes(json.getDouble(ConstantsKeys.totalMin));
                }

            }
            driverLogModel.setCreatedDate(endDateTime);

            driverLogModel.setStartLatitude(json.getString(ConstantsKeys.StartLatitude));
            driverLogModel.setStartLongitude(json.getString(ConstantsKeys.StartLongitude));
            driverLogModel.setEndLatitude(json.getString(ConstantsKeys.EndLatitude));
            driverLogModel.setEndLongitude(json.getString(ConstantsKeys.EndLongitude));

            driverLogModel.setYardMove(json.getBoolean(ConstantsKeys.YardMove));
            driverLogModel.setPersonal(json.getBoolean(ConstantsKeys.Personal));

            driverLogModel.setCurrentCyleId(CurrentCycleId);


            if(!json.isNull(ConstantsKeys.IsViolation))
                driverLogModel.setViolation(json.getBoolean(ConstantsKeys.IsViolation));
            else
                driverLogModel.setViolation(false);


            if(!json.isNull(ConstantsKeys.ViolationReason))
                driverLogModel.setViolationReason(json.getString(ConstantsKeys.ViolationReason));
            else
                driverLogModel.setViolationReason("");


            if(json.has(ConstantsKeys.IsShortHaulException) && !json.isNull(ConstantsKeys.IsShortHaulException))
                driverLogModel.setIsShortHaulException(json.getBoolean(ConstantsKeys.IsShortHaulException));
            else
                driverLogModel.setIsShortHaulException(false);



            if(json.has(ConstantsKeys.IsAdverseException) && !json.isNull(ConstantsKeys.IsAdverseException))
                driverLogModel.setIsAdverseException(json.getBoolean(ConstantsKeys.IsAdverseException));
            else
                driverLogModel.setIsAdverseException(false);






        }catch (Exception e){
            e.printStackTrace();
        }

        return  driverLogModel;
    }



    /*-------------------- GET DRIVER SELECTED MODEL -------------------- */
    public  DriverLog UpdateSelectedLogModel( JSONObject json, int status, DateTime startTime,  DateTime utcStartTime, DateTime endTime, DateTime endUtc){

        DriverLog driverLogModel = new DriverLog();
        try {
            // DateTime startDateTime       = Globally.getDateTimeObj(json.getString(ConstantsKeys.startDateTime),  false);
            DateTime endDateTime         = Globally.getDateTimeObj(json.getString(ConstantsKeys.endDateTime), false);
            // DateTime utcStartDateTime    = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcStartDateTime), true);

            int CurrentCycleId = 1;
            if(!json.isNull(ConstantsKeys.CurrentCycleId)){
                CurrentCycleId = json.getInt(ConstantsKeys.CurrentCycleId);
            }


            driverLogModel.setDriverLogId(json.getLong(ConstantsKeys.DriverLogId));
            driverLogModel.setDriverId(json.getLong(ConstantsKeys.DriverId));
            driverLogModel.setProjectId(json.getInt(ConstantsKeys.ProjectId));
            driverLogModel.setDriverStatusId(json.getInt(ConstantsKeys.DriverStatusId));

            driverLogModel.setStartDateTime(startTime);
            driverLogModel.setUtcStartDateTime(utcStartTime);

            driverLogModel.setEndDateTime(endTime);
            driverLogModel.setUtcEndDateTime(endUtc);
            driverLogModel.setTotalMinutes(endTime.getMinuteOfDay() - startTime.getMinuteOfDay());


            driverLogModel.setStartLatitude(json.getString(ConstantsKeys.StartLatitude));
            driverLogModel.setStartLongitude(json.getString(ConstantsKeys.StartLongitude));
            driverLogModel.setEndLatitude(json.getString(ConstantsKeys.EndLatitude));
            driverLogModel.setEndLongitude(json.getString(ConstantsKeys.EndLongitude));

            driverLogModel.setYardMove(json.getBoolean(ConstantsKeys.YardMove));
            driverLogModel.setPersonal(json.getBoolean(ConstantsKeys.Personal));

            driverLogModel.setCurrentCyleId(CurrentCycleId);
            driverLogModel.setViolation(json.getBoolean(ConstantsKeys.IsViolation));

            if(!json.isNull(ConstantsKeys.ViolationReason))
                driverLogModel.setViolationReason(json.getString(ConstantsKeys.ViolationReason));
            else
                driverLogModel.setViolationReason("");


            driverLogModel.setCreatedDate(endDateTime);

            if(json.has(ConstantsKeys.IsShortHaulException) && !json.isNull(ConstantsKeys.IsShortHaulException))
                driverLogModel.setIsShortHaulException(json.getBoolean(ConstantsKeys.IsShortHaulException));
            else
                driverLogModel.setIsShortHaulException(false);



            if(json.has(ConstantsKeys.IsAdverseException) && !json.isNull(ConstantsKeys.IsAdverseException))
                driverLogModel.setIsAdverseException(json.getBoolean(ConstantsKeys.IsAdverseException));
            else
                driverLogModel.setIsAdverseException(false);


        }catch (Exception e){
            e.printStackTrace();
        }

        return  driverLogModel;
    }

    /*-------------------- GET DRIVER SAVED LOG -------------------- */
    public List<DriverLog> get3DaysLog(int DriverId, DateTime selectedDate, DateTime endUtc, DBHelper dbHelper){

        List<DriverLog> driverLogList = new ArrayList<DriverLog>();
        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getDriverLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.DRIVER_LOG_LIST));

            try {
                logArray = new JSONArray(logList);
                for(int i = logArray.length()-1 ; i >= 0 ; i--){
                    JSONObject json = (JSONObject)logArray.get(i);
                    DateTime startDateTime      = Globally.getDateTimeObj(json.getString(ConstantsKeys.startDateTime), false);
                    DateTime endDateTime        = Globally.getDateTimeObj(json.getString(ConstantsKeys.endDateTime), false);
                    DateTime utcStartDateTime   = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcStartDateTime), true);
                    DateTime utcEndDateTime     = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcEndDateTime), true);
                    int DateDiff = DayDiffSplitMethod(selectedDate, startDateTime); //end.getDayOfMonth() - startDateTime.getDayOfMonth();

                    if(startDateTime.equals(selectedDate) || startDateTime.isBefore(selectedDate) ) {
                        if (DateDiff < 3) {
                            int CurrentCycleId = 1;
                            if (!json.isNull(ConstantsKeys.CurrentCycleId)) {
                                CurrentCycleId = json.getInt(ConstantsKeys.CurrentCycleId);
                            }

                            DriverLog driverLogModel = new DriverLog();
                            driverLogModel.setDriverLogId(json.getLong(ConstantsKeys.DriverLogId));
                            driverLogModel.setDriverId(json.getLong(ConstantsKeys.DriverId));
                            driverLogModel.setProjectId(json.getInt(ConstantsKeys.ProjectId));
                            driverLogModel.setDriverStatusId(json.getInt(ConstantsKeys.DriverStatusId));

                            driverLogModel.setStartDateTime(startDateTime);
                            driverLogModel.setUtcStartDateTime(utcStartDateTime);

                            if (i == logArray.length() - 1) {
                                driverLogModel.setEndDateTime(selectedDate);
                                driverLogModel.setUtcEndDateTime(endUtc);
                                driverLogModel.setTotalMinutes(selectedDate.getMinuteOfDay() - startDateTime.getMinuteOfDay());
                            } else {
                                driverLogModel.setEndDateTime(endDateTime);
                                driverLogModel.setUtcEndDateTime(utcEndDateTime);
                                driverLogModel.setTotalMinutes(json.getDouble(ConstantsKeys.totalMin));
                            }

                            driverLogModel.setStartLatitude(json.getString(ConstantsKeys.StartLatitude));
                            driverLogModel.setStartLongitude(json.getString(ConstantsKeys.StartLongitude));
                            driverLogModel.setEndLatitude(json.getString(ConstantsKeys.EndLatitude));
                            driverLogModel.setEndLongitude(json.getString(ConstantsKeys.EndLongitude));

                            driverLogModel.setYardMove(json.getBoolean(ConstantsKeys.YardMove));
                            driverLogModel.setPersonal(json.getBoolean(ConstantsKeys.Personal));

                            driverLogModel.setCurrentCyleId(CurrentCycleId);
                            driverLogModel.setViolation(json.getBoolean(ConstantsKeys.IsViolation));
                            if (!json.isNull(ConstantsKeys.ViolationReason))
                                driverLogModel.setViolationReason(json.getString(ConstantsKeys.ViolationReason));
                            else
                                driverLogModel.setViolationReason("");

                            driverLogModel.setCreatedDate(endDateTime);

                            if(json.has(ConstantsKeys.IsShortHaulException) && !json.isNull(ConstantsKeys.IsShortHaulException))
                                driverLogModel.setIsShortHaulException(json.getBoolean(ConstantsKeys.IsShortHaulException));
                            else
                                driverLogModel.setIsShortHaulException(false);



                            if(json.has(ConstantsKeys.IsAdverseException) && !json.isNull(ConstantsKeys.IsAdverseException))
                                driverLogModel.setIsAdverseException(json.getBoolean(ConstantsKeys.IsAdverseException));
                            else
                                driverLogModel.setIsAdverseException(false);


                            driverLogList.add(driverLogModel);

                        }else{
                            break;
                        }
                    }

                }

                Collections.reverse(driverLogList);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (!rs.isClosed()) {
            rs.close();
        }

        return driverLogList;

    }




    /*-------------------- GET DRIVER SAVED LOG -------------------- */
    public List<DriverLog> GetLogAsList(JSONArray logArray){

        List<DriverLog> driverLogList = new ArrayList<DriverLog>();


        try {
            for(int i = 0 ; i < logArray.length() ; i++){
                JSONObject json = (JSONObject)logArray.get(i);
                DateTime startDateTime       = Globally.getDateTimeObj(json.getString(ConstantsKeys.startDateTime),  false);
                DateTime endDateTime         = Globally.getDateTimeObj(json.getString(ConstantsKeys.endDateTime), false);
                DateTime utcStartDateTime    = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcStartDateTime), true);
                DateTime utcEndDateTime      = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcEndDateTime), true);
                int CurrentCycleId = 1; // temp set to check violation

                DriverLog driverLogModel = new DriverLog();
                driverLogModel.setDriverLogId(json.getLong(ConstantsKeys.DriverLogId));
                driverLogModel.setDriverId(json.getLong(ConstantsKeys.DriverId));
                driverLogModel.setProjectId(json.getInt(ConstantsKeys.ProjectId));
                driverLogModel.setDriverStatusId(json.getInt(ConstantsKeys.DriverStatusId));

                driverLogModel.setStartDateTime(startDateTime);
                driverLogModel.setUtcStartDateTime(utcStartDateTime);
                driverLogModel.setEndDateTime(endDateTime);
                driverLogModel.setUtcEndDateTime(utcEndDateTime);

                try {
                    driverLogModel.setTotalMinutes(json.getDouble(ConstantsKeys.totalMin));
                }catch (Exception e){
                    driverLogModel.setTotalMinutes(0);
                    e.printStackTrace();
                }


                driverLogModel.setStartLatitude(json.getString(ConstantsKeys.StartLatitude));
                driverLogModel.setStartLongitude(json.getString(ConstantsKeys.StartLongitude));
                driverLogModel.setEndLatitude(json.getString(ConstantsKeys.EndLatitude));
                driverLogModel.setEndLongitude(json.getString(ConstantsKeys.EndLongitude));

                driverLogModel.setYardMove(json.getBoolean(ConstantsKeys.YardMove));
                driverLogModel.setPersonal(json.getBoolean(ConstantsKeys.Personal));

                driverLogModel.setCurrentCyleId(CurrentCycleId);
                if(json.isNull(ConstantsKeys.IsViolation)){
                    driverLogModel.setViolation(false);
                }else {
                    driverLogModel.setViolation(json.getBoolean(ConstantsKeys.IsViolation));
                }
                driverLogModel.setViolationReason("");
                driverLogModel.setCreatedDate(endDateTime);

                if(json.has(ConstantsKeys.IsShortHaulException) && !json.isNull(ConstantsKeys.IsShortHaulException))
                    driverLogModel.setIsShortHaulException(json.getBoolean(ConstantsKeys.IsShortHaulException));
                else
                    driverLogModel.setIsShortHaulException(false);



                if(json.has(ConstantsKeys.IsAdverseException) && !json.isNull(ConstantsKeys.IsAdverseException))
                    driverLogModel.setIsAdverseException(json.getBoolean(ConstantsKeys.IsAdverseException));
                else
                    driverLogModel.setIsAdverseException(false);



                driverLogList.add(driverLogModel);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return driverLogList;

    }




    public String CheckStringNull(String value){
        if(value.equalsIgnoreCase("null") || value.equalsIgnoreCase("")){
            value = Globally.USA_WORKING_7_DAYS;
        }
        return value;
    }


    public int CalculateJobTime(int previousMin, int currentMin){
        return previousMin + currentMin;
    }



    public RulesResponseObject CheckDriverRule(int eldCyclesId, int JobStatus, DriverDetail oDriverDetail){

        int CANADA_CYCLE_1                       = 1;
        int CANADA_CYCLE_2                       = 2;
        int USA_WORKING_6_DAYS                   = 3;
        int USA_WORKING_7_DAYS                   = 4;

        LocalCalls CallDriverRule = new LocalCalls();

        if(eldCyclesId == CANADA_CYCLE_1 || eldCyclesId == CANADA_CYCLE_2) {  // 1 & 2 stands for CANADA cycle rule

            if (JobStatus == EldFragment.DRIVING ){
                return CallDriverRule.canadaDrivingRules(oDriverDetail);
            }else if(JobStatus == EldFragment.ON_DUTY){
                return CallDriverRule.canadaOnDutyRules(oDriverDetail);
            }else {
                return CallDriverRule.calculateLeftCycleMinutes(oDriverDetail);
            }

        }else if(eldCyclesId == USA_WORKING_6_DAYS || eldCyclesId == USA_WORKING_7_DAYS) { // 3 & 4 stands for USA cycle rule

            if (JobStatus == EldFragment.DRIVING ){
                return CallDriverRule.usaDrivingRules(oDriverDetail);
            }else if(JobStatus == EldFragment.ON_DUTY){
                return CallDriverRule.usaOnDutyRules(oDriverDetail);
            }else {
                return CallDriverRule.calculateLeftCycleMinutes(oDriverDetail);
            }
        }else{
                return CallDriverRule.calculateLeftCycleMinutes(oDriverDetail);
        }
    }


    public RulesResponseObject getRemainingTime(DateTime currentDate, DateTime currentUTCDate,
                                                final int offsetFromUTC, final int eldCyclesId, final boolean isSingleDriver,
                                                int DriverId, int LastStatus, boolean isOldRecord, boolean isHaulException, boolean isAdverseException,
                                                int rulesVersion, DBHelper dbHelper){

        LocalCalls CallDriverRule = new LocalCalls();

        List<DriverLog> oDriverLog3DaysList = get3DaysLog(DriverId, currentDate, currentUTCDate, dbHelper);   // 3 days log list
        //List<DriverLog> oDriverLogDetail = getSavedLogList(DriverId, currentDate, currentUTCDate, dbHelper);    // temp array

        DriverDetail oDriverDetailRemaining = getDriverList(currentDate, currentUTCDate, DriverId,
                offsetFromUTC, eldCyclesId, isSingleDriver, LastStatus, isOldRecord, isHaulException, isAdverseException,
                rulesVersion, oDriverLog3DaysList);  //oDriverLog3DaysList

        //oDriverDetailRemaining.getEldCyclesId()

        return CallDriverRule.calculateDailyMinutes(oDriverDetailRemaining);

    }



    public RulesResponseObject getDailyOffLeftMinutes(DateTime currentDate, DateTime currentUTCDate,
                                                      final int offsetFromUTC, final int eldCyclesId, final boolean isSingleDriver,
                                                      int DriverId, int LastStatus, boolean isOldRecord,
                                                      boolean isHaulException, boolean isAdverseException, int rulesVersion,
                                                      DBHelper dbHelper){

        LocalCalls CallDriverRule = new LocalCalls();
        List<DriverLog> oDriverLog3DaysList = get3DaysLog(DriverId, currentDate, currentUTCDate, dbHelper);
        DriverDetail oDriverDetailRemaining = getDriverList(currentDate, currentUTCDate, DriverId,
                offsetFromUTC, eldCyclesId, isSingleDriver, LastStatus, isOldRecord, isHaulException, isAdverseException,
                rulesVersion, oDriverLog3DaysList);

        return CallDriverRule.calculateDailyOffLeftMinutes(oDriverDetailRemaining);

    }




    public int GetDriverStatus(int DriverId, DBHelper dbHelper){
        int DriverStatus = 1;
        try {
            JSONArray driverLogArray = getSavedLogArray(DriverId, dbHelper);
            if (driverLogArray.length() > 0) {
                JSONObject lastJsonItem = (JSONObject) driverLogArray.get(driverLogArray.length() - 1);
                DriverStatus = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return DriverStatus;
    }


    public boolean CanChangeStatus(int JobStatus, JSONArray logArray, Globally Global, boolean IsPersonal){
        boolean CanChange = true;
        DateTime currentDateTime = new DateTime(Global.getCurrentDate());
        int MinDiff = 0, PrevJobStatus = 0;
        boolean wasPersonal = false;
        try {

            for(int i = logArray.length()-1 ; i >= 0 ; i-- ){
                JSONObject obj = (JSONObject)logArray.get(i);
                DateTime previousDateTime = new DateTime(obj.getString(ConstantsKeys.startDateTime));

                PrevJobStatus   = obj.getInt(ConstantsKeys.DriverStatusId);
                wasPersonal     = obj.getBoolean(ConstantsKeys.Personal);

                MinDiff = currentDateTime.getMinuteOfDay() - previousDateTime.getMinuteOfDay();

                Log.d("diff", "Min Diff: " + MinDiff );

                if(MinDiff > 0 || MinDiff < -1) {
                    break;
                }else{
                    if (PrevJobStatus == JobStatus) {
                        if (PrevJobStatus == Integer.valueOf(Globally.OFF_DUTY)) {
                            if(IsPersonal == wasPersonal){
                                CanChange = false;
                                break;
                            }
                        } else {
                            CanChange = false;
                            break;
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return CanChange;
    }





    /*-------------------- GET DRIVER ALL LOGS IN LIST TO EDIT DRIVER LOG-------------------- */
    public List<DriverLogModel> GetLogModelEditDriver(JSONArray logArray, DateTime end, DateTime endUtc, boolean isCurrentDate,  final int offsetFromUTC){
        List<DriverLogModel> driverLogList = new ArrayList<DriverLogModel>();
        boolean isLastElement;
        try {
            for(int i = 0 ; i < logArray.length() ; i++){
                JSONObject json = (JSONObject)logArray.get(i);

                if(i == logArray.length() - 1 )
                    isLastElement = true;
                else
                    isLastElement = false;

                DriverLogModel driverLogModel = GetSelectedLogModelEdit(json, end, endUtc, isLastElement, isCurrentDate, offsetFromUTC);
                driverLogList.add(driverLogModel);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return driverLogList;
    }



    /*-------------------- GET DRIVER SELECTED MODEL TO EDIT DRIVER -------------------- */
    public  DriverLogModel GetSelectedLogModelEdit( JSONObject json, DateTime end, DateTime endUtc, boolean isLastElement, boolean isCurrentDate, final int offsetFromUTC){

        DriverLogModel driverLogModel = new DriverLogModel();
        String IsStatusAutomatic = "false", DecesionSource = "", OBDSpeed = "0", GPSSpeed = "0", PlateNumber = "";
        String adverseExceptionRemark = "";
        boolean HaulHourException = false, IsAdverseException = false;

        try {
            DateTime startDateTime       = Globally.getDateTimeObj(json.getString(ConstantsKeys.startDateTime),  false);
            DateTime endDateTime         = Globally.getDateTimeObj(json.getString(ConstantsKeys.endDateTime), false);
            DateTime utcStartDateTime    = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcStartDateTime), true);
            DateTime utcEndDateTime      = Globally.getDateTimeObj(json.getString(ConstantsKeys.utcEndDateTime), true);
            int CurrentCycleId = 1;
            if(!json.isNull(ConstantsKeys.CurrentCycleId)){
                CurrentCycleId = json.getInt(ConstantsKeys.CurrentCycleId);
            }


            driverLogModel.setDriverLogId(json.getLong(ConstantsKeys.DriverLogId));
            driverLogModel.setDriverId(json.getLong(ConstantsKeys.DriverId));
            driverLogModel.setProjectId(json.getInt(ConstantsKeys.ProjectId));
            driverLogModel.setDriverStatusId(json.getInt(ConstantsKeys.DriverStatusId));

            driverLogModel.setStartDateTime(startDateTime);
            driverLogModel.setUtcStartDateTime(utcStartDateTime);

            if(isLastElement) {

                if(isCurrentDate) {
                    driverLogModel.setEndDateTime(end);
                    driverLogModel.setUtcEndDateTime(endUtc);
                    driverLogModel.setTotalMinutes(end.getMinuteOfDay() - startDateTime.getMinuteOfDay());

                }else{
                    String endDate = end.minusDays(1).toString();
                    endDate = endDate.substring(0, 11) + "23:59:59";
                    DateTime dateTime = new DateTime(Globally.getDateTimeObj(endDate, false));
                    DateTime endUtcDate =  Globally.getDateTimeObj(Globally.GetUTCFromDate(dateTime.toString(), offsetFromUTC), false);

                    driverLogModel.setEndDateTime(dateTime);
                    driverLogModel.setUtcEndDateTime(endUtcDate);
                    driverLogModel.setTotalMinutes(dateTime.getMinuteOfDay() - startDateTime.getMinuteOfDay());

                }

            }else{
                driverLogModel.setEndDateTime(endDateTime);
                driverLogModel.setUtcEndDateTime(utcEndDateTime);
                driverLogModel.setTotalMinutes(json.getDouble(ConstantsKeys.totalMin));
            }

            driverLogModel.setStartLatitude(json.getString(ConstantsKeys.StartLatitude));
            driverLogModel.setStartLongitude(json.getString(ConstantsKeys.StartLongitude));
            driverLogModel.setEndLatitude(json.getString(ConstantsKeys.EndLatitude));
            driverLogModel.setEndLongitude(json.getString(ConstantsKeys.EndLongitude));

            driverLogModel.setYardMove(json.getBoolean(ConstantsKeys.YardMove));
            driverLogModel.setPersonal(json.getBoolean(ConstantsKeys.Personal));

            driverLogModel.setCurrentCyleId(CurrentCycleId);
            driverLogModel.setViolation(json.getBoolean(ConstantsKeys.IsViolation));

            if(!json.isNull(ConstantsKeys.ViolationReason))
                driverLogModel.setViolationReason(json.getString(ConstantsKeys.ViolationReason));
            else
                driverLogModel.setViolationReason("");


            driverLogModel.setCreatedDate(endDateTime);

            driverLogModel.setDriverName(json.getString(ConstantsKeys.DriverName));
            driverLogModel.setRemarks(json.getString(ConstantsKeys.Remarks));
            driverLogModel.setTrailor(json.getString(ConstantsKeys.Trailor));
            driverLogModel.setStartLocation(json.getString(ConstantsKeys.StartLocation));
            driverLogModel.setEndLocation(json.getString(ConstantsKeys.EndLocation));
            driverLogModel.setTruck(json.getString(ConstantsKeys.Truck));

            if(json.has(ConstantsKeys.IsStatusAutomatic))
                IsStatusAutomatic = json.getString(ConstantsKeys.IsStatusAutomatic);

            if(json.has(ConstantsKeys.OBDSpeed))
                OBDSpeed = json.getString(ConstantsKeys.OBDSpeed);

            if(json.has(ConstantsKeys.GPSSpeed))
                GPSSpeed = json.getString(ConstantsKeys.GPSSpeed);

            if(json.has(ConstantsKeys.PlateNumber))
                PlateNumber = json.getString(ConstantsKeys.PlateNumber);


            if(json.has(ConstantsKeys.IsShortHaulException) && !json.getString(ConstantsKeys.IsShortHaulException).equals("null") )
                HaulHourException = json.getBoolean(ConstantsKeys.IsShortHaulException);

            if(json.has(ConstantsKeys.DecesionSource))
                DecesionSource = json.getString(ConstantsKeys.DecesionSource);

            if (json.has(ConstantsKeys.IsAdverseException )) {
                IsAdverseException = json.getBoolean(ConstantsKeys.IsAdverseException );
            }
            if (json.has(ConstantsKeys.AdverseExceptionRemarks)) {
                adverseExceptionRemark = json.getString(ConstantsKeys.AdverseExceptionRemarks);
            }

            driverLogModel.setIsStatusAutomatic(IsStatusAutomatic);
            driverLogModel.setOBDSpeed(OBDSpeed);
            driverLogModel.setGPSSpeed(GPSSpeed);
            driverLogModel.setPlateNumber(PlateNumber);
            driverLogModel.setHaulException(HaulHourException);
            driverLogModel.setDecesionSource(DecesionSource);

            driverLogModel.setAdverseException(IsAdverseException);
            driverLogModel.setAdverseExceptionRemark(adverseExceptionRemark);


        }catch (Exception e){
            e.printStackTrace();
        }

        return  driverLogModel;
    }



    public JSONArray GetSameArray(JSONArray array){

        JSONArray newArray = new JSONArray();

        for(int i = 0; i < array.length() ; i++){
            try {
                newArray.put(array.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return newArray;
    }


    /* ------------ Convert List To Json Array to Edit Log-------------  */
    public JSONArray ConvertListToJsonArray( List<DriverLogModel> list){

        JSONArray array = new JSONArray();
        for(int i = 0 ; i < list.size() ; i++){
            try {
                DriverLogModel logModel = list.get(i);
                boolean IsPersonal = logModel.isPersonal();
                boolean isYardMove = logModel.isYardMove();
                String remarks = logModel.getRemarks();

                int status = logModel.getDriverStatusId();
                if(status == 5){
                    status = 1;
                    IsPersonal = true;
                }

                if(status == Constants.ON_DUTY && isYardMove){
                    remarks = "Yard Move";
                }

                JSONObject obj =  AddJobInArray(logModel.getDriverLogId(),
                        logModel.getDriverId(),
                        status,
                        logModel.getStartDateTime().toString().substring(0,19),
                        logModel.getEndDateTime().toString().substring(0,19),
                        logModel.getUtcStartDateTime().toString().substring(0,19),
                        logModel.getUtcEndDateTime().toString().substring(0,19),

                        logModel.getTotalMinutes(),

                        logModel.getStartLatitude(),
                        logModel.getStartLongitude(),
                        logModel.getEndLatitude(),
                        logModel.getEndLongitude(),

                        isYardMove,
                        IsPersonal,

                        logModel.getCurrentCyleId(),
                        logModel.isViolation(),
                        logModel.getViolationReason(),
                        logModel.getDriverName(),
                        remarks,
                        logModel.getTrailor(),
                        logModel.getStartLocation(),

                        logModel.getEndLocation(),
                        logModel.getTruck(),
                        logModel.getIsStatusAutomatic(),
                        logModel.getOBDSpeed(),
                        logModel.getGPSSpeed(),
                        logModel.getPlateNumber(),
                        logModel.getIsHaulException(),
                        logModel.getHaulExceptionUpdate(),
                        logModel.getDecesionSource(),
                        ""+logModel.getIsAdverseException(),
                        logModel.getAdverseExceptionRemark()
                );

                array.put(obj);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return array;
    }


    /*-------------------- GET DRIVER LOG MODEL TO EDIT DRIVER-------------------- */
    public  DriverLogModel GetDriverLogModel( JSONObject json, DateTime startDateTime, DateTime startUtcDateTime,
                                              DateTime endDateTime, DateTime endUtcDateTime,
                                              boolean IsOffDutyPermission, boolean IsSleeperPermission,
                                              boolean IsDrivingPermission , boolean IsOnDutyPermission,
                                              boolean IsNewLogAdded){

        DriverLogModel driverLogModel = new DriverLogModel();


        try {

            int CurrentCycleId = 1;
            if(!json.isNull(ConstantsKeys.CurrentCycleId)){
                CurrentCycleId = json.getInt(ConstantsKeys.CurrentCycleId);
            }

            boolean HaulHourException = false, IsAdverseException = false;
            String IsStatusAutomatic = "false", OBDSpeed = "0", GPSSpeed = "0", PlateNumber = "", DecesionSource = "";
            String adverseExceptionRemark = "";

            int status = json.getInt(ConstantsKeys.DriverStatusId); //Integer.valueOf(statusStr);

            if(IsNewLogAdded) {
                status = GetStatusWithPermissionCheck(status, IsOffDutyPermission, IsSleeperPermission, IsDrivingPermission, IsOnDutyPermission);
            }else{
                if(json.has(ConstantsKeys.IsStatusAutomatic))
                    IsStatusAutomatic = json.getString(ConstantsKeys.IsStatusAutomatic);

            }


            if(json.has(ConstantsKeys.OBDSpeed))
                OBDSpeed = json.getString(ConstantsKeys.OBDSpeed);

            if(json.has(ConstantsKeys.GPSSpeed))
                GPSSpeed = json.getString(ConstantsKeys.GPSSpeed);

            if(json.has(ConstantsKeys.PlateNumber))
                PlateNumber = json.getString(ConstantsKeys.PlateNumber);

            if(json.has(ConstantsKeys.IsShortHaulException) && !json.getString(ConstantsKeys.IsShortHaulException).equals("null") )
                HaulHourException = json.getBoolean(ConstantsKeys.IsShortHaulException);

            if(json.has(ConstantsKeys.DecesionSource))
                DecesionSource = json.getString(ConstantsKeys.DecesionSource);

            if (json.has(ConstantsKeys.IsAdverseException )) {
                IsAdverseException = json.getBoolean(ConstantsKeys.IsAdverseException );
            }
            if (json.has(ConstantsKeys.AdverseExceptionRemarks)) {
                adverseExceptionRemark = json.getString(ConstantsKeys.AdverseExceptionRemarks);
            }

            driverLogModel.setDriverLogId(json.getLong(ConstantsKeys.DriverLogId));
            driverLogModel.setDriverId(json.getLong(ConstantsKeys.DriverId));
            driverLogModel.setProjectId(json.getInt(ConstantsKeys.ProjectId));
            driverLogModel.setDriverStatusId(status);

            driverLogModel.setStartDateTime(startDateTime);
            driverLogModel.setUtcStartDateTime(startUtcDateTime);

            driverLogModel.setEndDateTime(endDateTime);
            driverLogModel.setUtcEndDateTime(endUtcDateTime);
            driverLogModel.setTotalMinutes(endDateTime.getMinuteOfDay() - startDateTime.getMinuteOfDay());

            driverLogModel.setStartLatitude(json.getString(ConstantsKeys.StartLatitude));
            driverLogModel.setStartLongitude(json.getString(ConstantsKeys.StartLongitude));
            driverLogModel.setEndLatitude(json.getString(ConstantsKeys.EndLatitude));
            driverLogModel.setEndLongitude(json.getString(ConstantsKeys.EndLongitude));

            driverLogModel.setYardMove(json.getBoolean(ConstantsKeys.YardMove));

            if(status == PERSONAL){
                driverLogModel.setPersonal(true);
            }else {
                driverLogModel.setPersonal(json.getBoolean(ConstantsKeys.Personal));
            }

            if(status == ON_DUTY){
                driverLogModel.setRemarks(json.getString(ConstantsKeys.Remarks));
            }else {
                driverLogModel.setRemarks("");
            }
            driverLogModel.setCurrentCyleId(CurrentCycleId);
            driverLogModel.setViolation(false); //json.getBoolean(ConstantsKeys.IsViolation)

            if(!json.isNull(ConstantsKeys.ViolationReason))
                driverLogModel.setViolationReason(json.getString(ConstantsKeys.ViolationReason));
            else
                driverLogModel.setViolationReason("");

            driverLogModel.setCreatedDate(endDateTime);

            driverLogModel.setDriverName(json.getString(ConstantsKeys.DriverName));
            driverLogModel.setTrailor(json.getString(ConstantsKeys.Trailor));
            driverLogModel.setStartLocation(json.getString(ConstantsKeys.StartLocation));
            driverLogModel.setEndLocation(json.getString(ConstantsKeys.EndLocation));
            driverLogModel.setTruck(json.getString(ConstantsKeys.Truck));

            driverLogModel.setIsStatusAutomatic(IsStatusAutomatic);
            driverLogModel.setOBDSpeed(OBDSpeed);
            driverLogModel.setGPSSpeed(GPSSpeed);
            driverLogModel.setPlateNumber(PlateNumber);

            driverLogModel.setHaulException(HaulHourException);
            driverLogModel.setDecesionSource(DecesionSource);

            driverLogModel.setAdverseException(IsAdverseException);
            driverLogModel.setAdverseExceptionRemark(adverseExceptionRemark);


        }catch (Exception e){
            e.printStackTrace();
        }

        return  driverLogModel;
    }


    public JSONArray offlineSavedJob(JSONArray jsonArray, DateTime selectedDate){

        JSONArray array = new JSONArray();
        for(int i = 0 ; i < jsonArray.length() ; i++){
            try {
                JSONObject obj = (JSONObject)jsonArray.get(i);
                String dateStr = Globally.ConvertDeviceDateTimeFormat(obj.getString(ConstantsKeys.UTCDateTime));
                DateTime dateTime = new DateTime(Globally.getDateTimeObj(dateStr, false) );
                if(dateTime.isBefore(selectedDate)){
                    array.put(obj);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;

    }


    int GetStatusWithPermissionCheck(int status,  boolean IsOffDutyPermission, boolean IsSleeperPermission,
                                     boolean IsDrivingPermission , boolean IsOnDutyPermission){

        switch (status){

            case OFF_DUTY:
                if(!IsOffDutyPermission){
                    if(IsSleeperPermission){
                        status = SLEEPER;
                    }else if (IsDrivingPermission){
                        status = DRIVING;
                    }else {
                        status = ON_DUTY;
                    }
                }
                break;

            case SLEEPER:
                if(!IsSleeperPermission){
                    if(IsOffDutyPermission){
                        status = OFF_DUTY;
                    }else if (IsDrivingPermission){
                        status = DRIVING;
                    }else {
                        status = ON_DUTY;
                    }
                }
                break;

            case DRIVING:
                if(!IsDrivingPermission){
                    if(IsOffDutyPermission){
                        status = OFF_DUTY;
                    }else if(IsSleeperPermission){
                        status = SLEEPER;
                    }else {
                        status = ON_DUTY;
                    }
                }
                break;

            case ON_DUTY:
                if(!IsOnDutyPermission){
                    if(IsOffDutyPermission){
                        status = OFF_DUTY;
                    }else if(IsSleeperPermission){
                        status = SLEEPER;
                    }else {
                        status = DRIVING;
                    }
                }
                break;
        }

        return status;
    }


    public int getSecondLastJobStatus(JSONArray driver18DaysLogArray){

        int jobStatus = -1;

        if(driver18DaysLogArray.length() > 1 ){
            try {
                JSONObject obj = (JSONObject)driver18DaysLogArray.get(driver18DaysLogArray.length()-2);
                jobStatus = obj.getInt(ConstantsKeys.DriverStatusId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jobStatus;

    }


    public int getTimeDiffBwLast2Job(JSONArray driver18DaysLogArray){

        int minDiff = -1;

        if(driver18DaysLogArray.length() > 1 ){
            try {
                JSONObject lastObj              = (JSONObject)driver18DaysLogArray.get(driver18DaysLogArray.length()-1);
                JSONObject secLastObj           = (JSONObject)driver18DaysLogArray.get(driver18DaysLogArray.length()-2);
                String lastJobStartTimeStr      = lastObj.getString(ConstantsKeys.startDateTime);
                String secLastJobStartTimeStr   = secLastObj.getString(ConstantsKeys.startDateTime);

                if (lastJobStartTimeStr.substring(0, 10).equals(secLastJobStartTimeStr.substring(0, 10))) {
                    DateTime lastJobStartTime       = new DateTime(Globally.getDateTimeObj(lastJobStartTimeStr, false) );
                    DateTime secLastJobStartTime    = new DateTime(Globally.getDateTimeObj(secLastJobStartTimeStr, false) );

                    minDiff = lastJobStartTime.getMinuteOfDay() - secLastJobStartTime.getMinuteOfDay();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return minDiff;

    }


    /*===== Save Driver Jobs with Shared Preference to 18 days Array List and in unposted array those will be posted to server======= */
    public void SaveDriversJob(String DRIVER_ID, String DeviceId, String AdverseExceptionRemarks,
                               String decesionSource, boolean isShortHaulUpdate,
                               int DriverType, Constants constants, SharedPref sharedPref,
                               MainDriverEldPref MainDriverPref, CoDriverEldPref CoDriverPref,
                               EldSingleDriverLogPref eldSharedPref, EldCoDriverLogPref coEldSharedPref,
                               SyncingMethod syncingMethod,
                               Globally Global, HelperMethods hMethods, DBHelper dbHelper, Context context ) {

        boolean isViolation = false, IsYardMove = false;
        String address = "", wasViolation = "false", ViolationReason = "", isPersonal = "false";
        String City = "", State = "", Country = "", AddressLine = "", finalRemarks = "", Remarks = "";
        int DRIVER_JOB_STATUS = 1;
        String currentUTCTime = Global.GetCurrentUTCTime();
        String currentUtcTimeDiffFormat = Global.GetCurrentUTCTimeFormat();
        String CurrentCycleId   = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context );
        String  MainDriverName = DriverConst.GetDriverDetails(DriverConst.DriverName, context);
        String CoDriverName = DriverConst.GetCoDriverDetails(DriverConst.CoDriverName, context);
        String DriverCompanyId = DriverConst.GetDriverDetails(DriverConst.CompanyId, context);
        String TrailorNumber = sharedPref.getTrailorNumber(context);
        RulesResponseObject RulesObj;

        boolean isHaulExcptn;
        boolean isAdverseExcptn;
        if (sharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            isHaulExcptn    = sharedPref.get16hrHaulExcptn(context);
            isAdverseExcptn = sharedPref.getAdverseExcptn(context);
        }else{
            isHaulExcptn    = sharedPref.get16hrHaulExcptnCo(context);
            isAdverseExcptn = sharedPref.getAdverseExcptnCo(context);
        }

        String DriverName = "";
        CurrentCycleId = CheckStringNull(CurrentCycleId);
        if (DriverType == Constants.MAIN_DRIVER_TYPE) {
            DriverName = MainDriverName;
        } else {
            DriverName = CoDriverName;
        }

        try {
            JSONArray driverLogArray = getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
            JSONObject lastItemJson = GetLastJsonFromArray(driverLogArray);

            Remarks = lastItemJson.getString(ConstantsKeys.Remarks);
            isPersonal = lastItemJson.getString(ConstantsKeys.Personal);

            DRIVER_JOB_STATUS = lastItemJson.getInt(ConstantsKeys.DriverStatusId);
            IsYardMove = lastItemJson.getBoolean(ConstantsKeys.YardMove);

            if(AdverseExceptionRemarks.length() > 0) {
                if(DRIVER_JOB_STATUS == Constants.ON_DUTY) {
                    finalRemarks = Remarks + ", " + AdverseExceptionRemarks;
                }else{
                    finalRemarks = AdverseExceptionRemarks;
                }
            }
            String location = lastItemJson.getString(ConstantsKeys.StartLocation);

            String[] loc = location.split(", ");
            if (loc.length > 2) {
                int locLength = loc.length - 1;
                Country = loc[locLength];
                State = loc[locLength - 1];

                for (int i = 0; i < locLength - 1; i++) {
                    City = City + " " + loc[i];
                }
                AddressLine = City + ", " + State + ", " + Country;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try{
            // Check violation before save status
            if (DRIVER_JOB_STATUS == Constants.DRIVING || DRIVER_JOB_STATUS == Constants.ON_DUTY) {
                JSONArray logArray = constants.AddNewStatusInList("", ""+DRIVER_JOB_STATUS, "", "no_address",
                        DRIVER_ID, City, State, Country, AddressLine,
                        CurrentCycleId, Remarks, isPersonal, isViolation,
                        "false", String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                        String.valueOf(BackgroundLocationService.GpsVehicleSpeed), sharedPref.GetCurrentTruckPlateNo(context), decesionSource, IsYardMove,
                        Global, isHaulExcptn, isShortHaulUpdate,
                        ""+isAdverseExcptn,
                        AdverseExceptionRemarks,
                        hMethods, dbHelper);


                String CurrentDate = Global.GetCurrentDateTime();
                int rulesVersion = sharedPref.GetRulesVersion(context);
                int offsetFromUTC = (int) Global.GetTimeZoneOffSet();

                List<DriverLog> oDriverLog = GetLogAsList(logArray);
                DriverDetail oDriverDetail1 = getDriverList(new DateTime(CurrentDate), new DateTime(currentUtcTimeDiffFormat),
                        Integer.valueOf(DRIVER_ID), offsetFromUTC, Integer.valueOf(CurrentCycleId), Global.isSingleDriver(context),
                        DRIVER_JOB_STATUS, false, isHaulExcptn,
                        isAdverseExcptn, rulesVersion, oDriverLog);
                RulesObj = CheckDriverRule(Integer.valueOf(CurrentCycleId), DRIVER_JOB_STATUS,
                        oDriverDetail1);

                isViolation = RulesObj.isViolation();
                ViolationReason = RulesObj.getViolationReason();

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        try {

            // Save driver job in array
            EldDataModelNew locationModel = new EldDataModelNew(
                    Global.PROJECT_ID,
                    DRIVER_ID,
                    String.valueOf(DRIVER_JOB_STATUS),

                    ""+IsYardMove,
                    isPersonal,
                    DeviceId,

                    finalRemarks,
                    currentUTCTime,
                    Global.TRUCK_NUMBER,
                    TrailorNumber,
                    DriverCompanyId,
                    DriverName,

                    City,
                    State,
                    Country,
                    wasViolation,
                    ViolationReason,
                    Global.LATITUDE,
                    Global.LONGITUDE,
                    "false", // IsStatusAutomatic is false when mannual job has been done
                    String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                    String.valueOf(BackgroundLocationService.GpsVehicleSpeed),
                    sharedPref.GetCurrentTruckPlateNo(context),
                    String.valueOf(isHaulExcptn),
                    String.valueOf( isShortHaulUpdate),
                    decesionSource,
                    String.valueOf(isAdverseExcptn),
                    AdverseExceptionRemarks

            );



            // Save Model in offline Array
            if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                MainDriverPref.AddDriverLoc(context, locationModel);

                /* ==== Add data in list to show in offline mode ============ */
                EldDriverLogModel logModel = new EldDriverLogModel(DRIVER_JOB_STATUS, "startDateTime", "endDateTime", "totalHours",
                        "currentCycleId", false, currentUtcTimeDiffFormat, currentUtcTimeDiffFormat,
                        "", "", "", Boolean.parseBoolean(isPersonal),
                        isAdverseExcptn, isHaulExcptn);
                eldSharedPref.AddDriverLoc(context, logModel);
            } else {
                CoDriverPref.AddDriverLoc(context, locationModel);

                /* ==== Add data in list to show in offline mode ============ */
                EldDriverLogModel logModel = new EldDriverLogModel(DRIVER_JOB_STATUS, "startDateTime", "endDateTime", "totalHours",
                        "currentCycleId", false, currentUtcTimeDiffFormat, currentUtcTimeDiffFormat,
                        "", "", "", Boolean.parseBoolean(isPersonal),
                        isAdverseExcptn, isHaulExcptn);
                coEldSharedPref.AddDriverLoc(context, logModel);
            }

            // get unsaved posted data if available
            JSONArray DriverJsonArray = GetDriversSavedData(context, DriverType, MainDriverPref, CoDriverPref, constants);
            constants.SaveEldJsonToList(locationModel, DriverJsonArray);   /* Put data as JSON to List */

            // Saved json in synced array which is using in setting page to sync data mannually.
            JSONObject newObj = constants.GetJsonFromList(DriverJsonArray, DriverJsonArray.length() - 1);
            JSONArray savedSyncedArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DRIVER_ID), dbHelper);
            savedSyncedArray.put(newObj);
            syncingMethod.SyncingLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, savedSyncedArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{

            // ============================ Save Job Status in SQLite ==============================
            JSONArray driverLogArray = constants.AddNewStatusInList(DriverName, String.valueOf(DRIVER_JOB_STATUS), ViolationReason, address,
                    DRIVER_ID, City, State, Country, AddressLine,
                    CurrentCycleId, Remarks, isPersonal, isViolation,
                    "false", String.valueOf(BackgroundLocationService.obdVehicleSpeed),
                    String.valueOf(BackgroundLocationService.GpsVehicleSpeed),
                    sharedPref.GetCurrentTruckPlateNo(context), decesionSource, IsYardMove,
                    Global, isHaulExcptn, isShortHaulUpdate,
                    ""+isAdverseExcptn,
                    AdverseExceptionRemarks, hMethods, dbHelper);



            /* ---------------- DB Helper operations (Insert/Update) --------------- */
            DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, driverLogArray);
            BackgroundLocationService.IsAutoChange = false;

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /*===== Get Driver Jobs in Array List======= */
    private JSONArray GetDriversSavedData(Context context, int DriverType, MainDriverEldPref MainDriverPref,
                                          CoDriverEldPref CoDriverPref, Constants constants) {
        int listSize = 0;
        JSONArray DriverJsonArray = new JSONArray();
        List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

        if (DriverType == Constants.MAIN_DRIVER_TYPE) {
            try {
                listSize = MainDriverPref.LoadSavedLoc(context).size();
                tempList = MainDriverPref.LoadSavedLoc(context);
            } catch (Exception e) {
                listSize = 0;
            }
        } else {
            try {
                listSize = CoDriverPref.LoadSavedLoc(context).size();
                tempList = CoDriverPref.LoadSavedLoc(context);
            } catch (Exception e) {
                listSize = 0;
            }
        }

        try {
            if (listSize > 0) {
                for (int i = 0; i < tempList.size(); i++) {
                    EldDataModelNew listModel = tempList.get(i);

                    if (listModel != null) {
                        constants.SaveEldJsonToList(listModel, DriverJsonArray);  /* Put data as JSON to List */
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DriverJsonArray;
    }



}
