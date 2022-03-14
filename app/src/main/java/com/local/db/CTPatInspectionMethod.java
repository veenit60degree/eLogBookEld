package com.local.db;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CTPatInspectionMethod {


    public CTPatInspectionMethod() {
        super();
    }


    /*-------------------- GET CT-PAT Inspection LOG SAVED Array -------------------- */
    public JSONArray getCtPatUnPostedInspArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getCtPatInspectionLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.CT_PAT_INSPECTION_LIST));
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


    /*-------------------- GET CT-PAT 18 days Inspection LOG SAVED Array -------------------- */
    public JSONArray getCtPat18DaysInspectionArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getCtPatInsp18DaysLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.CT_PAT_INSP_18DAYS_LIST));
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




    /*--------------------  CT-PAT Inspection DB Helper -------------------- */
    public void DriverCtPatUnPostedInspHelper( int driverId, DBHelper dbHelper, JSONArray inspectArray){

        Cursor rs = dbHelper.getCtPatInspectionLog(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateCtPatInspectionLog(driverId, inspectArray );        // UPDATE DRIVER CT-PAT INSPECTION ARRAY
        }else{
            dbHelper.InsertCtPatInspectionLog( driverId, inspectArray  );      // INSERT DRIVER CT-PAT INSPECTION ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }







    /*--------------------  CT-PAT Inspection 18 Days DB Helper -------------------- */
    public void DriverCtPatInsp18DaysHelper( int driverId, DBHelper dbHelper, JSONArray inspectArray){

        Cursor rs = dbHelper.getCtPatInsp18DaysLog(driverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateCtPatInsp18DaysLog(driverId, inspectArray );        // UPDATE DRIVER CT-PAT 18 DAYS INSPECTION ARRAY
        }else{
            dbHelper.InsertCtPatInsp18DaysLog( driverId, inspectArray  );      // INSERT DRIVER CT-PAT 18 DAYS INSPECTION ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }









    //  Add new log as Json in 18 days inspection Array
    public JSONObject AddUnPostedCtPatInspObj(String DriverId, String DeviceId,
                                          String ProjectId, String DriverName,
                                          String CompanyId, String VehicleId, String VIN,
                                          String VehicleEquNumber, String TrailorEquNumber ,
                                          String InspectionDateTime, String ArrivalSealNumber ,
                                          String DepartureSealNumber, String SecurityInspectionPersonName ,
                                          String FollowUpInspectionPersonName, String AffixedSealPersonName ,
                                          String VerificationPersonName, String Latitude ,
                                          String Longitude, String TruckIssueType ,
                                          String TraiorIssueType, String ByteInspectionConductorSign ,
                                          String ByteFollowUpConductorSign, String ByteSealFixerSign ,
                                          String ByteSealVerifierSign,String AgricultureIssueType,String AreaOfInspectionRemarks,
                                          String ContainerIdentification

    ){

        JSONObject inspectionJson = new JSONObject();

        try {

            inspectionJson.put(ConstantsKeys.DriverId,                      DriverId );
            inspectionJson.put(ConstantsKeys.DeviceId,                      DeviceId);
            inspectionJson.put(ConstantsKeys.ProjectId,                     ProjectId);
            inspectionJson.put(ConstantsKeys.DriverName,                    DriverName);

            inspectionJson.put(ConstantsKeys.CompanyId,                     CompanyId);
            inspectionJson.put(ConstantsKeys.VehicleId,                     VehicleId );
            inspectionJson.put(ConstantsKeys.VIN,                           VIN );

            inspectionJson.put(ConstantsKeys.VehicleEquNumber,              VehicleEquNumber );
            inspectionJson.put(ConstantsKeys.TrailorEquNumber,              TrailorEquNumber );
            inspectionJson.put(ConstantsKeys.InspectionDateTime,            InspectionDateTime );

            inspectionJson.put(ConstantsKeys.ArrivalSealNumber,             ArrivalSealNumber );
            inspectionJson.put(ConstantsKeys.DepartureSealNumber,           DepartureSealNumber );

            inspectionJson.put(ConstantsKeys.SecurityInspectionPersonName,  SecurityInspectionPersonName  );
            inspectionJson.put(ConstantsKeys.FollowUpInspectionPersonName,  FollowUpInspectionPersonName  );
            inspectionJson.put(ConstantsKeys.AffixedSealPersonName,         AffixedSealPersonName );
            inspectionJson.put(ConstantsKeys.VerificationPersonName,        VerificationPersonName );

            inspectionJson.put(ConstantsKeys.Latitude,                      Latitude );
            inspectionJson.put(ConstantsKeys.Longitude,                     Longitude );

            inspectionJson.put(ConstantsKeys.TruckIssueType,                TruckIssueType  );
            inspectionJson.put(ConstantsKeys.TraiorIssueType,               TraiorIssueType  );

            inspectionJson.put(ConstantsKeys.ByteInspectionConductorSign,   ByteInspectionConductorSign  );
            inspectionJson.put(ConstantsKeys.ByteFollowUpConductorSign,     ByteFollowUpConductorSign  );
            inspectionJson.put(ConstantsKeys.ByteSealFixerSign ,            ByteSealFixerSign   );
            inspectionJson.put(ConstantsKeys.ByteSealVerifierSign ,         ByteSealVerifierSign   );
            inspectionJson.put(ConstantsKeys.AgricultureIssueType ,            AgricultureIssueType   );

//            if(!AreaOfInspectionRemarks.equals("")) {
            inspectionJson.put(ConstantsKeys.AreaOfInspectionRemarks, AreaOfInspectionRemarks);
//            }
            inspectionJson.put(ConstantsKeys.ContainerIdentification, ContainerIdentification);



        }catch (Exception e){
            e.printStackTrace();
        }

        return inspectionJson;

    }



    public JSONObject AddCtPat18DaysObj(JSONObject obj, ArrayList<String> truckIssueNameList, ArrayList<Integer> truckIssueIdList,
                                   ArrayList<String> trailerIssueNameList, ArrayList<Integer> trailerIssueIdList,ArrayList<String> agricultureIssueList, ArrayList<Integer> agricultureIssueIdList){

        JSONObject finalInspectionObj = new JSONObject();

        try{

            JSONArray truckArray = getCtPatIssueArray(truckIssueNameList, truckIssueIdList, 1);
            JSONArray trailerArray = getCtPatIssueArray(trailerIssueNameList, trailerIssueIdList, 2);
            JSONArray agricultureArray = getCtPatIssueArray(agricultureIssueList, agricultureIssueIdList, 3);

            finalInspectionObj.put(ConstantsKeys.Inspection, obj);
            finalInspectionObj.put(ConstantsKeys.TruckIssueList, truckArray);
            finalInspectionObj.put(ConstantsKeys.TrailorIssueList, trailerArray);
            finalInspectionObj.put(ConstantsKeys.AgricultureIssueTypeInspection, agricultureArray);

        }catch (Exception e){
            e.printStackTrace();
        }

        return finalInspectionObj;

    }


    public JSONArray getCtPatIssueArray(  ArrayList<String> issueNameList, ArrayList<Integer> issueIdList, int issueType){

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



}
