package com.constants;


public class APIs {

    /* -------- Upload ALS ELD updated APK to ALS server URL --------- */
    // https://alsrealtime.com/Home/AndroidAPK

    /* ---------- (Manual Driver) Android OBD APK upload link ----------- */
    // http://eld.alsrealtime.com/Home/ManualAndroidOBDAPK
    // http://develd.alsrealtime.com/Home/ManualAndroidOBDAPK

    /* -------- Upload OBD server updated APK to ALS server URL --------- */
    // http://eld.alsrealtime.com/Home/AndroidOBDAPK


    /* ----------- Wired OBD log files -------- */
    //http://eld.alsrealtime.com/Home/DriverLogFile



    static String EditLogGraphLocal             = "http://103.20.169.122:9287/";
    static String EditLogGraphProduction        = "https://alsrealtime.com/";


    /*=============== ALS SERVER URL =============== */
    static String ALS_DOMAIN_DEV         = "http://dev.alsrealtime.com/api/LogisticsApi/";
    static String ALS_DOMAIN_SINGAPORE 	 = "http://103.20.169.122:9287/api/LogisticsApi/";       //http://arethos.com:55557
    static String ALS_DOMAIN_PRODUCTION  = "https://alsrealtime.com/api/LogisticsApi/";

    /*=============== ELD SERVER URL =============== */
    static String ELD_DOMAIN_DEV         = "http://develd.alsrealtime.com/api/ELDAPI/";
    static String ELD_DOMAIN_SINGAPORE   = "http://103.20.169.122:9289/api/ELDAPI/";            //http://arethos.com:8285
    static String ELD_DOMAIN_PRODUCTION  = "https://eld.alsrealtime.com/api/ELDAPI/";            //http://104.167.9.210:8285/


    static String ALS_DOMAIN_INDIAN     = "http://182.73.78.171:8286/api/LogisticsApi/";
    static String ELD_DOMAIN_INDIAN     = "http://182.73.78.171:8287/api/ELDAPI/";

    static String ALS_DOMAIN_INDIAN1    = "http://192.168.0.2:8286/api/LogisticsApi/";       //  http://103.66.204.26
    static String ELD_DOMAIN_INDIAN1    = "http://192.168.0.2:8287/api/ELDAPI/";             //http://182.73.78.171:8287


    static String ALS_DOMAIN_DEV_NEW    = "http://104.167.9.210:43850/api/LogisticsApi/";
    static String ELD_DOMAIN_DEV_NEW    = "http://104.167.9.210:43851/api/ELDAPI/";


    /*========================= API URLs =========================*/
    public static String DOMAIN_URL_ALS 					= ALS_DOMAIN_PRODUCTION ;     	// ALS DOMAIN
    public static String DOMAIN_URL_ELD           			= ELD_DOMAIN_PRODUCTION ;  	// ELD DOMAIN

    public static String DOT_LOG_URL                        = DOMAIN_URL_ALS + "/DriverLog/MobileELDView?driverId=";

    public static String LOGIN_DEMO 						= DOMAIN_URL_ALS + "LoginDemo/";
    public static String LOGIN_USER 						= DOMAIN_URL_ALS + "login/";
    public static String VEHICLE_TRACKING 					= DOMAIN_URL_ALS + "VehicleTracking";
    public static String DRIVER_LOGOUT      				= DOMAIN_URL_ALS + "DriverLogout";
    public static String SEND_LOG      	 					= DOMAIN_URL_ALS + "EldInseption";
    public static String GET_APP_VERSION					= DOMAIN_URL_ALS + "GetAppVersion";
    public static String SAVE_SCREEN        				= DOMAIN_URL_ALS + "SaveScreenShots";
    public static String GET_DRIVER_STATUS      			= DOMAIN_URL_ALS + "GetDriverLogDetail";   //GetDriverStatusAndLocation
    public static String GET_DRIVER_STATUS_PERMISSION      	= DOMAIN_URL_ALS + "GetDriverStatusPermission";
    public static String GET_ANDROID_APP_DETAIL      	    = DOMAIN_URL_ALS + "GetAndroidAppDetail";
    public static String EditDriverLog	  	                = EditLogGraphProduction + "DriverLog/EditDriverLogOnMobile?Date=";  //http://arethos.com:55557
    public static String MOBILE_ELD_VIEW             	    = DOMAIN_URL_ALS + "MobileELDView";
    public static String MOBILE_ELD_VIEW_NEW           	    = DOMAIN_URL_ALS + "MobileELDViewAddedNewStatus";
    public static String GET_ELD_HELP_DOC                   = DOMAIN_URL_ALS + "GetEldHelpDoc";
    public static String GET_DRIVER_MILES                   = DOMAIN_URL_ALS + "GetDriverMilesFromLogDate";
    public static String SAVE_DRIVER_DEVICE_USAGE_LOG       = DOMAIN_URL_ALS + "SaveDriverDeviceUsageLog";
    public static String GET_UNIDENTIFIED_RECORDS	  	    = DOMAIN_URL_ALS + "GetUnidentifiedRecordListByVIN";
    public static String CLAIM_UNIDENTIFIED_RECORD	  	    = DOMAIN_URL_ALS + "ClaimUnidentifiedRecord";
    public static String REJECT_UNIDENTIFIED_RECORD	  	    = DOMAIN_URL_ALS + "RejectUnidentifiedRecord";
    public static String REJECT_COMPANY_ASSIGNED_RECORD	  	= DOMAIN_URL_ALS + "RejectCompanyAssignedRecord";
    public static String CLEAR_MALFNCN_DIAGSTC_EVENT	  	= DOMAIN_URL_ALS + "ClearMalfunctionAndDiagnosticEvent";
    public static String CHANGE_STATUS_SUGGESTED_EDIT       = DOMAIN_URL_ALS + "ChangeStatusSuggestedEdit";

    /*-------- ELD API ----------*/
    public static String SAVE_DRIVER_STATUS     			= DOMAIN_URL_ELD + "SaveELDDriverStatus";  //SaveDriverStatusAndLocation
    public static String SAVE_DRIVER_EDIT_LOG     			= DOMAIN_URL_ELD + "SaveDriverEditELDStatus";
    //  public static String GET_DRIVER_STATUS      			= DOMAIN_URL_ELD + "GetDriverStatusAndLocation";   //GetDriverLogDetail
    public static String GET_ONDUTY_REMARKS					= DOMAIN_URL_ELD + "GetOnDutyRemarks";
    public static String GET_NOTIFICATION_LOG      			= DOMAIN_URL_ELD + "GetNotificationLog";
    public static String GET_OBD_ASSIGNED_VEHICLES      	= DOMAIN_URL_ELD + "GetOBDAssignedVehicles";
    public static String UPDATE_OBD_ASSIGNED_VEHICLE      	= DOMAIN_URL_ELD + "UpdateOBDAssignedVehicle";
    public static String UPDATE_TRAILER_NUMBER            	= DOMAIN_URL_ELD + "SaveTrailor";
    public static String SAVE_LOG_SIGN      				= DOMAIN_URL_ELD + "SaveLogSign";
    public static String GET_INSPECTION_DETAIL   			= DOMAIN_URL_ELD + "GetInspectionDetail";
    public static String SAVE_INSPECTION      				= DOMAIN_URL_ELD + "SaveInspection";
    public static String SAVE_INSPECTION_OFFLINE			= DOMAIN_URL_ELD + "SaveInspectionOffline";
    public static String GET_SAVED_INSPECTION      			= DOMAIN_URL_ELD + "GetSavedInspection";
    public static String GET_ODOMETER      					= DOMAIN_URL_ELD + "GetOdometer";
    public static String GET_Add_FROM_LAT_LNG				= DOMAIN_URL_ELD + "GetAddressFromLatLong";
    public static String SAVE_ODOMETER      				= DOMAIN_URL_ELD + "SaveOdometer";
    public static String SAVE_ODOMETER_OFFLINE  			= DOMAIN_URL_ELD + "SaveOdometerOffLine";
    public static String GET_SHIPPING_DOC_NUMBER			= DOMAIN_URL_ELD + "GetShippingDocNumber";
    public static String SAVE_SHIPPING_DOC_NUMBER      		= DOMAIN_URL_ELD + "SaveShippingDocInfoList"; //SaveShippingDocNumber
    public static String UPDATE_DRIVER_LATLONG_LOCATION		= DOMAIN_URL_ELD + "UpdateDriverLatLongLocation";
    public static String UPDATE_OFF_LINE_DRIVER_LOG	    	= DOMAIN_URL_ELD + "UpdateOfflineDriverLog";
    public static String GET_DRIVER_LOG_18_DAYS	    	    = DOMAIN_URL_ELD + "GetDriverLogEighteenDaysDetail";
    public static String GET_DRIVER_LOG_18_DAYS_DETAILS 	= DOMAIN_URL_ELD + "GetDriverLogEighteenDaysPastDetail";
    public static String GET_SHIPPING_INFO_OFFLINE	  	    = DOMAIN_URL_ELD + "GetShippingInfoOffline";
    public static String GET_ODOMETER_OFFLINE	  	        = DOMAIN_URL_ELD + "GetOdometerOffline";
    public static String SAVE_LOG_TEXT_FILE 	  	        = DOMAIN_URL_ELD + "SaveLogTextFile";
    public static String SAVE_WIRED_LOG_FILE 	  	        = DOMAIN_URL_ELD + "SaveDriverLogTextFile";
    public static String SAVE_APP_USAGE_LOG_FILE 	        = DOMAIN_URL_ELD + "AppUsageLog";
    public static String SAVE_LAT_LONG 	  	                = DOMAIN_URL_ELD + "SaveLatLong";
    public static String CHECK_CONNECTION 	  	            = DOMAIN_URL_ELD + "Connection";
    public static String CONNECTION_UTC_DATE  	            = DOMAIN_URL_ELD + "ConnectionUTCDate";
    public static String DRIVER_VIOLATION_PERMISSION 	  	= DOMAIN_URL_ELD + "DriverViolationTestPermission";
    public static String GET_OFFLINE_INSPECTION_LIST  	    = DOMAIN_URL_ELD + "GetOfflineInspectionList";
    public static String CERTIFY_LOG_OFFLINE  	            = DOMAIN_URL_ELD + "CertifyLogOffline";
    public static String SAVE_NOTIFICATION  	            = DOMAIN_URL_ELD + "SaveNotification";
    public static String CLEAR_NOTIFICATION_LOG	            = DOMAIN_URL_ELD + "ClearNotificationLog";

    public static String SAVE_17_INSPECTION_OFFLINE			= DOMAIN_URL_ELD + "SaveSeventeenPointInspectionOffline";
    public static String GET_OFFLINE_17_INSPECTION_LIST     = DOMAIN_URL_ELD + "GetOfflineSeventeenPointInspectionList";
    public static String UPDATE_DRIVER_INSPECTION_LOC       = DOMAIN_URL_ELD + "UpdateDriverInspectionLocation";
    public static String UPDATE_DRIVER_LOG_RECORD 	  	    = DOMAIN_URL_ELD + "UpdateDriverLogRecordFromApp";
    public static String GET_MANNUAL_APK_DETAIL             = DOMAIN_URL_ELD + "GetManualAndroidOBDAppDetail";
    public static String GET_MALFUNCTION_EVENTS             = DOMAIN_URL_ELD + "MalfunctionAndDiagnosticEvents";
    public static String UPDATE_DRIVER_VERSION_IGNORE_COUNT = DOMAIN_URL_ELD + "UpdateDriverVersionIgnoreCount";
    public static String GET_NOTIFICATIONS                  = DOMAIN_URL_ELD + "GetNotifications";
    public static String GET_RECERTIFY_PENDING_RECORDS      = DOMAIN_URL_ELD + "GetRecertifyPendingRecords";
    public static String GET_SUGGESTED_RECORDS              = DOMAIN_URL_ELD + "GetSuggestedRecords";
    public static String SAVE_CERTIFY_SIGN_REJECTED_AUDIT   = DOMAIN_URL_ELD + "SaveCertifySignRejectedAudit";
    public static String GET_DRIVER_SETTINGS                = DOMAIN_URL_ELD + "GetDriverSettings";


}



/*

    public static String GET_DRIVER_JOB 					= DOMAIN_URL_ALS + "GetDriverJob/";
    public static String GET_LOAD_DETAIL 					= DOMAIN_URL_ALS + "GetLoadDetail/";
    public static String LOAD_CANCELLED_PARAMETERS 			= DOMAIN_URL_ALS + "LoadCancelled/";
    public static String LOAD_PICKED_UP 					= DOMAIN_URL_ALS + "LoadPickedup/";
    public static String LOAD_DELIVERED 					= DOMAIN_URL_ALS + "LoadDelivered/";
    public static String TRIP_HISTORY 						= DOMAIN_URL_ALS + "GetDriverDeliveredTrip/";
    public static String TRIP 								= DOMAIN_URL_ALS + "GetDriverTrip/";   		// (DriverId, deviceID)
    public static String CONFIRM_DELIVERY 					= DOMAIN_URL_ALS + "UpdateLoadDelivered/";
    public static String PHOTO_UPLOAD 						= DOMAIN_URL_ALS + "UploadDeliveryDocument/";
    public static String DOC_UPLOAD 						= DOMAIN_URL_ALS + "UploadLocalDeliveryDocument/";
    public static String GET_JOB_LOAD_TYPE 					= DOMAIN_URL_ALS + "GetJobLoadTypes/";
    public static String GET_TRIP_ROUTE 					= DOMAIN_URL_ALS + "GetTripRoute/";
    public static String CONFIRMED_PICK_DROP_LOAD 			= DOMAIN_URL_ALS + "ConfirmedPickAndDropLoad";
    public static String GET_CURRENY_EXPENSE 				= DOMAIN_URL_ALS + "GetCurrenyAndDriverExpenseReasonTypes";
    public static String UPDATE_DRIVER_TRIP_EXPENSES_DETAIL = DOMAIN_URL_ALS + "UpdateDriverTripExpensesDetail";
    public static String GET_DRIVER_TRIP_EXPENSES_DETAIL 	= DOMAIN_URL_ALS + "getDriverTripExpensesDetail";
    public static String MAKE_LOAD_ARRIVED 					= DOMAIN_URL_ALS + "MakeLoadArrived";
    public static String GET_TRIP_DETAILS 					= DOMAIN_URL_ALS + "GetDriverTripDetailBySearchDate";
    public static String SAVE_DRIVER_SETTINGS				= DOMAIN_URL_ALS + "UpdateDriverSetting";


    */
