package com.models;

import org.joda.time.DateTime;

public class RecapSignModel {

    boolean isCertified;
    boolean isMissingLocation;
    DateTime date;


    public RecapSignModel(boolean isCertified, boolean isMissingLocation, DateTime date) {

        this.isCertified = isCertified;
        this.isMissingLocation = isMissingLocation;
        this.date = date;
    }

    public boolean isCertified() {
        return isCertified;
    }

    public boolean isMissingLocation(){
        return isMissingLocation;
    }


    public DateTime getDate() {
        return date;
    }


}
