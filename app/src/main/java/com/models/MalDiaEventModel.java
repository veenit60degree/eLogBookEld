package com.models;

public class MalDiaEventModel {

    String EventTitle;
    String EventDesc;

    public MalDiaEventModel(String eventTitle, String eventDesc) {
        EventTitle = eventTitle;
        EventDesc = eventDesc;
    }

    public String getEventTitle() {
        return EventTitle;
    }

    public String getEventDesc() {
        return EventDesc;
    }
}
