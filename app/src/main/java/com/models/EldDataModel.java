package com.models;


public class EldDataModel {

    String DriverStatusId;
    String DriverId;

    String Latitude;
    String Longitude;
    String DriverCycleId;

    String CompanyTimeZone;
    String UtcTimeZone;
    String VIN_NUMBER;

    String isYard;
    String isPersonal;

    String StartOBDDeviceDataId;
    String EndOBDDeviceDataId;
    String DrivingStartTime;

    String IsContinueDriving;
    String TrailorNumber;
    String Remarks;


    public EldDataModel(String driverStatusId, String driverId,
                        String latitude, String longitude, String driverCycleId,
                        String companyTimeZone, String utcTimeZone, String VIN_NUMBER,
                        String isYard, String isPersonal, String startOBDDeviceDataId,
                        String endOBDDeviceDataId, String drivingStartTime,
                        String isContinueDriving, String trailorNumber, String remarks) {

        DriverStatusId = driverStatusId;
        DriverId = driverId;
        Latitude = latitude;
        Longitude = longitude;
        DriverCycleId = driverCycleId;
        CompanyTimeZone = companyTimeZone;
        UtcTimeZone = utcTimeZone;
        this.VIN_NUMBER = VIN_NUMBER;
        this.isYard = isYard;
        this.isPersonal = isPersonal;
        StartOBDDeviceDataId = startOBDDeviceDataId;
        EndOBDDeviceDataId = endOBDDeviceDataId;
        DrivingStartTime = drivingStartTime;
        IsContinueDriving = isContinueDriving;
        TrailorNumber = trailorNumber;
        Remarks = remarks;
    }


    public String getDriverStatusId() {
        return DriverStatusId;
    }

    public String getDriverId() {
        return DriverId;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public String getDriverCycleId() {
        return DriverCycleId;
    }

    public String getCompanyTimeZone() {
        return CompanyTimeZone;
    }

    public String getUtcTimeZone() {
        return UtcTimeZone;
    }

    public String getVIN_NUMBER() {
        return VIN_NUMBER;
    }

    public String getIsYard() {
        return isYard;
    }

    public String getIsPersonal() {
        return isPersonal;
    }

    public String getStartOBDDeviceDataId() {
        return StartOBDDeviceDataId;
    }

    public String getEndOBDDeviceDataId() {
        return EndOBDDeviceDataId;
    }

    public String getDrivingStartTime() {
        return DrivingStartTime;
    }

    public String getIsContinueDriving() {
        return IsContinueDriving;
    }

    public String getTrailorNumber() {
        return TrailorNumber;
    }

    public String getRemarks() {
        return Remarks;
    }
}
