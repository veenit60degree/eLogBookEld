package com.models;

/**
 * Created by kumar on 9/7/2017.
 */

public class CycleModel {

    String cycleId;
    String cycleName;

    public CycleModel(String cycleId, String cycleName) {
        this.cycleId = cycleId;
        this.cycleName = cycleName;
    }

    public String getCycleId() {
        return cycleId;
    }

    public String getCycleName() {
        return cycleName;
    }
}
