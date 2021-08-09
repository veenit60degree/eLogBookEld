package com.driver.details;

import android.content.Context;
import android.util.Log;

import com.constants.SharedPref;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.SupportMethod;
import com.messaging.logistic.Globally;
import com.models.CycleModel;
import com.models.DriverLocationModel;
import com.models.EldDriverLogModel;
import com.models.TimeZoneModel;
import com.shared.pref.CaCyclePrefManager;
import com.shared.pref.CoCAPref;
import com.shared.pref.CoTimeZonePref;
import com.shared.pref.CoUSPref;
import com.shared.pref.EldCoDriverLogPref;
import com.shared.pref.EldSingleDriverLogPref;
import com.shared.pref.StatePrefManager;
import com.shared.pref.TimeZonePrefManager;
import com.shared.pref.USCyclePrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ParseLoginDetails {

    StatePrefManager statePrefManager ;
    TimeZonePrefManager timeZonePrefManager;
    CoTimeZonePref coTimePrefManager;
    EldSingleDriverLogPref eldSharedPref;
    EldCoDriverLogPref coEldSharedPref;
    CaCyclePrefManager caPrefManager;
    USCyclePrefManager usPrefmanager;
    CoCAPref coCAPrefManager;
    CoUSPref coUSPrefmanager;


    public ParseLoginDetails() {
        super();
    }


    public void ParseLoginDetails(JSONArray resultJson, Context context) throws Exception {

        ClearList(context);
        DBHelper dbHelper = new DBHelper(context);

        try {
            for(int resultCount = 0 ; resultCount < resultJson.length() ; resultCount ++){
                int DriverId = 0;
                JSONObject dataObj = (JSONObject)resultJson.get(resultCount);

                if(!dataObj.isNull("DriverDetail")) {
                    JSONObject DriverDetail     = new JSONObject(dataObj.getString("DriverDetail"));
                    ParseDriverDetail(DriverDetail, resultCount, context);

                    DriverId = DriverDetail.getInt(ConstantsKeys.DriverId);

                    // Save current UTC date time
                    SharedPref.setCurrentUTCTime( DriverDetail.getString("CurrentUTCDateTime") , context );

                }

                if(!dataObj.isNull("DriverSetting")) {
                    JSONObject DriverSetting    = new JSONObject(dataObj.getString("DriverSetting"));
                    ParseDriverSetting(DriverSetting, resultCount, context);

                   if(!DriverSetting.isNull("CanadaCycles")) {
                        JSONArray caJsonArray = new JSONArray(DriverSetting.getString("CanadaCycles"));
                        ParseCycleArray(caJsonArray, "ca", resultCount, context);
                    }

                    if(!DriverSetting.isNull("USACycles")) {
                        JSONArray usJsonArray = new JSONArray(DriverSetting.getString("USACycles"));
                        ParseCycleArray(usJsonArray, "us", resultCount, context);
                    }

                    if(!DriverSetting.isNull("LstTimeZone")) {
                        JSONArray timeZoneJsonArray = new JSONArray(DriverSetting.getString("LstTimeZone"));
                        ParseTimeZoneArray(timeZoneJsonArray, resultCount, context);
                    }

                    if(resultCount == 0) {
                        int stateListSize = 0;
                        if (!DriverSetting.isNull("States")) {
                            JSONArray stateJsonArray = new JSONArray(DriverSetting.getString("States"));

                            if(statePrefManager == null) {
                                statePrefManager = new StatePrefManager();

                                try {
                                    stateListSize       = statePrefManager.GetState(context).size();
                                    if(stateListSize > 0){
                                       // statePrefManager.RemoveState(context);
                                       // stateListSize = 0;
                                    }
                                }catch (Exception e){
                                    stateListSize = 0;
                                }
                            }

                         //   Log.d("stateListSize", "state ListSize0: " + stateListSize);

                            if(stateListSize == 0) {
                                ParseStateArray(stateJsonArray, resultCount, context);
                            }
                        }

                        if(!dataObj.isNull("SupportDetail")) {
                            SupportMethod supportMethod = new SupportMethod();
                            JSONArray supportArray = new JSONArray(dataObj.getString("SupportDetail"));
                            supportMethod.SupportHelper(dbHelper, supportArray);
                        }

                    }

                    try{


                        if(resultCount == 0) {

                            if(DriverSetting.has(ConstantsKeys.IsAllowLogReCertification)) {
                                SharedPref.SetCertifcnUnIdenfdSettings(
                                        DriverSetting.getBoolean(ConstantsKeys.IsAllowLogReCertification),
                                        DriverSetting.getBoolean(ConstantsKeys.IsShowUnidentifiedRecords),
                                        DriverSetting.getBoolean(ConstantsKeys.IsPersonal),
                                        DriverSetting.getBoolean(ConstantsKeys.IsYardMove), context);
                            }else{
                                SharedPref.SetCertifcnUnIdenfdSettings(
                                        false,
                                        false,
                                        false,
                                        false, context);
                            }

                            if (DriverSetting.has(ConstantsKeys.IsAllowMalfunction)) {
                                SharedPref.SetDiagnosticAndMalfunctionSettingsMain(
                                        DriverSetting.getBoolean(ConstantsKeys.IsAllowMalfunction),
                                        DriverSetting.getBoolean(ConstantsKeys.IsAllowDiagnostic),
                                        DriverSetting.getBoolean(ConstantsKeys.IsClearMalfunction),
                                        DriverSetting.getBoolean(ConstantsKeys.IsClearDiagnostic),
                                         context);
                            } else {
                                SharedPref.SetDiagnosticAndMalfunctionSettingsMain(
                                        false,
                                        false,
                                        false,
                                        false,
                                         context);
                            }
                        }else{

                            if(DriverSetting.has(ConstantsKeys.IsAllowLogReCertification)) {
                                SharedPref.SetCertifcnUnIdenfdSettingsCo(
                                        DriverSetting.getBoolean(ConstantsKeys.IsAllowLogReCertification),
                                        DriverSetting.getBoolean(ConstantsKeys.IsShowUnidentifiedRecords),
                                        DriverSetting.getBoolean(ConstantsKeys.IsPersonal),
                                        DriverSetting.getBoolean(ConstantsKeys.IsYardMove), context);
                            }else{
                                SharedPref.SetCertifcnUnIdenfdSettingsCo(
                                        false,
                                        false,
                                        false,
                                        false, context);
                            }


                            if (DriverSetting.has(ConstantsKeys.IsAllowMalfunction)) {
                                SharedPref.SetDiagnosticAndMalfunctionSettingsCo(
                                        DriverSetting.getBoolean(ConstantsKeys.IsAllowMalfunction),
                                        DriverSetting.getBoolean(ConstantsKeys.IsAllowDiagnostic),
                                        DriverSetting.getBoolean(ConstantsKeys.IsClearMalfunction),
                                        DriverSetting.getBoolean(ConstantsKeys.IsClearDiagnostic),
                                         context);
                            } else {
                                SharedPref.SetDiagnosticAndMalfunctionSettingsCo(
                                        false,
                                        false,
                                        false,
                                        false,
                                         context);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if(!dataObj.isNull("DriverTripDetail")) {
                    JSONObject DriverTripDetail = new JSONObject(dataObj.getString("DriverTripDetail"));
                    ParseDriverTripDetail(DriverTripDetail, resultCount, context);
                }

                if(!dataObj.isNull("DriverLogDetail")) {
                    JSONObject DriverLogDetail = new JSONObject(dataObj.getString("DriverLogDetail"));
                    ParseDriverLogDetail(DriverLogDetail, resultCount, context);
                    SharedPref.SetIsAOBRD(DriverLogDetail.getBoolean("IsAOBRD"), context);

                  /*  if(!DriverLogDetail.isNull("DriverLog")) {
                        JSONArray driverLogJsonArray = new JSONArray(DriverLogDetail.getString("DriverLog"));
                        ParseDriverLogArray(driverLogJsonArray, resultCount, context);
                    }*/
                }



                if(dataObj.has("DriverPermission")) {
                    if (!dataObj.isNull("DriverPermission")) {
                        JSONObject DriverLogDetail = new JSONObject(dataObj.getString("DriverPermission"));
                        DriverPermissionMethod driverPermissionMethod = new DriverPermissionMethod();
                        driverPermissionMethod.DriverPermissionHelper(DriverId, dbHelper, DriverLogDetail);

                    }
                }



            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void ParseDriverDetail(JSONObject DriverDetail, int position, Context context){
        try{
            String Drivername = "", DriverId = "", LoadId = "", CompanyId = "", CompanyName = "",
                    HomeTerminal = "", Carrier = "", CarrierAddress = "";

            SharedPref.SetSystemToken(DriverDetail.getString("DeviceId"), context);

            Drivername      = DriverDetail.getString("DriverName");
            DriverId        = DriverDetail.getString(ConstantsKeys.DriverId);
            CompanyId       = DriverDetail.getString("CompanyId");

            HomeTerminal    = getStringData(DriverDetail, CompanyName, "HomeTerminal");
            CompanyName     = getStringData(DriverDetail, CompanyName, "CompanyName");
            Carrier         = getStringData(DriverDetail, Carrier, "Carrier");
            CarrierAddress  = getStringData(DriverDetail, CarrierAddress, "CarrierAddress");
            LoadId          = getStringData(DriverDetail, LoadId, "LoadId");

            int valueDrivingSpeed = getIntData(DriverDetail, "DrivingSpeed");
            int valueDrivingMinute = getIntData(DriverDetail, "DrivingMinute");
            int valueOnDutySpeed = getIntData(DriverDetail, "OnDutySpeed");
            int valueOnDutyMinute = getIntData(DriverDetail, "OnDutyMinute");
            int valueOffDutySpeed = getIntData(DriverDetail, "OffDutySpeed");
            int valueOffDutyMinute = getIntData(DriverDetail, "OffDutyMinute");


            if(position == 0) {
                SharedPref.setLoadId( LoadId , context);                      /*Set Main Load ID */
                SharedPref.setDriverId( DriverId , context);                  /*Set Driver ID*/
               // Globally.setUserName( Drivername, context);                 /*Set Driver name*/
                SharedPref.setCurrentDriverType(DriverConst.StatusSingleDriver, context);      /*Set Driver name*/
                DriverConst.SetDriverDetails(Drivername, DriverId, LoadId, CompanyName, CompanyId, Carrier,
                        CarrierAddress, HomeTerminal, context);

                //------------ Set User Configured Time ------------------
                DriverConst.setDriverConfiguredTime(valueDrivingSpeed, valueDrivingMinute, valueOnDutySpeed, valueOnDutyMinute,
                                                        valueOffDutySpeed, valueOffDutyMinute,  context) ;
            }else {
                Globally.SECOND_DRIVER_NAME = Drivername;
                DriverConst.SetCoDriverDetails(Drivername, DriverId, LoadId, CompanyName, CompanyId, Carrier,
                        CarrierAddress,  HomeTerminal, context);

                //------------ Set Co User Configured Time ------------------
                DriverConst.setCoDriverConfiguredTime(valueDrivingSpeed, valueDrivingMinute, valueOnDutySpeed, valueOnDutyMinute,
                        valueOffDutySpeed, valueOffDutyMinute,  context) ;

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void ParseDriverSetting(JSONObject DriverSetting, int position, Context context) {
        try{
            String CanCycleId="", USCycleId="",CanCycleName="", USCycleName="", TimeZone="", OffSet,
                    TimeZoneId="", CurrentCycle = "", CurrentCycleId = "";
            CurrentCycle    = DriverSetting.getString("CurrentCycleName");
            CurrentCycleId  = DriverSetting.getString("CurrentCycleId");
            CanCycleId      = DriverSetting.getString("CanadaCycleId");
            USCycleId       = DriverSetting.getString("USACycleId");
            CanCycleName    = DriverSetting.getString("CACycleName");
            USCycleName     = DriverSetting.getString("USACycleName");
            TimeZone        = DriverSetting.getString("DriverTimeZone");
            OffSet          = DriverSetting.getString("OffsetHours");
            TimeZoneId      = DriverSetting.getString("TimeZoneID");

            if(position == 0) {
                SharedPref.setUserCountryCycle(
                        "ca_cycle", DriverSetting.getString("CanadaCycleId"),
                        "us_cycle", DriverSetting.getString("USACycleId"), context);                     /* Set Cycles */
                SharedPref.setTimeZone( DriverSetting.getString("DriverTimeZone") , context);             /* Set TimeZone */

                DriverConst.SetDriverSettings(CurrentCycle, CurrentCycleId, CanCycleId, USCycleId,
                        CanCycleName, USCycleName, TimeZone, OffSet, TimeZoneId, context);

                DriverConst.SetDriverCurrentCycle(CurrentCycle, CurrentCycleId, context);
            }else
                DriverConst.SetCoDriverSettings(CurrentCycle, CurrentCycleId, CanCycleId, USCycleId,
                        CanCycleName, USCycleName, TimeZone,  OffSet, TimeZoneId, context);
            DriverConst.SetCoDriverCurrentCycle(CurrentCycle, CurrentCycleId, context);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void ParseDriverTripDetail(JSONObject DriverTripDetail, int position, Context context){
        try{
            String TripIds="", Trucks="", VINs="", Trailors="", TripNumbers="", ShipperNames="", ShipperCitys="",
                    ShipperStates="", ConsigneeNames="", ConsigneeCitys="", ConsigneeStates="", VehicleId = "",
                    EquipmentNumber = "", PlateNumber = "", DeviceMappingId = "";

            if(!DriverTripDetail.isNull("TripId"))
                TripIds         = DriverTripDetail.getString("TripId");
            if(!DriverTripDetail.isNull("Truck"))
                Trucks          = DriverTripDetail.getString("Truck");
            if(!DriverTripDetail.isNull("VIN"))
                VINs            = DriverTripDetail.getString("VIN");
            if(!DriverTripDetail.isNull("Trailor"))
                Trailors        = DriverTripDetail.getString("Trailor");
            if(!DriverTripDetail.isNull("TripNumber"))
                TripNumbers     = DriverTripDetail.getString("TripNumber");
            if(!DriverTripDetail.isNull("ShipperName"))
                ShipperNames    = DriverTripDetail.getString("ShipperName");
            if(!DriverTripDetail.isNull("ShipperCity"))
                ShipperCitys    = DriverTripDetail.getString("ShipperCity");
            if(!DriverTripDetail.isNull("ShipperState"))
                ShipperStates   = DriverTripDetail.getString("ShipperState");
            if(!DriverTripDetail.isNull("ConsigneeName"))
                ConsigneeNames  = DriverTripDetail.getString("ConsigneeName");
            if(!DriverTripDetail.isNull("ConsigneeCity"))
                ConsigneeCitys  = DriverTripDetail.getString("ConsigneeCity");
            if(!DriverTripDetail.isNull("ConsigneeState"))
                ConsigneeStates = DriverTripDetail.getString("ConsigneeState");
            if(!DriverTripDetail.isNull("VehicleId"))
                VehicleId       = DriverTripDetail.getString("VehicleId");
            if(!DriverTripDetail.isNull("EquipmentNumber"))
                EquipmentNumber = DriverTripDetail.getString("EquipmentNumber");
            if(!DriverTripDetail.isNull("PlateNumber"))
                PlateNumber     = DriverTripDetail.getString("PlateNumber");
            if(!DriverTripDetail.isNull("DeviceMappingId"))
                DeviceMappingId = DriverTripDetail.getString("DeviceMappingId");


            if(position == 0) {
                SharedPref.setVINNumber( VINs, context);
                SharedPref.setTrailorNumber(Trailors, context);
                Globally.TRUCK_NUMBER = Trucks;
                Globally.TRAILOR_NUMBER = Trailors;
                Globally.CONSIGNEE_NAME = ConsigneeNames;
                Globally.SHIPPER_NAME = ShipperNames;
                Globally.TRIP_NUMBER = TripNumbers;
                DriverConst.SetDriverTripDetails(TripIds, Trucks, VINs, Trailors, TripNumbers, ShipperNames, ShipperCitys,
                        ShipperStates, ConsigneeNames, ConsigneeCitys, ConsigneeStates, VehicleId, EquipmentNumber,
                        PlateNumber, DeviceMappingId, context);
            } else {
                DriverConst.SetCoDriverTripDetails(TripIds, Trucks, VINs, Trailors, TripNumbers, ShipperNames, ShipperCitys,
                        ShipperStates, ConsigneeNames, ConsigneeCitys, ConsigneeStates, VehicleId, EquipmentNumber,
                        PlateNumber, DeviceMappingId, context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void ParseDriverLogDetail(JSONObject DriverLogDetail, int position, Context context) {
        try {
            String DrivingHour="", OnDutyHour="", OffDutyHour="", SleeperHour="", LeftDayDrivingHour="",
            LeftDayOnDutyHour="",LeftWeekDrivingHour="", LeftWeekOnDutyHour="", TotalDrivingWeekHour="",
                    TotalOnDutyWeekHour="", StartingPoint="No location found.", EndingPoint="No location found",
                    TotalDistance="0", Remarks="--", LogSignImage = "";

            DrivingHour             = DriverLogDetail.getString("TotalDrivingHours");
            OnDutyHour              = DriverLogDetail.getString("TotalOnDutyHours");
            OffDutyHour             = DriverLogDetail.getString("TotalOffDutyHours");
            SleeperHour             = DriverLogDetail.getString("TotalSleeperBerthHours");
            LeftDayDrivingHour      = DriverLogDetail.getString("LeftDayDrivingHours");
            LeftDayOnDutyHour       = DriverLogDetail.getString("LeftDayOnDutyHours");
            LeftWeekDrivingHour     = DriverLogDetail.getString("LeftWeekDrivingHours");
            LeftWeekOnDutyHour      = DriverLogDetail.getString("LeftWeekOnDutyHours");
            TotalDrivingWeekHour    = DriverLogDetail.getString("TotalDrivingWeekHours");
            TotalOnDutyWeekHour     = DriverLogDetail.getString("TotalOnDutyWeekHours");
            LogSignImage            = DriverLogDetail.getString("LogSignImage");


            try {
                TotalDistance           = DriverLogDetail.getString("TotalDistance");
            }catch (Exception e){}


            if(!DriverLogDetail.isNull("StartingPoint"))
                StartingPoint           = DriverLogDetail.getString("StartingPoint");

            if(!DriverLogDetail.isNull("EndingPoint"))
                EndingPoint           = DriverLogDetail.getString("EndingPoint");

            if(!DriverLogDetail.isNull("DriverDetail"))
                Remarks           = DriverLogDetail.getString("Remarks");

            if(position == 0)
                DriverConst.SetDriverLogDetails(DrivingHour, OnDutyHour, OffDutyHour, SleeperHour, LeftDayDrivingHour,
                        LeftDayOnDutyHour,LeftWeekDrivingHour,LeftWeekOnDutyHour, TotalDrivingWeekHour, TotalOnDutyWeekHour,
                        StartingPoint, EndingPoint, TotalDistance, Remarks, LogSignImage, context);
            else
                DriverConst.SetCoDriverLogDetails(DrivingHour, OnDutyHour, OffDutyHour, SleeperHour, LeftDayDrivingHour,
                        LeftDayOnDutyHour,LeftWeekDrivingHour,LeftWeekOnDutyHour, TotalDrivingWeekHour, TotalOnDutyWeekHour,
                        StartingPoint, EndingPoint, TotalDistance, Remarks, LogSignImage, context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ParseCycleArray(JSONArray cycleArray, String cycleType, int position, Context context) {

        if(caPrefManager == null || usPrefmanager == null) {
            caPrefManager = new CaCyclePrefManager();
            usPrefmanager        = new USCyclePrefManager();
        }

        if(coCAPrefManager == null || coUSPrefmanager == null){
            coCAPrefManager                = new CoCAPref();
            coUSPrefmanager                = new CoUSPref();
        }


        try {
            for (int cycleCount = 0; cycleCount < cycleArray.length(); cycleCount++) {
                JSONObject cycleObj = (JSONObject) cycleArray.get(cycleCount);
                String cycleId = cycleObj.getString("ELDCyclesId");
                String cycleName = cycleObj.getString("CycleName");
                CycleModel cycleModel = new CycleModel(cycleId, cycleName);

                if (position == 0) {
                    if (cycleType.equals("us"))
                        usPrefmanager.AddCycle(context, cycleModel);
                    else
                        caPrefManager.AddCycle(context, cycleModel);
                } else {
                    if (cycleType.equals("us"))
                        coUSPrefmanager.AddCycle(context, cycleModel);
                    else
                        coCAPrefManager.AddCycle(context, cycleModel);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ParseTimeZoneArray(JSONArray timeZoneArray, int position, Context context) {

        if(timeZonePrefManager == null || coTimePrefManager == null) {
            timeZonePrefManager = new TimeZonePrefManager();
            coTimePrefManager            = new CoTimeZonePref();
        }

        try{
            for(int cycleCount = 0 ; cycleCount < timeZoneArray.length() ; cycleCount ++){
                JSONObject timeZomeObj = (JSONObject)timeZoneArray.get(cycleCount);
                String TimeZoneID       = timeZomeObj.getString("TimeZoneID");
                String TimeZone         = timeZomeObj.getString("TimeZone");
                String UTC              = timeZomeObj.getString("UTC");
                String TimeZoneName     = timeZomeObj.getString("TimeZoneName");
                String TimeZoneCity     = timeZomeObj.getString("TimeZoneCity");

                TimeZoneModel timeZoneModel = new TimeZoneModel(TimeZoneID, TimeZone, UTC, TimeZoneName, TimeZoneCity);

                if(position == 0)
                    timeZonePrefManager.AddTimeZone(context, timeZoneModel);
                else
                    coTimePrefManager.AddTimeZone(context, timeZoneModel);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void ParseStateArray(JSONArray stateArray, int position, Context context) {

        if(statePrefManager == null) {
            statePrefManager = new StatePrefManager();
        }
      //  CoStatePrefManager coStatePrefManager     = new CoStatePrefManager();

        try{
            for(int cycleCount = 0 ; cycleCount < stateArray.length() ; cycleCount ++){
                JSONObject timeZomeObj = (JSONObject)stateArray.get(cycleCount);
                String StateCode       = timeZomeObj.getString("StateCode");
                String StateName       = timeZomeObj.getString("StateName");
                String Country         = timeZomeObj.getString("Country");

                DriverLocationModel stateModel = new DriverLocationModel(StateCode, StateName, Country);
                statePrefManager.AddState(context, stateModel);

             /*   if(position == 0)
                    statePrefManager.AddState(context, stateModel);
                else
                    coStatePrefManager.AddCoState(context, stateModel);*/
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       // Log.d("ListSize", "state ListSize: " + statePrefManager.GetState(context).size());
    }



    public void ParseDriverLogArray(JSONArray driverLogArray, int position, Context context){

        if(eldSharedPref == null || coEldSharedPref == null){
            eldSharedPref = new EldSingleDriverLogPref();
            coEldSharedPref = new EldCoDriverLogPref();
        }
        try{
            for(int logCount = 0 ; logCount < driverLogArray.length() ; logCount ++){
                JSONObject logObj       = (JSONObject)driverLogArray.get(logCount);
                int driverStatusId      = logObj.getInt("DriverStatusId");
                String startDateTime    = logObj.getString("StartDateTime");
                String endDateTime      = logObj.getString("EndDateTime");
                String totalHours       = logObj.getString("TotalHours");
                String currentCycleId   = logObj.getString("CurrentCycleId");
                String UTCStartDateTime = logObj.getString("UTCStartDateTime");
                String UTCEndDateTime   = logObj.getString("UTCEndDateTime");
                boolean isViolation     = logObj.getBoolean("IsViolation");

                String Duration         = logObj.getString("Duration");
                String Location         = logObj.getString("Location");
                String remarks          = logObj.getString("Remarks");

                boolean isPersonal      = false;
                boolean IsShortHaulException = false;
                boolean IsAdverseException = false;

                if(!logObj.isNull("Personal"))
                      isPersonal      = logObj.getBoolean("Personal");


                if(!logObj.isNull(ConstantsKeys.IsAdverseException))
                    IsAdverseException = logObj.getBoolean(ConstantsKeys.IsAdverseException);

                if(!logObj.isNull(ConstantsKeys.IsShortHaulException))
                    IsShortHaulException = logObj.getBoolean(ConstantsKeys.IsShortHaulException);



                EldDriverLogModel driverLogModel = new EldDriverLogModel(driverStatusId, startDateTime, endDateTime, totalHours, currentCycleId,
                        isViolation, UTCStartDateTime, UTCEndDateTime, Duration , Location , "", remarks, isPersonal,
                        IsAdverseException, IsShortHaulException, logObj.getString("StartLatitude"), logObj.getString("StartLongitude"));

                if(position == 0)
                    eldSharedPref.AddDriverLoc(context, driverLogModel);
                else
                    coEldSharedPref.AddDriverLoc(context, driverLogModel);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void ClearList(Context context) throws Exception{

        if(caPrefManager == null || usPrefmanager == null || timeZonePrefManager == null) {
            caPrefManager = new CaCyclePrefManager();
            usPrefmanager = new USCyclePrefManager();
            timeZonePrefManager = new TimeZonePrefManager();
        }
        caPrefManager.RemoveCycleFromList(context);
        usPrefmanager.RemoveCycleFromList(context);
        timeZonePrefManager.RemoveTimeZoneFromList(context);

        if(coCAPrefManager == null || coUSPrefmanager == null || coTimePrefManager == null) {
            coCAPrefManager = new CoCAPref();
            coUSPrefmanager = new CoUSPref();
            coTimePrefManager = new CoTimeZonePref();
        }
        coCAPrefManager.RemoveCycleFromList(context);
        coUSPrefmanager.RemoveCycleFromList(context);
        coTimePrefManager.RemoveTimeZoneFromList(context);


        if(eldSharedPref == null || coEldSharedPref == null) {
            eldSharedPref = new EldSingleDriverLogPref();
            coEldSharedPref = new EldCoDriverLogPref();

        }
        eldSharedPref.ClearLogFromList(context);
        coEldSharedPref.ClearCoLogFromList(context);

    }


    public static void setUserDefault(String DriverType, Context context){

        if(DriverType.equals(DriverConst.SingleDriver)){
            /*=============================== SAVE MAIN DRIVER DATA ======================================*/

            /* Set Main Driver Details */
            SharedPref.setLoadId( DriverConst.GetDriverDetails(DriverConst.LoadID, context),  context );
            SharedPref.setDriverId( DriverConst.GetDriverDetails(DriverConst.DriverID, context) , context);


            /* Set Main Driver Settings */
            SharedPref.setUserCountryCycle(
                    "ca_cycle", DriverConst.GetDriverSettings(DriverConst.CANCycleId, context),
                    "us_cycle", DriverConst.GetDriverSettings(DriverConst.USACycleId, context), context);
            SharedPref.setTimeZone( DriverConst.GetDriverSettings(DriverConst.DriverTimeZone, context) , context);

            /* Set Main Driver NIN Number Details */
            SharedPref.setVINNumber( DriverConst.GetDriverTripDetails(DriverConst.VIN, context), context);

            Globally.TRUCK_NUMBER       =  DriverConst.GetDriverTripDetails(DriverConst.Truck, context);
            Globally.TRAILOR_NUMBER     = DriverConst.GetDriverTripDetails(DriverConst.Trailor, context);
            Globally.CONSIGNEE_NAME     = DriverConst.GetDriverTripDetails(DriverConst.ConsigneeName, context);
            Globally.SHIPPER_NAME       = DriverConst.GetDriverTripDetails(DriverConst.ShipperName, context);
            Globally.TRIP_NUMBER        = DriverConst.GetDriverTripDetails(DriverConst.TripNumber, context);

            Globally.SECOND_DRIVER_NAME = DriverConst.GetCoDriverDetails(DriverConst.CoDriverName, context);
            SharedPref.setCurrentDriverType(DriverConst.StatusSingleDriver, context);      /*Set Driver name*/



        }else{
        /*=============================== SAVE CO DRIVER DATA ======================================*/

            /* Set CO Driver Details */
            SharedPref.setLoadId( DriverConst.GetCoDriverDetails(DriverConst.CoLoadID, context),  context );
            SharedPref.setDriverId( DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, context) , context);


            /* Set CO Driver Settings */
            SharedPref.setUserCountryCycle(
                    "ca_cycle", DriverConst.GetCoDriverSettings(DriverConst.CoCANCycleId, context),
                    "us_cycle", DriverConst.GetCoDriverSettings(DriverConst.CoUSACycleId, context), context);
            SharedPref.setTimeZone( DriverConst.GetCoDriverSettings(DriverConst.CoDriverTimeZone, context) , context);

            /* Set CO Driver Trip Details */
            SharedPref.setVINNumber( DriverConst.GetCoDriverTripDetails(DriverConst.VIN, context), context);

            Globally.TRUCK_NUMBER       =  DriverConst.GetCoDriverTripDetails(DriverConst.CoTruck, context);
            Globally.TRAILOR_NUMBER     = DriverConst.GetCoDriverTripDetails(DriverConst.CoTrailor, context);
            Globally.CONSIGNEE_NAME     = DriverConst.GetCoDriverTripDetails(DriverConst.CoConsigneeName, context);
            Globally.SHIPPER_NAME       = DriverConst.GetCoDriverTripDetails(DriverConst.CoShipperName, context);
            Globally.TRIP_NUMBER        = DriverConst.GetCoDriverTripDetails(DriverConst.CoTripNumber, context);

            SharedPref.setCurrentDriverType(DriverConst.StatusTeamDriver, context);      /*Set Driver name*/

            // Globally.setTrailorNumber( DriverConst.GetCoDriverTripDetails(DriverConst.CoTrailor, context), context);
            //  Globally.setUserName( DriverConst.GetCoDriverDetails(DriverConst.CoDriverName, context), context);
           // Globally.SECOND_DRIVER_NAME = DriverConst.GetDriverDetails(DriverConst.DriverName, context);
        }
    }


    private String getStringData(JSONObject obj, String value, String key){
        if(obj.has(key))
            try {
                value     = obj.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        if(value.equalsIgnoreCase("null"))
            value = "";

        return value;
    }

    private int getIntData(JSONObject obj, String key){
        int value = 0;
        if(obj.has(key))
            try {
                value     = obj.getInt(key);
            } catch (JSONException e) {
                e.printStackTrace();
                value = 0;
            }

        return value;
    }


}
