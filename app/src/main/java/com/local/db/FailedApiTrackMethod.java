package com.local.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.als.logistic.Globally;
import com.constants.APIs;
import com.constants.Constants;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FailedApiTrackMethod {

    public FailedApiTrackMethod() {
        super();
    }


    /*-------------------- GET failed api track list -------------------- */
    public JSONArray getFailedApiTrackList(DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getFailedApiTrackEvent(Globally.PROJECT_ID_INT);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.FAILED_API_TRACK_LIST));
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

    /*-------------------- Failed Api Track event DB Helper -------------------- */
    public void FailedApiTrackHelper(DBHelper dbHelper, JSONArray trackList){

        Cursor rs = dbHelper.getFailedApiTrackEvent(Globally.PROJECT_ID_INT);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateFailedApiTrackEvent(Globally.PROJECT_ID_INT, trackList );     // UPDATE failed api track list
        }else{
            dbHelper.InsertFailedApiTrack( Globally.PROJECT_ID_INT, trackList  );        // INSERT failed api track list
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


    public void confirmAndSaveApiTrack(DBHelper dbHelper, String api, Globally global, Context context){
        try{
            boolean isAlreadyExistApi = false;
            JSONArray failedApiTrackList = getFailedApiTrackList(dbHelper);

            for (int i = 0; i < failedApiTrackList.length(); i++) {
                JSONObject obj = (JSONObject) failedApiTrackList.get(i);
                String FailedApiName = obj.getString(ConstantsKeys.FailedApiName);
                if(api.equals(FailedApiName)){
                    isAlreadyExistApi = true;

                    int FailedApiCount = obj.getInt(ConstantsKeys.FailedApiCount);
                    updateCountOnly(dbHelper, failedApiTrackList, FailedApiCount+1, i, global, context);

                    break;
                }
            }

            // if api not exist then add new entry in api list
            if(!isAlreadyExistApi){
                JSONObject newApiObj = new JSONObject();
                newApiObj.put(ConstantsKeys.FailedApiName, api);
                newApiObj.put(ConstantsKeys.FailedApiCount, 0);
                newApiObj.put(ConstantsKeys.FailedApiTime, Globally.GetCurrentDateTime(global, context));

                failedApiTrackList.put(newApiObj);

                // update failed api in table
                FailedApiTrackHelper(dbHelper, failedApiTrackList);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void updateCountOnly(DBHelper dbHelper, JSONArray failedApiTrackList, int count, int position,
                                Globally global, Context context){
        try{
            if(failedApiTrackList.length() > position) {
                JSONObject newApiObj = (JSONObject) failedApiTrackList.get(position);
                newApiObj.put(ConstantsKeys.FailedApiCount, count);
                if(count == 0){
                    newApiObj.put(ConstantsKeys.FailedApiTime, Globally.GetCurrentDateTime(global, context));
                }

                failedApiTrackList.put(position, newApiObj);

                // update api count and save in table
                FailedApiTrackHelper(dbHelper, failedApiTrackList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isAllowToCallOrReset(DBHelper dbHelper, String api, boolean isReset, Globally global, Context context){

        boolean isExist = false;
        int Count = 3;
        boolean isAllowToCall = true;
        JSONArray failedApiTrackList = getFailedApiTrackList(dbHelper);

        try {
            for (int i = 0; i < failedApiTrackList.length(); i++) {
                JSONObject obj = (JSONObject) failedApiTrackList.get(i);
                String FailedApiName = obj.getString(ConstantsKeys.FailedApiName);
                if (api.equals(FailedApiName)) {
                    isExist = true;
                    int FailedApiCount = obj.getInt(ConstantsKeys.FailedApiCount);
                    String callTime = obj.getString(ConstantsKeys.FailedApiTime);

                   // DateTime lastApiCallTime = Globally.getDateTimeObj(callTime, false);
                    DateTime currentTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(global, context), false);

                   // long minDiff = Constants.getDateTimeDuration(lastApiCallTime, currentTime).getStandardMinutes();
                    int minDiff = Constants.getTimeDiffInMin(callTime, currentTime);

                    if(isReset){
                        // reset failed api counter after success
                        updateCountOnly(dbHelper, failedApiTrackList, 0, i, global, context);

                    }else {

                        if(is18DaysListApis(api)){
                            Count = 2;
                        }

                        if (FailedApiCount >= Count) {
                            if (minDiff <= 30) {    // stop failed api call for 30 min

                                if(FailedApiCount > 40){
                                    // reset failed api count
                                    updateCountOnly(dbHelper, failedApiTrackList, 0, i, global, context);
                                }else {
                                    // update failed api count
                                    updateCountOnly(dbHelper, failedApiTrackList, FailedApiCount + 1, i, global, context);
                                }
                                isAllowToCall = false;

                            } else {
                                // reset failed api count
                                updateCountOnly(dbHelper, failedApiTrackList, 0, i, global, context);
                            }

                            break;
                        }
                    }
                }
            }

            if (!isExist && isReset){
                // save api call count on request time
                confirmAndSaveApiTrack(dbHelper, api, global, context);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return isAllowToCall;
    }


    public int getCallCount(DBHelper dbHelper, String api){

        int FailedApiCount = 0;
        JSONArray failedApiTrackList = getFailedApiTrackList(dbHelper);

        try {
            for (int i = 0; i < failedApiTrackList.length(); i++) {
                JSONObject obj = (JSONObject) failedApiTrackList.get(i);
                String FailedApiName = obj.getString(ConstantsKeys.FailedApiName);
                if (api.equals(FailedApiName)) {
                    FailedApiCount = obj.getInt(ConstantsKeys.FailedApiCount);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return FailedApiCount;
    }


    public boolean isSuccess(String response){

        try {
            JSONObject obj = new JSONObject(response);
            String Status = obj.getString(ConstantsKeys.Status);

            if (Status.equals("true")) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }


    public String getFailedInputArrayData(String DriverId, String failedApi, JSONArray data, Globally globally, Context context){

        JSONObject obj = new JSONObject();
        try{
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.APIName, failedApi);
          //  obj.put(ConstantsKeys.APIData, data);
            obj.put(ConstantsKeys.IssueDateTime, Globally.GetCurrentDateTime(globally, context));

        }catch (Exception e){
            e.printStackTrace();
        }

        return obj.toString();
    }


    public String getFailedInputObjData(String DriverId, String failedApi, JSONObject data, Globally globally, Context context){

        JSONObject obj = new JSONObject();
        try{
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.APIName, failedApi);
           // obj.put(ConstantsKeys.APIData, data);
            obj.put(ConstantsKeys.IssueDateTime, Globally.GetCurrentDateTime(globally, context));

        }catch (Exception e){
            e.printStackTrace();
        }

        return obj.toString();
    }

    private boolean is18DaysListApis(String api){
        if(api.equals(APIs.GET_DRIVER_LOG_18_DAYS) || api.equals(APIs.GET_DRIVER_LOG_18_DAYS_DETAILS) ||
                api.equals(APIs.GET_OFFLINE_INSPECTION_LIST) || api.equals(APIs.GET_OFFLINE_17_INSPECTION_LIST) ||
                api.equals(APIs.GET_SHIPPING_INFO_OFFLINE) || api.equals(APIs.GET_ODOMETER_OFFLINE)){
            return true;
        }else{
            return false;
        }
    }
}
