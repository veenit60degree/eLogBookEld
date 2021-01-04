package com.models;

public class DotDataModel {

    String DriverStatus;
    String StartTime;
    String EndTime;
    boolean IsMalfunction;


    String Time;
    String Location;
    String StartOdometer;
    String EndOdomoter;
    String OdometerInKm;
    String OdometerInMiles;
    String EngineHours;
    String EventTypeStatus;
    String Origin;

    public DotDataModel(String driverStatus, String startTime, String endTime, boolean isMalfunction, String time, String location,
                        String startOdometer, String endOdomoter, String odometerInKm, String odometerInMiles, String engineHours, String eventTypeStatus, String origin) {
        DriverStatus = driverStatus;
        StartTime = startTime;
        EndTime = endTime;
        this.IsMalfunction = isMalfunction;

        Time = time;
        Location = location;
        StartOdometer = startOdometer;
        EndOdomoter = endOdomoter;
        OdometerInKm = odometerInKm;
        OdometerInMiles = odometerInMiles;
        EngineHours = engineHours;
        EventTypeStatus = eventTypeStatus;
        Origin = origin;
    }

    public String getDriverStatus() {
        return DriverStatus;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public boolean isViolation() {
        return IsMalfunction;
    }

    public String getTime() {
        return Time;
    }

    public String getLocation() {
        return Location;
    }

    public String getStartOdometer() {
        return StartOdometer;
    }

    public String getEndOdomoter() {
        return EndOdomoter;
    }

    public String getOdometerInKm() {
        return OdometerInKm;
    }

    public String getOdometerInMiles() {
        return OdometerInMiles;
    }

    public String getEngineHours() {
        return EngineHours;
    }

    public String getEventTypeStatus() {
        return EventTypeStatus;
    }

    public String getOrigin() {
        return Origin;
    }
}
