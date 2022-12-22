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

import com.als.logistic.R;

public class MissingLocationDialog extends Dialog {


    public interface ChangeLocationListener {
        public void AddLocationListener();
    }


    private ChangeLocationListener readyListener;



    public MissingLocationDialog(Context context, ChangeLocationListener readyListener) {
        super(context);
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_engine_restarted);
       // setCancelable(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);


        TextView continueStatusTitleTV = (TextView) findViewById(R.id.continueStatusTitleTV);
        TextView continueStatusDescTV = (TextView) findViewById(R.id.continueStatusDescTV);
        Button continueStatusBtn = (Button) findViewById(R.id.continueStatusBtn);
        Button changeStatusBtn = (Button) findViewById(R.id.changeStatusBtn);

        continueStatusTitleTV.setText(getContext().getResources().getString(R.string.location_missing_dutystatus));
        continueStatusBtn.setText(getContext().getResources().getString(R.string.manually_add));
        continueStatusDescTV.setVisibility(View.GONE);
        changeStatusBtn.setVisibility(View.GONE);

        continueStatusBtn.setOnClickListener(new AddLocationListener());

    }



    private class AddLocationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.AddLocationListener();
            dismiss();
        }
    }

}