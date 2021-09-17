package com.models;

public class MalfunctionHeaderModel {

    String EventName;
    String EventCode;
    String EventDesc;
    boolean IsCleared;
    boolean IsOffline;

    public MalfunctionHeaderModel(String eventName, String eventCode, String eventDesc,
                                  boolean isCleared, boolean isOffline) {
        EventName = eventName;
        EventCode = eventCode;
        EventDesc = eventDesc;
        IsCleared = isCleared;
        IsOffline = isOffline;
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

    public boolean isCleared() {
        return IsCleared;
    }

    public boolean isOffline() {
        return IsOffline;
    }
}
