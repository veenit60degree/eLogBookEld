package com.constants;

public interface DriverLogResponse {

    void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag);
    void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag);



}
