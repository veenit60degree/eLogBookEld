package com.local.db;

import android.database.Cursor;

import com.constants.Constants;
import com.models.Notification18DaysModel;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NotificationMethod {


    public NotificationMethod() {
        super();
    }


    /*-------------------- GET Notification Array -------------------- */
    public JSONArray getSavedNotificationArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getNotificationLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.NOTIFICATION_HISTORY_LIST));
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

    /*---------------- Insert/Update Notification History Table ---------------- */
    public void NotificationHelper( int DriverId, DBHelper dbHelper, JSONArray notificationArray){

        Cursor rs = dbHelper.getNotificationLog(DriverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateNotificationLog(DriverId, notificationArray );     // UPDATE Notification DETAILS
        }else{
            dbHelper.InsertNotificationLog( DriverId, notificationArray  );       // INSERT Notification DETAILS
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }




    /*-------------------- GET Notification Array -------------------- */
    public JSONArray getSaveToNotificationArray(int DriverId, DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getNotificationToSaveLog(DriverId);

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.NOTIFICATION_TO_SAVE_LIST));
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

    /*---------------- Insert/Update Notification History Table ---------------- */
    public void SaveToNotificationHelper( int DriverId, DBHelper dbHelper, JSONArray notificationArray){

        Cursor rs = dbHelper.getNotificationToSaveLog(DriverId);

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateNotificationToSaveLog(DriverId, notificationArray );     // UPDATE Notification DETAILS
        }else{
            dbHelper.InsertNotificationToSaveLog( DriverId, notificationArray  );       // INSERT Notification DETAILS
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }




    //  Add new log as Json in Array
    public JSONObject AddJobInNotificationArray(int NotificationLogId, int DriverId, String DeviceId,
                                    String DriverName, int NotificationTypeId,
                                    String NotificationTypeName, String Title,
                                    String Message, String ImagePath,
                                    String SendDate, String CompanyId   ){

        JSONObject shipmentJson = new JSONObject();

        try {

            shipmentJson.put(ConstantsKeys.NotificationLogId,       NotificationLogId);
            shipmentJson.put(ConstantsKeys.DriverId,                DriverId );
            shipmentJson.put(ConstantsKeys.DeviceId,                DeviceId);

            shipmentJson.put(ConstantsKeys.DriverName,              DriverName);
            shipmentJson.put(ConstantsKeys.NotificationTypeId,      NotificationTypeId);

            shipmentJson.put(ConstantsKeys.NotificationTypeName,    NotificationTypeName );
            shipmentJson.put(ConstantsKeys.Title,                   Title);
            shipmentJson.put(ConstantsKeys.Message,                 Message);
            shipmentJson.put(ConstantsKeys.ImagePath,               ImagePath);
            shipmentJson.put(ConstantsKeys.SendDate,                SendDate);

            shipmentJson.put(ConstantsKeys.CompanyId,               CompanyId);


        }catch (Exception e){
            e.printStackTrace();
        }

        return shipmentJson;

    }



    public JSONArray ReverseArray(JSONArray array){
        JSONArray reversedArray = new JSONArray();

        for(int i = array.length()-1 ; i >= 0  ; i--){
            try {
                JSONObject obj = (JSONObject)array.get(i);
                reversedArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return reversedArray;
    }


    public void saveNotificationHistory(int DriverId, String DeviceId, String DriverName,  String title, String reason,
                                          DateTime currentDateTime, String DriverCompanyId, DBHelper dbHelper){

        // Create JSON object for Notification History
        JSONObject dataJson = AddJobInNotificationArray(
                Constants.StaticLocalNotificationId,
                DriverId,
                DeviceId,
                DriverName,
                Constants.ELDLocalRule,
                "",
                title,
                reason,
                "",
                currentDateTime.toString(),
                DriverCompanyId
        );


        //========================== Offline notification data ready to post to the server ==============================
        JSONArray saveToNotificationArray = getSaveToNotificationArray(DriverId, dbHelper);
        saveToNotificationArray.put(dataJson);
        SaveToNotificationHelper(DriverId, dbHelper, saveToNotificationArray);



        //========================== 18 days notification data saved locally ==============================
        JSONArray notification18DaysArray = getSavedNotificationArray(DriverId, dbHelper);

        if(notification18DaysArray.length() > 1) {
            JSONArray reverseArray = ReverseArray(notification18DaysArray);
            reverseArray.put(dataJson);

            // Again reverse array
            notification18DaysArray = new JSONArray();
            notification18DaysArray = ReverseArray(reverseArray);
        }else{
            notification18DaysArray.put(dataJson);
        }
        // Update Notifications 18 days Array in SQLite Helper
        NotificationHelper(DriverId, dbHelper, notification18DaysArray);


    }


    public JSONArray getNotification18DaysJSONArray(List<Notification18DaysModel> notificationsList){

        JSONArray jsonArray = new JSONArray();
        try {
            for(int i = 0 ; i < notificationsList.size() ; i++) {

                Notification18DaysModel notificationModel = notificationsList.get(i);

                JSONObject notificationObjJson = new JSONObject();
                notificationObjJson.put(ConstantsKeys.NotificationLogId , notificationModel.getNotificationLogId());
                notificationObjJson.put(ConstantsKeys.DriverId , notificationModel.getDriverId());
                notificationObjJson.put(ConstantsKeys.DriverName , notificationModel.getDriverName() );
                notificationObjJson.put(ConstantsKeys.NotificationTypeId , notificationModel.getNotificationTypeId() );
                notificationObjJson.put(ConstantsKeys.NotificationTypeName , notificationModel.getNotificationTypeName() );
                notificationObjJson.put(ConstantsKeys.Title , notificationModel.getTitle() );
                notificationObjJson.put(ConstantsKeys.Message , notificationModel.getMessage() );
                notificationObjJson.put(ConstantsKeys.ImagePath , notificationModel.getImagePath() );
                notificationObjJson.put(ConstantsKeys.SendDate , notificationModel.getSendDate() );
                notificationObjJson.put(ConstantsKeys.CompanyId , notificationModel.getCompanyId() );

                jsonArray.put(notificationObjJson);

            }

        }catch (Exception  e) {
            e.printStackTrace();
        }

        return jsonArray;

    }



}
