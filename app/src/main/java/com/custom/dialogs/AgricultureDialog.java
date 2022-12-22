package com.custom.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.constants.Constants;
import com.als.logistic.Globally;
import com.als.logistic.R;

public class AgricultureDialog extends Dialog {


    public interface ConfirmationListener {
        public void OkBtnReady();

    }

    private String description;
    private ConfirmationListener readyListener;
    Constants constants;

    public AgricultureDialog(Context context, String type, ConfirmationListener readyListener) {
        super(context);
        this.description = type;
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.popup_edit_delete_lay);

        constants = new Constants();


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        // lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        if(Globally.isTablet(getContext())) {
            lp.width = constants.intToPixel(getContext(), 680);
        }else{
            lp.width = constants.intToPixel(getContext(), 560);
        }

        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);


        TextView changeTitleView, titleDescView;
        changeTitleView = (TextView)findViewById(R.id.changeTitleView);
        titleDescView=(TextView)findViewById(R.id.titleDescView);
        final Button confirmPopupButton = (Button)findViewById(R.id.confirmPopupButton);
        Button cancelPopupButton = (Button)findViewById(R.id.cancelPopupButton);

        cancelPopupButton.setVisibility(View.GONE);

        changeTitleView.setVisibility(View.GONE);
        titleDescView.setText(Html.fromHtml(description));
        confirmPopupButton.setText("Ok");




        confirmPopupButton.setOnClickListener(new OkJobListener());


    }



    private class OkJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.OkBtnReady();
        }
    }


}

