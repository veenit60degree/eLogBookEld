package com.models;

import org.joda.time.DateTime;

public class RecapSignModel {

    boolean isCertified;
    boolean isMissingLocation;
    boolean isRecertificationReq;
    DateTime date;


    public RecapSignModel(boolean isCertified, boolean isMissingLocation, boolean isRecertificationReq, DateTime date) {

        this.isCertified = isCertified;
        this.isMissingLocation = isMissingLocation;
        this.isRecertificationReq = isRecertificationReq;
        this.date = date;
    }

    public boolean isCertified() {
        return isCertified;
    }

    public boolean isMissingLocation(){
        return isMissingLocation;
    }

    public boolean isRecertificationReq() {
        return isRecertificationReq;
    }

    public DateTime getDate() {
        return date;
    }


}
