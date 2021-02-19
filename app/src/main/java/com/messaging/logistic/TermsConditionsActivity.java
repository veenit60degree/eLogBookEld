package com.messaging.logistic;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.constants.SharedPref;
import com.constants.WebAppInterface;

public class TermsConditionsActivity extends FragmentActivity {

    WebView termsCondWebView;
    RelativeLayout rightMenuBtn, eldMenuLay;
    TextView EldTitleTV, dateActionBarTV;
    ImageView eldMenuBtn;
    ProgressBar termsCondProgressBar;
    String URl = "https://alsrealtime.com/PrivacyPolicy.html";
    boolean isLoggedIn = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_terms_conditions);

        termsCondWebView    = (WebView)findViewById(R.id.termsCondWebView);
        EldTitleTV          = (TextView)findViewById(R.id.EldTitleTV);
        dateActionBarTV     = (TextView) findViewById(R.id.dateActionBarTV);

        eldMenuBtn          = (ImageView)findViewById(R.id.eldMenuBtn);

        eldMenuLay          = (RelativeLayout)findViewById(R.id.eldMenuLay);
        rightMenuBtn        = (RelativeLayout)findViewById(R.id.rightMenuBtn);
        termsCondProgressBar= (ProgressBar)findViewById(R.id.termsCondProgressBar);

        EldTitleTV.setText(getString(R.string.terms_conditions));
        rightMenuBtn.setVisibility(View.GONE);
        termsCondProgressBar.setVisibility(View.GONE);

        if (SharedPref.getUserName(this).equals("") &&
                SharedPref.getPassword(this).equals("")) {
            eldMenuBtn.setImageResource(R.drawable.back_white);
            isLoggedIn = false;

        }else{
            isLoggedIn = true;

            dateActionBarTV.setVisibility(View.VISIBLE);
            dateActionBarTV.setBackgroundResource(R.drawable.transparent);
            dateActionBarTV.setTextColor(getResources().getColor(R.color.colorPrimary));

        }

        WebSettings webSettings = termsCondWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setSupportZoom(true);

       /* webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        termsCondWebView.setWebViewClient(new WebViewClient());
        termsCondWebView.setWebChromeClient(new WebChromeClient());
        termsCondWebView.addJavascriptInterface( new WebAppInterface(), "Android");

        termsCondWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                termsCondProgressBar.setVisibility(View.GONE);
            }

        });*/

        termsCondWebView.loadUrl(URl);


        eldMenuLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn) {
                    TabAct.sliderLay.performClick();
                }else{
                    finish();
                }

            }
        });


        dateActionBarTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabAct.host.setCurrentTab(0);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(SharedPref.IsAOBRD(TermsConditionsActivity.this)) {
            dateActionBarTV.setText(getString(R.string.aobrd));
        }else{
            dateActionBarTV.setText(getString(R.string.eld));
        }



    }

    @Override
    public void onBackPressed() {

        if (isLoggedIn) {
            if(TabAct.smenu.isMenuShowing()){
                TabAct.smenu.toggle();
            }else {
                TabAct.host.setCurrentTab(0);
            }
        }else{
            finish();
        }

    }



}
