package com.models;

public class EldDriverLogModel {

    int DriverStatusId;
    String DriverStatusLogId;
    String StartDateTime;
    String EndDateTime;
    String TotalHours;
    String CurrentCycleId;
    boolean IsViolation;
    String UTCStartDateTime;
    String UTCEndDateTime;
    String Duration;
    String Location;
    String LocationKm;
    String Remarks;
    boolean IsPersonal;
    boolean IsAdverseException;
    boolean IsShortHaulException;
    String StartLatitude;
    String StartLongitude;


    public EldDriverLogModel(int driverStatusId, String driverStatusLogId, String startDateTime, String endDateTime, String totalHours,
                             String currentCycleId, boolean isViolation, String UTCStartDateTime, String UTCEndDateTime,
                             String duration, String location, String locationKm, String remarks, boolean isPersonal,
                             boolean isAdverseException , boolean isShortHaulException, String startLatitude, String startLongitude) {
        DriverStatusId = driverStatusId;
        DriverStatusLogId = driverStatusLogId;
        StartDateTime = startDateTime;
        EndDateTime = endDateTime;
        TotalHours = totalHours;
        CurrentCycleId = currentCycleId;
        IsViolation = isViolation;
        this.UTCStartDateTime = UTCStartDateTime;
        this.UTCEndDateTime = UTCEndDateTime;
        Duration = duration;
        Location = location;
        LocationKm = locationKm;
        Remarks = remarks;
        IsPersonal = isPersonal;
        IsAdverseException = isAdverseException;
        IsShortHaulException = isShortHaulException;
        StartLatitude = startLatitude;
        StartLongitude = startLongitude;

    }


    public Integer getDriverStatusId() {
        return DriverStatusId;
    }

    public String getDriverStatusLogId() {
        return DriverStatusLogId;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public String getTotalHours() {
        return TotalHours;
    }

    public String getCurrentCycleId() {
        return CurrentCycleId;
    }

    public boolean isViolation() {
        return IsViolation;
    }

    public String getUTCStartDateTime() {
        return UTCStartDateTime;
    }

    public String getUTCEndDateTime() {
        return UTCEndDateTime;
    }


    public String getDuration() {
        return Duration;
    }

    public String getLocation() {
        return Location;
    }

    public String getRemarks() {
        return Remarks;
    }

    public boolean isPersonal() {
        return IsPersonal;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public boolean isAdverseException() {
        return IsAdverseException;
    }

    public boolean isShortHaulException() {
        return IsShortHaulException;
    }

    public String getLocationKm() {
        return LocationKm;
    }

    public void setLocationKm(String locationKm) {
        LocationKm = locationKm;
    }

    public String getStartLatitude() {
        return StartLatitude;
    }

    public String getStartLongitude() {
        return StartLongitude;
    }
}
