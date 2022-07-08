package com.local.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.messaging.logistic.Globally;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME            = "SQL_ALS.db";
    private static final int DATABASE_VERSION = 2;

    // SQLite TABLE name
    public static final String TABLE_DRIVER_LOG           = "tbl_driver_log";
    public static final String TABLE_SHIPMENT             = "tbl_shipment";
    public static final String TABLE_SHIPMENT_18DAYS      = "tbl_shipment_18days";
    public static final String TABLE_ODOMETER             = "tbl_odometer";
    public static final String TABLE_ODOMETER_18Days      = "tbl_odometer_18days";
    public static final String TABLE_INSPECTION           = "tbl_inspection";
    public static final String TABLE_RECAP_DATA_18DAYS    = "tbl_recap_view_18days";
    public static final String TABLE_SYNC_DATA            = "tbl_sync_data";
    public static final String TABLE_DRIVER_LOCATION      = "tbl_driver_location";
    public static final String TABLE_LAT_LON              = "tbl_lat_lon";
    public static final String TABLE_SUPPORT              = "tbl_support";
    public static final String TABLE_DRIVER_PERMISSION    = "tbl_driver_permission";
    public static final String TABLE_DRIVER_RECORD_LOG    = "tbl_driver_record_log";
    public static final String TABLE_INSPECTION_18DAYS    = "tbl_inspection_18days";
    public static final String TABLE_INSPECTION_OFFLINE   = "tbl_inspection_offline";
    public static final String TABLE_SHIPPING_LOG         = "tbl_shipping_log";
    public static final String TABLE_NOTIFICATION_HISTORY = "tbl_notification_history";
    public static final String TABLE_NOTIFICATION_TO_SAVE = "tbl_notification_to_save";
    public static final String TABLE_CT_PAT_INSPECTION    = "tbl_ct_pat_inspection";
    public static final String TABLE_CT_PAT_INSP_18DAYS   = "tbl_ct_pat_insp_18days";
    public static final String TABLE_MALFUNCTION_DIANOSTIC= "tbl_mal_diagnostic";
    public static final String TABLE_MALFUNCTION_DIANOSTIC1= "tbl_mal_diagnostic_one";
    public static final String TABLE_MAL_DIA_OCCURRED_TIME = "tbl_mal_dia_occurred_time";
    public static final String TABLE_POWER_COMP_MAL_DIA    = "tbl_power_comp_mal_dia";
    public static final String TABLE_DEFERRAL_RULE         = "tbl_deferral_rule";
    public static final String TABLE_MAl_DIA_EVENT_DURATION= "tbl_mal_dia_event_duration";
    public static final String TABLE_POSITION_DIA_MAL      = "tbl_position_dia_mal";
    public static final String TABLE_UNIDENTIFIED_LOGOUT_EVENT    = "tbl_unidentified_logout_event";
    public static final String TABLE_UNIDENTIFIED_RECORDS  = "tbl_unidentified_record";
    public static final String TABLE_DOWNLOADEDLOGS_USA_RECORDS     = "tbl_downloadedlogs_usa_record";
    public static final String TABLE_DOWNLOADEDLOGS_CANADA_RECORDS  = "tbl_downloadedlogs_canada_record";
    public static final String TABLE_BLE_GPS_APPLAUNCH_LOGS         = "tbl_ble_gps_app_launch_logs";



    public static final String DRIVER_ID_KEY              = "driver_id";
    public static final String PROJECT_ID_KEY             = "project_id";
    public static final String COMPANY_ID_KEY             = "company_id";

    public static final String DRIVER_LOG_LIST            = "oDriver_Log_Detail_List";
    public static final String SHIPMENT_LIST              = "oDriver_shipment_List";
    public static final String SHIPMENT_18DAYS_LIST       = "oDriver_shipment_18days_List";
    public static final String ODOMETER_LIST              = "oDriver_odometer_List";
    public static final String ODOMETER_18DAYS_LIST       = "oDriver_odometer_18days_List";
    public static final String INSPECTION_LIST            = "oDriver_odometer_List";
    public static final String RECAP_DATA_18DAYS_LIST     = "oDriver_recap_18days_List";
    public static final String SYNC_DATA_LIST             = "oDriver_sync_data_List";
    public static final String DRIVER_LOCATION_LIST       = "oDriver_driver_location_List";
    public static final String LAT_LON_LIST               = "oDriver_lat_lon_List";
    public static final String SUPPORT_LIST               = "oDriver_support_List";
    public static final String PERMISSION_LIST            = "oDriver_support_List";
    public static final String RECORD_LOG_LIST            = "oDriver_record_log_List";
    public static final String INSPECTION_18DAYS_LIST     = "oDriver_inspection_18days";
    public static final String INSPECTION_OFFLINE_LIST    = "oDriver_inspection_offline";
    public static final String SHIPPING_LOG_LIST          = "oDriver_shipping_log";
    public static final String NOTIFICATION_HISTORY_LIST  = "oDriver_notification_list";
    public static final String NOTIFICATION_TO_SAVE_LIST  = "oDriver_notification_to_save_list";
    public static final String CT_PAT_INSPECTION_LIST     = "oDriver_ct_pat_inspection_list";
    public static final String CT_PAT_INSP_18DAYS_LIST    = "oDriver_ct_pat_insp_18days_list";
    public static final String MALFUNCTION_DIANOSTIC_LIST = "oDriver_mal_diagnstc_list";
    public static final String MALFUNCTION_DIANOSTIC_LIST1 = "oDriver_mal_diagnstc_list_one";
    public static final String MAL_DIA_OCCURRED_TIME_LIST  = "oDriver_mal_diagnstc_occurred_list";
    public static final String POWER_COMP_MAL_DIA_LIST     = "oDriver_power_comp_mal_dia_list";
    public static final String DEFERRAL_RULE_LIST          = "oDriver_deferral_rule_list";
    public static final String MAl_DIA_EVENT_DURATION_LIST = "oDriver_mal_dia_event_dur_list";
    public static final String POSITION_MAl_DIA_EVENT_LIST = "oDriver_position_mal_dia_list";
    public static final String UNIDENTIFIED_LOGOUT_EVENT_LIST     = "oDriver_unidentified_logout_list";

    public static final String UNIDENTIFIED_RECORD_LIST    = "oDriver_unidentified_record_list";
    public static final String DOWNLOADLOGS_USA_RECORD_LIST       = "oDriver_downloadlogs_usa_record_list";
    public static final String DOWNLOADLOGS_CANADA_RECORD_LIST    = "oDriver_downloadlogs_canada_record_list";
    public static final String BLE_GPS_APPLAUNCH_LOG_LIST         = "oDriver_ble_gps_app_launch_log_list";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE " + TABLE_DRIVER_LOG +  "(" +
                DRIVER_ID_KEY + " INTEGER, " +  DRIVER_LOG_LIST + " TEXT )"  );

        db.execSQL(  "CREATE TABLE " + TABLE_SHIPMENT + "(" +
                PROJECT_ID_KEY + " INTEGER, " + SHIPMENT_LIST + " TEXT )" );

        db.execSQL(  "CREATE TABLE " + TABLE_SHIPMENT_18DAYS + "(" +
                DRIVER_ID_KEY + " INTEGER, " + SHIPMENT_18DAYS_LIST + " TEXT )" );


        db.execSQL( "CREATE TABLE " + TABLE_ODOMETER + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  ODOMETER_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_ODOMETER_18Days + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  ODOMETER_18DAYS_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_INSPECTION + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  INSPECTION_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_RECAP_DATA_18DAYS + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  RECAP_DATA_18DAYS_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_SYNC_DATA + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  SYNC_DATA_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_DRIVER_LOCATION + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  DRIVER_LOCATION_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_LAT_LON + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  LAT_LON_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_SUPPORT + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  SUPPORT_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_DRIVER_PERMISSION + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  PERMISSION_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_DRIVER_RECORD_LOG + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  RECORD_LOG_LIST + " TEXT )"
        );


        db.execSQL( "CREATE TABLE " + TABLE_INSPECTION_18DAYS + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  INSPECTION_18DAYS_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_INSPECTION_OFFLINE + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  INSPECTION_OFFLINE_LIST + " TEXT )"
        );


        db.execSQL( "CREATE TABLE " + TABLE_SHIPPING_LOG + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  SHIPPING_LOG_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_NOTIFICATION_HISTORY + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  NOTIFICATION_HISTORY_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_NOTIFICATION_TO_SAVE + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  NOTIFICATION_TO_SAVE_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_CT_PAT_INSPECTION + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  CT_PAT_INSPECTION_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_CT_PAT_INSP_18DAYS + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  CT_PAT_INSP_18DAYS_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_MALFUNCTION_DIANOSTIC + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  MALFUNCTION_DIANOSTIC_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_MALFUNCTION_DIANOSTIC1 + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  MALFUNCTION_DIANOSTIC_LIST1 + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_MAL_DIA_OCCURRED_TIME + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  MAL_DIA_OCCURRED_TIME_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_POWER_COMP_MAL_DIA + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  POWER_COMP_MAL_DIA_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_DEFERRAL_RULE + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  DEFERRAL_RULE_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_MAl_DIA_EVENT_DURATION + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  MAl_DIA_EVENT_DURATION_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_POSITION_DIA_MAL + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  POSITION_MAl_DIA_EVENT_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_UNIDENTIFIED_LOGOUT_EVENT + "(" +
                COMPANY_ID_KEY + " INTEGER, " +  UNIDENTIFIED_LOGOUT_EVENT_LIST + " TEXT )"
        );


        db.execSQL( "CREATE TABLE " + TABLE_UNIDENTIFIED_RECORDS + "(" +
                COMPANY_ID_KEY + " INTEGER, " +  UNIDENTIFIED_RECORD_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_DOWNLOADEDLOGS_USA_RECORDS + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  DOWNLOADLOGS_USA_RECORD_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_DOWNLOADEDLOGS_CANADA_RECORDS + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  DOWNLOADLOGS_CANADA_RECORD_LIST + " TEXT )"
        );

        db.execSQL( "CREATE TABLE " + TABLE_BLE_GPS_APPLAUNCH_LOGS + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  BLE_GPS_APPLAUNCH_LOG_LIST + " TEXT )"
        );

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIPMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIPMENT_18DAYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ODOMETER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ODOMETER_18Days);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSPECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECAP_DATA_18DAYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNC_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAT_LON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPPORT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER_PERMISSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER_RECORD_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSPECTION_18DAYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSPECTION_OFFLINE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIPPING_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_TO_SAVE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CT_PAT_INSPECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CT_PAT_INSP_18DAYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MALFUNCTION_DIANOSTIC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MALFUNCTION_DIANOSTIC1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAL_DIA_OCCURRED_TIME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POWER_COMP_MAL_DIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFERRAL_RULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAl_DIA_EVENT_DURATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITION_DIA_MAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNIDENTIFIED_LOGOUT_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNIDENTIFIED_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADEDLOGS_USA_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADEDLOGS_CANADA_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLE_GPS_APPLAUNCH_LOGS);

        onCreate(db);
    }



    // ==========================================================================================
    /* ---------------------- Create Driver Log table if not exist -------------------- */
    public void CreateDriverLogTable(){
       // DROP DATABASE DATABASE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_DRIVER_LOG +  "(" +
                DRIVER_ID_KEY + " INTEGER, " +  DRIVER_LOG_LIST + " TEXT )"  );

    }

    /* ---------------------- Create Shipment table if not exist -------------------- */
    public void CreateShipmentTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(  "CREATE TABLE " + TABLE_SHIPMENT + "(" +
                PROJECT_ID_KEY + " INTEGER, " + SHIPMENT_LIST + " TEXT )" );

    }

    /* ---------------------- Create Shipment 18 days table if not exist -------------------- */
    public void CreateShipment18DaysTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(  "CREATE TABLE " + TABLE_SHIPMENT_18DAYS + "(" +
                DRIVER_ID_KEY + " INTEGER, " + SHIPMENT_18DAYS_LIST + " TEXT )" );

    }

    /* ---------------------- Create Odometer table if not exist -------------------- */
    public void CreateOdometerTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_ODOMETER + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  ODOMETER_LIST + " TEXT )"
        );

    }

    /* ---------------------- Create Odometer table 18 days if not exist -------------------- */
    public void CreateOdometer18DaysTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_ODOMETER_18Days + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  ODOMETER_18DAYS_LIST + " TEXT )"
        );

    }

    /* ---------------------- Create Inspection table if not exist -------------------- */
    public void CreateInspectionTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_INSPECTION + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  INSPECTION_LIST + " TEXT )"
        );

    }


    /* ---------------------- Create Inspection table if not exist -------------------- */
    public void CreateRecap18DaysDataTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_RECAP_DATA_18DAYS + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  RECAP_DATA_18DAYS_LIST + " TEXT )"
        );

    }


    /* ---------------------- Create Syncing data table if not exist -------------------- */
    public void CreateSyncDataTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_SYNC_DATA + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  SYNC_DATA_LIST + " TEXT )"
        );

    }

    /* ---------------------- Create Driver Location table if not exist -------------------- */
    public void CreateDriverLocTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_DRIVER_LOCATION + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  DRIVER_LOCATION_LIST + " TEXT )"
        );

    }

    /* ---------------------- Create Location Lat lon table if not exist -------------------- */
    public void CreateLocLocalTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_LAT_LON + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  LAT_LON_LIST + " TEXT )"
        );

    }

    /* ---------------------- Create Support table if not exist -------------------- */
    public void CreateSupportTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_SUPPORT + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  SUPPORT_LIST + " TEXT )"
        );

    }

    /* ---------------------- Create Driver Permission table if not exist -------------------- */
    public void CreateDriverPermissionTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_DRIVER_PERMISSION + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  PERMISSION_LIST + " TEXT )"
        );

    }


    /* ---------------------- Create Driver Log Record table if not exist -------------------- */
    public void CreateDriverLogRecordTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_DRIVER_RECORD_LOG + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  RECORD_LOG_LIST + " TEXT )"
        );

    }


    /* ---------------------- Create Inspection 18 Days table if not exist -------------------- */
    public void CreateInspection18DaysTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_INSPECTION_18DAYS + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  INSPECTION_18DAYS_LIST + " TEXT )"
        );

    }


    /* ---------------------- Create Inspection offline table if not exist -------------------- */
    public void CreateInspectionOfflineTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_INSPECTION_OFFLINE + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  INSPECTION_OFFLINE_LIST + " TEXT )"
        );

    }


    /* ---------------------- Create Shipping Log table if not exist -------------------- */
    public void CreateShippingLogTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_SHIPPING_LOG+ "(" +
                DRIVER_ID_KEY + " INTEGER, " +  SHIPPING_LOG_LIST + " TEXT )"
        );

    }


    /* ---------------------- Create Notification history table if not exist -------------------- */
    public void CreateNotificationTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_NOTIFICATION_HISTORY + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  NOTIFICATION_HISTORY_LIST + " TEXT )"
        );

    }


    /* ---------------------- Create Notifications save to server table if not exist -------------------- */
    public void CreateNotificationToSaveTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_NOTIFICATION_TO_SAVE + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  NOTIFICATION_TO_SAVE_LIST+ " TEXT )"
        );

    }


    /* ---------------------- Create Ct-Pat Inspection table if not exist -------------------- */
    public void CreateCtPatInspectionTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_CT_PAT_INSPECTION + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  CT_PAT_INSPECTION_LIST+ " TEXT )"
        );

    }


    /* ---------------------- Create Ct-Pat Inspection 18 days table if not exist -------------------- */
    public void CreateCtPatInsp18DaysTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_CT_PAT_INSP_18DAYS + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  CT_PAT_INSP_18DAYS_LIST + " TEXT )"
        );

    }



    /* ---------------------- Create malfunction & diagnostic table if not exist -------------------- */
    public void CreateMalfcnDiagnstcTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_MALFUNCTION_DIANOSTIC + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  MALFUNCTION_DIANOSTIC_LIST + " TEXT )"
        );

    }

    /* ---------------------- Create malfunction & diagnostic table if not exist.
        We need this table to record all occurred malfncn to clear the events later, because in upper table we are clearing records after posted to server. -------------------- */
    public void CreateMalfcnDiagnstcTable1(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_MALFUNCTION_DIANOSTIC1 + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  MALFUNCTION_DIANOSTIC_LIST1 + " TEXT )"
        );

    }


    /* -- Create malfunction & diagnostic table event occurred time if not exist.*/
    public void CreateMalDiaOccTimeTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_MAL_DIA_OCCURRED_TIME + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  MAL_DIA_OCCURRED_TIME_LIST + " TEXT )"
        );

    }

    /*  Create power compliance malfunction & diagnostic table event occurred time if not exist.*/
    public void CreatePowerComplianceTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_POWER_COMP_MAL_DIA + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  POWER_COMP_MAL_DIA_LIST + " TEXT )"
        );

    }


    /* -- Create deferral rule table if not exist.*/
    public void CreateDefferalTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_DEFERRAL_RULE + "(" +
                DRIVER_ID_KEY + " INTEGER, " +  DEFERRAL_RULE_LIST + " TEXT )"
        );

    }


    /*  Create malfunction & diagnostic vents duration table event occurred time if not exist.*/
    public void CreateMalDiaDurationTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_MAl_DIA_EVENT_DURATION + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  MAl_DIA_EVENT_DURATION_LIST + " TEXT )"
        );

    }

    /*  Create positioning malfunction & diagnostic table event occurred time if not exist.*/
    public void CreatePositioningMalDiaTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_POSITION_DIA_MAL + "(" +
                PROJECT_ID_KEY + " INTEGER, " +  POSITION_MAl_DIA_EVENT_LIST + " TEXT )"
        );

    }


    /*  Create Unidentified Logout Event table event occurred time if not exist.*/
    public void CreateUnidentifiedLogoutEventTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "CREATE TABLE " + TABLE_UNIDENTIFIED_LOGOUT_EVENT + "(" +
                COMPANY_ID_KEY + " INTEGER, " +  UNIDENTIFIED_LOGOUT_EVENT_LIST + " TEXT )"
        );

    }


    /* -- Create unidentified record  table if not exist.*/
    public void CreateUnidentifiedRecordTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("CREATE TABLE " + TABLE_UNIDENTIFIED_RECORDS + "(" +
                COMPANY_ID_KEY + " INTEGER, " + UNIDENTIFIED_RECORD_LIST + " TEXT )"
        );
    }

    /* -- Create downloadLogs Usa record  table if not exist.*/
    public void CreateDownloadLogsUsaRecordTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("CREATE TABLE " + TABLE_DOWNLOADEDLOGS_USA_RECORDS + "(" +
                DRIVER_ID_KEY + " INTEGER, " + DOWNLOADLOGS_USA_RECORD_LIST + " TEXT )"
        );
    }

    /* -- Create downloadLogs Canada record  table if not exist.*/
    public void CreateDownloadLogsCanadaRecordTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("CREATE TABLE " + TABLE_DOWNLOADEDLOGS_CANADA_RECORDS + "(" +
                DRIVER_ID_KEY + " INTEGER, " + DOWNLOADLOGS_CANADA_RECORD_LIST + " TEXT )"
        );
    }



    /* -- Create ble, gps, app launch log table if not exist.*/
    public void CreateBleGpsAppLaunchLogTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("CREATE TABLE " + TABLE_BLE_GPS_APPLAUNCH_LOGS + "(" +
                PROJECT_ID_KEY + " INTEGER, " + BLE_GPS_APPLAUNCH_LOG_LIST + " TEXT )"
        );
    }









    //=======================================================================================================================

    /* ---------------------- Insert Driver Log -------------------- */
    public boolean InsertDriverLog(int DriverId, JSONArray oDriverLogDetail   ) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(DRIVER_LOG_LIST, String.valueOf(oDriverLogDetail));

        db.insert(TABLE_DRIVER_LOG, null, contentValues);
        return true;
    }

    /* ---------------------- Insert Shipment Details -------------------- */
    public boolean InsertShipmentDetails(int ProjectId, JSONArray shipmentList) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(SHIPMENT_LIST, String.valueOf(shipmentList));

        db.insert(TABLE_SHIPMENT, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Shipment 18 Days Details -------------------- */
    public boolean InsertShipment18DaysDetails(int DriverId, JSONArray shipmentList) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(SHIPMENT_18DAYS_LIST, String.valueOf(shipmentList));

        db.insert(TABLE_SHIPMENT_18DAYS, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Odometer Details -------------------- */
    public boolean InsertOdometerDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(ODOMETER_LIST, String.valueOf(list));

        db.insert(TABLE_ODOMETER, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Odometer 18 days Details -------------------- */
    public boolean InsertOdometer18DaysDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(ODOMETER_18DAYS_LIST, String.valueOf(list));

        db.insert(TABLE_ODOMETER_18Days, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Inspection Details -------------------- */
    public boolean InsertInspectionDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(INSPECTION_LIST, String.valueOf(list));

        db.insert(TABLE_INSPECTION, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Inspection Details -------------------- */
    public boolean InsertRecap18DaysDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(RECAP_DATA_18DAYS_LIST, String.valueOf(list));

        db.insert(TABLE_RECAP_DATA_18DAYS, null, contentValues);
        return true;
    }


    /* ---------------------- Insert sync data Details -------------------- */
    public boolean InsertSyncDataDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(SYNC_DATA_LIST, String.valueOf(list));

        db.insert(TABLE_SYNC_DATA, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Driver location Details -------------------- */
    public boolean InsertDriverLocDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(DRIVER_LOCATION_LIST, String.valueOf(list));

        db.insert(TABLE_DRIVER_LOCATION, null, contentValues);
        return true;
    }



    /* ---------------------- Insert Location Lat lon Details -------------------- */
    public boolean InsertLatLongDetails( JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, Integer.valueOf(Globally.PROJECT_ID) );
        contentValues.put(LAT_LON_LIST, String.valueOf(list));

        db.insert(TABLE_LAT_LON, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Location Lat lon Details -------------------- */
    public boolean InsertSupportDetails( JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, Integer.valueOf(Globally.PROJECT_ID) );
        contentValues.put(SUPPORT_LIST, String.valueOf(list));

        db.insert(TABLE_SUPPORT, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Driver Permission Details -------------------- */
    public boolean InsertDriverPermissionDetails(int DriverId, JSONObject jsonObj) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(PERMISSION_LIST, String.valueOf(jsonObj));

        db.insert(TABLE_DRIVER_PERMISSION, null, contentValues);
        return true;
    }



    /* ---------------------- Insert Driver Log record Details -------------------- */
    public boolean InsertDriverLogRecordDetails(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(RECORD_LOG_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_DRIVER_RECORD_LOG, null, contentValues);
        return true;
    }



    /* ---------------------- Insert Inspection 18 Days Log -------------------- */
    public boolean InsertInspection18Days(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(INSPECTION_18DAYS_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_INSPECTION_18DAYS, null, contentValues);
        return true;
    }



    /* ---------------------- Insert Inspection offline Log -------------------- */
    public boolean InsertInspectionOffline(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(INSPECTION_OFFLINE_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_INSPECTION_OFFLINE, null, contentValues);
        return true;
    }



    /* ---------------------- Insert Inspection 18 Days Log -------------------- */
    public boolean InsertShippingLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(SHIPPING_LOG_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_SHIPPING_LOG, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Notification History Log -------------------- */
    public boolean InsertNotificationLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(NOTIFICATION_HISTORY_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_NOTIFICATION_HISTORY, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Notification save to servere Log -------------------- */
    public boolean InsertNotificationToSaveLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(NOTIFICATION_TO_SAVE_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_NOTIFICATION_TO_SAVE, null, contentValues);
        return true;
    }



    /* ---------------------- Insert Ct-Pat Inspection save to servere Log -------------------- */
    public boolean InsertCtPatInspectionLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(CT_PAT_INSPECTION_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_CT_PAT_INSPECTION, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Ct-Pat 18 days Inspection save to servere Log -------------------- */
    public boolean InsertCtPatInsp18DaysLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(CT_PAT_INSP_18DAYS_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_CT_PAT_INSP_18DAYS, null, contentValues);
        return true;
    }



    /* ---------------------- Insert malfunction & diagnostic events  Log -------------------- */
    public boolean InsertMalfncnDiagnosticLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, ProjectId);
        contentValues.put(MALFUNCTION_DIANOSTIC_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_MALFUNCTION_DIANOSTIC, null, contentValues);
        return true;
    }


    /* ---------------------- Insert malfunction & diagnostic events  Log -------------------- */
    public boolean InsertMalfncnDiagnosticLog1(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(MALFUNCTION_DIANOSTIC_LIST1, String.valueOf(jsonArray));

        db.insert(TABLE_MALFUNCTION_DIANOSTIC1, null, contentValues);
        return true;
    }



    /* ---------------------- Insert malfunction & diagnostic events  Log -------------------- */
    public boolean InsertMalDiaOccTimeLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(MAL_DIA_OCCURRED_TIME_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_MAL_DIA_OCCURRED_TIME, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Power Compliance events  Log -------------------- */
    public boolean InsertPowerComplianceLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(POWER_COMP_MAL_DIA_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_POWER_COMP_MAL_DIA, null, contentValues);
        return true;
    }



    /* ---------------------- Insert deferral events  Log -------------------- */
    public boolean InsertDeferralLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(DEFERRAL_RULE_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_DEFERRAL_RULE, null, contentValues);
        return true;
    }



    /* ---------------------- Insert Mal/Dia events duration Log -------------------- */
    public boolean InsertMalDiaDurationLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(MAl_DIA_EVENT_DURATION_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_MAl_DIA_EVENT_DURATION, null, contentValues);
        return true;
    }


    /* ---------------------- Insert positioning Mal/Dia events Log -------------------- */
    public boolean InsertPositioningMalDiaLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(POSITION_MAl_DIA_EVENT_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_POSITION_DIA_MAL, null, contentValues);
        return true;
    }


    /* ---------------------- Insert Unidentified logout events  Log -------------------- */
    public boolean InsertUnidentifiedLogoutRecordLog(int CompanyId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COMPANY_ID_KEY, CompanyId);
        contentValues.put(UNIDENTIFIED_LOGOUT_EVENT_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_UNIDENTIFIED_LOGOUT_EVENT, null, contentValues);
        return true;
    }



    /* ---------------------- Insert Unidentified DR/OD events  Log -------------------- */
    public boolean InsertUnidetifiedRecordLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COMPANY_ID_KEY, DriverId);
        contentValues.put(UNIDENTIFIED_RECORD_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_UNIDENTIFIED_RECORDS, null, contentValues);
        return true;
    }

    /* ---------------------- Insert downloadLogs events  Log -------------------- */
    public boolean InsertDownloadLogsUsaRecordLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(DOWNLOADLOGS_USA_RECORD_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_DOWNLOADEDLOGS_USA_RECORDS, null, contentValues);
        return true;
    }

    /* ---------------------- Insert downloadLogs events  Log -------------------- */
    public boolean InsertDownloadLogsCanadaRecordLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(DOWNLOADLOGS_CANADA_RECORD_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_DOWNLOADEDLOGS_CANADA_RECORDS, null, contentValues);
        return true;
    }



    /* ---------------------- Insert Bluetooth/Gps/ App Launch events Log -------------------- */
    public boolean InsertBleGpsAppLaunchLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(BLE_GPS_APPLAUNCH_LOG_LIST, String.valueOf(jsonArray));

        db.insert(TABLE_BLE_GPS_APPLAUNCH_LOGS, null, contentValues);
        return true;
    }









    //=======================================================================================================================

    /* ---------------------- Update Driver Log -------------------- */
    public boolean UpdateDriverLog(int DriverId, JSONArray oDriverLogDetail ) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(DRIVER_LOG_LIST, String.valueOf(oDriverLogDetail));

        db.update(TABLE_DRIVER_LOG, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }

    /* ---------------------- Update Shipment Details -------------------- */
    public boolean UpdateShipmentDetails(int ProjectId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(SHIPMENT_LIST, String.valueOf(list));

        db.update(TABLE_SHIPMENT, contentValues, PROJECT_ID_KEY + " = ? ", new String[] { Integer.toString(ProjectId) } );
        return true;
    }

    /* ---------------------- Update Shipment 18 Days Details -------------------- */
    public boolean UpdateShipment18DaysDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(SHIPMENT_18DAYS_LIST, String.valueOf(list));

        db.update(TABLE_SHIPMENT_18DAYS, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }

    /* ---------------------- Update Odometer Details -------------------- */
    public boolean UpdateOdometerDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(ODOMETER_LIST, String.valueOf(list));

        db.update(TABLE_ODOMETER, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }

    /* ---------------------- Update Odometer 18 Days Details -------------------- */
    public boolean UpdateOdometer18DaysDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(ODOMETER_18DAYS_LIST, String.valueOf(list));

        db.update(TABLE_ODOMETER_18Days, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Inspection 18 days Details -------------------- */
    public boolean UpdateInspectionDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(INSPECTION_LIST, String.valueOf(list));

        db.update(TABLE_INSPECTION, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }

    /* ---------------------- Update Inspection 18 days Details -------------------- */
    public boolean UpdateRecap18DaysDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(RECAP_DATA_18DAYS_LIST, String.valueOf(list));

        db.update(TABLE_RECAP_DATA_18DAYS, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Inspection 18 days Details -------------------- */
    public boolean UpdateSyncDataDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(SYNC_DATA_LIST, String.valueOf(list));

        db.update(TABLE_SYNC_DATA, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }

    /* ---------------------- Update Driver Location Details -------------------- */
    public boolean UpdateDriverLocDetails(int DriverId, JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(DRIVER_LOCATION_LIST, String.valueOf(list));

        db.update(TABLE_DRIVER_LOCATION, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Udate Location Lat lon Details -------------------- */
    public boolean UpdateLatLongDetails( JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, Integer.valueOf(Globally.PROJECT_ID) );
        contentValues.put(LAT_LON_LIST, String.valueOf(list));

        db.update(TABLE_LAT_LON, contentValues, PROJECT_ID_KEY + " = ? ", new String[] { Globally.PROJECT_ID } );
        return true;
    }


    /* ---------------------- Udate Support Details -------------------- */
    public boolean UpdateSupportDetails( JSONArray list) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, Integer.valueOf(Globally.PROJECT_ID) );
        contentValues.put(SUPPORT_LIST, String.valueOf(list));

        db.update(TABLE_SUPPORT, contentValues, PROJECT_ID_KEY + " = ? ", new String[] { Globally.PROJECT_ID } );
        return true;
    }


    /* ---------------------- Update Driver Permission Details -------------------- */
    public boolean UpdateDriverPermissionDetails(int DriverId, JSONObject jsonObj) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(PERMISSION_LIST, String.valueOf(jsonObj));

        db.update(TABLE_DRIVER_PERMISSION, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Driver Log Record Details -------------------- */
    public boolean UpdateDriverLogRecordDetails(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(RECORD_LOG_LIST, String.valueOf(jsonArray));

        db.update(TABLE_DRIVER_RECORD_LOG, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Inspection 18 Days -------------------- */
    public boolean UpdateInspection18Days(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(INSPECTION_18DAYS_LIST, String.valueOf(jsonArray));

        db.update(TABLE_INSPECTION_18DAYS, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }



    /* ---------------------- Update Inspection Offline -------------------- */
    public boolean UpdateInspectionOffline(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(INSPECTION_OFFLINE_LIST, String.valueOf(jsonArray));

        db.update(TABLE_INSPECTION_OFFLINE, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }



    /* ---------------------- Update Shipping Log -------------------- */
    public boolean UpdateShippingLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(SHIPPING_LOG_LIST, String.valueOf(jsonArray));

        db.update(TABLE_SHIPPING_LOG, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Shipping Log -------------------- */
    public boolean UpdateNotificationLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(NOTIFICATION_HISTORY_LIST, String.valueOf(jsonArray));

        db.update(TABLE_NOTIFICATION_HISTORY, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Shipping Log -------------------- */
    public boolean UpdateNotificationToSaveLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(NOTIFICATION_TO_SAVE_LIST, String.valueOf(jsonArray));

        db.update(TABLE_NOTIFICATION_TO_SAVE, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Ct-Pat Inspection Log -------------------- */
    public boolean UpdateCtPatInspectionLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(CT_PAT_INSPECTION_LIST, String.valueOf(jsonArray));

        db.update(TABLE_CT_PAT_INSPECTION, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Ct-Pat 18 days Inspection Log -------------------- */
    public boolean UpdateCtPatInsp18DaysLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(CT_PAT_INSP_18DAYS_LIST, String.valueOf(jsonArray));

        db.update(TABLE_CT_PAT_INSP_18DAYS, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Malfunction & Diagnostic Log -------------------- */
    public boolean UpdateMalfunctionDiagnosticLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, ProjectId);
        contentValues.put(MALFUNCTION_DIANOSTIC_LIST, String.valueOf(jsonArray));

        db.update(TABLE_MALFUNCTION_DIANOSTIC, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(ProjectId) } );
        return true;
    }


    /* ---------------------- Update Malfunction & Diagnostic Log -------------------- */
    public boolean UpdateMalfunctionDiagnosticLog1(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(MALFUNCTION_DIANOSTIC_LIST1, String.valueOf(jsonArray));

        db.update(TABLE_MALFUNCTION_DIANOSTIC1, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }



    /* ---------------------- Update Malfunction & Diagnostic Log -------------------- */
    public boolean UpdateMalDiaOccTimeLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(MAL_DIA_OCCURRED_TIME_LIST, String.valueOf(jsonArray));

        db.update(TABLE_MAL_DIA_OCCURRED_TIME, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }


    /* ---------------------- Update Power Compliance Log -------------------- */
    public boolean UpdatePowerComplianceLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(POWER_COMP_MAL_DIA_LIST, String.valueOf(jsonArray));

        db.update(TABLE_POWER_COMP_MAL_DIA, contentValues, PROJECT_ID_KEY + " = ? ", new String[] { Integer.toString(ProjectId) } );
        return true;
    }


    /* ---------------------- Update Deferral Log -------------------- */
    public boolean UpdateDeferralLog(int DriverId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, DriverId);
        contentValues.put(DEFERRAL_RULE_LIST, String.valueOf(jsonArray));

        db.update(TABLE_DEFERRAL_RULE, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(DriverId) } );
        return true;
    }



    /* ---------------------- Update mal/dia duration events Log -------------------- */
    public boolean UpdateMalDiaDurationLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(MAl_DIA_EVENT_DURATION_LIST, String.valueOf(jsonArray));

        db.update(TABLE_MAl_DIA_EVENT_DURATION, contentValues, PROJECT_ID_KEY + " = ? ", new String[] { Integer.toString(ProjectId) } );
        return true;
    }


    /* ---------------------- Update positioning mal/dia duration events Log -------------------- */
    public boolean UpdatePositioningMalDiaLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(POSITION_MAl_DIA_EVENT_LIST, String.valueOf(jsonArray));

        db.update(TABLE_POSITION_DIA_MAL, contentValues, PROJECT_ID_KEY + " = ? ", new String[] { Integer.toString(ProjectId) } );
        return true;
    }



    /* ---------------------- Update Unidentified logout events Log -------------------- */
    public boolean UpdateUnidentifiedLogoutRecordLog(int CompanyId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COMPANY_ID_KEY, CompanyId);
        contentValues.put(UNIDENTIFIED_LOGOUT_EVENT_LIST, String.valueOf(jsonArray));

        db.update(TABLE_UNIDENTIFIED_LOGOUT_EVENT, contentValues, COMPANY_ID_KEY + " = ? ", new String[] { Integer.toString(CompanyId) } );
        return true;
    }


    /* ---------------------- Update Unidentified Log -------------------- */
    public boolean UpdateUnidentifiedRecordLog(int CompanyId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COMPANY_ID_KEY, CompanyId);
        contentValues.put(UNIDENTIFIED_RECORD_LIST, String.valueOf(jsonArray));

        db.update(TABLE_UNIDENTIFIED_RECORDS, contentValues, COMPANY_ID_KEY + " = ? ", new String[] { Integer.toString(CompanyId) } );
        return true;
    }

    /* ---------------------- Update downloaded Usa Log -------------------- */
    public boolean UpdateDownloadedUsaRecordLog(int CompanyId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, CompanyId);
        contentValues.put(DOWNLOADLOGS_USA_RECORD_LIST, String.valueOf(jsonArray));

        db.update(TABLE_DOWNLOADEDLOGS_USA_RECORDS, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(CompanyId) } );
        return true;
    }

    /* ---------------------- Update downloaded Canada Log -------------------- */
    public boolean UpdateDownloadedCanadaRecordLog(int CompanyId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DRIVER_ID_KEY, CompanyId);
        contentValues.put(DOWNLOADLOGS_CANADA_RECORD_LIST, String.valueOf(jsonArray));

        db.update(TABLE_DOWNLOADEDLOGS_CANADA_RECORDS, contentValues, DRIVER_ID_KEY + " = ? ", new String[] { Integer.toString(CompanyId) } );
        return true;
    }




    /* ---------------------- Update Bluetooth/Gps/ App Launch events Log -------------------- */
    public boolean UpdateBleGpsAppLaunchLog(int ProjectId, JSONArray jsonArray) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PROJECT_ID_KEY, ProjectId);
        contentValues.put(BLE_GPS_APPLAUNCH_LOG_LIST, String.valueOf(jsonArray));

        db.update(TABLE_BLE_GPS_APPLAUNCH_LOGS, contentValues, PROJECT_ID_KEY + " = ? ", new String[] { Integer.toString(ProjectId) } );
        return true;
    }








    //=======================================================================================================================

    /* ---------------------- Get Driver Log -------------------- */
    public Cursor getDriverLog(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_DRIVER_LOG + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});

//        Log.d("@@@DriverId","HelperDriverId: " +DriverId );
        return res;
    }

    /* ---------------------- Get Shipment Details -------------------- */
    public Cursor getShipmentDetails(int ProjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_SHIPMENT + " WHERE " +
                PROJECT_ID_KEY + "=?", new String[]{Integer.toString(ProjectId)});
        return res;
    }

    /* ---------------------- Get Shipment 18 Days Details -------------------- */
    public Cursor getShipment18DaysDetails(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_SHIPMENT_18DAYS + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }

    /* ---------------------- Get Odometer Details -------------------- */
    public Cursor getOdometerDetails(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_ODOMETER + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }

    /* ---------------------- Get Odometer 18 days Details -------------------- */
    public Cursor getOdometer18DaysDetails(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_ODOMETER_18Days + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }

    /* ---------------------- Get Inspection Details -------------------- */
    public Cursor getInspectionDetails(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_INSPECTION + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }

    /* ---------------------- Get Inspection Details -------------------- */
    public Cursor getRecap18DaysDetails(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_RECAP_DATA_18DAYS + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }

    /* ---------------------- Get Inspection Details -------------------- */
    public Cursor getSyncDataDetails(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_SYNC_DATA + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }

    /* ---------------------- Get Driver Location Details -------------------- */
    public Cursor getDriverLocDetails(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_DRIVER_LOCATION + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }


    /* ---------------------- Get Location Lat lon Details -------------------- */
    public Cursor getLatLonDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_LAT_LON + " WHERE " +
                PROJECT_ID_KEY + "=?", new String[]{ Globally.PROJECT_ID });
        return res;
    }


    /* ---------------------- Get Support Details -------------------- */
    public Cursor getSupportDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_SUPPORT + " WHERE " +
                PROJECT_ID_KEY + "=?", new String[]{ Globally.PROJECT_ID });
        return res;
    }


    /* ---------------------- Get Driver Permission Details -------------------- */
    public Cursor getDriverPermissionDetails(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_DRIVER_PERMISSION + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }

    /* ---------------------- Get Driver Log Record Details -------------------- */
    public Cursor getDriverLogRecordDetails(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_DRIVER_RECORD_LOG + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }


    /* ---------------------- Get Inspection 18 Days -------------------- */
    public Cursor getInspection18Days(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_INSPECTION_18DAYS + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }


    /* ---------------------- Get Inspection Offline -------------------- */
    public Cursor getOfflineInspection(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_INSPECTION_OFFLINE + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }



    /* ---------------------- Get Shipping Log -------------------- */
    public Cursor getShippingLog(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_SHIPPING_LOG + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }



    /* ---------------------- Get Notification Log -------------------- */
    public Cursor getNotificationLog(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION_HISTORY + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }

    /* ---------------------- Get Notification Save to server log-------------------- */
    public Cursor getNotificationToSaveLog(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION_TO_SAVE + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }


    /* ---------------------- Get Ct-Pat Inspection Save to server log-------------------- */
    public Cursor getCtPatInspectionLog(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_CT_PAT_INSPECTION + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }


    /* ---------------------- Get Ct-Pat Inspection 18 days Save to server log-------------------- */
    public Cursor getCtPatInsp18DaysLog(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_CT_PAT_INSP_18DAYS + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }


    /* ---------------------- Get Malfunction & Diagnostic log-------------------- */
    public Cursor getMalfunctionDiagnosticLog(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_MALFUNCTION_DIANOSTIC + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }


    /* ---------------------- Get Malfunction & Diagnostic log-------------------- */
    public Cursor getMalfunctionDiagnosticLog1(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_MALFUNCTION_DIANOSTIC1 + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }


    /* ---------------------- Get Malfunction & Diagnostic time log-------------------- */
    public Cursor getMalDiaTimeLog(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_MAL_DIA_OCCURRED_TIME + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }


    /* ---------------------- Get Power Compliance logs-------------------- */
    public Cursor getPowerComplianceLog(int ProjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_POWER_COMP_MAL_DIA + " WHERE " +
                PROJECT_ID_KEY + "=?", new String[]{Integer.toString(ProjectId)});
        return res;
    }



    /* ---------------------- Get deferral event log-------------------- */
    public Cursor getDeferralLog(int DriverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_DEFERRAL_RULE + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(DriverId)});
        return res;
    }



    /* ---------------------- Get Mal/Dia Events Duration logs-------------------- */
    public Cursor getMalDiaDurationLog(int ProjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_MAl_DIA_EVENT_DURATION + " WHERE " +
                PROJECT_ID_KEY + "=?", new String[]{Integer.toString(ProjectId)});
        return res;
    }

    /* ---------------------- Get positioning Mal/Dia Events logs-------------------- */
    public Cursor getPositioningMalDiaLog(int ProjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_POSITION_DIA_MAL + " WHERE " +
                PROJECT_ID_KEY + "=?", new String[]{Integer.toString(ProjectId)});
        return res;
    }


    /* ---------------------- Get unidentified logout event log-------------------- */
    public Cursor getUnidentifiedLogoutRecordLog(int CompanyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_UNIDENTIFIED_LOGOUT_EVENT + " WHERE " +
                COMPANY_ID_KEY + "=?", new String[]{Integer.toString(CompanyId)});
        return res;
    }



    /* ---------------------- Get unidentified event DR/OD log-------------------- */
    public Cursor getUnidentifiedRecordLog(int CompanyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_UNIDENTIFIED_RECORDS + " WHERE " +
                COMPANY_ID_KEY + "=?", new String[]{Integer.toString(CompanyId)});
        return res;
    }


    /* ---------------------- Get downloaded logs Usa event log-------------------- */
    public Cursor getDownloadedLogsUsaRecord(int CompanyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_DOWNLOADEDLOGS_USA_RECORDS + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(CompanyId)});
        return res;
    }

    /* ---------------------- Get downloaded logs Canada event log-------------------- */
    public Cursor getDownloadedLogsCanadaRecord(int CompanyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_DOWNLOADEDLOGS_CANADA_RECORDS + " WHERE " +
                DRIVER_ID_KEY + "=?", new String[]{Integer.toString(CompanyId)});
        return res;
    }



    /* ---------------------- Get Bluetooth, Gps, App Launch Events logs-------------------- */
    public Cursor getBleGpsAppLaunchLog(int ProjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_BLE_GPS_APPLAUNCH_LOGS + " WHERE " +
                PROJECT_ID_KEY + "=?", new String[]{Integer.toString(ProjectId)});
        return res;
    }




    //=======================================================================================================================

    /* ---------------------- Delete Driver Log -------------------- */
    public void DeleteTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_DRIVER_LOG);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* ---------------------- Delete Shipment Details -------------------- */
    public void DeleteShipmentTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_SHIPMENT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* ---------------------- Delete Shipment 18 Days Details -------------------- */
    public void DeleteShipment18DaysTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_SHIPMENT_18DAYS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* ---------------------- Delete Odometer Details -------------------- */
    public void DeleteOdometerTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_ODOMETER );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* ---------------------- Delete Odometer Details -------------------- */
    public void DeleteOdometer18DaysTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_ODOMETER_18Days );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* ---------------------- Delete Inspection Details -------------------- */
    public void DeleteInspectionTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_INSPECTION);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* ---------------------- Delete Inspection Details -------------------- */
    public void DeleteRecap18DaysTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_RECAP_DATA_18DAYS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Inspection Details -------------------- */
    public void DeleteSyncDataTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_SYNC_DATA);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* ---------------------- Delete Driver Location Details -------------------- */
    public void DeleteDriverLocTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_DRIVER_LOCATION);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Location Lat lon Details -------------------- */
    public void DeleteLatLongTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_LAT_LON);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Support Details -------------------- */
    public void DeleteSupportTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_SUPPORT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Driver Permission Details -------------------- */
    public void DeleteDriverPermissionTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_DRIVER_PERMISSION);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Driver Permission Details -------------------- */
    public void DeleteDriverLogRecordTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_DRIVER_RECORD_LOG);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Inspection 18 Days Table -------------------- */
    public void DeleteInspection18DaysTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_INSPECTION_18DAYS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Inspection Offline Table -------------------- */
    public void DeleteInspectionOfflineTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_INSPECTION_OFFLINE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /* ---------------------- Delete Shipping Log Table -------------------- */
    public void DeleteShippingLogTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_SHIPPING_LOG);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /* ---------------------- Delete Notification Log Table -------------------- */
    public void DeleteNotificationTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_NOTIFICATION_HISTORY);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Notification save to server Log Table -------------------- */
    public void DeleteNotificationSaveToTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_NOTIFICATION_TO_SAVE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Ct-Pat Inspection Log Table -------------------- */
    public void DeleteCtPatInspectionTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_CT_PAT_INSPECTION);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /* ---------------------- Delete Ct-Pat Inspection Log Table -------------------- */
    public void DeleteCtPat18DaysInspTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_CT_PAT_INSP_18DAYS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ---------------------- Delete Malfunction & Diagnostic Log Table -------------------- */
    public void DeleteMalfunctionDiagnosticTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_MALFUNCTION_DIANOSTIC);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /* ---------------------- Delete Malfunction & Diagnostic Log Table -------------------- */
    public void DeleteMalfunctionDiagnosticTable1() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_MALFUNCTION_DIANOSTIC1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ------ Delete Malfunction & Diagnostic time Log Table ------ */
    public void DeleteMalDiaOccTimeTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_MAL_DIA_OCCURRED_TIME);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ----- Delete Power Compliance Log Table ------ */
    public void DeletePowerComplianceTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_POWER_COMP_MAL_DIA);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /* ------ Delete Deferral time Log Table ------ */
    public void DeleteDeferralTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_DEFERRAL_RULE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ------ Delete Deferral time Log Table ------ */
    public void DeleteMalDiaDurationTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_MAl_DIA_EVENT_DURATION);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ------ Delete Unidentified logout Log Table ------ */
    public void DeleteUnidentifiedLogoutTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_UNIDENTIFIED_LOGOUT_EVENT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* ------ Delete Downloaded Log Usa Table ------ */
    public void DeleteDownloadedLogsUsaRecordTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_DOWNLOADEDLOGS_USA_RECORDS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* ------ Delete Downloaded Log Canada Table ------ */
    public void DeleteDownloadedLogsCanadaRecordTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_DOWNLOADEDLOGS_CANADA_RECORDS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /* ------ Delete Ble, Gps App Launch Table ------ */
    public void DeleteBleGpsAppLaunchTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ TABLE_BLE_GPS_APPLAUNCH_LOGS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }







    // =====================================================================================================

    /* ---------------------- Create table if table does not exist -------------------- */
    public void CheckAllTableExistingStatus(){

        if(!isTableExists(TABLE_DRIVER_LOG)) {
            CreateDriverLogTable();
        }

        if(!isTableExists(TABLE_SHIPMENT)) {
            CreateShipmentTable();
        }

        if(!isTableExists(TABLE_SHIPMENT_18DAYS)) {
            CreateShipment18DaysTable();
        }

        if(!isTableExists(TABLE_ODOMETER)) {
            CreateOdometerTable();
        }

        if(!isTableExists(TABLE_ODOMETER_18Days)) {
            CreateOdometer18DaysTable();
        }

        if(!isTableExists(TABLE_RECAP_DATA_18DAYS)) {
            CreateRecap18DaysDataTable();
        }

        if(!isTableExists(TABLE_SYNC_DATA)) {
            CreateSyncDataTable();
        }

        if(!isTableExists(TABLE_DRIVER_LOCATION)) {
            CreateDriverLocTable();
        }

        if(!isTableExists(TABLE_LAT_LON)) {
            CreateLocLocalTable();
        }

        if(!isTableExists(TABLE_SUPPORT)) {
            CreateSupportTable();
        }

        if(!isTableExists(TABLE_DRIVER_PERMISSION)) {
            CreateDriverPermissionTable();
        }

        if(!isTableExists(TABLE_DRIVER_RECORD_LOG)) {
            CreateDriverLogRecordTable();
        }

        if(!isTableExists(TABLE_INSPECTION_18DAYS)) {
            CreateInspection18DaysTable();
        }

        if(!isTableExists(TABLE_INSPECTION_OFFLINE)) {
            CreateInspectionOfflineTable();
        }


        if(!isTableExists(TABLE_SHIPPING_LOG)) {
            CreateShippingLogTable();
        }

        if(!isTableExists(TABLE_NOTIFICATION_HISTORY)) {
            CreateNotificationTable();
        }

        if(!isTableExists(TABLE_NOTIFICATION_TO_SAVE)) {
            CreateNotificationToSaveTable();
        }

        if(!isTableExists(TABLE_CT_PAT_INSPECTION)) {
            CreateCtPatInspectionTable();
        }

        if(!isTableExists(TABLE_CT_PAT_INSP_18DAYS)) {
            CreateCtPatInsp18DaysTable();
        }

        if(!isTableExists(TABLE_MALFUNCTION_DIANOSTIC)) {
            CreateMalfcnDiagnstcTable();
        }

        if(!isTableExists(TABLE_MALFUNCTION_DIANOSTIC1)) {
            CreateMalfcnDiagnstcTable1();
        }

        if(!isTableExists(TABLE_MAL_DIA_OCCURRED_TIME)) {
            CreateMalDiaOccTimeTable();
        }

        if(!isTableExists(TABLE_POWER_COMP_MAL_DIA)) {
            CreatePowerComplianceTable();
        }

        if(!isTableExists(TABLE_DEFERRAL_RULE)) {
            CreateDefferalTable();
        }

        if(!isTableExists(TABLE_MAl_DIA_EVENT_DURATION)) {
            CreateMalDiaDurationTable();
        }

        if(!isTableExists(TABLE_POSITION_DIA_MAL)) {
            CreatePositioningMalDiaTable();
        }

        if(!isTableExists(TABLE_UNIDENTIFIED_LOGOUT_EVENT)) {
            CreateUnidentifiedLogoutEventTable();
        }


        if(!isTableExists(TABLE_UNIDENTIFIED_RECORDS)) {
            CreateUnidentifiedRecordTable();
        }

        if(!isTableExists(TABLE_DOWNLOADEDLOGS_USA_RECORDS)) {
            CreateDownloadLogsUsaRecordTable();
        }

        if(!isTableExists(TABLE_DOWNLOADEDLOGS_CANADA_RECORDS)) {
            CreateDownloadLogsCanadaRecordTable();
        }


        if(!isTableExists(TABLE_BLE_GPS_APPLAUNCH_LOGS)) {
            CreateBleGpsAppLaunchLogTable();
        }


    }




    /* ---------------------- Check Table Existance -------------------- */
    public boolean isTableExists(String tableName) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        //  if(openDb) {
        if(mDatabase == null || !mDatabase.isOpen()) {
            mDatabase = getReadableDatabase();
        }

        if(!mDatabase.isReadOnly()) {
            mDatabase.close();
            mDatabase = getReadableDatabase();
        }
        //  }

        String query =  "SELECT name FROM sqlite_master WHERE type='table' AND name='" +tableName+ "'";
        Cursor cursor = mDatabase.rawQuery(query, null);
        if(cursor!=null) {
            if(cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }




    public Cursor getAllLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_DRIVER_LOG, null );
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_DRIVER_LOG);
        return numRows;
    }

    public Integer DeleteLogWithDate(Integer date) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DRIVER_LOG,
                DRIVER_ID_KEY + " = ? ",
                new String[] { Integer.toString(date) });
    }


}
