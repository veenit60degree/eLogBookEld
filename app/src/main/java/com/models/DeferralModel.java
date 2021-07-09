package com.models;

public class DeferralModel {

    String  DriverId, DeviceId, Truck, CompanyId, Latitude, Longitude, EngineHours, Odometer, DeferralOffTime, DayCount;

    public DeferralModel(String driverId, String deviceId, String truck, String companyId, String latitude,
                         String longitude, String engineHours, String odometer, String deferralOffTime, String dayCount) {
        DriverId = driverId;
        DeviceId = deviceId;
        Truck = truck;
        CompanyId = companyId;
        Latitude = latitude;
        Longitude = longitude;
        EngineHours = engineHours;
        Odometer = odometer;
        DeferralOffTime = deferralOffTime;
        DayCount = dayCount;
    }


    public String getDriverId() {
        return DriverId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public String getTruck() {
        return Truck;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public String getEngineHours() {
        return EngineHours;
    }

    public String getOdometer() {
        return Odometer;
    }

    public String getDeferralOffTime() {
        return DeferralOffTime;
    }

    public String getDayCount() {
        return DayCount;
    }
}
