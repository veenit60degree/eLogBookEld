package com.als.logistic;

import android.os.Bundle;

// import androidx.annotation.Nullable;
// import androidx.fragment.app.FragmentManager;
// import androidx.fragment.app.FragmentTransaction;
//import androidx.fragment.app.FragmentActivity;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.als.logistic.fragment.AlsSupportFragment;

public class AlsSupportActivity extends FragmentActivity {

    FragmentManager fragManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);


        AlsSupportFragment supportFragment = new AlsSupportFragment();
        fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, supportFragment);
        fragmentTran.addToBackStack("support");
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
