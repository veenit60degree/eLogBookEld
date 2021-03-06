package com.constants;

import android.content.Context;
import android.util.Log;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SaveLogJsonObj {

    DriverLogResponse postResponse;
    Context context;
    RequestQueue SaveLogRequest;

    public SaveLogJsonObj(Context cxt, DriverLogResponse response){
        context = cxt;
        postResponse = response;
    }


    public void SaveLogJsonObj(final JSONObject geoData, final String api, final int socketTimeout,
                                  final boolean isLoad, final boolean IsRecap, final int DriverType, final int flag){

        if (SaveLogRequest == null) {
            SaveLogRequest      = Volley.newRequestQueue(context);
        }


        StringRequest postRequest = new StringRequest(Request.Method.POST, api,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response ", ">>>Response: " + response);
                        postResponse.onApiResponse(response, isLoad, IsRecap, DriverType, flag);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", ">>errorrrrr: " + error);

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
                    // Log.d("certify", "certify Data: " + geoData.toString());
                    return geoData.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            geoData, "utf-8");
                    return null;
                }

            }

            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };

        //RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES);
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy); //new DefaultRetryPolicy()
        SaveLogRequest.add(postRequest);


    }




}
