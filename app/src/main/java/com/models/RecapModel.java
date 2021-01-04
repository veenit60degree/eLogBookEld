package com.models;


public class RecapModel {

    String Day;
    String Date;
    String HourWorked;

    public RecapModel(String day, String date, String hourWorked) {
        Day = day;
        Date = date;
        HourWorked = hourWorked;
    }

    public String getDay() {
        return Day;
    }

    public String getDate() {
        return Date;
    }

    public String getHourWorked() {
        return HourWorked;
    }
}
