package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.SharedPref;
import com.local.db.ConstantsKeys;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.TabAct;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AppUpdateDialog extends Dialog {



 /*   public interface UpdateListener {
        public void UpdateReady(String viewType);
    }*/

    String DriverId, DeviceId, currentVersion, newAppVersion;
    boolean isPlayStoreDownload;
   // private UpdateListener updateListener;
    TextView currentVersionTV, appVersionDescTV;
    Button btnIgnore, btnUpdate;
    TabHost tabHost;
    Context context;


    public AppUpdateDialog(Context context, boolean update_type, String newAppVersion, TabHost host) { //, UpdateListener updateListener
        super(context);
        this.context = context;
        this.isPlayStoreDownload = update_type;
        this.newAppVersion = newAppVersion;
        this.tabHost = host;
      //  this.updateListener = updateListener;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_popup);
        setCancelable(false);

        DriverId         = SharedPref.getDriverId(context);
        DeviceId         = SharedPref.GetSavedSystemToken(context);
        currentVersion   = Globally.GetAppVersion(context, "VersionName");

        currentVersionTV = (TextView)findViewById(R.id.currentVersionTV);
        appVersionDescTV = (TextView)findViewById(R.id.appVersionDescTV);

        btnIgnore        = (Button)findViewById(R.id.btnIgnore);
        btnUpdate        = (Button)findViewById(R.id.btnUpdate);

        currentVersionTV.setText(context.getResources().getString(R.string.current_version) + " " + currentVersion);

        String downloadDesc = context.getResources().getString(R.string.new_version) + newAppVersion +
                context.getResources().getString(R.string.update_app_desc);

        if(isPlayStoreDownload) {
            appVersionDescTV.setText(downloadDesc + " " + context.getResources().getString(R.string.play_store));
        }else{
            appVersionDescTV.setText(downloadDesc + " " + context.getResources().getString(R.string.app_settings));
        }

        btnIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIgnore.setEnabled(false);
                SharedPref.SetUpdateAppDialogTime(Globally.GetCurrentDeviceDate(null, new Globally(), context), context);

                ignoreUpdate(DriverId, DeviceId, currentVersion);

                dismiss();



            }
        });

        btnUpdate.setOnClickListener(new UpdateClickListener()) ;



    }




    private class UpdateClickListener implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
           // updateListener.UpdateReady( startTimeTextView, endTimeTextView, viewType, timePicker );
            SharedPref.SetUpdateAppDialogTime(Globally.GetCurrentDeviceDate(null, new Globally(), context), context);

            if(isPlayStoreDownload){
                launchPlayStore();
            }else{
                TabAct.isUpdateDirectly = true;
                tabHost.setCurrentTab(1);
            }

            dismiss();
        }
    };


    private void launchPlayStore(){

        try {

          /*  Launch app
          Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.android.vending");
            if (launchIntent != null) {
                context.startActivity(launchIntent);
            } */

            final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }


    void ignoreUpdate(final String DriverId, final String DeviceId, final String AppVersion ) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.UPDATE_DRIVER_VERSION_IGNORE_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.LogDebug("Response", ">>>response: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.LogDebug("error", "error: " + error);
                    }
                }
        ) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.headers == null) {
                    // cant just set a new empty map because the member is final.
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                            response.notModified,
                            response.networkTimeMs);
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(ConstantsKeys.DriverId, DriverId);
                 params.put(ConstantsKeys.DeviceId, DeviceId);
                params.put(ConstantsKeys.AppVersion, AppVersion);

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(Constants.SocketTimeout10Sec, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);


    }




}
