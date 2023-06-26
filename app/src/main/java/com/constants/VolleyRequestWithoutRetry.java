package com.constants;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.als.logistic.TabAct;

import java.util.Map;

public class VolleyRequestWithoutRetry {

        final String contentType = "application/x-www-form-urlencoded";  //"application/json; charset=utf-8";
    String url = "";
        Context context;
       // RequestQueue requestQueue;

        public VolleyRequestWithoutRetry(Context context) {
            this.context = context;
        }

   /* public void addHeader(String key, String value) {
        header.put(key, value);
    }*/

        public void executeRequest(int method, final String JsonURL, final Map<String, String> params,
                                   final int flag, int socketTimeout, final VolleyRequest.VolleyCallback callback, final VolleyRequest.VolleyErrorCall ErrorCallback) {

            url = JsonURL;
            if (TabAct.alsConnRequestQueue == null) {
                TabAct.alsConnRequestQueue = Volley.newRequestQueue(context);
            }


            StringRequest postRequest = new StringRequest(method, JsonURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.LogDebug("RES", " >>>Response" + flag + ": " + response);
                    if(context != null) {
                        callback.getResponse(response, flag);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.LogError("error", " error: " + error);
                    if(context != null) {
                        ErrorCallback.getError(error, flag);
                    }
                }
            }) {

                /* @Override
                 public String getBodyContentType() {
                     return contentType;
                 }
     */
                @Override
                protected Map<String, String> getParams() {
                    //  Logger.LogError("params", " params: " + params);

                    Logger.LogDebug("API TAG", ">>>Without retry " + url);

                    return params;
                }

            };

            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_MAX_RETRIES);
            postRequest.setRetryPolicy(policy); //new DefaultRetryPolicy()
            TabAct.alsConnRequestQueue.add(postRequest);

        }


        public interface VolleyCallback {
            public void getResponse(String response, int flag);
        }

        public interface VolleyErrorCall {
            public void getError(VolleyError error, int flag);
        }


}
