package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.adapter.logistic.NotificationPagerAdapter;
import com.adapter.logistic.PermissionPagerAdapter;
import com.constants.ConstantsEnum;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TermsConditionsActivity;
import com.models.NotificationNewsModel;
import com.models.PermissionInfoModel;

import java.util.ArrayList;
import java.util.List;

public class PermissionInfoDialog extends Dialog {

    TermsAgreeListener agreeListener;
    ViewPager permissionInfoPager;
    TextView permsnInfoBtn, termsCondBtn, getStartedBtn;
    CheckBox termsCondCheckBox;


    public interface TermsAgreeListener {
        public void AgreeReady();

    }


    public PermissionInfoDialog(Context context, TermsAgreeListener agreeListener) {
        super(context);
        this.agreeListener = agreeListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.popup_permission_info);
        setCancelable(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);

        permsnInfoBtn           = (TextView) findViewById(R.id.permsnInfoBtn);
        termsCondBtn            = (TextView) findViewById(R.id.termsCondBtn);
        getStartedBtn           = (TextView) findViewById(R.id.getStartedBtn);

        permissionInfoPager     = (ViewPager)findViewById(R.id.permissionInfoPager);
        termsCondCheckBox       = (CheckBox)findViewById(R.id.termsCondCheckBox);

        termsCondCheckBox.setVisibility(View.GONE);
        termsCondBtn.setVisibility(View.GONE);
        permsnInfoBtn.setVisibility(View.GONE);

        List<PermissionInfoModel> dataList = new ArrayList<>();
        dataList.add(new PermissionInfoModel("Welcome to ALS ELD", "ALS E-Logbook is an ideal solution which permits the organization to streamline operations of transportation business in US and CANADA. It is very user-friendly and convenient to use for every fleet. Drive more safe while staying with ALS.", R.drawable.get_started));
        dataList.add(new PermissionInfoModel("Phone Number", "The Phone UICCID is only used for the network connectivity issues or to check the data utilization of an individual SIM to provide better Fleet Management Solution.", R.drawable.permsn_phone));
        dataList.add(new PermissionInfoModel("Storage", "We may use storage permission to save driver's log locally when Internet connection is not available and synced automatically to server when comes online. So that driver can use the app freely without network dependency.", R.drawable.permsn_storage));
        dataList.add(new PermissionInfoModel("Location", "We may use location information to improve and personalize the services and to contact you about service offerings that may be of interest to you.", R.drawable.permsn_location));

        PermissionPagerAdapter pagerAdapter = new PermissionPagerAdapter(getContext(), dataList);
        permissionInfoPager.setAdapter(pagerAdapter);
        permissionInfoPager.setPageMargin(20);


        permissionInfoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    permsnInfoBtn.setVisibility(View.GONE);
                    getStartedBtn.setVisibility(View.VISIBLE);
                    termsCondCheckBox.setVisibility(View.GONE);
                    termsCondBtn.setVisibility(View.GONE);
                }else if(position == 3){
                    getStartedBtn.setVisibility(View.GONE);
                    termsCondCheckBox.setVisibility(View.VISIBLE);
                    termsCondBtn.setVisibility(View.VISIBLE);
                    permsnInfoBtn.setVisibility(View.VISIBLE);
                    permsnInfoBtn.setText(getContext().getResources().getString(R.string.agree));
                    if(termsCondCheckBox.isChecked()) {
                        permsnInfoBtn.setBackgroundResource(R.drawable.blue_new_drawable);
                        permsnInfoBtn.setTextColor(getContext().getResources().getColor(R.color.whiteee));
                    }else{
                        permsnInfoBtn.setTextColor(getContext().getResources().getColor(R.color.color_eld_theme));
                        permsnInfoBtn.setBackgroundResource(R.drawable.transparent);
                    }
                }else{

                    getStartedBtn.setVisibility(View.GONE);
                    termsCondCheckBox.setVisibility(View.GONE);
                    termsCondBtn.setVisibility(View.GONE);
                    permsnInfoBtn.setVisibility(View.VISIBLE);
                    permsnInfoBtn.setText(getContext().getResources().getString(R.string.next));
                    permsnInfoBtn.setTextColor(getContext().getResources().getColor(R.color.color_eld_theme));
                    permsnInfoBtn.setBackgroundResource(R.drawable.transparent);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        termsCondCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    permsnInfoBtn.setBackgroundResource(R.drawable.blue_new_drawable);
                    permsnInfoBtn.setTextColor(getContext().getResources().getColor(R.color.whiteee));
                }else{
                    permsnInfoBtn.setTextColor(getContext().getResources().getColor(R.color.color_eld_theme));
                    permsnInfoBtn.setBackgroundResource(R.drawable.transparent);
                }
            }
        });


        permsnInfoBtn.setOnClickListener(new LoadingJobListener());
        getStartedBtn.setOnClickListener(new LoadingJobListener());
        termsCondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), TermsConditionsActivity.class);
                getContext().startActivity(i);
            }
        });

        getStartedBtn.setOnClickListener(new LoadingJobListener());
    }



    private class LoadingJobListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(permsnInfoBtn.getText().toString().equals(getContext().getResources().getString(R.string.agree))){
                if(termsCondCheckBox.isChecked()) {
                    agreeListener.AgreeReady();
                }else{
                    Globally.EldScreenToast(termsCondCheckBox, getContext().getResources().getString(R.string.accept_terms_cond),
                            getContext().getResources().getColor(R.color.hos_send_log));
                }
            }else{
                int currItemPos = permissionInfoPager.getCurrentItem();
                permissionInfoPager.setCurrentItem(currItemPos+1);

            }
        }
    }



}


