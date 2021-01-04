package com.models;

import com.messaging.logistic.Globally;
import com.messaging.logistic.fragment.EldFragment;

import java.util.ArrayList;

public class SavedInspectionModel {

    String HeaderTitle;
    String VIN = "";
    String VehicleEquNumber = "";
    String TrailorEquNumber = "";
    String InspectionDateTime = "";
    String Location = "";
    boolean PreTripInspectionSatisfactory ;
    boolean PostTripInspectionSatisfactory ;
    boolean AboveDefectsCorrected ;
    boolean AboveDefectsNotCorrected ;
    String Remarks = "";
    String DriverSignature = "";
    String SupervisorMechanicsName = "";
    String SupervisorMechanicsSignature = "";
    String CreatedDate ;
    int InspectionTypeId;
    String DriverImageBytes ;
    String SupervisorImageByte ;

    ArrayList<String> TruckList;
    ArrayList<String> TrailerList;


    public SavedInspectionModel(String headerTitle, String VIN, String vehicleEquNumber, String trailorEquNumber, String inspectionDateTime,
                                String location, boolean preTripInspectionSatisfactory, boolean postTripInspectionSatisfactory,
                                boolean aboveDefectsCorrected, boolean aboveDefectsNotCorrected, String remarks, String driverSignature,
                                String supervisorMechanicsName, String supervisorMechanicsSignature, String createdDate,
                                int inspectionTypeId, String driverImageBytes , String supervisorImageByte, ArrayList<String> truckList, ArrayList<String> trailerList) {

        this.HeaderTitle = headerTitle;
        this.VIN = VIN;
        VehicleEquNumber = vehicleEquNumber;
        TrailorEquNumber = trailorEquNumber;
        InspectionDateTime = inspectionDateTime;
        Location = location;
        PreTripInspectionSatisfactory = preTripInspectionSatisfactory;
        PostTripInspectionSatisfactory = postTripInspectionSatisfactory;
        AboveDefectsCorrected = aboveDefectsCorrected;
        AboveDefectsNotCorrected = aboveDefectsNotCorrected;
        Remarks = remarks;
        DriverSignature = driverSignature;
        SupervisorMechanicsName = supervisorMechanicsName;
        SupervisorMechanicsSignature = supervisorMechanicsSignature;
        CreatedDate = createdDate;
        InspectionTypeId = inspectionTypeId;
        DriverImageBytes = driverImageBytes;
        SupervisorImageByte = supervisorImageByte;
        TruckList = truckList;
        TrailerList = trailerList;
    }



    public String getHeaderTitle() {
        return HeaderTitle;
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

    public String getLocation() {
        return Location;
    }

    public boolean IsPreTripInspectionSatisfactory() {
        return PreTripInspectionSatisfactory;
    }

    public boolean IsPostTripInspectionSatisfactory() {
        return PostTripInspectionSatisfactory;
    }

    public boolean IsAboveDefectsCorrected() {
        return AboveDefectsCorrected;
    }

    public boolean IsAboveDefectsNotCorrected() {
        return AboveDefectsNotCorrected;
    }

    public String getRemarks() {
        return Remarks;
    }

    public String getDriverSignature() {
        return DriverSignature;
    }

    public String getSupervisorMechanicsName() {
        return SupervisorMechanicsName;
    }

    public String getSupervisorMechanicsSignature() {
        return SupervisorMechanicsSignature;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public int getInspectionTypeId() {
        return InspectionTypeId;
    }

    public boolean isPreTripInspectionSatisfactory() {
        return PreTripInspectionSatisfactory;
    }

    public boolean isPostTripInspectionSatisfactory() {
        return PostTripInspectionSatisfactory;
    }

    public boolean isAboveDefectsCorrected() {
        return AboveDefectsCorrected;
    }

    public boolean isAboveDefectsNotCorrected() {
        return AboveDefectsNotCorrected;
    }

    public String getDriverImageBytes() {
        return DriverImageBytes;
    }

    public String getSupervisorImageByte() {
        return SupervisorImageByte;
    }

    public ArrayList<String> getTruckList() {
        return TruckList;
    }

    public ArrayList<String> getTrailerList() {
        return TrailerList;
    }
}
