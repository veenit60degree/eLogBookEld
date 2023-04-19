package com.local.db;


public class ConstantsKeys {

    // Driver Log params
    public static String DriverLogId        = "DriverLogId";
    public static String DriverId           = "DriverId";
    public static String DriverID           = "DriverID";
    public static String CustomRecord           = "CustomRecord";

    public static String ProjectId          = "ProjectId";
    public static String DriverStatusId     = "DriverStatusId";

    public static String startDateTime      = "StartDateTime";
    public static String endDateTime        = "EndDateTime";
    public static String utcStartDateTime   = "UTCStartDateTime";
    public static String utcEndDateTime     = "UTCEndDateTime";

    public static String FirstTimeLogin     = "FirstTimeLogin";
    public static String totalMin           = "TotalHours";
    public static String isNewRecord        = "isNewRecord";    // used at Edit log time when new log is inserted.

    public static String TotalHours         = "TotalHours";
    public static String Duration           = "Duration";
    public static String StartLatitude      = "StartLatitude";
    public static String StartLongitude     = "StartLongitude";
    public static String EndLatitude        = "EndLatitude";
    public static String EndLongitude       = "EndLongitude";

    public static String YardMove           = "YardMove";
    public static String Personal           = "Personal";
    public static String IsEdited           = "IsEdited";
    public static String False              = "false";

    public static String CycleChangeType    = "CycleChangeType";
    public static String CurrentCycleId     = "CurrentCycleId";
    public static String IsViolation        = "IsViolation";
    public static String IsInternet         = "IsInternet";
    public static String IsVehicleInMotion  = "IsVehicleInMotion";
    public static String EngineHourMilesReportListUnidentified  = "EngineHourMilesReportListUnidentified";


    public static String ViolationReason        = "ViolationReason";
    public static String createdDate            = "CreatedDate";
    public static String IsShortHaulException   = "IsShortHaulException";
    public static String IsShortHaulUpdate      = "IsShortHaulUpdate";
    public static String IsAdverseException     = "IsAdverseException";
    public static String isIsShortHaulException = "isIsShortHaulException";
    public static String isIsAdverseException   = "isIsAdverseException";
    public static String AdverseExceptionRemarks= "AdverseExceptionRemarks";

    public static String AgricultureLongitude   = "AgricultureLongitude";
    public static String AgricultureLatitude    = "AgricultureLatitude";
    public static String AgricultureAddress     = "AgricultureAddress";

    public static String SourceAddress          = "SourceAddress";
    public static String SourceLatitude         = "SourceLatitude";
    public static String SourceLongitude        = "SourceLongitude";
    public static String IsEnabled              = "IsEnabled";
    public static String EventDateTimeInUtc     = "EventDateTimeInUtc";

    public static String DriverName         = "DriverName";
    public static String Remarks            = "Remarks";
    public static String StartLocation      = "StartLocation";
    public static String EndLocation        = "EndLocation";
    public static String Truck              = "Truck";
    public static String Trailor            = "Trailor";
    public static String TrailerNumber      = "TrailerNumber";
    public static String UserName           = "UserName";
    public static String DutyStatus         = "DutyStatus";

    public static String StartCity          = "StartCity";
    public static String StartState         = "StartState";
    public static String StartCountry       = "StartCountry";

    public static String EndCity            = "EndCity";
    public static String EndState           = "EndState";
    public static String EndCountry         = "EndCountry";

    public static String AppVersionCode     = "AppVersionCode";
    public static String rulesVersion       = "rulesVersion";
    public static String ChkDateTime        = "ChkDateTime";

    public static String City               = "City";
    public static String State              = "State";
    public static String Country            = "Country";
    public static String LogType            = "LogType";

    public static String EldInspectionLogId = "EldInspectionLogId";
    public static String ShareId            = "ShareId";

    public static String APIName            = "APIName";
    public static String APIData            = "APIData";
    public static String IssueDateTime      = "IssueDateTime";

    public static String Inspection            = "Inspection";
    public static String InspectionIssueTypeId = "InspectionIssueTypeId";
    public static String IssueName             = "IssueName";
    public static String Type                  = "Type";
    public static String TruckIssueList        = "InspectionTruckIssueType";
    public static String TrailorIssueList      = "InspectionTrailorIssueType";
    public static String Text                  = "Text";
    public static String Selected              = "Selected";

    public static String Mode                  = "Mode";
    public static String TimeOfChange          = "TimeOfChange";
    public static String ModeTime              = "ModeTime";

    public static String UTCDateTime            = "UTCDateTime";
    public static String TrailorNumber          = "TrailorNumber";
    public static String TruckNumber            = "TruckNumber";
    public static String IsYardMove             = "IsYardMove";
    public static String IsPersonal             = "IsPersonal";
    public static String IsYardMoveCo           = "IsYardMoveCo";
    public static String IsPersonalCo           = "IsPersonalCo";
    public static String truckno                = "truckno";
    public static String companyid              = "companyid";

    public static String SendLog                = "SendLog";
    public static String ViewCertifyDays        = "ViewCertifyDays";
    public static String EditDays               = "EditDays";
    public static String LogDate                = "LogDate";
    public static String UTCCreatedDate         = "UTCCreatedDate";
    public static String SignImage              = "SignImage";
    public static String StringImage            = "StringImage";
    public static String IsSignCopy             = "IsSignCopy";
    public static String SignedCopyDate         = "SignedCopyDate";
    public static String IsCertifyMandatory     = "IsCertifyMandatory";
    public static String IsAutoDutyNotificationAllowed  = "IsAutoDutyNotificationAllowed";

    public static String EditedReason           = "EditedReason";
    public static String TruckIssues            = "TruckIssues";
    public static String TrailerIssues          = "TrailerIssues";
    public static String TruckCtPatIssues       = "TruckCtPatIssues";
    public static String TrailerCtPatIssues     = "TrailerCtPatIssues";
    public static String IsManualAppDownload    = "IsManualAppDownload";
    public static String IsDeviceDebugLogEnable = "IsDeviceDebugLogEnable";

    public static String MalCalledLastTime      = "MalCalledLastTime";
    public static String MalCalledLastOdo       = "MalCalledLastOdo";
    public static String MalCalledLastEngHr     = "MalCalledLastEngHr";
    public static String MalCalledLastRpm       = "MalCalledLastRpm";
    public static String MalCalledLastSpeed     = "MalCalledLastSpeed";
    public static String MalCalledLastEngIgntn  = "MalCalledLastEngIgntn";



    // --------------------- Shipment params -------------------------
    public static String CoDriverId             = "CoDriverId";
    public static String DeviceId               = "DeviceId";
    public static String DeviceID               = "DeviceID";

    public static String shippingdate           = "shippingdate";
    public static String ShippingSavedDate      = "ShippingSavedDate";
    public static String ShippingDocDate        = "ShippingDocDate";
    public static String ShippingDocumentNumber = "ShippingDocumentNumber";
    public static String Commodity              = "Commodity";
    public static String ShipperName            = "ShipperName";
    public static String FromAddress            = "ShipperState";
    public static String ToAddress              = "ShipperPostalCode";
    public static String IsPosted               = "isPosted";
    public static String IsShippingCleared      = "IsShippingCleared";
    public static String IsUpdateRecord         = "IsUpdateRecord";
    public static String ShipperPostalCode      = "ShipperPostalCode";
    public static String ShipperState           = "ShipperState";
    public static String IsEmptyLoad            = "IsEmptyLoad";
    public static String ShipperDocDateStr      = "ShipperDocDateStr";
    public static String IsUnAssignedMileRecord = "IsUnAssignedMileRecord";
    public static String Address                = "Address";

    // --------------------- Odometer params -------------------------
    public static String VIN                    = "VIN";
    public static String Odometer               = "Odometer";
    public static String strOdometersKm         = "strOdometersKm";
    public static String StartOdometer          = "StartOdometer";
    public static String EndOdometer            = "EndOdometer";
    public static String DistanceType           = "DistanceType";
    public static String IsEditOdometer         = "IsEditOdometer";
    public static String TruckOdometerId        = "TruckOdometerId";
    public static String CreatedDate            = "CreatedDate";
    public static String TotalMiles             = "TotalMiles";
    public static String TotalKM                = "TotalKM";
    public static String TruckEquipmentNumber   = "TruckEquipmentNumber";
    public static String TruckEquipment         = "TruckEquipment";
    public static String VehicleNumber          = "VehicleNumber";
    public static String DriverStatusID         = "DriverStatusID";
    public static String UTCStartDateTime       = "UTCStartDateTime";
    public static String UTCEndDateTime         = "UTCEndDateTime";
    public static String CrntOdodmeter          = "CrntOdodmeter";
    public static String LocationType           = "LocationType";
    public static String CurrentStatus          = "CurrentStatus";
    public static String DeferralOffTime        = "DeferralOff_dutyTime";
    public static String DayCount               = "DayCount";
    public static String StartOdometerKM        = "StartOdometerKM";
    public static String StartOdometerMiles     = "StartOdometerMiles";
    public static String DayOdometerInKm        = "DayOdometerInKm";
    public static String DayOdometerInMiles     = "DayOdometerInMiles";
    public static String DriverVehicleTypeId    = "DriverVehicleTypeId";


    // --------------------- Inspection params -------------------------
    public static String CompanyId                      = "CompanyId";
    public static String VehicleId                      = "VehicleId";
    public static String TrailorId                      = "TrailorId";
    public static String VehicleEquNumber               = "VehicleEquNumber";
    public static String TrailorEquNumber               = "TrailorEquNumber";
    public static String InspectionDateTime             = "InspectionDateTime";
    public static String InspectionId                   = "InspectionId";
    public static String Location                       = "Location";
    public static String PreTripInspectionSatisfactory  = "PreTripInspectionSatisfactory";
    public static String PostTripInspectionSatisfactory = "PostTripInspectionSatisfactory";
    public static String AboveDefectsCorrected          = "AboveDefectsCorrected";
    public static String AboveDefectsNotCorrected       = "AboveDefectsNotCorrected";
    public static String Latitude                       = "Latitude";
    public static String Longitude                      = "Longitude";
    public static String SupervisorMechanicsName        = "SupervisorMechanicsName";
    public static String TruckIssueType                 = "TruckIssueType";
    public static String TraiorIssueType                = "TraiorIssueType";
    public static String DriverSign                     = "DriverSign";
    public static String SupervisorSign                 = "SupervisorSign";
    public static String DeviceName                     = "DeviceName";
    public static String IsStatusAutomatic              = "IsStatusAutomatic";
    public static String OBDSpeed                       = "OBDSpeed";
    public static String GPSSpeed                       = "GPSSpeed";
    public static String obdSpeed                       = "obdSpeed";
    public static String isGpsEnabled                   = "isGpsEnabled";
    public static String BleDevices                     = "BleDevices";
    public static String IsCheckSuggestedEdit           = "IsCheckSuggestedEdit";

    public static String UnitNo                         = "UnitNo";
    public static String DeviceNumber                   = "DeviceNumber";
    public static String DiagnosticType                 = "DiagnosticType";
    public static String DrivingStartTime               = "DrivingStartTime";
    public static String CycleId                        = "CycleId";
    public static String DriverTimeZone                 = "DriverTimeZone";
    public static String PowerUnitNumber                = "PowerUnitNumber";
    public static String FromDate                       = "FromDate";
    public static String ToDate                         = "ToDate";
    public static String PreviousDeviceMappingId        = "PreviousDeviceMappingId";
    public static String DeviceMappingId                = "DeviceMappingId";
    public static String IMEINumber                     = "IMEINumber";
    public static String LoginTruckChange               = "LoginTruckChange";
    public static String IsAOBRD                        = "IsAOBRD";
    public static String isDeferral                     = "isDeferral";
    public static String UnassignedVehicleMilesId       = "UnassignedVehicleMilesId";

    public static String IsCertifyLog                   = "IsCertifyLog";
    public static String SearchedDate                   = "SearchedDate";
    public static String DriverTimeZoneName             = "DriverTimeZoneName";
    public static String LogDateTime                    = "LogDateTime";
    public static String ELDSearchDate                  = "ELDSearchDate";
    public static String TeamDriverType                 = "TeamDriverType";
    public static String IsSouthCanada                  = "IsSouthCanada";
    public static String CurrentDate                    = "CurrentDate";
    public static String ActionDateTime                 = "ActionDateTime";
    public static String ActionTimeZone                 = "ActionTimeZone";
    public static String IsOdometerFromOBD              = "IsOdometerFromOBD";
    public static String GeoData                        = "GeoData";
    public static String SettingName                    = "SettingName";
    public static String SettingCurrentValue            = "SettingCurrentValue";

    public static String fromDate                       = "fromDate";
    public static String toDate                         = "toDate";
    public static String mailToIds                      = "mailToIds";
    public static String InspectorComment               = "InspectorComment";
    public static String IsMail                         = "IsMail";
    public static String IsService                      = "IsService";
    public static String latitude                       = "latitude";
    public static String longitude                      = "longitude";
    public static String EmailAddress                   = "EmailAddress";
    public static String TitalUsage                     = "TitalUsage";
    public static String EntryDate                      = "EntryDate";
    public static String AppVersion                     = "AppVersion";
    public static String Password                       = "Password";
    public static String Username                       = "Username";
    public static String CoDriverUsername               = "CoDriverUsername";
    public static String CoDriverPassword               = "CoDriverPassword";
    public static String OSTypeDeviceType               = "OSTypeDeviceType";
    public static String SIM1                           = "SIM1";
    public static String OSType                         = "OSType";
    public static String DeviceType                     = "DeviceType";
    public static String IsOffsetAvailable              = "IsOffsetAvailable";
    public static String AppBuildVersion                = "AppBuildVersion";
    public static String LastLoginDriverId              = "LastLoginDriverId";


    public static String OdometerFromOBD                = "OdometerFromOBD";
    public static String PlateNumber                    = "PlateNumber";
    public static String HaulHourException              = "HaulHourException";
    public static String Carrier                        = "Carrier";
    public static String CarrierName                    = "CarrierName";
    public static String DecesionSource                 = "DecesionSource";
    public static String PersonalUse75Km                = "PersonalUse75Km";
    public static String PersonalUseSelected            = "PersonalUseSelected";
    public static String IsAOBRDAutomatic               = "IsAOBRDAutomatic";
    public static String AOBRD                          = "AOBRD";
    public static String IsAutoDriving                  = "IsAutoDriving";
    public static String IsDrivingShippingAllowed       = "IsDrivingShippingAllowed";
    public static String IsTimestampEnabled             = "IsTimestampEnabled";
    public static String MobileDeviceCurrentDateTime    = "MobileDeviceCurrentDateTime";
    public static String DrivingAllowedStatus           = "DrivingAllowedStatus";
    public static String DrivingAllowedStatusTime       = "DrivingAllowedStatusTime";
    public static String IsEldEcmALert                  = "IsEldEcmALert";
    public static String IsUnIdenLocMissing             = "IsUnIdenLocMissing";
    public static String IsInvalidTime                  = "IsInvalidTime";
    public static String IsOBDStatusUpdate              = "IsOBDStatusUpdate";
    public static String IsPcYmAlertChangeStatus        = "IsPcYmAlertChangeStatus";
    public static String IsNeedToUpdate18DaysLog        = "IsNeedToUpdate18DaysLog";
    public static String IsUpdateMalDiaInfoWindow       = "IsUpdateMalDiaInfoWindow";
    public static String IsAutoOnDutyDriveEnabled       = "IsAutoOnDutyDriveEnabled";
    public static String IsOBDPingAllowed               = "IsOBDPingAllowed";
    public static String IsAutoSync                     = "IsAutoSync";


    public static String IsStorageMalfunction           = "IsStorageMalfunction";
    public static String IsUnidentified                 = "IsUnidentified";
    public static String IsMalfunction                  = "IsMalfunction";
    public static String IsDiagnostic                   = "IsDiagnostic";
    public static String SuggestedEdit                  = "SuggestedEdit";
    public static String IsLocMalfunction               = "IsLocMalfunction";
    public static String LocMalfunctionOccurTime        = "LocMalfunctionOccurTime";
    public static String LocMalfunctionOccurUtcTime     = "LocMalfunctionOccurUtcTime";
    public static String UnidentifiedFromOBD            = "UnidentifiedFromOBD";
    public static String IsAgriException                = "IsAgriException";
    public static String IsCertifyReminder              = "IsCertifyReminder";
    public static String Is18DaysLogUpdate              = "Is18DaysLogUpdate";
    public static String DownloadProgress               = "download_progress";
    public static String MissingDiagnostic              = "MissingDiagnostic";
    public static String BleDataService                 = "BleDataService";
    public static String BleDataAfterNotify             = "BleDataAfterNotify";
    public static String BleDataNotifier                = "BleDataNotifier";
    public static String IsConnected                    = "IsConnected";
    public static String IsAnimation                    = "IsAnimation";
    public static String AnimationType                  = "AnimationType";

    public static String IsLocDiagnostic                = "IsLocDiagnostic";
    public static String LocDiaOccurTime                = "LocDiaOccurTime";
    public static String LocDiaOccurUtcTime             = "LocDiaOccurUtcTime";
    public static String LocDiaOccurTimeForMal          = "LocDiaOccurTimeForMal";

    public static String CoDriverSwitching              = "CoDriverSwitching";
    public static String EngSyncDiagnstc                = "EngSyncDiagnstc";
    public static String EngSyncDiagnstcCo              = "EngSyncDiagnstcCo";
    public static String EngSyncMalfunction             = "EngSyncMalfunction";
    public static String PowerDiagnstc                  = "PowerDiagnstc";
    public static String PowerMalfunction               = "PowerMalfunction";
    public static String PowerMalfunctionTimeOcc        = "PowerMalfunctionTimeOcc";
    public static String isIgnitionOffCalled            = "isIgnitionOffCalled";

    public static String EngSyncDiagnosticAlso          = "EngSyncDiagnstcAlso";
    public static String EngSyncDiagnosticTimeAlso      = "EngSyncDiagnstcTImeAlso";

    public static String PowerComplianceMal             = "PowerComplianceMal";
    public static String EnginSyncMal                   = "EnginSyncMal";
    public static String PostioningComplMal             = "PostioningComplMal";
    public static String TimingComplianceMal            = "TimingComplianceMal";

    public static String PowerDataDiag                  = "PowerDataDiag";
    public static String EnginSyncDiag                  = "EnginSyncDiag";
    public static String UnidentifiedDataDiag           = "UnidentifiedDataDiag";
    public static String UnidentifiedOccTime            = "UnidentifiedOccTime";

    public static String TimingComplianceMalTime        = "TimingComplianceMalTime";
    public static String TimingMalWarningTime           = "TimingMalWarningTime";
    public static String StartOdometerInKm              = "StartOdometerInKm";
    public static String EndOdometerInKm                = "EndOdometerInKm";

    public static String UnidentifiedDiag               = "UnidentifiedDiag";
    public static String IsDuplicateStatusAllowed       = "IsDuplicateStatusAllowed";
    public static String MissingDataDiag                = "MissingDataDiag";
    public static String DataTransferDiag               = "DataTransferDiag";
    public static String DataTransferComplMal           = "DataTransferComplMal";
    public static String DataRecComMal                  = "DataRecComMal";
    public static String TimingCompMal                  = "TimingCompMal";



    public static String IsUnidentifiedCo               = "IsUnidentifiedCo";
    public static String IsMalfunctionCo                = "IsMalfunctionCo";
    public static String IsDiagnosticCo                 = "IsDiagnosticCo";
    public static String SuggestedEditCo                = "SuggestedEditCo";

    public static String ShippingDocNumberId            = "ShippingDocNumberId";
    public static String ShipperCity                    = "ShipperCity";
    public static String ServerDate                     = "ServerDate";
    public static String IsUnloading                    = "IsUnloading";
    public static String ShipperAddress                 = "ShipperAddress";

    public static String suggested_data                 = "suggested_data";
    public static String DeferralDay                    = "DeferralDay";
    public static String DeferralDate                   = "DeferralDate";


    public static String EventDateTime                  = "EventDateTime";
    public static String EventEndDateTime               = "EventEndDateTime";
    public static String IsClearEvent                   = "IsClearEvent";
    public static String ClearEventId                   = "ClearEventId";

    public static String ClearOdometer                  = "ClearOdometer";
    public static String ClearEngineHours               = "ClearEngineHours";
    public static String ClearEventDateTime             = "ClearEventDateTime";
    public static String IsOccurEventAlreadyUploaded    = "IsOccurEventAlreadyUploaded";



    public static String EngineHours                    = "EngineHours";
    public static String Miles                          = "Miles";
    public static String DetectionDataEventCode         = "DetectionDataEventCode";
    public static String MasterDetectionDataEventId     = "MasterDetectionDataEventId";
    public static String EventCode                      = "EventCode";
    public static String EventName                      = "EventName";
    public static String Definition                     = "Definition";
    public static String array                          = "array";
    public static String list                           = "list";
    public static String Reason                         = "Reason";
    public static String MalfunctionDefinition          = "MalfunctionDefinition";
    public static String FromDateTime                   = "FromDateTime";
    public static String ToDateTime                     = "ToDateTime";
    public static String DriverZoneEventDate            = "DriverZoneEventDate";
    public static String SEQUENCE_NO                    = "SEQUENCE_NO";
    public static String HEXA_SEQUENCE_NUMBER           = "HexaSequenceNumber";
    public static String Id                             = "Id";
    public static String ApiCallStatus                  = "ApiCallStatus";
    //public static String ApiName                        = "ApiName";
    public static String ApiFlag                        = "ApiFlag";
    public static String IsAlreadyCalled                = "IsAlreadyCalled";
    public static String ApiCalledDate                  = "ApiCalledDate";
    public static String OnDutyRemarks                  = "OnDutyRemarks";

    public static String FailedApiName                  = "FailedApiName";
    public static String FailedApiCount                 = "FailedApiCount";
    public static String FailedApiTime                  = "FailedApiTime";

    public static String ArrivalSealNumber              = "ArrivalSealNumber";
    public static String DepartureSealNumber            = "DepartureSealNumber";
    public static String SecurityInspectionPersonName   = "SecurityInspectionPersonName";
    public static String FollowUpInspectionPersonName   = "FollowUpInspectionPersonName";
    public static String AffixedSealPersonName          = "AffixedSealPersonName";
    public static String VerificationPersonName         = "VerificationPersonName";
    public static String ByteInspectionConductorSign    = "ByteInspectionConductorSign";
    public static String ByteFollowUpConductorSign      = "ByteFollowUpConductorSign";
    public static String ByteSealFixerSign              = "ByteSealFixerSign";
    public static String ByteSealVerifierSign           = "ByteSealVerifierSign";

    public static String SecurityInspectionPersonSignature    = "SecurityInspectionPersonSignature";
    public static String FollowUpInspectionPersonSignature    = "FollowUpInspectionPersonSignature";
    public static String AffixedSealPersonSignature           = "AffixedSealPersonSignature";
    public static String VerificationPersonSignature          = "VerificationPersonSignature";


    public static String InspectionTypeId               = "InspectionTypeId";
    public static String ByteDriverSign                 = "ByteDriverSign";
    public static String DriverSignature                = "DriverSignature";
    public static String ByteSupervisorSign             = "ByteSupervisorSign";
    public static String SupervisorMechanicsSignature   = "SupervisorMechanicsSignature";

    public static String InspectionTruckIssueType       = "InspectionTruckIssueType";
    public static String InspectionTrailorIssueType     = "InspectionTrailorIssueType";

    public static String OdometerInMeters               = "OdometerInMeters";
    public static String LogSignImage                   = "LogSignImage";
    public static String LogSignImageInByte             = "LogSignImageInByte";
    public static String CertifyOldImage             = "CertifyOldImage";
    public static String CoDriverName                   = "CoDriverName";
    public static String CoDriverKey                    = "CoDriverKey";
    public static String IsSkipRecord                   = "IsSkipRecord";   // true used for same status like DRIVING/ONDUTY when violation occurred
    public static String EngineMileage                  = "EngineMileage";
    public static String Date                           = "Date";
    public static String IsRecertifyRequied             = "IsRecertifyRequied";

    public static String CycleDaysDriverLogModel        = "CycleDaysDriverLogModel";
    public static String DriverLogModel                 = "DriverLogModel";

    public static String Day                            = "Day";
    public static String HoursWorked                    = "HoursWorked";

    public static String UTCDate                        = "UTCDate";
    public static String MobileUtcDate                  = "MobileUtcDate";

    /*=================== Support Constant =================== */
    public static String SupportDetailId                = "SupportDetailId";
    public static String SupportKey                     = "Key";
    public static String SupportValue                   = "Value";
    public static String SupportKeyType                 = "KeyType";
    public static String SupportIsActive                = "IsActive";
    public static String SupportCreateDate              = "CreatedDate";


    /*=================== Driver Permissions Constant =================== */
    public static String OffDutyKey                     = "OffDuty";
    public static String SleeperKey                     = "Sleeper";
    public static String DrivingKey                     = "Driving";
    public static String OnDutyKey                      = "OnDuty";
    public static String LocationKey                    = "Location";

    public static String RecordType                     = "RecordType";
    public static String RecordValue                    = "RecordValue";
    public static String EditDateTimeUTC                = "EditDateTimeUTC";
    public static String EditDateTime                   = "EditDateTime";
    public static String CertifyLogDate                 = "CertifyLogDate";

    public static String CrntLat                        = "CrntLat";
    public static String CrntLong                       = "CrntLong";

    public static String StartDate                      = "StartDate";
    public static String StartLat                       = "StartLat";
    public static String StartLon                       = "StartLon";

    public static String EndDate                        = "EndDate";
    public static String EndLat                         = "EndLat";
    public static String EndLon                         = "EndLon";
    public static String PreviousRemarks                = "PreviousRemarks";
    public static String IsPersonalRecord               = "IsPersonalRecord";




    /*=================== Driver Notification History Constant =================== */
    public static String NotificationLogId              = "NotificationLogId";
    public static String NotificationTypeId             = "NotificationTypeId";
    public static String NotificationTypeName           = "NotificationTypeName";
    public static String Title                          = "Title";
    public static String Desc                           = "Desc";
    public static String Message                        = "Message";
    public static String ImagePath                      = "ImagePath";
    public static String SendDate                       = "SendDate";
    public static String Status                         = "Status";
    public static String Data                           = "Data";
    public static String ClearUnIdentifiedData          = "ClearUnIdentifiedData";
    public static String SaveOfflineData                = "SaveOfflineData";
    public static String DriverLogDate                  = "DriverLogDate";
    public static String DriverLogIds                   = "DriverLogIds";
    public static String SuggestedEditModel             = "SuggestedEditModel";
    public static String MalfunctionEngSync             = "SuggestedEditModel";
    public static String DiagnosticEngSync              = "SuggestedEditModel";
    public static String Allcodriver                    = "Allcodriver";

    // ELD Rule Inputs

    public static String CurrentDateTime                = "CurrentDateTime";
    public static String CurrentUTCTime                 = "CurrentUTCTime";
    public static String OffsetFromUTC                  = "OffsetFromUTC";
    public static String OffsetFromUtc                  = "OffsetFromUtc";
    public static String IsSingleDriver                 = "IsSingleDriver";
    public static String DriverJobStatus                = "DriverJobStatus";
    public static String IsOldRecord                    = "IsOldRecord";
    public static String Is16hrHaulException            = "Is16hrHaulException";
    public static String ViolationTest                  = "ViolationTest";
    public static String ELDCycleFile                   = "ELDCycleFile";
    public static String ALS_OBD_LOG                    = "als_obd_log";
    public static String SERVER_OBD_LOG                 = "obd_server_log";
    public static String APP_USAGE_LOG                  = "app_usage_log";
    public static String EXECUTION_TIME_LOG             = "execution_time_Log";
    public static String WIRED_OBD_SERVER_LOG           = "obd_server_log";

    public static String Version                        = "Version";
    public static String Heading                        = "Heading";
    public static String DocumentNumber                 = "DocumentNumber";
    public static String ServerDateTime                 = "ServerDateTime";
    public static String ELDFilePath                    = "ELDFilePath";


    public static String AlsSendingData                 = "AlsSendingData";
    public static String AlsReceivedData                = "AlsReceivedData";
    public static String MobileUsage                    = "MobileUsage";
    public static String TotalUsage                     = "TotalUsage";

    public static String MemoryUsage                    = "MemoryUsage";
    public static String CpuUsage                       = "CpuUsage";
    public static String ExecutionTime                  = "ExecutionTime";
    public static String Date_Time                      = "DateTime";
    public static String TotalMin                       = "TotalMin";
    public static String TotalMinutes                   = "TotalMinutes";

    public static String EquipmentNumber                = "EquipmentNumber";
    public static String TotalKm                        = "TotalKm";
    public static String StartDateTime                  = "StartDateTime";
    public static String EndDateTime                    = "EndDateTime";
    public static String StatusId                       = "StatusId";
    public static String CompanyStartDateTime           = "CompanyStartDateTime";
    public static String CompanyEndDateTime             = "CompanyEndDateTime";

    public static String DriverStartDateTime           = "DriverStartDateTime";
    public static String DriverEndDateTime             = "DriverEndDateTime";

    public static String LastDutyStatus             = "LastDutyStatus";
    public static String StatusStartTime             = "StatusStartTime";
    public static String StatusEndTime             = "StatusEndTime";
    public static String StartEngineSeconds             = "StartEngineSeconds";
    public static String EndEngineSeconds             = "EndEngineSeconds";
    public static String  Intermediate            = "Intermediate";
    public static String IntermediateUpdate             = "IntermediateUpdate";
    public static String IntermediateLogId             = "IntermediateLogId";
    public static String IsUploadedUnIdenRecord        = "IsUploadedUnIdenRecord";


    public static String UnidenStartOdometer               = "UnidenStartOdometer";
    public static String UnidenStartLatitude               = "UnidenStartLatitude";
    public static String UnidenStartLongitude               = "UnidenStartLongitude";
    public static String UnidenStartTime                   = "UnidenStartTime";
    public static String UnidenStartEngineSeconds          = "UnidenStartEngineSeconds";

/*
    public static String IsCleared                      = "IsCleared";
    public static String ClearedTime                    = "ClearedTime";
    public static String ClearedTimeOdometer            = "ClearedTimeOdometer";
    public static String ClearedTimeEngineHours         = "ClearedTimeEngineHours";
*/

    public static String IsCleared                      = "IsClearEvent";
    public static String ClearedTime                    = "ClearEventDateTime";
    public static String ClearedTimeOdometer            = "ClearOdometer";
    public static String ClearedTimeEngineHours         = "ClearEngineHours";


    public static String IsShowOnMobileApp              = "IsShowOnMobileApp";
    public static String NewsTitle                      = "NewsTitle";
    public static String NewsDescription                = "NewsDescription";

    public static String UnAssignedVehicleMilesId       = "UnAssignedVehicleMilesId";
    public static String AssignedUnidentifiedRecordsId  = "AssignedUnidentifiedRecordsId";
    public static String RejectionRemarks               = "RejectionRemarks";
    public static String Unidentified                   = "Unidentified";
    public static String CompanyAssigned                = "CompanyAssigned";
    public static String EventList                      = "EventList";
    public static String StartLocationKM                = "StartLocationKM";
    public static String EndLocationKM                  = "EndLocationKM";

    public static String DisConnectStartTime            = "DisConnectStartTime";
    public static String DisConnectEndTime              = "DisConnectEndTime";

    public static String IsAllowLogReCertification      = "IsAllowLogReCertification";
    public static String IsShowUnidentifiedRecords      = "IsShowUnidentifiedRecords";
    public static String IsAllowMalfunction             = "IsAllowMalfunction";
    public static String IsAllowDiagnostic              = "IsAllowDiagnostic";
    public static String IsClearMalfunction             = "IsClearMalfunction";
    public static String IsClearDiagnostic              = "IsClearDiagnostic";
    public static String DriverIdDiaMalMain             = "DriverIdDiaMalMain";
    public static String UnIdentifiedAlertStatus        = "UnIdentifiedDialogAlertStatus";
    public static String UnIdentifiedAlertStatusCo      = "UnIdentifiedDialogAlertStatusCo";
    public static String IsBleEnabled                   = "IsBleEnabled";
    public static String IsGpsEnabled                   = "IsGpsEnabled";
    public static String LocationStatus                 = "LocationStatus";

    public static String IsAllowLogReCertificationCo    = "IsAllowLogReCertificationCo";
    public static String IsShowUnidentifiedRecordsCo    = "IsShowUnidentifiedRecordsCo";
    public static String IsAllowMalfunctionCo           = "IsAllowMalfunctionCo";
    public static String IsAllowDiagnosticCo            = "IsAllowDiagnosticCo";
    public static String IsClearMalfunctionCo           = "IsClearMalfunctionCo";
    public static String IsClearDiagnosticCo            = "IsClearDiagnosticCo";
    public static String DriverIdDiaMalCo               = "DriverIdDiaMalCo";

    public static String IsCCMTACertified               = "IsCCMTACertified";
    public static String IsNorthCanada                  = "IsNorthCanada";
    public static String IsNorthCanadaCo                = "IsNorthCanadaCo";
    public static String IsExemptDriver                 = "IsExemptDriver";
    public static String IsExemptDriverCo               = "IsExemptDriverCo";
    public static String IsCycleRequest                 = "IsCycleRequest";
    public static String IsCycleRequestMain             = "IsCycleRequestMain";
    public static String IsCycleRequestCo               = "IsCycleRequestCo";
    public static String IsELDNotificationShown         = "IsELDNotificationShown";
    public static String DriverELDNotificationList      = "DriverELDNotificationList";
    public static String IsELDNotification              = "IsELDNotification";
    public static String IsELDNotificationAlert         = "IsELDNotificationAlert";
    public static String ObdPreference                  = "ObdPreference";
    public static String HighPrecisionUnit              = "HighPrecisionUnit";
    public static String StartLocationKm                = "StartLocationKm";
    public static String IsOdoCalculationAllowed        = "IsOdoCalculationAllowed";
    public static String IsEngineRestarted              = "IsEngineRestarted";
    public static String IsActiveHosScreen              = "IsActiveHosScreen";
    public static String IsActiveHomeScreen             = "IsActiveHomeScreen";
    public static String IsAppRestricted                = "IsAppRestricted";
    public static String LocReceivedFromObd             = "LocReceivedFromObd";
    public static String IsCycleChanged                 = "IsCycleChanged";
    public static String LocationSource                 = "LocationSource";
    public static String IsDriverLogout                 = "IsDriverLogout";

    public static String IsDismissDialog                = "IsDismissDialog";
    public static String ChangedToOthers                = "ChangedToOthers";
    public static String IsAutoStatusSaved              = "IsAutoStatusSaved";
    public static String IsIgnitionOn                   = "IsIgnitionOn";
    public static String obdCurrentIgnition             = "obdCurrentIgnition";
    public static String obdType                        = "obdType";
    public static String obdTime                        = "obdTime";
    public static String IsEventUpdate                  = "IsEventUpdate";
    public static String IsLocalEventUpdate             = "IsLocalEventUpdate";


    public static String GeoLocation                    = "GeoLocation";
    public static String StatusName                     = "StatusName";
    public static String cycleRequests                  = "cycleRequests";
    public static String DateTimeWithMins               = "DateTimeWithMins";
    public static String EventUTCTimeStamp              = "EventUTCTimeStamp";
    public static String EventType                      = "EventType";
    public static String DutyMinutes                    = "DutyMinutes";

    public static String Annotation                     = "Annotation";
    public static String EventDate                      = "EventDate";
    public static String EventTime                      = "EventTime";
    public static String AccumulatedVehicleMiles        = "AccumulatedVehicleMiles";
    public static String AccumulatedVehicleKm           = "AccumulatedVehicleKm";
    public static String AccumulatedEngineHours         = "AccumulatedEngineHours";
    public static String TotalVehicleMiles              = "TotalVehicleMiles";
    public static String TotalEngineHours               = "TotalEngineHours";
    public static String GPSLatitude                    = "GPSLatitude";
    public static String GPSLongitude                   = "GPSLongitude";
    public static String CMVVIN                         = "CMVVIN";

    public static String OdometerInKm                   = "OdometerInKm";
    public static String OdometerInMiles                = "OdometerInMiles";
    public static String OdometerInMeter                = "OdometerInMeter";
    public static String strEventType                   = "strEventType";
    public static String Origin                         = "Origin";
    public static String StartTime                      = "StartTime";
    public static String EndTime                        = "EndTime";
    public static String OBDDeviceDataId                = "OBDDeviceDataId";
    public static String CurrentObdDeviceDataId         = "CurrentObdDeviceDataId";
    public static String IsYard                         = "IsYard";
    public static String PowerEvent                     = "PowerEvent";
    public static String TimeStampUTC                   = "TimeStampUTC";
    public static String OffsetUTC                      = "OffsetUTC";


    public static String SequenceNumber                 = "SequenceNumber";

    public static String TotalVehicleKM                 = "TotalVehicleKM";
    public static String AdditionalInfo                 = "AdditionalInfo";
    public static String EditedById                     = "EditedById";

    public static String RecordStatus                   = "RecordStatus";

    public static String DistanceSinceLastValidCord     = "DistanceSinceLastValidCord";
    public static String RecordOrigin                   = "RecordOrigin";
    public static String DistanceInKM                   = "DistanceInKM";
    public static String HexaSeqNumber                  = "HexaSeqNumber";
    public static String OrderBy                        = "OrderBy";
    public static String OnDutyHours                    = "OnDutyHours";
    public static String OffDutyHours                   = "OffDutyHours";
    public static String TruckEquipmentNo               = "TruckEquipmentNo";
    public static String WorkShiftStart                 = "WorkShiftStart";
    public static String WorkShiftEnd                   = "WorkShiftEnd";
    public static String DriverZoneStartDateTime        = "DriverZoneStartDateTime";
    public static String DriverZoneEndDateTime          = "DriverZoneEndDateTime";
    public static String IsIntermediateLog              = "IsIntermediateLog";

    public static String graphRecordList                = "graphRecordList";
    public static String EngineHourMilesReportList      = "EngineHourMilesReportList";
    public static String loginAndLogoutDates            = "loginAndLogoutDates";
    public static String dutyStatusChangesList          = "dutyStatusChangesList";
    public static String loginAndLogoutList             = "loginAndLogoutList";
    public static String ChangeInDriversCycleList       = "ChangeInDriversCycleList";
    public static String commentsRemarksList            = "commentsRemarksList";
    public static String additionalHoursNotRecordedList = "additionalHoursNotRecordedList";
    public static String enginePowerUpAndShutDownList   = "enginePowerUpAndShutDownList";
    public static String UnAssignedVehicleMilesList     = "UnAssignedVehicleMilesList";


    public static String TotalOffDutyHours              = "TotalOffDutyHours";
    public static String TotalSleeperHours              = "TotalSleeperHours";
    public static String TotalDrivingHours              = "TotalDrivingHours";
    public static String TotalOnDutyHours               = "TotalOnDutyHours";

    public static String AgricultureIssueType           = "AgricultureIssueType";
    public static String AreaOfInspectionRemarks        = "AreaOfInspectionRemarks";
    public static String AgricultureIssueTypeInspection = "AgricultureIssueTypeInspection";
    public static String ContainerIdentification        = "ContainerIdentification";

    public static String oReportList                    = "oReportList";
    public static String ShippingInformationModel       = "ShippingInformationModel";
    public static String RecordDate                     = "RecordDate";
    public static String PrintDisplayDate               = "PrintDisplayDate";
    public static String USDOTNumber                    = "USDOTNumber";
    public static String DriverLicenseNumber            = "DriverLicenseNumber";
    public static String DriverLicenseState             = "DriverLicenseState";
    public static String ELDID                          = "ELDID";
    public static String TrailerId                      = "TrailerId";
    public static String  TimeZone                      = "TimeZone";
    public static String ELDManufacturer                = "ELDManufacturer";
    public static String ShippingID                     = "ShippingID";
    public static String DataDiagnosticIndicators       = "DataDiagnosticIndicators";
    public static String PeriodStartingTime             = "PeriodStartingTime";
    public static String CoDriverID                     = "CoDriverID";
    public static String  TruckTractorID                = "TruckTractorID";
    public static String UnIdentifiedDriverRecords      = "UnIdentifiedDriverRecords";
    public static String ELDMalfunctionIndicators       = "ELDMalfunctionIndicators";
    public static String TruckTractorVIN                = "TruckTractorVIN";
    public static String ExemptDriverStatus             = "ExemptDriverStatus";
    public static String StartEndEngineHours            = "StartEndEngineHours";
    public static String CurrentLocation                = "CurrentLocation";
    public static String FileComment                    = "FileComment";
    public static String OfficeAddress                  = "OfficeAddress";
    public static String StartEndOdometer               = "StartEndOdometer";
    public static String StartEndOdometerKM             = "StartEndOdometerKM";
    public static String OdometerDifference             = "OdometerDifference";
    public static String OdometerDifferenceKM           = "OdometerDifferenceKM";
    public static String ConnectedAfterLogin            = "ConnectedAfterLogin";

}
