package com.constants;


public interface RequestResponse {

    void onApiResponse(String response, int flag);
    void onResponseError(String error, int flag);

}
