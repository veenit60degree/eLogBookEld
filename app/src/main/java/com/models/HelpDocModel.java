package com.models;

public class HelpDocModel {

    String Version;
    int DocumentNumber ;
    String documentTitle;
    String ServerDateTime;
    String utcDateTime;
    String documentUrl;

    public HelpDocModel(String version, int documentNumber, String documentTitle, String serverDateTime, String utcDateTime, String documentUrl) {
        this.Version = version;
        this.DocumentNumber = documentNumber;
        this.documentTitle = documentTitle;
        this.ServerDateTime = serverDateTime;
        this.utcDateTime = utcDateTime;
        this.documentUrl = documentUrl;
    }

    public String getVersion() {
        return Version;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public int getDocumentNumber() {
        return DocumentNumber;
    }

    public String getServerDateTime() {
        return ServerDateTime;
    }

    public String getUtcDateTime() {
        return utcDateTime;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }
}
