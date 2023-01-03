package com.constants;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.local.db.DBHelper;
import com.local.db.FailedApiTrackMethod;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SaveDriverLogPost
{

    DriverLogResponse postResponse;
    Context context;
    RequestQueue SaveLogRequest;
    FailedApiTrackMethod failedApiTrackMethod;
    DBHelper dbHelper;

    public SaveDriverLogPost(Context cxt, DriverLogResponse response){
        context = cxt;
        postResponse = response;
        failedApiTrackMethod = new FailedApiTrackMethod();
        dbHelper = new DBHelper(cxt);

    }


    public void PostDriverLogData(final JSONArray driverLogData, final String api, final int socketTimeout,
                                  final boolean isLoad, final boolean IsRecap, final int DriverType, final int flag){

        if (SaveLogRequest == null) {
            SaveLogRequest = Volley.newRequestQueue(context);
        }

        if(failedApiTrackMethod.isAllowToCallOrReset(dbHelper, api, false)) {

            // save api call count on request time
            failedApiTrackMethod.confirmAndSaveApiTrack(dbHelper, api, true);


            StringRequest postRequest = new StringRequest(Request.Method.POST, api,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Logger.LogDebug("Response ", ">>>Response: " + response);

                            // Reset failed API track after successfully response
                            if(failedApiTrackMethod.isSuccess(response)) {
                                failedApiTrackMethod.isAllowToCallOrReset(dbHelper, api, true);
                            }

                            postResponse.onApiResponse(response, isLoad, IsRecap, DriverType, flag, driverLogData);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Logger.LogDebug("error", ">>errorrrrr: " + error);

                            postResponse.onResponseError(error.toString(), isLoad, IsRecap, DriverType, flag);
                        }
                    }
            ) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        Logger.LogDebug("api", ">>>certify Data api: " + api);
                        Logger.LogDebug("certify", ">>>certify Data: " + driverLogData.toString());
                        return driverLogData.toString().getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                driverLogData, "utf-8");
                        return null;
                    }

                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    return params;
                }
            };

            //RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES);
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy); //new DefaultRetryPolicy()
            SaveLogRequest.add(postRequest);
        }else{
            // pass error response on calling page
            postResponse.onResponseError("Error", isLoad, IsRecap, DriverType, flag);


            // ------------------------ Failed API code ------------------------------------
            String failedInputData = failedApiTrackMethod.getFailedInputArrayData(SharedPref.getDriverId(context),
                    api, driverLogData);

            // call failed record save api to post failed data on server
            StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.FAILED_API_TRACK,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Logger.LogDebug("Response ", ">>>Response: " + response);

                            // Reset failed API track after successfully response
                          /*  if(failedApiTrackMethod.isSuccess(response)) {
                                failedApiTrackMethod.isAllowToCallOrReset(dbHelper, api, true);
                            }*/

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Logger.LogDebug("error", ">>errorrrrr: " + error);
                        }
                    }
            ) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        Logger.LogDebug("FailedDataInput", ">>>FailedDataInput: " + failedInputData);
                        return failedInputData.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                failedInputData, "utf-8");
                        return null;
                    }

                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    return params;
                }
            };

            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy); //new DefaultRetryPolicy()
            SaveLogRequest.add(postRequest);


        }

    }




}
