package com.driver.details;

public class EldDriverLogModel {

    int DriverStatusId;
    String StartDateTime;
    String EndDateTime;
    String TotalHours;
    String CurrentCycleId;
    boolean IsViolation;
    String UTCStartDateTime;
    String UTCEndDateTime;
    String Duration;
    String Location;
    String Remarks;
    boolean IsPersonal;
    boolean IsAdverseException;
    boolean IsShortHaulException;


    public EldDriverLogModel(int driverStatusId, String startDateTime, String endDateTime, String totalHours,
                             String currentCycleId, boolean isViolation, String UTCStartDateTime, String UTCEndDateTime,
                             String duration, String location, String remarks, boolean isPersonal,
                             boolean isAdverseException , boolean isShortHaulException) {
        DriverStatusId = driverStatusId;
        StartDateTime = startDateTime;
        EndDateTime = endDateTime;
        TotalHours = totalHours;
        CurrentCycleId = currentCycleId;
        IsViolation = isViolation;
        this.UTCStartDateTime = UTCStartDateTime;
        this.UTCEndDateTime = UTCEndDateTime;
        Duration = duration;
        Location = location;
        Remarks = remarks;
        IsPersonal = isPersonal;
        IsAdverseException = isAdverseException;
        IsShortHaulException = isShortHaulException;

    }


    public Integer getDriverStatusId() {
        return DriverStatusId;
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
}
