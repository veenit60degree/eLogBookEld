package com.models;

import java.util.Date;

public class UnAssignedVehicleModel implements Comparable<UnAssignedVehicleModel>{

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
    String StartLocation;
    String EndLocation;
    String StartLatitude;
    String StartLongitude;
    String DutyStatus;
    Date dateTime;

    public UnAssignedVehicleModel(String unAssignedVehicleMilesId, String assignedUnidentifiedRecordsId, String equipmentNumber, String VIN, String startOdometer,
                                  String endOdometer, String totalMiles, String totalKm, String driverZoneStartDateTime, String driverZoneEndDateTime, String statusId,
                                  boolean isIntermediateLog, String hexaSeqNumber, String startLoc, String endLoc,
                                  String startLatitude, String startLongitude, String dutyStatus, Date date) {
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
        StartLocation = startLoc;
        EndLocation = endLoc;
        StartLatitude = startLatitude;
        StartLongitude = startLongitude;
        DutyStatus = dutyStatus;
        dateTime = date;

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

    public String getStartLocation() {
        return StartLocation;
    }

    public String getEndLocation() {
        return EndLocation;
    }

    public String getDutyStatus() {
        return DutyStatus;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }


    public String getStartLatitude() {
        return StartLatitude;
    }

    public String getStartLongitude() {
        return StartLongitude;
    }

    @Override
    public int compareTo(UnAssignedVehicleModel unAssignedVehicleModel) {
        if(getDateTime().equals(unAssignedVehicleModel.getDateTime())){
            return getHexaSeqNumber().compareTo(unAssignedVehicleModel.getHexaSeqNumber());
        }else{
            return getDateTime().compareTo(unAssignedVehicleModel.getDateTime());
        }
    }


}
