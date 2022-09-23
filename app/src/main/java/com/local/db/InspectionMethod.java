package com.local.db;

import android.database.Cursor;

import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.fragment.EldFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InspectionMethod {


    public InspectionMethod() {
        super();
    }


    /*-------------------- GET Inspection LOG SAVED Array -------------------- */
    public JSONArray getSavedInspectionArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getInspection18Days(DriverId);

        try {
            if (rs != null && rs.getCount() > 0) {
                rs.moveToFirst();
                String logList = rs.getString(rs.getColumnIndex(DBHelper.INSPECTION_18DAYS_LIST));
                try {
                    logArray = new JSONArray(logList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return logArray;

    }

    /*-------------------- Inspection 18 Days DB Helper -------------------- */
    public void DriverInspectionHelper( int driverId, DBHelper dbHelper, JSONArray inspectArray){

        try {
            Cursor rs = dbHelper.getInspection18Days(driverId);
            if(rs != null & rs.getCount() > 0) {
                rs.moveToFirst();
                dbHelper.UpdateInspection18Days(driverId, inspectArray );        // UPDATE DRIVER INSPECTION ARRAY
            }else{
                dbHelper.InsertInspection18Days( driverId, inspectArray  );      // INSERT DRIVER INSPECTION ARRAY
            }
            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }






    /*-------------------- GET SYNC LOG SAVED Array -------------------- */
    public JSONArray getOfflineInspectionsArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getOfflineInspection(DriverId);

        try {
            if (rs != null && rs.getCount() > 0) {
                rs.moveToFirst();
                String logList = rs.getString(rs.getColumnIndex(DBHelper.INSPECTION_OFFLINE_LIST));
                try {
                    logArray = new JSONArray(logList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!rs.isClosed()) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return logArray;

    }



    /*-------------------- Inspection 18 Days DB Helper -------------------- */
    public void DriverOfflineInspectionsHelper( int driverId, DBHelper dbHelper, JSONArray inspectArray){

        Cursor rs = dbHelper.getOfflineInspection(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateInspectionOffline(driverId, inspectArray );        // UPDATE DRIVER OFFLINE INSPECTION ARRAY
        }else{
            dbHelper.InsertInspectionOffline( driverId, inspectArray  );      // INSERT DRIVER OFFLINE INSPECTION ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }





    //  Add new log as Json in 18 days inspection Array
    public JSONObject AddNewInspectionObj(String DriverId, String DeviceId,
                                          String ProjectId, String DriverName,
                                          String CompanyId, String VehicleId,
                                          String TrailorId, String VIN,
                                          String VehicleEquNumber, String TrailorEquNumber ,
                                          String InspectionDateTime, String Location ,
                                          String PreTripInspectionSatisfactory, String PostTripInspectionSatisfactory ,
                                          String AboveDefectsCorrected, String AboveDefectsNotCorrected ,
                                          String Remarks, String Latitude ,
                                          String Longitude, String DriverTimeZone ,
                                          String SupervisorMechanicsName, String TruckIssueType ,
                                          String TraiorIssueType, String InspectionTypeId ,
                                          String ByteDriverSign, String ByteSupervisorSign,
                                          String OdometerInMeters, boolean IsSignCopy, String SignedCopyDate
                                          ){

        JSONObject inspectionJson = new JSONObject();

        try {

            inspectionJson.put(ConstantsKeys.DriverId,                      DriverId );
            inspectionJson.put(ConstantsKeys.DeviceId,                      DeviceId);
            inspectionJson.put(ConstantsKeys.ProjectId,                     ProjectId);
            inspectionJson.put(ConstantsKeys.DriverName,                    DriverName);
            inspectionJson.put(ConstantsKeys.CompanyId,                     CompanyId);

            inspectionJson.put(ConstantsKeys.VehicleId,                     VehicleId );
            inspectionJson.put(ConstantsKeys.TrailorId,                     TrailorId );
            inspectionJson.put(ConstantsKeys.VIN,                           VIN );
            inspectionJson.put(ConstantsKeys.VehicleEquNumber,              VehicleEquNumber );
            inspectionJson.put(ConstantsKeys.TrailorEquNumber,              TrailorEquNumber );

            inspectionJson.put(ConstantsKeys.InspectionDateTime,            InspectionDateTime );
            inspectionJson.put(ConstantsKeys.Location,                      Location );
            inspectionJson.put(ConstantsKeys.PreTripInspectionSatisfactory, PreTripInspectionSatisfactory );
            inspectionJson.put(ConstantsKeys.PostTripInspectionSatisfactory,PostTripInspectionSatisfactory  );
            inspectionJson.put(ConstantsKeys.AboveDefectsCorrected,         AboveDefectsCorrected  );
            inspectionJson.put(ConstantsKeys.AboveDefectsNotCorrected,      AboveDefectsNotCorrected );

            inspectionJson.put(ConstantsKeys.Remarks,                       Remarks );
            inspectionJson.put(ConstantsKeys.Latitude,                      Latitude );
            inspectionJson.put(ConstantsKeys.Longitude,                     Longitude );
            inspectionJson.put(ConstantsKeys.DriverTimeZone,                DriverTimeZone );
            inspectionJson.put(ConstantsKeys.SupervisorMechanicsName,       SupervisorMechanicsName );

            inspectionJson.put(ConstantsKeys.TruckIssueType,                TruckIssueType  );
            inspectionJson.put(ConstantsKeys.TraiorIssueType,               TraiorIssueType  );
            inspectionJson.put(ConstantsKeys.InspectionTypeId,              InspectionTypeId  );

            if(!IsSignCopy) {
                inspectionJson.put(ConstantsKeys.ByteDriverSign, ByteDriverSign);
            }

            inspectionJson.put(ConstantsKeys.IsSignCopy, IsSignCopy);
            inspectionJson.put(ConstantsKeys.SignedCopyDate, SignedCopyDate);

            inspectionJson.put(ConstantsKeys.ByteSupervisorSign ,           ByteSupervisorSign   );
            inspectionJson.put(ConstantsKeys.OdometerInMeters ,             OdometerInMeters   );


        }catch (Exception e){
            e.printStackTrace();
        }

        return inspectionJson;

    }




    public JSONObject Add18DaysObj(JSONObject obj, ArrayList<String> truckIssueNameList, ArrayList<Integer> truckIssueIdList,
                                   ArrayList<String> trailerIssueNameList, ArrayList<Integer> trailerIssueIdList){

        JSONObject finalInspectionObj = new JSONObject();

        try{

            JSONArray truckArray = getIssueArray(truckIssueNameList, truckIssueIdList, 1);
            JSONArray trailerArray = getIssueArray(trailerIssueNameList, trailerIssueIdList, 2);

            finalInspectionObj.put(ConstantsKeys.Inspection, obj);
            finalInspectionObj.put(ConstantsKeys.TruckIssueList, truckArray);
            finalInspectionObj.put(ConstantsKeys.TrailorIssueList, trailerArray);

        }catch (Exception e){
            e.printStackTrace();
        }

        return finalInspectionObj;

    }


    public JSONArray getIssueArray(  ArrayList<String> issueNameList, ArrayList<Integer> issueIdList, int issueType){

        JSONArray issueArray = new JSONArray();

        for(int i = 0 ; i < issueNameList.size() ; i++){
            if(issueNameList.get(i).length() > 0){
                try {
                    JSONObject issueObj = new JSONObject();
                    issueObj.put(ConstantsKeys.InspectionIssueTypeId,   issueIdList.get(i));
                    issueObj.put(ConstantsKeys.IssueName,               issueNameList.get(i));
                    issueObj.put(ConstantsKeys.Type,                    issueType);

                    issueArray.put(issueObj);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return issueArray;
    }



    public JSONArray updateInspectionArray( JSONArray inspectionArray, String location, String time){

        JSONArray selectedArray = new JSONArray();

        try {
            for(int i = 0 ; i < inspectionArray.length() ; i++){
                JSONObject obj = (JSONObject) inspectionArray.get(i);
                JSONObject subInspObj = new JSONObject(obj.getString(ConstantsKeys.Inspection));
                String selectedInspTime = subInspObj.getString(ConstantsKeys.InspectionDateTime);

                if(selectedInspTime.equals(time)){
                    try {
                        JSONObject updatedMainJSONObj = obj;

                        JSONObject updatedSubInspObj = subInspObj;
                        updatedSubInspObj.put(ConstantsKeys.Location, location);

                        updatedMainJSONObj.put(ConstantsKeys.Inspection, updatedSubInspObj);

                        selectedArray.put(updatedMainJSONObj);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    selectedArray.put(obj);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return selectedArray;
    }





}
