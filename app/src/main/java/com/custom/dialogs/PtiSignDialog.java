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
import android.widget.ImageView;
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

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PtiSignDialog extends Dialog {

    public interface PtiConfirmationListener {
        public void PtiBtnReady(String ByteDriverSign, String SignDate);
        public void CancelBtnReady();

    }

    PtiConfirmationListener certifyListener;
    Constants constants;
    Globally globally;
    JSONArray inspectionArray;
    String ByteDriverSign = "", SignDate = "", type, sign;

    public PtiSignDialog(@NonNull Context context, String type, String sign, JSONArray inspectionArray, PtiConfirmationListener certifyListener) {
        super(context);
        this.type = type;
        this.sign = sign;
        this.inspectionArray = inspectionArray;
        this.certifyListener = certifyListener;
        constants = new Constants();

        globally        = new Globally();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_sign_lay);

        if(Globally.isTablet(getContext())){
            getWindow().setLayout(constants.intToPixel(getContext(), 730), ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            getWindow().setLayout(constants.intToPixel(getContext(), 550), ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        final TextView changeTitleView = (TextView) findViewById(R.id.changeTitleView);
        final TextView titleDescView=(TextView)findViewById(R.id.titleDescView);
        final Button confirmPopupButton = (Button)findViewById(R.id.confirmPopupButton);
        Button cancelPopupButton = (Button)findViewById(R.id.cancelPopupButton);
        ImageView signImageView = (ImageView) findViewById(R.id.showPreviousSign);

        if(type.equals("pti")) {
            ArrayList<String> SignWithDate = constants.getPtiLastSignature(inspectionArray);
            if(SignWithDate.size() > 1) {
                ByteDriverSign  = SignWithDate.get(0);
                SignDate        = SignWithDate.get(1);
                constants.LoadByteImage(signImageView, ByteDriverSign);
            }
            changeTitleView.setText(getContext().getString(R.string.Inspection_sign));
        }else{
            ByteDriverSign = sign;
            constants.LoadByteImage(signImageView, sign);
            changeTitleView.setText("Certify log alert !!");
        }

        titleDescView.setText(getContext().getString(R.string.continue_sign_desc));
        titleDescView.setTextColor(getContext().getResources().getColor(R.color.gray_text1));
        cancelPopupButton.setTextColor(getContext().getResources().getColor(R.color.gray_text1));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 20, 0);
        cancelPopupButton.setLayoutParams(params);


        cancelPopupButton.setOnClickListener(new CancelBtnListener());
        confirmPopupButton.setOnClickListener(new CertifyListener());

    }


    private class CertifyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            certifyListener.PtiBtnReady(ByteDriverSign, SignDate);
            dismiss();
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            certifyListener.CancelBtnReady();
            dismiss();
        }
    }


}

