package com.models;

public class DriverLogRecordModel {

    String DriverId;
    String DeviceId;
    String DriverStatusId;
    String StartDateTime;
    String RecordType;
    String RecordValue;


    public DriverLogRecordModel(String driverId, String deviceId, String driverStatusId,
                                    String startDateTime, String recordType, String recordValue) {
        DriverId = driverId;
        DeviceId = deviceId;
        DriverStatusId = driverStatusId;
        StartDateTime = startDateTime;
        RecordType = recordType;
        RecordValue = recordValue;
    }


    public String getDriverId() {
        return DriverId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public String getDriverStatusId() {
        return DriverStatusId;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public String getRecordType() {
        return RecordType;
    }

    public String getRecordValue() {
        return RecordValue;
    }
}
