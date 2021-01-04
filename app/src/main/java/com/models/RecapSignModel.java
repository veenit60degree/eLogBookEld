package com.models;

import org.joda.time.DateTime;

public class RecapSignModel {

    boolean isCertified;
    DateTime date;


    public RecapSignModel(boolean isCertified, DateTime date) {

        this.isCertified = isCertified;
        this.date = date;
    }

    public boolean isCertified() {
        return isCertified;
    }

    public DateTime getDate() {
        return date;
    }


}
