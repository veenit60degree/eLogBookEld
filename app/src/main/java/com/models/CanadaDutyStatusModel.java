package com.models;

import java.util.Date;

public class CanadaDutyStatusModel implements Comparable<CanadaDutyStatusModel>{

            String DateTimeWithMins;
            String EventUTCTimeStamp;
            String DriverStatusID;

            int EventType;
            int EventCode;
            String DutyMinutes;

            String Annotation;
            String EventDate;
            String EventTime;
            String AccumulatedVehicleMiles;
            String AccumulatedEngineHours;
            String TotalVehicleMiles;
            String TotalEngineHours;
            String GPSLatitude;
            String GPSLongitude;
            String CMVVIN;
            String CarrierName;

            boolean IsMalfunction;

            String OdometerInKm;
            String strEventType;
            String Origin;
            String StartTime;
            String EndTime;
            String OBDDeviceDataId;
            String CurrentObdDeviceDataId;
            String DriverLogId;
            String Truck;
            String Trailor;
            String Remarks  ;
            String DriverId;

            boolean IsPersonal;
            boolean IsYard;

            String IsStatusAutomatic;

            String CurrentCycleId;
            int SequenceNumber;

            String TotalVehicleKM;
            String AdditionalInfo;
            String EditedById;
            String UserName;

            String RecordStatus;

            String DistanceSinceLastValidCord;
            String RecordOrigin;
            String DistanceInKM;
            String HexaSeqNumber;
            String OrderBy;
            String OnDutyHours;
            String OffDutyHours;
            String TruckEquipmentNo;
            String WorkShiftStart;
            String WorkShiftEnd;
            private Date dateTime;


    public CanadaDutyStatusModel(String dateTimeWithMins, String eventUTCTimeStamp, String driverStatusID, int eventType, int eventCode, String dutyMinutes, String annotation,
                                 String eventDate, String eventTime, String accumulatedVehicleMiles, String accumulatedEngineHours, String totalVehicleMiles, String totalEngineHours,
                                 String GPSLatitude, String GPSLongitude, String CMVVIN, String carrierName, boolean isMalfunction, String odometerInKm, String strEventType, String origin,
                                 String startTime, String endTime, String OBDDeviceDataId, String currentObdDeviceDataId, String driverLogId, String truck, String trailor, String remarks,
                                 String driverId, boolean isPersonal, boolean isYard, String isStatusAutomatic, String currentCycleId, int sequenceNumber, String totalVehicleKM,
                                 String additionalInfo, String editedById, String userName, String recordStatus, String distanceSinceLastValidCord, String recordOrigin, String distanceInKM,
                                 String hexaSeqNumber, String orderBy, String onDutyHours, String offDutyHours, String truckEquipmentNo, String workShiftStart, String workShiftEnd, Date dateTimee) {
        DateTimeWithMins = dateTimeWithMins;
        EventUTCTimeStamp = eventUTCTimeStamp;
        DriverStatusID = driverStatusID;
        EventType = eventType;
        EventCode = eventCode;
        DutyMinutes = dutyMinutes;
        Annotation = annotation;
        EventDate = eventDate;
        EventTime = eventTime;
        AccumulatedVehicleMiles = accumulatedVehicleMiles;
        AccumulatedEngineHours = accumulatedEngineHours;
        TotalVehicleMiles = totalVehicleMiles;
        TotalEngineHours = totalEngineHours;
        this.GPSLatitude = GPSLatitude;
        this.GPSLongitude = GPSLongitude;
        this.CMVVIN = CMVVIN;
        CarrierName = carrierName;
        IsMalfunction = isMalfunction;
        OdometerInKm = odometerInKm;
        this.strEventType = strEventType;
        Origin = origin;
        StartTime = startTime;
        EndTime = endTime;
        this.OBDDeviceDataId = OBDDeviceDataId;
        CurrentObdDeviceDataId = currentObdDeviceDataId;
        DriverLogId = driverLogId;
        Truck = truck;
        Trailor = trailor;
        Remarks = remarks;
        DriverId = driverId;
        IsPersonal = isPersonal;
        IsYard = isYard;
        IsStatusAutomatic = isStatusAutomatic;
        CurrentCycleId = currentCycleId;
        SequenceNumber = sequenceNumber;
        TotalVehicleKM = totalVehicleKM;
        AdditionalInfo = additionalInfo;
        EditedById = editedById;
        UserName = userName;
        RecordStatus = recordStatus;
        DistanceSinceLastValidCord = distanceSinceLastValidCord;
        RecordOrigin = recordOrigin;
        DistanceInKM = distanceInKM;
        HexaSeqNumber = hexaSeqNumber;
        OrderBy = orderBy;
        OnDutyHours = onDutyHours;
        OffDutyHours = offDutyHours;
        TruckEquipmentNo = truckEquipmentNo;
        WorkShiftStart = workShiftStart;
        WorkShiftEnd = workShiftEnd;
        dateTime = dateTimee;
    }


    public String getDateTimeWithMins() {
        return DateTimeWithMins;
    }

    public String getEventUTCTimeStamp() {
        return EventUTCTimeStamp;
    }

    public String getDriverStatusID() {
        return DriverStatusID;
    }

    public int getEventType() {
        return EventType;
    }

    public int getEventCode() {
        return EventCode;
    }

    public String getDutyMinutes() {
        return DutyMinutes;
    }

    public String getAnnotation() {
        return Annotation;
    }

    public String getEventDate() {
        return EventDate;
    }

    public String getEventTime() {
        return EventTime;
    }

    public String getAccumulatedVehicleMiles() {
        return AccumulatedVehicleMiles;
    }

    public String getAccumulatedEngineHours() {
        return AccumulatedEngineHours;
    }

    public String getTotalVehicleMiles() {
        return TotalVehicleMiles;
    }

    public String getTotalEngineHours() {
        return TotalEngineHours;
    }

    public String getGPSLatitude() {
        return GPSLatitude;
    }

    public String getGPSLongitude() {
        return GPSLongitude;
    }

    public String getCMVVIN() {
        return CMVVIN;
    }

    public String getCarrierName() {
        return CarrierName;
    }

    public boolean isMalfunction() {
        return IsMalfunction;
    }

    public String getOdometerInKm() {
        return OdometerInKm;
    }

    public String getStrEventType() {
        return strEventType;
    }

    public String getOrigin() {
        return Origin;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public String getOBDDeviceDataId() {
        return OBDDeviceDataId;
    }

    public String getCurrentObdDeviceDataId() {
        return CurrentObdDeviceDataId;
    }

    public String getDriverLogId() {
        return DriverLogId;
    }

    public String getTruck() {
        return Truck;
    }

    public String getTrailor() {
        return Trailor;
    }

    public String getRemarks() {
        return Remarks;
    }

    public String getDriverId() {
        return DriverId;
    }

    public boolean isPersonal() {
        return IsPersonal;
    }

    public boolean isYard() {
        return IsYard;
    }

    public String getIsStatusAutomatic() {
        return IsStatusAutomatic;
    }

    public String getCurrentCycleId() {
        return CurrentCycleId;
    }

    public int getSequenceNumber() {
        return SequenceNumber;
    }

    public String getTotalVehicleKM() {
        return TotalVehicleKM;
    }

    public String getAdditionalInfo() {
        return AdditionalInfo;
    }

    public String getEditedById() {
        return EditedById;
    }

    public String getUserName() {
        return UserName;
    }

    public String getRecordStatus() {
        return RecordStatus;
    }

    public String getDistanceSinceLastValidCord() {
        return DistanceSinceLastValidCord;
    }

    public String getRecordOrigin() {
        return RecordOrigin;
    }

    public String getDistanceInKM() {
        return DistanceInKM;
    }

    public String getHexaSeqNumber() {
        return HexaSeqNumber;
    }

    public String getOrderBy() {
        return OrderBy;
    }

    public String getOnDutyHours() {
        return OnDutyHours;
    }

    public String getOffDutyHours() {
        return OffDutyHours;
    }

    public String getTruckEquipmentNo() {
        return TruckEquipmentNo;
    }

    public String getWorkShiftStart() {
        return WorkShiftStart;
    }

    public String getWorkShiftEnd() {
        return WorkShiftEnd;
    }


    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }

    @Override
    public int compareTo(CanadaDutyStatusModel canadaDutyStatusModel) {
        if(getDateTime().equals(canadaDutyStatusModel.getDateTime())){
            return getHexaSeqNumber().compareTo(canadaDutyStatusModel.getHexaSeqNumber());
        }else{
            return getDateTime().compareTo(canadaDutyStatusModel.getDateTime());
        }
    }


}
