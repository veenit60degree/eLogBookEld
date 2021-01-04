package com.background.service;

/**
 * Created by Veenit on 12/29/2016.
 */

public class LocationModel {

    String Latitude;
    String Longitude;
    String VehicleSpeed;
    String TruckDateTime;


    public LocationModel(String latitude, String longitude, String vehicleSpeed, String truckDateTime) {
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.VehicleSpeed = vehicleSpeed;
        this.TruckDateTime = truckDateTime;
    }


    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getVehicleSpeed() {
        return VehicleSpeed;
    }

    public void setVehicleSpeed(String vehicleSpeed) {
        VehicleSpeed = vehicleSpeed;
    }

    public String getTruckDateTime() {
        return TruckDateTime;
    }

    public void setTruckDateTime(String truckDateTime) {
        TruckDateTime = truckDateTime;
    }
}
