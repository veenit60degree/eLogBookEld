package com.local.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.constants.SharedPref;
import com.als.logistic.Globally;
import com.als.logistic.fragment.EldFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OdometerHelperMethod {

    public OdometerHelperMethod() {
        super();
    }


    /*-------------------- GET ODOMETER SAVED Array -------------------- */
    public JSONArray getSavedOdometerArray(int DriverId, DBHelper dbHelper) {

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getOdometerDetails(DriverId);

        if (rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.ODOMETER_LIST));
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

    /* --------------- Insert/ Update Operations --------------- */
    public void OdometerHelper(int driverId, DBHelper dbHelper, JSONArray array) {

        Cursor rs = dbHelper.getOdometerDetails(driverId);

        if (rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateOdometerDetails(driverId, array);     // UPDATE ODOMETER DETAILS
        } else {
            dbHelper.InsertOdometerDetails(driverId, array);       // INSERT ODOMETER DETAILS
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


    //  Add new log as Json in Array
    public JSONObject AddOdometerInArray(String DriverId, String DeviceId,
                                         String VIN, String StartOdometer,
                                         String EndOdometer, String DistanceType,
                                         String CreatedDate, String IsEditOdometer,
                                         String TruckOdometerId, String VehicleNumber,
                                         String DriverStatusId, boolean isPersonal
    ) {

        JSONObject jsonObj = new JSONObject();

        try {

            jsonObj.put(ConstantsKeys.DriverId, DriverId);
            jsonObj.put(ConstantsKeys.DeviceId, DeviceId);

            jsonObj.put(ConstantsKeys.VIN, VIN);
            jsonObj.put(ConstantsKeys.StartOdometer, StartOdometer);
            jsonObj.put(ConstantsKeys.EndOdometer, EndOdometer);
            jsonObj.put(ConstantsKeys.DistanceType, DistanceType);
            jsonObj.put(ConstantsKeys.IsEditOdometer, IsEditOdometer);
            jsonObj.put(ConstantsKeys.VehicleNumber, VehicleNumber);

            if (IsEditOdometer.equals("true")) {
                jsonObj.put(ConstantsKeys.TruckOdometerId, TruckOdometerId);
            } else {
                jsonObj.put(ConstantsKeys.CreatedDate, CreatedDate);
            }

            if(!DriverStatusId.equals(Globally.OFF_DUTY)) {
                jsonObj.put(ConstantsKeys.DriverStatusID, DriverStatusId);
            }else{
                if(isPersonal){
                    jsonObj.put(ConstantsKeys.DriverStatusID, "5"); // Personal
                }else{
                    jsonObj.put(ConstantsKeys.DriverStatusID, DriverStatusId ); // Off Duty
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;

    }


    public void AddJsonObjectToArray(JSONArray newDataArray, JSONArray oldArray) {
        try {
            for (int i = 0; i < newDataArray.length(); i++) {
                JSONObject odometerJson = (JSONObject) newDataArray.get(i);

                odometerJson.put(ConstantsKeys.DriverId, odometerJson.getString(ConstantsKeys.DriverId));
                odometerJson.put(ConstantsKeys.DeviceId, odometerJson.getString(ConstantsKeys.DeviceId));
                odometerJson.put(ConstantsKeys.VIN, odometerJson.getString(ConstantsKeys.VIN));
                odometerJson.put(ConstantsKeys.StartOdometer, odometerJson.getString(ConstantsKeys.StartOdometer));
                odometerJson.put(ConstantsKeys.EndOdometer, odometerJson.getString(ConstantsKeys.EndOdometer));
                odometerJson.put(ConstantsKeys.DistanceType, odometerJson.getString(ConstantsKeys.DistanceType));
                odometerJson.put(ConstantsKeys.IsEditOdometer, odometerJson.getString(ConstantsKeys.IsEditOdometer));

                if (odometerJson.getString(ConstantsKeys.IsEditOdometer).equals("true")) {
                    odometerJson.put(ConstantsKeys.TruckOdometerId, odometerJson.getString(ConstantsKeys.TruckOdometerId));
                } else {
                    odometerJson.put(ConstantsKeys.CreatedDate, odometerJson.getString(ConstantsKeys.CreatedDate));
                }

                oldArray.put(odometerJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }








    /* =============================== Odometer 18 Days record ============================  */

    /*-------------------- GET ODOMETER SAVED 18 Days Array -------------------- */
    public JSONArray getSavedOdometer18DaysArray(int DriverId, DBHelper dbHelper) {

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getOdometer18DaysDetails(DriverId);

        if (rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            @SuppressLint("Range") String logList = rs.getString(rs.getColumnIndex(DBHelper.ODOMETER_18DAYS_LIST));
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


    /* --------------- Insert/ Update Operations 18 Days Odometer --------------- */
    public void Odometer18DaysHelper(int driverId, DBHelper dbHelper, JSONArray array) {

        Cursor rs = dbHelper.getOdometer18DaysDetails(driverId);

        if (rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateOdometer18DaysDetails(driverId, array);     // UPDATE ODOMETER 18 days DETAILS
        } else {
            dbHelper.InsertOdometer18DaysDetails(driverId, array);       // INSERT ODOMETER 18 days DETAILS
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }


    //  Get Last Json Object
    public JSONObject GetLastJsonObject(JSONArray array, int pos) {

        JSONObject jsonObj = new JSONObject();
        try {
            JSONObject obj = (JSONObject) array.get(array.length() - pos);

            jsonObj.put(ConstantsKeys.DriverId, obj.getString(ConstantsKeys.DriverId));
            jsonObj.put(ConstantsKeys.DeviceId, obj.getString(ConstantsKeys.DeviceId));
            jsonObj.put(ConstantsKeys.VIN, obj.getString(ConstantsKeys.VIN));
            jsonObj.put(ConstantsKeys.StartOdometer, obj.getString(ConstantsKeys.StartOdometer));
            jsonObj.put(ConstantsKeys.EndOdometer, obj.getString(ConstantsKeys.EndOdometer));
            jsonObj.put(ConstantsKeys.DistanceType, obj.getString(ConstantsKeys.DistanceType));
            jsonObj.put(ConstantsKeys.IsEditOdometer, obj.getString(ConstantsKeys.IsEditOdometer));
            jsonObj.put(ConstantsKeys.VehicleNumber, obj.getString(ConstantsKeys.VehicleNumber));

            if (obj.getString(ConstantsKeys.IsEditOdometer).equals("true")) {
                jsonObj.put(ConstantsKeys.TruckOdometerId, obj.getString(ConstantsKeys.TruckOdometerId));
            } else {
                jsonObj.put(ConstantsKeys.CreatedDate, obj.getString(ConstantsKeys.CreatedDate));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return jsonObj;
        }

        return jsonObj;

    }


    //  Get Last Json Object
    public JSONArray GetSelectedDateArray(JSONArray array, String selectedDate) {

        JSONArray selectedDateArray = new JSONArray();
        JSONArray selectedArray = new JSONArray();
        String StartOdometer = "";
        try {
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject obj = (JSONObject) array.get(i);
                String date = "";

                if(obj.has("strOdometerDate"))
                    date = obj.getString("strOdometerDate").split(" ")[0];
                else
                    date = obj.getString("CreatedDate").split(" ")[0];

                if(!Globally.CheckCorrectFormat(date)) {
                    date = Globally.ConvertDateSlashWithHyphen(date);
                }
                if (selectedDate.equals(date)) {
                    selectedDateArray.put(obj);
                }


            }


            for(int j = 0 ; j< selectedDateArray.length() ; j++){
                JSONObject obj = (JSONObject) selectedDateArray.get(j);

                JSONObject jsonObj = new JSONObject();
                jsonObj.put(ConstantsKeys.DriverId,         obj.getString(ConstantsKeys.DriverId));
                jsonObj.put(ConstantsKeys.VIN,              obj.getString(ConstantsKeys.VIN));
                jsonObj.put(ConstantsKeys.DistanceType,     obj.getString(ConstantsKeys.DistanceType));
                jsonObj.put(ConstantsKeys.CreatedDate,      obj.getString(ConstantsKeys.CreatedDate));
                jsonObj.put(ConstantsKeys.VehicleNumber,    obj.getString(ConstantsKeys.VehicleNumber));
                jsonObj.put(ConstantsKeys.DriverStatusID,   obj.getString(ConstantsKeys.DriverStatusID));

                if (obj.has(ConstantsKeys.IsPersonal) && !obj.getString(ConstantsKeys.IsPersonal).equalsIgnoreCase("null")) {
                    jsonObj.put(ConstantsKeys.IsPersonal, obj.getString(ConstantsKeys.IsPersonal));
                }

                if(obj.has("strOdometerDate")) {

                    if(j == selectedDateArray.length() - 1){
                        if (obj.getString(ConstantsKeys.EndOdometer).equalsIgnoreCase("null") ||
                                obj.getString(ConstantsKeys.EndOdometer).equalsIgnoreCase("") ) {
                            jsonObj.put(ConstantsKeys.EndOdometer, "");
                        }else{
                            jsonObj.put(ConstantsKeys.EndOdometer, obj.getString(ConstantsKeys.EndOdometer));
                        }

                        jsonObj.put(ConstantsKeys.StartOdometer, obj.getString(ConstantsKeys.StartOdometer));
                        jsonObj.put(ConstantsKeys.TruckOdometerId, obj.getString(ConstantsKeys.TruckOdometerId));
                        jsonObj.put(ConstantsKeys.TotalMiles, obj.getString(ConstantsKeys.TotalMiles));
                        jsonObj.put(ConstantsKeys.TotalKM, obj.getString(ConstantsKeys.TotalKM));
                        jsonObj.put(ConstantsKeys.TruckEquipmentNumber, obj.getString(ConstantsKeys.TruckEquipmentNumber));
                        selectedArray.put(jsonObj);

                    }else{
                        if (!obj.getString(ConstantsKeys.EndOdometer).equalsIgnoreCase("null") &&
                                !obj.getString(ConstantsKeys.EndOdometer).equalsIgnoreCase("")) {
                            jsonObj.put(ConstantsKeys.StartOdometer, obj.getString(ConstantsKeys.StartOdometer));
                            jsonObj.put(ConstantsKeys.EndOdometer, obj.getString(ConstantsKeys.EndOdometer));
                            jsonObj.put(ConstantsKeys.TruckOdometerId, obj.getString(ConstantsKeys.TruckOdometerId));
                            jsonObj.put(ConstantsKeys.TotalMiles, obj.getString(ConstantsKeys.TotalMiles));
                            jsonObj.put(ConstantsKeys.TotalKM, obj.getString(ConstantsKeys.TotalKM));
                            jsonObj.put(ConstantsKeys.TruckEquipmentNumber, obj.getString(ConstantsKeys.TruckEquipmentNumber));
                            selectedArray.put(jsonObj);
                        }
                    }
                }else {
                    float startOdo = 0;
                    float endOdo = 0;
                    float TotalKM = 0;
                    float TotalMiles = 0;

                    String tempStart = obj.getString(ConstantsKeys.StartOdometer);

                     if(j == 0 & (tempStart.equalsIgnoreCase("null") || tempStart.equals("")) ) {

                     }else{
                        if (j == selectedDateArray.length() - 1) {
                            if (!tempStart.equalsIgnoreCase("null") && !tempStart.equals(""))
                                StartOdometer = tempStart;

                            jsonObj.put(ConstantsKeys.StartOdometer, StartOdometer);
                            jsonObj.put(ConstantsKeys.TruckOdometerId, "");
                            jsonObj.put(ConstantsKeys.TruckEquipmentNumber, "");

                            if (StartOdometer.length() > 0) {

                                if (obj.getString(ConstantsKeys.EndOdometer).equals("") ||
                                        obj.getString(ConstantsKeys.EndOdometer).equalsIgnoreCase("null")) {
                                    jsonObj.put(ConstantsKeys.EndOdometer, "");
                                    jsonObj.put(ConstantsKeys.TotalMiles, "");
                                    jsonObj.put(ConstantsKeys.TotalKM, "");
                                } else {
                                    startOdo = Float.parseFloat(StartOdometer);
                                    endOdo = Float.parseFloat(obj.getString(ConstantsKeys.EndOdometer));
                                    TotalKM = endOdo - startOdo;
                                    TotalMiles = convertKmsToMiles(TotalKM);

                                    jsonObj.put(ConstantsKeys.EndOdometer, obj.getString(ConstantsKeys.EndOdometer));
                                    jsonObj.put(ConstantsKeys.TotalMiles, Convert2DecimalPlaces(TotalMiles));
                                    jsonObj.put(ConstantsKeys.TotalKM, Convert2DecimalPlaces(TotalKM));
                                }
                                selectedArray.put(jsonObj);
                            }


                        } else {
                            String endOdoStr = obj.getString(ConstantsKeys.EndOdometer);
                            if (!endOdoStr.equalsIgnoreCase("null") && endOdoStr.length() > 0 && StartOdometer.length() > 0) {

                                startOdo = Float.parseFloat(StartOdometer);
                                endOdo = Float.parseFloat(endOdoStr);
                                TotalKM = endOdo - startOdo;
                                TotalMiles = convertKmsToMiles(TotalKM);

                                jsonObj.put(ConstantsKeys.StartOdometer, StartOdometer);
                                jsonObj.put(ConstantsKeys.EndOdometer, obj.getString(ConstantsKeys.EndOdometer));
                                jsonObj.put(ConstantsKeys.TruckOdometerId, "");
                                jsonObj.put(ConstantsKeys.TotalMiles, Convert2DecimalPlaces(TotalMiles));
                                jsonObj.put(ConstantsKeys.TotalKM, Convert2DecimalPlaces(TotalKM));
                                jsonObj.put(ConstantsKeys.TruckEquipmentNumber, "");
                                selectedArray.put(jsonObj);

                            }
                        }

                    }
                }


                if(!obj.getString(ConstantsKeys.StartOdometer).equalsIgnoreCase("null") && !obj.getString(ConstantsKeys.StartOdometer).equals(""))
                    StartOdometer = obj.getString(ConstantsKeys.StartOdometer);

            }

        } catch (Exception e) {
            e.printStackTrace();
            return selectedArray;
        }

        return selectedArray;

    }


    // Update current date odometers with updated odomters with server...
    public JSONArray UpdateCurrentDayOdometer(JSONArray currentDayodometerArray, JSONArray final18DaysArray, String currentDate){

        JSONArray selectedArray = new JSONArray();

        try {
            for(int i = 0 ; i < final18DaysArray.length() ; i++){
                JSONObject obj = (JSONObject)final18DaysArray.get(i);
                if(obj.has("strOdometerDate")) {
                    String selectedDate = obj.getString("strOdometerDate").split(" ")[0];
                    if (!currentDate.equals(selectedDate)) {
                        selectedArray.put(obj);
                    }
                }
            }

            for(int i = 0 ; i < currentDayodometerArray.length() ; i++){
                JSONObject objCurrent = (JSONObject)final18DaysArray.get(i);
                    selectedArray.put(objCurrent);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return selectedArray;
    }

    public String Convert2DecimalPlaces(float value){
        return  String.format("%.2f",Double.parseDouble(String.valueOf(value)))  ;
    }




    public float convertKmsToMiles(float kms){
        float miles = (float) (0.621371 * kms);
        return miles;
    }

    //  Get Total miles
    public float GetTotalMiles(JSONArray array) {
        float miles = 0;
        try {
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject obj = (JSONObject) array.get(i);
                if(! obj.getString(ConstantsKeys.TotalMiles).equalsIgnoreCase("null"))
                    miles = miles + Float.parseFloat(obj.getString(ConstantsKeys.TotalMiles));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return miles;
        }
        return miles;
    }


    //  Get Total miles
    public float GetTotalKm(JSONArray array) {
        float km = 0;
        try {
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject obj = (JSONObject) array.get(i);
                String TotalKM = obj.getString(ConstantsKeys.TotalKM);
                if(!TotalKM.equalsIgnoreCase("null") && TotalKM.length() > 0)
                    km = km + Float.parseFloat(TotalKM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return km;
        }
        return km;
    }



    //  Update Last log date time in Array for end date time
    public JSONObject UpdateLastJsonFromArray(JSONArray logArray, String EndOdometer, String DistanceType,
                                              String CreatedDate, String IsEditOdometer,
                                              String TruckOdometerId, String VehicleNumber) {
        JSONObject jsonObj = new JSONObject();
        try {
            if (logArray.length() > 0) {
                JSONObject logObj = (JSONObject) logArray.get(logArray.length() - 1);

                jsonObj.put(ConstantsKeys.DriverId,         logObj.getLong(ConstantsKeys.DriverId));
                jsonObj.put(ConstantsKeys.DeviceId,         logObj.getLong(ConstantsKeys.DeviceId));
                jsonObj.put(ConstantsKeys.VIN,              logObj.getLong(ConstantsKeys.VIN));

                jsonObj.put(ConstantsKeys.StartOdometer,    logObj.getLong(ConstantsKeys.StartOdometer ));
                jsonObj.put(ConstantsKeys.EndOdometer,      EndOdometer);
                jsonObj.put(ConstantsKeys.DistanceType,     DistanceType);
                jsonObj.put(ConstantsKeys.IsEditOdometer,   IsEditOdometer);
                jsonObj.put(ConstantsKeys.VehicleNumber,    VehicleNumber);

                if (IsEditOdometer.equals("true")) {
                    jsonObj.put(ConstantsKeys.TruckOdometerId, TruckOdometerId);
                } else {
                    jsonObj.put(ConstantsKeys.CreatedDate, CreatedDate);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }


    public void AddOdometerAutomatically(String DriverId, String DeviceId, String odometerValue, String DriverStatusId,
                                         DBHelper dbHelper, Context context){

        JSONArray odometer18DaysArray = getSavedOdometer18DaysArray(Integer.valueOf(DriverId), dbHelper);
        try {
            String CreatedDate      = Globally.ConvertDateFormatMMddyyyyHHmm(Globally.GetDriverCurrentDateTime(new Globally(), context));
            String VIN_NUMBER       = SharedPref.getVINNumber(context);
            String TRUCK_NUMBER     = SharedPref.getTruckNumber(context); //DriverConst.GetDriverTripDetails(DriverConst.Truck, context);

            HelperMethods helperMethods   = new HelperMethods();
            JSONArray driverLogArray = helperMethods.getSavedLogArray(Integer.valueOf(DriverId), dbHelper);
            boolean isPersonal = helperMethods.isPersonal(driverLogArray);

            if(odometer18DaysArray.length() > 0) {
                JSONObject lastJsonObj  = GetLastJsonObject(odometer18DaysArray, 1);
                String DistanceType     = lastJsonObj.getString(ConstantsKeys.DistanceType);
                String StartOdometer    = lastJsonObj.getString(ConstantsKeys.StartOdometer);
                String EndOdometer      = lastJsonObj.getString(ConstantsKeys.EndOdometer);

                boolean isUpdate = false;
                if (StartOdometer.length() > 0 && EndOdometer.length() == 0) {
                    EndOdometer = odometerValue;    // end odometer value
                    isUpdate = true;
                } else {
                    StartOdometer = odometerValue;  // Start odometer value
                    EndOdometer = "";
                }


                JSONObject odoJson = AddOdometerInArray(DriverId, DeviceId, VIN_NUMBER, StartOdometer, EndOdometer, DistanceType,
                        CreatedDate, "false", "", TRUCK_NUMBER, DriverStatusId, isPersonal);

                if(isUpdate){
                    odometer18DaysArray.put(odometer18DaysArray.length()-1, odoJson);
                }else{
                    odometer18DaysArray.put(odoJson);
                }


                Odometer18DaysHelper(Integer.valueOf(DriverId), dbHelper, odometer18DaysArray);

            }else{
                JSONObject odoJson = AddOdometerInArray(DriverId, DeviceId, VIN_NUMBER, odometerValue, odometerValue, "km",
                        CreatedDate, "false", "", TRUCK_NUMBER, DriverStatusId, isPersonal);

                odometer18DaysArray.put(odoJson);

                Odometer18DaysHelper(Integer.valueOf(DriverId), dbHelper, odometer18DaysArray);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
