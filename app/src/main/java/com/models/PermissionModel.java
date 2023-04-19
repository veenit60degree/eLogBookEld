package com.models;

public class PermissionModel {

    String PermissionType ;
    String PermissionDesc;
    boolean PermissionStatus;
    int LocationType;

    public PermissionModel( String PermissionType, String PermissionDesc, boolean PermissionStatus, int LocationType) {
        this.PermissionType = PermissionType;
        this.PermissionDesc     = PermissionDesc;
        this.PermissionStatus = PermissionStatus;
        this.LocationType = LocationType;
    }

    public String getPermissionType() {
        return PermissionType;
    }

    public String getPermissionDesc() {
        return PermissionDesc;
    }

    public boolean IsPermissionGranted() {
        return PermissionStatus;
    }

    public int getLocationType() {
        return LocationType;
    }


}
