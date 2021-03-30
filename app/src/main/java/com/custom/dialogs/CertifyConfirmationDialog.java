package com.custom.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

import java.util.HashMap;
import java.util.Map;

public class CertifyConfirmationDialog extends Dialog {

    public interface CertifyConfirmationListener {
        public void CertifyBtnReady();
        public void CancelBtnReady();

    }

    String DeviceId, DriverId, CompanyId;
    CertifyConfirmationListener certifyListener;
    Constants constants;
    Globally globally;
    ProgressDialog progressDialog;
    VolleyRequest notReadyRequest;



    public CertifyConfirmationDialog(@NonNull Context context, CertifyConfirmationListener certifyListener) {
        super(context);
        this.certifyListener = certifyListener;
        constants = new Constants();

        notReadyRequest = new VolleyRequest(context);
        globally        = new Globally();
        progressDialog  = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");

        DeviceId           = SharedPref.GetSavedSystemToken(context);
        DriverId           = SharedPref.getDriverId(context);
        CompanyId          = DriverConst.GetDriverDetails(DriverConst.CompanyId, context);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_edit_delete_lay);

        if(Globally.isTablet(getContext())){
            getWindow().setLayout(constants.intToPixel(getContext(), 730), ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            getWindow().setLayout(constants.intToPixel(getContext(), 550), ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        final TextView changeTitleView, titleDescView;
        changeTitleView = (TextView) findViewById(R.id.changeTitleView);
        titleDescView=(TextView)findViewById(R.id.titleDescView);
        final Button confirmPopupButton = (Button)findViewById(R.id.confirmPopupButton);
        Button cancelPopupButton = (Button)findViewById(R.id.cancelPopupButton);

        changeTitleView.setText(getContext().getResources().getString(R.string.Confirmation_suggested));
        titleDescView.setText(getContext().getResources().getString(R.string.I_certify_that_suggested) + "\n\n" + getContext().getResources().getString(R.string.will_finalize_log));
        confirmPopupButton.setText(getContext().getResources().getString(R.string.agree_submit));
        cancelPopupButton.setText(getContext().getResources().getString(R.string.not_ready));
        titleDescView.setTextColor(getContext().getResources().getColor(R.color.gray_text1));
        cancelPopupButton.setTextColor(getContext().getResources().getColor(R.color.black_unidenfied));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 20, 0);
        cancelPopupButton.setLayoutParams(params);



        cancelPopupButton.setOnClickListener(new CancelBtnListener());
        confirmPopupButton.setOnClickListener(new CertifyListener());

    }


    private class CertifyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            certifyListener.CertifyBtnReady();
            dismiss();

        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(Globally.isConnected(getContext()) ) {
                NotReadyApi();
            }else{
                certifyListener.CancelBtnReady();
                dismiss();
            }

        }
    }


    void dismissDialog(){
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }catch (Exception e){ e.printStackTrace();}
    }


    private void NotReadyApi() {

        if(progressDialog.isShowing() == false)
            progressDialog.show();

        String driverName = "";
        if (SharedPref.getCurrentDriverType(getContext()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            driverName = DriverConst.GetDriverDetails(DriverConst.DriverName, getContext());
        } else {
            driverName = DriverConst.GetCoDriverDetails(DriverConst.CoDriverName, getContext());
        }
        String DriverCompanyId = DriverConst.GetDriverDetails(DriverConst.CompanyId, getContext());

        Map<String, String> mapParams = new HashMap<String, String>();
        mapParams.put(ConstantsKeys.DriverId, DriverId);
        mapParams.put(ConstantsKeys.DeviceId, DeviceId);
        mapParams.put(ConstantsKeys.DriverName, driverName);
        mapParams.put(ConstantsKeys.CompanyId, DriverCompanyId);
        mapParams.put(ConstantsKeys.DriverTimeZoneName, SharedPref.getTimeZone(getContext()));
        mapParams.put(ConstantsKeys.LogDateTime, globally.getCurrentDate());

        notReadyRequest.executeRequest(Request.Method.POST, APIs.SAVE_CERTIFY_SIGN_REJECTED_AUDIT, mapParams, 1010,
                Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);
    }




    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void getResponse(String response, int flag) {
            dismissDialog();
            certifyListener.CancelBtnReady();
            dismiss();
        }
    };

    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {
        @Override
        public void getError(VolleyError error, int flag) {
            dismissDialog();
            certifyListener.CancelBtnReady();
            dismiss();
        }
    };



}
