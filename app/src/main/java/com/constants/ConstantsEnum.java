package com.constants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.messaging.logistic.TabAct;

public class ConstantsEnum {

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


    public static String testData = "{\n" +
            "    \"Status\": true,\n" +
            "    \"Message\": \"Record get Successfully\",\n" +
            "    \"Data\": [\n" +
            "        {\n" +
            "            \"DriverLogDate\": \"2021-01-13T00:00:00\",\n" +
            "            \"SuggestedEditModel\": [\n" +
            "                {\n" +
            "                    \"DriverId\": 125341,\n" +
            "                    \"DriverStatusId\": 1,\n" +
            "                    \"StartDateTime\": \"2021-01-13T00:00:00\",\n" +
            "                    \"EndDateTime\": \"2021-01-13T10:00:00\",\n" +
            "                    \"UTCStartDateTime\": \"2021-01-13T08:00:00\",\n" +
            "                    \"UTCEndDateTime\": \"2021-01-13T18:00:00\",\n" +
            "                    \"TotalHours\": 600,\n" +
            "                    \"YardMove\": false,\n" +
            "                    \"Personal\": false,\n" +
            "                    \"CurrentCycleId\": 1,\n" +
            "                    \"IsViolation\": \"False\",\n" +
            "                    \"CreatedDate\": \"2021-01-12T13:27:14.573\",\n" +
            "                    \"DriverTimeZone\": \"Pacific Standard Time\",\n" +
            "                    \"DeviceId\": null,\n" +
            "                    \"SuggestedEditsId\": 40\n" +
            "                },\n" +
            "                {\n" +
            "                    \"DriverId\": 125341,\n" +
            "                    \"DriverStatusId\": 3,\n" +
            "                    \"StartDateTime\": \"2021-01-13T10:00:00\",\n" +
            "                    \"EndDateTime\": \"2021-01-13T12:00:00\",\n" +
            "                    \"UTCStartDateTime\": \"2021-01-13T18:00:00\",\n" +
            "                    \"UTCEndDateTime\": \"2021-01-13T20:00:00\",\n" +
            "                    \"TotalHours\": 120,\n" +
            "                    \"YardMove\": false,\n" +
            "                    \"Personal\": false,\n" +
            "                    \"CurrentCycleId\": 0,\n" +
            "                    \"IsViolation\": \"False\",\n" +
            "                    \"CreatedDate\": \"2021-01-12T13:27:47.617\",\n" +
            "                    \"DriverTimeZone\": \"Pacific Standard Time\",\n" +
            "                    \"DeviceId\": null,\n" +
            "                    \"SuggestedEditsId\": 41\n" +
            "                },\n" +
            "                {\n" +
            "                    \"DriverId\": 125341,\n" +
            "                    \"DriverStatusId\": 4,\n" +
            "                    \"StartDateTime\": \"2021-01-13T12:00:00\",\n" +
            "                    \"EndDateTime\": \"2021-01-13T13:00:00\",\n" +
            "                    \"UTCStartDateTime\": \"2021-01-13T20:00:00\",\n" +
            "                    \"UTCEndDateTime\": \"2021-01-13T21:00:00\",\n" +
            "                    \"TotalHours\": 60,\n" +
            "                    \"YardMove\": false,\n" +
            "                    \"Personal\": false,\n" +
            "                    \"CurrentCycleId\": 0,\n" +
            "                    \"IsViolation\": \"False\",\n" +
            "                    \"CreatedDate\": \"2021-01-12T13:27:51.353\",\n" +
            "                    \"DriverTimeZone\": \"Pacific Standard Time\",\n" +
            "                    \"DeviceId\": null,\n" +
            "                    \"SuggestedEditsId\": 42\n" +
            "                },\n" +
            "                {\n" +
            "                    \"DriverId\": 125341,\n" +
            "                    \"DriverStatusId\": 2,\n" +
            "                    \"StartDateTime\": \"2021-01-13T13:00:00\",\n" +
            "                    \"EndDateTime\": \"2021-01-13T14:00:00\",\n" +
            "                    \"UTCStartDateTime\": \"2021-01-13T21:00:00\",\n" +
            "                    \"UTCEndDateTime\": \"2021-01-13T22:00:00\",\n" +
            "                    \"TotalHours\": 60,\n" +
            "                    \"YardMove\": false,\n" +
            "                    \"Personal\": false,\n" +
            "                    \"CurrentCycleId\": 0,\n" +
            "                    \"IsViolation\": \"False\",\n" +
            "                    \"CreatedDate\": \"2021-01-12T13:27:57.997\",\n" +
            "                    \"DriverTimeZone\": \"Pacific Standard Time\",\n" +
            "                    \"DeviceId\": null,\n" +
            "                    \"SuggestedEditsId\": 43\n" +
            "                },\n" +
            "                {\n" +
            "                    \"DriverId\": 125341,\n" +
            "                    \"DriverStatusId\": 1,\n" +
            "                    \"StartDateTime\": \"2021-01-13T14:00:00\",\n" +
            "                    \"EndDateTime\": \"2021-01-13T18:00:00\",\n" +
            "                    \"UTCStartDateTime\": \"2021-01-13T22:00:00\",\n" +
            "                    \"UTCEndDateTime\": \"2021-01-12T02:00:00\",\n" +
            "                    \"TotalHours\": 240,\n" +
            "                    \"YardMove\": false,\n" +
            "                    \"Personal\": false,\n" +
            "                    \"CurrentCycleId\": 0,\n" +
            "                    \"IsViolation\": \"False\",\n" +
            "                    \"CreatedDate\": \"2021-01-12T13:28:02.307\",\n" +
            "                    \"DriverTimeZone\": \"Pacific Standard Time\",\n" +
            "                    \"DeviceId\": null,\n" +
            "                    \"SuggestedEditsId\": 44\n" +
            "                },\n" +
            "                {\n" +
            "                    \"DriverId\": 125341,\n" +
            "                    \"DriverStatusId\": 3,\n" +
            "                    \"StartDateTime\": \"2021-01-13T18:00:00\",\n" +
            "                    \"EndDateTime\": \"2021-01-13T20:00:00\",\n" +
            "                    \"UTCStartDateTime\": \"2021-01-12T02:00:00\",\n" +
            "                    \"UTCEndDateTime\": \"2021-01-12T04:00:00\",\n" +
            "                    \"TotalHours\": 120,\n" +
            "                    \"YardMove\": false,\n" +
            "                    \"Personal\": false,\n" +
            "                    \"CurrentCycleId\": 0,\n" +
            "                    \"IsViolation\": \"False\",\n" +
            "                    \"CreatedDate\": \"2021-01-12T13:28:07.033\",\n" +
            "                    \"DriverTimeZone\": \"Pacific Standard Time\",\n" +
            "                    \"DeviceId\": null,\n" +
            "                    \"SuggestedEditsId\": 45\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"DriverLogDate\": \"2021-01-12T00:00:00\",\n" +
            "            \"SuggestedEditModel\": [\n" +
            "                {\n" +
            "                    \"DriverId\": 125341,\n" +
            "                    \"DriverStatusId\": 2,\n" +
            "                    \"StartDateTime\": \"2021-01-12T00:00:00\",\n" +
            "                    \"EndDateTime\": \"2021-01-12T10:10:00\",\n" +
            "                    \"UTCStartDateTime\": \"2021-01-12T04:00:00\",\n" +
            "                    \"UTCEndDateTime\": \"2021-01-12T04:00:00\",\n" +
            "                    \"TotalHours\": 600,\n" +
            "                    \"YardMove\": false,\n" +
            "                    \"Personal\": false,\n" +
            "                    \"CurrentCycleId\": 0,\n" +
            "                    \"IsViolation\": \"False\",\n" +
            "                    \"CreatedDate\": \"2021-01-12T13:28:11.2\",\n" +
            "                    \"DriverTimeZone\": \"Pacific Standard Time\",\n" +
            "                    \"DeviceId\": null,\n" +
            "                    \"SuggestedEditsId\": 46\n" +
            "                },\n" +
            "  {\n" +
            "                    \"DriverId\": 125341,\n" +
            "                    \"DriverStatusId\": 4,\n" +
            "                    \"StartDateTime\": \"2021-01-12T10:10:00\",\n" +
            "                    \"EndDateTime\": \"2021-01-12T20:00:00\",\n" +
            "                    \"UTCStartDateTime\": \"2021-01-12T04:00:00\",\n" +
            "                    \"UTCEndDateTime\": \"2021-01-12T04:00:00\",\n" +
            "                    \"TotalHours\": 600,\n" +
            "                    \"YardMove\": false,\n" +
            "                    \"Personal\": false,\n" +
            "                    \"CurrentCycleId\": 0,\n" +
            "                    \"IsViolation\": \"False\",\n" +
            "                    \"CreatedDate\": \"2021-01-12T13:28:11.2\",\n" +
            "                    \"DriverTimeZone\": \"Pacific Standard Time\",\n" +
            "                    \"DeviceId\": null,\n" +
            "                    \"SuggestedEditsId\": 46\n" +
            "                },\n" +
            "                {\n" +
            "                    \"DriverId\": 125341,\n" +
            "                    \"DriverStatusId\": 3,\n" +
            "                    \"StartDateTime\": \"2021-01-12T20:00:00\",\n" +
            "                    \"EndDateTime\": \"2021-01-12T23:59:00\",\n" +
            "                    \"UTCStartDateTime\": \"2021-01-12T04:00:00\",\n" +
            "                    \"UTCEndDateTime\": \"2021-01-12T08:00:00\",\n" +
            "                    \"TotalHours\": 240,\n" +
            "                    \"YardMove\": false,\n" +
            "                    \"Personal\": false,\n" +
            "                    \"CurrentCycleId\": 0,\n" +
            "                    \"IsViolation\": \"False\",\n" +
            "                    \"CreatedDate\": \"2021-01-12T13:28:16.937\",\n" +
            "                    \"DriverTimeZone\": \"Pacific Standard Time\",\n" +
            "                    \"DeviceId\": null,\n" +
            "                    \"SuggestedEditsId\": 47\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";

}
