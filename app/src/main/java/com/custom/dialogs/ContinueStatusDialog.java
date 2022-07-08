package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.constants.Constants;
import com.constants.SharedPref;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

public class ContinueStatusDialog extends Dialog {

    public interface ContinueListener {
        public void ContinueBtnReady(String TruckIgnitionStatus);
        public void CancelBtnReady(String TruckIgnitionStatus, boolean isYardMove);
    }


    private boolean isYardMove, isPersonal, isLogin;
    String TruckIgnitionStatus;
    private ContinueListener readyListener;

    public ContinueStatusDialog(Context context, boolean isYardMove, boolean isPersonal, boolean isLogin,
                                String TruckIgnitionStatus, ContinueListener readyListener) {
        super(context);
        this.isYardMove = isYardMove;
        this.isPersonal = isPersonal;
        this.isLogin = isLogin;
        this.TruckIgnitionStatus = TruckIgnitionStatus;
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.dialog_engine_restarted);
        setCancelable(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);


        TextView continueStatusTitleTV  = (TextView) findViewById(R.id.continueStatusTitleTV);
        TextView continueStatusDescTV   = (TextView)findViewById(R.id.continueStatusDescTV);
        Button continueStatusBtn        = (Button) findViewById(R.id.continueStatusBtn);
        Button changeStatusBtn          = (Button) findViewById(R.id.changeStatusBtn);

        Constants.isPcYmAlertButtonClicked = true;

        if(isYardMove){
            //changeStatusBtn.setText("On Duty (Others)");

            continueStatusBtn.setText(getContext().getResources().getString(R.string.stay_in_yard_move));
            changeStatusBtn.setText("On Duty (Others)");
            continueStatusDescTV.setText(getContext().getString(R.string.ConfirmDutyStatus));
        }else if(isPersonal){
            continueStatusBtn.setText(getContext().getResources().getString(R.string.stay_in_personel));
            changeStatusBtn.setText("Off Duty");
            continueStatusDescTV.setText(getContext().getString(R.string.ConfirmDutyStatus));
        }

        SharedPref.savePcYmAlertCallTime(Globally.GetCurrentDateTime(), getContext());

        if(SharedPref.GetAfterLoginConfStatus(getContext()) == false && isLogin){
            continueStatusDescTV.setVisibility(View.INVISIBLE );
            continueStatusTitleTV.setText(getContext().getResources().getString(R.string.LoginConfirmation) );
        }

        continueStatusBtn.setOnClickListener(new ContinueJobListener());
        changeStatusBtn.setOnClickListener(new ChangeJobListener());

    //    SharedPref.SetTruckStartLoginStatus(false, getContext());
        SharedPref.SetAfterLoginConfStatus(true, getContext());
    }



    private class ContinueJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.ContinueBtnReady(TruckIgnitionStatus);
            Constants.isPcYmAlertButtonClicked = false;
            dismiss();
        }
    }

    private class ChangeJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.CancelBtnReady(TruckIgnitionStatus, isYardMove);
            Constants.isPcYmAlertButtonClicked = false;
            dismiss();
        }
    }



}