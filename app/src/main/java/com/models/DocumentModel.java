package com.models;

public class DocumentModel {

    String  JobLoadDocId, JobId, LoadID, JobLoadDocTypeId, JobLoadDocTypeName, FileName, FilePath;

    public DocumentModel(String jobLoadDocId, String jobId, String loadID, String jobLoadDocTypeId,
                         String jobLoadDocTypeName, String fileName, String filePath) {
        JobLoadDocId = jobLoadDocId;
        JobId = jobId;
        LoadID = loadID;
        JobLoadDocTypeId = jobLoadDocTypeId;
        JobLoadDocTypeName = jobLoadDocTypeName;
        FileName = fileName;
        FilePath = filePath;
    }


    public String getJobLoadDocId() {
        return JobLoadDocId;
    }

    public void setJobLoadDocId(String jobLoadDocId) {
        JobLoadDocId = jobLoadDocId;
    }

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public String getLoadID() {
        return LoadID;
    }

    public void setLoadID(String loadID) {
        LoadID = loadID;
    }

    public String getJobLoadDocTypeId() {
        return JobLoadDocTypeId;
    }

    public void setJobLoadDocTypeId(String jobLoadDocTypeId) {
        JobLoadDocTypeId = jobLoadDocTypeId;
    }

    public String getJobLoadDocTypeName() {
        return JobLoadDocTypeName;
    }

    public void setJobLoadDocTypeName(String jobLoadDocTypeName) {
        JobLoadDocTypeName = jobLoadDocTypeName;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }
}
