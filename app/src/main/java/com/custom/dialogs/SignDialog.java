package com.custom.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.simplify.ink.InkView;

public class SignDialog extends Dialog {


    public interface SignListener {
        public void SignOkBtn(InkView inkView, boolean IsSigned);
    }

    private SignListener readyListener;
    boolean IsSigned = false;
    Button signOkBtn, signCancelBtn;
    RelativeLayout clearSignBtn;
    LinearLayout inkLinLay;
    InkView inkView;



    public SignDialog(Context context, SignListener readyListener) {
        super(context);
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_signature);

       // getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        signOkBtn       = (Button) findViewById(R.id.signOkBtn);
        signCancelBtn   = (Button) findViewById(R.id.signCancelBtn);
        clearSignBtn    = (RelativeLayout) findViewById(R.id.clearSignBtn);
        inkLinLay       = (LinearLayout)findViewById(R.id.inkLinLay);
        inkView         = (InkView) findViewById(R.id.inkView);

        inkView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                IsSigned = true;
                return false;
            }
        });

        signOkBtn.setOnClickListener(new SignOkListener());
        signCancelBtn.setOnClickListener(new CancelBtnListener());
        clearSignBtn.setOnClickListener(new ClearBtnListener());

       // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            inkLinLay.setBackgroundColor(getContext().getResources().getColor(R.color.gray_background));
        }

    }






    private class SignOkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
                readyListener.SignOkBtn(inkView, IsSigned);
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
           dismiss();
        }
    }

    private class ClearBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            IsSigned = false;
            inkView.clear();
        }
    }

}