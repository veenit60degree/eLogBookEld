package com.models;


public class DriverLocationModel {

    String StateCode;
    String State;
    String Country;

    public DriverLocationModel(String stateCode, String state, String country) {
        StateCode = stateCode;
        State = state;
        Country = country;
    }


    public String getStateCode() {
        return StateCode;
    }

    public String getState() {
        return State;
    }

    public String getCountry() {
        return Country;
    }
}
