package com.models;


public class RecapViewModel {

    String CoDriverName;
    String CoDriverId;
    String EngineMileage;
    String Trailor;
    String Truck;
    String LogSignImage;
    String LogSignImageInByte;
    String Date;
    String CycleDaysList;

    public RecapViewModel(String coDriverName, String coDriverId, String engineMileage,
                          String trailor, String truck, String logSignImage, String logSignImageInByte,
                          String date, String cycleDaysList) {

        CoDriverName        = coDriverName;
        CoDriverId          = coDriverId;
        EngineMileage       = engineMileage;
        Trailor             = trailor;
        Truck               = truck;
        LogSignImage        = logSignImage;
        LogSignImageInByte  = logSignImageInByte;
        CycleDaysList       = cycleDaysList;
        Date                = date;
    }


    public String getCoDriverName() {
        return CoDriverName;
    }

    public String getCoDriverId() {
        return CoDriverId;
    }

    public String getEngineMileage() {
        return EngineMileage;
    }

    public String getTrailor() {
        return Trailor;
    }

    public String getTruck() {
        return Truck;
    }

    public String getLogSignImage() {
        return LogSignImage;
    }

    public String getLogSignImageInByte() {
        return LogSignImageInByte;
    }


    public String getDate() {
        return Date;
    }


    public String getCycleDaysList() {
        return CycleDaysList;
    }

}
