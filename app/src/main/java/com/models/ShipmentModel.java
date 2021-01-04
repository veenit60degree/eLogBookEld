package com.models;


public class ShipmentModel implements Comparable{

    int ParcableId;
    String DriverId;
    String CoDriverId;
    String DeviceId;
    String Date;
    String BlNoTripNo;
    String Commodity;
    String ShipperName;
    String FromAddress;
    String ToAddress;
    String SavedDate;
    boolean isPosted;
    boolean IsShippingCleared;



    public ShipmentModel(int parcableId, String driverId, String coDriverId, String deviceId,
                         String date, String blNoTripNo, String commodity,
                         String shipperName, String fromAddress, String toAddress, String savedDate,
                         boolean posted, boolean isUnloading) {

        ParcableId = parcableId;
        DriverId = driverId;
        CoDriverId = coDriverId;
        DeviceId = deviceId;
        Date = date;
        BlNoTripNo = blNoTripNo;
        Commodity   = commodity;
        ShipperName = shipperName;
        FromAddress = fromAddress;
        ToAddress = toAddress;
        SavedDate = savedDate;
        isPosted = posted;
        IsShippingCleared = isUnloading;

    }


    public int getParcableId() {
        return ParcableId;
    }

    public String getDriverId() {
        return DriverId;
    }

    public String getCoDriverId() {
        return CoDriverId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public String getDate() {
        return Date;
    }

    public String getBlNoTripNo() {
        return BlNoTripNo;
    }

    public String getCommodity() {
        return Commodity;
    }

    public String getShipperName() {
        return ShipperName;
    }

    public String getFromAddress() {
        return FromAddress;
    }

    public String getToAddress() {
        return ToAddress;
    }

    public String getSavedDate() {
        return SavedDate;
    }

    public boolean isPosted() {
        return isPosted;
    }

    public boolean isUnloading() {
        return IsShippingCleared;
    }

    @Override
    public int compareTo(Object o) {
        int compareId=((ShipmentModel)o).getParcableId();

        /* For Ascending order*/
        // return this.Id-compareId;


        /* For Descending order  */
        return compareId-this.ParcableId;
    }
}
