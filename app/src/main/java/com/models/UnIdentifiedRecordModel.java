package com.models;

public class UnIdentifiedRecordModel {

    String EquipmentNumber;
    String VIN;
    String StartOdometer;
    String EndOdometer;
    String TotalMiles;
    String TotalKm;
    String StartDateTime;
    String EndDateTime;
    String StartLatitude;
    String StartLongitude;
    String EndLatitude;
    String EndLongitude;
    String StartLocation;
    String EndLocation;
    String DriverStatusId;
    String Remarks;
    String StatusId;

    String UnAssignedVehicleMilesId;
    String AssignedUnidentifiedRecordsId;
    boolean IsCompanyAssigned;

    public UnIdentifiedRecordModel(String equipmentNumber, String VIN, String startOdometer,
                                   String endOdometer, String totalMiles, String totalKm, String startDateTime,
                                   String endDateTime, String startLatitude, String startLongitude, String endLatitude,
                                   String endLongitude, String startLocation, String endLocation, String driverStatusId,
                                   String remarks, String statusId, String unAssignedVehicleMilesId,
                                   String assignedUnidentifiedRecordsId, boolean isCompanyAssigned) {

        EquipmentNumber = equipmentNumber;
        this.VIN = VIN;
        StartOdometer = startOdometer;
        EndOdometer = endOdometer;
        TotalMiles = totalMiles;
        TotalKm = totalKm;
        StartDateTime = startDateTime;
        EndDateTime = endDateTime;
        StartLatitude = startLatitude;
        StartLongitude = startLongitude;
        EndLatitude = endLatitude;
        EndLongitude = endLongitude;
        StartLocation = startLocation;
        EndLocation = endLocation;
        DriverStatusId = driverStatusId;
        Remarks = remarks;
        StatusId = statusId;
        UnAssignedVehicleMilesId = unAssignedVehicleMilesId;
        AssignedUnidentifiedRecordsId = assignedUnidentifiedRecordsId;
        IsCompanyAssigned = isCompanyAssigned;

    }

    public String getEquipmentNumber() {
        return EquipmentNumber;
    }

    public String getVIN() {
        return VIN;
    }

    public String getStartOdometer() {
        return StartOdometer;
    }

    public String getEndOdometer() {
        return EndOdometer;
    }

    public String getTotalMiles() {
        return TotalMiles;
    }

    public String getTotalKm() {
        return TotalKm;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public String getStartLatitude() {
        return StartLatitude;
    }

    public String getStartLongitude() {
        return StartLongitude;
    }

    public String getEndLatitude() {
        return EndLatitude;
    }

    public String getEndLongitude() {
        return EndLongitude;
    }

    public String getStartLocation() {
        return StartLocation;
    }

    public String getEndLocation() {
        return EndLocation;
    }

    public String getDriverStatusId() {
        return DriverStatusId;
    }

    public String getRemarks() {
        return Remarks;
    }

    public String getStatusId() {
        return StatusId;
    }

    public String getUnAssignedVehicleMilesId() {
        return UnAssignedVehicleMilesId;
    }

    public String getAssignedUnidentifiedRecordsId() {
        return AssignedUnidentifiedRecordsId;
    }

    public boolean isCompanyAssigned() {
        return IsCompanyAssigned;
    }
}
