package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.constants.SharedPref;
import com.als.logistic.LoginActivity;
import com.als.logistic.R;
import com.als.logistic.TabAct;


public class UpdateAppDialog extends Dialog {

    Button btnUpdateApp;
    Activity activity;

    public UpdateAppDialog(Context context, Activity act) {
        super(context);
        activity    = act;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_update_app);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        btnUpdateApp = (Button) findViewById(R.id.btnUpdateApp);

        btnUpdateApp.setOnClickListener(new VehicleFieldListener());

        HideKeyboard();
    }


    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }


    private class VehicleFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            dismiss();

            Intent intent;
            if (SharedPref.IsDriverLogin(getContext())) {
                intent = new Intent(getContext(), TabAct.class);
            } else {
                intent = new Intent(getContext(), LoginActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("EXIT", true);
            getContext().startActivity(intent);
            activity.finish();


            final String appPackageName = getContext().getPackageName(); // getPackageName() from Context or Activity object
            try {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +appPackageName )));
            } catch (android.content.ActivityNotFoundException anfe) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +appPackageName )));
            }





        }
    }


}
