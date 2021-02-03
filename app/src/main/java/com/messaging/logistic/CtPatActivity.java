package com.messaging.logistic;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.Window;

import com.messaging.logistic.fragment.CtPatFragment;

public class CtPatActivity extends FragmentActivity {


    FragmentManager fragManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

/*
        if (UILApplication.getInstance().isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
*/


        setContentView(R.layout.frame_layout);



        CtPatFragment ctPatFragment = new CtPatFragment();
        fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, ctPatFragment);
        fragmentTran.addToBackStack("ct_pat");
        fragmentTran.commit();


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
