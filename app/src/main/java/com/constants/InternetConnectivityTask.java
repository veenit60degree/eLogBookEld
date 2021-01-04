package com.constants;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by kumar on 1/17/2018.
 */

public class InternetConnectivityTask {

    boolean InternetConnection = false, IsCheck;
    int Flag;
    static String HostName = "www.google.com"; //www.yahoo.com
    Context context;
    Socket socket;
    Thread thread;


    private ConnectivityInterface mListener;


    public InternetConnectivityTask(Context cxt) {
        this.context = cxt;
    }


    public void ConnectivityRequest(int flag, ConnectivityInterface listener){
        this.Flag = flag;
        this.mListener  = listener;
        IsCheck = false;

        InternetConnection = isNetworkConnected(context);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IsCheck = IsInternetWorking();
            }
        });
        thread.start();

        if(InternetConnection) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!IsCheck){
                        try {
                            if(socket != null)
                                socket.close();

                            thread = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mListener.IsConnected(IsCheck, Flag);
                    }else{
                        mListener.IsConnected(IsCheck, Flag);
                    }
                }
            }, 500);
        }else{
            mListener.IsConnected(InternetConnection, Flag);
        }


    }




    public interface ConnectivityInterface {
        public void IsConnected(boolean result, int flag);
    }


    // ------------ Check Available Internet it is working or not with Socket --------------
    private boolean IsInternetWorking() {

        int port = 80;
        socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(HostName, port), 2000);
            socket.close();
            return true;
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException es) {}
            return false;
        }


    }

    // -------------- Check Wifi or mobile hotspot is enabled or disabled -------------
    private boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

}
