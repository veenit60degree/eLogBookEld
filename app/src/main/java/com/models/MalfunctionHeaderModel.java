package com.models;

public class MalfunctionHeaderModel {

    String EventName;
    String EventCode;
    String EventDesc;

    public MalfunctionHeaderModel(String eventName, String eventCode, String eventDesc) {
        EventName = eventName;
        EventCode = eventCode;
        EventDesc = eventDesc;
    }

    public String getEventName() {
        return EventName;
    }

    public String getEventCode() {
        return EventCode;
    }

    public String getEventDesc() {
        return EventDesc;
    }

}
