package com.models;

import com.local.db.ConstantsKeys;

public class UnidentifiedEventModel {

    String StartTime;
    String EndDateTime;
    int EventType;
    int EventCode;


    public UnidentifiedEventModel(String startTime, String endDateTime, int eventType, int eventCode) {
        StartTime = startTime;
        EndDateTime = endDateTime;
        EventType = eventType;
        EventCode = eventCode;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public int getEventType() {
        return EventType;
    }

    public int getEventCode() {
        return EventCode;
    }
}



