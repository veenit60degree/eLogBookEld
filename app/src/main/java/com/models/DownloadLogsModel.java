package com.models;

public class DownloadLogsModel {

    String DriverId;
    String EldInspectionLogId;
    String PdfFilePath;
    String FileNameUniqueNumber;
    String LogGenratedDateTime;
    String FromDate;
    String ToDate;
    String ShareId;
    String LogType;
    String Country;



    public DownloadLogsModel(String driverId, String eldInspectionLogId, String pdfFilePath,
                             String fileNameUniqueNumber, String logGenratedDateTime,
                             String toDate, String fromDate, String logType, String shareId,String country) {
        DriverId = driverId;
        EldInspectionLogId = eldInspectionLogId;
        PdfFilePath = pdfFilePath;
        FileNameUniqueNumber = fileNameUniqueNumber;
        LogGenratedDateTime = logGenratedDateTime;
        ToDate = toDate;
        ShareId  = shareId;
        FromDate = fromDate;
        LogType  = logType;
        Country  = country;
    }

    public String getDriverId() {
        return DriverId;
    }

    public String getEldInspectionLogId() {
        return EldInspectionLogId;
    }

    public String getPdfFilePath() {
        return PdfFilePath;
    }

    public String getFileNameUniqueNumber() {
        return FileNameUniqueNumber;
    }

    public String getLogGenratedDateTime() {
        return LogGenratedDateTime;
    }

    public String getFromDate() {
        return FromDate;
    }

    public String getToDate() {
        return ToDate;
    }

    public String getLogtype() {
        return LogType;
    }

    public String getShareId() {
        return ShareId;
    }

    public String getCountry() {
        return Country;
    }
}
