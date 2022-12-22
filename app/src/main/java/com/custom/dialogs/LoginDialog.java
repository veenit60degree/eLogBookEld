package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driver.details.DriverConst;
import com.als.logistic.R;
import com.als.logistic.UILApplication;

public class LoginDialog extends Dialog {


    public interface LoginListener {

        public void CancelReady();
        public void LoginBtnReady(
                String UserType,
                String userName,
                String Password,
                EditText UsernameEditText,
                EditText PasswordEditText);

    }

    String UserType = "" ;
    String ScreenType = "";
    String MainDriverName = "";
    String coDriverName = "";
    private LoginListener readyListener;
    EditText coDriverUserNameText , coDriverConfirmPassText;
    Button coDriverLoginBtn, coDriverCancelBtn;
    ImageView driverUserImg, driverPassImg;
    TextView driverTitleTV, driverDescTV;
    RelativeLayout loginCoDriverLay;
    LinearLayout passLay, confirmPassLay;
    View userUnderLineView, passUnderLineView;


    public LoginDialog(Context context, String type, String screenType, String MainDriverName, String coDriverName, LoginListener readyListener) {
        super(context);
        this.UserType = type;
        this.ScreenType = screenType;
        this.MainDriverName = MainDriverName;
        this.coDriverName = coDriverName;
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.include_login_codriver);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        coDriverUserNameText      = (EditText)findViewById(R.id.coDriverUserNameText);
        coDriverConfirmPassText   = (EditText)findViewById(R.id.coDriverConfirmPassText);

        coDriverLoginBtn          = (Button)findViewById(R.id.coDriverLoginBtn);
        coDriverCancelBtn         = (Button)findViewById(R.id.coDriverCancelBtn);
        driverUserImg             = (ImageView) findViewById(R.id.driverUserImg);
        driverPassImg             = (ImageView)findViewById(R.id.driverPassImg);

        driverTitleTV             = (TextView)findViewById(R.id.driverTitleTV);
        driverDescTV              = (TextView)findViewById(R.id.driverDescTV);

        userUnderLineView         = (View)findViewById(R.id.userUnderLineView);
        passUnderLineView         = (View)findViewById(R.id.passUnderLineView);

        passLay                   = (LinearLayout) findViewById(R.id.passLay);
        confirmPassLay            = (LinearLayout) findViewById(R.id.confirmPassLay);

        loginCoDriverLay          = (RelativeLayout)findViewById(R.id.loginCoDriverLay);


        if(UserType.equals(DriverConst.StatusSingleDriver)){
            coDriverUserNameText.setText(MainDriverName);
        }else{
            coDriverUserNameText.setText(coDriverName);
        }

        if(coDriverUserNameText.getText().toString().trim().length() > 0){
            coDriverUserNameText.setEnabled(false);
        }

        if(UILApplication.getInstance().isNightModeEnabled()){
            driverTitleTV.setTextColor(Color.WHITE);
            driverDescTV.setTextColor(Color.WHITE);
            coDriverUserNameText.setTextColor(Color.WHITE);
            coDriverConfirmPassText.setTextColor(Color.WHITE);
            userUnderLineView.setBackgroundColor(Color.WHITE);
            passUnderLineView.setBackgroundColor(Color.WHITE);
        }else {
            driverTitleTV.setTextColor(Color.BLACK);
            driverDescTV.setTextColor(Color.BLACK);
            coDriverUserNameText.setTextColor(Color.BLACK);
            coDriverConfirmPassText.setTextColor(Color.BLACK);
            userUnderLineView.setBackgroundColor(Color.BLACK);
            passUnderLineView.setBackgroundColor(Color.BLACK);
        }
      //  passInputLay.

        passLay.setVisibility(View.INVISIBLE);
        confirmPassLay.setVisibility(View.VISIBLE);
        driverDescTV.setVisibility(View.VISIBLE);
        coDriverConfirmPassText.requestFocus();
        driverTitleTV.setText("Confirm Login");
        coDriverCancelBtn.setVisibility(View.VISIBLE);

        loginCoDriverLay.setBackgroundResource(R.drawable.white_full_border);

        if(ScreenType.equals(getContext().getResources().getString(R.string.DOT))){
            driverTitleTV.setText(getContext().getResources().getString(R.string.confirm_password_title));
            driverDescTV.setText(getContext().getResources().getString(R.string.confirm_password_to_exit_dot));
            coDriverLoginBtn.setText(getContext().getResources().getString(R.string.Confirm));
        }else{
            coDriverLoginBtn.setBackgroundResource(R.drawable.green_selector);
            //driverUserImg.setImageResource(R.drawable.username_green);
           // driverPassImg.setImageResource(R.drawable.password_green);
        }


        coDriverLoginBtn.setOnClickListener(new LoginFieldListener());
        coDriverCancelBtn.setOnClickListener(new CancelBtnListener());


        HideKeyboard();
    }



    void HideKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)  getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {   }
    }



    private class LoginFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.LoginBtnReady(
                    UserType,
                    coDriverUserNameText.getText().toString(),
                    coDriverConfirmPassText.getText().toString(),
                    coDriverUserNameText,
                    coDriverConfirmPassText
            );
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HideKeyboard();
            readyListener.CancelReady();
        }
    }



}
