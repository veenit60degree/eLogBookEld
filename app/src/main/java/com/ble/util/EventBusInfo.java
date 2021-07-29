package com.ble.util;

import java.io.Serializable;

public class EventBusInfo implements Serializable {
    public String address;
    public String uuid;
    public String action;
    public Object object;

    public EventBusInfo(){

    }

    public EventBusInfo(String action, String address){
        this.action =action;
        this.address = address;

    }

    public EventBusInfo(String action, String address, String uuid, Object object){
        this.action = action;
        this.address = address;
        this.uuid = uuid;
        this.object = object;
    }
}
