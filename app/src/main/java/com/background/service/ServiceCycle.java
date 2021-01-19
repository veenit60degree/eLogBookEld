package com.background.service;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.driver.details.EldDriverLogModel;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.LatLongHelper;
import com.local.db.LocationMethod;
import com.local.db.NotificationMethod;
import com.local.db.OdometerHelperMethod;
import com.local.db.ShipmentHelperMethod;
import com.local.db.SyncingMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.models.EldDataModelNew;
import com.models.NotificationHistoryModel;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.CoNotificationPref;
import com.shared.pref.EldCoDriverLogPref;
import com.shared.pref.EldSingleDriverLogPref;
import com.shared.pref.MainDriverEldPref;
import com.shared.pref.NotificationPref;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;

// Singh@#321
public class ServiceCycle implements TextToSpeech.OnInitListener {
    Context context;

    Constants constants;
    SharedPref sharedPref;
    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;
    EldSingleDriverLogPref eldSharedPref;
    EldCoDriverLogPref coEldSharedPref;
    List<DriverLog> oDriverLogDetail;
    DriverDetail oDriverDetail;
    RulesResponseObject RulesObj;
    RulesResponseObject RemainingTimeObj;
    Globally Global;
    String DRIVER_LOG_18DAYS    = "driver_log_18_days";
    String SET_DATA_ON_VIEW     = "set_data_on_view";
    String LastStatus           = "";
    String DeviceId             = "";
    String connectionSource     = "";

    boolean IsAppForground   = true;
    boolean isViolation      = false;
    int DriverId = 0, offsetFromUTC = 0;
    final int OFF_DUTY       = 1;
    final int SLEEPER        = 2;
    final int DRIVING        = 3;
    final int ON_DUTY		 = 4;

    final int WIRED_OBD      = 1001;
    final int WIFI_OBD       = 1002;
    final int API            = 1003;
    final int OFFLINE        = 1004;



    public static int ContinueSpeedCounter = 0;
    int ZeroSpeed	         = 2;    // For now 0km speed limt is 2.
    int AobrdSpeedLimit      = 10;   // initially the limit was 30 km but now we are validate AOBRD Auto for driving with 10 km speed limit.

    int OffDutyInterval      = 5;
    int OffDutySpeedLimit    = 0;

    int DrivingInterval      = 0;
    int DrivingSpeedLimit    = 10;

    int OnDutyInterval       = 5;
    int OnDutySpeedLimit     = 0;

    int CHANGED_STATUS       = 1;
    int VehicleSpeed         = 0;
    int DriverType           = 0;
    int OBDVehicleSpeed ;
    int GPSVehicleSpeed ;
    int connectionType;

    boolean isOldRecord      = false;
    boolean IsAlertTimeValid = false;
    boolean isSingleDriver   = true;
    boolean isALSConnection;


    String CurrentCycleId = "", message = "", DriverName = "", DriverCompanyId = "", TruckNo = "";;
    int DRIVER_JOB_STATUS;
    private TextToSpeech textToSpeech;

    ShipmentHelperMethod shipmentHelper;
    OdometerHelperMethod odometerhMethod;

    NotificationMethod notificationMethod;
    NotificationPref notificationPref;
    CoNotificationPref coNotificationPref;
    JSONArray driver18DaysLogArray = new JSONArray();


    public ServiceCycle(Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, this);
        Global = new Globally();
        notificationPref = new NotificationPref();
        coNotificationPref = new CoNotificationPref();

    }


    public void CalculateCycleTime(int driverId, final boolean IsLogApiACalled, boolean isAlertTimeValid, int vehicleSpeed,
                                   final HelperMethods hMethods, final DBHelper dbHelper, final LatLongHelper latLongHelper,
                                   final LocationMethod locMethod, final ServiceCallback serviceResponse,
                                   final ServiceError serviceError, NotificationMethod notiMethod,
                                   ShipmentHelperMethod shipingHelper,  OdometerHelperMethod odometerMethod,
                                   boolean isConnection, int connection_type, int obdVehicleSpeed, int gpsVehicleSpeed ) {

        DriverId                 = driverId;
        VehicleSpeed             = vehicleSpeed;
        oDriverLogDetail         = new ArrayList<DriverLog>();
        RulesObj                 = new RulesResponseObject();
        RemainingTimeObj         = new RulesResponseObject();
        eldSharedPref            = new EldSingleDriverLogPref();
        coEldSharedPref          = new EldCoDriverLogPref();
        constants                = new Constants();
        sharedPref               = new SharedPref();
        notificationMethod       = notiMethod;
        shipmentHelper           = shipingHelper;
        odometerhMethod          = odometerMethod;
        isALSConnection          = isConnection;
        connectionType           = connection_type;
        OBDVehicleSpeed          = obdVehicleSpeed;
        GPSVehicleSpeed          = gpsVehicleSpeed;

        getConnectionSource(connectionType);

        final DateTime currentDateTime = Global.getDateTimeObj(Global.GetCurrentDateTime(), false);    // Current Date Time
        final DateTime currentUTCTime  = Global.getDateTimeObj(Global.GetCurrentUTCTimeFormat(), true);
        //offsetFromUTC            = currentDateTime.getHourOfDay() - currentUTCTime.getHourOfDay();
        offsetFromUTC            = (int) Global.GetTimeZoneOffSet();
        IsAlertTimeValid         = isAlertTimeValid;
        IsAppForground           = UILApplication.isActivityVisible();
        DeviceId                 = sharedPref.GetSavedSystemToken(context);
        isSingleDriver           = Global.isSingleDriver(context);


        try {
            driver18DaysLogArray = hMethods.getSavedLogArray(DriverId, dbHelper);

            Thread.sleep(2200);

            CheckEldRule(driver18DaysLogArray, currentDateTime, currentUTCTime,  isSingleDriver,  IsLogApiACalled,
                    serviceResponse,  latLongHelper,  locMethod,
                    hMethods,  dbHelper,  serviceError);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    void CheckEldRule(JSONArray driverLogArray, DateTime currentDateTime,DateTime currentUTCTime, boolean isSingleDriver, boolean IsLogApiACalled,
                      ServiceCallback serviceResponse, LatLongHelper latLongHelper, LocationMethod locMethod,
                      HelperMethods hMethods, DBHelper dbHelper, ServiceError serviceError){
        if (driverLogArray.length() == 0) {

            try {
                driverLogArray = hMethods.getSavedLogArray( DriverId, dbHelper);
            } catch (Exception e) {
                e.printStackTrace();
                driverLogArray = new JSONArray();
            }
        }

        try {
            if (sharedPref.getDriverType(context).equals(DriverConst.SingleDriver)) {
                isSingleDriver = true;
            } else {
                isSingleDriver = false;
            }

            if(sharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
                DriverType = 0;     // Single Driver Type and Position is 0
                CurrentCycleId  = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context);
            } else {                // If Current driver is Co Driver
                DriverType = 1;
                CurrentCycleId  = DriverConst.GetCoDriverCurrentCycle(DriverConst.CoCurrentCycleId, context);
            }

            if(CurrentCycleId.equalsIgnoreCase("null") || CurrentCycleId.equalsIgnoreCase("-1")
                    || CurrentCycleId.length() == 0 ){
                CurrentCycleId = Globally.USA_WORKING_7_DAYS;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // Get Driver's few details
        getDriverDetails();

        if (driverLogArray.length() == 0) {
            serviceError.onServiceError(DRIVER_LOG_18DAYS, IsAppForground);
        }else{
            JSONArray selectedArray = hMethods.GetSelectedDateArray(driverLogArray, String.valueOf(DriverId), currentDateTime, currentDateTime,
                    currentUTCTime, offsetFromUTC, 2, dbHelper);
            //  JSONArray currentDayArray = hMethods.GetSingleDateArray(driverLogArray, currentDateTime, currentDateTime,  currentUTCTime);

            JSONObject lastJsonItem = new JSONObject();
            boolean isArrayNull = false;
            try {

                if (selectedArray.length() == 0) {
                    isArrayNull = true;
                    serviceError.onServiceError(DRIVER_LOG_18DAYS, IsAppForground);
                    // GetDriverLog18Days(DRIVER_ID, DeviceId, Global.GetCurrentUTCDate(), GetDriverLog18Days);
                } else {
                    lastJsonItem = (JSONObject) selectedArray.get(selectedArray.length() - 1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            // Calculate 18 days log data
            if(!isArrayNull) {
                try {
                    DRIVER_JOB_STATUS     = lastJsonItem.getInt(ConstantsKeys.DriverStatusId);
                    String currentJob     = sharedPref.getDriverStatusId("jobType", context);

                    String prevStartDate  = Global.ConvertDateFormatMMddyyyy(lastJsonItem.getString(ConstantsKeys.startDateTime));
                    String CurrentDate    = Global.ConvertDateFormatMMddyyyy(currentDateTime.toString() );

                    oDriverDetail = new DriverDetail();
                    oDriverLogDetail = hMethods.getSavedLogList(DriverId, currentDateTime, currentUTCTime, dbHelper);
                    if (oDriverLogDetail == null || oDriverLogDetail.size() == 0) {
                        if (!IsLogApiACalled) {
                            serviceError.onServiceError(DRIVER_LOG_18DAYS, IsAppForground);
                        }
                        // GetDriverLog18Days(DRIVER_ID, DeviceId, Global.GetCurrentUTCDate(), GetDriverLog18Days);
                    } else {


                        if(prevStartDate.equals(CurrentDate)) {     // Means both date are same
                            CycleTimeCalculation(currentDateTime, currentUTCTime, isSingleDriver, DRIVER_JOB_STATUS,
                                    DriverType, driverLogArray, hMethods, dbHelper, serviceResponse);
                        }else {
                            // When date is changed or new day is started..

                            // We are carry forward same entry in i8 days Driver log array, only start time changed with current time..
                            String currentDateMMddyyyyy = CurrentDate;
                            prevStartDate        = Global.ConvertDateFormat(prevStartDate);
                            CurrentDate          = Global.ConvertDateFormat(CurrentDate);
                            DateTime startDate   = Global.getDateTimeObj(prevStartDate, false);
                            DateTime currentDate = Global.getDateTimeObj(CurrentDate, false);

                            if( currentDate.isAfter(startDate)){

                                latLongHelper.LatLongHelper(dbHelper, new JSONArray());  // Clear previous date lat long from array

                                // Update Last entry With New Object updated date time in Driver log 18 days array
                                hMethods.UpdateLastWithAddNewObject(driverLogArray, String.valueOf(DRIVER_JOB_STATUS) , offsetFromUTC, dbHelper);



                                // Update Last entry With New Object updated date time in shipping detail 18 days array
                                // Shipping Details (Carry forward same entry in i8 days shipping array, only ShippingDocDate & shippingdate will be  changed with current time..
                                JSONArray Shipping18DaysArray   = shipmentHelper.getShipment18DaysArray(Integer.valueOf(DriverId), dbHelper);
                                JSONArray reverseArray          = shipmentHelper.ReverseArray(Shipping18DaysArray);

                                if(Shipping18DaysArray.length() > 0){
                                    JSONObject lastObj = (JSONObject) Shipping18DaysArray.get(0);
                                    JSONObject splitObj = shipmentHelper.updateSplitItemInShipmentArray(lastObj, currentDateMMddyyyyy, String.valueOf(currentUTCTime), CurrentDate);

                                    // add split JSON Object in array with current date time
                                    reverseArray.put(splitObj);

                                    JSONArray finalShipping18DaysArray = shipmentHelper.ReverseArray(reverseArray);

                                    shipmentHelper.Shipment18DaysHelper( DriverId, dbHelper, finalShipping18DaysArray);
                                }

                            }


                        }

                    }


                    boolean isPersonal = lastJsonItem.getBoolean(ConstantsKeys.Personal);
                    boolean isAutoDrive = sharedPref.isAutoDrive(context);

                    if(currentJob.equals(String.valueOf(DRIVER_JOB_STATUS))) {  // reason of this check: some times latest entry was not saved in 18 days array due to unknown/strange error. thats why wrong auto status entry was saved from app. So we need to add this check for safe side.
                        // Check If vehicle is ELD Type
                        if (!isPersonal && isAutoDrive && VehicleSpeed != -1) {
                            try {

                                // ------------------ Get current Job Status -----------------
                                String JobStatusStr = Global.JobStatus(DRIVER_JOB_STATUS, false);
                                DateTime startDate = Global.getDateTimeObj(lastJsonItem.getString(ConstantsKeys.startDateTime), false);
                                DateTime currentdateTime = Global.getDateTimeObj(currentDateTime.toString(), false);

                                long diffInMillis = currentdateTime.getMillis() - startDate.getMillis();
                                long minutesDiff = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);

                                if (minutesDiff == 0) {
                                    BackgroundLocationService.IsAutoChange = false;
                                }


                                // ---------- Get Driver Configured Time Details ------------
                                getConfiguredTime();


                                boolean IsAOBRD = sharedPref.IsAOBRD(context);
                                boolean IsAOBRDAutomatic = sharedPref.IsAOBRDAutomatic(context);
                                boolean IsAOBRDAutoDrive = sharedPref.IsAOBRDAutoDrive(context);


                                if (DRIVER_JOB_STATUS == OFF_DUTY || DRIVER_JOB_STATUS == SLEEPER) {

                                    if (IsAOBRD) {
                                        if (IsAOBRDAutomatic) {
                                            String jobStatus;
                                            if (DRIVER_JOB_STATUS == OFF_DUTY) {
                                                jobStatus = "off duty";
                                            } else {
                                                jobStatus = "sleeper";
                                            }

                                            if (VehicleSpeed > AobrdSpeedLimit && minutesDiff >= DrivingInterval) {

                                                if (IsAOBRDAutoDrive) {

                                                    if (BackgroundLocationService.IsAutoChange) {
                                                        message = "Your current status is " + JobStatusStr + " but your vehicle is running. Now your status is going to be changed to Driving.";
                                                    } else {
                                                        message = "Your current status is " + JobStatusStr + " but your vehicle is running. Please change your status to Driving.";
                                                    }

                                                    // added new lines according to AOBRD changes..
                                                    /* =================================================================================== */
                                                    if (hMethods.getSecondLastJobStatus(driver18DaysLogArray) == DRIVING) {
                                                        int minDiff = hMethods.getTimeDiffBwLast2Job(driver18DaysLogArray);
                                                        if (Math.max(-30, minDiff) == Math.min(minDiff, 30)) {
                                                            BackgroundLocationService.IsAutoChange = true;
                                                            message = "Your current status is " + JobStatusStr + " but your vehicle is running. Your status is going to be changed to Driving.";
                                                        }
                                                    }
                                                    /* =================================================================================== */

                                                    LastStatus = "_aobrd_From_" + DRIVER_JOB_STATUS;
                                                    CHANGED_STATUS = DRIVING;
                                                    ChangeStatusWithAlertMsg(VehicleSpeed, serviceResponse, dbHelper, hMethods,
                                                            driverLogArray, currentDateTime, currentUTCTime, CHANGED_STATUS, false, IsAOBRDAutomatic);


                                                } else {
                                                    message = "Your current status is " + jobStatus + " but your vehicle is running";
                                                    SpeakOutMsg(message);
                                                    serviceResponse.onServiceResponse(RulesObj, RemainingTimeObj, IsAppForground, false, constants.AobrdWarning, jobStatus);
                                                }
                                            }
                                        }
                                    } else {

                                        if (VehicleSpeed > DrivingSpeedLimit && minutesDiff >= DrivingInterval) {

                                            if (BackgroundLocationService.IsAutoChange) {
                                                message = "Your current status is " + JobStatusStr + " but your vehicle is running. Now your status is going to be changed to Driving.";
                                            } else {
                                                message = "Your current status is " + JobStatusStr + " but your vehicle is running. Please change your status to Driving.";
                                            }

                                            LastStatus = "_eld_From_" + DRIVER_JOB_STATUS;
                                            CHANGED_STATUS = DRIVING;
                                            ChangeStatusWithAlertMsg(VehicleSpeed, serviceResponse, dbHelper, hMethods,
                                                    driverLogArray, currentDateTime, currentUTCTime, CHANGED_STATUS, true, IsAOBRDAutomatic);

                                        } else {
                                            ContinueSpeedCounter = 0;
                                            ClearCount();
                                        }

                                    }

                                } else if (DRIVER_JOB_STATUS == DRIVING) {

                                    if (minutesDiff > OnDutyInterval) {   // && IsAlertTimeValid

                                        boolean isApplicable = false;

                                        if (connectionType == constants.API) {
                                            if (VehicleSpeed <= OnDutySpeedLimit && GPSVehicleSpeed <= OnDutySpeedLimit) {    //VehicleSpeed <= OnDutySpeedLimit && (
                                                isApplicable = true;
                                            }
                                        } else if (connectionType == constants.WIRED_OBD || connectionType == constants.WIFI_OBD) {
                                            if (OBDVehicleSpeed <=  OnDutySpeedLimit && GPSVehicleSpeed < OnDutySpeedLimit) {
                                                isApplicable = true;
                                            }
                                        }else {
                                            if (GPSVehicleSpeed <= OnDutySpeedLimit) {
                                                isApplicable = true;
                                            }
                                        }


                                        if (isApplicable) {

                                            if (!IsAOBRD || (IsAOBRD && IsAOBRDAutoDrive)) {
                                                if (BackgroundLocationService.IsAutoChange) {
                                                    message = "Your current status is Driving but your vehicle is running below the driving speed limit. Now your status is going to be changed to On Duty.";
                                                } else {
                                                    message = "Your current status is Driving but your vehicle is running below the driving speed limit. Please change your status to On Duty.";
                                                }

                                                CHANGED_STATUS = ON_DUTY;

                                                boolean isEldToast;
                                                if (IsAOBRD) {
                                                    isEldToast = false;
                                                } else {
                                                    isEldToast = true;
                                                }

                                                if (ContinueSpeedCounter >= OnDutyInterval) {
                                                    LastStatus = "_eld_From_" + DRIVER_JOB_STATUS;
                                                    ChangeStatusWithAlertMsg(VehicleSpeed, serviceResponse, dbHelper, hMethods,
                                                            driverLogArray, currentDateTime, currentUTCTime, CHANGED_STATUS, isEldToast, IsAOBRDAutoDrive);
                                                }
                                                ContinueSpeedCounter++;
                                            }

                                        } else {
                                            ContinueSpeedCounter = 0;
                                            ClearCount();
                                        }
                                    } else {
                                        if (VehicleSpeed <= OnDutySpeedLimit) {
                                            ContinueSpeedCounter++;
                                        } else {
                                            ContinueSpeedCounter = 0;
                                        }

                                        ClearCount();
                                    }

                                } else if (DRIVER_JOB_STATUS == ON_DUTY) {


                                    boolean isYardMove = lastJsonItem.getBoolean(ConstantsKeys.YardMove);

                                    if (!isYardMove) {   // if isYardMove = true. No ELD rule called when Truck in yardfor USA.
                                        if (BackgroundLocationService.IsAutoChange) {
                                            message = "Your current status is On Duty but your vehicle is running. Now your status is going to be changed to Driving.";
                                        } else {
                                            message = "Your current status is On Duty but your vehicle is running. Please change your status to Driving.";
                                        }


                                        if (IsAOBRD) {
                                            if (IsAOBRDAutomatic) {
                                                if (VehicleSpeed > AobrdSpeedLimit && minutesDiff >= DrivingInterval) {

                                                    if (IsAOBRDAutoDrive) {

                                                        // added new lines according to AOBRD changes..
                                                        /* =================================================================================== */
                                                        if (hMethods.getSecondLastJobStatus(driver18DaysLogArray) == DRIVING) {
                                                            int minDiff = hMethods.getTimeDiffBwLast2Job(driver18DaysLogArray);
                                                            if (Math.max(-30, minDiff) == Math.min(minDiff, 30)) {
                                                                BackgroundLocationService.IsAutoChange = true;
                                                                message = "Your current status is On Duty but your vehicle is running. Your status is going to be changed to Driving.";
                                                            }
                                                        }
                                                        /* =================================================================================== */

                                                        LastStatus = "_aobrd_From_" + DRIVER_JOB_STATUS;
                                                        CHANGED_STATUS = DRIVING;
                                                        ChangeStatusWithAlertMsg(VehicleSpeed, serviceResponse, dbHelper, hMethods,
                                                                driverLogArray, currentDateTime, currentUTCTime, CHANGED_STATUS, false, IsAOBRDAutomatic);
                                                    } else {
                                                        String jobStatus = "on duty";
                                                        message = "Your current status is " + jobStatus + " but your vehicle is running";
                                                        SpeakOutMsg(message);
                                                        serviceResponse.onServiceResponse(RulesObj, RemainingTimeObj, IsAppForground, false, constants.AobrdWarning, jobStatus);
                                                    }
                                                } else {
                                                    ContinueSpeedCounter = 0;
                                                    ClearCount();
                                                }

                                            }
                                        } else {

                                            boolean isApplicable = false;
                                            if (connectionType == constants.WIRED_OBD || connectionType == constants.WIFI_OBD) {
                                                if (OBDVehicleSpeed > DrivingSpeedLimit && VehicleSpeed > DrivingSpeedLimit) {    //VehicleSpeed <= OnDutySpeedLimit && (
                                                    isApplicable = true;
                                                }
                                            } else {
                                                if (VehicleSpeed > DrivingSpeedLimit) {
                                                    isApplicable = true;
                                                }
                                            }

                                            if (isApplicable && minutesDiff >= DrivingInterval) {
                                                LastStatus = "_eld_From_" + DRIVER_JOB_STATUS;
                                                CHANGED_STATUS = DRIVING;
                                                ChangeStatusWithAlertMsg(VehicleSpeed, serviceResponse, dbHelper, hMethods,
                                                        driverLogArray, currentDateTime, currentUTCTime, CHANGED_STATUS, true, false);
                                            } else {
                                                ContinueSpeedCounter = 0;
                                                ClearCount();
                                            }
                                        }
                                    } else {
                                        // auto change in Yard Move only applicable in canada cycle
                                        if (CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2)) {
                                            if (BackgroundLocationService.IsAutoChange) {
                                                message = "Your current status is On Duty - Yard Move but your vehicle is moving more then 32 km/h in Canada Cycle. Now your status is going to be changed to Driving.";
                                            } else {
                                                message = "Your current status is On Duty - Yard Move but your vehicle is moving more then 32 km/h in Canada Cycle. Please change your status to Driving.";
                                            }

                                            boolean isApplicable = false;
                                            DrivingSpeedLimit = 32; // in canada cycle for Yard Move status if speed will be more then on equal to 32 km/h, change to driving.

                                            if (connectionType == constants.WIRED_OBD || connectionType == constants.WIFI_OBD) {
                                                if (OBDVehicleSpeed > DrivingSpeedLimit && VehicleSpeed > DrivingSpeedLimit) {    //VehicleSpeed <= OnDutySpeedLimit && (
                                                    isApplicable = true;
                                                }
                                            } else {
                                                if (VehicleSpeed > DrivingSpeedLimit) {
                                                    isApplicable = true;
                                                }
                                            }

                                            if (isApplicable && minutesDiff >= DrivingInterval) {
                                                LastStatus = "_YM_From_" + DRIVER_JOB_STATUS;
                                                CHANGED_STATUS = DRIVING;
                                                ChangeStatusWithAlertMsg(VehicleSpeed, serviceResponse, dbHelper, hMethods,
                                                        driverLogArray, currentDateTime, currentUTCTime, CHANGED_STATUS, true, false);
                                            } else {
                                                ContinueSpeedCounter = 0;
                                                ClearCount();
                                            }

                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            // auto change in personal use only applicable in canada cycle
                            if (isPersonal && (CurrentCycleId.equals(Global.CANADA_CYCLE_1) || CurrentCycleId.equals(Global.CANADA_CYCLE_2))) {
                                if (sharedPref.isPersonalUse75KmCrossed(context)) {
                                    DateTime lastSaveUtcDate = Global.getDateTimeObj(sharedPref.getCurrentUTCTime(context), false);

                                    int dayDiff = hMethods.DayDiff(currentUTCTime, lastSaveUtcDate);
                                    if (dayDiff == 0) {    // if current day

                                        if (BackgroundLocationService.IsAutoChange) {
                                            message = "Your Personal Use limit (75 km) is exceeded for the day in Canada cycle. Now your status is going to be changed to Driving.";
                                        } else {
                                            message = "Your Personal Use limit (75 km) is exceeded for the day in Canada cycle. Please change your status to Driving.";
                                        }

                                        if (VehicleSpeed >= DrivingSpeedLimit) {
                                            LastStatus = "_P_From_" + DRIVER_JOB_STATUS;
                                            CHANGED_STATUS = DRIVING;
                                            ChangeStatusWithAlertMsg(VehicleSpeed, serviceResponse, dbHelper, hMethods,
                                                    driverLogArray, currentDateTime, currentUTCTime, CHANGED_STATUS, true, false);
                                        } else {
                                            ContinueSpeedCounter = 0;
                                            ClearCount();
                                        }
                                    }
                                }
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    if (!IsLogApiACalled)
                        serviceError.onServiceError(DRIVER_LOG_18DAYS, IsAppForground);
                }
            }else{
                serviceError.onServiceError(SET_DATA_ON_VIEW, IsAppForground);
            }


        }
    }




    private void getConnectionSource(int connectionType){

        switch (connectionType){

            case WIRED_OBD:
                connectionSource = "Wired_tablet";
                break;

            case WIFI_OBD:
                connectionSource = "wifi_OBD";
                break;

            case API:
                connectionSource = "API";
                break;

            case OFFLINE:
                connectionSource = "offline_GPS";
                break;

            default:
                connectionSource = "other";
                break;


        }
    }


    private void ClearCount(){
        constants.ClearNotifications(context);
        BackgroundLocationService.IsAutoChange = false;
    }

    void getConfiguredTime(){

        if (DriverType == 0) {
            OffDutyInterval     = DriverConst.getDriverConfiguredTime(DriverConst.OffDutyMinute, context);
            OffDutySpeedLimit   = DriverConst.getDriverConfiguredTime(DriverConst.OffDutySpeed, context);

            DrivingInterval     = DriverConst.getDriverConfiguredTime(DriverConst.DrivingMinute, context);
            DrivingSpeedLimit   = DriverConst.getDriverConfiguredTime(DriverConst.DrivingSpeed, context);

            OnDutyInterval      = DriverConst.getDriverConfiguredTime(DriverConst.OnDutyMinute, context);
            OnDutySpeedLimit    = DriverConst.getDriverConfiguredTime(DriverConst.OnDutySpeed, context);


        } else{
            OffDutyInterval     = DriverConst.getCoDriverConfiguredTime(DriverConst.CoOffDutyMinute, context);
            OffDutySpeedLimit   = DriverConst.getCoDriverConfiguredTime(DriverConst.CoOffDutySpeed, context);

            DrivingInterval     = DriverConst.getCoDriverConfiguredTime(DriverConst.CoDrivingMinute, context);
            DrivingSpeedLimit   = DriverConst.getCoDriverConfiguredTime(DriverConst.CoDrivingSpeed, context);

            OnDutyInterval      = DriverConst.getCoDriverConfiguredTime(DriverConst.CoOnDutyMinute, context);
            OnDutySpeedLimit    = DriverConst.getCoDriverConfiguredTime(DriverConst.CoOnDutySpeed, context);
        }

    }


    void ChangeStatusWithAlertMsg(double VehicleSpeed,  ServiceCallback serviceResponse, DBHelper dbHelper, HelperMethods hMethods,
                                  JSONArray driverLogArray, DateTime currentDateTime, DateTime currentUTCTime, int CHANGE_STATUS, boolean isEldToast, boolean IsAOBRDAuto ){

        if(VehicleSpeed < 0){
            VehicleSpeed = 0;
        }

        String AlertMsg = "";
        if(message.contains("Personal Use limit") || message.contains("32 km/h in Canada Cycle")){
            AlertMsg = message;
        }else{
            AlertMsg = "Your vehicle speed is " + new DecimalFormat("##.##").format(VehicleSpeed) + " km/h." + message;
        }


        // --------- Showing notification to user to change there status...

        if(IsAOBRDAuto) {
            Log.d("AobrdAutomatic","AobrdAutomatic: " + constants.AobrdAutomatic );
            // hide notification for blank aobrd Auto notification
         //   serviceResponse.onServiceResponse(RulesObj, RemainingTimeObj, IsAppForground, isEldToast, constants.AobrdAutomatic, AlertMsg);
        }else{
            serviceResponse.onServiceResponse(RulesObj, RemainingTimeObj, IsAppForground, isEldToast, AlertMsg, "");
        }
        // ---------- Text to speech listener---------
        SpeakOutMsg(message);

        // --------- Change Wrong Status Automatically -----------
        if (BackgroundLocationService.IsAutoChange == true) {  //&& BackgroundLocationService.IsAutoLogSaved == false
                ChangeWrongStatusAutomatically(dbHelper, hMethods, driverLogArray, currentDateTime, currentUTCTime, CHANGE_STATUS, serviceResponse);
        }else{

            JSONArray notificationArray = notificationMethod.getSavedNotificationArray(Integer.valueOf(DriverId), dbHelper);
            if(notificationArray.length() == 0 && driver18DaysLogArray.length() > 15 ) {
                // nothing to save
            }else {
                boolean IsWarningNotificationAllowed = constants.IsWarningNotificationAllowed(String.valueOf(DriverId), dbHelper);
                if(IsWarningNotificationAllowed) {
                    constants.SaveNotification(DriverType, context.getResources().getString(R.string.warning), AlertMsg, currentDateTime.toString(), hMethods, notificationPref, coNotificationPref, context);

                    // Save Notification in 18 days notification history array
                    notificationMethod.saveNotificationHistory(
                            DriverId,
                            DeviceId,
                            DriverName,
                            context.getResources().getString(R.string.warning),
                            AlertMsg,
                            currentDateTime,
                            DriverCompanyId,
                            dbHelper);
                }
            }

        }
        BackgroundLocationService.IsAutoChange = true;

    }

    // get Obd odometer Data
    void saveOdometer(String DriverStatusId, HelperMethods hMethods, DBHelper dbHelper){

        if(isALSConnection) {
            int lastJobStatus = hMethods.getSecondLastJobStatus(driver18DaysLogArray);
            int currentJobStatus = Integer.valueOf(DriverStatusId);
            int odometerValue = Integer.valueOf(sharedPref.GetWifiObdOdometer(context));   // get odometer value from wifi obd

            // if wifi odometer value is 0, check Wired obd to get odometer value
            if(odometerValue == 0){
               // odometerValue = sharedPref.getHighPrecisionOdometer(context) ;    // get High Precision Odometer for more accuracy result

               // if(odometerValue == 0 ) {
                    odometerValue = sharedPref.getWiredObdOdometer(context);    // get odometer
              //  }
            }

            if(odometerValue != 0){
                if ((currentJobStatus == ON_DUTY || currentJobStatus == DRIVING) &&
                        (lastJobStatus == OFF_DUTY || lastJobStatus == SLEEPER)) {

                    odometerhMethod.AddOdometerAutomatically(String.valueOf(DriverId), DeviceId, String.valueOf(odometerValue), DriverStatusId, dbHelper, context);


                } else {

                    if (currentJobStatus == OFF_DUTY || currentJobStatus == SLEEPER) {
                        if (lastJobStatus == DRIVING || lastJobStatus == ON_DUTY) {

                            odometerhMethod.AddOdometerAutomatically(String.valueOf(DriverId), DeviceId, String.valueOf(odometerValue), DriverStatusId, dbHelper, context);
                        }
                    }
                }
            }
        }
    }



    public static int getMinutesFromMillis(long milliseconds) {
        return  (int) ((milliseconds / (1000 * 60)) % 60);
    }



    void ChangeWrongStatusAutomatically(DBHelper dbHelper, HelperMethods hMethods, JSONArray driverLogArray,
                                        DateTime currentDateTime , DateTime currentUTCTime, int ChangedDriverStatus,
                                        ServiceCallback serviceCallback ){
        JSONObject lastItemJson = hMethods.GetLastJsonFromArray(driverLogArray);
        if (lastItemJson != null) {
           // ContinueSpeedCounter = 0;
            BackgroundLocationService.IsAutoChange = false;
            boolean isAutomatic = true;

            if(ChangedDriverStatus == DRIVING || ChangedDriverStatus == ON_DUTY){

                JSONArray logArray = constants.AddNewStatusInList("", String.valueOf(ChangedDriverStatus), "", "no_address",
                        String.valueOf(DriverId), "" , "" ,  "",  "",
                        CurrentCycleId,  "",  "false",  isViolation,
                        "false", String.valueOf(OBDVehicleSpeed),
                        String.valueOf(GPSVehicleSpeed), sharedPref.GetCurrentTruckPlateNo(context), connectionSource + LastStatus,false,
                        Global, sharedPref.get16hrHaulExcptn(context), false,
                        ""+sharedPref.getAdverseExcptn(context),
                        "", hMethods,  dbHelper);

                    String CurrentDate = Global.GetCurrentDateTime();
                String currentUtcTimeDiffFormat = Global.GetCurrentUTCTimeFormat();
                int rulesVersion = sharedPref.GetRulesVersion(context);

                List<DriverLog> oDriverLog = hMethods.GetLogAsList(logArray);
                DriverDetail oDriverDetail1 = hMethods.getDriverList(new DateTime(CurrentDate), new DateTime(currentUtcTimeDiffFormat),
                        DriverId,offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver,
                        DRIVER_JOB_STATUS, isOldRecord, sharedPref.get16hrHaulExcptn(context),
                        sharedPref.getAdverseExcptn(context), rulesVersion, oDriverLog);
                RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), ChangedDriverStatus, oDriverDetail1);


            }

            JSONObject sameStatusJson = hMethods.AddSameStatusJson(lastItemJson, currentDateTime, currentUTCTime, RulesObj, false, 0);
            driverLogArray.put(sameStatusJson);

            SaveDriversJob(sameStatusJson, driverLogArray, hMethods, dbHelper, DriverType, String.valueOf(RulesObj.isViolation()),
                    RulesObj.getViolationReason(), ChangedDriverStatus, isAutomatic, false);

            // callback method called to update Eld home screen
            serviceCallback.onServiceResponse(RulesObj, RemainingTimeObj, IsAppForground, true, "", context.getResources().getString(R.string.screen_reset));
        }
    }



    void CycleTimeCalculation(DateTime currentDateTime, DateTime currentUTCTime, boolean isSingleDriver, int DRIVER_JOB_STATUS,
                              int DriverType, JSONArray driverLogArray, HelperMethods hMethods, DBHelper dbHelper,
                                 ServiceCallback serviceCallback){
        int rulesVersion = sharedPref.GetRulesVersion(context);

        oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, DriverId,
                offsetFromUTC, Integer.valueOf(CurrentCycleId), isSingleDriver, DRIVER_JOB_STATUS, isOldRecord,
                sharedPref.get16hrHaulExcptn(context),  sharedPref.getAdverseExcptn(context),
                rulesVersion, oDriverLogDetail);
        RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), DRIVER_JOB_STATUS, oDriverDetail);

        // Calculate 2 days data to get remaining Driving/Onduty hours
        RemainingTimeObj = hMethods.getRemainingTime(currentDateTime, currentUTCTime, offsetFromUTC,
                Integer.valueOf(CurrentCycleId), isSingleDriver, DriverId, DRIVER_JOB_STATUS, isOldRecord,
                sharedPref.get16hrHaulExcptn(context),  sharedPref.getAdverseExcptn(context),
                rulesVersion, dbHelper);




        if (DRIVER_JOB_STATUS == DRIVING || DRIVER_JOB_STATUS == ON_DUTY) {
            if (RulesObj != null) {

                isViolation = RulesObj.isViolation();
                sharedPref.SetViolation(isViolation, context);
                String violationReason = RulesObj.getViolationReason();

                    if (isViolation ) {

                        // save current violation record with 18 days array

                            try {
                                // Write violated file in storage
                                boolean IsWarningNotificationAllowed = constants.IsWarningNotificationAllowed(String.valueOf(DriverId), dbHelper);

                                if(!constants.IsAlreadyViolation && IsWarningNotificationAllowed) {
                                    constants.writeViolationFile(currentDateTime, currentUTCTime, DriverId, CurrentCycleId, offsetFromUTC, isSingleDriver,
                                            DRIVER_JOB_STATUS, isOldRecord, sharedPref.get16hrHaulExcptn(context), violationReason, hMethods, dbHelper, context);
                                }


                                if(!constants.IS_ELD_ON_CREATE) {
                                    JSONObject lastItemJson = hMethods.GetLastJsonFromArray(driverLogArray);
                                    if (lastItemJson != null) {

                                        String oldVoilationReason =  lastItemJson.getString(ConstantsKeys.ViolationReason);

                                           JSONObject sameStatusJson = hMethods.AddSameStatusJson(lastItemJson, currentDateTime, currentUTCTime, RulesObj, false, 0);
                                            driverLogArray.put(sameStatusJson);

                                            if(violationReason.trim().length() > 0 && !violationReason.equals(oldVoilationReason)) {

                                                sharedPref.SetViolationReason(violationReason, context);
                                                sharedPref.SetIsReadViolation(false, context);

                                                DateTime lastLogStartTime = Global.getDateTimeObj(lastItemJson.getString(ConstantsKeys.startDateTime), false);
                                                DateTime duplicateLogStartTime = Global.getDateTimeObj(sameStatusJson.getString(ConstantsKeys.startDateTime), false);
                                                int minDiff = duplicateLogStartTime.getMinuteOfDay() - lastLogStartTime.getMinuteOfDay();

                                                if(minDiff > 30) {
                                                      SaveDriversJob(sameStatusJson, driverLogArray, hMethods, dbHelper, DriverType,
                                                                String.valueOf(isViolation), violationReason, 0, true, false);
                                                }else {
                                                      // update Last status with violation reason
                                                      SaveDriversJob(sameStatusJson, driverLogArray, hMethods, dbHelper, DriverType,
                                                                String.valueOf(isViolation), violationReason, 0, true, true);
                                                }

                                            //    JSONArray notificationArray = notificationMethod.getSavedNotificationArray(Integer.valueOf(DriverId), dbHelper);
                                            //    if(notificationArray.length() == 0 && driver18DaysLogArray.length() < 10 ) {
                                                    // nothing to save
                                             //   }else {

                                                if(IsWarningNotificationAllowed) {
                                                    // Save reason in notification history list
                                                    constants.SaveNotification(DriverType, context.getResources().getString(R.string.violation), violationReason, currentDateTime.toString(), hMethods, notificationPref, coNotificationPref, context);

                                                    // Save Notification in 18 days notification history array
                                                    notificationMethod.saveNotificationHistory(
                                                            DriverId,
                                                            DeviceId,
                                                            DriverName,
                                                            context.getResources().getString(R.string.violation),
                                                            violationReason,
                                                            currentDateTime,
                                                            DriverCompanyId,
                                                            dbHelper);

                                                }

                                                // callback method called tp update Eld home screen
                                                serviceCallback.onServiceResponse(RulesObj, RemainingTimeObj, IsAppForground, true, "", context.getResources().getString(R.string.screen_reset));

                                            }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }else{
                        String message = RulesObj.getMessage();
                        String title = RulesObj.getTitle();

                        if(message.length() > 0 && title.length() > 0){

                            serviceCallback.onServiceResponse(RulesObj, RemainingTimeObj, IsAppForground, false, context.getResources().getString(R.string.als_alert) + " " + title, message);

                            JSONArray notificationArray = notificationMethod.getSavedNotificationArray(Integer.valueOf(DriverId), dbHelper);
                            if(notificationArray.length() == 0 && driver18DaysLogArray.length() > 15 ) {
                                // nothing to save
                            }else {

                                boolean IsWarningNotificationAllowed = constants.IsWarningNotificationAllowed(String.valueOf(DriverId), dbHelper);
                                if (IsWarningNotificationAllowed) {

                                    // Save alert in db
                                    constants.SaveNotification(DriverType, context.getResources().getString(R.string.warning) + ": " + title, message, currentDateTime.toString(), hMethods, notificationPref, coNotificationPref, context);

                                    // Save Notification in 18 days notification history array
                                    notificationMethod.saveNotificationHistory(
                                            DriverId,
                                            DeviceId,
                                            DriverName,
                                            context.getResources().getString(R.string.warning) + ": " + title,
                                            message,
                                            currentDateTime,
                                            DriverCompanyId,
                                            dbHelper);
                                }
                            }
                 /*           List<NotificationHistoryModel> notificationsList = new ArrayList<NotificationHistoryModel>();
                            if (DriverType == 0) {
                                notificationsList = notificationPref.GetNotificationsList(context);
                            }else {
                                notificationsList = coNotificationPref.GetNotificationsList(context);
                            }
                            if(notificationsList.size() > 0){
                                String lastAlertMsg = notificationsList.get(notificationsList.size()-1).getNotificationDetails();
                                if(!lastAlertMsg.equals(message)){


                                }
                           }else{

                            }
*/

                        }

                    }

            }
        }

        serviceCallback.onServiceResponse(RulesObj, RemainingTimeObj, IsAppForground, false, "", "");

    }




    private void getDriverDetails(){

        if(DriverType == 0) {
            DriverName      = DriverConst.GetDriverDetails( DriverConst.DriverName, context);
            DriverCompanyId = DriverConst.GetDriverDetails(DriverConst.CompanyId, context);
            TruckNo         = DriverConst.GetDriverTripDetails(DriverConst.Truck, context);
        }else {
            DriverName      = DriverConst.GetCoDriverDetails( DriverConst.CoDriverName, context);
            DriverCompanyId = DriverConst.GetCoDriverDetails(DriverConst.CoCompanyId, context);
            TruckNo         = DriverConst.GetCoDriverTripDetails(DriverConst.CoTruck, context);
        }
    }


    /*===== Save Driver Jobs with Shared Preference to Array List======= */
    private void SaveDriversJob(JSONObject jobJsonObj, JSONArray driverLogArray, HelperMethods hMethods, DBHelper dbHelper,
                                int DriverType, String isViolationStr, String ViolationReason, int DriverStatusInt,
                                boolean isAutoChanged, boolean isUpdate) {


        MainDriverPref    = new MainDriverEldPref();
        CoDriverPref        = new CoDriverEldPref();

        String City = "", State = "", Country = "", Remarks = "";
        String currentUTCTime = Global.GetCurrentUTCTime();
        String currentUtcTimeDiffFormat = Global.GetCurrentUTCTimeFormat();
        String DriverStatusId = "", isPersonal = "", isYardMove = "", trailorNumber = "", isAutomatic = "";
        boolean isHaulException =  sharedPref.get16hrHaulExcptn(context);

        CurrentCycleId = hMethods.CheckStringNull(CurrentCycleId);
        trailorNumber = sharedPref.getTrailorNumber( context);

        if(!isViolationStr.equalsIgnoreCase("true") ){
            isViolationStr = "false";
        }

        JSONArray DriverJsonArray = GetDriversSavedArray();
        EldDataModelNew locationModel = null;
        try {

            String[] locationArray        = jobJsonObj.getString(ConstantsKeys.StartLocation).split(", ");
            Remarks                       = jobJsonObj.getString(ConstantsKeys.Remarks);

            if(isAutoChanged) { //jobJsonObj.has(ConstantsKeys.IsStatusAutomatic)
                isAutomatic = "true";
            }else{
                isAutomatic = "false";
            }

            int locLength = locationArray.length-1;
            if(locLength > 1) {
                Country = locationArray[locLength];
                State = locationArray[locLength - 1];
            }


            for(int i = 0 ; i < locLength-1 ; i++){
                City = City + " " + locationArray[i];
            }


            if(DriverStatusInt == 0) {
                DriverStatusId = jobJsonObj.getString(ConstantsKeys.DriverStatusId);
            }else{
                DriverStatusId = String.valueOf(DriverStatusInt);

                if(DriverStatusInt != ON_DUTY){
                    Remarks = "";
                }else{
                    if(Remarks.length() == 0){
                        Remarks = "Others";
                    }
                }
            }

            isPersonal      = "false";
            isYardMove      = "false";

            String plateNo = sharedPref.GetCurrentTruckPlateNo(context);

                locationModel = new EldDataModelNew(
                        Global.PROJECT_ID,
                        String.valueOf(DriverId),
                        DriverStatusId,

                        isYardMove,
                        isPersonal,
                        DeviceId,

                        Remarks,
                        currentUTCTime,
                        TruckNo,
                        trailorNumber,
                        DriverCompanyId,
                        DriverName,

                        City,
                        State,
                        Country,
                        isViolationStr,
                        ViolationReason,
                        Global.LATITUDE,
                        Global.LONGITUDE,
                        isAutomatic,
                        String.valueOf(OBDVehicleSpeed),
                        String.valueOf(GPSVehicleSpeed),
                        plateNo,
                        String.valueOf(isHaulException),
                        "false",
                        connectionSource + LastStatus,
                        String.valueOf(sharedPref.getAdverseExcptn(context)),
                        ""



                );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(DriverType == 0){
            MainDriverPref.AddDriverLoc(context, locationModel);

            /* ==== Add data in list to show in offline mode ============ */
            EldDriverLogModel logModel = new EldDriverLogModel(Integer.valueOf(DriverStatusId), "startDateTime", "endDateTime", "totalHours",
                    "currentCycleId", false , currentUtcTimeDiffFormat, currentUtcTimeDiffFormat,
                    "", City + ", " + State + ", " + Country, "", Boolean.parseBoolean(isPersonal),
                    sharedPref.getAdverseExcptn(context), sharedPref.get16hrHaulExcptn(context) );
            eldSharedPref.AddDriverLoc(context, logModel);
        }else{
            CoDriverPref.AddDriverLoc(context, locationModel);

            /* ==== Add data in list to show in offline mode ============ */
            EldDriverLogModel logModel = new EldDriverLogModel(Integer.valueOf(DriverStatusId), "startDateTime", "endDateTime", "totalHours",
                    "currentCycleId", false , currentUtcTimeDiffFormat, currentUtcTimeDiffFormat, "", City + ", " + State + ", " + Country,
                    "", Boolean.parseBoolean(isPersonal),
                    sharedPref.getAdverseExcptn(context), sharedPref.get16hrHaulExcptn(context) );
            coEldSharedPref.AddDriverLoc(context, logModel);
        }


        try {

            /* Put data as JSON to List */
            constants.SaveEldJsonToList( locationModel, DriverJsonArray );

            // Saved json in synced array which is using in setting page to sync data mannually.
            SyncingMethod syncingMethod = new SyncingMethod();
            JSONObject newObj = constants.GetJsonFromList(DriverJsonArray, DriverJsonArray.length() - 1);
            JSONArray savedSyncedArray = syncingMethod.getSavedSyncingArray(DriverId, dbHelper);
            savedSyncedArray.put(newObj);
            syncingMethod.SyncingLogHelper(DriverId, dbHelper, savedSyncedArray);


        } catch (Exception e) {
            e.printStackTrace();
        }




        // ============================ Save Job Status in SQLite 18 days record List ==============================

        long DriverLogId                = 0;
        double LastJobTotalMin          = 0;
        String lastDateTimeStr          = "";
        String StartDeviceCurrentTime   = Global.GetCurrentDateTime();
        String StartUTCCurrentTime      = Global.GetCurrentUTCTimeFormat();
        String startLoc = "", endLoc = "",address = "";
        try{
            driverLogArray  = hMethods.getSavedLogArray(DriverId, dbHelper);
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject lastItemJson = hMethods.GetLastJsonFromArray(driverLogArray);

        if(lastItemJson != null) {
            try {
                DriverLogId     = lastItemJson.getLong(ConstantsKeys.DriverLogId);
                lastDateTimeStr = lastItemJson.getString(ConstantsKeys.startDateTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            DateTime currentDateTime     = Global.getDateTimeObj(StartDeviceCurrentTime, false);
            DateTime lastDateTime        = Global.getDateTimeObj(lastDateTimeStr, false);
            LastJobTotalMin              = currentDateTime.getMinuteOfDay() - lastDateTime.getMinuteOfDay();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject lastItemUpdatedJson = hMethods.UpdateLastJsonFromArray(driverLogArray, StartDeviceCurrentTime, StartUTCCurrentTime,  LastJobTotalMin);

        try {

        startLoc = lastItemUpdatedJson.getString(ConstantsKeys.StartLocation);
        endLoc   = lastItemUpdatedJson.getString(ConstantsKeys.EndLocation);

        address = endLoc.trim();
        if(address.length() == 0){
            address = startLoc;
        }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(City.length() != 0 && State.length() != 0){
            address = City + ", " + State + ", " + Country;
        }


        DriverLogId = DriverLogId+1;
        String plateNo = sharedPref.GetCurrentTruckPlateNo(context);

        JSONObject newJsonData = hMethods.AddJobInArray(
                DriverLogId,
                DriverId,
                Integer.valueOf(DriverStatusId),

                StartDeviceCurrentTime,
                StartDeviceCurrentTime,
                StartUTCCurrentTime,
                StartUTCCurrentTime,

                0,  // because start and end date will be same for new status for that time
                Global.LATITUDE, Global.LONGITUDE, Global.LATITUDE, Global.LONGITUDE,
                Boolean.parseBoolean(isYardMove),
                Boolean.parseBoolean(isPersonal),
                Integer.valueOf(CurrentCycleId),
                isViolation,
                ViolationReason,
                DriverName,
                Remarks,
                trailorNumber,
                address,
                address,
                TruckNo,
                isAutomatic,
                String.valueOf(OBDVehicleSpeed),
                String.valueOf(GPSVehicleSpeed),
                plateNo,
                isHaulException,
                false,
                connectionSource + LastStatus,
                ""+sharedPref.getAdverseExcptn(context),
                ""

        );


        try {
            // Update end date time with current date time of last saved item in Array

            if(driverLogArray != null && driverLogArray.length() > 0)
                driverLogArray.put(driverLogArray.length()-1, lastItemUpdatedJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(isUpdate){

            try {
                JSONObject updateViolationJson = hMethods.AddJobInArray(
                        DriverLogId,
                        DriverId,
                        Integer.valueOf(DriverStatusId),
                        lastItemJson.getString(ConstantsKeys.startDateTime),
                        lastItemJson.getString(ConstantsKeys.endDateTime),
                        lastItemJson.getString(ConstantsKeys.utcStartDateTime),
                        lastItemJson.getString(ConstantsKeys.utcEndDateTime),

                        0,  // because start and end date will be same for new status for that time
                        Global.LATITUDE, Global.LONGITUDE, Global.LATITUDE, Global.LONGITUDE,
                        Boolean.parseBoolean(isYardMove),
                        Boolean.parseBoolean(isPersonal),
                        Integer.valueOf(CurrentCycleId),
                        isViolation,
                        ViolationReason,
                        DriverName,
                        Remarks,
                        trailorNumber,
                        address,
                        address,
                        TruckNo,
                        isAutomatic,
                        String.valueOf(OBDVehicleSpeed),
                        String.valueOf(GPSVehicleSpeed),
                        plateNo,
                        isHaulException,
                        false,
                        connectionSource + LastStatus,
                        ""+sharedPref.getAdverseExcptn(context),
                       ""

                );


                driverLogArray.put(driverLogArray.length()-1, updateViolationJson);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            driverLogArray.put(newJsonData);
        }

        // set current job status
        sharedPref.setDriverStatusId("jobType", DriverStatusId, context);

        /* ---------------- DB Helper operations (Insert/Update) --------------- */
        hMethods.DriverLogHelper(DriverId, dbHelper, driverLogArray);

        //ClearCounter
            BackgroundLocationService.IsAutoChange = false;

        // Save odometer
        saveOdometer(DriverStatusId, hMethods, dbHelper);

        constants.IsAlreadyViolation = false;
    }



    /*===== Get Driver Jobs in Array List======= */
    public JSONArray GetDriversSavedArray(){
        int listSize = 0;
        JSONArray DriverJsonArray = new JSONArray();
        List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

        if(sharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
            try {
                listSize = MainDriverPref.LoadSavedLoc(context).size();
                tempList = MainDriverPref.LoadSavedLoc(context);
            } catch (Exception e) {
                listSize = 0;
            }
        }else{
            try {
                listSize = CoDriverPref.LoadSavedLoc(context).size();
                tempList = CoDriverPref.LoadSavedLoc(context);
            } catch (Exception e) {
                listSize = 0;
            }
        }

        try {
            if (listSize > 0) {
                for (int i = 0; i < tempList.size(); i++) {
                    EldDataModelNew listModel = tempList.get(i);
                    if(listModel != null) {
                        constants.SaveEldJsonToList(          /* Put data as JSON to List */
                                listModel,
                                DriverJsonArray
                        );
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DriverJsonArray;
       // Log.d("Arraay", "Arraay: " + DriverJsonArray.toString());
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                SpeakOutMsg(message);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    // Speak Out Msg
    void SpeakOutMsg(String msg){
        textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);


    }


    public interface ServiceCallback {
        public void onServiceResponse(RulesResponseObject RulesObj, RulesResponseObject RemainingTimeObj, boolean IsForground,
                                      boolean isEldToast, String msg, String status);
    }

    public interface ServiceError {
        public void onServiceError(String error, boolean IsForground);
    }






}
