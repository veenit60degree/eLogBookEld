package com.local.db;


import android.database.Cursor;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.constants.Constants;
import com.messaging.logistic.Globally;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecapViewMethod {


    public RecapViewMethod() {
        super();
    }


    /*-------------------- GET RECAP/VIEW LOG SAVED Array -------------------- */
    public JSONArray getSavedRecapView18DaysArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getRecap18DaysDetails(DriverId);
        try {
            if (rs != null && rs.getCount() > 0) {
                rs.moveToFirst();
                String logList = rs.getString(rs.getColumnIndex(DBHelper.RECAP_DATA_18DAYS_LIST));
                try {
                    logArray = new JSONArray(logList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return logArray;

    }


    /*-------------------- Recap/ViewLog 18 Days DB Helper -------------------- */
    public void RecapView18DaysHelper( int driverId, DBHelper dbHelper, JSONArray recapViewArray){

        Cursor rs = dbHelper.getRecap18DaysDetails(driverId);

        try {
            if (rs != null & rs.getCount() > 0) {
                rs.moveToFirst();
                dbHelper.UpdateRecap18DaysDetails(driverId, recapViewArray);        // UPDATE RECAP/VIEW LOG ARRAY
            } else {
                dbHelper.InsertRecap18DaysDetails(driverId, recapViewArray);      // INSERT RECAP/VIEW LOG ARRAY
            }
            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /*-------------------- Get Selected Recap as JSONObject -------------------- */
    public JSONObject GetSelectedRecapData(JSONArray recapViewArray, String date){

        try {
            for(int i = 0 ; i < recapViewArray.length() ; i++){
                JSONObject recapSelectedJson = (JSONObject)recapViewArray.get(i);
                if(date.equals(recapSelectedJson.getString(ConstantsKeys.Date))){
                    return recapSelectedJson;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }



    /*-------------------- Update Selected Date Recap/View Log Array -------------------- */
    public JSONArray UpdateSelectedDateRecapArray(JSONArray recapViewArray, String date, String ImageInByte){

        try {
            for(int i = recapViewArray.length()-1 ; i >= 0  ; i--){
                JSONObject recapSelectedJson = (JSONObject)recapViewArray.get(i);
                String selectedDate = recapSelectedJson.getString(ConstantsKeys.Date);
                if(date.equals(selectedDate) ){
                    recapSelectedJson.put(ConstantsKeys.LogSignImage, "byteImage");
                    recapSelectedJson.put(ConstantsKeys.LogSignImageInByte, ImageInByte);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recapViewArray;
    }


    /*-------------------- Update Selected Date Engine Miles in Log Array -------------------- */
    public JSONArray UpdateSelectedDateEngineMiles(JSONArray recapViewArray, String date, String EngineMiles){

        try {
            for(int i = recapViewArray.length()-1 ; i >= 0  ; i--){
                JSONObject recapSelectedJson = (JSONObject)recapViewArray.get(i);
                String selectedDate = recapSelectedJson.getString(ConstantsKeys.Date);
                if(date.equals(selectedDate) ){
                    recapSelectedJson.put(ConstantsKeys.EngineMileage, EngineMiles);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recapViewArray;
    }



    /*-------------------- Update Selected Date Recap/View Log Array -------------------- */
    public void UpdateTrailerInRecapArray(JSONArray recapViewArray, String date, String Trailer, String DRIVER_ID, DBHelper dbHelper){

        try {

            if(recapViewArray.length() == 0){
                recapViewArray = getSavedRecapView18DaysArray(Integer.valueOf(DRIVER_ID), dbHelper);
            }

            int arrayLength = recapViewArray.length() - 1;
            for(int i = arrayLength ; i >= arrayLength ; i--){
                JSONObject recapSelectedJson = (JSONObject)recapViewArray.get(i);
                String TrailerStr =  recapSelectedJson.getString(ConstantsKeys.Trailor);

                boolean isMatching = false;
                String[] trailerArray = TrailerStr.split(",");
                for(int tr = 0 ; tr < trailerArray.length ; tr++){
                    if(Trailer.equalsIgnoreCase(trailerArray[tr])) {
                        isMatching = true;
                    }
                }
                if(!isMatching) {
                    TrailerStr = TrailerStr +  "," + Trailer ;

                    if(date.equals(recapSelectedJson.getString(ConstantsKeys.Date))){
                        recapSelectedJson.put(ConstantsKeys.Trailor, TrailerStr);
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecapView18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, recapViewArray);

    }


    /*-------------------- Get Trailer Number From Log Array -------------------- */
    public String getTrailerNumberFromArray(JSONArray recapViewArray, String date){
        String TrailerStr = "";

        try {

            for(int i = recapViewArray.length() - 1 ; i >=0  ; i--){
                JSONObject recapSelectedJson = (JSONObject)recapViewArray.get(i);
                if(date.equals(recapSelectedJson.getString(ConstantsKeys.Date))){
                    String trailer = recapSelectedJson.getString(ConstantsKeys.Trailor);
                    trailer = trailer.replaceAll(Constants.NoTrailer, "");

                    TrailerStr = trailer;

                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return TrailerStr;
    }





    /*-------------------- Parse Server Response Of Recap/View Log Array -------------------- */
    public JSONArray ParseServerResponseOfArray(JSONArray recapViewArray){
        JSONArray recapFinalArray = new JSONArray();
        try {
            for(int i = 0 ; i < recapViewArray.length() ; i++){
                JSONObject json = (JSONObject)recapViewArray.get(i);
                JSONObject recapJson = new JSONObject();

               // JSONArray DriverLogArray = new JSONArray(json.getString(ConstantsKeys.DriverLogModel));

               // if(DriverLogArray.length() > 0) {
                    String date = Globally.ConvertDateFormatMMddyyyy(json.getString(ConstantsKeys.Date));
                    String LogSignImage = "";

                    if (!json.getString(ConstantsKeys.LogSignImage).equalsIgnoreCase("null"))
                        LogSignImage = json.getString(ConstantsKeys.LogSignImage);

                    recapJson.put(ConstantsKeys.CycleDaysDriverLogModel, json.getJSONArray(ConstantsKeys.CycleDaysDriverLogModel));

                    recapJson.put(ConstantsKeys.LogSignImage, LogSignImage);
                    recapJson.put(ConstantsKeys.LogSignImageInByte, json.getString(ConstantsKeys.LogSignImageInByte));
                    recapJson.put(ConstantsKeys.CoDriverName, json.getString(ConstantsKeys.CoDriverName));
                    recapJson.put(ConstantsKeys.EngineMileage, json.getString(ConstantsKeys.EngineMileage));
                    recapJson.put(ConstantsKeys.Trailor, json.getString(ConstantsKeys.Trailor));
                    recapJson.put(ConstantsKeys.Truck, json.getString(ConstantsKeys.Truck));
                    recapJson.put(ConstantsKeys.Date, date);

                    recapFinalArray.put(recapJson);
               // }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recapFinalArray;
    }


    /*-------------------- Add Data In Recap/View Log Array -------------------- */

    public JSONArray AddDataInArray(JSONArray recapViewArray, JSONArray updatedData){

        try {
            for(int i = 0 ; i < updatedData.length() ; i++){
                JSONObject json = (JSONObject)updatedData.get(i);
                recapViewArray.put(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recapViewArray;
    }


    /*-------------------- Remove More Then 18Days data from Recap/View Log Array -------------------- */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public JSONArray RemoveMoreThen18DaysRecapArray(JSONArray recapViewArray){

        if(recapViewArray.length() > 18) {
            int count = recapViewArray.length() - 18;
            try {
                for (int i = recapViewArray.length() - 1; i >= 0; i--) {

                    if (i < count) {
                        recapViewArray.remove(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return recapViewArray;
    }



    /*-------------------- Get Last Item Date from Recap/View Log Array -------------------- */
    public String GetLastItemDate(JSONArray recapViewArray){

        String date = "";
        try {
            JSONObject json = (JSONObject)recapViewArray.get(recapViewArray.length() - 1);
            date = json.getString(ConstantsKeys.Date);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return date;
    }



}
