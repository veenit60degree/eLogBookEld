package com.als.logistic;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.als.logistic.fragment.OdometerFragment;

public class OdometerActivity extends FragmentActivity {



    FragmentManager fragManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);


        OdometerFragment detailFragment = new OdometerFragment();
        fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, detailFragment);
        fragmentTran.addToBackStack("odometer");
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
            }else {
                TabAct.host.setCurrentTab(0);
            }
        }
    }



}
