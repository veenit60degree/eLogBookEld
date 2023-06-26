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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class ShippingPost {

    RequestResponse postResponse;
    Context context;

    public ShippingPost(Context cxt, RequestResponse response){
        context = cxt;
        postResponse = response;
    }

    /*================== Save Shipping Document Number ===================*/
   public void PostListingData(final JSONArray geoData, String URL, final int flag){


        RequestQueue SaveLogRequest      = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Logger.LogDebug("Response ", ">>>Response: " + response);
                        postResponse.onApiResponse(response, flag);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.LogDebug("error", ">>errorrrrr: " + error);
                        postResponse.onResponseError(error.toString(), flag);
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
                    Logger.LogDebug("data", ">>geoData api: " + URL);
                    Logger.LogDebug("data", ">>geoData: " + geoData);
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

        int socketTimeout = 5000;   //5 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT );
        postRequest.setRetryPolicy(policy);
        SaveLogRequest.add(postRequest);


    }





}
