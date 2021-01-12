package com.messaging.logistic.fragment;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.messaging.logistic.R;

import java.util.ArrayList;
import java.util.List;

public class EditedLogCompFragment extends Fragment implements View.OnClickListener {


    View rootView;
    ImageView eldMenuBtn;
    TextView EldTitleTV;
    RelativeLayout rightMenuBtn;
    RelativeLayout eldMenuLay;
    ViewPager editedLogViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.activity_edit_log_compare, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }



    void initView(View v) {


        rightMenuBtn    = (RelativeLayout) v.findViewById(R.id.rightMenuBtn);
        eldMenuLay      = (RelativeLayout)v.findViewById(R.id.eldMenuLay);
        eldMenuBtn      = (ImageView)v.findViewById(R.id.eldMenuBtn);
        EldTitleTV      = (TextView) v.findViewById(R.id.EldTitleTV);


        rightMenuBtn.setVisibility(View.INVISIBLE);
        eldMenuBtn.setImageResource(R.drawable.back_white);
        EldTitleTV.setText(getResources().getString(R.string.wired_obd_details));


        // Setting ViewPager for each Tabs
        editedLogViewPager = (ViewPager) v.findViewById(R.id.editedLogPager);
        setupViewPager(editedLogViewPager);

        // Set Tabs inside Toolbar
        TabLayout editedLogTabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        editedLogTabLayout.setupWithViewPager(editedLogViewPager);


        editedLogTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                if(tab.getPosition() == 0){

                }else{

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        eldMenuLay.setOnClickListener(this);

    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        Adapter adapter = new Adapter(getChildFragmentManager());
       // adapter.addFragment(new TripHistoryFragment(), "Edited Log");
     //   adapter.addFragment(new LocalTripHistoryFragment(), "Original Log");
        viewPager.setAdapter(adapter);



    }


    class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }






    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.eldMenuLay:
                getActivity().finish();
                break;
        }
    }



}
