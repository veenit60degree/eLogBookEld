package com.driver.details;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class DriverConst {

    public static String SingleDriver   = "1";
    public static String TeamDriver     = "2";

    public static String StatusSingleDriver  = "main_driver";
    public static String StatusTeamDriver    = "co_driver";

    /*============== Single Driver Details Const =================  */
    public static String DriverName     = "driver_name";
    public static String DriverID       = "driver_id";
    public static String LoadID         = "driver_load_id";
    public static String CompanyName    = "driver_company_name";
    public static String CompanyId      = "driver_company_id";
    public static String Carrier        = "driver_carrier";
    public static String CarrierAddress = "driver_carrier_add";
    public static String HomeTerminal   = "driver_home_terminal";

    /*============== Driver Configured Time Const =================  */
    public static String DrivingSpeed   = "_DrivingSpeed";
    public static String DrivingMinute  = "_DrivingMin";
    public static String OnDutySpeed    = "_OnDutySpeed";
    public static String OnDutyMinute   = "_OnDutyMinute";
    public static String OffDutySpeed   = "_OffDutySpeed";
    public static String OffDutyMinute  = "_OffDutyMinute";



    /*=================== Driver Login Constant =================== */
    public static String UserName       = "driver_username";
    public static String Passsword      = "driver_pass";

    /*=================== Driver Setting Constant =================== */
    public static String CurrentCycle   = "driver_CurrentCycleName";
    public static String CurrentCycleId = "driver_CurrentCycleId";
    public static String CANCycleId     = "driver_CanadaCycleId";
    public static String USACycleId     = "driver_USACycleId";
    public static String CANCycleName   = "driver_CACycleName";
    public static String USACycleName   = "driver_USACycleName";
    public static String DriverTimeZone = "driver_DriverTimeZone";
    public static String OffsetHours    = "driver_OffsetHours";
    public static String TimeZoneID     = "driver_TimeZoneID";

    /*=================== Driver Trip Detail Constant =================== */
    public static String TripId         = "driver_TripId";
    public static String Truck          = "driver_Truck";
    public static String VIN            = "driver_VIN";
    public static String Trailor        = "driver_Trailor";
    public static String TripNumber     = "driver_TripNumber";
    public static String ShipperName    = "driver_ShipperName";
    public static String ShipperCity    = "driver_ShipperCity";
    public static String ShipperState   = "driver_ShipperState";
    public static String ConsigneeName  = "driver_ConsigneeName";
    public static String ConsigneeCity  = "driver_ConsigneeCity";
    public static String ConsigneeState = "driver_ConsigneeState";
    public static String VehicleId      = "driver_VehicleId";
    public static String EquipmentNumber= "driver_EquipmentNumber";
    public static String PlateNumber    = "driver_PlateNumber";
    public static String DeviceMappingId= "driver_DeviceMappingId";


    /*=================== Driver Log Detail Constant =================== */
    public static String DrivingHours           = "driver_DrivingHours";
    public static String OnDutyHours            = "driver_OnDutyHours";
    public static String OffDutyHours           = "driver_OffDutyHours";
    public static String SleeperHours           = "driver_SleeperHours";
    public static String LeftDayDrivingHours    = "driver_LeftDayDrivingHours";
    public static String LeftDayOnDutyHours     = "driver_LeftDayOnDutyHours";
    public static String LeftWeekDrivingHours   = "driver_LeftWeekDrivingHours";
    public static String LeftWeekOnDutyHours    = "driver_LeftWeekOnDutyHours";
    public static String TotalDrivingWeekHours  = "driver_TotalDrivingWeekHours";
    public static String TotalOnDutyWeekHours   = "driver_TotalOnDutyWeekHours";
    public static String StartingPoint          = "driver_StartingPoint";
    public static String EndingPoint            = "driver_EndingPoint";
    public static String TotalDistance          = "driver_TotalDistance";
    public static String Remarks                = "driver_Remarks";
    public static String LogSignImage           = "driver_log_image";



    /*============== Co Driver Details Const =================  */
    public static String CoDriverName     = "co_driver_name";
    public static String CoDriverID       = "co_driver_id";
    public static String CoLoadID         = "co_driver_load_id";
    public static String CoCompanyName    = "co_driver_company_name";
    public static String CoCompanyId      = "co_driver_company_id";
    public static String CoCarrier        = "co_driver_carrier";
    public static String CoCarrierAddress = "co_driver_carrier_add";
    public static String CoHomeTerminal   = "co_driver_home_terminal";

    /*============== Co Driver Configured Time Const =================  */
    public static String CoDrivingSpeed   = "co_DrivingSpeed";
    public static String CoDrivingMinute  = "co_DrivingMin";
    public static String CoOnDutySpeed    = "co_OnDutySpeed";
    public static String CoOnDutyMinute   = "co_OnDutyMinute";
    public static String CoOffDutySpeed   = "co_OffDutySpeed";
    public static String CoOffDutyMinute  = "co_OffDutyMinute";



    /*=================== Co Driver Login Constant =================== */
    public static String CoUserName       = "co_driver_username";
    public static String CoPasssword      = "co_driver_pass";


    /*=================== Co Driver Settings Constant =================== */
    public static String CoCurrentCycle   = "co_driver_CurrentCycleName";
    public static String CoCurrentCycleId = "co_driver_CurrentCycleId";
    public static String CoCANCycleId     = "co_driver_CanadaCycleId";
    public static String CoUSACycleId     = "co_driver_USACycleId";
    public static String CoCANCycleName   = "co_driver_CACycleName";
    public static String CoUSACycleName   = "co_driver_USACycleName";
    public static String CoDriverTimeZone = "co_driver_DriverTimeZone";
    public static String CoOffsetHours    = "co_driver_OffsetHours";
    public static String CoTimeZoneID     = "co_driver_TimeZoneID";


    /*=================== Co Driver Trip Detail Constant =================== */
    public static String CoTripId         = "co_driver_TripId";
    public static String CoTruck          = "co_driver_Truck";
    public static String CoVIN            = "co_driver_VIN";
    public static String CoTrailor        = "co_driver_Trailor";
    public static String CoTripNumber     = "co_driver_TripNumber";
    public static String CoShipperName    = "co_driver_ShipperName";
    public static String CoShipperCity    = "co_driver_ShipperCity";
    public static String CoShipperState   = "co_driver_ShipperState";
    public static String CoConsigneeName  = "co_driver_ConsigneeName";
    public static String CoConsigneeCity  = "co_driver_ConsigneeCity";
    public static String CoConsigneeState = "co_driver_ConsigneeState";
    public static String CoVehicleId      = "co_driver_VehicleId";
    public static String CoEquipmentNumber= "co_driver_EquipmentNumber";
    public static String CoPlateNumber    = "co_driver_PlateNumber";
    public static String CoDeviceMappingId= "co_driver_DeviceMappingId";


    /*=================== Co Driver Log Detail Constant =================== */
    public static String CoDrivingHours           = "co_driver_DrivingHours";
    public static String CoOnDutyHours            = "co_driver_OnDutyHours";
    public static String CoOffDutyHours           = "co_driver_OffDutyHours";
    public static String CoSleeperHours           = "co_driver_SleeperHours";
    public static String CoLeftDayDrivingHours    = "co_driver_LeftDayDrivingHours";
    public static String CoLeftDayOnDutyHours     = "co_driver_LeftDayOnDutyHours";
    public static String CoLeftWeekDrivingHours   = "co_driver_LeftWeekDrivingHours";
    public static String CoLeftWeekOnDutyHours    = "co_driver_LeftWeekOnDutyHours";
    public static String CoTotalDrivingWeekHours  = "co_driver_TotalDrivingWeekHours";
    public static String CoTotalOnDutyWeekHours   = "co_driver_TotalOnDutyWeekHours";
    public static String CoStartingPoint          = "co_driver_StartingPoint";
    public static String CoEndingPoint            = "co_driver_EndingPoint";
    public static String CoTotalDistance          = "co_driver_TotalDistance";
    public static String CoRemarks                = "co_driver_Remarks";
    public static String CoLogSignImage           = "co_driver_log_image";


    /* ============ Save Driver Login Details ============*/
    public static void SetDriverLoginDetails(String Username, String Password, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UserName, Username);
        editor.putString(Passsword, Password);
        editor.commit();
    }
    /* ============ Get Driver Login Details ============*/
    public static String GetDriverLoginDetails(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    /* ============ Save Driver Details ============*/
    public static void SetDriverDetails( String Drivername, String DriverId, String LoadId,
                                        String companyName, String companyId, String carrier,
                                         String carrierAddress, String homeTerminal, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(DriverName, Drivername);
        editor.putString(DriverID, DriverId);
        editor.putString(LoadID, LoadId);
        editor.putString(CompanyName, companyName);
        editor.putString(CompanyId, companyId);
        editor.putString(Carrier, carrier);
        editor.putString(CarrierAddress, carrierAddress);
        editor.putString(HomeTerminal, homeTerminal);

        editor.commit();
    }

    /* ============ Get Driver Details ============*/
    public static String GetDriverDetails(String key, Context context) {
        String details = "";
        if(context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            details = preferences.getString(key, "");
        }
        return details;
    }



    public void UpdateDriverDetails(String Drivername, String DriverId, String LoadId,
                                    String companyName, String companyId, String carrier,
                                    String carrierAddress, String homeTerminal, Context context){

        DriverConst.SetDriverDetails(Drivername, DriverId, LoadId, companyName, companyId, carrier,
                carrierAddress, homeTerminal, context);
    }



    public static void UpdateDriverCarrierName( String carrierName, Context context){

        String Drivername   = GetDriverDetails(DriverName, context);
        String DriverId     = GetDriverDetails(DriverID, context);
        String LoadId       = GetDriverDetails(LoadID, context);
        String company_name = GetDriverDetails(CompanyName, context);
        String companyID    = GetDriverDetails(CompanyId, context);
        String CarrierAdd   = GetDriverDetails(CarrierAddress, context);
        String HomeTermnl   = GetDriverDetails(HomeTerminal, context);

        DriverConst.SetDriverDetails(Drivername, DriverId, LoadId, company_name, companyID, carrierName,
                CarrierAdd, HomeTermnl, context);

    }



    //------------ Set User Configured Time -------------------
    public static void setDriverConfiguredTime(int valueDrivingSpeed, int valueDrivingMinute,
                                              int valueOnDutySpeed, int valueOnDutyMinute,
                                              int valueOffDutySpeed, int valueOffDutyMinute,
                                                Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(DrivingSpeed, valueDrivingSpeed);
        editor.putInt(DrivingMinute, valueDrivingMinute);
        editor.putInt(OnDutySpeed, valueOnDutySpeed);
        editor.putInt(OnDutyMinute, valueOnDutyMinute);
        editor.putInt(OffDutySpeed, valueOffDutySpeed);
        editor.putInt(OffDutyMinute, valueOffDutyMinute);

        editor.commit();
    }

    // ----------- Get User Configured Time -------------------
    public static int getDriverConfiguredTime(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }


    /* ============ Save Driver Settings ============*/
    public static void SetDriverCurrentCycle( String currentCycle, String currentCycleId, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CurrentCycle, currentCycle);
        editor.putString(CurrentCycleId, currentCycleId);
        editor.commit();
    }


    /* ============ Get Driver Login Details ============*/
    public static String GetDriverCurrentCycle(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "-1");
    }

    public static void SetDriverSettings(
            String currentCycle, String currentCycleId,
            String CanCycleId, String USCycleId,
            String CanCycleName, String USCycleName,
            String TimeZone, String offset, String TimeZoneId,
            Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(CurrentCycle, currentCycle);
        editor.putString(CurrentCycleId, currentCycleId);
        editor.putString(CANCycleId, CanCycleId);
        editor.putString(USACycleId, USCycleId);
        editor.putString(CANCycleName, CanCycleName);
        editor.putString(USACycleName, USCycleName);
        editor.putString(DriverTimeZone, TimeZone);
        editor.putString(OffsetHours, offset);
        editor.putString(TimeZoneID, TimeZoneId);

        editor.commit();
    }
    /* ============ Get Driver Settings ============*/
    public static String GetDriverSettings(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    /* ============ Save Driver Trip Details ============*/
    public static void SetDriverTripDetails(
            String TripIds, String Trucks,
            String VINs, String Trailors,
            String TripNumbers, String ShipperNames,
            String ShipperCitys, String ShipperStates,
            String ConsigneeNames, String ConsigneeCitys,
            String ConsigneeStates, String vehicleId,
            String equipmentNumber, String plateNumber,
            String deviceMappingId, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TripId, TripIds);
        editor.putString(Truck, Trucks);
        editor.putString(VIN, VINs);
        editor.putString(Trailor, Trailors);
        editor.putString(TripNumber, TripNumbers);
        editor.putString(ShipperName, ShipperNames);
        editor.putString(ShipperCity, ShipperCitys);
        editor.putString(ShipperState, ShipperStates);
        editor.putString(ConsigneeName, ConsigneeNames);
        editor.putString(ConsigneeCity, ConsigneeCitys);
        editor.putString(ConsigneeState, ConsigneeStates);
        editor.putString(VehicleId, vehicleId);
        editor.putString(EquipmentNumber, equipmentNumber);
        editor.putString(PlateNumber, plateNumber);
        editor.putString(DeviceMappingId, deviceMappingId);

        editor.commit();
    }
    /* ============ Get Driver Trip Details ============*/
    public static String GetDriverTripDetails(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }



    /* ============ Save Driver Log Details ============*/
    public static void SetDriverLogDetails(
            String DrivingHour, String OnDutyHour,
            String OffDutyHour, String SleeperHour,
            String LeftDayDrivingHour, String LeftDayOnDutyHour,
            String LeftWeekDrivingHour, String LeftWeekOnDutyHour,
            String TotalDrivingWeekHour, String TotalOnDutyWeekHour,
            String startingPoint, String endingPoint,
            String totalDistance, String remarks, String logSignImage,

            Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(DrivingHours, DrivingHour);
        editor.putString(OnDutyHours, OnDutyHour);
        editor.putString(OffDutyHours, OffDutyHour);
        editor.putString(SleeperHours, SleeperHour);
        editor.putString(LeftDayDrivingHours, LeftDayDrivingHour);
        editor.putString(LeftDayOnDutyHours, LeftDayOnDutyHour);
        editor.putString(LeftWeekDrivingHours, LeftWeekDrivingHour);
        editor.putString(LeftWeekOnDutyHours, LeftWeekOnDutyHour);
        editor.putString(TotalDrivingWeekHours, TotalDrivingWeekHour);
        editor.putString(TotalOnDutyWeekHours, TotalOnDutyWeekHour);
        editor.putString(StartingPoint, startingPoint);
        editor.putString(EndingPoint, endingPoint);
        editor.putString(TotalDistance, totalDistance);
        editor.putString(Remarks, remarks);
        editor.putString(LogSignImage, logSignImage);

        editor.commit();
    }
    /* ============ Get Driver Log Details ============*/
    public static String GetDriverLogDetails(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }











    /* ============================================= CoDriver Details ============================================*/

    /* ============ Save Co Driver Login Details ============*/
    public static void SetCoDriverLoginDetails(
            String Username, String Password,
            Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(CoUserName, Username);
        editor.putString(CoPasssword, Password);
        editor.commit();
    }
    /* ============ Get Co Driver Login Details ============*/
    public static String GetCoDriverLoginDetails(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    /* ================== CoDriver Details ====================*/
    public static void SetCoDriverDetails( String Drivername, String DriverId, String CoLoadId,
                                           String coCompanyName, String coCompanyId, String coCarrier,
                                           String coCarrierAddress, String coHomeTerminal, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(CoDriverName, Drivername);
        editor.putString(CoDriverID, DriverId);
        editor.putString(CoLoadID, CoLoadId);
        editor.putString(CoCompanyName, coCompanyName);
        editor.putString(CoCompanyId, coCompanyId);
        editor.putString(CoCarrier, coCarrier);
        editor.putString(CoCarrierAddress, coCarrierAddress);
        editor.putString(CoHomeTerminal, coHomeTerminal);

        editor.commit();

    }
    /* ============ Get Co Driver Details ============*/
    public static String GetCoDriverDetails(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }



    public static void UpdateCoDriverCarrierName( String carrierName, Context context){

        String Drivername   = GetCoDriverDetails(CoDriverName, context);
        String DriverId     = GetCoDriverDetails(CoDriverID, context);
        String LoadId       = GetCoDriverDetails(CoLoadID, context);
        String company_name = GetCoDriverDetails(CoCompanyName, context);
        String companyID    = GetCoDriverDetails(CoCompanyId, context);
        String CarrierAdd   = GetCoDriverDetails(CoCarrierAddress, context);
        String HomeTermnl   = GetCoDriverDetails(CoHomeTerminal, context);

        DriverConst.SetCoDriverDetails(Drivername, DriverId, LoadId, company_name, companyID, carrierName,
                CarrierAdd, HomeTermnl, context);

    }



    //------------ Set User Configured Time -------------------
    public static void setCoDriverConfiguredTime( int valueDrivingSpeed, int valueDrivingMinute,
                                                int valueOnDutySpeed, int valueOnDutyMinute,
                                                int valueOffDutySpeed, int valueOffDutyMinute,
                                                Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(CoDrivingSpeed, valueDrivingSpeed);
        editor.putInt(CoDrivingMinute, valueDrivingMinute);
        editor.putInt(CoOnDutySpeed, valueOnDutySpeed);
        editor.putInt(CoOnDutyMinute, valueOnDutyMinute);
        editor.putInt(CoOffDutySpeed, valueOffDutySpeed);
        editor.putInt(CoOffDutyMinute, valueOffDutyMinute);

        editor.commit();
    }

    // ----------- Get User Configured Time -------------------
    public static int getCoDriverConfiguredTime(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }




    /* ============ Save Co Driver Settings ============*/
    public static void SetCoDriverCurrentCycle( String currentCycleName, String currentCycleId, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CoCurrentCycle, currentCycleName);
        editor.putString(CoCurrentCycleId, currentCycleId);
        editor.commit();
    }
    /* ============ Get Driver Login Details ============*/
    public static String GetCoDriverCurrentCycle(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "1");     //default value is canada Cycle 1
    }


    public static void SetCoDriverSettings(
            String coCurrentCycle, String coCurrentCycleId,
            String CanCycleId, String USCycleId,
            String CanCycleName, String USCycleName,
            String TimeZone, String offset, String  TimeZoneId,
            Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(CoCurrentCycle, coCurrentCycle);
        editor.putString(CoCurrentCycleId, coCurrentCycleId);
        editor.putString(CoCANCycleId, CanCycleId);
        editor.putString(CoUSACycleId, USCycleId);
        editor.putString(CoCANCycleName, CanCycleName);
        editor.putString(CoUSACycleName, USCycleName);
        editor.putString(CoDriverTimeZone, TimeZone);
        editor.putString(CoOffsetHours, offset);
        editor.putString(CoTimeZoneID, TimeZoneId);

        editor.commit();
    }
    /* ============ Get Co Driver Settings ============*/
    public static String GetCoDriverSettings(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    /* ============ Save Driver Settings ============*/
    public static void SetCoDriverTripDetails(
            String TripIds, String Trucks,
            String VINs, String Trailors,
            String TripNumbers, String ShipperNames,
            String ShipperCitys, String ShipperStates,
            String ConsigneeNames, String ConsigneeCitys,
            String ConsigneeStates, String coVehicleId,
            String coEquipmentNumber, String coPlateNumber,
            String coDeviceMappingId, Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(CoTripId, TripIds);
        editor.putString(CoTruck, Trucks);
        editor.putString(CoVIN, VINs);
        editor.putString(CoTrailor, Trailors);
        editor.putString(CoTripNumber, TripNumbers);
        editor.putString(CoShipperName, ShipperNames);
        editor.putString(CoShipperCity, ShipperCitys);
        editor.putString(CoShipperState, ShipperStates);
        editor.putString(CoConsigneeName, ConsigneeNames);
        editor.putString(CoConsigneeCity, ConsigneeCitys);
        editor.putString(CoConsigneeState, ConsigneeStates);
        editor.putString(CoVehicleId, coVehicleId);
        editor.putString(CoEquipmentNumber, coEquipmentNumber);
        editor.putString(CoPlateNumber, coPlateNumber);
        editor.putString(CoDeviceMappingId, coDeviceMappingId);

        editor.commit();
    }
    /* ============ Get Driver Settings ============*/
    public static String GetCoDriverTripDetails(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }



    /* ============ Save Driver Log Details ============*/
    public static void SetCoDriverLogDetails(
            String DrivingHour, String OnDutyHour,
            String OffDutyHour, String SleeperHour,
            String LeftDayDrivingHour, String LeftDayOnDutyHour,
            String LeftWeekDrivingHour, String LeftWeekOnDutyHour,
            String TotalDrivingWeekHour, String TotalOnDutyWeekHour,

            String coStartingPoint, String coEndingPoint,
            String coTotalDistance, String coRemarks, String coLogSignImage,
            Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(CoDrivingHours, DrivingHour);
        editor.putString(CoOnDutyHours, OnDutyHour);
        editor.putString(CoOffDutyHours, OffDutyHour);
        editor.putString(CoSleeperHours, SleeperHour);
        editor.putString(CoLeftDayDrivingHours, LeftDayDrivingHour);
        editor.putString(CoLeftDayOnDutyHours, LeftDayOnDutyHour);
        editor.putString(CoLeftWeekDrivingHours, LeftWeekDrivingHour);
        editor.putString(CoLeftWeekOnDutyHours, LeftWeekOnDutyHour);
        editor.putString(CoTotalDrivingWeekHours, TotalDrivingWeekHour);
        editor.putString(CoTotalOnDutyWeekHours, TotalOnDutyWeekHour);
        editor.putString(CoStartingPoint, coStartingPoint);
        editor.putString(CoEndingPoint, coEndingPoint);
        editor.putString(CoTotalDistance, coTotalDistance);
        editor.putString(CoRemarks, coRemarks);
        editor.putString(CoLogSignImage, coLogSignImage);

        editor.commit();
    }
    /* ============ Get Driver Log Details ============*/
    public static String GetCoDriverLogDetails(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }





}
