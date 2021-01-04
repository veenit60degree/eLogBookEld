package com.local.db;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;

public class SupportMethod {


    public SupportMethod() {
        super();
    }


    /*-------------------- GET SUPPORT SAVED Array -------------------- */
    public JSONArray getSavedSupportArray(DBHelper dbHelper){

        JSONArray logArray = new JSONArray();
        Cursor rs = dbHelper.getSupportDetails();

        if(rs != null && rs.getCount() > 0) {
            rs.moveToFirst();
            String logList = rs.getString(rs.getColumnIndex(DBHelper.SUPPORT_LIST));
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

    /*-------------------- Recap/ViewLog 18 Days DB Helper -------------------- */
    public void SupportHelper(  DBHelper dbHelper, JSONArray supportArray){

        Cursor rs = dbHelper.getSupportDetails();

        if(rs != null & rs.getCount() > 0) {
            rs.moveToFirst();
            dbHelper.UpdateSupportDetails( supportArray );        // UPDATE SUPPORT ARRAY
        }else{
            dbHelper.InsertSupportDetails( supportArray  );      // INSERT SUPPORT ARRAY
        }
        if (!rs.isClosed()) {
            rs.close();
        }
    }



}
