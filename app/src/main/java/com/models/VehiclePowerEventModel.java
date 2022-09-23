package com.models;

public class VehiclePowerEventModel {

    String DriverId;
    String EquipmentNumber;
    String VIN;
    String VehicleId;
    String CompanyId;

    String Odometer;
    String EngineHour;


    public VehiclePowerEventModel(String driverId, String equipmentNumber, String VIN, String VehicleId,
                                  String companyId, String odometer, String engineHour) {
        DriverId = driverId;
        EquipmentNumber = equipmentNumber;
        this.VIN = VIN;
        this.VehicleId = VehicleId;
        CompanyId = companyId;
        Odometer = odometer;
        EngineHour = engineHour;
    }

    public String getDriverId() {
        return DriverId;
    }

    public String getEquipmentNumber() {
        return EquipmentNumber;
    }

    public String getVIN() {
        return VIN;
    }

    public String getVehicleId() {
        return VehicleId;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public String getOdometer() {
        return Odometer;
    }

    public String getEngineHour() {
        return EngineHour;
    }
}
