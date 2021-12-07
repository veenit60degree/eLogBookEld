package com.models;

public class EldDataModelNew {


    String ProjectId;
    String DriverId;
    String DriverStatusId;

    String IsYardMove;
    String IsPersonal;
    String DeviceID;

    String Remarks;
    String UTCDateTime;
    String TrailorNumber;
    String TruckNumber;
    String CompanyId;
    String DriverName;

    String City;
    String State;
    String Country;

    String IsViolation;
    String ViolationReason;

    String Latitude;
    String Longitude;

    String IsStatusAutomatic;

    String OBDSpeed;
    String GPSSpeed;
    String PlateNumber;

    String HaulHourException;
    String IsShortHaulUpdate;
    String DecesionSource;
    String IsAdverseException ;
    String AdverseExceptionRemarks;

    String EditedReason;
    String LocationType;
    String IsNorthCanada;

    String DrivingStartTime;
    String IsAobrd;
    String CurrentCycleId;
    String isDeferral;
    String UnassignedVehicleMilesId;
    String isNewRecord;
    String IsCycleChanged;

    public EldDataModelNew(String projectId, String driverId, String driverStatusId, String isYard,
                           String isPersonal, String deviceID, String remarks, String utcDateTime,
                           String truckNumber, String trailorNumber, String companyId, String driverName, String city,
                           String state, String country, String isViolation, String violationReason,
                           String latitude, String longitude, String isStatusAutomatic,
                           String oBDSpeed , String gPSSpeed, String plateNumber, String haulHourException , String isShortHaulUpdate,
                           String decesionSource, String isAdverseExc , String adverseExcRemarks, String editedReason, String locationType,
                           String isNorthCanada, String DrivingStartTime, String IsAobrd,
                           String CurrentCycleId, String isDeferral, String UnassignedVehicleMilesId,
                           String isNewRecord, String isCycleChanged
    ) {

        ProjectId           = projectId;
        DriverId            = driverId;
        DriverStatusId      = driverStatusId;
        this.IsYardMove     = isYard;
        this.IsPersonal     = isPersonal;
        DeviceID            = deviceID;
        Remarks             = remarks;
        UTCDateTime         = utcDateTime;
        TruckNumber         = truckNumber;
        TrailorNumber       = trailorNumber;
        CompanyId           = companyId;
        DriverName          = driverName;
        City                = city;
        State               = state;
        Country             = country;
        IsViolation         = isViolation;
        ViolationReason     = violationReason;
        Latitude            = latitude;
        Longitude           = longitude;
        IsStatusAutomatic   = isStatusAutomatic;
        OBDSpeed            = oBDSpeed;
        GPSSpeed            = gPSSpeed;
        PlateNumber         = plateNumber;
        HaulHourException   = haulHourException;
        IsShortHaulUpdate   = isShortHaulUpdate;
        DecesionSource      = decesionSource;
        IsAdverseException  = isAdverseExc;
        AdverseExceptionRemarks = adverseExcRemarks;
        EditedReason        = editedReason;
        LocationType        = locationType;
        IsNorthCanada       = isNorthCanada;

        this.DrivingStartTime = DrivingStartTime;
        this.IsAobrd = IsAobrd;
        this.CurrentCycleId = CurrentCycleId;
        this.isDeferral = isDeferral;
        this.UnassignedVehicleMilesId = UnassignedVehicleMilesId;
        this.isNewRecord            = isNewRecord;
        this.IsCycleChanged = isCycleChanged;

    }

    public String getProjectId() {
        return ProjectId;
    }

    public String getDriverId() {
        return DriverId;
    }

    public String getDriverStatusId() {
        return DriverStatusId;
    }

    public String getIsYard() {
        return IsYardMove;
    }

    public String getIsPersonal() {
        return IsPersonal;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public String getRemarks() {
        return Remarks;
    }

    public String getUTCDateTime() {
        return UTCDateTime;
    }

    public String getTrailorNumber() {
        return TrailorNumber;
    }

    public String getTruckNumber(){
        return TruckNumber;
    }

    public String getIsStatusAutomatic() {
        return IsStatusAutomatic;
    }

    public String getIsYardMove() {
        return IsYardMove;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public String getDriverName() {
        return DriverName;
    }

    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }

    public String getCountry() {
        return Country;
    }

    public String getIsViolation() {
        return IsViolation;
    }

    public String getViolationReason() {
        return ViolationReason;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public String IsStatusAutomatic() {
        return IsStatusAutomatic;
    }

    public String getGPSSpeed() {
        return GPSSpeed;
    }

    public String getOBDSpeed() {
        return OBDSpeed;
    }

    public String getPlateNumber() {
        return PlateNumber;
    }


    public String getHaulHourException() {
        return HaulHourException;
    }

    public String getShortHaulUpdate() {
        return IsShortHaulUpdate;
    }


    public String getDecesionSource(){
        return DecesionSource;
    }


    public String getIsAdverseException() {
        return IsAdverseException;
    }

    public String getAdverseExceptionRemarks() {
        return AdverseExceptionRemarks;
    }

    public String getEditedReason() {
        return EditedReason;
    }

    public String getLocationType() {
        return LocationType;
    }

    public String getIsShortHaulUpdate() {
        return IsShortHaulUpdate;
    }

    public String getIsNorthCanada() {
        return IsNorthCanada;
    }

    public String getDrivingStartTime() {
        return DrivingStartTime;
    }

    public String getIsAobrd() {
        return IsAobrd;
    }

    public String getCurrentCycleId() {
        return CurrentCycleId;
    }

    public String getIsDeferral() {
        return isDeferral;
    }

    public String getUnassignedVehicleMilesId() {
        return UnassignedVehicleMilesId;
    }

    public String getNewRecordStatus() {
        return isNewRecord;
    }

    public String IsCycleChanged() {
        return IsCycleChanged;
    }
}
