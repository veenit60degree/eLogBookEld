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

public class SaveUnidentifiedRecord
{

    VolleyRequest.VolleyCallback postResponse;
    VolleyRequest.VolleyErrorCall ErrorCallBack;
    Context context;
    RequestQueue SaveLogRequest;

    public SaveUnidentifiedRecord(Context cxt, VolleyRequest.VolleyCallback response){
        context = cxt;
        postResponse = response;
    }


    public void PostDriverLogData(final JSONArray driverLogData, final String api, final int socketTimeout, final int flag){

        if (SaveLogRequest == null) {
            SaveLogRequest      = Volley.newRequestQueue(context);
        }


        StringRequest postRequest = new StringRequest(Request.Method.POST, api,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response ", ">>>Response: " + response);
                       // SharedPref.SetEditedLogStatus(false, context);
                        postResponse.getResponse(response,flag);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", ">>errorrrrr: " + error);
                        try {
                            ErrorCallBack.getError(error, flag);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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
                    Log.d("api", ">>>certify uniden Data api: " + api);
                     Log.d("certify", ">>>>>certify uniden Data: " + driverLogData.toString());
                    return driverLogData.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            driverLogData, "utf-8");
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

    public interface VolleyCallback {
        public void getResponse(String response, int flag);
    }

    public interface VolleyErrorCall {
        public void getError(VolleyError error, int flag);
    }




}

