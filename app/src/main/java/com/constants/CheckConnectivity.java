package com.constants;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kumar on 5/22/2018.
 */

public class CheckConnectivity {

    boolean InternetConnection = false;
    int Flag;
    Context context;


    private ConnectivityInterface mListener;
    VolleyRequestWithoutRetry checkAlsConnection ;

    public CheckConnectivity(Context cxt) {
        this.context = cxt;
        checkAlsConnection = new VolleyRequestWithoutRetry(context);
    }


    public void ConnectivityRequest(int flag, ConnectivityInterface listener) {
        this.Flag = flag;
        this.mListener = listener;

        InternetConnection = isNetworkConnected(context);

        if (InternetConnection) {
            CheckInternet(checkAlsConnection);
        } else {
            mListener.IsConnected(InternetConnection, Flag);
        }


    }


    public interface ConnectivityInterface {
        public void IsConnected(boolean result, int flag);
    }


    // ------------ Check Available Internet it is working or not with Socket --------------
    void CheckInternet(VolleyRequestWithoutRetry checkAlsConnection) {
       // VolleyRequestWithoutRetry checkAlsConnection = new VolleyRequestWithoutRetry(context);
        Map<String, String> params = new HashMap<String, String>();
        checkAlsConnection.executeRequest(Request.Method.GET, APIs.CHECK_CONNECTION, params, 1,
                Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);
    }

    // -------------- Check Wifi or mobile hotspot is enabled or disabled -------------
    private boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            try {
                if(response.equals("true")){
                    mListener.IsConnected(true, Flag);
                }else {
                    mListener.IsConnected(false, Flag);
                }
            }catch (Exception e){e.printStackTrace();}

        }

    };



    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            mListener.IsConnected(false, Flag);
        }
    };



}