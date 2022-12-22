package com.local.db;


import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InspectionHelperMethod {

    public InspectionHelperMethod() {
        super();
    }

    /*-------------------- GET INSPECTION SAVED Array -------------------- */
    public JSONArray getSavedInspectionArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getInspectionDetails(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.INSPECTION_LIST));
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
    public void InspectionHelper( int driverId, DBHelper dbHelper, JSONArray array){

        Cursor rs = dbHelper.getInspectionDetails(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateInspectionDetails(driverId, array );     // UPDATE ODOMETER DETAILS
        }else{
            dbHelper.InsertInspectionDetails( driverId, array  );       // INSERT ODOMETER DETAILS
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }

    //  Add new log as Json in Array
    public JSONObject AddOdometerInArray(String DriverId, String DeviceId,
                                         String ProjectId, String DriverName,
                                         String CompanyId, String VehicleId,
                                         String TrailorId, String VIN,
                                         String VehicleEquNumber, String TrailorEquNumber,
                                         String InspectionDateTime, String Location,
                                         String TruckOdometerId, String CreatedDate,
                                         String PreTripInspectionSatisfactory, String PostTripInspectionSatisfactory,
                                         String AboveDefectsCorrected, String AboveDefectsNotCorrected,
                                         String Remarks, String Latitude,
                                         String Longitude, String DriverTimeZone,
                                         String SupervisorMechanicsName, String TruckIssueType,
                                         String TraiorIssueType, String DriverSign,
                                         String SupervisorSign ){



        JSONObject jsonObj = new JSONObject();

        try {

            jsonObj.put(ConstantsKeys.DriverId,                     DriverId );
            jsonObj.put(ConstantsKeys.DeviceId,                     DeviceId);

            jsonObj.put(ConstantsKeys.ProjectId,                    ProjectId);
            jsonObj.put(ConstantsKeys.DriverName,                   DriverName );
            jsonObj.put(ConstantsKeys.CompanyId,                    CompanyId  );
            jsonObj.put(ConstantsKeys.VehicleId,                    VehicleId );
            jsonObj.put(ConstantsKeys.TrailorId,                    TrailorId);

            jsonObj.put(ConstantsKeys.VIN,                          VIN);
            jsonObj.put(ConstantsKeys.VehicleEquNumber,             VehicleEquNumber );
            jsonObj.put(ConstantsKeys.TrailorEquNumber,             TrailorEquNumber );
            jsonObj.put(ConstantsKeys.InspectionDateTime,           InspectionDateTime);
            jsonObj.put(ConstantsKeys.Location,                     Location);
            jsonObj.put(ConstantsKeys.TruckOdometerId,              TruckOdometerId);
            jsonObj.put(ConstantsKeys.CreatedDate,                  CreatedDate );
            jsonObj.put(ConstantsKeys.PreTripInspectionSatisfactory, PreTripInspectionSatisfactory );
            jsonObj.put(ConstantsKeys.PostTripInspectionSatisfactory, PostTripInspectionSatisfactory  );
            jsonObj.put(ConstantsKeys.AboveDefectsCorrected,        AboveDefectsCorrected  );
            jsonObj.put(ConstantsKeys.AboveDefectsNotCorrected,     AboveDefectsNotCorrected  );
            jsonObj.put(ConstantsKeys.Remarks,                      Remarks );
            jsonObj.put(ConstantsKeys.Latitude,                     Latitude );
            jsonObj.put(ConstantsKeys.Longitude,                    Longitude );
            jsonObj.put(ConstantsKeys.DriverTimeZone,               DriverTimeZone);
            jsonObj.put(ConstantsKeys.SupervisorMechanicsName,      SupervisorMechanicsName  );
            jsonObj.put(ConstantsKeys.TruckIssueType,               TruckIssueType );
            jsonObj.put(ConstantsKeys.TraiorIssueType,              TraiorIssueType );
            jsonObj.put(ConstantsKeys.DriverSign,                   DriverSign );

            if(SupervisorSign.length() > 0)
                jsonObj.put(ConstantsKeys.SupervisorSign,           SupervisorSign );


        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonObj;

    }


}
