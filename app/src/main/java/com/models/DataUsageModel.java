package com.models;

public class DataUsageModel {

    public String DriverId;
    public String DriverName;
    public String AlsSendingData;
    public String AlsReceivedData ;
    public String MobileUsage ;
    public String TotalUsage ;
    public String Date ;

    public DataUsageModel() {
    }

    public DataUsageModel(String DriverId, String name, String AlsSendingData,
                         String AlsReceivedData, String MobileUsage,
                         String TotalUsage, String Date) {

        this.DriverId = DriverId;
        this.DriverName = name;
        this.AlsSendingData = AlsSendingData;

        this.AlsReceivedData = AlsReceivedData;
        this.MobileUsage = MobileUsage;

        this.TotalUsage = TotalUsage;
        this.Date = Date;

    }
}
