package com.driver.details;

/**
 * Created by kumar on 9/7/2017.
 */

public class TimeZoneModel {

    String TimeZoneID;
    String TimeZone;
    String UTC;
    String TimeZoneName;
    String TimeZoneCity;


    public TimeZoneModel(String timeZoneID, String timeZone, String UTC, String timeZoneName, String timeZoneCity) {
        TimeZoneID = timeZoneID;
        TimeZone = timeZone;
        this.UTC = UTC;
        TimeZoneName = timeZoneName;
        TimeZoneCity = timeZoneCity;
    }


    public String getTimeZoneID() {
        return TimeZoneID;
    }

    public String getTimeZone() {
        return TimeZone;
    }

    public String getUTC() {
        return UTC;
    }

    public String getTimeZoneName() {
        return TimeZoneName;
    }

    public String getTimeZoneCity() {
        return TimeZoneCity;
    }
}
