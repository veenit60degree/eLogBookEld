package com.models;

public class MalfunctionHeaderModel {

    String EventName;
    String EventCode;

    public MalfunctionHeaderModel(String eventName, String eventCode) {
        EventName = eventName;
        EventCode = eventCode;
    }

    public String getEventName() {
        return EventName;
    }

    public String getEventCode() {
        return EventCode;
    }
}
