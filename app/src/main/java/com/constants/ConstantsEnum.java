package com.constants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.messaging.logistic.TabAct;

public class ConstantsEnum {

    public static String SAVE_END_READING     = "Please save your End Odometer Reading after personal use";
    public static String SAVE_READING         = "Please save your Odometer Reading after personal use";
    public static String SAVE_READING_AFTER   = "Please save your Odometer Reading";
    public static String DUPLICATE_JOB_ALERT  = "You can't change same status at the same time. Please change after 1 minute";
    public static String NO_TRAILER_ALERT     = "You can not select (Trailer Drop) reason when there is no trailer attached";
    public static String PICK_TRAILER_ALERT   = "Trailer Number to pickup trailer";
    public static String PROPER_REASON_ALERT  = "Enter minimum 4 char for personal use reason";
    public static String ADVERSE_REASON_ALERT = "Enter minimum 4 char for Adverse Exception reason";
    public static String YARD_MOVE_DESC       = "Enter minimum 4 char for Yard Move description";
    public static String EDIT_REMARKS_DESC    = "Enter minimum 4 char for edit reason";
    public static String REJECT_REASON_ALERT  = "Enter minimum 4 char to reject unidentified records reason";
    public static String COMPANY_REJECT_REASON= "Enter minimum 4 char to reject company assigned unidentified records reason";
    public static String CLAIM_UNIDENTIFIED_REASON= "Enter minimum 4 char to claim unidentified records reason";
    public static String EDIT_LOG_REASON_ALERT    = "Enter minimum 4 char for Edit Log reason";
    public static String PROPER_REASON_MAX_ALERT  = "Maximum char for personal use reason are 160";
    public static String YARD_MOVE_MAX_DESC       = "Maximum char for Yard Move description are 160";
    public static String SELECT_TRAILER_ALERT = "Please enter Trailer Number";
    public static String CHANGE_TRAILER_ALERT = "Please enter different Trailer Number to switch the trailer";
    public static String StrTruckAttachedTxt  = "We can't see any truck attached with you. Please contact with your support team";
    public static String No_TRAILER_ALERT     = "You can not select (Trailer Drop) reason when there is no trailer attached";
    public static String No_PICKUP_ALERT      = ". So can not pick another trailer. You can change your trailer with (Trailer Switch) option";  //
    public static String ALREADY_ATTACHED     = "You already have a trailer";
    public static String TRUCK_CHANGE         = "Sorry, you can not change the truck while driving";
    public static String TRAILER_CHANGE       = "Sorry, you can not change the trailer while driving";
    public static String HOS_NOT_REFRESHED    = "ELD Cycle/Shift time is updated but driver miles and locations can not not updated due to network issue";
    public static String UPDATED              = "HOS view updated";

    public static String CANADA_MINIMUM_OFFDUTY_HOURS_VIOLATION = "CANADA MINIMUM OFF DUTY HOURS VIOLATION;";


    // App usage enum
    public static String StatusOnline           = "Online";
    public static String StatusOffline          = "Offline";
    public static String StatusScreenOn         = "ScreenOn";
    public static String StatusScreenOff        = "ScreenOff";
    public static String StatusBackground       = "App in background";
    public static String StatusForeground       = "App in foreground";
    public static String StatusAppKilled        = "App Killed but auto restart service";
    public static String StatusServiceStopped   = "Service stopped";
    public static String StatusPuAuto           = "PU_Odometer_auto";
    public static String StatusPuMannual        = "PU_Odometer_mannual";
    public static String PU_Status              = "PersonalUseStatus";
    public static String IsPUOdoDialog          = "IsPUOdometerDialogShown";



}
