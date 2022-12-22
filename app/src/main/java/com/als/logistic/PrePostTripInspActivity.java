package com.als.logistic;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.constants.Constants;
import com.als.logistic.fragment.InspectionFragment;
import com.als.logistic.fragment.InspectionsHistoryFragment;

public class PrePostTripInspActivity extends FragmentActivity{



    FragmentManager fragManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);


        InspectionFragment detailFragment = new InspectionFragment();
        fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, detailFragment);
        fragmentTran.addToBackStack("inspection");
        fragmentTran.commitAllowingStateLoss();

    }


    @Override
    public void onBackPressed() {
        fragManager = getSupportFragmentManager();

        if(TabAct.smenu.isMenuShowing()){
            TabAct.smenu.toggle();
        }else {
            if ( fragManager.getBackStackEntryCount() > 1) {
                if(Constants.isLocationUpdated && InspectionsHistoryFragment.refreshPtiBtnInvisible != null){
                    InspectionsHistoryFragment.refreshPtiBtnInvisible.performClick();
                }

                getSupportFragmentManager().popBackStack();
            }else {
                TabAct.host.setCurrentTab(0);
            }
        }
    }



}
