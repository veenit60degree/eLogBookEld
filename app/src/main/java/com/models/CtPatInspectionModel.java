package com.models;

public class CtPatInspectionModel {

    String DriverId ;
    String DeviceId ;
    String ProjectId ;
    String DriverName ;

    String CompanyId ;
    String VehicleId ;
    String VIN ;
    String VehicleEquNumber ;
    String TrailorEquNumber ;
    String InspectionDateTime ;

    String ArrivalSealNumber ;
    String DepartureSealNumber ;

    String SecurityInspectionPersonName ;
    String FollowUpInspectionPersonName ;
    String AffixedSealPersonName ;
    String VerificationPersonName ;

    String Latitude ;
    String Longitude ;

    String TruckIssueType ;
    String TraiorIssueType ;

    String ByteInspectionConductorSign ;
    String ByteFollowUpConductorSign ;
    String ByteSealFixerSign ;
    String ByteSealVerifierSign ;
    String AgricultureIssueType ;
    String AgricultureReason ;
    String ContainerIdentification;


    public CtPatInspectionModel(String driverId, String deviceId, String projectId, String driverName, String companyId, String vehicleId,
                                String VIN, String vehicleEquNumber, String trailorEquNumber, String inspectionDateTime, String arrivalSealNumber,
                                String departureSealNumber, String securityInspectionPersonName, String followUpInspectionPersonName, String affixedSealPersonName,
                                String verificationPersonName, String latitude, String longitude, String truckIssueType, String traiorIssueType, String byteInspectionConductorSign,
                                String byteFollowUpConductorSign, String byteSealFixerSign, String byteSealVerifierSign,String agricultureIssueType,String agricultureReason,String containerIdentification) {
        DriverId = driverId;
        DeviceId = deviceId;
        ProjectId = projectId;
        DriverName = driverName;
        CompanyId = companyId;
        VehicleId = vehicleId;
        this.VIN = VIN;
        VehicleEquNumber = vehicleEquNumber;
        TrailorEquNumber = trailorEquNumber;
        InspectionDateTime = inspectionDateTime;
        ArrivalSealNumber = arrivalSealNumber;
        DepartureSealNumber = departureSealNumber;
        SecurityInspectionPersonName = securityInspectionPersonName;
        FollowUpInspectionPersonName = followUpInspectionPersonName;
        AffixedSealPersonName = affixedSealPersonName;
        VerificationPersonName = verificationPersonName;
        Latitude = latitude;
        Longitude = longitude;
        TruckIssueType = truckIssueType;
        TraiorIssueType = traiorIssueType;
        ByteInspectionConductorSign = byteInspectionConductorSign;
        ByteFollowUpConductorSign = byteFollowUpConductorSign;
        ByteSealFixerSign = byteSealFixerSign;
        ByteSealVerifierSign = byteSealVerifierSign;
        AgricultureIssueType = agricultureIssueType;
        AgricultureReason    = agricultureReason;
        ContainerIdentification = containerIdentification;
    }

    public String getDriverId() {
        return DriverId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public String getProjectId() {
        return ProjectId;
    }

    public String getDriverName() {
        return DriverName;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public String getVehicleId() {
        return VehicleId;
    }

    public String getVIN() {
        return VIN;
    }

    public String getVehicleEquNumber() {
        return VehicleEquNumber;
    }

    public String getTrailorEquNumber() {
        return TrailorEquNumber;
    }

    public String getInspectionDateTime() {
        return InspectionDateTime;
    }

    public String getArrivalSealNumber() {
        return ArrivalSealNumber;
    }

    public String getDepartureSealNumber() {
        return DepartureSealNumber;
    }

    public String getSecurityInspectionPersonName() {
        return SecurityInspectionPersonName;
    }

    public String getFollowUpInspectionPersonName() {
        return FollowUpInspectionPersonName;
    }

    public String getAffixedSealPersonName() {
        return AffixedSealPersonName;
    }

    public String getVerificationPersonName() {
        return VerificationPersonName;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public String getTruckIssueType() {
        return TruckIssueType;
    }

    public String getTraiorIssueType() {
        return TraiorIssueType;
    }

    public String getByteInspectionConductorSign() {
        return ByteInspectionConductorSign;
    }

    public String getByteFollowUpConductorSign() {
        return ByteFollowUpConductorSign;
    }

    public String getByteSealFixerSign() {
        return ByteSealFixerSign;
    }

    public String getByteSealVerifierSign() {
        return ByteSealVerifierSign;
    }

    public String getAgricultureIssueType() {
        return AgricultureIssueType;
    }

    public String getAgricultureReason() {
        return AgricultureReason;
    }

    public String getContainerIdentificationReason() {
        return ContainerIdentification;
    }


}
