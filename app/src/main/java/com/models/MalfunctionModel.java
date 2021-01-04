package com.models;

public class MalfunctionModel {

    String Country;
    String VIN;
    String CompanyId;
    String EventDateTime;
    String EngineHours;
    String Miles;
    String DetectionDataEventCode;
    String MasterDetectionDataEventId;
    String EventCode;
    String Reason;
    String MalfunctionDefinition;
    String FromDateTime;
    String ToDateTime;
    String DriverZoneEventDate;
    String SequenceNo;
    String Id;

    public MalfunctionModel(String country, String VIN, String companyId, String eventDateTime, String engineHours, String miles,
                            String detectionDataEventCode, String masterDetectionDataEventId, String eventCode, String reason, String malfunctionDefinition,
                            String fromDateTime, String toDateTime, String driverZoneEventDate, String sequenceNo,String id) {
        Country = country;
        this.VIN = VIN;
        CompanyId = companyId;
        EventDateTime = eventDateTime;
        EngineHours = engineHours;
        Miles = miles;
        DetectionDataEventCode = detectionDataEventCode;
        MasterDetectionDataEventId = masterDetectionDataEventId;
        EventCode = eventCode;
        Reason = reason;
        MalfunctionDefinition = malfunctionDefinition;
        FromDateTime = fromDateTime;
        ToDateTime = toDateTime;
        DriverZoneEventDate = driverZoneEventDate;
        SequenceNo          = sequenceNo;
        Id                  = id;
    }

    public String getCountry() {
        return Country;
    }

    public String getVIN() {
        return VIN;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public String getEventDateTime() {
        return EventDateTime;
    }

    public String getEngineHours() {
        return EngineHours;
    }

    public String getMiles() {
        return Miles;
    }

    public String getDetectionDataEventCode() {
        return DetectionDataEventCode;
    }

    public String getMasterDetectionDataEventId() {
        return MasterDetectionDataEventId;
    }

    public String getEventCode() {
        return EventCode;
    }

    public String getReason() {
        return Reason;
    }

    public String getMalfunctionDefinition() {
        return MalfunctionDefinition;
    }

    public String getFromDateTime() {
        return FromDateTime;
    }

    public String getToDateTime() {
        return ToDateTime;
    }

    public String getDriverZoneEventDate() {
        return DriverZoneEventDate;
    }

    public String getSequenceNo() {
        return SequenceNo;
    }

    public String getId() {
        return Id;
    }
}
