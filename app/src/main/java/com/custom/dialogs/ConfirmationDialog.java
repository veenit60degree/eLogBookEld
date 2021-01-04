package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.messaging.logistic.R;

public class ConfirmationDialog extends Dialog {


    public interface ConfirmationListener {
        public void OkBtnReady();

    }

    private String formType;
    private ConfirmationListener readyListener;

    public ConfirmationDialog(Context context, String type, ConfirmationListener readyListener) {
        super(context);
        this.formType = type;
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.popup_edit_delete_lay);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);


        TextView changeTitleView, titleDescView;
        changeTitleView = (TextView)findViewById(R.id.changeTitleView);
        titleDescView=(TextView)findViewById(R.id.titleDescView);
        final Button confirmPopupButton = (Button)findViewById(R.id.confirmPopupButton);
        Button cancelPopupButton = (Button)findViewById(R.id.cancelPopupButton);

        changeTitleView.setText("Confirmation");

        if(formType.equals("settings")) {
            titleDescView.setText("Do you really want to cancel download ?");
            cancelPopupButton.setText("No");
            confirmPopupButton.setText("Yes");
            cancelPopupButton.setTypeface(null, Typeface.NORMAL);
        }else{
            titleDescView.setText("Do you really want to logout ?");
            confirmPopupButton.setText("Logout");

        }

        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        confirmPopupButton.setOnClickListener(new OkJobListener());

    }



    private class OkJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.OkBtnReady();
        }
    }


}
