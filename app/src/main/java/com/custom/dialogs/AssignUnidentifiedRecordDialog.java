package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.constants.Logger;
import com.constants.SharedPref;
import com.google.android.material.textfield.TextInputLayout;
import com.local.db.ConstantsKeys;
import com.als.logistic.Globally;
import com.als.logistic.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AssignUnidentifiedRecordDialog extends Dialog {


    private String Title, Desc, DRIVER_ID = "", DeviceId = "";
    Button btnAccept, btnDecline;
    TextView TitleTV, SpinnerTitleTV, recordTitleTV;
    TextInputLayout trailorNoInputType;


    public AssignUnidentifiedRecordDialog(Context context, String title, String desc) {
        super(context);
        this.Title = title;
        Desc = desc;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_trailor_fields);
        // setCancelable(false);

        // getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        btnAccept           = (Button) findViewById(R.id.btnLoadingJob);
        btnDecline          = (Button) findViewById(R.id.btnCancelLoadingJob);
        trailorNoInputType  = (TextInputLayout) findViewById(R.id.trailorNoInputType);

        TitleTV             = (TextView) findViewById(R.id.TitleTV);
        SpinnerTitleTV      = (TextView) findViewById(R.id.SpinnerTitleTV);
        recordTitleTV       = (TextView) findViewById(R.id.recordTitleTV);

        recordTitleTV.setVisibility(View.VISIBLE);
        trailorNoInputType.setVisibility(View.GONE);

        DRIVER_ID           = SharedPref.getDriverId( getContext());
        DeviceId            = SharedPref.GetSavedSystemToken(getContext());

        TitleTV.setText(Title);
        recordTitleTV.setText(Desc);
        btnAccept.setText("Accept");
        btnDecline.setText("Decline");

        btnAccept.setOnClickListener(new AcceptBtnListener());
        btnDecline.setOnClickListener(new DeclineBtnListener());

        HideKeyboard();
    }


    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }


    private class AcceptBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AcceptDeclineRecord(DRIVER_ID, DeviceId);
        }
    }


    private class DeclineBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AcceptDeclineRecord(DRIVER_ID, DeviceId);
        }
    }



    /*================== Accept Decline Record ===================*/
    void AcceptDeclineRecord(final String DriverId, final String DeviceId){

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, "" , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Logger.LogDebug("response", ">>>AcceptDeclineRecord response: " + response);
                String status = "" ;
                JSONObject obj;

                try {
                    obj = new JSONObject(response);
                    status = obj.getString("Status");

                    if(status.equalsIgnoreCase("true")){
                        Globally.EldScreenToast(btnAccept, obj.getString("Message") , getContext().getResources().getColor(R.color.color_eld_theme));
                        dismiss();
                    }else{
                        if(!obj.isNull("Message")) {
                            Globally.EldScreenToast(btnAccept, obj.getString("Message"), getContext().getResources().getColor(R.color.color_eld_theme));
                            dismiss();
                        }
                    }

                }catch(Exception e){  }
            }
        },
                new Response.ErrorListener()  {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.LogDebug("onduty error", "onDuty error: " + error.toString());
                        Globally.EldScreenToast(btnAccept, error.toString(), getContext().getResources().getColor(R.color.color_eld_theme));
                        dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put(ConstantsKeys.DriverId, DriverId);
                 params.put(ConstantsKeys.DeviceId, DeviceId);
               //  params.put(ConstantsKeys.CompanyId, CompanyId);

                Logger.LogDebug("api", ">>>Assign unidentified: " );

                return params;
            }
        };

        int socketTimeout = 40000;   //40 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);

    }



}