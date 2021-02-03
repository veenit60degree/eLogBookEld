package com.messaging.logistic;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.constants.Constants;
import com.constants.ExitStrategy;
import com.constants.SharedPref;
import com.custom.dialogs.LoginDialog;
import com.driver.details.DriverConst;
import com.messaging.logistic.fragment.EldFragment;

/**
 * Created by kumar on 9/19/2017.
 */

public class EldActivity extends FragmentActivity  {



    FragmentManager fragManager;
    public static EldActivity instance;
    LoginDialog loginDialog;
    public static TextView DOTButton;
    String MainDriverName = "", MainDriverPass = "", CoDriverName = "" , CoDriverPass = "";
    boolean isOnStart = false;
    SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (UILApplication.getInstance().isNightModeEnabled()) { //AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
           // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
          //  setTheme(R.style.ActivityThemeDark);
        }

        super.onCreate(savedInstanceState);
     //   requestWindowFeature(Window.FEATURE_NO_TITLE);

     //   UILApplication.getInstance().setTheme();

        setContentView(R.layout.frame_layout);

        sharedPref = new SharedPref();

        DOTButton = (TextView)findViewById(R.id.tripTitleTV);

        instance      = this;
        LoadFragment();

        DOTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDOT();
            }
        });
    }


    private void LoadFragment(){
        EldFragment detailFragment = new EldFragment();
        fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, detailFragment);
        fragmentTran.addToBackStack("eld");
        fragmentTran.commit();
    }


    void ConfirmDOT(){

        MainDriverName = DriverConst.GetDriverLoginDetails( DriverConst.UserName, getApplicationContext());
        MainDriverPass = DriverConst.GetDriverLoginDetails( DriverConst.Passsword, getApplicationContext());

        CoDriverName = DriverConst.GetCoDriverLoginDetails( DriverConst.CoUserName, getApplicationContext());
        CoDriverPass = DriverConst.GetCoDriverLoginDetails( DriverConst.CoPasssword, getApplicationContext());

        String Type;

        if (SharedPref.getCurrentDriverType(getApplicationContext()).equals(DriverConst.StatusSingleDriver)) {
            Type = "main_driver";
        }else{
            Type = "co_driver";
        }

        try {
            if (loginDialog != null && loginDialog.isShowing())
                loginDialog.dismiss();


            loginDialog = new LoginDialog(EldActivity.this , Type, getResources().getString(R.string.DOT), MainDriverName, CoDriverName, new LoginListener());
            loginDialog.show();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }




    private class LoginListener implements LoginDialog.LoginListener{


        @Override
        public void CancelReady() {
            if(loginDialog != null)
                loginDialog.dismiss();

            if(isOnStart){
                finish();
            }

        }

        @Override
        public void LoginBtnReady(String UserType, String userName, String Password, EditText UsernameEditText, EditText PasswordEditText) {

            if(Password.length() > 0) {
                if(UserType.equals("main_driver")){
                    if(userName.equals(MainDriverName) && Password.equals(MainDriverPass)) {
                        Globally.hideKeyboardView(getApplicationContext(), PasswordEditText);

                        if (loginDialog != null)
                            loginDialog.dismiss();

                        Globally.hideKeyboardView(getApplicationContext(), PasswordEditText);
                        Globally.EldScreenToast(DOTButton, "Password confirmed", getResources().getColor(R.color.color_eld_bg));

                        sharedPref.SetDOTStatus(false, getApplicationContext());

                        if(isOnStart){
                            LoadFragment();
                        }else {
                            fragManager = getSupportFragmentManager();
                            fragManager.popBackStack();
                        }
                        isOnStart = false;

                    }else{
                        Globally.EldScreenToast(UsernameEditText, "Incorrect password", getResources().getColor(R.color.colorSleeper) );
                    }
                }else{
                    if(userName.equals(CoDriverName) && Password.equals(CoDriverPass)){
                        Globally.hideKeyboardView(getApplicationContext(), PasswordEditText);

                        if(loginDialog != null)
                            loginDialog.dismiss();

                        Globally.EldScreenToast(DOTButton, "Password confirmed", getResources().getColor(R.color.color_eld_bg) );
                        sharedPref.SetDOTStatus(false, getApplicationContext());

                        if(isOnStart){
                            LoadFragment();
                        }else {
                            getSupportFragmentManager().popBackStack();
                        }
                        isOnStart = false;

                    }else{
                        Globally.EldScreenToast(UsernameEditText, "Incorrect password", getResources().getColor(R.color.colorSleeper) );
                    }
                }
            }else{
                Globally.EldScreenToast(UsernameEditText, "Please enter password", getResources().getColor(R.color.colorSleeper) );
            }


        }
    }



    @Override
    public void onBackPressed() {
        fragManager = getSupportFragmentManager();

        try {
            if (TabAct.smenu.isMenuShowing()) {
                TabAct.smenu.toggle();
            } else {
                int count = fragManager.getBackStackEntryCount();

                if (count > 2) {
                    Constants.IsEdiLogBackStack = true;
                }
                if (count > 1) {
                    if (count == 2) {
                        if (sharedPref.IsDOT(getApplicationContext())) {
                            ConfirmDOT();
                        } else {
                            fragManager.popBackStack();
                        }
                    } else {
                        fragManager.popBackStack();
                    }
                } else {
                    Constants.IsEdiLogBackStack = false;
                    if (ExitStrategy.canExit()) {
                        finish();
                    } else {
                        ExitStrategy.startExitDelay(2000);
                        Globally.EldScreenToast(EldFragment.refreshLogBtn/**/, getString(R.string.exit_msg), getResources().getColor(R.color.colorPrimary));
                    }


                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
