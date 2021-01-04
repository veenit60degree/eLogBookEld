package com.models;


public class DriverDetailModel {

    int DriverLogId;
    int DriverId;
    int ProjectId;
    int DriverStatusId;
    String StartDateTime;
    String EndDateTime;
    String TotalHours;
    double StartLatitude;
    double StartLongitude;
    double EndLatitude;
    double EndLongitude;
    int CurrentCycleId;
    boolean IsViolation;
    String CreatedDate;
    String UTCStartDateTime;
    String UTCEndDateTime;


    public DriverDetailModel(int driverLogId, int driverId, int projectId,
                             int driverStatusId, String startDateTime, String endDateTime,
                             String totalHours, double startLatitude, double startLongitude,
                             double endLatitude, double endLongitude, int currentCycleId,
                             boolean isViolation, String createdDate,
                             String UTCStartDateTime, String UTCEndDateTime ) {
        this.DriverLogId = driverLogId;
        this.DriverId = driverId;
        this.ProjectId = projectId;
        this.DriverStatusId = driverStatusId;
        this.StartDateTime = startDateTime;
        this.EndDateTime = endDateTime;
        this.TotalHours = totalHours;
        this.StartLatitude = startLatitude;
        this.StartLongitude = startLongitude;
        this.EndLatitude = endLatitude;
        this.EndLongitude = endLongitude;
        this.CurrentCycleId = currentCycleId;
        this.IsViolation = isViolation;
        this.CreatedDate = createdDate;
        this.UTCStartDateTime = UTCStartDateTime;
        this.UTCEndDateTime = UTCEndDateTime;
    }

    public int getDriverLogId() {
        return DriverLogId;
    }

    public int getDriverId() {
        return DriverId;
    }

    public int getProjectId() {
        return ProjectId;
    }

    public int getDriverStatusId() {
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

    public double getStartLatitude() {
        return StartLatitude;
    }

    public double getStartLongitude() {
        return StartLongitude;
    }

    public double getEndLatitude() {
        return EndLatitude;
    }

    public double getEndLongitude() {
        return EndLongitude;
    }

    public int getCurrentCycleId() {
        return CurrentCycleId;
    }

    public boolean isViolation() {
        return IsViolation;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public String getUTCStartDateTime() {
        return UTCStartDateTime;
    }

    public String getUTCEndDateTime() {
        return UTCEndDateTime;
    }

}
