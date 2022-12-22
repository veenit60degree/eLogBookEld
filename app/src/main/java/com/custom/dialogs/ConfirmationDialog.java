package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.als.logistic.Globally;
import com.als.logistic.R;

public class ConfirmationDialog extends Dialog {


    public interface ConfirmationListener {
        public void OkBtnReady();

    }

    private String formType;
    private ConfirmationListener readyListener;
    Constants constants;

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

        constants = new Constants();

        if(formType.equals(Constants.AlertUnidentified)) {
            if (Globally.isTablet(getContext())) {
                getWindow().setLayout(constants.intToPixel(getContext(), 700), ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                getWindow().setLayout(constants.intToPixel(getContext(), 410), ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            setCancelable(false);
        }else{
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            getWindow().setAttributes(lp);

        }




        TextView changeTitleView, titleDescView;
        changeTitleView = (TextView)findViewById(R.id.changeTitleView);
        titleDescView=(TextView)findViewById(R.id.titleDescView);
        final Button confirmPopupButton = (Button)findViewById(R.id.confirmPopupButton);
        Button cancelPopupButton = (Button)findViewById(R.id.cancelPopupButton);

        changeTitleView.setText(getContext().getResources().getString(R.string.Confirmation));

        if(formType.equals(Constants.AlertSettings)) {
            titleDescView.setText(getContext().getResources().getString(R.string.want_cancel_download));
            cancelPopupButton.setText(getContext().getResources().getString(R.string.no));
            confirmPopupButton.setText(getContext().getResources().getString(R.string.yes));
            cancelPopupButton.setTypeface(null, Typeface.NORMAL);
        }else if(formType.equals(Constants.AlertUnidentified)) {
            changeTitleView.setText(getContext().getResources().getString(R.string.unIdentified_record_title));
            titleDescView.setText(getContext().getResources().getString(R.string.pending_unIdentified));
            cancelPopupButton.setText(getContext().getResources().getString(R.string.dismiss));
            confirmPopupButton.setText(getContext().getResources().getString(R.string.view));
        }else{
            titleDescView.setText(getContext().getResources().getString(R.string.want_to_logout));
            confirmPopupButton.setText(getContext().getResources().getString(R.string.logout));

        }

        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(formType.equals(Constants.AlertUnidentified)) {
                    if(SharedPref.getCurrentDriverType(getContext()).equals(DriverConst.StatusSingleDriver) ) { // Single Driver Type and Position is 0
                        SharedPref.setUnidentifiedAlertViewStatus(false, getContext());
                    }else{
                        SharedPref.setUnidentifiedAlertViewStatusCo(false, getContext());
                    }
                }
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
