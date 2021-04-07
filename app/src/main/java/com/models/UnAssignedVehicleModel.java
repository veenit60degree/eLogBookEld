package com.models;

public class UnAssignedVehicleModel {

    String UnAssignedVehicleMilesId;
    String AssignedUnidentifiedRecordsId;
    String EquipmentNumber;
    String VIN;
    String StartOdometer;
    String EndOdometer;
    String TotalMiles;
    String TotalKm;
    String DriverZoneStartDateTime;
    String DriverZoneEndDateTime;
    String StatusId;
    boolean IsIntermediateLog;
    String HexaSeqNumber;

    public UnAssignedVehicleModel(String unAssignedVehicleMilesId, String assignedUnidentifiedRecordsId, String equipmentNumber, String VIN, String startOdometer,
                                  String endOdometer, String totalMiles, String totalKm, String driverZoneStartDateTime, String driverZoneEndDateTime, String statusId,
                                  boolean isIntermediateLog, String hexaSeqNumber) {
        UnAssignedVehicleMilesId = unAssignedVehicleMilesId;
        AssignedUnidentifiedRecordsId = assignedUnidentifiedRecordsId;
        EquipmentNumber = equipmentNumber;
        this.VIN = VIN;
        StartOdometer = startOdometer;
        EndOdometer = endOdometer;
        TotalMiles = totalMiles;
        TotalKm = totalKm;
        DriverZoneStartDateTime = driverZoneStartDateTime;
        DriverZoneEndDateTime = driverZoneEndDateTime;
        StatusId = statusId;
        IsIntermediateLog = isIntermediateLog;
        HexaSeqNumber = hexaSeqNumber;
    }

    public String getUnAssignedVehicleMilesId() {
        return UnAssignedVehicleMilesId;
    }

    public String getAssignedUnidentifiedRecordsId() {
        return AssignedUnidentifiedRecordsId;
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

    public String getDriverZoneStartDateTime() {
        return DriverZoneStartDateTime;
    }

    public String getDriverZoneEndDateTime() {
        return DriverZoneEndDateTime;
    }

    public String getStatusId() {
        return StatusId;
    }

    public boolean isIntermediateLog() {
        return IsIntermediateLog;
    }

    public String getHexaSeqNumber() {
        return HexaSeqNumber;
    }
}
