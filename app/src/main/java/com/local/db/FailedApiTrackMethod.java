package com.local.db;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.als.logistic.Globally;
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


    public void confirmAndSaveApiTrack(DBHelper dbHelper, String api, boolean isSave){
        try{
            boolean isAlreadyExistApi = false;
            JSONArray failedApiTrackList = getFailedApiTrackList(dbHelper);

            for (int i = 0; i < failedApiTrackList.length(); i++) {
                JSONObject obj = (JSONObject) failedApiTrackList.get(i);
                String FailedApiName = obj.getString(ConstantsKeys.FailedApiName);
                if(api.equals(FailedApiName)){
                    isAlreadyExistApi = true;

                    if(isSave){
                        int FailedApiCount = obj.getInt(ConstantsKeys.FailedApiCount);
                        saveApiCall(dbHelper, failedApiTrackList, api, FailedApiCount+1, i, true);
                    }

                    break;
                }
            }

            // if api not exist then save his entry
            if(!isAlreadyExistApi && isSave){
                saveApiCall(dbHelper, failedApiTrackList, api, 1, 0, false);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void saveApiCall(DBHelper dbHelper, JSONArray failedApiTrackList, String api, int count,
                             int position, boolean isUpdate){
        try{
            JSONObject newApiObj = new JSONObject();
            newApiObj.put(ConstantsKeys.FailedApiName, api);
            newApiObj.put(ConstantsKeys.FailedApiCount, count);
            newApiObj.put(ConstantsKeys.FailedApiTime, Globally.GetCurrentDateTime());

            if(isUpdate){
                failedApiTrackList.put(position, newApiObj);

            }else {
                failedApiTrackList.put(newApiObj);
            }

            // update api count and save in table
            FailedApiTrackHelper(dbHelper, failedApiTrackList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public boolean isAllowToCallOrReset(DBHelper dbHelper, String api, boolean isReset){

        boolean isAllowToCall = true;
        JSONArray failedApiTrackList = getFailedApiTrackList(dbHelper);

        try {
            for (int i = 0; i < failedApiTrackList.length(); i++) {
                JSONObject obj = (JSONObject) failedApiTrackList.get(i);
                String FailedApiName = obj.getString(ConstantsKeys.FailedApiName);
                if (api.equals(FailedApiName)) {
                    int FailedApiCount = obj.getInt(ConstantsKeys.FailedApiCount);
                    String callTime = obj.getString(ConstantsKeys.FailedApiTime);

                    DateTime callDateTime = Globally.getDateTimeObj(callTime, false);
                    long minDiff = Constants.getDateTimeDuration(callDateTime, Globally.GetCurrentJodaDateTime()).getStandardMinutes();

                    if(isReset){
                        // reset failed api counter after success
                        saveApiCall(dbHelper, failedApiTrackList, api, 0, i, true);
                    }else {
                        if (FailedApiCount > 4) {
                            if (minDiff <= 30) {    // stop failed api call for 30 min
                                isAllowToCall = false;
                            } else {
                                // update call time after 1 hour to ignore api call again
                                saveApiCall(dbHelper, failedApiTrackList, api, FailedApiCount + 1, i, true);
                            }

                            break;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isAllowToCall;
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


    public String getFailedInputArrayData(String DriverId, String failedApi, JSONArray data){

        JSONObject obj = new JSONObject();
        try{
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.APIName, failedApi);
            obj.put(ConstantsKeys.APIData, data);
            obj.put(ConstantsKeys.IssueDateTime, Globally.GetCurrentDateTime());

        }catch (Exception e){
            e.printStackTrace();
        }

        return obj.toString();
    }


    public String getFailedInputObjData(String DriverId, String failedApi, JSONObject data){

        JSONObject obj = new JSONObject();
        try{
            obj.put(ConstantsKeys.DriverId, DriverId);
            obj.put(ConstantsKeys.APIName, failedApi);
            obj.put(ConstantsKeys.APIData, data);
            obj.put(ConstantsKeys.IssueDateTime, Globally.GetCurrentDateTime());

        }catch (Exception e){
            e.printStackTrace();
        }

        return obj.toString();
    }

}
