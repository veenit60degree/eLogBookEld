package com.models;

import org.json.JSONArray;

import java.util.List;

public class TripHistoryModel {

    String ShipperName, ShipperAddress, ShipperStateCode, ShipperCity, ShipperPostal, ShipperCountryCode, LoadId, LoadNumber, IsRead,
            LoadStatusId, LoadStatusName, ConsigneeName, ConsigneeAddress, ConsigneeStateCode, ConsigneeCity, ConsigneePostal,
            ConsigneeCountryCode, ConsigneeLattitude, ConsigneeLongitude, IsProrityShipment, Remarks, Description, LoadShipperComment,
            EntryTime, ExitTime, DeliveryWaitingTime, DeliverywaitingTimeReason, LoadType, QTY, LBS, KGS, JobId, IsCustomeBrokerCleared, LoadDeliveryDocuments;



    public TripHistoryModel(String shipperName, String shipperAddress, String shipperStateCode, String shipperCity, String shipperPostal,
                            String shipperCountryCode, String loadId, String loadNumber, String isRead, String loadStatusId, String loadStatusName,
                            String consigneeName, String consigneeAddress, String consigneeStateCode, String consigneeCity, String consigneePostal,
                            String consigneeCountryCode, String consigneeLattitude, String consigneeLongitude, String isProrityShipment, String remarks,
                            String description, String loadShipperComment, String entryTime, String exitTime, String deliveryWaitingTime,
                            String deliverywaitingTimeReason, String loadType, String QTY, String LBS, String KGS, String jobId,
                            String isCustomeBrokerCleared, String LoadDeliveryDocuments) {
        this.ShipperName = shipperName;
        this.ShipperAddress = shipperAddress;
        this.ShipperStateCode = shipperStateCode;
        this.ShipperCity = shipperCity;
        this.ShipperPostal = shipperPostal;
        this.ShipperCountryCode = shipperCountryCode;
        this.LoadId = loadId;
        this.LoadNumber = loadNumber;
        this.IsRead = isRead;
        this.LoadStatusId = loadStatusId;
        this.LoadStatusName = loadStatusName;
        this.ConsigneeName = consigneeName;
        this.ConsigneeAddress = consigneeAddress;
        this.ConsigneeStateCode = consigneeStateCode;
        this.ConsigneeCity = consigneeCity;
        this.ConsigneePostal = consigneePostal;
        this.ConsigneeCountryCode = consigneeCountryCode;
        this.ConsigneeLattitude = consigneeLattitude;
        this.ConsigneeLongitude = consigneeLongitude;
        this.IsProrityShipment = isProrityShipment;
        this.Remarks = remarks;
        this.Description = description;
        this.LoadShipperComment = loadShipperComment;
        this.EntryTime = entryTime;
        this.ExitTime = exitTime;
        this.DeliveryWaitingTime = deliveryWaitingTime;
        this.DeliverywaitingTimeReason = deliverywaitingTimeReason;
        this.LoadType = loadType;
        this.QTY = QTY;
        this.LBS = LBS;
        this.KGS = KGS;
        this.JobId = jobId;
        this.IsCustomeBrokerCleared = isCustomeBrokerCleared;
        this.LoadDeliveryDocuments = LoadDeliveryDocuments;

    }


    public String getLoadDeliveryDocuments() {
        return LoadDeliveryDocuments;
    }

    public void setLoadDeliveryDocuments(String loadDeliveryDocuments) {
        LoadDeliveryDocuments = loadDeliveryDocuments;
    }

    public String getShipperName() {
        return ShipperName;
    }

    public void setShipperName(String shipperName) {
        ShipperName = shipperName;
    }

    public String getShipperAddress() {
        return ShipperAddress;
    }

    public void setShipperAddress(String shipperAddress) {
        ShipperAddress = shipperAddress;
    }

    public String getShipperStateCode() {
        return ShipperStateCode;
    }

    public void setShipperStateCode(String shipperStateCode) {
        ShipperStateCode = shipperStateCode;
    }

    public String getShipperCity() {
        return ShipperCity;
    }

    public void setShipperCity(String shipperCity) {
        ShipperCity = shipperCity;
    }

    public String getShipperPostal() {
        return ShipperPostal;
    }

    public void setShipperPostal(String shipperPostal) {
        ShipperPostal = shipperPostal;
    }

    public String getShipperCountryCode() {
        return ShipperCountryCode;
    }

    public void setShipperCountryCode(String shipperCountryCode) {
        ShipperCountryCode = shipperCountryCode;
    }

    public String getLoadId() {
        return LoadId;
    }

    public void setLoadId(String loadId) {
        LoadId = loadId;
    }

    public String getLoadNumber() {
        return LoadNumber;
    }

    public void setLoadNumber(String loadNumber) {
        LoadNumber = loadNumber;
    }

    public String getIsRead() {
        return IsRead;
    }

    public void setIsRead(String isRead) {
        IsRead = isRead;
    }

    public String getLoadStatusId() {
        return LoadStatusId;
    }

    public void setLoadStatusId(String loadStatusId) {
        LoadStatusId = loadStatusId;
    }

    public String getLoadStatusName() {
        return LoadStatusName;
    }

    public void setLoadStatusName(String loadStatusName) {
        LoadStatusName = loadStatusName;
    }

    public String getConsigneeName() {
        return ConsigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        ConsigneeName = consigneeName;
    }

    public String getConsigneeAddress() {
        return ConsigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        ConsigneeAddress = consigneeAddress;
    }

    public String getConsigneeStateCode() {
        return ConsigneeStateCode;
    }

    public void setConsigneeStateCode(String consigneeStateCode) {
        ConsigneeStateCode = consigneeStateCode;
    }

    public String getConsigneeCity() {
        return ConsigneeCity;
    }

    public void setConsigneeCity(String consigneeCity) {
        ConsigneeCity = consigneeCity;
    }

    public String getConsigneePostal() {
        return ConsigneePostal;
    }

    public void setConsigneePostal(String consigneePostal) {
        ConsigneePostal = consigneePostal;
    }

    public String getConsigneeCountryCode() {
        return ConsigneeCountryCode;
    }

    public void setConsigneeCountryCode(String consigneeCountryCode) {
        ConsigneeCountryCode = consigneeCountryCode;
    }

    public String getConsigneeLattitude() {
        return ConsigneeLattitude;
    }

    public void setConsigneeLattitude(String consigneeLattitude) {
        ConsigneeLattitude = consigneeLattitude;
    }

    public String getConsigneeLongitude() {
        return ConsigneeLongitude;
    }

    public void setConsigneeLongitude(String consigneeLongitude) {
        ConsigneeLongitude = consigneeLongitude;
    }

    public String getIsProrityShipment() {
        return IsProrityShipment;
    }

    public void setIsProrityShipment(String isProrityShipment) {
        IsProrityShipment = isProrityShipment;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLoadShipperComment() {
        return LoadShipperComment;
    }

    public void setLoadShipperComment(String loadShipperComment) {
        LoadShipperComment = loadShipperComment;
    }

    public String getEntryTime() {
        return EntryTime;
    }

    public void setEntryTime(String entryTime) {
        EntryTime = entryTime;
    }

    public String getExitTime() {
        return ExitTime;
    }

    public void setExitTime(String exitTime) {
        ExitTime = exitTime;
    }

    public String getDeliveryWaitingTime() {
        return DeliveryWaitingTime;
    }

    public void setDeliveryWaitingTime(String deliveryWaitingTime) {
        DeliveryWaitingTime = deliveryWaitingTime;
    }

    public String getDeliverywaitingTimeReason() {
        return DeliverywaitingTimeReason;
    }

    public void setDeliverywaitingTimeReason(String deliverywaitingTimeReason) {
        DeliverywaitingTimeReason = deliverywaitingTimeReason;
    }

    public String getLoadType() {
        return LoadType;
    }

    public void setLoadType(String loadType) {
        LoadType = loadType;
    }

    public String getQTY() {
        return QTY;
    }

    public void setQTY(String QTY) {
        this.QTY = QTY;
    }

    public String getLBS() {
        return LBS;
    }

    public void setLBS(String LBS) {
        this.LBS = LBS;
    }

    public String getKGS() {
        return KGS;
    }

    public void setKGS(String KGS) {
        this.KGS = KGS;
    }

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public String getIsCustomeBrokerCleared() {
        return IsCustomeBrokerCleared;
    }

    public void setIsCustomeBrokerCleared(String isCustomeBrokerCleared) {
        IsCustomeBrokerCleared = isCustomeBrokerCleared;
    }


}
