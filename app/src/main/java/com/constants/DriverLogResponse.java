package com.constants;

import org.json.JSONArray;

public interface DriverLogResponse {

    void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, JSONArray inputArray);
    void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag);



}
