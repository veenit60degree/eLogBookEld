package com.messaging.logistic;

import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class EditedLogActivity extends FragmentActivity implements View.OnClickListener{

    ImageView eldMenuBtn;
    TextView EldTitleTV;
    RelativeLayout rightMenuBtn;
    RelativeLayout eldMenuLay;
    ViewPager editedLogViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_log_compare);



        rightMenuBtn    = (RelativeLayout) findViewById(R.id.rightMenuBtn);
        eldMenuLay      = (RelativeLayout)findViewById(R.id.eldMenuLay);
        eldMenuBtn      = (ImageView)findViewById(R.id.eldMenuBtn);
        EldTitleTV      = (TextView) findViewById(R.id.EldTitleTV);


        rightMenuBtn.setVisibility(View.INVISIBLE);
        eldMenuBtn.setImageResource(R.drawable.back_white);
        EldTitleTV.setText(getResources().getString(R.string.wired_obd_details));


        // Setting ViewPager for each Tabs
        editedLogViewPager = (ViewPager) findViewById(R.id.editedLogPager);
        setupViewPager(editedLogViewPager);

        // Set Tabs inside Toolbar
        TabLayout editedLogTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        editedLogTabLayout.setupWithViewPager(editedLogViewPager);


        editedLogTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                if(tab.getPosition() == 0){
                    Log.d("tab", "Edited");
                }else{
                        Log.d("tab", "Original");
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


        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EditedLogFragment(), "Edited Log");
        adapter.addFragment(new OriginalLogFragment(), "Original Log");
        viewPager.setAdapter(adapter);



    }


    class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager manager) {
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





    public static class EditedLogFragment extends Fragment{

        View rootView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            if (rootView != null) {
                ViewGroup parent = (ViewGroup) rootView.getParent();
                if (parent != null)
                    parent.removeView(rootView);
            }
            try {
                rootView = inflater.inflate(R.layout.webview_log_preview, container, false);
                rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } catch (InflateException e) {
                e.printStackTrace();
            }


            WebView editLogWebView = (WebView)rootView.findViewById(R.id.previewLogWebView);

            return rootView;
        }
    }



    public static class OriginalLogFragment extends Fragment {


        View rootView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            if (rootView != null) {
                ViewGroup parent = (ViewGroup) rootView.getParent();
                if (parent != null)
                    parent.removeView(rootView);
            }
            try {
                rootView = inflater.inflate(R.layout.webview_log_preview, container, false);
                rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } catch (InflateException e) {
                e.printStackTrace();
            }


            WebView originalLogWebView = (WebView)rootView.findViewById(R.id.previewLogWebView);

            return rootView;
        }




    }





    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.eldMenuLay:
                finish();
                break;
        }
    }


}
