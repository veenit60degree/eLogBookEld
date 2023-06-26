package com.constants;

import android.content.Context;
import android.util.Log;

import com.als.logistic.Globally;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SaveLogJsonObj {

    DriverLogResponse postResponse;
    Context context;
    Globally global;
    RequestQueue SaveLogRequest;
    FailedApiTrackMethod failedApiTrackMethod;
    DBHelper dbHelper;

    public SaveLogJsonObj(Context cxt, DriverLogResponse response){
        context = cxt;
        postResponse = response;
        failedApiTrackMethod = new FailedApiTrackMethod();
        dbHelper = new DBHelper(cxt);
        global = new Globally();

    }


    public void SaveLogJsonObj(final JSONObject geoData, final String api, final int socketTimeout,
                                  final boolean isLoad, final boolean IsRecap, final int DriverType, final int flag) {

        if (SaveLogRequest == null) {
            SaveLogRequest = Volley.newRequestQueue(context);
        }

        if (failedApiTrackMethod.isAllowToCallOrReset(dbHelper, api, false, global, context)) {

            // save api call count on request time
            failedApiTrackMethod.confirmAndSaveApiTrack(dbHelper, api, global, context);


            StringRequest postRequest = new StringRequest(Request.Method.POST, api,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Logger.LogDebug("Response ", ">>>Response: " + response);

                            // Reset failed API track after successfully response
                            if (failedApiTrackMethod.isSuccess(response)) {
                                failedApiTrackMethod.isAllowToCallOrReset(dbHelper, api, true, global, context);
                            }


                            JSONArray inputArray = new JSONArray();
                            try {
                                String data = geoData.toString();
                                if (data.length() > 0) {
                                    if (!data.substring(0, 1).equals("[")) {
                                        data = "[" + geoData + "]";
                                    }
                                }
                                inputArray = new JSONArray(data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            postResponse.onApiResponse(response, isLoad, IsRecap, DriverType, flag, inputArray);

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
                        Logger.LogDebug("api", ">>>certify Json api: " + api);
                        Logger.LogDebug("certify", ">>>certify Json Data: " + geoData.toString());
                        return geoData.toString().getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                geoData, "utf-8");
                        return null;
                    }

                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    Logger.LogDebug("API TAG", ">>>SaveJsonLog 1- " +api);

                    return params;
                }
            };

            //RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES);
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy); //new DefaultRetryPolicy()
            SaveLogRequest.add(postRequest);

        } else {
            postResponse.onResponseError("Error", isLoad, IsRecap, DriverType, flag);


            // ------------------------ Failed API code ------------------------------------
            String DriverId = SharedPref.getDriverId(context);
            if(DriverId.length() > 0 && !DriverId.equals("0")) {

                int callCount = failedApiTrackMethod.getCallCount(dbHelper, api);
                if (callCount == 4 || callCount == 5) {

                    // save request time one more time to avoid api call
                    failedApiTrackMethod.confirmAndSaveApiTrack(dbHelper, api, global, context);

                    String failedInputData = failedApiTrackMethod.getFailedInputObjData(SharedPref.getDriverId(context),
                            api, geoData, global, context);

                    // call failed record save api to post failed data on server
                    StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.FAILED_API_TRACK,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Logger.LogDebug("Response ", ">>>Response: " + response);

                                    // Reset failed API track after successfully response
                            /*if(failedApiTrackMethod.isSuccess(response)) {
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
                                Logger.LogDebug("FailedDataInput", ">>FailedDataInput: " + failedInputData);
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

                            Logger.LogDebug("API TAG", ">>>SaveJsonLog- " +api);

                            return params;
                        }
                    };

                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    postRequest.setRetryPolicy(policy);
                    SaveLogRequest.add(postRequest);


                }
            }
        }

    }


}
