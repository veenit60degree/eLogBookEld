package com.models;

import com.local.db.ConstantsKeys;

import org.joda.time.DateTime;

public class DriverLogModel {

    private long driverLogId;
    private long driverId;
    private int projectId;
    private int driverStatusId;
    private DateTime startDateTime;
    private DateTime endDateTime;
    private DateTime utcStartDateTime;
    private DateTime utcEndDateTime;
    private double totalMinutes;
    private String startLatitude;
    private String startLongitude;
    private String endLatitude;
    private String endLongitude;
    private boolean yardMove;
    private boolean personal;
    private int currentCyleId;
    private boolean isViolation;
    private String violationReason;
    private DateTime createdDate;
    private String DriverName;
    private String Remarks;
    private String Trailor;
    private String StartLocation;
    private String EndLocation;
    private String StartLocationKm;
    private String Truck;

    private String IsStatusAutomatic;
    private String OBDSpeed ;
    private String GPSSpeed ;
    private String PlateNumber;

    private boolean isHaulException ;
    private boolean isHaulExceptionUpdate ;
    private String DecesionSource;

    private boolean isAdverseException ;
    private String adverseExceptionRemark;
    private String LocationType;
    private String MalfunctionDefinition;
    private boolean IsNorthCanada;
    private boolean isNewRecord;
    private boolean IsCycleChanged;
    private String StartOdometerInKm;
    private String EndOdometerInKm;
    private String CoDriverId;

    public DriverLogModel() {
    }

    public DriverLogModel(DriverLogModel logModel) {
        this.driverLogId = logModel.getDriverLogId();
        this.driverId = logModel.getDriverId();
        this.projectId = logModel.getProjectId();
        this.driverStatusId = logModel.getDriverStatusId();
        this.startDateTime = logModel.getStartDateTime();
        this.endDateTime = logModel.getEndDateTime();
        this.utcStartDateTime = logModel.getUtcStartDateTime();
        this.utcEndDateTime = logModel.getUtcEndDateTime();
        this.totalMinutes = logModel.getTotalMinutes();
        this.startLatitude = logModel.getStartLatitude();
        this.startLongitude = logModel.getStartLongitude();
        this.endLatitude = logModel.getEndLatitude();
        this.endLongitude = logModel.getEndLongitude();
        this.yardMove = logModel.isYardMove();
        this.personal = logModel.isPersonal();
        this.currentCyleId = logModel.getCurrentCyleId();
        this.isViolation = logModel.isViolation();
        this.violationReason = logModel.getViolationReason();
        this.createdDate = logModel.getCreatedDate();
        this.DriverName = logModel.getDriverName();
        this.Remarks = logModel.getRemarks();
        this.Trailor = logModel.getTrailor();
        this.StartLocation = logModel.getStartLocation();
        this.EndLocation = logModel.getEndLocation();
        this.StartLocationKm = logModel.getStartLocationKm();
        this.Truck = logModel.getTruck();
        this.IsStatusAutomatic = logModel.getIsStatusAutomatic();
        this.OBDSpeed = logModel.getOBDSpeed();
        this.GPSSpeed = logModel.getGPSSpeed();
        this.PlateNumber = logModel.getPlateNumber();
        this.isHaulException = logModel.getIsHaulException();
        this.isHaulExceptionUpdate = logModel.getHaulExceptionUpdate();
        this.DecesionSource  = logModel.getDecesionSource();
        this.isAdverseException = logModel.getIsAdverseException();
        this.adverseExceptionRemark  = logModel.getAdverseExceptionRemark();
        this.LocationType = logModel.getLocationType();
        this.MalfunctionDefinition = logModel.getMalfunctionDefinition();
        this.IsNorthCanada = logModel.IsNorthCanada();
        this.isNewRecord    = logModel.IsNewRecord();
        this.IsCycleChanged = logModel.IsCycleChanged();
        this.StartOdometerInKm = logModel.getStartOdometerInKm();
        this.EndOdometerInKm = logModel.getEndOdometerInKm();
        this.CoDriverId = logModel.getCoDriverId();

    }

    public void setDriverLogId(long driverLogId) {
        this.driverLogId = driverLogId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setDriverStatusId(int driverStatusId) {
        this.driverStatusId = driverStatusId;
    }

    public void setStartDateTime(DateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(DateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setUtcStartDateTime(DateTime utcStartDateTime) {
        this.utcStartDateTime = utcStartDateTime;
    }

    public void setUtcEndDateTime(DateTime utcEndDateTime) {
        this.utcEndDateTime = utcEndDateTime;
    }

    public void setTotalMinutes(double totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public void setStartLatitude(String startLatitude) {
        this.startLatitude = startLatitude;
    }

    public void setStartLongitude(String startLongitude) {
        this.startLongitude = startLongitude;
    }

    public void setEndLatitude(String endLatitude) {
        this.endLatitude = endLatitude;
    }

    public void setEndLongitude(String endLongitude) {
        this.endLongitude = endLongitude;
    }

    public void setYardMove(boolean yardMove) {
        this.yardMove = yardMove;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
    }

    public void setCurrentCyleId(int currentCyleId) {
        this.currentCyleId = currentCyleId;
    }

    public void setViolation(boolean violation) {
        isViolation = violation;
    }

    public void setViolationReason(String violationReason) {
        this.violationReason = violationReason;
    }



    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public void setTrailor(String trailor) {
        Trailor = trailor;
    }

    public void setStartLocation(String startLocation) {
        StartLocation = startLocation;
    }

    public void setStartLocationKm(String startLocationkm) {
        StartLocationKm = startLocationkm;
    }


    public void setEndLocation(String endLocation) {
        EndLocation = endLocation;
    }

    public void setTruck(String truck) {
        Truck = truck;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public long getDriverLogId() {
        return driverLogId;
    }

    public long getDriverId() {
        return driverId;
    }

    public int getProjectId() {
        return projectId;
    }

    public int getDriverStatusId() {
        return driverStatusId;
    }

    public DateTime getStartDateTime() {
        return startDateTime;
    }

    public DateTime getEndDateTime() {
        return endDateTime;
    }

    public DateTime getUtcStartDateTime() {
        return utcStartDateTime;
    }

    public DateTime getUtcEndDateTime() {
        return utcEndDateTime;
    }

    public double getTotalMinutes() {
        return totalMinutes;
    }

    public String getStartLatitude() {
        return startLatitude;
    }

    public String getStartLongitude() {
        return startLongitude;
    }

    public String getEndLatitude() {
        return endLatitude;
    }

    public String getEndLongitude() {
        return endLongitude;
    }

    public boolean isYardMove() {
        return yardMove;
    }

    public boolean isPersonal() {
        return personal;
    }

    public int getCurrentCyleId() {
        return currentCyleId;
    }

    public boolean isViolation() {
        return isViolation;
    }

    public String getViolationReason() {
        return violationReason;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public String getDriverName() {
        return DriverName;
    }

    public String getRemarks() {
        return Remarks;
    }

    public String getTrailor() {
        return Trailor;
    }

    public String getStartLocation() {
        return StartLocation;
    }

    public String getStartLocationKm() {
        return StartLocationKm;
    }


    public String getEndLocation() {
        return EndLocation;
    }

    public String getTruck() {
        return Truck;
    }

    public String getIsStatusAutomatic() {
        return IsStatusAutomatic;
    }

    public void setIsStatusAutomatic(String isStatusAutomatic) {
        IsStatusAutomatic = isStatusAutomatic;
    }

    public String getOBDSpeed() {
        return OBDSpeed;
    }

    public void setOBDSpeed(String OBDSpeed) {
        this.OBDSpeed = OBDSpeed;
    }

    public String getGPSSpeed() {
        return GPSSpeed;
    }

    public String getPlateNumber() {
        return PlateNumber;
    }


    public void setPlateNumber(String plateNumber) {
        this.PlateNumber = plateNumber;
    }


    public void setGPSSpeed(String GPSSpeed) {
        this.GPSSpeed = GPSSpeed;
    }

    public boolean getIsHaulException() {
        return isHaulException;
    }


    public void setHaulException(boolean haulException) {
        isHaulException = haulException;
    }


    public void setHaulExceptionUpdate(boolean haulExceptionUpdate) {
        isHaulExceptionUpdate = haulExceptionUpdate;
    }


    public boolean getHaulExceptionUpdate() {
        return isHaulExceptionUpdate;
    }





    public String getDecesionSource() {
        return DecesionSource;
    }


    public void setDecesionSource(String decesionSource) {
        this.DecesionSource = decesionSource;
    }



    public boolean getIsAdverseException() {
        return isAdverseException;
    }


    public void setAdverseException(boolean adverseException) {
        isAdverseException = adverseException;
    }



    public String getAdverseExceptionRemark() {
        return adverseExceptionRemark;
    }


    public void setAdverseExceptionRemark(String adverseExcRemark) {
        this.adverseExceptionRemark = adverseExcRemark;
    }


    public String getLocationType() {
        return LocationType;
    }
    public void setLocationType(String LocationType) {
        this.LocationType = LocationType;
    }



    public String getMalfunctionDefinition() {
        return MalfunctionDefinition;
    }
    public void setMalfunctionDefinition(String MalfunctionDefinition) {
        this.MalfunctionDefinition = MalfunctionDefinition;
    }


    public boolean IsNorthCanada() {
        return IsNorthCanada;
    }


    public void setNorthCanadaStatus(boolean isNorthCanada) {
        IsNorthCanada = isNorthCanada;
    }

    public boolean IsNewRecord() {
        return isNewRecord;
    }

    public void setNewRecordStatus(boolean IsNewRecord) {
        isNewRecord = IsNewRecord;
    }

    public boolean IsCycleChanged() {
        return IsCycleChanged;
    }

    public void setCycleChanged(boolean cycleChanged) {
        IsCycleChanged = cycleChanged;
    }

    public String getStartOdometerInKm() {
        return StartOdometerInKm;
    }

    public void setStartOdometerInKm(String startOdometerInKm) {
        StartOdometerInKm = startOdometerInKm;
    }

    public String getEndOdometerInKm() {
        return EndOdometerInKm;
    }

    public void setEndOdometerInKm(String endOdometerInKm) {
        EndOdometerInKm = endOdometerInKm;
    }

    public String getCoDriverId() {
        return CoDriverId;
    }

    public void setCoDriverId(String coDriverId) {
        CoDriverId = coDriverId;
    }



}
