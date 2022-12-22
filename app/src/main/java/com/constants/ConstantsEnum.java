package com.constants;

public class ConstantsEnum {

    public static final int UpdateObdVeh          =  20;
    public static final int GetObdAssignedVeh     =  30;
    public static final int GetOndutyRemarks      =  40;
    public static final int SendLog               =  70;
    public static final int GetOdometer           =  80;
    public static final int SaveTrailer           =  90;
    public static final int GetDriverLog18Days    = 100;
    public static final int GetCoDriverLog18Days  = 110;
    public static final int GetShipment18Days     = 120;
    public static final int GetShipment18DaysCo   = 130;
    public static final int GetOdometers18Days    = 140;
    public static final int GetDriverPermission   = 150;
    public static final int GetAddFromLatLng      = 160;
    public static final int GetInspection18Days   = 170;
    public static final int GetInspection18DaysCo = 180;
    public static final int GetInspection         = 190;
    public static final int GetNotifications      = 200;
    public static final int GetNewsNotifications  = 210;
    public static final int GetReCertifyRecords   = 220;
    public static final int GetRecapViewFlagMain  = 230;
    public static final int GetRecapViewFlagCo    = 240;
    public static final int NotReady              = 250;
    public static final int OdometerDetailInPu    = 260;
    public static final int SaveAgricultureException = 270;
    public static final int GetStateList          = 271;


    public static String SAVE_END_READING     = "Please save your End odometer Reading after personal use";
    public static String SAVE_READING         = "Please save your odometer reading after personal use";
    public static String SAVE_READING_AFTER   = "Please save your odometer reading";
    public static String SELECT_ONDUTY_REASON = "Please select OnDuty Reason";
    public static String DUPLICATE_JOB_ALERT  = "You can't change same status at the same time. Please change after 1 minute";
    public static String NO_TRAILER_ALERT     = "You can not select (Trailer Drop) reason when there is no trailer attached";
    public static String CO_DRIVING_ALERT     = "Co-Driver is in ";
    public static String CO_DRIVING_ALSO      = "Co-Driver is also in ";
    public static String CO_DRIVING_ALERT1    = ". Please change his status first.";
    public static String AFTER_SWITCH_ALERT   = "Please wait for a while after driver switch";
    public static String PICK_TRAILER_ALERT   = "Trailer Number to pickup trailer";
    public static String PROPER_REASON_ALERT  = "Enter minimum 4 char for personal use reason";
    public static String MAX_CHAR_LIMIT       = "Maximum 60 char limit";
    public static String SELECT_TRAILER_TYPE  = "Select Trailer type first";
    public static String ENTER_TRAILER_NO     = "Enter Trailer number";
    public static String ADVERSE_REASON_ALERT = "Enter minimum 4 char for Adverse Exception reason";
    public static String YARD_MOVE_DESC       = "Enter minimum 4 char for Yard Move description";
    public static String LOCATION_DESC        = "Enter minimum 5 char of Location ";
    public static String SELECT_STATE         = "Please select State.";
    public static String SELECT_DR_STATUS     = "Please select DRIVING records for swapping with Co-Driver.";

    public static String EDIT_REMARKS_DESC    = "Enter minimum 4 char for edit reason";
    public static String REJECT_REASON_ALERT  = "Enter minimum 4 char to reject unidentified records reason";
    public static String COMPANY_REJECT_REASON= "Enter minimum 4 char to reject company assigned unidentified records reason";
    public static String CLAIM_UNIDENTIFIED_REASON= "Enter minimum 4 char to claim unidentified records reason";
    public static String EDIT_LOG_REASON_ALERT    = "Enter minimum 4 char for Edit Log reason";
    public static String PROPER_REASON_MAX_ALERT  = "Maximum char for personal use reason are 160";
    public static String YARD_MOVE_MAX_DESC       = "Maximum char for Yard Move description are 160";
    public static String SELECT_TRAILER_ALERT   = "Please enter Trailer Number";
    public static String CHANGE_TRAILER_ALERT   = "Please enter different Trailer Number to switch the trailer";
    public static String StrTruckAttachedTxt    = "We can't see any truck attached with you. Please contact with your support team";
    public static String No_TRAILER_ALERT       = "You can not select (Trailer Drop) reason when there is no trailer attached";
    public static String No_PICKUP_ALERT        = ". So can not pick another trailer. You can change your trailer with (Trailer Switch) option";  //
    public static String ALREADY_ATTACHED       = "You already have a trailer";
    public static String TRUCK_CHANGE           = "Sorry, you can't change the truck while driving or vehicle is moving";
    public static String TRAILER_CHANGE         = "Sorry, you can't change the trailer while driving or vehicle is moving";
    public static String PTI_SAVE_ONDUTY_ONLY   = "You can save PTI only in OnDuty status.";
    public static String CTPAT_SAVE_ONDUTY_ONLY = "You can save Ct-Pat only in OnDuty status.";
    public static String HOS_NOT_REFRESHED      = "ELD Cycle/Shift time is updated but driver miles and locations can not not updated due to network issue";
    public static String UPDATED                = "HOS view updated";

    public static String CANADA_MINIMUM_OFFDUTY_HOURS_VIOLATION = "CANADA MINIMUM OFF DUTY HOURS VIOLATION;";


    // App usage enum
   // public static String StatusForeground       = "App in foreground";
    public static String StatusAppKilled        = "App Killed but auto restart service";
    public static String StatusServiceStopped   = "Service stopped";
    public static String StatusPuAuto           = "PU_Odometer_auto";
    public static String StatusPuMannual        = "PU_Odometer_mannual";
    public static String PU_Status              = "PersonalUseStatus";
    public static String IsPUOdoDialog          = "IsPUOdometerDialogShown";


}
