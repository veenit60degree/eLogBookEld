package com.models;


public class OdometerModel {

    String TruckOdometerId;
    String DriverId;
    String VIN;
    String StartOdometer;
    String EndOdometer;
    String TotalMiles;
    String TotalKM;
    String DistanceType;
    String CreatedDate;
    String TruckEquipmentNumber;
    String DriverStatusID;
    boolean IsPersonal;


    public OdometerModel(String truckOdometerId, String driverId, String VIN,
                         String startOdometer, String endOdometer, String totalMiles,
                         String totalKM, String distanceType, String createdDate,
                         String truckEquipmentNumber, String driverStatusID, boolean isPersonal) {
        TruckOdometerId = truckOdometerId;
        DriverId = driverId;
        this.VIN = VIN;
        StartOdometer = startOdometer;
        EndOdometer = endOdometer;
        TotalMiles = totalMiles;
        TotalKM = totalKM;
        DistanceType = distanceType;
        CreatedDate = createdDate;
        TruckEquipmentNumber    = truckEquipmentNumber;
        DriverStatusID  = driverStatusID;
        IsPersonal = isPersonal;
    }

    public String getTruckOdometerId() {
        return TruckOdometerId;
    }

    public String getDriverId() {
        return DriverId;
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

    public String getTotalKM() {
        return TotalKM;
    }

    public String getDistanceType() {
        return DistanceType;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public String getTruckEquipmentNumber() {
        return TruckEquipmentNumber;
    }

    public String getDriverStatusID() {
        return DriverStatusID;
    }

    public boolean isPersonal() {
        return IsPersonal;
    }
}
