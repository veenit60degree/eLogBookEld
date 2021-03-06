package com.messaging.logistic;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.messaging.logistic.fragment.UnidentifiedFragment;


public class UnidentifiedActivity extends FragmentActivity {


    FragmentManager fragManager;
    public static boolean isUnIdentifiedRecordClaimed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);


        UnidentifiedFragment fragment = new UnidentifiedFragment();
        fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, fragment);
        fragmentTran.addToBackStack("unidentified_log");
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
            }else {
                TabAct.host.setCurrentTab(0);
            }
        }
    }



}
