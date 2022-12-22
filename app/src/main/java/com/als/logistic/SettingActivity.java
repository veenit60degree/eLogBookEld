package com.als.logistic;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.Window;

import com.als.logistic.fragment.SettingFragment;


public class SettingActivity extends FragmentActivity {

    FragmentManager fragManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.frame_layout);



        SettingFragment settingFragment = new SettingFragment();
        fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, settingFragment);
        fragmentTran.addToBackStack("setting");
        fragmentTran.commitAllowingStateLoss();


    }


    @Override
    public void onBackPressed() {
        fragManager = getSupportFragmentManager();

        if(TabAct.smenu.isMenuShowing()){
            TabAct.smenu.toggle();
        }else {
            if ( fragManager.getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                if(TabAct.smenu.isMenuShowing())
                    TabAct.smenu.hideMenu();
                else
                    TabAct.host.setCurrentTab(0);
            }
        }
    }



}
